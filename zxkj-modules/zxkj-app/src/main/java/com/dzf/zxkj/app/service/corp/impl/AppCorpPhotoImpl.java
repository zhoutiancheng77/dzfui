package com.dzf.zxkj.app.service.corp.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.RegisterRespBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.corp.IAppCorpPhoto;
import com.dzf.zxkj.app.utils.CryptUtil;
import com.dzf.zxkj.app.utils.PinyinUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageCommonPath;
import com.dzf.zxkj.platform.model.sys.CorpDocVO;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 公司图片信息维护
 * 
 * @author zhangj
 *
 */
@Slf4j
@Service("corpPhotoservice")
public class AppCorpPhotoImpl implements IAppCorpPhoto {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	private String[] strs= new String[]{"logo","permit","orgcodecer","taxregcer","bankopcer","stacer"};
	
	private String[] strspath= new String[]{"logopath","permitpath","orgcodecerpath","taxregcerpath","bankopcerpath","stacerpath"};

	private String[] tips = new String[]{
			"企业logo维护成功!","企业营业执照维护成功!","企业组织机构代码证维护成功!",
			"企业税务登记证维护成功!","企业银行开户许可证维护成功!","企业统计登记证维护成功!"
	};
	
	private String[] docnames = new String[]{"企业logo","企业营业执照","企业组织机构代码证","企业税务登记证","企业银行开户许可证","企业统计登记证"};
	@Override
	public String upCorpPhoto(UserBeanVO userBean, RegisterRespBeanVO bean, Integer zzlx) throws DZFWarpException {
		// 文件保存
		String photostr = getPhotoContent(userBean, zzlx);
		if(StringUtil.isEmpty(photostr)){
			return "";
		}
		String photostrv = photostr.replaceAll(">", "").replaceAll("<", "");
		InputStream inputStream = new ByteArrayInputStream(Hex.decode(photostrv));
		String name = StringUtil.isEmpty(userBean.getPk_tempcorp()) ? userBean.getPk_corp() : userBean.getPk_tempcorp();
		String imagename = getPhotoPath(name, zzlx, userBean.getPhototype());
		
		saveCorpFilemsg(inputStream, imagename);
		
		//保存公司信息
		saveCorpDoc(userBean.getPk_corp(), userBean.getPk_tempcorp(),zzlx, userBean.getAccount(),userBean.getPhototype());
		
		bean.setAttributeValue(strspath[zzlx.intValue()],CryptUtil.getInstance().encryptAES(imagename));//路径赋值
		bean.setRescode(IConstant.DEFAULT);
//		if (!StringUtil.isEmpty(photostr)) {
			 bean.setResmsg(tips[zzlx.intValue()]);
//		}
		return null;
	}


	private String getPhotoContent(UserBeanVO userBean, Integer zzlx) {
		String content = "";
		content = (String) userBean.getAttributeValue(strs[zzlx.intValue()]);
		return content;
	}

	/**
	 * 保存公司信息
	 * 
	 * @param inputStream
	 * @param imagename
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Override
	public void saveCorpFilemsg(InputStream inputStream, String imagename) {
		File imagefile = new File(imagename);
		if (!imagefile.exists()) {
			try {
				imagefile.createNewFile();
			} catch (IOException e) {
				throw new WiseRunException(e);
			}
		}
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(imagefile);
			int bytesWritten = 0;
			int byteCount = 0;

			byte[] bytes = new byte[1024 * 1024];

			while ((byteCount = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, bytesWritten, byteCount);
				bytesWritten += byteCount;
			}
			inputStream.close();
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			throw new WiseRunException(e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error(e.getMessage(),e);
				}
			}
		}
	}

	private String getPhotoPath(String name, Integer zzlx, String imagetype) {

		String photoname = "";
		switch (zzlx.intValue()) {
		case 0:// logo
			photoname = ImageCommonPath.getLogoUnZipPath(name, imagetype);
			break;
		case 1:// 营业执照
			photoname = ImageCommonPath.getPermitUnZipPath(name, imagetype);
			break;
		case 2:// 组织机构代码证
			photoname = ImageCommonPath.getOrgCodeCerUnZipPath(name, imagetype);
			break;
		case 3:// 税务登记证
			photoname = ImageCommonPath.getTaxRegCerUnZipPath(name, imagetype);
			break;
		case 4:// 银行开户许可证
			photoname = ImageCommonPath.getBankOpCerUnZipPath(name, imagetype);
			break;
		case 5:// 统计登记证
			photoname = ImageCommonPath.getStaCerUnZipPath(name, imagetype);
			break;
		default:
			break;
		}

		return photoname;
	}
	
	/**
	 * 为文件名添加扩展名
	 */
	private static String forceFileExt(String filename, String extname){
		if(!extname.startsWith("."))
			extname = "." + extname;
		int dotindex = filename.lastIndexOf(".");
		if(dotindex<0) return filename + extname;
		return filename.substring(0, dotindex) + extname;
	}

