package com.dzf.zxkj.platform.auth.service;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.auth.entity.FunNode;

import java.util.List;

public interface IVersionMngService {
    String[] queryCorpVersion(String pk_corp) throws DZFWarpException;

    List<FunNode> getFunNodeByUseridAndPkCorp(String userid, String pk_corp);
}
