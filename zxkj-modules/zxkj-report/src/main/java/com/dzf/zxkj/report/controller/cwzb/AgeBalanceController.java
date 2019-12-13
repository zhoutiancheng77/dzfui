package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.common.entity.DynamicAttributeVO;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.query.AgeReportQueryVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.report.AgeReportResultVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwzb.AgeBalanceExcelField;
import com.dzf.zxkj.report.service.cwzb.IAgeBalanceReportService;
import com.dzf.zxkj.report.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("gl_rep_zlyebact")
@Slf4j
public class AgeBalanceController {

    @Autowired
    private IAgeBalanceReportService gl_rep_zlyeb;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @PostMapping("/query")
    public ReturnData<Grid> query(@MultiRequestBody AgeReportQueryVO param) {

        if (param.getFzlb() != null && param.getFzlb() > 0) {
            param.setAuaccount_type("fzhsx" + param.getFzlb());
        }

        if(StringUtil.isEmptyWithTrim(param.getPk_corp())){
            param.setPk_corp(SystemUtil.getLoginCorpId());
        }

        CorpVO corpVO = zxkjPlatformService.queryCorpByPk(param.getPk_corp());

        if (corpVO.getBegindate().after(param.getEnd_date())) {
            return ReturnData.error().message("截止日期不能早于建账日期");
        }

        param.setJz_date(corpVO.getBegindate());

        Grid json = new Grid();
        try {
            AgeReportResultVO rs = gl_rep_zlyeb.query(param);
            json.setRows(rs);
            json.setSuccess(true);
            json.setMsg("查询成功");
        } catch (Exception e) {
            json.setSuccess(false);
            json.setMsg("查询失败");
            log.error(e.getMessage());
        }
        return ReturnData.ok().data(json);
    }


    @PostMapping("export/excel")
    public void excelReport (@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        List<LinkedHashMap<String, Object>>  dataList =  JsonUtils.deserialize(excelExportVO.getData(), List.class, Map.class);
        AgeBalanceExcelField field = new AgeBalanceExcelField();
        field.setCorpName(excelExportVO.getCorpName());
        field.setQj(excelExportVO.getQj());
        DynamicAttributeVO[] dynamicAttributeVOS = new DynamicAttributeVO[dataList.size()];

        for(int i = 0; i < dataList.size(); i++){
            dynamicAttributeVOS[i] = new DynamicAttributeVO(dataList.get(i));
        }

        field.setExpvos(dynamicAttributeVOS);

        Fieldelement[] fieldelements = JsonUtils.deserialize(excelExportVO.getFields(), Fieldelement[].class);
        field.setFields(fieldelements);
        field.setCreator(userVO.getUser_name());
        OutputStream toClient = null;
        Excelexport2003<DynamicAttributeVO> lxs = new Excelexport2003<DynamicAttributeVO>();
        try {
            response.reset();
            String fileName = field.getExcelport2003Name();
            String formattedName = URLEncoder.encode(fileName, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ";filename*=UTF-8''" + formattedName);
            toClient = new BufferedOutputStream(response.getOutputStream());
            lxs.exportExcel(field, toClient);
            toClient.flush();
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("excel导出错误",e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (IOException e) {
                log.error("excel导出错误",e);
            }
            try {
                if(response!=null && response.getOutputStream() != null){
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("excel导出错误",e);
            }
        }
    }
}
