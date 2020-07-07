package com.dzf.zxkj.report.service.pub;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;

import java.util.List;

public interface IReportPubService {


    /**
     * 根据公司查询对应的tax信息，主要查询 单位负责人, 财务负责人
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    List<CorpTaxVo> queryTaxVoByParam(String pk_corp) throws DZFWarpException;
}
