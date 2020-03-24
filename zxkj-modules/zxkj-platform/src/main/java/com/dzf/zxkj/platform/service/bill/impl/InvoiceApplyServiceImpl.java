package com.dzf.zxkj.platform.service.bill.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
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
import com.dzf.zxkj.platform.model.sys.CorpDocVO;
import com.dzf.zxkj.platform.service.bill.IInvoiceApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("gl_kpsqserv")
public class InvoiceApplyServiceImpl implements IInvoiceApplyService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public List<InvoiceApplyVO> query(String userid, int page, int rows, InvoiceApplyVO paramvo) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();

        StringBuffer sf = new StringBuffer();
        sf.append(" select y.*, bd.innercode, bd.unitname, bd.vsoccrecode, bd.legalbodycode, ");
        sf.append("         bd.linkman2, bd.email1, bd.phone1, bd.vprovince  ");
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
        Map<String, String[]> map = getMapByVprovince();
        String[] vproArr;
        List<InvoiceApplyVO> result = new ArrayList<InvoiceApplyVO>();
        for(InvoiceApplyVO vo : list){
            flag = true;
            vo.setUnitname(CodeUtils1.deCode(vo.getUnitname()));
            vo.setPhone1(CodeUtils1.deCode(vo.getPhone1()));
            vo.setLegalbodycode(CodeUtils1.deCode(vo.getLegalbodycode()));

            vproArr = map.get(vo.getVprovince() + "");
            if(vproArr != null){
                vo.setVprovname(vproArr[0]);
                vo.setVprovcode(vproArr[1]);
            }

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

        //查找营业执照相关
       buildCorpDoc(list);

        return result;
    }

    private void buildCorpDoc(List<InvoiceApplyVO> list) {
        if(list == null || list.size() == 0){
            return;
        }

        List<String> pks = new ArrayList<>();
        for(InvoiceApplyVO vo : list){
            pks.add(vo.getPk_corp());
        }

        StringBuffer sf = new StringBuffer();
        sf.append(" select y.pk_doc, y.pk_corp, y.doctemp, y.vfilepath, y.filetype from ynt_corpdoc y where nvl(dr,0)=0 and y.filetype = 2 and ");
        sf.append(SqlUtil.buildSqlForIn("pk_corp", pks.toArray(new String[0])));

        List<CorpDocVO> doclist = (List<CorpDocVO>) singleObjectBO.executeQuery(sf.toString(),
                new SQLParameter(), new BeanListProcessor(CorpDocVO.class));

        if(doclist == null || doclist.size() == 0){
            return;
        }

        Map<String, CorpDocVO> docMap = DZfcommonTools.hashlizeObjectByPk(doclist,
                new String[]{"pk_corp"});

        CorpDocVO docvo;
        for(InvoiceApplyVO vo : list){
            if(docMap.containsKey(vo.getPk_corp())){
                vo.setFiletype(docMap.get(vo.getPk_corp()).getFiletype());
            }
        }

    }

    private Map<String, String[]> getMapByVprovince(){
        Map<String, String[]> map = new HashMap<>();
        map.put("2", new String[]{"北京市", "11"});
        map.put("3", new String[]{"天津市","12"});
        map.put("4", new String[]{"河北省","13"});
        map.put("5", new String[]{"山西省","14"});
        map.put("6", new String[]{"内蒙古自治区","15"});
        map.put("7", new String[]{"辽宁省","21"});
        map.put("8", new String[]{"吉林省","22"});
        map.put("9", new String[]{"黑龙江省","23"});
        map.put("10", new String[]{"上海市","31"});
        map.put("11", new String[]{"江苏省","32"});
        map.put("12", new String[]{"浙江省","33"});
        map.put("13", new String[]{"安徽省","34"});
        map.put("14", new String[]{"福建省","35"});
        map.put("15", new String[]{"江西省","36"});
        map.put("16", new String[]{"山东省","37"});
        map.put("17", new String[]{"河南省","41"});
        map.put("18", new String[]{"湖北省","42"});
        map.put("19", new String[]{"湖南省","43"});
        map.put("20", new String[]{"广东省","44"});
        map.put("21", new String[]{"广西壮族自治区","45"});
        map.put("22", new String[]{"海南省","46"});
        map.put("23", new String[]{"重庆市","50"});
        map.put("24", new String[]{"四川省","51"});
        map.put("25", new String[]{"贵州省","52"});
        map.put("26", new String[]{"云南省","53"});
        map.put("27", new String[]{"西藏自治区","54"});
        map.put("28", new String[]{"陕西省","61"});
        map.put("29", new String[]{"甘肃省","62"});
        map.put("30", new String[]{"青海省","63"});
        map.put("31", new String[]{"宁夏回族自治区","64"});
        map.put("32", new String[]{"新疆维吾尔自治区","65"});

        return map;
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
