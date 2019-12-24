package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.utils.DZFStringUtil;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.enums.SalaryReportEnum;
import com.dzf.zxkj.common.enums.SalaryTypeEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.InventoryQcVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.jzcl.KMQMJZVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxinfoClassifyBVO;
import com.dzf.zxkj.platform.model.tax.TaxinfoClassifyVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccAliasService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.icset.IInventoryService;
import com.dzf.zxkj.platform.service.pjgl.IVATGoodsInvenRelaService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.AccountUtil;
import com.dzf.zxkj.platform.util.Kmschema;
import com.dzf.zxkj.platform.util.TaxClassifyGetValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
@Service("gl_fzhsserv")
@Slf4j
public class AuxiliaryAccountServiceImpl implements IAuxiliaryAccountService {
    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private ICorpService corpService;

    @Autowired
    private IParameterSetService parameterserv;
    @Autowired
    private IInventoryService iservice;
    @Autowired
    private IYntBoPubUtil yntBoPubUtil;
    @Autowired
    private IVATGoodsInvenRelaService goodsinvenservice;
    @Autowired
    private IInventoryAccAliasService gl_ic_invtoryaliasserv;
    @Autowired
    private IInventoryAccSetService gl_ic_invtorysetserv;

    @Override
    public AuxiliaryAccountHVO queryHByID(String pk_auacount_h) throws DZFWarpException {
        String condition = " pk_auacount_h = ? and nvl(dr,0) = 0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_auacount_h);
        // sp.addParam(pk_corp);
        AuxiliaryAccountHVO[] results = (AuxiliaryAccountHVO[]) singleObjectBO
                .queryByCondition(AuxiliaryAccountHVO.class, condition, sp);
        if (results != null && results.length > 0)
            return results[0];
        return null;
    }

    @Override
    public AuxiliaryAccountBVO queryBByID(String pk_auacount_b, String pk_corp) throws DZFWarpException {
        String condition = " pk_auacount_b = ? and pk_corp = ? and nvl(dr,0) = 0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_auacount_b);
        sp.addParam(pk_corp);
        AuxiliaryAccountBVO[] results = (AuxiliaryAccountBVO[]) singleObjectBO
                .queryByCondition(AuxiliaryAccountBVO.class, condition, sp);
        if (results != null && results.length > 0)
            return results[0];
        return null;
    }

    @Override
    public AuxiliaryAccountHVO[] queryH(String pk_corp) throws DZFWarpException {
        String condition = " pk_corp in(?,?) and nvl(dr,0) = 0  ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(IDefaultValue.DefaultGroup);
        AuxiliaryAccountHVO[] results = (AuxiliaryAccountHVO[]) singleObjectBO
                .queryByCondition(AuxiliaryAccountHVO.class, condition, sp);
        VOUtil.ascSort(results, new String[]{"code"});
        return results;
    }

    @Override
    public AuxiliaryAccountBVO[] queryByH(String pk_corp, String pk_auacount_h) throws DZFWarpException {
        return queryB(pk_auacount_h, pk_corp, null);
    }

    @Override
    public AuxiliaryAccountHVO[] queryHCustom(String pk_corp) throws DZFWarpException {
        // String condition = " (pk_corp = ? or pk_corp = ?) and nvl(dr,0) = 0
        // ";
        String condition = " pk_corp = ? and nvl(dr,0) = 0  ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        // sp.addParam(IDefaultValue.DefaultGroup);
        AuxiliaryAccountHVO[] results = (AuxiliaryAccountHVO[]) singleObjectBO
                .queryByCondition(AuxiliaryAccountHVO.class, condition, sp);
        VOUtil.ascSort(results, new String[]{"code"});
        return results;
    }


