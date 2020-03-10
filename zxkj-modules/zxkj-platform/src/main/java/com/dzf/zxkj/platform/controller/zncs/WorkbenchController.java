package com.dzf.zxkj.platform.controller.zncs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dzf.cloud.redis.lock.RedissonDistributedLock;
import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.base.IOperatorLogService;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.IcDetailVO;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.OcrImageLibraryVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceDetailVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pjgl.VatGoosInventoryRelationVO;
import com.dzf.zxkj.platform.model.pjgl.VatInvoiceSetVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.*;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.glic.IKcCbb;
import com.dzf.zxkj.platform.service.glic.impl.CheckInventorySet;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.zncs.*;
import com.dzf.zxkj.platform.util.ReportUtil;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@RestController
@RequestMapping("/zncs/workbench")
public class WorkbenchController extends BaseController {


    private Logger log = Logger.getLogger(this.getClass());

    @Autowired
    private IBillcategory iBillcategory;
    @Autowired
    private IParaSet iParaSet;
    @Autowired
    private IBlackList iBlackList;
    @Autowired
    IInterfaceBill iInterfaceBill;
    @Autowired
    private IEditDirectory iEditDirectory;
    @Autowired
    private IDirectory iDirectory;
    @Autowired
    private IVoucherTemplet iVoucherTemplet;
    @Autowired
    private IVatInvoiceService vatinvoiceserv;
    @Autowired
    private IZncsVoucher iZncsVoucher;
    @Autowired
    private IAuxiliaryAccountService gl_fzhsserv;
    @Autowired
    private IBillCountService billCountService;
    @Autowired
    private IQmgzService qmgzService;
    @Autowired
    private IKcCbb ic_rep_cbbserv;
    @Autowired
    private IParameterSetService parameterserv;
    @Autowired
    private IVoucherService gl_tzpzserv;
    @Autowired
    private IOperatorLogService sys_ope_log2;
    @Autowired
    private IYntBoPubUtil yntBoPubUtil;

