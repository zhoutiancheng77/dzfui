package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.pjgl.InvoiceParamVO;
import com.dzf.zxkj.platform.model.pjgl.TicketNssbhVO;
import com.dzf.zxkj.platform.model.pjgl.VatGoosInventoryRelationVO;
import com.dzf.zxkj.platform.model.pjgl.VatInvoiceSetVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.model.zncs.AccsetKeywordBVO2;
import com.dzf.zxkj.platform.model.zncs.AccsetVO;
import com.dzf.zxkj.platform.model.zncs.BillCategoryVO;
import com.dzf.zxkj.platform.model.zncs.VATSaleInvoiceVO2;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IVATSaleInvoice2Service {

	/**
	 * 查询
	 * @param paramvo
	 * @param sort
	 * @param order
	 * @return
	 * @throws DZFWarpException
	 */
	public List<VATSaleInvoiceVO2> quyerByPkcorp(InvoiceParamVO paramvo, String sort, String order) throws DZFWarpException;

	public VATSaleInvoiceVO2 queryByID(String pk) throws DZFWarpException;
	/**
	 * 更新操作
	 * @throws DZFWarpException
	 */
//	public void updateVOArr(DZFBoolean isAddNew, String pk_corp, String cuserid, String sort, String order, List<VATSaleInvoiceVO2> list) throws DZFWarpException;
	
	public VATSaleInvoiceVO2[] updateVOArr(String pk_corp, Map<String, VATSaleInvoiceVO2[]> map) throws DZFWarpException;
	
	/**
	 * 删除
	 * @param vo
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	void delete(VATSaleInvoiceVO2 vo, String pk_corp) throws DZFWarpException;
	
	/**
	 * 导入
	 * @param file
	 * @param filename
	 * @param pk_corp
	 * @param fileType
	 * @param userid
	 * @throws DZFWarpException
	 */
	public void saveImp(MultipartFile file, String filename, VATSaleInvoiceVO2 paramvo, String pk_corp, String fileType, String userid, StringBuffer msg) throws DZFWarpException;
	
	/**
	 * 生成凭证
	 * @throws DZFWarpException
	 */
	public void createPZ(VATSaleInvoiceVO2 vo, String pk_corp, String userid, String period, VatInvoiceSetVO setvo, DZFBoolean lwflag, boolean accway, boolean isT
            , List<List<Object[]>> levelList, Map<String, Object[]> categoryMap, Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap, Set<String> zyFzhsList
            , Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap, InventorySetVO inventorySetVO, CorpVO corp, Map<String, InventoryAliasVO> fzhsBMMap
            , List<Object> paramList, Map<String, BdCurrencyVO> currMap, Map<String, Object[]> rateMap, Map<String, String> bankAccountMap, Map<String, YntCpaccountVO> accountMap
            , Map<String, AuxiliaryAccountBVO> assistMap, Map<String, List<AccsetVO>> accsetMap, Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map, Map<String, String> jituanSubMap, YntCpaccountVO[] accVOs
            , String tradeCode, String newrule, List<AuxiliaryAccountBVO> chFzhsBodyVOs) throws DZFWarpException;
	
//	public Map<String, YntCpaccountVO> construcTemKm(CorpVO corpvo) throws DZFWarpException;
	
	public List<VATSaleInvoiceVO2> constructVatSale(VATSaleInvoiceVO2[] vos, String pk_corp) throws DZFWarpException;
	/**
	 * 合并生单
	 * @param list
	 * @param pk_corp
	 * @param userid
	 * @param isT
	 * @throws DZFWarpException
	 */
	public void saveCombinePZ(List<VATSaleInvoiceVO2> list, String pk_corp, String userid, String period, DZFBoolean lwflag, VatInvoiceSetVO setvo, boolean accway, boolean isT, List<List<Object[]>> levelList, Map<String, Object[]> categoryMap, Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap, Set<String> zyFzhsList
            , Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap, InventorySetVO inventorySetVO, CorpVO corp, Map<String, InventoryAliasVO> fzhsBMMap
            , List<Object> paramList, Map<String, BdCurrencyVO> currMap, Map<String, Object[]> rateMap, Map<String, String> bankAccountMap, Map<String, YntCpaccountVO> accountMap
            , Map<String, AuxiliaryAccountBVO> assistMap, Map<String, List<AccsetVO>> accsetMap, Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map, Map<String, String> jituanSubMap, YntCpaccountVO[] accVOs
            , String tradeCode, String newrule, List<AuxiliaryAccountBVO> chFzhsBodyVOs) throws DZFWarpException;
	
	/*public List<String> getBusiTypes(String pk_corp) throws DZFWarpException;*/
	
	/**
	 * 设置业务类型
	 * @return
	 * @throws DZFWarpException
	 */
	/*public String saveBusiType(VATSaleInvoiceVO2[] vos, String busiid, String businame, String selvalue, String userid, String pk_corp) throws DZFWarpException;*/
	
	public CorpVO chooseTicketWay(String pk_corp) throws DZFWarpException;
	
	/**
	 * 设置入账期间
	 * @param vos
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public String saveBusiPeriod(VATSaleInvoiceVO2[] vos, String pk_corp, String period) throws DZFWarpException;
	
	/**
     * 构造凭证vo
     * @param vos
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
	public TzpzHVO getTzpzHVOByID(VATSaleInvoiceVO2[] vos, String pk_corp, String userid, VatInvoiceSetVO setvo, boolean accway) throws DZFWarpException;
	
	/**
	 * 合并生单前校验
	 * @param vos
	 * @throws DZFWarpException
	 */
	public void checkBeforeCombine(VATSaleInvoiceVO2[] vos) throws DZFWarpException;
	
	public List<String> getCustNames(String pk_corp, VATSaleInvoiceVO2[] vos) throws DZFWarpException;
	
	public Map<String, VATSaleInvoiceVO2> saveCft(String pk_corp, String userid, String ccrecode, String fptqm, VATSaleInvoiceVO2 paramvo, StringBuffer msg) throws DZFWarpException;
	
	public Map<String, VATSaleInvoiceVO2> saveKp(String pk_corp, String userid, VATSaleInvoiceVO2 paramvo, TicketNssbhVO nssbvo) throws DZFWarpException;
	
	/*public void scanMatchBusiName(VATSaleInvoiceVO2 salevo, Map<String, DcModelHVO> dcmap) throws DZFWarpException;*/

	/**
	 * 根据pks查询vo记录
	 * @param pks
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<VATSaleInvoiceVO2> queryByPks(String[] pks, String pk_corp) throws DZFWarpException;

	public List<TaxitemVO> queryTaxItems(String pk_corp) throws DZFWarpException;
	/*
	 * 生成出库单
	 */
	public IntradeHVO createIC(VATSaleInvoiceVO2 vo, YntCpaccountVO[] accounts, CorpVO corpvo, String userid, List<List<Object[]>> levelList, Map<String, Object[]> categoryMap, Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap, Set<String> zyFzhsList
            , Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap, InventorySetVO inventorySetVO, CorpVO corp, Map<String, InventoryAliasVO> fzhsBMMap
            , List<Object> paramList, Map<String, BdCurrencyVO> currMap, Map<String, Object[]> rateMap, Map<String, String> bankAccountMap, Map<String, YntCpaccountVO> accountMap
            , Map<String, AuxiliaryAccountBVO> assistMap, Map<String, List<AccsetVO>> accsetMap, Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map, Map<String, String> jituanSubMap, YntCpaccountVO[] accVOs
            , String tradeCode, String newrule, List<AuxiliaryAccountBVO> chFzhsBodyVOs) throws DZFWarpException;
	
	public void saveGL(IntradeHVO hvo, String pk_corp, String userid) throws DZFWarpException;
	
	public void saveTotalGL(IntradeHVO[] vos, String pk_corp, String userid) throws DZFWarpException;
	/**
	 * 删除凭证号
	 * @param pk_corp
	 * @param pk_tzpz_h
	 * @throws DZFWarpException
	 */
	public void deletePZH(String pk_corp, String pk_tzpz_h) throws DZFWarpException;
	/**
	 * 更新凭证号
	 * @param headvo
	 * @throws DZFWarpException
	 */
	public void updatePZH(TzpzHVO headvo) throws DZFWarpException;
	/**
	 * 更新单据出库状态
	 * @param pk_vatsaleinvoice
	 * @param pk_corp
	 * @param pk_ictrade_h
	 * @throws DZFWarpException
	 */
	public void updateICStatus(String pk_vatsaleinvoice, String pk_corp, String pk_ictrade_h) throws DZFWarpException;
	
	/**
	 * 根据公司查询是否认证通过
	 * @param corpvo
	 * @return
	 * @throws DZFWarpException
	 */
	public TicketNssbhVO getNssbvo(CorpVO corpvo) throws DZFWarpException;
	
	public List<VatGoosInventoryRelationVO> getGoodsInvenRela(List<VATSaleInvoiceVO2> saleList, String pk_corp) throws DZFWarpException;
	
	public void saveGoodsRela(Map<String, List<VatGoosInventoryRelationVO>> newRelMap, String pk_corp, String userid) throws DZFWarpException;
	
	public List<InventoryAliasVO> matchInventoryData(String pk_corp, VATSaleInvoiceVO2[] vos, InventorySetVO invsetvo)throws DZFWarpException;
	
	public InventoryAliasVO[] saveInventoryData(String pk_corp, InventoryAliasVO[] vos, List<Grid> logList)throws DZFWarpException;
	
	/**
	 * 生成凭证
	 * @throws DZFWarpException
	 */
	public void createPZ(VATSaleInvoiceVO2 vo, String pk_corp, String userid, boolean accway, boolean isT, VatInvoiceSetVO setvo, InventorySetVO invsetvo, String jsfs) throws DZFWarpException;
	
	/**
	 * 合并生单
	 * @param list
	 * @param pk_corp
	 * @param userid
	 * @param setvo
	 * @param isT
	 * @throws DZFWarpException
	 */
	public void saveCombinePZ(List<VATSaleInvoiceVO2> list, String pk_corp, String userid, VatInvoiceSetVO setvo, boolean accway, boolean isT, InventorySetVO invsetvo, String jsfs) throws DZFWarpException;
	
	/*public List<VatBusinessTypeVO> getBusiType(String pk_corp) throws DZFWarpException;*/
	
	public List<BillCategoryVO> querySaleCategoryRef(String pk_corp, String period)throws DZFWarpException;
	
	public void updateVO(String[] ids, String pk_model_h, String pk_corp, String pk_category_keyword, String busisztypecode, String rzkm, String jskm, String shkm)throws DZFWarpException;
	
	public List<VATSaleInvoiceVO2> changeToSale(List<VATSaleInvoiceVO2> sList, String pk_corp)throws DZFWarpException;

	public String checkNoStock(List<VATSaleInvoiceVO2> list, String pk_corp) throws DZFWarpException;
	
	public boolean checkIsStock(VATSaleInvoiceVO2 list) throws DZFWarpException;
	
	public void checkvoPzMsg(String pk_vatsaleinvoice)throws DZFWarpException;
	/**
	 * 生成出入库单
	 * @param oldVO
	 * @param corpvo
	 * @param userid
	 * @param fzhsBodyMap
	 * @return
	 * @throws DZFWarpException
	 */
	public IntradeHVO createIH(VATSaleInvoiceVO2 oldVO, CorpVO corpvo, String userid, Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap)throws DZFWarpException ;
}
