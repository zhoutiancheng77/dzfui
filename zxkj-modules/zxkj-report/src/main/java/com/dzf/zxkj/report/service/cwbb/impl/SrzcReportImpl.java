package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.SrzcBVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwbb.ISrzcReport;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.utils.VoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service("gl_rep_srzcserv")
public class SrzcReportImpl implements ISrzcReport {

	
	private IFsYeReport gl_rep_fsyebserv;

	@Autowired
	private IZxkjPlatformService zxkjPlatformService;

	@Override
	public SrzcBVO[] queryVos(QueryParamVO paramvo) throws DZFWarpException {
		
		
		//获取发生额的数据
		FseJyeVO[] fvos = gl_rep_fsyebserv.getFsJyeVOs(paramvo, 1);
		Map<String, FseJyeVO> map = new HashMap<String, FseJyeVO>();
		Map<String, YntCpaccountVO> cpavomap = 	zxkjPlatformService.queryMapByPk(paramvo.getPk_corp());
		if (fvos != null && fvos.length > 0) {
			int len = fvos == null ? 0 : fvos.length;
			for (int i = 0; i < len; i++) {
				fvos[i].setDirection(cpavomap.get(fvos[i].getPk_km()).getDirection());
				map.put(fvos[i].getKmbm(), fvos[i]);
			}
		}
		
		//getSrzcVos(map);
		return getSrzcVos(map);
	}

	private DZFDouble getXMValueByTotal(Map<String, FseJyeVO> map, int i , String ...kms) {
		DZFDouble total = DZFDouble.ZERO_DBL;
		if(kms == null || kms.length == 0){
			return total;
		}

		for(String km : kms){
			total = SafeCompute.add(total, getXMValue(map, km, i));
		}

		return total;
	}

