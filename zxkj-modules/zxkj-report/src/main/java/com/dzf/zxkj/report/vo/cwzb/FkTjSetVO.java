package com.dzf.zxkj.report.vo.cwzb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FkTjSetVO {
    @JsonProperty("id")
    private String pk_fktj;//
    private String pk_corp;// 公司
    private String vmemo;// 备注
    @JsonProperty("idate")
    private DZFDateTime inspectdate;// 体检时间
    private String qj;//会计期间
    private String vinspector;// 体检人
    private Integer dr;// 标识
    private DZFDateTime ts;//
    private String hy;//行业

    private DZFDate begindate;
    private DZFDate enddate;//结束时间
}
