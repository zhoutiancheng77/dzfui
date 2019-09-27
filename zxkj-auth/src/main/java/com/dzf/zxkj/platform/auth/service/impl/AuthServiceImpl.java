package com.dzf.zxkj.platform.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.auth.mapper.CorpMapper;
import com.dzf.zxkj.platform.auth.model.CorpModel;
import com.dzf.zxkj.platform.auth.service.IAuthService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service(version = "1.0.0", timeout = 2 * 60 * 1000)
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private CorpMapper corpMapper;

    @Override
    public CorpModel queryCorpByPk(String pk_corp) {
        if(StringUtil.isEmptyWithTrim(pk_corp)){
            return null;
        }
        QueryWrapper<CorpModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CorpModel::getPk_corp, pk_corp).and(condition -> condition.eq(CorpModel::getDr, "0").or().isNull(CorpModel::getDr));
        CorpModel corpModel = corpMapper.selectOne(queryWrapper);
        if(corpModel == null){
            return null;
        }
        corpModel.setUnitname(CodeUtils1.deCode(corpModel.getUnitname()));
        corpModel.setUnitshortname(CodeUtils1.deCode(corpModel.getUnitshortname()));
        return corpModel;
    }
}
