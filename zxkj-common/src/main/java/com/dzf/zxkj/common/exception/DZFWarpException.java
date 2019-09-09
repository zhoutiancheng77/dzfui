package com.dzf.zxkj.common.exception;

import org.springframework.dao.DataAccessException;

/**
 * @Auther: dandelion
 * @Date: 2019-09-02
 * @Description:
 */
public abstract class DZFWarpException extends DataAccessException {

    public DZFWarpException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DZFWarpException(String msg) {
        super(msg);
    }

    public DZFWarpException(Throwable cause) {
        super("dzf异常", cause);
    }
}
