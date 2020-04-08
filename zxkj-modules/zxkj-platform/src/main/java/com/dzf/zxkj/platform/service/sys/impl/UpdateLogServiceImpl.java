package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.UpdateVersionVO;
import com.dzf.zxkj.platform.model.sys.UserVersionVO;
import com.dzf.zxkj.platform.service.sys.IUpdateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("gl_logpicserv")
public class UpdateLogServiceImpl implements IUpdateLogService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public UpdateVersionVO query(String pk_user, String module) throws DZFWarpException {
        StringBuffer sf = new StringBuffer();
        sf.append("  select t1.*,t2.pk_userversion from ynt_upversion_new t1 ");
        sf.append(" left join ynt_upversion_read_new t2 on t1.versionid = t2.versionid ");
        sf.append("  and t2.pk_user = ?  ");
        sf.append(" where module = ? and islast = ?  ");
        sf.append(" and nvl(t1.dr,0) = 0 and nvl(t2.dr,0) = 0 order by t1.versionno desc ");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_user);
        sp.addParam(module);
        sp.addParam("Y");
        UpdateVersionVO vo = (UpdateVersionVO)singleObjectBO.executeQuery(sf.toString(), sp, new BeanProcessor(UpdateVersionVO.class));
        return vo;
    }

    @Override
    public void save(String pk_corp, String pk_user, String versionid) throws DZFWarpException {
        if(!StringUtil.isEmpty(pk_user) && !StringUtil.isEmpty(versionid)){
            UserVersionVO uservo = new UserVersionVO();
            uservo.setPk_user(pk_user);
            uservo.setVersionid(versionid);
            uservo.setDr(0);
            uservo.setPk_corp(IDefaultValue.DefaultGroup);
            singleObjectBO.saveObject(pk_corp, uservo);
        }
    }
}