	/**
	 * 收入支出获取数据
	 * @param map
	 */
	private SrzcBVO[] getSrzcVos(Map<String, FseJyeVO> map) {
		
		SrzcBVO[] srzcbvos = new SrzcBVO[25];

		//财政补助支出 500101、5001001 后续科目多了 再通过科目编码规则 KmbmUpgrade.getNewCode
		DZFDouble bqjbzc = getXMValueByTotal(map, 0, "500101", "5001001");
		DZFDouble bnjbzc = getXMValueByTotal(map, 1, "500101", "5001001");
		
		srzcbvos[0]= new SrzcBVO();
		srzcbvos[0].setXm("一、本期财政补助结转结余");
		DZFDouble bqsrzc_1= getXMValue(map,"4001",0);
		DZFDouble bqsrzc_2 = getXMValue(map, "5001", 0);
		DZFDouble bnsrzc_1= getXMValue(map,"4001",1);
		DZFDouble bnsrzc_2 = getXMValue(map, "5001", 1);
		srzcbvos[0].setMonnum(bqsrzc_1.sub(bqsrzc_2));
		srzcbvos[0].setYearnum(bnsrzc_1.sub(bnsrzc_2));

		srzcbvos[1]= new SrzcBVO();
		srzcbvos[1].setXm("财政补助收入");
		srzcbvos[1].setMonnum(bqsrzc_1);
		srzcbvos[1].setYearnum(bnsrzc_1);

		srzcbvos[2]= new SrzcBVO();
		srzcbvos[2].setXm("减：事业支出（财政补助支出）");
		srzcbvos[2].setMonnum(bqjbzc);
		srzcbvos[2].setYearnum(bnjbzc);

		srzcbvos[3]= new SrzcBVO();
		srzcbvos[3].setXm("二、本期事业结转结余");

		
		srzcbvos[4]= new SrzcBVO();
		srzcbvos[4].setXm("    （一）事业类收入");
		
		srzcbvos[5]= new SrzcBVO();
		srzcbvos[5].setXm("    1.事业收入");
		srzcbvos[5].setMonnum(getXMValue(map,"4101",0));
		srzcbvos[5].setYearnum(getXMValue(map,"4101",1));
		
		srzcbvos[6]= new SrzcBVO();
		srzcbvos[6].setXm("    2.上级补助收入");
		srzcbvos[6].setMonnum(getXMValue(map,"4201",0));
		srzcbvos[6].setYearnum(getXMValue(map,"4201",1));
		
		srzcbvos[7]= new SrzcBVO();
		srzcbvos[7].setXm("    3.附属单位上缴收入");
		srzcbvos[7].setMonnum(getXMValue(map,"4301",0));
		srzcbvos[7].setYearnum(getXMValue(map,"4301",1));
		
		srzcbvos[8]= new SrzcBVO();
		srzcbvos[8].setXm("    4.其他收入");
		srzcbvos[8].setMonnum(getXMValue(map,"4501",0));
		srzcbvos[8].setYearnum(getXMValue(map,"4501",1));
		
		srzcbvos[9]= new SrzcBVO();
		srzcbvos[9].setXm("      其中：捐赠收入");
		srzcbvos[9].setMonnum(DZFDouble.ZERO_DBL);
		srzcbvos[9].setYearnum(DZFDouble.ZERO_DBL);
		
		DZFDouble monnum4 =  srzcbvos[5].getMonnum().add(srzcbvos[6].getMonnum()).add(srzcbvos[7].getMonnum()).add(srzcbvos[8].getMonnum()).add(srzcbvos[9].getMonnum());
		DZFDouble yearnum4 =  srzcbvos[5].getYearnum().add(srzcbvos[6].getYearnum()).add(srzcbvos[7].getYearnum()).add(srzcbvos[8].getYearnum()).add(srzcbvos[9].getYearnum());
		srzcbvos[4].setMonnum(monnum4);
		srzcbvos[4].setYearnum(yearnum4);
		
		srzcbvos[10]= new SrzcBVO();
		srzcbvos[10].setXm("减：（二）事业类支出");
		
		srzcbvos[11]= new SrzcBVO();
		srzcbvos[11].setXm("    1.事业支出（非财政补助支出）");
		srzcbvos[11].setMonnum(SafeCompute.sub(bqsrzc_2, bqjbzc));//getXMValue(map,"5001",0)
		srzcbvos[11].setYearnum(SafeCompute.sub(bnsrzc_2, bnjbzc));//getXMValue(map,"5001",1)
		
		srzcbvos[12]= new SrzcBVO();
		srzcbvos[12].setXm("    2.上缴上级支出");
		srzcbvos[12].setMonnum(getXMValue(map,"5101",0));
		srzcbvos[12].setYearnum(getXMValue(map,"5101",1));
		
		srzcbvos[13]= new SrzcBVO();
		srzcbvos[13].setXm("    3.对附属单位补助支出");
		srzcbvos[13].setMonnum(getXMValue(map,"5201",0));
		srzcbvos[13].setYearnum(getXMValue(map,"5201",1));
		
		
		srzcbvos[14]= new SrzcBVO();
		srzcbvos[14].setXm("    4.其他支出");
		srzcbvos[14].setMonnum(getXMValue(map,"5401",0));
		srzcbvos[14].setYearnum(getXMValue(map,"5401",1));
		
		DZFDouble monnum10 =  srzcbvos[11].getMonnum().add(srzcbvos[12].getMonnum()).add(srzcbvos[13].getMonnum()).add(srzcbvos[14].getMonnum());
		DZFDouble yearnum10 = srzcbvos[11].getYearnum().add(srzcbvos[12].getYearnum()).add(srzcbvos[13].getYearnum()).add(srzcbvos[14].getYearnum());
		srzcbvos[10].setMonnum(monnum10);
		srzcbvos[10].setYearnum(yearnum10);
		
		
		srzcbvos[3].setMonnum(srzcbvos[4].getMonnum().sub(srzcbvos[10].getMonnum()));
		srzcbvos[3].setYearnum(srzcbvos[4].getYearnum().sub(srzcbvos[10].getYearnum()));
		
		
		srzcbvos[15]= new SrzcBVO();
		srzcbvos[15].setXm("三、本期经营结余");
		DZFDouble bqmonnum16= getXMValue(map,"4401",0);
		DZFDouble bnmonnum16= getXMValue(map,"4401",1);
		DZFDouble bqmonnum17 = getXMValue(map,"5301",0);
		DZFDouble bnmonnum17 = getXMValue(map,"5301",1);
		srzcbvos[15].setMonnum(bqmonnum16.sub(bqmonnum17));
		srzcbvos[15].setYearnum(bnmonnum16.sub(bnmonnum17));
		
		
		srzcbvos[16]= new SrzcBVO();
		srzcbvos[16].setXm("经营收入");
		srzcbvos[16].setMonnum(bqmonnum16);
		srzcbvos[16].setYearnum(bnmonnum16);
		
		
		srzcbvos[17]= new SrzcBVO();
		srzcbvos[17].setXm("减：经营支出");
		srzcbvos[17].setMonnum(bqmonnum17);
		srzcbvos[17].setYearnum(bnmonnum17);
		
		srzcbvos[18]= new SrzcBVO();
		srzcbvos[18].setXm("四、弥补以前年度亏损后的经营结余");
		srzcbvos[18].setMonnum(DZFDouble.ZERO_DBL);
		srzcbvos[18].setYearnum(DZFDouble.ZERO_DBL);
		
		srzcbvos[19]= new SrzcBVO();
		srzcbvos[19].setXm("五、本年非财政补助结转结余");
		DZFDouble bqmonnum19 = srzcbvos[18].getMonnum().doubleValue()>0?srzcbvos[3].getMonnum().add(srzcbvos[18].getMonnum()):srzcbvos[3].getMonnum();
		DZFDouble bnmonnum19 = srzcbvos[18].getYearnum().doubleValue()>0?srzcbvos[3].getYearnum().add(srzcbvos[18].getYearnum()):srzcbvos[3].getYearnum();
		srzcbvos[19].setMonnum(bqmonnum19);
		srzcbvos[19].setYearnum(bnmonnum19);
		
		
		srzcbvos[20]= new SrzcBVO();
		srzcbvos[20].setXm("减：非财政补助结转");
		srzcbvos[20].setMonnum(DZFDouble.ZERO_DBL);
		srzcbvos[20].setYearnum(DZFDouble.ZERO_DBL);
		
		srzcbvos[21]= new SrzcBVO();
		srzcbvos[21].setXm("六、本年非财政补助结余");
		srzcbvos[21].setMonnum(srzcbvos[19].getMonnum().sub(srzcbvos[20].getMonnum()));
		srzcbvos[21].setYearnum(srzcbvos[19].getYearnum().sub(srzcbvos[20].getYearnum()));
		
		srzcbvos[22]= new SrzcBVO();
		srzcbvos[22].setXm("减：应缴企业所得税");
		srzcbvos[22].setMonnum(DZFDouble.ZERO_DBL);
		srzcbvos[22].setYearnum(DZFDouble.ZERO_DBL);
		
		srzcbvos[23]= new SrzcBVO();
		srzcbvos[23].setXm("减：提取专用基金");
		srzcbvos[23].setMonnum(DZFDouble.ZERO_DBL);
		srzcbvos[23].setYearnum(DZFDouble.ZERO_DBL);
		
		srzcbvos[24]= new SrzcBVO();
		srzcbvos[24].setXm("七、转入事业基金");
		srzcbvos[24].setMonnum(srzcbvos[21].getMonnum().sub(srzcbvos[22].getMonnum()).sub(srzcbvos[23].getMonnum()));
		srzcbvos[24].setYearnum(srzcbvos[21].getYearnum().sub(srzcbvos[22].getYearnum()).sub(srzcbvos[23].getYearnum()));
		return srzcbvos;
		
	}

