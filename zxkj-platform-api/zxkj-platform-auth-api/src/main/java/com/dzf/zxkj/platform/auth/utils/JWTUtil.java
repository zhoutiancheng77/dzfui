package com.dzf.zxkj.platform.auth.utils;

import com.dzf.zxkj.platform.auth.constant.AuthConstant;
import com.dzf.zxkj.platform.auth.model.jwt.IJWTInfo;
import com.dzf.zxkj.platform.auth.model.jwt.JWTInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * @Auther: dandelion
 * @Date: 2019-08-13
 * @Description:
 */
public class JWTUtil implements Serializable {

    private JWTUtil() {

    }

    private static RsaKeyUtil rsaKeyUtil = RsaKeyUtil.getInstance();

    public static String generateToken(IJWTInfo jwtInfo, byte priKey[], int expire) throws Exception {
        String compactJws = Jwts.builder()
                .setSubject(jwtInfo.getSubject())
                .claim(AuthConstant.JWT_KEY_BODY, jwtInfo.getBody())
                .setExpiration(DateTime.now().plusSeconds(expire).toDate())
                .signWith(SignatureAlgorithm.RS256, rsaKeyUtil.getPrivateKey(priKey))
                .compact();
        return compactJws;
    }

    public static Jws<Claims> parserToken(String token, byte[] pubKey) throws Exception {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(rsaKeyUtil.getPublicKey(pubKey)).parseClaimsJws(token);
        return claimsJws;
    }

    public static IJWTInfo getInfoFromToken(String token, byte[] pubKey) throws Exception {
        Jws<Claims> claimsJws = parserToken(token, pubKey);
        Claims body = claimsJws.getBody();
        return new JWTInfo(body.getSubject(), getObjectValue(body.get(AuthConstant.JWT_KEY_BODY)));
    }

    public static String getObjectValue(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}
