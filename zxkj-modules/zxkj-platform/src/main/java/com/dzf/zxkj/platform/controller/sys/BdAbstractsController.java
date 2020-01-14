package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.sys.BDabstractsVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.sys.IBDabstractsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 摘要 controller 类
 */
@RestController
@RequestMapping("/sys/sys_zyact")
@Slf4j
public class BdAbstractsController extends BaseController {
    @Autowired
    private IBDabstractsService sys_zyserv;

    @GetMapping("/query")
    public ReturnData<Grid> query(@MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO){
        Grid grid = new Grid();
        try {
            BDabstractsVO[] vos = sys_zyserv.query(corpVO.getPk_corp());
            if (vos == null || vos.length == 0) {
                grid.setTotal(0L);
                grid.setRows(new ArrayList<BDabstractsVO>());
            } else {
                grid.setTotal(Long.valueOf(vos.length));
                grid.setRows(new ArrayList<BDabstractsVO>(Arrays.asList(vos)));
            }
            grid.setMsg("查询成功");
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setMsg("查询失败");
            if(e instanceof BusinessException){
                grid.setMsg(e.getMessage());
            }
            grid.setSuccess(false);
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("/save")
    public ReturnData<Json> save(@MultiRequestBody("bDabstractsVO") BDabstractsVO bDabstractsVO,@MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO){
        Json json = new Json();
        StringBuffer sf = new StringBuffer();
        if (bDabstractsVO != null) {
            try{
                bDabstractsVO.setDoperatedate(new DZFDate());
                bDabstractsVO.setDr(0);
                bDabstractsVO.setPk_corp(corpVO.getPk_corp());
                sys_zyserv.existCheck(bDabstractsVO);
                String pk_abstract = bDabstractsVO.getPk_abstracts();
                bDabstractsVO = sys_zyserv.save(bDabstractsVO);
                json.setSuccess(true);
                json.setRows(bDabstractsVO);
                json.setMsg("保存成功！");
                // 操作日志
                sf.append("新增常用摘要成功");
                if(!StringUtil.isEmpty(pk_abstract)){
                    sf.setLength(0);
                    sf.append("修改常用摘要成功");
                }
            }catch(Exception e){
                json.setMsg("保存失败");
                if(e instanceof BusinessException){
                    json.setMsg(e.getMessage());
                }
                json.setSuccess(false);
                log.error("保存失败", e);
            }
        }else{
            json.setMsg("保存失败！");
            json.setSuccess(false);
        }
        //日志记录
        doRecord(sf,corpVO);
        return ReturnData.ok().data(json);
    }


    private void doRecord (StringBuffer sf, CorpVO corpVO) {
        if(sf.length() > 0){
            writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, sf.toString(), ISysConstants.SYS_2);
        }
    }

    @PostMapping("/onDelete")
    public ReturnData<Json> onDelete(@MultiRequestBody("bavo") BDabstractsVO bavo,@MultiRequestBody CorpVO corpVO){
        Json json = new Json();
        StringBuffer sf = new StringBuffer();
        if (bavo != null) {
            try{
                bavo = sys_zyserv.queryByID(bavo.getPk_abstracts());
                if (bavo == null) {
                    throw new BusinessException("摘要不存在，或已被删除！");
                }
                sys_zyserv.delete(bavo);
                json.setSuccess(true);
                json.setRows(bavo);
                json.setMsg("删除成功！");
                sf.append("删除常用摘要成功");
            }catch(Exception e){
                json.setMsg("删除失败");
                if(e instanceof BusinessException){
                    json.setMsg(e.getMessage());
                }
                json.setSuccess(false);
                log.error("删除失败", e);
            }
        }else{
            json.setSuccess(false);
            json.setMsg("删除失败！");
        }
        //日志记录
        doRecord(sf,corpVO);
        return ReturnData.ok().data(json);
    }

    /**
     * 选中当前公司参照上级对应的摘要信息
     */
    @GetMapping("/queryParentZy")
    public ReturnData<Grid> queryParentZy(@MultiRequestBody CorpVO corpVO){
        Grid grid = new Grid();
        try {
            BDabstractsVO[] vos = sys_zyserv.queryParent(corpVO.getPk_corp());
            if (vos == null || vos.length == 0) {
                grid.setTotal(0L);
                grid.setRows(new ArrayList<BDabstractsVO>());
            } else {
                grid.setTotal(Long.valueOf(vos.length));
                grid.setRows(new ArrayList<BDabstractsVO>(Arrays.asList(vos)));
            }
            grid.setMsg("查询成功");
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setMsg("查询失败");
            if(e instanceof BusinessException){
                grid.setMsg(e.getMessage());
            }
            grid.setSuccess(false);
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    //会计工厂保存摘要，公司pk为图片pk
    @PostMapping("/saveByFct")
    public ReturnData<Json> saveByFct(@MultiRequestBody("bdAbstractsVO") BDabstractsVO vo, @RequestParam("id") String corpid){
        Json json = new Json();
        if (vo != null) {
            try{
                vo.setDoperatedate(new DZFDate());
                vo.setPk_corp(corpid);
                vo.setDr(0);
                sys_zyserv.existCheck(vo);
                vo = sys_zyserv.save(vo);
                json.setSuccess(true);
                json.setRows(vo);
                json.setMsg("保存成功！");
            }catch(Exception e){
                json.setSuccess(false);
                if(e instanceof BusinessException){
                    json.setMsg(e.getMessage());
                }
                json.setMsg("保存失败");
                log.error("保存失败", e);
            }
        }else{
            json.setMsg("保存失败！");
            json.setSuccess(false);
        }
        return ReturnData.ok().data(json);
    }
}
