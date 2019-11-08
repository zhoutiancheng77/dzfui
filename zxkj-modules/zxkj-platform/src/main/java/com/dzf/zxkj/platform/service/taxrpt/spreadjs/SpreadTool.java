package com.dzf.zxkj.platform.service.taxrpt.spreadjs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.constant.PeriodType;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletPosVO;
import com.dzf.zxkj.platform.service.jzcl.impl.IncomeTaxCalculator;
import com.dzf.zxkj.platform.service.taxrpt.ITaxBalaceCcrService;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SpreadTool {

	private ITaxBalaceCcrService taxbalance;
	
	private Map<String, ZcFzBVO[]> zcfzMaps;
	
	private Map<String, LrbVO[]> lrbMaps;
	
	private Map<String, LrbquarterlyVO[]> lrbquarters;
	
	private Map<String, XjllbVO[]> xjllMaps;

	private Map<String, XjllquarterlyVo[]> xjllQuarterMaps;
	
	private Map<String, CorpTaxVo> cptaxmap;
	
	private Map<String, List> listmap;
	
	private Map<String, List<DZFDouble>> mnyoutmap = new HashMap<String, List<DZFDouble>>();

	private Integer datasource = null;//数据来源
	
	public SpreadTool(ITaxBalaceCcrService parataxbalance) {
		// TODO Auto-generated constructor stub
		taxbalance = parataxbalance;
	}
	public SpreadTool() {
	}
	/**
	 * 得到报表名称列表
	 * @param mapJson
	 * @return
	 */
	public List<String> getReportNameList(Map mapJson) 
	{
		List<String> listRptName = new ArrayList<String>();
	
		LinkedHashMap hmsheets = (LinkedHashMap)mapJson.get("sheets");
		Iterator iter = hmsheets.entrySet().iterator();
		
		Map<String, String> hmSheetValue = null;
		Map.Entry entry = null; 
		
		while (iter.hasNext())
		{
			hmSheetValue = new HashMap<String, String>();
			entry = (Map.Entry)iter.next(); 
			//报表名称
			listRptName.add(((String)entry.getKey()).trim()); 
		}
		return listRptName;
	}
	private ObjectMapper getObjectMapper()
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>()  
        {  
   
            @Override  
            public void serialize(  
                    Object value,  
                    JsonGenerator jg,  
                    SerializerProvider sp) throws IOException, JsonProcessingException  
            {  
                jg.writeString("");  
            }  
        });  
		objectMapper.setSerializationInclusion(Include.ALWAYS);
		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		objectMapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, false);
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		return objectMapper;
	}
	/**
	 * json格式spreadJS报表输出行列值
	 * @param strJson 报表json字符串
	 * @return Map<String, Map>  key:报表名称 ( value: map<String, String> : key 行_列， value:此行列的取值 )
	 */
	public Map<String, Map> getMapValue(String strJson)
	{
		Map<String, Map> hmValue = new HashMap<String, Map>();
		try {
			Map obj = (Map)getObjectMapper().readValue(strJson, HashMap.class);
			LinkedHashMap hmsheets = (LinkedHashMap)obj.get("sheets");
			Iterator iter = hmsheets.entrySet().iterator();
			
			Map<String, String> hmSheetValue = null;
			Map.Entry entry = null;
			//报表名称
			String sheetname = null;
			LinkedHashMap hmsheet = null;
			LinkedHashMap hmdata = null;
			LinkedHashMap hmdataTable = null;
			
			//namestyle
			ArrayList<LinkedHashMap> listnamedStyles = null;
			Map<String, LinkedHashMap> hmStyles = null;
			
			Iterator iterrow = null;
			
			String row = null;
			//列集合
			LinkedHashMap cols = null;
			Iterator itercolumn = null;

			String column = null;
			LinkedHashMap cell = null;
					
			boolean isNumber = false;
						
			Object objStyle = null;
		
			Object objValue = null;
			
			LinkedHashMap hmstyle = null;
			
			while (iter.hasNext())
			{
				hmSheetValue = new HashMap<String, String>();
				entry = (Map.Entry)iter.next(); 
				//报表名称
				sheetname = (String)entry.getKey(); 
				hmsheet = (LinkedHashMap)entry.getValue(); 
				hmdata = (LinkedHashMap)hmsheet.get("data");
				hmdataTable =(LinkedHashMap)hmdata.get("dataTable");
				
				//namestyle
				listnamedStyles = (ArrayList<LinkedHashMap>)hmsheet.get("namedStyles");
				hmStyles = new HashMap<String, LinkedHashMap>();
				for (LinkedHashMap hmstyle1 : listnamedStyles)
				{
					hmStyles.put(hmstyle1.get("name").toString(), hmstyle1);
				}
				
				iterrow = hmdataTable.keySet().iterator();
				//遍历行
				while (iterrow.hasNext())
				{
					row = (String)iterrow.next();
					//列集合
					cols = (LinkedHashMap)hmdataTable.get(row);
					itercolumn = cols.keySet().iterator();
					//遍历列
					while (itercolumn.hasNext())
					{
						column = (String)itercolumn.next();
						cell = (LinkedHashMap)cols.get(column);
						if (cell.containsKey("value"))
						{
							isNumber = false;
							if (cell.containsKey("style"))
							{
								objStyle = cell.get("style");
								
								if (objStyle instanceof String)
								{
									hmstyle = hmStyles.get(objStyle);
								}
								else if (objStyle instanceof LinkedHashMap)
								{
									hmstyle = (LinkedHashMap)objStyle;
								}
								if (hmstyle.containsKey("formatter"))
								{
									if (hmstyle.get("formatter").toString().indexOf("0.00_") >= 0)
									{
										isNumber = true;
									}
								}
							}
							objValue = cell.get("value");
							if (isNumber )
							{
								hmSheetValue.put(row + "_" + column, getNumberValue(objValue));
							}
							else
							{
								hmSheetValue.put(row + "_" + column, (objValue == null ? null : objValue.toString()));
							}
						}
						
					}
				}
				hmValue.put(sheetname, hmSheetValue);
			}
		}
		catch (Exception ex)
		{
//			ex.printStackTrace();
		}
		return hmValue;
	}
	private String getNumberValue(Object objValue)
	{
		
		String strReturn = (objValue == null || objValue.toString().trim().length() == 0 ? "0" : objValue.toString().replaceAll(",", ""));
		
		if("-".equals(strReturn)){//DZFDouble的数值负号与“-”冲突
			return strReturn;
		}
		
		try {
			strReturn = new DZFDouble(strReturn).setScale(2, DZFDouble.ROUND_HALF_UP).toString();
		}
		catch (Exception ex)
		{
			strReturn = (objValue == null ? null : objValue.toString());
			//不用输出
		}
		return strReturn;
	}
	/**
	 * 用spreadJS报表转出的map数据填充pdf
	 * @param pdfFilePath
	 * @param posvos pdf文件行列位置key数组
	 * @param hmValue key：行_列
	 * @return  pdf文件的byte[]
	 */
	public void fillPDFValue(String pdfFilePath, TaxReportVO reportvo, CorpTaxVo taxvo, TaxRptTempletPosVO[] posvos, Map<String, String> hmValue, File pdffileOut, CorpVO corpvo) throws DZFWarpException
	{
		byte[] bytesReturn;
		File file = new File(pdfFilePath);
		if (file.exists() == false)
		{
			return;
		}
		if (pdffileOut.exists())
		{
			pdffileOut.delete();
		}
		FileOutputStream fos = null;
		PdfReader reader = null;
		PdfStamper ps = null;
		try {
			reader = new PdfReader(pdfFilePath);
			

			fos = new FileOutputStream(pdffileOut);
					
		
			ps = new PdfStamper(reader, fos);

			AcroFields acrofields = ps.getAcroFields();
			
			BaseFont bf = BaseFont.createFont("/com/dzf/service/spreadjs/simsun.ttc,1",BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

			
			acrofields.addSubstitutionFont(bf);
			

			
			Map pdfkeymap = acrofields.getFields();
			
			String fieldname = null;
			String key = null; 
				
			for (TaxRptTempletPosVO vo : posvos)
			{
				fieldname = vo.getItemkey().trim();
				if (pdfkeymap.containsKey(fieldname))
				{
					key = "" + vo.getRptrow() + "_" + vo.getRptcol();

					if (hmValue.containsKey(key))
					{

						acrofields.setField(fieldname, hmValue.get(key));

					}
				}

			}
			//填写表头公共信息
			fillPublicInfo(corpvo, taxvo,reportvo, acrofields);
			
			ps.setFormFlattening(true); // 这句不能少  
			fos.flush();
			
		}
		catch (Exception e)
		{
			throw new WiseRunException(e);
		}
		finally {

			
			if (ps != null) 
			{
				try {
					ps.close();
				}
				catch (Exception e)
				{
					throw new WiseRunException(e);
				}
			}

			if (reader != null) reader.close();
			
			if (fos != null)
			{
				try {
					fos.close();
				}
				catch (Exception e)
				{
					throw new WiseRunException(e);
				}
			}
		}

	}

	/**
	 * 
	 * @Param corpvo
	 * @Param reportvo
	 * @param acrofields
	 *
	 */
	public void fillPublicInfo( CorpVO corpvo, CorpTaxVo taxvo ,TaxReportVO reportvo, AcroFields acrofields) throws DZFWarpException
	{
		Map pdfKeyFieldMap = acrofields.getFields();
		try {
			//税款所属时间_年
			if (pdfKeyFieldMap.containsKey("qsrq_y"))
			{
				acrofields.setField("qsrq_y", reportvo.getPeriodfrom().substring(0, 4));
	
			}
			if (pdfKeyFieldMap.containsKey("qsrq_m"))
			{
				acrofields.setField("qsrq_m", reportvo.getPeriodfrom().substring(5, 7));
	
			}
			if (pdfKeyFieldMap.containsKey("qsrq_d"))
			{
				acrofields.setField("qsrq_d", reportvo.getPeriodfrom().substring(8, 10));
	
			}
			if (pdfKeyFieldMap.containsKey("jzrq_y"))
			{
				acrofields.setField("jzrq_y", reportvo.getPeriodto().substring(0, 4));
	
			}
			if (pdfKeyFieldMap.containsKey("jzrq_m"))
			{
				acrofields.setField("jzrq_m", reportvo.getPeriodto().substring(5, 7));
	
			}
			if (pdfKeyFieldMap.containsKey("jzrq_d"))
			{
				acrofields.setField("jzrq_d", reportvo.getPeriodto().substring(8, 10));
	
			}
			if (pdfKeyFieldMap.containsKey("nsrsbh"))	//纳税人识别号
			{
				acrofields.setField("nsrsbh", taxvo.getTaxcode());
	
			}
			if (pdfKeyFieldMap.containsKey("nsrmc"))	//纳税人名称
			{
				acrofields.setField("nsrmc", corpvo.getUnitname());
	
			}
			if (pdfKeyFieldMap.containsKey("tbrq_y"))	//填报日期:年
			{
				acrofields.setField("tbrq_y", "" + reportvo.getDoperatedate().getYear());
	
			}
			if (pdfKeyFieldMap.containsKey("tbrq_m"))	//填报日期:月
			{
				acrofields.setField("tbrq_m", "" + (reportvo.getDoperatedate().getMonth() < 10 ? "0" : "") + reportvo.getDoperatedate().getMonth());
	
			}
			if (pdfKeyFieldMap.containsKey("tbrq_d"))	//填报日期:日
			{
				acrofields.setField("tbrq_d", "" + (reportvo.getDoperatedate().getDay() < 10 ? "0" : "") + reportvo.getDoperatedate().getDay());
	
			}
		}
		catch (Exception e)
		{
			throw new WiseRunException(e);
		}

	}
	
	/**
	 * 
	 * @param fieldname
	 * @return 0: String 1:int 2:double
	 */
	private int getFieldType(String fieldname)
	{
		int iType = 2;
		if (fieldname.equals("nsrsbh") 
				|| fieldname.equals("nsrmc")
				|| fieldname.equals("qsrq_y")
				|| fieldname.equals("qsrq_m")
				|| fieldname.equals("qsrq_d")
				|| fieldname.equals("jzrq_y")
				|| fieldname.equals("jzrq_m")
				|| fieldname.equals("jzrq_d")
				|| fieldname.equals("tbrq_y")
				|| fieldname.equals("tbrq_m")
				|| fieldname.equals("tbrq_d")
				//101010007.pdf
				|| fieldname.startsWith("sb_ybnsr_dkdjsstzdkqd_kjrnsrsbh")
				|| fieldname.startsWith("sb_ybnsr_dkdjsstzdkqd_kjrmc")
				|| fieldname.startsWith("sb_ybnsr_dkdjsstzdkqd_zsjgmc")
				|| fieldname.startsWith("sb_ybnsr_dkdjsstzdkqd_dkdjxm")
				|| fieldname.startsWith("sb_ybnsr_dkdjsstzdkqd_dkdjpzbh")
				//10101008.pdf
				|| fieldname.startsWith("sb_ybnsr_ysfwkcxmqd_kpfnsrsbh")
				|| fieldname.startsWith("sb_ybnsr_ysfwkcxmqd_kpfdwmc")
				|| fieldname.startsWith("sb_ybnsr_ysfwkcxmqd_pzzl")
				|| fieldname.startsWith("sb_ybnsr_ysfwkcxmqd_fpdm")
				|| fieldname.startsWith("sb_ybnsr_ysfwkcxmqd_fphm")
				|| fieldname.startsWith("sb_ybnsr_ysfwkcxmqd_fwxmmc")
				//10101009.pdf
				|| fieldname.startsWith("sb_ybnsr_cpygxcqkmx_ypxh")
				//10101010.pdf
				|| fieldname.equals("ygjncpw_trcc_bz")
				|| fieldname.equals("ygjncpw_tbf_bz")
				|| fieldname.equals("gjncpzjxs_bz")
				|| fieldname.equals("gjncpyscsw_bz")
				|| fieldname.equals("hj_bz")
				//10101011.pdf
				|| fieldname.startsWith("sb_ybnsr_trccfhdncpzzs_sb_xh")
				|| fieldname.startsWith("sb_ybnsr_trccfhdncpzzs_cpmc")
				|| fieldname.startsWith("sb_ybnsr_trccfhdncpzzs_hyncpmc")
				//10101012.pdf
				|| fieldname.startsWith("sb_ybnsr_cbfhdncpzzsjxse_sb_xh")
				|| fieldname.startsWith("sb_ybnsr_cbfhdncpzzsjxse_cpmc")
				//10101013.pdf
				|| fieldname.startsWith("sb_ybnsr_gjncpzjxshdncpzzs_sb_xh")
				|| fieldname.startsWith("sb_ybnsr_gjncpzjxshdncpzzs_cpmc")
				//10101014.pdf
				|| fieldname.startsWith("sb_ybnsr_gjycpyyscyjbgchdncpzz_sb_xh")
				|| fieldname.startsWith("sb_ybnsr_gjycpyyscyjbgchdncpzz_cpmc")
				|| fieldname.startsWith("sb_ybnsr_gjycpyyscyjbgchdncpzz_hyncpmc")
				//10101015.pdf
				|| fieldname.startsWith("sb_ybnsr_scqyjljgdkmx_sb_xh")
				|| fieldname.startsWith("sb_ybnsr_scqyjljgdkmx_jljgmszmbh")
				|| fieldname.startsWith("sb_ybnsr_scqyjljgdkmx_zmcjny")
				|| fieldname.startsWith("sb_ybnsr_scqyjljgdkmx_bz")
				//10101016.pdf
				|| fieldname.startsWith("sb_ybnsr_scqyckhwzsmx_sb_xh")
				|| fieldname.startsWith("sb_ybnsr_scqyckhwzsmx_ckfphm")
				|| fieldname.startsWith("sb_ybnsr_scqyckhwzsmx_ckrq")
				|| fieldname.startsWith("sb_ybnsr_scqyckhwzsmx_myxz")
				|| fieldname.startsWith("sb_ybnsr_scqyckhwzsmx_jzpzh")
				//10101017.pdf
				|| fieldname.startsWith("sb_ybnsr_scqyckhwzsmxcb_sb_xh")
				|| fieldname.startsWith("sb_ybnsr_scqyckhwzsmxcb_bgdhm")
				|| fieldname.startsWith("sb_ybnsr_scqyckhwzsmxcb_dlckzmd")
				|| fieldname.startsWith("sb_ybnsr_scqyckhwzsmxcb_spmc")
				|| fieldname.startsWith("sb_ybnsr_scqyckhwzsmxcb_jldw")
				
				//101010018.pdf
				|| fieldname.startsWith("sb_ybnsr_gjyszmsmxsj_sb_xh")
				|| fieldname.startsWith("sb_ybnsr_gjyszmsmxsj_ysfwdm")
				|| fieldname.startsWith("sb_ybnsr_gjyszmsmxsj_ysfwmc")
				|| fieldname.startsWith("sb_ybnsr_gjyszmsmxsj_jldw")
				//101010019.pdf
				|| fieldname.startsWith("sb_ybnsr_jfsjfwzmsmxsj_sb_xh")
				|| fieldname.startsWith("sb_ybnsr_jfsjfwzmsmxsj_jldw")
				
				//10101021.pdf
				|| fieldname.startsWith("sb_ybnsr_jmsmx_jsxm_jmxz_dl_dm")
				|| fieldname.startsWith("sb_ybnsr_jmsmx_jsxm_jmxz_xl_dm")
				|| fieldname.startsWith("sb_ybnsr_jmsmx_msxm_jmxz_dl_dm")
				|| fieldname.startsWith("sb_ybnsr_jmsmx_msxm_jmxz_xl_dm")
				//101010024.pdf
				|| fieldname.startsWith("sb_ybnsr_ygzsffxcsmxb_ysxmdmjmc")
				
				//小规模表10102004.pdf
				|| fieldname.startsWith("sb_xgm_jmsmx_jsxm_jmxz_dm")
				|| fieldname.startsWith("sb_xgm_jmsmx_jsxm_jmxz_xl_dm")
				|| fieldname.startsWith("sb_xgm_jmsmx_msxm_jmxz_dm")
				|| fieldname.startsWith("sb_xgm_jmsmx_msxm_jmxz_dl_dm")
				//小规模10102005.pdf
				|| fieldname.startsWith("sb_xgm_ysfwkcxm_kpfnsrsbh")
				|| fieldname.startsWith("sb_xgm_ysfwkcxm_kpfdwmc")
				|| fieldname.startsWith("sb_xgm_ysfwkcxm_pzzl")
				|| fieldname.startsWith("sb_xgm_ysfwkcxm_fpdm")
				|| fieldname.startsWith("sb_xgm_ysfwkcxm_fphm")
				|| fieldname.startsWith("sb_xgm_ysfwkcxm_fwxmmc")

				
				)
		{
			iType = 0;
		}
		else if (fieldname.equals("rzxfdskzzszyfpsbdk_fs")
				|| fieldname.equals("bqrzxfqbqsbdk_fs")
				|| fieldname.equals("qqrzxfqbqsbdk_fs")
				|| fieldname.equals("qtkspz_fs")
				|| fieldname.equals("hgjkzzszyjks_fs")
				|| fieldname.equals("ncpsgfphzxsfp_fs")
				|| fieldname.equals("dkdjssjkpz_fs")
				|| fieldname.equals("ysfyjsdj_fs")
				|| fieldname.equals("dqsbdkjxsehj_fs")
				|| fieldname.equals("qcyrzxfqbqwsbdk_fs")
				|| fieldname.equals("bqrzxfqbqwsbdk_fs")
				|| fieldname.equals("qmyrzxfdwsbdk_fs")
				|| fieldname.equals("azsfgdbyxdk_fs")
				|| fieldname.equals("qtkspzddkjxse_fs")
				|| fieldname.equals("hgjkzzszyxsfp_fs")
				|| fieldname.equals("ncpsgfphzxsfpddk_fs")
				|| fieldname.equals("dkdjshjkpz_fs")
				|| fieldname.equals("ysfyjsdjddk_fs")
				|| fieldname.equals("bqrzxfdskzzszyfp_fs")
				//10101009.pdf
				|| fieldname.startsWith("sb_ybnsr_cpygxcqkmx_zgslqckc")
				|| fieldname.startsWith("sb_ybnsr_cpygxcqkmx_dcslqckc")
				|| fieldname.startsWith("sb_ybnsr_cpygxcqkmx_zgslbqrk")
				|| fieldname.startsWith("sb_ybnsr_cpygxcqkmx_dcslbqrk")
				|| fieldname.startsWith("sb_ybnsr_cpygxcqkmx_slysbfbqck")
				|| fieldname.startsWith("sb_ybnsr_cpygxcqkmx_zyslfysbfbqck")
				|| fieldname.startsWith("sb_ybnsr_cpygxcqkmx_dcslfysbfbqck")
				|| fieldname.startsWith("sb_ybnsr_cpygxcqkmx_zgslqmkc")
				|| fieldname.startsWith("sb_ybnsr_cpygxcqkmx_dcslqmkc")
				//10101011.pdf
				|| fieldname.startsWith("sb_ybnsr_trccfhdncpzzs_hdddhsl")
				|| fieldname.startsWith("sb_ybnsr_trccfhdncpzzs_qckcncpsl")
				|| fieldname.startsWith("sb_ybnsr_trccfhdncpzzs_dqgjncpsl")
				|| fieldname.startsWith("sb_ybnsr_trccfhdncpzzs_dqxshwsl")
				//10101013.pdf
				|| fieldname.startsWith("sb_ybnsr_gjncpzjxshdncpzzs_dqxsncpsl")
				|| fieldname.startsWith("sb_ybnsr_gjncpzjxshdncpzzs_shsl")
				|| fieldname.startsWith("sb_ybnsr_gjncpzjxshdncpzzs_ncpgjsl")
				|| fieldname.startsWith("sb_ybnsr_gjncpzjxshdncpzzs_qckcncpsl")
				|| fieldname.startsWith("sb_ybnsr_gjncpzjxshdncpzzs_dqgjncpsl")
				//10101014.pdf
				|| fieldname.startsWith("sb_ybnsr_gjycpyyscyjbgchdncpzz_dqhyncpsl")
				|| fieldname.startsWith("sb_ybnsr_gjycpyyscyjbgchdncpzz_qckcncpsl")
				|| fieldname.startsWith("sb_ybnsr_gjycpyyscyjbgchdncpzz_dqgjncpsl")
				//10101017.pdf
				|| fieldname.startsWith("sb_ybnsr_scqyckhwzsmxcb_cksl")
				//10101018.pdf
				|| fieldname.startsWith("sb_ybnsr_gjyszmsmxsj_bqyscs")
				|| fieldname.startsWith("sb_ybnsr_gjyszmsmxsj_zycdfs")
				|| fieldname.startsWith("sb_ybnsr_gjyszmsmxsj_zytdfshzkrs")
				//10101019.pdf
				|| fieldname.startsWith("sb_ybnsr_jfsjfwzmsmxsj_bqskpzfs")
				//10101020.pdf
				|| fieldname.equals("lt_xssl")
				|| fieldname.equals("zwxlt_xssl")
				|| fieldname.equals("xjlt_xssl")
				|| fieldname.equals("jj_xssl")
				|| fieldname.equals("yyycqydjj_xssl")
				|| fieldname.equals("syjj_xssl")
				|| fieldname.equals("qtjj_xssl")
				|| fieldname.equals("mtc_xssl")

				
				)
		{
			iType = 1;
		}
		return iType;
	}
	/**
	 * 用spreadJS报表转出的map数据填充vo
	 * @param hmReportValue key:属性名  value：属性值
	 * @param posvos pdf文件行列位置key数组
	 * @param hmValue key:row_col, value:值

	 */
	public void fillReportVOFValue(HashMap<String, Object> hmReportValue, TaxRptTempletPosVO[] posvos, Map<String, String> hmValue) throws DZFWarpException
	{
		try {
			
			String attributename = null;
			
			int iType = 0;
			
			String rowclokey = null;

			Object objValue = null;
				
			for (TaxRptTempletPosVO vo : posvos)
			{
				attributename = vo.getItemkey();
				
				iType = getFieldType(attributename);	// 0: String 1:int 2:double
				
				rowclokey = "" + vo.getRptrow() + "_" + vo.getRptcol();
				if (hmValue.containsKey(rowclokey))
				{
					objValue = hmValue.get(rowclokey);
					if ("——".equals(objValue) == false)
					{
						
						hmReportValue.put(attributename, (objValue == null ? (iType == 0 ? "" : (iType == 1 ? "0" : "0.00")) : objValue));
					}

				}
				else
				{
					//没填值的表格暂时不取数
//					hmReportValue.put(attributename, (iType == 0 ? "" : (iType == 1 ? "0" : "0.00")));
					
				}
			}
			
		}
		catch (Exception e)
		{
//			e.printStackTrace();
			throw e;
		}
	}
	public void setactiveSheetIndex(Map mapJson, String reportName) throws DZFWarpException
	{
		LinkedHashMap hmsheets = (LinkedHashMap)mapJson.get("sheets");
		Iterator iter = hmsheets.entrySet().iterator();
		int activeSheetIndex = 0;
		boolean isFound_activeSheetIndex = false;
		
		Map<String, String> hmSheetValue = null;
		Map.Entry entry = null;
		//报表名称
		String sheetname = null;
		
		//遍历报表
		while (iter.hasNext())
		{
			hmSheetValue = new HashMap<String, String>();
			entry = (Map.Entry)iter.next(); 
			//报表名称
			sheetname = ((String)entry.getKey()).trim(); 

			if (sheetname.equals(reportName.trim()))
			{
				break;
			}
			else
			{
				activeSheetIndex++;
			}
		}
		mapJson.put("activeSheetIndex", activeSheetIndex);
	}
	/**
	 * 表格金额数据四舍五入, 表头公共信息替换
	 * @param strJson
	 * @param corpvo
	 * @param reportvo
	 * @throws DZFWarpException
	 */
	public String adjustBeforeSave(String strJson, CorpVO corpvo, TaxReportVO reportvo) throws DZFWarpException
	{
//		if (1==1) return strJson;
		
		LinkedHashMap mapJson = null;
		try 
		{
			mapJson = (LinkedHashMap)getObjectMapper().readValue(strJson, LinkedHashMap.class);
			
			LinkedHashMap hmsheets = (LinkedHashMap)mapJson.get("sheets");
			Iterator iter = hmsheets.entrySet().iterator();
			
			Map.Entry entry = null;
			//报表名称
			String sheetname = null; 
			
			
			LinkedHashMap hmsheet = null;
			LinkedHashMap hmdata = null;
			LinkedHashMap hmdataTable = null;
			
			//namestyle
			ArrayList<LinkedHashMap> listnamedStyles = null;
			Map<String, LinkedHashMap> hmStyles = null;
			
			Iterator iterrow = null;
			
			String row = null;
			//列集合
			LinkedHashMap cols = null;
			Iterator itercolumn = null;
			//遍历列
			String column = null;
			LinkedHashMap cell = null;

			boolean isNumber = false;
			
			Object objStyle = null;
			LinkedHashMap hmstyle = null;
					
			Object objValue = null;
			
			while (iter.hasNext())
			{
				entry = (Map.Entry)iter.next(); 
				//报表名称
				sheetname = ((String)entry.getKey()).trim(); 
				//更新表头公共信息
//				if (corpvo.getVprovince() == null || corpvo.getVprovince() != 2)
//				{
//					fillSpreadReportHeadPublic(mapJson, sheetname, corpvo, reportvo);
//				}
				hmsheet = (LinkedHashMap)entry.getValue(); 
				hmdata = (LinkedHashMap)hmsheet.get("data");
				hmdataTable =(LinkedHashMap)hmdata.get("dataTable");
				
				//namestyle
				listnamedStyles = (ArrayList<LinkedHashMap>)hmsheet.get("namedStyles");
				hmStyles = new HashMap<String, LinkedHashMap>();
				for (LinkedHashMap hmstyle1 : listnamedStyles)
				{
					hmStyles.put(hmstyle1.get("name").toString(), hmstyle1);
				}
				
				
				iterrow = hmdataTable.keySet().iterator();
				//遍历行
				while (iterrow.hasNext())
				{
					row = (String)iterrow.next();
					//列集合
					cols = (LinkedHashMap)hmdataTable.get(row);
					itercolumn = cols.keySet().iterator();
					//遍历列
					while (itercolumn.hasNext())
					{
						column = (String)itercolumn.next();
						cell = (LinkedHashMap)cols.get(column);
						if (cell.containsKey("value"))
						{
							isNumber = false;
							if (cell.containsKey("style"))
							{
								objStyle = cell.get("style");
								hmstyle = null;
								if (objStyle instanceof String)
								{
									hmstyle = hmStyles.get(objStyle);
								}
								else if (objStyle instanceof LinkedHashMap)
								{
									hmstyle = (LinkedHashMap)objStyle;
								}
								if (hmstyle != null && hmstyle.containsKey("formatter"))
								{
									if (hmstyle.get("formatter").toString().indexOf("0.00_") >= 0)
									{
										isNumber = true;
									}
								}
							}
							
							if (isNumber )
							{
								objValue = cell.get("value");
								if (objValue != null && StringUtil.isEmptyWithTrim(objValue.toString()) == false)
								{
									cell.put("value", getNumberValue(objValue));
								}
							}
							
						}
						
					}
				}
	
			}
			return getObjectMapper().writeValueAsString(mapJson);
		}
		catch (Exception ex)
		{
			throw new WiseRunException(ex);
		}

	}
	/**
	 * 填写指定单元格的值
	 * @param mapJson
	 * @param reportname
	 * @param iRow
	 * @param iColumn
	 * @param value
	 */
	public void setCellValue(Map mapJson, String reportname, int iRow, int iColumn, String value)
	{
		LinkedHashMap hmsheets = (LinkedHashMap)mapJson.get("sheets");
		Iterator iter = hmsheets.entrySet().iterator();

		Map.Entry entry = null;
		//报表名称
		String sheetname = null; 

		LinkedHashMap hmsheet = null;
		LinkedHashMap hmdata = null;
		LinkedHashMap hmdataTable = null;
		
		LinkedHashMap hmColumns = null;
		LinkedHashMap cell = null;
		
		while (iter.hasNext())
		{

			entry = (Map.Entry)iter.next(); 
			//报表名称
			sheetname = ((String)entry.getKey()).trim(); 

			if (sheetname.trim().equals(reportname.trim()) == false)
			{
				continue;
			}
			
			hmsheet = (LinkedHashMap)entry.getValue(); 
			hmdata = (LinkedHashMap)hmsheet.get("data");
			hmdataTable = (LinkedHashMap)hmdata.get("dataTable");
			
			if (hmdataTable.containsKey("" + iRow))
			{
				hmColumns = (LinkedHashMap)hmdataTable.get("" + iRow);
				if (hmColumns.containsKey("" + iColumn))
				{
					cell = (LinkedHashMap)hmColumns.get("" + iColumn);
					if (value != null)
					{
						cell.put("value", value);
					}
					else
					{
						if (cell.containsKey("value"))
						{
							cell.remove("value");
						}
					}
					
				}
			}
		}
	}
	/**
	 * 填写指定单元格的值
	 * @param mapJson
	 * @param reportname
	 * @param iRow
	 * @param iColumn
	 * @param formula
	 */
	public void setCellFormula(Map mapJson, String reportname, int iRow, int iColumn, Object formula)
	{
		LinkedHashMap hmsheets = (LinkedHashMap)mapJson.get("sheets");
		@SuppressWarnings("rawtypes")
		Iterator iter = hmsheets.entrySet().iterator();

		Map.Entry entry = null; 
		//报表名称
		String sheetname = null; 

		LinkedHashMap hmsheet = null; 
		LinkedHashMap hmdata = null;
		LinkedHashMap hmdataTable = null;
		
		LinkedHashMap hmColumns = null;
		LinkedHashMap cell = null;
				
		while (iter.hasNext())
		{

			entry = (Map.Entry)iter.next(); 
			//报表名称
			sheetname = ((String)entry.getKey()).trim(); 

			if (sheetname.trim().equals(reportname.trim()) == false)
			{
				continue;
			}
			
			hmsheet = (LinkedHashMap)entry.getValue(); 
			hmdata = (LinkedHashMap)hmsheet.get("data");
			hmdataTable = (LinkedHashMap)hmdata.get("dataTable");
			
			if (hmdataTable.containsKey("" + iRow))
			{
				hmColumns = (LinkedHashMap)hmdataTable.get("" + iRow);
				if (hmColumns.containsKey("" + iColumn))
				{
					cell = (LinkedHashMap)hmColumns.get("" + iColumn);
					cell.put("formula", formula);
					cell.remove("value");
				}
			}
		}
	}
	
	//根据名称管理器名称获取 值
	public Object[] getCellXYByName(Map mapJson, String name){
		Object[] objReturn = null;
		List<LinkedHashMap> names = (List<LinkedHashMap>) mapJson.get("names");
		if(names == null || names.size() == 0)
			return objReturn;
		
		String formula = null;
		String key;
		Map target = null;
		for(LinkedHashMap nameMap : names){
			key = (String) nameMap.get("name");
			if(!StringUtil.isEmpty(key) 
					&& key.contains(name)){
				formula = (String) nameMap.get("formula");
				target = nameMap;
				break;
			}
		}
		
		if(StringUtil.isEmpty(formula) || formula.contains("#REF!"))
			return objReturn;
		
		String[] splits = formula.split("!");
		
		if(splits == null || splits.length < 2)
			return objReturn;
		
		String[] splits1 = splits[1].split("\\$|:");
		
		int x = fromNumSystem26(splits1[1]) - 1;
		int y = Integer.parseInt(splits1[2]) - 1;
		
		if(target != null && !splits[0].startsWith("'")){
			target.put("formula", "'" + splits[0] + "'!" + splits[1]);
		}
		
		objReturn = new Object[]{x, y, splits[0]};
		
		return objReturn;
	}
	
	private int fromNumSystem26(String str){
		int n = 0;
		
		if(StringUtil.isEmptyWithTrim(str))
			return n;
		
		for(int i = str.length() - 1, j = 1; i >= 0; i--, j *= 26){
			char c = Character.toUpperCase(str.charAt(i));
			
			if(c < 'A' || c > 'Z')
				return 0;
			
			n +=((int) c - 64) * j;
		}
		
		return n;
	}
	
	public Object getCellValue(Map mapJson, String reportname, int iRow, int iColumn)
	{
		Object objReturn = null;
		LinkedHashMap hmsheets = (LinkedHashMap)mapJson.get("sheets");
		Iterator iter = hmsheets.entrySet().iterator();


		Map.Entry entry = null;
		//报表名称
		String sheetname = null; 

		
		LinkedHashMap hmsheet = null;
		LinkedHashMap hmdata = null;
		LinkedHashMap hmdataTable = null;
		
		LinkedHashMap hmColumns = null;
		LinkedHashMap cell = null;
				
		while (iter.hasNext())
		{

			entry = (Map.Entry)iter.next(); 
			//报表名称
			sheetname = ((String)entry.getKey()).trim(); 

			if (sheetname.trim().equals(reportname.trim()) == false)
			{
				continue;
			}
			
			hmsheet = (LinkedHashMap)entry.getValue(); 
			hmdata = (LinkedHashMap)hmsheet.get("data");
			hmdataTable = (LinkedHashMap)hmdata.get("dataTable");
			
			if (hmdataTable.containsKey("" + iRow))
			{
				hmColumns = (LinkedHashMap)hmdataTable.get("" + iRow);
				if (hmColumns.containsKey("" + iColumn))
				{
					cell = (LinkedHashMap)hmColumns.get("" + iColumn);
					objReturn = cell.get("value");
					
				}
			}
			break;
			
		}
		return objReturn;
	}
	public Object getCellFormula(Map mapJson, String reportname, int iRow, int iColumn)
	{
		Object objReturn = null;
		LinkedHashMap hmsheets = (LinkedHashMap)mapJson.get("sheets");
		Iterator iter = hmsheets.entrySet().iterator();


		Map.Entry entry = null;
		//报表名称
		String sheetname = null; 

		LinkedHashMap hmsheet = null; 
		LinkedHashMap hmdata = null;
		LinkedHashMap hmdataTable = null;

		LinkedHashMap hmColumns = null;

		LinkedHashMap cell = null;
				
		while (iter.hasNext())
		{

			entry = (Map.Entry)iter.next(); 
			//报表名称
			sheetname = ((String)entry.getKey()).trim(); 

			if (sheetname.trim().equals(reportname.trim()) == false)
			{
				continue;
			}
			
			hmsheet = (LinkedHashMap)entry.getValue(); 
			hmdata = (LinkedHashMap)hmsheet.get("data");
			hmdataTable = (LinkedHashMap)hmdata.get("dataTable");
			
			if (hmdataTable.containsKey("" + iRow))
			{
				hmColumns = (LinkedHashMap)hmdataTable.get("" + iRow);
				if (hmColumns.containsKey("" + iColumn))
				{
					cell = (LinkedHashMap)hmColumns.get("" + iColumn);
					objReturn = cell.get("formula");
					
				}
			}
			break;
			
		}
		return objReturn;
	}
	/**
	 * 
	 * @param mapJson
	 * @param voPosition   TaxRptTempletPosVO
	 * @param reportname
	 * @return
	 */
	public Object getCellValue(Map mapJson, String reportname, TaxRptTempletPosVO voPosition) throws DZFWarpException
	{
		if (voPosition == null)
		{
			throw new BusinessException("无法在报表中取数。");
		}
		int iRow = voPosition.getRptrow();
		int iColumn = voPosition.getRptcol();
		
		return getCellValue(mapJson, reportname, iRow, iColumn);
		
	}
	
	/**
	 * 生成spread报表，填充期初，大账房函数值，公共表头信息
	 * @param readonly
	 * @param mapJson
	 * @param listRptName
	 * @param reportname
	 * @param reportvo
	 * @param corpvo
	 * @param corptaxvo
	 * @param hmQCData
	 * @return
	 * @throws DZFWarpException
	 */
	public Map fillDataToJsonTemplet(boolean readonly, Map mapJson, List<String> listRptName, String reportname, TaxReportVO reportvo, CorpVO corpvo, CorpTaxVo corptaxvo,HashMap hmQCData, YntCpaccountVO[] accountVO) throws DZFWarpException
	{
		//滚动条和页签比例
		mapJson.put("tabStripRatio", 0.75);
		LinkedHashMap hmsheets = (LinkedHashMap)mapJson.get("sheets");
		Iterator iter = hmsheets.entrySet().iterator();

		//保留下的报表hashmap
		HashMap<String, LinkedHashMap> hmReports = new HashMap<String, LinkedHashMap>();
		//遍历报表
		List<String> listDeleteSheet = new ArrayList<String>();

		while (iter.hasNext())
		{
			Map<String, String> hmSheetValue = new HashMap<String, String>();
			Map.Entry entry = (Map.Entry)iter.next(); 
			//报表名称
			String sheetname = ((String)entry.getKey()).trim(); 

			if (listRptName.contains(sheetname) == false)
			{
				listDeleteSheet.add(sheetname);
				continue;
			}

			//赋表头默认值
			setDefaultHeadValue(mapJson,sheetname,  reportvo,  corpvo,corptaxvo);
			
			fillReportByQcData(mapJson, sheetname, corpvo, corptaxvo, reportvo, hmQCData);
			
			hmReports.put(sheetname, (LinkedHashMap)entry.getValue());
			
			LinkedHashMap hmsheet = (LinkedHashMap)entry.getValue(); 
			LinkedHashMap hmdata = (LinkedHashMap)hmsheet.get("data");
			LinkedHashMap hmdataTable = (LinkedHashMap)hmdata.get("dataTable");
			
			//找蓝色格子#66FFFF
			String sColorBlue = "#66FFFF";
			ArrayList<LinkedHashMap> listnamedStyles = (ArrayList<LinkedHashMap>)hmsheet.get("namedStyles");
			Map<String, LinkedHashMap> hmBuleName = new HashMap<String, LinkedHashMap>();
			Map<String, LinkedHashMap> hmStyles = new HashMap<String, LinkedHashMap>();
			for (LinkedHashMap hmstyle : listnamedStyles)
			{
//				LinkedHashMap hmstyle = (LinkedHashMap)hmnamedStyles.get(keystyle);
				if (sColorBlue.equals(hmstyle.get("backColor")))
				{
					hmBuleName.put(hmstyle.get("name").toString(), hmstyle);
				}
				hmStyles.put(hmstyle.get("name").toString(), hmstyle);
			}
			hmsheet.put("index", listRptName.indexOf(sheetname));
			//保护工作表
			hmsheet.put("isProtected", true);
			//缩放百分比
			hmsheet.put("zoomFactor", 1.3);

			

			//行号
			String row = null;
			//列集合
			LinkedHashMap cols = null;
			Iterator itercolumn = null;

			String column = null;
			LinkedHashMap cell = null;
				
			//判断底色是否是浅蓝，浅蓝可编辑
			Object objStyle = null;
				
			LinkedHashMap<String, Object> hmStyle = null;
			Object objBackColor = null;
					
			LinkedHashMap thisstyle = null;
			
			Object objFormula = null;
				
			String strValue = null;
			//公式缓存
			Map mapValue = new HashMap<String, Object>();
			
			Iterator iterrow = hmdataTable.keySet().iterator();
			//遍历行
			while (iterrow.hasNext())
			{
				//行号
				row = (String)iterrow.next();
				//列集合
				cols = (LinkedHashMap)hmdataTable.get(row);
				itercolumn = cols.keySet().iterator();
				//遍历列
				while (itercolumn.hasNext())
				{
					column = (String)itercolumn.next();
					cell = (LinkedHashMap)cols.get(column);
					
					//判断底色是否是浅蓝，浅蓝可编辑
					objStyle = cell.get("style");
					if (objStyle != null)
					{
						if (objStyle instanceof LinkedHashMap) 
						{
							hmStyle = (LinkedHashMap<String, Object>)objStyle;
							objBackColor = hmStyle.get("backColor");
							if (objBackColor != null && objBackColor.toString().equals(sColorBlue))
							{
								hmStyle.put("locked", readonly);
							}
						}
						else if (objStyle instanceof String)
						{
							if (hmBuleName.containsKey(objStyle.toString()))
							{
								thisstyle = hmBuleName.get(objStyle.toString());
								thisstyle.put("locked", readonly);
							}
						}
					}
					if (cell.containsKey("formula"))
					{
						
						
						objFormula = cell.get("formula");
					
						if (objFormula == null || objFormula.toString().trim().length() == 0) continue;
						
						strValue = getFormulaValue(mapValue, objFormula, hmQCData, corpvo, reportvo, accountVO);
						strValue = getFormularBetweenTable(listRptName, strValue);
						
						//重新赋值公式
						//判断cell是浮点型，两位小数，则处理浮点
						if (cell.containsKey("style"))
						{
							hmStyle = null;
							Object objCellStyle = cell.get("style");
							if (objCellStyle instanceof String)
							{
								hmStyle = hmStyles.get(objCellStyle);
							}
							else if (objCellStyle instanceof LinkedHashMap)
							{
								hmStyle = (LinkedHashMap)objStyle;
							}
							if (hmStyle != null && hmStyle.containsKey("formatter"))
							{
								if (!strValue.contains("\"") && hmStyle.get("formatter").toString().indexOf("0.00_") >= 0  && !((String)objFormula).endsWith("+ 0.000000001, 2)"))
								{
									strValue =  "ROUND(" + strValue + " + 0.000000001, 2)";
								}
							}
						}
						cell.put("formula", strValue);
						if (cell.containsKey("value"))
						{
							cell.remove("value");
						}
					}
					
				}
			}
//			if (corpvo.getVprovince() != null && corpvo.getVprovince() == 2)	//北京
//			{
//				
//			}
//			else
//			{
//				fillSpreadReportHeadPublic(mapJson, sheetname, corpvo, reportvo);
//			}
		}


		if (listDeleteSheet.size() > 0)
		{
			for (String key : listDeleteSheet)
			{
				hmsheets.remove(key);
			}
		}

		
		mapJson.put("sheetCount", listRptName.size());
		mapJson.put("activeSheetIndex", listRptName.indexOf(reportname));
		mapJson.put("startSheetIndex", listRptName.indexOf(reportname));

		return mapJson;
	}
	
	/**
	 * 财务报表增加表头默认值
	 */
	public void setDefaultHeadValue(Map mapJson,String sheetname, TaxReportVO reportvo, CorpVO corpvo,CorpTaxVo corptaxvo){
		String corpname = "编制单位："+corpvo.getUnitname();
		String ymd = "期间："+reportvo.getPeriodto();
		String ym = "期间："+reportvo.getPeriodto().substring(0, 7);
		if("C1".equals(reportvo.getSb_zlbh()) || "C2".equals(reportvo.getSb_zlbh())||"C101".equals(reportvo.getSb_zlbh()) || "C201".equals(reportvo.getSb_zlbh())
				||"C3".equals(reportvo.getSb_zlbh()) ||"C301".equals(reportvo.getSb_zlbh())|| "C4".equals(reportvo.getSb_zlbh()) || "C401".equals(reportvo.getSb_zlbh()) //|| "C401".equals(reportvo.getSb_zlbh())
				|| "39801".equals(reportvo.getSb_zlbh()) || "39806".equals(reportvo.getSb_zlbh())){
			if("资产负债表".equals(sheetname)){
				setCellValue(mapJson, sheetname, 2, 0, corpname);//公司名称
				setCellValue(mapJson, sheetname, 2, 3, ymd);//年月日
			}else if("利润表".equals(sheetname)){
				setCellValue(mapJson, sheetname, 2, 0, corpname);//公司名称
				setCellValue(mapJson, sheetname, 2, 1, ym);//年月
			}else if("现金流量表".equals(sheetname)){
				setCellValue(mapJson, sheetname, 2, 0, corpname+"           "+ymd+"                            单位：元");//公司名称
			}
		} else if (corptaxvo.getTax_area() != null && corptaxvo.getTax_area() == 11
				&& "10412".equals(reportvo.getSb_zlbh())) {
			// 所得税
			if ("A202000企业所得税汇总纳税分支机构所得税分配表".equals(sheetname))
			{
				setCellValue(mapJson, sheetname, 2, 2, corpvo.getUnitname());
				setCellValue(mapJson, sheetname, 3, 2, corpvo.getVsoccrecode());
			}
		}
	}
	
	public String getFormularBetweenTable(List<String> listRptName, String strValue){
		String funcname = null;
		String funcpara = null;
		int iPosFrom = -1;
		int iPosTo = -1;
		
		Pattern p = Pattern.compile(getTableNamePattern(listRptName));
		Matcher m = p.matcher(strValue);
		while(m.find()){
			funcname = m.group(1);
			funcpara = m.group(2);
			iPosFrom = m.start();
			iPosTo = m.end();
			
			if(!funcname.contains("'")){
				strValue = new StringBuffer().append((iPosFrom > 0 ? strValue.substring(0, iPosFrom) : ""))
									.append("'")
									.append(funcname.substring(0, funcname.length() - 1))
									.append("'" )
									.append(funcname.substring(funcname.length() - 1))
									.append(funcpara)
									.append((iPosTo < strValue.length() ? strValue.substring(iPosTo) : "")).toString();
			
				m = p.matcher(strValue);
			}
			
		}
		
		List<String> tableList = getTableNameList(listRptName);
		if(tableList != null && tableList.size() > 0){//是否是全集
			p = Pattern.compile(getTableNamePattern(getTableNameList(listRptName)));
			m = p.matcher(strValue);
			
			while(m.find()){
				funcname = m.group(1);
				funcpara = m.group(2);
				iPosFrom = m.start();
				iPosTo = m.end();
				
				strValue = (iPosFrom > 0 ? strValue.substring(0, iPosFrom) : "") + new DZFDouble().toString() + (iPosTo < strValue.length() ? strValue.substring(iPosTo) : "");
				
				m = p.matcher(strValue);
			}
		}
		
		return strValue;
	}
	
	private String getTableNamePattern(List<String> list){
		String tabpattern = "(";
		
        for(String s : list){
        	s = s.replace("(", "\\(").replace(")", "\\)");
        	tabpattern += (tabpattern.length() > 1 ? "|" : "") + s + "!|'" + s + "'!";
        }
        tabpattern += ")((\\$)?.(\\$)?[0-9]{1,})";
		return tabpattern;
	}
	
	private List<String> getTableNameList(List<String> listRptName){
		String[] listAll = new String[]{
				"A000000企业基础信息表",
				"A100000中华人民共和国企业所得税年度纳税申报表（A类）",
				"A101010一般企业收入明细表",
				"A101020金融企业收入明细表",
				"A102010一般企业成本支出明细表",
				"A102020金融企业支出明细表",
				"A103000事业单位、民间非营利组织收入、支出明细表",
				"A104000期间费用明细表",
				"A105000纳税调整项目明细表",
				"A105010视同销售和房地产开发企业特定业务纳税调整明细表",
				"A105020未按权责发生制确认收入纳税调整明细表",
				"A105030投资收益纳税调整明细表",
				"A105040专项用途财政性资金纳税调整表",
				"A105050职工薪酬纳税调整明细表",
				"A105060广告费和业务宣传费跨年度纳税调整明细表",
				"A105070捐赠支出纳税调整明细表",
				"A105080资产折旧、摊销情况及纳税调整明细表",
//				"A105081固定资产加速折旧、扣除明细表",
				"A105090资产损失税前扣除及纳税调整明细表",
//				"A105091资产损失（专项申报）税前扣除及纳税调整明细表",
				"A105100企业重组纳税调整明细表",
				"A105110政策性搬迁纳税调整明细表",
				"A105120特殊行业准备金纳税调整明细表",
				"A106000企业所得税弥补亏损明细表",
				"A107010免税、减计收入及加计扣除优惠明细表",
				"A107011股息红利优惠明细表",
				"A107012研发费用加计扣除优惠明细表",
//				"A107012综合利用资源生产产品取得的收入优惠明细表",
//				"A107013金融保险等机构取得涉农利息保费收入优惠明细表",
//				"A107014研发费用加计扣除优惠明细表",
				"A107020所得减免优惠明细表",
				"A107030抵扣应纳税所得额明细表",
				"A107040减免所得税优惠明细表",
				"A107041高新技术企业优惠情况及明细表",
				"A107042软件、集成电路企业优惠情况及明细表",
				"A107050税额抵免优惠明细表",
				"A108000境外所得税收抵免明细表",
				"A108010境外所得纳税调整后所得明细表",
				"A108020境外分支机构弥补亏损明细表",
				"A108030跨年度结转抵免境外所得税明细表",
				"A109000跨地区经营汇总纳税企业年度分摊企业所得税明细表",
				"A109010企业所得税汇总纳税分支机构所得税分配表",

				"增值税纳税申报表（适用于增值税一般纳税人）",
				"增值税纳税申报表附列资料（一）",
				"增值税纳税申报表附列资料（二）",
				"增值税纳税申报表附列资料（三）",
				"增值税纳税申报表附列资料（四）",
				"固定资产（不含不动产）进项税额抵扣情况表",
				"代扣代缴税收通用缴款书抵扣清单",
				"应税服务扣除项目清单",
				"成品油购销存情况明细表",
				"增值税减免税申报明细表",
				"本期抵扣进项税额结构明细表",
				"增值税纳税申报表附列资料（五）",
				
				"汇总纳税分支机构所得税分配表",
				"不征税收入和税基类减免应纳税所得额明细表（附表1）",
				"固定资产加速折旧(扣除)明细表（附表2）",
				"减免所得税优惠明细表(附表3)"
		};
		List<String> tableList = new ArrayList<String>(Arrays.asList(listAll));
		tableList.removeAll(listRptName);//删除存在的表
		return tableList;
	}
	
	public Map initOldReport(boolean readonly, Map mapJson, List<String> listRptName, String reportname, TaxReportVO reportvo, CorpVO corpvo) throws DZFWarpException
	{
		//滚动条和页签比例
		mapJson.put("tabStripRatio", 0.75);
		LinkedHashMap hmsheets = (LinkedHashMap)mapJson.get("sheets");
		Iterator iter = hmsheets.entrySet().iterator();

		
		
		Map<String, String> hmSheetValue = null;
		Map.Entry entry = null;
		//报表名称
		String sheetname = null;

		
		LinkedHashMap hmsheet = null;
		LinkedHashMap hmdata = null;
		LinkedHashMap hmdataTable = null;
		
		//找蓝色格子#66FFFF
		String sColorBlue = "#66FFFF";
		ArrayList<LinkedHashMap> listnamedStyles = null;
		Map<String, LinkedHashMap> hmBuleName = null;
		
		Iterator iterrow = null;
		
		//行号
		String row = null;
		//列集合
		LinkedHashMap cols = null;
		Iterator itercolumn = null;
			
		String column = null;
		LinkedHashMap cell = null;
			
		Object objFormula = null;

		Object objStyle = null;
			
		LinkedHashMap<String, Object> hmStyle = null;
		Object objBackColor = null;
		LinkedHashMap thisstyle = null;

		while (iter.hasNext())
		{
			hmSheetValue = new HashMap<String, String>();
			entry = (Map.Entry)iter.next(); 
			//报表名称
			sheetname = ((String)entry.getKey()).trim(); 

			
			hmsheet = (LinkedHashMap)entry.getValue(); 
			hmdata = (LinkedHashMap)hmsheet.get("data");
			hmdataTable = (LinkedHashMap)hmdata.get("dataTable");
			
			//找蓝色格子#66FFFF
			listnamedStyles = (ArrayList<LinkedHashMap>)hmsheet.get("namedStyles");
			
			Map<String, LinkedHashMap> hmStyles = new HashMap<String, LinkedHashMap>();
			hmBuleName = new HashMap<String, LinkedHashMap>();
			for (LinkedHashMap hmstyle : listnamedStyles)
			{
				if (sColorBlue.equals(hmstyle.get("backColor")))
				{
					hmBuleName.put(hmstyle.get("name").toString(), hmstyle);
				}
				hmStyles.put(hmstyle.get("name").toString(), hmstyle);
			}
			hmsheet.put("index", listRptName.indexOf(sheetname));
			//保护工作表
			hmsheet.put("isProtected", true);
			//缩放百分比
			hmsheet.put("zoomFactor", 1.3);

			iterrow = hmdataTable.keySet().iterator();
			//遍历行
			while (iterrow.hasNext())
			{
				//行号
				row = (String)iterrow.next();
				//列集合
				cols = (LinkedHashMap)hmdataTable.get(row);
				itercolumn = cols.keySet().iterator();
				//遍历列
				while (itercolumn.hasNext())
				{
					column = (String)itercolumn.next();
					cell = (LinkedHashMap)cols.get(column);
					
					//判断底色是否是浅蓝，浅蓝可编辑
					objStyle = cell.get("style");
					if (objStyle != null)
					{
						if (objStyle instanceof LinkedHashMap) 
						{
							hmStyle = (LinkedHashMap<String, Object>)objStyle;
							objBackColor = hmStyle.get("backColor");
							if (objBackColor != null && objBackColor.toString().equals(sColorBlue))
							{
								hmStyle.put("locked", readonly);
							}
						}
						else if (objStyle instanceof String)
						{
							if (hmBuleName.containsKey(objStyle.toString()))
							{
								thisstyle = hmBuleName.get(objStyle.toString());
								thisstyle.put("locked", readonly);
							}
						}
					}
					if (cell.containsKey("formula"))
					{
						objFormula = cell.get("formula");
						
						if (objFormula == null || objFormula.toString().trim().length() == 0) continue;
						objFormula = getFormularBetweenTable(listRptName, (String)objFormula);
						
						//判断cell是浮点型，两位小数，则处理浮点
						if (cell.containsKey("style"))
						{
							hmStyle = null;
							Object objCellStyle = cell.get("style");
							if (objCellStyle instanceof String)
							{
								hmStyle = hmStyles.get(objCellStyle);
							}
							else if (objCellStyle instanceof LinkedHashMap)
							{
								hmStyle = (LinkedHashMap)objStyle;
							}
							if (hmStyle != null && hmStyle.containsKey("formatter"))
							{
								if (hmStyle.get("formatter").toString().indexOf("0.00_") >= 0 && !((String)objFormula).endsWith("+ 0.000000001, 2)"))
								{
									objFormula =  "ROUND(" + objFormula + " + 0.000000001, 2)";
								}
							}
						}
						
						cell.put("formula", objFormula);
					}
					
				}
			}
		}

		mapJson.put("sheetCount", listRptName.size());
		mapJson.put("activeSheetIndex", listRptName.indexOf(reportname));
		mapJson.put("startSheetIndex", listRptName.indexOf(reportname));

		return mapJson;
	}
	public DZFDouble getQsbbqsData(Map mapJson, String reportname, String x, String y) throws DZFWarpException
	{
		DZFDouble dReturn = null;
		LinkedHashMap hmsheets = (LinkedHashMap)mapJson.get("sheets");
		Iterator iter = hmsheets.entrySet().iterator();
		
		Map<String, String> hmSheetValue = null;
		Map.Entry entry = null;
		//报表名称
		String sheetname = null;

		
		LinkedHashMap hmsheet = null;
		LinkedHashMap hmdata = null;
		LinkedHashMap hmdataTable = null;
		
		Iterator iterrow = null;
		
		//行号
		String row = null;
		//列集合
		LinkedHashMap cols = null;
		Iterator itercolumn = null;
			
		String column = null;
		LinkedHashMap cell = null;
		
		Object sReturn = null;
				
		while (iter.hasNext())
		{
			hmSheetValue = new HashMap<String, String>();
			entry = (Map.Entry)iter.next(); 
			//报表名称
			sheetname = ((String)entry.getKey()).trim(); 

			if(StringUtil.isEmptyWithTrim(sheetname)
					|| !sheetname.equals(reportname))
				continue;
			
			hmsheet = (LinkedHashMap)entry.getValue(); 
			hmdata = (LinkedHashMap)hmsheet.get("data");
			hmdataTable = (LinkedHashMap)hmdata.get("dataTable");
			
			iterrow = hmdataTable.keySet().iterator();
			//遍历行
			while (iterrow.hasNext())
			{
				//行号
				row = (String)iterrow.next();
				
				if(!row.equals(y))
					continue;//如不是跳过
				//列集合
				cols = (LinkedHashMap)hmdataTable.get(row);
				itercolumn = cols.keySet().iterator();
				//遍历列
				while (itercolumn.hasNext())
				{
					column = (String)itercolumn.next();
					if(!column.equals(x))
						continue;
					
					cell = (LinkedHashMap)cols.get(column);
					
					sReturn = cell.get("value");
					
					if(sReturn != null){
						dReturn = getDZFDouble(sReturn);
//						if(sReturn instanceof Integer){
//							dReturn = new DZFDouble((Integer)sReturn);
//						}else if(sReturn instanceof String){
//							dReturn =  new DZFDouble((String)sReturn);
//						}
					}
					
					break;
				}
			}
		}

		return dReturn;
	}
	
	private DZFDouble getDZFDouble(Object value) {
		if (value == null || value.toString().trim().equals("")) {
			return DZFDouble.ZERO_DBL;
		} else if (value instanceof DZFDouble) {
			return (DZFDouble) value;
		} else if (value instanceof BigDecimal) {
			return new DZFDouble((BigDecimal) value);
		} else {
			return new DZFDouble(value.toString().trim());
		}
	}
	
	public Map adjustEditCell(Map mapJson) throws DZFWarpException
	{
		//滚动条和页签比例
		mapJson.put("tabStripRatio", 0.75);
		LinkedHashMap hmsheets = (LinkedHashMap)mapJson.get("sheets");
		Iterator iter = hmsheets.entrySet().iterator();

		
		
		
		
		Map<String, String> hmSheetValue = null;
		Map.Entry entry = null;
		//报表名称
		String sheetname = null;

		LinkedHashMap hmsheet = null;
		LinkedHashMap hmdata = null;
		LinkedHashMap hmdataTable = null;
		
		//找蓝色格子#66FFFF
		String sColorBlue = "#66FFFF";
		ArrayList<LinkedHashMap> listnamedStyles = null;
		Map<String, LinkedHashMap> hmBuleName = null;
		
		Iterator iterrow = null;
		
		//行号
		String row = null;
		//列集合
		LinkedHashMap cols = null;
		Iterator itercolumn = null;
		
		String column = null;
		LinkedHashMap cell = null;
				
		Object objStyle = null;
				
		LinkedHashMap<String, Object> hmStyle = null;
		Object objBackColor = null;
						
		LinkedHashMap thisstyle = null;
							
		while (iter.hasNext())
		{
			hmSheetValue = new HashMap<String, String>();
			entry = (Map.Entry)iter.next(); 
			//报表名称
			sheetname = ((String)entry.getKey()).trim(); 

			hmsheet = (LinkedHashMap)entry.getValue(); 
			hmdata = (LinkedHashMap)hmsheet.get("data");
			hmdataTable = (LinkedHashMap)hmdata.get("dataTable");
			
			//找蓝色格子#66FFFF
			listnamedStyles = (ArrayList<LinkedHashMap>)hmsheet.get("namedStyles");
			hmBuleName = new HashMap<String, LinkedHashMap>();
			for (LinkedHashMap hmstyle : listnamedStyles)
			{
				if (sColorBlue.equals(hmstyle.get("backColor")))
				{
					hmBuleName.put(hmstyle.get("name").toString(), hmstyle);
				}
			}
			//保护工作表
			hmsheet.put("isProtected", true);
			//缩放百分比
			hmsheet.put("zoomFactor", 1.3);

			iterrow = hmdataTable.keySet().iterator();
			//遍历行
			while (iterrow.hasNext())
			{
				//行号
				row = (String)iterrow.next();
				//列集合
				cols = (LinkedHashMap)hmdataTable.get(row);
				itercolumn = cols.keySet().iterator();
				//遍历列
				while (itercolumn.hasNext())
				{
					column = (String)itercolumn.next();
					cell = (LinkedHashMap)cols.get(column);
					
					//判断底色是否是浅蓝，浅蓝可编辑
					objStyle = cell.get("style");
					if (objStyle != null)
					{
						if (objStyle instanceof LinkedHashMap) 
						{
							hmStyle = (LinkedHashMap<String, Object>)objStyle;
							objBackColor = hmStyle.get("backColor");
							if (objBackColor != null && objBackColor.toString().equals(sColorBlue))
							{
								hmStyle.put("locked", false);
							}
						}
						else if (objStyle instanceof String)
						{
							if (hmBuleName.containsKey(objStyle.toString()))
							{
								thisstyle = hmBuleName.get(objStyle.toString());
								thisstyle.put("locked", false);
							}
						}
					}
				}
			}

		}


		mapJson.put("startSheetIndex", 0);

		return mapJson;
	}
	/**
	 * 处理公式表达式
	 * @param mapValue 已计算出的公式结果缓存
	 * @param objFormula
	 * @param hmQCData
	 * @param corpvo
	 * @param reportvo
	 * @return
	 */
	public String getFormulaValue(Map mapValue, Object objFormula, HashMap<String, Object> hmQCData, CorpVO corpvo, TaxReportVO reportvo,YntCpaccountVO[] accountVO)
	{
		String funcname = null;
		String funcpara = null;
		int iPosFrom = -1;
		int iPosTo = -1;
		
		Matcher m2 = null;
		
		//递归执行内部
		int iPosFrom2 = -1;
		int iPosTo2 = -1;
		String ret = null;

		String qckey = null;	
		String qcvalue = null;
		//取期初数据, 取期末数据, 取发生数据, 取净发生数据, 取累计发生数据， 取发生数据2, 取凭证张数
		String[] formulas = new String[] {"glopenbal", "glclosebal", "glamtoccr", "glnetamtoccr", "glcumulamtoccr", "glamtoccr2"
						, "trans","trans1", "glamtoccr3", "glamtoccr4", "revenue", "revenue2", "revenue3", "costs", "costs2", "costs3", "profitbeforetax"
						, "profitbeforetax2", "profitbeforetax3", "tax", "datasources", "thsmtamt", "qdinc", "qdout"
						, "ivnumber", "sbbqs", "yhzc", "zcfzb", "swbb","lrb", "jmbl", "taxnd", "glbalnc","sqldata", "accountplan", "switch" 
						, "zcfzbqmye","zcfzbncye","lrbbyje","lrbbjje","lrbbnljje","lrbbqje","lrbsqje","xjllljje","xjllbyje","xjllbqje","xjllsqje"
						,"xjllnbsnje","lrbnbsnje","cjssl","gslx","subjectamt","subjectamt2","national","beginning","genera", "gsxx", "getsbxx", "deficit",
						"deduction","zcbmqmye","zcbmncye","lrbmbqje","lrbmsqje","xjbmbqje","xjbmsqje","lrbmljje","lrbmbyje","xjbmljje","xjbmbyje","lrbmsnje","xjbmsnje"
		};
		
		String formulaStr = null;
		Object dzfValue = null;
		
		
		
		
		
		
		
		
		
		
		String strValue = objFormula.toString().trim().replaceAll("“", "\"").replaceAll("”", "\"");
		String strValue_lc = strValue.toLowerCase();
		
		//正则
		String regex = "(qc|glopenbal|glclosebal|glamtoccr|glnetamtoccr|glcumulamtoccr|glamtoccr2|trans|trans1|glamtoccr3|glamtoccr4|revenue|revenue2|revenue3|costs|costs2|costs3|profitbeforetax|profitbeforetax2|profitbeforetax3|tax|datasources|thsmtamt|qdinc|qdout|ivnumber|sbbqs|yhzc|zcfzb|swbb|lrb|jmbl|taxnd|glbalnc|sqldata|accountplan|switch"
				+ "|zcfzbqmye|zcfzbncye|lrbbyje|lrbbjje|lrbbnljje|lrbbqje|lrbsqje|xjllljje|xjllbyje|xjllbqje"
				+ "|xjllsqje|xjllnbsnje|lrbnbsnje|cjssl|gslx|subjectamt|subjectamt2"
				+ "|national|beginning|genera|gsxx|getsbxx|deficit|deduction|zcbmqmye|zcbmncye|lrbmbqje|lrbmsqje|xjbmbqje|xjbmsqje"
				+ "|lrbmljje|lrbmbyje|xjbmljje|xjbmbyje|lrbmsnje|xjbmsnje)\\(([^\\(\\)]*?)\\)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(strValue_lc);
		
		while(m.find()){
			funcname = m.group(1);
			funcpara = m.group(2);
			iPosFrom = m.start();
			iPosTo = m.end();	
			
			m2 = p.matcher(funcpara);
			if (m2.find())
			{
				//递归执行内部
				iPosFrom2 = m2.start();
				iPosTo2 = m2.end();
				ret = getFormulaValue(mapValue, strValue.substring(funcname.length() + 1, iPosTo - 1), hmQCData, corpvo, reportvo, accountVO);

				strValue = (iPosFrom2 > 0 ? strValue.substring(0, iPosFrom2) : "") + ret + (iPosTo2 < strValue.length() ? strValue.substring(iPosTo2) : "");
				strValue_lc = (iPosFrom2 > 0 ? strValue_lc.substring(0, iPosFrom2) : "") + ret + (iPosTo2 < strValue_lc.length() ? strValue_lc.substring(iPosTo2) : "");
			}
			else
			{
	
	
				if (funcname.trim().equals("qc"))
				{
					qckey = funcpara.trim();
					qcvalue = "0";
					//企业所得税，汇总纳税分支机构所得税分配表//zpm
					//qc(sb10412005vo.data1.fzjgqk.fzjgnsrsbh_0)
					//qc(sb10412005vo.data1.fzjgqk.fzjgnsrsbh_1)
					//qc(sb10412005vo.data1.fzjgqk.fzjgnsrsbh_2)
					if(qckey.startsWith("sb10412005vo.data1.fzjgqk")){
						qcvalue = getFZjgqkbiaoinfo(qckey,hmQCData);
					}
					
					if (hmQCData.containsKey(qckey))
					{
						qcvalue = hmQCData.get(qckey).toString();
					}
					qcvalue ="\""+qcvalue+"\"";
					strValue = (iPosFrom > 0 ? strValue.substring(0, iPosFrom) : "") + qcvalue + (iPosTo < strValue.length() ? strValue.substring(iPosTo) : "");
					strValue_lc = (iPosFrom > 0 ? strValue_lc.substring(0, iPosFrom) : "") + qcvalue + (iPosTo < strValue_lc.length() ? strValue_lc.substring(iPosTo) : "");
				}
				else
				{
					//取期初数据, 取期末数据, 取发生数据, 取净发生数据, 取累计发生数据
					//formulas = new String[] {"glopenbal", "glclosebal", "glamtoccr", "glnetamtoccr", "glcumulamtoccr"};
			
		lab:		for (int i = 0; i < formulas.length; i++)
					{
						if (formulas[i].equals(funcname) == false)
						{
							continue lab;
						}
						//替换大账房取数函数的运行结果
		
						formulaStr = strValue_lc.substring(iPosFrom, iPosTo);
						if (mapValue != null)
						{
							String key = formulaStr.replaceAll("“", "\"").replaceAll("”", "\"").replaceAll("‘", "'").replaceAll("’", "'").replaceAll(" ", "").toLowerCase();
							if (mapValue.containsKey(key))
							{
								dzfValue = mapValue.get(key);
							}
							else
							{
								dzfValue = calculateDZFFormula(formulaStr, corpvo, reportvo.getPeriodto(),reportvo,accountVO);
								mapValue.put(key, dzfValue);
							}
						}
						else
						{
							dzfValue = calculateDZFFormula(formulaStr, corpvo, reportvo.getPeriodto(),reportvo,accountVO);
						}
						
						strValue = (iPosFrom > 0 ? strValue.substring(0, iPosFrom) : "") + dzfValue.toString() + (iPosTo < strValue.length() ? strValue.substring(iPosTo) : "");
						strValue_lc = (iPosFrom > 0 ? strValue_lc.substring(0, iPosFrom) : "") + dzfValue.toString() + (iPosTo < strValue_lc.length() ? strValue_lc.substring(iPosTo) : "");
					}
				}
			}
			m = p.matcher(strValue_lc);
		}
		return strValue;
	}
	
	
	/**
	 * 取分支机构情况表
	 */
	private String getFZjgqkbiaoinfo(String qckey,HashMap<String, Object> hmQCData){
		String qcvalue = "";
		if(hmQCData.get("sb10412005vo.data1.fzjgqk")==null)
			return qcvalue;
		if(!qckey.startsWith("sb10412005vo.data1.fzjgqk"))
			return qcvalue;
		String[] qckeys = qckey.split("_");
		if(qckeys.length!=2)
			return qcvalue;
		List<Object> list = (List<Object>)hmQCData.get("sb10412005vo.data1.fzjgqk");
		int index = Integer.valueOf(qckeys[1]);//公式从0开始
		if(index>=list.size())
			return qcvalue;
		FzjgRptVO rptvo = (FzjgRptVO)list.get(index);
		if(qckey.contains("fzjgnsrsbh")){//分支机构纳税人识别号
			qcvalue = "\""+rptvo.getFzjgnsrsbh()+"\"";
		}else if(qckey.contains("fzjgmc")){//分支机构名称
			qcvalue = "\""+rptvo.getFzjgmc()+"\"";
		}else if(qckey.contains("fzjgsrze")){//营业收入
			DZFDouble sr = rptvo.getFzjgsrze();
			qcvalue = sr == null?"":sr.toString();
		}else if(qckey.contains("fzjggzze")){//职工薪酬
			DZFDouble ze = rptvo.getFzjggzze();;
			qcvalue = ze == null?"":ze.toString();
		}else if(qckey.contains("fzjgzcze")){//资产总额
			DZFDouble zcze = rptvo.getFzjgzcze();
			qcvalue = zcze == null?"":zcze.toString();
		}
		return qcvalue;
	}
	
	private void fillReportByQcData(Map mapJson,String sheetname, CorpVO corpvo, CorpTaxVo taxvo, TaxReportVO reportvo, HashMap<String, Object> hmQCData) {
		if (taxvo.getTax_area() == 11 ) {//江苏
			if (TaxRptConst.SB_ZLBHD1.equals(reportvo.getSb_zlbh())
					&& "印花税纳税申报表".equals(sheetname)) {
				List<Map<String, Object>> qcMaps = (List<Map<String, Object>>) hmQCData.get(reportvo.getSb_zlbh() + "qc");
				if (qcMaps != null) {
					int row = 6;
					for (Map<String, Object> map : qcMaps) {
						setCellValue(mapJson, sheetname, row, 0, (String) map.get("zspmmc"));
						setCellValue(mapJson, sheetname, row, 4, (String) map.get("hd_jsje"));
						setCellValue(mapJson, sheetname, row, 5, (String) map.get("hd_bl"));
						setCellValue(mapJson, sheetname, row, 6, (String) map.get("sl"));
						row++;
					}
				}
			} else if (TaxRptConst.SB_ZLBH_LOCAL_FUND_FEE.equals(reportvo.getSb_zlbh())
					&& "地方各项基金费（工会经费）申报表".equals(sheetname)) {
				List<Map<String, Object>> qcMaps = (List<Map<String, Object>>) hmQCData.get(reportvo.getSb_zlbh() + "qc");
				if (qcMaps != null) {
					int row = 7;
					for (Map<String, Object> map : qcMaps) {
						if (map.containsKey("zspmmc")) {
							setCellValue(mapJson, sheetname, row, 0, (String) map.get("zspmmc"));
						}
						if (map.containsKey("zszmmc")) {
							setCellValue(mapJson, sheetname, row, 1, (String) map.get("zszmmc"));
						}
						if (map.containsKey("jzl")) {
							setCellValue(mapJson, sheetname, row, 5, (String) map.get("jzl"));
						}
						row++;
					}
				}
			}
		}else if (taxvo.getTax_area() == 16 ) {//山东
			if (TaxRptConst.SB_ZLBHD1.equals(reportvo.getSb_zlbh())
					&& "印花税纳税申报表".equals(sheetname)) {
				List<Map<String, Object>> qcMaps = (List<Map<String, Object>>) hmQCData.get(reportvo.getSb_zlbh() + "qc");
				if (qcMaps != null) {
					int row = 9;
					for (Map<String, Object> map : qcMaps) {
						setCellValue(mapJson, sheetname, row, 0, (String) map.get("zspmDm")+"|"+(String) map.get("zspmMc"));
						setCellValue(mapJson, sheetname, row, 2, (String) map.get("hdyj"));
						setCellValue(mapJson, sheetname, row, 3, (String) map.get("hdbl"));
						setCellValue(mapJson, sheetname, row, 4, (String) map.get("sysl"));
						row++;
					}
				}
			} 
		}
	}
	/**
	 * 填充spreadJS报表公共表头信息
	 * @throws DZFWarpException
	 */
	private void fillSpreadReportHeadPublic(Map mapJson, String reportname, CorpVO corpvo,CorpTaxVo taxvo, TaxReportVO reportvo) throws DZFWarpException
	{
		String corp_nsrmc = (corpvo.getUnitname() == null ? "" : corpvo.getUnitname());
		String corp_nsrsbh = (taxvo.getTaxcode() == null ? "" : taxvo.getTaxcode());

		String sksssjAndTbrqAndJedwyjf = "税款所属时间：" + reportvo.getPeriodfrom().substring(0, 4) + "年"
				+ reportvo.getPeriodfrom().substring(5, 7)+"月"
				+ reportvo.getPeriodfrom().substring(8, 10) + "日至"
				+ reportvo.getPeriodto().substring(0, 4) + "年"
				+ reportvo.getPeriodto().substring(5, 7) + "月"
				+ reportvo.getPeriodto().substring(8, 10) + "日            填表日期："
				+ reportvo.getDoperatedate().getYear() + "年"
				+ reportvo.getDoperatedate().getMonth() + "月"
				+ reportvo.getDoperatedate().getDay() + "日                    金额单位：元至角分";
		String tbrqAndJedwyjf = "填表日期："
				+ reportvo.getDoperatedate().getYear() + "年"
				+ reportvo.getDoperatedate().getMonth() + "月"
				+ reportvo.getDoperatedate().getDay() + "日                    金额单位：元至角分";
		String sksssj = "税款所属时间：" + reportvo.getPeriodfrom().substring(0, 4) + "年"
				+ reportvo.getPeriodfrom().substring(5, 7)+"月"
				+ reportvo.getPeriodfrom().substring(8, 10) + "日至"
				+ reportvo.getPeriodto().substring(0, 4) + "年"
				+ reportvo.getPeriodto().substring(5, 7) + "月"
				+ reportvo.getPeriodto().substring(8, 10) + "日";
		String nsrmcAndJedwyjf = "纳税人名称：（公章）  " + corp_nsrmc + "                  金额单位：元至角分";
		
		String nsrmcAndNsrsbh = "纳税人名称（盖章）：" + corp_nsrmc + "      纳税人识别号：" + corp_nsrsbh;
		
		String sksssjAndJedwyjf = "税款所属时间：" + reportvo.getPeriodfrom().substring(0, 4) + "年"
				+ reportvo.getPeriodfrom().substring(5, 7)+"月"
				+ reportvo.getPeriodfrom().substring(8, 10) + "日至"
				+ reportvo.getPeriodto().substring(0, 4) + "年"
				+ reportvo.getPeriodto().substring(5, 7) + "月"
				+ reportvo.getPeriodto().substring(8, 10) + "日                                金额单位：元（列至角分）（共  页，第  页）";
		
		String sksssjAndTbrq = "税款所属期：" + reportvo.getPeriodfrom().substring(0, 4) + "年"
				+ reportvo.getPeriodfrom().substring(5, 7)+"月"
				+ reportvo.getPeriodfrom().substring(8, 10) + "日至"
				+ reportvo.getPeriodto().substring(0, 4) + "年"
				+ reportvo.getPeriodto().substring(5, 7) + "月"
				+ reportvo.getPeriodto().substring(8, 10) + "日            填表日期："
				+ reportvo.getDoperatedate().getYear() + "年"
				+ reportvo.getDoperatedate().getMonth() + "月"
				+ reportvo.getDoperatedate().getDay() + "日";
		
		String nsrmc = "纳税人名称：" + corp_nsrmc;
		String nsrmcAndGZ = "纳税人名称（公章）：" + corp_nsrmc;
		String nsrsbh = "纳税人识别号:" + corp_nsrsbh;
		//申报种类编号
		String sb_zlbh = reportvo.getSb_zlbh();
		
		if ("10101".equals(sb_zlbh))
		{
			//一般纳税人
			if ("增值税纳税申报表".equals(reportname))
			{
				setCellValue(mapJson, reportname, 3, 0, sksssjAndTbrqAndJedwyjf);
				//纳税人识别号
				setCellValue(mapJson, reportname, 4, 3, taxvo.getTaxcode());
				//纳税人名称
				setCellValue(mapJson, reportname, 5, 3, corpvo.getUnitname());
			}
			else if ("增值税纳税申报表附列资料（一）".equals(reportname)
					|| "增值税纳税申报表附列资料（二）".equals(reportname)
					|| "增值税纳税申报表附列资料（三）".equals(reportname)
					|| "增值税纳税申报表附列资料（四）".equals(reportname)
					|| "增值税纳税申报表附列资料（五）".equals(reportname))
			{
				setCellValue(mapJson, reportname, 2, 0, sksssj);
	
				setCellValue(mapJson, reportname, 3, 0, nsrmcAndJedwyjf);
			}
			else if ("固定资产（不含不动产）进项税额抵扣情况表".equals(reportname) ||
					"应税服务减免项目清单表".equals(reportname) ||
					
					"本期抵扣进项税额结构明细表".equals(reportname) ||
					"营改增税负分析测算明细表".equals(reportname))
			{
				setCellValue(mapJson, reportname, 1, 0, sksssj);
	
				setCellValue(mapJson, reportname, 2, 0, nsrmcAndJedwyjf);
			}
			else if ("代扣代缴税收通用缴款书抵扣清单".equals(reportname))
			{
				setCellValue(mapJson, reportname, 1, 0, nsrmcAndNsrsbh);
	
				setCellValue(mapJson, reportname, 2, 0, sksssjAndJedwyjf);
			}
			else if ("成品油购销存情况明细表".equals(reportname) ||
					"生产企业进料加工抵扣明细表".equals(reportname) ||
					"生产企业出口货物征（免）税明细主表".equals(reportname) ||
					"生产企业出口货物征（免）税明细从表".equals(reportname) ||
					"国际运输征免税明细数据表".equals(reportname) ||
					"增值税减免税申报明细表".equals(reportname) ||
					"研发、设计服务征免税明细数据表".equals(reportname))
					
			{
				setCellValue(mapJson, reportname, 1, 0, nsrmc);
				setCellValue(mapJson, reportname, 2, 0, nsrsbh);
	
				setCellValue(mapJson, reportname, 3, 0, sksssj);
			}
			else if ("农产品核定扣除增值税进项税额计算表（汇总表）".equals(reportname) ||
					"投入产出法核定农产品增值税进项税额计算表".equals(reportname) ||
					"成本法核定农产品增值税进项税额计算表".equals(reportname) ||
					"购进农产品直接销售核定农产品增值税进项税额计算表".equals(reportname) ||
					"购进农产品用于生产经营且不构成货物实体核定农产品增值税进项税额".equals(reportname))
			{
				setCellValue(mapJson, reportname, 1, 0, sksssj);
				setCellValue(mapJson, reportname, 2, 0, nsrsbh);
				setCellValue(mapJson, reportname, 3, 0, nsrmc);
			}
			else if ("部分产品销售统计表".equals(reportname))
			{
				setCellValue(mapJson, reportname, 1, 0, sksssj);
				setCellValue(mapJson, reportname, 2, 0, nsrmcAndGZ);
				setCellValue(mapJson, reportname, 3, 0, nsrsbh);
				setCellValue(mapJson, reportname, 4, 0, tbrqAndJedwyjf);
			}
			
		}
		else if ("10102".equals(sb_zlbh))
		{
			//小规模纳税人
			if ("增值税纳税申报表".equals(reportname))
			{
				setCellValue(mapJson, reportname, 2, 0, nsrsbh);
				setCellValue(mapJson, reportname, 3, 0, nsrmcAndJedwyjf);
				setCellValue(mapJson, reportname, 4, 0, sksssjAndTbrq);
			}
			else if ("增值税纳税申报表（小规模纳税人适用）附列资料".equals(reportname))
			{
				setCellValue(mapJson, reportname, 1, 0, sksssjAndTbrq);
				setCellValue(mapJson, reportname, 2, 0, nsrmcAndJedwyjf);
			}
			else if ("增值税纳税申报表附列资料（四）（税额抵减情况表）".equals(reportname))
			{
				setCellValue(mapJson, reportname, 1, 0, sksssj);
				setCellValue(mapJson, reportname, 2, 0, nsrmcAndNsrsbh);
				setCellValue(mapJson, reportname, 3, 0, tbrqAndJedwyjf);
			}
			else if ("增值税减免税申报明细表".equals(reportname))
			{
				setCellValue(mapJson, reportname, 1, 0, sksssj);
				setCellValue(mapJson, reportname, 2, 0, nsrmcAndJedwyjf);
			}
			else if ("应税服务减除项目清单".equals(reportname))
			{
				setCellValue(mapJson, reportname, 1, 0, nsrsbh);
				setCellValue(mapJson, reportname, 2, 0, nsrmcAndJedwyjf);
				setCellValue(mapJson, reportname, 3, 0, sksssjAndTbrq);
			}
		}
	}
	private Object calculateDZFFormula(String strFormula, CorpVO corpvo, String sDate,TaxReportVO reportvo,YntCpaccountVO[] accountVO)
	{
//		strFormula = strFormula.replaceAll("\"", "").replaceAll("'", "");
		DZFDouble dzfReturn = DZFDouble.ZERO_DBL;
		Object objReturn = null;

		//取期初数据, 取期末数据, 取发生数据, 取净发生数据, 取累计发生数据， 取发生数据2(多两个参数), 取凭证张数, 取季度发生数, 取季度发生数2(度两个参数), 按期间取取营业收入， 按季度取营业收入，按期间取营业成本， 按季度取营业成本， 按期间取利润总额， 按季度取利润总额, 是否设置带税率属性的科目, 清单取数
		//"glopenbal", "glclosebal", "glamtoccr", "glnetamtoccr", "glcumulamtoccr", "glamtoccr2", "trans", "glamtoccr3","glamtoccr4", "revenue", "revenue2", "costs", "costs2", "profitbeforetax", "profitbeforetax2" , "tax", "datasources"
		
		// 取进项清单  取销项清单  取认证发票数量  取除此科目分录行本期发生        取表格数    取政策优惠      资产负债表取数      取其他年度税务报表     利润表               取减免比率	取申报年度		取年初            判断公司科目方案   取营业收入从年初到现在的累计数   取营业成本年初到现在的累计数  取利润总额年初到现在的累计数
		// "qdinc",  "qdout",    "ivnumber", "thsmtamt",          "sbbqs",       "yhzc"      "zcfzb"         "swbb"          "lrb"    "jmbl"     "taxnd",       "glbalnc",  "accountplan",    "revenue3",    "costs3",   "profitbeforetax3"    

		int iLeft = strFormula.indexOf("(");
		int iRight = strFormula.length() - 1;
		String[] params = strFormula.substring(iLeft + 1, iRight).split(",");
		for (int i = 0; i < params.length; i++)
		{
			String param = params[i].replaceAll("\"", "").replaceAll("'", "").trim();
			params[i] = (StringUtil.isEmpty(param) ? null : param);
		}
		//处理科目参数
		String[] saAccsubj = null;
		DZFDate date = new DZFDate(sDate);
		//处理年度
		int iYear = date.getYear();
		//期间
		String period = null;
		//是否包含未记账 
		DZFBoolean isHasJZ = null;
		//方向
		String sDirection = null;
		//公司 pk_corp
		String pk_corp = corpvo.getPrimaryKey();
		
		if (strFormula.startsWith("glopenbal(") ||
				strFormula.startsWith("glclosebal(") ||
				strFormula.startsWith("glamtoccr") ||	//包含2，3，4末尾的了
				strFormula.startsWith("glnetamtoccr(") ||
				strFormula.startsWith("glcumulamtoccr(") ||
				strFormula.startsWith("beginning(") ||
				strFormula.startsWith("trans(") || 
				strFormula.startsWith("trans1(") ||
				strFormula.startsWith("glbalnc("))
		{
			
			if (params[0].indexOf("+") > 0)
			{
				saAccsubj = params[0].split("\\+");
			}
			else
			{
				saAccsubj = new String[1];
				saAccsubj[0] = params[0];
			}
	
			if (params[1].indexOf("+") > 0)
			{
				int iPlus = Integer.parseInt(params[1].split("\\+")[1].trim());
				iYear += iPlus;
			}
			else if (params[1].indexOf("-") > 0)
			{
				int iSub = Integer.parseInt(params[1].split("\\-")[1].trim());
				iYear -= iSub;
			}
			//处理期间
			int iPeriod = date.getMonth();
			if (params[2].indexOf("+") > 0)
			{
				int iPlus = Integer.parseInt(params[2].split("\\+")[1].trim());
				iPeriod += iPlus;
			}
			else if (params[2].indexOf("-") > 0)
			{
				int iSub = Integer.parseInt(params[2].split("\\-")[1].trim());
				iPeriod -= iSub;
			}
			//是否包含未记账 params[3]
			isHasJZ = new DZFBoolean(params[3].trim());
			
			period = "" + iYear + "-" + (iPeriod < 10 ? "0": "") + iPeriod;
		
			//方向 params[4]
			sDirection = params[4].trim();
			
			Map<String,List<QcYeVO>> beginMap = new HashMap<String,List<QcYeVO>>();
			for (String accsubj : saAccsubj)
			{
				if (strFormula.startsWith("glopenbal"))
				{
					dzfReturn = dzfReturn.add(taxbalance.getGlOpenBal(accsubj.trim(), period, sDirection, pk_corp, isHasJZ,accountVO));
				}else if (strFormula.startsWith("glbalnc"))
				{
					dzfReturn = dzfReturn.add(taxbalance.getGlBalNC(accsubj.trim(), period, sDirection, pk_corp, isHasJZ,accountVO));
				}
				else if (strFormula.startsWith("glclosebal"))
				{
					dzfReturn = dzfReturn.add(taxbalance.getGlCloseBal(accsubj.trim(), period, sDirection, pk_corp, isHasJZ,accountVO));
				}
				else if (strFormula.startsWith("glamtoccr2"))//增加开票信息、税率参数，按月取数
				{
					String invoiceflag = params[6];
					String taxrate = params[7];
					DZFDouble dzfTaxRate = (taxrate == null ? null : new DZFDouble(taxrate));
					dzfReturn = dzfReturn.add(taxbalance.getGlAmtoCcr2(accsubj.trim(), period, period, sDirection, pk_corp, isHasJZ, invoiceflag, dzfTaxRate,accountVO));
				}
				else if (strFormula.startsWith("glamtoccr3"))//按季度取数
				{
					if(reportvo.getPeriodtype() == PeriodType.jidureport){//季度
						dzfReturn = dzfReturn.add(taxbalance.getGlAmtoCcr3(accsubj.trim(), period, sDirection, pk_corp, isHasJZ,accountVO));
					}else if(reportvo.getPeriodtype() == PeriodType.monthreport){//月度
						dzfReturn = dzfReturn.add(taxbalance.getGlAmtoCcr(accsubj.trim(), period, period, sDirection, pk_corp, isHasJZ,accountVO));
					}
					
				}
				else if (strFormula.startsWith("glamtoccr4(")) //增加开票信息、税率参数，按季度取数
				{
					String invoiceflag = params[6];
					String taxrate = params[7];
					DZFDouble dzfTaxRate = (taxrate == null ? null : new DZFDouble(taxrate));

					if(reportvo.getPeriodtype() == PeriodType.jidureport){//季度
						dzfReturn = dzfReturn.add(taxbalance.getGlAmtoCcr4(accsubj.trim(), period, sDirection, pk_corp, isHasJZ, invoiceflag, dzfTaxRate,accountVO));
					}else if(reportvo.getPeriodtype() == PeriodType.monthreport){//月度
						dzfReturn = dzfReturn.add(taxbalance.getGlAmtoCcr2(accsubj.trim(), period, period, sDirection, pk_corp, isHasJZ, invoiceflag, dzfTaxRate,accountVO));
					}
					
				}
				else if (strFormula.startsWith("glamtoccr("))
				{
					dzfReturn = dzfReturn.add(taxbalance.getGlAmtoCcr(accsubj.trim(),
							reportvo.getPeriodfrom().substring(0, 7),
							reportvo.getPeriodto().substring(0, 7), sDirection, pk_corp, isHasJZ,accountVO));
				}
				else if (strFormula.startsWith("glnetamtoccr"))
				{
					dzfReturn = dzfReturn.add(taxbalance.getGlNetamtoCcr(accsubj.trim(), period, sDirection, pk_corp, isHasJZ,accountVO));
				}
				else if (strFormula.startsWith("glcumulamtoccr"))
				{
					dzfReturn = dzfReturn.add(taxbalance.getGlCumulamtoCcr(accsubj.trim(), period, sDirection, pk_corp, isHasJZ,accountVO));
				}
				else if (strFormula.startsWith("beginning"))
				{
					dzfReturn = dzfReturn.add(taxbalance.getGlBeginningfse(accsubj.trim(),sDirection, pk_corp,beginMap));
				}
				else if (strFormula.startsWith("trans("))
				{
					String invoiceflag = params[6];
					String taxrate = params[7];
					DZFDouble iTaxRate = (taxrate == null ? null : new DZFDouble(taxrate));
					dzfReturn = dzfReturn.add(taxbalance.getTrans(accsubj.trim(), period, sDirection, pk_corp, isHasJZ, invoiceflag, iTaxRate,accountVO));
				}
				else if (strFormula.startsWith("trans1("))
				{
					// 发票类型
					String invoiceflag = params[6];
					dzfReturn = dzfReturn.add(taxbalance.getTrans1(accsubj.trim(), period,
							sDirection, pk_corp, isHasJZ, invoiceflag, null));
				}
			}
		}
		else if  (strFormula.startsWith("revenue(")
				|| strFormula.startsWith("revenue2(")
				|| strFormula.startsWith("revenue3(")
				|| strFormula.startsWith("costs(")
				|| strFormula.startsWith("costs2(")
				|| strFormula.startsWith("costs3(")
				|| strFormula.startsWith("profitbeforetax(")
				|| strFormula.startsWith("profitbeforetax2(")
				|| strFormula.startsWith("profitbeforetax3("))
		{
			if (params[1].indexOf("+") > 0)
			{
				int iPlus = Integer.parseInt(params[1].split("\\+")[1].trim());
				iYear += iPlus;
			}
			else if (params[1].indexOf("-") > 0)
			{
				int iSub = Integer.parseInt(params[1].split("\\-")[1].trim());
				iYear -= iSub;
			}
			//处理期间
			int iPeriod = date.getMonth();
			if (params[2].indexOf("+") > 0)
			{
				int iPlus = Integer.parseInt(params[2].split("\\+")[1].trim());
				iPeriod += iPlus;
			}
			else if (params[2].indexOf("-") > 0)
			{
				int iSub = Integer.parseInt(params[2].split("\\-")[1].trim());
				iPeriod -= iSub;
			}
			period = "" + iYear + "-" + (iPeriod < 10 ? "0": "") + iPeriod;
			//是否包含未记账 params[3]
			isHasJZ = new DZFBoolean(params[3].trim());
			
			if (strFormula.startsWith("revenue2("))//带2的为季报的函数//zpm
			{
				if(reportvo.getPeriodtype() == PeriodType.jidureport){
					dzfReturn = dzfReturn.add(taxbalance.getRevenue2(pk_corp, period, isHasJZ));
				}else if(reportvo.getPeriodtype() == PeriodType.monthreport){//月报
					dzfReturn = dzfReturn.add(taxbalance.getRevenue(pk_corp, period, isHasJZ));
				}
				
			}
			else if (strFormula.startsWith("revenue("))
			{
				dzfReturn = dzfReturn.add(taxbalance.getRevenue(pk_corp, period, isHasJZ));
			}
			else if (strFormula.startsWith("revenue3("))
			{
				dzfReturn = dzfReturn.add(taxbalance.getRevenue3(pk_corp, period, isHasJZ));
			}
			else if (strFormula.startsWith("costs3("))
			{
				dzfReturn = dzfReturn.add(taxbalance.getCosts3(pk_corp, period, isHasJZ));
			}
			else if (strFormula.startsWith("costs2("))//带2的为季报的函数 //zpm
			{
				if(reportvo.getPeriodtype() == PeriodType.jidureport){
					dzfReturn = dzfReturn.add(taxbalance.getCosts2(pk_corp, period, isHasJZ));
				}else if(reportvo.getPeriodtype() == PeriodType.monthreport){//月报
					dzfReturn = dzfReturn.add(taxbalance.getCosts(pk_corp, period, isHasJZ));
				}
				
			}
			else if (strFormula.startsWith("costs("))
			{
				dzfReturn = dzfReturn.add(taxbalance.getCosts(pk_corp, period, isHasJZ));
			}
			else if (strFormula.startsWith("profitbeforetax3("))
			{
				dzfReturn = dzfReturn.add(taxbalance.getProfitBeforeTax3(pk_corp, period, isHasJZ));
			}
			else if (strFormula.startsWith("profitbeforetax2("))//带2的为季报的函数 //zpm
			{
				if(reportvo.getPeriodtype() == PeriodType.jidureport){
					dzfReturn = dzfReturn.add(taxbalance.getProfitBeforeTax2(pk_corp, period, isHasJZ));
				}else if(reportvo.getPeriodtype() == PeriodType.monthreport){//月报
					dzfReturn = dzfReturn.add(taxbalance.getProfitBeforeTax(pk_corp, period, isHasJZ));
				}
			}
			else if (strFormula.startsWith("profitbeforetax("))
			{
				dzfReturn = dzfReturn.add(taxbalance.getProfitBeforeTax(pk_corp, period, isHasJZ));
			}
		}
		else if  (strFormula.startsWith("tax("))
		{
			if (params[0].indexOf("+") > 0)
			{
				saAccsubj = params[0].split("\\+");
			}
			else
			{
				saAccsubj = new String[1];
				saAccsubj[0] = params[0];
			}
			for (String accsubj : saAccsubj)
			{
				//查科目的税率设置不用累加，只要发现即可返回
				Integer intRet = taxbalance.getTax(accsubj.trim(), pk_corp);
				if (intRet != null && intRet == 1)
				{
					return new DZFDouble("1");
				}
			}
		}
		else if  (strFormula.startsWith("datasources("))
		{
			Integer intSour = taxbalance.getDatasources(pk_corp);
			return new DZFDouble(intSour);
		}
		else if  (strFormula.startsWith("accountplan("))
		{
			dzfReturn = taxbalance.getAccountPlan(pk_corp);
		}
		else if  (strFormula.startsWith("cjssl("))
		{
			dzfReturn = taxbalance.getCjssl(pk_corp);
		}
		else if  (strFormula.startsWith("gsxx("))
		{
			//专项扣除
			String key =  params[0];
			if("yanglaobx".equals(key)|| "yiliaobx".equals(key)|| "shiyebx".equals(key)|| "zfgjj".equals(key)){
				CorpTaxVo corptaxvo  = null;
				if(cptaxmap != null && cptaxmap.size()>0 ){
					corptaxvo = cptaxmap.get(pk_corp);
				}else{
					corptaxvo = taxbalance.queryCorpTaxVO(pk_corp);
					cptaxmap = new HashMap<>();
					cptaxmap.put(pk_corp,corptaxvo);
				}
				List list  = null;
				if(listmap != null && listmap.size()>0 ){
					list = listmap.get(pk_corp);
				}else{
					list = taxbalance.querySpecChargeHis(pk_corp);
					listmap = new HashMap<>();
					listmap.put(pk_corp,list);
				}
				String eperiod = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
				objReturn =taxbalance.getSpecialdDeductionCumul(list,corptaxvo.getBegprodate(),null, eperiod, pk_corp, key);
			}else if("taxsale6".equals(key)){
				objReturn = taxbalance.getTaxSale6(pk_corp);
			}else{
				objReturn = taxbalance.getGsxx(pk_corp, params[0]);
			}
			if (objReturn != null && objReturn instanceof String) {
				objReturn = "\"" + objReturn + "\"";
			}
		}
		else if  (strFormula.startsWith("getsbxx("))
		{
			objReturn = reportvo.getAttributeValue(params[0]);
			if (objReturn != null && objReturn instanceof String) {
				objReturn = "\"" + objReturn + "\"";
			}
		}
		else if  (strFormula.startsWith("deficit("))
		{
			objReturn = taxbalance.getDeficit(pk_corp, iYear+"");
		}
		else if  (strFormula.startsWith("deduction("))
		{
			DZFDate begprodate = (DZFDate) taxbalance.getGsxx(pk_corp, "begprodate");
			dzfReturn = IncomeTaxCalculator
					.getInvestorDeduction(begprodate == null ? null : begprodate.toString(), reportvo.getPeriod());
		}
		else if  (strFormula.startsWith("gslx("))
		{
			dzfReturn = taxbalance.getGslx(pk_corp);
		}
		else if  (strFormula.startsWith("national("))
		{
			dzfReturn = taxbalance.getNational(pk_corp);
		}
		else if  (strFormula.startsWith("switch("))
		{
			objReturn = taxbalance.getSwitch(params);
			
		}
		else if  (strFormula.startsWith("jmbl("))
		{
			dzfReturn = taxbalance.getJmbl(params[0], pk_corp, iYear);
		}
		else if  (strFormula.startsWith("taxnd("))
		{//取申报年度
			dzfReturn = new DZFDouble(iYear);
		}
		else if  (strFormula.startsWith("qdinc(")
//				||  strFormula.startsWith("qdinamt(")
//				||  strFormula.startsWith("qdintxm(")
				||  strFormula.startsWith("qdout(")
//				||  strFormula.startsWith("qdouttxm(")
//				||  strFormula.startsWith("qdoutamt2(")
				||  strFormula.startsWith("ivnumber("))
		{
			
			if (params[1].indexOf("+") > 0)
			{
				int iPlus = Integer.parseInt(params[1].split("\\+")[1].trim());
				iYear += iPlus;
			}
			else if (params[1].indexOf("-") > 0)
			{
				int iSub = Integer.parseInt(params[1].split("\\-")[1].trim());
				iYear -= iSub;
			}
			//处理期间
			int iPeriod = date.getMonth();
			if (params[2].indexOf("+") > 0)
			{
				int iPlus = Integer.parseInt(params[2].split("\\+")[1].trim());
				iPeriod += iPlus;
			}
			else if (params[2].indexOf("-") > 0)
			{
				int iSub = Integer.parseInt(params[2].split("\\-")[1].trim());
				iPeriod -= iSub;
			}
			period = "" + iYear + "-" + (iPeriod < 10 ? "0": "") + iPeriod;
			
			String[] taxRateStr = null;
			DZFDouble[] taxRate = null;

			if(params.length > 5 && params[3] != null){
				if(params[3].indexOf("+") > 0)
				{
					taxRateStr = params[3].split("\\+");
					
					taxRate = new DZFDouble[taxRateStr.length];
					for(int i = 0; i < taxRateStr.length; i++){
						taxRate[i] = new DZFDouble(taxRateStr[i]);
					}
				}
				else
				{
					taxRateStr = new String[1];
					taxRateStr[0] = params[3];
					
					taxRate = new DZFDouble[1];
					taxRate[0] = new DZFDouble(params[3]);
				}
			}
			
			
//			if(strFormula.startsWith("qdinamt("))
//			{
//				Integer tickFlag = params[0] == null ? null :Integer.parseInt(params[0]);//专普票标识
//				String iv = params[4];
//				dzfReturn = taxbalance.getQDInAmt(pk_corp, period, tickFlag, taxRate, iv);
//			}
//			else if (strFormula.startsWith("qdintxm("))
//			{
//				Integer tickFlag = params[0] == null ? null :Integer.parseInt(params[0]);//专普票标识
//				String iv = params[4];
//				dzfReturn = taxbalance.getQDInTxm(pk_corp, period, tickFlag, taxRate, iv);
//			}
			if (strFormula.startsWith("qdinc("))
			{
				if(judgeSource(IParameterConstants.FROMSALT, pk_corp)){//判断是否清单取数
					Integer tickFlag = params[0] == null ? null :Integer.parseInt(params[0]);//专普票标识
					String iv = params[6];//认证未认证
					dzfReturn = taxbalance.getQDInc(pk_corp, period, tickFlag, taxRateStr, 
							params[4], params[5], iv, reportvo.getPeriodtype(), mnyoutmap);
				}
				
			}
			else if (strFormula.startsWith("qdout("))
			{
				if(judgeSource(IParameterConstants.FROMSALT, pk_corp)){//判断是否清单取数
					Integer tickFlag = params[0] == null ? null :Integer.parseInt(params[0]);//专普票标识
					dzfReturn =  taxbalance.getQDOut(pk_corp, period, tickFlag, taxRateStr, 
							params[4], params[5], reportvo.getPeriodtype(), mnyoutmap);
				}
			}
			else if (strFormula.startsWith("ivnumber("))
			{
				if(judgeSource(IParameterConstants.FROMSALT, pk_corp)){//判断是否清单取数
					Integer tickFlag = params[0] == null ? null :Integer.parseInt(params[0]);//专普票标识
					String ivflag = params[4];//认证标识
					dzfReturn = taxbalance.getIVnumber(pk_corp, period, tickFlag, params[3], 
							ivflag, reportvo.getPeriodtype());
				}
				
			}
		}
		else if (strFormula.startsWith("thsmtamt("))
		{//后续参数位置调整，可以第一分支合并
			if (params[0].indexOf("+") > 0)
			{
				saAccsubj = params[0].split("\\+");
			}
			else
			{
				saAccsubj = new String[1];
				saAccsubj[0] = params[0];
			}
	
			if (params[1].indexOf("+") > 0)
			{
				int iPlus = Integer.parseInt(params[1].split("\\+")[1].trim());
				iYear += iPlus;
			}
			else if (params[1].indexOf("-") > 0)
			{
				int iSub = Integer.parseInt(params[1].split("\\-")[1].trim());
				iYear -= iSub;
			}
			//处理期间
			int iPeriod = date.getMonth();
			if (params[2].indexOf("+") > 0)
			{
				int iPlus = Integer.parseInt(params[2].split("\\+")[1].trim());
				iPeriod += iPlus;
			}
			else if (params[2].indexOf("-") > 0)
			{
				int iSub = Integer.parseInt(params[2].split("\\-")[1].trim());
				iPeriod -= iSub;
			}
			
			//方向 params[3]
			sDirection = params[3].trim();
			
			//是否包含未记账 params[4]
			isHasJZ = new DZFBoolean(params[4].trim());
			
			period = "" + iYear + "-" + (iPeriod < 10 ? "0": "") + iPeriod;
		
			String invoiceflag = params[5];
			String taxrate = params[6];
			DZFDouble dzfTaxRate = (taxrate == null ? null : new DZFDouble(taxrate));
			
			for(String accsubj : saAccsubj){
				dzfReturn = dzfReturn.add(taxbalance.getThsmtAmt(accsubj.trim(), period, sDirection, pk_corp, isHasJZ, invoiceflag, dzfTaxRate,accountVO));
			}
			
		}
		else if  (strFormula.startsWith("sbbqs("))
		{
			String corpType = params[0];
			
			if (params[1].indexOf("+") > 0)
			{
				int iPlus = Integer.parseInt(params[1].split("\\+")[1].trim());
				iYear += iPlus;
			}
			else if (params[1].indexOf("-") > 0)
			{
				int iSub = Integer.parseInt(params[1].split("\\-")[1].trim());
				iYear -= iSub;
			}
			//处理期间
			int iPeriod = date.getMonth();
			if (params[2].indexOf("+") > 0)
			{
				int iPlus = Integer.parseInt(params[2].split("\\+")[1].trim());
				iPeriod += iPlus;
			}
			else if (params[2].indexOf("-") > 0)
			{
				int iSub = Integer.parseInt(params[2].split("\\-")[1].trim());
				iPeriod -= iSub;
			}
			
			period = "" + iYear + "-" + (iPeriod < 10 ? "0": "") + iPeriod;
			
			String reportName = params[3];
			String coordinate = params[4];
			
			dzfReturn = taxbalance.getQsbbqs(corpType, reportName, coordinate, pk_corp, period);
		}
		else if  (strFormula.startsWith("yhzc("))
		{
			dzfReturn = taxbalance.getYhzc(params[0], pk_corp);
		}
		else if  (strFormula.startsWith("zcfzb("))
		{
			String[] projname = null;
			if (params[0].indexOf("+") > 0)
			{
				projname = params[0].split("\\+");
			}
			else
			{
				projname = new String[1];
				projname[0] = params[0];
			}
			
			String descColname = params[1];
			
			if (params[2].indexOf("+") > 0)
			{
				int iPlus = Integer.parseInt(params[2].split("\\+")[1].trim());
				iYear += iPlus;
			}
			else if (params[2].indexOf("-") > 0)
			{
				int iSub = Integer.parseInt(params[2].split("\\-")[1].trim());
				iYear -= iSub;
			}
			//处理期间
			int iPeriod = 12;
			
			period = "" + iYear + "-" + (iPeriod < 10 ? "0": "") + iPeriod;

			dzfReturn = taxbalance.getZcfzb(projname, descColname, period, pk_corp);
		}
		else if  (strFormula.startsWith("swbb("))
		{
			String reportName = params[0];
			String coordinate = params[1];
			
			if (params[2].indexOf("+") > 0)
			{
				int iPlus = Integer.parseInt(params[2].split("\\+")[1].trim());
				iYear += iPlus;
			}
			else if (params[2].indexOf("-") > 0)
			{
				int iSub = Integer.parseInt(params[2].split("\\-")[1].trim());
				iYear -= iSub;
			}
			//处理期间
//			int iPeriod = 12;
			
//			period = "" + iYear + "-" + (iPeriod < 10 ? "0": "") + iPeriod;
			
			
			dzfReturn = taxbalance.getSwbb(reportName, coordinate, pk_corp, String.valueOf(iYear));
		}
		else if  (strFormula.startsWith("lrb(")) //利润表查询
		{
			
			//  处理报表项目名称
			if (params[0].indexOf("+") > 0)
			{
				saAccsubj = params[0].split("\\+");
			}
			else
			{
				saAccsubj = new String[1];
				saAccsubj[0] = params[0];
			}
			//  本年累计
			String nmnycol = params[1]; 
			//处理期间
			if (params[2].indexOf("+") > 0)
			{
				int iPlus = Integer.parseInt(params[1].split("\\+")[1].trim());
				iYear += iPlus;
			}
			else if (params[2].indexOf("-") > 0)
			{
				int iSub = Integer.parseInt(params[1].split("\\-")[1].trim());
				iYear -= iSub;
			}
			// 是否未记账
			isHasJZ = DZFBoolean.FALSE;
			for(String accsubj : saAccsubj){
				dzfReturn = dzfReturn.add(taxbalance.getLrb(accsubj, nmnycol,isHasJZ, pk_corp, iYear));
			}
		}
		else if(strFormula.startsWith("sqldata(")){//上期留抵金额
			
			String reportName = params[0];
			String coordinate = params[1];
			
			
			String pperiod = DateUtils.getPreviousPeriod(DateUtils.getPeriod(date));
			
			
			dzfReturn = taxbalance.getSqldata(reportName, coordinate, pk_corp, String.valueOf(pperiod));
			
		}
		else if(strFormula.startsWith("zcfzbqmye(")){//资产负债表
			String hc = params[0];//行次
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			ZcFzBVO[] zfbvos = null;
			if(zcfzMaps!=null && zcfzMaps.size() > 0){
				zfbvos = zcfzMaps.get(key);
			}else{
				zcfzMaps = taxbalance.getZcfzInfo(pk_corp, period1);
				zfbvos = zcfzMaps.get(key);
			}
			dzfReturn =  taxbalance.getHczcfzValue(zfbvos,hc,"qmye");
		}
		else if(strFormula.startsWith("zcfzbncye(")){//资产负债表
			String hc = params[0];//行次
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			ZcFzBVO[] zfbvos = null;
			if(zcfzMaps!=null && zcfzMaps.size() > 0){
				zfbvos = zcfzMaps.get(key);
			}else{
				zcfzMaps = taxbalance.getZcfzInfo(pk_corp, period1);
				zfbvos = zcfzMaps.get(key);
			}
			dzfReturn =  taxbalance.getHczcfzValue(zfbvos,hc,"ncye");
		}
		else if((strFormula.startsWith("lrbbyje(")
				|| strFormula.startsWith("lrbbjje("))
				&& reportvo.getPeriodtype() == PeriodType.monthreport){//利润表本月金额
			String hc = params[0];//行次
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			LrbVO[] zfbvos = null;
			if(lrbMaps!=null && lrbMaps.size() > 0){
				zfbvos = lrbMaps.get(key);
			}else{
				lrbMaps = taxbalance.getLrbInfo(pk_corp, period1);
				zfbvos = lrbMaps.get(key);
			}
			dzfReturn =  taxbalance.getHclrbValue(zfbvos,hc,"byje");
		}else if((strFormula.startsWith("lrbbyje(")
				|| strFormula.startsWith("lrbbjje("))
				&& reportvo.getPeriodtype() == PeriodType.jidureport){//利润表本季金额
			String hc = params[0];//行次
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			LrbquarterlyVO[] vos = null;
			if(lrbquarters != null && lrbquarters.size()>0 ){
				vos = lrbquarters.get(key);
			}else{
				lrbquarters = taxbalance.getLrbjidu(pk_corp, period1);
				vos = lrbquarters.get(key);
			}
			dzfReturn =  taxbalance.getHcLrbJiduValue(vos, hc,period1);
		}
		else if(strFormula.startsWith("lrbbnljje(")){//利润表本年累计
			String hc = params[0];//行次
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			LrbVO[] zfbvos = null;
			if(lrbMaps!=null && lrbMaps.size() > 0){
				zfbvos = lrbMaps.get(key);
			}else{
				lrbMaps = taxbalance.getLrbInfo(pk_corp, period1);
				zfbvos = lrbMaps.get(key);
			}
			dzfReturn =  taxbalance.getHclrbValue(zfbvos,hc,"bnljje");
		}
		else if(strFormula.startsWith("lrbbqje(")){//利润表本期金额
			String hc = params[0];//行次
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			String lastperiod = DateUtils.getPreviousYearPeriod(period1);
			String lastkey = pk_corp+","+lastperiod;
			LrbVO[] vos = null;
			LrbVO[] lastvos = null;
			if(lrbMaps!=null && lrbMaps.size() > 0){
				vos = lrbMaps.get(key);
				lastvos = lrbMaps.get(lastkey);
			}else{
				lrbMaps = taxbalance.getLrbQuarter(pk_corp, period1,lastperiod);
				vos = lrbMaps.get(key);
				lastvos = lrbMaps.get(lastkey);
			}
			dzfReturn =  taxbalance.getHcLrbQuarterValue(vos,lastvos,hc,"bqje");
		}
		else if(strFormula.startsWith("lrbsqje(")){//利润表上期金额
			String hc = params[0];//行次
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			String lastperiod = DateUtils.getPreviousYearPeriod(period1);
			String lastkey = pk_corp+","+lastperiod;
			LrbVO[] vos = null;
			LrbVO[] lastvos = null;
			if(lrbMaps!=null && lrbMaps.size() > 0){
				vos = lrbMaps.get(key);
				lastvos = lrbMaps.get(lastkey);
			}else{
				lrbMaps = taxbalance.getLrbQuarter(pk_corp, period1,lastperiod);
				vos = lrbMaps.get(key);
				lastvos = lrbMaps.get(lastkey);
			}
			dzfReturn =  taxbalance.getHcLrbQuarterValue(vos,lastvos,hc,"sqje");
		}else if(strFormula.startsWith("xjllljje(")){//现金流量累计金额
			String hc = params[0];//行次
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			XjllbVO[] xjllbvos = null;
			if(xjllMaps!=null && xjllMaps.size() > 0){
				xjllbvos = xjllMaps.get(key);
			}else{
				xjllMaps = taxbalance.getxjllInfo(pk_corp, period1);
				xjllbvos = xjllMaps.get(key);
			}
			dzfReturn =  taxbalance.getHcxjllValue(xjllbvos,hc,"ljje");
		}else if(strFormula.startsWith("xjllbyje(")){//现金流量本月金额
			String hc = params[0];//行次
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			if (reportvo.getPeriodtype() == PeriodType.monthreport) {
				XjllbVO[] xjllbvos = null;
				if(xjllMaps!=null && xjllMaps.size() > 0){
					xjllbvos = xjllMaps.get(key);
				}else{
					xjllMaps = taxbalance.getxjllInfo(pk_corp, period1);
					xjllbvos = xjllMaps.get(key);
				}
				dzfReturn =  taxbalance.getHcxjllValue(xjllbvos,hc,"byje");
			} else if (reportvo.getPeriodtype() == PeriodType.jidureport) {
				XjllquarterlyVo[] xjllbvos = null;
				if (xjllQuarterMaps != null && xjllQuarterMaps.size() > 0) {
					xjllbvos = xjllQuarterMaps.get(key);
				} else {
					xjllQuarterMaps = taxbalance.getxjllQuarterInfo(pk_corp, period1);
					xjllbvos = xjllQuarterMaps.get(key);
				}
				dzfReturn = taxbalance.getHcxjllQuarterValue(xjllbvos, hc, period1);
			}
		}else if(strFormula.startsWith("xjllbqje(")){//现金流量本期金额
			String hc = params[0];//行次
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			String lastperiod = DateUtils.getPreviousYearPeriod(period1);
			String lastkey = pk_corp+","+lastperiod;
			XjllbVO[] vos = null;
			XjllbVO[] lastvos = null;
			if(xjllMaps!=null && xjllMaps.size() > 0){
				vos = xjllMaps.get(key);
				lastvos = xjllMaps.get(lastkey);
			}else{
				xjllMaps = taxbalance.getxjllsqInfo(pk_corp, period1,lastperiod);
				vos = xjllMaps.get(key);
				lastvos = xjllMaps.get(lastkey);
			}
			dzfReturn =  taxbalance.getHcxjllsqValue(vos,lastvos,hc,"bqje");
		}else if(strFormula.startsWith("xjllsqje(")){//现金流量上期金额
			String hc = params[0];//行次
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			String lastperiod = DateUtils.getPreviousYearPeriod(period1);
			String lastkey = pk_corp+","+lastperiod;
			XjllbVO[] vos = null;
			XjllbVO[] lastvos = null;
			if(xjllMaps!=null && xjllMaps.size() > 0){
				vos = xjllMaps.get(key);
				lastvos = xjllMaps.get(lastkey);
			}else{
				xjllMaps = taxbalance.getxjllsqInfo(pk_corp, period1,lastperiod);
				vos = xjllMaps.get(key);
				lastvos = xjllMaps.get(lastkey);
			}
			dzfReturn =  taxbalance.getHcxjllsqValue(vos,lastvos,hc,"sqje");
		}else if(strFormula.startsWith("xjllnbsnje(")){//现金流量年报上年金额
			String hc = params[0];//行次
			String perioda = String.valueOf(date.getYear()-1)+"-12";//【取上一年】
			String key = pk_corp+","+perioda;
			XjllbVO[] xjllbvos = null;
			if(xjllMaps!=null && xjllMaps.size() > 0
					&& xjllMaps.get(key)!= null && xjllMaps.get(key).length > 0){
				xjllbvos = xjllMaps.get(key);
			}else{
				Map<String,XjllbVO[]> xjllmap = taxbalance.getxjllInfo(pk_corp, perioda);
				if(xjllMaps!=null){
					xjllMaps.putAll(xjllmap);
				}else{
					xjllMaps = xjllmap;
				}
				xjllbvos = xjllMaps.get(key);
			}
			dzfReturn =  taxbalance.getHcxjllValue(xjllbvos,hc,"ljje");
		}else if(strFormula.startsWith("lrbnbsnje(")){// 利润表年报上年金额
			String hc = params[0];//行次
			String perioda = String.valueOf(date.getYear()-1)+"-12";//【取上一年】
			String key = pk_corp+","+perioda;
			LrbVO[] zfbvos = null;
			if(lrbMaps!=null && lrbMaps.size() > 0 && 
					lrbMaps.get(key)!=null && lrbMaps.get(key).length > 0){
				zfbvos = lrbMaps.get(key);
			}else{
				Map<String, LrbVO[]> lrbmaps = taxbalance.getLrbInfo(pk_corp, perioda);
				if(lrbMaps!=null){
					lrbMaps.putAll(lrbmaps);
				}else{
					lrbMaps = lrbmaps;
				}
				zfbvos = lrbMaps.get(key);
			}
			dzfReturn =  taxbalance.getHclrbValue(zfbvos,hc,"bnljje");
		}else if(strFormula.startsWith("subjectamt(")//增值税一般纳税人新增取税目函数
				|| strFormula.startsWith("subjectamt2(")){//季报
			String taxcode = params[0];//税目编码
			String fpstyle = params[1];//发票类型---  [专用发票 --2 ////////  普票 --------1 ////// 没有开发票------3]
			String style = params[2];//金额、税额
			//方向 params[3]
			sDirection = params[3].trim();
			
			String[] taxcodes = null;
			if(taxcode.indexOf("+") > 0){
				taxcodes = taxcode.split("\\+");
			}else{
				taxcodes = new String[1];
				taxcodes[0] = taxcode;
			}
			
			String period2 = DateUtils.getPeriod(date);
			if(strFormula.startsWith("subjectamt(")){
				dzfReturn =  taxbalance.queryzzsFpValue(taxcodes,fpstyle,style,sDirection,period2,pk_corp);
			}else if(strFormula.startsWith("subjectamt2(")){
				dzfReturn =  taxbalance.queryzzsFpValue2(taxcodes,fpstyle,style,sDirection,period2,pk_corp);
			}
			
		} 
		/*************企业会计准则涉及的财报函数 begin***********/
		else if(strFormula.startsWith("zcbmqmye(")){//资产负债表  按编码
			String bm = params[0];//编码
			String period1 = DateUtils.getPeriod(date);
			//针对@period-2 运算做特殊处理***
			if(params[1].indexOf("+") > 0){
				int iPlus = Integer.parseInt(params[1].split("\\+")[1].trim());
				for(int i = 0; i < iPlus; i++){
					period1 = DateUtils.getNextPeriod(period1);
				}
			}
			if(params[1].indexOf("-") > 0){
				int iSub = Integer.parseInt(params[1].split("\\-")[1].trim());
				for(int i = 0; i < iSub; i++){
					period1 = DateUtils.getPreviousPeriod(period1);
				}
			}
			
			String key = pk_corp+","+period1;
			ZcFzBVO[] zfbvos = null;
			if(zcfzMaps!=null && zcfzMaps.size() > 0 && zcfzMaps.containsKey(key)){
				zfbvos = zcfzMaps.get(key);
			}else{
				zcfzMaps = taxbalance.getZcfzInfo(pk_corp, period1);
				zfbvos = zcfzMaps.get(key);
			}
			dzfReturn =  taxbalance.getBmzcfzValue(zfbvos,bm,"qmye");
		}else if(strFormula.startsWith("zcbmncye(")){//资产负债表  按编码
			String bm = params[0];//编码
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			ZcFzBVO[] zfbvos = null;
			if(zcfzMaps!=null && zcfzMaps.size() > 0){
				zfbvos = zcfzMaps.get(key);
			}else{
				zcfzMaps = taxbalance.getZcfzInfo(pk_corp, period1);
				zfbvos = zcfzMaps.get(key);
			}
			dzfReturn =  taxbalance.getBmzcfzValue(zfbvos,bm,"ncye");
		}else if(strFormula.startsWith("lrbmbqje(")){//利润表本期金额 按编码
			String bm = params[0];//编码
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			String lastperiod = DateUtils.getPreviousYearPeriod(period1);
			String lastkey = pk_corp+","+lastperiod;
			LrbVO[] vos = null;
			LrbVO[] lastvos = null;
			if(lrbMaps!=null && lrbMaps.size() > 0){
				vos = lrbMaps.get(key);
				lastvos = lrbMaps.get(lastkey);
			}else{
				lrbMaps = taxbalance.getLrbQuarter(pk_corp, period1,lastperiod);
				vos = lrbMaps.get(key);
				lastvos = lrbMaps.get(lastkey);
			}
			dzfReturn =  taxbalance.getBmLrbQuarterValue(vos,lastvos,bm,"bqje");
		} else if(strFormula.startsWith("lrbmsqje(")){//利润表上期金额 按编码
			String bm = params[0];//按编码
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			String lastperiod = DateUtils.getPreviousYearPeriod(period1);
			String lastkey = pk_corp+","+lastperiod;
			LrbVO[] vos = null;
			LrbVO[] lastvos = null;
			if(lrbMaps!=null && lrbMaps.size() > 0){
				vos = lrbMaps.get(key);
				lastvos = lrbMaps.get(lastkey);
			}else{
				lrbMaps = taxbalance.getLrbQuarter(pk_corp, period1,lastperiod);
				vos = lrbMaps.get(key);
				lastvos = lrbMaps.get(lastkey);
			}
			dzfReturn =  taxbalance.getBmLrbQuarterValue(vos,lastvos,bm,"sqje");
		}else if(strFormula.startsWith("xjbmbqje(")){//现金流量本期金额 按编码
			String bm = params[0];//编码
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			String lastperiod = DateUtils.getPreviousYearPeriod(period1);
			String lastkey = pk_corp+","+lastperiod;
			XjllbVO[] vos = null;
			XjllbVO[] lastvos = null;
			if(xjllMaps!=null && xjllMaps.size() > 0){
				vos = xjllMaps.get(key);
				lastvos = xjllMaps.get(lastkey);
			}else{
				xjllMaps = taxbalance.getxjllsqInfo(pk_corp, period1,lastperiod);
				vos = xjllMaps.get(key);
				lastvos = xjllMaps.get(lastkey);
			}
			dzfReturn =  taxbalance.getBmxjllsqValue(vos,lastvos,bm,"bqje");
		} else if(strFormula.startsWith("xjbmsqje(")){//现金流量上期金额 按编码
			String bm = params[0];//编码
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			String lastperiod = DateUtils.getPreviousYearPeriod(period1);
			String lastkey = pk_corp+","+lastperiod;
			XjllbVO[] vos = null;
			XjllbVO[] lastvos = null;
			if(xjllMaps!=null && xjllMaps.size() > 0){
				vos = xjllMaps.get(key);
				lastvos = xjllMaps.get(lastkey);
			}else{
				xjllMaps = taxbalance.getxjllsqInfo(pk_corp, period1,lastperiod);
				vos = xjllMaps.get(key);
				lastvos = xjllMaps.get(lastkey);
			}
			dzfReturn =  taxbalance.getBmxjllsqValue(vos,lastvos,bm,"sqje");
		}
		/*************企业会计准则涉及的财报函数 end***********/
		/*************小企业会计准则涉及的财报函数 begin********/
		else if(strFormula.startsWith("lrbmljje(")){//利润表本年累计 按编码
			String bm = params[0];//编码
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			LrbVO[] zfbvos = null;
			if(lrbMaps!=null && lrbMaps.size() > 0){
				zfbvos = lrbMaps.get(key);
			}else{
				lrbMaps = taxbalance.getLrbInfo(pk_corp, period1);
				zfbvos = lrbMaps.get(key);
			}
			dzfReturn =  taxbalance.getBmlrbValue(zfbvos,bm,"ljje");
		}
		else if(strFormula.startsWith("lrbmbyje(")){//利润表金额  按编码
			String bm = params[0];//编码
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			if(reportvo.getPeriodtype() == PeriodType.monthreport){//本月
				LrbVO[] zfbvos = null;
				if(lrbMaps!=null && lrbMaps.size() > 0){
					zfbvos = lrbMaps.get(key);
				}else{
					lrbMaps = taxbalance.getLrbInfo(pk_corp, period1);
					zfbvos = lrbMaps.get(key);
				}
				dzfReturn =  taxbalance.getBmlrbValue(zfbvos,bm,"byje");
			}else if(reportvo.getPeriodtype() == PeriodType.jidureport){//利润表本季金额  按编码
				LrbquarterlyVO[] vos = null;
				if(lrbquarters != null && lrbquarters.size()>0 ){
					vos = lrbquarters.get(key);
				}else{
					lrbquarters = taxbalance.getLrbjidu(pk_corp, period1);
					vos = lrbquarters.get(key);
				}
				dzfReturn =  taxbalance.getBmLrbJiduValue(vos, bm,period1);
			}
			
		}
		else if(strFormula.startsWith("xjbmljje(")){//现金流量累计金额 按编码
			String bm = params[0];//行次
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			XjllbVO[] xjllbvos = null;
			if(xjllMaps!=null && xjllMaps.size() > 0){
				xjllbvos = xjllMaps.get(key);
			}else{
				xjllMaps = taxbalance.getxjllInfo(pk_corp, period1);
				xjllbvos = xjllMaps.get(key);
			}
			dzfReturn =  taxbalance.getBmxjllValue(xjllbvos,bm,"ljje");
		}
		else if(strFormula.startsWith("xjbmbyje(")){//现金流量本月金额  按编码
			String bm = params[0];//编码
			String period1 = DateUtils.getPeriod(date);
			String key = pk_corp+","+period1;
			if (reportvo.getPeriodtype() == PeriodType.monthreport) {
				XjllbVO[] xjllbvos = null;
				if(xjllMaps!=null && xjllMaps.size() > 0){
					xjllbvos = xjllMaps.get(key);
				}else{
					xjllMaps = taxbalance.getxjllInfo(pk_corp, period1);
					xjllbvos = xjllMaps.get(key);
				}
				dzfReturn =  taxbalance.getBmxjllValue(xjllbvos,bm,"byje");
			} else if (reportvo.getPeriodtype() == PeriodType.jidureport) {
				XjllquarterlyVo[] xjllbvos = null;
				if (xjllQuarterMaps != null && xjllQuarterMaps.size() > 0) {
					xjllbvos = xjllQuarterMaps.get(key);
				} else {
					xjllQuarterMaps = taxbalance.getxjllQuarterInfo(pk_corp, period1);
					xjllbvos = xjllQuarterMaps.get(key);
				}
				dzfReturn = taxbalance.getBmxjllQuarterValue(xjllbvos, bm, period1);
			}
		}
		else if(strFormula.startsWith("lrbmsnje(")){// 利润表年报上年金额 按编码
			String bm = params[0];//编码
			String perioda = String.valueOf(date.getYear()-1)+"-12";//【取上一年】
			String key = pk_corp+","+perioda;
			LrbVO[] zfbvos = null;
			if(lrbMaps!=null && lrbMaps.size() > 0 && 
					lrbMaps.get(key)!=null && lrbMaps.get(key).length > 0){
				zfbvos = lrbMaps.get(key);
			}else{
				Map<String, LrbVO[]> lrbmaps = taxbalance.getLrbInfo(pk_corp, perioda);
				if(lrbMaps!=null){
					lrbMaps.putAll(lrbmaps);
				}else{
					lrbMaps = lrbmaps;
				}
				zfbvos = lrbMaps.get(key);
			}
			dzfReturn =  taxbalance.getBmlrbValue(zfbvos,bm,"snje");
		}
		else if(strFormula.startsWith("xjbmsnje(")){//现金流量年报上年金额  按编码
			String bm = params[0];//编码
			String perioda = String.valueOf(date.getYear()-1)+"-12";//【取上一年】
			String key = pk_corp+","+perioda;
			XjllbVO[] xjllbvos = null;
			if(xjllMaps!=null && xjllMaps.size() > 0
					&& xjllMaps.get(key)!= null && xjllMaps.get(key).length > 0){
				xjllbvos = xjllMaps.get(key);
			}else{
				Map<String,XjllbVO[]> xjllmap = taxbalance.getxjllInfo(pk_corp, perioda);
				if(xjllMaps!=null){
					xjllMaps.putAll(xjllmap);
				}else{
					xjllMaps = xjllmap;
				}
				xjllbvos = xjllMaps.get(key);
			}
			dzfReturn =  taxbalance.getBmxjllValue(xjllbvos,bm,"snje");
		}
		/*************小企业会计准则涉及的财报函数 end***********/
		else if (strFormula.startsWith("genera(")) {
			// 所属期间类型
			dzfReturn = new DZFDouble(reportvo.getPeriodtype());
		}
		dzfReturn = dzfReturn == null ? DZFDouble.ZERO_DBL : dzfReturn;
		return objReturn == null ? dzfReturn.setScale(2, DZFDouble.ROUND_HALF_UP) : objReturn;
	}
	
	private boolean judgeSource(int from, String pk_corp){
    	if(datasource == null){
    		datasource = taxbalance.getDatasources(pk_corp);
    	}
    	
    	return datasource == from;
    }
	
	public Map fillReportWithZero(Map mapJson, List<String> listRptName,
			TaxReportVO reportvo, HashMap<String, Object> hmQCData, CorpVO corpvo,CorpTaxVo corptaxvo) {
		boolean readonly = false;
		LinkedHashMap hmsheets = (LinkedHashMap) mapJson.get("sheets");
		Iterator iter = hmsheets.entrySet().iterator();

		// 遍历报表
		List<String> listDeleteSheet = new ArrayList<String>();

		while (iter.hasNext()) {
			Map<String, String> hmSheetValue = new HashMap<String, String>();
			Map.Entry entry = (Map.Entry) iter.next();
			// 报表名称
			String sheetname = ((String) entry.getKey()).trim();

			if (listRptName.contains(sheetname) == false) {
				listDeleteSheet.add(sheetname);
				continue;
			}

			// 赋表头默认值
			setDefaultHeadValue(mapJson, sheetname, reportvo, corpvo,corptaxvo);

			fillReportByQcData(mapJson, sheetname, corpvo, corptaxvo, reportvo, hmQCData);

			LinkedHashMap hmsheet = (LinkedHashMap) entry.getValue();
			LinkedHashMap hmdata = (LinkedHashMap) hmsheet.get("data");
			LinkedHashMap hmdataTable = (LinkedHashMap) hmdata.get("dataTable");

			// 找蓝色格子#66FFFF
			String sColorBlue = "#66FFFF";
			ArrayList<LinkedHashMap> listnamedStyles = (ArrayList<LinkedHashMap>) hmsheet
					.get("namedStyles");
			Map<String, LinkedHashMap> hmBuleName = new HashMap<String, LinkedHashMap>();
			Map<String, LinkedHashMap> hmStyles = new HashMap<String, LinkedHashMap>();
			for (LinkedHashMap hmstyle : listnamedStyles) {
				// LinkedHashMap hmstyle =
				// (LinkedHashMap)hmnamedStyles.get(keystyle);
				if (sColorBlue.equals(hmstyle.get("backColor"))) {
					hmBuleName.put(hmstyle.get("name").toString(), hmstyle);
				}
				hmStyles.put(hmstyle.get("name").toString(), hmstyle);
			}
			hmsheet.put("index", listRptName.indexOf(sheetname));
			// 保护工作表
			hmsheet.put("isProtected", true);
			// 缩放百分比
			hmsheet.put("zoomFactor", 1.3);

			// 行号
			String row = null;
			// 列集合
			LinkedHashMap cols = null;
			Iterator itercolumn = null;

			String column = null;
			LinkedHashMap cell = null;

			// 判断底色是否是浅蓝，浅蓝可编辑
			Object objStyle = null;
			Object objBackColor = null;
			LinkedHashMap<String, Object> hmStyle = null;
			LinkedHashMap thisstyle = null;
			Object objFormula = null;

			String strValue = null;
			// 公式缓存
			Map mapValue = new HashMap<String, Object>();

			Iterator iterrow = hmdataTable.keySet().iterator();
			// 遍历行
			while (iterrow.hasNext()) {
				// 行号
				row = (String) iterrow.next();
				// 列集合
				cols = (LinkedHashMap) hmdataTable.get(row);
				itercolumn = cols.keySet().iterator();
				// 遍历列
				while (itercolumn.hasNext()) {
					column = (String) itercolumn.next();
					cell = (LinkedHashMap) cols.get(column);
					
					objStyle = cell.get("style");
					if (objStyle != null)
					{
						if (objStyle instanceof LinkedHashMap) 
						{
							hmStyle = (LinkedHashMap<String, Object>)objStyle;
							objBackColor = hmStyle.get("backColor");
							if (objBackColor != null && objBackColor.toString().equals(sColorBlue))
							{
								hmStyle.put("locked", readonly);
							}
						}
						else if (objStyle instanceof String)
						{
							if (hmBuleName.containsKey(objStyle.toString()))
							{
								thisstyle = hmBuleName.get(objStyle.toString());
								thisstyle.put("locked", readonly);
							}
						}
					}
					
					if (cell.containsKey("formula")) {
						objFormula = cell.get("formula");
						if (objFormula == null
								|| objFormula.toString().trim().length() == 0)
							continue;
						String formularStr = objFormula.toString().trim()
								.replaceAll("“|”", "\"")
								.toLowerCase();
						// 期初函数
						String regex = "qc\\(([^\\(\\)]*?)\\)";
						Pattern p = Pattern.compile(regex);
						Matcher m = p.matcher(formularStr);
						DZFDouble val = DZFDouble.ZERO_DBL;
						boolean firstQc = true;
						while (m.find()) {
							String qckey = m.group(1);
							String qcvalue = "0";
							if(qckey.startsWith("sb10412005vo.data1.fzjgqk")){
								qcvalue = getFZjgqkbiaoinfo(qckey,hmQCData);
							}
							
							if (!hmQCData.containsKey(qckey))
							{
								continue;
							}
							qcvalue = hmQCData.get(qckey).toString();
							DZFDouble qcDouble = DZFDouble.ZERO_DBL;
							try {
								qcDouble = new DZFDouble(qcvalue);
							} catch (Exception e) {
							}
							if (firstQc) {
								val = val.add(qcDouble);
								firstQc = false;
								continue;
							}
							if (m.start() > 0) {
								char pre = formularStr.charAt(m.start() - 1);
								if (pre == '+') {
									val.add(qcDouble);
								} else if (pre == '-') {
									val.sub(qcDouble);
								} else {
									continue;
								}
							}
						}

						// 重新赋值公式
						cell.remove("formula");
						cell.put("value", val.doubleValue() == 0 ? "" : val.toString());
					}
				}
			}

		}
		if (corptaxvo.getTax_area() != null && corptaxvo.getTax_area() == 11
				&& TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())) {
			setCellValue(mapJson, "增值税纳税申报表",
					32, 2, "201|居民身份证");
			// 办税人身份证
			if (corptaxvo.getVtaxpersonid() != null) {
				setCellValue(mapJson, "增值税纳税申报表",
						32, 6, corptaxvo.getVtaxpersonid());
			}
		}
		if (listDeleteSheet.size() > 0) {
			for (String key : listDeleteSheet) {
				hmsheets.remove(key);
			}
		}
		mapJson.put("sheetCount", listRptName.size());
		return mapJson;
	}
}