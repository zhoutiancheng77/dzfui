package com.dzf.admin.model.app.transfer.filetrans;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RetDataFileDocVO implements Serializable {

    @JsonProperty("id")
    @ApiModelProperty(name="id",value = "主键")
    private String pk_kfiledoc;//主键

    @JsonProperty("pk_source")
    @ApiModelProperty(name="pk_source",value = "数据来源")
    private String pk_source;//数据来源

    @JsonProperty("corp")
    @ApiModelProperty(name="corp",value = "公司")
    private String pk_corp;//公司

    @JsonProperty("code")
    @ApiModelProperty(name="code",value = "编码")
    private String vcode;//编码

    @JsonProperty("name")
    @ApiModelProperty(name="name",value = "名称")
    private String vname;//名称

    @JsonProperty("memo")
    @ApiModelProperty(name="memo",value = "备注")
    private String vmemo;//备注

    @JsonProperty("isseal")
    @ApiModelProperty(name="isseal",value = "是否封存",dataType = "java.lang.Boolean")
    private Boolean issealed;//是否封存

    @JsonProperty("isleaf")
    @ApiModelProperty(name="isleaf",value = "是否末级",dataType = "java.lang.Boolean")
    private Boolean isleaf;//是否末级

    @JsonProperty("filelevel")
    @ApiModelProperty(name="filelevel",value = "层级")
    private Integer filelevel;//层级

    private Integer dr;//删除标志

    private String ts;//时间戳

    @JsonProperty("vtypeid")
    @ApiModelProperty(name="vtypeid",value = "资料类型主键")
    private String pk_filetype;//资料类型主键

    @JsonProperty("vtypenm")
    @ApiModelProperty(name="vtypenm",value = "资料类型名称")
    private String filetypename;//资料类型名称

    @JsonProperty("itype")
    @ApiModelProperty(name="itype",value = "0：原件；1：复印件；2：电子资料；3：打印资料")
    private Integer ifiletype; // ：0：原件；1：复印件；2：电子资料；3：打印资料

    @JsonProperty("proper")
    @ApiModelProperty(name="proper",value = "资料属性  0：普通类；1：财务类（记账凭证、财务账簿、财务报表）；")
    private Integer iproperty;//资料属性  0：普通类；1：财务类（记账凭证、财务账簿、财务报表）；

    @JsonProperty("only")
    @ApiModelProperty(name="only",value = "每个客户都只会有一件",dataType = "java.lang.Boolean")
    private Boolean isonly;//每个客户都只会有一件

}
