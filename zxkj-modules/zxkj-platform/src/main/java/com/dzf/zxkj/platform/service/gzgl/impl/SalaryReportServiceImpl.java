package com.dzf.zxkj.platform.service.gzgl.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.utils.DZFStringUtil;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.enums.SalaryReportEnum;
import com.dzf.zxkj.common.enums.SalaryTypeEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.gzgl.*;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.IPersonalSetService;
import com.dzf.zxkj.platform.service.common.IReferenceCheck;
import com.dzf.zxkj.platform.service.gzgl.ISalaryAccSetService;
import com.dzf.zxkj.platform.service.gzgl.ISalaryBaseService;
import com.dzf.zxkj.platform.service.gzgl.ISalaryCalService;
import com.dzf.zxkj.platform.service.gzgl.ISalaryReportService;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.util.AccountUtil;
import com.dzf.zxkj.platform.util.VoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("gl_gzbserv")
@Slf4j
public class SalaryReportServiceImpl implements ISalaryReportService {


	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IPersonalSetService gl_gxhszserv;
	@Autowired
	private IReferenceCheck refcheck;
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private ISalaryCalService gl_gzbcalserv;
	@Autowired
	private  ISalaryBaseService gl_gzbbaseserv;
	@Autowired
	private ISalaryAccSetService gl_gzkmszserv;
	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accService;

	@Override
	public SalaryReportVO[] query(String pk_corp, String qj, String billtype) throws DZFWarpException {
		String wheresql = " pk_corp = ? and qj = ? and nvl(dr,0) = 0 and nvl(billtype,'01') = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(qj);
		sp.addParam(billtype);
		SalaryReportVO[] srvos = (SalaryReportVO[]) singleObjectBO.queryByCondition(SalaryReportVO.class, wheresql, sp);
		return querySalaryReportVO(srvos, pk_corp, false);
	}

	@Override
	public SalaryReportVO[] queryAllType(String pk_corp, String qj) throws DZFWarpException {

		String wheresql = " pk_corp = ? and qj = ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(qj);
		SalaryReportVO[] srvos = (SalaryReportVO[]) singleObjectBO.queryByCondition(SalaryReportVO.class, wheresql, sp);

		for (SalaryReportVO vo : srvos) {
			vo.setSfyg("否");
			if (SalaryTypeEnum.REMUNERATION.getValue().equals(vo.getBilltype())) {
				vo.setSfgy("否");
			} else {
				vo.setSfgy("是");
			}
		}

		return querySalaryReportVO(srvos, pk_corp, true);
	}

	@Override
	public Map<String, List<String>> queryAllTypeBeforeCurr(String pk_corp, String qj) throws DZFWarpException {

		String wheresql = " pk_corp = ? and qj <= ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(qj);
		SalaryReportVO[] srvos = (SalaryReportVO[]) singleObjectBO.queryByCondition(SalaryReportVO.class, wheresql, sp);
		if (srvos == null || srvos.length == 0)
			srvos = new SalaryReportVO[0];
		Map<String, List<SalaryReportVO>> map = DZfcommonTools.hashlizeObject(Arrays.asList(srvos),
				new String[] { "cpersonid" });
		Map<String, List<String>> map1 = new HashMap<>();
		for (Map.Entry<String, List<SalaryReportVO>> entry : map.entrySet()) {
			List<SalaryReportVO> list = entry.getValue();
			List<String> qjlist = new ArrayList<>();
			List<String> qjlist1 = new ArrayList<>();
			if (list != null && list.size() > 0) {
				for (SalaryReportVO vo : list) {
					qjlist.add(vo.getQj());
				}
				qjlist1.add(Collections.min(qjlist));
				qjlist1.add(Collections.max(qjlist));
				map1.put(entry.getKey(), qjlist1);
			}
		}
		return map1;
	}

	private SalaryReportVO[] querySalaryReportVO(SalaryReportVO[] srvos, String pk_corp, boolean isAll) {

		if (srvos == null || srvos.length == 0)
			return new SalaryReportVO[0];

		SalaryCipherHandler.handlerSalaryVO(srvos, 1);// 解密处理
		List<SalaryReportVO> listall = new ArrayList<SalaryReportVO>();
		if (srvos != null && srvos.length > 0) {
			setTotalData(srvos);
			listall = Arrays.asList(srvos);
		}
		GxhszVO gxh = gl_gxhszserv.query(pk_corp);
		Integer kmShow = gxh.getSubjectShow();
		if (listall != null && listall.size() > 0) {
			Map map = accService.queryMapByPk(pk_corp);
			Map<String, AuxiliaryAccountBVO> aumap = getAuxiliaryMap(pk_corp);
			for (SalaryReportVO vo : listall) {
				if (!StringUtil.isEmpty(vo.getFykmid())) {
					YntCpaccountVO accvo = (YntCpaccountVO) map.get(vo.getFykmid());
					if (accvo != null) {
						if (kmShow == 0) {
							vo.setFykmname(accvo.getAccountname());
						} else if (kmShow == 1) {
							String[] fullname = accvo.getFullname().split("/");
							if (fullname.length > 1) {
								vo.setFykmname(fullname[0] + "/" + fullname[fullname.length - 1]);
							} else {
								vo.setFykmname(accvo.getFullname());
							}
						} else {
							vo.setFykmname(accvo.getFullname());
						}
						vo.setFykmkind(accvo.getAccountkind());
						vo.setFykmcode(accvo.getAccountcode());
					}
				}

				if (map != null && map.size() > 0) {
					AuxiliaryAccountBVO personvo = aumap.get(vo.getCpersonid());
					if (personvo != null) {
						vo.setCdeptid(personvo.getCdeptid());
						vo.setYgbm(personvo.getCode());
						vo.setYgname(personvo.getName());
						vo.setZjbm(personvo.getZjbm());
						vo.setZjlx(personvo.getZjlx());
						vo.setVphone(personvo.getVphone());
						if (StringUtil.isEmpty(personvo.getVarea())) {
							if (isAll) {
								vo.setVarea(SalaryReportEnum.getTypeEnumByValue(personvo.getZjlx()).getArea());
							}
						} else {
							vo.setVarea(personvo.getVarea());
						}
						if (personvo.getBirthdate() != null) {
							vo.setVdef3(personvo.getBirthdate().toString());
						}

						if (personvo.getIsex() != null) {
							if (personvo.getIsex().intValue() == 1) {
								vo.setVdef4("男");
							} else if (personvo.getIsex().intValue() == 2) {
								vo.setVdef4("女");
							}
						}
						vo.setVdef5(personvo.getVbirtharea());

						if (personvo.getEntrydate() != null)
							vo.setVdef21(personvo.getEntrydate().toString());

						if (personvo.getLeavedate() != null)
							vo.setVdef22(personvo.getLeavedate().toString());

						if (personvo.getEmployedate() != null)
							vo.setVdef1(personvo.getEmployedate().toString());

						vo.setLhtype(personvo.getLhtype());
						vo.setLhdate(personvo.getLhdate());

						if (personvo.getSffc() != null)
							vo.setSffc(personvo.getSffc());
					}

					AuxiliaryAccountBVO deptvo = aumap.get(vo.getCdeptid());
					if (deptvo != null) {
						vo.setVdeptname(deptvo.getName());
						vo.setCdeptid(deptvo.getPk_auacount_b());
					}
				}
			}
		}
		listall = sortByYgbm(listall);
		return listall.toArray(new SalaryReportVO[listall.size()]);
	}

	private List<SalaryReportVO> sortByYgbm(List<SalaryReportVO> listall) {

		if (listall == null || listall.size() == 0)
			return listall;

		List<SalaryReportVO> list1 = new ArrayList<SalaryReportVO>();
		List<SalaryReportVO> list2 = new ArrayList<SalaryReportVO>();

		for (SalaryReportVO vo : listall) {
			if (StringUtil.isEmpty(vo.getYgbm())) {
				list1.add(vo);
			} else {
				list2.add(vo);
			}
		}
		SalaryReportVO[] vos = list2.toArray(new SalaryReportVO[list2.size()]);
		VOUtil.ascSort(vos, new String[] { "ygbm" });

		Collections.sort(list1, new Comparator<SalaryReportVO>() {
			@Override
			public int compare(SalaryReportVO o1, SalaryReportVO o2) {
				return o1.getZjbm().compareTo(o2.getZjbm());
			}
		});
		for (SalaryReportVO vo : vos) {
			list1.add(vo);
		}
		return list1;
	}

	@Override
	public Object[] saveImpExcel(String loginDate, String cuserid, CorpVO loginCorpInfo, SalaryReportVO[] vos,
								 String opdate, int type, String billtype) throws DZFWarpException {
		String pk_corp = loginCorpInfo.getPk_corp();

		if (vos == null || vos.length == 0) {
			throw new BusinessException("数据为空，请检查！");
		}

		checkPz(pk_corp, opdate, "gzjt", false);
		checkPz(pk_corp, opdate, "gzff", false);

		List<SalaryReportVO> normlist = new ArrayList<>();
		List<SalaryReportVO> remulist = new ArrayList<>();
		List<SalaryReportVO> forelist = new ArrayList<>();
		List<SalaryReportVO> nullist = new ArrayList<>();

		for (SalaryReportVO vo : vos) {
			if (StringUtil.isEmpty(vo.getVproject())) {
				vo.setBilltype(null);
				nullist.add(vo);
			} else {
				if (vo.getVproject().contains("外籍人员正常工资薪金")) {
					vo.setBilltype(SalaryTypeEnum.FOREIGNSALARY.getValue());
					forelist.add(vo);
				} else if (vo.getVproject().contains("正常工资薪金") || vo.getVproject().contains("工资薪金")) {
					vo.setBilltype(SalaryTypeEnum.NORMALSALARY.getValue());
					normlist.add(vo);
				} else if (vo.getVproject().contains("劳务报酬")) {
					vo.setBilltype(SalaryTypeEnum.REMUNERATION.getValue());
					remulist.add(vo);
				} else {
					throw new BusinessException("所得税项目不符合系统类型");
				}
			}
		}

		checkTotal(vos);

		if (nullist != null && nullist.size() > 0) {
			opdate = nullist.get(0).getQj();
			implExcelByType(loginDate, cuserid, loginCorpInfo, nullist.toArray(new SalaryReportVO[nullist.size()]),
					opdate, nullist.get(0).getImpmodeltype(), billtype);
		}

		if (normlist != null && normlist.size() > 0) {
			opdate = normlist.get(0).getQj();
			implExcelByType(loginDate, cuserid, loginCorpInfo, normlist.toArray(new SalaryReportVO[normlist.size()]),
					opdate, normlist.get(0).getImpmodeltype(), normlist.get(0).getBilltype());
		}

		if (forelist != null && forelist.size() > 0) {
			opdate = forelist.get(0).getQj();
			implExcelByType(loginDate, cuserid, loginCorpInfo, forelist.toArray(new SalaryReportVO[forelist.size()]),
					opdate, forelist.get(0).getImpmodeltype(), forelist.get(0).getBilltype());
		}
		if (remulist != null && remulist.size() > 0) {
			opdate = remulist.get(0).getQj();
			implExcelByType(loginDate, cuserid, loginCorpInfo, remulist.toArray(new SalaryReportVO[remulist.size()]),
					opdate, remulist.get(0).getImpmodeltype(), remulist.get(0).getBilltype());
		}
		return query(pk_corp, opdate, billtype);
	}

	private void implExcelByType(String loginDate, String cuserid, CorpVO corpvo, SalaryReportVO[] vos, String opdate,
			int type, String billtype) {
		List<SalaryReportVO> list = new ArrayList<>();
		// 过滤掉空行
		String pk_corp = corpvo.getPrimaryKey();
		for (SalaryReportVO vo : vos) {
			if (vo.getVphone() == null && StringUtil.isEmpty(vo.getZjbm()) && StringUtil.isEmpty(vo.getZjlx())
					&& StringUtil.isEmpty(vo.getYgbm()) && StringUtil.isEmpty(vo.getYgname()) && vo.getYfgz() == null) {

			} else {
				if (StringUtil.isEmpty(vo.getVphone())) {
					if (corpvo != null)
						vo.setVphone(corpvo.getPhone1());
				}
				list.add(vo);
			}
		}

		if (list == null || list.size() == 0)
			throw new BusinessException("数据为空，请检查！");
		checkbeforeSaveImp(opdate, list.toArray(new SalaryReportVO[list.size()]), corpvo, type, billtype);
		SalaryCipherHandler.handlerSalaryVO(list.toArray(new SalaryReportVO[list.size()]), 0); // 加密控制
		clearJs(list.toArray(new SalaryReportVO[0]));
		List<SalaryReportVO> listnew = new ArrayList<SalaryReportVO>();// 新增
		List<SalaryReportVO> listedit = new ArrayList<SalaryReportVO>();// 修改

		for (SalaryReportVO vo : list) {
			if (vo.getPk_salaryreport() == null || StringUtil.isEmpty(vo.getPk_salaryreport())) {
				listnew.add(vo);
			} else {
				listedit.add(vo);
			}
		}
		if (listnew != null && listnew.size() > 0) {
			for (SalaryReportVO vo : listnew) {
				vo.setPk_corp(pk_corp);
				if (StringUtil.isEmpty(vo.getQj())) {
					vo.setQj(opdate);
				}
				if (StringUtil.isEmpty(vo.getVphone())) {
					if (corpvo != null)
						vo.setVphone(corpvo.getPhone1());
				}
				vo.setCoperatorid(cuserid);
				if (StringUtil.isEmpty(vo.getBilltype())) {
					vo.setBilltype(billtype);
				}
			}
			singleObjectBO.insertVOArr(pk_corp, listnew.toArray(new SalaryReportVO[listnew.size()]));
		}
		if (listedit != null && listedit.size() > 0) {
			singleObjectBO.updateAry(listedit.toArray(new SalaryReportVO[listedit.size()]));
		}
	}

