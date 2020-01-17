package com.dzf.zxkj.platform.controller.zcgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.WorkloadManagementVO;
import com.dzf.zxkj.platform.query.zcgl.WorkloadManagementParamVO;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.zcgl.IKpglService;
import com.dzf.zxkj.platform.service.zcgl.IworkloadManagement;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("am_workloadManagement")
@Slf4j
@SuppressWarnings("all")
public class WorkloadManagementController extends BaseController {
    @Autowired
    private IworkloadManagement am_workloadmagserv;
    @Autowired
    private IKpglService am_kpglserv;
    @Autowired
    private IQmclService gl_qmclserv;

    // 查询工作量法数据
    @PostMapping("query")
    public ReturnData<Grid> query(@MultiRequestBody WorkloadManagementParamVO workloadManagementParamVO, @MultiRequestBody CorpVO corpVO, @MultiRequestBody QueryParamVO queryParamvo) {
        Grid grid = new Grid();
        DZFDate begindate = DateUtils.getPeriodStartDate(workloadManagementParamVO.getPeriod());
        DZFDate enddate = DateUtils.getPeriodEndDate(workloadManagementParamVO.getPeriod());
        try {
            // 把字符串变成codelist集合
            if (queryParamvo.getPk_corp() != null && queryParamvo.getPk_corp().length() > 0) {
                List<String> codelist = Arrays.asList(queryParamvo.getPk_corp().split(","));
                queryParamvo.setCorpslist(codelist);
                // 校验
                checkSecurityData(null, codelist.toArray(new String[0]),null);
            }

            queryParamvo.setUserid(SystemUtil.getLoginUserId());

            QueryParamVO paramvo = new QueryParamVO();
            paramvo.setBegindate1(begindate);
            paramvo.setEnddate(enddate);
            paramvo.setPk_corp(workloadManagementParamVO.getCorpId());
            List<WorkloadManagementVO> list1 = am_workloadmagserv.queryWorkloadManagement(paramvo);

            DZFBoolean iszjjt = checkIsjtzj(begindate, enddate, SystemUtil.getLoginUserId(), queryParamvo);
            String prePeriod = DateUtils.getPreviousPeriod(workloadManagementParamVO.getPeriod());
            if (list1 != null && list1.size() > 0) {
                DZFDate beginDate = corpVO.getBusibegindate();
                String beginPeriod = DateUtils.getPeriod(beginDate);
                boolean isBeforeBegin = beginPeriod.compareTo(workloadManagementParamVO.getPeriod()) > 0;
                WorkloadManagementVO vo = null;
                WorkloadManagementVO vo1 = null;
                for (int i = list1.size() - 1; i >= 0; i--) {
                    vo = list1.get(i);
                    vo.setIsjtzj(iszjjt);
                    // 是期初卡片
                    if (vo.getIsperiodbegin() != null && vo.getIsperiodbegin().booleanValue()) {
                        if (isBeforeBegin) {
                            list1.remove(vo);
                            continue;
                        }
                    }else{//非期初资产
                        if(DateUtils.getPeriod(paramvo.getBegindate1()).equals(vo.getAccountdate().substring(0, 7))){
                            list1.remove(vo);
                            continue;
                        }
                    }

                    if (vo.getZjdate() != null
                            && DZFDate.getDate(prePeriod + "-01").after(
                            DZFDate.getDate(vo.getZjdate() + "-01"))) {
                        list1.remove(vo);
                        continue;
                    }
                    // 查询上月工作量数据
                    List<WorkloadManagementVO> list2 = checkLastMonth(prePeriod, workloadManagementParamVO.getCorpId(), vo.getPk_assetcard());
                    if (list2.size() == 0 && vo.getSyljgzl() == null) {
                        vo.setSyljgzl(DZFDouble.ZERO_DBL);
                    }
                    for (int j = list2.size() - 1; j >= 0; j--) {
                        vo1 = list2.get(j);
                        // 工作总量 == 累计工作量(完成工作量了)，不显示
                        if (vo1.getGzzl().getDouble() <= vo1.getLjgzl()
                                .getDouble()) {
                            list1.remove(vo);
                            continue;
                        }
                        // 卡片为空（非期初），工作量表为空---置0
                        if (vo.getSyljgzl() == null && vo1.getLjgzl() == null) {
                            vo.setSyljgzl(DZFDouble.ZERO_DBL);
                            // 卡片为空（非期初），工作量表不为空---取工作量表上个月的（累计工作量）数据
                        } else if (vo1.getLjgzl() != null) {
                            vo.setSyljgzl(vo1.getLjgzl());
                        }
                    }
                }

            }
            grid.setTotal(list1!=null? (long) list1.size():0);
            grid.setRows(list1);
            writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL,"工作量查询", ISysConstants.SYS_2);
            grid.setSuccess(true);
            grid.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    /*
     * 检查上月是否有数据
     */
    private List<WorkloadManagementVO> checkLastMonth(String prePeriod,
                                                      String corpId, String pk_assetcard) throws DZFWarpException {
        QueryParamVO paramvo = new QueryParamVO();
        paramvo.setBegindate1(DateUtils.getPeriodStartDate(prePeriod));
        paramvo.setEnddate(DateUtils.getPeriodEndDate(prePeriod));
        paramvo.setPk_assetcard(pk_assetcard);
        paramvo.setPk_corp(corpId);
        List<WorkloadManagementVO> list1 = am_workloadmagserv
                .queryBypk_assetcard(paramvo);
        return list1;
    }

    /*
     * 检查是否计提
     */
    private DZFBoolean checkIsjtzj(DZFDate begindate, DZFDate enddate,
                                   String userid, QueryParamVO queryParamvo) throws DZFWarpException {
        DZFDate doped = new DZFDate(SystemUtil.getLoginDate());
        DZFBoolean iscarover = queryParamvo.getIscarover();
        DZFBoolean isuncarover = queryParamvo.getIsuncarover();
        List<String> corppks = queryParamvo.getCorpslist();
        if (corppks == null || corppks.size() == 0) {
            corppks = new ArrayList<String>();
            corppks.add(SystemUtil.getLoginCorpId());
        }

        List<QmclVO> qmclList = gl_qmclserv.initquery(corppks, begindate, enddate,
                userid, doped, iscarover, isuncarover);
        DZFBoolean iszjjt = null;
        for (QmclVO qmvo : qmclList) {
            iszjjt = qmvo.getIszjjt();
        }
        return iszjjt;
    }

    // 保存工作量
    @PostMapping("save")
    public ReturnData<Grid> save(@MultiRequestBody WorkloadManagementVO[] list) {
        Grid json = new Grid();
        if (list != null && list.length > 0) {
            // 校验
            checkSecurityData(null,new String[]{SystemUtil.getLoginCorpId()},null);
            for (WorkloadManagementVO workloadManagementVO: list) {
                if (workloadManagementVO.getBygzl() == null) {
                    json.setSuccess(false);
                    json.setMsg("资产名称" + workloadManagementVO.getAssetname() + "，本月工作量不能为空");
                    return ReturnData.ok().data(json);
                }
                workloadManagementVO.setPk_corp(SystemUtil.getLoginCorpId());
            }
            try {
                am_workloadmagserv.save(list);
                json.setSuccess(true);
                json.setMsg("保存成功");
                writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL,"工作量保存",ISysConstants.SYS_2);
            } catch (Exception e) {
                printErrorLog(json, e, "保存失败");
                log.error("保存失败", e);
            }
        } else {
            json.setSuccess(false);
            json.setMsg("保存失败:数据为空。");
        }
        return ReturnData.ok().data(json);
    }

    // 工作量管理:继承上月
    @PostMapping("inherit")
    public ReturnData<Grid> inherit(@MultiRequestBody QueryParamVO paramvo) {
        Grid json = new Grid();
        String period = paramvo.getPeriod();
        String prePeriod = DateUtils.getPreviousPeriod(period);
        paramvo.setBegindate1(DateUtils.getPeriodStartDate(prePeriod));
        paramvo.setEnddate(DateUtils.getPeriodEndDate(prePeriod));
        try {
            // 校验
            checkSecurityData(null,new String[]{paramvo.getPk_corp()},null);
            List<WorkloadManagementVO> list = am_workloadmagserv.queryBypk_assetcard(paramvo);
            if (list != null && list.size() > 0) {
                json.setSuccess(true);
                json.setRows(list);
            } else {
                json.setSuccess(false);
                json.setMsg("无上月数据,请手工填写");
            }

        } catch (Exception e) {
            printErrorLog(json, e, "操作失败");
            log.error("操作失败", e);
        }
        return ReturnData.ok().data(json);
    }
}
