package com.dzf.zxkj.app.service.corp.impl;

import java.util.ArrayList;
import java.util.List;

import com.dzf.zxkj.app.model.app.corp.ScanCorpInfoVO;
import com.dzf.zxkj.app.model.app.corp.TempCorpVO;
import com.dzf.zxkj.app.model.app.corp.UserToCorp;
import com.dzf.zxkj.app.model.resp.bean.ContainCorpVo;
import com.dzf.zxkj.app.model.resp.bean.RegisterRespBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.corp.IAppCorpService;
import com.dzf.zxkj.app.service.login.IAppLoginCorpService;
import com.dzf.zxkj.app.service.pub.IAppPubservice;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.app.utils.BeanUtils;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.AccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 322的公司关联(通过激活码关联)
 * 
 * @author zhangj
 *
 */
@Slf4j
@Service("corp322service")
public class AppCorp322ServiceImpl extends AppCorpService implements IAppCorpService {
	
	@Autowired
	private IAppCorpService corpservice ;
	
	@Autowired
	private IAppPubservice apppubservice;
	
	@Autowired
	private IAppLoginCorpService user320service;
	
//	@Autowired
//	private ISMSService msservice;



	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public ResponseBaseBeanVO updateuserAddCorp(UserBeanVO userBean) throws DZFWarpException {
		// 当前公司名字是否存在
		RegisterRespBeanVO bean = new RegisterRespBeanVO();
		if (StringUtil.isEmpty(userBean.getCorpname())) {
			throw new BusinessException("添加公司失败：公司信息不能为空!");
		}
		if (StringUtil.isEmpty(userBean.getAccount_id())) {
			throw new BusinessException("添加公司失败：您帐号信息为空！");
		}
		//判断是否关联过
		String linktips = corpservice.isLinkCorp(userBean);
		if (!StringUtil.isEmpty(linktips)) {
			throw new BusinessException(linktips);
		}
		// 获取临时公司信息
		String[] pk_temp_corps = corpservice.getPk_temp_corpByName(userBean);
		String pk_temp_corp = null;
		if(pk_temp_corps!=null && pk_temp_corps.length >0){
			pk_temp_corp = pk_temp_corps[0];
		}
		
		//获取签约公司信息
		CorpVO[] cps = corpservice.getPk_corpVoByName(userBean);
		userBean.setPk_tempcorp(pk_temp_corp);
		String pk_corp = userBean.getPk_corp();
		
		if (!AppCheckValidUtils.isEmptyCorp(pk_corp)) {// 公司是否为空
			bean = isSign(userBean, pk_corp);
		} else {
			if (cps == null) {// 公司未签约
				checkAddCount(userBean);
				// 公司没注册过，生成公司信息 没有签约
				if (StringUtil.isEmpty(pk_temp_corp)) {
					// 1:生成公司的信息
					String pktempcorp = corpservice.genTempCorpMsg252(userBean, null,singleObjectBO);
					userBean.setPk_tempcorp(pktempcorp);
					pk_temp_corp = pktempcorp;
					corpservice.genTempUser(userBean, singleObjectBO, userBean.getPk_tempcorp(), null);
					bean.setRescode(IConstant.DEFAULT);
					bean.setCorpStatus("0");
					bean.setResmsg("恭喜您创建公司成功并成为公司管理员，其他人员的加入将由您审批!");
				} else if (!StringUtil.isEmpty(pk_temp_corp)) {
					// 判断当前公司是否已经添加过管理员，如果添加过，则通过短信的方式发送给管理员审核
					UserVO uvo = corpservice.isExistManage(pk_corp, userBean.getPk_tempcorp(),
							userBean.getAccount_id());
					if (uvo != null && !StringUtil.isEmpty(userBean.getIsconfirmsg())
							&& userBean.getIsconfirmsg().equals("Y")) {// 不显示确认消息
						bean = (RegisterRespBeanVO) addToAuditUser(userBean, bean, userBean.getCorpname(), uvo);
						bean.setCorpStatus("1");
					} else if (uvo != null && (userBean.getIsconfirmsg().equals("N")
							|| StringUtil.isEmpty(userBean.getIsconfirmsg()))) {
						bean.setRescode(IConstant.DEFAULT);
						bean.setConfirmsg("您创建的公司已存在，是否确认加入公司？如有疑问请联系客服");
					} else {
						throw new BusinessException("创建公司失败，公司可能已存在，请联系客服!");
					}
				}
			} else {// 公司已签约
				CorpVO[] corps = corpservice.getPk_corpandAccountByName(userBean);
				if (corps == null) {
					throw new BusinessException("当前公司已经注册完!");
				} else if (corps.length == 1) {
					pk_corp = corps[0].getPrimaryKey();
					if (corps != null) {// bd_corp 存在公司 已经签约
						bean = isSign(userBean, pk_corp);
					}
				} else if (corps.length > 1) {
					List<ContainCorpVo> cpvolists = new ArrayList<ContainCorpVo>();
					ContainCorpVo tempvo = null;
					for (CorpVO cpvo : corps) {
						tempvo = new ContainCorpVo();
						tempvo.setAccountname(cpvo.getDef1());
						tempvo.setCname(CodeUtils1.deCode(cpvo.getUnitname()));
						tempvo.setCcode(cpvo.getUnitcode());
						tempvo.setPk_corp(cpvo.getPk_corp());
						tempvo.setPhone(CodeUtils1.deCode(cpvo.getPhone1()));
						cpvolists.add(tempvo);
					}
					bean.setRescode(IConstant.DEFAULT);
					bean.setResmsg("您公司信息存在多个!");
					bean.setCorpStatus("2");
					bean.setCpvos(cpvolists.toArray(new ContainCorpVo[0]));
				}
			}
		}
		//如果通过营业执照过来则更新营业执照信息
		updateYYzz(pk_corp,pk_temp_corp,userBean.getCorpname());
		
		
		// 如果当前公司添加成，同时还是该公司的管理员，或者审核通过的用户，则给出该公司的信息
		ResponseBaseBeanVO autobean = user320service.getAutoLogin(userBean, userBean.getAccount_id(), pk_corp, pk_temp_corp);

		if ((bean.getCpvos()!=null && bean.getCpvos().length>0) 
				|| autobean == null || autobean.getRescode().equals(IConstant.FIRDES)) {
			return bean;
		} else {
			return autobean;
		}
	}

