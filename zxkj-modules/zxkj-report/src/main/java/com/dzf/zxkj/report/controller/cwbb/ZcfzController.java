package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.platform.model.report.ZcfzMsgVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.service.cwbb.ILrbReport;
import com.dzf.zxkj.report.service.cwbb.IXjllbReport;
import com.dzf.zxkj.report.service.cwbb.IZcFzBReport;
import com.dzf.zxkj.report.utils.VoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("gl_rep_zcfzact")
@Slf4j
public class ZcfzController  extends ReportBaseController {

    @Autowired
    private IZcFzBReport gl_rep_zcfzserv;

    @Autowired
    private ILrbReport gl_rep_lrbserv;

    @Autowired
    private IXjllbReport gl_rep_xjlybserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;


    /**
     * 查询
     */
    @PostMapping("/queryAction")
    public ReturnData queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        ReportDataGrid grid = new ReportDataGrid();
        QueryParamVO queryParamvo = getQueryParamVO(queryvo,corpVO);
        try {
            String begindate = DateUtils.getPeriod(queryParamvo.getBegindate1());
            String ishajz = "N";
            if (queryParamvo.getIshasjz() != null && queryParamvo.getIshasjz().booleanValue()) {
                ishajz = "Y";
            }
            String ishasye = queryvo.getIshasye();
            String hasye1 = queryvo.getHasye1();
            String hasye2 = queryvo.getHasye2();
            String hasye3 = queryvo.getHasye3();
            String hasye4 = queryvo.getHasye4();
            if (ishasye == null || "".equals(ishasye)) {
                ishasye = "N";
            }
            /** 全局记忆 按往来科目明细分析填列 */
//            getSession().setAttribute("ishasye", ishasye);
            checkPowerDate(queryParamvo,corpVO);
            ZcFzBVO[] kmmxvos = null;
            Object[] objs = null;
            if ("Y".equals(ishasye)) {
                String[] yes = new String[]{ishasye, hasye1, hasye2, hasye3,hasye4};
                objs = gl_rep_zcfzserv.getZCFZBVOsConMsg(begindate, queryParamvo.getPk_corp(), ishajz, yes);
            } else {
                objs = gl_rep_zcfzserv.getZCFZBVOsConMsg(begindate, queryParamvo.getPk_corp(), ishajz, new String[]{ishasye, "N", "N", "N","N"});
            }

            kmmxvos = (ZcFzBVO[]) objs[0];

            log.info("查询成功！");
            grid.setTotal((long) (kmmxvos == null ? 0 : kmmxvos.length));
            grid.setRows(kmmxvos == null ? new ArrayList<ZcFzBVO>() : Arrays.asList(kmmxvos));
            grid.setMsg("查询成功");
            String blancemsg = isBlance(kmmxvos, queryParamvo.getPk_corp());
            // 看看是否相平
            grid.setSuccess(true);
            if (blancemsg == null) {
                grid.setBlancemsg(true);
            } else {
                String noblance = (String) objs[1];
                grid.setBlancemsg(false);
                grid.setZcfz_jyx((ZcfzMsgVo) objs[2]);
                grid.setMsg(noblance);
                grid.setBlancetitle(blancemsg);
            }

        } catch (Exception e) {
            grid.setRows(new ArrayList<ZcFzBVO>());
            printErrorLog(grid, e, "查询失败！");
        }
        //日志记录
//        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT.getValue(),
//                "资产负债查询:" + queryParamvo.getBegindate1().toString().substring(0, 7), ISysConstants.SYS_2);

        return ReturnData.ok().data(grid);
    }



    private String isBlance2(ZcfzMsgVo zcfzmsgvo) {
        DZFDouble zcvalue = VoUtils.getDZFDouble(zcfzmsgvo.getZcvalue());
        DZFDouble fzvalue =  VoUtils.getDZFDouble(zcfzmsgvo.getFzvalue());
        DZFDouble qyvale =  VoUtils.getDZFDouble(zcfzmsgvo.getQyvalue());
        if (zcvalue.sub(fzvalue).sub(qyvale).doubleValue() != 0) {
            return "N";
        }
        DZFDouble wfpvalue =  VoUtils.getDZFDouble(zcfzmsgvo.getWfpvlaue());
        DZFDouble jlrvalue =  VoUtils.getDZFDouble(zcfzmsgvo.getJlrvalue());
        if (wfpvalue.sub(jlrvalue).doubleValue() != 0) {
            return "N";
        }
        return null;
    }

    public String isBlance(ZcFzBVO[] dataVOS, String pk_corp) {
        if (dataVOS == null || dataVOS.length == 0) {
            return null;
        }

        ZcFzBVO lastbvo = (ZcFzBVO) dataVOS[dataVOS.length - 1];
        //如果是村集体，则lastvo 不是最后一个
        Integer corpschema = zxkjPlatformService.getAccountSchema(pk_corp);
        if (corpschema == DzfUtil.VILLAGECOLLECTIVE.intValue()
                || corpschema == DzfUtil.RURALCOOPERATIVE.intValue() ) {
            for (int i = dataVOS.length - 1; i >= 0; i--) {
                if (dataVOS[i].getZc().indexOf("资产总计") >= 0) {
                    lastbvo = dataVOS[i];
                    break;
                }
            }
        }


        DZFDouble ncye1 = (lastbvo).getNcye1() == null ? DZFDouble.ZERO_DBL
                : (lastbvo).getNcye1();
        DZFDouble ncye2 = (lastbvo).getNcye2() == null ? DZFDouble.ZERO_DBL
                : (lastbvo).getNcye2();
        DZFDouble qmye1 = (lastbvo).getQmye1() == null ? DZFDouble.ZERO_DBL
                : (lastbvo).getQmye1();
        DZFDouble qmye2 = (lastbvo).getQmye2() == null ? DZFDouble.ZERO_DBL
                : (lastbvo).getQmye2();

        StringBuffer message = new StringBuffer();
        if (qmye1.setScale(2, DZFDouble.ROUND_HALF_UP).sub(qmye2.setScale(2, DZFDouble.ROUND_HALF_UP))
                .doubleValue() != 0) {
            message.append("期末余额,");
        }
        if (ncye1.setScale(2, DZFDouble.ROUND_HALF_UP).sub(ncye2.setScale(2, DZFDouble.ROUND_HALF_UP))
                .doubleValue() != 0) {
            message.append("年初余额，");
        }

        if (message.toString().trim().length() > 0) {
//			return "资产负债：" + message.substring(0, message.toString().trim().length() - 1) + "不平";
            return "资产负债表不平,差额" + Common.format(SafeCompute.sub(qmye1, qmye2)) + ",请检查";
        } else {
            return null;
        }
    }



    @Override
    public String getPrintTitleName() {
        return "资 产 负 债 表";
    }



















}
