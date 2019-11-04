package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.query.QueryCondictionVO;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.NumMnyGlVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwzb.INummnyReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数量金额总账
 */
@RestController
@RequestMapping("gl_rep_sljezzact")
@Slf4j
public class NmnyHZController {

    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private INummnyReport gl_rep_nmdtserv;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @GetMapping("/queryAction")
    public ReturnData<Grid> query(@RequestParam Map<String, String> param) {
        Grid grid = new Grid();
        QueryCondictionVO paramVO = new  QueryCondictionVO();
        try {
            paramVO = JsonUtils.convertValue(param, QueryCondictionVO.class);
            //前台传参三个值,前两个值为必输项目，第三个为非必输项目
            CorpVO corp = zxkjPlatformService.queryCorpByPk(SystemUtil.getLoginCorpId());
            paramVO.setPk_corp(corp.getPk_corp());
            paramVO.setJzdate(corp.getBegindate());
            paramVO.setIsic(new DZFBoolean(IcCostStyle.IC_ON.equals(corp.getBbuildic())));

            // 开始日期应该在建账日期前
//            CorpVO currcorp = CorpCache.getInstance().get("", paramVO.getPk_corp());
            DZFDate begdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(corp.getBegindate()));
            DZFDate qrybegdate = DateUtils.getPeriodEndDate(paramVO.getQjq());
            if (begdate.after(qrybegdate)) {
                throw new BusinessException("开始日期不能在建账日期(" + DateUtils.getPeriod(begdate) + ")前!");
            }

            List<NumMnyGlVO> kmmxvos = gl_rep_nmdtserv.getNumMnyGlVO(paramVO);

            if(kmmxvos!=null && kmmxvos.size()>0){
                ReportUtil.updateKFx(kmmxvos.toArray(new NumMnyGlVO[0]));
            }
            kmmxvos = conversionTree(kmmxvos,paramVO.getCjq(),paramVO.getCjz(),paramVO.getPk_corp());


            grid.setTotal((long) (kmmxvos==null?0:kmmxvos.size()));
            grid.setRows(kmmxvos);
            grid.setSuccess(true);
            grid.setMsg("查询成功！");
        } catch (Exception e) {
			log.error(e.getMessage());
			grid.setSuccess(false);
            grid.setRows(new ArrayList<NumMnyGlVO>());
			grid.setMsg("查询失败:"+e.getMessage());
        }
        return ReturnData.ok().data(grid);
    }
    private List<NumMnyGlVO> conversionTree(List<NumMnyGlVO> kmmxvos, Integer cjq,Integer cjz,String pk_corp) {
        YntCpaccountVO[] cpavos = zxkjPlatformService.queryByPk(pk_corp);
        Map<String,YntCpaccountVO> map = new HashMap<String,YntCpaccountVO>();
        for(YntCpaccountVO vo:cpavos){
            map.put(vo.getAccountcode(), vo);
        }
        //赋值科目层级
        YntCpaccountVO tvo = null;
        for(NumMnyGlVO vo:kmmxvos){
            tvo = map.get(vo.getKmbm());
            if(tvo!=null){
                vo.setAccountlevel(tvo.getAccountlevel());
            }
        }
        List<NumMnyGlVO>  reslist = new ArrayList<NumMnyGlVO>();
        for(NumMnyGlVO vo:kmmxvos){
            if(vo.getAccountlevel()!=null && vo.getAccountlevel() ==  cjq.intValue()){
                getTree(vo, kmmxvos, cjq+1,cjz.intValue());
                reslist.add(vo);
            }
        }
        return reslist;
    }

    private void getTree(NumMnyGlVO vo, List<NumMnyGlVO> kmmxvos, int i,int cjz) {
        for(NumMnyGlVO childvo:kmmxvos){
            if(i>cjz+1){
                return;
            } else {
                if (childvo.getKmbm().startsWith(vo.getKmbm())) {
                    if (childvo.getAccountlevel() != null && childvo.getAccountlevel() == i) {
                        getTree(childvo, kmmxvos, i + 1,cjz);
                        vo.addChildren(childvo);
                    }else if (childvo.getAccountlevel() == null && childvo.getKmbm().indexOf("_") > 0
                            && vo.getKmbm().equals(childvo.getKmbm().split("_")[0])
                    ) {
                        vo.addChildren(childvo);
                    }
                }
            }

        }
    }

}
