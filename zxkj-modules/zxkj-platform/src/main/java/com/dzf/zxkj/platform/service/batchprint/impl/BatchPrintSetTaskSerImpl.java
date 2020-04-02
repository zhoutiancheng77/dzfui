package com.dzf.zxkj.platform.service.batchprint.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.dzf.file.fastdfs.AppException;
import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.batchprint.BandingVO;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetQryVo;
import com.dzf.zxkj.platform.model.batchprint.PrintStatusEnum;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.batchprint.BatchPrintUtil;
import com.dzf.zxkj.platform.service.batchprint.IBatchPrintSetTaskSer;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.BeanUtils;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.dzf.zxkj.platform.util.ReportUtil;
import com.dzf.zxkj.platform.util.VoUtils;
import com.dzf.zxkj.report.service.IZxkjReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 批量设置业务操作
 * 
 * @author zhangj
 *
 */
@Service("batchprintser")
@Slf4j
public class BatchPrintSetTaskSerImpl implements IBatchPrintSetTaskSer {

	@Autowired
	private SingleObjectBO singleObjectBO = null;

	@Autowired
	private IVoucherService gl_tzpzserv ;

	@Autowired
	private IZxkjReportService zxkjReportService;

	@Autowired
	private IUserService userServiceImpl;

	@Autowired
	private IZxkjPlatformService zxkjPlatformService;


