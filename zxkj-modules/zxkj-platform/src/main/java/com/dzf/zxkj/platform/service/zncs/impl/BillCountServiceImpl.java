package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.zncs.*;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.zncs.IBillCountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BillCountServiceImpl implements IBillCountService {

	@Autowired
	SingleObjectBO singleObjectBO ;
	@Autowired
	private ICorpService corpService;

	@Override
	public List<BillCountVO> queryList(String period, String pk_corp) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		List<InvoiceCountVO> arrayList = new ArrayList<InvoiceCountVO>();
		sb.append(" select a.pjzs as pjzs,b.ysb as ysb,b.wsb as wsb,b.yzz as yzz,b.wzz as wzz,b.yzf as yzf,b.cf as cf,b.fbgs,b.kqzbq as kqzbq,c.kqzqt as kqzqt,d.scpj as scpj from ");
		sb.append(" ( select count(1) as pjzs From ynt_image_ocrlibrary yc left join ynt_image_library yci on "
				+ " yci.pk_image_library = yc.crelationid left join ynt_image_group yg on  "
				+ " yci.pk_image_group = yg.pk_image_group   left join ynt_interface_invoice yi"
				+ " on yi.ocr_id = yc.pk_image_ocrlibrary and nvl(yi.dr,0)=0    "
				+ "  where nvl(yc.dr,0)=0 and nvl(yg.dr,0)=0 and yc.pk_corp = ?  "
				+ "  and (yi.period = ? or (yi.period is null and  yg.cvoucherdate like ? ) )  ) a , ");
		sp.addParam(pk_corp);
		sp.addParam(period);
		sp.addParam(period+"%");
		sb.append(" (select sum(case when substr(yio.cvoucherdate,0,7)!=yii.period then 1 else null end) as kqzbq ,"
				+ " sum(case when yii.istate <>'未识别票据' then 1 else null end ) as ysb,"
				+ " sum(case when yii.istate ='未识别票据' then 1 else null end ) as wsb,"
				+ " sum(case when yig.istate in (100,101) then 1 else null end ) as yzz,"
				+ " sum(case when yig.istate not in (205,100,101) then 1 else null end ) as wzz,"
				+ " sum(case when yig.istate in (205) then 1 else null end ) as yzf,"
				+ " sum(case when yii.errordesc='重复票据' and yig.istate!=205 then 1 else null end ) as cf,"
				+ " sum(case when yii.errordesc like '%收付款方与公司名称不一致%' then 1 else null end ) as fbgs "
				+ " from ynt_interface_invoice yii inner join ynt_image_group yig on yii.pk_image_group=yig.pk_image_group "
				+ " left join ynt_image_ocrlibrary yio on yii.ocr_id=yio.pk_image_ocrlibrary ");
		sb.append(" where nvl(yii.dr,0)=0 and nvl(yig.dr,0)=0 and nvl(yio.dr,0)=0 and yii.pk_corp = ? and yii.period = ? ) b , ");
		sp.addParam(pk_corp);
		sp.addParam(period);
		sb.append(" (select count(1) as kqzqt from ynt_interface_invoice yii inner join ynt_image_group yig on yii.pk_image_group=yig.pk_image_group "
				+ " left join ynt_image_ocrlibrary yio on yii.ocr_id=yio.pk_image_ocrlibrary "
				+ " where nvl(yii.dr,0)=0 and nvl(yig.dr,0)=0 and nvl(yio.dr,0)=0 and yii.pk_corp = ? and yii.period != ? and substr(yio.cvoucherdate,0,7)= ? ) c , ");
		sp.addParam(pk_corp);
		sp.addParam(period);
		sp.addParam(period);
		sb.append(" (select count(1) as scpj from ynt_interface_invoice yii inner join ynt_image_group yig on yii.pk_image_group=yig.pk_image_group "
				+ " where nvl(yii.dr,0)=0 and nvl(yig.dr,0)=0 and yii.pk_corp = ? and substr(yig.cvoucherdate,0,7)= ?) d ");
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<BillCountVO> list=(List<BillCountVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(BillCountVO.class));
		List<InvoiceCountVO> inComCount = queryInComCount(pk_corp, period);
		List<InvoiceCountVO> saleCount = querySaleCount(pk_corp, period);
		arrayList.addAll(inComCount);
		arrayList.addAll(saleCount);
		list.get(0).setInvoicelist(arrayList);
		List<CategoryCountVO> catelist = queryategory(pk_corp, period);
		list.get(0).setCategorylist(catelist);
		return list;
	}
	//进销项数据
	private List<InvoiceCountVO> queryInComCount(String pk_corp,String period)throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append(" select count(1) as zsl,sum(hjje) as je,sum(spse) as se,sum(jshj) as jshj,"
				+ " sum(case when iszhuan='Y' then 1 else null end) as zp,sum(case when iszhuan='N' or iszhuan is null then 1 else null end) as pp , "
				+ " sum(case when pk_image_group is not null then 1 else null end) as yqs,"
				+ " sum(case when pk_image_group is null then 1 else null end) as wqs"
				+ " from ynt_vatincominvoice where nvl(dr,0)=0 and pk_corp = ? and inperiod = ? ");
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<InvoiceCountVO> list=(List<InvoiceCountVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(InvoiceCountVO.class));
		list.get(0).setInvoicetype("进项发票");
		return list;
	}
	//销项数据
	private List<InvoiceCountVO> querySaleCount(String pk_corp,String period)throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append(" select count(1) as zsl,sum(hjje) as je,sum(spse) as se,sum(jshj) as jshj,"
				+ " sum(case when iszhuan='Y' then 1 else null end) as zp,sum(case when iszhuan='N' or iszhuan is null then 1 else null end) as pp , "
				+ " sum(case when pk_image_group is not null then 1 else null end) as yqs,"
				+ " sum(case when pk_image_group is null then 1 else null end) as wqs"
				+ " from ynt_vatsaleinvoice where nvl(dr,0)=0 and pk_corp = ? and inperiod = ? ");
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<InvoiceCountVO> list=(List<InvoiceCountVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(InvoiceCountVO.class));
		list.get(0).setInvoicetype("销项发票");
		return list;
	}
	//分类情况
	private List<CategoryCountVO> queryategory(String pk_corp,String period)throws DZFWarpException{
		List<InvoiceinfoVO> ictradelist= new ArrayList<InvoiceinfoVO>();
		List<InvoiceinfoVO> incomlist= new ArrayList<InvoiceinfoVO>();
		List<InvoiceinfoVO> salelist= new ArrayList<InvoiceinfoVO>();
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select yii.pk_invoice,yii.pk_billcategory,yb.categorycode,yb.categoryname,yb.isaccount "
				+ " from ynt_interface_invoice yii left join ynt_image_group yig on yii.pk_image_group=yig.pk_image_group"
				+ " left join ynt_billcategory yb on yii.pk_billcategory=yb.pk_category"
				+ " where nvl(yii.dr,0)=0 and nvl(yig.dr,0)=0 and nvl(yb.dr,0)=0 and yig.istate!=205 and yii.pk_billcategory is not null"
				+ " and yii.pk_corp = ? and yii.period=? ");
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<InvoiceinfoVO> involist=(List<InvoiceinfoVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(InvoiceinfoVO.class));
		String bbuildic = corpService.queryByPk(pk_corp).getBbuildic();//是否是库存模式
		if(IcCostStyle.IC_ON.equals(bbuildic)){
			//库存
			StringBuffer asb=new StringBuffer();
			SQLParameter asp=new SQLParameter();
			asb.append("select distinct(yii.pk_invoice),yii.pk_billcategory,yb.categorycode,yb.categoryname, yih.cbilltype,yb.isaccount "
					+ " from ynt_interface_invoice yii left join ynt_image_group yig on yii.pk_image_group=yig.pk_image_group"
					+ " left join ynt_image_ocrlibrary yio on yio.pk_image_ocrlibrary =yii.ocr_id"
					+ " left join ynt_image_library yil on yio.crelationid=yil.pk_image_library"
					+ " left join ynt_ictrade_h yih on yil.pk_image_library=yih.pk_image_library"
					+ " left join ynt_billcategory yb on yii.pk_billcategory=yb.pk_category"
					+ " where nvl(yii.dr,0)=0 and nvl(yig.dr,0)=0 and nvl(yih.dr,0)=0 and nvl(yio.dr,0)=0 and nvl(yil.dr,0)=0 and nvl(yb.dr,0)=0 and yig.istate!=205 and yii.pk_billcategory is not null"
					+ " and yii.pk_corp = ? and yii.period=? ");
			asp.addParam(pk_corp);
			asp.addParam(period);
			ictradelist=(List<InvoiceinfoVO>)singleObjectBO.executeQuery(asb.toString(), asp, new BeanListProcessor(InvoiceinfoVO.class));
				
		}else if("2".equals(bbuildic)){
			//总账核算存货  总账不核算存货
			StringBuffer isb=new StringBuffer();
			SQLParameter isp=new SQLParameter();
			//进项表中查入库
			isb.append(" select distinct(t.vicbillcode) as cbilltype,v.pk_model_h as pk_billcategory,b.categorycode as categorycode from ynt_vatincominvoice v inner join  "
					+ "ynt_tzpz_h t on v.pk_tzpz_h=t.pk_tzpz_h left join ynt_billcategory b on v.pk_model_h=b.pk_category where nvl(v.dr,0)=0 and nvl(t.dr,0)=0 and nvl(b.dr,0)=0 "
					+ "and t.vicbillcode is not null and v.imgpath is not null and v.pk_corp=? and v.inperiod=?");
			isp.addParam(pk_corp);
			isp.addParam(period);
			incomlist=(List<InvoiceinfoVO>)singleObjectBO.executeQuery(isb.toString(), isp, new BeanListProcessor(InvoiceinfoVO.class));
			//销项表中查出库
			StringBuffer ssb=new StringBuffer();
			SQLParameter ssp=new SQLParameter();
			ssb.append(" select distinct(t.vicbillcode) as cbilltype,v.pk_model_h as pk_billcategory,b.categorycode as categorycode from ynt_vatsaleinvoice v inner join  "
					+ "ynt_tzpz_h t on v.pk_tzpz_h=t.pk_tzpz_h left join ynt_billcategory b on v.pk_model_h=b.pk_category where nvl(v.dr,0)=0 and nvl(t.dr,0)=0  "
					+ "and t.vicbillcode is not null and v.imgpath is not null and v.pk_corp=? and v.inperiod=?");
			ssp.addParam(pk_corp);
			ssp.addParam(period);
			salelist=(List<InvoiceinfoVO>)singleObjectBO.executeQuery(ssb.toString(), ssp, new BeanListProcessor(InvoiceinfoVO.class));
		}
		
		StringBuffer csb=new StringBuffer();
		SQLParameter csp=new SQLParameter();
		csb.append("select yii.pk_invoice,yii.pk_billcategory,yb.categorycode,yb.categoryname,yb.isaccount,ya.pk_assetcard "
				+ " from ynt_interface_invoice yii left join ynt_image_group yig on yii.pk_image_group=yig.pk_image_group"
				+ " left join ynt_assetcard ya on yii.pk_invoice=ya.pk_invoice"
				+ " left join ynt_billcategory yb on yii.pk_billcategory=yb.pk_category"
				+ " where nvl(yii.dr,0)=0 and nvl(yig.dr,0)=0 and nvl(ya.dr,0)=0 and nvl(yb.dr,0)=0 and yig.istate!=205 and yii.pk_billcategory is not null"
				+ " and ya.pk_assetcard is not null and yii.pk_corp = ? and yii.period=? ");
		csp.addParam(pk_corp);
		csp.addParam(period);
		List<InvoiceinfoVO> zckpList=(List<InvoiceinfoVO>)singleObjectBO.executeQuery(csb.toString(), csp, new BeanListProcessor(InvoiceinfoVO.class));
		StringBuffer bsb=new StringBuffer();
		bsb.append(" select categoryname,categorycode from ynt_basecategory where nvl(dr,0)=0 and useflag = 'Y' and pk_parentbasecategory is null order by showorder ");
		List<CategoryCountVO> catelist=(List<CategoryCountVO>)singleObjectBO.executeQuery(bsb.toString(), null, new BeanListProcessor(CategoryCountVO.class));
		CategoryCountVO vo = new CategoryCountVO();//合计
		vo.setCategoryname("合计");
		for (InvoiceinfoVO ivo : involist) {
			if(vo.getZsl()==null){
				vo.setZsl(0);
			}
			vo.setZsl(vo.getZsl()+1);//合计总数量
			
			if(ivo.getIsaccount()==null||ivo.getIsaccount().equals(DZFBoolean.FALSE)){
				if(vo.getWzz()==null){
					vo.setWzz(0);
				}
				vo.setWzz(vo.getWzz()+1);//未做账
			}else{
				if(vo.getYzz()==null){
					vo.setYzz(0);
				}
				vo.setYzz(vo.getYzz()+1);//已做账
			}
		}
		for (InvoiceinfoVO ivo : zckpList) {
			if (!StringUtils.isEmpty(ivo.getPk_assetcard())) {
				if (vo.getZckp() == null) {
					vo.setZckp(0);
				}
				vo.setZckp(vo.getZckp() + 1);// 资产卡片
			}
		}
		if(IcCostStyle.IC_ON.equals(bbuildic)){
			for (InvoiceinfoVO ivo : ictradelist) {//合计出入库数量
				
				if(!StringUtils.isEmpty(ivo.getCbilltype())&&ivo.getCbilltype().equals("HP75")){//出入库标志HP70入库HP75出库
					if(vo.getCkd()==null){
						vo.setCkd(0);
					}
					vo.setCkd(vo.getCkd()+1);//出库单
				}else if(!StringUtils.isEmpty(ivo.getCbilltype())&&ivo.getCbilltype().equals("HP70")){
					if(vo.getRkd()==null){
						vo.setRkd(0);
					}
					vo.setRkd(vo.getRkd()+1);//入库单
				}
			}
		}else if("2".equals(bbuildic)){
			vo.setCkd(salelist.size()==0?null:salelist.size());
			vo.setRkd(incomlist.size()==0?null:incomlist.size());
		}
		for (CategoryCountVO cvo : catelist) {
			for (InvoiceinfoVO ivo : involist) {
				if(!StringUtils.isEmpty(ivo.getCategorycode())&&ivo.getCategorycode().startsWith(cvo.getCategorycode())){
					if(cvo.getZsl()==null){
						cvo.setZsl(0);
					}
					cvo.setZsl(cvo.getZsl()+1);//票据总数量

					
					if(ivo.getIsaccount()==null||ivo.getIsaccount().equals(DZFBoolean.FALSE)){
						if(cvo.getWzz()==null){
							cvo.setWzz(0);
						}
						cvo.setWzz(cvo.getWzz()+1);//未做账
					}else{
						if(cvo.getYzz()==null){
							cvo.setYzz(0);
						}
						cvo.setYzz(cvo.getYzz()+1);//已做账
					}
				}
			}
			for (InvoiceinfoVO ivo : zckpList) {
				if(!StringUtils.isEmpty(ivo.getPk_assetcard())&&ivo.getCategorycode().startsWith(cvo.getCategorycode())){
					if(cvo.getZckp()==null){
						cvo.setZckp(0);
					}
					cvo.setZckp(cvo.getZckp()+1);//资产卡片
				}
			}
			if(IcCostStyle.IC_ON.equals(bbuildic)){
				for (InvoiceinfoVO ivo : ictradelist) {
					
					if(!StringUtils.isEmpty(ivo.getCbilltype())&&ivo.getCategorycode().startsWith(cvo.getCategorycode())&&ivo.getCbilltype().equals("HP75")){//出入库标志HP70入库HP75出库
						if(cvo.getCkd()==null){
							cvo.setCkd(0);
						}
						cvo.setCkd(cvo.getCkd()+1);//出库单
					}else if(!StringUtils.isEmpty(ivo.getCbilltype())&&ivo.getCategorycode().startsWith(cvo.getCategorycode())&&ivo.getCbilltype().equals("HP70")){
						if(cvo.getRkd()==null){
							cvo.setRkd(0);
						}
						cvo.setRkd(cvo.getRkd()+1);//入库单
					}
				}
			}else if("2".equals(bbuildic)){
				for (InvoiceinfoVO ivo : incomlist) {
					if(!StringUtils.isEmpty(ivo.getCbilltype())&&ivo.getCategorycode().startsWith(cvo.getCategorycode())){
						if(cvo.getRkd()==null){
							cvo.setRkd(0);
						}
						cvo.setRkd(cvo.getRkd()+1);//入库单
					}
				}
				for (InvoiceinfoVO ivo : salelist) {
					if(!StringUtils.isEmpty(ivo.getCbilltype())&&ivo.getCategorycode().startsWith(cvo.getCategorycode())){
						if(cvo.getCkd()==null){
							cvo.setCkd(0);
						}
						cvo.setCkd(cvo.getCkd()+1);//出库单
					}
				}
			}
			
		}
		catelist.add(vo);
		return catelist;
	}
	@Override
	public List<BillDetailVO> queryIntertemporal(String period, String pk_corp, String flag) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select yii.webid,yii.vinvoicecode as billdm,yii.vinvoiceno as billhm,substr(yio.cvoucherdate,0,7) as uploadperiod,yii.period as nowperiod "
				+ " from ynt_interface_invoice yii inner join ynt_image_group yig on yii.pk_image_group=yig.pk_image_group "
				+ " left join ynt_image_ocrlibrary yio on yii.ocr_id=yio.pk_image_ocrlibrary "
				+ " where nvl(yii.dr,0)=0 and nvl(yig.dr,0)=0 and nvl(yio.dr,0)=0 and yii.pk_corp = ? ");
		if(flag.equals("in")){
				sb.append(" and yii.period = ? and substr(yio.cvoucherdate,0,7)!= ? ");
			}else if(flag.equals("out")){
				sb.append(" and yii.period != ? and substr(yio.cvoucherdate,0,7)= ? ");
		}
		sp.addParam(pk_corp);	
		sp.addParam(period);	
		sp.addParam(period);	
		List<BillDetailVO> list=(List<BillDetailVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(BillDetailVO.class));

		return list;
	}
}
