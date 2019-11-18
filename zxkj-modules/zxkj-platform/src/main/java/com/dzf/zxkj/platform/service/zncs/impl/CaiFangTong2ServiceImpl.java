package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.constant.FieldConstant;
import com.dzf.zxkj.common.constant.IBillManageConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.piaotong.CaiFangTongBVO;
import com.dzf.zxkj.platform.model.piaotong.CaiFangTongHVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.VATSaleInvoiceBVO2;
import com.dzf.zxkj.platform.model.zncs.VATSaleInvoiceVO2;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.zncs.IBankStatement2Service;
import com.dzf.zxkj.platform.service.zncs.ICaiFangTong2Service;
import com.dzf.zxkj.platform.service.zncs.IVATSaleInvoice2Service;
import com.dzf.zxkj.platform.util.zncs.CaiFangTong;
import com.dzf.zxkj.platform.util.zncs.ICaiFangTongConstant;
import com.dzf.zxkj.platform.util.zncs.OcrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service("caifangtongservice2")
public class CaiFangTong2ServiceImpl implements ICaiFangTong2Service {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IVoucherService gl_tzpzserv;
	@Autowired
	private IBankStatement2Service gl_yhdzdserv2;
	@Autowired
	private IVATSaleInvoice2Service gl_vatsalinvserv;
	@Autowired
	private IParameterSetService sys_parameteract;
	@Override
	public Map<String, VATSaleInvoiceVO2> saveCft(CorpVO corpvo, String userid, DZFDate loginDate, StringBuffer msg) throws DZFWarpException {
		
		
		
		
		CaiFangTongHVO[] hvos = getCaiFangTongBillInfo(corpvo, userid, loginDate, msg);
		
		if(hvos == null 
				|| hvos.length == 0)
			return null;
		
		String pk_corp = corpvo.getPk_corp();
		VATSaleInvoiceVO2 salevo = null;
		List<VATSaleInvoiceVO2> listSaleVOs = new ArrayList<VATSaleInvoiceVO2>();

		VATSaleInvoiceVO2 oldSalevo = null;
		String kplx = null;
		String demo = null;
		String hm = null;
//		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
//		Map<String, DcModelHVO> dcMap = hashlizeDcModel(dcList);
		

		Map<String, VATSaleInvoiceVO2> repMap = new LinkedHashMap<String, VATSaleInvoiceVO2>();
		for(CaiFangTongHVO hvo : hvos){
			
			hm = hvo.getFphm();
//			repMap.put(hm, hm);
			
			kplx = hvo.getKplx();
			oldSalevo = queryByCGTId(hvo.getFphm(), hvo.getFpdm(), pk_corp);//查询库里是否存在此票
			
			if(!StringUtil.isEmpty(kplx)
					&& (ICaiFangTongConstant.FPLX_1.equals(kplx)
							|| ICaiFangTongConstant.FPLX_2.equals(kplx)
							|| ICaiFangTongConstant.FPLX_3.equals(kplx))){
				int dr = 0;
				if(oldSalevo != null){//判断是增加  还是删除
					dr = 1;
					setVatCFTDelVO(hvo, dr, "新增前校验到该号码代码数据库中已有数据");
				}

				hvo = (CaiFangTongHVO) singleObjectBO.saveObject(pk_corp, hvo);
				
				salevo = buildXjVOs(hvo, userid, dr);
				salevo.setVersion(new DZFDouble(1.0));
				//设置结算方式，结算科目，入账科目
//				if(!StringUtil.isEmpty(salevo.getPk_model_h())){
//					CategorysetVO setVO = gl_yhdzdserv2.queryCategorySetVO(salevo.getPk_model_h());
//					salevo.setSettlement(setVO.getSettlement()==null?0:setVO.getSettlement());
//					salevo.setPk_subject(setVO.getPk_accsubj());
//					salevo.setPk_settlementaccsubj(setVO.getPk_settlementaccsubj());
//				}
				listSaleVOs.add(salevo);
//				salevo = (VATSaleInvoiceVO2) singleObjectBO.saveObject(pk_corp, salevo);
				
				if(dr == 0){
					repMap.put(hm, salevo);
				}
			}else if(ICaiFangTongConstant.FPLX_4.equals(kplx)
					|| ICaiFangTongConstant.FPLX_5.equals(kplx)){
				int dr = 0;
				if(oldSalevo == null){
					dr = 0;
					demo = "新增前校验到该号码代码(废票)数据库中没有数据";
				}else{
					updateSaleVOStatus(oldSalevo, hvo, kplx, pk_corp);
					dr = 1;
					demo = "新增前校验到该号码代码(废票)数据库中有数据";
				}
				
				setVatCFTDelVO(hvo, dr, demo);
				hvo = (CaiFangTongHVO) singleObjectBO.saveObject(pk_corp, hvo);
				
				salevo = buildXjVOs(hvo, userid, dr);
				salevo.setVersion(new DZFDouble(1.0));
				listSaleVOs.add(salevo);
//				salevo = (VATSaleInvoiceVO2) singleObjectBO.saveObject(pk_corp, salevo);
				
				if(dr == 0){
					repMap.put(hm, salevo);
				}
			}
			
		}

		
		List<VATSaleInvoiceVO2> saleList = gl_vatsalinvserv.changeToSale(listSaleVOs, pk_corp);//分类
		//批量更新VATSaleInvoiceVO2
		int numPrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf009"));
//		int pricePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf010"));
		for (VATSaleInvoiceVO2 salevo2 : saleList)
		{
			//设置数量 ,单价精度
			VATSaleInvoiceBVO2[] bvos = (VATSaleInvoiceBVO2[]) salevo2.getChildren();
			if (bvos != null && bvos.length > 0) {
				for (VATSaleInvoiceBVO2 bvo : bvos) {
					if (bvo.getBnum() != null) {
						bvo.setBnum(bvo.getBnum().setScale(numPrecision, DZFDouble.ROUND_HALF_UP));
					}
//					if (bvo.getBprice() != null) {
//						bvo.setBprice(bvo.getBprice().setScale(pricePrecision, DZFDouble.ROUND_HALF_UP));
//					}
				}
			}
			singleObjectBO.saveObject(pk_corp, salevo2);
			if(salevo2.getDr() == 0){
				repMap.put(salevo2.getFp_hm(), salevo2);
			}
		}
		return repMap;
	}
	
