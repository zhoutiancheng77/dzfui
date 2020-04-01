package com.dzf.zxkj.platform.util.zncs;

import com.alibaba.fastjson.JSON;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.Base64CodeUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.config.ZncsUrlConfig;
import com.dzf.zxkj.platform.model.piaotong.PiaoTongResVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/**
 * 与票通接口相关的可统一用这个工具类
 */
@Slf4j
public class PiaoTongUtil {

    private static String[] chars = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    public static String xxptbm = null;
    private static String pturl = null;
    private static String platformCode = null;
    private static String signType = null;
    private static String format = null;
    private static String xxversion = null;
    private static String xxpwd = null;
    private static String privateKey = null;
    private static String publicKey = null;

    static{
        ZncsUrlConfig zncsUrlConfig = (ZncsUrlConfig) SpringUtils.getBean(ZncsUrlConfig.class);
        pturl = zncsUrlConfig.ptb_url;
        xxptbm = zncsUrlConfig.ptb_xxptbm;
        platformCode = zncsUrlConfig.ptb_platformCode;
        signType = zncsUrlConfig.ptb_signType;
        format = zncsUrlConfig.ptb_format;
        xxversion = zncsUrlConfig.ptb_xxversion;
        xxpwd = zncsUrlConfig.ptb_xxpwd;
        privateKey = zncsUrlConfig.ptb_privateKey;
        publicKey = zncsUrlConfig.ptb_publicKey;
    }

    public static PiaoTongResVO request(String content, String suffix){

        String result = null;
        try {
            Map<String, String> map = getBusiParams(content);
            List<NameValuePair> params = getParam(map);
            String url = pturl + suffix;
            result = RemoteClient.sendPostData(url, params);

            log.info("----------------票通调用-----------BEGIN");
            log.info(result);
            log.info("----------------票通调用-----------END");
        } catch (Exception e) {
            log.error("错误", e);
        }

        PiaoTongResVO vo = parseResult(result);
        return vo;
    }

    private static PiaoTongResVO parseResult(String result){
        if (StringUtil.isEmpty(result))
            throw new BusinessException("请求失败，请联系管理员");

        PiaoTongResVO resvo = JSON.parseObject(result, PiaoTongResVO.class);

        String content = resvo.getContent();
        if(StringUtil.isEmpty(content)){
            return null;
        }

        content = decrypt(content);
        resvo.setContent(content);
        return resvo;
    }

    public static String decrypt(String content){
        String res = null;
        byte[] bytes;
        try {
            bytes = CommonXml.decrypt3DES(xxpwd, Base64CodeUtils.decode(content));
            res = new String(bytes, "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return res;
    }

    private static List<NameValuePair> getParam(Map<String, String> map) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), "\"" + entry.getValue() + "\""));
        }

        return params;
    }

    private static Map<String, String> getBusiParams(String content) throws Exception {

        byte[] bytes = CommonXml.encrypt3DES(xxpwd, content.getBytes("UTF-8"));
        content = Base64CodeUtils.encode(bytes);
        content = content.replace("\r\n", "").replace("\n", "");
        Map<String, String> map = new HashMap<String, String>();
        map.put("platformCode", platformCode);
        map.put("signType", signType);
        map.put("format", format);
        map.put("version", xxversion);
        map.put("content", content);
        map.put("timestamp", new DZFDateTime().toString());
        map.put("serialNo", getSerialNo(xxptbm, null));//getSerialNo("DEMO"));
        String sign = sign(getSignatureContent(map), privateKey);
        sign = sign.replace("\r\n", "").replace("\n", "");
        map.put("sign", sign);
        return map;
    }

    private static String sign(String content, String privatekey){
        try
        {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64CodeUtils.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            Signature signature = Signature.getInstance("SHA1WithRSA");

            signature.initSign(priKey);
            signature.update(content.getBytes("UTF-8"));

            byte[] signed = signature.sign();

            return Base64CodeUtils.encode(signed);
        }
        catch (Exception e) {
            log.error("错误", e);
        }

        return null;
    }

    public static String getSignatureContent(Map<String, String> params){
        if (params == null) {
            return null;
        }
        StringBuffer content = new StringBuffer();
        List keys = new ArrayList(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = (String)keys.get(i);
            if (params.get(key) != null)
            {
                String value = String.valueOf(params.get(key));
                content.append(new StringBuilder().append(i == 0 ? "" : "&").append(key).append("=").append(value).toString());
            }
        }
        return content.toString();
    }

    public static String getSerialNo(String prefix, Integer len){
        return prefix
                + new DZFDateTime().toString().replace(" ", "").replace(":", "").replace("-", "")
                + generateShortUuid(len);
    }

    private static String generateShortUuid(Integer len){
        len = len == null ? 8 : len;
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < len; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[(x % 62)]);
        }
        return shortBuffer.toString();
    }

}
