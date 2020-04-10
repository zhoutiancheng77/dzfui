package com.dzf.zxkj.platform.service.zncs.impl;


import com.dzf.account.api.model.icbc.gyj.IcbcErcptApplyAndQrywlhdetailQo;
import com.dzf.account.api.model.icbc.gyj.IcbcErcptApplyAndQrywlhdetailVo;
import com.dzf.account.api.result.Result;
import com.dzf.account.api.service.IIcbcWlhService;
import com.dzf.cloud.redis.lock.RedissonDistributedLock;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.framework.util.SQLHelper;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.FieldMapping;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.IBillManageConstants;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.constant.IVoucherConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pjgl.BankBillToStatementVO;
import com.dzf.zxkj.platform.model.pjgl.VatInvoiceSetVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.*;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.bdset.IYHZHService;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IDcpzService;
import com.dzf.zxkj.platform.service.zncs.*;
import com.dzf.zxkj.platform.util.BeanUtils;
import com.dzf.zxkj.platform.util.ImageCopyUtil;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.dzf.zxkj.platform.util.zncs.OcrUtil;
import com.dzf.zxkj.platform.util.zncs.ZncsConst;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service("gl_yhdzdserv2")
public class  BankStatement2ServiceImpl implements IBankStatement2Service {
	

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private YntBoPubUtil yntBoPubUtil;
	@Autowired
	private IYHZHService gl_yhzhserv;
	@Autowired
	private IVoucherService voucher;
	@Autowired
	private IZncsVoucher zncsVoucher;
	@Autowired
	private ICpaccountService cpaccountService;
	@Autowired
	private IQmgzService qmgzService;
	@Autowired
	private IDcpzService dcpzjmbserv;
	@Autowired
	private IYHZHService yhzhserv;
	@Autowired
	private IImageGroupService img_groupserv;
	@Autowired
	private IQmclService gl_qmclserv;
	@Autowired
	private ISchedulCategoryService schedulCategoryService;
	@Autowired
	private IBillcategory iBillcategory;
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;

	private Map<Integer, Object[][]>  STYLE = null;
	@Autowired
	private IVATInComInvoice2Service gl_vatincinvact2;
	@Autowired
	private IAccountService accountService;
	@Autowired
	private ICorpService corpService;
	@Autowired
	private RedissonDistributedLock redissonDistributedLock;
	@Reference(version = "1.0.1", protocol = "dubbo", timeout = 30000, retries = 0)
	private IIcbcWlhService iIcbcWlhService;

	public List<BankStatementVO2> quyerByPkcorp(String pk_corp, BankStatementVO2 vo, String sort, String order) throws DZFWarpException{
//		try {
			SQLParameter sp = new SQLParameter();
			StringBuffer sb = new StringBuffer();
			sb.append(" select y.pk_bankstatement,y.coperatorid,y.doperatedate,y.pk_corp,y.batchflag,y.pk_bankaccount,y.tradingdate, y.inperiod, ");
			sb.append("        y.zy,nvl(d.pk_category,e.pk_model_h) as pk_model_h,nvl(y.busitypetempname,e.busitypetempname) as busitypetempname,y.syje,y.zcje,y.othaccountname,y.othaccountcode,y.ye,y.pk_tzpz_h,y.pk_category_keyword, ");
			sb.append("        y.sourcetem,y.sourcetype,y.period,y.billstatus,y.modifyoperid,y.modifydatetime,y.dr,y.ts, h.pzh, y.imgpath, y.sourcebillid , ");
			if(!StringUtils.isEmpty(vo.getPk_bankaccount())){
				sb.append("  yb.bankname as bankname ,yb.bankaccount as bankaccount ");
			}else{
				sb.append("  yb.accountname as bankname ,yb.myaccountcode as bankaccount ");
			}
			sb.append(" from ynt_bankstatement y ");
			sb.append(" left join ynt_tzpz_h h ");
			sb.append("   on y.pk_tzpz_h = h.pk_tzpz_h ");
			sb.append("   left join ynt_billcategory d ");
			sb.append("     on y.pk_model_h = d.pk_category ");
			sb.append(" left join ynt_dcmodel_h e on y.pk_model_h = e.pk_model_h ");
			if(!StringUtils.isEmpty(vo.getPk_bankaccount())){
				sb.append("left join ynt_bankaccount yb on y.pk_bankaccount=yb.pk_bankaccount");
			}else{
				sb.append(" left join ynt_bankbilltostatement yb on y.pk_bankstatement=yb.pk_bankstatement ");
			}
			sb.append(" where y.pk_corp=? and nvl(y.dr,0)=0 and nvl(yb.dr,0)=0 ");
			sp.addParam(pk_corp);
			
//			if(!StringUtil.isEmpty(vo.getPeriod())){
//				sb.append(" and y.period = ? ");
//				sp.addParam(vo.getPeriod());
//			}
			if(!StringUtil.isEmpty(vo.getSerdate())&&vo.getSerdate().equals("serDay")){//选择交易日期
				if(!StringUtil.isEmpty(vo.getStartyear1())
						&& !StringUtil.isEmpty(vo.getStartmonth1())
						&& !StringUtil.isEmpty(vo.getEndyear1())
						&& !StringUtil.isEmpty(vo.getEndmonth1())){
					String startPeriod = vo.getStartyear1() + "-" + vo.getStartmonth1();
					String endPeriod = vo.getEndyear1() + "-" + vo.getEndmonth1();
					DZFDate startDate = DateUtils.getPeriodStartDate(startPeriod);
					DZFDate endDate = DateUtils.getPeriodEndDate(endPeriod);
					
					sb.append(" and y.tradingdate >= ? ");
					sb.append(" and y.tradingdate <= ? ");
					sp.addParam(startDate);
					sp.addParam(endDate);
					
				}
			}else{
				if(!StringUtil.isEmpty(vo.getStartyear2())
						&& !StringUtil.isEmpty(vo.getStartmonth2())
						&& !StringUtil.isEmpty(vo.getEndyear2())
						&& !StringUtil.isEmpty(vo.getEndmonth2())){
					String startPeriod = vo.getStartyear2() + "-" + vo.getStartmonth2();
					String endPeriod = vo.getEndyear2() + "-" + vo.getEndmonth2();
					
					sb.append(" and y.inperiod >= ? ");
					sb.append(" and y.inperiod <= ? ");
					sp.addParam(startPeriod);
					sp.addParam(endPeriod);
					
				}
			}
			
			if(!StringUtil.isEmpty(vo.getIspz())){
				if("Y".equals(vo.getIspz())){
					sb.append(" and y.pk_tzpz_h is not null ");
				}else{
					sb.append(" and y.pk_tzpz_h is null ");
				}
			}
			
			if(!StringUtil.isEmpty(vo.getZy())){
				sb.append(" and y.zy like ? ");
				sp.addParam("%" + vo.getZy() + "%");
			}
			if(!StringUtil.isEmpty(vo.getBusitypetempname())){
				sb.append(" and y.busitypetempname like ? ");
				sp.addParam("%" + vo.getBusitypetempname() + "%");
			}
			if(!StringUtil.isEmpty(vo.getOthaccountname())){
				sb.append(" and y.othaccountname like ? ");
				sp.addParam("%" + vo.getOthaccountname() + "%");
			}
			
			if(StringUtil.isEmpty(vo.getPk_bankaccount())){
				sb.append(" and y.pk_bankaccount is null ");
			}else{
				sb.append(" and y.pk_bankaccount = ? ");
				sp.addParam(vo.getPk_bankaccount());
			}
			if(!StringUtils.isEmpty(vo.getInoutflag())){
				sb.append(" and d.inoutflag = ? ");
				sp.addParam(vo.getInoutflag());
			}
			if(!StringUtil.isEmpty(sort)){//sort != null && !"".equals(sort)
				String sortb = FieldMapping.getFieldNameByAlias(new BankStatementVO2(), sort);
				order = " order by " + (sortb == null ? sort : sortb) + " " +order + ", y.pk_bankstatement " + order;
				sb.append(order);
			}
			List<BankStatementVO2> listVo = (List<BankStatementVO2>) singleObjectBO.executeQuery(sb.toString(), 
					sp, new BeanListProcessor(BankStatementVO2.class));
			return listVo;

	}
//	@Override
//	public BankStatementVO2 saveNew(BankStatementVO2 vo) throws BusinessException {
//		BankStatementVO2 tvo = new BankStatementVO2();
//		BeanUtils.copyProperties(vo, tvo);
//		try {
//			if(StringUtil.isEmpty(tvo.getPk_corp())){
//				tvo.setPk_corp("109X");
//			}
//			
//			return (BankStatementVO2)singleObjectBO.saveObject(tvo.getPk_corp(), tvo);
//		} catch (BusinessException e) {
//			//e.printStackTrace();
//			throw new RuntimeException();
//		}
//	};
//	@Override
//	public void update(BankStatementVO2 vo) throws BusinessException {
//		BankStatementVO2 tvo = new BankStatementVO2();
//		BeanUtils.copyProperties(vo, tvo);
//		try {
//			singleObjectBO.saveObject(tvo.getPk_corp(), tvo);
//		} catch (BusinessException e) {
//			//e.printStackTrace();
//			throw new RuntimeException();
//		}
//	}
//	此方法未使用
//	public void saveVOArr(BankStatementVO2[] vos) throws DZFWarpException{
//		String s = vos[0].getPrimaryKey();
//		List<BankStatementVO2> list = quyerByPkcorp(vos[0].getPk_corp(),null,null);
//		BankStatementVO2[] dbvos = (BankStatementVO2[]) list.toArray();
//		if(s != null && !s.equals("")){
//			HashMap<String,BankStatementVO2> pom= new HashMap<String,BankStatementVO2>();
//			HashMap<String,BankStatementVO2> com= new HashMap<String,BankStatementVO2>();
//			HashMap<String,BankStatementVO2> nom= new HashMap<String,BankStatementVO2>();
//			for(BankStatementVO2 msvo:vos){
//				pom.put(msvo.getPrimaryKey(), msvo);
//			}
//			for(BankStatementVO2 msvo:dbvos){
//				if(pom.containsKey(msvo.getPrimaryKey())){
//					msvo = pom.get(msvo.getPrimaryKey());
//				}
//				com.put(msvo.getBusitypename(),msvo);
//				nom.put(msvo.getPk_busitype(),msvo);
//			}
//			for(BankStatementVO2 msvo:vos){
//				if(com.containsKey(msvo.getBusitypename())){
//					throw new BusinessException("编码不允许重复！");
//				}
//				if(nom.containsKey(msvo.getBusitypename())){
//					throw new BusinessException("名称不允许重复！");
//				}
//			}
//			singleObjectBO.updateAry(vos);
//		}else{
//			for(BankStatementVO2 msvo:vos){
//				if(!isExist(msvo).booleanValue()){
//					throw new BusinessException("编码和名称不允许重复！"); 
//				}
//				for(BankStatementVO2 msvo1:vos){
//					if(msvo.getCode().equals(msvo1.getCode())||msvo.getName().equals(msvo1.getName())){
//						if(msvo != msvo1)
//						throw new BusinessException("编码和名称不允许重复！");
//					}
//				}
//			}
//			singleObjectBO.insertVOArr(vos[0].getPk_corp(), vos);
//		}
//	}
	
	@Override
	public void delete(BankStatementVO2 vo, String pk_corp) throws DZFWarpException {
		//删除前数据安全验证
		BankStatementVO2 getmsvo = queryByPrimaryKey(vo.getPrimaryKey());
		if(getmsvo == null 
				|| !getmsvo.getPk_corp().equals(getmsvo.getPk_corp()) 
				|| vo.getPk_corp() == null
				|| !pk_corp.equals(vo.getPk_corp())){
			throw new BusinessException("正在处理中，请刷新重试！");
		}
		
		if(!StringUtils.isEmpty(getmsvo.getImgpath())){
			throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
		}
		
		if(!StringUtil.isEmpty(vo.getPzh())
				|| !StringUtil.isEmpty(vo.getPk_tzpz_h()))
			throw new BusinessException("单据已生成凭证，请检查。");
		
		singleObjectBO.deleteObjectByID(vo.getPrimaryKey(), new Class[]{BankStatementVO2.class});
		
		//中间表级联删除操作
		if(StringUtil.isEmpty(vo.getSourcebillid())){
			String pk = vo.getPrimaryKey();
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(pk);
			singleObjectBO.executeUpdate(" update ynt_bankbilltostatement set dr = 1 where pk_corp = ? and pk_bankstatement = ?  ", sp);
		}
	}
	
/*	@Override
	public DZFBoolean checkBeforeSaveNew(SuperVO vo)throws  BusinessException {
		if(isExist((BankStatementVO2)vo).booleanValue()){
			return DZFBoolean.TRUE;
		}else{
			throw new BusinessException("编码和名称不允许重复");
//			return  DZFBoolean.FALSE;
		}
	}*/
	
	
/*	@Override
	public DZFBoolean checkBeforeUpdata(SuperVO vo) throws BusinessException{
		
		BankStatementVO2 oldvo = (BankStatementVO2)getSingleObjectBO().queryVOByID(vo.getPrimaryKey(), BankStatementVO2.class);
		if(oldvo==null){
			throw new BusinessException("非法数据，请刷新后重新修改");
//			return  DZFBoolean.FALSE;
		}
		if(isExist((BankStatementVO2)vo).booleanValue()){
			return DZFBoolean.TRUE;
		}else{
			throw new BusinessException("编码和名称不允许重复");
//			return  DZFBoolean.FALSE;
		}
	}*/
	
	public BankStatementVO2 queryByPrimaryKey(String PrimaryKey) throws DZFWarpException{
		SuperVO vo = singleObjectBO.queryByPrimaryKey(BankStatementVO2.class, PrimaryKey);
		return (BankStatementVO2) vo;
	}
	
//	private DZFBoolean isExist(BankStatementVO2 vo) throws DZFWarpException{
//		
////		String sql = new String("SELECT 1 FROM YNT_MEASURE WHERE (CODE = ? OR NAME = ? )AND PK_CORP = ? and nvl(dr,0)=0 ");
//		StringBuffer sf = new StringBuffer();
//		sf.append("SELECT 1 FROM YNT_MEASURE WHERE (CODE = ? OR NAME = ? )AND PK_CORP = ? and nvl(dr,0)=0 ");
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(vo.getCode());
//		sp.addParam(vo.getName());
//		sp.addParam(vo.getPk_corp());
//		
//		if(!StringUtil.isEmpty(vo.getPrimaryKey())){//vo.getPrimaryKey()!=null&&vo.getPrimaryKey().length()>0
////			sql=sql+" AND PK_MEASURE !=?";
//			sf.append(" AND PK_MEASURE !=? ");
//			sp.addParam(vo.getPrimaryKey());
//		}
//		
//		Object i = getSingleObjectBO().executeQuery(sf.toString(), sp, new ColumnProcessor());
//		
//		if(i!=null){
//			return DZFBoolean.FALSE;
//		}
//		return DZFBoolean.TRUE;
//	}
	
//	public String isExistPk(BankStatementVO2 vo) throws DZFWarpException{
//		
////		String sql = new String("SELECT PK_MEASURE FROM YNT_MEASURE WHERE (CODE = ? OR NAME = ? )AND PK_CORP = ? and nvl(dr,0)=0 ");
//		StringBuffer sf = new StringBuffer();
//		sf.append("SELECT PK_MEASURE FROM YNT_MEASURE WHERE (CODE = ? OR NAME = ? )AND PK_CORP = ? and nvl(dr,0)=0 ");
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(vo.getCode());
//		sp.addParam(vo.getName());
//		sp.addParam(vo.getPk_corp());
//		
//		if(!StringUtil.isEmpty(vo.getPrimaryKey())){
////			sql=sql+" AND PK_MEASURE !=?";
//			sf.append(" AND PK_MEASURE !=? ");
//			sp.addParam(vo.getPrimaryKey());
//		}
//		
////		String[] pks = (String[]) getSingleObjectBO().executeQuery(sql, sp, new ColumnProcessor());
//		Object obj = singleObjectBO.executeQuery(sf.toString(), sp, new ColumnProcessor());
//		
//		if(obj != null && !"".equals(obj)){
//			return (String) obj;
//		}
//		return null;
//	}
	@Override
	public void updateVOArr(DZFBoolean isAddNew, String pk_corp, String cuserid, String sort, String order, List<BankStatementVO2> list) throws DZFWarpException {
		List<BankStatementVO2> listNew = new ArrayList<BankStatementVO2>();
		List<BankStatementVO2> listAll = quyerByPkcorp(pk_corp, new BankStatementVO2(), sort, order);
		String k = null;
		BankStatementVO2 voTemp = null;
		if(list == null || list.size()==0)
			return;
		
		for(BankStatementVO2 vo : list){
			k = vo.getPrimaryKey();
			if(StringUtil.isEmpty(k)){
				vo.setPk_corp(pk_corp);
				vo.setCoperatorid(cuserid);
				vo.setDoperatedate(new DZFDate(new Date()));
				listNew.add(vo);
				listAll.add(vo);
			}else if(!isAddNew.booleanValue()){
				voTemp = null;
				for(BankStatementVO2 msvo:listAll){
					if(msvo.getPrimaryKey().equals(k)){
						voTemp = msvo;
						break;
					}
				}
				if(voTemp != null){
					listAll.remove(voTemp);
					listAll.add(vo);
				}
			}
		}
		
		if( listAll != null && listAll.size() > 0 ){
			Collections.sort(listAll, new Comparator<BankStatementVO2>() {
				public int compare(BankStatementVO2 o1,BankStatementVO2 o2){
					if(SafeCompute.sub(o1.getYe(), o2.getYe()).compareTo(DZFDouble.ZERO_DBL) == 0){
						throw new BusinessException("余额不可重复！");
					}
					return SafeCompute.sub(o1.getYe(), o2.getYe()).compareTo(DZFDouble.ZERO_DBL) > 0 ?
							1 : -1;
				}
			});
//			Collections.sort(listAll, new Comparator<BankStatementVO2>() {
//				public int compare(BankStatementVO2 o1,BankStatementVO2 o2){
//					if(o1.getCode().equals(o2.getCode())){
//						throw new BusinessException("编码不可重复！");
//					}
//					return o1.getCode().compareTo(o2.getCode());
//				}
//			});
//			Collections.sort(listAll, new Comparator<BankStatementVO2>() {
//				public int compare(BankStatementVO2 o1,BankStatementVO2 o2){
//					if(o1.getName().equals(o2.getName())){
//						throw new BusinessException("名称不可重复！");
//					}
//					return o1.getName().compareTo(o2.getName());
//				}
//			});
		}
		
		if (listNew != null && listNew.size() > 0){
			BankStatementVO2[] vos = listNew.toArray(new BankStatementVO2[0]);
			for(BankStatementVO2 msvo:vos){
				if(msvo == null){
					throw new BusinessException("数据为空不能添加");
				}
				msvo.setVersion(new DZFDouble(1.0));
//				msvo.setPk_corp(pk_corp);
//				msvo.setCreatetime(new DZFDateTime(new Date()));
//				msvo.setCreator(cuserid);
			}
			singleObjectBO.insertVOArr(vos[0].getPk_corp(), vos);
		}else if (list != null && list.size() > 0) {
			BankStatementVO2[] vos = list.toArray(new BankStatementVO2[0]);
			for (int i = 0; i < vos.length; i++) {
				BankStatementVO2 bankStatementVO2 = vos[i];
				bankStatementVO2.setVersion(new DZFDouble(1.0));
			}
			singleObjectBO.updateAry(vos);
		}
	}
	@Override
	public String saveImp(MultipartFile file, BankStatementVO2 paramvo, String fileType, int sourceType) throws DZFWarpException {
		InputStream is = null;
		
		ByteArrayOutputStream baos = null;
		ByteArrayInputStream bais = null;
		try {
			is = file.getInputStream();
			
			baos = new ByteArrayOutputStream();
			
			byte[] buffer = new byte[1444]; 

			int byteread = 0; 
			int bytesum = 0;
			while ((byteread = is.read(buffer)) != -1) { 
				bytesum += byteread; //字节数 文件大小 

				baos.write(buffer, 0, byteread); 
				baos.flush();
			} 
			byte[] bytes = baos.toByteArray();
			
			bais = new ByteArrayInputStream(bytes);
			
			Workbook impBook = null;
			if("xls".equals(fileType)) {
				try {
					impBook = new HSSFWorkbook(bais);
				}
				catch (Exception ex)
				{
					impBook = new HSSFWorkbook(new ByteArrayInputStream(translateHTML2XLS(bytes)));
				}
			} else if ("xlsx".equals(fileType)) {
				try {
					impBook = new XSSFWorkbook(bais);
				}
				catch (Exception ex)
				{
					impBook = new HSSFWorkbook(new ByteArrayInputStream(translateHTML2XLS(bytes)));
				}
			}
			else if ("csv".equals(fileType))
			{
				impBook = new HSSFWorkbook(new ByteArrayInputStream(translateHTML2XLS(bytes)));
			} else {
				throw new BusinessException("不支持的文件格式");
			}
			
			int sheetno = impBook.getNumberOfSheets();
			if (sheetno == 0) {
				throw new Exception("需要导入的数据为空。");
			}
			
			Sheet sheet1 = impBook.getSheetAt(0);
			
			return doImport(file, sheet1, paramvo, fileType, sourceType);

		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new BusinessException("导入文件未找到");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new BusinessException("导入文件格式错误");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(e instanceof BusinessException){
				throw new BusinessException(e.getMessage(), ((BusinessException) e).getErrorCodeString());
			}
			throw new BusinessException("导入文件格式错误");
		}finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
			if (bais != null) {
				try {
					bais.close();
				} catch (IOException e) {
				}
			}
		}
	}
