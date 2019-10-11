package com.dzf.zxkj.report.service.cwbb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.query.cwbb.LrbQueryVO;
import com.dzf.zxkj.report.vo.cwbb.LrbVO;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ILrbService {
    /**
     * 利润表取数
     *
     * @param queryVO
     * @return
     * @throws Exception
     */
    LrbVO[] getLRBVOs(LrbQueryVO queryVO, CorpVO corpVO) throws Exception;

    /**
     * 利润表取数(包含具体某几个项目)
     *
     * @param queryVO
     * @param xmid
     * @return
     * @throws Exception
     */
    LrbVO[] getLRBVOsConXm(LrbQueryVO queryVO, List<String> xmid, CorpVO corpVO) throws Exception;

    /**
     * 从发生表获取数据
     *
     * @param queryVO
     * @param mp
     * @param pk_corp
     * @param fvos
     * @return
     * @throws Exception
     */
    LrbVO[] getLrbVosFromFs(LrbQueryVO queryVO, Map<String, YntCpaccountVO> mp, CorpVO corpVO, FseJyeVO[] fvos) throws Exception;

    /**
     * 利润表取数(按照期间取数 如 2017-12-01 到2018-12-31)
     *
     * @param paramVO
     * @return
     * @throws Exception
     */
    LrbVO[] getLRBVOsByPeriod(LrbQueryVO paramVO, CorpVO corpVO) throws Exception;

    /**
     * 利润表取数(多个月份)
     *
     * @param year
     * @param pk_corp
     * @param objs
     * @return
     * @throws Exception
     */
    Map<String, DZFDouble> getYearLRBVOs(String year, Object[] objs, CorpVO corpVO) throws Exception;

    /**
     * 获取每个月的数据
     *
     * @param year
     * @param pk_corp
     * @param xmmcid
     * @param objs
     * @param ishasjz
     * @return
     * @throws Exception
     */
    Map<String, List<LrbVO>> getYearLrbMap(String year, CorpVO corpVO, String xmmcid, Object[] objs, DZFBoolean ishasjz) throws Exception;

    /**
     * 获取区间段每个区间的数据
     *
     * @param begdate
     * @param enddate
     * @param pk_corp
     * @param xmmcid
     * @param objs
     * @param ishasjz
     * @return
     * @throws Exception
     */
    List<LrbVO[]> getBetweenLrbMap(DZFDate begdate, DZFDate enddate, CorpVO corpVO, String xmmcid, Object[] objs, DZFBoolean ishasjz) throws Exception;

    /**
     * 财务报税文件VO
     *
     * @param qj
     * @param corpIds
     * @param qjlx
     * @return
     * @throws Exception
     */
    LrbVO[] getLrbDataForCwBs(String qj, CorpVO corpVO, String qjlx) throws Exception;

    LrbVO[] getLrbVos(LrbQueryVO queryVO, CorpVO corpVO, Map<String, YntCpaccountVO> mp, Map<String, FseJyeVO> map, String xmmcid) throws Exception;
}
