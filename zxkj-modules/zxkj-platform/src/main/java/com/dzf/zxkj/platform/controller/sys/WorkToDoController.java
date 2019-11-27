package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Page;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.jzcl.QmGzBgVo;
import com.dzf.zxkj.platform.model.sys.CorpRoleVO;
import com.dzf.zxkj.platform.model.sys.WorkToDoVo;
import com.dzf.zxkj.platform.service.sys.IWorkToDoService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("sys/sys_work_todo")
public class WorkToDoController extends BaseController {

    @Autowired
    private IWorkToDoService gl_work_todoserv;

    @RequestMapping("query")
    public ReturnData query(@MultiRequestBody String qj, @MultiRequestBody Page pageInfo) {
        Grid json = new Grid();
        String corp = SystemUtil.getLoginCorpId();
        String czr = SystemUtil.getLoginUserId();
        try {
            int page = pageInfo.getPage() == null ? 1 : pageInfo.getPage();
            int rows = pageInfo.getRows() == null ? 100000 : pageInfo.getRows();

            List<QmGzBgVo> reslist = gl_work_todoserv.queryTodo(corp, qj, czr);
            json.setSuccess(true);
            if (reslist != null && reslist.size() > 0) {
                QmGzBgVo[] vos = paginationVOs(reslist.toArray(new QmGzBgVo[0]), page, rows);
                json.setRows(Arrays.asList(vos));
            } else {
                json.setRows(reslist);
            }
            json.setTotal(reslist != null ? (long) reslist.size() : 0);
            json.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
            log.info("查询失败", e);
        }
        return ReturnData.ok().data(json);
    }


    private QmGzBgVo[] paginationVOs(QmGzBgVo vos[], Integer page, Integer size) {
        page = page - 1;
        if (vos == null || vos.length == 0)
            return vos;

        List<QmGzBgVo> voList = new ArrayList<QmGzBgVo>();
        int start = page * size;
        int end = (page + 1) * size;
        for (int i = start; i < vos.length && i < end; i++) {
            voList.add(vos[i]);
        }
        return voList.toArray(new QmGzBgVo[0]);
    }

    @PostMapping("hand")
    public ReturnData hand(@MultiRequestBody String id) {
        Grid json = new Grid();
        try {
            gl_work_todoserv.updateHandToDo(id);
            json.setSuccess(true);
            json.setMsg("处理成功");
        } catch (Exception e) {
            printErrorLog(json, e, "处理失败");
            log.error("处理失败", e);
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("delete")
    public ReturnData delete(@MultiRequestBody String id) {
        Grid json = new Grid();

        try {
            gl_work_todoserv.delteTodo(id);
            json.setSuccess(true);
            json.setMsg("删除成功");
        } catch (Exception e) {
            printErrorLog(json, e, "删除失败");
            log.error("删除失败", e);
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("save")
    public ReturnData save(@MultiRequestBody WorkToDoVo todovo) {
        Grid json = new Grid();
        try {
            todovo.setPk_corp(SystemUtil.getLoginCorpId());
            todovo.setCoperatorid(SystemUtil.getLoginUserId());
            gl_work_todoserv.saveWorkTodo(todovo);
            json.setSuccess(true);
            json.setMsg("设置成功");
        } catch (Exception e) {
            printErrorLog(json, e, "设置失败");
            log.error("设置失败", e);
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("queryuser")
    public ReturnData queryuser() {
        Grid grid = new Grid();
        try {
            List<CorpRoleVO> lists = gl_work_todoserv.getPowUvos(SystemUtil.getLoginCorpId());

            grid.setSuccess(true);
            grid.setMsg("查询成功");
            grid.setRows(lists);
            grid.setTotal((long) lists.size());

        } catch (Exception e) {
            printErrorLog(grid, e, "查询权限失败!");
            log.error("查询权限失败", e);
        }
        return ReturnData.ok().data(grid);
    }

}
