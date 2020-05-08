package com.dzf.zxkj.platform.controller.jzcl;


import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintFileSetVo;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetQryVo;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.service.batchprint.IBatchPrintFileSet;
import com.dzf.zxkj.platform.service.batchprint.INewBatchPrintSetTaskSer;
import com.dzf.zxkj.platform.service.batchprint.INewBatchPrintSetTaskSer2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/gl/gl_gdszact")
@Slf4j
public class BatchPrintController extends BaseController {

    @Autowired
    private INewBatchPrintSetTaskSer newbatchprintser;// 任务1 （生成任务，下载任务数据，删除任务数据）

    @Autowired
    private INewBatchPrintSetTaskSer2 newbatchprintser2; // 任务2 （生成任务对应的pdf文件）

    @Autowired
    private IBatchPrintFileSet gl_batchfilesetser;// 设置


    @PostMapping("/queryFileSet")
    public ReturnData<Grid> queryFileSet() {
        Grid grid = new Grid();
        try {
            // 查询设置
            BatchPrintFileSetVo[] filesetvo = gl_batchfilesetser.queryFileSet(getLoginUserId());
            grid.setRows(filesetvo);
            grid.setSuccess(true);
            grid.setMsg("查询成功！");
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "查询失败!");
            log.error("查询失败!", e);
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("/saveFileSet")
    public ReturnData<Grid> saveFileSet(@MultiRequestBody("setvos")  BatchPrintFileSetVo[] setvos) {
        Grid grid = new Grid();
        try {
            // 查询设置
            gl_batchfilesetser.saveFileSet(setvos, getLoginUserId());
            grid.setSuccess(true);
            grid.setMsg("保存成功！");
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "保存失败");
            log.error("保存失败!", e);
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/queryTaskByPeriod")
    public ReturnData<Grid> queryTaskByPeriod(@MultiRequestBody("queryparam") QueryParamVO queryParamvo) {
        Grid grid = new Grid();
        try {
            // 查询设置
            List<BatchPrintSetQryVo> list =  gl_batchfilesetser.queryPrintVOs(queryParamvo.getPk_corp(),getLoginUserId(),queryParamvo.getQjq());
            if (list == null) {
                throw new BusinessException("查询数据为空");
            }
            grid.setRows(getPagedXSZVOs(list.toArray(new BatchPrintSetQryVo[0]),queryParamvo.getPage(), queryParamvo.getRows()));
            grid.setTotal((long)list.size());
            grid.setSuccess(true);
            grid.setMsg("查询成功！");
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "查询失败");
            log.error("查询失败!", e);
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/queryTask")
    public ReturnData<Grid> queryTask(@MultiRequestBody("queryparam") QueryParamVO queryParamvo) {
        Grid grid = new Grid();
        try {
            // 查询归档任务
            List<BatchPrintSetVo> list2 =  newbatchprintser2.queryTask(getLoginUserId());
            if (list2 == null) {
                throw new BusinessException("查询数据为空");
            }
            grid.setRows(getPagedXSZVOs(list2.toArray(new BatchPrintSetVo[0]),queryParamvo.getPage(), queryParamvo.getRows()));
            grid.setTotal((long)list2.size());
            grid.setSuccess(true);
            grid.setMsg("查询成功！");
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "查询失败");
            log.error("查询失败!", e);
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 将查询后的结果分页
     * @param xsZVOS
     * @param page
     * @param rows
     * @return
     */
    private SuperVO[] getPagedXSZVOs(SuperVO[] xsZVOS, int page, int rows){
        int beginIndex = rows * (page-1);
        int endIndex = rows*page;
        if(endIndex>=xsZVOS.length){//防止endIndex数组越界
            endIndex=xsZVOS.length;
        }
        xsZVOS = Arrays.copyOfRange(xsZVOS, beginIndex, endIndex);

        return xsZVOS;
    }
}