//	private byte[] translateCSV2XLS(byte[] bytes)
//	{
//		ByteArrayInputStream bais = null;
//		ByteArrayOutputStream baos = null;  
//		try {
//			bais = new ByteArrayInputStream(bytes);
//			CsvReader reader = new CsvReader(bais, Charset.defaultCharset());
//			
//			String[] headers = reader.getHeaders();
//			
//			String sheetName = "Sheet1";
//			baos = new ByteArrayOutputStream();  
//			//创建workbook  
//			HSSFWorkbook workbook = new HSSFWorkbook();  
//			//添加Worksheet（不添加sheet时生成的xls文件打开时会报错)  
//			workbook.createSheet(sheetName);    
//			//新建文件  
//			 //添加表头  
//			Row row = workbook.getSheet(sheetName).createRow(0);    //创建第一行  
//			if (headers != null)
//			{
//				for(int i = 0; i < headers.length; i++){  
//					Cell cell = row.createCell(i);  
//					cell.setCellValue(headers[i]);  
//				}
//			}
//			//添加表体
//			int iRow = 1;
//			while (reader.readRecord()) {
//				row = workbook.getSheet(sheetName).createRow(iRow++);
//				
//	             
//	            String[] sa = reader.getValues();
//	            if (sa != null)
//	            {
//		            for (int i = 0; i < sa.length; i++)
//		            {
//		            	Cell cell = row.createCell(i);  
//						cell.setCellValue(sa[i]);  
//		            }
//	            }
//	        }
//			workbook.write(baos);
//			baos.flush();
//			
////			FileOutputStream fos = new FileOutputStream(new File("test.xls"));
////			workbook.write(fos);
////			fos.flush();
////			fos.close();
//			
//			return baos.toByteArray();
//		}
//		catch (Exception ex)
//		{
//			log.error(ex.getMessage(), ex);
//		}
//		finally {
//			if (bais != null) {
//				try {
//					bais.close();
//				} catch (IOException e) {
//				}
//			}
//
//			if (baos != null) {
//				try {
//					baos.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//		return null;
//	}
	private byte[] translateHTML2XLS(byte[] bytes)
	{

		ByteArrayOutputStream baos = null;  
		try {
			
			String sheetName = "Sheet1";
			baos = new ByteArrayOutputStream();  
			//创建workbook  
			HSSFWorkbook workbook = new HSSFWorkbook();  
			//添加Worksheet（不添加sheet时生成的xls文件打开时会报错)  
			workbook.createSheet(sheetName);    
			//新建文件  
			 //添加表头  
			Row row = workbook.getSheet(sheetName).createRow(0);    //创建第一行  

			String str = new String(bytes, "utf-8");
			
			//解析表行
			int iRow = 1;
			String regex = "<tr>(.*?)</tr>";
        	Pattern p = Pattern.compile(regex);
    		Matcher m = p.matcher(str);
    		while(m.find()){

    			row = workbook.getSheet(sheetName).createRow(iRow++);
    		
    			String sRow = m.group(1);
    			String regex2 = ">([^<>]*?)(</td>|</th>)";
            	Pattern p2 = Pattern.compile(regex2);
        		Matcher m2 = p2.matcher(sRow);
        		int i = 0;
        		while(m2.find()){
        			Cell cell = row.createCell(i++);  
					cell.setCellValue(m2.group(1));  
            	}
    		}
			
			workbook.write(baos);
			baos.flush();
			
//			FileOutputStream fos = new FileOutputStream(new File("test.xls"));
//			workbook.write(fos);
//			fos.flush();
//			fos.close();
			
			return baos.toByteArray();
		}
		catch (Exception ex)
		{
			log.error(ex.getMessage(), ex);
		}
		finally {
			

			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
	private Map<String, BankAccountVO> hashliseYhzhMap(List<BankAccountVO> list){
		Map<String, BankAccountVO> map = new HashMap<String, BankAccountVO>();
		
		if(list != null && list.size() > 0){
			String key;
			for(BankAccountVO vo : list){
				key = vo.getBankaccount();
				if(!map.containsKey(key)){
					map.put(key, vo);
				}
			}
		}
		return map;
	}
	
	private String doImport(MultipartFile file, Sheet sheet, BankStatementVO2 paramvo, String fileType, int sourceType){
		StringBuffer msg = new StringBuffer();
//		BankAccountVO bankvo = getBankAccountVO(paramvo);
		List<BankAccountVO> yhzhList = yhzhserv.query(paramvo.getPk_corp(), "Y");//不包含停用
		Map<String, BankAccountVO> yhzhMap = hashliseYhzhMap(yhzhList);
		
		CorpVO corpvo = corpService.queryByPk(paramvo.getPk_corp());

		List<BankStatementVO2> list = null;
		if(sourceType == BankStatementVO2.SOURCE_1){
			try {
				list = getDataByExcelNew(file, sheet, sourceType, msg, yhzhMap, paramvo, corpvo);
			}
			catch (Exception e)
			{
				//不用处理异常，下面用通用银行模板再次尝试
				
			}
			if(list == null || list.size() == 0){
				//用银行通用模板再试一次。
				msg = new StringBuffer();
				sourceType = BankStatementVO2.SOURCE_999;
				list = getDataByExcel(file, sheet, sourceType, msg, yhzhMap, paramvo, corpvo);
				
			}
		}else{
			list = getDataByExcel(file, sheet, sourceType, msg, yhzhMap, paramvo, corpvo);
		}
		
		
		if(list == null || list.size() == 0){
			String frag = "<p>导入文件数据为空，请检查。</p>";
			
			if(msg.length() == 0){
				msg.append(frag);
			}
			
			throw new BusinessException(msg.toString());
		}
		try {
			list = beforeSave(list, paramvo, msg, sourceType, corpvo);
		}
		catch (BusinessException be)
		{
			if(sourceType == BankStatementVO2.SOURCE_1 ){	//原通用模板失败，用新银行通用模板再试一次。
				//用银行通用模板再试一次。
				msg = new StringBuffer();
				sourceType = BankStatementVO2.SOURCE_999;
				list = getDataByExcel(file, sheet, sourceType, msg, yhzhMap, paramvo, corpvo);
				list = beforeSave(list, paramvo, msg, sourceType, corpvo);
			}
			else
				throw be;
		}
		Map<String,String> map = new HashMap<String,String>();
		for (BankStatementVO2 vo : list) {
			//根据业务类型名称找业务类型主键
			if(!StringUtils.isEmpty(vo.getBusitypetempname())){
				String pk_category = map.get(vo.getBusitypetempname()+vo.getInperiod());
				if(StringUtils.isEmpty(pk_category)){
					List<BillCategoryVO> categoryList = queryCategoryList(vo.getInperiod(), corpvo.getPk_corp());
					if(categoryList==null||categoryList.size()==0){
						
						schedulCategoryService.newSaveCorpCategory(null, corpvo.getPk_corp(), vo.getInperiod(), corpvo);
					}
					pk_category = queryBillCategoryId(vo.getBusitypetempname(), paramvo.getPk_corp(), vo.getInperiod());
					map.put(vo.getBusitypetempname()+vo.getInperiod(), pk_category);
				}
				vo.setPk_model_h(pk_category);
		
			}
		}
		Map<String, BankStatementVO2[]> sendData = new HashMap<String, BankStatementVO2[]>();
		sendData.put("adddocvos", list.toArray(new BankStatementVO2[0]));
		DZFBoolean isFlag = paramvo.getIsFlag();
		BankStatement2ResponseVO responseVO = updateVOArr(paramvo.getPk_corp(), sendData, isFlag);
		
		setAfterImportPeriod(responseVO.getVos(), paramvo);
		
		if (msg.length() == 0)
		{
			msg.append(list != null && list.size() > 0 ? "成功导入 " + list.size() + " 行数据!" : "未读取到有效数据， 导入 0 行数据！");
		}
		return msg.toString();
	}
	private List<BillCategoryVO> queryCategoryList(String period,String pk_corp){
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append(" select * from ynt_billcategory where nvl(dr,0)=0 and period=? and pk_corp = ? ");
		sp.addParam(period);
		sp.addParam(pk_corp);
		List<BillCategoryVO> list = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(BillCategoryVO.class));
		return list;
	}
	private void setAfterImportPeriod(BankStatementVO2[] vos, BankStatementVO2 periodvo){
		if(vos != null && vos.length > 0){
			DZFDate minDate = null;
			DZFDate maxDate = null;
			DZFDate kprj = null;
			for(BankStatementVO2 vo : vos){
				
				kprj = vo.getTradingdate();
				
				if(minDate == null){
					minDate = kprj;
				}else if(minDate.after(kprj)){
					minDate = kprj;
				}
				
				if(maxDate == null){
					maxDate = kprj;
				}else if(kprj.after(maxDate)){
					maxDate = kprj;
				}
			}
			
			if(minDate != null && maxDate != null){
				String byear = minDate.getYear() + "";
				String bmonth = minDate.getMonth() > 9 ? minDate.getMonth() + ""  : "0" + minDate.getMonth();
				
				String eyear = maxDate.getYear() + "";
				String emonth= maxDate.getMonth() > 9 ? maxDate.getMonth() + "" : "0" + maxDate.getMonth();
				
				periodvo.setStartyear1(byear);
				periodvo.setStartmonth1(bmonth);
				periodvo.setEndyear1(eyear);
				periodvo.setEndmonth1(emonth);
			}
			
//			String firstPeriod = vos[0].getPeriod();
//			String secondPeriod= null;
//			for(int i = 1; i < vos.length; i++){
//				
//				secondPeriod = vos[i].getPeriod();
//				if(StringUtil.isEmpty(secondPeriod)){
//					continue;
//				}
//				
//				if(!StringUtil.isEmpty(firstPeriod)){
//					firstPeriod = firstPeriod.compareTo(secondPeriod) > 0 ? secondPeriod : firstPeriod;
//				}else{
//					firstPeriod = secondPeriod;
//				}
//			}
//			
//			periodvo.setPeriod(firstPeriod);
		}

	}
	
//	private BankAccountVO getBankAccountVO(BankStatementVO2 paramvo){
//		BankAccountVO vo = (BankAccountVO) singleObjectBO.queryVOByID(paramvo.getPk_bankaccount(), BankAccountVO.class);
//		
//		return vo == null ? new BankAccountVO() : vo;
//	}
	
	private List<BankStatementVO2> beforeSave(List<BankStatementVO2> list, 
			BankStatementVO2 paramvo, 
			StringBuffer msg ,
			int sourceType,
			CorpVO corpvo) throws DZFWarpException{
		List<BankStatementVO2> result = new ArrayList<BankStatementVO2>();
		
//		Map<String, DcModelHVO> dcmodelMap = null;
//		if(sourceType == BankStatementVO2.SOURCE_1){
		//	List<DcModelHVO> dcList = dcpzjmbserv.query(paramvo.getPk_corp());
		//	Map<String, DcModelHVO> dcmodelMap = new HashMap<String, DcModelHVO>();//关键字
		//	Map<String, DcModelHVO> dcmodelMap1 = new HashMap<String, DcModelHVO>();//业务类型名称
		//	filterDcModel(dcList, dcmodelMap, dcmodelMap1);
//		}

		for(BankStatementVO2 vo : list){
			if(checkValidData(vo, msg, corpvo)){
				setDefaultValue(vo, paramvo, sourceType);
				
				//通用模板，业务类型单独处理
//				specialBusiNameValue(vo, dcmodelMap, dcmodelMap1, sourceType);
//				specialBusiNameValue(vo, sourceType, corpvo, bankvo, dcmodelMap);
				
				result.add(vo);
			}
			
		}
		if(!StringUtil.isEmpty(msg.toString())){
			throw new BusinessException(msg.toString());
		}
		return result;
	}
	
//	private void specialBusiNameValue(BankStatementVO2 vo, 
//			int sourceType, 
//			CorpVO corpvo,
//			BankAccountVO bankvo,
//			Map<String, DcModelHVO> dcmodelMap){
//		DcModelHVO headvo = null;
//		if(sourceType == BankStatementVO2.SOURCE_1){//通用模板
//			String key = vo.getBusitypetempname();
//			if(!StringUtil.isEmpty(key)){
//				headvo = dcmodelMap.get(key);
//			}
//		}else{
//			OcrInvoiceVO invvo = buildOcrInvoiceVO(vo, corpvo, bankvo);
//			headvo = match_bankwords.getMatchModel(invvo, corpvo);
//			
//			if(headvo == null){
//				//收款回单
//				Map<String, String> trmap = TransPjlxTypeModel.tranPjlx(4, corpvo.getChargedeptname());
//				headvo = aitovoucherserv_bank.getModelHVO(trmap, corpvo, corpvo.getPk_corp());
//			}
//			
//		}
//		
//		if(headvo != null){
//			vo.setBusitypetempname(headvo.getBusitypetempname());
//			vo.setPk_model_h(headvo.getPk_model_h());
//		}else{
//			vo.setBusitypetempname(null);
//		}
//	}
//	
//	private OcrInvoiceVO buildOcrInvoiceVO(BankStatementVO2 vo, CorpVO corpvo, BankAccountVO bankvo){
//		String bankname = bankvo.getBankname();//银行账户名称
//		bankname = StringUtil.isEmpty(bankname)? "中国农业银行" : bankname;
//		
//		OcrInvoiceVO innvo = new OcrInvoiceVO();
//		innvo.setPk_corp(corpvo.getPk_corp());
//		innvo.setInvoicetype("b" + vo.getZy());
//		innvo.setVsaleopenacc(bankname);//银行名称
//		
//		String bz = StringUtil.isEmpty(vo.getZy()) ? "": "{\"摘要\": \"" + vo.getZy() + "\"}";
//		innvo.setVsalephoneaddr(bz);//银行备注
//		if(SafeCompute.add(DZFDouble.ZERO_DBL, vo.getSyje()).doubleValue() != 0){
//			innvo.setVpurchname(vo.getOthaccountname());//购买方 对方账户名称
//			innvo.setVsalename(corpvo.getUnitname());//销货方  本方账户名称
//		}else if(SafeCompute.add(DZFDouble.ZERO_DBL, vo.getZcje()).doubleValue() != 0){
//			innvo.setVpurchname(corpvo.getUnitname());//购买方 本方账户名称
//			innvo.setVsalename(vo.getOthaccountname());//销货方 对方账户名称 
//		}
//		
//		return innvo;
//	}
//	
//	private String spliceBusiNameBySourceType(BankStatementVO2 vo, int sourceType){
//		String key = null;
//		String zy = null;
//		if(sourceType == BankStatementVO2.SOURCE_1){
//			key = vo.getBusitypetempname();
//		}else if(!StringUtil.isEmpty(vo.getZy())){
//			zy = vo.getZy();
//			
//			Map<String, String[][]> ruleMap = getRuleMap();
//			String regex = buildRegex(ruleMap.keySet());
//			Pattern pattern = Pattern.compile(regex);
//			Matcher matcher = pattern.matcher(zy);
//			
//			String rs = null;
//			String[][] rsArr = null;
//			if(matcher.find()){
//				rs = matcher.group(1);
//				if(!StringUtil.isEmpty(rs)
//						&& ruleMap.containsKey(rs)){
//					rsArr = ruleMap.get(rs);
//				}
//			}
//			
//			if(rsArr != null){
//				if(vo.getZcje() != null){
//					key = rsArr[0][0];
//				}else if(vo.getSyje() != null){
//					key = rsArr[1][0];
//				}
//			}
//			
//		}
//		
//		return key;
//	}
	
//	private String buildRegex(Set<String> set){
//		StringBuilder sb = new StringBuilder();
//		Iterator<String> it = set.iterator();
//		int i = 0;
//		int length = set.size();
//		sb.append("(");
//		while(it.hasNext()){
//			sb.append(it.next());
//			
//			if(++i != length){
//				sb.append("|");
//			}
//		}
//		sb.append(")");
//		
//		return sb.toString();
//	}
	
//	private Map<String, String[][]> getRuleMap(){
//		Map<String, String[][]> map = new HashMap<String, String[][]>();
//		//String[] 0代表支出行  1代表收入行
//		
//		map.put("货款", buildDimen(FieldConstant.YWSTYLE_26, FieldConstant.YWSTYLE_25));
//		map.put("往来款", buildDimen(FieldConstant.YWSTYLE_26, FieldConstant.YWSTYLE_25));
//		
//		map.put("采购款", buildDimen(FieldConstant.YWSTYLE_26, ""));
//		
//		map.put("预付", buildDimen(FieldConstant.YWSTYLE_26, FieldConstant.YWSTYLE_25));
//		map.put("合同", buildDimen(FieldConstant.YWSTYLE_26, FieldConstant.YWSTYLE_25));
//		
//		map.put("购", buildDimen(FieldConstant.YWSTYLE_26, ""));
//		
//		map.put("税", buildDimen(FieldConstant.YWSTYLE_07, ""));
//		
//		map.put("社保", buildDimen(FieldConstant.YWSTYLE_08, ""));
//		map.put("网银手续费", buildDimen(FieldConstant.YWSTYLE_27, ""));
//		map.put("网银直连手续费", buildDimen(FieldConstant.YWSTYLE_27, ""));
//		map.put("网银汇费", buildDimen(FieldConstant.YWSTYLE_27, ""));
//		
//		return map;
//	}
	
//	private String[][] buildDimen(String arg1, String arg2){
//		
//		return new String[][]{ 
//			{ arg1},{ arg2} };
//	}
	
/*	private void specialBusiNameValue(BankStatementVO2 vo, 
			Map<String, DcModelHVO> dcmap,
			Map<String, DcModelHVO> dcmap1,
			int sourceType){
		if(dcmap == null || dcmap.size() == 0){
			return;
		}
		
		DcModelHVO tempdcvo = matchValue(vo, dcmap, dcmap1, sourceType);
		
		if(tempdcvo == null){
			vo.setBusitypetempname(null);
		}else{
			vo.setBusitypetempname(tempdcvo.getBusitypetempname());
			vo.setPk_model_h(tempdcvo.getPk_model_h());
		}
		
//		String key = spliceBusiNameBySourceType(vo, sourceType);
//		DcModelHVO tempdcvo = null;
//		if(!StringUtil.isEmpty(key)){
//			tempdcvo = dcmap.get(key);
//			if(tempdcvo == null){
//				vo.setBusitypetempname(null);
//			}else{
//				vo.setBusitypetempname(tempdcvo.getBusitypetempname());
//				vo.setPk_model_h(tempdcvo.getPk_model_h());
//			}
//		}
		
	}*/
	
	private String transFlag(BankStatementVO2 vo){
		DZFDouble zcje = vo.getZcje();
		DZFDouble syje = vo.getSyje();
		String flag = "";
		if(SafeCompute.add(zcje, DZFDouble.ZERO_DBL).doubleValue() != 0){
			flag = "ZC";
		}else if(SafeCompute.add(syje, DZFDouble.ZERO_DBL).doubleValue() != 0){
			flag = "SY";
		}
		
		return flag;
	}
	
/*	private DcModelHVO matchValue(BankStatementVO2 vo,
			Map<String, DcModelHVO> dcmap,
			Map<String, DcModelHVO> dcmap1,
			int sourceType){
		DcModelHVO hvo = null;
		if(sourceType == BankStatementVO2.SOURCE_1){
			String businame = vo.getBusitypetempname();
			if(!StringUtil.isEmpty(businame)){
				hvo = dcmap1.get(businame);
			}
		}else{
			String zy = vo.getZy();
			String[] words;
			if(!StringUtil.isEmpty(zy)){
				String keywords;
				String flag = transFlag(vo);
				for(Map.Entry<String, DcModelHVO> entry : dcmap.entrySet()){
					keywords = entry.getKey();
					if(!keywords.startsWith(flag)){//判断收入 支出
						continue;
					}
					if(hvo == null && keywords.contains(zy)){
						hvo = entry.getValue();
					}
					
					words = keywords.split("\\*");
					if(hvo == null && words != null && words.length > 0){
						for(String s : words){
							if(zy.contains(s)){
								hvo = entry.getValue();
								break;
							}
						}
					}
					
					if(hvo != null){
						break;
					}
					
				}
			}
			
		}
		
		return hvo;
	}*/
	
/*	private void filterDcModel(List<DcModelHVO> list,
			Map<String, DcModelHVO> map,
			Map<String, DcModelHVO> map1){
		if(list == null || list.size() == 0){
		}
		
		String businame = null;
		String pk_corp = null;
		String keywords = null;
		String vscode = null;
		for(DcModelHVO hvo : list){
			pk_corp = hvo.getPk_corp();
			vscode = hvo.getVspstylecode();
			businame = hvo.getBusitypetempname();
			keywords = hvo.getKeywords();
			if(FieldConstant.FPSTYLE_20.equals(vscode)){
				if(IDefaultValue.DefaultGroup.equals(pk_corp)
						&& keywords.startsWith("中国农业银行")){
					keywords = tranKeyWords(hvo);
					if(!map.containsKey(keywords)){
						map.put(keywords, hvo);
					}
					
					if(!map1.containsKey(businame)){
						map1.put(businame, hvo);
					}
				}else if(!IDefaultValue.DefaultGroup.equals(pk_corp)){
					map.put(keywords, hvo);
					
					map1.put(businame, hvo);//通用模板使用
				}
				
			}
		}
		
	}*/
	
	/*private String tranKeyWords(DcModelHVO hvo){
		String keywords = hvo.getKeywords();
		String stylecode= hvo.getSzstylecode();//结算方式
		if(FieldConstant.SZSTYLE_01.equals(stylecode)
				|| FieldConstant.SZSTYLE_03.equals(stylecode)
				|| FieldConstant.SZSTYLE_05.equals(stylecode)){
			keywords = "SY" + keywords;
		}else{
			keywords = "ZC" + keywords;
		}
		
		return keywords;
	}*/
	
	/**
	 * 校验数据完整性
	 * @param vo
	 */
	private boolean checkValidData(BankStatementVO2 vo, StringBuffer msg, CorpVO corpVO){
		boolean result = true;
		if(vo.getTradingdate() == null){
			result = false;
			msg.append("<p>第").append(vo.getSerialNum() + 1).append("行交易日期必输项为空！</P>");
		}else{
			if(vo.getTradingdate().before(corpVO.getBegindate())){
				throw new BusinessException("交易日期不允许在建账日期前，请检查");
			}
			boolean flag = false;
			if(SafeCompute.add(DZFDouble.ZERO_DBL, vo.getSyje()).doubleValue() != 0)
				flag = !flag;
			
			if(SafeCompute.add(DZFDouble.ZERO_DBL, vo.getZcje()).doubleValue() != 0)
				flag = !flag;
			
			if(!flag){
				result = false;
				msg.append("<p>第").append(vo.getSerialNum() + 1).append("行收入金额或支出金额有且仅录入一项！</P>");
			}
		}
		
		return result;
	}
	
	private void setDefaultValue(BankStatementVO2 vo, BankStatementVO2 paramvo,int sourceType){
		vo.setCoperatorid(paramvo.getCoperatorid());
		vo.setPk_corp(paramvo.getPk_corp());
		vo.setDoperatedate(new DZFDate());
		vo.setPeriod(DateUtils.getPeriod(vo.getTradingdate()));
		vo.setInperiod(vo.getPeriod());//入账期间
		vo.setSourcetem(paramvo.getSourcetem());
		vo.setSourcetype(sourceType);
		vo.setPk_bankaccount(paramvo.getPk_bankaccount());
		if(vo.getYe() == null)
			vo.setYe(new DZFDouble(0));
	}
	
	public List<BankStatementVO2> getDataByExcelNew(MultipartFile file,
			Sheet sheet, 
			int sourceType, 
			StringBuffer msg, 
			Map<String, BankAccountVO> yhzhMap,
			BankStatementVO2 param,
			CorpVO corpvo) throws BusinessException{
		List<BankStatementVO2> blist = null;
		int iBegin = 0;
		blist = new ArrayList<BankStatementVO2>();
		Cell aCell = null;
		String sTmp = "";
		String str;
		BankStatementVO2 excelvo = null;
		
		Object[][] STYLE_1 = getImpConfigObj();
		
		if(STYLE_1 == null || STYLE_1.length == 0){
			throw new BusinessException("导入配置信息不全,请联系管理员");
		}
		
		Map<String, Integer> cursorMap = buildImpStypeMap(STYLE_1);
		Map<String, Integer> cursor2Map= new HashMap<String, Integer>();
		Integer maxCCount = 20;//最大列
		
//		int loopCount = 0;
		for(; iBegin < (sheet.getLastRowNum() + 1); iBegin++){
			if(sheet.getRow(iBegin) == null 
					&& iBegin != sheet.getLastRowNum())
				continue;
			if(iBegin == sheet.getLastRowNum())
				throw new BusinessException("导入失败,导入文件抬头格式不正确 !");
			for(int k = 0; k < maxCCount; k++){
				aCell = sheet.getRow(iBegin).getCell(k);
				sTmp  = getExcelCellValue(aCell);
				
				if(!StringUtil.isEmpty(sTmp)){
					for(Map.Entry<String, Integer> e1 : cursorMap.entrySet()){
						str = e1.getKey();
						if(str.contains(sTmp)){
							cursor2Map.put(sTmp, k);
						}
					}
				}
				
			}
			
			if (cursor2Map.size() > 0) {
				String ss;
				int incount = 0;
				int judcount= 0;
				for(Object[]arr : STYLE_1){
					ss = (String) arr[3];
					if("Y".equals(ss)){
						judcount++;
					}
					ss = (String) arr[1];
					for(Map.Entry<String, Integer> ee : cursor2Map.entrySet()){
						if(ss.contains(ee.getKey())){
							incount++;
							break;
						}
					}
				}
				
				if(incount >= judcount){
					iBegin++;

					STYLE_1 = resetStyleCursor2(STYLE_1, cursor2Map);
					
					cursorMap = cursor2Map;
					break;
				}
				
			}
			
			if(iBegin == sheet.getLastRowNum())
				throw new BusinessException("文件格式不合法，请检查");
		}
		
		int count;//计数器的作用判断该行是不是空行
		boolean isNullFlag;
		for(; iBegin < (sheet.getLastRowNum() + 1); iBegin++){
			
			excelvo = new BankStatementVO2();
			count = 0;
			isNullFlag = false;
			for(int j = 0; j < STYLE_1.length; j++){
				if(sheet.getRow(iBegin) == null){
					isNullFlag = true;//该行不是null行
					break;
				}
				aCell =  sheet.getRow(iBegin).getCell((new Integer(STYLE_1[j][0].toString())).intValue());
				sTmp = getExcelCellValue(aCell);
				
//				if(checkLastRow(sourceType, iBegin, j, sTmp, sheet)){
//					isNullFlag = true;
//					break;
//				}
				
				if(sTmp != null && !StringUtil.isEmpty(sTmp.trim())){
					
//					sTmp = transFirstValue(sTmp, sourceType, excelvo, STYLE_1[j][2].toString());
					sTmp = tranDateValue(sTmp, (String)STYLE_1[j][2]);
					
					excelvo.setAttributeValue(STYLE_1[j][2].toString(), sTmp.replace(" ", ""));
				}else{
					count++;
				}
			}
			
			if(excelvo != null && count != STYLE_1.length && !isNullFlag){
				
//				getAccountByBody(excelvo, sourceType, param, yhzhMap);
//				dealSpecialValue(excelvo, sourceType);
				
				excelvo.setSerialNum(iBegin);//存储行号
				blist.add(excelvo);
			}
			
		}
		
		return blist;
	}
	
	private String tranDateValue(String value, String filed){
		if("tradingdate".equals(filed)){
			value = value.replaceAll("-|/", "");
			if(value.length() > 8){
				value = value.substring(0, 8);
			}
		}
		
		return value;
	}
	
	private Object[][] resetStyleCursor2(Object[][] style, Map<String, Integer> cursorMap) {
		
		if (style != null && style.length > 0) {
			List<Object[]> list = new ArrayList<Object[]>();
			String name = null;
//			Integer acursor = null;
			Integer mcursor = null;
			String key;
			String[] splits;
			boolean flag = false;
			for (Object[] arr : style) {
//				acursor = (Integer) arr[0];
				name = (String) arr[1];
				splits = name.split(";");
				flag = false;
				for(Map.Entry<String, Integer> entry : cursorMap.entrySet()){
					key = entry.getKey();
					
					for(String ss : splits){
						if(!StringUtil.isEmpty(ss) && ss.equals(key)){
							mcursor = cursorMap.get(key);
							arr[1] = key;
							arr[0] = mcursor;
							list.add(arr);
							flag = true;
							break;
						}
					}
					
					if(flag){
						break;
					}
				}
				

			}
			
			if(list.size() > 0){
				style = new Object[list.size()][4];
				for(int i=0; i < list.size();i++){
					
					style[i] =list.get(i); 
				}
			}
		}
		
		return style;
	}
	
	private Map<String, Integer> buildImpStypeMap(Object[][] style) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		if (style != null && style.length > 0) {
			String name = null;
			Integer cursor = null;
			for (Object[] arr : style) {

				cursor = (Integer) arr[0];
				name = (String) arr[1];
				if (!map.containsKey(name)) {
					map.put(name, cursor);
				}
			}
		}

		return map;
	}
	private Object[][] getStyleByField(Sheet sheet, int sourceType)
	{
		Set<String> setJYRQ = new HashSet<String>();
		Set<String> setZY = new HashSet<String>();
		Set<String> setJE = new HashSet<String>();
		Set<String> setYE = new HashSet<String>();
		Set<String> setDFZHMC = new HashSet<String>();
		Set<String> setDFZH = new HashSet<String>();
		Set<String> setSZFLAG = new HashSet<String>();			//收支标志列
		Set<String> setFUFANGDWMC = new HashSet<String>();		//付方单位名称
		Set<String> setSHOUFANGDWMC = new HashSet<String>();	//收方单位名称
		Set<String> setValidWord = new HashSet<String>();	//合法的字符，在脚本中包含的
		BankStatementSetVO[] vos = (BankStatementSetVO[])singleObjectBO.queryByCondition(BankStatementSetVO.class, "nvl(dr,0)=0", new SQLParameter());
		for (BankStatementSetVO vo : vos)
		{
			String str = vo.getColumnalias().toLowerCase();
			
			str = str.replace("（", "(").replace("）", ")").replace(" ", "");
			vo.setColumnalias(str);
			
			for (int i = 0; i < str.length(); i++)
			{
				if (setValidWord.contains(str.substring(i, i + 1)) == false)
				{
					setValidWord.add(str.substring(i, i + 1));
				}
			}
			if (vo.getColumncode().equals("tradingdate"))
			{
				setJYRQ.add(vo.getColumnalias().toLowerCase());
			}
			else if (vo.getColumncode().equals("zy"))
			{
				setZY.add(vo.getColumnalias().toLowerCase());
			}
			else if (vo.getColumncode().equals("je"))
			{
				setJE.add(vo.getColumnalias().toLowerCase());
			}
			else if (vo.getColumncode().equals("ye"))
			{
				setYE.add(vo.getColumnalias().toLowerCase());
			}
			else if (vo.getColumncode().equals("othaccountname"))
			{
				setDFZHMC.add(vo.getColumnalias().toLowerCase());
			}
			else if (vo.getColumncode().equals("othaccountcode"))
			{
				setDFZH.add(vo.getColumnalias().toLowerCase());
			}
			else if (vo.getColumncode().equals("szflag"))
			{
				setSZFLAG.add(vo.getColumnalias().toLowerCase());
			}
			else if  (vo.getColumncode().equals("fufangdwmc"))
			{
				setFUFANGDWMC.add(vo.getColumnalias().toLowerCase());
			}
			else if  (vo.getColumncode().equals("shoufangdwmc"))
			{
				setSHOUFANGDWMC.add(vo.getColumnalias().toLowerCase());
			}
		}
//		List<String> listJYRQ = new ArrayList<String>(Arrays.asList("交易日期", "记账日期", "交易时间", "日期"));
//		List<String> listZY = new ArrayList<String>(Arrays.asList("交易用途", "交易摘要", "摘要内容", "摘要", "备注", "备注信息", "个性化信息", "用途", "附言"));
//		List<String> listJE = new ArrayList<String>(Arrays.asList("贷", "贷方金额", "贷方发生额", "贷方发生额(收入)", "贷方发生额/元", "贷方发生额/元(收入)", "交易金额", "交易金额(借)", "交易金额(贷)", "借", "借方金额", "借方发生额", "借方发生额/元", "借方发生额(支取)", "借方发生额/元(支取)", "收入", "收入金额", "支出", "支出金额", "存入金额"));
//		List<String> listYE = new ArrayList<String>(Arrays.asList("余额", "账户余额", "本次余额"));
//		List<String> listDFZHMC = new ArrayList<String>(Arrays.asList("对方单位名称", "对方户名", "对方名称", "对方姓名", "对方账号名称", "对方账户名", "对方账户名称", "对方帐号名称", "对方帐户名", "对方帐户名称"));
//		List<String> listDFZH = new ArrayList<String>(Arrays.asList("对方账号", "对方账户", "对方帐号", "对方帐户"));
		
//		if (isSpecialBankBill(sourceType))
//		{
//			return getStyleMap().get(sourceType);
//		}
		
		List<Object[]> listObjects = new ArrayList<Object[]>();
		
		int iRow = 0;
		//寻找表头列
		for(; iRow < (sheet.getLastRowNum()); iRow++){
			

			Map<String, Object[]> mapColPos = new HashMap<String, Object[]>();
			
			int iColCount = (sheet.getRow(iRow) == null ? 0 : sheet.getRow(iRow).getLastCellNum());
			if (iColCount == 0) continue;
			List<Object[]> listZYCol = new ArrayList<Object[]>();
			Map<String, DZFDouble> mapLikePercent = new HashMap<String, DZFDouble>();	//比较一致的长度占整个名称的长度比例
			for (int iCol = 0; iCol <= iColCount; iCol++)
			{
				String strCellValue = getCellValue(sheet, iRow, iCol);
				if (StringUtil.isEmpty(strCellValue)) continue;
				String strCellValue_compare_ori = strCellValue.replace("（", "(").replace(" ", "").replace("　", "").replace("）", ")").replace("／", "/").replace(" ", "").toLowerCase();
				String strCellValue_compare = adjustValidString(strCellValue_compare_ori, setValidWord);	//过滤，留下合法字符
				DZFDouble likepercent = new DZFDouble(1.0 * strCellValue_compare.length() / strCellValue_compare_ori.length());	//占原始长度比例
				if (StringUtil.isEmptyWithTrim(strCellValue_compare) == false)
				{
					if (setJYRQ.contains(strCellValue_compare))				//交易日期
					{
						
						if (mapColPos.containsKey("tradingdate") == false)
						{
							Object[] objJYRQ = new Object[]{iCol, strCellValue, "tradingdate"};

							if (!mapLikePercent.containsKey("tradingdate") || mapLikePercent.get("tradingdate").compareTo(likepercent) < 0)
							{
								mapColPos.put("tradingdate", objJYRQ);
								mapLikePercent.put("tradingdate", likepercent);
							}
	
						}
						else
						{
							//判断列名中是否包含时间, 没有“时间”的优先
							if (strCellValue.contains("时间") == false)
							{

								
								if (!mapLikePercent.containsKey("tradingdate") || mapLikePercent.get("tradingdate").compareTo(likepercent) < 0)
								{
									mapColPos.remove("tradingdate");
									Object[] objJYRQ = new Object[]{iCol, strCellValue, "tradingdate"};
									mapColPos.put("tradingdate", objJYRQ);
									mapLikePercent.put("tradingdate", likepercent);
								}
							}
						}
					}
					else if (setJE.contains(strCellValue_compare))			//收入/支出金额
					{
						if (mapColPos.containsKey("syje") == false)
						{
							Object[] objSRJE = new Object[]{iCol, strCellValue, "syje"};
							
							if (!mapLikePercent.containsKey("syje") || mapLikePercent.get("syje").compareTo(likepercent) < 0)
							{
								mapColPos.put("syje", objSRJE);
								mapLikePercent.put("syje", likepercent);
							}

						}
						else if  (mapColPos.containsKey("zcje") == false)
						{
							Object[] objZCJE = new Object[]{iCol, strCellValue, "zcje"};
							if (!mapLikePercent.containsKey("zcje") || mapLikePercent.get("zcje").compareTo(likepercent) < 0)
							{
								mapColPos.put("zcje", objZCJE);
								mapLikePercent.put("zcje", likepercent);
							}

						}
					}
					else if (setYE.contains(strCellValue_compare))			//余额
					{
						if (mapColPos.containsKey("ye") == false)
						{
							Object[] objYE = new Object[]{iCol, strCellValue, "ye"};
							if (!mapLikePercent.containsKey("ye") || mapLikePercent.get("ye").compareTo(likepercent) < 0)
							{
								mapColPos.put("ye", objYE);
								mapLikePercent.put("ye", likepercent);
							}

						}
					}
					else if (setDFZHMC.contains(strCellValue_compare))			//对方账号名称
					{
						if (mapColPos.containsKey("othaccountname") == false)
						{
							Object[] objDFZHMC = new Object[]{iCol, strCellValue, "othaccountname"};
							if (!mapLikePercent.containsKey("othaccountname") || mapLikePercent.get("othaccountname").compareTo(likepercent) < 0)
							{
								mapColPos.put("othaccountname", objDFZHMC);
								mapLikePercent.put("othaccountname", likepercent);
							}

						}
					}
					else if (setDFZH.contains(strCellValue_compare))			//对方账号
					{
						if (mapColPos.containsKey("othaccountcode") == false)
						{
							Object[] objDFZH = new Object[]{iCol, strCellValue, "othaccountcode"};
							if (!mapLikePercent.containsKey("othaccountcode") || mapLikePercent.get("othaccountcode").compareTo(likepercent) < 0)
							{
								mapColPos.put("othaccountcode", objDFZH);
								mapLikePercent.put("othaccountcode", likepercent);
							}

						}
					}
					else if (setSZFLAG.contains(strCellValue_compare))			//收支标志
					{
						if (mapColPos.containsKey("szflag") == false)
						{
							Object[] objSZFLAG = new Object[]{iCol, strCellValue, "szflag"};
							if (!mapLikePercent.containsKey("szflag") || mapLikePercent.get("szflag").compareTo(likepercent) < 0)
							{
								mapColPos.put("szflag", objSZFLAG);
								mapLikePercent.put("szflag", likepercent);
							}

						}
					}
					else if (setFUFANGDWMC.contains(strCellValue_compare))			//付方单位名称
					{
						if (mapColPos.containsKey("fufangdwmc") == false)
						{
							Object[] objFUFANGDWMC = new Object[]{iCol, strCellValue, "fufangdwmc"};
							if (!mapLikePercent.containsKey("fufangdwmc") || mapLikePercent.get("fufangdwmc").compareTo(likepercent) < 0)
							{
								mapColPos.put("fufangdwmc", objFUFANGDWMC);
								mapLikePercent.put("fufangdwmc", likepercent);
							}

						}
					}
					else if (setSHOUFANGDWMC.contains(strCellValue_compare))			//收方单位名称
					{
						if (mapColPos.containsKey("shoufangdwmc") == false)
						{
							Object[] objSHOUFANGDWMC = new Object[]{iCol, strCellValue, "shoufangdwmc"};
							if (!mapLikePercent.containsKey("shoufangdwmc") || mapLikePercent.get("shoufangdwmc").compareTo(likepercent) < 0)
							{
								mapColPos.put("shoufangdwmc", objSHOUFANGDWMC);
								mapLikePercent.put("shoufangdwmc", likepercent);
							}

						}
					}
					//上面被选择的列，如果与摘要集合重复，也加入 2019-10-31 ztc
					if (setZY.contains(strCellValue_compare))			//摘要
					{
						listZYCol.add(new Object[] {iCol, strCellValue, "zy"});
	
					} 
				}
			}
			//如果只缺少收入、支出金额列，从下一行寻找，如果下一行只有收入支出两列，则采用！
			if (mapColPos.containsKey("tradingdate") && listZYCol.size() > 0
					&& (!mapColPos.containsKey("syje") || !mapColPos.containsKey("zcje"))
					&& mapColPos.containsKey("ye")) {

				if (iRow + 1 < (sheet.getLastRowNum()))
				{
					Map<String, Object[]> mapColPos_nextrow = new HashMap<String, Object[]>();
					int iNextRowColCnt = 0;
					int iNextColCount = (sheet.getRow(iRow + 1) == null ? 0 : sheet.getRow(iRow + 1).getLastCellNum());
					if (iNextColCount > 0){

						for (int iCol = 0; iCol <= iNextColCount; iCol++)
						{
							String strCellValue = getCellValue(sheet, iRow + 1, iCol);
							if (StringUtil.isEmpty(strCellValue)) continue;
							iNextRowColCnt++;
							String strCellValue_compare = strCellValue.replace("（", "(").replace(" ", "").replace("　", "").replace("）", ")").replace("／", "/").replace(" ", "").toLowerCase();
							strCellValue_compare = adjustValidString(strCellValue_compare, setValidWord);	//过滤，留下合法字符
							if (StringUtil.isEmptyWithTrim(strCellValue_compare) == false)
							{
								if (setJE.contains(strCellValue_compare))			//收入/支出金额
								{
									if (mapColPos_nextrow.containsKey("syje") == false)
									{
										Object[] objSRJE = new Object[]{iCol, strCellValue, "syje"};
										mapColPos_nextrow.put("syje", objSRJE);
									}
									else if  (mapColPos_nextrow.containsKey("zcje") == false)
									{
										Object[] objZCJE = new Object[]{iCol, strCellValue, "zcje"};
										mapColPos_nextrow.put("zcje", objZCJE);
									}
								}
								
							}
						}
					}
					if (iNextRowColCnt == 2 && mapColPos_nextrow.size() == 2)
					{
						mapColPos.put("syje", mapColPos_nextrow.get("syje"));
						mapColPos.put("zcje", mapColPos_nextrow.get("zcje"));
//						sheet.getRow(iRow).getCell(Integer.parseInt(((Object[])mapColPos_nextrow.get("syje"))[0].toString())).setCellValue((String)((Object[])mapColPos_nextrow.get("syje"))[1]);
//						sheet.getRow(iRow).getCell(Integer.parseInt(((Object[])mapColPos_nextrow.get("zcje"))[0].toString())).setCellValue((String)((Object[])mapColPos_nextrow.get("zcje"))[1]);
					}
				}
			}
			if (mapColPos.containsKey("tradingdate") && listZYCol.size() > 0 && (mapColPos.containsKey("syje") || mapColPos.containsKey("zcje")) && mapColPos.containsKey("ye"))
			{
				listObjects.add(mapColPos.get("tradingdate"));

				listObjects.addAll(listZYCol);
				if (mapColPos.containsKey("syje"))
				{
					listObjects.add(mapColPos.get("syje"));
				}
				if (mapColPos.containsKey("zcje"))
				{
					listObjects.add(mapColPos.get("zcje"));
				}
				listObjects.add(mapColPos.get("ye"));
				
				if (mapColPos.containsKey("othaccountname"))
				{
					listObjects.add(mapColPos.get("othaccountname"));
				}
				if (mapColPos.containsKey("othaccountcode"))
				{
					listObjects.add(mapColPos.get("othaccountcode"));
				}
				if (mapColPos.containsKey("szflag"))
				{
					listObjects.add(mapColPos.get("szflag"));
				}
				if (mapColPos.containsKey("fufangdwmc"))
				{
					listObjects.add(mapColPos.get("fufangdwmc"));
				}
				if (mapColPos.containsKey("shoufangdwmc"))
				{
					listObjects.add(mapColPos.get("shoufangdwmc"));
				}
				break;
			}
		}
		return (listObjects.size() > 0 ? listObjects.toArray(new Object[1][1]) : null);
	}	
	
	private String getCellValue(Sheet sheet, int iRow, int iColumn)
	{
		Cell aCell =  sheet.getRow(iRow).getCell(iColumn);
		String sTmp = getExcelCellValue(aCell);
		return (StringUtil.isEmptyWithTrim(sTmp) ? null : sTmp.trim());
	}
	private List<BankStatementVO2> adjustResult(List<BankStatementVO2> blist, Set<String> setFieldName)
	{
		if (blist == null || blist.size() == 0)
		{
			return blist;
		}
		if (blist.size() == 1)
		{
			throw new BusinessException("通用银行模板仅支持导入超过一行内容的银行对账单");
		}

		int iSize = blist.size();
		DZFDate begindate = blist.get(0).getTradingdate();
		DZFDate enddate = blist.get(blist.size() - 1).getTradingdate();
		boolean dateOrderOK = false;
		//时间顺序排序
		if (begindate != null && enddate != null)
		{
			if (begindate.compareTo(enddate) > 0)
			{
				Collections.reverse(blist);
				dateOrderOK = true;
			}
			else if (begindate.compareTo(enddate) < 0)
			{
				dateOrderOK = true;
			}
		}
		if (dateOrderOK == false)
		{
			boolean DirectionCanBeFound = false;
			for (int i = 0; i < iSize - 1; i++)
			{
				DZFDouble srje1 = (blist.get(i).getSyje() == null ? DZFDouble.ZERO_DBL : blist.get(i).getSyje());
				DZFDouble zcje1 = (blist.get(i).getZcje() == null ? DZFDouble.ZERO_DBL : blist.get(i).getZcje());
				DZFDouble srje2 = (blist.get(i + 1).getSyje() == null ? DZFDouble.ZERO_DBL : blist.get(i + 1).getSyje());
				DZFDouble zcje2 = (blist.get(i + 1).getZcje() == null ? DZFDouble.ZERO_DBL : blist.get(i + 1).getZcje());
				if (!srje1.equals(srje2) || !zcje1.equals(zcje2))
				{
					DirectionCanBeFound = true;
					break;
				}
			}
			if (DirectionCanBeFound == false)
			{
				throw new BusinessException("无法确定对账单内容的业务发生顺序");
			}
		}
		//仅有一个金额列标志
		boolean onlyOneJE = (setFieldName.contains("syje") == false || setFieldName.contains("zcje") == false);
		boolean hasSZFlag = setFieldName.contains("szflag");
		Boolean negativeFlag = null;	//支出金额用负金额表示
		Set<String> setSRName = new HashSet<String>();
		Set<String> setZCName = new HashSet<String>();
		Set<String> setSRZCWord = new HashSet<String>();
		if (hasSZFlag)
		{
			SQLParameter param = new SQLParameter();
			param.addParam("srname");
			param.addParam("zcname");
			BankStatementSetVO[] vos = (BankStatementSetVO[])singleObjectBO.queryByCondition(BankStatementSetVO.class, " columncode in (?, ?) and nvl(dr,0)=0", param);
			for (BankStatementSetVO setvo : vos)
			{
				String str = setvo.getColumnalias();
				for (int i = 0; i < str.length(); i++)
				{
					if (setSRZCWord.contains(str.substring(i, i + 1)) == false)
					{
						setSRZCWord.add(str.substring(i, i + 1));
					}
				}

				if ("srname".equals(setvo.getColumncode()))
				{
					setSRName.add(str);
				}
				else
				{
					setZCName.add(str);
				}
			}
		}
		int iCount = 0, iSwapCount = 0;
		boolean needReverse = false;
		boolean needSwapInOut = false;

		if (!dateOrderOK)	//没有明确判断出日期是升序，用余额逆向检查
		{

			for (int i = iSize - 2; i >= 0; i--)
			{

				
				DZFDouble prevYE = (blist.get(i + 1).getYe() == null ? DZFDouble.ZERO_DBL : blist.get(i + 1).getYe());
				
				DZFDouble SR = (blist.get(i).getSyje() == null ? DZFDouble.ZERO_DBL : blist.get(i).getSyje());
				DZFDouble ZC = (blist.get(i).getZcje() == null ? DZFDouble.ZERO_DBL : blist.get(i).getZcje());
				DZFDouble YE = (blist.get(i).getYe() == null ? DZFDouble.ZERO_DBL : blist.get(i).getYe());
				if (onlyOneJE)	//仅有一列金额
				{
					DZFDouble thisje = (SR.equals(DZFDouble.ZERO_DBL) ? ZC : SR);
					if (hasSZFlag)		//有收支方向的，按照收支方向测试，无收支方向的，按照金额正负测试
					{
						blist.get(i).setSzflag(adjustValidString(blist.get(i).getSzflag(), setSRZCWord));
						
						if (setSRName.contains(blist.get(i).getSzflag()))
						{
							if (prevYE.add(thisje).equals(YE))
							{
								blist.get(i).setSyje(thisje);
								blist.get(i).setZcje(null);
								iCount++;
							}
						}
						else if (setZCName.contains(blist.get(i).getSzflag()))
						{
							if (negativeFlag == null)
							{
								if (prevYE.sub(thisje).equals(YE))
								{
									blist.get(i).setSyje(null);
									blist.get(i).setZcje(thisje);
									negativeFlag = false;
									iCount++;
								}
								else if (prevYE.sub(thisje.multiply(-1.0)).equals(YE))
								{
									blist.get(i).setSyje(null);
									blist.get(i).setZcje(thisje.multiply(-1.0));
									negativeFlag = true;
									iCount++;
								}
							}
							else
							{
								if (prevYE.sub(negativeFlag ? thisje.multiply(-1.0) : thisje).equals(YE))
								{
									blist.get(i).setSyje(null);
									blist.get(i).setZcje(negativeFlag ? thisje.multiply(-1.0) : thisje);
									iCount++;
								}
							}
						}
						else
						{
							throw new BusinessException("无法确定收支列'" + blist.get(i).getSzflag() + "'内容含义，请从下列的银行清单中找到对应银行再次导入，若无对应银行，请将对账单样本发送给大账房客服部。");
						}
					}
					else			//无收支方向的，按照金额正负测试
					{
						if (thisje.compareTo(DZFDouble.ZERO_DBL) > 0)	//正数，收入
						{
							if (prevYE.add(thisje).equals(YE))
							{
								blist.get(i).setSyje(thisje);
								blist.get(i).setZcje(null);
								iCount++;
							}
						} else
						{
							if (prevYE.sub(thisje.abs()).equals(YE))
							{
								blist.get(i).setSyje(null);
								blist.get(i).setZcje(thisje.abs());
								iCount++;
							}
						}
					}
					
				}
				else
				{
					if (prevYE.add(SR).sub(ZC).equals(YE))
					{
						iCount++;

					}
					else if (prevYE.add(ZC).sub(SR).equals(YE))
					{
						iSwapCount++;
					}
				}
			}
			
			if (iCount == iSize - 1)
			{
				needReverse = true;
				
				//处理最后一行的金额位置
				DZFDouble SR = (blist.get(iSize - 1).getSyje() == null ? DZFDouble.ZERO_DBL : blist.get(iSize - 1).getSyje());
				DZFDouble ZC = (blist.get(iSize - 1).getZcje() == null ? DZFDouble.ZERO_DBL : blist.get(iSize - 1).getZcje());
				DZFDouble JE = (SR.equals(DZFDouble.ZERO_DBL) ? ZC : SR);
				if (onlyOneJE)	//仅有一列金额，处理最后一行的金额
				{
					
					if (hasSZFlag)
					{
						blist.get(iSize - 1).setSzflag(adjustValidString(blist.get(iSize - 1).getSzflag(), setSRZCWord));
						if (setSRName.contains(blist.get(iSize - 1).getSzflag()))
						{
							blist.get(iSize - 1).setSyje(JE);
							blist.get(iSize - 1).setZcje(null);
						}
						else if (setZCName.contains(blist.get(iSize - 1).getSzflag()))
						{
							blist.get(iSize - 1).setSyje(null);
							if (negativeFlag == null)	//没有支出列，全是收入列，最后一个支出肯定不是支出的冲销，所以应该是正数金额
							{
								blist.get(iSize - 1).setZcje(JE.abs());
							}
							else
							{
								blist.get(iSize - 1).setZcje(negativeFlag ? JE.multiply(-1.0) : JE);
							}
						}
						else
						{
							throw new BusinessException("无法确定尾行收支列'" + blist.get(iSize - 1).getSzflag() + "'内容含义，请从下列的银行清单中找到对应银行再次导入，若无对应银行，请将对账单样本发送给大账房客服部。");
						}
					}
					else			//无收支标志，只能靠正负号确定方向
					{
						if (JE.compareTo(DZFDouble.ZERO_DBL) > 0)	//正数，收入
						{
						
							blist.get(iSize - 1).setSyje(JE);
							blist.get(iSize - 1).setZcje(null);
						
						} else
						{
							blist.get(iSize - 1).setSyje(null);
							blist.get(iSize - 1).setZcje(JE.abs());
						}
					}
				}

			}
			else if (iSwapCount == iSize - 1)
			{
				needReverse = true;
				needSwapInOut = true;

			}
		}
		if (needReverse)	//对账单列表反序
		{
			Collections.reverse(blist);
			if (needSwapInOut)						//交换收入支出列
			{
				for (BankStatementVO2 vo : blist)	//收入支出列装反了。
				{
					DZFDouble d = vo.getSyje();
					vo.setSyje(vo.getZcje());
					vo.setZcje(d);
				}
			}

		}
		else
		{
			//检查收入支出列是否装反
			iCount = 0;
			iSwapCount = 0;
			
			negativeFlag = null;
			
			for (int i = 1; i < iSize; i++)
			{

				DZFDouble prevYE = (blist.get(i - 1).getYe() == null ? DZFDouble.ZERO_DBL : blist.get(i - 1).getYe());
				
				DZFDouble SR = (blist.get(i).getSyje() == null ? DZFDouble.ZERO_DBL : blist.get(i).getSyje());
				DZFDouble ZC = (blist.get(i).getZcje() == null ? DZFDouble.ZERO_DBL : blist.get(i).getZcje());
				DZFDouble YE = (blist.get(i).getYe() == null ? DZFDouble.ZERO_DBL : blist.get(i).getYe());
				if (onlyOneJE)
				{
					
					DZFDouble thisje = (SR.equals(DZFDouble.ZERO_DBL) ? ZC : SR);
					
					if (hasSZFlag)
					{
						blist.get(i).setSzflag(adjustValidString(blist.get(i).getSzflag(), setSRZCWord));
						if (setSRName.contains(blist.get(i).getSzflag()))
						{
							
							if (prevYE.add(thisje).equals(YE))
							{
								blist.get(i).setSyje(thisje);
								blist.get(i).setZcje(null);
								iCount++;
							}
						}
						else if (setZCName.contains(blist.get(i).getSzflag()))
						{
							if (negativeFlag == null)
							{
								 if (prevYE.sub(thisje).equals(YE))
								 {
									 blist.get(i).setSyje(null);
									 blist.get(i).setZcje(thisje);
									 negativeFlag = false;
									 iCount++;
								 }
								 else if  (prevYE.sub(thisje.multiply(-1.0)).equals(YE))
								 {
									 blist.get(i).setSyje(null);
									 blist.get(i).setZcje(thisje.multiply(-1));
									 negativeFlag = true;
									 iCount++;
								 }
							}
							else if (prevYE.sub(negativeFlag ? thisje.multiply(-1.0) : thisje).equals(YE))
							{
								blist.get(i).setSyje(null);
								blist.get(i).setZcje(negativeFlag ? thisje.multiply(-1.0) : thisje);
								iCount++;
							}
							
						}
						else
						{
							throw new BusinessException("无法确定收支列'" + blist.get(i).getSzflag() + "'内容含义，请从下列的银行清单中找到对应银行再次导入，若无对应银行，请将对账单样本发送给大账房客服部。");
						}
					}
					else			//无收支方向的，按照金额正负测试
					{
						if (thisje.compareTo(DZFDouble.ZERO_DBL) > 0)	//正数，收入
						{
							if (prevYE.add(thisje).equals(YE))
							{
								blist.get(i).setSyje(thisje);
								blist.get(i).setZcje(null);
								iCount++;
							}
						} else
						{
							if (prevYE.sub(thisje.abs()).equals(YE))
							{
								blist.get(i).setSyje(null);
								blist.get(i).setZcje(thisje.abs());
								iCount++;
							}
						}
					}
				}
				else
				{
					
					if (prevYE.add(SR).sub(ZC).equals(YE))
					{
						iCount++;

					}
					else if (prevYE.add(ZC).sub(SR).equals(YE))
					{
						iSwapCount++;
					}
				
				}
			}
			if (iSwapCount == iSize - 1)
			{
				for (BankStatementVO2 vo : blist)	//收入支出列装反了。
				{
					DZFDouble d = vo.getSyje();
					vo.setSyje(vo.getZcje());
					vo.setZcje(d);
				}
			}
			else if (iCount != iSize - 1)
			{
				throw new BusinessException("收入、支出、余额三列累加不连续，请核对对账单内容。");//，或者把对账单样本发送给大账房客服部");
			}
			if (onlyOneJE)
			{
				//处理首行的金额
				DZFDouble SR = (blist.get(0).getSyje() == null ? DZFDouble.ZERO_DBL : blist.get(0).getSyje());
				DZFDouble ZC = (blist.get(0).getZcje() == null ? DZFDouble.ZERO_DBL : blist.get(0).getZcje());
				DZFDouble JE = (SR.equals(DZFDouble.ZERO_DBL) ? ZC : SR);
				
				if (hasSZFlag)
				{
					blist.get(0).setSzflag(adjustValidString(blist.get(0).getSzflag(), setSRZCWord));
					if (setSRName.contains(blist.get(0).getSzflag()))
					{
						blist.get(0).setSyje(JE);
						blist.get(0).setZcje(null);
					}
					else if (setZCName.contains(blist.get(0).getSzflag()))
					{
						blist.get(0).setSyje(null);
						if (negativeFlag == null)
						{
							//没有发生过支出金额，不知道支出用正号还是负号表示，如果本笔业务就是负的，就当支出以负金额表示处理，这笔是首行，不应该首行就是反向金额冲销业务
							blist.get(0).setZcje(JE.abs());
							
						}
						else
						{
							blist.get(0).setZcje(negativeFlag ? JE.multiply(-1.0) : JE);
						}
					}
					else
					{
						throw new BusinessException("无法确定首行收支列'" + blist.get(0).getSzflag() + "'内容含义，请从下列的银行清单中找到对应银行再次导入，若无对应银行，请将对账单样本发送给大账房客服部。");
					}
				}
				else  //无收支列，按金额正负放位置
				{
					if (JE.compareTo(DZFDouble.ZERO_DBL) > 0)
					{
						blist.get(0).setSyje(JE);
						blist.get(0).setZcje(null);
					}
					else
					{
						blist.get(0).setSyje(null);
						blist.get(0).setZcje(JE.abs());
					}
				}
				
			}
		}
		
		
		return blist;
	}
	private String adjustValidString(String checkstr, Set<String> setValidWord)
	{
		if (StringUtil.isEmpty(checkstr) == false)
		{
			StringBuffer sbf = new StringBuffer();
			for (int i = 0; i < checkstr.length(); i++)
			{
				if (setValidWord.contains(checkstr.substring(i, i+ 1)))
				{
					sbf.append(checkstr.substring(i, i+ 1));
				}
						
			}
			checkstr = sbf.toString();
		}
		return checkstr;
	}
	public List<BankStatementVO2> getDataByExcel(MultipartFile file,
			Sheet sheet, 
			int sourceType, 
			StringBuffer msg, 
			Map<String, BankAccountVO> yhzhMap,
			BankStatementVO2 param,
			CorpVO corpvo) throws BusinessException{
		List<BankStatementVO2> blist = null;
		int iBegin = 0;
		blist = new ArrayList<BankStatementVO2>();
		Cell aCell = null;
		String sTmp = "";
		BankStatementVO2 excelvo = null;
		BankAccountVO bankvo = null;
		String unitname = corpvo == null ? "" : corpvo.getUnitname();
		
		//Object[][] STYLE_1 = getStyleMap().get(sourceType);
		Object[][] STYLE_1 = (sourceType == BankStatementVO2.SOURCE_999 ? getStyleByField(sheet, sourceType) : getStyleMap().get(sourceType));
		if (STYLE_1 == null)
		{
			throw new BusinessException("导入失败，文件格式比较特殊，请从下列的银行清单中找到对应银行再次导入，若无对应银行，请将对账单样本发送给大账房客服部。");
		}
		int loopCount = 0;
		for(; iBegin < (sheet.getLastRowNum() + 1); iBegin++){
			if(sheet.getRow(iBegin) == null 
					&& iBegin != sheet.getLastRowNum())
				continue;
			if(iBegin == sheet.getLastRowNum())
				throw new BusinessException("导入失败,导入文件抬头格式不正确 !");
			loopCount = 0;
			for(int k = 0; k < STYLE_1.length; k++){
				aCell = sheet.getRow(iBegin).getCell((new Integer(STYLE_1[k][0].toString())).intValue());
				sTmp  = getExcelCellValue(aCell);
				String sTmp2 = null;
				if ((STYLE_1[k][2].equals("syje") || STYLE_1[k][2].equals("zcje")) && iBegin < sheet.getLastRowNum() - 1)
				{
					aCell = sheet.getRow(iBegin + 1).getCell((new Integer(STYLE_1[k][0].toString())).intValue());
					sTmp2  = getExcelCellValue(aCell);
					if (sTmp2 != null)
					{
						sTmp2 = sTmp2.trim();
					}
				}
				
				bankvo = getAccountByHead(sheet, yhzhMap, sourceType, iBegin, (new Integer(STYLE_1[k][0].toString())).intValue(), sTmp, bankvo, param);
				
				if(sTmp != null && sTmp.trim().startsWith(STYLE_1[k][1].toString()) || sTmp2 != null && sTmp2.startsWith(STYLE_1[k][1].toString())){
					loopCount++;
				}else if(StringUtil.isEmptyWithTrim(STYLE_1[k][1].toString()) && StringUtil.isEmptyWithTrim(sTmp)){//排除合并行的干扰
					loopCount++;
				}else{
					break;
				}
				
			}
			if(STYLE_1.length == loopCount){
				iBegin++;
				break;
			}
			
			if(iBegin == sheet.getLastRowNum())
				throw new BusinessException("文件格式不合法，请检查");
			if (sourceType == BankStatementVO2.SOURCE_999)
			{
				bankvo = getAccountByHead_General(sheet, yhzhMap, iBegin, bankvo, param);
			}
		}
		
		int count;//计数器的作用判断该行是不是空行
		boolean isNullFlag = false;
		Set<String> setFieldName = new HashSet<String>();	//字段名
		if(sourceType == BankStatementVO2.SOURCE_2 || sourceType == BankStatementVO2.SOURCE_39
				|| sourceType == BankStatementVO2.SOURCE_40 || sourceType == BankStatementVO2.SOURCE_44
				|| sourceType == BankStatementVO2.SOURCE_52){//由于规则涉及太多，故将银行模板单独处理
			for(; iBegin < (sheet.getLastRowNum() + 1); iBegin++){
				
				excelvo = new BankStatementVO2();
				count = 0;
				isNullFlag = false;
				for(int j = 0; j < STYLE_1.length; j++){
					if(sheet.getRow(iBegin) == null){
						isNullFlag = true;//该行不是null行
						break;
//						throw new BusinessException("导入失败,导入文件格式不正确 !");
					}
					aCell =  sheet.getRow(iBegin).getCell((new Integer(STYLE_1[j][0].toString())).intValue());
					sTmp = getExcelCellValue(aCell);
					
					if(sTmp != null && !StringUtil.isEmptyWithTrim(sTmp)){
						sTmp = sTmp.trim();
						int k = new Integer(STYLE_1[j][0].toString());//标识
						if(13 == k){//交易金额
							if(SafeCompute.add(DZFDouble.ONE_DBL, new DZFDouble(sTmp)).doubleValue() > 0){
								excelvo.setAttributeValue("syje", sTmp.replace(" ", ""));
							}else{
								excelvo.setAttributeValue("zcje", SafeCompute.multiply(new DZFDouble(-1), new DZFDouble(sTmp)));
							}
//						}else if(excelvo.getAttributeValue(STYLE_1[j][2].toString()) instanceof String){//拼接字符串
						}else if(10 == k){
								sTmp = sTmp.substring(0, 4) + "/" + sTmp.substring(4, 6) + "/" + sTmp.substring(6,8);
								excelvo.setAttributeValue(STYLE_1[j][2].toString(), sTmp.replace(" ", ""));
						}else{
							String str = (String) excelvo.getAttributeValue(STYLE_1[j][2].toString());
							if(!StringUtil.isEmpty(str)){
								sTmp = sTmp + "," + str;
							}
							
							excelvo.setAttributeValue(STYLE_1[j][2].toString(), sTmp.replace(" ", ""));
						}
						
					}else{
						count++;
					}
				}
				
				if(excelvo != null && count != STYLE_1.length && !isNullFlag){// && !StringUtil.isEmpty(sTmp)
					excelvo.setSerialNum(iBegin);//存储行号
					zgyhSpecialValue(excelvo, bankvo, unitname);
					
					blist.add(excelvo);
				}
				
			}
		}else{//正常流程
			if(sourceType == BankStatementVO2.SOURCE_53){
				iBegin++;//长沙银行 行有合并
			}
			for(; iBegin < (sheet.getLastRowNum() + 1); iBegin++){
				
				if((sourceType == BankStatementVO2.SOURCE_43 
						|| sourceType == BankStatementVO2.SOURCE_46
						|| sourceType == BankStatementVO2.SOURCE_51
						|| sourceType == BankStatementVO2.SOURCE_58)&& isNullFlag)
					break;
				
				excelvo = new BankStatementVO2();
				count = 0;
				isNullFlag = false;
				for(int j = 0; j < STYLE_1.length; j++){
					if(sheet.getRow(iBegin) == null){
						isNullFlag = true;//该行不是null行
						break;
					}
					aCell =  sheet.getRow(iBegin).getCell((new Integer(STYLE_1[j][0].toString())).intValue());
					sTmp = getExcelCellValue(aCell);
					
					if(checkLastRow(sourceType, iBegin, j, sTmp, sheet)){
						isNullFlag = true;
						break;
					}
					//发现、处理的列
					if (!setFieldName.contains((String)STYLE_1[j][2]))
					{
						setFieldName.add((String)STYLE_1[j][2]);
					}
					if(sTmp != null && !StringUtil.isEmpty(sTmp.trim())){
						
						sTmp = sTmp.trim();
						sTmp = transFirstValue(sTmp, sourceType, excelvo, STYLE_1[j][2].toString(), corpvo);
						
						excelvo.setAttributeValue(STYLE_1[j][2].toString(), (sTmp == null ? null : sTmp.replace(" ", "")));
					}else{
						count++;
					}
				}
				boolean noSRJE = excelvo.getSyje() == null || excelvo.getSyje().equals(DZFDouble.ZERO_DBL);
				boolean noZCJE = excelvo.getZcje() == null || excelvo.getZcje().equals(DZFDouble.ZERO_DBL);
				boolean noYE = excelvo.getYe() == null || excelvo.getYe().equals(DZFDouble.ZERO_DBL);
	
				//无发生额和余额时，结束了
				if (sourceType == BankStatementVO2.SOURCE_999 
						&& (excelvo.getTradingdate() == null  || noSRJE && noZCJE && noYE))
				{
					if (blist.size() > 0 )
					{
						isNullFlag = true;
						break;
					}
					else
					{
						continue;
					}
				}
				if(excelvo != null && count != STYLE_1.length && !isNullFlag){
					
					getAccountByBody(excelvo, sourceType, param, yhzhMap);
					
					dealSpecialValue(excelvo, sourceType);
					
					excelvo.setSerialNum(iBegin);//存储行号
					
					//处理对方账户名称
					if (StringUtil.isEmpty(excelvo.getOthaccountname()) && (StringUtil.isEmpty(excelvo.getFufangdwmc()) == false || StringUtil.isEmpty(excelvo.getShoufangdwmc()) == false))
					{
						String ffmc = excelvo.getFufangdwmc();
						String sfmc = excelvo.getShoufangdwmc();
						if (StringUtil.isEmpty(ffmc) == false && StringUtil.isEmpty(sfmc) == false)
						{
							if (OcrUtil.isSameCompany(corpvo.getUnitname(), ffmc))
							{
								excelvo.setOthaccountname(sfmc);
							}
							else if (OcrUtil.isSameCompany(corpvo.getUnitname(), sfmc))
							{
								excelvo.setOthaccountname(ffmc);
							}
							else
							{
								throw new BusinessException(sfmc +  " 和 " + ffmc + " 都不是导入对账单的公司，请检查!");
							}
						}
						else if (StringUtil.isEmpty(ffmc) == false)
						{
							if (OcrUtil.isSameCompany(corpvo.getUnitname(), ffmc) == false)
							{
								excelvo.setOthaccountname(ffmc);
							}
						}
						else
						{
							if (OcrUtil.isSameCompany(corpvo.getUnitname(), sfmc) == false)
							{
								excelvo.setOthaccountname(sfmc);
							}
						}
					}
					blist.add(excelvo);
				}
				
			}
		}
		if (sourceType == BankStatementVO2.SOURCE_999)	//通用银行模板，重新整理数据
		{
			blist = adjustResult(blist, setFieldName);
		}
		return blist;
	}
	
	private void getAccountByBody(BankStatementVO2 vo,
			int sourceType,
			BankStatementVO2 param,
			Map<String, BankAccountVO> yhzhMap){
		if(yhzhMap == null || yhzhMap.size() == 0)
			return;
		
		String sTmp = null;
		BankAccountVO bankvo = null;
		if(sourceType == BankStatementVO2.SOURCE_12){
			sTmp = vo.getVdef8();
			if(!StringUtil.isEmpty(sTmp)){
				bankvo = yhzhMap.get(sTmp);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_13){
			sTmp = vo.getVdef8();
			if(!StringUtil.isEmpty(sTmp)){
				bankvo = yhzhMap.get(sTmp);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_23){
			String value = vo.getVdef1();
			if("收入".equals(value)){
				sTmp = vo.getVdef6();
			}else if("支出".equals(value)){
				sTmp = vo.getVdef3();
			}
			
			if(!StringUtil.isEmpty(sTmp)){
				bankvo = yhzhMap.get(sTmp);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_30){
			sTmp = vo.getVdef8();
			if(!StringUtil.isEmpty(sTmp)){
				bankvo = yhzhMap.get(sTmp);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_35){
			sTmp = vo.getVdef8();
			if(!StringUtil.isEmpty(sTmp)){
				bankvo = yhzhMap.get(sTmp);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_48){
			sTmp = vo.getVdef8();
			if(!StringUtil.isEmpty(sTmp)){
				bankvo = yhzhMap.get(sTmp);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_49){
			sTmp = vo.getVdef8();
			if(!StringUtil.isEmpty(sTmp)){
				bankvo = yhzhMap.get(sTmp);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_54){
			sTmp = vo.getVdef8();
			if(!StringUtil.isEmpty(sTmp)){
				bankvo = yhzhMap.get(sTmp);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_56){
			sTmp = vo.getVdef8();
			if(!StringUtil.isEmpty(sTmp)){
				bankvo = yhzhMap.get(sTmp);
			}
		}
		
		
		if(bankvo != null){
			param.setPk_bankaccount(bankvo.getPk_bankaccount());
		}
	}
	
	//根据excel表头确认银行账户
	private BankAccountVO getAccountByHead(Sheet sheet,
			Map<String, BankAccountVO> yhzhMap,
			int sourceType,
			int iBegin,
			int k,
			String value,
			BankAccountVO bankvo,
			BankStatementVO2 param){
		if(StringUtil.isEmpty(value) || bankvo != null || yhzhMap.size() == 0)
			return bankvo;
		
		Cell aCell = null;
		String sTmp = null;
		if(sourceType == BankStatementVO2.SOURCE_11 && k == 0){//第一列
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
				
			}
		}else if(sourceType == BankStatementVO2.SOURCE_3 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
				if(!StringUtil.isEmpty(sTmp)){
					sTmp = sTmp.replace("\t", "");
				}
			}
		}else if(sourceType == BankStatementVO2.SOURCE_55 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
				if(!StringUtil.isEmpty(sTmp)){
					sTmp = sTmp.replace("\t", "");
				}
			}
		}else if(sourceType == BankStatementVO2.SOURCE_37 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
				if(!StringUtil.isEmpty(sTmp)){
					sTmp = sTmp.replace("\t", "");
				}
			}
		}else if(sourceType == BankStatementVO2.SOURCE_38 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
				if(!StringUtil.isEmpty(sTmp)){
					sTmp = sTmp.replace("\t", "");
				}
			}
		}else if(sourceType == BankStatementVO2.SOURCE_45 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
				if(!StringUtil.isEmpty(sTmp)){
					sTmp = sTmp.replace("\t", "");
				}
			}
		}else if(sourceType == BankStatementVO2.SOURCE_34 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
				if(!StringUtil.isEmpty(sTmp)){
					sTmp = sTmp.replace("\t", "");
				}
			}
		}else if(sourceType == BankStatementVO2.SOURCE_36 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
				if(!StringUtil.isEmpty(sTmp)){
					sTmp = sTmp.replace("\t", "");
				}
			}
		}else if(sourceType == BankStatementVO2.SOURCE_10 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_2 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("查询账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_22 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_24 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_27 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("企业名称")){
				aCell = sheet.getRow(iBegin).getCell(k+4);
				sTmp  = getExcelCellValue(aCell);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_28 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("查询账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_31 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_32 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账户名称")){
				aCell = sheet.getRow(iBegin).getCell(k+3);
				sTmp  = getExcelCellValue(aCell);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_39 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("查询账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_52 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("查询账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_40 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("查询账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
				if(!StringUtil.isEmpty(sTmp)){
					sTmp = sTmp.replace("\t", "");
				}
			}
		}else if(sourceType == BankStatementVO2.SOURCE_41 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账户")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_47 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_50 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_51 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
			}
		}else if(sourceType == BankStatementVO2.SOURCE_57 && k == 5){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
				if(!StringUtil.isEmpty(sTmp)){
					sTmp = sTmp.replace(" ", "");
				}
			}
		}else if(sourceType == BankStatementVO2.SOURCE_58 && k == 0){
			value = value.replaceAll(" |　", "");
			if(value.startsWith("账号")){
				aCell = sheet.getRow(iBegin).getCell(++k);
				sTmp  = getExcelCellValue(aCell);
			}
		}
		
		if(!StringUtil.isEmpty(sTmp)){
			BankAccountVO vo = yhzhMap.get(sTmp);
			if(vo != null){
				param.setPk_bankaccount(vo.getPk_bankaccount());//重新设置银行账户
				return vo;
			}
		}
		
		return bankvo;
	}
	private String filterBankNo(String value)
	{
		if (value == null)
		{
			return value;
		}
		StringBuffer bufBankNO = new StringBuffer();
		for (char ch : value.toCharArray())
		{
			if (ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z')
			{
				bufBankNO.append(ch);
			}
		}
		return bufBankNO.toString();
	}
	//根据excel表头确认银行账户, 全列查找
		private BankAccountVO getAccountByHead_General(Sheet sheet,
				Map<String, BankAccountVO> yhzhMap,
				int iRow,
				BankAccountVO bankvo,
				BankStatementVO2 param){
			
			if(bankvo != null || yhzhMap.size() == 0)
				return bankvo;
			

			int iColCount = (sheet.getRow(iRow) == null ? 0 : sheet.getRow(iRow).getLastCellNum());
			
			for (int iCol = 0; iCol < iColCount; iCol++)
			{
				String sTmp = getCellValue(sheet, iRow, iCol);
				
				if (StringUtil.isEmptyWithTrim(sTmp)) continue;
				
				sTmp = filterBankNo(sTmp);
				if (sTmp.length() > 0)
				{
					BankAccountVO vo = yhzhMap.get(sTmp);
					if(vo != null){
						param.setPk_bankaccount(vo.getPk_bankaccount());//重新设置银行账户
						return vo;
					}
				}
			}
			
			return bankvo;
		}
	private boolean checkLastRow(int sourceType, int iBegin, int j, String sTmp, Sheet sheet){
		boolean flag = false;
		if (sourceType == BankStatementVO2.SOURCE_13
				&& iBegin == sheet.getLastRowNum()
				&& j == 0 && StringUtil.isEmpty(sTmp)) {
			flag = true;
		}else if(sourceType == BankStatementVO2.SOURCE_16
				&& j == 0
				&& !StringUtil.isEmpty(sTmp)
				&& (sTmp.startsWith("承前") || sTmp.startsWith("本页累计借方发生额"))){
			flag = true;
		}else if(sourceType == BankStatementVO2.SOURCE_28
				&& j == 0
				&& !StringUtil.isEmpty(sTmp)
				&& sTmp.startsWith("借方交易笔数")){
			flag = true;
		}else if(sourceType == BankStatementVO2.SOURCE_38
				&& j == 0
				&& !StringUtil.isEmpty(sTmp)
				&& sTmp.startsWith("截止日期")){
			flag = true;
		}else if(sourceType == BankStatementVO2.SOURCE_45
				&& j == 0
				&& !StringUtil.isEmpty(sTmp)
				&& sTmp.startsWith("截止日期")){
			flag = true;
		}else if(sourceType == BankStatementVO2.SOURCE_41
				&& j == 0
				&& !StringUtil.isEmpty(sTmp)
				&& (sTmp.startsWith("总笔数:")
						|| sTmp.startsWith("汇出笔数:")
						|| sTmp.startsWith("汇入笔数:"))){
			flag = true;
		}else if(sourceType == BankStatementVO2.SOURCE_43
				&& j == 0
				&& !StringUtil.isEmpty(sTmp)
				&& sTmp.startsWith("汇总收入金额")){
			flag = true;
		}else if(sourceType == BankStatementVO2.SOURCE_46
				&& j == 0
				&& !StringUtil.isEmpty(sTmp)
				&& sTmp.startsWith("汇总收入金额")){
			flag = true;
		}else if(sourceType == BankStatementVO2.SOURCE_51
				&& j == 0
				&& !StringUtil.isEmpty(sTmp)
				&& sTmp.startsWith("收入合计")){
			flag = true;
		}else if(sourceType == BankStatementVO2.SOURCE_58
				&& j == 0
				&& !StringUtil.isEmpty(sTmp)
				&& sTmp.startsWith("总笔数")){
			flag = true;
		}
		
		return flag;
	}
	
	private String transFirstValue(String value, int stype, BankStatementVO2 vo, String xy, CorpVO corpvo){
		
		if(stype == BankStatementVO2.SOURCE_3 
				|| stype == BankStatementVO2.SOURCE_36 
				|| stype == BankStatementVO2.SOURCE_26
				|| stype == BankStatementVO2.SOURCE_55){
			value = value.replace("\t", "");
		}else if(stype == BankStatementVO2.SOURCE_9){
			value = value.replace("", "");//特殊字符处理， 之所以放在这里 是因为苏州分行每个格子都有这个特殊字符 很无奈
		}else if(stype == BankStatementVO2.SOURCE_11 
				|| stype == BankStatementVO2.SOURCE_12
				|| stype == BankStatementVO2.SOURCE_15
				|| stype == BankStatementVO2.SOURCE_18
				|| stype == BankStatementVO2.SOURCE_42
				|| stype == BankStatementVO2.SOURCE_48
				|| stype == BankStatementVO2.SOURCE_49){
			String str = (String) vo.getAttributeValue(xy);
			if(!StringUtil.isEmpty(str)){
				value = value + "，" + str;
			}
		}
		else if (stype == BankStatementVO2.SOURCE_999)
		{
			if (xy.equals("zy"))
			{
				if (StringUtil.isEmpty(value) == false)
				{

					String str = (String) vo.getAttributeValue(xy);
					if (value.trim().equals("-") || value.trim().equals("―") || value.trim().equals("/") || value.trim().equals("/"))
					{
						value = str;
					}
					else
					{
						try 
						{
							if (str == null || str.getBytes("UTF-8").length < 190)
							{
								value = (str == null ? "" :  str + "，") + value;
	
								while (value.getBytes("UTF-8").length >= 200)
								{
									value = value.substring(0, value.length() - 1);
								}
							}
							else
							{
								value = str;	//不再接摘要
							}
						}
						catch (Exception e)
						{
							log.error(e.getMessage(), e);
						}
					}
				}
				
			}
			else if (xy.equals("tradingdate"))
			{
				if(!StringUtil.isEmpty(value)){
					try {
						value = filterDateChar(value.trim());
						Date date = null;
						if (value.contains(" "))
						{
							String[] sa = value.split(" ");
							value = sa[0];
						}
						if (value.length() == 8)
						{
							date = DateUtils.parse(value, "yyyyMMdd");
						}
						else if (value.length() == 16)
						{
							date = DateUtils.parse(value, "yyyyMMddhh:mm:ss");
						}
						else if (value.length() == 17)
						{
							date = DateUtils.parse(value, "yyyyMMdd hh:mm:ss");
						}
						else if (value.length() == 18)
						{
							 date = DateUtils.parse(value, "yyyy-MM-ddhh:mm:ss");
						}
						else if (value.length() == 19)
						{
							 date = DateUtils.parse(value, "yyyy-MM-dd hh:mm:ss");
						}
						if (date != null)
						{
							value = DateUtils.format(date, "yyyy-MM-dd");
						}
					} catch (Exception e) {
						log.error("错误",e);
					}
				}
			}
			else if (xy.equals("syje") || xy.equals("zcje") || xy.equals("ye"))
			{
				if (StringUtil.isEmptyWithTrim(value) == false)
				{
					StringBuffer bufValue = new StringBuffer();
					for (char ch : value.toCharArray())
					{
						if (ch >= '0' && ch <= '9' || ch == '.' || ch == '+' || ch == '-')
						{
							bufValue.append(ch);
						}
					}
					value = bufValue.toString();
				}
			}
			else if (xy.equals("szflag"))
			{
				if (!StringUtil.isEmpty(value))
				{
					value = value.replace(" ", "").replace("　", "");
				}
			}
			else if (xy.equals("othaccountcode"))
			{
				if (!StringUtil.isEmpty(value))
				{
					value = filterBankNo(value);
				}
			}
			else if (xy.equals("othaccountname"))
			{
				if (!StringUtil.isEmpty(value))
				{
					if (corpvo.getUnitname().contains(value) || value.contains(corpvo.unitname))	//防止把自己名称放到对方名称里面
					{
						value = null;
					}
				}
			}
		}
		
		return value;
	}
	private String filterDateChar(String value)
	{
		StringBuffer strBuf = new StringBuffer();
		if (!StringUtil.isEmptyWithTrim(value))
		{
			for (char ch : value.toCharArray())
			{
				if (ch == ' ' || ch == ':' || ch == '-' || ch == '/' || ch == '.' || ch >= '0' && ch <= '9')
				{
					strBuf.append(ch);
				}
			}
		}
		return strBuf.length() == 0 ? value : strBuf.toString();
	}
	/**
	 * 仅针对中国银行进行特殊处理
	 * @param vo
	 *
	 * @param unitname
	 */
	private void zgyhSpecialValue(BankStatementVO2 vo, 
			//String bankaccname,
			BankAccountVO bankvo,
			String unitname){
		String code = "";
		String name = "";
		String bankaccname = bankvo == null ? "" : bankvo.getBankaccount();
		unitname = StringUtil.isEmpty(unitname) ? "" : unitname;
		
		String vdef1 = filterBankNo(vo.getVdef1());
		String vdef2 = vo.getVdef2();
		String vdef3 = filterBankNo(vo.getVdef3());
		String vdef4 = vo.getVdef4();
		
		if(StringUtil.isEmpty(bankaccname)){
			if(StringUtil.isEmpty(vdef2) == false && StringUtil.isEmpty(vdef4) == false)
			{
				if(unitname.contains(vdef2) || vdef2.contains(unitname)) {//付款人名称
					code = vdef3;
					name = vdef4;
				}
				else if(unitname.contains(vdef4) || vdef4.contains(unitname)){//收款人名称
					code = vdef1;
					name = vdef2;
				}
				else
				{
					throw new BusinessException(vdef2 + " 和 " + vdef4 + "都不是导入对账单的公司，请检查!");
				}
			}
			else if (StringUtil.isEmpty(vdef2) == false)
			{
				if(unitname.contains(vdef2) == false && vdef2.contains(unitname) == false) {//付款人名称
					code = vdef1;
					name = vdef2;
				}
			}
			else if (StringUtil.isEmpty(vdef4) == false)
			{
				if(unitname.contains(vdef4) == false && vdef4.contains(unitname) == false) {//付款人名称
					code = vdef3;
					name = vdef4;
				}
			}
			
		}else{
			if(bankaccname.equals(vdef1)){//付款人账号
				code = vo.getVdef3();
				name = vo.getVdef4();
			}
			if(bankaccname.equals(vdef3)){//收款人账号
				code = vdef1;
				name = vdef2;
			}
		}
		
//		if(StringUtil.isEmpty(code) && StringUtil.isEmpty(name)){
//			if(StringUtil.isEmpty(vdef1)){
//				if(!StringUtil.isEmpty(vdef3)){
//					code = vdef3;
//				}
//			}else{
//				if(StringUtil.isEmpty(vdef3)){
//					code = vdef1;
//				}else{
//					code = vdef1 + "," + vdef3;
//				}
//			}
//			
//			if(StringUtil.isEmpty(vdef2)){
//				if(!StringUtil.isEmpty(vdef4)){
//					name = vdef4;
//				}
//			}else{
//				if(StringUtil.isEmpty(vdef4)){
//					name = vdef2;
//				}else{
//					name = vdef2 + "," + vdef4;
//				}
//			}
//		}
		
		vo.setOthaccountcode(code);//对方账户
		vo.setOthaccountname(name);//对方账户名称
	}
	
	private void dealSpecialValue(BankStatementVO2 vo, int stype){
		if(stype == BankStatementVO2.SOURCE_6){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyyMMddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}if(stype == BankStatementVO2.SOURCE_46){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyyMMddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}else if(stype == BankStatementVO2.SOURCE_43){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyyMMddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}else if(stype == BankStatementVO2.SOURCE_8){
			String value = vo.getVdef1();//重庆农村商业银行  汇出汇入字段
			if(StringUtil.isEmpty(value)){
				vo.setZcje(null);
				vo.setSyje(null);
				return;
			}
			if("汇入".equals(value)){//已与产品确认，该字段只会有 汇入  汇出两种标识
				vo.setZcje(null);
			}else if("汇出".equals(value)){
				vo.setSyje(null);
			}else{
				vo.setZcje(null);
				vo.setSyje(null);
			}
		}else if(stype == BankStatementVO2.SOURCE_9){
			String value = vo.getVdef1();//借贷标志 收入、支出字段
			if(StringUtil.isEmpty(value)){
				vo.setZcje(null);
				vo.setSyje(null);
			}
			if("收入".equals(value)){
				vo.setZcje(null);
			}else if("支出".equals(value)){
				vo.setSyje(null);
			}else{
				vo.setZcje(null);
				vo.setSyje(null);
			}
		}else if(stype == BankStatementVO2.SOURCE_20){
			String value = vo.getVdef1();//收入、支出字段
			if(StringUtil.isEmpty(value)){
				vo.setZcje(null);
				vo.setSyje(null);
			}
			if("收入".equals(value)){
				vo.setZcje(null);
			}else if("支出".equals(value)){
				vo.setSyje(null);
			}else{
				vo.setZcje(null);
				vo.setSyje(null);
			}
		}else if(stype == BankStatementVO2.SOURCE_21){
			String value = vo.getVdef1();//收入/支出字段
			if(StringUtil.isEmpty(value)){
				vo.setZcje(null);
				vo.setSyje(null);
			}
			if("收入".equals(value)){
				vo.setZcje(null);
			}else if("支出".equals(value)){
				vo.setSyje(null);
			}else{
				vo.setZcje(null);
				vo.setSyje(null);
			}
		}else if(stype == BankStatementVO2.SOURCE_23){//昆仑银行
			String value = vo.getVdef1();//收入/支出字段
			if(StringUtil.isEmpty(value)){
				vo.setZcje(null);
				vo.setSyje(null);
			}
			if("收入".equals(value)){
				vo.setZcje(null);
				vo.setOthaccountcode(vo.getVdef3());//付款人账号
				vo.setOthaccountname(vo.getVdef2());//付款人户名
			}else if("支出".equals(value)){
				vo.setSyje(null);
				
				vo.setOthaccountcode(vo.getVdef6());//收款人账号
				vo.setOthaccountname(vo.getVdef5());//收款人户名
			}else{
				vo.setZcje(null);
				vo.setSyje(null);
			}
		}else if(stype == BankStatementVO2.SOURCE_10){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyy-MM-ddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}else if(stype == BankStatementVO2.SOURCE_11){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyyMMddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}else if(stype == BankStatementVO2.SOURCE_42){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyyMMddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}else if(stype == BankStatementVO2.SOURCE_12){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyyMMddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}else if(stype == BankStatementVO2.SOURCE_13){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyy-MM-ddhh:mm");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
			String zy = vo.getVdef6();
			if (StringUtil.isEmpty(zy)) {
				zy = vo.getZy();
			} else if (!StringUtil.isEmpty(vo.getZy())) {
				zy += "，" + vo.getZy();
			}
			vo.setZy(zy);
		}else if(stype == BankStatementVO2.SOURCE_14){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyyMMddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}else if(stype == BankStatementVO2.SOURCE_28){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyy-MM-ddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
			
			String value = vo.getVdef1();//收入/支出字段
			if(StringUtil.isEmpty(value)){
				vo.setZcje(null);
				vo.setSyje(null);
			}
			if("贷".equals(value)){
				vo.setZcje(null);
			}else if("借".equals(value)){
				vo.setSyje(null);
			}else{
				vo.setZcje(null);
				vo.setSyje(null);
			}
		}else if(stype == BankStatementVO2.SOURCE_30){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyy-MM-ddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}else if(stype == BankStatementVO2.SOURCE_33){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyy-MM-ddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}else if(stype == BankStatementVO2.SOURCE_35){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyy-MM-ddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}else if(stype == BankStatementVO2.SOURCE_49){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyy-MM-ddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}else if(stype == BankStatementVO2.SOURCE_51){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyy-MM-ddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}else if(stype == BankStatementVO2.SOURCE_53){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyyMMddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
		}else if(stype == BankStatementVO2.SOURCE_54){
			if(!StringUtil.isEmpty(vo.getTempvalue())){
				try {
					Date date = DateUtils.parse(vo.getTempvalue(), "yyyy-MM-ddhh:mm:ss");
					String datestr = DateUtils.format(date, "yyyy-MM-dd");
					vo.setTradingdate(new DZFDate(datestr));
				} catch (Exception e) {
					log.error("错误",e);
				}
			}
			
			DZFDouble srje = vo.getSyje();
			if(SafeCompute.add(DZFDouble.ZERO_DBL, srje).doubleValue() >= 0){
				vo.setZcje(null);
			}else if(SafeCompute.add(DZFDouble.ZERO_DBL, srje).doubleValue() < 0){
				vo.setSyje(null);
				vo.setZcje(SafeCompute.sub(DZFDouble.ZERO_DBL, srje));
			}
		}else if(stype == BankStatementVO2.SOURCE_57){
			
			String value = vo.getVdef1();//收入/支出字段

			if("收入".equals(value)){
				vo.setZcje(null);
			}else if("支出".equals(value)){
				vo.setSyje(null);
			}else{
				vo.setZcje(null);
				vo.setSyje(null);
			}
		}
	}
	
	private String getExcelCellValue(Cell cell) {
		String ret = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 格式化日期字符串
			if (cell == null) {
				ret = null;
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
				ret = cell.getRichStringCellValue().getString();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
				ret = "" + Double.valueOf(cell.getNumericCellValue()).doubleValue();
//				 小数不可用这样格式，只为了凭证编码格式
				java.text.DecimalFormat formatter = new java.text.DecimalFormat("#############.##");
				formatter.setRoundingMode(RoundingMode.HALF_UP);
				if ("General".equals(cell.getCellStyle().getDataFormatString())) {
					ret = formatter.format(cell.getNumericCellValue());
				} else if(cell.getCellStyle().getDataFormatString().indexOf(".")>=0){
					ret = formatter.format(BigDecimal.valueOf(cell.getNumericCellValue()));
				}else{
					ret = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
				}
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
				String value1 = null;
				try {
					java.text.DecimalFormat formatter = new java.text.DecimalFormat("#############.##");
					value1 = formatter.format(cell.getNumericCellValue());
					ret = value1;
				}
				catch (Exception e)
				{}
				if (StringUtil.isEmpty(value1) || "0.00".equals(ret))
				{
					try {
						FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();  
					    CellValue cellValue = evaluator.evaluate(cell);
					    ret = String.valueOf(cellValue.getNumberValue());
					}
					catch (StackOverflowError se)
					{
						throw new StackOverflowError("表格中第 " + (cell.getRow().getRowNum() + 1) + " 行第" + (cell.getColumnIndex() + 1) + " 列的公式 " + cell.getCellFormula() + "不正常，请检查");
					}
					catch (Exception e)
					{}
				}
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
				ret = "" + cell.getErrorCellValue();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
				ret = "" + cell.getBooleanCellValue();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
				ret = null;
			}
		} catch (StackOverflowError sofe)	{
			throw new BusinessException(sofe.getMessage());
		} catch (Exception ex) {
			log.error("错误",ex);
			ret = null;
		}
		return OcrUtil.filterCorpName(ret);
	}
	
	private Object[][] getImpConfigObj(){
		String sql = " select * from ynt_vatimpconfset where stype = ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(IBillManageConstants.HEBING_YHDZF);
		List<VatImpConfSetVO> ll = (List<VatImpConfSetVO>) singleObjectBO.executeQuery(sql, 
				sp, new BeanListProcessor(VatImpConfSetVO.class));

		Object[][] objs = null;
		int length = ll == null || ll.size() == 0 ? 0 : ll.size();
		if(length > 0){
			objs = new Object[length][3];
			for(int i = 0; i < length; i++){
				VatImpConfSetVO vo = ll.get(i);
				Object[] obj = { 1000, vo.getColname(), vo.getFiled(), vo.getRequire() };
				objs[i] = obj;
				
			}
		}
		
		return objs;
	}
	
	public synchronized Map<Integer, Object[][]> getStyleMap(){
		if (STYLE == null) {
			STYLE = new HashMap<Integer, Object[][]>();
			Object[][] obj1= new Object[][]{
				{0, "交易日期", "tradingdate"},
				{1, "摘要", "zy"},
				{2, "收入金额", "syje"},
				{3, "支出金额", "zcje"},
				{4, "业务类型", "busitypetempname"},
				{5, "对方账户名称", "othaccountname"},
				{6, "对方账户", "othaccountcode"},
				{7, "余额", "ye"}
			};
			
			Object[][] obj2 = new Object[][]{
				{ 0, "交易类型", "vdef5" },
				{10, "交易日期", "tradingdate"},
				{23, "摘要", "zy"},
				{24, "用途", "zy"},
				{25, "交易附言", "zy"},
				{13, "交易金额", "syje"},//收入金额  取正数
				{13, "交易金额", "zcje"},//支出金额    取负数
				
//				{4, "付款人账号", "othaccountcode"},//对方账户   之前版本
//				{5, "付款人名称", "othaccountname"},//对方账户名称
//				{8, "收款人账号", "othaccountcode"},
//				{9, "收款人名称", "othaccountname"},//对方账户名称
				
				{4, "付款人账号", "vdef1"},//对方账户
				{5, "付款人名称", "vdef2"},//对方账户名称
				{8, "收款人账号", "vdef3"},
				{9, "收款人名称", "vdef4"},//对方账户名称
				
				{14, "交易后余额", "ye"}
			};
			
			Object[][] obj3 = new Object[][]{
				{0, "日期", "tradingdate"},
				{6, "摘要", "zy"},
				{8, "贷方发生额", "syje"},//收入金额
				{7, "借方发生额", "zcje"},//支出金额
				{4, "对方户名", "othaccountname"},//对方账户名称
				{5, "对方账号", "othaccountcode"},
				{9, "余额", "ye"}
			};
			
			Object[][] obj4 = new Object[][]{
				{1, "交易日期", "tradingdate"},
				{7, "交易摘要", "zy"},
				{2, "收入金额", "syje"},
				{3, "支出金额", "zcje"},
				{6, "对方户名", "othaccountname"},
				{5, "对方账号", "othaccountcode"},
				{4, "余额", "ye"}
			};
			
			Object[][] obj5 = new Object[][]{
				{1, "交易日期", "tradingdate"},
				{8, "摘要", "zy"},
				{3, "收入", "syje"},
				{4, "支出", "zcje"},
				{12, "对方户名", "othaccountname"},
				{10, "对方账号", "othaccountcode"},
				{6, "余额", "ye"}
			};
			
			Object[][] obj6 = new Object[][]{//中国农业银行
				{0, "交易时间", "tempvalue"},//20170901 17:37:04 农行格式需要二次转化,故先临时存放，
											//而后处理放置在tradingdate字段
				{8, "交易用途", "zy"},
				{1, "收入金额", "syje"},
				{2, "支出金额", "zcje"},
				{7, "对方户名", "othaccountname"},
				{6, "对方账号", "othaccountcode"},
				{3, "账户余额", "ye"},
				
				{4, "交易行名", "vdef1"},
				{5, "对方省市", "vdef2"}
			};
			
			Object[][] obj7 = new Object[][]{//重庆银行
				{0, "交易日期", "tradingdate"},
				{14, "摘要（备", "zy"},
				{4, "收入", "syje"},
				{10, "支出", "zcje"},
				{9, "收款账户名", "othaccountname"},
				{6, "收款账号", ""},
				{7, "", "othaccountcode"},
				{11, "余额", "ye"},
				
				{1, "付款账号", "vdef1"},
				{2, "付款账户名", "vdef2"},
				{12, "交易渠道", "vdef3"}
			};
			
			Object[][] obj8 = new Object[][]{//重庆农村商业银行
				{2, "交易日期", "tradingdate"},
				{22, "业务摘要", "zy"},
				{7, "交易金额", "syje"},
				{7, "交易金额", "zcje"},
				{13, "对方账户名", "othaccountname"},
				{14, "对方账号", "othaccountcode"},
				{8, "账户余额", "ye"},
				
				{5, "汇出汇入", "vdef1"},
				{6, "现转标志", "vdef2"},
				{9, "交易开户行", "vdef3"},
				{10, "交易账户名", "vdef4"},
				{11, "交易账号", "vdef5"},
				{12, "对方银行", "vdef6"},
				{15, "渠道", "vdef7"},
				{16, "用途", "vdef8"},
				{17, "备注", "vdef9"},
				{18, "附言", "vdef10"},
				{19, "交易信息", "vdef11"},
				{20, "交易描述", "vdef12"},
				{21, "交易说明", "vdef13"}
				
			};
			
			Object[][] obj9 = new Object[][]{//中国农业银行——苏州支行
				{0, "发生日期", "tradingdate"},
				{1, "本方账号", "vdef2"},
				{2, "对方账号", "othaccountcode"},
				{3, "对方户名", "othaccountname"},
				{4, "借贷标志", "vdef1"},
				{5, "发生金额", "syje"},
				{5, "发生金额", "zcje"},
				{6, "账户余额", "ye"},
				{7, "附言", "zy"}
			};
			
			Object[][] obj10 = new Object[][]{//苏州银行
				{0, "交易日期", "tempvalue"},//20170901 17:37:04 需要二次转化，故先临时存放
											//而后放在tradingdate字段
				{1, "收入", "syje"},
				{2, "支出", "zcje"},
				{3, "余额", "ye"},
				{4, "对方账号", "othaccountcode"},
				{5, "对方户名", "othaccountname"},
				{6, "对方行名", "vdef1"},
				{7, "摘要", "zy"}
				
			};
			
			Object[][] obj11 = new Object[][]{//中国建设银行
				{0, "交易时间", "tempvalue"},//需二次转化
				{1, "借方发生额/元(支取)", "zcje"},
				{2, "贷方发生额/元(收入)", "syje"},
				{3, "余额", "ye"},
				{4, "币种", "vdef2"},
				{5, "对方户名", "othaccountname"},
				{6, "对方账号", "othaccountcode"},
				{7, "对方开户机构", "vdef1"},
				{8, "记账日期", "vdef3"},
				{9, "摘要", "zy"},
				{10, "备注", "zy"},
				{11, "账户明细编号-交易流水号", "vdef4"},
				{12, "企业流水号", "vdef5"},
				{13, "凭证种类", "vdef6"},
				{14, "凭证号", "vdef7"}
			};
			
			Object[][] obj12 = new Object[][]{//中国建设银行-苏州支行
				{2, "交易时间", "tempvalue"},//需二次转化
				{3, "借方发生额（支取）", "zcje"},
				{4, "贷方发生额（收入）", "syje"},
				{5, "余额", "ye"},
				{6, "币种", "vdef2"},
				{7, "对方户名", "othaccountname"},
				{8, "对方账号", "othaccountcode"},
				{9, "对方开户机构", "vdef1"},
				{10, "记账日期", "vdef3"},
				{11, "摘要", "zy"},
				{12, "备注", "zy"},
				{13, "账户明细编号-交易流水号", "vdef4"},
				{14, "企业流水号", "vdef5"},
				{15, "凭证种类", "vdef6"},
				{16, "凭证号", "vdef7"},
				{0, "账号", "vdef8"},
				{1, "账户名称", "vdef9"}
			};
			
			Object[][] obj13 = new Object[][] {// 青岛银行
					{ 0, "流水号", "vdef5" },
					{ 1, "交易日期", "tempvalue" },//需二次转化
					{ 2, "账号", "vdef8" },
					{ 3, "币种", "vdef2" },
					{ 4, "交易类型", "vdef6" },
					{ 5, "收入金额", "syje" },
					{ 6, "支出金额", "zcje" },
					{ 7, "余额", "ye" },
					{ 8, "对方账号", "othaccountcode" },
					{ 9, "对方户名", "othaccountname" },
					{ 10, "备注", "zy" },
					{ 11, "支票号", "vdef3" }
			};
			
			Object[][] obj14 = new Object[][] {//兴业银行青岛分行
				    { 0, "交易日期", "tempvalue" },//需二次转化
				    { 1, "摘要", "zy" },
				    { 2, "凭证代号", "vdef3" },
				    { 3, "支出金额", "zcje" },
				    { 4, "收入金额", "syje" },
				    { 5, "收支方向", "vdef1" },
				    { 6, "余额", "ye" }
			};
			
			Object[][] obj15 = new Object[][] {//渤海银行
			    { 0, "交易日期 ", "tradingdate" },
			    { 1, "交易时间", "vdef3" },
			    { 2, "借方发生额", "zcje" },
			    { 3, "贷方发生额", "syje" },
			    { 4, "账户余额", "ye" },
			    { 5, "对方名称", "othaccountname" },
			    { 6, "对方账号", "othaccountcode" },
			    { 7, "对方行名称", "vdef4" },
			    { 8, "摘要", "zy" },
			    { 9, "附言", "zy" }
			};
			
			Object[][] obj16 = new Object[][] {//交通银行
				{ 0, "序号", "" },
			    { 1, "交易日期", "tradingdate" },
			    { 2, "交易名称", "vdef1" },
			    { 3, "凭证种类", "vdef6" },
			    { 4, "凭证号码", "vdef7" },
			    { 5, "借方发生额", "zcje" },
			    { 6, "贷方发生额", "syje" },
			    { 7, "余额", "ye" },
			    { 8, "卡号", "vdef8" },
			    { 9, "交易地点", "vdef9" },
			    {10, "对方账号", "othaccountcode" },
			    {11, "对方户名", "othaccountname" },
			    {12, "对方行名", "vdef4" },
			    {13, "摘要", "zy" },
			    {14, "流水号", "vdef4" }
			};
			
			Object[][] obj17 = new Object[][] {//青岛农村商业银行
			    { 1, "交易日期", "tradingdate" },
			    { 2, "交易地点", "vdef2"},
			    { 3, "收入", "syje" },
			    { 4, "支出", "zcje" },
			    { 5, "交易渠道", "vdef3" },
			    { 6, "余额", "ye" },
			    { 7, "对方户名", "othaccountname" },
			    { 8, "对方账户", "othaccountcode" },
			    { 9, "摘要", "zy" }
			};
			
			Object[][] obj18 = new Object[][] {//招商银行杭州分行
			    { 0, "交易日", "tradingdate" },
			    { 1, "交易时间", "vdef1" },
			    { 2, "起息日", "vdef2" },
			    { 3, "交易类型", "vdef3" },
			    { 4, "借方金额", "zcje" },
			    { 5, "贷方金额", "syje" },
			    { 6, "余额", "ye" },
			    { 7, "摘要", "zy" },
			    { 8, "流水号", "vdef4" },
			    { 10, "业务名称", "zy" }
			};
			
			Object[][] obj19 = new Object[][] {//民生银行
			    { 0, "交易日期", "tradingdate" },
			    { 1, "主机交易流水号", "vdef4" },
			    { 2, "借方发生额", "zcje" },
			    { 3, "贷方发生额", "syje" },
			    { 4, "账户余额", "ye" },
			    { 5, "凭证号", "vdef3" },
			    { 6, "摘要", "zy" },
			    { 7, "对方账号", "othaccountcode" },
			    { 8, "对方账号名称", "othaccountname" },
			    { 9, "对方开户行", "vdef2" },
			    {10, "交易时间", "vdef1" }
			};
			//////////
			
			Object[][] obj20 = new Object[][] {//浙江民泰商业银行
			    { 2, "交易日期", "tradingdate" },
			    { 6, "收入/支出", "vdef1" },
			    { 7, "交易金额", "syje" },
			    { 7, "交易金额", "zcje" },
			    { 8, "余额", "ye" },
			    { 9, "摘要", "zy" }
			};
			
			Object[][] obj21 = new Object[][] {//成都农商银行
				{ 1, "交易时间", "tradingdate" },
			    { 2, "收入/支出", "vdef1" },
			    { 3, "交易金额", "syje" },
			    { 3, "交易金额", "zcje" },
			    { 4, "余额", "ye" },
			    { 6, "对方账号", "othaccountcode" },
			    { 7, "对方户名", "othaccountname" },
			    { 8, "摘要", "zy" }
			};
			
			Object[][] obj22 = new Object[][] {//库尔勒银行
			    { 0, "交易时间", "tradingdate" },
			    { 1, "支出金额", "zcje" },
			    { 2, "收入金额", "syje" },
			    { 3, "对方帐号", "othaccountcode" },
			    { 4, "对方账户名", "othaccountname" },
			    { 5, "余额", "ye" },
			    { 7, "摘要", "zy" }
			};
			
			Object[][] obj23 = new Object[][] {//昆仑银行
			    { 0, "付款人户名", "vdef2" },
			    { 1, "付款人账号", "vdef3" },
			    { 2, "付款人开户银行", "vdef4" },
			    { 3, "收款人户名", "vdef5" },
			    { 4, "收款人账号", "vdef6" },
			    { 5, "收款人开户银行", "vdef7" },
			    { 6, "金额", "syje" },
			    { 6, "金额", "zcje" },
			    { 9, "收入/支出", "vdef1" },
			    { 10, "摘要", "zy" },
			    { 12, "记录网点", "vdef8" },
			    { 14, "记账日期", "tradingdate" },
			    
			};
			
			Object[][] obj24 = new Object[][] {//北京银行
				{ 0, "序号", "" },
			    { 3, "记账日期", "tradingdate" },
			    { 5, "摘要", "zy" },
			    { 6, "借方发生额", "zcje" },
			    { 7, "贷方发生额", "syje" },
			    { 8, "余额", "ye" },
			    { 11, "对方户名", "othaccountname" },
			    { 12, "对方账户", "othaccountcode" }
			};
			
			Object[][] obj25 = new Object[][] {//华夏银行
			    { 0, "交易日期", "tradingdate" },
			    { 2, "交易金额(借)", "zcje" },
			    { 3, "交易金额(贷)", "syje" },
			    { 5, "账户余额", "ye" },
			    { 6, "摘要", "zy" },
			    { 7, "对方账号", "othaccountcode" },
			    { 8, "对方账号名称", "othaccountname" },
			    { 9, "对方行名", "vdef4" }
			};
			
			Object[][] obj26 = new Object[][] {//中国建设银行成都分行
			    { 1, "交易日期", "tradingdate" },
			    { 5, "借方发生额/元", "zcje" },
			    { 6, "贷方发生额/元", "syje" },
			    { 7, "余额", "ye" },
			    { 8, "对方户名", "othaccountname" },
			    { 9, "对方账号", "othaccountcode" },
			    { 10, "摘要", "zy" },
			};
			
			Object[][] obj27 = new Object[][] {//兰州银行
			    { 0, "交易日期", "tradingdate" },
			    { 2, "支出", "zcje" },
			    { 3, "收入", "syje" },
			    { 4, "余额", "ye" },
			    { 5, "对方账号", "othaccountcode" },
			    { 6, "对方户名", "othaccountname" },
			    { 7, "对方银行行名", "vdef4" },
			    { 8, "摘要", "zy" }
			};
			
			Object[][] obj28 = new Object[][] {//交通银行上海分行
			    { 0, "交易时间", "tempvalue" },//需二次转化
			    { 1, "摘要", "zy" },
			    { 5, "发生额", "zcje" },
			    { 5, "发生额", "syje" },
			    { 7, "余额", "ye" },
			    { 8, "对方账号", "othaccountcode" },
			    { 9, "对方户名", "othaccountname" },
			    { 10, "借贷标志", "vdef1" }
			};
			
			Object[][] obj29 = new Object[][] {//南京银行上海分行
			    { 0, "交易日期", "tradingdate" },
			    { 1, "摘要", "zy" },
			    { 2, "借方发生额", "zcje" },
			    { 3, "贷方发生额", "syje" },
			    { 4, "余额", "ye" },
			    { 7, "对方户名", "othaccountname" },
			    { 8, "对方账号", "othaccountcode" },
			    { 9, "柜员流水号", "vdef2" },
			    { 10, "备注信息", "vdef3" }
			};
			
			Object[][] obj30 = new Object[][] {//工商银行上海分行
			    { 1, "本方账号", "vdef8" },
			    { 2, "对方账号", "othaccountcode" },
			    { 3, "交易时间", "tempvalue" },//需二次转化
			    { 4, "借/贷", "vdef1" },
			    { 5, "借方发生额", "zcje" },
			    { 6, "贷方发生额", "syje" },
			    { 7, "对方行号", "vdef4" },
			    { 8, "摘要", "zy" },
			    { 10, "对方单位名称", "othaccountname" },
			    { 11, "余额", "ye" },
			    { 12, "个性化信息", "vdef5" },
			};
			
			Object[][] obj31 = new Object[][] {//上海银行
				{ 0, "序号", "" },
			    { 1, "交易日期", "tradingdate" },
			    { 2, "交易时间", "vdef4" },
			    { 3, "借贷", "vdef1" },
			    { 4, "借方发生额", "zcje" },
			    { 5, "贷方发生额", "syje" },
			    { 6, "余额", "ye" },
			    { 7, "摘要", "zy" },
			    { 10, "用途", "zy" },
			    { 8, "对方账号", "othaccountcode" },
			    { 9, "对方户名", "othaccountname" },
			};
			
			Object[][] obj32 = new Object[][] {//中国民生银行成都分行
				{ 0, "交易时间", "tradingdate" },
				{ 1, "支出金额", "zcje" },
				{ 2, "存入金额", "syje" },
				{ 3, "账户余额", "ye" },
				{ 4, "对方账号", "othaccountcode" },
				{ 5, "对方名称", "othaccountname" },
				{ 6, "对方开户行", "vdef3" },
				{ 7, "交易方式", "vdef2" },
				{ 8, "摘要", "zy" }
			   
			};
			
			Object[][] obj33 = new Object[][] {//交通银行长春地区
				{ 0, "交易时间", "tempvalue" },
				{ 1, "摘要", "zy" },
				{ 5, "借方发生额", "zcje" },
				{ 6, "贷方发生额", "syje" },
				{ 8, "余额", "ye" },
				{ 9, "对方账号", "othaccountcode" },
				{ 10, "对方户名", "othaccountname" }
			   
			};
			
			Object[][] obj34 = new Object[][]{//工商银行重庆分行
				{0, "日期", "tradingdate"},
				{6, "摘要", "zy"},
				{8, "贷方发生额", "syje"},//收入金额
				{7, "借方发生额", "zcje"},//支出金额
				{4, "对方户名", "othaccountname"},//对方账户名称
				{5, "对方账号", "othaccountcode"},
				{9, "余额", "ye"}
			};
			
			Object[][] obj35 = new Object[][] {//工商银行江西分行
			    { 1, "本方账号", "vdef8" },
			    { 2, "对方账号", "othaccountcode" },
			    { 3, "交易时间", "tempvalue" },//需二次转化
			    { 4, "借/贷", "vdef1" },
			    { 5, "借方发生额", "zcje" },
			    { 6, "贷方发生额", "syje" },
			    { 7, "对方行号", "vdef4" },
			    { 8, "摘要", "zy" },
			    { 10, "对方单位名称", "othaccountname" },
			    { 11, "余额", "ye" },
			    { 12, "个性化信息", "vdef5" },
			};
			
			Object[][] obj36 = new Object[][]{//工商银行苏州分行
				{0, "日期", "tradingdate"},
				{6, "摘要", "zy"},
				{8, "贷方发生额", "syje"},//收入金额
				{7, "借方发生额", "zcje"},//支出金额
				{4, "对方户名", "othaccountname"},//对方账户名称
				{5, "对方账号", "othaccountcode"},
				{9, "余额", "ye"}
			};
			
			Object[][] obj37 = new Object[][]{//工商银行成都分行
				{0, "日期", "tradingdate"},
				{6, "摘要", "zy"},
				{8, "贷方发生额", "syje"},//收入金额
				{7, "借方发生额", "zcje"},//支出金额
				{4, "对方户名", "othaccountname"},//对方账户名称
				{5, "对方账号", "othaccountcode"},
				{9, "余额", "ye"}
			};
			
			Object[][] obj38 = new Object[][]{//工商银行泉州分行
				{0, "日期", "tradingdate"},
				{6, "摘要", "zy"},
				{8, "贷方发生额", "syje"},//收入金额
				{7, "借方发生额", "zcje"},//支出金额
				{4, "对方户名", "othaccountname"},//对方账户名称
				{5, "对方账号", "othaccountcode"},
				{9, "余额", "ye"}
			};
			
			Object[][] obj39 = new Object[][]{//中国银行上海分行
				{ 0, "交易类型", "vdef5" },
				{10, "交易日期", "tradingdate"},
				{23, "摘要", "zy"},
				{24, "用途", "zy"},
				{25, "交易附言", "zy"},
				{13, "交易金额", "syje"},//收入金额  取正数
				{13, "交易金额", "zcje"},//支出金额    取负数
				{4, "付款人账号", "vdef1"},//对方账户
				{5, "付款人名称", "vdef2"},//对方账户名称
				{8, "收款人账号", "vdef3"},
				{9, "收款人名称", "vdef4"},//对方账户名称
				
				{14, "交易后余额", "ye"}
			};
			
			Object[][] obj40 = new Object[][]{//中国银行宿迁分行
				{ 0, "交易类型", "vdef5" },
				{10, "交易日期", "tradingdate"},
				{23, "摘要", "zy"},
				{24, "用途", "zy"},
				{25, "交易附言", "zy"},
				{13, "交易金额", "syje"},//收入金额  取正数
				{13, "交易金额", "zcje"},//支出金额    取负数
				{4, "付款人账号", "vdef1"},//对方账户
				{5, "付款人名称", "vdef2"},//对方账户名称
				{8, "收款人账号", "vdef3"},
				{9, "收款人名称", "vdef4"},//对方账户名称
				
				{14, "交易后余额", "ye"}
			};
			
			Object[][] obj41 = new Object[][]{//吉林九台农村商业银行
				{0, "交易日期", "tradingdate"},
				{1, "借方发生额", "zcje"},
				{2, "贷方发生额", "syje"},
				{3, "余额", "ye"},
				{4, "对方账号", "othaccountcode"},
				{5, "对方户名", "othaccountname"},
				{7, "交易摘要", "zy"},
			};
			
			Object[][] obj42 = new Object[][]{//中国建设银行临河分行
				{0, "交易时间", "tempvalue"},//需二次转化
				{1, "借方发生额/元(支取)", "zcje"},
				{2, "贷方发生额/元(收入)", "syje"},
				{3, "余额", "ye"},
				{4, "币种", "vdef2"},
				{5, "对方户名", "othaccountname"},
				{6, "对方账号", "othaccountcode"},
				{7, "对方开户机构", "vdef1"},
				{8, "记账日期", "vdef3"},
				{9, "摘要", "zy"},
				{10, "备注", "zy"},
				{11, "账户明细编号-交易流水号", "vdef4"},
				{12, "企业流水号", "vdef5"},
				{13, "凭证种类", "vdef6"},
				{14, "凭证号", "vdef7"}
			};
			
			Object[][] obj43 = new Object[][]{//中国农业银行临河分行
				{0, "交易时间", "tempvalue"},//需要二次转化
				{8, "交易用途", "zy"},
				{1, "收入金额", "syje"},
				{2, "支出金额", "zcje"},
				{7, "对方户名", "othaccountname"},
				{6, "对方账号", "othaccountcode"},
				{3, "账户余额", "ye"},
				{4, "交易行名", "vdef1"},
				{5, "对方省市", "vdef2"}
			};
			
			Object[][] obj44 = new Object[][]{//中国银行临河分行
				{ 0, "交易类型", "vdef5" },
				{10, "交易日期", "tradingdate"},
				{23, "摘要", "zy"},
				{24, "用途", "zy"},
				{25, "交易附言", "zy"},
				{13, "交易金额", "syje"},//收入金额  取正数
				{13, "交易金额", "zcje"},//支出金额    取负数
				{4, "付款人账号", "vdef1"},//对方账户
				{5, "付款人名称", "vdef2"},//对方账户名称
				{8, "收款人账号", "vdef3"},
				{9, "收款人名称", "vdef4"},//对方账户名称
				{14, "交易后余额", "ye"}
			};
			
			Object[][] obj45 = new Object[][]{//工商银行石狮分行
				{0, "日期", "tradingdate"},
				{6, "摘要", "zy"},
				{8, "贷方发生额", "syje"},//收入金额
				{7, "借方发生额", "zcje"},//支出金额
				{4, "对方户名", "othaccountname"},//对方账户名称
				{5, "对方账号", "othaccountcode"},
				{9, "余额", "ye"}
			};
			
			Object[][] obj46 = new Object[][]{//中国农业银行乌鲁木齐分行
				{0, "交易时间", "tempvalue"},//20170901 17:37:04 农行格式需要二次转化,故先临时存放，
											//而后处理放置在tradingdate字段
				{8, "交易用途", "zy"},
				{1, "收入金额", "syje"},
				{2, "支出金额", "zcje"},
				{7, "对方户名", "othaccountname"},
				{6, "对方账号", "othaccountcode"},
				{3, "账户余额", "ye"},
				{4, "交易行名", "vdef1"},
				{5, "对方省市", "vdef2"}
			};
			
			Object[][] obj47 = new Object[][] {//中国民生银行太原分行
				{ 0, "交易日期", "tradingdate" },
				{ 2, "借方发生额", "zcje" },
				{ 3, "贷方发生额", "syje" },
				{ 4, "账户余额", "ye" },
				{ 7, "对方账号", "othaccountcode" },
				{ 8, "对方账号名称", "othaccountname" },
				{ 9, "对方开户行", "vdef3" },
				{ 10, "交易时间", "vdef2" },
				{ 6, "摘要", "zy" }
			   
			};
			
			Object[][] obj48 = new Object[][] {//平安银行深圳分行
				{ 0, "交易日期", "tradingdate" },
				{ 1, "账号", "vdef8" },
				{ 2, "借", "zcje" },
				{ 3, "贷", "syje" },
				{ 4, "账户余额", "ye" },
				{ 5, "对方账户", "othaccountcode" },
				{ 6, "对方账户名称", "othaccountname" },
				{ 9, "摘要", "zy" },
				{ 10, "用途", "zy" }
			};
			
			Object[][] obj49 = new Object[][] {//兴业银行深圳分行
				{ 1, "账号", "vdef8" },
				{ 6, "借方金额", "zcje" },
				{ 7, "贷方金额", "syje" },
				{ 8, "账户余额", "ye" },
				{ 9, "摘要", "zy" },
				{ 10, "对方账号", "othaccountcode" },
				{ 11, "对方户名", "othaccountname" },
				{ 14, "交易日期", "tempvalue" },//需要二次转化,故先临时存放
				{ 15, "用途", "zy" }
			};
			
			Object[][] obj50 = new Object[][] {//浦东发展银行深圳分行
				{ 0, "交易日期", "tradingdate" },
				{ 3, "借方金额", "zcje" },
				{ 4, "贷方金额", "syje" },
				{ 5, "余额", "ye" },
				{ 6, "对方账号", "othaccountcode" },
				{ 7, "对方户名", "othaccountname" },
				{ 8, "摘要", "zy" }
				
			};
			
			Object[][] obj51 = new Object[][] {//深圳农村商业银行
				{ 0, "序号", "" },
				{ 1, "交易时间", "tempvalue" },//需要二次转化,故先临时存放
				{ 2, "摘要", "zy" },
				{ 3, "收入金额", "syje" },
				{ 4, "支出金额", "zcje" },
				{ 5, "账户余额", "ye" },
				{ 6, "对方户名", "othaccountname" },
				{ 7, "对方账号", "othaccountcode" }
			};
			
			Object[][] obj52 = new Object[][]{//中国银行内蒙古分行
				{ 0, "交易类型", "vdef5" },
				{10, "交易日期", "tradingdate"},
				{23, "摘要", "zy"},
				{24, "用途", "zy"},
				{25, "交易附言", "zy"},
				{13, "交易金额", "syje"},//收入金额  取正数
				{13, "交易金额", "zcje"},//支出金额    取负数
				{4, "付款人账号", "vdef1"},//对方账户
				{5, "付款人名称", "vdef2"},//对方账户名称
				{8, "收款人账号", "vdef3"},
				{9, "收款人名称", "vdef4"},//对方账户名称
				
				{14, "交易后余额", "ye"}
			};
			
			Object[][] obj53 = new Object[][]{//长沙银行
				{ 0, "交易日期", "tempvalue" },//需要二次转化,故先临时存放
				{ 2, "摘要", "zy" },
				{ 3, "发生额", "zcje" },//支出
				{ 4, "", "syje" },//收入
				{ 5, "账户余额", "ye" },
				{ 6, "对方账号", "othaccountcode" },
				{ 7, "对方姓名", "othaccountname" }
			};
			
			Object[][] obj54 = new Object[][]{//济宁银行泰安分行
				{0, "交易时间", "tempvalue"},//需要二次转化,故先临时存放
				{1, "本单位账号", "vdef8"},
				{2, "对方账号", "othaccountcode"},
				{3, "收支(元)", "syje"},//收入金额  取正数
				{3, "收支(元)", "zcje"},//支出金额  取负数
				{6, "对方账户名", "othaccountname"},//对方账户名称
				{4, "余额(元)", "ye"},
				{7, "备注", "zy"}
			};
			
			Object[][] obj55 = new Object[][]{//中国工商银行泰安分行
				{0, "日期", "tradingdate"},
				{6, "摘要", "zy"},
				{8, "贷方发生额", "syje"},//收入金额 
				{7, "借方发生额", "zcje"},//支出金额
				{4, "对方户名", "othaccountname"},//对方账户名称
				{5, "对方账号", "othaccountcode"},
				{9, "余额", "ye"}
			};
			
			Object[][] obj56 = new Object[][]{//长沙平安银行
				{ 0, "交易日期", "tradingdate" },
				{ 1, "账号", "vdef8" },
				{ 2, "借", "zcje" },
				{ 3, "贷", "syje" },
				{ 4, "账户余额", "ye" },
				{ 5, "对方账户", "othaccountcode" },
				{ 6, "对方账户名称", "othaccountname" },
				{ 9, "摘要", "zy" },
				{ 10, "用途", "zy" }
			};
			
			Object[][] obj57 = new Object[][]{//中原银行西峡支行
				{ 5,  "", "" },
				{ 0, "交易日期", "tradingdate" },
				{ 3, "金额", "zcje" },
				{ 3, "金额", "syje" },
				{ 4, "收支状况", "vdef1" },
				{ 7, "余额", "ye" },
				{ 9, "对方户名", "othaccountname" },
				{ 10, "对方账号", "othaccountcode" },
				{ 17, "附言", "zy" }
			};
			
			Object[][] obj58 = new Object[][]{//长沙农商银行
				{ 0, "交易日期", "tradingdate" },
				{ 1, "借方发生额", "zcje" },
				{ 2, "贷方发生额", "syje" },
				{ 3, "余额", "ye" },
				{ 4, "对方账号", "othaccountcode" },
				{ 5, "对方户名", "othaccountname" },
				{ 7, "交易摘要", "zy" },
				{ 8, "摘要内容", "zy" }
			};
			
			STYLE.put(BankStatementVO2.SOURCE_1, obj1);
			STYLE.put(BankStatementVO2.SOURCE_2, obj2);
			STYLE.put(BankStatementVO2.SOURCE_3, obj3);
			STYLE.put(BankStatementVO2.SOURCE_4, obj4);
			STYLE.put(BankStatementVO2.SOURCE_5, obj5);
			STYLE.put(BankStatementVO2.SOURCE_6, obj6);
			STYLE.put(BankStatementVO2.SOURCE_7, obj7);
			STYLE.put(BankStatementVO2.SOURCE_8, obj8);
			STYLE.put(BankStatementVO2.SOURCE_9, obj9); 
			STYLE.put(BankStatementVO2.SOURCE_10, obj10);
			STYLE.put(BankStatementVO2.SOURCE_11, obj11); 
			STYLE.put(BankStatementVO2.SOURCE_12, obj12);
			STYLE.put(BankStatementVO2.SOURCE_13, obj13);
			
			STYLE.put(BankStatementVO2.SOURCE_14, obj14);
			STYLE.put(BankStatementVO2.SOURCE_15, obj15);
			STYLE.put(BankStatementVO2.SOURCE_16, obj16);
			STYLE.put(BankStatementVO2.SOURCE_17, obj17);
			STYLE.put(BankStatementVO2.SOURCE_18, obj18);
			STYLE.put(BankStatementVO2.SOURCE_19, obj19);
			
			STYLE.put(BankStatementVO2.SOURCE_20, obj20);
			STYLE.put(BankStatementVO2.SOURCE_21, obj21);
			STYLE.put(BankStatementVO2.SOURCE_22, obj22);
			STYLE.put(BankStatementVO2.SOURCE_23, obj23);
			STYLE.put(BankStatementVO2.SOURCE_24, obj24);
			STYLE.put(BankStatementVO2.SOURCE_25, obj25);
			STYLE.put(BankStatementVO2.SOURCE_26, obj26);
			
			STYLE.put(BankStatementVO2.SOURCE_27, obj27);
			STYLE.put(BankStatementVO2.SOURCE_28, obj28);
			STYLE.put(BankStatementVO2.SOURCE_29, obj29);
			STYLE.put(BankStatementVO2.SOURCE_30, obj30);
			STYLE.put(BankStatementVO2.SOURCE_31, obj31);
			STYLE.put(BankStatementVO2.SOURCE_32, obj32);
			
			STYLE.put(BankStatementVO2.SOURCE_33, obj33);
			STYLE.put(BankStatementVO2.SOURCE_34, obj34);
			STYLE.put(BankStatementVO2.SOURCE_35, obj35);
			STYLE.put(BankStatementVO2.SOURCE_36, obj36);
			
			STYLE.put(BankStatementVO2.SOURCE_37, obj37);
			STYLE.put(BankStatementVO2.SOURCE_38, obj38);
			STYLE.put(BankStatementVO2.SOURCE_39, obj39);
			STYLE.put(BankStatementVO2.SOURCE_40, obj40);
			STYLE.put(BankStatementVO2.SOURCE_41, obj41);
			
			STYLE.put(BankStatementVO2.SOURCE_42, obj42);
			STYLE.put(BankStatementVO2.SOURCE_43, obj43);
			STYLE.put(BankStatementVO2.SOURCE_44, obj44);
			STYLE.put(BankStatementVO2.SOURCE_45, obj45);
			STYLE.put(BankStatementVO2.SOURCE_46, obj46);
			STYLE.put(BankStatementVO2.SOURCE_47, obj47);
			STYLE.put(BankStatementVO2.SOURCE_48, obj48);
			STYLE.put(BankStatementVO2.SOURCE_49, obj49);
			STYLE.put(BankStatementVO2.SOURCE_50, obj50);
			
			STYLE.put(BankStatementVO2.SOURCE_51, obj51);
			STYLE.put(BankStatementVO2.SOURCE_52, obj52);
			STYLE.put(BankStatementVO2.SOURCE_53, obj53);
			STYLE.put(BankStatementVO2.SOURCE_54, obj54);
			STYLE.put(BankStatementVO2.SOURCE_55, obj55);
			STYLE.put(BankStatementVO2.SOURCE_56, obj56);
			STYLE.put(BankStatementVO2.SOURCE_57, obj57);
			STYLE.put(BankStatementVO2.SOURCE_58, obj58);
		}
		
		return STYLE;
	}

	private static String ISTEMP = "ISTEMP";//暂存标识
	@Override
	public void createPZ(BankStatementVO2 vo,
						 String pk_corp,
						 String userid,
						 String period,
						 BankAccountVO bankAccVO,
						 VatInvoiceSetVO setvo,
						 boolean accway,
						 boolean isT, List<List<Object[]>> levelList, Map<String, Object[]> categoryMap, Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap, Set<String> zyFzhsList
			, Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap, InventorySetVO inventorySetVO, CorpVO corp, Map<String, InventoryAliasVO> fzhsBMMap
			, List<Object> paramList, Map<String, BdCurrencyVO> currMap, Map<String, Object[]> rateMap, Map<String, String> bankAccountMap, Map<String, YntCpaccountVO> accountMap
			, Map<String, AuxiliaryAccountBVO> assistMap, Map<String, List<AccsetVO>> accsetMap, Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map, Map<String, String> jituanSubMap, YntCpaccountVO[] accVOs
			, String tradeCode, String newrule, List<AuxiliaryAccountBVO> chFzhsBodyVOs) throws DZFWarpException {

		CorpVO corpvo = corpService.queryByPk(pk_corp);

		String pk_curr = yntBoPubUtil.getCNYPk();
		
		List<BankStatementVO2> ll = new ArrayList<BankStatementVO2>();
		ll.add(vo);
		checkisGroup(ll, pk_corp);
		if(StringUtils.isEmpty(vo.getPk_model_h())){
			throw new BusinessException("业务类型为空,请重新选择业务类型");
		}
		
			int ifptype  = VATInvoiceTypeConst.VAT_BANK_INVOICE;//银行回单
			
			TzpzHVO headVO = new TzpzHVO();
			headVO.setIfptype(ifptype);
			headVO.setFp_style(null);
			//生成凭证			
			Map<String, Map<String, Object>> checkMsgMap=new HashMap<String, Map<String, Object>>();
			List<OcrInvoiceVO> invoiceList = changeToOcr(ll, pk_corp);
			List<TzpzHVO> tzpzhvoList = zncsVoucher.processGeneralTzpzVOsByInvoice(invoiceList, period, pk_corp, userid, checkMsgMap, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
			//取出子类
			List<TzpzBVO> tblist = Arrays.asList((TzpzBVO[])tzpzhvoList.get(0).getChildren());
			for (TzpzBVO tzpzBVO : tblist) {
				tzpzBVO.setVdef6(vo.getTradingdate().toString());
			}
			vo.setCount(1);
			headVO = createTzpzHVO(headVO, ll, pk_corp, userid, pk_curr, vo.getTotalmny(), accway,setvo);
			
			//设置图片组号
			if(!StringUtil.isEmpty(vo.getSourcebillid())){
				headVO.setPk_image_group(vo.getPk_image_group());
			}
			
			tblist = constructItems(tblist, setvo, vo, pk_corp);
			
			headVO.setChildren(tblist.toArray(new TzpzBVO[0]));
//			istemp = isTempMap.get(ISTEMP);
//			if(istemp || isT){
			if(isT){
				createTempPZ(headVO, new BankStatementVO2[]{ vo }, pk_corp);
			}else{
				headVO = voucher.saveVoucher(corpvo, headVO);
			}
			
	}
	
	/*private OcrImageLibraryVO changeOcrInvoiceVO(BankStatementVO2 vo,
			DcModelHVO mvo,
			String pk_corp,
			BankAccountVO accvo,
			int ifptype,
			int fp_style){
		OcrImageLibraryVO imageVO = new OcrImageLibraryVO();
//		imageVO.setVinvoicecode();
//		imageVO.setVinvoiceno();
		imageVO.setDinvoicedate(vo.getTradingdate().toString());
//		imageVO.setInvoicetype();
		imageVO.setItype(ifptype);
//		imageVO.setNmny();
//		imageVO.setNtaxnmny();
		
		String vsaleacc;
		if(accvo == null){
			vsaleacc = "默认账户";
		}else{
			vsaleacc = accvo.getBankname();//银行名称
		}
		imageVO.setVsaleopenacc(vsaleacc);
		
		if(SafeCompute.add(DZFDouble.ZERO_DBL, vo.getZcje()).doubleValue() > 0){
			imageVO.setNtotaltax(vo.getZcje().toString());
//			imageVO.setVsaletaxno(vo.getOthaccountcode());
//			imageVO.setVsaleopenacc(vo.getOthaccountcode());
			imageVO.setVsalename(vo.getOthaccountname());
			imageVO.setItype(VATInvoiceTypeConst.VAT_PAY_INVOICE);
//			if(accvo != null){
//				imageVO.setVpuropenacc(accvo.getBankaccount());
//			}
		}else if(SafeCompute.add(DZFDouble.ZERO_DBL, vo.getSyje()).doubleValue() > 0){
			imageVO.setNtotaltax(vo.getSyje().toString());
//			imageVO.setVpurchtaxno(vo.getOthaccountcode());
//			imageVO.setVpuropenacc(vo.getOthaccountcode());
			imageVO.setVpurchname(vo.getOthaccountname());
			imageVO.setItype(VATInvoiceTypeConst.VAT_RECE_INVOICE);
//			if(accvo != null){
//				imageVO.setVsaleopenacc(accvo.getBankname());
//			}
		}
//		imageVO.setNtotaltax();
//		imageVO.setVpurchname();
//		imageVO.setVpurchtaxno();
//		imageVO.setVpuropenacc();
//		imageVO.setVpurphoneaddr();
//		imageVO.setVpurbankname();
//		imageVO.setVsalename();
//		imageVO.setVsaletaxno();
//		imageVO.setVsaleopenacc();
//		imageVO.setVsalephoneaddr();
//		imageVO.setVsalebankname();
//		imageVO.setInvname();
//		imageVO.setVmemo();
//		imageVO.setDkbs();
//		imageVO.setUniquecode();
		imageVO.setPk_corp(pk_corp);
		imageVO.setIfpkind(fp_style);
//		imageVO.setIinvoicetype();// 费用类型
		imageVO.setPk_model_h(mvo.getPk_model_h());
		imageVO.setKeywords(mvo.getKeywords());

		imageVO.setZyFromBillZy(vo.getZy());//为生成凭证摘要做准备
		
		imageVO.setIsinterface(DZFBoolean.TRUE);
		
		return imageVO;
	}*/
	
	/**
	 * 生成暂存态凭证
	 * @param headVO
	 * @param vos
	 * @param pk_corp
	 */
	private void createTempPZ(TzpzHVO headVO, BankStatementVO2[] vos, String pk_corp){
		checkCreatePZ(pk_corp, headVO);
		headVO.setVbillstatus(IVoucherConstants.TEMPORARY);//暂存态
		//凭证号
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(pk_corp, headVO.getDoperatedate())) ;//改值
		headVO.setSourcebilltype(IBillTypeCode.HP85);
		headVO = (TzpzHVO) singleObjectBO.saveObject(headVO.getPk_corp(), headVO);
		//暂存态回写
		for(BankStatementVO2 vo : vos){
			vo.setPk_tzpz_h(headVO.getPrimaryKey());
			vo.setPzh(headVO.getPzh());
		}
		
		singleObjectBO.updateAry(vos, new String[]{"pk_tzpz_h", "pzh"});
	}
	
	public void checkCreatePZ(String pk_corp, TzpzHVO hvo) throws DZFWarpException{
		/**
		 * 已经年结不能反操作
		 */
		checkIsJz(hvo.getPk_corp(), hvo.getPeriod().substring(0, 4) + "-12");
		
		boolean isgz = qmgzService.isGz(hvo.getPk_corp(), hvo.getDoperatedate()
				.toString());
		if (isgz) {// 是否关账
			throw new BusinessException("制单失败: 制单日期所在的月份已关账，不能增加或修改凭证！");
		}
		
		/**
		 * 月末结转损益不能反操作
		 */
//		if (hvo.getIsqxsy() == null || !hvo.getIsqxsy().booleanValue()) {
//			BigDecimal repeatCodeNum = checkIsQjsyjz(hvo.getPk_corp(), hvo.getPeriod());
//
//			if (repeatCodeNum != null && repeatCodeNum.intValue() > 0) {
//				String errCodeStr = "制单失败: 当月已结转损益，不能操作！";
//				throw new BusinessException(errCodeStr);
//			}
//		}
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		// 操作日期在建账日期前也不能修改
		DZFDate begindate = corpvo.getBegindate();
		if (hvo.getDoperatedate().before(begindate)) {
			throw new BusinessException("制单失败: 录入日期在建账日期(" + begindate.toString()
					+ ")前，不能保存!");
		}
		
		// 判断制单日期，不能在建账日期之前
		if (hvo.getDoperatedate() != null) {
			if (corpvo.getBegindate() == null) {
				throw new BusinessException("制单失败: 当前公司建账日期为空，可能尚未建账，请检查");
			}
			if (hvo.getDoperatedate().before(corpvo.getBegindate())) {
				throw new BusinessException("制单失败: 制单日期不能在公司建账日期之前，请检查");
			}

		} else {
			throw new BusinessException("制单失败: 制单日期不能为空");
		}

	}
	
	public BigDecimal checkIsQjsyjz(String pk_corp, String period) throws DZFWarpException{
		StringBuilder sbCode = new StringBuilder(
				"select count(1) from  ynt_qmcl where pk_corp=? and period=?  and nvl(isqjsyjz,'N')='Y'");
		SQLParameter sp2 = new SQLParameter();
		sp2.addParam(pk_corp);
		sp2.addParam(period);
		BigDecimal repeatCodeNum = (BigDecimal) singleObjectBO
				.executeQuery(sbCode.toString(), sp2, new ColumnProcessor());
		
		return repeatCodeNum;
	}
	
	private void checkIsJz(String pk_corp, String period) {
		SQLParameter sqlp = new SQLParameter();
		sqlp.addParam(pk_corp);
		sqlp.addParam(period);
		String qmjzsqlwhere = "select jzfinish from YNT_QMJZ  where nvl(dr,0) = 0 and pk_corp = ? and period = ? ";
		String jzFinish = (String) singleObjectBO.executeQuery(qmjzsqlwhere,
				sqlp, new ColumnProcessor());
		if ("Y".equals(jzFinish)) {
			throw new BusinessException("制单失败:" + period.substring(0, 4) + "年度已经年结,不能操作!");
		}
	}
