package com.dzf.zxkj.platform.services.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.JtsjVO;

import java.util.List;

public interface IJtsjTemService {

	public List<JtsjVO> queryByKmmethod(String prjid, String pk_corp, boolean isgroup) throws DZFWarpException;

	public void save(JtsjVO vo, String pk_corp, String loginuser) throws DZFWarpException;

	public void delete(JtsjVO vo) throws DZFWarpException;

	public JtsjVO queryById(String pk)throws DZFWarpException ;

	public String getCpidFromTd(String id, String loginpk, String loginuser) throws DZFWarpException;
}
