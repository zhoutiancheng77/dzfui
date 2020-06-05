package com.dzf.zxkj.platform.controller.taxrpt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.CodeName;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.model.image.ImageLibraryVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pjgl.FastOcrStateInfoVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.TaxReportNewQcInitVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletVO;
import com.dzf.zxkj.platform.service.gzgl.ISalaryReportExcel;
import com.dzf.zxkj.platform.service.gzgl.ISalaryReportService;
import com.dzf.zxkj.platform.service.gzgl.ImpExcel.impl.SalaryReportExcelFactory;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.service.taxrpt.IFastTaxService;
import com.dzf.zxkj.platform.util.ExcelReport;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.dzf.zxkj.secret.CorpSecretUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

@RestController
@RequestMapping("/taxrpt/fastTax")
@Slf4j
public class FastTaxController  extends BaseController {
    @Autowired
    private IFastTaxService fastTaxService;
    @Autowired
    private IQmclService gl_qmclserv = null;
    @Autowired
    private IUserService userService = null;
    @Autowired
    private ISalaryReportService gl_gzbserv;
    @Autowired
    private SingleObjectBO singlebo;
    @Autowired
    private IImageGroupService gl_pzimageserv;

    @Autowired
    private SalaryReportExcelFactory factory;
    @Autowired
    protected IBDCorpTaxService sys_corp_tax_serv;
    @Autowired
    private ICorpService corpService;

    /**
     * 4、从服务器查询当前征期
     */
    @GetMapping("/getBsPeriod")
    public ReturnData<Json> getBsPeriod() {
        Json rsjson = new Json();
        try {
            String ret = fastTaxService.getBsPeriod();
            HashMap hm = new HashMap<String, String>();
            hm.put("period", ret);
            rsjson.setData(ret);
            rsjson.setStatus(200);
            rsjson.setSuccess(true);
            rsjson.setMsg("查询成功!");
        } catch (Exception e) {
            printErrorLog(rsjson, e, "从服务器查询当前征期失败");
            rsjson.setSuccess(false);
            rsjson.setStatus(-100);
        }
        return ReturnData.ok().data(rsjson);
    }

    /**
     * 5、查询客户列表
     */
    @PostMapping("/getCustomerList")
    public ReturnData<Json> getCustomerList(@RequestBody Map<String, String> param) {
        Json rsjson = new Json();
        try {
            String userId = param.get("userId");
            String isAllStr = param.get("isAll");
            DZFBoolean isAll = new DZFBoolean(isAllStr);// 转换
            String period = param.get("period");// 期间
            String corps = param.get("corps");// 指定公司
            // 格式：xxx,yyy,zzz
            Map<String, String> corpMap = transCorpStr2Map(corps);

            String loginCorp = SystemUtil.getLoginCorpId();// getLoginCorpInfo().getPk_corp()
            UserVO uservo = SystemUtil.getLoginUserVo();
            List<CorpVO> ret = fastTaxService.getCustomerList(loginCorp, uservo, userId, isAll, corpMap);

            ret = fastTaxService.getBsReportVos(period, ret);
            if(ret != null && ret.size() > 0){
                List<String> bslist = convertToValue(ret);
                //放置报税信息
                List<CorpTaxVo> list = fastTaxService.queryTaxCorpList(bslist);
                if(list != null && list.size() > 0){
                    Map<String,CorpTaxVo> map = DZfcommonTools.hashlizeObjectByPk(list, new String[]{"pk_corp"});
                    for(CorpVO cc : ret){
                        cc.setCorptaxvo(map.get(cc.getPk_corp()));
                    }
                }
            }
            rsjson.setData(ret);
            rsjson.setStatus(200);
            rsjson.setSuccess(true);
            rsjson.setMsg("查询成功!");
        } catch (Exception e) {
            printErrorLog(rsjson, e, "查询客户列表失败");
            rsjson.setSuccess(false);
            rsjson.setStatus(-100);

        }
        return ReturnData.ok().data(rsjson);
    }

    private List<String> convertToValue(List<CorpVO> ret){
        if(ret == null || ret.size() == 0)
            return null;
        List<String> list = new ArrayList<String>();
        for(CorpVO cc : ret){
            list.add(cc.getPk_corp());
        }
        return list;
    }

    private Map<String, String> transCorpStr2Map(String corps) {

        Map<String, String> corpMap = null;
        if (!StringUtil.isEmpty(corps)) {
            String[] corpArr = corps.split(",");
            if (corpArr != null && corpArr.length > 0) {
                corpMap = new HashMap<String, String>();
                for (String pk : corpArr) {
                    corpMap.put(pk, pk);
                }
            }
        }

        return corpMap;
    }

    /**
     * zpm查询，给退税接口用
     */
    @GetMapping("/getClientList")
    public ReturnData<Json> getClientList(String userId) {
        Json rsjson = new Json();
        try {
//            String userId = getRequest().getParameter("userId");
            UserVO uservo = SystemUtil.getLoginUserVo();
            String loginid = uservo.getPrimaryKey();
            if (!StringUtil.isEmpty(userId)) {
                if (userId.equals(loginid)) {
                    List<CorpVO> ret = fastTaxService.getCustomerList(null, uservo, userId, DZFBoolean.TRUE, null);
                    // 只传给对方公司编码、公司名称 数据。
                    rsjson.setData(createCode(ret));
                    rsjson.setStatus(200);
                    rsjson.setSuccess(true);
                    rsjson.setMsg("查询成功!");
                } else {
                    rsjson.setSuccess(false);
                    rsjson.setStatus(-100);
                    rsjson.setMsg("用户失效!");
                }
            } else {
                rsjson.setSuccess(false);
                rsjson.setStatus(-100);
                rsjson.setMsg("参数为空!");
            }
        } catch (Exception e) {
            printErrorLog(rsjson, e, "查询客户列表失败");
            rsjson.setSuccess(false);
            rsjson.setStatus(-100);
        }
        return ReturnData.ok().data(rsjson);
    }

    public CodeName[] createCode(List<CorpVO> ret) {
        if (ret == null || ret.size() == 0)
            return null;
        CodeName[] cns = new CodeName[ret.size()];
        for (int i = 0; i < ret.size(); i++) {
            cns[i] = new CodeName();
            cns[i].setCode(ret.get(i).getInnercode());
            cns[i].setName(ret.get(i).getUnitname());
        }
        return cns;
    }

