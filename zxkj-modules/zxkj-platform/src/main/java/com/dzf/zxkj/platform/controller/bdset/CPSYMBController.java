package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.common.base.BaseController;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.platform.model.bdset.YntCptransmbBVO;
import com.dzf.zxkj.platform.model.bdset.YntCptransmbHVO;
import com.dzf.zxkj.platform.service.bdset.ICPSYMBService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bdset/gl_cpsymb")
@Slf4j
public class CPSYMBController extends BaseController {

    @Autowired
    private ICPSYMBService gl_cpsymbserv;
    @Autowired
    private SingleObjectBO singleObjectBO;

    @GetMapping("query")
    public ReturnData query() {
        Grid grid = new Grid();
        try {
            List<YntCptransmbHVO> list = gl_cpsymbserv.query(SystemUtil.getLoginCorpId());
            for(YntCptransmbHVO yntCptransmbHVO : list){
                List<YntCptransmbBVO> yntCptransmbBVOS = gl_cpsymbserv.queryChildsById(yntCptransmbHVO.getPk_corp_transtemplate_h(), yntCptransmbHVO.getPk_corp());
                if(yntCptransmbBVOS != null && yntCptransmbBVOS.size() > 0){
                    yntCptransmbHVO.setChildren(yntCptransmbBVOS.stream().toArray(YntCptransmbBVO[]::new));
                }
            }
            if (list != null && list.size() > 0) {
                grid.setSuccess(true);
                grid.setTotal((long) list.size());
                grid.setRows(list);
            } else {
                grid.setTotal(Long.valueOf(0));
                grid.setMsg("查询数据为空");
            }
        } catch (Exception e) {
            printErrorLog(grid, e, "查询出错！");
            log.error("查询出错！", e);
        }
        //日志记录
        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET,
                "损益结转模板设置", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    @GetMapping("queryChild")
    public ReturnData queryChild(String hid, String pk_corp) {
        Grid grid = new Grid();
        grid.setTotal(Long.valueOf(0));
        grid.setSuccess(false);
        try {
            List<YntCptransmbBVO> list = gl_cpsymbserv.queryChildsById(hid, pk_corp);
            if (list != null && list.size() > 0) {
                if (!list.get(0).getPk_corp().equals(SystemUtil.getLoginCorpId()) && !list.get(0).getPk_corp().equals("000001"))
                    throw new BusinessException("只能操作该公司权限内的数据");
                grid.setTotal(Long.valueOf(list.size()));
                grid.setSuccess(true);
                grid.setRows(list);
            } else {
                throw new BusinessException("该数据不存在或已删除，请核对!");
            }
            return ReturnData.ok().data(grid);
        } catch (Exception e) {
            Grid j = new Grid();
            printErrorLog(j, e, "查询子表失败！");
            log.error("查询子表失败！", e);
            return ReturnData.ok().data(j);
        }
    }


}
