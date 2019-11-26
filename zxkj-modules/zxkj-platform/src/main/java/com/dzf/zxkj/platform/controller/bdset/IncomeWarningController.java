package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.IncomeHistoryVo;
import com.dzf.zxkj.platform.model.bdset.IncomeWarningVO;
import com.dzf.zxkj.platform.service.bdset.IIncomeWarningService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gl/gl_incomewarning")
@Slf4j
public class IncomeWarningController{
    @Autowired
    private IIncomeWarningService iw_serv;//预警信息

    @GetMapping("/query")
    public ReturnData<Grid> query(){
        Grid grid = new Grid();
        try {
            IncomeWarningVO[] ivos = iw_serv.query(SystemUtil.getLoginCorpId());
            List<IncomeWarningVO> list = Arrays.asList(ivos);
            grid.setRows(list);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败！");
        }
        //日志记录
//        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET.getValue(),
//                "收入预警设置查询", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    @GetMapping("/queryIncomeWarningInfo")
    public ReturnData<Json> queryIncomeWarningInfo(String nowDate, String filflg) {
        Json json = new Json();
        try {
            String pk_corp = SystemUtil.getLoginCorpId();

            //优化 gzhx
            IncomeWarningVO[] incomeWarningVOS = iw_serv.queryIncomeWaringVos(pk_corp,nowDate, filflg);

//			IncomeWarningVO[] ivos = iw_serv.query(pk_corp);
//			iw_serv.queryFseInfo(ivos, pk_corp, nowDate);

            json.setRows(incomeWarningVOS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败!");
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/save")
    public ReturnData<Json> save(@RequestBody Map<String, Object> param) {

        Json json = new Json();
        try {
            IncomeWarningVO data = JsonUtils.convertValue(param, IncomeWarningVO.class);
                    String pk_corp = SystemUtil.getLoginCorpId();
//                HttpServletRequest request = getRequest();
            IncomeWarningVO[] ivos = iw_serv.query(pk_corp);
            Integer period_type = data.getPeriod_type();
            if (period_type == null) {
                period_type = 3;
                data.setPeriod_type(period_type);
            }

            for (IncomeWarningVO ivo : ivos) {
                if (ivo.getXmmc().equals(data.getXmmc())
                        && period_type.equals(ivo.getPeriod_type() == null ? 3 : ivo.getPeriod_type())
                        && !ivo.getPk_sryj().equals(data.getPk_sryj())) {
                    json.setMsg("相同预警周期项目名称不可重复");
                    json.setRows(data);
                    json.setSuccess(false);
                    return ReturnData.ok().data(json);
                }
            }

            if(IDefaultValue.DefaultGroup.equals(data.getPk_corp())){
                data.setPk_corp(pk_corp);
                data.setPk_sryj(null);
            }

            String his = (String) param.get("history");
            if (!StringUtil.isEmpty(his)) {
                IncomeHistoryVo[] hisVos = JsonUtils.deserialize(his, IncomeHistoryVo[].class);
                for (int i = 0; i < hisVos.length; i++) {
                    IncomeHistoryVo hisVo = hisVos[i];
                    hisVo.setDr(0);
                    hisVo.setTs(new DZFDateTime());
                    hisVo.setPk_corp(pk_corp);
                    hisVo.setPk_sryj(data.getPk_sryj());
                }
                data.setChildren(hisVos);
            }
            iw_serv.save(data.getIsloginremind(), data.getIsinputremind(), data, pk_corp);
            json.setMsg("保存成功");
            json.setRows(data);
            json.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "保存失败!");
        }
        //日志记录
//        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET.getValue(),
//                "收入预警设置保存", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/onDelete")
    public ReturnData<Json> onDelete(@RequestBody IncomeWarningVO data) {
        Json json = new Json();
        if (data != null) {
            try {
                IncomeWarningVO[] bavo = iw_serv.queryByPrimaryKey(data.getPk_sryj());
                if (bavo == null) {
                    throw new BusinessException("摘要不存在或已被删除！");
                }
                IncomeWarningVO vo = bavo[0];
                if(vo.getSpeflg() != null && vo.getSpeflg().booleanValue()){
                    throw new BusinessException("系统预置不允许删除");
                }
                if (!SystemUtil.getLoginCorpId().equals(bavo[0].getPk_corp())) {
                    throw new BusinessException("无权操作！");
                }
                iw_serv.delete(bavo[0]);
                json.setSuccess(true);
                json.setRows(bavo);
                json.setMsg("删除成功！");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                json.setSuccess(false);
                json.setMsg(e instanceof BusinessException ? e.getMessage() : "删除失败！");
            }
        } else {
            json.setSuccess(false);
            json.setMsg("删除失败！");
        }
        //日志记录
//        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET.getValue(),
//                "收入预警设置删除", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }
}