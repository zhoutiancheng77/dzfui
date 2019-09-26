package com.dzf.zxkj.platform.model.st;

/**
 * 纳税申报项目对照总账科目
 * */
public class NssbContrastPrjToAcc {
	
	private static NssbContrastPrjToAcc cons;
	public final static String STA="STA";
	public final static String EDIT="EDIT";
	public final static String FM="FM";
	public final static int DF=1;
	public final static int JF=0;
	
	private NssbContrastPrjToAcc(){};
	
	
	public static NssbContrastPrjToAcc getInstanct(){
		if(cons==null){
			cons = new NssbContrastPrjToAcc();
		}
		return cons;
	}

	/**
	 * 获取科目取数方向
	 * */
	public int getReportKmqsfx(String reportcode){
		int fx=0;
		switch (reportcode) {
		case "A101010"://一般企业收入明细表
			fx= DF;
			break;
		case "A102010"://一般企业成本支出明细表
			fx= JF;
			break;
		case "A104000"://期间费用明细表
			fx= JF;
			break;
		case "A105050"://职工薪酬纳税调整明细表
			fx= JF;
			break;
		case "A105060"://广告费和业务宣传费跨年度纳税调整明细表
			fx= JF;
			break;
		}
		return fx;
	}
	
	/**
	 * 获取报表项目对照科目关系
	 * @param reportcode 报表编码
	 * */
	public String[][] getReportConst(String reportcode,String type){
		String[][] rp_const=null;
		switch (reportcode) {
		case "A100000"://纳税申报主表
			if("2007".equals(type)){
				rp_const= new String[][]{nssbxm,nssbkm_a,nssb_lb};
			}else{
				rp_const= new String[][]{nssbxm,nssbkm_b,nssb_lb};
			}
			break;
		case "B100000"://月季度纳税申报主表
			if("2007".equals(type)){
				rp_const= new String[][]{yjdnssbxm,yjdnssbkm_a,yjdnssbkm_lj_a,yjdnssb_lb};
			}else{
				rp_const= new String[][]{yjdnssbxm,yjdnssbkm_b,yjdnssbkm_lj_b,yjdnssb_lb};
			}
			break;
		case "A101010"://一般企业收入明细表
			if("2007".equals(type)){
				rp_const= new String[][]{ybqysrxm,ybqysrkm_a};
			}else{
				rp_const= new String[][]{ybqysrxm,ybqysrkm_b};
			}
			break;
		case "A102010"://一般企业成本支出明细表
			if("2007".equals(type)){
				rp_const= new String[][]{ybqycbxm,ybqycbkm_a};
			}else{
				rp_const= new String[][]{ybqycbxm,ybqycbkm_b};
			}
			break;
		case "A104000"://期间费用明细表
			if("2007".equals(type)){
				rp_const= new String[][]{qjfyxm,qjfykm_xsfy_a,qjfykm_xsfyjwzf,qjfykm_glfy_a,qjfykm_glfyjwzf,qjfykm_cwfy_a,qjfykm_cwfyjwzf};
			}else{
				rp_const= new String[][]{qjfyxm,qjfykm_xsfy_b,qjfykm_xsfyjwzf,qjfykm_glfy_b,qjfykm_glfyjwzf,qjfykm_cwfy_b,qjfykm_cwfyjwzf};
			}
			break;
		case "A105050"://职工薪酬纳税调整明细表
			if("2007".equals(type)){
				rp_const= new String[][]{zgxcxm,zgxc_zzje_a,zgxc_ssgdkcl,zgxc_ljjzkc,zgxc_ssje_a,zgxc_nstzje,zgxc_jzkcje};
			}else{
				rp_const= new String[][]{zgxcxm,zgxc_zzje_b,zgxc_ssgdkcl,zgxc_ljjzkc,zgxc_ssje_b,zgxc_nstzje,zgxc_jzkcje};
			}
			break;
		case "A105060"://广告费和业务宣传费跨年度纳税调整明细表
			if("2007".equals(type)){
				rp_const= new String[][]{ggywxcxm,ggywxckm_a};
			}else{
				rp_const= new String[][]{ggywxcxm,ggywxckm_b};
			}
			break;
		case "A105070"://捐赠支出纳税调整明细表
			rp_const= new String[][]{jzzcnstzxm,jzzcnstz_1,jzzcnstz_2,jzzcnstz_3,jzzcnstz_4,jzzcnstz_5,jzzcnstz_6};
			break;
		case "A105080"://资产折旧、摊销情况及纳税调整明细表
			rp_const= new String[][]{zjtxtzxm,zjtxtz_1,zjtxtz_2,zjtxtz_3,zjtxtz_4,zjtxtz_5,zjtxtz_6,zjtxtz_7,zjtxtz_8,zjtxtz_9,zjtxtz_10};
			break;
		case "A106000"://企业所得税弥补亏损明细表
			rp_const= new String[][]{mbksxm,mbks_1,mbks_2,mbks_3,mbks_4,mbks_5,mbks_6,mbks_7,mbks_8,mbks_9,mbks_10,mbks_11};
			break;
		case "A107040"://减免所得税优惠明细表
			rp_const= new String[][]{jmsdsxm,jmsdsje};
			break;
		case "A105000"://纳税调整明细表
			rp_const= new String[][]{nstzmxxm,nstzmx_zzje,nstzmx_ssje,nstzmx_tzje,nstzmx_tjje};
			break;
		}
		return rp_const;
	}
	
	
	/**
	 * 获取报表行内容
	 * @param reportcode 报表编码
	 * */
	public String[] getReportItem(String reportcode){
		String[] rp_item=null;
		switch (reportcode) {
		case "A100000"://纳税申报表
			rp_item= nssbitem;
			break;
		case "B100000"://月季度纳税申报表
			rp_item= yjdnssbitem;
			break;	
		case "A101010"://一般企业收入明细表
			rp_item= ybqysritem;
			break;
		case "A102010"://一般企业成本支出明细表
			rp_item= ybqycbitem;
			break;
		case "A104000"://期间费用明细表
			rp_item= qjfyitem;
			break;
		case "A105050"://职工薪酬纳税调整明细表
			rp_item= zgxcitem;
			break;
		case "A105060"://广告费和业务宣传费跨年度纳税调整明细表
			rp_item= ggywxcitem;
			break;
		case "A105070"://捐赠支出纳税调整明细表
			rp_item= jzzcnstzitem;
			break;
		case "A105080"://资产折旧、摊销情况及纳税调整明细表
			rp_item= zjtxtzitem;
			break;
		case "A106000"://企业所得税弥补亏损明细表
			rp_item= mbksitem;
			break;
		case "A107040"://减免所得税优惠明细表
			rp_item= jmsdsitem;
			break;
		case "A105000"://纳税调整明细表
			rp_item= nstzmxitem;
			break;
		}
		return rp_item;
	}
	
	/**
	 * 获取列值写入VO的Item
	 * @param reportcode 报表编码
	 * */
	public String[] getReportBeanItem(String reportcode){

		String[] rp_beanitem=null;
		switch (reportcode) {
		case "A100000"://纳税申报表
			rp_beanitem=nssbvoitem;
			break;
		case "B100000"://纳税申报表
			rp_beanitem=yjdnssbvoitem;
			break;
		case "A101010"://一般企业收入明细表
			rp_beanitem= ybqysrvoitem;
			break;
		case "A102010"://一般企业成本支出明细表
			rp_beanitem= ybqycbvoitem;
			break;
		case "A104000"://期间费用明细表
			rp_beanitem= qjfyvoitem;
			break;
		case "A105050"://职工薪酬纳税调整明细表
			rp_beanitem= zgxcvoitem;
			break;
		case "A105060"://职工薪酬纳税调整明细表
			rp_beanitem= ggywxcvoitem;
			break;
		case "A105070"://捐赠支出纳税调整明细表
			rp_beanitem= jzzcnstzvoitem;
			break;
		case "A105080"://资产折旧、摊销情况及纳税调整明细表
			rp_beanitem= zjtxtzvoitem;
			break;
		case "A106000"://企业所得税弥补亏损明细表
			rp_beanitem= mbksvoitem;
			break;
		case "A107040"://减免所得税优惠明细表
			rp_beanitem= jmsdsvoitem;
			break;
		case "A105000"://纳税调整明细表
			rp_beanitem= nstzmxvoitem;
			break;
		}
		return rp_beanitem;
	
	}
	
//	/**
//	 * 一般企业收入
//	 * */
//	public String[][] getYbqysrConst(String type){
//		if("2007".equals("type")){
//			return new String[][]{ybqysrxm,ybqysrkm_a};
//		}else{
//			return new String[][]{ybqysrxm,ybqysrkm_b};
//		}
//	}
//	public String[] getYbqysrConstItem(){
//		return ybqysritem;             
//	}
//	
//	/**
//	 * 一般企业成本
//	 * */
//	public String[][] getYbqycbConst(String type){
//		if("2007".equals("type")){
//			return new String[][]{ybqycbxm,ybqycbkm_a};
//		}else{
//			return new String[][]{ybqycbxm,ybqycbkm_b};
//		}
//	}
//	public String[] getYbqycbConstItem(){
//		return ybqycbitem;             
//	}
//	
//	/**
//	 * 期间费用明细
//	 * */
//	public String[][] getQjfyConst(String type){
//		if("2007".equals("type")){
//			return new String[][]{qjfyxm,qjfykm_xsfy_a,qjfykm_xsfyjwzf,qjfykm_glfy_a,qjfykm_glfyjwzf,qjfykm_cwfy_a,qjfykm_cwfyjwzf};
//		}else{
//			return new String[][]{qjfyxm,qjfykm_xsfy_b,qjfykm_xsfyjwzf,qjfykm_glfy_b,qjfykm_glfyjwzf,qjfykm_cwfy_b,qjfykm_cwfyjwzf};
//		}
//	}
//	public String[] getQjfyConstItem(){
//		return qjfyitem;             
//	}
//	
	
