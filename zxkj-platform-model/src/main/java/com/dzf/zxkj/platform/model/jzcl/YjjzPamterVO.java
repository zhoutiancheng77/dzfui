package com.dzf.zxkj.platform.model.jzcl;

/**
 * @Author: zpm
 * @Description:
 * @Date:Created by 2019/12/12
 * @Modified By:
 */

import com.dzf.zxkj.platform.model.bdset.AdjustExrateVO;

import java.util.List;

/**
 * 一键结转参数VO
 *
 */
public class YjjzPamterVO implements java.io.Serializable{

    //调整汇率
    private AdjustExrateVO[] exrates = null;

    private QmclVO[] qmclvos = null;

    //暂估数据
    private TempInvtoryVO[] zgdata = null;

    private List<QMJzsmNoICVO> qmjznoiclist = null;//不启用库存，商贸结转界面数据

    //当前执行的步骤
    private int currentproject = 1;

    private List<CostForwardVO> list1 = null; // 第一步数据

    private List<CostForwardVO> list2 = null; // 第二步数据

    private List<CostForwardVO> list3 = null;// 第三步数据

    private CostForwardInfo[] list4 = null;// 第四步数据

    private List<CostForwardVO> list5 = null;// 第五步数据

    public AdjustExrateVO[] getExrates() {
        return exrates;
    }

    public void setExrates(AdjustExrateVO[] exrates) {
        this.exrates = exrates;
    }

    public QmclVO[] getQmclvos() {
        return qmclvos;
    }

    public void setQmclvos(QmclVO[] qmclvos) {
        this.qmclvos = qmclvos;
    }

    public int getCurrentproject() {
        return currentproject;
    }

    public void setCurrentproject(int currentproject) {
        this.currentproject = currentproject;
    }

    public TempInvtoryVO[] getZgdata() {
        return zgdata;
    }

    public void setZgdata(TempInvtoryVO[] zgdata) {
        this.zgdata = zgdata;
    }

    public List<CostForwardVO> getList1() {
        return list1;
    }

    public void setList1(List<CostForwardVO> list1) {
        this.list1 = list1;
    }

    public List<CostForwardVO> getList2() {
        return list2;
    }

    public void setList2(List<CostForwardVO> list2) {
        this.list2 = list2;
    }

    public List<CostForwardVO> getList3() {
        return list3;
    }

    public void setList3(List<CostForwardVO> list3) {
        this.list3 = list3;
    }

    public CostForwardInfo[] getList4() {
        return list4;
    }

    public void setList4(CostForwardInfo[] list4) {
        this.list4 = list4;
    }

    public List<CostForwardVO> getList5() {
        return list5;
    }

    public void setList5(List<CostForwardVO> list5) {
        this.list5 = list5;
    }

    public List<QMJzsmNoICVO> getQmjznoiclist() {
        return qmjznoiclist;
    }

    public void setQmjznoiclist(List<QMJzsmNoICVO> qmjznoiclist) {
        this.qmjznoiclist = qmjznoiclist;
    }
}
