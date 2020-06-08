package com.dzf.zxkj.platform.controller.taxrpt;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.config.TaxCqtcConfig;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.service.taxrpt.ICqTaxInfoService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/cqtc/service_10102")
@Slf4j
public class CqtcTaxController extends BaseController {

    @Autowired
    private IUserService iuserService;
    @Autowired
    protected IBDCorpTaxService sys_corp_tax_serv;
    @Autowired
    protected ICqTaxInfoService taxinfoService;
    @Autowired
    private ICorpService corpserv;

    @Autowired
    private TaxCqtcConfig cqtcConfig;

    @GetMapping("/saveReportInitForCorp")
    public ReturnData<Json> saveReportInitForCorp(String pk_corp) {
        Json json = new Json();
        String msg = "获取期初";
        try {
            String pk_corps = pk_corp;
            String userid = SystemUtil.getLoginUserId();
            if (StringUtil.isEmpty(pk_corps))
                throw new BusinessException("公司不能为空！");
            String[] corp_string = pk_corps.split(",");
            List<CorpVO> corp_list = new ArrayList<CorpVO>();
            Set<String> powercorpSet = iuserService.querypowercorpSet(userid);
            for (int j = 0; j < corp_string.length; j++) {
                if(powercorpSet.contains(corp_string[j])){
                    CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(corp_string[j]);
                    if(corptaxvo.getTax_area()!=null && corptaxvo.getTax_area()==23){//重庆的
                        CorpVO cpvo = corpserv.queryByPk(corp_string[j]);
                        //	CorpVO cpvo = iCorp.findCorpVOByPK(corp_string[j]);
                        corp_list.add(cpvo);
                    }
                }
            }
            if (corp_list == null || corp_list.size() == 0)
                throw new BusinessException("无权限，请联系管理员！");
            String message = taxinfoService.saveTaxInfo(corp_list);
            json.setData(message);
            json.setStatus(200);
            json.setSuccess(true);
            json.setMsg(message);

            if(!StringUtil.isEmpty(message) && message.contains("失败")){
                msg += "失败";
            }else{
                msg += "成功";
            }

        } catch (Exception e) {
            json.setMsg("更新期初数据失败！:" + e.getMessage());
            json.setStatus(-200);
            json.setSuccess(false);

            msg += "失败";
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_TAX, msg, ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    /**
     * 用于一键报税客户端自动识别验证码
     * @param request
     * @return
     */
    @PostMapping("/recogImage")
    public ReturnData<Json> recogImage(HttpServletRequest request) {
        Json json = new Json();
        OutputStream os = null;
        InputStream is = null;
        BufferedReader br = null;
        Socket socket = null;
        PrintWriter pw = null;
        try {
            //imgtype:
            // "01" : 重庆国税旧版，黑色斜体
            // "02":  #重庆地税，横线干扰，右偏
            // "03":  #重庆国税新版 ， 加减乘除计算
            // "04":  #江苏地税
            // "05":  #山东国税
            // "06":  #山东地税

//            String imgtype = ((MultiPartRequestWrapper) getRequest()).getParameter("imgtype");
//
//            File file = null;
//            File[] files = ((MultiPartRequestWrapper) getRequest()).getFiles("image");
//            if (files != null) {
//                file = files[0];
//            }

            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile imgfile = multipartRequest.getFile("image");
            if (imgfile == null) {
                throw new BusinessException("请传入验证码图片");
            }
            String imgtype = multipartRequest.getParameter("imgtype");
            if (StringUtil.isEmpty(imgtype))
                throw new BusinessException("验证码类型为空");

            String socketip = cqtcConfig.socketip;
            String socketport = cqtcConfig.socketport;

            socket = new Socket(socketip, Integer.parseInt(socketport));

            os = socket.getOutputStream();// 字节输出流

            pw = new PrintWriter(os);// 将输出流包装为打印流
            pw.write(fileToBase64(imgfile) + (StringUtil.isEmptyWithTrim(imgtype) ? "01" : imgtype) +  "send ok");

            pw.flush();

            // 3.获取输入流，并读取服务器端的响应信息
            is = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            String info = "";
            String str;
            long lnow = System.currentTimeMillis();
            while((str = br.readLine()) != null
                    || System.currentTimeMillis() - lnow < 3000 && info.trim().length() == 0 && !imgtype.equals("18")
                    || System.currentTimeMillis() - lnow < 6000 && info.trim().length() < 4 && imgtype.equals("18")){
                if (str != null)
                {
                    info += str;
                }
                str = null;
            }

            json.setSuccess(true);
            json.setData(info);
            json.setMsg("识别成功");

            socket.shutdownOutput();// 关闭输出流
        } catch (Exception e) {
            printErrorLog(json, e, "验证码识别失败");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }

            try {
                if(pw!=null){
                    pw.close();
                }
            } catch (Exception e) {
            }
        }
        return ReturnData.ok().data(json);
    }

    private String fileToBase64(InputStreamSource file) {
        String base64 = null;
        InputStream in = null;
        ByteArrayOutputStream imageStream = null;
        try {
            // in = new FileInputStream(file);
            in = file.getInputStream();
//			byte[] bytes = new byte[in.available()];
//			in.read(bytes);
            //byte[]不一定是jpg格式，转变成jpg
            // 创建全屏截图。
            BufferedImage originalImage = ImageIO.read(in);

            imageStream = new ByteArrayOutputStream();
            //这里可以转变图片的编码格式
            ImageIO.write(originalImage, "jpg", imageStream);
            imageStream.flush();
            byte[] bytes = imageStream.toByteArray();

            base64 = new BASE64Encoder().encode(bytes);
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
            try {
                if (imageStream != null)
                {
                    imageStream.close();
                }
            } catch (IOException e) {
            }
        }
        return base64;
    }
}