	//一般企业成本
	private static String[] ybqysritem=new String[]{
		"一、营业收入（2+9）",
		"　　（一）主营业务收入（3+5+6+7+8）",
		"　　　　1.销售商品收入",
		"　　　　　　其中：非货币性资产交换收入",
		"　　　　2.提供劳务收入",
		"　　　　3.建造合同收入",
		"　　　　4.让渡资产使用权收入",
		"　　　　5.其他",
		"　　（二）其他业务收入（10+12+13+14+15）",
		"　　　　1.销售材料收入",
		"　　　　　　其中：非货币性资产交换收入",
		"　　　　2.出租固定资产收入",
		"　　　　3.出租无形资产收入",
		"　　　　4.出租包装物和商品收入",
		"　　　　5.其他",
		"二、营业外收入（17+18+19+20+21+22+23+24+25+26）",
		"　　　（一）非流动资产处置利得",
		"　　　（二）非货币性资产交换利得",
		"　　　（三）债务重组利得",
		"　　　（四）政府补助利得",
		"　　　（五）盘盈利得",
		"　　　（六）捐赠利得",
		"　　　（七）罚没利得",
		"　　　（八）确实无法偿付的应付款项",
		"　　　（九）汇兑收益",
		"　　　（十）其他"//二、营业外收入（17+18+19+20+21+22+23+24+25+26）
		
	};
	
	private static String[] ybqysrxm= new String[]{
		"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26"
	};
	//2007新会计
	private static String[] ybqysrkm_a= new String[]{
		"NA","NA",
		"600101","NA","600102","600103","600104","600105",//（一）主营业务收入（3+5+6+7+8）
		"NA",
		"605101","NA","605102","605103","605104","605105",//  （二）其他业务收入（10+12+13+14+15）
		"NA",
		"630101","630102","630103","630104","630105","630106","630107","630108","630109","630110"//二、营业外收入（17+18+19+20+21+22+23+24+25+26）
	};
	//2013小会计
	private static String[] ybqysrkm_b= new String[]{
		"NA","NA",
		"500101","NA","500102","500103","500104","500105",//（一）主营业务收入（3+5+6+7+8）
		"NA",
		"505101","NA","505102","505103","505104","505105",//  （二）其他业务收入（10+12+13+14+15）
		"NA",
		"530101","530102","530103","530104","530105","530106","530107","530108","530109","530110"//二、营业外收入（17+18+19+20+21+22+23+24+25+26）
	};
	
	private static String[] ybqysrvoitem=new String[]{
		"vmny"
	};
	
	//一般企业成本
	private static String[] ybqycbitem=new String[]{
		"一、营业成本（2+9）",
		"　　（一）主营业务成本（3+5+6+7+8）",
		"　　　　1.销售商品成本",
		"　　　　　　其中:非货币性资产交换成本",
		"　　　　2.提供劳务成本",
		"　　　　3.建造合同成本",
		"　　　　4.让渡资产使用权成本",
		"　　　　5.其他",
		"　　（二）其他业务成本（10+12+13+14+15）",
		"　　　　1.材料销售成本",
		"　　　　　　其中:非货币性资产交换成本",
		"　　　　2.出租固定资产成本",
		"　　　　3.出租无形资产成本",
		"　　　　4.包装物出租成本",
		"　　　　5.其他",
		"二、营业外支出（17+18+19+20+21+22+23+24+25+26）",
		"　　（一）非流动资产处置损失",
		"　　（二）非货币性资产交换损失",
		"　　（三）债务重组损失",
		"　　（四）非常损失",
		"　　（五）捐赠支出",
		"　　（六）赞助支出",
		"　　（七）罚没支出",
		"　　（八）坏账损失",
		"　　（九）无法收回的债券股权投资损失",
		"　　（十）其他"
	};
	
	private static String[] ybqycbxm= new String[]{
		"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26"
	};
	//2007新会计
	private static String[] ybqycbkm_a= new String[]{
		"NA","NA",
		"640101","NA","640102","640103","640104","640105",//（一）主营业务成本（3+5+6+7+8）
		"NA",
		"640201","NA","640202","640203","640204","640205",//  （二）其他业务成本（10+12+13+14+15）
		"NA",
		"671101","671102","671103","671104","671105","671106","671107","671108","671109","671110"//二、营业外支出（17+18+19+20+21+22+23+24+25+26）
	};
	//2013小会计
	private static String[] ybqycbkm_b= new String[]{
		"NA","NA",
		"540101","NA","540102","540103","540104","540105",//（一）主营业务成本（3+5+6+7+8）
		"NA",
		"540201","NA","540202","540203","540204","540205",//  （二）其他业务成本（10+12+13+14+15）
		"NA",
		"571101","571102","571103","571104","571105","571106","571107","571108","571109","571110"//二、营业外支出（17+18+19+20+21+22+23+24+25+26）
	};
	
	private static String[] ybqycbvoitem=new String[]{
		"vmny"
	};
	
	
	//期间费用明细表
	private static String[] qjfyitem=new String[]{
		"一、职工薪酬",
		"二、劳务费",
		"三、咨询顾问费",
		"四、业务招待费",
		"五、广告费和业务宣传费",
		"六、佣金和手续费",
		"七、资产折旧摊销费",
		"八、财产损耗、盘亏及毁损损失",
		"九、办公费",
		"十、董事会费",
		"十一、租赁费",
		"十二、诉讼费",
		"十三、差旅费",
		"十四、保险费",
		"十五、运输、仓储费",
		"十六、修理费",
		"十七、包装费",
		"十八、技术转让费",
		"十九、研究费用",
		"二十、各项税费",
		"二十一、利息收支",
		"二十二、汇兑差额",
		"二十三、现金折扣",
		"二十四、其他",
		"合计(1+2+3+…24)"

	};
	
