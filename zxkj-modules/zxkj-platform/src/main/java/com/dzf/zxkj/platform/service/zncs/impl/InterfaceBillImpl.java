package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.cloud.redis.lock.RedissonDistributedLock;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.BdTradeAccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.icset.MeasureVO;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.pjgl.BankBillToStatementVO;
import com.dzf.zxkj.platform.model.pjgl.BankStatementVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pjgl.VatGoosInventoryRelationVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.*;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.icset.IInventoryService;
import com.dzf.zxkj.platform.service.icset.IMeasureService;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.zncs.*;
import com.dzf.zxkj.platform.util.SecretCodeUtils;
import com.dzf.zxkj.platform.util.zncs.OcrUtil;
import com.dzf.zxkj.platform.util.zncs.ZncsConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Service
public class InterfaceBillImpl implements IInterfaceBill {
	@Autowired
	SingleObjectBO singleObjectBO;
	@Autowired
	IZncsVoucher ivoucher;
	@Autowired
	private YntBoPubUtil yntBoPubUtil;
	@Autowired
	private IMeasureService measervice;
	@Autowired
	private IAutoMatchName ocr_atuomatch;
	@Autowired
	private IInventoryService invservice;
	@Autowired
	ISchedulCategoryService cateservice;
	@Autowired
	IPrebillService iprebillservice;
	@Autowired
	IBillcategory ibillcategory;
	@Autowired
	ICpaccountCodeRuleService gl_accountcoderule;
	@Autowired
	private IInventoryService inventoryservice;
	@Autowired
	IOcrBillCreate billcreate;
	@Autowired
	ICpaccountService gl_cpacckmserv;
	@Autowired
	private IParameterSetService sys_parameteract;
	@Autowired
	private IAccountService accountService;
	@Autowired
	private ICorpService corpService;
	@Autowired
	private RedissonDistributedLock redissonDistributedLock;

	@Override
	public void updateInvalidBill(String billid[],String pk_corp) throws DZFWarpException {
		if(billid==null || billid.length==0){
			throw new BusinessException("请选择需要作废的票据!");
		}
		invadateAndRetrans(billid, null, PhotoState.state205,pk_corp);
	}

	@Override
	public void updateInvalidBatchBill(BillcategoryQueryVO paramVO) throws DZFWarpException {
		invadateAndRetrans(null, paramVO, PhotoState.state205,paramVO.getPk_corp());
	}

	private OcrInvoiceVO[] checkZckpAndKc(String billid[], String condition)throws DZFWarpException{
		String sql = "";//"nvl(dr,0)=0 and " + new SqlInUtil(billid).getInSql("invoice", "000001", "000001");//SqlUtil.buildSqlForIn("pk_invoice", billid) + "";
		if(StringUtil.isEmpty(condition)){
			sql = "nvl(dr,0)=0 and " + SqlUtil.buildSqlForIn("pk_invoice", billid) + "";// new SqlInUtil(billid).getInSql("invoice", "", "");
		}else{
			sql = condition;
		}
		OcrInvoiceVO vos[] = (OcrInvoiceVO[]) singleObjectBO.queryByCondition(OcrInvoiceVO.class, sql,
				new SQLParameter());
		if (vos == null || vos.length == 0)
			throw new BusinessException("没有查到对应的票据信息!");
		List<OcrInvoiceVO> list = Arrays.asList(vos);

		List<OcrInvoiceDetailVO> listdetail = iprebillservice.queryDetailByCondition(sql);

		Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(listdetail,
				new String[] { "pk_invoice" });

		for (OcrInvoiceVO invoicevo : list) {
			if(detailMap.get(invoicevo.getPk_invoice())!=null){
				invoicevo.setChildren(detailMap.get(invoicevo.getPk_invoice()).toArray(new OcrInvoiceDetailVO[0]));
			}
		}
		if (ibillcategory.checkHaveIctrade(vos).equals(DZFBoolean.TRUE)) {
			throw new BusinessException("所选票据已生成出入库单据，请检查！");
		}
		if (ibillcategory.checkHaveZckp(vos).equals(DZFBoolean.TRUE)) {
			throw new BusinessException("所选票据已生成资产卡片，请检查！");
		}
		return vos;
		
	}
	
	private void invadateAndRetrans(String billid[], BillcategoryQueryVO paramVO, int billstate,String pk_corp) {
		if (billid != null && billid.length > 0) {
			checkZckpAndKc(billid,null);
			String conditon = " pk_corp=? and  nvl(dr,0)=0 and pk_image_group in ( select pk_image_group from ynt_interface_invoice where "
					+ SqlUtil.buildSqlForIn("pk_invoice", billid) + ")";
			String sql = "update ynt_image_group set istate =  " + billstate + "   where " + conditon;
			SQLParameter param = new SQLParameter();// PhotoState.state205
			param.addParam(pk_corp);
			// param.addParam(PhotoState.state205);
			invalidOtherBill(pk_corp, new VATInComInvoiceVO2().getTableName(), conditon, param);
			invalidOtherBill(pk_corp, new VATSaleInvoiceVO2().getTableName(), conditon, param);
			invalidOtherBill(pk_corp, new BankStatementVO2().getTableName(), conditon, param);
			singleObjectBO.executeUpdate(sql, param);
			

		} else if (paramVO != null) {
			StringBuffer buff = new StringBuffer();
			StringBuffer buffm = new StringBuffer();
			SQLParameter param = new SQLParameter();
			StringBuffer checkSql = new StringBuffer();
			String pk_parant = "";
			String bankcode = "";
			if (StringUtil.isEmpty(paramVO.getPk_category()))
				throw new BusinessException("分组不能为空");

			if (paramVO.getPk_category().startsWith("bank_")) {
				bankcode = paramVO.getPk_category().substring(5, paramVO.getPk_category().length());
				pk_parant = paramVO.getPk_parentcategory();
			} else if (!StringUtil.isEmpty(paramVO.getPk_parentcategory())
					&& paramVO.getPk_parentcategory().startsWith("bank_")) {
				bankcode = paramVO.getPk_parentcategory().substring(5, paramVO.getPk_parentcategory().length());
				pk_parant = paramVO.getPk_category();
			} else {
				pk_parant = paramVO.getPk_category();
			}

			// param.addParam(PhotoState.state205);
			param.addParam(paramVO.getPk_corp());
			param.addParam(paramVO.getPeriod());
			buff.append(" update ynt_image_group  set istate =").append(billstate).append("   where  istate in (0,1) and");
			buffm.append(
					"   nvl(dr,0)=0 and pk_corp =? and pk_image_group in ( select d.pk_image_group from  ynt_interface_invoice d left join ynt_image_group sd on d.pk_image_group = sd.pk_image_group ");
			buffm.append("  where nvl(d.dr, 0) = 0  and d.period = ? and nvl(sd.dr, 0) = 0  and sd.istate in (0,1) ");
			checkSql.append(" select d.pk_invoice from  ynt_interface_invoice d left join ynt_image_group sd on d.pk_image_group = sd.pk_image_group where nvl(d.dr, 0) = 0 and d.pk_corp=? and d.period = ? and nvl(sd.dr, 0) = 0  and sd.istate in (0,1) ");
			if (!StringUtil.isEmpty(bankcode)) {
				buffm.append(" and (d.vpurchtaxno = ? or d.vsaletaxno = ?) ");
				checkSql.append(" and (d.vpurchtaxno = ? or d.vsaletaxno = ?) ");
				param.addParam(bankcode);
				param.addParam(bankcode);
			}
			buffm.append(" and d.pk_billcategory in ");
			buffm.append(" (select pk_category ");
			buffm.append("  from ynt_billcategory ");
			buffm.append("   where dr = 0  and nvl(isaccount, 'N') = 'N' ");
			buffm.append("     start with pk_category = ? ");
			buffm.append("      connect by prior pk_category = pk_parentcategory) ) ");
			checkSql.append(" and d.pk_billcategory in (select pk_category from ynt_billcategory where nvl(dr,0) = 0  and nvl(isaccount, 'N') = 'N' start with pk_category = ? connect by prior pk_category = pk_parentcategory)  ");
			buff.append(buffm.toString());
			param.addParam(pk_parant);
			List<OcrInvoiceVO> invoiceList=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(checkSql.toString(), param, new BeanListProcessor(OcrInvoiceVO.class));
			if(invoiceList!=null&&invoiceList.size()>0){
				List<String> pkInvoiceList=new ArrayList<String>();
				for(int i=0;i<invoiceList.size();i++){
					pkInvoiceList.add(invoiceList.get(i).getPk_invoice());
				}
				checkZckpAndKc(pkInvoiceList.toArray(new String[0]),null);
			}
			invalidOtherBill(pk_corp, new VATInComInvoiceVO2().getTableName(), buffm.toString(), param);
			invalidOtherBill(pk_corp, new VATSaleInvoiceVO2().getTableName(), buffm.toString(), param);
			invalidOtherBill(pk_corp, new BankStatementVO2().getTableName(), buffm.toString(), param);
			singleObjectBO.executeUpdate(buff.toString(), param);
			

		}

	}

	@Override
	public void updateRetransBill(String billid[], BillcategoryQueryVO paramVO) {// 先删票据,删进销项银行对账单,更新图片状态为初始态
		if (billid != null && billid.length > 0) {
			invadateAndRetrans(billid, null, PhotoState.state0,paramVO.getPk_corp());
			StringBuffer buff = new StringBuffer();

			buff.append(
					"update YNT_IMAGE_OCRLIBRARY set iszd='N', istate = (case when batchcode is null  then 0 else   10 end)  ");
			buff.append(",doperatedate ='").append(new DZFDate()).append("'");
			buff.append(
					" where pk_image_ocrlibrary in ( select ocr_id from ynt_interface_invoice   where nvl(dr,0)=0 and "
							+ SqlUtil.buildSqlForIn("pk_invoice", billid))
					.append(" ) ");
			singleObjectBO.executeUpdate(buff.toString(), new SQLParameter());
			String sql = "update ynt_interface_invoice set  dr=1   where "
					+ SqlUtil.buildSqlForIn("pk_invoice", billid);

			singleObjectBO.executeUpdate(sql, new SQLParameter());
			sql = "update ynt_interface_invoice_detail set  dr=1   where "
					+ SqlUtil.buildSqlForIn("pk_invoice", billid);
			singleObjectBO.executeUpdate(sql, new SQLParameter());

		} else {
			invadateAndRetrans(null, paramVO, PhotoState.state0,paramVO.getPk_corp());

			StringBuffer buff = new StringBuffer();
			SQLParameter param = new SQLParameter();
			String pk_parant = "";
			String bankcode = "";
			if (paramVO.getPk_category().startsWith("bank_")) {
				bankcode = paramVO.getPk_category().substring(5, paramVO.getPk_category().length());
				pk_parant = paramVO.getPk_parentcategory();
			} else if (!StringUtil.isEmpty(paramVO.getPk_parentcategory())
					&& paramVO.getPk_parentcategory().startsWith("bank_")) {
				bankcode = paramVO.getPk_parentcategory().substring(5, paramVO.getPk_parentcategory().length());
				pk_parant = paramVO.getPk_category();
			} else {
				pk_parant = paramVO.getPk_category();
			}

			// 更新ocr表
			param.addParam(paramVO.getPeriod());
			// buff.append("update YNT_IMAGE_OCRLIBRARY set iszd='N', istate
			// =").append(StateEnum.SUCCESS_INTER_INSERT.getValue());
			buff.append(
					"update YNT_IMAGE_OCRLIBRARY set iszd='N', istate = (case when batchcode is null  then 0 else   10 end)  ");
			buff.append(",doperatedate ='").append(new DZFDate()).append("' ");

			buff.append("  where pk_image_ocrlibrary  in ");
			buff.append(
					"  ( select d.ocr_id from ynt_interface_invoice d left join ynt_image_group sd on d.pk_image_group = sd.pk_image_group ");
			buff.append("  where nvl(d.dr, 0) = 0  and d.period = ?  and nvl(sd.dr, 0) = 0  and sd.istate in (0,1) ");
			if (!StringUtil.isEmpty(bankcode)) {
				buff.append(" and (d.vpurchtaxno = ? or d.vsaletaxno = ?) ");
				param.addParam(bankcode);
				param.addParam(bankcode);
			}
			buff.append(" and d.pk_billcategory in ");
			buff.append(" (select pk_category ");
			buff.append("  from ynt_billcategory ");
			buff.append("   where dr = 0  and nvl(isaccount, 'N') = 'N' ");
			buff.append("     start with pk_category = ? ");
			buff.append("      connect by prior pk_category = pk_parentcategory)  )");
			param.addParam(pk_parant);
			singleObjectBO.executeUpdate(buff.toString(), param);

			buff = new StringBuffer();
			param = new SQLParameter();
			// param.addParam(paramVO.getPk_corp());
			param.addParam(paramVO.getPeriod());
			buff.append(" update  ynt_interface_invoice set dr =1 where pk_invoice in (");
			buff.append(" select d.pk_invoice from ynt_interface_invoice d  left join ynt_image_group sd on d.pk_image_group = sd.pk_image_group   ");
			buff.append("  where nvl(d.dr, 0) = 0  and d.period = ?  and nvl(sd.dr, 0) = 0  and sd.istate in (0,1) ");
			if (!StringUtil.isEmpty(bankcode)) {
				buff.append(" and (d.vpurchtaxno = ? or d.vsaletaxno = ?) ");
				param.addParam(bankcode);
				param.addParam(bankcode);
			}
			buff.append(" and d.pk_billcategory in ");
			buff.append(" (select pk_category ");
			buff.append("  from ynt_billcategory ");
			buff.append("   where dr = 0  and nvl(isaccount, 'N') = 'N' ");
			buff.append("     start with pk_category = ? ");
			buff.append("      connect by prior pk_category = pk_parentcategory)  )");
			param.addParam(pk_parant);
			singleObjectBO.executeUpdate(buff.toString(), param);
			buff = new StringBuffer();
			param = new SQLParameter();
			// param.addParam(paramVO.getPk_corp());
			param.addParam(paramVO.getPeriod());
			buff.append(" update ynt_interface_invoice_detail   set dr =1 where pk_invoice  in ");
			buff.append("  ( select d.pk_invoice from ynt_interface_invoice d left join ynt_image_group sd on d.pk_image_group = sd.pk_image_group  ");
			buff.append("  where nvl(d.dr, 0) = 0  and d.period = ?  and nvl(sd.dr, 0) = 0  and sd.istate in (0,1)  ");
			if (!StringUtil.isEmpty(bankcode)) {
				buff.append(" and (d.vpurchtaxno = ? or d.vsaletaxno = ?) ");
				param.addParam(bankcode);
				param.addParam(bankcode);
			}
			buff.append(" and d.pk_billcategory in ");
			buff.append(" (select pk_category ");
			buff.append("  from ynt_billcategory ");
			buff.append("   where dr = 0  and nvl(isaccount, 'N') = 'N' ");
			buff.append("     start with pk_category = ? ");
			buff.append("      connect by prior pk_category = pk_parentcategory)  )");
			param.addParam(pk_parant);
			singleObjectBO.executeUpdate(buff.toString(), param);

		}

	}

	private void invalidOtherBill(String pk_corp, String table, String condition, SQLParameter param) {
		// )
		// 银行对账单 billstatus 0 1 2 其他的
		// vo.setSourcetype(IBillManageConstants.OCR);
		// condition : pk_image_group in ( )条件用于确定图片
		// 如果来源是ocr的直接删掉,,,如果不是ocr的清除图片
		StringBuffer buff = new StringBuffer();

		if (table.equals("ynt_bankstatement")) {// 清除银行对账单子表数据
			buff = new StringBuffer();
			buff.append(" update ynt_bankbilltostatement set dr=1  where pk_corp = '").append(pk_corp).append("' and pk_bankstatement in (");
			buff.append("  select pk_bankstatement from   ynt_bankstatement  where pk_corp='").append(pk_corp).append("' and ");
			buff.append(condition);
			buff.append(")");
			singleObjectBO.executeUpdate(buff.toString(), param);
		}
		
		buff = new StringBuffer();
		buff.append(" update ").append(table).append(" set pk_image_group=null , imgpath=null,vdef13=null  ");
		if (table.equals("ynt_bankstatement")) {
			buff.append(", sourcebillid=null ,billstatus=0 ");
		} else {
			buff.append(",pk_image_library =null ");
		}
		buff.append("  where pk_corp='").append(pk_corp).append("' and ");
		if (table.equals("ynt_bankstatement")) {
			buff.append(" billstatus in (0,2)  ");// 导入和合并过的
		} else {
			buff.append(" sourcetype !=").append(IBillManageConstants.OCR);
		}
		buff.append(" and ").append(condition);

		singleObjectBO.executeUpdate(buff.toString(), param);

	
		// 如果来源是ocr的直接删掉
		buff = new StringBuffer();
		buff.append(" update ").append(table).append(" set dr=1  ");

		buff.append("  where    ");
		if (table.equals("ynt_bankstatement")) {
			buff.append(" billstatus =1  ");
		} else {
			buff.append(" sourcetype =").append(IBillManageConstants.OCR);
		}
		buff.append(" and ").append(condition);

		singleObjectBO.executeUpdate(buff.toString(), param);

	}

