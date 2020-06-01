package com.dzf.zxkj.app.service.photo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dzf.zxkj.app.model.app.remote.AppCorpCtrlVO;
import com.dzf.zxkj.app.service.photo.IImagePubQryService;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("imageqry_pub")
public class ImagePubQryServiceImpl implements IImagePubQryService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public List<ImageGroupVO> queryImgGroupvo(String[] corpids, String account_id, DZFDate startdate, DZFDate enddate,
											  String groupid, String wherepart) throws DZFWarpException {
		if (corpids == null || corpids.length == 0) {
			throw new BusinessException("公司不能为空!");
		}
		StringBuffer gpsql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		gpsql.append(" SELECT * FROM YNT_IMAGE_GROUP ");
		gpsql.append("  WHERE  1=1 ");

		if (!StringUtil.isEmpty(account_id)) {
			gpsql.append("   and (COPERATORID = ?  or vapprovetor = ?)    ");
			sp.addParam(account_id);
			sp.addParam(account_id);
		}

		if (startdate != null) {
			gpsql.append(" AND DOPERATEDATE>=? ");
			sp.addParam(startdate.toString());
		}
		if (enddate != null) {
			gpsql.append(" AND DOPERATEDATE<=? ");
			sp.addParam(enddate.toString());
		}
		gpsql.append(" AND NVL(DR,0)=0 ");
		gpsql.append("and" + SqlUtil.buildSqlForIn("pk_corp", corpids));
		if (!StringUtil.isEmpty(groupid)) {
			gpsql.append(" and pk_image_group = ?  ");
			sp.addParam(groupid);
		}
		if (!StringUtil.isEmpty(wherepart)) {
			gpsql.append(wherepart);
		}
		gpsql.append("   ORDER BY  ts desc ");
		List<ImageGroupVO> groups = (ArrayList<ImageGroupVO>) singleObjectBO.executeQuery(gpsql.toString(), sp,
				new BeanListProcessor(ImageGroupVO.class));
		return groups;

	}

	@Override
	public Map<String, Integer> queryUserImgPower(Map<String, AppCorpCtrlVO> mapcorp, List<ImageGroupVO> gplist)
			throws DZFWarpException {
		Map<String, Integer> imgpower = new HashMap<String, Integer>();

		if (gplist == null || gplist.size() == 0) {
			return imgpower;
		}

		String pk_corp = null;
		AppCorpCtrlVO ctrlvo = null;
		for (ImageGroupVO gpvo : gplist) {
			pk_corp = gpvo.getPk_corp();

			ctrlvo = mapcorp.get(pk_corp);

			if (ctrlvo != null) {
				if ((gpvo.getIstate() == PhotoState.state0 ||  gpvo.getIstate() ==  PhotoState.state101) && ctrlvo.getIshasmake() != null
						&& ctrlvo.getIshasmake().booleanValue() ) {
					imgpower.put(gpvo.getPrimaryKey(), 1);// 通过+退回
				} else if (gpvo.getIstate() == PhotoState.state80 && ctrlvo.getIshasupload() != null
						&& ctrlvo.getIshasupload().booleanValue()) {
					imgpower.put(gpvo.getPrimaryKey(), 2);// 重传
				} else {
					imgpower.put(gpvo.getPrimaryKey(), 0);// 毫无权限
				}
			}
		}

		return imgpower;
	}

}
