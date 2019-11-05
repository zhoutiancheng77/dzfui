package com.dzf.zxkj.operate.log.service;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.base.IOperatorLogService;
import com.dzf.zxkj.common.base.LogQueryParamVO;
import com.dzf.zxkj.common.base.LogRecordVo;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * 日志操作
 * @author zhangj
 *
 */
@Service
public class OperatorLogServiceImpl implements IOperatorLogService {


	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Override
	public List<LogRecordVo> query(LogQueryParamVO paramvo) throws DZFWarpException {
		if(paramvo == null 
				||paramvo.getBegindate1() == null
				||paramvo.getEnddate() == null){
			throw new BusinessException("请录入查询日志起止时间");
		}
		
		SQLParameter sp = new SQLParameter();
		
		StringBuffer qrysql = new StringBuffer();
		qrysql.append(" select d.*,d.doperatedate as opestr, s.user_name as vuser  ");
		qrysql.append(" from ynt_logrecord d ");
		qrysql.append(" left join sm_user s on s.cuserid = d.cuserid ");
		qrysql.append(" where nvl(d.dr,0)=0 and d.pk_corp =  ? ");
		sp.addParam(paramvo.getPk_corp());
		
		if(paramvo.getBegindate1()!=null){
			qrysql.append(" and d.doperatedate >= ?  ");
			sp.addParam(paramvo.getBegindate1()+" 00:00:00");
		}
		
		if(paramvo.getEnddate()!=null){
			qrysql.append(" and d.doperatedate <= ?  ");
			sp.addParam(paramvo.getEnddate()+" 23:59:59");
		}
		
		if(!StringUtil.isEmpty(paramvo.getOtpye())){
			qrysql.append(" and d.iopetype = ?");
			sp.addParam(paramvo.getOtpye());
		}
		
		if (!StringUtil.isEmpty(paramvo.getOmsg())) {
			qrysql.append("  and d.vopemsg like  ? ");
			sp.addParam("%" + paramvo.getOmsg() + "%");
		}
		
	    if(!StringUtil.isEmpty(paramvo.getOpeuser())){
	    	qrysql.append("  and d.cuserid =  ? ");
			sp.addParam(paramvo.getOpeuser());
	    }
	    if(paramvo.getSys_ident() != null ){
            qrysql.append(" and d.sys_ident = ?  ");
            sp.addParam(paramvo.getSys_ident());
        }
		List<LogRecordVo> logrecords =   (List<LogRecordVo>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(LogRecordVo.class));
		//倒序排getDoperatedate,基类增加改写法，判断是否为jdk1.8编译。
		Collections.sort(logrecords, 
				Comparator.comparing((LogRecordVo u1)->(u1.getDoperatedate() == null ? "": u1.getDoperatedate())).reversed());
		return logrecords;
	}
	
	@Override
	public void saveLog(String pk_corp, String username, String ip, Integer type, String msg,Integer ident,String userid) throws DZFWarpException {
		LogRecordVo logvo = new LogRecordVo();
		
		logvo.setPk_corp(pk_corp);
//		logvo.setVuser(username);
		logvo.setVuserip(ip);
		logvo.setIopetype(type);
		logvo.setVopemsg(msg);
		logvo.setDoperatedate(new DZFDateTime().toString());
		logvo.setTs(new DZFDateTime());
		logvo.setSys_ident(ident);
		logvo.setCuserid(userid);
		singleObjectBO.saveObject(pk_corp, logvo);
	}

}
