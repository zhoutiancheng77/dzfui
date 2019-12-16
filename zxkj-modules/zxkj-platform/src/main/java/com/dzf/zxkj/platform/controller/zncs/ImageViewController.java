package com.dzf.zxkj.platform.controller.zncs;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageCommonPath;
import com.dzf.zxkj.platform.model.image.ImageLibraryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.SystemUtil;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/zncs/gl_imgview")
public class ImageViewController extends BaseController {


    private IUserService userService;
    private IImageGroupService gl_pzimageserv;
    @Autowired
    private ICorpService corpService;

    private Logger log = Logger.getLogger(this.getClass());

    public IUserService getUserService() {
        return userService;
    }
    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }
    public IImageGroupService getImageGroupService() {
        return gl_pzimageserv;
    }
    @Autowired
    public void setImageGroupService(IImageGroupService gl_pzimageserv) {
        this.gl_pzimageserv = gl_pzimageserv;
    }

    //根据条件查询图片,以流的形式显示
    @RequestMapping("/search")
    public ReturnData<Grid> search(@RequestParam("id") String Pk_image_library, @RequestParam("pk_corp")String pk_cprp_ser,
                                   @RequestParam("name")String imgname, String isSmall, String isMiddle,
                                   HttpServletResponse response, HttpSession session){
        Grid grid = new Grid();
        grid.setSuccess(false);

        //当前登录公司ID
        String pk_corp = SystemUtil.getLoginCorpId();
//        String Pk_image_library = getRequest().getParameter("id");
//        //查询结果中数据库存储的公司ID
//        String pk_cprp_ser = getRequest().getParameter("pk_corp");
//        String imgname = getRequest().getParameter("name");
//        String isSmall = getRequest().getParameter("isSmall");
//        String isMiddle= getRequest().getParameter("isMiddle");
//        HttpServletResponse response = getResponse();

//        Set<String> corpSet = userService.querypowercorpSet(SystemUtil.getLoginUserId());
//        if(pk_corp != null && pk_corp.trim().length() > 0){
//            if(!corpSet.contains(pk_corp)){
//                grid.setSuccess(false);
//                grid.setMsg("查询图片失败,公司错误！");
//                return  ReturnData.error().data(grid);
//            }
//        }
        ServletOutputStream sos = null;
        FileInputStream fis = null;
        InputStream is=null;
        try{
            ImageLibraryVO imglibvo = gl_pzimageserv.queryLibByID(pk_cprp_ser, Pk_image_library);
            String imgPathName = null;
            String type = null;
            if(imglibvo != null && imgname.equals(imglibvo.getImgname())){
                if(DZFBoolean.TRUE.toString().equals(isSmall)){
                    imgPathName = imglibvo.getSmallimgpath();
                    imgPathName = StringUtil.isEmpty(imgPathName) ? imglibvo.getImgpath() : imgPathName;
                }else if(DZFBoolean.TRUE.toString().equals(isMiddle)){
                    imgPathName = imglibvo.getMiddleimgpath();
                    imgPathName = StringUtil.isEmpty(imgPathName) ? imglibvo.getImgpath() : imgPathName;
                }else{
                    imgPathName = imglibvo.getImgpath();
                }

                if(imgPathName.startsWith("ImageOcr")){
                    type="ImageOcr";
                }else{
//					int index = imgPathName.lastIndexOf("/") == -1 ?  imgPathName.lastIndexOf("\\") : imgPathName.lastIndexOf("/");
//					imgPathName = imgPathName.substring(index + 1);
                    type="vchImg";
                }

            }
            CorpVO corpVO2 = corpService.queryByPk(pk_cprp_ser);//图片浏览查询框中公司pk
            File dir =  getImageFolder(type, corpVO2, imgPathName, imgname);
            String lujing = dir.getAbsolutePath();
            File file  = new File(lujing);

//			if(pk_corp.equals(pk_cprp_ser)){

            response.addHeader("Content-Disposition", "inline;filename=" + imgname);

            response.setContentType("image/jpeg");

//            if(corpSet.contains(pk_cprp_ser)){
            if(true){
                if(!file.exists()){
                   // Resource exportTemplate = new ClassPathResource("img"+ File.separator;+ fileName);
                  //  String pathNoExist = session.getServletContext().getRealPath("/")
                         //   + "img" + File.separator + "picnoexist.jpg";

                    String pathNoExist = "img"+ File.separator+ "picnoexist.jpg";

                    Resource exportTemplate = new ClassPathResource(pathNoExist);
                     is = exportTemplate.getInputStream();
                   // File picNoExist = new File(pathNoExist);

                   // fis = new FileInputStream(picNoExist);
                    sos = response.getOutputStream();

                    //读取文件流
                    int i = 0;
                    byte[] buffer = new byte[1024];
                    while((i = is.read(buffer)) != -1){
                        //写文件流
                        sos.write(buffer, 0, i);
                    }
                    sos.flush();
                    is.close();

                    grid.setSuccess(true);
                    log.info("图片不存在!");
                }else{
                    fis = new FileInputStream(file);
                    sos = response.getOutputStream();

                    //读取文件流
                    int i = 0;
                    byte[] buffer = new byte[1024];
                    while((i = fis.read(buffer)) != -1){
                        //写文件流
                        sos.write(buffer, 0, i);
                    }
                    sos.flush();
                    fis.close();

                    grid.setSuccess(true);
                    log.info("查询图片成功!");
                }
            }else{
               // String pathNoAuth = session.getServletContext().getRealPath("/")
                 //       + "img" + File.separator + "picnoauth.jpg";
              //  File picNoAuth = new File(pathNoAuth);
                String pathNoExist = "img"+ File.separator+ "picnoexist.jpg";

                Resource exportTemplate = new ClassPathResource(pathNoExist);
                is = exportTemplate.getInputStream();

                //fis = new FileInputStream(picNoAuth);
                sos = response.getOutputStream();

                //读取文件流
                int i = 0;
                byte[] buffer = new byte[1024];
                while((i = is.read(buffer)) != -1){
                    //写文件流
                    sos.write(buffer, 0, i);
                }
                sos.flush();
                is.close();

                grid.setSuccess(true);
                log.info("无权查看！");
            }
        }catch (Exception e) {
//			grid.setSuccess(false);
            printErrorLog(grid, e, "查询图片失败！");
        }finally {
            if(sos != null){
                try{
                    sos.close();
                }catch(Exception e){
                    //e.printStackTrace();
                }
            }
            if(fis != null){
                try{
                    fis.close();
                }catch(Exception e){
                    //e.printStackTrace();
                }
            }
            if(is != null){
                try{
                    is.close();
                }catch(Exception e){
                    //e.printStackTrace();
                }
            }

        }
        return ReturnData.ok().data(grid);
    }

    public static File getImageFolder(String type,CorpVO corpvo,String imgPathName, String imgName) throws FileNotFoundException {
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

    @RequestMapping("/getPicStatistics")
    public ReturnData<Json> getPicStatistics(@RequestBody Map<String,String> param) {
        Json json = new Json();
        try {
            String pk_corp = param.get("pk_corp");
            String beginDate = param.get("beginDate");
            String endDate = param.get("endDate");
            String serdate = param.get("serdate");
            if (StringUtil.isEmpty(pk_corp)) {
                pk_corp = SystemUtil.getLoginCorpId();
            } else {
                Set<String> corpSet = userService.querypowercorpSet(SystemUtil.getLoginUserId());
                if(!corpSet.contains(pk_corp)){
                    json.setSuccess(false);
                    json.setMsg("无权操作");
                    return ReturnData.error().data(json);
                }
            }
            Map<String, Object> statistics = gl_pzimageserv.getPicStatistics(
                    pk_corp, beginDate, endDate, serdate);
            json.setRows(statistics);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
        }
        return ReturnData.ok().data(json);
    }
    // 拆分图片组
    @RequestMapping("/splitGroup")
    public ReturnData<Json> splitGroup(String pk_image_group,String pk_corp) {
        Json json = new Json();
        try {
            gl_pzimageserv.processSplitGroup(pk_corp, pk_image_group);
            json.setMsg("拆分成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "拆分失败");
        }
        return ReturnData.ok().data(json);
    }

}
