package com.dzf.zxkj.platform.service.pzgl;

import com.alibaba.fastjson.JSONArray;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.image.ImageParamVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.pjgl.PictureBrowseVO;
import com.dzf.zxkj.platform.model.pzgl.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IPzglService {

    // 查询
    List<TzpzHVO> query(String period, String pk_corp) throws DZFWarpException;

    List<TzpzBVO> queryB(String hid, String pk_corp) throws DZFWarpException;

    List<TzpzHVO> queryByIDs(String ids, VoucherPrintParam param) throws DZFWarpException;

    TzpzHVO queryByID(String id, String pk_corp) throws DZFWarpException;

    // 审核
    void updateAudit(List<TzpzHVO> hvos, String userid) throws DZFWarpException;

    // 反审核
    void updateUnAudit(List<TzpzHVO> hvos) throws DZFWarpException;

    // 记账
    void updateAccounting(List<TzpzHVO> hvos, String userid) throws DZFWarpException;

    // 反记账
    void updateUnAccounting(List<TzpzHVO> hvos) throws DZFWarpException;

    void update(TzpzHVO vo) throws DZFWarpException;

    /**
     * 凭证整理:手工
     *
     * @param pk_corp
     * @param list
     * @return
     * @throws BusinessException
     */
    int doVoucherOrder3(String pk_corp, JSONArray list) throws DZFWarpException;

    /**
     * 凭证整理：自动
     *
     * @param pk_corps
     * @param beginperiod
     * @param endperiod
     * @return
     * @throws BusinessException
     */
    String doVoucherOrder(String[] pk_corps, DZFDate beginperiod, DZFDate endperiod) throws DZFWarpException;

    String updateNumByDate(String[] pk_corps, DZFDate beginperiod, DZFDate endperiod) throws DZFWarpException;

//	public List<ImageLibraryVO> search(String pk_corp, String begindate, String enddate, String group1, String group2,
//			String nowpage, String pagesize, String status) throws DZFWarpException;

    List<PictureBrowseVO> search(ImageParamVO imgparamvo) throws DZFWarpException;

    /**
     * 查询图片识别信息
     *
     * @param pk_corp 公司
     * @param groups  图片组id
     * @return
     * @throws DZFWarpException
     */
    Map<String, OcrInvoiceVO> queryInvoiceInfo(String pk_corp, Collection<String> groups) throws DZFWarpException;

    /**
     * 查询对公司有权限的用户
     *
     */
    List<UserVO> queryPowerUser(String pk_corp) throws DZFWarpException;

    void reuploadImage(File[] infiles, String imgName, String pk_image_library, String reuploadName, String reuploadType, CorpVO corpvo) throws DZFWarpException;

    void updateCreator(List<String> ids, String newCreator) throws DZFWarpException;

    List<TzpzHVO> saveImportVoucher(CorpVO corp, String user_id, String fileType,
                                    InputStream inputStream, boolean checkVoucherNumber) throws DZFWarpException;

    byte[] exportExcel(String ids) throws DZFWarpException;

    /**
     * 导出凭证模板（含科目、辅助核算、币种参照）
     *
     * @param pk_corp
     * @param tempPath 模板文件路径
     * @return
     * @throws DZFWarpException
     */
    byte[] exportTemplate(String pk_corp, String tempPath) throws DZFWarpException;

    /**
     * 获取凭证列显示设置
     *
     * @param userid
     * @return
     * @throws DZFWarpException
     */
    String readColumnSetting(String userid) throws DZFWarpException;

    /**
     * 保存凭证列显示设置
     *
     * @param pk_corp
     * @param setting
     * @throws DZFWarpException
     */
    void saveColumnSetting(String pk_corp, String userid, String setting) throws DZFWarpException;

    /**
     * 合并凭证
     *
     * @param pk_corp
     * @param ids
     * @throws DZFWarpException
     */
    String[] processMergeVoucher(String userid, String pk_corp, String[] ids,
                                 String zy) throws DZFWarpException;

    /**
     * 按上传顺序生成凭证，凭证号
     */
    String pzsortByuploadpic(String[] pk_corps, DZFDate beginperiod, DZFDate endperiod) throws DZFWarpException;

    /**
     * 出入库 单据号排序
     */
    String savechurukubillcodesort(String[] pk_corps, DZFDate beginperiod, DZFDate endperiod) throws DZFWarpException;

    /**
     * 查询合并规则
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    VoucherMergeSettingVO queryMergeSetting(String pk_corp) throws DZFWarpException;

    /**
     * 保存合并规则
     *
     * @param pk_corp
     * @param setting
     * @return
     * @throws DZFWarpException
     */
    VoucherMergeSettingVO saveMergeSetting(String pk_corp, VoucherMergeSettingVO setting) throws DZFWarpException;

    /**
     * @param pk_corp
     * @param bvos
     * @param summary 摘要
     * @return
     * @throws DZFWarpException
     */
    TzpzBVO[] mergeVoucherEntries(String pk_corp, TzpzBVO[] bvos, String summary) throws DZFWarpException;

    void savePrintAssistSetting(String pk_corp, String userid, VoucherPrintAssitSetVO[] vos) throws DZFWarpException;

    VoucherPrintAssitSetVO[] queryPrintAssistSetting(String pk_corp) throws DZFWarpException;


}
