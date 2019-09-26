package com.dzf.zxkj.platform.services.st.impl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.st.NssbReportCheck;
import com.dzf.zxkj.platform.model.st.NssbReportUtil;
import com.dzf.zxkj.platform.model.st.StBaseVO;
import com.dzf.zxkj.platform.model.st.StNssbInfoVO;
import com.dzf.zxkj.platform.services.st.INssbReportSrv;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 纳税申报报表业务逻辑实现类
 * */
@Service("nssbsrv")
public class NssbReportSrvImpl implements INssbReportSrv {
	
	

	@Override
	public StBaseVO[] querySingleReport(String reportcode, String cyear, String pk_corp) throws DZFWarpException {
		if (cyear == null || pk_corp == null || reportcode == null)
			throw new BusinessException("参数不正确!");
		StBaseVO[] rsvos =null;
		NssbReportCellDMO dmo = new NssbReportCellDMO();
		Class beantype = NssbReportUtil.getReportBeanType(reportcode);
		if (beantype == null) {
			throw new BusinessException("报表:" + reportcode + "没有定义实体类型,请前往NssbReportUtil注册");
		}
		try {
			rsvos = dmo.queryDefaultReport(pk_corp, cyear, beantype);
			if (rsvos != null && rsvos.length > 0) {
				rsvos = dmo.setReportValue(reportcode, rsvos);
			}
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

		return rsvos;
	}
	
	@Override
//	public Map<String,StBaseVO[]> queryReportAll(String cyear,String pk_corp) throws BusinessException{
	public Map<String,StBaseVO[]> queryReportAll(StNssbInfoVO infvo) throws DZFWarpException{
		
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
		StBaseVO[] basevo=null;
		for(String reportcode:reportcodes){
			basevo =querySingleReport(reportcode, cyear, pk_corp);
			if(reportcode.equals("A107040")){
				for(StBaseVO vo: basevo){
					if(vo.getVprojectname()!=null&& vo.getVprojectname().toString().indexOf("##")>0){
						String[] vls = vo.getVprojectname().split("##",2);
						vo.setVprojectname(vls[0]);
						vo.setAttributeValue("vprojectname2", vls[1]);
					}
				}
			}
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
	public Map<String,StBaseVO[]> GenNssbBasicReport(StNssbInfoVO infvo) throws DZFWarpException {
		
		String cyear =infvo.getCyear();
		String pk_corp =infvo.getPk_corp();
		
		if(StringUtil.isEmpty(cyear)|| StringUtil.isEmpty(pk_corp))
			throw new BusinessException("参数不正确!");
		NssbReportCellDMO dmo = new NssbReportCellDMO(pk_corp, cyear);
		
		
		String status = dmo.getStatus(infvo);
		if(status.equals(NssbReportUtil.approved)){
			throw new BusinessException("当前申报表已被审核，如需重新生成，请先取消审核！");
		}
		String[] reportcodes =NssbReportUtil.reportcodes;
		Map<String,StBaseVO[]> vosmap = new HashMap<String,StBaseVO[]>();
		Class beantype=null;
		StBaseVO[] basevo=null;
		for(String reportcode:reportcodes){
			beantype = NssbReportUtil.getReportBeanType(reportcode);
			
			if(beantype ==null){
				throw new BusinessException("报表:"+reportcode+"没有定义实体类型,请前往NssbReportUtil注册");
			}
			
			basevo =dmo.genBaseReprot(reportcode, beantype,vosmap);
			if(basevo!=null&&basevo.length>0){
				basevo=dmo.setReportValue(reportcode, basevo);
				vosmap.put(reportcode, basevo);
			}
		}
		
		infvo=dmo.saveNssbInfo(infvo);
		vosmap.put("info", new StBaseVO[]{infvo});
		
		return vosmap;
		
//		NssbReportCellDMO dmo = new NssbReportCellDMO(pk_corp, cyear);
//		Map<String,StBaseVO[]> baseMap= new HashMap<String,StBaseVO[]>();
//		
//		StYbqysrVO[] ybqysrvos =dmo.genYbqysrReprot();//一般企业收入
//		ybqysrvos=dmo.setReportValue("A101010", ybqysrvos);
//		baseMap.put("A101010", ybqysrvos);
//		
//		StYbqycbVO[] ybqycbvos =dmo.genYbqycbReprot();//一般企业成本
//		ybqycbvos=dmo.setReportValue("A102010", ybqycbvos);
//		baseMap.put("A102010", ybqycbvos);
//		
//		StqjfyVO[] qjfyvos =dmo.genQyjffymxReprot();//期间费用明细
//		qjfyvos=dmo.setReportValue("A104000", qjfyvos);
//		baseMap.put("A104000", qjfyvos);
//		
//		
//		
//		
//		return baseMap;
	}
	
	//修改后值重新计算
	@Override
	public StBaseVO[] reCalculate(String reportcode,StBaseVO[] reportvos) throws DZFWarpException{
		NssbReportRCellDMO dmo= new NssbReportRCellDMO();
		if(reportvos!=null&&reportvos.length>0){
			dmo.setPk_corp(reportvos[0].getPk_corp());
			try{
				reportvos=dmo.reCalculateRpCellFm(reportcode, reportvos);
				reportvos=dmo.reCalculate(reportcode, reportvos);//统计结果重新计算
			}catch(Exception e){
				throw new WiseRunException(e);
			}
		}
		return reportvos;
	}
	
	//全表计算
	public Map<String,StBaseVO[]>  reCalculateMain(Map<String,StBaseVO[]> vosmap)throws DZFWarpException{
		NssbReportCellDMO celldmo = new NssbReportCellDMO();
		NssbReportRCellDMO dmo= new NssbReportRCellDMO();
		dmo.setBcalmain(true);
		StBaseVO[] vos=null;
		if(vosmap!=null&&vosmap.size()>0){
			Iterator<String> it = vosmap.keySet().iterator();
			while(it.hasNext()){
				vos = vosmap.get(it.next());
				if(vos!=null&&vos.length>0){
					dmo.setPk_corp(vos[0].getPk_corp());
					break;
				}				
			}
			try{
				vosmap=dmo.reCalculateMain(vosmap);//根据公式计算
				
				String[] codes = NssbReportUtil.reportcodes;
				
				for(String code:codes){
					//持久化
					celldmo.updateReportVO(code, vosmap.get(code));//持久化
				}
				
			}catch(Exception e){
				throw new WiseRunException(e);
			}
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
	public Map<String,StBaseVO[]> GenNssbMainReport(StNssbInfoVO infvo) throws DZFWarpException {
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
			//e.printStackTrace();
			throw new BusinessException(e.getMessage());
			
		}
	}
	
	
	
	
	/**
	 * 审核
	 * */
	@Override
	public void approveRpinfo(StNssbInfoVO infvo) throws DZFWarpException{
		NssbReportCellDMO dmo = new NssbReportCellDMO();
		
		StNssbInfoVO oldvo=dmo.getNssbInfo(infvo);
		
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
		StNssbInfoVO oldvo=dmo.getNssbInfo(infvo);
		
		if(oldvo==null){
			throw new BusinessException("没有需要取消审核的税表信息!");
		}
		
		oldvo.setCstatus(NssbReportUtil.unapproved);
		oldvo.setApprovepsnid(null);
		oldvo.setApprovetime(null);
		
		dmo.updateNssbInfo(oldvo);
	}

}
