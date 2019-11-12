package com.dzf.zxkj.platform.controller.zcgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.zcgl.ValuemodifyVO;
import com.dzf.zxkj.platform.service.zcgl.IYzbgService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("am_yzbgact")
@Slf4j
@SuppressWarnings("all")
public class YzbgController extends BaseController {
    @Autowired
    private IYzbgService am_yzbgserv;

    // 保存
    @PostMapping("save")
    public ReturnData<Grid> save(@MultiRequestBody ValuemodifyVO data) {
        Grid json = new Grid();
        if (data != null) {
            try {
                setLoginValue(data);
                data = am_yzbgserv.save(data);
                json.setSuccess(true);
                json.setRows(data);
                json.setMsg("保存成功");
                writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL,"原值变更保存", ISysConstants.SYS_2);
            } catch (Exception e) {
                printErrorLog(json, e, "保存失败");
                log.error("保存失败", e);
            }
        } else {
            json.setSuccess(false);
            json.setMsg("保存失败");
        }
        return ReturnData.ok().data(json);
    }

    // 删除
    @PostMapping("delete")
    public ReturnData<Grid> delete(@MultiRequestBody ValuemodifyVO data) {
        Grid json = new Grid();
        try {
            checkCorp(SystemUtil.getLoginCorpId(), data);
            am_yzbgserv.delete(data);
            json.setSuccess(true);
            json.setMsg("删除成功!");
            writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL,"原值变更删除",ISysConstants.SYS_2);
        } catch (Exception e) {
            printErrorLog(json, e, "删除失败");
            log.error("删除失败",e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 赋值默认登录信息
     */
    private void setLoginValue(ValuemodifyVO data) {
        String strDate = SystemUtil.getLoginDate();
        data.setPk_corp(SystemUtil.getLoginCorpId());
        data.setCoperatorid(SystemUtil.getLoginUserId());
        data.setDoperatedate(new DZFDate(strDate));
    }

    // 查询
    @PostMapping("query")
    public ReturnData<Grid> query(@MultiRequestBody QueryParamVO paramvo) {
        Grid grid = new Grid();
        try {
            if (paramvo != null) {
                if(StringUtil.isEmptyWithTrim(paramvo.getPk_corp())){
                    paramvo.setPk_corp(SystemUtil.getLoginCorpId());
                }
                List<ValuemodifyVO> list = am_yzbgserv.query(paramvo);
                log.info("查询成功！");
                writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL,"原值变更查询",ISysConstants.SYS_2);
                if (list != null && list.size() > 0) {
                    grid.setTotal((long) list.size());
                    grid.setSuccess(true);
                    grid.setRows(list);
                }else{
                    grid.setTotal(0L);
                    grid.setSuccess(true);
                }
            }
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("update")
    public ReturnData<Grid> update(@MultiRequestBody ValuemodifyVO data) {
        Grid json = new Grid();
        if (data != null) {
            try {
                checkCorp(SystemUtil.getLoginCorpId(), data);
                am_yzbgserv.update(data);
                json.setSuccess(true);
                json.setRows(data);
                json.setMsg("成功");
            } catch (Exception e) {
                printErrorLog(json, e, "失败");
                log.error("更新失败", e);
            }
        } else {
            json.setSuccess(false);
            json.setMsg("失败");
        }
        return ReturnData.ok().data(json);
    }

    // 查询一条
    @PostMapping("queryOne")
    public ReturnData<Grid> queryOne(@MultiRequestBody ValuemodifyVO data) {
        Grid json = new Grid();
        ValuemodifyVO vo = null;
        try {

            vo = am_yzbgserv.queryById(data.getPk_assetvaluemodify());
            if (!SystemUtil.getLoginCorpId().equals(vo.getPk_corp())) {
                json.setSuccess(false);
                json.setMsg("无权操作");
                return ReturnData.ok().data(json);
            }
            log.info("查询成功！");
            json.setSuccess(true);
            json.setMsg("成功");
            json.setRows(vo);
        } catch (Exception e) {
            // log.info("失败！");
            // json.setSuccess(false);
            // json.setMsg("失败");
            printErrorLog(json, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("createVoucherById")
    public ReturnData<Grid> createVoucherById(@MultiRequestBody String id){
        Grid json = new Grid();
        try {
            TzpzHVO tzpzhvo = am_yzbgserv.createTzpzVoById(id, SystemUtil.getLoginCorpId(),new DZFDate(SystemUtil.getLoginDate()));
            json.setSuccess(true);
            json.setRows(tzpzhvo);
            json.setMsg("获取成功");
        } catch (Exception e) {
            printErrorLog(json,  e, "获取失败");
            log.error("获取失败", e);
        }
        return ReturnData.ok().data(json);
    }

    private void checkCorp(String loginCorp, ValuemodifyVO vo)
            throws DZFWarpException {
        ValuemodifyVO qvo = am_yzbgserv.queryById(vo.getPk_assetvaluemodify());
        if (qvo == null)
            throw new BusinessException("该数据不存在，或已被删除！");
        if (!loginCorp.equals(qvo.getPk_corp()))
            throw new BusinessException("只能操作当前登录公司权限内的数据！");
    }
}
