package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.ExMultiVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.report.KmReportDatagridColumn;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.service.cwzb.IMultiColumnReport;
import com.dzf.zxkj.report.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("gl_rep_multiserv")
@Slf4j
public class MultiColumnController extends ReportBaseController {

    @Autowired
    private IMultiColumnReport gl_rep_multiserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    private String[] getFrozenColumns(){
        return new String[]{"rq","pzh","zy","jf","df","fx","ye","pk_accsubj","pk_tzpz_h"};
    }
    /**
     * 查询科目明细数据
     */
    @PostMapping("/query")
    public ReturnData<Grid> queryAction(@MultiRequestBody KmReoprtQueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        ReportDataGrid grid = new ReportDataGrid();
        KmReoprtQueryParamVO vo = getQueryParamVO(queryvo,corpVO);
        try {
            List<KmReportDatagridColumn> columnList = new ArrayList<KmReportDatagridColumn>();
            List<KmReportDatagridColumn> columnList2 = new ArrayList<KmReportDatagridColumn>();
            /** 先动态生成column数据 */
            /** 开始日期应该在建账日期前 */
            checkPowerDate(vo,corpVO);
            /** 是否显示当年的本年累计 */
            vo.setBtotalyear(DZFBoolean.TRUE);
            /** 动态的列数 */
            Object[] objs = gl_rep_multiserv.getMulColumns(vo);

            ExMultiVO[] mulresvos =  (ExMultiVO[]) objs[0];

            if(mulresvos!=null && mulresvos.length>0){
                List<String> columnlist = (ArrayList<String>) objs[1];
                int len=columnlist==null?0:columnlist.size();

                SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
                SQLParameter sp = new SQLParameter();
                sp.addParam(vo.getPk_corp());
                YntCpaccountVO[] cpvos = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, " nvl(dr,0)=0 and pk_corp = ? ", sp);//AccountCache.getInstance().get("", vo.getPk_corp());
                Map<String,YntCpaccountVO> kmbmmap = new HashMap<String,YntCpaccountVO>();
                for(YntCpaccountVO cpvo:cpvos){
                    kmbmmap.put(cpvo.getAccountcode(), cpvo);
                }
                YntCpaccountVO currvo = kmbmmap.get(vo.getKms_first());
                /** 循环，根据科目确定借/贷方 */
                if(len>0){
                    KmReportDatagridColumn dc2 = new KmReportDatagridColumn();
                    dc2.setTitle("余额");
                    dc2.setField("ye");;
                    dc2.setWidth(150);
                    dc2.setRowspan(2);
                    dc2.setAlign("right");
                    dc2.setHalign("center");
                    columnList.add(dc2);

                    KmReportDatagridColumn dcjf = null ;

                    KmReportDatagridColumn dcdf = null;

                    Integer jfbfint = 0 ;
                    Integer dfbfint = 0 ;
                    List<String> frozenlist = Arrays.asList(getFrozenColumns());
                    Set<String> kmbmlist = new HashSet<String>();
                    for(String keyvalue:columnlist){
                        if(!frozenlist.contains(keyvalue)){
                            if(!kmbmlist.contains(keyvalue)){
                                kmbmlist.add(keyvalue);
                                YntCpaccountVO rescpavo =kmbmmap.get(keyvalue.split("_")[0]);
                                if((rescpavo !=null && rescpavo.getDirection() == 0) || (
                                        !StringUtil.isEmpty(vo.getFzlb()) && currvo.getDirection() == 0
                                )){
                                    if(dcjf  == null ){
                                        dcjf = new KmReportDatagridColumn();
                                    }
                                    dcjf.setTitle("借方");
                                    jfbfint = dcjf.getColspan() == null ? 0: dcjf.getColspan();
                                    dcjf.setColspan(jfbfint.intValue()+1);
                                }else if ((rescpavo !=null && rescpavo.getDirection() == 1) || (
                                        !StringUtil.isEmpty(vo.getFzlb()) && currvo.getDirection() == 1
                                )){
                                    if(dcdf  == null ){
                                        dcdf = new KmReportDatagridColumn();
                                    }
                                    dcdf.setTitle("贷方");
                                    dfbfint = dcdf.getColspan() == null ? 0: dcdf.getColspan();
                                    dcdf.setColspan(dfbfint.intValue()+1);
                                }
                            }
                        }
                    }
                    if(dcjf != null){
                        columnList.add(dcjf);
                    }

                    if(dcdf != null ){
                        columnList.add(dcdf);
                    }
                }
                String key;
                KmReportDatagridColumn dc =null;
                /** 获取属性的名字 */
                String[] strs=null;
                /** 分组，借方在上，贷方在下 */
                List<KmReportDatagridColumn> jfcolumnlist = new ArrayList<KmReportDatagridColumn>();
                List<KmReportDatagridColumn> dfcolumnlist = new ArrayList<KmReportDatagridColumn>();
                for(int i=0;i<len;i++){
                    key=columnlist.get(i);
                    dc = new KmReportDatagridColumn();
                    YntCpaccountVO rescpavo =kmbmmap.get(key.split("_")[0]);
                    /** 获取属性的名字 */
                    strs=key.split("_");
                    dc.setField(strs[0]);
                    dc.setTitle(strs[1]);
                    dc.setHalign("center");
                    dc.setAlign("right");
                    dc.setWidth(100);
                    if((rescpavo !=null && rescpavo.getDirection() == 0) || (
                            !StringUtil.isEmpty(vo.getFzlb()) && currvo.getDirection() == 0
                    )){
                        jfcolumnlist.add(dc);
                    }else if((rescpavo !=null && rescpavo.getDirection() == 1) || (
                            !StringUtil.isEmpty(vo.getFzlb()) && currvo.getDirection() == 1
                    )){
                        dfcolumnlist.add(dc);
                    }
                }

                for(KmReportDatagridColumn dctemp:jfcolumnlist){
                    columnList2.add(dctemp);
                }

                for(KmReportDatagridColumn dctemp:dfcolumnlist){
                    columnList2.add(dctemp);
                }

                HashMap<String, Object> map = null;
                List<Map<String,Object>> resultData = new ArrayList<>();
                /** 这里是重点 */
                List<ExMultiVO> loanList = new ArrayList<ExMultiVO>();
                if (mulresvos != null && mulresvos.length > 0) {
                    int i = 0;
                    for (ExMultiVO votemp : mulresvos) {
                        votemp.setPk_currency(vo.getPk_currency());
                        resultData.add(votemp.getHash());
                    }
                }
                grid.setRows(resultData);
                grid.setColumns(columnList);
                grid.setColumnlist2(columnList2);
                grid.setSuccess(true);
                grid.setMsg("查询成功!");

            }else{
                grid.setColumns(columnList);
                grid.setColumnlist2(columnList2);
                grid.setSuccess(false);
                grid.setRows(new ArrayList<ExMultiVO>());
                grid.setMsg("查询数据为空!");
            }
        } catch (Exception e) {
            grid.setRows(new ArrayList<KmMxZVO>());
            printErrorLog(grid, e, "查询失败!");
        }

//        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT.getValue(),
//                "多栏账查询:"+vo.getBegindate1().toString().substring(0, 7)
//                        +"-"+ vo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);

