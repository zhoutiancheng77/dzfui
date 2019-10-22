package com.dzf.zxkj.common.enums;

/**
 * @Auther: dandelion
 * @Date: 2019-09-02
 * @Description:
 */
public enum HttpStatusEnum {
    //权限校验
    EX_TOKEN_ERROR_CODE(40101, "令牌无效"),
    EX_TOKEN_EXPIRED_CODE(40102, "令牌过期"),
    EX_USER_INVALID_CODE(40103, "用户名无效"),
    EX_USER_PASS_INVALID_CODE(40104, "用户名密码无效"),
    EX_USER_FORBIDDEN_CODE(40105, "权限不足"),
    MISS_REQUEST_PARAMETER_CODE(422, "缺少参数"),
    INTERNAL_SERVER_ERROR(500, "系统异常"),
    OK(200,"success");


    private final int value;

    private final String reasonPhrase;


    HttpStatusEnum(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }


    /**
     * Return the integer value of this status code.
     */
    public int value() {
        return this.value;
    }

    public String msg() {
        return this.reasonPhrase;
    }
}
