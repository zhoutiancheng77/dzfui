package com.dzf.zxkj.platform.services.zcgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.base.tree.AccTreeCreateStrategy;
import com.dzf.zxkj.base.tree.BDTreeCreator;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.sys.BdtradeAccountSchemaVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.CorpAssetCategoryVO;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import com.dzf.zxkj.platform.services.zcgl.IZclbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产类别
 */
@Service("sys_zclbserv")
public class ZclbServiceImpl implements IZclbService {

    private SingleObjectBO singleObjectBO = null;

    public SingleObjectBO getSingleObjectBO() {
        return singleObjectBO;
    }

    @Autowired
    private ICorpService corpService;
    @Autowired
    public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
        this.singleObjectBO = singleObjectBO;
    }

    @Override
    public BdAssetCategoryVO[] queryParent(String pk_corp, Integer AccountSchema) throws DZFWarpException {
        String corpWhere = queryParentPk(pk_corp);  //先查询当前公司对应的上级公司id  包含当前公司
        //在查询这个几个公司的全部信息
        String condition;
        if (pk_corp.equals("000001")) {
            condition = " pk_corp  in (" + corpWhere + ") and nvl(dr,0) = 0 order by catecode ";
        } else {
            if (DzfUtil.POPULARSCHEMA.intValue() == AccountSchema || DzfUtil.CAUSESCHEMA.intValue() == AccountSchema) {
                condition = " pk_corp  in (" + corpWhere + ") and nvl(dr,0) = 0 and ( catecode like'03%'  or  catecode like'02%' ) order by catecode ";
            } else {
                condition = " pk_corp  in (" + corpWhere + ") and nvl(dr,0) = 0 and (catecode like'01%' or  catecode like'02%' or catecode like '04%' ) order by catecode  ";
            }
        }
        BdAssetCategoryVO[] vos = (BdAssetCategoryVO[]) singleObjectBO.queryByCondition(BdAssetCategoryVO.class, condition, null);
        if (vos == null || vos.length == 0)
            return null;

        //赋值公司设置数据
        putCorpData(pk_corp, vos);

        return vos;
    }

    private String queryParentPk(String pk_corp) throws DZFWarpException {
        //先查询当前公司对应的上级公司信息
        String sql = "select pk_corp from bd_corp start with pk_corp =? connect by prior fathercorp = pk_corp";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        List<String> corpList = (List<String>) singleObjectBO.executeQuery(sql, sp, new ColumnListProcessor());
        return SqlUtil.buildSqlConditionForIn(corpList.stream().toArray(String[]::new));
    }


    @Override
    public BdAssetCategoryVO[] queryAssetCategory(String pk_corp) throws DZFWarpException {
        BdAssetCategoryVO[] vos = queryParent(pk_corp, getAccountSchema(pk_corp));
        if (vos == null || vos.length == 0)
            return new BdAssetCategoryVO[0];
        BdAssetCategoryVO vo = (BdAssetCategoryVO) BDTreeCreator.createTree(
                vos, new AccTreeCreateStrategy(DZFConstant.ZCLBRULE) {
                    @Override
                    public SuperVO getRootVO() {
                        return new BdAssetCategoryVO();
                    }

                    @Override
                    public String getCodeValue(Object userObj) {
                        return ((BdAssetCategoryVO) userObj).getCatecode();
                    }
                });

        return vo.getChildren();
    }
    /**
     * 新增保存
     */
    private void insertVO(BdAssetCategoryVO vo) throws DZFWarpException {
        //查询上级是否引用
        BdAssetCategoryVO parent = queryParentVO(vo);
        if (parent == null)
            throw new BusinessException("上级资产类别不存在，请确认资产类别编码是否正确！");
        boolean isref = checkIsUsed1(parent) || checkIsUsed2(parent) || checkIsUsed3(parent);
        if (isref)
            throw new BusinessException("上级资产类别已被引用，不能新增！");
        //赋默认值
        vo.setAssetproperty(parent.getAssetproperty());
        vo.setCatelevel(parent.getCatelevel() + 1);
        //公司设置
        saveCorpAssetType(vo);
        singleObjectBO.saveObject(vo.getPk_corp(), vo);
    }

    /**
     * 修改保存
     * 编码不允许 修改，只能修改名称和备注。前台控制不能修改编码
     */
    private void updateVO(BdAssetCategoryVO vo) throws DZFWarpException {
        //查询当前公司设置(,"memo","uselimit","zjtype","salvageratio")
        saveCorpAssetType(vo);
        singleObjectBO.update(vo, new String[]{"catename"});
    }


    private BdAssetCategoryVO queryParentVO(BdAssetCategoryVO vo) throws DZFWarpException {
        if (vo == null)
            return null;
        BdAssetCategoryVO parent = null;
        String parentCode = vo.getCatecode().substring(0, vo.getCatecode().length() - 2);
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_corp());
        sp.addParam(parentCode);
        BdAssetCategoryVO[] bdAssetVos = (BdAssetCategoryVO[]) singleObjectBO.queryByCondition(BdAssetCategoryVO.class, " pk_corp in(?,'000001') and nvl(dr,0)=0 and catecode= ?", sp);
        if (bdAssetVos != null && bdAssetVos.length > 0) {
            parent = bdAssetVos[0];
            parent.setPk_corp(vo.getPk_corp());
        }
        return parent;
    }

    @Override
    public void save(BdAssetCategoryVO vo) throws DZFWarpException {
        if (!StringUtil.isEmpty(vo.getPk_assetcategory()))
            updateVO(vo);
        else
            insertVO(vo);
    }


    @Override
    public void delete(BdAssetCategoryVO vo, String Pk_corp) throws DZFWarpException {
        if (checkIsUsed3(vo)) {
            throw new BusinessException("资产类型被计提折旧清理凭证模板引用，不能删除！");
        }
        if (checkIsUsed2(vo)) {
            throw new BusinessException("资产类型被被公司资产与总帐对账表引用，不能删除！");
        }
        if (checkIsUsed1(vo)) {
            throw new BusinessException("资产类型被资产卡片引用，不能删除！");
        }
        singleObjectBO.deleteObject(vo);
    }


    public void update(BdAssetCategoryVO vo)
            throws DZFWarpException {
        singleObjectBO.update(vo);
    }


    @Override
    public String existCheck(BdAssetCategoryVO vo, String pk_corp) throws DZFWarpException {
        SQLParameter sp1 = new SQLParameter();
        String code = vo.getCatecode();
        String name = vo.getCatename();
        String id = "";
        if (!StringUtil.isEmpty(vo.getPk_assetcategory())) {
            id = vo.getPk_assetcategory();
        }
        sp1.addParam(code);
        sp1.addParam(name);
        if (!StringUtil.isEmpty(id)) {
            sp1.addParam(id);
            sp1.addParam(id);
        }
        String condition1 = " (catecode = ? or catename = ?) ";
        if (StringUtil.isEmpty(id) == false)
            condition1 = condition1 + " and (pk_assetcategory<? or pk_assetcategory>?) ";
        condition1 = condition1 + "  and pk_corp = '" + pk_corp + "' and nvl(dr,0) = 0 ";
        List<BdAssetCategoryVO> vo1 = (List<BdAssetCategoryVO>) singleObjectBO.retrieveByClause(BdAssetCategoryVO.class, condition1, sp1);

        int len = vo1 == null ? 0 : vo1.size();
        if (len > 0) {
            String str = vo1.get(0).getCatecode();
            if (code.equals(str))
                throw new BusinessException("资产类别编号已存在");
            else throw new BusinessException("资产类别名称已存在");
        } else return null;
    }


    @Override
    public BdAssetCategoryVO[] queryAssetCategoryRef(String pk_corp) throws DZFWarpException {
        BdAssetCategoryVO[] list = queryParent(pk_corp, getAccountSchema(pk_corp));

        if (list == null || list.length == 0)
            return new BdAssetCategoryVO[0];

        BdAssetCategoryVO vo = (BdAssetCategoryVO) BDTreeCreator.createTree(
                list, new AccTreeCreateStrategy(DZFConstant.ZCLBRULE) {
                    @Override
                    public SuperVO getRootVO() {
                        return new BdAssetCategoryVO();
                    }

                    @Override
                    public String getCodeValue(Object userObj) {
                        return ((BdAssetCategoryVO) userObj).getCatecode();
                    }
                });
        return vo.getChildren();
    }

    /**
     * 判断所使用的科目方案
     *
     * @return
     */
    public Integer getAccountSchema(String pk_corp) throws DZFWarpException {
        CorpVO corpVO = corpService.queryByPk(pk_corp);
        if (corpVO != null) {
            if (!StringUtil.isEmptyWithTrim(corpVO.getCorptype())) {
                BdtradeAccountSchemaVO schemaVO = (BdtradeAccountSchemaVO) singleObjectBO.queryVOByID(corpVO.getCorptype(), BdtradeAccountSchemaVO.class);
                if (schemaVO != null) {
                    return schemaVO.getAccountstandard();
                }
            }
        }
        return -1;
    }


    @Override
    public BdAssetCategoryVO queryAssetCategoryByPrimaryKey(BdAssetCategoryVO vo)
            throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_assetcategory());
        String condition = " pk_assetcategory = ? and nvl(dr,0) = 0 ";
        BdAssetCategoryVO[] rs = (BdAssetCategoryVO[]) singleObjectBO.queryByCondition(BdAssetCategoryVO.class, condition, sp);
        if (rs != null && rs.length > 0) {
            return rs[0];
        }
        return null;
    }

    private boolean checkIsUsed1(BdAssetCategoryVO vo) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_corp());
        sp.addParam(vo.getPk_assetcategory());
        String sql = " select count(1) from   ynt_assetcard where pk_corp = ? and nvl(dr,0) = 0 and ASSETCATEGORY = ? ";
        BigDecimal rowNum = (BigDecimal) singleObjectBO.executeQuery(sql, sp, new ColumnProcessor());
        if (rowNum.intValue() > 0) {
            return true;
        }
        return false;
    }

    private boolean checkIsUsed2(BdAssetCategoryVO vo) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_corp());
        sp.addParam(vo.getPk_assetcategory());
        String sql1 = " select count(1) from  ynt_tdcheck where pk_corp = ? and nvl(dr,0) = 0 and pk_assetcategory = ? ";
        BigDecimal rowNum1 = (BigDecimal) singleObjectBO.executeQuery(sql1, sp, new ColumnProcessor());
        if (rowNum1.intValue() > 0) {
            return true;
        }
        return false;
    }

    private boolean checkIsUsed3(BdAssetCategoryVO vo) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_corp());
        sp.addParam(vo.getPk_assetcategory());
        String sql2 = "select count(1) from   ynt_cpmb where pk_corp = ? and nvl(dr,0) = 0 and pk_assetcategory = ? ";
        BigDecimal rowNum2 = (BigDecimal) singleObjectBO.executeQuery(sql2, sp, new ColumnProcessor());
        if (rowNum2.intValue() > 0) {
            return true;
        }
        return false;
    }


    @Override
    public BdAssetCategoryVO queryByid(String pid) throws DZFWarpException {
        return (BdAssetCategoryVO) singleObjectBO.queryByPrimaryKey(BdAssetCategoryVO.class, pid);
    }

    private void putCorpData(String pk_corp, BdAssetCategoryVO[] vos) {
        //查询公司级的设置
        String sql = "select * from ynt_category_corp where nvl(dr,0)=0 and (pk_corp = ? or pk_corp = ?) order by pk_corp desc";

        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(IGlobalConstants.currency_corp);
        List<BdAssetCategoryVO> qrylist = (List<BdAssetCategoryVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(BdAssetCategoryVO.class));
        if (qrylist != null && qrylist.size() > 0) {
            //list转map
            Map<String, BdAssetCategoryVO> map = qrylist.stream().filter(v -> !StringUtil.isEmpty(v.getPk_assetcategory())).collect(HashMap::new, (m, v)-> {
                if(!(m.containsKey(v.getPk_assetcategory()) && IGlobalConstants.currency_corp.equals(v.getPk_corp()))){
                    m.put(v.getPk_assetcategory(), v);
                }
            },HashMap::putAll);

            BdAssetCategoryVO tvo = null;
            for (BdAssetCategoryVO vo : vos) {
                if (!StringUtil.isEmpty(vo.getPk_assetcategory())) {
                    if (map.containsKey(vo.getPk_assetcategory())) {
                        tvo = map.get(vo.getPk_assetcategory());
                        vo.setUselimit(tvo.getUselimit());
                        vo.setZjtype(tvo.getZjtype());
                        vo.setSalvageratio(tvo.getSalvageratio());
                        vo.setMemo(tvo.getMemo());
                    }
                }
            }
        }
    }

    /**
     * 公司个性化设置
     *
     * @param vo
     */
    private void saveCorpAssetType(BdAssetCategoryVO vo) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_corp());
        sp.addParam(vo.getPk_assetcategory());
        CorpAssetCategoryVO[] corptype = (CorpAssetCategoryVO[]) singleObjectBO.queryByCondition(CorpAssetCategoryVO.class, "nvl(dr,0)=0 and pk_corp = ? and pk_assetcategory =? ", sp);
        if (corptype != null && corptype.length > 0) {
            corptype[0].setMemo(vo.getMemo());
            corptype[0].setUselimit(vo.getUselimit());
            corptype[0].setZjtype(vo.getZjtype());
            corptype[0].setSalvageratio(vo.getSalvageratio());
            singleObjectBO.update(corptype[0]);
        } else {
            CorpAssetCategoryVO corpAssetType = new CorpAssetCategoryVO();
            corpAssetType.setMemo(vo.getMemo());
            corpAssetType.setUselimit(vo.getUselimit());
            corpAssetType.setZjtype(vo.getZjtype());
            corpAssetType.setSalvageratio(vo.getSalvageratio());
            corpAssetType.setPk_corp(vo.getPk_corp());
            corpAssetType.setPk_assetcategory(vo.getPk_assetcategory());
            singleObjectBO.saveObject(vo.getPk_corp(), corpAssetType);
        }
    }

}
