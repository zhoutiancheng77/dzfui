package com.dzf.zxkj.platform.controller.zcgl;

import com.dzf.zxkj.common.base.BaseController;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.zcgl.AssetCleanVO;
import com.dzf.zxkj.platform.model.zcgl.AssetQueryCdtionVO;
import com.dzf.zxkj.platform.query.zcgl.ZczjmxPrintParamVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.zcgl.IAssetCleanService;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.itextpdf.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("am_zcqlact")
public class AssetClsController extends BaseController {
    @Autowired
    private IAssetCleanService am_assetclsserv;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    // 查询
    @PostMapping("query")
    public ReturnData<Grid> query(@MultiRequestBody AssetQueryCdtionVO qryVO) {
        Grid grid = new Grid();
        try {
            if (qryVO != null) {
                List<AssetCleanVO> list = am_assetclsserv.query(
                        SystemUtil.getLoginCorpId(), qryVO);
                if (list != null && list.size() > 0) {
                    grid.setTotal((long) list.size());
                    grid.setRows(list);
                    grid.setSuccess(true);
                    grid.setMsg("查询成功！");
                }
                writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL,"资产清理查询", ISysConstants.SYS_2);
            }
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
    public void printAction(@MultiRequestBody ZczjmxPrintParamVO printParamVO, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        try {
            Map<String, String> pmap = new HashMap<String, String>(10);// 声明一个map用来存前台传来的设置参数

            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);

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
                printReporUtil.setIscross(DZFBoolean.TRUE);// 是否横向
            } else {
                printReporUtil.setIscross(DZFBoolean.FALSE);// 是否横向
            }
            AssetCleanVO[] bodyvos = JsonUtils.deserialize(printParamVO.getData(), AssetCleanVO[].class);

            Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存title
            tmap.put("公司", bodyvos[0].getGs());
            tmap.put("期间", bodyvos[0].getTitlePeriod());

            printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "资产清理",
                    new String[] { "pk_assetcard_name", "businessdate",
                            "istogl", "voucherno" }, new String[] { "资产卡片",
                            "清理日期", "转凭证", "凭证号" }, new int[] { 3, 3, 3, 3 },
                    20, printParamVO.getType(), pmap, tmap);
        } catch (DocumentException e) {
            log.error("资产清理打印失败", e);
        } catch (IOException e) {
            log.error("资产清理打印失败", e);
        }
    }


    /**
     * 转总账
     *
     * @throws Exception
     */
    @PostMapping("onBoToGL")
    public ReturnData<Grid> onBoToGL(@MultiRequestBody AssetCleanVO vo, @MultiRequestBody CorpVO corpVO) {
        Grid json = new Grid();
            try {
                if (vo.getIstogl() != null && vo.getIstogl().booleanValue()) {
                    json.setMsg("资产清理单已经转总账，不允许重复操作");
                    json.setSuccess(false);
                } else {
                    am_assetclsserv.insertToGL(SystemUtil.getLoginDate(),
                            corpVO, vo);
                    json.setSuccess(true);
                    json.setMsg("转总账成功。");
                    writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL,"资产清理转总账",ISysConstants.SYS_2);
                }
            } catch (Exception e) {
                // json.setSuccess(false);
                // json.setMsg("转总账失败:"+e.getMessage());
                printErrorLog(json, e, "转总账失败");
                log.error("转总账失败", e);
            }

        return ReturnData.ok().data(json);
    }

    @PostMapping("delete")
    public ReturnData<Grid> delete(@MultiRequestBody AssetCleanVO data) {
        Grid json = new Grid();
            try {
                AssetCleanVO vo = am_assetclsserv.queryById(data
                        .getPrimaryKey());
                if (!vo.getPk_corp().equals(SystemUtil.getLoginCorpId()))
                    throw new BusinessException("只能操作当前登录公司权限内的数据");
                am_assetclsserv.delete((AssetCleanVO) data);
                json.setSuccess(true);
                json.setRows(data);
                json.setMsg("删除成功");
                writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL,"资产清理删除",ISysConstants.SYS_2);
            } catch (Exception e) {
                printErrorLog(json, e, "删除失败");
                log.error("删除失败", e);
            }

        return ReturnData.ok().data(json);
    }
}
