package com.dzf.zxkj.platform.controller.jzcl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败");
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
                            tips.append(e instanceof BusinessException ? e.getMessage() : "计提折旧失败<br>");
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
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "计提折旧失败！");
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
                        tips.append(e instanceof BusinessException ? e.getMessage() :"反计提折旧失败！<br/>");
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
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "反计提折旧失败！");
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
                        tips.append(e instanceof BusinessException ? e.getMessage() :"增值税结转失败<br/>");
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
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "增值税结转失败！");
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
                        tips.append(e instanceof BusinessException ? e.getMessage() : "反增值税结转失败<br/>");
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
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "反增值税结转失败！");
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
}