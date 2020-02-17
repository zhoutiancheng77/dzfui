package com.dzf.zxkj.platform.controller.qcset;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Page;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.qcset.YntXjllqcyePageVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.qcset.IQcxjlyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("gl_qcxjlyact")
@SuppressWarnings("all")
@Slf4j
public class QcxjlyController extends BaseController {
    @Autowired
    private IQcxjlyService qcxjlyService;
    @Autowired
    private IQmgzService qmgzService;

    // 新增保存
    @PostMapping("save")
    public ReturnData<Grid> onSaveNew(@MultiRequestBody YntXjllqcyePageVO yntXjllqcyePageVO, @MultiRequestBody CorpVO loginCorp, @MultiRequestBody UserVO userVO) {
        Grid<YntXjllqcyePageVO> grid = new Grid();
        if (yntXjllqcyePageVO != null) {
            yntXjllqcyePageVO.setPk_corp(loginCorp.getPk_corp());
            yntXjllqcyePageVO.setDoperatedate(loginCorp.getBegindate().toString());
            yntXjllqcyePageVO.setCoperatorid(userVO.getCuserid());
            try {
                String year = yntXjllqcyePageVO.getDoperatedate().substring(0, 4);
                List<QmclVO> qmcls = qmgzService.yearhasGz(yntXjllqcyePageVO.getPk_corp(),
                        year);
                for (QmclVO qmvo : qmcls) {
                    if (qmvo.getIsgz().booleanValue()) {
                        throw new BusinessException("已经存在关账的月份，不允许修改期初数据哦！");
                    }
                }
                qcxjlyService.saveNew(yntXjllqcyePageVO);
                grid.setSuccess(true);
                grid.setRows(yntXjllqcyePageVO);
                grid.setMsg("保存成功");
                writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "期初现金流量编辑", ISysConstants.SYS_2);
            } catch (BusinessException bs){
                grid.setSuccess(false);
                grid.setMsg(bs.getMessage());
                log.error(bs.getMessage(), bs);
            }catch (Exception e) {
                grid.setSuccess(false);
                grid.setMsg("保存失败");
                log.error("保存失败", e);
            }
        } else {
            grid.setSuccess(false);
            grid.setMsg("保存失败");
        }
        return ReturnData.ok().data(grid);
    }

    // 修改保存
    @PostMapping("update")
    public ReturnData<Grid> onUpdate(@MultiRequestBody YntXjllqcyePageVO yntXjllqcyePageVO, @MultiRequestBody CorpVO loginCorp, @MultiRequestBody UserVO userVO) {
        Grid json = new Grid();
        if (yntXjllqcyePageVO != null) {
            yntXjllqcyePageVO.setDoperatedate(loginCorp.getBegindate().toString());
            yntXjllqcyePageVO.setCoperatorid(userVO.getCuserid());
            yntXjllqcyePageVO.setPk_corp(loginCorp.getPk_corp());
            try {
                // 修改保存前数据安全验证
                String primaryKey = yntXjllqcyePageVO.getPrimaryKey();
                if (!StringUtil.isEmpty(primaryKey)) {
                    YntXjllqcyePageVO getvo = qcxjlyService
                            .queryByPrimaryKey(primaryKey);
                    if (getvo != null
                            && !loginCorp.getPk_corp().equals(
                            getvo.getPk_corp())) {
                        throw new BusinessException("出现数据无权问题，无法修改！");
                    }
                }

                String year = yntXjllqcyePageVO.getDoperatedate().substring(0, 4);
                List<QmclVO> qmcls = qmgzService.yearhasGz(yntXjllqcyePageVO.getPk_corp(),
                        String.valueOf(year));
                for (QmclVO qmvo : qmcls) {
                    if (qmvo.getIsgz().booleanValue()) {
                        throw new BusinessException("已经存在关账的月份，不允许修改期初数据哦！");
                    }
                }
                String[] fields = new String[]{"coperatorid", "doperatedate",
                        "pk_project", "vdirect", "nmny"};

                qcxjlyService.updateByColumn(yntXjllqcyePageVO, fields);
                json.setSuccess(true);
                json.setRows(yntXjllqcyePageVO);
                json.setMsg("修改成功");
                writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "期初现金流量编辑", ISysConstants.SYS_2);
            } catch (BusinessException bs){
                json.setSuccess(false);
                json.setMsg(bs.getMessage());
                log.error(bs.getMessage(), bs);
            }catch (Exception e) {
                json.setSuccess(false);
                json.setMsg("修改失败");
                log.error("修改失败", e);
            }
        } else {
            json.setSuccess(false);
            json.setMsg("修改失败");
        }
        return ReturnData.ok().data(json);
    }

    // 删除记录
    @PostMapping("delete")
    public ReturnData<Grid> onDelete(@MultiRequestBody YntXjllqcyePageVO yntXjllqcyePageVO, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO) {
        Grid json = new Grid();
        if (yntXjllqcyePageVO != null) {
            try {
                String pk_corp = corpVO.getPk_corp();
                // 删除前权限控制
                if (!yntXjllqcyePageVO.getPk_corp().equals(pk_corp)) {
                    throw new BusinessException("无权操作！");
                }
                // 删除前数据安全验证
                // msvo.getPrimaryKey();
                YntXjllqcyePageVO getmsvo = qcxjlyService
                        .queryByPrimaryKey(yntXjllqcyePageVO.getPrimaryKey());
                if (getmsvo != null && !pk_corp.equals(getmsvo.getPk_corp())) {
                    throw new BusinessException("出现数据无权问题，无法删除！");
                }
                qcxjlyService.deleteInfovo(yntXjllqcyePageVO);
                json.setSuccess(true);
                // json.setObj(data);
                json.setMsg("删除成功！");
                writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "期初现金流量删除", ISysConstants.SYS_2);
            } catch (BusinessException bs){
                json.setSuccess(false);
                json.setMsg(bs.getMessage());
                log.error(bs.getMessage(), bs);
            }catch (Exception e) {
                log.error("删除失败！", e);
                json.setSuccess(false);
                json.setMsg("删除失败！" + e.getMessage());
            }
        } else {
            json.setSuccess(false);
            json.setMsg("删除失败！");
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("query")
    public ReturnData<Grid> queryInv(@MultiRequestBody CorpVO corpVO, Page page, String sort, String order) {

        Grid grid = new Grid();
        try {
            CorpVO corpVo = new CorpVO();
            corpVo.setPk_corp(corpVO.getPk_corp());
            List<YntXjllqcyePageVO> list = qcxjlyService.queryInfovo(
                    YntXjllqcyePageVO.class, corpVo, null, sort, order);
            log.info("查询成功！");
            grid.setRows(list == null ? new ArrayList<YntXjllqcyePageVO>()
                    : list);
            grid.setSuccess(true);
            grid.setMsg("查询成功");
            writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "期初现金流量查询", ISysConstants.SYS_2);
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg("查询失败");
        }
        return ReturnData.ok().data(grid);
    }
}
