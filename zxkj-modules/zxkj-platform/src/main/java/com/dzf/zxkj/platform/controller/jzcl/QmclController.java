package com.dzf.zxkj.platform.controller.jzcl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.exception.ExBusinessException;
import com.dzf.zxkj.platform.model.bdset.AdjustExrateVO;
import com.dzf.zxkj.platform.model.bdset.ExrateVO;
import com.dzf.zxkj.platform.model.jzcl.QmLossesVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.jzcl.TempInvtoryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @Author: zpm
 * @Description:期末处理
 * @Date:Created by 2019/11/14
 * @Modified By:
 */
@RestController
@RequestMapping("/gl/gl_qmclact")
@Slf4j
@SuppressWarnings("all")
public class QmclController {

    @Autowired
    private ICorpService corpService;
    @Autowired
    private IQmclService gl_qmclserv = null;

    @PostMapping("/query")
    public ReturnData<Grid> query(@MultiRequestBody QueryParamVO queryParamvo) {
        Grid grid = new Grid();
        try {
            List<String> corppks = queryParamvo.getCorpslist();
            DZFDate begindate1 = queryParamvo.getBegindate1();
            DZFDate enddate1 = queryParamvo.getEnddate();
            String userid = queryParamvo.getUserid();
            DZFDate logindate = queryParamvo.getClientdate();
            DZFBoolean iscarover = queryParamvo.getIscarover();
            DZFBoolean isuncarover = queryParamvo.getIsuncarover();
            List<QmclVO> list = gl_qmclserv.initquery(corppks, begindate1, enddate1, userid, logindate, iscarover, isuncarover);

            grid.setRows(list);
            grid.setSuccess(true);
            grid.setMsg("查询成功！");
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "查询失败");
            log.error("查询失败!", e);
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("/onjtzj")
    public ReturnData<Grid> onjtzj(@MultiRequestBody("qmvos")  QmclVO[] qmvos,@MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            Map<String, List<QmclVO>> qmclmap = new HashMap<String, List<QmclVO>>();
            for (QmclVO votemp : qmvos) {
                String pk_corp = votemp.getPk_corp();
                if (qmclmap.containsKey(pk_corp)) {
                    qmclmap.get(pk_corp).add(votemp);
                } else {
                    List<QmclVO> listtemp = new ArrayList<QmclVO>();
                    listtemp.add(votemp);
                    qmclmap.put(pk_corp, listtemp);
                }
            }
            StringBuffer tips = new StringBuffer();
            List<QmclVO> resqmcl = new ArrayList<QmclVO>();
            // 先按照公司
            for (String str : qmclmap.keySet()) {
                List<QmclVO> listtemp = qmclmap.get(str);
                QmclVO[] qmclvos = sortQmclByPeriod(listtemp, "asc");

                for (QmclVO votemp : qmclvos) {
                    try {
                        gl_qmclserv.checkTemporaryIsExist(votemp.getPk_corp(), votemp.getPeriod(),"不能计提折旧!");
                        votemp.setCoperatorid(userVO.getCuserid());
                        QmclVO resvos = gl_qmclserv.updateJiTiZheJiu(votemp,userVO.getCuserid());
                        resqmcl.add(resvos);
                    } catch (Exception e) {
                        if (e.getMessage().indexOf("null") >= 0) {
                            tips.append("计提折旧失败:" + "<br>");
                        } else {
                            tips.append(e instanceof BusinessException ? e.getMessage()+"<br>" : "计提折旧失败<br>");
                        }
                        resqmcl.add(votemp);
                        log.error("计提折旧失败:",e);
                    }
                }
            }
            if (tips.toString().length() > 0) {
                grid.setSuccess(false);
                grid.setMsg(tips.toString());
            } else {
                grid.setMsg("计提折旧成功！");
                grid.setSuccess(true);
            }
            grid.setTotal((long) 1);
            grid.setRows(resqmcl);
        } catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "计提折旧失败！");
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("/canceljtzj")
    public ReturnData<Grid> canceljtzj(@MultiRequestBody("qmvos")  QmclVO[] qmvos) {
        Grid grid = new Grid();
        try {
            // 重复调用接口，公司+月份
            Map<String, List<QmclVO>> qmclmap = new HashMap<String, List<QmclVO>>();
            for (int i = qmvos.length - 1; i >= 0; i--) {
                QmclVO votemp = qmvos[i];
                String pk_corp = votemp.getPk_corp();
                if (qmclmap.containsKey(pk_corp)) {
                    qmclmap.get(pk_corp).add(votemp);
                } else {
                    List<QmclVO> listtemp = new ArrayList<QmclVO>();
                    listtemp.add(votemp);
                    qmclmap.put(pk_corp, listtemp);
                }
            }
            StringBuffer tips = new StringBuffer();
            List<QmclVO> resqmcl = new ArrayList<QmclVO>();
            // 先按照公司
            for (String str : qmclmap.keySet()) {
                List<QmclVO> listtemp = qmclmap.get(str);
                QmclVO[] qmclvos = sortQmclByPeriod(listtemp, "desc");
                for (QmclVO votemp : qmclvos) {
                    try {
                        QmclVO resvos = gl_qmclserv.updateFanJiTiZheJiu(votemp);
                        resqmcl.add(resvos);
                    } catch (Exception e) {
                        tips.append(e instanceof BusinessException ? e.getMessage()+"<br>" :"反计提折旧失败！<br/>");
                        resqmcl.add(votemp);
                        log.error("错误",e);
                    }
                }
            }
            if (tips.toString().length() > 0) {
                grid.setSuccess(false);
                grid.setMsg(tips.toString());
            } else {
                grid.setSuccess(true);
                grid.setMsg("反计提折旧成功！");
            }
            grid.setTotal((long) resqmcl.size());
            grid.setRows(resqmcl);
        } catch (Exception e) {
            log.error("错误",e);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "反计提折旧失败！");
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/onzzsjz")
    public ReturnData<Grid> onzzsjz(@MultiRequestBody("qmvos")  QmclVO[] qmvos,@MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            Map<String, List<QmclVO>> qmclmap = new HashMap<String, List<QmclVO>>();
            for (QmclVO votemp : qmvos) {
                String pk_corp = votemp.getPk_corp();
                if (qmclmap.containsKey(pk_corp)) {
                    qmclmap.get(pk_corp).add(votemp);
                } else {
                    List<QmclVO> listtemp = new ArrayList<QmclVO>();
                    listtemp.add(votemp);
                    qmclmap.put(pk_corp, listtemp);
                }
            }
            StringBuffer tips = new StringBuffer();
            List<QmclVO> resqmcl = new ArrayList<QmclVO>();
            // 先按照公司
            for (String str : qmclmap.keySet()) {
                List<QmclVO> listtemp = qmclmap.get(str);
                QmclVO[] qmclvos = sortQmclByPeriod(listtemp, "asc");
                for (QmclVO votemp : qmclvos) {
                    try {
                        gl_qmclserv.checkTemporaryIsExist(votemp.getPk_corp(), votemp.getPeriod(), "不能增值税结转!");
                        votemp.setCoperatorid(userVO.getCuserid());
                        QmclVO resvos = gl_qmclserv.onzzsjz(userVO.getCuserid(), votemp);
                        resqmcl.add(resvos);
                    }  catch (Exception e) {
                        tips.append(e instanceof BusinessException ? e.getMessage()+"<br>" :"增值税结转失败<br/>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    }
                }
            }
            if (tips.toString().length() > 0) {
                grid.setMsg(tips.toString());
                grid.setSuccess(false);
            } else {
                grid.setMsg("增值税结转成功！");
                grid.setSuccess(true);
            }
            grid.setTotal((long) resqmcl.size());
            grid.setRows(resqmcl);
        } catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "增值税结转失败！");
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/cancelzzsjz")
    public ReturnData<Grid> cancelzzsjz(@MultiRequestBody("qmvos")  QmclVO[] qmvos) {
        Grid grid = new Grid();
        try {
            // 重复调用接口，公司+月份
            Map<String, List<QmclVO>> qmclmap = new HashMap<String, List<QmclVO>>();
            for (int i = qmvos.length - 1; i >= 0; i--) {
                QmclVO votemp = qmvos[i];
                String pk_corp = votemp.getPk_corp();
                if (qmclmap.containsKey(pk_corp)) {
                    qmclmap.get(pk_corp).add(votemp);
                } else {
                    List<QmclVO> listtemp = new ArrayList<QmclVO>();
                    listtemp.add(votemp);
                    qmclmap.put(pk_corp, listtemp);
                }
            }
            StringBuffer tips = new StringBuffer();
            List<QmclVO> resqmcl = new ArrayList<QmclVO>();
            // 先按照公司
            for (String str : qmclmap.keySet()) {
                List<QmclVO> listtemp = qmclmap.get(str);
                QmclVO[] qmclvos = sortQmclByPeriod(listtemp, "desc");
                for (QmclVO votemp : qmclvos) {
                    try {
                        QmclVO resvos = gl_qmclserv.onfzzsjz(votemp);
                        resqmcl.add(resvos);
                    }  catch (Exception e) {
                        tips.append(e instanceof BusinessException ? e.getMessage()+"<br>" : "反增值税结转失败<br/>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    }
                }
            }
            if (tips.toString().length() > 0) {
                grid.setMsg(tips.toString());
                grid.setSuccess(false);
            } else {
                grid.setMsg("反增值税结转成功！");
                grid.setSuccess(true);
            }
            grid.setTotal((long) resqmcl.size());
            grid.setRows(resqmcl);
        } catch (Exception e) {
            log.error("错误",e);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "反增值税结转失败！");
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("/onjtfjs")
    public ReturnData<Grid> onjtfjs(@MultiRequestBody("qmvos")  QmclVO[] qmvos,@MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            // 重复调用接口，公司+月份
            String kmmethod = null;
            Map<String, List<QmclVO>> qmclmap = new HashMap<String, List<QmclVO>>();
            for (QmclVO votemp : qmvos) {
                String pk_corp = votemp.getPk_corp();
                if (qmclmap.containsKey(pk_corp)) {
                    qmclmap.get(pk_corp).add(votemp);
                } else {
                    List<QmclVO> listtemp = new ArrayList<QmclVO>();
                    listtemp.add(votemp);
                    qmclmap.put(pk_corp, listtemp);
                }
            }

            StringBuffer tips = new StringBuffer();
            // Set<String> gsset = new HashSet<String>();
            List<QmclVO> resqmcl = new ArrayList<QmclVO>();
            String userid = userVO.getCuserid();
            // 先按照公司
            for (String pk_corp : qmclmap.keySet()) {
                List<QmclVO> listtemp = qmclmap.get(pk_corp);
                QmclVO[] qmclvos = sortQmclByPeriod(listtemp, "asc");
                CorpVO cpvo = corpService.queryByPk(pk_corp);
                kmmethod = cpvo.getCorptype();// 科目方案

                for (QmclVO votemp : qmclvos) {
                    try {
                        votemp.setCoperatorid(userid);
                        gl_qmclserv.checkTemporaryIsExist(votemp.getPk_corp(), votemp.getPeriod(), "不能计提附加税!");
                        QmclVO resvos = gl_qmclserv.updateJiTiShuiJin(votemp, kmmethod, votemp.getPk_corp(), userid);
                        resqmcl.add(resvos);
                    } catch (BusinessException e) {
                        if (StringUtil.isEmpty(e.getMessage()) || e.getMessage().indexOf("null") >= 0) {
                            tips.append("计提失败:" + "<br>");
                        } else {
                            tips.append(e.getMessage() + "<br>");
                        }
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    } catch (Exception e) {
                        if (StringUtil.isEmpty(e.getMessage()) || e.getMessage().indexOf("null") >= 0) {
                            tips.append("计提失败:<br>");
                        } else {
                            tips.append("计提失败:<br>");
                        }
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    }
                }
            }
            if (tips.toString().length() > 0) {
                grid.setMsg(tips.toString());
                grid.setSuccess(false);
            } else {
                grid.setMsg("计提附加税成功！");
                grid.setSuccess(true);
            }
            grid.setTotal((long) 1);
            grid.setRows(resqmcl);
        } catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "计提附加税失败！");
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/canceljtfjs")
    public ReturnData<Grid> canceljtfjs(@MultiRequestBody("qmvos")  QmclVO[] qmvos) {
        Grid grid = new Grid();
        try {
            // 重复调用接口，公司+月份
            Map<String, List<QmclVO>> qmclmap = new HashMap<String, List<QmclVO>>();

            for (int i = qmvos.length - 1; i >= 0; i--) {
                QmclVO votemp = qmvos[i];
                String pk_corp = votemp.getPk_corp();
                if (qmclmap.containsKey(pk_corp)) {
                    qmclmap.get(pk_corp).add(votemp);
                } else {
                    List<QmclVO> listtemp = new ArrayList<QmclVO>();
                    listtemp.add(votemp);
                    qmclmap.put(pk_corp, listtemp);
                }
            }
            StringBuffer tips = new StringBuffer();
            List<QmclVO> resqmcl = new ArrayList<QmclVO>();
            // 先按照公司
            for (String str : qmclmap.keySet()) {
                List<QmclVO> listtemp = qmclmap.get(str);
                QmclVO[] qmclvos = sortQmclByPeriod(listtemp, "desc");
                for (QmclVO votemp : qmclvos) {
                    try {
                        QmclVO resvos = gl_qmclserv.updateFanJiTiShuiJin(votemp);
                        resqmcl.add(resvos);
                    } catch (BusinessException e) {
                        tips.append(e.getMessage() + "<br/>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    } catch (Exception e) {
                        tips.append("反计提附加税失败<br/>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    }
                }
            }
            if (tips.toString().length() > 0) {
                grid.setMsg(tips.toString());
                grid.setSuccess(false);
            } else {
                grid.setMsg("反计提附加税成功");
                grid.setSuccess(true);
            }
            grid.setTotal((long) resqmcl.size());
            grid.setRows(resqmcl);
        }catch (Exception e) {
            log.error("错误",e);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "反计提附加税失败！");
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("/onjtsds")
    public ReturnData<Grid> onjtsds(@MultiRequestBody("qmvos")  QmclVO[] qmvos,@MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            String userid = userVO.getCuserid();
            // 重复调用接口，公司+月份
            Map<String, List<QmclVO>> qmclmap = new HashMap<String, List<QmclVO>>();
            for (QmclVO votemp : qmvos) {
                String pk_corp = votemp.getPk_corp();
                if (qmclmap.containsKey(pk_corp)) {
                    qmclmap.get(pk_corp).add(votemp);
                } else {
                    List<QmclVO> listtemp = new ArrayList<QmclVO>();
                    listtemp.add(votemp);
                    qmclmap.put(pk_corp, listtemp);
                }
            }
            StringBuffer tips = new StringBuffer();
            List<QmclVO> resqmcl = new ArrayList<QmclVO>();
            // 先按照公司
            for (String str : qmclmap.keySet()) {
                List<QmclVO> listtemp = qmclmap.get(str);
                QmclVO[] qmclvos = sortQmclByPeriod(listtemp, "asc");
                for (QmclVO votemp : qmclvos) {
                    try {
                        gl_qmclserv.checkTemporaryIsExist(votemp.getPk_corp(), votemp.getPeriod(), "不能计提所得税!");
                        votemp.setCoperatorid(userid);
                        QmclVO resvos = gl_qmclserv.onsdsjz(votemp, userid);
                        resqmcl.add(resvos);
                    } catch (BusinessException e) {
                        tips.append(e.getMessage() + "<br>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    } catch (Exception e) {
                        tips.append("所得税计提失败<br>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    }
                }
            }
            if (tips.toString().length() > 0) {
                grid.setMsg(tips.toString());
                grid.setSuccess(false);
            } else {
                grid.setMsg("所得税计提成功！");
                grid.setSuccess(true);
            }
            grid.setTotal((long) resqmcl.size());
            grid.setRows(resqmcl);
        } catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "计提所得税失败！");
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/canceljtsds")
    public ReturnData<Grid> canceljtsds(@MultiRequestBody("qmvos")  QmclVO[] qmvos) {
        Grid grid = new Grid();
        try {
            // 重复调用接口，公司+月份
            Map<String, List<QmclVO>> qmclmap = new HashMap<String, List<QmclVO>>();

            for (int i = qmvos.length - 1; i >= 0; i--) {
                QmclVO votemp = qmvos[i];
                String pk_corp = votemp.getPk_corp();
                if (qmclmap.containsKey(pk_corp)) {
                    qmclmap.get(pk_corp).add(votemp);
                } else {
                    List<QmclVO> listtemp = new ArrayList<QmclVO>();
                    listtemp.add(votemp);
                    qmclmap.put(pk_corp, listtemp);
                }
            }
            StringBuffer tips = new StringBuffer();
            List<QmclVO> resqmcl = new ArrayList<QmclVO>();
            // 先按照公司
            for (String str : qmclmap.keySet()) {
                List<QmclVO> listtemp = qmclmap.get(str);
                QmclVO[] qmclvos = sortQmclByPeriod(listtemp, "desc");
                for (QmclVO votemp : qmclvos) {
                    try {
                        QmclVO resvos = gl_qmclserv.onfsdsjz(votemp);
                        resqmcl.add(resvos);
                    } catch (BusinessException e) {
                        tips.append(e.getMessage() + "<br/>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    } catch (Exception e) {
                        tips.append("反所得税计提失败<br/>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    }
                }
            }
            if (tips.toString().length() > 0) {
                grid.setMsg(tips.toString());
                grid.setSuccess(false);
            } else {
                grid.setSuccess(true);
                grid.setMsg("反计提所得税成功");
            }
            grid.setTotal((long) resqmcl.size());
            grid.setRows(resqmcl);
        }catch (Exception e) {
            log.error("错误",e);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "反计提所得税失败！");
        }
        return ReturnData.ok().data(grid);
    }



    @PostMapping("/queryqmLoss")
    public ReturnData<Grid> queryqmLoss(@RequestBody Map<String, String> param) {
        Grid grid = new Grid();
        String begindate = param.get("begindate");
        String pk_corp = param.get("pk_corp");
        try {
            //查询登录公司 和 登录日期
            QmLossesVO lossvo = gl_qmclserv.queryLossmny(new DZFDate(begindate), pk_corp);
            grid.setRows(lossvo);
            grid.setSuccess(true);
            grid.setMsg("查询弥补金额成功！");
        }catch (Exception e) {
            log.error("错误",e);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "查询弥补金额失败！");
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/updateqmLoss")
    public ReturnData<Grid> updateqmLoss(@RequestBody Map<String, String> param) {
        Grid grid = new Grid();
        String begindate = param.get("begindate");
        String pk_corp = param.get("pk_corp");
        String lossvalue = param.get("lossvalue");
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空!");
        }
        if (StringUtil.isEmpty(begindate)) {
            throw new BusinessException("期间为空!");
        }
        if (StringUtil.isEmpty(lossvalue)) {
            throw new BusinessException("可弥补亏损额为空!");
        }
        String year = begindate.substring(0, 4);
        try {
            QmLossesVO lossvo = gl_qmclserv.updateLossmny(new DZFDate(begindate), pk_corp, new DZFDouble(lossvalue));
            grid.setRows(lossvo);
            grid.setSuccess(true);
            grid.setMsg("更新" + year + "年度弥补金额成功！");
        }catch (Exception e) {
            log.error("错误",e);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "更新" + year + "年度弥补金额失败！");
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/onsyjz")
    public ReturnData<Grid> onsyjz(@MultiRequestBody("qmvos")  QmclVO[] qmvos,@MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            String userid = userVO.getCuserid();
            // 重复调用接口，公司+月份
            Map<String, List<QmclVO>> qmclmap = new HashMap<String, List<QmclVO>>();
            for (QmclVO votemp : qmvos) {
                String pk_corp = votemp.getPk_corp();
                if (qmclmap.containsKey(pk_corp)) {
                    qmclmap.get(pk_corp).add(votemp);
                } else {
                    List<QmclVO> listtemp = new ArrayList<QmclVO>();
                    listtemp.add(votemp);
                    qmclmap.put(pk_corp, listtemp);
                }
            }
            StringBuffer tips = new StringBuffer();
            List<QmclVO> resqmcl = new ArrayList<QmclVO>();
            // 先按照公司
            for (String str : qmclmap.keySet()) {
                List<QmclVO> listtemp = qmclmap.get(str);
                QmclVO[] qmclvos = sortQmclByPeriod(listtemp, "asc");
                for (QmclVO votemp : qmclvos) {
                    try {
                        gl_qmclserv.checkTemporaryIsExist(votemp.getPk_corp(), votemp.getPeriod(), "不能损益结转!");
                        votemp.setCoperatorid(userid);
                        QmclVO resvos = gl_qmclserv.updateQiJianSunYiJieZhuan(votemp, userid);
                        resqmcl.add(resvos);
                    } catch (BusinessException e) {
                        tips.append(e.getMessage() + "<br>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    } catch (Exception e) {
                        tips.append("损益结转失败<br/>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    }
                }
            }
            if (tips.toString().length() > 0) {
                grid.setMsg(tips.toString());
                grid.setSuccess(false);
            } else {
                grid.setMsg("损益结转成功！");
                grid.setSuccess(true);
            }
            grid.setTotal((long) resqmcl.size());
            grid.setRows(resqmcl);
        } catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "损益结转失败！");
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/cancelsyjz")
    public ReturnData<Grid> cancelsyjz(@MultiRequestBody("qmvos")  QmclVO[] qmvos) {
        Grid grid = new Grid();
        try {
            // 重复调用接口，公司+月份
            Map<String, List<QmclVO>> qmclmap = new HashMap<String, List<QmclVO>>();
            for (int i = qmvos.length - 1; i >= 0; i--) {
                QmclVO votemp = qmvos[i];
                String pk_corp = votemp.getPk_corp();
                if (qmclmap.containsKey(pk_corp)) {
                    qmclmap.get(pk_corp).add(votemp);
                } else {
                    List<QmclVO> listtemp = new ArrayList<QmclVO>();
                    listtemp.add(votemp);
                    qmclmap.put(pk_corp, listtemp);
                }
            }
            StringBuffer tips = new StringBuffer();
            List<QmclVO> resqmcl = new ArrayList<QmclVO>();
            // 先按照公司
            for (String str : qmclmap.keySet()) {
                List<QmclVO> listtemp = qmclmap.get(str);
                QmclVO[] qmclvos = sortQmclByPeriod(listtemp, "desc");
                for (QmclVO votemp : qmclvos) {
                    try {
                        QmclVO resvos = gl_qmclserv.updateFanQiJianSunYiJieZhuan(votemp);
                        resqmcl.add(resvos);
                    } catch (BusinessException e) {
                        tips.append(e.getMessage() + "<br>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    } catch (Exception e) {
                        tips.append("反损益结转失败<br/>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    }
                }
            }
            if (tips.toString().length() > 0) {
                grid.setMsg(tips.toString());
                grid.setSuccess(false);
            } else {
                grid.setMsg("反损益结转成功");
                grid.setSuccess(true);
            }
            grid.setTotal((long) resqmcl.size());
            grid.setRows(resqmcl);
        }catch (Exception e) {
            log.error("错误",e);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "反损益结转失败！");
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/queryAdjust")
    public ReturnData<Grid> queryAdjust(@MultiRequestBody("qmvos")  QmclVO[] qmvos) {
        Grid grid = new Grid();
        try {
            if(qmvos == null || qmvos.length != 1){
                grid.setSuccess(false);
                grid.setMsg("请选择一行数据进行操作！");
            }else{
                ExrateVO[] list1 = gl_qmclserv.queryAdjust(qmvos[0]);
                grid.setRows(new ArrayList<ExrateVO>(Arrays.asList(list1)));
                grid.setSuccess(true);
                grid.setMsg("期末调汇查询成功！");
            }
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "期末调汇查询失败");
            log.error("期末调汇查询失败!", e);
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("/onhdsytz")
    public ReturnData<Grid> onhdsytz(@MultiRequestBody("qmvos")  QmclVO[] qmvos,
                                   @MultiRequestBody("exrates") AdjustExrateVO[] exrates,@MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            String userid = userVO.getCuserid();
            QmclVO qmclvo = qmvos[0];
            if (qmclvo.getIshdsytz() != null && qmclvo.getIshdsytz().booleanValue()) {
                grid.setSuccess(false);
                grid.setRows(new ArrayList<QmclVO>());
                grid.setMsg("期末调汇已经调整，不能重复调整");
            } else {
                if (exrates == null || exrates.length == 0) {
                    grid.setSuccess(false);
                    grid.setRows(new ArrayList<QmclVO>());
                    grid.setMsg("处理失败：数据为空!");
                } else {
                    qmclvo.setCoperatorid(userid);
                    HashMap<String, AdjustExrateVO> mapExrate = new HashMap<String, AdjustExrateVO>();
                    String corp = qmclvo.getPk_corp();
                    for (AdjustExrateVO vo : exrates) {
                        // 汇率相等也能调汇
                        // if (vo.getExrate() == null || vo.getAdjustrate()
                        // == null
                        // || vo.getExrate().equals(vo.getAdjustrate()))
                        // continue;
                        mapExrate.put(vo.getPk_currency(), vo);
                    }
                    if (mapExrate != null && mapExrate.size() > 0) {
                        qmclvo = gl_qmclserv.updateHuiDuiSunYiTiaoZheng(qmclvo, mapExrate, userid);
                    } else {// 直接更新期末调汇状态为Y
                        gl_qmclserv.updatehdsyzt(qmclvo, userid);
                    }
                    List<QmclVO> qmcllist = new ArrayList<QmclVO>();
                    qmcllist.add(qmclvo);
                    grid.setMsg("期末调汇成功!");
                    grid.setTotal((long) 1);
                    grid.setSuccess(true);
                    grid.setRows(qmcllist);
                }
            }
        } catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "期末调汇失败！");
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/cancelhdsytz")
    public ReturnData<Grid> cancelhdsytz(@MultiRequestBody("qmvos")  QmclVO[] qmvos) {
        Grid grid = new Grid();
        try {
            // 重复调用接口，公司+月份
            Map<String, List<QmclVO>> qmclmap = new HashMap<String, List<QmclVO>>();
            for (int i = qmvos.length - 1; i >= 0; i--) {
                QmclVO votemp = qmvos[i];
                String pk_corp = votemp.getPk_corp();
                if (qmclmap.containsKey(pk_corp)) {
                    qmclmap.get(pk_corp).add(votemp);
                } else {
                    List<QmclVO> listtemp = new ArrayList<QmclVO>();
                    listtemp.add(votemp);
                    qmclmap.put(pk_corp, listtemp);
                }
            }
            StringBuffer tips = new StringBuffer();
            List<QmclVO> resqmcl = new ArrayList<QmclVO>();
            // 先按照公司
            for (String str : qmclmap.keySet()) {
                List<QmclVO> listtemp = qmclmap.get(str);
                QmclVO[] qmclvos = sortQmclByPeriod(listtemp, "desc");
                for (QmclVO votemp : qmclvos) {
                    try {
                        QmclVO resvos = gl_qmclserv.updateFanHuiDuiSunYiTiaoZheng(votemp);
                        resqmcl.add(resvos);
                    } catch (BusinessException e) {
                        tips.append(e.getMessage() + "<br>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    } catch (Exception e) {
                        tips.append("反期末调汇失败<br/>");
                        resqmcl.add(votemp);
                        log.error("错误", e);
                    }
                }
            }
            if (tips.toString().length() > 0) {
                grid.setMsg(tips.toString());
                grid.setSuccess(false);
            } else {
                grid.setMsg("反期末调汇成功！");
                grid.setSuccess(true);
            }
            grid.setTotal((long) resqmcl.size());
            grid.setRows(resqmcl);
        }catch (Exception e) {
            log.error("错误",e);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "反期末调汇失败！");
        }
        return ReturnData.ok().data(grid);
    }

    // 该批量成本结转 ，只支持手工结转 和 比例结转
    @PostMapping("/onbatcbjz")
    public ReturnData<Grid> onbatcbjz(@MultiRequestBody("qmvos")  QmclVO[] qmvos, @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            String cuserid= userVO.getCuserid();
            StringBuffer msg = new StringBuffer();
            String pk_corp;
            Integer forwardtype;
            String unitname = null;
            String period = null;
            List<QmclVO> list = new ArrayList<QmclVO>();
            for (QmclVO vo : qmvos) {
                try {
                    pk_corp = vo.getPk_corp();
                    period = vo.getPeriod();
                    forwardtype = vo.getIcosttype();
                    unitname = vo.getCorpname();
                    msg.append("公司：").append(unitname).append(",期间").append(period);
                    if (forwardtype != null && (forwardtype == 2 || forwardtype == 3)) {
                        msg.append("<font color='red'>商贸成本、工业成本不允许多公司、多期间批量操作！</font><br>");
                        continue;
                    }
                    if(vo.getIscbjz() != null && vo.getIscbjz().booleanValue()){
                        msg.append("<font color='red'>已经成本结转，请勿重复结转！</font><br>");
                        continue;
                    }
                    gl_qmclserv.checkTemporaryIsExist(pk_corp, period, "不能批量结转!");
                    gl_qmclserv.checkQmclForKc(pk_corp, period, "不能批量结转!");
                    QmclVO vos = gl_qmclserv.saveCbjz(vo, cuserid);
                    list.add(vos);
                    msg.append("成本结转成功<br>");
                } catch (Exception e) {
                    log.error("成本结转:", e);
                    msg.append("<font color='red'>");
                    if (e instanceof BusinessException) {
                        msg.append(e.getMessage());
                    } else {
                        msg.append("成本结转失败");
                    }
                    msg.append("</font><br>");
                }
            }
            grid.setRows(list);
            grid.setMsg(msg.toString());
            grid.setSuccess(true);
        } catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "成本结转失败！");
        }
        return ReturnData.ok().data(grid);
    }

    // 单条成本结转
    @PostMapping("/onsinglecbjz")
    public ReturnData<Grid> onsinglecbjz(@MultiRequestBody("qmvo")  QmclVO qmvo, @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            if(qmvo == null)
                throw new BusinessException("请求参数为空");
            if(qmvo.getIscbjz() != null && qmvo.getIscbjz().booleanValue())
                throw new BusinessException("已经成本结转，请勿重复结转");
            Integer forwardtype = qmvo.getIcosttype();
            // 调用工业成本结转方法
            if(forwardtype != null && forwardtype == 3){
                return onIndustrycbjz(qmvo,userVO);
            }
            gl_qmclserv.checkTemporaryIsExist(qmvo.getPk_corp(), qmvo.getPeriod(), "不能批量结转!");
            gl_qmclserv.checkQmclForKc(qmvo.getPk_corp(), qmvo.getPeriod(), "不能批量结转!");
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            String userid = userVO.getCuserid();
            QmclVO resvos = gl_qmclserv.saveCbjz(qmvo, userid);
            grid.setMsg("成本结转成功!");
            grid.setTotal(Long.valueOf(1));
            grid.setSuccess(true);
            grid.setRows(Arrays.asList(resvos));
        } catch (ExBusinessException ex) {
            Map<String, List<TempInvtoryVO>> map = ex.getLmap();
            Collection<List<TempInvtoryVO>> cl = map.values();
            List<TempInvtoryVO> cvos = addTempInvtoryVO(cl);
            Grid grid1 = new Grid();
            grid1.setSuccess(true);
            grid1.setMsg("暂估");
            grid1.setRows(cvos.toArray(new TempInvtoryVO[0]));
            return ReturnData.ok().data(grid1);
        } catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "成本结转失败！");
        }
        return ReturnData.ok().data(grid);
    }

    // 工作结转
    public ReturnData<Grid> onIndustrycbjz(QmclVO qmvo, UserVO userVO) {
        Grid grid = new Grid();
        try {

        }catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "成本结转失败！");
        }
        return ReturnData.ok().data(grid);
    }

    // 反成本结转
    @PostMapping("/cancelCbjz")
    public ReturnData<Grid> cancelCbjz(@MultiRequestBody("qmvos")  QmclVO[] qmvos, @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            String pk_corp;
            String period = null;
            QmclVO qmclvo = null;
            StringBuffer msg = new StringBuffer();
            List<QmclVO> list = new ArrayList<QmclVO>();
            for (int i = qmvos.length - 1; i >= 0; i--) {// 倒序执行
                try {
                    qmclvo = qmvos[i];
                    pk_corp = qmclvo.getPk_corp();
                    period = qmclvo.getPeriod();
                    msg.append("公司：").append(qmclvo.getCorpname()).append(",期间").append(period);
                    if (qmclvo.getIscbjz() == null || !qmclvo.getIscbjz().booleanValue()) {
                        msg.append("成本结转取消成功!<br>");
                        continue;
                    }
                    qmclvo = gl_qmclserv.rollbackCbjz(qmclvo);
                    list.add(qmclvo);
                    msg.append("成本结转取消成功!<br>");
                } catch (Exception e) {
                    log.error("反成本结转:", e);
                    msg.append("<font color='red'>");
                    if (e instanceof BusinessException) {
                        msg.append(e.getMessage());
                    } else {
                        msg.append("成本结转取消失败!");
                    }
                    msg.append("</font><br>");
                }
            }
            grid.setMsg(msg.toString());
            grid.setRows(list);
            grid.setSuccess(true);
        }catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "取消成本结转失败！");
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/checkTemporaryIsExist")
    public ReturnData<Grid> checkTemporaryIsExist(@MultiRequestBody("qmvos")  QmclVO[] qmvos,@MultiRequestBody("type") String type) {
        Grid grid = new Grid();
        try {
            grid.setSuccess(false);
            if(qmvos != null && qmvos.length == 1){
                QmclVO headvo  = qmvos[0];
                gl_qmclserv.checkTemporaryIsExist(headvo.getPk_corp(), headvo.getPeriod(), "");
                if ("0".equals(type)) {// 0 默认成本结转
                    // 如果是成本结转走这个
                    gl_qmclserv.checkQmclForKc(headvo.getPk_corp(), headvo.getPeriod(), "");
                }
                grid.setSuccess(true);
            }
        }catch (Exception e) {
            log.error("错误",e);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "校验查询失败！");
        }
        return ReturnData.ok().data(grid);
    }


    private QmclVO[] sortQmclByPeriod(List<QmclVO> listtemp, final String ordervalue) {
        QmclVO[] qmclvos = listtemp.toArray(new QmclVO[0]);
        // 先对集合排序
        java.util.Arrays.sort(qmclvos, new Comparator<QmclVO>() {
            public int compare(QmclVO o1, QmclVO o2) {
                int i = 0;
                if ("desc".equals(ordervalue)) {
                    if (o1.getPeriod().compareTo(o2.getPeriod()) > 0) {
                        i = -1;
                    } else if (o1.getPeriod().compareTo(o2.getPeriod()) == 0) {
                        i = 0;
                    } else {
                        i = 1;
                    }
                } else {
                    i = o1.getPeriod().compareTo(o2.getPeriod());
                }
                return i;
            }
        });

        return qmclvos;
    }

    private String getLogMsg(String ope, QmclVO[] qmclvos, String ident) {

        StringBuffer value = new StringBuffer();
        if (ident.equals("asc")) {
            value.append(ope + ":" + qmclvos[0].getPeriod() + "~" + qmclvos[qmclvos.length - 1].getPeriod());
        } else {
            value.append(ope + ":" + qmclvos[qmclvos.length - 1].getPeriod() + "~" + qmclvos[0].getPeriod());
        }

        return value.toString();
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
}