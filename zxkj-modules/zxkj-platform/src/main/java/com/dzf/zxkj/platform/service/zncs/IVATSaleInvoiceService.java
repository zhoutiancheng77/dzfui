package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.pjgl.*;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface IVATSaleInvoiceService {

	/**
	 * 查询
	 * @param paramvo
	 * @param sort
	 * @param order
	 * @return
	 * @throws DZFWarpException
	 */
	public List<VATSaleInvoiceVO> quyerByPkcorp(InvoiceParamVO paramvo, String sort, String order) throws DZFWarpException;

	public VATSaleInvoiceVO queryByID(String pk) throws DZFWarpException;
	/**
	 * 更新操作
	 *
	 * @throws DZFWarpException
	 */
//	public void updateVOArr(DZFBoolean isAddNew, String pk_corp, String cuserid, String sort, String order, List<VATSaleInvoiceVO> list) throws DZFWarpException;
	
	public VATSaleInvoiceVO[] updateVOArr(String pk_corp, Map<String, VATSaleInvoiceVO[]> map) throws DZFWarpException;
	
	/**
	 * 删除
	 * @param vo
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	void delete(VATSaleInvoiceVO vo, String pk_corp) throws DZFWarpException;
	
	/**
	 * 导入
	 * @param file
	 * @param filename
	 * @param pk_corp
	 * @param fileType
	 * @param userid
	 * @throws DZFWarpException
	 */
	public void saveImp(File file, String filename, VATSaleInvoiceVO paramvo, String pk_corp, String fileType, String userid, StringBuffer msg) throws DZFWarpException;
	
	/**
	 * 生成凭证
	 * @throws DZFWarpException
	 */
	public void createPZ(VATSaleInvoiceVO vo, String pk_corp, String userid, Map<String, DcModelHVO> map, VatInvoiceSetVO setvo, DZFBoolean lwflag, boolean accway, boolean isT) throws DZFWarpException;
	
//	public Map<String, YntCpaccountVO> construcTemKm(CorpVO corpvo) throws DZFWarpException;
	
	public List<VATSaleInvoiceVO> constructVatSale(VATSaleInvoiceVO[] vos, String pk_corp) throws DZFWarpException;
	/**
	 * 合并生单
	 * @param list
	 * @param pk_corp
	 * @param userid
	 * @param
	 * @param isT
	 * @throws DZFWarpException
	 */
	public void saveCombinePZ(List<VATSaleInvoiceVO> list, String pk_corp, String userid, Map<String, DcModelHVO> dcmap, DZFBoolean lwflag, VatInvoiceSetVO setvo, boolean accway, boolean isT) throws DZFWarpException;
	
	public List<String> getBusiTypes(String pk_corp) throws DZFWarpException;
	
	/**
	 * 设置业务类型
	 * @param vos
	 * @param busiid
	 * @param businame
	 * @param selvalue
	 * @param userid
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public String saveBusiType(VATSaleInvoiceVO[] vos, String busiid, String businame, String selvalue, String userid, String pk_corp) throws DZFWarpException;
	
	public CorpVO chooseTicketWay(String pk_corp) throws DZFWarpException;
	
	/**
	 * 设置入账期间
	 * @param vos
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public String saveBusiPeriod(VATSaleInvoiceVO[] vos, String pk_corp, String period) throws DZFWarpException;
	
	/**
     * 构造凭证vo
     * @param vos
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
	public TzpzHVO getTzpzHVOByID(VATSaleInvoiceVO[] vos, String pk_corp, String userid, VatInvoiceSetVO setvo, boolean accway) throws DZFWarpException;
	
	/**
	 * 合并生单前校验
	 * @param vos
	 * @throws DZFWarpException
	 */
	public void checkBeforeCombine(VATSaleInvoiceVO[] vos) throws DZFWarpException;
	
	public List<String> getCustNames(String pk_corp, VATSaleInvoiceVO[] vos) throws DZFWarpException;
	
	public Map<String, VATSaleInvoiceVO> saveCft(String pk_corp, String userid, String ccrecode, String fptqm, VATSaleInvoiceVO paramvo, StringBuffer msg) throws DZFWarpException;
	
	public Map<String, VATSaleInvoiceVO> saveKp(String pk_corp, String userid, VATSaleInvoiceVO paramvo, TicketNssbhVO nssbvo) throws DZFWarpException;
	
	public void scanMatchBusiName(VATSaleInvoiceVO salevo, Map<String, DcModelHVO> dcmap) throws DZFWarpException;

	/**
	 * 根据pks查询vo记录
	 * @param pks
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<VATSaleInvoiceVO> queryByPks(String[] pks, String pk_corp) throws DZFWarpException;

	public List<TaxitemVO> queryTaxItems(String pk_corp) throws DZFWarpException;
	/*
	 * 生成出库单
	 */
	public IntradeHVO createIC(VATSaleInvoiceVO vo, YntCpaccountVO[] accounts, CorpVO corpvo, String userid) throws DZFWarpException;
	
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
	
	public List<VatGoosInventoryRelationVO> getGoodsInvenRela(Map<String, DcModelHVO> dcmap, List<VATSaleInvoiceVO> saleList, String pk_corp) throws DZFWarpException;
	
	public void saveGoodsRela(Map<String, List<VatGoosInventoryRelationVO>> newRelMap, String pk_corp, String userid) throws DZFWarpException;
	
	public List<InventoryAliasVO> matchInventoryData(String pk_corp, VATSaleInvoiceVO[] vos, InventorySetVO invsetvo)throws DZFWarpException;
	
	public InventoryAliasVO[] saveInventoryData(String pk_corp, InventoryAliasVO[] vos, List<Grid> logList)throws DZFWarpException;
	
	/**
	 * 生成凭证
	 * @throws DZFWarpException
	 */
	public void createPZ(VATSaleInvoiceVO vo, String pk_corp, String userid, boolean accway, boolean isT, VatInvoiceSetVO setvo, InventorySetVO invsetvo, String jsfs) throws DZFWarpException;
	
	/**
	 * 合并生单
	 * @param
	 * @param pk_corp
	 * @param userid
	 * @param
	 * @param isT
	 * @throws DZFWarpException
	 */
	public void saveCombinePZ(List<VATSaleInvoiceVO> list, String pk_corp, String userid, VatInvoiceSetVO setvo, boolean accway, boolean isT, InventorySetVO invsetvo, String jsfs) throws DZFWarpException;
	
	public List<VatBusinessTypeVO> getBusiType(String pk_corp) throws DZFWarpException;
}