	@Override
	public OcrImageLibraryVO[] queryImages(String[] ids, String corpid, String period, String pk_category)
			throws DZFWarpException {
		OcrImageLibraryVO vos[] = null;
		if (ids != null && ids.length > 0) {
			String sql = "nvl(dr,0)=0 and pk_image_ocrlibrary in ( select ocr_id from ynt_interface_invoice where nvl(dr,0)=0 and "
					+ SqlUtil.buildSqlForIn("pk_invoice", ids) + ")";
			vos = (OcrImageLibraryVO[]) singleObjectBO.queryByCondition(OcrImageLibraryVO.class, sql,
					new SQLParameter());
		} else if (!StringUtil.isEmpty(pk_category)) {
			StringBuffer buff = new StringBuffer();
			SQLParameter param = new SQLParameter();
			buff.append(" pk_image_ocrlibrary in (  ");
			buff.append(
					"  select ocr_id From ynt_interface_invoice where nvl(dr,0)=0 and period =? and pk_billcategory in ( ");
			buff.append("  select pk_category From ynt_billcategory ");
			buff.append("  where dr = 0   start with pk_category = ? ");
			buff.append(
					"   connect by prior pk_category = pk_parentcategory) and nvl(dr,0)=0) and nvl(dr,0)=0 and pk_corp =?   ");
			buff.append("   ");
			param.addParam(period);
			param.addParam(pk_category);
			param.addParam(corpid);
			vos = (OcrImageLibraryVO[]) singleObjectBO.queryByCondition(OcrImageLibraryVO.class, buff.toString(),
					param);
		} else {
			SQLParameter param = new SQLParameter();
			String sql = "nvl(dr,0)=0 and pk_image_ocrlibrary in ( select ocr_id from ynt_interface_invoice where period =? and pk_corp =? and nvl(dr,0)=0 )";
			param.addParam(period);
			param.addParam(corpid);
			vos = (OcrImageLibraryVO[]) singleObjectBO.queryByCondition(OcrImageLibraryVO.class, sql, param);
		}
		return vos;
	}

	@Override
	public void updateChangeBillPeroid(String[] billid, String period) throws DZFWarpException {
		//String innerinvoicesql = " pk_invoice " + new SqlInUtil(billid).getInSql("invoice", "", "");
		String innerinvoicesql = SqlUtil.buildSqlForIn("pk_invoice", billid) + "";
		String invoicesql = "nvl(dr,0)=0 and "+innerinvoicesql;
		
		OcrInvoiceVO vos[] = (OcrInvoiceVO[]) singleObjectBO.queryByCondition(OcrInvoiceVO.class, invoicesql,
				new SQLParameter());
		if (vos == null || vos.length == 0)
			throw new BusinessException("没有查到对应的票据信息!");
		List<OcrInvoiceVO> list = Arrays.asList(vos);
		List<OcrInvoiceDetailVO> listdetail = iprebillservice.queryDetailByCondition(invoicesql);
		
		Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(listdetail,new String[] { "pk_invoice" });
		
		for (OcrInvoiceVO invoicevo : list) {
			invoicevo.setPeriod(period);
			invoicevo.setUpdateflag(DZFBoolean.TRUE);
			invoicevo.setDatasource(ZncsConst.SJLY_1);
			invoicevo.setPk_billcategory(null);
			invoicevo.setPk_category_keyword(null);
			if(detailMap.get(invoicevo.getPk_invoice())!=null){
				invoicevo.setChildren(detailMap.get(invoicevo.getPk_invoice()).toArray(new OcrInvoiceDetailVO[0]));
			}
		}

		if (ibillcategory.checkHaveZckp(vos).equals(DZFBoolean.TRUE)) {
			throw new BusinessException("所选票据已生成后续资产卡片，请检查！");
		}
		if (ibillcategory.checkHaveIctrade(vos).equals(DZFBoolean.TRUE)) {
			throw new BusinessException("所选票据已生成后续出入库单，请检查！");
		}

		singleObjectBO.updateAry(vos, new String[] { "period", "pk_billcategory", "pk_category_keyword" });
		for (OcrInvoiceDetailVO taivo : listdetail) {

			taivo.setPk_billcategory(null);
			taivo.setPk_category_keyword(null);
		}
		singleObjectBO.updateAry(listdetail.toArray(new OcrInvoiceDetailVO[0]),
				new String[] { "pk_billcategory", "pk_category_keyword" });
		// 更新进项销项银行对账单的 VATInComInvoiceVO VATSaleInvoiceVO BankStatementVO
		String condition = "select pk_image_group from ynt_interface_invoice where " + "nvl(dr,0)=0 and "
				+ innerinvoicesql+ "";
		SQLParameter param = new SQLParameter();
		//param.addParam(period);
		updateBillPeriod(vos[0].getPk_corp(), new VATInComInvoiceVO2().getTableName(), condition, param,period);
		updateBillPeriod(vos[0].getPk_corp(), new VATSaleInvoiceVO2().getTableName(), condition, param,period);
		updateBillPeriod(vos[0].getPk_corp(), new BankStatementVO2().getTableName(), condition, param,period);
		updateBillPeriod(vos[0].getPk_corp(), new ImageGroupVO().getTableName(), condition, param,period);
		updateBillPeriod(vos[0].getPk_corp(), new ImageLibraryVO().getTableName(), condition, param,period);
		CorpVO corpVO = corpService.queryByPk(vos[0].getPk_corp());

		invCategory(vos[0].getPk_corp(), period, list);
	}

	private void updateBillPeriod(String pk_corp, String tablename, String condition, SQLParameter param,String peroid) {
		// update ynt_vatincominvoice set inperiod =? where pk_image_group in (
		// )
		StringBuffer buff = new StringBuffer();
		if(tablename.equals("ynt_image_group")||tablename.equals("ynt_image_library")){
			buff.append(" update ").append(tablename)
				.append(" set cvoucherdate= '")
					.append(DateUtils.getPeriodEndDate(peroid))
					.append("' where nvl(dr,0)=0 ")
					.append("and pk_corp='")
					.append(pk_corp)
					.append("' and pk_image_group in ( ");
			buff.append(condition);// select pk_image_group from
									// ynt_interface_invoice
			buff.append(" )   ");
			singleObjectBO.executeUpdate(buff.toString(), param);
			
			return ;
		}
		
		
		buff.append(" update ").append(tablename)
				.append(" set inperiod ='"+peroid+"' ,period = '"+peroid+"' where nvl(dr,0)=0 ")
				.append("and pk_corp='")
				.append(pk_corp)
				.append("' and pk_image_group in ( ");
		buff.append(condition);// select pk_image_group from
								// ynt_interface_invoice
		buff.append(" )   ");
		// param.addParam(period);
		singleObjectBO.executeUpdate(buff.toString(), param);
	}

	@Override
	public List<BillInfoVO> queryInvalidBill(String corp, String period) throws DZFWarpException {
		StringBuffer buff = new StringBuffer();
		buff.append(
				" select li.pk_image_library as imgsourid , li.imgname as imgname From ynt_image_library li where pk_image_group in ( ");
		buff.append(" select iv.pk_image_group from ynt_interface_invoice iv");
		buff.append(" left join ynt_image_group yg on iv.pk_image_group = yg.pk_image_group");
		buff.append("  where yg.istate = ? and nvl(iv.dr,0)=0 and nvl(yg.dr,0)=0 and iv.period =? and yg.pk_corp =?)");
		buff.append("  and nvl(li.dr,0)=0 order by ts desc");
		SQLParameter param = new SQLParameter();
		param.addParam(PhotoState.state205);
		param.addParam(period);
		param.addParam(corp);
		List<BillInfoVO> list = (List<BillInfoVO>) singleObjectBO.executeQuery(buff.toString(), param,
				new BeanListProcessor(BillInfoVO.class));
		return list;
	}

	@Override
	public BillInfoVO queryBillInfo(String billid) throws DZFWarpException {
		
		
		BillInfoVO billinfovo = new BillInfoVO();
		if (billid != null && billid.contains(","))
		{
			String sa[] = billid.split(",");
			if (sa[0].length() > 0)
			{
				billid = sa[0];
			}
		}
		OcrInvoiceVO vo = (OcrInvoiceVO) singleObjectBO.queryByPrimaryKey(OcrInvoiceVO.class, billid);
		vo = (OcrInvoiceVO)proBillInfo(vo);
		OcrInvoiceDetailVO[] vos = (OcrInvoiceDetailVO[]) singleObjectBO.queryByCondition(OcrInvoiceDetailVO.class,
				"nvl(dr,0)=0 and pk_invoice ='" + billid + "'", new SQLParameter());
		for (OcrInvoiceDetailVO ocrInvoiceDetailVO : vos) {
			ocrInvoiceDetailVO = (OcrInvoiceDetailVO)proBillInfo(ocrInvoiceDetailVO);
		}
		vo.setChildren(vos);
		billinfovo.setInvoicvo(vo);
		OcrImageLibraryVO librayrvo[] = (OcrImageLibraryVO[]) singleObjectBO.queryByCondition(OcrImageLibraryVO.class,
				"nvl(dr,0)=0 and pk_image_ocrlibrary ='" + vo.getOcr_id() + "'", new SQLParameter());
		billinfovo.setImgsourid(librayrvo[0].getCrelationid());
		billinfovo.setImgname(librayrvo[0].getImgname());
		billinfovo.setCorpId(librayrvo[0].getPk_custcorp());
		// CodeUtils1.deCode(hvo.getCn_user()
		vo.setCorpName(CodeUtils1.deCode(corpService.queryByPk(librayrvo[0].getPk_custcorp()).getUnitname()));
		vo.setCorpCode(corpService.queryByPk(librayrvo[0].getPk_custcorp()).getUnitcode());
		
		if (ibillcategory.checkHaveIctrade(new OcrInvoiceVO[]{vo}).equals(DZFBoolean.TRUE)) {
			billinfovo.setMessage("已生成出入库单，请删除单据后再修改！");
			return billinfovo;
		}
		if (ibillcategory.checkHaveZckp(new OcrInvoiceVO[]{vo}).equals(DZFBoolean.TRUE)) {
			billinfovo.setMessage("已生成资产卡片，请删除卡片后再修改！");
			return billinfovo;
		}
		
		
		return billinfovo;
	}
	//应前端要求把string类型的为空的处理成""类型
	private SuperVO proBillInfo(SuperVO vo){
		if(vo == null) return vo;
			String []names = vo.getAttributeNames();
			for (String name : names) {
				Object obj = vo.getAttributeValue(name);
				//if(obj instanceof String){
					if(obj == null){
						try {
							vo.setAttributeValue(name, "");//如果不是string类型会报错就不设值跳过
						} catch (Exception e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}
				//}
			}
		return vo;
	}
	
	@Override
	public List<BillInfoVO> queryBillInfos(String billids, String period, String pk_corp) throws DZFWarpException {
		List<BillInfoVO> returnList = new ArrayList<BillInfoVO>();
		// 得到要做账的票
		List<OcrInvoiceVO> invoiceList = queryOcrInvoiceVOsByWhere(billids.split(","), period, pk_corp);
		// 查票据表体
		List<OcrInvoiceDetailVO> detailList = queryInvoiceDetail(invoiceList);
		// 表体按表头主键分组
		Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(detailList,
				new String[] { "pk_invoice" });
		// 查ocrlib
		List<OcrImageLibraryVO> libraryList = queryOcrImageLibraryVOs(invoiceList);
		Map<String, List<OcrImageLibraryVO>> libraryMap = DZfcommonTools.hashlizeObject(libraryList,
				new String[] { "pk_image_ocrlibrary" });

		for (int i = 0; i < invoiceList.size(); i++) {
			BillInfoVO billinfovo = new BillInfoVO();
			OcrInvoiceVO vo = invoiceList.get(i);
			List<OcrInvoiceDetailVO> details = detailMap.get(vo.getPk_invoice());
			vo.setChildren(details.toArray(new OcrInvoiceDetailVO[0]));
			billinfovo.setInvoicvo(vo);
			List<OcrImageLibraryVO> librarys = libraryMap.get(vo.getOcr_id());
			billinfovo.setImgsourid(librarys.get(0).getCrelationid());
			billinfovo.setImgname(librarys.get(0).getImgname());
			billinfovo.setCorpId(librarys.get(0).getPk_custcorp());
			vo.setCorpName(
					CodeUtils1.deCode(corpService.queryByPk(librarys.get(0).getPk_custcorp()).getUnitname()));
			vo.setCorpCode(corpService.queryByPk(librarys.get(0).getPk_custcorp()).getUnitcode());
			returnList.add(billinfovo);
		}
		return returnList;
	}

	private List<OcrImageLibraryVO> queryOcrImageLibraryVOs(List<OcrInvoiceVO> list) throws DZFWarpException {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		List<String> pkList = new ArrayList<String>();
		for (OcrInvoiceVO ocrInvoiceVO : list) {
			pkList.add(ocrInvoiceVO.getOcr_id());
		}
		sb.append("select * from ynt_image_ocrlibrary where nvl(dr,0)=0 ");
		sb.append(" and " + SqlUtil.buildSqlForIn("pk_image_ocrlibrary", pkList.toArray(new String[0])));
		List<OcrImageLibraryVO> returnList = (List<OcrImageLibraryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(OcrImageLibraryVO.class));
		return returnList;
	}

	private List<OcrInvoiceDetailVO> queryInvoiceDetail(List<OcrInvoiceVO> list) throws DZFWarpException {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		List<String> pkList = new ArrayList<String>();
		for (OcrInvoiceVO ocrInvoiceVO : list) {
			pkList.add(ocrInvoiceVO.getPk_invoice());
		}
		sb.append("select * from ynt_interface_invoice_detail where nvl(dr,0)=0 ");
		sb.append(" and " + SqlUtil.buildSqlForIn("pk_invoice", pkList.toArray(new String[0])));
		sb.append(" order by rowno");
		List<OcrInvoiceDetailVO> returnList = (List<OcrInvoiceDetailVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(OcrInvoiceDetailVO.class));
		return returnList;
	}

	private List<OcrInvoiceVO> queryOcrInvoiceVOsByWhere(String[] pk_invoices, String period, String pk_corp)
			throws DZFWarpException {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append("select a.* from ynt_interface_invoice a,ynt_image_group b");
		sb.append(" where a.pk_image_group=b.pk_image_group  and nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and b.istate !=205 ");
		sb.append(" and b.istate !=100 and b.istate !=101 ");
		sb.append(" and  " + SqlUtil.buildSqlForIn("a.pk_invoice", pk_invoices));
		sb.append(" and a.pk_corp=? ");
		sp.addParam(pk_corp);
		sb.append(" and a.period=?");
		sp.addParam(period);
		List<OcrInvoiceVO> list = (List<OcrInvoiceVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(OcrInvoiceVO.class));
		return list;
	}

	@Override
	public void updateInvoiceInfo(OcrInvoiceVO headvo, OcrInvoiceDetailVO bodyvos[]) throws DZFWarpException {
		if (headvo == null || bodyvos == null || bodyvos.length == 0) {
			throw new BusinessException("提交数据有误!");
		}
		OcrInvoiceVO headvoold = (OcrInvoiceVO)singleObjectBO.queryByPrimaryKey(OcrInvoiceVO.class , headvo.getPk_invoice());
		if(headvoold==null){
			throw new BusinessException("提交数据有误!");
		}
		headvo.setVpurchname(StringUtil.isEmpty(headvo.getVpurchname())?"":headvo.getVpurchname().trim());
		headvo.setVsalename(StringUtil.isEmpty(headvo.getVsalename())?"":headvo.getVsalename().trim());
		BillCategoryVO categoryvo = (BillCategoryVO)singleObjectBO.queryByPrimaryKey(BillCategoryVO.class , headvoold.getPk_billcategory());
		if(categoryvo!=null&&categoryvo.getIsaccount().equals("Y")){
			throw new BusinessException("该票据已生成凭证，无法修改!");
		}
		//有后续单据的不让修改
		checkZckpAndKc(new String[]{headvo.getPk_invoice()},null);
		// String
		// bodyupdatenames[]={"invname","invtype","itemunit","itemamount","itemprice","itemmny","itemtaxrate","itemtaxmny","rowno"};

		// 大致分为三种种,表体数据增加和删除,和修改,表体删没了但是必须得有一条空数据
		List<OcrInvoiceDetailVO> updatelist = new ArrayList<OcrInvoiceDetailVO>();
		List<OcrInvoiceDetailVO> deletelist = new ArrayList<OcrInvoiceDetailVO>();
		int k = 1;
		for (int i = 0; i < bodyvos.length; i++) {
			if (bodyvos[i].getDr() == null || "0".equals(bodyvos[i].getDr().toString())) {
				// if(!StringUtil.isEmpty(bodyvos[i].getItemtaxrate())){
				// bodyvos[i].setItemtaxrate(bodyvos[i].getItemtaxrate()+"%");
				// }
				if (StringUtil.isEmpty(bodyvos[i].getPk_billcategory())) {
					bodyvos[i].setPk_billcategory(headvo.getPk_billcategory());
				}
				if(k==1){
					headvo.setVfirsrinvname(bodyvos[i].getInvname());
				}
				bodyvos[i].setPk_invoice(headvo.getPk_invoice());
				bodyvos[i].setRowno(k++);
				bodyvos[i].setPk_corp(headvo.getPk_corp());
				bodyvos[i].setOcr_id(headvo.getOcr_id());
				updatelist.add(bodyvos[i]);
				
				k++;
			} else {
				deletelist.add(bodyvos[i]);
			}
		}
		if (deletelist.size() > 0) {
			singleObjectBO.deleteVOArray(deletelist.toArray(new OcrInvoiceDetailVO[0]));
		}
		if (updatelist.size() > 0) {
			for (OcrInvoiceDetailVO ocrvo : updatelist) {
				if (StringUtil.isEmpty(ocrvo.getPk_invoice_detail())) {
					singleObjectBO.insertVO(headvo.getPk_corp(), ocrvo);
				} else {

					singleObjectBO.update(ocrvo);
				}
			}
		} else {
			OcrInvoiceDetailVO tailvo = new OcrInvoiceDetailVO();
			tailvo.setPk_invoice(headvo.getPk_invoice());
			tailvo.setPk_billcategory(headvo.getPk_billcategory());
			tailvo.setRowno(1);
			tailvo.setPk_corp(headvo.getPk_corp());
			tailvo.setOcr_id(headvo.getOcr_id());
			updatelist.add(tailvo);
			singleObjectBO.insertVO(headvo.getPk_corp(), tailvo);
		}
		headvo.setRowcount(updatelist.size());
		

		String error2  = headvo.getErrordesc2();
		if(!StringUtil.isEmpty(error2)){//清空ocr部分异常处理
			if( error2.contains("开票日期为空")&&!StringUtil.isEmpty(headvo.getDinvoicedate())){
				String msg = error2.replaceAll(",开票日期为空","").replaceAll("开票日期为空", "");
				headvo.setErrordesc2(msg);
			}
			
			if( error2.contains("金额为空")&&!StringUtil.isEmpty(headvo.getNtotaltax())){
				String msg = error2.replaceAll(",金额为空","").replaceAll("金额为空", "");
				headvo.setErrordesc2(msg);
			}
		}
		if(StringUtil.isEmpty(headvo.getDinvoicedate())){
			//过滤掉定额发票和过路过桥的发票
			if(  !(!StringUtil.isEmpty(headvo.getInvoicetype())&&(headvo.getInvoicetype().equals("c定额发票")||headvo.getInvoicetype().contains("c过路过桥通行")) )   ){
				if(StringUtil.isEmpty(error2)|| !error2.contains("开票日期为空")){
					headvo.setErrordesc2(conErrerInfo(headvo,"开票日期为空"));
				}
			}
		}
		if(StringUtil.isEmpty(headvo.getNtotaltax())){
			if(StringUtil.isEmpty(error2)|| !error2.contains("金额为空")){
				headvo.setErrordesc2(conErrerInfo(headvo,"金额为空"));
			}
		}
		
		singleObjectBO.update(headvo);
		//删掉相关银行对账单,进销项数据重新生成
		String conditon = " vdef13=? and nvl(dr,0)=0 and pk_corp=?";
		SQLParameter param = new SQLParameter();// PhotoState.state205
		param.addParam(headvo.getPk_invoice());
		param.addParam(headvo.getPk_corp());
		
		//VATInComInvoiceVO2 incomvos []= (VATInComInvoiceVO2[])singleObjectBO.queryByCondition(VATInComInvoiceVO2.class, conditon, param); 
		// param.addParam(PhotoState.state205);
		invalidOtherBill(headvoold.getPk_corp(), new VATInComInvoiceVO2().getTableName(), conditon, param);
		invalidOtherBill(headvoold.getPk_corp(), new VATSaleInvoiceVO2().getTableName(), conditon, param);
		invalidOtherBill(headvoold.getPk_corp(), new BankStatementVO2().getTableName(), conditon, param);
		//
		param = new SQLParameter();// PhotoState.state205
		param.addParam(headvo.getPk_invoice());
		conditon= "nvl(dr,0)=0 and pk_invoice=?";
		OcrInvoiceVO hvo =(OcrInvoiceVO)singleObjectBO.queryByPrimaryKey(OcrInvoiceVO.class, headvo.getPk_invoice());
		OcrInvoiceDetailVO childvos[] =(OcrInvoiceDetailVO[])singleObjectBO.queryByCondition(OcrInvoiceDetailVO.class, conditon, param);
		hvo.setChildren(childvos);
		ImageGroupVO grpvo =(ImageGroupVO)singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, hvo.getPk_image_group());
		conditon = " pk_image_library = ( select crelationid From ynt_image_ocrlibrary where pk_image_ocrlibrary = (select ss.ocr_id from ynt_interface_invoice ss where pk_invoice=?))";
		ImageLibraryVO imglibs[]=(ImageLibraryVO[])singleObjectBO.queryByCondition(ImageLibraryVO.class, conditon, param);
		
		hvo.setPk_billcategory(null);
		billcreate.createBill(hvo, grpvo, imglibs[0],null);
	}

	
	public  String conErrerInfo(OcrInvoiceVO invvo ,String msg) {
    	if(invvo == null) return msg;
    	
        return StringUtil.isEmpty(invvo.getErrordesc2())?msg:(invvo.getErrordesc2()+","+msg);
    }
	
