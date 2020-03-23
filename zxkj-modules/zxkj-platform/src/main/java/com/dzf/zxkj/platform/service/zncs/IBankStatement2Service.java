package com.dzf.zxkj.platform.service.zncs;

import com.dzf.account.api.model.icbc.gyj.IcbcErcptApplyAndQrywlhdetailQo;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.pjgl.BankBillToStatementVO;
import com.dzf.zxkj.platform.model.pjgl.VatInvoiceSetVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IBankStatement2Service{

	BankStatementVO2 queryByPrimaryKey(String primaryKey) throws DZFWarpException;

	void delete(BankStatementVO2 vo, String pk_corp) throws DZFWarpException;
	
//	public void saveVOArr(BankStatementVO2[] vos) throws DZFWarpException;
	
	public List<BankStatementVO2> quyerByPkcorp(String pk_corp, BankStatementVO2 vo, String sort, String order) throws DZFWarpException;
	
	public void updateVOArr(DZFBoolean isAddNew, String pk_corp, String cuserid, String sort, String order, List<BankStatementVO2> list) throws DZFWarpException;
	public BankStatement2ResponseVO updateVOArr(String pk_corp, Map<String, BankStatementVO2[]> map, DZFBoolean isFlag) throws DZFWarpException;
	
	public String saveImp(MultipartFile file, BankStatementVO2 paramvo, String fileType, int sourceType) throws DZFWarpException;
	
//	public List<BankStatementVO2> query(String pk_corp) throws DZFWarpException;
	/**
	 * 生成凭证
	 * @throws DZFWarpException
	 */
	public void createPZ(BankStatementVO2 vo, String pk_corp, String userid, String period, BankAccountVO bankAccVO, VatInvoiceSetVO setvo, boolean accway, boolean isT
            , List<List<Object[]>> levelList, Map<String, Object[]> categoryMap, Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap, Set<String> zyFzhsList
            , Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap, InventorySetVO inventorySetVO, CorpVO corp, Map<String, InventoryAliasVO> fzhsBMMap
            , List<Object> paramList, Map<String, BdCurrencyVO> currMap, Map<String, Object[]> rateMap, Map<String, String> bankAccountMap, Map<String, YntCpaccountVO> accountMap
            , Map<String, AuxiliaryAccountBVO> assistMap, Map<String, List<AccsetVO>> accsetMap, Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map, Map<String, String> jituanSubMap, YntCpaccountVO[] accVOs
            , String tradeCode, String newrule, List<AuxiliaryAccountBVO> chFzhsBodyVOs) throws DZFWarpException;
	/**
	 * 生成暂存态凭证前校验
	 * @param pk_corp
	 * @param hvo
	 * @throws DZFWarpException
	 */
	public void checkCreatePZ(String pk_corp, TzpzHVO hvo) throws DZFWarpException;
	
	public BigDecimal checkIsQjsyjz(String pk_corp, String period) throws DZFWarpException;
	/**
	 * 查询业务类型模板
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	/*public Map<String, DcModelHVO> queryDcModelVO(String pk_corp) throws DZFWarpException;*/
	
	/**
	 * 构建银行对账单vo
	 * @param vos
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BankStatementVO2> construcBankStatement(BankStatementVO2[] vos, String pk_corp) throws DZFWarpException;
	
	/**
	 * 设置业务类型
	 * @param vos
	 * @param busiid
	 * @param businame
	 * @return
	 * @throws DZFWarpException
	 */
	public String setBusiType(BankStatementVO2[] vos, String busiid, String businame) throws DZFWarpException;
	
	public String saveBusiPeriod(BankStatementVO2[] vos, String pk_corp, String period) throws DZFWarpException;
	
	/**
     * 构造凭证vo
     * @param vos
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
	public TzpzHVO getTzpzHVOByID(BankStatementVO2[] vos, BankAccountVO bankAccVO, String pk_corp, String userid, VatInvoiceSetVO setvo, boolean accway) throws DZFWarpException;
	/**
	 * 合并生单前校验
	 * @param vos
	 * @throws DZFWarpException
	 */
	public void checkBeforeCombine(BankStatementVO2[] vos) throws DZFWarpException;
	
	/**
	 * 合并生单
	 * @param
	 * @param pk_corp
	 * @param userid
	 * @param
	 * @param isT
	 * @throws DZFWarpException
	 */
	public void saveCombinePZ(List<BankStatementVO2> list, String pk_corp, String userid, String period, BankAccountVO bankAccVO, VatInvoiceSetVO setvo, boolean accway, boolean isT
            , List<List<Object[]>> levelList, Map<String, Object[]> categoryMap, Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap, Set<String> zyFzhsList
            , Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap, InventorySetVO inventorySetVO, CorpVO corp, Map<String, InventoryAliasVO> fzhsBMMap
            , List<Object> paramList, Map<String, BdCurrencyVO> currMap, Map<String, Object[]> rateMap, Map<String, String> bankAccountMap, Map<String, YntCpaccountVO> accountMap
            , Map<String, AuxiliaryAccountBVO> assistMap, Map<String, List<AccsetVO>> accsetMap, Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map, Map<String, String> jituanSubMap, YntCpaccountVO[] accVOs
            , String tradeCode, String newrule, List<AuxiliaryAccountBVO> chFzhsBodyVOs) throws DZFWarpException;
	
	/*public List<String> getBusiTypes(String pk_corp) throws DZFWarpException;*/
	
	/**
	 * 根据pks查询vo记录
	 * @param pks
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BankStatementVO2> queryByPks(String[] pks, String pk_corp) throws DZFWarpException;
	
	public void saveVOs(String pk_corp, List<BankStatementVO2> list) throws DZFWarpException;
	
	public BankStatementVO2 queryByBankBill(String pk_corp, BankBillToStatementVO vo) throws DZFWarpException;
	
	public boolean saveBankBill(String pk_corp, BankBillToStatementVO billvo, BankStatementVO2 bankvo) throws DZFWarpException;
	
	public void deleteBankBillByLibrary(List<String> libpks, String pk_corp) throws DZFWarpException;
	
	public StringBuffer buildQmjzMsg(List<String> periodList, String pk_corp) throws DZFWarpException;
	
	public List<BillCategoryVO> queryBankCategoryRef(String pk_corp, String period)throws DZFWarpException;
	
	public List<BankStatementVO2> updateVO(String id, String pk_model_h, String busitypetempname, String pk_corp, String rzkm, String pk_basecategory, String zdyzy,String jskm)throws DZFWarpException;
	
	public List<BankStatementVO2> queryByID(String pk_bankstatement)throws DZFWarpException;
	public List<BankStatementVO2> queryByIDs(String pk_bankstatement)throws DZFWarpException;

	public Map<String, DcModelHVO> queryDcModelVO(String pk_corp)throws DZFWarpException;
	
	public List<DcModelHVO> queryIsDcModel(String pk_dcmodel)throws DZFWarpException;
	
	public Map<String,BillCategoryVO> queryNewPkcategory(String pk_category, String period, String pk_corp)throws DZFWarpException;
	
	public CategorysetVO queryCategorySetVO(String pk_model_h)throws DZFWarpException;
	
	public void checkvoPzMsg(String pk_bankstatement)throws DZFWarpException;
	public List<DcModelHVO> queryIsDcModels(String pk_dcmodels) throws DZFWarpException;
	public void checkvoPzMsgs(String pk_bankstatements)throws DZFWarpException;
	
	/*
	 * 根据全限定业务类型名称找主键
	 */
	public String queryBillCategoryId(String name, String pk_corp, String period)throws DZFWarpException;
	public BankStatement2ResponseVO ercptApplyAndQrywlhdetail(IcbcErcptApplyAndQrywlhdetailQo vo,String pk_bankaccount)throws DZFWarpException;
	}