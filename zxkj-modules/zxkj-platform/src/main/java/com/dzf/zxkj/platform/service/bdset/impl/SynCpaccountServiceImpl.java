package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.BdTradeAccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.bdset.ISynCpaccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("gl_syncpacckmserv")
public class SynCpaccountServiceImpl implements ISynCpaccountService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private ICorpService corpService;

    @Override
    public YntCpaccountVO[] getHyKMVOS(String pk_corp) throws DZFWarpException {
        List<YntCpaccountVO> reslist =  getCorpvoFromHy(pk_corp);//获取行业的科目

        YntCpaccountVO[] corpcpavos = getSelfCorp(pk_corp);//获取本公司的科目
        Set<String> corpset = new HashSet<String>();
        for(YntCpaccountVO cpavotemp :corpcpavos){
            corpset.add(cpavotemp.getAccountcode()+cpavotemp.getAccountname());
        }

        //判断是否同步过
        for(YntCpaccountVO tempvo:reslist){
            String tempkey = tempvo.getAccountcode() + tempvo.getAccountname();
            if(!corpset.contains(tempkey)){
                tempvo.setVdef1("未同步");
            }else{
                tempvo.setVdef1("已同步");
            }
        }

        if(reslist!=null && reslist.size()>0){
            return reslist.toArray(new YntCpaccountVO[0]);
        }else{
            return null;
        }
    }

    private YntCpaccountVO[] getSelfCorp(String pk_corp) {
        SQLParameter sp = new SQLParameter();
        sp.clearParams();
        sp.addParam(pk_corp);
        YntCpaccountVO[] corpcpavos =  (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, " pk_corp =? and nvl(dr,0)=0 order by accountcode", sp);
        return corpcpavos;
    }


    @SuppressWarnings("unchecked")
    private List<YntCpaccountVO>  getCorpvoFromHy(String pk_corp)throws DZFWarpException{
        CorpVO corpvo = corpService.queryByPk(pk_corp);
        String hysql = "select accountcode,accountname,isleaf  from ynt_tdacc where nvl(dr,0)=0 and pk_trade_accountschema = ?  order by accountcode ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(corpvo.getCorptype());
        List<YntCpaccountVO> reslist = (List<YntCpaccountVO>) singleObjectBO.executeQuery(hysql, sp, new BeanListProcessor(YntCpaccountVO.class));

        ICpaccountCodeRuleService gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils.getBean("gl_accountcoderule");
        ICpaccountService gl_cpacckmserv =   (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
        String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);

        for(YntCpaccountVO tempvo:reslist){
            String newaccoutncode = gl_accountcoderule.getNewRuleCode(tempvo.getAccountcode(), DZFConstant.ACCOUNTCODERULE,newrule );
            tempvo.setAccountcode(newaccoutncode);
        }
        return reslist;
    }


    @SuppressWarnings("unchecked")
    private Map<String,YntCpaccountVO> getHyKMMap(String pk_schema,String pk_corp) throws DZFWarpException{

        Map<String,YntCpaccountVO> resmap = new HashMap<String,YntCpaccountVO>();

        //根据科目方案，找到行业会计科目
        String sql = "select * from ynt_tdacc  where pk_trade_accountschema=? and nvl(dr,0)=0 ";
        SQLParameter param = new SQLParameter();
        param.addParam(pk_schema);
        List<YntCpaccountVO> vo2s = (List<YntCpaccountVO>) singleObjectBO.executeQuery(sql, param, new BeanListProcessor(YntCpaccountVO.class));

        if(vo2s== null || vo2s.size() ==0){
            throw new BusinessException("该制度对应的科目为空，请检查!");
        }
        ICpaccountCodeRuleService gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils.getBean("gl_accountcoderule");
        ICpaccountService  gl_cpacckmserv =   (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
        String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);

        for (YntCpaccountVO tradetempvo : vo2s) {
            String newaccoutncode = gl_accountcoderule.getNewRuleCode(tradetempvo.getAccountcode(),DZFConstant.ACCOUNTCODERULE, newrule);
            tradetempvo.setAccountcode(newaccoutncode);
            resmap.put(tradetempvo.getAccountcode(), tradetempvo);
        }
        return resmap;

    }

    /**
     * 获取公司的科目体系
     */
    @Override
    public YntCpaccountVO[] getGsKmVOS(String pk_corp,YntCpaccountVO[] addvos) throws DZFWarpException {

        Set<String> changekm = new HashSet<String>();
        if(addvos!=null && addvos.length>0){
            for(YntCpaccountVO cpavo:addvos){
                changekm.add(cpavo.getAccountcode());
            }
        }

        YntCpaccountVO[] selfcorps = getSelfCorp(pk_corp);
        List<YntCpaccountVO> selflist = new ArrayList<YntCpaccountVO>(Arrays.asList(selfcorps));
        List<YntCpaccountVO> hyaccounts = getCorpvoFromHy(pk_corp);
        Set<String> hysets = new HashSet<String>();
        for(YntCpaccountVO tempvo:hyaccounts){
            hysets.add(tempvo.getAccountcode()  + tempvo.getAccountname());
        }

        //def2 预制信息，def3同步类型，def4对应同步后编码，def5对应同步后科目名称
        Set<String> gsset = new HashSet<String>();
        //把公司不存在的放在公司中
        for(YntCpaccountVO tempvo:selfcorps){
            gsset.add(tempvo.getAccountcode());
        }
        for(YntCpaccountVO corptempvo : selflist){
            String str = corptempvo.getAccountcode() + corptempvo.getAccountname();
            if(hysets.contains(str)){
                corptempvo.setVdef2("系统");
            }else{
                //如果不相等则需要处理(科目相同名称不同/科目和名称都不同)
                for(YntCpaccountVO hytempvo:hyaccounts){
                    if( changekm.contains(hytempvo.getAccountcode()) && hytempvo.getAccountcode().equals(corptempvo.getAccountcode()) && !hytempvo.getAccountname().equals(corptempvo.getAccountname()) ){
                        corptempvo.setVdef3("2");//科目已占用
                    }
                }
                corptempvo.setVdef2("自定义");
            }
        }
        //没有的添加进去(科目编码不存在的添加进去)
        ICpaccountService  gl_cpacckmserv =   (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
        String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);

        for(YntCpaccountVO tempvo:hyaccounts){
            String str = tempvo.getAccountcode();
            if(!gsset.contains(str) && changekm.contains(str)){//公司不存在，当前公司存在的需要添加
                tempvo.setVdef3("3");//新增科目
                tempvo.setVdef2("自定义");
                tempvo.setVdef4(str);
                tempvo.setVdef5(tempvo.getAccountname());//同步后科目名称
                selflist.add(tempvo);
                String parentcode = DZfcommonTools.getParentCode(str, newrule);
                for(YntCpaccountVO seletempvo:selflist){
                    if(seletempvo.getPk_corp_account()!=null && seletempvo.getIsleaf().booleanValue() && seletempvo.getAccountcode().equals(parentcode)){
                        if("2".equals(seletempvo.getVdef3()) || "4".equals(seletempvo.getVdef3())){//如果已被占用，则不考虑
                            seletempvo.setVdef3("4");//增加下级or科目被占用
                        }else{
                            seletempvo.setVdef3("1");//新增下级
                        }
                        seletempvo.setVdef4("");
                        seletempvo.setVdef5("");
                    }
                }
            }
        }

        YntCpaccountVO[] rescpa = selflist.toArray(new YntCpaccountVO[0]);
        //按照科目编码排序
        java.util.Arrays.sort(rescpa, new Comparator<YntCpaccountVO>() {
            @Override
            public int compare(YntCpaccountVO o1, YntCpaccountVO o2) {
                return o1.getAccountcode().compareTo(o2.getAccountcode());
            }
        });

        return rescpa;
    }

    @SuppressWarnings("unused")
    @Override
    public String saveCpacountVOS(YntCpaccountVO[] cpavos,String pk_corp) throws DZFWarpException {
        StringBuffer tips =new StringBuffer();
        CorpVO corpvo  = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
        Map<String,YntCpaccountVO> trademap = getHyKMMap(corpvo.getCorptype(),pk_corp);
        //需要判断是否已经重复
        for(YntCpaccountVO tempvo:cpavos){
            String comaccount = tempvo.getVdef4();
            int comcount =0;
            if(!StringUtil.isEmpty(tempvo.getVdef3()) && "1".equals(tempvo.getVdef3())){//增加下级不考虑
                continue;
            }

            if(!StringUtil.isEmpty(comaccount)){
                StringBuffer re_tips = new StringBuffer();
                for(YntCpaccountVO tempvocom:cpavos){
                    if(!StringUtil.isEmpty(tempvocom.getVdef3()) && "1".equals(tempvocom.getVdef3())){//增加下级不考虑
                        continue;
                    }
                    if(!StringUtil.isEmpty(tempvocom.getVdef4())){
                        if(comaccount.equals(tempvocom.getVdef4())){
                            comcount++;
                            re_tips.append("【"+tempvocom.getAccountcode()+"_"+tempvocom.getAccountname()+"】");
                        }
                    }else if(comaccount.equals(tempvocom.getAccountcode())){
                        comcount++;
                        re_tips.append("【"+tempvocom.getAccountcode()+"_"+tempvocom.getAccountname()+"】");
                    }
                }
                if(comcount>1){
                    if(trademap.containsKey(comaccount)){
                        throw new BusinessException("您将科目"+re_tips.toString()+"同步到同一个科目【"+comaccount+"_"+trademap.get(comaccount).getAccountname()+"】,请先进行下级转辅助，否则会造成数据错误");
                    }else{
                        throw new BusinessException("您将科目"+re_tips.toString()+"同步到同一个科目【"+comaccount+"】,请先进行下级转辅助，否则会造成数据错误");
                    }
                }
            }
        }
        //把公司科目数组变成map集合
        HashMap<String, YntCpaccountVO> gsmap = new HashMap<String, YntCpaccountVO>();

        YntCpaccountVO[] currcorpvos = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, " nvl(dr,0)=0 and pk_corp ='"+pk_corp+"'", new SQLParameter());

        for(YntCpaccountVO tempvo:currcorpvos){
            gsmap.put(tempvo.getAccountcode(),tempvo);
        }

