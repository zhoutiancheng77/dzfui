package com.dzf.zxkj.platform.service.taxrpt;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;
import java.util.Map;

/**
 * 期初期末，发生额取值
 * @author yinyx1
 * */
public interface ITaxBalaceCcrService {

	/****
	 * 期初余额
	 * @throws DZFWarpException
	 */
	public DZFDouble getGlOpenBal(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz, YntCpaccountVO[] accountVO)throws DZFWarpException;
	/****
	 * 获取期末余额
	 * @throws DZFWarpException
	 */
	public DZFDouble getGlCloseBal(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz, YntCpaccountVO[] accountVO)throws DZFWarpException;
	
	/****
	 * 获取发生额
	 * @throws DZFWarpException
	 */
	public DZFDouble getGlAmtoCcr(String subjcode, String periodFrom, String peirodTo, String dc, String pk_corp, DZFBoolean ishasjz, YntCpaccountVO[] accountVO)throws DZFWarpException;

	/****
	 * 获取净发生额
	 * @throws DZFWarpException
	 */
	public DZFDouble getGlNetamtoCcr(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz, YntCpaccountVO[] accountVO)throws DZFWarpException;
	
	/****
	 * 获取累计发生额
	 * @throws DZFWarpException
	 */
	public DZFDouble getGlCumulamtoCcr(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz, YntCpaccountVO[] accountVO)throws DZFWarpException;

	/****
	 * 获取发生额
	 * @throws DZFWarpException
	 */
	public DZFDouble getGlAmtoCcr2(String subjcode, String periodFrom, String periodTo, String dc, String pk_corp, DZFBoolean ishasjz, String invoiceflag, DZFDouble taxrate, YntCpaccountVO[] accountVO) throws DZFWarpException;
	
	/****
	 * 获取季度发生额
	 * @throws DZFWarpException
	 */
	public DZFDouble getGlAmtoCcr3(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz, YntCpaccountVO[] accountVO) throws DZFWarpException;
	
	/****
	 * 获取季度发生额
	 * 获取发生额算法四，增加了两个参数，开票和税率
	 * invoiceflag 1/2/3  普票/专票/未开票 空：不处理改字段
	 * taxrate 税率 ， 空代表全部税率
	 * @throws DZFWarpException
	 */
	public DZFDouble getGlAmtoCcr4(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz, String invoiceflag, DZFDouble taxrate, YntCpaccountVO[] accountVO)
			throws DZFWarpException;
	/****
	 * 获取凭证张数
	 * @throws DZFWarpException
	 */
	public Integer getTrans(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz, String invoiceflag, DZFDouble taxrate, YntCpaccountVO[] accountVO) throws DZFWarpException;

	/****
	 * 获取指定税目凭证张数
	 * @throws DZFWarpException
	 */
	public Integer getTrans1(String subjcode, String period, String dc, String pk_corp,
                             DZFBoolean ishasjz, String invoiceflag, DZFDouble taxrate) throws DZFWarpException;

	/**
	 * 代表取营业收入（当前公司、当前年、当前期间、是包含未记账）
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getRevenue(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException;
	
	/**
	 * 代表取季度营业收入（当前公司、当前年、当前期间、是包含未记账）
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getRevenue2(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException;
	
	/**
	 * 代表取营业收入从年初到现在的累计数
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getRevenue3(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException;
	/**
	 * 代表取营业成本（当前公司、当前年、当前期间、是包含未记账）
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getCosts(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException;
	/**
	 * 代表取季度营业成本（当前公司、当前年、当前期间、是包含未记账）
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getCosts2(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException;
	
	/**
	 * 代表取营业成本年初到现在的累计数（当前公司、当前年、当前期间、是包含未记账）
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getCosts3(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException;
	/**
	 * 代表取利润总额（当前公司、当前年、当前期间、是包含未记账）
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getProfitBeforeTax(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException;
	
	/**
	 * 代表取季度利润总额（当前公司、当前年、当前期间、是包含未记账）
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getProfitBeforeTax2(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException;
	
	/**
	 * 代表取利润总额年初到现在的累计数（当前公司、当前年、当前期间、是包含未记账）
	 * @param pk_corp
	 * @param period
	 * @param ishasjz
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getProfitBeforeTax3(String pk_corp, String period, DZFBoolean ishasjz) throws DZFWarpException;
	
	/**
	 * 是否设置带税率属性的科目（当前、当前）
	 * @param subjcode 科目
	 * @param pk_corp 公司
	 * @return  返回值=0为未设置，返回值=1为已设置
	 * @throws DZFWarpException
	 */
	public Integer getTax(String subjcode, String pk_corp) throws DZFWarpException;
	
