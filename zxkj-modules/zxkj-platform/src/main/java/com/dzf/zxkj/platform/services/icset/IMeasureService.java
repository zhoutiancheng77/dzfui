package com.dzf.zxkj.platform.services.icset;


import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.icset.MeasureVO;

import java.io.File;
import java.util.List;

public interface IMeasureService {

	MeasureVO queryByPrimaryKey(String primaryKey) throws DZFWarpException;

	void delete(MeasureVO vo) throws DZFWarpException;
	
	public List<MeasureVO> quyerByPkcorp(String pk_corp, String sort, String order) throws DZFWarpException;
	
	public void updateVOArr(String pk_corp, String cuserid, String sort, String order, List<MeasureVO> list) throws DZFWarpException;
	
	public String saveImp(File file, String pk_corp, String fileType, String userid) throws DZFWarpException;
	
	public List<MeasureVO> query(String pk_corp) throws DZFWarpException;
	
	public MeasureVO saveVO(MeasureVO vo) throws DZFWarpException;
	
	public MeasureVO[] savenNewVOArr(String pk_corp, String cuserid, String sort, String order, List<MeasureVO> list)throws DZFWarpException;

	MeasureVO save(MeasureVO data) throws DZFWarpException;
}