	@Override
	public void updateChangeBatchPeorid(BillcategoryQueryVO paramVO) throws DZFWarpException {
		if (StringUtil.isEmpty(paramVO.getPeriod()) || StringUtil.isEmpty(paramVO.getOldperiod())) {
			throw new BusinessException("期间不能为空");
		}
		StringBuffer buff;
		SQLParameter param;
		String pk_parant = "";
		String bankcode = "";
		if (StringUtil.isEmpty(paramVO.getPk_category()))
			throw new BusinessException("分组不能为空");

		if (paramVO.getPk_category().startsWith("bank_")) {
			bankcode = paramVO.getPk_category().substring(5, paramVO.getPk_category().length());
			pk_parant = paramVO.getPk_parentcategory();
		} else if (!StringUtil.isEmpty(paramVO.getPk_parentcategory())
				&& paramVO.getPk_parentcategory().startsWith("bank_")) {
			bankcode = paramVO.getPk_parentcategory().substring(5, paramVO.getPk_parentcategory().length());
			pk_parant = paramVO.getPk_category();
		} else {
			pk_parant = paramVO.getPk_category();
		}

		// 更新子表分组信息
		buff = new StringBuffer();
		param = new SQLParameter();

		
		buff = new StringBuffer();
		param = new SQLParameter();

		param.addParam(paramVO.getPk_corp());
		param.addParam(paramVO.getOldperiod());
		// set pk_billcategory =null ,pk_category_keyword =null , d.period=?
		buff.append("  select d.pk_invoice from ynt_interface_invoice d ");
		buff.append("   left join ynt_image_group g on d.pk_image_group =g.pk_image_group ");
		buff.append(
				"  where nvl(d.dr, 0) = 0  and d.pk_corp =? and d.period = ? and g.istate !=100 and g.istate !=101 and g.istate !=205  ");
		if (!StringUtil.isEmpty(bankcode)) {
			buff.append(" and (d.vpurchtaxno = ? or d.vsaletaxno = ?) ");
			param.addParam(bankcode);
			param.addParam(bankcode);
		}
		buff.append(" and d.pk_billcategory in ");
		buff.append(" (select pk_category ");
		buff.append("  from ynt_billcategory ");
		buff.append("   where nvl(dr,0) = 0   and nvl(isaccount, 'N') = 'N' ");
		buff.append("     start with pk_category = ? ");
		buff.append("      connect by prior pk_category = pk_parentcategory)  ");
		param.addParam(pk_parant);
//		List<OcrInvoiceVO> list = (List<OcrInvoiceVO>) singleObjectBO.executeQuery(buff.toString(), param,
//				new BeanListProcessor(OcrInvoiceVO.class));
//		singleObjectBO.exe
	//singleObjectBO.executeQuery(buff.toString(), param, new BeanListProcessor(String.class));
	  	//List <String> list = new ArrayList<>();
//		Object[] billid = (Object[]) singleObjectBO.executeQuery(buff.toString(), param,
//                new ArrayProcessor(list));
	  	List<String> list = (List<String>)singleObjectBO.executeQuery(buff.toString(), param, new ResultSetProcessor(){
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				List<String> list = new ArrayList<String>();
				while(rs.next()){
					list.add(rs.getString("pk_invoice"));
				}
				return list;
			}
		});
		if (list == null || list.size()== 0) {
			throw new BusinessException("未找到票据");
		}
		
