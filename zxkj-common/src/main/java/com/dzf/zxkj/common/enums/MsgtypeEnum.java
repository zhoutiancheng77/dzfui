package com.dzf.zxkj.common.enums;

/**
 * 消息通知类型枚举
 * @author zhangj
 *
 */
public enum MsgtypeEnum {

	/**
	 * 总后台操作产生消息类型
	 */
	MSG_TYPE_DZFPTGG(0, "平台公告"), MSG_TYPE_DDTKYWC(1, "订单退款已完成"), MSG_TYPE_DDTKTZ(2, "订单退款通知"), 
	MSG_TYPE_FWFBSHTG(3,"服务发布审核通过"), MSG_TYPE_FWFBSHWTG(4, "服务发布审核未通过"), MSG_TYPE_PTSHJDRTG(5, "平台审核接单人通过"), 
	MSG_TYPE_PTSHJDRWTG(6,"平台审核接单人未通过"), MSG_TYPE_JGXGXJTZ(7, "价格修改下架通知"),
	
	/**
	 * 管理平台操作产生消息类型
	 */
	MSG_TYPE_GSNBTZ(8, "公司内部通知"), 
	MSG_TYPE_YWCLTX(9,"业务处理提醒"), MSG_TYPE_YSTX(10, "应收提醒"), MSG_TYPE_HTDQTX(11, "合同到期提醒"), 
	MSG_TYPE_ZSDQTX(12,"证书到期提醒"), MSG_TYPE_FWJGFWFBSQSH(13, "服务机构服务发布申请审核"), MSG_TYPE_FWJGJDRSHSQ(14, "服务机构接单人审核申请"), 
	MSG_TYPE_FWJGTKQQ(15, "服务机构退款请求"), MSG_TYPE_DDFWWC(16, "订单服务完成"),
	MSG_TYPE_HTYSTZ(17, "缴费通知"),MSG_TYPE_HTDQTZ(18, "合同到期通知"),MSG_TYPE_FWJDBLTZ(19, "服务进度办理通知"),
	MSG_TYPE_FWJGXX(20, "服务机构消息"),MSG_TYPE_FWJGQYTZ(21, "服务机构签约通知"),MSG_TYPE_FWJGJQTZ(22, "服务机构拒签通知"),
	MSG_TYPE_DSDJYE(33, "代收代缴余额"),
	MSG_TYPE_DDWC(34, "订单完成通知"),
	MSG_TYPE_ZSDQTZ(35,"证书到期通知"),MSG_TYPE_SPTX(38,"送票提醒"),MSG_TYPE_CSTX(39,"抄税提醒"),
	MSG_TYPE_QKTX(40,"清卡提醒"),MSG_TYPE_JJPZTX(41,"凭证交接"),MSG_TYPE_YSP(42,"客户票据已送出"),MSG_TYPE_YCS(43,"客户确认抄税"),
	MSG_TYPE_YQK(44,"客户确认清卡"),MSG_TYPE_PZSD(45,"客户确认收到凭证"),MSG_TYPE_JHM(46,"激活码"),
	MSG_TYPE_ZLJJ(48,"资料交接"),MSG_TYPE_SPXX(49,"审批消息"),MSG_TYPE_BHXX(50,"驳回消息"),MSG_TYPE_YJTX(51,"应缴提醒"),MSG_TYPE_SJTX(52,"实缴提醒"),
	MSG_TYPE_QYCLTX(53,"签约处理提醒"),MSG_TYPE_YJNSSB(54,"纳税申报"),MSG_TYPE_GZRB(55,"工作日报"),MSG_TYPE_RWCL(56,"任务处理"),MSG_TYPE_RWWC(57,"任务完成"),
	MSG_TYPE_KHLZTX(58,"客户流转提醒"),MSG_TYPE_SBWCTX(59,"申报完成提醒"),
	/**
	 * 官网操作产生消息类型
	 */
	MSG_TYPE_XDTZ(23, "下单通知"),MSG_TYPE_DDFKWCTZ(24, "订单付款完成通知"),
	MSG_TYPE_KHSQTK(25, "客户申请退款"),MSG_TYPE_DDQRTZ(26, "订单确认通知"),
	
