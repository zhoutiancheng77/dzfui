package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.cwbb.ZcfzController;
import com.dzf.zxkj.report.service.cwbb.IZcFzBReport;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ZcfzPrint extends  AbstractPrint {

    private IZxkjPlatformService zxkjPlatformService;

    private PrintParamVO printParamVO;

    private KmReoprtQueryParamVO queryparamvo;

    private IZcFzBReport gl_rep_zcfzserv;


    public ZcfzPrint(IZxkjPlatformService zxkjPlatformService, PrintParamVO printParamVO, KmReoprtQueryParamVO queryparamvo, IZcFzBReport gl_rep_zcfzserv) {
        this.zxkjPlatformService = zxkjPlatformService;
        this.printParamVO = printParamVO;
        this.queryparamvo = queryparamvo;
        this.gl_rep_zcfzserv = gl_rep_zcfzserv;
    }

    public byte[] print (BatchPrintSetVo setVo,CorpVO corpVO, UserVO userVO) {
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, null);
            printReporUtil.setbSaveDfsSer(DZFBoolean.TRUE);
            ZcfzController zcfzController = new ZcfzController();
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            String type = printParamVO.getType();
            String font = printParamVO.getFont();
            printReporUtil.setIscross(DZFBoolean.FALSE);// 是否横向
            Map<String, String> tmap = new LinkedHashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getTitleperiod());
            tmap.put("单位", "元");
            QueryParamVO paramvo = new QueryParamVO();
            paramvo.setPk_corp(corpVO.getPk_corp());
            List<CorpTaxVo> listVos = zxkjPlatformService.queryTaxVoByParam(paramvo, userVO);
            if (listVos != null && listVos.size() > 0) {
                Optional<CorpTaxVo> optional = listVos.stream().filter(v -> corpVO.getPk_corp().equals(v.getPk_corp())).findFirst();
                optional.ifPresent(corpTaxVo -> {
                    if (!StringUtil.isEmpty(corpTaxVo.getLegalbodycode())) {
                        pmap.put("单位负责人", corpTaxVo.getLegalbodycode());
                    }
                    if (!StringUtil.isEmpty(corpTaxVo.getLinkman1())) {
                        pmap.put("财务负责人", corpTaxVo.getLinkman1());
                    }
                    pmap.put("制表人", userVO.getUser_name());
                });
            }

            printReporUtil.setLineheight(-1f);//设置行高
            printReporUtil.setFirstlineheight(20f);

            printReporUtil.setBshowzero(queryparamvo.getBshowzero());
            printReporUtil.setBf_Bold(printReporUtil.getBf());
            printReporUtil.setBasecolor(new BaseColor(0, 0, 0));//设置单元格线颜色
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));//设置表头字体
            Object[] obj = zcfzController.getPrintXm(0);
            printReporUtil.printHz(zcfzController.getZcfzMap(queryparamvo,zxkjPlatformService, gl_rep_zcfzserv), null, "资 产 负 债 表",
                    (String[]) obj[0], (String[]) obj[1], (int[]) obj[2], (int) obj[3], pmap, tmap);
            return printReporUtil.getContents();
        } catch (DocumentException e) {
            log.error("打印错误", e);
        } catch (IOException e) {
            log.error("打印错误", e);
        }
        return null;
    }
}