//	private DZFDouble getJDMny(BankStatementVO2 vo){
//		//生成借贷金额
//		DZFDouble mny = DZFDouble.ZERO_DBL;
//		if(vo.getSyje() != null 
//				&& DZFDouble.ZERO_DBL.compareTo(vo.getSyje()) != 0){
//			mny = vo.getSyje();
//			
//			return mny;
//		}
//		
//		if(vo.getZcje() != null 
//				&& DZFDouble.ZERO_DBL.compareTo(vo.getZcje()) != 0){
//			mny = vo.getZcje();
//		}
//				
//		return mny;
//	}
	
	private TzpzHVO createTzpzHVO(TzpzHVO headVO,
			List<BankStatementVO2> list, 
			String pk_corp, 
			String userid, 
			String pk_curr,
			DZFDouble mny,
			boolean accway,
			VatInvoiceSetVO setvo){
		//accway 标识已无效
		
		DZFDate lastDate = null;
		if(setvo!=null&&setvo.getPzrq()!=null&&setvo.getPzrq()==1){
			//凭证日期是当前账期最后一天
			if(!StringUtil.isEmpty(list.get(0).getInperiod())){
				lastDate = DateUtils.getPeriodEndDate(list.get(0).getInperiod());
			}else if(!StringUtil.isEmpty(list.get(0).getPeriod())){
				lastDate = DateUtils.getPeriodEndDate(list.get(0).getPeriod());
			}else{
				lastDate = list.get(0).getTradingdate();
			}
		}else{

			//凭证日期是票据实际日期
			DZFDate kprq=null;
			if(list!=null&&list.size()>1){
				
				for (BankStatementVO2 vo : list) {
					if(kprq==null){
						kprq = vo.getTradingdate();
					}else{
						if(kprq.before(vo.getTradingdate())){
							kprq=vo.getTradingdate();
						}
					}
					 
				}
			}else{	
				kprq=list.get(0).getTradingdate();
			}
			
			if(DateUtils.getPeriod(kprq).compareTo(list.get(0).getInperiod())==0){
			
				lastDate = kprq ;
			}else if(DateUtils.getPeriod(kprq).compareTo(list.get(0).getInperiod())<0){
				
				lastDate = DateUtils.getPeriodStartDate(list.get(0).getInperiod());
			}else if(DateUtils.getPeriod(kprq).compareTo(list.get(0).getInperiod())>0){
				
				lastDate = DateUtils.getPeriodEndDate(list.get(0).getInperiod());
			}else{
				
				lastDate = DateUtils.getPeriodEndDate(list.get(0).getInperiod());
			}

		}
		
		
//		TzpzHVO headVO = new TzpzHVO();
		headVO.setPk_corp(pk_corp);
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setJfmny(mny);
		headVO.setDfmny(mny);
		headVO.setCoperatorid(userid);
		headVO.setIshasjz(DZFBoolean.FALSE);
		headVO.setDoperatedate(lastDate);//vo.getTradingdate()
//		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(pk_corp, vo.getTradingdate()));
//		headVO.setVbillstatus(istemp ? IVoucherConstants.TEMPORARY : IVoucherConstants.FREE);// 默认自由态
		headVO.setVbillstatus(IVoucherConstants.FREE);// 默认自由态
		// 记录单据来源
		headVO.setSourcebillid(list.get(0).getPrimaryKey());
		headVO.setSourcebilltype(IBillTypeCode.HP85);
		String period = DateUtils.getPeriod(lastDate);
		headVO.setPeriod(period);
		headVO.setVyear(Integer.valueOf(period.substring(0, 4)));
		headVO.setIsfpxjxm(DZFBoolean.FALSE);
		
		int count = list.get(0).getCount();
		if(count == 0){
			count = 1;
		}
		headVO.setNbills(count);//
		headVO.setMemo(list.get(0).getZy());

		return headVO;
	}
	
