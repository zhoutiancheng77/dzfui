package com.dzf.zxkj.app.service.org.impl;

import com.dzf.zxkj.app.model.app.corp.TempCorpVO;
import com.dzf.zxkj.app.model.app.user.TempUserRegVO;
import com.dzf.zxkj.app.model.org.ServiceOrgVO;
import com.dzf.zxkj.app.model.org.SvorgCorpVO;
import com.dzf.zxkj.app.model.org.TempSvOrgVO;
import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.OrgRespBean;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.map.BaiduMapClient;
import com.dzf.zxkj.app.service.login.IAppLoginCorpService;
import com.dzf.zxkj.app.service.org.ICorpSign;
import com.dzf.zxkj.app.service.org.IOrgService;
import com.dzf.zxkj.app.service.user.IAppUserService;
import com.dzf.zxkj.app.utils.*;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.DataSourceFactory;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.ISmsConst;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.image.ImageCommonPath;
import com.dzf.zxkj.platform.model.sys.AccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("orgservice")
public class OrgServiceImpl implements IOrgService {

    @Autowired
    private  SingleObjectBO singleObjectBO;

    @Override
    public ResponseBaseBeanVO qrySvorgLs(UserBeanVO userBean, DZFBoolean bfwwd) throws DZFWarpException {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO() ;
        if(CommonServ.isnull(userBean.getLongitude()) || CommonServ.isnull(userBean.getLatitude())){
            bean.setRescode(IConstant.FIRDES) ;
            bean.setResmsg("经纬度不能为空!") ;
            return bean;
        }
        SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null, userBean.getPk_corp()));
        CorpVO cpvo =(CorpVO) sbo.queryByPrimaryKey(CorpVO.class, userBean.getPk_corp());

        if(!AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())){
            throw new BusinessException("已经正式签约不能更改代账机构!");
        }

        BigDecimal longitude = new BigDecimal(userBean.getLongitude()*Math.PI/180);
        BigDecimal latitude = new BigDecimal(userBean.getLatitude()*Math.PI/180);
        ArrayList<ServiceOrgVO> serviceOrgVOLs  = new ArrayList<ServiceOrgVO>();
        //获取当前经纬度所在的城市
        BaiduMapClient mapclient = new BaiduMapClient();
        String address = userBean.getLatitude()+","+userBean.getLongitude();
        String city = mapclient.getMapAddressCity(address);
        //根据城市获取code
        if(!StringUtil.isEmpty(city)){//城市不能为空
            serviceOrgVOLs = queryServiceOrg(sbo, longitude, latitude, city);
        }
        bean.setRescode(IConstant.DEFAULT);
        if(serviceOrgVOLs!=null && serviceOrgVOLs.size()>0){
            bean.setRescode(IConstant.DEFAULT);
            ArrayList<OrgRespBean> orgBeanLs = new  ArrayList<OrgRespBean>(serviceOrgVOLs.size());
            OrgRespBean tempBean = null;
            for(ServiceOrgVO qryVO :  serviceOrgVOLs){
                tempBean = new OrgRespBean();
                tempBean.setPk_svorg(qryVO.getPk_corp());//服务机构就是机构简称
                tempBean.setPk_acccorp(qryVO.getPk_corp());
                tempBean.setOrgname(qryVO.getOrgname());
                tempBean.setOrgaddr(qryVO.getAddr());
                tempBean.setTel(qryVO.getTel());
                tempBean.setMemo(qryVO.getMemo());
                tempBean.setLongitude(qryVO.getLongitude() ==null ? 0:qryVO.getLongitude().doubleValue());
                tempBean.setLatitude(qryVO.getLatitude() == null ? 0 :qryVO.getLatitude().doubleValue());
                tempBean.setOrgshortname(qryVO.getOrgshortname());
                tempBean.setLogopath(CryptUtil.getInstance().encryptAES(getAccountLog(qryVO.getOrgcode())));
                if(qryVO.getDistance() != null)
                    tempBean.setDistance(qryVO.getDistance().setScale(2, 1).doubleValue());
                orgBeanLs.add(tempBean);
            }
            OrgRespBean[] restemp= (OrgRespBean[]) QueryDeCodeUtils.decKeyUtils(new String[]{"orgshortname","orgname","tel"}, orgBeanLs.toArray(new OrgRespBean[orgBeanLs.size()]), 1);
            bean.setResmsg(restemp) ;
        }else{
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("查询数据为空");
        }
        return bean;
    }

    private String getAccountLog(String orgcode) {
        String[] types = new String[] { ".jpg", ".png", ".jpeg", ".bmp", ".gif" };
        String imagename = null;
        File imagefile = null;
        for (String type : types) {
            // 文件名
            imagename = ImageCommonPath.getAccountLogoPath(orgcode, type);
            imagefile = new File(imagename);
            if (imagefile.exists()) {
                return imagefile.getPath();
            }
        }
        return "";
    }

    private ArrayList<ServiceOrgVO> queryServiceOrg(SingleObjectBO sbo, BigDecimal longitude, BigDecimal latitude,
                                                    String city) {
        SQLParameter sp = new SQLParameter();
        String areaid = getAreaId(sbo, city, sp);
        StringBuffer accountsql = new StringBuffer();
        accountsql.append(" select pk_corp,orgcode,orgname,orgshortname,def10 as longitude,def11 as latitude,addr,tel, asin(sqrt(power(sin(("+latitude+"-y1)/2),2) + cos(y1)*cos("+latitude+")*power(sin(("+longitude+"-x1)/2),2)))*"+ Common.EARTH_RADIUS+"*2 distance,memo ");
        accountsql.append(" from ( select pk_corp,unitcode orgcode,unitname orgname, unitshortname orgshortname,def10,def11,def10*3.141592653589793/180 x1,def11*3.141592653589793/180 y1,postaddr addr,phone1 tel,briefintro memo from bd_account     ");
        accountsql.append("   where nvl(dr,0)=0 and nvl(isseal,'N')='N'   ");
        accountsql.append(" and innercode not in ("+unShowGs()+")");
        accountsql.append(" and ( nvl(ischannel,'N') ='Y' ");
        accountsql.append(" or exists (select 1 from wz_salesorder so where nvl(so.dr,0)=0 and so.pk_corpkjgs = bd_account.pk_corp and nvl(so.contractmny,0) <=10 ) ) ");
        accountsql.append(" and (vprovince = ?   or vcity = ? )  )  order by distance ");
        sp.clearParams();
        sp.addParam(areaid);
        sp.addParam(areaid);
        ArrayList<ServiceOrgVO> serviceOrgVOLs_temp = (ArrayList<ServiceOrgVO>)sbo.executeQuery(accountsql.toString(),sp,new BeanListProcessor(ServiceOrgVO.class));

        if(serviceOrgVOLs_temp == null || serviceOrgVOLs_temp.size() == 0){
            accountsql = new StringBuffer();
            accountsql.append(" select pk_corp,orgcode,orgname,orgshortname,def10 as longitude,def11 as latitude,addr,tel, asin(sqrt(power(sin(("+latitude+"-y1)/2),2) + cos(y1)*cos("+latitude+")*power(sin(("+longitude+"-x1)/2),2)))*"+Common.EARTH_RADIUS+"*2 distance,memo ");
            accountsql.append(" from ( select pk_corp,unitcode orgcode,unitname orgname, unitshortname orgshortname,def10,def11,def10*3.141592653589793/180 x1,def11*3.141592653589793/180 y1,postaddr addr,phone1 tel,briefintro memo from bd_account     ");
            accountsql.append("   where nvl(dr,0)=0  and nvl(isseal,'N')='N'   ");
            accountsql.append("  and innercode not in (" + unShowGs() + ")");
            accountsql.append(" and (vprovince = ?   or vcity = ? )  )  where rownum<11  order by distance ");
            sp.clearParams();
            sp.addParam(areaid);
            sp.addParam(areaid);
            serviceOrgVOLs_temp = (ArrayList<ServiceOrgVO>)sbo.executeQuery(accountsql.toString(),sp,new BeanListProcessor(ServiceOrgVO.class));
        }

        return serviceOrgVOLs_temp;
    }

    private String unShowGs() {
        // BJKJGS278 接受票据公司（内部使用） 4S8Z
        // BJKJGS277 工厂接受委托（内部使用） 6HNC
        // BJKJGS270 易税通客户账号 T4BU
        // BJKJGS287 智能机器人前端系统 MSE9
        StringBuffer strbuff = new StringBuffer();
        String[] strs = new String[] { "BJKJGS278", "BJKJGS277", "BJKJGS270", "BJKJGS287" };
        for (String str : strs) {
            strbuff.append("'" + str + "',");
        }
        return strbuff.substring(0, strbuff.length() - 1);
    }


    private String getAreaId(SingleObjectBO sbo, String city, SQLParameter sp) {
        StringBuffer areasql = new StringBuffer();
        areasql.append(" select region_id from ynt_area where region_name = ? ");
        sp.addParam(city);
        List<BigDecimal> arealist =  (List<BigDecimal>) sbo.executeQuery(areasql.toString(), sp, new ColumnListProcessor());
        String areaid = "";
        if(arealist!=null && arealist.size()>0){
            areaid = String.valueOf(arealist.get(0));
        }
        return areaid;
    }

    @Override
    public ResponseBaseBeanVO saveSvOrgSetting(UserBeanVO userBean,OrgRespBean[] userBeanLs) throws DZFWarpException {
        LoginResponseBeanVO bean = new LoginResponseBeanVO() ;
        SQLParameter sp = new SQLParameter();
        if (CommonServ.isnull(userBean.getLongitude()) || CommonServ.isnull(userBean.getLatitude())) {
            throw new BusinessException("经纬度不能为空！");
        }
        if(StringUtil.isEmpty(userBean.getCorpname())){
            throw new BusinessException("公司信息不能为空！");
        }

        //1:生成公司的信息
        if(StringUtil.isEmpty(userBean.getPk_tempcorp())){
            String pktempcorp = genTempCorpMsg(userBean,singleObjectBO);
            userBean.setPk_tempcorp(pktempcorp);
        }
        //2:新增用户信息
        genTempUser(userBean,singleObjectBO,userBean.getPk_tempcorp());

        //3:公司和用户的关联
        sp.clearParams();
        TempCorpVO[] tempCorp = delOldSvogSign(userBean, singleObjectBO, sp);
        List<TempSvOrgVO> svVOs = new ArrayList<TempSvOrgVO>();
        String orgname1 =null;
        String orgname2 = null;
        String msg = "";
        String telvalue = "";
        CorpVO signcorpvo = null;
        if(!AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())){
            signcorpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, userBean.getPk_corp());
            signcorpvo = CorpUtil.getCorpvo(signcorpvo);
        }
        for (OrgRespBean uBean : userBeanLs) {
            if (tempCorp != null && tempCorp.length > 0) {
                telvalue = tempCorp[0].getTel();
            }
            TempSvOrgVO orgvo = new TempSvOrgVO();
            orgvo.setPk_svorg(uBean.getPk_svorg());
            if(tempCorp!=null && tempCorp.length>0){
                orgvo.setPk_temp_corp(tempCorp[0].getPk_temp_corp());
            }
            if (signcorpvo != null){
                orgvo.setPk_corpk(signcorpvo.getPk_corp());
                orgvo.setCorpcode(signcorpvo.getUnitcode());//公司编码
                orgvo.setCorpname(signcorpvo.getUnitname());//公司名称
                orgvo.setIndustry(signcorpvo.getIndustry());
                orgvo.setCorptype(signcorpvo.getCorptype());
                orgvo.setChargedeptname(signcorpvo.getChargedeptname());//公司性质
            }

            svVOs.add(orgvo);

            CorpVO corpVO = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, uBean.getPk_svorg());

            String svorgtel = StringUtil.isEmpty(uBean.getTel())? CodeUtils1.deCode(corpVO.getPhone1()):
                    uBean.getTel();
