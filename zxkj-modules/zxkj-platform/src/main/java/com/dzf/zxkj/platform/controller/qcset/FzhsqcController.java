package com.dzf.zxkj.platform.controller.qcset;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.qcset.IFzhsqcService;
import com.dzf.zxkj.platform.service.qcset.IQcye;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/qcset/gl_fzhsqcact")
@Slf4j
public class FzhsqcController extends BaseController {

    @Autowired
    private IFzhsqcService gl_fzhsqcserv = null;

    private IQcye gl_qcyeserv = null;

    public IQcye getGl_qcyeserv() {
        return gl_qcyeserv;
    }

    @Autowired
    public void setGl_qcyeserv(IQcye gl_qcyeserv) {
        this.gl_qcyeserv = gl_qcyeserv;
    }

    @Autowired
    private SingleObjectBO singleObjectBO = null;


    private ICpaccountService gl_cpacckmserv = null;

    public ICpaccountService getGl_cpacckmserv() {
        return gl_cpacckmserv;
    }

    public void setGl_cpacckmserv(ICpaccountService gl_cpacckmserv) {
        this.gl_cpacckmserv = gl_cpacckmserv;
    }

    @GetMapping("queryFzQc")
    public ReturnData queryFzQc (String kmid, String currency) {
        Json json = new Json();
        try {
            FzhsqcVO[] fzqc = gl_fzhsqcserv.queryFzQc(SystemUtil.getLoginCorpId(), kmid);
            json.setRows(fzqc);
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败！");
        }
//        writeJson(json);
        return ReturnData.ok().data(json);
    }

    @GetMapping("saveCombo")
    public ReturnData saveCombo(String kemuid, String currency, HttpServletRequest request){
        Json json = new Json();
        CorpVO corpVo = SystemUtil.getLoginCorpVo();
        UserVO uservo = SystemUtil.getLoginUserVo();
        Map<String,String> fzhsx = new HashMap<String,String>();
        YntCpaccountVO kemuvo = gl_cpacckmserv.queryById(kemuid);
        int count = 0;
        int len=kemuvo.getIsfzhs().length();
        char[] cs=kemuvo.getIsfzhs().toCharArray();
        for(int i = 0; i < len; i++){
            if('1'==cs[i])
                count++;
        }
        cs=null;
        String qcfzhsx = null;
        for(int i = 1; i <= 10; i++){
            qcfzhsx = request.getParameter("fzhsx"+i);
            if(!StringUtil.isEmpty(qcfzhsx)){
                fzhsx.put("fzhsx"+i, qcfzhsx);
                count--;
            }
        }
        try{
            if(count != 0){
                throw new BusinessException("请确认为每个辅助核算项添加档案。");
            }
            FzhsqcVO vo = new FzhsqcVO();
            vo.setPk_corp(corpVo.getPk_corp());
            vo.setPk_accsubj(kemuid);
            vo.setPk_currency(currency);
            vo.setFzhsx1(fzhsx.get("fzhsx1"));
            vo.setFzhsx2(fzhsx.get("fzhsx2"));
            vo.setFzhsx3(fzhsx.get("fzhsx3"));
            vo.setFzhsx4(fzhsx.get("fzhsx4"));
            vo.setFzhsx5(fzhsx.get("fzhsx5"));
            vo.setFzhsx6(fzhsx.get("fzhsx6"));
            vo.setFzhsx7(fzhsx.get("fzhsx7"));
            vo.setFzhsx8(fzhsx.get("fzhsx8"));
            vo.setFzhsx9(fzhsx.get("fzhsx9"));
            vo.setFzhsx10(fzhsx.get("fzhsx10"));

            vo = gl_fzhsqcserv.saveCombo(vo, uservo, corpVo);
            json.setRows(vo);
            json.setSuccess(true);
            json.setMsg("添加成功。");

        }catch(Exception e){
            printErrorLog(json, e, "添加失败！");
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("saveFzQc")
    public ReturnData saveFzQc ( HttpServletRequest request) {
        Json json = new Json();
        CorpVO corp = SystemUtil.getLoginCorpVo();
        UserVO user = SystemUtil.getLoginUserVo();
        try {
            String pk_km = request.getParameter("pk_km");
            String fzdata = request.getParameter("fzdata" );
            String kmdata = request.getParameter("kmdata" );
            FzhsqcVO[] fzvos = JsonUtils.deserialize(fzdata, FzhsqcVO[].class);
            QcYeVO qcData = JsonUtils.deserialize(kmdata, QcYeVO.class);
            String currency = request.getParameter("currency");
            if (qcData != null) {
                gl_fzhsqcserv.saveFzQc(pk_km, fzvos, currency, qcData, user, corp);
            }
            json.setMsg("保存成功");
            json.setSuccess(true);
            writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "辅助期初保存", ISysConstants.SYS_2);
        } catch (Exception e) {
            printErrorLog(json, e, "保存失败！");
        }
        return ReturnData.ok().data(json);
    }

}
