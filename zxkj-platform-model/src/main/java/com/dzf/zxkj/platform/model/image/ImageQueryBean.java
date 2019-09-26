package com.dzf.zxkj.platform.model.image;

//import nc.vo.pub.portlet.ImageVO;

import com.dzf.zxkj.common.lang.DZFDateTime;

import java.io.Serializable;

public class ImageQueryBean  implements Serializable {

	private static final long serialVersionUID = 8152313851350667140L;
	
	private String groupKey;
	private String groupCode;
	private String imageKey;
	private String createdBy;
	private String createdOn;
	private String imageName;
	private String imagePath;
	private String pk_corp;
	private String corpName;
	private String cvoucherdate;
	private DZFDateTime ts;
	private int imageState;
	
	public int getImageState() {
		return imageState;
	}
	public void setImageState(int imageState) {
		this.imageState = imageState;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getCorpName() {
		return corpName;
	}
	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}
	public DZFDateTime getTs() {
		return ts;
	}
	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	public String getGroupKey() {
		return groupKey;
	}
	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}
	public String getImageKey() {
		return imageKey;
	}
	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getCvoucherdate() {
		return cvoucherdate;
	}
	public void setCvoucherdate(String cvoucherdate) {
		this.cvoucherdate = cvoucherdate;
	}
	
	
//	public static ImageQueryBean[] fromVO(ImageGroupVO[] aggVOs) throws BusinessException{		
//		if(aggVOs == null || aggVOs.length == 0) return null;
//		SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
//		IUserService userQuery = (IUserService) SpringUtils.getBean("");
//		HashMap<String, UserVO> userMap = new HashMap<String, UserVO>();
//		HashMap<String, CorpVO> corpMap = new HashMap<String, CorpVO>();
//		UserVO userVO = null;
//		CorpVO corpVO = null;
//		ArrayList<ImageQueryBean> beans = new ArrayList<ImageQueryBean>();
//		for(ImageGroupVO aggVO: aggVOs){
//			ImageLibraryVO[] imageVOs = (ImageLibraryVO[]) aggVO.getChildren();
//			if(imageVOs == null || imageVOs.length == 0) continue;
//			
//			ImageGroupVO groupVO = aggVO;
//			for(int i =0 ; i<imageVOs.length; i++){
//				String pk_corperator = imageVOs[i].getCoperatorid();
//				String pk_corp = imageVOs[i].getPk_corp();
//				if(!userMap.containsKey(pk_corperator)){
//					userVO = userQuery.queryById(pk_corperator);
//					if(userVO != null)
//						userMap.put(pk_corperator, userVO);
//				}
//				if(!corpMap.containsKey(pk_corp)){
//					corpVO = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
//					if(corpVO != null)
//						corpMap.put(pk_corp, corpVO);
//				}
//				
//				userVO = userMap.containsKey(pk_corperator)?userMap.get(pk_corperator):null;
//				corpVO = corpMap.containsKey(pk_corp)?corpMap.get(pk_corp):null;
//				
//				ImageQueryBean bean = new ImageQueryBean();
//				bean.setGroupKey(groupVO.getPrimaryKey());
//				bean.setGroupCode(groupVO.getGroupcode());
//				bean.setImageKey(imageVOs[i].getPrimaryKey());
//				bean.setCreatedBy(userVO == null?"":userVO.getUser_name());
//				bean.setCreatedOn(imageVOs[i].getDoperatedate().toString());
//				bean.setImageName(imageVOs[i].getImgname());
//				bean.setImagePath(imageVOs[i].getImgpath());
//				bean.setPk_corp(imageVOs[i].getPk_corp());
//				bean.setCorpName(corpVO.getUnitname());
//				bean.setTs(imageVOs[i].getTs());
//				beans.add(bean);
//			}
//		}
//		return beans.toArray(new ImageQueryBean[beans.size()]);
//	}
}
