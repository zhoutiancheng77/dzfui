package com.dzf.zxkj.platform.service.zcgl.impl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.BdTradeAccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.sys.BdTradeAssetCheckVO;
import com.dzf.zxkj.platform.service.common.impl.BgPubServiceImpl;
import com.dzf.zxkj.platform.service.zcgl.IHyAssetCheckService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("sys_zczzdzbserv")
public class HyAssetCheckServiceImpl extends BgPubServiceImpl implements IHyAssetCheckService {

	
	@Override
	public DZFBoolean checkBeforeSaveNew(SuperVO vo) throws DZFWarpException {
		checkExist(vo,vo.getPrimaryKey());
		return super.checkBeforeSaveNew(vo);
	}

	private void checkExist(SuperVO vo,String pk) throws DZFWarpException {
		//资产属性，资产类别，资产科目，总账科目不能重复
		Integer zcsx = (Integer) vo.getAttributeValue("assetproperty");
		String pk_assetcategory = (String) vo.getAttributeValue("pk_assetcategory");
		Integer assetaccount = (Integer) vo.getAttributeValue("assetaccount");
		String pk_glaccount = (String) vo.getAttributeValue("pk_glaccount");
		StringBuffer checksql = new StringBuffer();
		SQLParameter checkpara = new SQLParameter();
		checksql.append("select count(1) from ynt_tdcheck where pk_corp = ? and nvl(dr,0)=0");
		checkpara.addParam(vo.getAttributeValue("pk_corp"));
		if(zcsx != null){
			checksql.append(" and assetproperty = ? ");
			checkpara.addParam(zcsx);
		}
		
		if(!StringUtil.isEmpty(pk_assetcategory)){
			checksql.append( " and pk_assetcategory = ? ");
			checkpara.addParam(pk_assetcategory);
		}
		
		if(assetaccount!=null){
			checksql.append("  and assetaccount = ?");
			checkpara.addParam(assetaccount);
		}
		if(!StringUtil.isEmpty(pk_glaccount)){
			checksql.append("  and pk_glaccount =  ? ");
			checkpara.addParam(pk_glaccount);
		}
		if(!StringUtil.isEmpty(pk)){
			checksql.append("  and pk_trade_assetcheck !=  ? ");
			checkpara.addParam(pk);
		}
		BigDecimal res = (BigDecimal) getSingleObjectBO().executeQuery(checksql.toString(), checkpara, new ColumnProcessor());
		if(res!=null && res.intValue()>0){
			throw new BusinessException("存在相同的资产属性+资产类别+资产科目+总账科目");
		}
	}
	
	@Override
	public DZFBoolean checkBeforeUpdata(SuperVO vo) throws DZFWarpException {
		checkExist(vo, vo.getPrimaryKey());
		return super.checkBeforeUpdata(vo);
	}
	
	
	@Override
	public<T> List<T> completinfo(List<SuperVO>  rs,String pk_corp)throws DZFWarpException {
		//资产类别
		Map<String, BdAssetCategoryVO> zclbmap = new HashMap<String, BdAssetCategoryVO>();
		zclbmap = queryMap(BdAssetCategoryVO.class, IGlobalConstants.DefaultGroup);
		BdAssetCategoryVO lbvo;
		for(int i=0;i<rs.size();i++){
			lbvo = zclbmap.get(rs.get(i).getAttributeValue("pk_assetcategory"));
			if(lbvo!=null)
				rs.get(i).setAttributeValue("zclbmc", lbvo.getCatename());
		}
		
		
		//科目的取值出现变化
		if(IGlobalConstants.DefaultGroup.equals(pk_corp)){
			Map<String, BdTradeAccountVO> accmap = new HashMap<String, BdTradeAccountVO>();
			accmap =queryMap(BdTradeAccountVO.class,pk_corp);
			BdTradeAccountVO ivo;
			for(int i=0;i<rs.size();i++){
				ivo = accmap.get(rs.get(i).getAttributeValue("pk_glaccount"));
				lbvo = zclbmap.get(rs.get(i).getAttributeValue("pk_assetcategory"));
				if(ivo!=null)
					rs.get(i).setAttributeValue("zzkmmc", ivo.getAccountname());
				if(lbvo!=null)
					rs.get(i).setAttributeValue("zclbmc", lbvo.getCatename());
			}
		}else{
			Map<String, YntCpaccountVO> accmap = new HashMap<String, YntCpaccountVO>();
			accmap =queryMap(YntCpaccountVO.class,pk_corp);
			YntCpaccountVO ivo;
			for(int i=0;i<rs.size();i++){
				ivo = accmap.get(rs.get(i).getAttributeValue("pk_glaccount"));
				lbvo = zclbmap.get(rs.get(i).getAttributeValue("pk_assetcategory"));
				if(ivo!=null)
					rs.get(i).setAttributeValue("zzkmmc", ivo.getAccountname());
				if(lbvo!=null)
					rs.get(i).setAttributeValue("zclbmc", lbvo.getCatename());
			}
		}
		return (List<T>)rs;
	}

