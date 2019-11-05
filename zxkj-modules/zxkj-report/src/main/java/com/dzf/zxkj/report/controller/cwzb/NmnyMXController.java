package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.report.NumMnyDetailVO;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.service.cwzb.INummnyReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.SystemUtil;
import com.dzf.zxkj.report.utils.VoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("gl_rep_sljemxzact")
@Slf4j
public class NmnyMXController {

    @Autowired
    private INummnyReport gl_rep_nmdtserv;

    @PostMapping("/query")
    public ReturnData query(@MultiRequestBody QueryParamVO paramvo,
                            String currkmbm, String xsfzhs) {
        ReportDataGrid grid = new ReportDataGrid();
//        QueryParamVO paramvo = new QueryParamVO();
        try {
//            paramvo = JsonUtils.convertValue(param, QueryParamVO.class);

            //判断请求是否联查而来，如是，设置科目参数
            if(!StringUtil.isEmptyWithTrim(currkmbm)){

                paramvo.setKms_last("_9999999999999999");//加上限
            }

            //前台传参三个值,前两个值为必输项目，第三个为非必输项目
            if(!paramvo.getBegindate1().after(paramvo.getEnddate())){
                String startDate = (paramvo.getBegindate1()).toString();//举例2015-01-10
                String enddate = (paramvo.getEnddate()).toString();//举例2015-08-10
                String pk_inventory = paramvo.getPk_inventory() ;
                String pk_bz = paramvo.getPk_currency();

                if("undefined".equals(pk_bz)){
                    pk_bz = null;
                }

                if(startDate == null || "".equals(startDate)
                        || enddate == null || "".equals(enddate)){
                    grid.setSuccess(false);
                    grid.setRows(new ArrayList<NumMnyDetailVO>());
                    grid.setMsg("参数为空!");
                }else{
                    //开始日期应该在建账日期前
                    CorpVO currcorp = SystemUtil.getLoginCorpVo();
                    DZFDate begdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(currcorp.getBegindate())) ;
                    if(begdate.after(new DZFDate(startDate))){
                        throw new BusinessException("开始日期不能在建账日期("+DateUtils.getPeriod(begdate)+")前!");
                    }
                    String pk_corp = SystemUtil.getLoginCorpId();
                    String userid  = SystemUtil.getLoginUserId();
                    List<NumMnyDetailVO> kmmxvostemp = gl_rep_nmdtserv.getNumMnyDetailVO(startDate, enddate, pk_inventory,paramvo, pk_corp,userid,pk_bz,xsfzhs,begdate);
                    List<NumMnyDetailVO> kmmxvos = handleFs(kmmxvostemp,paramvo);
                    Map<String,NumMnyDetailVO> map = new TreeMap<String, NumMnyDetailVO>();
//					int count = 1;

                    int len = kmmxvos == null ? 0 : kmmxvos.size();
                    for (int i = 0; i < len; i++) {
                        if(!map.containsKey(kmmxvos.get(i).getKmbm())){
                            map.put(kmmxvos.get(i).getKmbm(), kmmxvos.get(i));
                        }

                    }

                    if(map!=null&&map.size()>0){
                        Map<String,NumMnyDetailVO> treemap = new TreeMap<String, NumMnyDetailVO>();
                        List<String> keys = new ArrayList<String>(map.keySet());
                        Iterator it = map.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry entry =(Map.Entry) it.next();
                            String key = (String) entry.getKey();
                            NumMnyDetailVO value=(NumMnyDetailVO) entry.getValue();
                            for(int i = keys.size()-1;i>-1;i--){
                                String parentkey = keys.get(i);

                                if(parentkey.indexOf("_")>-1){
                                    parentkey = parentkey.substring(0, parentkey.indexOf("_"));
                                }

                                if(key.startsWith(parentkey)&&key.length()>parentkey.length()){
                                    NumMnyDetailVO vo = map.get(parentkey);
                                    if(vo == null){
                                        continue;
                                    }
                                    NumMnyDetailVO[] child = (NumMnyDetailVO[]) vo.getChildren();
                                    List<NumMnyDetailVO> childlist = new ArrayList<NumMnyDetailVO>();
                                    if(child!=null){
                                        List<NumMnyDetailVO> list = Arrays.asList(child);
                                        childlist.addAll(list);
                                    }
                                    childlist.add(value);
                                    vo.setChildren(childlist.toArray(new NumMnyDetailVO[childlist.size()]));
//				        			nvo.add(vo);
//				        			if(!treemap.containsKey(vo.getKmbm())){
//				        				treemap.put(vo.getKmbm(), vo);
//									}
                                    break;
                                }
                            }
                            if(!treemap.containsKey(value.getKmbm())&&value.getKmbm().length()==4){
                                treemap.put(value.getKmbm(), value);
                            }
                        }
                        grid.setNumcombox(new ArrayList<NumMnyDetailVO>(map.values()));
                        grid.setNumMnyDetail(new ArrayList<NumMnyDetailVO>(treemap.values()));
                    }
//				}
                    if(kmmxvos!=null && kmmxvos.size()>0){
                        ReportUtil.updateKFx(kmmxvos.toArray(new NumMnyDetailVO[0]));
                    }
                    grid.setTotal((long) (kmmxvos==null?0:kmmxvos.size()));
                    grid.setRows(kmmxvos);
                    grid.setSuccess(true);
                }
            }else{
                grid.setSuccess(false);
                grid.setRows(new ArrayList<NumMnyDetailVO>());
                grid.setMsg("查询失败:查询开始日期，应该在查询结束日期前!!");
            }
        } catch (Exception e) {
            grid.setRows(new ArrayList<NumMnyDetailVO>());
//            printErrorLog(grid, log, e, "查询失败!");
            grid.setSuccess(false);
            grid.setMsg("查询失败!");
        }
        //日志记录
        String qjq = paramvo.getBegindate1() != null ? paramvo.getBegindate1().getYear() + "-" +paramvo.getBegindate1().getMonth() : "";
        String qjm = paramvo.getEnddate() != null ? paramvo.getEnddate().getYear() + "-" +paramvo.getEnddate().getMonth() : "";