//	private List<TzpzBVO> createTzpzBVO(BankStatementVO2 vo, 
//			DcModelHVO mHVO,
//			String pk_curr,
//			Map<String, YntCpaccountVO> ccountMap,
//			Map<String, Boolean> isTempMap,
//			String pk_corp,
//			BankAccountVO bankAccVO,
//			boolean isNewFz){
//		List<TzpzBVO> bodyList = new ArrayList<TzpzBVO>();
//		Boolean isTemp = null;
//		DcModelBVO[] mBVOs = mHVO.getChildren();
//		DZFDouble mny = null;
//		TzpzBVO tzpzbvo = null;
//		YntCpaccountVO cvo = null;
//		int rowno = 1;//分录行排序
//		for(DcModelBVO bvo : mBVOs){
//			mny = (DZFDouble) vo.getAttributeValue(bvo.getVfield());
//			tzpzbvo = new TzpzBVO();
//			tzpzbvo.setRowno(rowno++);//设置分录行编号
////			cvo = ccountMap.get(bvo.getPk_accsubj());
//			cvo = getMySelfAccountVO(bvo, ccountMap, pk_corp, bankAccVO);
//			if(cvo == null){
//				log.error("业务类型模板涉及主表："+ mHVO.getPrimaryKey()+",子表："+bvo.getPrimaryKey());
//				throw new BusinessException(mHVO.getBusitypetempname() + "业务类型科目未找到，请检查!");
//			}
//				
//			if(bvo.getDirection() == 0){
//				tzpzbvo.setJfmny(mny);
//				tzpzbvo.setYbjfmny(mny);
//				tzpzbvo.setVdirect(0);
//			}else{
//				tzpzbvo.setDfmny(mny);
//				tzpzbvo.setYbdfmny(mny);
//				tzpzbvo.setVdirect(1);
//			}
//			isTemp = isTempMap.get(ISTEMP);
//			if(!isTemp){
//				cvo = buildTzpzBVoByAccount(cvo, tzpzbvo, vo, isNewFz, isTempMap, 1);
//			}
//			
//			tzpzbvo.setPk_currency(pk_curr);
//			tzpzbvo.setPk_accsubj(cvo.getPk_corp_account());
//			tzpzbvo.setVcode(cvo.getAccountcode());
//			tzpzbvo.setVname(cvo.getAccountname());
//			
//			tzpzbvo.setKmmchie(cvo.getFullname());
//			tzpzbvo.setSubj_code(cvo.getAccountcode());
//			tzpzbvo.setSubj_name(cvo.getAccountname());
//			
//			tzpzbvo.setZy(vo.getTradingdate() + "," + bvo.getZy());
//			tzpzbvo.setNrate(DZFDouble.ONE_DBL);
//			tzpzbvo.setPk_corp(vo.getPk_corp());
//			
//			bodyList.add(tzpzbvo);
//			cvo = null;
//		}
//		return bodyList;
//	}
	
	private YntCpaccountVO buildTzpzBVoByAccount(YntCpaccountVO cvo, 
			TzpzBVO bvo, 
			BankStatementVO2 vo, 
			boolean isNewFz,
			Map<String,Boolean> isTempMap,
			int level){
		
		if(level > 4){
			isTempMap.put(ISTEMP, true);
			return cvo;
		}
		
		boolean isleaf = cvo.getIsleaf().booleanValue();//是否是末级
		boolean iskhfz = cvo.getIsfzhs().charAt(0) == '1';//是否是客户辅助核算
		boolean isgysfz = cvo.getIsfzhs().charAt(1) == '1';//是否是供应商辅助核算
		
		if(iskhfz || isgysfz){
			if(StringUtil.isEmpty(vo.getOthaccountname())){
				isTempMap.put(ISTEMP, true);
				return cvo;
			}
			
			constructFzhs(bvo, vo, iskhfz, isgysfz, isNewFz);
		}else if(!isleaf){
			if(StringUtil.isEmpty(vo.getOthaccountname())){
				isTempMap.put(ISTEMP, true);
				return cvo;
			}
			
			YntCpaccountVO[] accounts = accountService.queryByPk(vo.getPk_corp());
			cvo = getXJAccount(cvo, vo.getOthaccountname(), accounts, isTempMap, bvo, vo, isNewFz, ++level);
		}
		
		return cvo;
	}
	
	private TzpzBVO constructFzhs(TzpzBVO tzpzbvo,
			BankStatementVO2 vo, 
			boolean iskhfz, 
			boolean isgysfz,
			boolean isNewFz){
		List<AuxiliaryAccountBVO> fzhslist = new ArrayList<AuxiliaryAccountBVO>();
		AuxiliaryAccountBVO fzhsx = null;
		if(iskhfz){
			fzhsx = getfzhsx(AuxiliaryConstant.ITEM_CUSTOMER, vo.getPk_corp(),
					vo.getOthaccountname(), null, null, isNewFz);
			tzpzbvo.setFzhsx1(fzhsx.getPk_auacount_b());
			fzhslist.add(fzhsx);
		}
		if(isgysfz){
			fzhsx = getfzhsx(AuxiliaryConstant.ITEM_SUPPLIER, vo.getPk_corp(),
					vo.getOthaccountname(), null, null, isNewFz);
			tzpzbvo.setFzhsx2(fzhsx.getPk_auacount_b());
			fzhslist.add(fzhsx);
		}
		if(fzhslist.size() > 0){
			tzpzbvo.setFzhs_list(fzhslist);
		}
		return tzpzbvo;
	}
	
	//设置下级科目
	private YntCpaccountVO getXJAccount(YntCpaccountVO account, 
			String ghfmc, 
			YntCpaccountVO[] accounts, 
			Map<String,Boolean> isTempMap,
			TzpzBVO tzpzbvo,
			BankStatementVO2 vo,
			boolean isNewFz,
			int level){
		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();//存储下级科目
		
		boolean iskhfz;
		boolean isgysfz;
		for(YntCpaccountVO accvo : accounts){
			if(accvo.getIsleaf().booleanValue() && accvo.get__parentId() != null && accvo.get__parentId().startsWith(account.getAccountcode())){
				if(accvo.getAccountname().equals(ghfmc)){
					return accvo;
				}else{
					iskhfz = accvo.getIsfzhs().charAt(0) == '1';//是否是客户辅助核算
					isgysfz = accvo.getIsfzhs().charAt(1) == '1';//是否是供应商辅助核算
					if(iskhfz || isgysfz){
						accvo = buildTzpzBVoByAccount(accvo, tzpzbvo, vo, isNewFz, isTempMap, ++level);
						return accvo;
					}
					list.add(accvo);
				}
				
			}
		}
		YntCpaccountVO nextvo = null;
		if(list.size() > 0){//存在值即排序
			Collections.sort(list, new Comparator<YntCpaccountVO>() {

				@Override
				public int compare(YntCpaccountVO o1, YntCpaccountVO o2) {
					String code1 = o1.getAccountcode();
					String code2 = o2.getAccountcode();
					int i = code1.length() - code2.length() == 0 ?
						o1.getAccountcode().compareTo(o2.getAccountcode()) : code1.length() - code2.length();
					return i;
				}
			});
			
			String firaccountcode = account.getAccountcode();
			String[] specarrcode = IBillManageConstants.SPEC_ACC_CODE;
			if(firaccountcode.startsWith(specarrcode[0])
					|| firaccountcode.startsWith(specarrcode[1])
					|| firaccountcode.startsWith(specarrcode[2])
					|| firaccountcode.startsWith(specarrcode[3])){
				nextvo = (YntCpaccountVO) list.get(list.size() - 1).clone();
				int accountcode = Integer.parseInt(nextvo.getAccountcode()) + 1;
				nextvo.setPk_corp_account(null);
				nextvo.setFullname(null);
				nextvo.setAccountcode(accountcode + "");
				nextvo.setAccountname(ghfmc);
				nextvo.setDoperatedate(new DZFDate().toString().substring(0, 10).substring(0, 10));
				nextvo.setIssyscode(DZFBoolean.FALSE);
				nextvo.setBisseal(DZFBoolean.FALSE);
				cpaccountService.saveNew(nextvo);
			}else{
				for(YntCpaccountVO accvo : list){
					if(accvo.getIsleaf().booleanValue()){
						isTempMap.put(ISTEMP, true);
						return accvo;
					}
				}
			}
			
		}
		
		return nextvo;
	}
	
	private String getPK_fzhsx (String pk_fzhslb, String pk_corp, String name, String dw, String gg, boolean isNewFz) {
		return getfzhsx(pk_fzhslb, pk_corp, name, dw, gg, isNewFz).getPk_auacount_b();
	}
	
	private AuxiliaryAccountBVO getfzhsx (String pk_fzhslb, String pk_corp, String name, String dw, String gg, boolean isNewFz) {
		
		AuxiliaryAccountBVO fzhs = queryBByName(pk_corp, name, dw, gg, pk_fzhslb);
		
		if (fzhs == null) {
			fzhs = new AuxiliaryAccountBVO();
			fzhs.setPk_auacount_h(pk_fzhslb);
			fzhs.setCode(yntBoPubUtil.getFZHsCode(pk_corp, pk_fzhslb));//getRandomCode()
			fzhs.setName(name);
			fzhs.setUnit(dw);
			fzhs.setSpec(gg);
			fzhs.setPk_corp(pk_corp);
			fzhs = (AuxiliaryAccountBVO) singleObjectBO.saveObject(pk_corp, fzhs);
		}
		return fzhs;
	}
	
	private String getRandomCode () {
		StringBuilder sb = new StringBuilder();
		Random rand = new Random();
		for (int i = 0; i < 11; i++) {
			sb.append(rand.nextInt(10));
		}
		char letter = (char) (rand.nextInt(26) + 'a');
		sb.append(letter);
		return sb.toString();
	}
	
	private AuxiliaryAccountBVO queryBByName(String pk_corp, String name, String dw, String gg, String hid) throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		sf.append(" pk_auacount_h = ? and name = ? and pk_corp = ? and nvl(dr,0) = 0 ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(hid);
		sp.addParam(name);
		sp.addParam(pk_corp);
		if(AuxiliaryConstant.ITEM_INVENTORY.equals(hid)){
			if(!StringUtil.isEmpty(dw)){
				sf.append(" and unit = ? ");
				sp.addParam(dw);
			}
			if(!StringUtil.isEmpty(gg)){
				sf.append(" and spec = ? ");
				sp.addParam(gg);
			}
		}
		AuxiliaryAccountBVO[] results = (AuxiliaryAccountBVO[]) singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class, sf.toString(), sp);
		if (results != null && results.length > 0) {
			return results[0];
		}
		return null;
	}
	
	public List<BankStatementVO2> construcBankStatement(BankStatementVO2[] vos, String pk_corp){
		List<String> pks = new ArrayList<String>();
		for(BankStatementVO2 vo : vos){
			pks.add(vo.getPrimaryKey());
		}
		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_bankstatement where ");
		sf.append(SqlUtil.buildSqlForIn("pk_bankstatement", pks.toArray(new String[0])));
		sf.append(" and pk_corp = ? and nvl(dr,0) = 0 ");//and (pk_tzpz_h is null or pk_tzpz_h = '')
		sf.append(" order by tradingdate asc, rowid asc ");
		
		sp.addParam(pk_corp);
		List<BankStatementVO2> list = (List<BankStatementVO2>) singleObjectBO.executeQuery( sf.toString(), sp, 
				new BeanListProcessor(BankStatementVO2.class));
		
		return list;
	}
	
	/*public Map<String, DcModelHVO> queryDcModelVO(String pk_corp){
		Map<String, DcModelHVO> map = new HashMap<String, DcModelHVO>();
		
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(IDefaultValue.DefaultGroup);
		List<DcModelHVO> mhvos = (List<DcModelHVO>) singleObjectBO.executeQuery(" ( pk_corp = ? or pk_corp = ? ) and nvl(dr,0)=0 ", params, 
				new Class[]{DcModelHVO.class, DcModelBVO.class});
		
		String key = null;
		DcModelBVO[] bvos = null;
		for(DcModelHVO hvo : mhvos){
			key = hvo.getPk_model_h();
			
			//排序
			bvos = hvo.getChildren();
			if(bvos == null || bvos.length == 0){
				continue;
			}
			Arrays.sort(bvos, new Comparator<DcModelBVO>() {
				@Override
				public int compare(DcModelBVO o1, DcModelBVO o2) {
					return o1.getPrimaryKey().compareTo(o2.getPrimaryKey());
				}
			});
			
			map.put(key, hvo);
		}
		
		return map;
	}*/

	@Override
	public BankStatement2ResponseVO updateVOArr(String pk_corp, Map<String, BankStatementVO2[]> sendData, DZFBoolean isFlag)
			throws DZFWarpException {
		Map<String, String> repMap = new HashMap<String, String>();
		BankStatement2ResponseVO responseVO = new BankStatement2ResponseVO();
		String pk_bankaccount = "$$$";
		
		//如是新增， 需返回新增vos
		BankStatementVO2[] addvos = sendData.get("adddocvos");
		
		if(addvos != null && addvos.length > 0){
			pk_bankaccount = addvos[0].getPk_bankaccount();
			buildAlisMap(addvos, repMap);
			saveBillDatas(pk_corp, addvos);
		}
		BankStatementVO2[] updvos = sendData.get("upddocvos");
		List<BankStatementVO2> changeList = new ArrayList<BankStatementVO2>();
		if(updvos != null && updvos.length > 0){
			buildAlisMap(updvos, repMap);
			pk_bankaccount = updvos[0].getPk_bankaccount();
			for(BankStatementVO2 vo : updvos){
				vo.setVersion(new DZFDouble(1.0));
				
				//查询业务类型所属期间是否是入账期间
				if (StringUtil.isEmpty(vo.getPk_model_h()) == false)
				{
					BillCategoryVO categoryvo = (BillCategoryVO)singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, vo.getPk_model_h());
					if (vo.getInperiod().equals(categoryvo.getPeriod()) == false)
					{
						throw new BusinessException("业务类型所属期间与入账期间不一致，请重新选择一下业务类型");
					}
				}
				//修改业务类型对应的结算方式，入账科目，结算科目
				CategorysetVO setVO = queryCategorySetVO(vo.getPk_model_h());
				vo.setSettlement(setVO.getSettlement()==null?2:setVO.getSettlement());
				vo.setPk_subject(setVO.getPk_accsubj());
				vo.setPk_settlementaccsubj(setVO.getPk_settlementaccsubj());
				
				if(!StringUtil.isEmpty(vo.getImgpath())){
					vo.setBillstatus(BankStatementVO2.STATUS_2);//更为绑定成功
				}
				if(vo.getSourcetype() == BankStatementVO2.SOURCE_100){
					vo.setSourcetype(BankStatementVO2.SOURCE_0);//更改为手工录入
				}
				if(!StringUtils.isEmpty(vo.getPk_category_keyword()) && !StringUtils.isEmpty(vo.getPk_model_h())){
					//查询修改前的分类主键
					List<BankStatementVO2> oldpkList = queryByID(vo.getPk_bankstatement());
					if(!vo.getPk_model_h().equals(oldpkList.get(0).getPk_model_h())){
						changeList.add(vo);
					}
				}
			}
			if(changeList!=null&&changeList.size()>0){
				//自学习
				List<BankStatementVO2> oldList = queryByID(changeList.get(0).getPk_bankstatement());
				List<OcrInvoiceVO> OcrInvoiceVOList = changeToOcr(oldList, pk_corp);
				for (OcrInvoiceVO ocrInvoiceVO : OcrInvoiceVOList) {
					ocrInvoiceVO.setPk_invoice(null);
					ocrInvoiceVO.setChildren(new OcrInvoiceDetailVO[]{new OcrInvoiceDetailVO()});
					iBillcategory.saveNewCategroy(OcrInvoiceVOList.toArray(new OcrInvoiceVO[0]),
							changeList.get(0).getPk_model_h(), pk_corp, ocrInvoiceVO.getPeriod());
				}
				
			}
			
			singleObjectBO.updateAry(updvos, new String[]{
					"tradingdate", "zy", "syje", "zcje", "othaccountname", "othaccountcode",
					"ye", "sourcetype", "period", "billstatus", "inperiod",
					"pk_model_h", "busitypetempname","version","settlement","pk_subject","pk_settlementaccsubj"});
		}
		
		BankStatementVO2[] delvos = sendData.get("deldocvos");
		
		if(delvos != null && delvos.length > 0){
			deleteBillDatas(delvos, pk_corp, repMap);
		}
		
		String message = checkIsHasRepeation(pk_corp, pk_bankaccount, repMap, isFlag);
		responseVO.setMsg(message);
		if(updvos!=null&&updvos.length>0){
			if(!StringUtils.isEmpty(pk_bankaccount)){
				BankAccountVO bvo = queryBankname(pk_bankaccount);
				for (BankStatementVO2 bankStatementVO2 : updvos) {
					bankStatementVO2.setBankname(bvo.getBankname());
					bankStatementVO2.setBankaccount(bvo.getBankaccount());
				}
			}
			responseVO.setVos(updvos);
			return responseVO;
		}else{
			if(!StringUtils.isEmpty(pk_bankaccount)&&addvos!=null&&addvos.length>0){
				BankAccountVO bvo = queryBankname(pk_bankaccount);
				for (BankStatementVO2 bankStatementVO2 : addvos) {
					bankStatementVO2.setBankname(bvo.getBankname());
					bankStatementVO2.setBankaccount(bvo.getBankaccount());
				}
			}
			responseVO.setVos(addvos);
			return responseVO;
		}
		
	}
	
	private BankAccountVO queryBankname(String pk_bankaccount)throws DZFWarpException{
		
		BankAccountVO vo = (BankAccountVO)singleObjectBO.queryByPrimaryKey(BankAccountVO.class, pk_bankaccount);
		
		return vo;
	}
	private void buildAlisMap(BankStatementVO2[] vos, Map<String, String> repMap){
		String key;
		for(BankStatementVO2 vo : vos){
			key = buildAlisKey(vo);
			
			if(!repMap.containsKey(key)){
				repMap.put(key, key);
			}
		}
	}
	
	private String buildAlisKey(BankStatementVO2 vo){
		StringBuffer sf = new StringBuffer();
		String codestr = StringUtil.isEmpty(vo.getOthaccountcode()) 
				? "" : vo.getOthaccountcode();
		
		DZFDouble ye = SafeCompute.add(vo.getYe(), DZFDouble.ZERO_DBL);
		String yestr = chafei(ye);
		DZFDouble syje = SafeCompute.add(vo.getSyje(), DZFDouble.ZERO_DBL);
		String syjestr = chafei(syje);
		DZFDouble zcje = SafeCompute.add(vo.getZcje(), DZFDouble.ZERO_DBL);
		String zcjestr = chafei(zcje);
		
		sf.append(vo.getTradingdate())
		  .append(",")
		  .append(yestr)
		  .append(",")
		  .append(codestr)
		  .append(",")
		  .append(syjestr)
		  .append(",")
		  .append(zcjestr);
		
		return sf.toString();
	}
	private String chafei(DZFDouble d){
		String str = d.toString();
		int count = 10;
		int length;
		char c;
		while(true){
			count--;
			length = str.length();
			c = str.charAt(length - 1);
			if(count == 0){
				break;
			}else if(c == '0'){
				str = str.substring(0, length - 1);
			}else if(c == '.'){
				str = str.substring(0, length - 1);
				break;
			}else{
				break;
			}
		}
		
		return str;
	}
	
	private void deleteBillDatas(BankStatementVO2[] vos, String pk_corp, Map<String, String> repMap){
		singleObjectBO.deleteVOArray(vos);
		
		//更新中间表 对账单主键为空
		List<String> pks = new ArrayList<String>();
		String key;
		for(BankStatementVO2 vo : vos){
			pks.add(vo.getPrimaryKey());
			
			key = buildAlisKey(vo);//
			repMap.remove(key);
		}
		
		StringBuffer sf = new StringBuffer();
		sf.append(" update ynt_bankbilltostatement y set pk_bankstatement = null where nvl(dr,0)=0 and pk_corp = ? and ");
		sf.append(SqlUtil.buildSqlForIn("pk_bankstatement", pks.toArray(new String[0])));
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		
		singleObjectBO.executeUpdate(sf.toString(), sp);
		
	}
	
	private void saveBillDatas(String pk_corp, BankStatementVO2[] vos){
		Set<String> set = new HashSet<String>();
		String key;
		String period;
		String pk_bankaccount;
		for(BankStatementVO2 vo : vos){
			//查询业务类型所属期间是否是入账期间
			if (StringUtil.isEmpty(vo.getPk_model_h()) == false)
			{
				BillCategoryVO categoryvo = (BillCategoryVO)singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, vo.getPk_model_h());
				if (vo.getInperiod().equals(categoryvo.getPeriod()) == false)
				{
					throw new BusinessException("业务类型所属期间与入账期间不一致，请重新选择一下业务类型");
				}
			}
			vo.setVersion(new DZFDouble(1.0));
			period = DateUtils.getPeriod(vo.getTradingdate());
			pk_bankaccount = vo.getPk_bankaccount();
			key = pk_bankaccount + "_" + period;
			if(!set.contains(key)){
				set.add(key);
			}
		}
		
		//查询涉及到的数据
		List<BankBillToStatementVO> bankBillList = queryBankBillByPeriod(set, pk_corp);
		Map<String, List<BankBillToStatementVO>> bankBillMap = hashliseBankBillMap(bankBillList);
		
		key = null;
		BankBillToStatementVO dzdvo;
		List<BankBillToStatementVO> dzdList;
		List<BankStatementVO2> inList = new ArrayList<BankStatementVO2>();
		List<BankStatementVO2> upList = new ArrayList<BankStatementVO2>();
		List<BankStatementVO2> changeList = new ArrayList<BankStatementVO2>();
		List<BankBillToStatementVO> upBillList = new ArrayList<BankBillToStatementVO>();
		for(BankStatementVO2 vo : vos){
			key = buildKey(new String[]{
					vo.getTradingdate().toString(),
					vo.getPk_bankaccount(),
					getDefaultMnyValue(vo.getSyje()),
					getDefaultMnyValue(vo.getZcje()),
					vo.getOthaccountname()
			});
			
			if(bankBillMap.containsKey(key)){//更新数据
				dzdList = bankBillMap.get(key);
				if(dzdList == null || dzdList.size() == 0){
					inList.add(vo);
				}else{
					dzdvo = dzdList.get(0);
					vo.setBillstatus(BankStatementVO2.STATUS_2);//绑定
					vo.setSourcebillid(dzdvo.getSourcebillid());
					vo.setPk_image_group(dzdvo.getPk_image_group());
					vo.setImgpath(dzdvo.getImgpath());
					vo.setPrimaryKey(dzdvo.getPk_bankstatement());
					BankStatementVO2 tvo = (BankStatementVO2)singleObjectBO.queryByPrimaryKey(BankStatementVO2.class, dzdvo.getPk_bankstatement());
					vo.setVdef13(tvo.getVdef13());
					upList.add(vo);
					
					dzdvo.setMemo("对账单匹配上回单信息<br>");
					upBillList.add(dzdvo);
					
					dzdList.remove(0);//删除第一个
//					bankBillMap.remove(key);
				}
				
			}else{
				inList.add(vo);
			}
			
		}
		
		if(upBillList.size() > 0){
			singleObjectBO.updateAry(upBillList.toArray(new BankBillToStatementVO[0]),
					new String[]{ "memo" });
		}
		
		if(inList.size() > 0){
			ArrayList<BankStatementVO2> tempList = new ArrayList<BankStatementVO2>();
			for (BankStatementVO2 bVO2 : inList) {
				if(StringUtils.isEmpty(bVO2.getPk_model_h())){
					tempList.add(bVO2);
				}else{
					changeList.add(bVO2);
				}
			}
			if(tempList!=null&&tempList.size()>0){
				//转换分类
				List<BankStatementVO2> bankList = changeToBank(tempList, pk_corp);
				changeList.addAll(bankList);
			}
			HashMap<String, CategorysetVO> hashCategorysetVOCache = new HashMap<String, CategorysetVO>();
			for (BankStatementVO2 vo : changeList)
			{
				//查询入账科目，结算科目，结算方式
				if(!StringUtils.isEmpty(vo.getPk_model_h())){
					CategorysetVO setVO = null;
					if(hashCategorysetVOCache.containsKey(vo.getPk_model_h()))
					{
						setVO = hashCategorysetVOCache.get(vo.getPk_model_h());
					}
					else
					{
						setVO = queryCategorySetVO(vo.getPk_model_h());
						hashCategorysetVOCache.put(vo.getPk_model_h(), setVO);
					}
					if (setVO != null) {
						vo.setPk_subject(setVO.getPk_accsubj());
						vo.setPk_settlementaccsubj(setVO.getPk_settlementaccsubj());
						vo.setSettlement(setVO.getSettlement() == null ? 2 : setVO.getSettlement());
					}
				}
				singleObjectBO.insertVO(pk_corp, vo);
			}
//			String[] addpks = singleObjectBO.insertVOArr(pk_corp, 
//					changeList.toArray(new BankStatementVO2[0]));
//			for(int i = 0; i < inList.size(); i++){
//				inList.get(i).setPrimaryKey(addpks[i]);//为新增vo设置pk
//			}
		}
		if(upList.size() > 0){//匹配上不更新业务类型
			singleObjectBO.updateAry(upList.toArray(new BankStatementVO2[0]), 
					new String[]{"coperatorid", "doperatedate", "pk_corp", "pk_bankaccount",
							"tradingdate", "zy", "syje", "zcje", "othaccountname", "othaccountcode",
							"ye", "sourcetype", "period", "billstatus","version"});
		}
	}
	
	private String getDefaultMnyValue(DZFDouble je){
		return je == null ? DZFDouble.ZERO_DBL.doubleValue() + "" : je.doubleValue() + "";
	}
	
	/**
	 * 校验数据是否重复
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	private String checkIsHasRepeation(String pk_corp, 
			String pk_bankaccount, 
			Map<String, String> repMap,
			DZFBoolean isFlag) throws DZFWarpException{
		if(isFlag != null && isFlag.booleanValue()){
			return null;
		}
		if("$$$".equals(pk_bankaccount)){
			return null;
		}
		
		if(repMap == null || repMap.size() == 0){
			return null;
		}
		int i = 0;
		String[] fieldValue = new String[repMap.size()];
		for(Map.Entry<String, String> entry : repMap.entrySet()){
			fieldValue[i++] = entry.getValue();
		}
		String wherePart = SqlUtil.buildSqlForIn("aliname", fieldValue);
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(BankStatementVO2.STATUS_1);
		
		StringBuffer sf = new StringBuffer();
		sf.append(" Select t.aliname, t.tradingdate, t.ye From  ");
		sf.append(" (Select y.tradingdate  ");
		sf.append("        || ',' ||  ");
		sf.append("        case when y.ye is null then 0 else y.ye end ");
		sf.append("        || ',' ||  ");
		sf.append("        y.othaccountcode  ");
		sf.append("        || ',' ||  ");
		sf.append("        case when y.syje is null then 0 else y.syje end ");
		sf.append("        || ',' ||  ");
		sf.append("        case when y.zcje is null then 0 else y.zcje end aliname, ");
		sf.append("        y.* ");
		sf.append(" From ynt_bankstatement y ");
		sf.append(" Where y.pk_corp = ? and nvl(dr,0)=0 ");
		sf.append(" and y.billstatus != ? ");
		if(StringUtil.isEmpty(pk_bankaccount)){
			sf.append(" and y.pk_bankaccount is null ");
		}else{
			sf.append(" and y.pk_bankaccount = ? ");
			sp.addParam(pk_bankaccount);
		}
		sf.append(" ) t ");
		sf.append(" where ");
		sf.append(wherePart);
		sf.append(" group by t.aliname, t.tradingdate, t.ye having count(1) > 1 ");
		
		List<BankStatementVO2> list = (List<BankStatementVO2>) singleObjectBO.executeQuery(sf.toString(),
				sp, new BeanListProcessor(BankStatementVO2.class));
		sf = new StringBuffer();
		if(list != null && list.size() > 0){
			for(BankStatementVO2 vo : list){
				sf.append("<p>交易日期:")
					.append(vo.getTradingdate())
					.append(",余额:")
				    .append(vo.getYe())
				    .append(" 重复。</p>");//请检查！
			}
		}
		
//		if(!StringUtil.isEmpty(sf.toString())){
//			throw new BusinessException(sf.toString(), IBillManageConstants.ERROR_FLAG);
//		}
		return sf.toString();
	}
	
	@Override
	public String setBusiType(BankStatementVO2[] vos, String busiid, String businame) throws DZFWarpException {
		BankStatementVO2 newVO = null;
		int upCount = 0;
		int npCount = 0;
		List<BankStatementVO2> list = new ArrayList<BankStatementVO2>();
		for(BankStatementVO2 vo : vos){
			
			if(!StringUtil.isEmptyWithTrim(vo.getPk_tzpz_h())){
				npCount++;
				continue;
			}
			
			newVO = new BankStatementVO2();
			newVO.setPk_bankstatement(vo.getPk_bankstatement());;
			newVO.setPk_model_h(busiid);
			newVO.setBusitypetempname(businame);
			upCount++;
			list.add(newVO);
		}
		
		if(list != null && list.size() > 0){
			singleObjectBO.updateAry(list.toArray(new BankStatementVO2[0]),
					new String[]{"pk_model_h", "busitypetempname"});
		}
		
		return new StringBuffer().append("业务类型设置更新成功 ")
								 .append(upCount)
								 .append(" 条")
								 .append(npCount > 0 ? ",未更新 " + npCount + " 条" : "").toString();
	}
	
	@Override
	public TzpzHVO getTzpzHVOByID(BankStatementVO2[] vos, 
			BankAccountVO bankAccVO, 
			String pk_corp, 
			String userid,
			VatInvoiceSetVO setvo,
			boolean accway) throws DZFWarpException {
		List<String> pks = new ArrayList<String>();
		
		DZFDate maxDate = null;
		for(BankStatementVO2 vo : vos){
			if(!StringUtil.isEmpty(vo.getPrimaryKey()) && StringUtil.isEmpty(vo.getPk_tzpz_h())){
				pks.add(vo.getPrimaryKey());
			}
			
			maxDate = getMaxDate(maxDate, vo);
		}
		
		if(pks.size() == 0){
			return null;
		}
		
		StringBuffer sf = new StringBuffer();
		sf.append("select * from ynt_bankstatement where nvl(dr,0) = 0 and ")
		  .append(SqlUtil.buildSqlForIn("pk_bankstatement", pks.toArray(new String[pks.size()])));
		
		List<BankStatementVO2> afterlist = (List<BankStatementVO2>) singleObjectBO.executeQuery(
				sf.toString(), null, new BeanListProcessor(BankStatementVO2.class));
		
		if(afterlist == null)
			throw new BusinessException("查询银行对账单信息不存在，请检查");
		
		for (BankStatementVO2 bankStatementVO2 : afterlist) {
			if(StringUtils.isEmpty(bankStatementVO2.getPk_model_h())){
				throw new BusinessException("银行对账单:未能找到对应的业务类型模板，请检查");
			}
		}
		
		checkisGroup(afterlist, pk_corp);
		
		CorpVO corpVO = corpService.queryByPk(pk_corp);
		
		String pk_curr = yntBoPubUtil.getCNYPk();
		
		//YntCpaccountVO[] accounts = AccountCache.getInstance().get(null, pk_corp);
		//DcModelHVO mHVO = null;
		//Map<String, DcModelHVO> dcmap = queryDcModelVO(pk_corp);
		//mHVO = dcmap.get(afterlist.get(0).getPk_model_h());
		
		//if(mHVO == null)
			//throw new BusinessException("银行对账单:未能找到对应的业务类型模板，请检查");
		
		//DcModelBVO[] models = mHVO.getChildren();
		//OcrImageLibraryVO lib = null;
		List<TzpzBVO> tblist = new ArrayList<TzpzBVO>();
		
		Map<String,YntCpaccountVO> accountMap = accountService.queryMapByPk(corpVO.getPk_corp());
		YntCpaccountVO[] accVOs=accountService.queryByPk(corpVO.getPk_corp());
		
		Map<String, Object> paramMap=zncsVoucher.initVoucherParam(corpVO, afterlist.get(0).getInperiod(),true);
		List<List<Object[]>> levelList=(List<List<Object[]>>) paramMap.get("levelList");
		Map<String, Object[]> categoryMap =(Map<String, Object[]>) paramMap.get("categoryMap");
		Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap=(Map<Integer, AuxiliaryAccountHVO>) paramMap.get("fzhsHeadMap");
		Set<String> zyFzhsList=(Set<String>) paramMap.get("zyFzhsList");
		Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap=(Map<String, List<AuxiliaryAccountBVO>>) paramMap.get("fzhsBodyMap");
		InventorySetVO inventorySetVO=(InventorySetVO) paramMap.get("inventorySetVO");
		Map<String, InventoryAliasVO> fzhsBMMap=(Map<String, InventoryAliasVO>) paramMap.get("fzhsBMMap");
		List<Object> paramList = (List<Object>) paramMap.get("paramList");
		Map<String, BdCurrencyVO> currMap=(Map<String, BdCurrencyVO>) paramMap.get("currMap");
		Map<String, Object[]> rateMap=(Map<String, Object[]>) paramMap.get("rateMap");
		Map<String, String> bankAccountMap=(Map<String, String>) paramMap.get("bankAccountMap");
		Map<String, AuxiliaryAccountBVO> assistMap=(Map<String, AuxiliaryAccountBVO>) paramMap.get("assistMap");
		Map<String, List<AccsetVO>> accsetMap=(Map<String, List<AccsetVO>>) paramMap.get("accsetMap");
		Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map=(Map<String, List<AccsetKeywordBVO2>>) paramMap.get("accsetKeywordBVO2Map");
		Map<String, String> jituanSubMap=(Map<String, String>) paramMap.get("jituanSubMap");
		String tradeCode=(String) paramMap.get("tradeCode");
		String newrule = (String) paramMap.get("newrule");
		List<AuxiliaryAccountBVO> chFzhsBodyVOs=(List<AuxiliaryAccountBVO>) paramMap.get("chFzhsBodyVOs");
		
		int ifptype  = VATInvoiceTypeConst.VAT_BANK_INVOICE;//银行回单
		int fp_style = VATInvoiceTypeConst.NON_INVOICE;//未开票
		TzpzHVO headVO = new TzpzHVO();
		headVO.setIfptype(ifptype);
		headVO.setFp_style(null);
		for(BankStatementVO2 vo : afterlist){
			if(StringUtils.isEmpty(vo.getPk_model_h())){
				throw new BusinessException("业务类型为空,请重新选择业务类型");
			}
			/*lib = changeOcrInvoiceVO(vo, mHVO, pk_corp, bankAccVO, ifptype, fp_style);
			aitovoucherserv_bank.getTzpzBVOList(headVO, models, lib, null, tblist, accounts);*/
			List<BankStatementVO2> ll = new ArrayList<BankStatementVO2>();
			ll.add(vo);
			Map<String, Map<String, Object>> checkMsgMap=new HashMap<String, Map<String, Object>>();
			List<OcrInvoiceVO> invoiceList = changeToOcr(ll, pk_corp);
			List<TzpzHVO> tzpzhvoList = zncsVoucher.processGeneralTzpzVOsByInvoice(invoiceList, vo.getInperiod(), pk_corp, userid, checkMsgMap, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpVO, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
			//取出子类
			List<TzpzBVO> pzbvolist = Arrays.asList((TzpzBVO[])tzpzhvoList.get(0).getChildren());
			for (TzpzBVO tzpzBVO : pzbvolist) {
				tzpzBVO.setVdef6(vo.getTradingdate().toString());
			}
			tblist.addAll(pzbvolist);
			
		}
		
		//aitovoucherserv_bank.setSpeaclTzpzBVO1(headVO, lib, tblist);
		
		tblist = constructItems(tblist, setvo, afterlist.get(0), pk_corp);