	private static String[] qjfyxm= new String[]{
		"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25"
	};
	//2007新会计(销售费用)
	/**
	 * STA=*
	 * FM=公式计算
	 * */
	private static String[] qjfykm_xsfy_a= new String[]{
		"660101","660102","660103","660104","660105","660106","660107","660108","660109","660110","660111","660112","660113","660114","660115","660116","660117","660118","660119","660120",
		"STA","STA","STA","660121",
		"FM"
	};
	//2013小会计(销售费用)
	private static String[] qjfykm_xsfy_b= new String[]{
		"560101","560102","560103","560104","560105","560106","560107","560108","560109","560110","560111","560112","560113","560114","560115","560116","560117","560118","560119","560120",
		"STA","STA","STA","560121",
		"FM"
	};
	//(销售费用境外支付)
	private static String[] qjfykm_xsfyjwzf= new String[]{
		STA,EDIT,EDIT,STA,STA,EDIT,STA,STA,STA,STA,EDIT,STA,STA,STA,EDIT,EDIT,STA,EDIT,EDIT,STA,
		STA,STA,STA,EDIT,
		FM
	};
	//2007新会计(管理费用)
	private static String[] qjfykm_glfy_a= new String[]{
		"660201","660202","660203","660204","660205","660206","660207","660208","660209","660210","660211","660212","660213","660214","660215","660216","660217","660218","660219","660220",
		STA,STA,STA,"660221",
		FM
	};
	//2013小会计(管理费用)
	private static String[] qjfykm_glfy_b= new String[]{
		"560201","560202","560203","560204","560205","560206","560207","560209","560210","560211","560212","560213","560214","560215","560216","560217","560218","560219","560220","560222",
		STA,STA,STA,"560221+560208",
		FM
	};
	//(管理费用境外支付)
	private static String[] qjfykm_glfyjwzf= new String[]{
		STA,EDIT,EDIT,STA,STA,EDIT,STA,STA,STA,STA,EDIT,STA,STA,STA,EDIT,EDIT,STA,EDIT,EDIT,STA,
		STA,STA,STA,EDIT,
		FM
	};
	//2007新会计(财务费用)
	private static String[] qjfykm_cwfy_a= new String[]{
		STA,STA,STA,STA,STA,"66030401",STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,
		"660301","660302","660303","660304-66030401",
		FM
	};
	//2013小会计(财务费用)
	private static String[] qjfykm_cwfy_b= new String[]{
		STA,STA,STA,STA,STA,"56030401",STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,
		"560301","560302","560303","560304-56030401",
		FM
	};
	//(财务费用境外支付)
	private static String[] qjfykm_cwfyjwzf= new String[]{
		STA,STA,STA,STA,STA,EDIT,STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,STA,
		EDIT,EDIT,STA,EDIT,
		FM
	};
	private static String[] qjfyvoitem=new String[]{
		"vxsfy","vxsfyjwzf","vglfy","vglfyjwzf","vcwfy","vcwfyjwzf"
	};
	
	
	
	
	//职工薪酬纳税调整明细表
	private static String[] zgxcitem=new String[]{
		"一、工资薪金支出",
		"　其中：股权激励",
		"二、职工福利费支出",
		"三、职工教育经费支出",
		"　其中：按税收规定比例扣除的职工教育经费",
		"　　按税收规定全额扣除的职工培训费用",
		"四、工会经费支出",
		"五、各类基本社会保障性缴款",
		"六、住房公积金",
		"七、补充养老保险",
		"八、补充医疗保险",
		"九、其他",
		"合计（1+3+4+7+8+9+10+11+12)",
		"已计提的职工教育经费支出",//为了计算用，实际报表上没有这一项

	};
	//项目编码
	private static String[] zgxcxm= new String[]{
		"1","2","3","5","6","4","7","8","9","10","11","12","13","14"
	};
	
/*	private static String[] zgxc_zzje_a= new String[]{//2007新会计-账载金额
		"66010101+66020101+50010102","NA","66010102+66020102","NA","NA","NA","66010104+66020104","66010105+66020105","66010106+66020106","66010107+66020107","66010108+66020108","66010109+66020109","","66010103+66020103"
		
	};
	private static String[] zgxc_zzje_b= new String[]{//2013小会计-账载金额
		"56010101+56020101+40010102","NA","56010102+56020102","NA","NA","NA","56010104+56020104","56010105+56020105","56010106+56020106","56010107+56020107","56010108+56020108","56010109+56020109","","56010103+56020103"
	};*/
	private static String[] zgxc_zzje_a= new String[]{//2007新会计-账载金额
		"66010101+66020101+50010102","NA","66010102+66020102","66010103+66020103","NA","NA","66010104+66020104","66010105+66020105","66010106+66020106","66010107+66020107","66010108+66020108","66010109+66020109","","66010103+66020103"
		
	};
	private static String[] zgxc_zzje_b= new String[]{//2013小会计-账载金额
		"56010101+56020101+40010102","NA","56010102+56020102","56010103+56020103","NA","NA","56010104+56020104","56010105+56020105","56010106+56020106","56010107+56020107","56010108+56020108","56010109+56020109","","56010103+56020103"
	};
	
	private static String[] zgxc_ssgdkcl= new String[]{//税收规定扣除率
		"STA","STA","=0.14","STA","=0.025","=1","=0.02","STA","STA","=0.05","=0.05","STA","STA","STA"
	};
	private static String[] zgxc_ljjzkc= new String[]{//以前年度累计结转扣除额
		"STA","STA","STA","NA","EDIT","STA","STA","STA","STA","STA","STA","STA","STA","STA"
	};
	private static String[] zgxc_ssje_a= new String[]{//税收金额
		"66010101+66020101+50010102","NA",
		"=IF('1#vssje'*'3#vssgdkcl'<'3#vzzje','1#vssje'*'3#vssgdkcl','3#vzzje')","=('5#vssje'+'6#vssje')",
		"=IF(('5#vzzje'+'5#vljjzkc')<'1#vssje'*'5#vssgdkcl',('5#vzzje'+'5#vljjzkc'),'1#vssje'*'5#vssgdkcl')",
		"=('6#vzzje'*'6#vssgdkcl')",
		"=IF('7#vzzje'<('1#vssje'*'7#vssgdkcl'),'7#vzzje',('1#vssje'*'7#vssgdkcl'))",
		"66010105+66020105","66010106+66020106","=IF('1#vssje'*'10#vssgdkcl'<'10#vzzje','1#vssje'*'10#vssgdkcl','10#vzzje')","=IF('1#vssje'*'11#vssgdkcl'<'11#vzzje','1#vzzje'*'11#vssgdkcl','11#vzzje')","66010109+66020109","NA","NA"
	};
	private static String[] zgxc_ssje_b= new String[]{//税收金额
		"56010101+56020101+40010102","NA",
		"=IF('1#vssje'*'3#vssgdkcl'<'3#vzzje','1#vssje'*'3#vssgdkcl','3#vzzje')","=('5#vssje'+'6#vssje')",
		"=IF(('5#vzzje'+'5#vljjzkc')<'1#vssje'*'5#vssgdkcl',('5#vzzje'+'5#vljjzkc'),'1#vssje'*'5#vssgdkcl')",
		"=('6#vzzje'*'6#vssgdkcl')",
		"=IF('7#vzzje'<('1#vssje'*'7#vssgdkcl'),'7#vzzje',('1#vssje'*'7#vssgdkcl'))",
//		"56010105+56020105","56010106+56020106","56010107+56020107","56010108+56020108","56010109+56020109","NA","NA"
		"='8#vzzje'","='9#vzzje'","=IF('1#vssje'*'10#vssgdkcl'<'10#vzzje','1#vssje'*'10#vssgdkcl','10#vzzje')","=IF('1#vssje'*'11#vssgdkcl'<'11#vzzje','1#vssje'*'11#vssgdkcl','11#vzzje')","='12#vzzje'","NA","NA"
	};
	private static String[] zgxc_nstzje= new String[]{//纳税调整金额
		"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"
	};
	private static String[] zgxc_jzkcje= new String[]{//累计结转以后年度扣除额
		"STA","STA","STA","NA","NA","STA","STA","STA","STA","STA","STA","NA","NA","STA"
	};
	private static String[] zgxcvoitem=new String[]{//BeanItem
		"vzzje","vssgdkcl","vljjzkc","vssje","vnstzje","vjzkcje"
	};
	
	
	//广告业务宣传
	private static String[] ggywxcitem=new String[]{
		"一、本年广告费和业务宣传费支出",
		"　　减：不允许扣除的广告费和业务宣传费支出",
		"二、本年符合条件的广告费和业务宣传费支出（1-2）",
		"三、本年计算广告费和业务宣传费扣除限额的销售（营业）收入",
		"　　税收规定扣除率",
		"四、本企业计算的广告费和业务宣传费扣除限额（4×5）",
		"五、本年结转以后年度扣除额（3＞6，本行=3-6；3≤6，本行=0）",
		"　　加：以前年度累计结转扣除额",
		" 　　减：本年扣除的以前年度结转额[3＞6，本行=0；3≤6，本行=8或（6-3）孰小值]",
		"六、按照分摊协议归集至其他关联方的广告费和业务宣传费（10≤3或6孰小值）",
		" 　　按照分摊协议从其他关联方归集至本企业的广告费和业务宣传费",
		"七、本年广告费和业务宣传费支出纳税调整金额（3＞6，本行=2+3-6+10-11；3≤6，本行=2+10-11-9）",
		"八、累计结转以后年度扣除额（7+8-9）"

	};
	
