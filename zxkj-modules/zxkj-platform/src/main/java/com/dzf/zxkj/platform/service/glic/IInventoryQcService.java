package com.dzf.zxkj.platform.service.glic;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.glic.InventoryQcVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.io.File;
import java.util.List;

public interface IInventoryQcService {

	public List<InventoryQcVO> query(String pk_corp) throws DZFWarpException;
	
	//报表加载使用
	public DZFDate queryInventoryQcDate(String pk_corp) throws DZFWarpException;

	public InventoryQcVO save(String userid,String pk_corp,InventoryQcVO vo1) throws DZFWarpException;

	public void delete(InventoryQcVO[] vos) throws DZFWarpException;

	public void processSyncData(String userid, String pk_corp, String date)
			throws DZFWarpException;

	public void updateDate(String pk_corp, String date) throws DZFWarpException;

	public String processImportExcel(CorpVO corp, String user_id,
									 String fileType, File impFile, String date) throws DZFWarpException;
}