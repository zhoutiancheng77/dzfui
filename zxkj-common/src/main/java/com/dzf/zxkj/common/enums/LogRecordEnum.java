package com.dzf.zxkj.common.enums;

/**
 * 日志操作类型枚举
 * @author zhangj
 *
 */
public enum LogRecordEnum {

	OPE_ALL_SYS(null,"所有操作"),
	//------------会计端-------------------
	OPE_KJ_SYS(0, "系统"), OPE_KJ_ADDVOUCHER(1, "新增凭证"), OPE_KJ_EDITVOUCHER(2, "修改凭证"), 
	OPE_KJ_DELVOUCHER(3,"删除凭证"), OPE_KJ_OTHERVOUCHER(4, "凭证其他操作"), OPE_KJ_SETTLE(5, "结账办理"), 
	OPE_KJ_KMREPORT(6,"账簿查询"), OPE_KJ_CWREPORT(7, "报表查询"), OPE_KJ_TAX(8, "纳税申报"), 
	OPE_KJ_SALARY(9,"工资表查询"), OPE_KJ_BDSET(10, "基础设置"), OPE_KJ_SJWH(11, "数据维护"), 
	OPE_KJ_ZCGL(12,"资产管理"), OPE_KJ_ZCREPROT(13, "资产报表"), OPE_KJ_IC_SET(14, "库存基础设置"), 
	OPE_KJ_IC_BUSI(15, "库存日常业务处理"), OPE_KJ_IC_REPORT(16, "库存报表查询"), OPE_KJ_PJGL(17, "票据管理"),
	OPE_KJ_NSGZT(120, "纳税工作台"),OPE_KJ_CHGL(125, "存货管理"),OPE_KJ_QYXX(130, "企业信息"),
	
	/**
	 * 管理平台
	 */
	OPE_ADMIN_SYS(17, "系统"),OPE_ADMIN_WDKH(18, "我的客户"),OPE_ADMIN_QZKHGL(19, "潜在客户"),OPE_ADMIN_KHQY(20, "客户签约"),
    OPE_ADMIN_KHLY(21, "客户留言"),OPE_ADMIN_KHPJ(22, "客户评价"),OPE_ADMIN_XYWHZ(23, "新业务合作"),OPE_ADMIN_CYZY(24, "常用摘要"),
    OPE_ADMIN_ZLJJ(25, "资料交接"),OPE_ADMIN_ZTZY(26, "账套转移"),OPE_ADMIN_KHDR(27, "客户导入"),OPE_ADMIN_PLJZ(28, "批量建账"),
    OPE_ADMIN_JZTZ(29, "建账调整"),OPE_ADMIN_KHFC(30, "客户封存"),
    OPE_ADMIN_JDRXX(31, "接单人信息"),OPE_ADMIN_FWXMXX(32, "服务项目信息"),OPE_ADMIN_FWXMFB(33, "服务项目发布"),
    OPE_ADMIN_DDCX(34, "订单查询"),OPE_ADMIN_JDGL(35, "进度管理"),OPE_ADMIN_QFXX(36, "群发消息"),
    OPE_ADMIN_QFYJ(37, "群发邮件"),OPE_ADMIN_NSGZT(38, "纳税工作台"),OPE_ADMIN_JZGZT(39, "记账工作台"),OPE_ADMIN_QFTJ(40, "欠费统计"),
    OPE_ADMIN_SFSH(41, "收费审核"),OPE_ADMIN_SFDJ(42, "收费登记"),OPE_ADMIN_DSDJ(43, "代收代缴"),
    //OPE_ADMIN_YWCL(44, "业务处理"),OPE_ADMIN_YWCX(45, "业务查询"),
    OPE_ADMIN_DLJZHT(46, "代账合同"),OPE_ADMIN_ZZFWHT(47, "增值合同"),OPE_ADMIN_HTSH(48, "合同审核"),
    OPE_ADMIN_HTCX(49, "合同查询"),OPE_ADMIN_YXSZ(50, "邮箱设置"),OPE_ADMIN_BMDA(51, "部门档案"),OPE_ADMIN_FYLX(52, "费用类型"),
    OPE_ADMIN_CSSZ(53, "参数设置"),OPE_ADMIN_KJGS(54, "会计公司"),OPE_ADMIN_FZJG(55, "分支机构"),OPE_ADMIN_YWDL(56, "业务大类"),
    OPE_ADMIN_YWXL(57, "业务类型"),OPE_ADMIN_YWBLNR(58, "办理业务内容"),OPE_ADMIN_YWLCSZ(59, "流程设置"),OPE_ADMIN_DJBHWH(60, "单据编号维护"),
    OPE_ADMIN_DXTXSZ(61, "短信提醒设置"),OPE_ADMIN_ZLDA(62, "资料档案"),OPE_ADMIN_GZJQSZ(63, "工作机器设置"),OPE_ADMIN_YWSQ(64, "业务申请"),
    OPE_ADMIN_JSGL(65, "角色管理"),OPE_ADMIN_JSQX(66, "角色权限"),OPE_ADMIN_YHGL(67, "用户管理"),OPE_ADMIN_KJRYQXFP(68, "权限分配"),
    OPE_ADMIN_GSRYQXCK(69, "权限查看"),OPE_ADMIN_GLBB(70, "管理报表"),OPE_ADMIN_YSTX(78, "应收提醒"),OPE_ADMIN_DQHT(79, "到期合同"),
    OPE_ADMIN_SPSZ(121, "审批设置"),OPE_ADMIN_LCSZ(140, "流程设置"),OPE_ADMIN_REAP(141, "任务安排"),OPE_ADMIN_RWBL(142, "任务办理"),
    OPE_ADMIN_RWJD(143, "任务进度"),
	
