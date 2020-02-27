package com.dzf.zxkj.platform.controller.taxrpt;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.service.taxrpt.ICqTaxInfoService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/cqtc/service_10102")
@Slf4j
public class CqtcTaxController extends BaseController {

    @Autowired
    private IUserService iuserService;
    @Autowired
    protected IBDCorpTaxService sys_corp_tax_serv;
    @Autowired
    protected ICqTaxInfoService taxinfoService;
    @Autowired
    private ICorpService corpserv;

    @GetMapping("/saveReportInitForCorp")
    public ReturnData<Json> saveReportInitForCorp(String pk_corp) {
        Json json = new Json();
        String msg = "获取期初";
        try {
            String pk_corps = pk_corp;
            String userid = SystemUtil.getLoginUserId();
            if (StringUtil.isEmpty(pk_corps))
                throw new BusinessException("公司不能为空！");
            String[] corp_string = pk_corps.split(",");
            List<CorpVO> corp_list = new ArrayList<CorpVO>();
            Set<String> powercorpSet = iuserService.querypowercorpSet(userid);
            for (int j = 0; j < corp_string.length; j++) {
                if(powercorpSet.contains(corp_string[j])){
                    CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(corp_string[j]);
                    if(corptaxvo.getTax_area()!=null && corptaxvo.getTax_area()==23){//重庆的
                        CorpVO cpvo = corpserv.queryByPk(corp_string[j]);
                        //	CorpVO cpvo = iCorp.findCorpVOByPK(corp_string[j]);
                        corp_list.add(cpvo);
                    }
                }
            }
            if (corp_list == null || corp_list.size() == 0)
                throw new BusinessException("无权限，请联系管理员！");
            String message = taxinfoService.saveTaxInfo(corp_list);
            json.setData(message);
            json.setStatus(200);
            json.setSuccess(true);
            json.setMsg(message);

            if(!StringUtil.isEmpty(message) && message.contains("失败")){
                msg += "失败";
            }else{
                msg += "成功";
            }

        } catch (Exception e) {
            json.setMsg("更新期初数据失败！:" + e.getMessage());
            json.setStatus(-200);
            json.setSuccess(false);

            msg += "失败";
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_TAX, msg, ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }
}
