package com.dzf.zxkj.platform.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzf.zxkj.platform.auth.entity.FunNode;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunNodeMapper extends BaseMapper<FunNode> {
    List<FunNode> getFunNodeByUseridAndPkCorp(String userid, String pk_corp);
}