	private Map<String, DcModelHVO> hashlizeDcModel(List<DcModelHVO> list){
		
		if(list == null || list.size() == 0)
			return null;
		
		Map<String, DcModelHVO> map = new HashMap<String, DcModelHVO>();
		String key = null;
		String szcode = null;
		String vscode = null;
		String businame = null;
		for(DcModelHVO hvo : list){
			szcode = hvo.getSzstylecode();
			vscode = hvo.getVspstylecode();
			businame = hvo.getBusitypetempname();
			if(FieldConstant.SZSTYLE_05.equals(szcode)
					&& (FieldConstant.FPSTYLE_01.equals(vscode)
							|| FieldConstant.FPSTYLE_02.equals(vscode))
					&& (FieldConstant.YWSTYLE_22.equals(businame)
							|| FieldConstant.YWSTYLE_15.equals(businame))){//只过滤出 增值税 销售收入、服务收入 其他收入  业务类型模板
				
				key = hvo.getBusitypetempname()
						+ "_" + hvo.getVspstylecode()
						+ "_" + hvo.getSzstylecode();//业务类型名称+票据类型+结算方式
				
				if(!map.containsKey(key)){
					map.put(key, hvo);
				}
			}
		}
		
		return map;
	}
	
	private void setVatCFTDelVO(CaiFangTongHVO hvo, int dr, String demo){
		hvo.setDr(dr);//删除标识
		hvo.setDemo1(demo);
		CaiFangTongBVO[] cbvos = hvo.getChildren();
		if(cbvos != null && cbvos.length > 0){
			for(CaiFangTongBVO bvo : cbvos){
				bvo.setDr(dr);
			}
		}
	}
	
	private void updateSaleVOStatus(VATSaleInvoiceVO2 salevo, CaiFangTongHVO hvo, String kplx, String pk_corp){
		
		salevo.setKplx(kplx);
		singleObjectBO.update(salevo, 
				new String[]{"kplx"});
		
		if(!StringUtil.isEmpty(salevo.getPk_tzpz_h())){
			VoucherParamVO paramvo = new VoucherParamVO();
			paramvo.setPk_tzpz_h(salevo.getPk_tzpz_h());
			TzpzHVO tzpzhvo = gl_tzpzserv.queryHeadVoById(paramvo.getPk_tzpz_h());
			if(tzpzhvo != null){
				gl_tzpzserv.deleteVoucher(tzpzhvo);
			}
		}
		
	}
	
