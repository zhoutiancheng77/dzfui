package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.report.FzYebVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.service.cwzb.IFzhsYebReport;
import com.dzf.zxkj.report.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 辅助余额表
 *
 */
@RestController
@RequestMapping("gl_rep_fzyebact")
@Slf4j
public class AuxiliaryBalanceReportController {
    @Autowired
    private IFzhsYebReport gl_rep_fzyebserv;

    /**
     * 查询辅助余额表数据
     */
    @GetMapping("/queryAction")
    public ReturnData queryAction(@RequestParam Map<String, String> param) {
        Grid grid = new Grid();

        KmReoprtQueryParamVO queryParam = JsonUtils.convertValue(param, KmReoprtQueryParamVO.class);

        CorpVO corpVo = SystemUtil.getLoginCorpVo();
        DZFDate beginDate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(corpVo.getBegindate()));
        if (beginDate.after(queryParam.getBegindate1())) {
            throw new BusinessException("开始日期不能在建账日期(" + DateUtils.getPeriod(beginDate) + ")前!");
        }
        if (StringUtils.isBlank(queryParam.getPk_corp())) {
            queryParam.setPk_corp(corpVo.getPk_corp());
        }

        List<FzYebVO> fzyevoList = gl_rep_fzyebserv.getFzYebVOs(queryParam);
        if (fzyevoList != null && fzyevoList.size() > 0) {
            grid.setSuccess(true);
            grid.setTotal(fzyevoList == null ? 0 : (long) fzyevoList.size());
            grid.setRows(fzyevoList == null ? new ArrayList<FzYebVO>() : fzyevoList);
        } else {
            grid.setSuccess(false);
            grid.setRows(new ArrayList<FzYebVO>());
            grid.setMsg("查询为空!");
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 导出Excel
     */
    /*@PostMapping("/excelReport")
    public void excelReport(@RequestParam Map<String, String> param, HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            String fileName = "辅助余额表-" + ReportUtil.formatQj("2019-01") + ".xls";
            String formattedName = URLEncoder.encode(fileName, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ";filename*=UTF-8''" + formattedName);

            String strlist = param.get("list");
            List<FzYebVO> listVo = JsonUtils.deserialize(strlist, List.class, FzYebVO.class);
            EasyExcel.write(response.getOutputStream(), FzYebVO.class).registerConverter(new DZFDoubleConverter())
                    .sheet("模板").doWrite(listVo);


        } catch (IOException e) {
            log.error("excel导出错误", e);
        } finally {
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("excel导出错误", e);
            }
        }

    }*/
}
