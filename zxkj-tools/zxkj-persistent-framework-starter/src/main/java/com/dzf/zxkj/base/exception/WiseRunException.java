package com.dzf.zxkj.base.exception;

/**
 * 运行时除DAO和Business异常外其它异常
 */
public class WiseRunException extends DZFWarpException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public WiseRunException(Throwable cause) {
        super(cause);
    }

}