	/**
	 * 大账房APP操作产生消息类型
	 */
	MSG_TYPE_SJQYYXTZ(27, "手机签约意向通知"),MSG_TYPE_SJYWHZYXTZ(28, "手机业务合作意向通知"),
	MSG_TYPE_YGJRSQ(29, "员工加入申请"),MSG_TYPE_APPROVE(36,"票据审批通知"),
	MSG_TYPE_BILLING(37,"开票申请进度通知"),MSG_TYPE_APPLY_VOUCHER(47,"票据制单"),
	MSG_TYPE_KPFW(75,"开票服务"),
	
	/**
	 * 会计端操作产生消息类型
	 */
	MSG_TYPE_TPTHTZ(30, "图片退回通知"),MSG_TYPE_TPZZTZ(31, "图片制证通知"),MSG_TYPE_SQTKSB(32, "申请退款失败"),MSG_TYPE_CHARGE_NOTICE(71, "收费通知");
	
//	MSG_TYPE_FWXMXX(32, "服务项目信息"),
//	MSG_TYPE_FWXMFB(33, "服务项目发布"),MSG_TYPE_DDCX(34, "订单查询"),MSG_TYPE_JDGL(35, "进度管理"),MSG_TYPE_QFXX(36, "群发消息"),
//	MSG_TYPE_QFYJ(37, "群发邮件"),MSG_TYPE_NSGZT(38, "纳税工作台"),MSG_TYPE_JZGZT(39, "记账工作台"),MSG_TYPE_QFTJ(40, "欠费统计"),
//	MSG_TYPE_SFSH(41, "收费审核"),MSG_TYPE_SFDJ(42, "收费登记"),MSG_TYPE_DSDJ(43, "代收代缴"),MSG_TYPE_YWCL(44, "业务处理"),
//	MSG_TYPE_YWCX(45, "业务查询"),MSG_TYPE_DLJZHT(46, "代理记账合同"),MSG_TYPE_ZZFWHT(47, "增值服务合同"),MSG_TYPE_HTSH(48, "合同审核"),
//	MSG_TYPE_HTCX(49, "合同查询"),MSG_TYPE_YXSZ(50, "邮箱设置"),MSG_TYPE_BMDA(51, "部门档案"),MSG_TYPE_FYLX(52, "费用类型"),
//	MSG_TYPE_CSSZ(53, "参数设置"),MSG_TYPE_KJGS(54, "会计公司"),MSG_TYPE_FZJG(55, "分支机构"),MSG_TYPE_YWDL(56, "业务大类"),
//	MSG_TYPE_YWXL(57, "业务小类"),MSG_TYPE_YWBLNR(58, "办理业务内容"),MSG_TYPE_YWLCSZ(59, "业务流程设置"),MSG_TYPE_DJBHWH(60, "单据编号维护"),
//	MSG_TYPE_DXTXSZ(61, "短信提醒设置"),MSG_TYPE_ZLDA(62, "资料档案"),MSG_TYPE_GZJQSZ(63, "工作机器设置"),MSG_TYPE_YWSQ(64, "业务申请"),
//	MSG_TYPE_JSGL(65, "角色管理"),MSG_TYPE_JSQX(66, "角色权限"),MSG_TYPE_YHGL(67, "用户管理"),MSG_TYPE_KJRYQXFP(68, "会计公司权限分配"),
//	MSG_TYPE_GSRYQXCK(69, "公司人员权限查看"),MSG_TYPE_GLBB(70, "管理报表");
	
	/**
	 * 会计端基础的操作类型
	 * 
	 * @return
	 */
	public static MsgtypeEnum[] getKjSysEnum() {
		return new MsgtypeEnum[] {};
	}
	
	/**
	 * 管理平台操作类型
	 * 
	 * @return
	 */
	public static MsgtypeEnum[] getAdminSysEnum() {
		return new MsgtypeEnum[] {};
	}

	
	private Integer value;
	private String name;
	
	MsgtypeEnum(Integer value, String name){
		this.value = value;
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
