package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IZtszService;
import com.dzf.zxkj.platform.service.tax.ICorpTaxService;
import com.dzf.zxkj.platform.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("sys_ztsz_serv")
public class ZtszServiceImpl implements IZtszService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private ICorpTaxService corpTaxact;
	
	@Autowired
	private IBDCorpTaxService bdcorptaxserv;

	@Autowired
	private ICorpService corpService;

	@Override
	public void updateCorpTaxVo(CorpTaxVo corptaxvo, String selTaxReportIds, String unselTaxReportIds)
			throws DZFWarpException {
		//更新 征收方式
		saveCharge(corptaxvo);
		
		// 如果当前表void是空则新增
		corptaxvo.setIsmaintainedtax(new DZFBoolean(true));//是否维护了纳税信息
		if (StringUtil.isEmpty(corptaxvo.getPrimaryKey())) {
			saveNew(corptaxvo);
		} else {
			update(corptaxvo);
		}
		// 回写bd_corp表的数据，并且清除公司缓存
		writeBackCorp(corptaxvo);

		// 更新税务信息
		HashMap<String, SuperVO[]> sendData = new HashMap<String, SuperVO[]>();
		String[] taxRptids = null;
		if (!StringUtil.isEmpty(selTaxReportIds)) {
			taxRptids = selTaxReportIds.split(",");
		}
		String[] taxUnRptids = null;
		if (!StringUtil.isEmpty(unselTaxReportIds)) {
			taxUnRptids = unselTaxReportIds.split(",");
		}
		// 只是复制corptaxvo的字段
		// 后台更新的字段也只是纳税的字段，不会有问题
		CorpVO cpvo = new CorpVO();
		BeanUtils.copyProperties(corptaxvo, cpvo);
		cpvo.setPk_corp(corptaxvo.getPk_corp());
		corpTaxact.updateCorp(cpvo, sendData, taxRptids, taxUnRptids);
	}
	
	private void saveCharge(CorpTaxVo vo){
		CorpVO corpvo = corpService.queryByPk(vo.getPk_corp());
		if(corpvo == null 
				|| !"00000100AA10000000000BMD".equals(corpvo.getCorptype())){//不是13小企业 不设默认值
			return;
		}
		bdcorptaxserv.saveCharge(vo, vo.getPk_corp(), null, new StringBuffer());
	}
	
	private void saveNew(CorpTaxVo corptaxvo) {
		singleObjectBO.saveObject(corptaxvo.getPk_corp(), corptaxvo);
	}
	
	private void update(CorpTaxVo corptaxvo) {
		singleObjectBO.update(corptaxvo);
	}	
	/**
	 * 回写bd_corp表的数据
	 * @param corptaxvo
	 */
	private void writeBackCorp(CorpTaxVo corptaxvo) {
		String[] upcolumns = new String[] { "vsoccrecode", "isxrq", "drdsj", "legalbodycode",
				"vcorporatephone", "unitname", "unitshortname",  "industry", "chargedeptname", "icostforwardstyle"};
		String pk_corp = corptaxvo.getPk_corp();
		if (StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("公司不能为空");
		}
		CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		if (cpvo == null) {
			throw new BusinessException("公司不能为空");
		}
		for (String column : upcolumns) {
			// 处理字段加密
			if ("legalbodycode".equals(column) || "vcorporatephone".equals(column) || "unitname".equals(column)) {
				String value = (String) corptaxvo.getAttributeValue(column);
//				if (!StringUtil.isEmpty(value)) {
//					value = CodeUtils1.enCode(value);
//				}
				cpvo.setAttributeValue(column, value);
			}else if("isxrq".equals(column)){//Integer 类型有问题，单独处理
				cpvo.setIsxrq((Integer)corptaxvo.getAttributeValue(column));
			}
			else if("unitshortname".equals(column)){
				cpvo.setUnitshortname(cpvo.getUnitname());//公司简称默认等于公司名称
			}
			else {// 处理非加密的字段
				cpvo.setAttributeValue(column, corptaxvo.getAttributeValue(column));
			}
		}
		
		//TODO 这个没考虑bd_corp的加锁
		singleObjectBO.update(cpvo, upcolumns);

	}
}
