package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.constant.IDzfServiceConst;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.CustServVO;
import com.dzf.zxkj.platform.service.sys.ICorpQryService;
import com.dzf.zxkj.platform.service.sys.ICustServService;
import com.dzf.zxkj.platform.service.sys.IVersionMngService;
import com.dzf.zxkj.secret.CorpSecretUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("custServServiceImpl")
public class CustServServiceImpl implements ICustServService {

    @Autowired
    private ICorpQryService corpQueryImpl;

    @Autowired
    private IVersionMngService sys_funnodeversionserv;

    @Override
    public CustServVO query(String pk_corp) throws DZFWarpException {
        CorpVO cvo = corpQueryImpl.queryTopCorp(pk_corp);
        CustServVO csvo = new CustServVO();
        if (cvo != null) {
            csvo.setIschannel(cvo.getIschannel());
            csvo.setIsfactory(cvo.getIsfactory());
            csvo.setChanneltype(cvo.getChanneltype());
            csvo.setUnitname(CorpSecretUtil.deCode(cvo.getUnitname()));
        }
        String version = sys_funnodeversionserv.queryKjgsBigVersion(pk_corp);

        if (version.equals(IDzfServiceConst.DzfVersion_01)) {//普通版
            csvo.setIversion(1);
        } else if (version.equals(IDzfServiceConst.DzfVersion_02)) {//C端版
            csvo.setIversion(2);
        } else if (version.equals(IDzfServiceConst.DzfVersion_03)) {//渠道商版
            csvo.setIversion(3);
        } else if (version.equals(IDzfServiceConst.DzfVersion_04)) {//标准版
            csvo.setIversion(4);
        } else if (version.equals(IDzfServiceConst.DzfVersion_05)) {//旗舰版
            csvo.setIversion(5);
        } else if (version.equals(IDzfServiceConst.DzfVersion_06)) {//加盟商版
            csvo.setIversion(6);
        } else if (version.equals(IDzfServiceConst.DzfVersion_07)) {//工厂版
            csvo.setIversion(7);
        }

        if (csvo.getChanneltype() != null && csvo.getChanneltype() == 1) {
            csvo.setIversion(6);
        } else if (csvo.getChanneltype() != null && csvo.getChanneltype() == 2) {//金牌加盟商
            csvo.setIversion(8);
        } else if (csvo.getChanneltype() != null && csvo.getChanneltype() == 9) {//演示加盟商
            csvo.setIversion(9);
        }
        if (csvo.getIsfactory() != null && csvo.getIsfactory().booleanValue()) {
            csvo.setIversion(7);
        }
        return csvo;
    }
}
