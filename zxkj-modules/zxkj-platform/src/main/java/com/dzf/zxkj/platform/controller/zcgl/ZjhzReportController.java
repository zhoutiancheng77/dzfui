package com.dzf.zxkj.platform.controller.zcgl;

import com.dzf.zxkj.common.base.BaseController;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.excel.zcgl.ZchzExcelField;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;
import com.dzf.zxkj.platform.model.zcgl.ZjhzbReportVO;
import com.dzf.zxkj.platform.query.zcgl.ZczjmxPrintParamVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.service.zcgl.IZjhzbReportSerice;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
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
import java.util.*;

@SuppressWarnings("all")
@RequestMapping("am_rep_zjhzbact")
@Slf4j
@RestController
public class ZjhzReportController extends BaseController {

    @Autowired
    private IZjhzbReportSerice zjhzbRepSer;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;
    @Autowired
    private IUserService userService;

    @RequestMapping("query")
    public ReturnData<Grid> query(@MultiRequestBody QueryParamVO queryParamvo) {

        Grid json = new Grid();
        json.setSuccess(false);

        try {
            DZFDate startdate = DateUtils.getPeriodStartDate(queryParamvo.getPeriod());
            DZFDate enddate = DateUtils.getPeriodEndDate(queryParamvo.getPeriod());
            queryParamvo.setBegindate1(startdate);
            queryParamvo.setEnddate(enddate);
            if (StringUtil.isEmpty(queryParamvo.getPk_corp())) {
                queryParamvo.setPk_corp(SystemUtil.getLoginCorpId());
            }
            List<ZjhzbReportVO> zjhbzList = zjhzbRepSer.queryZjhzb(queryParamvo);
            //插入合计，小计
            zjhbzList = appendTotal(zjhbzList);
            if (zjhbzList.size() == 0) {
                json.setMsg("无数据！");
                json.setRows(zjhbzList);
            } else {
                json.setSuccess(true);
                json.setMsg("查询成功！");
                json.setRows(zjhbzList);
            }

            writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL, "折旧汇总表查询", ISysConstants.SYS_2);
        } catch (Exception e) {
            json.setRows(new ArrayList<ZjhzbReportVO>());
            printErrorLog(json, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(json);
    }

    // 导出Excel
    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody String strlist, @MultiRequestBody("corpName") String gs, @MultiRequestBody String qj, HttpServletResponse response) {
        AssetDepreciaTionVO[] listVo = JsonUtils.deserialize(strlist, AssetDepreciaTionVO[].class);
        Excelexport2003<AssetDepreciaTionVO> lxs = new Excelexport2003<>();
        ZchzExcelField zcz = new ZchzExcelField();
        zcz.setAssdetivos(listVo);
        zcz.setQj(qj);
        zcz.setCreator(SystemUtil.getLoginUserId());
        zcz.setCorpName(gs);

        OutputStream toClient = null;
        try {
            response.reset();
            String filename = zcz.getExcelport2003Name();
            String formattedName = URLEncoder.encode(filename, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            lxs.exportExcel(zcz, toClient);
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
                if (response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("excel导出错误", e);
            }
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL, "折旧汇总表导出" + qj, ISysConstants.SYS_2);
    }

    /**
     * 打印操作
     */
    @PostMapping("print/pdf")
    public void printAction(@MultiRequestBody ZczjmxPrintParamVO printParamVO, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        try {

            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);

            Map<String, String> pmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            pmap.put("type", printParamVO.getType());
            pmap.put("pageOrt", printParamVO.getPageOrt());
            pmap.put("left", printParamVO.getLeft());
            pmap.put("top", printParamVO.getTop());
            pmap.put("printdate", printParamVO.getPrintdate());
            pmap.put("font", printParamVO.getFont());
            if (printParamVO.getData() == null) {
                return;
            }
            //设置是否横向
            printReporUtil.setIscross(printParamVO.getPageOrt().equals("Y") ? DZFBoolean.TRUE : DZFBoolean.FALSE);

            AssetDepreciaTionVO[] bodyvos = JsonUtils.deserialize(printParamVO.getData(), AssetDepreciaTionVO[].class);
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(printParamVO.getFont()), Font.NORMAL));//设置表头字体
            Map<String, String> tmap = new LinkedHashMap<String, String>();// 声明一个map用来存title
            tmap.put("公司", bodyvos[0].getGs());
            tmap.put("期间", bodyvos[0].getTitlePeriod());
            String title = "折 旧 汇 总 表";

            printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos, title,
                    new String[]{"assetproperty", "catename", "assetmny", "originalvalue", "depreciationmny",
                            "assetnetmny"},
                    new String[]{"资产属性", "资产类别", "资产原值", "本期折旧额", "累计折旧额", "资产净值"},
                    new int[]{3, 3, 3, 3, 3, 3}, 20, pmap, tmap);

        } catch (DocumentException e) {
            log.error("折旧汇总打印失败", e);
        } catch (IOException e) {
            log.error("折旧汇总表打印失败", e);
        }
    }

    private List<ZjhzbReportVO> appendTotal(List<ZjhzbReportVO> zjhbzList) {
        if (zjhbzList == null || zjhbzList.size() == 0) {
            return zjhbzList;
        }

        List<ZjhzbReportVO> resvos = new ArrayList<ZjhzbReportVO>();

        // --------类型
        DZFDouble assetmny_lx = DZFDouble.ZERO_DBL;
        DZFDouble depreciationmny_lx = DZFDouble.ZERO_DBL;
        DZFDouble assetnetmny_lx = DZFDouble.ZERO_DBL;
        DZFDouble originalvalue_lx = DZFDouble.ZERO_DBL;

        // --------合计
        DZFDouble assetmny_hj = DZFDouble.ZERO_DBL;
        DZFDouble depreciationmny_hj = DZFDouble.ZERO_DBL;
        DZFDouble assetnetmny_hj = DZFDouble.ZERO_DBL;
        DZFDouble originalvalue_hj = DZFDouble.ZERO_DBL;

        Integer assetproperty = 0;
        ZjhzbReportVO vo = null;
        for (int i = 0; i < zjhbzList.size(); i++) {
            vo = zjhbzList.get(i);
            if (vo.getAssetproperty().intValue() != assetproperty.intValue()) {
                if (i != 0) {
                    ZjhzbReportVO temp = getHjVO(assetmny_lx, depreciationmny_lx, assetnetmny_lx, originalvalue_lx,
                            assetproperty + "");
                    resvos.add(temp);
                }
                assetmny_lx = DZFDouble.ZERO_DBL;
                depreciationmny_lx = DZFDouble.ZERO_DBL;
                assetnetmny_lx = DZFDouble.ONE_DBL;
                originalvalue_lx = DZFDouble.ZERO_DBL;
            }
            // ------类型合计
            assetmny_lx = SafeCompute.add(assetmny_lx, getDzfDouble(vo.getAssetmny()));
            depreciationmny_lx = SafeCompute.add(depreciationmny_lx, getDzfDouble(vo.getDepreciationmny()));
            assetnetmny_lx = SafeCompute.add(assetnetmny_lx, getDzfDouble(vo.getAssetnetmny()));
            originalvalue_lx = SafeCompute.add(originalvalue_lx, getDzfDouble(vo.getOriginalvalue()));

            // ----总的合计
            assetmny_hj = SafeCompute.add(assetmny_hj, getDzfDouble(vo.getAssetmny()));
            depreciationmny_hj = SafeCompute.add(depreciationmny_hj, getDzfDouble(vo.getDepreciationmny()));
            assetnetmny_hj = SafeCompute.add(assetnetmny_hj, getDzfDouble(vo.getAssetnetmny()));
            originalvalue_hj = SafeCompute.add(originalvalue_hj, getDzfDouble(vo.getOriginalvalue()));

            assetproperty = vo.getAssetproperty();// 资产属性

            resvos.add(vo);
            if (i == zjhbzList.size() - 1) {
                ZjhzbReportVO temp = getHjVO(assetmny_lx, depreciationmny_lx, assetnetmny_lx, originalvalue_lx,
                        assetproperty + "");
                resvos.add(temp);
            }

        }

        ZjhzbReportVO temp = getHjVO(assetmny_hj, depreciationmny_hj, assetnetmny_hj, originalvalue_hj, "total");
        resvos.add(temp);

        return resvos;
    }

    private ZjhzbReportVO getHjVO(DZFDouble assetmny_lx, DZFDouble depreciationmny_lx, DZFDouble assetnetmny_lx,
                                  DZFDouble originalvalue_lx, String assetproperty) {
        ZjhzbReportVO temp = new ZjhzbReportVO();
        temp.setCatecode("");
        temp.setAssetproperty(-1);//默认合计/小计
        temp.setCatename(getAssetProperty(assetproperty));
        temp.setAssetmny(assetmny_lx.toString());
        temp.setDepreciationmny(depreciationmny_lx.toString());
        temp.setAssetnetmny(assetnetmny_lx.toString());
        temp.setOriginalvalue(originalvalue_lx.toString());
        return temp;
    }

    private String getAssetProperty(String assetProperty) {
        if ("0".equals(assetProperty))
            return "固定资产小计";
        else if ("1".equals(assetProperty))
            return "无形资产小计";
        else if ("3".equals(assetProperty))
            return "待摊费用小计";
        else if ("total".equals(assetProperty)) {
            return "合计";
        } else
            return "";
    }

    private DZFDouble getDzfDouble(String value) {
        if (StringUtil.isEmpty(value)) {
            return DZFDouble.ZERO_DBL;
        }

        return new DZFDouble(value);
    }
}
