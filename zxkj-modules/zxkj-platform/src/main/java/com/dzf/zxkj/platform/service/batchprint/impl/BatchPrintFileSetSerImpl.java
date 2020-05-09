package com.dzf.zxkj.platform.service.batchprint.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintFileSetVo;
import com.dzf.zxkj.platform.service.batchprint.IBatchPrintFileSet;
import com.dzf.zxkj.platform.service.sys.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("gl_batchfilesetser")
public class BatchPrintFileSetSerImpl implements IBatchPrintFileSet {

    @Autowired
    private SingleObjectBO singleObjectBO = null;

    @Autowired
    private IUserService userServiceImpl = null;

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

}
