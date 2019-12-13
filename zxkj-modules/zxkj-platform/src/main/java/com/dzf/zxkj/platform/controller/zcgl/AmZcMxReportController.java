package com.dzf.zxkj.platform.controller.zcgl;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.zcgl.ZcMxZVO;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("am_rep_zcmxact")
@Slf4j
public class AmZcMxReportController extends BaseController {
    @Autowired
    private IAssetcardReport am_rep_zcmxserv;
    @Autowired
    private IUserService userService;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;
    /**
     * 查询科目明细数据
     */
    @PostMapping("query")
    public ReturnData<Grid> queryAction(@MultiRequestBody QueryParamVO queryParamvo) {
        Grid grid = new Grid();
        try {
            ZcMxZVO[] zcmxvos = am_rep_zcmxserv
                    .queryAssetcardDetail(queryParamvo);
            grid.setTotal((long) (zcmxvos == null ? 0 : zcmxvos.length));
            grid.setRows(zcmxvos == null ? new ArrayList<ZcMxZVO>() : Arrays.asList(zcmxvos));
            grid.setSuccess(true);
            grid.setMsg("查询成功");
            writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL, "资产明细账查询", ISysConstants.SYS_2);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("print/pdf")
    public void printAction(@MultiRequestBody PrintParamVO param, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        try {

            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);

            String strlist = param.getList();
            if (strlist == null) {
                return;
            }

            Map<String, String> pmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            pmap.put("type", param.getType());
            pmap.put("pageOrt", param.getPageOrt());
            pmap.put("left", param.getLeft());
            pmap.put("top", param.getTop());
            pmap.put("printdate", param.getPrintdate());
            pmap.put("font", param.getFont());
            pmap.put("pageNum", param.getPageNum());

            ZcMxZVO[] bodyvos = JsonUtils.deserialize(param.getList(), ZcMxZVO[].class);
            printReporUtil.setIscross(DZFBoolean.TRUE);// 是否横向
            printReporUtil.setLineheight(22f);
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(param.getFont()), Font.NORMAL));//设置表头字体
            Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存title
            tmap.put("公司", param.getCorpName());
            tmap.put("期间", param.getPeriod());
            printReporUtil.printHz(null, bodyvos, "资 产 明 细 账",
                    new String[]{"rq", "zcbh", "zcmc", "zclb", "zcsx",
                            "pzzh", "zy", "yzjf", "yzdf", "yzye", "ljjf",
                            "ljdf", "ljye", "jzye"}, new String[]{"日期",
                            "资产编码", "资产名称", "资产类别", "资产属性", "凭证号", "摘要",
                            "固定资产借方", "固定资产贷方", "固定资产余额", "累计折旧借方", "累计折旧贷方",
                            "累计折旧余额", "净值余额"}, new int[]{4, 4, 4, 7, 4, 3,
                            4, 4, 4, 4, 4, 4, 4, 4}, 20,
                    pmap, tmap);
        } catch (DocumentException e) {
            log.error("资产明细账打印失败", e);
        } catch (IOException e) {
            log.error("资产明细账打印失败", e);
        }
    }
}
