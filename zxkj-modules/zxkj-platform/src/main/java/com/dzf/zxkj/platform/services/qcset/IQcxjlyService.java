package com.dzf.zxkj.platform.services.qcset;

import com.dzf.zxkj.platform.model.qcset.YntXjllqcyePageVO;
import com.dzf.zxkj.platform.services.common.IBgPubService;

public interface IQcxjlyService extends IBgPubService {

    YntXjllqcyePageVO queryByPrimaryKey(String primaryKey);
}
