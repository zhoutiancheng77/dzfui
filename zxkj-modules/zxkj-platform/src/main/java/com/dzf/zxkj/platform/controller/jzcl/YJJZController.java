package com.dzf.zxkj.platform.controller.jzcl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.jzcl.SetJz;
import com.dzf.zxkj.platform.model.jzcl.YJJZSetVO;
import com.dzf.zxkj.platform.model.jzcl.YjjzVO;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.jzcl.IYJJZService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/gl/gl_yjjzact")
@Slf4j
public class YJJZController {

    @Autowired
    private IYJJZService gl_yjjzserv;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IQmclService gl_qmclserv;

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
    public ReturnData<Grid> savejzSet(@RequestBody Map<String, String> map){
        Grid grid = new Grid();
        String pk_corp = map.get("corpid");
        try {
            if (StringUtil.isEmpty(pk_corp))
                throw new BusinessException("查询公司为空！");
            String res = map.get("res");
            if(StringUtil.isEmpty(res))
                throw new BusinessException("参数为空，保存失败！");
            SetJz[] bodyvos = JsonUtils.deserialize(res, SetJz[].class);
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

}
