package com.dzf.zxkj.platform.service.pzgl;

import com.alibaba.fastjson.JSONObject;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.platform.model.bdset.PZTaxItemRadioVO;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.ImageLibraryVO;
import com.dzf.zxkj.platform.model.image.ImageParamVO;
import com.dzf.zxkj.platform.model.image.ImageTurnMsgVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.report.XjllVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;

import java.util.List;

public interface IVoucherService {
    // 分页查询
    QueryPageVO query(VoucherParamVO paramvo) throws DZFWarpException;

    QueryPageVO processQueryVoucherPaged(VoucherParamVO paramvo) throws DZFWarpException;

    TzpzHVO saveVoucher(CorpVO corpvo, TzpzHVO hvo) throws DZFWarpException;

    TzpzHVO deleteVoucher(TzpzHVO hvo) throws DZFWarpException;

    TzpzHVO queryVoucherById(String id) throws DZFWarpException;

    /**
     * 根据id查询凭证
     *
     * @param pklists
     * @param containsChildren
     * @param containsRelation
     * @param containsTaxItem
     * @return
     * @throws DZFWarpException
     */
    List<TzpzHVO> queryVoucherByIds(List<String> pklists, boolean containsChildren, boolean containsRelation,
                                    boolean containsTaxItem) throws DZFWarpException;

    List<TzpzHVO> queryVoucherByIds(List<String> pklists) throws DZFWarpException;

    ImageLibraryVO[] queryImageVO(String pk_image_group) throws DZFWarpException;// 根据图片组查询图片信息

    /**
     * 更新数据
     *
     * @param pk_corp(退回/更新标识需要)
     * @param pk_image_group     (退回/更新标识使用)
     * @param pk_image_library
     * @param ident              (退回/更新标识都需要)
     * @param msgvo              (退回消息)
     * @param hvo                (退回的凭证)
     * @throws DZFWarpException
     */
    void updateImageVo(String pk_corp, String pk_image_group, String pk_image_library, String ident,
                       ImageTurnMsgVO msgvo, TzpzHVO hvo, boolean delVch,
                       String curropeid, boolean isadd) throws DZFWarpException;// 图片退回/更新图片是否已经使用

    TzpzHVO queryHeadVoById(String id) throws DZFWarpException;

    /**
     * 现金流分配
     */
    List<XjllVO> addCashFlow(XjllVO[] xjllvos) throws DZFWarpException;

    /**
     * 现金流查询
     */
    XjllVO[] queryCashFlow(String pk_tzpz_h, String pk_corp) throws DZFWarpException;

    /**
     * 现金流量删除
     */
    void deleteCashFlow(String pk_tzpz_h, String pk_corp) throws DZFWarpException;

    /**
     * 已期间损益则抛出-150异常
     */
    void checkQjsy(TzpzHVO headVO) throws DZFWarpException;

    /**
     * @param corpvo
     * @param ids
     * @param copyPeriod 复制期间
     * @param aimPeriod  目标期间
     * @param aimDate    目标日期（天）
     * @param userId
     * @return
     * @throws DZFWarpException
     */
    List<TzpzHVO> processCopyVoucher(CorpVO corpvo, List<String> ids, String copyPeriod,
                                     String aimPeriod, String aimDate, String userId) throws DZFWarpException;

    /**
     * 制单日期和凭证号 为红字回冲查询使用
     */
    List<TzpzHVO> serHasRedBack(VoucherParamVO paramvo);

    // 根据图片pk查询凭证pk
    List<TzpzHVO> serVoucherByImgPk(VoucherParamVO paramvo) throws DZFWarpException;

    // 根据Pk查询图片组
    ImageGroupVO queryImageGroupByPrimaryKey(String pk_image_group) throws DZFWarpException;

    /**
     * 填制凭证 图片查询
     */
    List<ImageGroupVO> queryImageGroupByPicture(ImageParamVO param)
            throws DZFWarpException;

    /**
     * 检查图片组是否存在,包括删除数据
     * @param id
     * @return
     * @throws DZFWarpException
     */
    boolean checkImageGroupExist(String id)
            throws DZFWarpException;

    /**
     * 校验暂估生成的凭证不可删除
     *
     * @param pk_corp
     * @param pk_Intrade_h
     */
    void checkIcIntrade(String pk_corp, String pk_Intrade_h);

    TzpzHVO createVoucherQuick(CorpVO corp, String user_id, JSONObject rawData) throws DZFWarpException;

    void saveTaxItem(TzpzHVO hvo, CorpVO corpvo) throws DZFWarpException;

    String getLastVoucherDate(String pk_corp) throws DZFWarpException;

    /**
     * 查询纳税类型对应的所有税目
     *
     * @param chargeType 公司纳税类型
     * @return
     * @throws DZFWarpException
     */
    List<TaxitemVO> getTaxItems(String chargeType) throws DZFWarpException;

    /**
     * 查询凭证税表表项
     *
     * @param pk_voucher 凭证ID
     * @param pk_corp    公司ID
     * @return 凭证税表表项
     * @throws DZFWarpException
     */
    PZTaxItemRadioVO[] getVoucherTaxItem(String pk_voucher, String pk_corp) throws DZFWarpException;

    /**
     * 保存税表表项
     *
     * @param items   税表表项
     * @param pk_corp 公司ID
     * @throws DZFWarpException
     */
    void saveVoucherTaxItem(PZTaxItemRadioVO[] items, String pk_corp) throws DZFWarpException;

    /**
     * 删除凭证税表表项
     *
     * @param pk_voucher 凭证ID
     * @param pk_corp    公司ID
     * @throws DZFWarpException
     */
    void deleteVoucherTaxItem(String pk_voucher, String pk_corp) throws DZFWarpException;

    String checkChannelContract(String gs) throws DZFWarpException;
}
