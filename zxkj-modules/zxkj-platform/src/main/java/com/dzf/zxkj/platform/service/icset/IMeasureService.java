package com.dzf.zxkj.platform.service.icset;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.icset.MeasureVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IMeasureService {

	MeasureVO queryByPrimaryKey(String primaryKey) throws DZFWarpException;

	void delete(MeasureVO vo) throws DZFWarpException;

	String deleteBatch(String[] ids, String pk_corp) throws DZFWarpException;

	List<MeasureVO> quyerByPkcorp(String pk_corp, String sort, String order) throws DZFWarpException;
	
	void updateVOArr(String pk_corp, String cuserid, List<MeasureVO> list) throws DZFWarpException;
	
	String saveImp(MultipartFile file, String pk_corp, String fileType, String userid) throws DZFWarpException;
	
	List<MeasureVO> query(String pk_corp) throws DZFWarpException;
	
	MeasureVO saveVO(MeasureVO vo) throws DZFWarpException;
	
	MeasureVO[] savenNewVOArr(String pk_corp, String cuserid, List<MeasureVO> list)throws DZFWarpException;

	MeasureVO save(MeasureVO data) throws DZFWarpException;
}