//		CorpVO corpvo = CorpCache.getInstance().get("", pk_corp);
//		Map<String,YntCpaccountVO> trademap = getHyKMMap(corpvo.getCorptype(),pk_corp);
        Map<String,String> parentsingmap =new HashMap<String,String>();//上级修改的科目
        //新增下级的科目
        Set<String> addkmmap =new HashSet<String>();
        for (YntCpaccountVO tempvo : cpavos) {
            if ("3".equals(tempvo.getVdef3())) {// 新增科目
                if (StringUtil.isEmpty(tempvo.getVdef4())) {//
                    continue;
                } else {
                    addkmmap.add(tempvo.getVdef4());
                }
            }
        }

        //判断当前科目是否是末级科目
        Map<String, DZFBoolean> isleafmap = new HashMap<String,DZFBoolean>();
        for (YntCpaccountVO tempvo1 : cpavos) {
            DZFBoolean isleaf = DZFBoolean.TRUE;
            for (YntCpaccountVO tempvo2 : cpavos) {
                String comparam = null;
                if("3".equals(tempvo2.getVdef3()) && !StringUtil.isEmpty(tempvo2.getVdef4())){
                    comparam = tempvo2.getVdef4() ;
                }else{
                    comparam = tempvo2.getAccountcode();
                }
                if(tempvo2.getAccountcode().startsWith(tempvo1.getAccountcode()) && !tempvo1.getAccountcode().equals(tempvo2.getAccountcode())){
                    isleaf = DZFBoolean.FALSE;
                    break;
                }
            }
            isleafmap.put(tempvo1.getAccountcode(), isleaf);
        }


        //如果是添加下级
        for(YntCpaccountVO tempvo:cpavos){
            //找到行业的科目，生成对应的下级科目
            if(StringUtil.isEmpty(tempvo.getVdef3()) || "0".equals(tempvo.getVdef3())){//同步类型
                continue;
            }else if("1".equals(tempvo.getVdef3())){//新增下级()
                if(StringUtil.isEmpty(tempvo.getVdef4())){//对应同步后的编码
                    continue;
                }else{
                    //判断当前科目是否存在下级
                    YntCpaccountVO currkmvo  =	gsmap.get(tempvo.getAccountcode());
//				    if(currkmvo.getIsfzhs().indexOf("1")>=0){
//				    	throw new BusinessException("暂不支持辅助核算升级!");
//				    }
                    if(currkmvo.getIsleaf() == null || !currkmvo.getIsleaf().booleanValue()){//如果现有系统中存在下级则不考虑
                        continue;
                    }else{
                        if(!addkmmap.contains(tempvo.getVdef4())){
                            throw new BusinessException("新增的科目不存在:"+tempvo.getVdef4());
                        }
                        if(!isleafmap.get(tempvo.getVdef4()).booleanValue()){
                            throw new BusinessException("新增的科目不是末级科目:"+tempvo.getVdef4());
                        }
                        parentsingmap.put(tempvo.getVdef4(), tempvo.getAccountcode());
                    }
                }
            }else if("2".equals(tempvo.getVdef3())){//科目被占用
                if(StringUtil.isEmpty(tempvo.getVdef4())){//
                    continue;
                }else {
                    //修改原有科目的科目编码和科目名称
                    YntCpaccountVO bdtradevo = trademap.get(tempvo.getVdef4());
                    YntCpaccountVO  ynccpavo = 	(YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, tempvo.getPrimaryKey());
                    if(!isleafmap.get(tempvo.getVdef4()).booleanValue()){
                        throw new BusinessException("要修改的科目不是末级科目:"+tempvo.getVdef4());
                    }
//					 if(ynccpavo.getIsfzhs().indexOf("1")>=0){
//					    	throw new BusinessException("暂不支持辅助核算升级!");
//					  }
                    tips.append(ynccpavo.getAccountcode()+",");
                    ynccpavo.setAccountcode(bdtradevo.getAccountcode());
                    ynccpavo.setAccountname(bdtradevo.getAccountname());
                    ynccpavo.setFullname(bdtradevo.getFullname());
                    ynccpavo.setFullname(bdtradevo.getFullname());
                    singleObjectBO.update(ynccpavo, new String[]{"accountcode","accountname","fullname"});
                    updateOldData(ynccpavo);
                }
            }else if("3".equals(tempvo.getVdef3())){//新增科目
                if(StringUtil.isEmpty(tempvo.getVdef4())){//
                    continue;
                }else {
                    //判断当前科目是否存在上级
                    ICpaccountService  gl_cpacckmserv =   (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
                    String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
                    String parentcode = DZfcommonTools.getParentCode(tempvo.getVdef4(), newrule);
                    if(!isleafmap.containsKey(parentcode) && !StringUtil.isEmpty(parentcode)){
                        throw new BusinessException("更新科目失败："+tempvo.getVdef4()+"当前科目不存在上级科目!");
                        //父级的父级如果不存在，则不生成
//						YntCpaccountVO bdtradevo_parent = trademap.get(parentcode);
//						String newparentcode = DZfcommonTools.getParentCode(tempvo.getVdef4(), newrule);
//						if(!isleafmap.containsKey(newparentcode) && !StringUtil.isEmpty(newparentcode)){
//							throw new BusinessException("更新科目失败："+parentcode+"当前科目不存在上级科目!");
//						}
//						YntCpaccountVO acorpVO_parent = createNewCpaccount(pk_corp, corpvo, isleafmap, bdtradevo_parent);
//						acorpVO_parent.setIsleaf(DZFBoolean.FALSE);
//						singleObjectBO.saveObject(pk_corp, acorpVO_parent);
                    }

                    YntCpaccountVO bdtradevo = trademap.get(tempvo.getVdef4());
                    //新增下级 （先保存科目，然后再）生成科目信息
                    YntCpaccountVO acorpVO = createNewCpaccount(pk_corp, corpvo, isleafmap, bdtradevo);
                    String[] pkvs = singleObjectBO.insertVOArr(pk_corp, new YntCpaccountVO[]{acorpVO});
                    acorpVO.setPrimaryKey(pkvs[0]);
                    //更新当前科目到第一个下级
                    acorpVO.setVdef5(parentsingmap.get(acorpVO.getAccountcode()));
                    updateSubKm(acorpVO);
                    tips.append(acorpVO.getAccountcode()+",");
                }
            }
        }

        if(tips.toString().length()>0){
            return "更新的数据："+tips.toString().substring(0,tips.length()-1);
        }else {
            return "暂无数据变化";
        }
    }

    private YntCpaccountVO createNewCpaccount(String pk_corp, CorpVO corpvo, Map<String, DZFBoolean> isleafmap,
                                              YntCpaccountVO bdtradevo) {
        BdTradeAccountVO tempbdtardevo = new BdTradeAccountVO();
        String[] attrNames = tempbdtardevo.getAttributeNames();
        YntCpaccountVO acorpVO = new YntCpaccountVO() ;
        for(String attName:attrNames){
            if("children".equals(attName) ||"dr".equals(attName))
                continue;
            if("pk_trade_account".equals(attName)  || "".equals(attName) || attName == null)
                continue;
            if(attName.equals("pk_trade_accountschema")){

            }else if(attName.equals("pk_corp")){
                acorpVO.setPk_corp(pk_corp);
            }else{
                acorpVO.setAttributeValue(attName, bdtradevo.getAttributeValue(attName)) ;
            }
        }
        acorpVO.setIssyscode(DZFBoolean.TRUE);
        acorpVO.setPk_corp_accountschema(corpvo.getCorptype());//行业
        acorpVO.setIsfzhs("0000000000");
        //是否末级的判断是说 当前公司是否存在科目
        acorpVO.setIsleaf(isleafmap.get(acorpVO.getAccountcode()));
        return acorpVO;
    }

    /**
     * 更新上级科目的发生数据
     * @param acorpVO
     */
    private void updateSubKm(YntCpaccountVO acorpVO) {
        //判断是否有下级数据，如果有下级数据则不做一级数据的更新
//		ICpaccountService  gl_cpacckmserv =   (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
//		String newrule = gl_cpacckmserv.queryAccountRule(acorpVO.getPk_corp());
        //根据科目编码规则 找到对应的上级科目
        String parentcode =acorpVO.getVdef5();//取的就是上级的科目 DZfcommonTools.getParentCode(acorpVO.getAccountcode(), newrule);
        SQLParameter sp = new SQLParameter();
        sp.addParam(parentcode+"%");
        sp.addParam(acorpVO.getPk_corp());
        sp.addParam(parentcode);

        //先更新科目
        if(!StringUtil.isEmpty(parentcode)){
            sp.clearParams();
            sp.addParam(acorpVO.getPk_corp());
            sp.addParam(parentcode);
            YntCpaccountVO[] rescpavos = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, "nvl(dr,0)=0 and pk_corp=? and accountcode =?", sp);
            if(rescpavos ==null || rescpavos.length ==0){
                throw new BusinessException("更新失败，当前科目:"+parentcode+"已被删除");
            }
//			 if(rescpavos[0].getIsfzhs().indexOf("1")>=0){
//			    	throw new BusinessException("暂不支持辅助核算升级!");
//			  }
            //主键互换
            String temppk = rescpavos[0].getPrimaryKey();
            String parentfzxm  =rescpavos[0].getIsfzhs() ;
            rescpavos[0].setIsleaf(DZFBoolean.FALSE);//是否末级需要修改
            rescpavos[0].setIsfzhs("0000000000");//辅助核算清空
            rescpavos[0].setPrimaryKey(acorpVO.getPrimaryKey());
            acorpVO.setPrimaryKey(temppk);
            acorpVO.setIsfzhs(parentfzxm);//取父级的辅助核算项目
            singleObjectBO.updateAry(new YntCpaccountVO[]{rescpavos[0],acorpVO});
            //更新这些值(重新生成一个期初数据)
            sp.clearParams();
            sp.addParam(acorpVO.getPk_corp());
            sp.addParam(temppk);
            QcYeVO[] oldqcyevos = (QcYeVO[]) singleObjectBO.queryByCondition(QcYeVO.class, " nvl(dr,0)=0 and pk_corp  =? and pk_accsubj = ? ",sp);
            //生成新的期初数据
            QcYeVO tempnewqcye = new QcYeVO();
            if(oldqcyevos!=null && oldqcyevos.length>0){
                BeanUtils.copyNotNullProperties(oldqcyevos[0], tempnewqcye);
                tempnewqcye.setPrimaryKey(null);
                tempnewqcye.setPk_accsubj(rescpavos[0].getPrimaryKey());
                singleObjectBO.insertVOArr(acorpVO.getPk_corp(), new QcYeVO[]{tempnewqcye});
            }
        }
        //再更新(预制数据，凭证模板等)
        updateOldData(acorpVO);
    }


    private void updateOldData(YntCpaccountVO acorpVO) {
        //更新预制数据
        //更新期间损益模板
        SQLParameter sp = new SQLParameter();
        sp.addParam(acorpVO.getAccountcode());
        sp.addParam(acorpVO.getAccountname());
        sp.addParam(acorpVO.getPk_corp_account());
        String sysql =  getUpdateSQLaddName("ynt_cptransmb", "accountcode","vname", "pk_transferinaccount");
        singleObjectBO.executeUpdate(sysql, sp);
        //更新成本结转模板(贷方)
        String cbdfsql =  getUpdateSQLaddName("ynt_cpcosttrans", "dvcode","dvname", "pk_creditaccount");
        singleObjectBO.executeUpdate(cbdfsql, sp);
        //成本结转模板(借方)
        String cbjfsql =  getUpdateSQLaddName("ynt_cpcosttrans", "jvcode","jvname" ,"pk_debitaccount");
        singleObjectBO.executeUpdate(cbjfsql, sp);
        //期初余额
        String qcsql =  getUpdateSQLaddName("ynt_qcye", "vcode","vname", "pk_accsubj");
        singleObjectBO.executeUpdate(qcsql, sp);

        //常用凭证模板
        String cysql = getUpdateSQLaddName("ynt_cppztemmb_b", "vcode","vname", "pk_accsubj");
        singleObjectBO.executeUpdate(cysql, sp);

        sp.clearParams();
        sp.addParam(acorpVO.getAccountcode());
        sp.addParam(acorpVO.getPk_corp_account());
        //汇兑损益模板(收益)
        String hdsql =  getUpdateSQL("ynt_remittance", "accountcode", "pk_corp_account");
        singleObjectBO.executeUpdate(hdsql, sp);
        //汇兑损益模板(损失)
        String hdsql1 =  getUpdateSQL("ynt_remittance", "outatcode", "pk_out_account");
        singleObjectBO.executeUpdate(hdsql1, sp);


        //凭证
        String pzsql =  getUpdateSQLaddPzName(sp, "ynt_tzpz_b", new String[]{"vcode", "vname","subj_name","kmmchie"},new String[]{acorpVO.getAccountcode(),acorpVO.getAccountname(),acorpVO.getAccountname(),acorpVO.getFullname()} ,"pk_accsubj",acorpVO.getPk_corp_account());
        singleObjectBO.executeUpdate(pzsql, sp);
    }

    private String getUpdateSQLaddPzName(SQLParameter sp,String tablename, String[] columns, String[] columnvalues, String wherepartcode,String wherepartvalue) {
        sp.clearParams();
        StringBuffer upsql = new StringBuffer();
        upsql.append("update "+tablename +" set  " );
        for(int i=0;i<columns.length;i++){
            if(i == columns.length-1){
                upsql.append( ""+columns[i] +" = ? ");
            }else{
                upsql.append( ""+columns[i] +" = ? ,");
            }
            sp.addParam(columnvalues[i]);
        }
        upsql.append(" where nvl(dr,0)=0 and "+wherepartcode +"=?");
        sp.addParam(wherepartvalue);
        return upsql.toString();
    }

    private String getUpdateSQLaddName(String tablename, String columncode, String columnname,String wherepartcode) {
        String upsql="update "+tablename+ " set "+columncode +" = ? ,"+columnname+" = ? where nvl(dr,0)=0 and "+wherepartcode +"=?";
        return upsql;
    }
    private String getUpdateSQL(String tablename, String columncode,String wherepartcode) {
        String upsql="update "+tablename+ " set "+columncode +" = ? where nvl(dr,0)=0 and "+wherepartcode +"=?";
        return upsql;
    }
}