	private void checkTotal(SalaryReportVO[] vos) {
		if (vos == null || vos.length == 0)
			return;
		// 计算合计行数据
		DZFDouble d1 = DZFDouble.ZERO_DBL;
		DZFDouble d2 = DZFDouble.ZERO_DBL;
		DZFDouble d3 = DZFDouble.ZERO_DBL;
		DZFDouble d4 = DZFDouble.ZERO_DBL;
		DZFDouble d5 = DZFDouble.ZERO_DBL;
		for (SalaryReportVO svo : vos) {
			if (svo.getYfgz() == null)
				svo.setYfgz(DZFDouble.ZERO_DBL);
			d1 = SafeCompute.add(d1, VoUtils.getDZFDouble(svo.getYfgz()).setScale(2, DZFDouble.ROUND_HALF_UP));
			d2 = SafeCompute.add(d2, VoUtils.getDZFDouble(svo.getYanglaobx()).setScale(2, DZFDouble.ROUND_HALF_UP));
			d3 = SafeCompute.add(d3, VoUtils.getDZFDouble(svo.getYiliaobx()).setScale(2, DZFDouble.ROUND_HALF_UP));
			d4 = SafeCompute.add(d4, VoUtils.getDZFDouble(svo.getShiyebx()).setScale(2, DZFDouble.ROUND_HALF_UP));
			d5 = SafeCompute.add(d5, VoUtils.getDZFDouble(svo.getZfgjj()).setScale(2, DZFDouble.ROUND_HALF_UP));
		}

		// 金额不能大于10亿
		if (d1.compareTo(new DZFDouble(1e9)) >= 0) {
			throw new BusinessException("应发工资汇总金额只能10亿以下的金额！");
		}

		if (d2.compareTo(new DZFDouble(1e9)) >= 0) {
			throw new BusinessException("养老保险汇总金额只能10亿以下的金额！");
		}

		if (d3.compareTo(new DZFDouble(1e9)) >= 0) {
			throw new BusinessException("医疗保险汇总金额只能10亿以下的金额！");
		}

		if (d4.compareTo(new DZFDouble(1e9)) >= 0) {
			throw new BusinessException("失业保险汇总金额只能10亿以下的金额！");
		}
		if (d5.compareTo(new DZFDouble(1e9)) >= 0) {
			throw new BusinessException("公积金汇总金额只能10亿以下的金额！");
		}
	}

	private void checkbeforeSaveImp(String opdate, SalaryReportVO[] vos, CorpVO corpvo, int type, String billtype)
			throws DZFWarpException {
		boolean isimp = true;
		if (vos == null || vos.length == 0) {
			throw new BusinessException("数据为空，请检查！");
		}
		String pk_corp = corpvo.getPrimaryKey();
		if (queryIsGZ(pk_corp, opdate).booleanValue()) {
			throw new BusinessException("当月已关账，不能操作！");
		}

		DZFDate corpdate = corpvo.getBegindate();
		String qj = corpdate.toString().substring(0, 7);
		if (!StringUtil.isEmpty(vos[0].getQj())) {
			if (vos[0].getQj().compareTo(qj) < 0) {
				throw new BusinessException("所属日期必须在建账日之后！");
			}
		}
		SalaryReportVO[] srvos = null;
		List<String> codelist = new ArrayList<String>();
		srvos = queryAllType(pk_corp, opdate);
		if (srvos != null && srvos.length > 0) {
			for (SalaryReportVO vo : srvos) {
				String key = vo.getZjbm() + vo.getBilltype();
				if (!codelist.contains(key)) {
					codelist.add(key);
				}
			}
		}

		SalaryAccSetVO setvo = null;
		try {
			setvo = getSASVO(pk_corp, billtype);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		YntCpaccountVO[] accvos = accService.queryByPk(pk_corp);
		Map<String, YntCpaccountVO> map = AccountUtil.getAccVOByCode(pk_corp, accvos);

		Map<String, AuxiliaryAccountBVO> audeptmap = AccountUtil.getAuxiliaryAccountBVOByName(pk_corp,
				AuxiliaryConstant.ITEM_DEPARTMENT);
		AuxiliaryAccountBVO[] bvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_STAFF, pk_corp, null);
		Map<String, AuxiliaryAccountBVO> aumapkey = new HashMap<>();
		Map<String, AuxiliaryAccountBVO> aumapid = new HashMap<>();

		if (bvos != null && bvos.length > 0) {
			for (AuxiliaryAccountBVO accvo : bvos) {
				aumapkey.put(getPersonKey1(accvo), accvo);
				aumapid.put(accvo.getPk_auacount_b(), accvo);
			}
		}
		List<AuxiliaryAccountBVO> addlist = new ArrayList<>();
		List<AuxiliaryAccountBVO> uplist = new ArrayList<>();

		Map<String, SalaryBaseVO> basemap = gl_gzbbaseserv.getSalaryBaseVO(pk_corp, opdate);
		for (SalaryReportVO vo : vos) {

			if (vo.getYgbm() == null || StringUtil.isEmpty(vo.getYgbm())) {
			}
			if (!StringUtil.isEmpty(vo.getYgbm()) && vo.getYgbm().indexOf(".") > -1) {
				vo.setYgbm(vo.getYgbm().substring(0, vo.getYgbm().indexOf(".")));
			}
			if (vo.getZjlx() == null || StringUtil.isEmpty(vo.getZjlx())) {
				if (type == 1) {
					throw new BusinessException("证照类型不允许为空，请检查！");
				} else {
					throw new BusinessException("证件类型不允许为空，请检查！");
				}
			}
			if (vo.getZjlx().indexOf(".") > -1) {
				vo.setZjlx(vo.getZjlx().substring(0, vo.getZjlx().indexOf(".")));
			}

			if (vo.getZjbm() == null || StringUtil.isEmpty(vo.getZjbm())) {

				if (type == 1) {
					throw new BusinessException("证照编码不允许为空，请检查！");
				} else {
					throw new BusinessException("证件编码不允许为空，请检查！");
				}
			}
			if (vo.getYgname() == null || StringUtil.isEmpty(vo.getYgname())) {
				if (type == 1) {
					throw new BusinessException("姓名不允许为空，请检查！");
				} else {
					throw new BusinessException("员工姓名不允许为空，请检查！");
				}
			}
			if (vo.getYgname().indexOf(".") > -1) {
				vo.setYgname(vo.getYgname().substring(0, vo.getYgname().indexOf(".")));
			}

			if (!StringUtil.isEmpty(vo.getCdeptid())) {
				AuxiliaryAccountBVO bvo = audeptmap.get(vo.getCdeptid());
				if (bvo != null) {
					vo.setCdeptid(bvo.getPk_auacount_b());
				} else {
					throw new BusinessException("部门{" + vo.getCdeptid() + "}不存在，请检查！");
				}
			}
			AuxiliaryAccountBVO bvo = null;
			String key = getPersonKey(vo);
			if (!StringUtil.isEmpty(key)) {
				bvo = aumapkey.get(key);
			}

			if (bvo != null) {
				vo.setCpersonid(bvo.getPk_auacount_b());
				if (!StringUtil.isEmpty(bvo.getBilltype())) {
					if (SalaryTypeEnum.REMUNERATION.getValue().equals(bvo.getBilltype())
							|| SalaryTypeEnum.NORMALSALARY.getValue().equals(bvo.getBilltype())
							|| SalaryTypeEnum.FOREIGNSALARY.getValue().equals(bvo.getBilltype())) {
					}
				}
				bvo = getAuxiliaryAccountBVO(bvo, vo, pk_corp, billtype, opdate, isimp);
				uplist.add(bvo);
			} else {
				bvo = getAuxiliaryAccountBVO(null, vo, pk_corp, billtype, opdate, isimp);
				addlist.add(bvo);
			}

			String key1 = null;
			if (StringUtil.isEmpty(vo.getBilltype())) {
				key1 = getPersonKey(vo) + billtype;
			} else {
				key1 = getPersonKey(vo) + vo.getBilltype();
			}

			List<AuxiliaryAccountBVO> auxiliaryAccountBVOList = gl_fzhsserv.queryPerson(AuxiliaryConstant.ITEM_STAFF,
					pk_corp, null);

			List<String> codeListFc = auxiliaryAccountBVOList.stream()
					.filter(v -> v.getSffc() != null && v.getSffc() == 1).map(v -> {
						String uniqueCheck = null;
						if (StringUtil.isEmpty(v.getBilltype())) {
							uniqueCheck = getPersonKey1(v) + billtype;
						} else {
							uniqueCheck = getPersonKey1(v) + v.getBilltype();
						}
						return uniqueCheck;
					}).collect(Collectors.toList());

			if (!codelist.contains(key1) && !codeListFc.contains(key1)) {
				codelist.add(key1);
			} else {
				if (codelist.contains(key1)) {
					throw new BusinessException("员工：" + vo.getYgname() + ",证件编号：" + vo.getZjbm() + "重复，请检查！");
				}
				if (codeListFc.contains(key1)) {
					throw new BusinessException("员工：" + vo.getYgname() + ",证件编号：" + vo.getZjbm() + "已封存不允许添加！");
				}
			}

			formatNumber(vo, 2);
			gl_gzbcalserv.calSbGjj(vo, setvo, basemap.get(vo.getCpersonid()));
			calGz(vo, billtype, opdate, true);
		}
		checkTotal(vos);
		if (addlist != null && addlist.size() > 0) {
			AuxiliaryAccountBVO[] avos = gl_fzhsserv.saveBs(addlist, true);
			if (avos != null && avos.length > 0) {
				for (SalaryReportVO vo : vos) {
					String key = getPersonKey(vo);
					for (AuxiliaryAccountBVO bvo : avos) {
						if (key.equals(getPersonKey1(bvo))) {
							vo.setCpersonid(bvo.getPk_auacount_b());
						}
					}
				}
			}
		}

		if (uplist != null && uplist.size() > 0) {
			gl_fzhsserv.saveBs(uplist, false);
		}
	}

