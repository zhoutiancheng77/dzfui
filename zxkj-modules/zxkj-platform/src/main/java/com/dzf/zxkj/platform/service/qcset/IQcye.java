package com.dzf.zxkj.platform.service.qcset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.qcset.QcYeCurrency;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.qcset.SsphRes;
import com.dzf.zxkj.platform.model.qcset.VerifyBeginVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IQcye {
    // 查询
    Map<String, QcYeVO[]> query(String pk_corp, String pk_currency, String isShowFC) throws DZFWarpException;

    // 保存，返回结果
    void save(String userid, DZFDate jzdate, String pk_currency, String pk_corp, QcYeVO[] qcvos)
            throws DZFWarpException;

    // 试算平衡
    SsphRes ssph(String pk_corp) throws DZFWarpException;

    // 固定资产同步
    void saveGdzcsync(String userid, DZFDate jzdate, String pk_corp) throws DZFWarpException;

    // 查询币种
    QcYeCurrency[] queryCur(String pk_corp) throws DZFWarpException;

    QcYeVO queryByPrimaryKey(String primaryKey);

    /**
     * 保存单个科目期初（一级科目包括所有下级科目）
     *
     * @param userid
     * @param jzdate
     * @param pk_currency
     * @param pk_corp
     * @param qcvos
     * @throws DZFWarpException
     */
    void saveOne(String userid, DZFDate jzdate, String pk_currency, String pk_corp, QcYeVO[] qcvos)
            throws DZFWarpException;

    void deleteAll(String pk_corp) throws DZFWarpException;

    void deleteFs(String pk_corp) throws DZFWarpException;

    void saveVerifyBegin(String pk_accsubj, CorpVO corp, VerifyBeginVo[] vos) throws DZFWarpException;

    List<String> queryVerifyBeginAccounts(String pk_corp) throws DZFWarpException;

    List<VerifyBeginVo> queryVerifyBegin(String pk_corp, String pk_accsubj) throws DZFWarpException;

    byte[] exportExcel(String pk_corp, boolean showQuantity) throws DZFWarpException;

    /**
     * 导出辅助
     *
     * @param pk_corp
     * @param pk_accsubj
     * @param tempPath     模板路径
     * @param showQuantity
     * @return
     * @throws DZFWarpException
     */
    byte[] exportFzExcel(String pk_corp, String pk_accsubj, String tempPath, boolean showQuantity) throws DZFWarpException;

    void processImportExcel(String pk_corp, DZFDate jzdate, InputStream file) throws DZFWarpException;

    /**
     * 导入辅助期初
     *
     * @param pk_corp
     * @param jzdate
     * @param file
     * @throws DZFWarpException
     */
    void processFzImportExcel(CorpVO corpVo, String userId,
                              String pk_accsubj, DZFDate jzdate, InputStream file)
            throws DZFWarpException;

    void saveKc2GLSync(String userid, String pk_corp) throws DZFWarpException;

    void saveGL2KcSync(String userid, String pk_corp, StringBuffer msg) throws DZFWarpException;

    List<QcYeVO> queryAllQcInfo(String pk_corp, String pk_currence,
                                String rmb) throws DZFWarpException;

}
