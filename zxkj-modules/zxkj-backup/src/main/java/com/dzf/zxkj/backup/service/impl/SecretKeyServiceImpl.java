package com.dzf.zxkj.backup.service.impl;

import com.alibaba.fastjson.JSON;
import com.dzf.zxkj.backup.service.ISecretKeyService;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.secret.RsaVo;
import com.dzf.zxkj.platform.model.secret.SecretKeyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("secretkeyser")
@Slf4j
public class SecretKeyServiceImpl implements ISecretKeyService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	//private Logger log  = Logger.getLogger(this.getClass());

	@Override
	public Map<String, String> getRsaCodeValue(String filename) throws DZFWarpException {
		Map<String, String> resmap = new HashMap<String, String>();
		InputStream in = null;
		ByteArrayOutputStream baos = null;
		byte[] byteData = null;
		try {
			// 一次读多个字节
			byte[] tempbytes = new byte[100];
			baos = new ByteArrayOutputStream();
			int byteread = 0;
			in = this.getClass().getResourceAsStream("/" + filename);
			// 读入多个字节到字节数组中，byteread为一次读入的字节数
			while ((byteread = in.read(tempbytes)) != -1) {
				baos.write(tempbytes, 0, byteread);
			}
			byteData = baos.toByteArray();
		} catch (Exception e1) {
			log.error("错误",e1);
			throw new BusinessException("加密文件不存在！");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {

				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					log.error("错误",e);
				}
			}
		}
		
		RsaVo vo = new RsaVo();
		try {
			String value = new String(byteData,"UTF-8");
			vo = JSON.parseObject(value,RsaVo.class);
		} catch (UnsupportedEncodingException e) {
			log.error("错误",e);
			throw new WiseRunException(e);
		}
		resmap.put("prikey", vo.getPrikey());
		resmap.put("pubkey", vo.getPubkey());
		return resmap;
	}

	@Override
	public List<SecretKeyVo>  querySecretKeyFromNo(String pk_corp, Integer isourcesys, String versionno)
			throws DZFWarpException {
		
		if(StringUtil.isEmpty(pk_corp)){
			throw new BusinessException("公司不能为空");
		}
		
		if(isourcesys == null){
			throw new BusinessException("来源不能为空");
		}
		
		if(StringUtil.isEmpty(versionno) ){
			throw new BusinessException("版本号不能为空");
		}
		
		StringBuffer qrysql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		qrysql.append(" select * ");
		qrysql.append(" from  "+SecretKeyVo.TABLE_NAME);
		qrysql.append("  where nvl(dr,0)=0 ");
		qrysql.append(" and pk_corp =? ");
		qrysql.append(" and isourcesys = ?  ");
		qrysql.append(" and vversionno = ?  ");
		sp.addParam(pk_corp);
		sp.addParam(isourcesys);
		sp.addParam(versionno);
		
		
		List<SecretKeyVo> list = (List<SecretKeyVo>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(SecretKeyVo.class));
		
		return list;
	}

	@Override
	public List<SecretKeyVo>  querySecretKeyFromDate(String pk_corp, Integer isourcesys, String versiondate,String versionno )
			throws DZFWarpException {

		if(StringUtil.isEmpty(pk_corp)){
			throw new BusinessException("公司不能为空");
		}
		
		if(isourcesys == null){
			throw new BusinessException("来源不能为空");
		}
		
		if(StringUtil.isEmpty(versiondate) && StringUtil.isEmpty(versionno)){
			throw new BusinessException("版本启用时间不能为空");
		}
		
		StringBuffer qrysql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		qrysql.append(" select * ");
		qrysql.append(" from  "+SecretKeyVo.TABLE_NAME);
		qrysql.append("  where nvl(dr,0)=0 ");
		qrysql.append(" and pk_corp =? ");
		qrysql.append(" and isourcesys = ?  ");
		sp.addParam(pk_corp);
		sp.addParam(isourcesys);
		if(!StringUtil.isEmpty(versionno)){//先通过版本号查询
			qrysql.append(" and vversionno = ?  ");
			sp.addParam(versionno);
		}else{//如果版本号不存在，则通过日期查询
			qrysql.append(" and dversionbegdate <= ?");
			sp.addParam(versiondate);
		}
		qrysql.append(" order by dversionbegdate desc ");
		
		
		List<SecretKeyVo> list = (List<SecretKeyVo>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(SecretKeyVo.class));
		
		return list;
	}

}
