package com.dzf.zxkj.platform.services.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.tree.AccTreeCreateStrategyByID;
import com.dzf.zxkj.common.tree.BDTreeCreator;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.platform.model.sys.ComboBoxVO;
import com.dzf.zxkj.platform.model.sys.ResponAreaVO;
import com.dzf.zxkj.platform.model.sys.YntArea;
import com.dzf.zxkj.platform.services.sys.IAreaSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("areaService")
public class AreaSearchServiceImpl implements IAreaSearch {
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public YntArea query() throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		String where = " pk_corp = ?  and nvl(dr,0) = 0  ";
		YntArea[] vos = (YntArea[])singleObjectBO.queryByCondition(YntArea.class, where, sp);
		YntArea vo = (YntArea) BDTreeCreator.createTree(
				vos, new AccTreeCreateStrategyByID(){
					@Override
					public SuperVO getRootVO() {
						return new YntArea();
					}
					@Override
					public Object getNodeId(Object obj) {
						YntArea vo = (YntArea)obj;
						return vo.getRegion_id();
					}
					@Override
					public Object getParentNodeId(Object obj) {
						YntArea vo = (YntArea)obj;
						return vo.getParenter_id();
					}
				});
		return vo;
	}

	@Override
	public YntArea queryOpenArea() throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		String where = " pk_corp = ?  and nvl(dr,0) = 0 and nvl(iservice,'N')='Y' ";
		YntArea[] vos = (YntArea[])singleObjectBO.queryByCondition(YntArea.class, where, sp);
		YntArea vo = (YntArea) BDTreeCreator.createTree(
				vos, new AccTreeCreateStrategyByID(){
					@Override
					public SuperVO getRootVO() {
						return new YntArea();
					}
					@Override
					public Object getNodeId(Object obj) {
						YntArea vo = (YntArea)obj;
						return vo.getRegion_id();
					}
					@Override
					public Object getParentNodeId(Object obj) {
						YntArea vo = (YntArea)obj;
						return vo.getParenter_id();
					}
				});
		return vo;
	}
	
	@Override
	public List queryArea(String parenter_id) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select region_id, region_name ");
		sql.append("  from ynt_area ");
		sql.append(" where nvl(dr, 0) = 0 ");
		sql.append("   and parenter_id = ? ");
		sql.append("   order by region_id asc ");
		sp.addParam(Integer.parseInt(parenter_id));
		ArrayList list = (ArrayList) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(ResponAreaVO.class));
		return list;
	}
	
	@Override
	public List queryBsArea() throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append(" select region_id, region_name ");
		sql.append("  from ynt_area ");
		sql.append(" where nvl(dr, 0) = 0 ");
		sql.append("   and isbaoshui = ? ");
		sql.append("   order by region_code asc ");
		sp.addParam("Y");
		ArrayList list = (ArrayList) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(ResponAreaVO.class));
		return list;
	}
	
	
	
	@Override
	public List queryWebArea(String parenter_id) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select region_id, region_name ");
		sql.append("  from ynt_area ");
		sql.append(" where nvl(dr, 0) = 0 ");
		sql.append("   and parenter_id = ? and iservice='Y' ");
		sp.addParam(Integer.parseInt(parenter_id));
		ArrayList list = (ArrayList) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(ResponAreaVO.class));
		return list;
	}

    @Override
    public ArrayList<ComboBoxVO> queryComboxArea(String parenter_id) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append("select region_id as id, region_name as name ");
        sql.append("  from ynt_area ");
        sql.append(" where nvl(dr, 0) = 0 ");
        sql.append("   and parenter_id = ? ");
        sql.append("   order by region_id asc ");
        sp.addParam(Integer.parseInt(parenter_id));
        ArrayList<ComboBoxVO> list = (ArrayList<ComboBoxVO>) singleObjectBO.executeQuery(sql.toString(), sp,
                new BeanListProcessor(ComboBoxVO.class));
        return list;
    }

    @Override
    public ArrayList<ComboBoxVO> queryComArea(String parenter_id) throws DZFWarpException {

        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append("select region_id as id, region_name as name ");
        sql.append("  from ynt_area ");
        sql.append(" where nvl(dr, 0) = 0 ");
        sql.append("   and parenter_id = ? ");
        sql.append("   order by region_order,region_id asc ");
        sp.addParam(Integer.parseInt(parenter_id));
        ArrayList<ComboBoxVO> list = (ArrayList<ComboBoxVO>) singleObjectBO.executeQuery(sql.toString(), sp,
                new BeanListProcessor(ComboBoxVO.class));
        return list;
    
    }

}