	/**
     * 加盟商--管理平台
     */
	OPE_ADMIN_JMKHDA(301, "客户档案"),OPE_ADMIN_JMYHGJ(302, "用户管理"),OPE_ADMIN_JMQXFZ(303, "权限复制"),OPE_ADMIN_JMSKDJ(304, "收款登记"),
	/**
	 * 集团
	 */
	OPE_JITUAN_SYS(71,"系统"),OPE_JITUAN_BDSET(72,"基础设置"),OPE_JITUAN_PZMB(73,"凭证模板设置"),OPE_JITUAN_YWSZ(74,"业务设置"),
	OPE_JITUAN_QXGL(75,"权限管理"),OPE_JITUAN_GLBB(76,"管理报表"),OPE_JITUAN_FWGM(77,"服务购买"),
    
    /**
     * 加盟商
     */
	OPE_CHANNEL_SYS(90,"系统"),OPE_CHANNEL_XSGZSZ(80,"销售规则设置"),OPE_CHANNEL_FPGL(81,"发票管理"),OPE_CHANNEL_HYFX(82,"行业分析"),OPE_CHANNEL_YHGL(84,"用户管理"),
    OPE_CHANNEL_QDQYHF(85,"渠道区域划分"),OPE_CHANNEL_SSJFX(86,"省数据分析"),OPE_CHANNEL_DQSJFX(87," 大区数据分析"),OPE_CHANNEL_QDZSJFX(88,"渠道总数据分析"),
    OPE_CHANNEL_FKDQR(89,"付款单确认"),OPE_CHANNEL_QXFP(91,"权限分配"),OPE_CHANNEL_JMSKPCX(92,"加盟商开票查询"),
    OPE_CHANNEL_23(93,"加盟商扣款查询"),OPE_CHANNEL_24(94,"销售数据分析"),OPE_CHANNEL_25(95,"返点单录入"),OPE_CHANNEL_26(96," 返点单确认"),
    OPE_CHANNEL_27(97,"返点单审批"),OPE_CHANNEL_28(98,"培训区域划分"),OPE_CHANNEL_29(99,"扣款统计表"),OPE_CHANNEL_3(100,"付款余额查询"),
    OPE_CHANNEL_30(101,"客户名称审核"),OPE_CHANNEL_31(102,"合同审批设置"),OPE_CHANNEL_32(103," 省业绩分析"),OPE_CHANNEL_33(104,"大区业绩分析"),
    OPE_CHANNEL_34(105,"渠道总业绩分析"),OPE_CHANNEL_35(106,"运营区域划分"),OPE_CHANNEL_36(107,"付款单录入"),OPE_CHANNEL_37(108,"付款单审批"),
    OPE_CHANNEL_38(109,"数据权限"),OPE_CHANNEL_4(111,"合同审核"),OPE_CHANNEL_40(112,"退款单"),
    OPE_CHANNEL_41(113,"加盟商人员统计"),OPE_CHANNEL_5(114,"客户审批"),OPE_CHANNEL_6(115,"服务套餐定义"),OPE_CHANNEL_7(116,"业绩统计"),
    OPE_CHANNEL_8(117,"财务处理分析"), OPE_CHANNEL_42(118,"商品管理"),OPE_CHANNEL_43(119,"加盟商订单"),OPE_CHANNEL_44(120,"扣款率设置"),
    OPE_CHANNEL_45(122,"流失客户名称"),OPE_CHANNEL_46(123,"合同查询"),OPE_CHANNEL_80(124,"退货单");
    
    
	/**
	 * 会计端基础的操作类型
	 * 
	 * @return
	 */
	public static LogRecordEnum[] getKjSysEnum() {
		return new LogRecordEnum[] { OPE_ALL_SYS,OPE_KJ_SYS, OPE_KJ_ADDVOUCHER, OPE_KJ_EDITVOUCHER, 
				OPE_KJ_DELVOUCHER, OPE_KJ_OTHERVOUCHER, OPE_KJ_SETTLE, OPE_KJ_KMREPORT, 
				OPE_KJ_CWREPORT, OPE_KJ_TAX, OPE_KJ_SALARY,OPE_KJ_BDSET,OPE_KJ_SJWH,OPE_KJ_PJGL,
				OPE_KJ_QYXX};
	}
	