	private void checkbeforeSave(String opdate, SalaryReportVO[] vos, CorpVO corpvo, int type, String billtype)
			throws DZFWarpException {
		if (vos == null || vos.length == 0) {
			throw new BusinessException("数据为空，请检查！");
		}
		String pk_corp = corpvo.getPrimaryKey();
		if (queryIsGZ(pk_corp, opdate).booleanValue()) {
			throw new BusinessException("当月已关账，不能操作！");
		}

		DZFDate corpdate = corpvo.getBegindate();
		String qj = corpdate.toString().substring(0, 7);
		if (!StringUtil.isEmpty(vos[0].getQj())) {
			if (vos[0].getQj().compareTo(qj) < 0) {
				throw new BusinessException("所属日期必须在建账日之后！");
			}
		}

		List<String> pkList = new ArrayList<String>();
		for (SalaryReportVO vo : vos) {
			if (!pkList.contains(vo.getPk_salaryreport())) {
				pkList.add(vo.getPk_salaryreport());
			}
		}
		SalaryReportVO[] srvos = null;
		List<String> personidlist = new ArrayList<String>();
		srvos = queryAllType(pk_corp, opdate);
		if (srvos != null && srvos.length > 0) {
			for (SalaryReportVO vo : srvos) {
				if (!pkList.contains(vo.getPk_salaryreport())) {
					String key = vo.getCpersonid();
					if (!personidlist.contains(key)) {
						personidlist.add(key);
					}
				}
			}
		}
		SalaryAccSetVO setvo = null;
		try {
			setvo = getSASVO(pk_corp, billtype);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		Map<String, SalaryBaseVO> basemap = gl_gzbbaseserv.getSalaryBaseVO(pk_corp, opdate);
		List<AuxiliaryAccountBVO> auxiliaryAccountBVOList = gl_fzhsserv.queryPerson(AuxiliaryConstant.ITEM_STAFF,
				pk_corp, null);
		List<String> codeListFc = auxiliaryAccountBVOList.stream().filter(v -> v.getSffc() != null && v.getSffc() == 1)
				.map(v -> {
					return v.getPk_auacount_b();
				}).collect(Collectors.toList());
		for (SalaryReportVO vo : vos) {
			if (!personidlist.contains(vo.getCpersonid()) && !codeListFc.contains(vo.getCpersonid())) {
				personidlist.add(vo.getCpersonid());
			} else {
				if (vo.getCpersonid().contains(vo.getCpersonid())) {
					throw new BusinessException("员工：" + vo.getYgname() + ",证件编号：" + vo.getZjbm() + "工资表数据重复，请检查！");
				}
				if (codeListFc.contains(vo.getCpersonid())) {
					throw new BusinessException("员工：" + vo.getYgname() + ",证件编号：" + vo.getZjbm() + "已封存不允许添加！");
				}
			}
			formatNumber(vo, 2);
			gl_gzbcalserv.calSbGjj(vo, setvo, basemap.get(vo.getCpersonid()));
			calGz(vo, billtype, opdate, true);
		}
		checkTotal(vos);
	}

	private void setDefault(AuxiliaryAccountBVO bvo) {

		String zjlx = bvo.getZjlx();
		String zjbm = bvo.getZjbm();
		int len = zjbm.length();
		if ("身份证".equals(zjlx) && len == 18) {
			bvo.setIsex(Integer.parseInt(IdCard(zjbm, 2)));
			bvo.setBirthdate(new DZFDate(IdCard(zjbm, 1)));
		}

		if ("身份证".equals(zjlx) || "护照".equals(zjlx)) {
			bvo.setVarea(SalaryReportEnum.CHINACARD.getArea());
			bvo.setVbirtharea(SalaryReportEnum.CHINACARD.getArea());
		} else {
			if ("港澳居民居住证".equals(zjlx) || "港澳居民来往内地通行证".equals(zjlx)) {
				bvo.setVarea(SalaryReportEnum.GACARD.getArea());
				bvo.setVbirtharea(SalaryReportEnum.GACARD.getArea());
			} else if ("台湾居民来往大陆通行证".equals(zjlx) || "台湾身份证".equals(zjlx)) {
				bvo.setVarea(SalaryReportEnum.TAICARD.getArea());
				bvo.setVbirtharea(SalaryReportEnum.TAICARD.getArea());
			} else {
			}
		}
	}

	private String IdCard(String UUserCard, int num) {
		String temp = null;
		if (num == 1) {
			// 获取出生日期
			temp = UUserCard.substring(6, 10) + "-" + UUserCard.substring(10, 12) + "-" + UUserCard.substring(12, 14);
		}
		if (num == 2) {
			// 获取性别
			if (Integer.parseInt(UUserCard.substring(16, 17)) % 2 == 1) {
				// 男
				temp = "1";
			} else if (Integer.parseInt(UUserCard.substring(16, 17)) % 2 == 0) {
				// 女
				temp = "2";
			} else {
				temp = "0";
			}
		}
		if (num == 3) {
			// 获取年龄
			DZFDate myDate = new DZFDate();
			int month = myDate.getMonth() + 1;
			int day = myDate.getDay();
			int age = myDate.getYear() - Integer.parseInt(UUserCard.substring(6, 10)) - 1;
			if (Integer.parseInt(UUserCard.substring(10, 12)) < month
					|| Integer.parseInt(UUserCard.substring(10, 12)) == month
							&& Integer.parseInt(UUserCard.substring(12, 14)) <= day) {
				age++;
			}
			temp = Integer.toString(age);
		}
		return temp;
	}

	private String getPersonKey(SalaryReportVO vo) {
		String key = vo.getZjbm();
		return key;
	}

	private String getPersonKey1(AuxiliaryAccountBVO vo) {
		String key = vo.getZjbm();
		return key;
	}

	private AuxiliaryAccountBVO getAuxiliaryAccountBVO(AuxiliaryAccountBVO bvo, SalaryReportVO vo, String pk_corp,
			String billtype, String opdate, boolean isimp) {

		if (bvo == null) {
			bvo = new AuxiliaryAccountBVO();
			if (!StringUtil.isEmpty(vo.getQj())) {
				bvo.setEmployedate(new DZFDate(vo.getQj() + "-01"));
			} else {
				bvo.setEmployedate(new DZFDate(opdate + "-01"));
			}
		} else {
			if (!StringUtil.isEmpty(vo.getYgbm()))
				bvo.setCode(vo.getYgbm());
		}

		if (isimp) {
			bvo.setIsimp(DZFBoolean.TRUE);
		} else {
			bvo.setIsimp(DZFBoolean.FALSE);
		}

		bvo.setName(vo.getYgname());
		if (!StringUtil.isEmpty(vo.getCdeptid()))
			bvo.setCdeptid(vo.getCdeptid());
		bvo.setZjbm(vo.getZjbm());
		bvo.setZjlx(vo.getZjlx());
		if (StringUtil.isEmpty(vo.getBilltype())) {
			bvo.setBilltype(billtype);
		} else {
			bvo.setBilltype(vo.getBilltype());
		}
		if (!StringUtil.isEmpty(vo.getLhtype()))
			bvo.setLhtype(vo.getLhtype());
		if (vo.getLhdate() != null)
			bvo.setLhdate(vo.getLhdate());
		if (!StringUtil.isEmpty(vo.getVarea()))
			bvo.setVarea(vo.getVarea());
		if (!StringUtil.isEmpty(vo.getVphone()))
			bvo.setVphone(vo.getVphone());
		bvo.setPk_corp(pk_corp);
		bvo.setPk_auacount_h(AuxiliaryConstant.ITEM_STAFF);
		// gl_fzhsserv.saveB(bvo);
		// vo.setCpersonid(bvo.getPk_auacount_b());
		setDefault(bvo);
		return bvo;
	}

	private void formatNumber(SalaryReportVO vo, int power) {

		if (vo.getYfgz() != null)
			vo.setYfgz(vo.getYfgz().setScale(power, 0));
		if (vo.getYanglaobx() != null)
			vo.setYanglaobx(vo.getYanglaobx().setScale(power, 0));
		if (vo.getYiliaobx() != null)
			vo.setYiliaobx(vo.getYiliaobx().setScale(power, 0));
		if (vo.getShiyebx() != null)
			vo.setShiyebx(vo.getShiyebx().setScale(power, 0));
		if (vo.getZfgjj() != null)
			vo.setZfgjj(vo.getZfgjj().setScale(power, 0));
		if (vo.getYnssde() != null)
			vo.setYnssde(vo.getYnssde().setScale(power, 0));
		if (vo.getGrsds() != null)
			vo.setGrsds(vo.getGrsds().setScale(power, 0));
		if (vo.getSfgz() != null)
			vo.setSfgz(vo.getSfgz().setScale(power, 0));
	}

	private void calGz(SalaryReportVO vo, String billtype, String opdate, boolean isAllowZero) {

		if (StringUtil.isEmpty(billtype) || billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {

		} else {
			vo.setYanglaobx(DZFDouble.ZERO_DBL);
			vo.setYiliaobx(DZFDouble.ZERO_DBL);
			vo.setShiyebx(DZFDouble.ZERO_DBL);
			vo.setZfgjj(DZFDouble.ZERO_DBL);
			vo.setQyyanglaobx(DZFDouble.ZERO_DBL);
			vo.setQyyiliaobx(DZFDouble.ZERO_DBL);
			vo.setQyshiyebx(DZFDouble.ZERO_DBL);
			vo.setQyzfgjj(DZFDouble.ZERO_DBL);
			vo.setQygsbx(DZFDouble.ZERO_DBL);
			vo.setQyshybx(DZFDouble.ZERO_DBL);
		}
		if (vo.getYfgz() == null || vo.getYfgz().doubleValue() < 0) {
			// throw new BusinessException("员工..不允许为空，请检查！");
			// 应发工资<=0 数据全部置空
			if (isAllowZero) {
				vo.setYanglaobx(DZFDouble.ZERO_DBL);
				vo.setYiliaobx(DZFDouble.ZERO_DBL);
				vo.setShiyebx(DZFDouble.ZERO_DBL);
				vo.setZfgjj(DZFDouble.ZERO_DBL);
				vo.setQyyanglaobx(DZFDouble.ZERO_DBL);
				vo.setQyyiliaobx(DZFDouble.ZERO_DBL);
				vo.setQyshiyebx(DZFDouble.ZERO_DBL);
				vo.setQyzfgjj(DZFDouble.ZERO_DBL);
				vo.setQygsbx(DZFDouble.ZERO_DBL);
				vo.setQyshybx(DZFDouble.ZERO_DBL);
				vo.setYnssde(DZFDouble.ZERO_DBL);
				vo.setShuilv(DZFDouble.ZERO_DBL);
				vo.setGrsds(DZFDouble.ZERO_DBL);
				vo.setSfgz(DZFDouble.ZERO_DBL);
			}
		} else {

			// 正常薪金
			if (StringUtil.isEmpty(billtype) || billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
				if ("2019-01".compareTo(opdate) > 0) {
					calOldData(vo, billtype, opdate);
				} else {
					calNewNormalData(vo, billtype, opdate);
				}
				// 外籍薪金
			} else if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
				calOldData(vo, billtype, opdate);
				// 劳务
			} else if (billtype.equals(SalaryTypeEnum.REMUNERATION.getValue())) {
				calOldData(vo, billtype, opdate);
			}
		}
	}

	private void calNewNormalData(SalaryReportVO vo, String billtype, String opdate) {
		// 累计收入额
		DZFDouble sr = vo.getLjsre();

		// 累计已预扣预缴税额
		DZFDouble yykse = vo.getYyjse();

		// 累计减除费用
		DZFDouble jcfy = vo.getLjjcfy();

		// 累计专项扣除 = 当年累计五险一金
		DZFDouble zxkc = vo.getLjzxkc();

		// 计算累计专项附加抵扣合计
		DZFDouble zxdk = getPeriodLjZxdc(vo);

		// 累计预扣预缴应纳税所得额=累计收入-累计减除费用-累计专项扣除-累计专项附加扣除
		DZFDouble[] dous = new DZFDouble[] { sr, jcfy, zxkc, zxdk };
		DZFDouble ynssde = subByDZFDouble(dous);
		if (ynssde.doubleValue() < 0)
			ynssde = DZFDouble.ZERO_DBL;
		vo.setYnssde(ynssde);
		double[] taxs = getNewNormalTax(ynssde.doubleValue());

		// 计算税率 =累计预扣预缴应纳税所得额对应的税率
		DZFDouble shuilv = new DZFDouble(taxs[0]);
		vo.setShuilv(shuilv);
		// 计算速算扣除数 =累计预扣预缴应纳税所得额对应的扣除数
		DZFDouble quickdeduction = new DZFDouble(taxs[1]);

		vo.setYyjse(yykse);
		// 累计应纳税额 =累计预扣预缴应纳税所得额×预扣率-速算扣除数
		DZFDouble ynse = SafeCompute.sub(SafeCompute.multiply(ynssde, SafeCompute.div(shuilv, new DZFDouble(100))),
				quickdeduction);
		vo.setLjynse(ynse);
		// 本期应预扣预缴税额=累计应纳税额-累计已预扣预缴税额
		DZFDouble grsds = SafeCompute.sub(ynse, yykse);
		if (grsds.doubleValue() <= 0) {
			grsds = DZFDouble.ZERO_DBL;
		}
		vo.setGrsds(grsds);
		dous = new DZFDouble[] { vo.getYfgz(), getPeriodZxkc(vo), grsds };
		DZFDouble sfgz = subByDZFDouble(dous);
		if (sfgz.doubleValue() < 0)
			sfgz = DZFDouble.ZERO_DBL;
		vo.setSfgz(sfgz);
	}

	private void calOldData(SalaryReportVO vo, String billtype, String opdate) {
		double kcnum = getkcnum(billtype, opdate);
		DZFDouble d1 = SafeCompute.sub(VoUtils.getDZFDouble(vo.getYfgz()), VoUtils.getDZFDouble(vo.getYanglaobx()));
		DZFDouble d2 = SafeCompute.sub(d1, VoUtils.getDZFDouble(vo.getYiliaobx()));
		DZFDouble d3 = SafeCompute.sub(d2, VoUtils.getDZFDouble(vo.getShiyebx()));
		DZFDouble ynssde = SafeCompute.sub(d3, VoUtils.getDZFDouble(vo.getZfgjj()));
		if (ynssde.doubleValue() <= 0) {
			ynssde = DZFDouble.ZERO_DBL;
		}
		DZFDouble grsds = new DZFDouble(getTax(ynssde.doubleValue(), kcnum, billtype, opdate)).setScale(2,
				DZFDouble.ROUND_HALF_UP);
		if (grsds.doubleValue() <= 0) {
			grsds = DZFDouble.ZERO_DBL;
		}
		DZFDouble shuilv = new DZFDouble(getShuilv(ynssde.doubleValue(), kcnum, billtype, opdate));

		DZFDouble sfgz = SafeCompute.sub(ynssde, grsds);
		vo.setShuilv(shuilv);
		DZFDouble taxbase = SafeCompute.sub(ynssde, new DZFDouble(kcnum));
		if (taxbase.doubleValue() <= 0)// 低于个税起征点
		{
			vo.setYnssde(DZFDouble.ZERO_DBL);
		} else {
			vo.setYnssde(taxbase);
		}

		vo.setGrsds(grsds);
		if (sfgz.doubleValue() <= 0) {
			vo.setSfgz(DZFDouble.ZERO_DBL);
		} else {
			vo.setSfgz(sfgz);
		}
	}

	public SalaryReportVO[] calLjData(String pk_corp, String cpersonids, String billtype, String opdate)
			throws BusinessException {
		List<SalaryReportVO> listvo = new ArrayList<>();

		SalaryAccSetVO setvo = getSASVO(pk_corp, SalaryTypeEnum.NORMALSALARY.getValue());
		if (StringUtil.isEmpty(cpersonids)) {
			SalaryReportVO vo = new SalaryReportVO();
			gl_gzbcalserv.calSbGjj(vo, setvo, null);
			double kcmun = getnewkcnum(billtype, opdate);
			DZFDouble dkcmun = new DZFDouble(kcmun);
			vo.setLjjcfy(dkcmun);
			vo.setLjzxkc(getPeriodZxkc(vo));
			listvo.add(vo);
		} else {
			String pks[] = DZFStringUtil.getString2Array(cpersonids, ",");
			if (DZFValueCheck.isNotEmpty(pks)) {
				List<String> list = Arrays.asList(pks);
				if (list == null || list.size() == 0)
					return new SalaryReportVO[0];

				Map<String, SalaryReportVO> lastperiodmap = getLastPeriodSalaryReport(pk_corp, opdate);

				Map<String, SalaryReportVO> lasttotalmap = getLastTotailSalaryReport(pk_corp, billtype, opdate);
				Map<String, SalaryReportVO> latelyperiodmap = getLatelyPeriodSalaryReport(pk_corp, opdate);
				SalaryReportVO vo = null;
				Map<String, YntCpaccountVO> map = accService.queryMapByPk(pk_corp);
				GxhszVO gxh = gl_gxhszserv.query(pk_corp);
				Integer kmShow = gxh.getSubjectShow();
				Map<String, SalaryBaseVO> basemap = gl_gzbbaseserv.getSalaryBaseVO(pk_corp, opdate);
				for (String cpersonid : list) {
					vo = new SalaryReportVO();
					setCurTaxData(latelyperiodmap, cpersonid, vo, billtype, opdate, pk_corp, false);
					gl_gzbcalserv.calSbGjj(vo, setvo, basemap.get(cpersonid));
					setLjTaxData(lastperiodmap, lasttotalmap, cpersonid, vo, billtype, opdate);
					vo.setCpersonid(cpersonid);
					setShowKm(setvo, map, kmShow, vo);
					calGz(vo, vo.getBilltype(), opdate, false);
					listvo.add(vo);
				}
			}
		}
		SalaryReportVO[] vos = listvo.toArray(new SalaryReportVO[listvo.size()]);
		setTotalData(vos);
		return vos;
	}

	private void setTotalData(SalaryReportVO[] vos) {

		if (DZFValueCheck.isEmpty(vos)) {
			return;
		}

		for (SalaryReportVO vo : vos) {
			vo.setZxkcxj(getPeriodZxkc(vo));
			vo.setZxkcfjxj(getPeriodZxdc(vo));
		}
	}

	private void setShowKm(SalaryAccSetVO setvo, Map<String, YntCpaccountVO> map, Integer kmShow, SalaryReportVO vo) {
		if (!StringUtil.isEmpty(setvo.getJtgz_gzfykm())) {
			YntCpaccountVO accvo = (YntCpaccountVO) map.get(setvo.getJtgz_gzfykm());
			if (accvo != null) {
				if (kmShow == 0) {
					vo.setFykmname(accvo.getAccountname());
				} else if (kmShow == 1) {
					String[] fullname = accvo.getFullname().split("/");
					if (fullname.length > 1) {
						vo.setFykmname(fullname[0] + "/" + fullname[fullname.length - 1]);
					} else {
						vo.setFykmname(accvo.getFullname());
					}
				} else {
					vo.setFykmname(accvo.getFullname());
				}
				vo.setFykmkind(accvo.getAccountkind());
				vo.setFykmcode(accvo.getAccountcode());
			}
		}

	}

	private void setCurTaxData(Map<String, SalaryReportVO> latelyperiodmap, String cpersonid, SalaryReportVO vo,
			String billtype, String opdate, String pk_corp, boolean isCopy) {
		SalaryReportVO lastvo = latelyperiodmap.get(cpersonid);
		if (lastvo == null) {

		} else {
			if (vo.getYfgz() == null || vo.getYfgz().doubleValue() == 0)
				vo.setYfgz(lastvo.getYfgz());
			vo.setJxjyzc(lastvo.getJxjyzc());
			vo.setSylrzc(lastvo.getSylrzc());
			vo.setZfdkzc(lastvo.getZfdkzc());
			vo.setZfzjzc(lastvo.getZfzjzc());
			vo.setZnjyzc(lastvo.getZnjyzc());
			vo.setYiliaobx(lastvo.getYiliaobx());
			vo.setYanglaobx(lastvo.getYanglaobx());
			vo.setZfgjj(lastvo.getZfgjj());
			vo.setShiyebx(lastvo.getShiyebx());
		}
	}

	private void setLjTaxData(Map<String, SalaryReportVO> lastperiodmap, Map<String, SalaryReportVO> lasttotalmap,
			String cpersonid, SalaryReportVO vo, String billtype, String opdate) {

		// 计算累计专项附加扣除 = 上期+本期 （新增的 赡养老人 住房租金等）
		// 用于取上期的累计数据
		SalaryReportVO lastvo = lastperiodmap.get(cpersonid);
		if (lastvo == null) {
			// 取截止到上期累计数据
			lastvo = lasttotalmap.get(cpersonid);
			if (lastvo == null)
				lastvo = new SalaryReportVO();
			// 已预扣预缴税额
			vo.setYyjse(lastvo.getYyjse());
		} else {
			// 已预扣预缴税额
			vo.setYyjse(SafeCompute.add(lastvo.getYyjse(), lastvo.getGrsds()));
		}
		if (opdate.endsWith("-01")) {

			DZFDouble sr = vo.getYfgz();
			vo.setLjsre(sr);

			vo.setLjjxjyzc(vo.getJxjyzc());
			vo.setLjsylrzc(vo.getSylrzc());
			vo.setLjzfdkzc(vo.getZfdkzc());
			vo.setLjzfzjzc(vo.getZfzjzc());
			vo.setLjznjyzc(vo.getZnjyzc());

			double kcmun = getnewkcnum(billtype, opdate);
			DZFDouble dkcmun = new DZFDouble(kcmun);
			// 计算累计减除费用 = 当年累计的扣除数 5000
			vo.setLjjcfy(dkcmun);

			// 计算累计专项扣除 = 当年累计五险一金
			vo.setLjzxkc(getPeriodZxkc(vo));
			vo.setYyjse(DZFDouble.ZERO_DBL);
		} else {
			// 计算累计收入 = 上期+本期
			DZFDouble sr = SafeCompute.add(lastvo.getLjsre(), vo.getYfgz());
			vo.setLjsre(sr);

			vo.setLjjxjyzc(SafeCompute.add(lastvo.getLjjxjyzc(), vo.getJxjyzc()));
			vo.setLjsylrzc(SafeCompute.add(lastvo.getLjsylrzc(), vo.getSylrzc()));
			vo.setLjzfdkzc(SafeCompute.add(lastvo.getLjzfdkzc(), vo.getZfdkzc()));
			vo.setLjzfzjzc(SafeCompute.add(lastvo.getLjzfzjzc(), vo.getZfzjzc()));
			vo.setLjznjyzc(SafeCompute.add(lastvo.getLjznjyzc(), vo.getZnjyzc()));

			double kcmun = getnewkcnum(billtype, opdate);
			DZFDouble dkcmun = new DZFDouble(kcmun);
			// 计算累计减除费用 = 当年累计的扣除数 5000
			vo.setLjjcfy(SafeCompute.add(lastvo.getLjjcfy(), dkcmun));

			// 计算累计专项扣除 = 当年累计五险一金
			vo.setLjzxkc(SafeCompute.add(lastvo.getLjzxkc(), getPeriodZxkc(vo)));
		}
	}

	private double getnewkcnum(String billtype, String qj) {

		double kcnum = 0;
		if (StringUtil.isEmpty(billtype) || billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
			kcnum = 5000;
		} else if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
			kcnum = 4800;
		}
		return kcnum;
	}

