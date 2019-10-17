package com.dzf.zxkj.platform.service.bdset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.SurtaxTemplateVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;

public interface ISurtaxTemplateService {
    List<SurtaxTemplateVO> query(CorpVO corp) throws DZFWarpException;

    /**
     * 获取集团预置模板
     *
     * @param corp
     * @return
     * @throws DZFWarpException
     */
    List<SurtaxTemplateVO> getPresetTemplate(CorpVO corp) throws DZFWarpException;

    SurtaxTemplateVO[] save(String pk_corp, SurtaxTemplateVO[] vos) throws DZFWarpException;
}
