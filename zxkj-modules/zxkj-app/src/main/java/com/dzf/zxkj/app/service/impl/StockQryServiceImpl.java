package com.dzf.zxkj.app.service.impl;

import com.dzf.zxkj.app.model.report.AppFzChVo;
import com.dzf.zxkj.app.service.IStockQryService;
import com.dzf.zxkj.app.utils.ReportUtil;
import com.dzf.zxkj.app.utils.VoUtils;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.NumMnyDetailVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.service.IRemoteReportService;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service("stockQryService")
public class StockQryServiceImpl implements IStockQryService {

	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IRemoteReportService iRemoteReportService;
	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IZxkjRemoteAppService iZxkjRemoteAppService;
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	public AppFzChVo getStockResvo(String startDate, String enddate, String pk_corp) {
		SingleObjectBO sbo = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		String pk_inventory = "";
		QueryParamVO paramvo = new QueryParamVO();
		paramvo.setBegindate1(new DZFDate(startDate));
		paramvo.setIshasjz(DZFBoolean.FALSE);
		paramvo.setIshowfs(DZFBoolean.TRUE);
		paramvo.setEnddate(new DZFDate(startDate));
		paramvo.setIsLevel(DZFBoolean.FALSE);
		paramvo.setCjq(1);
		paramvo.setCjz(5);
		paramvo.setXswyewfs(DZFBoolean.TRUE);
		paramvo.setKms_last("_9999999999999999");
		// 重庆美圣雅恒环境工程有限公司
		String userid = "";
		String pk_bz = "";
		String xsfzhs = "Y";
		CorpVO currcorp = iZxkjRemoteAppService.queryByPk(pk_corp);
		DZFDate begdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(currcorp.getBegindate()));
		List<NumMnyDetailVO> kmmxvostemp = iRemoteReportService.getNumMnyDetailVO(startDate, enddate, pk_inventory, paramvo,
				pk_corp, userid, pk_bz, xsfzhs, begdate);
		List<NumMnyDetailVO> kmmxvos = handleFs(kmmxvostemp, paramvo);
		AuxiliaryAccountBVO[] bvos = iZxkjRemoteAppService.queryAllBByLb(pk_corp, "6");
		Map<String, AuxiliaryAccountBVO> fzmap = getFzmap(bvos);
		// 同一个科目 + 同一个辅助项目的合并 （不包含辅助项目的，）
		// 1 先根据辅助项目分组
		Map<String, YntCpaccountVO> accountmap = iZxkjRemoteAppService.queryMapByPk(pk_corp);
		Map<String, Map<String, List<NumMnyDetailVO>>> fzandKmmap = new LinkedHashMap<String, Map<String, List<NumMnyDetailVO>>>();
		if (kmmxvos != null && kmmxvos.size() > 0) {
			//2： 数据进行分组
			groupData(kmmxvos, accountmap, fzandKmmap);
			//3： 根据map折算出结果集
			AppFzChVo appvo = convertAppVo(fzmap, accountmap, fzandKmmap,pk_corp);
			
			return appvo;
		}
		return new AppFzChVo();
	}

	/**
	 * 数据进行分组
	 * @param kmmxvos
	 * @param accountmap
	 * @param fzandKmmap
	 */
	private void groupData(List<NumMnyDetailVO> kmmxvos, Map<String, YntCpaccountVO> accountmap,
			Map<String, Map<String, List<NumMnyDetailVO>>> fzandKmmap) {
		for (NumMnyDetailVO vo : kmmxvos) {
			YntCpaccountVO cpavo = accountmap.get(vo.getPk_subject());
			if (cpavo != null) {
				String accountcode = cpavo.getAccountcode();
				String fzcode = vo.getKmbm().replace(accountcode + "_", "");
				if (!StringUtil.isEmpty(fzcode) && !vo.getKmbm().equals(accountcode)) {// 没辅助项目的不考虑
					if (fzandKmmap.containsKey(fzcode)) {
						// ttmap key是科目id
						Map<String, List<NumMnyDetailVO>> ttmap = fzandKmmap.get(fzcode);
						if (ttmap.containsKey(vo.getPk_subject())) {
							ttmap.get(vo.getPk_subject()).add(vo);
						} else {
							List<NumMnyDetailVO> list = new ArrayList<NumMnyDetailVO>();
							list.add(vo);
							ttmap.put(vo.getPk_subject(), list);
						}
					} else {
						Map<String, List<NumMnyDetailVO>> ttmap = new LinkedHashMap<String, List<NumMnyDetailVO>>();
						List<NumMnyDetailVO> list = new ArrayList<NumMnyDetailVO>();
						list.add(vo);
						ttmap.put(vo.getPk_subject(), list);
						fzandKmmap.put(fzcode, ttmap);
					}
				}
			}
		}
	}

	private AppFzChVo convertAppVo(Map<String, AuxiliaryAccountBVO> fzmap, Map<String, YntCpaccountVO> accountmap,
			Map<String, Map<String, List<NumMnyDetailVO>>> fzandKmmap,String pk_corp) {
		String numStr = iZxkjRemoteAppService.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
		String priceStr = iZxkjRemoteAppService.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
		int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
		int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		AppFzChVo vo = new AppFzChVo();
		DZFDouble sum_sl = DZFDouble.ZERO_DBL;
		DZFDouble sum_je = DZFDouble.ZERO_DBL;
		List<AppFzChVo.AppFzMx1> list = new ArrayList<AppFzChVo.AppFzMx1>();
		for (Map.Entry<String, Map<String, List<NumMnyDetailVO>>> entry : fzandKmmap.entrySet()) {
			String key = entry.getKey();
			Map<String, List<NumMnyDetailVO>> value = entry.getValue();
			// 通过编码(key)得到对应的名称
			if (fzmap.containsKey(key)) {
				AppFzChVo.AppFzMx1 appfzvo = new AppFzChVo.AppFzMx1();
				appfzvo.setName(fzmap.get(key).getName());
				appfzvo.setCode(fzmap.get(key).getCode());
				List<AppFzChVo.AppFzmx2> list2 = new ArrayList<AppFzChVo.AppFzmx2>();
				for (Map.Entry<String, List<NumMnyDetailVO>> entry1 : value.entrySet()) {
					AppFzChVo.AppFzmx2 appfzmxvo = new AppFzChVo.AppFzmx2();
					YntCpaccountVO cpavo = accountmap.get(entry1.getKey());
					appfzmxvo.setName(cpavo.getAccountname());
					List<AppFzChVo.AppFzmx2.AppFzKmMxVo> appfzkmmxlist = new ArrayList<AppFzChVo.AppFzmx2.AppFzKmMxVo>();
					for(NumMnyDetailVO numdetailvo: entry1.getValue()){
						if (!numdetailvo.getZy().equals("期初余额")
								&& !numdetailvo.getZy().equals("本年累计")) {
							if (numdetailvo.getZy().equals("本期合计")) {
								if (cpavo.getAccountkind() == 5) { // 损益科目，不考虑合计
									continue;
								}
								sum_sl = SafeCompute.add(sum_sl, numdetailvo.getNynum());
								sum_je = SafeCompute.add(sum_je, numdetailvo.getNymny());
								appfzmxvo.setSydj(ReportUtil.format(numdetailvo.getNyprice(), price));
								appfzmxvo.setSyje(ReportUtil.format(numdetailvo.getNymny(), 2));
								appfzmxvo.setSysl(ReportUtil.format(numdetailvo.getNynum(), num));
							}else {
								AppFzChVo.AppFzmx2.AppFzKmMxVo appfzkmmxvo = new AppFzChVo.AppFzmx2.AppFzKmMxVo();
								appfzkmmxvo.setRq(numdetailvo.getOpdate());
								if (numdetailvo.getNmny()!=null && numdetailvo.getNmny().doubleValue()!=0) {
									appfzkmmxvo.setFx("0");
								}else {
									appfzkmmxvo.setFx("1");
								}
								appfzkmmxvo.setSl(ReportUtil.format(SafeCompute.add(numdetailvo.getNnum(), numdetailvo.getNdnum()), num));
								appfzkmmxvo.setDj(ReportUtil.format(SafeCompute.add(numdetailvo.getNprice(), numdetailvo.getNdprice()), price));
								appfzkmmxvo.setJe(ReportUtil.format(SafeCompute.add(numdetailvo.getNmny(), numdetailvo.getNdmny()), 2));
								appfzkmmxlist.add(appfzkmmxvo);
							}
						}
					}
					appfzmxvo.setFzmxvos3(appfzkmmxlist.toArray(new AppFzChVo.AppFzmx2.AppFzKmMxVo[0]));
					list2.add(appfzmxvo);
				}
				appfzvo.setFzmxvos2(list2.toArray(new AppFzChVo.AppFzmx2[0]));
				list.add(appfzvo);
			}
		}
		vo.setSl(ReportUtil.format(sum_sl, num));
		vo.setJe(ReportUtil.format(sum_je, 2));
		vo.setFzmxvos1(list.toArray(new AppFzChVo.AppFzMx1[0]));
		return vo;
	}

	private Map<String, AuxiliaryAccountBVO> getFzmap(AuxiliaryAccountBVO[] fzvos) {
		Map<String, AuxiliaryAccountBVO> map = new HashMap<String, AuxiliaryAccountBVO>();

		if (fzvos != null && fzvos.length > 0) {
			for (AuxiliaryAccountBVO vo : fzvos) {
				map.put(vo.getCode(), vo);
			}
		}

		return map;

	}

	/**
	 * 处理无发生无余额不显示，有余额无发生不显示(包含无余额无发生不显示)
	 * 
	 * @param kmmxvos
	 */
	private List<NumMnyDetailVO> handleFs(List<NumMnyDetailVO> kmmxvos, QueryParamVO paramvo) {
		List<NumMnyDetailVO> result = new ArrayList<NumMnyDetailVO>();

		Map<String, DZFBoolean> periodvalue = new HashMap<String, DZFBoolean>();
		Map<String, String> qcperiodvalue = new HashMap<String, String>();// 期初期间

		if (kmmxvos != null && kmmxvos.size() > 0
				&& ((paramvo.getXswyewfs() != null && paramvo.getXswyewfs().booleanValue())
						|| (paramvo.getIshowfs() != null && !paramvo.getIshowfs().booleanValue()))) {
			String period = "";
			String key = "";
			for (NumMnyDetailVO vo : kmmxvos) {
				period = vo.getOpdate().substring(0, 7);
				key = vo.getKmbm();
				if (StringUtil.isEmpty(vo.getZy()) || !vo.getZy().equals("本期合计")) {
					continue;
				}

				if (paramvo.getIshowfs() != null && !paramvo.getIshowfs().booleanValue()) {
					if (VoUtils.getDZFDouble(vo.getNmny()).doubleValue() == 0
							&& VoUtils.getDZFDouble(vo.getNdmny()).doubleValue() == 0) {
						periodvalue.put(key + period, DZFBoolean.TRUE);
					} else {
						periodvalue.put(key + period, DZFBoolean.FALSE);
						if (!qcperiodvalue.containsKey(key)) {
							qcperiodvalue.put(key,
									DateUtils.getPeriodStartDate(vo.getOpdate().substring(0, 7)).toString());
						}
					}
				} else if (paramvo.getXswyewfs() != null && paramvo.getXswyewfs().booleanValue()) {
					if (VoUtils.getDZFDouble(vo.getNmny()).doubleValue() == 0
							&& VoUtils.getDZFDouble(vo.getNdmny()).doubleValue() == 0
							&& VoUtils.getDZFDouble(vo.getNymny()).doubleValue() == 0) {
						periodvalue.put(key + period, DZFBoolean.TRUE);
					} else {
						periodvalue.put(key + period, DZFBoolean.FALSE);
						if (!qcperiodvalue.containsKey(key)) {
							qcperiodvalue.put(key,
									DateUtils.getPeriodStartDate(vo.getOpdate().substring(0, 7)).toString());
						}
					}
				}
			}

			for (NumMnyDetailVO vo : kmmxvos) {
				period = vo.getQj().substring(0, 7);
				key = vo.getKmbm();
				DZFBoolean bxs = periodvalue.get(key + period);
				String qcperiod = qcperiodvalue.get(key);
				if (!StringUtil.isEmpty(qcperiod) && "期初余额".equals(vo.getZy())) {
					vo.setOpdate(qcperiod);
					result.add(vo);
				}
				if (bxs != null && !bxs.booleanValue() && !"期初余额".equals(vo.getZy())) {
					result.add(vo);
				}
			}
		} else {
			return kmmxvos;
		}

		return result;
	}

}
