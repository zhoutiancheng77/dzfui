package com.dzf.zxkj.app.service.login.impl;

import com.dzf.zxkj.app.model.app.user.TempUserRegVO;
import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.login.IAppDemoCorpService;
import com.dzf.zxkj.app.service.login.IAppLoginCorpService;
import com.dzf.zxkj.app.service.pub.IAppPubservice;
import com.dzf.zxkj.app.utils.CryptUtil;
import com.dzf.zxkj.app.utils.PinyinUtil;
import com.dzf.zxkj.app.utils.SourceSysEnum;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageCommonPath;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;


@Service("democorpser")
@Slf4j
public class AppDemoCorpServiceImpl implements IAppDemoCorpService {

	@Autowired
	private IAppLoginCorpService user320service;

	@Autowired
	private IAppPubservice apppubservice;
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	/**
	 * 发送临时公司
	 * 
	 * @param bean
	 * @param user_code
	 * @param user_name
	 * @param userid
	 * @param corpvo
	 * @throws Exception
	 */
	@Override
	public void sendDemoCorp(LoginResponseBeanVO bean, String user_code, String user_name, String userid,
							 String vdevicemsg, CorpVO[] corpvo, String sourcesys, String pk_svorg) throws DZFWarpException {
		if (corpvo == null || corpvo.length == 0) {
			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg("您当前用户没公司信息!");
			return;
		}
		bean.setAccount(user_code);
		if (StringUtil.isEmpty(user_name)) {
			bean.setUser_name(user_code);
		} else {
			bean.setUser_name(user_name);
		}
		bean.setAccount_id(userid);
		bean.setIsys(IConstant.DEFAULT);
		bean.setCorpname(CodeUtils1.deCode(corpvo[0].getUnitname()));
		bean.setPk_corp(corpvo[0].getPk_corp());
		bean.setIsaccorp("N");
		bean.setIsautologin(IConstant.DEFAULT);
		bean.setUsergrade(IConstant.FIRDES);
		bean.setBdata("Y");
		bean.setBaccount("Y");
		bean.setBbillapply("Y");// 是否有开票申请权限
		bean.setIsdemo(IConstant.DEFAULT);
		bean.setRescode(IConstant.DEFAULT);
		SQLParameter sp = new SQLParameter();
		sp.addParam(corpvo[0].getPk_corp());
		CorpTaxVo[] corptaxvo = (CorpTaxVo[]) singleObjectBO.queryByCondition(CorpTaxVo.class, "nvl(dr,0)=0 and pk_corp = ?",sp);
		if(corptaxvo!=null && corptaxvo.length>0){
			bean.setSh(corptaxvo[0].getTaxcode());// 税号
		}
		bean.setKpdh(StringUtil.isEmpty(corpvo[0].getPhone1()) ? "" : CodeUtils1.deCode(corpvo[0].getPhone1()));// 开票电话
		bean.setKhzh(corpvo[0].getVbankcode());// 开户帐号
		bean.setKhh(corpvo[0].getVbankname());// 开户行
		bean.setBegdate(corpvo[0].getBegindate());
		bean.setPriid(IConstant.PRIID_COM);
		if ("小规模纳税人".equals(corpvo[0].getChargedeptname())) {
			bean.setChargedeptname("0");
		} else if ("一般纳税人".equals(corpvo[0].getChargedeptname())) {
			bean.setChargedeptname("1");
		} else {
			bean.setChargedeptname("");
		}

		String[] types = new String[] { ".jpg", ".png", ".jpeg", ".bmp", ".gif" };
		String imagename = null;
		String imagelogoname = null;
		String imagepermitname = null;
		File imagefile = null;
		File logoimagefile = null;
		File permitimagefile = null;
		for (String type : types) {
			// 文件名
			imagename = ImageCommonPath.getUserHeadPhotoPath(user_code, type);
			imagelogoname = ImageCommonPath.getLogoUnZipPath(PinyinUtil.getPinYin(bean.getCorpname()), type);
			imagepermitname = ImageCommonPath.getPermitUnZipPath(PinyinUtil.getPinYin(bean.getCorpname()), type);
			imagefile = new File(imagename);
			logoimagefile = new File(imagelogoname);
			permitimagefile = new File(imagepermitname);
			if (imagefile.exists()) {
				bean.setPhotopath(CryptUtil.getInstance().encryptAES(imagefile.getPath()));
			}
			if (logoimagefile.exists()) {
				bean.setLogopath(CryptUtil.getInstance().encryptAES(logoimagefile.getPath()));
			}
			if (permitimagefile.exists()) {
				bean.setPermitpath(CryptUtil.getInstance().encryptAES(permitimagefile.getPath()));
			}

		}

		if (SourceSysEnum.SOURCE_SYS_CST.getValue().equals(sourcesys)) {// 临时公司信息
			if (StringUtil.isEmpty(pk_svorg)) {
				List<TempUserRegVO> tempulist = apppubservice.getTempList(user_code);
				bean.setPk_svorg(new String[] { tempulist.get(0).getPk_svorg() });
			} else {
				bean.setPk_svorg(new String[] { pk_svorg });
			}
			if(bean.getPk_svorg() == null || bean.getPk_svorg().length == 0){
				bean.setIbindfwjg(IConstant.FIRDES);//暂未关联服务机构
			}else{
				bean.setIbindfwjg(IConstant.DEFAULT);//关联服务机构
			}
		}

		// 赋值demo公司信息
		user320service.putFatherCorp("", bean, sourcesys);

		bean.setResmsg("登录成功");
	}

}
