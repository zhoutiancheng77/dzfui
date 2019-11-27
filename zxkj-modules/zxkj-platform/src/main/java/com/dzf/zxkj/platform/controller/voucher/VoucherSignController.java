package com.dzf.zxkj.platform.controller.voucher;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.IVoucherConstants;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.service.pzgl.IPzqzService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/voucher-manage/vouchersign")
@Slf4j
public class VoucherSignController {

    @Autowired
    private IPzqzService gl_pzqzserv;

    @Autowired
    private IUserService userService;

    @Autowired
    private IVoucherService gl_tzpzserv;

    @GetMapping("/sign")
    public ReturnData<Json> sign(String[] ids, String mode, String opesign) {
        Json json = new Json();
        List<String> pklist = new ArrayList<String>();
        String[] idsArray = ids;
        pklist = Arrays.asList(idsArray);
        VoucherParamVO paramvo = new VoucherParamVO();
        String pk_corp = SystemUtil.getLoginCorpId();
        DZFDate signdate = DZFDate.getDate(SystemUtil.getLoginDate());
        String vsigntor = SystemUtil.getLoginUserId();
        Set<String> corpSet = userService.querypowercorpSet(vsigntor);
        try{
            int fail = 0;
            int success = 0;
            StringBuffer msg = new StringBuffer();
            StringBuffer datePz = new StringBuffer();
            StringBuffer auditPz = new StringBuffer();
            String errMsg = "";
//			TzpzHVO hvo =null;

            List<TzpzHVO> hvos =  gl_tzpzserv.queryVoucherByIds(pklist);
            if(hvos == null){
                throw new BusinessException("凭证信息为空!");
            }

            for(TzpzHVO hvo:hvos){
                paramvo.setPk_corp(pk_corp);
                paramvo.setPk_tzpz_h(hvo.getPrimaryKey());
//				hvo = gl_tzpzserv.queryVoucherById(paramvo);
//				if (hvo == null) {
//					fail++;
//					continue;
//				}
                if(!corpSet.contains(hvo.getPk_corp())){
                    if("".equals(errMsg)){
                        errMsg = "无权操作！";
                    }
                    fail++;
                    continue;
                }
                if (hvo.getVbillstatus() == IVoucherConstants.TEMPORARY) {//暂存态不能签字
                    fail++;
                    if("".equals(errMsg)){
                        errMsg = "暂存态不能进行审核！";
                    }
                    continue;
                }

                if (hvo.getVbillstatus() == IVoucherConstants.AUDITED) {//审核通过
                    auditPz.append(hvo.getPzh() + "，");
                    fail++;
                    if("".equals(errMsg)){
                        errMsg = "已审核，不能签字！";
                    }
                    continue;
                }

                if (hvo.getBsign()!=null && hvo.getBsign().booleanValue() ) {//不能重复签字
                    auditPz.append(hvo.getPzh() + "，");
                    fail++;
                    if("".equals(errMsg)){
                        errMsg = "不能重复签字！";
                    }
                    continue;
                }


                if(signdate.before(hvo.getDoperatedate())){
                    if (mode.equals("0")) {
                        signdate = hvo.getDoperatedate();
                    } else {
                        datePz.append(hvo.getPzh() + "，");
                        fail++;
                        if("".equals(errMsg)){
                            errMsg = "签字时间不能小于制单日期！";
                        }
                        continue;
                    }
                }

                gl_pzqzserv.saveSignPz(hvo, vsigntor, signdate);//签字
                success++;
            }
            if (fail > 0){
                json.setStatus(2);
                msg.append("成功：" + success + "，失败：" + fail + ("".equals(errMsg) ? "" : (",原因：" + errMsg)));
            } else {
                json.setStatus(0);
                msg.append("签字成功" + success + "条");
            }
            json.setSuccess(true);
            json.setMsg(msg.toString());
//            writeLogRecord(LogRecordEnum.OPE_KJ_OTHERVOUCHER.getValue(),
//                    "签字成功" + success + "条", ISysConstants.SYS_2);
        }catch(Exception e){
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "签字失败!");
        }

        return ReturnData.ok().data(json);
    }

    @GetMapping("/cancelSign")
    public ReturnData<Json> cancelSign(String[] ids){
        Json json = new Json();
        List<String> pklist = new ArrayList<String>();
        String[] idsArray = ids;
        pklist = Arrays.asList(idsArray);
        VoucherParamVO paramvo = new VoucherParamVO();
        String pk_corp = SystemUtil.getLoginCorpId();
        String vsigntor = SystemUtil.getLoginUserId();
        Set<String> corpSet = userService.querypowercorpSet(vsigntor);
        try{
            int fail = 0;
            int success = 0;
            StringBuffer msg = new StringBuffer();
            StringBuffer auditPz = new StringBuffer();
            String errMsg = "";
//			TzpzHVO hvo =null;

            List<TzpzHVO> hvos =  gl_tzpzserv.queryVoucherByIds(pklist);
            if(hvos == null){
                throw new BusinessException("凭证信息为空!");
            }

            for(TzpzHVO hvo:hvos){
                paramvo.setPk_corp(pk_corp);
                paramvo.setPk_tzpz_h(hvo.getPrimaryKey());
                if(!corpSet.contains(hvo.getPk_corp())){
                    if("".equals(errMsg)){
                        errMsg = "无权操作！";
                    }
                    fail++;
                    continue;
                }
                if (hvo.getVbillstatus() == IVoucherConstants.TEMPORARY) {//暂存态不能审核
                    fail++;
                    if("".equals(errMsg)){
                        errMsg = "暂存态不能进行取消签字！";
                    }
                    continue;
                }
                if (hvo.getBsign()==null || !hvo.getBsign().booleanValue()) {//审核通过
                    auditPz.append(hvo.getPzh() + "，");
                    fail++;
                    if("".equals(errMsg)){
                        errMsg = "不能重复取消签字！";
                    }
                    continue;
                }

                if(hvo.getVbillstatus() == IVoucherConstants.AUDITED){
                    auditPz.append(hvo.getPzh() + "，");
                    fail++;
                    if("".equals(errMsg)){
                        errMsg = "已审核不能取消签字！";
                    }
                    continue;
                }

                gl_pzqzserv.saveCancelSignPz(hvo, vsigntor );//取消签字
                success++;
            }
            if (fail > 0){
                json.setStatus(2);
                msg.append("成功：" + success + "，失败：" + fail + ("".equals(errMsg) ? "" : (",原因：" + errMsg)));
            } else {
                json.setStatus(0);
                msg.append("取消签字成功" + success + "条");
            }
            json.setSuccess(true);
            json.setMsg(msg.toString());
//            writeLogRecord(LogRecordEnum.OPE_KJ_OTHERVOUCHER.getValue(),
//                    "取消签字成功" + success + "条", ISysConstants.SYS_2);
        }catch(Exception e){
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "取消签字失败!");
        }
        return ReturnData.ok().data(json);
    }
}
