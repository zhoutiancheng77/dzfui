package com.dzf.zxkj.app.service.message.impl;

import java.util.ArrayList;
import java.util.List;

import com.dzf.zxkj.app.model.app.CollabtevaltVO;
import com.dzf.zxkj.app.service.message.IMessageSendService;
import com.dzf.zxkj.app.service.pub.IAppPubservice;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.app.utils.BeanUtils;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.enums.MsgtypeEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.message.MsgAdminVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.workbench.BsWorkbenchVO;
import com.dzf.zxkj.platform.model.tax.workbench.CorpMsgVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * app消息生成
 * 
 * @author zhangj
 *
 */
@Service("sys_appmsgserv")
public class MsgSendServiceImpl implements IMessageSendService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IAppPubservice apppubservice;

	@Override
	public void saveTypeMsg(String userid, String pk_account, String content, String pk_corp, String pk_temp_corp,
							String id, MsgtypeEnum enumtype, String period) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_account) && !StringUtil.isEmpty(pk_corp)) {
			CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
			pk_account = cpvo.getFathercorp();
		}

		List<String> accountlist = getAccountlist(pk_account, pk_corp, enumtype.getValue());

		List<MsgAdminVO> adminlist = new ArrayList<MsgAdminVO>();
		List<CorpMsgVO> corpmsglist = new ArrayList<CorpMsgVO>();
		if (accountlist != null && accountlist.size() > 0) {
			for (String str : accountlist) {
				MsgAdminVO msgvo = new MsgAdminVO();
				msgvo.setCuserid(str);// 接收人
				msgvo.setVcontent(content);
				msgvo.setSendman(userid);//
				msgvo.setVsenddate(new DZFDateTime().toString());
				msgvo.setSys_send(ISysConstants.DZF_APP);
				msgvo.setVtitle(null);
				msgvo.setIsread(DZFBoolean.FALSE);
				msgvo.setPk_corpk(pk_corp);// 小企业主信息
				msgvo.setPk_corp(pk_account);// 会计公司信息
				msgvo.setPk_temp_corp(pk_temp_corp);// 临时公司信息
				msgvo.setDr(0);
				msgvo.setPk_bill(id);
				msgvo.setMsgtype(enumtype.getValue());
				msgvo.setMsgtypename(enumtype.getName());
				msgvo.setVperiod(period);
				adminlist.add(msgvo);
				if(enumtype.getValue().intValue() == MsgtypeEnum.MSG_TYPE_YSP.getValue().intValue()
						|| enumtype.getValue().intValue() == MsgtypeEnum.MSG_TYPE_YCS.getValue().intValue()
						|| enumtype.getValue().intValue() == MsgtypeEnum.MSG_TYPE_YQK.getValue().intValue()
						|| enumtype.getValue().intValue() == MsgtypeEnum.MSG_TYPE_PZSD.getValue().intValue()
						){
					CorpMsgVO corpmsgvo = new CorpMsgVO();
					BeanUtils.copyNotNullProperties(msgvo, corpmsgvo);
					corpmsglist.add(corpmsgvo);
				}
			}
		}
		singleObjectBO.insertVOArr(StringUtil.isEmpty(pk_account) ? pk_corp : pk_account,
				adminlist.toArray(new MsgAdminVO[0]));
		if(corpmsglist!=null && corpmsglist.size()>0){
			singleObjectBO.insertVOArr(StringUtil.isEmpty(pk_account) ? pk_corp : pk_account,
					corpmsglist.toArray(new CorpMsgVO[0]));
		}
	};

	/**
	 * 查询当前代账公司下，有权限的人
	 * 
	 * @param pk_account
	 * @param pk_corp
	 * @param msgtype
	 * @return
	 * @throws DZFWarpException
	 */
	public List<String> getAccountlist(String pk_account, String pk_corp, Integer msgtype) throws DZFWarpException {

		if (StringUtil.isEmpty(pk_account)) {
			return null;
		}

		if (msgtype == null) {
			return null;
		}

		SQLParameter sp = new SQLParameter();
		StringBuffer qrysql = new StringBuffer();
		qrysql.append(" select  distinct sm.cuserid from sm_user sm ");
		qrysql.append(" inner join sm_user_role sr on sm.cuserid = sr.cuserid   ");
		qrysql.append(" inner join sm_power_func sf on sf.pk_role = sr.pk_role     ");
		qrysql.append(" where nvl(sm.dr,0)=0 and nvl(sr.dr,0)=0  and nvl(sf.dr,0) = 0");
		qrysql.append(" and sm.pk_corp =  ? ");
		qrysql.append(" and sf.resource_data_id = ? ");
		sp.addParam(pk_account);
		if (msgtype.intValue() == MsgtypeEnum.MSG_TYPE_SJQYYXTZ.getValue().intValue()) {// 客户签约
			sp.addParam("DZF102SS0ESS00000EE00045");
		} else if (msgtype.intValue() == MsgtypeEnum.MSG_TYPE_SJYWHZYXTZ.getValue().intValue()) {// 新业务合作
			sp.addParam("DZF102SS0ESS00000EE00081");
		}else if(msgtype.intValue() == MsgtypeEnum.MSG_TYPE_YSP.getValue().intValue()
				|| msgtype.intValue() == MsgtypeEnum.MSG_TYPE_YCS.getValue().intValue()
				|| msgtype.intValue() == MsgtypeEnum.MSG_TYPE_YQK.getValue().intValue()
				|| msgtype.intValue() == MsgtypeEnum.MSG_TYPE_PZSD.getValue().intValue()
				){//送票提醒
			sp.addParam("00000100000000NP1Ech0004");
		}else if(msgtype.intValue() == MsgtypeEnum.MSG_TYPE_YJNSSB.getValue().intValue()){//纳税工作台
			sp.addParam("00000100000000NP1Ech0004");
		}
		

		List<String> xxlist = (List<String>) singleObjectBO.executeQuery(qrysql.toString(), sp,
				new ColumnListProcessor());

		return xxlist;
	}

	@Override
	public void deleTypeMsg(String sourceid, String pk_corp, String pk_temp_corp, Integer msgtype)
			throws DZFWarpException {
		if (StringUtil.isEmpty(sourceid)) {
			return;
		}
		SQLParameter sp = new SQLParameter();
		StringBuffer delsql = new StringBuffer();
		delsql.append(" delete from ynt_msg_admin  ");
		delsql.append(" where nvl(dr,0)=0 and pk_bill =? ");
		sp.addParam(sourceid);
		delsql.append(" and msgtype = ? ");
		sp.addParam(msgtype);
		if (!StringUtil.isEmpty(pk_corp)) {
			delsql.append(" and pk_corp = ?");
			sp.addParam(pk_corp);
		}
		if (!StringUtil.isEmpty(pk_temp_corp)) {
			delsql.append(" and pk_temp_corp = ?");
			sp.addParam(pk_temp_corp);
		}

		singleObjectBO.executeUpdate(delsql.toString(), sp);

	}

	@Override
	public void saveMsgVoFromImage(String pk_corp, String pk_image_group) throws DZFWarpException {

		if (AppCheckValidUtils.isEmptyCorp(pk_corp)) {
			return;
		}

		if (StringUtil.isEmpty(pk_image_group)) {
			return;
		}

		ImageGroupVO grouvo = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, pk_image_group);

		if (grouvo == null) {
			return;
		}
		
		if(grouvo.getIstate()!= PhotoState.state0){//待处理的接受处理
			return;
		}

		// 查询参数设置
