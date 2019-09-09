package com.dzf.zxkj.common.exception;

/**
 * @Auther: dandelion
 * @Date: 2019-09-02
 * @Description:
 */

/**
 * 正常的业务提示信息提示到前台
 */

public class BusinessException extends DZFWarpException {


//	private static Logger logger = Logger.getLogger(BusinessException.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String hint;

    private String errorCodeString = "";


    public BusinessException(String s) {
        super(s);
    }


    public BusinessException(String s, String errorCode) {
        super(s);
        setErrorCodeString(errorCode);
    }


    public java.lang.String getHint() {
        return hint;
    }


    public void setHint(java.lang.String newHint) {
        hint = newHint;
    }


    public String getErrorCodeString() {
        return errorCodeString;
    }

    public void setErrorCodeString(String errorCode) {
        this.errorCodeString = errorCode;
    }

}