	/**
	 * 根据公司+期间查询成功的公司设置
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BatchPrintSetQryVo> queryPrintVOs2(String pk_corp , String period)  throws DZFWarpException{

		return null;
	}
	
	@Override
	public List<BatchPrintSetVo> queryPrintVOs(String parentcorpid, String corpname,
											   String corpcode, DZFDate begdate, DZFDate enddate, String qry_zt) throws DZFWarpException {

		return queryPrintVOs(parentcorpid, corpname, corpcode, begdate, enddate, qry_zt, "");
	}

	@Override
	public BatchPrintSetVo saveSetTaskVo(BatchPrintSetVo setvo, String operatorid, DZFDateTime opedate) throws DZFWarpException {

		if (setvo == null) {
			throw new BusinessException("数据不能为空!");
		}
		CorpVO cpvo=zxkjPlatformService.queryCorpByPk(setvo.getPk_corp());
		
		if(!StringUtil.isEmpty(setvo.getPk_batch_print_set())){
			
			BatchPrintSetVo oldsetvo = (BatchPrintSetVo) singleObjectBO.queryByPrimaryKey(BatchPrintSetVo.class, setvo.getPk_batch_print_set());
			
			if(oldsetvo.getIfilestatue() !=null && 
					oldsetvo.getIfilestatue().intValue() !=  PrintStatusEnum.PROCESSING.getCode()
					&& oldsetvo.getIfilestatue().intValue() !=  PrintStatusEnum.GENFAIL.getCode()){
				throw new BusinessException(cpvo.getUnitname()+"已生成打印文件，不允许修改!");
			}
			
			setvo.setIfilestatue(PrintStatusEnum.PROCESSING.getCode());
			
			singleObjectBO.update(setvo,new String[]{"Ifilestatue","vprintname","vprintcode","dprintdate"
					,"dleftmargin","dtopmargin","vfontsize","pk_corp","vprintperiod","vothername","zdrlx"});
			
			setvo = (BatchPrintSetVo) singleObjectBO.queryByPrimaryKey(BatchPrintSetVo.class, setvo.getPrimaryKey());
			
			return setvo;
		}
		
		setvo.setVoperateid(operatorid);
		
		setvo.setDoperadatetime(opedate);
		
		setvo.setIfilestatue(PrintStatusEnum.PROCESSING.getCode());//已经生成
		
		setvo.setVmemo("系统排队中,请在次日查看打印文件!");
		
		//如果已经存在，则删除已经存在的数据
//		deleteOldVo(setvo.getPk_corp());
		
		BatchPrintSetVo savevo = (BatchPrintSetVo) singleObjectBO.saveObject(setvo.getPk_corp(), setvo);

		return savevo;
	}
	
	private void deleteOldVo(String pk_corp) throws DZFWarpException{
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		BatchPrintSetVo[] setvos = (BatchPrintSetVo[]) singleObjectBO.queryByCondition(BatchPrintSetVo.class, "nvl(dr,0)=0 and pk_corp = ? ", sp);
		
		if(setvos!=null && setvos.length>0){
			for(BatchPrintSetVo vo:setvos){
				deleteSetTaskVo(vo.getPk_batch_print_set());
			}
		}
		
		
	}

	@Override
	public void updateSetTaskVO(BatchPrintSetVo setvo) throws DZFWarpException {
		if (setvo == null) {
			throw new BusinessException("数据不能为空!");
		}
		singleObjectBO.update(setvo);
	}

	@Override
	public void deleteSetTaskVo(String priid) throws DZFWarpException {

		if (StringUtil.isEmpty(priid)) {
			throw new BusinessException("主键信息不能为空!");
		}
		
		BatchPrintSetVo vo = (BatchPrintSetVo) singleObjectBO.queryByPrimaryKey(BatchPrintSetVo.class, priid);
		
		if(vo == null){
			return;
		}
		
		if (!StringUtil.isEmpty(vo.getVfilepath())) {

			FastDfsUtil util = (FastDfsUtil) SpringUtils.getBean("connectionPool");

			try {
				util.deleteFile(vo.getVfilepath().substring(1));
			} catch (AppException e) {
				throw new WiseRunException(e);
			}

		}
		singleObjectBO.deleteObject(vo);


	}

	@Override
	public void printReport() throws DZFWarpException {
		//生成文件
		SQLParameter sp = new SQLParameter();
		BatchPrintSetVo[] vos = (BatchPrintSetVo[]) singleObjectBO.queryByCondition(BatchPrintSetVo.class, "nvl(dr,0)=0 and (nvl(ifilestatue,0) = 0 or nvl(ifilestatue,0) = 3 )", sp);
		
		if(vos!=null && vos.length>0){
			for(BatchPrintSetVo vo:vos ){
				printReportFromSetVos(vo);
			}
		}
	}

	public void printReportFromSetVos(BatchPrintSetVo vo) throws DZFWarpException {
		QueryParamVO paramvo = null;
		Object[] obj = null;
		CorpVO cpvo = null;
		paramvo = new QueryParamVO();
		try {
			cpvo =  zxkjPlatformService.queryCorpByPk(vo.getPk_corp());
			if (cpvo.getBegindate() == null) {
				throw new BusinessException("公司尚未建账");
			}
			Map<String, String> pmap = new HashMap<String, String>();// 打印参数
			Map<String, SuperVO[]> map = new LinkedHashMap<String, SuperVO[]>();// 打印数据
			// 校验
			if (StringUtil.isEmpty(vo.getVprintname()) || StringUtil.isEmpty(vo.getVprintperiod())) {
				throw new BusinessException("参数不能为空");
			}

			getPrintMap(vo, pmap);// 获取打印参数

			obj = getBaseData(vo.getVprintperiod(), paramvo, vo, cpvo);// 获取基础数据

			getMapData(vo.getVprintperiod(), vo.getVprintname(), obj, paramvo, vo, map, cpvo);// 获取打印数据

			upPrintSetVO(vo, pmap, map);

		} catch (Exception e) {
			vo.setIfilestatue(PrintStatusEnum.GENFAIL.getCode());
			if (e instanceof BusinessException) {
				vo.setVmemo(e.getMessage());
			} else {
				log.error("错误", e);
				vo.setVmemo("生成失败!");
			}
		}
		
		singleObjectBO.update(vo,new String[]{"ifilestatue","vfilepath","vfilename","vmemo","dgendatetime"});
//		singleObjectBO.updateAry(vo,new String[]{"ifilestatue","vfilepath","vfilename","vmemo","dgendatetime"});//更新状态字段
	}
	
	private void upPrintSetVO(BatchPrintSetVo vo,Map<String, String> pmap,Map<String,SuperVO[]> map){
		String fileid = "";//文件id
		
		if(map.size() ==0){//如果不存在数据则不进行文件生成
			throw new BusinessException("公司尚未做账!");
		}
		
		BatchPrintUtil util = new BatchPrintUtil();
		CorpVO cpvo =  null;
		try {
			cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, vo.getPk_corp());
			fileid = util.print(map, pmap, vo.getVoperateid(), cpvo,cpvo.getUnitcode(),vo,userServiceImpl);
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
		
		vo.setIfilestatue(PrintStatusEnum.GENERATE.getCode());
		
		vo.setVfilepath(fileid);
		
		vo.setDgendatetime(new DZFDateTime());
		
		vo.setVmemo("文件已生成!");
		
		SimpleDateFormat f=new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒");
		String str_time = f.format(new Date());
		
		vo.setVfilename(CodeUtils1.deCode(cpvo.getUnitname())+"("+str_time+")"+".pdf");
	}

	/**
	 * 查询科目明细账数据
	 * @param qj
	 * @param paramvo
	 * @param vo
	 * @param cpvo
	 * @return
	 */
	private Object[] getBaseData(String qj, QueryParamVO paramvo, BatchPrintSetVo vo,CorpVO cpvo) {
		String[] qjs = qj.split("~");
		Object[] obj;
		paramvo.setPk_corp(vo.getPk_corp());
		Integer begyear = Integer.parseInt(qjs[0].substring(0, 4));
		DZFDate begdate = DateUtils.getPeriodEndDate((begyear-1)+"-"+qjs[0].substring(5, 7));
		if(begdate.before(cpvo.getBegindate())){
			begdate = new DZFDate(cpvo.getBegindate().getYear()+"-01-01");
		}
		DZFDate enddate = DateUtils.getPeriodEndDate(qjs[1]);
		if(enddate.before(cpvo.getBegindate())){
			throw new BusinessException("查询区间"+qj+"中在该公司建账日期("+cpvo.getBegindate()+")前");
		}
		paramvo.setBegindate1(begdate);
		paramvo.setEnddate(DateUtils.getPeriodEndDate(qjs[1]));
		paramvo.setIshasjz(DZFBoolean.FALSE);
		paramvo.setXswyewfs(DZFBoolean.FALSE);
		paramvo.setBtotalyear(DZFBoolean.TRUE);//本年累计
		paramvo.setCjq(1);
		paramvo.setCjz(6);
		obj =  zxkjReportService.getKMMXZVOs1(paramvo, false);//获取基础数据(科目明细账)
		//重新赋值真正查询日期
		begdate = DateUtils.getPeriodEndDate((begyear)+"-"+qjs[0].substring(5, 7));
		if(begdate.before(cpvo.getBegindate())){
			begdate = cpvo.getBegindate();
		}
		paramvo.setBegindate1(begdate);
		return obj;
	}

