package com.dzf.zxkj.platform.service.pjgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.platform.service.pjgl.IVATGoodsInvenRelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("gl_goodsinvenrelaserv")
public class VATGoodsInvenRelaServiceImpl implements IVATGoodsInvenRelaService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public void deleteCasCadeGoods(String[] pks, String pk_corp) throws DZFWarpException {
		String part = SqlUtil.buildSqlForIn("pk_inventory", pks);
		StringBuffer sf = new StringBuffer();
		sf.append(" update ynt_goodsinvenrela y set dr = 1  ");
		sf.append("  Where pk_corp = ? and ");
		sf.append(part);
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		
		singleObjectBO.executeUpdate(sf.toString(), sp);
	}

}
