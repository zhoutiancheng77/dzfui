package com.dzf.zxkj.platform.service.icset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.base.utils.FieldMapping;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.icset.MeasureVO;
import com.dzf.zxkj.platform.service.icset.IMeasureService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
@Slf4j
@Service("ic_meausreserv")
public class MeasureServiceImpl implements IMeasureService {

	private SingleObjectBO singleObjectBO = null;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	@Autowired
	private IYntBoPubUtil yntBoPubUtil;

	public List<MeasureVO> quyerByPkcorp(String pk_corp, String sort, String order) throws DZFWarpException {
		// try {
		SQLParameter sp = new SQLParameter();
		StringBuffer sb = new StringBuffer();
		sb.append("pk_corp=? and nvl(dr,0)=0");
		sp.addParam(pk_corp);
		
		List<MeasureVO> listVo = (List<MeasureVO>) singleObjectBO.retrieveByClause(MeasureVO.class, sb.toString(), sp);
		if (!StringUtil.isEmpty(sort)) {// sort != null && !"".equals(sort)
			String sortb = FieldMapping.getFieldNameByAlias(new MeasureVO(), sort);
			sortb = StringUtil.isEmpty(sortb)? sort : sortb;
			int flag = VOUtil.DESC;
			if("asc".equalsIgnoreCase(order)){
				flag = VOUtil.ASC;
			}
			VOUtil.sort(listVo, new String[]{sortb}, new int[]{flag});
		}
		
		return listVo;
	}

	@Override
	public void delete(MeasureVO vo) throws DZFWarpException {
		String sql = "select 1 from ynt_inventory where pk_corp=? and  pk_measure=? and nvl(dr,0)=0 ";
		SQLParameter param = new SQLParameter();
		param.addParam(vo.getPk_corp());
		param.addParam(vo.getPk_measure());
		// List<SuperVO> measureList =
		// (List<SuperVO>)getSingleObjectBO().executeQuery(sql, param, new
		// BeanListProcessor(MeasureVO.class));
		boolean b = singleObjectBO.isExists(vo.getPk_corp(), sql, param);
		// if(null != measureList && measureList.size() >0){
		// throw new BusinessException("计量单位被引用，不能删除！");
		// }else{
		if (b == true) {
			throw new BusinessException("计量单位被引用，不能删除！");
		} else {
			singleObjectBO.deleteObjectByID(vo.getPrimaryKey(), new Class[] { MeasureVO.class });
		}
	}

	public MeasureVO queryByPrimaryKey(String PrimaryKey) throws DZFWarpException {
		SuperVO vo = getSingleObjectBO().queryByPrimaryKey(MeasureVO.class, PrimaryKey);
		return (MeasureVO) vo;
	}