	/**
	 * 根据科目获取对应的值
	 * @param map
	 * @param i
	 * @return
	 */
	private DZFDouble getXMValue(Map<String, FseJyeVO> map, String kms, int i) {
		
		if(StringUtil.isEmpty(kms)){
			return DZFDouble.ZERO_DBL;
		}
		
		if(map.get(kms)==null){
			return DZFDouble.ZERO_DBL;
		}
		FseJyeVO fsvo = map.get(kms);
		Integer direction = fsvo.getDirection() == null?0:fsvo.getDirection();
		DZFDouble sumvalue = DZFDouble.ZERO_DBL;
		if(i == 0){//取本月期末数据
			DZFDouble jffs = VoUtils.getDZFDouble( fsvo.getFsjf());
			DZFDouble dffs = VoUtils.getDZFDouble( fsvo.getFsdf());
			if(direction.intValue() ==0 ){
				sumvalue = sumvalue.add(jffs);
			}else{
				sumvalue = sumvalue.add(dffs);
			}
		}else if( i == 1){//取本年发生值
			DZFDouble jffs = VoUtils.getDZFDouble( fsvo.getJftotal());
			DZFDouble dffs = VoUtils.getDZFDouble( fsvo.getDftotal());
			if(direction.intValue() ==0 ){
				sumvalue = sumvalue.add(jffs);
			}else{
				sumvalue = sumvalue.add(dffs);
			}
		}
		return sumvalue;
	}

	public IFsYeReport getGl_rep_fsyebserv() {
		return gl_rep_fsyebserv;
	}


	@Autowired
	public void setGl_rep_fsyebserv(IFsYeReport gl_rep_fsyebserv) {
		this.gl_rep_fsyebserv = gl_rep_fsyebserv;
	}
}
