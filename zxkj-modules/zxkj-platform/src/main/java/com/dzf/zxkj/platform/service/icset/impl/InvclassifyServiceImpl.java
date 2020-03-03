package com.dzf.zxkj.platform.service.icset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.icset.InvclassifyVO;
import com.dzf.zxkj.platform.service.common.impl.BgPubServiceImpl;
import com.dzf.zxkj.platform.service.icset.IInvclassifyService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 
 * 存货类别
 *
 */
@Service("ic_inclsserv")
public class InvclassifyServiceImpl extends BgPubServiceImpl implements IInvclassifyService {

	public SingleObjectBO getSingleObjectBO() {
		return super.getSingleObjectBO();
	}
	@Autowired
	private IYntBoPubUtil yntBoPubUtil;

	@Override
	public InvclassifyVO save(InvclassifyVO vo) throws DZFWarpException {

		if(StringUtil.isEmpty(vo.getCode()) && !StringUtil.isEmpty(vo.getPk_corp())){
			vo.setCode(yntBoPubUtil.getInvclCode(vo.getPk_corp()));
		}
		vo.setName(StringUtil.replaceBlank(vo.getName()));
		checkExist(vo);
		if(!StringUtil.isEmpty(vo.getPk_invclassify())){
			update(vo);
			return vo;
		}
		return (InvclassifyVO) getSingleObjectBO().saveObject(vo.getPk_corp(), vo);
	}

	@Override
	public List<InvclassifyVO> query(String pk_corp) throws DZFWarpException{
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		List<InvclassifyVO> qryVOs = (List<InvclassifyVO>) getSingleObjectBO().retrieveByClause(InvclassifyVO.class, " pk_corp = ? and nvl(dr,0) = 0 ", params);//order by code asc
		if(qryVOs != null && qryVOs.size()>0){
			//排序 wzn
			Collections.sort(qryVOs, new Comparator<InvclassifyVO>() {
				@Override
				public int compare(InvclassifyVO o1, InvclassifyVO o2) {
					int i = o1.getCode().compareTo(o2.getCode());
					return i;
				}
			});
			return qryVOs;
		}
		return null;
	}
	
	public InvclassifyVO queryByPrimaryKey(String PrimaryKey) throws DZFWarpException{
		SuperVO vo = getSingleObjectBO().queryByPrimaryKey(InvclassifyVO.class, PrimaryKey);
		return (InvclassifyVO) vo;
	}

	@Override
	public void delete(InvclassifyVO vo) throws DZFWarpException {
		String sql = "select 1 from ynt_inventory where pk_corp=? and  pk_invclassify=? and nvl(dr,0)=0 ";
		SQLParameter param = new SQLParameter();
		param.addParam(vo.getPk_corp());
		param.addParam(vo.getPk_invclassify());
		boolean b=getSingleObjectBO().isExists(vo.getPk_corp(), sql, param);//.executeQuery(sql, param, new BeanListProcessor(InventoryVO.class));
		if(b==true){
			throw new BusinessException("存货类型被引用，不能删除！");
		}else{
			getSingleObjectBO().deleteObject(vo);
		}
		
	}

	@Override
	// 批量删除 ， 被引用的存货分类不能被删除
	public String deleteBatch(String[] ids, String pk_corp) throws DZFWarpException {
		String strids = SqlUtil.buildSqlConditionForIn(ids);
		SQLParameter param = new SQLParameter();
		param.addParam(pk_corp);
        InvclassifyVO[] invos = (InvclassifyVO[]) getSingleObjectBO().queryByCondition(InvclassifyVO.class,
				"  pk_corp=? and pk_invclassify in ( " + strids + " ) ", param);

		if (invos == null || invos.length == 0)
			throw new BusinessException("存货分类不存在，或已经删除！");

		Map<String, InvclassifyVO> map = new HashMap<String, InvclassifyVO>();
		for (InvclassifyVO invo : invos) {
			String pk_inventory = invo.getPk_invclassify();
			if (!map.containsKey(pk_inventory)) {
				map.put(pk_inventory, invo);
			}
		}
		StringBuffer errmsg = new StringBuffer();
		List<String> errlist = new ArrayList<>();
		checkInventoryRef(strids, pk_corp, errmsg, errlist, map, "ynt_inventory", "存货档案");
		if (errlist != null && errlist.size() > 0) {

			for (String str : errlist) {
				if (map.containsKey(str))
					map.remove(str);
			}
		}
		if (map != null && map.size() > 0) {
			String[] pks = map.keySet().toArray(new String[0]);
			getSingleObjectBO().deleteByPKs(InvclassifyVO.class, pks);
		}

		if (errmsg != null && errmsg.length() > 0) {
			return errmsg.toString();
		} else {
			return null;
		}

	}

	private void checkInventoryRef(String strids, String pk_corp, StringBuffer errmsg, List<String> errlist,
								   Map<String, InvclassifyVO> map, String tablename, String msg) {

		StringBuffer sf = new StringBuffer();
		sf.append("select distinct pk_invclassify from " + tablename
				+ " where pk_corp=? and nvl(dr,0) = 0 and pk_invclassify in ( ").append(strids).append(" ) ");
		SQLParameter param = new SQLParameter();
		param.addParam(pk_corp);
		List<String> list = (List<String>) getSingleObjectBO().executeQuery(sf.toString(), param, new ColumnListProcessor());
		if (list != null && list.size() > 0) {
			for (String str : list) {
                InvclassifyVO invo = map.get(str);
				if (invo != null) {
					if (!errlist.contains(invo.getPk_invclassify())) {
						errmsg.append(
								"<p><font color = 'red'>存货分类[" + invo.getCode() + "]已被" + msg + "引用,不能删除!</font></p>");
						errlist.add(invo.getPk_invclassify());
					}
				}
			}
		}
	}