		private RegisterRespBeanVO isSign(UserBeanVO userBean, String pk_corp) throws DZFWarpException {
		RegisterRespBeanVO bean = new RegisterRespBeanVO();
		CorpVO rescorp = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		if(rescorp.getIsseal()!=null && rescorp.getIsseal().booleanValue()){
			throw new BusinessException("该公司已封存!");
		}
		String phone = CodeUtils1.deCode(rescorp.getPhone2());
		String corpname = userBean.getCorpname();
		//获取代账机构信息
		AccountVO accountvo = (AccountVO) singleObjectBO.queryByPrimaryKey(AccountVO.class, rescorp.getFathercorp());
		if (rescorp.getIsdatacorp() != null && rescorp.getIsdatacorp().booleanValue()) {
			throw new BusinessException("您不能添加数据中心相关信息!");
		}
		if (rescorp.getIsaccountcorp() != null && rescorp.getIsaccountcorp().booleanValue()) {
			throw new BusinessException("您不能添加服务机构相关信息!");
		}
		bean.setRescode(IConstant.DEFAULT);
		//获取激活码，通过激活码关联
		UserVO uvo = corpservice.isExistManage(pk_corp, userBean.getPk_tempcorp(), userBean.getAccount_id());
		if(!StringUtil.isEmpty(userBean.getIsconfirmsg()) && "Y".equals(userBean.getIsconfirmsg())){
			if(!StringUtil.isEmpty(userBean.getActivecode()) ){//如果存在激活码时则通过激活码关联
				corpservice.updateAddCorpFromActiveCode(userBean);
				bean.setPhone(phone);
			}else{
				userBean.setPk_corp(pk_corp);
				ResponseBaseBeanVO resbean = addToAuditUser(userBean, bean, corpname, uvo);
				BeanUtils.copyNotNullProperties(resbean, bean);
			}
		}else{
			if((uvo  == null) || userBean.getAccount().equals(phone)){//如果不存在管理员，或者更改管理员时
				if(StringUtil.isEmpty(accountvo.getPhone1())){
					throw new BusinessException("获取代账机构"+CodeUtils1.deCode(accountvo.getUnitname())+"电话失败，请联系客服(400-600-9365)!");
				}
				bean.setConfirmsg("您创建的公司在系统中已存在，您可通过联系代账公司校验激活码加入您公司，是否确认加入?如有疑问请联系客服");
				bean.setFphone(CodeUtils1.deCode(accountvo.getPhone1()));
			}else{
				bean.setConfirmsg("您创建的公司已存在，是否确认加入公司？如有疑问请联系客服");
			}
		}
		bean.setPk_corp(pk_corp);
		return bean;
	}