//		isTempMap.put(ISTEMP, false);
		
//		bodyList = createCombinTzpzBVO(afterlist, bkmap, mHVO, pk_curr, ccountMap, isTempMap, pk_corp, bankAccVO, true);
		DZFDouble totalMny = DZFDouble.ZERO_DBL;
		for(TzpzBVO bvo : tblist){
			totalMny = SafeCompute.add(bvo.getJfmny(), totalMny);
		}
		
		afterlist.get(0).setInperiod(DateUtils.getPeriod(maxDate));//将入账期间设置为最大的
		afterlist.get(0).setCount(afterlist.size());
		createTzpzHVO(headVO, afterlist, pk_corp, userid, pk_curr, totalMny, accway,setvo);
		
		//headvo sourcebillid 重新赋值
		pks = new ArrayList<String>();
		for(BankStatementVO2 svo : afterlist){
			pks.add(svo.getPrimaryKey());
		}
		String sourcebillid = SqlUtil.buildSqlConditionForInWithoutDot(pks.toArray(new String[pks.size()]));
		headVO.setSourcebillid(sourcebillid);
		
		//如果有图片的话 图片组设置上
		if(!StringUtil.isEmpty(afterlist.get(0).getPk_image_group())){
			headVO.setPk_image_group(afterlist.get(0).getPk_image_group());
		}
		
		headVO.setChildren(tblist.toArray(new TzpzBVO[0]));
		
		return headVO;
	}
	
	private DZFDate getMaxDate(DZFDate date, BankStatementVO2 vo){
		DZFDate voucherDate = null;
		if(!StringUtil.isEmpty(vo.getInperiod())){
			voucherDate = DateUtils.getPeriodEndDate(vo.getInperiod());
		}else if(!StringUtil.isEmpty(vo.getPeriod())){
			voucherDate = DateUtils.getPeriodEndDate(vo.getPeriod());
		}else{
			voucherDate = vo.getTradingdate();
		}
		
		if(date == null){
			return voucherDate;
		}else if(date.before(voucherDate)){
			date = voucherDate;
		}
		
		return date;
	}
	
	private void checkisGroup(List<BankStatementVO2> list, String pk_corp){
		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append(" select y.tradingdate from ynt_bankstatement y ");
		sf.append(" where pk_corp = ? and nvl(dr,0)=0  and ( 1 <> 1  ");
		
		sp.addParam(pk_corp);
		
		boolean flag = true;
		List<String> libs = new ArrayList<String>();
		for(BankStatementVO2 vo : list){
			if(!StringUtil.isEmpty(vo.getSourcebillid())
					&& !StringUtil.isEmpty(vo.getPk_image_group())){
				sf.append(" or pk_image_group = ?  ");
				sp.addParam(vo.getPk_image_group());
				libs.add(vo.getSourcebillid());
				flag = false;
			}
		}
		sf.append(" ) ");
		
		if(flag){
			return;
		}
		
		for(String s : libs){
			sf.append(" and y.sourcebillid != ? ");
			sp.addParam(s);
		}
		
		List<BankStatementVO2> vos = (List<BankStatementVO2>) singleObjectBO.executeQuery(
				sf.toString(), sp, new BeanListProcessor(BankStatementVO2.class));

		if(vos != null && vos.size() > 0){
			sf.setLength(0);
			sf.append("<br>交易日期:");
			for(int i = 0; i < vos.size(); i++){
				sf.append(vos.get(i).getTradingdate().toString());
				
				if(i != vos.size() - 1){
					sf.append(", ");
				}
			}
			sf.append("归属于合并生单的一组图片，需要勾选涉及该组图片一起生单");
			
			throw new BusinessException(sf.toString());
		}
	}
	
