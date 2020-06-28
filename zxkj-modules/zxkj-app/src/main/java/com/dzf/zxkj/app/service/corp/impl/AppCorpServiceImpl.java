package com.dzf.zxkj.app.service.corp.impl;

import com.dzf.zxkj.app.config.AppConfig;
import com.dzf.zxkj.app.model.app.corp.ScanCorpInfoVO;
import com.dzf.zxkj.app.model.app.corp.TempCorpVO;
import com.dzf.zxkj.app.model.app.corp.UserToCorp;
import com.dzf.zxkj.app.model.app.user.AppUserVO;
import com.dzf.zxkj.app.model.app.user.TempUserRegVO;
import com.dzf.zxkj.app.model.bill.NsCorpUserVO;
import com.dzf.zxkj.app.model.resp.bean.*;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.corp.IAppCorpPhoto;
import com.dzf.zxkj.app.service.corp.IAppCorpService;
import com.dzf.zxkj.app.service.login.IAppLoginCorpService;
import com.dzf.zxkj.app.service.pub.IAppPubservice;
import com.dzf.zxkj.app.service.user.IAppUserActService;
import com.dzf.zxkj.app.service.user.IAppUserService;
import com.dzf.zxkj.app.utils.*;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.DataSourceFactory;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.sys.AccountVO;
import com.dzf.zxkj.platform.model.sys.CorpDocVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("corpservice")
public class AppCorpServiceImpl implements IAppCorpService {

    @Autowired
    private IAppCorpService corpservice ;

    @Autowired
    private IAppPubservice apppubservice;

    @Autowired
    private IAppLoginCorpService user320service;

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private IAppCorpPhoto corpPhotoservice ;

    @Autowired
    AppConfig appConfig;

