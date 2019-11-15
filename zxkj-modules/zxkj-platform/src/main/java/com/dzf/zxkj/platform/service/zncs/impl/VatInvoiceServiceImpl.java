package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.pjgl.VatInvoiceSetVO;
import com.dzf.zxkj.platform.service.zncs.IVatInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("gl_vatinvoicesetserv")
public class VatInvoiceServiceImpl implements IVatInvoiceService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Override
	public VatInvoiceSetVO[] queryByType(String pk_corp, String type) throws DZFWarpException {
		
		String wherePart = " nvl(dr,0) = 0 and style = ? and pk_corp = ? ";
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(type);
		sp.addParam(pk_corp);
		
		VatInvoiceSetVO[] vos = (VatInvoiceSetVO[]) singleObjectBO.queryByCondition(VatInvoiceSetVO.class, 
				wherePart, sp);
		
		return vos;
	}

	@Override
	public void updateVO(String pk_corp, VatInvoiceSetVO vo, String[] fields) throws DZFWarpException {
		
		if(StringUtil.isEmpty(vo.getPrimaryKey())){
			VatInvoiceSetVO setvo[] = queryByType(pk_corp, vo.getStyle());
			if(setvo!=null && setvo.length>0){
				singleObjectBO.deleteVOArray(setvo);
			}
			singleObjectBO.saveObject(pk_corp, vo);
		}else{
			VatInvoiceSetVO svo = (VatInvoiceSetVO)singleObjectBO.queryByPrimaryKey(VatInvoiceSetVO.class, vo.getPrimaryKey());
			
			VatInvoiceSetVO setvo[] = queryByType(pk_corp, svo.getStyle());
			for (int i = 0; i < setvo.length; i++) {
				if(!vo.getPrimaryKey().equals(setvo[i].getPrimaryKey())){
					singleObjectBO.deleteObject(setvo[i]);
				}
			}
			singleObjectBO.update(vo, fields);
		}
	}

}
