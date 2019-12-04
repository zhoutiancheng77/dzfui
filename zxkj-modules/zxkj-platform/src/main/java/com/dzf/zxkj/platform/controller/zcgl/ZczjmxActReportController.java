package com.dzf.zxkj.platform.controller.zcgl;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.excel.zcgl.ZczjmxExcelField;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;
import com.dzf.zxkj.platform.model.zcgl.DepreciationVO;
import com.dzf.zxkj.platform.query.zcgl.ZcQueryParamVO;
import com.dzf.zxkj.platform.query.zcgl.ZczjmxPrintParamVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.service.zcgl.IDepreciationService;
import com.dzf.zxkj.platform.service.zcgl.IZczjmxReport;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

@RestController
@RequestMapping("am_rep_zczjmxact")
@SuppressWarnings("all")
@Slf4j
public class ZczjmxActReportController extends BaseController {
    @Autowired
    private IDepreciationService am_rep_checkvoucher;
    @Autowired
    private IZczjmxReport am_rep_zczjmxserv;

    @Autowired
    private IUserService userService;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    /**
     * 查询科目明细数据
     */
    @PostMapping("/query")
    public ReturnData<Grid> queryAction(@MultiRequestBody(required = false) ZcQueryParamVO zcQueryParamVO, @MultiRequestBody QueryParamVO queryParamvo) {
        Grid grid = new Grid();
        try {
            if (queryParamvo.getPk_corp() == null || queryParamvo.getPk_corp().trim().length() == 0) {
                // 如果编制单位为空则取当前默认公司
                queryParamvo.setPk_corp(SystemUtil.getLoginCorpId());
            }

            AssetDepreciaTionVO[] assetdepreciationvos = null;//gl_rep_fsyebserv.getKMZZVOs(vo);

            if (zcQueryParamVO.getComeFrom() != null) {//从折旧汇总表跳转过来的，结束日期根据传过来的年月添加
                assetdepreciationvos = am_rep_zczjmxserv.getZczjMxVOsByHz(queryParamvo, zcQueryParamVO.getCatecode(), zcQueryParamVO.getCatename());
            } else {
                assetdepreciationvos = am_rep_zczjmxserv.getZczjMxVOs(queryParamvo, zcQueryParamVO.getAsset_id());
            }
            log.info("查询成功！");
            grid.setTotal(assetdepreciationvos == null ? 0 : (long) Arrays.asList(assetdepreciationvos).size());
            grid.setRows(assetdepreciationvos == null ? null : Arrays.asList(assetdepreciationvos));
            grid.setSuccess(true);
            grid.setMsg("查询成功");
            writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL, "折旧明细账查询");
        } catch (Exception e) {
            grid.setRows(new ArrayList<AssetDepreciaTionVO>());
            printErrorLog(grid, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 资产卡片联查资产折旧明细
     */
    @PostMapping("link")
    public ReturnData<Grid> linkAction(@MultiRequestBody QueryParamVO paramvo) {
        Grid grid = new Grid();
        try {
            AssetDepreciaTionVO[] vos = am_rep_zczjmxserv.linkZczjMxVOs(paramvo);
            log.info("查询成功！");
            if (vos != null && vos.length > 0) {
                grid.setTotal((long) Arrays.asList(vos).size());
                grid.setRows(Arrays.asList(vos));
                grid.setMsg("查询成功！");
            }
        } catch (Exception e) {
            printErrorLog(grid,  e, "联查失败");
            log.error("联查失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 打印操作
     */
    @PostMapping("print/pdf")
    public void printAction(ZczjmxPrintParamVO printParamVO, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
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
            if (printParamVO.getPageOrt().equals("Y")) {
                printReporUtil.setIscross(DZFBoolean.TRUE);//是否横向
            } else {
                printReporUtil.setIscross(DZFBoolean.FALSE);//是否横向
            }
            printReporUtil.setIspaging(printParamVO.getIsPaging());
            AssetDepreciaTionVO[] bodyvos = JsonUtils.deserialize(printParamVO.getData(), AssetDepreciaTionVO[].class);
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(printParamVO.getFont()), Font.NORMAL));//设置表头字体
            Map<String, String> tmap = new LinkedHashMap<String, String>();// 声明一个map用来存title
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getPeriod());
            String title = "折 旧 明 细 账";
            if (printParamVO.getIsPaging() != null && "Y".equals(printParamVO.getIsPaging())) {//分页打印
                //转换map
                Map<String, List<SuperVO>> maps = new LinkedHashMap<>();
                String assetcode = "";
                for (AssetDepreciaTionVO vo : bodyvos) {
                    assetcode = vo.getAssetcode();
                    if ("0".equals(vo.getAssetproperty()) || "2".equals(vo.getAssetproperty())) {//固定资产
                        vo.setZy("计提折旧");
                    } else if (!StringUtil.isEmpty(vo.getAssetproperty())) {
                        vo.setZy("计提摊销");
                        title = "资 产 摊 销 明 细 账";
                    }
                    if (StringUtil.isEmpty(assetcode)) {
                        continue;
                    }
                    if (maps.containsKey(assetcode)) {
                        maps.get(assetcode).add(vo);
                    } else {
                        List<SuperVO> list = new ArrayList<SuperVO>();
                        list.add(vo);
                        maps.put(assetcode, list);
                    }
                }
                printReporUtil.printHz(maps, new SuperVO[]{}, title,
                        new String[]{"zy", "businessdate", "assetmny", "depreciationmny", "assetnetmny",
                                "originalvalue"},
                        new String[]{"摘要", "折旧日期", "资产原值", "累计折旧", "资产净值", "本期折旧"},
                        new int[]{3, 3, 3, 3, 3, 3}, 20,  pmap, tmap);
            } else {
                printReporUtil.setLineheight(22f);
                printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), filter(bodyvos, printParamVO.getXjtotal(), printParamVO.getHjtotal()), title,
                        new String[]{"catename", "assetcode", "assetname", "businessdate",
                                "uselimit", "assetmny", "depreciationmny", "assetnetmny",
                                "originalvalue"},
                        new String[]{"类别", "资产编码", "资产名称", "折旧日期",
                                "预计使用年限", "资产原值", "累计折旧", "资产净值",
                                "本期折旧"},
                        new int[]{
                                "N".equals(printParamVO.getLb()) ? 0 : 3,
                                "N".equals(printParamVO.getZcbm()) ? 0 : 2,
                                4,
                                "N".equals(printParamVO.getZjrq()) ? 0 : 3,
                                "N".equals(printParamVO.getZjnx()) ? 0 : 3,
                                4, 4, 4, 4}, 20,  pmap, tmap);
            }
        } catch (DocumentException e) {
            log.error("折旧明细账打印失败", e);
        } catch (IOException e) {
            log.error("折旧明细账打印失败", e);
        }
    }


    private AssetDepreciaTionVO[] filter(AssetDepreciaTionVO[] bodyvos, String xjtotal, String hjtotal) {
        List<AssetDepreciaTionVO> list = new ArrayList<AssetDepreciaTionVO>();
        if (bodyvos != null && bodyvos.length > 0) {
            for (AssetDepreciaTionVO body : bodyvos) {
                if (StringUtil.isEmpty(body.getCatename())
                        && StringUtil.isEmpty(body.getAssetcode())) {
                    if ("小计".equals(body.getAssetname()) && "N".equals(xjtotal)) {
                        continue;
                    }
                    if ("合计".equals(body.getAssetname()) && "N".equals(hjtotal)) {
                        continue;
                    }
                }
                list.add(body);
            }
        }
        return list.toArray(new AssetDepreciaTionVO[0]);
    }

    /**
     * 删除折旧明细
     */
    @PostMapping("delete/{pk}")
    public ReturnData<Grid> delete(@PathVariable("pk") String pk) {
        Grid grid = new Grid();
        List<AssetDepreciaTionVO> listdepvo = new ArrayList<AssetDepreciaTionVO>();
        try {
            AssetDepreciaTionVO vo = am_rep_zczjmxserv.queryById(pk);
            if (!vo.getPk_corp().equals(SystemUtil.getLoginCorpId()))
                throw new BusinessException("只能操作当前登录公司权限内的数据");
            am_rep_zczjmxserv.deleteZjmx(pk, "");
            grid.setMsg("删除成功!");
            grid.setSuccess(true);
            grid.setRows(listdepvo);
            grid.setTotal((long) 0);
        } catch (Exception e) {
            grid.setRows(listdepvo);
            grid.setTotal((long) 0);
            printErrorLog(grid, e, "删除失败");
            log.error("删除失败", e);
        }
        return ReturnData.ok().data(grid);
    }


    /**
     * 生成凭证的处理逻辑
     */
    @PostMapping("genVoucher")
    public ReturnData<Grid> genVoucher(String pk, @MultiRequestBody CorpVO corpVO) {

        Grid grid = new Grid();
        List<AssetDepreciaTionVO> listdepvo = new ArrayList<AssetDepreciaTionVO>();
        try {
            AssetDepreciaTionVO vo = am_rep_zczjmxserv.queryById(pk);
            if (!vo.getPk_corp().equals(SystemUtil.getLoginCorpId()))
                throw new BusinessException("只能操作当前登录公司权限内的数据");
            AssetDepreciaTionVO depvo = am_rep_zczjmxserv.insertVoucher(corpVO, pk);
            listdepvo.add(depvo);
            grid.setMsg("生成凭证成功!");
            grid.setSuccess(true);
            grid.setRows(listdepvo);
            grid.setTotal((long) listdepvo.size());
        } catch (Exception e) {
            grid.setRows(listdepvo);
            grid.setTotal((long) 0);
            printErrorLog(grid, e, "生成凭证失败");
            log.error("生成凭证失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("queryAll")
    public ReturnData<Grid> queryAll(String vouvher_id) {
        Grid grid = new Grid();
        try {
            String pk_corp = SystemUtil.getLoginCorpId();
            DepreciationVO[] depreciationvos = null;
            depreciationvos = am_rep_checkvoucher.query(pk_corp, vouvher_id);

            log.info("查询成功！");
            grid.setRows(Arrays.asList(depreciationvos));
            grid.setSuccess(true);

        } catch (Exception e) {
            grid.setRows(new ArrayList<AssetDepreciaTionVO>());
            printErrorLog(grid, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    // 导出Excel
    @PostMapping("export/excel")
    public void excelReport(String strlist, String corpName, String qj, HttpServletResponse response) {
        AssetDepreciaTionVO[] listVo = JsonUtils.deserialize(strlist, AssetDepreciaTionVO[].class);

        Excelexport2003<AssetDepreciaTionVO> lxs = new Excelexport2003<AssetDepreciaTionVO>();
        ZczjmxExcelField zcz = new ZczjmxExcelField();
        zcz.setAssdetivos(listVo);
        zcz.setQj(qj);
        zcz.setCreator(SystemUtil.getLoginUserId());
        zcz.setCorpName(corpName);

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
        writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL, "折旧明细账导出" + qj, ISysConstants.SYS_2);
    }

    private Map<String, String> getExpFieldMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("catename", "类别");
        map.put("assetcode", "资产编码");
        map.put("assetname", "资产名称");
        map.put("accountdate", "使用时间");
        map.put("assetmny", "资产原值");
        map.put("uselimit", "预计使用期间数");
        map.put("salvageratio", "净残值率(%)");
        map.put("businessdate", "折旧日期");
        map.put("originalvalue", "本期折旧");
        map.put("depreciationmny", "累计折旧");
        map.put("assetnetmny", "期末净值");
        map.put("istogl", "转总账");
        map.put("pzh", "凭证号");
        map.put("issettle", "已结账");
        return map;
    }
}
