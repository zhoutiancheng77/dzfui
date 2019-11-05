package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.service.cwzb.IKMZZReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("gl_rep_kmzjact")
@Slf4j
public class KmzzController extends ReportBaseController {

    @Autowired
    private IKMZZReport gl_rep_kmzjserv;

    /**
     * 查询科目明细数据
     */
    @PostMapping("/queryAction")
    public ReturnData<Grid> queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        QueryParamVO queryParamvo = getQueryParamVO(queryvo, corpVO);
        try {
            KmZzVO[] vos = null;
            /** 验证 查询范围应该在当前登录人的权限范围内 */
            checkPowerDate(queryParamvo, corpVO);
            queryParamvo.setIsnomonthfs(DZFBoolean.TRUE);
            queryParamvo.setBtotalyear(DZFBoolean.TRUE);
            KmZzVO[] kmmxvos = gl_rep_kmzjserv.getKMZZVOs(queryParamvo, null);
            new ReportUtil().updateKFx(kmmxvos);
            /** 如果有期初余额则不显示下面的 */
            List<KmZzVO> listmx = filterQC(kmmxvos);
            vos = listmx.toArray(new KmZzVO[0]);
            grid.setTotal((long) (vos == null ? 0 : vos.length));
            if (vos != null && vos.length > 0) {
                vos = getPagedZZVOs(vos, queryvo.getPage(), queryvo.getRows());
            }
            grid.setRows(vos == null ? new ArrayList<KmZzVO>() : Arrays.asList(vos));
            grid.setSuccess(true);

        } catch (Exception e) {
            grid.setRows(new ArrayList<KmZzVO>());
            printErrorLog(grid, e, "查询失败！");
        }
//        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT.getValue(),
//                "科目总账查询:"+queryParamvo.getBegindate1().toString().substring(0, 7) +
//                        "-"+ queryParamvo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    public static List<KmZzVO> filterQC(KmZzVO[] kmmxvos) {
        HashMap<String, DZFBoolean> mapshow = new HashMap<String, DZFBoolean>();
        for (KmZzVO mxzvo : kmmxvos) {
            mapshow.put(mxzvo.getPk_accsubj(), DZFBoolean.FALSE);
        }
        List<KmZzVO> listmx = new ArrayList<KmZzVO>();
        for (KmZzVO mxzvo : kmmxvos) {
            if (mapshow.get(mxzvo.getPk_accsubj()).booleanValue() && "期初余额".equals(mxzvo.getZy())) {
                continue;
            }

            listmx.add(mxzvo);
            if (!mapshow.get(mxzvo.getPk_accsubj()).booleanValue() && "期初余额".equals(mxzvo.getZy())) {
                mapshow.put(mxzvo.getPk_accsubj(), DZFBoolean.TRUE);
            }
        }
        return listmx;
    }

    /**
     * 将查询后的结果分页
     *
     * @param kmmxvos
     * @param page
     * @param rows
     * @return
     */
    public KmZzVO[] getPagedZZVOs(KmZzVO[] kmmxvos, int page, int rows) {
        int beginIndex = rows * (page - 1);
        int endIndex = rows * page;
        if (endIndex >= kmmxvos.length) {// 防止endIndex数组越界
            endIndex = kmmxvos.length;
        }
        kmmxvos = Arrays.copyOfRange(kmmxvos, beginIndex, endIndex);
        return kmmxvos;
    }

    @Override
    public String getPrintTitleName() {
        return "科 目 总 账";
    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public Map<String, List<SuperVO>> sortMapByKey(Map<String, List<SuperVO>> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, List<SuperVO>> sortMap = new TreeMap<String, List<SuperVO>>(new Comparator<String>() {
            public int compare(String str1, String str2) {
                return str1.compareTo(str2);
            }
        }
        );
        sortMap.putAll(map);

        return sortMap;
    }



}
