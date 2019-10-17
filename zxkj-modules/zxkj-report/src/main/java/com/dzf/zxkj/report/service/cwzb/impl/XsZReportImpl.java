package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.XsZVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwzb.IXsZReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

/**
 * 序时账
 * @author JasonLiu
 *
 */
@Service("gl_rep_xszserv")
public class XsZReportImpl implements IXsZReport {

	@Autowired
	private SingleObjectBO singleObjectBO = null;

	@Reference(version = "1.0.0")
	private IZxkjPlatformService zxkjPlatformService;

	
	public XsZVO[] getXSZVOs(String pk_corp , String kms , String kmsx , String zdr , String shr, KmReoprtQueryParamVO queryvo) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		
		if(queryvo.getBegindate1() == null || queryvo.getEnddate() ==null){
			throw new BusinessException("开始日期，结束日期不能为空!");
		}
		
		if(queryvo.getBegindate1().after(queryvo.getEnddate())){
			throw new BusinessException("开始日期应在结束日期之前!");
		}
		StringBuffer whereSql = new StringBuffer() ;
		if(!StringUtil.isEmptyWithTrim(zdr)){
			whereSql.append(" and coperatorid= ? ") ;
			sp.addParam(zdr);
		}
		if(!StringUtil.isEmptyWithTrim(shr)){
			whereSql.append(" and vapproveid= ?  ") ;
			sp.addParam(shr);
		}
		if(queryvo.getIshasjz()==null||!queryvo.getIshasjz().booleanValue()){
			//不包含未记账的凭证
			whereSql.append(" and nvl(ishasjz,'N')='Y' ") ;			
		}
		
		if(!StringUtil.isEmptyWithTrim(queryvo.getPk_currency())){
			whereSql.append(" and b.pk_currency= ? ") ;
			sp.addParam(queryvo.getPk_currency());
		}
		//按科目区间查询
		if(null != queryvo.getKms_first() && queryvo.getKms_first().length() > 0) {
			whereSql.append(" and km.accountcode >= ? ")  ;
			sp.addParam(queryvo.getKms_first());
		} 
		if(null != queryvo.getKms_last() && queryvo.getKms_last().length() > 0) {
			whereSql.append(" and km.accountcode <= ? ") ;
			sp.addParam(queryvo.getKms_last());
		}
		if(!StringUtil.isEmpty(queryvo.getZy())){
			whereSql.append(" and b.zy like ? ");
			sp.addParam("%"+queryvo.getZy()+"%");
		}
		if (!StringUtil.isEmpty(queryvo.getMinmny())  ){
			whereSql.append(" and  (nvl(b.jfmny,0)+nvl(b.dfmny,0)) >= ?   ");
			sp.addParam(queryvo.getMinmny());
		}
		if( !StringUtil.isEmpty( queryvo.getMaxmny() )){
			whereSql.append(" and (nvl(b.jfmny,0)+nvl(b.dfmny,0)) <= ?   ");
			sp.addParam(queryvo.getMaxmny());
		}
		
		GxhszVO myselfset = zxkjPlatformService.queryGxhszVOByPkCorp(pk_corp);//个性化设置vo
		
		Integer subjectShow  = myselfset.getSubjectShow();//科目现在方式，默认显示本级
		
		YntCpaccountVO[] cpavos =  zxkjPlatformService.queryByPk(pk_corp);
		
		Map<String, String> resultmap =  ReportUtil.getLevelKmName(cpavos, subjectShow);
		
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select h.pk_tzpz_h , km.pk_corp_account as pk_accsubj , '记' as pzz ,substr(h.doperatedate,6,2) as qj,substr(h.doperatedate,1,4) as  year, ");
		sql.append(" h.doperatedate as rq ,h.pzh,b.zy,km.accountcode as kmbm,");
		sql.append(" b.nrate  as hl ,b.ybjfmny   as ybjf,  b.ybdfmny as ybdf  , t.currencyname as bz , ");
		sql.append(" h.pk_corp as pk_corp, ");
		sql.append(" km.accountname as kmmc,km.fullname as fullkmmc, km.direction as fx,b.dfmny,b.jfmny from ynt_tzpz_h h  ");
		sql.append(" inner join ynt_tzpz_b b on  h.pk_tzpz_h=b.pk_tzpz_h  ");
		sql.append(" inner join ynt_cpaccount km on b.pk_accsubj=km.pk_corp_account ");
		sql.append(" left join ynt_bd_currency t  on t.pk_currency = b.pk_currency ");
		sql.append("  where nvl(b.dr,0)=0 ");
		sql.append(whereSql.toString());
		sql.append(" and h.pk_corp= ? ");
		sql.append(" and h.doperatedate>=? and h.doperatedate<= ?     ") ;
		sql.append(" order by h.doperatedate  ,h.pzh  , b.rowno ");
		sp.addParam(pk_corp);
		sp.addParam(queryvo.getBegindate1().toString());
		sp.addParam(queryvo.getEnddate().toString());
		
		ArrayList<XsZVO> al = (ArrayList<XsZVO>)singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(XsZVO.class)) ;
		
		if(al!=null&&!al.isEmpty()){
			XsZVO[] vos = al.toArray(new XsZVO[0]) ;
			for(XsZVO vo:vos){
				String kmmc = resultmap.get(vo.getKmbm());
				vo.setKmmc(!StringUtil.isEmpty(kmmc) ?kmmc:vo.getKmmc());
				if(vo.getDfmny()!=null&&vo.getDfmny().doubleValue()!=0){
					/** 贷方有金额 */
					vo.setFx("贷方") ;
					vo.setJe(vo.getDfmny()) ;
				}else if(vo.getJfmny()!=null&&vo.getJfmny().doubleValue()!=0){
					vo.setFx("借方") ;
					vo.setJe(vo.getJfmny()) ;
				}
				/** 如果币种是空则不显示 */
				if(StringUtil.isEmpty(vo.getBz()) || "人民币".equals(vo.getBz())){
					vo.setBz("人民币");
					vo.setHl(new DZFDouble(1));
				}
				
				/** 如果汇率是空或者1，则原币不显示 */
				if(vo.getHl().doubleValue() == 1){
					vo.setYbjf(vo.getJfmny());
					vo.setYbdf(vo.getDfmny());
				}
			}
			return vos ;
		}
		
		return null ;
	}
}
