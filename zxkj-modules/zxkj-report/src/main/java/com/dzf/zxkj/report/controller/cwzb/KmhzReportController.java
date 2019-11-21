package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.SubjectCollectGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.service.cwzb.IKmHzReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 科目汇总表
 */
@RestController
@RequestMapping("gl_rep_kmhzbact")
@Slf4j
public class KmhzReportController extends ReportBaseController {

    @Autowired
    private IKmHzReport gl_rep_kmhzbserv;

    /**
     * 查询科目明细数据
     */
    @GetMapping("/queryAction")
    public ReturnData<Grid> queryAction(@RequestParam Map<String, String> param, @MultiRequestBody CorpVO corpVO) {
        SubjectCollectGrid grid = new SubjectCollectGrid();
        QueryParamVO vo = JsonUtils.convertValue(param, QueryParamVO.class);
        try {
            /** 验证权限 */
            checkPowerDate(vo, corpVO);
            /** 查询 */
            List<Object> kmmxvos = gl_rep_kmhzbserv.getKMHzVOs(vo);
            /** 获取rows */
            List<FseJyeVO> reslist = getGridRows(vo, kmmxvos);
            /** 获取凭证数 */
            grid.setVoucherCount(kmmxvos.size() > 0 ? (Integer) kmmxvos.get(0) : 0);
            /** 获取附件单数 */
            grid.setBillCount(kmmxvos.size() > 1 ? (Integer) kmmxvos.get(1) : 0);
            grid.setTotal((long) (reslist == null ? 0 : reslist.size()));
            grid.setRows(reslist);
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<FseJyeVO>());
            log.error(e.getMessage(), e);
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException? e.getMessage() : "查询失败！");
        }
        return ReturnData.ok().data(grid);
    }

    private List<FseJyeVO> getGridRows(QueryParamVO vo, List<Object> kmmxvos) {
        List<FseJyeVO> reslist = new ArrayList<FseJyeVO>();
        if (kmmxvos != null && kmmxvos.size() > 0) {
            reslist = Arrays.asList((FseJyeVO[]) kmmxvos.get(2));
        }
        /** 转换成tree类型 */
        reslist = conversionTree(reslist, vo.getCjq(), vo.getCjz());
        reslist = getTotalRow(reslist);
        return reslist;
    }

    private List<FseJyeVO> getTotalRow(List<FseJyeVO> reslist) {
        if(reslist==null || reslist.size()==0){
            return reslist;
        }
        FseJyeVO total = new FseJyeVO();

        DZFDouble fsjf = DZFDouble.ZERO_DBL;
        DZFDouble fsdf = DZFDouble.ZERO_DBL;
        for(FseJyeVO vo:reslist){
            fsjf = SafeCompute.add(fsjf, vo.getFsjf());
            fsdf = SafeCompute.add(fsdf, vo.getFsdf());
        }
        total.setKmbm("合计");
        total.setFsjf(fsjf);
        total.setFsdf(fsdf);
        reslist.add(total);

        return reslist;
    }

    private List<FseJyeVO> conversionTree(List<FseJyeVO> reslist, Integer cjq,Integer cjz) {
        List<FseJyeVO> res = new ArrayList<FseJyeVO>();
        for(FseJyeVO vo:reslist){
            if(vo.getAlevel() == cjq.intValue()){
                getTree(vo,reslist,vo.getAlevel()+1,cjz);
                res.add(vo);
            }
        }
        return res;
    }

    private void getTree(FseJyeVO vo, List<FseJyeVO> fsejyevos, int i,int cjz) {
        for(FseJyeVO childvo:fsejyevos){
            if(i>cjz){
                return;
            }else{
                if(childvo.getAlevel() == i && childvo.getKmbm().startsWith(vo.getKmbm())){
                    getTree(childvo, fsejyevos, i+1,cjz);
                    vo.addChildren(childvo);
                }
            }

        }
    }


}
