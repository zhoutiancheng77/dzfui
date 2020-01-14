package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.ExrateVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.bdset.IHLService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/bdset/gl_bdhlact")
@Slf4j
public class HLController extends BaseController {
    @Autowired
    private IHLService gl_bdhlserv;

    @PostMapping("/save")
    public ReturnData<Json> save(@RequestBody ExrateVO exrateVO, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO) {
        Json json = new Json();
        StringBuffer sf = new StringBuffer();
        try {
            if (!StringUtil.isEmptyWithTrim(exrateVO.getPk_exrate())) {
                //校验
                ExrateVO vo = gl_bdhlserv.queryById(exrateVO.getPrimaryKey());
                if (vo == null)
                    throw new BusinessException("该数据不存在或已删除，请核对！");

                setDefaultValue(exrateVO, corpVO, userVO);
                gl_bdhlserv.update(exrateVO);//更新
                sf.append("更新汇率："+exrateVO.getCurrencyname()+"，成功");
            } else {
                setDefaultValue(exrateVO, corpVO, userVO);
                gl_bdhlserv.save(exrateVO);//新增
                sf.append("新增汇率："+exrateVO.getCurrencyname()+"，成功");
            }
            json.setSuccess(true);
            json.setRows(exrateVO);
            json.setMsg("保存成功！");
        } catch (Exception e) {
            json.setMsg("保存失败！");
            if (e instanceof BusinessException) {
                json.setMsg(e.getMessage());
            }
            json.setSuccess(false);
            log.error("保存失败！", e);
        }
        // 日志操作
        doRecord(sf);
        return ReturnData.ok().data(json);
    }

    private void doRecord (StringBuffer sf) {
        if(sf.length() > 0){
            writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, sf.toString(), ISysConstants.SYS_2);
        }
    }

    /**
     * 赋默认值
     */
    public void setDefaultValue(ExrateVO vo, CorpVO corpVO, UserVO userVO) {
        boolean isAdd = StringUtil.isEmptyWithTrim(vo.getPk_exrate());
        String corpid = corpVO.getPk_corp();
        String userid = userVO.getCuserid();
        if (isAdd) {
            vo.setPk_corp(corpid);
            vo.setCreator(userid);
            vo.setCreatetime(new DZFDateTime());
            vo.setDr(0);
        } else {
            vo.setModifier(userid);
            vo.setModifytime(new DZFDateTime());
            vo.setDr(0);
        }
    }

    @GetMapping("/query")
    public ReturnData<Grid> query(@MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        try {
            List<ExrateVO> vos = gl_bdhlserv.query(corpVO.getPk_corp());
            if (vos == null || vos.size() == 0) {
                grid.setTotal(0L);
                grid.setRows(new ArrayList<ExrateVO>());
            } else {
                // 解码创建人（历史数据）
                for (ExrateVO exrateVO : vos) {
                    if (exrateVO.getCreatorname() != null)
                        try {
                            exrateVO.setCreatorname(CodeUtils1.deCode(exrateVO.getCreatorname()));
                        } catch (Exception e) {
                        }
                }
                grid.setTotal(Long.valueOf(vos.size()));
                grid.setRows(vos);
            }
            //grid.setRows(vos == null ? new ArrayList<ExrateVO>() : vos);
            grid.setMsg("查询成功！");
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setMsg("查询失败！");
            if (e instanceof BusinessException) {
                grid.setMsg(e.getMessage());
            }
            grid.setSuccess(false);
            log.error("查询失败！", e);
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 好像没有用
     *
     * @param exrateVO
     * @return
     */
    @GetMapping("/queryOne")
    public ReturnData<Json> queryOne(@RequestBody ExrateVO exrateVO) {
        Json json = new Json();
        try {
            ExrateVO vo = gl_bdhlserv.queryById(exrateVO.getPk_exrate());
            // json.setSuccess(true);
            // json.setRows(vo);
            // json.setMsg("保存成功！");
            return ReturnData.ok().data(vo);
        } catch (Exception e) {
            json.setMsg("保存失败！");
            if (e instanceof BusinessException) {
                json.setMsg(e.getMessage());
            }
            json.setSuccess(false);
            log.error("保存失败！", e);
        }

        return ReturnData.ok().data(json);
    }

    @PostMapping("/delete")
    public ReturnData<Json> delete(@RequestBody ExrateVO ervo) throws BusinessException {
        Json json = new Json();
        StringBuffer sf = new StringBuffer();
        if (ervo != null) {
            try{
//                ervo = gl_bdhlserv.queryById(ervo.getPrimaryKey());
//                if (ervo == null)
//                    throw new BusinessException("该数据不存在或已删除，请核对！");
                gl_bdhlserv.delete(ervo);
                json.setSuccess(true);
                json.setRows(ervo);
                json.setMsg("删除成功！");
                sf.append("删除汇率："+ervo.getCurrencyname()+"，成功");
            }catch(Exception e){
                json.setMsg("删除失败！");
                if(e instanceof BusinessException){
                    json.setMsg(e.getMessage());
                }
                json.setSuccess(false);
                log.error("删除失败！", e);
            }
        }else{
            json.setSuccess(false);
            json.setMsg("删除失败！");
        }
        // 日志操作
        doRecord(sf);
        return ReturnData.ok().data(json);
    }
}
