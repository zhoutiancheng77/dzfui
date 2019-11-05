package com.dzf.zxkj.platform.service.qcset.impl;

import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.platform.model.bdset.BdtradecashflowVO;
import com.dzf.zxkj.platform.model.qcset.YntXjllqcyePageVO;
import com.dzf.zxkj.platform.service.common.impl.BgPubServiceImpl;
import com.dzf.zxkj.platform.service.qcset.IQcxjlyService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("gl_qcxjlyserv")
public class QcxjlyServiceImpl extends BgPubServiceImpl implements IQcxjlyService {
	@Override
	public<T> List<T> completinfo(List<SuperVO>  rs, String pk_corp){
		Map<String, BdtradecashflowVO> prjmap = new HashMap<String, BdtradecashflowVO>();
		prjmap =queryMap(BdtradecashflowVO.class, IGlobalConstants.DefaultGroup);
		BdtradecashflowVO prjvo;
		for(int i=0;i<rs.size();i++){
			prjvo = prjmap.get(rs.get(i).getAttributeValue("pk_project"));
			if(prjvo==null)
				continue;
			rs.get(i).setAttributeValue("projectname", prjvo.getItemname());
//			rs.get(i).setAttributeValue("measurename", ivo.getMeasurename());
//			rs.get(i).setAttributeValue("invspec", ivo.getInvspec());
//			rs.get(i).setAttributeValue("invtype", ivo.getInvtype());
		}
		return (List<T>)rs;
	}

	@Override
	public DZFBoolean checkBeforeSaveNew(SuperVO vo)throws DZFWarpException {
		if(isExist((YntXjllqcyePageVO)vo).booleanValue()){
			return DZFBoolean.TRUE;
		}else{
			throw  new BusinessException("该项目已存在现金流量项目期初记录!");
//			return  DZFBoolean.FALSE;
		}
	}


	@Override
	public DZFBoolean checkBeforeUpdata(SuperVO vo)throws DZFWarpException  {

		YntXjllqcyePageVO oldvo = (YntXjllqcyePageVO)getSingleObjectBO().queryVOByID(vo.getPrimaryKey(), YntXjllqcyePageVO.class);
		if(oldvo==null){
			throw  new BusinessException("非法数据，请刷新后重新修改");
//			return  DZFBoolean.FALSE;
		}
		if(isExist((YntXjllqcyePageVO)vo).booleanValue()){
			return DZFBoolean.TRUE;
		}else{
			throw new BusinessException("该项目已存在现金流量项目期初记录!");
//			return  DZFBoolean.FALSE;
		}
	}

	public YntXjllqcyePageVO queryByPrimaryKey(String PrimaryKey){
		SuperVO msvo = getSingleObjectBO().queryByPrimaryKey(YntXjllqcyePageVO.class, PrimaryKey);
		return (YntXjllqcyePageVO) msvo;
	}

	private DZFBoolean isExist(YntXjllqcyePageVO vo){
		
		String sql = new String("SELECT 1 FROM YNT_XJLLQCYE WHERE (PK_PROJECT = ? )AND PK_CORP = ? and nvl(dr,0)=0 ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_project());
		sp.addParam(vo.getPk_corp());
		
		if(vo.getPrimaryKey()!=null&&vo.getPrimaryKey().length()>0){
			sql=sql+" AND PK_XJLLQCYE !=?";
			sp.addParam(vo.getPrimaryKey());
		}
		
		Object i = getSingleObjectBO().executeQuery(sql, sp, new ColumnProcessor());
		
		if(i!=null){
			return DZFBoolean.FALSE;
		}
		return DZFBoolean.TRUE;
	}
	
}