	private ResponseBaseBeanVO addToAuditUser(UserBeanVO userBean, ResponseBaseBeanVO bean, String corpname,
			UserVO uvo) {
		// 更新关联关系
		// 判断该公司对应的用户是否已经注册过
		userBean.setIstates("3");
		userBean.setIaudituser(DZFBoolean.TRUE);
		corpservice.genTempUser(userBean, singleObjectBO, userBean.getPk_tempcorp(), userBean.getPk_corp());
		bean.setRescode(IConstant.DEFAULT);
		userBean.setPhone(uvo.getUser_code());
		userBean.setAccount(userBean.getAccount());
//		StringBuffer msg = new StringBuffer();
//		msg.append("【"+AppQueryUtil.getSignSms(userBean.getSourcesys(), userBean.getAccount(),DZFBoolean.FALSE,userBean.getQysbh())+"】手机号");
//		msg.append(userBean.getAccount().substring(0,3));
//		msg.append("****");
//		msg.append(userBean.getAccount().substring(7, 11));
//	    msg.append("正申请加入");
//	    msg.append(userBean.getCorpname());
//	    msg.append("公司，请登录"+AppQueryUtil.getDefaultAppValue(userBean.getSourcesys())+"app审核");
//		int i = SMSService.sendSMS(new String[] {userBean.getPhone()}, msg.toString());
//		msservice.sendJoinAudit(userBean);
		bean.setResmsg("您的加入申请已经发送给"+corpname+"公司的管理员"+uvo.getUser_name()+"，手机号"+uvo.getUser_code().substring(0, 3)+"****"+ uvo.getUser_code().substring(7, 11)+".请等待审核。");
		return bean;
	}

	private void updateYYzz(String pk_corp, String pk_temp_corp,String corpname) {
		//更新工商信息
//		ScanCorpInfoVO scanvo = (ScanCorpInfoVO) BusinessAction.busi_identify.getValue(corpname);
		ScanCorpInfoVO scanvo = null;
		if(scanvo!=null){
			if(!AppCheckValidUtils.isEmptyCorp(pk_corp)){
				CorpVO cpvo  = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
				if(cpvo!=null){
					cpvo.setVsoccrecode(scanvo.getVsoccrecode());//信用代码
					cpvo.setLegalbodycode(scanvo.getLegalbodycode());//法人
					cpvo.setSaleaddr(scanvo.getSaleaddr());//住所
					cpvo.setIcompanytype(scanvo.getIcompanytype());// 公司类型 1：有限公司；2：个人独资企业；3：合伙企业；
					cpvo.setDef9(scanvo.getDef9());// 注册资本
					cpvo.setDestablishdate(scanvo.getDestablishdate());;// 成立日期
					cpvo.setVbusinescope(scanvo.getVbusinescope());;// 经营范围
					cpvo.setVregistorgans(scanvo.getVregistorgans());;// 登记机关
					cpvo.setDapprovaldate(scanvo.getDapprovaldate());;// 核准日期（发证日期）
				}
				singleObjectBO.update(cpvo,new String[]{"vsoccrecode","legalbodycode","saleaddr","icompanytype","def9",
						"destablishdate","vbusinescope","vregistorgans","dapprovaldate"});
			}
			if(!StringUtil.isEmpty(pk_temp_corp)){
				TempCorpVO tempcorpvo = (TempCorpVO) singleObjectBO.queryByPrimaryKey(TempCorpVO.class, pk_temp_corp);
				if(tempcorpvo!=null){
					tempcorpvo.setVsoccrecode(scanvo.getVsoccrecode());//信用代码
					tempcorpvo.setLegalbodycode(scanvo.getLegalbodycode());//法人
					tempcorpvo.setSaleaddr(scanvo.getSaleaddr());//住所
					tempcorpvo.setIcompanytype(scanvo.getIcompanytype());;// 公司类型 1：有限公司；2：个人独资企业；3：合伙企业；
					tempcorpvo.setDef9(scanvo.getDef9());// 注册资本
					tempcorpvo.setDestablishdate(scanvo.getDestablishdate());;// 成立日期
					tempcorpvo.setVbusinescope(scanvo.getVbusinescope());;// 经营范围
					tempcorpvo.setVregistorgans(scanvo.getVregistorgans());;// 登记机关
					tempcorpvo.setDapprovaldate(scanvo.getDapprovaldate());;// 核准日期（发证日期）
					singleObjectBO.update(tempcorpvo);
				}
			}
		}


	}
	private void checkAddCount(UserBeanVO userBean) throws DZFWarpException {
		//创建未签约公司的次数不能超过15家
		List<UserToCorp>  userlist  = apppubservice.getUserCorp("",userBean.getAccount_id());

		if(userlist!=null && userlist.size()>0){
			int unqycount = 0;//未签约次数
			for(UserToCorp vo:userlist){
				if(AppCheckValidUtils.isEmptyCorp(vo.getPk_corp())){
					unqycount++;
				}
			}
			if(unqycount>15){
				throw new BusinessException("超越您创建的公司上限,如有需要请联系管理员!");
			}
		}
	}





}
