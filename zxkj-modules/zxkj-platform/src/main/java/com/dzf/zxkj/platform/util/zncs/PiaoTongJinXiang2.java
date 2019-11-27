package com.dzf.zxkj.platform.util.zncs;

import com.alibaba.fastjson.JSON;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.config.ZncsUrlConfig;
import com.dzf.zxkj.platform.model.piaotong.PiaoTongJinXiangDataVO;
import com.dzf.zxkj.platform.model.piaotong.PiaoTongJinXiangHVO;
import com.dzf.zxkj.platform.model.piaotong.PiaoTongJinXiangInvoiceVO;
import com.dzf.zxkj.platform.model.piaotong.PiaoTongJinXiangVO;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


public class PiaoTongJinXiang2 {


	private static String ptjxurl=null;
	private static Logger logger = Logger.getLogger(RemoteClient.class);
	private static String SUCCESS = "200";//成功标识
	
	private String taxNum;//税号
	private String taxDiscPassword;//税盘口令
	private String f2;//绑定码
	private String invoiceDateEnd;  //开票开始日期
	private String invoiceDateStart; //开票截止日期
	private String serType;//请求日期类型     yjqpDay开票日期    yjqpDay1认证日期
	private String rzPeriod;//认证期间
	
	private Integer totalCount;//发票采集总数量
	private Integer totalPageNum;//分页总数
	private Integer currentPageNum;//当前页数
	private String nextRequestTime;//下次请求时间
	
	public PiaoTongJinXiang2(String taxNum,
			String f2,
			String taxDiscPassword,
			String invoiceDateStart,
			String invoiceDateEnd,
			Integer currentPageNum,
			String serType,
			String rzPeriod){
		this.taxNum = taxNum;
		this.taxDiscPassword = taxDiscPassword;
		this.f2 = f2;
//		this.lastRequestTime = lastRequestTime;
//		this.period = period;
		this.invoiceDateStart = invoiceDateStart;
		this.invoiceDateEnd = invoiceDateEnd;
		
		this.currentPageNum = currentPageNum;
		this.serType = serType;
		this.rzPeriod = rzPeriod;
	}
	
