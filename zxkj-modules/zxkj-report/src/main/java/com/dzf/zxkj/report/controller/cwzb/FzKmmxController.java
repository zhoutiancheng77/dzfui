package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.report.FzKmmxVO;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.service.cwzb.IFzKmmxReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("gl_rep_fzkmmxjact")
@Slf4j
public class FzKmmxController extends ReportBaseController {

    @Autowired
    private IFzKmmxReport gl_rep_fzkmmxjrptserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;


    @PostMapping("/queryAction")
    public ReturnData queryAction(@MultiRequestBody KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO) {
        ReportDataGrid grid = new ReportDataGrid();
        queryparamvo = getQueryParamVO(queryparamvo,corpVO);
        try {
            /** 开始日期应该在建账日期前 */
            checkPowerDate(queryparamvo,corpVO);
            Object[] objs = gl_rep_fzkmmxjrptserv.getFzkmmxVos(queryparamvo, DZFBoolean.FALSE);
            List<FzKmmxVO> rsfzvos = (List<FzKmmxVO>) objs[0];
            List<FzKmmxVO> fzkmms = (objs[1] == null)? new ArrayList<FzKmmxVO>() :(List<FzKmmxVO>) objs[1];
            if (rsfzvos == null || rsfzvos.size() == 0) {
                grid.setRows(new ArrayList<FzKmmxVO>());
                grid.setSuccess(true);
                grid.setTotal((long)0);
                grid.setFzkmmx(fzkmms);
                grid.setMsg("查询数据为空!");
            } else {
                new ReportUtil().updateKFx(fzkmms.toArray(new FzKmmxVO[0]));
                grid.setFzkmmx(fzkmms);
                grid.setRows(rsfzvos);
                grid.setTotal((long)rsfzvos.size());
                grid.setSuccess(true);
                grid.setMsg("查询成功!");
            }

        } catch (Exception e) {
            grid.setRows(new ArrayList<FzKmmxVO>());
            printErrorLog(grid, e, "辅助明细账查询失败!");
        }
//        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT.getValue(),
//                "辅助明细账查询:"+queryparamvo.getBegindate1().toString().substring(0, 7)
//                        +"-"+ queryparamvo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    /**
     * 获取辅助类别
     */
    @PostMapping("/getFzlb")
    public ReturnData getFzlb(@RequestParam("corpid") String pk_corp , @MultiRequestBody CorpVO corpVO) {
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = corpVO.getPk_corp();
        }
        Grid grid = new Grid();
        try {
            AuxiliaryAccountHVO[] hvos = zxkjPlatformService.queryHByPkCorp(pk_corp);
            grid.setRows(Arrays.asList(hvos));
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<AuxiliaryAccountHVO>());
            printErrorLog(grid, e, "辅助类别查询失败!");
        }
        return ReturnData.ok().data(grid);
    }


    /**
     * 获取辅助项目参照
     */
    @PostMapping("/getFzxm")
    public ReturnData getFzxm(@RequestParam("corpid") String pk_corp,@RequestParam("fzlbid") String fzlb, @MultiRequestBody CorpVO corpVO) {
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = corpVO.getPk_corp();
        }
        Grid grid = new Grid();
        try {
            AuxiliaryAccountBVO[] bvos = zxkjPlatformService.queryBByFzlb(pk_corp,fzlb);
            grid.setRows(Arrays.asList(bvos));
            grid.setSuccess(true);
        } catch (Exception e) {
            log.error("辅助类别查询失败:", e);
            grid.setRows(new ArrayList<AuxiliaryAccountHVO>());
            printErrorLog(grid, e, "辅助类别查询失败!");
        }
        return  ReturnData.ok().data(grid);
    }

    private KmReoprtQueryParamVO getQueryParamVO(KmReoprtQueryParamVO queryparamvo, CorpVO corpVO) {
        if (StringUtil.isEmpty(queryparamvo.getPk_corp())) {
            // 如果编制单位为空则取当前默认公司
            queryparamvo.setPk_corp(corpVO.getPk_corp());
        }
        return queryparamvo;
    }


    private void putHlJd(FzKmmxVO[] bodyvos, Integer hljd) {
        if(bodyvos!=null && bodyvos.length>0){
            for(FzKmmxVO vo:bodyvos){
                if(!StringUtil.isEmpty(vo.getBz())){
                    String hl = DZFDouble.ZERO_DBL.setScale(hljd, DZFDouble.ROUND_HALF_UP).toString();
                    if(!StringUtil.isEmpty(vo.getHl())){
                        hl = new DZFDouble(vo.getHl()).setScale(hljd, DZFDouble.ROUND_HALF_UP).toString();
                    }
                    vo.setBz(vo.getBz()+"/"+hl);
                }
            }
        }
    }

}
