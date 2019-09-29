package com.dzf.zxkj.platform.auth.constant;

/**
 * @Auther: dandelion
 * @Date: 2019-08-14
 * @Description:
 */
public interface AuthConstant {
    Integer EX_TOKEN_ERROR_CODE = 40101;
    Integer EX_USER_INVALID_CODE = 40102;
    Integer EX_USER_PASS_INVALID_CODE = 40001;
    Integer EX_OTHER_CODE = 500;

    String JWT_KEY_SUBJECT= "username";
    String JWT_KEY_BODY= "body";
}
