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
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.batchprint.IBatchPrintFileSet;
import com.dzf.zxkj.platform.service.batchprint.INewBatchPrintSetTaskSer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gl/gl_gdszact")
@Slf4j
public class BatchPrintController extends BaseController {

    @Autowired
    private INewBatchPrintSetTaskSer newbatchprintser;// 任务

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

    @PostMapping("/saveFileTask")
    public ReturnData<Grid> saveFileTask(@RequestBody Map<String, String> pmap1) {
        Grid grid = new Grid();
        try {
            // 查询设置
            newbatchprintser.saveTask(pmap1.get("corpids"),getLoginUserId(),pmap1.get("type"),
                    pmap1.get("period"), pmap1.get("vprintdate"), pmap1.get("bsysdate"));
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
            List<BatchPrintSetQryVo> list =  newbatchprintser.queryPrintVOs(queryParamvo.getPk_corp(),getLoginUserId(),queryParamvo.getQjq());
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


    @PostMapping("/deleteTask")
    public ReturnData<Grid> deleteTask(@MultiRequestBody("setvo") BatchPrintSetVo setvo) {
        Grid grid = new Grid();
        try {
            // 执行任务
            newbatchprintser.deleteTask(setvo.getPrimaryKey());
            grid.setSuccess(true);
            grid.setMsg("删除成功！");
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "删除任务失败");
            log.error("删除任务失败!", e);
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("down")
    public void down( @RequestParam Map<String, String> pmap1,@MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO,
                     HttpServletResponse response) {
        ServletOutputStream out = null;
        Grid json = new Grid();
        try {
            Object[] objs = newbatchprintser.downLoadFile(pmap1.get("cid"),pmap1.get("id"));

            if(objs == null ||  objs.length ==0){
                throw new BusinessException("暂无数据!");
            }

            byte[] bytes = (byte[]) objs[0];

            if(bytes == null ||  bytes.length ==0 ){
                throw new BusinessException("暂无数据!");
            }

            response.addCookie(new Cookie("downsuccess", "1"));
            response.setContentType("application/octet-stream");
            String contentDisposition = "attachment;filename=" + URLEncoder.encode((String)objs[1], "UTF-8")
                    + ";filename*=UTF-8''" + URLEncoder.encode((String)objs[1], "UTF-8");
            response.addHeader("Content-Disposition", contentDisposition);
            out = response.getOutputStream();
            out.write((byte[])objs[0]);
            out.flush();
            out.close();
        } catch (Exception e) {
            json.setMsg(e.getMessage());
            json.setSuccess(false);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }
    }
    @PostMapping("batchdown")
    public void batchdown( @RequestParam Map<String, String> pmap1,@MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO,
                           HttpServletResponse response){
        ServletOutputStream out = null;
        Grid json = new Grid();
        try {
            String ids = pmap1.get("ids");
            Object[] objs = newbatchprintser.downBatchLoadFiles(corpVO.getFathercorp(), ids.split(","));
            if(objs == null ||  objs.length ==0){
                throw new BusinessException("暂无数据!");
            }

            byte[] bytes = (byte[]) objs[0];

            if(bytes == null ||  bytes.length ==0 ){
                throw new BusinessException("暂无数据!");
            }

            response.addCookie(new Cookie("downsuccess", "1"));
            response.setContentType("application/octet-stream");
            String contentDisposition = "attachment;filename=" + URLEncoder.encode((String)objs[1], "UTF-8")
                    + ";filename*=UTF-8''" + URLEncoder.encode((String)objs[1], "UTF-8");
            response.addHeader("Content-Disposition",  contentDisposition);
            out = response.getOutputStream();
            out.write((byte[])objs[0]);
            out.flush();
        } catch (Exception e) {
            json.setMsg(e.getMessage());
            json.setSuccess(false);
            log.error(e.getMessage(),e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }
//		writeJson(json);
    }

    @PostMapping("/execTask")
    public ReturnData<Grid> execTask() {
        Grid grid = new Grid();
        try {
            // 执行任务
             newbatchprintser.execTask(getLoginUserId());
            grid.setSuccess(true);
            grid.setMsg("任务执行成功！");
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "任务执行失败");
            log.error("任务执行失败!", e);
        }
        return ReturnData.ok().data(grid);
    }



    @PostMapping("/queryTask")
    public ReturnData<Grid> queryTask(@MultiRequestBody("queryparam") QueryParamVO queryParamvo) {
        Grid grid = new Grid();
        try {
            // 查询归档任务
            List<BatchPrintSetVo> list2 =  newbatchprintser.queryTask(getLoginUserId(),"");
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
