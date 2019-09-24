package com.dzf.zxkj.platform.services.bdset;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.PzmbbVO;
import com.dzf.zxkj.platform.model.bdset.PzmbhVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;

public interface IPzmbhService {

    // 保存
    PzmbhVO save(PzmbhVO vo) throws DZFWarpException;

    // 修改
    // public void update(PzmbhVO vo)throws BusinessException;

    // 查询
    List<PzmbhVO> query(String pk_corp) throws DZFWarpException;

    /**
     * 包含子表数据
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    List<PzmbhVO> queryWithBody(String pk_corp) throws DZFWarpException;

    // 查询带科目
    List<PzmbhVO> queryAll(String pk_corp) throws DZFWarpException;

    // 查询字表
    List<PzmbbVO> queryB(String pk_corp) throws DZFWarpException;

    // 删除
    void delete(String pid) throws DZFWarpException;

    // 根据主键查找
    PzmbhVO queryById(String id) throws DZFWarpException;

    String copy(String[] tmps, CorpVO[] corps, String login_corp) throws DZFWarpException;

    /**
     * 获取新编码
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    String getNewCode(String pk_corp) throws DZFWarpException;
}