	/**
	 * 清单取数
	 * @param pk_corp
	 * @return 返回值=0为销项进项清单取数  返回值=1为总账取数
	 * @throws DZFWarpException
	 */
	public Integer getDatasources(String pk_corp) throws DZFWarpException;
	
	/**
	 * 取进项清单金额 (专票、本年、本期间、17税率、发票认证)
	 * @param pk_corp
	 * @param period
	 * @param tickFlag
	 * @param taxRate
	 * @param ivflag
	 * @return
	 * @throws DZFWarpException
	 */
//	public DZFDouble getQDInAmt(String pk_corp, String period, Integer tickFlag, DZFDouble[] taxRate, String ivflag) throws DZFWarpException;
	/**
	 * 取进项清单税额 (专票、本年、本期间、17税率、发票认证)
	 * @param pk_corp
	 * @param period
	 * @param tickFlag
	 * @param taxRate
	 * @param ivflag
	 * @return
	 * @throws DZFWarpException
	 */
//	public DZFDouble getQDInTxm(String pk_corp, String period, Integer tickFlag, DZFDouble[] taxRate, String ivflag) throws DZFWarpException;
	
	public DZFDouble getQDInc(String pk_corp, String period, Integer tickFlag,
                              String[] taxRate, String mnytype, String bstype, String ivflag, Integer periodtype, Map<String, List<DZFDouble>> mnyoutmap) throws DZFWarpException;
	/**
	 * 取销项清单金额 (专票、本年、本期间、17税率)
	 * @param pk_corp
	 * @param period
	 * @param tickFlag
	 * @param taxRate
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getQDOut(String pk_corp, String period, Integer tickFlag,
                              String[] taxRate, String mnytype, String busitype, Integer periodtype, Map<String, List<DZFDouble>> mnyoutmap) throws DZFWarpException;
	
	/**
	 * 取销项清单季度金额 (专票、本年、本期间、17税率)  期间前移两位
	 * @param pk_corp
	 * @param period
	 * @param tickFlag
	 * @param taxRate
	 * @return
	 * @throws DZFWarpException
	 */
//	public DZFDouble getQDOutAmt2(String pk_corp, String period, Integer tickFlag, DZFDouble[] taxRate) throws DZFWarpException;
	
	/**
	 * 取销项清单税额 (专票、本年、本期间、17税率)
	 * @param pk_corp
	 * @param period
	 * @param tickFlag
	 * @param taxRate
	 * @return
	 * @throws DZFWarpException
	 */
//	public DZFDouble getQDOutTxm(String pk_corp, String period, Integer tickFlag, DZFDouble[] taxRate) throws DZFWarpException;
	
	/**
	 * 取认证发票的数量（认证、本年、本期间）
	 */
	public DZFDouble getIVnumber(String pk_corp, String period, Integer tickFlag,
                                 String bstype, String ivflag, Integer periodtype) throws DZFWarpException;
	
	/****
	 * 取除此科目分录行本期发生  
	 * invoiceflag 1/2/3  普票/专票/未开票 空：不处理改字段
	 * taxrate 税率 ， 空代表全部税率
	 * @throws DZFWarpException
	 */
	public DZFDouble getThsmtAmt(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz, String invoiceflag, DZFDouble taxrate, YntCpaccountVO[] accountVO) throws DZFWarpException;
	
	/**
	 * 取表格某行某列的数
	 * @param corpType    公司性质
	 * @param reportName  报表名称
	 * @param coordinate  坐标
	 * @param pk_corp     公司
	 * @param period      期间
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getQsbbqs(String corpType, String reportName, String coordinate, String pk_corp, String period) throws DZFWarpException;
	
	/**
	 * 取优惠政策
	 * @param yhzc    优惠政策
	 * @param pk_corp 公司
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getYhzc(String yhzc, String pk_corp) throws DZFWarpException;
	
	/**
	 * 资产负债表取数
	 * @param projnames   项目名称
	 * @param descColname 列名
	 * @param period      期间
	 * @param pk_corp     公司
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getZcfzb(String[] projnames, String descColname, String period, String pk_corp) throws DZFWarpException;
	
	/**
	 * 取其他年度税务报表
	 * @param reportName
	 * @param coordinate
	 * @param pk_corp
	 * @param year
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getSwbb(String reportName, String coordinate, String pk_corp, String year) throws DZFWarpException;
	
	/**
	 * 取利润表某个表项的 金额
	 * @param nmnycol 累计金额  本期金额（暂时支持累计金额）
	 * @param ishasjz
	 * @param ishasjz 是包含未记账
	 * @param pk_corp 公司
	 * @param iYear 期间
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getLrb(String subjname, String nmnycol, DZFBoolean ishasjz, String pk_corp, int iYear) throws DZFWarpException;
	/**
     * 减免比例
     * 参数为：xwqy  代表小微企业优惠
     * 参数为：rjqy  代表软件企业优惠
     * 参数为：gxjs  代表高新技术企业优惠
     * 返回值：软件企业 1.0 免税，0.5 减半 0：不减免
     * 其他 ： 1 是小微/高新   0， 不是小微/高新
     */
	public DZFDouble getJmbl(String yhzc, String pk_corp, int iYear) throws DZFWarpException;
	
