package com.dzf.zxkj.platform.service.gzgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryAccSetVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryKmDeptVO;
import com.dzf.zxkj.platform.model.gzgl.SalarySetTableVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.SalaryModelBVO;
import com.dzf.zxkj.platform.model.sys.SalaryModelHVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.gzgl.ISalaryAccSetService;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("gl_gzkmszserv")
public class SalaryAccSetServiceImpl implements ISalaryAccSetService {

	@Autowired
	private YntBoPubUtil yntBoPubUtil;
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accService;
	@Override
	public SalaryAccSetVO query(String pk_corp) throws DZFWarpException {
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		String condition = " pk_corp = ? and nvl(dr,0) = 0 ";
		SalaryAccSetVO[] pvos = (SalaryAccSetVO[]) singleObjectBO.queryByCondition(SalaryAccSetVO.class, condition,
				params);
		if (pvos != null && pvos.length > 0) {
			SalaryAccSetVO gvo = pvos[0];
			return gvo;
		} else {
			// 设置默认并保存 暂不处理
			// if(gl_cbconstant.getFangan_2007().equals(corpVo.getCorptype())){//07会计方案
			// gvo.set
			// }
			// if(gl_cbconstant.getFangan_2013().equals(corpVo.getCorptype())){//13会计方案
			// gvo.set
			// }
		}
		return new SalaryAccSetVO();
	}

	@Override
	public SalaryAccSetVO save(SalaryAccSetVO vo) throws DZFWarpException {

		if (vo == null) {
			throw new BusinessException("数据为空,保存失败!");
		}

		Object[] objs = vo.getTableData3();

		Set<String> set = new HashSet<>();

		Map<String, String> map = SalaryTableGetter.getGzQyJt();
		for (Object o : objs) {
			SalarySetTableVO tablevo = JsonUtils.deserialize(JsonUtils.serialize(o), SalarySetTableVO.class);
			if (set.contains(tablevo.getKmsz())) {
				String value = map.get(tablevo.getKmsz());
				if(!StringUtil.isEmpty(value))
					throw new BusinessException("科目设置项[" + value.split("-")[0] + "]重复设置!");
			} else {
				set.add(tablevo.getKmsz());
			}
		}
		String pk_corp = vo.getPk_corp();
		SalaryAccSetVO vo1 = new SalaryAccSetVO();
		vo1 = SalaryTableGetter.setGzJt(vo1, vo.getTableData1());
		vo1 = SalaryTableGetter.setGzJt(vo1, vo.getTableData2());
		vo1 = SalaryTableGetter.setGzJt(vo1, vo.getTableData3());
		vo1.setPk_salaryaccset(vo.getPk_salaryaccset());
		vo1.setDr(0);
		vo1.setJtqybf(vo.getJtqybf());
		vo1.setPk_corp(vo.getPk_corp());
		vo1.setShowmore(vo.getShowmore());
		String[] fields = SalaryAccSetVO.getFileds();
		return saveVoByColumn(pk_corp, vo1, fields);
	}

	public SalaryAccSetVO saveGroupVO(String pk_corp) throws DZFWarpException {
		SalaryAccSetVO vo1 = queryGroupVO(pk_corp);
		if (vo1 == null)
			throw new BusinessException("没有预置工资科目");
		vo1.setJtqybf(DZFBoolean.FALSE);
		vo1.setShowmore(DZFBoolean.FALSE);
		String[] fields = SalaryAccSetVO.getFileds();
		return saveVoByColumn(pk_corp, vo1, fields);
	}

	public SalaryAccSetVO saveVoByColumn(String pk_corp, SalaryAccSetVO vo1, String[] strs) throws DZFWarpException {

		SalaryAccSetVO vo = query(pk_corp);

		if (StringUtil.isEmpty(vo.getPk_corp())) {
			vo.setPk_corp(pk_corp);
		}
		for (String str : strs) {
			vo.setAttributeValue(str, vo1.getAttributeValue(str));
		}
		vo = (SalaryAccSetVO) singleObjectBO.saveObject(pk_corp, vo);
		return vo;
	}