//            try {
//                Map<String, String> params = new HashMap<String, String>();
//                if(uBean.getOrgshortname() == null || uBean.getOrgshortname().trim().length() == 0){
//                    params.put("vphone", telvalue);
//                    msservice.sendSmsMsg(params, svorgtel, ISmsConst.TEMPLATECODE_0012, AppQueryUtil.getSignSms(userBean.getSourcesys(), userBean.getAccount(),DZFBoolean.FALSE,userBean.getQysbh()));
//                }else{
//                    //恭喜您，${corpaddr}客户${corpname}公司${vphone}申请与您签约，作为您的潜在客户，请贵司安排人员及时联系回复并登录进行相关操作：http://gs.dazhangfang.com 如有疑问请致电客服：${kfphone}感谢您的使用！
//                    params.put("corpaddr", tempCorp[0].getCorpaddr());
//                    params.put("corpname", tempCorp[0].getCorpname());
//                    params.put("vphone", userBean.getPhone());
//                    params.put("kfphone", AppQueryUtil.getFwjgPhone(userBean.getSourcesys(), userBean.getAccount(),userBean.getQysbh(),""));
//                    msservice.sendSmsMsg(params, svorgtel, ISmsConst.TEMPLATECODE_0013, AppQueryUtil.getSignSms(userBean.getSourcesys(), userBean.getAccount(),DZFBoolean.FALSE,userBean.getQysbh()));
//                }
//            } catch (Exception e) {
//                log.error(e,e);
//            }

        }
        String[] pks = singleObjectBO.insertVOWithPK(userBeanLs[0].getPk_svorg(),svVOs.toArray(new TempSvOrgVO[svVOs.size()]));

        //获取登录信息
        IAppLoginCorpService user320service = (IAppLoginCorpService) SpringUtils.getBean("user320service");
        userBean.setUsercode(userBean.getAccount());
        bean = user320service.loginFromTel(userBean,bean);

        if(pks!=null && pks.length>0){
            String content = tempCorp[0].getCorpname()+","+bean.getUser_name() +"申请签约，请尽快联系，联系方式："+userBean.getAccount()+"！";
            for(int i =0;i<pks.length;i++){
                TempSvOrgVO orgvo = (TempSvOrgVO) singleObjectBO.queryByPrimaryKey(TempSvOrgVO.class, pks[i]);
//                sys_appmsgserv.saveTypeMsg(userBean.getAccount_id(), orgvo.getPk_svorg(), content,
//                        userBean.getPk_corp(), userBean.getPk_tempcorp(), orgvo.getPrimaryKey(), MsgtypeEnum.MSG_TYPE_SJQYYXTZ,"");
            }
        }

        Map<String, String> params = new HashMap<String, String>();
