package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.constant.IBillManageConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.BankAccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.bdset.IYHZHService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bdset/gl_yhzhact")
@Slf4j
public class YHZHController extends BaseController {
    @Autowired
    private IYHZHService gl_yhzhserv;
    @Autowired
    private IYntBoPubUtil yntBoPubUtil;

    @Autowired
    private IAccountService accountService;

    @GetMapping("/query")
    public ReturnData<Grid> query(String isnhsty) {
        Grid grid = new Grid();
        try {
            String pk_corp = SystemUtil.getLoginCorpId();
            List<BankAccountVO> list = gl_yhzhserv.query(pk_corp, isnhsty);
            if (list == null || list.size() == 0) {
                grid.setTotal(0L);
            } else {
                grid.setTotal(Long.valueOf(list.size()));
            }
            grid.setRows(list == null ? new ArrayList<BankAccountVO>() : list);
            grid.setMsg("查询成功");
            grid.setSuccess(true);
        } catch (DZFWarpException e) {
            grid.setMsg("查询失败");
            grid.setSuccess(false);
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    @GetMapping("/queryOne")
    public ReturnData<Json> queryOne(String id) {
        Json json = new Json();
        if (!StringUtil.isEmpty(id)) {
            BankAccountVO vo = gl_yhzhserv.queryById(id);
            //设置
            if (vo != null && !StringUtil.isEmpty(vo.getRelatedsubj())) {
                Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(SystemUtil.getLoginCorpId());
                if (accmap != null && accmap.size() > 0) {
                    YntCpaccountVO accvo = accmap.get(vo.getRelatedsubj());
                    if (accvo != null) {
                        vo.setAccountcode(accvo.getAccountcode());
                        vo.setAccountname(accvo.getAccountname());
                    }

                }
            }
            json.setData(vo);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } else {
            json.setSuccess(false);
            json.setMsg("查询失败");
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/save")
    public ReturnData<Json> save(@RequestBody BankAccountVO bankAccountVO) {
        Json json = new Json();
        if (bankAccountVO != null) {
            boolean isAdd = true;
            if (!StringUtil.isEmpty(bankAccountVO.getPrimaryKey())) {
                isAdd = false;
            }
            try {
                if (beforeSave(bankAccountVO, isAdd)) {
                    if (isAdd) {
                        gl_yhzhserv.save(bankAccountVO);
                        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET,
                                "银行账户保存");
                    } else {
                        gl_yhzhserv.update(bankAccountVO,
                                new String[]{"bankcode", "bankname", "bankaccount",
                                        "relatedsubj", "modifyoperid", "modifydatetime"});
                        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "编辑银行账户");
                    }
                    json.setSuccess(true);
                    json.setRows(bankAccountVO);
                    json.setMsg("更新成功");
                } else {
                    json.setSuccess(false);
                    json.setMsg("更新失败");
                }
            } catch (Exception e) {
                json.setSuccess(false);
                json.setMsg("更新失败");
                log.error("更新失败", e);
            }
        } else {
            json.setSuccess(false);
            json.setMsg("更新失败");
        }
        return ReturnData.ok().data(json);
    }

    private boolean beforeSave(BankAccountVO vo, boolean isAdd) {
        boolean result = true;
        if (isAdd) {
            setDefaultValue(vo, isAdd);
        } else {
            setDefaultValue(vo, isAdd);
        }
        return result;
    }

    private void setDefaultValue(BankAccountVO vo, boolean isAdd) {
        if (isAdd) {
            vo.setCoperatorid(SystemUtil.getLoginUserId());
            vo.setDoperatedate(new DZFDate());
        } else {
            vo.setModifyoperid(SystemUtil.getLoginUserId());
            vo.setModifydatetime(new DZFDateTime());
        }
        vo.setPk_corp(SystemUtil.getLoginCorpId());
        vo.setDr(0);
        //启用标识
        vo.setState(IBillManageConstants.QIY_STATUS);
    }

    @PostMapping("/delete")
    public ReturnData<Json> delete(@RequestBody BankAccountVO vo) {
        Json json = new Json();
        if (vo != null) {
            try {
                gl_yhzhserv.delete(vo);
                json.setSuccess(true);
                json.setRows(vo);
                json.setMsg("删除成功");
            } catch (Exception e) {
                log.error("删除失败", e);
                json.setSuccess(false);
                json.setMsg("删除失败");
            }
        } else {
            json.setSuccess(false);
            json.setMsg("删除失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "银行账户删除");
        return ReturnData.ok().data(json);
    }

    /**
     * 启用
     */
    @PostMapping("/qiyong")
    public ReturnData<Json> qiyong(@RequestBody BankAccountVO vo) {
        Json json = new Json();
        if (vo != null) {
            try {
                beforeAuth(vo, IBillManageConstants.QIY_STATUS,
                        SystemUtil.getLoginCorpVo(), SystemUtil.getLoginUserVo());
                gl_yhzhserv.update(vo,
                        new String[]{"state", "modifyoperid", "modifydatetime"});
                json.setSuccess(true);
                json.setRows(vo);
                json.setMsg("启用成功");
            } catch (Exception e) {
                json.setSuccess(false);
                json.setMsg("启用失败");
                log.error("启用失败", e);
            }
        } else {
            json.setSuccess(false);
            json.setMsg("启用失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "银行账户启用");
        return ReturnData.ok().data(json);
    }

    private void beforeAuth(BankAccountVO vo, int flag, CorpVO corpVO, UserVO userVO) {
        if (StringUtil.isEmpty(vo.getPrimaryKey())) {
            throw new BusinessException("该数据未找到，请检查");
        }
        //赋值
        vo.setState(flag);
        vo.setPk_corp(corpVO.getPk_corp());
        vo.setModifyoperid(userVO.getUser_name());
        vo.setModifydatetime(new DZFDateTime());
    }

    @PostMapping("/tingyong")
    public ReturnData<Json> tingyong(@RequestBody BankAccountVO vo) {
        Json json = new Json();
        if (vo != null) {
            try {
                beforeAuth(vo, IBillManageConstants.TINGY_STATUS,
                        SystemUtil.getLoginCorpVo(), SystemUtil.getLoginUserVo());

                gl_yhzhserv.update(vo,
                        new String[]{"state", "modifyoperid", "modifydatetime"});
                json.setSuccess(true);
                json.setRows(vo);
                json.setMsg("停用成功");
            } catch (Exception e) {
                log.error("停用失败", e);
                json.setSuccess(false);
                json.setMsg("停用失败");
            }
        } else {
            json.setSuccess(false);
            json.setMsg("停用失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "银行账户停用");
        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryDjCode")
    public ReturnData<Json> queryDjCode(String pk_corp) {
        Json grid = new Json();
        try {
            if (pk_corp == null) {
                pk_corp = SystemUtil.getLoginCorpId();
            }
            String invcode = yntBoPubUtil.getYhzhCode(pk_corp);
            grid.setData(invcode);
            grid.setSuccess(true);
            grid.setMsg("获取单据号成功");
        } catch (DZFWarpException e) {
            grid.setSuccess(false);
            grid.setMsg("获取单据号失败");
            log.error("获取单据号失败", e);
        }
        return ReturnData.ok().data(grid);
    }
}
