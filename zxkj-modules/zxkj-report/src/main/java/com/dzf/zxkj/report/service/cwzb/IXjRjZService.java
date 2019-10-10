package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.report.vo.cwzb.KmMxZVO;

import java.util.List;

public interface IXjRjZService {
    KmMxZVO[] getSpecCashPay(String pk_corp , DZFDate beginDate , DZFDate endDate  , DZFBoolean includesg , String pk_currency, String kms, List<String> kmcodelist) throws Exception ;
    KmMxZVO[] getXJRJZVOs(String pk_corp , String kms ,  DZFDate begindate , DZFDate enddate  ,DZFBoolean xswyewfs , DZFBoolean xsyljfs , DZFBoolean ishasjz , DZFBoolean ishassh,String pk_currency) throws  Exception ;
    KmMxZVO[] getXJRJZVOs(String pk_corp , String kms ,  DZFDate begindate , DZFDate enddate  ,DZFBoolean xswyewfs , DZFBoolean xsyljfs , DZFBoolean ishasjz , DZFBoolean ishassh) throws  Exception ;
    KmMxZVO[] getXJRJZVOsConMo(String pk_corp , String kmsbegin,String kmsend ,
                               DZFDate begindate , DZFDate enddate  ,DZFBoolean xswyewfs , DZFBoolean xsyljfs ,
                               DZFBoolean ishasjz , DZFBoolean ishassh,String pk_currency,List<String>  kmcodelist,Object[] qryobjs) throws  Exception ;

}
