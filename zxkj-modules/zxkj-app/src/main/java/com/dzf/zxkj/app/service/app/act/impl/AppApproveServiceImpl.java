package com.dzf.zxkj.app.service.app.act.impl;

import java.util.ArrayList;
import java.util.List;

import com.dzf.zxkj.app.model.app.user.AppUserVO;
import com.dzf.zxkj.app.model.approve.ApproveSetVo;
import com.dzf.zxkj.app.model.req.BusiReqBeanVo;
import com.dzf.zxkj.app.model.resp.bean.BusinessResonseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserLsBean;
import com.dzf.zxkj.app.model.ticket.ZzsTicketHVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.MsgtypeEnum;
import com.dzf.zxkj.app.service.app.act.IAppApproveService;
import com.dzf.zxkj.app.service.app.act.IAppBusinessService;
import com.dzf.zxkj.app.service.user.IManagingUsers;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.app.utils.UserUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.ImageRecordVO;
import com.dzf.zxkj.platform.model.message.MsgAdminVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 审批流操作
 * 
 * @author zhangj
 *
 */
@Service("appapprovehand")
public class AppApproveServiceImpl implements IAppApproveService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IManagingUsers mguser;
	@Autowired
	private IAppBusinessService appbusihand;

	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IZxkjRemoteAppService iZxkjRemoteAppService;



