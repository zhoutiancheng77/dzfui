package com.dzf.zxkj.platform.service.tax.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.*;
import com.dzf.zxkj.platform.model.tax.CorpTaxRptVO;
import com.dzf.zxkj.platform.model.tax.SynTaxInfoVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.service.tax.ICorpTaxService;
import com.dzf.zxkj.platform.service.tax.ISynTaxInfoService;
import com.dzf.zxkj.secret.CorpSecretUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("sys_synTaxInfoImple")
@Slf4j
public class SynTaxInfoServiceImpl implements ISynTaxInfoService {
    @Autowired
    private SingleObjectBO singleObjectBO = null;
    @Autowired
    private IUserService iuserService;
    @Autowired
    private ICorpService corpService;
    @Autowired
    private ICorpTaxService corpTaxService;

    @Override
    public void updateTaxCorpVos(UserVO uservo, SynTaxInfoVO[] corps) throws DZFWarpException {
        CorpTaxVo cvo = null;
        CorpVO corp = null;
        HashMap<String, AreaVO> map = this.queryBaoshuiAreaVos();
        HashMap<String, Integer> mapKjzc = new HashMap();
        mapKjzc.put("企业会计准则", 0);
        mapKjzc.put("小企业会计准则", 1);
        mapKjzc.put("企业会计制度", 2);
        mapKjzc.put("事业单位会计准则", 3);
        mapKjzc.put("民间非营利组织会计制度", 4);
        mapKjzc.put("企业会计准则商业银行", 5);
        mapKjzc.put("企业会计准则保险", 6);
        mapKjzc.put("企业会计准则证券", 7);
        if (corps != null && corps.length > 0) {
            ArrayList<String> listField = new ArrayList();
            ArrayList<String> clistField = new ArrayList();
            Set<String> set = new HashSet();
            Set<String> powset = this.queryPowerSet(uservo.getPrimaryKey());
            for (SynTaxInfoVO infovo : corps) {
                listField.clear();
                clistField.clear();
                if (!this.checkAuthority(powset, infovo.getCorpid())) {
                    throw new BusinessException("对不起，您无操作权限!");
                }

                // corp = CorpCache.getInstance().get((String)null, infovo.getCorpid());
                corp = corpService.queryByPk(infovo.getCorpid());

                cvo = this.corpTaxService.queryCorpTaxVO(infovo.getCorpid());
                cvo.setPk_corp(infovo.getCorpid());
                listField.add("pk_corp");
                if (!StringUtil.isEmpty(infovo.getLegalbodycode())) {
                    corp.setLegalbodycode(infovo.getLegalbodycode());
                    clistField.add("legalbodycode");
                }

                if (!StringUtil.isEmpty(infovo.getVcorporatephone())) {
                    corp.setVcorporatephone(CorpSecretUtil.enCode(infovo.getVcorporatephone()));
                    clistField.add("vcorporatephone");
                }

                if (!StringUtil.isEmpty(infovo.getTax_area())) {
                    cvo.setTax_area(((AreaVO) map.get(infovo.getTax_area())).getRegion_id());
                    listField.add("tax_area");
                }

                if (!StringUtil.isEmpty(infovo.getVsoccrecode())) {
                    corp.setVsoccrecode(infovo.getVsoccrecode());
                    clistField.add("vsoccrecode");
                }

                cvo.setVstatetaxpwd(infovo.getVstatetaxpwd());
                listField.add("vstatetaxpwd");
                cvo.setVlocaltaxpwd(infovo.getVlocaltaxpwd());
                listField.add("vlocaltaxpwd");
                if (!StringUtil.isEmpty(infovo.getKjzc())) {
                    cvo.setIkjzc((Integer) mapKjzc.get(infovo.getKjzc()));
                    listField.add("ikjzc");
                }

                if (!StringUtil.isEmpty(infovo.getChargedeptname())) {
                    corp.setChargedeptname(infovo.getChargedeptname());
                    clistField.add("chargedeptname");
                }

                if (!StringUtil.isEmpty(infovo.getSdsbsjg())) {
                    if (infovo.getSdsbsjg().equals("国税局")) {
                        cvo.setIsdsbsjg(0);
                    } else if (infovo.getSdsbsjg().equals("地税局")) {
                        cvo.setIsdsbsjg(1);
                    }

                    listField.add("isdsbsjg");
                }

                if (!StringUtil.isEmpty(infovo.getVcompcode())) {
                    cvo.setVcompcode(infovo.getVcompcode());
                    listField.add("vcompcode");
                }

                cvo.setVstateuname(infovo.getVstateuname());
                listField.add("vstateuname");
                cvo.setVlocaluname(infovo.getVlocaluname());
                listField.add("vlocaluname");
                cvo.setVpersonalpwd(infovo.getVpersonalpwd());
                listField.add("vpersonalpwd");
                if (!StringUtil.isEmpty(infovo.getDef16())) {
                    cvo.setDef16(infovo.getDef16());
                    listField.add("def16");
                    corp.setDef16(infovo.getDef16());
                    clistField.add("def16");
                }

                if (!StringUtil.isEmpty(infovo.getVllogintype())) {
                    if ("CFCA证书".equals(infovo.getVllogintype())) {
                        cvo.setVllogintype("0");
                    } else if ("法人一证通".equals(infovo.getVllogintype())) {
                        cvo.setVllogintype("1");
                    } else if ("用户名密码".equals(infovo.getVllogintype())) {
                        cvo.setVllogintype("2");
                    } else {
                        cvo.setVllogintype(infovo.getVllogintype());
                    }

                    listField.add("vllogintype");
                }

                cvo.setUkeypwd(infovo.getUkeypwd());
                listField.add("ukeypwd");
                if (!StringUtil.isEmpty(infovo.getIdnumber())) {
                    cvo.setIdnumber(infovo.getIdnumber());
                    listField.add("idnumber");
                }

                if (listField != null && listField.size() > 0) {
                    cvo.setIsmaintainedtax(DZFBoolean.TRUE);
                    listField.add("ismaintainedtax");
                    cvo.setCitybuildtax(new DZFDouble(0.07D));
                    listField.add("citybuildtax");
                    if (StringUtil.isEmptyWithTrim(cvo.getPk_corp_tax())) {
                        this.singleObjectBO.insertVO(cvo.getPk_corp(), cvo);
                    } else {
                        this.singleObjectBO.update(cvo, (String[]) listField.toArray(new String[listField.size()]));
                    }
                }

                if (clistField != null && clistField.size() > 0) {
                    this.singleObjectBO.update(corp, (String[]) clistField.toArray(new String[clistField.size()]));
                    // CorpCache.getInstance().remove(infovo.getCorpid());
                }

                set.add(infovo.getCorpid());
            }

            this.saveBatTaxTypeList(set);
        }

    }

