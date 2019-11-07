package com.dzf.zxkj.platform.service.icset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.icset.InvclassifyVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.icset.MeasureVO;
import com.dzf.zxkj.platform.model.jzcl.KMQMJZVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.glic.IInvAccAliasService;
import com.dzf.zxkj.platform.service.icset.IInvclassifyService;
import com.dzf.zxkj.platform.service.icset.IInventoryService;
import com.dzf.zxkj.platform.service.icset.IMeasureService;
import com.dzf.zxkj.platform.service.pjgl.IVATGoodsInvenRelaService;
import com.dzf.zxkj.platform.service.icreport.IQueryLastNum;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service("ic_inventoryserv")
public class InventoryServiceImpl implements IInventoryService {
    @Autowired
	private SingleObjectBO singleObjectBO = null;
    @Autowired
	private IInvclassifyService splbService;
    @Autowired
	private IMeasureService jldwService;
	@Autowired
	private IVATGoodsInvenRelaService goodsinvenservice;
	@Autowired
	private IYntBoPubUtil yntBoPubUtil;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private IInvAccAliasService ic_invtoryaliasserv = null;
	@Autowired
	private IAccountService accountService;
	@Autowired
	private ICorpService corpService;
    @Autowired
    private IQueryLastNum ic_rep_cbbserv;
	@Override
	public String save(String pk_corp, InventoryVO[] vos) throws DZFWarpException {
		return save(pk_corp, vos, null);
	}


	@Override
	public String save(String pk_corp, InventoryVO[] vos, List<InventoryVO> listAll) throws DZFWarpException {
		if (vos == null || vos.length == 0)
			return "success";

		checkBeforeSave(pk_corp, vos, listAll);

		List<InventoryVO> list1 = new ArrayList<InventoryVO>();
		List<InventoryVO> list2 = new ArrayList<InventoryVO>();
		for (InventoryVO v : vos) {
			if (StringUtil.isEmpty(v.getPk_inventory())) {
				list1.add(v);
			} else {
				list2.add(v);
			}
			if (v.getXslx() == null)
				v.setXslx(0);
		}
		if (list1.size() > 0)
			singleObjectBO.insertVOArr(pk_corp, list1.toArray(new InventoryVO[0]));
		if (list2.size() > 0) {
			singleObjectBO.updateAry(vos);
		}
		return "success";
	}

	private void checkBeforeSave(String pk_corp, InventoryVO[] vos, List<InventoryVO> listAll) {

		if (vos == null || vos.length == 0)
			throw new BusinessException("存货信息不完整,请检查!");

		InventoryVO ivo = vos[0];
		if (!pk_corp.equals(ivo.getPk_corp()))// 要进行操作的公司是否为登录公司
			throw new BusinessException("对不起，您无操作权限！");
		// 修改保存前数据安全验证
		InventoryVO getvo = queryByPrimaryKey(ivo.getPrimaryKey());
		if (getvo != null && !pk_corp.equals(getvo.getPk_corp())) {
			throw new BusinessException("出现数据无权问题，无法修改！");
		}

		HashMap<String, InventoryVO> map = new HashMap<>();
		HashSet<String> pkSet = new HashSet<String>();
		for (InventoryVO vo : vos) {
			if (!StringUtil.isEmpty(vo.getPk_inventory()))
				pkSet.add(vo.getPk_inventory());
		}
		if (listAll == null || listAll.size() == 0)
			listAll = queryInventoryVO(pk_corp);
		HashSet<String> codeSet = new HashSet<String>();
		HashSet<String> nameInfoSet = new HashSet<String>();
		if (listAll != null && listAll.size() != 0) {
			for (InventoryVO vo : listAll) {
				if (!pkSet.contains(vo.getPk_inventory())) {
					codeSet.add(getCodeUnitKey(vo));
					nameInfoSet.add(getNameInfoKey(vo));
				}

				if (!StringUtil.isEmpty(vo.getPk_inventory()))
					map.put(vo.getPk_inventory(), vo);
			}
		}

		StringBuffer msg = new StringBuffer();

		Set<String> error_msg = new HashSet<>();
		InventoryVO oldvo = null;
		for (InventoryVO invo : vos) {
			// 检查编码是否已存在
			oldvo = null;
			if (StringUtil.isEmpty(invo.getPk_subject())) {
				throw new BusinessException("科目不能为空！");
			}

			if (StringUtil.isEmpty(invo.getCode())) {
				throw new BusinessException("存货编码不能为空！");
			}

			if (StringUtil.isEmpty(invo.getName())) {
				throw new BusinessException("存货名称不能为空！");
			}

			String codekey = getCodeUnitKey(invo);

			if (!StringUtil.isEmpty(invo.getPk_inventory())) {
				oldvo = map.get(invo.getPk_inventory());
				if (oldvo != null) {
					if (!StringUtil.isEmpty(oldvo.getPk_subject())
							&& !oldvo.getPk_subject().equals(invo.getPk_subject())) {
						checkInventoryRefByModify(invo.getPk_inventory(), pk_corp, error_msg, map, "ynt_icbalance",
								"库存期初");
						checkInventoryRefByModify(invo.getPk_inventory(), pk_corp, error_msg, map, "ynt_ictradein",
								"入库单");
						checkInventoryRefByModify(invo.getPk_inventory(), pk_corp, error_msg, map, "ynt_ictradeout",
								"出库单");
					}
				}
			}
			if (codeSet.contains(codekey)) {
				// throw new BusinessException("存货编码[" + invo.getCode() +
				// "]和计量单位至少要有一项不同!");
				// throw new BusinessException("存货编码[" + invo.getCode() +
				// "]不能重复!");
				error_msg.add("存货编码[" + invo.getCode() + "]不能重复!");
			} else {
				codeSet.add(codekey);
			}

			String nameInfoKey = getNameInfoKey(invo);
			if (nameInfoSet.contains(nameInfoKey)) {
				msg.setLength(0);
				if (!StringUtil.isEmpty(invo.getName())) {
					msg.append("存货名称[" + invo.getName() + "]、");
				}
				if (!StringUtil.isEmpty(invo.getInvspec())) {
					msg.append("规格(型号)[" + invo.getInvspec() + "]、");
				} else {
					msg.append("规格(型号)、");
				}
//				if (!StringUtil.isEmpty(invo.getInvtype())) {
//					msg.append("型号[" + invo.getInvtype() + "] 、");
//				} else {
//					msg.append("型号、");
//				}

				// throw new BusinessException(msg.toString() + "计量单位和 科目
				// 至少要有一项不同!");
				error_msg.add(msg.toString() + "计量单位和 科目 至少要有一项不同!");
			} else {
				nameInfoSet.add(nameInfoKey);
			}
		}

		if (!error_msg.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			Iterator it = error_msg.iterator();
			while (it.hasNext()) {
				sb.append(it.next());
				sb.append("<br/>");
			}
			throw new BusinessException(sb.toString());
		}
	}

	private String getCodeUnitKey(InventoryVO invo) {
		StringBuffer strb = new StringBuffer();
		strb.append(appendIsNull(invo.getCode()));
		// strb.append(appendIsNull(invo.getPk_measure()));
		return strb.toString();
	}

	private String getNameInfoKey(InventoryVO invo) {
		StringBuffer strb = new StringBuffer();
		strb.append(appendIsNull(invo.getPk_subject()));
		strb.append(appendIsNull(invo.getName()));
		strb.append(appendIsNull(invo.getInvspec()));
//		strb.append(appendIsNull(invo.getInvtype()));
		strb.append(appendIsNull(invo.getInvspec()));
		strb.append(appendIsNull(invo.getPk_measure()));
		return strb.toString();

	}