    public List<PiaoTongJinXiangHVO> getVOs(String token){
    	List<PiaoTongJinXiangInvoiceVO> gyList = new ArrayList<PiaoTongJinXiangInvoiceVO>();
    	//2、获取概要信息
//    	i = 2;s
    	if(serType.equals("yjqpDay1")){
    		//根据认证所属期获取发票
        	gyList = getInvoiceListByRz(token);
    	}else{
    		//根据开票期间获取发票
        	gyList = getInvoiceListByKp(token);
    	}
    	
    	if(gyList == null || gyList.size() == 0){
    		return null;
    	}
    	
    	//3、获取详细信息
//    	i = 3;
    	List<PiaoTongJinXiangHVO> hList = new ArrayList<PiaoTongJinXiangHVO>();
    	PiaoTongJinXiangHVO detailvo = null;
    	for(PiaoTongJinXiangInvoiceVO vo : gyList){
    		//03机动车票
    		if(!("01".equals(vo.getInvoiceTicketType())
    				|| "04".equals(vo.getInvoiceTicketType())
    				|| "11".equals(vo.getInvoiceTicketType())
    				||"03".equals(vo.getInvoiceTicketType())))//暂时先如此处理
    			continue;
    		
    		detailvo = getInvoiceDetail(vo, token);
    		
    		hList.add(detailvo);
    	}
    	
    	return hList;
    }
    private String getPtjxurl(){
		if(StringUtils.isEmpty(ptjxurl)){
			ZncsUrlConfig zncsUrlConfig = (ZncsUrlConfig)SpringUtils.getBean(ZncsUrlConfig.class);
			ptjxurl = zncsUrlConfig.ptjx_ptjxurl;
		}
		return ptjxurl;
	}
    private PiaoTongJinXiangHVO getInvoiceDetail(PiaoTongJinXiangInvoiceVO invo, String token){
    	String url = getPtjxurl() + "/invoice/getInvoiceDetails";
    	
    	Map<String, String> paramMap = new HashMap<String, String>();
    	paramMap.put("token", getQuotes(token));
    	paramMap.put("taxNum", getQuotes(taxNum));
    	paramMap.put("invoiceNum", getQuotes(invo.getInvoiceNum()));
    	paramMap.put("invoiceCode", getQuotes(invo.getInvoiceCode()));
//    	paramMap.put("billingDate", invo.getBillingDate());
    	String msg = "获取详细信息出错[" + invo.getInvoiceNum() + "," + invo.getInvoiceCode() + "]";
    	
    	PiaoTongJinXiangVO vo = parseResult(paramMap, url, msg);
    	
    	PiaoTongJinXiangDataVO datavo = vo.getResponse().getData();

    	if(datavo == null 
//    			|| datavo.getInvoiceContent() == null
    			){
    		throw new BusinessException("获取详细信息[" + invo.getInvoiceNum()
    						+ "," + invo.getInvoiceCode() + "]出错，解析失败");
    	}
    	
    	PiaoTongJinXiangHVO detailvo = new PiaoTongJinXiangHVO();
    	detailvo.setInvoiceCode(datavo.getInvoiceCode());
    	detailvo.setInvoiceNum(datavo.getInvoiceNum());
    	detailvo.setBuyerName(datavo.getBuyerName());
    	detailvo.setSalesName(StringUtil.isEmpty(datavo.getSalesName())?datavo.getSellername():datavo.getSalesName());
    	detailvo.setTotalAmount(datavo.getTotalAmount());
    	detailvo.setTotalMoney(datavo.getTotalMoney());
    	detailvo.setTotalTaxAmount(datavo.getTotalTaxAmount());
    	detailvo.setInvoiceTicketType(datavo.getInvoiceTicketType());
    	detailvo.setInvoiceStatus(datavo.getInvoiceStatus());
    	detailvo.setRemarks(datavo.getRemarks());
    	detailvo.setInvoiceVatDetailsList(datavo.getInvoiceVatDetailsList());
    	detailvo.setBillingDate(datavo.getBillingDate());
    	detailvo.setBuyerTaxNum(datavo.getBuyerTaxNum());
    	detailvo.setSalesTaxNum(StringUtil.isEmpty(datavo.getSalesTaxNum())?datavo.getSalestaxpayernum():datavo.getSalesTaxNum());
    	detailvo.setBuyerContactWay(datavo.getBuyerContactWay());
    	detailvo.setBuyerBankAccount(datavo.getBuyerBankAccount());
    	detailvo.setSalesContactWay(datavo.getSalesContactWay());
    	detailvo.setSalesBankAccount(datavo.getSalesBankAccount());
    	//机动车发票
    	//detailvo.setSalesName(StringUtil.isEmpty(datavo.getSalesName())?datavo.getSellername():datavo.getSalesName());//销售方
    	//detailvo.setSalesTaxNum(StringUtil.isEmpty(datavo.getSalesTaxNum())?datavo.getSalestaxpayernum():datavo.getSalesTaxNum());//销方识别号
    	detailvo.setVehicleType(datavo.getVehicletype());//车辆类型
    	detailvo.setFactoryType(datavo.getFactorytype());//厂牌型号
    	detailvo.setTaxRate(datavo.getTaxrate());//税率
    	detailvo.setSellerName(datavo.getSellername());;//销货单位名称
    	detailvo.setPhoneNum(datavo.getPhonenum());;//电话
    	detailvo.setAccount(datavo.getAccount());;//账号
    	detailvo.setAddress(datavo.getAddress());;//地址
    	detailvo.setDepositBank(datavo.getDepositbank());;//开户银行
    		
    	//下次请求时间
    	detailvo.setNextRequestTime(nextRequestTime);
    	//设值
    	detailvo.setImagePath(invo.getImagePath());//设置路径
    	detailvo.setDeductibleStatus(invo.getDeductibleStatus());
    	detailvo.setDeductibleDate(invo.getDeductibleDate());
    	detailvo.setDeductiblePeriod(invo.getDeductiblePeriod());
    	
    	return detailvo;
    }
    
    
    /**
     * 根据开票日期获取最新采集内容 概要信息
     */
    public List<PiaoTongJinXiangInvoiceVO> getInvoiceListByKp(String token){
//    	String url = ptjxurl + "/invoice/getInvoiceCollection.pt";
//    	String url = ptjxurl + "/invoice/getInvoiceCollectionByMonth";
    	String url = getPtjxurl() + "/invoice/getInvoicesByInvoiceDate";
    	
    	Map<String, String> paramMap = new HashMap<String, String>();
    	paramMap.put("token", getQuotes(token));
    	paramMap.put("taxNum", getQuotes(taxNum));
//    	paramMap.put("lastRequestTime", getQuotes(lastRequestTime));
    	paramMap.put("currentPageNum", getQuotes(currentPageNum + ""));
    	if(serType.equals("yjqpDay1")){//认证期间
    		throw new BusinessException("请联系管理员！");
    	}else{//开票期间
    		paramMap.put("invoiceDateStart", getQuotes(invoiceDateStart));
        	paramMap.put("invoiceDateEnd", getQuotes(invoiceDateEnd));
    	}
    	
    	PiaoTongJinXiangVO vo = parseResult(paramMap, url, "获取概要信息出错");
    	
    	PiaoTongJinXiangDataVO datavo = vo.getResponse().getData();
    	
    	List<PiaoTongJinXiangInvoiceVO> invoiceList = null;
    	if(datavo != null){
    		
    		Integer wtotal = datavo.getTotalCount();
    		Integer wpagenum = datavo.getTotalPageNum();
    		Integer wcurrnum = datavo.getCurrentPageNum();
    		String nextRequestTime = datavo.getNextRequestTime();
    		
    		checkInvoiceData(wtotal, wpagenum, wcurrnum);
    		
    		setTotalCount(wtotal);//重新赋值
    		setTotalPageNum(wpagenum);
    		setCurrentPageNum(wcurrnum);
    		setNextRequestTime(nextRequestTime);
    		
    		invoiceList = datavo.getInvoiceList();
    		
    	}
    	
    	return invoiceList;
    }
    /**
     * 根据入账日期获取最新采集内容 概要信息
     */
    public List<PiaoTongJinXiangInvoiceVO> getInvoiceListByRz(String token){
//    	String url = ptjxurl + "/invoice/getInvoiceCollection.pt";
//    	String url = ptjxurl + "/invoice/getDeductedInvoiceCollection";
    	String url = getPtjxurl() + "/invoice/getInvoicesByTaxPeriod";
    	
    	Map<String, String> paramMap = new HashMap<String, String>();
    	paramMap.put("token", getQuotes(token));
    	paramMap.put("taxNum", getQuotes(taxNum));
//    	paramMap.put("lastRequestTime", getQuotes(lastRequestTime));
    	paramMap.put("currentPageNum", getQuotes(currentPageNum + ""));
    	paramMap.put("taxPeriod", getQuotes(rzPeriod));//认证所属期
    	
    	
    	PiaoTongJinXiangVO vo = parseResult(paramMap, url, "获取概要信息出错");
    	
    	PiaoTongJinXiangDataVO datavo = vo.getResponse().getData();
    	
    	List<PiaoTongJinXiangInvoiceVO> invoiceList = null;
    	if(datavo != null){
    		
    		Integer wtotal = datavo.getTotalCount();
    		Integer wpagenum = datavo.getTotalPageNum();
    		Integer wcurrnum = datavo.getCurrentPageNum();
    		String nextRequestTime = datavo.getNextRequestTime();
    		
    		checkInvoiceData(wtotal, wpagenum, wcurrnum);
    		
    		setTotalCount(wtotal);//重新赋值
    		setTotalPageNum(wpagenum);
    		setCurrentPageNum(wcurrnum);
    		setNextRequestTime(nextRequestTime);
    		
    		invoiceList = datavo.getInvoiceList();
    		
    	}
    	
    	return invoiceList;
    }
    
