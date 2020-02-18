package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwzb.XjyhrjzExcelField;
import com.dzf.zxkj.report.service.cwzb.IXjRjZReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("gl_rep_xjyhrjzact")
@Slf4j
public class XjyhrjzController extends ReportBaseController {

    @Autowired
    private IXjRjZReport gl_rep_xjyhrjzserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;


    /**
     * 查询科目明细数据
     */
    @PostMapping("/queryAction")
    public ReturnData<Grid> queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        QueryParamVO queryParamvo = getQueryParamVO(queryvo, corpVO);
        try {
            // 校验
            checkSecurityData(null, new String[]{queryParamvo.getPk_corp()},null);
            int page = queryParamvo == null ? 1 : queryParamvo.getPage();
            int rows = queryParamvo == null ? 100000 : queryParamvo.getRows();
            /** 验证 查询范围应该在当前登录人的权限范围内 */
            checkPowerDate(queryParamvo, corpVO);
            KmMxZVO[] kmmxvos = gl_rep_xjyhrjzserv.getXJRJZVOsConMo(queryParamvo.getPk_corp(),
                    queryParamvo.getKms_first(), queryParamvo.getKms_last(), queryParamvo.getBegindate1(), queryParamvo.getEnddate(),
                    queryParamvo.getXswyewfs(), queryParamvo.getXsyljfs(),
                    queryParamvo.getIshasjz(), queryParamvo.getIshassh(), queryParamvo.getPk_currency(), null, null);//默认人民币
            putKmRq(kmmxvos);
            kmmxvos = getPagedMXZVOs(kmmxvos, page, rows, grid);
            grid.setRows(Arrays.asList(kmmxvos));
            grid.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败!");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                "现金/银行日记账查询:"+queryParamvo.getBegindate1().toString().substring(0, 7)
                        +"-"+ queryParamvo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);

    }

    /**
     * 将查询后的结果分页
     *
     * @param kmmxvos
     * @param page
     * @param rows
     * @return
     */
    public KmMxZVO[] getPagedMXZVOs(KmMxZVO[] kmmxvos, int page, int rows, Grid grid) throws DZFWarpException {
        if (kmmxvos == null || kmmxvos.length == 0) {
            grid.setTotal((long) 0);
            return kmmxvos;
        }

        /** 需要返回的结果集 */
        List<KmMxZVO> listresmxvo = new ArrayList<KmMxZVO>();

        if (kmmxvos != null && kmmxvos.length > 0) {
            int start = (page - 1) * rows;
            for (int i = start; i < page * rows && i < kmmxvos.length; i++) {
                listresmxvo.add(kmmxvos[i]);
            }
            grid.setTotal((long) kmmxvos.length);
            kmmxvos = listresmxvo.toArray(new KmMxZVO[0]);
        } else {
            kmmxvos = new KmMxZVO[0];
            grid.setTotal((long) 0);
        }

        return kmmxvos;
    }


    @Override
    public String getPrintTitleName() {
        return "现 金 / 银 行 日 记 账";
    }

    private void putKmRq(KmMxZVO[] listVo) {
        for (KmMxZVO vo : listVo) {
            if (vo.getZy() != null && ReportUtil.bSysZy(vo)
                    && (vo.getZy().equals("期初余额") || vo.getZy().equals("本月合计") || vo.getZy().equals("本年累计"))) {
                vo.setRq(vo.getRq() + vo.getDay());
            }
        }
    }

    /**
     * 导出Excel
     */
    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody KmReoprtQueryParamVO queryParamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {

        // 校验
        checkSecurityData(null, new String[]{queryParamvo.getPk_corp()},null);
//        KmMxZVO[] listVo = JsonUtils.deserialize(excelExportVO.getList(), KmMxZVO[].class);
//        CorpVO qrycorpvo = zxkjPlatformService.queryCorpByPk(queryParamvo.getPk_corp());
        String gs = excelExportVO.getCorpName();
        String qj = excelExportVO.getTitleperiod();
        String pk_currency = queryParamvo.getPk_currency();
        KmMxZVO[] listVo = gl_rep_xjyhrjzserv.getXJRJZVOsConMo(queryParamvo.getPk_corp(), queryParamvo.getKms_first(),
                queryParamvo.getKms_last(), queryParamvo.getBegindate1(), queryParamvo.getEnddate(),
                queryParamvo.getXswyewfs(), queryParamvo.getXsyljfs(), queryParamvo.getIshasjz(),
                queryParamvo.getIshassh(), queryParamvo.getPk_currency(), null, null);// 默认人民币
        putKmRq(listVo);
        List<KmMxZVO> list = new ArrayList<KmMxZVO>();
        for (KmMxZVO vo : listVo) {
            vo.km = vo.km.trim();
            list.add(vo);
        }
        Excelexport2003<KmMxZVO> lxs = new Excelexport2003<KmMxZVO>();
        XjyhrjzExcelField xsz = new XjyhrjzExcelField();
        xsz.setXjrjzvos(list.toArray(new KmMxZVO[0]));
        xsz.setQj(qj);
        xsz.setCreator(userVO.getUser_name());
        xsz.setCorpName(gs);
        xsz.setCurrencyname(new ReportUtil(zxkjPlatformService).getCurrencyByPk(pk_currency));

        baseExcelExport(response,lxs,xsz);


        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                "现金/银行日记账导出:" + queryParamvo.getBegindate1().toString().substring(0, 7) + "-"
                        + queryParamvo.getEnddate().toString().substring(0, 7),
                ISysConstants.SYS_2);
    }
    /**
     * 打印操作
     */
    @PostMapping("print")
    public void printAction(@RequestParam Map<String, String> pmap1, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response){
        try {

            PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
            QueryParamVO queryParamvo = JsonUtils.deserialize(JsonUtils.serialize(pmap1), QueryParamVO.class);
            // 校验
            checkSecurityData(null, new String[]{queryParamvo.getPk_corp()},null);
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            String lineHeight = pmap.get("lineHeight");
            String font = pmap.get("font");
            printReporUtil.setIscross(new DZFBoolean(pmap.get("pageOrt")));

            /** 声明一个map用来存前台传来的设置参数 */
            String pk_currency = queryParamvo.getPk_currency();
            Map<String, String> tmap = new LinkedHashMap<>();
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间",printParamVO.getTitleperiod());
            tmap.put("单位", new ReportUtil(zxkjPlatformService).getCurrencyByPk(queryParamvo.getPk_currency()));
            KmMxZVO[] bodyvos = gl_rep_xjyhrjzserv.getXJRJZVOsConMo(queryParamvo.getPk_corp(),
                    queryParamvo.getKms_first(),queryParamvo.getKms_last(),  queryParamvo.getBegindate1(), queryParamvo.getEnddate(),
                    queryParamvo.getXswyewfs(),queryParamvo.getXsyljfs(),
                    queryParamvo.getIshasjz(), queryParamvo.getIshassh(), queryParamvo.getPk_currency(), null,null);//默认人民币
            putKmRq(bodyvos);
            printReporUtil.setLineheight(22f);
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));//设置表头字体
            for (int i = 0; i < bodyvos.length; i++) {
                bodyvos[i].km=bodyvos[i].km.trim();
            }
            Object[] obj = getPrintXm(0);
            printReporUtil.printHz(new HashMap<String, List<SuperVO>>(),bodyvos,"现金/银行日记账",(String[])obj[0],
                    (String[])obj[1], (int[])obj[2],(int)obj[3],pmap,tmap);
        } catch (DocumentException e) {
            log.error("打印错误",e);
        } catch (IOException e) {
            log.error("打印错误",e);
        }
    }

    public Object[] getPrintXm(int type){
        Object[] obj = new Object[4];
        switch (type) {
            case 0:
                obj[0] = new String[]{"rq","km","pzh","zy","jf","df","fx","ye"};
                obj[1] = new String[]{"日期","科目","凭证号","摘要","借方","贷方","方向","余额"};
                obj[2] = new int[]{2,3,1,4,2,2,1,2};
                obj[3] = 20;
                break;
            default:
                break;
        }
        return obj;
    }


}