	private VATSaleInvoiceVO2 queryByCGTId(String fphm, String fpdm, String pk_corp){
		String sql = "nvl(dr,0) = 0 and pk_corp = ? and fp_hm = ? and fp_dm = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(fphm);
		sp.addParam(fpdm);
		
		List<VATSaleInvoiceVO2> list = (List<VATSaleInvoiceVO2>) singleObjectBO.executeQuery(sql, sp,
				new Class[]{VATSaleInvoiceVO2.class, VATSaleInvoiceBVO2.class});
		
		return list == null || list.size() == 0 ? null : list.get(0);
	}
	
	private VATSaleInvoiceVO2 buildXjVOs(CaiFangTongHVO hvo, String userid, int dr){
//		List<VATSaleInvoiceVO2> list = new ArrayList<VATSaleInvoiceVO2>();
		VATSaleInvoiceVO2 svo = new VATSaleInvoiceVO2();
		svo.setXhfmc(hvo.getXsf_nsrmc());
		svo.setXhfsbh(hvo.getXsf_nsrsbh());
		if(!StringUtil.isEmpty(hvo.getFp_zldm())&&hvo.getFp_zldm().toUpperCase().equals("JDC")){
			svo.setXhfdzdh(transNullValue(hvo.getXsf_dz()) +" "+ transNullValue(hvo.getXfdh()));
			svo.setXhfyhzh(transNullValue(hvo.getXsf_yhzh())+" "+transNullValue(hvo.getXfkhzh()));
		}else{
			svo.setXhfdzdh(transNullValue(hvo.getXsf_dz()) + " "+transNullValue(hvo.getXsf_dh()));
			svo.setXhfyhzh(transNullValue(hvo.getXsf_yh()) + " "+transNullValue(hvo.getXsf_yhzh()));
		}
		svo.setFp_dm(hvo.getFpdm());
		svo.setFp_hm(hvo.getFphm());
		
		svo.setKprj(new DZFDateTime(hvo.getKprq()).getDate());
		svo.setJshj(new DZFDouble(hvo.getKphjje()));
		svo.setHjje(new DZFDouble(hvo.getHjbhsje()));
		svo.setSpse(new DZFDouble(hvo.getKphjse()));
		svo.setKhmc(hvo.getGmf_nsrmc());
		svo.setCustidentno(hvo.getGmf_nsrsbh());
		svo.setGhfdzdh(transNullValue(hvo.getGmf_dz()) + transNullValue(hvo.getGmf_dh()));
		svo.setGhfyhzh(transNullValue(hvo.getGmf_yh()) + transNullValue(hvo.getGmf_yhzh()));
		
		svo.setPeriod(DateUtils.getPeriod(svo.getKprj()));
		svo.setInperiod(svo.getPeriod());
		svo.setIszhuan(ICaiFangTongConstant.FPZLDM_Z0.equals(
				hvo.getFp_zldm()) ? DZFBoolean.TRUE : (ICaiFangTongConstant.FPZLDM_JDC.equals(hvo.getFp_zldm().toUpperCase())?DZFBoolean.TRUE:DZFBoolean.FALSE));
//		DZFDouble bsl = DZFDouble.ZERO_DBL;
//		bsl = SafeCompute.multiply(SafeCompute.div(svo.getSpse(), svo.getHjje()), new DZFDouble(100));
//		bsl = bsl.setScale(0, DZFDouble.ROUND_HALF_UP);
//		svo.setSpsl(bsl);
		
		
		svo.setCoperatorid(userid);
		svo.setDoperatedate(new DZFDate());
		svo.setPk_corp(hvo.getPk_corp());
		svo.setSourcetype(IBillManageConstants.CAIFANGTONG);
		svo.setSourcebillid(hvo.getPk_caifangtong_h());
		svo.setSourcebilltype(ICaiFangTongConstant.LYDJLX);
		svo.setKplx(hvo.getKplx());
		
		CaiFangTongBVO[] bvos = hvo.getChildren();
		
		String spmcstr = null;
		DZFDouble spsl = null;
		DZFDouble bbsl = DZFDouble.ZERO_DBL;
		VATSaleInvoiceBVO2[] sbvos = null;
		if(bvos != null && bvos.length > 0){
			CaiFangTongBVO bvo = null;
			List<VATSaleInvoiceBVO2> blist = new ArrayList<VATSaleInvoiceBVO2>();
//			sbvos = new VATSaleInvoiceBVO2[bvos.length];
			VATSaleInvoiceBVO2 sbvo = null;
			for(int i = 0; i < bvos.length; i++){
				bvo = bvos[i];
				
				sbvo = new VATSaleInvoiceBVO2();
				
				if("原价合计".equals(bvo.getSpmc())
						|| "折扣额合计".equals(bvo.getSpmc())){
					continue;
				}
				
				if(!StringUtil.isEmpty(bvo.getSpmc())
						&& StringUtil.isEmpty(spmcstr)){
					spmcstr = bvo.getSpmc();
				}
				
				sbvo.setRowno(Integer.parseInt(bvo.getSphxh()));//商品行序号
				
				sbvo.setBspmc(bvo.getSpmc());
				
				sbvo.setBnum(new DZFDouble(bvo.getSpsl()));
				sbvo.setBhjje(new DZFDouble(bvo.getSpje()));
				sbvo.setBprice(new DZFDouble(bvo.getSpdj()));
				sbvo.setMeasurename(bvo.getDw());
				sbvo.setInvspec(bvo.getGgxh());
				sbvo.setBspse(new DZFDouble(bvo.getSe()));
				
				bbsl = SafeCompute.multiply(SafeCompute.div(sbvo.getBspse(), sbvo.getBhjje()), new DZFDouble(100));
				bbsl = bbsl.setScale(0, DZFDouble.ROUND_HALF_UP);
				
				sbvo.setBspsl(bbsl);
				if(bbsl != null && spsl == null){
					spsl = bbsl;
				}
				
				sbvo.setPk_corp(hvo.getPk_corp());
				sbvo.setDr(dr);
				
//				sbvos[i] = sbvo;
				blist.add(sbvo);
			}
			sbvos = blist.toArray(new VATSaleInvoiceBVO2[0]);
		}
		
		svo.setSpmc(spmcstr);
		svo.setSpsl(spsl);
		
		//gl_vatsalinvserv.scanMatchBusiName(svo, dcMap);//匹配模板
		
		svo.setChildren(sbvos);
		svo.setDr(dr);
//		list.add(svo);
//		List<VATSaleInvoiceVO2> saleList = gl_vatsalinvserv.changeToSale(list, list.get(0).getPk_corp());//分类
//		return saleList.get(0);
		return svo;
	}
	
