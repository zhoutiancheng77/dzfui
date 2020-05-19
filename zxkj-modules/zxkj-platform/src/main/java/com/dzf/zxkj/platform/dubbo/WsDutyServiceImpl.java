package com.dzf.zxkj.platform.dubbo;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageCommonPath;
import com.dzf.zxkj.platform.model.image.ImageLibraryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.DutyPayVO;
import com.dzf.zxkj.platform.model.zncs.ReturnData;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.zncs.IInterfaceBill;
import com.dzf.zxkj.platform.service.zncs.IWsDutyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Service
@org.apache.dubbo.config.annotation.Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class WsDutyServiceImpl implements IWsDutyService {
    @Autowired
    private IInterfaceBill iInterfaceBill;
    @Autowired
    private IImageGroupService gl_pzimageserv;
    @Autowired
    private ICorpService corpService;
    @Override
    public ReturnData queryDutyTolalInfo(String period,String corpNames[],String []pkcorps,int page,int rows) {
        ReturnData data = new ReturnData();
        try{
            if(StringUtil.isEmpty(period)){
                throw new BusinessException("期间不能为空!");
            }
            if(pkcorps==null || pkcorps.length==0){
                if(corpNames==null || corpNames.length==0 ){
                    throw new BusinessException("查寻公司不能为空!");
                }
                pkcorps = iInterfaceBill.queryCorpByName(corpNames);
            }

            DutyPayVO[] datas = iInterfaceBill.queryDutyTolalInfo(pkcorps,period,page,rows);
            //DutyPayVO[] data2 = getPageDutydata(datas,page,rows);

            data.setTotal((datas == null ? 0 : datas.length));
            data.setCode("200");
            data.setPkcorps(pkcorps);
            data.setData(datas);
        } catch(Exception e) {
            data.setMessage(e.getMessage());
            data.setCode("500");
        }
        return data;


    }

    @Override
    public InputStream searhImage(String Pk_image_library, String pk_cprp_ser,
                                 String imgname, String isSmall, String isMiddle){
        {
            InputStream is = null;
            FileInputStream fis = null;
            try {
                ImageLibraryVO imglibvo = gl_pzimageserv.queryLibByID(pk_cprp_ser, Pk_image_library);
                String imgPathName = null;
                String type = null;
                if (imglibvo != null && imgname.equals(imglibvo.getImgname())) {
                    if (DZFBoolean.TRUE.toString().equals(isSmall)) {
                        imgPathName = imglibvo.getSmallimgpath();
                        imgPathName = StringUtil.isEmpty(imgPathName) ? imglibvo.getImgpath() : imgPathName;
                    } else if (DZFBoolean.TRUE.toString().equals(isMiddle)) {
                        imgPathName = imglibvo.getMiddleimgpath();
                        imgPathName = StringUtil.isEmpty(imgPathName) ? imglibvo.getImgpath() : imgPathName;
                    } else {
                        imgPathName = imglibvo.getImgpath();
                    }

                    if (imgPathName.startsWith("ImageOcr")) {
                        type = "ImageOcr";
                    } else {
                        type = "vchImg";
                    }

                }
                CorpVO corpVO2 = corpService.queryByPk(pk_cprp_ser);//图片浏览查询框中公司pk
                File dir = getImageFolder(type, corpVO2, imgPathName, imgname);
                String lujing = dir.getAbsolutePath();
                File file = new File(lujing);
                if (!file.exists()) {
                    String pathNoExist = "img"+ File.separator+ "picnoexist.jpg";

                    Resource exportTemplate = new ClassPathResource(pathNoExist);
                    is = exportTemplate.getInputStream();
                } else {
                    fis = new FileInputStream(file);
                    return fis;
                }
            }catch (Exception e){
                if(fis != null){
                    try{
                        fis.close();
                    }catch(Exception e1){
                        //e.printStackTrace();
                    }
                }
                if(is != null){
                    try{
                        is.close();
                    }catch(Exception e1){
                        //e.printStackTrace();
                    }
                }
                return  null;
            }
            return is;
        }
      }

        public  File getImageFolder(String type,CorpVO corpvo,String imgPathName, String imgName) throws FileNotFoundException {
            File dir = null;
            String dateFolder = imgName.substring(0, 8);
            if("vchImg".equals(type)){
                if (imgPathName.indexOf("/") < 0 && imgPathName.indexOf("\\") < 0)	//原始文件名，无目录
                {
                    String imgfolder = ImageCommonPath.getDataCenterPhotoPath()  + "/" +  corpvo.getUnitcode() + "/" + dateFolder;
                    String folder = imgfolder +"/"+ imgPathName; //DZFConstant.DZF_KJ_UPLOAD_BASE + imgfolder;
                    dir = new File(folder);
                }
                else	//已经包含路径的文件名
                {
                    String folder = ImageCommonPath.getDataCenterPhotoPath()  +"/"+ imgPathName;
                    dir = new File(folder);
                }

            }else if("ImageOcr".equals(type)){
//			String imgfolder = ImageCommonPath.getDataCenterPhotoPath()  + "/" +  corpvo.getUnitcode() + "/" + dateFolder;
                String folder = ImageCommonPath.getDataCenterPhotoPath()  +"/"+ imgPathName; //DZFConstant.DZF_KJ_UPLOAD_BASE + imgfolder;
                dir = new File(folder);
            }

            return dir;
        }
}
