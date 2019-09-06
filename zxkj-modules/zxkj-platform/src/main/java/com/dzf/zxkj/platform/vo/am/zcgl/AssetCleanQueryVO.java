package com.dzf.zxkj.platform.vo.am.zcgl;

import com.dzf.zxkj.common.lang.DZFDate;
import lombok.Data;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
@Data
public class AssetCleanQueryVO {
    //日期
    private DZFDate start_date;
    private DZFDate end_date;
    //资产卡片
    private String asscd_id;
    private String ascode;
    private String pk_corp;
}
