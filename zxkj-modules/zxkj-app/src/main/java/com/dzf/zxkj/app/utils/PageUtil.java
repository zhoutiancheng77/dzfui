package com.dzf.zxkj.app.utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.StringUtil;

/**
 * 分页工具
 * 
 * @author yinyx1
 *
 */
public class PageUtil {

	public static SuperVO[] paginationVOs(SuperVO vos[], HttpServletRequest requert) {
		Integer page = null;
		Integer size = null;
		try {
			page = new Integer(requert.getParameter("page"));
			size = new Integer(requert.getParameter("rows"));
		} catch (NumberFormatException e) {
			return vos;
		}
		page = page - 1;
		if (vos == null || vos.length == 0)
			return vos;

		List<SuperVO> voList = new ArrayList<SuperVO>();
		int start = page * size;
		int end = (page + 1) * size;
		for (int i = start; i < vos.length && i < end; i++) {
			voList.add(vos[i]);
		}
		return voList.toArray(new SuperVO[0]);
	}
	
	
	public static SuperVO[] paginationVOs(SuperVO vos[], Integer page ,Integer size) {
//		Integer page = null;
//		Integer size = null;
//		try {
//			page = new Integer(requert.getParameter("page"));
//			size = new Integer(requert.getParameter("rows"));
//		} catch (NumberFormatException e) {
//			return vos;
//		}
		page = page - 1;
		if (vos == null || vos.length == 0)
			return vos;

		List<SuperVO> voList = new ArrayList<SuperVO>();
		int start = page * size;
		int end = (page + 1) * size;
		for (int i = start; i < vos.length && i < end; i++) {
			voList.add(vos[i]);
		}
		return voList.toArray(new SuperVO[0]);
	}
	
	public static List paginationVOs(List list, Integer page ,Integer size) {
		if(size  == null || size.intValue() <=0){
			return list;
		}
		
		page = page - 1;
		if (list == null || list.size() == 0)
			return list;

		List voList = new ArrayList();
		int start = page * size;
		int end = (page + 1) * size;
		for (int i = start; i < list.size() && i < end; i++) {
			voList.add(list.get(i));
		}
		return voList;
	}

	/**
	 * 根据时间分页
	 * @param vos
	 * @param l_date
	 * @param size
	 * @param column   时间字段
	 * @param con_column  根据某个字段查询
	 * @return
	 */
	public static SuperVO[] paginationVOs(SuperVO vos[], Long l_date,String con_value ,Integer size, String column,String con_column) {
		if (size == null) {
			return vos;
		}
		if (vos == null || vos.length == 0)
			return vos;

		List<SuperVO> voList = new ArrayList<SuperVO>();
		long date = 0;
		int count = 0;
		String name = null;
		for (int i = 0; i < vos.length; i++) {
			date = (long) vos[i].getAttributeValue(column);
			if(!StringUtil.isEmpty(con_value)){
				if(StringUtil.isEmpty(con_column)){
					throw new BusinessException("模糊字段为空!");
				}
				name = (String) vos[i].getAttributeValue(con_column);
				if(StringUtil.isEmpty(name) || name.indexOf(con_value)<0){
					continue;
				}
			}
			if (count >= size) {
				break;
			}
			if ((l_date == null || l_date == 0) || (date < l_date)) {
				if(l_date == null || l_date == 0){
					l_date = (date+1);
				}
				voList.add(vos[i]);
				count++;
			}
		}

		return voList.toArray(new SuperVO[0]);
	}
}