	private void getPrintMap(BatchPrintSetVo vo, Map<String, String> pmap) {
		pmap.put("left",String.valueOf( vo.getDleftmargin()));
		pmap.put("top",String.valueOf(vo.getDtopmargin()));
		pmap.put("printdate",vo.getDprintdate() == null ? new DZFDate().toString():vo.getDprintdate().toString());
		pmap.put("font",vo.getVfontsize().toString());
		pmap.put("pageNum","1");
	}

	private void getMapData(String qj,String reportname, Object[] obj, QueryParamVO paramvo, BatchPrintSetVo vo,
			Map<String, SuperVO[]> map,CorpVO cpvo) {
		KmZzVO[] zzvos =  null;
		KmMxZVO[] kmmxvos = null;
		KmMxZVO[] xjyhvos = null;
		List<ZcFzBVO[]> zcfzvos = null;
		List<LrbVO[]> lrbvos = null;
		FseJyeVO[] fsejyevos  = null;
		String[] reportnames = reportname.split(",");
		
		String begperiod  = DateUtils.getPeriod(paramvo.getBegindate1());
		
		paramvo.setQjq(begperiod);
		
		String vprintperiod = DateUtils.getPeriod(paramvo.getBegindate1())+"~"+DateUtils.getPeriod(paramvo.getEnddate());
		
		//排序
		String[] defaultnames = new String[]{"凭证","科目总账","科目明细账","现金/银行日记账","发生额及余额表","资产负债表","利润表","凭证封皮","总账明细账封皮"};
		
		for(String str:defaultnames){
			if(Arrays.asList(reportnames).contains(str)){
				try {
					if("凭证".equals(str)){
						VoucherParamVO voucherparmvo = new VoucherParamVO();
						voucherparmvo.setPk_corp(paramvo.getPk_corp());
						voucherparmvo.setBegindate(DateUtils.getPeriodStartDate(begperiod));
						voucherparmvo.setEnddate(paramvo.getEnddate());
//						List<TzpzHVO> hvos =  gl_tzpzserv.queryVoucher(voucherparmvo);
//						if(hvos!=null && hvos.size()>0){
//							map.put("凭证_"+vprintperiod, hvos.toArray(new TzpzHVO[0]));
//						}
					} else if ("科目总账".equals(str)) {
						Object[] objtemp  = (Object[]) deepClone(obj);
						handleKmmx(objtemp,begperiod);
						QueryParamVO kmzzqryvo = new QueryParamVO();
						BeanUtils.copyNotNullProperties(paramvo, kmzzqryvo);
						kmzzqryvo.setCjq(1);
						kmzzqryvo.setCjz(1);
						zzvos = zxkjReportService.getKMZZVOs(kmzzqryvo, objtemp);
						if(zzvos!=null && zzvos.length>0){
							List<KmZzVO> kmzzvos = KmzzUtil.filterQC(zzvos);
							map.put("科目总账_"+vprintperiod, kmzzvos.toArray(new KmZzVO[0]));
						}
					} else if ("科目明细账".equals(str)) {
						Object[] objtemp  = (Object[]) deepClone(obj);
						handleKmmx(objtemp,begperiod);
						QueryParamVO kmmxparamvo =  (QueryParamVO) deepClone(paramvo);
						kmmxvos =  zxkjReportService.getKMMXZConFzVOs(kmmxparamvo, objtemp);
						if(kmmxvos!=null && kmmxvos.length>0){
							kmmxvos = KmmxUtil.filterQcVos(kmmxvos,kmmxparamvo.getPk_corp());
							map.put("科目明细账_"+vprintperiod, kmmxvos);
						}
					} else if ("现金/银行日记账".equals(str)) {
						Object[] objtemp  = (Object[]) deepClone(obj);
						handleKmmx(objtemp,begperiod);
						QueryParamVO xjrjparamvo =  (QueryParamVO) deepClone(paramvo);
						xjyhvos = zxkjReportService.getXJRJZVOsConMo(xjrjparamvo.getPk_corp(),
								xjrjparamvo.getKms_first(),xjrjparamvo.getKms_last(),xjrjparamvo.getBegindate1(), 
								xjrjparamvo.getEnddate(), xjrjparamvo.getXswyewfs(),
								xjrjparamvo.getXsyljfs(), xjrjparamvo.getIshasjz(), 
								xjrjparamvo.getIshassh(), xjrjparamvo.getPk_currency(), null,objtemp);
						if(xjyhvos!=null && xjyhvos.length>0){
							for(KmMxZVO xjrjvo:xjyhvos){//
								xjrjvo.setKm(xjrjvo.getKm().trim());
								if(xjrjvo.getZy()!=null && ReportUtil.bSysZy(xjrjvo) && (xjrjvo.getZy().equals("期初余额") ||xjrjvo.getZy().equals("本月合计") || xjrjvo.getZy().equals("本年累计"))){
									xjrjvo.setRq(xjrjvo.getRq()+xjrjvo.getDay());
								}
							}
							map.put("现金/银行日记账_"+vprintperiod, xjyhvos);
						}
					} else if ("资产负债表".equals(str)) {
						Object[] objtemp  = (Object[]) deepClone(obj);
						zcfzvos = zxkjReportService.getZcfzVOs(paramvo.getBegindate1(), paramvo.getEnddate(), paramvo.getPk_corp(),
								"N", new String[]{"N","N","N","N","N"}, objtemp);
						if(zcfzvos!=null && zcfzvos.size()>0){
							for(ZcFzBVO[] tempvos:zcfzvos){
								map.put("资产负债表_"+DateUtils.getPeriodEndDate(tempvos[0].getPeriod()), tempvos);
							}
						}
					} else if ("利润表".equals(str)) {
						QueryParamVO lrbparamvo =  (QueryParamVO) deepClone(paramvo);
						lrbparamvo.setRptsource("lrb");
						Object[] objtemp  = getBaseData(vo.getVprintperiod(), lrbparamvo, vo, cpvo);
						if("小规模纳税人".equals(cpvo.getChargedeptname())){
							Map<String, LrbquarterlyVO[]> lrbjbmap =  zxkjReportService.getLRBquarterlyVOs(lrbparamvo, objtemp);
							if(lrbjbmap!=null && lrbjbmap.size()>0){
								for(Entry<String, LrbquarterlyVO[]> entry:lrbjbmap.entrySet()){
									map.put("利润表季报_"+entry.getKey(), entry.getValue());
								}
							}
						}else{
//							ILrbReport gl_rep_lrbserv = (ILrbReport) SpringUtils.getBean("gl_rep_lrbserv");
							lrbvos =  zxkjReportService.getBetweenLrbMap(lrbparamvo.getBegindate1(), lrbparamvo.getEnddate(), lrbparamvo.getPk_corp(), "", objtemp,null);
							if(lrbvos!=null && lrbvos.size()>0){
								for(LrbVO[] tempvos:lrbvos){
									map.put("利润表_"+tempvos[0].getPeriod(), tempvos);
								}
							}
						}
					}else if ("发生额及余额表".equals(str)) {
						Object[] objtemp  = (Object[]) deepClone(obj);
//						IFsYeReport gl_rep_fsyebserv = (IFsYeReport) SpringUtils.getBean("gl_rep_fsyebserv");
						handleKmmx(objtemp,begperiod);
						QueryParamVO fspparamvo = (QueryParamVO) deepClone(paramvo);
						fsejyevos = zxkjReportService.getFsJyeVOs(fspparamvo, objtemp);
						if(fsejyevos!=null && fsejyevos.length>0){
							DZFDouble qcjfhj = DZFDouble.ZERO_DBL;
							DZFDouble qcdfhj = DZFDouble.ZERO_DBL;
							DZFDouble fsjfhj = DZFDouble.ZERO_DBL;
							DZFDouble fsdfhj = DZFDouble.ZERO_DBL;
							DZFDouble jftotalhj = DZFDouble.ZERO_DBL;
							DZFDouble dftotalhj =DZFDouble.ZERO_DBL;
							DZFDouble qmjfhj = DZFDouble.ZERO_DBL;
							DZFDouble qmdfhj = DZFDouble.ZERO_DBL;
							FseJyeVO totalvo = new FseJyeVO();
							for (FseJyeVO fsevo : fsejyevos) {
								if ("0".equals(fsevo.getKmlb())) {
									fsevo.setKmlb("资产");
								} else if ("1".equals(fsevo.getKmlb())) {
									fsevo.setKmlb("负债");
								} else if ("2".equals(fsevo.getKmlb())) {
									fsevo.setKmlb("共同");
								} else if ("3".equals(fsevo.getKmlb())) {
									fsevo.setKmlb("所有者权益");
								} else if ("4".equals(fsevo.getKmlb())) {
									fsevo.setKmlb("成本");
								} else if ("5".equals(fsevo.getKmlb())) {
									fsevo.setKmlb("损益");
								} else {
									fsevo.setKmlb("合计");
								}
								if(fsevo.getAlevel() == 1){
									qcjfhj = SafeCompute.add(qcjfhj, fsevo.getQcjf());
									qcdfhj = SafeCompute.add(qcdfhj, fsevo.getQcdf());
									fsjfhj = SafeCompute.add(fsjfhj, fsevo.getFsjf());
									fsdfhj = SafeCompute.add(fsdfhj, fsevo.getFsdf());
									jftotalhj = SafeCompute.add(jftotalhj, fsevo.getJftotal());
									dftotalhj = SafeCompute.add(dftotalhj, fsevo.getDftotal());
									qmjfhj = SafeCompute.add(qmjfhj, fsevo.getQmjf());
									qmdfhj = SafeCompute.add(qmdfhj, fsevo.getQmdf());
								}
							}
							totalvo.setKmlb("合计:");
							totalvo.setQcjf(qcjfhj);
							totalvo.setQcdf(qcdfhj);
							totalvo.setFsjf(fsjfhj);
							totalvo.setFsdf(fsdfhj);
							totalvo.setJftotal(jftotalhj);
							totalvo.setDftotal(dftotalhj);
							totalvo.setQmjf(qmjfhj);
							totalvo.setQmdf(qmdfhj);
							FseJyeVO[] totalvos = new FseJyeVO[fsejyevos.length+1];
							System.arraycopy(fsejyevos, 0, totalvos, 0, fsejyevos.length);  
							totalvos[fsejyevos.length] = totalvo;
							map.put("发生额及余额表_"+vprintperiod, totalvos);
						}
					}else if("总账明细账封皮".equals(str)){
						map.put("总账明细账封皮_"+vprintperiod, null);
					} else if("凭证封皮".equals(str)){
						map.put("凭证封皮_"+vprintperiod, null);
					}
				} catch (Exception e) {
					if(e instanceof BusinessException){
						log.error(e.getMessage(),e);
					}else {
						throw new WiseRunException(e); 
					}
				} 
			}
		}
	}
	
