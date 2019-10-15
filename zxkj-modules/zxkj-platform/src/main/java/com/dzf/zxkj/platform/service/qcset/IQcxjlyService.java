package com.dzf.zxkj.platform.service.qcset;

import com.dzf.zxkj.platform.model.qcset.YntXjllqcyePageVO;
import com.dzf.zxkj.platform.service.common.IBgPubService;

public interface IQcxjlyService extends IBgPubService {

    YntXjllqcyePageVO queryByPrimaryKey(String primaryKey);
}
