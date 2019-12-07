package com.dzf.zxkj.platform.service.pjgl.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayProcessor;
import com.dzf.zxkj.base.framework.processor.ObjectProcessor;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageLibraryVO;
import com.dzf.zxkj.platform.model.image.OcrImageGroupVO;
import com.dzf.zxkj.platform.model.image.OcrImageLibraryVO;
import com.dzf.zxkj.platform.model.image.StateEnum;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.pjgl.IOcrImageGroupService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("gl_ocrimageserv")
public class OcrImageGroupServiceImpl implements IOcrImageGroupService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private ICorpService corpService;

	@Override
	// 生成管理端图片中间表数据 2017-09-07
	public void saveData(CorpVO corpvo1, ImageLibraryVO ilib, String pjlxType, int ibusinesstype, String filename)
			throws DZFWarpException {

		CorpVO corpvo = corpService.queryByPk(ilib.getPk_corp());
		// 智能识别生成中间表数据
		String ipicflow = corpvo.getDef4();
		if (!StringUtil.isEmpty(ipicflow)) {
			int iflow = Integer.parseInt(ipicflow);
//			if ((PhotoState.TREAT_TYPE_1 == iflow || PhotoState.TREAT_TYPE_3 == iflow
//					|| PhotoState.TREAT_TYPE_5 == iflow|| PhotoState.TREAT_TYPE_6 == iflow|| PhotoState.TREAT_TYPE_7 == iflow)) {
			if(PhotoState.TREAT_TYPE_7 == iflow){
				OcrImageGroupVO ig = new OcrImageGroupVO();
				ig.setPk_corp(ilib.getPk_corp());
				ig.setCoperatorid(ilib.getCoperatorid());
				ig.setDoperatedate(new DZFDate());
				// istate=0 标识对应直接生单,不启用切图、识图，以后用PhotoState常量类
				ig.setIstate(PhotoState.state0);// 0
				ig.setPk_selectcorp(ilib.getPk_corp());
				ig.setImagecounts(Integer.valueOf(1));
				ig.setIscomplete(DZFBoolean.TRUE);
				ig.setDr(Integer.valueOf(0));
				long maxCode = getMaxOcrImageGroupCode(ilib.getPk_corp());
				if (maxCode > 0) {
					ig.setGroupcode((new StringBuilder(String.valueOf(maxCode + 1L))).toString());
				} else {
					ig.setGroupcode((new StringBuilder(String.valueOf(getCurDate()))).append("0001").toString());
				}
				ig.setPk_corp(corpvo1.getPk_corp());
				ig.setDoperatedate(new DZFDate());
				ig.setPjlxstatus(StringUtil.isEmpty(pjlxType) ? null : Integer.parseInt(pjlxType));

				OcrImageLibraryVO il = new OcrImageLibraryVO();
				il.setImgname(ilib.getImgname());
				il.setPk_corp(corpvo1.getPk_corp());
				il.setPk_custcorp(ilib.getPk_corp());
				il.setCoperatorid(ilib.getCoperatorid());
				il.setCvoucherdate(ilib.getCvoucherdate());
				il.setDoperatedate(new DZFDate());
				il.setDr(Integer.valueOf(0));
				il.setIstate(Integer.valueOf(StateEnum.INIT.getValue()));
				il.setIszd(DZFBoolean.FALSE);
				il.setIsinterface(DZFBoolean.FALSE);
				il.setIbusinesstype(Integer.valueOf(ibusinesstype));
				il.setIspartition(DZFBoolean.FALSE);
				il.setIorder(Integer.valueOf(getOrderNo(il)));
				il.setReason("上传成功");
				il.setImgmd(ilib.getImgmd());
				il.setImgpath(ilib.getImgpath());
				il.setPdfpath(ilib.getPdfpath());
				il.setSmallimgpath(ilib.getSmallimgpath());
				il.setMiddleimgpath(ilib.getMiddleimgpath());
				// 关联关系设置
				il.setCrelationid(ilib.getPk_image_library());
				il.setSourceid(ilib.getPk_image_library());
				if (ilib.getSourcemode() != null) {
					il.setSystem(Integer.toString(ilib.getSourcemode()));
				}
				il.setPjlxstatus(StringUtil.isEmpty(pjlxType) ? null : Integer.parseInt(pjlxType));
				il.setOldfilename(filename);
				ig.addChildren(il);
				singleObjectBO.saveObject(ig.getPk_corp(), ig);
			}
		}
	}

	private long getMaxOcrImageGroupCode(String pk_corp) {
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(new DZFDate().toString());

		String sql = "select max(groupcode) from ynt_image_ocrgroup where pk_corp = ? and doperatedate = ? ";
		long maxcode = 0;

		Object[] array = (Object[]) singleObjectBO.executeQuery(sql, params, new ArrayProcessor());
		if (array != null && array.length > 0) {
			if (array[0] != null)
				maxcode = Long.parseLong(array[0].toString());
		}
		return maxcode;
	}

	// 获取当前年月日
	private String getCurDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(Calendar.getInstance().getTime());
	}

	// 获取最大顺序号
	private synchronized int getOrderNo(OcrImageLibraryVO vo) {
		// 加上docno like 'FR-2%'，排除垃圾数据单号
		String sql = " select  max(iorder)  from ynt_image_ocrlibrary where  pk_custcorp = '" + vo.getPk_custcorp()
				+ "' and cvoucherdate= '" + vo.getCvoucherdate() + "'"; //
		BigDecimal maxDocNo = (BigDecimal) singleObjectBO.executeQuery(sql, null, new ObjectProcessor());
		if (maxDocNo != null) {
			int maxNo = maxDocNo.intValue() + 1;
			return maxNo;
		}
		return 0;
	}
}
