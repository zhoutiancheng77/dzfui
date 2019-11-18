package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;

public interface IAutoMatchName {

	// public CorpVO autoMatchCorp(String name, String taxno, int type, CorpVO[]
	// corpvos);

	public AuxiliaryAccountBVO autoMatchAuxiliaryAccount(String name, int type, String pk_corp);

	public InventoryVO autoMatchInventoryVO(String name, int type, String pk_corp);

	// public CorpVO getCorpByName(String name, CorpVO[] corpvos);

	public AuxiliaryAccountBVO getAuxiliaryAccountBVOByName(String name, String pk_corp, String pk_auacount_h);
	
	public AuxiliaryAccountBVO getAuxiliaryAccountBVOByInfo(String invname, String invtype, String invunit, String pk_corp, String pk_auacount_h);

	public InventoryVO getInventoryVOByName(String name, String pk_corp);
	
	public InventoryVO getInventoryVOByName(String invname, String invtype, String invunit, String pk_corp);

	public YntCpaccountVO getXJAccountVOByName(String name, String code, String pk_corp);

	public YntCpaccountVO atuoMatchXJAccountVOByName(String name, String code, String pk_corp);

	public AuxiliaryAccountBVO getAuxiliaryAccountBVOByTaxNo(String taxno, String pk_corp, String pk_auacount_h);
}