package com.dzf.zxkj.platform.controller.zncs;

import cn.jiguang.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.cloud.redis.lock.RedissonDistributedLock;
import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.IBillManageConstants;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.pjgl.VatInvoiceSetVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.*;
import com.dzf.zxkj.platform.service.bdset.IYHZHService;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.zncs.*;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.dzf.zxkj.platform.util.zncs.OcrUtil;
import com.dzf.zxkj.platform.util.zncs.VatExportUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/zncs/gl_yhdzdact2")
public class BankStatement2Controller extends BaseController {

    // 获取前台传来的文件
//	public static final String[] arr = {"commonImpFile", "bankImpFile", "bankImpFile", "bankImpFile",
//			"bankImpFile", "bankImpFile", "bankImpFile", "bankImpFile", "bankImpFile",
//			"bankImpFile", "bankImpFile", "bankImpFile", "bankImpFile" };
    private static final String[] arr = {"commonImpFile", "bankImpFile"};

    @Autowired
    private IBankStatement2Service gl_yhdzdserv2;
    @Autowired
    private IVatInvoiceService vatinvoiceserv;
    @Autowired
    private IYHZHService gl_yhzhserv;
    @Autowired
    private ISchedulCategoryService schedulCategoryService;
    @Autowired
    private IZncsVoucher zncsVoucher;
    @Autowired
    private IVATInComInvoice2Service gl_vatincinvact2;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private ICorpService corpService;
    @Autowired
    private RedissonDistributedLock redissonDistributedLock;
//	@Autowired
//	private IParameterSetService parameterserv;

    @PostMapping("/queryInfo")
    public ReturnData<Json> queryInfo(@RequestBody BankStatementVO2 bvo){
//		Grid grid = new Grid();
        Json json = new Json();
        try {
            //查询并分页
            if(StringUtil.isEmpty(SystemUtil.getLoginCorpId())){//corpVo.getPrimaryKey()
                throw new BusinessException("出现数据无权问题！");
            }
            List<BankStatementVO2> list = gl_yhdzdserv2.quyerByPkcorp(SystemUtil.getLoginCorpId(),
                    bvo == null ? new BankStatementVO2() : bvo, bvo.getSort(), bvo.getOrder());
            //list变成数组
            json.setTotal((long) (list==null ? 0 : list.size()));
//			grid.setTotal((long) (list==null ? 0 : list.size()));
            //分页
            BankStatementVO2[] vos = null;
            if(list!=null && list.size()>0){
                vos = getPagedZZVOs(list.toArray(new BankStatementVO2[0]), bvo.getPage(), bvo.getRows());
            }


            vos = filterByBillStatus(vos, bvo.getFlag());
            if (vos != null && vos.length > 0)
            {
                for (BankStatementVO2 vo : vos)
                {
                    //未制证，有图片来源，有业务类型，则不显示业务类型，通过票据工作台处理
                    if (StringUtil.isEmpty(vo.getPk_tzpz_h()) && !StringUtil.isEmpty(vo.getImgpath()) && (!StringUtil.isEmpty(vo.getPk_model_h()) || !StringUtil.isEmpty(vo.getBusitypetempname())))
                    {
                        vo.setPk_model_h(null);
                        vo.setBusitypetempname(null);
                    }
                    //处理改版前的图片路径，将/gl/gl_imgview!search.action替换成/zncs/gl_imgview/search
                    if(!StringUtil.isEmpty(vo.getImgpath())&&vo.getImgpath().contains("/gl/gl_imgview!search.action")){
                        vo.setImgpath(vo.getImgpath().replace("/gl/gl_imgview!search.action","/zncs/gl_imgview/search"));
                    }
                }
            }
            log.info("查询成功！");
//			grid.setRows(vos==null?new ArrayList<BankStatementVO2>():Arrays.asList(vos));
//			grid.setSuccess(true);
//			grid.setMsg("查询成功");
            json.setRows(vos==null?new ArrayList<BankStatementVO2>(): Arrays.asList(vos));
            json.setHead(getHeadVO(list));
            json.setSuccess(true);
            json.setMsg("查询成功");
        } catch (Exception e) {
//			printErrorLog(grid, log, e, "查询失败");
            log.error("银行对账单查询失败");
        }

       return ReturnData.ok().data(json);
    }

    private BankStatementVO2[] filterByBillStatus(BankStatementVO2[] vos,String flag){
//        String flag = getRequest().getParameter("flag");//2,1  2,0  0
        if(StringUtil.isEmpty(flag))
            return vos;

        if(vos == null
                || vos.length == 0)

            return vos;

        List<BankStatementVO2> list = new ArrayList<BankStatementVO2>();
        String[] arr = flag.split(",");
        int[] iarr = new int[arr.length];
        for(int i = 0; i < arr.length;i++){
            iarr[i] = Integer.parseInt(arr[i]);
        }

        for(BankStatementVO2 vo : vos){
            for(int i : iarr){
                if(vo.getBillstatus() == i){
                    list.add(vo);
                    break;
                }
            }
        }
        return list.toArray(new BankStatementVO2[0]);
    }

    private BankStatementVO2 getHeadVO(List<BankStatementVO2> list){
        int yhdzdsl = 0;
        int yhhdsl  = 0;
        int wsdhdsl = 0;

        if(list != null && list.size() > 0){
            for(BankStatementVO2 b : list){

                int status = b.getBillstatus();
                if(status == BankStatementVO2.STATUS_0){
                    wsdhdsl++;
                    yhdzdsl++;
                }else if(status == BankStatementVO2.STATUS_1){
                    yhhdsl++;
                }else if(status == BankStatementVO2.STATUS_2){
                    yhhdsl++;
                    yhdzdsl++;
                }
            }
        }
        BankStatementVO2 vo = new BankStatementVO2();
        vo.setYhdzdsl(yhdzdsl);
        vo.setYhhdsl(yhhdsl);
        vo.setWsdhdsl(wsdhdsl);
        return vo;
    }

    private BankStatementVO2[] getPagedZZVOs(BankStatementVO2[] vos, int page, int rows) {
        int beginIndex = rows * (page-1);
        int endIndex = rows * page;
        if(endIndex >= vos.length){//防止endIndex数组越界
            endIndex = vos.length;
        }
        vos = Arrays.copyOfRange(vos, beginIndex, endIndex);
        return vos;
    }

