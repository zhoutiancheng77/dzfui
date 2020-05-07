package com.dzf.zxkj.platform.service.bdset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IAuxiliaryAccountService {
    /**
     * 根据id查询辅助项目
     *
     * @param pk_auacount_h
     * @return
     * @throws DZFWarpException
     */
    AuxiliaryAccountHVO queryHByID(String pk_auacount_h) throws DZFWarpException;

    /**
     * 根据code获取辅助类别vo
     *
     * @param pk_corp
     * @param code
     * @return
     * @throws DZFWarpException
     */
    AuxiliaryAccountHVO queryHByCode(String pk_corp, String code) throws DZFWarpException;

    /**
     * 根据id查询辅助明细
     *
     * @param pk_auacount_b
     * @return
     * @throws DZFWarpException
     */
    AuxiliaryAccountBVO queryBByID(String pk_auacount_b, String pk_corp) throws DZFWarpException;

    /**
     * 查询全部辅助核算项目（包含预置）
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    AuxiliaryAccountHVO[] queryH(String pk_corp) throws DZFWarpException;

    AuxiliaryAccountBVO[] queryByH(String pk_corp, String pk_auacount_h) throws DZFWarpException;
    /**
     * 查询自定义辅助核算项目
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    AuxiliaryAccountHVO[] queryHCustom(String pk_corp) throws DZFWarpException;

    AuxiliaryAccountBVO[] queryB(String pk_auacount_h, String pk_corp, String kmid) throws DZFWarpException;

    QueryPageVO queryBodysBypage(String pk_auacount_h, String pk_corp, String kmid, int page, int rows, String type) throws DZFWarpException;

    AuxiliaryAccountBVO queryBByName(String pk_corp, String name, String hid) throws DZFWarpException;

    AuxiliaryAccountBVO queryBByCode(String pk_corp, String code, String hid) throws DZFWarpException;

//	public AuxiliaryAccountBVO[] queryBParam(String pk_auacount_h, String param, String pk_corp)throws DZFWarpException;

    QueryPageVO queryBParamBypage(String pk_auacount_h, String param, String pk_corp, int page, int rows, String type) throws DZFWarpException;

//	public AuxiliaryAccountBVO[] queryBInvBycondition(String pk_auacount_h, String code,String name,String spec, String qchukukmid, String qkmclassify, String pk_corp)
//			throws DZFWarpException;

    QueryPageVO queryBInvByconditionBypage(String pk_auacount_h, String code, String name, String spec, String qchukukmid, String qkmclassify, String pk_corp, int page, int rows)
            throws DZFWarpException;


    AuxiliaryAccountHVO saveH(AuxiliaryAccountHVO hvo) throws DZFWarpException;

    AuxiliaryAccountBVO saveB(AuxiliaryAccountBVO bvo) throws DZFWarpException;

    void delete(AuxiliaryAccountHVO hvo) throws DZFWarpException;

    void delete(AuxiliaryAccountBVO bvo) throws DZFWarpException;

    /**
     * @param bvos
     * @return 成功删除数量
     * @throws DZFWarpException
     */
    String[] delete(AuxiliaryAccountBVO[] bvos) throws DZFWarpException;

    /**
     *
     */
    Map<String, String> saveBImp(InputStream is, String pk_auacount_h, String pk_corp, String fileType) throws DZFWarpException;

    /**
     *
     */
    Map<String, String> updateBImp(InputStream is, String pk_corp, String fileType, Map<Integer, String> STYLE_1) throws DZFWarpException;

    /**
     * 检查辅助明细是否被总账引用
     *
     * @param bvo
     * @param type 0 修改编码， 1删除
     * @throws DZFWarpException
     */
    void checkBRef(AuxiliaryAccountBVO bvo, int type) throws DZFWarpException;

    Map<String, AuxiliaryAccountBVO> queryMap(String pk_corp) throws DZFWarpException;


    AuxiliaryAccountBVO[] queryAllB(String pk_corp) throws DZFWarpException;

    /**
     * 通过辅助类别编码+公司获取辅助核算项目
     *
     * @param pk_corp
     * @param fzlb
     * @return
     * @throws DZFWarpException
     */
    AuxiliaryAccountBVO[] queryAllBByLb(String pk_corp, String fzlb) throws DZFWarpException;

    /**
     * 根据名称获取辅助核算项，没有则新建一条记录
     *
     * @param pk_corp
     * @param name
     * @param hid
     * @return
     * @throws DZFWarpException
     */
    AuxiliaryAccountBVO createBvoByName(String pk_corp, String name, String hid) throws DZFWarpException;

    /**
     * 检查名称编码是否重复
     *
     * @param bvo
     * @param type
     * @throws DZFWarpException
     */
    boolean checkRepeat(AuxiliaryAccountBVO bvo) throws DZFWarpException;

    /**
     * 科目是否为存货大类
     */
    boolean isInventoryCategory(String corpId, String subjectId) throws DZFWarpException;


    /**
     * 辅助是否存在
     * @param pk_corp
     * @param pk_auacount_b
     * @param pk_auacount_h
     * @return
     * @throws DZFWarpException
     */
    boolean isExistFz(String pk_corp, String pk_auacount_b,String pk_auacount_h) throws DZFWarpException;
    /**
     * 根据名称查询相关税务信息
     *
     * @param invname
     */
    AuxiliaryAccountBVO[] queryInvtaxInfo(String invname, String pk_corp) throws DZFWarpException;

    /**
     * 按照类型查找人员信息
     *
     * @param pk_auacount_h
     * @param pk_corp
     * @param billtype
     * @return
     * @throws DZFWarpException
     */
    List<AuxiliaryAccountBVO> queryPerson(String pk_auacount_h, String pk_corp, String billtype) throws DZFWarpException;

    /**
     * 批量保存
     *
     * @param list
     * @return
     * @throws DZFWarpException
     */
    AuxiliaryAccountBVO[] saveBs(List<AuxiliaryAccountBVO> list, boolean isadd) throws DZFWarpException;

    /**
     * 存货档案（存货大类方式） 批量修改 存货类别 和出库科目字段， 通过 选中项
     *
     * @param list
     * @return
     * @throws DZFWarpException
     */
    int updateBatchAuxiliaryAccountByIDS(List<AuxiliaryAccountBVO> list, String[] modifyFiled) throws DZFWarpException;

    AuxiliaryAccountHVO queryHByName(String pk_corp, String name) throws DZFWarpException;

    /*
        封存职员
     */
    void onSeal(String id) throws DZFWarpException;

    /*
        解封职员
     */
    void unSeal(String id) throws DZFWarpException;

    /*
        根据id批量修改
     */
    int updateBatchAuxiliaryAccountByID(AuxiliaryAccountBVO[] auxiliaryAccountBVOS, String[] modifyFiled) throws DZFWarpException;

    AuxiliaryAccountBVO saveMergeData(String pk_corp, String id, AuxiliaryAccountBVO[] vos) throws DZFWarpException;
}
