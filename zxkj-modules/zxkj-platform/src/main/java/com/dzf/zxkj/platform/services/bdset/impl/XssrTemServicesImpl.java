package com.dzf.zxkj.platform.services.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.XssrVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.services.bdset.IXssrTemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class XssrTemServicesImpl implements IXssrTemService {

    @Autowired
    private SingleObjectBO singleObjectBO = null;


    @SuppressWarnings("unchecked")
    public List<XssrVO> query(String pk_corp) throws DZFWarpException {

        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);

        sql.append("select a.*,t1.accountname as ysxjkmmc,t2.accountname as srlkmmc,t3.accountname as yjsfkmmc ");
        sql.append(" from ynt_xssr a ");
        sql.append(" left join ynt_cpaccount t1 on a.ysxjkm_id = t1.pk_corp_account and a.pk_corp = t1.pk_corp ");
        sql.append(" left join ynt_cpaccount t2 on a.srlkm_id = t2.pk_corp_account and a.pk_corp = t2.pk_corp ");
        sql.append(" left join ynt_cpaccount t3 on a.yjsfkm_id = t3.pk_corp_account and a.pk_corp = t3.pk_corp ");
        sql.append(" where nvl(a.dr,0) =0 and a.pk_corp = ?");

        List<XssrVO> listVO = (List<XssrVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(XssrVO.class));
        VOUtil.ascSort(listVO, new String[]{"ts"});
        return listVO;
    }


    @SuppressWarnings("unchecked")
    @Override
    public void save(XssrVO vo) throws DZFWarpException {

        String ysxjid = vo.getYsxjkm_id();
        String slrid = vo.getSrlkm_id();
        String yjsfid = vo.getYjsfkm_id();
        if (StringUtil.isEmpty(ysxjid) || StringUtil.isEmpty(slrid) || StringUtil.isEmpty(ysxjid)) {
            throw new BusinessException("请输入或选择正确的科目编码");
        }
        String[] ids = new String[]{ysxjid, slrid, yjsfid};
        String insql = SqlUtil.buildSqlForIn("pk_corp_account", ids);
        StringBuilder sb = new StringBuilder();
        sb.append("pk_corp=? and ").append(insql);
        SQLParameter params = new SQLParameter();
        params.addParam(vo.getPk_corp());
        List<YntCpaccountVO> list = (List<YntCpaccountVO>) singleObjectBO.retrieveByClause(YntCpaccountVO.class, sb.toString(), params);

        Map<String, YntCpaccountVO> map = list.stream().collect(Collectors.toMap(YntCpaccountVO::getPk_corp_account, accountvo -> accountvo, (k1, k2) -> k1));

        vo.setYsxjkmmc(map.get(ysxjid).getAccountname());
        vo.setYsxjkm_code(map.get(ysxjid).getAccountcode());
        vo.setSrlkmmc(map.get(slrid).getAccountname());
        vo.setSrlkm_code(map.get(slrid).getAccountcode());
        vo.setYjsfkmmc(map.get(yjsfid).getAccountname());
        vo.setYjsfkm_code(map.get(yjsfid).getAccountcode());

        if (StringUtil.isEmpty(vo.getPk_xssrtemplate())) {
            singleObjectBO.saveObject(vo.getPk_corp(), vo);
        } else {
            singleObjectBO.update(vo);
        }
    }


    @Override
    public void delete(XssrVO vo) throws DZFWarpException {
        vo = (XssrVO) singleObjectBO.queryByPrimaryKey(XssrVO.class, vo.getPk_xssrtemplate());
        singleObjectBO.deleteObject(vo);
    }


    @Override
    public XssrVO queryById(String id) throws DZFWarpException {
        XssrVO vo = (XssrVO) singleObjectBO.queryByPrimaryKey(XssrVO.class, id);
        return vo;
    }
}