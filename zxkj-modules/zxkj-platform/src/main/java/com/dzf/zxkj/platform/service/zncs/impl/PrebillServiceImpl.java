package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.platform.model.image.OcrInvoiceDetailVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.service.zncs.IPrebillService;
import com.dzf.zxkj.platform.util.zncs.ZncsConst;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("prebillService")
public class PrebillServiceImpl implements IPrebillService {

	@Autowired
	SingleObjectBO singleObjectBO ;
	
	/*
	 *查询所有未分类的票据
	 * 
	 */
	@Override
	public List<OcrInvoiceVO> queryNotCategory()throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append(" select yii.* from ynt_interface_invoice yii inner join ynt_image_group "
				+ " yig on yii.pk_image_group=yig.pk_image_group  where nvl(yii.dr,0)=0 "
				+ " and nvl(yig.dr,0)=0 and yig.istate in (0, 1)  and yii.pk_corp is not null and yii.period is not null and yii.pk_billcategory is null "
				+ " and to_char(yii.ts,'yyyy-MM-dd')>= ? order by yii.ts ");
		sp.addParam(new DZFDate().getDateBefore(1));
		List<OcrInvoiceVO> list=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceVO.class));
		
		return list;
	}
	/*public List<OcrInvoiceVO> queryNotCategory()throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		sb.append("select * from ynt_interface_invoice where pk_corp='mx9cJX' and pk_invoice='mx9cJX00000001ruMfH4001n' order by ts desc");
		List<OcrInvoiceVO> list=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), null, new BeanListProcessor(OcrInvoiceVO.class));
		return list;
	}*/
	@Override
	public List<OcrInvoiceDetailVO> queryDetailByInvList(List<OcrInvoiceVO> list1) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		List<String> pkList = new ArrayList<String>();
		for (OcrInvoiceVO ocrInvoiceVO : list1) {
			pkList.add(ocrInvoiceVO.getPk_invoice());
		}
		sb.append("select * from ynt_interface_invoice_detail where nvl(dr,0)=0 ");
		sb.append(" and "+ SqlUtil.buildSqlForIn("pk_invoice", pkList.toArray(new String[0])));
		sb.append(" order by rowno");
		List<OcrInvoiceDetailVO> list=(List<OcrInvoiceDetailVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceDetailVO.class));

		return list;
	}
	
	public List<OcrInvoiceDetailVO> queryDetailByCondition(String condition) throws DZFWarpException {

		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select * from ynt_interface_invoice_detail where nvl(dr,0)=0 ");
		sb.append(" and "+condition);
		sb.append(" order by rowno");
		List<OcrInvoiceDetailVO> list=(List<OcrInvoiceDetailVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceDetailVO.class));

		return list;
	
		
	}
	
	@Override
	public void updateOcrInv(OcrInvoiceVO vo) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("update ynt_interface_invoice set errordesc = ? ");
		sp.addParam(vo.getErrordesc());
		sb.append(" ,errordesc2 = ? ");
		sp.addParam(vo.getErrordesc2());
		if(!StringUtils.isEmpty(vo.getPk_billcategory())){
			sb.append(" ,pk_billcategory = ? ");
			sp.addParam(vo.getPk_billcategory());
		}
		if(!StringUtils.isEmpty(vo.getPk_category_keyword())){
			sb.append(" ,pk_category_keyword = ? ");
			sp.addParam(vo.getPk_category_keyword());
		}
		if(!StringUtils.isEmpty(vo.getUpdatets().toString())){
			sb.append(" ,updatets = ? ");
			sp.addParam(vo.getUpdatets().toString());
		}
		if(!StringUtils.isEmpty(vo.getBilltitle())){
			sb.append(" ,billtitle = ? ");
			sp.addParam(vo.getBilltitle());
		}
		sb.append(" where nvl(dr,0)=0 and pk_invoice = ?");
		sp.addParam(vo.getPk_invoice());
		singleObjectBO.executeUpdate(sb.toString(), sp);
	}
	@Override
	public void updateErrorDesc(OcrInvoiceVO vo)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("update ynt_interface_invoice set errordesc = ?, errordesc2 = ? ");
		sp.addParam(vo.getErrordesc());
		sp.addParam(vo.getErrordesc2());
		sb.append(" where nvl(dr,0)=0 and pk_invoice = ?");
		sp.addParam(vo.getPk_invoice());
		singleObjectBO.executeUpdate(sb.toString(), sp);
	}
	@Override
	public void updateOcrInvDetail(OcrInvoiceDetailVO vo) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("update ynt_interface_invoice_detail set ");
		if(!StringUtils.isEmpty(vo.getPk_billcategory())){
			sb.append(" pk_billcategory = ? ");
			sp.addParam(vo.getPk_billcategory());
		}
		if(!StringUtils.isEmpty(vo.getPk_category_keyword())){
			sb.append(" ,pk_category_keyword = ? ");
			sp.addParam(vo.getPk_category_keyword());
		}
		if(!StringUtils.isEmpty(vo.getInvname())){
			sb.append(" ,invname = ? ");
			sp.addParam(vo.getInvname());
		}
		if(!StringUtils.isEmpty(vo.getItemmny())){
			sb.append(" ,itemmny = ? ");
			sp.addParam(vo.getItemmny());
		}
		if(!StringUtils.isEmpty(vo.getUpdatets().toString())){
			sb.append(" ,updatets = ? ");
			sp.addParam(vo.getUpdatets().toString());
		}
		sb.append(" where nvl(dr,0)=0 and pk_invoice_detail = ?");
		sp.addParam(vo.getPk_invoice_detail());
		singleObjectBO.executeUpdate(sb.toString(), sp);
		
	}
	@Override
	public List<OcrInvoiceVO> queryOcrInvoiceVOByBillId(List<String> arrayList, String pk_corp, String period)
			throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		
		sb.append("select yii.* from ynt_interface_invoice yii "
				+ "inner join ynt_image_group yig on yii.pk_image_group=yig.pk_image_group  "
				+ "where nvl(yii.dr,0)=0 and nvl(yig.dr,0)=0 and yig.istate in (0, 1) and yii.pk_corp = ? and yii.period=? ");
		sb.append(" and "+SqlUtil.buildSqlForIn("yii.pk_billcategory", arrayList.toArray(new String[0])));
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<OcrInvoiceVO> list=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceVO.class));
		
		return list;
	}
	public void updateInvoiceById(List<OcrInvoiceVO> list)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		List<String> arrayList = new ArrayList<String>();
		if(!list.isEmpty()||list.size()!=0){
			for (OcrInvoiceVO ocrInvoiceVO : list) {
				arrayList.add(ocrInvoiceVO.getPk_invoice());
				ocrInvoiceVO.setPk_billcategory(null);
				ocrInvoiceVO.setPk_category_keyword(null);
			}
		}else{
			throw new BusinessException("该目录下暂无票据");
		}
		sb.append("update ynt_interface_invoice set pk_category_keyword = null,pk_billcategory = null,errordesc=null where nvl(dr,0)=0 ");
		sb.append(" and "+SqlUtil.buildSqlForIn("pk_invoice",arrayList.toArray(new String[0]) ));
		singleObjectBO.executeUpdate(sb.toString(), null);
		
	}
	public void updateInvoiceDetailByInvId(List<OcrInvoiceVO> list)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		List<String> arrayList = new ArrayList<String>();
		if(!list.isEmpty()&&list.size()!=0){
			for (OcrInvoiceVO ocrInvoiceVO : list) {
				arrayList.add(ocrInvoiceVO.getPk_invoice());
			}
		}else{
			throw new BusinessException("参数异常");
		}
		sb.append("update ynt_interface_invoice_detail set pk_category_keyword = null,pk_billcategory = null where nvl(dr,0)=0 ");
		sb.append(" and "+SqlUtil.buildSqlForIn("pk_invoice",arrayList.toArray(new String[0]) ));
		singleObjectBO.executeUpdate(sb.toString(), null);
		
	}
	/*
	 * 查询票据是否重复
	 * param istate识别状态
	 * param vinvoicecode 发票代码
	 * param vinvoiceno 发票号码
	 * param uniquecode  唯一码 单据标识号
	 */
	@Override
	public List<OcrInvoiceVO> queryOcrVOIsOnly(OcrInvoiceVO ocrInvoiceVO,String pk_corp)
			throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select yii.* from ynt_interface_invoice yii inner join ynt_image_group yig on yii.pk_image_group=yig.pk_image_group left outer join ynt_image_ocrlibrary oc on yii.ocr_id=oc.pk_image_ocrlibrary ");
		sb.append(" where nvl(yii.dr,0)=0 and nvl(yig.dr,0)=0 and yii.pk_corp = ? and yig.istate != 205 ");
		sp.addParam(pk_corp);
		if(ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_1)){//b银行票据
			sb.append(" and yii.uniquecode = ? and yii.vpurbankname = ? and yii.invoicetype = ? ");
			sb.append(" and yii.keywords = ? and yii.dinvoicedate = ? and yii.vpurchname = ? ");
			sb.append(" and yii.istate = ? and yii.vpuropenacc = ? and yii.vsalename = ? and yii.vsalebankname = ? ");
			sb.append(" and yii.vsaleopenacc = ? and yii.ntotaltax = ? and yii.vsalephoneaddr = ? ");
			sp.addParam(ocrInvoiceVO.getUniquecode());
			sp.addParam(ocrInvoiceVO.getVpurbankname());
			sp.addParam(ocrInvoiceVO.getInvoicetype());
			sp.addParam(ocrInvoiceVO.getKeywords());
			sp.addParam(ocrInvoiceVO.getDinvoicedate());
			sp.addParam(ocrInvoiceVO.getVpurchname());
			sp.addParam(ocrInvoiceVO.getIstate());
			sp.addParam(ocrInvoiceVO.getVpuropenacc());
			sp.addParam(ocrInvoiceVO.getVsalename());
			sp.addParam(ocrInvoiceVO.getVsalebankname());
			sp.addParam(ocrInvoiceVO.getVsaleopenacc());
			sp.addParam(ocrInvoiceVO.getNtotaltax());
			sp.addParam(ocrInvoiceVO.getVsalephoneaddr());
		}else if(ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_3)){//增值税发票
			sb.append(" and yii.vinvoicecode = ? and yii.vinvoiceno = ? and oc.ts < (select ts from ynt_image_ocrlibrary where pk_image_ocrlibrary = ?) ");
			sp.addParam(ocrInvoiceVO.getVinvoicecode());
			sp.addParam(ocrInvoiceVO.getVinvoiceno());
			sp.addParam(ocrInvoiceVO.getOcr_id());
		}
		sb.append(" and yii.pk_invoice != ? ");
		sp.addParam(ocrInvoiceVO.getPk_invoice());
		List<OcrInvoiceVO> list=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceVO.class));
		return list;
	}
	@Override
	public List<OcrInvoiceVO> queryOcrVOByPkcorpAndPeriod(String pk_corp, String period) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append(" select  yii.*,yb.inoutflag as inoutflag,yb.categorycode as categorycode from ynt_interface_invoice yii inner join ynt_image_group yig "
				+ " on yii.pk_image_group=yig.pk_image_group left join ynt_billcategory yb "
				+ " on yii.pk_billcategory = yb.pk_category  where nvl(yii.dr,0)=0 and nvl(yig.dr,0)=0 "
				+ " and yig.istate in (0, 1) and pk_billcategory is not null and yii.pk_corp=? and yii.period=? and  yb.isaccount = 'N'");
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<OcrInvoiceVO> list=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceVO.class));
		return list;
	}
	
	
	
	 
}
