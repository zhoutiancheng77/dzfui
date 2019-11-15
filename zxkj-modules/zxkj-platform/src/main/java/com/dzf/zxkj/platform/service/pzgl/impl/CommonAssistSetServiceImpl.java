package com.dzf.zxkj.platform.service.pzgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.CommonAssistVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.pzgl.ICommonAssistSetService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("gl_cyfzhsserv")
public class CommonAssistSetServiceImpl implements ICommonAssistSetService {
    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private IAuxiliaryAccountService gl_fzhsserv;
    @Autowired
    private ICorpService corpService;

    @Override
    public List<CommonAssistVO> query(String pk_corp, boolean hasAssistData)
            throws DZFWarpException {
        StringBuilder sql = new StringBuilder();
        sql.append(
                "select ass.*, acc.isfzhs, acc.isnum, acc.accountcode as code from ynt_commonassist ass")
                .append(" left join ynt_cpaccount acc ")
                .append(" on acc.pk_corp_account = ass.pk_accsubj")
                .append(" where ass.pk_corp = ? and nvl(acc.dr,0) = 0 and nvl(ass.dr,0) = 0");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        List<CommonAssistVO> vos = (List<CommonAssistVO>) singleObjectBO
                .executeQuery(sql.toString(), sp, new BeanListProcessor(
                        CommonAssistVO.class));
        if (vos != null && vos.size() > 0) {

            dealAssistData(vos, pk_corp, hasAssistData);
        }
        return vos;
    }

    /**
     * 过滤垃圾数据，添加辅助数据
     *
     * @param vos
     * @param pk_corp
     * @param hasAssistData
     */
    private void dealAssistData(List<CommonAssistVO> vos, String pk_corp,
                                boolean hasAssistData) {
        CorpVO corpVO = corpService.queryByPk(pk_corp);
        Map<String, AuxiliaryAccountBVO> assistMap = gl_fzhsserv.queryMap(pk_corp);
        boolean isic = IcCostStyle.IC_ON.equals(corpVO.getBbuildic());
        Iterator<CommonAssistVO> it = vos.iterator();
        List<AuxiliaryAccountBVO> assistData = null;
        top:
        while (it.hasNext()) {
            CommonAssistVO vo = it.next();
            String isfzhs = null;
            if (!StringUtils.isEmpty(vo.getIsfzhs())) {
                isfzhs = vo.getIsfzhs();
            } else {
                isfzhs = "0000000000";
            }
            if ("0000000000".equals(isfzhs)) {
                it.remove();
                continue;
            }
            if (hasAssistData) {
                assistData = new ArrayList<AuxiliaryAccountBVO>();
                vo.setAssistData(assistData);
            }
            StringBuilder code = new StringBuilder();
            if (!StringUtils.isEmpty(vo.getCode())) {
                code.append(vo.getCode());
            }
            char[] flags = isfzhs.toCharArray();
            for (int i = 0; i < flags.length; i++) {
                char flag = flags[i];
                if (i == 5 && isic && "Y".equals(vo.getIsnum())) {
                    flag = '1';
                    flags[i] = flag;
                }
                String key = "fzhsx" + (i + 1);
                String assistId = (String) vo.getAttributeValue(key);
                if (flag == '1') {
                    if (StringUtils.isEmpty(assistId)
                            || !assistMap.containsKey(assistId)) {
                        // 垃圾数据，移除
                        it.remove();
                        continue top;
                    }
                    AuxiliaryAccountBVO assistVO = assistMap.get(assistId);
                    if (assistVO.getSffc() != null && assistVO.getSffc() == 1) {
                        it.remove();
                        continue top;
                    }

                    code.append("_").append(assistVO.getCode());
                    if (hasAssistData) {
                        assistData.add(assistVO);
                    }
                } else {
                    if (!StringUtils.isEmpty(assistId)) {
                        // 垃圾数据，移除
                        it.remove();
                        continue top;
                    }
                    vo.setAttributeValue(key, null);
                }
            }
            vo.setIsfzhs(String.valueOf(flags));
            vo.setCode(code.toString());
        }
        // 按编码排序
        Collections.sort(vos, new Comparator<CommonAssistVO>() {
            @Override
            public int compare(CommonAssistVO o1, CommonAssistVO o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });
    }

    @Override
    public CommonAssistVO save(CommonAssistVO vo) throws DZFWarpException {
        if (!checkExist(vo)) {
            singleObjectBO.saveObject(vo.getPk_corp(), vo);
        }
        return vo;
    }

    @Override
    public void delete(CommonAssistVO vo) throws DZFWarpException {
        StringBuilder sql = new StringBuilder();
        sql.append("update ynt_commonassist set dr = 1 where pk_corp = ? and nvl(dr, 0) = 0 and pk_accsubj = ? ");
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_corp());
        sp.addParam(vo.getPk_accsubj());
        if (StringUtils.isEmpty(vo.getPk_tax_item())) {
            sql.append(" and pk_tax_item is null ");
        } else {
            sql.append(" and pk_tax_item = ? ");
            sp.addParam(vo.getPk_tax_item());
        }
        for (int i = 1; i <= 10; i++) {
            String key = "fzhsx" + i;
            String assistId = (String) vo.getAttributeValue(key);
            if (!StringUtils.isEmpty(assistId)) {
                sql.append(" and ").append(key).append(" = ? ");
                sp.addParam(assistId);
            }
        }
        singleObjectBO.executeUpdate(sql.toString(), sp);
    }

    @Override
    public boolean checkExist(CommonAssistVO vo) throws DZFWarpException {
        StringBuilder sql = new StringBuilder();
        sql.append("select 1 from ynt_commonassist where pk_corp = ? and nvl(dr, 0) = 0 and pk_accsubj = ? ");
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_corp());
        sp.addParam(vo.getPk_accsubj());
        if (StringUtils.isEmpty(vo.getPk_tax_item())) {
            sql.append(" and pk_tax_item is null ");
        } else {
            sql.append(" and pk_tax_item = ? ");
            sp.addParam(vo.getPk_tax_item());
        }
        for (int i = 1; i <= 10; i++) {
            String key = "fzhsx" + i;
            String assistId = (String) vo.getAttributeValue(key);
            if (!StringUtils.isEmpty(assistId)) {
                sql.append(" and ").append(key).append(" = ? ");
                sp.addParam(assistId);
            }
        }
        return singleObjectBO.isExists(vo.getPk_corp(), sql.toString(), sp);
    }
}