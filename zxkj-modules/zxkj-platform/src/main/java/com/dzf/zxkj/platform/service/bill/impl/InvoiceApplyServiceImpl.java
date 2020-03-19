package com.dzf.zxkj.platform.service.bill.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IInvoiceApplyConstant;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.enums.MsgtypeEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bill.InvoiceApplyVO;
import com.dzf.zxkj.platform.model.message.MsgAdminVO;
import com.dzf.zxkj.platform.service.bill.IInvoiceApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("gl_kpsqserv")
public class InvoiceApplyServiceImpl implements IInvoiceApplyService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public List<InvoiceApplyVO> query(String userid, int page, int rows, InvoiceApplyVO paramvo) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();

        StringBuffer sf = new StringBuffer();
        sf.append(" select y.*, bd.innercode, bd.unitname ");
        sf.append("   from ynt_invoice_apply y ");
        sf.append("   left join bd_corp bd ");
        sf.append("     on y.pk_corp = bd.pk_corp ");
        sf.append("  where y.cuserid = ? ");
        sf.append("    and nvl(y.dr, 0) = 0 ");
        sp.addParam(userid);

        List<InvoiceApplyVO> list = (List<InvoiceApplyVO>) singleObjectBO.execQueryWithPage(InvoiceApplyVO.class,
                "(" + sf.toString() + ")", null, sp, page, rows, null);

        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }

        String innercode = paramvo.getInnercode();
        String unitname = paramvo.getUnitname();

        boolean flag;
        List<InvoiceApplyVO> result = new ArrayList<InvoiceApplyVO>();
        for(InvoiceApplyVO vo : list){
            flag = true;
            vo.setUnitname(CodeUtils1.deCode(vo.getUnitname()));
            if(!StringUtil.isEmpty(innercode)){
                if(!vo.getInnercode().contains(innercode)){
                    flag = false;
                }
            }

            if(flag && !StringUtil.isEmpty(unitname)){
                if(!vo.getUnitname().contains(unitname)){
                    flag = false;
                }
            }

            if(flag){
                result.add(vo);
            }
        }

        return result;
    }

    @Override
    public List<InvoiceApplyVO> save(String userid, String pk_corp, String[] gss) throws DZFWarpException{
        List<String> pks = new ArrayList(Arrays.asList(gss));
        List<InvoiceApplyVO> ovos = query(userid, gss);
        if(ovos != null && ovos.size() > 0){
            for(InvoiceApplyVO vo : ovos){
                pks.remove(vo.getPk_corp());
            }
        }

        List<InvoiceApplyVO> cvos = new ArrayList<InvoiceApplyVO>();
        InvoiceApplyVO avo = null;
        for(String pk : pks){
            avo = new InvoiceApplyVO();
            avo.setPk_corp(pk);
            avo.setCuserid(userid);
            avo.setCreatedate(new DZFDate());
            avo.setIstatus(IInvoiceApplyConstant.APPLY_STATUS_0);
            avo.setImode(0);//自开模式

            cvos.add(avo);
        }

        String[] ids = singleObjectBO.insertVOArr(pk_corp, cvos.toArray(new InvoiceApplyVO[0]));
        for(int i = 0; i < ids.length; i++){
            cvos.get(i).setPrimaryKey(ids[i]);
        }

        return cvos;
    }

    public List<InvoiceApplyVO> query(String userid, String[] gss) throws DZFWarpException{
        SQLParameter sp = new SQLParameter();
        sp.addParam(userid);
        StringBuffer sf = new StringBuffer();
        sf.append(" select * from ynt_invoice_apply y ");
        sf.append(" where y.cuserid = ? and nvl(dr,0) = 0 ");
        if(gss != null && gss.length > 0){
            sf.append(" and ").append(SqlUtil.buildSqlForIn("pk_corp", gss));
        }
        List<InvoiceApplyVO> list = (List<InvoiceApplyVO>) singleObjectBO.executeQuery(sf.toString(),
                sp, new BeanListProcessor(InvoiceApplyVO.class));

        return list;
    }

    @Override
    public void saveApply(String userid, String pk_corp, InvoiceApplyVO[] vos) throws DZFWarpException{
        //往app发消息
        List<MsgAdminVO> msgs = new ArrayList<MsgAdminVO>();
        DZFDateTime time = new DZFDateTime();

        String content;
        MsgAdminVO msgvo;
        for(InvoiceApplyVO vo : vos){
            vo.setIstatus(IInvoiceApplyConstant.APPLY_STATUS_1);//企业主确认中
            vo.setModifydatetime(time);

            content = String.format("%s 公司提交了开票申请，请尽快处理",
                    new String[] { vo.getUnitname()});
            msgvo = convertVO(vo, time, userid, content);
            msgs.add(msgvo);
        }

        singleObjectBO.insertVOArr(pk_corp, msgs.toArray(new MsgAdminVO[0]));

        //更新单据状态
        singleObjectBO.updateAry(vos, new String[]{ "istatus", "modifydatetime" });
    }

    private MsgAdminVO convertVO(InvoiceApplyVO vo, DZFDateTime time,
                                 String userid, String content){
        MsgAdminVO msgvo = new MsgAdminVO();
        msgvo.setPk_corp(vo.getFathercorp());
        msgvo.setCuserid(userid);
        msgvo.setMsgtype(MsgtypeEnum.MSG_TYPE_KPFW.getValue());
        msgvo.setMsgtypename(MsgtypeEnum.MSG_TYPE_KPFW.getName());
        msgvo.setVcontent(content);
        msgvo.setSendman(userid);
        msgvo.setVsenddate(time.toString());
        msgvo.setSys_send(ISysConstants.SYS_KJ);
        msgvo.setVtitle(null);
        msgvo.setIsread(DZFBoolean.FALSE);
        msgvo.setPk_corpk(vo.getPk_corp());//小企业主信息
        msgvo.setDr(0);
        msgvo.setPk_bill(vo.getPk_invoice_apply());
        return msgvo;
    }
}
