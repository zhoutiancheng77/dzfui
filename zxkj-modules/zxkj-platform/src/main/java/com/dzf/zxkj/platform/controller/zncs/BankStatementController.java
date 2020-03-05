package com.dzf.zxkj.platform.controller.zncs;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSON;
import com.dzf.cloud.redis.lock.RedissonDistributedLock;
import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.IBillManageConstants;
import com.dzf.zxkj.common.constant.ISysConstants;
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
import com.dzf.zxkj.platform.model.bdset.BankAccountVO;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.pjgl.BankStatementVO;
import com.dzf.zxkj.platform.model.pjgl.VatInvoiceSetVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.service.bdset.IYHZHService;
import com.dzf.zxkj.platform.service.zncs.IBankStatementService;
import com.dzf.zxkj.platform.service.zncs.IVatInvoiceService;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.dzf.zxkj.platform.util.zncs.VatExportUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.*;

@RestController
@RequestMapping("/zncs/gl_yhdzdact")
public class BankStatementController extends BaseController {


    private Logger log = Logger.getLogger(this.getClass());
    // 获取前台传来的文件
//	public static final String[] arr = {"commonImpFile", "bankImpFile", "bankImpFile", "bankImpFile",
//			"bankImpFile", "bankImpFile", "bankImpFile", "bankImpFile", "bankImpFile",
//			"bankImpFile", "bankImpFile", "bankImpFile", "bankImpFile" };
    private static final String[] arr = {"commonImpFile", "bankImpFile"};

    @Autowired
    private IBankStatementService gl_yhdzdserv;
    @Autowired
    private IVatInvoiceService vatinvoiceserv;
    @Autowired
    private IYHZHService gl_yhzhserv;
    @Autowired
    private RedissonDistributedLock redissonDistributedLock;

    @RequestMapping("/queryInfo")
    public ReturnData<Json> queryInfo(@RequestBody BankStatementVO bvo){
//		Grid grid = new Grid();
        Json json = new Json();
        try {
            //查询并分页
            if(StringUtil.isEmpty(SystemUtil.getLoginCorpId())){//corpVo.getPrimaryKey()
                throw new BusinessException("出现数据无权问题！");
            }
            List<BankStatementVO> list = gl_yhdzdserv.quyerByPkcorp(SystemUtil.getLoginCorpId(),
                    bvo == null ? new BankStatementVO() : bvo, bvo.getSort(), bvo.getOrder());
            //list变成数组
            json.setTotal((long) (list==null ? 0 : list.size()));
//			grid.setTotal((long) (list==null ? 0 : list.size()));
            //分页
            BankStatementVO[] vos = null;
            if(list!=null && list.size()>0){
                vos = getPagedZZVOs(list.toArray(new BankStatementVO[0]),bvo.getPage(),bvo.getRows());
                for (BankStatementVO vo: vos) {
                    //处理改版前的图片路径，将/gl/gl_imgview!search.action替换成/zncs/gl_imgview/search
                    if(!StringUtil.isEmpty(vo.getImgpath())&&vo.getImgpath().contains("/gl/gl_imgview!search.action")){
                        vo.setImgpath(vo.getImgpath().replace("/gl/gl_imgview!search.action","/zncs/gl_imgview/search"));
                    }
                }
            }

            vos = filterByBillStatus(vos,bvo.getFlag());

            log.info("查询成功！");
//			grid.setRows(vos==null?new ArrayList<BankStatementVO>():Arrays.asList(vos));
//			grid.setSuccess(true);
//			grid.setMsg("查询成功");
            json.setRows(vos==null?new ArrayList<BankStatementVO>(): Arrays.asList(vos));
            json.setHead(getHeadVO(list));
            json.setSuccess(true);
            json.setMsg("查询成功");
        } catch (Exception e) {
//			printErrorLog(grid, log, e, "查询失败");
            printErrorLog(json, e, "查询失败");
        }

       return ReturnData.ok().data(json);
    }