    /**
     * 6、查询客户申报报表列表
     */
    @PostMapping("/getBsReportList")
    public ReturnData<Json> getBsReportList(@RequestBody Map<String, String> param) {
        Json rsjson = new Json();
        try {
            String customerId = param.get("customerId");
            if (StringUtil.isEmpty(customerId))
                throw new BusinessException("客户参数不能为空");
            String period = param.get("period");
            if (StringUtil.isEmpty(period))
                throw new BusinessException("期间参数不能为空");
            String loginCorp = SystemUtil.getLoginCorpId();
            UserVO uservo = userService.queryUserJmVOByID(SystemUtil.getLoginUserId());
            List<Map<String, Object>> ret = fastTaxService.getBsReportList(loginCorp, uservo, customerId, period);
            ObjectMapper objmapper = new ObjectMapper();
            rsjson.setData(objmapper.writeValueAsString(ret));
            rsjson.setStatus(200);
            rsjson.setSuccess(true);
            rsjson.setMsg("查询成功!");

        } catch (Exception e) {
            printErrorLog(rsjson, e, "查询客户申报报表列表失败");
            rsjson.setSuccess(false);
            rsjson.setStatus(-100);

        }
        return ReturnData.ok().data(rsjson);
    }

    /**
     * 7、查询客户指定报表的数据
     */
    @PostMapping("/getBsReportDetail")
    public ReturnData<Json> getBsReportDetail(@RequestBody Map<String, String> param) {
        Json rsjson = new Json();
        try {
            String customerId = param.get("customerId");
            String rptGroupId = param.get("rptGroupId");
            String loginCorp = SystemUtil.getLoginCorpId();
            UserVO uservo = SystemUtil.getLoginUserVo();
            String ret = fastTaxService.getBsReportDetail(loginCorp, uservo, customerId, rptGroupId);
            rsjson.setData(ret);
            rsjson.setStatus(200);
            rsjson.setSuccess(true);
            rsjson.setMsg("查询成功!");

        } catch (Exception e) {
            printErrorLog(rsjson, e, "查询客户指定报表的数据失败");
            rsjson.setSuccess(false);
            rsjson.setStatus(-100);

        }
        return ReturnData.ok().data(rsjson);
    }

    /**
     * 8、更新报表申报状态
     *
     *  customerId 客户Id
     *  period 征期
     *  reportId  报表Id（或报表名称），表明是哪个sheet。
     *  categoryId 报表类别Id（或名称），表明是哪个excel。可不传。
     *  status 报表当期申报状态 0-未填报 1-已填报 2-已上传（提交）申报
     *  taxMny 税额
     *
     */
    @PostMapping("/updateBsReportStatus")
    public ReturnData<Json> updateBsReportStatus(@RequestBody Map<String, String> param) {
        Json rsjson = new Json();
        try {
            String rptGroupId = param.get("rptGroupId");
            String status = param.get("status");
            String strMny = param.get("taxMny");
            DZFDouble taxMny = null;
            if (!StringUtil.isEmpty(strMny)) {
                taxMny = new DZFDouble(strMny);
            }
            String loginCorp = SystemUtil.getLoginCorpId();
            UserVO uservo = SystemUtil.getLoginUserVo();
            fastTaxService.updateBsReportStatus(loginCorp, uservo, rptGroupId, status, null, taxMny);
            rsjson.setStatus(200);
            rsjson.setSuccess(true);
            rsjson.setMsg("更新成功!");
        } catch (Exception e) {
            printErrorLog(rsjson, e, "更新报表申报状态失败");
            rsjson.setSuccess(false);
            rsjson.setStatus(-100);

        }
        return ReturnData.ok().data(rsjson);
    }

    /**
     * 一键报税零申报后更新申报状态
     */
    @PostMapping("/updateBsReportByZeroDeclare")
    public ReturnData<Json> updateBsReportByZeroDeclare(@RequestBody Map<String, String> param) {
        Json rsjson = new Json();
        try {
            String customerId = param.get("customerId");
            String period = param.get("period");
            String sbzlbh = param.get("sbzlbh");
            String status = param.get("status");
            String strMny = param.get("taxMny");// 税款
            String loginCorp = SystemUtil.getLoginCorpId();
            UserVO uservo = SystemUtil.getLoginUserVo();
            String loginDate = SystemUtil.getLoginDate();
            DZFDouble taxMny = null;
            if (!StringUtil.isEmpty(strMny)) {
                taxMny = new DZFDouble(strMny);
            }
            fastTaxService.updateBsReportByZeroDeclare(loginCorp, uservo, loginDate, customerId, period, sbzlbh, status,
                    taxMny);
            rsjson.setStatus(200);
            rsjson.setSuccess(true);
            rsjson.setMsg("零申报更新成功!");
        } catch (Exception e) {
            printErrorLog(rsjson, e, "零申报更新报表申报状态失败");
            rsjson.setSuccess(false);
            rsjson.setStatus(-100);

        }
        return ReturnData.ok().data(rsjson);
    }

    /**
     * 勾选
     */
    @PostMapping("/updateBsReportDetail")
    public ReturnData<Json> updateBsReportDetail(@RequestBody Map<String, String> param) {
        Json rsjson = new Json();
        try {
            String customerId = param.get("customerId");
            String reportId = param.get("reportId");
            String isAdd = param.get("isAdd");
            String sbzlbh = param.get("sbzlbh");
            String sbzq = param.get("sbzq");
            String loginCorp = SystemUtil.getLoginCorpId();
            UserVO uservo = SystemUtil.getLoginUserVo();
            fastTaxService.updateBsReportDetail(loginCorp, uservo, customerId, reportId, isAdd, sbzlbh, sbzq);
            rsjson.setStatus(200);
            rsjson.setSuccess(true);
            rsjson.setMsg("更新成功!");
        } catch (Exception e) {
            printErrorLog(rsjson, e, "更新纳税信息维护表失败!");
            rsjson.setSuccess(false);
            rsjson.setStatus(-100);

        }
        return ReturnData.ok().data(rsjson);
    }

    @PostMapping("/updateBsReportRemark")
    public ReturnData<Json> updateBsReportRemark(@RequestBody Map<String, String> param) {
        Json rsjson = new Json();
        try {
            String rptGroupId = param.get("rptGroupId");
            String customerId = param.get("customerId");
            String remark = param.get("aremark");
            fastTaxService.updateBsReportRemark(customerId, rptGroupId, remark);
            rsjson.setStatus(200);
            rsjson.setSuccess(true);
            rsjson.setMsg("更新成功!");
        } catch (Exception e) {
            printErrorLog(rsjson, e, "更新纳税信息失败!");
            rsjson.setSuccess(false);
            rsjson.setStatus(-100);

        }
        return ReturnData.ok().data(rsjson);
    }