	private void update(InvclassifyVO vo) throws DZFWarpException {
		getSingleObjectBO().update(vo);
	}
	private void checkExist(InvclassifyVO vo) throws DZFWarpException{
		String sql = "select 1 from ynt_invclassify where pk_invclassify<>? and pk_corp=? and (code = ? or name=?) and nvl(dr,0)=0";
		SQLParameter sp = new SQLParameter();
		if(StringUtil.isEmpty(vo.getPrimaryKey()))
			sp.addParam(" ");
		else{
			sp.addParam(vo.getPrimaryKey());
		}
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getCode());
		sp.addParam(vo.getName());
		boolean b= getSingleObjectBO().isExists(vo.getPk_corp(), sql, sp);//(InvclassifyVO.class, where, sp);
		if(b == true){
			throw new BusinessException("存货分类编码或名称已经存在");
		}
	}

	@Override
	public String saveImp(MultipartFile file, String pk_corp, String fileType) throws DZFWarpException {
		InputStream is = null;
		try {
			is = file.getInputStream();
			Workbook impBook = null;
			if("xls".equals(fileType)) {
				impBook = new HSSFWorkbook(is);
			} else if ("xlsx".equals(fileType)) {
				  impBook = new XSSFWorkbook(is);
			} else {
				throw new BusinessException("不支持的文件格式");
			}
			Sheet sheet1 = impBook.getSheetAt(0);
			HashSet<String> codeSet = new HashSet<String>();
			HashSet<String> nameSet = new HashSet<String>();
			
			List<InvclassifyVO> invVO = query(pk_corp);
			if(invVO != null && invVO.size() > 0){
				for (InvclassifyVO vo : invVO) {
					codeSet.add(vo.getCode());
				}
				for(InvclassifyVO vo : invVO){
					nameSet.add(vo.getName());
				}
			}

			List<InvclassifyVO> list = new ArrayList<InvclassifyVO>();
			
			Cell codeCell = null;
			Cell nameCell = null;
			Cell memoCell = null;
			String name = null;
			String code = null;
			String memo = null;
			int failCount = 0;
			StringBuffer msg = new StringBuffer();
			int length = sheet1.getLastRowNum();
			if(length>1000){
				throw new  BusinessException("最多可导入1000行");
			}
			InvclassifyVO vo = null;
            Row row = null;
            for (int iBegin = 1; iBegin <= length; iBegin++) {
				row = sheet1.getRow(iBegin);
				if (row == null || isRowEmpty(row)) {
					continue;
				}
				vo = new InvclassifyVO();
				code = null;
				name = null;
				codeCell = row.getCell(0);
//				if (codeCell == null)
//					continue;
				nameCell = row.getCell(1);
//				if (nameCell == null)
//					continue;
				memoCell = row.getCell(2);
				
				if (codeCell != null && codeCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					code = codeCell.getRichStringCellValue().getString();
					code = StringUtil.replaceBlank(code);
					vo.setCode(code);
				} else if (codeCell != null && codeCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
					int codeVal = Double.valueOf(codeCell.getNumericCellValue()).intValue();
					code = StringUtil.replaceBlank(String.valueOf(codeVal));
					vo.setCode(code);
				}
				if (nameCell != null && nameCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					name = nameCell.getRichStringCellValue().getString();
					name = StringUtil.replaceBlank(name);
					vo.setName(name);
				}
				if(memoCell != null){
					if (memoCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
						memo = memoCell.getRichStringCellValue().getString();
						memo = memo.trim();
						vo.setMemo(memo);
					}
				}else{
					vo.setMemo(null);
				}
				
				if(StringUtil.isEmpty(code) && StringUtil.isEmpty(name)){
					continue;
				}
				
				if (StringUtil.isEmpty(code) || StringUtil.isEmpty(name)) {
					failCount++;
					msg.append("<p><font color='red'>第").append(iBegin + 1).append("行必输项为空！</font></p>");
					continue;
				}
				if (codeSet.contains(code)) {
					failCount++;
					msg.append("<p><font color='red'>第").append(iBegin + 1).append("行编号为：").append(code).append("的项目已存在！</font></p>");
					continue;
				}
				if (nameSet.contains(name)) {
					failCount++;
					msg.append("<p><font color='red'>第").append(iBegin + 1).append("行名称为：").append(name).append("的项目已存在！</font></p>");
					continue;
				}
				vo.setPk_corp(pk_corp);
				codeSet.add(vo.getCode());
				nameSet.add(vo.getName());
				list.add(vo);
			}
			
			InvclassifyVO[] newvos = new InvclassifyVO[list.size()];
			newvos = list.toArray(newvos);
			if(newvos != null && newvos.length>0)
			getSingleObjectBO().insertVOArr(pk_corp, newvos);
			if(StringUtil.isEmpty(msg.toString())){
				return null;
			}else{
                StringBuilder sucmsg = new StringBuilder();
                sucmsg.append("成功导入 ").append(list.size()).append(" 条数据。失败 ").append(failCount).append(" 条。");
                msg.insert(0,sucmsg.toString());
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

	private boolean isRowEmpty(Row row) {
		for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
			Cell cell = row.getCell(c);
			if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
				return false;
		}
		return true;
	}
}