	private String transNullValue(String value){
		
		return StringUtil.isEmpty(value) ? "" : value;
	}
	
	private CaiFangTongHVO[] getCaiFangTongBillInfo(CorpVO corpvo,
			String userid, 
			DZFDate loginDate,
			StringBuffer msg){
		CaiFangTongHVO[] hvos = null;
		
		if(corpvo == null)
			return hvos;
		
		String pk_corp = corpvo.getPk_corp();
		String nsrsbh = corpvo.getVsoccrecode();
		String unitname = corpvo.getUnitname();
		String bdm = corpvo.getFax2();//发票提取码
		String period = DateUtils.getPeriod(loginDate);
//		String lastTime = getLastTime(pk_corp, ICaiFangTongConstant.LYLX_XX);
		
		CaiFangTong cft = new CaiFangTong(nsrsbh, unitname, bdm, period,
				 userid, pk_corp, ICaiFangTongConstant.LYLX_XX);
		//校验当前登录公司和票通返回公司名称是否一致
		String corpName = cft.getCorpName();
		boolean flag = OcrUtil.isSameCompany(unitname, corpName);
		if(!flag){
			throw new BusinessException("纳税人识别号和绑定码所属与当前公司不一致，请检查！");
		}
		hvos = getResult(cft, msg);
		
		return hvos;
	}

	private CaiFangTongHVO[] getResult(CaiFangTong cft, StringBuffer msg){
		int count = 0;
		
		List<CaiFangTongHVO> list = new ArrayList<CaiFangTongHVO>();
		CaiFangTongHVO[] vos = null;
		do{
			int start = count * 50;
			int end = start + 50;
			try{
				vos = cft.getVOs(start, end, msg);
			}catch(Exception e){
				if(e instanceof BusinessException){
					if(list.size() == 0){
						throw e;
					}else{
						break;
					}
				}else{
					throw new BusinessException("一键取票获取数据失败:" + e.getMessage());
				}
				
			}
			
			if(vos != null && vos.length > 0){
				list.addAll(Arrays.asList(vos));
			}
			count++;
		}while(vos != null && vos.length == 50);
		
		return list.toArray(new CaiFangTongHVO[0]);
	}
	
//	private String getLastTime(String pk_corp, int sjly){
//		
//		String sql = "Select max(h.maxkprq) From ynt_caifangtong_h h Where nvl(dr,0) = 0 and pk_corp = ? and ly = ? ";
//		
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(pk_corp);
//		sp.addParam(sjly);
//		
//		String lastTime = (String) singleObjectBO.executeQuery(sql, 
//				sp, new ObjectProcessor());
//		
//		return lastTime;
//	}
	
}