//        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT.getValue(),
//                new StringBuffer().append("数量金额明细账查询:")
//                        .append(qjq).append("-").append(qjm).toString(), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);

    }

    /**
     * 处理无发生无余额不显示，有余额无发生不显示(包含无余额无发生不显示)
     * @param kmmxvos
     */
    private List<NumMnyDetailVO> handleFs(List<NumMnyDetailVO> kmmxvos, QueryParamVO paramvo) {
        List<NumMnyDetailVO> result = new ArrayList<NumMnyDetailVO>();

        Map<String, DZFBoolean> periodvalue = new HashMap<String, DZFBoolean>();
        Map<String,String> qcperiodvalue = new HashMap<String,String>();//期初期间

        if(kmmxvos!=null && kmmxvos.size()>0
                && ((paramvo.getXswyewfs()!=null && paramvo.getXswyewfs().booleanValue())
                || (paramvo.getIshowfs()!=null && !paramvo.getIshowfs().booleanValue()))
        ){
            String period = "";
            String key = "";
            for(NumMnyDetailVO vo:kmmxvos){
                period = vo.getOpdate().substring(0, 7);
                key = vo.getKmbm();
                if(StringUtil.isEmpty(vo.getZy()) || !vo.getZy().equals("本期合计")){
                    continue;
                }

                if(paramvo.getIshowfs()!=null && !paramvo.getIshowfs().booleanValue()){
                    if(VoUtils.getDZFDouble(vo.getNmny()).doubleValue()==0
                            && VoUtils.getDZFDouble(vo.getNdmny()).doubleValue()==0
                    ){
                        periodvalue.put(key+period,  DZFBoolean.TRUE);
                    }else{
                        periodvalue.put(key+period,  DZFBoolean.FALSE);
                        if(!qcperiodvalue.containsKey(key)){
                            qcperiodvalue.put(key, DateUtils.getPeriodStartDate(vo.getOpdate().substring(0,7)).toString() );
                        }
                    }
                }else if(paramvo.getXswyewfs()!=null && paramvo.getXswyewfs().booleanValue()){
                    if(VoUtils.getDZFDouble(vo.getNmny()).doubleValue()==0
                            && VoUtils.getDZFDouble(vo.getNdmny()).doubleValue()==0
                            && VoUtils.getDZFDouble(vo.getNymny()).doubleValue() == 0){
                        periodvalue.put(key+period,  DZFBoolean.TRUE);
                    }else{
                        periodvalue.put(key+period, DZFBoolean.FALSE);
                        if(!qcperiodvalue.containsKey(key)){
                            qcperiodvalue.put(key, DateUtils.getPeriodStartDate(vo.getOpdate().substring(0,7)).toString() );
                        }
                    }
                }
            }

            for(NumMnyDetailVO vo:kmmxvos){
                period = vo.getQj().substring(0, 7);
                key = vo.getKmbm();
                DZFBoolean bxs = periodvalue.get(key+period);
                String qcperiod = qcperiodvalue.get(key);
                if(!StringUtil.isEmpty(qcperiod) && "期初余额".equals(vo.getZy())){
                    vo.setOpdate(qcperiod);
                    result.add(vo);
                }
                if(bxs!=null &&  !bxs.booleanValue() && !"期初余额".equals(vo.getZy())){
                    result.add(vo);
                }
            }
        }else{
            return kmmxvos;
        }

        return result;
    }
}
