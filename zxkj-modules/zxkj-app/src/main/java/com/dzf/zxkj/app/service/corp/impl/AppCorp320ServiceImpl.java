package com.dzf.zxkj.app.service.corp.impl;

import com.dzf.zxkj.app.model.app.user.AppUserVO;
import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.corp.IAppCorp320Service;
import com.dzf.zxkj.app.service.corp.IAppCorpPhoto;
import com.dzf.zxkj.app.service.corp.IAppCorpService;
import com.dzf.zxkj.app.service.pub.IAppPubservice;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.app.utils.CryptUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageCommonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;


/**
 * 获取公司信息
 * 
 * @author zhangj
 *
 */
@Slf4j
@Service("corp320service")
public class AppCorp320ServiceImpl extends AppCorpService implements IAppCorp320Service {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IAppPubservice apppubservice ;

	@Autowired
	@Qualifier("corpservice")
	private IAppCorpService iAppCorpService;
	
	@Autowired
	private IAppCorpPhoto corpPhotoservice;
	
	/**
	 * 根据公司主键获取公司对应的，省市区，信息
	 */
	@Override
	public LoginResponseBeanVO getCorpMsg(UserBeanVO userBean) throws DZFWarpException {
		LoginResponseBeanVO bean = new LoginResponseBeanVO();
		if (StringUtil.isEmpty(userBean.getPk_corp()) && StringUtil.isEmpty(userBean.getPk_tempcorp())) {
			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg("公司信息为空!");
			return bean;
		}
		SQLParameter sp = new SQLParameter();
		StringBuffer loginsql = new StringBuffer();
		if (!AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())) {// 签约公司信息
			getLoginCorpSQL(sp, userBean.getPk_corp(), loginsql);
		} else if (!StringUtil.isEmpty(userBean.getPk_tempcorp())) {// 没签约公司信息
			getLoginTempCorpSQL(sp, userBean.getPk_tempcorp(), loginsql);
		}
		try {
			ArrayList<AppUserVO> alres = (ArrayList<AppUserVO>) singleObjectBO.executeQuery(loginsql.toString(), sp, new BeanListProcessor(AppUserVO.class));
			if (alres != null && alres.size() > 0) {
				AppUserVO uservo = alres.get(0);
				bean.setPk_temp_corp(uservo.getPk_tempcorp());
				bean.setPk_corp(StringUtil.isEmpty(uservo.getPk_corp()) ? Common.tempidcreate : uservo.getPk_corp());
				bean.setCorpaddr(uservo.getApp_corpadd() == null ? "" : uservo.getApp_corpadd());
				// 处理公司信息
				handlerCorpMsg(uservo, bean);
				getCorpPhoto(userBean, bean);//获取公司信息
				ResponseBaseBeanVO rbbvo = iAppCorpService.qrykpmsg(userBean.getPk_corp(), userBean.getPk_tempcorp(), userBean.getAccount_id(),userBean.getAccount());
				bean.setKhh(rbbvo.getKhh());
				bean.setKhzh(rbbvo.getKhzh());
				bean.setCorpaddr(rbbvo.getCorpaddr());
				bean.setRescode(IConstant.DEFAULT);
				bean.setResmsg("获取信息成功!");
			}else{
				bean.setRescode(IConstant.FIRDES);
				bean.setResmsg("企业信息不存在!");
			}
		} catch (Exception e) {
			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg("查询用户异常:" + e.getMessage());
			log.error("查询用户异常:" + e.getMessage(), e);
		}
		return bean;
	}
	
	/**
	 * 获取用户和公司信息
	 * @param userBean
	 * @param bean
	 */
	private void getCorpPhoto(UserBeanVO userBean, LoginResponseBeanVO bean) {
		String[] types = new String[] { ".jpg", ".png", ".jpeg", ".bmp", ".gif" };
		String imagename = null;
		File imagefile = null;
		for (String type : types) {
			// 文件名
			imagename = ImageCommonPath.getUserHeadPhotoPath(userBean.getAccount(), type);
			imagefile = new File(imagename);
			if (imagefile.exists()) {
				bean.setPhotopath(CryptUtil.getInstance().encryptAES(imagefile.getPath()));
			}
		}
		corpPhotoservice.getCorpPhoto(userBean, bean);
	}

	/**
	 * 处理公司信息
	 * 
	 * @param uservo
	 * @param bean
	 */
	private void handlerCorpMsg(AppUserVO uservo, LoginResponseBeanVO bean) {
		bean.setVsoccrecode(uservo.getVsoccrecode());
		bean.setIndustry(uservo.getIndustry());
		bean.setChargedeptname(uservo.getChargedeptname());
		if("小规模纳税人".equals(uservo.getChargedeptname())){
			bean.setChargedept_num("0");
		}else if("一般纳税人".equals(uservo.getChargedeptname())){
			bean.setChargedept_num("1");
		}
		bean.setLegalbodycode(uservo.getLegalbodycode());
		if (!StringUtil.isEmpty(uservo.getPk_corp()) && !Common.tempidcreate.equals(uservo.getPk_corp())) {
			// 需要处理，省市区，行业，法人，行业
			if (!StringUtil.isEmpty(uservo.getLegalbodycode())) {
				bean.setLegalbodycode(CodeUtils1.deCode(uservo.getLegalbodycode()));
			}
			Map<String, String> areamap = apppubservice.queryArea("0");

			bean.setVprovince(areamap.get(uservo.getVprovince()));
			bean.setVcity(areamap.get(uservo.getVcity()));
			bean.setVarea(areamap.get(uservo.getVarea()));
		} else if (!StringUtil.isEmpty(uservo.getPk_tempcorp())) {
			bean.setVprovince(uservo.getVprovince());
			bean.setVcity(uservo.getVcity());
			bean.setVarea(uservo.getVarea());
		}
	}

	/**
	 * 获取登录没签约的公司
	 * 
	 * @param userBean
	 * @param sp
	 * @param pk_corp
	 * @param loginsql
	 */
	private void getLoginCorpSQL(SQLParameter sp, String pk_corp, StringBuffer loginsql) {
		// 新用户
		loginsql.append("   select  c.postaddr  as  app_corpadd ,c.pk_corp , ");
		loginsql.append("    c.vsoccrecode, c.legalbodycode,ynt_bd_trade.tradename as  industry,   "); // 公司信息
		loginsql.append("    c.chargedeptname,c.vprovince,c.vcity,c.varea "); 
		loginsql.append("   from  bd_corp c   ");
		loginsql.append("   left join  ynt_bd_trade on ynt_bd_trade.pk_trade= c.industry ");
		loginsql.append("   where 1=1  ");
		sp.clearParams();
		if (!StringUtil.isEmpty(pk_corp)) {
			loginsql.append("   and c.pk_corp = ? ");
			sp.addParam(pk_corp);
		}
	}

	/**
	 * 获取已经签约的公司信息
	 * 
	 * @param userBean
	 * @param sp
	 * @param pk_corp
	 * @param loginsql
	 */
	private void getLoginTempCorpSQL(SQLParameter sp, String pk_tempcorp, StringBuffer loginsql) {
		// 新用户
		loginsql.append("   select   temp.vsoccrecode,temp.legalbodycode,ynt_bd_trade.tradename as  industry,");
		loginsql.append("   temp.pk_temp_corp as pk_tempcorp,  ");
		loginsql.append("   temp.chargedeptname,temp.vprovince,temp.vcity,temp.varea ");
		loginsql.append("   from  app_temp_corp temp    ");
		loginsql.append("   left join  ynt_bd_trade on ynt_bd_trade.pk_trade= temp.industry ");
		loginsql.append("   where 1=1  ");
		sp.clearParams();
		if (!StringUtil.isEmpty(pk_tempcorp)) {
			loginsql.append("   and temp.pk_temp_corp = ? ");
			sp.addParam(pk_tempcorp);
		}
	}

}
