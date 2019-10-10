package com.dzf.zxkj.report.service.cwbb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.report.vo.cwbb.ZcFzBVO;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;

import java.util.List;
import java.util.Map;

public interface IZcFzBService {
    /**
     *  资产负债表取数
     * @param period
     * @param pk_corp
     * @param ishasjz
     * @param ishasye
     * @return
     * @throws Exception
     */
    ZcFzBVO[] getZCFZBVOs(String period , String pk_corp, String ishasjz, String ishasye) throws  Exception ;

    /**
     * 资产负债查询(不包含平衡原因)
     * @param period
     * @param pk_corp
     * @param ishasjz
     * @param hasyes
     * @return
     * @throws Exception
     */
    ZcFzBVO[] getZCFZBVOs(String period , String pk_corp,String ishasjz,String[] hasyes) throws  Exception ;

    /**
     *  资产负债查询,单独某几个项目(不包含平衡原因)
     * @param period
     * @param pk_corp
     * @param ishasjz
     * @param hasyes
     * @param xmids
     * @return
     * @throws Exception
     */
    ZcFzBVO[] getZCFZBVOsConXmids(String period , String pk_corp, String ishasjz, String[] hasyes, List<String> xmids) throws  Exception ;

    /**
     *  资产负债表查询(包含不平衡原因,原因有效率因素，不要轻易用这个接口，如果只是为了查询，使用上面的接口)
     * @param period
     * @param pk_corp
     * @param ishasjz
     * @param hasyes
     * @return
     * @throws Exception
     */
    Object[] getZCFZBVOsConMsg(String period ,String pk_corp,String ishasjz,String[] hasyes) throws  Exception ;

    /**
     *  资产负债表查询(根据"发生额余额表"取数) 单个月份的
     * @param pk_corp
     * @param hasyes
     * @param mapc
     * @param fvos
     * @return
     * @throws Exception
     */
    ZcFzBVO[] getZcfzVOs(String pk_corp, String[] hasyes, Map<String, YntCpaccountVO> mapc, FseJyeVO[] fvos) throws Exception ;

    /**
     *  资产负债表查询(根据"发生额余额表"取数) 多个月份的
     * @param begdate
     * @param enddate
     * @param pk_corp
     * @param ishasjz
     * @param hasyes
     * @param qryobjs
     * @return
     * @throws Exception
     */
    List<ZcFzBVO[]> getZcfzVOs(DZFDate begdate, DZFDate enddate , String pk_corp, String ishasjz, String[] hasyes, Object[] qryobjs) throws Exception ;
}
