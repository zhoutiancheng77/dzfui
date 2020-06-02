package com.dzf.zxkj.app.service.bill.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dzf.zxkj.app.dao.app.IAppInvoiceDao;
import com.dzf.zxkj.app.service.bill.IAppInvoiceService;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.platform.model.pjgl.VATInComInvoiceVO;
import com.dzf.zxkj.platform.model.pjgl.VATSaleInvoiceVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("invoiceservice")
public class AppInvoiceServiceImpl implements IAppInvoiceService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IZxkjRemoteAppService iZxkjRemoteAppService;
	
	@Autowired
	private IAppInvoiceDao invoice_dao;

	@Override
	public Object[] queryXXTotal(String pk_corp, String period) throws DZFWarpException {
		Integer count = 0;
		DZFDouble totalmny = DZFDouble.ZERO_DBL;

		if(AppCheckValidUtils.isEmptyCorp(pk_corp)){
			return new Object[]{count,totalmny};
		}
		CorpVO cpvo =  iZxkjRemoteAppService.queryByPk(pk_corp);
		if(cpvo.getBegindate() == null){//尚未建账
			return new Object[]{count,totalmny};
		}

		List<VATSaleInvoiceVO> xxlist = queryXXInvoice(pk_corp, period);

		if(xxlist!=null && xxlist.size()>0){
			count = xxlist.size();
			for(VATSaleInvoiceVO vo:xxlist){
				totalmny = SafeCompute.add(totalmny, vo.getJshj());
			}
		}
		return new Object[]{count,totalmny};
	}

	@Override
	public Object[] queryJxTotal(String pk_corp, String period) throws DZFWarpException {
		Integer count = 0;
		DZFDouble totalmny = DZFDouble.ZERO_DBL;

		if(AppCheckValidUtils.isEmptyCorp(pk_corp)){
			return new Object[] { count, totalmny };
		}

		CorpVO cpvo =  iZxkjRemoteAppService.queryByPk(pk_corp);
		if(cpvo.getBegindate() == null){//尚未建账
			return new Object[]{count,totalmny};
		}

		List<VATInComInvoiceVO> jxlist = queryJxInvoice(pk_corp, period);

		if (jxlist != null && jxlist.size() > 0) {
			count = jxlist.size();
			for (VATInComInvoiceVO vo : jxlist) {
				totalmny = SafeCompute.add(totalmny, vo.getJshj());
			}
		}
		return new Object[] { count, totalmny };
	}
	@Override
	public List<VATSaleInvoiceVO> queryXXInvoice(String pk_corp, String period) throws DZFWarpException {
		return invoice_dao.queryXXInvoice(pk_corp, period);
	}

	@Override
	public List<VATInComInvoiceVO> queryJxInvoice(String pk_corp, String period) throws DZFWarpException {
		return invoice_dao.queryJxInvoice(pk_corp, period);
	}



