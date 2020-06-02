package com.dzf.zxkj.app.dubbo;

import com.dzf.zxkj.app.model.resp.bean.ReportBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ReportResBean;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.IVersionConstant;
import com.dzf.zxkj.app.report.IAppReportService;
import com.dzf.zxkj.app.service.app.act.IQryReport1Service;
import com.dzf.zxkj.app.service.app.act.IQryReportService;
import com.dzf.zxkj.app.service.pub.IUserPubService;
import com.dzf.zxkj.app.service.user.IAppUserService;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.app.utils.AppQueryUtil;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;



@Slf4j
@Service("appReportService")
@org.apache.dubbo.config.annotation.Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class AppReportServiceImpl implements IAppReportService {

    @Qualifier("orgreportService")
    @Autowired
    private IQryReportService orgreportService;
    @Autowired
    private IAppUserService userservice;
    @Autowired
    private IQryReportService org324reportService;
    @Qualifier("org327reportService")
    @Autowired
    private IQryReportService org327reportService;
    @Autowired
    private IQryReport1Service qryReport1Service;
    @Autowired
    private IUserPubService userPubService;
    @Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
    private IZxkjRemoteAppService iZxkjRemoteAppService;


    public ReturnData<ResponseBaseBeanVO> doReport(ReportBeanVO reportBean){
        UserVO uservo = userPubService.queryUserVOId(reportBean.getAccount_id());
        reportBean.setUsercode(uservo.getUser_code());
        reportBean.setAccount_id(uservo.getCuserid());
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
        Integer operate = Integer.parseInt(reportBean.getOperate());
        try {
            validateRpt(bean, reportBean, operate);
        } catch (Exception e) {
            log.error("错误",e);
            bean.setRescode(IConstant.FIRDES);
            if (e instanceof BusinessException) {
                bean.setResmsg(e.getMessage());
            } else {
                bean.setResmsg("查询失败");
            }
            return ReturnData.error().data(bean);
        }

        switch (operate) {
            case IConstant.ZORE:// 日报
                bean = orgreportService.qryDaily(reportBean);
                break;
            case IConstant.ONE:// 月报
                bean = queryMonthRpt(reportBean);// iqr.qryMonthRep(reportBean);
                break;
            case IConstant.TWO:// 资产负债表
                bean = orgreportService.qryAssetsLiab(reportBean);
                break;
            case IConstant.THREE:// 利润表
                bean = orgreportService.qryProfits(reportBean);
                break;
            case IConstant.FORTH: //现金流量
                bean = orgreportService.qryCashFlow(reportBean);
                break;
            case IConstant.SIX:// 明细表
            case IConstant.SEVEN:
            case IConstant.EIGTH:
            case IConstant.NINE:
                bean = orgreportService.qryDetailReport(reportBean);
                break;
            case IConstant.TWELVE:// 支出比重分析
                bean = orgreportService.expendption(reportBean);
                break;
            case IConstant.THIRTEEN:// 利润增长分析
                bean = orgreportService.profitgrow(reportBean);
                break;
            case IConstant.ONE_ZERO_ZERO://税负预警
                bean = querySfyj(reportBean);
                break;
            case IConstant.ONE_ZERO_TWO://辅助余额表
                bean = queryFzYe(reportBean);
                break;
        }

        return ReturnData.ok().data(bean);
    }

    private ResponseBaseBeanVO queryFzYe(ReportBeanVO reportBean) {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
        String fzlb = reportBean.getFzlb();
        try {
            ReportResBean rptbean = qryReport1Service.qryFzye(reportBean.getPk_corp(), reportBean.getPeriod(), fzlb,
                    reportBean.getPage(),reportBean.getRows(),reportBean.getVersionno());//查询辅助余额表
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg(rptbean);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return bean;
    }

    /**
     * 查询税负预警
     * @param reportBean
     * @return
     */
    private ResponseBaseBeanVO querySfyj(ReportBeanVO reportBean) {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();

        try {
            ReportResBean rptbean = qryReport1Service.qrySfYj(reportBean.getPk_corp(), reportBean.getYear());
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg(rptbean);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return bean;
    }

    private ResponseBaseBeanVO queryMonthRpt(ReportBeanVO reportBean){

        Integer versiono =  reportBean.getVersionno();

        if(versiono<= IVersionConstant.VERSIONNO323){

            return  orgreportService.qryMonthRep(reportBean);
        } else if (versiono >= IVersionConstant.VERSIONNO324 && versiono < IVersionConstant.VERSIONNO327){

            return  org324reportService.qryMonthRep(reportBean);
        }else if(versiono >=IVersionConstant.VERSIONNO327){//327版修改

            return  org327reportService.qryMonthRep(reportBean);
        }

        ResponseBaseBeanVO beanvo = new ResponseBaseBeanVO();

        beanvo.setRescode(IConstant.FIRDES);
        beanvo.setResmsg("版本信息不正确!");

        return beanvo;

    }


    private void validateRpt(ResponseBaseBeanVO bean, ReportBeanVO reportBean, Integer operate) {
        CorpVO[] corpvos =  AppQueryUtil.getInstance().getDemoCorpMsg();
        // 判断是否demo公司
        String demoname = null;
        if (corpvos != null && corpvos.length > 0) {
            if (!StringUtil.isEmpty(corpvos[0].getUnitname())) {
                demoname = CodeUtils1.deCode(corpvos[0].getUnitname());
            }
        }
        //非演示公司 才有账号停用校验
        if (demoname == null || (!StringUtil.isEmpty(reportBean.getCorpname())
                && !demoname.equals(reportBean.getCorpname()))) {
            if(operate != IConstant.SALARY_QRY && operate!= IConstant.SALARY_DETAIL_QRY ){//工资表,工资表详情不考虑
                if (userservice.getPrivilege(IConstant.REPORT,reportBean.getAccount(),"",reportBean.getPk_corp(),reportBean.getPk_tempcorp())) {
                    throw new BusinessException("您没该权限，请联系管理员!");
                }
            }
        }


        Integer versionno  = reportBean.getVersionno();
        if(versionno == null || versionno.intValue() ==0){
            throw new BusinessException("您当前版本出问题，请更新最新版本");
        }

        // 如果该公司没签约则返回是空的
        if (AppCheckValidUtils.isEmptyCorp(reportBean.getPk_corp())) {
            throw new BusinessException("您公司没签约，查询数据为空");
        }
        if (!AppCheckValidUtils.isEmptyCorp(reportBean.getPk_corp())) {
            CorpVO cpvo = iZxkjRemoteAppService.queryByPk(reportBean.getPk_corp());
            if (cpvo == null || cpvo.getBegindate() == null) {
                throw new BusinessException("您公司尚未建账，查询数据为空");
            }
        }
    }

}
