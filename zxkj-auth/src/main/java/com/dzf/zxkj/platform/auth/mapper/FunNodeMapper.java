package com.dzf.zxkj.platform.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzf.zxkj.platform.auth.entity.FunNode;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunNodeMapper extends BaseMapper<FunNode> {
    List<FunNode> getFunNodeByUseridAndPkCorp(@Param("userid") String userid, @Param("pk_corp") String pk_corp);
}
