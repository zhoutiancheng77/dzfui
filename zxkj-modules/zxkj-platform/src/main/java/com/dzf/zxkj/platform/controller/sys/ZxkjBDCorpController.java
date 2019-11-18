package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/sys/sys_zxkj_corpact")
@Slf4j
public class ZxkjBDCorpController {

    @Autowired
    private IBDCorpTaxService sys_corp_tax_serv;
    @Autowired
    private IUserService userService;

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

}
