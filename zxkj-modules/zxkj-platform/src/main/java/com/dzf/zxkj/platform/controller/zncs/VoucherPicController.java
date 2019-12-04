package com.dzf.zxkj.platform.controller.zncs;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageCommonPath;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.ImageLibraryVO;
import com.dzf.zxkj.platform.model.pjgl.PjCheckBVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.SystemUtil;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/zncs/gl_imageuplad")
public class VoucherPicController extends BaseController {


    private Logger log = Logger.getLogger(this.getClass());
    @Autowired
    private IImageGroupService gl_pzimageserv;
    private static final int BUFFER_SIZE = 16 * 1024;
    @Autowired
    private IQmgzService qmgzService;
    @Autowired
    private IUserService userService;
    @Autowired
    private ICorpService corpService;
//    @Autowired
//    private IPjsjManageService ipjsjmanageServ;
//    @Autowired
//    private IPjsjManageService pjsj_serv;

    public IQmgzService getQmgzService() {
        return qmgzService;
    }
    @Autowired
    public void setQmgzService(IQmgzService qmgzService) {
        this.qmgzService = qmgzService;
    }
    @RequestMapping("/uploadMultiFile")
    public ReturnData<Json> uploadMultiFile(MultipartFile[] infiles) {
        Json json = new Json();
        try {
            CorpVO corpvo = SystemUtil.getLoginCorpVo();
            UserVO userVo = SystemUtil.getLoginUserVo();
//            ((MultiPartRequestWrapper) getRequest()).getParameterMap();
//            File[] infiles = ((MultiPartRequestWrapper) getRequest())
//                    .getFiles("file");
//            String[] filenames = ((MultiPartRequestWrapper) getRequest())
//                    .getFileNames("file");

            if (infiles == null||infiles.length==0) {
                throw new Exception("文件为空");
            }
			/*if (!filename.endsWith(".xlsx")) {
				throw new Exception("导入文件格式不正确");
			}*/
            ImageGroupVO ig = null;
            String imgName = "";
            ig = new ImageGroupVO();
            ig.setPk_corp(corpvo.getPk_corp());
            ig.setCoperatorid(userVo.getCuserid());
            ig.setDoperatedate(new DZFDate());
            long maxCode = getGl_pzimageserv().getNowMaxImageGroupCode(corpvo.getPk_corp());
            if(maxCode > 0){
                ig.setGroupcode(maxCode + 1 + "");
            }else{
                ig.setGroupcode(getCurDate() + "0001");
            }

            for(int i=0;i<infiles.length;i++){

                String nameSuffix = infiles[i].getOriginalFilename().substring(infiles[i].getOriginalFilename().lastIndexOf("."));
                String ds = "";	 //getRequest().getRealPath("/").replaceAll("\\\\","/");
//	        	  ds = ds.substring(0,ds.length() -1);
                File dir =  getImageFolder("vchImg", corpvo,ds);

                File destFile = new File(dir,ig.getGroupcode() + "-" + getLibraryNum(i+1) + nameSuffix);
                try {
                    copy(infiles[i], destFile);
                } catch(Exception e) {
                    throw new Exception("保存图片失败");
                }

                ImageLibraryVO il = new ImageLibraryVO();
                il.setImgpath(corpvo.getUnitcode() + "/" + getCurDate() + "/" + destFile.getName());
                il.setImgname(destFile.getName());
                il.setPk_corp(corpvo.getPk_corp());
                il.setCoperatorid(userVo.getCuserid());
                il.setDoperatedate(new DZFDate());
                ig.addChildren(il);
            }
            getGl_pzimageserv().save(ig);
            json.setRows(null);
            json.setSuccess(true);
            json.setMsg("保存成功!");
        } catch (Exception e) {
//			log.info("导入文件失败!" + e.getMessage());
//			json.setSuccess(false);
//			json.setMsg("导入文件失败!\n" + e.getMessage());
            printErrorLog(json, e, "导入文件失败!\n");
        }
        return ReturnData.ok().data(json);
    }

