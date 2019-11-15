package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.image.OcrInvoiceDetailVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.zncs.BillCategoryVO;
import com.dzf.zxkj.platform.model.zncs.BillcategoryQueryVO;
import com.dzf.zxkj.platform.model.zncs.CategoryTreeVO;
import com.dzf.zxkj.platform.model.zncs.CheckOcrInvoiceVO;

import java.util.List;

/**
 * 分类树
 * @author mfz
 *
 */
public interface IBillcategory {
	/**
	 * 主界面分类+票据查询
	 * @param paramVO
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BillCategoryVO> queryCategoryTree(BillcategoryQueryVO paramVO)throws DZFWarpException;
	
	/**
	 * 移动到-参照
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CategoryTreeVO> queryCategoryTree(String pk_corp, String period)throws DZFWarpException;
	
	/**
	 * 移动到-保存
	 * @param pk_bills
	 * @param pk_tree 目标树ID
	 * @param pk_corp 
	 * @throws DZFWarpException
	 */
	public void saveNewCategory(String[] pk_bills, String pk_tree, String pk_corp, String period)throws DZFWarpException;
	public void saveNewCategroy(OcrInvoiceVO[] vos, String pk_tree, String pk_corp, String period) throws DZFWarpException;
	/**
	 * 表体移动到
	 * @throws DZFWarpException
	 */
	public void saveNewCategoryBody(String[] pk_bills, String pk_tree, String pk_corp, String period)throws DZFWarpException;
	/**
	 * 修改公司级分类的名称
	 * @param pk_category
	 * @param categoryname
	 * @throws DZFWarpException
	 */
	public void updateCategoryName(String pk_category, String categoryname)throws DZFWarpException;
	
	/*
	 * 重新整理目录
	 * param  period 期间
	 * param  pk_category 当前类别主键
	 * param pk_parent 父类别主键
	 * param  pk_corp 当前公司主键
	 */
	public void updateCategoryAgain(String pk_category, String pk_parent, String pk_corp, String period)throws DZFWarpException;
	/*
	 * 检测分类
	 * param  period 期间
	 * param  pk_corp 当前公司主键
	 */
	public List<CheckOcrInvoiceVO> modifyCheckCategory(String pk_corp, String period)throws DZFWarpException;
	/*
	 * 查询父节点
	 */
	public String queryParentPK(DZFBoolean isyh, OcrInvoiceVO ocrVO, String categorycode)throws DZFWarpException;
	
	public List<OcrInvoiceDetailVO> queryDetailVOs(String pk_invoice)throws DZFWarpException;
	
	public List<OcrInvoiceVO> queryBankInvoiceVOs(BillcategoryQueryVO paramVO)throws DZFWarpException;
	
	/**
	 * 查分类下有错误的票据
	 * @param paramVO
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CheckOcrInvoiceVO> queryErrorDetailVOs(BillcategoryQueryVO paramVO)throws DZFWarpException;
	/**
	 * 检查发票是否生成了资产卡片
	 * @param vos
	 * @throws DZFWarpException
	 */
	public DZFBoolean checkHaveZckp(OcrInvoiceVO[] vos)throws DZFWarpException;
	/**
	 * 检查发票是否生成了出入库单
	 * @param vos
	 * @throws DZFWarpException
	 */
	public DZFBoolean checkHaveIctrade(OcrInvoiceVO[] vos)throws DZFWarpException;
	
	/**
	 * 年结、关账检查当月票是否已做账
	 * @return
	 * @throws DZFWarpException
	 */
	public int checkInvoiceForGz(String pk_corp, String period)throws DZFWarpException;
	
	public List<OcrInvoiceVO> queryBillsByWhere(BillcategoryQueryVO paramVO)throws DZFWarpException;
}
