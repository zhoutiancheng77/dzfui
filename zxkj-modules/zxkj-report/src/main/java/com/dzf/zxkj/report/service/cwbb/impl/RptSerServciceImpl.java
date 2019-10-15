package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.LrbRptSetVo;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.platform.model.report.ZcfzRptSetVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwbb.ILrbReport;
import com.dzf.zxkj.report.service.cwbb.IRptSetService;
import com.dzf.zxkj.report.service.cwbb.IZcFzBReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("rptsetser")
public class RptSerServciceImpl implements IRptSetService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IZcFzBReport gl_rep_zcfzserv;
	
	@Autowired
	private ILrbReport gl_rep_lrbserv;
	@Autowired
	private IZxkjPlatformService zxkjPlatformService;

	@Override
	public ZcfzRptSetVo[] queryZcfzRptVOs(String pk_trade_accountschema) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_trade_accountschema)) {
			throw new BusinessException("请求参数为空");
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_trade_accountschema);
		ZcfzRptSetVo[] setvos = (ZcfzRptSetVo[]) singleObjectBO.queryByCondition(ZcfzRptSetVo.class,
				"nvl(dr,0)=0 and pk_trade_accountschema= ? ", sp);

		return setvos;
	}

	@Override
	public LrbRptSetVo[] queryLrbRptVos(String pk_trade_accountschema) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_trade_accountschema)) {
			throw new BusinessException("请求参数为空");
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_trade_accountschema);
		LrbRptSetVo[] setvos = (LrbRptSetVo[]) singleObjectBO.queryByCondition(LrbRptSetVo.class,
				"nvl(dr,0)=0 and pk_trade_accountschema= ? ", sp);

		return setvos;
	}

	@Override
	public List<String> queryZcFzKmFromSetVo(String pk_trade_accountschema) throws DZFWarpException {
		List<String> list = new ArrayList<String>();
		ZcfzRptSetVo[] setvos = queryZcfzRptVOs(pk_trade_accountschema);
		if(setvos!=null && setvos.length>0){
			for(ZcfzRptSetVo setvo:setvos){
				//重分类的没考虑
				String zckms = setvo.getZckm();
				String fzkms = setvo.getFzkm();
				putListKms(list, zckms);
				putListKms(list, fzkms);
			}
		}
		return list;
	}

	private void putListKms(List<String> list, String zckms) {
		if(!StringUtil.isEmpty(zckms)){
			String strs[] = zckms.split(",");
			for(String str:strs){
				if(!StringUtil.isEmpty(str) && !list.contains(str)){
					list.add(str.substring(0, 4));
				}
			}
		}
	}

	@Override
	public String queryZcFzKmsToString(String pk_trade_accountschema) throws DZFWarpException {
		List<String> lists = queryZcFzKmFromSetVo(pk_trade_accountschema);
		StringBuffer buffer = listToString(lists);
		return buffer.toString();
	}

	private StringBuffer listToString(List<String> lists) {
		StringBuffer buffer = new StringBuffer();
		if(lists!=null && lists.size()>0){
			for(String str:lists){
				if(!StringUtil.isEmpty(str)){
					buffer.append(str+",");
				}
			}
			if(buffer.length()>0){
				buffer.substring(0, buffer.length()-1);
			}
		}
		return buffer;
	}

	@Override
	public List<String> queryLrbKmsFromSetVo(String pk_trade_accountschema) throws DZFWarpException {
		List<String> list = new ArrayList<String>();
		LrbRptSetVo[] setvos = queryLrbRptVos(pk_trade_accountschema);
		if (setvos != null && setvos.length > 0) {
			for (LrbRptSetVo setvo : setvos) {
				String kms = setvo.getKm();
				String kms2 = setvo.getKm2();
				putListKms(list, kms);
				putListKms(list, kms2);

			}
		}
		return list;
	}

	@Override
	public String queryLrbKmsToString(String pk_trade_accountschema) throws DZFWarpException {
		List<String> lists = queryLrbKmsFromSetVo(pk_trade_accountschema);
		StringBuffer buffer = listToString(lists);
		return buffer.toString();
	}

	@Override
	public List<String> queryZcfzKmFromDaima(String pk_corp,List<String> xmhcid,String[] hasyes) throws DZFWarpException {
		List<String> kmlist = new ArrayList<String>();
		if (hasyes == null || hasyes.length != 5 || !"Y".equals(hasyes[0])){
			hasyes = new String[]{"N","N","N","N","N"};
		}
		ZcFzBVO[] bvos =  gl_rep_zcfzserv.getZcfzVOs(pk_corp,hasyes, new HashMap<String, YntCpaccountVO>(), null);
		if(bvos!=null &&bvos.length>0){
			for(ZcFzBVO bvo:bvos){
				if(xmhcid!=null && xmhcid.size()>0){
					if(!xmhcid.contains(bvo.getHc1())
						 && !xmhcid.contains(bvo.getHc2()) ){
						continue;
					}
				}
				if(!StringUtil.isEmpty(bvo.getZcconkms())){
					putListKms(kmlist, bvo.getZcconkms());
				}
				if(!StringUtil.isEmpty(bvo.getFzconkms())){
					putListKms(kmlist, bvo.getFzconkms());
				}
			}
		}
		return kmlist;
	}

	@Override
	public List<String> queryLrbKmsFromDaima(String pk_corp,List<String> xmid) throws DZFWarpException {
		CorpVO cpvo = zxkjPlatformService.queryCorpByPk(pk_corp);
		List<String> kmlist = new ArrayList<String>();
		LrbVO[] lrbvos = gl_rep_lrbserv.getLrbVos(new QueryParamVO(), pk_corp, new HashMap<String,YntCpaccountVO>(), null, "");
		if (lrbvos != null && lrbvos.length > 0) {
			for(LrbVO lrbvo:lrbvos){
				if(xmid!=null && xmid.size()>0){
					if( !xmid.contains(lrbvo.getHs())){
						continue;
					}
				}
				if(!StringUtil.isEmpty(lrbvo.getVconkms())){
					putListKms(kmlist, lrbvo.getVconkms());
				}
			}
		}
		
		if("00000100AA10000000000BMD".equals(cpvo.getCorptype())){//13
			kmlist.add("3103");
		}else if("00000100AA10000000000BMF".equals(cpvo.getCorptype())){//07
			kmlist.add("4103");
		}else if("00000100000000Ig4yfE0005".equals(cpvo.getCorptype())){//企业会计制度
			return null;//企业会计制度暂不优化
//			kmlist.add("3131");
		}
		return kmlist;
	}

}