	/**
	 * 会计端基础的操作类型
	 * 
	 * @return
	 */
	public static LogRecordEnum[] getJituanSysEnum() {
		return new LogRecordEnum[] { OPE_ALL_SYS,OPE_JITUAN_SYS, OPE_JITUAN_BDSET, OPE_JITUAN_PZMB, 
				OPE_JITUAN_YWSZ, OPE_JITUAN_QXGL, OPE_JITUAN_GLBB, OPE_JITUAN_FWGM };
	}

	/**
	 * 存在资产
	 * 
	 * @return
	 */
	public static LogRecordEnum[] getKjZcEnum() {
		return new LogRecordEnum[]{OPE_KJ_ZCGL,OPE_KJ_ZCREPROT};
	}

	/**
	 * 存在库存
	 * 
	 * @return
	 */
	public static LogRecordEnum[] getKjIcEnum() {
		return new LogRecordEnum[]{OPE_KJ_IC_SET,OPE_KJ_IC_BUSI,OPE_KJ_IC_REPORT};
	}
	
	/**
	 * 总账核算存货
	 * @return
	 */
	public static LogRecordEnum[] getKjChglEnum() {
		return new LogRecordEnum[]{OPE_KJ_CHGL};
	}
	
	/**
	 * 管理平台操作类型
	 * 
	 * @return
	 */
	public static LogRecordEnum[] getAdminSysEnum() {
        return new LogRecordEnum[] {OPE_ALL_SYS,OPE_ADMIN_WDKH,OPE_ADMIN_QZKHGL,
                OPE_ADMIN_ZLJJ,OPE_ADMIN_ZTZY,OPE_ADMIN_KHDR,OPE_ADMIN_PLJZ,
                OPE_ADMIN_JZTZ,OPE_ADMIN_KHFC,OPE_ADMIN_JDGL,OPE_ADMIN_QFXX,
                OPE_ADMIN_QFYJ,OPE_ADMIN_NSGZT,OPE_ADMIN_JZGZT,OPE_ADMIN_QFTJ,
                OPE_ADMIN_SFSH,OPE_ADMIN_SFDJ,OPE_ADMIN_DSDJ,
                OPE_ADMIN_DLJZHT,OPE_ADMIN_ZZFWHT,OPE_ADMIN_HTSH,
                OPE_ADMIN_HTCX,OPE_ADMIN_YXSZ,OPE_ADMIN_BMDA,OPE_ADMIN_FYLX,
                OPE_ADMIN_CSSZ,OPE_ADMIN_KJGS,OPE_ADMIN_FZJG,
                OPE_ADMIN_YWXL,OPE_ADMIN_YWLCSZ,OPE_ADMIN_DJBHWH,
                OPE_ADMIN_ZLDA,OPE_ADMIN_GZJQSZ,OPE_ADMIN_YWSQ,
                OPE_ADMIN_JSGL,OPE_ADMIN_JSQX,OPE_ADMIN_YHGL,OPE_ADMIN_KJRYQXFP,
                OPE_ADMIN_GSRYQXCK,OPE_ADMIN_SPSZ,OPE_ADMIN_LCSZ,OPE_ADMIN_REAP,OPE_ADMIN_RWBL,OPE_ADMIN_RWJD,
                OPE_ADMIN_GLBB,OPE_ADMIN_CYZY,OPE_ADMIN_XYWHZ,OPE_ADMIN_KHQY};
    }
	
