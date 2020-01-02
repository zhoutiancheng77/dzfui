package com.dzf.zxkj.platform.auth.service;


import com.dzf.zxkj.base.exception.DZFWarpException;

public interface IVersionMngService {
    String[] queryCorpVersion(String pk_corp) throws DZFWarpException;
}
