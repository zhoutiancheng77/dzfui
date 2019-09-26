package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.fasterxml.jackson.annotation.JsonProperty;

//合同编码VO
@SuppressWarnings({ "serial", "rawtypes" })
public class ConCodeVO extends SuperVO {

    private String codetype; // 单据类型
    private String pk_contcodeset; // 合同编号主键
    private String pk_corp;// 公司主键
    private String vcontcode;// 合同编号
    private String busitypemax;// 业务大类标识
    private String vproductname; // 业务大类名称
    private String vprefix;// 前缀字符
    private String vyear;// 时间标签（年）
    private String vmonth;// 时间标签（月）
    private String vmidcht;// 中间字符
    private String inumber;// 数字位数
    private int dr;// 删除标志
    private String ts;// 时间戳
    private String coperatorid;// 制单人
    private DZFDate doperatedate;// 制单日期
    @JsonProperty("isc")
    private String ischarge;// 是否是收款单据

    @JsonProperty("product_code")
    private String vproductcode; // 产品编码

    @JsonProperty("btype")
    private String vbilltype;// 单据类型
    
    // 生成get/set方法

    public String getPk_contcodeset() {
        return pk_contcodeset;
    }

    public String getVbilltype() {
        return vbilltype;
    }

    public void setVbilltype(String vbilltype) {
        this.vbilltype = vbilltype;
    }

    public String getVproductcode() {
        return vproductcode;
    }

    public void setVproductcode(String vproductcode) {
        this.vproductcode = vproductcode;
    }

    public void setPk_contcodeset(String pk_contcodeset) {
        this.pk_contcodeset = pk_contcodeset;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getVcontcode() {
        return vcontcode;
    }

    public void setVcontcode(String vcontcode) {
        this.vcontcode = vcontcode;
    }

    public String getBusitypemax() {
        return busitypemax;
    }

    public void setBusitypemax(String busitypemax) {
        this.busitypemax = busitypemax;
    }

    public String getVprefix() {
        return vprefix;
    }

    public void setVprefix(String vprefix) {
        this.vprefix = vprefix;
    }

    public String getVyear() {
        return vyear;
    }

    public void setVyear(String vyear) {
        this.vyear = vyear;
    }

    public String getVmonth() {
        return vmonth;
    }

    public void setVmonth(String vmonth) {
        this.vmonth = vmonth;
    }

    public String getVmidcht() {
        return vmidcht;
    }

    public void setVmidcht(String vmidcht) {
        this.vmidcht = vmidcht;
    }

    public String getInumber() {
        return inumber;
    }

    public void setInumber(String inumber) {
        this.inumber = inumber;
    }

    public int getDr() {
        return dr;
    }

    public void setDr(int dr) {
        this.dr = dr;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public DZFDate getDoperatedate() {
        return doperatedate;
    }

    public void setDoperatedate(DZFDate doperatedate) {
        this.doperatedate = doperatedate;
    }

    public String getCodetype() {
        return codetype;
    }

    public void setCodetype(String codetype) {
        this.codetype = codetype;
    }

    public String getIscharge() {
        return ischarge;
    }

    public void setIscharge(String ischarge) {
        this.ischarge = ischarge;
    }

    public String getVproductname() {
        return vproductname;
    }

    public void setVproductname(String vproductname) {
        this.vproductname = vproductname;
    }

    @Override
    public String getParentPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPKFieldName() {
        // TODO Auto-generated method stub
        return "pk_contcodeset";
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return "ynt_contcodeset";
    }

}