	//无余额无发生不显示
	private void handleKmmx(Object[] obj,String begqj) {
		List[] liststemp = (List[]) obj[0];
		List<KmMxZVO> mxlists = liststemp[0];
		KmMxZVO[] kmmxvos = mxlists.toArray(new KmMxZVO[0]);
		List<KmMxZVO> result = new ArrayList<KmMxZVO>();

		Map<String,DZFBoolean> periodvalue = new HashMap<String,DZFBoolean>();
		Map<String,String> qcperiodvalue = new HashMap<String,String>();//期初期间
		
		String period = "";
		String key = "";
		for(KmMxZVO vo:kmmxvos){
			period = vo.getRq().substring(0, 7);
			if(period.compareTo(begqj)<0){//小于当前期间则不显示
				continue;
			}
			key = vo.getKmbm();
			if(StringUtil.isEmpty(vo.getZy()) || (!(vo.getZy().equals("本月合计") && ReportUtil.bSysZy(vo)))){
				continue;
			}
			
			if (VoUtils.getDZFDouble(vo.getYe()).doubleValue() == 0
					&& VoUtils.getDZFDouble(vo.getJf()).doubleValue() == 0
					&& VoUtils.getDZFDouble(vo.getDf()).doubleValue() == 0) {
				periodvalue.put(key + period, DZFBoolean.TRUE);
			} else {
				periodvalue.put(key + period, DZFBoolean.FALSE);
				if (!qcperiodvalue.containsKey(key)) {
					qcperiodvalue.put(key, DateUtils.getPeriodStartDate(vo.getRq().substring(0, 7)).toString());
				}
			}
		}
		
		for(KmMxZVO vo:kmmxvos){
			period = vo.getRq().substring(0, 7);
			key = vo.getKmbm();
			DZFBoolean bxs = periodvalue.get(key+period);
			String qcperiod = qcperiodvalue.get(key);
			qcperiodvalue.remove(key);
			if(bxs!=null &&  !bxs.booleanValue()){
				if(!StringUtil.isEmpty(qcperiod) && ReportUtil.bSysZy(vo) && "期初余额".equals(vo.getZy())){
					vo.setRq(qcperiod.substring(0,7));
				} 
				result.add(vo);
			}
		}
		((List[]) obj[0])[0] = result;
	}

