package com.dzf.zxkj.platform.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzf.zxkj.platform.auth.entity.LoginUser;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginUserMapper extends BaseMapper<LoginUser> {
    @Update("update sm_user set user_password=#{password} where cuserid = #{userid}")
    int updatePassWord(String userid, String password);
}
