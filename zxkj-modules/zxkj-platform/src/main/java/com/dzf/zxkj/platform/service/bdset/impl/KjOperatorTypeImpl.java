package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.sys.IOperatorType;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("sys_ope_type")
public class KjOperatorTypeImpl implements IOperatorType {

	@Override
	public List<LogRecordEnum> getLogEnum(CorpVO cpvo) throws DZFWarpException {
		// 根据当前公司的类型，获取对应的操作权限
//		DZFBoolean icqy = cpvo.getBbuildic();// 是否启用库存

		DZFBoolean holdflag = cpvo.getHoldflag();// 是否启用固定资产

		List<LogRecordEnum> records = new ArrayList<>();

		for (LogRecordEnum enumtemp : LogRecordEnum.getKjSysEnum()) {// 基础
			records.add(enumtemp);
		}
		records.add(LogRecordEnum.OPE_ADMIN_WDKH);

		if (holdflag != null && holdflag.booleanValue()) {// 资产
			for (LogRecordEnum enumtemp : LogRecordEnum.getKjZcEnum()) {
				records.add(enumtemp);
			}
		}

		if (IcCostStyle.IC_ON.equals(cpvo.getBbuildic())) {// 库存
			for (LogRecordEnum enumtemp : LogRecordEnum.getKjIcEnum()) {
				records.add(enumtemp);
			}
		} else if (IcCostStyle.IC_INVTENTORY.equals(cpvo.getBbuildic())) {//总账核算存货
			for (LogRecordEnum enumtemp : LogRecordEnum.getKjChglEnum()) {
				records.add(enumtemp);
			}
		}
		
		return records;
	}

	@Override
	public List<UserVO> getListUservo(String pk_corp) throws DZFWarpException {
		StringBuffer qrysql = new StringBuffer();

		SQLParameter sp = new SQLParameter();

		qrysql.append("select distinct sm.cuserid ,sm.user_name ,sm.user_code ");
		qrysql.append("  from sm_user_role sr ");
		qrysql.append(" inner join sm_user sm ");
		qrysql.append("    on sr.cuserid = sm.cuserid ");
		qrysql.append(" inner join bd_corp bp ");
		qrysql.append("    on bp.pk_corp = sr.pk_corp ");
		qrysql.append(" where sr.pk_corp = ? ");
		qrysql.append(" order by sm.user_code ");

		SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");

		sp.addParam(pk_corp);
		List<UserVO> listuvos = (List<UserVO>) singleObjectBO.executeQuery(qrysql.toString(), sp,
				new BeanListProcessor(UserVO.class));
		
		listuvos = (List<UserVO>) QueryDeCodeUtils.decKeyUtils(new String[]{"user_name"}, listuvos, 1);

		return listuvos;
	}

}
