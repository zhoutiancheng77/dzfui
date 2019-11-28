package com.dzf.zxkj.platform.service.icset;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.icset.InvclassifyVO;
import com.dzf.zxkj.platform.service.common.IBgPubService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IInvclassifyService extends IBgPubService {

	 InvclassifyVO save(InvclassifyVO vo)throws DZFWarpException;

	 List<InvclassifyVO> query(String pk_corp)throws DZFWarpException;

	 void delete(InvclassifyVO vo)throws DZFWarpException;

	 String deleteBatch(String[] ids, String pk_corp) throws DZFWarpException;

	 InvclassifyVO queryByPrimaryKey(String primaryKey) throws DZFWarpException;

	 String saveImp(MultipartFile file, String pk_corp, String fileType) throws DZFWarpException;

}
