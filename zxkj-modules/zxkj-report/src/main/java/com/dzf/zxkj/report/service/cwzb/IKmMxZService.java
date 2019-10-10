package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.report.query.cwzb.KmmxQueryVO;
import com.dzf.zxkj.report.vo.cwzb.KmMxZVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IKmMxZService {
    KmMxZVO[] getKMMXZVOs(KmmxQueryVO vo, Object[] qryobj) throws  Exception ;
    KmMxZVO[] getKMMXZConFzVOs(KmmxQueryVO vo,Object[] qryobjs) throws  Exception ;
    Object[] getKMMXZVOs1(KmmxQueryVO vo,boolean  b) throws  Exception ;
    String getKmTempTable(KmmxQueryVO vo);
    List<KmMxZVO> getKmFSByPeriod(String pk_corp, DZFBoolean ishasjz, DZFBoolean ishassh, DZFDate start, DZFDate end, String kmwhere) throws Exception ;
    Map<String, YntCpaccountVO> getKM(String pk_corp, String kmwhere) throws Exception;
    List<KmMxZVO> getResultVos(Map<String, KmMxZVO> qcmapvos, Map<String, List<KmMxZVO>> fsmapvos,
                               HashMap<String, DZFDouble[]> corpbegqcmap, List<String> periods, Map<String ,
            YntCpaccountVO> kmmap, String pk_corp, DZFBoolean ishowfs, String kmslast, DZFBoolean btotalyear) throws Exception;
    YntCpaccountVO[] getkm_first(String kms_first,String pk_corp);
}
