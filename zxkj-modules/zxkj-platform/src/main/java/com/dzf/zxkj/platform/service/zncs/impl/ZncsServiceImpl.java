package com.dzf.zxkj.platform.service.zncs.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.OcrInvoiceDetailVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.VATInComInvoiceBVO2;
import com.dzf.zxkj.platform.model.zncs.VATSaleInvoiceBVO2;
import com.dzf.zxkj.platform.model.zncs.ZncswhiteVO;
import com.dzf.zxkj.platform.service.fct.IFctpubService;
import com.dzf.zxkj.platform.service.zncs.IZncsService;
import com.dzf.zxkj.platform.util.zncs.OcrUtil;
import com.dzf.zxkj.platform.util.zncs.ZncsConst;
import com.dzf.zxkj.secret.CorpSecretUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service("zncsServiceImpl")
public class ZncsServiceImpl implements IZncsService {
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IFctpubService iFctpubService;

	@Override
	public List<CorpVO> queryWhiteListCorpVOs(String pk_corp) throws DZFWarpException {

		if (StringUtil.isEmpty(pk_corp)) return null;
		String pk_factory = iFctpubService.getAthorizeFactoryCorp(new DZFDate(), pk_corp);
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();

		sb.append("select * from zncs_whitelist where nvl(dr,0)=0 ");
		if (StringUtil.isEmpty(pk_factory)) {
			sb.append("and " + SqlUtil.buildSqlForIn("pk_corp", queryCascadeCorps(pk_corp)));
		} else {
			sb.append("and pk_corp=?");
			sp.addParam(pk_factory);
		}
		List<ZncswhiteVO> whiteList = (List<ZncswhiteVO>) singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(ZncswhiteVO.class));
		if (whiteList != null && whiteList.size() > 0) {
			sb = new StringBuffer();
			sp.clearParams();
			sp.addParam(pk_corp);
			sb.append("select * from bd_corp where nvl(dr,0)=0 and nvl(isseal,'N')='N' and fathercorp=(select fathercorp from bd_corp where pk_corp=?)");
			List<CorpVO> corpList = (List<CorpVO>) singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(CorpVO.class));
			if (corpList != null && corpList.size() > 0) {
				for (int i = 0; i < corpList.size(); i++) {
					corpList.get(i).setUnitname(CorpSecretUtil.deCode(corpList.get(i).getUnitname()));
				}
			}
			return corpList;
		}
		return null;
	}
	private String[] queryCascadeCorps(String pk_corp) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_corp))
			return null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String sql = "select pk_corp from bd_corp  start with pk_corp = ? connect by  pk_corp = prior  fathercorp and nvl(dr,0) = 0";
		List<CorpVO> list = (List<CorpVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(CorpVO.class));
		List<String> corpLost=new ArrayList<String>();
		if (list != null && list.size() >0) {
			for(int i=0;i<list.size();i++){
				if(!list.get(i).getPk_corp().equals("000001")&&!list.get(i).getPk_corp().equals(pk_corp)){
					corpLost.add(list.get(i).getPk_corp());
				}
			}
		}
		return corpLost.toArray(new String[0]);

	}
	@Override
	public List<DZFDouble> queryVATSaleInvoiceMny(String pk_corp, String period, Integer tickflag, String[] taxrate) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp)||StringUtil.isEmpty(period)||taxrate==null)return null;
		List<DZFDouble> returnList=new ArrayList<DZFDouble>();
		DZFDouble v1=new DZFDouble(0.0);
		DZFDouble v2=new DZFDouble(0.0);
		DZFDouble v3=new DZFDouble(0.0);
		DZFDouble v4=new DZFDouble(0.0);
		//2部分数据，1是一件取票、导入新增的，直接用销项清单做，2是票据上传的用票据做
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		sb.append("select a.*,b.categorycode from ynt_vatsaleinvoice_b a left outer join ynt_billcategory b on(a.pk_billcategory=b.pk_category) where nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and a.pk_vatsaleinvoice in( ");
		sb.append("select pk_vatsaleinvoice from ynt_vatsaleinvoice where nvl(dr,0)=0 and pk_corp=? and period=? and (kplx is null or  kplx=1 or  kplx=2 or  kplx=3) and vdef13 is null ");
		if(tickflag!=null){
			if(tickflag==2){
				sb.append(" and nvl(iszhuan,'N')='Y' ");
			}else if(tickflag==1){
				sb.append(" and nvl(iszhuan,'N')='N' ");
			}else if(tickflag==3){
				sb.append(" and pk_vatsaleinvoice is null ");
			}
		}
		sb.append(") and  "+SqlUtil.buildSqlForIn("a.bspsl", taxrate));
		List<VATSaleInvoiceBVO2> list1=(List<VATSaleInvoiceBVO2>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(VATSaleInvoiceBVO2.class));
		if(list1!=null&&list1.size()>0){
			for(int i=0;i<list1.size();i++){
				VATSaleInvoiceBVO2 body=list1.get(i);
				if(!StringUtil.isEmpty(body.getCategorycode())&&!body.getCategorycode().equals(ZncsConst.FLCODE_WT)&&!body.getCategorycode().equals(ZncsConst.FLCODE_WSB)){
					if(body.getCategorycode().startsWith("101011")||body.getCategorycode().startsWith("101111")||body.getCategorycode().startsWith("101112")
							||body.getCategorycode().startsWith("101113")||body.getCategorycode().startsWith("101114")){
						//劳务
						v1=v1.add(body.getBhjje()==null?DZFDouble.ZERO_DBL:body.getBhjje());
						v2=v2.add(body.getBspse()==null?DZFDouble.ZERO_DBL:body.getBspse());
					}else{
						//货物
						if(body.getCategorycode().startsWith("1012")){
							if(body.getBspse()!=null&&body.getBspse().compareTo(DZFDouble.ZERO_DBL)!=0){
								v3=v3.add(body.getBhjje()==null?DZFDouble.ZERO_DBL:body.getBhjje());
								v4=v4.add(body.getBspse()==null?DZFDouble.ZERO_DBL:body.getBspse());
							}
						}else{
							v3=v3.add(body.getBhjje()==null?DZFDouble.ZERO_DBL:body.getBhjje());
							v4=v4.add(body.getBspse()==null?DZFDouble.ZERO_DBL:body.getBspse());
						}
					}
				}
			}
		}
		sb=new StringBuffer();
		sb.append("select a.* from ynt_interface_invoice a,ynt_image_group b");
		sb.append(" where a.pk_image_group=b.pk_image_group and nvl(b.dr,0)=0 and nvl(a.dr,0)=0 and (a.errordesc2 is null or a.errordesc2 not like '%发票已作废%') and b.istate !=205 ");
		sb.append(" and  a.pk_invoice in(select vdef13 from ynt_vatsaleinvoice where nvl(dr,0)=0 and pk_corp=? and period=? and vdef13 is not null )");
		sb.append(" and a.pk_corp=? ");
		sp.addParam(pk_corp);
		sb.append(" and a.period=?");
		sp.addParam(period);
		sb.append(" and a.pk_billcategory not in(select pk_category from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and categorycode in("+ZncsConst.FLCODE_WT+","+ZncsConst.FLCODE_WSB+"))");
		sp.addParam(pk_corp);
		sp.addParam(period);
		if(tickflag!=null){
			if(tickflag==2){
				sb.append(" and (a.invoicetype like '%增值税专用发票%' or a.invoicetype like '%机动车销售统一发票%' or a.invoicetype like '%通行费增值税电子普通发票%') ");
			}else if(tickflag==1){
				sb.append(" and a.invoicetype not like '%增值税专用发票%' and a.invoicetype not like '%机动车销售统一发票%' and a.invoicetype not like '%通行费增值税电子普通发票%'");
			}else if(tickflag==3){
				sb.append(" and a.pk_invoice is null ");
			}
		}
		List<OcrInvoiceVO> list2=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceVO.class));
		if(list2!=null&&list2.size()>0){
			List<OcrInvoiceDetailVO> detailList = queryInvoiceDetail(list2,taxrate);
			if(detailList!=null&&detailList.size()>0){
				for(int i=0;i<detailList.size();i++){
					OcrInvoiceDetailVO body=detailList.get(i);
					if(!StringUtil.isEmpty(body.getCategorycode())&&!body.getCategorycode().equals(ZncsConst.FLCODE_WT)&&!body.getCategorycode().equals(ZncsConst.FLCODE_WSB)){
						if(body.getCategorycode().startsWith("101011")||body.getCategorycode().startsWith("101111")||body.getCategorycode().startsWith("101112")
								||body.getCategorycode().startsWith("101113")||body.getCategorycode().startsWith("101114")){
							//劳务
							v1=v1.add(body.getItemmny()==null?DZFDouble.ZERO_DBL:new DZFDouble(OcrUtil.turnMnyByCurrency(body.getItemmny())));
							v2=v2.add(body.getItemtaxmny()==null?DZFDouble.ZERO_DBL:new DZFDouble(OcrUtil.turnMnyByCurrency(body.getItemtaxmny())));
						}else{
							//货物
							if(body.getCategorycode().startsWith("1012")){
								if(body.getItemtaxmny()!=null&&new DZFDouble(OcrUtil.turnMnyByCurrency(body.getItemtaxmny())).compareTo(DZFDouble.ZERO_DBL)!=0){
									v3=v3.add(body.getItemmny()==null?DZFDouble.ZERO_DBL:new DZFDouble(OcrUtil.turnMnyByCurrency(body.getItemmny())));
									v4=v4.add(body.getItemtaxmny()==null?DZFDouble.ZERO_DBL:new DZFDouble(OcrUtil.turnMnyByCurrency(body.getItemtaxmny())));
								}
							}else{
								v3=v3.add(body.getItemmny()==null?DZFDouble.ZERO_DBL:new DZFDouble(OcrUtil.turnMnyByCurrency(body.getItemmny())));
								v4=v4.add(body.getItemtaxmny()==null?DZFDouble.ZERO_DBL:new DZFDouble(OcrUtil.turnMnyByCurrency(body.getItemtaxmny())));
							}
						}
					}
				}
			}
		}
		returnList.add(v1);
		returnList.add(v2);
		returnList.add(v3);
		returnList.add(v4);
		return returnList;
	}
	
	private List<OcrInvoiceDetailVO> queryInvoiceDetail(List<OcrInvoiceVO> list,String[] taxrate) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		List<String> pkList = new ArrayList<String>();
		for (OcrInvoiceVO ocrInvoiceVO : list) {
			pkList.add(ocrInvoiceVO.getPk_invoice());
		}
		sb.append("select a.*,b.categorycode from ynt_interface_invoice_detail a left outer join ynt_billcategory b on(a.pk_billcategory=b.pk_category) where nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and "+buildRateLikeSql(taxrate));
		sb.append(" and "+SqlUtil.buildSqlForIn("pk_invoice", pkList.toArray(new String[0])));
		List<OcrInvoiceDetailVO> returnList=(List<OcrInvoiceDetailVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceDetailVO.class));
		return returnList;
	}
	
	private List<OcrInvoiceDetailVO> queryInvoiceDetail(List<OcrInvoiceVO> list, String[] taxrate, Integer bs) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		List<String> pkList = new ArrayList<String>();
		for (OcrInvoiceVO ocrInvoiceVO : list) {
			pkList.add(ocrInvoiceVO.getPk_invoice());
		}
		sb.append("select a.*,b.categorycode from ynt_interface_invoice_detail a left outer join ynt_billcategory b on(a.pk_billcategory=b.pk_category) where nvl(a.dr,0)=0 and nvl(b.dr,0)=0 ");
		if(taxrate!=null&&bs!=2){
			sb.append(" and "+buildRateLikeSql(taxrate));
		}
		sb.append(" and "+SqlUtil.buildSqlForIn("pk_invoice", pkList.toArray(new String[0])));
		if(bs!=null){
			if(bs==2){
				sb.append(" and  (a.invname like '%*旅客运输*%' and (a.itemtaxrate='9%' or a.itemtaxrate='9' or a.itemtaxrate='3%' or a.itemtaxrate='3' ) or a.invname like '%*运输服务*%' or (a.invname is null and a.itemtaxrate is null)) ");
			}
		}
		List<OcrInvoiceDetailVO> returnList=(List<OcrInvoiceDetailVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceDetailVO.class));
		return returnList;
	}
	
	private String buildRateLikeSql(String[] taxrate)throws DZFWarpException {
		String sql="(";
		for (int i = 0; i < taxrate.length; i++) {
			sql+=" a.itemtaxrate like '"+taxrate[i]+"%' or";
		}
		sql=sql.substring(0, sql.length()-2)+")";
		return sql;
	}
	
	private String buildRateLikeSqlHead(String[] taxrate)throws DZFWarpException {
		String sql="(";
		for (int i = 0; i < taxrate.length; i++) {
			sql+=" a.taxrate like '"+taxrate[i]+"%' or";
		}
		sql=sql.substring(0, sql.length()-2)+")";
		return sql;
	}
	
	@Override
	public List<DZFDouble> queryVATIncomeInvoiceMny(String pk_corp, String period, Integer tickflag, String[] taxrate, Integer bs, Integer rzbs) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp)||StringUtil.isEmpty(period)||taxrate==null)return null;
		List<DZFDouble> returnList=new ArrayList<DZFDouble>();
		DZFDouble v1=new DZFDouble(0.0);
		DZFDouble v2=new DZFDouble(0.0);
		
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		if(bs==null||bs==1||bs==2){
			sb.append("select a.*,b.categorycode from ynt_vatincominvoice_b a left outer join ynt_billcategory b on(a.pk_billcategory=b.pk_category) where nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and a.pk_vatincominvoice in( ");
			sb.append("select pk_vatincominvoice from ynt_vatincominvoice where nvl(dr,0)=0 and pk_corp=? and period=? and (kplx is null or  kplx=1 or  kplx=2 or  kplx=3) and vdef13 is null ");
			if(tickflag!=null){
				if(tickflag==2){
					sb.append(" and nvl(iszhuan,'N')='Y' ");
				}else if(tickflag==1){
					sb.append(" and nvl(iszhuan,'N')='N' ");
				}else if(tickflag==3){
					sb.append(" and pk_vatincominvoice is null ");
				}
			}
			if(rzbs!=null){
				if(rzbs==0){
					sb.append(" and rzrj is not null ");
				}else if(rzbs==1){
					sb.append(" and rzrj is null ");
				}
			}
			if(bs!=null){
				if(bs==2){
					sb.append(" and kprj>'2019-03-31' ");
				}
			}
			sb.append(") and  "+SqlUtil.buildSqlForIn("a.bspsl", taxrate));
			if(bs!=null){
				if(bs==2){
					sb.append(" and ((a.bspmc like '%*旅客运输*%' and (bspsl=9 or bspsl=3)) or a.bspmc like '%*运输服务*%')");
				}
			}
			List<VATInComInvoiceBVO2> list1=(List<VATInComInvoiceBVO2>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(VATInComInvoiceBVO2.class));
			if(list1!=null&&list1.size()>0){
				for(int i=0;i<list1.size();i++){
					VATInComInvoiceBVO2 body=list1.get(i);
					if(!StringUtil.isEmpty(body.getCategorycode())&&!body.getCategorycode().equals(ZncsConst.FLCODE_WT)&&!body.getCategorycode().equals(ZncsConst.FLCODE_WSB)){
						v1=v1.add(body.getBhjje()==null?DZFDouble.ZERO_DBL:body.getBhjje());
						v2=v2.add(body.getBspse()==null?DZFDouble.ZERO_DBL:body.getBspse());
					}
				}
			}
		}
		sb=new StringBuffer();
		sb.append("select a.* from ynt_interface_invoice a,ynt_image_group b");
		sb.append(" where a.pk_image_group=b.pk_image_group and nvl(b.dr,0)=0 and nvl(a.dr,0)=0 and (a.errordesc2 is null or a.errordesc2 not like '%发票已作废%') and b.istate !=205 ");
		sb.append(" and  (a.pk_invoice in(select vdef13 from ynt_vatincominvoice where nvl(dr,0)=0 and pk_corp=? and period=? and vdef13 is not null ");
		if(rzbs!=null){
			if(rzbs==0){
				sb.append(" and rzrj is not null ");
			}else if(rzbs==1){
				sb.append(" and rzrj is null ");
			}
		}
		sb.append(" )  ");
		if(bs==null||bs==1||bs==2){
			sb.append("  or (invoicetype like '%火车票%' or invoicetype like '%航空运输电子客票行程单%' or (invoicetype like '%汽车客票%' and staffname is not null) or (invoicetype like '%水路运输客票%' and staffname is not null)) ");
		}
		sb.append(") ");
		sb.append(" and a.pk_corp=? ");
		sp.addParam(pk_corp);
		sb.append(" and a.period=?");
		sp.addParam(period);
		sb.append(" and a.pk_billcategory not in(select pk_category from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and categorycode in("+ZncsConst.FLCODE_WT+","+ZncsConst.FLCODE_WSB+"))");
		sp.addParam(pk_corp);
		sp.addParam(period);
		if(tickflag!=null){
			if(tickflag==2){
				sb.append(" and (a.invoicetype like '%增值税专用发票%' or a.invoicetype like '%机动车销售统一发票%' or a.invoicetype like '%通行费增值税电子普通发票%') ");
			}else if(tickflag==1){
				sb.append(" and a.invoicetype not like '%增值税专用发票%' and a.invoicetype not like '%机动车销售统一发票%' and a.invoicetype not like '%通行费增值税电子普通发票%'");
			}else if(tickflag==3){
				sb.append(" and a.pk_invoice is null ");
			}
		}
		if(bs!=null){
			if(bs==3){
				sb.append(" and a.vmemo like '%收购(左上角标志)%' ");
			}
			if(bs==2){
				sb.append(" and (a.dinvoicedate is not null and (replace(replace(replace(a.dinvoicedate,'年',''),'月',''),'日','')>'20190331'))");
			}
		}
		List<OcrInvoiceVO> list2=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceVO.class));
		if(list2!=null&&list2.size()>0){
			List<OcrInvoiceDetailVO> detailList = queryInvoiceDetail(list2,taxrate,bs);
			if(detailList!=null&&detailList.size()>0){
				for(int i=0;i<detailList.size();i++){
					OcrInvoiceDetailVO body=detailList.get(i);
					if(!StringUtil.isEmpty(body.getCategorycode())&&!body.getCategorycode().equals(ZncsConst.FLCODE_WT)&&!body.getCategorycode().equals(ZncsConst.FLCODE_WSB)){
						v1=v1.add(body.getItemmny()==null?DZFDouble.ZERO_DBL:new DZFDouble(OcrUtil.turnMnyByCurrency(body.getItemmny())));
						v2=v2.add(body.getItemtaxmny()==null?DZFDouble.ZERO_DBL:new DZFDouble(OcrUtil.turnMnyByCurrency(body.getItemtaxmny())));
					}
				}
			}
			for(int i=0;i<list2.size();i++){
				String invoicetype=list2.get(i).getInvoicetype();
				if(!StringUtil.isEmpty(invoicetype)&&(invoicetype.indexOf("火车票")>-1||invoicetype.indexOf("航空运输电子客票行程单")>-1||invoicetype.indexOf("汽车客票")>-1||invoicetype.indexOf("水路运输客票")>-1)){
					v1=v1.add(StringUtil.isEmpty(list2.get(i).getNmny())?DZFDouble.ZERO_DBL:new DZFDouble(OcrUtil.turnMnyByCurrency(list2.get(i).getNmny())));
					v2=v2.add(StringUtil.isEmpty(list2.get(i).getNtaxnmny())?DZFDouble.ZERO_DBL:new DZFDouble(OcrUtil.turnMnyByCurrency(list2.get(i).getNtaxnmny())));
				}
			}
		}
		returnList.add(v1);
		returnList.add(v2);
		return returnList;
	}
	@Override
	public DZFDouble queryVATIncomeInvoiceNumber(String pk_corp, String period, Integer tickflag, Integer bs, Integer rzbs) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp)||StringUtil.isEmpty(period))return null;
		List<DZFDouble> returnList=new ArrayList<DZFDouble>();
		DZFDouble v1=new DZFDouble(0);
		
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		if(bs==null||bs==1||bs==2){
			sb.append("select a.pk_vatincominvoice,b.categorycode from ynt_vatincominvoice_b a left outer join ynt_billcategory b on(a.pk_billcategory=b.pk_category) where nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and a.pk_vatincominvoice in( ");
			sb.append("select pk_vatincominvoice from ynt_vatincominvoice where nvl(dr,0)=0 and pk_corp=? and period=? and (kplx is null or  kplx=1 or  kplx=2 or  kplx=3) and vdef13 is null ");
			if(tickflag!=null){
				if(tickflag==2){
					sb.append(" and nvl(iszhuan,'N')='Y' ");
				}else if(tickflag==1){
					sb.append(" and nvl(iszhuan,'N')='N' ");
				}else if(tickflag==3){
					sb.append(" and pk_vatincominvoice is null ");
				}
			}
			if(rzbs!=null){
				if(rzbs==0){
					sb.append(" and rzrj is not null ");
				}else if(rzbs==1){
					sb.append(" and rzrj is null ");
				}
			}
			if(bs!=null){
				if(bs==2){
					sb.append(" and kprj>'2019-03-31' ");
				}
			}
			sb.append(" ) ") ;
			if(bs!=null){
				if(bs==2){
					sb.append(" and ((a.bspmc like '%*旅客运输*%' and (bspsl=9 or bspsl=3)) or a.bspmc like '%*运输服务*%')");
				}
			}
			
			List<VATInComInvoiceBVO2> list1=(List<VATInComInvoiceBVO2>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(VATInComInvoiceBVO2.class));
			if(list1!=null&&list1.size()>0){
				Set<String> tmpSet=new HashSet<String>();
				for(int i=0;i<list1.size();i++){
					VATInComInvoiceBVO2 body=list1.get(i);
					if(!StringUtil.isEmpty(body.getCategorycode())&&!body.getCategorycode().equals(ZncsConst.FLCODE_WT)&&!body.getCategorycode().equals(ZncsConst.FLCODE_WSB)){
						if(!tmpSet.contains(body.getPk_vatincominvoice())){
							tmpSet.add(body.getPk_vatincominvoice());
						}
					}
				}
				v1=v1.add(tmpSet.size());
			}
		}
		sb=new StringBuffer();
		sb.append("select a.* from ynt_interface_invoice a,ynt_image_group b");
		sb.append(" where a.pk_image_group=b.pk_image_group and nvl(b.dr,0)=0 and nvl(a.dr,0)=0 and (a.errordesc2 is null or a.errordesc2 not like '%发票已作废%') and b.istate !=205 ");
		sb.append(" and  (a.pk_invoice in(select vdef13 from ynt_vatincominvoice where nvl(dr,0)=0 and pk_corp=? and period=? and vdef13 is not null ");
		if(rzbs!=null){
			if(rzbs==0){
				sb.append(" and rzrj is not null ");
			}else if(rzbs==1){
				sb.append(" and rzrj is null ");
			}
		}
		sb.append(" )  ");
		if(bs==null||bs==1||bs==2){
			sb.append("  or (invoicetype like '%火车票%' or invoicetype like '%航空运输电子客票行程单%' or (invoicetype like '%汽车客票%' and staffname is not null)  or (invoicetype like '%水路运输客票%' and staffname is not null)) ");
		}
		sb.append(") ");
		sb.append(" and a.pk_corp=? ");
		sp.addParam(pk_corp);
		sb.append(" and a.period=?");
		sp.addParam(period);
		sb.append(" and a.pk_billcategory not in(select pk_category from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and categorycode in("+ZncsConst.FLCODE_WT+","+ZncsConst.FLCODE_WSB+"))");
		sp.addParam(pk_corp);
		sp.addParam(period);
		if(tickflag!=null){
			if(tickflag==2){
				sb.append(" and (a.invoicetype like '%增值税专用发票%' or a.invoicetype like '%机动车销售统一发票%' or a.invoicetype like '%通行费增值税电子普通发票%') ");
			}else if(tickflag==1){
				sb.append(" and a.invoicetype not like '%增值税专用发票%' and a.invoicetype not like '%机动车销售统一发票%' and a.invoicetype not like '%通行费增值税电子普通发票%'");
			}else if(tickflag==3){
				sb.append(" and a.pk_invoice is null ");
			}
		}
		if(bs!=null){
			if(bs==3){
				sb.append(" and a.vmemo like '%收购(左上角标志)%' ");
			}
			if(bs==2){
				sb.append(" and (a.dinvoicedate is not null and (replace(replace(replace(a.dinvoicedate,'年',''),'月',''),'日','')>'20190331'))");
			}
		}
		List<OcrInvoiceVO> list2=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceVO.class));
		if(list2!=null&&list2.size()>0){
			List<OcrInvoiceDetailVO> detailList = queryInvoiceDetail(list2,null,bs);
			Set<String> headKeySet=new HashSet<String>();
			if(detailList!=null&&detailList.size()>0){
				for(int i=0;i<detailList.size();i++){
					OcrInvoiceDetailVO body=detailList.get(i);
					if(!StringUtil.isEmpty(body.getCategorycode())&&!body.getCategorycode().equals(ZncsConst.FLCODE_WT)&&!body.getCategorycode().equals(ZncsConst.FLCODE_WSB)){
						if(!headKeySet.contains(body.getPk_invoice())){
							headKeySet.add(body.getPk_invoice());
						}
					}
				}
			}
			v1=v1.add(headKeySet.size());
		}
		return v1;
	}
}
