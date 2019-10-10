package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.report.query.cwzb.XsZQueryVO;
import com.dzf.zxkj.report.vo.cwzb.XsZVO;

public interface IXsZService {
    XsZVO[] getXSZVOs(String pk_corp , String kms , String kmsx , String zdr , String shr, XsZQueryVO queryvo) throws Exception ;
}
