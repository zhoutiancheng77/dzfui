package com.dzf.zxkj.platform.util.zncs;

/**
 * 常量类
 * @author mfz
 *
 */
public class ZncsConst {

	public static final Integer SJLY_0=0;//数据来源_全部
	public static final Integer SJLY_1=1;//数据来源_智能识别
	public static final Integer SJLY_2=2;//数据来源_银行对账单
	public static final Integer SJLY_3=3;//数据来源_进项发票
	public static final Integer SJLY_4=4;//数据来源_销项发票

	public static final Integer PJLB_0=0;//票据类别_全部
	public static final Integer PJLB_1=1;//票据类别_b银行单据
	public static final Integer PJLB_2=2;//票据类别_c其它票据
	public static final Integer PJLB_3=3;//票据类别_发票
	
	public static final Integer SFBZ_0=0;//收方标志_本公司
	public static final Integer SFBZ_1=1;//收方标志_非本公司
	public static final Integer SFBZ_2=2;//收方标志_个人
	public static final Integer SFBZ_3=3;//收方标志_空
	public static final Integer SFBZ_4=4;//收方标志_忽略
	
	public static final Integer FFBZ_0=0;//付方标志_本公司
	public static final Integer FFBZ_1=1;//付方标志_非本公司
	public static final Integer FFBZ_2=2;//付方标志_个人
	public static final Integer FFBZ_3=3;//付方标志_空
	public static final Integer FFBZ_4=4;//付方标志_忽略
	
	public static final Integer JSFS_0=0;//结算方式_往来结算
	public static final Integer JSFS_1=1;//结算方式_现金结算
	public static final Integer JSFS_2=2;//结算方式_银行结算
	
	public static final Integer HBFS_0=0;//合并方式_不合并
	public static final Integer HBFS_1=1;//合并方式_合并凭证
	public static final Integer HBFS_2=2;//合并方式_合并分录
	
	public static final String FLCODE_SR="10";//分类编码_收入
	public static final String FLCODE_KC="11";//分类编码_库存
	public static final String FLCODE_YHPJ="12";//分类编码_银行票据
	public static final String FLCODE_FY="13";//费用
	public static final String FLCODE_CB="14";//成本
	public static final String FLCODE_ZC="15";//资产
	public static final String FLCODE_ZDY="19";//分类编码_自定义
	public static final String FLCODE_WSB="17";//分类编码_未识别
	public static final String FLCODE_WT="18";//分类编码_问题
	public static final String FLCODE_YHZR="1210";//分类编码_银行票据  转入
	public static final String FLCODE_YHZC="1211";//分类编码_银行票据  转出
	public static final String FLCODE_SPXSSR="101015";//分类编码_商品销售收入
	public static final String FLCODE_BGF="1312";//分类编码_费用  办公费
	public static final String FLCODE_LXZC="1214";//银行票据-利息支出
	public static final String FLCODE_SB="1225";//银行票据-社保
	
	public static final Integer CATEGORYTYPE_0=0;//基础
	public static final Integer CATEGORYTYPE_1=1;//自定义
	public static final Integer CATEGORYTYPE_2=2;//用户在非自定义目录中新增的自定义类别
	public static final Integer CATEGORYTYPE_3=3;//销项数据按税率
	public static final Integer CATEGORYTYPE_4=4;//销项数据按客户
	public static final Integer CATEGORYTYPE_5=5;//银行票据按账户
	
	public static final String YCXX_1="黑名单";//黑名单
	public static final String YCXX_2="无法分类";//无法分类
	public static final String YCXX_3="异常";//异常
	public static final String YCXX_4="重复票据";//重复票据
	public static final String YCXX_5="收付款方与公司名称不一致";//收付款方与公司名称不一致
	public static final String YCXX_6="开票日期晚于当前账期";//开票日期晚于当前账期
	public static final String YCXX_7="金额为空";//金额为空
	public static final String YCXX_8="销项发票应在开票期间入账";//销项发票应在开票期间入账
	public static final String YCXX_9="日期格式非法";//日期格式非法
	public static final String YCXX_10="2017年7月1日后增值税发票购买方必须填写纳税人识别号"; //2017年7月1日后增值税普通发票购买方必须填写纳税人识别号
	public static final String YCXX_11="发票已作废";	//发票已作废
	
	public static final Integer LBFX_0=0;//类别方向  无方向
	public static final Integer LBFX_1=1;//收入(销售)
	public static final Integer LBFX_2=2;//支出(采购)
	
	public static final String SBZT_1="b银行票据";
	public static final String SBZT_2="c其它票据";
	public static final String SBZT_3="增值税发票";
	public static final String SBZT_4="未识别票据";
	
	public static final Integer SRFL_0=0;//收入分类_无
	public static final Integer SRFL_1=1;//收入分类_按税率分类
	public static final Integer SRFL_2=2;//收入分类_按客户分类
	
	public static final Integer PZRQ_0=0;//收入分类_票据实际日期
	public static final Integer PZRQ_1=1;//收入分类_当前账期最后一天
	
	public static final Integer TMPMNY_0=0;//金额
	public static final Integer TMPMNY_1=1;//税额
	public static final Integer TMPMNY_2=2;//价税合计
	public static final Integer TMPMNY_3=3;//负数金额
	public static final Integer TMPMNY_4=4;//负数税额
	public static final Integer TMPMNY_5=5;//负数价税合计
	
	public static final Integer HANDFLAG_0=0;//自动
	public static final Integer HANDFLAG_1=1;//后台
	public static final Integer HANDFLAG_2=2;//前台
	
}
