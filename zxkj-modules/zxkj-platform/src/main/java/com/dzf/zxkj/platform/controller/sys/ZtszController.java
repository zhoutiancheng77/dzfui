package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.pzgl.ICorpInfoService;
import com.dzf.zxkj.platform.service.sys.IBDCorpService;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.IZtszService;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/sys_kj_ztsz")
@Slf4j
public class ZtszController {

    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private IBDCorpService sys_corpserv;
    @Autowired
    private ICorpInfoService corpInfoSer;
    @Autowired
    private IZtszService ztsz_serv;
    @Autowired
    private IBDCorpTaxService sys_corp_tax_serv;

    /**
     * 更新公司基本信息
     */
    @PostMapping("updateTax")
    public ReturnData<Json> updateTax(@MultiRequestBody CorpTaxVo data, @MultiRequestBody String selTaxReportIds, @MultiRequestBody String unselTaxReportIds) {
        Json json = new Json();
        if (data != null) {
            try {

                // 判断操作用户是否为当前登录公司
                CorpVO curcorpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, SystemUtil.getLoginCorpId());
                String pk_corp = curcorpvo.getFathercorp();
                if (pk_corp.equals(data.getFathercorp())) {
                    CorpVO corp = sys_corpserv.queryByID(data.getPk_corp());
                    if (corp == null) {
                        throw new BusinessException("该数据不存在，或已被删除！");
                    } else if (!pk_corp.equals(corp.getFathercorp())) {
                        throw new BusinessException("对不起，您无操作权限！");
                    }
                    beforeCheck(corp, data);
                    // 1.主表VO
                    // 对需要加密字段进行加密操作：
                    CorpTaxVo[] datas = (CorpTaxVo[]) QueryDeCodeUtils.decKeyUtils(new String[] { "legalbodycode", "phone1",
                            "phone2", "unitname", "unitshortname", "vcorporatephone" }, new CorpTaxVo[] { data }, 0);
                    ztsz_serv.updateCorpTaxVo(datas[0], selTaxReportIds, unselTaxReportIds);
                    // 对需要加密字段进行解密操作：
                    datas = (CorpTaxVo[]) QueryDeCodeUtils.decKeyUtils(new String[] { "legalbodycode", "phone1",
                            "phone2", "unitname", "unitshortname", "vcorporatephone" }, new CorpTaxVo[] { data }, 1);
                    json.setSuccess(true);
                    json.setRows(datas[0]);
                    // 附件信息（此处有问题：返回的附件信息仅是新增的数据，但是现在保存后直接返回列表界面，故不作处理）
                    json.setMsg("更新成功");
                } else {
                    json.setMsg("对不起，您无操作权限！");
                    json.setSuccess(false);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                json.setSuccess(false);
                json.setMsg(e instanceof BusinessException ? e.getMessage() : "更新失败");
            }
        } else {
            json.setSuccess(false);
            json.setMsg("更新失败");
        }
        return ReturnData.ok().data(json);
    }

    private void beforeCheck(CorpVO corpvo, CorpTaxVo data){
        String chname = data.getChargedeptname();
        //辅导期校验
        if("一般纳税人".equals(chname)){
            DZFDate coachbdate = data.getDcoachbdate();//辅导期开始
            DZFDate coachedate = data.getDcoachedate();//辅导期结束

            if(coachbdate != null){
                DZFDate sxdate;
                DZFDate rdsj = data.getDrdsj();//一般认定日期
                Integer sxrq = data.getIsxrq();//一般生效日期
                if(rdsj != null){
                    if(sxrq == 0){
                        sxdate = rdsj;
                    }else{
                        sxdate = DateUtils.getPeriodStartDate(DateUtils.getNextPeriod(DateUtils.getPeriod(rdsj)));
                    }

                    if(coachbdate.compareTo(sxdate) < 0){
                        throw new BusinessException("辅导期的开始日期不能早于一般人生效日期");
                    }

                    if(coachedate != null){
                        if(coachbdate.compareTo(coachedate) > 0){
                            throw new BusinessException("辅导期的开始日期不能晚于辅导期的结束日期");
                        }
                    }
                }

            }
        }
        //校验成本结转类型
        String buildic = corpvo.getBbuildic();
        int cblx = data.getIcostforwardstyle();
        corpInfoSer.checkCbjzlx(buildic, cblx);
    }

    @GetMapping("/queryByInfo")
    public ReturnData<Json> queryByInfo(String pk_gs){
        Json json = new Json();
        try {
            if(StringUtil.isEmpty(pk_gs))
                throw new BusinessException("参数不完整");
            CorpTaxVo vo = sys_corp_tax_serv.queryCorpTaxVOByType(pk_gs, null);
            json.setData(vo);
            json.setSuccess(true);
            json.setMsg("查询成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败");
        }

        return ReturnData.ok().data(json);
    }
}
