package com.dzf.zxkj.backup.controller;

import com.dzf.zxkj.backup.service.ICorpService;
import com.dzf.zxkj.backup.service.IDataBackUp;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.base.IOperatorLogService;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.BackupVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
 * 备份与恢复
 */
@RestController
@RequestMapping("/gl/gl_backupact")
@Slf4j
@SuppressWarnings("all")
public class BackupAndRestoreController {

    @Autowired
    private IDataBackUp gl_databackup;

    @Autowired
    private ICorpService corpService;

    @Autowired
    private IOperatorLogService operatorLogService;


    @GetMapping("/query")
    public ReturnData<Json> query(String pk_corp) {
        Json json = new Json();
        try {
            List<BackupVO> bakeupData = gl_databackup.query(pk_corp);
            json.setRows(bakeupData);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败！");
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/save")
    public ReturnData<Json> save(@RequestBody String listvalue) {
        String strlist = listvalue;
//		String strlist = param.get("listvalue"); @RequestParam Map<String, String> param
        Json json = new Json();
        StringBuffer tips = new StringBuffer();
        if (StringUtil.isEmpty(strlist)) {
            tips.append("请选择数据!");
        }

        CorpVO[] listVo = JsonUtils.deserialize(strlist, CorpVO[].class);

        String requestid = UUID.randomUUID().toString();
        String finalid = "DATABACKZXKJID";
        if (listVo != null && listVo.length > 0) {
            saveBatch(listVo, json, tips);
        }
        if (tips.length() > 0) {
            json.setSuccess(false);
            json.setMsg(tips.toString());
        } else {
            json.setMsg("保存成功");
            json.setSuccess(true);
        }

//        writeLogRecord(LogRecordEnum.OPE_KJ_SJWH, "数据备份", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    //线程池批量操作
    private void saveBatch(CorpVO[] cplist, Json json, StringBuffer tips) {
        if (cplist == null || cplist.length == 0) {
            return;
        }
        ExecutorService pool = null;
        try {
            int maxcount = 100;

            if (cplist.length <= maxcount) {
                maxcount = cplist.length;
            }
            pool = Executors.newFixedThreadPool(maxcount);

            List<Future<String>> vc = new Vector<Future<String>>();

            for (CorpVO cpvo : cplist) {
                Future<String> future = pool.submit(new SaveDataBack(cpvo, json, tips));

                vc.add(future);
            }

            for (Future<String> fu : vc) {
                fu.get();
            }
            pool.shutdown();
        } catch (Exception e) {
            log.error("错误", e);
        } finally {
            try {
                if (pool != null) {
                    pool.shutdown();
                }
            } catch (Exception e) {
                log.error("错误", e);
            }

        }
    }

    private class SaveDataBack implements Callable<String> {

        private CorpVO cpvo = null;

        private Json json = null;

        private StringBuffer tips = null;

        public SaveDataBack(CorpVO cpvo, Json json, StringBuffer tips) {
            this.cpvo = cpvo;
            this.json = json;
            this.tips = tips;
        }

        @Override
        public String call() throws Exception {
            String end = "ok";
            String requestid = UUID.randomUUID().toString();
            String finalid = "DATABACKZXKJID_" + cpvo.getPk_corp();
            //TODO
            boolean lock = true;
            try {
                if (lock) {
                    gl_databackup.updatedataBackUp(cpvo);
                }
            } catch (Exception e) {
                if (e instanceof BusinessException) {
                    tips.append("公司名称:" + cpvo.getUnitname() + e.getMessage() + "备份失败!<br/>");
                } else {
                    tips.append("公司名称:" + cpvo.getUnitname() + "备份失败!<br/>");
                }
            } finally {

            }
            return end;
        }

    }

    @PostMapping("/upBackVo")
    public ReturnData<Json> upBackVo(@RequestBody BackupVO data) {
        Json json = new Json();
        try {
            gl_databackup.updateBackVo(data);
            json.setSuccess(true);
            json.setMsg("保存成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "更新失败");
        }

//        writeLogRecord(LogRecordEnum.OPE_KJ_SJWH, "数据备注更新", ISysConstants.SYS_2);

        return ReturnData.ok().data(json);
    }

    @PostMapping("/multiRestore")
    public ReturnData<Json> multiRestore(@MultiRequestBody String listvalue) {
        Json json = new Json();
        StringBuffer tips = new StringBuffer();
        String strlist = listvalue;
        if (StringUtil.isEmpty(strlist)) {
            tips.append("请选择数据!");
        }

        CorpVO[] listVo = JsonUtils.deserialize(strlist, CorpVO[].class);
        if (listVo != null && listVo.length > 0) {
            for (CorpVO cpvo : listVo) {
                try {
                    BackupVO oldvo = gl_databackup.queryNewByCorp(cpvo.getPk_corp());
                    checkData(oldvo, cpvo.getPk_corp());
                    gl_databackup.updatedateReturn(oldvo);
                } catch (Exception e) {
//					printErrorLog(json, log, e, "恢复失败！");
                    tips.append("公司名称:" + cpvo.getUnitname() + "恢复失败!<br/>");
                }
            }
        }
        if (tips.length() > 0) {
            json.setSuccess(false);
            json.setMsg(tips.toString());
        } else {
            json.setMsg("恢复成功");
            json.setSuccess(true);
        }
//        writeLogRecord(LogRecordEnum.OPE_KJ_SJWH, "数据恢复", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/restore")
    public ReturnData<Json> restore(String fid, String pk_corp) {
        Json json = new Json();
        try {
            BackupVO oldvo = gl_databackup.queryByID(fid);
            checkData(oldvo, pk_corp);
            gl_databackup.updatedateReturn(oldvo);
            json.setMsg("恢复成功");
            json.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "恢复失败！");
        }
//        writeLogRecord(LogRecordEnum.OPE_KJ_SJWH, "数据恢复", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }


    @PostMapping("/delete")
    public ReturnData<Json> delete(String fid, String pk_corp) {
        Json json = new Json();
        try {
            BackupVO oldvo = gl_databackup.queryByID(fid);
            checkData(oldvo, pk_corp);
            gl_databackup.delete(oldvo);
            json.setMsg("删除成功");
            json.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "删除失败！");
        }
//        writeLogRecord(LogRecordEnum.OPE_KJ_SJWH, "备份删除", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response, String pk_corp, @RequestParam Map<String, String> pmap) {
        FileInputStream fileis = null;
        ServletOutputStream out = null;
        Json json = new Json();
        try {
            String fid = pmap.get("fid");
            BackupVO oldvo = gl_databackup.queryByID(fid);
            checkData(oldvo, pk_corp);
            fileis = new FileInputStream(oldvo.getFilePath());
            response.setContentType("application/octet-stream");

            String filename = null;
            if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0 || request.getHeader("User-Agent").toUpperCase().indexOf("RV:11") > 0) {
                filename = URLEncoder.encode(oldvo.getFileName(), "UTF-8");
            } else {
                filename = new String(oldvo.getFileName().getBytes("UTF-8"), "ISO8859-1");
            }
            response.addHeader("Content-Disposition", "attachment;filename=" + filename);
            out = response.getOutputStream();
            byte[] buf = new byte[4 * 1024];  // 4K buffer
            int bytesRead;
            while ((bytesRead = fileis.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
            out.flush();
        } catch (Exception e) {
            json.setMsg(e.getMessage());
            json.setSuccess(false);
        } finally {
            if (fileis != null) {
                try {
                    fileis.close();
                } catch (IOException e) {
                    log.error("错误", e);
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("错误", e);
                }
            }
        }
    }

    @PostMapping("/upload")
    public ReturnData<Json> upload(@RequestParam("impfile") MultipartFile file, String pk_corp) {
        Json json = new Json();
        try {
            CorpVO corp = corpService.queryByPk(pk_corp);
            if (file != null) {
                String filename = file.getOriginalFilename();
                gl_databackup.saveUpFile(file.getInputStream(), filename, corp);
            }
            json.setMsg("上传成功");
            json.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "上传失败！");
        }
//        writeLogRecord(LogRecordEnum.OPE_KJ_SJWH, "上传本地备份", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private void checkData(BackupVO vo, String pk_corp) {
        if (vo == null)
            throw new BusinessException("备份数据不存在！");
        if (!pk_corp.equals(vo.getPk_corp()))
            throw new BusinessException("无权操作！");
    }
}
