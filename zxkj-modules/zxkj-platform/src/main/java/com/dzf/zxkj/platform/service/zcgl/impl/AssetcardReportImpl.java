package com.dzf.zxkj.platform.service.zcgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.zcgl.ZcMxZVO;
import com.dzf.zxkj.platform.model.zcgl.ZcZzVO;
import com.dzf.zxkj.platform.service.zcgl.IAssetcardReport;
import com.dzf.zxkj.common.query.QueryParamVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * 资产账表查询
 * 
 * @author zhangj
 *
 */
@Service("am_rep_zczzserv")
@SuppressWarnings("all")
public class AssetcardReportImpl implements IAssetcardReport {
	@Autowired
	private SingleObjectBO singleObjectBO;

	/**
	 * 累计VO的数值
	 * 
	 * @param sourceVO
	 * @param addVO
	 */
	private void addVOValue(ZcZzVO sourceVO, ZcZzVO addVO) {
		// 原值借方
		sourceVO.setYzjf(SafeCompute.add(sourceVO.getYzjf(), addVO.getYzjf()));
		// 原值贷方
		sourceVO.setYzdf(SafeCompute.add(sourceVO.getYzdf(), addVO.getYzdf()));
		// 原值余额
		sourceVO.setYzye(SafeCompute.add(sourceVO.getYzye(), addVO.getYzye()));
		// 累计折旧借方
		sourceVO.setLjjf(SafeCompute.add(sourceVO.getLjjf(), addVO.getLjjf()));
		// 累计折旧贷方
		sourceVO.setLjdf(SafeCompute.add(sourceVO.getLjdf(), addVO.getLjdf()));
		// 累计折旧余额
		sourceVO.setLjye(SafeCompute.add(sourceVO.getLjye(), addVO.getLjye()));
		// 净值余额
		sourceVO.setJzye(SafeCompute.add(sourceVO.getJzye(), addVO.getJzye()));
	}

	/**
	 * 累计VO的数值
	 * 
	 * @param sourceVO
	 * @param addVO
	 */
	private void addVOValue(ZcMxZVO sourceVO, ZcMxZVO addVO) {
		// 原值借方
		sourceVO.setYzjf(SafeCompute.add(sourceVO.getYzjf(), addVO.getYzjf()));
		// 原值贷方
		sourceVO.setYzdf(SafeCompute.add(sourceVO.getYzdf(), addVO.getYzdf()));
		// 原值余额
		sourceVO.setYzye(SafeCompute.add(sourceVO.getYzye(), addVO.getYzye()));
		// 累计折旧借方
		sourceVO.setLjjf(SafeCompute.add(sourceVO.getLjjf(), addVO.getLjjf()));
		// 累计折旧贷方
		sourceVO.setLjdf(SafeCompute.add(sourceVO.getLjdf(), addVO.getLjdf()));
		// 累计折旧余额
		sourceVO.setLjye(SafeCompute.add(sourceVO.getLjye(), addVO.getLjye()));
		// 净值余额
		sourceVO.setJzye(SafeCompute.add(sourceVO.getJzye(), addVO.getJzye()));
	}

	private String getAssetProperty(String assetProperty) {
		if ("0".equals(assetProperty))
			return "固定资产";
		else if ("1".equals(assetProperty))
			return "无形资产";
		else if ("3".equals(assetProperty))
			return "待摊费用";
		else
			return "";
	}

	private void initVO(ZcMxZVO vo) {
		vo.setYzye(DZFDouble.ZERO_DBL);
		vo.setLjye(DZFDouble.ZERO_DBL);
		vo.setJzye(DZFDouble.ZERO_DBL);
	}

	private void initVO(ZcZzVO vo) {
		vo.setYzye(DZFDouble.ZERO_DBL);
		vo.setLjye(DZFDouble.ZERO_DBL);
		vo.setJzye(DZFDouble.ZERO_DBL);
	}