    @Autowired
    private IQmclService gl_qmclserv;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private ICorpService corpService;
    @Autowired
    private RedissonDistributedLock redissonDistributedLock;
    /**
     * 查询分类+票据树
     */
    @RequestMapping("/queryCategory")
    public ReturnData<Grid> queryCategory(@RequestBody Map<String,String> param) {
        Grid grid = new Grid();
        try {
            checkPeriod(param.get("period"), false);
            BillcategoryQueryVO paramVO=buildParamVO(param);
            List<BillCategoryVO> list = iBillcategory.queryCategoryTree(paramVO);
            grid.setSuccess(true);
            grid.setTotal((long)caclTotal(list));
            grid.setMsg("查询成功!");
            grid.setRows(list);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败!");
        }
        return ReturnData.ok().data(grid);
    }
    /**
     * 汇总总数
     * @return
     */
    private Integer caclTotal(List<BillCategoryVO> list){
        if(list==null||list.size()==0){
            return 0;
        }
        int total=0;
        for(int i=0;i<list.size();i++){
            BillCategoryVO vo=list.get(i);
            if(vo.getItype()==0){//分类
                total+=vo.getBillcount();
            }else{
                total+=1;
            }
        }
        return total;
    }
    /**
     * 树参照
     */
    @RequestMapping("/queryTree")
    public ReturnData<Grid> queryTree(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        try {
            String period = param.get("period");
            List<CategoryTreeVO> list = iBillcategory.queryCategoryTree(SystemUtil.getLoginCorpId(),period);
            if(list!=null&& list.size()>0){
                grid.setRows(list);
                grid.setSuccess(true);
            }else{
                grid.setTotal(0L);
                grid.setSuccess(true);
                grid.setMsg("查询失败!");
                grid.setRows(null);
            }
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败!");
        }
        return ReturnData.ok().data(grid);
    }
    /**
     * 参数查询
     */
    @RequestMapping("/quertParaSet")
    public ReturnData<Grid> quertParaSet(){
        Grid grid = new Grid();
        try {
            String pk_corp=SystemUtil.getLoginCorpId();
            List<ParaSetVO> list = iParaSet.queryParaSet(pk_corp);
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
            grid.setRows(list);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败!");
        }
       return ReturnData.ok().data(grid);
    }
    /**
     * 参数保存
     */
    @RequestMapping("/saveParaSet")
    public ReturnData<Json> saveParaSet(@RequestBody Map<String,String> param){
        Json json = new Json();
        String pType = param.get("pType");
        String pValue = param.get("pValue");
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            iParaSet.saveParaSet(pk_corp,pType, pValue);
            json.setSuccess(true);
            json.setMsg("设置成功");
        } catch(Exception e) {
            printErrorLog(json, e, "设置失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-修改参数设置："+getParaLog(pType, pValue), ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private String getParaLog(String pType,String pValue){
        String returnName="";
        if(pType.equals("srfl")){
            returnName="按往来单位分类：收入："+(Integer.parseInt(pValue)==0?"否":"是");
        }else if(pType.equals("cgfz")){
            returnName="按往来单位分类：库存采购："+(DZFBoolean.TRUE.equals(new DZFBoolean(pValue))?"是":"否");
        }else if(pType.equals("cbfz")){
            returnName="按往来单位分类：成本："+(DZFBoolean.TRUE.equals(new DZFBoolean(pValue))?"是":"否");
        }else if(pType.equals("yhhb")){
            returnName="按往来单位分类：银行票据："+(DZFBoolean.TRUE.equals(new DZFBoolean(pValue))?"是":"否");
        }else if(pType.equals("isyh")){
            returnName="银行票据按账户分类 ："+(DZFBoolean.TRUE.equals(new DZFBoolean(pValue))?"是":"否");
        }else if(pType.equals("isrz")){
            returnName="进项票据标为已认证 ："+(DZFBoolean.TRUE.equals(new DZFBoolean(pValue))?"是":"否");
        }else if(pType.equals("ncpsl")){
            returnName="农产品收购发票抵扣税率 ："+(pValue);
        }else if(pType.equals("iszpb")){
            returnName="凭证区分专普票 ："+(DZFBoolean.TRUE.equals(new DZFBoolean(pValue))?"是":"否");
        }else if(pType.equals("pzrq")){
            returnName="凭证日期 ："+(Integer.parseInt(pValue)==0?"票据实际日期":"当前账期最后一天");
        }else if(pType.equals("iscwvhr")){
            returnName="智能做账包含问题票据及未识别票据："+(DZFBoolean.TRUE.equals(new DZFBoolean(pValue))?"是":"否");
        }else if(pType.equals("ishbfl")){
            returnName="凭证合并分录："+(DZFBoolean.TRUE.equals(new DZFBoolean(pValue))?"是":"否");
        }else if(pType.equals("flpx")){
            returnName="分录排序："+(Integer.parseInt(pValue)==0?"不排序":"先借后贷");
        }
        return returnName;
    }
    /**
     * 表头移动到
     */
    @RequestMapping("/saveNewCategory")
    public ReturnData<Json> saveNewCategory(@RequestBody Map<String,String> param){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            String bills = param.get("ids");
            String treeid= param.get("treeid");
            String period= param.get("period");
            checkPeriod(period, true);
            iBillcategory.saveNewCategory(bills.split(","), treeid, pk_corp, period);
            json.setSuccess(true);
            json.setMsg("设置成功");
        } catch(Exception e) {
            printErrorLog(json, e, "设置失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-移动票据", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }
    private void checkPeriod(String period, boolean checkEmpty)
    {
        if (StringUtil.isEmptyWithTrim(period))
        {
            if (checkEmpty)
            {
                throw new BusinessException("期间不能为空!");
            }
        }
        CorpVO corpvo = SystemUtil.getLoginCorpVo();
        DZFDate begindate = corpvo.getBegindate();
        try {
            String newperiod = period + "-01";
            DZFDate testdate = new DZFDate(newperiod);
            if (testdate.compareTo(begindate) < 0)
            {
                throw new BusinessException("期间 '" + period + "' 在建账日期 '" + begindate.toString() + "' 之前！");
            }
        }
        catch (Exception ex)
        {
            throw new BusinessException("期间格式 '" + period + "' 非法");
        }
    }
    private BillcategoryQueryVO buildParamVO(Map<String,String> param){
        BillcategoryQueryVO paramVO=new BillcategoryQueryVO();
        paramVO.setOldperiod(param.get("oldperiod"));
        paramVO.setPeriod(param.get("period"));
        paramVO.setPk_category(param.get("id"));
        paramVO.setBillstate(param.get("billstate")==null?null:Integer.parseInt(param.get("billstate")));
        paramVO.setCategorycode(param.get("code"));
        paramVO.setPk_corp(SystemUtil.getLoginCorpId());
        paramVO.setPk_parentcategory(param.get("pk_parent"));
        paramVO.setBilltype(param.get("billtype"));//票据类型
        paramVO.setInvoicetype(param.get("invoicetype"));//单据类型
        paramVO.setRemark(param.get("remark"));
        paramVO.setBilltitle(param.get("billtitle"));//票据名称
        paramVO.setVpurchname(param.get("vpurchname"));//付款方
        paramVO.setVsalename(param.get("vsalename"));//收款方
        paramVO.setBdate(param.get("bdate"));//开票日期开始
        paramVO.setEdate(param.get("edate"));//开票日期结束
        paramVO.setBntotaltax(param.get("bntotaltax"));//金额开始
        paramVO.setTruthindent(param.get("truthindent"));//真伪
        paramVO.setPk_bankcode(param.get("pk_bankcode"));
        return paramVO;
    }
    /**
     * 查询黑名单
     */
    @RequestMapping("/queryBlackList")
    public ReturnData<Grid> queryBlackList(){
        Grid grid = new Grid();
        try {
            String pk_corp=SystemUtil.getLoginCorpId();
            List<BlackListVO> list = iBlackList.queryBlackListVOs(pk_corp);
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
            grid.setRows(list);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败!");
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 保存黑名单关键字
     */
    @RequestMapping("/saveBlackList")
    public ReturnData<Json> saveBlackList(@RequestBody Map<String,String> param){
        Json json = new Json();
        String blackListnames = param.get("names");
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{

            if (StringUtil.isEmpty(blackListnames))
            {
                throw new BusinessException("黑名单内容为空");
            }
            else
            {
                if (blackListnames.contains("未识别票据"))
                {
                    throw new BusinessException("黑名单内容非法");
                }
            }
            List<BlackListVO> saveList=iBlackList.saveBlackListVO(pk_corp, blackListnames);
            json.setSuccess(true);
            json.setRows(saveList);
            json.setMsg("保存成功");
        } catch(Exception e) {
            printErrorLog(json,  e, "保存失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-保存黑名单:"+blackListnames, ISysConstants.SYS_2);
        return  ReturnData.ok().data(json);
    }

    /**
     * 删除黑名单关键字
     */
    @RequestMapping("/deleteBlackList")
    public ReturnData<Json> deleteBlackList(@RequestBody Map<String,String> param){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            String pk_blacklist = param.get("id");
            iBlackList.deleteBlackListVO(pk_corp, pk_blacklist);
            json.setSuccess(true);
            json.setMsg("删除成功");
        } catch(Exception e) {
            printErrorLog(json, e, "删除失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-删除黑名单", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    /**
     * 获取辅助核算项
     */
    @RequestMapping("/queryFzhsItem")
    public ReturnData<Grid> queryFzhsItem(){
        Grid grid = new Grid();
        try {
            String pk_corp=SystemUtil.getLoginCorpId();
            List<AuxiliaryAccountHVO> list=iEditDirectory.queryAuxiliaryAccountHVOs(pk_corp);
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
            grid.setRows(list);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败!");
        }
        return ReturnData.ok().data(grid);
    }

    @RequestMapping("/queryFzhsValue")
    public ReturnData<Grid> queryFzhsValue(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        try {
            String pk_head = param.get("id");
            String pk_corp=SystemUtil.getLoginCorpId();
            List<AuxiliaryAccountBVO> list=iEditDirectory.queryAuxiliaryAccountBVOs(pk_corp,pk_head);
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
            grid.setRows(list);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败!");
        }
        return ReturnData.ok().data(grid);
    }
    /**
     * 右键编辑目录保存
     */
    @RequestMapping("/saveEditDirectory")
    public ReturnData<Json> saveEditDirectory(@RequestBody Map<String,String> param){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            String head = param.get("head");
            String body = param.get("body");
            CategorysetVO headvo = JsonUtils.deserialize(head,CategorysetVO.class);
            CategorysetBVO[] bodyvos = JsonUtils.deserialize(body,CategorysetBVO[].class);
            iEditDirectory.saveAuxiliaryAccountVO(pk_corp, headvo, bodyvos);
            json.setSuccess(true);
            json.setMsg("设置成功");
        } catch(Exception e) {
            printErrorLog(json, e, "设置失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-编辑分类", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    /**
     * 编辑目录查询
     */
    @RequestMapping("queryEditDirection")
    public ReturnData<Json> queryEditDirection(@RequestBody Map<String,String> param){
        Json json = new Json();
        try{
            String pk_tree = param.get("id");
            String pk_corp=SystemUtil.getLoginCorpId();
            CategorysetVO head = iEditDirectory.queryCategorysetVO(pk_tree, pk_corp);
            json.setSuccess(true);
            json.setMsg("查询成功");
            json.setData(head);
        } catch(Exception e) {
            printErrorLog(json, e, "查询失败");
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 查询票据信息
     */
    @RequestMapping("/queryBillInfo")
    public ReturnData<Json> queryBillInfo(@RequestBody Map<String,String> param){
        Json json = new Json();
        try{
            String billid = param.get("id");
            String pk_corp=SystemUtil.getLoginCorpId();
            BillInfoVO billinfo = iInterfaceBill.queryBillInfo(billid);
            json.setSuccess(true);
            json.setMsg("查询成功");
            json.setData(billinfo);
        } catch(Exception e) {
            json.setStatus(-100);
            printErrorLog(json, e, "查询失败");
        }
        return ReturnData.ok().data(json);
    }
    /**
     * 查询票据信息s
     */
    @RequestMapping("/queryBillInfos")
    public ReturnData<Json> queryBillInfos(@RequestBody Map<String,String> param){
        Json json = new Json();
        try{
            String billids = param.get("ids");
            String period = param.get("period");
            String pk_corp=SystemUtil.getLoginCorpId();
            List<BillInfoVO> billinfoList = iInterfaceBill.queryBillInfos(billids, period, pk_corp);
            json.setSuccess(true);
            json.setMsg("查询成功");
            json.setData(billinfoList);
        } catch(Exception e) {
            json.setStatus(-100);
            printErrorLog(json, e, "查询失败");
        }
        return ReturnData.ok().data(json);
    }
    /**
     * 作废票据
     */

    @RequestMapping("/invalidBill")
    public ReturnData<Json>  invalidBill(@RequestBody Map<String,String> param){
        //iInterfaceBill
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            String ids = param.get("ids");
            String[] idArray = ids.split(",");
            iInterfaceBill.updateInvalidBill(idArray,pk_corp);
            json.setSuccess(true);
            json.setMsg("作废成功");
        } catch(Exception e) {
            printErrorLog(json,  e, "作废失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-作废票据", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);

    }
    /**
     * String period,String billstate,String pk_category,String categorycode,
     * String pk_parent,String oldperiod,String pk_bankcode,String billtype,
     * String invoicetype,String remark,String billtitle,String vpurchname,
     * String vsalename,String bdate,String edate,String bntotaltax,String entotaltax,String truthindent
     * 批量按照分类作废票据
     */
    @RequestMapping("/invalidBatchBill")
    public ReturnData<Json> invalidBatchBill(@RequestBody Map<String,String> param){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            BillcategoryQueryVO paramVO=buildParamVO(param);
            iInterfaceBill.updateInvalidBatchBill(paramVO);
            json.setSuccess(true);
            json.setMsg("作废成功!");
        } catch (Exception e) {
            json.setStatus(-100);
            printErrorLog(json,  e, "作废失败!");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-批量作废票据", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);

    }

    /**
     * 票据重传
     * String ids,String period,String billstate,String pk_category,String categorycode,
     * String pk_parent,String oldperiod,String pk_bankcode,String billtype,
     * String invoicetype,String remark,String billtitle,String vpurchname,
     * String vsalename,String bdate,String edate,String bntotaltax,String entotaltax,
     * String truthindent
     */
    @RequestMapping("/updateRetransBill")
    public ReturnData<Json> updateRetransBill(@RequestBody Map<String,String> param){
        Json json = new Json();
        try {
            String ids=param.get("ids");
            String[] idArray = StringUtil.isEmpty(ids)?null:ids.split(",");
            BillcategoryQueryVO paramVO=buildParamVO(param);
            iInterfaceBill.updateRetransBill(idArray, paramVO);
            json.setSuccess(true);
            json.setMsg("重传成功!");
        } catch (Exception e) {
            json.setStatus(-100);
            printErrorLog(json, e, "重传失败!");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-票据重传", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);

    }
    /**
     * 导出下载票据
     */
    @RequestMapping("/exportBill")
    public ReturnData<Json>  exportBill(HttpServletResponse response,String ids,String period,String categoryid){
        //iInterfaceBill
        Json json = new Json();
        try{
            String pkcorp=SystemUtil.getLoginCorpId();
            String[] idArray = ids==null?null:ids.split(",");
            CorpVO corpVO2 = SystemUtil.getLoginCorpVo();//图片浏览查询框中公司pk
            OcrImageLibraryVO[]imageVos = iInterfaceBill.queryImages(idArray,pkcorp,period,categoryid);
            List<File> list =new ArrayList<File>();
            for (int i = 0;imageVos!=null&& i < imageVos.length; i++) {
                String type = null;
                String imgPathName = imageVos[i].getImgpath();
                if(imgPathName.startsWith("ImageOcr")){
                    type="ImageOcr";
                }else{
//					int index = imgPathName.lastIndexOf("/") == -1 ?  imgPathName.lastIndexOf("\\") : imgPathName.lastIndexOf("/");
//					imgPathName = imgPathName.substring(index + 1);
                    type="vchImg";
                }
                File file =  ImageViewController.getImageFolder(type, corpVO2, imgPathName, imageVos[i].getImgname());

                if(file.exists()) list.add(file);
            }
            if(list.size()>00){
                exportFileToZip(response, list.toArray(new File[0]), new DZFDate().toString()+"-"+new Random().nextInt(9999));
                json.setSuccess(true);
                json.setMsg("下载成功");
            }else{
                json.setSuccess(false);
                json.setMsg("暂无可下载的票据!");
                json.setStatus(-100);
            }

        } catch(Exception e) {
            json.setStatus(-100);
            printErrorLog(json, e, "下载失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-下载票据", ISysConstants.SYS_2);

        return ReturnData.ok().data(json);

    }
    /**
     * 跨期
     */
    @RequestMapping("/changePeorid_long")
    public ReturnData<Json>  changePeorid_long(@RequestBody Map<String,String> param){
        //iInterfaceBill
        Json json = new Json();
        String period = param.get("period");
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            String ids = param.get("ids");
            checkPeriod(period, true);		//检查期间的合法性
            String[] idArray = ids.split(",");

            CorpVO currcorp = SystemUtil.getLoginCorpVo();
            DZFDate begdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(currcorp.getBegindate())) ;
            if(begdate.after(new DZFDate(period+"-01"))){
                throw new BusinessException("期间不能在建账期间("+DateUtils.getPeriod(begdate)+")前!");
            }
            boolean isgz = qmgzService.isGz(SystemUtil.getLoginCorpId(), new DZFDate(period+"-01").toString());
            if (isgz) {// 是否关账
                throw new BusinessException("所选入账期间"+period+"已关账，请检查！");
            }
            iInterfaceBill.updateChangeBillPeroid(idArray, period);
            json.setSuccess(true);
            json.setMsg("跨期成功");
        } catch(Exception e) {
            json.setStatus(-100);
            printErrorLog(json,  e, "跨期失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-票据跨期:"+"跨期至"+period, ISysConstants.SYS_2);

        return ReturnData.ok().data(json);
    }

    /**
     * 查询作废票据
     */
    @RequestMapping("/queryInvalidBillInfo")
    public ReturnData<Grid> queryInvalidBillInfo(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        try {
            String period = param.get("period");
//			String pkcorp=StringUtil.isEmpty(getRequest().getParameter("corpid"))?
//					getLoginCorpInfo().getPk_corp():getRequest().getParameter("corpid");
            String pkcorp=SystemUtil.getLoginCorpId();

            List<BillInfoVO> list = iInterfaceBill.queryInvalidBill(pkcorp, period);
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
            grid.setRows(list);
            grid.setTotal((long)list.size());
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败!");
        }
        return ReturnData.ok().data(grid);
    }

    private boolean exportFileToZip(HttpServletResponse response, File []files, String
            zipFileName) {
        if (files == null&& files.length<=0) {
            return false;
        }
        FileInputStream fis = null;
        OutputStream out = null;
        ZipOutputStream zos = null;
        try {
            // 清空输出流
            response.resetBuffer();
            // 设置reponse返回数据类型，文件名，以及处理文件名乱码
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Connection", "close"); // 表示不能用浏览器直接打开
            response.setHeader("Accept-Ranges", "bytes");// 告诉客户端允许断点续传多线程连接下载
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String((zipFileName + ".zip").getBytes("GB2312"), "ISO8859-1"));
            response.setCharacterEncoding("UTF-8");
            // 取得输出流
            out = response.getOutputStream();
            // 压缩输出流
            zos = new ZipOutputStream(out);
            byte[] bufs = new byte[1024 * 10];
            BufferedInputStream bis =null;
            for (int i = 0; i < files.length; i++) {
                try {
                    // 创建ZIP实体，并添加进压缩包
                    ZipEntry zipEntry = new ZipEntry(files[i].getName());
                    zos.putNextEntry(zipEntry);
                    // 读取待压缩的文件并写进压缩包里
                    fis = new FileInputStream(files[i]);
                    bis = new BufferedInputStream(fis, 1024 * 10);
                    int read = 0;
                    while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                        zos.write(bufs, 0, read);
                    }
                    zos.flush();
                } catch (Exception e) {
                    throw new BusinessException(e.getMessage());
                } finally {
                    // 关闭流
                    try {
                        if (null != bis)
                            bis.close();
                    } catch (IOException e) {
                        throw new BusinessException(e.getMessage());
                    }
                    try {
                        if (null != fis)
                            fis.close();
                    } catch (IOException e) {
                        throw new BusinessException(e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            return false;
        } finally {
            try {
                // 关闭顺序必须是zos在前面，否则在用wrar解压时报错：“不可预料的压缩文件末端是什么原因”
                if (zos != null) {
                    zos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {

            }
        }
        return true;
    }

    /**
     * 创建目录
     */
    @RequestMapping("/createDirectory")
    public ReturnData<Json> createDirectory(@RequestBody Map<String,String> param ){
        Json json = new Json();
        String dirName = param.get("name");
        String period = param.get("period");
        String pk_parent = param.get("pk_parent");
        checkPeriod(period, true);		//检查期间合法性
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            iDirectory.saveNewDirectory(dirName, pk_parent, pk_corp, period,SystemUtil.getLoginUserId());
            json.setSuccess(true);
            json.setMsg("创建成功");
        } catch(Exception e) {
            printErrorLog(json, e, "创建失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-新建分类:"+dirName+",期间："+period, ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    /**
     * 删除目录
     */
    @RequestMapping("/deleteDirectory")
    public ReturnData<Json> deleteDirectory(@RequestBody Map<String,String> param){
        Json json = new Json();
        String period=param.get("period");
        String id = param.get("id");
        String pk_parent = param.get("pid");
        checkPeriod(period, true);		//检查期间合法性
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            iDirectory.deleteDirectory(id, pk_parent, pk_corp, period);
            json.setSuccess(true);
            json.setMsg("删除成功");
        } catch(Exception e) {
            printErrorLog(json, e, "删除失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-删除分类:"+"期间："+period, ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {

            @Override
            public void serialize(Object value, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
                jg.writeString("");
            }
        });
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        objectMapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, false);
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        return objectMapper;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<VouchertempletHVO> turnVouchertempletHVO(ArrayList objMap){
        List<VouchertempletHVO> list=new ArrayList<VouchertempletHVO>();
        for(int i=0;i<objMap.size();i++){
            LinkedHashMap map=(LinkedHashMap)objMap.get(i);
            LinkedHashMap head=(LinkedHashMap)map.get("head");
            ArrayList bodys=(ArrayList)map.get("body");
            VouchertempletHVO headVO=buildVouchertempletHVO(head);
            VouchertempletBVO[] bodyVOs=buildVouchertempletBVOs(bodys);
            if(bodyVOs.length<2){
                throw new BusinessException("自定义模板分录最少2行。");
            }
            headVO.setChildren(bodyVOs);
            list.add(headVO);
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    private VouchertempletHVO buildVouchertempletHVO(LinkedHashMap head){
        VouchertempletHVO headVO=new VouchertempletHVO();
//		if(head.containsKey("id")){
//			headVO.setPk_vouchertemplet_h((String)head.get("id"));
//		}
//		if(head.containsKey("corpid")){
//			headVO.setPk_corp((String)head.get("corpid"));
//		}
//		if(head.containsKey("baseid")){
//			headVO.setPk_basecategory((String)head.get("baseid"));
//		}
        if(head.containsKey("categoryid")){
            headVO.setPk_category((String)head.get("categoryid"));
        }
        if(head.containsKey("order")){
            headVO.setOrderno(Integer.parseInt(head.get("order").toString()));
        }
        if(head.containsKey("words")){
            headVO.setKeywords((String)head.get("words"));
        }
//		if(head.containsKey("name")){
//			headVO.setTempletname((String)head.get("name"));
//		}
        return headVO;
    }

    @SuppressWarnings("rawtypes")
    private VouchertempletBVO[] buildVouchertempletBVOs(ArrayList bodys){
        List<VouchertempletBVO> list=new ArrayList<VouchertempletBVO>();
        for(int i=0;i<bodys.size();i++){
            VouchertempletBVO bodyVO=new VouchertempletBVO();
            LinkedHashMap body=(LinkedHashMap)bodys.get(i);
//			if(body.containsKey("id")){
//				bodyVO.setPk_vouchertemplet_b((String)body.get("id"));
//			}
//			if(body.containsKey("pid")){
//				bodyVO.setPk_vouchertemplet_h((String)body.get("pid"));
//			}
//			if(body.containsKey("corpid")){
//				bodyVO.setPk_corp((String)body.get("corpid"));
//			}
            if(body.containsKey("zy")){
                bodyVO.setZy((String)body.get("zy"));
            }
            if(body.containsKey("accid")){
                bodyVO.setPk_accsubj((String)body.get("accid"));
            }
            if(body.containsKey("debit")&&!StringUtil.isEmpty(body.get("debit").toString())){
                bodyVO.setDebitmny(Integer.parseInt(body.get("debit").toString()));
            }
            if(body.containsKey("credit")&&!StringUtil.isEmpty(body.get("credit").toString())){
                bodyVO.setCreditmny(Integer.parseInt(body.get("credit").toString()));
            }
            list.add(bodyVO);
        }
        return list.toArray(new VouchertempletBVO[0]);
    }
    /**
     * 自定义凭证模板保存
     */
    @RequestMapping("/saveVoucherTemplet")
    public ReturnData<Json> saveVoucherTemplet(@RequestBody Map<String,String> param){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            String templet = param.get("templet");
            ArrayList objMap = getObjectMapper().readValue(templet, ArrayList.class);
            List<VouchertempletHVO> templetList=turnVouchertempletHVO(objMap);
            iVoucherTemplet.saveVoucherTempletList(templetList, pk_corp);
            json.setSuccess(true);
            json.setMsg("保存成功");
        } catch(Exception e) {
            printErrorLog(json, e, "保存失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-自定义分类编辑规则", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }
    /**
     * 自定义模板查询
     */
    @RequestMapping("/quertVoucherTemplet")
    public ReturnData<Grid> quertVoucherTemplet(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        try {
            String pk_category = param.get("categoryid");
            String pk_corp=SystemUtil.getLoginCorpId();
            List<VouchertempletHVO> list=iVoucherTemplet.queryVoucherTempletList(pk_corp, pk_category);
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
            grid.setRows(list);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败!");
        }
        return ReturnData.ok().data(grid);
    }
    /**
     * 整理分类
     */
    @RequestMapping("/updateCategoryAgain")
    public ReturnData<Grid> updateCategoryAgain(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        boolean lock = false;
        int count = 0;
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            String pk_category = param.get("categoryid");
            String pk_parent = param.get("pk_parent");
            String period = param.get("period");
            checkPeriod(period, true);		//检查期间合法性
            if(StringUtils.isEmpty(period)){
                throw new BusinessException("参数异常");
            }
            while (lock == false && count < 10)
            {
                lock = redissonDistributedLock.tryGetDistributedFairLock("zncsCategory_"+pk_corp+period);
                if (lock == false)
                {
                    Thread.sleep(500);
                    count++;
                }
            }
            if (lock) {
                try {
                    iBillcategory.updateCategoryAgain(pk_category, pk_parent, pk_corp, period);
                    grid.setSuccess(true);
                    grid.setMsg("整理成功!");
                } catch (Exception e) {
                    printErrorLog(grid, e, "整理失败!");
                }
                finally {
                    if(lock){
                        redissonDistributedLock.releaseDistributedFairLock("zncsCategory_"+pk_corp+period);
                    }
                }
            }
            else
            {
                grid.setSuccess(false);
                grid.setMsg("系统正在处理，请稍后!");
            }
        } catch (Exception e) {
            printErrorLog(grid, e, "整理失败!");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-整理分类", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }
    /*
     * 检测分类
     */
    @RequestMapping("/checkCategory")
    public ReturnData<Grid> checkCategory(@RequestBody Map<String,String> param) {
        Grid grid = new Grid();
        String period = param.get("period");
        try {
            String pk_corp = SystemUtil.getLoginCorpId();
            checkPeriod(period, true);		//检查期间合法性
//			if (StringUtils.isEmpty(period)) {
//				throw new BusinessException("参数异常");
//			}

            List<CheckOcrInvoiceVO> list = iBillcategory.modifyCheckCategory(pk_corp, period);
            grid.setRows(list);
            grid.setSuccess(true);
            grid.setMsg("检测成功!");

        } catch (Exception e) {
            printErrorLog(grid, e, "检测失败!");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-检测分类:期间："+period, ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }
    @RequestMapping("/changeBatchPeorid_long")
    public ReturnData<Json> changeBatchPeorid_long(@RequestBody Map<String,String> param){
        Json json = new Json();
        BillcategoryQueryVO paramVO=buildParamVO(param);
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            CorpVO currcorp = SystemUtil.getLoginCorpVo();
            DZFDate begdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(currcorp.getBegindate())) ;

            checkPeriod(paramVO.getPeriod(), true);  	//检查期间，主要是格式是否是合法日期
            if(begdate.after(new DZFDate(paramVO.getPeriod()+"-01"))){
                throw new BusinessException("期间不能在建账期间("+DateUtils.getPeriod(begdate)+")前!");
            }
            boolean isgz = qmgzService.isGz(SystemUtil.getLoginCorpId(), new DZFDate(paramVO.getPeriod()+"-01").toString());
            if (isgz) {// 是否关账
                throw new BusinessException("所选入账期间"+paramVO.getPeriod()+"已关账，请检查！");
            }
            iInterfaceBill.updateChangeBatchPeorid(paramVO);
            json.setSuccess(true);
            json.setMsg("更改成功!");
        } catch (Exception e) {
            json.setStatus(-100);
            printErrorLog(json, e, "更改失败!");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-票据跨期:"+"跨期至"+paramVO.getPeriod(), ISysConstants.SYS_2);
        return ReturnData.ok().data(json);

    }
    @RequestMapping("/saveOcrInvoiceVO")
    public ReturnData<Json> saveOcrInvoiceVO(@RequestBody Map<String,String> param){
        Json json = new Json();
        String webid=null;
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            String head = param.get("head");
            String body = param.get("body");
            body = body.replace("}{", "},{");


            OcrInvoiceVO headvo = JsonUtils.deserialize(head, OcrInvoiceVO.class);
            OcrInvoiceDetailVO[] bodyvos = JsonUtils.deserialize(body,OcrInvoiceDetailVO[].class);
            if(!headvo.getPk_corp().equals(pk_corp)){
                throw new BusinessException("无权操作此数据.");
            }
//            OcrInvoiceDetailVO[] bodyvos =DzfTypeUtils.cast(array,bodymapping, OcrInvoiceDetailVO[].class, JSONConvtoJAVA.getParserConfig());
            iInterfaceBill.updateInvoiceInfo(headvo, bodyvos);
            webid=headvo.getWebid();
            json.setSuccess(true);
            json.setMsg("保存成功");
        } catch(Exception e) {
//			e.printStackTrace();
            printErrorLog(json, e, "保存失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-修改票据信息：图片ID："+webid, ISysConstants.SYS_2);
        return ReturnData.ok().data(json);

    }
    @RequestMapping("/queryRule")
    public ReturnData<Json> queryRule(){
        Json json = new Json();
        try {
            VatInvoiceSetVO[] vos = vatinvoiceserv.queryByType(SystemUtil.getLoginCorpId(), IBillManageConstants.HEBING_ZNPZ);

            VatInvoiceSetVO vo = null;
            if(vos != null && vos.length > 0){
                vo = vos[0];
            }

            json.setRows(vo);
            json.setMsg("查询合并规则成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "查询合并规则失败");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "智能凭证更新业务类型", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @RequestMapping("/combineRule")
    public ReturnData<Json> combineRule(@RequestBody Map<String,String> param){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            String pzrule = param.get("pzrule");
            String flrule = param.get("flrule");
            String zy = param.get("zy");
            String setId = param.get("setid");
            String bk = param.get("bk");
            if(StringUtil.isEmpty(pzrule)
                    || StringUtil.isEmpty(flrule)){
                throw new BusinessException("合并规则设置失败，请重试");
            }
            VatInvoiceSetVO vo = new VatInvoiceSetVO();
            String[] fields = new String[]{ "value", "entry_type", "isbank", "zy" };
            if(!StringUtil.isEmpty(setId)){
                vo.setPrimaryKey(setId);
            }else{
                vo.setPk_corp(pk_corp);
                vo.setStyle(IBillManageConstants.HEBING_ZNPZ);
            }
            vo.setValue(Integer.parseInt(pzrule));
            vo.setEntry_type(Integer.parseInt(flrule));
            vo.setZy(zy);
            if(StringUtil.isEmpty(bk)){
                vo.setIsbank(null);
            }else{
                vo.setIsbank(DZFBoolean.TRUE);
            }

            vatinvoiceserv.updateVO(pk_corp, vo, fields);
            json.setMsg("合并规则设置成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "合并规则设置失败");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "合并规则调整", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    /**
     * 智能做账
     *String pk_category=getRequest().getParameter("categoryid");//公司树分类ID
     *  String pk_parent=getRequest().getParameter("parentid");//公司树分类ID
     * String pk_bills=getRequest().getParameter("ids");//票据IDS
     * String isforce=getRequest().getParameter("isforce" );//强制制单(忽略掉提示性质错误)N是检查，Y是忽略错误
     * String pk_bankcode=getRequest().getParameter("pk_bankcode" );
     */
    @RequestMapping("/makeAccount_long")
    public ReturnData<Json> makeAccount_long(@RequestBody Map<String,String> param){
        String period = param.get("period");
        String pk_category = param.get("categoryid");
        String pk_parent = param.get("parentid");
        String pk_bills = param.get("ids");
        String isforce = param.get("isforce");
        String pk_bankcode = param.get("pk_bankcode");
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        boolean lock = false;
        try{
            lock = redissonDistributedLock.tryGetDistributedFairLock("zncsVoucher"+pk_corp+period);
            if(!lock){//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍后刷新数据");
                return ReturnData.error().data(json);
            }
            checkPeriod(period, true);		//检查期间合法性
            if(!StringUtil.isEmpty(pk_bankcode)&&pk_category!=null&&!pk_category.startsWith("bank_")){
                pk_parent=pk_bankcode;
            }
            Map<String, Map<String, Object>> checkMsgMap=new HashMap<String, Map<String, Object>>();
            List<TzpzHVO> list=iZncsVoucher.processGeneralTzpzVOs(pk_category, pk_bills, period, pk_corp,pk_parent,checkMsgMap,SystemUtil.getLoginUserId());
            boolean isError=false;
            Iterator<String> itor=checkMsgMap.keySet().iterator();
            while(itor.hasNext()){
                Map<String, Object> valueMap=checkMsgMap.get(itor.next());
                //强制的有错，直接返回错误，不管前端参数
                if(valueMap.get("ismust").toString().equals("Y")&&!valueMap.get("count").toString().equals("0")){
                    isError=true;
                    break;
                    //非强制的有错，看前端参数
                }else if(valueMap.get("ismust").toString().equals("N")&&!valueMap.get("count").toString().equals("0")&&"N".equals(isforce)){
                    isError=true;
                    break;
                }
            }
            json.setSuccess(true);
//			if(isError){//需要返回错误信息
//				json.setStatus(777);
//				json.setRows(checkMsgMap);
//			}else{//不需要
//				json.setStatus(888);
//				json.setRows(list);
//			}
//			Map<String, Object> returnObject=new HashMap<String, Object>();
//			returnObject.put("voucherList", list);
//			returnObject.put("errorList", checkMsgMap);
            json.setRows(list);
            json.setData(checkMsgMap);
            json.setMsg("成功");
        } catch(Exception e) {
            printErrorLog(json, e, "失败");
        } finally{
            if(lock){//处理
                redissonDistributedLock.releaseDistributedFairLock("zncsVoucher"+pk_corp+period);
            }
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-智能做账:期间："+period, ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @Autowired
    private IInventoryAccSetService gl_ic_invtorysetserv = null;
    @Autowired
    private IVATInComInvoice2Service gl_vatincinvact;
    @Autowired
    private CheckInventorySet inventory_setcheck;

    @RequestMapping("/getInventroyType")
    public ReturnData<Json> getInventroyType(){
        Json json = new Json();
        try{
            CorpVO corpvo = SystemUtil.getLoginCorpVo();
            String pk_corp=corpvo.getPk_corp();
            json.setSuccess(true);
            json.setData(corpvo.getBbuildic());
            json.setMsg("成功");
        } catch(Exception e) {
            printErrorLog(json, e, "失败");
        }
        return ReturnData.ok().data(json);
    }
    //保存总账存货
    @RequestMapping("/saveInventoryData_long")
    public ReturnData<Grid> saveInventoryData_long(@RequestBody Map<String,String> param) {
        Grid grid = new Grid();
        boolean lock = false;
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            InventorySetVO inventorySetVO = gl_ic_invtorysetserv.query(SystemUtil.getLoginCorpId());
            CorpVO corpvo = SystemUtil.getLoginCorpVo();
            if(!IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic())){//启用库存 --匹配存货
                throw new BusinessException("当前公司未启用总账库存核算!");
            }
            // 加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("zncswb2pp"+pk_corp);
            if (!lock) {// 处理
                grid.setSuccess(false);
                grid.setMsg("正在处理中，请稍候");
                return ReturnData.error().data(grid);
            }
            String goods = param.get("goods");
            InventoryAliasVO[] goodvos = getInvAliasData(goods);
            if (goodvos == null || goodvos.length == 0)
                throw new BusinessException("未找到存货别名数据，请检查");
            String error = iInterfaceBill.checkInvtorySubj(goodvos, inventorySetVO, pk_corp, SystemUtil.getLoginUserId(), true);
            if (!StringUtil.isEmpty(error)) {
                error = error.replaceAll("<br>", " ");
                throw new BusinessException("进项发票存货匹配失败:"+error);
            }
            List<Grid> logList = new ArrayList<Grid>();//记录更新日志
            gl_vatincinvact.saveInventoryData(pk_corp, goodvos, logList);
            grid.setSuccess(true);
            grid.setMsg("智能发票匹配存货成功");
        } catch (Exception e) {
            printErrorLog(grid,  e, "智能发票匹配存货失败");
        } finally {
            if(lock){
                redissonDistributedLock.releaseDistributedFairLock("zncswb2pp"+pk_corp);
            }
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-匹配存货", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }


    private InventoryAliasVO[] getInvAliasData(String goods) {

        InventoryAliasVO[] vos = null;

        if (StringUtil.isEmpty(goods)) {
            return null;
        }
        goods = goods.replace("}{", "},{");
        //goods = "[" + goods + "]";
        vos = JsonUtils.deserialize(goods, InventoryAliasVO[].class);
        return vos;
    }
    //匹配总账存货的
    @RequestMapping("/matchInventoryData_long")
    public ReturnData<Json> matchInventoryData_long(@RequestBody Map<String,String> param){
        //启用存货的

        Json json = new Json();
        try{
            String isshow = param.get("isshow");
            String period  = param.get("period");
            String category = param.get("categoryid");
            String bills = param.get("ids");
            CorpVO corpvo = SystemUtil.getLoginCorpVo();
            if(StringUtil.isEmpty(isshow)){
                isshow ="Y";
            }
            if(IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic())){//启用总账库存 --匹配存货
                InventorySetVO inventorySetVO = gl_ic_invtorysetserv.query(SystemUtil.getLoginCorpId());

                if(inventorySetVO == null){
                    throw new BusinessException("启用总账核算存货，请先设置存货成本核算方式!");
                }

                //String error = inventory_setcheck.checkInventorySet(getLoginUserid(), getLogincorppk(),inventorySetVO);

                if(inventorySetVO != null && inventorySetVO.getChcbjzfs()== InventoryConstant.IC_NO_MXHS){
                    throw new BusinessException("不核算存货模式不允许匹配存货!");
                }

//				if (!StringUtil.isEmpty(error)) {
//					error = error.replaceAll("<br>", " ");
//					throw new BusinessException("进项发票存货匹配失败:"+error);
//				}
                checkPeriod(period, true);		//检查期间合法性
//				if(StringUtil.isEmpty(period)){
//					throw new BusinessException("期间不能为空!");
//				}
                String billids []= StringUtil.isEmpty(bills)?null: bills.split(",");
                List<OcrInvoiceVO> list = iInterfaceBill.queryMatchInvoice(SystemUtil.getLoginCorpId(), period,billids, category, 3);
                if(list==null||list.size()==0){
                    throw new BusinessException("未找到所需要匹配的票据信息，请检查");
                }
                List<InventoryAliasVO> relvos = iInterfaceBill.matchInventoryData(corpvo.getPk_corp(), list, inventorySetVO);
                if(relvos != null && relvos.size()>0){

                    String error = iInterfaceBill.checkInvtorySubj(relvos.toArray(new InventoryAliasVO[0]), inventorySetVO, SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId(), false);
                    if (!StringUtil.isEmpty(error)) {
                        error = error.replaceAll("<br>", " ");
                        throw new BusinessException("存货匹配失败:"+error);
                    }

                    //没有匹配上存货的默认新增
                    List<InventoryAliasVO>  addlist =new ArrayList<>();
                    DZFBoolean  show = new DZFBoolean(isshow);
                    for(InventoryAliasVO vo:relvos){
                        if(StringUtil.isEmpty(vo.getPk_inventory())){
                            vo.setIsAdd(DZFBoolean.TRUE);
                            addlist.add(vo);
                        }else{
                            vo.setIsAdd(DZFBoolean.FALSE);
                        }
                    }
                    if(!show.booleanValue()){
                        relvos =addlist;
                    }
                    for (int i = 0; i < relvos.size(); i++) {
                        relvos.get(i).setMid((i+1)+"");
                    }
                    json.setRows(relvos);
                    Collections.sort(relvos, new Comparator<InventoryAliasVO>() {
                        @Override
                        public int compare(InventoryAliasVO o1, InventoryAliasVO o2) {
                            return o2.getIsAdd().compareTo(o1.getIsAdd());
                        }
                    });

                }else{
                    throw new BusinessException("智能发票明细数据不存在!");
                }
            }
//			else if(IcCostStyle.IC_ON.equals(corpvo.getBbuildic())){
//
//			}
            else{
                throw new BusinessException("当前没有启用总账核算存货!");
            }
            json.setSuccess(true);
            json.setMsg("智能发票匹配成功");
        } catch(Exception e) {
//			e.printStackTrace();
            printErrorLog(json, e, "智能发票匹配失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-匹配存货", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);

    }


    @RequestMapping("/getGoodsInvenRela_long")
    public ReturnData<Grid> getGoodsInvenRela_long(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        try{
            String period = param.get("period");
            String ids = param.get("ids");
            String category = param.get("categoryid");
            CorpVO corpvo = SystemUtil.getLoginCorpVo();
            String pk_corp = corpvo.getPk_corp();
            if(!IcCostStyle.IC_ON.equals(corpvo.getBbuildic())){//启用库存 --匹配存货
                throw new BusinessException("当前公司未启用库存核算!");
            }
            String []bills = StringUtil.isEmpty(ids)?null:ids.split(",");
            if(StringUtil.isEmpty(period)){
                throw new BusinessException("期间不能为空!");
            }
            List<OcrInvoiceVO> stoList = iInterfaceBill.queryMatchInvoice(SystemUtil.getLoginCorpId(), period, bills, category, 3);
            if(stoList == null
                    || stoList.size() == 0)
                throw new BusinessException("未找到所需要匹配的票据信息，请检查");

            List<VatGoosInventoryRelationVO> relvos = iInterfaceBill.getGoodsInvenRela(
                    stoList, pk_corp);

            List<VatGoosInventoryRelationVO> blankvos = new ArrayList<VatGoosInventoryRelationVO>();
            //List<VatGoosInventoryRelationVO> newRelvos = getFilterGoods(relvos, blankvos, pk_corp);
//
            Long total = relvos == null ? 0L : relvos.size();
//			if(blankvos.size() == 0){
//				total = newRelvos == null ? 0L : newRelvos.size();
//			}
            //if(newRelvos==null){
            //grid.setTotal(0L);
            //	grid.setRows(null);
            //}else{
            if(relvos!=null&&relvos.size()>0){//用于前端确定唯一
                for (int i = 0; i < relvos.size(); i++) {
                    relvos.get(i).setMid((i+1)+"");
                }
            }
            grid.setTotal(total);
            grid.setRows(relvos);
            //}

            grid.setSuccess(true);
            grid.setMsg("获取存货对照关系成功");
        }catch(Exception e){
            printErrorLog(grid, e, "获取存货对照关系失败");
        }

        return ReturnData.ok().data(grid);
    }
    @RequestMapping("/saveVatGoosInventory_long")
    public ReturnData<Grid> saveVatGoosInventory_long(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            CorpVO corpvo = SystemUtil.getLoginCorpVo();
            if(!IcCostStyle.IC_ON.equals(corpvo.getBbuildic())){//启用库存 --匹配存货
                throw new BusinessException("当前公司未启用库存核算!");
            }
            String goods = param.get("goods");
            VatGoosInventoryRelationVO[] vos = JsonUtils.deserialize(goods,VatGoosInventoryRelationVO[].class);
            if(vos==null||vos.length==0)throw new BusinessException("未勾选数据，请检查!");
            iInterfaceBill.updateGoodsInvenRela(vos, SystemUtil.getLoginUserId(), pk_corp);
            //grid.setTotal(total);
            //grid.setRows(newRelvos);
            grid.setSuccess(true);
            grid.setMsg("智能发票匹配存货成功!");
        }catch(Exception e){
            printErrorLog(grid, e, "智能发票匹配存货失败!");
        }

        return ReturnData.ok().data(grid);

    }

    private List<VatGoosInventoryRelationVO> getFilterGoods(List<VatGoosInventoryRelationVO> relvos,
                                                            List<VatGoosInventoryRelationVO> blankvos,
                                                            String pk_corp){

        YntCpaccountVO[] cpavos = accountService.queryByPk(pk_corp);

        String code;
        String fzhs;
        boolean flag = false;
        for(YntCpaccountVO cpa : cpavos){
            code = cpa.getAccountcode();
            if(!StringUtil.isEmpty(code)
                    && (code.startsWith("500101")
                    || code.startsWith("600101")
                    || code.startsWith("5001001")
                    || code.startsWith("6001001")
                    || code.startsWith("1405")
                    || code.startsWith("505101")
                    || code.startsWith("605101")

            )){
                fzhs = cpa.getIsfzhs();
                if( !StringUtil.isEmpty(fzhs) && fzhs.charAt(5) == '1'){
                    flag = true;
                    break;
                }

            }
        }

        if(!flag){
            //throw new BusinessException("请检查库存商品,商品销售收入,销售材料收入科目的辅助核算设置!");
            return null;
        }

        List<VatGoosInventoryRelationVO> sencondList = new ArrayList<VatGoosInventoryRelationVO>();

        if(relvos != null && relvos.size() > 0){
            String pk_inventory;
            for(VatGoosInventoryRelationVO vo : relvos){
                pk_inventory = vo.getPk_inventory();
                if(StringUtil.isEmpty(pk_inventory)){
                    blankvos.add(vo);
                }
                sencondList.add(vo);
            }
        }

        return sencondList;
    }


    /**
     * 凭证保存
     */
    @RequestMapping("/saveTzpzHVOs")
    public ReturnData<Json> saveTzpzHVOs(@RequestBody String vouchers){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            ArrayList objMap = getObjectMapper().readValue(vouchers, ArrayList.class);
            List<TzpzHVO> tzpzHVOs=turnTzpzHVOs(objMap);
            iZncsVoucher.saveVouchersBefore(tzpzHVOs);
            json.setSuccess(true);
            json.setMsg("保存成功");
        } catch(Exception e) {
            printErrorLog(json, e, "保存失败");
        }
        return ReturnData.ok().data(json);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<TzpzHVO> turnTzpzHVOs(ArrayList objMap){
        List<TzpzHVO> list=new ArrayList<TzpzHVO>();
        for(int i=0;i<objMap.size();i++){
            LinkedHashMap map=(LinkedHashMap)objMap.get(i);
            LinkedHashMap head=(LinkedHashMap)map.get("head");
            ArrayList bodys=(ArrayList)map.get("body");
            TzpzHVO headVO=buildTzpzHVO(head);
            TzpzBVO[] bodyVOs=buildTzpzBVOs(bodys);
            headVO.setChildren(bodyVOs);
            list.add(headVO);
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    private TzpzHVO buildTzpzHVO(LinkedHashMap head){
        TzpzHVO headVO=new TzpzHVO();
        headVO.setCoperatorid(SystemUtil.getLoginUserId());//制单人id
        headVO.setDr(0);
        headVO.setPk_corp(SystemUtil.getLoginCorpId());
        if(head.containsKey("dfhj")){
            headVO.setDfmny(new DZFDouble((String)head.get("dfhj")));//贷方合计
        }
        if(head.containsKey("zdrq")){
            headVO.setDoperatedate(new DZFDate(head.get("zdrq").toString()));//制单日期
        }
        if(head.containsKey("fp_style")){
            headVO.setFp_style(Integer.parseInt((String)head.get("fp_style")));//1普票 2专票3未开票
        }
        if(head.containsKey("iautorecognize")){
            headVO.setIautorecognize(Integer.parseInt((String)head.get("iautorecognize")));//// 0-- 非识别 1----识别
        }
//		if(head.containsKey("ifptype")){
//			headVO.setFp_style(Integer.parseInt((String)head.get("ifptype")));//0-- 销项 // 1---进项 // 2---其他
//		}
        if(head.containsKey("sffpxjll")){
            headVO.setIsfpxjxm(new DZFBoolean((String)head.get("sffpxjll")));//是否已分配现金流量项目
        }
        if(head.containsKey("sfjz")){
            headVO.setIshasjz(new DZFBoolean((String)head.get("sfjz")));//是否记账
        }
        if(head.containsKey("isocr")){
            headVO.setIsocr(new DZFBoolean((String)head.get("isocr")));
        }
        if(head.containsKey("jfhj")){
            headVO.setJfmny(new DZFDouble((String)head.get("jfhj")));//借方合计
        }
        if(head.containsKey("fdjs")){
            headVO.setNbills(Integer.parseInt((String)head.get("fdjs")));//附单据数
        }
        if(head.containsKey("qj")){
            headVO.setPeriod((String)head.get("qj"));//期间
        }
        if(head.containsKey("tpgid")){
            headVO.setPk_image_group((String)head.get("tpgid"));//图片IDS
        }
        if(head.containsKey("pzh")){
            headVO.setPzh((String)head.get("pzh"));//凭证号
        }
        if(head.containsKey("pzlb")){
            headVO.setPzlb(Integer.parseInt((String)head.get("pzlb")));//0记账凭证
        }
        if(head.containsKey("lydjid")){
            headVO.setSourcebillid((String)head.get("lydjid"));//来源单据id
        }
        if(head.containsKey("lydjlx")){
            headVO.setSourcebilltype((String)head.get("lydjlx"));//来源单据类型
        }
        if(head.containsKey("pzzt")){
            headVO.setVbillstatus(Integer.parseInt((String)head.get("pzzt")));//凭证状态
        }
        if(head.containsKey("userObject")){
            headVO.setUserObject((String)head.get("userObject"));//票据ids
        }
        if(head.containsKey("nd")){
            headVO.setVyear(Integer.parseInt((String)head.get("nd")));//年度
        }
        return headVO;
    }

    @SuppressWarnings("rawtypes")
    private TzpzBVO[] buildTzpzBVOs(ArrayList bodys){
        List<TzpzBVO> list=new ArrayList<TzpzBVO>();
        for(int i=0;i<bodys.size();i++){
            TzpzBVO bodyVO=new TzpzBVO();
            bodyVO.setDr(0);
            bodyVO.setPk_corp(SystemUtil.getLoginCorpId());
            LinkedHashMap body=(LinkedHashMap)bodys.get(i);
            if(body.containsKey("dfmny")){
                bodyVO.setDfmny(new DZFDouble((String)body.get("dfmny")));//贷方金额
            }
            if(body.containsKey("isCur")){
                bodyVO.setIsCur(new DZFBoolean((String)body.get("isCur")));//是否外汇
            }
            if(body.containsKey("jfmny")){
                bodyVO.setJfmny(new DZFDouble((String)body.get("jfmny")));//借方金额
            }
            if(body.containsKey("fullname")){
                bodyVO.setKmmchie((String)body.get("fullname"));//科目全称
            }
            if(body.containsKey("chnum")){
                bodyVO.setNnumber(new DZFDouble((String)body.get("chnum")));//存货数量
            }
            if(body.containsKey("chdj")){
                bodyVO.setNprice(new DZFDouble((String)body.get("chdj")));//存货单价
            }
            if(body.containsKey("kmid")){
                bodyVO.setPk_accsubj((String)body.get("kmid"));//科目
            }
            if(body.containsKey("bzid")){
                bodyVO.setPk_currency((String)body.get("bzid"));//币种
            }
            if(body.containsKey("kmcode")){
                bodyVO.setVcode((String)body.get("kmcode"));//科目编码
            }
            if(body.containsKey("fx")){
                bodyVO.setVdirect(Integer.parseInt((String)body.get("fx")));//方向
            }
            if(body.containsKey("kmname")){
                bodyVO.setVname((String)body.get("kmname"));//科目名称
            }
            if(body.containsKey("zy")){
                bodyVO.setZy((String)body.get("zy"));//摘要
            }
            list.add(bodyVO);
        }
        return list.toArray(new TzpzBVO[0]);
    }

    /**
     * 手动凭证
     */
    @RequestMapping("/handVoucher")
    public ReturnData<Json> handVoucher(@RequestBody Map<String,String> param){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            String pk_category = param.get("categoryid");
            String pk_parent = param.get("parentid");
            String pk_bills = param.get("ids");
            String period = param.get("period");
            String pk_bankcode = param.get("pk_bankcode");
            checkPeriod(period, true);		//检查期间合法性
            if(!StringUtil.isEmpty(pk_bankcode)&&!pk_category.startsWith("bank_")){
                pk_parent=pk_bankcode;
            }
            Map<String, Object> billMap=iZncsVoucher.generalHandTzpzVOs(pk_category, pk_bills, period, pk_corp,pk_parent);
            json.setSuccess(true);
            json.setRows(billMap);
            json.setMsg("成功");
        } catch(Exception e) {
            printErrorLog(json, e, "失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-手动凭证", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    /**
     * 手动凭证保存
     */
    @RequestMapping("/saveHandTzpzHVOs")
    public ReturnData<Json> saveHandTzpzHVOs(@RequestBody String vouchers){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            ArrayList objMap = getObjectMapper().readValue(vouchers, ArrayList.class);
            List<TzpzHVO> tzpzHVOs=turnTzpzHVOs(objMap);
            iZncsVoucher.saveHandVouchers(tzpzHVOs);
            json.setSuccess(true);
            json.setMsg("保存成功");
        } catch(Exception e) {
            printErrorLog(json, e, "保存失败");
        }
        return ReturnData.ok().data(json);
    }

    @RequestMapping("/queryBatchBillByState")
    public ReturnData<Json> queryBatchBillByState(@RequestBody Map<String,String> param){
        Json json = new Json();
        try{
            String state = param.get("istate");
            String period = param.get("period");
//            String state=getRequest().getParameter("istate");//票据IDS
//            String period=getRequest().getParameter("period");//期间
            if(period==null) throw new BusinessException("期间不能为空");
            String pk_corp=SystemUtil.getLoginCorpId();
            List<BillInfoVO> list = iInterfaceBill.queryBillByState(pk_corp, period, state==null?null:new Integer(state));
            json.setSuccess(true);
            json.setRows(list);
            json.setMsg("查询成功");
        } catch(Exception e) {
            printErrorLog(json, e, "查询失败");
        }
        return ReturnData.ok().data(json);
    }
    /**
     * 查票的表体
     */
    @RequestMapping("/queryInvoicDetails")
    public ReturnData<Json> queryInvoicDetails(@RequestBody Map<String,String> param){
        Json json = new Json();
        try{
            String pk_invoice = param.get("pk_bill");
//            String pk_invoice=getRequest().getParameter("pk_bill");//票据主键
            List<OcrInvoiceDetailVO> list=iBillcategory.queryDetailVOs(pk_invoice);
            json.setSuccess(true);
            json.setRows(list);
            json.setMsg("查询成功");
        } catch(Exception e) {
            printErrorLog(json, e, "查询失败");
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 表体移动到
     */
    @RequestMapping("/saveNewCategoryBody")
    public ReturnData<Json> saveNewCategoryBody(@RequestBody Map<String,String> param){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            String bills = param.get("ids");
            String treeid = param.get("treeid");
            String period = param.get("period");
            checkPeriod(period, true);		//检查期间合法性
            iBillcategory.saveNewCategoryBody(bills.split(","), treeid, pk_corp, period);
            json.setSuccess(true);
            json.setMsg("设置成功");
        } catch(Exception e) {
            printErrorLog(json, e, "设置失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "票据工作台-移动票据明细行", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    /**
     * 点分类感叹号
     */
    @RequestMapping("/queryErrorDetails")
    public ReturnData<Grid> queryErrorDetails(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        try {
            BillcategoryQueryVO paramVO=buildParamVO(param);
            List<CheckOcrInvoiceVO> list = iBillcategory.queryErrorDetailVOs(paramVO);
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
            grid.setRows(list);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败!");
        }
        return ReturnData.ok().data(grid);
    }

    @RequestMapping("/queryBankInfo")
    public ReturnData<Grid> queryBankInfo(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        try {
            String account = param.get("account");
            String type = param.get("type");
            String accountcode = param.get("accountcode");
            String bperiod = param.get("bperiod");
            String eperiod = param.get("eperiod");
            String ismr = param.get("ismust");
            String pk_corp=SystemUtil.getLoginCorpId();

            List<BankStatementVO2> list = iInterfaceBill.queryBankInfo(pk_corp, bperiod,eperiod, account, type, accountcode,ismr);
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
            grid.setRows(list);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败!");
        }

        return ReturnData.ok().data(grid);

    }

    @RequestMapping("/matchBankInfo")
    public ReturnData<Grid> matchBankInfo( Map<String,String> param){
        Grid grid = new Grid();
        try {
            String pk_bankdzd = param.get("pk_bankdzd");
            String pk_bankhd = param.get("pk_bankhd");
            String pk_corp=SystemUtil.getLoginCorpId();

            iInterfaceBill.updateMatchBankInfo(pk_bankdzd, pk_bankhd);
            grid.setSuccess(true);
            grid.setMsg("匹配成功!");

        } catch (Exception e) {
            printErrorLog(grid, e, "匹配失败!");
        }

        return ReturnData.ok().data(grid);

    }

    @RequestMapping("/queryB")
    public ReturnData<Json> queryB(@RequestBody Map<String,String> param) {
        Json json = new Json();
        String hid = param.get("id");
        String kmid = param.get("kmid");
        String billtype = param.get("billtype");
        String isfenye = param.get("isfenye");
        String pk_corp = param.get("pk_corp");
        Integer page = StringUtil.isEmpty(param.get("page"))?1: java.lang.Integer.parseInt(param.get("page"));
        Integer rows = StringUtil.isEmpty(param.get("rows"))?100: java.lang.Integer.parseInt(param.get("rows"));
        try {

            if (StringUtil.isEmpty(hid)) {
                json.setMsg("参数为空！");
                json.setSuccess(false);
                return ReturnData.error().data(json);
            }

            if (StringUtil.isEmpty(pk_corp)) {
                pk_corp = SystemUtil.getLoginCorpId();
            } else {
                if (!ReportUtil.checkHasRight(SystemUtil.getLoginUserId(), pk_corp))
                    throw new BusinessException("无权操作！");
            }
            AuxiliaryAccountBVO[] bvos = null;
            if (StringUtil.isEmpty(billtype)) {
                if ("Y".equals(isfenye)) {//分页
                    QueryPageVO pagevo = gl_fzhsserv.queryBodysBypage(hid, pk_corp, kmid, page, rows,null);
                    json.setTotal(Long.valueOf(pagevo.getTotal()));
                    json.setRows(pagevo.getPagevos());
                } else {
                    bvos = gl_fzhsserv.queryB(hid, pk_corp, kmid);
                    if (bvos == null || bvos.length == 0){
                        bvos = new AuxiliaryAccountBVO[0];
                    }else{
                        String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
                        String djnumStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
                        int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
                        int djnum= StringUtil.isEmpty(djnumStr) ? 4 : Integer.parseInt(djnumStr);
                        Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(pk_corp);
                        bvos = Arrays.asList(bvos).stream().filter(v -> v.getSffc() == null || v.getSffc() == 0).toArray(AuxiliaryAccountBVO[]::new);
                        QueryParamVO queryParamvo = new QueryParamVO();
                        queryParamvo.setPk_corp(SystemUtil.getLoginCorpVo().getPk_corp());
                        queryParamvo.setEnddate(new DZFDate());
                        queryParamvo.setBegindate1(new DZFDate());
                        Map<String, IcDetailVO>  result = ic_rep_cbbserv.queryDetail(queryParamvo, SystemUtil.getLoginCorpVo());;
                        if(!result.isEmpty()){
                            Set<String> set = result.keySet();
                            Map<String, String> keymap = new HashMap<String, String>();
                            for (String str : set) {
                                if(!StringUtil.isEmpty(str)  &&str.contains("_")){
                                    String ss []=str.split("_");
                                    keymap.put(ss[0], str);
                                }
                            }
                            for (int i = 0; i < bvos.length; i++) {
                                if( keymap.containsKey(bvos[i].getPk_auacount_b())){
                                    String key = keymap.get(bvos[i].getPk_auacount_b());
                                    IcDetailVO icvo = result.get(key);
                                    bvos[i].setNjznum(icvo.getJcsl()==null?icvo.getJcsl():new DZFDouble(icvo.getJcsl().toString(),num));
                                    bvos[i].setJsprice(icvo.getJcdj()==null?icvo.getJcdj():new DZFDouble(icvo.getJcdj().toString(),djnum));

                                }
                                if(accmap.containsKey(bvos[i].getKmclassify())){
                                    bvos[i].setKmclassifyname(accmap.get(bvos[i].getKmclassify()).getAccountname());
                                }
                            }
                        }


                        for (int i = 0; i < bvos.length; i++) {
                            if(accmap.containsKey(bvos[i].getKmclassify())){
                                bvos[i].setKmclassifyname(accmap.get(bvos[i].getKmclassify()).getAccountname());
                            }
                        }
                    }


                    json.setRows(bvos);
                }
                json.setMsg("查询成功");
                json.setSuccess(true);
            } else {
                List<AuxiliaryAccountBVO> list = gl_fzhsserv.queryPerson(hid, pk_corp, billtype);
                if (list != null && list.size() > 0) {
                    bvos = list.stream().filter(v -> v.getSffc() == null || v.getSffc() == 0).toArray(AuxiliaryAccountBVO[]::new);
                }
                if (bvos == null || bvos.length == 0)
                    bvos = new AuxiliaryAccountBVO[0];

                json.setRows(bvos);
                json.setMsg("查询成功");
                json.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.setRows(new AuxiliaryAccountBVO[0]);
            printErrorLog(json, e, "查询失败!");
        }
        return ReturnData.ok().data(json);
    }
    //统计分析
    @RequestMapping("/queryBillCount")
    public ReturnData<Grid> queryBillCount(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        try {
            String period = param.get("period");
            String pk_corp=SystemUtil.getLoginCorpId();
            if(StringUtils.isEmpty(period)||StringUtils.isEmpty(pk_corp)){
                throw new BusinessException("参数有误！");
            }
            List<BillCountVO> list = billCountService.queryList(period, pk_corp);
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
            grid.setRows(list);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败!");
        }

        return ReturnData.ok().data(grid);

    }
    //跨期到本期，跨期至其他期间票据查询
    @RequestMapping("/queryIntertemporal")
    public ReturnData<Grid> queryIntertemporal(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        try {
            String period = param.get("period");
            String flag = param.get("flag");
            String pk_corp=SystemUtil.getLoginCorpId();
            if(StringUtils.isEmpty(period)||StringUtils.isEmpty(pk_corp)||StringUtils.isEmpty(flag)){
                throw new BusinessException("参数有误！");
            }
            List<BillDetailVO> list = billCountService.queryIntertemporal(period, pk_corp,flag);
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
            grid.setRows(list);
        } catch (Exception e) {
            printErrorLog(grid,e, "查询失败!");
        }

        return ReturnData.ok().data(grid);

    }

    /**
     * 条件查询票据
     */
    @RequestMapping("/queryBillsByWhere")
    public ReturnData<Grid> queryBillsByWhere(@RequestBody Map<String,String> param) {
        Grid grid = new Grid();
        try {
            BillcategoryQueryVO paramVO=buildParamVO(param);
            List<OcrInvoiceVO> list = iBillcategory.queryBillsByWhere(paramVO);
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
            grid.setRows(list);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败!");
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 跨公司
     */
    @RequestMapping("/changeCorp_long")
    public ReturnData<Grid> changeCorp_long(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            String bills = param.get("ids");
            String corpid = param.get("corpid");
            String period = param.get("period");
            checkPeriod(period, true);		//检查期间合法性
            boolean isgz = qmgzService.isGz(corpid, new DZFDate(period+"-01").toString());
            if (isgz) {// 是否关账
                throw new BusinessException("所选入账期间"+period+"已关账，请检查！");
            }

            OcrInvoiceVO vos[]=iInterfaceBill.updateChangeBillCorp(bills.split(","), corpid, period);
            CorpVO corpvo = corpService.queryByPk(corpid);
            StringBuffer buff = new StringBuffer();
            buff.append("票据工作台-期间:").append(period).append("图片ID(").append(vos[0].getWebid()).append(")等");
            buff.append(vos.length).append("张票据跨至").append(corpvo.getUnitname());

            writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, buff.toString(), ISysConstants.SYS_2);

            buff = new StringBuffer();
            corpvo = SystemUtil.getLoginCorpVo() ;
            buff.append("票据工作台-期间:").append(period).append("由").append(corpvo.getUnitname());
            buff.append("跨入图片ID(").append(vos[0].getWebid()).append(")等").append(vos.length).append("张票据");

            writeLogRecord(LogRecordEnum.OPE_KJ_PJGL.getValue(), buff.toString(), ISysConstants.SYS_2,corpid);
            if(vos.length>20){
                grid.setMsg("票据跨公司任务已提交后台执行，请稍后查询！");
            }else{
                grid.setMsg("票据跨公司成功!");
            }
            grid.setSuccess(true);

        } catch (Exception e) {
            printErrorLog(grid, e, "票据跨公司失败!"+e.getMessage());
        }

        return ReturnData.ok().data(grid);

    }
    /**
     * 批量保存凭证
     */
    @RequestMapping("/saveVouchers_long")
    public ReturnData<Json> saveVouchers_long(@RequestBody String vchData,@RequestBody VoucherParamVO paramvo){
        //ZncsVoucherSaveInfo
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            ZncsVoucherSaveInfo voucherinfo = new ZncsVoucherSaveInfo();
            JSONArray array = JSON.parseArray(vchData);
            Object [] jsonlist = (Object[])array.toArray();
            Integer suss = 0;
            Integer fal = 0;
            Map<String,String> map = new HashMap<>();
            List<String> list = new ArrayList<String>();
            List<JSONObject> listtzpz = new ArrayList<JSONObject>();
            DZFBoolean isqxsy = null;
            for (Object headjs1 : jsonlist) {
                JSONObject obj2 = null;
                try {
                    JSONObject obj = (JSONObject)headjs1;
                    obj2 = (JSONObject)obj.clone();
                    TzpzHVO tzpz = saveVoucher(obj,map,list, paramvo);
                    //listtzpz.add(tzpz);
                    suss++;
                } catch (Exception e) {
                    if(!map.containsKey(e.getMessage())){
                        list.add(e.getMessage());
                        map.put(e.getMessage(), e.getMessage());
                    }
                    listtzpz.add(obj2);
                    fal++;
                }
            }
            voucherinfo.setSuss(suss);
            voucherinfo.setFal(fal);
            voucherinfo.setTzpzlist(listtzpz);
            voucherinfo.setMessage(list);

            json.setMsg("保存凭证成功");
            json.setStatus(200);
            json.setSuccess(true);
            json.setRows(voucherinfo);
        } catch (Exception e) {
            printErrorLog(json,e, "保存凭证失败！");
//			json.setMsg("保存凭证失败");
            json.setStatus(IVoucherConstants.STATUS_ERROR_CODE);
//			json.setSuccess(false);
//			json.setRows(null);
        }
        return ReturnData.ok().data(json);
    }
    private TzpzHVO saveVoucher(JSONObject headjs1,Map<String,String> map,List<String> list,VoucherParamVO paramvo){

        //Json json = new Json();
        TzpzHVO headvo =null;
        try{
            CorpVO corpvo =SystemUtil.getLoginCorpVo();
            //String vchData = getRequest().getParameter("vchData");

            //JSONObject headjs1 = (JSONObject) JSON.parse(vchData);
            JSONArray array = headjs1.getJSONArray("children");
            headjs1.remove("children");
//            Map<String,String> headmaping=FieldMapping.getFieldMapping(new TzpzHVO());
//            Map<String,String> bodymapping=FieldMapping.getFieldMapping(new TzpzBVO());
//            headvo =DzfTypeUtils.cast(headjs1,headmaping, TzpzHVO.class, JSONConvtoJAVA.getParserConfig());
            headvo = JSON.parseObject(String.valueOf(headjs1),TzpzHVO.class);
            headvo.setPk_corp(corpvo.getPk_corp());
            headvo.setIshasjz(DZFBoolean.FALSE);
            headvo.setTs(new DZFDateTime(Calendar.getInstance().getTime().getTime()));
            headvo.setDr(0);
            boolean isNew = false;
            if(StringUtil.isEmpty(headvo.getPk_tzpz_h())){
                if ((headvo.getPreserveCode() == null || !headvo.getPreserveCode().booleanValue())
                        && !StringUtil.isEmpty(headvo.getSourcebilltype()) && !"Y".equals(headvo.getIsInsert())) {
                    if (!headvo.getSourcebilltype().endsWith("gzjt") &!headvo.getSourcebilltype().endsWith("gzff")) {
                        headvo.setPzh(yntBoPubUtil.getNewVoucherNo(corpvo.getPk_corp(), headvo.getDoperatedate()));
                    }
                }
                isNew = true;
                headvo.setIsfpxjxm(DZFBoolean.FALSE);
                headvo.setVbillstatus(8);
            }else{
                if(Integer.valueOf(headvo.getPzh())>9999){
                    throw new BusinessException("凭证号最大允许9999");
                };
            }
            //TODO setIsfpxjxm是否已分配现金流量项目 怎么取值？
//            TzpzBVO[] bodyvos =DzfTypeUtils.cast(array,bodymapping, TzpzBVO[].class, JSONConvtoJAVA.getParserConfig());
            TzpzBVO[] bodyvos = array.toArray(new TzpzBVO[0]);
            for(int i=0;i<bodyvos.length;i++){
                bodyvos[i].setPk_corp(corpvo.getPk_corp());
                bodyvos[i].setTs(new DZFDateTime());
            }
            headvo.setChildren(bodyvos);
            //数据中心与在线会计平台针对直接生单方式使用抢占模式,所以保存之前先检验
            if (!StringUtil.isEmpty(headvo.getPk_image_group())) {
                checkIsCreated(headvo);
            }
            checkVoucherData(headvo, paramvo);

            try {
                gl_tzpzserv.checkQjsy(headvo);
            } catch (Exception e) {
                headvo.setIsqxsy(DZFBoolean.TRUE);
                if(!map.containsKey(e.getMessage())){
                    map.put(e.getMessage(), e.getMessage());
                    list.add(headvo.getPeriod()+"期间损益已结转，生成凭证后，请重新结转期间损益！");
                }
            }
            //校验总账存货，提示性
            checkInventorySet(headvo,corpvo);
            headvo = gl_tzpzserv.saveVoucher(corpvo,headvo);
            String msg = getMsgOnSave(headvo, corpvo);
//			json.setMsg(msg);
//			json.setStatus(200);
//			json.setSuccess(true);
//			json.setRows(headvo);
            if (isNew) {
                writeLogRecord(LogRecordEnum.OPE_KJ_ADDVOUCHER,
                        "新增凭证：" + DateUtils.getPeriod(headvo.getDoperatedate()) + "，凭证号：记_" + headvo.getPzh(), ISysConstants.SYS_2);
            } else {
                writeLogRecord(LogRecordEnum.OPE_KJ_EDITVOUCHER,
                        "修改凭证：" + DateUtils.getPeriod(headvo.getDoperatedate()) + "，凭证号：记_" + headvo.getPzh(), ISysConstants.SYS_2);
            }
        }catch(Exception e){

            throw e;
        }
        //writeJson(json);
        return headvo;
    }
    private VoucherParamVO getQueryParamVO(VoucherParamVO paramvo){
//        VoucherParamVO paramvo = new VoucherParamVO();
//        paramvo=(VoucherParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
        if(StringUtil.isEmpty(paramvo.getPk_corp())){
            paramvo.setPk_corp(SystemUtil.getLoginCorpId());
        }
        return paramvo;
    }
    private void checkVoucherData(TzpzHVO headvo,VoucherParamVO paramvo1){
        String pk_corp = SystemUtil.getLoginCorpId();
        if(!StringUtil.isEmpty(headvo.getPk_tzpz_h())){
//			修改时的验证
            VoucherParamVO paramvo = getQueryParamVO(paramvo1);
            paramvo.setPk_corp(pk_corp);
            paramvo.setPk_tzpz_h(headvo.getPk_tzpz_h());
            TzpzHVO old_hvo = gl_tzpzserv.queryVoucherById(paramvo.getPk_tzpz_h());
            if(old_hvo == null){
                throw new BusinessException("凭证不存在，请刷新重试");
            }
            if(!old_hvo.getPk_corp().equals(pk_corp)){
                throw new BusinessException("无权操作！");
            }
            if(old_hvo != null && old_hvo.getIshasjz() != null && old_hvo.getIshasjz().booleanValue()){
                throw new BusinessException("修改失败,已记账凭证不能修改！");
            }
            if(old_hvo != null && old_hvo.getVbillstatus() != 8 && old_hvo.getVbillstatus() != -1){//-1状态为转会计生成凭证使用
                throw new BusinessException("修改失败,已审核凭证不能修改！");
            }
            if ((headvo.getPreserveCode() == null || !headvo.getPreserveCode().booleanValue())
                    && !headvo.getDoperatedate().toString().substring(0, 7).equals(old_hvo.getDoperatedate().toString().substring(0, 7))) {
                headvo.setPzh(yntBoPubUtil.getNewVoucherNo(headvo.getPk_corp(), headvo.getDoperatedate()));
            }
            headvo.setIsfpxjxm(old_hvo.getIsfpxjxm());
        }else{
//			新增时验证
            if(!headvo.getPk_corp().equals(pk_corp)){
                throw new BusinessException("无权操作！");
            }
        }

//		验证凭证分录，必需与登录公司pk相同
        if(headvo.getChildren() != null && headvo.getChildren().length > 0){
            TzpzBVO bvo = null;
            for(int i = 0;i<headvo.getChildren().length;i++){
                bvo = (TzpzBVO)headvo.getChildren()[i];
                if(!bvo.getPk_corp().equals(pk_corp)){
                    throw new BusinessException("分录无权操作！");
                }
            }
        }

    }
    private String getMsgOnSave(TzpzHVO hvo, CorpVO corpVO) {
        StringBuilder msg = new StringBuilder();
        int jflag = 0;
        int dflag = 0;
        TzpzBVO[] bodyvos = (TzpzBVO[]) hvo.getChildren();
        boolean hasIncome = false;
        String incomeCode = null;
        boolean isCbjz = false;
        try {
            QmclVO qmclVO = gl_qmclserv.queryQmclVO(hvo.getPk_corp(), hvo.getPeriod());
            // 成本结转
            isCbjz = qmclVO !=null && qmclVO.getIscbjz() != null && qmclVO.getIscbjz().booleanValue();
        } catch (Exception e) {
            log.error("获取成本结转状态失败", e);
        }
        if ("00000100AA10000000000BMD".equals(corpVO.getCorptype())) {
            incomeCode = "^5001\\d+$";
        } else if ("00000100AA10000000000BMF".equals(corpVO.getCorptype())) {
            incomeCode = "^6001\\d+$";
        } else if ("00000100000000Ig4yfE0005".equals(corpVO.getCorptype())) {
            incomeCode = "^5101\\d+$";
        }
        for(int i=0;i<bodyvos.length;i++){
            Integer fx = bodyvos[i].getVdirect();
            jflag += fx != null && fx == 0 ? 1 : 0;
            dflag += fx != null && fx == 1 ? 1 : 0;
            String code = bodyvos[i].getVcode();
            if (isCbjz && !hasIncome && code != null
                    && incomeCode != null && code.matches(incomeCode)) {
                hasIncome = true;
            }
        }
        if (hvo.getAutoAnaly() != null && hvo.getAutoAnaly().booleanValue()) {
            if(jflag > 1 && dflag > 1){//多借多贷提示语修改,原因为多借多贷现金流量自动分析不准确
                msg.append("现金流量已自动分析,多借多贷凭证请手工确认<br>");
            }else{
                msg.append("保存凭证成功,现金流量已自动分析<br>");
            }
        }
        if (hasIncome) {
            msg.append("销售收入数据已变更，请重新进行成本结转<br>");
        }
        if (msg.length() == 0) {
            msg.append("保存凭证成功");

        }
        return msg.toString();
    }
    private void checkIsCreated(TzpzHVO headvo){
        if(!StringUtil.isEmpty(headvo.getPk_tzpz_h())){//如是修改则跳过

        }else{
            ImageGroupVO groupVO = (ImageGroupVO) gl_tzpzserv.queryImageGroupByPrimaryKey(headvo.getPk_image_group());
            if(groupVO == null)
                return;
            if(DZFBoolean.TRUE == groupVO.getIsuer() && PhotoState.state101 != groupVO.getIstate()){
                throw new BusinessException("图片组"+ groupVO.getGroupcode() + "已直接生单,不能再次生单!");
            }
        }
    }
    //总账存货 保存提示性校验
    private void checkInventorySet(TzpzHVO headvo, CorpVO cpvo) throws BusinessException{
        if(headvo == null
                || (headvo.getIsglicsave() != null
                && headvo.getIsglicsave().booleanValue()))
            return;
        String error = inventory_setcheck.checkInventorySetByPZ(SystemUtil.getLoginUserId(), cpvo, headvo);
        if (!StringUtil.isEmpty(error)) {
            throw new BusinessException(IVoucherConstants.EXE_INVGL_CODE+error);
        }
    }

    public void writeLogRecord(Integer opename,String msg,Integer ident,String pk_corp){
        try {
            String norecord = null;

            sys_ope_log2.saveLog(pk_corp, null, null, opename, msg,ident,SystemUtil.getLoginUserId());
        } catch (Exception e) {
//            throw e;
        }
    }


}
