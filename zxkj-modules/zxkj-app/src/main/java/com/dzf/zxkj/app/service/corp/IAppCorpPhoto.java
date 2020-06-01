package com.dzf.zxkj.app.service.corp;

import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.RegisterRespBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;

import java.io.InputStream;


/**
 * 公司相关证照管理
 * @author zhangj
 *
 */
public interface IAppCorpPhoto {

	/**
	 * 上传公司图片
	 * @param userBean
	 * @param bean
	 * @param zzlx
	 * @return
	 * @throws DZFWarpException
	 */
	public String upCorpPhoto(UserBeanVO userBean, RegisterRespBeanVO bean, Integer zzlx ) throws DZFWarpException;
	
	/**
	 * 保存图片信息
	 * @param inputStream
	 * @param imagename
	 * @throws DZFWarpException
	 */
	public void saveCorpFilemsg(InputStream inputStream, String imagename) throws DZFWarpException;
	
	
	
	public String getCorpPhoto(UserBeanVO userBean, LoginResponseBeanVO bean) throws DZFWarpException;
	
	/**
	 * 生成公司证照(单个)
	 * @param pk_corp
	 * @param path
	 * @param unitcode
	 * @throws DZFWarpException
	 */
	public void saveCorpDoc(String pk_corp,String pk_tempcorp,Integer zzlx,String usercode,String phototype) throws DZFWarpException;
	
	
	/**
	 * 公司生成多张证照信息(多个)
	 * @param pk_corp
	 * @param pk_tempcorp
	 * @param usercode
	 * @throws DZFWarpException
	 */
	public void saveCorpDocs(String pk_corp,String pk_tempcorp,String usercode) throws DZFWarpException;
}
