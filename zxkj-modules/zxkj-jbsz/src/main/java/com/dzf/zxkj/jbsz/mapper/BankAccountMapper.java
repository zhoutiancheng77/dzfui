package com.dzf.zxkj.jbsz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzf.zxkj.jbsz.vo.BankAccountVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-02
 * @Description:
 */
@Repository
public interface BankAccountMapper extends BaseMapper<BankAccountVO> {

    List<BankAccountVO> query(String pk_corp, String isnhsty);

    IPage<BankAccountVO> query(Page page, String pk_corp, String isnhsty);
}