    /**
     * 存储期初数据 增值税、企业所得税
     */
    @PostMapping("/updateBsReportQC")
    public ReturnData<Json> updateBsReportQC(@RequestBody Map<String, String> param) {
        Json rsjson = new Json();
        try {
            String corps = param.get("corps");
//            JSONArray array = JSON.parseArray(corps);
//            Map<String, String> bodyMapping = FieldMapping.getFieldMapping(new TaxReportNewQcInitVO());
//            TaxReportNewQcInitVO[] initvos = DzfTypeUtils.cast(array, bodyMapping, TaxReportNewQcInitVO[].class,
//                    JSONConvtoJAVA.getParserConfig());
            TaxReportNewQcInitVO[] initvos = JsonUtils.deserialize(corps, TaxReportNewQcInitVO[].class);
            if (initvos == null || initvos.length == 0) {
                throw new BusinessException("参数信息不完整，请检查");
            }

            StringBuffer sf = new StringBuffer();

            for (TaxReportNewQcInitVO vo : initvos) {
                try {
                    fastTaxService.updateBsReportQC(vo, SystemUtil.getLoginUserVo());
                } catch (Exception e) {
                    log.error("错误",e);
                    sf.append("公司:").append(vo.getPk_corp()).append(",期间:").append(vo.getPeriod()).append("期初更新失败。");
                    if (e instanceof BusinessException) {
                        sf.append("MSG:").append(e.getMessage());
                    }
                    sf.append("<br/>");
                }
            }

            if (sf.length() > 0) {
                rsjson.setMsg(sf.toString());
            } else {
                rsjson.setMsg("更新期初数据成功!");
            }
            rsjson.setStatus(200);
            rsjson.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(rsjson, e, "更新期初数据失败!");
            rsjson.setSuccess(false);
            rsjson.setStatus(-100);
        }

        return ReturnData.ok().data(rsjson);
    }

    /**
     * 是否接口填报
     */
    @GetMapping("/getSendTaxWay")
    public ReturnData<Json> getSendTaxWay() {
        Json rsjson = new Json();
        try {
            // String customerId = getRequest().getParameter("customerId");
            // if (StringUtil.isEmpty(customerId)) {
            // customerId = getLoginCorpInfo().getPk_corp();
            // }
            // YntParameterSet paravo =
            // paramterService.queryParamterbyCode(customerId,
            // IParameterConstants.DZF006);
            //
            // Integer ret = null;
            // if (paravo == null || paravo.getPardetailvalue() == null) {
            // ret = IParameterConstants.ISINTER;
            // } else {
            // ret = paravo.getPardetailvalue();
            // }

            rsjson.setData(0);// 接口填报
            rsjson.setStatus(200);
            rsjson.setSuccess(true);
            rsjson.setMsg("查询成功!");

        } catch (Exception e) {
            printErrorLog(rsjson, e, "查询客户指定填报方式失败");
            rsjson.setSuccess(false);
            rsjson.setStatus(-100);

        }
        return ReturnData.ok().data(rsjson);
    }

    /**
     * 注：********** 该接口为个税收费接口，为混淆接口含义，故叫查询申报信息
     */
    @PostMapping("/")
    public ReturnData<Json> getBsReportDatas(@RequestBody Map<String, String> param) {
        Json rsjson = new Json();
        try {
            String customerId = param.get("customerId");
            String period = param.get("period");
            // 扣费
            DZFDouble mny = fastTaxService.getTaxFee(customerId);
            fastTaxService.processShoufei(customerId, period, SystemUtil.getLoginUserId(), mny);
            rsjson.setStatus(200);
            rsjson.setSuccess(true);
            rsjson.setMsg("查询申报信息成功!");
        } catch (Exception e) {

            printErrorLog(rsjson, e, "查询申报信息失败!");
            rsjson.setSuccess(false);
            rsjson.setStatus(-100);

        }
        return ReturnData.ok().data(rsjson);
    }

    /**
     * 获取纳税模板 sbzlbh 申报种类编号 reportIds 报表编码s
     */
    // public void getBsReportTemplet() {
    // Json rsjson = new Json();
    // try {
    // String sb_zlbh = getRequest().getParameter("sbzlbh");
    // String repcodes = getRequest().getParameter("reportIds");
    //
    // String[] repCodeArr = null;
    // if(!StringUtil.isEmpty(repcodes)){
    // repCodeArr = repcodes.split(",");
    // }
    //
    // UserVO uservo = getLoginUserInfo();
    // String loginCorp = getLogincorppk();
    //
    // String ret = fastTaxService.getBsReportTemplet(uservo, loginCorp,
    // sb_zlbh, repCodeArr, "北京");
    // rsjson.setData(ret);
    // rsjson.setStatus(200);
    // rsjson.setSuccess(true);
    // rsjson.setMsg("查询成功!");
    //
    // } catch (Exception e) {
    // printErrorLog(rsjson, log, e, "查询客户指定报表的数据失败");
    // rsjson.setSuccess(false);
    // rsjson.setStatus(-100);
    //
    // }
    // writeJson(rsjson);
    // }

    private String getClientSessionid(String usercode, String clientid) {
        return usercode + "," + clientid;
    }

