package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.PzmbbVO;
import com.dzf.zxkj.platform.model.bdset.PzmbhVO;
import com.dzf.zxkj.platform.model.sys.BDabstractsVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.service.bdset.IPzmbhService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 常用凭证模板
 *
 */
@RestController
@RequestMapping("/bdset/gl_cymb")
public class PzmbhController {
    @Autowired
    private IPzmbhService pzmbhService;

    @Autowired
    private IParameterSetService sys_parameteract;

    // 保存
    @PostMapping("/save")
    public ReturnData save(@RequestBody PzmbhVO vo) {
        Json json = new Json();
        setDefaultInfo(vo);
        if (!StringUtils.isEmpty(vo.getPrimaryKey())) {//不是新增情况，校验
            PzmbhVO savedVo = pzmbhService.queryById(vo.getPrimaryKey());
            if (savedVo == null)
                throw new BusinessException("该数据不存在或已删除，请核对!");
            if (!savedVo.getPk_corp().equals(vo.getPk_corp()))
                throw new BusinessException("只能操作当前登录公司权限内的数据");
        }
        PzmbhVO savevo = pzmbhService.save(vo);
        PzmbbVO[] savebody = savevo.getChildren();
        savevo.setChildren(null);
        //
        json.setSuccess(true);
        json.setMsg("保存成功!");
        json.setHead(savevo);
        json.setChilds(setGrid(savebody));
        return ReturnData.ok().data(json);
    }

    //子表数据
    public Grid setGrid(PzmbbVO[] savebody) {
        Grid gr = new Grid();
        gr.setTotal(Long.valueOf(savebody.length));
        gr.setSuccess(true);
        gr.setRows(new ArrayList<>(Arrays.asList(savebody)));
        return gr;
    }

    //赋默认值
    public void setDefaultInfo(PzmbhVO vo) {
        String pk_corp = SystemUtil.getLoginCorpId();
        vo.setPk_corp(pk_corp);
        vo.setCoperatorid(SystemUtil.getLoginUserId());
        vo.setDoperatedate(new DZFDate(new Date()));
        vo.setDr(0);
        PzmbbVO[] bvos = vo.getChildren();
        for (int i = 0; i < bvos.length; i++) {
            bvos[i].setPk_corp(pk_corp);
            bvos[i].setDr(0);
        }
    }

    // 查询
    @GetMapping("/query")
    public ReturnData query(@RequestParam(name = "isZy", required = false) String isZy,
                            String withBody) {
        Grid grid = new Grid();
        String pk_corp = SystemUtil.getLoginCorpId();
        List<PzmbhVO> list = null;
        if ("Y".equals(isZy)) {
            // 和摘要一起展示在前台
            List<BDabstractsVO> listSummaryVo = new ArrayList<BDabstractsVO>();
            YntParameterSet paramSet = sys_parameteract.queryParamterbyCode(pk_corp, "dzf002");
            if (paramSet != null && paramSet.getPardetailvalue() == 0) {
                list = pzmbhService.queryAll(pk_corp);
                if (list != null && list.size() > 0) {
                    BDabstractsVO absVo = null;
                    for (PzmbhVO pVo : list) {
                        absVo = new BDabstractsVO();
                        absVo.setAbstractscode("#muban#" + pVo.getPk_corp_pztemplate_h());
                        absVo.setAbstractsname(pVo.getMemo());
                        listSummaryVo.add(absVo);
                    }
                }
            }
            grid.setRows(listSummaryVo);
        } else {
            if ("Y".equals(withBody)) {
                list = pzmbhService.queryWithBody(pk_corp);
            } else {
                list = pzmbhService.query(pk_corp);
            }
            grid.setRows(list);
        }
        if (list == null || list.size() == 0) {
            grid.setTotal(Long.valueOf(0));
        } else {
            grid.setTotal(Long.valueOf(list.size()));
        }
        return ReturnData.ok().data(grid);
    }

    // 查询带科目的主表
    @GetMapping("/queryAll")
    public ReturnData queryAll() {
        Grid grid = new Grid();
        String pk_corp = SystemUtil.getLoginCorpId();
        List<PzmbhVO> list = pzmbhService.queryAll(pk_corp);
        if (list == null || list.size() == 0) {
            grid.setTotal(Long.valueOf(0));
        } else {
            grid.setTotal(Long.valueOf(list.size()));
        }
        grid.setRows(list);
        return ReturnData.ok().data(grid);
    }

    // 查询子表
    @GetMapping("/queryB")
    public ReturnData queryB(String pk_h_id) {
        Grid grid = new Grid();
        grid.setTotal(Long.valueOf(0));
        grid.setSuccess(false);
        List<PzmbbVO> list = pzmbhService.queryB(pk_h_id);
        if (list != null && list.size() > 0) {
            grid.setTotal(Long.valueOf(list.size()));
            grid.setSuccess(true);
            grid.setRows(list);
        }
        return ReturnData.ok().data(grid);
    }

    // 删除
    @PostMapping("/delete")
    public ReturnData delete(@RequestBody Map<String, String> param) {
        Json json = new Json();
        //验证id和pk_corp
        String ids = param.get("ids");
        String[] idArray = ids.split(",");
        for (String id : idArray) {
            PzmbhVO vo = pzmbhService.queryById(id);
            if (vo == null)
                throw new BusinessException("该数据不存在或已删除，请核对!");
            if (!vo.getPk_corp().equals(SystemUtil.getLoginCorpId()))
                throw new BusinessException("只能操作当前登录公司权限内的数据");
            pzmbhService.delete(id);
        }
        json.setSuccess(true);
        json.setMsg("删除成功!");
        return ReturnData.ok().data(json);
    }

    @PostMapping("/copy")
    public ReturnData copy(@RequestBody Map<String, String> param) {
        String corpsStr = param.get("corps");
        String tmpsStr = param.get("tmps");
        Json json = new Json();
        CorpVO[] corps = JsonUtils.deserialize(corpsStr, CorpVO[].class);
        String msg = pzmbhService.copy(tmpsStr.split(","), corps, SystemUtil.getLoginCorpId());
        json.setSuccess(true);
        json.setMsg(msg);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/getNewCode")
    public ReturnData getNewCode() {
        Json json = new Json();
        String code = pzmbhService.getNewCode(SystemUtil.getLoginCorpId());
        json.setSuccess(true);
        json.setData(code);
        json.setMsg("获取新编码成功");
        return ReturnData.ok().data(json);
    }
}
