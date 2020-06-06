package com.dzf.admin.model.app.transfer.filetrans;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RetAppFiletransBVO implements Serializable {

    @JsonProperty("pk_bid")
    private String pk_filetrans_b; //子表主键

    @JsonProperty("pk_hid")
    private String pk_filetrans_h;//主表主键

    @JsonProperty("pk_id")
    private String pk_filetrans; // 资料台账主键

    @JsonProperty("corpid")
    private String pk_corp; // 所属会计公司

    @JsonProperty("ddate")
    private String ddealdate; // 操作时间

    @JsonProperty("vsta")
    private Integer vstatus; // 借/还 ( 1：借出；2：归还 ；3新增 4：归还客户)

    @JsonProperty("vhand")
    private String vhanderid; // 经手人

    @JsonProperty("vhandna")
    private String vhandname; // 经手人姓名

    @JsonProperty("purp")
    private String purpose; // 用途

    @JsonProperty("memo")
    private String vmemo; // 备注

    @JsonProperty("dr")
    private Integer dr; // 删除标记

    @JsonProperty("ts")
    private String ts; // 时间戳

    @JsonProperty("opid")
    private String coperatorid; // 录入人

    @JsonProperty("dodate")
    private String doperatedate; // 录入日期

    //此次新增字段&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

    @JsonProperty("corpkid")
    private String pk_corpk; // 客户公司主键

    @JsonProperty("corpkcd")
    private String corpkcode; // 客户公司编码

    @JsonProperty("corpkna")
    private String corpkname; // 客户公司名称

    @JsonProperty("pk_file")
    private String pk_filedoc; // 资料主键

    @JsonProperty("vfname")
    private String vfilename; // 资料名称

    @JsonProperty("vfcode")
    private String vfilecode; // 资料编码

    @JsonProperty("ftype")
    private String pk_filetype; //资料类型主键

    @JsonProperty("ftypenm")
    private String filetypename; //资料类型名称

    @JsonProperty("nums")
    private Integer nfilenums;//资料件数

    @JsonProperty("vbperiod")
    private String vbegperiod;//开始期间

    @JsonProperty("veperiod")
    private String vendperiod;//结束期间

    @JsonProperty("vsuerid")
    private String vsurrender;//交出人

    @JsonProperty("vsuernm")
    private String vsuername;//交出人名称

    @JsonProperty("vsuercp")
    private String vsuercorp;//交出人所属公司

    @JsonProperty("vcaerid")
    private String vcatcher;//接手人

    @JsonProperty("vcaernm")
    private String vcaername;//接手人名称

    @JsonProperty("vcaercd")
    private String vcaercode;//接手人编码

    @JsonProperty("vcaercp")
    private String vcaercorp;//接手人所属公司

    @JsonProperty("vbsta")
    private Integer vbstatus;//状态：1：未确认；2：已确认；3：部分确认

    @JsonProperty("vstanm")
    private String vstaname;//状态名称

    @JsonProperty("vconid")
    private String vconfirmpsn;// 确认人

    @JsonProperty("dcondate")
    private String dconfirmdate;// 确认时间

    @JsonProperty("itype")
    private Integer ifiletype; // 0：原件；1：复印件；2：电子资料；3：打印资料；

    @JsonProperty("vnconfirm")
    private String vneedconfirm;//待确认人

    @JsonProperty("vnconm")
    private String vneedconame;//待确认人姓名

    @JsonProperty("sdate")
    private String vshowdate;//显示日期

    @JsonProperty("showmemo")
    private String vshowmemo; // 展示备注（app使用）

    @JsonProperty("only")
    private Boolean isonly;//每个客户都只会有一件（仅作数据传递校验使用，不存库）

}
