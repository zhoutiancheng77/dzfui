package com.dzf.zxkj.platform.controller.jzcl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.VOSortUtils;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.config.QmjzByDzfConfig;
import com.dzf.zxkj.platform.exception.ExBusinessException;
import com.dzf.zxkj.platform.model.bdset.AdjustExrateVO;
import com.dzf.zxkj.platform.model.bdset.ExrateVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.jzcl.*;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.glic.impl.CheckInventorySet;
import com.dzf.zxkj.platform.service.icset.IInventoryService;
import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.service.jzcl.IQmclNoicService;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.jzcl.IndustryForward;
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
    @Autowired
    private QmjzByDzfConfig qmjzByDzfConfig;
    @Autowired
    private IQmclNoicService gl_qmclnoicserv;
    @Autowired
    private CheckInventorySet inventory_setcheck;
    @Autowired
    private IInventoryAccSetService gl_ic_invtorysetserv;
    @Autowired
    private IndustryForward gl_industryserv;
    @Autowired
    private ICbComconstant gl_cbconstant;
    @Autowired
    private IInventoryService ic_inventoryserv;

    @PostMapping("/query")
    public ReturnData<Grid> query(@MultiRequestBody("queryparam") QueryParamVO queryParamvo) {
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


    @PostMapping("/queryGlpz")
    public ReturnData<Grid> queryGlpz(@MultiRequestBody("sourcebilltype") String sourcebilltype,
                                      @MultiRequestBody("pk_corp") String pk_corp,
                                      @MultiRequestBody("period") String period) {
        Grid grid = new Grid();
        try {
            List<TzpzHVO> tzpzHVOList = gl_qmclserv.queryQmclGlpz(period, pk_corp, sourcebilltype);
            grid.setRows(tzpzHVOList);
            grid.setSuccess(true);
            grid.setMsg("联查成功！");
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "联查失败");
            log.error("联查失败!", e);
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
                        gl_qmclserv.checkTemporaryIsExist(votemp.getPk_corp(), votemp.getPeriod(),false,"不能计提折旧!");
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
                        gl_qmclserv.checkTemporaryIsExist(votemp.getPk_corp(), votemp.getPeriod(), false,"不能增值税结转!");
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
                        gl_qmclserv.checkTemporaryIsExist(votemp.getPk_corp(), votemp.getPeriod(), false,"不能计提附加税!");
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
                        gl_qmclserv.checkTemporaryIsExist(votemp.getPk_corp(), votemp.getPeriod(), false,"不能计提所得税!");
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
                        gl_qmclserv.checkTemporaryIsExist(votemp.getPk_corp(), votemp.getPeriod(), false,"不能损益结转!");
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
                    gl_qmclserv.checkTemporaryIsExist(pk_corp, period,true,"不能成本结转!");
                    gl_qmclserv.checkQmclForKc(pk_corp, period, true);
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
    public ReturnData<Grid> onsinglecbjz(@MultiRequestBody("qmvo")  QmclVO qmvo,
                                         @MultiRequestBody("zgdata")  TempInvtoryVO[]  zgdata,
                                         @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        grid.setSuccess(false);
        grid.setRows(new ArrayList<QmclVO>());
        try {
            if(qmvo == null)
                throw new BusinessException("请求参数为空");
            if(qmvo.getIscbjz() != null && qmvo.getIscbjz().booleanValue())
                throw new BusinessException("已经成本结转，请勿重复结转");
            if(zgdata != null && zgdata.length > 0){
                qmvo.setZgdata(zgdata);
            }
            //校验
            gl_qmclserv.checkTemporaryIsExist(qmvo.getPk_corp(), qmvo.getPeriod(), false , "不能成本结转!");
            gl_qmclserv.checkQmclForKc(qmvo.getPk_corp(), qmvo.getPeriod(), false);
            String userid = userVO.getCuserid();
            QmclVO resvos = null;
            // 成本结转方式为手工结转 和 比例结转
            Integer forwardtype = qmvo.getIcosttype();
            // 在特殊情况下，大账房公司，这里强制按 0 进行处理
            boolean dzfflag = qmjzByDzfConfig.dzf_pk_gs.equals(qmvo.getPk_corp());
            if(dzfflag){
                forwardtype = 0 ;
            }
            switch(forwardtype){
                case 0:  //手工结转
                case 1:{ //比例结转
                    resvos = gl_qmclserv.saveCbjz(qmvo, userid);
                    break;
                }
                case 2:{//商贸
                    return onBusinessTradeCbjz(qmvo,userVO);
                }
                case 3:{//工业
                    return onIndustrycbjz(qmvo,userVO);
                }
            }
            grid.setMsg("成本结转成功!");
            grid.setTotal(Long.valueOf(1));
            grid.setSuccess(true);
            grid.setRows(Arrays.asList(resvos));
        }  catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "成本结转失败！");
        }
        return ReturnData.ok().data(grid);
    }

    // 商贸成本结转
    public ReturnData<Grid> onBusinessTradeCbjz(QmclVO qmvo, UserVO userVO) {
        Grid grid = new Grid();
        String userid = userVO.getCuserid();
        QmclVO resvos = null;
        try {
            // 上一期成本是否结转校验
            gl_qmclnoicserv.judgeLastPeriod(qmvo.getPk_corp(), userid, qmvo.getPeriod(), String.valueOf(qmvo.getIcosttype()));
            CorpVO cpvo = corpService.queryByPk(qmvo.getPk_corp());
            if(IcCostStyle.IC_ON.equals(cpvo.getBbuildic())){ // 启用进销存的
                resvos = gl_qmclserv.saveCbjz(qmvo, userid);
                grid.setMsg("成本结转成功!");
                grid.setTotal(Long.valueOf(1));
                grid.setSuccess(true);
                grid.setRows(Arrays.asList(resvos));
            } else if(IcCostStyle.IC_INVTENTORY.equals(cpvo.getBbuildic())){ // 启用总账存货
                InventorySetVO setvo = gl_ic_invtorysetserv.query(qmvo.getPk_corp());
                String error = inventory_setcheck.checkInventorySet(userid, qmvo.getPk_corp(),setvo);
                if (!StringUtil.isEmpty(error)) {
                    error = error.replaceAll("<br>", " ");
                    throw new BusinessException("成本结转失败！"+error);
                }
                grid.setSuccess(true);
                grid.setMsg("总账存货结转期末销售成本");
                grid.setTotal(Long.valueOf(1));
                grid.setSuccess(true);
                grid.setRows(Arrays.asList(resvos));
            } else {//不启用进销存的
                grid.setSuccess(true);
                grid.setMsg("普通结转期末销售成本");
                grid.setTotal(Long.valueOf(1));
                grid.setSuccess(true);
                grid.setRows(Arrays.asList(resvos));
            }
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


    // 工业成本结转
    public ReturnData<Grid> onIndustrycbjz(QmclVO qmvo, UserVO userVO) {
        Grid grid = new Grid();
        String userid = userVO.getCuserid();
        try {
            // 上一期成本是否结转校验
            gl_qmclnoicserv.judgeLastPeriod(qmvo.getPk_corp(), userid, qmvo.getPeriod(), String.valueOf(qmvo.getIcosttype()));
            CorpVO cpvo = corpService.queryByPk(qmvo.getPk_corp());
            if(IcCostStyle.IC_ON.equals(cpvo.getBbuildic())){ //启用进销存的
                grid.setSuccess(true);
                grid.setMsg("启用库存工业结转");
                grid.setTotal(Long.valueOf(1));
                grid.setSuccess(true);
                grid.setRows(Arrays.asList(qmvo));
            } else if(IcCostStyle.IC_INVTENTORY.equals(cpvo.getBbuildic())){//启用总账存货
                InventorySetVO setvo = gl_ic_invtorysetserv.query(qmvo.getPk_corp());
                String error = inventory_setcheck.checkInventorySet(userid, qmvo.getPk_corp(),setvo);
                if (!StringUtil.isEmpty(error)) {
                    error = error.replaceAll("<br>", " ");
                    throw new BusinessException("成本结转失败！"+error);
                }
                throw new BusinessException("总账存货模式暂不支持工业结转！");
            } else {//不启用进销存的
                grid.setSuccess(true);
                grid.setMsg("不启用库存工业结转");
                grid.setTotal(Long.valueOf(1));
                grid.setSuccess(true);
                grid.setRows(Arrays.asList(qmvo));
            }
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
//                    msg.append("公司：").append(qmclvo.getCorpname()).append(",期间").append(period);
//                    if (qmclvo.getIscbjz() == null || !qmclvo.getIscbjz().booleanValue()) {
//                        msg.append("成本结转取消成功!<br>");
//                        continue;
//                    }
                    qmclvo = gl_qmclserv.rollbackCbjz(qmclvo);
                    list.add(qmclvo);
                } catch (Exception e) {
                    log.error("反成本结转:", e);
                    if (e instanceof BusinessException) {
                        msg.append(e.getMessage());
                    } else {
                        msg.append("成本结转取消失败!");
                    }
                    msg.append("<br>");
                }
            }
            if(msg != null && msg.length() > 0){
                grid.setMsg(msg.toString());
                grid.setRows(list);
                grid.setSuccess(false);
            }else{
                grid.setMsg("成本结转取消成功");
                grid.setRows(list);
                grid.setSuccess(true);
            }
        }catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "取消成本结转失败！");
        }
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/checkCbjzmb")
    public ReturnData<Grid> checkCbjzmb(@MultiRequestBody("jztype")  String jztype,
                                        @MultiRequestBody("pk_gs")  String pk_gs,
                                        @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            String userid = userVO.getCuserid();
            if(!StringUtil.isEmpty(jztype)){
                if("1".equalsIgnoreCase(jztype) || "3".equalsIgnoreCase(jztype))
                    gl_qmclnoicserv.checkCbjzmb(pk_gs, jztype);
            }
            grid.setSuccess(true);
            grid.setMsg("成本模板校验成功!");
        } catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "成本模板校验失败！");
        }
        return ReturnData.ok().data(grid);
    }


    //查询存货、材料相关科目(不启用库存，成本结转对话框)
    @PostMapping("/queryCBJZKM")
    public ReturnData<Grid> queryCBJZKM(@MultiRequestBody("jztype")  String jztype,
                                        @MultiRequestBody("pk_gs")  String pk_gs,
                                        @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            String userid = userVO.getCuserid();
            if(!StringUtil.isEmpty(jztype)){
                if("1".equalsIgnoreCase(jztype) || "3".equalsIgnoreCase(jztype))
                    gl_qmclnoicserv.checkCbjzmb(pk_gs, jztype);
            }
            List<QMJzsmNoICVO> vos = gl_qmclnoicserv.queryCBJZAccountVOS(pk_gs, userid, jztype);
            if (vos != null && vos.size() > 0) {
                grid.setRows(vos);
            }
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
        }catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "查询表体加载科目信息失败！");
        }
        return ReturnData.ok().data(grid);
    }

    // 计算
    @PostMapping("/jisuan")
    public ReturnData<Grid> jisuan(@MultiRequestBody("begindate")  String begindate,
                                   @MultiRequestBody("enddate")  String enddate,
                                   @MultiRequestBody("kmbms")  String kmbm,
                                   @MultiRequestBody("pk_gs")  String pk_gs,
                                   @MultiRequestBody("jztype")  String jztype, @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        String[] kmbms = null;
        if (kmbm != null && kmbm.length() > 0) {
            kmbm = kmbm.replaceAll("\"", "");
            kmbm = kmbm.substring(1);
            kmbm = kmbm.substring(0, kmbm.length() - 1);
            if(kmbm != null && kmbm.length() > 0){
                kmbms = kmbm.split(",");
            }
        }
        try {
            String userid = userVO.getCuserid();
            List<QMJzsmNoICVO> vos = gl_qmclnoicserv.queryCBJZqcpzAccountVOS(pk_gs,userid, begindate, enddate, kmbms, jztype);
            if (vos != null && vos.size() > 0) {
                Collections.sort(vos, new Comparator<QMJzsmNoICVO>() {
                    @Override
                    public int compare(QMJzsmNoICVO o1, QMJzsmNoICVO o2) {
                        return o1.getKmbm().compareTo(o2.getKmbm());
                    }
                });
                grid.setRows(vos);
            }
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
        }catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "计算失败！");
        }
        return ReturnData.ok().data(grid);
    }

    // 生成凭证
    @PostMapping("/saveToPz")
    public ReturnData<Grid> saveToPz(@MultiRequestBody("qmvo")  QmclVO qmvo,
                                     @MultiRequestBody("noicjzvos")  QMJzsmNoICVO[]  noicjzvos,
                                     @MultiRequestBody("zgdata")  TempInvtoryVO[]  zgdata,
                                     @MultiRequestBody("cbjzCount")  String cbjzCount,//不启用库存，(工业结转) 成本结转步骤
                                     @MultiRequestBody("jztype")  String jztype,//不启用库存，(工业结转)  结转类型[材料成本结转、完工入库结转、销售成本结转]
                                     @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        Map<QmclVO, List<QMJzsmNoICVO>> map = new HashMap<QmclVO, List<QMJzsmNoICVO>>();
        if(qmvo != null){
            qmvo.setZgdata(zgdata);
            map.put(qmvo,new ArrayList<QMJzsmNoICVO>(Arrays.asList(noicjzvos)));
        }
        try {
            String userid = userVO.getCuserid();
            QmclVO vos = gl_qmclnoicserv.saveToSalejzVoucher(userid, map, jztype, cbjzCount, "");
            grid.setSuccess(true);
            grid.setMsg("成本结转成功!");
            grid.setTotal((long) 1);
            grid.setSuccess(true);
            grid.setRows(Arrays.asList(vos));
        } catch (ExBusinessException ex) {
            Map<String, List<TempInvtoryVO>> mapzg = ex.getLmap();
            Collection<List<TempInvtoryVO>> cl = mapzg.values();
            List<TempInvtoryVO> cvos = addTempInvtoryVO(cl);
            if(cvos != null && cvos.size() > 1){
                Collections.sort(cvos, new Comparator<TempInvtoryVO>() {
                    @Override
                    public int compare(TempInvtoryVO o1, TempInvtoryVO o2) {
                        return VOSortUtils.compareContainsNull(o1.getKmbm(), o2.getKmbm());
                    }
                });
            }
            Grid grid1 = new Grid();
            grid1.setSuccess(true);
            grid1.setMsg("暂估");
            grid1.setRows(cvos.toArray(new TempInvtoryVO[0]));
            return ReturnData.ok().data(grid1);
        }catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "成本结转失败！");
        }
        return ReturnData.ok().data(grid);
    }


    // 第一步:工业结转明细
    @PostMapping("/queryIndustCFVO")
    public ReturnData<Grid> queryIndustCFVO(@MultiRequestBody("qmvo")  QmclVO qmvo,
                                            @MultiRequestBody("isgy")  String isgy) {
        Grid grid = new Grid();
        try {
            boolean isgybool = false;
            if (isgy != null && "Y".equals(isgy)) {
                isgybool = true;
            }
            List<CostForwardVO> list = gl_industryserv.queryIndustCFVO(qmvo, isgybool);
            grid.setMsg("查询成功!");
            grid.setTotal((long) list.size());
            grid.setSuccess(true);
            grid.setRows(list);
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "工业结转明细失败");
            log.error("工业结转明细失败!", e);
        }
        return ReturnData.ok().data(grid);
    }

    // 第二步:结转所有辅助生产成本至制造费用
    @PostMapping("/secondquery")
    public ReturnData<Grid> secondquery(@MultiRequestBody("qmvo")  QmclVO qmvo,
                                        @MultiRequestBody("cbjzPara0")  CostForwardVO[] cbjzPara0,
                                        @MultiRequestBody("isgy")  String isgy) {
        Grid grid = new Grid();
        grid.setSuccess(true);
        grid.setRows(new ArrayList<CostForwardVO>());
        try {
            String pk_corp = qmvo.getPk_corp();
            if (cbjzPara0 != null && cbjzPara0.length > 0) {
                List<CostForwardVO> listz = secondOperate(cbjzPara0,isgy,pk_corp);
                grid.setMsg("操作成功!");
                grid.setTotal((long) listz.size());
                grid.setRows(listz);
            }
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "第二步工业结转,结转所有辅助生产成本至制造费用失败!");
            log.error("第二步工业结转,结转所有辅助生产成本至制造费用失败!", e);
        }
        return ReturnData.ok().data(grid);
    }


    private List<CostForwardVO> secondOperate(CostForwardVO[] cbjzPara0,String isgy,String pk_corp){
        List<CostForwardVO> listz = new ArrayList<CostForwardVO>();
        if (isgy != null && "Y".equals(isgy)) {
            String str = gl_cbconstant.getFzcb2007(pk_corp);// 500102
            String str1 = gl_cbconstant.getFzcb2013(pk_corp);// 400102
            String str3 = gl_cbconstant.getZzfy2007();
            String str4 = gl_cbconstant.getZzfy2013();
            List<String> mjkmbm = gl_qmclserv.getMjkmbms(str, pk_corp);
            List<String> mjkmbm1 = gl_qmclserv.getMjkmbms(str1, pk_corp);
            String mjkmbm3 = gl_qmclserv.getMjkmbm(str3, pk_corp);
            String mjkmbm4 = gl_qmclserv.getMjkmbm(str4, pk_corp);
            for (CostForwardVO v : cbjzPara0) {
                CostForwardVO v1 = null;
                CostForwardVO v2 = null;
                if (mjkmbm.contains(v.getVcode())) {// 辅助成本
                    v1 = gl_industryserv.createsecZJVO(pk_corp, v, true, mjkmbm3);// 借方
                    v2 = gl_industryserv.createsecZJVO(pk_corp, v, false, mjkmbm3);// 贷方
                    listz.add(v1);
                    listz.add(v2);
                } else if (mjkmbm1.contains(v.getVcode())) {// 辅助成本
                    v1 = gl_industryserv.createsecZJVO(pk_corp, v, true, mjkmbm4);// 借方
                    v2 = gl_industryserv.createsecZJVO(pk_corp, v, false, mjkmbm4);// 贷方
                    listz.add(v1);
                    listz.add(v2);
                }
            }
        } else {
            for (CostForwardVO v : cbjzPara0) {
                CostForwardVO v1 = null;
                CostForwardVO v2 = null;
                if (gl_cbconstant.getFzcb2007(pk_corp).equals(v.getVcode())) {// 辅助成本
                    v1 = gl_industryserv.createsecZJVO(pk_corp, v, true, gl_cbconstant.getZzfy2007());// 借方
                    v2 = gl_industryserv.createsecZJVO(pk_corp, v, false, gl_cbconstant.getZzfy2007());// 贷方
                    listz.add(v1);
                    listz.add(v2);
                } else if (gl_cbconstant.getFzcb2013(pk_corp).equals(v.getVcode())) {// 辅助成本
                    v1 = gl_industryserv.createsecZJVO(pk_corp, v, true, gl_cbconstant.getZzfy2013());// 借方
                    v2 = gl_industryserv.createsecZJVO(pk_corp, v, false, gl_cbconstant.getZzfy2013());// 贷方
                    listz.add(v1);
                    listz.add(v2);
                }
            }
        }
        return listz;
    }

    // 第三步:结转所有制造费用到生产成本--基本生产成本--制造费用
    @PostMapping("/thirdquery")
    public ReturnData<Grid> thirdquery(@MultiRequestBody("qmvo")  QmclVO qmvo,
                                       @MultiRequestBody("cbjzPara0")  CostForwardVO[] cbjzPara0,
                                       @MultiRequestBody("cbjzPara1")  CostForwardVO[] cbjzPara1) {
        Grid grid = new Grid();
        grid.setSuccess(true);
        grid.setRows(new ArrayList<CostForwardVO>());
        try {
            String pk_corp = qmvo.getPk_corp();
            List<CostForwardVO> list1 = new ArrayList<CostForwardVO>(Arrays.asList(cbjzPara0));
            // List<CostForwardVO> list1 = new ArrayList<CostForwardVO>();
            List<CostForwardVO> list2 = new ArrayList<CostForwardVO>(Arrays.asList(cbjzPara1));
            if (list1 != null && list1.size() > 0) {
                List<CostForwardVO> listz = thirdOperate(pk_corp,list1,list2);
                grid.setMsg("操作成功!");
                grid.setSuccess(true);
                grid.setTotal((long) listz.size());
                grid.setRows(listz);
            }
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "第三步工业结转,结转所有制造费用到生产成本失败!");
            log.error("第三步工业结转,结转所有制造费用到生产成本失败!", e);
        }
        return ReturnData.ok().data(grid);
    }

    private List<CostForwardVO> thirdOperate(String pk_corp, List<CostForwardVO> list1, List<CostForwardVO> list2){
        // if(true){
        if (list2 != null && list2.size() > 0) {
            list1.addAll(list2);
        }
        // 合并制造费用list1
        CostForwardVO zzfyvo = null;
        DZFDouble jfmny = DZFDouble.ZERO_DBL;
        String str3 = gl_cbconstant.getZzfy2007();
        String str4 = gl_cbconstant.getZzfy2013();
        String jbcb_zzfy2007 = gl_cbconstant.getJbcb_zzfy2007(pk_corp);
        String jbcb_zzfy2013 = gl_cbconstant.getJbcb_zzfy2013(pk_corp);
        List<String> listzzfy07 = gl_qmclserv.getMjkmbms(str3, pk_corp);
        List<String> listzzfy13 = gl_qmclserv.getMjkmbms(str4, pk_corp);
        String jbzzfy07 = gl_qmclserv.getMjkmbm(jbcb_zzfy2007, pk_corp);
        String jbzzfy13 = gl_qmclserv.getMjkmbm(jbcb_zzfy2013, pk_corp);
        List<CostForwardVO> listz = new ArrayList<CostForwardVO>();
        for (CostForwardVO v : list1) {
            // if(v.getZy()!=null){
            if (listzzfy07.contains(v.getVcode()) || listzzfy13.contains(v.getVcode())) {// 制造费用
                // if("结转所有辅助生产成本至制造费用".equals(v.getZy())){
                // continue;
                // }
                zzfyvo = (CostForwardVO) v.clone();
                jfmny = jfmny.add(v.getJfmny());
                if (listzzfy07.contains(zzfyvo.getVcode())) {// 制造费用
                    CostForwardVO vo2 = gl_industryserv.createthirdZJVO(pk_corp, zzfyvo, false, jbzzfy07);// 贷方
                    listz.add(vo2);
                } else if (listzzfy13.contains(zzfyvo.getVcode())) {// 制造费用
                    CostForwardVO vo2 = gl_industryserv.createthirdZJVO(pk_corp, zzfyvo, false, jbzzfy13);// 贷方
                    listz.add(vo2);
                }
            }
            // }
        }
        if (zzfyvo != null) {
            zzfyvo.setJfmny(jfmny);
            if (listzzfy07.contains(zzfyvo.getVcode())) {// 制造费用
                CostForwardVO vo1 = gl_industryserv.createthirdZJVO(pk_corp, zzfyvo, true, jbzzfy07);// 借方
                listz.add(0, vo1);
            } else if (listzzfy13.contains(zzfyvo.getVcode())) {// 制造费用
                CostForwardVO vo1 = gl_industryserv.createthirdZJVO(pk_corp, zzfyvo, true, jbzzfy13);// 借方
                listz.add(0, vo1);
            }
        }
        return listz;
    }


    // 第四步：比例分配材料、人工、制造费用
    @PostMapping("/fourthquery")
    public ReturnData<Grid> fourthquery(@MultiRequestBody("qmvo")  QmclVO qmvo,
                                        @MultiRequestBody("cbjzPara2")  CostForwardVO[] cbjzPara2) {
        Grid grid = new Grid();
        grid.setSuccess(true);
        grid.setRows(new ArrayList<CostForwardVO>());
        try {
            List<CostForwardInfo> info = forthoperate(qmvo,cbjzPara2,true,"");
            grid.setMsg("操作成功!");
            grid.setTotal((long) info.size());
            grid.setRows(info);
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "第四步工业结转,比例分配材料、人工、制造费用失败!");
            log.error("第四步工业结转,比例分配材料、人工、制造费用失败!", e);
        }
        return ReturnData.ok().data(grid);
    }

    private List<CostForwardInfo> forthoperate (QmclVO qmvo,CostForwardVO[] cbjzPara2,boolean isic,String jztype) {
        // 获取前台数据
        List<CostForwardInfo> info = null;
        if(isic){
            info = (List<CostForwardInfo>) gl_industryserv.queryIndustQCInvtory(qmvo);
        }else{
//            info = (List<CostForwardInfo>) gl_industryserv.queryIndustQCInvtoryNOIC(qmvo,jztype);
            // zpm 先让他走 库存的。都是取总账数据。 2019.12.7
            info = (List<CostForwardInfo>) gl_industryserv.queryIndustQCInvtory(qmvo);
        }
        // 增加CostForwardInfo[0][生产成本-辅助生产成本-制造费用]信息
        DZFDouble zzfy_zhuanchu = null;
        if (!DZFValueCheck.isEmpty(cbjzPara2)) {
            List<CostForwardVO> list3 = new ArrayList<CostForwardVO>(Arrays.asList(cbjzPara2));
            if (list3 != null && list3.size() > 0) {
                String zzfy2007 = gl_cbconstant.getJbcb_zzfy2007(qmvo.getPk_corp());
                String zzfy2013 = gl_cbconstant.getJbcb_zzfy2013(qmvo.getPk_corp());
                for (CostForwardVO vo : list3) {
                    if (vo.getVcode().startsWith(zzfy2007) || vo.getVcode().startsWith(zzfy2013)) {
                        zzfy_zhuanchu = vo.getJfmny();
                    }
                }
            }
        }
        DZFDouble zsum = SafeCompute.add(info.get(0).getNzhizao_fs(), zzfy_zhuanchu);
        info.get(0).setNzhizao_fs(zsum);
        info.get(1).setNzhizao_fs(zsum);
        if (info.get(0).getNcailiao_fs() == null) {
            info.get(0).setNcailiao_fs(DZFDouble.ZERO_DBL);
        }
        if (info.get(0).getNrengong_fs() == null) {
            info.get(0).setNrengong_fs(DZFDouble.ZERO_DBL);
        }
        // info.get(0).setNcailiao_fs(DZFDouble.ZERO_DBL);
        // info.get(0).setNrengong_fs(DZFDouble.ZERO_DBL);
        //
        return info;
    }

    // 第五步 :本月完工分配材料及人工制造费用
    @PostMapping("/fivequery")
    public ReturnData<Grid> fivequery(@MultiRequestBody("qmvo")  QmclVO qmvo,
                                      @MultiRequestBody("cbjzPara3")  CostForwardInfo[] cbjzPara3) {
        Grid grid = new Grid();
        grid.setSuccess(true);
        grid.setRows(new ArrayList<CostForwardVO>());
        try {
            if (cbjzPara3 != null && cbjzPara3.length > 0) {
                String pk_corp = qmvo.getPk_corp();
                List<CostForwardVO> zlistjf = new ArrayList<CostForwardVO>();
                List<CostForwardVO> zlistdf = new ArrayList<CostForwardVO>();
                boolean is2007 = gl_industryserv.is2007(pk_corp);

                String clcode = null;
                String rgcode = null;
                String zgcode = null;
                if (is2007) {
                    clcode = gl_cbconstant.getJbcb_zjcl2007(pk_corp);
                    rgcode = gl_cbconstant.getJbcb_zjrg2007(pk_corp);
                    zgcode = gl_cbconstant.getJbcb_zzfy2007(pk_corp);
                } else {
                    clcode = gl_cbconstant.getJbcb_zjcl2013(pk_corp);
                    rgcode = gl_cbconstant.getJbcb_zjrg2013(pk_corp);
                    zgcode = gl_cbconstant.getJbcb_zzfy2013(pk_corp);
                }

                for (int i = 0; i < cbjzPara3.length; i++) {
                    if (i == 0) {// 贷方
                        DZFDouble z1 = cbjzPara3[0].getNcailiao_wg();
                        DZFDouble z2 = cbjzPara3[0].getNrengong_wg();
                        DZFDouble z3 = cbjzPara3[0].getNzhizao_wg();
                        CostForwardVO lvo1 = null;
                        CostForwardVO lvo2 = null;
                        CostForwardVO lvo3 = null;
                        if (is2007) {
                            lvo1 = gl_industryserv.createfiveZJVO(pk_corp, false, clcode, z1, null, null, null);
                            lvo2 = gl_industryserv.createfiveZJVO(pk_corp, false, rgcode, z2, null, null, null);
                            lvo3 = gl_industryserv.createfiveZJVO(pk_corp, false, zgcode, z3, null, null, null);
                        } else {
                            lvo1 = gl_industryserv.createfiveZJVO(pk_corp, false, clcode, z1, null, null, null);
                            lvo2 = gl_industryserv.createfiveZJVO(pk_corp, false, rgcode, z2, null, null, null);
                            lvo3 = gl_industryserv.createfiveZJVO(pk_corp, false, zgcode, z3, null, null, null);
                        }
                        if (lvo1 != null)
                            zlistdf.add(lvo1);
                        if (lvo2 != null)
                            zlistdf.add(lvo2);
                        if (lvo3 != null)
                            zlistdf.add(lvo3);
                    } else {// 借方
                        DZFDouble zd1 = cbjzPara3[i].getNcailiao_wg();
                        DZFDouble zd2 = cbjzPara3[i].getNrengong_wg();
                        DZFDouble zd3 = cbjzPara3[i].getNzhizao_wg();
                        DZFDouble zd4 = SafeCompute.add(SafeCompute.add(zd1, zd2), zd3);
                        DZFDouble num = cbjzPara3[i].getNnum_wg();
                        String pk_inv = cbjzPara3[i].getPk_inventory();
                        String vname = cbjzPara3[i].getVname();
                        CostForwardVO lvo = gl_industryserv.createfiveZJVO(pk_corp, true, gl_cbconstant.getKcsp_code(),
                                zd4, num, pk_inv, vname);
                        if (lvo != null)
                            zlistjf.add(lvo);
                    }
                }
                zlistjf.addAll(zlistdf);
                grid.setMsg("操作成功!");
                grid.setTotal((long) zlistjf.size());
                grid.setRows(zlistjf);
            }
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "第五步工业结转,本月完工分配材料及人工制造费用失败!");
            log.error("第五步工业结转,本月完工分配材料及人工制造费用失败!", e);
        }
        return ReturnData.ok().data(grid);
    }


    // 第六步 :工业成本结转保存
    @PostMapping("/onIndustrySave")
    public ReturnData<Grid> onIndustrySave(@MultiRequestBody("qmvo")  QmclVO qmvo,
                                           @MultiRequestBody("cbjzPara0")  CostForwardVO[] cbjz0,
                                           @MultiRequestBody("cbjzPara1")  CostForwardVO[] cbjz1,
                                           @MultiRequestBody("cbjzPara2")  CostForwardVO[] cbjz2,
                                           @MultiRequestBody("cbjzPara3")  CostForwardInfo[] cbjz3,
                                           @MultiRequestBody("cbjzPara4")  CostForwardVO[] cbjz4,
                                           @MultiRequestBody("zgdata")  TempInvtoryVO[]  zgdata,
                                           @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            List<CostForwardVO> list1 = null; // 第一步数据
            List<CostForwardVO> list2 = null; // 第二步数据
            List<CostForwardVO> list3 = null;// 第三步数据
            CostForwardInfo[] list4 = null;// 第四步数据
            List<CostForwardVO> list5 = null;// 第五步数据
            if (cbjz0 != null && cbjz0.length > 0) {
                list1 = new ArrayList<CostForwardVO>(Arrays.asList(cbjz0));
            }
            if (cbjz1 != null && cbjz1.length > 0) {
                list2 = new ArrayList<CostForwardVO>(Arrays.asList(cbjz1));
            }
            if (cbjz2 != null && cbjz2.length > 0) {
                list3 = new ArrayList<CostForwardVO>(Arrays.asList(cbjz2));
            }
            list4 = cbjz3;
            if (cbjz4 != null && cbjz4.length > 0) {
                list5 = new ArrayList<CostForwardVO>(Arrays.asList(cbjz4));
            }
            // 暂估数据
            qmvo.setZgdata(zgdata);
            TransFerVOInfo fervo = new TransFerVOInfo();
            fervo.setCostforwardvolist1(list1);
            fervo.setCostforwardvolist2(list2);
            fervo.setCostforwardvolist3(list3);
            fervo.setCostforwardvolist4(list4);
            fervo.setCostforwardvolist5(list5);
            fervo.setQmvo(qmvo);
            //
            QmclVO resvos = gl_qmclserv.saveIndustryJZ(fervo, userVO.getCuserid());
            grid.setMsg("工业成本结转保存成功!");
            grid.setTotal(Long.valueOf(1));
            grid.setSuccess(true);
            List<QmclVO> zlist = new ArrayList<QmclVO>();
            zlist.add(resvos);
            grid.setRows(zlist);
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
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "工业成本结转保存失败！");
        }
        return ReturnData.ok().data(grid);
    }


    /**
     * 工业期末处理 完工计算
     * @param qmvo
     * @param qmwgcpvos
     * @param fsbl
     * @param bili1
     * @param userVO
     * @return
     */
    @PostMapping("/onIndustryCalc")
    public ReturnData<Grid> onIndustryCalc(@MultiRequestBody("qmvo")  QmclVO qmvo,
                                           @MultiRequestBody("qmwgcpvo")  QmWgcpVO[] qmwgcpvos,
                                           @MultiRequestBody("fsbl")  String fsbl,
                                           @MultiRequestBody("bili")  String bili1,
                                           @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            if (qmwgcpvos==null  || qmwgcpvos.length == 0) {
                throw new BusinessException("完工处理，计算表体不允许为空!");
            }
            if (StringUtil.isEmpty(bili1)){
                throw new BusinessException("完工比例数不允许为空!");
            }
            DZFDouble bili = new DZFDouble(bili1);
            if (qmwgcpvos != null && qmwgcpvos.length > 0) {
                String pk_corp = qmvo.getPk_corp();
                CorpVO corpvo = corpService.queryByPk(pk_corp);
                if (IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
                    jisuanIc(fsbl, bili, qmwgcpvos, qmvo);
                } else {
                    jisuanNoIc(fsbl, bili, qmwgcpvos, qmvo);
                }
                grid.setRows(qmwgcpvos);
                grid.setSuccess(true);
            }
        }  catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "工业成本计算失败！");
        }
        return ReturnData.ok().data(grid);
    }


    private void jisuanIc(String fsbl, DZFDouble bili, QmWgcpVO[] bodyvos, QmclVO qmvo) {
        if (fsbl != null && "1".equals(fsbl)) {// 单价
            calcgyjzByprice(bodyvos, qmvo);// 先计算比例,在按比例计算
            QmgyjzVOs gyvo = calcgyBodyvosIc(bodyvos);
            // calcgyjzbyPercent(bodyvos, bili, gyvo);
            calcgyjzbyPercent1ByRowBLIc(bodyvos, bili, gyvo);
        } else if (fsbl != null && "2".equals(fsbl)) {// 比例
            QmgyjzVOs gyvo = calcgyBodyvosIc(bodyvos);
            // calcgyjzbyPercent(bodyvos, bili, gyvo);
            calcgyjzbyPercent1ByRowBLIc(bodyvos, bili, gyvo);
        } else if (fsbl != null && "3".equals(fsbl)) {// 金额
            QmgyjzVOs gyvo = calcgyBodyvosIc(bodyvos);
            // calcgyjzbyMny(bodyvos, bili, gyvo);
            calcgyjzbyMnyByRowBLIc(bodyvos, bili, gyvo);
        }
    }

    private void jisuanNoIc(String fsbl, DZFDouble bili, QmWgcpVO[] bodyvos, QmclVO qmvo) {
        if (fsbl != null && "1".equals(fsbl)) {// 单价
            calcgyjzByprice(bodyvos, qmvo);// 先计算比例,在按比例计算
            QmgyjzVOs gyvo = calcgyBodyvos(bodyvos);
            // calcgyjzbyPercent(bodyvos, bili, gyvo);
            calcgyjzbyPercent1ByRowBL(bodyvos, bili, gyvo);
        } else if (fsbl != null && "2".equals(fsbl)) {// 比例
            QmgyjzVOs gyvo = calcgyBodyvos(bodyvos);
            // calcgyjzbyPercent(bodyvos, bili, gyvo);
            calcgyjzbyPercent1ByRowBL(bodyvos, bili, gyvo);
        } else if (fsbl != null && "3".equals(fsbl)) {// 金额
            QmgyjzVOs gyvo = calcgyBodyvos(bodyvos);
            // calcgyjzbyMny(bodyvos, bili, gyvo);
            calcgyjzbyMnyByRowBL(bodyvos, bili, gyvo);
        }
    }

    private void calcgyjzByprice(QmWgcpVO[] bodyvos, QmclVO qmvo) {
        if (bodyvos == null || bodyvos.length == 0)
            return;
        if (qmvo == null || StringUtil.isEmpty(qmvo.getPk_corp())) {
            throw new BusinessException("计算参数请求错误");
        }
        String[] pks = getQmWgcpVOpk(bodyvos);
        if (pks == null || pks.length == 0) {
            return;
        }
        Map<String, InventoryVO> chmap = ic_inventoryserv.queryInventoryVOs(qmvo.getPk_corp(), pks);
        DZFDouble sumje = null;
        QmWgcpVO firstvo = bodyvos[0];
        for (int i = 0; i < bodyvos.length; i++) {
            if (i > 0) {// 不包括第一行 数据
                if (numberToZero(bodyvos[i].getNnum_wg()).doubleValue() <= 0) {
                    // throw new BusinessException("按单价分摊，请输入各商品的完工数量");
                    continue;
                }
                // 判断是否有单价
                InventoryVO vo = chmap.get(bodyvos[i].getPk_inventory());
                if (vo == null || vo.getJsprice() == null || vo.getJsprice().doubleValue() <= 0) {
                    throw new BusinessException(bodyvos[i].getVname() + "，单价没有设置，请到存货档案维护");
                }
                bodyvos[i].setPrice(vo.getJsprice());
                DZFDouble mny = SafeCompute.multiply(bodyvos[i].getNnum_wg(), bodyvos[i].getPrice()).setScale(2,
                        DZFDouble.ROUND_HALF_UP);
                bodyvos[i].setMny(mny);
                sumje = SafeCompute.add(sumje, mny);
            }
        }
        // 计算比例，比例保留两位有效数字
        DZFDouble percent = new DZFDouble(100);
        DZFDouble cent = null;
        for (int i = 0; i < bodyvos.length; i++) {
            if (i == bodyvos.length - 1) {
                setDefaultVaule(bodyvos[i], firstvo, SafeCompute.sub(percent, cent));
            } else if (i > 0) {
                DZFDouble mny = bodyvos[i].getMny();
                DZFDouble calcpercent = SafeCompute.multiply(SafeCompute.div(mny, sumje), percent).setScale(2,
                        DZFDouble.ROUND_HALF_UP);
                cent = SafeCompute.add(cent, calcpercent);
                setDefaultVaule(bodyvos[i], firstvo, calcpercent);
            }
        }
    }

    private void setDefaultVaule(QmWgcpVO cpvo, QmWgcpVO firstvo, DZFDouble calcpercent) {
        if (cpvo == null)
            return;
        if (numberToZero(firstvo.getNcailiao_qc()).doubleValue() != 0) {
            cpvo.setNcailiao_qc(calcpercent);
        }
        if (numberToZero(firstvo.getNrengong_qc()).doubleValue() != 0) {
            cpvo.setNrengong_qc(calcpercent);
        }
        if (numberToZero(firstvo.getNzhizao_qc()).doubleValue() != 0) {
            cpvo.setNzhizao_qc(calcpercent);
        }
        if (numberToZero(firstvo.getNcailiao_fs()).doubleValue() != 0) {
            cpvo.setNcailiao_fs(calcpercent);
        }
        if (numberToZero(firstvo.getNrengong_fs()).doubleValue() != 0) {
            cpvo.setNrengong_fs(calcpercent);
        }
        if (numberToZero(firstvo.getNzhizao_fs()).doubleValue() != 0) {
            cpvo.setNzhizao_fs(calcpercent);
        }
    }

    private QmgyjzVOs calcgyBodyvosIc(QmWgcpVO[] bodyvos) {
        if (bodyvos == null || bodyvos.length == 0)
            return null;
        int len = bodyvos.length;
        DZFDouble z_f_cailiao_qc = DZFDouble.ZERO_DBL;// 第1行--材料期初
        DZFDouble z_f_rengong_qc = DZFDouble.ZERO_DBL;// 第1行--人工期初
        DZFDouble z_f_zzfy_qc = DZFDouble.ZERO_DBL;// 第1行--制造费用期初
        DZFDouble z_f_cailiao = DZFDouble.ZERO_DBL;// 第1行-材料发生
        DZFDouble z_f_rengong = DZFDouble.ZERO_DBL;// 第1行-人工发生
        DZFDouble z_f_zzfy = DZFDouble.ZERO_DBL;// 第1行-制造费用发生
        //
        DZFDouble f_cailiao_qc = DZFDouble.ZERO_DBL;// 第2行以后--[比例]----材料----期初
        DZFDouble f_rengong_qc = DZFDouble.ZERO_DBL;
        DZFDouble f_zzfy_qc = DZFDouble.ZERO_DBL;
        DZFDouble f_cailiao_bl = DZFDouble.ZERO_DBL;// 第2行以后--[比例]---材料----发生
        DZFDouble f_rengong_bl = DZFDouble.ZERO_DBL;
        DZFDouble f_zzfy_bl = DZFDouble.ZERO_DBL;
        //
        DZFDouble cailiao = DZFDouble.ZERO_DBL;// 第2行以后----[金额] ---材料---期初
        DZFDouble rengong = DZFDouble.ZERO_DBL;
        DZFDouble zzfy = DZFDouble.ZERO_DBL;
        DZFDouble f_f_cailiao = DZFDouble.ZERO_DBL;// 第2行以后---[金额]----材料---发生
        DZFDouble f_f_rengong = DZFDouble.ZERO_DBL;
        DZFDouble f_f_zzfy = DZFDouble.ZERO_DBL;
        for (int i = 0; i < len; i++) {
            if (i == 0) {
                if (bodyvos[i].getNcailiao_fs() != null)
                    z_f_cailiao = numberToZero(bodyvos[i].getNcailiao_fs());
                if (bodyvos[i].getNrengong_fs() != null)
                    z_f_rengong = numberToZero(bodyvos[i].getNrengong_fs());
                if (bodyvos[i].getNzhizao_fs() != null)
                    z_f_zzfy = numberToZero(bodyvos[i].getNzhizao_fs());
                //
                if (bodyvos[i].getNcailiao_qc() != null) {
                    z_f_cailiao_qc = numberToZero(bodyvos[i].getNcailiao_qc());
                }
                if (bodyvos[i].getNrengong_qc() != null) {
                    z_f_rengong_qc = numberToZero(bodyvos[i].getNrengong_qc());
                }
                if (bodyvos[i].getNzhizao_qc() != null) {
                    z_f_zzfy_qc = numberToZero(bodyvos[i].getNzhizao_qc());
                }
            } else if (i == 1) {

            } else {
                if (bodyvos[i].getNcailiao_fs() != null) {
                    f_cailiao_bl = SafeCompute.add(f_cailiao_bl, numberToZero(bodyvos[i].getNcailiao_fs()));
                    f_f_cailiao = SafeCompute.add(f_f_cailiao, numberToZero(bodyvos[i].getNcailiao_fs()));
                }
                if (bodyvos[i].getNrengong_fs() != null) {
                    f_rengong_bl = SafeCompute.add(f_rengong_bl, numberToZero(bodyvos[i].getNrengong_fs()));
                    f_f_rengong = SafeCompute.add(f_f_rengong, numberToZero(bodyvos[i].getNrengong_fs()));
                }
                if (bodyvos[i].getNzhizao_fs() != null) {
                    f_zzfy_bl = SafeCompute.add(f_zzfy_bl, numberToZero(bodyvos[i].getNzhizao_fs()));
                    f_f_zzfy = SafeCompute.add(f_f_zzfy, numberToZero(bodyvos[i].getNzhizao_fs()));
                }
                //
                if (bodyvos[i].getNcailiao_qc() != null) {
                    f_cailiao_qc = SafeCompute.add(f_cailiao_qc, numberToZero(bodyvos[i].getNcailiao_qc()));
                    cailiao = SafeCompute.add(cailiao, numberToZero(bodyvos[i].getNcailiao_qc()));
                }
                if (bodyvos[i].getNrengong_qc() != null) {
                    f_rengong_qc = SafeCompute.add(f_rengong_qc, numberToZero(bodyvos[i].getNrengong_qc()));
                    rengong = SafeCompute.add(rengong, numberToZero(bodyvos[i].getNrengong_qc()));
                }
                if (bodyvos[i].getNzhizao_qc() != null) {
                    f_zzfy_qc = SafeCompute.add(f_zzfy_qc, numberToZero(bodyvos[i].getNzhizao_qc()));
                    zzfy = SafeCompute.add(zzfy, numberToZero(bodyvos[i].getNzhizao_qc()));
                }
            }
        }
        QmgyjzVOs vo = new QmgyjzVOs();
        vo.setZ_f_cailiao_qc(z_f_cailiao_qc);// 第1行--材料期初
        vo.setZ_f_rengong_qc(z_f_rengong_qc);// 第1行--人工期初
        vo.setZ_f_zzfy_qc(z_f_zzfy_qc);// 第1行--制造费用期初
        vo.setZ_f_cailiao(z_f_cailiao);// 第1行-材料发生
        vo.setZ_f_rengong(z_f_rengong);// 第1行-人工发生
        vo.setZ_f_zzfy(z_f_zzfy);// 第1行-制造费用发生
        vo.setCailiao(cailiao);// 第2行以后----[金额] ---材料---期初
        vo.setRengong(rengong);
        vo.setZzfy(zzfy);
        vo.setF_f_cailiao(f_f_cailiao);// 第2行以后---[金额]----材料---发生
        vo.setF_f_rengong(f_f_rengong);
        vo.setF_f_zzfy(f_f_zzfy);
        vo.setF_cailiao_qc(f_cailiao_qc);// 第2行以后--[比例]----材料----期初
        vo.setF_rengong_qc(f_rengong_qc);
        vo.setF_zzfy_qc(f_zzfy_qc);
        vo.setF_cailiao_bl(f_cailiao_bl);// 第2行以后--[比例]---材料----发生
        vo.setF_rengong_bl(f_rengong_bl);
        vo.setF_zzfy_bl(f_zzfy_bl);
        return vo;
    }

    private QmgyjzVOs calcgyBodyvos(QmWgcpVO[] bodyvos) {
        if (bodyvos == null || bodyvos.length == 0)
            return null;
        int len = bodyvos.length;
        DZFDouble z_f_cailiao_qc = DZFDouble.ZERO_DBL;// 第1行--材料期初
        DZFDouble z_f_rengong_qc = DZFDouble.ZERO_DBL;// 第1行--人工期初
        DZFDouble z_f_zzfy_qc = DZFDouble.ZERO_DBL;// 第1行--制造费用期初
        DZFDouble z_f_cailiao = DZFDouble.ZERO_DBL;// 第1行-材料发生
        DZFDouble z_f_rengong = DZFDouble.ZERO_DBL;// 第1行-人工发生
        DZFDouble z_f_zzfy = DZFDouble.ZERO_DBL;// 第1行-制造费用发生
        //
        DZFDouble f_cailiao_qc = DZFDouble.ZERO_DBL;// 第2行以后--[比例]----材料----期初
        DZFDouble f_rengong_qc = DZFDouble.ZERO_DBL;
        DZFDouble f_zzfy_qc = DZFDouble.ZERO_DBL;
        DZFDouble f_cailiao_bl = DZFDouble.ZERO_DBL;// 第2行以后--[比例]---材料----发生
        DZFDouble f_rengong_bl = DZFDouble.ZERO_DBL;
        DZFDouble f_zzfy_bl = DZFDouble.ZERO_DBL;
        //
        DZFDouble cailiao = DZFDouble.ZERO_DBL;// 第2行以后----[金额] ---材料---期初
        DZFDouble rengong = DZFDouble.ZERO_DBL;
        DZFDouble zzfy = DZFDouble.ZERO_DBL;
        DZFDouble f_f_cailiao = DZFDouble.ZERO_DBL;// 第2行以后---[金额]----材料---发生
        DZFDouble f_f_rengong = DZFDouble.ZERO_DBL;
        DZFDouble f_f_zzfy = DZFDouble.ZERO_DBL;
        for (int i = 0; i < len; i++) {
            if (i == 0) {
                if (bodyvos[i].getNcailiao_fs() != null)
                    z_f_cailiao = numberToZero(bodyvos[i].getNcailiao_fs());
                if (bodyvos[i].getNrengong_fs() != null)
                    z_f_rengong = numberToZero(bodyvos[i].getNrengong_fs());
                if (bodyvos[i].getNzhizao_fs() != null)
                    z_f_zzfy = numberToZero(bodyvos[i].getNzhizao_fs());
                //
                if (bodyvos[i].getNcailiao_qc() != null) {
                    z_f_cailiao_qc = numberToZero(bodyvos[i].getNcailiao_qc());
                }
                if (bodyvos[i].getNrengong_qc() != null) {
                    z_f_rengong_qc = numberToZero(bodyvos[i].getNrengong_qc());
                }
                if (bodyvos[i].getNzhizao_qc() != null) {
                    z_f_zzfy_qc = numberToZero(bodyvos[i].getNzhizao_qc());
                }
            } else if (i == 1) {// zpm 改的两者一致  2019.12.09

            } else {
                if (bodyvos[i].getNcailiao_fs() != null) {
                    f_cailiao_bl = SafeCompute.add(f_cailiao_bl, numberToZero(bodyvos[i].getNcailiao_fs()));
                    f_f_cailiao = SafeCompute.add(f_f_cailiao, numberToZero(bodyvos[i].getNcailiao_fs()));
                }
                if (bodyvos[i].getNrengong_fs() != null) {
                    f_rengong_bl = SafeCompute.add(f_rengong_bl, numberToZero(bodyvos[i].getNrengong_fs()));
                    f_f_rengong = SafeCompute.add(f_f_rengong, numberToZero(bodyvos[i].getNrengong_fs()));
                }
                if (bodyvos[i].getNzhizao_fs() != null) {
                    f_zzfy_bl = SafeCompute.add(f_zzfy_bl, numberToZero(bodyvos[i].getNzhizao_fs()));
                    f_f_zzfy = SafeCompute.add(f_f_zzfy, numberToZero(bodyvos[i].getNzhizao_fs()));
                }
                //
                if (bodyvos[i].getNcailiao_qc() != null) {
                    f_cailiao_qc = SafeCompute.add(f_cailiao_qc, numberToZero(bodyvos[i].getNcailiao_qc()));
                    cailiao = SafeCompute.add(cailiao, numberToZero(bodyvos[i].getNcailiao_qc()));
                }
                if (bodyvos[i].getNrengong_qc() != null) {
                    f_rengong_qc = SafeCompute.add(f_rengong_qc, numberToZero(bodyvos[i].getNrengong_qc()));
                    rengong = SafeCompute.add(rengong, numberToZero(bodyvos[i].getNrengong_qc()));
                }
                if (bodyvos[i].getNzhizao_qc() != null) {
                    f_zzfy_qc = SafeCompute.add(f_zzfy_qc, numberToZero(bodyvos[i].getNzhizao_qc()));
                    zzfy = SafeCompute.add(zzfy, numberToZero(bodyvos[i].getNzhizao_qc()));
                }
            }
        }
        QmgyjzVOs vo = new QmgyjzVOs();
        vo.setZ_f_cailiao_qc(z_f_cailiao_qc);// 第1行--材料期初
        vo.setZ_f_rengong_qc(z_f_rengong_qc);// 第1行--人工期初
        vo.setZ_f_zzfy_qc(z_f_zzfy_qc);// 第1行--制造费用期初
        vo.setZ_f_cailiao(z_f_cailiao);// 第1行-材料发生
        vo.setZ_f_rengong(z_f_rengong);// 第1行-人工发生
        vo.setZ_f_zzfy(z_f_zzfy);// 第1行-制造费用发生
        vo.setCailiao(cailiao);// 第2行以后----[金额] ---材料---期初
        vo.setRengong(rengong);
        vo.setZzfy(zzfy);
        vo.setF_f_cailiao(f_f_cailiao);// 第2行以后---[金额]----材料---发生
        vo.setF_f_rengong(f_f_rengong);
        vo.setF_f_zzfy(f_f_zzfy);
        vo.setF_cailiao_qc(f_cailiao_qc);// 第2行以后--[比例]----材料----期初
        vo.setF_rengong_qc(f_rengong_qc);
        vo.setF_zzfy_qc(f_zzfy_qc);
        vo.setF_cailiao_bl(f_cailiao_bl);// 第2行以后--[比例]---材料----发生
        vo.setF_rengong_bl(f_rengong_bl);
        vo.setF_zzfy_bl(f_zzfy_bl);
        return vo;
    }

    // 期初、发生按比例计算 按照行完工比例计算
    private void calcgyjzbyPercent1ByRowBL(QmWgcpVO[] bodyvos, DZFDouble bili, QmgyjzVOs gyvo) {
        DZFDouble oneH = new DZFDouble(100);
        if (!gyvo.getZ_f_cailiao_qc().equals(DZFDouble.ZERO_DBL)) {
            if (!gyvo.getF_cailiao_qc().equals(oneH)) {
                throw new BusinessException("期初材料分配比例之和必须为100%");
            }
        }

        if (!gyvo.getZ_f_rengong_qc().equals(DZFDouble.ZERO_DBL)) {
            if (!gyvo.getF_rengong_qc().equals(oneH)) {
                throw new BusinessException("期初人工分配比例之和必须为100%");
            }
        }

        if (!gyvo.getZ_f_zzfy_qc().equals(DZFDouble.ZERO_DBL)) {
            if (!gyvo.getF_zzfy_qc().equals(oneH)) {
                throw new BusinessException("期初制造费用分配比例之和必须为100%");
            }
        }

        if (!gyvo.getZ_f_cailiao().equals(DZFDouble.ZERO_DBL)) {
            if (!gyvo.getF_cailiao_bl().equals(oneH)) {
                throw new BusinessException("本期发生材料分配比例之和必须为100%");
            }
        }

        if (!gyvo.getZ_f_rengong().equals(DZFDouble.ZERO_DBL)) {
            if (!gyvo.getF_rengong_bl().equals(oneH)) {
                throw new BusinessException("本期发生人工分配比例之和必须为100%");
            }
        }
        if (!gyvo.getZ_f_zzfy().equals(DZFDouble.ZERO_DBL)) {
            if (!gyvo.getF_zzfy_bl().equals(oneH)) {
                throw new BusinessException("本期发生制造费用分配比例之和必须为100%");
            }
        }

        DZFDouble ncailiao_wg_total = DZFDouble.ZERO_DBL;
        DZFDouble nrengong_wg_total = DZFDouble.ZERO_DBL;
        DZFDouble nzhizao_wg_total = DZFDouble.ZERO_DBL;
        DZFDouble ncailiao_nwg_total = DZFDouble.ZERO_DBL;
        DZFDouble nrengong_nwg_total = DZFDouble.ZERO_DBL;
        DZFDouble nzhizao_nwg_total = DZFDouble.ZERO_DBL;
        DZFDouble ncailiao_qc_last = DZFDouble.ZERO_DBL;
        DZFDouble nrengong_qc_last = DZFDouble.ZERO_DBL;
        DZFDouble nzhizao_qc_last = DZFDouble.ZERO_DBL;
        DZFDouble ncailiao_fs_last = DZFDouble.ZERO_DBL;
        ;
        DZFDouble nrengong_fs_last = DZFDouble.ZERO_DBL;
        DZFDouble nzhizao_fs_last = DZFDouble.ZERO_DBL;

        for (int i = 1; i < bodyvos.length; i++) {
            // if (bodyvos[i].getWgbl() != null)
            bili = bodyvos[i].getWgbl();// 取每一行完工比例进行计算
            if (i < bodyvos.length - 1) {

                DZFDouble v1 = calLast(bodyvos[0].getNcailiao_qc(), bodyvos[i].getNcailiao_qc());
                ncailiao_qc_last = subDZFDouble(ncailiao_qc_last, v1);

                v1 = calLast(bodyvos[0].getNrengong_qc(), bodyvos[i].getNrengong_qc());
                nrengong_qc_last = subDZFDouble(nrengong_qc_last, v1);

                v1 = calLast(bodyvos[0].getNzhizao_qc(), bodyvos[i].getNzhizao_qc());
                nzhizao_qc_last = subDZFDouble(nzhizao_qc_last, v1);

                v1 = calLast(bodyvos[0].getNcailiao_fs(), bodyvos[i].getNcailiao_fs());
                ncailiao_fs_last = subDZFDouble(ncailiao_fs_last, v1);

                v1 = calLast(bodyvos[0].getNrengong_fs(), bodyvos[i].getNrengong_fs());
                nrengong_fs_last = subDZFDouble(nrengong_fs_last, v1);

                v1 = calLast(bodyvos[0].getNzhizao_fs(), bodyvos[i].getNzhizao_fs());
                nzhizao_fs_last = subDZFDouble(nzhizao_fs_last, v1);

                v1 = calWgMNYByBLByRowBl(gyvo.getZ_f_cailiao_qc(), bodyvos[i].getNcailiao_qc(), gyvo.getZ_f_cailiao(),
                        bodyvos[i].getNcailiao_fs(), bili);
                bodyvos[i].setNcailiao_wg(v1);
                ncailiao_wg_total = addDZFDouble(ncailiao_wg_total, bodyvos[i].getNcailiao_wg());

                v1 = calWgMNYByBLByRowBl(gyvo.getZ_f_rengong_qc(), bodyvos[i].getNrengong_qc(), gyvo.getZ_f_rengong(),
                        bodyvos[i].getNrengong_fs(), bili);
                bodyvos[i].setNrengong_wg(v1);
                nrengong_wg_total = addDZFDouble(nrengong_wg_total, bodyvos[i].getNrengong_wg());

                v1 = calWgMNYByBLByRowBl(gyvo.getZ_f_zzfy_qc(), bodyvos[i].getNzhizao_qc(), gyvo.getZ_f_zzfy(),
                        bodyvos[i].getNzhizao_fs(), bili);
                bodyvos[i].setNzhizao_wg(v1);
                nzhizao_wg_total = addDZFDouble(nzhizao_wg_total, bodyvos[i].getNzhizao_wg());

                v1 = calWwgMNYByBLByRowBl(gyvo.getZ_f_cailiao_qc(), bodyvos[i].getNcailiao_qc(), gyvo.getZ_f_cailiao(),
                        bodyvos[i].getNcailiao_fs(), bodyvos[i].getNcailiao_wg());
                bodyvos[i].setNcailiao_nwg(v1);
                ncailiao_nwg_total = addDZFDouble(ncailiao_nwg_total, bodyvos[i].getNcailiao_nwg());

                v1 = calWwgMNYByBLByRowBl(gyvo.getZ_f_rengong_qc(), bodyvos[i].getNrengong_qc(), gyvo.getZ_f_rengong(),
                        bodyvos[i].getNrengong_fs(), bodyvos[i].getNrengong_wg());
                bodyvos[i].setNrengong_nwg(v1);
                nrengong_nwg_total = addDZFDouble(nrengong_nwg_total, bodyvos[i].getNrengong_nwg());

                v1 = calWwgMNYByBLByRowBl(gyvo.getZ_f_zzfy_qc(), bodyvos[i].getNzhizao_qc(), gyvo.getZ_f_zzfy(),
                        bodyvos[i].getNzhizao_fs(), bodyvos[i].getNzhizao_wg());
                bodyvos[i].setNzhizao_nwg(v1);
                nzhizao_nwg_total = addDZFDouble(nzhizao_nwg_total, bodyvos[i].getNzhizao_nwg());
            } else if (i == (bodyvos.length - 1)) {

                DZFDouble v1 = calWgMNYByBLLastRowByRowBl(gyvo.getZ_f_cailiao_qc(), ncailiao_qc_last,
                        gyvo.getZ_f_cailiao(), ncailiao_fs_last, bili);
                bodyvos[i].setNcailiao_wg(v1);
                ncailiao_wg_total = addDZFDouble(ncailiao_wg_total, bodyvos[i].getNcailiao_wg());

                v1 = calWgMNYByBLLastRowByRowBl(gyvo.getZ_f_rengong_qc(), nrengong_qc_last, gyvo.getZ_f_rengong(),
                        nrengong_fs_last, bili);
                bodyvos[i].setNrengong_wg(v1);
                nrengong_wg_total = addDZFDouble(nrengong_wg_total, bodyvos[i].getNrengong_wg());

                v1 = calWgMNYByBLLastRowByRowBl(gyvo.getZ_f_zzfy_qc(), nzhizao_qc_last, gyvo.getZ_f_zzfy(),
                        nzhizao_fs_last, bili);
                bodyvos[i].setNzhizao_wg(v1);
                nzhizao_wg_total = addDZFDouble(nzhizao_wg_total, bodyvos[i].getNzhizao_wg());

                v1 = calNwgMNYByBLLastRowByRowBl(gyvo.getZ_f_cailiao_qc(), ncailiao_qc_last, gyvo.getZ_f_cailiao(),
                        ncailiao_fs_last, bodyvos[i].getNcailiao_wg());
                bodyvos[i].setNcailiao_nwg(v1);
                ncailiao_nwg_total = addDZFDouble(ncailiao_nwg_total, bodyvos[i].getNcailiao_nwg());

                v1 = calNwgMNYByBLLastRowByRowBl(gyvo.getZ_f_rengong_qc(), nrengong_qc_last, gyvo.getZ_f_rengong(),
                        nrengong_fs_last, bodyvos[i].getNrengong_wg());
                bodyvos[i].setNrengong_nwg(v1);
                nrengong_nwg_total = addDZFDouble(nrengong_nwg_total, bodyvos[i].getNrengong_nwg());

                v1 = calNwgMNYByBLLastRowByRowBl(gyvo.getZ_f_zzfy_qc(), nzhizao_qc_last, gyvo.getZ_f_zzfy(),
                        nzhizao_fs_last, bodyvos[i].getNzhizao_wg());
                bodyvos[i].setNzhizao_nwg(v1);
                nzhizao_nwg_total = addDZFDouble(nzhizao_nwg_total, bodyvos[i].getNzhizao_nwg());
            }
        }
        bodyvos[0].setNcailiao_wg(ncailiao_wg_total);
        bodyvos[0].setNrengong_wg(nrengong_wg_total);
        bodyvos[0].setNzhizao_wg(nzhizao_wg_total);
        bodyvos[0].setNcailiao_nwg(ncailiao_nwg_total);
        bodyvos[0].setNrengong_nwg(nrengong_nwg_total);
        bodyvos[0].setNzhizao_nwg(nzhizao_nwg_total);
    }

    // 期初、发生按比例计算 按照行完工比例计算
    private void calcgyjzbyPercent1ByRowBLIc(QmWgcpVO[] bodyvos, DZFDouble bili, QmgyjzVOs gyvo) {
        DZFDouble oneH = new DZFDouble(100);
        if (!gyvo.getZ_f_cailiao_qc().equals(DZFDouble.ZERO_DBL)) {
            if (!gyvo.getF_cailiao_qc().equals(oneH)) {
                throw new BusinessException("期初材料分配比例之和必须为100%");
            }
        }

        if (!gyvo.getZ_f_rengong_qc().equals(DZFDouble.ZERO_DBL)) {
            if (!gyvo.getF_rengong_qc().equals(oneH)) {
                throw new BusinessException("期初人工分配比例之和必须为100%");
            }
        }

        if (!gyvo.getZ_f_zzfy_qc().equals(DZFDouble.ZERO_DBL)) {
            if (!gyvo.getF_zzfy_qc().equals(oneH)) {
                throw new BusinessException("期初制造费用分配比例之和必须为100%");
            }
        }

        if (!gyvo.getZ_f_cailiao().equals(DZFDouble.ZERO_DBL)) {
            if (!gyvo.getF_cailiao_bl().equals(oneH)) {
                throw new BusinessException("本期发生材料分配比例之和必须为100%");
            }
        }

        if (!gyvo.getZ_f_rengong().equals(DZFDouble.ZERO_DBL)) {
            if (!gyvo.getF_rengong_bl().equals(oneH)) {
                throw new BusinessException("本期发生人工分配比例之和必须为100%");
            }
        }
        if (!gyvo.getZ_f_zzfy().equals(DZFDouble.ZERO_DBL)) {
            if (!gyvo.getF_zzfy_bl().equals(oneH)) {
                throw new BusinessException("本期发生制造费用分配比例之和必须为100%");
            }
        }

        DZFDouble ncailiao_wg_total = DZFDouble.ZERO_DBL;
        DZFDouble nrengong_wg_total = DZFDouble.ZERO_DBL;
        DZFDouble nzhizao_wg_total = DZFDouble.ZERO_DBL;
        DZFDouble ncailiao_nwg_total = DZFDouble.ZERO_DBL;
        DZFDouble nrengong_nwg_total = DZFDouble.ZERO_DBL;
        DZFDouble nzhizao_nwg_total = DZFDouble.ZERO_DBL;
        DZFDouble ncailiao_qc_last = DZFDouble.ZERO_DBL;
        DZFDouble nrengong_qc_last = DZFDouble.ZERO_DBL;
        DZFDouble nzhizao_qc_last = DZFDouble.ZERO_DBL;
        DZFDouble ncailiao_fs_last = DZFDouble.ZERO_DBL;
        ;
        DZFDouble nrengong_fs_last = DZFDouble.ZERO_DBL;
        DZFDouble nzhizao_fs_last = DZFDouble.ZERO_DBL;

        for (int i = 2; i < bodyvos.length; i++) {
            // if (bodyvos[i].getWgbl() != null)
            bili = bodyvos[i].getWgbl();// 取每一行完工比例进行计算
            if (i < bodyvos.length - 1) {

                DZFDouble v1 = calLast(bodyvos[0].getNcailiao_qc(), bodyvos[i].getNcailiao_qc());
                ncailiao_qc_last = subDZFDouble(ncailiao_qc_last, v1);

                v1 = calLast(bodyvos[0].getNrengong_qc(), bodyvos[i].getNrengong_qc());
                nrengong_qc_last = subDZFDouble(nrengong_qc_last, v1);

                v1 = calLast(bodyvos[0].getNzhizao_qc(), bodyvos[i].getNzhizao_qc());
                nzhizao_qc_last = subDZFDouble(nzhizao_qc_last, v1);

                v1 = calLast(bodyvos[0].getNcailiao_fs(), bodyvos[i].getNcailiao_fs());
                ncailiao_fs_last = subDZFDouble(ncailiao_fs_last, v1);

                v1 = calLast(bodyvos[0].getNrengong_fs(), bodyvos[i].getNrengong_fs());
                nrengong_fs_last = subDZFDouble(nrengong_fs_last, v1);

                v1 = calLast(bodyvos[0].getNzhizao_fs(), bodyvos[i].getNzhizao_fs());
                nzhizao_fs_last = subDZFDouble(nzhizao_fs_last, v1);

                v1 = calWgMNYByBLByRowBl(gyvo.getZ_f_cailiao_qc(), bodyvos[i].getNcailiao_qc(), gyvo.getZ_f_cailiao(),
                        bodyvos[i].getNcailiao_fs(), bili);
                bodyvos[i].setNcailiao_wg(v1);
                ncailiao_wg_total = addDZFDouble(ncailiao_wg_total, bodyvos[i].getNcailiao_wg());

                v1 = calWgMNYByBLByRowBl(gyvo.getZ_f_rengong_qc(), bodyvos[i].getNrengong_qc(), gyvo.getZ_f_rengong(),
                        bodyvos[i].getNrengong_fs(), bili);
                bodyvos[i].setNrengong_wg(v1);
                nrengong_wg_total = addDZFDouble(nrengong_wg_total, bodyvos[i].getNrengong_wg());

                v1 = calWgMNYByBLByRowBl(gyvo.getZ_f_zzfy_qc(), bodyvos[i].getNzhizao_qc(), gyvo.getZ_f_zzfy(),
                        bodyvos[i].getNzhizao_fs(), bili);
                bodyvos[i].setNzhizao_wg(v1);
                nzhizao_wg_total = addDZFDouble(nzhizao_wg_total, bodyvos[i].getNzhizao_wg());

                v1 = calWwgMNYByBLByRowBl(gyvo.getZ_f_cailiao_qc(), bodyvos[i].getNcailiao_qc(), gyvo.getZ_f_cailiao(),
                        bodyvos[i].getNcailiao_fs(), bodyvos[i].getNcailiao_wg());
                bodyvos[i].setNcailiao_nwg(v1);
                ncailiao_nwg_total = addDZFDouble(ncailiao_nwg_total, bodyvos[i].getNcailiao_nwg());

                v1 = calWwgMNYByBLByRowBl(gyvo.getZ_f_rengong_qc(), bodyvos[i].getNrengong_qc(), gyvo.getZ_f_rengong(),
                        bodyvos[i].getNrengong_fs(), bodyvos[i].getNrengong_wg());
                bodyvos[i].setNrengong_nwg(v1);
                nrengong_nwg_total = addDZFDouble(nrengong_nwg_total, bodyvos[i].getNrengong_nwg());

                v1 = calWwgMNYByBLByRowBl(gyvo.getZ_f_zzfy_qc(), bodyvos[i].getNzhizao_qc(), gyvo.getZ_f_zzfy(),
                        bodyvos[i].getNzhizao_fs(), bodyvos[i].getNzhizao_wg());
                bodyvos[i].setNzhizao_nwg(v1);
                nzhizao_nwg_total = addDZFDouble(nzhizao_nwg_total, bodyvos[i].getNzhizao_nwg());
            } else if (i == (bodyvos.length - 1)) {

                DZFDouble v1 = calWgMNYByBLLastRowByRowBl(gyvo.getZ_f_cailiao_qc(), ncailiao_qc_last,
                        gyvo.getZ_f_cailiao(), ncailiao_fs_last, bili);
                bodyvos[i].setNcailiao_wg(v1);
                ncailiao_wg_total = addDZFDouble(ncailiao_wg_total, bodyvos[i].getNcailiao_wg());

                v1 = calWgMNYByBLLastRowByRowBl(gyvo.getZ_f_rengong_qc(), nrengong_qc_last, gyvo.getZ_f_rengong(),
                        nrengong_fs_last, bili);
                bodyvos[i].setNrengong_wg(v1);
                nrengong_wg_total = addDZFDouble(nrengong_wg_total, bodyvos[i].getNrengong_wg());

                v1 = calWgMNYByBLLastRowByRowBl(gyvo.getZ_f_zzfy_qc(), nzhizao_qc_last, gyvo.getZ_f_zzfy(),
                        nzhizao_fs_last, bili);
                bodyvos[i].setNzhizao_wg(v1);
                nzhizao_wg_total = addDZFDouble(nzhizao_wg_total, bodyvos[i].getNzhizao_wg());

                v1 = calNwgMNYByBLLastRowByRowBl(gyvo.getZ_f_cailiao_qc(), ncailiao_qc_last, gyvo.getZ_f_cailiao(),
                        ncailiao_fs_last, bodyvos[i].getNcailiao_wg());
                bodyvos[i].setNcailiao_nwg(v1);
                ncailiao_nwg_total = addDZFDouble(ncailiao_nwg_total, bodyvos[i].getNcailiao_nwg());

                v1 = calNwgMNYByBLLastRowByRowBl(gyvo.getZ_f_rengong_qc(), nrengong_qc_last, gyvo.getZ_f_rengong(),
                        nrengong_fs_last, bodyvos[i].getNrengong_wg());
                bodyvos[i].setNrengong_nwg(v1);
                nrengong_nwg_total = addDZFDouble(nrengong_nwg_total, bodyvos[i].getNrengong_nwg());

                v1 = calNwgMNYByBLLastRowByRowBl(gyvo.getZ_f_zzfy_qc(), nzhizao_qc_last, gyvo.getZ_f_zzfy(),
                        nzhizao_fs_last, bodyvos[i].getNzhizao_wg());
                bodyvos[i].setNzhizao_nwg(v1);
                nzhizao_nwg_total = addDZFDouble(nzhizao_nwg_total, bodyvos[i].getNzhizao_nwg());
            }
        }
        bodyvos[0].setNcailiao_wg(ncailiao_wg_total);
        bodyvos[0].setNrengong_wg(nrengong_wg_total);
        bodyvos[0].setNzhizao_wg(nzhizao_wg_total);
        bodyvos[0].setNcailiao_nwg(ncailiao_nwg_total);
        bodyvos[0].setNrengong_nwg(nrengong_nwg_total);
        bodyvos[0].setNzhizao_nwg(nzhizao_nwg_total);

        bodyvos[1].setNcailiao_qc(DZFDouble.ZERO_DBL);
        bodyvos[1].setNrengong_qc(DZFDouble.ZERO_DBL);
        bodyvos[1].setNzhizao_qc(DZFDouble.ZERO_DBL);
        bodyvos[1].setNcailiao_fs(DZFDouble.ZERO_DBL);
        bodyvos[1].setNrengong_fs(DZFDouble.ZERO_DBL);
        bodyvos[1].setNzhizao_fs(DZFDouble.ZERO_DBL);
        bodyvos[1].setNcailiao_nwg(DZFDouble.ZERO_DBL);
        bodyvos[1].setNrengong_nwg(DZFDouble.ZERO_DBL);
        bodyvos[1].setNzhizao_nwg(DZFDouble.ZERO_DBL);
        bodyvos[1].setNcailiao_wg(DZFDouble.ZERO_DBL);
        bodyvos[1].setNrengong_wg(DZFDouble.ZERO_DBL);
        bodyvos[1].setNzhizao_wg(DZFDouble.ZERO_DBL);
    }

    // 期初、发生按金额计算 按照行完工比例计算
    private void calcgyjzbyMnyByRowBL(QmWgcpVO[] bodyvos, DZFDouble bili, QmgyjzVOs gyvo) {

        // 金额
        if (!gyvo.getCailiao().equals(gyvo.getZ_f_cailiao_qc())) {
            throw new BusinessException("期初材料费用金额汇总不一致");
        }
        if (!gyvo.getRengong().equals(gyvo.getZ_f_rengong_qc())) {
            throw new BusinessException("期初人工费用金额汇总不一致");
        }
        if (!gyvo.getZzfy().equals(gyvo.getZ_f_zzfy_qc())) {
            throw new BusinessException("期初制造费用金额汇总不一致");
        }
        if (!gyvo.getF_f_zzfy().equals(gyvo.getZ_f_zzfy())) {
            throw new BusinessException("本月发生制造费用金额汇总不一致");
        }
        if (!gyvo.getZ_f_rengong().equals(gyvo.getF_f_rengong())) {
            throw new BusinessException("本月发生人工金额汇总不一致");
        }
        if (!gyvo.getZ_f_cailiao().equals(gyvo.getF_f_cailiao())) {
            throw new BusinessException("本月发生材料金额汇总不一致");
        }

        DZFDouble ncailiao_wg_total = DZFDouble.ZERO_DBL;
        DZFDouble nrengong_wg_total = DZFDouble.ZERO_DBL;
        DZFDouble nzhizao_wg_total = DZFDouble.ZERO_DBL;
        DZFDouble ncailiao_nwg_total = DZFDouble.ZERO_DBL;
        DZFDouble nrengong_nwg_total = DZFDouble.ZERO_DBL;
        DZFDouble nzhizao_nwg_total = DZFDouble.ZERO_DBL;
        for (int i = bodyvos.length - 1; i >= 0; i--) {
            // if (bodyvos[i].getWgbl() != null)
            bili = bodyvos[i].getWgbl();// 取每一行完工比例进行计算
            if (i > 0) {

                DZFDouble v1 = calWgMNYByMnyByRowBl(bodyvos[i].getNcailiao_qc(), bodyvos[i].getNcailiao_fs(), bili);
                bodyvos[i].setNcailiao_wg(v1);
                ncailiao_wg_total = addDZFDouble(ncailiao_wg_total, v1);

                v1 = calWgMNYByMnyByRowBl(bodyvos[i].getNrengong_qc(), bodyvos[i].getNrengong_fs(), bili);
                bodyvos[i].setNrengong_wg(v1);
                nrengong_wg_total = addDZFDouble(nrengong_wg_total, v1);

                v1 = calWgMNYByMnyByRowBl(bodyvos[i].getNzhizao_qc(), bodyvos[i].getNzhizao_fs(), bili);
                bodyvos[i].setNzhizao_wg(v1);
                nzhizao_wg_total = addDZFDouble(nzhizao_wg_total, v1);

                v1 = calWwgMNYByMnyByRowBl(bodyvos[i].getNcailiao_qc(), bodyvos[i].getNcailiao_fs(),
                        bodyvos[i].getNcailiao_wg());
                bodyvos[i].setNcailiao_nwg(v1);
                ncailiao_nwg_total = addDZFDouble(ncailiao_nwg_total, v1);

                v1 = calWwgMNYByMnyByRowBl(bodyvos[i].getNrengong_qc(), bodyvos[i].getNrengong_fs(),
                        bodyvos[i].getNrengong_wg());
                bodyvos[i].setNrengong_nwg(v1);
                nrengong_nwg_total = addDZFDouble(nrengong_nwg_total, v1);

                v1 = calWwgMNYByMnyByRowBl(bodyvos[i].getNzhizao_qc(), bodyvos[i].getNzhizao_fs(),
                        bodyvos[i].getNzhizao_wg());
                bodyvos[i].setNzhizao_nwg(v1);
                nzhizao_nwg_total = addDZFDouble(nzhizao_nwg_total, v1);

            } else if (i == 0) {
                bodyvos[i].setNcailiao_wg(ncailiao_wg_total);
                bodyvos[i].setNrengong_wg(nrengong_wg_total);
                bodyvos[i].setNzhizao_wg(nzhizao_wg_total);
                bodyvos[i].setNcailiao_nwg(ncailiao_nwg_total);
                bodyvos[i].setNrengong_nwg(nrengong_nwg_total);
                bodyvos[i].setNzhizao_nwg(nzhizao_nwg_total);
            }
        }

        for (int i = 0; i < bodyvos.length; i++) {
        }
    }

    // 期初、发生按金额计算 按照行完工比例计算
    private void calcgyjzbyMnyByRowBLIc(QmWgcpVO[] bodyvos, DZFDouble bili, QmgyjzVOs gyvo) {

        // 金额
        if (!gyvo.getCailiao().equals(gyvo.getZ_f_cailiao_qc())) {
            throw new BusinessException("期初材料费用金额汇总不一致");
        }
        if (!gyvo.getRengong().equals(gyvo.getZ_f_rengong_qc())) {
            throw new BusinessException("期初人工费用金额汇总不一致");
        }
        if (!gyvo.getZzfy().equals(gyvo.getZ_f_zzfy_qc())) {
            throw new BusinessException("期初制造费用金额汇总不一致");
        }
        if (!gyvo.getF_f_zzfy().equals(gyvo.getZ_f_zzfy())) {
            throw new BusinessException("本月发生制造费用金额汇总不一致");
        }
        if (!gyvo.getZ_f_rengong().equals(gyvo.getF_f_rengong())) {
            throw new BusinessException("本月发生人工金额汇总不一致");
        }
        if (!gyvo.getZ_f_cailiao().equals(gyvo.getF_f_cailiao())) {
            throw new BusinessException("本月发生材料金额汇总不一致");
        }

        DZFDouble ncailiao_wg_total = DZFDouble.ZERO_DBL;
        DZFDouble nrengong_wg_total = DZFDouble.ZERO_DBL;
        DZFDouble nzhizao_wg_total = DZFDouble.ZERO_DBL;
        DZFDouble ncailiao_nwg_total = DZFDouble.ZERO_DBL;
        DZFDouble nrengong_nwg_total = DZFDouble.ZERO_DBL;
        DZFDouble nzhizao_nwg_total = DZFDouble.ZERO_DBL;
        for (int i = bodyvos.length - 1; i >= 0; i--) {
            // if (bodyvos[i].getWgbl() != null)
            bili = bodyvos[i].getWgbl();// 取每一行完工比例进行计算
            if (i > 1) {

                DZFDouble v1 = calWgMNYByMnyByRowBl(bodyvos[i].getNcailiao_qc(), bodyvos[i].getNcailiao_fs(), bili);
                bodyvos[i].setNcailiao_wg(v1);
                ncailiao_wg_total = addDZFDouble(ncailiao_wg_total, v1);

                v1 = calWgMNYByMnyByRowBl(bodyvos[i].getNrengong_qc(), bodyvos[i].getNrengong_fs(), bili);
                bodyvos[i].setNrengong_wg(v1);
                nrengong_wg_total = addDZFDouble(nrengong_wg_total, v1);

                v1 = calWgMNYByMnyByRowBl(bodyvos[i].getNzhizao_qc(), bodyvos[i].getNzhizao_fs(), bili);
                bodyvos[i].setNzhizao_wg(v1);
                nzhizao_wg_total = addDZFDouble(nzhizao_wg_total, v1);

                v1 = calWwgMNYByMnyByRowBl(bodyvos[i].getNcailiao_qc(), bodyvos[i].getNcailiao_fs(),
                        bodyvos[i].getNcailiao_wg());
                bodyvos[i].setNcailiao_nwg(v1);
                ncailiao_nwg_total = addDZFDouble(ncailiao_nwg_total, v1);

                v1 = calWwgMNYByMnyByRowBl(bodyvos[i].getNrengong_qc(), bodyvos[i].getNrengong_fs(),
                        bodyvos[i].getNrengong_wg());
                bodyvos[i].setNrengong_nwg(v1);
                nrengong_nwg_total = addDZFDouble(nrengong_nwg_total, v1);

                v1 = calWwgMNYByMnyByRowBl(bodyvos[i].getNzhizao_qc(), bodyvos[i].getNzhizao_fs(),
                        bodyvos[i].getNzhizao_wg());
                bodyvos[i].setNzhizao_nwg(v1);
                nzhizao_nwg_total = addDZFDouble(nzhizao_nwg_total, v1);

            } else if (i == 0) {
                bodyvos[i].setNcailiao_wg(ncailiao_wg_total);
                bodyvos[i].setNrengong_wg(nrengong_wg_total);
                bodyvos[i].setNzhizao_wg(nzhizao_wg_total);
                bodyvos[i].setNcailiao_nwg(ncailiao_nwg_total);
                bodyvos[i].setNrengong_nwg(nrengong_nwg_total);
                bodyvos[i].setNzhizao_nwg(nzhizao_nwg_total);
            } else if (i == 1) {
                bodyvos[i].setNcailiao_wg(DZFDouble.ZERO_DBL);
                bodyvos[i].setNrengong_wg(DZFDouble.ZERO_DBL);
                bodyvos[i].setNzhizao_wg(DZFDouble.ZERO_DBL);
                bodyvos[i].setNcailiao_nwg(DZFDouble.ZERO_DBL);
                bodyvos[i].setNrengong_nwg(DZFDouble.ZERO_DBL);
                bodyvos[i].setNzhizao_nwg(DZFDouble.ZERO_DBL);
                bodyvos[i].setNcailiao_qc(DZFDouble.ZERO_DBL);
                bodyvos[i].setNrengong_qc(DZFDouble.ZERO_DBL);
                bodyvos[i].setNzhizao_qc(DZFDouble.ZERO_DBL);
                bodyvos[i].setNcailiao_fs(DZFDouble.ZERO_DBL);
                bodyvos[i].setNrengong_fs(DZFDouble.ZERO_DBL);
                bodyvos[i].setNzhizao_fs(DZFDouble.ZERO_DBL);
            }
        }

        for (int i = 0; i < bodyvos.length; i++) {
        }
    }


    // 计算完工期初和发生之和 乘以比例 算出完工(按金额 按照行完工比例)
    private DZFDouble calWgMNYByMnyByRowBl(Object qc, Object fs, Object bili) {
        DZFDouble oneH = new DZFDouble(100);
        return divDZFDouble(mulDZFDouble(addDZFDouble(qc, fs), bili), oneH);
    }

    // 计算完工期初和发生之和 乘以比例 算出完工(按金额 按照行完工比例)
    private DZFDouble calWwgMNYByMnyByRowBl(Object qc, Object fs, Object wg) {
        return subDZFDouble(addDZFDouble(qc, fs), wg);
    }

    // 计算完工期初和发生之和 乘以比例 算出完工
    private DZFDouble calWgMNYByBLByRowBl(Object zqc, Object rqc, Object zfs, Object rfs, Object bili) {
        DZFDouble oneH = new DZFDouble(100);

        // 按行比例计算行期初金额
        DZFDouble d1 = divDZFDouble(mulDZFDouble(zqc, rqc), oneH);
        // 按行比例计算行发生金额
        DZFDouble d2 = divDZFDouble(mulDZFDouble(zfs, rfs), oneH);
        // 按完工比例计算行完工金额
        DZFDouble v1 = divDZFDouble(mulDZFDouble(addDZFDouble(d1, d2), bili), oneH);
        return v1;
    }

    // 计算完工期初和发生之和 乘以比例 算出完工
    private DZFDouble calWwgMNYByBLByRowBl(Object zqc, Object rqc, Object zfs, Object rfs, Object wg) {
        DZFDouble oneH = new DZFDouble(100);

        // 按行比例计算行期初金额
        DZFDouble d1 = divDZFDouble(mulDZFDouble(zqc, rqc), oneH);
        // 按行比例计算行发生金额
        DZFDouble d2 = divDZFDouble(mulDZFDouble(zfs, rfs), oneH);
        // 按完工比例计算行完工金额
        DZFDouble v1 = subDZFDouble(addDZFDouble(d1, d2), wg);
        return v1;
    }

    // 计算完工期初和发生之和 乘以比例 算出未完工(最后一行)
    private DZFDouble calNwgMNYByBLLastRowByRowBl(Object zqc, Object rqc, Object zfs, Object rfs, Object wg) {
        DZFDouble d1 = addDZFDouble(zqc, rqc);
        DZFDouble d2 = addDZFDouble(zfs, rfs);
        return subDZFDouble(addDZFDouble(d1, d2), wg);
    }

    // 计算完工期初和发生之和 乘以比例 算出完工(最后一行)
    private DZFDouble calWgMNYByBLLastRowByRowBl(Object zqc, Object rqc, Object zfs, Object rfs, Object bili) {
        DZFDouble oneH = new DZFDouble(100);

        DZFDouble d1 = addDZFDouble(zqc, rqc);
        DZFDouble d2 = addDZFDouble(zfs, rfs);
        return divDZFDouble(mulDZFDouble(addDZFDouble(d1, d2), bili), oneH);
    }

    private DZFDouble calLast(Object qc, Object rqc) {
        DZFDouble oneH = new DZFDouble(100);
        return divDZFDouble(mulDZFDouble(qc, rqc), oneH);
    }

    private DZFDouble addDZFDouble(Object a, Object b) {

        return numberToZero(SafeCompute.add(numberToZero(a), numberToZero(b)));
    }

    private DZFDouble subDZFDouble(Object a, Object b) {

        return numberToZero(SafeCompute.sub(numberToZero(a), numberToZero(b)));
    }

    private DZFDouble mulDZFDouble(Object a, Object b) {

        return numberToZero(SafeCompute.multiply(numberToZero(a), numberToZero(b)));
    }

    private DZFDouble divDZFDouble(Object a, Object b) {

        return numberToZero(SafeCompute.div(numberToZero(a), numberToZero(b)));
    }

    private String[] getQmWgcpVOpk(QmWgcpVO[] bodyvos) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < bodyvos.length; i++) {
            String pk = bodyvos[i].getPk_inventory();
            if (!StringUtil.isEmpty(pk)) {
                list.add(pk);
            }
        }
        return list.toArray(new String[0]);
    }


    private DZFDouble numberToZero(Object o) {
        if (o == null) {
            return DZFDouble.ZERO_DBL;
        }
        if (o instanceof DZFDouble) {
            return ((DZFDouble) o).setScale(2, 0);
        }
        return DZFDouble.ZERO_DBL;
    }

    // 目前只有汇兑损益调用，前台 成本类并没有调用。。。 zpm 2019.12.20
    @PostMapping("/checkTemporaryIsExist")
    public ReturnData<Grid> checkTemporaryIsExist(@MultiRequestBody("qmvos")  QmclVO[] qmvos,@MultiRequestBody("type") String type) {
        Grid grid = new Grid();
        try {
            grid.setSuccess(false);
            if(qmvos != null && qmvos.length == 1){
                QmclVO headvo  = qmvos[0];
                gl_qmclserv.checkTemporaryIsExist(headvo.getPk_corp(), headvo.getPeriod(), false,"不能期末调汇!");
                if ("0".equals(type)) {// 0 默认成本结转
                    // 如果是成本结转走这个,,,前台 成本类并没有调用。。。 zpm 2019.12.20
                    ////gl_qmclserv.checkQmclForKc(headvo.getPk_corp(), headvo.getPeriod(), "");
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


    // 不启用进销存，工业结转，取消各种场景下的结转(材料结转、辅助成本、制造费用、完工产品、销售成本)
    @PostMapping("/canceljiezhuan")
    public ReturnData<Grid> canceljiezhuan(@MultiRequestBody("qmvo")  QmclVO qmvo,
                                           @MultiRequestBody("cbjzcount")  String cbjzCount,
                                           @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            QmclVO resvos = gl_qmclnoicserv.rollbackCbjzNoic(qmvo, cbjzCount);
            grid.setMsg("反操作成功!");
            grid.setTotal(Long.valueOf(1));
            grid.setSuccess(true);
            grid.setRows(Arrays.asList(resvos));
        }catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "取消结转失败！");
        }
        return ReturnData.ok().data(grid);
    }

    // 不启用进销存，工业结转，结转辅助生产成本
    @PostMapping("/jzfuzhusccb")
    public ReturnData<Grid> jzfuzhusccb(@MultiRequestBody("qmvo")  QmclVO qmvo,
                                        @MultiRequestBody("cbjzcount")  String cbjzCount,
                                        @MultiRequestBody("isgy")  String isgy,
                                        @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        grid.setRows(new ArrayList<QmclVO>(Arrays.asList(qmvo)));
        try {
            boolean isgybool = false;
            if (isgy != null && "Y".equals(isgy)) {
                isgybool = true;
            }
            List<CostForwardVO> list = gl_industryserv.queryIndustCFVO(qmvo, isgybool);
            String pk_corp = qmvo.getPk_corp();
            if(list == null){
                list = new ArrayList<CostForwardVO>();
            }
            List<CostForwardVO> listz = secondOperate(list.toArray(new CostForwardVO[0]),isgy,pk_corp);
            QmclVO qmvo1 = gl_qmclnoicserv.jzfuzhusccb(qmvo, listz, userVO.getCuserid(), cbjzCount);
            grid.setRows(new ArrayList<QmclVO>(Arrays.asList(qmvo1)));
            grid.setMsg("辅助生产成本结转成功!");
            grid.setTotal(Long.valueOf(1));
            grid.setSuccess(true);
        }catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "结转辅助生产成本失败！");
        }
        return ReturnData.ok().data(grid);
    }

    // 不启用进销存，工业结转，结转制造费用
    @PostMapping("/jzzhizaofy")
    public ReturnData<Grid> jzzhizaofy(@MultiRequestBody("qmvo")  QmclVO qmvo,
                                       @MultiRequestBody("cbjzcount")  String cbjzCount,
                                       @MultiRequestBody("isgy")  String isgy,
                                       @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        grid.setRows(new ArrayList<QmclVO>(Arrays.asList(qmvo)));
        try {
            boolean isgybool = false;
            if (isgy != null && "Y".equals(isgy)) {
                isgybool = true;
            }
            List<CostForwardVO> list = gl_industryserv.queryIndustCFVO(qmvo, isgybool);
            String pk_corp = qmvo.getPk_corp();
            if(list == null){
                list = new ArrayList<CostForwardVO>();
            }
            List<CostForwardVO> list2 = secondOperate(list.toArray(new CostForwardVO[0]),isgy,pk_corp);
            List<CostForwardVO> list3 = thirdOperate(pk_corp,list,list2);
            QmclVO qmvo1 = gl_qmclnoicserv.jzfuzhusccb(qmvo, list3, userVO.getCuserid(), cbjzCount);
            grid.setRows(new ArrayList<QmclVO>(Arrays.asList(qmvo1)));
            grid.setMsg("结转制造费用成功!");
            grid.setTotal(Long.valueOf(1));
            grid.setSuccess(true);
        }catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "结转制造费用失败！");
        }
        return ReturnData.ok().data(grid);
    }

    // 不启用进销存，工业结转，结转完工产品。（产品的成本分摊）
    @PostMapping("/queryWangong")
    public ReturnData<Grid> queryWangong(@MultiRequestBody("qmvo")  QmclVO qmvo,
                                         @MultiRequestBody("isgy")  String isgy,
                                         @MultiRequestBody("jztype")  String jztype,
                                         @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        grid.setRows(new ArrayList<QmclVO>(Arrays.asList(qmvo)));
        try {
            boolean isgybool = false;
            if (isgy != null && "Y".equals(isgy)) {
                isgybool = true;
            }
            List<CostForwardVO> list = gl_industryserv.queryIndustCFVO(qmvo, isgybool);
            String pk_corp = qmvo.getPk_corp();
            if(list == null){
                list = new ArrayList<CostForwardVO>();
            }
            List<CostForwardVO> list2 = secondOperate(list.toArray(new CostForwardVO[0]),isgy,pk_corp);
            List<CostForwardVO> list3 = thirdOperate(pk_corp,list,list2);
            if(list3 != null && list3.size() > 0){
                List<CostForwardInfo> list4 = forthoperate(qmvo,list3.toArray(new CostForwardVO[0]),false,jztype);
                grid.setRows(list4);
                grid.setMsg("操作成功!");
                grid.setTotal((long) list4.size());
            }else{
                grid.setRows(new ArrayList<CostForwardInfo>());
                grid.setMsg("操作成功!");
                grid.setTotal(Long.valueOf(1));
            }
            grid.setSuccess(true);
        }catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "结转制造费用失败！");
        }
        return ReturnData.ok().data(grid);
    }


    // 查询存货、材料相关科目，不启用进销存、工业结转，结转完工产品。（产品的成本分摊）
    @PostMapping("/queryCBJZKMwg")
    public ReturnData<Grid> queryCBJZKMwg(@MultiRequestBody("jztype")  String jztype,
                                          @MultiRequestBody("pk_gs")  String pk_gs,
                                          @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            String userid = userVO.getCuserid();
            List<CostForwardInfo> vos = gl_qmclnoicserv.queryCBJZAccountVOSwg(pk_gs, userid, jztype);
            if (vos != null && vos.size() > 0) {
                grid.setRows(vos);
            }else{
                grid.setRows(new ArrayList<CostForwardInfo>());
            }
            grid.setSuccess(true);
            grid.setMsg("查询成功!");
        }catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "查询表体加载科目信息失败！");
        }
        return ReturnData.ok().data(grid);
    }


    // 不启用进销存，工业结转，结转完工产品。保存 完工 凭证
    @PostMapping("/savePzWangong")
    public ReturnData<Grid> savePzWangong(@MultiRequestBody("qmvo")  QmclVO qmvo,
                                          @MultiRequestBody("cbjzPara3")  CostForwardInfo[] cbjz3,
                                          @MultiRequestBody("cbjzCount")  String cbjzCount,
                                          @MultiRequestBody("jztype")  String jztype,
                                          @MultiRequestBody UserVO userVO) {
        Grid grid = new Grid();
        try {
            Map<QmclVO, List<CostForwardInfo>> map = new HashMap<QmclVO, List<CostForwardInfo>>();
            List<CostForwardInfo> list = null;
            if(cbjz3 == null || cbjz3.length == 0){
                list = new ArrayList<CostForwardInfo>();
            }else{
                list = new ArrayList<CostForwardInfo>(Arrays.asList(cbjz3));
            }
            map.put(qmvo,list);
            QmclVO vos = gl_qmclnoicserv.saveWgVoucherNoic(userVO.getCuserid(), map, jztype, cbjzCount);
            grid.setSuccess(true);
            grid.setMsg("操作成功!");
            grid.setTotal((long) 1);
            grid.setSuccess(true);
            grid.setRows(Arrays.asList(vos));
        }catch (Exception e) {
            log.error("错误",e);
            grid.setSuccess(false);
            grid.setRows(new ArrayList<QmclVO>());
            grid.setMsg(e instanceof BusinessException ? e.getMessage()+"<br>" : "完工产品保存失败！");
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