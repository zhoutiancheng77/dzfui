package com.dzf.zxkj.platform.service.gzgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.utils.*;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.enums.SalaryTypeEnum;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryAccSetVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryBaseVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.gzgl.ISalaryAccSetService;
import com.dzf.zxkj.platform.service.gzgl.ISalaryBaseService;
import com.dzf.zxkj.platform.service.gzgl.ISalaryCalService;
import com.dzf.zxkj.platform.service.gzgl.ISalaryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service("gl_gzbbaseserv")
public class SalaryBaseServiceImpl implements ISalaryBaseService {

	@Autowired
	private ISalaryReportService gl_gzbserv = null;

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private ISalaryCalService gl_gzbcalserv;

	@Autowired
	private ISalaryAccSetService gl_gzkmszserv = null;

	@Override
	public SalaryBaseVO[] query(String pk_corp, String qj) throws DZFWarpException {
		String wheresql = " pk_corp = ? and qj = ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(qj);
		SalaryBaseVO[] srvos = (SalaryBaseVO[]) singleObjectBO.queryByCondition(SalaryBaseVO.class, wheresql, sp);

		List<AuxiliaryAccountBVO> list = gl_fzhsserv.queryPerson(AuxiliaryConstant.ITEM_STAFF, pk_corp, null);
		Map<String, AuxiliaryAccountBVO> aumap = DZfcommonTools.hashlizeObjectByPk(list,
				new String[] { "pk_auacount_b" });
		for (SalaryBaseVO vo : srvos) {
			AuxiliaryAccountBVO personvo = aumap.get(vo.getCpersonid());
			if (personvo != null) {
				vo.setYgbm(personvo.getCode());
				vo.setYgname(personvo.getName());
				vo.setZjbm(personvo.getZjbm());
				vo.setZjlx(personvo.getZjlx());
			}
		}
		VOUtil.ascSort(srvos, new String[] { "ygbm" });
		return srvos;
	}