//        if(!StringUtil.isEmpty(orgname1) && !StringUtil.isEmpty(orgname2)){
//            params.put("corpaddr", tempCorp[0].getCorpaddr());
//            params.put("vphone", tempCorp[0].getCorpname()+userBean.getPhone() );
//            params.put("orgname1", orgname1);
//            params.put("orgname2", orgname2);
//            msservice.sendSmsMsg(params, "18607159873", ISmsConst.TEMPLATECODE_0030, SourceSysEnum.SOURCE_SYS_DZF.getSmsdefaultvalue());
//        }else if(!StringUtil.isEmpty(orgname1) || !StringUtil.isEmpty(orgname2)){
//            String temp = StringUtil.isEmpty(orgname1) ?orgname2:orgname1;
//            params.put("corpaddr", tempCorp[0].getCorpaddr());
//            params.put("vphone", tempCorp[0].getCorpname()+userBean.getPhone() );
//            params.put("orgname", temp);
//            msservice.sendSmsMsg(params, "18607159873", ISmsConst.TEMPLATECODE_0029, SourceSysEnum.SOURCE_SYS_DZF.getSmsdefaultvalue());
//        }

        if(bean.getRescode().equals(IConstant.DEFAULT)){
            bean.setResmsg("操作成功!");
        }
        return bean;
    }

    private TempCorpVO[] delOldSvogSign(UserBeanVO userBean, SingleObjectBO sbo, SQLParameter sp) {
        sp.addParam(userBean.getPk_tempcorp());
        TempCorpVO[] tempCorp = (TempCorpVO[]) sbo.queryByCondition(TempCorpVO.class, "pk_temp_corp=?", sp);
        if (tempCorp != null && tempCorp.length > 0) {
            tempCorp[0].setCorpaddr(userBean.getCorpaddr());
            tempCorp[0].setLatitude(new DZFDouble(String.valueOf(userBean.getLatitude())));
            tempCorp[0].setLongitude(new DZFDouble(String.valueOf(userBean.getLongitude())));
            sbo.update(tempCorp[0], new String[] { "corpaddr", "latitude","longitude" });

            String sql = "select *  from app_temp_svorg where pk_temp_corp=?";
            sp.clearParams();
            sp.addParam(tempCorp[0].getPrimaryKey());
            List<TempSvOrgVO> svlist =  (List<TempSvOrgVO>) sbo.executeQuery(sql, sp, new BeanListProcessor(TempSvOrgVO.class));
//            if(svlist!=null && svlist.size()>0){
//                for(TempSvOrgVO temp:svlist){
//                    sys_appmsgserv.deleTypeMsg(temp.getPrimaryKey(), "",tempCorp[0].getPrimaryKey(),MsgtypeEnum.MSG_TYPE_SJQYYXTZ.getValue());
//                }
//            }
            String delsql = "delete from app_temp_svorg where pk_temp_corp=?";
            sbo.executeUpdate(delsql, sp);
        }
        return tempCorp;
    }

    private String genTempCorpMsg(UserBeanVO userBean,SingleObjectBO sbo) throws DZFWarpException {
        //判断当前的公司名字是否存在
        String checksql = "select corpname from app_temp_corp where corpname =?  union all select unitname from bd_corp where unitname = ?";
        SQLParameter sp = new SQLParameter();
        String enname = CodeUtils1.enCode(userBean.getCorpname());
        sp.addParam(userBean.getCorpname());
        sp.addParam(enname);
        List<String> corpnamelist = (List<String>) sbo.executeQuery(checksql, sp, new ColumnListProcessor());
        if(corpnamelist == null || corpnamelist.size() == 0){
            TempCorpVO tempcorpvo = new TempCorpVO();
            String userObjKey = IDGenerate.getInstance().getNextID(Common.tempidcreate);
            tempcorpvo.setCorpname(userBean.getCorpname());
            tempcorpvo.setUsercode(userBean.getAccount());
            tempcorpvo.setPk_corp(Common.tempidcreate);
            tempcorpvo.setCustnature(2);//法人
            tempcorpvo.setUsername(userBean.getUsername());
            tempcorpvo.setTel(userBean.getPhone());
            tempcorpvo.setPk_temp_corp(userObjKey);
            sbo.insertVOWithPK(tempcorpvo);
            return userObjKey;
        }else{
            throw new BusinessException("当前公司已存在,请重新录入!");
        }
    }

    /**
     * 生成临时用户信息
     * @param userBean
     * @param sbo
     */
    private void genTempUser(UserBeanVO userBean, SingleObjectBO sbo,String pk_tempcorp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(userBean.getAccount());
        TempUserRegVO[] tempvos = (TempUserRegVO[]) sbo.queryByCondition(TempUserRegVO.class," user_code = ? and nvl(dr,0)=0 ", sp);
        if(tempvos!=null && tempvos.length>0){
            userBean.setPhone(userBean.getAccount());
            IAppUserService userservice = (IAppUserService) SpringUtils.getBean("userservice");
            List<UserVO> listuser =  userservice.saveUser(userBean, pk_tempcorp, "", DZFBoolean.TRUE,1);
            if(listuser!=null && listuser.size()>0){
                userBean.setAccount_id(listuser.get(0).getCuserid());
                tempvos[0].setPk_user(listuser.get(0).getCuserid());
                sbo.update(tempvos[0]);
            }
        }

    }

    @Override
    public ResponseBaseBeanVO qrySignOrg(UserBeanVO userBean) throws DZFWarpException {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO() ;
        bean.setRescode(IConstant.DEFAULT) ;
        //如果当前公司pk_corp不等于空则先查询对应的会计公司，如果pk_corp 等于空则不查询
        //查询对应的
        CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, userBean.getPk_corp());
        if(!AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())){
            getFatherCorp(userBean, bean, singleObjectBO, corpvo);
        } else {
            StringBuffer sf = new StringBuffer();
            sf.append(" select tsv.pk_temp_svorg pk_svorg,sv.unitname orgname,sv.postaddr corpaddr,sv.phone1 tel,tsv.bsign , ");
            sf.append(" nvl(cp.bconfirmsign,'N') as bconfirmsign,bd_corp.fathercorp as pk_corp,trim(tsv.pk_svorg) as pk_svorg,  ");
            sf.append(" tsv.signtype ");
            sf.append(" from app_temp_svorg tsv ");
            sf.append(" left join app_temp_corp cp on tsv.pk_temp_corp=cp.pk_temp_corp");
            sf.append(" left join bd_account sv on trim(tsv.pk_svorg)=sv.pk_corp ");
            sf.append(" left join ynt_corp_user on  ynt_corp_user.pk_tempcorp =cp.pk_temp_corp  ");
            sf.append(" left join bd_corp on bd_corp.pk_corp = ynt_corp_user.pk_corp  ");
            sf.append(" where cp.pk_temp_corp=?  and ynt_corp_user.pk_user=?  " );
            SQLParameter sp = new SQLParameter();
            sp.addParam(userBean.getPk_tempcorp());
            sp.addParam(userBean.getAccount_id());
            List<SvorgCorpVO> signOrgVOs = (List<SvorgCorpVO>) singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(SvorgCorpVO.class));
            if (signOrgVOs != null && signOrgVOs.size() > 0) {
                List<OrgRespBean> orgBeanLs = new ArrayList<OrgRespBean>(signOrgVOs.size());
                List<OrgRespBean> orgBeanLs1 = new ArrayList<OrgRespBean>();
                OrgRespBean orgbean = null;
                for (SvorgCorpVO signCorp : signOrgVOs) {
                    orgbean = new OrgRespBean();
                    orgbean.setPk_svorg(signCorp.getPk_svorg());
                    orgbean.setOrgname(signCorp.getOrgname());
                    orgbean.setOrgaddr(signCorp.getCorpaddr());
                    orgbean.setTel(signCorp.getTel());
                    if (signCorp.getPk_corp() != null && signCorp.getPk_svorg() != null && signCorp.getPk_corp().equals(signCorp.getPk_svorg())) {
                        orgbean.setPk_zt("0");
                        orgBeanLs1.add(orgbean);
                    } else {
                        if(signCorp.getSigntype()!=null && signCorp.getSigntype().intValue()  ==5){
                            orgbean.setPk_zt("3");//拒签
                        }else if(signCorp.getBsign() != null && signCorp.getBsign().booleanValue()){
                            orgbean.setPk_zt("2");
                        }else{
                            orgbean.setPk_zt("1");
                        }
                    }
                    orgBeanLs.add(orgbean);
                }
                OrgRespBean[] respbeans;
                if (orgBeanLs1.size() > 0) {
                    respbeans = (OrgRespBean[]) QueryDeCodeUtils.decKeyUtils(new String[] { "orgname", "tel" },
                            orgBeanLs1.toArray(new OrgRespBean[0]), 1);
                } else {
                    respbeans = (OrgRespBean[]) QueryDeCodeUtils.decKeyUtils(new String[] { "orgname", "tel" },
                            orgBeanLs.toArray(new OrgRespBean[orgBeanLs.size()]), 1);
                }
                bean.setResmsg(respbeans);
            }else if(!AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp()) ){
                getFatherCorp(userBean, bean, singleObjectBO, corpvo);
            }else{
                bean.setRescode(IConstant.FIRDES);
                bean.setResmsg("代账机构不存在,请选择!");
            }
        }
        return bean;
    }

    private void getFatherCorp(UserBeanVO userBean, ResponseBaseBeanVO bean, SingleObjectBO sbo, CorpVO corpvo) {
        AccountVO facorpvo = (AccountVO) sbo.queryByPrimaryKey(AccountVO.class, corpvo.getFathercorp());
        CorpVO[] cpvos = AppQueryUtil.getInstance().getDemoCorpMsg();
        if(facorpvo!=null){
            OrgRespBean orgbean = new OrgRespBean();
            orgbean.setPk_svorg(facorpvo.getPk_corp());
            orgbean.setOrgname(CodeUtils1.deCode(facorpvo.getUnitname()));
            orgbean.setOrgaddr(facorpvo.getPostaddr() == null?"":facorpvo.getPostaddr());
            orgbean.setQysbh(facorpvo.getDef12());
            if(cpvos!=null && cpvos.length>0 && userBean.getPk_corp().equals(cpvos[0].getPk_corp())){
                orgbean.setTel("400-600-9365");//如果是demo公司，电话是
            }else{
                orgbean.setTel(facorpvo.getPhone1() ==null?"":CodeUtils1.deCode(facorpvo.getPhone1()));
            }
            orgbean.setPk_zt("0");
            bean.setResmsg(new OrgRespBean[]{orgbean});
        }
    }

    @Override
    public ResponseBaseBeanVO updateconfirmSignOrg(UserBeanVO userBean) throws DZFWarpException {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO() ;
        ICorpSign is=(ICorpSign) SpringUtils.getBean("corpsign");
        CorpVO corp =is.confirmSignCorp(userBean,userBean.getAccount());//确认签约逻辑
        bean.setPk_corp(corp.getPk_corp());
        bean.setCorpname(corp.getUnitname());
        //代账公司
        SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null, userBean.getPk_signcorp()));
        CorpVO corptempvo = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, userBean.getPk_signcorp());
        //提醒代账公司
        String msg = null;
