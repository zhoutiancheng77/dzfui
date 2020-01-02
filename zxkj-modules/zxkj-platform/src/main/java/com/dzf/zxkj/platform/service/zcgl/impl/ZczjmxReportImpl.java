package com.dzf.zxkj.platform.service.zcgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.jzcl.GdzcjzVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;
import com.dzf.zxkj.platform.model.zcgl.AssetcardVO;
import com.dzf.zxkj.platform.model.zcgl.ValuemodifyVO;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.zcgl.IAssetCard;
import com.dzf.zxkj.platform.service.zcgl.IAssetDepreciation;
import com.dzf.zxkj.platform.service.zcgl.IZczjmxReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 资产折旧明细
 * 
 * @author JasonLiu
 * 
 */
@Service("am_rep_zczjmxserv")
public class ZczjmxReportImpl implements IZczjmxReport {
	
	private SingleObjectBO singleObjectBO = null;
	
	private IYntBoPubUtil yntBoPubUtil = null;

	public IYntBoPubUtil getYntBoPubUtil() {
		return yntBoPubUtil;
	}
	@Autowired
	public void setYntBoPubUtil(IYntBoPubUtil yntBoPubUtil) {
		this.yntBoPubUtil = yntBoPubUtil;
	}

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}
	
	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}
	
	public AssetDepreciaTionVO[] getZczjMxVOs(QueryParamVO vo, String asset_id) throws DZFWarpException {
		StringBuffer qrysql = new StringBuffer();
		if(vo.getBegindate1()==null && StringUtil.isEmpty(asset_id)){
			throw new BusinessException("查询失败:开始日期不能为空!");
		}
		
		if(vo.getBegindate1()!=null && vo.getEnddate()!=null){
			if(vo.getBegindate1().after(vo.getEnddate())){
				throw new BusinessException("查询开始日期，应在结束日期之前!");
			}
		}
		
		String zxlb = vo.getPk_assetcategory();
		String zcsx = vo.getZcsx();
		String zcbm = vo.getZccode();
		SQLParameter sp = new SQLParameter();
		qrysql.append("select d.catename,d.assetproperty,b.assetcode,b.assetname,b.accountdate,b.uselimit,b.salvageratio,");
		qrysql.append("a.businessdate,b.assetmny, a.depreciationmny, a.assetnetmny,d.catecode,d.catelevel,");
		qrysql.append("a.originalvalue, a.istogl, a.issettle,c.pzh, a.pk_corp ,a.pk_voucher,a.pk_assetdepreciation,a.pk_assetcard,d.pk_assetcategory,c.doperatedate  as doperatedate ");
		qrysql.append("from ynt_depreciation a  left join ynt_assetcard b on a.pk_assetcard = b.pk_assetcard ");
		qrysql.append("left join ynt_category d on b.assetcategory=d.pk_assetcategory ");
		qrysql.append("left join ynt_tzpz_h  c on c.pk_tzpz_h = a.pk_voucher  and  nvl(c.dr,0)=0");
		qrysql.append(" where a.pk_corp =? and  nvl(a.dr,0)=0  and nvl(b.dr,0)=0 ");
		sp.addParam(vo.getPk_corp());
		if(vo.getBegindate1()!=null){
			qrysql.append(" and a.businessdate >=? ");
			sp.addParam(vo.getBegindate1());
		}
		if(vo.getEnddate()!=null){
			qrysql.append(" and a.businessdate <=? ");
			sp.addParam(DateUtils.getPeriodEndDate(DateUtils.getPeriod(vo.getEnddate())) );//默认查询最后一个月份
		}
		
		if(!StringUtil.isEmpty(zxlb)){
			qrysql.append("  and b.assetcategory ='"+zxlb+"'");
		}
		if(!StringUtil.isEmpty(zcsx)){
			if(!"2".equals(zcsx)){//2位显示全部
				qrysql.append("  and d.assetproperty="+ zcsx);
			}
		}
		if(!StringUtil.isEmpty(zcbm)){
			qrysql.append(" and b.assetcode like '%"+zcbm+"%'");
			//qrysql.append("  and b.assetname like '%"+zcbm+"%'");
		}
		if(!StringUtil.isEmpty(asset_id)){
			qrysql.append(" and a.pk_assetcard = ? ");
			sp.addParam(asset_id);
		}
		qrysql.append(" order by d.catecode,b.assetcode,b.assetname,a.businessdate");
	  	List<AssetDepreciaTionVO> result1 = (List<AssetDepreciaTionVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(AssetDepreciaTionVO.class));
	  	
	  	//查询当前卡片变更过多少次取最近的日期
	  	StringBuffer bgsql = new StringBuffer();
		bgsql.append( " select a.originalvalue,a.newvalue,a.pk_assetcard,a.businessdate as doperatedate  from ynt_valuemodify a  inner join  ynt_assetcard b ");
		bgsql.append(" 	on a.pk_assetcard = b.pk_assetcard  and nvl(a.dr,0)=0 and nvl(b.dr,0)=0 ");
		bgsql.append(" 	where nvl(a.istogl,'N')='Y'  ");
		bgsql.append("  and a.pk_corp =  ? " );
		bgsql.append(" 	order by b.assetcode ,a.businessdate desc ");
	  	sp.clearParams();
	  	sp.addParam(vo.getPk_corp());
		List<ValuemodifyVO> result2 = (List<ValuemodifyVO>) singleObjectBO.executeQuery(bgsql.toString(), sp, new BeanListProcessor(ValuemodifyVO.class));
		
		for(AssetDepreciaTionVO  resvo:result1){
			String pk_asset = resvo.getPk_assetcard();
			DZFDate dopedate = resvo.getBusinessdate();
			DZFBoolean isasset = DZFBoolean.FALSE;
			DZFDouble resdouble = DZFDouble.ZERO_DBL;
			Integer cont = 0;
			for(ValuemodifyVO modivo:result2){
				if(modivo.getPk_assetcard().equals(pk_asset)){
					if(!isasset.booleanValue() && (modivo.getDoperatedate().before(dopedate) || modivo.getDoperatedate().compareTo(dopedate) ==0)){
						isasset = DZFBoolean.TRUE;
						resvo.setAssetmny(modivo.getNewvalue());
					}
					resdouble = modivo.getOriginalvalue();
					cont++;
				}
			}
			if(cont>0 && !isasset.booleanValue()){
				resvo.setAssetmny(resdouble);
			}
		}
	  	AssetDepreciaTionVO[]  zczjarray= result1.toArray(new AssetDepreciaTionVO[0]);
	  	
	  	return zczjarray;
	}
	
	/**
	 * 折旧汇总表联查明细
	 * 需要资产类别的编码和名称查询
	 */
	public AssetDepreciaTionVO[] getZczjMxVOsByHz(QueryParamVO vo,String catecode,String catename) throws DZFWarpException {
		
//		YntBoPubUtil yntBoPubUtil = (YntBoPubUtil) SpringUtils.getBean("yntBoPubUtil");
		StringBuffer qrysql = new StringBuffer();
		if(vo.getBegindate1()==null){
			throw new BusinessException("查询失败:开始日期不能为空!");
		}
		if(vo.getBegindate1()!=null && vo.getEnddate()!=null){
			if(vo.getBegindate1().after(vo.getEnddate())){
				throw new BusinessException("查询开始日期，应在结束日期之前!");
			}
		}
		
		String zxlb = vo.getPk_assetcategory();
		String zcsx = vo.getZcsx();
		String zcbm = vo.getZccode();
		SQLParameter sp = new SQLParameter();
		qrysql.append("select d.catename,d.assetproperty,b.assetcode,b.assetname,b.accountdate,b.uselimit,b.salvageratio,");
		qrysql.append("a.businessdate,b.assetmny, a.depreciationmny, a.assetnetmny,d.catecode,d.catelevel,");
		qrysql.append("a.originalvalue, a.istogl, a.issettle,c.pzh, a.pk_corp ,a.pk_voucher,a.pk_assetdepreciation,a.pk_assetcard,d.pk_assetcategory ");
		qrysql.append("from ynt_depreciation a  left join ynt_assetcard b on a.pk_assetcard = b.pk_assetcard ");
		qrysql.append("left join ynt_category d on b.assetcategory=d.pk_assetcategory ");
		qrysql.append("left join ynt_tzpz_h  c on c.pk_tzpz_h = a.pk_voucher  and  nvl(c.dr,0)=0");
		qrysql.append(" where a.pk_corp =? and  nvl(a.dr,0)=0  and nvl(b.dr,0)=0");
		qrysql.append(" and a.businessdate >=? ");
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getBegindate1());
		if(vo.getEnddate()!=null){
			qrysql.append(" and a.businessdate <=? ");
			sp.addParam(vo.getEnddate());
		}
		
		if(!StringUtil.isEmpty(zxlb)){
			qrysql.append("  and b.assetcategory ='"+zxlb+"'");
		}
		if(!StringUtil.isEmpty(zcsx)){
			if(!"2".equals(zcsx)){//2位显示全部
				qrysql.append("  and d.assetproperty="+ zcsx);
			}
		}
		if(!StringUtil.isEmpty(catecode) && !StringUtil.isEmpty(catename)){
			//qrysql.append(" and d.catecode=? and d.catename=?");
			qrysql.append(" and d.catecode like ? ");
			sp.addParam(catecode + "%");
//			sp.addParam(catename);
		}
		qrysql.append(" order by d.catecode,b.assetcode,b.assetname,a.businessdate");
	  	List<AssetDepreciaTionVO> result1 = (List<AssetDepreciaTionVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(AssetDepreciaTionVO.class));
	  	
	  //查询当前卡片变更过多少次取最近的日期
	  	StringBuffer bgsql = new StringBuffer();
		bgsql.append( " select a.originalvalue,a.newvalue,a.pk_assetcard,a.businessdate as doperatedate  from ynt_valuemodify a  inner join  ynt_assetcard b ");
		bgsql.append(" 	on a.pk_assetcard = b.pk_assetcard  and nvl(a.dr,0)=0 and nvl(b.dr,0)=0 ");
		bgsql.append(" 	where nvl(a.istogl,'N')='Y'  ");
		bgsql.append("  and a.pk_corp =  ? " );
		bgsql.append(" 	order by b.assetcode ,a.businessdate desc ");
	  	sp.clearParams();
	  	sp.addParam(vo.getPk_corp());
		List<ValuemodifyVO> result2 = (List<ValuemodifyVO>) singleObjectBO.executeQuery(bgsql.toString(), sp, new BeanListProcessor(ValuemodifyVO.class));
		
		for(AssetDepreciaTionVO  resvo:result1){
			String pk_asset = resvo.getPk_assetcard();
			DZFDate dopedate = resvo.getBusinessdate();
			DZFBoolean isasset = DZFBoolean.FALSE;
			DZFDouble resdouble = DZFDouble.ZERO_DBL;
			Integer cont = 0;
			for(ValuemodifyVO modivo:result2){
				if(modivo.getPk_assetcard().equals(pk_asset)){
					if(!isasset.booleanValue() && (modivo.getDoperatedate().before(dopedate) || modivo.getDoperatedate().compareTo(dopedate) ==0)){
						isasset = DZFBoolean.TRUE;
						resvo.setAssetmny(modivo.getNewvalue());
					}
					resdouble = modivo.getOriginalvalue();
					cont++;
				}
			}
			if(cont>0 && !isasset.booleanValue()){
				resvo.setAssetmny(resdouble);
			}
		}
	  	
	  	AssetDepreciaTionVO[]  zczjarray= result1.toArray(new AssetDepreciaTionVO[0]);
	  	
	  	return zczjarray;
	}

	//zpm修改提交
	public AssetDepreciaTionVO[] linkZczjMxVOs(QueryParamVO vo) throws DZFWarpException {
		StringBuffer sb = new StringBuffer();
		sb.append("select b.assetcode,    a.businessdate,   a.assetmny, a.depreciationmny,  a.assetnetmny, ");
		sb.append("  a.originalvalue,   a.istogl, a.issettle,c.pzh, a.pk_corp ,c.pk_tzpz_h as pk_voucher,a.pk_assetdepreciation ");
		sb.append("  from ynt_depreciation a  left join ynt_assetcard b on a.pk_assetcard = b.pk_assetcard  and nvl(a.dr, 0) = 0  ");
		sb.append("  left join ynt_tzpz_h  c on c.pk_tzpz_h = a.pk_voucher and nvl(a.dr,0)=0  and  nvl(c.dr,0)=0  ");
		sb.append(" where b.pk_assetcard = ? order by businessdate");
	  	SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_assetcard());
	  	List<AssetDepreciaTionVO> result1 = (List<AssetDepreciaTionVO>) singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(AssetDepreciaTionVO.class));
	  	AssetDepreciaTionVO[]  zczjarray= result1.toArray(new AssetDepreciaTionVO[0]);
	  	return zczjarray;
	}

	@Override
	public void deleteZjmx(String pk,String pk_vouchid) throws DZFWarpException {
		//默认是一个折旧对应一个资产
		//先查询
		if(StringUtil.isEmpty(pk) && StringUtil.isEmpty(pk_vouchid)){
			throw new BusinessException("数据为空,删除失败!");
		}
		AssetDepreciaTionVO depvo = (AssetDepreciaTionVO) singleObjectBO.queryByPrimaryKey(AssetDepreciaTionVO.class, pk);
		//如果为空则根据凭证查询折旧明细
		if(depvo == null){
			TzpzHVO hvo = (TzpzHVO) singleObjectBO.queryByPrimaryKey(TzpzHVO.class, pk_vouchid);
			if(hvo!=null){
				String period = hvo.getPeriod();
				SQLParameter sp = new SQLParameter();
				sp.addParam(pk_vouchid);
				sp.addParam(period+"%");
				sp.addParam(hvo.getPk_corp());
				AssetDepreciaTionVO[] assetdepvos = (AssetDepreciaTionVO[]) singleObjectBO.queryByCondition(AssetDepreciaTionVO.class,
						"nvl(dr,0)=0 and pk_voucher = ?  and businessdate like ? and pk_corp = ? ", sp);
				if(assetdepvos!=null && assetdepvos.length == 1){//只是查询一张才这么处理，如果是多，则不处理(多个折旧明细，生成一张折旧凭证)如果是多个，需要为以后批量准备
					depvo = assetdepvos[0];
				}
			}
		}
		
		if(depvo == null){
			throw new BusinessException("数据为空，删除失败!");
		}
		//删除校验
		checkPeriodIsSettle(depvo.getPk_corp(),depvo.getBusinessdate());
		isgenVoucher(depvo);
		singleObjectBO.deleteObject(depvo);
		IAssetCard assetCardImpl = (IAssetCard) SpringUtils.getBean("assetCardImpl");
		assetCardImpl.deleteAssetDepreciation(depvo);
	}
	
	
	/**
	 * 校验期间是否已经结账，如果已经结账，则当月不允许进行任何操作
	 * @param pk_corp
	 * @param date
	 * @throws BusinessException
	 */
	public  void checkPeriodIsSettle(String pk_corp,  DZFDate date) throws DZFWarpException{
		
		if(date==null){
			throw new BusinessException("处理失败,折旧明细日期为空!");
		}
		
		String period = date.toString().substring(0, 7);
		String where = String.format("pk_corp='%s' and period='%s' and nvl(dr,0)=0", pk_corp,  period);
		List<GdzcjzVO> gdzcjzVOs =(List<GdzcjzVO>) singleObjectBO.retrieveByClause(GdzcjzVO.class, where, new SQLParameter());
//		if(gdzcjzVOs != null && gdzcjzVOs.size() > 0 && gdzcjzVOs.get(0).getJzfinish() != null && gdzcjzVOs.get(0).getJzfinish().booleanValue())
//			throw new BusinessException(String.format("月份%s 已经进行固定资产结账，不允许进行操作！", period));
	}

	/**
	 * 生成凭证逻辑
	 */
	@Override
	public AssetDepreciaTionVO insertVoucher(CorpVO corpvo, String pk) throws DZFWarpException {
		// 先查询
		if (pk == null || pk.trim().length() == 0) {
			throw new BusinessException("数据为空,生成凭证失败!");
		}
		AssetDepreciaTionVO assetdepVO = (AssetDepreciaTionVO) singleObjectBO .queryByPrimaryKey(AssetDepreciaTionVO.class, pk);

		if (assetdepVO == null)
			throw new BusinessException("数据为空,生成凭证失败!");
		if (assetdepVO instanceof AssetDepreciaTionVO) {
//			IAssetCard assetCardImpl = (IAssetCard) SpringUtils.getBean("assetCardImpl");
//			assetCardImpl.processeDreciationToGL(assetdepVO);
			processeDreciationToGL(corpvo,assetdepVO);
		}
		return assetdepVO;
	}
	
	private void checkAssetDepConcurrency(AssetDepreciaTionVO assetdepVO)
		throws DZFWarpException {
		AssetDepreciaTionVO oldVO = (AssetDepreciaTionVO) singleObjectBO
				.queryVOByID(assetdepVO.getPrimaryKey(),
						AssetDepreciaTionVO.class);
		
		if (oldVO == null) {
			throw new BusinessException("该数据已经被他人删除，请刷新界面");
		}

	}

	public void processeDreciationToGL(CorpVO corpvo, SuperVO assetdepVO) throws DZFWarpException {
		if (assetdepVO == null)
			return;
		if (assetdepVO instanceof AssetDepreciaTionVO) {
			AssetDepreciaTionVO vo = (AssetDepreciaTionVO) assetdepVO;
			checkAssetDepConcurrency(vo);
			AssetcardVO assetcardVO = (AssetcardVO) singleObjectBO
					.queryByPrimaryKey(AssetcardVO.class, vo.getPk_assetcard());
			IAssetDepreciation assetDepreciationImpl = (IAssetDepreciation) SpringUtils.getBean("assetDepreciationImpl");
			//processAssetDepToGL(vo, assetcardVO);
			assetDepreciationImpl.processAssetDepToGL(corpvo, vo, assetcardVO);
		}
	}
	/**
	 * 
	 * @param assetdepVO
	 */
	public void isgenVoucher(AssetDepreciaTionVO assetdepVO) throws DZFWarpException{
//		String pk_voucher = assetdepVO.getPk_voucher();
//		if(pk_voucher!=null && pk_voucher.trim().length()>0){
//			TzpzHVO hvo = (TzpzHVO) singleObjectBO.queryByPrimaryKey(TzpzHVO.class, pk_voucher);
//			if(hvo!=null){
//				throw new BusinessException("处理失败，已生成凭证!");
//			}
//		}
		if(assetdepVO.getIstogl() != null && assetdepVO.getIstogl().booleanValue()){
			throw new BusinessException("处理失败，已生成凭证!");
		}
	}
	public AssetDepreciaTionVO queryById(String id) throws DZFWarpException {
			AssetDepreciaTionVO vo = (AssetDepreciaTionVO) singleObjectBO
					.queryVOByID(id,AssetDepreciaTionVO.class);
		return vo;
		
	}
	
	public AssetDepreciaTionVO[] queryAll(QueryParamVO vo)throws DZFWarpException{
//		YntBoPubUtil yntBoPubUtil = (YntBoPubUtil) SpringUtils.getBean("yntBoPubUtil");
		StringBuffer qrysql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		
		qrysql.append("select d.catename,b.assetcode,b.assetname,b.accountdate,b.uselimit,b.salvageratio,");
		qrysql.append("a.businessdate,b.assetmny, a.depreciationmny, a.assetnetmny,d.catecode,d.catelevel,");
		qrysql.append("a.originalvalue, a.istogl, a.issettle,c.pzh, a.pk_corp ,a.pk_voucher,a.pk_assetdepreciation,a.pk_assetcard,d.pk_assetcategory ");
		qrysql.append("from ynt_depreciation a  left join ynt_assetcard b on a.pk_assetcard = b.pk_assetcard ");
		qrysql.append("left join ynt_category d on b.assetcategory=d.pk_assetcategory ");
		qrysql.append("left join ynt_tzpz_h  c on c.pk_tzpz_h = a.pk_voucher  and  nvl(c.dr,0)=0");
		//qrysql.append("where d.catename is not null and  nvl(a.dr,0)=0  and nvl(b.dr,0)=0");
		qrysql.append(" where a.pk_corp =? and  nvl(a.dr,0)=0  and nvl(b.dr,0)=0 ");
		sp.addParam(vo.getPk_corp());

		qrysql.append(" order by d.catename,b.assetcode,b.assetname,a.businessdate");
	  	List<AssetDepreciaTionVO> result1 = (List<AssetDepreciaTionVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(AssetDepreciaTionVO.class));
	  	AssetDepreciaTionVO[]  zczjarray= result1.toArray(new AssetDepreciaTionVO[0]);
	  	
	  	return zczjarray;
	}
}