//		boolean kjgs = apppubservice.isParamSysCreatePZ(pk_corp);
//		if (!kjgs) {
			
			CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);

			UserVO uvo = (UserVO) singleObjectBO.queryByPrimaryKey(UserVO.class,grouvo.getCoperatorid()); //UserCache.getInstance().get(grouvo.getCoperatorid(), "");
			String corpname = cpvo.getUnitname();
			String upname = uvo == null ? "" : uvo.getUser_name();
			String update = grouvo.getTs().toString();
			Integer count = grouvo.getImagecounts();
			String cont = corpname + upname + update + "上传金额为" + grouvo.getMny().setScale(2, DZFDouble.ROUND_HALF_UP)
					+ "的" + count + "张票据，请尽快制单!";
			// 获取代账公司ids
			List<String> accountlist = apppubservice.getUserForPowerCorp(pk_corp, "WDZF02SS0ESS00000EE00069");// 填制凭证

			// xx公司xx（取上传人）2017年1月1日13:00：00上传金额为1000.00元的13张票据，请尽快制单！
			List<MsgAdminVO> adminlist = new ArrayList<MsgAdminVO>();
			if (accountlist != null && accountlist.size() > 0) {
				for (String str : accountlist) {
					MsgAdminVO msgvo = new MsgAdminVO();
					msgvo.setCuserid(str);// 接收人
					msgvo.setVcontent(cont);
					msgvo.setSendman(grouvo.getCoperatorid());
					msgvo.setVsenddate(new DZFDateTime().toString());
					msgvo.setSys_send(ISysConstants.DZF_APP);
					msgvo.setVtitle(null);
					msgvo.setIsread(DZFBoolean.FALSE);
					msgvo.setPk_corpk(pk_corp);// 小企业主信息
					msgvo.setPk_corp(cpvo.getFathercorp());// 会计公司信息
					msgvo.setPk_temp_corp("");// 临时公司信息
					msgvo.setDr(0);
					msgvo.setPk_bill(grouvo.getPrimaryKey());
					msgvo.setMsgtype(MsgtypeEnum.MSG_TYPE_APPLY_VOUCHER.getValue());
					msgvo.setMsgtypename(MsgtypeEnum.MSG_TYPE_APPLY_VOUCHER.getName());
					adminlist.add(msgvo);
				}
			}
			singleObjectBO.insertVOArr(pk_corp, adminlist.toArray(new MsgAdminVO[0]));

