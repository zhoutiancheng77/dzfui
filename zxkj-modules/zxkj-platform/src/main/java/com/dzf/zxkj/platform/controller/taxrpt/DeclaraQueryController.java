package com.dzf.zxkj.platform.controller.taxrpt;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.tax.TaxReportDetailVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxDeclarationService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/taxrpt/declaraquery")
@Slf4j
public class DeclaraQueryController extends BaseController {
    @Autowired
    private ITaxDeclarationService taxDeclarationService;
    @Autowired
    private IUserService iuserService;

    // 查询
    @GetMapping("/query")
    public ReturnData<Grid> query(String pk_corp, String periodfrom, String periodto, String sbzt_dm) {
        Grid grid = new Grid();
        try {
            //校验当前公司权限
            Set<String> nnmnc = iuserService.querypowercorpSet(SystemUtil.getLoginUserId());
            if (!nnmnc.contains(pk_corp)) {
                throw new BusinessException("当前操作人，不包含该公司权限");
            }
            List<TaxReportVO> list = taxDeclarationService.queryTaxReprotVOs(periodfrom, periodto, pk_corp, sbzt_dm);
            if (list == null || list.size() == 0) {
                grid.setTotal(Long.valueOf(0));
                grid.setMsg("查询数据为空");
            } else {
                grid.setTotal(Long.valueOf(list.size()));
                grid.setMsg("查询成功");
            }
            grid.setSuccess(true);
            grid.setRows(list);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败");
        }

        // 日志记录
        writeLogRecord(LogRecordEnum.OPE_KJ_TAX, "纳税申报查询", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    // 查询子表数据
    @GetMapping("/queryB")
    public ReturnData queryB(String pk_corp, String pk_taxreport) {
        Grid grid = new Grid();
        grid.setTotal(Long.valueOf(0));
        grid.setSuccess(false);
        try {
            //校验当前公司权限
            Set<String> nnmnc = iuserService.querypowercorpSet(SystemUtil.getLoginUserId());
            if (!nnmnc.contains(pk_corp)) {
                throw new BusinessException("当前操作人，不包含该公司权限");
            }

            List<TaxReportDetailVO> list = taxDeclarationService.queryTaxReprotDetailsVOs(pk_corp, pk_taxreport);

            if (list != null && list.size() > 0) {
                for(TaxReportDetailVO taxReportdetailVO : list){
                    if (!StringUtil.isEmpty(taxReportdetailVO.getSpreadfile())) {// 报表PDF文件下载
                        StringBuffer buff = new StringBuffer();
                        buff.append("<a onclick=\"onShow('").append(taxReportdetailVO.getPk_taxreport())
                                .append("','").append(taxReportdetailVO.getReportcode()).append("','")
                                .append(taxReportdetailVO.getReportname())
                                .append("')\" style=\"width:90px\">查看</a> ");

                        taxReportdetailVO.setSpreadfile(buff.toString());
                    }
                }
                grid.setTotal(Long.valueOf(list.size()));
                grid.setRows(list);
                grid.setSuccess(true);
            }else{
                grid.setSuccess(false);
                grid.setTotal(0l);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "查询子表失败");
        }
        return ReturnData.ok().data(grid);
    }

    /***
     * 获取SpreadJs 数据
     */
    @GetMapping("/getSpreadJsData")
    public ReturnData<Json> getSpreadJsData(String pk_taxreport, String pk_corp, String reportcode, String reportname) {

        Json json = new Json();
        try {
            //校验当前公司权限
            Set<String> nnmnc = iuserService.querypowercorpSet(SystemUtil.getLoginUserId());
            if (!nnmnc.contains(pk_corp)) {
                throw new BusinessException("当前操作人，不包含该公司权限");
            }

            String spreadjson = taxDeclarationService.getSpreadJSData(pk_taxreport, SystemUtil.getLoginUserVo(),reportname, true);
            if (spreadjson != null) {
                json.setData(spreadjson);
                json.setStatus(200);
                json.setSuccess(true);
                json.setMsg("数据加载成功");
            } else {

                json.setSuccess(false);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "获取报表模板数据异常");
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 取期间所属月的最后一天
     *
     * @param date
     * @return
     */
    @GetMapping("/getPeroidDZFDate")
    public DZFDate getPeroidDZFDate(String date) {
        //
        return DateUtils.getPeriodEndDate(DateUtils.getPeriod(new DZFDate(date)));
    }

}
