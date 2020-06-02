package com.dzf.zxkj.app.utils;

import java.util.List;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 科目公共方法
 * 
 * @author zhangj
 *
 */
public class CpaccountUtil {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IZxkjRemoteAppService iZxkjRemoteAppService;

//	@Autowired
//	private ICpaccountService gl_cpacckmserv;
//
//	@Autowired
//	private ICpaccountCodeRuleService gl_accountcoderule;

	public static CpaccountUtil appQueryutil = null;

	public static CpaccountUtil getInstance() {
		if (appQueryutil == null) {
			appQueryutil = new CpaccountUtil();
		}
		return appQueryutil;
	}

//	private CpaccountUtil() {
//		super();
//		singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
//		gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
//		gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils.getBean("gl_accountcoderule");
//	}


	/**
	 * 查找对应的下级科目或者辅助项目
	 *
	 * @param kmcode
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public Object[] getNextKmOrFzxm(String kmcode, String pk_corp, YntCpaccountVO[] cpavos, String gfmc, String xfmc)
			throws DZFWarpException {

		if (StringUtil.isEmpty(kmcode)) {
			throw new BusinessException("科目不能为空!");
		}
		if (AppCheckValidUtils.isEmptyCorp(pk_corp)) {
			throw new BusinessException("公司不能为空!");
		}

		SQLParameter sp = new SQLParameter();

		Object[] nextids = obtainKM(cpavos, kmcode, singleObjectBO, sp);
		if (nextids == null) {
			throw new BusinessException("未查到相应科目(或辅助项目)!");
		}
		return nextids;
	}


	private Object[] obtainKM(YntCpaccountVO[] cpavos, String kmcode, SingleObjectBO singleObjectBO, SQLParameter sp) {
		for (YntCpaccountVO cpavo : cpavos) {
			if (cpavo.getAccountcode().equals(kmcode)) {
				if (cpavo.getIsleaf() != null && cpavo.getIsleaf().booleanValue()
						&&(StringUtil.isEmpty(cpavo.getIsfzhs()) || cpavo.getIsfzhs().indexOf("1") <0)
						) {
					return new Object[] { cpavo, };
				} else if (cpavo.getIsfzhs().indexOf("1") >= 0) {
					sp.clearParams();
					int value = cpavo.getIsfzhs().indexOf("1");
					// 查询对应的辅助项目
					StringBuffer qrysql = new StringBuffer();
					qrysql.append("select b.code,b.name,b.pk_auacount_b from ynt_fzhs_b b ");
					qrysql.append(" inner join ynt_fzhs_h h on b.pk_auacount_h = b.pk_auacount_h ");
					qrysql.append(" where nvl(b.dr,0)=0 and nvl(h.dr,0)=0");
					qrysql.append(" and b.pk_corp = ? and h.code = ? ");
					qrysql.append("  order by b.code ");
					sp.addParam(cpavo.getPk_corp());
					sp.addParam((value+1));
					List<AuxiliaryAccountBVO> bvolist = (List<AuxiliaryAccountBVO>) singleObjectBO
							.executeQuery(qrysql.toString(), sp, new BeanListProcessor(AuxiliaryAccountBVO.class));
					return new Object[] { cpavo, bvolist.get(0) };
				} else {
					String newrule = iZxkjRemoteAppService.queryAccountRule(cpavo.getPk_corp());
					String kmnew = getNextCode(kmcode, newrule);
					return obtainKM(cpavos, kmnew, singleObjectBO, sp);
				}
			}
		}
		return null;
	}
	
	
	public static String getNextCode(String code,String newrule){
		if(StringUtil.isEmpty(code) || StringUtil.isEmpty(newrule)){
			return  code;
		}
		
		String[] strs = newrule.split("/");
		int count =0;
		String tempcode = "";
		for(int i=0;i<strs.length;i++){
			count = count+Integer.parseInt(strs[i]);
			if(code.length() == count){
				if(i == strs.length-1){
					tempcode = String.format("%02d", 1);
				}else{
					String temp = "%0"+Integer.parseInt(strs[i+1])+"d";
					tempcode = String.format(temp, 1);
				}
				break;
			}
		}
		
		
		return code+tempcode;
	}
	
}