	private static String[] ggywxcxm= new String[]{
		"1","2","3","4","5","6","7","8","9","10","11","12","13"
	};
	//2007新会计
	private static String[] ggywxckm_a= new String[]{
		//"660105+660205",
		"=([A104000!5#vxsfy]+[A104000!5#vglfy])",
		"EDIT",
		"=('1#vmny'-'2#vmny')",
		"=([A101010!1#vmny])",
		"=0.15",
		"=('4#vmny'*'5#vmny')",
		"=IF('3#vmny'>'6#vmny','3#vmny'-'6#vmny',0)",
		"EDIT",
		"=IF('3#vmny'>'6#vmny',0,IF('8#vmny'<('6#vmny'-'3#vmny'),'8#vmny',('6#vmny'-'3#vmny')))",
		"EDIT",
		"EDIT",
		"=IF('3#vmny'>'6#vmny','2#vmny'+'3#vmny'-'6#vmny'+'10#vmny'-'11#vmny','2#vmny'+'10#vmny'-'11#vmny'-'9#vmny')",
		"=('7#vmny'+'8#vmny'-'9#vmny')"
	};
	//2013小会计
	private static String[] ggywxckm_b= new String[]{
//		"560105+560205",
		"=([A104000!5#vxsfy]+[A104000!5#vglfy])",
		"EDIT",
		"=('1#vmny'-'2#vmny')",
		"=([A101010!1#vmny])",
		"=0.15",
		"=('4#vmny'*'5#vmny')",
		"=IF('3#vmny'>'6#vmny','3#vmny'-'6#vmny',0)",
		"EDIT",
		"=IF('3#vmny'>'6#vmny',0,IF('8#vmny'<('6#vmny'-'3#vmny'),'8#vmny',('6#vmny'-'3#vmny')))",
		"EDIT",
		"EDIT",
		"=IF('3#vmny'>'6#vmny','2#vmny'+'3#vmny'-'6#vmny'+'10#vmny'-'11#vmny','2#vmny'+'10#vmny'-'11#vmny'-'9#vmny')",
		"=('7#vmny'+'8#vmny'-'9#vmny')"
	};
	
	private static String[] ggywxcvoitem=new String[]{
		"vmny"
	};
	
	
	//资产折旧、摊销情况及纳税调整明细表  
	private static String[] zjtxtzitem=new String[]{
		"一、固定资产（2+3+4+5+6+7）",
		"　　（一）房屋、建筑物",
		"　　（二）飞机、火车、轮船、机器、机械和其他生产设备",
		"　　（三）与生产经营活动有关的器具、工具、家具等",
		"　　（四）飞机、火车、轮船以外的运输工具",
		"　　（五）电子设备",
		"　　（六）其他",
		"二、生产性生物资产（9+10）",
		"　　（一）林木类",
		"　　（二）畜类",
		"三、无形资产（12+13+14+15+16+17+18）",
		"　　（一）专利权",
		"　　（二）商标权",
		"　　（三）著作权",
		"　　（四）土地使用权",
		"　　（五）非专利技术",
		"　　（六）特许权使用费",
		"　　（七）其他",
		"四、长期待摊费用（20+21+22+23+24）",
		"　　（一）已足额提取折旧的固定资产的改建支出",
		"　　（二）租入固定资产的改建支出",
		"　　（三）固定资产的大修理支出",
		"　　（四）开办费",
		"　　（五）其他",
		"五、油气勘探投资",
		"六、油气开发投资",
		"合计（1+8+11+19+25+26）"


	};
	
	private static String[] zjtxtzxm= new String[]{
		"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27"
	};
	//2007新会计
	private static String[] zjtxtz_1= new String[]{//资产账载金额
		"NA","160101_NMJF","160102_NMJF","160103_NMJF","160104_NMJF","160105_NMJF","160106_NMJF","NA","162101_NMJF","162102_NMJF","NA","170101_NMJF","170102_NMJF","170103_NMJF","170104_NMJF","170105_NMJF","170106_NMJF","170107_NMJF","NA","180101_NMJF","180102_NMJF","180103_NMJF","180104_NMJF","180105_NMJF","EDIT","EDIT","NA"
	};
	private static String[] zjtxtz_2= new String[]{//本年折旧、摊销额
		"NA","160201_DF","160202_DF","160203_DF","160204_DF","160205_DF","160206_DF","NA","EDIT","EDIT","NA","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","1702_DF","NA","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","NA"
	};	
	private static String[] zjtxtz_3= new String[]{//累计折旧、摊销额
		"NA","160201_NMDF","160202_NMDF","160203_NMDF","160204_NMDF","160205_NMDF","160206_NMDF","NA","EDIT","EDIT","NA","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","1702_NMDF","NA","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","NA"
	};
	private static String[] zjtxtz_4= new String[]{//资产计税基础
//		"NA","='2#vzzmny'","='3#vzzmny'","='4#vzzmny'","='5#vzzmny'","='6#vzzmny'","='7#vzzmny'","NA","='9#vzzmny'","='10#vzzmny'","NA","='12#vzzmny'","='13#vzzmny'","='14#vzzmny'","='15#vzzmny'","='16#vzzmny'","='17#vzzmny'","='18#vzzmny'","NA","='20#vzzmny'","='21#vzzmny'","='22#vzzmny'","='23#vzzmny'","='24#vzzmny'","='25#vzzmny'","='26#vzzmny'","NA"
		"NA","160101_NMJF","160102_NMJF","160103_NMJF","160104_NMJF","160105_NMJF","160106_NMJF","NA","162101_NMJF","162102_NMJF","NA","170101_NMJF","170102_NMJF","170103_NMJF","170104_NMJF","170105_NMJF","170106_NMJF","170107_NMJF","NA","180101_NMJF","180102_NMJF","180103_NMJF","180104_NMJF","180105_NMJF","EDIT","EDIT","NA"
	};
	private static String[] zjtxtz_5= new String[]{//按税收一般规定计算的本年折旧、摊销额
//		"NA","='2#vbnzjmny'","='3#vbnzjmny'","='4#vbnzjmny'","='5#vbnzjmny'","='6#vbnzjmny'","='7#vbnzjmny'","NA","='9#vbnzjmny'","='10#vbnzjmny'","NA","='12#vbnzjmny'","='13#vbnzjmny'","='14#vbnzjmny'","='15#vbnzjmny'","='16#vbnzjmny'","='17#vbnzjmny'","='18#vbnzjmny'","NA","='20#vbnzjmny'","='21#vbnzjmny'","='22#vbnzjmny'","='23#vbnzjmny'","='24#vbnzjmny'","='25#vbnzjmny'","='26#vbnzjmny'","NA"
		"NA","160201_DF","160202_DF","160203_DF","160204_DF","160205_DF","160206_DF","NA","EDIT","EDIT","NA","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","1702_DF","NA","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","NA"
	};
	private static String[] zjtxtz_6= new String[]{//本年加速折旧额
		"NA","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","NA","EDIT","EDIT","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","NA"
	};
	private static String[] zjtxtz_7= new String[]{//其中：2014年及以后年度新增固定资产加速折旧额
		"NA","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","NA"
	};
	private static String[] zjtxtz_8= new String[]{//累计折旧、摊销额
//		"NA","='2#vljzjmny'","='3#vljzjmny'","='4#vljzjmny'","='5#vljzjmny'","='6#vljzjmny'","='7#vljzjmny'","NA","='9#vljzjmny'","='10#vljzjmny'","NA","='12#vljzjmny'","='13#vljzjmny'","='14#vljzjmny'","='15#vljzjmny'","='16#vljzjmny'","='17#vljzjmny'","='18#vljzjmny'","NA","='20#vljzjmny'","='21#vljzjmny'","='22#vljzjmny'","='23#vljzjmny'","='24#vljzjmny'","='25#vljzjmny'","='26#vljzjmny'","NA"
		"NA","160201_NMDF","160202_NMDF","160203_NMDF","160204_NMDF","160205_NMDF","160206_NMDF","NA","EDIT","EDIT","NA","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","1702_NMDF","NA","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","NA"
	};
	private static String[] zjtxtz_9= new String[]{//金额
		"NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA"
	};
	private static String[] zjtxtz_10= new String[]{//调整原因
		"STA","NA","NA","NA","NA","NA","NA","NA","NA","NA","STA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","STA"
	};
	
	private static String[] zjtxtzvoitem=new String[]{
		"vzzmny","vbnzjmny","vljzjmny","vjsjcmny","vbnzj2mny","vjszjmny","vjszj2mny","vljzj2mny","vnstzmny","ctzyy"
	};
	
	
	//企业所得税弥补亏损明细表
	private static String[] mbksitem=new String[]{
		"9999",
		"前五年度",
		"前四年度",
		"前三年度",
		"前二年度",
		"前一年度",
		"本年度",
		"可结转以后年度弥补的亏损额合计"
	};
	
