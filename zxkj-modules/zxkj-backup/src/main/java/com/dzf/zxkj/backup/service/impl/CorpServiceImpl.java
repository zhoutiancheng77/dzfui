package com.dzf.zxkj.backup.service.impl;

import com.dzf.zxkj.backup.service.ICorpService;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CorpServiceImpl implements ICorpService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public CorpVO queryByPk(String pk_corp) {
        return (CorpVO) singleObjectBO.queryVOByID(pk_corp, CorpVO.class);
    }
}
