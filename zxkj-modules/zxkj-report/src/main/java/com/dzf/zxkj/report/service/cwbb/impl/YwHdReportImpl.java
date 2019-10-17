package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.YwHdVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwbb.IYwHdReport;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.utils.VoUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务活动处理器类
 * 
 * @author zhangj
 * 
 */
@Service("gl_rep_ywhdserv")
public class YwHdReportImpl implements IYwHdReport {

	@Reference(version = "1.0.0")
	private IZxkjPlatformService zxkjPlatformService;

	private IFsYeReport gl_rep_fsyebserv;

	private SingleObjectBO singleObjectBO = null;


	@Override
	public YwHdVO[] queryYwHdValues(QueryParamVO paramvo) throws DZFWarpException {
		//获取发生额的数据
		FseJyeVO[] fvos= gl_rep_fsyebserv.getFsJyeVOs(paramvo,1);
		Map<String,FseJyeVO> map=new HashMap<String, FseJyeVO>();
		Map<String, YntCpaccountVO> cpavomap = 	zxkjPlatformService.queryMapByPk(paramvo.getPk_corp());
		if(fvos != null && fvos.length > 0){
			int len=fvos==null?0:fvos.length;
			for (int i = 0; i < len; i++) {
				fvos[i].setDirection(cpavomap.get(fvos[i].getPk_km()).getDirection());
				map.put(fvos[i].getKmbm(), fvos[i]);
			}
		}
		
		return getYwHdVO(map,paramvo.getPk_corp());
	}

	
	private String formatkm(String pk_corp,String km){
		String newrule = zxkjPlatformService.queryAccountRule(pk_corp);
		String kmnew = zxkjPlatformService.getNewRuleCode(km, DZFConstant.ACCOUNTCODERULE, newrule);
		return kmnew;
	}
	
	
	private YwHdVO[] getYwHdVO(Map<String, FseJyeVO> map,String pk_corp) {
		YwHdVO[] ywhdvos = new YwHdVO[21];
		ywhdvos[0] =  new YwHdVO();
		ywhdvos[0].setHs(null);
		ywhdvos[0].setXm("一、收  入");
		ywhdvos[0].setMonfxdx(null);
		ywhdvos[0].setMonxdx(null);
		ywhdvos[0].setMonhj(null);
		ywhdvos[0].setYearfxdx(null);
		ywhdvos[0].setYearxdx(null);
		ywhdvos[0].setYearhj(null);
		
		ywhdvos[1] = new YwHdVO();
		ywhdvos[1].setXm("其中：捐赠收入");
		ywhdvos[1].setHs("1");
		ywhdvos[1].setMonfxdx(getKMValue(map,0,formatkm(pk_corp, "410101")));
		ywhdvos[1].setMonxdx(getKMValue(map,0,formatkm(pk_corp, "410102")));
		ywhdvos[1].setMonhj(getKMValue(map,0,"4101"));
		ywhdvos[1].setYearfxdx(getKMValue(map,1,formatkm(pk_corp, "410101")));
		ywhdvos[1].setYearxdx(getKMValue(map,1,formatkm(pk_corp,"410102")));
		ywhdvos[1].setYearhj(getKMValue(map,1,"4101"));
		
		
		ywhdvos[2] = new YwHdVO();
		ywhdvos[2].setXm("     会费收入");
		ywhdvos[2].setHs("2");
		ywhdvos[2].setMonfxdx(getKMValue(map,0,formatkm(pk_corp, "420101")));
		ywhdvos[2].setMonxdx(getKMValue(map,0,formatkm(pk_corp, "420102")));
		ywhdvos[2].setMonhj(getKMValue(map,0,"4201"));
		ywhdvos[2].setYearfxdx(getKMValue(map,1,formatkm(pk_corp, "420101")));
		ywhdvos[2].setYearxdx(getKMValue(map,1,formatkm(pk_corp, "420102")));
		ywhdvos[2].setYearhj(getKMValue(map,1,"4201"));
		
		
		ywhdvos[3]  = new YwHdVO();
		ywhdvos[3].setXm("     提供服务收入");
		ywhdvos[3].setHs("3");
		ywhdvos[3].setMonfxdx(getKMValue(map,0,formatkm(pk_corp, "430101")));
		ywhdvos[3].setMonxdx(getKMValue(map,0,formatkm(pk_corp, "430102")));
		ywhdvos[3].setMonhj(getKMValue(map,0,"4301"));
		ywhdvos[3].setYearfxdx(getKMValue(map,1,formatkm(pk_corp, "430101")));
		ywhdvos[3].setYearxdx(getKMValue(map,1,formatkm(pk_corp, "430102")));
		ywhdvos[3].setYearhj(getKMValue(map,1,"4301"));
		
		ywhdvos[4] = new YwHdVO();
		ywhdvos[4].setXm("     商品销售收入");
		ywhdvos[4].setHs("4");
		ywhdvos[4].setMonfxdx(getKMValue(map,0,formatkm(pk_corp, "450101")));
		ywhdvos[4].setMonxdx(getKMValue(map,0,formatkm(pk_corp, "450102")));
		ywhdvos[4].setMonhj(getKMValue(map,0,"4501"));
		ywhdvos[4].setYearfxdx(getKMValue(map,1,formatkm(pk_corp, "450101")));
		ywhdvos[4].setYearxdx(getKMValue(map,1,formatkm(pk_corp, "450102")));
		ywhdvos[4].setYearhj(getKMValue(map,1,"4501"));
		
		ywhdvos[5] = new YwHdVO();
		ywhdvos[5].setXm("     政府补助收入");
		ywhdvos[5].setHs("5");
		ywhdvos[5].setMonfxdx(getKMValue(map,0,formatkm(pk_corp, "440101")));
		ywhdvos[5].setMonxdx(getKMValue(map,0,formatkm(pk_corp, "440102")));
		ywhdvos[5].setMonhj(getKMValue(map,0,"4401"));
		ywhdvos[5].setYearfxdx(getKMValue(map,1,formatkm(pk_corp, "440101")));
		ywhdvos[5].setYearxdx(getKMValue(map,1,formatkm(pk_corp, "440102")));
		ywhdvos[5].setYearhj(getKMValue(map,1,"4401"));
		
		ywhdvos[6] = new YwHdVO();
		ywhdvos[6].setXm("     投资收益");
		ywhdvos[6].setHs("6");
		ywhdvos[6].setMonfxdx(getKMValue(map,0,formatkm(pk_corp, "460101")));
		ywhdvos[6].setMonxdx(getKMValue(map,0,formatkm(pk_corp, "460102")));
		ywhdvos[6].setMonhj(getKMValue(map,0,"4601"));
		ywhdvos[6].setYearfxdx(getKMValue(map,1,formatkm(pk_corp, "460101")));
		ywhdvos[6].setYearxdx(getKMValue(map,1,formatkm(pk_corp, "460102")));
		ywhdvos[6].setYearhj(getKMValue(map,1,"4601"));
		
		ywhdvos[7] = new YwHdVO();
		ywhdvos[7].setXm("     其他收入");
		ywhdvos[7].setHs("9");
		ywhdvos[7].setMonfxdx(getKMValue(map,0,formatkm(pk_corp, "490101")));
		ywhdvos[7].setMonxdx(getKMValue(map,0,formatkm(pk_corp, "490102")));
		ywhdvos[7].setMonhj(getKMValue(map,0,"4901"));
		ywhdvos[7].setYearfxdx(getKMValue(map,1,formatkm(pk_corp, "490101")));
		ywhdvos[7].setYearxdx(getKMValue(map,1,formatkm(pk_corp, "490102")));
		ywhdvos[7].setYearhj(getKMValue(map,1,"4901"));
		
		
		ywhdvos[8] = new YwHdVO();
		ywhdvos[8].setXm("收入合计");
		ywhdvos[8].setHs("11");
		ywhdvos[8].setMonfxdx(VoUtils.getDZFDouble(ywhdvos[1].getMonfxdx()).add(VoUtils.getDZFDouble(ywhdvos[2].getMonfxdx())).add(VoUtils.getDZFDouble(ywhdvos[3].getMonfxdx()))
				.add(VoUtils.getDZFDouble(ywhdvos[4].getMonfxdx())).add(VoUtils.getDZFDouble(ywhdvos[5].getMonfxdx())).add(VoUtils.getDZFDouble(ywhdvos[6].getMonfxdx()))
				.add(VoUtils.getDZFDouble(ywhdvos[7].getMonfxdx()))
				);
		ywhdvos[8].setMonxdx(VoUtils.getDZFDouble(ywhdvos[1].getMonxdx()).add(VoUtils.getDZFDouble(ywhdvos[2].getMonxdx())).add(VoUtils.getDZFDouble(ywhdvos[3].getMonxdx()))
				.add(VoUtils.getDZFDouble(ywhdvos[4].getMonxdx())).add(VoUtils.getDZFDouble(ywhdvos[5].getMonxdx())).add(VoUtils.getDZFDouble(ywhdvos[6].getMonxdx()))
				.add(VoUtils.getDZFDouble(ywhdvos[7].getMonxdx()))
				);
		ywhdvos[8].setMonhj(VoUtils.getDZFDouble(ywhdvos[1].getMonhj()).add(VoUtils.getDZFDouble(ywhdvos[2].getMonhj())).add(VoUtils.getDZFDouble(ywhdvos[3].getMonhj()))
				.add(VoUtils.getDZFDouble(ywhdvos[4].getMonhj())).add(VoUtils.getDZFDouble(ywhdvos[5].getMonhj())).add(VoUtils.getDZFDouble(ywhdvos[6].getMonhj()))
				.add(VoUtils.getDZFDouble(ywhdvos[7].getMonhj()))
				);
		ywhdvos[8].setYearfxdx(VoUtils.getDZFDouble(ywhdvos[1].getYearfxdx()).add(VoUtils.getDZFDouble(ywhdvos[2].getYearfxdx())).add(VoUtils.getDZFDouble(ywhdvos[3].getYearfxdx()))
				.add(VoUtils.getDZFDouble(ywhdvos[4].getYearfxdx())).add(VoUtils.getDZFDouble(ywhdvos[5].getYearfxdx())).add(VoUtils.getDZFDouble(ywhdvos[6].getYearfxdx()))
				.add(VoUtils.getDZFDouble(ywhdvos[7].getYearfxdx()))
				);
		ywhdvos[8].setYearxdx(VoUtils.getDZFDouble(ywhdvos[1].getYearxdx()).add(VoUtils.getDZFDouble(ywhdvos[2].getYearxdx())).add(VoUtils.getDZFDouble(ywhdvos[3].getYearxdx()))
				.add(VoUtils.getDZFDouble(ywhdvos[4].getYearxdx())).add(VoUtils.getDZFDouble(ywhdvos[5].getYearxdx())).add(VoUtils.getDZFDouble(ywhdvos[6].getYearxdx()))
				.add(VoUtils.getDZFDouble(ywhdvos[7].getYearxdx()))
				);
		ywhdvos[8].setYearhj(VoUtils.getDZFDouble(ywhdvos[1].getYearhj()).add(VoUtils.getDZFDouble(ywhdvos[2].getYearhj())).add(VoUtils.getDZFDouble(ywhdvos[3].getYearhj()))
				.add(VoUtils.getDZFDouble(ywhdvos[4].getYearhj())).add(VoUtils.getDZFDouble(ywhdvos[5].getYearhj())).add(VoUtils.getDZFDouble(ywhdvos[6].getYearhj()))
				.add(VoUtils.getDZFDouble(ywhdvos[7].getYearhj()))
				);
		
		
		ywhdvos[9] =  new YwHdVO();
		ywhdvos[9].setHs(null);
		ywhdvos[9].setXm("二、费  用");
		ywhdvos[9].setMonfxdx(null);
		ywhdvos[9].setMonxdx(null);
		ywhdvos[9].setMonhj(null);
		ywhdvos[9].setYearfxdx(null);
		ywhdvos[9].setYearxdx(null);
		ywhdvos[9].setYearhj(null);
		
		
		ywhdvos[10] = new YwHdVO();
		ywhdvos[10].setXm("    （一）业务活动成本");
		ywhdvos[10].setHs("12");
		ywhdvos[10].setMonfxdx(getKMValue(map,0,"5101"));
		ywhdvos[10].setMonxdx(DZFDouble.ZERO_DBL);
		ywhdvos[10].setMonhj(getKMValue(map,0,"5101"));
		ywhdvos[10].setYearfxdx(getKMValue(map,1,"5101"));
		ywhdvos[10].setYearxdx(DZFDouble.ZERO_DBL);
		ywhdvos[10].setYearhj(getKMValue(map,1,"5101"));
		
		
		ywhdvos[11] =  new YwHdVO();
		ywhdvos[11].setHs("13");
		ywhdvos[11].setXm("其中：");
		ywhdvos[11].setMonfxdx(null);
		ywhdvos[11].setMonxdx(null);
		ywhdvos[11].setMonhj(null);
		ywhdvos[11].setYearfxdx(null);
		ywhdvos[11].setYearxdx(null);
		ywhdvos[11].setYearhj(null);
		
		ywhdvos[12] =  new YwHdVO();
		ywhdvos[12].setHs("14");
		ywhdvos[12].setXm("");
		ywhdvos[12].setMonfxdx(null);
		ywhdvos[12].setMonxdx(null);
		ywhdvos[12].setMonhj(null);
		ywhdvos[12].setYearfxdx(null);
		ywhdvos[12].setYearxdx(null);
		ywhdvos[12].setYearhj(null);
		
		
		ywhdvos[13] =  new YwHdVO();
		ywhdvos[13].setHs("15");
		ywhdvos[13].setXm("");
		ywhdvos[13].setMonfxdx(null);
		ywhdvos[13].setMonxdx(null);
		ywhdvos[13].setMonhj(null);
		ywhdvos[13].setYearfxdx(null);
		ywhdvos[13].setYearxdx(null);
		ywhdvos[13].setYearhj(null);
		
		ywhdvos[14] =  new YwHdVO();
		ywhdvos[14].setHs("16");
		ywhdvos[14].setXm("");
		ywhdvos[14].setMonfxdx(null);
		ywhdvos[14].setMonxdx(null);
		ywhdvos[14].setMonhj(null);
		ywhdvos[14].setYearfxdx(null);
		ywhdvos[14].setYearxdx(null);
		ywhdvos[14].setYearhj(null);
		
		ywhdvos[15] = new YwHdVO();
		ywhdvos[15].setXm("（二）管理费用");
		ywhdvos[15].setHs("21");
		ywhdvos[15].setMonfxdx(getKMValue(map,0,"5201"));
		ywhdvos[15].setMonxdx(DZFDouble.ZERO_DBL);
		ywhdvos[15].setMonhj(getKMValue(map,0,"5201"));
		ywhdvos[15].setYearfxdx(getKMValue(map,1,"5201"));
		ywhdvos[15].setYearxdx(DZFDouble.ZERO_DBL);
		ywhdvos[15].setYearhj(getKMValue(map,1,"5201"));
		
		ywhdvos[16] = new YwHdVO();
		ywhdvos[16].setXm("（三）筹资费用");
		ywhdvos[16].setHs("24");
		ywhdvos[16].setMonfxdx(getKMValue(map,0,"5301"));
		ywhdvos[16].setMonxdx(DZFDouble.ZERO_DBL);
		ywhdvos[16].setMonhj(getKMValue(map,0,"5301"));
		ywhdvos[16].setYearfxdx(getKMValue(map,1,"5301"));
		ywhdvos[16].setYearxdx(DZFDouble.ZERO_DBL);
		ywhdvos[16].setYearhj(getKMValue(map,1,"5301"));
		
		
		ywhdvos[17] = new YwHdVO();
		ywhdvos[17].setXm("（四）其他费用");
		ywhdvos[17].setHs("28");
		ywhdvos[17].setMonfxdx(getKMValue(map,0,"5401"));
		ywhdvos[17].setMonxdx(DZFDouble.ZERO_DBL);
		ywhdvos[17].setMonhj(getKMValue(map,0,"5401"));
		ywhdvos[17].setYearfxdx(getKMValue(map,1,"5401"));
		ywhdvos[17].setYearxdx(DZFDouble.ZERO_DBL);
		ywhdvos[17].setYearhj(getKMValue(map,1,"5401"));
		
		ywhdvos[18] = new YwHdVO();
		ywhdvos[18].setXm("费用合计");
		ywhdvos[18].setHs("35");
		ywhdvos[18].setMonfxdx(VoUtils.getDZFDouble(ywhdvos[10].getMonfxdx()).add(VoUtils.getDZFDouble(ywhdvos[15].getMonfxdx()))
				.add(VoUtils.getDZFDouble(ywhdvos[16].getMonfxdx())).add(VoUtils.getDZFDouble(ywhdvos[17].getMonfxdx())));
		ywhdvos[18].setMonxdx(DZFDouble.ZERO_DBL);
		ywhdvos[18].setMonhj(VoUtils.getDZFDouble(ywhdvos[10].getMonhj()).add(VoUtils.getDZFDouble(ywhdvos[15].getMonhj()))
				.add(VoUtils.getDZFDouble(ywhdvos[16].getMonhj())).add(VoUtils.getDZFDouble(ywhdvos[17].getMonhj())));
		ywhdvos[18].setYearfxdx(VoUtils.getDZFDouble(ywhdvos[10].getYearfxdx()).add(VoUtils.getDZFDouble(ywhdvos[15].getYearfxdx()))
				.add(VoUtils.getDZFDouble(ywhdvos[16].getYearfxdx())).add(VoUtils.getDZFDouble(ywhdvos[17].getYearfxdx())));
		ywhdvos[18].setYearxdx(DZFDouble.ZERO_DBL);
		ywhdvos[18].setYearhj(VoUtils.getDZFDouble(ywhdvos[10].getYearhj()).add(VoUtils.getDZFDouble(ywhdvos[15].getYearhj()))
				.add(VoUtils.getDZFDouble(ywhdvos[16].getYearhj())).add(VoUtils.getDZFDouble(ywhdvos[17].getYearhj())));
		
		
		ywhdvos[19] =  new YwHdVO();
		ywhdvos[19].setHs("40");
		ywhdvos[19].setXm("三、限定性净资产转为非限定性净资产");
		ywhdvos[19].setMonfxdx(null);
		ywhdvos[19].setMonxdx(null);
		ywhdvos[19].setMonhj(null);
		ywhdvos[19].setYearfxdx(null);
		ywhdvos[19].setYearxdx(null);
		ywhdvos[19].setYearhj(null);
		
		
		ywhdvos[20] =  new YwHdVO();
		ywhdvos[20].setHs("45");
		ywhdvos[20].setXm("四、净资产变动额（若为净资产减少额，以“-”号填列）");
		ywhdvos[20].setMonfxdx(VoUtils.getDZFDouble(ywhdvos[8].getMonfxdx()).sub(VoUtils.getDZFDouble(ywhdvos[18].getMonfxdx()))
				.add(VoUtils.getDZFDouble(ywhdvos[19].getMonfxdx())));
		ywhdvos[20].setMonxdx(VoUtils.getDZFDouble(ywhdvos[8].getMonxdx()).sub(VoUtils.getDZFDouble(ywhdvos[18].getMonxdx()))
				.add(VoUtils.getDZFDouble(ywhdvos[19].getMonxdx())));
		ywhdvos[20].setMonhj(VoUtils.getDZFDouble(ywhdvos[8].getMonhj()).sub(VoUtils.getDZFDouble(ywhdvos[18].getMonhj()))
				.add(VoUtils.getDZFDouble(ywhdvos[19].getMonhj())));
		ywhdvos[20].setYearfxdx(VoUtils.getDZFDouble(ywhdvos[8].getYearfxdx()).sub(VoUtils.getDZFDouble(ywhdvos[18].getYearfxdx()))
				.add(VoUtils.getDZFDouble(ywhdvos[19].getYearfxdx())));
		ywhdvos[20].setYearxdx(VoUtils.getDZFDouble(ywhdvos[8].getYearxdx()).sub(VoUtils.getDZFDouble(ywhdvos[18].getYearxdx()))
				.add(VoUtils.getDZFDouble(ywhdvos[19].getYearxdx())));
		ywhdvos[20].setYearhj(VoUtils.getDZFDouble(ywhdvos[8].getYearhj()).sub(VoUtils.getDZFDouble(ywhdvos[18].getYearhj()))
				.add(VoUtils.getDZFDouble(ywhdvos[19].getYearhj())));
		
		return ywhdvos;
	}
	
	/**
	 * 获取对应的数据
	 * @param mode
	 * @return
	 */
	private DZFDouble getKMValue(Map<String, FseJyeVO> map, int mode,String km){
		
        FseJyeVO fsvo = map.get(km);
        
        if(fsvo == null){
        	return DZFDouble.ZERO_DBL;
        }
        Integer direction = fsvo.getDirection() == null?0:fsvo.getDirection();
		DZFDouble sumvalue = DZFDouble.ZERO_DBL;
        if(0==mode){//取期末数据
        	DZFDouble jffs = VoUtils.getDZFDouble(fsvo.getFsjf());
			DZFDouble dffs = VoUtils.getDZFDouble(fsvo.getFsdf());
			if(direction.intValue() ==0 ){
				sumvalue = sumvalue.add(jffs);
			}else{
				sumvalue = sumvalue.add(dffs);
			}
		}else if(1 == mode){//取本年发生额数据
			DZFDouble jffs = VoUtils.getDZFDouble(fsvo.getJftotal());
			DZFDouble dffs = VoUtils.getDZFDouble(fsvo.getDftotal());
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

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

}
