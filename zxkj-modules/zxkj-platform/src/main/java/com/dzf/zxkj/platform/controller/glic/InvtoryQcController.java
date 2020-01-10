package com.dzf.zxkj.platform.controller.glic;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.glic.InventoryQcVO;
import com.dzf.zxkj.platform.service.glic.IInventoryQcService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 存货期初
 *
 */
@RestController
@RequestMapping("/glic/gl_icinvqc")
@Slf4j
public class InvtoryQcController extends BaseController {
    @Autowired
    private IInventoryQcService gl_ic_invtoryqcserv;

    @GetMapping("/query")
    public ReturnData query(@RequestParam Map<String, String> param) {
        Grid grid = new Grid();
        List<InventoryQcVO> list = gl_ic_invtoryqcserv.query(SystemUtil.getLoginCorpId());
        String isfenye = param.get("isfenye");
        QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
        InventoryQcVO[]  vos = null;
        if (list != null && list.size() > 0) {
            // 分页
            if("Y".equals(isfenye)) {
                int page = queryParamvo.getPage();
                int rows = queryParamvo.getRows();
                vos = getPagedZZVOs(list.toArray(new InventoryQcVO[list.size()]), page, rows);
            }else {
                vos = list.toArray(new InventoryQcVO[list.size()]);
            }
        }
        grid.setTotal(vos == null ? 0L : vos.length );
        grid.setRows(vos == null ? new InventoryQcVO[0] : vos);
        grid.setMsg("查询成功");
        grid.setSuccess(true);
        return ReturnData.ok().data(grid);
    }

    // 将查询后的结果分页
    private InventoryQcVO[] getPagedZZVOs(InventoryQcVO[] PzglPagevos, int page, int rows) {
        int beginIndex = rows * (page - 1);
        int endIndex = rows * page;
        if (endIndex >= PzglPagevos.length) {// 防止endIndex数组越界
            endIndex = PzglPagevos.length;
        }
        PzglPagevos = Arrays.copyOfRange(PzglPagevos, beginIndex, endIndex);
        return PzglPagevos;
    }

    @PostMapping("/save")
    public ReturnData save(@RequestBody InventoryQcVO data) {
        Json json = new Json();
        data.setPk_corp(SystemUtil.getLoginCorpId());
        InventoryQcVO vo = gl_ic_invtoryqcserv.save(SystemUtil.getLoginUserId(),SystemUtil.getLoginCorpId(),data);
        json.setRows(vo);
        json.setMsg("保存成功");
        json.setSuccess(true);
        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL,"存货期初，保存", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/onDelete")
    public ReturnData onDelete(@RequestBody InventoryQcVO[] vos) {
        Json json = new Json();
//        String rows = map.get("rows");
//        if (DZFValueCheck.isEmpty(rows)) {
//            throw new BusinessException("数据为空,删除失败!");
//        }
//        InventoryQcVO[] vos = JsonUtils.deserialize(rows, InventoryQcVO[].class);
        if (vos == null || vos.length == 0) {
            throw new BusinessException("数据为空,删除失败!");
        }
        gl_ic_invtoryqcserv.delete(vos);
        json.setMsg("删除成功");
        json.setSuccess(true);

        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL,"存货期初，删除", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/sync")
    public ReturnData sync(@RequestParam("date") String date) {
        Json json = new Json();
        gl_ic_invtoryqcserv.processSyncData(SystemUtil.getLoginUserId(),
                SystemUtil.getLoginCorpId(), date);
        json.setMsg("同步成功");
        json.setSuccess(true);
        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL,"存货期初，同步", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/updateDate")
    public ReturnData updateDate(@RequestParam("date") String date) {
        Json json = new Json();
        gl_ic_invtoryqcserv.updateDate( SystemUtil.getLoginCorpId(), date);
        json.setMsg("修改成功");
        json.setSuccess(true);
        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL,"存货期初，修改启用期间", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/importExcel")
    public ReturnData importExcel(HttpServletRequest request) {
        Json json = new Json();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile infile = multipartRequest.getFile("impfile");
        if (infile == null) {
            throw new BusinessException("请选择导入文件!");
        }

        String date = multipartRequest.getParameter("date");
        String fileName = infile.getOriginalFilename();
        String   fileType =fileName.substring(fileName.lastIndexOf(".") + 1,
                fileName.length());

        String msg = gl_ic_invtoryqcserv.processImportExcel(SystemUtil.getLoginCorpVo(),
                SystemUtil.getLoginUserId(), fileType, infile, date);
        json.setMsg(msg);
        json.setSuccess(true);

        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL,"存货期初，导入", ISysConstants.SYS_2);
//        writeJson(json);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/expExcel")
    public void expExcel(HttpServletResponse response, @RequestParam Map<String, String> pmap) {
        OutputStream toClient = null;
        try {
            response.reset();
            String  fileName = "cunhuoqichu.xls";
            // 设置response的Header
            String date = "存货期初模板";
            String exName = new String(date.getBytes("GB2312"), "ISO_8859_1");
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(exName + ".xls"));
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            byte[] length = expExcel(toClient, fileName);

            String srt2 = new String(length, "UTF-8");
            response.addHeader("Content-Length", srt2);
            toClient.flush();
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("excel导出错误", e);
        } catch (Exception e) {
            log.error("excel导出错误", e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (Exception e) {
                log.error("excel导出错误", e);
            }
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (Exception e) {
                log.error("excel导出错误", e);
            }
            writeLogRecord(LogRecordEnum.OPE_KJ_CHGL,
                    "存货期初模板导出", ISysConstants.SYS_2);
        }
    }

    private byte[] expExcel(OutputStream out, String fileName) throws Exception {
        ByteArrayOutputStream bos = null;
        InputStream is = null;
        try {
            Resource exportTemplate = new ClassPathResource(DZFConstant.DZF_KJ_EXCEL_TEMPLET + fileName);
            is = exportTemplate.getInputStream();
            bos = new ByteArrayOutputStream();
            if (fileName.indexOf(".xlsx") > 0) {
                XSSFWorkbook xworkbook = new XSSFWorkbook(is);
                is.close();
                bos = new ByteArrayOutputStream();
                xworkbook.write(bos);
            } else {
                HSSFWorkbook gworkbook = new HSSFWorkbook(is);
                is.close();
                bos = new ByteArrayOutputStream();
                gworkbook.write(bos);
            }
            bos.writeTo(out);
            return bos.toByteArray();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @GetMapping("/getQcDate")
    public ReturnData updateDate() {
        Json json = new Json();
        DZFDate date =gl_ic_invtoryqcserv.queryInventoryQcDate( SystemUtil.getLoginCorpId());
        if(date == null){
            date = SystemUtil.getLoginCorpVo().getBegindate();
        }
        json.setData(date.toString());
        json.setMsg("修改成功");
        json.setSuccess(true);
        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL,"存货期初，修改启用期间", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }
}