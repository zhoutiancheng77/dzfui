package com.dzf.zxkj.platform.service.pjgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.bdset.BankAccountVO;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.pjgl.BankBillToStatementVO;
import com.dzf.zxkj.platform.model.pjgl.BankStatementVO;
import com.dzf.zxkj.platform.model.pjgl.VatInvoiceSetVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IBankStatementService {

	BankStatementVO queryByPrimaryKey(String primaryKey) throws DZFWarpException;

	void delete(BankStatementVO vo, String pk_corp) throws DZFWarpException;
	
//	public void saveVOArr(BankStatementVO[] vos) throws DZFWarpException;
	
	public List<BankStatementVO> quyerByPkcorp(String pk_corp, BankStatementVO vo, String sort, String order) throws DZFWarpException;
	
	public void updateVOArr(DZFBoolean isAddNew, String pk_corp, String cuserid, String sort, String order, List<BankStatementVO> list) throws DZFWarpException;
	public BankStatementVO[] updateVOArr(String pk_corp, Map<String, BankStatementVO[]> map, DZFBoolean isFlag) throws DZFWarpException;
	
	public String saveImp(File file, BankStatementVO paramvo, String fileType, int sourceType) throws DZFWarpException;
	
//	public List<BankStatementVO> query(String pk_corp) throws DZFWarpException;
	/**
	 * 生成凭证
	 * @throws DZFWarpException
	 */
	public void createPZ(BankStatementVO vo, String pk_corp, String userid, Map<String, DcModelHVO> dcmap, BankAccountVO bankAccVO, VatInvoiceSetVO setvo, boolean accway, boolean isT) throws DZFWarpException;
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
	public Map<String, DcModelHVO> queryDcModelVO(String pk_corp) throws DZFWarpException;
	
	/**
	 * 构建银行对账单vo
	 * @param vos
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BankStatementVO> construcBankStatement(BankStatementVO[] vos, String pk_corp) throws DZFWarpException;
	
	/**
	 * 设置业务类型
	 * @param vos
	 * @param busiid
	 * @param businame
	 * @return
	 * @throws DZFWarpException
	 */
	public String setBusiType(BankStatementVO[] vos, String busiid, String businame) throws DZFWarpException;
	
	public String saveBusiPeriod(BankStatementVO[] vos, String pk_corp, String period) throws DZFWarpException;
	
	/**
     * 构造凭证vo
     * @param vos
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
	public TzpzHVO getTzpzHVOByID(BankStatementVO[] vos, BankAccountVO bankAccVO, String pk_corp, String userid, VatInvoiceSetVO setvo, boolean accway) throws DZFWarpException;
	/**
	 * 合并生单前校验
	 * @param vos
	 * @throws DZFWarpException
	 */
	public void checkBeforeCombine(BankStatementVO[] vos) throws DZFWarpException;
	
	/**
	 * 合并生单
	 * @param vos
	 * @param pk_corp
	 * @param userid
	 * @param modelvo
	 * @param isT
	 * @throws DZFWarpException
	 */
	public void saveCombinePZ(List<BankStatementVO> list, String pk_corp, String userid, Map<String, DcModelHVO> dcmap, BankAccountVO bankAccVO, VatInvoiceSetVO setvo, boolean accway, boolean isT) throws DZFWarpException;
	
	public List<String> getBusiTypes(String pk_corp) throws DZFWarpException;
	
	/**
	 * 根据pks查询vo记录
	 * @param pks
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BankStatementVO> queryByPks(String[] pks, String pk_corp) throws DZFWarpException;
	
	public void saveVOs(String pk_corp, List<BankStatementVO> list) throws DZFWarpException;
	
	public BankStatementVO queryByBankBill(String pk_corp, BankBillToStatementVO vo) throws DZFWarpException;
	
	public boolean saveBankBill(String pk_corp, BankBillToStatementVO billvo, BankStatementVO bankvo) throws DZFWarpException;
	
	public void deleteBankBillByLibrary(List<String> libpks, String pk_corp) throws DZFWarpException;
	
	public StringBuffer buildQmjzMsg(List<String> periodList, String pk_corp) throws DZFWarpException;
}