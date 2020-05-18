package com.dzf.zxkj.platform.dubbo;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.zncs.DutyPayVO;
import com.dzf.zxkj.platform.service.zncs.IInterfaceBill;
import com.dzf.zxkj.platform.service.zncs.IWsDutyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
@org.apache.dubbo.config.annotation.Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class WsDutyServiceImpl implements IWsDutyService {
    @Autowired
    IInterfaceBill iInterfaceBill;
    @Autowired
    SingleObjectBO singleObjectBO;
    @Override
    public String queryDutyTolalInfo(String period,String corpNames[],String []pkcorps,int page,int rows) {
        Json json = new Json();
        try{
            if(StringUtil.isEmpty(period)){
                throw new BusinessException("期间不能为空!");
            }
            if(pkcorps==null || pkcorps.length==0){
                if(corpNames==null || corpNames.length==0 ){
                    throw new BusinessException("查寻公司不能为空!");
                }
                pkcorps = iInterfaceBill.queryCorpByName(corpNames);
            }

            DutyPayVO[] datas = iInterfaceBill.queryDutyTolalInfo(pkcorps,period,page,rows);
            //DutyPayVO[] data2 = getPageDutydata(datas,page,rows);
            json.setHead(pkcorps);
            json.setSuccess(true);
            json.setMsg("查询成功");
            json.setRows(rows);
            json.setTotal((long) (datas == null ? 0 : datas.length));
            json.setData(datas);
        } catch(Exception e) {
            json.setStatus(-100);
        }
        return JsonUtils.serialize(json);


    }


}
