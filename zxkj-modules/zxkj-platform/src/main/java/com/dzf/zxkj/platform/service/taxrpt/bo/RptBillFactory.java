package com.dzf.zxkj.platform.service.taxrpt.bo;

import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.service.taxrpt.ITaxRptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("rptbillfactory")
public class RptBillFactory {
	
	@Autowired
    @Qualifier("taxRptservice_default")
	private ITaxRptService taxRptservice_default;
	
	@Autowired
    @Qualifier("taxRptservice_beijing")
	private ITaxRptService taxRptservice_beijing;
	
	@Autowired
    @Qualifier("taxRptservice_congqin")
	private ITaxRptService taxRptservice_congqin;
	
	@Autowired
    @Qualifier("taxRptservice_henan")
	private ITaxRptService taxRptservice_henan;
	
	@Autowired
    @Qualifier("taxRptservice_jiangsu")
	private ITaxRptService taxRptservice_jiangsu;

	@Autowired
    @Qualifier("taxRptservice_shandong")
	private ITaxRptService taxRptservice_shandong;
	
	@Autowired
    @Qualifier("taxRptservice_tianjin")
	private ITaxRptService taxRptservice_tianjin;
	
	@Autowired
    @Qualifier("taxRptservice_hebei")
	private ITaxRptService taxRptservice_hebei;
	
	@Autowired
    @Qualifier("taxRptservice_hainan")
	private ITaxRptService taxRptservice_hainan;
	
	@Autowired
    @Qualifier("taxRptservice_guangxi")
	private ITaxRptService taxRptservice_guangxi;
	
	@Autowired
    @Qualifier("taxRptservice_xiamen")
	private ITaxRptService taxRptservice_xiamen;

	@Autowired
    @Qualifier("taxRptservice_hubei")
	private ITaxRptService taxRptservice_hubei;
	
	@Autowired
    @Qualifier("taxRptservice_shanghai")
	private ITaxRptService taxRptservice_shanghai;
	
	@Autowired
    @Qualifier("taxRptservice_shenzhen")
	private ITaxRptService taxRptservice_shenzhen;
	
	@Autowired
    @Qualifier("taxRptservice_zhejiang")
	private ITaxRptService taxRptservice_zhejiang;
	
	@Autowired
    @Qualifier("taxRptservice_fujian")
	private ITaxRptService taxRptservice_fujian;
	
	@Autowired
    @Qualifier("taxRptservice_hunan")
	private ITaxRptService taxRptservice_hunan;
	
	@Autowired
    @Qualifier("taxRptservice_liaoning")
	private ITaxRptService taxRptservice_liaoning;
	
	@Autowired
    @Qualifier("taxRptservice_qingdao")
	private ITaxRptService taxRptservice_qingdao;
	
	@Autowired
    @Qualifier("taxRptservice_anhui")
	private ITaxRptService taxRptservice_anhui;
	
	@Autowired
    @Qualifier("taxRptservice_guangdong")
	private ITaxRptService taxRptservice_guangdong;
	
	@Autowired
    @Qualifier("taxRptservice_gansu")
	private ITaxRptService taxRptservice_gansu;
	
	@Autowired
    @Qualifier("taxRptservice_jilin")
	private ITaxRptService taxRptservice_jilin;
	
	@Autowired
    @Qualifier("taxRptservice_yunnan")
	private ITaxRptService taxRptservice_yunnan;
	
	public ITaxRptService produce(CorpTaxVo corptaxvo) {
		ITaxRptService taxrpt = null;

		if (corptaxvo.getTax_area() == null) {
			return taxRptservice_default;
		}
		switch (corptaxvo.getTax_area()) {
		case 2:
			// 北京
			taxrpt = taxRptservice_beijing;
			break;
		case 3:
			// 天津
			taxrpt = taxRptservice_tianjin;
			break;
		case 4:
			// 河北
			taxrpt = taxRptservice_hebei;
			break;
		case 7:
			// 辽宁
			taxrpt = taxRptservice_liaoning;
			break;
		case 8:
			// 吉林
			taxrpt = taxRptservice_jilin;;
			break;
		case 10:
			// 上海
			taxrpt = taxRptservice_shanghai;
			break;
		case 11:
			// 江苏
			taxrpt = taxRptservice_jiangsu;
			break;
		case 12:
			// 浙江
			taxrpt = taxRptservice_zhejiang;
			break;
		case 13:
			//安徽
			taxrpt = taxRptservice_anhui;
			break;
		case 14:
			// 福建
			taxrpt = taxRptservice_fujian;
			break;
		case 16:
			// 山东
			taxrpt = taxRptservice_shandong;
			break;
		case 17:
			// 河南
			taxrpt = taxRptservice_henan;
			break;
		case 18:
			// 湖北
			taxrpt = taxRptservice_hubei;
			break;
		case 19:
			// 湖南
			taxrpt = taxRptservice_hunan;
			break;
		case 20:
			// 广东
			taxrpt = taxRptservice_guangdong;
			break;	
		case 21:
			// 广西
			taxrpt = taxRptservice_guangxi;
			break;
		case 22:
			// 海南
			taxrpt = taxRptservice_hainan;
			break;
		case 23:
			//重庆
			taxrpt = taxRptservice_congqin;
			break;
		case 26:
			//云南
			taxrpt = taxRptservice_yunnan;
			break;
		case 29:
			//甘肃
			taxrpt = taxRptservice_gansu;
			break;
		case 151:
			//厦门
			taxrpt = taxRptservice_xiamen;
			break;
		case 171:
			//青岛
			taxrpt = taxRptservice_qingdao;
			break;
		case 234:
			//深圳
			taxrpt = taxRptservice_shenzhen;
			break;
		default:
			taxrpt = taxRptservice_default;
		}
		return taxrpt;

	}
}
