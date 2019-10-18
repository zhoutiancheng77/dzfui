package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.vo.QueryPageVO;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/bdset/gl_fzhsact")
@Slf4j
public class AuxiliaryAccountController {

    @Autowired
    private IAuxiliaryAccountService gl_fzhsserv;

    @GetMapping("/queryH")
    public ReturnData<Grid> queryType(@RequestParam("pk_corp") String pk_corp,
                                      @RequestParam("isfull") String isfull,
                                      @MultiRequestBody CorpVO corpVO) {
        Json json = new Json();
        try {
            if (StringUtil.isEmpty(pk_corp)) {
                pk_corp = corpVO.getPk_corp();
            }
            AuxiliaryAccountHVO[] hvos;
            if (isfull != null && "Y".equals(isfull)) {
                hvos = gl_fzhsserv.queryH(pk_corp);
            } else {
                hvos = gl_fzhsserv.queryHCustom(pk_corp);
            }
            json.setRows(hvos);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (DZFWarpException e) {
            json.setMsg("查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryFzhs")
    public ReturnData<Json> queryAllType(@MultiRequestBody CorpVO corpVO) {
        Json json = new Json();
        try {
            AuxiliaryAccountHVO[] hvos = gl_fzhsserv.queryH(corpVO.getPk_corp());
            json.setRows(hvos);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (DZFWarpException e) {
            json.setMsg("查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("/onSeal")
    public ReturnData<Json> seal(@RequestParam("id") String id) {
        Json json = new Json();
        if (StringUtil.isEmpty(id)) {
            json.setMsg("参数为空！");
        } else {
            try {
                gl_fzhsserv.onSeal(id);
                json.setSuccess(true);
            } catch (DZFWarpException e) {
                json.setMsg("修改失败!");
                log.error("修改失败!", e);
            }
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("/unSeal")
    public ReturnData<Json> unSeal(@RequestParam("id") String id) {
        Json json = new Json();
        if (StringUtil.isEmpty(id)) {
            json.setMsg("参数为空！");
        } else {
            try {
                gl_fzhsserv.unSeal(id);
                json.setSuccess(true);
            } catch (DZFWarpException e) {
                json.setMsg("修改失败!");
                log.error("修改失败!", e);
            }
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryB")
    public ReturnData<Json> queryArchive(@RequestParam("page") Integer page,
                                         @RequestParam("rows") Integer rows,
                                         @RequestParam("id") String hid,
                                         @RequestParam("kmid") String kmid,
                                         @RequestParam("billtype") String billtype,
                                         @RequestParam("type") String type,
                                         @RequestParam("isfenye") String isfenye,
                                         @RequestParam("pk_corp") String pk_corp,
                                         @MultiRequestBody CorpVO corpVO) {
        Json json = new Json();
        if (StringUtil.isEmpty(hid)) {
            json.setMsg("参数为空！");
        } else {
            try {

                if (StringUtil.isEmpty(pk_corp)) {
                    pk_corp = corpVO.getPk_corp();
                }
                AuxiliaryAccountBVO[] bvos = new AuxiliaryAccountBVO[0];
                if (StringUtil.isEmpty(billtype)) {
                    if ("Y".equals(isfenye)) {
                        // 分页
                        page = page == null ? 1 : page;
                        rows = rows == null ? 10 : rows;
                        QueryPageVO pagevo = gl_fzhsserv.queryBodysBypage(hid, pk_corp, kmid, page, rows,type);
                        json.setTotal(Long.valueOf(pagevo.getTotal()));
                        json.setRows(pagevo.getPagevos());
                    } else {
                        bvos = gl_fzhsserv.queryB(hid, pk_corp, kmid);
                        if (bvos != null && bvos.length > 0) {
                            bvos = Arrays.asList(bvos).stream().filter(v -> v.getSffc() == null || v.getSffc() == 0)
                                    .toArray(AuxiliaryAccountBVO[]::new);
                        }
                    }
                } else {
                    List<AuxiliaryAccountBVO> list = gl_fzhsserv.queryPerson(hid, pk_corp, billtype);
                    if (list != null && list.size() > 0) {
                        bvos = list.stream().filter(v -> v.getSffc() == null || v.getSffc() == 0)
                                .toArray(AuxiliaryAccountBVO[]::new);
                    }
                }
                json.setRows(bvos);
                json.setMsg("查询成功");
                json.setSuccess(true);
            } catch (DZFWarpException e) {
                json.setMsg("查询失败!");
                log.error("查询失败!", e);
                json.setRows(new AuxiliaryAccountBVO[0]);
            }
        }
        return ReturnData.ok().data(json);
    }
}
