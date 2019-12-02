package com.dzf.zxkj.platform.service.zncs.image.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.util.SQLHelper;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.ImageLibraryVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.zncs.image.IImage2BillServiceImpl;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultImage2BillServiceImpl implements IImage2BillServiceImpl {

	private Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public TzpzHVO saveBill(CorpVO corpvo, TzpzHVO hvo, OcrInvoiceVO invvo, ImageGroupVO grpvo, boolean isRecog)
			throws DZFWarpException {
		return null;
	}

	// 设置日期
	protected DZFDate getDzfDateData(String sdate) {
		try {
			if (StringUtil.isEmpty(sdate)) {
				return null;
			}
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Date date = null;
			StringTokenizer st = new StringTokenizer(sdate, "-/.");
			if (st.countTokens() == 3) {
				DZFDate ddate = new DZFDate(sdate);
				date = ddate.toDate();
			} else {
				date = formatter.parse(sdate);
			}
			String dateString = formatter.format(date);
			return new DZFDate(dateString);
		} catch (Exception e) {
			log.error("日期转换未知错误", e);
		} finally {

		}
		return null;
	}

	protected String getStrFormateDate(String str) {

		if (StringUtil.isEmpty(str)) {

			return null;

		}
		StringBuffer strb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			try {
				char ch = str.charAt(i);
				Integer.parseInt(String.valueOf(ch));
				strb.append(ch);
			} catch (NumberFormatException e) {
				// log.error("日期转换错误");
			} catch (Exception e) {
				log.error("日期转换未知错误", e);
			}
		}
		if (strb.length() > 0) {
			return strb.toString();
		} else {
			return null;
		}
	}

	protected DZFDouble getDZFDouble(String smny) {
		DZFDouble mny = DZFDouble.ZERO_DBL;
		try {

			if (StringUtil.isEmpty(smny)) {
				mny = DZFDouble.ZERO_DBL;
			} else {
				smny = smny.replaceAll("[￥%$*免税]", "");
				smny = replaceBlank(smny);
			}
			mny = new DZFDouble(smny);
		} catch (Exception e) {
			if (e instanceof NumberFormatException)
				throw new BusinessException("数字识别出错");
			else
				throw new BusinessException(e.getMessage());
		}
		return mny;
	}

	protected String replaceBlank(String str) {
		String dest = "";
		if (!StringUtil.isEmpty(str)) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	protected TzpzHVO queryTzpzByImageGroup(ImageGroupVO grpvo) {
		if (grpvo == null) {
			return null;
		}

		SQLParameter sp = new SQLParameter();
		sp.addParam(grpvo.getPrimaryKey());
		List<TzpzHVO> headList = (List<TzpzHVO>) singleObjectBO.executeQuery("nvl(dr,0)=0 and pk_image_group= ? ", sp,
				new Class[] { TzpzHVO.class, TzpzBVO.class });

		if (headList == null || headList.size() == 0) {
			return null;
		}

		return headList.get(0);
	}

	protected void updatePzImageGroup(String pk_image_group, String pk_tzpz_h) {
		String sql = " update  ynt_tzpz_h set  pk_image_group = ?,iautorecognize = ? where  pk_tzpz_h = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_image_group);
		sp.addParam(1);
		sp.addParam(pk_tzpz_h);
		singleObjectBO.executeUpdate(sql, sp);

	}

	protected void updatePzImageGroup7(String pk_image_group, String pk_tzpz_h) {
		String sql = " update  ynt_tzpz_h set  pk_image_group = ? where  pk_tzpz_h = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_image_group);
		sp.addParam(pk_tzpz_h);
		singleObjectBO.executeUpdate(sql, sp);

		sql = " update  ynt_image_group set  istate=? where  pk_image_group = ?";
		sp = new SQLParameter();
		sp.addParam(PhotoState.state100);
		sp.addParam(pk_image_group);
		singleObjectBO.executeUpdate(sql, sp);
	}

	protected void updateRepeatedInfo(String pk_tzpz_h, OcrInvoiceVO invvo) {
		// 有图片 则不关联 不处理 停止生成凭证流程 更新重复标识 更新重复凭证id
		String sql = " update  ynt_image_group set  istate=? where  pk_image_group = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(PhotoState.state102);
		sp.addParam(invvo.getPk_image_group());
		singleObjectBO.executeUpdate(sql, sp);

		sql = " update  ynt_image_ocrlibrary set  def10=? where  pk_image_ocrlibrary = ?";
		sp.clearParams();
		sp.addParam(pk_tzpz_h);
		sp.addParam(invvo.getOcr_id());
		singleObjectBO.executeUpdate(sql, sp);

	}

	protected void updateImageGroup(OcrInvoiceVO invvo) {
		// 图片生成销进项清单已占用
		String sql = " update  ynt_image_group set  istate=? where  pk_image_group = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(PhotoState.state1);
		sp.addParam(invvo.getPk_image_group());
		singleObjectBO.executeUpdate(sql, sp);
	}

	protected void checkValidImgGrpData(ImageGroupVO imggrpvo) {
		ImageGroupVO ingrpvo = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class,
				imggrpvo.getPrimaryKey());
		if (ingrpvo == null || (ingrpvo.getDr() != null && ingrpvo.getDr().intValue() == 1))
			throw new BusinessException("该图片信息不存在，请检查");
		Integer istate = ingrpvo.getIstate();
		if ((istate != null && istate != PhotoState.state0 && istate != PhotoState.state101&& istate != PhotoState.state1)) {
			if (istate == PhotoState.state102) {
				throw new BusinessException("该图片已经重复，请检查");
			} else {
				throw new BusinessException("该图片已经被制证，请检查");
			}

		}
	}

	protected ImageLibraryVO getImageLibraryVO(String pk_corp, OcrInvoiceVO invvo) {
		StringBuffer strb = new StringBuffer();
		strb.append(" select iy.* from  ynt_image_library iy ");
		strb.append(" join ynt_image_ocrlibrary oy on iy.pk_image_library = oy.crelationid ");
		strb.append("where  nvl(oy.dr,0)=0 and nvl(iy.dr,0)=0 ");
		strb.append(" and oy.pk_image_ocrlibrary = ? and iy.pk_corp = ? ");

		SQLParameter sp = new SQLParameter();
		sp.addParam(invvo.getOcr_id());
		sp.addParam(pk_corp);

		List<ImageLibraryVO> list = (List<ImageLibraryVO>) singleObjectBO.executeQuery(strb.toString(), sp,
				new BeanListProcessor(ImageLibraryVO.class));
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;

	}

	protected String getImgpath(ImageLibraryVO libvo) {
		String imagepath = null;
		if (libvo != null) {
			imagepath = "/gl/gl_imgview!search.action?id=" + libvo.getPk_image_library() + "&name=" + libvo.getImgname()
					+ "&pk_corp=" + libvo.getPk_corp() + "";

		}
		return imagepath;

	}

	// 重新识别
	protected void saveVatInvoiceDataModelRecog7(CorpVO corpvo, TzpzHVO hvo, OcrInvoiceVO invvo, ImageGroupVO grpvo) {

		// 如果已经制证 不生成销进项清单
		checkValidImgGrpData(grpvo);

		SuperVO supervo = queryBillData(invvo, corpvo.getPk_corp());

		if (supervo == null) {
			// 是否存在清单 如果不存在清单 直接生成清单(以后可考虑生成凭证后 在生成清单 需要清单绑定凭证)
			saveBillData(corpvo, hvo, invvo, grpvo,null);
			updateImageGroup(invvo);
		} else {
			// 如果存在清单 更新清单
			saveBillData(corpvo, hvo, invvo, grpvo,supervo.getPrimaryKey());
		}
	}

	// 需要判断是否存在进项清单 如果存在 没有生成凭证 不生成凭证 已经生成凭证 关联凭证
	protected void saveVatInvoiceDataModel7(CorpVO corpvo, TzpzHVO hvo, OcrInvoiceVO invvo, ImageGroupVO grpvo) {

		// 如果已经制证 不生成销进项清单
		checkValidImgGrpData(grpvo);

		SuperVO supervo = queryBillData(invvo, corpvo.getPk_corp());

		if (supervo == null) {
			// 是否存在清单 如果不存在清单 直接生成清单(以后可考虑生成凭证后 在生成清单 需要清单绑定凭证)
			saveBillData(corpvo, hvo, invvo, grpvo,null);
			updateImageGroup(invvo);
		} else {
			// 如果存在清单 判断是否已经绑定图片

			// 清单图片id
			String pk_image_group = (String) supervo.getAttributeValue("pk_image_group");
			// 凭证id
			String pk_tzpz_h = (String) supervo.getAttributeValue("pk_tzpz_h");
			// 凭证图片id
			String pk_image_group1 = (String) supervo.getAttributeValue("pk_image_group1");

			// 如果绑定图片 标识重复
			if (!StringUtil.isEmpty(pk_image_group)) {
				// 标识重复
				updateRepeatedInfo(pk_tzpz_h, invvo);
			} else {
				// 如果未绑定图片 绑定图片到清单
				String imagepath = null;
				ImageLibraryVO libvo = getImageLibraryVO(hvo.getPk_corp(), invvo);
				if (libvo != null) {
					imagepath = getImgpath(libvo);
				}
				if (!StringUtil.isEmpty(imagepath)) {
					updateBillImagePath(imagepath, libvo, supervo.getPrimaryKey(), grpvo.getPk_corp());
					updateImageGroup(invvo);
				}
				// 凭证无图片 停止生成凭证流程
				if (!StringUtil.isEmpty(pk_tzpz_h)) {
					// 如果已经生成凭证 绑定图片到凭证
					if (StringUtil.isEmpty(pk_image_group1)) {
						// 如果 凭证无图片 停止生成凭证流程
						updatePzImageGroup7(grpvo.getPk_image_group(), pk_tzpz_h);
					} else {
						// 凭证有图片合并组
						List<String> list1 = new ArrayList<>();
						list1.add(pk_image_group1);
						list1.add(grpvo.getPk_image_group());
						mergeImage(grpvo.getPk_corp(), list1);
					}
				} else {
					// 如果没有生成凭证 停止
				}
			}
		}
	}

	/**
	 * 合并凭证图片组
	 * 
	 * @param pk_corp
	 * @param imageGroupList
	 * @return
	 */
	protected String mergeImage(String pk_corp, List<String> imageGroupList) {
		int count = imageGroupList.size();
		String groupId = imageGroupList.get(0);
		if (count == 1) {
			return groupId;
		}
		imageGroupList.remove(0);
		String inSQL = SQLHelper.getInSQL(imageGroupList);
		SQLParameter sp = new SQLParameter();
		sp.addParam(groupId);
		sp.addParam(pk_corp);
		for (String pk_group : imageGroupList) {
			sp.addParam(pk_group);
		}
		singleObjectBO.executeUpdate(
				"update ynt_image_library set pk_image_group = ? where pk_corp = ? and pk_image_group in " + inSQL
						+ " and nvl(dr,0)=0 ",
				sp);
		singleObjectBO.executeUpdate(
				"update ynt_vatsaleinvoice set pk_image_group = ? where pk_corp = ? and pk_image_group in " + inSQL
						+ " and nvl(dr,0)=0 ",
				sp);
		singleObjectBO.executeUpdate(
				"update ynt_vatincominvoice set pk_image_group = ? where pk_corp = ? and pk_image_group in " + inSQL
						+ " and nvl(dr,0)=0 ",
				sp);
		singleObjectBO.deleteByPKs(ImageGroupVO.class, imageGroupList.toArray(new String[0]));
		return groupId;
	}

	protected SuperVO queryBillData(OcrInvoiceVO invvo, String pk_corp) {
		return null;
	}

	protected void saveBillData(CorpVO corpvo, TzpzHVO hvo, OcrInvoiceVO invvo, ImageGroupVO grpvo,String pk) {

	}

	protected void updateBillImagePath(String imagepath, ImageLibraryVO libvo, String primarykey, String pk_corp) {
	}

	protected String filterName(String name) {
		if (!StringUtil.isEmpty(name)) {
			name = name.replaceAll("[()（）\\[\\]]", "");
		} else {
			name = "";
		}
		name = getHanzi(name);
		return name;
	}

	private String getHanzi(String string) {
		if (StringUtil.isEmpty(string))
			return null;
		String reg1 = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(reg1);
		Matcher m = p.matcher(string);

		String lasrChar = null;
		while (m.find()) {
			lasrChar = lasrChar + m.group();
		}
		return string;
	}
	
	
	protected int getQuarter(int iMonth) {
		switch (iMonth) {
			case 1:
			case 2:
			case 3: {
				return 1;
			}
			case 4:
			case 5:
			case 6: {
				return 2;
			}
			case 7:
			case 8:
			case 9: {
				return 3;
			}
			case 10:
			case 11:
			case 12: {
				return 4;
			}
		}
		return -1;

	}
}
