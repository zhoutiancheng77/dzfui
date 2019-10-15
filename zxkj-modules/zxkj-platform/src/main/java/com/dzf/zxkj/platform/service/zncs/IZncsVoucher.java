package com.dzf.zxkj.platform.service.zncs;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InventoryAliasVO;
import com.dzf.zxkj.platform.model.icset.InventorySetVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.AccsetKeywordBVO2;
import com.dzf.zxkj.platform.model.zncs.AccsetVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IZncsVoucher {
	/**
	 * 职能做账
	 * @param pk_category 分类树
	 * @param pk_bills 票据
	 * @param period 期间
	 * @param pk_corp 公司
	 * @return
	 * @throws DZFWarpException
	 */
	public List<TzpzHVO> processGeneralTzpzVOs(String pk_category, String pk_bills, String period, String pk_corp, String pk_parent, Map<String, Map<String, Object>> checkMsgMap, String pk_user)throws DZFWarpException;

	/**
	 * 职能做账凭证保存
	 * @param tzpzHVOs
	 * @throws DZFWarpException
	 */
	public void  saveVouchersBefore(List<TzpzHVO> tzpzHVOs)throws DZFWarpException;
	public void  saveVoucherBefore(TzpzHVO tzpzHVOs)throws DZFWarpException;
	public void  deleteVoucherBefore(TzpzHVO tzpzHVOs)throws DZFWarpException;
	public void  deleteVoucherAfter(TzpzHVO tzpzHVOs)throws DZFWarpException;
	public void  saveVoucherAfter(TzpzHVO tzpzHVOs)throws DZFWarpException;
	/**
	 * 手动做账
	 * @param pk_category
	 * @param pk_bills
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, Object> generalHandTzpzVOs(String pk_category, String pk_bills, String period, String pk_corp, String pk_parent)throws DZFWarpException;

	/**
	 * 手动凭证保存
	 * @param tzpzHVOs
	 * @throws DZFWarpException
	 */
	public void  saveHandVouchers(List<TzpzHVO> tzpzHVOs)throws DZFWarpException;

	public List<OcrInvoiceVO> queryOcrInvoiceVOs(String pk_category, String pk_bills, String period, String pk_corp, String pk_parent, DZFBoolean isErrorVoucher)throws DZFWarpException;

	/**
	 * 进项、销项、银行对账单转invoice后调用，返回凭证
	 * @param invoiceList
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<TzpzHVO> processGeneralTzpzVOsByInvoice(List<OcrInvoiceVO> invoiceList, String period, String pk_corp, String pk_user, Map<String, Map<String, Object>> checkMsgMap
            , List<List<Object[]>> levelList, Map<String, Object[]> categoryMap, Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap, Set<String> zyFzhsList
            , Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap, InventorySetVO inventorySetVO, CorpVO corp, Map<String, InventoryAliasVO> fzhsBMMap
            , List<Object> paramList, Map<String, BdCurrencyVO> currMap, Map<String, Object[]> rateMap, Map<String, String> bankAccountMap, Map<String, YntCpaccountVO> accountMap
            , Map<String, AuxiliaryAccountBVO> assistMap, Map<String, List<AccsetVO>> accsetMap, Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map, Map<String, String> jituanSubMap, YntCpaccountVO[] accVOs
            , String tradeCode, String newrule, List<AuxiliaryAccountBVO> chFzhsBodyVOs) throws DZFWarpException;

	/**
	 * 保存凭证是否调用职能财税处理路径
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFBoolean getVoucherFlag()throws DZFWarpException;

	/**
	 * 返回分类全名称
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, String> queryCategoryFullName(List<String> pk_categoryList, String period, String pk_corp)throws DZFWarpException;

	public Map<String, Object> initVoucherParam(CorpVO corp, String period, boolean isBank)throws DZFWarpException;
}
