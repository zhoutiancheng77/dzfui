package com.dzf.zxkj.platform.service.glic.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.InventoryConstant;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.util.Kmschema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

@Service("gl_ic_invtorysetserv")
public class InventoryAccSetServiceImpl implements IInventoryAccSetService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private CheckInventorySet inventory_setcheck;
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private IAccountService accountService;

	@Override
	public InventorySetVO query(String pk_corp) throws DZFWarpException {
		InventorySetVO body = null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		InventorySetVO[] bodyvos = (InventorySetVO[])
				singleObjectBO.queryByCondition(InventorySetVO.class, " nvl(dr,0) = 0 and pk_corp = ? ", sp);
		if(bodyvos != null && bodyvos.length>0){
			body = bodyvos[0];
		}else{
			body = new InventorySetVO();
//			body.setChcbjzfs(InventoryConstant.IC_FZMXHS);
		}
		return body;
	}

	@Override
	public InventorySetVO save(String userid,String pk_corp,InventorySetVO vo1,boolean ischeck) throws DZFWarpException {
		if(vo1 == null){
			throw new BusinessException("保存数据为空，保存失败！");
		}
		if(vo1.getChcbjzfs() == -1){
			throw new BusinessException("存货成本核算方式设置为空！");
		}
//		if(DZFValueCheck.isEmpty(vo1.getPk_corp())){
		vo1.setPk_corp(pk_corp);
//		}
		//如果成本结转方式发生了变化，进行校验
		InventorySetVO orignvo = query(pk_corp);
		if(orignvo == null){
			orignvo = new InventorySetVO();
		}
		if(ischeck && vo1.getChcbjzfs() != orignvo.getChcbjzfs()){
			String errorinfo = inventory_setcheck.checkInventorySet(userid, pk_corp, vo1);
			if(!StringUtil.isEmpty(errorinfo)){
				 vo1.setErrorinfo(errorinfo);
				 return vo1;
			}
		}
		//
		YntCpaccountVO kmvo = (YntCpaccountVO)singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, vo1.getZgrkdfkm());
		if(kmvo==null ){
			throw new BusinessException("暂估入库贷方科目不存在！");
		}
		if(!StringUtil.isEmpty(vo1.getZgkhfz())){
			if(StringUtil.isEmpty(kmvo.getIsfzhs())
				|| !"1".equals(String.valueOf(kmvo.getIsfzhs().charAt(1)))){//供应商辅助
				vo1.setZgkhfz(null);
			}
		}else{
			if(!StringUtil.isEmpty(kmvo.getIsfzhs())
					&& "1".equals(String.valueOf(kmvo.getIsfzhs().charAt(1)))){//供应商辅助
				throw new BusinessException("暂估入库贷方科目已经启用供应商辅助，请设置供应商辅助！如果界面没有设置供应商辅助选项，请重新打开该节点进行操作");
			}
		}
		//自动清空存货大类信息
		if(vo1.getChcbjzfs()  != InventoryConstant.IC_CHDLHS){
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam("000001000000000000000006");
			singleObjectBO.executeUpdate(" update  ynt_fzhs_b set kmclassify = null  where pk_corp = ? and nvl(dr,0) = 0 and pk_auacount_h = ?  ", sp);
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo1.getPk_corp());
		singleObjectBO.executeUpdate(" delete from ynt_glicset where pk_corp = ?  ", sp);
		vo1.setPk_glicset(null);
		singleObjectBO.saveObject(vo1.getPk_corp(), vo1);
		
		buildLogInfo(orignvo, vo1);
		
		return vo1;
	}
	
	private void buildLogInfo(InventorySetVO oldvo, InventorySetVO newvo){
		StringBuffer msg = new StringBuffer();
		//第一步
		int oldjzfs = oldvo.getChcbjzfs();
		int newjzfs = newvo.getChcbjzfs();
		if(newjzfs != oldjzfs){
			msg.append("存货成本核算方式修改为");
			if(newjzfs == InventoryConstant.IC_FZMXHS){
				msg.append("“辅助明细”；");
			}else if(newjzfs == InventoryConstant.IC_CHDLHS){
				msg.append("“存货大类”；");
			}else if(newjzfs == InventoryConstant.IC_NO_MXHS){
				msg.append("“不核算明细”；");
			}
		}
		
		int oldjscgz= oldvo.getChppjscgz();
		int newjscgz= newvo.getChppjscgz();
		if(newjscgz != oldjscgz){
			msg.append("存货匹配规则修改为");
			if(newjscgz == InventoryConstant.IC_RULE_0){
				msg.append("“存货名称+规格（型号）+计量单位”；");
			}else if(newjscgz == InventoryConstant.IC_RULE_1){
				msg.append("“存货名称+计量单位”；");
			}
		}
		
		//第二步
		String[][] fields = {{"zgrkdfkm", "暂估入库贷方科目"},  {"zgkhfz", "供应商辅助核算"}, 
				{"kcsprkkm", "入库科目"}, {"kcspckkm", "出库科目"},
				{"jxshuiekm", "进项税额"}, {"xxshuiekm", "销项税额"},
				{"yingshoukm", "应收"}, {"yingfukm", "应付"}, 
				{"yinhangkm", "银行"},  {"xianjinkm", "现金"}};
		
		String oldvalue;
		String newvalue;
		String info = "";
		for(String[] s : fields){
			oldvalue = (String) oldvo.getAttributeValue(s[0]);
			newvalue = (String) newvo.getAttributeValue(s[0]);
			if(!comparevalue(oldvalue, newvalue)){
				info += "“" + s[1] + "”、";
			}
		}
		
		if(!StringUtil.isEmpty(info)){
			msg.append("修改了").append(info);
		}
		
		if(msg.length() > 0){
			newvo.setLoginfo(msg.substring(0, msg.length()-1));
		}
	}
	
	private boolean comparevalue(String o1, String o2){
		if(StringUtil.isEmpty(o1)){
			o1 = "";
		}
		if(StringUtil.isEmpty(o2)){
			o2 = "";
		}
		
		return o1.equals(o2);
	}

	@Override
	public InventorySetVO saveDefaultValue(String userid, CorpVO cpvo,boolean isQy) throws DZFWarpException {
		String pk_corp = cpvo.getPk_corp();
		String corptype = cpvo.getCorptype();
		YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);
		Map<String,YntCpaccountVO> kmmap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(accounts), new String[]{"accountcode"});
		InventorySetVO vo = new InventorySetVO();
		vo.setKcsprkkm(Kmschema.getKmid(corptype, Kmschema.style_sp, kmmap));
		vo.setKcspckkm(Kmschema.getKmid(corptype, Kmschema.style_shouru, kmmap));
		vo.setYclrkkm(Kmschema.getKmid(corptype, Kmschema.style_ycliao, kmmap));
		vo.setYclckkm(Kmschema.getKmid(corptype, Kmschema.style_zjcl, kmmap));
		vo.setLwrkkm(Kmschema.getKmid(corptype, Kmschema.style_lwcb, kmmap));
		vo.setLwckkm(Kmschema.getKmid(corptype, Kmschema.style_lwsr, kmmap));
		//
		vo.setJxshuiekm(Kmschema.getKmid(corptype, Kmschema.style_jxse, kmmap));
		vo.setXxshuiekm(Kmschema.getKmid(corptype, Kmschema.style_xxse, kmmap));
		//
		vo.setYingshoukm(Kmschema.getKmid(corptype, Kmschema.style_ys, kmmap));
		vo.setYingfukm(Kmschema.getKmid(corptype, Kmschema.style_yf, kmmap));
		vo.setYinhangkm(Kmschema.getKmid(corptype, Kmschema.style_yhck, kmmap));
		vo.setXianjinkm(Kmschema.getKmid(corptype, Kmschema.style_kcxj, kmmap));
		
		vo.setChcbjzfs(InventoryConstant.IC_FZMXHS);//默认按辅助明细
		vo.setChppjscgz(InventoryConstant.IC_RULE_0);//默认按存货名称+规格（型号）+计量单位
		
		vo.setZgrkdfkm(Kmschema.getKmid(corptype, Kmschema.style_yf, kmmap));
		
		//默认暂估
		AuxiliaryAccountBVO[] bodyvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_SUPPLIER, pk_corp, null);
		String zgfz = null;
		if(bodyvos != null && bodyvos.length >0){
			for(AuxiliaryAccountBVO bvo : bodyvos){
				if(!StringUtil.isEmpty(bvo.getName()) 
						&& bvo.getName().trim().contains("暂估")){
					zgfz = bvo.getPk_auacount_b();
					break;
				}
			}
		}

		//启用总账存货不需要设置原来的设置
		if(!isQy){
            setOldKhfz(vo,cpvo,bodyvos);
        }

		if(StringUtil.isEmpty(vo.getZgkhfz())){
            vo.setZgkhfz(zgfz);//默认供应商辅助
        }
		vo =save(userid,pk_corp,vo,true);
		return vo;
	}

	private void setOldKhfz(InventorySetVO vo ,CorpVO cpvo,AuxiliaryAccountBVO[] bodyvos ){
        InventorySetVO vo1 = query(cpvo.getPk_corp());
        if(vo1!=null){
            if(vo1.getChcbjzfs() != -1)
                vo.setChcbjzfs(vo1.getChcbjzfs());
            if(!StringUtil.isEmpty(vo1.getZgrkdfkm()))
                vo.setZgrkdfkm(vo1.getZgrkdfkm());
            if(bodyvos != null && bodyvos.length >0){
                boolean flag = true;
                for(AuxiliaryAccountBVO bvo : bodyvos){
                    if(!StringUtil.isEmpty(bvo.getPk_auacount_b())
                            && bvo.getPk_auacount_b().trim().equals(vo1.getZgkhfz())){
                        vo.setZgkhfz(vo1.getZgkhfz());
                        flag =false;
                        break;
                    }
                }
                if(flag){
                    vo.setZgkhfz(null);
                }
            }else{
                vo.setZgkhfz(null);
            }
        }
    }
	@Override
	public String checkInventorySet(String userid,String pk_corp,InventorySetVO vo) throws DZFWarpException {
		return inventory_setcheck.checkInventorySet(userid, pk_corp, vo);
	}
}