	/****
	 * 取年初余额
	 * @throws DZFWarpException
	 */
	public DZFDouble getGlBalNC(String subjcode, String period, String dc, String pk_corp, DZFBoolean ishasjz, YntCpaccountVO[] accountVO)throws DZFWarpException;
	/**
	 * 取其他月份增值税务报表
	 * @param reportName
	 * @param coordinate
	 * @param pk_corp
	 * @param yearmonth
	 */
	public DZFDouble getSqldata(String reportName, String coordinate, String pk_corp, String yearmonth) throws DZFWarpException;

	/**
	 * 公司的科目方案，返回值=0为小企业2007新会计，返回值=1为小企业2013小会计
	 * AccountPlan= 0/1
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getAccountPlan(String pk_corp) throws DZFWarpException;
	
	/**
	 * 公司的城建税税率，返回值为 0.01  0.05  0.07
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getCjssl(String pk_corp) throws DZFWarpException;
	
	/**
	 * 取公司纳税信息
	 * @param pk_corp
	 * @param key
	 * @return
	 * @throws DZFWarpException
	 */
	public Object getGsxx(String pk_corp, String key) throws DZFWarpException;
	
	public DZFDouble getDeficit(String pk_corp, String year) throws DZFWarpException;
	
	/**
	 * 是否为个体户
	 */
	public DZFDouble getGslx(String pk_corp) throws DZFWarpException;
	/**
	 * 所得税报送机关是国税是1,否则是地税
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getNational(String pk_corp) throws DZFWarpException;
	
	/**
	 * Switch(expr-1, value-1[, expr-2, value-2 _ [, expr-n,value-n]])
	 * expr 必要参数。要加以计算的 Variant表达式
	 * value 必要参数。如果相关的表达式为 True，则返回此部分的数值或表达式
	 * @param 
	 * @return
	 * @throws DZFWarpException
	 */
	public Object getSwitch(String[] params) throws DZFWarpException;
	
	/**
	 * 获取资产负债信息
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, ZcFzBVO[]> getZcfzInfo(String pk_corp, String period) throws DZFWarpException;
	
	/**
	 * 根据资产负债取行次的值
	 * @param zcfzvos
	 * @param qmye
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getHczcfzValue(ZcFzBVO[] zcfzvos, String hc, String qmye)throws DZFWarpException;
	//按编码
	public DZFDouble getBmzcfzValue(ZcFzBVO[] zcfzvos, String bm, String qmye)throws DZFWarpException;
	//按名称
	public DZFDouble getMczcfzValue(ZcFzBVO[] zcfzvos, String mc, String qmye)throws DZFWarpException;
	
	/**
	 * 获取利润表信息
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, LrbVO[]> getLrbInfo(String pk_corp, String period) throws DZFWarpException;
	
	
	/**
	 * 根据利润表取行次的值
	 * @param lrbvos
	 * @param qmye
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getHclrbValue(LrbVO[] lrbvos, String hc, String qmye)throws DZFWarpException;
	public DZFDouble getBmlrbValue(LrbVO[] lrbvos, String bm, String qmye)throws DZFWarpException;
	public DZFDouble getMclrbValue(LrbVO[] lrbvos, String bm, String qmye)throws DZFWarpException;

	
	/**
	 * 获取利润表值
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String,LrbVO[]> getLrbQuarter(String pk_corp, String period, String lastperiod)throws DZFWarpException;
	/**
	 * 获取利润表取行次的值
	 * @param nowlrbvos
	 * @param hc
	 * @param qmye
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getHcLrbQuarterValue(LrbVO[] nowlrbvos, LrbVO[] lastlrbvos, String hc, String qmye)throws DZFWarpException;
	//按编码
	public DZFDouble getBmLrbQuarterValue(LrbVO[] nowlrbvos, LrbVO[] lastlrbvos, String bm, String qmye)throws DZFWarpException;
	
	/**
	 * 获取季度利润表的值
	 * @param pk_corp
	 * @param period
	 */
	public Map<String, LrbquarterlyVO[]> getLrbjidu(String pk_corp, String period) throws DZFWarpException;
	