	private static String[] mbksxm= new String[]{
		//提前计算公式用，否则会有空指针
		"1","2","3","4","5","6","7","8"
	};
	//2007新会计
	private static String[] mbks_1= new String[]{//年度
		"=9999",
		"NA","NA","NA","NA","NA","NA","NA"
	};
	private static String[] mbks_2= new String[]{//纳税调整后所得
		"=IF([A100000!19#vmny]>0,"
		+ "IF([A100000!20#vmny]>=0,"
		+ "IF(([A100000!19#vmny]-[A100000!20#vmny]-[A100000!21#vmny])>0,"
		+ "([A100000!19#vmny]-[A100000!20#vmny]-[A100000!21#vmny]),"
		+ "0),"
		+ "[A100000!19#vmny]),"
		+ "[A100000!19#vmny])",
		"EDIT","EDIT","EDIT","EDIT","EDIT",
		"='1#vnstzsd'",
		"NA"
	};
	private static String[] mbks_3= new String[]{//合并、分立转入（转出）可弥补的亏损额
		"NA",
		"EDIT","EDIT","EDIT","EDIT","EDIT","NA","NA"
	};
	private static String[] mbks_4= new String[]{//当年可弥补的亏损额
//		"NA",
//		"=IF('1#vnstzsd'>0,'1#vkmbks',('1#vnstzsd'+'1#vkmbks'))",
//		"=IF('2#vnstzsd'>0,'2#vkmbks',('2#vnstzsd'+'2#vkmbks'))",
//		"=IF('3#vnstzsd'>0,'3#vkmbks',('3#vnstzsd'+'3#vkmbks'))",
//		"=IF('4#vnstzsd'>0,'4#vkmbks',('4#vnstzsd'+'4#vkmbks'))",
//		"=IF('5#vnstzsd'>0,'5#vkmbks',('5#vnstzsd'+'5#vkmbks'))",
//		"=IF('6#vnstzsd'>0,'6#vkmbks',('6#vnstzsd'+'6#vkmbks'))",
//		"NA"
		"NA",
		"=IF('2#vnstzsd'>0,'2#vkmbks',('2#vnstzsd'+'2#vkmbks'))",
		"=IF('3#vnstzsd'>0,'3#vkmbks',('3#vnstzsd'+'3#vkmbks'))",
		"=IF('4#vnstzsd'>0,'4#vkmbks',('4#vnstzsd'+'4#vkmbks'))",
		"=IF('5#vnstzsd'>0,'5#vkmbks',('5#vnstzsd'+'5#vkmbks'))",
		"=IF('6#vnstzsd'>0,'6#vkmbks',('6#vnstzsd'+'6#vkmbks'))",
		"=IF('7#vnstzsd'>0,'7#vkmbks',('7#vnstzsd'+'7#vkmbks'))",
		"NA"
	};
	private static String[] mbks_5= new String[]{//以前年度亏损已弥补额(前四年度)
		"NA",
		"EDIT","STA","STA","STA","STA","STA","NA"
	};
	private static String[] mbks_6= new String[]{//以前年度亏损已弥补额(前三年度)
		"NA",
		"EDIT","EDIT","STA","STA","STA","STA","NA"
	};
	private static String[] mbks_7= new String[]{//以前年度亏损已弥补额(前二年度)
		"NA",
		"EDIT","EDIT","EDIT","STA","STA","STA","NA"
	};
	private static String[] mbks_8= new String[]{//以前年度亏损已弥补额(前一年度)
		"NA",
		"EDIT","EDIT","EDIT","EDIT","STA","STA","NA"
	};
	private static String[] mbks_9= new String[]{//以前年度亏损已弥补额(合计)
		"NA",
		"=IF('2#vdnkmbks'>0,0,'2#vprefourth'+'2#vprethird'+'2#vpresecond'+'2#vprefirst')",
		"=IF('3#vdnkmbks'>0,0,'3#vprethird'+'3#vpresecond'+'3#vprefirst')",
		"=IF('4#vdnkmbks'>0,0,'4#vpresecond'+'4#vprefirst')",
		"=IF('5#vdnkmbks'>0,0,'5#vprefirst')","STA","STA","NA"
	};
	private static String[] mbks_10= new String[]{//本年度实际弥补的以前年度亏损额
//		"NA",
//		"=IF('6#vnstzsd'<=0,0,IF('1#vdnkmbks'>=0,0,IF((-1*'1#vdnkmbks')-'1#vtotal'<='6#vnstzsd',(-1*'1#vdnkmbks')-'1#vtotal','6#vnstzsd')))",
//		"=IF('6#vnstzsd'<=0,0,IF('2#vdnkmbks'>=0,0,IF((-1*'2#vdnkmbks')-'2#vtotal'<='6#vnstzsd'-'1#vsjksmb',(-1*'2#vdnkmbks')-'2#vtotal','6#vnstzsd'-'1#vsjksmb')))",
//		"=IF('6#vnstzsd'<=0,0,IF('3#vdnkmbks'>=0,0,IF((-1*'3#vdnkmbks')-'3#vtotal'<='6#vnstzsd'-'1#vsjksmb'-'2#vsjksmb',(-1*'3#vdnkmbks')-'3#vtotal','6#vnstzsd'-'1#vsjksmb'-'2#vsjksmb')))",
//		"=IF('6#vnstzsd'<=0,0,IF('4#vdnkmbks'>=0,0,IF((-1*'4#vdnkmbks')-'4#vtotal'<='6#vnstzsd'-'1#vsjksmb'-'2#vsjksmb'-'3#vsjksmb',(-1*'4#vdnkmbks')-'4#vtotal','6#vnstzsd'-'1#vsjksmb'-'2#vsjksmb'-'3#vsjksmb')))",
//		"=IF('6#vnstzsd'<=0,0,IF('5#vdnkmbks'>=0,0,IF((-1*'5#vdnkmbks')<='6#vnstzsd'-'1#vsjksmb'-'2#vsjksmb'-'3#vsjksmb'-'4#vsjksmb',(-1*'5#vdnkmbks'),'6#vnstzsd'-'1#vsjksmb'-'2#vsjksmb'-'3#vsjksmb'-'4#vsjksmb')))",
//		"=('1#vsjksmb'+'2#vsjksmb'+'3#vsjksmb'+'4#vsjksmb'+'5#vsjksmb')",
//		"NA"
		"NA",
		"=IF('1#vnstzsd'<=0,0,IF('2#vdnkmbks'>=0,0,IF((-1*'2#vdnkmbks')-'2#vtotal'<='1#vnstzsd',(-1*'2#vdnkmbks')-'2#vtotal','1#vnstzsd')))",
		"=IF('1#vnstzsd'<=0,0,IF('3#vdnkmbks'>=0,0,IF((-1*'3#vdnkmbks')-'3#vtotal'<='1#vnstzsd'-'2#vsjksmb',(-1*'3#vdnkmbks')-'3#vtotal','1#vnstzsd'-'2#vsjksmb')))",
		"=IF('1#vnstzsd'<=0,0,IF('4#vdnkmbks'>=0,0,IF((-1*'4#vdnkmbks')-'4#vtotal'<='1#vnstzsd'-'2#vsjksmb'-'3#vsjksmb',(-1*'4#vdnkmbks')-'4#vtotal','1#vnstzsd'-'2#vsjksmb'-'3#vsjksmb')))",
		"=IF('1#vnstzsd'<=0,0,IF('5#vdnkmbks'>=0,0,IF((-1*'5#vdnkmbks')-'5#vtotal'<='1#vnstzsd'-'2#vsjksmb'-'3#vsjksmb'-'4#vsjksmb',(-1*'5#vdnkmbks')-'5#vtotal','1#vnstzsd'-'2#vsjksmb'-'3#vsjksmb'-'4#vsjksmb')))",
		"=IF('1#vnstzsd'<=0,0,IF('6#vdnkmbks'>=0,0,IF((-1*'6#vdnkmbks')<='1#vnstzsd'-'2#vsjksmb'-'3#vsjksmb'-'4#vsjksmb'-'5#vsjksmb',(-1*'6#vdnkmbks'),'1#vnstzsd'-'2#vsjksmb'-'3#vsjksmb'-'4#vsjksmb'-'5#vsjksmb')))",
		"=('2#vsjksmb'+'3#vsjksmb'+'4#vsjksmb'+'5#vsjksmb'+'6#vsjksmb')",
		"NA"
	};
	private static String[] mbks_11= new String[]{//可结转以后年度弥补的亏损额
//		"NA",
//		"STA",
//		"=IF('2#vdnkmbks'>0,0,-1*'2#vdnkmbks'-'2#vtotal'-'2#vsjksmb')",
//		"=IF('3#vdnkmbks'>0,0,-1*'3#vdnkmbks'-'3#vtotal'-'3#vsjksmb')",
//		"=IF('4#vdnkmbks'>0,0,-1*'4#vdnkmbks'-'4#vtotal'-'4#vsjksmb')",
//		"=IF('5#vdnkmbks'>0,0,-1*'5#vdnkmbks'-'5#vsjksmb')",
//		"=IF('6#vnstzsd'>0,0,-1*'6#vnstzsd'-'6#vsjksmb')",
//		"=('2#vkjzksmb'+'3#vkjzksmb'+'4#vkjzksmb'+'5#vkjzksmb'+'6#vkjzksmb')"
		"NA",
		"STA",
		"=IF('3#vdnkmbks'>0,0,-1*'3#vdnkmbks'-'3#vtotal'-'3#vsjksmb')",
		"=IF('4#vdnkmbks'>0,0,-1*'4#vdnkmbks'-'4#vtotal'-'4#vsjksmb')",
		"=IF('5#vdnkmbks'>0,0,-1*'5#vdnkmbks'-'5#vtotal'-'5#vsjksmb')",
		"=IF('6#vdnkmbks'>0,0,-1*'6#vdnkmbks'-'6#vsjksmb')",
		"=IF('7#vnstzsd'>0,0,-1*'7#vnstzsd'-'7#vsjksmb')",
		"=('3#vkjzksmb'+'4#vkjzksmb'+'5#vkjzksmb'+'6#vkjzksmb'+'7#vkjzksmb')"
	};
	
