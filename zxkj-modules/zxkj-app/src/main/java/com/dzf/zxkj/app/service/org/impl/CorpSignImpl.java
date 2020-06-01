package com.dzf.zxkj.app.service.org.impl;

import com.dzf.zxkj.app.model.app.corp.TempCorpVO;
import com.dzf.zxkj.app.model.org.SvorgCorpVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.service.corp.IAppCorpPhoto;
import com.dzf.zxkj.app.service.org.ICorpSign;
import com.dzf.zxkj.app.service.pub.IAppPubservice;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.DataSourceFactory;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ObjectProcessor;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.AccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("corpsign")
public class CorpSignImpl implements ICorpSign {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private IAppCorpPhoto corpPhotoservice;

    @Autowired
    private IAppPubservice apppubservice;

    @Override
    public CorpVO confirmSignCorp(UserBeanVO userBean, String user_code) throws DZFWarpException {

        StringBuffer sf = new StringBuffer();
        sf.append("select sv.pk_corp pk_corp,tsv.pk_temp_corp pk_temp_corp,tsv.pk_corpk as pk_corpk ,");
        sf.append(" tsv.corpcode corpcode,tsv.corpname corpname,sv.unitname orgname, ");
        sf.append(" tsv.industry industry, tsv.corptype corptype,");
        sf.append(" cp.corpaddr corpaddr,sv.province contactman,");
        sf.append(" cp.tel tel,sv.phone1 phone,cp.username username,cp.usercode usercode from app_temp_svorg tsv");
        sf.append(" inner join app_temp_corp cp on tsv.pk_temp_corp=cp.pk_temp_corp");
        sf.append(
                " inner join bd_account sv on tsv.pk_svorg=sv.pk_corp where tsv.pk_temp_corp=? and trim(tsv.pk_svorg) = ? ");

        SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, IGlobalConstants.DefaultGroup));
        SQLParameter sp = new SQLParameter();
        sp.addParam(userBean.getPk_tempcorp());
        sp.addParam(userBean.getPk_signcorp());
        List<SvorgCorpVO> svorgCorpVOs = (List<SvorgCorpVO>) sbo.executeQuery(sf.toString(), sp,
                new BeanListProcessor(SvorgCorpVO.class));
        CorpVO corpVo = new CorpVO();
        if (svorgCorpVOs != null && svorgCorpVOs.size() > 0) {
            // 当前小企业的存在代账机构，更改
            if (!AppCheckValidUtils.isEmptyCorp(svorgCorpVOs.get(0).getPk_corpk())) {
                corpVo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class,svorgCorpVOs.get(0).getPk_corpk() );
                corpVo.setFathercorp(svorgCorpVOs.get(0).getPk_corp().trim());
                singleObjectBO.update(corpVo, new String[] { "fathercorp" });
                return corpVo;
            }
            checkIsOnly(svorgCorpVOs.get(0));
            corpVo = saveCorp(svorgCorpVOs.get(0));// 生成公司信息
            updateUserVO(svorgCorpVOs.get(0), corpVo);// 更新用户
            String sql = "update app_temp_corp set bconfirmsign='Y' where pk_temp_corp=?";
            SQLParameter sp1 = new SQLParameter();
            sp1.addParam(userBean.getPk_tempcorp());
            sbo.executeUpdate(sql, sp1);

            // 更新业务合作的pk_corp
            String collsql = "update  app_collabtevalt set pk_corp =? where pk_tempcorp = ?";
            sp1.clearParams();
            sp1.addParam(corpVo.getPk_corp());
            sp1.addParam(userBean.getPk_tempcorp());
            sbo.executeUpdate(collsql, sp1);
        }

        // 生成公司信息 (保存不同的证照信息)
        corpPhotoservice.saveCorpDocs(corpVo.getPk_corp(), userBean.getPk_tempcorp(), userBean.getAccount());

        return corpVo;
    }

    private void updateUserVO(SvorgCorpVO svorgCorpVO, CorpVO corpVo) throws DZFWarpException {
        SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, corpVo.getPk_corp()));
        SQLParameter sp = new SQLParameter();
        String sql = "update ynt_corp_user set pk_corp = ?,fathercorp= ?  where pk_tempcorp =? and (pk_corp is null or  pk_corp = 'appuse' )";
        sp.clearParams();
        sp.addParam(corpVo.getPk_corp());
        sp.addParam(corpVo.getFathercorp());
        sp.addParam(svorgCorpVO.getPk_temp_corp());
        sbo.executeUpdate(sql, sp);

        String sql1 = "update ynt_hximaccount set pk_corp = ?  where pk_tempcorp =?  and (pk_corp is null or  pk_corp = 'appuse' )";
        sp.clearParams();
        sp.addParam(corpVo.getPk_corp());
        sp.addParam(svorgCorpVO.getPk_temp_corp());
        sbo.executeUpdate(sql1, sp);

        String sql2 = "update app_approve_set set pk_corp = ?  where pk_temp_corp =?  and (pk_corp is null or  pk_corp = 'appuse' )";
        sp.clearParams();
        sp.addParam(corpVo.getPk_corp());
        sp.addParam(svorgCorpVO.getPk_temp_corp());
        sbo.executeUpdate(sql2, sp);

        String sql3 = "update ynt_app_customer set pk_corp = ?  where pk_temp_corp =?  and (pk_corp is null or  pk_corp = 'appuse' )";
        sp.clearParams();
        sp.addParam(corpVo.getPk_corp());
        sp.addParam(svorgCorpVO.getPk_temp_corp());
        sbo.executeUpdate(sql3, sp);
    }

    protected CorpVO saveCorp(SvorgCorpVO svorgCorpVO) throws DZFWarpException {
        // 转换公司数据
        TempCorpVO tempcorpvo = (TempCorpVO) singleObjectBO.queryByPrimaryKey(TempCorpVO.class,
                svorgCorpVO.getPk_temp_corp());
        CorpVO corpVo = new CorpVO();
        AccountVO accountvo = (AccountVO) singleObjectBO.queryByPrimaryKey(AccountVO.class, svorgCorpVO.getPk_corp());
        if(accountvo!=null && accountvo.getIschannel()!=null && accountvo.getIschannel().booleanValue()){
            corpVo.setIschannel(DZFBoolean.TRUE);
            corpVo.setApprove_status(1);//已审批
        }
        if(accountvo!=null){
            corpVo.setUnitcode(accountvo.getUnitcode());// 放代账公司unitcode
        }
        corpVo.setInnercode(svorgCorpVO.getCorpcode());
        corpVo.setUnitname(svorgCorpVO.getCorpname());
        corpVo.setPhone1(svorgCorpVO.getTel());
        corpVo.setPhone2(svorgCorpVO.getTel());
        if(accountvo==null || accountvo.getIschannel()==null || !accountvo.getIschannel().booleanValue()){//非加盟商建账
            corpVo.setBegindate(new DZFDate());//建账日期
            corpVo.setIshasaccount(DZFBoolean.TRUE);// 是否建账(Y)
        }else{
            corpVo.setIshasaccount(DZFBoolean.FALSE);// 是否建账(Y)
        }
        corpVo.setUnitshortname(svorgCorpVO.getCorpname());
        corpVo.setFathercorp(svorgCorpVO.getPk_corp());
        corpVo.setDef1(svorgCorpVO.getOrgname());
        corpVo.setForeignname(svorgCorpVO.getContactman());
        corpVo.setCreatedate(new DZFDate());
        corpVo.setDef2(svorgCorpVO.getPhone());
        corpVo.setDef3(svorgCorpVO.getPhone());
        corpVo.setDef5(svorgCorpVO.getPk_temp_corp());// 存临时公司
        corpVo.setIsformal(DZFBoolean.TRUE);// 是否正式客户
        corpVo.setIcostforwardstyle(0);
        corpVo.setCreatedate(new DZFDate());
        corpVo.setCorptype(svorgCorpVO.getCorptype());
        corpVo.setIcostforwardstyle(0);// 成本结转类型模板0
        corpVo.setIndustry(svorgCorpVO.getIndustry());
        corpVo.setPostaddr(svorgCorpVO.getCorpaddr());
        corpVo.setIsworkingunit(DZFBoolean.TRUE);// 是否是小企业
        corpVo.setLegalbodycode(tempcorpvo.getLegalbodycode());// 法人
        corpVo.setVsoccrecode(tempcorpvo.getVsoccrecode());
        if (StringUtil.isEmpty(tempcorpvo.getChargedeptname())) {
            corpVo.setChargedeptname("小规模纳税人");
        } else {
            corpVo.setChargedeptname(tempcorpvo.getChargedeptname());
        }

        corpVo.setIsdkfp(DZFBoolean.FALSE);// 代开发票
        corpVo.setIsdbbx(DZFBoolean.FALSE);// 代办保险
//		corpVo.setIsywskp(DZFBoolean.TRUE);// 是否有税控盘
//		corpVo.setIfwgs(0);// 房屋归属(个人)
        corpVo.setIsformal(DZFBoolean.TRUE);//正式客户

        //工商信息
        corpVo.setSaleaddr(tempcorpvo.getSaleaddr());//住所
        corpVo.setIcompanytype(tempcorpvo.getIcompanytype());;// 公司类型 1：有限公司；2：个人独资企业；3：合伙企业；
        corpVo.setDef9(tempcorpvo.getDef9());// 注册资本
        corpVo.setDestablishdate(tempcorpvo.getDestablishdate());;// 成立日期
        corpVo.setVbusinescope(tempcorpvo.getVbusinescope());;// 经营范围
        corpVo.setVregistorgans(tempcorpvo.getVregistorgans());;// 登记机关
        corpVo.setDapprovaldate(tempcorpvo.getDapprovaldate());;// 核准日期（发证日期）
        // 区域
        Map<String, String> areamap = apppubservice.queryArea("1");
        if (!StringUtil.isEmpty("1" + "_" + tempcorpvo.getVprovince())) {
            if (areamap.get(tempcorpvo.getVprovince()) != null) {
                corpVo.setVprovince(Integer.parseInt(areamap.get("1" + "_" + tempcorpvo.getVprovince())));
            }
        }

        if (!StringUtil.isEmpty(tempcorpvo.getVcity())) {
            if (areamap.get(corpVo.getVprovince() + "_" + tempcorpvo.getVcity()) != null) {
                corpVo.setVcity(Integer.parseInt(areamap.get(corpVo.getVprovince() + "_" + tempcorpvo.getVcity())));
            }
        }

        if (!StringUtil.isEmpty(tempcorpvo.getVarea())) {
            if (areamap.get(corpVo.getVcity() + "_" + tempcorpvo.getVarea()) != null) {
                corpVo.setVarea(Integer.parseInt(areamap.get(corpVo.getVcity() + "_" + tempcorpvo.getVarea())));
            }
        }

//        ICorp corp = (ICorp) SpringUtils.getBean("corpImpl");
//        CorpVO[] corpvostemp = (CorpVO[]) QueryDeCodeUtils.decKeyUtils(
//                new String[] { "legalbodycode", "phone1", "phone2", "unitname", "unitshortname" },
//                new CorpVO[] { corpVo }, 0);
//        String pk_corp = corp.insertCorp(corpvostemp[0], null);
//        corpVo.setPk_corp(pk_corp);
//        //如果是加盟商的不建账
//        if(accountvo.getIschannel()==null || !accountvo.getIschannel().booleanValue()){//非加盟商建账
//            sys_corpserv.saveCorpAccount(corpvostemp[0]);// 建账
//        }

        // -------------------保存app纳税信息--------------------
//        NsCorpVO nscorpvo = new NsCorpVO();
//        nscorpvo.setTaxcode(tempcorpvo.getVtaxcode());
//        nscorpvo.setVbankname(tempcorpvo.getVbillbank());
//        nscorpvo.setVbankcode(tempcorpvo.getVbillbankcode());
//        nscorpvo.setPk_corp(pk_corp);
//        nscorpvo.setPostaddr(svorgCorpVO.getCorpaddr());
//        nscorpvo.setPhone1(svorgCorpVO.getTel());
//        singleObjectBO.saveObject(pk_corp, nscorpvo);
//
//        corpvostemp = (CorpVO[]) QueryDeCodeUtils.decKeyUtils(
//                new String[] { "legalbodycode", "phone1", "phone2", "unitname", "unitshortname" },
//                new CorpVO[] { corpVo }, 1);
//        return corpvostemp[0];
        return null;
    }

    public boolean checkIsOnly(SvorgCorpVO svorgCorpVO) throws DZFWarpException {
        try {
            SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, svorgCorpVO.getPk_corp()));
            SQLParameter sp = new SQLParameter();
            sp.addParam(svorgCorpVO.getCorpcode());
            sp.addParam(CodeUtils1.enCode(svorgCorpVO.getCorpname()));
            sp.addParam(svorgCorpVO.getPk_svorg());

            StringBuilder sbCode = new StringBuilder(
                    "select count(1) from bd_corp where (unitcode=? or unitname=?) and nvl(dr,0) = 0  and fathercorp = ? ");
            Object repeatCodeNum = (Object) sbo.executeQuery(sbCode.toString(), sp, new ObjectProcessor());

            Integer valueres = Integer.parseInt(repeatCodeNum.toString());

            if (valueres > 0) {
                throw new BusinessException("公司编码或公司名称已存在！");
            }
            return false;
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw new BusinessException(e.getMessage());
            } else {
                throw new WiseRunException(e);
            }
        }
    }
}