    /**
     * 根据辅助项目主键，查询辅助项目子表数据
     * [kmid]---主要是给填制凭证，辅助下拉用的。
     */
    @Override
    public AuxiliaryAccountBVO[] queryB(String pk_auacount_h, String pk_corp, String kmid) throws DZFWarpException {
        CorpVO corp = corpService.queryByPk(pk_corp);
        AuxiliaryAccountBVO[] results = null;
        if (AuxiliaryConstant.ITEM_INVENTORY.equals(pk_auacount_h)) {// 存货
            if (IcCostStyle.IC_ON.equals(corp.getBbuildic())) {// 启用库存,后台调用的时候。
                if (!StringUtil.isEmpty(kmid)) {
                    YntCpaccountVO accountVo = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class,
                            kmid);
                    if (accountVo == null || !"1403".equals(accountVo.getAccountcode())
                            && !"1405".equals(accountVo.getAccountcode())) {
                        kmid = null;
                    }
                }
                results = queryInventory(pk_corp, kmid);
            } else if (IcCostStyle.IC_INVTENTORY.equals(corp.getBbuildic())) {// 启用总账存货
                InventorySetVO invSet = gl_ic_invtorysetserv.query(pk_corp);
                if (!StringUtil.isEmpty(kmid) && invSet != null && invSet.getChcbjzfs() == InventoryConstant.IC_CHDLHS // 存货大类
                        && Kmschema.isKmclassify(pk_corp, corp.getCorptype(), kmid)) {// 按存货大类查询,填制凭证的，存货辅助按大类过滤
                    results = queryInventoryByKm(pk_auacount_h, kmid, pk_corp);
                } else {
                    results = queryAuxiliaryAccountBVO(pk_auacount_h, pk_corp);
                }
            } else {
                results = queryAuxiliaryAccountBVO(pk_auacount_h, pk_corp);
            }
        } else if (AuxiliaryConstant.ITEM_STAFF.equals(pk_auacount_h)) {// 职员
            List<AuxiliaryAccountBVO> list = queryPerson(pk_auacount_h, pk_corp, null);
            if (list != null && list.size() > 0) {
                results = list.toArray(new AuxiliaryAccountBVO[list.size()]);
            }
        } else {// 其他
            results = queryAuxiliaryAccountBVO(pk_auacount_h, pk_corp);
        }
        return results;
    }

    /**
     * 辅助核算界面，显示用,前台调用 ，后台禁止调用。分页显示
     */
    @Override
    public QueryPageVO queryBodysBypage(String pk_auacount_h, String pk_corp, String kmid, int page, int rows, String type)
            throws DZFWarpException {
        QueryPageVO pagevo = null;
        if (AuxiliaryConstant.ITEM_STAFF.equals(pk_auacount_h)) {// 职员
            pagevo = queryPersonBypage(pk_auacount_h, pk_corp, page, rows, type);
        } else {// 其他
            pagevo = queryAuxiliaryAccountBVOBypage(pk_auacount_h, pk_corp, page, rows);
        }
        return pagevo;
    }

    private AuxiliaryAccountBVO[] queryAuxiliaryAccountBVO(String pk_auacount_h, String pk_corp) {
        String condition = " pk_auacount_h = ? and pk_corp = ? and nvl(dr,0) = 0  ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_auacount_h);
        sp.addParam(pk_corp);
        AuxiliaryAccountBVO[] results = (AuxiliaryAccountBVO[]) singleObjectBO
                .queryByCondition(AuxiliaryAccountBVO.class, condition, sp);
        VOUtil.ascSort(results, new String[]{"code"});
        return results;
    }

    private QueryPageVO queryAuxiliaryAccountBVOBypage(String pk_auacount_h, String pk_corp, int page, int rows) {
        String condition = " pk_auacount_h = ? and pk_corp = ? and nvl(dr,0) = 0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_auacount_h);
        sp.addParam(pk_corp);
        // 查询总数
        int total = singleObjectBO.getTotalRow("ynt_fzhs_b", condition, sp);
        // 查询分页数据
        List<AuxiliaryAccountBVO> auxiliaryAccountBVOS = (List<AuxiliaryAccountBVO>) singleObjectBO
                .execQueryWithPage(AuxiliaryAccountBVO.class, "ynt_fzhs_b", condition, sp, page, rows, "order by code");

        AuxiliaryAccountBVO[] results = auxiliaryAccountBVOS
                .toArray(new AuxiliaryAccountBVO[auxiliaryAccountBVOS.size()]);
        QueryPageVO pagevo = new QueryPageVO();
        pagevo.setTotal(total);
        pagevo.setPage(page);
        pagevo.setPageofrows(rows);
        pagevo.setPagevos(results);
        return pagevo;
    }

    private QueryPageVO queryPersonBypage(String pk_auacount_h, String pk_corp, int page, int rows, String type) {
        String condition = " pk_auacount_h = ? and pk_corp = ? and nvl(dr,0) = 0 ";

        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_auacount_h);
        sp.addParam(pk_corp);
        if ("gz".equals(type)) {
            condition = condition + " and billtype <> ?";
            sp.addParam(SalaryTypeEnum.NONORMAL.getValue());
        }

        // 查询总数
        int total = singleObjectBO.getTotalRow("ynt_fzhs_b", condition, sp);

        // 根据查询条件查询公司的信息
        StringBuffer sf = new StringBuffer();
        sf.append(" select y.* ,fb.name vdeptname");
        sf.append(" From ynt_fzhs_b y ");
        sf.append(" left join ynt_fzhs_b fb ");
        sf.append(" on y.cdeptid = fb.pk_auacount_b and fb.pk_auacount_h = ?");
        sf.append(" where y.pk_corp = ? ");
        sf.append(" and nvl(y.dr, 0) = 0 ");
        sf.append(" and y.pk_auacount_h= ? ");
        sp.clearParams();
        sp.addParam(AuxiliaryConstant.ITEM_DEPARTMENT);// 部门
        sp.addParam(pk_corp);
        sp.addParam(pk_auacount_h);// 职员
        if ("gz".equals(type)) {
            sf.append(" and y.billtype <> ?");
            sp.addParam(SalaryTypeEnum.NONORMAL.getValue());
        }
        List<AuxiliaryAccountBVO> list = (List<AuxiliaryAccountBVO>) singleObjectBO.execQueryWithPage(
                AuxiliaryAccountBVO.class, "(" + sf.toString() + ")", null, sp, page, rows, "order by code");
        QueryPageVO pagevo = new QueryPageVO();
        pagevo.setTotal(total);
        pagevo.setPage(page);
        pagevo.setPageofrows(rows);
        pagevo.setPagevos(list.toArray(new AuxiliaryAccountBVO[0]));
        return pagevo;
    }

    public List<AuxiliaryAccountBVO> queryPerson(String pk_auacount_h, String pk_corp, String billtype) {
        // 根据查询条件查询公司的信息
        StringBuffer sf = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sf.append(" select y.* ,fb.name vdeptname");
        sf.append(" From ynt_fzhs_b y ");
        sf.append(" left join ynt_fzhs_b fb ");
        sf.append(" on y.cdeptid = fb.pk_auacount_b and fb.pk_auacount_h = ?");

        sf.append(" where y.pk_corp = ? ");
//		sf.append(" and nvl(y.sffc, 0) = 0 ");
        sf.append(" and nvl(y.dr, 0) = 0 ");
        sf.append(" and y.pk_auacount_h= ? ");
        sp.addParam(AuxiliaryConstant.ITEM_DEPARTMENT);// 部门
        sp.addParam(pk_corp);
        sp.addParam(pk_auacount_h);// 职员

        if (!StringUtil.isEmpty(billtype)) {
            sf.append(" and y.billtype= ? ");
            sp.addParam(billtype);// z
        }

        List<AuxiliaryAccountBVO> listVO = (List<AuxiliaryAccountBVO>) singleObjectBO.executeQuery(sf.toString(), sp,
                new BeanListProcessor(AuxiliaryAccountBVO.class));
        VOUtil.ascSort(listVO, new String[]{"code"});
        return listVO;

    }

    private AuxiliaryAccountBVO[] queryInventoryByKm(String pk_auacount_h, String kmid, String pk_corp) {
        String condition = " pk_auacount_h = ? and kmclassify = ? and pk_corp = ? and nvl(dr,0) = 0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_auacount_h);
        sp.addParam(kmid);
        sp.addParam(pk_corp);
        AuxiliaryAccountBVO[] results = (AuxiliaryAccountBVO[]) singleObjectBO
                .queryByCondition(AuxiliaryAccountBVO.class, condition, sp);
        VOUtil.ascSort(results, new String[]{"code"});
        return results;
    }

    private AuxiliaryAccountBVO[] queryInventory(String pk_corp, String kmid) {
        List<InventoryVO> list = null;

        if (StringUtil.isEmpty(kmid)) {
            list = iservice.queryInfo(pk_corp, null);
        } else {
            list = iservice.query(pk_corp, kmid);
        }

        if (list == null || list.isEmpty()) {
            return null;
        }
        List<AuxiliaryAccountBVO> alist = new ArrayList<AuxiliaryAccountBVO>();
        AuxiliaryAccountBVO bvo = null;
        for (InventoryVO invo : list) {
            bvo = new AuxiliaryAccountBVO();
            bvo.setCode(invo.getCode());
            bvo.setName(invo.getName());
            bvo.setPk_auacount_h("000001000000000000000006");
            bvo.setPk_auacount_b(invo.getPk_inventory());
            bvo.setUnit(invo.getMeasurename());
            bvo.setSpec(invo.getInvspec());// 规格
//			bvo.setInvtype(invo.getInvtype());// 型号
            bvo.setPk_corp(invo.getPk_corp());
            bvo.setTaxratio(invo.getTaxratio());// 税率
            bvo.setVmemo(invo.getMemo());// 备注
            bvo.setPk_accsubj(invo.getPk_subject());//科目
            bvo.setSubjname(invo.getKmname());//科目名称
            alist.add(bvo);
        }
        return alist.toArray(new AuxiliaryAccountBVO[alist.size()]);
    }

    @Override
    public AuxiliaryAccountHVO saveH(AuxiliaryAccountHVO hvo) throws DZFWarpException {
        AuxiliaryAccountHVO samevo = queryHByName(hvo.getPk_corp(), hvo.getName());
        if (samevo != null && !samevo.getPk_auacount_h().equals(hvo.getPk_auacount_h())) {
            throw new BusinessException("名称不能重复！");
        }
        if (StringUtil.isEmpty(hvo.getPk_auacount_h())) {
            hvo.setCode(getNewHCode(hvo));
        }
        return (AuxiliaryAccountHVO) singleObjectBO.saveObject(hvo.getPk_corp(), hvo);
    }


    @Override
    public AuxiliaryAccountBVO[] saveBs(List<AuxiliaryAccountBVO> list, boolean isadd) throws DZFWarpException {
        if (list == null || list.size() == 0)
            throw new BusinessException("数据不能为空！");

        HashSet<String> codeSet = new HashSet<String>();
        HashSet<String> taxpayerSet = new HashSet<String>();
        HashSet<String> nameZjbmSet = new HashSet<String>();
        HashSet<String> fcztSet = new HashSet<String>();
        AuxiliaryAccountBVO[] qbvos = queryB(list.get(0).getPk_auacount_h(), list.get(0).getPk_corp(), null);

        List<String> slist = new ArrayList<>();
        for (AuxiliaryAccountBVO nbvo : list) {
            if (!StringUtil.isEmpty(nbvo.getPk_auacount_b())) {
                slist.add(nbvo.getPk_auacount_b());
            }
        }
        if (qbvos != null && qbvos.length > 0) {
            for (AuxiliaryAccountBVO bvo : qbvos) {
                if (bvo.getSffc() != null && bvo.getSffc() == 1) {
                    fcztSet.add(bvo.getZjbm());
                }
                if (slist.contains(bvo.getPk_auacount_b())) {
                    continue;
                }
                codeSet.add(bvo.getCode());

                if (!StringUtil.isEmpty(bvo.getTaxpayer()))
                    taxpayerSet.add(bvo.getTaxpayer());
                String namezjbm = getCheckUniqueKey(list.get(0).getPk_auacount_h(), bvo);
                if (!StringUtil.isEmpty(namezjbm)) {
                    nameZjbmSet.add(namezjbm);
                }
            }
        }
        String code = yntBoPubUtil.getFZHsCode(list.get(0).getPk_corp(), list.get(0).getPk_auacount_h());
        Map<String, AuxiliaryAccountBVO> aumap = AccountUtil.getAuxiliaryAccountBVOByName(list.get(0).getPk_corp(),
                AuxiliaryConstant.ITEM_DEPARTMENT);
        StringBuffer msg = new StringBuffer();
        for (AuxiliaryAccountBVO nbvo : list) {
            boolean hasCode = true;
            if (StringUtil.isEmpty(nbvo.getPk_auacount_b()) && StringUtil.isEmpty(nbvo.getCode())) {
                hasCode = false;
                nbvo.setCode(code);
            }
            nbvo.setDr(0);
            String error = checkbeforeSave(list.get(0).getPk_auacount_h(), list.get(0).getPk_corp(), nbvo, codeSet,
                    taxpayerSet, nameZjbmSet, aumap, -1, fcztSet);

            if (StringUtil.isEmpty(error)) {
                // if (nbvo != null) {
                // list.add(nbvo);
                // }
            } else {
                msg.append("<font color = 'red'>" + error + "</font>");
            }
            if (StringUtil.isEmpty(nbvo.getPk_auacount_b())) {
                codeSet.add(nbvo.getCode());
                // 新生成code
                if (!hasCode) {
                    code = getFinalcode(code);
                }
            }
        }
        if (!StringUtil.isEmpty(msg.toString()))
            throw new BusinessException(msg.toString());

        if (isadd) {
            singleObjectBO.insertVOArr(list.get(0).getPk_corp(), list.toArray(new AuxiliaryAccountBVO[list.size()]));
        } else {
            singleObjectBO.updateAry(list.toArray(new AuxiliaryAccountBVO[list.size()]));
        }
        return queryB(list.get(0).getPk_auacount_h(), list.get(0).getPk_corp(), null);
    }

    private String getCheckUniqueKey(String pk_auacount_h, AuxiliaryAccountBVO bvo) {
        String namezjbm = null;
        if (AuxiliaryConstant.ITEM_STAFF.equals(pk_auacount_h)) {
            if (!SalaryTypeEnum.NONORMAL.getValue().equals(bvo.getBilltype())) {
                namezjbm = bvo.getZjbm();
            } else {
                namezjbm = bvo.getName();
            }
        } else if (AuxiliaryConstant.ITEM_INVENTORY.equals(pk_auacount_h)) {
            namezjbm = getNameInfoKey(bvo);
        } else {
            namezjbm = bvo.getName();
        }
        return namezjbm;
    }

    @Override
    public AuxiliaryAccountBVO saveB(AuxiliaryAccountBVO bvo1) throws DZFWarpException {

        if (bvo1 == null)
            throw new BusinessException("数据不能为空！");

        DZFStringUtil.removeBlank(bvo1, new String[]{"code", "name", "zjbm", "vphone"});
        HashSet<String> codeSet = new HashSet<String>();
        HashSet<String> taxpayerSet = new HashSet<String>();
        HashSet<String> nameZjbmSet = new HashSet<String>();
        HashSet<String> fcztSet = new HashSet<String>();
        AuxiliaryAccountBVO[] qbvos = queryB(bvo1.getPk_auacount_h(), bvo1.getPk_corp(), null);


        if (qbvos != null && qbvos.length > 0) {
            for (AuxiliaryAccountBVO bvo : qbvos) {

                if (bvo.getSffc() != null && bvo.getSffc() == 1) {
                    fcztSet.add(bvo.getZjbm());
                }
                if (!StringUtil.isEmpty(bvo1.getPk_auacount_b())
                        && bvo1.getPk_auacount_b().equals(bvo.getPk_auacount_b())) {
                    continue;
                }
                codeSet.add(bvo.getCode());

                if (!StringUtil.isEmpty(bvo.getTaxpayer()))
                    taxpayerSet.add(bvo.getTaxpayer());

                String namezjbm = getCheckUniqueKey(bvo1.getPk_auacount_h(), bvo);

                if (!StringUtil.isEmpty(namezjbm)) {
                    nameZjbmSet.add(namezjbm);
                }
            }
        }

        Map<String, AuxiliaryAccountBVO> aumap = AccountUtil.getAuxiliaryAccountBVOByName(bvo1.getPk_corp(),
                AuxiliaryConstant.ITEM_DEPARTMENT);
        String error = checkbeforeSave(bvo1.getPk_auacount_h(), bvo1.getPk_corp(), bvo1, codeSet, taxpayerSet,
                nameZjbmSet, aumap, -1, fcztSet);
        if (!StringUtil.isEmpty(error))
            throw new BusinessException(error);
        bvo1.setDr(0);
        AuxiliaryAccountBVO qvo = (AuxiliaryAccountBVO) singleObjectBO.saveObject(bvo1.getPk_corp(), bvo1);
        return qvo;
    }

    private String getNameInfoKey(AuxiliaryAccountBVO invo) {
        StringBuffer strb = new StringBuffer();
        strb.append(appendIsNull(invo.getName()));
        strb.append(appendIsNull(invo.getSpec()));
        strb.append(appendIsNull(invo.getInvtype()));
        strb.append(appendIsNull(invo.getUnit()));
        return strb.toString();

    }

    private String appendIsNull(String info) {
        StringBuffer strb = new StringBuffer();
        if (StringUtil.isEmpty(info)) {
            strb.append("null");
        } else {
            strb.append(info);
        }
        return strb.toString();
    }

    private void checkFieldLength(AuxiliaryAccountBVO bvo, StringBuffer message, int row) {

        if (!StringUtil.isEmpty(bvo.getCode())) {
            if (bvo.getCode().length() > 20) {
                dealMessage(message, row, "编号为：" + bvo.getCode() + "的编码长度超出限制！");
            }
        }
        if (!StringUtil.isEmpty(bvo.getName())) {
            if (bvo.getName().length() > 1000) {
                dealMessage(message, row, "名称为：" + bvo.getName() + "的名称长度超出限制！");
            }
        }
        if (!StringUtil.isEmpty(bvo.getAddress())) {
            if (bvo.getAddress().length() > 500) {
                dealMessage(message, row, "地址为：" + bvo.getAddress() + "的地址长度超出限制！");
            }
        }
        if (!StringUtil.isEmpty(bvo.getPhone_num())) {
            if (bvo.getPhone_num().length() > 500) {
                dealMessage(message, row, "电话为：" + bvo.getPhone_num() + "的电话长度超出限制！");
            }
        }
        if (!StringUtil.isEmpty(bvo.getBank())) {
            if (bvo.getBank().length() > 500) {
                dealMessage(message, row, "开户行为：" + bvo.getBank() + "的开户行长度超出限制！");
            }
        }

        if (!StringUtil.isEmpty(bvo.getAccount_num())) {
            if (bvo.getAccount_num().length() > 330) {
                dealMessage(message, row, "账号为：" + bvo.getAccount_num() + "的账号长度超出限制！");
            }
        }

        if (!StringUtil.isEmpty(bvo.getVphone())) {
            if (bvo.getVphone().length() > 30) {
                dealMessage(message, row, "手机号为：" + bvo.getVphone() + "的手机号长度超出限制！");
            }
        }

        if (!StringUtil.isEmpty(bvo.getZjbm())) {
            if (bvo.getZjbm().length() > 130) {
                dealMessage(message, row, "证件编号为：" + bvo.getZjbm() + "的证件编号长度超出限制！");
            }
        }

        if (!StringUtil.isEmpty(bvo.getVmemo())) {
            if (bvo.getVmemo().length() > 530) {
                dealMessage(message, row, "备注为：" + bvo.getZjbm() + "的备注长度超出限制！");
            }
        }
    }

    private void checkIsNotNull(AuxiliaryAccountBVO bvo, StringBuffer message, int row) {
        // “正常薪金”必输项：员工编码，员工姓名，证件类型、证件编码、手机号码
        if (StringUtil.isEmpty(bvo.getCode())) {
            dealMessage(message, row, "编码不能为空！");
        }

        if (StringUtil.isEmpty(bvo.getName())) {
            dealMessage(message, row, "名称不能为空！");
        }

        if (StringUtil.isEmpty(bvo.getZjlx())) {
            dealMessage(message, row, "证件类型不能为空！");
        }
        if (StringUtil.isEmpty(bvo.getZjbm())) {
            dealMessage(message, row, "证件编码不能为空！");
        }
        if (bvo.getEmployedate() == null) {
            dealMessage(message, row, "任职受雇从业日期不能为空！");
        }
        if (bvo.getIsimp() == null || !bvo.getIsimp().booleanValue()) {
            if (StringUtil.isEmpty(bvo.getVphone())) {
                dealMessage(message, row, "手机号不能为空！");
            }
        }

    }

    @Override
    public void delete(AuxiliaryAccountHVO hvo) throws BusinessException {
        checkHRef(hvo);
        checkBExsist(hvo.getPk_auacount_h());
        singleObjectBO.deleteObject(hvo);
    }

    @Override
    public void delete(AuxiliaryAccountBVO bvo) throws BusinessException {
        checkBRef(bvo, 1);
        singleObjectBO.deleteObject(bvo);
        // if (!StringUtil.isEmpty(bvo.getPk_corp()))
        // AuAccountCache.getInstance().remove(bvo.getPk_corp());
    }

    @Override
    public void checkBRef(AuxiliaryAccountBVO bvo, int type) throws DZFWarpException {
        AuxiliaryAccountHVO hvo = queryHByID(bvo.getPk_auacount_h());
        List<AuxiliaryAccountBVO> vos = new ArrayList<AuxiliaryAccountBVO>();
        vos.add(bvo);
        Map<String, String> rs = filterUsedItem(vos, hvo);

        if (rs.size() > 0) {
            String tips = type == 0 ? "编码不可修改！" : "不可删除！";
            for (String msg : rs.keySet()) {
                throw new BusinessException("此辅助核算项目已经" + msg + "，" + tips);
            }
        }
    }

