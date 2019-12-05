package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.PrintSettingVO;
import com.dzf.zxkj.platform.service.bdset.IPrintSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("gl_print_setting_serv")
public class PrintSettingServiceImpl implements IPrintSettingService {
	private SingleObjectBO singleObjectBO;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	@Override
	public PrintSettingVO query(String pk_corp, String user_id, String nodeName)
			throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(nodeName);
		String condition = " pk_corp = ? and nodename = ? and nvl(dr,0)=0 ";
		PrintSettingVO[] rs = (PrintSettingVO[]) singleObjectBO
				.queryByCondition(PrintSettingVO.class, condition, sp);
		if (rs.length > 0) {
//			//首先判断里集合里是否有该用户的习惯，没有取公司习惯，没有则返回null
//			String cuserid;
//			PrintSettingVO setting = null;//公司级习惯
//			for(PrintSettingVO vo : rs){
//				cuserid = vo.getCuserid();
//				if(StringUtil.isEmpty(cuserid)){
//					setting = vo;
//				}else if(user_id.equals(cuserid)){
//					return vo;
//				}
//			}
//			
//			if(setting != null){
//				return setting;
//			}
			
			return rs[0];
		}
		return null;
	}

	@Override
	public void save(PrintSettingVO vo) throws DZFWarpException {
//		String corpids = vo.getCorpids();
//		if(!StringUtil.isEmpty(corpids)){//按公司级设置
			saveMulSetting(vo);
//		}
//		else{
//			saveSinSetting(vo);
//		}
		
	}


	@Override
	public void saveMulColumn(PrintSettingVO vo) throws DZFWarpException {
		String corpids = vo.getCorpids();
		String pk_corp = vo.getPk_corp();
		String nodename= vo.getNodename();
		corpids = StringUtil.isEmpty(corpids) ? pk_corp : corpids;
		List<PrintSettingVO> list = new ArrayList<PrintSettingVO>();//公司习惯
		String[] pks = corpids.split(",");
		PrintSettingVO vo1;
		for(String pk : pks){
			vo1 = (PrintSettingVO) vo.clone();
			vo1.setPk_corp(pk);
			list.add(vo1);
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(nodename);
		// 直接更新了，不走删除，插入了
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_settings_print y Where nvl(dr,0)=0 and nodename = ? and ");
		sf.append(SqlUtil.buildSqlForIn("pk_corp", pks));
		List<PrintSettingVO> qrylist =  (ArrayList<PrintSettingVO>)singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(PrintSettingVO.class));
		if (qrylist!=null && qrylist.size()>0) {
			if (!StringUtil.isEmpty(vo.getUpdatecolumn())){
				String[] columns = vo.getUpdatecolumn().split(",");
				for (PrintSettingVO votemp: qrylist) {
					for (String str:columns) {
						votemp.setAttributeValue(str, vo.getAttributeValue(str));
					}
				}
				singleObjectBO.updateAry(qrylist.toArray(new PrintSettingVO[0]), columns);
			}
		} else {
			//添加公司级习惯
			singleObjectBO.insertVOArr(pk_corp,
					list.toArray(new PrintSettingVO[0]));
		}
	}


	//多公司习惯设置
	private void saveMulSetting(PrintSettingVO vo){
		String corpids = vo.getCorpids();
		String pk_corp = vo.getPk_corp();
		String nodename= vo.getNodename();
		corpids = StringUtil.isEmpty(corpids) ? pk_corp : corpids;
		
		List<PrintSettingVO> list = new ArrayList<PrintSettingVO>();//公司习惯
		
		String[] pks = corpids.split(",");
		PrintSettingVO vo1;
		for(String pk : pks){
			vo1 = (PrintSettingVO) vo.clone();
			vo1.setPk_corp(pk);
			list.add(vo1);
		}

		//先将习惯清除，然后设置公司习惯
		SQLParameter sp = new SQLParameter();
		sp.addParam(nodename);

		StringBuffer sf = new StringBuffer();
		sf.append(" delete from ynt_settings_print y Where nvl(dr,0)=0 and nodename = ? and ");
		sf.append(SqlUtil.buildSqlForIn("pk_corp", pks));
		singleObjectBO.executeUpdate(sf.toString(), sp);
		
		//添加公司级习惯
		singleObjectBO.insertVOArr(pk_corp, 
				list.toArray(new PrintSettingVO[0]));
	}
	
	//先注释掉
//	private void saveSinSetting(PrintSettingVO vo){
//		PrintSettingVO oldVO = query(vo.getPk_corp(), vo.getCuserid(),
//				vo.getNodename());
//		if (oldVO != null) {
//			vo.setPk_print_setting(oldVO.getPk_print_setting());
//		}
//		singleObjectBO.saveObject(vo.getPk_corp(), vo);
//	}
}