	private ZcMxZVO[] onAfterLoadDataWithZCMX(DZFDate begin, DZFDate end,
											  ArrayList<ZcMxZVO> dataVOS) throws DZFWarpException {
		if (dataVOS == null || dataVOS.size() < 1)
			return null;

		ArrayList<ZcMxZVO> resultVOs = new ArrayList<ZcMxZVO>();
		ZcMxZVO beginVO = new ZcMxZVO();
		initVO(beginVO);
		beginVO.setZy("期初余额");
		resultVOs.add(beginVO);

		ZcMxZVO lastVO = new ZcMxZVO(); // 上一行VO
		ZcMxZVO currVO = null; // 当前行VO
		ZcMxZVO periodVO = null; // 本期合计VO
		ZcMxZVO totalVO = new ZcMxZVO(); // 本年合计VO
		initVO(totalVO);
		totalVO.setZy("本年累计");

		String currPeriod = "";
		int len = dataVOS.size();
		for (int i = 0; i < len; i++) {
			currVO = dataVOS.get(i);
			if (currVO.getRq().before(begin)) { // 在开始日期之前，作为期初余额
				addVOValue(beginVO, currVO);
				addVOValue(totalVO, currVO);
				lastVO = beginVO;
			} else {
				if (StringUtil.isEmptyWithTrim(currPeriod)
						|| !currPeriod.equals(currVO.getQj())) {
					if (!StringUtil.isEmptyWithTrim(currPeriod)
							&& !currPeriod.equals(currVO.getQj())) {
						ZcMxZVO periodVOtemp1 = (ZcMxZVO) periodVO.clone();
						ZcMxZVO totalVOtemp1 = (ZcMxZVO) totalVO.clone();
						resultVOs.add(periodVOtemp1); // 添加本期合计行
						resultVOs.add(totalVOtemp1); // 添加本年累计行
					}
					currPeriod = currVO.getQj();
					periodVO = new ZcMxZVO();
					initVO(periodVO);
					periodVO.setZy("本期合计");
				}

				// 本行的原值余额=上一行的原值余额+本行原值借方-本行原值贷方
				currVO.setYzye(SafeCompute.sub(
						SafeCompute.add(lastVO.getYzye(), currVO.getYzjf()),
						currVO.getYzdf()));
				// 本行的累计折旧余额=上一行的累计折旧余额+本行累计折旧贷方-本行累计折旧借方
				currVO.setLjye(SafeCompute.sub(
						SafeCompute.add(lastVO.getLjye(), currVO.getLjdf()),
						currVO.getLjjf()));
				// 本行的净值余额=本行原值余额-本行累计折旧余额
				currVO.setJzye(SafeCompute.sub(currVO.getYzye(),
						currVO.getLjye()));
				// 资产属性
				currVO.setZcsx(getAssetProperty(currVO.getZcsx()));

				addVOValue(periodVO, currVO);
				// 余额不能简单相加
				periodVO.setYzye(currVO.getYzye());
				periodVO.setLjye(currVO.getLjye());
				periodVO.setJzye(currVO.getJzye());

				addVOValue(totalVO, currVO);
				// 余额不能简单相加
				totalVO.setYzye(currVO.getYzye());
				totalVO.setLjye(currVO.getLjye());
				totalVO.setJzye(currVO.getJzye());

				resultVOs.add(currVO);
				lastVO = currVO;
			}
		}
		if (periodVO != null) {
			ZcMxZVO periodVOtemp = periodVO;
			resultVOs.add(periodVOtemp); // 添加本期合计行
		}
		if (totalVO != null) {
			ZcMxZVO totalVOtemp = totalVO;
			resultVOs.add(totalVOtemp); // 添加本年累计行
		}
		return resultVOs.toArray(new ZcMxZVO[resultVOs.size()]);
	}