    private void checkInvoiceData(Integer wtotal, Integer wpagenum, Integer wcurrnum){
    	if(wtotal == null
				|| wpagenum == null
				|| wcurrnum == null){
			throw new BusinessException("获取概要信息时返回的参数为空，请联系管理员");
		}
    	
//    	if((totalCount != null && totalCount != wtotal)
//    			|| (totalPageNum != null && totalPageNum != wpagenum)){
//    		throw new BusinessException("获取概要信息时返回的信息校验失败，请重试");
//    	}
    }
    
    private PiaoTongJinXiangVO parseResult(Map<String, String> paramMap, String url, String errorMsg){
    	
    	List<NameValuePair> params = getParam(paramMap);
    	
    	String result = RemoteClient.sendPostData(url, params);
    	
//    	String result = null;
//    	if(i == 1){
//    		result = "{\"code\": \"200\",\"response\":{\"data\":{\"token\":\"1234567890123456678900\"}},\"message\": \"处理成功\"}";
//    	}else if(i == 2){
//    		result = "{\"code\": \"200\",\"response\":{\"data\":{\"totalCount\":1,\"totalPageNum\":1,\"currentPageNum\":1,\"nextRequestTime\":\"20171219000000\",\"invoiceList\":[{\"invoiceCode\":\"4403171130\",\"invoiceNum\":\"04419078\",\"buyerName\":\"913100005708144796\",\"salesName\":\"XXXXXX有限公司\",\"salesTaxNum\":\"XXXXXXXXXXXXXXXXX\",\"billingDate\":\"1512637087\",\"totalTaxAmount\":1517.64,\"totalAmount\":10445,\"totalMoney\":8927.36,\"invoiceTicketType\":\"01\",\"invoiceStatus\":\"0\",\"deductibleStatus\":\"0\",\"deductibleDate\":\"1512637087\",\"deductiblePeriod\":\"201708\"}]}},\"message\": \"处理成功\"}";
////    		result = "{\"code\": \"200\",\"response\":{\"data\":{\"totalCount\":1,\"totalPageNum\":1,\"currentPageNum\":1,\"nextRequestTime\":\"20171219000000\",\"invoiceList\":[{\"invoiceCode\":\"4403171130\",\"invoiceNum\":\"04419078\",\"buyerName\":\"913100005708144796\",\"salesName\":\"XXXXXX有限公司\",\"salesTaxNum\":\"XXXXXXXXXXXXXXXXX\",\"billingDate\":\"2017-11-11\",\"totalTaxAmount\":1517.64,\"totalAmount\":10445,\"totalMoney\":8927.36,\"invoiceTicketType\":\"01\",\"status\":\"0\",\"deductibleStatus\":\"0\"}]}},\"message\": \"处理成功\"}";
//    	}else if(i == 3){
////    		result = "{\"code\": \"200\",\"response\":{\"data\":{\"invoiceContent\":{\"invoiceCode\":\"3100173130\",\"invoiceNum\":\"28334487\",\"buyerName\":\"北京精诚智博科技有限公司\",\"salesName\":\"基恩士（中国）有限公司\",\"totalTaxAmount\":1787.18,\"totalAmount\":12300,\"totalMoney\":10512.82,\"invoiceTicketType\":\"01\",\"invoiceStatus\":\"0\",\"remarks\":\"11024410\",\"invoiceVatDetailsList\":[{\"name\":\"条形码读码器大屏幕无线条码手持终端二维码型\",\"specification\":\"BT-W155G\",\"unit\":\"个\",\"quantity\":2,\"unitPrice\":4347.38,\"money\":8694.77,\"taxRate\":\"17\",\"taxAmount\":1478.11},{\"name\":\"锂电池（锂离子蓄电池）蓄电池\",\"specification\":\"BT-WB1\",\"unit\":\"个\",\"quantity\":2,\"unitPrice\":303.01,\"money\":606.02,\"taxRate\":\"17\",\"taxAmount\":103.02},{\"name\":\"条形码读码器零件通讯单元USB型\",\"specification\":\"BT-WUC1U\",\"unit\":\"个\",\"quantity\":2,\"unitPrice\":606.02,\"money\":1212.03,\"taxRate\":\"17\",\"taxAmount\":206.05}],\"billingDate\":1513526400000,\"buyerTaxNum\":\"91110108744735042F\",\"salesTaxNum\":\"91310115729446808Y\"}}},\"message\": \"处理成功\"}";
////    		result = "{\"code\": \"200\",\"response\":{\"data\":{\"invoiceTicketType\":\"02\",\"invoiceContent\":{\"invoiceCode\":\"4300133700\",\"invoiceNum\":\"00015152\",\"billingDate\":\"1512637087\",\"buyerName\":\"XXXXX有限公司\",\"buyerTaxNum\":\"XXXXX\",\"totalTaxAmount\":1770.13,\"totalAmount\":60774.36,\"invoiceTicketType\":\"02\",\"invoiceStatus\":\"0\",\"remarks\":\"XXXXX\",\"carrierName\":\"XXX\",\"carrierNum\":\"914403007649523353\",\"consigneeName\":\"XXXXX有限公司\",\"consigneeNum\":\"4307237632\",\"consignerName\":\"黄从其\",\"consignerNum\":\"4307237699\",\"draweeName\":\"XXXXX有限公司\",\"draweeNum\":\"4307237632\",\"cargoInfo\":\"未知\",\"startPlace\":\"北京-上海\",\"taxRate\":\"0.3\",\"taxDiskNum\":\"未知\",\"carTypeNum\":\"未知\",\"carTon\":\"未知\",\"taxAuthorities\":\"XXXXX\",\"competentTaxName\":\"XXXXX\",\"invoiceCargoDetails\":[]}}},\"message\": \"处理成功\"}";
////    		result = "{\"code\": \"200\",\"response\":{\"data\":{\"invoiceTicketType\":\"03\",\"invoiceContent\":{\"invoiceCode\":\"321401425541\",\"invoiceNum\":\"05149208\",\"billingDate\":\"1512637087\",\"totalTaxAmount\":53060,\"totalAmount\":53060,\"totalMoney\":12345678.9,\"invoiceTicketType\":\"03\",\"invoiceStatus\":\"0\",\"remarks\":\"备注\",\"machineNum\":\"28858E25861125\",\"buyerName\":\"XXXXX有限公司\",\"buyerTaxNum\":\"XXXXX\",\"salesName\":\"XXXXX有限公司\",\"salesTaxpayerNum\":\"XXXXX\",\"identityNum\":\"421081136335482354\",\"vehicleType\":\"轿车\",\"factoryType\":\"福特牌64VB\",\"productionPlace\":\"美国\",\"certificateNum\":\"BG258E25861125\",\"inspectionNum\":\"未知\",\"engineNum\":\"52358B21\",\"vehicleidentityNum\":\"BG258E25861125\",\"importCardNum\":\"未知\",\"sellerName\":\"代用名汽车\",\"phoneNum\":\"12354023698\",\"account\":\"025258115556618815581\",\"address\":\"北京市\",\"depositBank\":\"XXXXX\",\"taxRate\":\"未知\",\"taxAuthoritiesNum\":\"未知\",\"taxPaiedProofNum\":\"48515D125\",\"ton\":\"1.5\",\"limitPerson\":\"5\",\"competentTaxName\":\"未知\"}}},\"message\": \"处理成功\"}";
//    		
//    		result = "{\"code\": \"200\",\"response\":{\"data\":{\"invoiceCode\":\"4403171130\",\"invoiceNum\":\"04419078\",\"buyerName\":\"北京精诚智博科技有限公司\",\"salesName\":\"基恩士（中国）有限公司\",\"totalTaxAmount\":1787.18,\"totalAmount\":12300,\"totalMoney\":10512.82,\"invoiceTicketType\":\"01\",\"invoiceStatus\":\"0\",\"remarks\":\"11024410\",\"invoiceVatDetailsList\":[{\"name\":\"条形码读码器大屏幕无线条码手持终端二维码型\",\"specification\":\"BT-W155G\",\"unit\":\"个\",\"quantity\":2,\"unitPrice\":4347.38,\"money\":8694.77,\"taxRate\":\"17\",\"taxAmount\":1478.11}],\"billingDate\":1513526400000,\"buyerTaxNum\":\"91110108744735042F\",\"salesTaxNum\":\"91310115729446808Y\"}},\"message\": \"处理成功\"}";
//    	}
    	PiaoTongJinXiangVO ptvo = JSON.parseObject(result, 
    			PiaoTongJinXiangVO.class);
    	
    	if(ptvo == null || !SUCCESS.equals(ptvo.getCode())){
    		logger.error(errorMsg + ":" + result);
    		
    		StringBuilder sb = new StringBuilder();
    		sb.append(errorMsg);
    		sb.append(",请联系管理员");
    		
    		if(ptvo != null && !StringUtil.isEmpty(ptvo.getCode())){
    			sb.append("。失败原因:");
    			sb.append(ptvo.getMessage());
    		}
    		
    		throw new BusinessException(sb.toString());
    	}
    	
    	return ptvo;
    }
    