//	private List<TzpzBVO> createCombinTzpzBVO(List<BankStatementVO2> vos, 
//			Map<String, BankStatementVO2> bkmap,
//			DcModelHVO mHVO,
//			String pk_curr,
//			Map<String, YntCpaccountVO> ccountMap,
//			Map<String, Boolean> isTempMap,
//			String pk_corp,
//			BankAccountVO bankAccVO,
//			boolean isNewFz){
//		List<TzpzBVO> bodyList = new ArrayList<TzpzBVO>();
//		Boolean isTemp = null;
//		DcModelBVO[] mBVOs = mHVO.getChildren();
//		DZFDouble mny = null;
//		TzpzBVO tzpzbvo = null;
//		YntCpaccountVO cvo = null;
//		BankStatementVO2 vo = null;
//		String key = null;
//		Map<String,TzpzBVO> tzpzbmap = new HashMap<String, TzpzBVO>();
//		TzpzBVO temptzpz = null;
//		int rowno = 1;//分录行排序
//		for(DcModelBVO bvo : mBVOs){
//			for(Map.Entry<String, BankStatementVO2> entry : bkmap.entrySet()){
//				key = entry.getKey();
//				vo = entry.getValue();
//				if("$$$".equals(key)){
//					vo.setOthaccountname("");
//				}
//				
//				mny = (DZFDouble) vo.getAttributeValue(bvo.getVfield());
//				tzpzbvo = new TzpzBVO();
//				tzpzbvo.setRowno(rowno++);//设置分录行编号
//				cvo = getMySelfAccountVO(bvo, ccountMap, pk_corp, bankAccVO);//ccountMap.get(bvo.getPk_accsubj());
//				if(cvo == null){
//					log.error("业务类型模板涉及主表："+ mHVO.getPrimaryKey()+",子表："+bvo.getPrimaryKey());
//					throw new BusinessException(mHVO.getBusitypetempname() + "业务类型科目未找到，请检查!");
//				}
//					
//				if(bvo.getDirection() == 0){
//					tzpzbvo.setJfmny(mny);
//					tzpzbvo.setYbjfmny(mny);
//					tzpzbvo.setVdirect(0);
//				}else{
//					tzpzbvo.setDfmny(mny);
//					tzpzbvo.setYbdfmny(mny);
//					tzpzbvo.setVdirect(1);
//				}
//				isTemp = isTempMap.get(ISTEMP);
//				
//				cvo = buildTzpzBVoByAccount(cvo, tzpzbvo, vo, isNewFz, isTempMap, 1);
//				tzpzbvo.setPk_currency(pk_curr);
//				tzpzbvo.setPk_accsubj(cvo.getPk_corp_account());
//				tzpzbvo.setVcode(cvo.getAccountcode());
//				tzpzbvo.setVname(cvo.getAccountname());
//				
//				tzpzbvo.setKmmchie(cvo.getFullname());
//				tzpzbvo.setSubj_code(cvo.getAccountcode());
//				tzpzbvo.setSubj_name(cvo.getAccountname());
//				
//				tzpzbvo.setZy(bvo.getZy());
//				tzpzbvo.setNrate(DZFDouble.ONE_DBL);
//				tzpzbvo.setPk_corp(vo.getPk_corp());
//				
//				key = new StringBuffer().append(tzpzbvo.getPk_accsubj())
//						.append("_")
//						.append(tzpzbvo.getFzhsx1())
//						.append("_")
//						.append(tzpzbvo.getFzhsx2())
//						.toString();
//				if(tzpzbmap.containsKey(key)){
//					temptzpz = tzpzbmap.get(key);
//					temptzpz.setJfmny(SafeCompute.add(temptzpz.getJfmny(), tzpzbvo.getJfmny()));
//					temptzpz.setYbjfmny(SafeCompute.add(temptzpz.getYbjfmny(), tzpzbvo.getYbjfmny()));
//					temptzpz.setDfmny(SafeCompute.add(temptzpz.getDfmny(), tzpzbvo.getDfmny()));
//					temptzpz.setYbdfmny(SafeCompute.add(temptzpz.getYbdfmny(), tzpzbvo.getYbdfmny()));
//				}else{
//					tzpzbmap.put(key, tzpzbvo);
//					bodyList.add(tzpzbvo);
//				}
//				
//				cvo = null;
//			}
//		}
//		return bodyList;
//	}
//	
//	private YntCpaccountVO getMySelfAccountVO(DcModelBVO bvo, Map<String, YntCpaccountVO> ccountMap, String pk_corp, BankAccountVO bankAccVO){
//		//先判断银行档案
//		if(!StringUtil.isEmpty(bvo.getKmbm()) 
//				&& bvo.getKmbm().startsWith("1002")){
//			String pk_subj = bankAccVO == null ? "" : bankAccVO.getRelatedsubj();
//			if(!StringUtil.isEmpty(pk_subj)){
//				YntCpaccountVO fcvo = ccountMap.get(pk_subj);
//				if(fcvo != null){
//					return fcvo;
//				}
//			}
//		}
//		
//		String newrule = cpaccountService.queryAccountRule(pk_corp);
//
//		String kmnew = null;
//		if (IDefaultValue.DefaultGroup.equals(bvo.getPk_corp())) {
//			kmnew = gl_accountcoderule.getNewRuleCode(bvo.getKmbm(), DZFConstant.ACCOUNTCODERULE, newrule);
//		} else {
//			kmnew = bvo.getKmbm();
//		}
//
//		YntCpaccountVO account = getAccountByCode(kmnew, ccountMap);
//		
//		return account;
//	}
	
	protected YntCpaccountVO getAccountByCode(String code, Map<String, YntCpaccountVO> ccountMap) {
		YntCpaccountVO yntCpaccountVO = null;
		for(Map.Entry<String, YntCpaccountVO> entry : ccountMap.entrySet()){
			yntCpaccountVO = entry.getValue();
			if (yntCpaccountVO.getAccountcode().equals(code))
				return yntCpaccountVO;
		}
		return null;
	}

	@Override
	public void checkBeforeCombine(BankStatementVO2[] vos) throws DZFWarpException {
		DZFDouble sy = DZFDouble.ZERO_DBL;
		DZFDouble zc = DZFDouble.ZERO_DBL;
		String busipk = null;
		for(BankStatementVO2 vo : vos){
			sy = SafeCompute.add(sy, vo.getSyje());
			zc = SafeCompute.add(zc, vo.getZcje());
			
			if(!StringUtil.isEmpty(busipk) && !busipk.equals(vo.getPk_model_h())){
				throw new BusinessException("业务类型不一致,请检查");
			}
			busipk = vo.getPk_model_h();
			if(StringUtil.isEmpty(busipk)){
				throw new BusinessException("业务类型不能为空");
			}
		}
		
		if(sy.doubleValue() != 0 && zc.doubleValue() != 0){
			throw new BusinessException("收入、支出类型不一致");
		}
		BillCategoryVO modelhvo = (BillCategoryVO) singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, 
				busipk);
		
		if(modelhvo == null 
				|| modelhvo.getDr() == 1){
			throw new BusinessException("请重新选择业务类型");
		}
		
	}
	
	@Override
	public void saveCombinePZ(List<BankStatementVO2> list, 
			String pk_corp, 
			String userid, 
			String period,
			BankAccountVO bankAccVO,
			VatInvoiceSetVO setvo,
			boolean accway,
			boolean isT,List<List<Object[]>> levelList,Map<String, Object[]> categoryMap,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Set<String> zyFzhsList
			,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,InventorySetVO inventorySetVO,CorpVO corp,Map<String, InventoryAliasVO> fzhsBMMap
			,List<Object> paramList,Map<String, BdCurrencyVO> currMap,Map<String, Object[]> rateMap,Map<String, String> bankAccountMap,Map<String,YntCpaccountVO> accountMap
			,Map<String, AuxiliaryAccountBVO> assistMap,Map<String, List<AccsetVO>> accsetMap,Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map,Map<String, String> jituanSubMap,YntCpaccountVO[] accVOs
			,String tradeCode,String newrule,List<AuxiliaryAccountBVO> chFzhsBodyVOs) throws DZFWarpException {
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		String pk_curr = yntBoPubUtil.getCNYPk();
		
		checkisGroup(list, pk_corp);//校验
		
		//YntCpaccountVO[] accounts = AccountCache.getInstance().get(null, pk_corp);
		int ifptype  = VATInvoiceTypeConst.VAT_BANK_INVOICE;//银行回单
		int fp_style = VATInvoiceTypeConst.NON_INVOICE;//未开票
		TzpzHVO headVO = new TzpzHVO();
		headVO.setIfptype(ifptype);
		headVO.setFp_style(null);
		
		/*DcModelHVO mHVO = null;
		DcModelBVO[] models = null;
		OcrImageLibraryVO lib = null;*/
		List<TzpzBVO> tblist = new ArrayList<TzpzBVO>();
		List<String> imageGroupList = new ArrayList<String>();//图片组号
		DZFDate maxDate = null;
		List<BankStatementVO2> ll=null;
		for(BankStatementVO2 vo : list){
			if(StringUtils.isEmpty(vo.getPk_model_h())){
				throw new BusinessException("业务类型为空,请重新选择业务类型");
			}
			/*mHVO = dcmap.get(vo.getPk_model_h());
			if(mHVO == null)
				continue;
			models = mHVO.getChildren();
			lib = changeOcrInvoiceVO(vo, mHVO, pk_corp, bankAccVO, ifptype, fp_style);
			aitovoucherserv_bank.getTzpzBVOList(headVO, models, lib, null, tblist, accounts);*/
			ll=new ArrayList<BankStatementVO2>();
			ll.add(vo);
			Map<String, Map<String, Object>> checkMsgMap=new HashMap<String, Map<String, Object>>();
			List<OcrInvoiceVO> invoiceList = changeToOcr(ll, pk_corp);
			List<TzpzHVO> tzpzhvoList = zncsVoucher.processGeneralTzpzVOsByInvoice(invoiceList, period, pk_corp, userid, checkMsgMap, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
			//取出子类
			List<TzpzBVO> pzbvolist = Arrays.asList((TzpzBVO[])tzpzhvoList.get(0).getChildren());
			for (TzpzBVO tzpzBVO : pzbvolist) {
				tzpzBVO.setVdef6(vo.getTradingdate().toString());//临时存放交易日期  分录排序
			}
			tblist.addAll(pzbvolist);
			//图片制单
			if(!StringUtil.isEmpty(vo.getPk_image_group()) && !imageGroupList.contains(vo.getPk_image_group())){
				imageGroupList.add(vo.getPk_image_group());
			}
			
			maxDate = getMaxDate(maxDate, vo);
			
		}
		
		//aitovoucherserv_bank.setSpeaclTzpzBVO1(headVO, lib, tblist);
		
		DZFDouble totalMny = DZFDouble.ZERO_DBL;
		for(TzpzBVO bvo : tblist){
			totalMny = SafeCompute.add(bvo.getJfmny(), totalMny);
		}
		
		list.get(0).setInperiod(DateUtils.getPeriod(maxDate));//将入账期间设置为最大的
		list.get(0).setCount(list.size());
		createTzpzHVO(headVO, list, pk_corp, userid, pk_curr, totalMny, accway,setvo);
		
		List<String> pks = new ArrayList<String>();
		for(BankStatementVO2 svo : list){
			pks.add(svo.getPrimaryKey());
		}
		
		//合并图片组
		if(imageGroupList.size() > 0){
			String pk_img_group = img_groupserv.processMergeGroup(pk_corp, null, imageGroupList);
			headVO.setPk_image_group(pk_img_group);
		}
		
		List<TzpzBVO> bodyAllList2 = constructItems(tblist, setvo, list.get(0), pk_corp);
		
		headVO.setChildren(bodyAllList2.toArray(new TzpzBVO[0]));
		

		if(isT){
			createTempPZ(headVO, list.toArray(new BankStatementVO2[list.size()]), pk_corp);
		}else{
			String sourcebillid = SqlUtil.buildSqlConditionForInWithoutDot(pks.toArray(new String[pks.size()]));
			headVO.setSourcebillid(sourcebillid);
			headVO = voucher.saveVoucher(corpvo, headVO);
		}
	}
	
	private List<TzpzBVO> constructItems(List<TzpzBVO> tzpzlist,
			VatInvoiceSetVO setvo,
			BankStatementVO2 vo,
			String pk_corp){
		//合并同类项
		DZFBoolean isbk = setvo.getIsbank();//是否合并银行科目
		isbk = isbk == null ? DZFBoolean.FALSE : DZFBoolean.TRUE;
		Integer type = setvo.getEntry_type();//凭证分录合并规则
		type = type == null ? IBillManageConstants.HEBING_FL_02 : type;//默认 同方向分录合并
		
		Integer vvvalue = setvo.getValue();//凭证合并的规则
		vvvalue = vvvalue == null ? IBillManageConstants.HEBING_GZ_01 : vvvalue;//默认不合并
		
		List<TzpzBVO> finalList = new ArrayList<TzpzBVO>();
		List<TzpzBVO> afterList = new ArrayList<TzpzBVO>();
		Map<String,TzpzBVO> tzpzbmap = new HashMap<String, TzpzBVO>();
		TzpzBVO temptzpz = null;
		String key;
		
		//合并后 分录按借借贷
//		if((type == IBillManageConstants.HEBING_FL_02 
//				|| type == IBillManageConstants.HEBING_FL_04) && tzpzlist.size() > 2){
//		if(vvvalue != IBillManageConstants.HEBING_GZ_01
//				&& type != IBillManageConstants.HEBING_FL_03){
//			sortEntryByDirection(tzpzlist, pk_corp);
//		}
		sortEntryByDirection(tzpzlist, pk_corp);
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		String zy = buildZy(setvo, vo, map);
		Boolean isaddZy = map.get("flag");
		
		int rowno = 1;
		for(TzpzBVO tzpzbvo : tzpzlist){
			if(isaddZy&&vvvalue != IBillManageConstants.HEBING_GZ_01){
				tzpzbvo.setZy(zy);
			}
			
			if(type == IBillManageConstants.HEBING_FL_02
					&& (isbk.booleanValue() ? !tzpzbvo.getVcode().startsWith("1002") : true)){
				key = constructTzpzKey(tzpzbvo, setvo);
				
				if(tzpzbmap.containsKey(key)){
					temptzpz = tzpzbmap.get(key);
					if(!isaddZy){
						temptzpz.setZy(mergeZy(temptzpz, tzpzbvo));
					}
					temptzpz.setJfmny(SafeCompute.add(temptzpz.getJfmny(), tzpzbvo.getJfmny()));
					temptzpz.setYbjfmny(SafeCompute.add(temptzpz.getYbjfmny(), tzpzbvo.getYbjfmny()));
					temptzpz.setDfmny(SafeCompute.add(temptzpz.getDfmny(), tzpzbvo.getDfmny()));
					temptzpz.setYbdfmny(SafeCompute.add(temptzpz.getYbdfmny(), tzpzbvo.getYbdfmny()));
					if (tzpzbvo.getNnumber() != null) {
						temptzpz.setNnumber(SafeCompute.add(temptzpz.getNnumber(), tzpzbvo.getNnumber()));
						if (temptzpz.getDfmny() != null && temptzpz.getDfmny().doubleValue() > 0) {
							temptzpz.setNprice(SafeCompute.div(temptzpz.getDfmny(), temptzpz.getNnumber()));
						}
						if (temptzpz.getJfmny() != null && temptzpz.getJfmny().doubleValue() > 0) {
							temptzpz.setNprice(SafeCompute.div(temptzpz.getJfmny(), temptzpz.getNnumber()));
						}
					}
				}else{
					tzpzbmap.put(key, tzpzbvo);
					tzpzbvo.setRowno(rowno++);
					finalList.add(tzpzbvo);
				}
			}else if(type == IBillManageConstants.HEBING_FL_04
					&& (isbk.booleanValue() ? !tzpzbvo.getVcode().startsWith("1002") : true)){
				
				key = constructTzpzKey(tzpzbvo, setvo);
				
				if(tzpzbmap.containsKey(key)){
					temptzpz = tzpzbmap.get(key);
					if(!isaddZy){
						temptzpz.setZy(mergeZy(temptzpz, tzpzbvo));
					}
					
					if(temptzpz.getNnumber() != null || tzpzbvo.getNnumber() != null){
						if(temptzpz.getVdirect() == tzpzbvo.getVdirect()){
							temptzpz.setNnumber(SafeCompute.add(temptzpz.getNnumber(), tzpzbvo.getNnumber()));
						}else{
							DZFDouble mny1 = null;
							DZFDouble mny2 = null;
							if (temptzpz.getVdirect() == 0) {
								mny1 = temptzpz.getJfmny();
							} else {
								mny1 = temptzpz.getDfmny();
							}
							if (tzpzbvo.getVdirect() == 0) {
								mny2 = tzpzbvo.getJfmny();
							} else {
								mny2 = tzpzbvo.getDfmny();
							}
							DZFDouble number = null;
							if (mny1.compareTo(mny2) >= 0) {
								number = SafeCompute.sub(temptzpz.getNnumber(), tzpzbvo.getNnumber());
							} else {
								number = SafeCompute.sub(tzpzbvo.getNnumber(), temptzpz.getNnumber());
							}
							temptzpz.setNnumber(number);
						}
					}
					
					temptzpz.setJfmny(SafeCompute.add(temptzpz.getJfmny(), tzpzbvo.getJfmny()));
					temptzpz.setYbjfmny(SafeCompute.add(temptzpz.getYbjfmny(), tzpzbvo.getYbjfmny()));
					temptzpz.setDfmny(SafeCompute.add(temptzpz.getDfmny(), tzpzbvo.getDfmny()));
					temptzpz.setYbdfmny(SafeCompute.add(temptzpz.getYbdfmny(), tzpzbvo.getYbdfmny()));
					
					if(temptzpz.getJfmny().doubleValue() != 0 && temptzpz.getDfmny().doubleValue() != 0){
						if(temptzpz.getJfmny().compareTo(temptzpz.getDfmny()) >= 0){
							temptzpz.setJfmny(SafeCompute.sub(temptzpz.getJfmny(), temptzpz.getDfmny()));
							temptzpz.setDfmny(DZFDouble.ZERO_DBL);
							
						}else{
							temptzpz.setDfmny(SafeCompute.sub(temptzpz.getDfmny(), temptzpz.getJfmny()));
						}
					}
					if (temptzpz.getNnumber() != null) {
//						temptzpz.setNnumber(SafeCompute.add(temptzpz.getNnumber(), tzpzbvo.getNnumber()));
						if (temptzpz.getDfmny() != null && temptzpz.getDfmny().doubleValue() > 0) {
							temptzpz.setNprice(SafeCompute.div(temptzpz.getDfmny(), temptzpz.getNnumber()));
						}
						if (temptzpz.getJfmny() != null && temptzpz.getJfmny().doubleValue() > 0) {
							temptzpz.setNprice(SafeCompute.div(temptzpz.getJfmny(), temptzpz.getNnumber()));
						}
					}
				}else{
					tzpzbmap.put(key, tzpzbvo);
					tzpzbvo.setRowno(rowno++);
					finalList.add(tzpzbvo);
				}
			}else{
				tzpzbvo.setRowno(rowno++);
				finalList.add(tzpzbvo);
			}
			
		}
		for (TzpzBVO bvo : finalList) {
			if(bvo.getJfmny()!=null&&bvo.getJfmny().doubleValue()!=0||(bvo.getDfmny()!=null&&bvo.getDfmny().doubleValue()!=0)){
				afterList.add(bvo);
			}
		}
		if(afterList!=null&&afterList.size()>0){
			return afterList;
		}else{
			return finalList;
		}
		
	}
	
	private void sortEntryByDirection(List<TzpzBVO> list, String pk_corp){
		if(list == null || list.size() == 0)
			return;
		Map<String, AuxiliaryAccountBVO> assistMap = gl_fzhsserv.queryMap(pk_corp);
		BdCurrencyVO[] currencyvos = gl_qmclserv.queryCurrency();
		Map<String, BdCurrencyVO> currencyMap = new HashMap<String, BdCurrencyVO>();
		if(currencyvos != null && currencyvos.length > 0){
			for (BdCurrencyVO bdCurrencyVO : currencyvos) {
				currencyMap.put(bdCurrencyVO.getPk_currency(), bdCurrencyVO);
			}
		}
		
		for(TzpzBVO bvo : list){
			setEntryFullcode(bvo, assistMap, currencyMap);
		}
		
		sortVoucherEntry(list, pk_corp);
	}
	
	private void sortVoucherEntry(List<TzpzBVO> bvos, String pk_corp) {
		CorpVO corpVO = corpService.queryByPk(pk_corp);
		String taxCode = null;
		if ("00000100AA10000000000BMD".equals(corpVO.getCorptype())
				|| "00000100AA10000000000BMF".equals(corpVO.getCorptype())) {
			// 进项税额，待认证进项税额，销项税额
			taxCode = "^(22210?13|22210+10+(1|2))";
		} else if ("00000100000000Ig4yfE0005".equals(corpVO.getCorptype())) {
			// 企业会计制度 进项税额，销项税额
			taxCode = "^21710+10+(1|5)";
		} else if ("00000100AA10000000000BMQ".equals(corpVO.getCorptype())) {
			// 民间非盈利 进项税额，待抵扣进项税，销项税额
			taxCode = "^2206(0?10|0+10+(1|2))";
		}
		final String taxMatch = taxCode;
	    Collections.sort(bvos, new Comparator<TzpzBVO>() {
	        @Override
	        public int compare(TzpzBVO o1, TzpzBVO o2) {
	            int jf1 = o1.getJfmny() == null
	                    || o1.getJfmny().doubleValue() == 0 ? 0 : 1;
	            int jf2 = o2.getJfmny() == null
	                    || o2.getJfmny().doubleValue() == 0 ? 0 : 1;
	            int cp = jf2 - jf1;
	            if (cp == 0) {
	            	String period1=o1.getVdef6();
	            	String period2=o2.getVdef6();
	            	cp = period1.compareTo(period2);
	            	if(cp==0){
		            	String code1 = o1.getFullcode() == null ? o1.getVcode() : o1.getFullcode();
		            	String code2 = o2.getFullcode() == null ? o2.getVcode() : o2.getFullcode();
		            	if (taxMatch != null) {
		            		if (code1.matches(taxMatch) && !code2.matches(taxMatch)) {
			            		code1 = "999";
							} else if (code2.matches(taxMatch) && !code1.matches(taxMatch)) {
								code2 = "999";
							}
						}
		                cp = code1.compareTo(code2);	
	            	}	
	            }
	            return cp;
	        }
	    });
	}
	
	private void setEntryFullcode(TzpzBVO vo,
			Map<String, AuxiliaryAccountBVO> assistMap,
			Map<String, BdCurrencyVO> currencyMap) {
		StringBuilder fullcode = new StringBuilder();
		if (vo.getVcode() != null) {
			fullcode.append(vo.getVcode());
		}
		for (int i = 1; i <= 10; i++) {
			String fzhsID = (String) vo.getAttributeValue("fzhsx" + i);
			if (i == 6 && StringUtil.isEmpty(fzhsID)) {
				fzhsID = vo.getPk_inventory();
			}
			if (!StringUtil.isEmpty(fzhsID)) {
				AuxiliaryAccountBVO assist = assistMap.get(fzhsID);
				if (assist != null) {
					fullcode.append("_").append(assist.getCode());
				}
			}
		}
		if (vo.getPk_currency() != null
				&& !IGlobalConstants.RMB_currency_id
						.equals(vo.getPk_currency())) {
			if (currencyMap.containsKey(vo.getPk_currency())) {
				fullcode.append("_").append(
						currencyMap.get(vo.getPk_currency()).getCurrencycode());
			}
		}
		vo.setFullcode(fullcode.toString());
	}
	
	private String buildZy(VatInvoiceSetVO setvo,
			BankStatementVO2 vo,
			Map<String, Boolean> map){
		Boolean flag = false;
		StringBuffer sf = new StringBuffer();
		String zy = setvo.getZy();
		if(!StringUtil.isEmpty(zy) 
				&& zy.contains("$")){
			if(zy.contains("selectZdyZy:")){
				int beginindex = zy.lastIndexOf("selectZdyZy:");
				int endindex = zy.lastIndexOf("$");
				String ss = zy.substring(beginindex + 12, endindex);
				sf.append(ss);
				flag = true;
			}
		}
		
		map.put("flag", flag);
		String str = sf.toString();
		str = str.length() > 200 ? str.substring(0, 200) : str;
		return str;
	}
	
	private String mergeZy(TzpzBVO b1, TzpzBVO b2){
		String zy = "";
		String z1 = b1.getZy();
		String z2 = b2.getZy();
		if(StringUtil.isEmpty(z1)){
			zy = z2;
		}else if(StringUtil.isEmpty(z2)){
			zy = z1;
		}else if(!z1.contains(z2)){
			zy = z1 + "," + z2;
		}else{
			zy = z1;
		}
		
		return zy;
	}
	
	protected String constructTzpzKey(TzpzBVO bvo, VatInvoiceSetVO setvo) {
		StringBuffer sf = new StringBuffer();
		Integer type = setvo.getEntry_type();
		type = type == null ? IBillManageConstants.HEBING_FL_02 : type;//默认 同方向分录合并
		if(type != IBillManageConstants.HEBING_FL_04){
			sf.append("&").append(bvo.getVdirect());
		}
		
		sf.append("&").append(bvo.getPk_accsubj()).append("&")
			.append(bvo.getPk_inventory()).append("&").append(bvo.getPk_taxitem()).append("&");

		for (int i = 1; i <= 10; i++) {
			sf.append(bvo.getAttributeValue("fzhsx" + i)).append("&");
		}

		return sf.toString();
	}
	
	/**
	 * 合并凭证图片组
	 * 
	 * @param pk_corp
	 * @param imageGroupList
	 * @return
	 */
	private String mergeImage(String pk_corp,
			List<String> imageGroupList,
			List<String> pks) {
		int count = imageGroupList.size();
		String groupId = imageGroupList.get(0);
		if (count == 1) {
			return groupId;
		}
		imageGroupList.remove(0);
		String inSQL = SQLHelper.getInSQL(imageGroupList);
		SQLParameter sp = new SQLParameter();
		sp.addParam(groupId);
		sp.addParam(pk_corp);
		for (String pk_group : imageGroupList) {
			sp.addParam(pk_group);
		}
		singleObjectBO.executeUpdate(
				"update ynt_image_library set pk_image_group = ? where pk_corp = ? and pk_image_group in " + inSQL
						+ " and nvl(dr,0)=0 ",
				sp);
		singleObjectBO.deleteByPKs(ImageGroupVO.class, imageGroupList.toArray(new String[0]));
		
		inSQL = SQLHelper.getInSQL(pks);
		sp.clearParams();
		sp.addParam(groupId);
		sp.addParam(pk_corp);
		for(String pk : pks){
			sp.addParam(pk);
		}
		singleObjectBO.executeUpdate(
				" update ynt_bankstatement set pk_image_group = ? where pk_corp = ? and nvl(dr,0) = 0 and pk_bankstatement in  " + inSQL
				, sp);

		return groupId;
	}
	
//	public void combinePZ(List<BankStatementVO2> list, 
//			String pk_corp, 
//			String userid, 
//			Map<String, DcModelHVO> dcmap,
//			BankAccountVO bankAccVO,
//			VatInvoiceSetVO setvo,
//			boolean isT) throws DZFWarpException {
//		CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
//		String pk_curr = yntBoPubUtil.getCNYPk();
//		
//		Map<String, YntCpaccountVO> accountMap = AccountCache.getInstance().getMap(userid, pk_corp);
//		
//		TzpzHVO headVO = null;
//		List<TzpzBVO> bodyList = null;
//		Map<String,Boolean> isTempMap = new HashMap<String,Boolean>();
//		
//		Boolean isTemp = false;
//		
//		isTempMap.put(ISTEMP, false);
//		
//		Map<String, BankStatementVO2> bkmap = new HashMap<String, BankStatementVO2>();
//		BankStatementVO2 innervo = null;
//		for(BankStatementVO2 vo : list){
//			if(StringUtil.isEmpty(vo.getOthaccountname())){
//				vo.setOthaccountname("$$$");
//			}
//			if(bkmap.containsKey(vo.getOthaccountname())){
//				innervo = bkmap.get(vo.getOthaccountname());
//				innervo.setSyje(SafeCompute.add(innervo.getSyje(), vo.getSyje()));
//				innervo.setZcje(SafeCompute.add(innervo.getZcje(), vo.getZcje()));
//			}else{
//				bkmap.put(vo.getOthaccountname(), vo);
//			}
//		}
//		
//		bodyList = createCombinTzpzBVO(list, bkmap, mHVO, pk_curr, accountMap, isTempMap, pk_corp, bankAccVO, true);
//		DZFDouble totalMny = DZFDouble.ZERO_DBL;
//		for(TzpzBVO bvo : bodyList){
//			totalMny = SafeCompute.add(bvo.getJfmny(), totalMny);
//		}
//		
//		headVO = createTzpzHVO(list.get(0), pk_corp, userid, pk_curr, accountMap, totalMny, isTempMap);
//		
//		List<String> pks = new ArrayList<String>();
//		for(BankStatementVO2 svo : list){
//			pks.add(svo.getPrimaryKey());
//		}
//		
//		headVO.setChildren(bodyList.toArray(new TzpzBVO[0]));
//		
//		isTemp = isTempMap.get(ISTEMP);
//		if(isTemp || isT){
//			createTempPZ(headVO, list.toArray(new BankStatementVO2[list.size()]), pk_corp);
//		}else{
//			String sourcebillid = SqlUtil.buildSqlConditionForInWithoutDot(pks.toArray(new String[pks.size()]));
//			headVO.setSourcebillid(sourcebillid);
//			headVO = voucher.saveVoucher(corpvo, headVO);
//		}
//	}

/*	@Override
	public List<String> getBusiTypes(String pk_corp) throws DZFWarpException {
		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
		List<String> busiList = new LinkedList<String>();
		
		if(dcList != null && dcList.size() > 0){
			Collections.sort(dcList, new Comparator<DcModelHVO>() {
				@Override
				public int compare(DcModelHVO o1, DcModelHVO o2) {
					int i = o1.getBusitypetempname().compareTo(o2.getBusitypetempname());
					return i;
				}
			});
			
			dcList = new DcPzmb().filterDataCommon(dcList, pk_corp,
					"Y", null, null, null);
			
			Map<String, String> map = new HashMap<String, String>();
			if(dcList != null && dcList.size() > 0){
				String businame;
				for(DcModelHVO hvo : dcList){
					businame = hvo.getBusitypetempname();
					if(!map.containsKey(businame)){
						map.put(businame, businame);
						busiList.add(businame);
					}
				}
			}
			
//			String vscode = null;
//			String busiName = null;
//			String keywords = null;
//			for(DcModelHVO hvo : dcList){
//				vscode = hvo.getVspstylecode();
//				busiName = hvo.getBusitypetempname();
//				keywords = hvo.getKeywords();
//				if(FieldConstant.FPSTYLE_20.equals(vscode)
//						&& !StringUtil.isEmpty(busiName)
//						&& !busiMap.containsKey(busiName)
//						&& !StringUtil.isEmpty(keywords)
//						&& keywords.startsWith("中国农业银行")){
//					busiList.add(hvo.getBusitypetempname());
//					busiMap.put(busiName, busiName);
//				}
//			}
		}
		return busiList;
	}*/

	@Override
	public List<BankStatementVO2> queryByPks(String[] pks, String pk_corp) throws DZFWarpException {
		
		String wherePart = SqlUtil.buildSqlForIn("pk_bankstatement", pks);
		
		SQLParameter sp = new SQLParameter();
		StringBuffer sb = new StringBuffer();
		sb.append(" select pk_bankstatement,y.coperatorid,y.doperatedate,y.pk_corp,y.batchflag,y.pk_bankaccount,y.tradingdate, ");
		sb.append("        y.zy,y.pk_model_h,y.busitypetempname,y.syje,y.zcje,y.othaccountname,y.othaccountcode,y.ye,y.pk_tzpz_h, ");
		sb.append("        y.sourcetem,y.sourcetype,y.period,y.billstatus,y.modifyoperid,y.modifydatetime,y.dr,y.ts, h.pzh  ");
		sb.append(" from ynt_bankstatement y ");
		sb.append(" left join ynt_tzpz_h h ");
		sb.append("   on y.pk_tzpz_h = h.pk_tzpz_h ");
		sb.append(" where y.pk_corp=? and nvl(y.dr,0)=0");
		sb.append(" and ");
		sb.append(wherePart);
		
		sp.addParam(pk_corp);
		
		List<BankStatementVO2> listvo = (List<BankStatementVO2>) singleObjectBO.executeQuery(sb.toString(), 
				sp, new BeanListProcessor(BankStatementVO2.class));
		
		return listvo;
	}

//	public void saveVOs(String pk_corp, List<BankStatementVO2> list) throws DZFWarpException {
//		if(list == null || list.size() == 0){
//			log.info("银行回单未生成对账单，公司主键为:"+ pk_corp);
//			return;
//		}
//		
//		List<BankAccountVO> bankAccList = yhzhserv.query(pk_corp, "Y");//不包含停用
//		Map<String, BankAccountVO> bankAccMap = hashliseBankAccMap(bankAccList);
//		
//		Set<String> periodSet = new HashSet<String>();
//		String myBankAcc;//已方银行账号
//		String period;
//		String pk_bankaccount;
//		String key;
//		for(BankStatementVO2 vo : list){
//			myBankAcc = vo.getVdef2();//约定的  已方银行账号
//			pk_bankaccount = null;
//			if(!StringUtil.isEmpty(myBankAcc)
//					&& bankAccMap.containsKey(myBankAcc)){
//				pk_bankaccount = bankAccMap.get(myBankAcc).getPk_bankaccount();
//				vo.setPk_bankaccount(pk_bankaccount);//银行账户主键
//			}
//			
//			if(vo.getTradingdate() == null){
//				vo.setDr(1);
//				vo.setMemo("该数据没有交易日期，无法进行匹配<br>");
//				continue;
//			}else{
//				period = DateUtils.getPeriod(vo.getTradingdate());
//				key = pk_bankaccount + "_" + period;
//				if(!periodSet.contains(key)){
//					periodSet.add(key);
//				}
//			}
//			
//		}
//		
//		if(periodSet.size() == 0)
//			return;
//		
//		//查询涉及到的数据
//		List<BankStatementVO2> bankStateList = queryDatasByPeriod(periodSet, pk_corp);
//		Map<String, BankStatementVO2> bankStateMap = hashliseBankStatementMap(bankStateList);
//		
//		key = null;
//		BankStatementVO2 dzdvo;
//		List<BankStatementVO2> inList = new ArrayList<BankStatementVO2>();
//		List<BankStatementVO2> upList = new ArrayList<BankStatementVO2>();
//		for(BankStatementVO2 vo : list){
//			if(vo.getDr() == 1)
//				continue;
//			key = buildKey(new String[]{
//					vo.getTradingdate().toString(),
//					vo.getPk_bankaccount(),
//					vo.getSyje().doubleValue() + "",
//					vo.getZcje().doubleValue() + "",
//					vo.getOthaccountname()
//			});
//			
//			if(bankStateMap.containsKey(key)){
//				dzdvo = bankStateMap.get(key);
//				dzdvo.setBillstatus(BankStatementVO2.STATUS_2);
//				dzdvo.setSourcebillid(vo.getSourcebillid());
//				dzdvo.setPk_image_group(vo.getPk_image_group());
//				dzdvo.setImgpath(vo.getImgpath());
//				if(StringUtil.isEmpty(dzdvo.getPk_tzpz_h())){//凭证号
//					dzdvo.setPk_tzpz_h(vo.getPk_tzpz_h());
//					dzdvo.setPzh(vo.getPk_tzpz_h());
//				}
//				
//				if(StringUtil.isEmpty(dzdvo.getPk_model_h())){//业务类型模板
//					dzdvo.setBusitypetempname(vo.getBusitypetempname());
//					dzdvo.setPk_model_h(vo.getPk_model_h());
//				}
//				dzdvo.setMemo("回单匹配上对账单<br>");
//				upList.add(dzdvo);
//			}else{
//				vo.setBillstatus(BankStatementVO2.STATUS_1);
//				vo.setMemo("回单新增对账单<br>");
//				inList.add(vo);
//			}
//			
//		}
//		
//		if(inList.size() > 0){
//			singleObjectBO.insertVOArr(pk_corp, 
//					inList.toArray(new BankStatementVO2[0]));
//		}
//		if(upList.size() > 0){
//			singleObjectBO.updateAry(upList.toArray(new BankStatementVO2[0]), 
//					new String[]{"billstatus", "sourcebillid", "pk_image_group", "imgpath",
//							"pk_tzpz_h", "pzh", "busitypetempname", "pk_model_h", "memo"});
//		}
//		
//	}
	
	private List<BankBillToStatementVO> queryBankBillByPeriod(Set<String> set, String pk_corp) throws DZFWarpException{
		
		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append(" Select * From ynt_bankbilltostatement y join ynt_bankstatement b on y.pk_bankstatement = b.pk_bankstatement ");
		sf.append(" Where y.pk_corp = ? and nvl(y.dr,0) = 0  ");
		sf.append(" and b.billstatus = ? and ( 1 <> 1 ");
		sp.addParam(pk_corp);
		sp.addParam(BankStatementVO2.STATUS_1);
		String[] arr;
		DZFDate bdate;
		DZFDate edate;
		for(String s : set){
			arr = s.split("_");
			sf.append(" or ( ");
			if(StringUtil.isEmpty(arr[0])){//pk_bankaccount
				sf.append(" y.pk_bankaccount is null ");
			}else{
				sf.append(" y.pk_bankaccount = ? ");
				sp.addParam(arr[0]);
			}
			
			bdate = DateUtils.getPeriodStartDate(arr[1]);//期间
			edate = DateUtils.getPeriodEndDate(arr[1]);
			
			sf.append(" and y.tradingdate >= ? and y.tradingdate <= ? ) ");
			
			sp.addParam(bdate);
			sp.addParam(edate);
		}
		
		sf.append(" ) order by y.ts desc ");
		List<BankBillToStatementVO> list = (List<BankBillToStatementVO>) singleObjectBO.executeQuery(sf.toString(), 
				sp, new BeanListProcessor(BankBillToStatementVO.class));
		
		return list;
	}
	
	private String buildKey(String[] keys){
		StringBuffer sf = new StringBuffer();
		int length = keys.length;
		for(int i = 0; i < length; i++){
			if(StringUtil.isEmpty(keys[i])){
				sf.append("");
			}else{
				sf.append(keys[i]);
			}
			
			if(i != length - 1){
				sf.append(",");
			}
		}
		
		return sf.toString();
	}
	
//	private Map<String, BankAccountVO> hashliseBankAccMap(List<BankAccountVO> list){
//		Map<String, BankAccountVO> map = new HashMap<String, BankAccountVO>();
//		
//		if(list != null && list.size() > 0){
//			String key = null;
//			for(BankAccountVO vo : list){
//				key = vo.getBankaccount();//银行账号
//				if(!map.containsKey(key)){
//					map.put(key, vo);
//				}
//			}
//			
//		}
//		return map;
//	}

	@Override
	public BankStatementVO2 queryByBankBill(String pk_corp, BankBillToStatementVO vo) throws DZFWarpException {
		
		DZFDate tradingdate = vo.getTradingdate();
		if(tradingdate == null){
			return null;//交易日期为空，不在查询
		}
		
		BankStatementVO2 bankvo = queryBankVOByImgPk(vo, pk_corp);
		if(bankvo != null){
			return bankvo;
		}
		
		String myBankAcc = vo.getMyaccountcode();//约定的  已方银行账号
		String pk_bankaccount = getYhzh(myBankAcc, pk_corp);//银行账户主键
		
		List<BankStatementVO2> bankStateList = queryDataByTradingdate(vo, pk_bankaccount, pk_corp, true);
		
		if(bankStateList == null || bankStateList.size() == 0){
			return null;
		}
		
		Map<String, BankStatementVO2> bankStateMap = hashliseBankStatementMap(bankStateList);
		
		String key = buildKey(new String[]{
				vo.getTradingdate().toString(),
				pk_bankaccount,
				getDefaultMnyValue(vo.getSyje()),
				getDefaultMnyValue(vo.getZcje()),
				vo.getOthaccountname()
		});
		
		if(bankStateMap.containsKey(key)){
			return bankStateMap.get(key);
		}
		
		return null;
	}
	
	private BankStatementVO2 queryBankVOByImgPk(BankBillToStatementVO vo, String pk_corp){
		String pk = vo.getPk_image_ocrlibrary();
		if(StringUtil.isEmpty(pk)){
			return null;
		}
		if(!StringUtil.isEmpty(pk)){
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(pk);
			OcrImageLibraryVO[] libvos = (OcrImageLibraryVO[]) singleObjectBO.queryByCondition(OcrImageLibraryVO.class,
					" nvl(dr,0)=0 and pk_corp = ? and pk_image_ocrlibrary = ? ", sp);
			if(libvos != null && libvos.length > 0){
				pk = libvos[0].getCrelationid();//更新成图片信息表主键
			}
		}
		
		StringBuffer sf = new StringBuffer();
//		sf.append(" Select * From ynt_bankbilltostatement t Where ");
		sf.append(" nvl(dr,0) = 0 and pk_corp = ? and sourcebillid = ? ");
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk);
		
		BankStatementVO2[] vos = (BankStatementVO2[]) singleObjectBO.queryByCondition(BankStatementVO2.class, 
				sf.toString(), sp);
		
		return vos == null || vos.length == 0 
				? null :vos[0];
	}
	
	private Map<String, List<BankBillToStatementVO>> hashliseBankBillMap(List<BankBillToStatementVO> list){
		Map<String, List<BankBillToStatementVO>> map = new HashMap<String, List<BankBillToStatementVO>>();
		
		if(list == null || list.size() == 0){
			return map;
		}
		
		String key;
		List<BankBillToStatementVO> bklist;
		for(BankBillToStatementVO vo : list){

			key = buildKey(new String[]{
					vo.getTradingdate().toString(),
					vo.getPk_bankaccount(),
					getDefaultMnyValue(vo.getSyje()),
					getDefaultMnyValue(vo.getZcje()),
					vo.getOthaccountname()
			});
			
			if(map.containsKey(key)){
				bklist = map.get(key);
			    bklist.add(vo);
			}else{
				bklist = new ArrayList<BankBillToStatementVO>();
				bklist.add(vo);
				map.put(key, bklist);
			}
		}
		
		return map;
	}
	
	private Map<String, BankStatementVO2> hashliseBankStatementMap(List<BankStatementVO2> list){
		Map<String, BankStatementVO2> map = new HashMap<String, BankStatementVO2>();
		
		if(list == null || list.size() == 0){
			return map;
		}
		
		String key;
		for(BankStatementVO2 vo : list){

			key = buildKey(new String[]{
					vo.getTradingdate().toString(),
					vo.getPk_bankaccount(),
					getDefaultMnyValue(vo.getSyje()),
					getDefaultMnyValue(vo.getZcje()),
					vo.getOthaccountname()
			});
			
			if(!map.containsKey(key)){
				map.put(key, vo);
			}
		}
		
		return map;
	}
	
	private List<BankStatementVO2> queryDataByTradingdate(BankBillToStatementVO vo,
			String pk_bankaccount,
			String pk_corp,
			boolean isY) throws DZFWarpException{
		
		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append(" Select * From ynt_bankstatement y Where y.pk_corp = ? ");
		sf.append(" and nvl(y.dr,0) = 0 and y.sourcebillid is null ");
		sp.addParam(pk_corp);
		
		if(StringUtil.isEmpty(pk_bankaccount)){//pk_bankaccount
			sf.append(" and y.pk_bankaccount is null ");
		}else{
			sf.append(" and y.pk_bankaccount = ? ");
			sp.addParam(pk_bankaccount);
		}
		
		sf.append(" and y.tradingdate = ? ");
		sp.addParam(vo.getTradingdate());
		
		if(isY){
			sf.append(" and y.billstatus = ? ");
			sp.addParam(BankStatementVO2.STATUS_0);//只匹配银行对账单未绑定的数据
		}
		
		sf.append(" order by ts desc ");
		
		List<BankStatementVO2> list = (List<BankStatementVO2>) singleObjectBO.executeQuery(sf.toString(), 
				sp, new BeanListProcessor(BankStatementVO2.class));
		
		return list;
	}
	
	private String getYhzh(String myBankAcc, String pk_corp){
		
		if(!StringUtil.isEmpty(myBankAcc)){
			BankAccountVO[] vos = yhzhserv.queryByCode(myBankAcc, pk_corp);
			
			if(vos != null && vos.length > 0)
				return vos[0].getPk_bankaccount();
		}
		
		return null;
	}

	@Override
	public boolean saveBankBill(String pk_corp, BankBillToStatementVO billvo, BankStatementVO2 bankvo)
			throws DZFWarpException {
		//先转换值 souceid 传回来的是 pk_image_ocrlibrary，
		//更新成 pk_image_library 主键
		//pk_bankaccount 重新赋值
		transBankBillValue(pk_corp, billvo, bankvo);
		
		if(bankvo != null){//回单绑定上了

			boolean isSave = !StringUtil.isEmpty(bankvo.getSourcebillid()) 
					&& bankvo.getSourcebillid().equals(billvo.getSourcebillid()) ? true : false;
			
			List<String> filedList = new ArrayList<String>();
			if(!StringUtil.isEmpty(billvo.getPk_tzpz_h())
					&& StringUtil.isEmpty(bankvo.getPk_tzpz_h())){//对账单没有生成凭证，将回单的凭证号回写
				bankvo.setPk_tzpz_h(billvo.getPk_tzpz_h());
				bankvo.setPzh(billvo.getPzh());
				
				filedList.add("pk_tzpz_h");
				filedList.add("pzh");
			}
			
			bankvo.setSourcebillid(billvo.getSourcebillid());
			bankvo.setImgpath(billvo.getImgpath());
			bankvo.setPk_image_group(billvo.getPk_image_group());
			bankvo.setPk_image_library(billvo.getPk_image_library());
			bankvo.setBillstatus(BankStatementVO2.STATUS_2);//绑定关系
			bankvo.setInperiod(billvo.getPeriod());//入账期间
			filedList.add("sourcebillid");
			filedList.add("imgpath");
			filedList.add("pk_image_group");
			filedList.add("billstatus");
			filedList.add("inperiod");//更新入账期间
			
			singleObjectBO.update(bankvo, 
					filedList.toArray(new String[0]));
			
			billvo.setPk_bankaccount(bankvo.getPk_bankaccount());
			billvo.setPk_bankstatement(bankvo.getPrimaryKey());//对账单主键
			billvo.setMemo("回单匹配上对账单<br>");
			
			if(!isSave){
				singleObjectBO.insertVOArr(pk_corp, 
						new BankBillToStatementVO[]{ billvo });
			}
		}else{//未绑定
			BankStatementVO2 vo = new BankStatementVO2();
			BeanUtils.copyNotNullProperties(billvo, vo);
			vo.setInperiod(billvo.getPeriod());// 更新入账期间
			vo.setBillstatus(BankStatementVO2.STATUS_1);//来源回单
			vo.setSourcetem(BankStatementVO2.SOURCE_100);
			vo.setSourcetype(BankStatementVO2.SOURCE_100);
			
			String pk = singleObjectBO.insertVOWithPK(vo);
			
			billvo.setPk_bankstatement(pk);//对账单主键
			billvo.setMemo("回单未匹配上,新增对账单<br>");
			
			singleObjectBO.insertVOArr(pk_corp, new BankBillToStatementVO[]{ billvo });
		}
		
		
		return true;
	}
	
	private void transBankBillValue(String pk_corp, BankBillToStatementVO billvo, BankStatementVO2 bankvo){
		if(bankvo == null){
			String myBankAcc = billvo.getMyaccountcode();//约定的  已方银行账号
			String pk_bankaccount = getYhzh(myBankAcc, pk_corp);//银行账户主键
			billvo.setPk_bankaccount(pk_bankaccount);
		}
		
		String pk_image_ocrlibrary = billvo.getPk_image_ocrlibrary();//
		if(!StringUtil.isEmpty(pk_image_ocrlibrary)){
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(pk_image_ocrlibrary);
			OcrImageLibraryVO[] libvos = (OcrImageLibraryVO[]) singleObjectBO.queryByCondition(OcrImageLibraryVO.class, 
					" nvl(dr,0)=0 and pk_corp = ? and pk_image_ocrlibrary = ? ", sp);
			if(libvos != null && libvos.length > 0){
				billvo.setSourcebillid(libvos[0].getCrelationid());//更新成图片信息表主键
			}
		}
	}

	@Override
	public void deleteBankBillByLibrary(List<String> libpks, String pk_corp) throws DZFWarpException {
		//查询中间表设计到的图片信息
		SQLParameter sp = new SQLParameter();
		
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_bankbilltostatement y where nvl(dr,0)=0 and pk_corp = ? and pk_bankstatement is not null and ");
		sf.append(SqlUtil.buildSqlForIn("sourcebillid", libpks.toArray(new String[0])));
		
		sp.addParam(pk_corp);
		
		List<BankBillToStatementVO> billList = (List<BankBillToStatementVO>) singleObjectBO.executeQuery(sf.toString(), 
					sp, new BeanListProcessor(BankBillToStatementVO.class));
		
		if(billList == null || billList.size() == 0)
			return;
		//删除中间表信息
		singleObjectBO.deleteVOArray(billList.toArray(new BankBillToStatementVO[0]));
		
		//更新对账单字段
		List<String> bankpks = new ArrayList<String>();
		for(BankBillToStatementVO vo : billList){
			bankpks.add(vo.getPk_bankstatement());
		}
		
		sf.setLength(0);
		sf.append(" select * from ynt_bankstatement y where pk_corp = ? and nvl(dr,0)=0 and ");
		sf.append(SqlUtil.buildSqlForIn("pk_bankstatement", bankpks.toArray(new String[0])));
		sp.clearParams();
		sp.addParam(pk_corp);
		List<BankStatementVO2> bankList = (List<BankStatementVO2>) singleObjectBO.executeQuery(sf.toString(),
				sp, new BeanListProcessor(BankStatementVO2.class));
		
		if(bankList == null || bankList.size() == 0)
			return;
		
		for(BankStatementVO2 vo : bankList){
			if(vo.getBillstatus() == BankStatementVO2.STATUS_1){
				vo.setDr(1);
			}else if(vo.getBillstatus() == BankStatementVO2.STATUS_0){
				
			}else if(vo.getBillstatus() == BankStatementVO2.STATUS_2){
				vo.setSourcebillid(null);
				vo.setPk_image_group(null);
				vo.setImgpath(null);
				vo.setVdef13(null);
				vo.setBillstatus(BankStatementVO2.STATUS_0);
			}
			
		}
		
		singleObjectBO.updateAry(bankList.toArray(new BankStatementVO2[0]), 
				new String[]{ "dr", "sourcebillid", "pk_image_group", "billstatus","imgpath","vdef13" });
	}

	@Override
	public void saveVOs(String pk_corp, List<BankStatementVO2> list) throws DZFWarpException {
		
	}

	@Override
	public String saveBusiPeriod(BankStatementVO2[] vos, String pk_corp, String period) throws DZFWarpException {
		// 期间关账检查
		boolean isgz = qmgzService.isGz(pk_corp, period);
		if (isgz) {
			throw new BusinessException("所选入账期间" + period + "已关账，请检查。");
		}
		
		StringBuffer msg = new StringBuffer();
		
		BigDecimal repeatCodeNum = checkIsQjsyjz(pk_corp, period);
		if(repeatCodeNum != null && repeatCodeNum.intValue() > 0) {
			msg.append("<p>所选入账期间" + period + "已损益结转。</p>");
		}
		

		//BankStatementVO2 newVO = null;
		int upCount = 0;
		int npCount = 0;

		StringBuffer part = new StringBuffer();
		List<BankStatementVO2> list = new ArrayList<BankStatementVO2>();
		StringBuffer checkKeys=new StringBuffer();
		for (BankStatementVO2 vo : vos) {
			vo.setVersion(new DZFDouble(1.0));
			if (!StringUtil.isEmptyWithTrim(vo.getPk_tzpz_h())) {
				part.append("<p>交易日期:" + vo.getTradingdate() + "的单据已生成凭证。</p>");
				npCount++;
				continue;
			}
			if(!StringUtils.isEmpty(vo.getPk_model_h())){
				Map<String, BillCategoryVO> map = queryNewPkcategory(vo.getPk_model_h(), period, pk_corp);
				vo.setInperiod(period);
				BillCategoryVO billvo = map.get(vo.getPk_model_h());
				if (billvo == null)	//新期间未找到对应类别，重新分类
				{
					vo.setPk_model_h(null);
					vo.setPk_category_keyword(null);
					vo.setBusitypetempname(null);
					List<BankStatementVO2> bList = new ArrayList<BankStatementVO2>();
					bList.add(vo);
					bList = changeToBank(bList, pk_corp);
					vo = bList.get(0);
				}
				else 
				{
					vo.setPk_model_h(billvo.getPk_category());
				}
			}else{
				vo.setInperiod(period);
			}
			upCount++;
			list.add(vo);
			checkKeys.append(vo.getPrimaryKey()+",");
		}
		if (list != null && list.size() > 0) {
			List<BankStatementVO2> tmpList=queryByIDs(checkKeys.toString().substring(0, checkKeys.length()-1));
			for (BankStatementVO2 vo : tmpList) {
				if(!StringUtils.isEmpty(vo.getImgpath())){
					throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
				}
			}
			for (BankStatementVO2 vo : list) {
				//设置结算方式，结算科目，入账科目
				if(!StringUtils.isEmpty(vo.getPk_model_h())){
					CategorysetVO setVO = queryCategorySetVO(vo.getPk_model_h());
					vo.setSettlement(setVO.getSettlement()==null?0:setVO.getSettlement());
					vo.setPk_subject(setVO.getPk_accsubj());
					vo.setPk_settlementaccsubj(setVO.getPk_settlementaccsubj());
				}
			}
			singleObjectBO.updateAry(list.toArray(new BankStatementVO2[0]),
					new String[] { "inperiod", "pk_model_h", "pk_category_keyword", "busitypetempname", "version",
							"settlement", "pk_subject", "pk_settlementaccsubj" });
		}

		msg.append("<p>入账期间更新成功 ").append(upCount).append(" 条")
				.append(npCount > 0 ? ",未更新 " + npCount + " 条。未更新详细原因如下:</p>" + part.toString() : "</p>");

		return msg.toString();
	}

	@Override
	public StringBuffer buildQmjzMsg(List<String> periodList, String pk_corp) throws DZFWarpException {
		if(periodList == null || periodList.size() == 0)
			return null;
		
		String part = SqlUtil.buildSqlForIn("period", periodList.toArray(new String[0]));
		
		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_qmcl where nvl(dr,0)=0 and pk_corp = ? and ");
		sf.append(part);
		sf.append(" order by period ");
		sp.addParam(pk_corp);
		
		List<QmclVO> list = (List<QmclVO>) singleObjectBO.executeQuery(sf.toString(),
				sp, new BeanListProcessor(QmclVO.class));
		
		sf.setLength(0);
		if(list != null && list.size() > 0){
			String period;
			DZFBoolean value;
			for(QmclVO vo : list){
				period = vo.getPeriod();
				
				value = vo.getIsqjsyjz();
				if(value != null && value.booleanValue()){
					sf.append("<p><font color = 'red'>").append(period).append("期间损益已结转，生成凭证后，请重新结转期间损益!</font></p>");
				}
				
				value = vo.getIscbjz();
				if(value != null && value.booleanValue()){
					sf.append("<p><font color = 'red'>").append(period).append("成本已结转，生成凭证后，请重新结转成本!</font></p>");
				}
				
				value = vo.getIshdsytz();
				if(value != null && value.booleanValue()){
					sf.append("<p><font color = 'red'>").append(period).append("汇兑调整已完成，生成凭证后，请重新进行汇兑调整!</font></p>");
				}
			}
		}
		
		return sf;
	}
	private List<OcrInvoiceVO> changeToOcr(List<BankStatementVO2> bList,String pk_corp){
		List<OcrInvoiceVO> list = new ArrayList<OcrInvoiceVO>();
		CorpVO corpVO = corpService.queryByPk(pk_corp);

		HashMap<String, String> hmBankAccount = new HashMap<String, String>();
		HashMap<String, String> hmBankBillToStatement = new HashMap<String, String>();


		ArrayList<String> alpk_bankaccount = new ArrayList<String>();
		ArrayList<String> alpk_bankstatement = new ArrayList<String>();
		for (BankStatementVO2 bvo : bList)
		{
			if (StringUtil.isEmptyWithTrim(bvo.getPk_bankaccount()) == false)
			{
				if (alpk_bankaccount.contains(bvo.getPk_bankaccount()) == false)
				{
					alpk_bankaccount.add(bvo.getPk_bankaccount());
				}
			}
			else if (StringUtil.isEmptyWithTrim(bvo.getPk_bankstatement()) == false)
			{
				if (alpk_bankstatement.contains(bvo.getPk_bankstatement()) == false)
				{
					alpk_bankstatement.add(bvo.getPk_bankstatement());
				}
			}
		}
		if (alpk_bankaccount.size() > 0)
		{
			BankAccountVO[] vos = (BankAccountVO[])singleObjectBO.queryByCondition(BankAccountVO.class, SqlUtil.buildSqlForIn("pk_bankaccount", alpk_bankaccount.toArray(new String[0])), null);
			if (vos != null)
			{
				for (BankAccountVO vo : vos)
				{
					hmBankAccount.put(vo.getPk_bankaccount(), vo.getBankaccount());
				}
			}
		}
		if (alpk_bankstatement.size() > 0)
		{
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);

			BankBillToStatementVO[] vos = (BankBillToStatementVO[])singleObjectBO.queryByCondition(BankBillToStatementVO.class, "pk_corp= ?  and nvl(dr, 0) = 0 and " + SqlUtil.buildSqlForIn("pk_bankstatement", alpk_bankstatement.toArray(new String[0])), sp);
			if (vos != null)
			{
				for (BankBillToStatementVO vo : vos)
				{
					hmBankBillToStatement.put(vo.getPk_bankstatement(), vo.getMyaccountcode());
				}
			}
		}
		for (BankStatementVO2 bvo : bList) {
			OcrInvoiceVO ovo = new OcrInvoiceVO();
			String randomUUID = UUID.randomUUID().toString();
			//回写分类时使用（相当于主键）
			bvo.setTempvalue(randomUUID);
			ovo.setPk_invoice(randomUUID);
			ovo.setDatasource(ZncsConst.SJLY_2);//数据来源
			ovo.setPeriod(bvo.getInperiod());//入账期间
			ovo.setPk_corp(pk_corp);//入账公司
			ovo.setPk_billcategory(bvo.getPk_model_h());
			ovo.setPk_category_keyword(bvo.getPk_category_keyword());
			//判断是转出还是转入  判断对方公司是销方还是收方
//			String mycode = queryAccbankNo(pk_corp, bvo.getPk_bankaccount(),bvo.getPk_bankstatement());
			String mycode = (StringUtil.isEmpty(bvo.getPk_bankaccount()) == false ? hmBankAccount.get(bvo.getPk_bankaccount()) : (StringUtil.isEmpty(bvo.getPk_bankstatement()) == false ? hmBankBillToStatement.get(bvo.getPk_bankstatement()) : null));
			if((bvo.getSyje()==null||bvo.getSyje().doubleValue()==0)&&(bvo.getZcje()!=null||bvo.getZcje().doubleValue()!=0)){//收入为空,支出不空，对方公司为收方
				ovo.setVpurchname(corpVO.getUnitname());//付方
				ovo.setVsalename(bvo.getOthaccountname());//收方
				ovo.setNtotaltax(bvo.getZcje().toString());//价税合计
				ovo.setVsaletaxno(bvo.getOthaccountcode());//销方纳税号
				ovo.setVpurchtaxno(mycode);//购方纳税号
			}else if((bvo.getSyje()!=null||bvo.getSyje().doubleValue()!=0)&&(bvo.getZcje()==null||bvo.getZcje().doubleValue()==0)){//收入不空,支出为空，对方公司为销方
				ovo.setVpurchname(bvo.getOthaccountname());//付方
				ovo.setVsalename(corpVO.getUnitname());//收方
				ovo.setNtotaltax(bvo.getSyje().toString());//价税合计
				ovo.setVsaletaxno(mycode);//销方纳税号
				ovo.setVpurchtaxno(bvo.getOthaccountcode());//购方纳税号
			}
			ovo.setDinvoicedate(bvo.getTradingdate().toString());//开票日期、交易日期
			ovo.setVsalephoneaddr(bvo.getZy());//摘要
			ovo.setIstate(ZncsConst.SBZT_1);//发票类型
			ovo.setPk_image_group(bvo.getPk_image_group());;//图片组号
			ovo.setUpdateflag(DZFBoolean.FALSE);//是否存库标志
			ovo.setSettlement(bvo.getSettlement());//结算方式
			ovo.setPk_subject(bvo.getPk_subject());//入账科目
			ovo.setPk_settlementaccsubj(bvo.getPk_settlementaccsubj());//结算方式
//			ovo.setPk_taxaccsubj(null);//税行科目
			ovo.setChildren(changeToOcrDetail(bvo));
			list.add(ovo);
		}

		return list;
	}
	private OcrInvoiceDetailVO[] changeToOcrDetail(BankStatementVO2 bvo){
		List<OcrInvoiceDetailVO> list = new ArrayList<OcrInvoiceDetailVO>();
		OcrInvoiceDetailVO odvo = new OcrInvoiceDetailVO();
		odvo.setRowno(1);//序号
		odvo.setPk_billcategory(bvo.getPk_model_h());
		odvo.setPk_category_keyword(bvo.getPk_category_keyword());
		list.add(odvo);
		return list.toArray(new OcrInvoiceDetailVO[0]);
	}
	private List<BankStatementVO2> changeToBank(List<BankStatementVO2> bList,String pk_corp){

		CorpVO corpVO = corpService.queryByPk(pk_corp);
		List<OcrInvoiceVO> olist = changeToOcr(bList, pk_corp);
		if (olist != null&& olist.size() > 0) {	
			Map<String, List<OcrInvoiceVO>> map = DZfcommonTools.hashlizeObject(olist,
					new String[] {"pk_corp", "period"});//期间分组
			for (String key : map.keySet()) {
				ArrayList<String> pk_categoryList = new ArrayList<String>();
				boolean lock = false;
				try {
					if (StringUtils.isEmpty(key)) {
						continue;
					}
					lock = redissonDistributedLock.tryGetDistributedFairLock("zncsCategory_"+key.replace(",",""));
					if (lock) {
						schedulCategoryService.newSaveCorpCategory(map.get(key), corpVO.getPk_corp(), map.get(key).get(0).getPeriod(), corpVO);

						List<OcrInvoiceVO> ocrList = schedulCategoryService.updateInvCategory(map.get(key), pk_corp, map.get(key).get(0).getPeriod(),
								corpVO);// 票据分类
						//查询类别全名称
						for (OcrInvoiceVO ocrInvoiceVO : ocrList) {
							pk_categoryList.add(ocrInvoiceVO.getPk_billcategory());
						}
						Map<String, String> fullNameMap = zncsVoucher.queryCategoryFullName(pk_categoryList, map.get(key).get(0).getPeriod(), pk_corp);
						for (OcrInvoiceVO ocrInvoiceVO : ocrList) {
							for (BankStatementVO2 bvo : bList) {
								if (ocrInvoiceVO.getPk_invoice().equals(bvo.getTempvalue())) {
									bvo.setPk_category_keyword(ocrInvoiceVO.getPk_category_keyword());// 关键字主键
									bvo.setPk_model_h(ocrInvoiceVO.getPk_billcategory());// 类别主键
									bvo.setBusitypetempname(fullNameMap.get(ocrInvoiceVO.getPk_billcategory()));// 类别名称
								}
							}
						}
					} else {
						continue;
					}
				} catch (Exception e) {
					log.error("分类任务异常", e);
				} finally {
					if (lock) {
						redissonDistributedLock.releaseDistributedFairLock("zncsCategory_"+key.replace(",",""));
					}
				}
			}
				
			
		}
		
		return bList;
	}

	@Override
	public List<BillCategoryVO> queryBankCategoryRef(String pk_corp, String period) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuffer sb = new StringBuffer();
		sb.append("select distinct bi.pk_category as pk_category,ca.zdyzy,ca.pk_accsubj,bi.categoryname as categoryname,bi.showorder "
				+ " from ynt_billcategory bi left join ynt_categoryset ca on bi.pk_category=ca.pk_category "
				+ " where  nvl(bi.dr,0)=0 and nvl(ca.dr,0)=0 and bi.isleaf='Y' and "
				+ " nvl(bi.isaccount,'N')='N' and bi.categorycode like '12%' and bi.categorycode !='12' "
				+ " and bi.pk_corp = ? and bi.period = ? order by bi.showorder ");
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<BillCategoryVO> listVo = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), 
				sp, new BeanListProcessor(BillCategoryVO.class));
		return listVo;
	}

	@Override
	public List<BankStatementVO2> updateVO(String id, String pk_model_h,String busitypetempname,String pk_corp,String rzkm,String pk_basecategory,String zdyzy,String jskm) throws DZFWarpException {

		String[] ids = id.split(",");
		
		List<BankStatementVO2> list=queryByIDs(id);
		if(list!=null&&list.size()>0){
			List<BankStatementVO2> studylist=new ArrayList<BankStatementVO2>();
			for (int i = 0; i < list.size(); i++) {
//				checkvoPzMsg(ids[i]);
				if(!StringUtils.isEmpty(list.get(i).getImgpath())){
					throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
				}
				// 自学习
				// 查询修改前的分类主键
//				List<BankStatementVO2> oldVOList = queryByID(ids[i]);
				if(!StringUtil.isEmpty(list.get(i).getPk_tzpz_h())){
					throw new BusinessException("已经生成凭证，不能操作。");
				}
				if (!StringUtils.isEmpty(list.get(i).getPk_category_keyword()) && !StringUtils.isEmpty(pk_model_h)
						&& !pk_model_h.equals(list.get(i).getPk_model_h())) {// 有没有修改分类
					studylist.add(list.get(i));
				}
			}
			if(studylist.size()>0){
				List<OcrInvoiceVO> OcrInvoiceVOList = changeToOcr(studylist, pk_corp);
				for (OcrInvoiceVO ocrInvoiceVO : OcrInvoiceVOList) {
					ocrInvoiceVO.setPk_invoice(null);
				}
				iBillcategory.saveNewCategroy(OcrInvoiceVOList.toArray(new OcrInvoiceVO[0]), pk_model_h, pk_corp,
						OcrInvoiceVOList.get(0).getPeriod());
			}
		}
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append(" update ynt_bankstatement set version=1.0, pk_model_h= ?,busitypetempname=?,pk_subject=?,pk_settlementaccsubj=? where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_bankstatement", ids));
		sp.addParam(pk_model_h);
		sp.addParam(busitypetempname);
		sp.addParam(rzkm);
		sp.addParam(jskm);
		singleObjectBO.executeUpdate(sb.toString(), sp);
		
		gl_vatincinvact2.updateCategoryset(new DZFBoolean(true),pk_model_h,null,pk_basecategory,pk_corp,rzkm,jskm,null,zdyzy);
		
		list=queryByIDs(id);
		return list;
	}
	
	@Override
	public List<BankStatementVO2> queryByID(String pk_bankstatement) throws DZFWarpException {

		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append(" select * from ynt_bankstatement where nvl(dr,0)=0 and pk_bankstatement = ?");
		sp.addParam(pk_bankstatement);
		List<BankStatementVO2> list = (List<BankStatementVO2>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(BankStatementVO2.class));
		return list!=null&&list.size()>0?list:null;
		
	}
	public List<BankStatementVO2> queryByIDs(String id) throws DZFWarpException {
		String[] ids = id.split(",");
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append(" select * from ynt_bankstatement where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_bankstatement", ids));
		
		List<BankStatementVO2> list = (List<BankStatementVO2>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(BankStatementVO2.class));
		return list!=null&&list.size()>0?list:null;
		
	}

	@Override
	public Map<String, DcModelHVO> queryDcModelVO(String pk_corp){
		Map<String, DcModelHVO> map = new HashMap<String, DcModelHVO>();
		
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(IDefaultValue.DefaultGroup);
		List<DcModelHVO> mhvos = (List<DcModelHVO>) singleObjectBO.executeQuery(" ( pk_corp = ? or pk_corp = ? ) and nvl(dr,0)=0 ", params, 
				new Class[]{DcModelHVO.class, DcModelBVO.class});
		
		String key = null;
		DcModelBVO[] bvos = null;
		for(DcModelHVO hvo : mhvos){
			key = hvo.getPk_model_h();
			
			//排序
			bvos = hvo.getChildren();
			if(bvos == null || bvos.length == 0){
				continue;
			}
			Arrays.sort(bvos, new Comparator<DcModelBVO>() {
				@Override
				public int compare(DcModelBVO o1, DcModelBVO o2) {
					return o1.getPrimaryKey().compareTo(o2.getPrimaryKey());
				}
			});
			
			map.put(key, hvo);
		}
		
		return map;
	}
	
	@Override
	public List<DcModelHVO> queryIsDcModel(String pk_dcmodel) throws DZFWarpException {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append("select * from ynt_dcmodel_h where nvl(dr,0)=0 and pk_model_h = ?");
		sp.addParam(pk_dcmodel);
		List<DcModelHVO> list = (List<DcModelHVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(DcModelHVO.class));
		return list;
	}
	@Override
	public List<DcModelHVO> queryIsDcModels(String pk_dcmodels) throws DZFWarpException {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append("select * from ynt_dcmodel_h where nvl(dr,0)=0 and  "+SqlUtil.buildSqlForIn("pk_model_h", pk_dcmodels.split(",")));
//		sp.addParam(pk_dcmodels);
		List<DcModelHVO> list = (List<DcModelHVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(DcModelHVO.class));
		return list;
	}
	@Override
	public Map<String, BillCategoryVO> queryNewPkcategory(String pk_category, String period, String pk_corp)
			throws DZFWarpException {
		StringBuffer psb = new StringBuffer();
		SQLParameter psp = new SQLParameter();
		psb.append(" select * from ynt_billcategory where nvl(dr,0)=0 and pk_category=?");
		psp.addParam(pk_category);
		List<BillCategoryVO> oldlist = (List<BillCategoryVO>) singleObjectBO.executeQuery(psb.toString(), psp,
				new BeanListProcessor(BillCategoryVO.class));
		Map<String,BillCategoryVO> map = new HashMap<String,BillCategoryVO>();
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append(" select * from ynt_billcategory where nvl(dr,0)=0 and nvl(isaccount,'N')='N' and period=? and pk_corp=? and pk_basecategory=?");
		sp.addParam(period);
		sp.addParam(pk_corp);
		sp.addParam(oldlist.get(0).getPk_basecategory());
		List<BillCategoryVO> list = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(BillCategoryVO.class));
		if(list!=null&&list.size()>0){
			map.put(pk_category, list.get(0));
		}
		return map;
	}
