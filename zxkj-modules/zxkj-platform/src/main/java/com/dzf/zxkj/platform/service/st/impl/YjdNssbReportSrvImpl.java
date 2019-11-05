package com.dzf.zxkj.platform.service.st.impl;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.platform.model.st.StBaseVO;
import com.dzf.zxkj.platform.model.st.StNssbInfoVO;
import com.dzf.zxkj.platform.service.st.IYjdNssbReportSrv;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 纳税申报报表业务逻辑实现类
 * */
@Service("Yjdnssbsrv")
public class YjdNssbReportSrvImpl implements IYjdNssbReportSrv {
	
	

	@Override
	public StBaseVO[] querySingleReport(String reportcode, String cyear, String period, String pk_corp) throws DZFWarpException {
		if(cyear==null||pk_corp==null||reportcode==null)
			throw new BusinessException("参数不正确!");
		NssbYjdDMO dmo = new NssbYjdDMO();
		Class beantype = NssbReportUtil.getReportBeanType(reportcode);
		if(beantype ==null){
			throw new BusinessException("报表:"+reportcode+"没有定义实体类型,请前往NssbReportUtil注册");
		}
		try{
		StBaseVO[] rsvos =dmo.queryDefaultReport(pk_corp, cyear, period,beantype);
		if(rsvos!=null&&rsvos.length>0){
			rsvos=dmo.setReportValue(reportcode, rsvos);
		}else{
			return null;
		}
		return rsvos;
		}catch(Exception e){
			////e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}
	
/*	@Override
//	public Map<String,StBaseVO[]> queryReportAll(String cyear,String pk_corp) throws BusinessException{
	public Map<String,StBaseVO[]> queryReportAll(StNssbInfoVO infvo) throws BusinessException{
		
		NssbReportCellDMO dmo = new NssbReportCellDMO();
		
		String cyear =infvo.getCyear();
		String pk_corp =infvo.getPk_corp();
		if(cyear==null||pk_corp==null)
			throw new BusinessException("参数不正确!");
		
		infvo = dmo.getNssbInfo(infvo);
		if(infvo==null){
			throw new BusinessException("没有税表信息，请先生成申报表!");
		}
		
		
		String[] reportcodes =NssbReportUtil.reportcodes;
		Map<String,StBaseVO[]> vosmap = new HashMap<String,StBaseVO[]>();
		int rpcount=0;
		for(String reportcode:reportcodes){
			StBaseVO[] basevo =querySingleReport(reportcode, cyear,null, pk_corp);
			if(basevo!=null&&basevo.length>0){
				vosmap.put(reportcode, basevo);
				rpcount++;
			}
		}
		
		if(rpcount==0){
			throw new BusinessException("没有税表信息，请先生成申报表!");
		}
		
		vosmap.put("info", new StBaseVO[]{infvo});
		
		return vosmap;
	}
	*/
	public Map<String,StBaseVO[]> queryYJDReport(StNssbInfoVO infvo) throws DZFWarpException{
		
		NssbReportCellDMO dmo = new NssbReportCellDMO();
		
		String cyear =infvo.getCyear();
		String pk_corp =infvo.getPk_corp();
		String period=infvo.getPeriod();
		if(cyear==null||pk_corp==null||period==null)
			throw new BusinessException("参数不正确!");
		
		infvo = dmo.getjdNssbInfo(infvo);
		if(infvo==null){
			throw new BusinessException("没有税表信息，请先生成申报表!");
		}
		
		
		String[] reportcodes =NssbReportUtil.yjdreportcodes;
		Map<String,StBaseVO[]> vosmap = new HashMap<String,StBaseVO[]>();
		int rpcount=0;
		for(String reportcode:reportcodes){
			StBaseVO[] basevo =querySingleReport(reportcode, cyear,period, pk_corp);
			if(basevo!=null&&basevo.length>0){
				vosmap.put(reportcode, basevo);
				rpcount++;
			}
		}
		
		if(rpcount==0){
			throw new BusinessException("没有税表信息，请先生成申报表!");
		}
		
		vosmap.put("info", new StBaseVO[]{infvo});
		
		return vosmap;
	}
	
	@Override
//	public Map<String,StBaseVO[]> GenNssbBasicReport(String cyear, String pk_corp)StNssbInfoVO infvo
	public Map<String,StBaseVO[]> GenNssbBasicReport(StNssbInfoVO infvo)
			throws DZFWarpException {
		
		String cyear =infvo.getCyear();
		String pk_corp =infvo.getPk_corp();
		
		if(cyear==null||pk_corp==null)
			throw new BusinessException("参数不正确!");
		NssbReportCellDMO dmo = new NssbReportCellDMO(pk_corp, cyear);
		
		
		String status = dmo.getStatus(infvo);
		if(status.equals(NssbReportUtil.approved)){
			throw new BusinessException("当前申报表已被审核，如需重新生成，请先取消审核！");
		}
		String[] reportcodes =NssbReportUtil.reportcodes;
		Map<String,StBaseVO[]> vosmap = new HashMap<String,StBaseVO[]>();
		for(String reportcode:reportcodes){
			Class beantype = NssbReportUtil.getReportBeanType(reportcode);
			
			if(beantype ==null){
				throw new BusinessException("报表:"+reportcode+"没有定义实体类型,请前往NssbReportUtil注册");
			}
			
			StBaseVO[] basevo =dmo.genBaseReprot(reportcode, beantype,vosmap);
			if(basevo!=null&&basevo.length>0){
				basevo=dmo.setReportValue(reportcode, basevo);
				vosmap.put(reportcode, basevo);
			}
		}
		
		infvo=dmo.saveNssbInfo(infvo);
		vosmap.put("info", new StBaseVO[]{infvo});
		
		return vosmap;

	}
	
	//修改后值重新计算
	@Override
	public StBaseVO[] reCalculate(String reportcode,StBaseVO[] reportvos) throws DZFWarpException{
		NssbReportRCellDMO dmo= new NssbReportRCellDMO();
		try {
			reportvos=dmo.reCalculateRpCellFm(reportcode, reportvos);
			reportvos=dmo.reCalculate(reportcode, reportvos);//统计结果重新计算
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		return reportvos;
	}
	
	//全表计算
	public Map<String,StBaseVO[]>  reCalculateMain(Map<String,StBaseVO[]> vosmap)throws DZFWarpException{
		NssbReportCellDMO celldmo = new NssbReportCellDMO();
		NssbReportRCellDMO dmo= new NssbReportRCellDMO();
		dmo.setBcalmain(true);
		
		try{
			vosmap=dmo.reCalculateMain(vosmap);//根据公式计算
			String[] codes = NssbReportUtil.reportcodes;
			for(String code:codes){
				//持久化
				celldmo.updateReportVO(code, vosmap.get(code));//持久化
			}
		}catch(Exception e){
			throw new BusinessException(e.getMessage());
		}
		return vosmap;
	}
	
	@Override
	public void updateReportAll(Map<String,StBaseVO[]> vosmap) throws DZFWarpException{
		if(vosmap==null){
			throw new BusinessException("保存失败，没有数据!");
		}
		String[] reportcodes = vosmap.keySet().toArray(new String[0]);
		if(reportcodes==null||reportcodes.length==0){
			throw new BusinessException("保存失败，没有数据!");
		}
		for(String reportcode:reportcodes){
			updateSingleReport(reportcode, vosmap.get(reportcode));
		}
		
	}
	
	//单表保存
	@Override
	public void updateSingleReport(String reportcode,StBaseVO[] reportvos) throws DZFWarpException{
		NssbReportCellDMO dmo= new NssbReportCellDMO();
		
		if(reportcode==null||reportcode.length()==0||reportvos==null||reportvos.length==0){
			throw new BusinessException("数据不完全，请重新提交！");
		}
		
		//先校验金额正确性
		String checkrs = NssbReportCheck.getInstance().CheckCellEdit(reportcode, reportvos);
		if(checkrs!=null){
			throw new BusinessException(checkrs,NssbReportUtil.ERROR_CHK);
		}
		
		
		//然后再重新计算 
		reportvos = reCalculate(reportcode, reportvos);
		
		//持久化
		dmo.updateReportVO(reportcode, reportvos);
	}
	

	@Override
	public Map<String,StBaseVO[]> GenNssbMainReport(StNssbInfoVO infvo)
			throws DZFWarpException {
		String cyear =infvo.getCyear();
		String pk_corp =infvo.getPk_corp();
		
		if(cyear==null||pk_corp==null)
			throw new BusinessException("参数不正确!");
		NssbReportCellDMO dmo = new NssbReportCellDMO(pk_corp, cyear);
		
		
		String status = dmo.getStatus(infvo);
		if(status.equals(NssbReportUtil.approved)){
			throw new BusinessException("当前申报表已被审核，如需重新生成，请先取消审核！");
		}
		
		try{
	//		String[] reportcodes =NssbReportUtil.reportcodes;
			Map<String,StBaseVO[]> vosmap = dmo.genMainReprot();
			 
			String[] reportcodes=vosmap.keySet().toArray(new String[0]);
			
			for(String reportcode:reportcodes){
				StBaseVO[] basevo=dmo.setReportValue(reportcode, vosmap.get(reportcode));
				vosmap.put(reportcode, basevo);
			}
			
			infvo=dmo.saveNssbInfo(infvo);
			vosmap.put("info", new StBaseVO[]{infvo});
			
			return vosmap;
		}catch(Exception e){
			throw new BusinessException(e.getMessage());
			
		}
	}
	//月季度纳税申报
	@Override
	public Map<String, StBaseVO[]> GenYJDNssbMainReport(StNssbInfoVO infvo)
			throws DZFWarpException {
		
		String cyear =infvo.getCyear();
		String pk_corp =infvo.getPk_corp();
		String period=infvo.getPeriod();
		
		if(cyear==null||pk_corp==null||period==null)
			throw new BusinessException("参数不正确!");
		NssbYjdDMO dmo = new NssbYjdDMO(pk_corp, cyear, period);
		
		
		String status = dmo.getStatus(infvo);
		if(status.equals(NssbReportUtil.approved)){
			throw new BusinessException("当前申报表已被审核，如需重新生成，请先取消审核！");
		}
		
		try{
	//		String[] reportcodes =NssbReportUtil.reportcodes;
			Map<String,StBaseVO[]> lj_vosmap = dmo.genYJDMainReprot();
			 
			String[] reportcodes=lj_vosmap.keySet().toArray(new String[0]);
			
			for(String reportcode:reportcodes){
				StBaseVO[] basevo=dmo.setReportValue(reportcode, lj_vosmap.get(reportcode));
				lj_vosmap.put(reportcode, basevo);
			}
			
			infvo=dmo.saveNssbInfo(infvo);
			lj_vosmap.put("info", new StBaseVO[]{infvo});
	
			return lj_vosmap;
		}catch(Exception e){
			throw new BusinessException(e.getMessage());
		}
	}
	
	
	
	/**
	 * 审核
	 * */
	@Override
	public void approveRpinfo(StNssbInfoVO infvo) throws DZFWarpException{
		NssbReportCellDMO dmo = new NssbReportCellDMO();
		
		StNssbInfoVO oldvo=dmo.getjdNssbInfo(infvo);
		
		if(oldvo==null){
			throw new BusinessException("没有需要审核的税表信息!");
		}
		
		oldvo.setCstatus(NssbReportUtil.approved);
		oldvo.setApprovepsnid(infvo.getApprovepsnid());
		oldvo.setApprovetime(new DZFDateTime());
		
		dmo.updateNssbInfo(oldvo);
	}
	
	/**
	 * 取消审核
	 * */
	@Override
	public void unaApproveRpinfo(StNssbInfoVO infvo) throws DZFWarpException{
		NssbReportCellDMO dmo = new NssbReportCellDMO();
		StNssbInfoVO oldvo=dmo.getjdNssbInfo(infvo);
		
		if(oldvo==null){
			throw new BusinessException("没有需要取消审核的税表信息!");
		}
		
		oldvo.setCstatus(NssbReportUtil.unapproved);
		oldvo.setApprovepsnid(null);
		oldvo.setApprovetime(null);
		
		dmo.updateNssbInfo(oldvo);
	}



}
