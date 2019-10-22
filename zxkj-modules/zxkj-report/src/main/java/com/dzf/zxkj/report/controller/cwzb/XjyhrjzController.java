package com.dzf.zxkj.report.controller.cwzb;

import com.alibaba.fastjson.JSON;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.utils.DzfTypeUtils;
import com.dzf.zxkj.base.utils.FieldMapping;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.PzmbbVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.service.cwzb.IXjRjZReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("gl_rep_xjyhrjzact")
@Slf4j
public class XjyhrjzController extends ReportBaseController {

    @Autowired
    private IXjRjZReport gl_rep_xjyhrjzserv;


    /**
     * 查询科目明细数据
     */
    @PostMapping("/queryAction")
    public ReturnData<Grid> queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        QueryParamVO queryParamvo = getQueryParamVO(queryvo, corpVO);
        try {
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
//        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT.getValue(),
//                "现金/银行日记账查询:"+queryParamvo.getBegindate1().toString().substring(0, 7)
//                        +"-"+ queryParamvo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);
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


}