	private double getkcnum(String billtype, String qj) {

		double kcnum = 0;
		if (StringUtil.isEmpty(billtype) || billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {

			if ("2018-10".compareTo(qj) <= 0) {
				kcnum = 5000;
			} else {
				kcnum = 3500;
			}
		} else if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
			if ("2018-10".compareTo(qj) <= 0) {
				kcnum = 5000;
			} else {
				kcnum = 4800;
			}
		}
		return kcnum;
	}

	private void checkRefDoc(SalaryReportVO[] vos, String pk_corp, Map<String, YntCpaccountVO> map) {
		// checkAccRefDoc(vos, map);

		Map<String, AuxiliaryAccountBVO> aumap = AccountUtil.getAuxiliaryAccountBVOByPk(pk_corp,
				AuxiliaryConstant.ITEM_DEPARTMENT);
		checkDeptRefDoc(vos, aumap);

		aumap = AccountUtil.getAuxiliaryAccountBVOByPk(pk_corp, AuxiliaryConstant.ITEM_STAFF);
		checkPsnRefDoc(vos, aumap);
	}

	private void checkDeptRefDoc(SalaryReportVO[] vos, Map<String, AuxiliaryAccountBVO> aumap) {

		if (vos == null || vos.length == 0)
			return;

		for (SalaryReportVO vo : vos) {
			if (StringUtil.isEmpty(vo.getCdeptid()))
				continue;
			if (aumap.get(vo.getCdeptid()) == null)
				throw new BusinessException("部门[" + vo.getVdeptname() + "]不存在,或者已删除!");
		}

	}

	private void checkPsnRefDoc(SalaryReportVO[] vos, Map<String, AuxiliaryAccountBVO> aumap) {

		if (vos == null || vos.length == 0)
			return;

		for (SalaryReportVO vo : vos) {
			if (StringUtil.isEmpty(vo.getCpersonid()))
				continue;
			if (aumap.get(vo.getCpersonid()) == null)
				throw new BusinessException("员工[" + vo.getYgname() + "]不存在,或者已删除!");
		}

	}

	private void checkAccRefDoc(SalaryReportVO[] vos, Map<String, YntCpaccountVO> map) {
		if (vos == null || vos.length == 0)
			return;

		for (SalaryReportVO vo : vos) {
			if (StringUtil.isEmpty(vo.getFykmid()))
				continue;
			if (map.get(vo.getFykmid()) == null)
				throw new BusinessException("科目不存在,或者已删除!");
		}

	}

