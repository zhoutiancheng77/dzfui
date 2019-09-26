package com.dzf.zxkj.platform.services.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.RemittanceVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.services.bdset.ICpaccountService;
import com.dzf.zxkj.platform.services.jzcl.IVoucherTemplate;
import com.dzf.zxkj.platform.services.sys.IAccountService;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("vouchertempser")
public class VoucherTemplateImpl implements IVoucherTemplate {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private ICpaccountService gl_cpacckmserv;

	@Autowired
	private ICpaccountCodeRuleService gl_accountcoderule;

	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accountService;

	@Override
	public SuperVO[] queryTempateByName(String classname, String pk_corp) throws DZFWarpException {
		try {
			if (StringUtil.isEmpty(classname) || StringUtil.isEmpty(pk_corp)) {
				throw new BusinessException("参数不能为空");
			}
			CorpVO cpvo = corpService.queryByPk(pk_corp);

			SuperVO tvo = (SuperVO) Class.forName(classname).newInstance();

			String tablename = tvo.getTableName();

			// 查询公司数据
			List<SuperVO> list = queryCorpData(pk_corp, tvo, tablename);

			if (list == null || list.size() == 0) {
				// 查询行业数据
				list = queryHyData(cpvo, tvo, tablename, pk_corp);
			}
			SuperVO[] resvos = converArray(list,classname);
			return resvos;

		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw new BusinessException(e.getMessage());
			} else {
				throw new WiseRunException(e);
			}
		}
	}

	private List<SuperVO> queryHyData(CorpVO cpvo, SuperVO tvo, String tablename, String pk_corp) {
		List<SuperVO> list;
		SQLParameter sp1 = new SQLParameter();
		String qrysql1 = "select * from " + tablename
				+ " where nvl(dr,0)=0 and pk_corp = ? and pk_trade_accountschema = ? ";
		sp1.addParam(IGlobalConstants.currency_corp);
		sp1.addParam(cpvo.getCorptype());
		list = (List<SuperVO>) singleObjectBO.executeQuery(qrysql1, sp1, new BeanListProcessor(tvo.getClass()));
//		if(list == null || list.size() ==0 ){
//			throw new BusinessException("该制度暂不支持，敬请期待");
//		}
		// 生成公司级科目PK
		putCorpPkFromJt(list, tvo, pk_corp);
		return list;
	}

	private List<SuperVO> queryCorpData(String pk_corp, SuperVO tvo, String tablename) {
		SQLParameter sp = new SQLParameter();

		String qrysql = "select * from " + tablename + " where nvl(dr,0)=0 and pk_corp = ?";

		sp.addParam(pk_corp);

		List<SuperVO> list = (List<SuperVO>) singleObjectBO.executeQuery(qrysql, sp,
				new BeanListProcessor(tvo.getClass()));
		return list;
	}

	/**
	 * 根据公司级的科目
	 * 
	 * @param list
	 */
	private void putCorpPkFromJt(List<SuperVO> list, SuperVO tvo, String pk_corp) {
		String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);

		YntCpaccountVO[] cpavos = accountService.queryByPk(pk_corp);

		Map<String, YntCpaccountVO> cpamap = convertMap(cpavos);
		/**
		 * 汇兑模板
		 */
		if (tvo instanceof RemittanceVO) {
			handleHd(list, newrule, cpamap);
		}

	}

	private void handleHd(List<SuperVO> list, String newrule, Map<String, YntCpaccountVO> cpamap) {
		// pk_corp_account(accountcode) ,pk_out_account(outatcode)
		String accountcode = "";
		String outatcode = "";
		for (SuperVO vo : list) {
			accountcode = (String) vo.getAttributeValue("accountcode");
			outatcode = (String) vo.getAttributeValue("outatcode");

			if (!StringUtil.isEmpty(accountcode)) {
				putPkFromCode(vo, accountcode, newrule, cpamap, "pk_corp_account");
			}
			if (!StringUtil.isEmpty(outatcode)) {
				putPkFromCode(vo, outatcode, newrule, cpamap, "pk_out_account");
			}
		}
	}

	private void putPkFromCode(SuperVO vo, String accountcode, String newrule, Map<String, YntCpaccountVO> cpamap,
			String columname) {

		// 根据行业的科目编码，或者新的code
		String newcode = gl_accountcoderule.getNewRuleCode(accountcode, DZFConstant.ACCOUNTCODERULE, newrule);

		YntCpaccountVO cpavo = cpamap.get(newcode);
		if (cpavo != null) {
			vo.setAttributeValue(columname, cpavo.getPrimaryKey());
		}
	}

	/**
	 * 根据vos转换成Map集合
	 * 
	 * @param cpavos
	 * @return
	 */
	private Map<String, YntCpaccountVO> convertMap(YntCpaccountVO[] cpavos) {

		Map<String, YntCpaccountVO> resmap = new HashMap<String, YntCpaccountVO>();

		for (YntCpaccountVO cpavo : cpavos) {
			resmap.put(cpavo.getAccountcode(), cpavo);
		}

		return resmap;
	}

	private SuperVO[] converArray(List<SuperVO> list,String classname) throws NegativeArraySizeException, ClassNotFoundException {
		if (list != null && list.size() > 0) {
			SuperVO[] svos = (SuperVO[]) Array.newInstance(Class.forName(classname), list.size());
			for (int i = 0; i < list.size(); i++) {
				svos[i] = list.get(i);
			}
			return svos;
		} else {
			return null;
		}
	}

}
