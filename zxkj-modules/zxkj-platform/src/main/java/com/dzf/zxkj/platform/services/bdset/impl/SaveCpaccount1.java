package com.dzf.zxkj.platform.services.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.constant.InventoryConstant;
import com.dzf.zxkj.common.enums.SalaryTypeEnum;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.CodeName;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InventorySetVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.icset.IInventoryAccSetService;
import com.dzf.zxkj.platform.services.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.util.Kmschema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 除存货外，科目下级转辅助工具类
 *
 */
@Component("kmzfz_SaveCpaccount1")
public class SaveCpaccount1 {

	@Autowired
	private IYntBoPubUtil yntBoPubUtil;
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IInventoryAccSetService gl_ic_invtorysetserv = null;
	
	//保存开始
	public void save(YntCpaccountVO[] vos, YntCpaccountVO vo, CodeName fzcodename, CorpVO cpvo, String pk_fz, String userid){
		Map<String, AuxiliaryAccountBVO> map = buildjzFZ(vos,vo,cpvo,pk_fz);
		String measurename = getMeasurename(vos);
		saveAcctoFZ(map,cpvo,fzcodename,vo,userid,measurename);
	}
	
	private Map<String,AuxiliaryAccountBVO> buildjzFZ(YntCpaccountVO[] vos,YntCpaccountVO parentvo,CorpVO cpvo,String pk_fz)throws BusinessException {
		if(vos == null || vos.length == 0)
			return null;
		String pk_corp = cpvo.getPk_corp();
		//查询当前辅助项目
		AuxiliaryAccountBVO[] fzhsbvos = queryFZHsBVOs(pk_corp,pk_fz);
		Map<String,AuxiliaryAccountBVO> map = null;
		if(fzhsbvos!=null && fzhsbvos.length>0){
			map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(fzhsbvos), new String[]{"name"});
		}
		Map<String,AuxiliaryAccountBVO> mapkmfz = new HashMap<String,AuxiliaryAccountBVO>();
		String code = yntBoPubUtil.getFZHsCode(pk_corp, pk_fz);
		//取得商品销售收入科目。
		YntCpaccountVO shourukmvo = queryShouruKmVO(pk_corp);
		for(YntCpaccountVO vv : vos){
			AuxiliaryAccountBVO bvo = null;
			if(map!=null && map.containsKey(vv.getAccountname())){
				bvo = map.get(vv.getAccountname());
			}else{
				bvo = buildABVO(vv,code,pk_fz,cpvo,parentvo,shourukmvo);
				//code自增
				code = getFinalcode(Long.valueOf(code)+1);
			}
			mapkmfz.put(vv.getPk_corp_account(), bvo);
		}
		return mapkmfz;
	}

	private YntCpaccountVO queryShouruKmVO(String pk_corp){
		SQLParameter sp = new SQLParameter();
		sp.addParam("商品销售收入");
		sp.addParam(pk_corp);
		sp.addParam("Y");
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_cpaccount aa where aa.accountname = ? and aa.pk_corp = ? and isleaf = ? and nvl(dr,0) = 0 ");
		YntCpaccountVO vo = (YntCpaccountVO)singleObjectBO.executeQuery(sf.toString(), sp, new BeanProcessor(YntCpaccountVO.class));
		return vo;
	}
	
	private AuxiliaryAccountBVO[] queryFZHsBVOs(String pk_corp,String pk_fz){
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_fz);
		String where = "  pk_corp = ? and pk_auacount_h = ? and nvl(dr,0)=0  ";
		AuxiliaryAccountBVO[] vos = (AuxiliaryAccountBVO[])singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class, where, sp);
		return vos;
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
	
	private AuxiliaryAccountBVO buildABVO(YntCpaccountVO vv,String code,String pk_fz,CorpVO corpVo,YntCpaccountVO parentvo,YntCpaccountVO shourukmvo)
			throws BusinessException{
		AuxiliaryAccountBVO bvo = new AuxiliaryAccountBVO();
		bvo.setCode(code);
		bvo.setName(vv.getAccountname());
		bvo.setPk_auacount_h(pk_fz);
		bvo.setPk_corp(vv.getPk_corp());
		bvo.setUnit(vv.getMeasurename());//计量单位
		if(AuxiliaryConstant.ITEM_STAFF.equals(pk_fz)){
			bvo.setBilltype(SalaryTypeEnum.NONORMAL.getValue());
		}else if(AuxiliaryConstant.ITEM_INVENTORY.equals(pk_fz)//启用存货
				&& isallowUpdatekmclassify(corpVo,parentvo)){
			bvo.setKmclassify(parentvo.getPk_corp_account());//分类
			if(shourukmvo!=null){
				bvo.setChukukmid(shourukmvo.getPk_corp_account());//出库科目
			}
		}
		return bvo;
	}
	
	private boolean isallowUpdatekmclassify(CorpVO corpVo,YntCpaccountVO parentvo) throws BusinessException{
		boolean falg = false;
		String corptype = parentvo.getPk_corp_accountschema();
		String kmcode = parentvo.getAccountcode();
		if(parentvo.getAccountlevel()!= null 
			&& parentvo.getAccountlevel() == 2 //2级科目的下级科目，转下级。
			&& IcCostStyle.IC_INVTENTORY.equals(corpVo.getBbuildic())	//总账存货
			&& (Kmschema.isYclbm(corptype,kmcode)|| Kmschema.isKcspbm(corptype,kmcode))){///原材料/库存商品
				InventorySetVO vo = gl_ic_invtorysetserv.query(corpVo.getPk_corp());
				if(vo != null){
					int	chcbjzfs = vo.getChcbjzfs();
					if(chcbjzfs == InventoryConstant.IC_CHDLHS){//存货大类
						falg = true;
					}
				}else{
					String kmname = parentvo.getAccountname();
					throw new BusinessException("当前公司启用总账存货，科目["+kmcode+"_"+kmname+"]下级转辅助失败，请先进行存货设置");
				}
		}
		return falg;
	}
	
	//记录pk与辅助的关系
	private void saveAcctoFZ(Map<String,AuxiliaryAccountBVO> map,CorpVO cpvo,
			CodeName fzcodename,YntCpaccountVO vo,String userid,String measurename){
		String pk_corp = cpvo.getPk_corp();
		//新增辅助
		insertFzinfo(map,pk_corp);
		//更新科目
		updateKMInfo(cpvo,vo,fzcodename,measurename);
		//更新凭证
		updatePzinfo(fzcodename,vo,pk_corp,map);
		//更新期初
		updateKmQcinfo(fzcodename,vo,pk_corp,map,userid);
	}
	
	private void insertFzinfo(Map<String,AuxiliaryAccountBVO> map,String pk_corp){
		List<AuxiliaryAccountBVO> listaddfz = new ArrayList<AuxiliaryAccountBVO>();
		for(AuxiliaryAccountBVO bvo : map.values()){
			if(StringUtil.isEmpty(bvo.getPk_auacount_b())){
				listaddfz.add(bvo);
			}
		}
		singleObjectBO.insertVOArr(pk_corp, listaddfz.toArray(new AuxiliaryAccountBVO[0]));
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
	
	private void updatePzinfo(CodeName fzcodename,YntCpaccountVO vo,String pk_corp,Map<String,AuxiliaryAccountBVO> map){
		String name = getKMfzName(fzcodename);
		if("fzhsx".equals(name))
			return;
		StringBuffer sf = new StringBuffer();
		sf.append(" update ynt_tzpz_b set pk_accsubj=? , kmmchie=? , vcode=? ,  ");
		sf.append(" vname=? , subj_code=? , subj_name=? ,  ");
		sf.append(name+"=? ");
		sf.append(" where pk_corp = ? and  pk_accsubj = ?   ");
		List<SQLParameter> list = new ArrayList<SQLParameter>();
		for(String key:map.keySet()){
			AuxiliaryAccountBVO bvo = map.get(key);
			if(bvo == null)
				continue;
			SQLParameter sp = new SQLParameter();
			sp.addParam(vo.getPk_corp_account());
			sp.addParam(vo.getFullname());
			sp.addParam(vo.getAccountcode());
			sp.addParam(vo.getAccountname());
			sp.addParam(vo.getAccountcode());
			sp.addParam(vo.getAccountname());
			sp.addParam(bvo.getPk_auacount_b());
			sp.addParam(pk_corp);
			sp.addParam(key);
			list.add(sp);
		}
		singleObjectBO.executeBatchUpdate(sf.toString(), list.toArray(new SQLParameter[0]));
	}
	
	private void updateKmQcinfo(CodeName fzcodename,YntCpaccountVO vo,String pk_corp,Map<String,AuxiliaryAccountBVO> map,String userid){
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
	
	private String getKMfzName(CodeName fzcodename){
		StringBuffer sf = new StringBuffer();
		sf.append("fzhsx");
		for(int i=1;i<=10;i++){
			if(String.valueOf(i).equals(fzcodename.getCode())){
				sf.append(String.valueOf(i));
			}
		}
		return sf.toString();
	}
	
	private String getKMfzName2(CodeName fzcodename){
		String res = "";
		for(int i=1;i<=10;i++){
			if(String.valueOf(i).equals(fzcodename.getCode())){
				res =String.valueOf(i);
			}
		}
		return res;
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
	
	private List<FzhsqcVO> buildFZqcVOs(QcYeVO[] qcvos,String pk_corp,Map<String,AuxiliaryAccountBVO> map,YntCpaccountVO cpvo,CodeName fzcodename,String userid){
		if(qcvos == null || qcvos.length == 0)
			return null;
		Map<String, QcYeVO> mapqc = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(qcvos), new String[]{"pk_accsubj"});
		String suffix = getKMfzName2(fzcodename);
		List<FzhsqcVO> list = new ArrayList<FzhsqcVO>();
		for(String key : map.keySet()){
			QcYeVO vo = mapqc.get(key);
			AuxiliaryAccountBVO bvo = map.get(key);
			FzhsqcVO qcvo = buildFzhsqcVO(vo,pk_corp,bvo,cpvo,userid,suffix);
			if(qcvo != null){
				list.add(qcvo);
			}
		}
		return list;
	}
	
	private FzhsqcVO buildFzhsqcVO(QcYeVO qcye,String pk_corp,AuxiliaryAccountBVO bvo,YntCpaccountVO cpvo,String userid,String suffix){
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
		//
		try{
			if(!StringUtil.isEmpty(suffix)){
				Class clazz = qcvo.getClass();
				Method m2 = clazz.getDeclaredMethod("setFzhsx"+suffix, String.class);
				m2.invoke(qcvo, bvo.getPk_auacount_b());
			}
		}catch(Exception e){
			//吃掉异常
		}
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