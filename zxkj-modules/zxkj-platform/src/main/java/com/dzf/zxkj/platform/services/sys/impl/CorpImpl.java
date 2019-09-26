package com.dzf.zxkj.platform.services.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.CorpGenerate;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.MD5;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.sys.ICorp;
import com.dzf.zxkj.platform.util.CryptCodeUtil;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Corp的BO类
 * 
 * 创建日期：(2001-5-16)
 * 
 * @author：童志杰
 */
@Service("corpImpl")
public class CorpImpl implements ICorp {
//
//	private static final char[] CORPKEYS = { '0', '1', '2', '3', '4', '5', '6',
//			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
//			'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
//			'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
//			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
//			'x', 'y', 'z' };

	public SingleObjectBO singleObjectBO = null;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	@Override
	public void cancelCorp(CorpVO corp) throws DZFWarpException {
		// TODO Auto-generated method stub

	}

	@Override
	public int delete(CorpVO vo) throws DZFWarpException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String insert(CorpVO corp) throws DZFWarpException {
		// new CheckSealCanChg(corp, "pk_corp", "fathercorp", "isseal")
		// .checkCanChgSeal();
		// addInnerCode(corp); // 设置内部编码
		//corp.setInnercode(corp.getUnitcode());// gjw 将公司主键作为公司内码

		corp.setPrimaryKey(getNextPkCorp());
		checkIsOnly(corp, true);
		String innercode = corp.getInnercode();
        corp.setUnitcode(innercode + "_"+ MD5.md5crypt(corp.getPk_corp()));
		// nc.bs.bd.service.BDOperateServ bdOS = new
		// nc.bs.bd.service.BDOperateServ();
		// bdOS.beforeOperate("100406",
		// nc.vo.bd.service.IBDOperate.BDOPERATION_INSERT, null, null,
		// corp);
        String unitname = CodeUtils1.deCode(corp.getUnitname());
        corp.setRcunitname(CryptCodeUtil.enCode(unitname));
		String key = singleObjectBO.insertVOWithPK(corp);
		corp.setPrimaryKey(key);
		// bdOS.afterOperate("100406",
		// nc.vo.bd.service.IBDOperate.BDOPERATION_INSERT, key, null, corp);
		//
		// nc.bs.bd.cache.CacheProxy.fireDataInserted("bd_corp", key);
		return key;

	}

	/**
	 * 返回下一个可用的公司主键 创建日期：(2001-5-16)
	 * 
	 * @String
	 * @exception DZFWarpException
	 *                异常说明。
	 */
	public String getNextPkCorp() throws DZFWarpException {
		String sql = "select max(pk_corp) from bd_corp ";
		String maxPk = null;

		ColumnProcessor p = new ColumnProcessor();
		Object o = singleObjectBO.executeQuery(sql, null, p);
		if (o != null)
			maxPk = o.toString();
		if (maxPk == null)
			maxPk = IGlobalConstants.DefaultGroup;
		if("002bzn".compareTo(maxPk)>0){//截止2015年12月17日10点26分，NC数据库,pk_corp最大值。往后新生成50000个。
			maxPk = "002bzn";
		}
		return CorpGenerate.getInstance().getNextID(maxPk);
	}




//	private int getCorpKeysIndex(char c) {
//		for (int i = 0; i < CORPKEYS.length; i++) {
//			if (CORPKEYS[i] == c)
//				return i;
//		}
//		return -1;
//	}

	@Override
	public String insertCorp(CorpVO corp, String[] accountMsgInfo)
			throws DZFWarpException {
		try {

			//checkIsOnly(corp, true);
			// 检查父公司是否存在
			checkFatherExist(corp.getFathercorp());

			// corp.setCancel(new UFBoolean(false)); //2002-04-17 tzj Add
			// corp.setCreatedate(new UFDate(System.currentTimeMillis()));
			String corpid = insert(corp);
			// nc.bs.bd.cache.CacheProxy.fireDataInserted("bd_corp", corpid);

			return corpid;
		} finally {

		}
	}

	@Override
	public CorpVO insertCorpReturnSelf(CorpVO corp, String[] accountMsgInfo)
			throws DZFWarpException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String insertDefaultCorp(String dsName, CorpVO corp,
			String[] accountMsgInfo) throws DZFWarpException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String update(CorpVO corp) throws DZFWarpException {
		// 校验是否存在上级循环引用
//		checkIsCycleForFathercorp(corp);
		// 检查唯一性
		checkIsOnly(corp, false);

		// chenwei 040522 封存
//		CorpVO oldVO = findCorpVOByPK(corp.getPrimaryKey());
//		boolean isSeal = isSeal(corp, oldVO); // 是否封存

//		singleObjectBO.update(corp); // 数据库操作
		singleObjectBO.update(corp, new String[]{"innercode","unitname","unitshortname","postaddr",
				"legalbodycode","linkman1","linkman2","phone1","phone2","linkman3","industry","corptype","email1","begindate","taxcode","iscurr","holdflag",
				"busibegindate","ishasaccount","bbuildic","isseal","citycounty",
				"isworkingunit","foreignname","def3","chargedeptname",
				"ownersharerate","briefintro","icostforwardstyle","createdate",
				"vcustsource","vsourcenote"});
		CorpVO[] corpvos= 	(CorpVO[]) QueryDeCodeUtils.decKeyUtils(new String[]{"unitname","legalbodycode","phone1","phone2","unitshortname"}, new CorpVO[]{corp}, 1);
		//CorpCache.getInstance().add(corp.pk_corp, corpvos[0]);
		return "ok";
	}
	
