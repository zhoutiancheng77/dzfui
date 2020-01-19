package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.service.sys.IZxkjTaxService;
import com.dzf.zxkj.platform.service.tax.ICorpTaxService;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/sys/sys_zxkj_corpact")
@Slf4j
public class ZxkjBDCorpController  extends BaseController {

    @Autowired
    private IBDCorpTaxService sys_corp_tax_serv;
    @Autowired
    private IUserService userService;
    @Autowired
    private IZxkjTaxService zxkj_taxserv;
    @Autowired
    private ICorpTaxService zxkj_corptaxserv;

    /**
     * 公司查询
     */
    @GetMapping("/query")
    public ReturnData<Grid> query(QueryParamVO paramvo, String[] corpArrays ) {
        Grid grid = new Grid();
        try {
            if (corpArrays != null) {// 只查询新增的情况
                List<CorpTaxVo> listVos = sys_corp_tax_serv.queryTaxVoByIds(corpArrays);
                grid.setTotal((long) (listVos == null ? 0 : listVos.size()));
                grid.setRows(listVos);
                grid.setSuccess(true);
                grid.setMsg("查询成功!");
            } else {// 根据条件查询
                UserVO uservo = SystemUtil.getLoginUserVo();
                Set<String> clist = getCorpids(uservo);
                List<CorpTaxVo> listVos = sys_corp_tax_serv.queryTaxVoByParam(paramvo, uservo);
                //当前登录账号有会计权限的公司
                List<CorpTaxVo> corpvos = filterCorpVo(clist, listVos, paramvo.getCorpname());
                int len = corpvos == null ? 0 : corpvos.size();
                if (len > 0) {
                    grid.setTotal((long) len);
                    grid.setRows(corpvos);
                    grid.setSuccess(true);
                    grid.setMsg("查询成功！");
                } else {
                    grid.setTotal(Long.valueOf(0));
                    grid.setSuccess(true);
                    grid.setMsg("查询结果为空！");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败");
        }
        return ReturnData.ok().data(grid);
    }

    private List<CorpTaxVo> filterCorpVo(Set<String> clist, List<CorpTaxVo> corpvostemp, String corpname) {
        List<CorpTaxVo> restlistcorp = new ArrayList<CorpTaxVo>();

        if (corpvostemp != null && corpvostemp.size() > 0) {
            for (CorpTaxVo cvo : corpvostemp) {
                if (clist.contains(cvo.getPk_corp())) {
                    if(!StringUtil.isEmpty(corpname)){
                        if(cvo.getUnitname().contains(corpname)){
                            restlistcorp.add(cvo);
                        }
                    }else{
                        restlistcorp.add(cvo);
                    }

                }
            }
        }
        return restlistcorp;
    }

    private Set<String> getCorpids(UserVO uservo) {
        List<CorpVO> list = userService.queryPowerCorpKj(uservo.getPrimaryKey());
        Set<String> setlist = new HashSet<String>();
        if (list != null && list.size() > 0) {
            for (CorpVO cvo : list) {
                setlist.add(cvo.getPk_corp());
            }
        }
        return setlist;
    }
    @GetMapping("/queryUser")
    public ReturnData<Grid> queryUser(){
        Grid grid = new Grid();
        try {
            UserVO[] resvos = zxkj_taxserv.queryUser(SystemUtil.getLoginCorpId());
            resvos = (UserVO[]) QueryDeCodeUtils.decKeyUtils(new String[] { "user_name" }, resvos, 1);
            grid.setMsg("查询成功!");
            grid.setRows(Arrays.asList(resvos));
            grid.setTotal((long) resvos.length);
            grid.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败");
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("/delchargHis")
    public ReturnData<Json> delchargHis(@MultiRequestBody String id,
                                        @MultiRequestBody String type,
                                        @MultiRequestBody String pk_gs){
        Json json = new Json();
        try {
            String pk = id;//历史id
            String pk_corp = pk_gs;
            if(StringUtil.isEmpty(pk_corp)){
                pk_corp = SystemUtil.getLoginCorpId();
            }else{
                checkOwnCorp(pk_corp);
            }

            if("specHis".equals(type)){
                sys_corp_tax_serv.deleteSpecChargHis(pk_corp, pk);
            }else{
                sys_corp_tax_serv.deletechargHis(pk_corp, pk);
            }

            json.setSuccess(true);
            json.setMsg("删除成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "删除失败");
        }

        return ReturnData.ok().data(json);
    }

    @PostMapping("/saveCharge")
    public ReturnData<Json> saveCharge(@RequestBody CorpTaxVo data){
        Json json = new Json();
        StringBuffer msg = new StringBuffer();
        try {
            sys_corp_tax_serv.saveCharge(data, SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId(), msg);
            json.setData(data);
            json.setSuccess(true);
            json.setMsg("保存成功");

//            //日志记录
//            String sr = msg.length() > 0 ? "征收方式调整：修改"+msg.toString() : "";
//            if(!StringUtil.isEmpty(sr)){
//                writeLogRecord(LogRecordEnum.OPE_KJ_QYXX.getValue(),
//                        sr, ISysConstants.SYS_2);
//            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "保存失败");
        }

        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryChargeHis")
    public ReturnData<Grid> queryChargeHis(String type, String pk_gs){
        Grid grid = new Grid();
        try{

            List list = null;
            String pk_corp = pk_gs;
            if(StringUtil.isEmpty(pk_corp)){
                pk_corp = SystemUtil.getLoginCorpId();
            }else{
                checkOwnCorp(pk_corp);
            }
            if("specHis".equals(type)){
                list =sys_corp_tax_serv.querySpecChargeHis(pk_corp);
            }else{
                list =zxkj_corptaxserv.queryChargeHis(pk_corp);
            }
            grid.setTotal(list == null || list.size()==0 ? 0L : list.size());
            grid.setRows(list);
            grid.setSuccess(true);
            grid.setMsg("历史记录查询成功");
        }catch(Exception e){
            log.error(e.getMessage(), e);
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "历史记录查询失败");
        }

        return ReturnData.ok().data(grid);
    }

}
