package com.dzf.zxkj.platform.service.batchprint.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.batchprint.PrintStatusEnum;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.batchprint.BatchPrintUtil;
import com.dzf.zxkj.platform.service.batchprint.INewBatchPrintSetTaskSer;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.BeanUtils;
import com.dzf.zxkj.platform.util.ReportUtil;
import com.dzf.zxkj.report.service.IZxkjReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("newbatchprintser")
@Slf4j
public class NewBatchPrintSetTaskSerImpl implements INewBatchPrintSetTaskSer {

    @Autowired
    private SingleObjectBO singleObjectBO = null;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @Autowired
    private IZxkjReportService zxkjReportService;

    @Autowired
    private IUserService userServiceImpl;

    @Override
    public void execTask() throws DZFWarpException {
        //生成文件
        SQLParameter sp = new SQLParameter();
        BatchPrintSetVo[] vos = (BatchPrintSetVo[]) singleObjectBO.queryByCondition(BatchPrintSetVo.class, "nvl(dr,0)=0 and (nvl(ifilestatue,0) = 0 or nvl(ifilestatue,0) = 3 )", sp);

        if(vos!=null && vos.length>0){
            for(BatchPrintSetVo vo:vos ){
                printReportFromSetVos(vo);
            }
        }

    }

    private void printReportFromSetVos(BatchPrintSetVo vo) throws DZFWarpException {
        QueryParamVO paramvo = null;
        Object[] obj = null;
        CorpVO cpvo = null;
        paramvo = new QueryParamVO();
        try {
            cpvo =  zxkjPlatformService.queryCorpByPk(vo.getPk_corp());
            if (cpvo.getBegindate() == null) {
                throw new BusinessException("公司尚未建账");
            }
            Map<String, String> pmap = new HashMap<String, String>();// 打印参数
            Map<String, SuperVO[]> map = new LinkedHashMap<String, SuperVO[]>();// 打印数据
            // 校验
            if (StringUtil.isEmpty(vo.getVprintname()) || StringUtil.isEmpty(vo.getVprintperiod())) {
                throw new BusinessException("参数不能为空");
            }

            putPrintMap(vo, pmap);// 获取打印参数

            getMapData(vo.getVprintperiod(), vo.getVprintname(), paramvo, vo, map, cpvo);// 获取打印数据

            upPrintSetVO(vo, pmap, map);

        } catch (Exception e) {
            vo.setIfilestatue(PrintStatusEnum.GENFAIL.getCode());
            if (e instanceof BusinessException) {
                vo.setVmemo(e.getMessage());
            } else {
                log.error("错误", e);
                vo.setVmemo("生成失败!");
            }
        }
        singleObjectBO.update(vo,new String[]{"ifilestatue","vfilepath","vfilename","vmemo","dgendatetime"});
    }