    @Override
    public ResponseBaseBeanVO updateuserAndCorpRelation(UserBeanVO userBean) throws DZFWarpException {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
        if (userBean == null || userBean.getVersionno() == null) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("版本号为空");
            return bean;
        }
        try {
            int versionno = userBean.getVersionno().intValue();
            return userAndCorpRelation300(userBean);//
        } catch (Exception e) {
            bean.setRescode(IConstant.FIRDES);
            if(e instanceof BusinessException){
                bean.setResmsg(e.getMessage());
            }else{
                bean.setResmsg("关联公司出错！");
            }
            log.error(e.getMessage(),e);
        }
        return bean;
    }

    private ResponseBaseBeanVO userAndCorpRelation300(UserBeanVO userBean) {
        RegisterRespBeanVO bean = new RegisterRespBeanVO();
        String corpname = userBean.getCorpname();
        if (StringUtil.isEmpty(corpname) || AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("完善公司失败：公司信息不能为空!");
            return bean;
        }

        if(!StringUtil.isEmpty(userBean.getIdentify())){
            String tips = CommonServ.resetUserIdentify(userBean, userBean.getIdentify(), true);
            if(tips.length()>0){
                bean.setRescode(IConstant.FIRDES);
                bean.setResmsg(tips);
                return bean;
            }
        }

        String pk_corp = userBean.getPk_corp();
        genTempUser(userBean, singleObjectBO, userBean.getPk_tempcorp(), pk_corp);
        //如果当前公司添加成，同时还是该公司的管理员，或者审核通过的用户，则给出该公司的信息
        ResponseBaseBeanVO autobean = getAutoLogin(userBean, userBean.getAccount_id(), pk_corp, userBean.getPk_tempcorp());

        if(autobean==null || autobean.getRescode().equals(IConstant.FIRDES)){
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg("创建公司成功!");
            return bean;
        }else{
            autobean.setCorpStatus("3");
            return autobean;
        }
    }
    /**
     * 获取自动登录的信息
     * @return
     * @throws DZFWarpException
     */
    private ResponseBaseBeanVO getAutoLogin(UserBeanVO ubean ,String account_id,String pk_corp ,String pk_temp_corp) throws DZFWarpException{

        SQLParameter sp = new SQLParameter();
        StringBuffer qrysql =  new StringBuffer();
        qrysql.append(" select * from ynt_corp_user  " );
        qrysql.append("  where pk_user = ? and nvl(dr,0)=0 and nvl(istate,2)=2 ");
        sp.addParam(account_id);
        if(!StringUtil.isEmpty(pk_corp)){
            qrysql.append("   and pk_corp = ?   ");
            sp.addParam(pk_corp);
        }else if(!StringUtil.isEmpty(pk_temp_corp)){
            qrysql.append("    and pk_tempcorp= ?  ");
            sp.addParam(pk_temp_corp);
        }else{
            return null;
        }
        qrysql.append(" union all ");
        qrysql.append(" select distinct yu.* from ynt_corp_user yu ");
        qrysql.append(" inner join app_temp_user tu  on yu.pk_user = tu.pk_user ");
        qrysql.append("  where tu.pk_temp_user = ? and nvl(yu.dr,0)=0 and nvl(tu.dr,0)=0  ");
        qrysql.append("  and nvl(yu.istate,2)=2 ");
        sp.addParam(account_id);
        if(!StringUtil.isEmpty(pk_corp)){
            qrysql.append("   and yu.pk_corp = ?   ");
            sp.addParam(pk_corp);
        }else if(!StringUtil.isEmpty(pk_temp_corp)){
            qrysql.append("    and yu.pk_tempcorp= ?  ");
            sp.addParam(pk_temp_corp);
        }else{
            return null;
        }

        List<UserToCorp> corplist=  (List<UserToCorp>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(UserToCorp.class));


        if(corplist == null || corplist.size() == 0){
            return null;
        }
        ubean.setAccount_id(corplist.get(0).getPk_user());
        ubean.setPk_corp(pk_corp);
        ubean.setPk_tempcorp(pk_temp_corp);
        ubean.setUsercode(ubean.getAccount());

        IAppLoginCorpService user320service = (IAppLoginCorpService) SpringUtils.getBean("user320service");

        ResponseBaseBeanVO bean = user320service.loginFromTel(ubean,new LoginResponseBeanVO());

        return bean;

    }

    private void zscorpAddMsg(UserBeanVO userBean, RegisterRespBeanVO bean) {
        CorpVO tempvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, userBean.getPk_corp());
        if (tempvo == null) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("上传信息失败，公司信息为空!");
            return;
        }
        String checksql = "select * from ynt_corp_user where pk_corp = ? and nvl(dr,0)=0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(tempvo.getPk_corp());
        List<UserToCorp> resuserlist = (List<UserToCorp>) singleObjectBO.executeQuery(checksql, sp,
                new BeanListProcessor(UserToCorp.class));

        // 是否是管理员
        DZFBoolean ismanage = DZFBoolean.FALSE;
        if(resuserlist!=null){
            for (UserToCorp regvo : resuserlist) {
                if (regvo.getPk_user() != null && userBean.getAccount_id() != null
                        && regvo.getPk_user().equals(userBean.getAccount_id())) {
                    DZFBoolean iscorpmange = regvo.getIsmanage();
                    if (iscorpmange != null && iscorpmange.booleanValue()) {
                        ismanage = DZFBoolean.TRUE;
                    }
                }
            }
        }
        if (!ismanage.booleanValue()) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("您非公司管理员，不能更改公司信息!");
            return;
        }
        try {
            // 更新公司名称
            if (!StringUtil.isEmpty(userBean.getCorpname())) {
                // 如果当前公司已经签约不能修改公司名称
                if (resuserlist != null && resuserlist.size() > 0) {
                    bean.setRescode(IConstant.FIRDES);
                    bean.setResmsg("公司已经签约，不能修改公司名称!");
                    return;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("公司名称维护失败!");
            return;
        }
        bean.setRescode(IConstant.DEFAULT);
    }

    private RegisterRespBeanVO corpAddMsgSwitch1(UserBeanVO userBean) {
        RegisterRespBeanVO bean = new RegisterRespBeanVO();

        AppCheckValidUtils.isEmptyWithCorp(userBean.getPk_corp(), userBean.getPk_tempcorp(), "上传信息失败，公司信息为空!");

        if (!AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())) {
            zscorpAddMsg(userBean, bean);
        } else {
            tempCorpAddMsg(userBean, bean);
        }

        putCorpPhoto(userBean, bean);
        return bean;
    }

    private void putCorpPhoto(UserBeanVO userBean, RegisterRespBeanVO bean) {
        try {
            if(!StringUtil.isEmpty(userBean.getLogo())){
                corpPhotoservice.upCorpPhoto(userBean, bean, 0);
            }
        } catch (Exception e) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("企业logo维护失败!");
            return;
        }

        try {
            if(!StringUtil.isEmpty(userBean.getPermit())){
                corpPhotoservice.upCorpPhoto(userBean, bean, 1);
            }
        } catch (Exception e) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("企业营业执照维护失败!");
            return;
        }

        try {
            if(!StringUtil.isEmpty(userBean.getOrgcodecer())){
                corpPhotoservice.upCorpPhoto(userBean, bean, 2);
            }
        } catch (Exception e) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("企业组织机构代码证维护失败!");
            return;
        }

        try {
            if(!StringUtil.isEmpty(userBean.getTaxregcer())){
                corpPhotoservice.upCorpPhoto(userBean, bean, 3);
            }
        } catch (Exception e) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("企业税务登记证维护失败!");
            return;
        }

        try {
            if(!StringUtil.isEmpty(userBean.getBankopcer())){
                corpPhotoservice.upCorpPhoto(userBean, bean, 4);
            }
        } catch (Exception e) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("企业银行开户许可证维护失败!");
            return;
        }

        try {
            if(!StringUtil.isEmpty(userBean.getStacer())){
                corpPhotoservice.upCorpPhoto(userBean, bean, 5);
            }
        } catch (Exception e) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("企业统计登记证维护失败!");
            return;
        }
    }

    private void tempCorpAddMsg(UserBeanVO userBean, RegisterRespBeanVO bean) {
        TempCorpVO tempvo = (TempCorpVO) singleObjectBO.queryByPrimaryKey(TempCorpVO.class, userBean.getPk_tempcorp());
        if (tempvo == null) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("上传信息失败，公司信息为空!");
            return;
        }

        String checksql = "select * from ynt_corp_user where pk_tempcorp = ? and nvl(dr,0)=0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(tempvo.getPk_temp_corp());
        List<UserToCorp> resuserlist = (List<UserToCorp>) singleObjectBO.executeQuery(checksql, sp,
                new BeanListProcessor(UserToCorp.class));

        // 是否已经签约
        DZFBoolean issign = DZFBoolean.FALSE;
        if(resuserlist!=null){
            for (UserToCorp regvo : resuserlist) {
                if (!StringUtil.isEmpty(regvo.getPk_corp()) && !regvo.getPk_corp().equals("appuse")) {
                    issign = DZFBoolean.TRUE;
                }
            }
        }

        // 是否是管理员
        DZFBoolean ismanage = DZFBoolean.FALSE;
        if(resuserlist!=null && resuserlist.size()>0){
            for (UserToCorp regvo : resuserlist) {
                if (regvo.getPk_user() != null && userBean.getAccount_id() != null
                        && regvo.getPk_user().equals(userBean.getAccount_id())) {
                    DZFBoolean iscorpmange = regvo.getIsmanage();
                    if (iscorpmange != null && iscorpmange.booleanValue()) {
                        ismanage = DZFBoolean.TRUE;
                    }
                }
            }
        }
        if (!ismanage.booleanValue()) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("您非公司管理员，不能更改公司信息!");
            return;
        }
        try {
            // 更新公司名称
            if (!StringUtil.isEmpty(userBean.getCorpname())) {
                // 判断当前公司名称是否已经存在
                String unitsql = " select unitname from bd_corp where nvl(dr,0)=0  and unitname =?  union select corpname from app_temp_corp where nvl(dr,0)=0 and  corpname = ? ";
                sp.clearParams();
                sp.addParam(CodeUtils1.enCode(userBean.getCorpname()));
                sp.addParam(userBean.getCorpname());
                List<String> columns = (List<String>) singleObjectBO.executeQuery(unitsql, sp, new ColumnListProcessor());
                if (columns != null && columns.size() > 0) {
                    bean.setRescode(IConstant.FIRDES);
                    bean.setResmsg("已存在该名称，不能修改公司名称!");
                    return;
                }
                // 如果当前公司已经签约不能修改公司名称
                if (resuserlist != null && resuserlist.size() > 0) {
                    if (issign.booleanValue()) {
                        bean.setRescode(IConstant.FIRDES);
                        bean.setResmsg("公司已经签约，不能修改公司名称!");
                        return;
                    }
                }
                tempvo.setCorpname(userBean.getCorpname());
                singleObjectBO.update(tempvo, new String[] { "corpname" });
                bean.setResmsg("公司名称维护成功!");
            }
        } catch (Exception e) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("公司名称维护失败!");
            return;
        }

        try {
            updateAppCorpmsg(userBean, tempvo);
            bean.setResmsg("公司信息维护成功!");
        } catch (Exception e) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("公司信息维护失败!");
            return;
        }
        bean.setRescode(IConstant.DEFAULT);
    }

    private void updateAppCorpmsg(UserBeanVO userBean, TempCorpVO tempvo ) {
        List<String> upcolumn = new ArrayList<String>();
        //注册码
        if(!StringUtil.isEmpty(userBean.getVsoccrecode())){
            tempvo.setVsoccrecode(userBean.getVsoccrecode());
            upcolumn.add("vsoccrecode");
        }
        //法人代码
        if(!StringUtil.isEmpty(userBean.getLegalbodycode())){
            tempvo.setLegalbodycode( userBean.getLegalbodycode());
            upcolumn.add("legalbodycode");
        }
        //行业
        if(!StringUtil.isEmpty(userBean.getIndustry())){
            tempvo.setIndustry(userBean.getIndustry());
            upcolumn.add("industry");
        }
        //公司性质
        if(!StringUtil.isEmpty(userBean.getChargedeptname())){
            tempvo.setChargedeptname(userBean.getChargedeptname());
            upcolumn.add("chargedeptname");
        }
        //区域
        if(!StringUtil.isEmpty(userBean.getVprovince())){
            tempvo.setVprovince(userBean.getVprovince());
            upcolumn.add("vprovince");
        }

        if(!StringUtil.isEmpty(userBean.getVcity())){
            tempvo.setVcity( userBean.getVcity());
            upcolumn.add("vcity");
        }

        if (!StringUtil.isEmpty(userBean.getVarea())) {
            tempvo.setVarea(userBean.getVarea());
            upcolumn.add("varea");
        }

        if (upcolumn != null && upcolumn.size() > 0) {
            singleObjectBO.update(tempvo, upcolumn.toArray(new String[0]));
        }
    }


    @Override
    public RegisterRespBeanVO corpAddMsg(UserBeanVO userBean) throws DZFWarpException {
        RegisterRespBeanVO bean = new RegisterRespBeanVO();
        try {
            boolean isdemocorp =  AppQueryUtil.getInstance().isDemoCorp("", userBean.getPk_corp());

            if(isdemocorp){
                throw new BusinessException("演示公司无权限");
            }

            return corpAddMsgSwitch1(userBean);
        } catch (Exception e) {
            bean.setRescode(IConstant.FIRDES);
            if( e instanceof BusinessException){
                bean.setResmsg(e.getMessage());
            }else{
                bean.setResmsg("更改公司信息出错！");
            }
            log.error(e.getMessage(),e);
        }
        return bean;
    }

    @Override
    public ResponseBaseBeanVO userAddCorpExamine(UserBeanVO userBean) {
        RegisterRespBeanVO bean = new RegisterRespBeanVO();
        bean.setRescode(IConstant.FIRDES);
        bean.setResmsg("您版本过低，请升级版本，完善信息！");
        return bean;
    }

    @Override
    public String[] getPk_temp_corpByName(UserBeanVO userBean) throws DZFWarpException {
        StringBuffer checkname = new StringBuffer();// 临时公司信息
        SQLParameter sp = new SQLParameter();
        checkname.append(" select pk_temp_corp  from app_temp_corp ");
        checkname.append(" where corpname = ? and nvl(dr,0)=0 ");
        sp.addParam(userBean.getCorpname());

        List<String> unitnamelist = (List<String>) singleObjectBO.executeQuery(checkname.toString(), sp,
                new ColumnListProcessor());

        if (unitnamelist == null || unitnamelist.size() == 0) {
            return null;
        } else {
            return unitnamelist.toArray(new String[0]);
        }
    }

    @Override
    public String[] getPk_corpByName(UserBeanVO userBean) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        StringBuffer checkname1 = new StringBuffer();
        checkname1.append("select pk_corp from bd_corp where unitname =?  and nvl(dr,0)=0"); // 公司信息
        sp.clearParams();
        sp.addParam(CodeUtils1.enCode(userBean.getCorpname()));
        List<String> unitnamelist1 = (List<String>) singleObjectBO.executeQuery(checkname1.toString(), sp,
                new ColumnListProcessor());
        if (unitnamelist1 == null || unitnamelist1.size() == 0) {
            return null;
        } else {
            return unitnamelist1.toArray(new String[0]);
        }
    }

    @Override
    public CorpVO[] getPk_corpVoByName(UserBeanVO userBean) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        StringBuffer linkcorpsql = new StringBuffer();
        linkcorpsql.append(" select t.* from bd_corp  t  ");
        linkcorpsql.append(" where nvl(t.dr,0)=0 and t. unitname =?  ");
        linkcorpsql.append(" and  nvl(isdatacorp,'N')='N' and nvl(isaccountcorp,'N')='N' ");
        sp.addParam(CodeUtils1.enCode(userBean.getCorpname()));

        List<CorpVO> unitnamelist1 = (List<CorpVO>) singleObjectBO.executeQuery(linkcorpsql.toString(), sp,
                new BeanListProcessor(CorpVO.class));
        if (unitnamelist1 == null || unitnamelist1.size() == 0) {
            return null;
        } else {
            return unitnamelist1.toArray(new CorpVO[0]);
        }
    }

    @Override
    public String genTempCorpMsg252(UserBeanVO userBean, String pk_corp, SingleObjectBO sbo) throws DZFWarpException {
        // 判断当前的公司名字是否存在
        String[] corpnamelist =  getPk_temp_corpByName(userBean);
        String pk_svorg = "";
        //获取用户信息
        if(!AppCheckValidUtils.isEmptyCorp(pk_corp)){//如果从正式过来的，服务机构是相应的代账机构的
            CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);//
            cpvo = CorpUtil.getCorpvo(cpvo);
            pk_svorg =  cpvo.getFathercorp();
        } else if(SourceSysEnum.SOURCE_SYS_CST.getValue().equals(userBean.getSourcesys())){
            if(StringUtil.isEmpty(userBean.getAccount_id())){
                throw new BusinessException("用户信息为空!");
            }
            SQLParameter sp = new SQLParameter();
            sp.addParam(userBean.getAccount());
            TempUserRegVO[] regvos = (TempUserRegVO[]) sbo.queryByCondition(TempUserRegVO.class, "nvl(dr,0)=0 and user_code = ? ", sp);
            if(regvos == null || regvos.length == 0){
                throw new BusinessException("用户不存在!");
            }
            if(StringUtil.isEmpty(regvos[0].getPk_svorg())){
                throw new BusinessException("用户尚未关联代账机构!");
            }
            pk_svorg = regvos[0].getPk_svorg();
        }
        if (corpnamelist == null || corpnamelist.length == 0) {
            TempCorpVO tempcorpvo = new TempCorpVO();
            String userObjKey = IDGenerate.getInstance().getNextID(Common.tempidcreate);
            tempcorpvo.setCorpname(userBean.getCorpname());
            tempcorpvo.setUsercode(userBean.getAccount());
            tempcorpvo.setUsername(userBean.getUsername());
            tempcorpvo.setPk_corp(Common.tempidcreate);
            tempcorpvo.setCustnature(2);//默认法人
            tempcorpvo.setTel(userBean.getPhone());
            tempcorpvo.setPk_svorg(pk_svorg);//默认代账机构信息
            tempcorpvo.setPk_temp_corp(userObjKey);
            sbo.insertVOWithPK(tempcorpvo);
            return userObjKey;
        }
        return "";
    }

    @Override
    public void genTempUser(UserBeanVO userBean, SingleObjectBO sbo, String pk_tempcorp, String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(userBean.getAccount());
        TempUserRegVO[] tempvos = (TempUserRegVO[]) sbo.queryByCondition(TempUserRegVO.class,
                " user_code = ? and nvl(dr,0)=0 ", sp);
        if (tempvos != null && tempvos.length > 0) {
            IAppUserService userservice = (IAppUserService) SpringUtils.getBean("userservice");
            userBean.setPhone(userBean.getAccount());
            List<UserVO> listuser = userservice.saveUser(userBean, pk_tempcorp, pk_corp, DZFBoolean.TRUE, 0);
            if (listuser != null && listuser.size() > 0) {
                tempvos[0].setPk_user(listuser.get(0).getCuserid());
                sbo.update(tempvos[0]);
            }
        }
    }

    @Override
    public CorpVO[] getPk_corpandAccountByName(UserBeanVO userBean) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        StringBuffer linkcorpsql = new StringBuffer();
        linkcorpsql.append(" select t.* from bd_corp  t  ");
        linkcorpsql.append(" where nvl(t.dr,0)=0 and t. unitname =? ");
        linkcorpsql.append( " and not exists ");
        linkcorpsql.append("  ( select 1 from ynt_corp_user s  where nvl(s.dr,0)=0 and s.pk_corp = t.pk_corp and s.pk_user=? ) ");
        sp.addParam(CodeUtils1.enCode(userBean.getCorpname()));
        sp.addParam(userBean.getAccount_id());
        List<CorpVO> unitnamelist1 = (List<CorpVO>) singleObjectBO.executeQuery(linkcorpsql.toString(), sp,
                new BeanListProcessor(CorpVO.class));
        if (unitnamelist1 == null || unitnamelist1.size() == 0) {
            return null;
        } else {
            StringBuffer parentpk = new StringBuffer();
            for (CorpVO cpvo : unitnamelist1) {
                parentpk.append("'").append(cpvo.getFathercorp()).append("',");
            }

            AccountVO[] accounts = (AccountVO[]) singleObjectBO.queryByCondition(AccountVO.class,
                    " nvl(dr,0)=0 and pk_corp in(" + parentpk.substring(0, parentpk.length() - 1) + ")",
                    new SQLParameter());
            for (CorpVO cpvo : unitnamelist1) {
                for (AccountVO accountvo : accounts) {
                    if (cpvo.getFathercorp() != null && cpvo.getFathercorp().equals(accountvo.getPrimaryKey())) {
                        cpvo.setDef1(CodeUtils1.deCode(accountvo.getUnitname()));
                        break;
                    }
                }
            }
            return unitnamelist1.toArray(new CorpVO[0]);
        }
    }

    @Override
    public UserVO isExistManage(String pk_corp, String pk_tempcorp, String account_id) throws DZFWarpException {
        if(StringUtil.isEmpty(account_id)){
            throw new BusinessException("用户信息不能为空!");
        }
        StringBuffer qrysql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        UserVO resvo = null;
        if(!AppCheckValidUtils.isEmptyCorp(pk_corp)){
            qrysql.append("select sm_user.* from ynt_corp_user ");
            qrysql.append(" inner join sm_user on ynt_corp_user.pk_user = sm_user.cuserid ");
            qrysql.append(" where ynt_corp_user.pk_corp = ? and nvl(ynt_corp_user.dr,0)=0  and nvl(sm_user.dr,0)= 0 ");
            qrysql.append("   and nvl(ynt_corp_user.ismanage,'N') ='Y'");
            sp.addParam(pk_corp);
            List<UserVO> res =   (List<UserVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(UserVO.class));
            if(res!=null && res.size()>0){
                resvo =  res.get(0);
            }
        }else if(!StringUtil.isEmpty(pk_tempcorp)){
            qrysql.append("select sm_user.* from ynt_corp_user ");
            qrysql.append(" inner join sm_user on ynt_corp_user.pk_user = sm_user.cuserid ");
            qrysql.append(" where ynt_corp_user.pk_tempcorp = ? and nvl(ynt_corp_user.dr,0)=0  and nvl(sm_user.dr,0)= 0 ");
            qrysql.append("  and nvl(ynt_corp_user.ismanage,'N') ='Y'");
            sp.addParam(pk_tempcorp);
            List<UserVO> res =   (List<UserVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(UserVO.class));
            if(res!=null && res.size()>0){
                resvo =  res.get(0);
            }
        }

        if(resvo!=null){
            resvo.setUser_name(CodeUtils1.deCode(resvo.getUser_name()));
        }
        return resvo;
    }

    @Override
    public String isLinkCorp(UserBeanVO userBean) throws DZFWarpException {
        String tips = "";
        // 判断当前公司是否已经注册过
        String[] pk_temp_corps = getPk_temp_corpByName(userBean);
        String pk_temp_corp =  null;
        if(pk_temp_corps!=null && pk_temp_corps.length>1){
            throw new BusinessException("您输入的公司名称对应多家公司，请联系贵公司管理员自行添加!");
        }else if(pk_temp_corps == null){
            pk_temp_corp = "" ;
        }else{
            pk_temp_corp = pk_temp_corps[0];
        }

        String democorp = appConfig.democorpcode;

        CorpVO[] cps = getPk_corpVoByName(userBean);
        List<String> corpids = new ArrayList<String>();
        if (cps != null && cps.length > 0) {
            for (CorpVO cpvo : cps) {
                if (cpvo.getUnitcode().equals(democorp)) {
                    throw new BusinessException("与演示公司同名!");
                }
                corpids.add(cpvo.getPrimaryKey());
            }
        }

        String[] pk_corps = corpids.toArray(new String[0]);

        SQLParameter sp = new SQLParameter();
        if (pk_temp_corp != null || pk_corps != null) {
            StringBuffer linksql = new StringBuffer();
            linksql.append(" select nvl(istate,2)  from ynt_corp_user ");
            linksql.append(" where nvl(dr,0)=0 ");
            linksql.append(" and pk_user = ? ");
            sp.clearParams();
            sp.addParam(userBean.getAccount_id());

            if (pk_corps !=null  && pk_corps.length>0 ) {
                linksql.append(" and " + SqlUtil.buildSqlForIn("pk_corp", pk_corps));
            }else if(!StringUtil.isEmpty(pk_temp_corp)){
                linksql.append(" and pk_tempcorp =?");
                sp.addParam(pk_temp_corp);
            }else{
                return "";
            }

            List<BigDecimal> linkresvalue =   (List<BigDecimal>) singleObjectBO.executeQuery(linksql.toString(), sp, new ColumnListProcessor());
            if (linkresvalue != null && linkresvalue.size() > 0) {
                if(pk_corps !=null  && pk_corps.length>0 ){
                    if(linkresvalue.size() == pk_corps.length){
                        tips = "您已添加过该公司，请直接切换到该公司!";
                    }
                }else if(!StringUtil.isEmpty(pk_temp_corp)){
                    if("2".equals(linkresvalue.get(0).toString())){
                        tips = "您已添加过该公司，请直接切换到该公司!";
                    }else{
                        tips= "您已发送过加入申请，请等待管理员通过!";
                    }
                }
            }
        }
        return tips;
    }

    @Override
    public ResponseBaseBeanVO getFwPjValues() throws DZFWarpException {
        ResponseBaseBeanVO bean= new ResponseBaseBeanVO();

        String qrysql ="select name from wz_appraisal  where nvl(dr,0)=0 order by code";

        List<String> fwpjlist =  (List<String>) singleObjectBO.executeQuery(qrysql, new SQLParameter(), new ColumnListProcessor());

        bean.setRescode(IConstant.DEFAULT);

        bean.setResmsg(fwpjlist.toArray(new String[0]));

        return bean;
    }

    @Override
    public RegisterRespBeanVO updateUserTel(UserBeanVO userBean) throws DZFWarpException {
        RegisterRespBeanVO bean = new RegisterRespBeanVO();
        String phone = userBean.getPhone();
        String userPkNew = null;//新user的主键
        String pwdnew = null;//新user的秘钥
        String userPk = userBean.getAccount_id();//当前user主键
        String pkCorp = userBean.getPk_corp();//当前user公司主键
        String pkTCorp = userBean.getPk_tempcorp();//当前user临时公司主键
        if(StringUtil.isEmpty(pkCorp) && StringUtil.isEmpty(pkTCorp)){
            throw new BusinessException("更改失败：传输信息不全！");
        }else if (StringUtil.isEmpty(phone) || StringUtil.isEmpty(userPk)){
            throw new BusinessException("更改失败：当前帐号信息为空!");
        }else {
            if(!StringUtil.isEmpty(userBean.getIdentify())){
                String tips = CommonServ.resetUserIdentify(userBean, userBean.getIdentify(), true);
                if(tips.length()>0){
                    bean.setRescode(IConstant.FIRDES);
                    bean.setResmsg(tips);
                    return bean;
                }
            }else{
                if(SourceSysEnum.SOURCE_SYS_DZF.getValue().equals(userBean.getSourcesys())){
                    throw new BusinessException("请更新最新版本");
                }
            }

            // 手机号已注册和未注册需要不同的处理
            String checksql = new String("select sm_user.cuserid,sm_user.user_password from sm_user where sm_user.user_code =? and nvl(sm_user.dr,0)=0");
            SQLParameter sp = new SQLParameter();
            sp.addParam(phone);
            List<UserVO> listres = (List<UserVO>) singleObjectBO.executeQuery(checksql.toString(), sp, new BeanListProcessor(UserVO.class));
            if (listres != null && listres.size() > 0){// 此手机号已经注册
                userPkNew = listres.get(0).getCuserid();
                pwdnew = listres.get(0).getUser_password();
                if(StringUtil.isEmpty(userPkNew)){
                    throw new BusinessException("更改失败：此手机号状态异常！");
                }
                String userToCorpSql = new String("select * from ynt_corp_user where pk_user = ? and nvl(dr,0)=0");
                sp.clearParams();
                sp.addParam(userPkNew);
                List<UserToCorp> ucVosTarget = (List<UserToCorp>) singleObjectBO.executeQuery(userToCorpSql, sp,new BeanListProcessor(UserToCorp.class));
                sp.clearParams();
                sp.addParam(userPk);
                List<UserToCorp> ucVos = (List<UserToCorp>) singleObjectBO.executeQuery(userToCorpSql, sp,new BeanListProcessor(UserToCorp.class));
                List<UserToCorp> ucVosUpdate = new ArrayList<UserToCorp>();
                List<UserToCorp> ucVosDelete = new ArrayList<UserToCorp>();
                if(ucVos != null && ucVos.size() > 0 && ucVosTarget != null && ucVosTarget.size() > 0){//要更换的手机号码已有绑定公司，需要两个用户的绑定公司作比较求交集
                    for(UserToCorp vo:ucVos){//更新前vo
                        for(UserToCorp vot:ucVosTarget){//目标vo
                            if (vo.getPk_corp().equals(vot.getPk_corp())
                                    || (!StringUtil.isEmpty(vo.getPk_tempcorp()) && !StringUtil.isEmpty(vot.getPk_tempcorp())
                                    && vo.getPk_tempcorp().equals(vot.getPk_tempcorp()) )
                            ){
                                if((vo.getIsmanage() !=null && vo.getIsmanage().booleanValue())
                                        || (vot.getIsmanage()!=null &&  vot.getIsmanage().booleanValue())){
                                    vo.setIsmanage(DZFBoolean.TRUE);//相同的公司取大的权限
                                    vo.setIaudituser(DZFBoolean.FALSE); //如果是管理员，则待审核的去掉
                                }
                                if((vo.getBaccount() !=null && vo.getBaccount().booleanValue())
                                        || (vot.getBaccount()!=null &&  vot.getBaccount().booleanValue())){
                                    vo.setBaccount(DZFBoolean.TRUE);//相同的公司取大的权限
                                }
                                if((vo.getBdata() !=null && vo.getBdata().booleanValue())
                                        || (vot.getBdata()!=null &&  vot.getBdata().booleanValue())){
                                    vo.setBdata(DZFBoolean.TRUE);//相同的公司取大的权限
                                }
                                if((vo.getIstate() !=null && vo.getIstate() ==  IConstant.TWO)
                                        || (vot.getIstate()!=null &&  vot.getIstate() ==  IConstant.TWO)){
                                    vo.setIstate(IConstant.TWO);
                                }
                                ucVosDelete.add(vot);
                                break;
                            }
                        }
                        vo.setPk_user(userPkNew);
                        ucVosUpdate.add(vo);
                    }
                }else if(ucVos != null && ucVos.size() > 0){//要更换的手机号码没有绑定公司
                    for(UserToCorp vo:ucVos){
                        vo.setPk_user(userPkNew);
                        ucVosUpdate.add(vo);
                    }
                }
                UserToCorp[] updateVos = ucVosUpdate.toArray(new UserToCorp[0]);//没有关联过的进行增加
                if(updateVos != null && updateVos.length > 0){
                    singleObjectBO.updateAry(updateVos);
                }
                UserToCorp[] deleteVos = ucVosDelete.toArray(new UserToCorp[0]);
                if(deleteVos != null && deleteVos.length > 0){
                    singleObjectBO.deleteVOArray(deleteVos);//存在相同的公司，把原来的关系删除
                }
            }else{//不存在需要先注册
                IAppUserActService ias = (IAppUserActService) SpringUtils.getBean("auaservice");
                String userObjKey = IDGenerate.getInstance().getNextID(Common.tempidcreate);//生成主键
                AppUserVO userVO = null;
                try {
                    SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, userBean.getPk_corp()));
                    userVO = (AppUserVO) sbo.queryVOByID(userPk, AppUserVO.class);
                } catch (Exception e) {
                    throw new WiseRunException(e);
                }
                pwdnew = userVO.getUser_password();
                userVO.setPrimaryKey(userObjKey);
                userVO.setCheckcode(phone);
                userVO.setUser_code(phone);
                userVO.setUser_note(phone);
                userVO.setApp_user_tel(phone);
                userPkNew = ias.saveUser(userVO);
                if(StringUtil.isEmpty(userPkNew) || StringUtil.isEmpty(userPk)){
                    throw new BusinessException("更改信息失败!");
                }
                StringBuffer updateSql = new StringBuffer();
                updateSql.append("update ynt_corp_user set pk_user = ? where pk_user =? and nvl(dr,0)=0");
                sp.clearParams();
                sp.addParam(userPkNew);
                sp.addParam(userPk);
                singleObjectBO.executeUpdate(updateSql.toString(), sp);//转移关联关系
            }
            saveTempUser(phone, userPkNew,pwdnew);

            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg("更改成功!");
            return bean;
        }
    }

    // 临时用户
    private List<TempUserRegVO> getTempList(String account) {
        SQLParameter sp = new SQLParameter();
        String appsql = "select * from app_temp_user where user_code=? and  nvl(isaccount,'N') ='N' ";
        sp.addParam(account);
        List<TempUserRegVO> templistapp = (List<TempUserRegVO>) singleObjectBO.executeQuery(appsql, sp,
                new BeanListProcessor(TempUserRegVO.class));

        return templistapp;

    }
    private void saveTempUser(String account,String userid,String pwd){
        List<TempUserRegVO> tlist = getTempList(account);

        if(tlist == null || tlist.size() ==0 ){
            TempUserRegVO tempuservo = new TempUserRegVO();
            tempuservo.setUser_code(account);
            tempuservo.setUser_password(pwd);
            tempuservo.setUser_name(account);
            tempuservo.setApp_user_qq(account);
            tempuservo.setPhone(account);
            tempuservo.setPk_user(userid);
            tempuservo.setIstate(IConstant.TWO);
            SuperVO vo = singleObjectBO.saveObject(Common.tempidcreate, tempuservo);
        }

    }

    @Override
    public ResponseBaseBeanVO updateuserAddCorp(UserBeanVO userBean) throws DZFWarpException {
        // 当前公司名字是否存在
        RegisterRespBeanVO bean = new RegisterRespBeanVO();
        if (StringUtil.isEmpty(userBean.getCorpname())) {
            throw new BusinessException("添加公司失败：公司信息不能为空!");
        }
        if (StringUtil.isEmpty(userBean.getAccount_id())) {
            throw new BusinessException("添加公司失败：您帐号信息为空！");
        }
        //判断是否关联过
        String linktips = corpservice.isLinkCorp(userBean);
        if (!StringUtil.isEmpty(linktips)) {
            throw new BusinessException(linktips);
        }
        // 获取临时公司信息
        String[] pk_temp_corps = corpservice.getPk_temp_corpByName(userBean);
        String pk_temp_corp = null;
        if(pk_temp_corps!=null && pk_temp_corps.length >0){
            pk_temp_corp = pk_temp_corps[0];
        }

        //获取签约公司信息
        CorpVO[] cps = corpservice.getPk_corpVoByName(userBean);
        userBean.setPk_tempcorp(pk_temp_corp);
        String pk_corp = userBean.getPk_corp();

        if (!AppCheckValidUtils.isEmptyCorp(pk_corp)) {// 公司是否为空
            bean = isSign(userBean, pk_corp);
        } else {
            if (cps == null) {// 公司未签约
                checkAddCount(userBean);
                // 公司没注册过，生成公司信息 没有签约
                if (StringUtil.isEmpty(pk_temp_corp)) {
                    // 1:生成公司的信息
                    String pktempcorp = corpservice.genTempCorpMsg252(userBean, null,singleObjectBO);
                    userBean.setPk_tempcorp(pktempcorp);
                    pk_temp_corp = pktempcorp;
                    corpservice.genTempUser(userBean, singleObjectBO, userBean.getPk_tempcorp(), null);
                    bean.setRescode(IConstant.DEFAULT);
                    bean.setCorpStatus("0");
                    bean.setResmsg("恭喜您创建公司成功并成为公司管理员，其他人员的加入将由您审批!");
                } else if (!StringUtil.isEmpty(pk_temp_corp)) {
                    // 判断当前公司是否已经添加过管理员，如果添加过，则通过短信的方式发送给管理员审核
                    UserVO uvo = corpservice.isExistManage(pk_corp, userBean.getPk_tempcorp(),
                            userBean.getAccount_id());
                    if (uvo != null && !StringUtil.isEmpty(userBean.getIsconfirmsg())
                            && userBean.getIsconfirmsg().equals("Y")) {// 不显示确认消息
                        bean = (RegisterRespBeanVO) addToAuditUser(userBean, bean, userBean.getCorpname(), uvo);
                        bean.setCorpStatus("1");
                    } else if (uvo != null && (userBean.getIsconfirmsg().equals("N")
                            || StringUtil.isEmpty(userBean.getIsconfirmsg()))) {
                        bean.setRescode(IConstant.DEFAULT);
                        bean.setConfirmsg("您创建的公司已存在，是否确认加入公司？如有疑问请联系客服");
                    } else {
                        throw new BusinessException("创建公司失败，公司可能已存在，请联系客服!");
                    }
                }
            } else {// 公司已签约
                CorpVO[] corps = corpservice.getPk_corpandAccountByName(userBean);
                if (corps == null) {
                    throw new BusinessException("当前公司已经注册完!");
                } else if (corps.length == 1) {
                    pk_corp = corps[0].getPrimaryKey();
                    if (corps != null) {// bd_corp 存在公司 已经签约
                        bean = isSign(userBean, pk_corp);
                    }
                } else if (corps.length > 1) {
                    List<ContainCorpVo> cpvolists = new ArrayList<ContainCorpVo>();
                    ContainCorpVo tempvo = null;
                    for (CorpVO cpvo : corps) {
                        tempvo = new ContainCorpVo();
                        tempvo.setAccountname(cpvo.getDef1());
                        tempvo.setCname(CodeUtils1.deCode(cpvo.getUnitname()));
                        tempvo.setCcode(cpvo.getUnitcode());
                        tempvo.setPk_corp(cpvo.getPk_corp());
                        tempvo.setPhone(CodeUtils1.deCode(cpvo.getPhone1()));
                        cpvolists.add(tempvo);
                    }
                    bean.setRescode(IConstant.DEFAULT);
                    bean.setResmsg("您公司信息存在多个!");
                    bean.setCorpStatus("2");
                    bean.setCpvos(cpvolists.toArray(new ContainCorpVo[0]));
                }
            }
        }
        //如果通过营业执照过来则更新营业执照信息
        updateYYzz(pk_corp,pk_temp_corp,userBean.getCorpname());


        // 如果当前公司添加成，同时还是该公司的管理员，或者审核通过的用户，则给出该公司的信息
        ResponseBaseBeanVO autobean = user320service.getAutoLogin(userBean, userBean.getAccount_id(), pk_corp, pk_temp_corp);

        if ((bean.getCpvos()!=null && bean.getCpvos().length>0)
                || autobean == null || autobean.getRescode().equals(IConstant.FIRDES)) {
            return bean;
        } else {
            return autobean;
        }
    }

    @Override
    public void updateAddCorpFromActiveCode(UserBeanVO userBean) throws DZFWarpException {
        if(StringUtil.isEmpty(userBean.getActivecode())){
            throw new BusinessException("关联公司失败:激活码不能为空!");
        }

        if(AppCheckValidUtils.isEmptyCorp(userBean.getHandcorp())){
            throw new BusinessException("关联公司失败:待激活公司信息不能为空!");
        }

        CorpVO cpvo  = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, userBean.getHandcorp());

        if(cpvo == null ){
            throw new BusinessException("关联公司失败:公司不存在");
        }

        if(userBean.getActivecode().equals(cpvo.getDef11())){
            genTempUser(userBean, singleObjectBO, "", userBean.getHandcorp());
        }else{
            throw new BusinessException("关联公司失败:激活码不正确!");
        }
    }

    private NsCorpUserVO[] querynsVOs(String pk_corp, String pk_temp_corp,
                                      String account_id,boolean bqrydeafult){
        SQLParameter sp = new SQLParameter();
        StringBuffer wherepart = new StringBuffer();;
        if (!AppCheckValidUtils.isEmptyCorp(pk_corp)) {
            wherepart.append( " nvl(dr,0)=0 and pk_corp = ? ");
            sp.addParam(pk_corp);
        } else {
            wherepart.append( " nvl(dr,0)=0 and pk_temp_corp = ?  " );
            sp.addParam(pk_temp_corp);
        }
        wherepart.append(" and pk_user = ?  ");
        sp.addParam(account_id);
        NsCorpUserVO[] nscorpuservos = (NsCorpUserVO[]) singleObjectBO.queryByCondition(NsCorpUserVO.class, wherepart.toString(), sp);

        if (!AppCheckValidUtils.isEmptyCorp(pk_corp)) {// 取公司默认的值
            CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
            if (cpvo !=null && nscorpuservos != null && nscorpuservos.length > 0 ){
                nscorpuservos[0].setTaxcode(cpvo.getVsoccrecode());
            }
        }

        //从纳税信息获取数据(如果是空是否查询默认值)
        if ((nscorpuservos == null || nscorpuservos.length == 0) && bqrydeafult) {
            if (!AppCheckValidUtils.isEmptyCorp(pk_corp)) {// 取公司默认的值
                CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
                NsCorpUserVO[] vos = new NsCorpUserVO[1];
                NsCorpUserVO vo = new NsCorpUserVO();
                if(cpvo!=null){
                    vo.setTaxcode(cpvo.getVsoccrecode());
                }
                vo.setVbankname(cpvo.getVbankname());
                vo.setVbankcode(cpvo.getVbankcode());
                vo.setPk_corp(pk_corp);
                vo.setPostaddr(cpvo.getPostaddr());
                vo.setPhone1(CodeUtils1.deCode(cpvo.getPhone1()));
                vo.setPhone2(CodeUtils1.deCode(cpvo.getPhone1()));
                vos[0] = vo;
                return vos;
            }
        }
        return nscorpuservos;
    }
    @Override
    public void saveKpmsg(String pk_corp, String pk_temp_corp, String account_id, String corpname, String sh, String gsdz, String kpdh, String khh, String khzh, String grdh, String gryx) throws DZFWarpException {

        AppCheckValidUtils.isEmptyWithCorp(pk_corp, pk_temp_corp, "");

        NsCorpUserVO[] nscorpvos = querynsVOs(pk_corp, pk_temp_corp, account_id,false);

        if (nscorpvos != null && nscorpvos.length > 0 ) {
            nscorpvos[0].setTaxcode(sh);
            nscorpvos[0].setPhone1(kpdh);
            nscorpvos[0].setVbankname(khh);// 开户行
            nscorpvos[0].setVbankcode(khzh);// 开户帐号
            nscorpvos[0].setPostaddr(gsdz);
            nscorpvos[0].setPk_corp(pk_corp);
            nscorpvos[0].setPk_user(account_id);// 用户id
            nscorpvos[0].setPhone2(grdh);// 个人电话
            nscorpvos[0].setPk_temp_corp(pk_temp_corp);
            nscorpvos[0].setVmail(gryx);// 个人邮箱
            singleObjectBO.update(nscorpvos[0]);
        } else {
            NsCorpUserVO nscorpvo = new NsCorpUserVO();
            nscorpvo.setTaxcode(sh);
            nscorpvo.setPhone1(kpdh);
            nscorpvo.setVbankname(khh);// 开户行
            nscorpvo.setVbankcode(khzh);// 开户帐号
            nscorpvo.setPostaddr(gsdz);
            nscorpvo.setPk_corp(pk_corp);
            nscorpvo.setPk_temp_corp(pk_temp_corp);
            nscorpvo.setPk_user(account_id);
            nscorpvo.setPhone2(grdh);// 个人电话
            nscorpvo.setVmail(gryx);// 个人邮箱
            singleObjectBO.saveObject(pk_corp, nscorpvo);
        }
    }

    private String getImg(String pk_corp){
        String sql = "select * from ynt_corpdoc y where nvl(dr,0)=0 and y.pk_corp = ? and y.filetype = ?";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(2);
        List<CorpDocVO> list = (List<CorpDocVO>) singleObjectBO.executeQuery(sql, sp,
                new BeanListProcessor(CorpDocVO.class));
        if(list == null
                || list.size() == 0
                || StringUtil.isEmpty(list.get(0).getVfilepath()))
            throw new BusinessException("请上传营业执照");

        String sReturn = "";
        File f = new File(list.get(0).getVfilepath());
        if (f.exists() && f.isFile()) {
            int byteread = 0;
            int bytesum = 0;

            FileInputStream inStream = null;
            ByteOutputStream bos = null;
            try {
                inStream = new FileInputStream(f);

                bos = new ByteOutputStream();
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    bos.write(buffer, 0, byteread);
                }
                bos.flush();
                byte[] bs = bos.toByteArray();
//				sReturn = new String(bs, "utf-8");
                sReturn = Base64CodeUtils.encode(bs);
            } catch (Exception e) {
                throw new WiseRunException(e);
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException ioe) {
                    }
                }
                if (bos != null)
                    bos.close();
            }
        }

        return sReturn;
    }
    @Override
    public void saveConfirmApply(String id, String confirm, String msgtype) throws DZFWarpException {
    }

    @Override
    public ResponseBaseBeanVO qrykpmsg(String pk_corp, String pk_temp_corp, String account_id, String account) throws DZFWarpException {
        AppCheckValidUtils.isEmptyWithCorp(pk_corp, pk_temp_corp, "");

        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();

        String corpname = apppubservice.getCorpName(pk_corp, pk_temp_corp);

        String sh = "";// 税号
        String khh = "";// 开户行
        String khzh = "";// 开户帐号
        String kpdh = "";// 开票电话
        String gsdz = "";// 公司地址
        String grdh = "";//个人电话
        String gryx = "";//个人邮箱

        NsCorpUserVO[] nscorpvos = querynsVOs(pk_corp, pk_temp_corp, account_id,true);

        if(nscorpvos!=null && nscorpvos.length>0){
            sh = nscorpvos[0].getTaxcode();
            khh = nscorpvos[0].getVbankname();
            khzh = nscorpvos[0].getVbankcode();
            kpdh = nscorpvos[0].getPhone1();//开票电话
            gsdz = nscorpvos[0].getPostaddr();
            grdh = nscorpvos[0].getPhone2();//个人电话
            gryx = nscorpvos[0].getVmail();//个人邮箱
        }

        if(StringUtil.isEmpty(grdh)){
            grdh = account;
        }

        SQLParameter sp = new SQLParameter();
        StringBuffer loginsql = new StringBuffer();
        if (!AppCheckValidUtils.isEmptyCorp(pk_corp)) {// 签约公司信息
            getLoginCorpSQL(sp, pk_corp, loginsql);
        } else if (!StringUtil.isEmpty(pk_temp_corp)) {// 没签约公司信息
            getLoginTempCorpSQL(sp, pk_temp_corp, loginsql);
        }
        ArrayList<AppUserVO> alres = (ArrayList<AppUserVO>) singleObjectBO.executeQuery(loginsql.toString(), sp, new BeanListProcessor(AppUserVO.class));
        if(alres!=null&&alres.size()>0){
            bean.setLegalbodycode(CodeUtils1.deCode(alres.get(0).getLegalbodycode()));//个人姓名
            bean.setVsoccrecode(alres.get(0).getVsoccrecode());//注册码
        }
        bean.setSh(StringUtil.isEmpty(sh) ? "" : sh);
        bean.setKhh(StringUtil.isEmpty(khh) ? "" : khh);
        bean.setKhzh(StringUtil.isEmpty(khzh) ? "" : khzh);
        bean.setKpdh(kpdh);
        bean.setCorpaddr(gsdz);
        bean.setCorpname(corpname);// 公司名字
        bean.setGrdh(grdh);//个人电话
        bean.setGryx(gryx);//个人邮箱


        boolean isman = apppubservice.isManageUserInCorp(pk_corp, pk_temp_corp, account_id);
        // 赋值
        bean.setUsergrade(isman ? IConstant.DEFAULT : IConstant.FIRDES);// 是否是管理员

        return bean;
    }

    /**
     * 获取登录没签约的公司
     *
     * @param userBean
     * @param sp
     * @param pk_corp
     * @param loginsql
     */
    private void getLoginCorpSQL(SQLParameter sp, String pk_corp, StringBuffer loginsql) {
        // 新用户
        loginsql.append("   select  c.postaddr  as  app_corpadd ,c.pk_corp , ");
        loginsql.append("    c.vsoccrecode, c.legalbodycode,ynt_bd_trade.tradename as  industry,   "); // 公司信息
        loginsql.append("    c.chargedeptname,c.vprovince,c.vcity,c.varea ");
        loginsql.append("   from  bd_corp c   ");
        loginsql.append("   left join  ynt_bd_trade on ynt_bd_trade.pk_trade= c.industry ");
        loginsql.append("   where 1=1  ");
        sp.clearParams();
        if (!StringUtil.isEmpty(pk_corp)) {
            loginsql.append("   and c.pk_corp = ? ");
            sp.addParam(pk_corp);
        }
    }

    /**
     * 获取已经签约的公司信息
     *
     * @param userBean
     * @param sp
     * @param pk_corp
     * @param loginsql
     */
    private void getLoginTempCorpSQL(SQLParameter sp, String pk_tempcorp, StringBuffer loginsql) {
        // 新用户
        loginsql.append("   select   temp.vsoccrecode,temp.legalbodycode,ynt_bd_trade.tradename as  industry,");
        loginsql.append("   temp.pk_temp_corp as pk_tempcorp,  ");
        loginsql.append("   temp.chargedeptname,temp.vprovince,temp.vcity,temp.varea ");
        loginsql.append("   from  app_temp_corp temp    ");
        loginsql.append("   left join  ynt_bd_trade on ynt_bd_trade.pk_trade= temp.industry ");
        loginsql.append("   where 1=1  ");
        sp.clearParams();
        if (!StringUtil.isEmpty(pk_tempcorp)) {
            loginsql.append("   and temp.pk_temp_corp = ? ");
            sp.addParam(pk_tempcorp);
        }
    }

    @Override
    public UserBeanVO saveUserFromInvite(UserBeanVO userbean, Integer repeattips) throws DZFWarpException {
        if (StringUtil.isEmpty(userbean.getCorpid())) {// 通过cid信息来获取
            throw new BusinessException("邀请码信息为空");
        }

        String url = userbean.getCorpid().replace( appConfig.invite_DZFURL+"?type=dzf&cid=","");

        String decryid = AppEncryPubUtil.decryParam(url);

        if (StringUtil.isEmpty(decryid)) {
            throw new BusinessException("邀请码信息为空");
        }

        UserToCorp ucorpvo = (UserToCorp) singleObjectBO.queryByPrimaryKey(UserToCorp.class, decryid);

        if (ucorpvo == null) {
            throw new BusinessException("邀请信息为空");
        }

        String pk_corp = ucorpvo.getPk_corp();

        String pk_temp_corp = ucorpvo.getPk_tempcorp();

        UserBeanVO requserbeanvo = new UserBeanVO();

        requserbeanvo.setPk_corp(pk_corp);

        requserbeanvo.setIstates(userbean.getIstates());

        requserbeanvo.setPk_tempcorp(pk_temp_corp);

        requserbeanvo.setAccount(userbean.getAccount());

        requserbeanvo.setCorpname(apppubservice.getCorpName(pk_corp, pk_temp_corp));

        // 自动关联公司
        genTempUser(requserbeanvo, singleObjectBO, pk_temp_corp, pk_corp,repeattips);

        return requserbeanvo;
    }

    /**
     * 生成临时用户信息
     * @param userBean
     * @param sbo
     */
    public void genTempUser(UserBeanVO userBean, SingleObjectBO sbo, String pk_tempcorp, String pk_corp,Integer operator) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(userBean.getAccount());
        TempUserRegVO[] tempvos = (TempUserRegVO[]) sbo.queryByCondition(TempUserRegVO.class,
                " user_code = ? and nvl(dr,0)=0 ", sp);
        if (tempvos != null && tempvos.length > 0) {
            IAppUserService userservice = (IAppUserService) SpringUtils.getBean("userservice");
            userBean.setPhone(userBean.getAccount());
            List<UserVO> listuser = userservice.saveUser(userBean, pk_tempcorp, pk_corp, DZFBoolean.TRUE, operator);
            if (listuser != null && listuser.size() > 0) {
                tempvos[0].setPk_user(listuser.get(0).getCuserid());
                sbo.update(tempvos[0]);
            }
        }
    }


    private void updateYYzz(String pk_corp, String pk_temp_corp,String corpname) {
        //更新工商信息
        ScanCorpInfoVO scanvo = null;///(ScanCorpInfoVO) BusinessAction.busi_identify.getValue(corpname);
        if(scanvo!=null){
            if(!AppCheckValidUtils.isEmptyCorp(pk_corp)){
                CorpVO cpvo  = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
                if(cpvo!=null){
                    cpvo.setVsoccrecode(scanvo.getVsoccrecode());//信用代码
                    cpvo.setLegalbodycode(scanvo.getLegalbodycode());//法人
                    cpvo.setSaleaddr(scanvo.getSaleaddr());//住所
                    cpvo.setIcompanytype(scanvo.getIcompanytype());// 公司类型 1：有限公司；2：个人独资企业；3：合伙企业；
                    cpvo.setDef9(scanvo.getDef9());// 注册资本
                    cpvo.setDestablishdate(scanvo.getDestablishdate());;// 成立日期
                    cpvo.setVbusinescope(scanvo.getVbusinescope());;// 经营范围
                    cpvo.setVregistorgans(scanvo.getVregistorgans());;// 登记机关
                    cpvo.setDapprovaldate(scanvo.getDapprovaldate());;// 核准日期（发证日期）
                }
                singleObjectBO.update(cpvo,new String[]{"vsoccrecode","legalbodycode","saleaddr","icompanytype","def9",
                        "destablishdate","vbusinescope","vregistorgans","dapprovaldate"});
            }
            if(!StringUtil.isEmpty(pk_temp_corp)){
                TempCorpVO tempcorpvo = (TempCorpVO) singleObjectBO.queryByPrimaryKey(TempCorpVO.class, pk_temp_corp);
                if(tempcorpvo!=null){
                    tempcorpvo.setVsoccrecode(scanvo.getVsoccrecode());//信用代码
                    tempcorpvo.setLegalbodycode(scanvo.getLegalbodycode());//法人
                    tempcorpvo.setSaleaddr(scanvo.getSaleaddr());//住所
                    tempcorpvo.setIcompanytype(scanvo.getIcompanytype());;// 公司类型 1：有限公司；2：个人独资企业；3：合伙企业；
                    tempcorpvo.setDef9(scanvo.getDef9());// 注册资本
                    tempcorpvo.setDestablishdate(scanvo.getDestablishdate());;// 成立日期
                    tempcorpvo.setVbusinescope(scanvo.getVbusinescope());;// 经营范围
                    tempcorpvo.setVregistorgans(scanvo.getVregistorgans());;// 登记机关
                    tempcorpvo.setDapprovaldate(scanvo.getDapprovaldate());;// 核准日期（发证日期）
                    singleObjectBO.update(tempcorpvo);
                }
            }
        }


    }


    private void checkAddCount(UserBeanVO userBean) throws DZFWarpException {
        //创建未签约公司的次数不能超过15家
        List<UserToCorp>  userlist  = apppubservice.getUserCorp("",userBean.getAccount_id());

        if(userlist!=null && userlist.size()>0){
            int unqycount = 0;//未签约次数
            for(UserToCorp vo:userlist){
                if(AppCheckValidUtils.isEmptyCorp(vo.getPk_corp())){
                    unqycount++;
                }
            }
            if(unqycount>15){
                throw new BusinessException("超越您创建的公司上限,如有需要请联系管理员!");
            }
        }
    }


    private RegisterRespBeanVO isSign(UserBeanVO userBean, String pk_corp) throws DZFWarpException {
        RegisterRespBeanVO bean = new RegisterRespBeanVO();
        CorpVO rescorp = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
        if(rescorp.getIsseal()!=null && rescorp.getIsseal().booleanValue()){
            throw new BusinessException("该公司已封存!");
        }
        String phone = CodeUtils1.deCode(rescorp.getPhone2());
        String corpname = userBean.getCorpname();
        //获取代账机构信息
        AccountVO accountvo = (AccountVO) singleObjectBO.queryByPrimaryKey(AccountVO.class, rescorp.getFathercorp());
        if (rescorp.getIsdatacorp() != null && rescorp.getIsdatacorp().booleanValue()) {
            throw new BusinessException("您不能添加数据中心相关信息!");
        }
        if (rescorp.getIsaccountcorp() != null && rescorp.getIsaccountcorp().booleanValue()) {
            throw new BusinessException("您不能添加服务机构相关信息!");
        }
        bean.setRescode(IConstant.DEFAULT);
        //获取激活码，通过激活码关联
        UserVO uvo = corpservice.isExistManage(pk_corp, userBean.getPk_tempcorp(), userBean.getAccount_id());
        if(!StringUtil.isEmpty(userBean.getIsconfirmsg()) && "Y".equals(userBean.getIsconfirmsg())){
            if(!StringUtil.isEmpty(userBean.getActivecode()) ){//如果存在激活码时则通过激活码关联
                corpservice.updateAddCorpFromActiveCode(userBean);
                bean.setPhone(phone);
            }else{
                userBean.setPk_corp(pk_corp);
                ResponseBaseBeanVO resbean = addToAuditUser(userBean, bean, corpname, uvo);
                BeanUtils.copyNotNullProperties(resbean, bean);
            }
        }else{
            if((uvo  == null) || userBean.getAccount().equals(phone)){//如果不存在管理员，或者更改管理员时
                if(StringUtil.isEmpty(accountvo.getPhone1())){
                    throw new BusinessException("获取代账机构"+CodeUtils1.deCode(accountvo.getUnitname())+"电话失败，请联系客服(400-600-9365)!");
                }
                bean.setConfirmsg("您创建的公司在系统中已存在，您可通过联系代账公司校验激活码加入您公司，是否确认加入?如有疑问请联系客服");
                bean.setFphone(CodeUtils1.deCode(accountvo.getPhone1()));
            }else{
                bean.setConfirmsg("您创建的公司已存在，是否确认加入公司？如有疑问请联系客服");
            }
        }
        bean.setPk_corp(pk_corp);
        return bean;
    }

    private ResponseBaseBeanVO addToAuditUser(UserBeanVO userBean, ResponseBaseBeanVO bean, String corpname,
                                              UserVO uvo) {
        // 更新关联关系
        // 判断该公司对应的用户是否已经注册过
        userBean.setIstates("3");
        userBean.setIaudituser(DZFBoolean.TRUE);
        corpservice.genTempUser(userBean, singleObjectBO, userBean.getPk_tempcorp(), userBean.getPk_corp());
        bean.setRescode(IConstant.DEFAULT);
        userBean.setPhone(uvo.getUser_code());
        userBean.setAccount(userBean.getAccount());
        bean.setResmsg("您的加入申请已经发送给"+corpname+"公司的管理员"+uvo.getUser_name()+"，手机号"+uvo.getUser_code().substring(0, 3)+"****"+ uvo.getUser_code().substring(7, 11)+".请等待审核。");
        return bean;
    }
}
