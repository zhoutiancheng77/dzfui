package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.QueryCondictionVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.NumMnyDetailVO;
import com.dzf.zxkj.platform.model.report.NumMnyGlVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwzb.INummnyReport;
import com.dzf.zxkj.report.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数量金额账----总账-------明细账接口
 */
@Service("gl_rep_nmdtserv")
@SuppressWarnings("all")
public class NummnydtReportImpl implements INummnyReport {

    private SingleObjectBO singleObjectBO = null;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @Autowired
    public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
        this.singleObjectBO = singleObjectBO;
    }

    @Override
    public List<NumMnyDetailVO> getNumMnyDetailVO(String startDate,
                                                  String enddate, String pk_inventory, QueryParamVO paranVo, String pk_corp, String user_id, String pk_bz, String xsfzhs, DZFDate begdate)
            throws DZFWarpException {
        List<NumMnyDetailVO> list = null;
        CorpVO corpvo = zxkjPlatformService.queryCorpByPk(pk_corp);
        Map<String, String> precisionMap = getPrecision(pk_corp);

        if (!IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
            QueryMxNoIC mx = new QueryMxNoIC(singleObjectBO, zxkjPlatformService, precisionMap);
            list = mx.querymx(startDate, enddate, pk_corp, user_id, paranVo, pk_bz, false, xsfzhs, begdate);
        } else {
//			QueryMx mx = new QueryMx(singleObjectBO,ic_rep_cbbserv);

            if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() != 1) {
                QueryMxKC1 mx1 = new QueryMxKC1(singleObjectBO, zxkjPlatformService, precisionMap);
                //暂时如此处理，只针对startDate < corpvo.getIcbegindate() < enddate  又注释掉了。。。
//				if(corpvo.getIcbegindate() != null 
//						&& new DZFDate(startDate).before(corpvo.getIcbegindate())
//						&& !corpvo.getIcbegindate().after(new DZFDate(enddate))){
//					startDate = corpvo.getIcbegindate().toString();
//				}
                list = mx1.querymx(startDate, enddate, pk_corp, user_id, paranVo, pk_bz, false, xsfzhs, begdate);
            } else {
                QueryMxKC mx = new QueryMxKC(singleObjectBO, zxkjPlatformService, precisionMap);
                //暂时如此处理，只针对startDate < corpvo.getIcbegindate() < enddate
//				if(corpvo.getIcbegindate() != null 
//						&& new DZFDate(startDate).before(corpvo.getIcbegindate())
//						&& !corpvo.getIcbegindate().after(new DZFDate(enddate))){
//					startDate = corpvo.getIcbegindate().toString();
//				}
//				list = mx.querymx(startDate, enddate, pk_corp, pk_inventory);
                list = mx.querymx(startDate, enddate, pk_corp, user_id, paranVo, pk_bz, false, xsfzhs, begdate);
            }
        }
        return list;
    }

    private Map<String, String> getPrecision(String pk_corp) {
        Map<String, String> precisionMap = new HashMap<String, String>();
        String precision = zxkjPlatformService.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);//数量
        if (StringUtil.isEmpty(precision)) {
            precision = "4";
        }
        precisionMap.put(IParameterConstants.DZF009, precision);

        precision = zxkjPlatformService.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);//单价
        if (StringUtil.isEmpty(precision)) {
            precision = "4";
        }
        precisionMap.put(IParameterConstants.DZF010, precision);

        return precisionMap;
    }

    @Override
    public List<NumMnyGlVO> getNumMnyGlVO(QueryCondictionVO paramVo) throws DZFWarpException {
        List<NumMnyGlVO> list = new ArrayList<NumMnyGlVO>();
        List<NumMnyGlVO> listtemp = null;
        Map<String, String> precisionMap = getPrecision(paramVo.getPk_corp());

        if (paramVo.getIsic() == null || !paramVo.getIsic().booleanValue()) {
            QueryHZdic hz = new QueryHZdic(singleObjectBO, zxkjPlatformService, precisionMap);
            listtemp = hz.buildData(paramVo);
        } else {
            CorpVO corpvo = zxkjPlatformService.queryCorpByPk(paramVo.getPk_corp());
            if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() != 1) {//针对库存老模式
                QueryHZKC1 hz = new QueryHZKC1(singleObjectBO, zxkjPlatformService, precisionMap);
                listtemp = hz.buildData(paramVo);
            } else {
                QueryHZKC hz = new QueryHZKC(singleObjectBO, zxkjPlatformService, precisionMap);
                listtemp = hz.buildData(paramVo);
            }

        }

        //有余额无发生不显示
        if (paramVo.getIshowfs() != null && !paramVo.getIshowfs().booleanValue()) {
            if (listtemp != null && listtemp.size() > 0) {
                for (NumMnyGlVO gvo : listtemp) {
                    if (VoUtils.getDZFDouble(gvo.getBqdfmny()).doubleValue() != 0
                            || VoUtils.getDZFDouble(gvo.getBqjfnum()).doubleValue() != 0
                            || VoUtils.getDZFDouble(gvo.getBqdfnum()).doubleValue() != 0
                            || VoUtils.getDZFDouble(gvo.getBqdfmny()).doubleValue() != 0) {
                        list.add(gvo);
                    }
                }
            }
        } else if (paramVo.getXswyewfs() != null && paramVo.getXswyewfs().booleanValue()) {
            if (listtemp != null && listtemp.size() > 0) {
                for (NumMnyGlVO gvo : listtemp) {
                    if (VoUtils.getDZFDouble(gvo.getBqdfmny()).doubleValue() != 0
                            || VoUtils.getDZFDouble(gvo.getBqjfnum()).doubleValue() != 0
                            || VoUtils.getDZFDouble(gvo.getBqdfnum()).doubleValue() != 0
                            || VoUtils.getDZFDouble(gvo.getBqdfmny()).doubleValue() != 0
                            || VoUtils.getDZFDouble(gvo.getQmmny()).doubleValue() != 0
                    ) {
                        list.add(gvo);
                    }
                }
            }
        } else {
            list = listtemp;
        }
        return list;
    }
}
