package com.dzf.zxkj.platform.services.qcset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.icset.InvclassifyVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.icset.MeasureVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.services.bdset.ICpaccountService;
import com.dzf.zxkj.platform.services.common.impl.BgPubServiceImpl;
import com.dzf.zxkj.platform.services.icset.IInventoryService;
import com.dzf.zxkj.platform.services.jzcl.IQmgzService;
import com.dzf.zxkj.platform.services.qcset.IQcService;
import com.dzf.zxkj.platform.services.qcset.IQcye;
import com.dzf.zxkj.platform.services.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.services.sys.IAccountService;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import com.dzf.zxkj.platform.services.sys.IParameterSetService;
import com.dzf.zxkj.platform.services.sys.IUserService;
import com.dzf.zxkj.platform.util.ReportUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 库存期初
 *
 */
@Service("ic_qcserv")
public class QcServiceImpl extends BgPubServiceImpl implements IQcService {

	@Autowired
	private IInventoryService invservice;
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	@Autowired
	private IYntBoPubUtil yntBoPubUtil = null;
	@Autowired
	private IQmgzService gzservice;
	@Autowired
	private ICpaccountService cpaccountService = null;
	@Autowired
	private IQcye gl_qcyeserv;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private IUserService userServiceImpl;
	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accountService;

	public List<IcbalanceVO> quyerInfovoic(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select e.*,ry.name inventoryname,ry.code inventorycode,ry.invspec,ry.invtype,fy.name inventorytype,");
		sf.append(" re.name measurename,ry.pk_subject,nt.accountname pk_subjectname ,nt.accountcode pk_subjectcode from ynt_icbalance e ");
		sf.append(" join ynt_inventory  ry on e.pk_inventory = ry.pk_inventory ");
		sf.append(" left join ynt_invclassify fy on ry.pk_invclassify = fy.pk_invclassify ");
		sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
		sf.append(" left join ynt_cpaccount nt on nt.pk_corp_account = ry.pk_subject ");
		sf.append(" where e.pk_corp = ? and nvl(e.dr,0) = 0 and ");
		sf.append(" e.dbilldate = (select min(dbilldate) from ynt_icbalance where pk_corp=? and nvl(dr,0)=0)  ");
		List<IcbalanceVO> list = (List<IcbalanceVO>) getSingleObjectBO().executeQuery(sf.toString(), sp,
				new BeanListProcessor(IcbalanceVO.class));
//		list = completinfo1(list, pk_corp);
		return list;
	}
	
	public List<IcbalanceVO> quyerInfovoic(String pk_corp,String ids) throws DZFWarpException {
		
		if (StringUtil.isEmpty(ids)) {
			return null;
		}
		String strids = SqlUtil.buildSqlConditionForIn(ids.split(","));
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select e.*,ry.name inventoryname,ry.code inventorycode,ry.invspec,ry.invtype,fy.name inventorytype,");
		sf.append(" re.name measurename,ry.pk_subject,nt.accountname pk_subjectname ,nt.accountcode pk_subjectcode from ynt_icbalance e ");
		sf.append(" join ynt_inventory  ry on e.pk_inventory = ry.pk_inventory ");
		sf.append(" left join ynt_invclassify fy on ry.pk_invclassify = fy.pk_invclassify ");
		sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
		sf.append(" left join ynt_cpaccount nt on nt.pk_corp_account = ry.pk_subject ");
		sf.append(" where e.pk_corp = ? and nvl(e.dr,0) = 0 and ");
		sf.append(" 	 e.dbilldate = (select min(dbilldate) from ynt_icbalance where pk_corp=? and nvl(dr,0)=0)  ");
		sf.append(" and pk_icbalance in ( ").append(strids).append(" ) ");

		List<IcbalanceVO> list = (List<IcbalanceVO>) getSingleObjectBO().executeQuery(sf.toString(), sp,
				new BeanListProcessor(IcbalanceVO.class));
//		list = completinfo1(list, pk_corp);
		return list;
	}

	private List<IcbalanceVO> completinfo1(List<IcbalanceVO> rs, String pk_corp) throws DZFWarpException {
		Map<String, InventoryVO> ivmap = new HashMap<String, InventoryVO>();
		Map<String, MeasureVO> msmap = new HashMap<String, MeasureVO>();
		Map<String, InvclassifyVO> ilmap = new HashMap<String, InvclassifyVO>();
		Map<String, YntCpaccountVO> ycmap = new HashMap<String, YntCpaccountVO>();
		ivmap = queryMap(InventoryVO.class, pk_corp);
		msmap = queryMap(MeasureVO.class, pk_corp);
		ilmap = queryMap(InvclassifyVO.class, pk_corp);
		ycmap = queryMap(YntCpaccountVO.class, pk_corp);
		InventoryVO ivo;
		MeasureVO msvo;
		InvclassifyVO ilvo;
		YntCpaccountVO ycvo;
		for (int i = 0; i < rs.size(); i++) {
			ivo = ivmap.get(rs.get(i).getAttributeValue("pk_inventory"));
			if (ivo != null) {
				rs.get(i).setAttributeValue("inventoryname", ivo.getName());
				rs.get(i).setAttributeValue("inventorycode", ivo.getCode());
				rs.get(i).setAttributeValue("invspec", ivo.getInvspec());
//				rs.get(i).setAttributeValue("invtype", ivo.getInvtype());
				msvo = msmap.get(ivo.getPk_measure());
				ilvo = ilmap.get(ivo.getPk_invclassify());
				ycvo = ycmap.get(ivo.getPk_subject());
				if (msvo != null) {
					rs.get(i).setAttributeValue("measurename", msvo.getName());
				}
				if (ilvo != null) {
					rs.get(i).setAttributeValue("inventorytype", ilvo.getName());
				}
				if (ycvo != null) {
					rs.get(i).setAttributeValue("pk_subjectname", ycvo.getAccountname());
					rs.get(i).setAttributeValue("pk_subjectcode", ycvo.getAccountcode());
					if (StringUtil.isEmpty(rs.get(i).getPk_subject())) {
						rs.get(i).setAttributeValue("pk_subject", ycvo.getPk_corp_account());
					}
				}
			}

		}
		return rs;
	}

