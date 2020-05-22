package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.pjgl.InvoiceParamVO;
import com.dzf.zxkj.platform.model.pjgl.VatGoosInventoryRelationVO;
import com.dzf.zxkj.platform.model.pjgl.VatInvoiceSetVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.model.zncs.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IVATInComInvoice2Service {

	/**
	 * 查询updatePZH
	 * @param paramvo
	 * @param sort
	 * @param order
	 * @return
	 * @throws DZFWarpException
	 */
	public List<VATInComInvoiceVO2> quyerByPkcorp(InvoiceParamVO paramvo, String sort, String order) throws DZFWarpException;

	public VATInComInvoiceVO2 queryByID(String pk) throws DZFWarpException;
	/**
	 * 更新操作
	 * @throws DZFWarpException
	 */
//	public void updateVOArr(DZFBoolean isAddNew, String pk_corp, String cuserid, String sort, String order, List<VATInComInvoiceVO2> list) throws DZFWarpException;
	
	
	public VATInComInvoiceVO2[] updateVOArr(String pk_corp, Map<String, VATInComInvoiceVO2[]> map) throws DZFWarpException;
	
	/**
	 * 删除
	 * @param vo
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	void delete(VATInComInvoiceVO2 vo, String pk_corp) throws DZFWarpException;
	
	/**
	 * 导入
	 * @param file
	 * @param pk_corp
	 * @param fileType
	 * @param userid
	 * @throws DZFWarpException
	 */
	public void saveImp(MultipartFile file, VATInComInvoiceVO2 paramvo, String pk_corp, String fileType, String userid, StringBuffer msg) throws DZFWarpException;
	
	/**
	 * 生成凭证
	 * @throws DZFWarpException
	 */
	public void createPZ(VATInComInvoiceVO2 vo, String pk_corp, String userid, String period, VatInvoiceSetVO setvo, DZFBoolean lwflag, boolean accway, boolean isT, ZncsParamVO zncsParamVO) throws DZFWarpException;
	
//	public Map<String, DcModelHVO> queryDcModelVO(String pk_corp) throws DZFWarpException;
	/**
	 * 构造进项vo
	 * @param vos
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<VATInComInvoiceVO2> construcComInvoice(VATInComInvoiceVO2[] vos, String pk_corp) throws DZFWarpException;
	
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
	/*public String saveBusiType(VATInComInvoiceVO2[] vos, String busiid, String businame, String selvalue, String userid, String pk_corp) throws DZFWarpException;*/
	
	/**
	 * 设置入账期间

	 * @throws DZFWarpException
	 */
	public String saveBusiPeriod(VATInComInvoiceVO2[] vos, String pk_corp, String[] arguments) throws DZFWarpException;
	
    /**
     * 构造凭证vo
     * @param vos
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
	public TzpzHVO getTzpzHVOByID(VATInComInvoiceVO2[] vos, String pk_corp, String userid, VatInvoiceSetVO setvo, boolean accway) throws DZFWarpException;
	
	/**
	 * 合并生单前校验
	 * @param vos
	 * @throws DZFWarpException
	 */
	public void checkBeforeCombine(VATInComInvoiceVO2[] vos) throws DZFWarpException;
	
	/**
	 * 合并生单
	 * @param list
	 * @param pk_corp
	 * @param userid
	 * @param period
	 * @param isT
	 * @throws DZFWarpException
	 */
	public void saveCombinePZ(List<VATInComInvoiceVO2> list, String pk_corp, String userid, String period, VatInvoiceSetVO setvo, DZFBoolean lwflag, boolean accway, boolean isT,ZncsParamVO zncsParamVO) throws DZFWarpException;
	
	/**
	 * 构造需要的业务类型
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	/*public List<String> getBusiTypes(String pk_corp) throws DZFWarpException;*/
	
	/*public void scanMatchBusiName(VATInComInvoiceVO2 incomvo, Map<String, DcModelHVO> dcmap) throws DZFWarpException;*/
	
	public Map<String, VATInComInvoiceVO2> savePt(String pk_corp, String userid, String ccrecode, String jspbh, VATInComInvoiceVO2 paramvo, String serType, String rzPeriod) throws DZFWarpException;
	
	public VATInComInvoiceVO2 queryByCGTId(String fphm, String fpdm, String pk_corp) throws DZFWarpException;

	/**
	 * 根据pks查询vo记录
	 * @param pks
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<VATInComInvoiceVO2> queryByPks(String[] pks, String pk_corp) throws DZFWarpException;

	public List<TaxitemVO> queryTaxItems(String pk_corp) throws DZFWarpException;
	
//	public void dealBodyTaxItem(List<TaxitemVO> taxvos, VATInComInvoiceBVO bvo) throws DZFWarpException;
	
	/**
	 * 生成入库单
	 * @param vo
	 * @param accounts
	 * @param corpvo
	 * @param userid
	 * @return
	 * @throws DZFWarpException
	 */
	public IntradeHVO createIC(VATInComInvoiceVO2 vo, YntCpaccountVO[] accounts, CorpVO corpvo, String userid) throws DZFWarpException;

	public void saveGL(IntradeHVO hvo, String pk_corp, String userid) throws DZFWarpException;
	
	public void saveTotalGL(IntradeHVO[] vos, String pk_corp, String userid) throws DZFWarpException;
	
	public void deletePZH(String pk_corp, String pk_tzpz_h) throws DZFWarpException;
	
	public void updatePZH(TzpzHVO headvo) throws DZFWarpException;
	
	/**
	 * 更新单据入库状态
	 * @param pk_vatsaleinvoice
	 * @param pk_corp
	 * @param pk_ictrade_h
	 * @throws DZFWarpException
	 */
	public void updateICStatus(String pk_vatsaleinvoice, String pk_corp, String pk_ictrade_h) throws DZFWarpException;
	
	public List<InventoryAliasVO> matchInventoryData(String pk_corp, VATInComInvoiceVO2[] vos, InventorySetVO invsetvo)throws DZFWarpException;
	
	public InventoryAliasVO[] saveInventoryData(String pk_corp, InventoryAliasVO[] vos, List<Grid> logList)throws DZFWarpException;
	
	/**
	 * 生成凭证
	 * @throws DZFWarpException
	 */
	public void createPZ(VATInComInvoiceVO2 vo, String pk_corp, String userid, boolean accway, boolean isT, InventorySetVO invsetvo, VatInvoiceSetVO setvo, String jsfs,ZncsParamVO zncsParamVO) throws DZFWarpException;
	
	/**
	 * 合并生单F
	 * @param list
	 * @param pk_corp
	 * @param userid
	 * @param setvo
	 * @param isT
	 * @throws DZFWarpException
	 */
	public void saveCombinePZ(List<VATInComInvoiceVO2> list, String pk_corp, String userid, VatInvoiceSetVO setvo, boolean accway, boolean isT, InventorySetVO invsetvo, String jsfs,ZncsParamVO zncsParamVO) throws DZFWarpException;
	
	public CorpVO chooseTicketWay(String pk_corp) throws DZFWarpException;

	/*public List<VatBusinessTypeVO> getBusiType(String pk_corp) throws DZFWarpException;*/
	
	public List<BillCategoryVO> queryIncomeCategoryRef(String pk_corp, String period)throws DZFWarpException;
	
	public List<CategorysetVO> queryIncomeCategorySet(String id, String pk_corp)throws DZFWarpException;

	public void updateCategoryset(DZFBoolean flag, String pk_model_h, String busisztypecode, String pk_basecategory, String pk_corp, String rzkm, String jskm, String shkm, String zdyzy)throws DZFWarpException;
	
	public void updateVO(String[] ids, String pk_model_h, String pk_corp, String pk_category_keyword, String busisztypecode, String rzkm, String jskm, String shkm)throws DZFWarpException;
	
	public List<VATInComInvoiceVO2> queryVOByID(String pk_vatincominvoice) throws DZFWarpException;
	
	public List<VATInComInvoiceVO2> changeToInCom(List<VATInComInvoiceVO2> bList, String pk_corp) throws DZFWarpException;

	
	public List<VATInComInvoiceVO2> constructVatSale(VATInComInvoiceVO2[] vos, String pk_corp) throws DZFWarpException;
	
	public List<VatGoosInventoryRelationVO> getGoodsInvenRela(List<VATInComInvoiceVO2> incomeList, String pk_corp) throws DZFWarpException;
	
	public String checkNoStock(List<VATInComInvoiceVO2> list, String pk_corp)throws DZFWarpException;
	
	public boolean checkIsStock(VATInComInvoiceVO2 vo)throws DZFWarpException;

	public VATInComInvoiceVO2 checkvoPzMsg(String pk_vatincominvoice)throws DZFWarpException;
	
	/**
	 * 生出入库单
	 * @param vo
	 * @param accounts
	 * @param corpvo
	 * @param userid
	 * @return
	 * @throws DZFWarpException
	 */
	public IntradeHVO createIH(VATInComInvoiceVO2 vo, YntCpaccountVO[] accounts, CorpVO corpvo, String userid)throws DZFWarpException;
	
	public void saveOrUpdateCorpReference(CorpReferenceVO vo)throws DZFWarpException;
	
	public CorpReferenceVO queryCorpReference(String pk_corp, Integer isjinxiang)throws DZFWarpException;
}