    /**
     * 身份认证接口:获取token
     */
    
    public String getToken(){
    	String url = getPtjxurl() + "/company/getToken";
    	String token = null;
    	Map<String, String> paramMap = new HashMap<String, String>();
    	paramMap.put("taxNum", getQuotes(taxNum));
    	paramMap.put("vertifyCode", getQuotes(f2));
    	paramMap.put("taxDiscPassword", getQuotes(taxDiscPassword));
//    	i= 1;
    	PiaoTongJinXiangVO vo = parseResult(paramMap, url, "获取token出错");
    	
    	PiaoTongJinXiangDataVO dvo = vo.getResponse().getData();
    	
    	token = dvo.getToken();
    	
    	return token;
    }
    
    private String getQuotes(String value){
    	return "\"" + value + "\"";
    }
    
    private List<NameValuePair> getParam(Map<String, String> map){
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	
    	for(Map.Entry<String, String> entry : map.entrySet()){
    		params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
    	}
    	
    	return params;
    }

	public Integer getTotalCount() {
		return totalCount;
	}

	public Integer getTotalPageNum() {
		return totalPageNum;
	}

	public Integer getCurrentPageNum() {
		return currentPageNum;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public void setTotalPageNum(Integer totalPageNum) {
		this.totalPageNum = totalPageNum;
	}

	public void setCurrentPageNum(Integer currentPageNum) {
		this.currentPageNum = currentPageNum;
	}

	public String getNextRequestTime() {
		return nextRequestTime;
	}

	public void setNextRequestTime(String nextRequestTime) {
		this.nextRequestTime = nextRequestTime;
	}
    
}