//		msg = "【"+AppQueryUtil.getSignSms(userBean.getSourcesys(), userBean.getAccount(),DZFBoolean.FALSE,userBean.getQysbh())+"】恭喜您，客户"+userBean.getAccount()+"已与您签约，请及时回复或登录客户端进行相关操作， 如有疑问请致电客服："+IConstant.DEFAULTPHONE+"感谢您的使用！";
//		int i = SMSService.sendSMS(new String[] {CodeUtils1.deCode(corptempvo.getPhone1())}, msg);
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("vphone", userBean.getAccount());
//        params.put("kfphone", IConstant.DEFAULTPHONE);
//        //恭喜您，客户${vphone}已与您签约，请及时回复或登录客户端进行相关操作， 如有疑问请致电客服：${kfphone}感谢您的使用！
//        msservice.sendSmsMsg(params, CodeUtils1.deCode(corptempvo.getPhone1()),
//                ISmsConst.TEMPLATECODE_0015,
//                AppQueryUtil.getSignSms(userBean.getSourcesys(), userBean.getAccount(),DZFBoolean.FALSE,userBean.getQysbh()));

//		//客户签约确认后提醒客户
//		String msg1 = "【"+AppQueryUtil.getSignSms(userBean.getSourcesys(), userBean.getAccount(),DZFBoolean.FALSE,userBean.getQysbh())+"】恭喜您，您已成功签约"+CodeUtils1.deCode(corptempvo.getUnitshortname())+"代账机构，请保持手机畅通以便及时联系您。快捷报账尽在"+AppQueryUtil.getDefaultAppValue(userBean.getSourcesys())+"。"+AppQueryUtil.getFwjgPhone(userBean.getSourcesys(), userBean.getAccount(),userBean.getQysbh(),"如有疑问请致电客服：")+" 感谢您的使用！";
//		int i1 = SMSService.sendSMS(new String[] {userBean.getAccount()}, msg1);
//        Map<String, String> params1 = new HashMap<String, String>();
//        //恭喜您，您已成功签约${corpname}代账机构，请保持手机畅通以便及时联系您。快捷报账尽在${sysname}。如有疑问请致电客服：${kfphone} 感谢您的使用！
//        params1.put("corpname", CodeUtils1.deCode(corptempvo.getUnitshortname()));
//        params1.put("sysname", AppQueryUtil.getDefaultAppValue(userBean.getSourcesys()));
//        params1.put("kfphone", AppQueryUtil.getFwjgPhone(userBean.getSourcesys(), userBean.getAccount(),userBean.getQysbh(),""));
//        msservice.sendSmsMsg(params1,userBean.getAccount(),
//                ISmsConst.TEMPLATECODE_0014,
//                AppQueryUtil.getSignSms(userBean.getSourcesys(), userBean.getAccount(),DZFBoolean.FALSE,userBean.getQysbh()));
        bean.setRescode(IConstant.DEFAULT) ;
        bean.setResmsg("确认签约成功");
        return bean;
    }
}