	private static String[] mbksvoitem=new String[]{
		"cnd","vnstzsd","vkmbks","vdnkmbks","vprefourth","vprethird","vpresecond","vprefirst","vtotal","vsjksmb","vkjzksmb"
	};
	
	
	//减免所得税优惠明细表
	private static String[] jmsdsitem=new String[]{
		"一、符合条件的小型微利企业",
		"    其中：减半征税",
		"二、国家需要重点扶持的高新技术企业（4+5）",
		"   （一）高新技术企业低税率优惠（填写A107041）",
		"   （二）经济特区和上海浦东新区新设立的高新技术企业定期减免（填写A107041）",
		"三、其他专项优惠（7+8+9+10+11…+14+15+16+…+31）",
		"   （一）受灾地区损失严重的企业（7.1+7.2+7.3）",
		"　　	其中：1.",
		"　　　　　 2.",
		"　　　　　 3.",
		"   （二）受灾地区农村信用社（8.1+8.2+8.3）",
		"　　	其中：1.",
		"　　　　　 2.",
		"　　　　　 3.",
		"（三）受灾地区的促进就业企业（9.1+9.2+9.3）",
		"　　	其中：1.",
		"　　　　　 2.",
		"　　　　　 3.",
		"（四）支持和促进重点群体创业就业企业(10.1+10.2+10.3)",
		"　	      　其中:1.下岗失业人员再就业",
		"　　　　　2.高校毕业生就业",
		"　　　　　3.退役士兵就业",
		"（五）技术先进型服务企业",
		"（六）动漫企业",
		"（七）集成电路线宽小于0.8微米（含）的集成电路生产企业",
		"（八）集成电路线宽小于0.25微米的集成电路生产企业（14.1+14.2）",
		"　	      　其中：1.定期减免企业所得税",
		"　　　　　2.减按15%税率征收企业所得税",
		"	（九）投资额超过80亿元人民币的集成电路生产企业（15.1+15.2）",
		"		其中：1.定期减免企业所得税",
		"　　　2.减按15%税率征收企业所得税",
		"	（十）新办集成电路设计企业（填写A107042）",
		"	（十一）国家规划布局内重点集成电路设计企业",
		"	（十二）集成电路封装、测试企业",
		"	（十三）集成电路关键专用材料生产企业或集成电路专用设备生产企业",
		"	（十四）符合条件的软件企业（填写A107042）",
		"	（十五）国家规划布局内重点软件企业",
		"	（十六）经营性文化事业单位转制企业",
		"	（十七）符合条件的生产和装配伤残人员专门用品企业",
		"	（十八）设在西部地区的鼓励类产业企业",
		"	（十九）新疆困难地区新办企业",
		"	（二十）新疆喀什、霍尔果斯特殊经济开发区新办企业",
		"	（二十一）横琴新区、平潭综合实验区和前海深港现代化服务业合作区企业",
		"	（二十二）享受过渡期税收优惠企业",
		"   （二十三）其他1",
		"	（二十四）其他2",
		"	（二十五）其他3",
		"四、减：项目所得额按法定税率减半征收企业所得税叠加享受减免税优惠",
		"五、减免地方分享所得税的民族自治地方企业",
		"合计：（1+3+6-32+33）",
	};
	private static String[] jmsdsxm= new String[]{
		"1","2","3","4","5","6","7","8","9","10",
		"11","12","13","14","15","16","17","18","19","20",
		"21","22","23","24","25","26","27","28","29","30",
		"31","32","33","34","35","36","37","38","39","40",
		"41","42","43","44","45","46","47","48","49","50"
	};
	private static String[] jmsdsxm_hc= new String[]{
		"1","2","3","4","5","6","7","7.1","7.2","7.3",
		"8","8.1","8.2","8.3","9","9.1","9.2","9.3","10","10.1",
		"10.2","10.3","11","12","13","14","14.1","14.2","15","15.1",
		"15.2","16","17","18","19","20","21","22","23","24",
		"25","26","27","28","29","30","31","32","33","34"
	}; 
	//2007新会计
	private static String[] jmsdsje= new String[]{//金额
		"=[A100000!23#vmny]","=[A100000!23#vmny]","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT",
		"EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT",
		"EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT",
		"EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT",
		"EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT"
		
	};
	
	private static String[] jmsdsvoitem=new String[]{
		"vmny"
	};
	
	
	//捐赠支出纳税调整
	private static String[] jzzcnstzitem=new String[]{
		"","","","","","","","","","","","","","","","","","","","合计"
	};
	
	private static String[] jzzcnstzxm= new String[]{
		"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"
	};
	//2007新会计
	private static String[] jzzcnstz_1= new String[]{//账载金额
		"EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT",
		//"NA"
		"=('1#vgyzzje'+'2#vgyzzje'+'3#vgyzzje'+'4#vgyzzje'+'5#vgyzzje'+'6#vgyzzje'+'7#vgyzzje'+'8#vgyzzje'+'9#vgyzzje'+'10#vgyzzje'"
		+ "+'11#vgyzzje'+'12#vgyzzje'+'13#vgyzzje'+'14#vgyzzje'+'15#vgyzzje'+'16#vgyzzje'+'17#vgyzzje'+'18#vgyzzje'+'19#vgyzzje')"
	};
	private static String[] jzzcnstz_2= new String[]{//按税收规定计算的扣除限额
		"STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","=IF([A100000!13#vmny]>0,[A100000!13#vmny]*0.12,0)"
	};
	private static String[] jzzcnstz_3= new String[]{//税收金额
		"STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","=IF('20#vgyzzje'<'20#vgykcxe','20#vgyzzje','20#vgykcxe')"
	};
	private static String[] jzzcnstz_4= new String[]{//纳税调整金额
		"STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","=('20#vgyzzje'-'20#vgyssje')"
	};
	private static String[] jzzcnstz_5= new String[]{//账载金额-非公益捐赠
		"EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT","EDIT",
//		"NA"
		"=('1#vfgyzzje'+'2#vfgyzzje'+'3#vfgyzzje'+'4#vfgyzzje'+'5#vfgyzzje'+'6#vfgyzzje'+'7#vfgyzzje'+'8#vfgyzzje'+'9#vfgyzzje'+'10#vfgyzzje'"
		+ "+'11#vfgyzzje'+'12#vfgyzzje'+'13#vfgyzzje'+'14#vfgyzzje'+'15#vfgyzzje'+'16#vfgyzzje'+'17#vfgyzzje'+'18#vfgyzzje'+'19#vfgyzzje')"
	};
	private static String[] jzzcnstz_6= new String[]{//纳税调整金额
		"STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","STA","=('20#vgynstzje'+'20#vfgyzzje')"
	};
	private static String[] jzzcnstzvoitem=new String[]{
		"vgyzzje","vgykcxe","vgyssje","vgynstzje","vfgyzzje","vnstzje"
	};
	
	//纳税调整项目明细表
	private static String[] nstzmxitem=new String[]{
		"一、收入类调整项目（2+3+4+5+6+7+8+10+11）",
		"　　（一）视同销售收入（填写A105010）",
		"　　（二）未按权责发生制原则确认的收入（填写A105020）",
		"　　（三）投资收益（填写A105030）",
		"　　（四）按权益法核算长期股权投资对初始投资成本调整确认收益",
		"　　（五）交易性金融资产初始投资调整  ",
		"　　（六）公允价值变动净损益",
		"　　（七）不征税收入",
		"　　　　其中：专项用途财政性资金（填写A105040）",
		"　　（八）销售折扣、折让和退回",
		"　　（九）其他",
		"二、扣除类调整项目（13+14+15+16+17+18+19+20+21+22+23+24+26+27+28+29）",
		"　　（一）视同销售成本（填写A105010）",
		"　　（二）职工薪酬（填写A105050）",
		"　　（三）业务招待费支出",
		"　　（四）广告费和业务宣传费支出（填写A105060）",
		"　　（五）捐赠支出（填写A105070）",
		"　　（六）利息支出",
		"　　（七）罚金、罚款和被没收财物的损失",
		"　　（八）税收滞纳金、加收利息",
		"　　（九）赞助支出",
		"　　（十）与未实现融资收益相关在当期确认的财务费用",
		"　　（十一）佣金和手续费支出",
		"　　（十二）不征税收入用于支出所形成的费用 ",
		"　　　　其中：专项用途财政性资金用于支出所形成的费用（填写A105040）",
		"　　（十三）跨期扣除项目",
		"　　（十四）与取得收入无关的支出",
		"　　（十五）境外所得分摊的共同支出",
		"　　（十六）其他",
		"三、资产类调整项目（31+32+33+34）",
		"　　（一）资产折旧、摊销 （填写A105080）",
		"　　（二）资产减值准备金",
		"　　（三）资产损失（填写A105090）",
		"　　（四）其他",
		"四、特殊事项调整项目（36+37+38+39+40）",
		"　  （一）企业重组（填写A105100）",
		"　　（二）政策性搬迁（填写A105110）",
		"　　（三）特殊行业准备金（填写A105120）",
		"　　（四）房地产开发企业特定业务计算的纳税调整额(填写A105010)",
		"　　（五）其他",
		"五、特别纳税调整应税所得",
		"六、其他",
		"合计（1+12+30+35+41+42）"

	};
	