    private BankStatementVO[] filterByBillStatus(BankStatementVO[] vos,String flag){

        if(StringUtil.isEmpty(flag))
            return vos;

        if(vos == null
                || vos.length == 0)

            return vos;

        List<BankStatementVO> list = new ArrayList<BankStatementVO>();
        String[] arr = flag.split(",");
        int[] iarr = new int[arr.length];
        for(int i = 0; i < arr.length;i++){
            iarr[i] = Integer.parseInt(arr[i]);
        }

        for(BankStatementVO vo : vos){
            //处理改版前的图片路径，将/gl/gl_imgview!search.action替换成/zncs/gl_imgview/search
            if(!StringUtil.isEmpty(vo.getImgpath())&&vo.getImgpath().contains("/gl/gl_imgview!search.action")){
                vo.setImgpath(vo.getImgpath().replace("/gl/gl_imgview!search.action","/zncs/gl_imgview/search"));
            }
            for(int i : iarr){
                if(vo.getBillstatus() == i){
                    list.add(vo);
                    break;
                }
            }

        }
        return list.toArray(new BankStatementVO[0]);
    }

    private BankStatementVO getHeadVO(List<BankStatementVO> list){
        int yhdzdsl = 0;
        int yhhdsl  = 0;
        int wsdhdsl = 0;

        if(list != null && list.size() > 0){
            for(BankStatementVO b : list){

                int status = b.getBillstatus();
                if(status == BankStatementVO.STATUS_0){
                    wsdhdsl++;
                    yhdzdsl++;
                }else if(status == BankStatementVO.STATUS_1){
                    yhhdsl++;
                }else if(status == BankStatementVO.STATUS_2){
                    yhhdsl++;
                    yhdzdsl++;
                }
            }
        }
        BankStatementVO vo = new BankStatementVO();
        vo.setYhdzdsl(yhdzdsl);
        vo.setYhhdsl(yhhdsl);
        vo.setWsdhdsl(wsdhdsl);
        return vo;
    }