	@Override
	public List<BdTradeAssetCheckVO> queryAssCheckVOs(String pk_corp , String  kmfaid) throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		SQLParameter sl = new SQLParameter();
		if(IGlobalConstants.DefaultGroup.equals(pk_corp)){
			sf.append(" select oo.*,cc.fullname zzkmmc,dd.catename   zclbmc,  ");
			sf.append(" cc1.fullname as zjfykmmc,cc2.fullname as jskmmc ,dd.catecode");
			sf.append(" from ynt_tdcheck oo  ");
			sf.append(" left join ynt_tdacc cc on oo.pk_glaccount = cc.pk_trade_account ");
			sf.append(" left join ynt_tdacc cc1 on oo.pk_zjfykm = cc1.pk_trade_account ");
			sf.append(" left join ynt_tdacc cc2 on oo.pk_jskm = cc2.pk_trade_account ");
			sf.append(" left join ynt_category dd on oo.pk_assetcategory = dd.pk_assetcategory ");
			if(kmfaid!=null){
				sl.addParam(pk_corp);
				sl.addParam(kmfaid);
				sf.append(" where oo.pk_corp = ? and oo.PK_TRADE_ACCOUNTSCHEMA = ? and nvl(oo.dr,0)=0 ");
			}else{
				sl.addParam(pk_corp);
				sf.append(" where oo.pk_corp = ? and nvl(oo.dr,0)=0 ");
			}
			sf.append(" order by oo.assetproperty, catecode,oo.assetaccount");
		}else{
			sf.append(" select oo.*,cc.fullname zzkmmc,dd.catename   zclbmc ,  ");
			sf.append(" cc1.fullname as zjfykmmc,cc2.fullname as jskmmc ,dd.catecode ");
			sf.append(" from ynt_tdcheck oo  ");
			sf.append(" left join ynt_cpaccount cc on oo.pk_glaccount = cc.pk_corp_account ");
			sf.append(" left join ynt_cpaccount cc1 on oo.pk_zjfykm = cc1.pk_corp_account ");
			sf.append(" left join ynt_cpaccount cc2 on oo.pk_jskm = cc2.pk_corp_account ");
			sf.append(" left join ynt_category dd on oo.pk_assetcategory = dd.pk_assetcategory ");
			if(kmfaid!=null){
				sl.addParam(pk_corp);
				sl.addParam(kmfaid);
				sf.append(" where oo.pk_corp = ? and oo.PK_TRADE_ACCOUNTSCHEMA = ? and nvl(oo.dr,0)=0 ");
			}else{
				sl.addParam(pk_corp);
				sf.append(" where oo.pk_corp = ? and nvl(oo.dr,0)=0 ");
			}
			sf.append(" order by oo.assetproperty, catecode,oo.assetaccount");
		}
		List<BdTradeAssetCheckVO> list = (List<BdTradeAssetCheckVO>)getSingleObjectBO().executeQuery(sf.toString(), sl, new BeanListProcessor(BdTradeAssetCheckVO.class));
		return list;
	}
	
}
