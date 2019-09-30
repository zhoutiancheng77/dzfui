package com.dzf.zxkj.platform.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzf.zxkj.platform.auth.entity.FunNode;

import java.util.List;

public interface FunNodeMapper extends BaseMapper<FunNode> {
    List<FunNode> getFunNodeByUseridAndPkCorp(String userid, String pk_corp);
}