	@Override
	public String getCorpPhoto(UserBeanVO userBean, LoginResponseBeanVO bean) throws DZFWarpException {
		String[] types = new String[] { ".jpg", ".png", ".jpeg", ".bmp", ".gif" };
		String imagelogoname = null;
		File logoimagefile = null;
		
		for(int i=0;i<strspath.length;i++){
			for (String type : types) {
				String name = StringUtil.isEmpty(userBean.getPk_tempcorp())?userBean.getPk_corp():userBean.getPk_tempcorp();
				imagelogoname = getPhotoPath(name, i, type);
				logoimagefile = new File(imagelogoname);
				if (logoimagefile.exists()) {
					bean.setAttributeValue(strspath[i], CryptUtil.getInstance().encryptAES( logoimagefile.getPath()) );
				}else{
					imagelogoname = ImageCommonPath.getLogoUnZipPath(PinyinUtil.getPinYin(userBean.getCorpname()), type);
					logoimagefile = new File(imagelogoname);
					if(logoimagefile.exists()){
						bean.setAttributeValue(strspath[i],CryptUtil.getInstance().encryptAES(logoimagefile.getPath()));
					}
				}
			}
		}
		return null;
	}


	@Override
	public void saveCorpDoc(String pk_corp,String pk_tempcorp,Integer zzlx,String usercode,String phototype) throws DZFWarpException {
		
		if(StringUtil.isEmpty(pk_corp) || Common.tempidcreate.equals(pk_corp)){
			return;
		}
		String name = StringUtil.isEmpty(pk_tempcorp) ? pk_corp : pk_tempcorp; //图片名字
		
		String pathname = forceFileExt(name, phototype);
		
		String path = getPhotoPath(name, zzlx, phototype);//图片路径
		
		String docname = docnames[zzlx]+phototype;//显示名字
		
		File imagefile = new File(path);
		if (!imagefile.exists()) {
			return;//图片信息不存在，则返回不生成
		}
		
		//避免重复的生成数据pk_corp + vfilepath + doctemp 来判断唯一性
		StringBuffer qrysql = new StringBuffer();
		qrysql.append(" select * from ynt_corpdoc  ");
		qrysql.append(" where nvl(dr,0)=0 and pk_corp = ? ");
		qrysql.append(" and vfilepath = ? and doctemp = ?  ");
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(path);
		sp.addParam(pathname);
		
		List<CorpDocVO> docvos =  (List<CorpDocVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(CorpDocVO.class));
		
		if(docvos!=null && docvos.size() == 1){
			docvos.get(0).setDocTime(new DZFDateTime());
			singleObjectBO.update(docvos.get(0), new String[]{"doctime"});
		}else if(docvos==null || docvos.size() == 0){
			CorpDocVO docvo = new CorpDocVO();
			docvo.setDocName(docname);
			docvo.setPk_corp(pk_corp);
			docvo.setDocOwner(usercode);
			docvo.setDocTime(new DZFDateTime());
			docvo.setDocTemp(pathname);
			docvo.setVfilepath(path);
			
			singleObjectBO.saveObject(pk_corp, docvo);
		}
	}


	@Override
	public void saveCorpDocs(String pk_corp, String pk_tempcorp, String usercode) throws DZFWarpException {
		String[] types = new String[] { ".jpg", ".png", ".jpeg", ".bmp", ".gif" };
		for(String str:types){
			for(int i=0;i<5;i++){
				saveCorpDoc(pk_corp, pk_tempcorp, i, usercode, str);
			}
		}
	}

}