	private boolean isSeal(CorpVO vo, CorpVO oldVO) {
		boolean isSeal = false; // 是否封存
		if (vo.getIsseal().booleanValue()) {
			if (oldVO.getIsseal().booleanValue()) {
				vo.setSealeddate(oldVO.getSealeddate());
			} else {
				isSeal = true;
			}
		} else {
			vo.setSealeddate(null);
		}
		return isSeal;
	}


	@Override
	public CorpVO updateReturnSelf(CorpVO corp) throws DZFWarpException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateAccountFlag(String pkCorp, boolean bHasAccount)
			throws DZFWarpException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAccountVO(CorpVO corpVO) throws DZFWarpException {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertAccountVO(CorpVO corpVO) throws DZFWarpException {
		// TODO Auto-generated method stub

	}

	/**
	 * 此处插入方法说明。
	 * 
	 * 该方法检查单位编码和单位名称的唯一性，如果是新建公司时的检查，isInsert为true, 如果是修改公司时的检查，isInsert为false;
	 * 
	 * 创建日期：(2001-11-23 15:37:01) 作者：李充蒲
	 * 
	 * @return boolean
	 * @param corpVO
	 *            nc.vo.bd.CorpVO
	 * @param isInsert
	 *            boolean
	 * @exception BDException
	 *                异常说明。 230修改
	 */
	public boolean checkIsOnly(CorpVO condCorpVO, boolean isInsert)
			throws DZFWarpException {
		StringBuilder sbCode = new StringBuilder(
				"select count(1) from bd_corp where innercode='").append(
				condCorpVO.getInnercode()).append("' and fathercorp = '"+condCorpVO.getFathercorp()+"'");
		StringBuilder sbName = new StringBuilder(
				"select count(1) from bd_corp where unitname='").append(
				condCorpVO.getUnitname()).append("' and fathercorp = '"+condCorpVO.getFathercorp()+"'");
		if (!isInsert) {
			sbCode.append(" and pk_corp!='").append(condCorpVO.getPrimaryKey())
					.append("'");
			sbName.append(" and pk_corp!='").append(condCorpVO.getPrimaryKey())
					.append("'");
		}
		BigDecimal ojbCodeNum = (BigDecimal) singleObjectBO.executeQuery(
				sbCode.toString(), null, new ColumnProcessor());
		Integer repeatCodeNum = ojbCodeNum.intValue();
		BigDecimal objNameNum = (BigDecimal) singleObjectBO.executeQuery(
				sbName.toString(), null, new ColumnProcessor());
		Integer repeatNameNum = objNameNum.intValue();

		if (repeatCodeNum > 0) {
			throw new BusinessException("客户编码["+condCorpVO.getInnercode()+"]不能重复。");
		} else if (repeatNameNum > 0) {
			throw new BusinessException("客户名称["+CodeUtils1.deCode(condCorpVO.getUnitname())+"]不能重复。");
		}
		return false;
	}

	/**
	 * 如果父PK不为空,检查父PK对应的公司VO是否存在 如不存在,抛出指定父结点不存在异常
	 * 
	 * @param fatherPk
	 * @throws BusinessException
	 */
	private void checkFatherExist(String fatherPk) throws DZFWarpException {
		if (fatherPk != null) {
			CorpVO father = findCorpVOByPK(fatherPk);
			if (father == null) {
				throw new BusinessException("上级公司不存在"); // 上级公司不存在
			}
		}
	}

	/**
	 * 通过主键获得VO对象。
	 * 
	 * 创建日期：(2001-5-16)
	 * 
	 * @return nc.vo.bd.CorpVO
	 * @param key
	 *            String
	 * @exception BDException
	 *                异常说明。
	 */
	public CorpVO findCorpVOByPK(String key) throws DZFWarpException {

		if (IGlobalConstants.currency_corp.equals(key)) {
			CorpVO groupCorp = new CorpVO();
			groupCorp.setPk_corp(IGlobalConstants.currency_corp);
			return groupCorp;
		}

		CorpVO corp = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class,
				key);

		return corp;
	}

	@Override
	public CorpVO queryCorpByName(String unitname) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		try {
			sp.addParam(CodeUtils1.enCode(unitname));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CorpVO[] vos = (CorpVO[])singleObjectBO.queryByCondition(CorpVO.class, " unitname = ? and nvl(dr,0) = 0 ", sp);
		if(vos == null || vos.length == 0)
			return null;
		return vos[0];
	}

}