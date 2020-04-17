package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.framework.util.SQLHelper;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.PzmbbVO;
import com.dzf.zxkj.platform.model.bdset.PzmbhVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.IPzmbhService;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.util.ReportUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("pzmbhService")
public class PzmbhServiceImpl implements IPzmbhService {
	
	private SingleObjectBO singleObjectBO;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	@Autowired
	private IAccountService accountService;

	@Override
	public PzmbhVO save(PzmbhVO vo)  throws DZFWarpException {
		/*checkExist(vo);*/
		if (vo.getVtemplatename() != null && vo.getVtemplatename().length() > 50) {
			throw new BusinessException("名称不能超过50个字符");
		}
		PzmbhVO tvo = new PzmbhVO();
		BeanUtils.copyProperties(vo, tvo);
		List<PzmbhVO> list  = query(vo.getPk_corp());
		if (null != list) {
			for (PzmbhVO pzmbhVO : list) {
				if (vo.getVtemplatecode().equals(pzmbhVO.getVtemplatecode())
						&& !(pzmbhVO.getPk_corp_pztemplate_h().equals(vo.getPk_corp_pztemplate_h()))) {
					throw new BusinessException("模板编码重复！");
				}
			}
		}
		/*checkBeforeSave(tvo,true);*/
		/*if(!StringUtil.isEmpty(vo.getPk_corp_pztemplate_h())){
			update(vo);
			return vo;
		}*/
		setDefaultCodes(tvo);
		if (!StringUtil.isEmpty(tvo.getPk_corp_pztemplate_h())) {
			SQLParameter sp = new SQLParameter();
			sp.addParam(tvo.getPk_corp());
			sp.addParam(tvo.getPk_corp_pztemplate_h());
			singleObjectBO.executeUpdate("delete from ynt_cppztemmb_b where pk_corp = ? and pk_corp_pztemplate_h = ?", sp);
			
		}
		return (PzmbhVO)singleObjectBO.saveObject(vo.getPk_corp(), tvo);
	}

//	@Override
//	public void update(PzmbhVO vo) {
//		PzmbhVO tvo = new PzmbhVO();
//		BeanUtils.copyProperties(vo, tvo);
//		try {
//			/*checkBeforeSave(tvo,true);*/
//			setDefaultCodes(vo);
//			singleObjectBO.saveObject(tvo.getPk_corp(), tvo);
//		} catch (BusinessException e) {
//			//e.printStackTrace();
//			throw new RuntimeException();
//		}
//	}

	@Override
	public List<PzmbhVO> query(String pk_corp)  throws DZFWarpException  {
		List<PzmbhVO> listVo = new ArrayList<PzmbhVO>();
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		listVo = (List<PzmbhVO>)singleObjectBO.retrieveByClause(PzmbhVO.class,
				" pk_corp=? and nvl(dr,0)=0 order by vtemplatecode", sp);
		return listVo;
	}

	@Override
	public List<PzmbhVO> queryWithBody(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		String headSql = "select * from ynt_cppztemmb where pk_corp = ? and nvl(dr,0) = 0 order by vtemplatecode ";
		sp.addParam(pk_corp);
		List<PzmbhVO> headVos = (List<PzmbhVO>)singleObjectBO
				.executeQuery(headSql,sp, new BeanListProcessor(PzmbhVO.class));
		if (headVos.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(" select b.*,acc.accountcode||'_'|| acc.accountname subjname from ynt_cppztemmb_b b ")
					.append(" left join  ynt_cpaccount acc on acc.pk_corp_account = b.pk_accsubj ")
					.append(" where b.pk_corp_pztemplate_h in (")
					.append("select pk_corp_pztemplate_h from ynt_cppztemmb where pk_corp = ? and nvl(dr,0) = 0")
					.append(") and nvl(b.dr,0) = 0 order by pk_corp_pztemplate_b");
			List<PzmbbVO> bodyVos = (List<PzmbbVO>)singleObjectBO.executeQuery(sb.toString(),
					sp, new BeanListProcessor(PzmbbVO.class));
			Map<String, List<PzmbbVO>> bodyMap = DZfcommonTools
					.hashlizeObject(bodyVos, new String[] {"pk_corp_pztemplate_h"});

			for (PzmbhVO hvo: headVos) {
				List<PzmbbVO> bodys = bodyMap.get(hvo.getPk_corp_pztemplate_h());
				if (bodys != null) {
					hvo.setChildren(bodys.toArray(new PzmbbVO[0]));
				}
			}
		}
		return headVos;
	}