	public Map<String, SalaryBaseVO> getSalaryBaseVO(String pk_corp, String qj) throws BusinessException {
		StringBuffer strb = new StringBuffer();
		strb.append(" select * from ynt_salarybase e1 join");
		strb.append(" (select cpersonid,max(qj) qj from ynt_salarybase  e ");
		strb.append(" where e.pk_corp = ? and e.qj <= ?  and nvl(e.dr,0) = 0 ");
		strb.append(" group by cpersonid) e2 ");
		strb.append(" on  e1.cpersonid = e2.cpersonid and e1.qj = e2.qj ");
		strb.append(" where e1.pk_corp = ? and e1.qj <= ?  and nvl(e1.dr,0) = 0 ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(qj);
		sp.addParam(pk_corp);
		sp.addParam(qj);

		List<SalaryBaseVO> list = (List<SalaryBaseVO>) singleObjectBO.executeQuery(strb.toString(), sp,
				new BeanListProcessor(SalaryBaseVO.class));

		Map<String, SalaryBaseVO> basemap = DZfcommonTools.hashlizeObjectByPk(list, new String[] { "cpersonid" });
		return basemap;
	}

	@Override
	public void saveChangeNum(String pk_corp, String cuserid, String ids, SalaryBaseVO basevo, SalaryAccSetVO accsetvo,
			String qj) throws BusinessException {

		// 更新入账设置
		// save(accsetvo);

		updateChangeNum(pk_corp, basevo, ids, qj, SalaryTypeEnum.NORMALSALARY.getValue());
	}

	@Override
	public void updateChangeNum(String pk_corp, SalaryBaseVO basevo, String ids, String qj, String billtype)
			throws BusinessException {

		if (basevo == null)
			return;

		gl_gzbserv.checkPz(pk_corp, qj, "gzjt", false);
		gl_gzbserv.checkPz(pk_corp, qj, "gzff", false);

		String[] pids = null;
		// 人员信息
		List<AuxiliaryAccountBVO> list = gl_fzhsserv.queryPerson(AuxiliaryConstant.ITEM_STAFF, pk_corp,
				SalaryTypeEnum.NORMALSALARY.getValue());
		Map<String, AuxiliaryAccountBVO> map = DZfcommonTools.hashlizeObjectByPk(list,
				new String[] { "pk_auacount_b" });

		if (DZFValueCheck.isEmpty(ids) || "null".equals(ids)) {
			// 更新全部人员的基数比例
			pids = map.keySet().toArray(new String[map.keySet().size()]);

		} else {
			pids = DZFStringUtil.getString2Array(ids, ",");
		}
		if (DZFValueCheck.isEmpty(pids))
			return;

		// 更新比例
		updateSalaryBaseVo(pk_corp, pids, basevo, qj);

		// 存在工资表 重新计算当期工资表
		gl_gzbserv.calGzb(pk_corp, pids, qj, billtype);
	}

	private void updateSalaryBaseVo(String pk_corp, String[] pids, SalaryBaseVO basevo, String qj) {

		List<SalaryBaseVO> baselist = getSalaryBaseVO(pids, qj);

		Map<String, SalaryBaseVO> basemap = DZfcommonTools.hashlizeObjectByPk(baselist, new String[] { "cpersonid" });

		basevo.setPk_corp(pk_corp);
		basevo.setQj(qj);
		basevo.setDr(0);

		List<SalaryBaseVO> nlist = new ArrayList<>();
		for (String pid : pids) {
			SalaryBaseVO vo = (SalaryBaseVO) basevo.clone();
			vo.setCpersonid(pid);
			if (DZFValueCheck.isNotEmpty(basemap.get(pid))) {
				vo.setPk_sqlarybase(basemap.get(pid).getPk_sqlarybase());
				nlist.add(vo);
			} else {
				nlist.add(vo);
			}
		}
		save(pk_corp, nlist, qj);

	}

	private List<SalaryBaseVO> getSalaryBaseVO(String[] pids, String qj) {

		TempSqlBuilder builder = new TempSqlBuilder();
		builder.select(new String[] { "*" });
		builder.from("ynt_salarybase");
		builder.where();
		builder.append("cpersonid", null, null, pids);
		builder.and();
		builder.append(" qj = ?");
		builder.appendDr();
		SQLParameter sp = new SQLParameter();
		sp.addParam(qj);
		String sql = builder.toString();

		List<SalaryBaseVO> baselist = (List<SalaryBaseVO>) singleObjectBO.executeQuery(sql, sp,
				new BeanListProcessor(SalaryBaseVO.class));

		return baselist;
	}

	private List<String> getPidList(String[] pids, String qj) {

		TempSqlBuilder builder = new TempSqlBuilder();
		builder.select(new String[] { "cpersonid" });
		builder.from("ynt_salarybase");
		builder.where();
		builder.append("pk_sqlarybase", null, null, pids);
		builder.and();
		builder.append(" qj = ?");
		builder.appendDr();
		SQLParameter sp = new SQLParameter();
		sp.addParam(qj);
		String sql = builder.toString();

		List<String> baselist = (List<String>) singleObjectBO.executeQuery(sql, sp, new ColumnListProcessor());

		return baselist;
	}

	// 判断是否存在后续工资表的 不允许调整基数
	private void checkGzb(String[] pids, String qj, String pk_corp, Map<String, AuxiliaryAccountBVO> aumap) {

		TempSqlBuilder builder = new TempSqlBuilder();
		builder.select();
		builder.distinct();
		builder.append("cpersonid");
		builder.from("ynt_salaryreport");
		builder.where();
		builder.append("cpersonid", null, null, pids);
		builder.and();
		builder.append(" qj >= ?");
		builder.appendDr();
		SQLParameter sp = new SQLParameter();
		sp.addParam(qj);
		String sql = builder.toString();
		List<String> baselist = (List<String>) singleObjectBO.executeQuery(sql, sp, new ColumnListProcessor());

		if (DZFValueCheck.isEmpty(baselist))
			return;

		StringBuffer strb = new StringBuffer();
		for (String id : baselist) {
			if(aumap.get(id)== null){
				strb.append("员工出错!<br>");
			}else{
				strb.append("员工" + aumap.get(id).getName() + "存在期间在" + qj + "或者之后的工资表!<br>");
			}
		}
		StringBuffer msg = new StringBuffer();
		if (DZFValueCheck.isNotEmpty(strb)) {
			msg.append("<font>" + strb.toString() + "</font>");
			throw new BusinessException(msg.toString());
		}
	}

	@Override
	public SalaryBaseVO[] save(String pk_corp, List<SalaryBaseVO> list, String qj) throws DZFWarpException {
		if (list == null || list.size() == 0) {
			throw new BusinessException("数据为空！");
		}

		HashSet<String> nameZjbmSet = new HashSet<String>();
		SalaryBaseVO[] qbvos = query(pk_corp, qj);

		List<String> slist = new ArrayList<>();
		for (SalaryBaseVO nbvo : list) {
			if (!StringUtil.isEmpty(nbvo.getPk_sqlarybase())) {
				slist.add(nbvo.getPk_sqlarybase());
			}
		}
		if (qbvos != null && qbvos.length > 0) {
			for (SalaryBaseVO bvo : qbvos) {
				if (slist.contains(bvo.getPk_sqlarybase())) {
					continue;
				}
				String namezjbm = bvo.getCpersonid();
				if (!StringUtil.isEmpty(namezjbm)) {
					nameZjbmSet.add(namezjbm);
				}
			}
		}
		List<String> pidlist = new ArrayList<>();
		StringBuffer strb = new StringBuffer();
		for (SalaryBaseVO basevo : list) {
			pidlist.add(basevo.getCpersonid());
			if (!nameZjbmSet.contains(basevo.getCpersonid())) {
				nameZjbmSet.add(basevo.getCpersonid());

			} else {
				if (StringUtil.isEmpty(basevo.getCpersonid())) {
					strb.append("员工不能为空!<br>");
				} else {
					strb.append("员工[" + basevo.getYgname() + "]已存在!<br>");
				}
			}
		}
		StringBuffer msg = new StringBuffer();
		if (DZFValueCheck.isNotEmpty(strb)) {
			msg.append("<font>" + strb.toString() + "</font>");
			throw new BusinessException(msg.toString());
		}

		// 人员信息
		List<AuxiliaryAccountBVO> alist = gl_fzhsserv.queryPerson(AuxiliaryConstant.ITEM_STAFF, pk_corp, null);
		Map<String, AuxiliaryAccountBVO> aumap = DZfcommonTools.hashlizeObjectByPk(alist,
				new String[] { "pk_auacount_b" });
		checkPsnRefDoc(list.toArray(new SalaryBaseVO[list.size()]), aumap);
		checkGzb(pidlist.toArray(new String[pidlist.size()]), qj, pk_corp, aumap);

		List<SalaryBaseVO> listnew = new ArrayList<>();// 新增
		List<SalaryBaseVO> listedit = new ArrayList<>();// 修改
		for (SalaryBaseVO vo : list) {

			if (vo.getPk_sqlarybase() == null || StringUtil.isEmpty(vo.getPk_sqlarybase())) {
				listnew.add(vo);
			} else {
				listedit.add(vo);
			}
		}

		if (listnew != null && listnew.size() > 0) {
			for (SalaryBaseVO vo : listnew) {
				vo.setPk_corp(pk_corp);
				vo.setQj(qj);
			}
			singleObjectBO.insertVOArr(pk_corp, listnew.toArray(new SalaryBaseVO[listnew.size()]));
		}
		if (listedit != null && listedit.size() > 0) {
			for (SalaryBaseVO vo : listedit) {
				vo.setPk_corp(pk_corp);
				vo.setQj(qj);
			}
			singleObjectBO.updateAry(listedit.toArray(new SalaryBaseVO[listedit.size()]));
		}

		return query(pk_corp, qj);
	}

	private void checkPsnRefDoc(SalaryBaseVO[] vos, Map<String, AuxiliaryAccountBVO> aumap) {

		if (vos == null || vos.length == 0)
			return;
		StringBuffer strb = new StringBuffer();
		for (SalaryBaseVO vo : vos) {
			if (StringUtil.isEmpty(vo.getCpersonid()))
				continue;
			if (aumap.get(vo.getCpersonid()) == null)
				strb.append("员工[" + vo.getYgname() + "]不存在,或者已删除!<br>");
		}
		StringBuffer msg = new StringBuffer();
		if (DZFValueCheck.isNotEmpty(strb)) {
			msg.append("<font>" + strb.toString() + "</font>");
			throw new BusinessException(msg.toString());
		}
	}

	@Override
	public SalaryBaseVO[] delete(String pk_corp, String pks, String qj) throws DZFWarpException {

		String[] pkss = DZFStringUtil.getString2Array(pks, ",");
		if (DZFValueCheck.isEmpty(pkss))
			return new SalaryBaseVO[0];

		// 人员信息
		List<AuxiliaryAccountBVO> alist = gl_fzhsserv.queryPerson(AuxiliaryConstant.ITEM_STAFF, pk_corp,
				SalaryTypeEnum.NORMALSALARY.getValue());
		Map<String, AuxiliaryAccountBVO> aumap = DZfcommonTools.hashlizeObjectByPk(alist,
				new String[] { "pk_auacount_b" });
		List<String> pids = getPidList(pkss, qj);
		
		String[] pidarr = DZFStringUtil.removeNull(pids.toArray(new String[pids.size()]));
		
		if(pidarr != null  && pidarr.length > 0){
			checkGzb(pids.toArray(new String[pids.size()]), qj, pk_corp, aumap);
		}

		List<SQLParameter> list = new ArrayList<SQLParameter>();
		SQLParameter sp = null;
		for (String pk1 : pkss) {
			sp = new SQLParameter();
			sp.addParam(1);
			sp.addParam(pk1);
			sp.addParam(pk_corp);
			list.add(sp);
		}
		// 更新公司数据
		String sql = " update ynt_salarybase set dr = ? where pk_sqlarybase =? and pk_corp = ? ";
		singleObjectBO.executeBatchUpdate(sql.toString(), list.toArray(new SQLParameter[list.size()]));
		return new SalaryBaseVO[0];
	}

	@Override
	public SalaryReportVO getSalarySetInfo(String pk_corp, String cpersonids, String qj) throws BusinessException {

		SalaryReportVO vo = new SalaryReportVO();
		vo.setQj(qj);
		String[] pks = DZFStringUtil.getString2Array(cpersonids, ",");
		SalaryAccSetVO setvo = gl_gzbserv.getSASVO(pk_corp, SalaryTypeEnum.NORMALSALARY.getValue());
		if (DZFValueCheck.isEmpty(pks)) {
			gl_gzbcalserv.calSbGjj(vo, setvo, null);
		} else {
			Map<String, SalaryBaseVO> basemap = getSalaryBaseVO(pk_corp, qj);
			gl_gzbcalserv.calSbGjj(vo, setvo, basemap.get(pks[0]));
		}
		return vo;
	}

	@Override
	public void saveChangeNumGroup(String pk_corp, String cuserid, SalaryAccSetVO accsetvo) throws BusinessException {

		String[] strs = new String[] { "yfbx_js", "yfbx_bl", "yfbx_mny", "qyyfbx_bl", "ylbx_js", "ylbx_bl", "ylbx_mny",
				"qyylbx_bl", "sybx_js", "sybx_bl", "sybx_mny", "qysybx_bl", "gjj_js", "gjj_bl", "gjj_mny", "qygjj_bl",
				"gsbx_js", "qygsbx_bl", "shybx_js", "qyshybx_bl" };
		gl_gzkmszserv.saveVoByColumn(pk_corp, accsetvo, strs);
	}

	@Override
	public QueryPageVO queryBodysBypage(String pk_corp, String qj, int page, int rows) throws DZFWarpException {

		String condition = " pk_corp = ? and qj = ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(qj);

		// 查询总数
		int total = singleObjectBO.getTotalRow("ynt_salarybase", condition, sp);

		// 根据查询条件查询公司的信息
		StringBuffer sf = new StringBuffer();
		sf.append(" select y.*,b.zjlx,b.zjbm,b.code ygbm,b.name  ygname");
		sf.append(" From ynt_salarybase y ");
		sf.append(" left join  ynt_fzhs_b  b  on y.cpersonid = b.pk_auacount_b ");
		sf.append(" and b.pk_auacount_h=? ");
		sf.append(" where y.pk_corp = ? ");
		sf.append(" and nvl(y.dr, 0) = 0 ");
		sf.append(" and y.qj= ? ");
		sp.clearParams();
		sp.addParam(AuxiliaryConstant.ITEM_STAFF);
		sp.addParam(pk_corp);
		sp.addParam(qj);
		List<SalaryBaseVO> list = (List<SalaryBaseVO>) singleObjectBO.execQueryWithPage(SalaryBaseVO.class,
				"(" + sf.toString() + ")", null, sp, page, rows, "order by ygbm ");
		QueryPageVO pagevo = new QueryPageVO();
		pagevo.setTotal(total);
		pagevo.setPage(page);
		pagevo.setPageofrows(rows);
		pagevo.setPagevos(list.toArray(new SalaryBaseVO[0]));
		return pagevo;
	}

}
