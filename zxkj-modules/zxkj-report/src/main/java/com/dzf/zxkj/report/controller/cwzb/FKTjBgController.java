package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.report.FkTjBgVo;
import com.dzf.zxkj.platform.model.report.FkTjSetVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.query.FktjQueryParam;
import com.dzf.zxkj.report.service.cwzb.IFkTjBgService;
import com.dzf.zxkj.report.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("gl_rep_fktjbgact")
@Slf4j
public class FKTjBgController extends BaseController {
    @Autowired
    private IFkTjBgService gl_fktjbgserv;

    @PostMapping("query")
    public ReturnData<Grid> query(@MultiRequestBody FktjQueryParam queryParam) {
        Grid grid = new Grid();
        try {
            FkTjSetVo[] setvos = gl_fktjbgserv.query(SystemUtil.getLoginCorpId(), queryParam.getBegindate(), queryParam.getEnddate());
            grid.setMsg("查询成功");
            grid.setTotal((long) (setvos == null ? 0 : setvos.length));
            grid.setSuccess(true);
            grid.setRows(setvos != null ? Arrays.asList(setvos) : new ArrayList<FkTjSetVo>());
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("save")
    public ReturnData save() {
        Grid json = new Grid();
        try {
            FkTjSetVo setvo = new FkTjSetVo();
            setvo.setPk_corp(SystemUtil.getLoginCorpId());
            setvo.setInspectdate(new DZFDateTime());
            setvo.setQj(SystemUtil.getLoginDate().substring(0, 4) + "-01~" + SystemUtil.getLoginDate().substring(0, 7));
            setvo.setVinspector(SystemUtil.getLoginUserId());
            gl_fktjbgserv.save(setvo);
            json.setMsg("保存成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "保存失败");
            log.error("保存失败", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 查询报告数据
     */
    @PostMapping("queryFktj")
    public ReturnData queryFktj(@MultiRequestBody CorpVO cpvo, @MultiRequestBody DZFDate idate) {
        Json json = new Json();
        try {
            Map<String, Object> resmap = new HashMap<String, Object>();
            DZFDate enddate = DateUtils.getPeriodEndDate(DateUtils.getPeriod(idate));
            if (enddate.before(cpvo.getBegindate())) {
                throw new BusinessException("查询开始日期不能在建账日期前");
            }
            // 指标分组(分出正常，异常，无法计算)
            FkTjBgVo[] bgvos = gl_fktjbgserv.queryFktj(idate, cpvo);
            putXmGroup(bgvos, resmap);
            resmap.put("zbxq", bgvos);// 指标详情
            json.setData(resmap);
            json.setSuccess(true);
            json.setMsg("查询成功");
            json.setRows(bgvos);
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(json);
    }

    private void putXmGroup(FkTjBgVo[] bgvos, Map<String, Object> resmap) {
        List<String> yczblist = new ArrayList<String>();//异常指标项
        List<String> wfjslist = new ArrayList<String>();//无法计算项
        List<String> zczblist = new ArrayList<String>();//正常指标项
        if (bgvos != null && bgvos.length > 0) {
            for (FkTjBgVo vo : bgvos) {
                if (vo.getFx().intValue() == 0) {//正常
                    zczblist.add(vo.getZbmc());
                } else if (vo.getFx().intValue() == 1) {//异常
                    yczblist.add(vo.getZbmc());
                } else {//无法计算
                    wfjslist.add(vo.getZbmc());//无法计算项
                }
            }
        }

        resmap.put("yczbx", yczblist);//异常指标项
        resmap.put("wfjs", wfjslist);//无法计算项
        resmap.put("zczb", zczblist);//正常指标
    }

    /**
     * 增值税查询
     */
    @PostMapping("queryZzs")
    public ReturnData queryZzs(@MultiRequestBody CorpVO cpvo, @MultiRequestBody String idate) {

        DZFDate date = new DZFDate(idate);

        Grid json = new Grid();

        try {
            DZFDate enddate = DateUtils.getPeriodEndDate(date.getYear() + "-12");
            if (enddate.before(cpvo.getBegindate())) {
                throw new BusinessException("查询开始日期不能在建账日期前");
            }
            String period = date.toString().substring(0, 7);
            Object[] zzsvos = gl_fktjbgserv.queryZzsBg(period, cpvo);
            json.setRows(zzsvos[1]);
            json.setMsg((String) zzsvos[0]);
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "查询增值税失败");
            log.error("查询增值税失败", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 所得税查询
     */
    @PostMapping("querySds")
    public ReturnData querySds(@MultiRequestBody CorpVO cpvo, @MultiRequestBody String idate) {
        DZFDate date = new DZFDate(idate);
        Grid grid = new Grid();
        try {
            DZFDate enddate = DateUtils.getPeriodEndDate(date.getYear() + "-12");
            if (enddate.before(cpvo.getBegindate())) {
                throw new BusinessException("查询开始日期不能在建账日期前");
            }
            String year = idate.substring(0, 7);
            Object[] sdsvos = gl_fktjbgserv.querySdsBg(year, cpvo);
            grid.setRows(sdsvos[1]);
            grid.setSuccess(true);
            grid.setMsg((String) sdsvos[0]);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询所得税失败");
            log.error("查询所得税失败", e);
        }
        return ReturnData.ok().data(grid);
    }

}