	@Override
	public List<PzmbhVO> queryAll(String pk_corp)  throws DZFWarpException  {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select b.*,acc.accountcode||'_'|| acc.accountname subjname from ynt_cppztemmb_b b ");
		sf.append(" join  ynt_cpaccount acc on acc.pk_corp_account = b.pk_accsubj ");
		sf.append(" where b.pk_corp = ? and nvl(b.dr,0) = 0 order by pk_corp_pztemplate_b");
		List<PzmbbVO> ancevos = (List<PzmbbVO>)singleObjectBO.executeQuery(sf.toString(), sp,new BeanListProcessor(PzmbbVO.class));
		List<PzmbhVO> listPzmbhVos = query(pk_corp);
		Map<String, PzmbhVO> tempMap = new HashMap<String, PzmbhVO>();
		for(PzmbhVO hvo:listPzmbhVos){
			tempMap.put(hvo.getPk_corp_pztemplate_h(), hvo);
		}
		Map<String, YntCpaccountVO> kmMap = accountService.queryMapByPk(pk_corp);
		Map<String, PzmbhVO> mapVo = new HashMap<String, PzmbhVO>();
		for(PzmbbVO bvo: ancevos){
			YntCpaccountVO accountVO = kmMap.get(bvo.getPk_accsubj());
			bvo.setVcode(accountVO == null ? "" : accountVO.getAccountcode());
			PzmbhVO tempVO = null;
			StringBuilder memo = new StringBuilder();
			if (mapVo.containsKey(bvo.getPk_corp_pztemplate_h())) {
				tempVO = mapVo.get(bvo.getPk_corp_pztemplate_h());
				memo.append(tempVO.getMemo());
			} else {
				tempVO = tempMap.get(bvo.getPk_corp_pztemplate_h());
				mapVo.put(tempVO.getPk_corp_pztemplate_h(), tempVO);
				memo.append(tempVO.getVtemplatecode())
				.append(" ")
				.append(tempVO.getVtemplatename())
				.append(" ");
			}
			memo.append(0 == bvo.getDirection() ? "借：" : "贷：")
			.append(bvo.getVcode())
			.append(" ")
			.append(bvo.getVname())
			.append(" ");
			tempVO.setMemo(memo.toString());
		}
		List<PzmbhVO> listVo = new ArrayList<PzmbhVO>();
		listVo.addAll(mapVo.values());
		Collections.sort(listVo, new Comparator<PzmbhVO>() {
			@Override
			public int compare(PzmbhVO o1, PzmbhVO o2) {
				return o1.getVtemplatecode().compareTo(o2.getVtemplatecode());
			}
		});
		return listVo;
	}

