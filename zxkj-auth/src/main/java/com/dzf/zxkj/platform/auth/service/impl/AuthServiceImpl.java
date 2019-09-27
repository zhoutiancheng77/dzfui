package com.dzf.zxkj.platform.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.platform.auth.mapper.CorpMapper;
import com.dzf.zxkj.platform.auth.model.CorpModel;
import com.dzf.zxkj.platform.auth.service.IAuthService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private CorpMapper corpMapper;


    @Override
    public CorpModel queryCorpByPk(String pk_corp) throws Exception {
        QueryWrapper<CorpModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CorpModel::getPk_corp, pk_corp).ne(CorpModel::getDr,"1");
        CorpModel corpModel = corpMapper.selectOne(queryWrapper);
        corpModel.setUnitname(CodeUtils1.deCode(corpModel.getUnitname()));
        corpModel.setUnitshortname(CodeUtils1.deCode(corpModel.getUnitshortname()));
        return corpModel;
    }
}
