package com.dzf.zxkj.platform.util.taxrpt.shandong.deal;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.platform.config.TaxSdtcConfig;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;
import com.dzf.zxkj.platform.service.taxrpt.shandong.impl.WebServiceProxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;

/**
 * 山东中税密钥加密
 */
@Slf4j
public class CreateSignUtils {

//	private static Logger log = Logger.getLogger(CreateSignUtils.class);

	// 大账房的私钥串
	// 测试环境
	private static String TESTPRIVATEKEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJQ8VV+2TMr/8SW+Ln+DSX6bxGmARWxEM3lt1pH9o7hHDyE18nsIkpZbVwGpR5iuSAU2ln6/LAgr7BRWicN8+RYtBXmAYYfwa20Sj+mdgfFOqXE1RepCHQPVBN8Pj08Yq6megzD4+QJkVcAkuqf9pA/xbaNmn31M9gkGZinjmk6bAgMBAAECgYEAjqWImnQ5bdfh3SooYLicDuQinEu9grQWvLGAHDjyRbDL1G+vicn0FhCLp6OYrEp6L9oZ9JpO6wYpRmEIbUBkCQJZeW46cGPXz+8s+82TnCZ1IMmiQZMfXaoVaz4ii2Rah9Xc1uKHbW4BiFCj+FcWgrYv9lbf2Lr95T1ZOwW1nuECQQDgYR2SoOZri2K92wmFkNruZA0fxcIf8xeQoX/rM7RXr28ox8WiwTK5gHcks4dmJ+DSPhEMTfv14eMZSWLeYYaTAkEAqSAzA0Zrw/R1kFlqroq7ETdbb0losbHUyFmCq5ioSrUcFknc8wiFbyqiKBM8cSNYKGwC+iFAJ0pxj6BPv31U2QJAe2MMRyCx9TveHbdAwjFJI0TjrrAqMzWTpNYaqPVy27E+eHd66ChDw5ywZ/9NmtCdIiA9cb3Eq47/Ol2Pv+hzfwJBAIrS/myqylyv9iyF6Tbac5E/MlOYG1L42OOX7dWy2jlwjlyRRsdLcFP+19ozaAKqc3vCpXLyBjn6NlshyTQyyYkCQQCfgunv/xORCGdnN5owZTyEt5BQN41K996OjqrdBFex3osEyBm2AKsBn8EOkVVuuXO9T3hRy2HgQwEPIqJEWMnk";
	// 正式环境
	private static String PROPRIVATEKEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJoWINdezMTGNVdp3wSJjRoltVPzE0IPkdzssUnfPTYR/6ECPCKvhGNI0Pk1QVL2d1TfNpB8B2py+c6IBiSGUnq+HnueH7WOJd7qL+17bY3TCvJLOpBVm8F6ZVfdChBkpUVUjK9MOUBA2jhuttfGRfzlXQiko/s70oqW27XEKX+jAgMBAAECgYBByrcFHs5SKsu14le+aTkddAJjsQOFDn7alRnyD+bkAnVi+0BQEx91AS9xaSLWWLSFQbXbpYnJEwTFAeGVkOEAKK4HACVfxJx69reBlyOpQu0dT/6/6UWqnINltyv4nls1oUHID0jQiYhtu+KeVL8S9T+MRODkQBdTsbXQ0Q0e2QJBAPOIhrcrZ0lJM8rpIIbZ8D+rtwrIyOHCQTY/UUrHrb+KYqLBxYXZogVQjgF6cqJRpjFgoxHF2RjWDoHkhvn16q8CQQCh+Wl14ruATASExh3B5GIYso/o/oTlzARw2PwcBzJwYAnaxhR19cp4mo0fGJrBheIq63esrIU0eQYj5na78udNAkEAh8Bu9+Pl62A20sEpNIJ//b4Ghqht8gqKt2aMNhcgr0jAuuEw7e/m9Pd2cTSEeh7xeUzZGasj3UhVRerRoryGWQJAPpSny3VLrnkwccA99RDxWct70LCt1j9qI9OiLI4XTdW9WPqZIy5RCSHljnMqL8UzqhOKDHwnhyuPVMWRcHKcLQJALwAquCl5AjPawD9OcO79SC93zvzyWDdUZ3bLbht/pxk6jP6S9NoXGO8NjBMjvkCxoRk2aE0HB6R1BEkA1Mg9Hw==";
	private static String KEYCODE = "RSA";

	public static String getPrivateKey() {
		String key = PROPRIVATEKEY;
		TaxSdtcConfig taxSdtcConfig = SpringUtils.getBean(TaxSdtcConfig.class);
		if ("true".equals(taxSdtcConfig.istest)) {
			key = TESTPRIVATEKEY;
		}
		return key;
	}

	/**
	 * 
	 * @param plainText
	 *            加密的内容
	 * @param isclient
	 *            是否客户端生成
	 * @return
	 */
	public static String getSign(String plainText, boolean isclient) {
		// 待加密内容，格式为 nsrsbh+supplier+ywlx+skssq
		String sign = "";// 加密后的内容

		try {
			if (!isclient) {

				// 先MD5加密
				plainText = DigestUtils.md5Hex(plainText);
				sign = WebServiceProxy.setSign(getPrivateKey(), plainText);

				HashMap<String, String> map = ParseJsonData.getJsonData(sign);

				if (map == null || map.size() == 0 || map.get(TaxConst.RETURN_ITEMKEY_NO) == null) {
					throw new BusinessException("中税加密数据失败");
				}

				if (!"0".equals(map.get(TaxConst.RETURN_ITEMKEY_NO))) {
					throw new BusinessException(map.get(TaxConst.RETURN_ITEMKEY_OBJ));
				}
				sign = map.get(TaxConst.RETURN_ITEMKEY_OBJ);
			} else {
				PrivateKey privateKey = null;
				Cipher cipher = null;
				// 读取私钥
				byte[] buffer = Base64.decode(getPrivateKey());
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
				KeyFactory keyFactory = KeyFactory.getInstance(KEYCODE);
				privateKey = keyFactory.generatePrivate(keySpec);

				// 开始加密
				cipher = Cipher.getInstance(KEYCODE);
				cipher.init(Cipher.ENCRYPT_MODE, privateKey);
				// 先MD5加密
				plainText = DigestUtils.md5Hex(plainText);
				byte[] output = cipher.doFinal(plainText.getBytes());

				// 加密成功
				sign = Base64.encode(output);
			}
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(),e);
			throw new BusinessException("加密失败");
		} catch (InvalidKeySpecException e) {
			log.error(e.getMessage(),e);
			throw new BusinessException("加密失败");
		} catch (NoSuchPaddingException e) {
			log.error(e.getMessage(),e);
			throw new BusinessException("加密失败");
		} catch (InvalidKeyException e) {
			log.error(e.getMessage(),e);
			throw new BusinessException("加密失败");
		} catch (IllegalBlockSizeException e) {
			log.error(e.getMessage(),e);
			throw new BusinessException("加密失败");
		} catch (BadPaddingException e) {
			log.error(e.getMessage(),e);
			throw new BusinessException("加密失败");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BusinessException("加密失败");
		}
		return sign;
	}

	public static void main(String[] args) {
		String plainText = "";// 待加密内容
		String nsrsbh = "371325751788249";
		String supplier = "dzf2017";
		// 待加密内容，格式为 nsrsbh+supplier+ywlx+skssq
		plainText = nsrsbh + supplier + "C00.TY.SFYZ.nsrdlyz" + "201612";
		System.out.println(getSign(plainText, false));

	}
}