	/**
	 * 取季度利润表的值
	 * @param lrbvos
	 * @param hc
	 */
	public DZFDouble getHcLrbJiduValue(LrbquarterlyVO[] lrbvos, String hc, String period) throws DZFWarpException;
	public DZFDouble getBmLrbJiduValue(LrbquarterlyVO[] lrbvos, String hc, String period) throws DZFWarpException;
	
	/**
	 * 获取现金流量表信息
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, XjllbVO[]> getxjllInfo(String pk_corp, String period) throws DZFWarpException;

	/**
	 * 获取现金流量表季报信息
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, XjllquarterlyVo[]> getxjllQuarterInfo(String pk_corp, String period) throws DZFWarpException;
	
	public Map<String,XjllbVO[]> getxjllsqInfo(String pk_corp, String period, String preperiod) throws DZFWarpException;
	
	/**
	 * 根据行次找现金流量信息
	 */
	public DZFDouble getHcxjllValue(XjllbVO[] xjllvos, String hc, String qmye) throws DZFWarpException;
	public DZFDouble getBmxjllValue(XjllbVO[] xjllvos, String bm, String qmye) throws DZFWarpException;
	
	public DZFDouble getHcxjllsqValue(XjllbVO[] xjllvos, XjllbVO[] prexjllvos, String hc, String qmye) throws DZFWarpException;
	//按编码
	public DZFDouble getBmxjllsqValue(XjllbVO[] xjllvos, XjllbVO[] prexjllvos, String bm, String qmye) throws DZFWarpException;

	/**
	 * 取现金流量表季报的值
	 * @param vos
	 * @param hc
	 */
	public DZFDouble getHcxjllQuarterValue(XjllquarterlyVo[] vos, String hc, String period) throws DZFWarpException;
	public DZFDouble getBmxjllQuarterValue(XjllquarterlyVo[] vos, String bm, String period) throws DZFWarpException;

	/**
	 * 取后台项目编码中带有税率的本期发生数的金额/税额(月)
	 * SubjectAmt("编码","1/2/3","金额/税额"，"dr/cr"，"@period"，"@year","@corp")
	 * @param taxcode
	 * @param fpstyle
	 * @param style
	 * @param dc
	 * @param period2
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble queryzzsFpValue(String[] taxcode, String fpstyle, String style, String dc, String period2, String pk_corp)  throws DZFWarpException;
	/**
	 * 取后台项目编码中带有税率的季度发生数的金额/税额（季）
	 * SubjectAmt2("编码","1/2/3"/"","金额/税额"，"dr/cr"，"@period"，"@year","@corp")
	 * @param taxcode
	 * @param fpstyle
	 * @param style
	 * @param dc
	 * @param period2
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble queryzzsFpValue2(String[] taxcode, String fpstyle, String style, String dc, String period2, String pk_corp)  throws DZFWarpException;
	
	/****
	 * 获取期初累计发生额
	 * @throws DZFWarpException
	 */
	public DZFDouble getGlBeginningfse(String subjcode, String dr, String pk_corp, Map<String, List<QcYeVO>> beginMap)throws DZFWarpException;
	
	/****
	 * 获取专项扣除的本年累计
	 * @throws DZFWarpException
	 */
	/**
	 * 
	 * @param list 专项扣除历史记录
	 * @param begprodate  生产经营日期
	 * @param bperiod 开始期间
	 * @param eperiod 结束期间
	 * @param pk_corp 公司
	 * @param key  如果 key 空 获取三险一金的本年累计 不为空 获取其中一项的本年累计
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getSpecialdDeductionCumul(List list, DZFDate begprodate, String bperiod, String eperiod, String pk_corp, String key)throws DZFWarpException;
	//6% 是简易计税 还是 一般计税
	public DZFDouble getTaxSale6(String pk_corp) throws DZFWarpException;

	public CorpTaxVo queryCorpTaxVO(String pk_corp) throws DZFWarpException;
	
	public List querySpecChargeHis(String pk_corp)  throws DZFWarpException;

	public DZFDouble getTaxValue(CorpVO cpvo, String rptname, String period, int[][] zbs) throws DZFWarpException;

}