	@Override
	public List<PzmbbVO> queryB(String PId) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(PId);
		StringBuffer sf = new StringBuffer();
		sf.append(" select ynt_inventory.code as invcode, ynt_inventory.name as invname, ");
		sf.append(" taxitem.taxcode as taxcode,taxitem.taxname as taxname,taxitem.taxratio as taxratio, ");
		sf.append(" ynt_measure.code as meacode, ynt_measure.name as meaname, ");
		sf.append(" b.*,acc.accountcode||'_'|| acc.accountname subjname from ynt_cppztemmb_b b ");
		sf.append(" left join ynt_inventory ynt_inventory on ynt_inventory.pk_inventory = b.pk_inventory");
		sf.append(" left join ynt_measure ynt_measure on ynt_inventory.pk_measure = ynt_measure.pk_measure");
		sf.append(" left join ynt_taxitem taxitem on taxitem.pk_taxitem = b.pk_taxitem");
		sf.append(" join  ynt_cpaccount acc on acc.pk_corp_account = b.pk_accsubj ");
		sf.append(" where b.pk_corp_pztemplate_h = ? and nvl(b.dr,0) = 0 order by pk_corp_pztemplate_b");
		List<PzmbbVO> ancevos = (List<PzmbbVO>)singleObjectBO.executeQuery(sf.toString(), sp,new BeanListProcessor(PzmbbVO.class));
		if(ancevos == null || ancevos.size() == 0)
			return null;
		return ancevos;
	}
	
	@Override
	public void delete(String pid) throws DZFWarpException {
		singleObjectBO.deleteObjectByID(pid, new Class[]{PzmbhVO.class,PzmbbVO.class});
	}
	public void checkBeforeSave(PzmbhVO vo,boolean isadd) throws DZFWarpException{
		String vtemplatename = vo.getVtemplatename();
		String vtemplatecode = vo.getVtemplatecode();
		if(vtemplatename == null)
			throw new BusinessException("模板名称不能为空。");
		
		if(vtemplatecode == null)
			throw new BusinessException("模板编码不能为空。");
		
		if(isadd){
			List<PzmbhVO> list = queryBySql(vo.getPk_corp(),vtemplatecode,vo.getPk_corp_pztemplate_h());
			List<PzmbhVO> list1 = queryBySql1(vo.getPk_corp(),vtemplatename,vo.getPk_corp_pztemplate_h());
			if(StringUtil.isEmpty(vo.getPk_corp_pztemplate_h())) {  //增加
				if(list != null && list.size() > 0){
					throw new BusinessException("模板编码已经存在。");
				}
				if(list1 != null && list1.size() > 0){
					throw new BusinessException("模板名称已经存在。");
				}
			}else{   //修改
						/*throw new BusinessException("模板编码，模板名称已经存在。");*/
						if(list != null && list.size() > 0 ){
							throw new BusinessException("模板编码已经存在。");
						}
						if(list1 != null && list1.size() > 0){
							throw new BusinessException("模板名称已经存在。");
				}
				}
		}
	}
	
	public void setDefaultCodes(PzmbhVO vo){
		if(vo == null)
			return;
		PzmbbVO[] bodyvos = (PzmbbVO[])vo.getChildren();
		String where = " pk_corp =? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_corp());
		YntCpaccountVO[] cpvo = (YntCpaccountVO[])singleObjectBO.queryByCondition(YntCpaccountVO.class, where, sp);
		Map<String, YntCpaccountVO> map = hashlizeObject(cpvo);
		DZFDouble jf = DZFDouble.ZERO_DBL;
		DZFDouble df = DZFDouble.ZERO_DBL;
		if(bodyvos != null &&bodyvos.length>0){
			YntCpaccountVO cc = null;
			for(PzmbbVO v : bodyvos){
				cc = null;
				v.setPk_corp_pztemplate_b(null);
				cc = map.get(v.getPk_accsubj());
				v.setVcode(cc.getAccountcode());
				v.setVname(cc.getAccountname());
				if (v.getMny() != null) {
					if (v.getDirection() == 0) {
						jf = jf.add(v.getMny());
					} else if (v.getDirection() == 1) {
						df = df.add(v.getMny());
					}
				}
//				v.setFzhsx1(null);
//				v.setFzhsx2(null);
//				v.setFzhsx3(null);
//				v.setFzhsx4(null);
//				v.setFzhsx5(null);
//				v.setFzhsx6(null);
//				v.setFzhsx7(null);
//				v.setFzhsx8(null);
//				v.setFzhsx9(null);
//				v.setFzhsx10(null);
				
			}
			if (!jf.equals(df)) {
				throw new BusinessException("借贷金额不平！");
			}
		}
		vo.setTotalmny(df.doubleValue() != 0 ? df : null);
	}
	
	
	public  Map<String, YntCpaccountVO> hashlizeObject(YntCpaccountVO[] objs)  throws DZFWarpException {
		Map<String, YntCpaccountVO> result = new HashMap<String, YntCpaccountVO>();
		if (objs == null || objs.length == 0)
			return result;
		String key = null;
		for (int i = 0; i < objs.length; i++) {
			key = null;
			key = objs[i].getPk_corp_account();
			result.put(key, objs[i]);
		}
		return result;
	}
	/*public List<PzmbhVO> queryBySql2(String pk_corp,String vtemplatename,String vtemplatecode){
		String condition = " select * from ynt_cppztemmb where nvl(dr,0)=0 and pk_corp=? and vtemplatecode=? "
				+ "or vtemplatename=? ";
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(vtemplatecode);
		params.addParam(vtemplatename);
		List<PzmbhVO> list2 = (List<PzmbhVO>) singleObjectBO.executeQuery(condition, params, new BeanListProcessor(PzmbhVO.class));
		return list2;
	}*/
	public List<PzmbhVO> queryBySql(String pk_corp,String vtemplatecode,String updateobjId) throws DZFWarpException{
		StringBuffer condition = new StringBuffer();
		condition.append(" select * from ynt_cppztemmb where nvl(dr,0)=0 and pk_corp=? ")
		.append("and vtemplatecode=? and pk_corp_pztemplate_h <> ?");
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(vtemplatecode);
		params.addParam(updateobjId);
		return (List<PzmbhVO>) singleObjectBO.executeQuery(condition.toString(), params, new BeanListProcessor(PzmbhVO.class));
	}
	public List<PzmbhVO> queryBySql1(String pk_corp,String vtemplatename,String updateobjId) throws DZFWarpException{
		StringBuffer condition = new StringBuffer();
		condition.append(" select * from ynt_cppztemmb where nvl(dr,0)=0 and pk_corp=? ")
		.append("and vtemplatename=? and pk_corp_pztemplate_h <> ?");
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(vtemplatename);
		params.addParam(updateobjId);
		return (List<PzmbhVO>) singleObjectBO.executeQuery(condition.toString(), params, new BeanListProcessor(PzmbhVO.class));
	}
	public PzmbhVO queryById(String id) throws DZFWarpException{
		return (PzmbhVO)singleObjectBO.queryVOByID(id, PzmbhVO.class);
	}

	private List<PzmbhVO> queryHeadAndChildrenByIDs (List<String> ids, String pk_corp) {
		StringBuilder sb = new StringBuilder();
		sb.append(" pk_corp_pztemplate_h in ")
			.append(SQLHelper.getInSQL(ids))
			.append(" and pk_corp = ? and nvl(dr, 0) = 0");
		SQLParameter params = SQLHelper.getSQLParameter(ids);
		params.addParam(pk_corp);
		return (List<PzmbhVO>) singleObjectBO.executeQuery(sb.toString(),
				params, new Class[] {PzmbhVO.class, PzmbbVO.class});
	}
	
	@Override
	public String copy(String[] tmps, CorpVO[] corps, String login_corp) throws DZFWarpException {
		StringBuilder msg = new StringBuilder();
		List<String> ids = Arrays.asList(tmps);
		List<PzmbhVO> tmpList = queryHeadAndChildrenByIDs(ids, login_corp);
		SQLParameter sp = new SQLParameter();
		String checkCodeSql = "select pk_corp_pztemplate_h from ynt_cppztemmb"
				+ " where pk_corp = ? and vtemplatecode = ? and nvl(dr,0)=0 ";
//		String checkNameSql = "select pk_corp_pztemplate_h from ynt_cppztemmb"
//				+ " where pk_corp = ? and vtemplatename = ? and nvl(dr,0)=0 ";
		String errorKey = null;
		for (CorpVO corp : corps) {
			String corp_id = corp.getPk_corp();
			Map<String, String> errorMsg = new HashMap<String, String>();
			int successCount = 0;
			for (PzmbhVO tmp : tmpList) {
				sp.clearParams();
				sp.addParam(corp_id);
				sp.addParam(tmp.getVtemplatecode());
				String tmpCode = tmp.getVtemplatecode();
				if (singleObjectBO.isExists(corp_id, checkCodeSql, sp)) {
					errorKey = "目的账簿存着编码相同的凭证模板！";
					if (errorMsg.containsKey(errorKey))
						tmpCode = errorMsg.get(errorKey) + "、" + tmpCode;
					errorMsg.put(errorKey, tmpCode);
					continue;
				}
//				sp.clearParams();
//				sp.addParam(corp_id);
//				sp.addParam(tmp.getVtemplatename());
//				if (singleObjectBO.isExists(corp_id, checkNameSql, sp)) {
//					errorKey = "目的账簿存着名称相同的凭证模板！";
//					if (errorMsg.containsKey(errorKey))
//						tmpCode = errorMsg.get(errorKey) + "、" + tmpCode;
//					errorMsg.put(errorKey, tmpCode);
//					continue;
//				}
				tmp.setPk_corp(corp_id);
				tmp.setPk_corp_pztemplate_h(null);
				PzmbbVO[] bvos = (PzmbbVO[]) tmp.getChildren();
				for (PzmbbVO bvo : bvos) {
					String vcode = bvo.getVcode();
					if (StringUtil.isEmpty(vcode)) {
						String accSql = "select accountcode from ynt_cpaccount"
								+ " where pk_corp = ? and pk_corp_account=? and nvl(dr,0)=0 ";
						sp.clearParams();
						sp.addParam(login_corp);
						sp.addParam(bvo.getPk_accsubj());
						vcode = (String) singleObjectBO.executeQuery(accSql, sp, new ColumnProcessor());
						
					}
					YntCpaccountVO destAcc = matchAccountSubj(bvo.getVcode(), corp_id);
					if (destAcc == null) {
						errorKey = "目的账簿科目" + bvo.getVcode() + "不存在，请确认！";
						if (errorMsg.containsKey(errorKey))
							tmpCode = errorMsg.get(errorKey) + "、" + tmpCode;
						errorMsg.put(errorKey, tmpCode);
						tmp = null;
						break;
					}
					if (destAcc.getIsleaf() == null || !destAcc.getIsleaf().booleanValue()) {
						errorKey = "目的账簿科目" + bvo.getVcode() + "为非末级科目，请确认！";
						if (errorMsg.containsKey(errorKey))
							tmpCode = errorMsg.get(errorKey) + "、" + tmpCode;
						errorMsg.put(errorKey, tmpCode);
						tmp = null;
						break;
					}
					bvo.setVcode(destAcc.getAccountcode());
					bvo.setPk_accsubj(destAcc.getPk_corp_account());
					bvo.setPk_corp(corp_id);
					bvo.setPk_taxitem(null);
					bvo.setPk_corp_pztemplate_b(null);
					bvo.setPk_corp_pztemplate_h(null);
					bvo.setFzhsx1(null);
					bvo.setFzhsx2(null);
					bvo.setFzhsx3(null);
					bvo.setFzhsx4(null);
					bvo.setFzhsx5(null);
					bvo.setFzhsx6(null);
					bvo.setFzhsx7(null);
					bvo.setFzhsx8(null);
					bvo.setFzhsx9(null);
					bvo.setFzhsx10(null);
				}
				if (tmp != null) {
					singleObjectBO.saveObject(corp_id, tmp);
					successCount++;
				}
			}
			msg.append(corp.getUnitname()).append("，").append("成功复制").append(successCount).append("条");
			if (errorMsg.size() > 0) {
				msg.append("，失败").append(tmpList.size() - successCount).append("条！")
				.append("失败原因：");
				for (String error : errorMsg.keySet()) {
					msg.append(error).append(errorMsg.get(error)).append("；");
				}
			}
			msg.append("<br>");
		}
		return msg.toString();
	}

	@Override
	public String getNewCode(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String maxCode = (String) singleObjectBO.executeQuery("select * from( select vtemplatecode" +
				" from ynt_cppztemmb where pk_corp = ? and nvl(dr,0)=0 " +
				"and REGEXP_LIKE(vtemplatecode,'^[0-9]+$') " +
				"order by to_number(vtemplatecode) desc) where rownum = 1", sp, new ColumnProcessor());
		String newCode = ReportUtil.getNextCode(maxCode);
		return newCode;
	}

	private YntCpaccountVO matchAccountSubj (String accCode, String desCorp) {
		YntCpaccountVO acc = null;
		String sql = "select pk_corp_account, accountcode, accountname, isleaf from ynt_cpaccount"
				+ " where pk_corp = ? and accountcode like ? and isleaf = 'Y' and nvl(dr,0)=0 order by accountcode";
		SQLParameter sp = new SQLParameter();
		sp.addParam(desCorp);
		sp.addParam(accCode.substring(0, 4) + "%");
		List<YntCpaccountVO> rs = (List<YntCpaccountVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(YntCpaccountVO.class));
		if (rs != null && rs.size() > 0) {
			int codeLen = accCode.length();
			for (YntCpaccountVO yntCpaccountVO : rs) {
				String lcode = yntCpaccountVO.getAccountcode();
				int lcodeLen = lcode.length();
				if (lcodeLen >= codeLen && lcode.indexOf(accCode) == 0
						|| lcodeLen < codeLen && accCode.indexOf(lcode) == 0) {
					// 上级或第一个下级
					acc = yntCpaccountVO;
					break;
				}
			}
		}
		return acc;
	}


}