    /**
     * 登录给一键报税使用
     */
//    public void bslogin() {
//        Json json = new Json();
//        // 取得访问的客户端
//        String clientid = getRequest().getParameter("clientid");
//        if (StringUtil.isEmpty(clientid)) {
//            getSession().removeAttribute("rand");
//            json.setSuccess(false);
//            json.setStatus(-200);
//            json.setMsg("传输参数错误!请指定客户端!");
//            writeJson(json);
//            return;
//        }
//        getRequest().getSession().removeAttribute(IGlobalConstants.logout_msg);
//        LoginLogVo loginLogVo = getLoginVo("dzf_kj", clientid);
//        json.setSuccess(false);
//        UserVO taxuservo = getQuestUser();
//        String password = null;
//        String usercode = null;
//        try {
//            password = taxuservo.getUser_password();
//            usercode = taxuservo.getUser_code();
//            password = RSAUtils.decryptStringByJs(password);
//            usercode = RSAUtils.decryptStringByJs(usercode);
//            taxuservo.setUser_code(usercode);
//
//        } catch (Exception e) {
//            getSession().removeAttribute("rand");
//            log.error("错误",e);
//            json.setSuccess(false);
//            json.setStatus(-200);
//            json.setMsg("用户名、密码或验证码错误!");
//            writeJson(json);
//            return;
//        }
//
//        try {
//            String date = getRequest().getParameter("date");
//            if (!checkUserInfo(taxuservo, clientid))
//                return;
////			taxuservo.setUser_code(taxuservo.getUser_code().trim());
//            UserVO userVo = userService.loginByCode(taxuservo.getUser_code().trim());
//            // sp.addParam(new Encode().encode(vo.getUser_password()));
//            if (userVo == null) {
//                getSession().removeAttribute("rand");
//                json.setMsg("用户名、密码或验证码错误!");
//                log.error("用户:"+taxuservo.getUser_code()+"，一键报税登录失败，用户名、密码或验证码错误!");
//                writeJson(json);
//                return;
//            }
//            loginLogVo.setPk_user(userVo.getPrimaryKey());
//            UserLoginVo userLoginVo = LoginCache.getUserVo(getClientSessionid(taxuservo.getUser_code(), clientid));
//            if (!userVo.getUser_password().equals(new Encode().encode(password))) {
//                getSession().removeAttribute("rand");
//                LoginCache.loginFail(getClientSessionid(userVo.getUser_code(), clientid));
//                userLoginVo = LoginCache.getUserVo(getClientSessionid(taxuservo.getUser_code(), clientid));
//                json.setStatus(userLoginVo.getNumber());
//                if (userLoginVo.getNumber() >= IGlobalConstants.lock_fail_login) {
//                    json.setMsg("提示：您的账号已被锁定! "
//                            + (IGlobalConstants.lock_login_min
//                            - (new Date().getTime() - userLoginVo.getLogin_date().getTime()) / (60 * 1000))
//                            + "分钟之后解锁  !");
//                    loginLogVo.setMemo("被锁定");
//                    userService.loginLog(loginLogVo);
//                    writeJson(json);
//                    return;
//                }
//                // json.setMsg("用户名或密码错误!失败" + (IGlobalConstants.lock_fail_login
//                // - userLoginVo.getNumber()) + "次后将被锁定！");
//                json.setMsg("用户名、密码或验证码错误!");
//                loginLogVo.setMemo("密码错误");
//            } else {
//                LoginCache.ClearUserVo(getClientSessionid(userVo.getUser_code(), clientid));
//                json.setStatus(userLoginVo.getNumber());
//                if (IDefaultValue.DefaultGroup.equals(userVo.getPk_corp())
//                        || (userVo.getBappuser() != null && userVo.getBappuser().booleanValue())) {
//                    getSession().removeAttribute("rand");
//                    json.setMsg("无权操作!");
//                    loginLogVo.setMemo("无权操作");
//                } else if (userVo.getLocked_tag() != null && userVo.getLocked_tag().booleanValue()) {
//                    getSession().removeAttribute("rand");
//                    json.setMsg("当前用户被锁定，请联系管理员!");
//                    loginLogVo.setMemo("锁定");
//                } /*
//                 * else if(userVo.getAble_time() == null ||
//                 * userVo.getAble_time().compareTo(new DZFDate(date)) > 0){
//                 * json.setMsg("用户还未生效!"); loginLogVo.setMemo("未生效"); }
//                 */else {
//                    json.setSuccess(true);
//                    json.setRows(convertToUserInfo(userVo));
//                    json.setMsg("登录成功!");
//                    loginLogVo.setLoginstatus(1);
//                    loginLogVo.setPk_corp(userVo.getPk_corp());
//                    loginLogVo.setMemo("登录成功");
//                    StringBuffer sf = new StringBuffer();
//                    if (!checkUserPWD(password, sf)) {
//                        getSession().setAttribute(IGlobalConstants.login_user, userVo.getPrimaryKey());
//                        getSession().setAttribute(IGlobalConstants.login_date, date);
//                        json.setSuccess(false);
//                        json.setStatus(-200);
//                        if (sf.length() > 0) {
//                            json.setMsg(sf.toString());
//                        } else {
//                            json.setMsg("抱歉，您的密码过于简单，请您修改密码后再登录系统！");
//                        }
//                        json.setRows(null);
//                        loginLogVo.setMemo("密码简单");
//                        userService.loginLog(loginLogVo);
//                        writeJson(json);
//                        return;
//                    }
//                    // 判断是否在其它端登录
//                    if (isOtherClientOnline(userVo.getPrimaryKey(), IGlobalConstants.DZF_KJ, clientid)) {
//                        String force = getRequest().getParameter("f");
//
//                        if (force != null && force.equals("1")) {
////							try {
////								LoginLogVo loginOldVo = getLoginVo("dzf_kj", clientid);
////								loginOldVo.setLogoutdate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
////								loginOldVo.setLogouttype(2);
////								loginOldVo.setPk_user(loginLogVo.getPk_user());
////								loginOldVo.setLoginsession(SessionCache.getInstance()
////										.getByUserID(userVo.getPrimaryKey(), IGlobalConstants.DZF_KJ, clientid)
////										.getSessionid());
////								userService.logoutLog(loginOldVo);
////							} catch (Exception e) {
////								log.error("错误",e);
////							}
//                            loginLogVo.setMemo("强制登录");
//                            // DzfSessionContext.getInstance().DelUserSessionByPkUser(userVo.getPrimaryKey(),false);
//                        } else {
//                            // 重新把验证码放入缓存
//                            String uuid = (String) getSession().getAttribute(IGlobalConstants.uuid);
//                            SessionCache.getInstance().addVerify(uuid, getRequest().getParameter("verify"));
//
//                            json.setSuccess(false);
//                            json.setStatus(-100);
//                            json.setMsg("当前用户已经登录，是否强制退出？");
//                            loginLogVo.setMemo("已登录");
//                            userService.loginLog(loginLogVo);
//                            writeJson(json);
//                            return;
//                        }
//                    }
//
//                    getSession().setAttribute(IGlobalConstants.login_user, userVo.getPrimaryKey());
//                    getSession().setAttribute(IGlobalConstants.login_date, date);
//                    getSession().setAttribute(IGlobalConstants.clientid, clientid);
//                    // DzfSessionContext.getInstance().AddUserSession(getSession());
//
//                    getSession().setAttribute(IGlobalConstants.appid, IGlobalConstants.DZF_KJ); // 会计核算端
//
//                    // 未选择公司的部分登录成功，也要写session信息和cookie，否则登录不能支持分布式
//                    addRedisSessionAndCookie(userVo.getPrimaryKey(), json);
//                }
//            }
//        } catch (Exception e) {
//            getSession().removeAttribute("rand");
//            log.error("错误",e);
//            json.setSuccess(false);
//            json.setStatus(-200);
//            json.setMsg("登录失败！");
//        }
//        // if (json.isSuccess()) {
//        // json.setHead(getRequest().getContextPath() + "/selcomp.jsp?appid=" +
//        // IGlobalConstants.DZF_KJ);
//        // }
//        userService.loginLog(loginLogVo);
//        writeJson(json);
//    }
//
//    private UserVOinfo convertToUserInfo(UserVO userVo) {
//        if (userVo == null)
//            return null;
//        UserVOinfo info = new UserVOinfo();
//        info.setUid(userVo.getCuserid());
//        info.setUcode(userVo.getUser_code());
//        info.setUname(userVo.getUser_name());
//        info.setCorp_id(userVo.getPk_corp());
//        info.setCrtcorp_id(userVo.getPk_creatcorp());
//        info.setEn_time(userVo.getAble_time());
//        info.setB_mng(userVo.getIsmanager());
//        info.setBaccount(userVo.getBaccount());
//        info.setBdata(userVo.getBdata());
//        info.setIslogin(userVo.getIslogin());
//        info.setIstate(userVo.getIstate());
//        info.setPhonenum(userVo.getPhone());
//        info.setMail(userVo.getUser_mail());
//        info.setLock_flag(userVo.getLocked_tag());
//        info.setStatus(userVo.getStatus());
//        info.setDepartid(userVo.getPk_department());
//        return info;
//    }
//
//    class UserVOinfo implements Serializable {
//        private String uid;
//        private String corp_id;
//        private String crtcorp_id;
//        private String ucode;
//        private String uname;
//        private DZFDate en_time;
//        private DZFBoolean b_mng;
//        private DZFBoolean baccount;
//        private DZFBoolean bdata;
//        private DZFBoolean islogin;
//        private String phonenum;
//        private String mail;
//        private String departid;
//        private DZFBoolean lock_flag;
//        private Integer istate;
//        private int status = 0;
//
//        public String getUid() {
//            return uid;
//        }
//
//        public void setUid(String uid) {
//            this.uid = uid;
//        }
//
//        public String getCorp_id() {
//            return corp_id;
//        }
//
//        public void setCorp_id(String corp_id) {
//            this.corp_id = corp_id;
//        }
//
//        public String getCrtcorp_id() {
//            return crtcorp_id;
//        }
//
//        public void setCrtcorp_id(String crtcorp_id) {
//            this.crtcorp_id = crtcorp_id;
//        }
//
//        public String getUcode() {
//            return ucode;
//        }
//
//        public void setUcode(String ucode) {
//            this.ucode = ucode;
//        }
//
//        public String getUname() {
//            return uname;
//        }
//
//        public void setUname(String uname) {
//            this.uname = uname;
//        }
//
//        public DZFDate getEn_time() {
//            return en_time;
//        }
//
//        public void setEn_time(DZFDate en_time) {
//            this.en_time = en_time;
//        }
//
//        public DZFBoolean getB_mng() {
//            return b_mng;
//        }
//
//        public void setB_mng(DZFBoolean b_mng) {
//            this.b_mng = b_mng;
//        }
//
//        public DZFBoolean getBaccount() {
//            return baccount;
//        }
//
//        public void setBaccount(DZFBoolean baccount) {
//            this.baccount = baccount;
//        }
//
//        public DZFBoolean getBdata() {
//            return bdata;
//        }
//
//        public void setBdata(DZFBoolean bdata) {
//            this.bdata = bdata;
//        }
//
//        public DZFBoolean getIslogin() {
//            return islogin;
//        }
//
//        public void setIslogin(DZFBoolean islogin) {
//            this.islogin = islogin;
//        }
//
//        public String getPhonenum() {
//            return phonenum;
//        }
//
//        public void setPhonenum(String phonenum) {
//            this.phonenum = phonenum;
//        }
//
//        public String getMail() {
//            return mail;
//        }
//
//        public void setMail(String mail) {
//            this.mail = mail;
//        }
//
//        public String getDepartid() {
//            return departid;
//        }
//
//        public void setDepartid(String departid) {
//            this.departid = departid;
//        }
//
//        public DZFBoolean getLock_flag() {
//            return lock_flag;
//        }
//
//        public void setLock_flag(DZFBoolean lock_flag) {
//            this.lock_flag = lock_flag;
//        }
//
//        public Integer getIstate() {
//            return istate;
//        }
//
//        public void setIstate(Integer istate) {
//            this.istate = istate;
//        }
//
//        public int getStatus() {
//            return status;
//        }
//
//        public void setStatus(int status) {
//            this.status = status;
//        }
//    }
//
//    private boolean checkUserPWD(String pwd, StringBuffer eInfo) {
//        if (pwd.length() < 8) {
//            eInfo.append("密码长度不能小于8\n");
//            return false;
//        }
//        if (!pwd.matches(".*([0-9]+.*[A-Za-z]+|[A-Za-z]+.*[0-9]+).*")) {
//            eInfo.append("密码必须含有数字、字母\n");
//            return false;
//        }
//        // 判断是否为初始化密码
//        if (Arrays.asList(CommonPassword.INIT_PASSWORD).contains(pwd)) {
//            eInfo.append("密码为初始化密码!\n");
//            return false;
//        }
//        String regEx = "[~!@#$%^&*()<>?+=]";
//        Pattern p = Pattern.compile(regEx);
//        Matcher m = p.matcher(pwd);
//        if (!m.find()) {
//            eInfo.append("密码必须含有特殊字符\n");
//            return false;
//        }
//        return true;
//    }
//
//    private LoginLogVo getLoginVo(String project, String clinetid) {
//        LoginLogVo loginLogVo = new LoginLogVo();
//        try {
//            loginLogVo.setLogindate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
//            loginLogVo.setLoginsession(getSession().getId() + ",client:" + clinetid);
//            loginLogVo.setLoginstatus(0);
//            loginLogVo.setProject_name(project);
//            loginLogVo.setLoginip(CommonUtil.getIpAddr(getRequest()));
//        } catch (Exception e) {
//            log.error("错误",e);
//        }
//        return loginLogVo;
//    }
//
//    private UserVO getQuestUser() {
//        UserVO vo = new UserVO();
//        String user_code = getRequest().getParameter("ucode");
//        String user_password = getRequest().getParameter("u_pwd");
//        vo.setUser_code(user_code);
//        vo.setUser_password(user_password);
//        return vo;
//    }
//
//    private boolean checkUserInfo(UserVO taxuservo, String clientid) {
//        Json json = new Json();
//        if (taxuservo == null || taxuservo.getUser_code() == null || taxuservo.getUser_code().trim().length() == 0) {
//            getSession().removeAttribute("rand");
//            json.setMsg("用户名、密码或验证码错误!");
//            writeJson(json);
//            return false;
//        }
//        LoginCache.ClearUserVoByTime(getClientSessionid(taxuservo.getUser_code(), clientid));
//        UserLoginVo uservo = LoginCache.getUserVo(getClientSessionid(taxuservo.getUser_code(), clientid));
//        json.setStatus(uservo.getNumber());
//
//        String verify = getRequest().getParameter("verify");
//        // rand 从redis读取 2016-07-27
//        String rand = SessionCache.getInstance().getVerify((String) getSession().getAttribute(IGlobalConstants.uuid));
//        if (rand == null) {
//            rand = (String) getSession().getAttribute("rand");
//        }
//
//        // if(((String)getSession().getAttribute("rand")) != null){
//        // String rand =
//        // ((String)getSession().getAttribute("rand")).toLowerCase();
//        if (rand != null) {
//            if (verify == null || !rand.toLowerCase().equals(verify.toLowerCase())) {
//                getSession().removeAttribute("rand");
//                json.setMsg("用户名、密码或验证码错误!");
//                writeJson(json);
//                return false;
//            }
//        } else {
//            getSession().removeAttribute("rand");
//            json.setMsg("用户名、密码或验证码错误!");
//            json.setStatus(uservo.getNumber());
//            writeJson(json);
//            return false;
//        }
//
//        // 输入密码错误超过三次锁定
//        if (uservo.getNumber() >= IGlobalConstants.lock_fail_login) {
//            getSession().removeAttribute("rand");
//            json.setMsg(
//                    "提示：您的账号已被锁定! "
//                            + (IGlobalConstants.lock_login_min
//                            - (new Date().getTime() - uservo.getLogin_date().getTime()) / (60 * 1000))
//                            + "分钟之后解锁  !");
//            writeJson(json);
//            return false;
//        } /*
//         * else if(uservo.getNumber() >=IGlobalConstants.verify_fail_login){
//         *
//         * }
//         */
//
//        /*
//         * 登录就要求输验证码
//         */
//        /*
//         * if(uservo.getNumber()>= 0 ){ String verify =
//         * getRequest().getParameter("verify");
//         * if(((String)getSession().getAttribute("rand")) != null){ String rand
//         * = ((String)getSession().getAttribute("rand")).toLowerCase(); if (
//         * verify==null ||!rand.equals(verify.toLowerCase())) { json.setMsg(
//         * "提示：验证码不正确! "); writeJson(json); return false; } }else{ json.setMsg(
//         * "提示：验证码不正确! "); json.setStatus(uservo.getNumber()); writeJson(json);
//         * return false; } }
//         */
//
//        if (taxuservo.getUser_password() == null || taxuservo.getUser_password().trim().length() == 0) {
//            getSession().removeAttribute("rand");
//            json.setMsg("用户名、密码或验证码错误!");
//            writeJson(json);
//            return false;
//        }
//        return true;
//    }
//
//    private boolean isOtherClientOnline(String pk_user, String appid, String clientid) {
//        boolean isOtherOnline = false;
//        DZFSessionVO session_userid = SessionCache.getInstance().getByUserID(pk_user, appid, clientid);
//        if (session_userid != null) {
//            String uuid = null;
//            String token = DzfCookieTool.getToken(getRequest());
//            if (token != null) {
//                String realtoken = DzfCookieTool.getRealToken(token);
//                if (realtoken != null) {
//                    String[] sa = realtoken.split(",");
//                    uuid = sa[0];
//                }
//            }
//            if (uuid == null || session_userid.getUuid().equals(uuid) == false) // 当前用户没建立uuid，或者已建立的uuid与网络已登录的不一致，则其它用户在线。
//            {
//                isOtherOnline = true;
//            }
//        }
//        return isOtherOnline;
//    }
//
//    private void addRedisSessionAndCookie(String userid, Json json) throws BusinessException {
//
//        getSession().setAttribute(IGlobalConstants.remote_address, getRequest().getRemoteAddr());
//
//        // 写cookie到客户端
//        DzfCookieTool.writeCookie(getSession(), getRequest(), getResponse());
//
//        // session同步至redis服务器
//        SessionCache.getInstance().addSession(getSession());
//
//        //// zpm 为了测试，增加一个公司的数据，即登录成功赋值一个公司-------------start
//
//        ////// getSession().setAttribute(IGlobalConstants.login_corp, "002c5g");
//        /// zpm------------------------------------------end
//
//        /*
//         * String service = getRequest().getParameter("service");
//         *
//         * if (StringUtil.isEmptyWithTrim(service) == false) {
//         *
//         * HttpSession hs = getSession(); if
//         * (hs.getAttribute(IGlobalConstants.login_token) == null) {
//         * RSACoderUtils.createToken(getSession()); }
//         *
//         *
//         *
//         * String ticket = userid + System.currentTimeMillis(); String
//         * encryptTicket = SSOServerUtils.encryptByPublicKey(ticket);
//         *
//         * DZFSessionVO sessionvo = DzfSessionTool.createSession(getSession());
//         *
//         * SSOServerUtils.putTicket(ticket, sessionvo);
//         *
//         *
//         * StringBuilder url = new StringBuilder(); url.append(service); if (0
//         * <= service.indexOf("?")) { url.append("&"); } else { url.append("?");
//         * } url.append("t=").append(encryptTicket);
//         * json.setHead(url.toString());
//         *
//         * }
//         */
//    }

//	/**
//	 * 更新客户纳税等信息  问罗力华没有使用。zpm去掉。2018.11.14
//	 */
//	public void updateCustIndustryCode() {
//		Json rsjson = new Json();
//		try {
//			TaxCustomerInfoVO invo = new TaxCustomerInfoVO();
//			TaxCustomerInfoVO infovo = (TaxCustomerInfoVO) DzfTypeUtils.cast(getRequest(), invo);
//			String loginCorp = getLoginCorpInfo().getPk_corp();
//			UserVO uservo = getLoginUserInfo();
//			fastTaxService.updateCustIndustryCode(loginCorp, uservo, infovo);
//			rsjson.setStatus(200);
//			rsjson.setSuccess(true);
//			rsjson.setMsg("更新成功!");
//		} catch (Exception e) {
//			printErrorLog(rsjson, log, e, "更新客户信息失败");
//			rsjson.setSuccess(false);
//			rsjson.setStatus(-100);
//		}
//		writeJson(rsjson);
//
//	}