	public String isExistPk(MeasureVO vo) throws DZFWarpException {

		// String sql = new
		// String("SELECT PK_MEASURE FROM YNT_MEASURE WHERE (CODE = ? OR NAME =
		// ? )AND PK_CORP = ? and nvl(dr,0)=0 ");
		StringBuffer sf = new StringBuffer();
		sf.append("SELECT PK_MEASURE FROM YNT_MEASURE WHERE (CODE = ? OR NAME = ? )AND PK_CORP = ? and nvl(dr,0)=0 ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getCode());
		sp.addParam(vo.getName());
		sp.addParam(vo.getPk_corp());

		if (!StringUtil.isEmpty(vo.getPrimaryKey())) {
			// sql=sql+" AND PK_MEASURE !=?";
			sf.append(" AND PK_MEASURE !=? ");
			sp.addParam(vo.getPrimaryKey());
		}

		// String[] pks = (String[]) getSingleObjectBO().executeQuery(sql, sp,
		// new ColumnProcessor());
		Object obj = getSingleObjectBO().executeQuery(sf.toString(), sp, new ColumnProcessor());

		if (obj != null && !"".equals(obj)) {
			return (String) obj;
		}
		return null;
	}

	@Override
	public void updateVOArr(String pk_corp, String cuserid, String sort, String order, List<MeasureVO> list)
			throws DZFWarpException {

		String k = null;
		if (list == null || list.size() == 0)
			return;

		checkBeforeSave(pk_corp, sort, order, list);

		List<MeasureVO> listNew = new ArrayList<MeasureVO>();
		for (MeasureVO vo : list) {
			k = vo.getPrimaryKey();
			if (StringUtil.isEmpty(k)) {
				setDefaultValue(vo, pk_corp, cuserid);
				listNew.add(vo);
			}
		}

		if (listNew != null && listNew.size() > 0) {
			MeasureVO[] vos = listNew.toArray(new MeasureVO[0]);
			for (MeasureVO msvo : vos) {
				if (msvo == null) {
					throw new BusinessException("数据为空不能添加");
				}
			}
			singleObjectBO.insertVOArr(vos[0].getPk_corp(), vos);
		} else if (list != null && list.size() > 0) {
			MeasureVO[] vos = list.toArray(new MeasureVO[0]);
			singleObjectBO.updateAry(vos);
		}
	}

	@Override
	public MeasureVO[] savenNewVOArr(String pk_corp, String cuserid, String sort, String order, List<MeasureVO> list)
			throws DZFWarpException {

		String k = null;
		if (list == null || list.size() == 0)
			return null;

		checkBeforeSave(pk_corp, sort, order, list);

		List<MeasureVO> listNew = new ArrayList<MeasureVO>();
		for (MeasureVO vo : list) {
			k = vo.getPrimaryKey();
			if (StringUtil.isEmpty(k)) {
				setDefaultValue(vo, pk_corp, cuserid);
				listNew.add(vo);
			}
		}

		if (listNew != null && listNew.size() > 0) {
			MeasureVO[] vos = listNew.toArray(new MeasureVO[0]);
			for (MeasureVO msvo : vos) {
				if (msvo == null) {
					throw new BusinessException("数据为空不能添加");
				}
			}
			singleObjectBO.insertVOArr(vos[0].getPk_corp(), vos);
		} else if (list != null && list.size() > 0) {
			MeasureVO[] vos = list.toArray(new MeasureVO[0]);
			singleObjectBO.updateAry(vos);
		}

		List<String> list3 = new ArrayList<String>();
		for (MeasureVO vo : list) {
			list3.add(vo.getPrimaryKey());
		}
		String condition = SqlUtil.buildSqlForIn("pk_measure", list3.toArray(new String[list3.size()]));

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select  * from ynt_measure re ");
		sf.append(" where nvl(re.dr,0) = 0 and re.pk_corp = ?  and ");
		sf.append(condition);

		List<MeasureVO> invlist = (List<MeasureVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(MeasureVO.class));
		if (invlist == null || invlist.size() == 0) {
			return null;
		}
		return invlist.toArray(new MeasureVO[invlist.size()]);
	}

	@Override
	public MeasureVO save(MeasureVO vo) throws DZFWarpException {
		if(StringUtil.isEmpty(vo.getCode()) && !StringUtil.isEmpty(vo.getPk_corp())){
			vo.setCode(yntBoPubUtil.getInvclCode(vo.getPk_corp()));
		}
		checkExist(vo);
		return (MeasureVO) getSingleObjectBO().saveObject(vo.getPk_corp(), vo);
	}

	private void checkExist(MeasureVO vo) throws DZFWarpException{
		String sql = "select 1 from ynt_measure where  pk_corp=? and (code = ? or name=?) and nvl(dr,0)=0";
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getCode());
		sp.addParam(vo.getName());
		boolean b= getSingleObjectBO().isExists(vo.getPk_corp(), sql, sp);//(InvclassifyVO.class, where, sp);
		if(b == true){
			throw new BusinessException("计量单位编码或名称已经存在");
		}
	}

	private void checkBeforeSave(String pk_corp, String sort, String order, List<MeasureVO> list) {
		List<MeasureVO> listAll = quyerByPkcorp(pk_corp, sort, order);
		HashSet<String> pkSet = new HashSet<String>();
		for (MeasureVO vo : list) {
			if (!StringUtil.isEmpty(vo.getPk_measure()))
				pkSet.add(vo.getPk_measure());
		}
		HashSet<String> nameSet = new HashSet<String>();
		if (listAll != null && listAll.size() != 0) {
			for (MeasureVO vo : listAll) {
				if (!pkSet.contains(vo.getPk_measure()))
					nameSet.add(vo.getName());
			}
		}
		StringBuffer message = new StringBuffer();
		for (MeasureVO vo : list) {
			if (!StringUtil.isEmpty(vo.getName())) {
				if (nameSet.contains(vo.getName())) {
					message.append("名称为：" + vo.getName() + "已存在！" + "<br>");
				} else {
					nameSet.add(vo.getName());
				}
			} else {
				message.append("名称不能为空！");
			}
		}

		if (!StringUtil.isEmpty(message.toString()))
			throw new BusinessException(message.toString());
	}

	private void setDefaultValue(MeasureVO vo, String pk_corp, String cuserid) {
		vo.setPk_corp(pk_corp);
		vo.setCreatetime(new DZFDateTime(new Date()));
		vo.setCreator(cuserid);
	}

