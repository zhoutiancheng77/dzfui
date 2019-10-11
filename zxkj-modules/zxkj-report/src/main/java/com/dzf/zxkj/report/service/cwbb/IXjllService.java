package com.dzf.zxkj.report.service.cwbb;

import com.dzf.zxkj.platform.model.qcset.YntXjllqcyePageVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.query.cwbb.XjllQueryVO;
import com.dzf.zxkj.report.vo.cwbb.XjllMxvo;
import com.dzf.zxkj.report.vo.cwbb.XjllbVO;

import java.util.List;
import java.util.Map;

public interface IXjllService {
    /**
     *  现金流量表
     * @param vo
     * @return
     * @throws Exception
     */
    XjllbVO[] query(XjllQueryVO vo, CorpVO corpVO) throws Exception;

    /**
     *  截止到当前期间的每个月的现金流量信息
     * @param vo
     * @return
     * @throws Exception
     */
    Map<String,XjllbVO[]> queryEveryPeriod(XjllQueryVO vo,CorpVO corpVO) throws Exception;

    /**
     * 现金流量明细数据
     * @param period
     * @param pk_corp
     * @param hc
     * @return
     * @throws Exception
     */
    XjllMxvo[] getXJllMX(String period , String pk_corp, String hc) throws  Exception ;

    /**
     * 财务报税导出不同地区的格式
     * @param qj
     * @param corpIds
     * @param qjlx
     * @return
     * @throws Exception
     */
    XjllbVO[] getXjllDataForCwBs(String qj, String corpIds, String qjlx, CorpVO corpVO) throws Exception;

    /**
     *  获取现金流量期初数据
     * @param listvo
     * @param pk_corp
     * @return
     * @throws Exception
     */
    List<YntXjllqcyePageVO> bulidXjllQcData(List<YntXjllqcyePageVO> listvo, String pk_corp) throws Exception;
}
