package com.dzf.zxkj.platform.service.batchprint.impl;

import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.ReportUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KmmxUtil {

    //获取汇率精度
    public static Integer getHlJd(String pk_corp) {

        IParameterSetService sys_parameteract = (IParameterSetService) SpringUtils.getBean("sys_parameteract");

        Integer hljd = 6;
        YntParameterSet setvo = sys_parameteract.queryParamterbyCode(pk_corp, "dzf011");

        if (setvo != null && !StringUtil.isEmpty(setvo.getParametervalue())) {
            String[] paramvalu = setvo.getParametervalue().split(";");
            hljd = Integer.parseInt(paramvalu[setvo.getPardetailvalue()]);
        }
        return hljd;
    }
    /**
     * 过滤期初数据
     * @param kmmxvos
     * @return
     */
    public static KmMxZVO[] filterQcVos(KmMxZVO[] kmmxvos, String pk_corp) {
        Integer hljd =  getHlJd(pk_corp);
        KmMxZVO[] vos;
        //存在一次的不显示期初余额
        HashMap<String, DZFBoolean> mapshow = new HashMap<String, DZFBoolean>();
        for(KmMxZVO mxzvo:kmmxvos){
            mapshow.put(mxzvo.getPk_accsubj(), DZFBoolean.FALSE);
        }
        List<KmMxZVO> listmx = new ArrayList<KmMxZVO>();
        for(KmMxZVO mxzvo:kmmxvos){
            if(mapshow.get(mxzvo.getPk_accsubj()).booleanValue()  && "期初余额".equals(mxzvo.getZy()) && ReportUtil.bSysZy(mxzvo)){
                continue;
            }
            listmx.add(mxzvo);
            if(!mapshow.get(mxzvo.getPk_accsubj()).booleanValue()  && "期初余额".equals(mxzvo.getZy())  && ReportUtil.bSysZy(mxzvo) ){
                mapshow.put(mxzvo.getPk_accsubj(),  DZFBoolean.TRUE) ;
            }

            if(!StringUtil.isEmpty(mxzvo.getBz()) && !StringUtil.isEmpty(mxzvo.getHl())){
                mxzvo.setBz(mxzvo.getBz()+"/"+new DZFDouble(mxzvo.getHl()).setScale(hljd, DZFDouble.ROUND_HALF_UP).toString());
            }
        }
        vos = listmx.toArray(new KmMxZVO[0]);
        return vos;
    }

}