		updateChangeBillPeroid(list.toArray(new String[0]), paramVO.getPeriod());
		
		
		
		
		
		
		/*
		
		param.addParam(paramVO.getPk_corp());
		param.addParam(paramVO.getOldperiod());
		buff.append(
				" update ynt_interface_invoice_detail set pk_billcategory =null,pk_category_keyword=null where pk_invoice in ");
		buff.append(" (select pk_invoice  from ynt_interface_invoice d ");
		buff.append("   left join ynt_image_group g on d.pk_image_group =g.pk_image_group ");
		buff.append(
				"  where nvl(d.dr, 0) = 0  and d.pk_corp =? and d.period = ?  and g.istate !=100 and g.istate !=101 and g.istate !=205 ");
		if (!StringUtil.isEmpty(bankcode)) {
			buff.append(" and (d.vpurchtaxno = ? or d.vsaletaxno = ?) ");
			param.addParam(bankcode);
			param.addParam(bankcode);
		}
		buff.append(" and d.pk_billcategory in ");
		buff.append(" (select pk_category ");
		buff.append("  from ynt_billcategory ");
		buff.append("   where nvl(dr,0) = 0  and nvl(isaccount, 'N') = 'N' ");
		buff.append("     start with pk_category = ? ");
		buff.append("      connect by prior pk_category = pk_parentcategory)  )");
		param.addParam(pk_parant);
		singleObjectBO.executeUpdate(buff.toString(), param);
		// //更新进项销项银行对账单的 VATInComInvoiceVO VATSaleInvoiceVO BankStatementVO
		buff = new StringBuffer();
		param = new SQLParameter();
		//param.addParam(paramVO.getPeriod());
		param.addParam(paramVO.getPk_corp());
		param.addParam(paramVO.getOldperiod());
		buff.append(" select d.pk_image_group  from ynt_interface_invoice d ");
		buff.append("   left join ynt_image_group g on d.pk_image_group =g.pk_image_group ");
		buff.append(
				"  where nvl(d.dr, 0) = 0  and d.pk_corp =? and d.period = ?  and g.istate !=100 and g.istate !=101 and g.istate !=205 ");
		if (!StringUtil.isEmpty(bankcode)) {
			buff.append(" and (d.vpurchtaxno = ? or d.vsaletaxno = ?) ");
			param.addParam(bankcode);
			param.addParam(bankcode);
		}
		buff.append(" and d.pk_billcategory in ");
		buff.append(" (select pk_category ");
		buff.append("  from ynt_billcategory ");
		buff.append("   where nvl(dr,0) = 0   and nvl(isaccount, 'N') = 'N' ");
		buff.append("     start with pk_category = ? ");
		buff.append("      connect by prior pk_category = pk_parentcategory)  ");
		param.addParam(pk_parant);
		updateBillPeriod(new VATInComInvoiceVO2().getTableName(), buff.toString(), param,paramVO.getPeriod());
		updateBillPeriod(new VATSaleInvoiceVO2().getTableName(), buff.toString(), param,paramVO.getPeriod());
		updateBillPeriod(new BankStatementVO2().getTableName(), buff.toString(), param,paramVO.getPeriod());
		updateBillPeriod(new ImageGroupVO().getTableName(), buff.toString(), param,paramVO.getPeriod());
		updateBillPeriod(new ImageLibraryVO().getTableName(), buff.toString(), param,paramVO.getPeriod());

		// 查询票据主表
		buff = new StringBuffer();
		param = new SQLParameter();

		param.addParam(paramVO.getPk_corp());
		param.addParam(paramVO.getOldperiod());
		// set pk_billcategory =null ,pk_category_keyword =null , d.period=?
		buff.append("  select d.* from ynt_interface_invoice d ");
		buff.append("   left join ynt_image_group g on d.pk_image_group =g.pk_image_group ");
		buff.append(
				"  where nvl(d.dr, 0) = 0  and d.pk_corp =? and d.period = ? and g.istate !=100 and g.istate !=101 and g.istate !=205  ");
		if (!StringUtil.isEmpty(bankcode)) {
			buff.append(" and (d.vpurchtaxno = ? or d.vsaletaxno = ?) ");
			param.addParam(bankcode);
			param.addParam(bankcode);
		}
		buff.append(" and d.pk_billcategory in ");
		buff.append(" (select pk_category ");
		buff.append("  from ynt_billcategory ");
		buff.append("   where nvl(dr,0) = 0   and nvl(isaccount, 'N') = 'N' ");
		buff.append("     start with pk_category = ? ");
		buff.append("      connect by prior pk_category = pk_parentcategory)  ");
		param.addParam(pk_parant);
		List<OcrInvoiceVO> list = (List<OcrInvoiceVO>) singleObjectBO.executeQuery(buff.toString(), param,
				new BeanListProcessor(OcrInvoiceVO.class));
		if (list == null || list.size() == 0) {
			throw new BusinessException("未找到票据");
		}

		List<OcrInvoiceDetailVO> listdetail = iprebillservice.queryDetailByInvList(list);

		Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(listdetail,
				new String[] { "pk_invoice" });

		for (OcrInvoiceVO invoicevo : list) {

			invoicevo.setPeriod(paramVO.getPeriod());
			invoicevo.setUpdateflag(DZFBoolean.TRUE);
			invoicevo.setDatasource(ZncsConst.SJLY_1);
			invoicevo.setPk_billcategory(null);
			invoicevo.setPk_category_keyword(null);
			if(detailMap.get(invoicevo.getPk_invoice())!=null){
				invoicevo.setChildren(detailMap.get(invoicevo.getPk_invoice()).toArray(new OcrInvoiceDetailVO[0]));
			}
		}

		if (ibillcategory.checkHaveZckp(list.toArray(new OcrInvoiceVO[0])).equals(DZFBoolean.TRUE)) {
			throw new BusinessException("所选票据已生成后续资产卡片，请检查！");
		}
		if (ibillcategory.checkHaveIctrade(list.toArray(new OcrInvoiceVO[0])).equals(DZFBoolean.TRUE)) {
			throw new BusinessException("所选票据已生成后续出入库单，请检查！");
		}
		// //更新主表
		buff = new StringBuffer();
		param = new SQLParameter();

		param.addParam(paramVO.getPk_corp());
		param.addParam(paramVO.getOldperiod());
		// set pk_billcategory =null ,pk_category_keyword =null , d.period=?
		buff.append(" update ( select d.* from ynt_interface_invoice d ");
		buff.append("   left join ynt_image_group g on d.pk_image_group =g.pk_image_group ");
		buff.append(
				"  where nvl(d.dr, 0) = 0  and d.pk_corp =? and d.period = ? and g.istate !=100 and g.istate !=101 and g.istate !=205  ");
		if (!StringUtil.isEmpty(bankcode)) {
			buff.append(" and (d.vpurchtaxno = ? or d.vsaletaxno = ?) ");
			param.addParam(bankcode);
			param.addParam(bankcode);
		}
		buff.append(" and d.pk_billcategory in ");
		buff.append(" (select pk_category ");
		buff.append("  from ynt_billcategory ");
		buff.append("   where nvl(dr,0) = 0   and nvl(isaccount, 'N') = 'N' ");
		buff.append("     start with pk_category = ? ");
		buff.append(
				"      connect by prior pk_category = pk_parentcategory)  ) set pk_billcategory = null, pk_category_keyword = null , period=?");
		param.addParam(pk_parant);
		param.addParam(paramVO.getPeriod());
		singleObjectBO.executeUpdate(buff.toString(), param);

		invCategory(paramVO.getPk_corp(), paramVO.getPeriod(), list);*/

	}

	// @Autowired
	// IPrebillService iPrebillService;
	private void invCategory(String pk_corp, String period, List<OcrInvoiceVO> list) {
		// 删除票头和票体上的pk_category_keyword
		// iPrebillService.updateInvoiceById(list);
		// iPrebillService.updateInvoiceDetailByInvId(list);
		boolean lock = false;
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		if (list == null)
			return;
		try {
			lock = redissonDistributedLock.tryGetDistributedFairLock("zncsCategory_"+pk_corp+period);
			if (lock) {
				cateservice.newSaveCorpCategory(null, pk_corp, period, corpvo);

				cateservice.updateInvCategory(list, pk_corp, period, corpvo);
			} else {
				//throw new BusinessException("分类失败,请稍后重试!");
			}
		} catch (Exception e) {
			throw new BusinessException("分类失败,请稍后重试!");
		} finally {
			if (lock) {
				redissonDistributedLock.releaseDistributedFairLock("zncsCategory_"+pk_corp+period);
			}
		}
	}

	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;

	@Override
	public List<OcrInvoiceVO> queryMatchInvoice(String corpid, String period, String[] billid, String category,
			int type) {// 1进项，2销项类，3全部
		Map<String, String> billmap = null;
		if (billid != null && billid.length > 0) {// billid过滤用
			billmap = new HashMap<String, String>();
			for (String string : billid) {
				billmap.put(string, string);
			}
		}

		List<OcrInvoiceVO> list = null;
		SQLParameter param = new SQLParameter();
		// 查出库存采购（对应进项）、 收入-主营业务收入-商品销售收入（对应销项）、收入-其他业务收入-销售材料收入（对应销项）及其以下分类或票据
		// 三个分类下的所有票据
		// 17 18 19
		StringBuffer sqlbuff = new StringBuffer();
		sqlbuff.append(" select s1.* From ynt_interface_invoice s1 ");
		sqlbuff.append(" left join ynt_billcategory s2 on s1.pk_billcategory = s2.pk_category ");
		sqlbuff.append("  left join ynt_image_group s3 on s3.pk_image_group = s1.pk_image_group ");
		sqlbuff.append(
				"  where  s1.pk_corp=? and s1.period = ? and s2.categorycode not like '17%' and s2.categorycode not like '18%' and s2.categorycode not like '19%'");
		sqlbuff.append(
				" and s1.istate='增值税发票' and nvl(s1.dr, 0) = 0 and nvl(s3.dr, 0) = 0  and s2.pk_corp = ? and s2.period = ?  and nvl(s2.isaccount, 'N') = 'N' and s3.istate not in(205)");
		param.addParam(corpid);
		param.addParam(period);
		param.addParam(corpid);
		param.addParam(period);
		list = (List<OcrInvoiceVO>) singleObjectBO.executeQuery(sqlbuff.toString(), param,
				new BeanListProcessor(OcrInvoiceVO.class));
		if (list == null || list.size() == 0)
			return null;

		sqlbuff = new StringBuffer();

		sqlbuff.append(
				" select s1.itemunit as unit , s1.pk_invoice,s1.pk_invoice_detail,s3.vinvoiceno ,s1.invname as bspmc ,s1.invtype as invspec ,s1.itemunit as measurename,s2.categorycode,s1.pk_billcategory from ynt_interface_invoice_detail s1");
		sqlbuff.append(" left join ynt_billcategory s2 on s1.pk_billcategory = s2.pk_category ");
		sqlbuff.append("left join ynt_interface_invoice s3 on s1.pk_invoice =s3.pk_invoice");
		sqlbuff.append(
				"   where nvl(s1.dr,0)=0 and ( s2.categorycode like '11%' or s2.categorycode like '101015%' or s2.categorycode like '101110%') ");
		sqlbuff.append(" and s1.pk_invoice =?");

		List<OcrInvoiceVO> ocrlist = new ArrayList<OcrInvoiceVO>();
		;
		for (OcrInvoiceVO ocrInvoiceVO : list) {
			if (billmap != null && !billmap.containsKey(ocrInvoiceVO.getPk_invoice())) {
				continue;
			}
			param = new SQLParameter();
			param.addParam(ocrInvoiceVO.getPk_invoice());
			List<OcrInvoiceTailInfoVO> listtail = (List<OcrInvoiceTailInfoVO>) singleObjectBO
					.executeQuery(sqlbuff.toString(), param, new BeanListProcessor(OcrInvoiceTailInfoVO.class));
			if (listtail != null && listtail.size() > 0) {
				for (OcrInvoiceTailInfoVO infoVO : listtail) {
					infoVO.setBspmc(OcrUtil.execInvname(infoVO.getBspmc()));
				}

				ocrInvoiceVO.setChildren(listtail.toArray(new OcrInvoiceTailInfoVO[0]));
				ocrlist.add(ocrInvoiceVO);
			}
		}

		return ocrlist;

	}

	@Override
	public List<InventoryAliasVO> matchInventoryData(String pk_corp, List<OcrInvoiceVO> ocrlist,
													 InventorySetVO invsetvo) throws DZFWarpException {

		Map<String, AuxiliaryAccountBVO> invenMap = new LinkedHashMap<>();
		Map<String, InventoryAliasVO> invenMap1 = new LinkedHashMap<>();
		Map<String, YntCpaccountVO> accountmap= new HashMap<String, YntCpaccountVO>();
		
		String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
		AuxiliaryAccountBVO[] invenvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_INVENTORY, pk_corp, null);
		int pprule = invsetvo.getChppjscgz();//匹配规则
		if (invenvos != null && invenvos.length > 0) {
			List<AuxiliaryAccountBVO> invenList = Arrays.asList(invenvos);
//			invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, new String[] { "name", "spec", "unit" });
			if(pprule == InventoryConstant.IC_RULE_1 ){//存货名称+计量单位
				invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, new String[] { "name", null, "unit" });
			}else{
				invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, new String[] { "name", "spec", "unit" });
			}
			Map<String, AuxiliaryAccountBVO> tempMap = DZfcommonTools.hashlizeObjectByPk(invenList,
					new String[] { "pk_auacount_b" });
			invenMap1 = buildInvenMapModel7(tempMap, invenMap, pk_corp,pprule);
		}
		/// ----
		Map<String, OcrInvoiceTailInfoVO> bvoMap = buildGoodsInvenRelaMapModel7(ocrlist, invsetvo);
		List<InventoryAliasVO> list = null;
		if (bvoMap != null && bvoMap.size() > 0) {
			list = new ArrayList<InventoryAliasVO>();
			Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(pk_corp);
			String key;
			AuxiliaryAccountBVO invenvo;
			OcrInvoiceTailInfoVO bvo;
			InventoryAliasVO relvo = null;
			InventoryAliasVO relvo1 = null;
			for (Map.Entry<String, OcrInvoiceTailInfoVO> entry : bvoMap.entrySet()) {
				relvo = null;
				key = entry.getKey();
				bvo = entry.getValue();
				invenvo = invenMap.get(key);

				// 先匹配别名 别名不存在在匹配存货
				if (invenMap1.containsKey(key)) {
					relvo = invenMap1.get(key);
				}
				if (relvo == null) {
					relvo = new InventoryAliasVO();
					relvo.setAliasname(bvo.getBspmc());
					relvo.setSpec(bvo.getInvspec());
					relvo.setUnit(bvo.getMeasurename());
					if (invenvo != null) {
						relvo.setPk_inventory(invenvo.getPk_auacount_b());
						relvo.setChukukmid(invenvo.getChukukmid());
						relvo.setKmclassify(invenvo.getKmclassify());
						YntCpaccountVO accvo = getAccountVO(accmap, invenvo.getChukukmid());
						if (accvo != null) {
							relvo.setKmmc_sale(accvo.getAccountname());
							relvo.setChukukmcode(accvo.getAccountcode());
						}
						accvo = getAccountVO(accmap, invenvo.getKmclassify());
						if (accvo != null) {
							relvo.setKmmc_invcl(accvo.getAccountname());
							relvo.setKmclasscode(accvo.getAccountcode());
						}
						
						if(StringUtil.isEmpty(relvo.getChukukmid())){
							accvo = accountmap.get(bvo.getPk_billcategory()+"_1");
							if(accvo==null){
								accvo = queryCategorSubj(bvo.getPk_billcategory(), new String[] { "101015", "101110" }, 1,
										pk_corp, accmap, newrule);
							}
							if(accvo==null){
								accvo = getFisrtNextLeafAccount("600101",accmap);
							}
							if(accvo==null){
								accvo = getFisrtNextLeafAccount("500101",accmap);
							}
							if (accvo != null) {
								relvo.setKmmc_sale(accvo.getAccountname());
								relvo.setChukukmcode(accvo.getAccountcode());
								relvo.setChukukmid(accvo.getPk_corp_account());
								accountmap.put(bvo.getPk_billcategory()+"_1", accvo);
							}
						}
						if(StringUtil.isEmpty(relvo.getKmclassify())){
							accvo = accountmap.get(bvo.getPk_billcategory()+"_2");
							if(accvo==null){
								accvo = queryCategorSubj(bvo.getPk_billcategory(), new String[] { "11" }, 2, pk_corp, accmap,
										newrule);
							}
							if(accvo==null){
								accvo = getFisrtNextLeafAccount("1405",accmap);
							}
						
							if (accvo != null) {
								relvo.setKmmc_invcl(accvo.getAccountname());
								relvo.setKmclasscode(accvo.getAccountcode());
								relvo.setKmclassify(accvo.getPk_corp_account());
								accountmap.put(bvo.getPk_billcategory()+"_2", accvo);
							}
							
						}
						
						String name = invenvo.getName();

						if (!StringUtil.isEmpty(invenvo.getSpec())) {
							name = name + " (" + invenvo.getSpec() + ")";
						}

						if (!StringUtil.isEmpty(invenvo.getUnit())) {
							name = name + " " + invenvo.getUnit();
						}
						relvo.setName(name);

						relvo1 = invenMap1.get(key);
						if (relvo1 != null)
							relvo.setPk_alias(relvo1.getPk_alias());
					} else {
						YntCpaccountVO accvo = null;
						relvo.setChukukmid(invsetvo.getKcspckkm());
						relvo.setKmclassify(invsetvo.getKcsprkkm());
						accvo = accountmap.get(bvo.getPk_billcategory()+"_1");
						if(accvo==null){
							accvo = queryCategorSubj(bvo.getPk_billcategory(), new String[] { "101015", "101110" }, 1,
									pk_corp, accmap, newrule);
						}
						if(accvo==null){
							accvo = getFisrtNextLeafAccount("600101",accmap);
						}
						if(accvo==null){
							accvo = getFisrtNextLeafAccount("500101",accmap);
						}
					
						if (accvo != null) {
							relvo.setKmmc_sale(accvo.getAccountname());
							relvo.setChukukmcode(accvo.getAccountcode());
							relvo.setChukukmid(accvo.getPk_corp_account());
						}
						accvo = accountmap.get(bvo.getPk_billcategory()+"_2");
						if(accvo==null){
							accvo = queryCategorSubj(bvo.getPk_billcategory(), new String[] { "11" }, 2, pk_corp, accmap,
									newrule);
						}
						if(accvo==null){
							accvo = getFisrtNextLeafAccount("1405",accmap);
						}
					
						if (accvo != null) {
							relvo.setKmmc_invcl(accvo.getAccountname());
							relvo.setKmclasscode(accvo.getAccountcode());
							relvo.setKmclassify(accvo.getPk_corp_account());
							accountmap.put(bvo.getPk_billcategory()+"_2", accvo);
						}
					}
					if (relvo.getHsl() == null || DZFDouble.ZERO_DBL.compareTo(relvo.getHsl()) == 0) {
						relvo.setHsl(DZFDouble.ONE_DBL);
					}
				}
				relvo.setFphm(bvo.getFphm());// 发票号
				list.add(relvo);
			}
		}
		
		if(list!=null && list.size()>0){
			List<String> slist = new ArrayList<>();
			Map<String,String>  map = new HashMap();
			for (OcrInvoiceVO svo : ocrlist) {
				if(map.containsKey(svo.getPeriod())) continue;
				else{
					map.put(svo.getPeriod(), svo.getPeriod());
					slist.add(svo.getPeriod());
				}
				
			}
			Map<String,List<VATSaleInvoiceBVO2>> salemap = querySaleInvoiceInfo(pk_corp, slist, pprule);
			int numPrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf009"));
			int pricePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf010"));
			
			for (InventoryAliasVO rvo : list) {
				InventorySaleInfoVO saleinfo = new InventorySaleInfoVO();
				saleinfo.setName(rvo.getAliasname());
				saleinfo.setSpec(rvo.getSpec());
				saleinfo.setUnit(rvo.getUnit());
				saleinfo.setPk_corp(pk_corp);
				InventorySaleInfoVO infovo = querySaleBillInfo(slist, pprule, saleinfo,salemap,numPrecision,pricePrecision);
				if(infovo!=null){
					rvo.setSaleNumber(infovo.getSaleNumber());
					rvo.setSalePrice(infovo.getSalePrice());
				}
				
			}
		}
		return list;
	}

	private YntCpaccountVO getAccountVO(Map<String, YntCpaccountVO> accmap, String accid) {
		if (accmap == null || accmap.size() == 0)
			return null;
		return accmap.get(accid);
	}

	// 查询第一分支的最末级科目
	private YntCpaccountVO getFisrtNextLeafAccount(String accountcode, Map<String, YntCpaccountVO> ccountMap) {

		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();// 存储下级科目
		for (YntCpaccountVO accvo : ccountMap.values()) {
			if (accvo.getIsleaf().booleanValue() && accvo.getAccountcode() != null
					&& accvo.getAccountcode().startsWith(accountcode)) {
				list.add(accvo);
			}
		}

		if (list == null || list.size() == 0) {
			return null;
		}
		YntCpaccountVO[] accountvo = list.toArray(new YntCpaccountVO[list.size()]);
		VOUtil.ascSort(accountvo, new String[] { "accountcode" });
		return accountvo[0];
	}

	private Map<String, InventoryAliasVO> buildInvenMapModel7(Map<String, AuxiliaryAccountBVO> tempinMap,
			Map<String, AuxiliaryAccountBVO> invenMap, String pk_corp,int pprule) {

		Map<String, InventoryAliasVO> invenMap1 = new LinkedHashMap<String, InventoryAliasVO>();

		List<InventoryAliasVO> list = queryInventoryAliasVO(pk_corp);
		Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(pk_corp);

		if (list != null && list.size() > 0) {
			String key;
			String pk_inventory;
			AuxiliaryAccountBVO avo = null;
			for (InventoryAliasVO vo : list) {
				//key = vo.getAliasname() + "," + vo.getSpec() + "," + vo.getUnit();
				if(pprule == InventoryConstant.IC_RULE_1 ){//存货名称+计量单位
					key = vo.getAliasname() + ",null," + vo.getUnit();
				}else{
					key = vo.getAliasname() + "," + vo.getSpec() + "," + vo.getUnit();
				}
				if (invenMap1.containsKey(key)) {
					continue;
				}

				pk_inventory = vo.getPk_inventory();
				if (!StringUtil.isEmpty(pk_inventory) && tempinMap.containsKey(pk_inventory)) {
					avo = tempinMap.get(pk_inventory);
					vo.setChukukmid(avo.getChukukmid());
					vo.setKmclassify(avo.getKmclassify());
					YntCpaccountVO accvo = getAccountVO(accmap, vo.getChukukmid());
					if (accvo != null) {
						vo.setKmmc_sale(accvo.getAccountname());
					}
					accvo = getAccountVO(accmap, vo.getKmclassify());
					if (accvo != null) {
						vo.setKmmc_invcl(accvo.getAccountname());
					}
					vo.setName(avo.getName());
					invenMap1.put(key, vo);
				}
			}
		}
		return invenMap1;
	}

	private List<InventoryAliasVO> queryInventoryAliasVO(String pk_corp) throws DZFWarpException {

		String sql = "Select * From ynt_icalias y Where y.pk_corp = ? and nvl(dr,0) = 0 order by ts desc";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		List<InventoryAliasVO> list = (List<InventoryAliasVO>) singleObjectBO.executeQuery(sql, sp,
				new BeanListProcessor(InventoryAliasVO.class));

		return list;

	}

	private Map<String, OcrInvoiceTailInfoVO> buildGoodsInvenRelaMapModel7(List<OcrInvoiceVO> list,
			InventorySetVO invsetvo) {

		int pprule = invsetvo.getChppjscgz();// 匹配规则
		Map<String, OcrInvoiceTailInfoVO> map = new LinkedHashMap<>();

		String key;
		OcrInvoiceTailInfoVO[] bvos = null;

		for (OcrInvoiceVO vo : list) {
			// if(!StringUtil.isEmpty(vo.getPk_tzpz_h())){
			// continue;
			// }
			bvos = (OcrInvoiceTailInfoVO[]) vo.getChildren();
			if (bvos == null || bvos.length == 0)
				continue;
			// if(bvos[0].getIszd().booleanValue()) continue;

			if (bvos != null && bvos.length > 0) {
				for (OcrInvoiceTailInfoVO bvo : bvos) {
					// key = bvo.getBspmc() + "," + bvo.getInvspec() + "," +
					// bvo.getMeasurename();
					key = buildByRule(bvo.getBspmc(), bvo.getInvspec(), bvo.getMeasurename(), pprule);

					if (!map.containsKey(key)) {
						bvo.setFphm(vo.getVinvoiceno());
						map.put(key, bvo);
					}

				}
			}
		}
		return map;
	}

	private String buildByRule(String name, String gg, String unit, int rule) {
		String key = null;
		if (rule == InventoryConstant.IC_RULE_1) {// 存货名称+计量单位
			key = name + ",null," + unit;
		} else {
			key = name + "," + gg + "," + unit;
		}

		return key;
	}

	@Override
	public List<VatGoosInventoryRelationVO> getGoodsInvenRela(List<OcrInvoiceVO> saleList, String pk_corp)
			throws DZFWarpException {

		Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(pk_corp);
		String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
		YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);
		Map<String, YntCpaccountVO> accountmap= new HashMap<String, YntCpaccountVO>();
		AuxiliaryAccountBVO[] invenvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_INVENTORY, pk_corp, null);

		Map<String, AuxiliaryAccountBVO> invenMap = new HashMap<>();
		Map<String, AuxiliaryAccountBVO> invenMap1 = new HashMap<>();
		//aList<InventoryVO> intorylist = inventoryservice.querySpecialKM(pk_corp);
		if (invenvos != null && invenvos.length > 0) {
			List<AuxiliaryAccountBVO> invenList = Arrays.asList(invenvos);
			invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, new String[] { "name", "spec","unit" });//加上计量单位
			Map<String, AuxiliaryAccountBVO> tempMap = DZfcommonTools.hashlizeObjectByPk(invenList,
					new String[] { "pk_auacount_b" });
			invenMap1 = buildInvenMap(tempMap, invenMap, pk_corp);
		}

		Map<String, OcrInvoiceTailInfoVO> bvoMap = buildGoodsInvenRelaMap(saleList);

		List<VatGoosInventoryRelationVO> list = null;
		if (bvoMap != null && bvoMap.size() > 0) {
			list = new ArrayList<VatGoosInventoryRelationVO>();

			String key;
			AuxiliaryAccountBVO invenvo;
			OcrInvoiceTailInfoVO bvo;
			VatGoosInventoryRelationVO relvo;
			for (Map.Entry<String, OcrInvoiceTailInfoVO> entry : bvoMap.entrySet()) {
				key = entry.getKey();
				bvo = entry.getValue();
				invenvo = invenMap1.get(key);//先匹配别名
				relvo = new VatGoosInventoryRelationVO();
				if (invenvo == null && invenMap.containsKey(key)) {
					invenvo = invenMap.get(key);
				}
				if(invenMap.containsKey(key)){
					relvo.setPk_inventory_old(invenMap.get(key).getPrimaryKey());
				}
				
				if (invenvo != null) {
					relvo.setPk_inventory(invenvo.getPrimaryKey());
//					relvo.setPk_inventory_old(invenvo.getPrimaryKey());
					relvo.setCalcmode(invenvo.getCalcmode());
					relvo.setHsl(invenvo.getHsl());
					relvo.setCode(invenvo.getCode());
					relvo.setName(invenvo.getName());
					relvo.setPk_subj(invenvo.getPk_accsubj());
					relvo.setSubjname(invenvo.getSubjname());
				}
				if(StringUtil.isEmpty(relvo.getPk_subj())){
					YntCpaccountVO accountvo = accountmap.get(bvo.getPk_billcategory());
					if(accountvo==null){
						queryCategorSubj(bvo.getPk_billcategory(), new String[] { "11" }, 2, pk_corp,
								accmap, newrule);
						}
					if (accountvo == null) {
						for (YntCpaccountVO acc : accounts) {
							if ("1405".equalsIgnoreCase(acc.getAccountcode())){
								//invvo.setPk_subject(acc.getPk_corp_account());
							relvo.setPk_subj(acc.getPk_corp_account());
							relvo.setSubjname(acc.getAccountname());
							//relvo.setSubjname(acc.get);
							accountmap.put(bvo.getPk_billcategory(), acc);
							}
						}
					} else {
						//invvo.setPk_subject(accountvo.getPk_corp_account());
						relvo.setPk_subj(accountvo.getPk_corp_account());
						relvo.setSubjname(accountvo.getAccountname());
						accountmap.put(bvo.getPk_billcategory(), accountvo);
					}
				}
				
				if(relvo.getHsl()==null){
					relvo.setHsl(DZFDouble.ONE_DBL);
					relvo.setCalcmode(0);
				}
				relvo.setFphm(bvo.getFphm());
				relvo.setUnit(bvo.getMeasurename());
				relvo.setSpmc(bvo.getBspmc());
				relvo.setInvspec(bvo.getInvspec());

				list.add(relvo);
			}
		}

		
		if(list!=null && list.size()>0){
			List<String> slist = new ArrayList<>();
			for (OcrInvoiceVO svo : saleList) {
				slist.add(svo.getPeriod());
			}
			Map<String,List<VATSaleInvoiceBVO2>> salemap = querySaleInvoiceInfo(pk_corp, slist, InventoryConstant.IC_RULE_0);
			int numPrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf009"));
			int pricePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf010"));
			
			for (VatGoosInventoryRelationVO rvo : list) {
				InventorySaleInfoVO saleinfo = new InventorySaleInfoVO();
				saleinfo.setName(rvo.getSpmc());
				saleinfo.setSpec(rvo.getInvspec());
				saleinfo.setUnit(rvo.getUnit());
				saleinfo.setPk_corp(pk_corp);
				InventorySaleInfoVO infovo = querySaleBillInfo(slist, InventoryConstant.IC_RULE_0, saleinfo,salemap,numPrecision,pricePrecision);
				//querySaleBillInfo(slist, InventoryConstant.IC_RULE_0, saleinfo,null,0,0);
				if(infovo!=null){
					rvo.setSaleNumber(infovo.getSaleNumber());
					rvo.setSalePrice(infovo.getSalePrice());
				}
				
			}
		}
		return list;
	}

	private Map<String, AuxiliaryAccountBVO> buildInvenMap(Map<String, AuxiliaryAccountBVO> tempinMap,
			Map<String, AuxiliaryAccountBVO> invenMap, String pk_corp) {

		Map<String, AuxiliaryAccountBVO> invenMap1 = new HashMap<String, AuxiliaryAccountBVO>();

		List<InventoryAliasVO> list = queryVatGoosInvenRela(pk_corp);

		if (list != null && list.size() > 0) {
			String key;
			String pk_inventory;
			for (InventoryAliasVO vo : list) {
				//key = vo.getSpmc() + "," + vo.getInvspec();
				key = vo.getAliasname() + "," + vo.getSpec()+ ","+vo.getUnit();//名称规格计量单位
				
				if (invenMap1.containsKey(key)) {
					continue;
				}

				pk_inventory = vo.getPk_inventory();
				if (!StringUtil.isEmpty(pk_inventory) && tempinMap.containsKey(pk_inventory)) {
					AuxiliaryAccountBVO svo = (AuxiliaryAccountBVO)tempinMap.get(pk_inventory).clone();
					svo.setHsl(vo.getHsl());
					svo.setCalcmode(vo.getCalcmode());
					svo.setUnit(vo.getUnit());
					invenMap1.put(key, svo);
				}

			}
		}

		return invenMap1;
	}

	private List<InventoryAliasVO> queryVatGoosInvenRela(String pk_corp) throws DZFWarpException {

		String sql = "Select * From ynt_icalias y Where y.pk_corp = ? and nvl(dr,0) = 0 order by ts desc";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		List<InventoryAliasVO> list = (List<InventoryAliasVO>) singleObjectBO.executeQuery(sql, sp,
				new BeanListProcessor(InventoryAliasVO.class));

		return list;

	}

	private Map<String, OcrInvoiceTailInfoVO> buildGoodsInvenRelaMap(List<OcrInvoiceVO> saleList) {

		Map<String, OcrInvoiceTailInfoVO> map = new HashMap<>();

		String key;
		OcrInvoiceTailInfoVO[] bvos = null;

		for (OcrInvoiceVO vo : saleList) {
			bvos = (OcrInvoiceTailInfoVO[]) vo.getChildren();

			if (bvos != null && bvos.length > 0) {
				for (OcrInvoiceTailInfoVO bvo : bvos) {
					key = bvo.getBspmc() + "," + bvo.getInvspec()+","+bvo.getUnit();
					bvo.setFphm(vo.getVinvoiceno());
					if (!map.containsKey(key)) {
						map.put(key, bvo);
					}

				}
			}

		}

		return map;
	}

	@Override
	public void updateGoodsInvenRela(VatGoosInventoryRelationVO[] goodsvois, String cuserid, String pk_corp)
			throws DZFWarpException {
		if (goodsvois == null || goodsvois.length == 0)
			throw new BusinessException("商品信息不能为空");
		Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(pk_corp);
		YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);
		String rule=null;
		String sql = "Select * From ynt_icalias y Where y.pk_corp = ? and nvl(dr,0) = 0 order by ts desc";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		List<InventoryAliasVO> list = (List<InventoryAliasVO>) singleObjectBO.executeQuery(sql, sp,
				new BeanListProcessor(InventoryAliasVO.class));
		Map<String, InventoryAliasVO> map = new HashMap<String, InventoryAliasVO>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String key = list.get(i).getAliasname() + "," + list.get(i).getSpec() + ","+list.get(i).getUnit();
				if (!map.containsKey(key)) {
					map.put(key, list.get(i));
				}
			}
		}

		for (VatGoosInventoryRelationVO gvo : goodsvois) {
			String key = gvo.getSpmc() + "," + gvo.getInvspec()+ "," + gvo.getUnit();
			String pk_inventory_old = gvo.getPk_inventory_old();
			String pk_inventory = gvo.getPk_inventory();
			if (StringUtil.isEmpty(pk_inventory)||  (!StringUtil.isEmpty(pk_inventory_old)&&pk_inventory_old.equals(pk_inventory))) {
				if (map.containsKey(key)) {
					singleObjectBO.deleteObject(map.get(key));
				}
				matchInvtoryIC(gvo, pk_corp, cuserid,rule,accmap,accounts);
			} else {
				InventoryAliasVO alvo = new InventoryAliasVO();
				alvo.setAliasname(gvo.getSpmc());
				alvo.setSpec(gvo.getInvspec());
				alvo.setPk_corp(pk_corp);
				alvo.setPk_inventory(gvo.getPk_inventory());
				alvo.setUnit(gvo.getUnit());
				alvo.setCalcmode(gvo.getCalcmode());
				alvo.setHsl(gvo.getHsl());
				
				if (!map.containsKey(key)) {
					singleObjectBO.insertVO(pk_corp, alvo);
				} else {
					InventoryAliasVO ivo = map.get(key);
					ivo.setPk_inventory(gvo.getPk_inventory());
					ivo.setCalcmode(gvo.getCalcmode());
					ivo.setHsl(gvo.getHsl());
					singleObjectBO.update(ivo);// ,"spmc","invspec"
				}
			}

		}
	}

	private MeasureVO getMeasureVO(String measurename, String pk_corp, String cuserid) {// VATSaleInvoiceVO
																						// salevo,
																						// VATSaleInvoiceBVO
																						// salechild,
		// 查找计量单位

		if (StringUtil.isEmpty(measurename)) {
			return new MeasureVO();
		}

		StringBuffer sb = new StringBuffer();
		sb.append(" pk_corp=? and nvl(dr,0)=0 and name = ? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(measurename);
		List<MeasureVO> listVo = (List<MeasureVO>) singleObjectBO.retrieveByClause(MeasureVO.class, sb.toString(), sp);
		MeasureVO meavo = null;
		if (listVo == null || listVo.size() == 0) {
			meavo = new MeasureVO();
			meavo.setPk_corp(pk_corp);
			meavo.setCreatetime(new DZFDateTime());
			meavo.setCreator(cuserid);
			meavo.setName(measurename);
			meavo.setCode(yntBoPubUtil.getMeasureCode(pk_corp));
			listVo = new ArrayList<>();
			listVo.add(meavo);
			measervice.updateVOArr(pk_corp, cuserid,listVo);
		} else {
			meavo = listVo.get(0);
		}
		return meavo;
	}

	public InventoryVO matchInvtoryIC(VatGoosInventoryRelationVO gvo, String pk_corp, String cuserid, String newrule, Map<String, YntCpaccountVO> accmap, YntCpaccountVO[] accounts)
			throws DZFWarpException {
		//Map<String, YntCpaccountVO> accmap = AccountCache.getInstance().getMap(null, pk_corp);
		if (StringUtil.isEmpty(pk_corp) || gvo == null)
			return null;
		 newrule = StringUtil.isEmpty(newrule)?gl_cpacckmserv.queryAccountRule(pk_corp):newrule;
		//YntCpaccountVO[] accounts = AccountCache.getInstance().get(null, pk_corp);

		MeasureVO meavo = null;
		InventoryVO invvo = null;
		String pk_measure = null;

		String pk_inventory = null;

		meavo = getMeasureVO(gvo.getUnit(), pk_corp, cuserid);

		if (meavo != null) {
			pk_measure = meavo.getPk_measure();
		}

		invvo = ocr_atuomatch.getInventoryVOByName(gvo.getSpmc(), gvo.getInvspec(), pk_measure, pk_corp);

		if (invvo == null && !StringUtil.isEmpty(gvo.getSpmc())) {
			invvo = new InventoryVO();
			invvo.setPk_corp(pk_corp);
			invvo.setCreatetime(new DZFDateTime());
			if (meavo != null)
				invvo.setPk_measure(pk_measure);
			invvo.setCreator(gvo.getCoperatorid());

			invvo.setInvspec(gvo.getInvspec());
			invvo.setName(gvo.getSpmc());

			invvo.setCode(yntBoPubUtil.getInventoryCode(pk_corp));
			invvo.setPk_subject(gvo.getPk_subj());
			// invvo.setPk_measure(pk_measure);
			if(StringUtil.isEmpty(gvo.getPk_subj())){
				YntCpaccountVO accountvo = queryCategorSubj(gvo.getPk_billcategory(), new String[] { "11" }, 2, pk_corp,
						accmap, newrule);
				if (accountvo == null) {
					for (YntCpaccountVO acc : accounts) {
						if ("1405".equalsIgnoreCase(acc.getAccountcode())){
							invvo.setPk_subject(acc.getPk_corp_account());
						}
					}
				} else {
					invvo.setPk_subject(accountvo.getPk_corp_account());
				}
			}

			invservice.save(pk_corp, new InventoryVO[] { invvo });
		}
		return invvo;
	}

	public YntCpaccountVO queryCategorSubj(String pk_billcagegory, String catecode[], int jici, String pk_corp,
			Map<String, YntCpaccountVO> accmap, String newrule) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_billcagegory)) {
			return null;
		}
		YntCpaccountVO cavo = new YntCpaccountVO();
		CorpVO corp = corpService.queryByPk(pk_corp);
		String corptype = corp.getCorptype();

		// 先去前台编辑分类的设置里面找
		StringBuffer sqlbuff = new StringBuffer();
		SQLParameter param = new SQLParameter();
		// sqlbuff.append("nvl(dr,0)=0 and categorycode like ? and
		// pk_category=?");
		sqlbuff.append("  select * from ynt_billcategory  where nvl(dr,0)=0 and pk_corp = ? and  (");
		param.addParam(pk_corp);
		for (int i = 0; i < catecode.length; i++) {
			sqlbuff.append("categorycode like ? ");
			param.addParam(catecode[i] + "%");
			if (i < catecode.length - 1) {
				sqlbuff.append(" or ");
			}
		}
		sqlbuff.append(") start with pk_category = ? ");
		sqlbuff.append(" connect by prior   pk_parentcategory = pk_category ");
		sqlbuff.append(" order by categorycode  ");
		param.addParam(pk_billcagegory);
		
		List<BillCategoryVO> list = (List<BillCategoryVO>) singleObjectBO.executeQuery(sqlbuff.toString(), param,
				new BeanListProcessor(BillCategoryVO.class));
		if (list == null || list.size() == 0 || list.size() < jici){
			String defaultcode = "1110";
			if (catecode.length == 1) {
				defaultcode = "1110";
			} else {
				defaultcode = "101015";
			}
			String sql = " categorycode ='" + defaultcode + "' and nvl(dr,0)=0  ";
			BaseCategoryVO catevo[] = (BaseCategoryVO[]) singleObjectBO.queryByCondition(BaseCategoryVO.class, sql,
					new SQLParameter());
			return processAccount(catevo[0].getPk_basecategory(), corptype, accmap, newrule);
		}
			//return null;

		sqlbuff = new StringBuffer();
		param = new SQLParameter();
		sqlbuff.append(" nvl(dr,0)=0 and   pk_category=?");
		BillCategoryVO category = (BillCategoryVO) list.get(jici - 1);
		param.addParam(category.getPk_category());
		CategorysetVO setvo[] = (CategorysetVO[]) singleObjectBO.queryByCondition(CategorysetVO.class,
				sqlbuff.toString(), param);
		if (setvo != null && setvo.length > 0 && !StringUtil.isEmpty(setvo[0].getPk_accsubj())) {
			// cavo.setPk_corp_account(setvo[0].getPk_accsubj());
			return getAccountVO(accmap, setvo[0].getPk_accsubj());
		}

		// 然后再去后台入账规则去找
		BillCategoryVO categoryvo = (BillCategoryVO) singleObjectBO.queryByPrimaryKey(BillCategoryVO.class,
				pk_billcagegory);
		if (categoryvo == null)
			return null;
		String categorycode = categoryvo.getCategorycode();

		if (categorycode.startsWith("1110") || categorycode.startsWith("1111")// 库存商品和原材料
				|| categorycode.startsWith("101015") || categorycode.startsWith("101110")) {
			return processAccount(category.getPk_basecategory(), corptype, accmap, newrule);
		} else {
			String defaultcode = "1110";
			if (catecode.length == 1) {
				defaultcode = "1110";
			} else {
				defaultcode = "101015";
			}
			String sql = " categorycode ='" + defaultcode + "' and nvl(dr,0)=0  ";
			BaseCategoryVO catevo[] = (BaseCategoryVO[]) singleObjectBO.queryByCondition(BaseCategoryVO.class, sql,
					new SQLParameter());
			return processAccount(catevo[0].getPk_basecategory(), corptype, accmap, newrule);
		}

	}

	// 通过分类去找后台对应的末级科目
	private YntCpaccountVO processAccount(String Pk_basecategory, String corptype, Map<String, YntCpaccountVO> accmap,
			String newrule) {
		String condition = " nvl(dr,0)=0 and pk_basecategory =? and pk_accountschema = ?";
		SQLParameter param = new SQLParameter();
		param.addParam(Pk_basecategory);
		param.addParam(corptype);
		AccsetVO vos[] = (AccsetVO[]) singleObjectBO.queryByCondition(AccsetVO.class, condition, param);
		if (vos != null && vos.length > 0) {
			BdTradeAccountVO jfkmVO = (BdTradeAccountVO) singleObjectBO.queryVOByID(vos[0].getPk_accsubj(),
					BdTradeAccountVO.class);
			String kmnew_accsubj = gl_accountcoderule.getNewRuleCode(jfkmVO.getAccountcode(),
					DZFConstant.ACCOUNTCODERULE, newrule);

			YntCpaccountVO accvo = getFisrtNextLeafAccount(kmnew_accsubj, accmap);
			return accvo;
		}
		return null;
	}

	/****
	 * 通过状态查询票据图片,,然后通过票据id查询票据信息 istate PhotoState.state205 不传 :全部,205:票据作废
	 * ,206 处理中,state100:已生成凭证,900:未生成凭证
	 */

	@Override
	public List<BillInfoVO> queryBillByState(String corp, String period, Integer state) throws DZFWarpException {

		SQLParameter param = new SQLParameter();
		// --0处理中--1作废--2未生成凭证--3已生成凭证
		StringBuffer buff = new StringBuffer();
		buff.append(
				" select  nvl(yi.webid,'------') as webid , yc.pk_custcorp as corpId, yi.pk_invoice as billid, yc.crelationid as imgsourid ,yc.imgname as imgname  ");
		buff.append(
				", case when ( (yi.pk_invoice is null or yi.pk_billcategory is null) and  yg.istate not in (205,100,101) ) then '0' when yg.istate=205 then '1' when (yg.istate not in (205,100,101) and yi.pk_billcategory is not null ) then '2' when yg.istate  in (100,101) then '3'   end as istate ");
		buff.append("   From ynt_image_ocrlibrary yc ");
		buff.append("  left join ynt_image_library yci on yci.pk_image_library = yc.crelationid ");
		buff.append("  left join ynt_image_group yg on  yci.pk_image_group = yg.pk_image_group ");
		buff.append("  left join ynt_interface_invoice yi on yi.ocr_id = yc.pk_image_ocrlibrary and nvl(yi.dr,0)=0  ");
		buff.append("    where nvl(yc.dr,0)=0 and nvl(yg.dr,0)=0 and yc.pk_custcorp = ?   ");
		param.addParam(corp);
	//	if (state == null || state == 206 || state == 100) {
			buff.append(" and (yi.period = ? or (yi.period is null and  yg.cvoucherdate like ?) ) ");
			param.addParam(period);
			param.addParam(period + "%");
//		} else {
//			buff.append("   	 and yi.period = ? ");
//			param.addParam(period);
//		}
		if (state != null) {
			if (state == 206) { // 正在处理中
				buff.append("    and ( (yi.pk_invoice is null or yi.pk_billcategory is null) and  yg.istate not in (205,100,101) )");//yg.istate <> 205剔除已做账
			} else if (state == 900) {//未生成凭证
				buff.append("   and yg.istate not in (205,100,101)  and yi.pk_billcategory is not null ");
				// param.addParam(state);
			} else if (state == 100) {//已生成凭证
				buff.append("   and yg.istate  in (100,101) ");
			} else {
				buff.append("   and yg.istate = ? ");
				param.addParam(state);
			}
		}
		buff.append(" order by yc.iorder asc");
		List<BillInfoVO> list = (List<BillInfoVO>) singleObjectBO.executeQuery(buff.toString(), param,
				new BeanListProcessor(BillInfoVO.class));
		return list;
	}

	// 0;//银行对账单导入、录入 未绑定1;//回单回传， 未绑定
	@Override
	public List<BankStatementVO2> queryBankInfo(String pk_corp, String bperiod, String eperiod, String account,
			String type, String accountcode, String izmr) throws DZFWarpException {
		if (StringUtil.isEmpty(bperiod) || StringUtil.isEmpty(eperiod)) {
			throw new BusinessException("期间不能为空!");
		}
		String bdate = bperiod; //+ "-01";
		String edate =eperiod;// DateUtils.getPeriodEndDate(eperiod).toString();
		// if(StringUtil.isEmpty(account)){
		// throw new BusinessException("银行账号不能为空不能为空!");
		// }/
		if (StringUtil.isEmpty(type)) {
			throw new BusinessException("查询类型不能为空!");
		}
		SQLParameter sp = new SQLParameter();
		StringBuffer sb = new StringBuffer();
		List<BankStatementVO2> listVo = null;
		if (type.equals("0")) {//银行对账单导入的数据
			sb.append(" select b.bankaccount as accountcode,b.bankname as accountname, t.billstatus , ");
			sb.append(" case when syje is null then zcje else syje end as mony ,syje,zcje, ");
			sb.append(" othaccountname,othaccountcode,t.tradingdate,c.categoryname as busitypetempname, ");
			sb.append(" t.pk_bankstatement From ynt_bankstatement t  ");
			sb.append(" left join ynt_bankaccount b on t.pk_bankaccount = b.pk_bankaccount and nvl(b.dr,0)=0 ");
			sb.append(" left join ynt_billcategory c on c.pk_category = t.pk_model_h ");
			sb.append(
					" where t.billstatus =0 and  nvl(t.dr,0)=0 and t.pk_corp=? and t.inperiod >= ?  and t.inperiod <=? ");
			sp.addParam(pk_corp);
			sp.addParam(bdate);
			sp.addParam(edate);
			if (!StringUtil.isEmpty(izmr)) {
				sb.append(
						" and (t.pk_bankaccount is null or t.pk_bankaccount not in (select pk_bankaccount  From ynt_bankaccount where state =0 and pk_corp = ? and nvl(dr,0)=0 ) )");
				sp.addParam(pk_corp);
			} else if (!StringUtil.isEmpty(accountcode)) {
				sb.append(" and t.pk_bankaccount = ? ");
				sp.addParam(accountcode);
			}
			sb.append(" order by t.tradingdate desc  , accountcode desc ,mony desc  ");

			listVo = (List<BankStatementVO2>) singleObjectBO.executeQuery(sb.toString(), sp,
					new BeanListProcessor(BankStatementVO2.class));
		} else if (type.equals("1")) {//银行回单 ocr来源
			sb.append(
					" select b.myaccountcode as accountcode,b.myaccountname as accountname, t.billstatus ,s.uniquecode as tradecode, ");
			sb.append(" case when t.syje is null then t.zcje else t.syje end as mony ,t.syje,t.zcje, ");
			sb.append(" t.othaccountname,t.othaccountcode,t.tradingdate,");
			sb.append(" t.pk_bankstatement From ynt_bankstatement t ");
			sb.append(
					" left join ynt_bankbilltostatement b on t.pk_bankstatement = b.pk_bankstatement and nvl(b.dr,0)=0");
			sb.append(" left join ynt_interface_invoice s on s.ocr_id = b.pk_image_ocrlibrary ");
			sb.append(
					" where t.billstatus =1 and nvl(t.dr,0)=0 and t.pk_corp=? and t.inperiod >= ?  and t.inperiod <=?  ");
			sp.addParam(pk_corp);
			sp.addParam(bdate);
			sp.addParam(edate);
			if (!StringUtil.isEmpty(izmr) && izmr.equals("Y")) {//银行账号匹配上和没匹配上两种情况,,,,匹配上的取匹配过的账号主键,,没有则取账号
				//b.myaccountcode is null or
				sb.append(
						" and ( t.pk_bankaccount is  null and b.myaccountcode not in (select bankaccount  From ynt_bankaccount where state =0 and pk_corp = ? and nvl(dr,0)=0 ) ");
				sb.append(
						" or ( t.pk_bankaccount is not null and t.pk_bankaccount not in (select pk_bankaccount  From ynt_bankaccount where state =0 and pk_corp = ? and nvl(dr,0)=0 )   )) ");
				sp.addParam(pk_corp);
				sp.addParam(pk_corp);
			} else if (!StringUtil.isEmpty(account)) {
				sb.append("and ( (t.pk_bankaccount is  null and b.myaccountcode=?)  or t.pk_bankaccount  in (select pk_bankaccount  From ynt_bankaccount where state =0 and pk_corp = ? and bankaccount=? and nvl(dr,0)=0 ) )");
				sp.addParam(account);
				sp.addParam(pk_corp);
				sp.addParam(account);
			}
			sb.append(" order by t.tradingdate desc  , accountcode desc ,mony desc ");

			listVo = (List<BankStatementVO2>) singleObjectBO.executeQuery(sb.toString(), sp,
					new BeanListProcessor(BankStatementVO2.class));
		}
		return listVo;

	}

	@Override
	public void updateMatchBankInfo(String pk_bankdzd, String pk_bankhd) throws DZFWarpException {
		BankStatementVO2 bankdzd = (BankStatementVO2) singleObjectBO.queryByPrimaryKey(BankStatementVO2.class,
				pk_bankdzd);// 对账单数据
		BankStatementVO2 bankhd = (BankStatementVO2) singleObjectBO.queryByPrimaryKey(BankStatementVO2.class,
				pk_bankhd);// 回单数据
		String condition = " nvl(dr,0)=0 and pk_bankstatement = '" + pk_bankhd + "'";
		BankBillToStatementVO[] bankbillvo = (BankBillToStatementVO[]) singleObjectBO
				.queryByCondition(BankBillToStatementVO.class, condition, new SQLParameter());

		if (bankdzd == null || bankdzd.getBillstatus() != BankStatementVO2.STATUS_0) {
			throw new BusinessException("银行对账单不存在或状态有误!");
		}
		if (bankhd == null || bankhd.getBillstatus() != BankStatementVO2.STATUS_1) {
			throw new BusinessException("银行回单不存在或状态有误!");
		}
		if (bankbillvo == null || bankbillvo.length == 0) {
			throw new BusinessException("银行回单数据有误!");
		}
		if (!StringUtil.isEmpty(bankdzd.getPk_tzpz_h()) && !StringUtil.isEmpty(bankhd.getPk_tzpz_h())) {
			throw new BusinessException("银行对账单和回单不能同时生成凭证!");
		}
		List<String> filedList = new ArrayList<String>();
		String pk_tzpz = !StringUtil.isEmpty(bankdzd.getPk_tzpz_h()) ? bankdzd.getPk_tzpz_h() : bankhd.getPk_tzpz_h();
		String pzh = !StringUtil.isEmpty(bankdzd.getPzh()) ? bankdzd.getPzh() : bankhd.getPzh();
		bankdzd.setPk_tzpz_h(pk_tzpz);
		bankdzd.setPzh(pzh);
		filedList.add("pk_tzpz_h");
		filedList.add("pzh");
		BankBillToStatementVO billvo = bankbillvo[0];
		bankdzd.setSourcebillid(billvo.getSourcebillid());
		bankdzd.setImgpath(billvo.getImgpath());
		bankdzd.setPk_image_group(billvo.getPk_image_group());
		bankdzd.setPk_image_library(billvo.getPk_image_library());
		bankdzd.setBillstatus(BankStatementVO.STATUS_2);// 绑定关系
		bankdzd.setInperiod(billvo.getPeriod());// 入账期间
		bankdzd.setVdef13(bankhd.getVdef13());
		filedList.add("sourcebillid");
		filedList.add("imgpath");
		filedList.add("pk_image_group");
		filedList.add("billstatus");
		filedList.add("inperiod");// 更新入账期间
		filedList.add("vdef13");
		singleObjectBO.update(bankdzd, filedList.toArray(new String[0]));
		billvo.setPk_bankaccount(bankdzd.getPk_bankaccount());
		billvo.setPk_bankstatement(bankdzd.getPrimaryKey());// 对账单主键
		billvo.setMemo("回单手工匹配上对账单<br>");
		singleObjectBO.update(billvo, new String[] { "pk_bankaccount", "pk_bankstatement", "memo" });
		// 删除回单
		singleObjectBO.deleteObject(bankhd);

	}

	@Override
	public OcrAuxiliaryAccountBVO[] processGoods(String pk_corp, AuxiliaryAccountBVO[] bvos) throws DZFWarpException {
		if (bvos == null || bvos.length == 0)
			return null;
		CorpVO corp = corpService.queryByPk(pk_corp);
		List<OcrAuxiliaryAccountBVO> list = new ArrayList<OcrAuxiliaryAccountBVO>();
		OcrAuxiliaryAccountBVO obvo = null;

		for (AuxiliaryAccountBVO bvo : bvos) {
			obvo = new OcrAuxiliaryAccountBVO();
			String showname = bvo.getName();
			// showname =
			// StringUtil.isEmpty(bvo.getSpec())?showname:showname+"_"+bvo.getSpec();
			showname = StringUtil.isEmpty(bvo.getInvtype()) ? showname : showname + "_" + bvo.getInvtype();
			showname = StringUtil.isEmpty(bvo.getUnit()) ? showname : showname + "_" + bvo.getUnit();
			obvo.setShowname(showname);
			obvo.setDatainfo(bvo);
			if (IcCostStyle.IC_ON.equals(corp.getBbuildic())) {// 启用库存,后台调用的时候。
				// obvo.setSubjname(inmap.get(bvo.getp ));
				InventoryVO invo = query(pk_corp, bvo.getPk_auacount_b());
				obvo.setSubjname(invo == null ? null : invo.getInvclassname());
			}else{
				Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(pk_corp);
				if(!StringUtil.isEmpty(bvo.getKmclassify())){
					obvo.setSubjname(accmap.get(bvo.getKmclassify())==null?null:accmap.get(bvo.getKmclassify()).getAccountname());
				}
			}
			list.add(obvo);
		}
		return list.toArray(new OcrAuxiliaryAccountBVO[0]);
	}

	public InventoryVO query(String pk_corp, String pk_inventory) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_inventory);
		StringBuffer sf = new StringBuffer();
		sf.append(" select  fy.name invclassname from ynt_inventory  ry  ");
		sf.append(" left join ynt_invclassify fy on ry.pk_invclassify = fy.pk_invclassify ");
		sf.append(" where nvl(ry.dr,0) = 0 and ry.pk_corp = ? and ry.pk_inventory = ?");
		List<InventoryVO> ancevos = (List<InventoryVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(InventoryVO.class));
		if (ancevos == null || ancevos.size() == 0)
			return null;
		return ancevos.get(0);
	}

	@Override
	public boolean checkIsMatchCategroy(String categoryKey, Map<String, BillCategoryVO> map) throws DZFWarpException {
		BillCategoryVO categoryvo = null;
		if (map == null)
			map = new HashMap<String, BillCategoryVO>();
		if (map.containsKey(categoryKey)) {// 用个map缓存这次查过的数据避免重复查...
			categoryvo = map.get(categoryKey);
		} else {
			categoryvo = (BillCategoryVO) singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, categoryKey);

			map.put(categoryKey, categoryvo);
		}

		if (categoryvo == null)
			return false;
		String code = categoryvo.getCategorycode();
		// '11%' or s2.categorycode like '101015%' or s2.categorycode like
		// '101110%') ");

		if (code.startsWith("11") || code.startsWith("101015") || code.startsWith("101110")) {

			CorpVO corp = corpService.queryByPk(categoryvo.getPk_corp());
			if (corp.getBbuildic().equals(IcCostStyle.IC_OFF)) {
				String pk_corp = categoryvo.getPk_corp();
				Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(pk_corp);
				String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
				// YntCpaccountVO accountvo =
				// processAccount(categoryvo.getPk_basecategory(),
				// corp.getCorptype(), accmap, newrule);
				YntCpaccountVO accountvo = null;
				if (code.startsWith("11")) {
					accountvo = queryCategorSubj(categoryvo.getPk_category(), new String[] { "11" }, 2, pk_corp, accmap,
							newrule);
				} else {

					accountvo = queryCategorSubj(categoryvo.getPk_category(), new String[] { "101015", "101110" }, 1,
							pk_corp, accmap, newrule);
				}

				if (accountvo != null) {// && !StringUtil.isEmpty(accountvo.getIsfzhs())&& accountvo.getIsfzhs().charAt(5) == '1'
					return true;
				}
			} else {

				return true;
			}
		}
		return false;
	}
	// @Override
	// public void saveGoods(VatGoosInventoryRelationVO svo,String pk_corp,
	// String userid)throws DZFWarpException{
	// singleObjectBO.saveObject("000001", svo);
	// }

	@Override
	public String checkInvtorySubj(InventoryAliasVO[] invectory,InventorySetVO vo, String pk_corp,String userid,boolean ischecked) throws DZFWarpException {
		if(invectory==null || invectory.length==0)
			return "";
		if(StringUtil.isEmpty(pk_corp))
			return "";
		CorpVO cpvo = corpService.queryByPk(pk_corp);
		if(cpvo == null)
			return "";
		//启用总账存货的参与校验
		if(!IcCostStyle.IC_INVTENTORY.equals(cpvo.getBbuildic()))
			return "";
		int chcbjzfs = InventoryConstant.IC_NO_MXHS;//不核算存货
		if(vo != null)
			chcbjzfs = vo.getChcbjzfs();
		if(chcbjzfs == InventoryConstant.IC_NO_MXHS)//不核算存货
			return "";
		StringBuffer sbf = new StringBuffer();
		String str = "";
		YntCpaccountVO[] accountvos =accountService.queryByPk(cpvo.getPk_corp());
		Map<String, YntCpaccountVO> map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(accountvos), new String[]{"accountcode"});
		
		Map<String,YntCpaccountVO> subjmap = new HashMap<String,YntCpaccountVO>();
		List<String> rukuList = new ArrayList<String>();
		List<String> cukuList = new ArrayList<String>();
		for (int i = 0; i < invectory.length; i++) {
			if(!StringUtil.isEmpty(invectory[i].getKmclasscode())&&!subjmap.containsKey(invectory[i].getKmclasscode())){
				rukuList.add(invectory[i].getKmclasscode());
				subjmap.put(invectory[i].getKmclasscode(), map.get(invectory[i].getKmclasscode()));
				
				String code = invectory[i].getKmclasscode().substring(0, 4);
				List<YntCpaccountVO> listcp =getNextAccount(accountvos, code);
				for (YntCpaccountVO yntCpaccountVO : listcp) {
					if(!subjmap.containsKey(yntCpaccountVO.getAccountcode())){
						rukuList.add(yntCpaccountVO.getAccountcode());
						subjmap.put(yntCpaccountVO.getAccountcode(), yntCpaccountVO);
					}
				}
			}
			if(!StringUtil.isEmpty(invectory[i].getChukukmcode())&&!subjmap.containsKey(invectory[i].getChukukmcode())){
				cukuList.add(invectory[i].getChukukmcode());
				subjmap.put(invectory[i].getChukukmcode(), map.get(invectory[i].getChukukmcode()));
			}
		}
		
		
		StringBuffer sf = new StringBuffer();
		Map<String,List<String>> msgmap = new HashMap<String,List<String>>();
		if(chcbjzfs == InventoryConstant.IC_CHDLHS){//大类 1405,1403科目必须为二级科目
			str = "启用存货大类：";
		}else if(chcbjzfs == InventoryConstant.IC_FZMXHS){//明细1405,1403科目必须为一级科目
			str = "启用明细核算：";
		}
		String dlmsg = checkInventoryDoc_DL(pk_corp, chcbjzfs, sf);
		for (String string : rukuList) {
			checkKmDoc(map.get(string), chcbjzfs, accountvos, msgmap, 0);
		}
		for (String string : cukuList) {
			checkKmDoc(map.get(string), chcbjzfs, accountvos, msgmap, 1);
		}
		if(msgmap.isEmpty()&&StringUtil.isEmpty(dlmsg)){
			return "";
		}
		if(!StringUtil.isEmpty(dlmsg)){
			sf.append(dlmsg);
		}
		for(String key : msgmap.keySet()){
			sf.append(""+getPromptName(msgmap.get(key))+"科目，");
			sf.append(key+"。");
		}
		
		
		sbf.append(str);
		sbf.append(sf);

		return sbf.toString();
	
	}
	
	private String checkInventoryDoc_DL(String pk_corp,int hsstyle,StringBuffer sf) throws BusinessException{
		if(InventoryConstant.IC_CHDLHS != hsstyle)//存货大类
			return null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam("000001000000000000000006");
		String condition = " pk_corp = ? and nvl(dr,0) = 0 and pk_auacount_h = ? ";
		AuxiliaryAccountBVO[] bodyvos = (AuxiliaryAccountBVO[])singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class, 
				condition, sp);
		if(bodyvos == null || bodyvos.length == 0)
			return null;
		for(AuxiliaryAccountBVO b : bodyvos){
			if(StringUtil.isEmpty(b.getKmclassify())){
				//sf.append("存货档案的存货分类不能为空。<br>");
				//break;
				return "存货档案的存货分类不能为空。";
			}
		}
		return null;
	}
	
	
	private String checkKmDoc(YntCpaccountVO kmvo,int chcbjzfs,YntCpaccountVO[] vos,Map<String,List<String>> msgmap,int type) throws BusinessException{
		//取库存商品、原材料。
		////大类 1405,1403科目必须为二级科目
		//明细1405,1403科目必须为一级科目
			StringBuffer sb = new StringBuffer();
			String kmcode = kmvo.getAccountcode();
			if(type==0){
				if(chcbjzfs == InventoryConstant.IC_CHDLHS){//大类 1405,1403科目必须为二级科目
					if(kmvo.getAccountlevel()!=null && kmvo.getAccountlevel() == 1){//1级
						if(kmvo.getIsleaf()!=null && kmvo.getIsleaf().booleanValue()){
						sb.append("必须增加二级，二级作为大类启用数量、存货辅助");
						}
					}else if(kmvo.getAccountlevel()!=null && kmvo.getAccountlevel() == 2){//2级
						if(kmvo.getIsleaf()!=null && !kmvo.getIsleaf().booleanValue()){
							sb.append("必须为末级");
						}
						sb.append(getFzhsMsg(kmvo));
					}
				}else if(chcbjzfs == InventoryConstant.IC_FZMXHS){//明细1405,1403科目必须为一级科目
					if(kmvo.getAccountlevel()!=null && kmvo.getAccountlevel() == 1){//1级
						if(kmvo.getIsleaf()!=null && !kmvo.getIsleaf().booleanValue()){
							sb.append("必须为末级");
						}
						sb.append(getFzhsMsg(kmvo));
					}
				}
				
			}else{
				
				sb.append(getFzhsMsg(kmvo));
			}
		if(StringUtil.isEmpty(sb.toString())){
			return sb.toString();
		}
		if(msgmap.containsKey(sb.toString())){
			msgmap.get(sb.toString()).add(kmcode);
		}else{
			List<String> list =new ArrayList<String>();
			list.add(kmcode);
			msgmap.put(sb.toString(),list);
		}
		return sb.toString();
	}
	
	private List<YntCpaccountVO> getNextAccount(YntCpaccountVO[] vos,String code) {
		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();
		for(YntCpaccountVO kmvo : vos){
			if(!StringUtil.isEmpty(kmvo.getAccountcode())){
					if(kmvo.getAccountcode().startsWith(code)){
						list.add(kmvo);
					}
			}
		}
		return list;
	}
	
	private String getFzhsMsg(YntCpaccountVO kmvo){
		StringBuffer sb= new StringBuffer();
		if(kmvo.getIsfzhs() == null || (kmvo.getIsfzhs()!=null && !"1".equals(String.valueOf(kmvo.getIsfzhs().charAt(5))))){
			if(sb.length()>0){
				sb.append("、启用存货辅助");
			}else{
				sb.append("必须启用存货辅助");
			}
		}
		if(kmvo.getIsnum() == null ||(kmvo.getIsnum()!=null && !kmvo.getIsnum().booleanValue())){
			if(sb.length()>0){
				sb.append("、启用数量核算");
			}else{
				sb.append("必须启用数量核算");
			}
		}
		return sb.toString();
	}
	
	
	private String getPromptName(List<String> list){
		if(list == null || list.size() ==0)
			return "";
		StringBuffer sf = new StringBuffer();
		sf.append("[");
		for(int i=0;i<list.size();i++){
			if(i>3){
				sf.append(" ...");
				break;
			}else{
				if(i ==list.size()-1){
					sf.append(list.get(i));
				}else{
					sf.append(list.get(i)+",");
				}
			}
		}
		sf.append("]");
		return sf.toString();
	}

	@Override
	public OcrInvoiceVO[] updateChangeBillCorp(String[] billid, String pk_corp, String period) throws DZFWarpException {

		if(billid==null || billid.length==0){
			throw new BusinessException("请选择需要处理的票据！");
		}
		if(StringUtil.isEmpty(period)){
			throw new BusinessException("期间不能为空！");
		}
		CorpVO corpvo = corpService.queryByPk(pk_corp);

		if (corpvo.getBegindate() == null) {
			throw new BusinessException("当前公司建账日期为空，可能尚未建账，请检查！");
		}
		DZFDate date = new DZFDate(period+"-01");
		if (date.before(corpvo.getBegindate())) {
			throw new BusinessException("所选公司当前期间未建账，请检查！");
		}
		String sql = "nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_invoice", billid) + "";
		OcrInvoiceVO[] ocrInfos = checkZckpAndKc(null,sql);
		//批量查出所有图片数据并存到map里面使用
		//OcrImageLibraryVO  OcrImageGroupVO ImageGroupVO ImageLibraryVO

		 Map<String,ImageInfoVO> infomap = processBillInfo(sql, pk_corp, ocrInfos);

		for (OcrInvoiceVO invvo : ocrInfos) {
			String pk_invoice = invvo.getPk_invoice();
			ImageInfoVO infovo = infomap.get(pk_invoice);



			invvo.setUpdateflag(DZFBoolean.TRUE);
			invvo.setDatasource(ZncsConst.SJLY_1);
			//更新票据表头
			invvo.setPk_corp(pk_corp);
			invvo.setPk_billcategory(null);
			invvo.setPk_category_keyword(null);
			String unitName = corpService.queryByPk(pk_corp).getUnitname();
			//其他单据（除机打发票）跨公司后，将付款方名称变为跨入公司名称（不用去特殊符号）
			if(invvo.getIstate().equals("c其它票据") && !invvo.getInvoicetype().contains("机打发票")){
				//if(StringUtil.isEmpty(invvo.getVpurchname()) && (StringUtil.isEmpty(invvo.getVsalename()) || !invvo.getVsalename().startsWith(unitName))  ){
					invvo.setVpurchname(unitName);
				//}
			}
			OcrInvoiceDetailVO []tailvos = (OcrInvoiceDetailVO[])invvo.getChildren();
			//singleObjectBO.deleteObject(invvo);
			//处理票据表体
			//singleObjectBO.deleteVOArray(tailvos);
			//处理ocr图片信息
			OcrImageLibraryVO ocrLibraryvo =infovo.getOcrImageLbVO();//(OcrImageLibraryVO) singleObjectBO.queryByPrimaryKey(OcrImageLibraryVO.class,invvo.getOcr_id());
			String keycode =!StringUtil.isEmpty(ocrLibraryvo.getKeycode())?ocrLibraryvo.getKeycode():
				ocrLibraryvo.getBatchcode() + ocrLibraryvo.getPk_custcorp() + ocrLibraryvo.getVinvoicecode()+ ocrLibraryvo.getVinvoiceno();

			ocrLibraryvo.setKeycode(keycode);
			ocrLibraryvo.setPk_custcorp(pk_corp);
			boolean isequal = ocrLibraryvo.getPk_corp().equals(ocrLibraryvo.getPk_custcorp());
			if(isequal){
				ocrLibraryvo.setPk_corp(pk_corp);
			}
			//singleObjectBO.deleteObject(ocrLibraryvo);
			//处理orc图片组头
			OcrImageGroupVO ocrgvo =infovo.getOcrImageGVO();//(OcrImageGroupVO) singleObjectBO.queryByPrimaryKey(OcrImageGroupVO.class, ocrLibraryvo.getPk_image_ocrgroup());


			ocrgvo.setPk_selectcorp(pk_corp);
			if(isequal){
				ocrgvo.setPk_corp(pk_corp);
			}
			//处理图片组头
			ImageGroupVO grpvo =infovo.getImageGVO(); //(ImageGroupVO)singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, invvo.getPk_image_group());
			if(grpvo==null) continue;
			grpvo.setPk_corp(pk_corp);
			//singleObjectBO.deleteObject(grpvo);

			// 处理在线发票组
			ImageLibraryVO imglib = infovo.getImageLbVO();//(ImageLibraryVO)singleObjectBO.queryByPrimaryKey(ImageLibraryVO.class, ocrLibraryvo.getCrelationid());

			imglib.setPk_corp(pk_corp);
			//singleObjectBO.deleteObject(imglib);
			//删完后再保存
			//保存图片组头
			grpvo.setPk_image_group(null);
			grpvo = (ImageGroupVO)singleObjectBO.insertVO(pk_corp, grpvo);
			imglib.setPk_image_library(null);
			imglib.setPk_image_group(grpvo.getPk_image_group());
			imglib = (ImageLibraryVO)singleObjectBO.insertVO(pk_corp, imglib);
			//保存ocr图片组
			ocrgvo.setPk_corp(pk_corp);
			ocrgvo.setPk_image_ocrgroup(null);
			ocrgvo = (OcrImageGroupVO)singleObjectBO.insertVO(pk_corp, ocrgvo);
			ocrLibraryvo.setPk_image_ocrgroup(ocrgvo.getPk_image_ocrgroup());
			ocrLibraryvo.setPk_image_ocrlibrary(null);
			ocrLibraryvo.setPk_corp(pk_corp);
			if(ocrLibraryvo.getCrelationid().equals(ocrLibraryvo.getSourceid())){
				ocrLibraryvo.setSourceid(imglib.getPk_image_library());
			}
			ocrLibraryvo.setCrelationid(imglib.getPk_image_library());
			ocrLibraryvo = (OcrImageLibraryVO)singleObjectBO.insertVO(pk_corp, ocrLibraryvo);
			//保存发票
			invvo.setPk_invoice(null);
			invvo.setPk_image_group(grpvo.getPk_image_group());
			invvo.setOcr_id(ocrLibraryvo.getPk_image_ocrlibrary());

			//tailvos
			for (OcrInvoiceDetailVO tailvo : tailvos) {
				tailvo.setPk_corp(pk_corp);
				tailvo.setPk_billcategory(null);
				tailvo.setPk_category_keyword(null);
				tailvo.setPk_invoice_detail(null);
				//tailvo.setPk_invoice(invvo.getPk_invoice());
			}
			//singleObjectBO.insertVOArr(pk_corp, tailvos);
			invvo.setChildren(tailvos);
			invvo = (OcrInvoiceVO)singleObjectBO.insertVO(pk_corp, invvo);
			billcreate.createBill(invvo, grpvo, imglib, null);
			//重新分类
		}
		if(ocrInfos.length<=20){
			invCategory(pk_corp, period, Arrays.asList(ocrInfos));
		}
		return ocrInfos;
	}
	//	String sql = " nvl(dr,0)=0 and pk_invoice " + new SqlInUtil(billid).getInSql("invoice", "000001", "000001");
	private Map<String,ImageInfoVO> processBillInfo(String conditon, String pk_corp, OcrInvoiceVO invvos[]){
		//ImageGroupVO ImageLibraryVO OcrImageGroupVO OcrImageLibraryVO 
		//先查出所有数据,,然后删掉,在组装数据
		//sqlbuff.toString(), param, new BeanListProcessor(VATSaleInvoiceBVO2.class)
		StringBuffer buff1 = new StringBuffer();
		SQLParameter params = new SQLParameter();
		buff1.append("select* From ynt_image_group where  pk_image_group in ( ");
		buff1.append("select pk_image_group From ynt_interface_invoice  where ").append(conditon);
		buff1.append(")");
		List<ImageGroupVO> igvos = (List<ImageGroupVO>)singleObjectBO.executeQuery(buff1.toString(), params, new BeanListProcessor(ImageGroupVO.class));
		StringBuffer buff2 = new StringBuffer();
		buff2.append("select* From ynt_image_library where pk_image_library in(select crelationid From ynt_image_ocrlibrary  ");
		buff2.append(" where pk_image_ocrlibrary in( ");
		buff2.append("select ocr_id From ynt_interface_invoice  where ").append(conditon);
		buff2.append(" ))");
		List<ImageLibraryVO> ilbvos= (List<ImageLibraryVO>)singleObjectBO.executeQuery(buff2.toString(), params, new BeanListProcessor(ImageLibraryVO.class));
		
		StringBuffer buff3 = new StringBuffer();
		buff3.append("select* From ynt_image_ocrlibrary where pk_image_ocrlibrary in(");
		buff3.append("select ocr_id From ynt_interface_invoice  where ").append(conditon);
		buff3.append(" )");
		List<OcrImageLibraryVO> ocrilbvos= (List<OcrImageLibraryVO>)singleObjectBO.executeQuery(buff3.toString(), params, new BeanListProcessor(OcrImageLibraryVO.class));
		StringBuffer buff4 = new StringBuffer();
		buff4.append("select* From ynt_image_ocrgroup where pk_image_ocrgroup in(");
		buff4.append(" select pk_image_ocrgroup From ynt_image_ocrlibrary where pk_image_ocrlibrary in(");
		buff4.append("select ocr_id From ynt_interface_invoice  where ").append(conditon);
		buff4.append(" ))");
		List<OcrImageGroupVO> ocrgvos= (List<OcrImageGroupVO>)singleObjectBO.executeQuery(buff4.toString(), params, new BeanListProcessor(OcrImageGroupVO.class));
		
		singleObjectBO.executeUpdate(buff1.toString().replaceFirst("select\\*", "delete "), params);
		singleObjectBO.executeUpdate(buff2.toString().replaceFirst("select\\*", "delete "), params);
		singleObjectBO.executeUpdate(buff3.toString().replaceFirst("select\\*", "delete "), params);
		singleObjectBO.executeUpdate(buff4.toString().replaceFirst("select\\*", "delete "), params);
		StringBuffer buff5 = new StringBuffer();
		buff5.append("delete from ynt_interface_invoice where ").append(conditon);
		singleObjectBO.executeUpdate(buff5.toString(), params);
		StringBuffer buff6 = new StringBuffer();
		buff6.append("delete from ynt_interface_invoice_detail where ").append(conditon);
		singleObjectBO.executeUpdate(buff6.toString(), params);
		
		Map<String, ImageGroupVO> groupmap = DZfcommonTools.hashlizeObjectByPk(igvos,new String[] { "pk_image_group" });
		Map<String, ImageLibraryVO> librirymap = DZfcommonTools.hashlizeObjectByPk(ilbvos,new String[] { "pk_image_group" });
		Map<String, OcrImageLibraryVO>  ocrlibrirymap= DZfcommonTools.hashlizeObjectByPk(ocrilbvos,new String[] { "pk_image_ocrlibrary" });
		Map<String, OcrImageGroupVO>  ocrgroupmap= DZfcommonTools.hashlizeObjectByPk(ocrgvos,new String[] { "pk_image_ocrgroup" });
		Map<String,ImageInfoVO>   imageInfoMap = new HashMap<String,ImageInfoVO>();
		ImageInfoVO infovo ;
		for (int i = 0; i < invvos.length; i++) {
			OcrInvoiceVO invvo = invvos[i];
			infovo = new ImageInfoVO();
			infovo.setImageGVO(groupmap.get(invvo.getPk_image_group()));
			infovo.setImageLbVO(librirymap.get(invvo.getPk_image_group()));
			OcrImageLibraryVO lbvo = ocrlibrirymap.get(invvo.getOcr_id());
			infovo.setOcrImageLbVO(ocrlibrirymap.get(invvo.getOcr_id()));
			infovo.setOcrImageGVO(ocrgroupmap.get(lbvo.getPk_image_ocrgroup()));
			imageInfoMap.put(invvo.getPk_invoice(), infovo);
		}
		
		//作废其他相关票据
		String updatesql = conditon.replace("pk_invoice", "vdef13");
		invalidOtherBill(pk_corp, new VATInComInvoiceVO2().getTableName(), updatesql, params);
		invalidOtherBill(pk_corp, new VATSaleInvoiceVO2().getTableName(), updatesql, params);
		invalidOtherBill(pk_corp, new BankStatementVO2().getTableName(), updatesql, params);
		
		return imageInfoMap;
	}
	@Override
	public Map<String,List<VATSaleInvoiceBVO2>> querySaleInvoiceInfo(String pk_corp,List<String> plist,int ic_rule)throws DZFWarpException{
		if(plist==null || plist.size() ==0) return null;//期间不能为空
		Map<String,String> map = new HashMap<>();
		
		List<String> plist2 = new ArrayList<>();
		for (int i = 0; i < plist.size(); i++) {
			String period = plist.get(i);
			if(map.containsKey(period)){
			}else{
				plist2.add(period);
				map.put(period, period);
			}
		}
		StringBuffer sqlbuff = new StringBuffer();
		SQLParameter param =  new SQLParameter();
		sqlbuff.append(" select s1.*,s2.period as tempvalue From ynt_vatsaleinvoice_b s1 left join ynt_vatsaleinvoice s2 on s1.pk_vatsaleinvoice = s2.pk_vatsaleinvoice ");
		sqlbuff.append("  where nvl(s1.dr,0)=0 and nvl(s2.dr,0)=0 and  s2.pk_corp =?   ");
		sqlbuff.append(" and  ").append(SqlUtil.buildSqlForIn("s2.period", plist2.toArray(new String[0])));
		param.addParam(pk_corp);
		List<VATSaleInvoiceBVO2> sallist = (List<VATSaleInvoiceBVO2>) singleObjectBO.executeQuery(sqlbuff.toString(), param, new BeanListProcessor(VATSaleInvoiceBVO2.class));
		
		if(sallist==null||sallist.size()==0)return null;
		Map<String,List<VATSaleInvoiceBVO2>> salemap = new HashMap<String,List<VATSaleInvoiceBVO2>>();
		for (VATSaleInvoiceBVO2 bvo : sallist) {
			String name = OcrUtil.execInvname(bvo.getBspmc());
			String key ="";
			if(ic_rule == InventoryConstant.IC_RULE_1 ){//存货名称+计量单位+期间
				key = name+null+bvo.getMeasurename()+bvo.getTempvalue();
			}else{//存货名称+规格（型号）+计量单位
				key = name+bvo.getInvspec()+bvo.getMeasurename()+bvo.getTempvalue();
			}
			if(salemap.containsKey(key)){
				salemap.get(key).add(bvo);
			}else{
				List<VATSaleInvoiceBVO2> list = new ArrayList<VATSaleInvoiceBVO2>();
				list.add(bvo);
				salemap.put(key, list);
			}
		}
		return salemap;
	}


	@Override
	public  InventorySaleInfoVO querySaleBillInfo(List<String> plist, int ic_rule, InventorySaleInfoVO saleinfo,Map<String,List<VATSaleInvoiceBVO2>> salemap,int numPrecision,int pricePrecision)
			throws DZFWarpException {
		if(salemap ==null || salemap.isEmpty()) return null;
		if(plist==null || plist.size() ==0) return null;//期间不能为空
		Map<String,String> map = new HashMap<>();
		
		List<InventorySaleInfoVO> list = new ArrayList<>();
		for (int i = 0; i < plist.size(); i++) {
			String period = plist.get(i);
			if(map.containsKey(period)){
			}else{
				InventorySaleInfoVO vo = new InventorySaleInfoVO();
				vo.setPeriod(period);
				list.add(vo);
				map.put(period, period);
			}
		}
		
		Collections.sort(list, new Comparator<InventorySaleInfoVO>() {
			@Override
			public int compare(InventorySaleInfoVO o1, InventorySaleInfoVO o2) {
				DZFDate date1 = new DZFDate(o1.getPeriod()+"-01");
				DZFDate date2 = new DZFDate(o2.getPeriod()+"-01");
				int i = date2.compareTo(date1);
				return i;
			}
		});
		
		for (int i = 0; i < list.size(); i++) {
			InventorySaleInfoVO infovo= list.get(i);
			String key ="";
			if(ic_rule == InventoryConstant.IC_RULE_1 ){//存货名称+计量单位+期间
				key = saleinfo.getName()+null+saleinfo.getUnit()+infovo.getPeriod();
			}else{//存货名称+规格（型号）+计量单位
				key = saleinfo.getName()+saleinfo.getSpec()+saleinfo.getUnit()+infovo.getPeriod();
			}
			
			InventorySaleInfoVO vo = querySaleInfo(salemap.get(key),saleinfo, ic_rule,numPrecision,pricePrecision );
			if(vo!=null) return vo;
		}
//		
		
		return null;
	}
	
	private InventorySaleInfoVO querySaleInfo(List<VATSaleInvoiceBVO2> sallist,InventorySaleInfoVO saleinfo,int ic_rule,int numPrecision,int pricePrecision){//String peroid,int ic_rule, InventorySaleInfoVO saleinfo
		
		
		
		if(sallist==null ||sallist.size()==0) return null;
		
		List<VATSaleInvoiceBVO2> tlist = new ArrayList<>();
		for (int i = 0; i < sallist.size(); i++) {
			VATSaleInvoiceBVO2 bvo = sallist.get(i);
			if(!StringUtil.isEmpty(saleinfo.getName())&&saleinfo.getName().equals(OcrUtil.execInvname(bvo.getBspmc()))){
				tlist.add(bvo);
			}
		}
		if(tlist.size()==0) return null;
		DZFDouble total = new DZFDouble();//数量合计
		DZFDouble totalPrice =  new DZFDouble(); 
		DZFDouble price =  new DZFDouble(); 
		
		//设置数量 ,单价精度
		//int numPrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(saleinfo.getPk_corp(), "dzf009"));
		//int pricePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(saleinfo.getPk_corp(), "dzf010"));
		
		for (VATSaleInvoiceBVO2 bsalevo : tlist) {
			total = bsalevo.getBnum()!=null?total.add(bsalevo.getBnum()):total;
			totalPrice =bsalevo.getBhjje()!=null? totalPrice.add(bsalevo.getBhjje()):total;
		}
		
		total.setScale(numPrecision, DZFDouble.ROUND_HALF_UP);
		price = totalPrice.div(total).setScale(pricePrecision, DZFDouble.ROUND_HALF_UP);
		
		InventorySaleInfoVO infovo = new InventorySaleInfoVO();
		infovo.setSaleNumber(total);
		infovo.setSalePrice(price);
		return infovo;
	}

	@Override
	public DutyPayVO []queryDutyTolalInfo(String[] pkcorps, String period,int page,int rows) throws DZFWarpException {



		String sql = getDutyQuerySql(pkcorps,period);

		 List<DutyPayVO> dutilist =  (List<DutyPayVO>)singleObjectBO.executeQuery(sql,new SQLParameter(),new BeanListProcessor(DutyPayVO.class));
		 if(dutilist==null||dutilist.isEmpty()){
		 	return null;
		 }


		Map<String,List<DutyPayVO>> dutymap = new HashMap<String,List<DutyPayVO>>();
		List<DutyPayVO> dlist;
		DutyPayVO dpvo ;
		for (DutyPayVO dvo:dutilist) {
			dpvo  =(DutyPayVO)dvo.clone();
			dpvo.setCorpname(SecretCodeUtils.deCode(dvo.getCorpname()));
			String key = dvo.getInvname()+"_"+dvo.getPk_corp()+"_"+dvo.getPeriod();
			if(dutymap.get(key)==null){
				dlist = new ArrayList<DutyPayVO>();

				dlist.add(dpvo);
				dutymap.put(key,dlist);
			}else{
				dlist = dutymap.get(key);
				dlist.add(dpvo);
			}

		}
		//统计数
		Set<String> set = new HashSet<String>();
		List<DutyPayVO> dtlist = new ArrayList<DutyPayVO>();
		for  (DutyPayVO dvo:dutilist) {
			String key = dvo.getInvname()+"_"+dvo.getPk_corp()+"_"+dvo.getPeriod();
			if(set.contains(key)) continue;
			set.add(key);
			dpvo = new DutyPayVO();
			dpvo.setItemmny(new DZFDouble(0));
			dpvo.setCorpname(SecretCodeUtils.deCode(dvo.getCorpname()));
			dpvo.setInvname(dvo.getInvname());
			dpvo.setPeriod(dvo.getPeriod());
			for (DutyPayVO dvo_2:dutilist) {
				if(dvo.getPeriod().equals(dvo_2.getPeriod())  && dvo.getPk_corp().equals(dvo_2.getPk_corp())  && dvo.getInvname().equals(dvo_2.getInvname()) ){
					dpvo.setItemmny( dpvo.getItemmny().add(dvo_2.getItemmny()));
				}
			}
			dpvo.setImageInfo(dutymap.get(key));
			dtlist.add(dpvo);
		}

		return getPageDutydata(dtlist.toArray(new DutyPayVO[0]),page,rows);
	}
	private String getDutyQuerySql(String []pkcorps,String period){
		StringBuffer buff = new StringBuffer();
		String corpsql = "   and  " + SqlUtil.buildSqlForIn("c.pk_corp", pkcorps);
		//buff.append(" insert into DZF_TMP_DUTY(invname,corpname,itemmny,period,pk_corp,sourceid,imgname)  ");
		buff.append(" select d.invname as invname ,e.unitname as corpname,d.itemmny as itemmny,c.period as period ,d.pk_corp as pk_corp,f.crelationid as sourceid,f.imgname ");
		buff.append(" From ynt_interface_invoice_detail d ");
		buff.append(" left join ynt_interface_invoice c on c.pk_invoice =d.pk_invoice  ");
		buff.append(" left join ynt_image_ocrlibrary f on f.pk_image_ocrlibrary = c.ocr_id ");
		buff.append(" left join bd_corp e on d.pk_corp = e.pk_corp ");
		buff.append(" left join ynt_billcategory b1 on b1.pk_category = c.pk_billcategory ");
		buff.append(" left join ynt_image_group g1 on g1.pk_image_group = c.pk_image_group ");
		buff.append(" where c.invoicetype = 'b税收完税证明' and invname is not null  and b1.categorycode!='18' and c.pk_billcategory is not null ");
		buff.append(" and g1.istate!='205' and nvl(d.dr,0)=0 and  nvl(c.dr,0)=0   and nvl(g1.dr,0)=0  ");
		buff.append(" and c.period ='"+period+"' ");
		buff.append(corpsql);
		buff.append(" order by d.pk_corp,d.invname ");
		return buff.toString();
	}


	public String[] queryCorpByName(String unitnames[]) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuffer conditionbuff = new StringBuffer();
		try {
			//conditionbuff.append(" and nvl(dr,0) = 0   and (");
			for (String unitname:unitnames) {
				if(conditionbuff.toString().isEmpty()){
					conditionbuff.append(" ( unitname = ? ");
				}else{
					conditionbuff.append(" or unitname = ? ");
				}
				sp.addParam(SecretCodeUtils.enCode(unitname));
			}
			conditionbuff.append(" ) and nvl(dr,0) = 0");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CorpVO[] vos = (CorpVO[])singleObjectBO.queryByCondition(CorpVO.class, conditionbuff.toString(), sp);
		List<String> list = new ArrayList<String>();
		if(vos!=null && vos.length>0){
			for (CorpVO vo:vos) {
				list.add(vo.getPk_corp());
			}
		}
		return list.toArray(new String[0]);
	}

	private DutyPayVO []getPageDutydata(DutyPayVO[]datas,int page,int rows){
		if(datas==null||datas.length==0)return  datas;
		if(rows < 0) return datas;

		List<DutyPayVO> list = new ArrayList<DutyPayVO>();
		int start = (page - 1) * rows;
		for (int i = start; i < page * rows && i < datas.length; i++) {
			list.add(datas[i]);
		}
		return list.toArray(new DutyPayVO[0]);
	}
//	private String marchCorp(CorpVO corpvo[], String corpName){
//		for (CorpVO corpVO:corpvo) {
//			String cname = CodeUtils1.deCode(corpVO.getUnitname());
//			if(!StringUtil.isEmpty(corpName) && corpName.length()>2&&OcrUtil.isSameCompany(cname,corpName)){
//
//				return corpVO.getPk_corp();
//			}
//		}
//		return null;
//	}
	
}
