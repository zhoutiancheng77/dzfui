package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.enums.SurTaxEnum;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.SurtaxTemplateVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.SurTaxTemplate;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.ISurtaxTemplateService;
import com.dzf.zxkj.platform.service.tax.ICorpTaxService;
import com.dzf.zxkj.platform.util.KmbmUpgrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("gl_surtaxtempserv")
public class SurtaxTemplateServiceImpl implements ISurtaxTemplateService {
    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private ICorpTaxService sys_corp_tax_serv;

    @Override
    public List<SurtaxTemplateVO> query(CorpVO corp) throws DZFWarpException {
        StringBuilder sql = new StringBuilder();
        SQLParameter sp = new SQLParameter();
        sp.addParam(corp.getPk_corp());
        sql.append(" select a.pk_jtsjtemplate,a.pk_corp, a.pk_group,a.vdef1, a.vdef2, a.jfkm_id,a.dfkm_id, ");
        sql.append(" a.tax_code, a.tax_name, a.tax,a.memo, a.snumber, ");
        sql.append(" c.accountname jfkmmc, d.accountname dfkmmc from ynt_jtsj a ");
        sql.append(" left join ynt_cpaccount c on a.jfkm_id = c.pk_corp_account and nvl(c.dr,0)=0 ");
        sql.append(" left join ynt_cpaccount d on a.dfkm_id = d.pk_corp_account and nvl(d.dr,0)=0 ");
        sql.append(" where nvl(a.dr, 0) = 0 and a.pk_corp = ? ");
        List<SurtaxTemplateVO> corpTemps = (List<SurtaxTemplateVO>) singleObjectBO
                .executeQuery(sql.toString(), sp, new BeanListProcessor(SurtaxTemplateVO.class));
        // 停用的预置模板
        Set<String> disabledTemp = new HashSet<>();
        // 是否加载预置模板
        boolean shouldLoadPreset = true;
        List<SurtaxTemplateVO> tempList = new ArrayList<>();
        for (SurtaxTemplateVO vo: corpTemps) {
            if (shouldLoadPreset && "Y".equals(vo.getVdef2())) {
                shouldLoadPreset = false;
            }
            if (!StringUtil.isEmpty(vo.getPk_group())) {
                if ("N".equals(vo.getVdef1())) {
                    disabledTemp.add(vo.getPk_group());
                }
            } else if (!"N".equals(vo.getVdef1())) {
                tempList.add(vo);
            }
        }
        if (shouldLoadPreset) {
            List<SurtaxTemplateVO> presetTemps = getPresetTemplate(corp.getCorptype());
            for (SurtaxTemplateVO vo: presetTemps) {
                if (!disabledTemp.contains(vo.getPk_jtsjtemplate())) {
                    tempList.add(vo);
                }
            }
        }
        setDefaultValue(corp.getPk_corp(), tempList);
        sortTemplate(tempList);
        convertSubject(corp, tempList);
        return tempList;
    }

    @Override
    public List<SurtaxTemplateVO> getPresetTemplate(CorpVO corp) throws DZFWarpException {
        List<SurtaxTemplateVO> presetTemps = getPresetTemplate(corp.getCorptype());
        setDefaultValue(corp.getPk_corp(), presetTemps);
        sortTemplate(presetTemps);
        convertSubject(corp, presetTemps);
        return presetTemps;
    }