	@Override
	public DZFBoolean checkBeforeSaveNew(SuperVO vo) throws DZFWarpException {
		if (isExist((IcbalanceVO) vo).booleanValue()) {
			return DZFBoolean.TRUE;
		} else {
			throw new BusinessException("该存货已存在库存期初记录!");
			// return DZFBoolean.FALSE;
		}
	}

	@Override
	public DZFBoolean checkBeforeUpdata(SuperVO vo) throws DZFWarpException {

		IcbalanceVO oldvo = (IcbalanceVO) getSingleObjectBO().queryVOByID(vo.getPrimaryKey(), IcbalanceVO.class);
		if (oldvo == null) {
			throw new BusinessException("非法数据，请刷新后重新修改");
			// return DZFBoolean.FALSE;
		}
		if (!oldvo.getPk_corp().equals(((IcbalanceVO) vo).getPk_corp())) {
			throw new BusinessException("出现数据无权问题，无法修改！");
		}
		if (isExist((IcbalanceVO) vo).booleanValue()) {
			return DZFBoolean.TRUE;
		} else {
			throw new BusinessException("该存货已存在库存期初记录!");
			// return DZFBoolean.FALSE;
		}
	}

	private DZFBoolean isExist(IcbalanceVO vo) throws DZFWarpException {

		// String sql = new String("SELECT 1 FROM YNT_ICBALANCE WHERE
		// (PK_INVENTORY = ? )AND PK_CORP = ? AND nvl(dr,0)=0 ");
		StringBuffer sf = new StringBuffer();
		sf.append("SELECT 1 FROM YNT_ICBALANCE WHERE (PK_INVENTORY = ? )AND PK_CORP = ? AND nvl(dr,0)=0 ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_inventory());
		sp.addParam(vo.getPk_corp());

		if (vo.getPrimaryKey() != null && vo.getPrimaryKey().length() > 0) {
			// sql=sql+" AND PK_ICBALANCE !=?";
			sf.append(" AND PK_ICBALANCE !=? ");
			sp.addParam(vo.getPrimaryKey());
		}

		Object i = getSingleObjectBO().executeQuery(sf.toString(), sp, new ColumnProcessor());

		if (i != null) {
			return DZFBoolean.FALSE;
		}
		return DZFBoolean.TRUE;
	}

	@Override
	public IcbalanceVO queryByPrimaryKey(String primaryKey) throws DZFWarpException {
		SuperVO vo = getSingleObjectBO().queryByPrimaryKey(IcbalanceVO.class, primaryKey);
		return (IcbalanceVO) vo;
	}

	private Map<String, Integer> getPreMap(String pk_corp) {
		String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
		int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
		Map<String, Integer> preMap = new HashMap<String, Integer>();
		preMap.put(IParameterConstants.DZF009, num);
		return preMap;
	}

	
	@Override
	public String saveImp(File file, String pk_corp, String fileType, String userid, DZFDate icdate)
			throws DZFWarpException {
		FileInputStream is = null;
		try {

			checkIsCbjz(pk_corp);
			DZFDateTime date = new DZFDateTime();
			is = new FileInputStream(file);
			Workbook impBook = null;
			FormulaEvaluator formula= null;
			if ("xls".equals(fileType)) {
				impBook = new HSSFWorkbook(is);
//				formula= new HSSFFormulaEvaluator((HSSFWorkbook)impBook);
			} else if ("xlsx".equals(fileType)) {
				impBook = new XSSFWorkbook(is);
//				formula= new XSSFFormulaEvaluator((XSSFWorkbook)impBook);
			} else {
				throw new BusinessException("不支持的文件格式");
			}
			
			Sheet sheet1 = impBook.getSheetAt(0);
			HashSet<String> codeSet = new HashSet<String>();

			IcbalanceVO[] icbVOs = queryIcBalance(pk_corp);
			for (IcbalanceVO vo : icbVOs) {
				codeSet.add(vo.getPk_inventory());
			}
			Map<String, InventoryVO> invVOMap = queryInventory(pk_corp);
			Map<String, Integer> preMap = getPreMap(pk_corp);
			Integer nump = preMap.get(IParameterConstants.DZF009);// 数量
			List<IcbalanceVO> list = new ArrayList<IcbalanceVO>();
			Cell invnameCell = null;// 存货名称
			Cell invspecCell = null;// 规格
			Cell invypeCell = null;// 型号
			Cell measureCell = null;// 计量单位
			Cell kmcodeCell = null;// 科目编码
			Cell kmnameCell = null;// 科目名称
			Cell invclassnameCell = null;// 存货类别
			Cell numCell = null;// 数量
			Cell costCell = null;// 成本
			Cell memoCell = null;// 备注

			String invname = null;// 存货名称
			String invspec = null;// 规格
			String invype = null;// 型号
			String measure = null;// 计量单位
			String kmcode = null;// 科目编码
			String kmname = null;// 科目名称
			String invclassname = null;// 存货类别
			String num = null;// 数量
			String cost = null;// 成本
			String memo = null;// 备注
			int failCount = 0;
			StringBuffer msg = new StringBuffer();
			InventoryVO invvo = null;
			IcbalanceVO icbvo = null;
			int length = sheet1.getLastRowNum();
			if(length>1000){
				throw new  BusinessException("最多可导入1000行");
			}
			DecimalFormat nf = new DecimalFormat("0");// 格式化数字
			for (int iBegin = 1; iBegin <= length; iBegin++) {
				invvo = new InventoryVO();
				icbvo = new IcbalanceVO();
				invnameCell = sheet1.getRow(iBegin).getCell(0);
				// if(invnameCell == null)
				// continue;
				invspecCell = sheet1.getRow(iBegin).getCell(1);
//				invypeCell = sheet1.getRow(iBegin).getCell(2);
				measureCell = sheet1.getRow(iBegin).getCell(2);
				// if(measureCell == null)
				// continue;

				kmcodeCell = sheet1.getRow(iBegin).getCell(3);

				kmnameCell = sheet1.getRow(iBegin).getCell(4);
				// if(kmCell == null)
				// continue;
				invclassnameCell = sheet1.getRow(iBegin).getCell(5);
				// if(invclassnameCell == null)
				// continue;
				numCell = sheet1.getRow(iBegin).getCell(6);
				// if(numCell == null)
				// continue;
				costCell = sheet1.getRow(iBegin).getCell(7);
				// if(costCell == null)
				// continue;
				memoCell = sheet1.getRow(iBegin).getCell(8);
				invname = null;
				kmcode = null;
				num = null;
				cost = null;
				if (invnameCell != null && invnameCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					invname = invnameCell.getRichStringCellValue().getString().trim();
					invvo.setName(invname);
				}
				if (invspecCell != null && invspecCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					invspec = invspecCell.getRichStringCellValue().getString().trim();
					invvo.setInvspec(invspec);
				}
//				if (invypeCell != null && invypeCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
//					invype = invypeCell.getRichStringCellValue().getString().trim();
//					invvo.setInvtype(invype);
//				}
				if (measureCell != null && measureCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					measure = measureCell.getRichStringCellValue().getString().trim();
					invvo.setMeasurename(measure);
				}
				// -----------------科目编码，如果格式为文本格式
				if (kmcodeCell != null && kmcodeCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					kmcode = kmcodeCell.getRichStringCellValue().getString().trim();
					invvo.setKmcode(kmcode);
				}
				// -----------------科目编码，如果格式为数字格式
				if (kmcodeCell != null && kmcodeCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
					kmcode = nf.format(kmcodeCell.getNumericCellValue());
					invvo.setKmcode(kmcode);
				}
				//
				if (kmnameCell != null && kmnameCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					kmname = kmnameCell.getRichStringCellValue().getString().trim();
					invvo.setKmname(kmname);
				}
				if (invclassnameCell != null && invclassnameCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					invclassname = invclassnameCell.getRichStringCellValue().getString().trim();
					invvo.setInvclassname(invclassname);
				}
				if (numCell != null && numCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					num = numCell.getRichStringCellValue().getString().trim();
					DZFDouble numVal = new DZFDouble(num);
					icbvo.setNnum(numVal);
				} else if (numCell != null && numCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
					DZFDouble numVall = new DZFDouble(numCell.getNumericCellValue());
					num = numVall.toString();
					icbvo.setNnum(numVall);
				}
				icbvo.setNnum(icbvo.getNnum().setScale(nump, DZFDouble.ROUND_HALF_UP));
				if (costCell != null && costCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					cost = costCell.getRichStringCellValue().getString().trim();
					DZFDouble costVal = new DZFDouble(cost);
					icbvo.setNcost(costVal);
				} else if (costCell != null && costCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
					DZFDouble costVall = new DZFDouble(costCell.getNumericCellValue());
					cost = costVall.toString();
					icbvo.setNcost(costVall);
				}else if (costCell != null && costCell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
//					DZFDouble costVall = new DZFDouble(formula.evaluate(costCell).getNumberValue());
//					cost = costVall.toString();
//					icbvo.setNcost(costVall);
				}
				if (memoCell != null && memoCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					memo = memoCell.getRichStringCellValue().getString().trim();
					icbvo.setMemo(memo);
				}

				if (StringUtil.isEmpty(invname) && StringUtil.isEmpty(kmcode) && StringUtil.isEmpty(num)
						&& StringUtil.isEmpty(cost)) {
					continue;
				}

				if (StringUtil.isEmpty(invname) || StringUtil.isEmpty(kmcode) || StringUtil.isEmpty(num)
						|| StringUtil.isEmpty(cost)) {
					failCount++;
					msg.append("<p><font color = 'red'>第").append(iBegin + 1).append("行必输项为空！</font></p>");
					continue;
				}
				String tempKey = getInventoryKey(invvo);
				if (!invVOMap.containsKey(tempKey)) {
					failCount++;
					msg.append("<p><font color = 'red'>第").append(iBegin + 1).append("行的存货信息在系统中未找到！</font></p>");
					continue;
				} else {
					InventoryVO inventory = invVOMap.get(tempKey);
					String pk_inventory = inventory.getPrimaryKey();
					if (codeSet.contains(pk_inventory)) {
						failCount++;
						// msg.append("<p>第").append(iBegin +
						// 1).append("行信息为：").append(tempKey).append("的项目已存在！</p>");
						msg.append("<p><font color = 'red'>第").append(iBegin + 1).append("行存货信息已存在库存期初记录！</font></p>");
						continue;
					}
					codeSet.add(pk_inventory);
					icbvo.setPk_inventory(pk_inventory);
				}

				icbvo.setPk_corp(pk_corp);
				icbvo.setCreatetime(date.toString());
				icbvo.setCreator(userid);
				icbvo.setCreatedate(icdate);
				icbvo.setDbilldate(icdate.toString());
				list.add(icbvo);
			}
			getSingleObjectBO().insertVOArr(pk_corp, list.toArray(new IcbalanceVO[0]));
			if (StringUtil.isEmpty(msg.toString())) {
				return null;
			} else {
				msg.append("成功导入 ").append(list.size()).append(" 条数据。失败 ").append(failCount).append(" 条");
				return msg.toString();
			}
		} catch (FileNotFoundException e) {
			throw new BusinessException("导入文件未找到");
		} catch (IOException e) {
			throw new BusinessException(e.getMessage());
		} catch (BusinessException e) {
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			throw new BusinessException("未知异常");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	private IcbalanceVO[] queryIcBalance(String pk_corp) {
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		return (IcbalanceVO[]) getSingleObjectBO().queryByCondition(IcbalanceVO.class, " pk_corp = ? and nvl(dr,0)=0 ",
				params);
	}

	private Map<String, InventoryVO> queryInventory(String pk_corp) {
		List<InventoryVO> invList = invservice.queryInfo(pk_corp,null);
		Map<String, InventoryVO> invMap = new HashMap<String, InventoryVO>();
		if (invList != null && invList.size() > 0) {
			String key = null;
			// StringBuffer sf = new StringBuffer();
			for (InventoryVO vo : invList) {
				key = getInventoryKey(vo);
				if (!invMap.containsKey(key)) {
					invMap.put(key, vo);
				}
			}
		}
		return invMap;
	}

	private String getInventoryKey(InventoryVO vo) {
		StringBuffer sf = new StringBuffer();
		sf.append(appendIsNull(vo.getName()));
		sf.append("_");
		sf.append(appendIsNull(vo.getInvspec()));
		sf.append("_");
//		sf.append(appendIsNull(vo.getInvtype()));
//		sf.append("_");
		sf.append(appendIsNull(vo.getMeasurename()));
		sf.append("_");
		sf.append(appendIsNull(vo.getKmcode()));
		// sf.append("_");
		// sf.append(vo.getKmname());
		sf.append("_");
		sf.append(appendIsNull(vo.getInvclassname())).toString();
		return sf.toString();
	}
	
	private String appendIsNull(String info) {
		StringBuffer strb = new StringBuffer();
		if (StringUtil.isEmpty(info)) {
			strb.append("null");
		} else {
			strb.append(info);
		}
		return strb.toString();
	}

	@Override
	public String deleteBatch(String[] ids, String pk_corp) throws DZFWarpException {
		String strids = SqlUtil.buildSqlConditionForIn(ids);
		StringBuffer sf = new StringBuffer();
		sf.append("select 1 from ynt_icbalance where nvl(dr,0) = 0 and pk_corp <> ? and pk_icbalance in ( ")
				.append(strids).append(" ) ");
		SQLParameter param = new SQLParameter();
		param.addParam(pk_corp);
		boolean b = singleObjectBO.isExists(pk_corp, sf.toString(), param);
		if (b) {
			throw new BusinessException("出现数据无权问题，无法删除！");
		}

		checkIsCbjz(pk_corp);

		IcbalanceVO[] invos = (IcbalanceVO[]) singleObjectBO.queryByCondition(IcbalanceVO.class,
				" pk_icbalance in ( " + strids + " ) ", null);

		if (invos == null || invos.length == 0)
			throw new BusinessException("库存期初记录不存在，或已经删除！");

		Map<String, String> map = new HashMap<String, String>();
		for (IcbalanceVO invo : invos) {
			String pk_inventory = invo.getPk_inventory();
			if (!map.containsKey(pk_inventory)) {
				map.put(pk_inventory, invo.getPk_icbalance());
			}
		}

		Map<String, InventoryVO> invmap = queryInventory1(pk_corp);

		StringBuffer errmsg = new StringBuffer();
		List<String> errlist = new ArrayList<>();

		strids = SqlUtil.buildSqlConditionForIn(map.keySet().toArray(new String[0]));
		checkInventoryRef(strids, pk_corp, errmsg, errlist, invmap, "ynt_ictradein", "入库单");

		checkInventoryRef(strids, pk_corp, errmsg, errlist, invmap, "ynt_ictradeout", "出库单");

		checkInventoryRef(strids, pk_corp, errmsg, errlist, invmap, "YNT_TZPZ_B", "凭证");

		if (errlist != null && errlist.size() > 0) {

			for (String str : errlist) {
				if (map.containsKey(str))
					map.remove(str);
			}
		}

		if (map != null && map.size() > 0) {
			singleObjectBO.deleteByPKs(IcbalanceVO.class, map.values().toArray(new String[0]));
			errmsg.append("<p>成功删除" + map.size() + "条库存期初记录!</p>");
		}

		if (errmsg != null && errmsg.length() > 0) {
			return errmsg.toString();
		} else {
			return null;
		}
	}

	private void checkInventoryRef(String strids, String pk_corp, StringBuffer errmsg, List<String> errlist,
			Map<String, InventoryVO> invmap, String tablename, String msg) {

		StringBuffer sf = new StringBuffer();
		sf.append("select distinct pk_inventory from " + tablename
				+ " where pk_corp=? and nvl(dr,0) = 0 and pk_inventory in ( ").append(strids).append(" ) ");

		SQLParameter param = new SQLParameter();
		param.addParam(pk_corp);
		List<String> list = (List<String>) singleObjectBO.executeQuery(sf.toString(), param, new ColumnListProcessor());
		if (list != null && list.size() > 0) {
			for (String str : list) {
				InventoryVO invo = invmap.get(str);
				if (invo != null) {
					if (!errlist.contains(invo.getPk_inventory())) {
						errmsg.append("<p><font color='red'>存货[" + invo.getCode() + "]已被" + msg + "引用,不能删除库存期初!</font></p>");
						errlist.add(invo.getPk_inventory());
					}
				}
			}
		}

	}

	@Override
	public String save(String pk_corp, IcbalanceVO[] vos, String userid) throws DZFWarpException {
		if (vos == null || vos.length == 0)
			return "success";

		checkBeforeSave(pk_corp, vos);

		List<IcbalanceVO> list1 = new ArrayList<IcbalanceVO>();
		List<IcbalanceVO> list2 = new ArrayList<IcbalanceVO>();
		for (IcbalanceVO v : vos) {
			if (StringUtil.isEmpty(v.getPk_icbalance())) {
				list1.add(v);
			} else {
				list2.add(v);
			}
		}
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		DZFDate icdate = corpvo.getIcbegindate();
		if (list1.size() > 0) {
			for (IcbalanceVO icbvo : list1) {
				icbvo.setPk_corp(pk_corp);
				icbvo.setCreatetime(new DZFDateTime(new Date()).toString());
				icbvo.setCreator(userid);
				icbvo.setCreatedate(icdate);
				icbvo.setDbilldate(icdate.toString());
			}
			singleObjectBO.insertVOArr(pk_corp, list1.toArray(new IcbalanceVO[0]));
		}
		if (list2.size() > 0) {
			singleObjectBO.updateAry(vos);
		}
		return "success";
	}

	private void checkBeforeSave(String pk_corp, IcbalanceVO[] vos) {

		if (vos == null || vos.length == 0)
			throw new BusinessException("期初信息不完整,请检查!");

		IcbalanceVO ivo = vos[0];
		if (!pk_corp.equals(ivo.getPk_corp()))// 要进行操作的公司是否为登录公司
			throw new BusinessException("对不起，您无操作权限！");
		// 修改保存前数据安全验证
		IcbalanceVO getvo = queryByPrimaryKey(ivo.getPrimaryKey());
		if (getvo != null && !pk_corp.equals(getvo.getPk_corp())) {
			throw new BusinessException("出现数据无权问题，无法修改！");
		}
		checkIsCbjz(pk_corp);

		List<IcbalanceVO> listAll = quyerInfovoic(pk_corp);

		HashSet<String> pkSet = new HashSet<String>();
		for (IcbalanceVO vo : vos) {
			if (!StringUtil.isEmpty(vo.getPk_icbalance()))
				pkSet.add(vo.getPk_icbalance());
		}

		HashSet<String> codeSet = new HashSet<String>();
		if (listAll != null && listAll.size() != 0) {
			for (IcbalanceVO vo : listAll) {
				if (!pkSet.contains(vo.getPk_icbalance())) {
					codeSet.add(vo.getPk_inventory());
				}
			}
		}

		int i = 0;
		for (IcbalanceVO invo : vos) {
			// 检查编码是否已存在
			if (StringUtil.isEmpty(invo.getPk_inventory())) {
				throw new BusinessException("存货不能为空！");
			}

			if (invo.getNcost() == null) {
				throw new BusinessException("成本不能为空！");
			}

			if (invo.getNnum() == null) {
				throw new BusinessException("数量不能为空！");
			}

			Map<String, InventoryVO> invVOMap = queryInventory1(pk_corp);
			InventoryVO inventory = invVOMap.get(invo.getPk_inventory());
			if (inventory == null) {
				throw new BusinessException("第" + (i + 1) + "行的存货信息在系统中未找到!");
			}

			if (codeSet.contains(invo.getPk_inventory())) {
				throw new BusinessException("存货[" + inventory.getName() + "]已存在库存期初记录!");
			} else {
				codeSet.add(invo.getPk_inventory());
			}
		}
	}

	private Map<String, InventoryVO> queryInventory1(String pk_corp) {
		List<InventoryVO> invList = invservice.queryInfo(pk_corp,null);
		Map<String, InventoryVO> invMap = new HashMap<String, InventoryVO>();
		if (invList != null && invList.size() > 0) {
			String key = null;
			// StringBuffer sf = new StringBuffer();
			for (InventoryVO vo : invList) {
				key = vo.getPk_inventory();
				if (!invMap.containsKey(key)) {
					invMap.put(key, vo);
				}
			}
		}
		return invMap;
	}

	private void checkIsCbjz(String pk_corp) {
		// 取得当前公司的库存启用日期
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		DZFDate icbegindate = corpvo.getIcbegindate();
		if (icbegindate == null) {
			throw new BusinessException("当前公司库存启用日期为空!");
		}
		String period = DateUtils.getPeriod(icbegindate);
		SQLParameter sp = new SQLParameter();
		sp.addParam(period);
		sp.addParam(pk_corp);
		String sql = " select pk_qmcl from ynt_qmcl where period >=? and pk_corp = ?  and nvl(dr,0) = 0  and nvl(iscbjz,'N')='Y'";
		boolean boo = singleObjectBO.isExists(pk_corp, sql, sp);
		if (boo) {
			throw new BusinessException("在库存启用期间之后，已经成本结转，期初数据不可更改!");
		}
	}

	public void saveIcSync(String userid, String pk_corp) throws DZFWarpException {

		beforeSyncCheck(pk_corp);

		saveAssetSync(pk_corp, userid);
	}

	private void saveAssetSync(String pk_corp, String userid) throws DZFWarpException {

		CorpVO vo1 = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);

		List<IcbalanceVO> list = quyerInfovoic(pk_corp);
		if (list == null || list.size() == 0)
			return;

		String pk_curr = yntBoPubUtil.getCNYPk();
		List<QcYeVO> yelist = gl_qcyeserv.queryAllQcInfo(pk_corp, pk_curr, pk_curr);

		if (yelist == null || yelist.size() == 0)
			return;

		Map<String, QcYeVO> maps = hashlizeObject(yelist.toArray(new QcYeVO[yelist.size()]));

		Map<String, List<FzhsqcVO>> map = new HashMap<>();
		for (IcbalanceVO balvo : list) {
			String pk_account = balvo.getPk_subject();
			if (IGlobalConstants.DefaultGroup.equals(balvo.getPk_corp())) {// 集团级数据
				pk_account = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(balvo.getPk_subject(), pk_corp);
			}
			YntCpaccountVO vo = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, pk_account);
			if (vo.getIsleaf() == null || !vo.getIsleaf().booleanValue()) {
				throw new BusinessException("科目:" + vo.getAccountname() + "，为非末级科目 ！");
			}
			FzhsqcVO fzhsqcVO = new FzhsqcVO();
			fzhsqcVO.setBnqcnum(balvo.getNnum());
			fzhsqcVO.setYearqc(balvo.getNcost());
			fzhsqcVO.setYbthismonthqc(DZFDouble.ZERO_DBL);
			fzhsqcVO.setCoperatorid(userid);
			fzhsqcVO.setDoperatedate(balvo.getCreatedate());
			fzhsqcVO.setPk_corp(balvo.getPk_corp());
			fzhsqcVO.setPk_accsubj(balvo.getPk_subject());
			fzhsqcVO.setVcode(balvo.getPk_subjectcode() + "_" + balvo.getInventorycode());
			fzhsqcVO.setVname(balvo.getPk_subjectname() + "_" + balvo.getInventoryname());
			fzhsqcVO.setDirect(vo.getDirection());
			fzhsqcVO.setVlevel(vo.getAccountlevel());
			fzhsqcVO.setPk_currency(pk_curr);
			fzhsqcVO.setVyear(balvo.getCreatedate().getYear());
			fzhsqcVO.setPeriod(DateUtils.getPeriod(balvo.getCreatedate()));
			fzhsqcVO.setFzhsx6(balvo.getPk_inventory());

			List<FzhsqcVO> list1 = new ArrayList<>();
			if (map.containsKey(balvo.getPk_subjectcode())) {
				list1 = map.get(balvo.getPk_subjectcode());

			} else {
				list1 = new ArrayList<>();
			}
			list1.add(fzhsqcVO);
			map.put(balvo.getPk_subjectcode(), list1);
		}

		if (map == null || map.size() == 0)
			return;
		CorpVO corp = corpService.queryByPk(pk_corp);
		UserVO user = userServiceImpl.queryUserJmVOByID(userid);

		for (Map.Entry<String, List<FzhsqcVO>> entry : map.entrySet()) {
			String pk_account = entry.getKey();
			QcYeVO qcyevo = maps.get(pk_account);
			List<FzhsqcVO> list1 = entry.getValue();
			DZFDouble newvalue = DZFDouble.ZERO_DBL;
			DZFDouble newvalue1 = DZFDouble.ZERO_DBL;
			if (list1 != null && list1.size() > 0) {
				saveFzQc(list1.get(0).getPk_accsubj(), list1.toArray(new FzhsqcVO[list1.size()]), pk_curr, user, corp);
				for (FzhsqcVO fzvo : list1) {
					newvalue = SafeCompute.add(newvalue, fzvo.getYearqc());
					newvalue1 = SafeCompute.add(newvalue1, fzvo.getBnqcnum());
				}
			}
			saveIncAccMny(pk_curr, vo1, pk_corp, maps, qcyevo.getVcode(), qcyevo.getYearqc(), qcyevo.getBnqcnum(),
					newvalue, newvalue1, singleObjectBO);
		}
	}

	private void saveFzQc(String pk_accsubj, FzhsqcVO[] fzvos, String currency, UserVO user, CorpVO corp)
			throws DZFWarpException {
		if (fzvos != null && fzvos.length > 0) {
			checkVos(pk_accsubj, currency, fzvos, user, corp);
		}

		if (fzvos != null && fzvos.length > 0) {
			for (FzhsqcVO fzqc : fzvos) {
				calFzQc(fzqc);
			}
		}
		String sql = "delete from ynt_fzhsqc where pk_corp = ? and pk_accsubj = ? and pk_currency = ?  and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(corp.getPk_corp());
		sp.addParam(pk_accsubj);
		sp.addParam(currency);
		singleObjectBO.executeUpdate(sql, sp);
		singleObjectBO.insertVOArr(corp.getPk_corp(), fzvos);
	}

	private void checkVos(String pk_accsubj, String currency, FzhsqcVO[] fzvos, UserVO user, CorpVO corp) {
		Set<String> fzhsCombo = new HashSet<String>();
		String pk_corp = corp.getPk_corp();
		Map<String, YntCpaccountVO> kmMap = accountService.queryMapByPk(pk_corp);
		YntCpaccountVO account = kmMap.get(pk_accsubj);
		for (FzhsqcVO fzhsqcVO : fzvos) {
			// fzhsqcVO.setVcode(account.getAccountcode());
			// fzhsqcVO.setVname(account.getAccountname());
			fzhsqcVO.setVlevel(account.getAccountlevel());
			fzhsqcVO.setDirect(account.getDirection());
			fzhsqcVO.setPk_currency(currency);
			fzhsqcVO.setIsfzhs(account.getIsfzhs());
			DZFDate jzdate = corp.getBegindate();
			fzhsqcVO.setDoperatedate(jzdate);
			fzhsqcVO.setCoperatorid(user.getCuserid());
			fzhsqcVO.setDr(0);
			fzhsqcVO.setPk_accsubj(pk_accsubj);
			fzhsqcVO.setPk_corp(pk_corp);
			String combo = ReportUtil.getFzKey(fzhsqcVO);
			if (fzhsCombo.contains(combo)) {
				throw new BusinessException("辅助核算组合重复！");
			}
			fzhsCombo.add(combo);
		}

	}

	private void calFzQc(FzhsqcVO fzqc) {
		if (fzqc == null)
			return;
		DZFDouble qm = null;
		DZFDouble qcnum = null;
		DZFDouble ybqm = null;
		if (fzqc.getDirect() == 0) {// 借方
			qm = SafeCompute.sub(SafeCompute.add(fzqc.getYearqc(), fzqc.getYearjffse()), fzqc.getYeardffse());
			ybqm = SafeCompute.sub(SafeCompute.add(fzqc.getYbyearqc(), fzqc.getYbyearjffse()), fzqc.getYbyeardffse());
			qcnum = SafeCompute.sub(SafeCompute.add(fzqc.getBnqcnum(), fzqc.getBnfsnum()), fzqc.getBndffsnum());
		} else {// 贷方
			qm = SafeCompute.sub(SafeCompute.add(fzqc.getYearqc(), fzqc.getYeardffse()), fzqc.getYearjffse());
			ybqm = SafeCompute.sub(SafeCompute.add(fzqc.getYbyearqc(), fzqc.getYbyeardffse()), fzqc.getYbyearjffse());
			qcnum = SafeCompute.sub(SafeCompute.add(fzqc.getBnqcnum(), fzqc.getBndffsnum()), fzqc.getBnfsnum());
		}
		fzqc.setThismonthqc(qm);
		fzqc.setYbthismonthqc(ybqm);
		fzqc.setMonthqmnum(qcnum);
	}

	public void doOneRowCalc(QcYeVO qc) {
		if (qc == null)
			return;
		DZFDouble qm = null;
		DZFDouble qcnum = null;
		DZFDouble ybqm = null;
		if (qc.getDirect() == 0) {// 借方
			qm = SafeCompute.sub(SafeCompute.add(qc.getYearqc(), qc.getYearjffse()), qc.getYeardffse());
			ybqm = SafeCompute.sub(SafeCompute.add(qc.getYbyearqc(), qc.getYbyearjffse()), qc.getYbyeardffse());
			qcnum = SafeCompute.sub(SafeCompute.add(qc.getBnqcnum(), qc.getBnfsnum()), qc.getBndffsnum());
		} else {// 贷方
			qm = SafeCompute.sub(SafeCompute.add(qc.getYearqc(), qc.getYeardffse()), qc.getYearjffse());
			ybqm = SafeCompute.sub(SafeCompute.add(qc.getYbyearqc(), qc.getYbyeardffse()), qc.getYbyearjffse());
			qcnum = SafeCompute.sub(SafeCompute.add(qc.getBnqcnum(), qc.getBndffsnum()), qc.getBnfsnum());
		}
		qc.setThismonthqc(qm);
		qc.setYbthismonthqc(ybqm);
		qc.setMonthqmnum(qcnum);
	}

	private void saveIncAccMny(String pk_curr, CorpVO vo1, String pk_corp, Map<String, QcYeVO> maps, String acccode,
			DZFDouble oldvalue, DZFDouble oldvalue1, DZFDouble newvalue, DZFDouble newvalue1,
			SingleObjectBO singleObjectBO) throws DZFWarpException {
		if (acccode == null || "".equals(acccode))
			return;
		QcYeVO qcyevo = maps.get(acccode);
		if (qcyevo != null) {
			DZFDate jzdate = vo1.getBegindate();
			qcyevo.setChildren(null);
			qcyevo.setDoperatedate(vo1.getBegindate());
			qcyevo.setPk_currency(pk_curr);

			qcyevo.setYearqc(SafeCompute.add(SafeCompute.sub(qcyevo.getYearqc(), oldvalue), newvalue));
			qcyevo.setBnqcnum(SafeCompute.add(SafeCompute.sub(qcyevo.getBnqcnum(), oldvalue1), newvalue1));
			if (jzdate != null) {
				qcyevo.setVyear(Integer.valueOf(jzdate.toString().substring(0, 4)));
				qcyevo.setPeriod(jzdate.toString().substring(0, 7));
			}
			doOneRowCalc(qcyevo);
			if (qcyevo.getPk_qcye() == null) {
				if (qcyevo.getThismonthqc().compareTo(DZFDouble.ZERO_DBL) > 0
						|| qcyevo.getYearqc().compareTo(DZFDouble.ZERO_DBL) > 0) {
					singleObjectBO.saveObject(pk_corp, qcyevo);
				}
			} else {
				singleObjectBO.update(qcyevo);
			}
			// getHYPubBO().update(qcyevo);

			String parentAcccode = getParentAccCode(qcyevo.getVcode(), pk_corp);
			saveIncAccMny(pk_curr, vo1, pk_corp, maps, parentAcccode, oldvalue, oldvalue1, newvalue, newvalue1,
					singleObjectBO);
		}
	}

	// 计算取得上级科目（zpm 修改）

	public String getParentAccCode(String childCode, String pk_corp) {
		String coderule = getCodeRule(pk_corp);
		return DZfcommonTools.getParentCode(childCode, coderule);
	}

	public String getCodeRule(String pk_corp) {
		String accountrule = cpaccountService.queryAccountRule(pk_corp);
		return accountrule;
	}

	public Map<String, QcYeVO> hashlizeObject(QcYeVO[] qcyevos) throws DZFWarpException {
		Map<String, QcYeVO> result = new HashMap<String, QcYeVO>();
		if (qcyevos == null || qcyevos.length == 0)
			return result;
		String key = null;
		for (int i = 0; i < qcyevos.length; i++) {
			key = qcyevos[i].getVcode();
			result.put(key, qcyevos[i]);
		}
		return result;
	}

	// 同步前校验
	private void beforeSyncCheck(String pk_corp) throws DZFWarpException {
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		DZFDate icbegindate = corpvo.getIcbegindate();
		DZFDate begindate = corpvo.getBegindate();

		if (begindate == null) {
			throw new BusinessException("当前公司建账日期为空!");
		}
		if (icbegindate == null) {
			throw new BusinessException("当前公司库存启用日期为空!");
		}

		if (!begindate.equals(icbegindate)) {
			// throw new BusinessException("总账启用日期和库存启用日期不一致,不支持同步期初!");
		}

		boolean isgz = gzservice.checkLaterMonthGz(pk_corp, DateUtils.getPeriod(begindate));

		if (isgz) {
			throw new BusinessException("已关账，不能期初同步!");
		}

		checkIsCbjz(pk_corp);
	}

	@Override
	public void saveGL2KcSync(String userid, String pk_corp, List<FzhsqcVO> fzhsqcList, StringBuffer msg)
			throws DZFWarpException {

		beforeSyncCheck(pk_corp);

		saveGL2KcSyncInfor(pk_corp, userid, fzhsqcList, msg);
	}

	private void saveGL2KcSyncInfor(String pk_corp, String userid, List<FzhsqcVO> fzhsqcList, StringBuffer msg)
			throws DZFWarpException {

		Map<String, FzhsqcVO> fzhsMap = DZfcommonTools.hashlizeObjectByPk(fzhsqcList,
				new String[] { "pk_accsubj", "fzhsx6" });// 总账辅助期初

		List<IcbalanceVO> icbalList = quyerInfovoic(pk_corp);// 库存期初

		Map<String, IcbalanceVO> icbalMap = DZfcommonTools.hashlizeObjectByPk(icbalList,
				new String[] { "pk_subject", "pk_inventory" });

		List<InventoryVO> invenList = invservice.queryInfo(pk_corp,null);

		Map<String, InventoryVO> invenMap = DZfcommonTools.hashlizeObjectByPk(invenList,
				new String[] { "pk_subject", "pk_inventory" });// 存货档案

		String key;
		FzhsqcVO fzhsqcvo;
		IcbalanceVO icbalancevo;
		int errCount = 0;
		int sucCount = 0;
		List<IcbalanceVO> list1 = new ArrayList<IcbalanceVO>();
		List<IcbalanceVO> list2 = new ArrayList<IcbalanceVO>();
		for (Map.Entry<String, FzhsqcVO> entry : fzhsMap.entrySet()) {
			key = entry.getKey();
			fzhsqcvo = entry.getValue();

			if (!invenMap.containsKey(key)) {
				throw new BusinessException(
						String.format("库存期初同步时发现项目[%s]不符合规范，请检查", new String[] { fzhsqcvo.getVcode() }));
			}

			if (icbalMap.containsKey(key)) {
				icbalancevo = getAfterUpdateIcBalanceVO(fzhsqcvo, icbalMap.get(key));
				if (icbalancevo == null || icbalancevo.getNnum() == null) {
					errCount++;
				} else {
					list2.add(icbalancevo);
					sucCount++;
				}

			} else {
				list1.add(getInsertIcBalanceVO(fzhsqcvo, pk_corp, userid));
				sucCount++;
			}

		}

		if (list1.size() > 0) {
			singleObjectBO.insertVOWithPK(pk_corp, list1.toArray(new IcbalanceVO[0]));
		}

		if (list2.size() > 0) {
			singleObjectBO.updateAry(list2.toArray(new IcbalanceVO[0]), new String[] { "nnum", "ncost" });
		}

		if (errCount > 0) {
			msg.append(String.format("<p>保存成功%d个,失败%d个,请添加“本年期初数量”</p>", new Integer[] { sucCount, errCount }));
		}

	}

	private IcbalanceVO getAfterUpdateIcBalanceVO(FzhsqcVO fzhsqcvo, IcbalanceVO balancevo) {

		balancevo.setNnum(fzhsqcvo.getBnqcnum());// 数量
		balancevo.setNcost(fzhsqcvo.getYearqc());// 成本

		return balancevo;
	}

	private IcbalanceVO getInsertIcBalanceVO(FzhsqcVO fzhsqcvo, String pk_corp, String userid) {
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		DZFDate icdate = corpvo.getIcbegindate();

		IcbalanceVO addvo = new IcbalanceVO();

		addvo.setPk_corp(pk_corp);
		addvo.setCreatetime(new DZFDateTime(new Date()).toString());
		addvo.setCreator(userid);
		addvo.setCreatedate(icdate);
		addvo.setDbilldate(icdate.toString());

		addvo.setNnum(fzhsqcvo.getBnqcnum());// 数量
		addvo.setNcost(fzhsqcvo.getYearqc());// 成本

		addvo.setPk_inventory(fzhsqcvo.getFzhsx6());
		addvo.setPk_subject(fzhsqcvo.getPk_accsubj());

		return addvo;
	}
}
