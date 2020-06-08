package com.dzf.zxkj.platform.controller.zcgl;

import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.controller.PrintAndExcelExportController;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.excel.zcgl.AssetGlComprExcelField;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.zcgl.AssetQueryCdtionVO;
import com.dzf.zxkj.platform.model.zcgl.ZcdzVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.zcgl.IZczzdzReportService;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/am/am_rep_zczzdzbact")
@Slf4j
public class AssetsGlComprReportController extends PrintAndExcelExportController {

    @Autowired
    private IZczzdzReportService zczzdzReportService;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @PostMapping("/query")
    public ReturnData<Grid> query(@MultiRequestBody AssetQueryCdtionVO qryVO, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        try {
            // 校验
            checkSecurityData(null, new String[]{corpVO.getPk_corp()},null);
            ZcdzVO[] zcmxvos = null;
            if (qryVO != null) {
                if (qryVO.getStart_date() == null) {
                    qryVO.setStart_date(new DZFDate());
                }
                String period = DateUtils.getPeriod(qryVO.getStart_date());
                zcmxvos = zczzdzReportService.queryAssetCheckVOs(corpVO.getPk_corp(), period);

            }
            if (zcmxvos != null && zcmxvos.length > 0) {
                grid.setTotal((long) zcmxvos.length);
                grid.setRows(Arrays.asList(zcmxvos));
            }
            grid.setMsg("查询成功");
            grid.setSuccess(true);
            writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL,"资产总账对账表查询", ISysConstants.SYS_2);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
        }

        return ReturnData.ok().data(grid);
    }

    @PostMapping("export/excel")
    public void export(@MultiRequestBody  String list, @MultiRequestBody  String corpName, @MultiRequestBody  String period, @MultiRequestBody UserVO userVO, HttpServletResponse response) throws IOException {
        ZcdzVO[] listVo = JsonUtils.deserialize(list, ZcdzVO[].class);

        Excelexport2003<ZcdzVO> lxs = new Excelexport2003<ZcdzVO>();
        AssetGlComprExcelField yhd = new AssetGlComprExcelField();
        yhd.setVos(listVo);
        yhd.setQj(period);
        yhd.setCreator(userVO.getCuserid());
        yhd.setCorpName(corpName);

        baseExcelExport(response,lxs,yhd);

    }

    @PostMapping("print")
    public void print(@MultiRequestBody String corpName, @MultiRequestBody String period, @MultiRequestBody PrintParamVO printParamVO, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
        try {
            // 校验
            checkSecurityData(null, new String[]{SystemUtil.getLoginCorpId()},null);
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            String strlist = printParamVO.getList();
            if (strlist == null) {
                return;
            }
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);

            if (printParamVO.getPageOrt().equals("Y")) {
                printReporUtil.setIscross(DZFBoolean.TRUE);// 是否横向
            } else {
                printReporUtil.setIscross(DZFBoolean.FALSE);// 是否横向
            }
            ZcdzVO[] bodyvos = JsonUtils.deserialize(printParamVO.getList(), ZcdzVO[].class);

            Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存title
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getPeriod());
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(printParamVO.getFont()), Font.NORMAL));//设置表头字体
            printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos,
                    "总 账 对 账", new String[] { "zcsx", "zclb", "zckm",
                            "zzkmbh", "zzkmmc", "zcje", "zzje" }, new String[] {
                            "资产属性", "资产类别", "资产科目", "总账科目编号", "总账科目名称", "资产金额",
                            "总账" }, new int[] { 1, 1, 1, 1, 1, 1, 1 }, 20,
                     pmap, tmap);
            writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL,"资产总账对账表打印"+printParamVO.getPeriod(), ISysConstants.SYS_2);
        } catch (DocumentException e) {
            log.error("打印错误", e);
        } catch (IOException e) {
            log.error("打印错误", e);
        }
    }

}