	public double getShuilv(double salaryBeforeTax, double kcnum, String billtype, String qj) {
		double taxbase = 0;
		if (StringUtil.isEmpty(billtype) || billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
			taxbase = getPersonShuilv(salaryBeforeTax, kcnum, qj);
		} else if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
			taxbase = getPersonShuilv(salaryBeforeTax, kcnum, qj);
		} else if (billtype.equals(SalaryTypeEnum.REMUNERATION.getValue())) {
			taxbase = getRemunerationShuilv(salaryBeforeTax, kcnum);
		}
		return taxbase;
	}

	private double getPersonShuilv(double salaryBeforeTax, double kcnum, String qj) {
		double taxbase = salaryBeforeTax - kcnum;
		double Taxrate = 0;// 这里税率没有除以百分比；
		if ("2018-10".compareTo(qj) <= 0) {
			if (taxbase <= 0)// 低于个税起征点
			{
				return 0;
			} else if (taxbase <= 3000) {
				Taxrate = 3;
			} else if (taxbase <= 12000) {
				Taxrate = 10;
			} else if (taxbase <= 25000) {
				Taxrate = 20;
			} else if (taxbase <= 35000) {
				Taxrate = 25;
			} else if (taxbase <= 55000) {
				Taxrate = 30;
			} else if (taxbase <= 80000) {
				Taxrate = 35;
			} else {
				Taxrate = 45;
			}
		} else {
			if (taxbase <= 0)// 低于个税起征点
			{
				return 0;
			} else if (taxbase <= 1500) {
				Taxrate = 3;
			} else if (taxbase <= 4500) {
				Taxrate = 10;
			} else if (taxbase <= 9000) {
				Taxrate = 20;
			} else if (taxbase <= 35000) {
				Taxrate = 25;
			} else if (taxbase <= 55000) {
				Taxrate = 30;
			} else if (taxbase <= 80000) {
				Taxrate = 35;
			} else {
				Taxrate = 45;
			}
		}
		return Taxrate;
	}

	private double getRemunerationShuilv(double salaryBeforeTax, double kcnum) {
		double taxbase = salaryBeforeTax - kcnum;
		if (taxbase <= 0)// 低于个税起征点
		{
			return 0;
		} else if (taxbase <= 800) {
			taxbase = 0;
		} else if (taxbase <= 4000) {
			taxbase = 20;
		} else if (taxbase <= 20000) {
			taxbase = 20;
		} else if (taxbase <= 50000) {
			taxbase = 30;
		} else {
			taxbase = 40;
		}
		return taxbase;
	}

	public double getTax(double salaryBeforeTax, double kcnum, String billtype, String qj) {
		double taxbase = 0;

		if (StringUtil.isEmpty(billtype) || billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
			taxbase = getPersonTax(salaryBeforeTax, kcnum, qj);
		} else if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
			taxbase = getPersonTax(salaryBeforeTax, kcnum, qj);
		} else if (billtype.equals(SalaryTypeEnum.REMUNERATION.getValue())) {
			taxbase = getRemunerationTax(salaryBeforeTax, kcnum);
		}
		return taxbase;
	}

	private double getRemunerationTax(double salaryBeforeTax, double kcnum) {
		double taxbase = 0;
		// 应纳税所得额 = 劳务报酬（少于4000元） - 800元
		//
		// 应纳税所得额 = 劳务报酬（超过4000元） × （1 - 20%）
		//
		// 应纳税额 = 应纳税所得额 × 适用税率 - 速算扣除数

		// 1 不超过20,000元 20% 0
		// 2 超过20,000元至50,000元的部分 30% 2,000
		// 3 超过50,000元的部分 40% 7,000
		taxbase = salaryBeforeTax;
		double Taxrate = 0;// 这里税率没有除以百分比；
		double Quickdeduction = 0;
		if (taxbase <= 0)// 低于个税起征点
		{
			return 0;
		} else if (taxbase <= 800) {
			return 0;
		} else if (taxbase < 4000) {
			return (taxbase - 800) * 0.2;
		} else if (taxbase < 20000) {
			Taxrate = 20;
		} else if (taxbase < 50000) {
			Taxrate = 30;
			Quickdeduction = 2000;
		} else {
			Taxrate = 40;
			Quickdeduction = 7000;
		}
		taxbase = (taxbase * (1 - 0.2) * Taxrate / 100 - Quickdeduction);
		return taxbase;
	}

	private double getPersonTax(double salaryBeforeTax, double kcnum, String qj) {
		// （3W-3.5K）*25%-1005
		// 扣税公式是：
		// （扣除社保医保公积金后薪水-个税起征点）*税率-速算扣除数
		double taxbase = salaryBeforeTax - kcnum;
		double Taxrate = 0;// 这里税率没有除以百分比；
		double Quickdeduction = 0;

		if ("2018-10".compareTo(qj) <= 0) {
			if (taxbase <= 0)// 低于个税起征点
			{
				return 0;
			} else if (taxbase <= 3000) {
				Taxrate = 3;
				Quickdeduction = 0;
			} else if (taxbase <= 12000) {
				Taxrate = 10;
				Quickdeduction = 210;
			} else if (taxbase <= 25000) {
				Taxrate = 20;
				Quickdeduction = 1410;
			} else if (taxbase <= 35000) {
				Taxrate = 25;
				Quickdeduction = 2660;
			} else if (taxbase <= 55000) {
				Taxrate = 30;
				Quickdeduction = 4410;
			} else if (taxbase <= 80000) {
				Taxrate = 35;
				Quickdeduction = 7160;
			} else {
				Taxrate = 45;
				Quickdeduction = 15160;
			}
		} else {
			if (taxbase <= 0)// 低于个税起征点
			{
				return 0;
			} else if (taxbase <= 1500) {
				Taxrate = 3;
				Quickdeduction = 0;
			} else if (taxbase <= 4500) {
				Taxrate = 10;
				Quickdeduction = 105;
			} else if (taxbase <= 9000) {
				Taxrate = 20;
				Quickdeduction = 555;
			} else if (taxbase <= 35000) {
				Taxrate = 25;
				Quickdeduction = 1005;
			} else if (taxbase <= 55000) {
				Taxrate = 30;
				Quickdeduction = 2755;
			} else if (taxbase <= 80000) {
				Taxrate = 35;
				Quickdeduction = 5505;
			} else {
				Taxrate = 45;
				Quickdeduction = 13505;
			}
		}

		return ((salaryBeforeTax - kcnum) * Taxrate / 100 - Quickdeduction);
	}

	private double[] getNewNormalTax(double taxbase) {
		double Quickdeduction = 0;
		double Taxrate = 0;
		// 不超过 36000 元 3 0
		// 2 超过 36000 元至 144000 元的部分 10 2520
		// 3 超过 144000 元至 300000 元的部分 20 16920
		// 4 超过 300000 元至 420000 元的部分 25 31920
		// 5 超过 420000 元至 660000 元的部分 30 52920
		// 6 超过 660000 元至 960000 元的部分 35 85920
		// 7 超过 960000 元的部分 45 181920
		double[] dous = new double[2];
		if (taxbase <= 0)// 低于个税起征点
		{
			Taxrate = 0;
			Quickdeduction = 0;
		} else if (taxbase <= 36000)//
		{
			Taxrate = 3;
			Quickdeduction = 0;
		} else if (taxbase <= 144000) {
			Taxrate = 10;
			Quickdeduction = 2520;
		} else if (taxbase <= 300000) {
			Taxrate = 20;
			Quickdeduction = 16920;
		} else if (taxbase <= 420000) {
			Taxrate = 25;
			Quickdeduction = 31920;
		} else if (taxbase <= 660000) {
			Taxrate = 30;
			Quickdeduction = 52920;
		} else if (taxbase <= 960000) {
			Taxrate = 35;
			Quickdeduction = 85920;
		} else {
			Taxrate = 45;
			Quickdeduction = 181920;
		}
		dous[0] = Taxrate;
		dous[1] = Quickdeduction;
		return dous;
	}

	@Override
	public SalaryReportVO[] saveCopyByMonth(String pk_corp, String copyFromdate, String copyTodate, String cuserid,
			String billtype) throws DZFWarpException {
		if (copyFromdate == null || "".equals(copyFromdate) || copyTodate == null || "".equals(copyTodate)) {
			throw new BusinessException("请设置要复制的日期！");
		}
		if (copyFromdate.compareTo(copyTodate) == 0) {
			throw new BusinessException("请选择不同的日期！");
		}

		SalaryReportVO[] copyFromdatevos = null;
		if (!StringUtil.isEmpty(billtype)) {
			copyFromdatevos = query(pk_corp, copyFromdate, billtype);
		} else {
			copyFromdatevos = queryAllType(pk_corp, copyFromdate);
		}

		if (copyFromdatevos == null || copyFromdatevos.length == 0) {
			throw new BusinessException("被复制的会计期间内没有工资表数据！");
		}

		List<SalaryReportVO> list = new ArrayList<>();
		for (SalaryReportVO copyvo : copyFromdatevos) {
			if (copyvo.getSffc() == null || copyvo.getSffc() != 1) {
				list.add(copyvo);
			}
		}

		if (list != null && list.size() > 0) {
			copyFromdatevos = list.toArray(new SalaryReportVO[list.size()]);
		}

		if (copyFromdatevos == null || copyFromdatevos.length == 0) {
			throw new BusinessException("被复制的会计期间内没有工资表数据！");
		}

		SalaryReportVO[] copyTodatevos = null;
		if (!StringUtil.isEmpty(billtype)) {
			copyTodatevos = query(pk_corp, copyTodate, billtype);
		} else {
			copyTodatevos = queryAllType(pk_corp, copyTodate);
		}
		if (copyTodatevos != null && copyTodatevos.length > 0) {
			throw new BusinessException("复制的" + copyTodate + "期间内已经存在工资表，不允许复制！");
		}

		SalaryAccSetVO setvo = getSASVO(pk_corp, SalaryTypeEnum.NORMALSALARY.getValue());
		Map<String, SalaryReportVO> lastperiodmap = getLastPeriodSalaryReport(pk_corp, copyTodate);
		Map<String, SalaryReportVO> lasttotalmap = getLastTotailSalaryReport(pk_corp, billtype, copyTodate);
		Map<String, SalaryBaseVO> basemap = gl_gzbbaseserv.getSalaryBaseVO(pk_corp, copyTodate);
		for (SalaryReportVO vo : copyFromdatevos) {
			vo.setPk_salaryreport(null);// 主键置空
			vo.setQj(copyTodate);// 变日期
			if (StringUtil.isEmpty(vo.getVphone())) {
			}
			vo.setCoperatorid(cuserid);

			gl_gzbcalserv.calSbGjj(vo, setvo, basemap.get(vo.getCpersonid()), billtype, true, true);
			setLjTaxData(lastperiodmap, lasttotalmap, vo.getCpersonid(), vo, billtype, copyTodate);

			calGz(vo, vo.getBilltype(), copyTodate, true);
			if (copyTodate.compareTo("2019-01") >= 0) {
				vo.setFykmid(null);
			}
		}
		SalaryCipherHandler.handlerSalaryVO(copyFromdatevos, 0); // 加密控制
		clearJs(copyFromdatevos);
		singleObjectBO.insertVOArr(pk_corp, copyFromdatevos);

		SalaryReportVO[] vos = query(pk_corp, copyTodate, billtype);
		return vos;
	}

	// 取最近月份的数据
	private Map<String, SalaryReportVO> getLatelyPeriodSalaryReport(String pk_corp, String qj) {
		String sql = "select max(qj) from  ynt_salaryreport t where t.qj <? and t.pk_corp =? and nvl(t.dr,0)=0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(qj);
		sp.addParam(pk_corp);
		Object o = singleObjectBO.executeQuery(sql, sp, new ColumnProcessor());
		Map<String, SalaryReportVO> map = new HashMap<>();
		if (o != null) {
			SalaryReportVO[] vos = queryAllType(pk_corp, (String) o);
			map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(vos), new String[] { "cpersonid" });
		}
		return map;

	}

	// 本年上期累计数据
	private Map<String, SalaryReportVO> getLastPeriodSalaryReport(String pk_corp, String qj) {
		Map<String, SalaryReportVO> map = new HashMap<>();

		String period = DateUtils.getPreviousPeriod(qj);
		SalaryReportVO[] vos = queryAllType(pk_corp, period);
		map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(vos), new String[] { "cpersonid" });
		return map;
	}

	// 截止到上期的累计数据
	private Map<String, SalaryReportVO> getLastTotailSalaryReport(String pk_corp, String billtype, String qj) {
		Map<String, SalaryReportVO> map = new HashMap<>();
		if (!qj.endsWith("-01")) {
			String fromperiod = qj.substring(0, 4) + "-01";
			String toperiod = DateUtils.getPreviousPeriod(qj);
			String wheresql = " pk_corp = ? and qj >= ? and qj <= ? and nvl(dr,0) = 0 and billtype = '"
					+ SalaryTypeEnum.NORMALSALARY.getValue() + "'";
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(fromperiod);
			sp.addParam(toperiod);
			SalaryReportVO[] srvos = (SalaryReportVO[]) singleObjectBO.queryByCondition(SalaryReportVO.class, wheresql,
					sp);
			SalaryCipherHandler.handlerSalaryVO(srvos, 1);// 解密处理
			Collections.sort(Arrays.asList(srvos), new Comparator<SalaryReportVO>() {
				@Override
				public int compare(SalaryReportVO o1, SalaryReportVO o2) {
					int i = o1.getQj().compareTo(o2.getQj());
					return i;
				}
			});

			double kcmun = getnewkcnum(billtype, qj);
			DZFDouble dkcmun = new DZFDouble(kcmun);
			for (SalaryReportVO vo : srvos) {
				if (map.containsKey(vo.getCpersonid())) {
					SalaryReportVO lastvo = map.get(vo.getCpersonid());
					vo.setLjsre(SafeCompute.add(vo.getYfgz(), lastvo.getLjsre()));// 累计收入额
					vo.setLjjcfy(SafeCompute.add(dkcmun, lastvo.getLjjcfy()));// 累计减除费用
					vo.setLjjxjyzc(SafeCompute.add(lastvo.getLjjxjyzc(), vo.getJxjyzc()));// 累计继续教育支出
					vo.setLjsylrzc(SafeCompute.add(lastvo.getLjsylrzc(), vo.getSylrzc()));// 累计赡养老人支出
					vo.setLjzfdkzc(SafeCompute.add(lastvo.getLjzfdkzc(), vo.getZfdkzc()));// 累计住房贷款利息支出
					vo.setLjzfzjzc(SafeCompute.add(lastvo.getLjzfzjzc(), vo.getZfzjzc()));// 累计住房租金支出
					vo.setLjznjyzc(SafeCompute.add(lastvo.getLjznjyzc(), vo.getZnjyzc()));// 累计子女教育支出
					vo.setLjzxkc(SafeCompute.add(lastvo.getLjzxkc(), getPeriodZxkc(vo)));// 累计专项扣除
					vo.setYyjse(SafeCompute.add(lastvo.getYyjse(), vo.getGrsds()));// 累计专项扣除
				} else {
					vo.setLjsre(vo.getYfgz());
					vo.setLjjcfy(dkcmun);
					vo.setLjjxjyzc(vo.getJxjyzc());// 累计继续教育支出
					vo.setLjsylrzc(vo.getSylrzc());// 累计赡养老人支出
					vo.setLjzfdkzc(vo.getZfdkzc());// 累计住房贷款利息支出
					vo.setLjzfzjzc(vo.getZfzjzc());// 累计住房租金支出
					vo.setLjznjyzc(vo.getZnjyzc());// 累计子女教育支出
					vo.setLjzxkc(getPeriodZxkc(vo));// 累计专项扣除
					vo.setYyjse(vo.getGrsds());
				}
				map.put(vo.getCpersonid(), vo);
			}
		}
		return map;
	}

	private DZFDouble getPeriodZxkc(SalaryReportVO vo) {
		DZFDouble ntotalzxkc = DZFDouble.ZERO_DBL;
		String[] columns = new String[] { "zfgjj", "yanglaobx", "yiliaobx", "shiyebx" };
		ntotalzxkc = addByColumn(columns, vo);
		return ntotalzxkc;
	}

	private DZFDouble getPeriodLjZxdc(SalaryReportVO vo) {
		DZFDouble ntotalzxdc = DZFDouble.ZERO_DBL;
		String[] columns = new String[] { "ljznjyzc", "ljjxjyzc", "ljzfdkzc", "ljzfzjzc", "ljsylrzc" };
		ntotalzxdc = addByColumn(columns, vo);
		return ntotalzxdc;
	}

	private DZFDouble getPeriodZxdc(SalaryReportVO vo) {
		DZFDouble ntotalzxdc = DZFDouble.ZERO_DBL;
		String[] columns = new String[] { "znjyzc", "jxjyzc", "zfdkzc", "zfzjzc", "sylrzc" };
		ntotalzxdc = addByColumn(columns, vo);
		return ntotalzxdc;
	}

	private DZFDouble addByColumn(String[] columns, SuperVO vo) {
		DZFDouble temp = DZFDouble.ZERO_DBL;
		if (columns == null || columns.length == 0)
			return temp;

		List<DZFDouble> list = new ArrayList<>();
		for (String column : columns) {
			list.add((DZFDouble) vo.getAttributeValue(column));
		}
		temp = addByDZFDouble(list.toArray(new DZFDouble[list.size()]));
		return temp;
	}

	private DZFDouble subByDZFDouble(DZFDouble[] dous) {
		DZFDouble temp = DZFDouble.ZERO_DBL;
		if (dous == null || dous.length == 0)
			return temp;

		int len = dous.length;
		for (int i = 0; i < len; i++) {
			if (i == 0) {
				temp = dous[i];
			} else {
				temp = SafeCompute.sub(temp, dous[i]);
			}
		}
		return temp;
	}

	private DZFDouble addByDZFDouble(DZFDouble[] dous) {
		DZFDouble temp = DZFDouble.ZERO_DBL;
		if (dous == null || dous.length == 0)
			return temp;

		for (DZFDouble dou : dous) {
			temp = SafeCompute.add(temp, dou);
		}
		return temp;
	}

	@Override
	public TzpzHVO saveToVoucher(CorpVO corpvp, String gzjttotal, String bxtotal, String gjjtotal, String grsdstotal,
								 String yfgztotal, String qj, String cuserid, String str) throws DZFWarpException {
		String pk_corp = corpvp.getPk_corp();
		TzpzHVO headVO = new TzpzHVO();
		String sourcebilltype = pk_corp + qj + str;
		//
		String wheresql = " pk_corp = ? and sourcebilltype = ? and nvl(dr,0) = 0  ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(sourcebilltype);
		TzpzHVO[] hvos = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, wheresql, sp);
		// String msg = "";
		if (queryIsGZ(pk_corp, qj).booleanValue()) {
			throw new BusinessException("当月已关账，不能生成凭证！");
		}
		if (hvos != null && hvos.length > 0) {
			if (hvos[0].getIshasjz().booleanValue()) {// 已记账或已审核
				throw new BusinessException("凭证已记账，不能重新生成！");
			}
			if (hvos[0].getVbillstatus() == 1) {
				throw new BusinessException("凭证已审核，不能重新生成！");
			}
		}
		SalaryAccSetVO setvo = getSASVO(pk_corp, SalaryTypeEnum.NORMALSALARY.getValue());
		if (setvo == null) {
			throw new BusinessException("工资科目设置为空！");
		}

		List<TzpzBVO> list = null;
		if ("gzjt".equals(str)) {// 工资计提
			list = saveToVoucherGzjt1(qj, pk_corp, setvo);
			DZFDouble gzjt = DZFDouble.ZERO_DBL;
			for (TzpzBVO bvo : list) {
				gzjt = SafeCompute.add(gzjt, bvo.getDfmny());
			}
		} else {// 工资发放
			// list = saveToVoucherGzff(pk_corp, qj, setvo, bxtotal, gjjtotal,
			// grsdstotal, yfgztotal);
			list = saveToVoucherGzff1(pk_corp, qj, setvo);
		}

		DZFDouble dyfgztotal = DZFDouble.ZERO_DBL;
		for (TzpzBVO bvo : list) {
			dyfgztotal = SafeCompute.add(dyfgztotal, bvo.getDfmny());
		}
		headVO.setJfmny(dyfgztotal);
		headVO.setDfmny(dyfgztotal);
		if (StringUtil.isEmpty(headVO.getPk_tzpz_h())) {
			headVO.setPzlb(0);// 凭证类别：记账
			headVO.setPk_corp(pk_corp);
			headVO.setCoperatorid(cuserid);
			headVO.setIshasjz(DZFBoolean.FALSE);
			DZFDate nowDatevalue = getPeroidDZFDate(qj);
			headVO.setDoperatedate(nowDatevalue);
			if (hvos != null && hvos.length > 0) {
				headVO.setPzh(hvos[0].getPzh());
			}
			headVO.setVbillstatus(8);// 默认自由态
			// 记录单据来源
			headVO.setSourcebillid(null);
			headVO.setSourcebilltype(sourcebilltype);
			headVO.setPeriod(qj);
			headVO.setVyear(Integer.valueOf(qj.substring(0, 4)));
		}
		if (headVO.getIsfpxjxm() == null) {
			if (hvos != null && hvos.length > 0) {
				headVO.setIsfpxjxm(hvos[0].getIsfpxjxm());
			} else {
				headVO.setIsfpxjxm(DZFBoolean.FALSE);
			}
		}
		headVO.setChildren(list.toArray(new TzpzBVO[list.size()]));
		return headVO;
	}

	private Map<String, AuxiliaryAccountBVO> getAuxiliaryMap(String pk_corp) {
		Map<String, AuxiliaryAccountBVO> aumap = AccountUtil.getAuxiliaryAccountBVOByPk(pk_corp,
				AuxiliaryConstant.ITEM_DEPARTMENT);

		Map<String, AuxiliaryAccountBVO> aumap1 = AccountUtil.getAuxiliaryAccountBVOByPk(pk_corp,
				AuxiliaryConstant.ITEM_STAFF);
		aumap.putAll(aumap1);
		return aumap;

	}

	private List<TzpzBVO> saveToVoucherGzjt1(String qj, String pk_corp, SalaryAccSetVO setvo) {

		String jtgz_yfgzkm = setvo.getJtgz_yfgzkm();// 应付工资科目id
		if (DZFValueCheck.isEmpty(jtgz_yfgzkm)) {
			throw new BusinessException("计提应付工资科目设置为空，请检查！");
		}
		String zy = qj.substring(0, 4) + "年" + qj.substring(5, 7) + "月工资计提";
		Map<String, AuxiliaryAccountBVO> aumap = getAuxiliaryMap(pk_corp);
		List<TzpzBVO> list = new ArrayList<TzpzBVO>();
		SalaryReportVO[] srvos = queryAllType(pk_corp, qj);
		if (srvos == null || srvos.length == 0) {
			return list;
		}
		if (DZFValueCheck.isEmpty(setvo.getJtqybf()) || !setvo.getJtqybf().booleanValue()) {
			Map<String, YntCpaccountVO> ccountMap = accService.queryMapByPk(srvos[0].getPk_corp());
			List<TzpzBVO> fylist = createCommonTzpzBVO(srvos, null, zy, "yfgz", 0, ccountMap, aumap, setvo);
			list.addAll(fylist);//
			List<TzpzBVO> yflist = createCommonTzpzBVO(srvos, jtgz_yfgzkm, zy, "yfgz", 1, ccountMap, aumap, setvo);
			list.addAll(yflist);//
		} else {
			// 2019年之前不计提企业部分
			if (qj.compareTo("2019-01") < 0) {
				Map<String, YntCpaccountVO> ccountMap =accService.queryMapByPk( srvos[0].getPk_corp());
				List<TzpzBVO> fylist = createCommonTzpzBVO(srvos, null, zy, "yfgz", 0, ccountMap, aumap, setvo);
				list.addAll(fylist);//
				List<TzpzBVO> yflist = createCommonTzpzBVO(srvos, jtgz_yfgzkm, zy, "yfgz", 1, ccountMap, aumap, setvo);
				list.addAll(yflist);//
			} else {
				list = saveToVoucherGzjtCorp(srvos, qj, pk_corp, setvo, zy);
			}

		}

		return list;
	}

	private List<TzpzBVO> saveToVoucherGzjtCorp(SalaryReportVO[] srvos, String qj, String pk_corp, SalaryAccSetVO setvo,
			String zy) {
		Map<String, AuxiliaryAccountBVO> aumap = getAuxiliaryMap(pk_corp);
		String jtgz_yfgzkm = setvo.getJtgz_yfgzkm();// 应付工资科目id
		if (DZFValueCheck.isEmpty(jtgz_yfgzkm)) {
			throw new BusinessException("计提应付工资科目设置为空，请检查！");
		}
		String jtgz_yfsbkm = setvo.getJtgz_yfsbkm();// 应付工资科目id
		if (DZFValueCheck.isEmpty(jtgz_yfsbkm)) {
			throw new BusinessException("计提应付社保科目设置为空，请检查！");
		}
		String jtgz_sbfykm = setvo.getJtgz_sbfykm();//
		if (DZFValueCheck.isEmpty(jtgz_sbfykm)) {
			throw new BusinessException("计提社保费用科目设置为空，请检查！");
		}

		List<TzpzBVO> list = new ArrayList<TzpzBVO>();
		Map<String, YntCpaccountVO> ccountMap =accService.queryMapByPk(srvos[0].getPk_corp());

		List<TzpzBVO> fylist = createCommonTzpzBVO(srvos, null, zy, "yfgz", 0, ccountMap, aumap, setvo);
		list.addAll(fylist);//

		List<TzpzBVO> yflist = createCommonTzpzBVO(srvos, jtgz_yfgzkm, zy, "yfgz", 1, ccountMap, aumap, setvo);
		list.addAll(yflist);//

		zy = qj.substring(0, 4) + "年" + qj.substring(5, 7) + "月社保计提(企业部分)";
		String hjcolumn = "qyyanglaobx&qyyiliaobx&qyshiyebx&qyzfgjj&qygsbx&qyshybx";
		List<TzpzBVO> sbfylist = createCommonTzpzBVO(srvos, jtgz_sbfykm, zy, hjcolumn, 0, ccountMap, aumap, setvo);
		list.addAll(sbfylist);//

		List<TzpzBVO> qyhjlist = createCommonTzpzBVO(srvos, setvo.getJtgz_yfsbkm(), zy, hjcolumn, 1, ccountMap, aumap,
				setvo);
		list.addAll(qyhjlist);//

		// 计提明细部分
		SalarySetTableVO[] tables = filterData(pk_corp, setvo);
		if (tables != null && tables.length > 3) {
			saveToVoucherGzjtCorpMx(srvos, qj, pk_corp, setvo, zy, aumap, ccountMap, hjcolumn, list);
		}
		Map<String, TzpzBVO> map1 = new LinkedHashMap<>();
		List<TzpzBVO> finBodyList = new ArrayList<TzpzBVO>();
		for (TzpzBVO vo : list) {
			String key = constructTzpzKey(vo);
			if (!map1.containsKey(key)) {
				map1.put(key, vo);
			} else {
				TzpzBVO temp = map1.get(key);
				if (temp != null) {
					temp.setDfmny(SafeCompute.add(vo.getDfmny(), temp.getDfmny()));
					temp.setJfmny(SafeCompute.add(vo.getJfmny(), temp.getJfmny()));
					temp.setYbdfmny(SafeCompute.add(vo.getYbdfmny(), temp.getYbdfmny()));
					temp.setYbjfmny(SafeCompute.add(vo.getYbjfmny(), temp.getYbjfmny()));
					map1.put(key, temp);
				} else {
					map1.put(key, vo);
				}
			}
		}
		if (map1 == null || map1.size() == 0) {
			throw new BusinessException("合并科目数据失败!");
		} else {
			finBodyList.addAll(map1.values());
		}
		return finBodyList;
	}

	private SalarySetTableVO[] filterData(String pk_corp, SalaryAccSetVO setvo) {
		SalarySetTableVO[] tableData3 = SalaryTableGetter.getGzJt(pk_corp, setvo, 3);
		List<SalarySetTableVO> list = new ArrayList<>();
		if (tableData3 == null || tableData3.length == 0)
			return new SalarySetTableVO[0];

		int i = 1;
		for (SalarySetTableVO table : tableData3) {
			table.setXh(Integer.toString(i));
			if (!StringUtil.isEmpty(table.getKjkm())) {
				list.add(table);
			}
		}
		return list.toArray(new SalarySetTableVO[list.size()]);
	}

	private void saveToVoucherGzjtCorpMx(SalaryReportVO[] srvos, String qj, String pk_corp, SalaryAccSetVO setvo,
			String zy, Map<String, AuxiliaryAccountBVO> aumap, Map<String, YntCpaccountVO> ccountMap, String hjcolumn,
			List<TzpzBVO> list) {

		List<TzpzBVO> qyyfhjlist1 = createCommonTzpzBVO(srvos, setvo.getJtgz_yfsbkm(), zy, hjcolumn, 0, ccountMap,
				aumap, setvo);
		list.addAll(qyyfhjlist1);//

		List<TzpzBVO> qyyfyllist = createCommonTzpzBVO(srvos, setvo.getJitgz_qyyfsbgrbf(), zy, "qyyanglaobx", 1,
				ccountMap, aumap, setvo);
		if (qyyfyllist != null && qyyfyllist.size() > 0) {
			if (StringUtil.isEmpty(setvo.getJitgz_qyyfsbgrbf())) {
				throw new BusinessException("计提应付养老保险科目设置为空，请检查！");
			}
			list.addAll(qyyfyllist);//
		}

		List<TzpzBVO> qyyfylilist = createCommonTzpzBVO(srvos, setvo.getJitgz_qyyfyilbxbf(), zy, "qyyiliaobx", 1,
				ccountMap, aumap, setvo);
		if (qyyfylilist != null && qyyfylilist.size() > 0) {
			if (StringUtil.isEmpty(setvo.getJitgz_qyyfyilbxbf())) {
				throw new BusinessException("计提应付医疗保险科目设置为空，请检查！");
			}
			list.addAll(qyyfylilist);//
		}

		List<TzpzBVO> qyyfshiyelist = createCommonTzpzBVO(srvos, setvo.getJitgz_qyyfsybxbf(), zy, "qyshiyebx", 1,
				ccountMap, aumap, setvo);
		if (qyyfshiyelist != null && qyyfshiyelist.size() > 0) {
			if (StringUtil.isEmpty(setvo.getJitgz_qyyfsybxbf())) {
				throw new BusinessException("计提应付失业保险科目设置为空，请检查！");
			}
			list.addAll(qyyfshiyelist);//
		}

		List<TzpzBVO> qyyfgjjlist = createCommonTzpzBVO(srvos, setvo.getJitgz_qyyfgjjgrbf(), zy, "qyzfgjj", 1,
				ccountMap, aumap, setvo);
		if (qyyfgjjlist != null && qyyfgjjlist.size() > 0) {
			if (StringUtil.isEmpty(setvo.getJitgz_qyyfgjjgrbf())) {
				throw new BusinessException("计提应付公积金科目设置为空，请检查！");
			}
			list.addAll(qyyfgjjlist);//
		}

		List<TzpzBVO> qyyfgslist = createCommonTzpzBVO(srvos, setvo.getJitgz_qyyfgsbxkm(), zy, "qygsbx", 1, ccountMap,
				aumap, setvo);
		if (qyyfgslist != null && qyyfgslist.size() > 0) {
			if (StringUtil.isEmpty(setvo.getJitgz_qyyfgsbxkm())) {
				throw new BusinessException("计提应付工伤保险科目设置为空，请检查！");
			}
			list.addAll(qyyfgslist);//
		}

		List<TzpzBVO> qyyfshylist = createCommonTzpzBVO(srvos, setvo.getJitgz_qyyfshybxkm(), zy, "qyshybx", 1,
				ccountMap, aumap, setvo);
		if (qyyfshylist != null && qyyfshylist.size() > 0) {
			if (StringUtil.isEmpty(setvo.getJitgz_qyyfshybxkm())) {
				throw new BusinessException("计提应付生育保险科目设置为空，请检查！");
			}
			list.addAll(qyyfshylist);//
		}

		List<TzpzBVO> qyfyhjlist1 = createCommonTzpzBVO(srvos, setvo.getJtgz_sbfykm(), zy, hjcolumn, 1, ccountMap,
				aumap, setvo);
		list.addAll(qyfyhjlist1);//

		List<TzpzBVO> qyfyyllist = createCommonTzpzBVO(srvos, setvo.getJitgz_qyfysbgrbf(), zy, "qyyanglaobx", 0,
				ccountMap, aumap, setvo);
		if (qyfyyllist != null && qyfyyllist.size() > 0) {
			if (StringUtil.isEmpty(setvo.getJitgz_qyfysbgrbf())) {
				throw new BusinessException("计提社保养老保险科目设置为空，请检查！");
			}
			list.addAll(qyfyyllist);//
		}

		List<TzpzBVO> qyfyylilist = createCommonTzpzBVO(srvos, setvo.getJitgz_qyfyyilbxbf(), zy, "qyyiliaobx", 0,
				ccountMap, aumap, setvo);
		if (qyfyylilist != null && qyfyylilist.size() > 0) {
			if (StringUtil.isEmpty(setvo.getJitgz_qyfyyilbxbf())) {
				throw new BusinessException("计提社保医疗保险科目设置为空，请检查！");
			}
			list.addAll(qyfyylilist);//
		}

		List<TzpzBVO> qyfyshiyelist = createCommonTzpzBVO(srvos, setvo.getJitgz_qyfysybxbf(), zy, "qyshiyebx", 0,
				ccountMap, aumap, setvo);
		if (qyfyshiyelist != null && qyfyshiyelist.size() > 0) {
			if (StringUtil.isEmpty(setvo.getJitgz_qyfysybxbf())) {
				throw new BusinessException("计提社保失业保险科目设置为空，请检查！");
			}
			list.addAll(qyfyshiyelist);//
		}

		List<TzpzBVO> qyfygjjlist = createCommonTzpzBVO(srvos, setvo.getJitgz_qyfygjjgrbf(), zy, "qyzfgjj", 0,
				ccountMap, aumap, setvo);
		if (qyfygjjlist != null && qyfygjjlist.size() > 0) {
			if (StringUtil.isEmpty(setvo.getJitgz_qyfygjjgrbf())) {
				throw new BusinessException("计提社保公积金科目设置为空，请检查！");
			}
			list.addAll(qyfygjjlist);//
		}

		List<TzpzBVO> qyfygslist = createCommonTzpzBVO(srvos, setvo.getJitgz_qyfygsbxkm(), zy, "qygsbx", 0, ccountMap,
				aumap, setvo);
		if (qyfygslist != null && qyfygslist.size() > 0) {
			if (StringUtil.isEmpty(setvo.getJitgz_qyfygsbxkm())) {
				throw new BusinessException("计提社保工伤保险科目设置为空，请检查！");
			}
			list.addAll(qyfygslist);//
		}

		List<TzpzBVO> qyfyshylist = createCommonTzpzBVO(srvos, setvo.getJitgz_qyfyshybxkm(), zy, "qyshybx", 0,
				ccountMap, aumap, setvo);
		if (qyfyshylist != null && qyfyshylist.size() > 0) {
			if (StringUtil.isEmpty(setvo.getJitgz_qyfyshybxkm())) {
				throw new BusinessException("计提社保生育保险科目设置为空，请检查！");
			}
			list.addAll(qyfyshylist);//
		}

	}

	private List<TzpzBVO> saveToVoucherGzff1(String pk_corp, String qj, SalaryAccSetVO setvo) {

		if (StringUtil.isEmpty(setvo.getFfgz_yfgzkm())) {
			throw new BusinessException("发放工资应付工资科目设置为空，请检查！");
		}
		if (StringUtil.isEmpty(setvo.getFfgz_sbgrbf())) {
			throw new BusinessException("发放养老保险科目设置为空，请检查！");
		}
		if (StringUtil.isEmpty(setvo.getFfgz_yilbxbf())) {
			throw new BusinessException("发放医疗保险科目设置为空，请检查！");
		}
		if (StringUtil.isEmpty(setvo.getFfgz_sybxbf())) {
			throw new BusinessException("发放失业保险科目设置为空，请检查！");
		}
		if (StringUtil.isEmpty(setvo.getFfgz_gjjgrbf())) {
			throw new BusinessException("发放公积金科目设置为空，请检查！");
		}
		if (StringUtil.isEmpty(setvo.getFfgz_grsds())) {
			throw new BusinessException("发放应缴个税科目设置为空，请检查！");
		}
		if (StringUtil.isEmpty(setvo.getFfgz_xjlkm())) {
			throw new BusinessException("发放工资发放科目设置为空，请检查！");
		}

		SalaryReportVO[] srvos = queryAllType(pk_corp, qj);
		List<TzpzBVO> list = new ArrayList<TzpzBVO>();
		if (srvos == null || srvos.length == 0) {
			return list;
		}

		Map<String, AuxiliaryAccountBVO> aumap = getAuxiliaryMap(pk_corp);

		Map<String, YntCpaccountVO> ccountMap = accService.queryMapByPk(srvos[0].getPk_corp());
		String zy = qj.substring(0, 4) + "年" + qj.substring(5, 7) + "月工资发放";
		String ffgz_yfgzkm = setvo.getFfgz_yfgzkm();// 应付工资科目科目id
		List<TzpzBVO> yflist = createCommonTzpzBVO(srvos, ffgz_yfgzkm, zy, "yfgz", 0, ccountMap, aumap, setvo);
		list.addAll(yflist);//
		String ffgz_sbgrbf = setvo.getFfgz_sbgrbf();// 养老保险
		List<TzpzBVO> sblist = createCommonTzpzBVO(srvos, ffgz_sbgrbf, zy, "yanglaobx", 1, ccountMap, aumap, setvo);
		list.addAll(sblist);//
		String ffgz_yilbxbf = setvo.getFfgz_yilbxbf();// 医疗保险
		List<TzpzBVO> yllist = createCommonTzpzBVO(srvos, ffgz_yilbxbf, zy, "yiliaobx", 1, ccountMap, aumap, setvo);
		list.addAll(yllist);//
		String ffgz_sybxbf = setvo.getFfgz_sybxbf();//
		List<TzpzBVO> sylist = createCommonTzpzBVO(srvos, ffgz_sybxbf, zy, "shiyebx", 1, ccountMap, aumap, setvo);
		list.addAll(sylist);//

		String ffgz_gjjgrbf = setvo.getFfgz_gjjgrbf();// 公积金科目id
		List<TzpzBVO> gjjlist = createCommonTzpzBVO(srvos, ffgz_gjjgrbf, zy, "zfgjj", 1, ccountMap, aumap, setvo);
		list.addAll(gjjlist);//
		String ffgz_grsds = setvo.getFfgz_grsds();// 个人所得税科目id
		List<TzpzBVO> grsdslist = createCommonTzpzBVO(srvos, ffgz_grsds, zy, "grsds", 1, ccountMap, aumap, setvo);
		list.addAll(grsdslist);//
		String ffgz_xjlkm = setvo.getFfgz_xjlkm();// 现金类科目id
		List<TzpzBVO> dsfgzlist = createCommonTzpzBVO(srvos, ffgz_xjlkm, zy, "sfgz", 1, ccountMap, aumap, setvo);
		list.addAll(dsfgzlist);//
		Map<String, TzpzBVO> map1 = new LinkedHashMap<>();
		List<TzpzBVO> finBodyList = new ArrayList<TzpzBVO>();
		for (TzpzBVO vo : list) {
			String key = constructTzpzKey(vo);
			if (!map1.containsKey(key)) {
				map1.put(key, vo);
			} else {
				TzpzBVO temp = map1.get(key);
				if (temp != null) {
					temp.setDfmny(SafeCompute.add(vo.getDfmny(), temp.getDfmny()));
					temp.setJfmny(SafeCompute.add(vo.getJfmny(), temp.getJfmny()));
					temp.setYbdfmny(SafeCompute.add(vo.getYbdfmny(), temp.getYbdfmny()));
					temp.setYbjfmny(SafeCompute.add(vo.getYbjfmny(), temp.getYbjfmny()));
					map1.put(key, temp);
				} else {
					map1.put(key, vo);
				}
			}
		}
		if (map1 == null || map1.size() == 0) {
			throw new BusinessException("合并科目数据失败!");
		} else {
			finBodyList.addAll(map1.values());
		}

		return finBodyList;
	}

	private String constructTzpzKey(TzpzBVO bvo) {
		StringBuffer sf = new StringBuffer();
		sf.append("&").append(bvo.getPk_accsubj()).append("&").append(bvo.getPk_inventory()).append("&")
				.append(bvo.getPk_taxitem()).append("&").append(bvo.getVdirect()).append("&");

		for (int i = 1; i <= 10; i++) {
			sf.append(bvo.getAttributeValue("fzhsx" + i)).append("&");
		}

		return sf.toString();

	}

	private List<TzpzBVO> createCommonTzpzBVO(SalaryReportVO[] ibodyvos, String pk_accsubj, String zy, String column1,
			int vdirect, Map<String, YntCpaccountVO> ccountMap, Map<String, AuxiliaryAccountBVO> aumap,
			SalaryAccSetVO setvo) {
		if (ibodyvos == null || ibodyvos.length == 0)
			return null;

		String column2 = null;
		List<TzpzBVO> list = new ArrayList<>();
		// 转换成凭证vo

		// pk_accsubj 为空 计提费用科目 根据部门科目关系进行计提
		Map<String, SalaryKmDeptVO> kmmap = new HashMap<>();
		if (StringUtil.isEmpty(pk_accsubj)) {
			SalaryKmDeptVO[] vos = gl_gzkmszserv.queryFykm(ibodyvos[0].getPk_corp());
			kmmap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(vos), new String[] { "cdeptid" });
		}

		SalaryKmDeptVO kmvo = null;
		for (SalaryReportVO ibody : ibodyvos) {

			YntCpaccountVO cvo = ccountMap.get(pk_accsubj);
			if (!StringUtil.isEmpty(pk_accsubj)) {
				// 其他科目处理
				cvo = ccountMap.get(pk_accsubj);
			} else {
				// 费用科目计提
				// if (!StringUtil.isEmpty(ibody.getFykmid())) {
				// // 取工资表的中的费用科目
				// cvo = ccountMap.get(ibody.getFykmid());
				// } else {
				// 2019年之后的走部门费用 按照人员的部门信息计提
				kmvo = kmmap.get(ibody.getCdeptid());
				if (kmvo != null) {
					cvo = ccountMap.get(kmvo.getCkjkmid());
				}
				// }
				// 取入账设置中的费用科目
				if (cvo == null) {
					cvo = ccountMap.get(setvo.getJtgz_gzfykm());
				}

				if (cvo == null) {
					throw new BusinessException("费用科目不能为空");
				}
			}

			DZFDouble nmny = DZFDouble.ZERO_DBL;

			if (column1.indexOf("&") > 0) {
				for (String column : column1.split("&")) {
					nmny = SafeCompute.add((DZFDouble) ibody.getAttributeValue(column), nmny);
				}
			} else {
				column2 = column1;
				nmny = SafeCompute.add((DZFDouble) ibody.getAttributeValue(column2), nmny);
			}
			// 金额为零的 不记录凭证行
			if (nmny.compareTo(DZFDouble.ZERO_DBL) != 0) {
				TzpzBVO bvo = createSingleTzpzBVO(cvo, zy, vdirect, nmny, ibody, aumap);
				list.add(bvo);
			}
			cvo = null;
		}
		// 汇总vo
		Map<String, TzpzBVO> map = new LinkedHashMap<>();
		for (TzpzBVO bvo : list) {
			String inv = bvo.getPk_accsubj() + bvo.getFzhsx5() + bvo.getFzhsx3();
			if (StringUtil.isEmpty(inv)) {
				inv = "aaaaa";
			}
			TzpzBVO temp = null;
			if (!map.containsKey(inv)) {
				temp = bvo;
			} else {
				temp = map.get(inv);
				temp.setDfmny(SafeCompute.add(temp.getDfmny(), bvo.getDfmny()));
				temp.setYbdfmny(SafeCompute.add(temp.getYbdfmny(), bvo.getYbdfmny()));
				temp.setJfmny(SafeCompute.add(temp.getJfmny(), bvo.getJfmny()));
				temp.setYbjfmny(SafeCompute.add(temp.getYbjfmny(), bvo.getYbjfmny()));
			}
			map.put(inv, temp);
		}

		List<TzpzBVO> bodyList = new ArrayList<TzpzBVO>();

		for (TzpzBVO value : map.values()) {
			if (vdirect == 1) {
				if (value.getDfmny() == null || value.getDfmny().doubleValue() == 0) {
					continue;
				}
			} else {
				if (value.getJfmny() == null || value.getJfmny().doubleValue() == 0) {
					continue;
				}
			}
			bodyList.add(value);
		}
		return bodyList;
	}

	private TzpzBVO createSingleTzpzBVO(YntCpaccountVO cvo, String zy, int vdirect, DZFDouble totalDebit,
			SalaryReportVO ibody, Map<String, AuxiliaryAccountBVO> aumap) {
		TzpzBVO depvo = new TzpzBVO();
		depvo.setPk_accsubj(cvo.getPk_corp_account());
		depvo.setVcode(cvo.getAccountcode());
		depvo.setVname(cvo.getAccountname());
		depvo.setKmmchie(cvo.getFullname());
		depvo.setZy(zy);// 摘要
		String cdeptid = ibody.getCdeptid();
		String cpersonid = ibody.getCpersonid();
		List<AuxiliaryAccountBVO> fzhs_list = new ArrayList<>();
		if (cvo.getIsfzhs().charAt(4) == '1') {
			if (!StringUtil.isEmpty(cdeptid)) {
				depvo.setFzhsx5(cdeptid);
				fzhs_list.add(aumap.get(cdeptid));
			}
		}
		if (cvo.getIsfzhs().charAt(2) == '1') {
			if (!StringUtil.isEmpty(cpersonid)) {
				depvo.setFzhsx3(cpersonid);
				fzhs_list.add(aumap.get(cpersonid));
			}
		}
		depvo.setFzhs_list(fzhs_list);
		depvo.setVdirect(vdirect);
		totalDebit = totalDebit.setScale(2, DZFDouble.ROUND_HALF_UP);
		if (vdirect == 1) {
			depvo.setDfmny(totalDebit);
			depvo.setYbdfmny(totalDebit);
		} else {
			depvo.setJfmny(totalDebit);
			depvo.setYbjfmny(totalDebit);
		}
		depvo.setPk_currency(DzfUtil.PK_CNY);
		depvo.setNrate(DZFDouble.ONE_DBL);
		depvo.setPk_corp(ibody.getPk_corp());
		return depvo;

	}

	private DZFDate getPeroidDZFDate(String qj) {
		String qj1 = qj.substring(0, 7);
		int year = Integer.parseInt(qj1.substring(0, 4));
		int month = Integer.parseInt(qj1.substring(5, 7));
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE);
		return new DZFDate(qj1 + "-" + maxDate);
	}

	public SalaryAccSetVO getSASVO(String pk_corp, String billtype) throws BusinessException {

		SalaryAccSetVO salaryTemp = null;

		SalaryAccSetVO vo = gl_gzkmszserv.query(pk_corp);
		if (vo != null) {
			salaryTemp = vo;
		} else {
			salaryTemp = gl_gzkmszserv.queryGroupVO(pk_corp);
		}
		if (salaryTemp == null)
			throw new BusinessException("工资科目设置为空！");

		// 外籍工资 正常薪金
		if (!StringUtil.isEmpty(billtype) && (billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue()))) {

		} else {
			salaryTemp.setGjj_bl(DZFDouble.ZERO_DBL);
			salaryTemp.setGjj_js(DZFDouble.ZERO_DBL);
			salaryTemp.setYfbx_bl(DZFDouble.ZERO_DBL);
			salaryTemp.setYfbx_js(DZFDouble.ZERO_DBL);
			salaryTemp.setYlbx_bl(DZFDouble.ZERO_DBL);
			salaryTemp.setYlbx_js(DZFDouble.ZERO_DBL);
			salaryTemp.setSybx_bl(DZFDouble.ZERO_DBL);
			salaryTemp.setSybx_js(DZFDouble.ZERO_DBL);
		}
		return salaryTemp;

	}

	public SalaryReportVO[] getSalarySetInfo(String pk_corp, String billtype, String cpersonids, String qj)
			throws BusinessException {
		Map<String, YntCpaccountVO> map = accService.queryMapByPk(pk_corp);

		SalaryAccSetVO salaryTemp = getSASVO(pk_corp, billtype);

		GxhszVO gxh = gl_gxhszserv.query(pk_corp);
		Integer kmShow = gxh.getSubjectShow();
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		List<SalaryReportVO> slist = new ArrayList<>();
		String pks[] = DZFStringUtil.getString2Array(cpersonids, ",");
		if (DZFValueCheck.isNotEmpty(pks)) {
			List<String> list = Arrays.asList(pks);
			if (list == null || list.size() == 0)
				return new SalaryReportVO[0];

			Map<String, SalaryBaseVO> basemap = gl_gzbbaseserv.getSalaryBaseVO(pk_corp, qj);
			for (String pk : pks) {
				SalaryReportVO reportvo = new SalaryReportVO();
				if (!StringUtil.isEmpty(salaryTemp.getJtgz_gzfykm())) {
					YntCpaccountVO accvo = (YntCpaccountVO) map.get(salaryTemp.getJtgz_gzfykm());
					if (accvo != null) {
						if (kmShow == 0) {
							reportvo.setFykmname(accvo.getAccountname());
						} else if (kmShow == 1) {
							String[] fullname = accvo.getFullname().split("/");
							if (fullname.length > 1) {
								reportvo.setFykmname(fullname[0] + "/" + fullname[fullname.length - 1]);
							} else {
								reportvo.setFykmname(accvo.getFullname());
							}
						} else {
							reportvo.setFykmname(accvo.getFullname());
						}
						reportvo.setFykmkind(accvo.getAccountkind());
						reportvo.setFykmcode(accvo.getAccountcode());
					}
				}
				gl_gzbcalserv.calSbGjj(reportvo, salaryTemp, basemap.get(pk), billtype, false, false);
				if (corpvo != null) {
					reportvo.setVphone(corpvo.getPhone1());
				}
				slist.add(reportvo);
			}
		}
		SalaryReportVO[] vos = slist.toArray(new SalaryReportVO[slist.size()]);
		setTotalData(vos);
		return vos;

	}

	@Override
	public SalaryReportVO[] save(String pk_corp, List<SalaryReportVO> list, String qj, String cuserid, String billtype)
			throws DZFWarpException {
		if (list == null || list.size() == 0) {
			throw new BusinessException("数据为空！");
		}
		checkPz(pk_corp, qj, "gzjt", false);
		checkPz(pk_corp, qj, "gzff", false);
		Map<String, YntCpaccountVO> map = accService.queryMapByPk(pk_corp);
		checkRefDoc(list.toArray(new SalaryReportVO[list.size()]), pk_corp, map);
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		checkbeforeSave(qj, list.toArray(new SalaryReportVO[list.size()]), corpvo, 0, billtype);
		SalaryCipherHandler.handlerSalaryVO(list.toArray(new SalaryReportVO[0]), 0);// 保存加密处理
		clearJs(list.toArray(new SalaryReportVO[0]));
		List<SalaryReportVO> listnew = new ArrayList<SalaryReportVO>();// 新增
		List<SalaryReportVO> listedit = new ArrayList<SalaryReportVO>();// 修改
		for (SalaryReportVO vo : list) {

			if (vo.getPk_salaryreport() == null || StringUtil.isEmpty(vo.getPk_salaryreport())) {
				listnew.add(vo);
			} else {
				listedit.add(vo);
				// 校验数据是否被他人修改
				refcheck.isDataEffective(vo);
			}
			if (StringUtil.isEmpty(vo.getBilltype())) {
				vo.setBilltype(billtype);
			}
		}

		if (listnew != null && listnew.size() > 0) {
			for (SalaryReportVO vo : listnew) {
				vo.setPk_corp(pk_corp);
				vo.setQj(qj);
				vo.setCoperatorid(cuserid);
				if (StringUtil.isEmpty(vo.getVphone())) {
					if (corpvo != null)
						vo.setVphone(corpvo.getPhone1());
				}
			}
			singleObjectBO.insertVOArr(pk_corp, listnew.toArray(new SalaryReportVO[listnew.size()]));
		}
		if (listedit != null && listedit.size() > 0) {
			for (SalaryReportVO vo : listedit) {
				vo.setPk_corp(pk_corp);
				vo.setQj(qj);
				vo.setCoperatorid(cuserid);
				if (StringUtil.isEmpty(vo.getVphone())) {
					if (corpvo != null)
						vo.setVphone(corpvo.getPhone1());
				}
			}
			singleObjectBO.updateAry(listedit.toArray(new SalaryReportVO[listedit.size()]));
		}

		return query(pk_corp, qj, billtype);
	}

	@Override
	public SalaryReportVO[] delete(String pk_corp, String pk, String qj2) throws DZFWarpException {
		if (queryIsGZ(pk_corp, qj2).booleanValue()) {
			throw new BusinessException("当月已关账，不能操作！");
		}
		checkPzDelete(pk_corp, qj2, "gzjt");
		checkPzDelete(pk_corp, qj2, "gzff");
		String pks[] = DZFStringUtil.getString2Array(pk, ",");
		if (DZFValueCheck.isNotEmpty(pks)) {

			List<SQLParameter> list = new ArrayList<SQLParameter>();
			SQLParameter sp = null;
			for (String pk1 : pks) {
				sp = new SQLParameter();
				sp.addParam(1);
				sp.addParam(pk1);
				sp.addParam(pk_corp);
				list.add(sp);
			}
			// 更新公司所有凭证
			String sql = " update ynt_salaryreport set dr = ? where pk_salaryreport =? and pk_corp = ? ";
			singleObjectBO.executeBatchUpdate(sql.toString(), list.toArray(new SQLParameter[list.size()]));
		}
		return null;
	}

	private void checkPzDelete(String pk_corp, String qj, String str) {
		String sourcebilltype = pk_corp + qj + str;
		//
		String wheresql = " pk_corp = ? and sourcebilltype = ? and nvl(dr,0) = 0  ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(sourcebilltype);
		TzpzHVO[] hvos = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, wheresql, sp);

		String msg = null;
		if ("gzjt".equalsIgnoreCase(str)) {
			msg = "工资计提";
		} else {
			msg = "工资发放";
		}

		if (queryIsGZ(pk_corp, qj).booleanValue()) {
			throw new BusinessException(msg + "凭证月份已关账！");
		}
		if (hvos != null && hvos.length > 0) {
			throw new BusinessException("已生成" + msg + "凭证！");
		}
	}

	@Override
	public DZFBoolean queryIsGZ(String pk_corp, String qj) throws DZFWarpException {
		String wheresql = " pk_corp = ? and qj = ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(qj);
		JudgeIsGZVO[] vos = (JudgeIsGZVO[]) singleObjectBO.queryByCondition(JudgeIsGZVO.class, wheresql, sp);
		if (vos != null && vos.length > 0) {
			if (vos[0].getIsGz() != null && vos[0].getIsGz().booleanValue()) {
				return DZFBoolean.TRUE;
			}
		} else {
			JudgeIsGZVO vo = new JudgeIsGZVO();
			vo.setQj(qj);
			vo.setPk_corp(pk_corp);
			vo.setIsGz(DZFBoolean.FALSE);
			singleObjectBO.saveObject(pk_corp, vo);
		}
		return DZFBoolean.FALSE;
	}

	@Override
	public DZFBoolean isGZ(String pk_corp, String qj, String isgz) throws DZFWarpException {
		String wheresql = " pk_corp = ? and qj = ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(qj);
		JudgeIsGZVO[] vos = (JudgeIsGZVO[]) singleObjectBO.queryByCondition(JudgeIsGZVO.class, wheresql, sp);
		if (vos != null && vos.length > 0) {
			if ("true".equals(isgz)) {
				vos[0].setIsGz(DZFBoolean.TRUE);
				singleObjectBO.update(vos[0]);
				return DZFBoolean.TRUE;
			}
			if ("false".equals(isgz)) {
				vos[0].setIsGz(DZFBoolean.FALSE);
				singleObjectBO.update(vos[0]);
				return DZFBoolean.FALSE;
			}
		}
		return null;
	}

	@Override
	public String judgeHasPZ(String pk_corp, String qj) throws DZFWarpException {
		String msg = "";
		String sourcebilltype = pk_corp + qj + "gzjt";
		String sourcebilltype1 = pk_corp + qj + "gzff";
		String wheresql = " sourcebilltype in (?,?) and nvl(dr,0) = 0  ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(sourcebilltype);
		sp.addParam(sourcebilltype1);
		TzpzHVO[] hvos = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, wheresql, sp);
		if (hvos != null && hvos.length > 0) {
			//
			boolean bool = true;
			boolean bool1 = true;
			for (TzpzHVO hvo : hvos) {
				if (sourcebilltype.equals(hvo.getSourcebilltype())) {
					bool = false;
				}
				if (sourcebilltype1.equals(hvo.getSourcebilltype())) {
					bool1 = false;
				}
			}
			if (bool) {
				msg += "工资计提 ";
			}
			if (bool1) {
				msg += " 工资发放 ";
			}
		} else {
			msg += "工资计提   工资发放";
		}
		if (msg.length() > 0) {
			msg += " 凭证未生成，是否继续？";
		}
		return msg;
	}

	@Override
	public void checkPz(String pk_corp, String qj, String str, boolean ischeckdata) throws DZFWarpException {

		if (ischeckdata) {
			SalaryReportVO[] srvos = queryAllType(pk_corp, qj);
			if (srvos == null || srvos.length == 0)
				throw new BusinessException("没有需要生成凭证的工资表数据！");

		}

		String sourcebilltype = pk_corp + qj + str;
		//
		String wheresql = " pk_corp = ? and sourcebilltype = ? and nvl(dr,0) = 0  ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(sourcebilltype);
		TzpzHVO[] hvos = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, wheresql, sp);

		String msg = null;
		if ("gzjt".equalsIgnoreCase(str)) {
			msg = "工资计提";
		} else {
			msg = "工资发放";
		}

		if (queryIsGZ(pk_corp, qj).booleanValue()) {
			throw new BusinessException(msg + "凭证月份已关账！");
		}
		if (hvos != null && hvos.length > 0) {
			if (hvos[0].getIshasjz().booleanValue()) {// 已记账或已审核
				throw new BusinessException(msg + "凭证已记账！");
			}
			if (hvos[0].getVbillstatus() == 1) {
				throw new BusinessException(msg + "凭证已审核！");
			}

		}
	}

	@Override
	public void updateFykm(String pk_corp, String fykmid, String pk, String qj, String billtype)
			throws BusinessException {

		if (StringUtil.isEmpty(fykmid))
			return;

		checkPz(pk_corp, qj, "gzjt", false);
		checkPz(pk_corp, qj, "gzff", false);

		SalaryReportVO[] savos = query(pk_corp, qj, billtype);

		// 记录相同部门的记录
		List<SalaryReportVO> samelist = new ArrayList<SalaryReportVO>();

		String pks[] = DZFStringUtil.getString2Array(pk, ",");
		if (DZFValueCheck.isNotEmpty(pks)) {
			List<SalaryReportVO> list = new ArrayList<>();
			String strids = SqlUtil.buildSqlConditionForIn(pks);

			SalaryReportVO[] salays = (SalaryReportVO[]) singleObjectBO.queryByCondition(SalaryReportVO.class,
					" pk_salaryreport in ( " + strids + " ) ", null);
			List<String> deptlist = new ArrayList<>();
			for (SalaryReportVO vo : salays) {
				if (StringUtil.isEmpty(vo.getCdeptid()))
					continue;
				if (!deptlist.contains(vo.getCdeptid())) {
					deptlist.add(vo.getCdeptid());
				}
			}
			SalaryReportVO vo = null;
			for (String str : pks) {
				vo = new SalaryReportVO();
				vo.setPk_salaryreport(str);
				vo.setFykmid(fykmid);
				list.add(vo);
			}

			if (savos != null && savos.length > 0) {
				for (SalaryReportVO vo1 : savos) {
					if (deptlist.contains(vo1.getCdeptid())) {
						samelist.add(vo1);
					}
				}
				for (SalaryReportVO svo : samelist) {
					svo.setFykmid(fykmid);
					list.add(svo);
				}
			}
			Map<String, YntCpaccountVO> map = accService.queryMapByPk(pk_corp);
			checkAccRefDoc(list.toArray(new SalaryReportVO[list.size()]), map);
			singleObjectBO.updateAry(list.toArray(new SalaryReportVO[list.size()]), new String[] { "fykmid" });
		}
	}

	@Override
	public void updateDeptid(String pk_corp, String deptid, String pk, String qj, String billtype)
			throws BusinessException {

		if (StringUtil.isEmpty(deptid))
			return;

		checkPz(pk_corp, qj, "gzjt", false);
		checkPz(pk_corp, qj, "gzff", false);

		SalaryReportVO[] savos = query(pk_corp, qj, billtype);

		// 记录相同部门的记录
		List<SalaryReportVO> samelist = new ArrayList<SalaryReportVO>();
		if (savos != null && savos.length > 0) {
			for (SalaryReportVO vo : savos) {
				if (deptid.equals(vo.getCdeptid())) {
					samelist.add(vo);
				}
			}
		}

		String pks[] = DZFStringUtil.getString2Array(pk, ",");
		if (DZFValueCheck.isNotEmpty(pks)) {
			List<SalaryReportVO> list = new ArrayList<>();
			SalaryReportVO vo = null;
			int i = 0;
			// 按照第一个费用科目更新其他选中的科目
			SalaryReportVO firstvo = null;
			for (String str : pks) {
				if (i == 0) {
					firstvo = (SalaryReportVO) singleObjectBO.queryByPrimaryKey(SalaryReportVO.class, str);
					if (firstvo == null) {
						throw new BusinessException("选中的第一条工资条目不存在，请重新查询");
					}
				}
				vo = new SalaryReportVO();
				vo.setPk_salaryreport(str);
				vo.setCdeptid(deptid);
				vo.setFykmid(firstvo.getFykmid());
				list.add(vo);
				i++;
			}

			for (SalaryReportVO svo : samelist) {
				svo.setFykmid(firstvo.getFykmid());
				list.add(svo);
			}
			Map<String, YntCpaccountVO> map = accService.queryMapByPk(pk_corp);
			checkRefDoc(list.toArray(new SalaryReportVO[list.size()]), pk_corp, map);
			singleObjectBO.updateAry(list.toArray(new SalaryReportVO[list.size()]),
					new String[] { "fykmid", "cdeptid" });
		}
	}

	public void calGzb(String pk_corp, String[] pids, String qj, String billtype) {
		// 更新当期工资表
		if (pids != null && pids.length > 0) {
			String strids = SqlUtil.buildSqlConditionForIn(pids);
			Map<String, SalaryReportVO> lastperiodmap = getLastPeriodSalaryReport(pk_corp, qj);

			Map<String, SalaryReportVO> lasttotalmap = getLastTotailSalaryReport(pk_corp, billtype, qj);
			SalaryReportVO[] salays = (SalaryReportVO[]) singleObjectBO.queryByCondition(SalaryReportVO.class,
					" cpersonid in ( " + strids + " ) ", null);
			if (salays != null && salays.length > 0) {
				Map<String, SalaryBaseVO> basemap = gl_gzbbaseserv.getSalaryBaseVO(pk_corp, qj);
				SalaryCipherHandler.handlerSalaryVO(salays, 1); // 解密控制
				for (SalaryReportVO vo : salays) {
					gl_gzbcalserv.calSbGjj(vo, null, basemap.get(vo.getCpersonid()), billtype, false, false);
					setLjTaxData(lastperiodmap, lasttotalmap, vo.getCpersonid(), vo, billtype, qj);
					calGz(vo, billtype, qj, true);
				}
				SalaryCipherHandler.handlerSalaryVO(salays, 0); // 加密控制
				clearJs(salays);
				singleObjectBO.updateAry(salays);
			}
		}
	}

	@Override
	public Object[] saveImpExcelForTax(String loginDate, String cuserid, CorpVO loginCorpInfo, SalaryReportVO[] vos,
			String opdate, int type, String billtype) throws DZFWarpException {

		SalaryReportVO[] srvos = query(loginCorpInfo.getPk_corp(), opdate, billtype);
		if (srvos != null && srvos.length > 0) {
			singleObjectBO.deleteVOArray(srvos);
		}
		Object[] os = saveImpExcel(loginDate, cuserid, loginCorpInfo, vos, opdate, type, billtype);
		return os;
	}

	private void clearJs(SalaryReportVO[] vos) {
		if (DZFValueCheck.isNotEmpty(vos)) {
			String[] columns = new String[] { "yfbx_js", "yfbx_bl", "yfbx_mny", "ylbx_js", "ylbx_bl", "ylbx_mny",
					"sybx_js", "sybx_bl", "sybx_mny", "gjj_js", "gjj_bl", "gjj_mny", "gsbx_js", "shybx_js", "qygjj_bl",
					"qyyfbx_bl", "qyylbx_bl", "qysybx_bl", "qygsbx_bl", "qyshybx_bl" };
			for (SalaryReportVO vo : vos) {
				for (String column : columns) {
					vo.setAttributeValue(column, DZFDouble.ZERO_DBL);
				}
			}
		}
	}

	@Override
	public QueryPageVO queryBodysBypage(String pk_corp, String qj, String billtype, int page, int rows) {
		String condition = " pk_corp = ? and qj = ? and nvl(dr,0) = 0 and nvl(billtype,'01') = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(qj);
		sp.addParam(billtype);
		// 查询总数
		int total = singleObjectBO.getTotalRow("ynt_salaryreport", condition, sp);

		// 根据查询条件查询公司的信息
		StringBuffer sf = new StringBuffer();
		sf.append(" select y.*,b.code ygbm1 ");
		sf.append(" From ynt_salaryreport y ");
		sf.append(" left join  ynt_fzhs_b  b  on y.cpersonid = b.pk_auacount_b ");
		sf.append(" and b.pk_auacount_h=? ");
		sf.append(" where y.pk_corp = ? ");
		sf.append(" and nvl(y.dr, 0) = 0 ");
		sf.append(" and y.qj= ? and nvl(y.billtype,'01') = ? ");
		sp.clearParams();
		sp.addParam(AuxiliaryConstant.ITEM_STAFF);
		sp.addParam(pk_corp);
		sp.addParam(qj);
		sp.addParam(billtype);
		List<SalaryReportVO> list = (List<SalaryReportVO>) singleObjectBO.execQueryWithPage(SalaryReportVO.class,
				"(" + sf.toString() + ")", null, sp, page, rows, "order by t.ygbm1 ");

		SalaryReportVO[] vos = querySalaryReportVO(list.toArray(new SalaryReportVO[0]), pk_corp, false);
		QueryPageVO pagevo = new QueryPageVO();
		pagevo.setTotal(total);
		pagevo.setPage(page);
		pagevo.setPageofrows(rows);
		pagevo.setPagevos(vos);
		return pagevo;
	}

}
