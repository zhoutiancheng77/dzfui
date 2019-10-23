package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.report.YyFpVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.excel.cwbb.YyFpExcelField;
import com.dzf.zxkj.report.service.cwbb.IYyFpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("gl_rep_yyfpact")
public class YyfpController {

    @Autowired
    private IYyFpService yyfpser;

    @Reference
    private IZxkjPlatformService zxkjPlatformService;

    @GetMapping("query")
    public ReturnData<Grid> query(QueryParamVO queryParamVO, @MultiRequestBody CorpVO corpVO) {

        Grid grid = new Grid();
        try {

            if (queryParamVO.getPk_corp() == null || queryParamVO.getPk_corp().trim().length() == 0) {
                // 如果编制单位为空则取当前默认公司
                queryParamVO.setPk_corp(corpVO.getPk_corp());
            }

            List<YyFpVO> list = yyfpser.queryList(queryParamVO);
            grid.setSuccess(true);
            grid.setRows(list);
            grid.setMsg("查询成功");
        } catch (DZFWarpException e) {
            e.printStackTrace();
            log.error("操作失败", e);
            grid.setMsg("操作失败");
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("export/excel")
    public void export(String list, String corpName, String period, @MultiRequestBody UserVO userVO, HttpServletResponse response) throws IOException {
        YyFpVO[] listVo = JsonUtils.deserialize(list, YyFpVO[].class);

        Excelexport2003<YyFpVO> lxs = new Excelexport2003<YyFpVO>();
        YyFpExcelField yhd = new YyFpExcelField();
        yhd.setYwhdvos(listVo);
        yhd.setQj(period);
        yhd.setCreator(userVO.getCuserid());
        yhd.setCorpName(corpName);

        OutputStream toClient = null;
        try {
            response.reset();
            String filename = yhd.getExcelport2003Name();
            String formattedName = URLEncoder.encode(filename, "UTF-8");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            lxs.exportExcel(yhd, toClient);
            toClient.flush();
            response.getOutputStream().flush();

        } catch (IOException e) {
            log.error("excel导出错误", e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (IOException e) {
                log.error("excel导出错误", e);
            }
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("excel导出错误", e);
            }
        }
    }

}
