package com.dzf.zxkj.platform.controller.jzcl;

import com.dzf.zxkj.base.controller.PrintAndExcelExportController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.jzcl.QmGzBgSetVO;
import com.dzf.zxkj.platform.model.jzcl.QmGzBgVo;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.jzcl.IQmGzBgService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.pzgl.IPzglService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.itextpdf.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 期末关账
 */
@RestController
@RequestMapping("/gl/gl_qmgzact")
@Slf4j
@SuppressWarnings("all")
public class QmgzController  extends PrintAndExcelExportController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IQmgzService qmgzService;

    @Autowired
    private IPzglService gl_pzglserv;

    @Autowired
    private IQmGzBgService gl_qmgzbgserv;//期末关账检查报告

    @Autowired
    private IUserService userService;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @PostMapping("query")
    public ReturnData query(@MultiRequestBody QueryParamVO queryParamvo , @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO ) {
        Grid grid = new Grid();
        try {
            if(StringUtil.isEmpty(queryParamvo.getPk_corp())){
                throw new BusinessException("查询公司为空!");
            }
            Set<String> nnmnc = iUserService.querypowercorpSet(userVO.getCuserid());
            String corp = queryParamvo.getPk_corp();
            String corps[] = corp.split(",");
            for (int i = 0; i < corps.length; i++) {
                if (!nnmnc.contains(corps[i])) {
                    throw new BusinessException("操作无权限。");
                }
            }
            String userid = userVO.getCuserid();//(String) getSession().getAttribute(IGlobalConstants.login_user);
            DZFDate doped = new DZFDate(SystemUtil.getLoginDate());// new DZFDate((String) getSession().getAttribute(IGlobalConstants.login_date));
            QmclVO[] qmclVO = qmgzService.query(queryParamvo, userid, doped);
            getGrid(grid, "查询成功！", qmclVO, DZFBoolean.TRUE);
        } catch (Exception e) {
            getGrid(grid, "查询失败:" + e.getMessage(), new QmclVO[0], DZFBoolean.FALSE);
            printErrorLog(grid, e, "查询失败！");
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("saveSet")
    public ReturnData saveSet(@MultiRequestBody QmGzBgSetVO setvo, @MultiRequestBody UserVO userVO){
        Json grid = new Json();
        try {
            Set<String> nnmnc = iUserService.querypowercorpSet(userVO.getCuserid());
            if (!nnmnc.contains(setvo.getPk_corp())) {
                throw new BusinessException("操作无权限。");
            }
            gl_qmgzbgserv.saveQmgzBg(setvo, userVO.getCuserid());
            grid.setSuccess(true);
            grid.setMsg("操作成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "操作失败！");
        }

        return ReturnData.ok().data(grid);
    }

    @PostMapping("queryQmGzBg")
    public ReturnData queryQmGzBg(@MultiRequestBody QueryParamVO queryParamvo, @MultiRequestBody UserVO userVO) {
        Json grid = new Json();
        try {
            if(StringUtil.isEmpty(queryParamvo.getPk_corp())){
                throw new BusinessException("查询公司为空!");
            }
            Set<String> nnmnc = iUserService.querypowercorpSet(userVO.getCuserid());
            String corp = queryParamvo.getPk_corp();
            String corps[] = corp.split(",");
            for (int i = 0; i < corps.length; i++) {
                if (!nnmnc.contains(corps[i])) {
                    throw new BusinessException("操作无权限。");
                }
            }
            String period = queryParamvo.getQjq();
            String pk_corp = queryParamvo.getPk_corp();
            Map<String, List<QmGzBgVo>> gzmap = gl_qmgzbgserv.queryQmgzZb(pk_corp, period);
            grid.setSuccess(true);
            grid.setData(gzmap);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败！");
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 自动整理
     */
    @PostMapping("sort")
    public ReturnData sort(@MultiRequestBody QueryParamVO queryParamvo, @MultiRequestBody UserVO userVO,String[] companys ) {
        Json json = new Json();
//        String[] companys = getRequest().getParameterValues("companys[]");
        if (null == companys) {
            companys = new String[] {queryParamvo.getPk_corp()};
        }
        DZFDate bdate = DateUtils.getPeriodStartDate(queryParamvo.getQjq());
        DZFDate edate =DateUtils.getPeriodEndDate(queryParamvo.getQjq());
        String sort_type = "auto_number";
        String sort_label = "期末关账,自动整理";
        Set<String> corpSet = userService.querypowercorpSet(userVO.getCuserid());
        if (companys != null && companys.length > 0) {
            for (String pk_corp : companys) {
                if (!corpSet.contains(pk_corp)) {
                    json.setSuccess(false);
                    json.setMsg("整理失败,公司错误！");
                    return ReturnData.ok().data(json);
                }
            }
        }
        try {
            String msg = null;
//			if ("auto_date".equals(sort_type)) {
//				msg = gl_pzglserv.updateNumByDate(companys, bdate, edate);
//			}
            if ("auto_number".equals(sort_type)) {//按照凭证号顺次
                msg = gl_pzglserv.doVoucherOrder(companys, bdate, edate);
            }
            Map<String, List<QmGzBgVo>> gzmap = gl_qmgzbgserv.queryQmgzZb(queryParamvo.getPk_corp(), queryParamvo.getQjq());
//			if ("auto_uploadpic".equals(sort_type)) {
//				msg = gl_pzglserv.pzsortByuploadpic(companys, bdate, edate);
//			}
//			if("auto_churukubillcode".equals(sort_type)){
//				msg = gl_pzglserv.savechurukubillcodesort(companys, bdate, edate);
//			}
            json.setSuccess(true);
            json.setMsg(msg);
            json.setData(gzmap);
        } catch (Exception e) {
            printErrorLog(json, e, "整理失败!");
        }
//        writeLogRecord(LogRecordEnum.OPE_KJ_SETTLE.getValue(),
//                "凭证整理：" + DateUtils.getPeriod(bdate) + "到" + DateUtils.getPeriod(edate) + "，" + sort_label,
//                ISysConstants.SYS_2);

        return ReturnData.ok().data(json);
    }

    @GetMapping("phCheck")
    public ReturnData phCheck( String corpid,  String qj ) {
        Json grid = new Json();
        try {
            Set<String> nnmnc = iUserService.querypowercorpSet(SystemUtil.getLoginUserId());
            String corp = corpid;
            String corps[] = corp.split(",");
            for (int i = 0; i < corps.length; i++) {
                if (!nnmnc.contains(corps[i])) {
                    throw new BusinessException("操作无权限。");
                }
            }
            List<Object> phlist = qmgzService.gzCheck(corpid, qj);
            grid.setRows(phlist);
            grid.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(grid, e, "试算平衡失败！");
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("onGz")
    public ReturnData onGz(@MultiRequestBody QmclVO[] list) {
        Json grid = new Json();
        QmclVO[] bodyvos = list;
        String logmsg = "";
        //重复调用接口，公司+月份
        try {
            Map<String,List<QmclVO>> qmclmap = new HashMap<String,List<QmclVO>>();
            for(QmclVO votemp:bodyvos){
                String pk_corp = votemp.getPk_corp();
                if(qmclmap.containsKey(pk_corp)){
                    qmclmap.get(pk_corp).add(votemp);
                }else{
                    List<QmclVO> listtemp = new ArrayList<QmclVO>();
                    listtemp.add(votemp);
                    qmclmap.put(pk_corp, listtemp);
                }
            }
            StringBuffer tips = new StringBuffer();
            for(String str:qmclmap.keySet()){
                List<QmclVO> listtemp = qmclmap.get(str);
                QmclVO[] qmclvos = sortQmclByPeriod(listtemp,"asc");
                logmsg = getLogMsg("总账关账检查", qmclvos, "asc");
                for(QmclVO votemp:qmclvos){
                    try {
                        singleGz(votemp);
                    } catch (Exception e) {
                        tips.append(e.getMessage() +"<br>");
                        log.error("错误",e);
                    }
                }
            }

            if(tips.toString().length()>0){
                grid.setMsg(tips.toString());
            }else{
                grid.setMsg("1");
            }
            grid.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(grid, e, "关账失败！");
        }

//        writeLogRecord(LogRecordEnum.OPE_KJ_SETTLE.getValue(), logmsg, ISysConstants.SYS_2);

        return ReturnData.ok().data(grid);
    }

    @PostMapping("bgGz")
    public ReturnData bgGz(@MultiRequestBody QmclVO votemp ){
        Json grid = new Json();
        String logmsg = "";
        //重复调用接口，公司+月份
        try {
            singleGz(votemp);
            logmsg = getLogMsg("总账关账报告", new QmclVO[]{votemp}, "asc");
            grid.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(grid, e, "关账失败！");
        }

//        writeLogRecord(LogRecordEnum.OPE_KJ_SETTLE.getValue(), logmsg, ISysConstants.SYS_2);

//        writeJson(grid);
        return ReturnData.ok().data(grid);
    }

    /**
     * 单个vo的关账
     * @param votemp
     */
    private void singleGz(QmclVO votemp) {
        if(votemp.getIsgz()!=null && votemp.getIsgz().booleanValue()){
            throw new BusinessException(votemp.getCorpname()+"在期间"+votemp.getPeriod()+"已关账，请勿重复操作");
        }
        qmgzService.processGzOperate(votemp.getPk_corp(), votemp.getPeriod(), DZFBoolean.TRUE,SystemUtil.getLoginUserId());
    }

    private String getLogMsg(String ope,QmclVO[] qmclvos,String ident) {

        StringBuffer value = new StringBuffer();
        if(ident.equals("asc")){
            value.append(ope+":"+ qmclvos[0].getPeriod()+"~"+qmclvos[qmclvos.length-1].getPeriod());
        }else{
            value.append(ope+":"+ qmclvos[qmclvos.length-1].getPeriod()+"~"+qmclvos[0].getPeriod());
        }

        return value.toString();
    }

    private QmclVO[] sortQmclByPeriod(List<QmclVO> listtemp,final String ordervalue) {
        QmclVO[] qmclvos = listtemp.toArray(new QmclVO[0]);
        //先对集合排序
        java.util.Arrays.sort(qmclvos, new Comparator<QmclVO>() {
            public int compare(QmclVO o1, QmclVO o2) {
                int i =  0;
                if("desc".equals(ordervalue)){
                    if(o1.getPeriod().compareTo(o2.getPeriod()) >0){
                        i = -1;
                    }else if(o1.getPeriod().compareTo(o2.getPeriod()) ==0){
                        i= 0;
                    }else {
                        i= 1;
                    }
                }else{
                    i = o1.getPeriod().compareTo(o2.getPeriod());
                }
                return i;
            }
        });
        return qmclvos;
    }


    @PostMapping("fanGz")
    public ReturnData fanGz(String list ,String laterMonth, String funname) {
        Json grid = new Json();
        if(StringUtil.isEmpty(funname)){
            funname = "总账月末反关账";
        }
        QmclVO[] bodyvos = JsonUtils.deserialize(list, QmclVO[].class);
        String logmsg = "";
        try {
            if (bodyvos.length == 1 && "true".equals(laterMonth)) {
                QmclVO vo = bodyvos[0];
                if(vo.getIsgz()!=null && vo.getIsgz().booleanValue()){
                    qmgzService.cancelGzPeriodAndLater(bodyvos[0].getPk_corp(), bodyvos[0].getPeriod());
                    grid.setMsg("1");
                } else {
                    throw new BusinessException(vo.getCorpname() + "在期间" + vo.getPeriod() + "未关账，不能反关账");
                }
            } else {
                Map<String,List<QmclVO>> qmclmap = new HashMap<String,List<QmclVO>>();
                for(QmclVO votemp:bodyvos){
                    String pk_corp = votemp.getPk_corp();
                    if(qmclmap.containsKey(pk_corp)){
                        qmclmap.get(pk_corp).add(votemp);
                    }else{
                        List<QmclVO> listtemp = new ArrayList<QmclVO>();
                        listtemp.add(votemp);
                        qmclmap.put(pk_corp, listtemp);
                    }
                }
                StringBuffer tips = new StringBuffer();
                for(String str:qmclmap.keySet()){
                    List<QmclVO> listtemp = qmclmap.get(str);
                    QmclVO[] qmclvos = sortQmclByPeriod(listtemp,"asc");
                    logmsg =  getLogMsg(funname, qmclvos, "asc");
                    for(QmclVO votemp:qmclvos){
                        try {
                            if(votemp.getIsgz()!=null&&votemp.getIsgz().booleanValue()){
                                qmgzService.processGzOperate(votemp.getPk_corp(), votemp.getPeriod(), DZFBoolean.FALSE,SystemUtil.getLoginUserId());
                            }else{
                                throw new BusinessException(votemp.getCorpname()+"在期间"+votemp.getPeriod()+"未关账，不能反关账");
                            }
                        } catch (Exception e) {
                            tips.append(e.getMessage() +"<br>");
                            log.error("错误",e);
                        }
                    }
                }
                if(tips.toString().length()>0){
                    grid.setMsg(tips.toString());
                }else{
                    grid.setMsg("1");
                }
            }
            grid.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(grid, e, "反关账失败！");
        }

//        writeLogRecord(LogRecordEnum.OPE_KJ_SETTLE.getValue(), logmsg, ISysConstants.SYS_2);

        return ReturnData.ok().data(grid);
    }

    /**
     * 打印操作
     */
    @PostMapping("print/pdf")
    public void printAction(String corpName, String period, PrintParamVO printParamVO, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            QmclVO[] bodyvos =JsonUtils.deserialize(printParamVO.getList(),QmclVO[].class) ;
            Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            tmap.put("期间", printParamVO.getTitleperiod());
            printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "关 账 检 查",
                    new String[] { "period", "corpname", "isgz" }, new String[] { "期间", "公司", "关账完成" },
                    new int[] { 2, 3, 2 }, 20, pmap,tmap);
        } catch (DocumentException e) {
            log.error("打印错误",e);
        } catch (IOException e) {
            log.error("打印错误",e);
        }
    }

    public QueryParamVO getQueryParamVO(  QueryParamVO paramvo) {
        // 把字符串变成codelist集合
        if (paramvo.getPk_corp() != null && paramvo.getPk_corp().length() > 0) {
            List<String> codelist = Arrays.asList(paramvo.getPk_corp().split(","));
            paramvo.setCorpslist(codelist);
        }
//        UserVO uservo = new UserVO();
//        uservo.setCuserid((String) getSession().getAttribute(IGlobalConstants.login_user));
//        paramvo.setUservo(uservo);
        return paramvo;
    }

    private void getGrid(Grid grid, String msg, QmclVO[] jzvos, DZFBoolean issuccess) {
        // log.info(msg);
        grid.setMsg(msg);
        grid.setTotal((long) (jzvos == null ? 0 : jzvos.length));
        grid.setRows(jzvos == null ? new ArrayList<QmclVO>() : Arrays.asList(jzvos));
        grid.setSuccess(issuccess.booleanValue());
    }

    @PostMapping("checkLaterMonthGz")
    public ReturnData checkLaterMonthGz (@MultiRequestBody QmclVO data) {
        boolean isGz = false;
        try {
            isGz = qmgzService.checkLaterMonthGz(data.getPk_corp(), data.getPeriod());
        } catch (Exception e) {
            log.error("检查期间后月份关账失败！", e);
        }
        return ReturnData.ok().data(isGz);
    }

}
