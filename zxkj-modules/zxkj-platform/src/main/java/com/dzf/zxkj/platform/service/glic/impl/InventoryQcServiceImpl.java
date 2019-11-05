package com.dzf.zxkj.platform.service.glic.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.query.QueryCondictionVO;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.glic.InventoryQcVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.report.NumMnyGlVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.glic.IInventoryQcService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("gl_ic_invtoryqcserv")
public class InventoryQcServiceImpl implements IInventoryQcService {

	@Autowired
	private SingleObjectBO singleObjectBO;
//	@Reference(version = "1.0.0")
//	INummnyReport gl_rep_nmdtserv;
	@Autowired
	IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private IParameterSetService sys_parameteract;
	@Autowired
	private CheckInventorySet  inventory_setcheck;
	@Autowired
	private IInventoryAccSetService gl_ic_invtorysetserv;
	@Autowired
	private ICorpService corpService;
	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryQcVO> query(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);

		StringBuilder sql = new StringBuilder();
		sql.append("select qc.*, inv.code as chbm, inv.name as chmc,")
				.append("inv.spec as spec, inv.unit as jldw, ")
				.append(" acc.accountcode as chlbbm, acc.accountname as chlb from ynt_glicqc qc")
				.append(" left join ynt_fzhs_b inv on inv.pk_auacount_b = qc.pk_inventory")
				.append(" left join ynt_cpaccount acc on acc.pk_corp_account = inv.kmclassify")
				.append(" where qc.pk_corp = ? and nvl(qc.dr,0) = 0 order by inv.code");
		List<InventoryQcVO> vos = (List<InventoryQcVO>) singleObjectBO
				.executeQuery(sql.toString(), sp, new BeanListProcessor(
						InventoryQcVO.class));
		return vos;
	}

	@Override
	public InventoryQcVO save(String userid,String pk_corp,InventoryQcVO vo) throws DZFWarpException {
		check(userid,pk_corp,vo);
		if (!StringUtil.isEmpty(vo.getPk_icqc())) {
			singleObjectBO.update(vo, new String[] { "pk_inventory",
					"thismonthqc", "monthqmnum", "monthqc_price","memo" });
		} else {
			vo.setCoperatorid(userid);
			singleObjectBO.saveObject(vo.getPk_corp(), vo);
		}
		return vo;
	}

	private void check(String userid,String pk_corp,InventoryQcVO vo) throws DZFWarpException {
		if (vo.getThismonthqc() == null) {
			vo.setThismonthqc(DZFDouble.ZERO_DBL);
		}
		//由于四舍五入的原因，估计会报此错误
//		if (!SafeCompute.multiply(vo.getMonthqmnum(), vo.getMonthqc_price())
//				.setScale(2, DZFDouble.ROUND_HALF_UP).equals(vo.getThismonthqc())) {
//			throw new BusinessException("数量*单价不等于金额！");
//		}

		checkInventorySet(userid, pk_corp,vo.getPk_inventory());
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getPk_inventory());
		StringBuilder sql = new StringBuilder();
		sql.append(
				"select 1 from ynt_glicqc where pk_corp = ? and pk_inventory = ? ")
				.append(" and nvl(dr, 0) = 0 ");
		if (!StringUtil.isEmpty(vo.getPk_icqc())) {
			sql.append(" and pk_icqc <> ? ");
			sp.addParam(vo.getPk_icqc());
		}
		boolean isExist = singleObjectBO.isExists(vo.getPk_corp(),
				sql.toString(), sp);
		if (isExist) {
			throw new BusinessException("所选存货已存在期初！");
		}
		checkDate(vo);
	}

	/**
	 * 校验存货设置
	 * @param userid
	 * @param pk_corp
	 */
	private void checkInventorySet(String userid, String pk_corp,String pk_inventory) {
		// 增加校验
		InventorySetVO setvo = gl_ic_invtorysetserv.query(pk_corp);
		if (setvo == null) {
			throw new BusinessException("请先进行存货设置");
		}
		String error = inventory_setcheck.checkInventorySet(userid, pk_corp,
				setvo,pk_inventory);
		if (!StringUtil.isEmpty(error)) {
			throw new BusinessException(error);
		}
	}

	/**
	 * 检查启用期间是否一致
	 * 
	 * @param vo
	 * @throws DZFWarpException
	 */

	private void checkDate(InventoryQcVO vo) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getDoperatedate());
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ynt_glicqc where pk_corp = ? ").append(
				" and nvl(dr, 0) = 0 and doperatedate <> ?");
		if (!StringUtil.isEmpty(vo.getPk_icqc())) {
			sql.append(" and pk_icqc <> ? ");
			sp.addParam(vo.getPk_icqc());
		}
		boolean isExist = singleObjectBO.isExists(vo.getPk_corp(),
				sql.toString(), sp);
		if (isExist) {
			throw new BusinessException("启用期间与已录入期初期间不一致！");
		}
	}

	@Override
	public void delete(InventoryQcVO[] vos) throws DZFWarpException {
		singleObjectBO.deleteVOArray(vos);
	}

	@Override
	public void processSyncData(String userid, String pk_corp, String date)
			throws DZFWarpException {
		checkInventorySet(userid, pk_corp,null);
		// 删除已存在数据
		String deleteSql = "update ynt_glicqc set dr = 1 where pk_corp = ? and nvl(dr,0 )=0";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		singleObjectBO.executeUpdate(deleteSql, sp);
		String period = date.substring(0, 7);
		CorpVO corpVo = corpService.queryByPk(pk_corp);
		QueryCondictionVO paramVo = new QueryCondictionVO();
		paramVo.setJzdate(corpVo.getBegindate());
		paramVo.setIsic(new DZFBoolean(IcCostStyle.IC_ON.equals(corpVo
				.getBbuildic())));
		paramVo.setQjq(period);
		paramVo.setQjz(period);
		paramVo.setKms_first("1405");
		paramVo.setKms_last("1406");
		paramVo.setIsfzhs(DZFBoolean.TRUE);
		paramVo.setPk_corp(pk_corp);
		List<NumMnyGlVO> numVos = null;
//				gl_rep_nmdtserv.getNumMnyGlVO(paramVo);
		if (numVos != null && numVos.size() > 0) {
			Map<String, InventoryQcVO> qcMap = new HashMap<String, InventoryQcVO>();
			for (NumMnyGlVO numVo : numVos) {
				String pk_inventory = numVo.getFzhsx6();
				if (!StringUtil.isEmpty(pk_inventory)
						&& numVo.getQcnum() != null
						&& numVo.getQcnum().doubleValue() != 0) {
					InventoryQcVO qcVo = null;
					if (qcMap.containsKey(pk_inventory)) {
						qcVo = qcMap.get(pk_inventory);
					} else {
						qcVo = new InventoryQcVO();
						qcVo.setPk_corp(pk_corp);
						qcVo.setPk_inventory(pk_inventory);
						qcVo.setDoperatedate(new DZFDate(date));
						qcVo.setCoperatorid(userid);
						qcMap.put(pk_inventory, qcVo);
					}
					qcVo.setMonthqmnum(SafeCompute.add(qcVo.getMonthqmnum(),
							numVo.getQcnum()));
					qcVo.setMonthqc_price(SafeCompute.add(
							qcVo.getMonthqc_price(), numVo.getQcprice()));
					qcVo.setThismonthqc(SafeCompute.add(qcVo.getThismonthqc(),
							numVo.getQcmny()));
				}
			}
			if (qcMap.size() > 0) {
				InventoryQcVO[] insertVos = qcMap.values().toArray(
						new InventoryQcVO[0]);
				singleObjectBO.insertVOArr(pk_corp, insertVos);
			}
		}
	}

	@Override
	public void updateDate(String pk_corp, String date) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		String sql = "update YNT_GLICQC set doperatedate = ? where pk_corp = ? and nvl(dr,0)=0";
		sp.addParam(date);
		sp.addParam(pk_corp);
		singleObjectBO.executeUpdate(sql, sp);
	}

	@Override
	public String processImportExcel(CorpVO corp, String userid, String fileType,
			File impFile, String date) throws DZFWarpException {
		checkInventorySet(userid, corp.getPk_corp(),null);
		StringBuilder msg = new StringBuilder();
		FileInputStream is = null;
		try {
			is = new FileInputStream(impFile);
			Workbook impBook = null;
			if ("xls".equals(fileType)) {
				impBook = new HSSFWorkbook(is);
			} else if ("xlsx".equals(fileType)) {
				impBook = new XSSFWorkbook(is);
			} else {
				throw new BusinessException("不支持的文件格式 ");
			}
			AuxiliaryAccountBVO[] invVos = gl_fzhsserv.queryB(
					AuxiliaryConstant.ITEM_INVENTORY, corp.getPk_corp(), null);
			if (invVos == null || invVos.length == 0) {
				throw new BusinessException("当前公司没有存货档案");
			}
			Map<String, AuxiliaryAccountBVO> invMap = new HashMap<String, AuxiliaryAccountBVO>();
			for (AuxiliaryAccountBVO inv : invVos) {
				if (invMap.containsKey(inv.getName())) {
					continue;
				}
				StringBuilder key = new StringBuilder();
				key.append(inv.getName()).append(",")
					.append(inv.getSpec() == null ? "" : inv.getSpec()).append(",")
					.append(inv.getUnit() == null ? "" : inv.getUnit());
				invMap.put(key.toString(), inv);
			}
			Map<String, InventoryQcVO> existQcMap = new HashMap<String, InventoryQcVO>();
			InventoryQcVO[] existQcs = getAllInventoryQc(corp.getPk_corp());
			if (existQcs != null && existQcs.length > 0) {
				if (!existQcs[0].getDoperatedate().toString().equals(date)) {
					throw new BusinessException("启用期间与已录入期初期间不一致");
				}
				for (InventoryQcVO inventoryQcVO : existQcs) {
					existQcMap.put(inventoryQcVO.getPk_inventory(), inventoryQcVO);
				}
			}
			Sheet sheet = impBook.getSheetAt(0);
			Row headRow = sheet.getRow(0);
			Map<String, Integer> headMap = new HashMap<String, Integer>();
			int colNum = headRow.getLastCellNum();
			for (int i = 0; i <= colNum; i++) {
				Cell cell = headRow.getCell(i);
				if (cell == null) {
					break;
				}
				String val = cell.getRichStringCellValue().getString();
				if (!StringUtil.isEmptyWithTrim(val)) {
					val = val.trim();
					headMap.put(val, i);
				}
			}

			int numPrecision = Integer.valueOf(sys_parameteract
					.queryParamterValueByCode(corp.getPk_corp(), "dzf009"));
			int pricePrecision = Integer.valueOf(sys_parameteract
					.queryParamterValueByCode(corp.getPk_corp(), "dzf010"));
			List<InventoryQcVO> qcList = new ArrayList<InventoryQcVO>();
			Set<String> nameSet = new HashSet<String>();
			int length = sheet.getLastRowNum();
			for (int iBegin = 1; iBegin <= length; iBegin++) {
				Row row = sheet.getRow(iBegin);
				String name = getCellValue(row, iBegin, getHeadIndex(headMap, "存货名称"),
						false);
				if (StringUtil.isEmptyWithTrim(name)) {
					continue;
				}
				String spec = getCellValue(row, iBegin, getHeadIndex(headMap, "规格（型号）"),
						false);
				String unit = getCellValue(row, iBegin, getHeadIndex(headMap, "计量单位"),
						false);
				StringBuilder identify = new StringBuilder();
				identify.append(name).append(",").append(spec == null ? "" : spec).append(",")
					.append(unit == null ? "" : unit);
				String identifyStr = identify.toString();
				if (nameSet.contains(identifyStr)) {
					msg.append("第").append(iBegin + 1).append("行存货（").append(name).append("）重复<br>");
					continue;
				}
				AuxiliaryAccountBVO inv = invMap.get(identifyStr);
				if (inv == null) {
					msg.append("第").append(iBegin + 1).append("行存货（").append(name).append("）不存在<br>");
					continue;
				}
				if (existQcMap.containsKey(inv.getPk_auacount_b())) {
					msg.append("第").append(iBegin + 1).append("行存货（").append(name).append("）已存在期初数据<br>");
					continue;
				}
				String numStr = getCellValue(row, iBegin, getHeadIndex(headMap, "数量"),
						true);
				String mnyStr = getCellValue(row, iBegin, getHeadIndex(headMap, "金额"),
						true);
				DZFDouble num = new DZFDouble(numStr, numPrecision);
				if (num.doubleValue() == 0) {
					msg.append("第").append(iBegin + 1).append("行数量为空<br>");
					continue;
				}
				DZFDouble mny = new DZFDouble(mnyStr, 2);
				DZFDouble price = DZFDouble.ZERO_DBL;
				if (mny.doubleValue() != 0) {
					price = mny.div(num, pricePrecision);
				}
				String memo = getCellValue(row, iBegin, getHeadIndex(headMap, "备注"), false);
				
				InventoryQcVO qcVo = new InventoryQcVO();
				qcVo.setPk_corp(corp.getPk_corp());
				qcVo.setPk_inventory(inv.getPk_auacount_b());
				qcVo.setDoperatedate(new DZFDate(date));
				qcVo.setCoperatorid(userid);
				qcVo.setMonthqmnum(num);
				qcVo.setMonthqc_price(price);
				qcVo.setThismonthqc(mny);
				if (!StringUtil.isEmptyWithTrim(memo)) {
					qcVo.setMemo(memo.trim());
				}
				qcList.add(qcVo);
				nameSet.add(identifyStr);
			}
			int addCount = qcList.size();
			msg.insert(0, "成功导入" + addCount + "条数据<br>");
			if (addCount > 0) {
				InventoryQcVO[] insertVos = qcList
						.toArray(new InventoryQcVO[0]);
				singleObjectBO.insertVOArr(corp.getPk_corp(), insertVos);
			}
		} catch (FileNotFoundException e) {
			throw new BusinessException("导入文件未找到 ");
		} catch (IOException e) {
			throw new BusinessException("导入文件格式错误");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}

		return msg.toString();
	}
	private int getHeadIndex(Map<String, Integer> headMap, String headName) {
		if (headMap.containsKey(headName)) {
			return headMap.get(headName);
		} else {
			throw new BusinessException(headName + "列不存在，请检查模板！");
		}
	}
	
	private InventoryQcVO[] getAllInventoryQc(String pk_corp) {
		String condition = " pk_corp = ? and nvl(dr, 0) = 0";
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		InventoryQcVO[] vos = (InventoryQcVO[]) singleObjectBO
				.queryByCondition(InventoryQcVO.class, condition, params);
		return vos;
	}

	private String getCellValue(Row row, int rowNum, int colNum, boolean isNum) {
		String val = null;
		Cell cell = row.getCell(colNum);
		if (cell == null) {
			return val;
		}
		if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
			val = cell.getRichStringCellValue().getString();
			val = val.replaceAll("^( | )+|( | )+$", "");
		} else if (HSSFDateUtil.isCellDateFormatted(cell)) {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			val = dateFormatter.format(HSSFDateUtil.getJavaDate(cell
					.getNumericCellValue()));
		} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
			DecimalFormat format = isNum ? new DecimalFormat("#.########")
					: new DecimalFormat("#");
			double numVal = cell.getNumericCellValue();
			val = format.format(numVal);
		}

		return val;
	}

	@Override
	public DZFDate queryInventoryQcDate(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuilder sql = new StringBuilder();
		sql.append(" select qc.doperatedate from ynt_glicqc qc");
		sql.append(" where qc.pk_corp = ? and nvl(qc.dr,0) = 0 ");
		DZFDate date = (DZFDate)singleObjectBO.executeQuery(sql.toString(), sp, new ResultSetProcessor(){
			DZFDate doperatedate = null;
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while(rs.next()){
					String date = rs.getString("doperatedate");
					if(!StringUtil.isEmpty(date)){
						doperatedate = new DZFDate(date);
						break;
					}
				}
				return doperatedate;
			}
		});
		return date;
	}
}