//	private String queryAccbankNo(String pk_corp, String pk_bankaccount,String pk_bankstatement)throws DZFWarpException{
//
//		if(!StringUtils.isEmpty(pk_bankaccount)){
//			BankAccountVO bankaccvo = gl_yhzhserv.queryById(pk_bankaccount);
//			return bankaccvo.getBankaccount();
//		}else{
//			StringBuffer sb = new StringBuffer();
//			SQLParameter sp = new SQLParameter();
//			sb.append(" select * from  ynt_bankbilltostatement where pk_corp= ? and nvl(dr,0) = 0 and  pk_bankstatement =?");
//			sp.addParam(pk_corp);
//			sp.addParam(pk_bankstatement);
//			List<BankBillToStatementVO> list = (List<BankBillToStatementVO>) singleObjectBO.executeQuery(sb.toString(), sp,
//					new BeanListProcessor(BankBillToStatementVO.class));
//			return list!=null&&list.size()>0?list.get(0).getMyaccountcode():null;
//		}
//	}

	@Override
	public CategorysetVO queryCategorySetVO(String pk_model_h) throws DZFWarpException {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append(" select * from  ynt_categoryset where nvl(dr,0) = 0 and  pk_category =?");
		sp.addParam(pk_model_h);
		List<CategorysetVO> list = (List<CategorysetVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(CategorysetVO.class));
		
		return list!=null&&list.size()>0?list.get(0):new CategorysetVO();
	}
	@Override
	public void checkvoPzMsg(String pk_bankstatement) throws DZFWarpException {
		List<BankStatementVO2> list = queryByID(pk_bankstatement);
		if(list==null||list.size()<=0||!StringUtils.isEmpty(list.get(0).getImgpath())){
			throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
		}
		
	}

	@Override
	public void checkvoPzMsgs(String pk_bankstatements) throws DZFWarpException {
		List<BankStatementVO2> list = queryByIDs(pk_bankstatements);
		for (BankStatementVO2 vo : list) {
			if(!StringUtils.isEmpty(vo.getImgpath())){
				throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
			}
		}
	}
	@Override
	public String queryBillCategoryId(String name, String pk_corp, String period) throws DZFWarpException {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		Map<String,String> map = new HashMap<String,String>();
		sb.append(" select bi.* from ynt_basecategory ba inner join ynt_billcategory bi on ba.pk_basecategory=bi.pk_basecategory "
				+ "where nvl(ba.dr,0)=0 and nvl(bi.dr,0)=0 and bi.ISACCOUNT='N' and ba.catalogname=? and bi.pk_corp=? and bi.period=? ");
		sp.addParam(name);
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<BillCategoryVO> billList = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(BillCategoryVO.class));
		
		return billList!=null&&billList.size()>0?billList.get(0).getPk_category():null;
	}

	@Override
	public BankStatement2ResponseVO ercptApplyAndQrywlhdetail(IcbcErcptApplyAndQrywlhdetailQo vo,String pk_bankaccount) throws DZFWarpException {
		BankStatement2ResponseVO responseVO = null;
		try{

			Result<PageInfo<IcbcErcptApplyAndQrywlhdetailVo>> result =  iIcbcWlhService.ercptApplyAndQrywlhdetail(vo);
			//200表示一键获取成功
			if(200==result.getCode()){
					PageInfo pageInfo = result.getData();
					if(pageInfo.getTotal()>0){
						Map<String, BankStatementVO2[]> sendData = new HashMap<String, BankStatementVO2[]>();
						List<BankStatementVO2> bankvolist = new ArrayList<BankStatementVO2>();
						List<IcbcErcptApplyAndQrywlhdetailVo> list =pageInfo.getList();
						for (IcbcErcptApplyAndQrywlhdetailVo resultvo:list) {
							//查询是否重复
							List<BankStatementVO2> queryList = queryVoByVdef11(resultvo.getTransserialno());
							if(queryList ==null || queryList.size()==0){
								BankStatementVO2 bvo = new BankStatementVO2();
								bvo.setCoperatorid(SystemUtil.getLoginUserId());//操作人
								bvo.setDoperatedate(new DZFDate(new Date()));//操作时间
								bvo.setPk_corp(SystemUtil.getLoginCorpId());//公司主键
								bvo.setPk_bankaccount(pk_bankaccount);//银行账户主键
								bvo.setVdef11(resultvo.getTransserialno());//交易流水号
								bvo.setTradingdate(new DZFDate(resultvo.getTranstime().substring(0,10)));//交易时间
								bvo.setOthaccountname(resultvo.getRecipaccname());//对方账户名称
								bvo.setOthaccountcode(resultvo.getRecipaccno());//对方账户
								bvo.setYe(new DZFDouble(resultvo.getCurrentbalance()));//余额
								bvo.setSourcetem(BankStatementVO2.SOURCE_59);//数据来源
								bvo.setSourcetype(BankStatementVO2.SOURCE_101);
								bvo.setPeriod(resultvo.getTranstime().substring(0,7));//入账期间
								bvo.setVdef12(resultvo.getWlhstatus().getDesc());//电子回单获取状态
								bvo.setInperiod(resultvo.getTranstime().substring(0,7));//入账期间
								bvo.setVersion(new DZFDouble(1.0));//版本
								if(resultvo.getDrcrf().equals("1")){//1-借(出);2-贷(入
									bvo.setZcje(new DZFDouble(resultvo.getAmount()));//支出金额
								}else{
									bvo.setSyje(new DZFDouble(resultvo.getAmount()));//收入金额
								}
								//复制图片并将pdf转成图片格式
								if(!StringUtils.isEmpty(resultvo.getWlhaddress())) {
									ImageLibraryVO il = copyAndTrans(resultvo.getWlhaddress());
                                    bvo.setImgpath("/zncs/gl_imgview/search?id="+il.getPk_image_library()+"&name="+il.getImgname()+"&pk_corp="+il.getPk_corp());                                }

								bankvolist.add(bvo);
							}
						}
						sendData.put("adddocvos", bankvolist.toArray(new BankStatementVO2[0]));
						responseVO = updateVOArr(SystemUtil.getLoginCorpId(), sendData, new DZFBoolean("Y"));
					}else{
						throw new BusinessException("一键获取数据为空！");
					}
				}else{
					throw new BusinessException(result.getMsg());
				}
		}catch(Exception e){
			throw new BusinessException("一键获取服务调取失败！");
		}
		return responseVO;
	}
    //根据交易流水号查询票据
	public List<BankStatementVO2> queryVoByVdef11(String transserialno) throws DZFWarpException{
		List<BankStatementVO2> list = null;
		if(!StringUtil.isEmpty(transserialno)){
			StringBuffer sb = new StringBuffer();
			SQLParameter sp = new SQLParameter();
			sb.append("select * from ynt_bankstatement where nvl(dr,0)=0 and vdef11 = ? ");
			sp.addParam(transserialno);
			list = (List<BankStatementVO2>) singleObjectBO.executeQuery(sb.toString(), sp,
					new BeanListProcessor(BankStatementVO2.class));
		}
		return list;
	}
    // 获取当前年月日
    private static String getCurDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(Calendar.getInstance().getTime());
    }
	private ImageLibraryVO copyAndTrans(String http){
		String imgDir = File.separator + "ImageUpload" + File.separator ;
		String imgPath = SystemUtil.getLoginCorpVo().getUnitcode() + File.separator + getCurDate();
		String imgFilePath = UUID.randomUUID().toString() + ".jpg";
		String PdfFileName = UUID.randomUUID().toString() + ".pdf";
		ImageLibraryVO il = new ImageLibraryVO();
		try {

			File dir = new File(imgDir+imgPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File imgFile = new File(imgDir+imgPath + File.separator + imgFilePath);
			URL url = new URL(http);
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			ImageCopyUtil.transPdfToJpg(is, new File(imgDir+imgPath + File.separator + PdfFileName), imgFile);

			il.setImgpath(imgPath+File.separator+imgFilePath);
			il.setPdfpath(imgPath+File.separator+PdfFileName);
			il.setImgname(imgFilePath);
			il.setPk_corp(SystemUtil.getLoginCorpId());
			il.setPk_uploadcorp(SystemUtil.getLoginCorpId());
			il.setCoperatorid(SystemUtil.getLoginUserId());
			il.setDoperatedate(new DZFDate());
//			il.setCvoucherdate(DateUtils.getPeriodEndDate(period));
//			il.setImgmd(imgMD);// 图片MD5码值
			singleObjectBO.saveObject(il.getPk_corp(), il);
		}catch(Exception e){
			throw new BusinessException("获取银行回单失败！");
		}
		return il;
	}

}