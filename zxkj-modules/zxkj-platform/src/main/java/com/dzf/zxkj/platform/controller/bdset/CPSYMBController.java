package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.YntCptransmbBVO;
import com.dzf.zxkj.platform.model.bdset.YntCptransmbHVO;
import com.dzf.zxkj.platform.service.bdset.ICPSYMBService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
            for (YntCptransmbHVO yntCptransmbHVO : list) {
                List<YntCptransmbBVO> yntCptransmbBVOS = gl_cpsymbserv.queryChildsById(yntCptransmbHVO.getPk_corp_transtemplate_h(), yntCptransmbHVO.getPk_corp());
                if (yntCptransmbBVOS != null && yntCptransmbBVOS.size() > 0) {
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

    //保存
    @PostMapping("save")
    public ReturnData<Grid> save(@MultiRequestBody YntCptransmbHVO headvo, @MultiRequestBody YntCptransmbBVO[] bodyvos) {

        Grid json = new Grid<>();
        if (headvo != null && bodyvos != null) {
            try {
                String corpid = SystemUtil.getLoginCorpId();
                String date = SystemUtil.getLoginDate();
                String userid = SystemUtil.getLoginUserId();
                YntCptransmbHVO savevo = gl_cpsymbserv.save(headvo, corpid, bodyvos, corpid, date, userid);
                YntCptransmbBVO[] savebody = (YntCptransmbBVO[]) savevo.getChildren();
                savevo.setChildren(null);
                json.setSuccess(true);
                json.setMsg("保存成功!");
                savevo.setChildren(savebody);
                json.setRows(savevo);

            } catch (Exception e) {
                printErrorLog(json, e, "保存失败！");
                log.error("保存失败！", e);
            }
        }
        return ReturnData.ok().data(json);
    }


    //删除
    @PostMapping("delete")
    public ReturnData delete(@MultiRequestBody YntCptransmbHVO data) {
        Grid json = new Grid();
        try {
            //校验id和pk_corp
            YntCptransmbHVO vo = gl_cpsymbserv.queryById(data.getPrimaryKey());
            if (vo == null)
                throw new BusinessException("该数据不存在或已删除！");
            if (!vo.getPk_corp().equals(SystemUtil.getLoginCorpId()))
                throw new BusinessException("只能操作该公司权限内的数据");
//				gl_cpsymbserv.delete(data);
            gl_cpsymbserv.delete(vo);
            json.setSuccess(true);
            json.setRows(data);
            json.setMsg("成功");
        } catch (Exception e) {
            printErrorLog(json, e, "删除失败!");
            log.error("删除失败!", e);
        }

        return ReturnData.ok().data(json);
    }

}
