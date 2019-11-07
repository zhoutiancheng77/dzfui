package com.dzf.zxkj.platform.service.icbill.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.icset.IctradeinVO;
import com.dzf.zxkj.platform.service.icbill.ITradeinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service("ic_tradeinserv")
public class TradeinServiceImpl implements ITradeinService {

	private SingleObjectBO singleObjectBO = null;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	@Override
	public List<IctradeinVO> query(QueryParamVO paramvo ) throws DZFWarpException {
		StringBuffer sb = buidSql();
		SQLParameter sp=new SQLParameter();
		sp.addParam(paramvo.getPk_corp());
		sb.append(" where nvl(ynt_ictradein.dr,0)=0 ");
		sb.append(" and ynt_ictradein.pk_corp=? ");
		if(paramvo.getBegindate1() != null && paramvo.getEnddate() != null){
			if(paramvo.getBegindate1().equals(paramvo.getEnddate())){
				sp.addParam(paramvo.getBegindate1());
				sb.append(" and ynt_ictradein.dbilldate = ? ");
			}else{
				sp.addParam(paramvo.getBegindate1());
				sp.addParam(paramvo.getEnddate());
				sb.append(" and (ynt_ictradein.dbilldate >=? and ynt_ictradein.dbilldate <=?)");
			}
		}
		if(StringUtil.isEmptyWithTrim(paramvo.getPk_inventory())==false){
			sp.addParam(paramvo.getPk_inventory());
			sb.append(" and ynt_ictradein.pk_inventory = ?");
		}
//		sb.append(" order by  ynt_ictradein.dbilldate ");
		
		List<IctradeinVO> listVO = (List<IctradeinVO>) singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(IctradeinVO.class));
		//排序
		Collections.sort(listVO, new Comparator<IctradeinVO>() {
			@Override
			public int compare(IctradeinVO o1, IctradeinVO o2) {
				int i = o2.getDbilldate().compareTo(o1.getDbilldate());
				return i;
			}
		});
		return listVO;
	}

	@Override
	public IctradeinVO queryById(String id) throws DZFWarpException {
		IctradeinVO vo = (IctradeinVO) singleObjectBO.queryVOByID(id,
				IctradeinVO.class);
		return vo;
	}
	
	private StringBuffer buidSql(){
		StringBuffer sb = new StringBuffer();
		sb.append(" select ");
		String[] joinFields = getJoinFieldItems();
		for(String filed : joinFields){
			sb.append(filed)
				.append(",");
		}
		sb.append(" ynt_ictradein.*,ynt_cpaccount.accountname kmmc ");
		sb.append(" from ynt_ictradein ynt_ictradein ");
		sb.append(" left join ynt_inventory ynt_inventory on ynt_inventory.pk_inventory = ynt_ictradein.pk_inventory");
		sb.append(" left join ynt_measure ynt_measure on ynt_measure.pk_measure = ynt_inventory.pk_measure");
//		sb.append(" left join ynt_tzpz_b ynt_tzpz_b on ynt_tzpz_b.pk_tzpz_b = ynt_ictradein.pk_voucher_b");
//		sb.append(" left join ynt_tzpz_h ynt_tzpz_h on ynt_tzpz_h.pk_tzpz_h = ynt_tzpz_b.pk_tzpz_h");
		sb.append(" left join ynt_cpaccount ynt_cpaccount on ynt_inventory.pk_subject = ynt_cpaccount.pk_corp_account");
		
		return sb;
	}
	
	private String[] getJoinFieldItems(){
		String[] joinFields = new String[]{
				"ynt_inventory.name as Invname",
				"ynt_inventory.invspec as Invspec",
				"ynt_inventory.invtype as Invtype",
				"ynt_measure.name as Measure"
		};
		return joinFields;
	}
}
