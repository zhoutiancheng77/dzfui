package com.dzf.admin.model.app.transfer.filetrans;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RetDataFileDocVO implements Serializable {

    @JsonProperty("id")
    private String pk_kfiledoc;//主键

    @JsonProperty("pk_source")
    private String pk_source;//数据来源

    @JsonProperty("corp")
    private String pk_corp;//公司

    @JsonProperty("code")
    private String vcode;//编码

    @JsonProperty("name")
    private String vname;//名称

    @JsonProperty("memo")
    private String vmemo;//备注

    @JsonProperty("isseal")
    private Boolean issealed;//是否封存

    @JsonProperty("isleaf")
    private Boolean isleaf;//是否末级

    @JsonProperty("filelevel")
    private Integer filelevel;//层级

    private Integer dr;//删除标志

    private String ts;//时间戳

    @JsonProperty("vtypeid")
    private String pk_filetype;//资料类型主键

    @JsonProperty("vtypenm")
    private String filetypename;//资料类型名称

    @JsonProperty("itype")
    private Integer ifiletype; // ：0：原件；1：复印件；2：电子资料；3：打印资料

    @JsonProperty("proper")
    private Integer iproperty;//资料属性  0：普通类；1：财务类（记账凭证、财务账簿、财务报表）；

    @JsonProperty("only")
    private Boolean isonly;//每个客户都只会有一件

}
