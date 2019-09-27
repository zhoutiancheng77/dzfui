package com.dzf.zxkj.platform.auth.service;

import com.dzf.zxkj.platform.auth.model.CorpModel;

public interface IAuthService {
    CorpModel queryCorpByPk(String pk_corp);
}
