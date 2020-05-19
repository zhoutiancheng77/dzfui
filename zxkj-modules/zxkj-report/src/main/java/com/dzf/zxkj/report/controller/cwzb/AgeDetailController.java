package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.DynamicAttributeVO;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
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
import com.dzf.zxkj.report.excel.cwzb.AgeDetailExcelField;
import com.dzf.zxkj.report.service.cwzb.IAgeDetailReportService;
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
@RequestMapping("gl_rep_zlmxbact")
@Slf4j
public class AgeDetailController extends BaseController {

    @Autowired
    private IAgeDetailReportService gl_rep_zlmxb;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @PostMapping("query")
    public ReturnData<Grid> query (@MultiRequestBody AgeReportQueryVO ageReportQueryVO) {
        Grid json = new Grid();
        try {

            if(StringUtil.isEmptyWithTrim(ageReportQueryVO.getPk_corp())){
                ageReportQueryVO.setPk_corp(SystemUtil.getLoginCorpId());
            }
            // 校验
            checkSecurityData(null, new String[]{ageReportQueryVO.getPk_corp()},null);

            CorpVO corpVO = zxkjPlatformService.queryCorpByPk(ageReportQueryVO.getPk_corp());

            if (corpVO.getBegindate().after(ageReportQueryVO.getEnd_date())) {
                throw new BusinessException("截止日期不能早于建账日期");
            }
            ageReportQueryVO.setJz_date(corpVO.getBegindate());

            if (ageReportQueryVO.getFzlb() != null && ageReportQueryVO.getFzlb() > 0) {
                ageReportQueryVO.setAuaccount_type("fzhsx" + ageReportQueryVO.getFzlb());
            }

            AgeReportResultVO rs = gl_rep_zlmxb.query(ageReportQueryVO);
            json.setRows(rs);
            json.setSuccess(true);
            json.setMsg("查询成功");
            writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT, "往来账龄明细账查询：" + ageReportQueryVO.getEnd_date(), ISysConstants.SYS_2);
        } catch (Exception e) {
            printErrorLog(json,e, "查询失败");
        }
        return ReturnData.ok().data(json);
    }


    @PostMapping("export/excel")
    public void excelReport (@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        List<LinkedHashMap<String, Object>>  dataList =  JsonUtils.deserialize(excelExportVO.getData(), List.class, Map.class);
        AgeDetailExcelField field = new AgeDetailExcelField();
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
            writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT, "往来账龄明细账导出：" + excelExportVO.getQj(), ISysConstants.SYS_2);
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
