package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.query.QueryParamVO;
import com.dzf.zxkj.common.constant.InventoryConstant;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.MllDetailVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.service.cwzb.IMllbReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iMllbReport")
public class MllbReportImpl implements IMllbReport {
    @Autowired
    private SingleObjectBO singleObjectBO = null;

//    @Autowired
//    private IParameterSetService parameterserv;

    @Override
    public List<MllDetailVO> queryMllMx(QueryParamVO queryParamvo, CorpVO loginCorpInfo, String currsp) {
        SQLParameter parameter = new SQLParameter();
        parameter.addParam(loginCorpInfo.pk_corp);
        parameter.addParam(InventoryConstant.IC_STYLE_OUT);

//        String numStr = parameterserv.queryParamterValueByCode(loginCorpInfo.pk_corp, IParameterConstants.DZF009);
//        String priceStr = parameterserv.queryParamterValueByCode(loginCorpInfo.pk_corp, IParameterConstants.DZF010);

        StringBuffer sb = new StringBuffer();
//        sb.append("select fzid,code, vname,vcode, vid, name,spec,unit,pk_corp,ROUND(sum(nnumber),"+numStr+") as cksl, ROUND(sum(xssl)/nullif(SUM(nnumber),0), "+priceStr+") as xsdj, ROUND(sum(xsjzcb)/nullif(SUM(nnumber),0), "+priceStr+") as ckdj, nvl(ROUND((sum(xssl)-sum(xsjzcb))/nullif(SUM(xssl),0), 4),0) as mll from (");
        sb.append("select fzid,code, vname,vcode, vid, name,spec,unit,pk_corp, sum(nnumber) as cksl, sum(xssl)/nullif(SUM(nnumber),0) as xsdj, sum(xsjzcb)/nullif(SUM(nnumber),0) as ckdj, nvl(ROUND((sum(xssl)-sum(xsjzcb))/nullif(SUM(xssl),0), 4),0) as mll from (");
        sb.append("select pz.FZHSX6 as fzid, fz.code,fz.name,cp.pk_corp_account as vid, CASE pz.vdirect WHEN 1 THEN pz.dfmny ELSE 0-pz.jfmny END AS xssl,case when (pz.vdirect = 0 and pz.jfmny > 0) then 0-abs(nnumber) when (pz.vdirect = 0 and pz.jfmny < 0) then abs(nnumber) when (pz.vdirect = 1 and pz.dfmny < 0) then 0-abs(nnumber) when (pz.vdirect = 1 and pz.dfmny > 0) then abs(nnumber) else nnumber end as nnumber,fz.spec, fz.unit,cp.accountname as vname,cp.accountcode as vcode,pz.nprice,pz.pk_corp, pz.xsjzcb as xsjzcb from");
        sb.append("  YNT_TZPZ_B pz LEFT JOIN ynt_fzhs_b fz ON pz.PK_CORP = fz.PK_CORP AND pz.FZHSX6 = fz.PK_AUACOUNT_B left join ynt_cpaccount cp on cp.PK_CORP_ACCOUNT = fz.kmclassify where pz.PK_CORP = ? ");
        sb.append("AND pz.VICBILLCODETYPE = ? AND nvl(pz.dr,0) = 0 AND nvl(fz.dr,0) = 0");
        if(!StringUtil.isEmpty(currsp)){
            parameter.addParam(currsp);
            sb.append("and pz.FZHSX6 = ?");
        }
        if(!StringUtil.isEmpty(queryParamvo.getXmlbid())){
            parameter.addParam(queryParamvo.getXmlbid());
            sb.append("and cp.pk_corp_account = ?");
        }

        parameter.addParam(queryParamvo.getBegindate1());
        parameter.addParam(queryParamvo.getEnddate());
        sb.append("and EXISTS (select 1 from ynt_tzpz_h h where pz.PK_TZPZ_H = h.PK_TZPZ_H and to_date(h.period,'yyyy-MM') >= to_date(?,'yyyy-MM-dd') and to_date(h.period,'yyyy-MM') <= to_date(?,'yyyy-MM-dd'))) group by code, name,spec,unit,vcode,vname,pk_corp,fzid ,vid order by code");

        return (List<MllDetailVO>) singleObjectBO.executeQuery(sb.toString(), parameter, new BeanListProcessor(MllDetailVO.class));
    }
}