    //修改保存
    @RequestMapping("/onUpdate")
    public ReturnData<Json> onUpdate(@RequestBody Map<String,String> param){
        Json json = new Json();
//		String[] strArr = getRequest().getParameterValues("strArr[]");
        try{
            BankStatementVO2 bvo = new BankStatementVO2();
            String bankaccid = param.get("bankaccid");
            bvo.setPk_bankaccount(bankaccid);
            String adddoc = param.get("adddoc");
            String deldoc = param.get("deldoc");
            String uptdoc = param.get("upddoc");
            String pk_corp = SystemUtil.getLoginCorpId();
            checkValidData(bvo);
            checkSecurityData(null, new String[]{pk_corp}, null);
            HashMap<String, BankStatementVO2[]> sendData = new HashMap<String, BankStatementVO2[]>();
            if(!StringUtil.isEmpty(adddoc)){
                adddoc = adddoc.replace("}{", "},{");
                adddoc = "[" + adddoc + "]";
                BankStatementVO2[] adddocvos =  JsonUtils.deserialize(adddoc,BankStatementVO2[].class);
                if (adddocvos != null && adddocvos.length > 0) {
                    for(BankStatementVO2 vo : adddocvos){
                        checkJEValid(vo);
                        setDefultValue(vo, bvo, pk_corp, false);
                    }
                    sendData.put("adddocvos", adddocvos);
                }
            }

            if(!StringUtil.isEmpty(deldoc)){
                deldoc = deldoc.replace("}{", "},{");
                deldoc = "[" + deldoc + "]";
                BankStatementVO2[] deldocvos =  JsonUtils.deserialize(deldoc,BankStatementVO2[].class);
                if (deldocvos != null && deldocvos.length > 0) {
                    sendData.put("deldocvos", deldocvos);
                }
            }

            if(!StringUtil.isEmpty(uptdoc)){
                uptdoc = uptdoc.replace("}{", "},{");
                uptdoc = "[" + uptdoc + "]";
                BankStatementVO2[] uptdocvos =  JsonUtils.deserialize(uptdoc,BankStatementVO2[].class);
                if (uptdocvos != null && uptdocvos.length > 0) {
                    StringBuffer checkKeys=new StringBuffer();
                    for(BankStatementVO2 vo : uptdocvos){
                        checkJEValid(vo);
                        setDefultValue(vo, bvo, pk_corp, true);
                        checkKeys.append(vo.getPk_bankstatement()+",");
                    }

                    gl_yhdzdserv2.checkvoPzMsgs(checkKeys.toString().substring(0, checkKeys.length()-1));
                    sendData.put("upddocvos", uptdocvos);
                }
            }

            BankStatement2ResponseVO responseVO = gl_yhdzdserv2.updateVOArr(pk_corp, sendData, null);

            json.setStatus(200);
            json.setRows(responseVO.getVos());
            json.setSuccess(true);
            json.setMsg(StringUtils.isEmpty(responseVO.getMsg())?"保存成功！":responseVO.getMsg());
        }catch(Exception e){
            printErrorLog(json,  e, "保存失败");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "银行对账单编辑", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }
    /**
     * 校验金额是否一致
     * @param vo
     */
    private void checkJEValid(BankStatementVO2 vo){
        if(!StringUtils.isEmpty(vo.getOthaccountname())){
            OcrUtil.filterCorpName(vo.getOthaccountname());
        }

        if(vo.getTradingdate() == null)
            throw new BusinessException("交易日期不允许为空，请检查");
        if(vo.getTradingdate().before(SystemUtil.getLoginCorpVo().getBegindate())){
            throw new BusinessException("交易日期不允许在建账日期前，请检查");
        }

        boolean flag = false;
        if(SafeCompute.add(DZFDouble.ZERO_DBL, vo.getSyje()).doubleValue() != 0)
            flag = !flag;

        if(SafeCompute.add(DZFDouble.ZERO_DBL, vo.getZcje()).doubleValue() != 0)
            flag = !flag;

        if(!flag)
            throw new BusinessException("收入金额或支出金额有且仅录入一项，请检查");
    }

    private void setDefultValue(BankStatementVO2 vo, BankStatementVO2 data, String pk_corp, boolean isUpdate){

//		vo.setPeriod(data.getPeriod());
        vo.setPeriod(DateUtils.getPeriod(vo.getTradingdate()));//取交易日期时间
        vo.setInperiod(vo.getPeriod());//入账期间
        if(isUpdate){
            vo.setModifydatetime(new DZFDateTime());
            vo.setModifyoperid(SystemUtil.getLoginUserId());
        }else{
            vo.setPk_corp(pk_corp);
            vo.setDoperatedate(new DZFDate());
            vo.setCoperatorid(SystemUtil.getLoginUserId());
            vo.setPk_bankaccount(data.getPk_bankaccount());
            vo.setSourcetype(vo.SOURCE_0);
        }

    }

    private void checkValidData(BankStatementVO2 vo){
        String error = null;
        if(vo == null){
            error = "信息不完整,请检查";
        }
//		else if(StringUtil.isEmpty(data.getPeriod())){
//			error = "期间信息不完整,请检查";
//		}
//		else if(StringUtil.isEmpty(data.getPk_bankaccount())){
//			error = "银行账户信息,请检查";
//		}

        if (!StringUtil.isEmpty(error)) {
            throw new BusinessException(error);
        }

    }

    //删除记录
    @RequestMapping("/onDelete")
    public ReturnData<Json> onDelete(@RequestBody Map<String,String> param){
        String body = param.get("head");
        Json json = new Json();
        StringBuffer strb = new StringBuffer();
        BankStatementVO2[] bodyvos = null;
        int errorCount = 0;
        boolean lock = false;
        String pk_corp = "";
        try{
            pk_corp = SystemUtil.getLoginCorpId();
            checkSecurityData(null, new String[]{pk_corp}, null);
            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("duizhangdandel"+pk_corp);
            if(!lock){//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍候刷新界面");

                return ReturnData.error().data(json);
            }
            if (body == null) {
                throw new BusinessException("数据为空,删除失败!!");
            }
            body = body.replace("}{", "},{");
            body = "[" + body + "]";
            bodyvos = JsonUtils.deserialize(body,BankStatementVO2[].class);

            if (bodyvos == null || bodyvos.length == 0) {
                throw new BusinessException("数据为空,删除失败!!");
            }

            for(BankStatementVO2 vo : bodyvos){
//				gl_yhdzdserv2.checkvoPzMsg(vo.getPk_bankstatement());
                try {
                    gl_yhdzdserv2.delete(vo, pk_corp);
                    strb.append("<font color='#2ab30f'><p>银行对账单[" + vo.getTradingdate() + "],删除成功!</p></font>");
                } catch (Exception e) {
                    log.error( "删除失败!",json, log, e);
                    errorCount++;
                    strb.append("<font color='red'><p>银行对账单[" + vo.getTradingdate() + "]," + json.getMsg() + "</p></font>");
                }
            }

        }catch(Exception e){
            log.error("删除失败", json, e);
            strb.append("删除失败");
        }finally {
            if(lock){
                redissonDistributedLock.releaseDistributedFairLock("duizhangdandel"+pk_corp);
            }
        }

        if(errorCount == 0){
            json.setSuccess(true);
            json.setMsg(strb.toString());
        }else{
            json.setSuccess(false);
            json.setMsg(strb.toString());
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "银行对账单编辑", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @RequestMapping("/impExcel")
    public ReturnData<Json> impExcel(MultipartFile file,BankStatementVO2 bvo,Integer sourcetem,String impForce){
        Json json = new Json();
        json.setSuccess(false);
        try {
            String pk_corp = SystemUtil.getLoginCorpId();
            checkSecurityData(null, new String[]{pk_corp}, null);
            if(bvo == null || sourcetem == 0)
                throw new BusinessException("未选择上传文档，请检查");
            bvo = getParamVO(bvo, sourcetem);

            DZFBoolean isFlag = "Y".equals(impForce) ? DZFBoolean.TRUE : DZFBoolean.FALSE;
//			String source = arr[data.getSourcetem() - 1];
            String source = sourcetem == 1 ? arr[0] : arr[1];
//            File[] infiles = ((MultiPartRequestWrapper) getRequest()).getFiles(source);
            if(file.isEmpty() || file.getSize() == 0){
                throw new BusinessException("请选择导入文件!");
            }
//            String[] fileNames = ((MultiPartRequestWrapper) getRequest()).getFileNames(source);
            String fileName = file.getOriginalFilename();
            String fileType = null;
            if (fileName != null && fileName.length() > 0) {

                fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
            }

            bvo.setIsFlag(isFlag);
            String msg = gl_yhdzdserv2.saveImp(file, bvo, fileType, sourcetem);

            json.setHead(bvo);
            json.setMsg(StringUtil.isEmpty(msg) ? "导入成功!" : msg);
            json.setSuccess(true);

            writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                    "导入银行对账单：" + (bvo != null && !StringUtil.isEmpty(bvo.getPeriod()) ? bvo.getPeriod() : ""), ISysConstants.SYS_2);
        } catch (Exception e) {
            if(e instanceof BusinessException
                    && IBillManageConstants.ERROR_FLAG.equals(((BusinessException) e).getErrorCodeString())){
                String msg = e.getMessage() + "<p>请确定是否要导入?</p>";
                json.setMsg(msg);
                json.setStatus(Integer.parseInt(IBillManageConstants.ERROR_FLAG));
                json.setSuccess(false);
            }else{
                log.error("导入失败!",json, log, e );
                json.setMsg(e.getMessage());
            }
        }

        return ReturnData.ok().data(json);
    }

    private BankStatementVO2 getParamVO(BankStatementVO2 vo, int sourceType){
        vo.setSourcetem(sourceType);
        vo.setCoperatorid(SystemUtil.getLoginUserId());
        vo.setPk_corp(SystemUtil.getLoginCorpId());

        return vo;
    }

    /**
     * 生成凭证
     */
    @RequestMapping("/createPZ")
    public ReturnData<Json> createPZ(String body){
        Json json = new Json();
        BankStatementVO2[] vos = null;
        boolean lock = false;
        String pk_corp =  SystemUtil.getLoginCorpId();
        checkSecurityData(null, new String[]{pk_corp}, null);
        try {
            lock = redissonDistributedLock.tryGetDistributedFairLock("yhdzd2createpz"+pk_corp);
            if(!lock){//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍候刷新界面");
                return ReturnData.error().data(json);
            }
            if (body == null) {
                throw new BusinessException("数据为空,请检查!");
            }
            body = body.replace("}{", "},{");
            body = "[" + body + "]";

            vos = JsonUtils.deserialize(body, BankStatementVO2[].class);
            if(vos == null || vos.length == 0)
                throw new BusinessException("数据为空,生成凭证失败，请检查");


            String userid  = SystemUtil.getLoginUserId();

            List<BankStatementVO2> storeList = gl_yhdzdserv2.construcBankStatement(vos, pk_corp);
            if(storeList == null || storeList.size() == 0){
                throw new BusinessException("查询银行对账数据失败，请检查");
            }
            //校验是否存在老数据
            StringBuffer checkKeys=new StringBuffer();
            for (BankStatementVO2 vo : storeList) {
                checkKeys.append(vo.getPk_model_h()+",");
            }
            List<DcModelHVO> list = gl_yhdzdserv2.queryIsDcModels(checkKeys.toString().substring(0,checkKeys.length()-1));
            if(list!=null&&list.size()>0){
                throw new BusinessException("请重新选择业务类型");
            }
            BankAccountVO bankAccVO = getBankAccountVO(storeList.get(0));
            boolean accway = getAccWay(pk_corp);
            VatInvoiceSetVO setvo = queryRuleByType();

            int errorCount = 0;
            StringBuffer msg = new StringBuffer();
            List<String> periodSet = new ArrayList<String>();
            String key = "";
            String period = "";
            CorpVO corpvo = corpService.queryByPk(pk_corp);
            Map<String,YntCpaccountVO> accountMap = accountService.queryMapByPk(corpvo.getPk_corp());
            YntCpaccountVO[] accVOs=accountService.queryByPk(corpvo.getPk_corp());
            Map<String, Object> paramMap=null;
            List<List<Object[]>> levelList=null;
            Map<String, Object[]> categoryMap =null;
            Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap=null;
            Set<String> zyFzhsList=null;
            Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap=null;
            InventorySetVO inventorySetVO=null;
            Map<String, InventoryAliasVO> fzhsBMMap=null;
            List<Object> paramList = null;
            Map<String, BdCurrencyVO> currMap=null;
            Map<String, Object[]> rateMap=null;
            Map<String, String> bankAccountMap=null;
            Map<String, AuxiliaryAccountBVO> assistMap=null;
            Map<String, List<AccsetVO>> accsetMap=null;
            Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map=null;
            Map<String, String> jituanSubMap=null;
            String tradeCode=null;
            String newrule = null;
            List<AuxiliaryAccountBVO> chFzhsBodyVOs=null;
            Map<String, Map<String, Object>> periodMap=new HashMap<String, Map<String, Object>>();
            for(BankStatementVO2 vo : storeList){
                try {
                    if(StringUtil.isEmpty(vo.getPk_tzpz_h())){
                        key  = buildkey(vo, setvo);
                        period = splitKey(key);

                        if(periodMap.containsKey(period)){
                            paramMap=periodMap.get(period);
                        }else{
                            paramMap=zncsVoucher.initVoucherParam(corpvo, period,true);
                            periodMap.put(period, paramMap);
                        }

                        levelList=(List<List<Object[]>>) paramMap.get("levelList");
                        categoryMap =(Map<String, Object[]>) paramMap.get("categoryMap");
                        fzhsHeadMap=(Map<Integer, AuxiliaryAccountHVO>) paramMap.get("fzhsHeadMap");
                        zyFzhsList=(Set<String>) paramMap.get("zyFzhsList");
                        fzhsBodyMap=(Map<String, List<AuxiliaryAccountBVO>>) paramMap.get("fzhsBodyMap");
                        inventorySetVO=(InventorySetVO) paramMap.get("inventorySetVO");
                        fzhsBMMap=(Map<String, InventoryAliasVO>) paramMap.get("fzhsBMMap");
                        paramList = (List<Object>) paramMap.get("paramList");
                        currMap=(Map<String, BdCurrencyVO>) paramMap.get("currMap");
                        rateMap=(Map<String, Object[]>) paramMap.get("rateMap");
                        bankAccountMap=(Map<String, String>) paramMap.get("bankAccountMap");
                        assistMap=(Map<String, AuxiliaryAccountBVO>) paramMap.get("assistMap");
                        accsetMap=(Map<String, List<AccsetVO>>) paramMap.get("accsetMap");
                        accsetKeywordBVO2Map=(Map<String, List<AccsetKeywordBVO2>>) paramMap.get("accsetKeywordBVO2Map");
                        jituanSubMap=(Map<String, String>) paramMap.get("jituanSubMap");
                        tradeCode=(String) paramMap.get("tradeCode");
                        newrule = (String) paramMap.get("newrule");
                        chFzhsBodyVOs=(List<AuxiliaryAccountBVO>) paramMap.get("chFzhsBodyVOs");

                        gl_yhdzdserv2.createPZ(vo, pk_corp, userid, period, bankAccVO, setvo, accway,false, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
                        msg.append("<font color='#2ab30f'>交易日期:").append(vo.getTradingdate()).append("的单据生成凭证成功。</font><br/>");

                        if(!StringUtil.isEmpty(period) && !periodSet.contains(period)){
                            periodSet.add(period);
                        }
                    }else{
                        errorCount++;
                        msg.append("<font color='red'>交易日期:").append(vo.getTradingdate()).append("的单据已生成凭证，无需再次生成凭证。</font><br/>");
                    }

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if(e instanceof BusinessException
                            && !StringUtil.isEmpty(e.getMessage())
                            && !e.getMessage().startsWith("银行对账单")
                            && !e.getMessage().startsWith("制单失败")){
                        try{

                            key  = buildkey(vo, setvo);
                            period = splitKey(key);
                            gl_yhdzdserv2.createPZ(vo, pk_corp, userid, period, bankAccVO, setvo, accway, true, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
                            msg.append("<font color='#2ab30f'>交易日期:").append(vo.getTradingdate()).append("的单据生成凭证成功。</font><br/>");
                            if(!StringUtil.isEmpty(period) && !periodSet.contains(period)){
                                periodSet.add(period);
                            }
                        }catch(Exception ex){
                            errorCount++;
                            if(ex instanceof BusinessException){
                                msg.append("<font color='red'>交易日期:")
                                        .append(vo.getTradingdate())
                                        .append("的单据生成凭证失败，原因:")
                                        .append(e.getMessage())
                                        .append("。</font><br/>");
                            }else{
                                msg.append("<font color='red'>交易日期:")
                                        .append(vo.getTradingdate())
                                        .append("的单据生成凭证失败")
                                        .append("。</font><br/>");
                            }

                        }

                    }else if(!StringUtil.isEmpty(e.getMessage())
                            && (e.getMessage().startsWith("银行对账单")
                            || e.getMessage().startsWith("制单失败"))){
                        errorCount++;
                        msg.append("<font color='red'>交易日期:")
                                .append(vo.getTradingdate())
                                .append("的单据生成凭证失败，原因:")
                                .append(e.getMessage())
                                .append("。</font><br/>");
                    }else{
                        errorCount++;
                        msg.append("<font color='red'>交易日期:")
                                .append(vo.getTradingdate())
                                .append("的单据生成凭证失败。</font><br/>");
                    }

                }
            }

            //应产品要求：判断所在期间期末结转情况
            StringBuffer headMsg = gl_yhdzdserv2.buildQmjzMsg(periodSet, pk_corp);
            if(headMsg != null && headMsg.length() > 0){
                headMsg.append(msg.toString());
                msg = headMsg;
            }

            json.setSuccess(errorCount > 0 ? false : true);
            json.setMsg(msg.toString());
        } catch (Exception e) {
            printErrorLog(json,  e, "生成凭证失败");
        }finally {
            if(lock){
                redissonDistributedLock.releaseDistributedFairLock("yhdzd2createpz"+pk_corp);
            }
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "银行对账单生成凭证", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private boolean getAccWay(String pk_corp){
//		String acc = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF012);//入账设置
//		boolean accway = StringUtil.isEmpty(acc) || "0".equals(acc)
//				? true : false;//按入账日期为true, 按期间为false

        return true;
    }

    private BankAccountVO getBankAccountVO(BankStatementVO2 vo){
        String pk_bankaccount = vo.getPk_bankaccount();

        BankAccountVO bankaccvo = null;
        if(!StringUtil.isEmpty(pk_bankaccount)){
            bankaccvo = gl_yhzhserv.queryById(pk_bankaccount);
        }

        return bankaccvo;
    }
    //zhaoning的老方法 已弃用  现已改成updateCategoryset
//    public void setBusiType(){
//        Json json = new Json();
//        json.setSuccess(false);
//
//        try {
//            String data = getRequest().getParameter("rows");
//            String busiid = getRequest().getParameter("busiid");
//            String businame = getRequest().getParameter("businame");
//
//            if(StringUtil.isEmptyWithTrim(data)
//                    || StringUtil.isEmptyWithTrim(busiid)
//                    || StringUtil.isEmptyWithTrim(businame)){
//                throw new BusinessException("传入后台参数为空，请检查");
//            }
//
//            JSONArray array = (JSONArray) JSON.parseArray(data);
//            Map<String, String> bodymapping = FieldMapping.getFieldMapping(new BankStatementVO2());
//            BankStatementVO2[] listvo = DzfTypeUtils.cast(array, bodymapping, BankStatementVO2[].class, JSONConvtoJAVA.getParserConfig());
//
//            if(listvo == null || listvo.length == 0)
//                throw new BusinessException("解析前台参数失败，请检查");
//
//            String msg = gl_yhdzdserv2.setBusiType(listvo, busiid, businame);
//
//            //重新查询
//            String[] pks = buildPks(listvo);
//            List<BankStatementVO2> newList = gl_yhdzdserv2.queryByPks(pks, getLogincorppk());
//
//            json.setRows(newList);
//            json.setMsg(msg);
//            json.setSuccess(true);
//        } catch (Exception e) {
//            printErrorLog(json, log, e, "更新业务类型失败");
//        }
//
////		writeLogRecord(LogRecordEnum.OPE_KJ_PJGL.getValue(),
////				"银行对账单更新业务类型", ISysConstants.SYS_2);
//        writeJson(json);
//    }

    @RequestMapping("/setBusiperiod")
    public ReturnData<Json> setBusiperiod(@RequestBody Map<String,String> param){
        Json json = new Json();
        json.setSuccess(false);

        try {
            CorpVO corpVO = SystemUtil.getLoginCorpVo();
            checkSecurityData(null, new String[]{corpVO.getPk_corp()}, null);
            String data = param.get("rows");
            String period = param.get("period");
            if(StringUtil.isEmptyWithTrim(data)
                    || StringUtil.isEmptyWithTrim(period)){
                throw new BusinessException("传入后台参数为空，请检查");
            }

            DZFDate date = DateUtils.getPeriodEndDate(period);
            if(date == null){
                throw new BusinessException("入账期间解析错误，请检查");
            }

            BankStatementVO2[] listvo = JsonUtils.deserialize(data,BankStatementVO2[].class);
            if(listvo == null || listvo.length == 0)
                throw new BusinessException("解析前台参数失败，请检查");
            //处理老数据中的业务类型
            for (BankStatementVO2 vo : listvo) {
//				gl_yhdzdserv2.checkvoPzMsg(vo.getPk_bankstatement());
                List<DcModelHVO> list = gl_yhdzdserv2.queryIsDcModel(vo.getPk_model_h());
                if(list!=null&&list.size()>0){
                    throw new BusinessException("不允许期间调整，请重新设置业务类型!");
                }
            }


            List<BillCategoryVO> list2 = schedulCategoryService.queryBillCategoryByCorpAndPeriod(corpVO.getPk_corp(), period);
            if (list2 == null || list2.size() == 0) {

                schedulCategoryService.newSaveCorpCategory(null, corpVO.getPk_corp(), period, corpVO);

            }

            String msg = gl_yhdzdserv2.saveBusiPeriod(listvo, corpVO.getPk_corp(), period);

            json.setRows(null);
            json.setMsg(msg);
            json.setSuccess(true);//(StringUtil.isEmpty(msg) || msg.contains("详细原因") ) ? false : true
        } catch (Exception e) {
            log.error("期间调整失败",json, log, e );
            json.setMsg(e.getMessage());
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "银行对账单期间调整", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private String[] buildPks(BankStatementVO2[] vos){
        String[] arr = new String[vos.length];

        for(int i = 0; i < vos.length; i++){
            arr[i] = vos[i].getPk_bankstatement();
        }

        return arr;
    }

    @RequestMapping("/checkBeforPZ")
    public ReturnData<Json> checkBeforPZ(@RequestBody Map<String,String> param){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        try {
            String str = param.get("row");
            str = "[" + str + "]";
            BankStatementVO2[] listvo = JsonUtils.deserialize(str, BankStatementVO2[].class);
            if(listvo == null || listvo.length == 0)
                throw new BusinessException("解析前台参数失败，请检查");

            StringBuffer checkKeys=new StringBuffer();
            for (BankStatementVO2 vo : listvo) {
//				gl_yhdzdserv2.checkvoPzMsg(vo.getPk_bankstatement());
                checkKeys.append(vo.getPk_bankstatement()+",");
            }
            gl_yhdzdserv2.checkvoPzMsgs(checkKeys.toString().substring(0, checkKeys.length()-1));
            gl_yhdzdserv2.checkBeforeCombine(listvo);
            TzpzHVO headVO = constructCheckTzpzHVo(pk_corp, listvo[0]);
            gl_yhdzdserv2.checkCreatePZ(pk_corp, headVO);

            json.setSuccess(true);
            json.setMsg("校验成功");
        } catch (Exception e) {
            printErrorLog(json,  e, "校验失败");
        }

        return ReturnData.ok().data(json);
    }

    /**
     * 构造生成凭证前校验需要的HVO
     * @param pk_corp
     * @param vo
     * @return
     */
    private TzpzHVO constructCheckTzpzHVo(String pk_corp, BankStatementVO2 vo){
        DZFDate lastDate = DateUtils.getPeriodEndDate(vo.getInperiod());//取入账日期所在期间的最后一天
        TzpzHVO hvo = new TzpzHVO();
        hvo.setPk_corp(pk_corp);
        hvo.setDoperatedate(lastDate);
        hvo.setPeriod(vo.getInperiod());

        return hvo;
    }

    @RequestMapping("/getTzpzHVOByID")
    public ReturnData<Json> getTzpzHVOByID(@RequestBody Map<String,String> param){
        Json json = new Json();
        try {
            String pk_corp = SystemUtil.getLoginCorpId();
            checkSecurityData(null, new String[]{pk_corp}, null);
            String str = param.get("row");
            if (str == null) {
                throw new BusinessException("数据为空,请检查!");
            }

            BankStatementVO2[] listvo = JsonUtils.deserialize(str,BankStatementVO2[].class);
            if(listvo == null || listvo.length == 0)
                throw new BusinessException("转化后数据为空,请检查!");

            BankAccountVO bankAccVO = getBankAccountVO(listvo[0]);
            boolean accway = getAccWay(pk_corp);
            VatInvoiceSetVO setvo = queryRuleByType();

            TzpzHVO hvo = gl_yhdzdserv2.getTzpzHVOByID(listvo, bankAccVO,
                    pk_corp, SystemUtil.getLoginUserId(), setvo, accway);
            json.setData(hvo);
            json.setSuccess(true);
            json.setMsg("凭证分录构造成功");
        } catch (Exception e) {
            printErrorLog(json,  e, "凭证分录构造失败");
        }

        return  ReturnData.ok().data(json);
    }

    @RequestMapping("/combinePZ_long")
    public ReturnData<Json> combinePZ_long(@RequestBody Map<String,String> param){
        VatInvoiceSetVO setvo = queryRuleByType();
        String body = param.get("head");
        ReturnData<Json> data=null;
        if(setvo == null
                || setvo.getValue() == null
                || setvo.getValue() == IBillManageConstants.HEBING_GZ_01){
            data = createPZ(body);
        }else{
            data = combinePZ1(setvo,body);
        }
        return data;
    }

    public ReturnData<Json> combinePZ1(VatInvoiceSetVO setvo,String body){
        Json json = new Json();
        BankStatementVO2[] vos = null;
        try {
            if (body == null) {
                throw new BusinessException("数据为空,合并生成凭证失败!");
            }
            body = body.replace("}{", "},{");
            body = "[" + body + "]";
            vos = JsonUtils.deserialize(body, BankStatementVO2[].class);
            if(vos == null || vos.length == 0)
                throw new BusinessException("数据为空，合并生成凭证失败，请检查");

            String pk_corp = SystemUtil.getLoginCorpId();
            String userid  = SystemUtil.getLoginUserId();
            checkSecurityData(null,new String[]{pk_corp}, userid);
            List<BankStatementVO2> storeList = gl_yhdzdserv2.construcBankStatement(vos, pk_corp);
            if(storeList == null || storeList.size() == 0){
                throw new BusinessException("合并制证：查询银行对账数据失败，请检查");
            }
            StringBuffer checkKeys=new StringBuffer();
            for (BankStatementVO2 vo : storeList) {
                checkKeys.append(vo.getPk_model_h()+",");
            }
            List<DcModelHVO> list = gl_yhdzdserv2.queryIsDcModels(checkKeys.toString().substring(0,checkKeys.length()-1));
            if(list!=null&&list.size()>0){
                throw new BusinessException("请重新选择业务类型");
            }
            BankAccountVO bankAccVO = getBankAccountVO(storeList.get(0));
            boolean accway = getAccWay(pk_corp);
            Map<String, List<BankStatementVO2>> combineMap = new LinkedHashMap<String, List<BankStatementVO2>>();
            StringBuffer msg = new StringBuffer();
            //DcModelHVO modelHVO = null;
            String key;
            String period = "";
            int errorCount = 0;
            String pk_model_h;
            List<BankStatementVO2> combineList = null;
            List<String> periodSet = new ArrayList<String>();
            for(BankStatementVO2 vo : storeList){

                pk_model_h = vo.getPk_model_h();
                if(StringUtil.isEmpty(pk_model_h)){
                    errorCount++;
                    msg.append("<font color='red'><p>交易日期:" + vo.getTradingdate() + "的单据无业务类型，不能生成凭证。</p></font>");
                    continue;
                }

                //modelHVO = dcmap.get(pk_model_h);
                if(StringUtils.isEmpty(pk_model_h)){
                    errorCount++;
                    msg.append("<font color='red'><p>交易日期:" + vo.getTradingdate() + "单据业务类型为空，请检查。</p></font>");
                    continue;
                }

                key = buildkey(vo, setvo);
                if(!StringUtil.isEmpty(vo.getPk_tzpz_h())){
                    errorCount++;
                    msg.append("<font color='red'><p>交易日期:")
                            .append(vo.getTradingdate())
                            .append("的单据已生成凭证，无需再次生成凭证。</p></font>");
                }else if(combineMap.containsKey(key)){
                    combineList = combineMap.get(key);

                    combineList.add(vo);
                }else{
                    combineList = new ArrayList<BankStatementVO2>();
                    combineList.add(vo);
                    combineMap.put(key, combineList);
                }
            }
            CorpVO corpvo=corpService.queryByPk(pk_corp);
            Map<String,YntCpaccountVO> accountMap = accountService.queryMapByPk(corpvo.getPk_corp());
            YntCpaccountVO[] accVOs=accountService.queryByPk(corpvo.getPk_corp());
            Map<String, Object> paramMap=null;
            List<List<Object[]>> levelList=null;
            Map<String, Object[]> categoryMap =null;
            Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap=null;
            Set<String> zyFzhsList=null;
            Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap=null;
            InventorySetVO inventorySetVO=null;
            Map<String, InventoryAliasVO> fzhsBMMap=null;
            List<Object> paramList = null;
            Map<String, BdCurrencyVO> currMap=null;
            Map<String, Object[]> rateMap=null;
            Map<String, String> bankAccountMap=null;
            Map<String, AuxiliaryAccountBVO> assistMap=null;
            Map<String, List<AccsetVO>> accsetMap=null;
            Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map=null;
            Map<String, String> jituanSubMap=null;
            String tradeCode=null;
            String newrule = null;
            List<AuxiliaryAccountBVO> chFzhsBodyVOs=null;
            Map<String, Map<String, Object>> periodMap=new HashMap<String, Map<String, Object>>();
            for(Map.Entry<String, List<BankStatementVO2>> entry : combineMap.entrySet()){
                try {
                    combineList = entry.getValue();

                    key = entry.getKey();
                    period = splitKey(key);

                    if(periodMap.containsKey(period)){
                        paramMap=periodMap.get(period);
                    }else{
                        paramMap=zncsVoucher.initVoucherParam(corpvo, period,true);
                        periodMap.put(period, paramMap);
                    }
                    levelList=(List<List<Object[]>>) paramMap.get("levelList");
                    categoryMap =(Map<String, Object[]>) paramMap.get("categoryMap");
                    fzhsHeadMap=(Map<Integer, AuxiliaryAccountHVO>) paramMap.get("fzhsHeadMap");
                    zyFzhsList=(Set<String>) paramMap.get("zyFzhsList");
                    fzhsBodyMap=(Map<String, List<AuxiliaryAccountBVO>>) paramMap.get("fzhsBodyMap");
                    inventorySetVO=(InventorySetVO) paramMap.get("inventorySetVO");
                    fzhsBMMap=(Map<String, InventoryAliasVO>) paramMap.get("fzhsBMMap");
                    paramList = (List<Object>) paramMap.get("paramList");
                    currMap=(Map<String, BdCurrencyVO>) paramMap.get("currMap");
                    rateMap=(Map<String, Object[]>) paramMap.get("rateMap");
                    bankAccountMap=(Map<String, String>) paramMap.get("bankAccountMap");
                    assistMap=(Map<String, AuxiliaryAccountBVO>) paramMap.get("assistMap");
                    accsetMap=(Map<String, List<AccsetVO>>) paramMap.get("accsetMap");
                    accsetKeywordBVO2Map=(Map<String, List<AccsetKeywordBVO2>>) paramMap.get("accsetKeywordBVO2Map");
                    jituanSubMap=(Map<String, String>) paramMap.get("jituanSubMap");
                    tradeCode=(String) paramMap.get("tradeCode");
                    newrule = (String) paramMap.get("newrule");
                    chFzhsBodyVOs=(List<AuxiliaryAccountBVO>) paramMap.get("chFzhsBodyVOs");

                    gl_yhdzdserv2.saveCombinePZ(combineList, pk_corp, userid, period, bankAccVO, setvo, accway, false, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
                    msg.append("<font color='#2ab30f'><p>入账期间为" + period + "的单据生成凭证成功。</p></font>");

                    if(!StringUtil.isEmpty(period) && !periodSet.contains(period)){
                        periodSet.add(period);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if(e instanceof BusinessException
                            && !StringUtil.isEmpty(e.getMessage())
                            && !e.getMessage().startsWith("银行对账单")
                            && !e.getMessage().startsWith("制单失败")){
                        try {

                            key = entry.getKey();
                            period = splitKey(key);

                            gl_yhdzdserv2.saveCombinePZ(combineList, pk_corp, userid, period, bankAccVO, setvo, accway, true, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
                            msg.append("<font color='#2ab30f'><p>入账期间为" + period + "的单据生成凭证成功。</p></font>");

                            if(!StringUtil.isEmpty(period) && !periodSet.contains(period)){
                                periodSet.add(period);
                            }
                        } catch (Exception ex) {
                            errorCount++;
                            if(ex instanceof BusinessException){
                                msg.append("<font color='red'><p>入账期间为")
                                        .append(period)
                                        .append("的单据生成凭证失败，原因：")
                                        .append(ex.getMessage())
                                        .append("。</p></font>");
                            }else{
                                msg.append("<font color='red'><p>入账期间为")
                                        .append(period)
                                        .append("的单据生成凭证失败。</p></font>");
                            }
                        }
                    }else if(!StringUtil.isEmpty(e.getMessage())
                            && (e.getMessage().startsWith("银行对账单")
                            || e.getMessage().startsWith("制单失败"))){
                        errorCount++;
                        msg.append("<font color='red'><p>入账期间为")
                                .append(period)
                                .append("的单据生成凭证失败，原因：")
                                .append(e.getMessage())
                                .append("。</p></font>");
                    }else{
                        errorCount++;
                        msg.append("<font color='red'><p>入账期间为")
                                .append(period)
                                .append("的单据生成凭证失败。</p></font>");
                    }
                }
            }

            //应产品要求：判断所在期间期末结转情况
            StringBuffer headMsg = gl_yhdzdserv2.buildQmjzMsg(periodSet, pk_corp);
            if(headMsg != null && headMsg.length() > 0){
                headMsg.append(msg.toString());
                msg = headMsg;
            }

            json.setSuccess(errorCount > 0 ? false : true);
            json.setMsg(msg.toString());
        } catch (Exception e) {
            printErrorLog(json, e, "生成凭证失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "银行对账单合并凭证", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private String splitKey(String key){
        if(!StringUtil.isEmpty(key)){
            return key.substring(0, 7);
        }
        return "";
    }

//	private String[] splitkeys(String allkey){
//		String[] keys = StringUtil.isEmpty(allkey) ? null : allkey.split("_");
//
//		return keys;
//	}

    private String buildkey(BankStatementVO2 vo, VatInvoiceSetVO setvo){
        String key = null;
        if(!StringUtil.isEmpty(vo.getInperiod())){
            key = vo.getInperiod();
        }else if(StringUtil.isEmpty(vo.getPeriod())){
            key = DateUtils.getPeriod(vo.getTradingdate());
        }else{
            key = vo.getPeriod();
        }

        if(setvo != null
                && setvo.getValue() == IBillManageConstants.HEBING_GZ_02){//按往来单位合并
            String name = vo.getOthaccountname();
            if(!StringUtil.isEmpty(name)){
                key += name;
            }else{
                key += vo.getPk_model_h();
            }
        }

        return key;
    }

//	private String buildkey(BankStatementVO2 vo, Integer itype){
//		String key = null;
//
//		String jeFlag = null;
//		DZFDouble sy = vo.getSyje();
//		DZFDouble zc = vo.getZcje();
//
//		if(sy != null && sy.doubleValue() != 0 && zc != null && zc.doubleValue() != 0){
//			jeFlag = "ALL";
//		}else if(sy != null && sy.doubleValue() != 0){
//			jeFlag = "SY";
//		}else if(zc != null && zc.doubleValue() != 0){
//			jeFlag = "ZC";
//		}else{
//			jeFlag = "NO";
//		}
//
//		if(itype == IBillManageConstants.HEBING_GZ_01){//相同往来单位合并一张
//
//			String period = DateUtils.getPeriod(vo.getTradingdate());
//
//			key = appendkey(new String[]{
//				vo.getPk_model_h(),
//				period,
//				vo.getOthaccountname(),
//				jeFlag
//			});
//		}else if(itype == IBillManageConstants.HEBING_GZ_02){
//
//			key = appendkey(new String[]{
//					vo.getPk_model_h(),
//					vo.getTradingdate().toString(),
//					jeFlag
//			});
//		}else if(itype == IBillManageConstants.HEBING_GZ_03){
//
//			key = appendkey(new String[]{
//					vo.getPk_model_h(),
//					vo.getTradingdate().toString(),
//					vo.getOthaccountname(),
//					jeFlag
//			});
//		}else{
//
//			String period = DateUtils.getPeriod(vo.getTradingdate());
//
//			key = appendkey(new String[]{
//					vo.getPk_model_h(),
//					period,
//					jeFlag
//			});
//		}
//
//		return key;
//	}
//
//	private String appendkey(String[] params){
//		StringBuffer sf = new StringBuffer();
//
//		for(int i = 0; i < params.length; i++){
//			sf.append(params[i]);
//
//			if(i != params.length - 1){
//				sf.append("_");
//			}
//		}
//
//		return sf.toString();
//	}

    private VatInvoiceSetVO queryRuleByType(){

        VatInvoiceSetVO[] setvos = vatinvoiceserv.queryByType(SystemUtil.getLoginCorpId(),
                IBillManageConstants.HEBING_YHDZF);

        if(setvos != null && setvos.length > 0){
            return setvos[0];
        }

        VatInvoiceSetVO vo = new VatInvoiceSetVO();
        vo.setValue(IBillManageConstants.HEBING_GZ_01);
        vo.setEntry_type(IBillManageConstants.HEBING_FL_02);
        return vo;
    }

    @RequestMapping("/expExcelData")
    public void expExcelData(@RequestBody Map<String,String> param, HttpServletResponse response ){

        OutputStream toClient = null;
        try {
            String strrows = param.get("daterows");
            JSONArray array = JSON.parseArray(strrows);
            response.reset();
            String exName = new String("银行对账单.xlsx");
            exName = new String(exName.getBytes("GB2312"), "ISO_8859_1");// 解决中文乱码问题
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(exName));
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            byte[] length = null;
            VatExportUtils exp = new VatExportUtils();
            Map<Integer, String> fieldColumn = getExpFieldMap();
            //List<String> busiList = gl_yhdzdserv2.getBusiTypes(getLogincorppk());
            ArrayList<String> busiList = new ArrayList<String>();
            length = exp.exportExcelForXlsx(fieldColumn, array, toClient,
                    "yinhangduizhangdan2.xlsx", 0, 1, 1, VatExportUtils.EXP_DZD,
                    true, 1, busiList , 0, null);
            String srt2 = new String(length, "UTF-8");
            response.addHeader("Content-Length", srt2);
            toClient.flush();
            response.getOutputStream().flush();
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
                if (response!=null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (Exception e) {
                log.error("excel导出错误", e);
            }
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "导出银行对账单" , ISysConstants.SYS_2);
    }

    /**
     * 导出映射字段
     * @return
     */
    private Map<Integer, String> getExpFieldMap(){
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(0, "jyrq");
        map.put(1, "zy");
        map.put(2, "yhsyje");
        map.put(3, "yhzcje");
        map.put(4, "busitempname");
        map.put(5, "dfzhmc");
        map.put(6, "dfzhbm");
        map.put(7, "yfye");

        return map;
    }

    @RequestMapping("/queryRule")
    public ReturnData<Json> queryRule(){
        Json json = new Json();
        try {
            VatInvoiceSetVO[] vos = vatinvoiceserv.queryByType(SystemUtil.getLoginCorpId(), IBillManageConstants.HEBING_YHDZF);

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

//		writeLogRecord(LogRecordEnum.OPE_KJ_PJGL.getValue(),
//				"银行对账单更新业务类型", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @RequestMapping("/combineRule")
    public ReturnData<Json> combineRule(@RequestBody Map<String,String> param){
        Json json = new Json();
        try {
            String pzrq = param.get("pzrq");
            String pzrule = param.get("pzrule");
            String flrule = param.get("flrule");
            String zy = param.get("zy");
            String setId = param.get("setid");
            String bk = param.get("bk");
        if(StringUtil.isEmpty(pzrule)
                    || StringUtil.isEmpty(flrule)|| StringUtil.isEmpty(pzrq)){
                throw new BusinessException("合并规则设置失败，请重试");
            }

            String pk_corp = SystemUtil.getLoginCorpId();
            checkSecurityData(null,new String[]{pk_corp},null);
            VatInvoiceSetVO vo = new VatInvoiceSetVO();
            String[] fields = new String[]{ "pzrq","value", "entry_type", "isbank", "zy" };
            if(!StringUtil.isEmpty(setId)){
                vo.setPrimaryKey(setId);
            }else{
                vo.setPk_corp(pk_corp);
                vo.setStyle(IBillManageConstants.HEBING_YHDZF);
            }
            vo.setPzrq(Integer.parseInt(pzrq));
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

    @RequestMapping("/queryCategoryRef")
    public ReturnData<Grid> queryCategoryRef(String period){
        Grid grid = new Grid();
        ArrayList<String> pk_categoryList = new ArrayList<String>();
        try {

            try {
                DZFDate date = DateUtils.getPeriodEndDate(period);
                if(date == null){
                    throw new BusinessException("入账期间解析错误，请检查");
                }
            }
            catch (Exception ex)
            {
                throw new BusinessException("入账期间解析错误，请检查");
            }
            CorpVO corpVO = SystemUtil.getLoginCorpVo();
            List<BillCategoryVO> list2 = schedulCategoryService.queryBillCategoryByCorpAndPeriod(corpVO.getPk_corp(), period);
            if (list2 == null || list2.size() == 0) {

                schedulCategoryService.newSaveCorpCategory(null, corpVO.getPk_corp(), period, corpVO);

            }
            //银行参照
            List<BillCategoryVO> list = gl_yhdzdserv2.queryBankCategoryRef(corpVO.getPk_corp(),period);
            for (BillCategoryVO billCategoryVO : list) {
                pk_categoryList.add(billCategoryVO.getPk_category());
            }
            //查询全名称
            Map<String, String> map = zncsVoucher.queryCategoryFullName(pk_categoryList, period, corpVO.getPk_corp());
            for (BillCategoryVO billCategoryVO : list) {
                billCategoryVO.setCategoryname(map.get(billCategoryVO.getPk_category()));
            }
            log.info("查询成功！");
            grid.setRows(list==null?new ArrayList<BillCategoryVO>():list);
            grid.setSuccess(true);
            grid.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
        }

        return ReturnData.ok().data(grid);
    }

    @RequestMapping("/queryCategoryset")
    public ReturnData<Grid> queryCategoryset( Map<String,String> param){
        Grid grid = new Grid();
        try {
            String id = param.get("id");
            String period = param.get("period");
            ArrayList<String> pk_categoryList = new ArrayList<String>();

            List<CategorysetVO> list = gl_vatincinvact2.queryIncomeCategorySet(id,SystemUtil.getLoginCorpId());
            for (CategorysetVO vo : list) {
                pk_categoryList.add(vo.getPk_category());
            }
            //查询全名称
            Map<String, String> map = zncsVoucher.queryCategoryFullName(pk_categoryList, period, SystemUtil.getLoginCorpId());
            for (CategorysetVO vo : list) {
                vo.setCategoryname(map.get(vo.getPk_category()));
            }
            log.info("查询成功！");
            grid.setRows(list==null?new ArrayList<BillCategoryVO>():list);
            grid.setSuccess(true);
            grid.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
        }

        return ReturnData.ok().data(grid);
    }
    /*
     * 入账设置
     */
    @RequestMapping("/updateCategoryset")
    public ReturnData<Grid> updateCategoryset(String pk_model_h,String id,String busitypetempname,String pk_basecategory,String zdyzy,String rzkm ){
        Grid grid = new Grid();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            List<BankStatementVO2> list=gl_yhdzdserv2.updateVO(id , pk_model_h,busitypetempname,pk_corp,rzkm,pk_basecategory,zdyzy);

            log.info("设置成功！");
            grid.setRows(list);
            grid.setSuccess(true);
            grid.setMsg("设置成功!");
        } catch (Exception e) {
            printErrorLog(grid, e, "设置失败！");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "银行对账单更新业务类型", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }


}