	private static String[] nstzmxxm= new String[]{
		"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19",
		"20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40",
		"41","42","43"
	}; 
	//2007新会计
	private static String[] nstzmx_zzje= new String[]{//账载金额
		"STA","NA","NA","NA","NA","NA","NA","NA","NA","NA","NA","STA","NA","=([A105050!13#vzzje])","=([A104000!4#vxsfy]+[A104000!4#vglfy])","=([A104000!5#vxsfy]+[A104000!5#vglfy])","=([A105070!20#vgyzzje]+[A105070!20#vfgyzzje])","NA","NA",
		"NA","NA","NA","=([A104000!6#vxsfy]+[A104000!6#vglfy]+[A104000!6#vcwfy])","NA","NA","NA","NA","NA","NA","STA","=([A105080!27#vbnzjmny])","NA","NA","NA","STA","NA","STA","NA","STA","STA",
		"STA","STA","STA"
	};
	private static String[] nstzmx_ssje= new String[]{//税收金额
		"STA","NA","NA","NA","STA","STA","STA","STA","STA","NA","NA","STA","NA","=([A105050!13#vssje])","=IF(('15#vzzje'*0.6)<[A100000!1#vmny]*0.005,('15#vzzje'*0.6),[A100000!1#vmny]*0.005)","STA","=([A105070!20#vgyssje])","='18#vzzje'","STA",
		"STA","STA","='22#vzzje'","='23#vzzje'","STA","STA","NA","STA","STA","NA","STA","=([A105080!27#vbnzj2mny]+[A105080!27#vjszjmny])","STA","NA","NA","STA","NA","STA","NA","NA","STA",
		"STA","STA","STA"
	};
	private static String[] nstzmx_tzje= new String[]{//调增金额
		"NA","NA","NA","NA","STA","NA","=IF('7#vzzje'<0,-1*'7#vzzje',0)","NA","NA","=IF(('10#vzzje'-'10#vssje')>0,'10#vzzje'-'10#vssje',0)","=IF(('11#vzzje'-'11#vssje')>0,'11#vzzje'-'11#vssje',0)","NA","STA",
		"=IF([A105050!13#vnstzje]>0,[A105050!13#vnstzje],0)","=IF('15#vzzje'-'15#vssje'>0,'15#vzzje'-'15#vssje',0)","=IF([A105060!12#vmny]>0,[A105060!12#vmny],0)","=([A105070!20#vnstzje])","=IF('18#vzzje'-'18#vssje'>0,'18#vzzje'-'18#vssje',0)","='19#vzzje'",
		"='20#vzzje'","='21#vzzje'","=IF('22#vzzje'-'22#vssje'>0,'22#vzzje'-'22#vssje',0)","='23#vzzje'-'23#vssje'","NA","NA","=IF('26#vzzje'-'26#vssje'>0,'26#vzzje'-'26#vssje',0)","='27#vzzje'","NA","=IF('29#vzzje'-'29#vssje'>0,'29#vzzje'-'29#vssje',0)","NA","=IF([A105080!27#vnstzmny]>0,[A105080!27#vnstzmny],0)","='32#vzzje'","NA","=IF('34#vzzje'-'34#vssje'>0,'34#vzzje'-'34#vssje',0)","NA","NA","NA","NA","NA","NA",
		"NA","NA","NA"
	};
	private static String[] nstzmx_tjje= new String[]{//调减金额
		"NA","STA","NA","NA","NA","STA","='7#vzzje'","NA","NA","=IF('10#vzzje'-'10#vssje'<0,-1*('10#vzzje'-'10#vssje'),0)","=IF('11#vzzje'-'11#vssje'<0,-1*('11#vzzje'-'11#vssje'),0)","NA","NA","=IF([A105050!13#vnstzje]<0,-1*[A105050!13#vnstzje],0)","STA","=IF([A105060!12#vmny]<0,-1*[A105060!12#vmny],0)","STA","=IF('18#vzzje'-'18#vsje'<0,-1*('18#vzzje'-'18#vssje'),0)","STA",
		"STA","STA","=IF('22#vzzje'-'22#vssje'<0,-1*('22#vzzje'-'22#vssje'),0)","STA","STA","STA","=IF('26#vzzje'-'26#vssje'<0,-1*('26#vzzje'-'26#vssje'),0)","STA","STA","=IF('29#vzzje'-'29#vssje'<0,-1*('29#vzzje'-'29#vssje'),0)","NA","=IF(-1*[A105080!27#vnstzmny]>0,-1*[A105080!27#vnstzmny],0)","=IF('32#vssje'<0,-1*('32#vssje'),0)","NA","=IF('34#vzzje'-'34#vssje'<0,-1*('34#vzzje'-'34#vssje'),0)","NA","NA","NA","NA","NA","NA",
		"NA","NA","NA"
	};
	
	private static String[] nstzmxvoitem=new String[]{
		"vzzje","vssje","vtzje","vtjje"
	};
	
	
	//中华人民共和国企业所得税年度纳税申报表（A类）
	private static String[] nssbitem=new String[]{
			"一、营业收入(填写A101010\\101020\\103000)",
			"　　减：营业成本(填写A102010\\102020\\103000)",
			"　　　　税金及附加",
			"　　　　销售费用(填写A104000)",
			"　　　　管理费用(填写A104000)",
			"　　　　财务费用(填写A104000)",
			"　　　　资产减值损失",
			"　　加：公允价值变动收益",
			" 　　　　投资收益",
			"二、营业利润(1-2-3-4-5-6-7+8+9)",
			"　　加：营业外收入(填写A101010\\101020\\103000)",
			"　　减：营业外支出(填写A102010\\102020\\103000)",
			"三、利润总额（10+11-12）",
			"　　减：境外所得（填写A108010）",
			"　　加：纳税调整增加额（填写A105000）",
			"　　减：纳税调整减少额（填写A105000）",
			"　　减：免税、减计收入及加计扣除（填写A107010）",
			"　　加：境外应税所得抵减境内亏损（填写A108000）",
			"四、纳税调整后所得（13-14+15-16-17+18）",
			"　　减：所得减免（填写A107020）",
			"　　减：抵扣应纳税所得额（填写A107030）",
			"　　减：弥补以前年度亏损（填写A106000）",
			"五、应纳税所得额（19-20-21-22）",
			"　　税率（25%）",
			"六、应纳所得税额（23×24）",
			"　　减：减免所得税额（填写A107040）",
			"　　减：抵免所得税额（填写A107050）",
			"七、应纳税额（25-26-27）",
			"　　加：境外所得应纳所得税额（填写A108000）",
			"　　减：境外所得抵免所得税额（填写A108000）",
			"八、实际应纳所得税额（28+29-30）",
			"　　减：本年累计实际已预缴的所得税额",
			"九、本年应补（退）所得税额（31-32）",
			"　　其中：总机构分摊本年应补（退）所得税额(填写A109000)",
			"　　　　财政集中分配本年应补（退）所得税额（填写A109000）",
			"　　　　总机构主体生产经营部门分摊本年应补（退）所得税额(填写A109000)",
			"以前年度多缴的所得税额在本年抵减额",
			"以前年度应缴未缴在本年入库所得税额",
	};
	
	private static String[] nssbxm= new String[]{
		"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38"
	};
	
