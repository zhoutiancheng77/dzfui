package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.cwbb.LrbController;
import com.dzf.zxkj.report.service.cwbb.ILrbReport;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class LrbPrint extends  AbstractPrint {

    private ILrbReport gl_rep_lrbserv;

    private IZxkjPlatformService zxkjPlatformService;

    private PrintParamVO printParamVO;

    private KmReoprtQueryParamVO queryparamvo;

    public LrbPrint(ILrbReport gl_rep_lrbserv, IZxkjPlatformService zxkjPlatformService, PrintParamVO printParamVO, KmReoprtQueryParamVO queryparamvo) {
        this.gl_rep_lrbserv = gl_rep_lrbserv;
        this.zxkjPlatformService = zxkjPlatformService;
        this.printParamVO = printParamVO;
        this.queryparamvo = (KmReoprtQueryParamVO) queryparamvo.clone();
    }

    public byte[] print (BatchPrintSetVo setVo,CorpVO corpVO, UserVO userVO) {
        try {
            this.queryparamvo.setQjq(this.queryparamvo.getBegindate1().getYear()+ "-01");
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, null);
            LrbController lrbController = new LrbController();
            printReporUtil.setbSaveDfsSer(DZFBoolean.TRUE);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            String xmmcid = printParamVO.getXmmcid();
            String type = printParamVO.getType();
            String font = printParamVO.getFont();
            String columnOrder = printParamVO.getColumnOrder();
            Map<String, String> tmap = new LinkedHashMap<String, String>();/** 声明一个map用来存前台传来的设置参数 */
            CorpVO cpvo = zxkjPlatformService.queryCorpByPk(queryparamvo.getPk_corp());
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
            if (type.equals("2")) {
                printReporUtil.setLineheight(12f);
            } else {
                printReporUtil.setLineheight(18f);
            }
            printReporUtil.setBshowzero(queryparamvo.getBshowzero());
            printReporUtil.setBf_Bold(printReporUtil.getBf());
            printReporUtil.setBasecolor(new BaseColor(0, 0, 0));//设置单元格线颜色
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));
            String titlename = "利 润 表";
            Map<String, List<SuperVO>> qrymap = lrbController.getLrbMap(queryparamvo,zxkjPlatformService,gl_rep_lrbserv);
            Object[] obj = lrbController.getPrintOrder(columnOrder);//根据类型查询
            if (qrymap!=null && qrymap.size() > 0) {
                printReporUtil.printHz(qrymap, null, titlename,
                        (String[]) obj[0], (String[]) obj[1], (int[]) obj[2], (int) obj[3], pmap, tmap);
                return printReporUtil.getContents();
            }

        } catch (DocumentException e) {
            log.error("打印错误", e);
        } catch (Exception e) {
            log.error("打印错误", e);
        }
        return  null;
    }
}
