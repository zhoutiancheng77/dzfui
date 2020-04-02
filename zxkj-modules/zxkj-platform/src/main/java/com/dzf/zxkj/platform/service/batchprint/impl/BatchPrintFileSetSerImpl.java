package com.dzf.zxkj.platform.service.batchprint.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintFileSetVo;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetQryVo;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.service.batchprint.IBatchPrintFileSet;
import com.dzf.zxkj.platform.service.sys.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("gl_batchfilesetser")
public class BatchPrintFileSetSerImpl implements IBatchPrintFileSet {

    @Autowired
    private SingleObjectBO singleObjectBO = null;

    @Autowired
    private IUserService userServiceImpl = null;

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
        tasksql.append(" select a.unitname,b.*,c.isgz");
        tasksql.append(" from bd_corp  a ");
        tasksql.append(" left join " +  BatchPrintSetVo.TABLE_NAME + " b  on a.pk_corp = b.pk_corp ");
        tasksql.append(" left join ynt_qmcl c on a.pk_corp = c.pk_corp ");
        tasksql.append(" where nvl(b.dr,0)=0 and b.vprintperiod = ?  and c.period = ?");
        sp.addParam(period + "~"+ period);
        sp.addParam(period);

        List<BatchPrintSetVo> taskvoslist = (List<BatchPrintSetVo>) singleObjectBO.executeQuery(tasksql.toString(), sp, new BeanListProcessor(BatchPrintSetVo.class));

        if (taskvoslist!=null && taskvoslist.size() > 0) {
            List<BatchPrintSetQryVo> reslist  = new ArrayList<BatchPrintSetQryVo>();
            for (BatchPrintSetVo vo: taskvoslist) {
                BatchPrintSetQryVo qryvo = new BatchPrintSetQryVo();
                qryvo.setGz_bs(vo.getIsgz());
                qryvo.setPk_corp(vo.getPk_corp());
                if (!StringUtil.isEmpty(vo.getCname())) {
                    qryvo.setCname(vo.getCname());
                } else {
                    qryvo.setCname(CodeUtils1.deCode(vo.getUnitname()));
                }
                if (!StringUtil.isEmpty(vo.getVprintcode())) {
                    String[] codevos = vo.getVprintcode().split(",");
                    if (codevos!=null && codevos.length > 0) {
                        for (String str: codevos) {
                            qryvo.setAttributeValue(str+ "_bs", "Y");
                        }
                    }
                }
                reslist.add(qryvo);
            }
            return reslist;
        }
        return null;
    }

    @Override
    public void saveFileSet(BatchPrintFileSetVo setvo, String pk_corp, String cuserid,String period) throws DZFWarpException {

        // 校验
        validateSaveFileSet(pk_corp, cuserid, period);

        // 查询当前所属的公司
        Set<String> cpids = userServiceImpl.querypowercorpSet(cuserid);

        if (cpids == null || cpids.size() == 0 ) {
            throw new BusinessException("该用户无关联公司");
        }

        String wherepart = SqlUtil.buildSqlForIn("pk_corp", cpids.toArray(new String[0]));

        SQLParameter sp = new SQLParameter();

        sp.addParam(period);

        BatchPrintFileSetVo[] setvos = (BatchPrintFileSetVo[]) singleObjectBO.queryByCondition(BatchPrintFileSetVo.class,"nvl(dr,0)=0 and "+ wherepart, new SQLParameter());

        List<BatchPrintFileSetVo> addlist = new ArrayList<BatchPrintFileSetVo>();// 新增的

        List<BatchPrintFileSetVo> uplist = new ArrayList<BatchPrintFileSetVo>();// 更新的

        BatchPrintFileSetVo tempvo = null;

        for (String cpid: cpids) {
            tempvo = new  BatchPrintFileSetVo();
            BeanUtils.copyProperties(setvo, tempvo);
            tempvo.setPk_corp(cpid);
            if(setvos != null && setvos.length > 0){
                for (BatchPrintFileSetVo qrysetvo : setvos) {
                    if (cpid.equals(qrysetvo.getPk_corp())) {
                        tempvo.setPrimaryKey(qrysetvo.getPrimaryKey());

                        uplist.add(tempvo);
                        break;
                    }
                }
            }
            if (!StringUtil.isEmpty( tempvo.getPrimaryKey())) { // 新增的
                singleObjectBO.saveObject(cpid, tempvo); // 新增的在这里保存
            }
        }

        if (uplist.size() > 0) {
            singleObjectBO.updateAry(uplist.toArray(new BatchPrintFileSetVo[0]));
        }

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