    private void getMapData(String qj,String reportname,QueryParamVO paramvo, BatchPrintSetVo vo,
                            Map<String, SuperVO[]> map,CorpVO cpvo) {
        KmZzVO[] zzvos =  null;
        KmMxZVO[] kmmxvos = null;
        KmMxZVO[] xjyhvos = null;
        List<ZcFzBVO[]> zcfzvos = null;
        List<LrbVO[]> lrbvos = null;
        FseJyeVO[] fsejyevos  = null;
        String[] reportnames = reportname.split(",");

        String begperiod  = DateUtils.getPeriod(paramvo.getBegindate1());

        paramvo.setQjq(begperiod);

        String vprintperiod = DateUtils.getPeriod(paramvo.getBegindate1())+"~"+DateUtils.getPeriod(paramvo.getEnddate());

        //排序
        String[] defaultnames = new String[]{"凭证","科目总账","科目明细账","现金/银行日记账","发生额及余额表","资产负债表","利润表","凭证封皮","总账明细账封皮"};

        for(String str:defaultnames){
            if(Arrays.asList(reportnames).contains(str)){
                try {
                    if("凭证".equals(str)){
                        VoucherParamVO voucherparmvo = new VoucherParamVO();
                        voucherparmvo.setPk_corp(paramvo.getPk_corp());
                        voucherparmvo.setBegindate(DateUtils.getPeriodStartDate(begperiod));
                        voucherparmvo.setEnddate(paramvo.getEnddate());
//						List<TzpzHVO> hvos =  gl_tzpzserv.queryVoucher(voucherparmvo);
//						if(hvos!=null && hvos.size()>0){
//							map.put("凭证_"+vprintperiod, hvos.toArray(new TzpzHVO[0]));
//						}
                    } else if ("科目总账".equals(str)) {
                        QueryParamVO kmzzqryvo = new QueryParamVO();
                        BeanUtils.copyNotNullProperties(paramvo, kmzzqryvo);
                        kmzzqryvo.setCjq(1);
                        kmzzqryvo.setCjz(1);
                        zzvos = zxkjReportService.getKMZZVOs(kmzzqryvo, null);
                        if(zzvos!=null && zzvos.length>0){
                            List<KmZzVO> kmzzvos = KmzzUtil.filterQC(zzvos);
                            map.put("科目总账_"+vprintperiod, kmzzvos.toArray(new KmZzVO[0]));
                        }
                    } else if ("科目明细账".equals(str)) {
                        QueryParamVO kmmxparamvo = new QueryParamVO();
                        BeanUtils.copyNotNullProperties(paramvo, kmmxparamvo);
                        kmmxvos =  zxkjReportService.getKMMXZConFzVOs(kmmxparamvo, null);
                        if(kmmxvos!=null && kmmxvos.length>0){
                            kmmxvos = KmmxUtil.filterQcVos(kmmxvos,kmmxparamvo.getPk_corp());
                            map.put("科目明细账_"+vprintperiod, kmmxvos);
                        }
                    } else if ("现金/银行日记账".equals(str)) {
                        QueryParamVO xjrjparamvo =  new QueryParamVO();
                        BeanUtils.copyNotNullProperties(paramvo, xjrjparamvo);
                        xjyhvos = zxkjReportService.getXJRJZVOsConMo(xjrjparamvo.getPk_corp(),
                                xjrjparamvo.getKms_first(),xjrjparamvo.getKms_last(),xjrjparamvo.getBegindate1(),
                                xjrjparamvo.getEnddate(), xjrjparamvo.getXswyewfs(),
                                xjrjparamvo.getXsyljfs(), xjrjparamvo.getIshasjz(),
                                xjrjparamvo.getIshassh(), xjrjparamvo.getPk_currency(), null,null);
                        if(xjyhvos!=null && xjyhvos.length>0){
                            for(KmMxZVO xjrjvo:xjyhvos){//
                                xjrjvo.setKm(xjrjvo.getKm().trim());
                                if(xjrjvo.getZy()!=null && ReportUtil.bSysZy(xjrjvo) && (xjrjvo.getZy().equals("期初余额") ||xjrjvo.getZy().equals("本月合计") || xjrjvo.getZy().equals("本年累计"))){
                                    xjrjvo.setRq(xjrjvo.getRq()+xjrjvo.getDay());
                                }
                            }
                            map.put("现金/银行日记账_"+vprintperiod, xjyhvos);
                        }
                    } else if ("资产负债表".equals(str)) {
                        zcfzvos = zxkjReportService.getZcfzVOs(paramvo.getBegindate1(), paramvo.getEnddate(), paramvo.getPk_corp(),
                                "N", new String[]{"N","N","N","N","N"}, null);
                        if(zcfzvos!=null && zcfzvos.size()>0){
                            for(ZcFzBVO[] tempvos:zcfzvos){
                                map.put("资产负债表_"+DateUtils.getPeriodEndDate(tempvos[0].getPeriod()), tempvos);
                            }
                        }
                    } else if ("利润表".equals(str)) {
                        QueryParamVO lrbparamvo =  new QueryParamVO();
                        BeanUtils.copyNotNullProperties(paramvo, lrbparamvo);
                        lrbparamvo.setRptsource("lrb");
                        Object[] objtemp  = getBaseData(vo.getVprintperiod(), lrbparamvo, vo, cpvo);
                        if("小规模纳税人".equals(cpvo.getChargedeptname())){
                            Map<String, LrbquarterlyVO[]> lrbjbmap =  zxkjReportService.getLRBquarterlyVOs(lrbparamvo, objtemp);
                            if(lrbjbmap!=null && lrbjbmap.size()>0){
                                for(Map.Entry<String, LrbquarterlyVO[]> entry:lrbjbmap.entrySet()){
                                    map.put("利润表季报_"+entry.getKey(), entry.getValue());
                                }
                            }
                        }else{
                            lrbvos =  zxkjReportService.getBetweenLrbMap(lrbparamvo.getBegindate1(), lrbparamvo.getEnddate(), lrbparamvo.getPk_corp(), "", objtemp,null);
                            if(lrbvos!=null && lrbvos.size()>0){
                                for(LrbVO[] tempvos:lrbvos){
                                    map.put("利润表_"+tempvos[0].getPeriod(), tempvos);
                                }
                            }
                        }
                    }else if ("发生额及余额表".equals(str)) {
                        QueryParamVO fspparamvo = new QueryParamVO();
                        BeanUtils.copyNotNullProperties(paramvo, fspparamvo);
                        fsejyevos = zxkjReportService.getFsJyeVOs(fspparamvo, 1);
                        if(fsejyevos != null && fsejyevos.length > 0){
                            FseJyeVO totalvo = handleFsye(fsejyevos);
                            FseJyeVO[] totalvos = new FseJyeVO[fsejyevos.length+1];
                            System.arraycopy(fsejyevos, 0, totalvos, 0, fsejyevos.length);
                            totalvos[fsejyevos.length] = totalvo;
                            map.put("发生额及余额表_"+vprintperiod, totalvos);
                        }
                    }else if("总账明细账封皮".equals(str)){
                        map.put("总账明细账封皮_"+vprintperiod, null);
                    } else if("凭证封皮".equals(str)){
                        map.put("凭证封皮_"+vprintperiod, null);
                    }
                } catch (Exception e) {
                    if(e instanceof BusinessException){
                        log.error(e.getMessage(),e);
                    }else {
                        throw new WiseRunException(e);
                    }
                }
            }
        }
    }

