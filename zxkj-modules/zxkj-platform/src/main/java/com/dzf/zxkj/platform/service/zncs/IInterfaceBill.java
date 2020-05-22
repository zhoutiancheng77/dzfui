package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.image.OcrImageLibraryVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceDetailVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.pjgl.VatGoosInventoryRelationVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.*;

import java.util.List;
import java.util.Map;

public interface IInterfaceBill {
	/**
	 * 查询票据详情
	 * @param billid
	 * @throws DZFWarpException
	 */
	public BillInfoVO queryBillInfo(String billid)throws DZFWarpException;
	public List<BillInfoVO> queryBillInfos(String billids, String period, String pk_corp)throws DZFWarpException;
	/**
	 * 作废票据
	 * @param billid
	 * @throws DZFWarpException
	 */
	public void updateInvalidBill(String billid[], String pk_corp)throws DZFWarpException;
	
	
	/**
	 * 按照分组批量作废票据
	 * @param paramVO
	 * @throws DZFWarpException
	 */
	public void updateInvalidBatchBill(BillcategoryQueryVO paramVO)throws DZFWarpException;
	
	/**
	 * 查询票据图片
	 * @param ids
	 * @return
	 * @throws DZFWarpException
	 */
	public OcrImageLibraryVO[] queryImages(String[] ids, String corpid, String period, String pk_category)throws DZFWarpException;
	/**
	 * 更改期间
	 * @param billid
	 * @param period
	 * @throws DZFWarpException
	 */
	public void updateChangeBillPeroid(String billid[], String period)throws DZFWarpException;
	/**
	 * 查询作废票据
	 * @param corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BillInfoVO> queryInvalidBill(String corp, String period)throws DZFWarpException;
	
	/**
	 * 修改保存数据信息
	 * @param headvo
	 * @param bodyvos
	 * @throws DZFWarpException
	 */
	public void updateInvoiceInfo(OcrInvoiceVO headvo, OcrInvoiceDetailVO bodyvos[]) throws DZFWarpException;
	
	/**
	 * 按照分组批量更改票据期间
	 * @param paramVO
	 * @throws DZFWarpException
	 */
	public void updateChangeBatchPeorid(BillcategoryQueryVO paramVO)throws DZFWarpException;
	/**
	 * 进销项存货匹配
	 * @param pk_corp
	 * @param ocrlist
	 * @param invsetvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<InventoryAliasVO> matchInventoryData(String pk_corp, List<OcrInvoiceVO> ocrlist, InventorySetVO invsetvo)throws DZFWarpException;
	
	/**
	 * 查询进销项匹配的票据
	 * @param corpid
	 * @param period
	 * @param billid
	 * @param category
	 * @param type //1进项，2销项类，3全部
	 * @return
	 * @throws DZFWarpException
	 */
	public List<OcrInvoiceVO> queryMatchInvoice(String corpid, String period, String[] billid, String category, int type)throws DZFWarpException;
	/**
	 * 查询销项匹配数据
	 * @param saleList
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<VatGoosInventoryRelationVO> getGoodsInvenRela(List<OcrInvoiceVO> saleList, String pk_corp) throws DZFWarpException;
	
	/**
	 * 更新启用存货的存货
	 * @param goodsvois
	 * @param cuserid
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	public void updateGoodsInvenRela(VatGoosInventoryRelationVO[] goodsvois, String cuserid, String pk_corp) throws DZFWarpException;

	/**
	 * 更据状态查询票据图片0:对账单,1:回单
	 * @param corp
	 * @param period
	 * @param state
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BillInfoVO> queryBillByState(String corp, String period, Integer state) throws DZFWarpException;
	
     /**
      * 查询未对账银行对账单数据0;//银行对账单导入、录入  未绑定1;//回单回传， 未绑定
      * @param pk_corp
      * @param bperiod
      * @param account
      * @param type
      * @return
      * @throws DZFWarpException
      */
	public List<BankStatementVO2> queryBankInfo(String pk_corp, String bperiod, String eperiod, String account, String type, String accountcode, String izmr)throws DZFWarpException;
	
    /**
     * 绑定银行对账单
     * @param pk_bankdzd
     * @param pk_bankhd
     * @return
     * @throws DZFWarpException
     */
	public void updateMatchBankInfo(String pk_bankdzd, String pk_bankhd)throws DZFWarpException;
	
	public OcrAuxiliaryAccountBVO[] processGoods(String pk_corp, AuxiliaryAccountBVO bvos[])throws DZFWarpException;
	/**
	 * 票据重传
	 * @param billid
	 * @param paramVO
	 * @throws DZFWarpException
	 */
	public void updateRetransBill(String billid[], BillcategoryQueryVO paramVO)throws DZFWarpException;
	/**
	 * 检查是否需要匹配的类别
	 * @param categoryKey
	 * @return
	 * @throws DZFWarpException
	 */
	public boolean checkIsMatchCategroy(String categoryKey, Map<String, BillCategoryVO> map)throws DZFWarpException;
	
	public InventoryVO matchInvtoryIC(VatGoosInventoryRelationVO gvo, String pk_corp, String cuserid, String newrule, Map<String, YntCpaccountVO> accmap, YntCpaccountVO[] accounts)throws DZFWarpException;


	public YntCpaccountVO queryCategorSubj(Map<String, YntCpaccountVO> catchmap,String pk_billcagegory, String catecode[], int jici, String pk_corp, Map<String, YntCpaccountVO> accmap, String newrule,CorpVO corp) throws DZFWarpException;

	/**
	 * 校验存货科目
	 * @param invectory
	 * @param vo
	 * @param pk_corp
	 * @param userid
	 * @param ischecked
	 * @return
	 * @throws DZFWarpException
	 */
	public String checkInvtorySubj(InventoryAliasVO[] invectory, InventorySetVO vo, String pk_corp, String userid, boolean ischecked) throws DZFWarpException;


	/**
	 * 票据跨公司
	 * @param billid
	 * @param period
	 * @throws DZFWarpException
	 */
	public OcrInvoiceVO[]  updateChangeBillCorp(String billid[], String pk_corp, String period)throws DZFWarpException;
	
	/**
	 * 根据销项发票查询存货销售信息
	 * @param plist
	 * @param ic_rule
	 * @param saleinfo
	 * @return
	 * @throws DZFWarpException
	 */
	public InventorySaleInfoVO querySaleBillInfo(List<String> plist, int ic_rule, InventorySaleInfoVO saleinfo, Map<String, List<VATSaleInvoiceBVO2>> salemap, int numPrecision, int pricePrecision)throws DZFWarpException;
	/**
	 * 查询销项发票表体数据
	 * @param pk_corp
	 * @param plist
	 * @param ic_rule
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String,List<VATSaleInvoiceBVO2>> querySaleInvoiceInfo(String pk_corp, List<String> plist, int ic_rule)throws DZFWarpException;

	/**
	 * 统计完税统计信息
	 * @throws DZFWarpException
	 */
	public DutyPayVO []queryDutyTolalInfo(String pkcorps[],String period,String izdf,int page,int rows)throws DZFWarpException;

	/**
	 * 根据公司名称查出公司
	 * @param unitnames
	 * @return
	 * @throws DZFWarpException
	 */
	public String[] queryCorpByName(String unitnames[]) throws DZFWarpException;

}