//		}
	}

	@Override
	public void saveAdminNsMsg(String pk_corp,String accound_id, String period, String msg_id,Object msg_hand) throws DZFWarpException {
		if(StringUtil.isEmpty(msg_id)){
			throw new BusinessException("消息不能为空!");
		}
		MsgAdminVO msgadminvo = (MsgAdminVO) singleObjectBO.queryByPrimaryKey(MsgAdminVO.class, msg_id);
		if(msgadminvo == null){
			throw new BusinessException("消息不能为空!");
		}
		
		saveAdminNsMsg(pk_corp, accound_id, period, msgadminvo,msg_hand);
		
	}
	
	@Override
	public void saveAdminNsMsg(String pk_corp,String accound_id, String period, MsgAdminVO msgadminvo,Object msg_hand)
			throws DZFWarpException {
		
		CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		//判断是否有数据，没的话重新生成vo
		SQLParameter sp = new SQLParameter();
		String qrysql = "select * from nsworkbench where nvl(dr,0)=0 and pk_corp = ? and period = ?";
		sp.addParam(msgadminvo.getPk_corpk());
		sp.addParam(msgadminvo.getVperiod());
		List<BsWorkbenchVO> bsworklist =  (List<BsWorkbenchVO>) singleObjectBO.executeQuery(qrysql, sp, new BeanListProcessor(BsWorkbenchVO.class));
		
		Object[] obj = null; ;
		String id = null;
		if(bsworklist!=null && bsworklist.size()>0){
			for(BsWorkbenchVO vo:bsworklist){
				obj = updateBsWorkvo(cpvo.getUnitname(),msgadminvo, vo,msg_hand);
			}
			id = bsworklist.get(0).getPrimaryKey();
			singleObjectBO.updateAry(bsworklist.toArray(new BsWorkbenchVO[0]));
		}else{
			BsWorkbenchVO bsworkvo = new BsWorkbenchVO();
			bsworkvo.setPk_corp(pk_corp);
			bsworkvo.setPeriod(msgadminvo.getVperiod());
			obj  = updateBsWorkvo(cpvo.getUnitname(),msgadminvo, bsworkvo,msg_hand);
			SuperVO spvo = singleObjectBO.saveObject(msgadminvo.getPk_corpk(), bsworkvo);//保存
			id = spvo.getPrimaryKey();
		}
		
		//发送消息
		saveTypeMsg(accound_id, "", (String)obj[0], pk_corp, "", id, (MsgtypeEnum)obj[1],msgadminvo.getVperiod());
		
	}
	
	private Object[] updateBsWorkvo(String unitname,MsgAdminVO msgadminvo, BsWorkbenchVO bsworkvo,Object msg_hand) {
		Object[] obj = new Object[2];

		String content = "";
		if (MsgtypeEnum.MSG_TYPE_SPTX.getValue().intValue() == msgadminvo.getMsgtype().intValue()) {// 送票提醒
			bsworkvo.setIsptx(1);
			content = unitname+"的票据已送出,请知悉!";
			obj[1] = MsgtypeEnum.MSG_TYPE_YSP;
		} else if (MsgtypeEnum.MSG_TYPE_CSTX.getValue().intValue() == msgadminvo.getMsgtype().intValue()) {// 抄税提醒
			bsworkvo.setTaxStateCopy(1);
			content = unitname+"已经抄税完成,请知悉!";
			obj[1] = MsgtypeEnum.MSG_TYPE_YCS;
		} else if (MsgtypeEnum.MSG_TYPE_QKTX.getValue().intValue() == msgadminvo.getMsgtype().intValue()) {// 清卡提醒
			bsworkvo.setTaxStateClean(1);
			content = unitname+"已经清卡完成,请知悉!";
			obj[1] = MsgtypeEnum.MSG_TYPE_YQK;
		} else if (MsgtypeEnum.MSG_TYPE_JJPZTX.getValue().intValue() == msgadminvo.getMsgtype().intValue()) {// 凭证交接
			bsworkvo.setIpzjjzt(1);
			content = unitname+"已经收到凭证,请知悉!";
			obj[1] = MsgtypeEnum.MSG_TYPE_PZSD;
		} else if(MsgtypeEnum.MSG_TYPE_YJTX.getValue().intValue() == msgadminvo.getMsgtype().intValue()){
			String ihand = (String) msg_hand;
			if("0".equals(ihand)){
				bsworkvo.setItaxconfstate(1);
				content = unitname+"已确认"+msgadminvo.getVperiod()+"的纳税申报数据,请尽快确认";
			}else{
				bsworkvo.setItaxconfstate(2);
				content = unitname+"不同意"+msgadminvo.getVperiod()+"的纳税申报数据,请尽快确认";
			}
			obj[1] = MsgtypeEnum.MSG_TYPE_YJNSSB;
		}

		obj[0] = content;

		return obj;
	}

	@Override
	public void saveMsgForWork(String id, String pk_corp, String pk_temp_corp,
			String account_id,String jsr,MsgtypeEnum typeenum,Object obj) throws DZFWarpException {
		CorpVO cpvo = null;
		if(!AppCheckValidUtils.isEmptyCorp(pk_corp)){
			cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		}
		UserVO uvo = (UserVO) singleObjectBO.queryByPrimaryKey(UserVO.class, account_id);
		MsgAdminVO msgvo = new MsgAdminVO();
		msgvo.setCuserid(jsr);// 接收人
		String content = getContent(typeenum,obj,uvo);
		msgvo.setVcontent(content);
		msgvo.setSendman(account_id);//
		msgvo.setVsenddate(new DZFDateTime().toString());
		msgvo.setSys_send(ISysConstants.DZF_APP);
		String code = getCode(typeenum,obj);
		msgvo.setNodecode("app_"+code);//节点名字
		msgvo.setVtitle(null);
		msgvo.setIsread(DZFBoolean.FALSE);
		msgvo.setPk_corpk(pk_corp);// 小企业主信息
		msgvo.setPk_corp(cpvo != null ? cpvo.getFathercorp() : "");// 会计公司信息
		msgvo.setPk_temp_corp(pk_temp_corp);// 临时公司信息
		msgvo.setDr(0);
		msgvo.setPk_bill(id);
		msgvo.setMsgtype(typeenum.getValue());
		msgvo.setMsgtypename(typeenum.getName());
		
		if(StringUtil.isEmpty(pk_corp)){
			pk_corp = Common.tempidcreate;
		}
		singleObjectBO.saveObject(pk_corp, msgvo);
	}

	private String getCode(MsgtypeEnum typeenum,Object obj) {
		if(typeenum.getValue().intValue() ==  MsgtypeEnum.MSG_TYPE_GZRB.getValue().intValue()){
			String[] strs = (String[]) obj;
			return strs[1];
		}
		return null;
	}

	private String getContent(MsgtypeEnum typeenum,Object obj,UserVO uvo) {
		if(typeenum.getValue().intValue() ==  MsgtypeEnum.MSG_TYPE_GZRB.getValue().intValue()){
			String value = ((String[])obj)[0];
			String msg =  ((String[])obj)[2];
			if("day".equals(value)){
				return msg;
			}else if("week".equals(value)){
				return  msg;
			}else if("reply".equals(value)){
				return "回复:"+msg;
			}
		}
		return null;
	}

	@Override
	public void saveAdminFWPJ(String pk_corp, String account_id, String msg_id,Integer ztpj,String ztpj_cotent,String khpj) throws DZFWarpException {

		if (StringUtil.isEmpty(msg_id)) {
			return;
		}
		MsgAdminVO adminvo = (MsgAdminVO) singleObjectBO.queryByPrimaryKey(MsgAdminVO.class, msg_id);

		if (adminvo == null) {
			return;
		}

		CollabtevaltVO vo = new CollabtevaltVO();
		vo.setBusidate(new DZFDate());
		vo.setPk_user(account_id);
		vo.setPk_corp(adminvo.getPk_corpk());
		vo.setBusitype(1);//服务评价
		vo.setItype("1");
		vo.setVbusiname(adminvo.getVbusiname());
		vo.setPk_bill(adminvo.getPk_bill());
		
		vo.setMessage(khpj);//客户评价
		vo.setSatisfaction(ztpj);//总体评价
		vo.setAppraisalnames(ztpj_cotent);
		
		singleObjectBO.saveObject(adminvo.getPk_corp(), vo);
	}

}