    private FseJyeVO handleFsye (  FseJyeVO[] fsejyevos) {
        DZFDouble qcjfhj = DZFDouble.ZERO_DBL;
        DZFDouble qcdfhj = DZFDouble.ZERO_DBL;
        DZFDouble fsjfhj = DZFDouble.ZERO_DBL;
        DZFDouble fsdfhj = DZFDouble.ZERO_DBL;
        DZFDouble jftotalhj = DZFDouble.ZERO_DBL;
        DZFDouble dftotalhj =DZFDouble.ZERO_DBL;
        DZFDouble qmjfhj = DZFDouble.ZERO_DBL;
        DZFDouble qmdfhj = DZFDouble.ZERO_DBL;
        FseJyeVO totalvo = new FseJyeVO();
        for (FseJyeVO fsevo : fsejyevos) {
            if ("0".equals(fsevo.getKmlb())) {
                fsevo.setKmlb("资产");
            } else if ("1".equals(fsevo.getKmlb())) {
                fsevo.setKmlb("负债");
            } else if ("2".equals(fsevo.getKmlb())) {
                fsevo.setKmlb("共同");
            } else if ("3".equals(fsevo.getKmlb())) {
                fsevo.setKmlb("所有者权益");
            } else if ("4".equals(fsevo.getKmlb())) {
                fsevo.setKmlb("成本");
            } else if ("5".equals(fsevo.getKmlb())) {
                fsevo.setKmlb("损益");
            } else {
                fsevo.setKmlb("合计");
            }
            if(fsevo.getAlevel() == 1){
                qcjfhj = SafeCompute.add(qcjfhj, fsevo.getQcjf());
                qcdfhj = SafeCompute.add(qcdfhj, fsevo.getQcdf());
                fsjfhj = SafeCompute.add(fsjfhj, fsevo.getFsjf());
                fsdfhj = SafeCompute.add(fsdfhj, fsevo.getFsdf());
                jftotalhj = SafeCompute.add(jftotalhj, fsevo.getJftotal());
                dftotalhj = SafeCompute.add(dftotalhj, fsevo.getDftotal());
                qmjfhj = SafeCompute.add(qmjfhj, fsevo.getQmjf());
                qmdfhj = SafeCompute.add(qmdfhj, fsevo.getQmdf());
            }
        }
        totalvo.setKmlb("合计:");
        totalvo.setQcjf(qcjfhj);
        totalvo.setQcdf(qcdfhj);
        totalvo.setFsjf(fsjfhj);
        totalvo.setFsdf(fsdfhj);
        totalvo.setJftotal(jftotalhj);
        totalvo.setDftotal(dftotalhj);
        totalvo.setQmjf(qmjfhj);
        totalvo.setQmdf(qmdfhj);
        return totalvo;
    }

