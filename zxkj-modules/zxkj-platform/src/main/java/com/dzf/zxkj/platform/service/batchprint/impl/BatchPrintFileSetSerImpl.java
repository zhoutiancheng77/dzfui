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
        tasksql.append(" left join " +  BatchPrintSetVo.TABLE_NAME + " b  on a.pk_corp = b.pk_corp and b.vprintperiod = ?  ");
        tasksql.append(" left join ynt_qmcl c on a.pk_corp = c.pk_corp  and c.period = ? ");
        tasksql.append(" where nvl(b.dr,0)=0 ");
        tasksql.append(" and  "+ wherepart);
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
    public void saveFileSet(BatchPrintFileSetVo[] setvos, String cuserid) throws DZFWarpException {
        if (setvos !=null && setvos.length > 0) {
            for (BatchPrintFileSetVo setvo : setvos) {
                if (!StringUtil.isEmpty(setvo.getPrimaryKey())) {
                    singleObjectBO.update(setvo);
                } else {
                    setvo.setPk_user(cuserid);
                    singleObjectBO.saveObject(setvo.getPk_corp(), setvo);
                }
            }
        }
    }

    @Override
    public BatchPrintFileSetVo[] queryFileSet(String cuserid) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();

        sp.addParam(cuserid);

        BatchPrintFileSetVo[] setvos = (BatchPrintFileSetVo[]) singleObjectBO.queryByCondition(BatchPrintFileSetVo.class,"nvl(dr,0)=0 and pk_user=?", sp);

        if (setvos != null && setvos.length > 0) {
            return setvos;
        } else {
            return null;
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