	/**
     * 加盟商--管理平台操作类型
     * 
     * @return
     */
    public static LogRecordEnum[] getJmsAdminSysEnum() {
        return new LogRecordEnum[] {OPE_ALL_SYS,OPE_ADMIN_SYS,OPE_ADMIN_JDGL,OPE_ADMIN_QFXX,OPE_ADMIN_JMKHDA,OPE_ADMIN_JMYHGJ,OPE_ADMIN_JMQXFZ,
                OPE_ADMIN_KJGS,OPE_ADMIN_BMDA,OPE_ADMIN_CSSZ,OPE_ADMIN_JMQXFZ,OPE_ADMIN_FYLX,OPE_ADMIN_ZLDA,OPE_ADMIN_YWDL,
                OPE_ADMIN_KHDR,OPE_ADMIN_JZTZ,OPE_ADMIN_KHFC,OPE_ADMIN_JMSKDJ,OPE_ADMIN_LCSZ,OPE_ADMIN_REAP,OPE_ADMIN_RWBL,OPE_ADMIN_RWJD};
    }
	
	public static LogRecordEnum[] getChnSysEnum() {
        return new LogRecordEnum[] {OPE_CHANNEL_SYS,OPE_CHANNEL_XSGZSZ,OPE_CHANNEL_FPGL,OPE_CHANNEL_HYFX,OPE_CHANNEL_YHGL,OPE_CHANNEL_QDQYHF,
                OPE_CHANNEL_SSJFX,OPE_CHANNEL_DQSJFX,OPE_CHANNEL_QDZSJFX,OPE_CHANNEL_FKDQR,OPE_CHANNEL_QXFP,OPE_CHANNEL_JMSKPCX,
                OPE_CHANNEL_23,OPE_CHANNEL_24,OPE_CHANNEL_25,OPE_CHANNEL_26,OPE_CHANNEL_27,OPE_CHANNEL_28,OPE_CHANNEL_29,
                OPE_CHANNEL_3,OPE_CHANNEL_30,OPE_CHANNEL_31,OPE_CHANNEL_32,OPE_CHANNEL_33,OPE_CHANNEL_34,OPE_CHANNEL_35,
                OPE_CHANNEL_36,OPE_CHANNEL_37,OPE_CHANNEL_38,OPE_CHANNEL_4,OPE_CHANNEL_40,OPE_CHANNEL_41,OPE_CHANNEL_5,
                OPE_CHANNEL_6,OPE_CHANNEL_7,OPE_CHANNEL_8,OPE_CHANNEL_42,OPE_CHANNEL_43,OPE_CHANNEL_44,OPE_CHANNEL_45,OPE_CHANNEL_46,
                OPE_CHANNEL_80};
    }


	
	private Integer value;
	private String name;
	
	LogRecordEnum(Integer value, String name){
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