    private void putPrintMap(BatchPrintSetVo vo, Map<String, String> pmap) {
        pmap.put("left",String.valueOf( vo.getDleftmargin()));
        pmap.put("top",String.valueOf(vo.getDtopmargin()));
        pmap.put("printdate",vo.getDprintdate() == null ? new DZFDate().toString():vo.getDprintdate().toString());
        pmap.put("font",vo.getVfontsize().toString());
        pmap.put("pageNum","1");
    }

    /**
     * 查询科目明细账数据
     * @param qj
     * @param paramvo
     * @param vo
     * @param cpvo
     * @return
     */
    private Object[] getBaseData(String qj, QueryParamVO paramvo, BatchPrintSetVo vo,CorpVO cpvo) {
        String[] qjs = qj.split("~");
        Object[] obj;
        paramvo.setPk_corp(vo.getPk_corp());
        Integer begyear = Integer.parseInt(qjs[0].substring(0, 4));
        DZFDate begdate = DateUtils.getPeriodEndDate((begyear-1)+"-"+qjs[0].substring(5, 7));
        if(begdate.before(cpvo.getBegindate())){
            begdate = new DZFDate(cpvo.getBegindate().getYear()+"-01-01");
        }
        DZFDate enddate = DateUtils.getPeriodEndDate(qjs[1]);
        if(enddate.before(cpvo.getBegindate())){
            throw new BusinessException("查询区间"+qj+"中在该公司建账日期("+cpvo.getBegindate()+")前");
        }
        paramvo.setBegindate1(begdate);
        paramvo.setEnddate(DateUtils.getPeriodEndDate(qjs[1]));
        paramvo.setIshasjz(DZFBoolean.FALSE);
        paramvo.setXswyewfs(DZFBoolean.FALSE);
        paramvo.setBtotalyear(DZFBoolean.TRUE);//本年累计
        paramvo.setCjq(1);
        paramvo.setCjz(6);
        obj =  zxkjReportService.getKMMXZVOs1(paramvo, false);//获取基础数据(科目明细账)
        //重新赋值真正查询日期
        begdate = DateUtils.getPeriodEndDate((begyear)+"-"+qjs[0].substring(5, 7));
        if(begdate.before(cpvo.getBegindate())){
            begdate = cpvo.getBegindate();
        }
        paramvo.setBegindate1(begdate);
        return obj;
    }


    private void upPrintSetVO(BatchPrintSetVo vo,Map<String, String> pmap,Map<String,SuperVO[]> map){
        String fileid = "";//文件id

        if(map.size() ==0){//如果不存在数据则不进行文件生成
            throw new BusinessException("公司尚未做账!");
        }

        BatchPrintUtil util = new BatchPrintUtil();
        CorpVO cpvo =  null;
        try {
            cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, vo.getPk_corp());
            fileid = util.print(map, pmap, vo.getVoperateid(), cpvo,cpvo.getUnitcode(),vo,userServiceImpl);
        } catch (Exception e) {
            throw new WiseRunException(e);
        }

        vo.setIfilestatue(PrintStatusEnum.GENERATE.getCode());

        vo.setVfilepath(fileid);

        vo.setDgendatetime(new DZFDateTime());

        vo.setVmemo("文件已生成!");

        SimpleDateFormat f=new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒");
        String str_time = f.format(new Date());

        vo.setVfilename(CodeUtils1.deCode(cpvo.getUnitname())+"("+str_time+")"+".pdf");
    }
}