        return ReturnData.ok().data(grid);
    }


    /**
     * 获取辅助项目参照
     */
    @GetMapping("queryFzxm")
    public ReturnData<Grid> getFzxm(@RequestParam("corpid") String pk_corp) {
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        Grid grid = new Grid();
        try {
            AuxiliaryAccountBVO[] bvos =zxkjPlatformService.queryAllB(pk_corp) ;
            grid.setRows(bvos);
            grid.setSuccess(true);
        } catch (Exception e) {
            log.error("辅助类别查询失败:", e);
            grid.setRows(new ArrayList<AuxiliaryAccountHVO>());
            printErrorLog(grid, e, "辅助类别查询失败!");
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 序列化成Json字符串
     *
     * @return
     * @throws Exception
     */
    private static String toJson(String str, Map<String, Object> map, int i) {
        str = str + "{";
        for (String key : map.keySet()) {
            if(map.get(key)!=null){
                str = str + "\"" + key + "\"" + ":\"" + map.get(key).toString().replaceAll("\n", "") + "\",";
            }else{
                str = str + "\"" + key + "\"" + ":\"" + map.get(key) + "\",";
            }
        }
        str = str + "},";
        return str;
    }

    private KmReoprtQueryParamVO getQueryParamVO(KmReoprtQueryParamVO paramvo, CorpVO corpVO) {
        paramvo.setXsyljfs(DZFBoolean.TRUE);
        paramvo.setXswyewfs(DZFBoolean.TRUE);
        paramvo.setIshasjz(DZFBoolean.FALSE);
        paramvo.setPk_corp(paramvo.getCorpIds1());
        paramvo.setBtotalyear(DZFBoolean.TRUE);
        paramvo = (KmReoprtQueryParamVO) super.getQueryParamVO(paramvo,corpVO );
        return paramvo;
    }



}