	private ZcZzVO[] onAfterLoadDataWithZCZZ(DZFDate begin, DZFDate end,
			ArrayList<ZcZzVO> dataVOS) throws DZFWarpException {
		if (dataVOS == null || dataVOS.size() < 1)
			return null;

		ArrayList<ZcZzVO> resultVOs = new ArrayList<ZcZzVO>();
		ZcZzVO beginVO = new ZcZzVO();
		initVO(beginVO);
		beginVO.setZy("期初余额");
		resultVOs.add(beginVO);

		ZcZzVO lastVO = new ZcZzVO(); // 上一行VO
		ZcZzVO currVO = null; // 当前行VO
		ZcZzVO periodVO = null; // 本期合计VO
		ZcZzVO totalVO = new ZcZzVO(); // 本年合计VO
		initVO(totalVO);
		totalVO.setZy("本年累计");
		// totalVO.setZclb(currVO.getZclb());

		String currPeriod = "";
		int len = dataVOS == null ? 0 : dataVOS.size();
		DZFDate date = null;
		for (int i = 0; i < len; i++) {
			currVO = dataVOS.get(i);
			date = DZFDate.getDate(currVO.getQj() + "-01");
			if (date.before(begin)) {
				// 在开始日期之前，作为期初余额
				addVOValue(beginVO, currVO);
				addVOValue(totalVO, currVO);
				lastVO = beginVO;
			} else {
				if (StringUtil.isEmptyWithTrim(currPeriod)
						|| !currPeriod.equals(currVO.getQj())) {
					if (!StringUtil.isEmptyWithTrim(currPeriod)
							&& !currPeriod.equals(currVO.getQj())) {
						// 添加本期合计行
						resultVOs.add((ZcZzVO) periodVO.clone());
						// 添加本年累计行
						resultVOs.add((ZcZzVO) totalVO.clone());
					}
					currPeriod = currVO.getQj();
					periodVO = new ZcZzVO();
					initVO(periodVO);
					periodVO.setZy("本期合计");
					periodVO.setQj(currPeriod);
					totalVO.setQj(currPeriod);
					totalVO.setZclb(currVO.getZclb());
					periodVO.setZclb(currVO.getZclb());
				}

				// 本行的原值余额=上一行的原值余额+本行原值借方-本行原值贷方
				currVO.setYzye(SafeCompute.sub(
						SafeCompute.add(lastVO.getYzye(), currVO.getYzjf()),
						currVO.getYzdf()));
				// 本行的累计折旧余额=上一行的累计折旧余额+本行累计折旧贷方-本行累计折旧借方
				currVO.setLjye(SafeCompute.sub(
						SafeCompute.add(lastVO.getLjye(), currVO.getLjdf()),
						currVO.getLjjf()));
				// 本行的净值余额=本行原值余额-本行累计折旧余额
				currVO.setJzye(SafeCompute.sub(currVO.getYzye(),
						currVO.getLjye()));

				if(periodVO == null){
					periodVO = new ZcZzVO();
				}

				addVOValue(periodVO, currVO);
				// 余额不能简单相加
				periodVO.setYzye(currVO.getYzye());
				periodVO.setLjye(currVO.getLjye());
				periodVO.setJzye(currVO.getJzye());

				addVOValue(totalVO, currVO);
				// 余额不能简单相加
				totalVO.setYzye(currVO.getYzye());
				totalVO.setLjye(currVO.getLjye());
				totalVO.setJzye(currVO.getJzye());

				lastVO = currVO;
			}
		}
		if (periodVO != null)
			resultVOs.add((ZcZzVO) periodVO.clone()); // 添加本期合计行
//		if (totalVO != null)  永远为true
			resultVOs.add((ZcZzVO) totalVO.clone()); // 添加本年累计行

		return resultVOs.toArray(new ZcZzVO[resultVOs.size()]);
	}
	@Override
	public ZcZzVO[] queryAssetcardTotal(String pk_corp, DZFDate beginDate,
										DZFDate endDate, String where, SQLParameter sp, String zclb,
										String zcsx, String zcname) throws DZFWarpException {
		String strSql = getQuerySql(sp, pk_corp, beginDate, endDate, where,
				zclb, zcsx, zcname);
		if (StringUtil.isEmptyWithTrim(strSql))
			return null;
		ArrayList<ZcZzVO> zczzVOs = (ArrayList<ZcZzVO>) singleObjectBO
				.executeQuery(strSql, sp, new BeanListProcessor(ZcZzVO.class));
		if (zczzVOs == null || zczzVOs.size() == 0)
			return null;
		return onAfterLoadDataWithZCZZ(beginDate, endDate, zczzVOs);
	}

	@Override
	public ZcMxZVO[] queryAssetcardDetail(QueryParamVO queryparamvo)
			throws DZFWarpException {
		String pk_corp = queryparamvo.getPk_corp();
		DZFDate beginDate = queryparamvo.getBegindate1();
		DZFDate endDate = queryparamvo.getEnddate();
		String zxlb = queryparamvo.getPk_assetcategory();
		String zcsx = queryparamvo.getZcsx();
		String zcmc = queryparamvo.getAsname();
		String where = "";

		if (beginDate == null || endDate == null) {
			throw new BusinessException("查询开始日期，结束日期不能为空!");
		}

		if (beginDate.after(endDate)) {
			throw new BusinessException("查询开始日期，应在结束日期之前!");
		}
		SQLParameter sp = new SQLParameter();
		String strSql = getQuerySql(sp, pk_corp, beginDate, endDate, where,
				zxlb, zcsx, zcmc);
		if (StringUtil.isEmptyWithTrim(strSql))
			return null;
		ArrayList<ZcMxZVO> zcmxVOs = (ArrayList<ZcMxZVO>) singleObjectBO
				.executeQuery(strSql, sp, new BeanListProcessor(ZcMxZVO.class));
		if (zcmxVOs == null || zcmxVOs.size() == 0)
			return new ZcMxZVO[0];
		return onAfterLoadDataWithZCMX(beginDate, endDate, zcmxVOs);
	}