    private HashMap<String, AreaVO> queryBaoshuiAreaVos() {
        SQLParameter sp = new SQLParameter();
        sp.addParam("Y");
        AreaVO[] vos = (AreaVO[])this.singleObjectBO.queryByCondition(AreaVO.class, " nvl(dr,0) = 0 and isbaoshui  = ? ", sp);
        HashMap<String, AreaVO> map = new HashMap();
        for (AreaVO vo : vos) {
            map.put(vo.getRegion_name(), vo);
        }

        return map;
    }

    private Set<String> queryPowerSet(String userid) {
        if (StringUtil.isEmpty(userid)) {
            return new HashSet();
        } else {
            Set<String> corps = this.iuserService.querypowercorpSet(userid);
            if (corps == null) {
                corps = new HashSet();
            }

            return (Set)corps;
        }
    }

    private boolean checkAuthority(Set<String> corps, String pk_corp) {
        return corps != null && !StringUtil.isEmpty(pk_corp) && corps.contains(pk_corp);
    }

    private void saveBatTaxTypeList(Set<String> set) {
        if (set != null && set.size() != 0) {
            for (String pk_corp : set) {
                // CorpVO pvo = CorpCache.getInstance().get((String)null, pk_corp);
                CorpVO pvo = corpService.queryByPk(pk_corp);
                if (pvo != null) {
                    this.corpTaxService.saveInitCorpRptVOs(pvo);
                }
            }

        }
    }