    private BankStatementVO[] getPagedZZVOs(BankStatementVO[] vos, int page, int rows) {
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
        BankStatementVO bvo = new BankStatementVO();
        String bankaccid = param.get("bankaccid");
        bvo.setPk_bankaccount(bankaccid);
        String adddoc = param.get("adddoc");
        String deldoc = param.get("deldoc");
        String uptdoc = param.get("upddoc");
        try{
            String pk_corp = SystemUtil.getLoginCorpId();
            checkSecurityData(null,new String[]{pk_corp}, null);
            checkValidData(bvo);

            HashMap<String, BankStatementVO[]> sendData = new HashMap<String, BankStatementVO[]>();
            if(!StringUtil.isEmpty(adddoc)){
                adddoc = adddoc.replace("}{", "},{");
                adddoc = "[" + adddoc + "]";
                BankStatementVO[] adddocvos =  JsonUtils.deserialize(adddoc,BankStatementVO[].class);

                if (adddocvos != null && adddocvos.length > 0) {
                    for(BankStatementVO vo : adddocvos){
                        checkJEValid(vo);
                        setDefultValue(vo, bvo, pk_corp, false);
                    }
                    sendData.put("adddocvos", adddocvos);
                }
            }

            if(!StringUtil.isEmpty(deldoc)){
                deldoc = deldoc.replace("}{", "},{");
                deldoc = "[" + deldoc + "]";
                BankStatementVO[] deldocvos =  JsonUtils.deserialize(deldoc,BankStatementVO[].class);
                if (deldocvos != null && deldocvos.length > 0) {
                    sendData.put("deldocvos", deldocvos);
                }
            }

            if(!StringUtil.isEmpty(uptdoc)){
                uptdoc = uptdoc.replace("}{", "},{");
                uptdoc = "[" + uptdoc + "]";
                BankStatementVO[] uptdocvos =  JsonUtils.deserialize(uptdoc,BankStatementVO[].class);
                if (uptdocvos != null && uptdocvos.length > 0) {
                    for(BankStatementVO vo : uptdocvos){
                        checkJEValid(vo);
                        setDefultValue(vo, bvo, pk_corp, true);
                    }
                    sendData.put("upddocvos", uptdocvos);
                }
            }

            BankStatementVO[] addvos = gl_yhdzdserv.updateVOArr(pk_corp, sendData, null);

            json.setStatus(200);
            json.setRows(addvos);
            json.setSuccess(true);
            json.setMsg("保存成功！");
        }catch(Exception e){
            printErrorLog(json, e, "保存失败");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, StringUtil.isEmpty(adddoc)?"银行对账单修改":"银行对账单新增", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }
    /**
     * 校验金额是否一致
     * @param vo
     */
    private void checkJEValid(BankStatementVO vo){
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

    private void setDefultValue(BankStatementVO vo, BankStatementVO data, String pk_corp, boolean isUpdate){

//		vo.setPeriod(data.getPeriod());
        vo.setPeriod(DateUtils.getPeriod(vo.getTradingdate()));//取交易日期时间
        vo.setInperiod(vo.getPeriod());//入账期间
        if(isUpdate){
            vo.setModifydatetime(new DZFDateTime());
            vo.setModifyoperid(SystemUtil.getLoginDate());
        }else{
            vo.setPk_corp(pk_corp);
            vo.setDoperatedate(new DZFDate());
            vo.setCoperatorid(SystemUtil.getLoginUserId());
            vo.setPk_bankaccount(data.getPk_bankaccount());
            vo.setSourcetype(vo.SOURCE_0);
        }

    }

    private void checkValidData(BankStatementVO vo){
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
        Json json = new Json();
        StringBuffer strb = new StringBuffer();
        BankStatementVO[] bodyvos = null;
        int errorCount = 0;
        boolean lock = false;
        String pk_corp = "";
        try{
            String body = param.get("head");
            pk_corp = SystemUtil.getLoginCorpId();
            checkSecurityData(null,new String[]{pk_corp}, null);
            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("duizhangdandel"+pk_corp);
            if(!lock){//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍候刷新界面");
                return ReturnData.ok().data(json);
            }
            if (body == null) {
                throw new BusinessException("数据为空,删除失败!!");
            }
            body = body.replace("}{", "},{");
            body = "[" + body + "]";
            bodyvos = JsonUtils.deserialize(body,BankStatementVO[].class);
            if (bodyvos == null || bodyvos.length == 0) {
                throw new BusinessException("数据为空,删除失败!!");
            }

            for(BankStatementVO vo : bodyvos){
                try {
                    gl_yhdzdserv.delete(vo, pk_corp);
                    strb.append("<font color='#2ab30f'><p>银行对账单[" + vo.getTradingdate() + "],删除成功!</p></font>");
                } catch (Exception e) {
                    printErrorLog(json, e, "删除失败!");
                    errorCount++;
                    strb.append("<font color='red'><p>银行对账单[" + vo.getTradingdate() + "]," + json.getMsg() + "</p></font>");
                }
            }

        }catch(Exception e){
            printErrorLog(json, e, "删除失败");
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
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "银行对账单删除", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @RequestMapping("/impExcel")
    public ReturnData<Json> impExcel(String impForce, MultipartFile file, BankStatementVO bvo, Integer sourcetem){
        Json json = new Json();
        json.setSuccess(false);
        try {
            String pk_corp = SystemUtil.getLoginCorpId();
            checkSecurityData(null,new String[]{pk_corp}, null);
            if(bvo == null || bvo.getSourcetem() == 0)
                throw new BusinessException("未选择上传文档，请检查");
            bvo = getParamVO(bvo, sourcetem);

            DZFBoolean isFlag = "Y".equals(impForce) ? DZFBoolean.TRUE : DZFBoolean.FALSE;
//			String source = arr[data.getSourcetem() - 1];
            String source = sourcetem == 1 ? arr[0] : arr[1];
            if(file == null || file.getSize()==0){
                throw new BusinessException("请选择导入文件!");
            }
            String fileName = file.getOriginalFilename();
            String fileType = null;
            if (!StringUtil.isEmpty(fileName)) {
                fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            }

            bvo.setIsFlag(isFlag);
            String msg = gl_yhdzdserv.saveImp(file, bvo, fileType, bvo.getSourcetem());

            json.setHead(bvo);
            json.setMsg(StringUtil.isEmpty(msg) ? "导入成功!" : msg);
            json.setSuccess(true);

            writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                    "导入银行对账单" +(bvo.getPeriod() != null ? "："+bvo.getPeriod() : ""), ISysConstants.SYS_2);
        } catch (Exception e) {
            if(e instanceof BusinessException
                    && IBillManageConstants.ERROR_FLAG.equals(((BusinessException) e).getErrorCodeString())){
                String msg = e.getMessage() + "<p>请确定是否要导入?</p>";
                json.setMsg(msg);
                json.setStatus(Integer.parseInt(IBillManageConstants.ERROR_FLAG));
                json.setSuccess(false);
            }else{
                printErrorLog(json, e, "导入失败!");
            }
        }

        return ReturnData.ok().data(json);
    }

    private BankStatementVO getParamVO(BankStatementVO vo, int sourceType){
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
        BankStatementVO[] vos = null;
        try {
            if (body == null) {
                throw new BusinessException("数据为空,请检查!");
            }
            body = body.replace("}{", "},{");
            body = "[" + body + "]";
            vos = JsonUtils.deserialize(body,BankStatementVO[].class);
            if(vos == null || vos.length == 0)
                throw new BusinessException("数据为空,生成凭证失败，请检查");

            String pk_corp = SystemUtil.getLoginCorpId();
            String userid  = SystemUtil.getLoginUserId();
            checkSecurityData(null,new String[]{pk_corp}, userid);
            Map<String, DcModelHVO> dcmap = gl_yhdzdserv.queryDcModelVO(pk_corp);
            List<BankStatementVO> storeList = gl_yhdzdserv.construcBankStatement(vos, pk_corp);
            if(storeList == null || storeList.size() == 0){
                throw new BusinessException("查询银行对账数据失败，请检查");
            }

            BankAccountVO bankAccVO = getBankAccountVO(storeList.get(0));
            boolean accway = getAccWay(pk_corp);
            VatInvoiceSetVO setvo = queryRuleByType();

            int errorCount = 0;
            StringBuffer msg = new StringBuffer();
            List<String> periodSet = new ArrayList<String>();
            String key = "";
            String period = "";
            for(BankStatementVO vo : storeList){
                try {
                    if(StringUtil.isEmpty(vo.getPk_tzpz_h())){
                        gl_yhdzdserv.createPZ(vo, pk_corp, userid, dcmap, bankAccVO, setvo, accway,false);
                        msg.append("<font color='#2ab30f'>交易日期:").append(vo.getTradingdate()).append("的单据生成凭证成功。</font><br/>");
                        key  = buildkey(vo, setvo);
                        period = splitKey(key);
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
                            gl_yhdzdserv.createPZ(vo, pk_corp, userid, dcmap, bankAccVO, setvo, accway, true);
                            msg.append("<font color='#2ab30f'>交易日期:").append(vo.getTradingdate()).append("的单据生成凭证成功。</font><br/>");
                            key  = buildkey(vo, setvo);
                            period = splitKey(key);
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
            StringBuffer headMsg = gl_yhdzdserv.buildQmjzMsg(periodSet, pk_corp);
            if(headMsg != null && headMsg.length() > 0){
                headMsg.append(msg.toString());
                msg = headMsg;
            }

            json.setSuccess(errorCount > 0 ? false : true);
            json.setMsg(msg.toString());
        } catch (Exception e) {
            printErrorLog(json,  e, "生成凭证失败");
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

    private BankAccountVO getBankAccountVO(BankStatementVO vo){
        String pk_bankaccount = vo.getPk_bankaccount();

        BankAccountVO bankaccvo = null;
        if(!StringUtil.isEmpty(pk_bankaccount)){
            bankaccvo = gl_yhzhserv.queryById(pk_bankaccount);
        }

        return bankaccvo;
    }
 @RequestMapping("/setBusiType")
    public ReturnData<Json> setBusiType(@RequestBody Map<String,String> param){
        Json json = new Json();
        json.setSuccess(false);

        try {
            String pk_corp = SystemUtil.getLoginCorpId();
            checkSecurityData(null,new String[]{pk_corp}, null);
            String data = param.get("rows");
            String busiid = param.get("busiid");
            String businame = param.get("businame");

            if(StringUtil.isEmptyWithTrim(data)
                    || StringUtil.isEmptyWithTrim(busiid)
                    || StringUtil.isEmptyWithTrim(businame)){
                throw new BusinessException("传入后台参数为空，请检查");
            }

            BankStatementVO[] listvo = JsonUtils.deserialize(data,BankStatementVO[].class);

            if(listvo == null || listvo.length == 0)
                throw new BusinessException("解析前台参数失败，请检查");

            String msg = gl_yhdzdserv.setBusiType(listvo, busiid, businame);

            //重新查询
            String[] pks = buildPks(listvo);
            List<BankStatementVO> newList = gl_yhdzdserv.queryByPks(pks, pk_corp);

            json.setRows(newList);
            json.setMsg(msg);
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "更新业务类型失败");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "银行对账单更新业务类型", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @RequestMapping("/setBusiperiod")
    public ReturnData<Json> setBusiperiod(@RequestBody Map<String,String> param){
        Json json = new Json();
        json.setSuccess(false);

        try {
            String pk_corp = SystemUtil.getLoginCorpId();
            checkSecurityData(null,new String[]{pk_corp}, null);
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


            BankStatementVO[] listvo = JsonUtils.deserialize(data, BankStatementVO[].class);

            if(listvo == null || listvo.length == 0)
                throw new BusinessException("解析前台参数失败，请检查");


            String msg = gl_yhdzdserv.saveBusiPeriod(listvo, pk_corp, period);

            json.setRows(null);
            json.setMsg(msg);
            json.setSuccess(true);//(StringUtil.isEmpty(msg) || msg.contains("详细原因") ) ? false : true
        } catch (Exception e) {
            printErrorLog(json, e, "期间调整失败");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "银行对账单期间调整", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private String[] buildPks(BankStatementVO[] vos){
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
            BankStatementVO[] listvo = JsonUtils.deserialize(str, BankStatementVO[].class);

            if(listvo == null || listvo.length == 0)
                throw new BusinessException("解析前台参数失败，请检查");

            gl_yhdzdserv.checkBeforeCombine(listvo);
            TzpzHVO headVO = constructCheckTzpzHVo(pk_corp, listvo[0]);
            gl_yhdzdserv.checkCreatePZ(pk_corp, headVO);

            json.setSuccess(true);
            json.setMsg("校验成功");
        } catch (Exception e) {
            printErrorLog(json, e, "校验失败");
        }

        return ReturnData.ok().data(json);
    }

    /**
     * 构造生成凭证前校验需要的HVO
     * @param pk_corp
     * @param vo
     * @return
     */
    private TzpzHVO constructCheckTzpzHVo(String pk_corp, BankStatementVO vo){
        DZFDate lastDate = DateUtils.getPeriodEndDate(
                DateUtils.getPeriod(vo.getTradingdate())
        );//取交易日期所在期间的最后一天

        TzpzHVO hvo = new TzpzHVO();
        hvo.setPk_corp(pk_corp);
        hvo.setDoperatedate(lastDate);
        hvo.setPeriod(vo.getPeriod());

        return hvo;
    }

    @RequestMapping("/getTzpzHVOByID")
    public ReturnData<Json> getTzpzHVOByID(@RequestBody Map<String,String> param){
        Json json = new Json();

        try {
            String pk_corp = SystemUtil.getLoginCorpId();
            checkSecurityData(null,new String[]{pk_corp}, null);
            String str = param.get("row");
            if (str == null) {
                throw new BusinessException("数据为空,请检查!");
            }

            BankStatementVO[] listvo = JsonUtils.deserialize(str, BankStatementVO[].class);

            if(listvo == null || listvo.length == 0)
                throw new BusinessException("转化后数据为空,请检查!");

            BankAccountVO bankAccVO = getBankAccountVO(listvo[0]);
            boolean accway = getAccWay(pk_corp);
            VatInvoiceSetVO setvo = queryRuleByType();

            TzpzHVO hvo = gl_yhdzdserv.getTzpzHVOByID(listvo, bankAccVO,
                    pk_corp, SystemUtil.getLoginUserId(), setvo, accway);
            json.setData(hvo);
            json.setSuccess(true);
            json.setMsg("凭证分录构造成功");
        } catch (Exception e) {
            printErrorLog(json, e, "凭证分录构造失败");
        }

        return ReturnData.ok().data(json);
    }

    @RequestMapping("/combinePZ")
    public ReturnData<Json> combinePZ(@RequestBody Map<String,String> param){
        VatInvoiceSetVO setvo = queryRuleByType();
        String body = param.get("head");
        ReturnData<Json> data = null;
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
        BankStatementVO[] vos = null;
        try {
            if (body == null) {
                throw new BusinessException("数据为空,合并生成凭证失败!");
            }
            body = body.replace("}{", "},{");
            body = "[" + body + "]";

            vos = JsonUtils.deserialize(body, BankStatementVO[].class);

            if(vos == null || vos.length == 0)
                throw new BusinessException("数据为空，合并生成凭证失败，请检查");

            String pk_corp = SystemUtil.getLoginCorpId();
            String userid  = SystemUtil.getLoginUserId();
            checkSecurityData(null,new String[]{pk_corp}, null);
//			VatInvoiceSetVO setvo = queryRuleByType();

            Map<String, DcModelHVO> dcmap = gl_yhdzdserv.queryDcModelVO(pk_corp);
            List<BankStatementVO> storeList = gl_yhdzdserv.construcBankStatement(vos, pk_corp);

            if(storeList == null || storeList.size() == 0){
                throw new BusinessException("合并制证：查询银行对账数据失败，请检查");
            }

            BankAccountVO bankAccVO = getBankAccountVO(storeList.get(0));
            boolean accway = getAccWay(pk_corp);
            Map<String, List<BankStatementVO>> combineMap = new LinkedHashMap<String, List<BankStatementVO>>();
            StringBuffer msg = new StringBuffer();
            DcModelHVO modelHVO = null;
            String key;
            String period = "";
            int errorCount = 0;
            String pk_model_h;
            List<BankStatementVO> combineList = null;
            List<String> periodSet = new ArrayList<String>();
            for(BankStatementVO vo : storeList){

                pk_model_h = vo.getPk_model_h();
                if(StringUtil.isEmpty(pk_model_h)){
                    errorCount++;
                    msg.append("<font color='red'><p>交易日期:" + vo.getTradingdate() + "的单据无业务类型，不能生成凭证。</p></font>");
                    continue;
                }

                modelHVO = dcmap.get(pk_model_h);
                if(modelHVO == null){
                    errorCount++;
                    msg.append("<font color='red'><p>交易日期:" + vo.getTradingdate() + "单据业务类型未找到，请检查。</p></font>");
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
                    combineList = new ArrayList<BankStatementVO>();
                    combineList.add(vo);
                    combineMap.put(key, combineList);
                }
            }

            for(Map.Entry<String, List<BankStatementVO>> entry : combineMap.entrySet()){
                try {
                    combineList = entry.getValue();

                    key = entry.getKey();
                    period = splitKey(key);

                    gl_yhdzdserv.saveCombinePZ(combineList,
                            pk_corp, userid, dcmap, bankAccVO, setvo, accway, false);
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

                            gl_yhdzdserv.saveCombinePZ(combineList, pk_corp, userid, dcmap, bankAccVO, setvo, accway, true);
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
            StringBuffer headMsg = gl_yhdzdserv.buildQmjzMsg(periodSet, pk_corp);
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

    private String buildkey(BankStatementVO vo, VatInvoiceSetVO setvo){
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
            }
        }

        return key;
    }

//	private String buildkey(BankStatementVO vo, Integer itype){
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
    public void expExcelData(@RequestBody Map<String,String> param, HttpServletResponse response){
        String strrows = param.get("daterows");

        String opdate =  param.get("opdate");

        JSONArray array = JSON.parseArray(strrows);


        OutputStream toClient = null;
        try {
            response.reset();
            String exName = new String("银行对账单.xls");
            exName = new String(exName.getBytes("GB2312"), "ISO_8859_1");// 解决中文乱码问题
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(exName));
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            byte[] length = null;
            VatExportUtils exp = new VatExportUtils();
            Map<Integer, String> fieldColumn = getExpFieldMap();
            List<String> busiList = gl_yhdzdserv.getBusiTypes(SystemUtil.getLoginCorpId());
            length = exp.exportExcel(fieldColumn, array, toClient,
                    "yinhangduizhangdan.xls", 0, 1, 1, VatExportUtils.EXP_DZD,
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
                !StringUtil.isEmpty(strrows)?"导出银行对账单":"下载银行对账单模板", ISysConstants.SYS_2);
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
            String pk_corp = SystemUtil.getLoginCorpId();
            checkSecurityData(null,new String[]{pk_corp}, null);
            String pzrule = param.get("pzrule");
            String flrule = param.get("flrule");
            String zy = param.get("zy");
            String setId = param.get("setid");
            String bk = param.get("bk");

            if(StringUtil.isEmpty(pzrule)
                    || StringUtil.isEmpty(flrule)){
                throw new BusinessException("设置失败，请重试");
            }

            VatInvoiceSetVO vo = new VatInvoiceSetVO();
            String[] fields = new String[]{ "value", "entry_type", "isbank", "zy" };
            if(!StringUtil.isEmpty(setId)){
                vo.setPrimaryKey(setId);
            }else{
                vo.setPk_corp(pk_corp);
                vo.setStyle(IBillManageConstants.HEBING_YHDZF);
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
            json.setMsg("设置成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "设置失败");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "合并规则调整", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

}