	private String getQuerySql(SQLParameter sp, String pk_corp, DZFDate begin,
			DZFDate end, String strWhere, String zclb, String zcsx,
			String zcname) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from (");
		// 资产建账
		sb.append(" select 1 xh,  a.accountdate rq, substr(a.accountdate, 1, 7) qj,");
		sb.append("   a.assetcode zcbh, a.assetname zcmc, b.catename zclb, b.assetproperty zcsx, null ywdh, h.pzh pzzh, '资产建账' zy, ");
		sb.append("   a.accountmny yzjf, null yzdf, a.accountmny yzye, null ljjf, a.initdepreciation ljdf, a.initdepreciation ljye, a.accountmny-nvl(a.initdepreciation,0) jzye,   ");
		sb.append("   h.pk_tzpz_h as pzpk ,a.pk_assetcard    ");
		sb.append(" from ynt_assetcard a inner join ynt_category b on a.assetcategory=b.pk_assetcategory left join ynt_tzpz_h h on a.pk_voucher=h.pk_tzpz_h");
		sb.append(" where nvl(a.dr,0)=0 and a.pk_corp = ?  and a.accountdate<= ? ");
		if (StringUtil.isEmptyWithTrim(strWhere) == false) {
			sb.append(" and ").append(strWhere);
		}
		sp.addParam(pk_corp);
		sp.addParam(end.toString());
		if (!StringUtil.isEmpty(zclb)) {
			sb.append("  and a.assetcategory = ? ");
			sp.addParam(zclb);
		}
		if (!StringUtil.isEmpty(zcsx)) {
			sb.append("  and b.assetproperty= ? ");
			sp.addParam(zcsx);
		}
		if (!StringUtil.isEmpty(zcname)) {
			sb.append("  and a.assetname like ? ");
			sp.addParam("%" + zcname + "%");
		}
		// 资产增加
		sb.append(" union all");
		sb.append(" select 2 xh, m.businessdate rq,substr(m.businessdate, 1, 7) qj,  a.assetcode zcbh, a.assetname zcmc, b.catename zclb, b.assetproperty zcsx, m.vbillno ywdh, h.pzh pzzh, '资产增加' zy,");
		sb.append("  m.changevalue yzjf, null yzdf, m.changevalue yzye, null ljjf, null ljdf, null ljye, m.changevalue jzye,  ");
		sb.append("  h.pk_tzpz_h as pzpk ,a.pk_assetcard ");
		sb.append(" from ynt_valuemodify m inner join ynt_assetcard a on m.pk_assetcard=a.pk_assetcard and nvl(a.dr,0)=0 inner join ynt_category b on a.assetcategory=b.pk_assetcategory left join ynt_tzpz_h h on m.pk_voucher=h.pk_tzpz_h");
		sb.append(" where nvl(m.dr,0)=0 and m.changevalue>0 and m.pk_corp= ?  and m.businessdate<= ?");
		if (StringUtil.isEmptyWithTrim(strWhere) == false) {
			sb.append(" and ").append(strWhere);
		}
		sp.addParam(pk_corp);
		sp.addParam(end.toString());
		if (!StringUtil.isEmpty(zclb)) {
			sb.append("  and a.assetcategory = ? ");
			sp.addParam(zclb);
		}
		if (!StringUtil.isEmpty(zcsx)) {
			sb.append("  and b.assetproperty= ? ");
			sp.addParam(zcsx);
		}
		if (!StringUtil.isEmpty(zcname)) {
			sb.append("  and a.assetname like ?");
			sp.addParam("%" + zcname + "%");
		}
		// 资产减少
		sb.append(" union all");
		sb.append(" select 3 xh, m.businessdate rq,substr(m.businessdate, 1, 7) qj,  a.assetcode zcbh, a.assetname zcmc, b.catename zclb, b.assetproperty zcsx, m.vbillno ywdh, h.pzh pzzh, '资产减少' zy, ");
		sb.append(" null yzjf, -m.changevalue yzdf, m.changevalue yzye, null ljjf, null ljdf, null ljye, m.changevalue jzye ");
		sb.append(" , h.pk_tzpz_h as pzpk,a.pk_assetcard ");
		sb.append(" from ynt_valuemodify m inner join ynt_assetcard a on m.pk_assetcard=a.pk_assetcard and nvl(a.dr,0)=0 inner join ynt_category b on a.assetcategory=b.pk_assetcategory left join ynt_tzpz_h h on m.pk_voucher=h.pk_tzpz_h");
		sb.append(" where nvl(m.dr,0)=0 and m.changevalue<0 and m.pk_corp= ?  and m.businessdate<= ? ");
		if (StringUtil.isEmptyWithTrim(strWhere) == false) {
			sb.append(" and ").append(strWhere);
		}
		sp.addParam(pk_corp);
		sp.addParam(end.toString());
		if (!StringUtil.isEmpty(zclb)) {
			sb.append("  and a.assetcategory = ? ");
			sp.addParam(zclb);
		}
		if (!StringUtil.isEmpty(zcsx)) {
			sb.append("  and b.assetproperty= ? ");
			sp.addParam(zcsx);
		}
		if (!StringUtil.isEmpty(zcname)) {
			sb.append("  and a.assetname like ? ");
			sp.addParam("%" + zcname + "%");
		}
		// 累计折旧
		sb.append(" union all");
		sb.append(" select 4 xh, n.businessdate rq,substr(n.businessdate, 1, 7) qj,  a.assetcode zcbh, a.assetname zcmc, b.catename zclb, b.assetproperty zcsx, '' ywdh, h.pzh pzzh, '累计折旧' zy,");
		sb.append(" null yzjf, null yzdf, null yzye, null ljjf, n.originalvalue ljdf, n.originalvalue ljye, -n.originalvalue jzye, h.pk_tzpz_h as pzpk ,a.pk_assetcard ");
		sb.append(" from ynt_depreciation n inner join ynt_assetcard a on n.pk_assetcard=a.pk_assetcard and nvl(a.dr,0)=0 inner join ynt_category b on a.assetcategory=b.pk_assetcategory left join ynt_tzpz_h h on n.pk_voucher=h.pk_tzpz_h");
		sb.append(" where nvl(n.dr,0)=0 and a.pk_corp= ?  and n.businessdate<= ? ");
		if (StringUtil.isEmptyWithTrim(strWhere) == false) {
			sb.append(" and ").append(strWhere);
		}
		sp.addParam(pk_corp);
		sp.addParam(end.toString());
		if (!StringUtil.isEmpty(zclb)) {
			sb.append("  and a.assetcategory = ? ");
			sp.addParam(zclb);
		}
		if (!StringUtil.isEmpty(zcsx)) {
			sb.append("  and b.assetproperty= ? ");
			sp.addParam(zcsx);
		}
		if (!StringUtil.isEmpty(zcname)) {
			sb.append("  and a.assetname like ? ");
			sp.addParam("%" + zcname + "%");
		}
		// 资产清理
		sb.append(" union all");
		sb.append(" select 5 xh, l.businessdate rq,substr(l.businessdate, 1, 7) qj,  a.assetcode zcbh, a.assetname zcmc, b.catename zclb, b.assetproperty zcsx, l.vbillno ywdh, h.pzh pzzh, '资产清理' zy, null yzjf, a.assetmny yzdf, -a.assetmny yzye, a.depreciation ljjf, null ljdf, -a.depreciation ljye, -a.assetmny+a.depreciation jzye, h.pk_tzpz_h as pzpk,a.pk_assetcard");
		sb.append(" from ynt_assetclear l inner join ynt_assetcard a on l.pk_assetcard=a.pk_assetcard inner join ynt_category b on a.assetcategory=b.pk_assetcategory left join ynt_tzpz_h h on l.pk_voucher=h.pk_tzpz_h");
		sb.append(" where nvl(l.dr,0)=0 and l.pk_corp= ?  and l.businessdate<= ? ");
		if (StringUtil.isEmptyWithTrim(strWhere) == false) {
			sb.append(" and ").append(strWhere);
		}
		sp.addParam(pk_corp);
		sp.addParam(end.toString());
		if (!StringUtil.isEmpty(zclb)) {
			sb.append("  and a.assetcategory = ? ");
			sp.addParam(zclb);
		}
		if (!StringUtil.isEmpty(zcsx)) {
			sb.append("  and b.assetproperty= ? ");
			sp.addParam(zcsx);
		}
		if (!StringUtil.isEmpty(zcname)) {
			sb.append("  and a.assetname like ? ");
			sp.addParam("%" + zcname + "%");
		}

		sb.append(")  asset ");
		// 按照资产编号排序
		sb.append(" order by asset.zcsx, asset.qj,  asset.zcbh, asset.rq,  asset.xh");

		return sb.toString();
	}
}
