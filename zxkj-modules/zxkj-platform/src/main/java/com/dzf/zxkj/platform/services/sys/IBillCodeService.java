package com.dzf.zxkj.platform.services.sys;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.MaxCodeVO;

public interface IBillCodeService {


    /**
     * VO对象作为参数，方便后续扩展
     * @author gejw
     * @time 下午1:44:38
     * @param mcvo
     * @return
     * @throws DZFWarpException
     */
    public String getBillCode(MaxCodeVO mcvo) throws DZFWarpException;
    

    /**
     * 编码自动生成（会计公司编码+日期+几位流水）
     * @param mcvo
     * @return
     * @throws DZFWarpException
     */
    public String getDefaultCode(final MaxCodeVO mcvo) throws DZFWarpException;
    
    /**
     * 解锁编码
     * @author gejw
     * @time 下午8:08:59
     * @param mcvo
     * @throws DZFWarpException
     */
    public void unLockCode(MaxCodeVO mcvo) throws DZFWarpException;
}
