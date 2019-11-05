package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.CodeName;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.util.Kmschema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 启用进销存，，存货转下级
 * 存货，科目下级转辅助工具类
 *
 */
@Component("kmzfz_SaveCpaccount")
public class SaveCpaccount {

	@Autowired
	private IYntBoPubUtil yntBoPubUtil;
	@Autowired
	private SingleObjectBO singleObjectBO;

	//保存开始
	public void save(YntCpaccountVO[] vos, YntCpaccountVO vo, CodeName fzcodename, CorpVO cpvo, String pk_fz, String userid){
		String pk_corp = cpvo.getPk_corp();
		Map<String, InventoryVO> map = buildjzInventory(vos,vo,pk_corp);
		String measurename = getMeasurename(vos);
		saveAcctoFZ(map,cpvo,fzcodename,vo,userid,measurename);
	}
	
	private Map<String,InventoryVO> buildjzInventory(YntCpaccountVO[] vos,YntCpaccountVO vo,String pk_corp)throws BusinessException {
		if(vos == null || vos.length == 0)
			return null;
		//查询当前存货
		InventoryVO[] fzhsbvos = queryInventoryVOs(pk_corp,vo.getPk_corp_account());
		Map<String,InventoryVO> map = null;
		if(fzhsbvos!=null && fzhsbvos.length>0){
			map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(fzhsbvos), new String[]{"name"});
		}
		Map<String,InventoryVO> mapkmfz = new HashMap<String,InventoryVO>();
		String code = yntBoPubUtil.getInventoryCode(pk_corp);
		for(YntCpaccountVO vv : vos){
			InventoryVO bvo = null;
			if(map!=null && map.containsKey(vv.getAccountname())){
				bvo = map.get(vv.getAccountname());
			}else{
				bvo = buildInventoryVO(vv,code,vo.getPk_corp_account());
				//code自增
				code = getFinalcode(Long.valueOf(code)+1);
			}
			mapkmfz.put(vv.getPk_corp_account(), bvo);
		}
		return mapkmfz;
	}
	
	//记录pk与辅助的关系
	private void saveAcctoFZ(Map<String,InventoryVO> map, CorpVO cpvo,
							 CodeName fzcodename, YntCpaccountVO vo, String userid, String measurename){
		String pk_corp = cpvo.getPk_corp();
		//新增辅助
		insertInvinfo(map,pk_corp);
		//更新库存单据
		updateInvIC(vo, pk_corp, map);
		//更新科目
		updateKMInfo(cpvo,vo,fzcodename,measurename);
		//更新凭证
		updateInvPzinfo(fzcodename,vo,pk_corp,map);
		//更新期初
		updateKmQcInvinfo(fzcodename,vo,pk_corp,map,userid);
	}
	
	private void insertInvinfo(Map<String,InventoryVO> map,String pk_corp){
		List<InventoryVO> listaddfz = new ArrayList<InventoryVO>();
		for(InventoryVO bvo : map.values()){
			if(StringUtil.isEmpty(bvo.getPk_inventory())){
				listaddfz.add(bvo);
			}
		}
		singleObjectBO.insertVOArr(pk_corp, listaddfz.toArray(new InventoryVO[0]));
	}
	
	private void updateKMInfo(CorpVO cpvo,YntCpaccountVO vo,CodeName fzcodename,String measurename){
		String pk_corp = cpvo.getPk_corp();
		String corptype = cpvo.getCorptype();
		String kmcode = vo.getAccountcode();
		SQLParameter sp = new SQLParameter();
		String sql =" update  ynt_cpaccount set dr = 1 where pk_corp = ? and accountcode like ? and accountcode <> ? ";
		sp.addParam(pk_corp);
		sp.addParam(kmcode+"%");
		sp.addParam(kmcode);
		singleObjectBO.executeUpdate(sql, sp);
		//
		sp.clearParams();
		//启用数量辅助核算
		if(Kmschema.isYclbm(corptype,kmcode)//原材料
				|| Kmschema.isKcspbm(corptype,kmcode)//库存商品
				|| Kmschema.isshouru(corptype,kmcode)){//收入类科目 
			sql = " update ynt_cpaccount  set measurename = ? ,isnum = ?,isleaf = ? , isfzhs = ? where pk_corp = ? and accountcode = ? and nvl(dr,0)=0  ";
			sp.addParam(measurename);
			sp.addParam("Y");
		}else{
			sql = " update ynt_cpaccount  set isleaf = ? , isfzhs = ? where pk_corp = ? and accountcode = ? and nvl(dr,0)=0  ";
		}
		sp.addParam("Y");
		sp.addParam(getKMfzstatus(fzcodename));
		sp.addParam(pk_corp);
		sp.addParam(kmcode);
		singleObjectBO.executeUpdate(sql, sp);
	}
	
	private void updateInvIC(YntCpaccountVO vo,String pk_corp,Map<String,InventoryVO> map){
		List<SQLParameter> list = new ArrayList<SQLParameter>();
		for(String key:map.keySet()){
			InventoryVO bvo = map.get(key);
			if(bvo == null)
				continue;
			SQLParameter sp = new SQLParameter();
			sp.addParam(vo.getPk_corp_account());
			sp.addParam(pk_corp);
			sp.addParam(key);
			list.add(sp);
		}
		
		//库存期初
		String sqlUpdate = " update ynt_icbalance set pk_subject = ? where pk_corp=? and pk_subject=?";
		singleObjectBO.executeBatchUpdate(sqlUpdate, list.toArray(new SQLParameter[0]));
		//存货
		sqlUpdate = " update ynt_inventory set pk_subject = ? where pk_corp=? and pk_subject=?";
		singleObjectBO.executeBatchUpdate(sqlUpdate, list.toArray(new SQLParameter[0]));
		//入库单
		sqlUpdate = " update ynt_ictradein set pk_subject = ? where pk_corp=? and pk_subject=?";
		singleObjectBO.executeBatchUpdate(sqlUpdate, list.toArray(new SQLParameter[0]));
		//出库单
		sqlUpdate = " update ynt_ictradeout set pk_subject = ? where pk_corp=? and pk_subject=?";
		singleObjectBO.executeBatchUpdate(sqlUpdate, list.toArray(new SQLParameter[0]));
	}
	
	private void updateInvPzinfo(CodeName fzcodename,YntCpaccountVO vo,String pk_corp,Map<String,InventoryVO> map){
		StringBuffer sf = new StringBuffer();
		sf.append(" update ynt_tzpz_b set pk_accsubj=? , kmmchie=? , vcode=? ,  ");
		sf.append(" vname=? , subj_code=? , subj_name=? ,  ");
		sf.append(" pk_inventory = ? ");
		sf.append(" where pk_corp = ? and  pk_accsubj = ?   ");
		List<SQLParameter> list = new ArrayList<SQLParameter>();
		for(String key:map.keySet()){
			InventoryVO bvo = map.get(key);
			if(bvo == null)
				continue;
			SQLParameter sp = new SQLParameter();
			sp.addParam(vo.getPk_corp_account());
			sp.addParam(vo.getFullname());
			sp.addParam(vo.getAccountcode());
			sp.addParam(vo.getAccountname());
			sp.addParam(vo.getAccountcode());
			sp.addParam(vo.getAccountname());
			sp.addParam(bvo.getPk_inventory());
			sp.addParam(pk_corp);
			sp.addParam(key);
			list.add(sp);
		}
		singleObjectBO.executeBatchUpdate(sf.toString(), list.toArray(new SQLParameter[0]));
	}
	
	private void updateKmQcInvinfo(CodeName fzcodename,YntCpaccountVO vo,String pk_corp,Map<String,InventoryVO> map,String userid){
		//查询期初内容
		QcYeVO[] qcvos = queryQcYeVOs(pk_corp,vo.getAccountcode());
		//组装辅助期初内容
		List<FzhsqcVO> qclist = buildFZqcVOs(qcvos,pk_corp,map,vo,fzcodename,userid);
		//保存辅助期初内容
		if(qclist!=null&& qclist.size()>0){
			singleObjectBO.insertVOArr(pk_corp, qclist.toArray(new FzhsqcVO[0]));
		}
		//删除原有期初内容
		SQLParameter sp = new SQLParameter();
		String sql =" delete from ynt_qcye where pk_corp = ? and vcode like ? and vcode <> ?  ";
		sp.addParam(pk_corp);
		sp.addParam(vo.getAccountcode()+"%");
		sp.addParam(vo.getAccountcode());
		singleObjectBO.executeUpdate(sql, sp);
	}
	
	private QcYeVO[] queryQcYeVOs(String pk_corp,String vcode){
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(vcode+"%");
		sp.addParam(vcode);
		String where = "  pk_corp = ? and vcode like ? and vcode <> ? and nvl(dr,0) = 0  ";
		QcYeVO[] vos = (QcYeVO[])singleObjectBO.queryByCondition(QcYeVO.class, where, sp);
		return vos;
	}
	
	private List<FzhsqcVO> buildFZqcVOs(QcYeVO[] qcvos,String pk_corp,Map<String,InventoryVO> map,YntCpaccountVO cpvo,CodeName fzcodename,String userid){
		if(qcvos == null || qcvos.length == 0)
			return null;
		Map<String, QcYeVO> mapqc = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(qcvos), new String[]{"pk_accsubj"});
		List<FzhsqcVO> list = new ArrayList<FzhsqcVO>();
		for(String key : map.keySet()){
			QcYeVO vo = mapqc.get(key);
			InventoryVO bvo = map.get(key);
			FzhsqcVO qcvo = buildFzhsqcVO(vo,pk_corp,bvo,cpvo,userid);
			if(qcvo != null){
				list.add(qcvo);
			}
		}
		return list;
	}
	
	private InventoryVO[] queryInventoryVOs(String pk_corp,String pk_subject){
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_subject);
		String where = "  pk_corp = ? and pk_subject = ? and nvl(dr,0)=0  ";
		InventoryVO[] vos = (InventoryVO[])singleObjectBO.queryByCondition(InventoryVO.class, where, sp);
		return vos;
	}
	
	private InventoryVO buildInventoryVO(YntCpaccountVO vv,String code,String pk_subject){
		InventoryVO bvo = new InventoryVO();
		bvo.setCode(code);
		bvo.setName(vv.getAccountname());
		bvo.setPk_subject(pk_subject);
		bvo.setPk_corp(vv.getPk_corp());
		return bvo;
	}
	
	private String getKMfzstatus(CodeName fzcodename){
		StringBuffer sf = new StringBuffer();
		for(int i=1;i<=10;i++){
			if(String.valueOf(i).equals(fzcodename.getCode())){
				sf.append("1");
			}else{
				sf.append("0");
			}
		}
		return sf.toString();
	}
	
	private String getFinalcode(long code){
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
	
	private FzhsqcVO buildFzhsqcVO(QcYeVO qcye,String pk_corp,InventoryVO bvo,YntCpaccountVO cpvo,String userid){
		if(qcye == null || bvo == null)
			return null;
		FzhsqcVO qcvo = new FzhsqcVO();
		qcvo.setPk_corp(pk_corp);
		qcvo.setDoperatedate(qcye.getDoperatedate());
		qcvo.setCoperatorid(userid);
		qcvo.setPk_accsubj(cpvo.getPk_corp_account());
		qcvo.setVcode(cpvo.getAccountcode()+"_"+bvo.getCode());
		qcvo.setVname(cpvo.getAccountname()+"_"+bvo.getName());
		qcvo.setThismonthqc(qcye.getThismonthqc());
		qcvo.setYbthismonthqc(qcye.getYbthismonthqc());
		qcvo.setYearjffse(qcye.getYearjffse());
		qcvo.setYbyearjffse(qcye.getYbyearjffse());
		qcvo.setYeardffse(qcye.getYeardffse());
		qcvo.setYbyeardffse(qcye.getYbyeardffse());
		qcvo.setYearqc(qcye.getYearqc());
		qcvo.setYbyearqc(qcye.getYbyearqc());
		qcvo.setDirect(qcye.getDirect());
		qcvo.setVlevel(qcye.getVlevel());
		qcvo.setNrate(qcye.getNrate());
		qcvo.setPk_currency(qcye.getPk_currency());
		qcvo.setPeriod(qcye.getPeriod());
		qcvo.setVyear(qcye.getVyear());
		qcvo.setBnqcnum(qcye.getBnqcnum());
		qcvo.setBnfsnum(qcye.getBnfsnum());
		qcvo.setBndffsnum(qcye.getBndffsnum());
		qcvo.setMonthqmnum(qcye.getMonthqmnum());
		qcvo.setFzhsx6(bvo.getPk_inventory());
		return qcvo;
	}
	
	private String getMeasurename(YntCpaccountVO[] vos){
		if(vos == null || vos.length == 0)
			return null;
		String measure = null;
		for(YntCpaccountVO vv : vos){
			if(!StringUtil.isEmpty(vv.getMeasurename())){
				measure = vv.getMeasurename();
				break;
			}
		}
		return measure;
	}
}