    private List<SurtaxTemplateVO> getPresetTemplate(String kmMethod) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(IDefaultValue.DefaultGroup);
        sp.addParam(kmMethod);
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.pk_jtsjtemplate,a.pk_corp,a.kmmethod,a.jfkm_id,a.dfkm_id, ");
        sql.append(" a.tax,a.memo, c.accountcode jfkmbm, c.accountname jfkmmc, d.accountcode dfkmbm, ");
        sql.append(" d.accountname dfkmmc, a.pk_group from ynt_jtsj a ");
        sql.append(" left join ynt_tdacc c on a.jfkm_id = c.pk_trade_account ");
        sql.append(" left join ynt_tdacc d on a.dfkm_id = d.pk_trade_account ");
        sql.append("  where nvl(a.dr, 0) = 0 ");
        sql.append(" and a.pk_corp = ? ");
        sql.append(" and a.kmmethod = ? ");
        List<SurtaxTemplateVO> list = (List<SurtaxTemplateVO>) singleObjectBO
                .executeQuery(sql.toString(), sp, new BeanListProcessor(SurtaxTemplateVO.class));
        return list;
    }

    /**
     * 根据模板科目设置默认税种
     *
     * @param temps
     */
    private void setDefaultValue(String pk_corp, List<SurtaxTemplateVO> temps) {
        CorpTaxVo corpTax = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
        for (SurtaxTemplateVO temp: temps) {
            if (temp.getDfkmmc() != null && temp.getTax_name() == null) {
                if ("应交城建税".equals(temp.getDfkmmc())) {
                    temp.setTax_code(SurTaxEnum.URBAN_CONSTRUCTION_TAX.getCode());
                    temp.setTax_name(SurTaxEnum.URBAN_CONSTRUCTION_TAX.getName());
                } else if ("应交教育费附加".equals(temp.getDfkmmc())) {
                    temp.setTax_code(SurTaxEnum.EDUCATION_SURTAX.getCode());
                    temp.setTax_name(SurTaxEnum.EDUCATION_SURTAX.getName());
                } else if ("应交地方教育附加".equals(temp.getDfkmmc())) {
                    temp.setTax_code(SurTaxEnum.LOCAL_EDUCATION_SURTAX.getCode());
                    temp.setTax_name(SurTaxEnum.LOCAL_EDUCATION_SURTAX.getName());
                } else if (temp.getDfkmmc().contains("水利建设基金")) {
                    temp.setTax_code(SurTaxEnum.LOCAL_WATER_CONSTRUCTION_FUND.getCode());
                    temp.setTax_name(SurTaxEnum.LOCAL_WATER_CONSTRUCTION_FUND.getName());
                }
            }
            if (SurTaxEnum.URBAN_CONSTRUCTION_TAX.getName().equals(temp.getTax_name())) {
                temp.setTax(corpTax.getCitybuildtax().multiply(100));
            } else if (SurTaxEnum.LOCAL_EDUCATION_SURTAX.getName().equals(temp.getTax_name())) {
                temp.setTax(corpTax.getLocaleducaddtax().multiply(100));
            } else if (SurTaxEnum.LOCAL_WATER_CONSTRUCTION_FUND.getName().equals(temp.getTax_name())) {
                SurTaxTemplate wt = getWaterTaxTemplate(pk_corp);
                if (wt != null) {
                    temp.setTax(wt.getRate().multiply(100));
                }
            }
        }
    }

    private void convertSubject(CorpVO corp, List<SurtaxTemplateVO> temps) {
        Map<String, String> codeMap = new HashMap<>();
        String rule = corp.getAccountcoderule();
        if (rule == null) {
            rule = DZFConstant.ACCOUNTCODERULE;
        }
        for (SurtaxTemplateVO temp: temps) {
            if (IDefaultValue.DefaultGroup.equals(temp.getPk_corp())) {
                codeMap.put(temp.getJfkmbm(),
                        KmbmUpgrade.getNewCode(temp.getJfkmbm(), DZFConstant.ACCOUNTCODERULE, rule));
                codeMap.put(temp.getDfkmbm(),
                        KmbmUpgrade.getNewCode(temp.getDfkmbm(), DZFConstant.ACCOUNTCODERULE, rule));
            } else {
                if (temp.getJfkmmc() == null) {
                    temp.setJfkm_id(null);
                }
                if (temp.getDfkmmc() == null) {
                    temp.setDfkm_id(null);
                }
            }
        }

        if (codeMap.size() > 0) {
            SQLParameter sp = new SQLParameter();
            sp.addParam(corp.getPk_corp());
            StringBuilder sql = new StringBuilder();
            sql.append("select pk_corp_account, accountcode, accountname from ynt_cpaccount where pk_corp = ? and  ")
                    .append(SqlUtil.buildSqlForIn("accountcode", codeMap.values().toArray(new String[0])))
                    .append(" and nvl(dr,0)=0 ");
            List<YntCpaccountVO> accounts = (List<YntCpaccountVO>) singleObjectBO
                    .executeQuery(sql.toString(), sp, new BeanListProcessor(YntCpaccountVO.class));
            Map<String, YntCpaccountVO> accountMap = DZfcommonTools
                    .hashlizeObjectByPk(accounts, new String[]{"accountcode"});
            for (SurtaxTemplateVO temp: temps) {
                if (IDefaultValue.DefaultGroup.equals(temp.getPk_corp())) {
                    temp.setPk_corp(corp.getPk_corp());
                    YntCpaccountVO jfAccount = accountMap.get(codeMap.get(temp.getJfkmbm()));
                    temp.setJfkm_id(jfAccount == null ? null : jfAccount.getPk_corp_account());
                    YntCpaccountVO dfAccount = accountMap.get(codeMap.get(temp.getDfkmbm()));
                    temp.setDfkm_id(dfAccount == null ? null : dfAccount.getPk_corp_account());
                }
            }
        }
    }

    private void sortTemplate(List<SurtaxTemplateVO> temps) {
        temps.sort((vo1, vo2) -> {
            Integer num1 = vo1.getSnumber() == null ? Integer.valueOf(99) : vo1.getSnumber();
            Integer num2 = vo2.getSnumber() == null ? Integer.valueOf(99) : vo2.getSnumber();
            int cp = num1.compareTo(num2);
            if (cp == 0) {
                String code1 = vo1.getTax_code() == null ? "999" : vo1.getTax_code();
                String code2 = vo2.getTax_code() == null ? "999" : vo2.getTax_code();
                cp = code1.compareTo(code2);
                if (cp == 0) {
                    String name1 = vo1.getTax_name() == null ? "" : vo1.getTax_name();
                    String name2 = vo2.getTax_name() == null ? "" : vo2.getTax_name();
                    cp = name1.compareTo(name2);
                }
            }
            return cp;
        });
    }

    @Override
    public SurtaxTemplateVO[] save(String pk_corp, SurtaxTemplateVO[] vos) throws DZFWarpException {
        for (int i = 0; i < vos.length; i++) {
            SurtaxTemplateVO vo = vos[i];
            if (StringUtil.isEmpty(vo.getTax_name())) {
                throw new BusinessException("税种不能为空");
            } else if (vo.getTax_name().length() > 20) {
                throw new BusinessException("税种名称不能超过20个字符");
            } else if (StringUtil.isEmpty(vo.getJfkm_id())) {
                throw new BusinessException("借方科目不能为空");
            } else if (StringUtil.isEmpty(vo.getDfkm_id())) {
                throw new BusinessException("贷方科目不能为空");
            } else if (vo.getTax() == null) {
                throw new BusinessException("税率不能为空");
            } else if (StringUtil.isEmpty(vo.getMemo())) {
                throw new BusinessException("摘要不能为空");
            } else if (vo.getMemo().length() > 200) {
                throw new BusinessException("摘要不能超过200个字符");
            }
            vo.setSnumber(i + 1);
            vo.setPk_corp(pk_corp);
            vo.setVdef2("Y");
        }
        SQLParameter delSp = new SQLParameter();
        delSp.addParam(pk_corp);
        String delSql = "delete from ynt_jtsj where pk_corp = ?";
        singleObjectBO.executeUpdate(delSql, delSp);
        singleObjectBO.insertVOArr(pk_corp, vos);
        updateRelatedRate(pk_corp, vos);
        return vos;
    }

    /**
     * 更新纳税信息、税费计算关联税率
     *
     * @param pk_corp
     * @param vos
     */
    private void updateRelatedRate(String pk_corp, SurtaxTemplateVO[] vos) {
        DZFDouble urbanTaxRate = null;
        DZFDouble localEduTaxRate = null;
        DZFDouble waterTaxRate = null;
        for (SurtaxTemplateVO vo: vos) {
            if (SurTaxEnum.URBAN_CONSTRUCTION_TAX.getName().equals(vo.getTax_name())) {
                urbanTaxRate = vo.getTax();
            } else if (SurTaxEnum.LOCAL_EDUCATION_SURTAX.getName().equals(vo.getTax_name())) {
                localEduTaxRate = vo.getTax();
            } else if (SurTaxEnum.LOCAL_WATER_CONSTRUCTION_FUND.getName().equals(vo.getTax_name())) {
                waterTaxRate = vo.getTax();
            }
        }
        if ((urbanTaxRate == null || urbanTaxRate.doubleValue() == 0)
                && (localEduTaxRate == null || localEduTaxRate.doubleValue() == 0)) {
            return;
        }
        if (urbanTaxRate != null || localEduTaxRate != null) {
            CorpTaxVo corpTax = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
            if (urbanTaxRate != null) {
                corpTax.setCitybuildtax(urbanTaxRate.div(100));
            }
            if (localEduTaxRate != null) {
                corpTax.setLocaleducaddtax(localEduTaxRate.div(100));
            }
            if (corpTax.getPk_corp_tax() != null) {
                singleObjectBO.update(corpTax, new String[]{"citybuildtax", "localeducaddtax"});
            } else {
                singleObjectBO.saveObject(pk_corp, corpTax);
            }
        }

        if (waterTaxRate != null) {
            SurTaxTemplate temp = getWaterTaxTemplate(pk_corp);
            if (temp == null) {
                temp = new SurTaxTemplate();
                temp.setPk_corp(pk_corp);
                temp.setPk_archive("000001000000000000000004");
                temp.setRate(waterTaxRate.div(100));
                temp.setSummary(SurTaxEnum.LOCAL_WATER_CONSTRUCTION_FUND.getName());
                singleObjectBO.saveObject(pk_corp, temp);
            } else {
                temp.setRate(waterTaxRate.div(100));
                singleObjectBO.update(temp, new String[]{"rate"});
            }
        }
    }

    /**
     * 税费计算的水利建设基金模板
     *
     * @param pk_corp
     * @return
     */
    private SurTaxTemplate getWaterTaxTemplate(String pk_corp) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam("000001000000000000000004");
        String sql = "select * from ynt_taxcal_surtax_template where pk_corp = ? and pk_archive = ? ";
        SurTaxTemplate temp = (SurTaxTemplate) singleObjectBO.executeQuery(sql, sp, new BeanProcessor(SurTaxTemplate.class));
        return temp;
    }
}