    @RequestMapping("/uploadSingleFile")
    public ReturnData<Json> uploadSingleFile(MultipartFile infiles,String pjlx,String selMon,String selYear,String pk_corp,String msgkey,String g_id) {
//        String pjlx = param.get("pjlx");
//        String selMon= param.get("selMon");
//        String selYear= param.get("selYear");
//        String pk_corp= param.get("pk_corp");
//        String msgkey= param.get("msgkey");
//        String g_id= param.get("g_id");
        Json json = new Json();
        json.setSuccess(false);
//		json.setStatus(-200);
        pjlx = "-1".equals(pjlx) ? null : pjlx;//如果为-1，表明传的业务类型为空
        UserVO userVo = SystemUtil.getLoginUserVo();
        CorpVO corpvo = null;
        try {
            if (StringUtil.isEmpty(pk_corp)) {
                pk_corp = SystemUtil.getLoginCorpId();
            } else {
                Set<String> powerCorpSet = userService.querypowercorpSet(userVo.getPrimaryKey());
                if (!powerCorpSet.contains(pk_corp)) {
                    throw new BusinessException("无权操作");
                }
            }

            corpvo = corpService.queryByPk(pk_corp);
            //((MultiPartRequestWrapper) getRequest()).getParameterMap();
//            File[] infiles = ((MultiPartRequestWrapper) getRequest())
//                    .getFiles("file");
//            String[] filenames = ((MultiPartRequestWrapper) getRequest())
//                    .getFileNames("file");
            if (infiles == null||infiles.getSize()==0) {
                throw new BusinessException("文件为空");
            }

            String period = selYear+"-"+selMon;
            ImageLibraryVO il = gl_pzimageserv.uploadSingFile(corpvo, userVo, infiles,
                    g_id, period, pjlx);
            //选择票据类型不在生成临时凭证
//			gl_pzimageserv.saveCreatePz(il, corpvo, g_id, pjlxType);

            //发送消息
            gl_pzimageserv.saveMsg(msgkey, pk_corp, selYear, selMon, SystemUtil.getLoginUserVo(), il);

            json.setRows(il);
            json.setSuccess(true);
            json.setMsg("保存成功!");
        } catch (Exception e) {
//			log.info("上传凭证图片失败!" + e.getMessage());
//			json.setSuccess(false);
//			json.setMsg("上传失败!\n" + e.getMessage());
           // pjsj_serv.updateCountByPjlx(pk_corp, selYear + "-" + selMon, pjlxType, null, null, userVo, corpvo, -1);//统计数
            printErrorLog(json, e, "上传失败！");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_OTHERVOUCHER, "上传图片：" + selYear + "年" + selMon + "月", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private static String getLibraryNum(int n){
        String tmp = n + "";
        while(tmp.length() < 3){
            tmp = "0" + tmp;
        }
        return tmp;
    }

    private static File getImageFolder(String type,CorpVO corpvo,String directory){
        File dir = null;
        if("vchImg".equals(type)){
            String imgfolder = ImageCommonPath.getDataCenterPhotoPath()  + File.separator +  corpvo.getUnitcode() + File.separator + getCurDate();
            String folder = directory + imgfolder; //DZFConstant.DZF_KJ_UPLOAD_BASE + imgfolder;
            dir = new File(folder);
            if(!dir.exists()) {
                dir.mkdirs();
            }
        }
        return dir;
    }

    //	获取当前年月日
    private static String getCurDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(Calendar.getInstance().getTime());
    }

    private static void copy(MultipartFile src, File dst) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(src.getInputStream(), BUFFER_SIZE);
            out = new BufferedOutputStream(new FileOutputStream(dst),
                    BUFFER_SIZE);
            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
    }

    @RequestMapping("/queryPjlxTypes")
    public ReturnData<Json> queryPjlxTypes(@RequestParam("custid") String custid){
        Json json = new Json();
        try {

           // List<PjCheckBVO> bvoList = ipjsjmanageServ.queryPjlxTypes(custid);
            json.setSuccess(true);
           // json.setRows(bvoList);
            json.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
        }

        return ReturnData.ok().data(json);
    }

    @PostMapping("/beforeCheck")
    public ReturnData<Json> beforeCheck(@RequestBody Map<String, String> param){
        String selMon =  param.get("selMon");
        String selYear =  param.get("selYear");
        String pk_corp =  param.get("pk_corp");
        Json json = new Json();
        try {
            gl_pzimageserv.isQjSyJz(pk_corp, selYear + "-" + selMon);

            json.setSuccess(true);
            json.setMsg("校验成功");
        } catch (Exception e) {
            if(e instanceof BusinessException
                    && !StringUtil.isEmpty(e.getMessage())
                    && e.getMessage().startsWith("该月份已经结转损益")){
                json.setStatus(-150);
            }
            printErrorLog(json, e, "校验失败");
        }

        return ReturnData.ok().data(json);
    }

    public IImageGroupService getGl_pzimageserv() {
        return gl_pzimageserv;
    }

    @Autowired
    public void setGl_pzimageserv(IImageGroupService gl_pzimageserv) {
        this.gl_pzimageserv = gl_pzimageserv;
    }


}