//	@Override
//	public List<InvoiceVo> qryInvoice(String pk_corp) throws DZFWarpException {
//
//		List<InvoiceVo> lists = new ArrayList<InvoiceVo>();
//		// 查询进项发票
//		List<VATSaleInvoiceVO> xxvos = queryXXInvoice(pk_corp, null);
//		// 查询销项发票
//		List<VATInComInvoiceVO> jxvos = queryJxInvoice(pk_corp, null);
//		// 销项处理
//		handleXx(xxvos, lists);
//		// 进项处理
//		handleJx(jxvos, lists);
//
//		return lists;
//	}
//
//	private void handleJx(List<VATInComInvoiceVO> jxvos, List<InvoiceVo> lists) {
//		Map<String, Object[]> maps = new LinkedHashMap<String, Object[]>();
//
//		Object[] objs = null;
//
//		if (jxvos != null && jxvos.size() > 0) {
//			String rq = "";
//			for (VATInComInvoiceVO vo : jxvos) {
//				if(!StringUtil.isEmpty(vo.getInperiod())){//入账日期
//					rq = vo.getInperiod().toString().substring(0, 7);
//				}else {//开票日期
//					rq = vo.getKprj().toString().substring(0, 7);
//				}
//				if (maps.containsKey(rq)) {
//					objs = maps.get(rq);
//					objs[0] = ((Integer) objs[0] + 1);
//					objs[1] = SafeCompute.add((DZFDouble) objs[1], vo.getJshj());// 价税合计
//					objs[2] = SafeCompute.add((DZFDouble) objs[2], vo.getSpse());// 税额
//				} else {
//					maps.put(rq, new Object[] { 1, VoUtils.getDZFDouble(vo.getJshj()), VoUtils.getDZFDouble(vo.getSpse()) });
//				}
//			}
//
//			for (Entry<String, Object[]> entry : maps.entrySet()) {
//				objs = entry.getValue();
//				InvoiceVo vo = null;
//				FpContent fpvo = new FpContent();
//				fpvo.setCount("进项发票 "+String.valueOf(objs[0])+" 张");
//				fpvo.setJshj(Common.format(objs[1]));
//				fpvo.setSe(Common.format(objs[2]));
//				fpvo.setFplx(1);// 进项发票
//				for(InvoiceVo invoice:lists){
//					if(invoice.getRq().equals(entry.getKey())){
//						vo = invoice;
//						if(invoice.getFpcontent() == null){
//							List<FpContent> list = new ArrayList<FpContent>();
//							list.add(fpvo);
//							invoice.setFpcontent(list);
//						}else{
//							invoice.getFpcontent().add(fpvo);
//						}
//						break;
//					}
//				}
//				if(vo == null){
//					vo = new InvoiceVo();
//					vo.setRq(entry.getKey());
//					List<FpContent> list = new ArrayList<FpContent>();
//					list.add(fpvo);
//					vo.setFpcontent(list);
//					lists.add(vo);
//				}
//			}
//		}
//	}
//
//	private void handleXx(List<VATSaleInvoiceVO> xxvos, List<InvoiceVo> lists) {
//		Map<String, Object[]> maps = new LinkedHashMap<String, Object[]>();
//
//		Object[] objs = null;
//
//		if (xxvos != null && xxvos.size() > 0) {
//			String rq = "";
//			for (VATSaleInvoiceVO vo : xxvos) {
//				rq = vo.getKprj().toString().substring(0, 7);
//				if (maps.containsKey(rq)) {
//					objs = maps.get(rq);
//					objs[0] = ((Integer) objs[0] + 1);
//					objs[1] = SafeCompute.add((DZFDouble) objs[1], vo.getJshj());// 价税合计
//					objs[2] = SafeCompute.add((DZFDouble) objs[2], vo.getSpse());// 税额
//				} else {
//					maps.put(rq, new Object[] { 1, VoUtils.getDZFDouble(vo.getJshj()), VoUtils.getDZFDouble(vo.getSpse()) });
//				}
//			}
//
//			for (Entry<String, Object[]> entry : maps.entrySet()) {
//				objs = entry.getValue();
//				InvoiceVo vo = new InvoiceVo();
//				List<FpContent> fplist = new ArrayList<FpContent>();
//				FpContent fpvo = new FpContent();
//				vo.setRq(entry.getKey());
//				fpvo.setCount("销项发票 " + String.valueOf(objs[0]) + " 张");
//				fpvo.setJshj(Common.format(objs[1]));
//				fpvo.setSe(Common.format(objs[2]));
//				fpvo.setFplx(0);// 销项发票
//				fplist.add(fpvo);
//				vo.setFpcontent(fplist);
//				lists.add(vo);
//			}
//		}
//	}
//
//	@Override
//	public VATSaleInvoiceVO queryXXInvoiceDetail(String pk_corp, String id) throws DZFWarpException {
//
//		// 查询主表信息
//		if (StringUtil.isEmpty(id)) {
//			throw new BusinessException("查询信息不能为空!");
//		}
//		VATSaleInvoiceVO vo = (VATSaleInvoiceVO) singleObjectBO.queryByPrimaryKey(VATSaleInvoiceVO.class, id);
//
//		if (vo == null) {
//			throw new BusinessException("发票信息为空!");
//		}
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(id);
//		VATSaleInvoiceBVO[] bvos = (VATSaleInvoiceBVO[]) singleObjectBO.queryByCondition(VATSaleInvoiceBVO.class,
//				"nvl(dr,0)=0 and pk_vatsaleinvoice =? ", sp);
//
//		vo.setChildren(bvos);
//
//		if(vo.getIsZhuan()!=null && vo.getIsZhuan().booleanValue()){
//			vo.setIszh(IConstant.DEFAULT);
//		}else{
//			vo.setIszh(IConstant.FIRDES);
//		}
//
//		return vo;
//	}
//

//
}
