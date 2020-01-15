package com.dzf.zxkj.platform.controller.jzcl;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.IQmclConstant;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.exception.ExBusinessException;
import com.dzf.zxkj.platform.model.bdset.AdjustExrateVO;
import com.dzf.zxkj.platform.model.bdset.ExrateVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.jzcl.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.glic.impl.CheckInventorySet;
import com.dzf.zxkj.platform.service.jzcl.IQmclNoicService;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.jzcl.IYJJZService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/gl/gl_yjjzact")
@Slf4j
public class YJJZController extends BaseController {

    @Autowired
    private IYJJZService gl_yjjzserv;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IQmclService gl_qmclserv;
    @Autowired
    private IQmclNoicService gl_qmclnoicserv;
    @Autowired
    private IQmgzService qmgzService;
    @Autowired
    private ICorpService corpService;
    @Autowired
    private IInventoryAccSetService gl_ic_invtorysetserv;
    @Autowired
    private CheckInventorySet inventory_setcheck;

    @PostMapping("/query")
    public ReturnData<Grid> query(@MultiRequestBody QueryParamVO queryParamvo) {
        Grid grid = new Grid();
        List<String> corppks = queryParamvo.getCorpslist();
        if (corppks == null || corppks.size() == 0) {
            throw new BusinessException("查询公司为空！");
        }
        try {
//            if (StringUtil.isEmpty(queryParamvo.getPk_corp())) {
//                throw new BusinessException("查询公司为空！");
//            }

            String userid = SystemUtil.getLoginUserId();
            Set<String> nnmnc = iUserService.querypowercorpSet(userid);
//            String corp = queryParamvo.getPk_corp();
//            String corps[] = corp.split(",");
            String[] corps = corppks.toArray(new String[0]);
            for (int i = 0; i < corps.length; i++) {
                if (nnmnc == null ||  !nnmnc.contains(corps[i])) {
                    throw new BusinessException("不包含该公司！");
                }
            }
            List<YjjzVO> list = gl_yjjzserv.query(corppks, queryParamvo.getBegindate1(), queryParamvo.getEnddate(),
                    userid, SystemUtil.getLoginDate());
            if(list!=null&&list.size()>0){
                Map<String,List<SetJz>> mapjz = new HashMap<String, List<SetJz>>();
                List<QmclVO> qmlist = queryqmclData(queryParamvo);
                Map<String,QmclVO> maps = DZfcommonTools.hashlizeObjectByPk(qmlist, new String[]{"pk_corp","period"});
                String key = "";
                for(YjjzVO v : list){
                    key = v.getPk_corp()+","+v.getPeriod();
                    QmclVO qmvo = maps.get(key);
                    v.setPk_qmcl(qmvo.getPk_qmcl());
                    v.setIsjz(new DZFBoolean(setJZZT(mapjz, v.getPk_corp(), userid, qmvo)));
                    v.setCorpname(CodeUtils1.deCode(v.getCorpname()));
                }
            }
            grid.setRows(list);
            grid.setSuccess(true);
            grid.setMsg("查询成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            grid.setRows(new ArrayList<YjjzVO>());
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败！");
        }

        return ReturnData.ok().data(grid);
    }

    //查询设置一键结转状态
    public boolean setJZZT(Map<String,List<SetJz>> mapjz,String pk_corp,String userid,QmclVO qmvo){
        List<SetJz> listsets = getSetJzlist(mapjz,pk_corp,userid);
        boolean isjz = false;
        for(SetJz jz : listsets){
            switch(Integer.valueOf(jz.getId())){
                case 1 : {isjz = qmvo.getIshdsytz() == null ? false : qmvo.getIshdsytz().booleanValue();break;}
                case 2 : {isjz = qmvo.getIscbjz() == null ? false : qmvo.getIscbjz().booleanValue();break;}
                case 3 : {isjz = qmvo.getIszjjt() == null ? false : qmvo.getIszjjt().booleanValue();break;}
                case 4 : {isjz = qmvo.getZzsjz() == null ? false : qmvo.getZzsjz().booleanValue();break;}
                case 5 : {isjz = qmvo.getIsjtsj() == null ? false : qmvo.getIsjtsj().booleanValue();break;}
                case 6 : {isjz = qmvo.getQysdsjz() == null ? false : qmvo.getQysdsjz().booleanValue();break;}
                case 7 : {isjz = qmvo.getIsqjsyjz() == null ? false : qmvo.getIsqjsyjz().booleanValue();break;}
            }
            if(!isjz)break;
        }
        return isjz;
    }

    public List<QmclVO> queryqmclData(QueryParamVO queryParamvo){
        String userid = SystemUtil.getLoginUserId();
        DZFDate logindate = new DZFDate(SystemUtil.getLoginDate());
        DZFBoolean iscarover = queryParamvo.getIscarover();
        DZFBoolean isuncarover = queryParamvo.getIsuncarover();
        List<String> corppks = queryParamvo.getCorpslist();
        if (corppks == null || corppks.size() == 0) {
            throw new BusinessException("查询公司为空!");
        }
        List<QmclVO> list = gl_qmclserv.initquery(corppks, queryParamvo.getBegindate1(), queryParamvo.getEnddate(),
                userid, logindate, iscarover, isuncarover);
        return list;
    }

    private List<SetJz> getSetJzlist(Map<String,List<SetJz>> mapjz,String pk_corp,String userid){
        List<SetJz> list = null;
        String[][] jzresult = getJzresult(userid,pk_corp);
        if(mapjz.containsKey(pk_corp)){
            list = mapjz.get(pk_corp);
        }else{
            YJJZSetVO vo = gl_yjjzserv.queryset(userid, pk_corp);
            list = getCorpSetjz(vo,jzresult);
            mapjz.put(pk_corp, list);
        }
        return list;
    }

    private String[][] getJzresult(String userid,String pk_corp){

        return new String[][]{
                {"1","期末调汇","N"},
                {"2","成本结转","N"},
                {"3","计提折旧","Y"},
                {"4","增值税结转","Y"},
                {"5","计提附加税","Y"},
                {"6","计提所得税","Y"},
                {"7","损益结转","Y"}};
    }

    private List<SetJz> getCorpSetjz(YJJZSetVO vo,String[][] jzresult){
        List<SetJz> list = new ArrayList<SetJz>();
        if(vo != null){
            if(!StringUtil.isEmpty(vo.getResult1())){
                String[] res = vo.getResult1().split(",");
                for(int i=0;i<jzresult.length;i++){
                    SetJz jz = new SetJz();
                    String id = jzresult[i][0];
                    for(String s : res){
                        if(id.equals(s)){
                            jz.setCk("Y");
                            break;
                        }
                    }
                    jz.setId(jzresult[i][0]);
                    jz.setText(jzresult[i][1]);
                    if("Y".equals(jz.getCk())){
                        list.add(jz);
                    }
                }
            }else{
                getDefaultjz(list,jzresult);
            }
        }else{
            getDefaultjz(list,jzresult);
        }
        return list;
    }

    public void getDefaultjz(List<SetJz> list,String[][] jzresult){
        if(list == null)
            list = new ArrayList<SetJz>();
        for(int i=0;i<jzresult.length;i++){
            if("Y".equals(jzresult[i][2])){
                SetJz jz = new SetJz();
                jz.setId(jzresult[i][0]);
                jz.setText(jzresult[i][1]);
                jz.setCk("Y");
                list.add(jz);
            }
        }
    }

    /**
     * 查询一键结转设置
     */
    @GetMapping("/queryset")
    public ReturnData<Grid> queryset(String corpid) {
        Grid grid = new Grid();
        String userid = SystemUtil.getLoginUserId();
        String pk_corp = corpid;
        try {
            if (StringUtil.isEmpty(pk_corp)) {
                throw new BusinessException("查询公司为空！");
            }
            pk_corp = pk_corp.split(",")[0];
            YJJZSetVO vo = gl_yjjzserv.queryset(userid, pk_corp);
            List<SetJz> list = createSetjz(vo,pk_corp);
            grid.setRows(list);
            grid.setSuccess(true);
            grid.setMsg("查询成功！");
        } catch (Exception e) {
            log.error("错误",e);
            grid.setRows(new ArrayList<YJJZSetVO>());
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败！");
        }
        return ReturnData.ok().data(grid);
    }

    public List<SetJz> createSetjz(YJJZSetVO vo,String pk_corp){
        List<SetJz> list = new ArrayList<SetJz>();
        String[][] jzresult = getJzresult(SystemUtil.getLoginUserId(),pk_corp);
        if(vo != null){
            if(!StringUtil.isEmpty(vo.getResult1())){
                String[] res = vo.getResult1().split(",");
                for(int i=0;i<jzresult.length;i++){
                    SetJz jz = new SetJz();
                    String id = jzresult[i][0];
                    for(String s : res){
                        if(id.equals(s)){
                            jz.setCk("Y");
                            break;
                        }else{
                            jz.setCk("N");
                        }
                    }
                    jz.setId(jzresult[i][0]);
                    jz.setText(jzresult[i][1]);
                    list.add(jz);
                }
            }else{
                createDefaultjz(list,jzresult);
            }
        }else{
            createDefaultjz(list,jzresult);
        }
        return list;
    }

    /**
     * 这里控制一下，小规模纳税人，不显示增值税结转
     * @param list
     */
    public void createDefaultjz(List<SetJz> list,String[][] jzresult){
        if(list == null)
            list = new ArrayList<SetJz>();
        for(int i=0;i<jzresult.length;i++){
            SetJz jz = new SetJz();
            jz.setId(jzresult[i][0]);
            jz.setText(jzresult[i][1]);
            jz.setCk(jzresult[i][2]);
            list.add(jz);
        }
    }

    /**
     * 保存一键结转设置
     */
    @PostMapping("/savejzSet")
    public ReturnData<Grid> savejzSet(@MultiRequestBody("corpid")  String pk_corp,
                                      @MultiRequestBody("res")  SetJz[] bodyvos){
        Grid grid = new Grid();
        try {
            if (StringUtil.isEmpty(pk_corp))
                throw new BusinessException("查询公司为空！");
            if(bodyvos == null || bodyvos.length == 0)
                throw new BusinessException("请选择数据，保存失败！");
            String z = "";
            for(SetJz jz : bodyvos){
                if(z.length()>0){
                    z = z +","+jz.getId();
                }else{
                    z = jz.getId();
                }
            }
            List<YJJZSetVO> zlist = new ArrayList<YJJZSetVO>();
            String[] args = pk_corp.split(",");
            for(String pkgs : args){
                if(!StringUtil.isEmpty(pkgs)){
                    YJJZSetVO vo = new YJJZSetVO();
                    vo.setPk_corp(pkgs);
                    vo.setResult1(z);
                    zlist.add(vo);
                }
            }
            gl_yjjzserv.savejzset(zlist);
            grid.setSuccess(true);
            grid.setMsg("保存成功！");
        } catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<YJJZSetVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "保存失败！");
        }
        return ReturnData.ok().data(grid);
    }


    // 取消一键结转
    @PostMapping("/cancelYjjz")
    public ReturnData<Grid> cancelYjjz(@MultiRequestBody("qmvos")  QmclVO[] qmvos, @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        StringBuffer sf = new StringBuffer();
        try {
            YjjzReturnVO returnvo = new YjjzReturnVO();
            String userid = userVO.getCuserid();
            List<YjjzOperateVO> list = checkcancelQmvos(qmvos,userid);
            if(list == null || list.size() == 0){
                returnvo.setSuccess(false);
                returnvo.setIsreturn(true);
                returnvo.getAllmessage().append("<font color='red'>取消失败，请检查所选数据是否关账，或者是否有操作公司权限！</font><br>");
                grid.setMsg("取消一键结转失败！");
                grid.setRows(returnvo);
                grid.setSuccess(false);
                return ReturnData.ok().data(grid);
            }
            for(int i = list.size()-1 ;i >= 0 ;i--){//倒序执行cancelYjjz
                YjjzOperateVO svo = list.get(i);
                onjzcancel(svo,returnvo);
            }
            sf.append("取消一键结转成功");
            grid.setMsg("取消一键结转成功！");
            grid.setRows(returnvo);
            grid.setSuccess(true);
        }catch (Exception e) {
            log.error("错误",e);
            YjjzReturnVO returnvo = new YjjzReturnVO();
            returnvo.setSuccess(false);
            returnvo.setIsreturn(true);
            returnvo.getAllmessage().append("<font color='red'>"+ (e instanceof BusinessException ? e.getMessage():"取消一键结转失败！")+"</font><br>");
            grid.setSuccess(false);
            grid.setRows(returnvo);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "取消一键结转失败！");
        }
        // 日志操作
        doRecord(sf);
        return ReturnData.ok().data(grid);
    }


    /**
     * 取消动作：：：：判断当前选中qmvos是否关账、公司数据是否合法
     */
    private List<YjjzOperateVO> checkcancelQmvos(QmclVO[] qmvos,String userid){
        if(qmvos == null || qmvos.length == 0)
            return null;
        List<YjjzOperateVO> yjjzvoList = new ArrayList<YjjzOperateVO>();
        Map<String,List<SetJz>> mapjz = new HashMap<String, List<SetJz>>();
        Set<String> nnmnc = new HashSet<String>();
        for(int i = 0 ;i< qmvos.length;i++){
            QmclVO qmvo = qmvos[i];
            qmvo = gl_qmclnoicserv.queryById(qmvo.getPk_qmcl());//重新查询qmclvo信息
            String pk_corp = qmvo.getPk_corp();
            qmvo.setCoperatorid(userid);//设置操作人
            //查询当前公司一键结转设置
            List<SetJz> listsets = getSetJzlist(mapjz,pk_corp,userid);
            CorpVO pvo = corpService.queryByPk(pk_corp);
            YjjzOperateVO atvo = addOperatevo(pvo,listsets,pk_corp,qmvo,userid);
            //检验: 1、公司数据是否合法。
            boolean isgon = checkCorpower(pk_corp,nnmnc,pvo,atvo,qmvo,userid);
            //校验：2、是否关账。
            if(isgon){
                isgon = checkguanzhang(pk_corp,qmvo.getPeriod(),pvo,atvo);
            }
            if(isgon){//取消动作这里也不一样的。
                yjjzvoList.add(atvo);
            }
        }
        return yjjzvoList;
    }

    private YjjzOperateVO addOperatevo(CorpVO pvo,List<SetJz> listsets,String pk_corp,QmclVO qmvo,String userid){
        YjjzOperateVO atvo = new YjjzOperateVO();
        atvo.setCorpvo(pvo);
        atvo.setJzsets(listsets);
        atvo.setPk_corp(pk_corp);
        atvo.setQmvo(qmvo);
        atvo.setUserid(userid);
        return atvo;
    }

    private boolean checkCorpower(String pk_corp,Set<String> nnmnc,CorpVO pvo,YjjzOperateVO atvo,QmclVO qmvo,String userid){
        if(nnmnc==null || nnmnc.size() == 0){
            nnmnc = iUserService.querypowercorpSet(userid);
        }
        if (!nnmnc.contains(pk_corp)) {
            atvo.setIsgoonjz(false);
            atvo.setOperatemsg("<font color='red'>无权限操作！</font>");
        }
        return atvo.isIsgoonjz();
    }

    /**
     * 判断是否关账
     */
    private boolean checkguanzhang(String pk_corp,String period,CorpVO pvo,YjjzOperateVO atvo){
        boolean isgz = qmgzService.isGz(pk_corp, period);
        if(isgz){
            atvo.setIsgoonjz(false);
            atvo.setOperatemsg("<font color='red'>已经关账！</font>");
        }
        return atvo.isIsgoonjz();
    }

    private void onjzcancel(YjjzOperateVO atvo,YjjzReturnVO returnvo){
        if(atvo == null)
            return;
        List<SetJz> listjz = atvo.getJzsets();
        QmclVO qmvo = atvo.getQmvo();
        CorpVO rpvo = atvo.getCorpvo();
        String userid = atvo.getUserid();
        StringBuffer sbf = new StringBuffer();//一条期末结转记录对应【结转项目】的结转结果
        sbf.append(getCorperiodName(rpvo, userid, qmvo));
        for(int i = listjz.size()-1 ;i >= 0 ;i--){//倒序执行
            SetJz jzset = listjz.get(i);
            String idx = jzset.getId();
            int index = Integer.valueOf(idx);
            onyjjzcancel(index,qmvo,rpvo,returnvo,sbf);
        }
        returnvo.getAllmessage().append(sbf.toString()+"<br>");
    }

    /**
     * 返回公司+期间
     */
    private String getCorperiodName(CorpVO corp,String userid,QmclVO qmvo){
        String unitname = "";
        if(corp == null){
            corp = corpService.queryByPk(qmvo.getPk_corp());
            unitname = CodeUtils1.deCode(corp.getUnitname());
        }else{
            unitname = corp.getUnitname();
        }
        return "公司:"+unitname+"，期间:"+qmvo.getPeriod()+"，";
    }

    private void onyjjzcancel(int index,QmclVO qmvo,CorpVO rpvo,YjjzReturnVO returnvo,StringBuffer sbf){
        try{
            switch(index){
                case 1://期末调汇
                {
                    if(qmvo.getIshdsytz()!=null && qmvo.getIshdsytz().booleanValue()){
                        gl_qmclserv.updateFanHuiDuiSunYiTiaoZheng(qmvo);
                    }
                    sbf.append(getSigname(index)+"取消成功！");
                    break;
                }
                case 2://成本结转
                {
                    if(qmvo.getIscbjz()!=null && qmvo.getIscbjz().booleanValue()){
                        gl_qmclserv.rollbackCbjz(qmvo);
                    }
                    sbf.append(getSigname(index)+"取消成功！");
                    break;
                }
                case 3://计提折旧
                {
                    if(qmvo.getIszjjt()!=null && qmvo.getIszjjt().booleanValue()){
                        gl_qmclserv.updateFanJiTiZheJiu(qmvo);
                    }
                    sbf.append(getSigname(index)+"取消成功！");
                    break;
                }
                case 4://增值税结转
                {
                    if(qmvo.getZzsjz()!=null && qmvo.getZzsjz().booleanValue()){
                        gl_qmclserv.onfzzsjz(qmvo);
                    }
                    sbf.append(getSigname(index)+"取消成功！");
                    break;
                }
                case 5://计提附加税
                {
                    if(qmvo.getIsjtsj()!=null && qmvo.getIsjtsj().booleanValue()){
                        gl_qmclserv.updateFanJiTiShuiJin(qmvo);
                    }
                    sbf.append(getSigname(index)+"取消成功！");
                    break;
                }
                case 6://所得税计提
                {
                    if(qmvo.getQysdsjz()!=null && qmvo.getQysdsjz().booleanValue()){
                        gl_qmclserv.onfsdsjz(qmvo);
                    }
                    sbf.append(getSigname(index)+"取消成功！");
                    break;
                }
                case 7://期间损益
                {
                    if(qmvo.getIsqjsyjz()!=null && qmvo.getIsqjsyjz().booleanValue()){
                        gl_qmclserv.updateFanQiJianSunYiJieZhuan(qmvo);
                    }
                    sbf.append(getSigname(index)+"取消成功！");
                    break;
                }
                default:
                    break;

            }
        } catch (Exception e) {
            printErrorLog(index,sbf, e, "操作失败!");
        }
    }

    protected void printErrorLog(int index,StringBuffer sbf,Throwable e,String errorinfo){
        if(StringUtil.isEmpty(errorinfo))
            errorinfo = "<font color='red'>操作失败</font>";
        if(e instanceof BusinessException){
            sbf.append("<font color='red'>"+getSigname(index)+","+e.getMessage()+"</font>");
        }else{
            sbf.append("<font color='red'>"+getSigname(index)+","+errorinfo+"</font>");
            log.error(errorinfo,e);
        }
    }

    /**
     * 返回个性签名
     */
    private String getSigname(int index){
        String sign = "";
        switch(index){
            case 1:sign = "期末调汇";break;
            case 2:sign = "成本结转";break;
            case 3:sign = "计提折旧";break;
            case 4:sign = "增值税结转";break;
            case 5:sign = "计提附加税";break;
            case 6:sign = "计提所得税";break;
            case 7:sign = "损益结转";break;
        }
        return sign;
    }

    // 开始一键结转
    @PostMapping("/onYjjz")
    public ReturnData<Grid> onYjjz(@MultiRequestBody("exrates")  AdjustExrateVO[] exrates,
                                   @MultiRequestBody("qmclvos")  QmclVO[]  qmclvos,
                                   @MultiRequestBody("zgdata")   TempInvtoryVO[] zgdata,
                                   @MultiRequestBody("qmjznoiclist")  QMJzsmNoICVO[] qmjznoiclist,
                                   @MultiRequestBody("currentproject")  int currentproject,
                                   @MultiRequestBody("list1")  CostForwardVO[] list1,
                                   @MultiRequestBody("list2")  CostForwardVO[] list2,
                                   @MultiRequestBody("list3")  CostForwardVO[] list3,
                                   @MultiRequestBody("list4")  CostForwardInfo[] list4,
                                   @MultiRequestBody("list5")  CostForwardVO[] list5,
                                   @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        StringBuffer sf = new StringBuffer();
        try {
            YjjzPamterVO pamtervo = new YjjzPamterVO();
            pamtervo.setExrates(exrates);
            pamtervo.setQmclvos(qmclvos);
            pamtervo.setZgdata(zgdata);
            pamtervo.setQmjznoiclist(qmjznoiclist != null && qmjznoiclist.length > 0 ? new ArrayList<QMJzsmNoICVO>(Arrays.asList(qmjznoiclist)): null);
            pamtervo.setCurrentproject(currentproject);
            pamtervo.setList1(list1 != null && list1.length > 0 ? new ArrayList<CostForwardVO>(Arrays.asList(list1)): null);
            pamtervo.setList2(list2 != null && list2.length > 0 ? new ArrayList<CostForwardVO>(Arrays.asList(list2)): null);
            pamtervo.setList3(list3 != null && list3.length > 0 ? new ArrayList<CostForwardVO>(Arrays.asList(list3)): null);
            pamtervo.setList4(list4);
            pamtervo.setList5(list5 != null && list5.length > 0 ? new ArrayList<CostForwardVO>(Arrays.asList(list5)): null);
            //
            YjjzReturnVO returnvo = new YjjzReturnVO();
            String userid = userVO.getCuserid();
            List<YjjzOperateVO> list = checkQmvosData(pamtervo.getQmclvos(),userid);
            if(list == null || list.size() == 0){
                returnvo.setSuccess(false);
                returnvo.setIsreturn(true);
                returnvo.getAllmessage().append("<font color='red'>一键结转数据为空！</font><br>");
                grid.setMsg("一键结转失败！");
                grid.setRows(returnvo);
                grid.setSuccess(false);
                return ReturnData.ok().data(grid);
            }
            for(int i = 0 ;i<list.size();i++){
                YjjzOperateVO svo = list.get(i);
                QmclVO qmvo = svo.getQmvo();
                CorpVO mvo = svo.getCorpvo();
                //是否可以继续结转
                if(!svo.isIsgoonjz()){
                    returnvo.getAllmessage().append(getCorperiodName(mvo,userid,qmvo)+svo.getOperatemsg()+"<br>");
                    continue;
                }
                onjzorder(svo,returnvo,pamtervo,userid);
                if(returnvo.isIsreturn())
                    break;
            }
            grid.setMsg("一键结转成功！");
            grid.setRows(returnvo);
            if(!returnvo.isIsreturn()){
                sf.append("一键结转成功");
            }
            grid.setSuccess(true);
        }catch (Exception e) {
            log.error("错误",e);
            YjjzReturnVO returnvo = new YjjzReturnVO();
            returnvo.setSuccess(false);
            returnvo.setIsreturn(true);
            returnvo.getAllmessage().append("<font color='red'>"+ (e instanceof BusinessException ? e.getMessage():"一键结转失败！")+"</font><br>");
            grid.setSuccess(false);
            grid.setRows(returnvo);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "一键结转失败！");
        }
        // 日志记录
        doRecord(sf);
        return ReturnData.ok().data(grid);
    }

    /**
     * 判断当前选中qmvos是否支持批量合法、是否关账、公司数据是否合法
     */
    private List<YjjzOperateVO> checkQmvosData(QmclVO[] qmvos,String userid){
        if(qmvos == null || qmvos.length == 0)
            return null;
        List<YjjzOperateVO> yjjzvoList = new ArrayList<YjjzOperateVO>();
        Map<String,List<SetJz>> mapjz = new HashMap<String, List<SetJz>>();
        Set<String> nnmnc = new HashSet<String>();
        for(int i = 0 ;i< qmvos.length;i++){
            QmclVO qmvo = qmvos[i];
            qmvo = gl_qmclnoicserv.queryById(qmvo.getPk_qmcl());//重新查询qmclvo信息
            String pk_corp = qmvo.getPk_corp();
            qmvo.setCoperatorid(userid);//设置操作人
            //查询当前公司一键结转设置
            List<SetJz> listsets = getSetJzlist(mapjz,pk_corp,userid);
            CorpVO pvo = corpService.queryByPk(pk_corp);
            YjjzOperateVO atvo = addOperatevo(pvo,listsets,pk_corp,qmvo,userid);
            //检验: 1、公司数据是否合法。
            boolean isgon = checkCorpower(pk_corp,nnmnc,pvo,atvo,qmvo,userid);
            //校验：2、是否批量。
            if(isgon){
                isgon = checksupportbatch(qmvos.length>1,listsets,pvo,atvo,qmvo,userid);
            }
            //校验：3、是否关账。
            if(isgon){
                isgon = checkguanzhang(pk_corp,qmvo.getPeriod(),pvo,atvo);
            }
            //校验 ：4、校验是否存在未识别的凭证 高志新
            if(isgon){
                isgon = checkIssb(pk_corp,qmvo.getPeriod(),atvo);
            }
            //校验：5、校验总账存货数据是否合法
            if(isgon){
                isgon = checkIsGlicSet(userid,pk_corp,atvo);
            }
            yjjzvoList.add(atvo);
        }
        return yjjzvoList;
    }

    private boolean checksupportbatch(boolean ismore,List<SetJz> listsets,CorpVO pvo,YjjzOperateVO atvo,QmclVO qmvo,String userid){
        if(!ismore){//界面仅选择一行操作
            atvo.setIsgoonjz(true);
            return atvo.isIsgoonjz();
        }
        String[][] jzresult = getJzresult(userid,qmvo.getPk_corp());
        for(SetJz jz : listsets){
            if(jzresult[0][0].equals(jz.getId())){//汇兑损益
                atvo.setIsgoonjz(false);
                atvo.setOperatemsg("<font color='red'>汇兑损益不允许多公司、多期间批量操作！</font>");
                break;
            }
            if(jzresult[1][0].equals(jz.getId())
                    && pvo.getIcostforwardstyle()!=null
                    && (pvo.getIcostforwardstyle() == 2
                    || pvo.getIcostforwardstyle() == 3)){//成本
                atvo.setIsgoonjz(false);
                atvo.setOperatemsg("<font color='red'>商贸成本、工业成本不允许多公司、多期间批量操作！</font>");
                break;
            }
            atvo.setIsgoonjz(true);
        }
        return atvo.isIsgoonjz();
    }

    private boolean checkIssb(String pk_corp, String period, YjjzOperateVO atvo) {
        try{
            gl_qmclserv.checkTemporaryIsExist(pk_corp,period,true,"不能一键结转!");
        }catch(Exception e){
            atvo.setIsgoonjz(false);
            atvo.setOperatemsg("<font color='red'>"+e.getMessage()+"</font>");
        }
        return atvo.isIsgoonjz();
    }


    private boolean checkIsGlicSet(String userid,String pk_corp, YjjzOperateVO atvo){
        InventorySetVO setvo = gl_ic_invtorysetserv.query(pk_corp);
        String error = inventory_setcheck.checkInventorySet(userid, pk_corp,setvo);
        if (!StringUtil.isEmpty(error)) {
            error = error.replaceAll("<br>", " ");
            atvo.setIsgoonjz(false);
            atvo.setOperatemsg("<font color='red'>"+error+"</font>");
        }
        return atvo.isIsgoonjz();
    }


    private void onjzorder(YjjzOperateVO atvo,YjjzReturnVO returnvo,YjjzPamterVO pamtervo,String userid){
        if(atvo == null)
            return;
        List<SetJz> listjz = atvo.getJzsets();
        QmclVO qmvo = atvo.getQmvo();
        CorpVO rpvo = atvo.getCorpvo();
        StringBuffer sbf = new StringBuffer();//一条期末结转记录对应【结转项目】的结转结果
        sbf.append(getCorperiodName(rpvo, atvo.getUserid(), qmvo));
        for(SetJz jzset : listjz){
            String idx = jzset.getId();
            int index = Integer.valueOf(idx);
            onyjjzselect(index,qmvo,rpvo,returnvo,pamtervo,sbf,userid);
            if(returnvo.isIsreturn()){
                break;
            }
        }
        returnvo.getAllmessage().append(sbf.toString()+"<br>");
    }

    /**
     * 说明:
     * @param returnvo  这里returnvo仅记录中途返回前台的错误信息，全局提示信息放到上层方法中调用
     * @param sbf       不允许增加换行标识符，但增加结转类型的个性签名，比如【汇兑调整】
     */
    private void onyjjzselect(int index,QmclVO qmvo,CorpVO rpvo,YjjzReturnVO returnvo,YjjzPamterVO pamtervo,StringBuffer sbf,String userid){
        //当前执行步骤顺序判断
        int currnet = pamtervo.getCurrentproject();
        if(index < currnet)
            return;
        try{
            switch(index){
                case 1://期末调汇
                {
                    if(qmvo.getIshdsytz()==null || !qmvo.getIshdsytz().booleanValue()){
                        if(pamtervo.getExrates() == null || pamtervo.getExrates().length == 0){
                            ExrateVO[] ratevos = gl_qmclserv.queryAdjust(qmvo);
                            if(ratevos == null || ratevos.length == 0){
                                gl_qmclserv.updatehdsyzt(qmvo,userid);
                                break;
                            }
                            returnvo.setSuccess(true);
                            returnvo.setIsreturn(true);
                            returnvo.setCurrentproject(index);
                            returnvo.setListrate(ratevos);
                            returnvo.setBranchmsg(getSigname(index)+"损益调整查询成功！");
                            returnvo.setStatuscode(StatusCode.statuscode2);
                        }else{
                            Map<String, AdjustExrateVO> map  = buildadjustratevo(pamtervo.getExrates());
                            if(map!=null && map.size()>0){
                                gl_qmclserv.updateHuiDuiSunYiTiaoZheng(qmvo, map,userid);
                            }else{//期末调汇状态
                                gl_qmclserv.updatehdsyzt(qmvo,userid);
                            }
                        }
                    }
                    sbf.append(getSigname(index)+"完成！");
                    break;
                }
                case 2://成本结转
                {
                    if(qmvo.getIscbjz()==null || !qmvo.getIscbjz().booleanValue()){
                        qmvo.setZgdata(pamtervo.getZgdata());//设置暂估数据
                        cbjz(index,qmvo,rpvo,returnvo,currnet,pamtervo,userid);
                    }
                    sbf.append(getSigname(index)+"完成！");
                    break;
                }
                case 3://计提折旧
                {
                    if(qmvo.getIszjjt()==null || !qmvo.getIszjjt().booleanValue()){
                        gl_qmclserv.updateJiTiZheJiu(qmvo,userid);
                    }
                    sbf.append(getSigname(index)+"完成！");
                    break;
                }
                case 4://增值税结转
                {
                    if(qmvo.getZzsjz()==null || !qmvo.getZzsjz().booleanValue()){
                        gl_qmclserv.onzzsjz(userid, qmvo);
                    }
                    sbf.append(getSigname(index)+"完成！");
                    break;
                }
                case 5://计提附加税
                {
                    if(qmvo.getIsjtsj()==null || !qmvo.getIsjtsj().booleanValue()){
                        gl_qmclserv.updateJiTiShuiJin(qmvo, rpvo.getCorptype(), rpvo.getPk_corp(),userid);
                    }
                    sbf.append(getSigname(index)+"完成！");
                    break;
                }
                case 6://所得税计提
                {
                    if(qmvo.getQysdsjz()==null || !qmvo.getQysdsjz().booleanValue()){
                        gl_qmclserv.onsdsjz(qmvo, userid);
                    }
                    sbf.append(getSigname(index)+"完成！");
                    break;
                }
                case 7://期间损益
                {
                    if(qmvo.getIsqjsyjz()==null || !qmvo.getIsqjsyjz().booleanValue()){
                        gl_qmclserv.updateQiJianSunYiJieZhuan(qmvo,userid);
                    }
                    sbf.append(getSigname(index)+"完成！");
                    break;
                }
                default:
                    break;

            }
        } catch (Exception e) {
            printErrorLog(index,sbf, e, "操作失败!");
        }
    }

    public Map<String, AdjustExrateVO> buildadjustratevo(AdjustExrateVO[] vos){
        Map<String, AdjustExrateVO> mapExrate = new HashMap<String, AdjustExrateVO>();
        if (vos != null && vos.length > 0) {
            for (AdjustExrateVO vo : vos) {
                if(vo.getExrate() == null
                        || vo.getAdjustrate() == null
//					|| vo.getExrate().equals(vo.getAdjustrate()) 汇率相等也能调整
                )
                    continue;
                mapExrate.put(vo.getPk_currency(), vo);
            }
        }
        return mapExrate;
    }

    /**
     * 成本结转
     */
    private void cbjz(int index,QmclVO qmvo,CorpVO pvo,YjjzReturnVO returnvo,int current,YjjzPamterVO pamtervo,String userid) throws Exception{
        try{
            Integer coststyle = pvo.getIcostforwardstyle();
            if(coststyle == IQmclConstant.z0 || coststyle ==IQmclConstant.z1){//直接结转、比例结转
                gl_qmclserv.saveCbjz(qmvo,userid);
            }else if(coststyle == IQmclConstant.z2){//商贸结转
                if(IcCostStyle.IC_ON.equals(pvo.getBbuildic())){//启用库存-----------新版库存，是不会弹出暂估的，因为在销售出库的时候已经做暂估入库。
                    gl_qmclserv.saveCbjz(qmvo,userid);
                }else{
                    //判断，判断成功，即返回。
                    gl_qmclnoicserv.judgeLastPeriod(pvo.getPk_corp(), userid, qmvo.getPeriod(), String.valueOf(coststyle));
                    if(current == 1){//初次调用
                        setGongyeNoicReturninfo1(index,returnvo,pvo);
                    }else{
                        Map<QmclVO, List<QMJzsmNoICVO>> noicmap = new HashMap<QmclVO, List<QMJzsmNoICVO>>();
                        noicmap.put(qmvo, pamtervo.getQmjznoiclist());
                        gl_qmclnoicserv.saveToSalejzVoucher(userid, noicmap, null, null, null);
                    }
                }
            }else if(coststyle == IQmclConstant.z3){//工业结转
                if(IcCostStyle.IC_ON.equals(pvo.getBbuildic())){//启用库存
                    if(current == 1){//初次调用
                        setGongyeReturninfo(index,returnvo);
                    }else{//准备好数据调用
                        savegongyeData(qmvo,pamtervo,userid);
                    }
                } else if (IcCostStyle.IC_INVTENTORY.equals(pvo.getBbuildic())) { // 启用总账存货
                    throw new BusinessException("总账存货模式暂不支持工业结转！");
                } else{
                    //判断，判断成功，即返回。
                    gl_qmclnoicserv.judgeLastPeriod(pvo.getPk_corp(), userid, qmvo.getPeriod(), String.valueOf(coststyle));
                    setGongyeNoicReturninfo2(index,returnvo);
                }
            }
        }catch (ExBusinessException ex) {
            setZGReturninfo(index,ex,returnvo);
        }catch (Exception e) {
            throw e;
        }
    }
    private void setGongyeNoicReturninfo1(int index,YjjzReturnVO returnvo,CorpVO pvo){
        if(IcCostStyle.IC_INVTENTORY.equals(pvo.getBbuildic())){// 启用总账存货
            returnvo.setIcinvtentory(true);
        }
        returnvo.setSuccess(true);
        returnvo.setIsreturn(true);
        returnvo.setCurrentproject(index);
        returnvo.setListrate(null);
        returnvo.setBranchmsg(getSigname(index)+"返回商贸，不启用库存");
        returnvo.setStatuscode(StatusCode.statuscode5);
    }
    public void setGongyeReturninfo(int index,YjjzReturnVO returnvo){
        returnvo.setSuccess(true);
        returnvo.setIsreturn(true);
        returnvo.setCurrentproject(index);
        returnvo.setListrate(null);
        returnvo.setBranchmsg(getSigname(index)+"返回工业结转");
        returnvo.setStatuscode(StatusCode.statuscode4);
    }
    public void savegongyeData(QmclVO qmvo,YjjzPamterVO pamtervo,String userid) throws Exception{
        TransFerVOInfo fervo = new TransFerVOInfo();
        fervo.setCostforwardvolist1(pamtervo.getList1());
        fervo.setCostforwardvolist2(pamtervo.getList2());
        fervo.setCostforwardvolist3(pamtervo.getList3());
        fervo.setCostforwardvolist4(pamtervo.getList4());
        fervo.setCostforwardvolist5(pamtervo.getList5());
        fervo.setQmvo(qmvo);
        gl_qmclserv.saveIndustryJZ(fervo,userid);
    }
    private void setGongyeNoicReturninfo2(int index,YjjzReturnVO returnvo){
        returnvo.setSuccess(true);
        returnvo.setIsreturn(true);
        returnvo.setCurrentproject(index);
        returnvo.setListrate(null);
        returnvo.setBranchmsg(getSigname(index)+"返回工业结转，不启用库存");
        returnvo.setStatuscode(StatusCode.statuscode6);
    }
    public void setZGReturninfo(int index,ExBusinessException ex,YjjzReturnVO returnvo){
        Map<String, List<TempInvtoryVO>> map = ex.getLmap();
        Collection<List<TempInvtoryVO>> cl = map.values();
        List<TempInvtoryVO> cvos = addTempInvtoryVO(cl);
        ZgDataTrans s = new ZgDataTrans();
        s.setIszg(true);
        s.setMsg("暂估");
        s.setTorys(cvos.toArray(new TempInvtoryVO[0]));
        returnvo.setZginfo(s);
        returnvo.setSuccess(true);
        returnvo.setIsreturn(true);
        returnvo.setCurrentproject(index);
        returnvo.setListrate(null);
        returnvo.setBranchmsg(getSigname(index)+"返回暂估信息");
        returnvo.setStatuscode(StatusCode.statuscode3);
    }
    private List<TempInvtoryVO> addTempInvtoryVO(Collection<List<TempInvtoryVO>> cl) {
        List<TempInvtoryVO> list = new ArrayList<TempInvtoryVO>();
        if (cl != null && cl.size() > 0) {
            for (List<TempInvtoryVO> c : cl) {
                for (TempInvtoryVO c1 : c) {
                    // 生成UU-ID
                    c1.setId(UUID.randomUUID().toString());
                }
                list.addAll(c);
            }
        }
        return list;
    }
    private void doRecord (StringBuffer sf) {
        if(sf.length() > 0){
            writeLogRecord(LogRecordEnum.OPE_KJ_SETTLE, sf.toString(), ISysConstants.SYS_2);
        }
    }
}