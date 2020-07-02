package com.dzf.zxkj.report.service.pub.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.report.service.pub.IReportPubService;
import com.dzf.zxkj.secret.CorpSecretUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("gl_rep_pubser")
public class ReportPubServiceImpl implements IReportPubService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public List<CorpTaxVo> queryTaxVoByParam(String pk_corp) throws DZFWarpException {
        if (StringUtil.isEmpty(pk_corp)) {
            return new ArrayList<CorpTaxVo>();
        }
        StringBuffer qrysql = new StringBuffer();
        qrysql.append(" select b1.*,a.legalbodycode ");
        qrysql.append(" from bd_corp a ");
        qrysql.append(" left join bd_corp_tax b1 on a.pk_corp = b1.pk_corp  ");
        qrysql.append(" where nvl(a.dr,0)=0 and nvl(b1.dr,0) =0");
        qrysql.append(" and a.pk_corp = ? ");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);

        List<CorpTaxVo> vos = (List<CorpTaxVo>) singleObjectBO.executeQuery(qrysql.toString(), sp,
                new BeanListProcessor(CorpTaxVo.class));

        if (vos!=null && vos.size() > 0) {
            for (CorpTaxVo corpTaxVo: vos) {
                if (!StringUtil.isEmpty(corpTaxVo.getLegalbodycode())) {
                    corpTaxVo.setLegalbodycode(CorpSecretUtil.deCode(corpTaxVo.getLegalbodycode()));
                }
            }
        }
        return vos;
    }
}