	@Override
	public SalaryAccSetVO queryGroupVO(String pk_corp) throws DZFWarpException {
		SalaryAccSetVO salaryTemp = null;
		CorpVO corpVO = corpService.queryByPk(pk_corp);
		String corpType = corpVO.getCorptype();
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		sp.addParam(corpType);
		YntCpaccountVO[] accvos =accService.queryByPk(pk_corp);
		Map<String, YntCpaccountVO> map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(accvos),
				new String[] { "pk_corp_account" });
		List<SalaryModelHVO> group_models = (List<SalaryModelHVO>) singleObjectBO.executeQuery(
				"pk_corp = ? and pk_trade_accountschema = ? and nvl(dr,0) = 0", sp,
				new Class[] { SalaryModelHVO.class, SalaryModelBVO.class });
		if (group_models != null && group_models.size() > 0) {
			salaryTemp = new SalaryAccSetVO();
			for (SalaryModelHVO salaryModelHVO : group_models) {
				List<SalaryModelBVO> bvos = Arrays.asList(salaryModelHVO.getChildren());
				for (SalaryModelBVO salaryModelBVO : bvos) {
					String corp_account = yntBoPubUtil.getCorpAccountPkByTradeAccountPkWithMsg(
							salaryModelBVO.getPk_accsubj(), pk_corp, "请到数据维护-标准科目节点，升级会计科目。");
					corp_account = getFisrtNextLeafAccount(map, corp_account, accvos);
					salaryModelBVO.setPk_accsubj(corp_account);
				}
				if (salaryModelHVO.getTemp_type() == 0) {
					for (SalaryModelBVO salaryModelBVO : bvos) {
						switch (salaryModelBVO.getZy()) {
						case "工资费用科目":
							salaryTemp.setJtgz_gzfykm(salaryModelBVO.getPk_accsubj());
							break;
						case "应付工资科目":
							salaryTemp.setJtgz_yfgzkm(salaryModelBVO.getPk_accsubj());
							break;
						case "应付社保科目":
							salaryTemp.setJtgz_yfsbkm(salaryModelBVO.getPk_accsubj());
							break;
						case "社保费用科目":
							salaryTemp.setJtgz_sbfykm(salaryModelBVO.getPk_accsubj());
							break;
						}
					}

				} else {
					for (SalaryModelBVO salaryModelBVO : bvos) {
						switch (salaryModelBVO.getZy()) {
						case "应付工资科目":
							salaryTemp.setFfgz_yfgzkm(salaryModelBVO.getPk_accsubj());
							break;
						case "公积金个人部分":
							salaryTemp.setFfgz_gjjgrbf(salaryModelBVO.getPk_accsubj());
							break;
						case "应缴个税科目":
							salaryTemp.setFfgz_grsds(salaryModelBVO.getPk_accsubj());
							break;
						case "工资发放科目":
							salaryTemp.setFfgz_xjlkm(salaryModelBVO.getPk_accsubj());
							break;
						case "个人养老保险":
							salaryTemp.setFfgz_sbgrbf(salaryModelBVO.getPk_accsubj());
							break;
						case "个人医疗保险":
							salaryTemp.setFfgz_yilbxbf(salaryModelBVO.getPk_accsubj());
							break;
						case "个人失业保险":
							salaryTemp.setFfgz_sybxbf(salaryModelBVO.getPk_accsubj());
							break;
						case "工伤保险科目":
							salaryTemp.setFfgz_gsbxkm(salaryModelBVO.getPk_accsubj());
							break;
						case "生育保险科目":
							salaryTemp.setFfgz_shybxkm(salaryModelBVO.getPk_accsubj());
							break;
						default:
							break;
						}
					}
				}
			}
		}
		return salaryTemp;

	}

	private String getFisrtNextLeafAccount(Map<String, YntCpaccountVO> map, String corp_account,
			YntCpaccountVO[] accvos) {
		YntCpaccountVO accvo = null;
		if (map != null && map.size() > 0) {
			accvo = map.get(corp_account);
			if (accvo != null) {
				if (accvo.getIsleaf() != null && accvo.getIsleaf().booleanValue()) {

				} else {
					// 获取最末级科目
					accvo = getFisrtNextLeafAccount(accvo.getAccountcode(), accvos);
					if (accvo != null) {
						corp_account = accvo.getPk_corp_account();
					}
				}
			}
		}
		return corp_account;
	}

	// 查询第一分支的最末级科目
	private YntCpaccountVO getFisrtNextLeafAccount(String accountcode, YntCpaccountVO[] accvos) {

		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();// 存储下级科目
		for (YntCpaccountVO accvo : accvos) {
			if (accvo.getIsleaf().booleanValue() && accvo.getAccountcode() != null
					&& accvo.getAccountcode().startsWith(accountcode)) {
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

	@Override
	public SalaryAccSetVO queryTable(String pk_corp) throws DZFWarpException {
		SalaryAccSetVO setvo1 = query(pk_corp);
		SalaryAccSetVO setvo = new SalaryAccSetVO();
		SalarySetTableVO[] tableData1 = SalaryTableGetter.getGzJt(pk_corp, setvo1, 1);
		setvo.setTableData1(tableData1);
		SalarySetTableVO[] tableData2 = SalaryTableGetter.getGzJt(pk_corp, setvo1, 2);
		setvo.setTableData2(tableData2);
		SalarySetTableVO[] tableData3 = SalaryTableGetter.getGzJt(pk_corp, setvo1, 3);
		tableData3 = filterData(tableData3);
		setvo.setTableData3(tableData3);
		setvo.setPk_corp(setvo1.getPk_corp());
		setvo.setJtqybf(setvo1.getJtqybf());
		setvo.setShowmore(setvo1.getShowmore());
		setvo.setPk_salaryaccset(setvo1.getPk_salaryaccset());
		return setvo;
	}

	private SalarySetTableVO[] filterData(SalarySetTableVO[] tableData3) {
		List<SalarySetTableVO> list = new ArrayList<>();
		if (tableData3 == null || tableData3.length == 0)
			return new SalarySetTableVO[0];

		int i = 1;
		for (SalarySetTableVO table : tableData3) {
			table.setXh(Integer.toString(i));
			if ("jtgz_sbfykm".equals(table.getKmsz()) || "jtgz_yfsbkm".equals(table.getKmsz())) {
				list.add(table);
			} else {
				if (!StringUtil.isEmpty(table.getKjkm())) {
					list.add(table);
				} else {
					continue;
				}
			}

			i++;
		}
		return list.toArray(new SalarySetTableVO[list.size()]);
	}

	@Override
	public SalaryKmDeptVO[] saveFykm(String pk_corp, SalaryKmDeptVO[] vos) throws DZFWarpException {

		List<AuxiliaryAccountBVO> deptlist = gl_fzhsserv.queryPerson(AuxiliaryConstant.ITEM_DEPARTMENT, pk_corp, null);
		Map<String, AuxiliaryAccountBVO> audeptmap = DZfcommonTools.hashlizeObjectByPk(deptlist,
				new String[] { "pk_auacount_b" });
		Set<String> set = new HashSet<>();
		for (SalaryKmDeptVO vo : vos) {

			if (StringUtil.isEmpty(vo.getCdeptid()) || StringUtil.isEmpty(vo.getCkjkmid())) {
				throw new BusinessException("部门和科目不能空!");
			}
			if (set.contains(vo.getCdeptid())) {
				AuxiliaryAccountBVO bvo = audeptmap.get(vo.getCdeptid());
				if (bvo == null) {
					throw new BusinessException("存在部门重复设置!");
				} else {
					throw new BusinessException("部门" + bvo.getName() + "重复设置!");
				}

			} else {
				set.add(vo.getCdeptid());
			}
			vo.setPk_corp(pk_corp);
			vo.setDr(0);
		}
		String delsqlb = "delete from ynt_salarykmdept where pk_corp = '" + pk_corp + "'";
		singleObjectBO.executeUpdate(delsqlb, null);
		singleObjectBO.insertVOArr(pk_corp, vos);
		return null;
	}

	@Override
	public SalaryKmDeptVO[] queryFykm(String pk_corp) throws DZFWarpException {

		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		String condition = " pk_corp = ? and nvl(dr,0) = 0 ";
		SalaryKmDeptVO[] pvos = (SalaryKmDeptVO[]) singleObjectBO.queryByCondition(SalaryKmDeptVO.class, condition,
				params);

		if (pvos == null || pvos.length == 0)
			return new SalaryKmDeptVO[0];
		Map map = accService.queryMapByPk(pk_corp);
		List<AuxiliaryAccountBVO> deptlist = gl_fzhsserv.queryPerson(AuxiliaryConstant.ITEM_DEPARTMENT, pk_corp, null);
		Map<String, AuxiliaryAccountBVO> audeptmap = DZfcommonTools.hashlizeObjectByPk(deptlist,
				new String[] { "pk_auacount_b" });
		for (SalaryKmDeptVO vo : pvos) {
			if (!StringUtil.isEmpty(vo.getCkjkmid())) {
				YntCpaccountVO accvo = (YntCpaccountVO) map.get(vo.getCkjkmid());
				if (accvo != null) {
					vo.setKjkmcode(accvo.getAccountcode());
					vo.setKjkmname(accvo.getAccountname());
				}
			}
			if (!StringUtil.isEmpty(vo.getCdeptid())) {
				AuxiliaryAccountBVO deptvo = audeptmap.get(vo.getCdeptid());
				if (deptvo != null) {
					vo.setVdeptname(deptvo.getName());
					vo.setVdeptcode(deptvo.getCode());
				}
			}
		}
		return pvos;
	}

}
