package com.dzf.zxkj.report.service.power.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.ButtonPowerBVo;
import com.dzf.zxkj.platform.model.report.ButtonPowerVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.service.power.IButtonPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("btn_power_ser")
public class ButtonPowerServiceImpl implements IButtonPowerService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public String qryButtonPower(String pk_corp) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_corp)) {
			return "公司不存在!";
		}

		CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);

		if (cpvo == null) {
			return "公司不存在!";
		}

		Integer status = null;//判断状态
		SQLParameter sp = new SQLParameter();
		
		//先看自身是否 控制
		sp.addParam(pk_corp);
		ButtonPowerBVo bvo = (ButtonPowerBVo) singleObjectBO.executeQuery(" select * from sm_power_button_b b where nvl(dr,0)=0 and pk_corpk = ? ",
				sp, new BeanProcessor(ButtonPowerBVo.class));
		
		//如果   bvo 为空，走第二步判断
		if(bvo == null){
			sp.clearParams();
			sp.addParam(cpvo.getFathercorp());
			ButtonPowerVo[] vos = (ButtonPowerVo[]) singleObjectBO.queryByCondition(ButtonPowerVo.class,
					"nvl(dr,0)=0 and pk_account = ? ", sp);
			
			if(vos!=null && vos.length>0){
				ButtonPowerVo vo = vos[0];
				status = vo.getIbtnstatus();
			}
		}else{
			status = bvo.getIbtnstatus();
		}

		if(status != null && status.intValue() == 1){//按钮关闭状态
			if(cpvo.getIschannel()!=null && cpvo.getIschannel().booleanValue()){
				return "暂不能导出，请联系大账房会计运营经理导出哦！";
			}else{
				return "暂不能导出，请联系大账房客服，为此造成的不便，请谅解！";
			}
		}

		return "";
	}

}