	private String appendIsNull(String info) {
		StringBuffer strb = new StringBuffer();
		if (StringUtil.isEmpty(info)) {
			strb.append("null");
		} else {
			strb.append(info);
		}
		return strb.toString();
	}

	@Override
	public List<InventoryVO> query(String pk_corp, String kmid) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(kmid);
		StringBuffer sf = new StringBuffer();
		sf.append(
				" select  ry.*,fy.name invclassname,re.name measurename,nt.accountname kmname from ynt_inventory  ry  ");
		sf.append(" left join ynt_invclassify fy on ry.pk_invclassify = fy.pk_invclassify ");
		sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
		sf.append(" left join ynt_cpaccount nt on nt.pk_corp_account = ry.pk_subject ");
		sf.append(" where nvl(ry.dr,0) = 0 and ry.pk_corp = ? and ry.pk_subject = ?");
		List<InventoryVO> ancevos = (List<InventoryVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(InventoryVO.class));
		if (ancevos == null || ancevos.size() == 0)
			return null;
		return ancevos;
	}

	@Override
	public List<InventoryVO> queryInfo(String pk_corp, String ininvclids) throws DZFWarpException {

		List<InventoryVO> ancevos = queryInfo(pk_corp, ininvclids, null);
		return ancevos;
	}

	@Override
	public List<InventoryVO> queryInfo(String pk_corp, String ininvclids, InventoryVO param) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select  ry.*,fy.name invclassname,re.name measurename,ry.pk_subject from ynt_inventory  ry  ");
		sf.append(" left join ynt_invclassify fy on ry.pk_invclassify = fy.pk_invclassify ");
		sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
		// sf.append(" left join ynt_cpaccount nt on nt.pk_corp_account =
		// ry.pk_subject ");
		sf.append(" where nvl(ry.dr,0) = 0 and ry.pk_corp = ? ");

		String wheInvcl = null;
		if (!StringUtil.isEmptyWithTrim(ininvclids)) {
			if ("all".equals(ininvclids)) {

			} else if ("noclass".equals(ininvclids)) {
				sf.append(" and ry.pk_invclassify is null");
			} else {
				String[] spris = ininvclids.split(",");
				wheInvcl = SqlUtil.buildSqlConditionForIn(spris);
				sf.append(" and fy.pk_invclassify in ( ");
				sf.append(wheInvcl);
				sf.append(" ) ");
			}
		}

		if (param != null) {
			if (!StringUtil.isEmptyWithTrim(param.getCode())) {
				sf.append(" and ry.code like ? ");
				sp.addParam("%" + param.getCode() + "%");
			}

			if (!StringUtil.isEmptyWithTrim(param.getName())) {
				sf.append(" and ry.name like ?  ");
				sp.addParam("%" + param.getName() + "%");
			}

			if (!StringUtil.isEmptyWithTrim(param.getInvspec())) {
				sf.append(" and (ry.invspec like ? ");
				sp.addParam("%" + param.getInvspec() + "%");
				sf.append(" or ry.invtype like ? )");
				sp.addParam("%" + param.getInvspec() + "%");
			}
		}

		List<InventoryVO> ancevos = (List<InventoryVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(InventoryVO.class));
		if (ancevos == null || ancevos.size() == 0)
			return new ArrayList<InventoryVO>();

		Map<String, YntCpaccountVO> map = accountService.queryMapByPk(pk_corp);
		YntCpaccountVO accvo = null;
		for (InventoryVO vo : ancevos) {
			accvo = map.get(vo.getPk_subject());
			if (accvo != null) {
				vo.setKmcode(accvo.getAccountcode());
				vo.setKmname(accvo.getAccountname());
			}
		}
		VOUtil.ascSort(ancevos, new String[] { "code" });
		return ancevos;
	}

	@Override
	public List<InventoryVO> queryByIDs(String pk_corp, String ids) throws DZFWarpException {

		if (StringUtil.isEmpty(ids)) {
			return null;
		}
		String strids = SqlUtil.buildSqlConditionForIn(ids.split(","));
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select  ry.*,fy.name invclassname,re.name measurename,nt.accountname kmname ,"
				+ " nt.accountcode kmcode,ry.pk_subject from ynt_inventory  ry  ");
		sf.append(" left join ynt_invclassify fy on ry.pk_invclassify = fy.pk_invclassify ");
		sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
		sf.append(" left join ynt_cpaccount nt on nt.pk_corp_account = ry.pk_subject ");
		sf.append(" where nvl(ry.dr,0) = 0 and ry.pk_corp = ? ");
		sf.append(" and ry.pk_inventory in ( ").append(strids).append(" ) ");
		List<InventoryVO> ancevos = (List<InventoryVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(InventoryVO.class));
		if (ancevos == null || ancevos.size() == 0)
			return null;

		Collections.sort(ancevos, new Comparator<InventoryVO>() {
			@Override
			public int compare(InventoryVO o1, InventoryVO o2) {
				int i = o1.getCode().compareTo(o2.getCode());
				return i;
			}
		});
		return ancevos;
	}

	@Override
	public List<InventoryVO> querysp(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(
				" select  ry.*,fy.name invclassname,re.name measurename,nt.accountname kmname from ynt_inventory  ry  ");
		sf.append(" join ynt_cpaccount nt on nt.pk_corp_account = ry.pk_subject and nt.accountcode = '1405' ");
		sf.append(" left join ynt_invclassify fy on ry.pk_invclassify = fy.pk_invclassify ");
		sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
		sf.append(" where nvl(ry.dr,0) = 0 and ry.pk_corp = ? ");
		List<InventoryVO> ancevos = (List<InventoryVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(InventoryVO.class));
		if (ancevos == null || ancevos.size() == 0)
			return null;
		return ancevos;
	}

	// @Override
	// public InventoryVO queryById(String id) throws BusinessException {
	// InventoryVO vo = (InventoryVO)singleObjectBO.queryVOByID(id,
	// InventoryVO.class);
	// return vo;
	// }

	// @Override
	// public void update(InventoryVO vo) throws BusinessException {
	// String[] strs = new
	// String[]{"pk_currency","exrate","convmode_box","isfloatrate_box","memo","modifier","modifytime"};
	// singleObjectBO.update(vo, strs);
	// singleObjectBO.update(vo);
	// }

	@Override
	public void delete(InventoryVO vo) throws DZFWarpException {
		singleObjectBO.deleteObject(vo);
	}

	@Override
	public List<InventoryVO> querySpecialKM(String pk_corp) throws DZFWarpException {
		List<InventoryVO> ancevos = null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		// 判断如果是库存新模式，则取1403 和 1405的下级的末级科目。
		if (corpvo.getIbuildicstyle() != null && corpvo.getIbuildicstyle() == 1) {
			sf.append(
					" select accountcode kmcode ,accountname kmname,pk_corp_account pk_subject  from ynt_cpaccount  ry  ");
			sf.append("  where ry.pk_corp = ? and (ry.accountcode like '1403%' or ry.accountcode like '1405%') ");
			sf.append("  and  isleaf ='Y' and nvl(dr,0) = 0 ");
		} else {// 库存老模式
			sf.append(
					" select accountcode kmcode ,accountname kmname,pk_corp_account pk_subject  from ynt_cpaccount  ry  ");
			// sf.append(" where ry.pk_corp = ? and ry.isnum = 'Y' ");//zpm修改
			sf.append("  where ry.pk_corp = ? and ry.accountcode in ('1403','1405') ");// zpm修改
			sf.append("  and nvl(dr,0) = 0 order by kmcode");
		}
		ancevos = (List<InventoryVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(InventoryVO.class));
		if (ancevos == null || ancevos.size() == 0)
			return null;
		return ancevos;
	}

	@Override
	// 批量删除 ， 被引用的存货不能被删除
	public String deleteBatch(String[] ids, String pk_corp) throws DZFWarpException {
		String strids = SqlUtil.buildSqlConditionForIn(ids);
		StringBuffer sf = new StringBuffer();
		sf.append("select 1 from ynt_inventory where nvl(dr,0) = 0 and pk_corp <> ? and pk_inventory in ( ")
				.append(strids).append(" ) ");
		SQLParameter param = new SQLParameter();
		param.addParam(pk_corp);
		boolean b = singleObjectBO.isExists(pk_corp, sf.toString(), param);
		if (b) {
			throw new BusinessException("出现数据无权问题，无法删除！");
		}

		InventoryVO[] invos = (InventoryVO[]) singleObjectBO.queryByCondition(InventoryVO.class,
				" pk_inventory in ( " + strids + " ) ", null);

		if (invos == null || invos.length == 0)
			throw new BusinessException("存货不存在，或已经删除！");

		Map<String, InventoryVO> map = new HashMap<String, InventoryVO>();
		for (InventoryVO invo : invos) {
			String pk_inventory = invo.getPk_inventory();
			if (!map.containsKey(pk_inventory)) {
				map.put(pk_inventory, invo);
			}
		}

		StringBuffer errmsg = new StringBuffer();
		List<String> errlist = new ArrayList<>();

		checkInventoryRef(strids, pk_corp, errmsg, errlist, map, "ynt_ictradein", "入库单");

		checkInventoryRef(strids, pk_corp, errmsg, errlist, map, "ynt_ictradeout", "出库单");

		checkInventoryRef(strids, pk_corp, errmsg, errlist, map, "YNT_TZPZ_B", "凭证");

		checkInventoryRef(strids, pk_corp, errmsg, errlist, map, "ynt_icbalance", "库存期初");

		checkInventoryRef(strids, pk_corp, errmsg, errlist, map, "ynt_app_billapply_detail", "开票申请");

		checkInventoryKmKm(strids, pk_corp, errmsg, errlist, map, "ynt_fzhsqc", "科目期初");

		if (errlist != null && errlist.size() > 0) {

			for (String str : errlist) {
				if (map.containsKey(str))
					map.remove(str);
			}
		}

		if (map != null && map.size() > 0) {
			String[] pks = map.keySet().toArray(new String[0]);
			singleObjectBO.deleteByPKs(InventoryVO.class, pks);

			// +++关联删除存货匹配表 销项匹配存货的记录表
			goodsinvenservice.deleteCasCadeGoods(pks, pk_corp);
			ic_invtoryaliasserv.deleteByInvs(pks, pk_corp);
			if (errmsg != null && errmsg.length() > 0) {
				errmsg.append("<p>成功删除" + map.size() + "条存货!</p>");
			}

		}

		if (errmsg != null && errmsg.length() > 0) {
			return errmsg.toString();
		} else {
			return null;
		}

	}

	private void checkInventoryRefByModify(String strid, String pk_corp, Set<String> error_msg,
			Map<String, InventoryVO> map, String tablename, String msg) {

		StringBuffer sf = new StringBuffer();
		sf.append("select distinct pk_inventory from " + tablename
				+ " where pk_corp=? and nvl(dr,0) = 0 and pk_inventory in ( '").append(strid).append("' ) ");

		SQLParameter param = new SQLParameter();
		param.addParam(pk_corp);
		List<String> list = (List<String>) singleObjectBO.executeQuery(sf.toString(), param, new ColumnListProcessor());
		if (list != null && list.size() > 0) {
			for (String str : list) {
				InventoryVO invo = map.get(str);
				if (invo != null) {
					error_msg.add("存货[" + invo.getCode() + "]已被" + msg + "引用,不能修改科目!");
				}
			}
		}
	}

	private void checkInventoryRef(String strids, String pk_corp, StringBuffer errmsg, List<String> errlist,
			Map<String, InventoryVO> map, String tablename, String msg) {

		StringBuffer sf = new StringBuffer();
		sf.append("select distinct pk_inventory from " + tablename
				+ " where pk_corp=? and nvl(dr,0) = 0 and pk_inventory in ( ").append(strids).append(" ) ");

		SQLParameter param = new SQLParameter();
		param.addParam(pk_corp);
		List<String> list = (List<String>) singleObjectBO.executeQuery(sf.toString(), param, new ColumnListProcessor());
		if (list != null && list.size() > 0) {
			for (String str : list) {
				InventoryVO invo = map.get(str);
				if (invo != null) {
					if (!errlist.contains(invo.getPk_inventory())) {
						errmsg.append(
								"<p><font color = 'red'>存货[" + invo.getCode() + "]已被" + msg + "引用,不能删除!</font></p>");
						errlist.add(invo.getPk_inventory());
					}
				}
			}
		}

	}

	private void checkInventoryKmKm(String strids, String pk_corp, StringBuffer errmsg, List<String> errlist,
			Map<String, InventoryVO> map, String tablename, String msg) {

		StringBuffer sf = new StringBuffer();
		sf.append("select distinct fzhsx6 from " + tablename + " where pk_corp=? and nvl(dr,0) = 0 and fzhsx6 in ( ")
				.append(strids).append(" ) ");

		SQLParameter param = new SQLParameter();
		param.addParam(pk_corp);
		List<String> list = (List<String>) singleObjectBO.executeQuery(sf.toString(), param, new ColumnListProcessor());
		if (list != null && list.size() > 0) {
			for (String str : list) {
				InventoryVO invo = map.get(str);
				if (invo != null) {
					if (!errlist.contains(invo.getPk_inventory())) {
						errmsg.append(
								"<p><font color = 'red'>存货[" + invo.getCode() + "]已被" + msg + "引用,不能删除!</font></p>");
						errlist.add(invo.getPk_inventory());
					}
				}
			}
		}

	}

	@Override
	public InventoryVO queryByPrimaryKey(String pk) throws DZFWarpException {
		SuperVO vo = singleObjectBO.queryByPrimaryKey(InventoryVO.class, pk);
		return (InventoryVO) vo;
	}

	@Override
	public String saveImp(MultipartFile file, String pk_corp, String fileType, String userid) throws DZFWarpException {
		InputStream is = null;
		try {
			DZFDateTime date = new DZFDateTime();
			is = file.getInputStream();
			Workbook impBook = null;
			if ("xls".equals(fileType)) {
				impBook = new HSSFWorkbook(is);
			} else if ("xlsx".equals(fileType)) {
				impBook = new XSSFWorkbook(is);
			} else {
				throw new BusinessException("不支持的文件格式");
			}
			Sheet sheet1 = impBook.getSheetAt(0);
			Map<String, InvclassifyVO> invclassmap = new HashMap<>();
			Map<String, MeasureVO> jldwmap = new HashMap<>();

			List<InvclassifyVO> newInvclVoList = new ArrayList<>();
			List<MeasureVO> newMeasureVOList = new ArrayList<>();

			List<InvclassifyVO> spflVO = splbService.query(pk_corp);
			List<MeasureVO> jldwVO = jldwService.query(pk_corp);

			if (spflVO != null && spflVO.size() > 0) {
				for (InvclassifyVO spflvo : spflVO) {
					invclassmap.put(spflvo.getName(), spflvo);
				}

			}

			if (jldwVO != null && jldwVO.size() > 0) {
				for (MeasureVO spflvo : jldwVO) {
					jldwmap.put(spflvo.getName(), spflvo);
				}
			}

			YntCpaccountVO[] accvos = accountService.queryByPk(pk_corp);
			if (accvos == null || accvos.length == 0) {
				throw new BusinessException("当前公司没有科目");
			}

			Map<String, YntCpaccountVO> map = DZfcommonTools.hashlizeObjectByPk(
					new ArrayList<YntCpaccountVO>(Arrays.asList(accvos)), new String[] { "accountcode" });

			CorpVO corpvo = corpService.queryByPk(pk_corp);

			List<InventoryVO> listAll = queryInventoryVO(pk_corp);

			HashSet<String> codeSet = new HashSet<String>();
			HashSet<String> nameInfoSet = new HashSet<String>();
			if (listAll != null && listAll.size() != 0) {
				for (InventoryVO vo : listAll) {
					codeSet.add(getCodeUnitKey(vo));
					nameInfoSet.add(getNameInfoKey(vo));
				}
			}

			String invclcode = yntBoPubUtil.getInvclCode(pk_corp);
			String mescode = yntBoPubUtil.getMeasureCode(pk_corp);
			List<InventoryVO> list = new ArrayList<InventoryVO>();
			Cell kmcodecell = null;
			Cell codeCell = null;
			Cell nameCell = null;
			Cell shortnameCell = null;
			Cell spflCell = null;
			Cell ggCell = null;
			Cell xhCell = null;
			Cell jldwCell = null;
			Cell jsjCell = null;
			Cell memoCell = null;
			String kmcode = null;
			String name = null;
			String code = null;
			String shortname = null;
			String spfl = null;
			String gg = null;
			String xh = null;
			String jldw = null;
			String memo = null;
			String jsj = null;
			int failCount = 0;
			StringBuffer msg = new StringBuffer();
			StringBuffer namemsg = new StringBuffer();
			int length = sheet1.getLastRowNum();

			if (length > 1000) {
				throw new BusinessException("最多可导入1000行");
			}

			InventoryVO vo = null;
			boolean kmmcFlag = false;
			boolean kmmaFlagold = false;// 老模式库存
			boolean jldwFlag = false;
			boolean invclFlag = false;
			boolean isSame = false;
			InvclassifyVO invclassvo = null;
			MeasureVO jldwvo = null;
			for (int iBegin = 1; iBegin <= length; iBegin++) {
				kmmcFlag = false;
				kmmaFlagold = false;// 老模式库存
				jldwFlag = false;
				isSame = false;
				vo = new InventoryVO();
				kmcodecell = sheet1.getRow(iBegin).getCell(0);
				codeCell = sheet1.getRow(iBegin).getCell(2);
				nameCell = sheet1.getRow(iBegin).getCell(3);
				shortnameCell = sheet1.getRow(iBegin).getCell(4);
				spflCell = sheet1.getRow(iBegin).getCell(5);
				ggCell = sheet1.getRow(iBegin).getCell(6);
//				xhCell = sheet1.getRow(iBegin).getCell(7);
				jldwCell = sheet1.getRow(iBegin).getCell(7);
				jsjCell = sheet1.getRow(iBegin).getCell(8);
				memoCell = sheet1.getRow(iBegin).getCell(9);

				// 科目赋值
				if (kmcodecell != null && kmcodecell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					kmcode = kmcodecell.getRichStringCellValue().getString();
					if (kmcode != null && kmcode.length() > 0) {
						kmcode = kmcode.trim();
						// 老库存模式，科目编码必须为1403或者1405开头。
//						if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle().intValue() == 0) {
							if (!"1405".equals(kmcode) && !"1403".equals(kmcode)) {
								kmmaFlagold = true;
							}
//						}
						if (!kmmaFlagold) {
							if (map.containsKey(kmcode)) {
								YntCpaccountVO v = map.get(kmcode);
								v = getFisrtNextLeafAccount(v, pk_corp, accvos);
								vo.setPk_subject(v.getPk_corp_account());
							} else {
								kmmcFlag = true;
							}
						}
					} else {
						kmmcFlag = true;
					}
				}

				if (codeCell != null && codeCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					code = codeCell.getRichStringCellValue().getString();
					code = code.trim();
					vo.setCode(code);
				} else if (codeCell != null && codeCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
					int codeVal = Double.valueOf(codeCell.getNumericCellValue()).intValue();
					code = String.valueOf(codeVal);
					vo.setCode(code);
				}
				if (nameCell != null && nameCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					name = nameCell.getRichStringCellValue().getString();
					name = name.trim();
					vo.setName(name);
				}
				if (shortnameCell != null && shortnameCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					shortname = shortnameCell.getRichStringCellValue().getString();
					shortname = shortname.trim();
					vo.setShortname(shortname);
				}
				if (spflCell != null && spflCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					spfl = spflCell.getRichStringCellValue().getString();
					spfl = spfl.trim();
					invclassvo = invclassmap.get(spfl);

					if (invclassvo == null && !StringUtil.isEmpty(spfl)) {
						invclassvo = buildInvClassifyVO(spfl, pk_corp);
						if (!invclassmap.containsKey(spfl)) {
							invclassmap.put(spfl, invclassvo);
						}
						invclFlag = true;
					} else {
						invclFlag = false;
					}
					if (invclassvo != null)
						vo.setPk_invclassify(invclassvo.getPk_invclassify());
				}
				if (ggCell != null && ggCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					gg = ggCell.getRichStringCellValue().getString();
					gg = gg.trim();
					vo.setInvspec(gg);
				} else if (ggCell != null && ggCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
					Double codeVal = Double.valueOf(ggCell.getNumericCellValue());
					gg = codeVal.toString();
					vo.setInvspec(gg);
				}
				if (xhCell != null && xhCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					xh = xhCell.getRichStringCellValue().getString();
					xh = xh.trim();
//					vo.setInvtype(xh);
				}
				if (jldwCell != null && jldwCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					jldw = jldwCell.getRichStringCellValue().getString();
					jldw = jldw.trim();
					jldwvo = jldwmap.get(jldw);
					if (jldwvo == null && !StringUtil.isEmpty(jldw)) {
						jldwvo = buildMeasureVO(jldw, pk_corp, userid);
						if (!jldwmap.containsKey(jldw)) {
							jldwmap.put(jldw, jldwvo);
						}
						jldwFlag = true;
					} else {
						jldwFlag = false;
					}
					if (jldwvo != null)
						vo.setPk_measure(jldwvo.getPk_measure());
				}

				if (jsjCell != null && jsjCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					jsj = jsjCell.getRichStringCellValue().getString().trim();
					DZFDouble costVal = new DZFDouble(jsj);
					vo.setJsprice(costVal);
				} else if (jsjCell != null && jsjCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
					DZFDouble costVall = new DZFDouble(jsjCell.getNumericCellValue());
					vo.setJsprice(costVall);
				}

				if (memoCell != null && memoCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					memo = memoCell.getRichStringCellValue().getString();
					memo = memo.trim();
					vo.setMemo(memo);
				}
				if (StringUtil.isEmpty(kmcode)) {
					failCount++;
					msg.append("<p><font color = 'red'>第").append(iBegin + 1).append("行科目编码为空！</font></p>");
					continue;
				}

				if (StringUtil.isEmpty(code)) {
					failCount++;
					msg.append("<p><font color = 'red'>第").append(iBegin + 1).append("行存货编码为空！</font></p>");
					continue;
				}

				if (StringUtil.isEmpty(name)) {
					failCount++;
					msg.append("<p><font color = 'red'>第").append(iBegin + 1).append("行存货名称为空！</font></p>");
					continue;
				}

				// if (StringUtil.isEmpty(jldw)) {
				// failCount++;
				// msg.append("<p>第").append(iBegin + 1).append("行计量单位为空！</p>");
				// continue;
				// }

				if (kmmaFlagold) {
					failCount++;
					msg.append("<p><font color = 'red'>第").append(iBegin + 1)
							.append("行，当前库存模式，科目编码必须为1403或者1405</font></p>");
					continue;
				}
				if (kmmcFlag) {
					failCount++;
					msg.append("<p><font color = 'red'>第").append(iBegin + 1).append("行的科目编码在系统中未找到！</font></p>");
					continue;
				}

				String codekey = getCodeUnitKey(vo);
				if (codeSet.contains(codekey)) {
					msg.append("<p><font color = 'red'>第").append(iBegin + 1)
							.append("行存货编码[" + vo.getCode() + "]和计量单位至少要有一项不同!</font></p>");
					isSame = true;
				} else {
					codeSet.add(codekey);
				}

				String nameInfoKey = getNameInfoKey(vo);
				if (nameInfoSet.contains(nameInfoKey)) {
					namemsg.setLength(0);
					if (!StringUtil.isEmpty(vo.getName())) {
						namemsg.append("存货名称[" + vo.getName() + "]、");
					}
					if (!StringUtil.isEmpty(vo.getInvspec())) {
						namemsg.append("规格(型号)[" + vo.getInvspec() + "]、");
					} else {
						namemsg.append("规格(型号)、");
					}
//					if (!StringUtil.isEmpty(vo.getInvtype())) {
//						namemsg.append("型号[" + vo.getInvtype() + "] 、");
//					} else {
//						namemsg.append("型号、");
//					}

					if (!isSame) {
						msg.append("<p><font color = 'red'>第").append(iBegin + 1).append("行")
								.append(namemsg.toString() + "计量单位和 科目 至少要有一项不同!</font></p>");
						isSame = true;
					}
				} else {
					nameInfoSet.add(nameInfoKey);
				}

				if (StringUtil.isEmpty(vo.getCode())){
					msg.append("<p><font color = 'red'>第").append(iBegin + 1)
					.append("行存货编码为空!</font></p>");
					isSame = true;
				}
					
				if (StringUtil.isEmpty(vo.getName())){
					msg.append("<p><font color = 'red'>第").append(iBegin + 1)
					.append("行存货名称为空!</font></p>");
					isSame = true;
				}
				
				if (isSame) {
					failCount++;
					continue;
				}

				vo.setPk_corp(pk_corp);
				vo.setCreator(userid);
				vo.setCreatetime(date);
				vo.setXslx(0);
				
				
				list.add(vo);
				if (invclFlag) {
					if (invclassvo != null){
						if(newInvclVoList.size()==0){
							invclassvo.setCode(invclcode);
						}else{
							invclcode =getCode(invclcode);
							invclassvo.setCode(invclcode);
						}
						newInvclVoList.add(invclassvo);
						invclassvo = null;
					}
						
				}

				if (jldwFlag) {
					if (jldwvo != null){
						if(newMeasureVOList.size()==0){
							jldwvo.setCode(mescode);
						}else{
							mescode =getCode(mescode);
							jldwvo.setCode(mescode);
						}
						newMeasureVOList.add(jldwvo);
						jldwvo = null;
					}
						
				}
			}

			
			if(list != null && list.size()>0){
				InventoryVO[] newvos = new InventoryVO[list.size()];
				newvos = list.toArray(newvos);
                singleObjectBO.insertVOArr(pk_corp, newvos);
			}
			
			if (newInvclVoList != null && newInvclVoList.size() > 0) {
                singleObjectBO.insertVOWithPK(pk_corp,
						newInvclVoList.toArray(new InvclassifyVO[newInvclVoList.size()]));
			}
			
			if (newMeasureVOList != null && newMeasureVOList.size() > 0) {
                singleObjectBO.insertVOWithPK(pk_corp,
						newMeasureVOList.toArray(new MeasureVO[newMeasureVOList.size()]));
			}

			if (StringUtil.isEmpty(msg.toString())) {
				return null;
			} else {
				msg.append("成功导入 ").append(list.size()).append(" 条数据。失败 ").append(failCount).append(" 条");
				return msg.toString();
			}
		} catch (FileNotFoundException e) {
			throw new BusinessException("导入文件未找到");
		} catch (IOException e) {
			throw new BusinessException(e.getMessage());
		} catch (BusinessException e) {
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			throw new BusinessException("导入出错,请检查模板!");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	private String getCode(String code) {
		Long result = 1L;
		Long maxNum = 1L;
		try {
			result = Long.parseLong(code.trim()) + 1;
			if (result > maxNum) {
				maxNum = result;
			}
		} catch (Exception e) {
			// 吃掉异常
		}
		code =getFinalcode(maxNum);
		return code;
	}
	private String getFinalcode(Long code){
		String str = "";
		if(code > 0 && code < 10){
			str = "00"+String.valueOf(code);
		}else if(code > 9 && code < 100){
			str = "0"+String.valueOf(code);
		}else{
			str = String.valueOf(code);
		}
		return str;
	}

	// 查询第一分支的最末级非外币科目
	private YntCpaccountVO getFisrtNextLeafAccount(YntCpaccountVO account, String pk_corp, YntCpaccountVO[] accounts) {

		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();// 存储下级科目

		for (YntCpaccountVO accvo : accounts) {
			if (accvo.getIsleaf().booleanValue() && accvo.getAccountcode() != null
					&& accvo.getAccountcode().startsWith(account.getAccountcode())) {
				list.add(accvo);
			}
		}

		if (list == null || list.size() == 0) {
			return null;
		}
		YntCpaccountVO[] accountvo = list.toArray(new YntCpaccountVO[list.size()]);
		VOUtil.ascSort(accountvo, new String[] { "accountcode" });
		return accountvo[0];
	}

	private InvclassifyVO buildInvClassifyVO(String spfl, String pk_corp) {
		InvclassifyVO vo = new InvclassifyVO();
//		vo.setCode(yntBoPubUtil.getInvclCode(pk_corp));
		vo.setName(spfl);
		vo.setPk_corp(pk_corp);
		// creator 前台新增未赋值
		// ic_inclsserv.save(vo);
		// singleObjectBO.insertVOWithPK(vo)
		String npks[] = IDGenerate.getInstance().getNextIDS(pk_corp, 1);
		vo.setPk_invclassify(npks[0]);
		return vo;
	}

	private MeasureVO buildMeasureVO(String jldw, String pk_corp, String creator) {
		MeasureVO vo = new MeasureVO();
//		vo.setCode(yntBoPubUtil.getMeasureCode(pk_corp));
		vo.setName(jldw);
		vo.setPk_corp(pk_corp);
		vo.setCreator(creator);
		vo.setCreatetime(new DZFDateTime());
		// ic_measureserv.saveVO(vo);
		String npks[] = IDGenerate.getInstance().getNextIDS(pk_corp, 1);
		vo.setPk_measure(npks[0]);
		return vo;
	}

	private List<InventoryVO> queryInventoryVO(String pk_corp) {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append("pk_corp=? and nvl(dr,0)=0");
		sp.addParam(pk_corp);
		List<InventoryVO> listVo = (List<InventoryVO>) singleObjectBO.retrieveByClause(InventoryVO.class, sb.toString(),
				sp);
		return listVo;
	}

	@Override
	public InventoryVO[] save1(String pk_corp, InventoryVO[] vos) throws DZFWarpException {

		List<InventoryVO> list1 = new ArrayList<InventoryVO>();
		List<InventoryVO> list2 = new ArrayList<InventoryVO>();
		for (InventoryVO v : vos) {
			if (StringUtil.isEmpty(v.getPk_inventory())) {
				list1.add(v);
			} else {
				list2.add(v);
			}
			if (v.getXslx() == null)
				v.setXslx(0);
		}
		checkBeforeSave(pk_corp, vos, null);
		if (list1.size() > 0) {

			singleObjectBO.insertVOArr(pk_corp, list1.toArray(new InventoryVO[0]));
		}
		if (list2.size() > 0) {
			singleObjectBO.updateAry(list2.toArray(new InventoryVO[0]));
		}
		List<String> list3 = new ArrayList<String>();
		for (InventoryVO vo : vos) {
			list3.add(vo.getPrimaryKey());
		}
		String condition = SqlUtil.buildSqlForIn("pk_inventory", list3.toArray(new String[list3.size()]));

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select  ry.*,fy.name invclassname,re.name measurename,nt.accountname kmname ,"
				+ " nt.accountcode kmcode,ry.pk_subject from ynt_inventory  ry  ");
		sf.append(" left join ynt_invclassify fy on ry.pk_invclassify = fy.pk_invclassify ");
		sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
		sf.append(" left join ynt_cpaccount nt on nt.pk_corp_account = ry.pk_subject ");
		sf.append(" where nvl(ry.dr,0) = 0 and ry.pk_corp = ?  and ");
		sf.append(condition);

		List<InventoryVO> invlist = (List<InventoryVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(InventoryVO.class));
		if (invlist == null || invlist.size() == 0) {
			return null;
		}
		return invlist.toArray(new InventoryVO[invlist.size()]);
	}

	@Override
	public Map<String, InventoryVO> queryInventoryVOs(String pk_corp, String[] pks) throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		String ina = SqlUtil.buildSqlForIn("pk_inventory", pks);
		sf.append("select * from ynt_inventory where pk_corp = ? and  " + ina);
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		List<InventoryVO> list = (List<InventoryVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(InventoryVO.class));
		Map<String, InventoryVO> map = DZfcommonTools.hashlizeObjectByPk(list, new String[] { "pk_inventory" });
		return map;
	}

	@Override
	public List<InventoryVO> query(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select  ry.*,fy.name invclassname,re.name measurename,nt.accountname kmname ,"
				+ " nt.accountcode kmcode,ry.pk_subject from ynt_inventory  ry  ");
		sf.append(" left join ynt_invclassify fy on ry.pk_invclassify = fy.pk_invclassify ");
		sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
		sf.append(" left join ynt_cpaccount nt on nt.pk_corp_account = ry.pk_subject ");
		sf.append(" where nvl(ry.dr,0) = 0 and ry.pk_corp = ? ");

		List<InventoryVO> ancevos = (List<InventoryVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(InventoryVO.class));
		if (ancevos == null || ancevos.size() == 0)
			return null;

		VOUtil.ascSort(ancevos, new String[] { "code" });
		return ancevos;
	}

	@Override
	public InventoryVO saveMergeData(String pk_corp, String id, InventoryVO[] vos) throws DZFWarpException {

		CorpVO corpvo = corpService.queryByPk(pk_corp);

		InventoryVO vo = queryByPrimaryKey(id);
		if (DZFValueCheck.isEmpty(vo))
			throw new BusinessException("合并的存货不存在!");

		if (DZFValueCheck.isEmpty(vos)) {
			throw new BusinessException("被合并的存货不允许为空!");
		}
		checkMergeData(corpvo, vo, vos);

		SQLParameter sp = new SQLParameter();
		List<SQLParameter> list = new ArrayList<SQLParameter>();
		List<String> idlist = new ArrayList<>();
		idlist.add(id);
		for (InventoryVO vo1 : vos) {
			sp = new SQLParameter();
			sp.addParam(vo.getPk_inventory());
			sp.addParam(vo1.getPk_inventory());
			sp.addParam(corpvo.getPk_corp());
			list.add(sp);
			idlist.add(vo1.getPk_inventory());
		}

		// 更新凭证里的存货id
		StringBuffer sb = new StringBuffer();
		sb.append(" update ynt_tzpz_b set pk_inventory=? where pk_inventory =?  and pk_corp=? and nvl(dr,0)=0");
		int row = singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));

		// 更新别名表的存货id
		sb.setLength(0);
		sb.append(" update ynt_icalias set pk_inventory=? where pk_inventory =?  and pk_corp=? and nvl(dr,0)=0");
		row = singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));

		// 更新出库单的存货id
		sb.setLength(0);
		sb.append(" update ynt_ictradeout set pk_inventory=? where pk_inventory =?  and pk_corp=? and nvl(dr,0)=0");
		row = singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));

		// 更新入库单的存货id
		sb.setLength(0);
		sb.append(" update ynt_ictradein set pk_inventory=? where pk_inventory =?  and pk_corp=? and nvl(dr,0)=0");
		row = singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));

		// 更新库存老模式的 中间表存货id
		sb.setLength(0);
		sb.append(" update ynt_subinvtory set pk_inventory=? where pk_inventory =?  and pk_corp=? and nvl(dr,0)=0");
		row = singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));

		String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
		int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		// 库存期初合并数据
		updateIcBalance(pk_corp, idlist, price, id);
		// 辅助期初的合并数据 并同步的科目期初
		updateIcFzQc(pk_corp, idlist, price, id);
		// 年结表合并数据
		updateIcNj(pk_corp, idlist, price, id);
		// 更新被合并存货dr=1
		list.clear();
		for (InventoryVO vo1 : vos) {
			sp = new SQLParameter();
			sp.addParam(vo1.getPk_inventory());
			sp.addParam(corpvo.getPk_corp());
			list.add(sp);
		}
		sb.setLength(0);
		sb.append(" update ynt_inventory set dr=1 where pk_inventory =? and  pk_corp=? and nvl(dr,0)=0");
		row = singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));
		return vo;
	}

	private void checkMergeData(CorpVO corpvo, InventoryVO vo, InventoryVO[] vos) {

		String temp1 = "";
		String temp2 = "";
		StringBuffer msg = new StringBuffer();
		List<String> list = new ArrayList<>();
		for (InventoryVO invo : vos) {
			list.clear();
			temp1 = StringUtil.isEmpty(invo.getPk_subject()) ? "invnull" : invo.getPk_subject();
			temp2 = StringUtil.isEmpty(vo.getPk_subject()) ? "invnull" : vo.getPk_subject();
			if (!temp1.equals(temp2))
				list.add("科目");

			temp1 = StringUtil.isEmpty(invo.getPk_measure()) ? "invnull" : invo.getPk_measure();
			temp2 = StringUtil.isEmpty(vo.getPk_measure()) ? "invnull" : vo.getPk_measure();
			if (!temp1.equals(temp2))
				list.add("计量单位");

			if (list.size() > 0) {
				msg.append("<p><font color = 'red'>存货[ " + getStrInvName(vo) + "] 和存货[" + getStrInvName(invo) + "]的");
				int size = list.size();
				for (int i = 0; i < size; i++) {
					if (i == 0) {
						msg.append(list.get(i));
					} else {
						msg.append("\\" + list.get(i));
					}
				}
				msg.append("不一致，请检查</font></p>");
			}
		}
		if (msg.length() > 0)
			throw new BusinessException(msg.toString());
	}

	private String getStrInvName(InventoryVO vo) {
		String temp1 = vo.getName();
		if (!StringUtil.isEmpty(vo.getInvspec())) {
			temp1 = temp1 + " " + vo.getInvspec();
		}

//		if (!StringUtil.isEmpty(vo.getInvtype())) {
//			temp1 = temp1 + " " + vo.getInvtype();
//		}

		return temp1;
	}

	private void updateIcBalance(String pk_corp, List<String> idlist, int price, String id) {

		String wherepart = SqlUtil.buildSqlConditionForIn(idlist.toArray(new String[idlist.size()]));
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		IcbalanceVO[] bals = (IcbalanceVO[]) singleObjectBO.queryByCondition(IcbalanceVO.class,
				" pk_inventory in (" + wherepart + ") and nvl(dr,0) = 0 and pk_corp = ?", sp);

		if (bals != null && bals.length > 0) {
			int len = bals.length;
			IcbalanceVO vo = bals[0];
			IcbalanceVO temp = null;
			for (int i = 1; i < len; i++) {
				temp = bals[i];
				vo.setNcost(SafeCompute.add(vo.getNcost(), temp.getNcost()));
				vo.setNnum(SafeCompute.add(vo.getNnum(), temp.getNnum()));
				vo.setNprice(SafeCompute.div(vo.getNcost(), vo.getNnum()).setScale(price, 2));
				bals[i].setDr(1);
			}
			vo.setPk_inventory(id);
			singleObjectBO.updateAry(bals);
		}

	}

	private void updateIcFzQc(String pk_corp, List<String> idlist, int price, String id) {

		String wherepart = SqlUtil.buildSqlConditionForIn(idlist.toArray(new String[idlist.size()]));
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		FzhsqcVO[] bals = (FzhsqcVO[]) singleObjectBO.queryByCondition(FzhsqcVO.class,
				" fzhsx6 in (" + wherepart + ") and nvl(dr,0) = 0 and pk_corp = ?", sp);

		if (bals == null || bals.length == 0)
			return;

		Map<String, List<FzhsqcVO>> map = DZfcommonTools.hashlizeObject(Arrays.asList(bals),
				new String[] { "pk_accsubj", "fzhsx1", "fzhsx2", "fzhsx3", "fzhsx4", "fzhsx5", "fzhsx7", "fzhsx8",
						"fzhsx9", "fzhsx10" });

		for (Map.Entry<String, List<FzhsqcVO>> entry : map.entrySet()) {
			List<FzhsqcVO> list = entry.getValue();
			if (list != null && list.size() > 0) {
				int size = list.size();

				if (size == 1) {
					list.get(0).setFzhsx6(id);
				} else {
					FzhsqcVO vo = list.get(0);
					FzhsqcVO temp = null;
					String[] cols = new String[] { "yearqc", "yearjffse", "yeardffse", "ybyearqc", "ybyearjffse",
							"ybyeardffse", "bnqcnum", "bnfsnum", "bndffsnum" };
					for (int i = 1; i < size; i++) {
						temp = list.get(i);
						for (String name : cols) {
							vo.setAttributeValue(name, SafeCompute.add((DZFDouble) vo.getAttributeValue(name),
									(DZFDouble) temp.getAttributeValue(name)));
						}
						calFzQc(vo);
						list.get(i).setDr(1);
					}
					vo.setFzhsx6(id);
				}
			}
		}
		singleObjectBO.updateAry(bals);

	}

	private void calFzQc(FzhsqcVO fzqc) {
		if (fzqc == null)
			return;
		DZFDouble qm = null;
		DZFDouble qcnum = null;
		DZFDouble ybqm = null;
		if (fzqc.getDirect() == 0) {// 借方
			qm = SafeCompute.sub(SafeCompute.add(fzqc.getYearqc(), fzqc.getYearjffse()), fzqc.getYeardffse());
			ybqm = SafeCompute.sub(SafeCompute.add(fzqc.getYbyearqc(), fzqc.getYbyearjffse()), fzqc.getYbyeardffse());
			qcnum = SafeCompute.sub(SafeCompute.add(fzqc.getBnqcnum(), fzqc.getBnfsnum()), fzqc.getBndffsnum());
		} else {// 贷方
			qm = SafeCompute.sub(SafeCompute.add(fzqc.getYearqc(), fzqc.getYeardffse()), fzqc.getYearjffse());
			ybqm = SafeCompute.sub(SafeCompute.add(fzqc.getYbyearqc(), fzqc.getYbyeardffse()), fzqc.getYbyearjffse());
			qcnum = SafeCompute.sub(SafeCompute.add(fzqc.getBnqcnum(), fzqc.getBndffsnum()), fzqc.getBnfsnum());
		}
		fzqc.setThismonthqc(qm);
		fzqc.setYbthismonthqc(ybqm);
		fzqc.setMonthqmnum(qcnum);
	}

	private void updateIcNj(String pk_corp, List<String> idlist, int price, String id) {

		String wherepart = SqlUtil.buildSqlConditionForIn(idlist.toArray(new String[idlist.size()]));
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		KMQMJZVO[] bals = (KMQMJZVO[]) singleObjectBO.queryByCondition(KMQMJZVO.class,
				" fzhsx6 in (" + wherepart + ") and nvl(dr,0) = 0 and pk_corp = ?", sp);

		if (bals == null || bals.length == 0)
			return;

		Map<String, List<KMQMJZVO>> map = DZfcommonTools.hashlizeObject(Arrays.asList(bals),
				new String[] { "pk_accsubj", "period", "pk_currency", "fzhsx1", "fzhsx2", "fzhsx3", "fzhsx4", "fzhsx5",
						"fzhsx7", "fzhsx8", "fzhsx9", "fzhsx10" });

		List<KMQMJZVO> alist = new ArrayList<>();
		for (Map.Entry<String, List<KMQMJZVO>> entry : map.entrySet()) {
			List<KMQMJZVO> list = entry.getValue();
			if (list != null && list.size() > 0) {
				int len = list.size();
				KMQMJZVO vo = list.get(0);
				KMQMJZVO temp = null;
				for (int i = 1; i < len; i++) {
					temp = list.get(i);
					vo.setThismonthqc(SafeCompute.add(vo.getThismonthqc(), temp.getThismonthqc()));
					vo.setJffse(SafeCompute.add(vo.getJffse(), temp.getJffse()));
					vo.setDffse(SafeCompute.add(vo.getDffse(), temp.getDffse()));
					vo.setThismonthqm(SafeCompute.add(vo.getThismonthqm(), temp.getThismonthqm()));
					vo.setYbjfmny(SafeCompute.add(vo.getYbjfmny(), temp.getYbjfmny()));
					vo.setYbdfmny(SafeCompute.add(vo.getYbdfmny(), temp.getYbdfmny()));
					vo.setYbthismonthqc(SafeCompute.add(vo.getYbthismonthqc(), temp.getYbthismonthqc()));
					vo.setYbthismonthqm(SafeCompute.add(vo.getYbthismonthqm(), temp.getYbthismonthqm()));
					list.get(i).setDr(1);
					alist.add(list.get(i));
				}
				vo.setFzhsx6(id);
				alist.add(vo);
			}
		}
		singleObjectBO.updateAry(bals);
	}

	@Override
	public String updateBatch(String pk_corp, String ids, InventoryVO updatevo) throws DZFWarpException {

		List<InventoryVO> listAll = queryInventoryVO(pk_corp);
		String[] idsArr = ids.split(",");

		Map<String, InventoryVO> map = DZfcommonTools.hashlizeObjectByPk(listAll, new String[] { "pk_inventory" });
		List<InventoryVO> bvolist = new ArrayList<InventoryVO>();
		for (int i = 0; i < idsArr.length; i++) {
			InventoryVO aabvo = map.get(idsArr[i]);
			if (aabvo == null) {
				continue;
			}
			aabvo.setPk_inventory(idsArr[i]);
			if (!StringUtil.isEmpty(updatevo.getInvspec())) {
				aabvo.setInvspec(updatevo.getInvspec());
			}
			if (!StringUtil.isEmpty(updatevo.getPk_measure())) {
				aabvo.setPk_measure(updatevo.getPk_measure());
			}
//			if (!StringUtil.isEmpty(updatevo.getInvtype())) {
//				aabvo.setInvtype(updatevo.getInvtype());
//			}

			if (!StringUtil.isEmpty(updatevo.getPk_invclassify())) {
				aabvo.setPk_invclassify(updatevo.getPk_invclassify());
			}
			bvolist.add(aabvo);
		}
		String susflag = save(pk_corp, bvolist.toArray(new InventoryVO[bvolist.size()]), listAll);
		return susflag;
	}

	@Override
	public InventoryVO createPrice(String pk_corp, String priceway, String bili, String vdate, InventoryVO[] vos)
			throws DZFWarpException {
		DZFDate vDate = new DZFDate();
		if (!StringUtil.isEmpty(vdate)) {
			vDate = new DZFDate(vdate);
		}
		String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
		int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
		int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);

		if ("1".equals(priceway)) {
//			SQLParameter sp = new SQLParameter();
//			StringBuffer sf = new StringBuffer();
//			sf.append(" select * from ynt_qmcl where nvl(dr,0)=0 and pk_corp = ? and  period = ? ");
//			sp.addParam(pk_corp);
//			sp.addParam(DateUtils.getPeriod(vDate));
//			List<QmclVO> list = (List<QmclVO>) singleObjectBO.executeQuery(sf.toString(),
//					sp, new BeanListProcessor(QmclVO.class));
//
//			if(list ==null || list.size()==0){
//				throw new BusinessException("期末处理数据出错！");
//			}else{
//				DZFBoolean value = list.get(0).getIscbjz();
//				if(value == null || !value.booleanValue()){
//					throw new BusinessException("本期还未成本结转，请先结转成本或选择按【销售平均单价】生成结算价！");
//				}
//			}
			setJcdj(vos, pk_corp, vDate, num, price);
		} else if ("2".equals(priceway)) {
			setXsdj(vos, pk_corp, vDate, bili, price);
		}
		updateJsPrice(vos);
		return null;
	}

	private void setJcdj(InventoryVO[] vos, String pk_corp, DZFDate vDate, int num, int price) {
		if (vos == null || vos.length == 0)
			return;
		Map<String, IcbalanceVO> balMap = ic_rep_cbbserv.queryLastBanlanceVOs_byMap1(vDate.toString(), pk_corp, null,
				true);

		// 新模式模式 启用库存
		CorpVO corpVo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);// 防止vo信息有变化
		Map<String, IcbalanceVO> balMap1 = ic_rep_cbbserv.queryLastBanlanceVOs_byMap4(vDate.toString(), pk_corp, null,
				true);
		for (InventoryVO vo : vos) {
			if (corpVo.getIbuildicstyle() != null && corpVo.getIbuildicstyle() == 1) {// 新模式库存
				if (balMap != null && balMap.size() > 0) {
					IcbalanceVO balvo = balMap.get(vo.getPk_inventory());
					if (balvo != null) {
						vo.setNjznum(balvo.getNnum() == null ? balvo.getNnum()
								: new DZFDouble(balvo.getNnum().toString(), num));
					}

					IcbalanceVO balvo1 = balMap1.get(vo.getPk_inventory());
					if (balvo1 != null) {
						if ((vo.getNjznum() == null || vo.getNjznum().doubleValue() == 0)
								&& (vo.getNjzmny() == null || vo.getNjzmny().doubleValue() == 0)) {
						} else {
							vo.setNcbprice(SafeCompute.div(balvo1.getNcost(), balvo1.getNnum()).setScale(price, 2));
							vo.setNjzmny(balvo1.getNcost());
							vo.setJsprice(SafeCompute.div(balvo1.getNcost(), balvo1.getNnum()).setScale(price, 2));
						}
					}
				}
			} else {
				if ((vo.getNjznum() == null || vo.getNjznum().doubleValue() == 0)
						&& (vo.getNjzmny() == null || vo.getNjzmny().doubleValue() == 0)) {
				} else {
					vo.setNcbprice(SafeCompute.div(vo.getNjzmny(), vo.getNjznum()).setScale(price, 2));
					vo.setJsprice(SafeCompute.div(vo.getNjzmny(), vo.getNjznum()).setScale(price, 2));
				}
			}
		}
	}

	private void setXsdj(InventoryVO[] vos, String pk_corp, DZFDate vDate, String bili, int price)
			throws DZFWarpException {
		if (vos == null || vos.length == 0)
			return;
		String period = DateUtils.getPeriod(vDate);
		String bdate = DateUtils.getPeriodStartDate(period).toString();
		String edate = DateUtils.getPeriodEndDate(period).toString();
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select sum(nnum) nnum ,sum(nymny) nymny,pk_inventory from ( ");
		// 出库
		sf.append("  (select out1.pk_inventory,nnum as nnum ,nymny as nymny from ynt_ictradeout out1 ");
		sf.append(
				"   where out1.dbilldate >= ? and out1.dbilldate <= ? and out1.pk_corp =  ? and nvl(out1.dr,0)=0  and nvl(out1.nnum,0)>0 and nvl(out1.nymny,0)>0 ");
		sp.addParam(bdate);
		sp.addParam(edate);
		sp.addParam(pk_corp);
		sf.append("  )) group by pk_inventory ");
		List<IcbalanceVO> ancevos = (List<IcbalanceVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(IcbalanceVO.class));
		Map<String, IcbalanceVO> balMap = new java.util.HashMap<String, IcbalanceVO>();
		if (ancevos != null && ancevos.size() > 0) {
			for (IcbalanceVO v : ancevos) {
				balMap.put(v.getPk_inventory(), v);
			}
		}
		DZFDouble dbili = new DZFDouble(bili);
		for (InventoryVO vo : vos) {
			if (balMap != null && balMap.size() > 0) {
				IcbalanceVO balvo = balMap.get(vo.getPk_inventory());
				if (balvo != null) {
					vo.setJsprice(SafeCompute.multiply(SafeCompute.div(balvo.getNymny(), balvo.getNnum()), dbili)
							.setScale(price, 2));
				}
			}
		}
	}

	private void updateJsPrice(InventoryVO[] vos) {
		if (vos == null || vos.length == 0)
			return;
		//更新价格大于零的结算价
		List<InventoryVO> list = Arrays.asList(vos);
		List<InventoryVO> btlist = list.stream()
				.filter(item -> item.getJsprice() != null && item.getJsprice().doubleValue() > 0)
				.collect(Collectors.toList());
		if(btlist == null || btlist.size() == 0)
			return;
		singleObjectBO.updateAry(btlist.toArray(new InventoryVO[btlist.size()]), new String[] { "jsprice" });

	}
}
