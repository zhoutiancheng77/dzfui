package com.dzf.zxkj.platform.services.icset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.common.constant.InventoryConstant;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * 库存单据号自动生成
 * @author zpm
 *
 */
@Component("icbillcode_create")
@Slf4j
public class ICbillcodeCreate {

	@Autowired
	private SingleObjectBO singleObjectBO;

	/**
	 * 获取库存单据编码
	 */
	public void setICbillcode(String pk_corp, String period, String style, TzpzHVO tzpzhvo){
		if(StringUtil.isEmpty(pk_corp) || StringUtil.isEmpty(period) || StringUtil.isEmpty(style))
			return;
		//修改情况，，以原先的值为准，不从界面获取，是因为太多的联查过来的凭证。
		//比如手工将采购 的凭证，改成成本的凭证.
		if(!StringUtil.isEmpty(tzpzhvo.getPk_tzpz_h())){
			TzpzHVO hvo = (TzpzHVO)singleObjectBO.queryByPrimaryKey(TzpzHVO.class, tzpzhvo.getPk_tzpz_h());
			if(!StringUtil.isEmpty(hvo.getVicbillcode()) && style.equals(hvo.getVicbillcodetype())){
				tzpzhvo.setVicbillcode(hvo.getVicbillcode());
				tzpzhvo.setVicbillcodetype(hvo.getVicbillcodetype());
				return;
			}
		}
//		使用synchronized，不太适用在分布式场景中。//synchronized (pk_corp){}
		String uuid = UUID.randomUUID().toString();
		String rediskey =  "";
		try{
			//从redis 中获取。
			rediskey = pk_corp+","+period;
			String billcode1 = "";
			//从数据库中获取。
			String billcode2 = getMaxicBillcode(pk_corp,period,style);
			//比较后，新生成的单据编码，，返回结果存入数据库。
			if(StringUtil.isEmpty(billcode1))
				billcode1 = "";
			if(StringUtil.isEmpty(billcode2))
				billcode2 = "";
			if(billcode1.compareTo(billcode2)<0)
				billcode1 = billcode2;
			if(StringUtil.isEmpty(billcode1)){
				if(InventoryConstant.IC_STYLE_IN.equals(style)){
					billcode1 = InventoryConstant.IC_IN_PREFIX+"0001";
				}else if(InventoryConstant.IC_STYLE_OUT.equals(style)){
					billcode1 = InventoryConstant.IC_OUT_PREFIX+"0001";
				}
			}else{
				Integer code = Integer.valueOf(billcode1.substring(billcode1.length()-4, billcode1.length()))+1;
				String icode = "";
				for(int i = code.toString().length(); i < 4; i++){
					icode = icode+"0";
				}
				if(InventoryConstant.IC_STYLE_IN.equals(style)){
					billcode1 = InventoryConstant.IC_IN_PREFIX+icode+code.toString();
				}else if(InventoryConstant.IC_STYLE_OUT.equals(style)){
					billcode1 = InventoryConstant.IC_OUT_PREFIX+icode+code.toString();
				}
			}
			tzpzhvo.setVicbillcode(billcode1);
			tzpzhvo.setVicbillcodetype(style);
		}catch(Exception e){
			log.error("生成库存单据号失败",e);
		}
	}
	
	private String getMaxicBillcode(String pk_corp,String period,String vicbillcodetype){
		String sql =" select max(vicbillcode) billcode from ynt_tzpz_h  where vicbillcodetype = ? and pk_corp = ? and period = ? and nvl(dr,0) = 0  ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(vicbillcodetype);
		sp.addParam(pk_corp);
		sp.addParam(period);
		String billcode = (String)singleObjectBO.executeQuery(sql, sp, new ResultSetProcessor(){
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				String billcode = "";
				if(rs.next()){
					billcode = rs.getString("billcode");
				}
				return billcode;
			}
		});
		return billcode;	
	}
}