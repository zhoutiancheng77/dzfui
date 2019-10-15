package com.dzf.zxkj.platform.service.image.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * zpm
 *
 */
public class ImageBeanConvert {
	
	private SingleObjectBO singleObjectBO;
	
	public ImageBeanConvert(SingleObjectBO singleObjectBO){
		this.singleObjectBO = singleObjectBO;
	}

	public ImageQueryBean[] fromVO(ImageGroupVO[] aggVOs) {
		if (aggVOs == null || aggVOs.length == 0)
			return null;
		HashMap<String, UserVO> userMap = new HashMap<String, UserVO>();
		HashMap<String, CorpVO> corpMap = new HashMap<String, CorpVO>();
		UserVO userVO = null;
		CorpVO corpVO = null;
		ArrayList<ImageQueryBean> beans = new ArrayList<ImageQueryBean>();
		for (ImageGroupVO aggVO : aggVOs) {
			ImageLibraryVO[] imageVOs = (ImageLibraryVO[]) aggVO.getChildren();
			if (imageVOs == null || imageVOs.length == 0)
				continue;

			ImageGroupVO groupVO = aggVO;
			for (int i = 0; i < imageVOs.length; i++) {
				String pk_corperator = imageVOs[i].getCoperatorid();
				String pk_corp = imageVOs[i].getPk_corp();
				if (!userMap.containsKey(pk_corperator)) {
					userVO = (UserVO)singleObjectBO.queryVOByID(pk_corperator, UserVO.class);
					if (userVO != null)
						userMap.put(pk_corperator, userVO);
				}
				if (!corpMap.containsKey(pk_corp)) {
					corpVO = (CorpVO) singleObjectBO.queryByPrimaryKey(
							CorpVO.class, pk_corp);
					if (corpVO != null)
						corpMap.put(pk_corp, corpVO);
				}

				userVO = userMap.containsKey(pk_corperator) ? userMap
						.get(pk_corperator) : null;
				corpVO = corpMap.containsKey(pk_corp) ? corpMap.get(pk_corp)
						: null;

				ImageQueryBean bean = new ImageQueryBean();
				bean.setGroupKey(groupVO.getPrimaryKey());
				bean.setGroupCode(groupVO.getGroupcode());
				bean.setImageKey(imageVOs[i].getPrimaryKey());
				bean.setCreatedBy(userVO == null ? "" : userVO.getUser_name());
				bean.setCreatedOn(imageVOs[i].getDoperatedate().toString());
				bean.setImageName(imageVOs[i].getImgname());
				bean.setImagePath(imageVOs[i].getImgpath());
				bean.setPk_corp(imageVOs[i].getPk_corp());
				bean.setCorpName(corpVO==null?"":corpVO.getUnitname());
				bean.setTs(imageVOs[i].getTs());
				bean.setCvoucherdate(groupVO.getCvoucherdate() == null ? null : groupVO.getCvoucherdate().toString());
				bean.setImageState(groupVO.getIstate() == null ? PhotoState.state2 : groupVO.getIstate());//状态2为未知状态
				beans.add(bean);
			}
		}
		return beans.toArray(new ImageQueryBean[beans.size()]);
	}

	//WJX
	public ImgGroupRsBean[] fromImgHisVO(ImageGroupVO[] aggVOs) {
		if (aggVOs == null || aggVOs.length == 0)
			return null;
		HashMap<String, UserVO> userMap = new HashMap<String, UserVO>();
		HashMap<String, CorpVO> corpMap = new HashMap<String, CorpVO>();
		UserVO userVO = null;
		CorpVO corpVO = null;
		
		List<ImgGroupRsBean> rlist = new ArrayList<ImgGroupRsBean>();
		String[] sorfields = new String[]{"imgname"};
		for (ImageGroupVO aggVO : aggVOs) {
			ImageLibraryVO[] imageVOs = (ImageLibraryVO[]) aggVO.getChildren();
			VOUtil.ascSort(imageVOs, sorfields);//按照名称排序
			
			if (imageVOs == null || imageVOs.length == 0)
				continue;

			ImgGroupRsBean bean = new ImgGroupRsBean();//创建组
			ImageGroupVO groupVO = aggVO;
			bean.setGroupKey(groupVO.getPrimaryKey());
			bean.setGroupCode(groupVO.getGroupcode());
			bean.setPk_corp(groupVO.getPk_corp());
			bean.setGroupsession(groupVO.getSessionflag());
			bean.setMemo(groupVO.getMemo()==null?"":groupVO.getMemo());
			bean.setPaymethod(groupVO.getSettlemode()==null?"":groupVO.getSettlemode());
			bean.setMny(groupVO.getMny()==null?"":groupVO.getMny().toString());
			//图片状态设置
			int imgState = PhotoState.state2;//未知状态
			if(groupVO.getIstate() != null){
				imgState = groupVO.getIstate();
			}
			bean.setGroupState(imgState + "");//设置图片状态
			
			ImgInfoRsBean[] ifbeans = new ImgInfoRsBean[imageVOs.length];
			for (int i = 0; i < imageVOs.length; i++) {//添加组内图片信息
				String pk_corperator = imageVOs[i].getCoperatorid();
				String pk_corp = imageVOs[i].getPk_corp();
				if (!userMap.containsKey(pk_corperator)) {
					userVO = (UserVO)singleObjectBO.queryVOByID(pk_corperator, UserVO.class);
					if (userVO != null)
						userMap.put(pk_corperator, userVO);
				}
				if (!corpMap.containsKey(pk_corp)) {
					corpVO = (CorpVO) singleObjectBO.queryByPrimaryKey(
							CorpVO.class, pk_corp);
					if (corpVO != null)
						corpMap.put(pk_corp, corpVO);
				}

				userVO = userMap.containsKey(pk_corperator) ? userMap
						.get(pk_corperator) : null;
				corpVO = corpMap.containsKey(pk_corp) ? corpMap.get(pk_corp)
						: null;

				ifbeans[i] = new ImgInfoRsBean();
				ifbeans[i] .setGroupKey(groupVO.getPrimaryKey());
				ifbeans[i] .setImageKey(imageVOs[i].getPrimaryKey());
				ifbeans[i] .setCreatedBy(userVO == null ? "" : userVO.getUser_name());
				ifbeans[i] .setCreatedOn(imageVOs[i].getDoperatedate().toString());
				ifbeans[i] .setImageName(imageVOs[i].getImgname());
				ifbeans[i] .setImagePath(imageVOs[i].getImgpath());
				ifbeans[i].setImageState(imageVOs[i].getIsback() == DZFBoolean.TRUE ? "Y" : "N");
			}
			bean.setChildren(ifbeans);
			rlist.add(bean);
		}
		return rlist.toArray(new ImgGroupRsBean[rlist.size()]);
	}

}
