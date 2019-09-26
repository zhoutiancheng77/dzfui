package com.dzf.zxkj.platform.model.st;

/**
 * 纳税申报统计公式
 * */
public class NssbReportFormula {
	private static NssbReportFormula formula;
	
	
	private NssbReportFormula(){};
	
	
	public static NssbReportFormula getInstanct(){
		if(formula==null){
			formula = new NssbReportFormula();
		}
		return formula;
	}
	
	
	public String[] getCalculateFromula(String reportcode){
		String[] formula=null;
		switch (reportcode) {
		case "A100000"://纳税申报表
			formula= nssbform;
			break;
		case "A101010"://一般企业收入明细表
			formula= ybqysrform;
			break;
		case "A102010"://一般企业成本支出明细表
			formula= ybqycbform;
			break;
		case "A104000"://期间费用明细表
			formula= qjfyform;
			break;
		case "A105050"://职工薪酬纳税调整明细表
			formula= zgxcform;
			break;
		case "A105080"://资产折旧、摊销情况及纳税调整明细表
			formula= zjtxnstzform;
			break;
		case "A107040"://减免所得税优惠明细表
			formula= jmsdsform;
			break;
		case "A105000"://纳税调整项目明细表
			formula= nstzmxform;
			break;
		}
		
		return formula;
	}
	
	public String[] getCalculateColFromula(String reportcode){
		String[] formula=null;
		switch (reportcode) {
		case "A105050"://职工薪酬纳税调整明细表
			formula= zgxvform_col;
			break;
		case "A105080"://资产折旧、摊销情况及纳税调整明细表
			formula= zjtxnstzform_col;
			break;
		}
		
		return formula;
	}
	
	
//	/**
//	 * 一般企业收入计算公式
//	 * */
//	public String[] getYbqysrFormula(){
//		return ybqysrform;
//	}
//	/**
//	 * 一般企业成本计算公式
//	 * */
//	public String[] getYbqycbFormula(){
//		return ybqycbform;
//	}
//	/**
//	 * 期间费用明细表
//	 * */
//	public String[] getQjfyFormula(){
//		return qjfyform;
//	}
	
	
	private String[] nssbform = new String[]{
			"'10'=('1'-'2'-'3'-'4'-'5'-'6'-'7'+'8'+'9')","'13'=('10'+'11'-'12')","'19'=('13'-'14'+'15'-'16'-'17'+'18')",
			//"'23'=('19'-'20'-'21'-'22')",
			"'25'=('23'*'24')","'28'=('25'-'26'-'27')","'31'=('28'+'29'-'30')","'33'=('31'-'32')"
	};
	
	private static String[] ybqysrform = new String[]{
		"'1'=('2'+'9')","'2'=('3'+'5'+'6'+'7'+'8')","'9'=('10'+'12'+'13'+'14'+'15')","'16'=('17'+'18'+'19'+'20'+'21'+'22'+'23'+'24'+'25'+'26')"
	};
	private static String[] ybqycbform = new String[]{
		"'1'=('2'+'9')","'2'=('3'+'5'+'6'+'7'+'8')","'9'=('10'+'12'+'13'+'14'+'15')","'16'=('17'+'18'+'19'+'20'+'21'+'22'+'23'+'24'+'25'+'26')"
	};
	private static String[] qjfyform = new String[]{
		"'25'=('1'+'2'+'3'+'4'+'5'+'6'+'7'+'8'+'9'+'10'+'11'+'12'+'13'+'14'+'15'+'16'+'17'+'18'+'19'+'20'+'21'+'22'+'23'+'24')"
	};
	/*private static String[] zgxcform = new String[]{//行公式
		"'4'=('5'+'6')","'13'='1'+'3'+'4'+'7'+'8'+'9'+'10'+'11'+'12'"
	};*/
	private static String[] zgxcform = new String[]{//行公式
		"'13'=('1'+'3'+'4'+'7'+'8'+'9'+'10'+'11'+'12')"
	};
	private static String[] zjtxnstzform = new String[]{//行公式
		"'1'=('2'+'3'+'4'+'5'+'6'+'7')","'8'=('9'+'10')","'11'=('12'+'13'+'14'+'15'+'16'+'17'+'18')","'19'=('20'+'21'+'22'+'23'+'24')","'27'=('1'+'8'+'11'+'19'+'25'+'26')"
	};
	/*private static String[] zjtxnstzform = new String[]{//行公式
		"'1'=('2'+'3'+'4'+'5'+'6'+'7')","'11'=('12'+'13'+'14'+'15'+'16'+'17'+'18')","'19'=('20'+'21'+'22'+'23'+'24')","'27'=('1'+'8'+'11'+'19'+'25'+'26')"
	};*/
	private static String[] jmsdsform = new String[]{//行公式
		//"'4'=('5'+'6'+'7'+'8'+'9'+'10'+'11'+'12'+'13'+'14'+'15'+'16'+'17'+'18'+'19'+'20'+'21'+'22'+'23'+'24'+'25'+'26'+'27')","'29'=('1'+'2'+'3'+'4'-'28')"
		"'3'=('4'+'5')",
		"'7'=('8'+'9'+'10')",
		"'11'=('12'+'13'+'14')",
		"'15'=('16'+'17'+'18')",
		"'19'=('20'+'21'+'22')",
		"'26'=('27'+'28')",
		"'29'=('30'+'31')",
		"'6'=('7'+'11'+'15'+'19'+'23'+'24'+'25'+'26'+'29'+'32'+'33'+'34'+'35'+'36'+'37'+'38'+'39'+'40'+'41'+'42'+'43'+'44'+'45'+'46'+'47')",
		"'50'=('1'+'3'+'6'-'48'+'49')"
	};
	private static String[] nstzmxform= new String[]{
		"'1'=('2'+'3'+'4'+'5'+'6'+'7'+'8'+'10'+'11'-'9')",
		"'12'=('13'+'14'+'15'+'16'+'17'+'18'+'19'+'20'+'21'+'22'+'23'+'24'+'26'+'27'+'28'+'29'-'25')",
		"'30'='31'+'32'+'33'+'34'","'35'='36'+'37'+'38'+'39'+'40'","'43'='1'+'12'+'30'+'35'+'41'+'42'"
	};
//	private static String[] zgxvform_col = new String[]{//列公式
//		"'vnstzje'='vzzje'-'vssje'","'vjzkcje'='vzzje'+'vljjzkc'-'vssje'"
//	};
	private static String[] zgxvform_col = new String[]{//列公式
		"'5'='1'-'4'","'6'='1'+'3'-'4'"
	};
	private static String[] zjtxnstzform_col = new String[]{//行公式
		"'9'=('2'-'5'-'6')"
	};
}
