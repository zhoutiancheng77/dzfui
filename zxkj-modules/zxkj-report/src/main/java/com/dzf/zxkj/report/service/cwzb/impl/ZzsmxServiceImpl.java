package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.MapProcessor;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.constant.IVoucherConstants;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.XssrVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.yscs.DzfpscReqBVO;
import com.dzf.zxkj.platform.model.yscs.DzfpscReqHVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwzb.IZzsmxService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ZzsmxServiceImpl implements IZzsmxService {

	@Autowired
	SingleObjectBO singleObjectBO;

	@Reference(version = "2.0.0")
	private IZxkjPlatformService zxkjPlatformService;

	@SuppressWarnings("unchecked")
	@Override
	public List<DzfpscReqBVO> getZzsmx(QueryParamVO vo) throws DZFWarpException {
		
		if(StringUtil.isEmpty(vo.getPk_corp())){
		  throw new BusinessException("公司信息不能为空");
		}
		
		int pageNo = vo.getPage(); 
		int pageSize = vo.getRows();
		
		StringBuilder pagesql = new StringBuilder();
		pagesql.append(" select * from ( SELECT ROWNUM AS ROWNO, t.*,a.fphm as fphmb,nvl(substr(a.kprq,0,10),null) as kprqb,a.ghdw as ghdwb, ");
		pagesql.append(" a.pk_corp  ");
		pagesql.append("  FROM ynt_yscs_dzfpsc_b t ");
		pagesql.append("  inner join ynt_yscs_dzfpsc_h a on a.pk_dzfpsc_h = t.pk_dzfpsc_h ");
		pagesql.append( " WHERE NVL(t.dr,0)=0 ");
		pagesql.append( " and a.pk_corp=?  ");
		pagesql.append( " and a.kprq>=? and a.kprq<=? ");
		pagesql.append( "AND ROWNUM<=? ");	
		pagesql.append( " ) WHERE ROWNO> ?");
		
		SQLParameter params = new SQLParameter();
		params.addParam(vo.getPk_corp());
		params.addParam(vo.getBegindate1());
		params.addParam(vo.getEnddate());
		params.addParam(pageNo*pageSize);
		params.addParam((pageNo-1)*pageSize);
		
		List<DzfpscReqBVO> res = (List<DzfpscReqBVO>)singleObjectBO.executeQuery(pagesql.toString(), params, new BeanListProcessor(DzfpscReqBVO.class));
		
		//按开票日期排序
		Collections.sort(res, new Comparator<DzfpscReqBVO>() {

			@Override
			public int compare(DzfpscReqBVO o1, DzfpscReqBVO o2) {

				if(o1.getKprqb().before(o2.getKprqb())){
					
					return -1;
				}
				
				if(o1.getKprqb().after(o2.getKprqb())){
					
					return 1;
				}

				return 0;
			}	
		});
		
		return res;
	}
	
	public static void main(String[] args) {
		
		DZFDate date1 = new DZFDate("2016-06-30");
		DZFDate date2 = new DZFDate("2016-06-30");
		System.out.println(date1.before(date2));
	}

	
	@SuppressWarnings("rawtypes")
	@Override
	public long getZzsmxCount(QueryParamVO vo) throws DZFWarpException {
		
		if(StringUtil.isEmpty(vo.getPk_corp())){
			  throw new BusinessException("公司信息不能为空");
		}
		
		StringBuilder countsql = new StringBuilder();
		countsql.append(" select count(1) ct from ynt_yscs_dzfpsc_h a ");
		countsql.append(" inner join ynt_yscs_dzfpsc_b b on a.pk_dzfpsc_h = b.pk_dzfpsc_h ");
		countsql.append(" where a.pk_corp=? ");
		countsql.append(" and a.kprq>=? and a.kprq<=? ");//开票日期
		
		SQLParameter params = new SQLParameter();
		params.addParam(vo.getPk_corp());
		params.addParam(vo.getBegindate1());
		params.addParam(vo.getEnddate());
		
		Map map = (Map)singleObjectBO.executeQuery(countsql.toString(), params, new MapProcessor());
		long count = Long.parseLong(map.get("ct").toString());
		
		return count;
	}

	
	@Override
	public List<DzfpscReqBVO> saveAsVoucher(CorpVO corpvo, UserVO uservo, QueryParamVO queryvo, List<DzfpscReqBVO>  vos)
			throws DZFWarpException {
		
		if(StringUtil.isEmpty(corpvo.getPk_corp())){
			 
			throw new BusinessException("公司信息不能为空");
	    }
		
		List<XssrVO> xssrmb = zxkjPlatformService.queryXssrVO(corpvo.getPk_corp());
//		SqlUtil.buildSqlForIn("pk_dzfpsc_b",pks.toArray(new String[0]));
		if(xssrmb==null||xssrmb.size()<=0){
			throw new BusinessException("未找到公司销售凭证模板");
		}
		
		TzpzHVO queryheadVO = new TzpzHVO();
		queryheadVO.setIsqxsy(queryvo.getIsqc());
		queryheadVO.setPk_corp(corpvo.getPk_corp());
		queryheadVO.setDoperatedate(new DZFDate(new Date()));
		ReturnData returnData = zxkjPlatformService.checkQjsy(queryheadVO);

		if(IVoucherConstants.EXE_RECONFM_CODE.equals(returnData.getStatus())){
			throw new BusinessException(returnData.getMessage());
		}
		
		XssrVO srmb = xssrmb.get(0);
		Map<String,List<DzfpscReqBVO>> map = new HashMap<String, List<DzfpscReqBVO>>();
		List<DzfpscReqBVO> glist = null;
		List<DzfpscReqBVO> mlist = null;
		/*按发票号分组*/
	    for(DzfpscReqBVO vo : vos){
	    	mlist = map.get(vo.getFphmb());
	    	if(mlist==null){
	    		 glist = new ArrayList<DzfpscReqBVO>();
	    		 glist.add(vo);
	    		 map.put(vo.getFphmb(), glist);
	    	}else{
	    		mlist.add(vo);
	    	}
	    }
	    
	    Iterator<String> it =  map.keySet().iterator();
	    TzpzHVO headvo = null;
	    String fphm = null;
	    Map<String,TzpzHVO> pzmap = new HashMap<String, TzpzHVO>();
	   /* 暂时先这么写 后期可能需要优化 批量保存凭证(主子表)*/
	    while(it.hasNext()){	    	
	    	fphm = it.next();
	    	headvo = getVoucherVO(srmb,map.get(fphm),corpvo,uservo,queryvo);
			headvo = zxkjPlatformService.saveVoucher(corpvo,headvo);
	    	pzmap.put(fphm, headvo);
	    }
	    
	    for(DzfpscReqBVO vo : vos){
	    	headvo = pzmap.get(vo.getFphmb());
	    	vo.setPzzh(headvo.getPzh());
	    	vo.setSummary(srmb.getMemo());
	    	vo.setPk_tzpz_h(headvo.getPrimaryKey());
	    }
	    singleObjectBO.updateAry(vos.toArray(new DzfpscReqBVO[0]),new String[]{"pzzh","summary","pk_tzpz_h"});

		return getZzsmx(queryvo);
	}
	
	
	/****
	 * 获取凭证VO
	 * @param srmb  销售凭证模板vo
	 * @param blist  增值税明细VO
	 * @param corpvo  公司VO
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private TzpzHVO getVoucherVO(XssrVO srmb,List<DzfpscReqBVO> blist,CorpVO corpvo,UserVO uservo,QueryParamVO queryvo){
		
		DZFDouble jfmny = DZFDouble.ZERO_DBL;
		DZFDouble df1mny = DZFDouble.ZERO_DBL;
		DZFDouble df2mny = DZFDouble.ZERO_DBL;
		
		for(DzfpscReqBVO vo : blist){
			jfmny = SafeCompute.add(jfmny, vo.getJshj());
			df1mny = SafeCompute.add(df1mny, vo.getMoney());
			df2mny = SafeCompute.add(df2mny, vo.getTaxmny());
		}
		
		TzpzHVO headvo = new TzpzHVO();
		headvo.setIsqxsy(queryvo.getIsqc());
		headvo.setPk_corp(corpvo.getPk_corp());
		headvo.setIshasjz(DZFBoolean.FALSE);
		headvo.setTs(new DZFDateTime(Calendar.getInstance().getTime().getTime()));
		headvo.setDr(0);
		headvo.setDoperatedate(new DZFDate(new Date()));
		headvo.setPzh(zxkjPlatformService.getNewVoucherNo(corpvo.getPk_corp(), headvo.getDoperatedate()));
		headvo.setIsfpxjxm(DZFBoolean.FALSE);
		headvo.setVbillstatus(8);
		headvo.setJfmny(jfmny);
		headvo.setDfmny(SafeCompute.add(df1mny,df2mny));
		headvo.setIsfpxjxm(DZFBoolean.FALSE);
		headvo.setVyear(Calendar.getInstance().get(Calendar.YEAR));
		headvo.setCoperatorid(uservo.getCuserid());
		headvo.setPeriod(DateUtils.getPeriod(new DZFDate(new Date())));
		//已期间损
		//gl_tzpzserv.checkQjsy(headvo);
		
		TzpzBVO[] bodyvos = new TzpzBVO[3];

		bodyvos[0] = new TzpzBVO();
		bodyvos[0].setZy(srmb.getMemo());
		bodyvos[0].setRowno(1);
		bodyvos[0].setDirect(0);
		bodyvos[0].setJfmny(jfmny);
		bodyvos[0].setYbjfmny(jfmny);
		bodyvos[0].setPk_accsubj(srmb.getYsxjkm_id());
		bodyvos[0].setVcode(srmb.getYsxjkm_code());
		bodyvos[0].setVname(srmb.getYsxjkmmc());
		bodyvos[0].setKmmchie(srmb.getYsxjkmmc());
		bodyvos[0].setPk_currency(IGlobalConstants.RMB_currency_id);
		bodyvos[0].setIsnum(new DZFBoolean(false));
		bodyvos[0].setNrate(new DZFDouble(1));
		
		if(df1mny.doubleValue()!=0){
			bodyvos[1] = new TzpzBVO();
			bodyvos[1].setZy(srmb.getMemo());
			bodyvos[1].setRowno(2);
			bodyvos[1].setDirect(1);
			bodyvos[1].setDfmny(df1mny);
			bodyvos[1].setYbdfmny(df1mny);
			bodyvos[1].setPk_accsubj(srmb.getSrlkm_id());
			bodyvos[1].setVcode(srmb.getSrlkm_code());
			bodyvos[1].setVname(srmb.getSrlkmmc());
			bodyvos[1].setKmmchie(srmb.getSrlkmmc());
			bodyvos[1].setPk_currency(IGlobalConstants.RMB_currency_id);
			bodyvos[1].setIsnum(new DZFBoolean(false));
			bodyvos[1].setNrate(new DZFDouble(1));
		}

		if(df2mny.doubleValue()!=0){
			bodyvos[2] = new TzpzBVO();
			bodyvos[2].setZy(srmb.getMemo());
			bodyvos[2].setRowno(3);
			bodyvos[2].setDirect(1);
			bodyvos[2].setDfmny(df2mny);
			bodyvos[2].setYbdfmny(df2mny);
			bodyvos[2].setPk_accsubj(srmb.getYjsfkm_id());
			bodyvos[2].setVcode(srmb.getYjsfkm_code());
			bodyvos[2].setVname(srmb.getYjsfkmmc());
			bodyvos[2].setKmmchie(srmb.getYjsfkmmc());
			bodyvos[2].setPk_currency(IGlobalConstants.RMB_currency_id);
			bodyvos[2].setIsnum(new DZFBoolean(false));
			bodyvos[2].setNrate(new DZFDouble(1));
		}
		
		List<TzpzBVO> cvo = new ArrayList<TzpzBVO>();
		for(int i=0;i<bodyvos.length;i++){
			if(bodyvos[i]!=null){
				cvo.add(bodyvos[i]);
			}
		}
		headvo.setChildren(cvo.toArray(new TzpzBVO[0]));
		
		return headvo;		
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public String getFilePath(String fphm) throws DZFWarpException {
		
		String filepath = null;
		String condition = "fphm=? and nvl(dr,0)=0";
		SQLParameter params = new SQLParameter();
		params.addParam(fphm);
		
		List<DzfpscReqHVO> vos = (List<DzfpscReqHVO>)singleObjectBO.retrieveByClause(DzfpscReqHVO.class, condition, params);
		if(vos!=null&&vos.size()>0){
			filepath = vos.get(0).getPdffile();
		}
				
		return filepath;
	} 
	
}