//	@Autowired
//	private IMsgService sys_msgtzserv ;
//






	@Override
	public BusinessResonseBeanVO updateApprove(BusiReqBeanVo ubean, SingleObjectBO sbo) throws DZFWarpException {
		BusinessResonseBeanVO bean = new BusinessResonseBeanVO();

		if (StringUtil.isEmpty(ubean.getPk_image_group())) {
			throw new BusinessException("图片信息为空!");
		}

		ImageGroupVO groupvo = (ImageGroupVO) sbo.queryByPrimaryKey(ImageGroupVO.class,
				ubean.getPk_image_group());

		if(groupvo == null){
			throw new BusinessException("图片信息为空!");
		}

		CorpVO cpvo = iZxkjRemoteAppService.queryByPk(groupvo.getPk_corp());

		if (groupvo == null) {
			throw new BusinessException("图片不存在!");
		}
		// 寻找当前审批人的下个人
		String currappid = groupvo.getVapprovetor();
		String currope = IConstant.APPROVE;

		if(StringUtil.isEmpty(currappid)){
			currappid = groupvo.getCoperatorid();
			currope = IConstant.COMMIT;
		}

		String[] nextid = getNextAppid(ubean, currappid,currope);

		if(nextid[0].equals(groupvo.getCoperatorid())){//下个审批人是自己的话则审批流程走完
			nextid =new String[]{"END","END"};
		}

		ImageRecordVO recordvo = new ImageRecordVO();

		String[] currs = new String[]{currappid,""};

		String vmemo = StringUtil.isEmpty(ubean.getVmemo()) ? "通过":ubean.getVmemo();

		iZxkjRemoteAppService.saveImageRecord(ubean.getPk_image_group(), ubean.getPk_image_group(),
				currs, nextid, currope, ubean.getPk_corp(),
				ubean.getPk_tempcorp(), vmemo);

		if ("END".equals(nextid[0])) {
			recordvo.setVapprovemsg("会计开始处理票据!");
			groupvo.setVapprovetor(nextid[0]);
			groupvo.setIstate(PhotoState.state0);
			//是否生成凭证
			if(!StringUtil.isEmpty(groupvo.getPk_ticket_h())){
				ZzsTicketHVO zzshvo = (ZzsTicketHVO) singleObjectBO.queryByPrimaryKey(ZzsTicketHVO.class, groupvo.getPk_ticket_h());
				if(zzshvo != null){
					appbusihand.saveVoucherFromTicket(groupvo.getCvoucherdate(),groupvo.getMny(),groupvo.getSettlemode(), groupvo.getMemo(), ubean.getPk_corp(),
							groupvo.getCoperatorid(), zzshvo, groupvo);
				}
			}else{
				singleObjectBO.update(groupvo);
				IAppBusinessService appbusihand = (IAppBusinessService) SpringUtils.getBean("appbusihand");
				appbusihand.saveVoucherFromPic(groupvo.getPrimaryKey(), groupvo.getPk_corp(),sbo);
				groupvo = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, groupvo.getPrimaryKey());
			}

		} else {
			if(!ubean.getAccount_id().equals(currappid)){
				throw new BusinessException("您公司存在审批流，当前审批人错误!");
			}
			groupvo.setVapprovetor(nextid[0]);
			groupvo.setIstate(PhotoState.state200);
		}

		MsgAdminVO msgvo = new MsgAdminVO();
		UserVO currappvo = iZxkjRemoteAppService.queryUserJmVOByID(currappid);// UserCache.getInstance().get(currappid, "");
		UserVO copervo = iZxkjRemoteAppService.queryUserJmVOByID(groupvo.getCoperatorid());//UserCache.getInstance().get(groupvo.getCoperatorid(), "");
		if(currappvo != null){
			//给上传人发当前人的审批
			msgvo.setPk_corp("");
			msgvo.setCuserid(groupvo.getCoperatorid());
			msgvo.setMsgtype(MsgtypeEnum.MSG_TYPE_APPROVE.getValue());
			msgvo.setMsgtypename(MsgtypeEnum.MSG_TYPE_APPROVE.getName());
			msgvo.setVcontent(currappvo.getUser_name() +"同意您"+groupvo.getDoperatedate()+"申请审批的票据，审批说明:"+vmemo +",请点击查看!");
			msgvo.setSendman(currappvo.getPrimaryKey());
			msgvo.setVsenddate(new DZFDateTime().toString());
			msgvo.setSys_send(ISysConstants.DZF_APP);
			msgvo.setVtitle(null);
			msgvo.setIsread(DZFBoolean.FALSE);
			msgvo.setPk_corpk(groupvo.getPk_corp());//小企业主信息
			msgvo.setDr(0);
			msgvo.setPk_bill(groupvo.getPrimaryKey());
			//生成消息记录
			if(!currope.equals(IConstant.COMMIT)){
				singleObjectBO.saveObject(groupvo.getPk_corp(), msgvo);
			}
			//给下个审批人发申请审批的数据
			if(!"END".equals(nextid[0])){
				MsgAdminVO nextmsgvo = new MsgAdminVO();
				BeanUtils.copyProperties(msgvo, nextmsgvo);
				nextmsgvo.setSendman(currappvo.getPrimaryKey());
				nextmsgvo.setCuserid(nextid[0]);
				nextmsgvo.setVcontent(cpvo.getUnitname()+"的"+copervo.getUser_name() +"申请审批票据,请点击查看!");
				nextmsgvo.setPrimaryKey(null);
				singleObjectBO.saveObject(groupvo.getPk_corp(), nextmsgvo);
			}
		}

		singleObjectBO.update(groupvo);

		bean.setRescode(IConstant.DEFAULT);
		bean.setResmsg("审批成功!");
		return bean;
	}



		/**
	 * 获取当前
	 *
	 * @return
	 * @throws DZFWarpException
	 */
	private String[] getNextAppid(BusiReqBeanVo ubean, String currappid,String currope) throws DZFWarpException {
		//根据当前的审批人，+图片信息查询对应的审批记录
		String[] apps = new String[2];
		//从审批流获取下一审批人
		BusinessResonseBeanVO bean = queryApprovSet(ubean);

		DZFBoolean buse = bean.getUse();

		List<UserLsBean> nomalbean = bean.getStaff();

		List<String> normallist = new ArrayList<String>();

		if(nomalbean!=null && nomalbean.size()>0){
			for(UserLsBean temp:nomalbean){
				normallist.add(temp.getUserid());
			}
		}

		UserLsBean adminbean = bean.getBoss();

		UserLsBean cashbean = bean.getCash();

		if (buse != null && buse.booleanValue()) {
			String vboss = adminbean.getUserid();
			String vcash = cashbean.getUserid();
			if (!StringUtil.isEmpty(vboss) && vboss.equals(currappid)) {
				if (!StringUtil.isEmpty(vcash) && vcash.equals(currappid)) {
					apps[0] ="END";
					apps[1] ="END";
				} else {
					apps[0] =vcash;
					apps[1] =IConstant.CASH;
				}
			} else  {
				if (currope == IConstant.APPROVE && !StringUtil.isEmpty(vcash) && vcash.equals(currappid)) {
					apps[0] ="END";
					apps[1] ="END";
				} else {
					apps[0] =vboss;
					apps[1] =IConstant.ADMIN;
				}
			}
			if(StringUtil.isEmpty(apps[0])){
				throw new BusinessException("审批失败，请重新配置审批流！");
			}
		}
		if(StringUtil.isEmpty(apps[0]) && StringUtil.isEmpty(apps[1])){
			apps[0] ="END";
			apps[1] ="END";
		}
		return apps;

	}

	public BusinessResonseBeanVO queryApprovSet(BusiReqBeanVo ubean) throws DZFWarpException {
		BusinessResonseBeanVO bean = new BusinessResonseBeanVO();
		AppCheckValidUtils.isEmptyWithCorp(ubean.getPk_corp(), ubean.getPk_tempcorp(), "查询审批流失败：公司信息为空!");
		// 查询当前公司是否已经存在，
		SQLParameter sp = new SQLParameter();
		StringBuffer qrysql = new StringBuffer();
		qrysql.append(" select * from " + ApproveSetVo.TABLE_NAME);
		qrysql.append(" where ");
		if (!AppCheckValidUtils.isEmptyCorp(ubean.getPk_corp())) {
			qrysql.append(" pk_corp = ?  ");
			sp.addParam(ubean.getPk_corp());
		} else {
			qrysql.append(" pk_temp_corp = ? ");
			sp.addParam(ubean.getPk_tempcorp());
		}
		qrysql.append(" and nvl(dr,0) = 0 ");

		List<ApproveSetVo> setvos = (List<ApproveSetVo>) singleObjectBO.executeQuery(qrysql.toString(), sp,
				new BeanListProcessor(ApproveSetVo.class));

		String vboss = null;

		String vcash = null;
		DZFBoolean use = DZFBoolean.FALSE;
		if (setvos != null && setvos.size() > 0) {
			vboss = setvos.get(0).getVboss();

			vcash = setvos.get(0).getVcash();

			use = setvos.get(0).getBuse() == null ? DZFBoolean.FALSE : setvos.get(0).getBuse();
		}

		ubean.setIstates("2");// 没被停用的用户

		List<AppUserVO> listvo = mguser.getUserlist(ubean, DZFBoolean.FALSE);

		UserLsBean[] ubeans = UserUtil.setUserLsBean(listvo.toArray(new AppUserVO[0]));

		// 普通用户分成一组
		List<UserLsBean> normals = new ArrayList<UserLsBean>();

		UserLsBean bossbean = new UserLsBean();

		UserLsBean cashbean = new UserLsBean();

		DZFBoolean isnormal = DZFBoolean.TRUE;
		for (UserLsBean temp : ubeans) {
			isnormal = DZFBoolean.TRUE;

			if (!StringUtil.isEmpty(vboss) && vboss.equals(temp.getUserid())) {
				BeanUtils.copyProperties(temp, bossbean);
				isnormal = DZFBoolean.FALSE;
			}

			if (!StringUtil.isEmpty(vcash) && vcash.equals(temp.getUserid())) {
				BeanUtils.copyProperties(temp, cashbean);
				isnormal = DZFBoolean.FALSE;
			}


			if (isnormal.booleanValue() && "Y".equals(temp.getBdata()) || IConstant.FIRDES.equals(temp.getUsergrade())) {
				normals.add(temp);
			}
		}

		bean.setRescode(IConstant.DEFAULT);
		bean.setUse(use);
		bean.setResmsg("查询审批流设置成功!");
		bean.setStaff(normals);
		bean.setBoss(bossbean);
		bean.setCash(cashbean);

		return bean;
	}
	@Override
	public boolean bOpenApprove(String pk_corp) throws DZFWarpException {

		if (AppCheckValidUtils.isEmptyCorp(pk_corp)){
			throw new BusinessException("公司信息不能为空!");
		}

		StringBuffer qrysql = new StringBuffer();
		qrysql.append("select * from " + ApproveSetVo.TABLE_NAME);
		qrysql.append(" where nvl(dr,0) =0 ");
		qrysql.append("  and pk_corp = ?  ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);

	 	List<ApproveSetVo> listvos =
	 			(List<ApproveSetVo>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(ApproveSetVo.class));

	 	if(listvos == null || listvos.size() == 0 ){
	 		return false;
	 	}

	 	DZFBoolean bopen = listvos.get(0).getBuse();

	 	if(bopen!=null && bopen.booleanValue()){
	 		return true;
	 	}

		return false;
	}

	@Override
	public BusinessResonseBeanVO updateReject(BusiReqBeanVo ubean) throws DZFWarpException {
		// 寻找当前审批人的最开始的人
		BusinessResonseBeanVO bean = new BusinessResonseBeanVO();

		if (StringUtil.isEmpty(ubean.getPk_image_group())) {
			throw new BusinessException("图片信息为空!");
		}

		ImageGroupVO groupvo = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class,
				ubean.getPk_image_group());

		if (groupvo == null) {
			throw new BusinessException("图片不存在!");
		}

		// 寻找当前审批人的下个人
		String currappid = groupvo.getVapprovetor();
		String currope = IConstant.APPRETURN;

		String[] currs = new String[]{currappid,""};
		String[] nextid = new String[]{"",""};

		iZxkjRemoteAppService.saveImageRecord(ubean.getPk_image_group(), ubean.getPk_image_group(), currs,
				nextid, currope, ubean.getPk_corp(), ubean.getPk_tempcorp(),ubean.getVmemo());

		groupvo.setIstate(PhotoState.state201);
		groupvo.setVapprovetor("");

		singleObjectBO.update(groupvo);


		//生成消息记录
		MsgAdminVO msgvo = new MsgAdminVO();
		UserVO currappvo =iZxkjRemoteAppService.queryUserJmVOByID(currappid);// UserCache.getInstance().get(currappid, "");
		UserVO copervo =iZxkjRemoteAppService.queryUserJmVOByID(groupvo.getCoperatorid());// UserCache.getInstance().get(groupvo.getCoperatorid(), "");
		//给上传人发当前人的审批
		msgvo.setPk_corp("");
		msgvo.setCuserid(groupvo.getCoperatorid());
		msgvo.setMsgtype(MsgtypeEnum.MSG_TYPE_APPROVE.getValue());
		msgvo.setMsgtypename(MsgtypeEnum.MSG_TYPE_APPROVE.getName());
		msgvo.setVcontent(currappvo.getUser_name() +"拒绝您"+groupvo.getDoperatedate()+"申请审批的票据，审批说明:"+ubean.getVmemo() +",请点击查看!");
		msgvo.setSendman(currappvo.getPrimaryKey());
		msgvo.setVsenddate(new DZFDateTime().toString());
		msgvo.setSys_send(ISysConstants.DZF_APP);
		msgvo.setVtitle(null);
		msgvo.setIsread(DZFBoolean.FALSE);
		msgvo.setPk_corpk(groupvo.getPk_corp());//小企业主信息
		msgvo.setDr(0);
		msgvo.setPk_bill(groupvo.getPrimaryKey());
		singleObjectBO.saveObject(groupvo.getPk_corp(), msgvo);

		bean.setRescode(IConstant.DEFAULT);
		bean.setResmsg("驳回成功!");


		return bean;
	}

	@Override
	public BusinessResonseBeanVO saveApproveSet(ApproveSetVo setvo) throws DZFWarpException {
		BusinessResonseBeanVO bean = new BusinessResonseBeanVO();

		AppCheckValidUtils.isEmptyWithCorp(setvo.getPk_corp(), setvo.getPk_temp_corp(), "审批流设置失败：公司信息为空!");

		// 查询当前公司是否已经存在，
		SQLParameter sp = new SQLParameter();
		StringBuffer qrysql = new StringBuffer();
		qrysql.append(" select * from " + ApproveSetVo.TABLE_NAME);
		qrysql.append(" where ");
		if (!AppCheckValidUtils.isEmptyCorp(setvo.getPk_corp())) {
			qrysql.append(" pk_corp = ?  ");
			sp.addParam(setvo.getPk_corp());
		} else {
			qrysql.append(" pk_temp_corp = ? ");
			sp.addParam(setvo.getPk_temp_corp());
		}
		qrysql.append(" and nvl(dr,0) = 0 ");

		List<ApproveSetVo> setvos = (List<ApproveSetVo>) singleObjectBO.executeQuery(qrysql.toString(), sp,
				new BeanListProcessor(ApproveSetVo.class));


		if(setvo.getBuse()!=null && setvo.getBuse().booleanValue()){
			if(StringUtil.isEmpty(setvo.getVboss())){
				throw new BusinessException("老板不能为空!");
			}
			if(StringUtil.isEmpty(setvo.getVcash())){
				throw new BusinessException("出纳不能为空!");
			}
		}


		if (setvos != null && setvos.size() > 0) {
			ApproveSetVo vo = setvos.get(0);
			vo.setVstaff(setvo.getVstaff());
			vo.setVboss(setvo.getVboss());
			vo.setVcash(setvo.getVcash());
			vo.setBuse(setvo.getBuse());
			singleObjectBO.update(vo, new String[] { "vstaff", "vboss", "vcash", "buse" });
		} else {
			String pk_corp = setvo.getPk_corp();
			if (StringUtil.isEmpty(setvo.getPk_corp())) {
				pk_corp = Common.tempidcreate;
			}
			singleObjectBO.saveObject(pk_corp, setvo);
		}

		bean.setRescode(IConstant.DEFAULT);
		bean.setResmsg("设置成功!");

		return bean;
	}












}
