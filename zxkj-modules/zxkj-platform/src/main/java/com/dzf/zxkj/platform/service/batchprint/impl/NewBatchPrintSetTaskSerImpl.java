package com.dzf.zxkj.platform.service.batchprint.impl;

import com.dzf.file.fastdfs.AppException;
import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.batchprint.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.batchprint.IBatchPrintFileSet;
import com.dzf.zxkj.platform.service.batchprint.INewBatchPrintSetTaskSer;
import com.dzf.zxkj.platform.service.batchprint.impl.util.DefaultBatchPrintSetFactory;
import com.dzf.zxkj.platform.service.batchprint.impl.util.SetCovertTask;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.dzf.zxkj.platform.util.ReportUtil;
import com.dzf.zxkj.report.service.IZxkjReportService;
import com.dzf.zxkj.secret.CorpSecretUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    private IBatchPrintFileSet gl_batchfilesetser;

    @Autowired
    private IUserService userServiceImpl;

    @Override
    public List<BatchPrintSetQryVo> queryPrintVOs(String pk_corp, String cuserid,String period) throws DZFWarpException {
        // 校验
        validateSaveFileSet(pk_corp, cuserid, period);

        // 查询当前所属的公司
        Set<String> cpids = userServiceImpl.querypowercorpSet(cuserid);

        if (cpids == null || cpids.size() == 0 ) {
            throw new BusinessException("该用户无关联公司");
        }

        // 根据归档任务
        SQLParameter  sp = new SQLParameter();
        String wherepart = SqlUtil.buildSqlForIn("a.pk_corp",cpids.toArray(new String[0]));
        StringBuffer tasksql = new StringBuffer();
        tasksql.append(" select a.unitname,c.isgz,a.pk_corp");
        tasksql.append(" from bd_corp  a ");
        //tasksql.append(" left join " +  BatchPrintSetVo.TABLE_NAME + " b  on a.pk_corp = b.pk_corp and b.vprintperiod = ?  ");
        tasksql.append(" left join ynt_qmcl c on a.pk_corp = c.pk_corp  and c.period = ?  and nvl(c.dr,0)=0");
        tasksql.append(" where " + wherepart);
        tasksql.append(" order by a.innercode");
//        sp.addParam(period + "~"+ period);
        sp.addParam(period);

        List<BatchPrintSetVo> taskvoslist = (List<BatchPrintSetVo>) singleObjectBO.executeQuery(tasksql.toString(), sp, new BeanListProcessor(BatchPrintSetVo.class));

        // 当前用户设置的任务
        List<BatchPrintSetVo> taskvos = queryTask(cuserid,"");

        if (taskvoslist!=null && taskvoslist.size() > 0) {
            List<BatchPrintSetQryVo> reslist  = new ArrayList<BatchPrintSetQryVo>();
            for (BatchPrintSetVo vo: taskvoslist) {
                BatchPrintSetQryVo qryvo = new BatchPrintSetQryVo();
                qryvo.setGz_bs(vo.getIsgz());
                qryvo.setPk_corp(vo.getPk_corp());
                if (!StringUtil.isEmpty(vo.getCname())) {
                    qryvo.setCname(vo.getCname());
                } else {
                    qryvo.setCname(CorpSecretUtil.deCode(vo.getUnitname()));
                }
                for (BatchPrintSetVo setvo2 : taskvos) {
                    if (!StringUtil.isEmpty(setvo2.getVprintperiod())
                    && setvo2.getPk_corp().equals(vo.getPk_corp()) && "month".equals(setvo2.getSetselect())) {
                        String vprintperiod = setvo2.getVprintperiod();
                        String[] periods = vprintperiod.split("~");
                        if (period.compareTo(periods[0]) < 0 || period.compareTo( periods[1]) >0) {
                            continue;
                        }
                        if (!StringUtil.isEmpty(setvo2.getVprintcode())) {
                            String[] codevos = setvo2.getVprintcode().split(",");
                            if (codevos!=null && codevos.length > 0) {
                                for (String str: codevos) {
                                    if ("voucher".equals(str)) {
                                        qryvo.setAttributeValue("pz_bs","Y");
                                    } else {
                                        qryvo.setAttributeValue(str+ "_bs", "Y");
                                    }
                                }
                            }
                        }
                        break;
                    }
                }

                reslist.add(qryvo);
            }
            return reslist;
        }
        return null;
    }

    @Override
    public void execTask(String userid) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();

        BatchPrintSetVo[] vos = (BatchPrintSetVo[]) singleObjectBO.queryByCondition(BatchPrintSetVo.class,
                "nvl(dr,0)=0 and (nvl(ifilestatue,0) = 0 or nvl(ifilestatue,0) = 3 )", sp);

        log.info("任务执行的条数:"+vos.length);

        UserVO uservo = zxkjPlatformService.queryUserById(userid);

        zxkjReportService.batchPrint(vos,uservo);
    }

    @Override
    public void deleteTask(String priid) throws DZFWarpException {
        if (StringUtil.isEmpty(priid)) {
            throw new BusinessException("主键信息不能为空!");
        }
        BatchPrintSetVo vo = (BatchPrintSetVo) singleObjectBO.queryByPrimaryKey(BatchPrintSetVo.class, priid);
        if(vo == null){
            return;
        }
        if (!StringUtil.isEmpty(vo.getVfilepath())) {
            FastDfsUtil util = (FastDfsUtil) SpringUtils.getBean("connectionPool");
            try {
                util.deleteFile(vo.getVfilepath().substring(1));
            } catch (AppException e) {
                throw new WiseRunException(e);
            }
        }
        singleObjectBO.deleteObject(vo);

    }

    @Override
    public Object[] downLoadFile(String pk_corp,String id) throws DZFWarpException {
        if(StringUtil.isEmpty(id)){
            throw new BusinessException("信息不能为空");
        }
        BatchPrintSetVo vo = (BatchPrintSetVo) singleObjectBO.queryByPrimaryKey(BatchPrintSetVo.class, id);

        if(vo == null){
            throw new BusinessException("数据不存在!");
        }

        if(StringUtil.isEmpty(vo.getVfilepath())){
            throw new BusinessException("文件不存在!");
        }

        try {
            Object[] objs = new Object[2];

            byte[] bytes = ((FastDfsUtil)SpringUtils.getBean("connectionPool")).downFile(vo.getVfilepath().substring(1));

            objs[0] =  bytes;

            objs[1] = vo.getVfilename();

            vo.setIfilestatue(PrintStatusEnum.LOADED.getCode());

            singleObjectBO.update(vo, new String[]{"ifilestatue"});

            updateBanding(vo);//更新装订信息

            return objs;
        } catch (AppException e) {
            throw new BusinessException("获取文件失败!");
        }
    }

    @Override
    public Object[] downBatchLoadFiles(String pk_corp, String[] ids) throws DZFWarpException {
        if(ids == null || ids.length == 0){
            throw new BusinessException("信息不能为空");
        }
        String wherepart = SqlUtil.buildSqlForIn("pk_batch_print_set", ids);
        BatchPrintSetVo[] setvos =  (BatchPrintSetVo[]) singleObjectBO.queryByCondition(BatchPrintSetVo.class,
                "nvl(dr,0)=0 and ifilestatue in(1,2) and "+wherepart, new SQLParameter());

        if(setvos == null || setvos.length == 0){
            throw new BusinessException("暂无可下载的文件");
        }

        ByteArrayOutputStream zipbyte = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(zipbyte);
        try {
            Object[] objs = new Object[2];

            for(BatchPrintSetVo vo :setvos){
                if(StringUtil.isEmpty(vo.getVfilepath())){
                    continue;
                }

                try {
                    byte[] bytes = ((FastDfsUtil)SpringUtils.getBean("connectionPool")).downFile(vo.getVfilepath().substring(1));

                    if (bytes !=null && bytes.length >0) {
                        zos.putNextEntry(new ZipEntry(vo.getVfilename()));

                        zos.write(bytes);

                        vo.setIfilestatue(PrintStatusEnum.LOADED.getCode());

                        singleObjectBO.update(vo, new String[]{"ifilestatue"});

                        updateBanding(vo);//更新装订信息
                    }
                } catch (AppException e) {
                    log.error(e.getMessage(),e);
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                } catch (DAOException e) {
                    log.error(e.getMessage(),e);
                }
            }

            zos.close();
            objs[0] = zipbyte.toByteArray();
            objs[1] = new DZFDate().toString()+".zip";
            return objs;
        } catch (IOException e) {
            throw new BusinessException("获取文件失败!");
        }finally {
            if(zos !=null){
                try {
                    zos.close();
                } catch (IOException e) {
                }
            }

        }
    }

    private void updateBanding(BatchPrintSetVo vo) {
        CorpVO cpvo = zxkjPlatformService.queryCorpByPk(vo.getPk_corp());
        //装订vo更新
        DZFBoolean isvouchprint = DZFBoolean.FALSE;//凭证
        DZFBoolean iskmprint = DZFBoolean.FALSE;//科目
        DZFBoolean isreportprint = DZFBoolean.FALSE;//财务账表

        if(!StringUtil.isEmpty(vo.getVprintname())){
            if(vo.getVprintname().indexOf("凭证")>-1){
                isvouchprint = DZFBoolean.TRUE;
            }
            if(vo.getVprintname().indexOf("科目总账")>-1
                    || vo.getVprintname().indexOf("科目明细账")>-1
                    || vo.getVprintname().indexOf("现金/银行日记账")>-1
                    || vo.getVprintname().indexOf("发生额及余额表")>-1){
                iskmprint = DZFBoolean.TRUE;
            }
            if(vo.getVprintname().indexOf("资产负债表")>-1
                    || vo.getVprintname().indexOf("利润表")>-1){
                isreportprint = DZFBoolean.TRUE;
            }
        }
        String period = vo.getVprintperiod();
        DZFDate begdate = DateUtils.getPeriodStartDate(period.split("~")[0]);
        if(begdate.before(cpvo.getBegindate())){//从建账日期取
            begdate = cpvo.getBegindate();
        }
        DZFDate enddate = DateUtils.getPeriodEndDate(period.split("~")[1]);
        List<String> periods =  ReportUtil.getPeriods(begdate, enddate);
        Map<String, BandingVO> map = getBandingMap(vo, periods);//获取vo
        BandingVO badingvo = null;
        for(String p:periods){
            badingvo = map.get(p);
            if(badingvo==null){
                badingvo = new BandingVO();
                badingvo.setPeriod(p);
                badingvo.setPk_corp(vo.getPk_corp());
            }
            if(isvouchprint.booleanValue() || iskmprint.booleanValue() || isreportprint.booleanValue()){
                badingvo.setBstatus(1);
            }else{
                badingvo.setBstatus(2);
            }
            badingvo.setIsvouchprint(isvouchprint);
            badingvo.setIskmprint(iskmprint);
            badingvo.setIsreportprint(isreportprint);
            if(StringUtil.isEmpty(badingvo.getPrimaryKey())){
                singleObjectBO.saveObject(vo.getPk_corp(), badingvo);
            }else{
                singleObjectBO.update(badingvo);
            }
        }
    }

    private Map<String, BandingVO> getBandingMap(BatchPrintSetVo vo, List<String> periods) {
        Map<String,BandingVO> map = new HashMap<String,BandingVO>();
        String wherepart = SqlUtil.buildSqlForIn("period", periods.toArray(new String[0]));
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_corp());
        BandingVO[] vos =  (BandingVO[]) singleObjectBO.queryByCondition(BandingVO.class, " nvl(dr,0)=0 and pk_corp = ?  and "+wherepart, sp );

        if(vos!=null && vos.length>0){
            for(BandingVO tvo:vos){
                map.put(tvo.getPeriod(), tvo);
            }
        }
        return map;
    }

    @Override
    public void saveTask(String corpidstr, String userid,String type,String period, String vprintdate,String bsysdate) throws DZFWarpException {

        if (StringUtil.isEmpty(corpidstr)) {
            throw new BusinessException("公司不存在");
        }

        String[] corpids = corpidstr.split(",");

        if (StringUtil.isEmpty(userid)) {
            throw new BusinessException("用户不存在");
        }

        // 查询用户关联的设置
        BatchPrintFileSetVo[] setvos =  gl_batchfilesetser.queryFileSet(userid);

        BatchPrintFileSetVo ressetvo = null;

        if (setvos != null && setvos.length > 0) {
            for (BatchPrintFileSetVo setvo: setvos) {
                if (type.equals(setvo.getSetselect())) {
                    ressetvo = setvo;
                }
            }
        }
        if (ressetvo == null) {
            ressetvo = DefaultBatchPrintSetFactory.getSetvo(type);
        }
        if (ressetvo == null) {
            throw new BusinessException("暂无设置信息");
        }

        for (String cpid: corpids) {
            BatchPrintSetVo taskvo = SetCovertTask.convertTask(ressetvo,cpid,period,userid,vprintdate,bsysdate);
            taskvo.setIfilestatue(PrintStatusEnum.PROCESSING.getCode());
            singleObjectBO.saveObject(cpid, taskvo);
        }

    }

    @Override
    public List<BatchPrintSetVo> queryTask(String userid,String period) throws DZFWarpException {

        if (StringUtil.isEmpty(userid)) {
            throw new BusinessException("查询参数为空");
        }

        // 查询当前所属的公司
        Set<String> cpids = userServiceImpl.querypowercorpSet(userid);

        if (cpids == null || cpids.size() == 0 ) {
            throw new BusinessException("该用户无关联公司");
        }

        StringBuffer qry = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        qry.append(" select a.*,bd_corp.unitname as cname from ");
        qry.append(" " + BatchPrintSetVo.TABLE_NAME + " a");
        qry.append(" left join bd_corp on  a.pk_corp = bd_corp.pk_corp ");
        qry.append(" where nvl(a.dr,0)=0 and  " + SqlUtil.buildSqlForIn("a.pk_corp",cpids.toArray(new String[0])));
        if (!StringUtil.isEmpty(period)) {
            qry.append(" and a.vprintperiod like ? ");
            sp.addParam("%" + period + "%");
        }
        qry.append(" order by a.doperadatetime desc , bd_corp.innercode ");

        List<BatchPrintSetVo> qrylist = (List<BatchPrintSetVo>) singleObjectBO.executeQuery(qry.toString(), sp
                ,new BeanListProcessor(BatchPrintSetVo.class));

        QueryDeCodeUtils.decKeyUtils(new String[]{"cname"}, qrylist, 1 );
        return qrylist;
    }

    private void validateSaveFileSet(String pk_corp, String cuserid,String period) {
        if (StringUtil.isEmpty(cuserid)) {
            throw new BusinessException("用户信息为空");
        }

        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司为空");
        }

        if (StringUtil.isEmpty(period)) {
            throw new BusinessException("查询期间不能为空");
        }

    }

}