	public Object deepClone(Object objs) {
		ByteArrayOutputStream bo = null;
		ObjectOutputStream oo = null;
		ByteArrayInputStream bi =  null;
		ObjectInputStream oi = null;
		// 将对象写到流里
		try {
			bo = new ByteArrayOutputStream();
			oo = new ObjectOutputStream(bo);
			oo.flush();
			oo.writeObject(objs);
			// 从流里读出来
			bi = new ByteArrayInputStream(bo.toByteArray());
			oi = new ObjectInputStream(bi);
			return (oi.readObject());
		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
		}finally{
			if(bi!=null){
				try {
					bi.close();
				} catch (IOException e) {
				}
			}
			if(oi!=null){
				try {
					oi.close();
				} catch (IOException e) {
				}
			}
			
			//输出流
			if(bo!=null){
				try {
					bo.close();
				} catch (IOException e) {
					
				}
			}
			if(oo!=null){
				try {
					oo.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}  

	@Override
	public Object[] downReport(String pk_corp,String id) throws DZFWarpException {
		
		if(StringUtil.isEmpty(id)){
			throw new BusinessException("信息不能为空");
		}
		BatchPrintSetVo vo = (BatchPrintSetVo) singleObjectBO.queryByPrimaryKey(BatchPrintSetVo.class, id);
		
		if(vo == null){
			throw new BusinessException("数据不存在!");
		}
		
		if(StringUtil.isEmpty(vo.getVfilepath())){
			throw new BusinessException("文件不存在!");
		}
		
		try {
			Object[] objs = new Object[2];
			
			byte[] bytes = ((FastDfsUtil)SpringUtils.getBean("connectionPool")).downFile(vo.getVfilepath().substring(1));
			
			objs[0] =  bytes;
			
			objs[1] = vo.getVfilename();
			
			vo.setIfilestatue(PrintStatusEnum.LOADED.getCode());
			
			singleObjectBO.update(vo, new String[]{"ifilestatue"});
			
			updateBanding(vo);//更新装订信息
			
			return objs;
		} catch (AppException e) {
			throw new BusinessException("获取文件失败!");
		}
		
		
	}

	private void updateBanding(BatchPrintSetVo vo) {
		CorpVO cpvo = zxkjPlatformService.queryCorpByPk(vo.getPk_corp());
		//装订vo更新
		DZFBoolean isvouchprint = DZFBoolean.FALSE;//凭证
		DZFBoolean iskmprint = DZFBoolean.FALSE;//科目
		DZFBoolean isreportprint = DZFBoolean.FALSE;//财务账表
		
		if(!StringUtil.isEmpty(vo.getVprintname())){
			if(vo.getVprintname().indexOf("凭证")>-1){
				isvouchprint = DZFBoolean.TRUE;
			}
			if(vo.getVprintname().indexOf("科目总账")>-1
					|| vo.getVprintname().indexOf("科目明细账")>-1
					|| vo.getVprintname().indexOf("现金/银行日记账")>-1
					|| vo.getVprintname().indexOf("发生额及余额表")>-1){
				iskmprint = DZFBoolean.TRUE;
			}
			if(vo.getVprintname().indexOf("资产负债表")>-1
					|| vo.getVprintname().indexOf("利润表")>-1){
				isreportprint = DZFBoolean.TRUE;
			}
		}
		String period = vo.getVprintperiod();
		DZFDate begdate = DateUtils.getPeriodStartDate(period.split("~")[0]);
		if(begdate.before(cpvo.getBegindate())){//从建账日期取
			begdate = cpvo.getBegindate();
		}
		DZFDate enddate = DateUtils.getPeriodEndDate(period.split("~")[1]);
		List<String> periods =  ReportUtil.getPeriods(begdate, enddate);
		Map<String, BandingVO> map = getBandingMap(vo, periods);//获取vo
		BandingVO badingvo = null;
		for(String p:periods){
			badingvo = map.get(p);
			if(badingvo==null){
				badingvo = new BandingVO();
				badingvo.setPeriod(p);
				badingvo.setPk_corp(vo.getPk_corp());
			}
			if(isvouchprint.booleanValue() || iskmprint.booleanValue() || isreportprint.booleanValue()){
				badingvo.setBstatus(1);
			}else{
				badingvo.setBstatus(2);
			}
			badingvo.setIsvouchprint(isvouchprint);
			badingvo.setIskmprint(iskmprint);
			badingvo.setIsreportprint(isreportprint);
			if(StringUtil.isEmpty(badingvo.getPrimaryKey())){
				singleObjectBO.saveObject(vo.getPk_corp(), badingvo);
			}else{
				singleObjectBO.update(badingvo);
			}
		}
	}

	private Map<String, BandingVO> getBandingMap(BatchPrintSetVo vo, List<String> periods) {
		Map<String,BandingVO> map = new HashMap<String,BandingVO>();
		String wherepart = SqlUtil.buildSqlForIn("period", periods.toArray(new String[0]));
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_corp());
		BandingVO[] vos =  (BandingVO[]) singleObjectBO.queryByCondition(BandingVO.class, " nvl(dr,0)=0 and pk_corp = ?  and "+wherepart, sp );
		
		if(vos!=null && vos.length>0){
			for(BandingVO tvo:vos){
				map.put(tvo.getPeriod(), tvo);
			}
		}
		return map;
	}

	@Override
	public Object[] downReports(String pk_corp, String[] ids) throws DZFWarpException {
		
		if(ids == null || ids.length == 0){
			throw new BusinessException("信息不能为空");
		}
		String wherepart = SqlUtil.buildSqlForIn("pk_batch_print_set", ids);
		BatchPrintSetVo[] setvos =  (BatchPrintSetVo[]) singleObjectBO.queryByCondition(BatchPrintSetVo.class, "nvl(dr,0)=0 and ifilestatue in(1,2) and "+wherepart, new SQLParameter());
		
		if(setvos == null || setvos.length == 0){
			throw new BusinessException("暂无可下载的文件");
		}
		
		ByteArrayOutputStream zipbyte = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(zipbyte);
		try {
			Object[] objs = new Object[2];
			
			for(BatchPrintSetVo vo :setvos){
				if(StringUtil.isEmpty(vo.getVfilepath())){
					continue;
				}
				
				byte[] bytes = ((FastDfsUtil)SpringUtils.getBean("connectionPool")).downFile(vo.getVfilepath().substring(1));
				
				zos.putNextEntry(new ZipEntry(vo.getVfilename()));
				
				zos.write(bytes);
				
				vo.setIfilestatue(PrintStatusEnum.LOADED.getCode());
				
				singleObjectBO.update(vo, new String[]{"ifilestatue"});
				
				updateBanding(vo);//更新装订信息
			}
			
			zos.close();
			objs[0] = zipbyte.toByteArray();
			objs[1] = new DZFDate().toString()+".zip";
			return objs;
		} catch (AppException e) {
			throw new BusinessException("获取文件失败!");
		} catch (IOException e) {
			throw new BusinessException("获取文件失败!");
		}finally {
			if(zos !=null){
				try {
					zos.close();
				} catch (IOException e) {
				}
			}
			
		}
	}

	@Override
	public List<BatchPrintSetVo> queryPrintVOs(String parentcorpid, String corpname, String corpcode, DZFDate begdate,
			DZFDate enddate, String qry_zt, String cuserid) throws DZFWarpException {
		if (StringUtil.isEmpty(parentcorpid)) {
			throw new BusinessException("公司信息不能为空!");
		}

		SQLParameter sp = new SQLParameter();

		CorpVO corp = zxkjPlatformService.queryCorpByPk(parentcorpid);
		StringBuffer qrysql = new StringBuffer();
		
		if(DZFBoolean.TRUE.equals(corp.getIsfactory())){
			qrysql.append(" select t.*,c.unitname as cname ");
			qrysql.append(" from " + BatchPrintSetVo.TABLE_NAME + " t ");
			qrysql.append(" inner join bd_corp c on t.pk_corp = c.pk_corp ");
			qrysql.append(" where nvl(t.dr,0)=0");
			qrysql.append(" and c.pk_corp in (select PK_CUSTOMER from fct_busiapply_b where nvl(dr,0)=0 and isconfirm='Y'   and vstatus=3 and pk_busiapply in "
					+ "( select pk_busiapply from fct_busiapply where PK_FACTORY= ? )) ");
			sp.addParam(parentcorpid);
			if (begdate != null) {
				qrysql.append(" and t.doperadatetime >= ?  ");
				sp.addParam(begdate.toString());
			}
			if (enddate != null) {
				qrysql.append("  and t.doperadatetime <= ?  ");
				sp.addParam(enddate.toString());
			}
			
			if(!StringUtil.isEmpty(qry_zt)){
				qrysql.append(" and t.ifilestatue = ? ");
				sp.addParam(qry_zt);
			}
			qrysql.append(" order by t.doperadatetime desc , c.innercode");
		}else{
			qrysql.append(" select t.*,c.unitname as cname ");
			qrysql.append(" from " + BatchPrintSetVo.TABLE_NAME + " t ");
			qrysql.append(" inner join bd_corp c on t.pk_corp = c.pk_corp ");
			qrysql.append(" where nvl(t.dr,0)=0");
			qrysql.append("  and c.fathercorp = ?  ");
			sp.addParam(parentcorpid);
			if (begdate != null) {
				qrysql.append(" and t.doperadatetime >= ?  ");
				sp.addParam(begdate.toString());
			}
			if (enddate != null) {
				qrysql.append("  and t.doperadatetime <= ?  ");
				sp.addParam(enddate.toString());
			}
			
			if(!StringUtil.isEmpty(qry_zt)){
				qrysql.append(" and t.ifilestatue = ? ");
				sp.addParam(qry_zt);
			}
			if(!StringUtil.isEmpty(cuserid)){
				qrysql.append(" and c.pk_corp in (select distinct pk_corp from sm_user_role where cuserid = ?  and nvl(dr,0) =0 ) ");
				sp.addParam(cuserid);
			}
			qrysql.append(" order by t.doperadatetime desc , c.innercode ");
		}
		
		List<BatchPrintSetVo> printsetvos = (List<BatchPrintSetVo>) singleObjectBO.executeQuery(qrysql.toString(), sp,
				new BeanListProcessor(BatchPrintSetVo.class));

		QueryDeCodeUtils.decKeyUtils(new String[]{"cname"}, printsetvos, 1);
		
		return printsetvos;
	}

}