    // 工资表导出纳税申报表
    @PostMapping("/exportTaxDeclaration")
    public void exportTaxDeclaration(@RequestBody Map<String, String> param, HttpServletResponse response) {

//        HttpServletResponse response = getResponse();

        OutputStream toClient = null;
        try {

            String billtype = param.get("billtype");
            if (StringUtil.isEmpty(billtype))
                billtype = "01";

            String pk_corp = param.get("pk_corp");
            String period = param.get("period");
            // 查询工资表数据
            SalaryReportVO[] vos = gl_gzbserv.query(pk_corp, period, billtype);
            if (vos.length == 0) {
                return;
            }
//            String json = JSONProcessor.toJSONString(vos, new FastjsonFilter(), new SerializerFeature[] {
//                    SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect });
//
//            JSONArray jsonArray = (JSONArray) JSON.parseArray(json);

            String json = JsonUtils.serialize(vos);
            JSONArray jsonArray = (JSONArray) JSON.parseArray(json);

            response.reset();
            String fileName = "正常工资薪金.xls";
            String formattedName = URLEncoder.encode(fileName, "UTF-8");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + fileName + ";filename*=UTF-8''" + formattedName);

            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
//			CorpVO corpvo = (CorpVO) singlebo.queryByPrimaryKey(CorpVO.class, getLogincorppk());
            CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(SystemUtil.getLoginCorpId());
            ISalaryReportExcel salExcel = factory.produce(corptaxvo);
            byte[] length = salExcel.exportExcel(jsonArray, toClient, billtype, corptaxvo, SystemUtil.getLoginUserVo());
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
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (Exception e) {
                log.error("excel导出错误", e);
            }
        }
    }

    // 工资表导出人员信息
    @PostMapping("/exportPersonnelInfo")
    public void exportPersonnelInfo(@RequestBody Map<String, String> param, HttpServletResponse response) {
//        HttpServletResponse response = getResponse();

        OutputStream toClient = null;
        try {
            String pk_corp = param.get("pk_corp");
            String period = param.get("period");

            String billtype = param.get("billtype");
            if (StringUtil.isEmpty(billtype))
                billtype = "01";

            // 查询工资表数据
            SalaryReportVO[] vos = gl_gzbserv.query(pk_corp, period, billtype);
            if (vos == null || vos.length == 0)
                return;

            // 查询上一个月工资表数据
            String prePeriod = DateUtils.getPreviousPeriod(period);
            SalaryReportVO[] preVos = gl_gzbserv.query(pk_corp, prePeriod, billtype);
            List<SalaryReportVO> list = new ArrayList<>();
            List<String> slist = new ArrayList<>();
            for (SalaryReportVO vo : vos) {
                vo.setRyzt("正常");
                list.add(vo);
                slist.add(vo.getYgname());

            }

            if (preVos != null && preVos.length > 0) {
                for (SalaryReportVO vo : preVos) {
                    if (!slist.contains(vo.getYgname())) {
                        vo.setRyzt("非正常");
                        list.add(vo);
                    }
                }
            }

            response.reset();

            String fileName = "人员信息.xls";
            String formattedName = URLEncoder.encode(fileName, "UTF-8");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + fileName + ";filename*=UTF-8''" + formattedName);
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");

            ExcelReport<SalaryReportVO> ex = new ExcelReport<SalaryReportVO>();

            byte[] length = ex.expPerson(SystemUtil.getLoginCorpVo().getPhone1(), list, toClient, "01");
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
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (Exception e) {
                log.error("excel导出错误", e);
            }
        }
    }

    // impExcel
    @PostMapping("impExcel")
    public ReturnData impExcel(@RequestParam("impfile") MultipartFile file, HttpServletRequest request) {
        Json json = new Json();
        String opdate = null;
        try {

            String billtype = request.getParameter("billtype");
            if (StringUtil.isEmpty(billtype))
                billtype = "01";

//            File[] infiles = ((MultiPartRequestWrapper) getRequest()).getFiles("file");
            String customerId = request.getParameter("customerId");
            if (file == null) {
                throw new BusinessException("请选择导入文件!");
            }
//            String[] fileNames = ((MultiPartRequestWrapper) getRequest()).getFileNames("file");
            opdate = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
//            File infile = infiles[0];
            String filename = file.getOriginalFilename();
            int index = filename.lastIndexOf(".");
            String filetype = filename.substring(index + 1);
            Object[] vos = onBoImp(file, opdate, filetype, customerId, billtype);

            json.setMsg("工资表导入成功!");
            json.setRows(vos);
            json.setSuccess(true);

        } catch (Exception e) {
            // log.error("文件导入失败!" , e);
            printErrorLog(json, e, "文件导入失败!");
            // json.setSuccess(false);
            // json.setMsg("文件导入失败!\n" ,e);
        }
        if (!StringUtil.isEmpty(opdate)) {
            DZFDate date = new DZFDate(opdate + "-01");
            writeLogRecord(LogRecordEnum.OPE_KJ_SALARY,
                    "导入工资表：" + date.getYear() + "年" + date.getMonth() + "月", ISysConstants.SYS_2);
        }
        return ReturnData.ok().data(json);
    }

    private Object[] onBoImp(MultipartFile infile, String opdate, String filename, String customerId, String billtype)
            throws Exception {
        InputStream is = null;
        try {
            is = infile.getInputStream();
            Workbook rwb = null;
            if ("xls".equals(filename)) {
                rwb = new HSSFWorkbook(is);
            } else if ("xlsx".equals(filename)) {
                rwb = new XSSFWorkbook(is);
            } else {
                throw new BusinessException("不支持的文件格式");
            }
            // XSSFWorkbook rwb = new XSSFWorkbook(is);
            int sheetno = rwb.getNumberOfSheets();
            if (sheetno == 0) {
                throw new Exception("需要导入的数据为空。");
            }
            Sheet sheets = rwb.getSheetAt(0);// 取第2个工作簿
            return doImport(null, sheets, opdate, customerId, billtype);
        } catch (FileNotFoundException e2) {
            throw new Exception("文件未找到");
        } catch (IOException e2) {
            throw new Exception("文件格式不正确，请选择导入文件");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
    }

    private Object[] doImport(File excelfile, Sheet sheets, String opdate, String customerId, String billtype)
            throws Exception {

        CorpVO corpvo = (CorpVO) singlebo.queryByPrimaryKey(CorpVO.class, customerId);
        CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(customerId);
        ISalaryReportExcel salExcel = factory.produce(corptaxvo);
        SalaryReportVO[] vos = salExcel.impExcel(null, sheets, opdate, billtype,corptaxvo);
        if (vos == null || vos.length <= 0) {
            throw new BusinessException("导入文件数据为空，请检查。");
        }
        Object[] objs = gl_gzbserv.saveImpExcelForTax(SystemUtil.getLoginDate(), SystemUtil.getLoginUserId(),
                corpvo, vos, opdate, vos[0].getImpmodeltype(), billtype);

        return objs;
    }
    @RequestMapping("/uploadSingleFile")
    public ReturnData<Json> uploadSingleFile(MultipartFile infiles,
                                             String period,String pk_corp,String invoicedata,
                                             String pjlx, String isbizperiod, String g_id) {
        Json json = new Json();
        json.setSuccess(false);
//        String period = null;
        try {
//            period = ((MultiPartRequestWrapper) getRequest()).getParameter("period");// 上传期间
//            String invoicedata = ((MultiPartRequestWrapper) getRequest()).getParameter("invoicedata");// 上传发票信息json
//            String pk_corp = ((MultiPartRequestWrapper) getRequest()).getParameter("pk_corp");// 上传公司

            log.info("扫描信息：" + invoicedata);
            if (StringUtil.isEmpty(period)) {
                throw new BusinessException("上传期间缺失,请检查!");
            }
            if (StringUtil.isEmpty(pk_corp)) {
                throw new BusinessException("上传公司缺失,请检查!");
            }

//            File[] infiles = ((MultiPartRequestWrapper) getRequest()).getFiles("file");
//            String[] filenames = ((MultiPartRequestWrapper) getRequest()).getFileNames("file");
            String[] filenames =  new String[]{ infiles.getOriginalFilename() };
            if (infiles == null) {
                throw new BusinessException("文件为空,请检查!");
            }

//            String pjlxType= ((MultiPartRequestWrapper) getRequest()).getParameter("pjlx");
            if (StringUtil.isEmpty(pjlx)) {
                throw new BusinessException("上传分类缺失,请检查!");
            }
            //
            UserVO userVo = SystemUtil.getLoginUserVo();
            if (userVo == null) {
                throw new BusinessException("登录信息丢失,请重新登录!");
            }
            // Y按业务日期入账，N按指定期间入账
//            String isbizperiod = ((MultiPartRequestWrapper) getRequest()).getParameter("isbizperiod");// 否按实际业务日期入账

            CorpVO corpvo = corpService.queryByPk(pk_corp);
            if (StringUtil.isEmpty(pk_corp)) {
                pk_corp = SystemUtil.getLoginCorpId();
            } else {
                Set<String> powerCorpSet = userService.querypowercorpSet(userVo.getPrimaryKey());
                if (powerCorpSet == null || powerCorpSet.size() == 0 || !powerCorpSet.contains(pk_corp)) {
                    throw new BusinessException("无权操作");
                }

            }
//            String g_id = getRequest().getParameter("g_id");
            ImageLibraryVO il = gl_pzimageserv.uploadSingFileByFastTax(corpvo, userVo, infiles, filenames, g_id, period,
                    invoicedata,pjlx);
            json.setRows(il);
            json.setSuccess(true);
            json.setMsg("上传成功!");
        } catch (Exception e) {
            printErrorLog(json, e, "上传失败！");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_OTHERVOUCHER, "上传图片：期间" + period, ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }
    @PostMapping("/checkQjSyJz")
    public ReturnData<Json> checkQjSyJz(@RequestBody Map<String, String> param) {
        Json json = new Json();
        json.setSuccess(false);

        String period = null;
        // 格式2018-01
        try {

            String pk_corp = param.get("pk_corp");// 上传公司
            if (StringUtil.isEmpty(pk_corp)) {
                throw new BusinessException("公司缺失,请检查!");
            }

            List<String> cplist = new ArrayList<>();
            cplist.add(pk_corp);
            UserVO userVo = SystemUtil.getLoginUserVo();
            if (userVo == null) {
                throw new BusinessException("登录信息丢失,请重新登录!");
            }

            CorpVO corpvo = corpService.queryByPk(pk_corp);

            Set<String> powerCorpSet = userService.querypowercorpSet(userVo.getPrimaryKey());
            if (powerCorpSet == null || powerCorpSet.size() == 0 || !powerCorpSet.contains(pk_corp)) {
                throw new BusinessException("无权操作");
            }

            period = param.get("period");// 上传期间

            DZFDate dateq = null;
            DZFDate datez = null;
            if (StringUtil.isEmpty(period)) {

                if (corpvo == null) {
                    throw new BusinessException("该公司不存在！");
                }
                if (corpvo.getBegindate() == null) {
                    throw new BusinessException("公司:'" + deCodename(corpvo.getUnitname()) + "'的建账日期为空，可能尚未建账，请检查!");
                }
                dateq = corpvo.getBegindate();
                datez = new DZFDate();
            } else {
                dateq = DateUtils.getPeriodStartDate(period);
                datez = DateUtils.getPeriodStartDate(period);
            }
            List<QmclVO> list = gl_qmclserv.initquery(cplist, dateq, datez, SystemUtil.getLoginUserId(), new DZFDate(),
                    DZFBoolean.FALSE, DZFBoolean.FALSE);
            json.setRows(list);
            json.setSuccess(true);
            json.setMsg("成功!");
        } catch (Exception e) {
            printErrorLog(json, e, "失败！");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_OTHERVOUCHER, "期间损益校验：期间" + period, ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private String deCodename(String corpName) {
        String realName = "";
        try {
            realName = CorpSecretUtil.deCode(corpName);
        } catch (Exception e) {
            throw new WiseRunException(e);
        }
        return realName;
    }

    @PostMapping("/querySalaryReportData")
    public ReturnData<Grid> querySalaryReportData(@RequestBody Map<String, String> param) {
        Grid json = new Grid();
        json.setSuccess(false);
        // json.setStatus(-200);
        String period = null;// 上传期间
        try {

            period = param.get("period");// 上传期间

            String billtype = param.get("billtype");
            if (StringUtil.isEmpty(billtype))
                billtype = "01";

            String pk_corp = param.get("pk_corp");// 上传公司
            if (StringUtil.isEmpty(period)) {
                throw new BusinessException("上传期间缺失,请检查!");
            }
            if (StringUtil.isEmpty(pk_corp)) {
                throw new BusinessException("上传公司缺失,请检查!");
            }
            UserVO userVo = SystemUtil.getLoginUserVo();
            if (userVo == null) {
                throw new BusinessException("登录信息丢失,请重新登录!");
            }

            CorpVO corpvo = corpService.queryByPk(pk_corp);
            if (StringUtil.isEmpty(pk_corp)) {
                pk_corp = SystemUtil.getLoginCorpId();
            } else {
                Set<String> powerCorpSet = userService.querypowercorpSet(userVo.getPrimaryKey());
                if (powerCorpSet == null || powerCorpSet.size() == 0 || !powerCorpSet.contains(pk_corp)) {
                    throw new BusinessException("无权操作");
                }

            }
            SalaryReportVO[] vo = gl_gzbserv.query(pk_corp, period, billtype);// 查询工资表数据
            if (vo == null || vo.length == 0) {
                json.setTotal((long) 0);
            } else {
                json.setTotal((long) vo.length);
            }

            json.setSuccess(true);
            json.setMsg("上传成功!");
        } catch (Exception e) {
            printErrorLog(json, e, "上传失败！");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_OTHERVOUCHER, "上传图片：期间" + period, ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/queryOcrStateInfo")
    public ReturnData<Json> queryOcrStateInfo(@RequestBody Map<String, String> param) {
        Json json = new Json();
        json.setSuccess(false);
        try {
            String sourceids = param.get("sourceids");
            String pk_corp = param.get("pk_corp");

            // sourceids = "1";
            // pk_corp = "003Nyf";
            // if (StringUtil.isEmpty(invoicedata)) {
            // throw new BusinessException("扫描信息缺失,请检查!");
            // }
            log.info("扫描信息：" + sourceids);
            if (StringUtil.isEmpty(sourceids)) {
                throw new BusinessException("查询ids缺失,请检查!");
            }
            if (StringUtil.isEmpty(pk_corp)) {
                throw new BusinessException("查询公司缺失,请检查!");
            }

            UserVO userVo = SystemUtil.getLoginUserVo();
            if (userVo == null) {
                throw new BusinessException("登录信息丢失,请重新登录!");
            }

            CorpVO corpvo = corpService.queryByPk(pk_corp);
            if (StringUtil.isEmpty(pk_corp)) {
                pk_corp = SystemUtil.getLoginCorpId();
            } else {
                Set<String> powerCorpSet = userService.querypowercorpSet(userVo.getPrimaryKey());
                if (powerCorpSet == null || powerCorpSet.size() == 0 || !powerCorpSet.contains(pk_corp)) {
                    throw new BusinessException("无权操作");
                }
            }
            List<FastOcrStateInfoVO> list = gl_pzimageserv.getOcrStateInfoVOS(pk_corp, sourceids);
            json.setData(list);
            json.setSuccess(true);
            json.setMsg("查询成功!");
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败！");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_OTHERVOUCHER, "查询识别状态信息", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    /**
     * 一键报税使用，返回当前地区所有模板数据
     */
    @GetMapping("/queryTaxRptempvosbydq")
    public ReturnData<Json> queryTaxRptempvosbydq(String dq) {
        Json json = new Json();
        json.setSuccess(false);
        try {
//            String dq = getRequest().getParameter("dq");
            if (StringUtil.isEmpty(dq)) {
                json.setMsg("地区参数为空");
            } else {
                List<TaxRptTempletVO> list = fastTaxService.queryCorpRptTempletVOBydq(dq);
                json.setMsg("查询成功");
                json.setRows(list);
                json.setSuccess(true);
            }
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败！");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_OTHERVOUCHER, "查询地区报税模板信息", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }
}
