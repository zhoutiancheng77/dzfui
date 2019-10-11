package com.dzf.zxkj.report.mapper;

import com.dzf.zxkj.report.vo.cwbb.XjllMxvo;
import com.dzf.zxkj.report.vo.cwbb.XjllbVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XjllMapper {
    List<XjllbVO> queryXjllbVOList(@Param("pk_corp") String pk_corp, @Param("begin_period") String beginPeriod, @Param("endPeriod") String endPeriod, @Param("year") String year);
    List<XjllbVO> queryFsXjll(@Param("pk_corp") String pk_corp, @Param("begin_period") String beginPeriod, @Param("endPeriod") String endPeriod);
    List<XjllMxvo> queryXjllMxList(@Param("pk_corp") String pk_corp, @Param("period_like") String period, @Param("pk_trade_accountschema") String pk_trade_accountschema, @Param("hc") String hc);
}
