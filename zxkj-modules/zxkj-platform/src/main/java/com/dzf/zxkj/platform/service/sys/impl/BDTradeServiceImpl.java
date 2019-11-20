package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.tree.AccTreeCreateStrategy;
import com.dzf.zxkj.common.tree.BDTreeCreator;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.BDTradeVO;
import com.dzf.zxkj.platform.model.sys.ComboBoxVO;
import com.dzf.zxkj.platform.service.sys.IBDTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 行业
 * 
 */
@Service("sys_hyserv")
public class BDTradeServiceImpl implements IBDTradeService {
	
	private SingleObjectBO singleObjectBO = null;
	
	public static final String coderule = "2/1/1";

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}
	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO){
		this.singleObjectBO = singleObjectBO;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<BDTradeVO> queryTrade(BDTradeVO paramvo) throws DZFWarpException {
		int pageNo = paramvo.getPage();
		int pageSize = paramvo.getRows();
		String order = "tradecode";
		StringBuffer table = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		table.append("SELECT * \n") ;
		table.append("  FROM ynt_bd_trade \n") ; 
		table.append(" WHERE pk_corp = '000001'\n") ; 
		table.append("   AND nvl(dr, 0) = 0 \n");
		if(!StringUtil.isEmpty(paramvo.getTradename())){
			table.append("  AND ( tradecode like ? OR tradename like ? ) \n");
			spm.addParam("%"+paramvo.getTradename()+"%");
			spm.addParam("%"+paramvo.getTradename()+"%");
		}
		StringBuffer qrysql = new StringBuffer();
		qrysql.append(" select * from ( SELECT ROWNUM AS ROWNO, tt.* FROM ( ");
		qrysql.append(table);
		qrysql.append(" order by " + order + " asc) tt WHERE ROWNUM <= "
				+ pageNo * pageSize);
		qrysql.append(" ) WHERE ROWNO> " + (pageNo - 1) * pageSize + " ");
		return (List<BDTradeVO>)singleObjectBO.executeQuery(qrysql.toString(), spm, new BeanListProcessor(BDTradeVO.class));
	}
	
	@Override
	public Integer queryTotalRow(BDTradeVO paramvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		sql.append(" pk_corp = '000001' and nvl(dr,0) = 0 ");
		SQLParameter spm = new SQLParameter();
		if(!StringUtil.isEmpty(paramvo.getTradename())){
		    sql.append("  AND ( tradecode like ? OR tradename like ? ) \n");
            spm.addParam("%"+paramvo.getTradename()+"%");
            spm.addParam("%"+paramvo.getTradename()+"%");
        }
		BDTradeVO[] vos = (BDTradeVO[])singleObjectBO.queryByCondition(BDTradeVO.class, sql.toString(), spm);
		if(vos != null && vos.length > 0){
			return vos.length;
		}
		return 0;
	}
	@Override
	public BDTradeVO save(BDTradeVO vo) throws DZFWarpException {
		if(!StringUtil.isEmpty(vo.getPk_trade())){
			update(vo);
			return vo;
		}
		return (BDTradeVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
	}
	@Override
	public void delete(BDTradeVO vo) throws DZFWarpException {
		singleObjectBO.deleteObject(vo);
		
	}
	
	public void update(BDTradeVO vo) throws DZFWarpException {
		singleObjectBO.update(vo);
	}
	@Override
	public BDTradeVO queryByID(String id) throws DZFWarpException {
		BDTradeVO[] vos = (BDTradeVO[])singleObjectBO.queryByCondition(BDTradeVO.class, "pk_trade = '" + id + "'", null);
		if(vos == null || vos.length == 0)
			return null;
		return vos[0];
	}
	@Override
	public String existCheck(BDTradeVO vo) throws DZFWarpException {
		SQLParameter sp1 = new SQLParameter();
		//SQLParameter sp2 = new SQLParameter();
		String code = vo.getTradecode();
		String name = vo.getTradename();
		sp1.addParam(code);		
		sp1.addParam(name);
		String id = "";
		if(!StringUtil.isEmpty(vo.getPk_trade())){
			id = vo.getPk_trade();
		}
		if(StringUtil.isEmpty(id)==false){
			sp1.addParam(id);
			sp1.addParam(id);
		}
		String condition1 = " (tradecode = ? or tradename = ?) ";
		if(StringUtil.isEmpty(id)==false)
			condition1=condition1+" and (pk_trade<? or pk_trade>?) ";
		condition1=condition1+" and pk_corp = '000001' and nvl(dr,0) = 0 ";
		//String condition2 = " tradename = ? and pk_corp = '000001' and nvl(dr,0) = 0 ";
		List<BDTradeVO> vo1 = (List<BDTradeVO>) singleObjectBO.retrieveByClause(BDTradeVO.class, condition1, sp1);
		//List<BDTradeVO> vo2 = (List<BDTradeVO>) singleObjectBO.retrieveByClause(BDTradeVO.class, condition2, sp2);
		int len=vo1==null?0:vo1.size();
		if(len>0){
			if(vo1.get(0).getTradecode().equals(code))
				return "行业编号已存在";
			else return "行业名称已存在";
		}
		return null;
//		if((vo1 == null || vo1.size() == 0) && (vo2 == null || vo2.size() == 0)){
//			return null;
//		}else if(vo1 == null || vo1.size() == 0){
//			if (vo2.get(0).getPk_trade().equals(id))
//				return null;
//			else
//				return "行业名称已存在";
//		} else {
//			if (vo2 == null || vo2.size() == 0){
//				if (vo1.get(0).getPk_trade().equals(id))
//					return null;
//				else return "行业编号已存在";
//			} else {
//				if (vo1.get(0).getPk_trade().equals(id) && vo2.get(0).getPk_trade().equals(id))
//					return null;
//				else if (vo1.get(0).getPk_trade().equals(id))
//					return "行业名称已存在";
//				else return "行业编号已存在";
//			}
//		}
	}
    @Override
    public List<BDTradeVO> queryRef(BDTradeVO vo) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append(" pk_corp = ? and nvl(dr,0) = 0 ");
        sp.addParam(IDefaultValue.DefaultGroup);
        if(!StringUtil.isEmpty(vo.getTradecode())){
            sql.append(" and( tradecode like ?");
            sp.addParam(vo.getTradecode()+"%");
            sql.append(" or tradename like ? )");
            sp.addParam("%"+vo.getTradecode()+"%");
        }
        sql.append("  order by tradecode");
        BDTradeVO[] vos = (BDTradeVO[]) singleObjectBO.queryByCondition(BDTradeVO.class, sql.toString(), sp);
        if (vos == null || vos.length == 0) {
            return new ArrayList<BDTradeVO>();
        }
        
        // 构造树返回
        BDTradeVO ftvo = (BDTradeVO) BDTreeCreator.createTree(vos, new AccTreeCreateStrategy(coderule) {
            @Override
            public SuperVO<BDTradeVO> getRootVO() {
                return new BDTradeVO();
            }

            @Override
            public String getCodeValue(Object userObj) {
                return ((BDTradeVO) userObj).getTradecode();
            }
        });
        return Arrays.asList(ftvo.getChildren());
    }
    @Override
    public ArrayList<ComboBoxVO> queryComboBox() throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append("select pk_trade as id, tradename as name\n");
        sql.append(" from ynt_bd_trade");
        sql.append(" where pk_corp = ? and nvl(dr,0) = 0 ");
        sp.addParam(IDefaultValue.DefaultGroup);
        sql.append(" and tradecode like 'Z%'");
        sql.append(" order by tradecode");
        ArrayList<ComboBoxVO> list = (ArrayList<ComboBoxVO>) singleObjectBO.executeQuery(sql.toString(), sp,
                new BeanListProcessor(ComboBoxVO.class));
        return list;
    }
}
