package com.dzf.zxkj.platform.controller.bdset;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.Kmschema;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 辅助核算
 */
@RestController
@RequestMapping("/bdset/gl_fzhsact")
@Slf4j
public class AuxiliaryAccountController {

    @Autowired
    private IAuxiliaryAccountService gl_fzhsserv;
    @Autowired
    private IParameterSetService parameterserv;
    @Autowired
    private IYntBoPubUtil yntBoPubUtil;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private ICorpService corpService;

    @GetMapping("/queryH")
    public ReturnData<Json> queryType(String pk_corp, String isfull) {
        Json json = new Json();
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
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
        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryFzhs")
    public ReturnData<Json> queryAllType() {
        Json json = new Json();
        AuxiliaryAccountHVO[] hvos = gl_fzhsserv.queryH(SystemUtil.getLoginCorpId());
        json.setRows(hvos);
        json.setMsg("查询成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/onSeal")
    public ReturnData<Json> seal(@RequestParam("id") String id) {
        Json json = new Json();
        gl_fzhsserv.onSeal(id);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/unSeal")
    public ReturnData<Json> unSeal(@RequestParam("id") String id) {
        Json json = new Json();
        gl_fzhsserv.unSeal(id);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryB")
    public ReturnData<Json> queryArchive(Integer page, Integer rows,
                                         @RequestParam("id") String hid,
                                         String kmid, String billtype, String type,
                                         String isfenye, String pk_corp) {
        Json json = new Json();
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        AuxiliaryAccountBVO[] bvos = new AuxiliaryAccountBVO[0];
        if (StringUtil.isEmpty(billtype)) {
            if ("Y".equals(isfenye)) {
                // 分页
                QueryPageVO pagevo = gl_fzhsserv.queryBodysBypage(hid, pk_corp, kmid, page, rows, type);
                json.setTotal(Long.valueOf(pagevo.getTotal()));
                json.setRows(pagevo.getPagevos());
                bvos = (AuxiliaryAccountBVO[]) pagevo.getPagevos();
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
        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryBParam")
    public ReturnData<Json> queryBParam(Integer page, Integer rows,
                                        @RequestParam("id") String hid, String param, String qrystyle,
                                        String isfenye, String type, String code, String name, String spec,
                                        String qchukukmid, String qkmclassify) {
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        if ("qryinv".equals(qrystyle)) {// 总账存货查询 存货档案
            if ("Y".equals(isfenye)) {
                QueryPageVO pagevo = gl_fzhsserv.queryBInvByconditionBypage(hid, code, name, spec, qchukukmid,
                        qkmclassify, pk_corp, page, rows);
                json.setTotal(Long.valueOf(pagevo.getTotal()));
                json.setRows(pagevo.getPagevos());
            }
            json.setMsg("查询成功");
            json.setSuccess(true);

        } else {
            if ("Y".equals(isfenye)) {
                QueryPageVO pagevo = gl_fzhsserv.queryBParamBypage(hid, param, pk_corp, page, rows, type);
                json.setTotal(Long.valueOf(pagevo.getTotal()));
                json.setRows(pagevo.getPagevos());
            }
            json.setMsg("查询成功");
            json.setSuccess(true);
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/saveH")
    public ReturnData<Json> saveType(@RequestBody AuxiliaryAccountHVO data) {
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        AuxiliaryAccountHVO hvo = data;
        if (!StringUtil.isEmptyWithTrim(hvo.getPk_auacount_h())) {
            AuxiliaryAccountHVO qvo = gl_fzhsserv.queryHByID(hvo.getPk_auacount_h());
            if (qvo != null) {
                hvo.setCode(qvo.getCode());
                hvo.setPk_corp(qvo.getPk_corp());
            }
        }
        if (hvo.getPk_corp() != null && !hvo.getPk_corp().equals(pk_corp)) {
            json.setMsg("无权操作！");
        } else {
            hvo.setPk_corp(pk_corp);
            hvo.setDr(0);
            hvo = gl_fzhsserv.saveH(hvo);
            json.setRows(hvo);
            json.setMsg("保存成功");
            json.setSuccess(true);
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/saveB")
    public ReturnData<Json> saveArchive(@RequestBody AuxiliaryAccountBVO bvo) {
        Json json = new Json();
        String login_corp = SystemUtil.getLoginCorpId();
        if (StringUtils.isBlank(bvo.getPk_corp())) {
            bvo.setPk_corp(login_corp);
        }
        if (!StringUtil.isEmptyWithTrim(bvo.getPk_auacount_b())) {
            AuxiliaryAccountBVO qvo = gl_fzhsserv.queryBByID(bvo.getPk_auacount_b(), bvo.getPk_corp());
            if (qvo != null && !qvo.getCode().trim().equals(bvo.getCode().trim())) {
                gl_fzhsserv.checkBRef(bvo, 0);
            }
        }
        bvo = gl_fzhsserv.saveB(bvo);
        json.setMsg("保存成功");
        json.setRows(bvo);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/batchSaveZy")
    public ReturnData<Json> batchSaveZy(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String ids = param.get("zyids");
        String xglx = param.get("xglx");
        String sgrqdate = param.get("employedate");
        String cdeptid = param.get("cdeptid1");
        String deptname = param.get("deptname");
        String[] idsArr = ids.split(",");
        List<AuxiliaryAccountBVO> list = new ArrayList<>();
        for (int i = 0; i < idsArr.length; i++) {
            if (StringUtil.isEmpty(idsArr[i])) {
                continue;
            }
            AuxiliaryAccountBVO auxiliaryAccountBVO = new AuxiliaryAccountBVO();
            auxiliaryAccountBVO.setPk_auacount_b(idsArr[i]);
            auxiliaryAccountBVO.setCdeptid(cdeptid);
            auxiliaryAccountBVO.setVdeptname(deptname);
            auxiliaryAccountBVO.setEmployedate(new DZFDate(sgrqdate));
            list.add(auxiliaryAccountBVO);
        }
        gl_fzhsserv.updateBatchAuxiliaryAccountByID(list.toArray(new AuxiliaryAccountBVO[list.size()]), new String[]{xglx});
        json.setMsg("保存成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/batchSaveB")
    public ReturnData<Json> batchSaveArchive(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String ids = param.get("ids");
        String[] idsArr = ids.split(",");
        if (idsArr.length < 1) {
            throw new BusinessException("您未选择要更新的行数据");
        }
        String kmclassify = param.get("kmclassifybatch");
        String chukukmid = param.get("chukukmidbatch");
        String unit = param.get("unitbatch");
        String spec = param.get("specbatch");
        if (StringUtil.isEmpty(kmclassify) && StringUtil.isEmpty(chukukmid) && StringUtil.isEmpty(unit)
                && StringUtil.isEmpty(spec)) {
            throw new BusinessException("没有可以修改的数据");
        }
        String fileds = "";
        fileds += kmclassify == null ? "" : "kmclassify,";
        fileds += chukukmid == null ? "" : "chukukmid,";
        fileds += unit == null ? "" : "unit,";
        fileds += spec == null ? "" : "spec,";

        String pk_corp = param.get("pk_corp");
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        AuxiliaryAccountBVO[] qbvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_INVENTORY, pk_corp, null);

        Map<String, AuxiliaryAccountBVO> map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(qbvos),
                new String[]{"pk_auacount_b"});
        List<AuxiliaryAccountBVO> bvolist = new ArrayList<AuxiliaryAccountBVO>();
        for (int i = 0; i < idsArr.length; i++) {
            AuxiliaryAccountBVO aabvo = map.get(idsArr[i]);
            if (aabvo == null) {
                continue;
            }
            aabvo.setPk_auacount_b(idsArr[i]);
            if (!StringUtil.isEmpty(kmclassify) && StringUtil.isEmpty(aabvo.getKmclassify())) {// 只更新【存货类别】为空的数据
                aabvo.setKmclassify(kmclassify);
            }
            if (!StringUtil.isEmpty(chukukmid)) {
                aabvo.setChukukmid(chukukmid);
            }
            if (!StringUtil.isEmpty(unit)) {
                aabvo.setUnit(unit);
            }

            if (!StringUtil.isEmpty(spec)) {
                aabvo.setSpec(spec);
            }
            // aabvo.setPrimaryKey(idsArr[i]);
            bvolist.add(aabvo);
        }
        String[] modifyFiled = fileds.split(",");
        int results = gl_fzhsserv.updateBatchAuxiliaryAccountByIDS(bvolist, modifyFiled);
        if (results > 0) {
            json.setMsg("保存成功");
            json.setRows(results);
            json.setSuccess(true);
        } else {
            json.setMsg("保存失败");
            json.setRows(results);
            json.setSuccess(false);
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/deleteH")
    public ReturnData<Json> deleteH(@RequestBody HashMap<String, String> param) {
        Json json = new Json();
        String hid = param.get("hid");
        String pk_corp = SystemUtil.getLoginCorpId();
        AuxiliaryAccountHVO qvo = gl_fzhsserv.queryHByID(hid);
        if (qvo == null) {
            json.setMsg("要删除数据不存在！");
            throw new BusinessException("要删除数据不存在");
        }
        if (!pk_corp.equals(qvo.getPk_corp())) {
            throw new BusinessException("无权操作");
        }
        gl_fzhsserv.delete(qvo);
        json.setMsg("删除成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/deleteB")
    public ReturnData<Json> deleteB(@RequestBody AuxiliaryAccountBVO[] bvos) {
        Json json = new Json();
        json.setSuccess(false);
        String[] msg = gl_fzhsserv.delete(bvos);
        json.setMsg(msg[0]);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/importB")
    public ReturnData<Json> importArchive(@RequestParam("impfile") MultipartFile file,
                                          @RequestParam("hid") String hid) {
        Json json = new Json();
        if (file == null) {
            throw new BusinessException("请选择要导入的文件！");
        }
        String fileName = file.getOriginalFilename();
        String fileType = null;
        if (!StringUtils.isBlank(fileName)) {
            fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        }

        String pk_corp = SystemUtil.getLoginCorpId();
        try {
            Map<String, String> result = gl_fzhsserv.saveBImp(file.getInputStream(),
                    hid, pk_corp, fileType);
            json.setMsg(result.get("msg"));
            json.setSuccess(true);
        } catch (IOException e) {
            json.setMsg("导入失败");
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/impUpdateB")
    public ReturnData<Json> impArchiveForUpdate(@RequestParam("impfile") MultipartFile file) {
        Json json = new Json();
        json.setSuccess(false);
        if (file == null) {
            throw new BusinessException("请选择要导入的文件！");
        }
        String fileName = file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);

        String pk_corp = SystemUtil.getLoginCorpId();
        try {
            Map<String, String> result = gl_fzhsserv.updateBImp(file.getInputStream(),
                    pk_corp, fileType, getExpFieldMap());
            json.setMsg(result.get("msg"));
            json.setSuccess(true);
        } catch (IOException e) {
            json.setMsg("导入失败");
        }
        return ReturnData.ok().data(json);
    }

    // 目前只做了存货
    @PostMapping("/exportData")
    public HttpEntity<byte[]> exportData(@RequestParam Map<String, String> param) {
        HttpEntity<byte[]> response = null;
        JSONArray array = JSON.parseArray(param.get("daterows"));
        String fileName = "更新存货档案.xls";
        Map<Integer, String> fieldColumn = getExpFieldMap();
        String pk_corp = SystemUtil.getLoginCorpId();
        String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
        int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
        byte[] bytes = exportExcel(fieldColumn, array, "fztemplatezzhsCHup.xls", price);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        header.setContentLength(bytes.length);
        response = new HttpEntity<>(bytes, header);
        return response;
    }

    private Map<Integer, String> getExpFieldMap() {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(0, "code");// 编码
        map.put(1, "name");// 名称
        map.put(2, "spec");// 规格(型号)
        map.put(3, "jsprice");// 结算单价
        map.put(4, "unit");// 计量单位

        return map;
    }

    private byte[] exportExcel(Map<Integer, String> fieldColumn, JSONArray array, String excelName,
                               int price) {
        byte[] bytes = null;
        if (fieldColumn == null || fieldColumn.size() == 0 || StringUtil.isEmpty(excelName)) {
            return null;
        }
        Resource exportTemplate = new ClassPathResource(DZFConstant.DZF_KJ_EXCEL_TEMPLET + excelName);
        InputStream is = null;
        HSSFWorkbook workbook = null;
        ByteArrayOutputStream bos = null;

        DZFDouble doublevalue = null;
        HSSFRichTextString richString = null;
        try {
            is = exportTemplate.getInputStream();
            workbook = new HSSFWorkbook(is);
            HSSFSheet sheet = workbook.getSheetAt(0);

            HSSFRow row = sheet.getRow(1);
            int minindex = row.getFirstCellNum(); // 列起
            int maxindex = row.getLastCellNum(); // 列止

            HSSFRow rowTo = null;

            HSSFCell c1 = null;
            HSSFCell c2 = null;
            Map<String, Object> map = null;
            // YntCpaccountVO accvo;

            String key = null;

            Object obj = null;
            int len = array == null ? 0 : array.size();
            // 数据行
            for (int i = 0; i < len; i++) {
                rowTo = sheet.createRow(1 + i);
                rowTo.setHeight(row.getHeight());
                for (int colindex = minindex; colindex < maxindex; colindex++) {
                    c1 = row.getCell(colindex);
                    c2 = rowTo.createCell(colindex);
                    c2.setCellStyle(c1.getCellStyle());
                    if (fieldColumn.containsKey(colindex)) {
                        map = (Map<String, Object>) array.get(i);
                        key = fieldColumn.get(colindex);
                        obj = map.get(key);
                        if (obj != null) {
                            if ("jsprice".equals(key)) {
                                doublevalue = new DZFDouble(obj.toString());
                                if (SafeCompute.add(doublevalue, DZFDouble.ZERO_DBL).doubleValue() == 0)
                                    continue;
                                doublevalue = doublevalue.setScale(price, DZFDouble.ROUND_HALF_UP);
                                c2.setCellValue(doublevalue.toString());
                            } else {
                                richString = new HSSFRichTextString(obj.toString());
                                c2.setCellValue(richString);
                            }
                        } else {
                        }
                        continue;
                    }
                }
            }

            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            bytes = bos.toByteArray();
            bos.close();
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("导出失败", e);
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
        return bytes;
    }

    @PostMapping("/checkRepeat")
    public ReturnData<Json> checkRepeat(@RequestBody AuxiliaryAccountBVO bvo) {
        Json json = new Json();
        bvo.setPk_corp(SystemUtil.getLoginCorpId());
        boolean isRepeat = false;
        if (!StringUtil.isEmpty(bvo.getPk_auacount_h())) {
            isRepeat = gl_fzhsserv.checkRepeat(bvo);
        }
        json.setMsg("" + isRepeat);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    /**
     * 查询存货税务信息
     */
    @GetMapping("/queryInvTaxinfo")
    public ReturnData<Json> queryInvTaxinfo(@RequestParam("invname") String invname,
                                            String pk_corp) {
        Json json = new Json();
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        AuxiliaryAccountBVO[] bvos = gl_fzhsserv.queryInvtaxInfo(invname, pk_corp);
        json.setRows(bvos);
        json.setMsg("查询成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    // 查询单据号
    @GetMapping("/queryDjCode")
    public ReturnData<Json> queryDjCode(@RequestParam("id") String pk_auacount_h,
                                        String pk_corp) {
        Json json = new Json();
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        String invcode = yntBoPubUtil.getFZHsCode(pk_corp, pk_auacount_h);
        json.setData(invcode);
        json.setSuccess(true);
        json.setMsg("获取单据号成功");
        return ReturnData.ok().data(json);
    }

    @PostMapping("/mergeData")
    public ReturnData<Json> mergeData(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String spid = param.get("spid");
        if (StringUtils.isBlank(spid)) {
            throw new BusinessException("合并的存货不允许为空!");
        }
        String pk_corp = param.get("pk_corp");
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }

        String body = param.get("body"); // 子表
        body = body.replace("}{", "},{");
//        body = "[" + body + "]";

        AuxiliaryAccountBVO[] bodyvos = JsonUtils.deserialize(body, AuxiliaryAccountBVO[].class);
        if (bodyvos == null || bodyvos.length == 0) {
            throw new BusinessException("被合并的存货不允许为空!");
        }

        AuxiliaryAccountBVO vo = gl_fzhsserv.saveMergeData(pk_corp, spid, bodyvos);
        json.setMsg("存货合并成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    // 出库科目
    @GetMapping("/getChukuKm")
    public ReturnData<Json> getChukuKm() {
        Json json = new Json();
        YntCpaccountVO[] accounts =accountService.queryByPk(SystemUtil.getLoginCorpId());
        CorpVO corpVo = corpService.queryByPk(SystemUtil.getLoginCorpId());
        List<String> chukukm = Kmschema.getChukuKm(corpVo.getCorptype(), accounts);
        json.setRows(chukukm.toArray(new String[chukukm.size()]));
        json.setMsg("获取出库科目成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    // 分类科目
    @GetMapping("/getKmclassify")
    public ReturnData<Json> getKmclassify() {
        Json json = new Json();
        YntCpaccountVO[] accounts =accountService.queryByPk(SystemUtil.getLoginCorpId());
        CorpVO corpVo = corpService.queryByPk(SystemUtil.getLoginCorpId());
        //得到分类科目
        List<String> classify = Kmschema.getKmclassify(corpVo.getCorptype(), accounts);
        json.setRows(classify.toArray(new String[classify.size()]));
        json.setMsg("获取分类科目成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

}