//	/**
//	 * 会计工厂是否引用
//	 * 
//	 * @param bvo
//	 * @param pcode
//	 * @param tips
//	 * @throws DZFWarpException
//	 */
//	private void checkFctRef(AuxiliaryAccountBVO bvo, int pcode, String tips) throws DZFWarpException {
//		StringBuilder pzSQL = new StringBuilder();
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(bvo.getPk_corp());
//		sp.addParam(bvo.getPk_auacount_b());
//		pzSQL.append("select count(1) from fct_ynt_tzpz_b where pk_corp =? and nvl(dr,0) = 0 ");
//		pzSQL.append(" and fzhsx");
//		pzSQL.append(pcode);
//		pzSQL.append(" = ?");
//		BigDecimal pzCount = (BigDecimal) singleObjectBO.executeQuery(pzSQL.toString(), sp, new ColumnProcessor());
//
//		if (pzCount.intValue() > 0) {
//			throw new BusinessException("此辅助核算项目已经被会计工厂凭证引用，" + tips);
//		}
//	}

    @Override
    public AuxiliaryAccountHVO queryHByName(String pk_corp, String name) throws DZFWarpException {
        String condition = " (pk_corp = ? or pk_corp = ?) and name = ? and nvl(dr,0) = 0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(IDefaultValue.DefaultGroup);
        sp.addParam(name);
        AuxiliaryAccountHVO[] results = (AuxiliaryAccountHVO[]) singleObjectBO
                .queryByCondition(AuxiliaryAccountHVO.class, condition, sp);
        if (results != null && results.length > 0) {
            return results[0];
        }
        return null;
    }

    @Override
    public void onSeal(String id) throws DZFWarpException {
        AuxiliaryAccountBVO auxiliaryAccountBVO = new AuxiliaryAccountBVO();
        auxiliaryAccountBVO.setPk_auacount_b(id);
        auxiliaryAccountBVO.setSffc(AuxiliaryConstant.SEAL);
        singleObjectBO.update(auxiliaryAccountBVO, new String[]{"sffc"});
    }

    @Override
    public void unSeal(String id) throws DZFWarpException {
        AuxiliaryAccountBVO auxiliaryAccountBVO = new AuxiliaryAccountBVO();
        auxiliaryAccountBVO.setPk_auacount_b(id);
        auxiliaryAccountBVO.setSffc(AuxiliaryConstant.UNSEAL);
        singleObjectBO.update(auxiliaryAccountBVO, new String[]{"sffc"});
    }

    @Override
    public int updateBatchAuxiliaryAccountByID(AuxiliaryAccountBVO[] auxiliaryAccountBVOS, String[] modifyFiled) throws DZFWarpException {
        return singleObjectBO.updateAry(auxiliaryAccountBVOS, modifyFiled);
    }

    @Override
    public AuxiliaryAccountBVO queryBByName(String pk_corp, String name, String hid) throws DZFWarpException {
        String condition = " pk_auacount_h = ? and name = ? and pk_corp = ? and nvl(dr,0) = 0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(hid);
        sp.addParam(name);
        sp.addParam(pk_corp);
        AuxiliaryAccountBVO[] results = (AuxiliaryAccountBVO[]) singleObjectBO
                .queryByCondition(AuxiliaryAccountBVO.class, condition, sp);
        VOUtil.ascSort(results, new String[]{"code"});
        if (results != null && results.length > 0) {
            return results[0];
        }
        return null;
    }

    public AuxiliaryAccountBVO queryBByCode(String pk_corp, String code, String hid) throws DZFWarpException {
        String condition = " pk_auacount_h = ? and code = ? and pk_corp = ? and nvl(dr,0) = 0  ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(hid);
        sp.addParam(code);
        sp.addParam(pk_corp);
        AuxiliaryAccountBVO[] results = (AuxiliaryAccountBVO[]) singleObjectBO
                .queryByCondition(AuxiliaryAccountBVO.class, condition, sp);
        VOUtil.ascSort(results, new String[]{"code"});
        if (results != null && results.length > 0) {
            return results[0];
        }
        return null;
    }

    // @Override
    // public AuxiliaryAccountBVO[] queryBParam(String pk_auacount_h, String
    // param, String pk_corp)
    // throws DZFWarpException {
    // AuxiliaryAccountBVO[] results = null;
    // if (AuxiliaryConstant.ITEM_STAFF.equals(pk_auacount_h)) {
    // List<AuxiliaryAccountBVO> list = queryPersonByParam(pk_auacount_h, param,
    // pk_corp);
    // if (list != null && list.size() > 0) {
    // results = list.toArray(new AuxiliaryAccountBVO[list.size()]);
    // }
    // } else {
    // SQLParameter sp = new SQLParameter();
    // String condition = " pk_auacount_h = ? and pk_corp = ? and nvl(dr,0) = 0
    // ";
    // sp.addParam(pk_auacount_h);
    // sp.addParam(pk_corp);
    // if (!StringUtil.isEmpty(param)) {
    // condition += " and (code like ? or name like ?)";
    // param = "%" + param + "%";
    // sp.addParam(param);
    // sp.addParam(param);
    // }
    // condition += " order by code ";
    // results = (AuxiliaryAccountBVO[])
    // singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class, condition,
    // sp);
    // }
    //
    // return results;
    // }

    // private List<AuxiliaryAccountBVO> queryPersonByParam(String
    // pk_auacount_h, String param, String pk_corp) {
    // // 根据查询条件查询公司的信息
    // StringBuffer sf = new StringBuffer();
    // SQLParameter sp = new SQLParameter();
    // sf.append(" select y.* ,fb.name vdeptname");
    // sf.append(" From ynt_fzhs_b y ");
    // sf.append(" left join ynt_fzhs_b fb ");
    // sf.append(" on y.cdeptid = fb.pk_auacount_b and fb.pk_auacount_h = ?");
    //
    // sf.append(" where y.pk_corp = ? ");
    // sf.append(" and nvl(y.dr, 0) = 0 ");
    // sf.append(" and y.pk_auacount_h= ? ");
    // sp.addParam(AuxiliaryConstant.ITEM_DEPARTMENT);// 部门
    // sp.addParam(pk_corp);
    // sp.addParam(pk_auacount_h);// 职员
    //
    // if (!StringUtil.isEmpty(param)) {
    // sf.append(" and (y.code like ? or y.name like ?)");
    // param = "%" + param + "%";
    // sp.addParam(param);
    // sp.addParam(param);
    // }
    // sf.append("order by y.code ");
    //
    // List<AuxiliaryAccountBVO> listVO = (List<AuxiliaryAccountBVO>)
    // singleObjectBO.executeQuery(sf.toString(), sp,
    // new BeanListProcessor(AuxiliaryAccountBVO.class));
    // return listVO;
    //
    // }

    @SuppressWarnings("unchecked")
    private int getNewHCode(AuxiliaryAccountHVO vo) {
        String pk_corp = vo.getPk_corp();
        StringBuffer sf = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sf.append("select code from ynt_fzhs_h where pk_corp = ? and nvl(dr,0) = 0 ");
        List<Integer> codes = (List<Integer>) singleObjectBO.executeQuery(sf.toString(), sp, new ResultSetProcessor() {
            public Object handleResultSet(ResultSet rs) throws SQLException {
                List<Integer> result = new ArrayList<Integer>();
                while (rs.next()) {
                    result.add(Integer.valueOf(rs.getString("code")));
                }
                return result;
            }
        });
        if (codes.size() >= 4) {
            throw new BusinessException("最多只允许添加4个辅助核算类别！");
        }
        return codes.size() == 0 ? 7 : getMinDiscontinue(codes);
    }

    // 获取最小不连续值
    private int getMinDiscontinue(List<Integer> vals) {
        // 从7,8,9,10四个值中取合适的值
        boolean flag = false;
        int inx = 7;
        for (; inx < 11; inx++) {
            flag = false;
            for (int i = 0; i < vals.size(); i++) {
                if (inx == vals.get(i).intValue()) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                break;
            }
        }
        return inx;
    }

    /**
     * 校验辅助核算类别是否被科目引用
     *
     * @param pk_auacount_h
     */
    private void checkHRef(AuxiliaryAccountHVO hvo) {
        StringBuilder kmfzhs = new StringBuilder("__________");
        Integer code = hvo.getCode();
        kmfzhs.setCharAt(code.intValue() - 1, '1');
        StringBuffer sf = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sp.addParam(hvo.getPk_corp());
        sp.addParam(kmfzhs.toString());
        sf.append("select count(1) from ynt_cpaccount where pk_corp =? and nvl(dr,0) = 0 ");
        sf.append(" and isfzhs like ? ");
        BigDecimal refCount = (BigDecimal) singleObjectBO.executeQuery(sf.toString(), sp, new ColumnProcessor());
        if (refCount.intValue() > 0) {
            throw new BusinessException("该辅助核算已被科目引用，不能删除！");
        }

    }

    private void checkBExsist(String pk_auacount_h) {
        SQLParameter sqlpa = new SQLParameter();
        sqlpa.addParam(pk_auacount_h);
        String querysql = "select count(1) from ynt_fzhs_b where pk_auacount_h = ?  and nvl(dr,0) = 0 ";
        BigDecimal res = (BigDecimal) singleObjectBO.executeQuery(querysql, sqlpa, new ColumnProcessor());
        if (res != null && res.intValue() > 0)
            throw new BusinessException("删除失败！待删除的项目含有明细档案，无法删除，请先删除明细档案!");
    }

    @Override
    public Map<String, String> saveBImp(InputStream is, String pk_auacount_h, String pk_corp, String fileType)
            throws DZFWarpException {
        Map<String, String> importResult = new HashMap<String, String>();
        try {

            StringBuffer msg = new StringBuffer();
            HashSet<String> codeSet = new HashSet<String>();
            HashSet<String> taxpayerSet = new HashSet<String>();
            HashSet<String> nameZjbmSet = new HashSet<String>();
            HashSet<String> fcztSet = new HashSet();
            AuxiliaryAccountBVO[] qbvos = queryB(pk_auacount_h, pk_corp, null);

            if (qbvos != null && qbvos.length > 0) {
                for (AuxiliaryAccountBVO bvo : qbvos) {

                    if (bvo.getSffc() != null && bvo.getSffc() == 1) {
                        fcztSet.add(bvo.getZjbm());
                    }

                    codeSet.add(bvo.getCode());

                    if (!StringUtil.isEmpty(bvo.getTaxpayer()))
                        taxpayerSet.add(bvo.getTaxpayer());

                    String namezjbm = getCheckUniqueKey(pk_auacount_h, bvo);
//					if (AuxiliaryConstant.ITEM_STAFF.equals(pk_auacount_h)) {
//						namezjbm = bvo.getZjbm();
//					} else if (AuxiliaryConstant.ITEM_INVENTORY.equals(pk_auacount_h)) {
//						namezjbm = getNameInfoKey(bvo);
//					} else {
//						namezjbm = bvo.getName();
//					}
                    if (!StringUtil.isEmpty(namezjbm)) {
                        nameZjbmSet.add(namezjbm);
                    }
                }
            }

            Map<String, AuxiliaryAccountBVO> aumap = AccountUtil.getAuxiliaryAccountBVOByName(pk_corp,
                    AuxiliaryConstant.ITEM_DEPARTMENT);
            Map<String, YntCpaccountVO> accmap = null;
            if (AuxiliaryConstant.ITEM_INVENTORY.equals(pk_auacount_h)) {
                YntCpaccountVO[] accvos = accountService.queryByPk(pk_corp);
                accmap = AccountUtil.getAccVOByCodeName(pk_corp, accvos, "_");
            }

            AuxiliaryAccountBVO bvo = null;
            Workbook impBook = null;
            if ("xls".equals(fileType)) {
                impBook = new HSSFWorkbook(is);
            } else if ("xlsx".equals(fileType)) {
                impBook = new XSSFWorkbook(is);
            } else {
                throw new BusinessException("不支持的文件格式");
            }
            List<AuxiliaryAccountBVO> list = new ArrayList<>();
            Sheet sheet1 = impBook.getSheetAt(0);
            Map<Integer, String> STYLE_1 = getColumnMap(sheet1, pk_auacount_h);
            Cell aCell = null;
            String sTmp = "";
            int iBegin = 1;
            int failCount = 0;
            String code = yntBoPubUtil.getFZHsCode(pk_corp, pk_auacount_h);
            for (; iBegin < (sheet1.getLastRowNum() + 1); iBegin++) {
                Row row = sheet1.getRow(iBegin);
                if (row == null || isRowEmpty(row))
                    continue;
                bvo = new AuxiliaryAccountBVO();
                bvo.setPk_auacount_h(pk_auacount_h);
                bvo.setPk_corp(pk_corp);
                bvo.setCode(code);
                bvo.setDr(0);
                for (Integer key : STYLE_1.keySet()) {
                    String column = STYLE_1.get(key);
                    aCell = row.getCell(key.intValue());
                    if (aCell == null)
                        continue;
                    if ("code".equals(column))// 编码以系统为准.
                        continue;
                    if ("zjlx".equals(column)) {
                        aCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        aCell = sheet1.getRow(iBegin).getCell(key.intValue());
                        sTmp = (String) aCell.getStringCellValue();
                    } else if ("isex".equals(column)) {
                        aCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        aCell = sheet1.getRow(iBegin).getCell(key.intValue());
                        sTmp = (String) aCell.getStringCellValue();
                        if ("女".equals(sTmp)) {
                            sTmp = "2";
                        } else if ("男".equals(sTmp)) {
                            sTmp = "1";
                        }
                    } else if ("birthdate".equals(column) || "employedate".equals(column) || "entrydate".equals(column)
                            || "leavedate".equals(column)) {
                        if (aCell != null && aCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
                            // 判断是否为日期类型
                            aCell = sheet1.getRow(iBegin).getCell(key.intValue());
                            sTmp = (String) aCell.getStringCellValue();
                            sTmp = getDateData(sTmp);
                        } else if (aCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                            aCell = sheet1.getRow(iBegin).getCell(key.intValue());
                            if (HSSFDateUtil.isCellDateFormatted(aCell)) {
                                // 用于转化为日期格式
                                Date date = aCell.getDateCellValue();
                                DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                                sTmp = formater.format(date);
                            } else {
                                aCell = sheet1.getRow(iBegin).getCell(key.intValue());
                                // 用于格式化数字，只保留数字的整数部分
                                DecimalFormat df = new DecimalFormat("########");
                                sTmp = df.format(aCell.getNumericCellValue());
                            }
                        }

                    } else {
                        aCell = sheet1.getRow(iBegin).getCell(key.intValue());
                        sTmp = getExcelCellValue(aCell, column);
                    }
                    if (!StringUtil.isEmpty(sTmp)) {
                        sTmp = sTmp.replaceAll("^( | )+|( | )+$", "");
                        bvo.setAttributeValue(column, sTmp);
                    }
                    sTmp = null;
                }
                convertSpecialValue(pk_auacount_h, pk_corp, bvo, accmap);

                String error = checkbeforeSave(pk_auacount_h, pk_corp, bvo, codeSet, taxpayerSet, nameZjbmSet, aumap,
                        iBegin, fcztSet);

                if (StringUtil.isEmpty(error)) {
                    if (bvo != null) {
                        list.add(bvo);
                    }
                } else {
                    msg.append("<font color = 'red'>" + error + "</font>");
                    failCount++;
                }
                // 新生成code
                code = getFinalcode(code);

                setDefault(bvo);
            }
            if (list != null && list.size() > 0) {
                singleObjectBO.insertVOArr(pk_corp, list.toArray(new AuxiliaryAccountBVO[list.size()]));
                msg.append("导入成功 ").append(list.size()).append(" 条记录<br> ");
            }
            if (failCount > 0) {
                msg.append("<font color = 'red'>导入失败 ").append(failCount).append(" 条记录</font>");
            }
            importResult.put("msg", msg.toString());
            importResult.put("failCount", failCount + "");
            importResult.put("successCount", list.size() + "");
            return importResult;
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            throw new BusinessException("未知异常");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void setDefault(AuxiliaryAccountBVO bvo) {

        String hid = bvo.getPk_auacount_h();
        if (AuxiliaryConstant.ITEM_STAFF.equals(hid)) {

            String zjlx = bvo.getZjlx();
            String zjbm = bvo.getZjbm();

            if (StringUtil.isEmpty(zjlx))
                return;

            int len = 0;
            if (!StringUtil.isEmpty(zjbm)) {
                len = zjbm.length();
            }

            if ("身份证".equals(zjlx) && len == 18) {
                bvo.setIsex(Integer.parseInt(IdCard(zjbm, 2)));
                try {
                    bvo.setBirthdate(new DZFDate(IdCard(zjbm, 1)));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

            if ("身份证".equals(zjlx) || "护照".equals(zjlx)) {
                bvo.setVarea(SalaryReportEnum.CHINACARD.getArea());
                bvo.setVbirtharea(SalaryReportEnum.CHINACARD.getArea());
            } else {
                if ("港澳居民居住证".equals(zjlx) || "港澳居民来往内地通行证".equals(zjlx)) {
                    bvo.setVarea(SalaryReportEnum.GACARD.getArea());
                    bvo.setVbirtharea(SalaryReportEnum.GACARD.getArea());
                } else if ("台湾居民来往大陆通行证".equals(zjlx) || "台湾身份证".equals(zjlx)) {
                    bvo.setVarea(SalaryReportEnum.TAICARD.getArea());
                    bvo.setVbirtharea(SalaryReportEnum.TAICARD.getArea());
                } else {
                }
            }
        }
    }

    private String IdCard(String UUserCard, int num) {
        String temp = null;
        if (num == 1) {
            // 获取出生日期
            temp = UUserCard.substring(6, 10) + "-" + UUserCard.substring(10, 12) + "-" + UUserCard.substring(12, 14);
        }
        if (num == 2) {
            // 获取性别
            if (Integer.parseInt(UUserCard.substring(16, 17)) % 2 == 1) {
                // 男
                temp = "1";
            } else if (Integer.parseInt(UUserCard.substring(16, 17)) % 2 == 0) {
                // 女
                temp = "2";
            } else {
                temp = "0";
            }
        }
        if (num == 3) {
            // 获取年龄
            DZFDate myDate = new DZFDate();
            int month = myDate.getMonth() + 1;
            int day = myDate.getDay();
            int age = myDate.getYear() - Integer.parseInt(UUserCard.substring(6, 10)) - 1;
            if (Integer.parseInt(UUserCard.substring(10, 12)) < month
                    || Integer.parseInt(UUserCard.substring(10, 12)) == month
                    && Integer.parseInt(UUserCard.substring(12, 14)) <= day) {
                age++;
            }
            temp = Integer.toString(age);
        }
        return temp;
    }

    private String getDateData(String sdate) {
        try {
            if (StringUtil.isEmpty(sdate)) {
                return null;
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Date date = null;
            StringTokenizer st = new StringTokenizer(sdate, "-/.");
            if (st.countTokens() == 3) {
                DZFDate ddate = new DZFDate(sdate);
                date = ddate.toDate();
            } else {
                date = formatter.parse(sdate);
            }
            String dateString = formatter.format(date);
            return dateString;
        } catch (Exception e) {
            throw new BusinessException("导入数据" + sdate + "格式不正确，应为日期格式，例如:" + new DZFDate().toString());
        } finally {

        }
    }

    private void convertSpecialValue(String pk_auacount_h, String pk_corp, AuxiliaryAccountBVO bvo,
                                     Map<String, YntCpaccountVO> accmap) {

        if (AuxiliaryConstant.ITEM_INVENTORY.equals(pk_auacount_h) && accmap != null && accmap.size() != 0) {
            String classsify = bvo.getKmclassifyname();
            String chukuname = bvo.getChukukmname();
            YntCpaccountVO cpavo;
            if (!StringUtil.isEmpty(classsify) && accmap.containsKey(classsify)) {
                cpavo = accmap.get(classsify);
                bvo.setKmclassify(cpavo.getPk_corp_account());
            }

            if (!StringUtil.isEmpty(chukuname) && accmap.containsKey(chukuname)) {
                cpavo = accmap.get(chukuname);
                bvo.setChukukmid(cpavo.getPk_corp_account());
            }
        }
    }

    private String getFinalcode(String code) {
        Long result = 1l;
        try {
            result = Long.parseLong(code.trim()) + 1;
        } catch (Exception e) {
            // 吃掉异常
        }
        String str = "";
        if (result > 0 && result < 10) {
            str = "00" + String.valueOf(result);
        } else if (result > 9 && result < 100) {
            str = "0" + String.valueOf(result);
        } else {
            str = String.valueOf(result);
        }
        return str;
    }

    private String getExcelCellValue(Cell cell, String columnvalue) {
        String ret = "";
        try {
            Field field = AuxiliaryAccountBVO.class.getDeclaredField(columnvalue);
            if (cell == null) {
                ret = null;
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {// 格式化日期字符串
                ret = cell.getRichStringCellValue().getString();
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                if (field.getType().toString().endsWith("String")) {
                    ret = new DecimalFormat("#").format(cell.getNumericCellValue()).toString();
                } else if (field.getType().toString().endsWith("class com.dzf.pub.lang.DZFDate")) {
                    ret = new DZFDate(cell.getDateCellValue()).toString();
                } else {
                    ret = "" + Double.valueOf(cell.getNumericCellValue()).doubleValue();
                }
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
                ret = cell.getCellFormula();
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
                ret = "" + cell.getErrorCellValue();
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
                ret = "" + cell.getBooleanCellValue();
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
                ret = null;
            }

        } catch (Exception ex) {
            ret = null;
        }
        return ret;
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
                return false;
        }
        return true;
    }

    private Map<Integer, String> getColumnMap(Sheet sheets1, String hid) {

        Map<String, Integer> map1 = getCommonGzTableHeadTiTle(sheets1, 0);

        Map<String, String> jsimp = getTypeImportMap(hid);
        Map<Integer, String> map = getColumnMap(map1, jsimp);

        if (map == null || map.size() == 0) {
            throw new BusinessException("导入文件格式不正确，请下载模板后重新导入！");
        } else if (map.size() == jsimp.size()) {
            return map;
        } else {
            throw new BusinessException("导入文件格式不正确，请下载模板后重新导入！");
        }
    }

    private Map<String, String> getTypeImportMap(String hid) {

        String[] TYDZFIMP = null;

        String[] CODESTYIMP = null;
        if (AuxiliaryConstant.ITEM_CUSTOMER.equals(hid) || AuxiliaryConstant.ITEM_SUPPLIER.equals(hid)) {
            TYDZFIMP = new String[]{"编码", "名称", "统一社会信用代码", "地址", "电话", "开户行", "账户"}; // "纳税人识别号",
            // gzx
            CODESTYIMP = new String[]{"code", "name", "credit_code", "address", "phone_num", "bank", // "taxpayer",
                    "account_num"};

        } else if (AuxiliaryConstant.ITEM_STAFF.equals(hid)) {
            TYDZFIMP = new String[]{"工号", "姓名", "证照类型", "证照号码", "手机号码", "员工类别", "国籍", "性别", "出生日期", "任职受雇日期", "出生国家",
                    "首次入境时间", "预计离境时间", "部门名称", "备注"};
            CODESTYIMP = new String[]{"code", "name", "zjlx", "zjbm", "vphone", "billtype", "varea", "isex",
                    "birthdate", "employedate", "vbirtharea", "entrydate", "leavedate", "vdeptname", "vmemo"};
        } else if (AuxiliaryConstant.ITEM_INVENTORY.equals(hid)) {
            TYDZFIMP = new String[]{"编码", "名称", "存货类别", "出库科目", "规格(型号)", "结算单价", "计量单位"};
            CODESTYIMP = new String[]{"code", "name", "kmclassifyname", "chukukmname", "spec", "jsprice", "unit"};
        } else {
            TYDZFIMP = new String[]{"编码", "名称"};
            CODESTYIMP = new String[]{"code", "name"};
        }
        Map<String, String> map = getMapColumn(null, TYDZFIMP, CODESTYIMP);
        return map;
    }

    private Map<String, String> getMapColumn(List<Integer> hiddenColList, String[] names, String[] codes) {

        int len = names.length;
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (int i = 0; i < len; i++) {
            if (hiddenColList != null && hiddenColList.size() > 0 && hiddenColList.contains(i)) {
                continue;
            }
            if (!map.containsKey(names[i])) {
                map.put(names[i], codes[i]);
            }
        }
        return map;
    }

    private Map<Integer, String> getColumnMap(Map<String, Integer> map1, Map<String, String> jsimp) {

        Map<Integer, String> map = new HashMap<>();

        for (Map.Entry<String, Integer> entry : map1.entrySet()) {
            String key = entry.getKey().toString();
            if (!StringUtil.isEmpty(jsimp.get(key))) {
                Integer value = entry.getValue();
                map.put(value, jsimp.get(key));
            }
        }

        return map;

    }

    protected Map<String, Integer> getTableHeadTiTle(Sheet sheets1, int iBegin, int count) {

        Map<String, Integer> map = new HashMap<>();
        Cell cell = null;
        for (int i = 0; i < count; i++) {
            if (sheets1.getRow(iBegin) != null) {
                cell = sheets1.getRow(iBegin).getCell(i);
                if (cell != null) {
                    if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
                        String ret = cell.getRichStringCellValue().getString();
                        if (!StringUtil.isEmpty(ret))
                            map.put(ret, i);

                    }
                }
            }
        }
        return map;
    }

    protected Map<String, Integer> getCommonGzTableHeadTiTle(Sheet sheets1, int iBegin) {

        int count = 0;
        if (sheets1.getRow(iBegin) == null) {
            count = 45;
        } else {
            count = sheets1.getRow(iBegin).getLastCellNum();
        }

        Map<String, Integer> map = getTableHeadTiTle(sheets1, iBegin, count);
        return map;
    }

    //更新导入存货
    @Override
    public Map<String, String> updateBImp(InputStream is, String pk_corp, String fileType,
                                          Map<Integer, String> STYLE_1) throws DZFWarpException {
        Map<String, String> importResult = new HashMap<String, String>();
        String pk_auacount_h = AuxiliaryConstant.ITEM_INVENTORY;
        try {
            StringBuffer msg = new StringBuffer();
            AuxiliaryAccountBVO[] qbvos = queryB(pk_auacount_h, pk_corp, null);
            if (qbvos == null || qbvos.length == 0) {
                throw new BusinessException("存货档案为空，请检查");
            }

            Map<String, AuxiliaryAccountBVO> oldmaps = DZfcommonTools.hashlizeObjectByPk(
                    Arrays.asList(qbvos), new String[]{"code", "name"});
//			YntCpaccountVO[] accvos = AccountCache.getInstance().get(null, pk_corp);
//			Map<String, YntCpaccountVO> accmap = AccountUtil.getAccVOByCodeName(pk_corp, accvos, "_");

            AuxiliaryAccountBVO bvo = null;
            Workbook impBook = null;
            if ("xls".equals(fileType)) {
                impBook = new HSSFWorkbook(is);
            } else if ("xlsx".equals(fileType)) {
                impBook = new XSSFWorkbook(is);
            } else {
                throw new BusinessException("不支持的文件格式");
            }
            List<AuxiliaryAccountBVO> list = new ArrayList<>();
            Sheet sheet1 = impBook.getSheetAt(0);
//			Map<Integer, String> STYLE_1 = getColumnMap(sheet1, pk_auacount_h);
            Cell aCell = null;
            String sTmp = "";
            int iBegin = 1;

            for (; iBegin < (sheet1.getLastRowNum() + 1); iBegin++) {
                Row row = sheet1.getRow(iBegin);
                if (row == null || isRowEmpty(row))
                    continue;
                bvo = new AuxiliaryAccountBVO();
                for (Integer key : STYLE_1.keySet()) {
                    String column = STYLE_1.get(key);
                    aCell = row.getCell(key.intValue());
                    sTmp = getExcelCellValue(aCell, column);

                    if (!StringUtil.isEmpty(sTmp)) {
                        sTmp = sTmp.replaceAll("^( | )+|( | )+$", "");
                        bvo.setAttributeValue(column, sTmp);
                    }
                }

//				convertSpecialValue(pk_auacount_h, pk_corp, bvo, accmap);
                list.add(bvo);
            }

            Map<String, AuxiliaryAccountBVO> newmaps = DZfcommonTools.hashlizeObjectByPk(
                    list, new String[]{"code", "name"});

            List<AuxiliaryAccountBVO> errlist = new ArrayList<>();
            List<AuxiliaryAccountBVO> succlist = new ArrayList<>();
            String key;
            AuxiliaryAccountBVO oldbvo = null;
            String[] fields = new String[]{"spec", "jsprice", "unit"};//"kmclassify", "chukukmid",
            for (Map.Entry<String, AuxiliaryAccountBVO> entry : newmaps.entrySet()) {
                key = entry.getKey();
                bvo = entry.getValue();
                if (oldmaps.containsKey(key)) {
                    oldbvo = oldmaps.get(key);
                    for (String field : fields) {
                        oldbvo.setAttributeValue(field, bvo.getAttributeValue(field));
                    }
                    succlist.add(oldbvo);
                } else {
                    errlist.add(bvo);
                }
            }

            if (succlist != null && succlist.size() > 0) {
                singleObjectBO.updateAry(succlist.toArray(new AuxiliaryAccountBVO[0]), fields);
                msg.append("更新导入成功 ").append(succlist.size()).append(" 条记录<br> ");
            }
            if (errlist.size() > 0) {
                msg.append("<font color = 'red'>更新导入失败 ").append(errlist.size()).append(" 条记录</font><br/>");
                StringBuffer inner = new StringBuffer();

                int count = 1;
                for (AuxiliaryAccountBVO vo : errlist) {
                    inner.append(count++ % 10 == 0 ? "<br/>" : "").append(vo.getCode()).append("_").append(vo.getName()).append(", ");
                }
                msg.append("<font color = 'red'>其中包含:").append(inner.substring(0, inner.length() - 2)).append("与档案不一致,请检查</font>");
            }
            importResult.put("msg", msg.toString());
            importResult.put("failCount", errlist.size() + "");
            importResult.put("successCount", succlist.size() + "");
            return importResult;
        } catch (BusinessException e) {
            log.error("错误", e);
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            log.error("错误", e);
            throw new BusinessException("未知异常");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public Map<String, AuxiliaryAccountBVO> queryMap(String pk_corp) throws DZFWarpException {
        Map<String, AuxiliaryAccountBVO> map = new HashMap<String, AuxiliaryAccountBVO>();
        AuxiliaryAccountBVO[] bvos = queryAllB(pk_corp);
        if (bvos != null) {
            for (AuxiliaryAccountBVO bvo : bvos) {
                map.put(bvo.getPk_auacount_b(), bvo);
            }
        }
        return map;
    }

    /**
     * 查询全部辅助项目子表,注意和queryB 方法有区分。
     */
    @Override
    public AuxiliaryAccountBVO[] queryAllB(String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
        AuxiliaryAccountBVO[] bvos = null;
        //启用进销存
        if (IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
            bvos = (AuxiliaryAccountBVO[]) singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class,
                    " pk_corp = ? and nvl(dr,0) = 0  and pk_auacount_h <> '000001000000000000000006'  ", sp);
            List<AuxiliaryAccountBVO> bvoList = null;
            if (bvos == null || bvos.length == 0) {
                bvoList = new ArrayList<AuxiliaryAccountBVO>();
            } else {
                bvoList = new ArrayList<AuxiliaryAccountBVO>(Arrays.asList(bvos));
            }
            //查询存货档案
            AuxiliaryAccountBVO[] invos = queryInventory(pk_corp, null);
            if (invos != null && invos.length > 0) {
                bvoList.addAll(new ArrayList<AuxiliaryAccountBVO>(Arrays.asList(invos)));
            }
            bvos = bvoList.toArray(bvos);
        } else {
            bvos = (AuxiliaryAccountBVO[]) singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class,
                    " pk_corp = ? and nvl(dr,0) = 0 ", sp);
        }
        VOUtil.ascSort(bvos, new String[]{"pk_auacount_h", "code"});
        return bvos;
    }

//	private String filterZero(String number) {
//		if (number.indexOf(".") > 0) {
//			number = number.replaceAll("0+$", "");// 去掉多余的0
//			number = number.replaceAll("\\.$", "");// 如最后一位是.则去掉
//		}
//		return number;
//	}

    @Override
    public AuxiliaryAccountBVO createBvoByName(String pk_corp, String name, String hid) throws DZFWarpException {
        return null;
    }

    @Override
    public boolean checkRepeat(AuxiliaryAccountBVO bvo) throws DZFWarpException {
        boolean isRepeat = false;
        String hid = bvo.getPk_auacount_h();
        String pk_corp = bvo.getPk_corp();
        AuxiliaryAccountBVO qvo = null;
        if (!StringUtil.isEmpty(bvo.getCode())) {
            qvo = queryBByCode(pk_corp, bvo.getCode(), hid);
        } else if (!StringUtil.isEmpty(bvo.getName())) {
            qvo = queryBByName(pk_corp, bvo.getName(), hid);
        } else {
            return false;
        }
        if (qvo != null && !qvo.getPk_auacount_b().equals(bvo.getPk_auacount_b()))
            isRepeat = true;
        return isRepeat;
    }

    @Override
    public boolean isInventoryCategory(String corpId, String subjectId) throws DZFWarpException {
        boolean isCategory = false;
        CorpVO corp = corpService.queryByPk(corpId);
        if (IcCostStyle.IC_INVTENTORY.equals(corp.getBbuildic())) {
            InventorySetVO invSet = gl_ic_invtorysetserv.query(corpId);
            if (invSet != null && invSet.getChcbjzfs() == InventoryConstant.IC_CHDLHS
                    && Kmschema.isKmclassify(corpId, corp.getCorptype(), subjectId)) {
                isCategory = true;
            }
        }
        return isCategory;
    }

    @Override
    public AuxiliaryAccountBVO[] queryInvtaxInfo(String invname, String pk_corp) throws DZFWarpException {
        if (StringUtil.isEmpty(invname)) {
            return null;
        }
        TaxClassifyGetValue tcv = new TaxClassifyGetValue(invname);
        String result = tcv.sendPostData();
        TaxinfoClassifyVO headvo = JsonUtils.deserialize(result, TaxinfoClassifyVO.class);
        if (!headvo.isSuccess())
            return null;
        TaxinfoClassifyBVO[] bodyvos = headvo.getData();
        if (bodyvos == null || bodyvos.length == 0)
            return null;
        AuxiliaryAccountBVO[] bvos = new AuxiliaryAccountBVO[bodyvos.length];
        for (int i = 0; i < bodyvos.length; i++) {
            bvos[i] = new AuxiliaryAccountBVO();
            bvos[i].setTaxcode(bodyvos[i].getTcode());
            bvos[i].setTaxname(bodyvos[i].getUname());
            bvos[i].setTaxclassify(bodyvos[i].getTname());
        }
        return bvos;
    }

    @Override
    public String[] delete(AuxiliaryAccountBVO[] bvos) throws DZFWarpException {
        if (bvos == null || bvos.length == 0) {
            return new String[]{"没有要删除的项目"};
        }
        AuxiliaryAccountHVO hvo = queryHByID(bvos[0].getPk_auacount_h());
        List<AuxiliaryAccountBVO> list = Arrays.asList(bvos);
        list = new ArrayList<AuxiliaryAccountBVO>(list);
        Map<String, String> refInfo = filterUsedItem(list, hvo);
        singleObjectBO.deleteVOArray(list.toArray(new AuxiliaryAccountBVO[0]));
        StringBuilder msg = new StringBuilder();
        int total = bvos.length;
        int success = list.size();
        if (bvos.length > 1) {
            msg.append("成功删除").append(success).append("条<br>");
        } else if (success == 1) {
            msg.append("删除成功");
        }
        if (total > success) {
            for (Map.Entry<String, String> entry : refInfo.entrySet()) {
                msg.append("项目").append(entry.getValue()).append("删除失败：").append(entry.getKey()).append("<br>");
            }
        }
        List<String> successInfo = new ArrayList<>();
        successInfo.add(msg.toString());
        for (AuxiliaryAccountBVO bvo : list) {
            successInfo.add("编码：" + bvo.getCode() + "，名称：" + bvo.getName());
        }
        // 关联删除存货匹配表
        deleteCasCadeGoods(hvo, list);
        return successInfo.toArray(new String[0]);
    }

    private void deleteCasCadeGoods(AuxiliaryAccountHVO hvo, List<AuxiliaryAccountBVO> list) {
        Integer pCode = hvo.getCode().intValue();
        if (pCode != null && pCode == 6 && list != null && list.size() > 0) {
            List<String> pkList = new ArrayList<String>();
            for (AuxiliaryAccountBVO bvo : list) {
                pkList.add(bvo.getPrimaryKey());
            }

            String pk_corp = list.get(0).getPk_corp();
            goodsinvenservice.deleteCasCadeGoods(pkList.toArray(new String[0]), pk_corp);
            gl_ic_invtoryaliasserv.deleteByInvs(pkList.toArray(new String[0]), pk_corp);
        }
    }

    private Map<String, String> filterUsedItem(List<AuxiliaryAccountBVO> vos, AuxiliaryAccountHVO hvo) {
        int total = vos.size();
        Map<String, String> errorMsg = new LinkedHashMap<String, String>();
        Set<String> used = new HashSet<String>();
        List<String> itemID = new ArrayList<String>();
        Map<String, String> codeMap = new HashMap<String, String>();
        for (AuxiliaryAccountBVO vo : vos) {
            itemID.add(vo.getPk_auacount_b());
            codeMap.put(vo.getPk_auacount_b(), vo.getCode());
        }
        int pCode = hvo.getCode().intValue();

        SQLParameter sp = new SQLParameter();
        sp.addParam(vos.get(0).getPk_corp());

        List<String> rs = null;
        StringBuilder sql = null;
        String colName = "fzhsx" + pCode;
        String inSql = SqlUtil.buildSqlForIn(colName, itemID.toArray(new String[itemID.size()]));
        // 期初
        sql = new StringBuilder();
        sql.append("select distinct ").append(colName).append(" as fzhs from ynt_fzhsqc where ").append(inSql)
                .append(" and pk_corp = ? and nvl(dr,0) = 0 ");
        rs = (List<String>) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnListProcessor("fzhs"));
        if (rs.size() > 0) {
            used.addAll(rs);
            castPkToCode(rs, codeMap);
            errorMsg.put("被科目期初引用", StringUtil.toString(rs.toArray()));
            if (used.size() == total) {
                vos.clear();
                return errorMsg;
            }
        }

        // 凭证
        sql = new StringBuilder();
        sql.append("select distinct ").append(colName).append(" as fzhs from ynt_tzpz_b where ").append(inSql)
                .append(" and pk_corp = ? and nvl(dr,0) = 0 ");
        rs = (List<String>) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnListProcessor("fzhs"));
        if (rs.size() > 0) {
            used.addAll(rs);
            castPkToCode(rs, codeMap);
            errorMsg.put("被凭证引用", StringUtil.toString(rs.toArray()));
            if (used.size() == total) {
                vos.clear();
                return errorMsg;
            }
        }
        // 工厂凭证
//		sql = new StringBuilder();
//		sql.append("select distinct ").append(colName).append(" as fzhs from fct_ynt_tzpz_b where ").append(inSql)
//				.append(" and pk_corp = ? and nvl(dr,0) = 0 ");
//		rs = (List<String>) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnListProcessor("fzhs"));
//		if (rs.size() > 0) {
//			used.addAll(rs);
//			castPkToCode(rs, codeMap);
//			errorMsg.put("被工厂凭证引用", StringUtil.toString(rs.toArray()));
//			if (used.size() == total) {
//				vos.clear();
//				return errorMsg;
//			}
//		}

        if (AuxiliaryConstant.ITEM_STAFF.equals(hvo.getPk_auacount_h())) {
            inSql = SqlUtil.buildSqlForIn("cpersonid", itemID.toArray(new String[itemID.size()]));
            sql = new StringBuilder();
            sql.append("select distinct cpersonid as fzhs from ynt_salaryreport where ").append(inSql)
                    .append(" and pk_corp = ? and nvl(dr,0) = 0 ");
            rs = (List<String>) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnListProcessor("fzhs"));
            if (rs.size() > 0) {
                used.addAll(rs);
                castPkToCode(rs, codeMap);
                errorMsg.put("被工资表引用", StringUtil.toString(rs.toArray()));
                if (used.size() == total) {
                    vos.clear();
                    return errorMsg;
                }
            }

            inSql = SqlUtil.buildSqlForIn("cpersonid", itemID.toArray(new String[itemID.size()]));
            sql = new StringBuilder();
            sql.append("select distinct cpersonid as fzhs from ynt_salarybase where ").append(inSql)
                    .append(" and pk_corp = ? and nvl(dr,0) = 0 ");
            rs = (List<String>) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnListProcessor("fzhs"));
            if (rs.size() > 0) {
                used.addAll(rs);
                castPkToCode(rs, codeMap);
                errorMsg.put("被社保公积金引用", StringUtil.toString(rs.toArray()));
                if (used.size() == total) {
                    vos.clear();
                    return errorMsg;
                }
            }
        }

        if (AuxiliaryConstant.ITEM_DEPARTMENT.equals(hvo.getPk_auacount_h())) {
            inSql = SqlUtil.buildSqlForIn("cdeptid", itemID.toArray(new String[itemID.size()]));
            sql = new StringBuilder();
            sql.append("select distinct cdeptid as fzhs from ynt_fzhs_b where ").append(inSql)
                    .append(" and pk_corp = ? and nvl(dr,0) = 0 ");
            rs = (List<String>) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnListProcessor("fzhs"));
            if (rs.size() > 0) {
                used.addAll(rs);
                castPkToCode(rs, codeMap);
                errorMsg.put("被辅助职员引用", StringUtil.toString(rs.toArray()));
                if (used.size() == total) {
                    vos.clear();
                    return errorMsg;
                }
            }

            sql.setLength(0);
            sql.append("select distinct cdeptid as fzhs from ynt_salaryreport where ").append(inSql)
                    .append(" and pk_corp = ? and nvl(dr,0) = 0 ");
            rs = (List<String>) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnListProcessor("fzhs"));
            if (rs.size() > 0) {
                used.addAll(rs);
                castPkToCode(rs, codeMap);
                errorMsg.put("被工资表引用", StringUtil.toString(rs.toArray()));
                if (used.size() == total) {
                    vos.clear();
                    return errorMsg;
                }
            }

            sql.setLength(0);
            sql.append("select distinct cdeptid as fzhs from ynt_salarykmdept where ").append(inSql)
                    .append(" and pk_corp = ? and nvl(dr,0) = 0 ");
            rs = (List<String>) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnListProcessor("fzhs"));
            if (rs.size() > 0) {
                used.addAll(rs);
                castPkToCode(rs, codeMap);
                errorMsg.put("被部门费用设置引用", StringUtil.toString(rs.toArray()));
                if (used.size() == total) {
                    vos.clear();
                    return errorMsg;
                }
            }
        }
        if (AuxiliaryConstant.ITEM_CUSTOMER.equals(hvo.getPk_auacount_h())) {
            sql = new StringBuilder();
            inSql = SqlUtil.buildSqlForIn("pk_app_customer", itemID.toArray(new String[itemID.size()]));
            sql.append("select distinct pk_app_customer as fzhs from ynt_app_billapply where ").append(inSql)
                    .append(" and pk_corp = ? and nvl(dr,0) = 0 ");
            rs = (List<String>) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnListProcessor("fzhs"));
            if (rs.size() > 0) {
                used.addAll(rs);
                castPkToCode(rs, codeMap);
                errorMsg.put("被开票申请单引用", StringUtil.toString(rs.toArray()));
                if (used.size() == total) {
                    vos.clear();
                    return errorMsg;
                }
            }

            sql = new StringBuilder();
            inSql = SqlUtil.buildSqlForIn("pk_cust", itemID.toArray(new String[itemID.size()]));
            sql.append("select distinct pk_cust as fzhs   from ynt_ictrade_h where ").append(inSql)
                    .append(" and pk_corp = ? and nvl(dr,0) = 0 and cbilltype ='" + IBillTypeCode.HP75 + "' ");
            rs = (List<String>) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnListProcessor("fzhs"));

            if (rs.size() > 0) {
                used.addAll(rs);
                castPkToCode(rs, codeMap);
                errorMsg.put("被出库单引用", StringUtil.toString(rs.toArray()));
                if (used.size() == total) {
                    vos.clear();
                    return errorMsg;
                }
            }
        }
        if (AuxiliaryConstant.ITEM_SUPPLIER.equals(hvo.getPk_auacount_h())) {
            inSql = SqlUtil.buildSqlForIn("pk_cust", itemID.toArray(new String[itemID.size()]));
            sql = new StringBuilder();
            sql.append("select  distinct pk_cust as fzhs   from ynt_ictrade_h where ").append(inSql)
                    .append(" and pk_corp = ? and nvl(dr,0) = 0 and cbilltype ='" + IBillTypeCode.HP70 + "' ");
            rs = (List<String>) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnListProcessor("fzhs"));

            if (rs.size() > 0) {
                used.addAll(rs);
                castPkToCode(rs, codeMap);
                errorMsg.put("被入库单引用", StringUtil.toString(rs.toArray()));
                if (used.size() == total) {
                    vos.clear();
                    return errorMsg;
                }
            }
        }
        if (AuxiliaryConstant.ITEM_INVENTORY.equals(hvo.getPk_auacount_h())) {
            inSql = SqlUtil.buildSqlForIn("pk_app_commodity", itemID.toArray(new String[itemID.size()]));
            sql = new StringBuilder();
            sql.append("select distinct pk_app_commodity as fzhs from ynt_app_billapply_detail where ").append(inSql)
                    .append(" and pk_corp = ? and nvl(dr,0) = 0 ");
            rs = (List<String>) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnListProcessor("fzhs"));
            if (rs.size() > 0) {
                used.addAll(rs);
                castPkToCode(rs, codeMap);
                errorMsg.put("被开票申请单引用", StringUtil.toString(rs.toArray()));
                if (used.size() == total) {
                    vos.clear();
                    return errorMsg;
                }
            }

            inSql = SqlUtil.buildSqlForIn("pk_inventory", itemID.toArray(new String[itemID.size()]));
            sql = new StringBuilder();
            sql.append("select distinct pk_inventory as fzhs from ynt_glicqc where ").append(inSql)
                    .append(" and pk_corp = ? and nvl(dr,0) = 0 ");
            rs = (List<String>) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnListProcessor("fzhs"));
            if (rs.size() > 0) {
                used.addAll(rs);
                castPkToCode(rs, codeMap);
                errorMsg.put("被总账存货期初引用", StringUtil.toString(rs.toArray()));
                if (used.size() == total) {
                    vos.clear();
                    return errorMsg;
                }
            }
        }
        // 从列表中移除被引用项目
        removeItem(vos, used);
        return errorMsg;
    }

    private void castPkToCode(List<String> list, Map<String, String> map) {
        int len = list.size();
        for (int i = 0; i < len; i++) {
            list.set(i, map.get(list.get(i)));
        }
    }

    private void removeItem(List<AuxiliaryAccountBVO> vos, Set<String> set) {
        Iterator<AuxiliaryAccountBVO> it = vos.iterator();
        while (it.hasNext()) {
            AuxiliaryAccountBVO vo = it.next();
            if (set.contains(vo.getPk_auacount_b())) {
                it.remove();
            }
        }
    }

    @Override
    public AuxiliaryAccountBVO[] queryAllBByLb(String pk_corp, String fzlb) throws DZFWarpException {

        AuxiliaryAccountBVO[] resvos = queryAllB(pk_corp);

        List<AuxiliaryAccountBVO> list = new ArrayList<AuxiliaryAccountBVO>();

        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(fzlb);
        AuxiliaryAccountHVO[] hvos = (AuxiliaryAccountHVO[]) singleObjectBO.queryByCondition(AuxiliaryAccountHVO.class,
                " nvl(dr,0)=0 and pk_corp in( ?,'000001') and code = ?", sp);

        if (hvos != null && hvos.length > 0) {
            String pk_headid = hvos[0].getPk_auacount_h();
            if (resvos != null && resvos.length > 0) {
                for (AuxiliaryAccountBVO bvo : resvos) {
                    if (pk_headid.equals(bvo.getPk_auacount_h())) {
                        list.add(bvo);
                    }
                }
            }
        }

        return list.toArray(new AuxiliaryAccountBVO[0]);
    }

    private String checkbeforeSave(String pk_auacount_h, String pk_corp, AuxiliaryAccountBVO bvo,
                                   HashSet<String> codeSet, HashSet<String> taxpayerSet, HashSet<String> nameSet,
                                   Map<String, AuxiliaryAccountBVO> aumap, int row, Set<String> fcztSet) throws DZFWarpException {

        String hid = bvo.getPk_auacount_h();
        StringBuffer message = new StringBuffer();

        checkFieldLength(bvo, message, row);

        if (AuxiliaryConstant.ITEM_CUSTOMER.equals(hid) || AuxiliaryConstant.ITEM_SUPPLIER.equals(hid)) {

            checkCust(message, bvo, codeSet, taxpayerSet, nameSet, row);

        } else if (AuxiliaryConstant.ITEM_STAFF.equals(hid)) {

            checkSTAFF(message, bvo, codeSet, nameSet, aumap, row, fcztSet);
        } else if (AuxiliaryConstant.ITEM_INVENTORY.equals(hid)) {
            checkinv(message, bvo, codeSet, nameSet, row);
            if (bvo.getXslx() == null)
                bvo.setXslx(0);
        } else {
            checkother(message, bvo, codeSet, nameSet, row);
        }
        if (row > 0 && message.length() > 0) {
            message.insert(0, "第" + (row + 1) + "行的").append("<br>");
        }
        return message.toString();
    }

    private void checkSTAFF(StringBuffer message, AuxiliaryAccountBVO bvo, HashSet<String> codeSet,
                            HashSet<String> nameSet, Map<String, AuxiliaryAccountBVO> aumap, int row, Set<String> fcztSet) {

        if (!StringUtil.isEmpty(bvo.getCode()) && bvo.getCode().indexOf(".") > -1) {
            bvo.setCode(bvo.getCode().substring(0, bvo.getCode().indexOf(".")));
        }
        if (!StringUtil.isEmpty(bvo.getZjlx()) && bvo.getZjlx().indexOf(".") > -1) {
            bvo.setZjlx(bvo.getZjlx().substring(0, bvo.getZjlx().indexOf(".")));
        }

        // if (!StringUtil.isEmpty(bvo.getName()) && bvo.getName().indexOf(".")
        // > -1) {
        // bvo.setName(bvo.getName().substring(0, bvo.getName().indexOf(".")));
        // }

        if (StringUtil.isEmpty(bvo.getBilltype())) {
            dealMessage(message, row, "员工类型不能为空！");
            return;
        }

        if (row > 0) {
            if (SalaryTypeEnum.getTypeEnumByName(bvo.getBilltype()) == null) {
                dealMessage(message, row, "员工类型" + bvo.getBilltype() + ",不匹配！");
                return;
            } else {
                bvo.setBilltype(SalaryTypeEnum.getTypeEnumByName(bvo.getBilltype()).getValue());
            }
        }

        if (!StringUtil.isEmpty(bvo.getCode())) {
            if (codeSet.contains(bvo.getCode())) {
                dealMessage(message, row, "编码为：" + bvo.getCode() + "已存在！");
            } else {
                codeSet.add(bvo.getCode());
            }
        } else {
            dealMessage(message, row, "编码不能为空！");
        }

        if (SalaryTypeEnum.NORMALSALARY.getValue().equals(bvo.getBilltype())) {
            checkIsNotNull(bvo, message, row);
        } else if (SalaryTypeEnum.REMUNERATION.getValue().equals(bvo.getBilltype())) {
            checkIsNotNull(bvo, message, row);
        } else if (SalaryTypeEnum.FOREIGNSALARY.getValue().equals(bvo.getBilltype())) {
            // “外籍薪资”必输项：员工编码，员工姓名，证件类型、证件编码、手机号码、国籍、来华时间。
            checkIsNotNull(bvo, message, row);
            if (bvo.getIsimp() == null || !bvo.getIsimp().booleanValue()) {
                if (StringUtil.isEmpty(bvo.getVarea())) {
                    dealMessage(message, row, "国籍不能为空！");
                }
                // if (bvo.getLhdate() == null) {
                // dealMessage(message, row, "来华时间不能为空！");
                // }
                // if (StringUtil.isEmpty(bvo.getLhtype())) {
                // dealMessage(message, row, "适用公式不能为空！");
                // }
            }
        }

        if (!StringUtil.isEmpty(bvo.getName()) || !StringUtil.isEmpty(bvo.getZjbm())) {

            if (!SalaryTypeEnum.NONORMAL.getValue().equals(bvo.getBilltype())) {
                if (nameSet.contains(bvo.getZjbm())) {

                    if (StringUtil.isEmpty(bvo.getZjbm())) {
                        dealMessage(message, row, "名称为：" + bvo.getName() + "已存在！");
                    } else if (StringUtil.isEmpty(bvo.getName())) {
                        dealMessage(message, row, "证件编号为" + bvo.getZjbm() + "已存在！");
                    } else {
                        if (fcztSet.contains(bvo.getZjbm())) {
                            dealMessage(message, row, "员工：" + bvo.getName() + "，证件编号为" + bvo.getZjbm() + "已封存，不允许重复添加！");
                        } else {
                            dealMessage(message, row, "名称为：" + bvo.getName() + "，证件编号为" + bvo.getZjbm() + "已存在！");
                        }
                    }
                } else {
                    nameSet.add(bvo.getZjbm());
                }
            } else {
                if (nameSet.contains(bvo.getName())) {

                    if (StringUtil.isEmpty(bvo.getZjbm())) {
                        dealMessage(message, row, "名称为：" + bvo.getName() + "已存在！");
                    } else if (StringUtil.isEmpty(bvo.getName())) {
                        dealMessage(message, row, "证件编号为" + bvo.getZjbm() + "已存在！");
                    } else {
                        dealMessage(message, row, "名称为：" + bvo.getName() + "，证件编号为" + bvo.getZjbm() + "已存在！");
                    }
                } else {
                    nameSet.add(bvo.getName());
                }
            }
        }

        Pattern p = null;
        Matcher m = null;
        String phonepattern = "^((1[3-9][0-9]))\\d{8}$";
        String idpattern = "((11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|50|51|52|53|54|61|62|63|64|65)[0-9]{4})"
                + "(([1|2][0-9]{3}[0|1][0-9][0-3][0-9][0-9]{3}" + "[Xx0-9])|([0-9]{2}[0|1][0-9][0-3][0-9][0-9]{3}))";

        List<String> list1 = new ArrayList<String>();
        list1.add("身份证");
        list1.add("护照");
        list1.add("军官证");
        list1.add("士兵证");
        list1.add("武警警官证");

        List<String> list = new ArrayList<String>();

        for (SalaryReportEnum enum1 : SalaryReportEnum.values()) {
            if (SalaryTypeEnum.FOREIGNSALARY.getValue().equals(bvo.getBilltype())) {
                if (!list1.contains(enum1.getValue())) {
                    list.add(enum1.getValue());
                }
            } else {
                list.add(enum1.getValue());
            }
        }

        if (!StringUtil.isEmpty(bvo.getZjlx())) {
            if (row > 0) {
                if (SalaryReportEnum.getTypeEnumByName(bvo.getZjlx()) == null) {
                    dealMessage(message, row, "证件类型" + bvo.getZjlx() + ",不匹配！");
                    return;
                } else {
                    bvo.setZjlx(SalaryReportEnum.getTypeEnumByName(bvo.getZjlx()).getValue());
                }
            }

            if (!list.contains(bvo.getZjlx())) {
                dealMessage(message, row, "证件类型：" + bvo.getZjlx() + "不合规范，请检查！");
            }
        }

        if (!StringUtil.isEmpty(bvo.getZjlx()) && !StringUtil.isEmpty(bvo.getZjbm())) {
            if (bvo.getZjlx().equalsIgnoreCase("身份证")) {
                p = Pattern.compile(idpattern);
                m = p.matcher(bvo.getZjbm());
                if (!m.matches()) {
                    dealMessage(message, row, "居民身份证：" + bvo.getZjbm() + "不合规范，请检查！");
                }
            }
        }

        if (!StringUtil.isEmpty(bvo.getVphone())) {
            // 手机号验证
            p = Pattern.compile(phonepattern);
            m = p.matcher(bvo.getVphone());
            if (!m.matches()) {
                dealMessage(message, row, "手机号：" + bvo.getVphone() + "不正确，请检查！");
            }
        }

        if (StringUtil.isEmpty(bvo.getPk_auacount_b())) {
            if (!StringUtil.isEmpty(bvo.getVdeptname())) {
                AuxiliaryAccountBVO deptvo = aumap.get(bvo.getVdeptname());
                if (deptvo != null) {
                    bvo.setCdeptid(deptvo.getPk_auacount_b());
                } else {
                    dealMessage(message, row, "部门：" + bvo.getVdeptname() + "不存在，请检查！");
                }
            }
        }

        DZFDate entrydate = bvo.getEntrydate();
        DZFDate leavedate = bvo.getLeavedate();

        if (entrydate != null && leavedate != null) {
            if (entrydate.compareTo(leavedate) >= 0) {
                dealMessage(message, row, "预计离境时间必须大于首次入境时间 ，请检查！");
            }
        }

    }

    private void checkCust(StringBuffer message, AuxiliaryAccountBVO bvo, HashSet<String> codeSet,
                           HashSet<String> taxpayerSet, HashSet<String> nameSet, int row) {

        if (!StringUtil.isEmpty(bvo.getCode())) {
            if (codeSet.contains(bvo.getCode())) {
                dealMessage(message, row, "编码为：" + bvo.getCode() + "已存在！");
            } else {
                codeSet.add(bvo.getCode());
            }
        } else {
            dealMessage(message, row, "编码不能为空！");
        }

        if (!StringUtil.isEmpty(bvo.getName())) {
            if (nameSet.contains(bvo.getName())) {
                dealMessage(message, row, "名称为：" + bvo.getName() + "已存在！");
            } else {
                nameSet.add(bvo.getName());
            }
        } else {
            dealMessage(message, row, "名称不能为空！");
        }

        // 纳税人识别号重复校验
        if (!StringUtil.isEmpty(bvo.getTaxpayer())) {
            if (taxpayerSet.contains(bvo.getTaxpayer())) {
                dealMessage(message, row, "纳税人识别号为：" + bvo.getTaxpayer() + "已存在！");
            } else {
                taxpayerSet.add(bvo.getTaxpayer());
            }
        }
    }

    // 存货校验
    private void checkinv(StringBuffer message, AuxiliaryAccountBVO bvo, HashSet<String> codeSet,
                          HashSet<String> nameSet, int row) {
        if (!StringUtil.isEmpty(bvo.getCode())) {
            if (codeSet.contains(bvo.getCode())) {
                dealMessage(message, row, "编码为：" + bvo.getCode() + "已存在！");
            } else {
                codeSet.add(bvo.getCode());
            }
        } else {
            dealMessage(message, row, "编码不能为空！");
        }
        String nameInfoKey = getNameInfoKey(bvo);
        if (!StringUtil.isEmpty(nameInfoKey)) {
            if (nameSet.contains(nameInfoKey)) {
                dealMessage(message, row, "存货名称[" + bvo.getName() + "]、规格(型号)[" + bvo.getSpec() + "]、计量单位["
                        + bvo.getUnit() + "]至少有一项不同！");
            } else {
                nameSet.add(nameInfoKey);
            }
        } else {
            dealMessage(message, row, "名称不能为空！");
        }
        // 判断存货类别是否合规
        if (!StringUtil.isEmpty(bvo.getKmclassifyname()) && StringUtil.isEmpty(bvo.getKmclassify())) {
            dealMessage(message, row, "存货类别：" + bvo.getKmclassifyname() + "未匹配上!");
        }
        // 判断出库科目是否合规
        if (!StringUtil.isEmpty(bvo.getChukukmname()) && StringUtil.isEmpty(bvo.getChukukmid())) {
            dealMessage(message, row, "出库科目：" + bvo.getChukukmname() + "未匹配上!");
        }
    }

    private void checkother(StringBuffer message, AuxiliaryAccountBVO bvo, HashSet<String> codeSet,
                            HashSet<String> nameSet, int row) {

        if (!StringUtil.isEmpty(bvo.getCode())) {
            if (codeSet.contains(bvo.getCode())) {
                dealMessage(message, row, "编码为：" + bvo.getCode() + "已存在！");
            } else {
                codeSet.add(bvo.getCode());
            }
        } else {
            dealMessage(message, row, "编码不能为空！");
        }
        String nameInfoKey = bvo.getName();
        if (!StringUtil.isEmpty(nameInfoKey)) {
            if (nameSet.contains(nameInfoKey)) {
                dealMessage(message, row, "名称为：" + bvo.getName() + "已存在！");
            } else {
                nameSet.add(bvo.getName());
            }
        } else {
            dealMessage(message, row, "名称不能为空！");
        }
    }

    private void dealMessage(StringBuffer message, int row, String errinfo) {
        if (row < 0) {
            message.append(errinfo + "<br>");
        } else {
            message.append(errinfo);
        }

    }

    @Override
    public int updateBatchAuxiliaryAccountByIDS(List<AuxiliaryAccountBVO> list, String[] modifyFiled)
            throws DZFWarpException {

        AuxiliaryAccountBVO[] vos = saveBs(list, false);

        return vos.length;
    }

    // @Override
    // public AuxiliaryAccountBVO[] queryBInvBycondition(String pk_auacount_h,
    // String code, String name, String spec, String qchukukmid, String
    // qkmclassify, String pk_corp) throws DZFWarpException {
    // SQLParameter sp = new SQLParameter();
    // String condition = " pk_auacount_h = ? and pk_corp = ? and nvl(dr,0) = 0
    // ";
    // sp.addParam(pk_auacount_h);
    // sp.addParam(pk_corp);
    // if(!StringUtil.isEmpty(code)){
    // condition += " and code like ? ";
    // sp.addParam("%" + code + "%");
    // }
    // if(!StringUtil.isEmpty(name)){
    // condition += " and name like ? ";
    // sp.addParam("%" + name + "%");
    // }
    // if(!StringUtil.isEmpty(spec)){
    // condition += " and spec like ? ";
    // sp.addParam("%" + spec + "%");
    // }
    //
    //
    // if(!StringUtil.isEmpty(qkmclassify)){
    // if(!"all".equals(qkmclassify) && !"-1".equals(qkmclassify)){
    // condition += " and KMCLASSIFY = ? ";
    // sp.addParam(qkmclassify);
    // }else if("-1".equals(qkmclassify)){
    // condition += " and KMCLASSIFY is null ";
    // }
    // }
    //
    // if(!StringUtil.isEmpty(qchukukmid)){
    // if(!"all".equals(qchukukmid) && !"-1".equals(qchukukmid)){
    // condition += " and CHUKUKMID = ? ";
    // sp.addParam(qchukukmid);
    // }else if("-1".equals(qchukukmid)){
    // condition += " and CHUKUKMID is null ";
    // }
    // }
    //
    // condition += " order by code ";
    // AuxiliaryAccountBVO[] results = (AuxiliaryAccountBVO[])
    // singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class, condition,
    // sp);
    // return results;
    // }

    @Override
    public QueryPageVO queryBInvByconditionBypage(String pk_auacount_h, String code, String name, String spec,
                                                  String qchukukmid, String qkmclassify, String pk_corp, int page, int rows) {
        SQLParameter sp = new SQLParameter();
        String condition = " pk_auacount_h = ? and pk_corp = ? and nvl(dr,0) = 0 ";
        sp.addParam(pk_auacount_h);
        sp.addParam(pk_corp);
        if (!StringUtil.isEmpty(code)) {
            condition += " and code like ? ";
            sp.addParam("%" + code + "%");
        }
        if (!StringUtil.isEmpty(name)) {
            condition += " and name like ? ";
            sp.addParam("%" + name + "%");
        }
        if (!StringUtil.isEmpty(spec)) {
            condition += " and spec like ? ";
            sp.addParam("%" + spec + "%");
        }

        if (!StringUtil.isEmpty(qkmclassify)) {
            if (!"all".equals(qkmclassify) && !"-1".equals(qkmclassify)) {
                condition += " and KMCLASSIFY = ? ";
                sp.addParam(qkmclassify);
            } else if ("-1".equals(qkmclassify)) {
                condition += " and KMCLASSIFY is null ";
            }
        }

        if (!StringUtil.isEmpty(qchukukmid)) {
            if (!"all".equals(qchukukmid) && !"-1".equals(qchukukmid)) {
                condition += " and CHUKUKMID = ? ";
                sp.addParam(qchukukmid);
            } else if ("-1".equals(qchukukmid)) {
                condition += " and CHUKUKMID is null ";
            }
        }
        // 统计总数
        int total = singleObjectBO.getTotalRow("ynt_fzhs_b", condition, sp);

        List<AuxiliaryAccountBVO> auxiliaryAccountBVOS = (List<AuxiliaryAccountBVO>) singleObjectBO
                .execQueryWithPage(AuxiliaryAccountBVO.class, "ynt_fzhs_b", condition, sp, page, rows, "order by code");
        QueryPageVO pagevo = new QueryPageVO();
        pagevo.setTotal(total);
        pagevo.setPage(page);
        pagevo.setPageofrows(rows);
        pagevo.setPagevos(auxiliaryAccountBVOS.toArray(new AuxiliaryAccountBVO[0]));
        return pagevo;
    }

    @Override
    public QueryPageVO queryBParamBypage(String pk_auacount_h, String param, String pk_corp, int page, int rows, String type) {
        QueryPageVO pagevo = null;
        if (AuxiliaryConstant.ITEM_STAFF.equals(pk_auacount_h)) {
            pagevo = queryPersonByParamPage(pk_auacount_h, param, pk_corp, page, rows, type);
        } else {
            SQLParameter sp = new SQLParameter();
            String condition = "pk_auacount_h = ? and pk_corp = ? and nvl(dr,0) = 0 ";
            sp.addParam(pk_auacount_h);
            sp.addParam(pk_corp);
            if (!StringUtil.isEmpty(param)) {
                condition += " and (code like ? or name like ?)";
                param = "%" + param + "%";
                sp.addParam(param);
                sp.addParam(param);
            }

            // 统计总数
            int total = singleObjectBO.getTotalRow("ynt_fzhs_b", condition, sp);
            List<AuxiliaryAccountBVO> auxiliaryAccountBVOS = (List<AuxiliaryAccountBVO>) singleObjectBO
                    .execQueryWithPage(AuxiliaryAccountBVO.class, new AuxiliaryAccountBVO().getTableName(), condition,
                            sp, page, rows, "order by code");
//			VOUtil.ascSort(auxiliaryAccountBVOS, new String[]{"code"});
            pagevo = new QueryPageVO();
            pagevo.setTotal(total);
            pagevo.setPage(page);
            pagevo.setPageofrows(rows);
            pagevo.setPagevos(auxiliaryAccountBVOS.toArray(new AuxiliaryAccountBVO[auxiliaryAccountBVOS.size()]));
        }
        return pagevo;
    }

    private QueryPageVO queryPersonByParamPage(String pk_auacount_h, String param, String pk_corp, int page, int rows, String type) {
        // 计算总数
        SQLParameter sp = new SQLParameter();
        StringBuffer sbf1 = new StringBuffer();
        StringBuffer sbf2 = new StringBuffer();
        sbf1.append(" pk_auacount_h = ? and pk_corp = ? and nvl(dr,0) = 0 ");
        sp.addParam(pk_auacount_h);
        sp.addParam(pk_corp);
        if (!StringUtil.isEmpty(param)) {
            sbf2.append(" and (code like ? or name like ?)");
            param = "%" + param + "%";
            sp.addParam(param);
            sp.addParam(param);
        }
        if ("gz".equals(type)) {
            sbf1.append(" and billtype <> ?");
            sp.addParam(SalaryTypeEnum.NONORMAL.getValue());
        }
        // 查询总数
        int total = singleObjectBO.getTotalRow("ynt_fzhs_b", sbf1.append(sbf2).toString(), sp);

        // 根据查询条件查询公司的信息
        StringBuffer sf = new StringBuffer();
        sp.clearParams();
        sf.append(" select y.* ,fb.name vdeptname");
        sf.append(" From ynt_fzhs_b y ");
        sf.append(" left join ynt_fzhs_b fb ");
        sf.append(" on y.cdeptid = fb.pk_auacount_b and fb.pk_auacount_h = ?");

        sf.append(" where y.pk_corp = ? ");
        sf.append(" and nvl(y.dr, 0) = 0 ");
        sf.append(" and y.pk_auacount_h= ? ");
        sp.addParam(AuxiliaryConstant.ITEM_DEPARTMENT);// 部门
        sp.addParam(pk_corp);
        sp.addParam(pk_auacount_h);// 职员
        sbf2.setLength(0);
        if (!StringUtil.isEmpty(param)) {
            sbf2.append(" and (y.code like ? or y.name like ?)");
            param = "%" + param + "%";
        }
        if (sbf2.length() > 0) {
            sf.append(sbf2);
            sp.addParam(param);
            sp.addParam(param);
        }

        if ("gz".equals(type)) {
            sf.append(" and y.billtype <> ?");
            sp.addParam(SalaryTypeEnum.NONORMAL.getValue());
        }
        List<AuxiliaryAccountBVO> list = (List<AuxiliaryAccountBVO>) singleObjectBO.execQueryWithPage(
                AuxiliaryAccountBVO.class, "(" + sf.toString() + ")", "", sp, page, rows, "order by code");
//		VOUtil.ascSort(list, new String[]{"code"});
        sbf1.setLength(0);
        sbf2.setLength(0);
        sf.setLength(0);
        QueryPageVO pagevo = new QueryPageVO();
        pagevo.setTotal(total);
        pagevo.setPage(page);
        pagevo.setPageofrows(rows);
        pagevo.setPagevos(list.toArray(new AuxiliaryAccountBVO[0]));
        return pagevo;
    }

    @Override
    public AuxiliaryAccountBVO saveMergeData(String pk_corp, String id, AuxiliaryAccountBVO[] vos) throws DZFWarpException {
        CorpVO corpvo = corpService.queryByPk(pk_corp);

        AuxiliaryAccountBVO vo = queryBByID(id, pk_corp);
        if (DZFValueCheck.isEmpty(vo))
            throw new BusinessException("合并的存货不存在!");

        if (DZFValueCheck.isEmpty(vos)) {
            throw new BusinessException("被合并的存货不允许为空!");
        }
        checkMergeData(corpvo, vo, vos);

        SQLParameter sp = new SQLParameter();
        List<SQLParameter> list = new ArrayList<SQLParameter>();
        List<String> idlist = new ArrayList<>();
        idlist.add(id);
        for (AuxiliaryAccountBVO vo1 : vos) {
            sp = new SQLParameter();
            sp.addParam(vo.getPk_auacount_b());
            sp.addParam(vo1.getPk_auacount_b());
            sp.addParam(corpvo.getPk_corp());
            list.add(sp);
            idlist.add(vo1.getPk_auacount_b());
        }

        // 更新凭证里的存货id
        StringBuffer sb = new StringBuffer();
        sb.append(" update ynt_tzpz_b set fzhsx6=? where fzhsx6 =?  and pk_corp=? and nvl(dr,0)=0");
        int row = singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));

        // 更新别名表的存货id
        sb.setLength(0);
        sb.append(" update ynt_icalias set pk_inventory=? where pk_inventory =?  and pk_corp=? and nvl(dr,0)=0");
        row = singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));

        String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
        int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
        // 辅助期初的合并数据 并同步的科目期初
        updateIcFzQc(pk_corp, idlist, price, id);
        // 存货期初合并数据
        updateIcBalance(pk_corp, idlist, price, id);
        // 年结表合并数据
        updateIcNj(pk_corp, idlist, price, id);
        // 更新被合并存货dr=1
        list.clear();
        for (AuxiliaryAccountBVO vo1 : vos) {
            sp = new SQLParameter();
            sp.addParam(vo1.getPk_auacount_b());
            sp.addParam(corpvo.getPk_corp());
            list.add(sp);
        }
        sb.setLength(0);
        sb.append(" update ynt_fzhs_b set dr=1 where pk_auacount_b =? and pk_corp=? and nvl(dr,0)=0");
        row = singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));
        return vo;
    }

    private void checkMergeData(CorpVO corpvo, AuxiliaryAccountBVO vo, AuxiliaryAccountBVO[] vos) {

        InventorySetVO orignvo = gl_ic_invtorysetserv.query(corpvo.getPk_corp());
        if (orignvo == null) {
            orignvo = new InventorySetVO();
        }
        // 启用总账存货的参与校验
        if (!IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic()))
            return;
        int chcbjzfs = InventoryConstant.IC_NO_MXHS;// 不核算存货
        if (vo != null)
            chcbjzfs = orignvo.getChcbjzfs();
        String temp1 = "";
        String temp2 = "";
        StringBuffer msg = new StringBuffer();
        if (chcbjzfs == InventoryConstant.IC_CHDLHS) {// 大类
            List<String> list = new ArrayList<>();

            for (AuxiliaryAccountBVO invo : vos) {

                list.clear();

                temp1 = StringUtil.isEmpty(invo.getKmclassify()) ? "invnull" : invo.getKmclassify();
                temp2 = StringUtil.isEmpty(vo.getKmclassify()) ? "invnull" : vo.getKmclassify();
                if (!temp1.equals(temp2))
                    list.add("存货类别");

                temp1 = StringUtil.isEmpty(invo.getChukukmid()) ? "invnull" : invo.getChukukmid();
                temp2 = StringUtil.isEmpty(vo.getChukukmid()) ? "invnull" : vo.getChukukmid();
                if (!temp1.equals(temp2))
                    list.add("出库科目");

                temp1 = StringUtil.isEmpty(invo.getUnit()) ? "invnull" : invo.getUnit();
                temp2 = StringUtil.isEmpty(vo.getUnit()) ? "invnull" : vo.getUnit();
                if (!temp1.equals(temp2))
                    list.add("计量单位");

                if (list.size() > 0) {
                    msg.append(
                            "<p><font color = 'red'>存货[ " + getStrInvName(vo) + "] 和存货[" + getStrInvName(invo) + "]的");
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        if (i == 0) {
                            msg.append(list.get(i));
                        } else {
                            msg.append("\\" + list.get(i));
                        }
                    }
                    msg.append("不一致，请检查</font></p>");
                }
            }
        } else {
            for (AuxiliaryAccountBVO invo : vos) {
                temp1 = StringUtil.isEmpty(invo.getUnit()) ? "invnull" : invo.getUnit();
                temp2 = StringUtil.isEmpty(vo.getUnit()) ? "invnull" : vo.getUnit();
                if (!temp1.equals(temp2)) {
                    msg.append("<p><font color = 'red'>存货[ " + getStrInvName(vo) + "] 和存货[" + getStrInvName(invo)
                            + "]的计量单位不一致，请检查</font></p>");
                }
            }
        }
        if (msg.length() > 0)
            throw new BusinessException(msg.toString());
    }

    private String getStrInvName(AuxiliaryAccountBVO vo) {
        String temp1 = vo.getName();
        if (!StringUtil.isEmpty(vo.getSpec())) {
            temp1 = temp1 + " " + vo.getSpec();
        }

        if (!StringUtil.isEmpty(vo.getInvtype())) {
            temp1 = temp1 + " " + vo.getInvtype();
        }

        return temp1;
    }

    private void updateIcFzQc(String pk_corp, List<String> idlist, int price, String id) {

        String wherepart = SqlUtil.buildSqlConditionForIn(idlist.toArray(new String[idlist.size()]));
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        FzhsqcVO[] bals = (FzhsqcVO[]) singleObjectBO.queryByCondition(FzhsqcVO.class,
                " fzhsx6 in (" + wherepart + ") and nvl(dr,0) = 0 and pk_corp = ?", sp);

        if (bals == null || bals.length == 0)
            return;

        Map<String, List<FzhsqcVO>> map = DZfcommonTools.hashlizeObject(Arrays.asList(bals),
                new String[]{"pk_accsubj", "fzhsx1", "fzhsx2", "fzhsx3", "fzhsx4", "fzhsx5",
                        "fzhsx7", "fzhsx8", "fzhsx9", "fzhsx10"});

        for (Map.Entry<String, List<FzhsqcVO>> entry : map.entrySet()) {
            List<FzhsqcVO> list = entry.getValue();
            if (list != null && list.size() > 0) {
                int size = list.size();

                if (size == 1) {
                    list.get(0).setFzhsx6(id);
                } else {
                    FzhsqcVO vo = list.get(0);
                    FzhsqcVO temp = null;
                    String[] cols = new String[]{"yearqc", "yearjffse", "yeardffse", "ybyearqc", "ybyearjffse",
                            "ybyeardffse", "bnqcnum", "bnfsnum", "bndffsnum"};
                    for (int i = 1; i < size; i++) {
                        temp = list.get(i);
                        for (String name : cols) {
                            vo.setAttributeValue(name, SafeCompute.add((DZFDouble) vo.getAttributeValue(name),
                                    (DZFDouble) temp.getAttributeValue(name)));
                        }
                        calFzQc(vo);
                        list.get(i).setDr(1);
                    }
                    vo.setFzhsx6(id);
                }
            }
        }
        singleObjectBO.updateAry(bals);

    }

    private void calFzQc(FzhsqcVO fzqc) {
        if (fzqc == null)
            return;
        DZFDouble qm = null;
        DZFDouble qcnum = null;
        DZFDouble ybqm = null;
        if (fzqc.getDirect() == 0) {// 借方
            qm = SafeCompute.sub(SafeCompute.add(fzqc.getYearqc(), fzqc.getYearjffse()), fzqc.getYeardffse());
            ybqm = SafeCompute.sub(SafeCompute.add(fzqc.getYbyearqc(), fzqc.getYbyearjffse()), fzqc.getYbyeardffse());
            qcnum = SafeCompute.sub(SafeCompute.add(fzqc.getBnqcnum(), fzqc.getBnfsnum()), fzqc.getBndffsnum());
        } else {// 贷方
            qm = SafeCompute.sub(SafeCompute.add(fzqc.getYearqc(), fzqc.getYeardffse()), fzqc.getYearjffse());
            ybqm = SafeCompute.sub(SafeCompute.add(fzqc.getYbyearqc(), fzqc.getYbyeardffse()), fzqc.getYbyearjffse());
            qcnum = SafeCompute.sub(SafeCompute.add(fzqc.getBnqcnum(), fzqc.getBndffsnum()), fzqc.getBnfsnum());
        }
        fzqc.setThismonthqc(qm);
        fzqc.setYbthismonthqc(ybqm);
        fzqc.setMonthqmnum(qcnum);
    }

    private void updateIcBalance(String pk_corp, List<String> idlist, int price, String id) {

        String wherepart = SqlUtil.buildSqlConditionForIn(idlist.toArray(new String[idlist.size()]));
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        InventoryQcVO[] bals = (InventoryQcVO[]) singleObjectBO.queryByCondition(InventoryQcVO.class,
                " pk_inventory in (" + wherepart + ") and nvl(dr,0) = 0 and pk_corp = ?", sp);

        if (bals != null && bals.length > 0) {
            int len = bals.length;
            InventoryQcVO vo = bals[0];
            InventoryQcVO temp = null;
            for (int i = 1; i < len; i++) {
                temp = bals[i];
                vo.setThismonthqc(SafeCompute.add(vo.getThismonthqc(), temp.getThismonthqc()));
                vo.setMonthqmnum(SafeCompute.add(vo.getMonthqmnum(), temp.getMonthqmnum()));
                vo.setMonthqc_price(SafeCompute.div(vo.getThismonthqc(), vo.getMonthqmnum()).setScale(price, 2));
                bals[i].setDr(1);
            }
            vo.setPk_inventory(id);
            singleObjectBO.updateAry(bals);
        }

    }

    private void updateIcNj(String pk_corp, List<String> idlist, int price, String id) {

        String wherepart = SqlUtil.buildSqlConditionForIn(idlist.toArray(new String[idlist.size()]));
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        KMQMJZVO[] bals = (KMQMJZVO[]) singleObjectBO.queryByCondition(KMQMJZVO.class,
                " fzhsx6 in (" + wherepart + ") and nvl(dr,0) = 0 and pk_corp = ?", sp);

        if (bals == null || bals.length == 0)
            return;

        Map<String, List<KMQMJZVO>> map = DZfcommonTools.hashlizeObject(Arrays.asList(bals),
                new String[]{"pk_accsubj", "period", "pk_currency", "fzhsx1", "fzhsx2", "fzhsx3", "fzhsx4", "fzhsx5",
                        "fzhsx7", "fzhsx8", "fzhsx9", "fzhsx10"});

        List<KMQMJZVO> alist = new ArrayList<>();
        for (Map.Entry<String, List<KMQMJZVO>> entry : map.entrySet()) {
            List<KMQMJZVO> list = entry.getValue();
            if (list != null && list.size() > 0) {
                int len = list.size();
                KMQMJZVO vo = list.get(0);
                KMQMJZVO temp = null;
                for (int i = 1; i < len; i++) {
                    temp = list.get(i);
                    vo.setThismonthqc(SafeCompute.add(vo.getThismonthqc(), temp.getThismonthqc()));
                    vo.setJffse(SafeCompute.add(vo.getJffse(), temp.getJffse()));
                    vo.setDffse(SafeCompute.add(vo.getDffse(), temp.getDffse()));
                    vo.setThismonthqm(SafeCompute.add(vo.getThismonthqm(), temp.getThismonthqm()));
                    vo.setYbjfmny(SafeCompute.add(vo.getYbjfmny(), temp.getYbjfmny()));
                    vo.setYbdfmny(SafeCompute.add(vo.getYbdfmny(), temp.getYbdfmny()));
                    vo.setYbthismonthqc(SafeCompute.add(vo.getYbthismonthqc(), temp.getYbthismonthqc()));
                    vo.setYbthismonthqm(SafeCompute.add(vo.getYbthismonthqm(), temp.getYbthismonthqm()));
                    list.get(i).setDr(1);
                    alist.add(list.get(i));
                }
                vo.setFzhsx6(id);
                alist.add(vo);
            }
        }
        singleObjectBO.updateAry(bals);
    }

    @Override
    public AuxiliaryAccountHVO queryHByCode(String pk_corp, String code) throws DZFWarpException {
        AuxiliaryAccountHVO[] hvos = queryH(pk_corp);
        if (hvos != null && hvos.length > 0) {
            int fzlbcode = Integer.parseInt(code);
            for (AuxiliaryAccountHVO hvo : hvos) {
                if (fzlbcode == hvo.getCode().intValue()) {
                    return hvo;
                }
            }
        }
        return null;
    }
}