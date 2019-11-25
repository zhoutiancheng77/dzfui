package com.dzf.zxkj.platform.controller.jzcl;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.query.ReportPrintParamVO;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.jzcl.QmJzVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.jzcl.ITerminalSettle;
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
import java.util.*;

@RestController
@RequestMapping("gl_qmjzact")
@Slf4j
@SuppressWarnings("all")
public class QmjzController extends BaseController {

    @Autowired
    private ITerminalSettle gl_qmjzserv;

    @Autowired
    private IQmgzService qmgzService;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @PostMapping("query")
    public ReturnData<Grid> query(@MultiRequestBody QueryParamVO queryParamVO) {
        Grid grid = new Grid();
        try {
            if(queryParamVO.getCorpslist() == null || queryParamVO.getCorpslist().size() == 0){
                queryParamVO.setCorpslist(Arrays.asList(new String[]{SystemUtil.getLoginCorpId()}));
            }
            QmJzVO[] qmjzvo = gl_qmjzserv.initQueryQmJzVO(queryParamVO);
            grid.setSuccess(true);
            grid.setRows(qmjzvo);
            grid.setMsg("查询成功!");
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败!");
            log.error("查询失败!", e);
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("profitJz")
    public ReturnData<Grid> profitJz(@MultiRequestBody QmJzVO qmJzVO) {
        Grid grid = new Grid();
        String logmsg;
        if (qmJzVO != null) {
            logmsg = "利润结转:" + qmJzVO.getPeriod();
            DZFBoolean b = null;
            b = DZFBoolean.valueOf(true);
            String pk_corp = qmJzVO.getPk_corp();
            String date1 = qmJzVO.getPeriod();
            date1 = date1 + "-" + "31";
            DZFDate date = new DZFDate(date1);
            qmJzVO.setVdef10("1");
            String userid = SystemUtil.getLoginUserId();
            gl_qmjzserv.updateProfitJz(pk_corp, date, qmJzVO, b, userid);
            log.info("结转成功");
            grid.setMsg("结转成功");
            grid.setSuccess(true);
        } else {
            log.info("数据为空");
            grid.setMsg("数据为空");
            logmsg = "数据为空";
            grid.setSuccess(false);
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_SETTLE, logmsg, ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    /**
     * 反利润结转
     */
    @PostMapping("fanLiRunJz")
    public ReturnData<Grid> fanLiRunJz(@MultiRequestBody QmJzVO qmJzVO) {
        Grid grid = new Grid();
        String logmsg = "";
        if (qmJzVO != null) {
            logmsg = "反利润结转:" + qmJzVO.getPeriod();
            String date1 = qmJzVO.getPeriod();
            date1 = date1 + "-" + "31";
            DZFDate date = new DZFDate(date1);
            qmJzVO.setVdef10("0");
            gl_qmjzserv.updateFanLiRunJz(qmJzVO.getPk_corp(), qmJzVO, date);
            log.info("反利润结转成功");
            grid.setMsg("反利润结转成功");
            grid.setSuccess(true);
        } else {
            log.info("数据为空");
            grid.setMsg("数据为空");
            grid.setSuccess(false);
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_SETTLE, logmsg);
        return ReturnData.ok().data(grid);
    }

    /**
     * 结账检查
     *
     * @return
     */
    @PostMapping("checksettle")
    public ReturnData<Grid> checksettle(@MultiRequestBody QmJzVO[] qmJzVOS) {
        Grid grid = new Grid();
        if (qmJzVOS != null && qmJzVOS.length > 0) {
            QmJzVO[] jzvos = gl_qmjzserv.updatecheckTerminalSettleData(qmJzVOS);
            StringBuffer sb = new StringBuffer();
            DZFBoolean fontc = DZFBoolean.FALSE;
            for (QmJzVO jzvo : jzvos) {
                String info = "";
                DZFBoolean pzhasjz = jzvo.getPzhasjz();
                DZFBoolean gdzchasjz = jzvo.getGdzchasjz();
                DZFBoolean sykmwye = jzvo.getSykmwye();
                DZFBoolean qmph = jzvo.getQmph();
                if (jzvo.getHoldflag().booleanValue()) { //公司启用了固定资产
                    if (!pzhasjz.booleanValue()) {
                        info += "凭证都已记账检查未通过";
                    }
                    if (!gdzchasjz.booleanValue()) {
                        info += "、固定资产已结账未通过";
                    }
                    if (!sykmwye.booleanValue()) {
                        info += "、期间损益已结转未通过";
                    }
                    if (!qmph.booleanValue()) {
                        info += "、期末试算平衡未通过";
                    }
                    if (info.length() == 0) {
                        info = "结账检查通过！";
                        fontc = DZFBoolean.TRUE;
                    } else {
                        info = "公司：" + jzvo.getCorpname() + info;
                        fontc = DZFBoolean.FALSE;
                        sb.append(info + "。<br>");
                    }
                } else {  //公司未启用固定资产
                    if (!pzhasjz.booleanValue()) {
                        info += "凭证都已记账检查未通过";
                    }
                    if (!sykmwye.booleanValue()) {
                        info += "、期间损益已结转未通过";
                    }
                    if (!qmph.booleanValue()) {
                        info += "、期末试算平衡未通过";
                    }
                    if (info.length() == 0) {
                        info = "公司：" + jzvo.getCorpname() + ",结账检查通过";
                        fontc = DZFBoolean.TRUE;
                        sb.append(info + "。<br/>");
                    } else {
                        info = "公司：" + jzvo.getCorpname() + "," + info;
                        fontc = DZFBoolean.FALSE;
                        sb.append(info + "。<br/>");
                    }
                }
            }
            if (sb != null && sb.length() > 0) {
                grid = getGrid(grid, sb.toString(), jzvos, fontc);
            } else {
                grid = getGrid(grid, "结账检查通过！", jzvos, fontc);
            }
        } else {
            grid = getGrid(grid, "数据为空!", new QmJzVO[0], DZFBoolean.FALSE);
        }

        return ReturnData.ok().data(grid);
    }


    private Grid getGrid(Grid grid, String msg, QmJzVO[] jzvos, DZFBoolean issuccess) {
        log.info(msg);
        grid.setMsg(msg);
        grid.setTotal((long) (jzvos == null ? 0 : jzvos.length));
        grid.setRows(jzvos == null ? new ArrayList<QmJzVO>() : Arrays.asList(jzvos));
        grid.setSuccess(issuccess.booleanValue());
        return grid;
    }

    /**
     * 结账
     *
     * @return
     */
    @PostMapping("settle")
    public ReturnData<Grid> settle(@MultiRequestBody QmJzVO[] qmJzVOS) {
        Grid grid = new Grid();
        String logmsg = "";
        if (qmJzVOS != null && qmJzVOS.length > 0) {
            String logdatevalue = SystemUtil.getLoginDate();
            /** 重复调用接口，公司+月份 */
            Map<String, List<QmJzVO>> qmjzmap = new HashMap<String, List<QmJzVO>>();
            for (QmJzVO votemp : qmJzVOS) {
                String pk_corp = votemp.getPk_corp();
                if (qmjzmap.containsKey(pk_corp)) {
                    qmjzmap.get(pk_corp).add(votemp);
                } else {
                    List<QmJzVO> listtemp = new ArrayList<QmJzVO>();
                    listtemp.add(votemp);
                    qmjzmap.put(pk_corp, listtemp);
                }
            }
            StringBuffer tips = new StringBuffer();
            List<QmJzVO> resqmjz = new ArrayList<QmJzVO>();
            /** 先按照公司 */
            for (String str : qmjzmap.keySet()) {
                List<QmJzVO> listtemp = qmjzmap.get(str);
                QmJzVO[] qmjzvos = sortQmclByPeriod(listtemp, "asc");
                logmsg = getLogMsg("结账", qmjzvos, "asc");
                for (QmJzVO votemp : qmjzvos) {
                    try {
                        String year = votemp.getPeriod().substring(0, 4);
                        List<QmclVO> qmcls = qmgzService.yearhasGz(votemp.getPk_corp(), year);
                        DZFBoolean ishasgz = DZFBoolean.TRUE;
                        for (QmclVO qmvo : qmcls) {

                            if (qmvo.getIsgz() != null && qmvo.getIsgz().booleanValue()) {
                                continue;
                            }
                            try {
                                qmgzService.processGzOperate(votemp.getPk_corp(), qmvo.getPeriod(), DZFBoolean.TRUE, SystemUtil.getLoginUserId());
                            } catch (BusinessException e) {
                                ishasgz = DZFBoolean.FALSE;
                                tips.append(e.getMessage() + "<br>");
                            }
                        }
                        if (!ishasgz.booleanValue()) {
                            tips.append("请将已经通过的月份关账成功。");
                            continue;
                        }
                        QmJzVO[] jzvos = gl_qmjzserv.saveTerminalSettleDataFromPZ(new QmJzVO[]{votemp}, new DZFDate(logdatevalue), SystemUtil.getLoginUserId());
                        resqmjz.add(jzvos[0]);
                    } catch (Exception e) {
                        tips.append(e.getMessage() + "<br>");
                        resqmjz.add(votemp);
                        log.error("错误", e);
                    }
                }
            }
            if (tips.toString().length() > 0) {
                grid = getGrid(grid, tips.toString(), resqmjz.toArray(new QmJzVO[0]), DZFBoolean.TRUE);
            } else {
                grid = getGrid(grid, "1", resqmjz.toArray(new QmJzVO[0]), DZFBoolean.TRUE);
            }
        } else {
            grid = getGrid(grid, "数据为空!", new QmJzVO[0], DZFBoolean.FALSE);
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_SETTLE, logmsg, ISysConstants.SYS_2);

        return ReturnData.ok().data(grid);
    }

    private String getLogMsg(String ope, QmJzVO[] qmjzvos, String ident) {
        StringBuffer value = new StringBuffer();
        if (ident.equals("asc")) {
            value.append(ope + ":" + qmjzvos[0].getPeriod() + "~" + qmjzvos[qmjzvos.length - 1].getPeriod());
        } else {
            value.append(ope + ":" + qmjzvos[qmjzvos.length - 1].getPeriod() + "~" + qmjzvos[0].getPeriod());
        }

        return value.toString();
    }

    private QmJzVO[] sortQmclByPeriod(List<QmJzVO> listtemp, final String ordervalue) {
        QmJzVO[] qmclvos = listtemp.toArray(new QmJzVO[0]);
        /** 先对集合排序 */
        java.util.Arrays.sort(qmclvos, new Comparator<QmJzVO>() {
            public int compare(QmJzVO o1, QmJzVO o2) {
                int i = 0;
                if ("desc".equals(ordervalue)) {
                    if (o1.getPeriod().compareTo(o2.getPeriod()) > 0) {
                        i = -1;
                    } else if (o1.getPeriod().compareTo(o2.getPeriod()) == 0) {
                        i = 0;
                    } else {
                        i = 1;
                    }
                } else {
                    i = o1.getPeriod().compareTo(o2.getPeriod());
                }
                return i;
            }
        });
        return qmclvos;
    }


    /**
     * 反结账
     */
    @PostMapping("unsettle")
    public ReturnData<Grid> unsettle(@MultiRequestBody QmJzVO[] qmJzVOS) {
        // easyui 专用的逻辑
        Grid grid = new Grid();
        String logmsg = "";
        if (qmJzVOS != null && qmJzVOS.length > 0) {
            /** 重复调用接口，公司+月份 */
            Map<String, List<QmJzVO>> qmclmap = new HashMap<String, List<QmJzVO>>();
            for (int i = qmJzVOS.length - 1; i >= 0; i--) {
                QmJzVO votemp = qmJzVOS[i];
                String pk_corp = votemp.getPk_corp();
                if (qmclmap.containsKey(pk_corp)) {
                    qmclmap.get(pk_corp).add(votemp);
                } else {
                    List<QmJzVO> listtemp = new ArrayList<QmJzVO>();
                    listtemp.add(votemp);
                    qmclmap.put(pk_corp, listtemp);
                }
            }
            StringBuffer tips = new StringBuffer();
            List<QmJzVO> resqmcl = new ArrayList<QmJzVO>();
            /** 先按照公司 */
            for (String str : qmclmap.keySet()) {
                List<QmJzVO> listtemp = qmclmap.get(str);
                QmJzVO[] qmclvos = sortQmclByPeriod(listtemp, "desc");
                logmsg = getLogMsg("反结账", qmclvos, "desc");
                for (QmJzVO votemp : qmclvos) {
                    try {
                        QmJzVO[] jzvos = gl_qmjzserv.updatecancelTerminalSettleData(new QmJzVO[]{votemp});
                        resqmcl.add(jzvos[0]);
                    } catch (Exception e) {
                        tips.append(e.getMessage() + "<br>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    }
                }
            }
            if (tips.toString().length() > 0) {
                grid = getGrid(grid, tips.toString(), resqmcl.toArray(new QmJzVO[0]), DZFBoolean.TRUE);
            } else {
                grid = getGrid(grid, "1", resqmcl.toArray(new QmJzVO[0]), DZFBoolean.TRUE);
            }
        } else {
            grid = getGrid(grid, "数据为空!", new QmJzVO[0], DZFBoolean.FALSE);
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_SETTLE, logmsg, ISysConstants.SYS_2);

        return ReturnData.ok().data(grid);
    }

    @PostMapping("print/pdf")
    public void print(ReportPrintParamVO printParamVO, String data, String period, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {

        try {
            QmJzVO[] qmJzVOS = JsonUtils.deserialize(data, QmJzVO[].class);
            if (qmJzVOS == null || qmJzVOS.length == 0) {
                return;
            }

            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);

            Map<String, String> pmap = new HashMap<String, String>();
            pmap.put("type", printParamVO.getType());
            pmap.put("pageOrt", printParamVO.getPageOrt());
            pmap.put("left", printParamVO.getLeft());
            pmap.put("top", printParamVO.getTop());
            pmap.put("printdate", printParamVO.getPrintdate());
            pmap.put("font", printParamVO.getFont());

            qmJzVOS = Arrays.stream(qmJzVOS).map(vo -> {
                if (vo.getVdef10() != null && vo.getVdef10().equals("1")) {
                    vo.setVdef10("是");
                } else {
                    vo.setVdef10("否");
                }
                return vo;
            }).toArray(size -> new QmJzVO[size]);

            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
            Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            tmap.put("期间", period);
            printReporUtil.setLineheight(22f);

            printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), qmJzVOS, "年 末 结 账",
                    new String[]{"period", "corpname", "pzhasjz", "gdzchasjz", "sykmwye", "vdef10", "qmph",
                            "jzfinish"},
                    new String[]{"期间", "公司", "凭证都已记账", "固定资产已结账", "期间损益已结转", "利润结转", "期末试算平衡", "结账完成"},
                    new int[]{2, 5, 3, 3, 3, 3, 3, 2}, 20, pmap, tmap);
        } catch (DocumentException e) {
            log.error("打印错误", e);
        } catch (IOException e) {
            log.error("打印错误", e);
        }
    }
}