    @Override
    public void updateTaxCorpBodys(String loginCorp, UserVO uservo, SynTaxInfoVO[] corps) throws DZFWarpException {
        if (corps != null && corps.length != 0) {
            Set<String> powset = this.queryPowerSet(uservo.getPrimaryKey());
            for (SynTaxInfoVO svo : corps) {
                String corpid = svo.getCorpid();
                if (!this.checkAuthority(powset, corpid)) {
                    throw new BusinessException("对不起，您无操作权限!");
                }

                if (StringUtil.isEmpty(corpid)) {
                    throw new BusinessException("客户ID不能为空");
                }

                String sql;
                SQLParameter sp;
                if (svo.getIspersonal() != null && svo.getIspersonal().booleanValue()) {
                    sql = "update bd_corp set ispersonal = 'Y' where pk_corp = ?";
                    sp = new SQLParameter();
                    sp.addParam(corpid);
                    this.singleObjectBO.executeUpdate(sql, sp);
                } else if (svo.getIspersonal() != null && !svo.getIspersonal().booleanValue()) {
                    sql = "update bd_corp set ispersonal = 'N' where pk_corp = ?";
                    sp = new SQLParameter();
                    sp.addParam(corpid);
                    this.singleObjectBO.executeUpdate(sql, sp);
                }

                List<String> selnos = svo.getSelno();
                if (selnos != null && selnos.size() > 0) {
                    this.deleteTaxrptemplet(corpid, selnos);
                }

                List<String> selyess = svo.getSelyes();
                if (selyess != null && selyess.size() > 0) {
                    this.deleteTaxrptemplet(corpid, selyess);
                    this.insertTaxrptemplet(corpid, selyess);
                }
            }

        }
    }

    private void deleteTaxrptemplet(String corpid, List<String> idlist) {
        if (!StringUtil.isEmpty(corpid) && idlist != null && idlist.size() != 0) {
            String sqlIn = SqlUtil.buildSqlConditionForIn((String[])idlist.toArray(new String[0]));
            String sql = " delete from ynt_taxrpt where pk_corp = ? and nvl(dr,0) = 0 and pk_taxrpttemplet in (" + sqlIn + ")";
            SQLParameter sp = new SQLParameter();
            sp.addParam(corpid);
            this.singleObjectBO.executeUpdate(sql, sp);
        }
    }

    private void insertTaxrptemplet(String corpid, List<String> idlist) {
        if (!StringUtil.isEmpty(corpid) && idlist != null && idlist.size() != 0) {
            List<TaxRptTempletVO> tempvos = this.queryTempletvo((String[])idlist.toArray(new String[0]));
            this.saveTaxRptTempletVO(tempvos, corpid);
        }
    }

    private void saveTaxRptTempletVO(List<TaxRptTempletVO> tempvos, String corpid) {
        if (tempvos != null && tempvos.size() != 0) {
            List<CorpTaxRptVO> list = new ArrayList();
            for (TaxRptTempletVO rptvo : tempvos) {
                CorpTaxRptVO taxvo = new CorpTaxRptVO();
                taxvo.setPk_taxrpttemplet(rptvo.getPk_taxrpttemplet());
                taxvo.setTaxrptcode(rptvo.getReportcode());
                taxvo.setTaxrptname(rptvo.getReportname());
                taxvo.setPk_corp(corpid);
                list.add(taxvo);
            }

            this.singleObjectBO.insertVOArr(corpid, (SuperVO[])list.toArray(new CorpTaxRptVO[0]));
        }
    }

    private List<TaxRptTempletVO> queryTempletvo(String[] addarray) {
        String ina = SqlUtil.buildSqlConditionForIn(addarray);
        String strsql = " select * from ynt_taxrpttemplet where  nvl(dr,0) = 0 and pk_taxrpttemplet in (" + ina + ")";
        List<TaxRptTempletVO> list = (List)this.singleObjectBO.executeQuery(strsql, (SQLParameter)null, new BeanListProcessor(TaxRptTempletVO.class));
        return list;
    }
}
