package com.dzf.zxkj.platform.controller.zcgl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.FieldMapping;
import com.dzf.zxkj.common.base.BaseController;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.zcgl.ZcZzVO;
import com.dzf.zxkj.platform.query.zcgl.ZczzPrintParamVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.service.zcgl.IAssetcardReport;
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
import java.util.LinkedList;
import java.util.Map;

@RestController
@RequestMapping("am_rep_zczzact")
@Slf4j
public class ZczzReportController extends BaseController {
    @Autowired
    private IAssetcardReport am_rep_zczzserv;
    @Autowired
    private IUserService userService;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    /**
     * 查询增值税和营业税月度申报对比表数据
     */
    @PostMapping("query")
    public ReturnData<Grid> queryAction(@MultiRequestBody QueryParamVO queryParamvo) {
        Grid grid = new Grid();
        try {
            String zxlb = queryParamvo.getPk_assetcategory();
            String zcsx = queryParamvo.getZcsx();
            String zcmc = queryParamvo.getAsname();

            if (queryParamvo.getBegindate1() == null
                    || queryParamvo.getEnddate() == null) {
                throw new BusinessException("查询开始日期，结束日期不能为空!");
            }

            if (queryParamvo.getBegindate1().after(queryParamvo.getEnddate())) {
                throw new BusinessException("查询开始日期,应在结束日期之前!");
            }

            ZcZzVO[] zczzvo = am_rep_zczzserv.queryAssetcardTotal(
                    queryParamvo.getPk_corp(), queryParamvo.getBegindate1(),
                    queryParamvo.getEnddate(), "", new SQLParameter(), zxlb,
                    zcsx, zcmc);
            grid.setTotal((long) (zczzvo == null ? 0 : zczzvo.length));
            if (zczzvo != null && zczzvo.length > 0) {
                grid.setRows(Arrays.asList(zczzvo));
            }
            grid.setSuccess(true);
            writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL, "资产总账查询", ISysConstants.SYS_2);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 打印操作
     */
    @PostMapping("print/pdf")
    public void printAction(@MultiRequestBody ZczzPrintParamVO param, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {

        PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);

        try {
            String strlist = param.getList();
            if (strlist == null) {
                return;
            }
            String zcsx = param.getZcsx();
            Map<String, String> pmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            pmap.put("type", param.getType());
            pmap.put("pageOrt", param.getPageOrt());
            pmap.put("left", param.getLeft());
            pmap.put("top", param.getTop());
            pmap.put("printdate", param.getPrintdate());
            pmap.put("font", param.getFont());
            pmap.put("pageNum", param.getPageNum());

            JSONArray array = JSON.parseArray(strlist);
            Map<String, String> bodymapping = FieldMapping
                    .getFieldMapping(new ZcZzVO());
            String title1 = null;
            String title2 = null;
            if (zcsx != null && zcsx != "") {
                if (zcsx.equals("0") || zcsx.equals("2")) {
                    title1 = "固定资产";
                    title2 = "累计折旧";
                } else if (zcsx.equals("1")) {
                    title1 = "无形资产";
                    title2 = "累计摊销";
                } else if (zcsx.equals("3")) {
                    title1 = "待摊费用";
                    title2 = "累计摊销";
                }
            }

            ZcZzVO[] bodyvos = JsonUtils.deserialize(param.getData(), ZcZzVO[].class);

            Map<String, String> tmap = new HashMap<>();// 声明一个map用来存title
            tmap.put("公司", bodyvos[0].getGs());
            tmap.put("期间", bodyvos[0].getTitlePeriod());

            String[] columnames = new String[]{"期间", "摘要", title1, title2,
                    "净值余额", "借方", "贷方", "余额", "借方", "贷方", "余额"};
            String[] columnkeys = new String[]{"qj", "zy", null, null, "yzjf", "yzdf",
                    "yzye", "ljjf", "ljdf", "ljye", "jzye"};
            LinkedList<ColumnCellAttr> columnlist = new LinkedList<>();
            int len = columnames.length;
            ColumnCellAttr attr = null;
            for (int i = 0; i < len; i++) {
                attr = new ColumnCellAttr();
                attr.setColumname(columnames[i]);
                attr.setColumn(columnkeys[i]);
                if (i == 0 || i == 1 || i == 4) {
                    attr.setRowspan(2);
                } else if (i == 2) {
                    attr.setColspan(3);
                } else if (i == 3) {
                    attr.setColspan(3);
                }

                columnlist.add(attr);
            }
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(param.getFont()), Font.NORMAL));//设置表头字体
            if ("1".equals(param.getType()))
                printReporUtil.printGroup(null, bodyvos, "资 产 总 账", columnkeys, columnames,
                        columnlist, new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1}, 0,
                        null, pmap, tmap);// A4纸张打印
            else if ("2".equals(param.getType())) {
                printReporUtil.printB5(null, bodyvos, "资 产 总 账", columnkeys, columnames, columnlist,
                        new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1}, 0, null, pmap, tmap);
            }
            // printGroup(new HashMap<String, List<SuperVO>>(),bodyvos,
            // "资 产 总 账", columnkeys, null, columnlist, new
            // int[]{1,1,1,1,1,1,1,1,1}, 0);
        } catch (DocumentException e) {
            log.error("资产总账打印失败", e);
        } catch (IOException e) {
            log.error("资产总账打印失败", e);
        }
    }
}
