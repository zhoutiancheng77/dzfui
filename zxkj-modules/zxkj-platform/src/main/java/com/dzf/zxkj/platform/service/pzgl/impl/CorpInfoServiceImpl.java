package com.dzf.zxkj.platform.service.pzgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.constant.IQmclConstant;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.pzgl.ICorpInfoService;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("corpInfoSer")
@Slf4j
public class CorpInfoServiceImpl implements ICorpInfoService {

	private SingleObjectBO singleObjectBO = null;
	@Autowired
	private IBDCorpTaxService sys_corp_tax_serv;
	@Autowired
	private ICorpService corpService;
	
	/**
	 * 根据公司编码查询公司信息
	 * 会计端-企业信息调用
	 * @param pk_corp
	 * @throws BusinessException
	 */
	@Override
	public CorpVO[] queryCorpInfo(String pk_corp) throws DZFWarpException {
		StringBuffer corpsql = new StringBuffer();
		corpsql.append("select a.chargedeptname,a.VBUSINESCOPE, nvl(a.holdflag,'N') as holdflag, a.ibuildicstyle,a.bbuildic, a.begindate,a.postaddr,a.unitcode,a.innercode,a.phone1,a.issmall,a.establishtime, ");
		corpsql.append(" a.icbegindate, a.busibegindate, a.icostforwardstyle, a.corptype, sche.accname as ctypename, ");
		corpsql.append(" a.unitname, a.taxcode, a.vbankname, a.vbankcode, a.vsoccrecode, ");
		corpsql.append(" t.tradename as indusname from bd_corp a");
		corpsql.append(" left join ynt_bd_trade t on a.industry = t.pk_trade ");
		corpsql.append(" left join ynt_tdaccschema sche on a.corptype = sche.pk_trade_accountschema ");
		//corpsql.append(" nvl(dr,0)=0 and nvl(isdatacorp,'N')='N' and isseal='N' ");
		if(pk_corp != null){
			corpsql.append("where a.pk_corp='" + pk_corp + "'");
		}
		List<CorpVO> corpInfo = (List<CorpVO>)singleObjectBO.executeQuery(corpsql.toString(), null, new BeanListProcessor(CorpVO.class));
		CorpVO[] vos = null;
		if(corpInfo != null && corpInfo.size() > 0)
			vos =  corpInfo.toArray(new CorpVO[0]);
		return vos;
	}

	//查询是否填制凭证
	@Override
	public List<TzpzHVO> isVoucher(String pk_corp) throws DZFWarpException {
		
		StringBuffer tzpzsql = new StringBuffer();
		tzpzsql.append(" select * from ynt_tzpz_h where");
		tzpzsql.append(" pk_corp = '" + pk_corp + "'");
		
		List<TzpzHVO> tzpzvos = (List<TzpzHVO>)singleObjectBO.executeQuery(tzpzsql.toString(), null, new BeanListProcessor(CorpVO.class));
		
		return tzpzvos;
	}
	
	//查询是否期间损益
	@Override
	public QmclVO isQjsy(String pk_corp, String mounth) throws DZFWarpException {
		
		StringBuffer qmclsql = new StringBuffer();
		qmclsql.append(" select * from YNT_QMCL where");
		qmclsql.append(" pk_corp = '" + pk_corp + "'");
		qmclsql.append(" and period = '" + mounth +"'");
		
		List<QmclVO> qmclvos = (List<QmclVO>)singleObjectBO.executeQuery(qmclsql.toString(), null, new BeanListProcessor(QmclVO.class));
		
		QmclVO vos = null;
		if(qmclvos != null && qmclvos.size() > 0)
			vos =  qmclvos.get(0);
		return vos;
	}
	
	
	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}
	
	@Override
	public void checkCbjzlx(String buildic, int cblx) throws DZFWarpException{
		if(IcCostStyle.IC_ON.equals(buildic)
				&& (IQmclConstant.z0 == cblx || IQmclConstant.z1 == cblx)){
			throw new BusinessException("启用库存模块，成本结转类型请选择工业成本或商贸成本");
		}else if(IcCostStyle.IC_INVTENTORY.equals(buildic)
				&& (IQmclConstant.z0 == cblx || IQmclConstant.z1 == cblx || IQmclConstant.z3 == cblx)){
			throw new BusinessException("启用总账核算存货，成本结转类型请选择商贸成本结转");
		}
	}

	@Override
	public DZFDouble getTaxBurdenRate(String pk_corp) throws DZFWarpException {
		String sql = "select hy.warning_rate from bd_corp corp " +
				" left join ynt_bd_trade hy on corp.industry = hy.pk_trade " +
				" where corp.pk_corp = ? and nvl(corp.dr,0)=0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		BigDecimal rate = (BigDecimal) singleObjectBO.executeQuery(sql, sp, new ColumnProcessor());
		if (rate != null) {
			return new DZFDouble(rate);
		} else {
			return null;
		}
	}

	@Override
	public void update(StringBuffer msg, String pk_corp,String yhzc,
			String cbjzlx, String cbtax, String leatax, String demo) throws DZFWarpException {
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		String buildic = corpvo.getBbuildic();
		int cblx = Integer.parseInt(cbjzlx);
		DZFDouble dcbtax = new DZFDouble(cbtax);
		DZFDouble dleatax= new DZFDouble(leatax);
		Integer oldCbjz  = corpvo.getIcostforwardstyle();
		checkCbjzlx(buildic, cblx);
		
		corpvo.setIcostforwardstyle(cblx);
		singleObjectBO.update(corpvo, new String[]{"icostforwardstyle"});

		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
		
		String oldyhzc = taxvo.getVyhzc();
		DZFDouble oldcbtax = taxvo.getCitybuildtax();
		DZFDouble oldleatax= taxvo.getLocaleducaddtax();
		if("0".equals(yhzc) 
				|| (!"1".equals(taxvo.getVyhzc()) && !"2".equals(taxvo.getVyhzc()))){//如果是小微企业 才更新值
			taxvo.setVyhzc(yhzc);
		}
		taxvo.setCitybuildtax(dcbtax);
		taxvo.setLocaleducaddtax(dleatax);
		taxvo.setVdemo(demo);
		singleObjectBO.saveObject(pk_corp, taxvo);
		
	
		//构造日志
		if(!StringUtil.isEmpty(cbjzlx) && !cbjzlx.equals(oldCbjz+"")){
			msg.append("成本结转类型;");
		}
		
		if(!StringUtil.isEmpty(yhzc) && !yhzc.equals(oldyhzc)){
			msg.append("是否小型微利企业;");
		}
		
		if(SafeCompute.sub(dcbtax, oldcbtax).doubleValue() !=0){
			msg.append("城建税税率;");
		}
		
		if(SafeCompute.sub(dleatax, oldleatax).doubleValue() != 0){
			msg.append("地方教育费附加;");
		}
	}
	
}