	//2007新会计
	private static String[] nssbkm_a= new String[]{
		"=[A101010!1#vmny]","=[A102010!1#vmny]","6403_JF","=[A104000!25#vxsfy]","=[A104000!25#vglfy]","=[A104000!25#vcwfy]",
		"6701_JF","6101_DF","6111_DF",
		"=('1#vmny'-'2#vmny'-'3#vmny'-'4#vmny'-'5#vmny'-'6#vmny'-'7#vmny'+'8#vmny'+'9#vmny')",
		"=[A101010!16#vmny]","=[A102010!16#vmny]",
		"=('10#vmny'+'11#vmny'-'12#vmny')",
		"EDIT","=[A105000!43#vtzje]","=[A105000!43#vtjje]",
		"EDIT","EDIT",
		"=('13#vmny'-'14#vmny'+'15#vmny'-'16#vmny'-'17#vmny'+'18#vmny')",
		"EDIT","EDIT","=[A106000!7#vsjksmb]",
		"=IF('19#vmny'-'20#vmny'-'21#vmny'-'22#vmny'>0,'19#vmny'-'20#vmny'-'21#vmny'-'22#vmny',0)","=0.25",
		"=('23#vmny'*'24#vmny')","=[A107040!50#vmny]","EDIT",
		"=('25#vmny'-'26#vmny'-'27#vmny')","EDIT","EDIT",
		"=('28#vmny'+'29#vmny'-'30#vmny')",
		"6711_DF",
		"=('31#vmny'-'32#vmny')",
		"EDIT","EDIT","EDIT","EDIT","EDIT"
	};
	//2013小会计
	private static String[] nssbkm_b= new String[]{
		
		"=[A101010!1#vmny]","=[A102010!1#vmny]","5403_JF","=[A104000!25#vxsfy]","=[A104000!25#vglfy]","=[A104000!25#vcwfy]",
		"6701_JF","6101_DF","5111_DF",
		"=('1#vmny'-'2#vmny'-'3#vmny'-'4#vmny'-'5#vmny'-'6#vmny'-'7#vmny'+'8#vmny'+'9#vmny')",
		"=[A101010!16#vmny]","=[A102010!16#vmny]",
		"=('10#vmny'+'11#vmny'-'12#vmny')",
		"EDIT","=[A105000!43#vtzje]","=[A105000!43#vtjje]",
		"EDIT","EDIT",
		"=('13#vmny'-'14#vmny'+'15#vmny'-'16#vmny'-'17#vmny'+'18#vmny')",
		"EDIT","EDIT","=[A106000!7#vsjksmb]",
		"=IF('19#vmny'-'20#vmny'-'21#vmny'-'22#vmny'>0,'19#vmny'-'20#vmny'-'21#vmny'-'22#vmny',0)","=0.25",
		"=('23#vmny'*'24#vmny')","=[A107040!50#vmny]","EDIT",
		"=('25#vmny'-'26#vmny'-'27#vmny')","EDIT","EDIT",
		"=('28#vmny'+'29#vmny'-'30#vmny')",
		"5711_DF",
		"=('31#vmny'-'32#vmny')",
		"EDIT","EDIT","EDIT","EDIT","EDIT"
	};
	
	private static String[] nssb_lb= new String[]{
		"=利润总额计算","2","3","4","5","6","7","8","9","10","11","12","13","=应纳税所得额计算","15","16","17","18","19","20","21","22","23","=应纳税额计算","25","26","27","28","29","30","31","32","33","34","35","36","=附列资料","38"
	};
	
	private static String[] nssbvoitem=new String[]{
		"vmny","citemclass"
	};
	
	
	
	//中华人民共和国企业所得税月(季)度预缴纳税申报表(A类)
		private static String[] yjdnssbitem=new String[]{
				"一、据实预缴",
				"	营业收入",
				"	营业成本",
				"	利润总额",
				"	税率（25%）",
				"	应纳所得税额（4行×5行）",
				"	减免所得税额",
				"	实际已缴所得税额",
				"	 应补（退）的所得税额（6行-7行-8行）",
				"二、按上一纳税年度应纳税所得额的平均额预缴",
				"上一纳税年度应纳税所得额",
				"本月（季）应纳税所得额（11行÷12或11行÷4）",
				"税率（25%）",
				"本月（季）应纳税所得额（12行×13行）",
				"三、按照税务机关确定的其他方法预缴",
				"本月（季）确定预缴的所得税额",
				"　　					总分机构纳税人",
				"　　总机构",
				"	总机构",
				"　　总机构",
				"  分支机构",
				"  分支机构",
		};
		
		private static String[] yjdnssbxm= new String[]{
			"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22"
		};
		
		//2007新会计
		private static String[] yjdnssbkm_a= new String[]{

			"NA","bq6001_DF+bq6051_DF","bq6401_JF+bq6402_JF","bq6001_DF+bq6051_DF-bq6401_JF-bq6402_JF-bq6403_JF-bq6601_JF-bq6602_JF-bq6603_JF-bq6701_JF+bq6101_DF+bq6111_DF+bq6301_DF-bq6711_JF",
			"=0.25",
			"=IF('4#vbqmny'*'5#vbqmny'>0,'4#vbqmny'*'5#vbqmny',0)","EDIT","STA",
			"STA","NA",
			"EDIT","=('11#vbqmny'/'4')",
			"=0.25","=('12#vbqmny'*'13#vbqmny')","NA",
			"EDIT","NA","EDIT","EDIT","EDIT",
			"EDIT","=('20#vbqmny'*'21#vbqmny')"
		};
		//2013小会计
		private static String[] yjdnssbkm_b= new String[]{
			
			"NA","bq5001_DF+bq5051_DF","bq5401_JF+bq5402_JF","bq5001_DF+bq5051_DF-bq5401_JF-bq5402_JF-bq5403_JF-bq5601_JF-bq5602_JF-bq5603_JF+bq5111_DF+bq5301_DF-bq5711_JF",
			"=0.25",
			"=IF('4#vbqmny'*'5#vbqmny'>0,'4#vbqmny'*'5#vbqmny',0)","EDIT","STA",
			"STA","NA",
			"EDIT","=('11#vbqmny'/'4')",
			"=0.25","=('12#vbqmny'*'13#vbqmny')","NA",
			"EDIT","NA","EDIT","EDIT","EDIT",
			"EDIT","=('20#vbqmny'*'21#vbqmny')"
		};
		//2007新会计
		private static String[] yjdnssbkm_lj_a= new String[]{

			"NA","lj6001_DF+lj6051_DF","lj6401_JF+lj6402_JF","lj6001_DF+lj6051_DF-lj6401_JF-lj6402_JF-lj6403_JF-lj6601_JF-lj6602_JF-lj6603_JF-lj6701_JF+lj6101_DF+lj6111_DF+lj6301_DF-lj6711_JF",
			"=0.25",
			"=IF('4#vljmny'*'5#vljmny'>0,'4#vljmny'*'5#vljmny',0)","EDIT",
			"NA","=IF('6#vljmny'-'7#vljmny'-'8#vljmny'>0,'6#vljmny'-'7#vljmny'-'8#vljmny',0)","NA",
			"EDIT","=('11#vljmny'/'4')",
			"=0.25","=('12#vljmny'*'13#vljmny')","NA",
			"EDIT","NA","EDIT","EDIT","EDIT",
			"EDIT","=('20#vljmny'*'21#vljmny')"
		};
		//2013小会计
		private static String[] yjdnssbkm_lj_b= new String[]{
			
			"NA","lj5001_DF+lj5051_DF","lj5401_JF+lj5402_JF","lj5001_DF+lj5051_DF-lj5401_JF-lj5402_JF-lj5403_JF-lj5601_JF-lj5602_JF-lj5603_JF+lj5111_DF+lj5301_DF-lj5711_JF",
			"=0.25",
			"=IF('4#vljmny'*'5#vljmny'>0,'4#vljmny'*'5#vljmny',0)","EDIT","EDIT",
			"=IF('6#vljmny'-'7#vljmny'-'8#vljmny'>0,'6#vljmny'-'7#vljmny'-'8#vljmny',0)","NA",
			"EDIT","=('11#vljmny'/'4')",
			"=0.25","=('12#vljmny'*'13#vljmny')","NA",
			"EDIT","NA","EDIT","EDIT","EDIT",
			"EDIT","=('20#vljmny'*'21#vljmny')"
		};
		
		private static String[] yjdnssb_lb= new String[]{
			"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17",
			"=总机构应分摊的所得税额（9行或14行或16行×25%）","=中央财政集中分配的所得税额（9行或14行或16行×25%）",
			"=分支机构分摊的所得税额（9行或14行或16行×50%）","=分配比例","=分配的所得税额（20行×21行）"
		};
		
		private static String[] yjdnssbvoitem=new String[]{
			"vbqmny","vljmny","citemclass"
		};
		
		public  String[] getReportVno(String reportcode){
			
			if("A107040".equals(reportcode)){
				
				return jmsdsxm_hc;
			}
			
			return null;
		}
}
