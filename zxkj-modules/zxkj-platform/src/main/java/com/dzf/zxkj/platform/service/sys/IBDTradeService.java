package com.dzf.zxkj.platform.service.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.BDTradeVO;
import com.dzf.zxkj.platform.model.sys.ComboBoxVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 行业
 *
 */

public interface IBDTradeService {
    public List<BDTradeVO> queryTrade(BDTradeVO paramvo) throws DZFWarpException;

    public BDTradeVO queryByID(String id) throws DZFWarpException;

    public BDTradeVO save(BDTradeVO vo) throws DZFWarpException;

    public void delete(BDTradeVO vo) throws DZFWarpException;

    public String existCheck(BDTradeVO vo) throws DZFWarpException;

    public Integer queryTotalRow(BDTradeVO paramvo) throws DZFWarpException;

    public List<BDTradeVO> queryRef(BDTradeVO vo) throws DZFWarpException;

    /**
     * 大账房行业下拉参照
     * @author gejw
     * @time 上午10:30:35
     * @return
     * @throws DZFWarpException
     */
    public ArrayList<ComboBoxVO> queryComboBox() throws DZFWarpException;
}
