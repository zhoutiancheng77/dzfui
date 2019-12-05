package com.dzf.zxkj.platform.service.zncs.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.dzf.zxkj.platform.model.pjgl.VATSaleInvoiceVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.VATSaleInvoiceBVO;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.sys.IDcpzService;
import com.dzf.zxkj.platform.service.zncs.ICaiFangTongService;
import com.dzf.zxkj.platform.service.zncs.IVATSaleInvoiceService;
import com.dzf.zxkj.platform.util.zncs.CaiFangTong;
import com.dzf.zxkj.platform.util.zncs.ICaiFangTongConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("caifangtongservice")
public class CaiFangTongServiceImpl implements ICaiFangTongService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IVoucherService gl_tzpzserv;
	@Autowired
	private IDcpzService dcpzjmbserv;
	@Autowired
	private IVATSaleInvoiceService gl_vatsalinvserv;
	
	@Override
	public Map<String, VATSaleInvoiceVO> saveCft(CorpVO corpvo, String userid, DZFDate loginDate, StringBuffer msg) throws DZFWarpException {

		CaiFangTongHVO[] hvos = getCaiFangTongBillInfo(corpvo, userid, loginDate, msg);
		
		if(hvos == null 
				|| hvos.length == 0)
			return null;
		
		String pk_corp = corpvo.getPk_corp();
		VATSaleInvoiceVO salevo = null;
		VATSaleInvoiceVO oldSalevo = null;
		String kplx = null;
		String demo = null;
		String hm = null;
		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
		Map<String, DcModelHVO> dcMap = hashlizeDcModel(dcList);
		Map<String, VATSaleInvoiceVO> repMap = new LinkedHashMap<String, VATSaleInvoiceVO>();
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
				
				salevo = buildXjVOs(hvo, userid, dr, dcMap);
				
				salevo = (VATSaleInvoiceVO) singleObjectBO.saveObject(pk_corp, salevo);
				
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
				
				salevo = buildXjVOs(hvo, userid, dr, dcMap);
				
				salevo = (VATSaleInvoiceVO) singleObjectBO.saveObject(pk_corp, salevo);
				
				if(dr == 0){
					repMap.put(hm, salevo);
				}
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
	
	private void updateSaleVOStatus(VATSaleInvoiceVO salevo, CaiFangTongHVO hvo, String kplx, String pk_corp){
		
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
	
	private VATSaleInvoiceVO queryByCGTId(String fphm, String fpdm, String pk_corp){
		String sql = "nvl(dr,0) = 0 and pk_corp = ? and fp_hm = ? and fp_dm = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(fphm);
		sp.addParam(fpdm);
		
		List<VATSaleInvoiceVO> list = (List<VATSaleInvoiceVO>) singleObjectBO.executeQuery(sql, sp,
				new Class[]{VATSaleInvoiceVO.class, VATSaleInvoiceBVO.class});
		
		return list == null || list.size() == 0 ? null : list.get(0);
	}
	
	private VATSaleInvoiceVO buildXjVOs(CaiFangTongHVO hvo, String userid, int dr, Map<String, DcModelHVO> dcMap){
		VATSaleInvoiceVO svo = new VATSaleInvoiceVO();
		svo.setXhfmc(hvo.getXsf_nsrmc());
		svo.setXhfsbh(hvo.getXsf_nsrsbh());
		svo.setXhfdzdh(transNullValue(hvo.getXsf_dz()) + transNullValue(hvo.getXsf_dh()));
		svo.setXhfyhzh(transNullValue(hvo.getXsf_yh()) + transNullValue(hvo.getXsf_yhzh()));
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
				hvo.getFp_zldm()) ? DZFBoolean.TRUE : DZFBoolean.FALSE);
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
		VATSaleInvoiceBVO[] sbvos = null;
		if(bvos != null && bvos.length > 0){
			CaiFangTongBVO bvo = null;
			List<VATSaleInvoiceBVO> blist = new ArrayList<VATSaleInvoiceBVO>();
//			sbvos = new VATSaleInvoiceBVO[bvos.length];
			VATSaleInvoiceBVO sbvo = null;
			for(int i = 0; i < bvos.length; i++){
				bvo = bvos[i];
				
				sbvo = new VATSaleInvoiceBVO();
				
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
			sbvos = blist.toArray(new VATSaleInvoiceBVO[0]);
		}
		
		svo.setSpmc(spmcstr);
		svo.setSpsl(spsl);
		
		gl_vatsalinvserv.scanMatchBusiName(svo, dcMap);//匹配模板
		
		svo.setChildren(sbvos);
		svo.setDr(dr);
		
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
