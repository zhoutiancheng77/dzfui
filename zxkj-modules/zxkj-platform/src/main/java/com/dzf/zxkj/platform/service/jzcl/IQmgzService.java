package com.dzf.zxkj.platform.service.jzcl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.base.query.QueryParamVO;

import java.util.List;

public interface IQmgzService {

    QmclVO[] query(QueryParamVO vo, String userid, DZFDate d) throws DZFWarpException;

    void processGzOperate(String pk_corp, String qj, DZFBoolean b, String userid) throws DZFWarpException;

    List<Object> gzCheck(String pk_corp, String qj) throws DZFWarpException;

    boolean isGz(String pk_corp, String startqj) throws DZFWarpException;

    List<QmclVO> yearhasGz(String pk_corp, String qj) throws DZFWarpException;

    /**
     * 检查期间之后的月份是否已关账
     *
     * @param pk_corp
     * @param qj
     * @return
     * @throws DZFWarpException
     */
    boolean checkLaterMonthGz(String pk_corp, String qj) throws DZFWarpException;

    /**
     * 同时反关账所选期间之后的月份
     *
     * @param pk_corp
     * @param qj
     * @return
     * @throws DZFWarpException
     */
    void cancelGzPeriodAndLater(String pk_corp, String qj) throws DZFWarpException;
}