	@Override
	public String saveImp(File file, String pk_corp, String fileType, String userid) throws DZFWarpException {
		DZFDateTime date = new DZFDateTime();
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			Workbook impBook = null;
			if ("xls".equals(fileType)) {
				impBook = new HSSFWorkbook(is);
			} else if ("xlsx".equals(fileType)) {
				impBook = new XSSFWorkbook(is);
			} else {
				throw new BusinessException("不支持的文件格式");
			}
			Sheet sheet1 = impBook.getSheetAt(0);
			HashSet<String> codeSet = new HashSet<String>();
			HashSet<String> nameSet = new HashSet<String>();

			List<MeasureVO> meaVO = query(pk_corp);
			for (MeasureVO vo : meaVO) {
				codeSet.add(vo.getCode());
			}
			for (MeasureVO vo : meaVO) {
				nameSet.add(vo.getName());
			}

			List<MeasureVO> list = new ArrayList<MeasureVO>();

			Cell codeCell = null;
			Cell nameCell = null;
			Cell memoCell = null;
			String name = null;
			String code = null;
			String memo = null;
			int failCount = 0;
			StringBuffer msg = new StringBuffer();
			int length = sheet1.getLastRowNum();
			if (length > 1000) {
				throw new BusinessException("最多可导入1000行");
			}
			MeasureVO vo = null;
			for (int iBegin = 1; iBegin <= length; iBegin++) {
				vo = new MeasureVO();
				code = null;
				name = null;
				codeCell = sheet1.getRow(iBegin).getCell(0);
				// if (codeCell == null)
				// continue;
				nameCell = sheet1.getRow(iBegin).getCell(1);
				// if (nameCell == null)
				// continue;
				memoCell = sheet1.getRow(iBegin).getCell(2);

				if (codeCell != null && codeCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					code = codeCell.getRichStringCellValue().getString();
					code = code.trim();
					vo.setCode(code);
				} else if (codeCell != null && codeCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
					int codeVal = Double.valueOf(codeCell.getNumericCellValue()).intValue();
					code = String.valueOf(codeVal);
					vo.setCode(code);
				}
				if (nameCell != null && nameCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					name = nameCell.getRichStringCellValue().getString();
					name = name.trim();
					vo.setName(name);
				}
				if (memoCell != null) {
					if (memoCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
						memo = memoCell.getRichStringCellValue().getString();
						memo = memo.trim();
						vo.setMemo(memo);
					}
				} else {
					vo.setMemo(null);
				}
				if (StringUtil.isEmpty(code) && StringUtil.isEmpty(name)) {
					continue;
				}

				if (StringUtil.isEmpty(code) || StringUtil.isEmpty(name)) {
					failCount++;
					msg.append("<p><font color = 'red'>第").append(iBegin + 1).append("行必输项为空！</font></p>");
					continue;
				}
				if (codeSet.contains(code)) {
					failCount++;
					msg.append("<p><font color = 'red'>第").append(iBegin + 1).append("行编号为：").append(code).append("的项目已存在！</font></p>");
					continue;
				}
				if (nameSet.contains(name)) {
					failCount++;
					msg.append("<p><font color = 'red'>第").append(iBegin + 1).append("行名称为：").append(name).append("的项目已存在！</font></p>");
					continue;
				}
				vo.setPk_corp(pk_corp);
				vo.setCreator(userid);
				vo.setCreatetime(date);
				codeSet.add(vo.getCode());
				nameSet.add(vo.getName());
				list.add(vo);
			}

			MeasureVO[] newvos = new MeasureVO[list.size()];
			newvos = list.toArray(newvos);
			singleObjectBO.insertVOArr(pk_corp, newvos);
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

	@Override
	public List<MeasureVO> query(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuffer sb = new StringBuffer();
		sb.append("pk_corp=? and nvl(dr,0)=0");
		sp.addParam(pk_corp);
		List<MeasureVO> listVo = (List<MeasureVO>) singleObjectBO.retrieveByClause(MeasureVO.class, sb.toString(), sp);
		return listVo;
	}

	@Override
	public MeasureVO saveVO(MeasureVO vo) throws DZFWarpException {

		if(StringUtil.isEmpty(vo.getCode()) && !StringUtil.isEmpty(vo.getPk_corp())){
			vo.setCode(yntBoPubUtil.getMeasureCode(vo.getPk_corp()));
		}

		MeasureVO newVO = (MeasureVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);

		return newVO;
	}

}
