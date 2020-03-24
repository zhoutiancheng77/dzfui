package com.dzf.zxkj.platform.service.bill.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.enums.MsgtypeEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bill.BillApplyDetailVo;
import com.dzf.zxkj.platform.model.bill.BillApplyVO;
import com.dzf.zxkj.platform.model.bill.BillHistoryVO;
import com.dzf.zxkj.platform.model.message.MsgAdminVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bill.IBillingProcessService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("gl_kpclserv")
public class BillingProcessServiceImpl implements IBillingProcessService {

    public final static Integer STATUS_APPLY = 0;
    public final static Integer STATUS_BILLING = 1;
    public final static Integer STATUS_SENTOUT = 2;
    public final static Integer STATUS_ACCOUNTING = 3;
    public final static Integer STATUS_TAX = 4;

    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private IUserService userServiceImpl;
    @Autowired
    private ICorpService corpService;

    @Override
    public List<BillApplyVO> query(String pk_corp, String customer,
                                   Integer status) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        StringBuffer sql = new StringBuffer();
        sql.append(" select bill.*, s.user_code as vapplycode, ")
//		.append(" customer.vcompanyname, customer.vcompanytype, ")
//		.append(" customer.vtaxcode, customer.vcompanyaddr, ")
//		.append(" customer.vphone, customer.vbank, customer.vbankcode  ")
                .append(" fzkh.name as vcompanyname,   ")
                .append(" fzkh.taxpayer as vtaxcode,  fzkh.address as vcompanyaddr, ")
                .append(" fzkh.phone_num as vphone, fzkh.bank as vbank, fzkh.account_num  as vbankcode  ")
                .append(" from ynt_app_billapply bill ")
//		.append(" left join ynt_app_customer customer on bill.pk_app_customer = customer.pk_app_customer ")
                .append(" left join YNT_FZHS_B fzkh on bill.pk_app_customer = fzkh.pk_auacount_b and  fzkh.pk_auacount_h = '000001000000000000000001'  ")
                .append(" left join sm_user s on bill.vapplytor = s.cuserid  ")
                .append(" where bill.pk_corp = ? ");
        if (status != null) {
            sql.append(" and ibillstatus = ? ");
            sp.addParam(status);
        }
        if (!StringUtil.isEmpty(customer)) {
            sql.append(" and customer.vcompanyname like ? ");
            sp.addParam("%" + customer + "%");
        }
        sql.append(" order by  bill.dapplydate desc ");
        List<BillApplyVO> rs = (List<BillApplyVO>) singleObjectBO.executeQuery(
                sql.toString(), sp, new BeanListProcessor(BillApplyVO.class));
        if (rs.size() > 0) {
            dealUserName(rs);
        }
        return rs;
    }

    @Override
    public String billing(BillApplyVO[] bills, String userid)
            throws DZFWarpException {
        String pk_corp = bills[0].getPk_corp();
        List<BillApplyVO> billList = new ArrayList<BillApplyVO>();
        List<MsgAdminVO> msgs = new ArrayList<MsgAdminVO>();
        List<BillHistoryVO> historys = new ArrayList<BillHistoryVO>();
        DZFDateTime time = new DZFDateTime();
        for (BillApplyVO bill : bills) {
            Integer status = bill.getIbillstatus();
            if (status == null || status < STATUS_BILLING) {
                bill.setIbillstatus(STATUS_BILLING);
                bill.setVbilltor(userid);
                bill.setDdate(time);
                billList.add(bill);
                historys.add(recodeHistory(bill, time, userid, "开票"));
                String content = "您" + bill.getDapplydate() + "为" + bill.getVcompanyname() + "提交的开票申请已开票，请点击查看";
                msgs.add(generateMessage(bill, time, userid, content));
            }
        }
        singleObjectBO.insertVOArr(pk_corp, historys.toArray(new BillHistoryVO[0]));
        singleObjectBO.insertVOArr(pk_corp, msgs.toArray(new MsgAdminVO[0]));
        singleObjectBO.updateAry(billList.toArray(new BillApplyVO[0]), new String[]{"ibillstatus",
                "vbilltor", "ddate"});

        int success = billList.size();
        int fail = bills.length - success;
        String result = "成功：" + success;
        if (fail > 0) {
            result += ", 失败：" + fail;
        }
        return result;
    }

    @Override
    public String sentOut(BillApplyVO[] bills, String userid)
            throws DZFWarpException {
        String pk_corp = bills[0].getPk_corp();
        List<BillApplyVO> billList = new ArrayList<BillApplyVO>();
        List<MsgAdminVO> msgs = new ArrayList<MsgAdminVO>();
        List<BillHistoryVO> historys = new ArrayList<BillHistoryVO>();
        DZFDateTime time = new DZFDateTime();
        for (BillApplyVO bill : bills) {
            Integer status = bill.getIbillstatus();
            if (status == STATUS_BILLING) {
                bill.setIbillstatus(STATUS_SENTOUT);
                bill.setVsendtor(userid);
                bill.setDsenddate(time);
                billList.add(bill);
                historys.add(recodeHistory(bill, time, userid, "寄出"));
                String content = "您" + bill.getDapplydate() + "为" + bill.getVcompanyname() + "提交的开票申请已寄出，请点击查看";
                msgs.add(generateMessage(bill, time, userid, content));
            }
        }
        singleObjectBO.insertVOArr(pk_corp, historys.toArray(new BillHistoryVO[0]));
        singleObjectBO.insertVOArr(pk_corp, msgs.toArray(new MsgAdminVO[0]));
        singleObjectBO.updateAry(billList.toArray(new BillApplyVO[0]), new String[]{"ibillstatus",
                "vsendtor", "dsenddate"});

        int success = billList.size();
        int fail = bills.length - success;
        String result = "成功：" + success;
        if (fail > 0) {
            result += ", 失败：" + fail;
        }
        return result;
    }

    @Override
    public String accounting(BillApplyVO[] bills, String userid)
            throws DZFWarpException {
        String pk_corp = bills[0].getPk_corp();
        List<BillApplyVO> billList = new ArrayList<BillApplyVO>();
        List<MsgAdminVO> msgs = new ArrayList<MsgAdminVO>();
        List<BillHistoryVO> historys = new ArrayList<BillHistoryVO>();
        DZFDateTime time = new DZFDateTime();
        for (BillApplyVO bill : bills) {
            Integer status = bill.getIbillstatus();
            if (status == STATUS_SENTOUT) {
                bill.setIbillstatus(STATUS_ACCOUNTING);
                bill.setVaccountor(userid);
                bill.setDaccountdate(time);
                billList.add(bill);
                historys.add(recodeHistory(bill, time, userid, "入账"));
                String content = "您" + bill.getDapplydate() + "为" + bill.getVcompanyname() + "提交的开票申请已入账，请点击查看";
                msgs.add(generateMessage(bill, time, userid, content));
            }
        }
        singleObjectBO.insertVOArr(pk_corp, historys.toArray(new BillHistoryVO[0]));
        singleObjectBO.insertVOArr(pk_corp, msgs.toArray(new MsgAdminVO[0]));
        singleObjectBO.updateAry(billList.toArray(new BillApplyVO[0]), new String[]{"ibillstatus",
                "vaccountor", "daccountdate"});

        int success = billList.size();
        int fail = bills.length - success;
        String result = "成功：" + success;
        if (fail > 0) {
            result += ", 失败：" + fail;
        }
        return result;
    }

    @Override
    public String tax(BillApplyVO[] bills, String userid)
            throws DZFWarpException {
        String pk_corp = bills[0].getPk_corp();
        List<BillApplyVO> billList = new ArrayList<BillApplyVO>();
        List<MsgAdminVO> msgs = new ArrayList<MsgAdminVO>();
        List<BillHistoryVO> historys = new ArrayList<BillHistoryVO>();
        DZFDateTime time = new DZFDateTime();
        for (BillApplyVO bill : bills) {
            Integer status = bill.getIbillstatus();
            if (status == STATUS_ACCOUNTING) {
                bill.setIbillstatus(STATUS_TAX);
                bill.setVtaxer(userid);
                bill.setDtaxdate(time);
                billList.add(bill);
                historys.add(recodeHistory(bill, time, userid, "报税"));
                String content = "您" + bill.getDapplydate() + "为" + bill.getVcompanyname() + "提交的开票申请已报税，请点击查看";
                msgs.add(generateMessage(bill, time, userid, content));
            }
        }
        singleObjectBO.insertVOArr(pk_corp, historys.toArray(new BillHistoryVO[0]));
        singleObjectBO.insertVOArr(pk_corp, msgs.toArray(new MsgAdminVO[0]));
        singleObjectBO.updateAry(billList.toArray(new BillApplyVO[0]), new String[]{"ibillstatus",
                "vtaxer", "dtaxdate"});

        int success = billList.size();
        int fail = bills.length - success;
        String result = "成功：" + success;
        if (fail > 0) {
            result += ", 失败：" + fail;
        }
        return result;
    }

    private void dealUserName(List<BillApplyVO> bills) {
        HashMap<String, String> userMap = new HashMap<String, String>();
        String[] idAttrs = {"vapplytor", "vbilltor", "vsendtor", "vaccountor", "vtaxer"};
        String[] nameAttrs = {"apply_name", "billing_name", "sendout_name",
                "accounting_name", "taxer_name"};
        String uid = null;
        String userName = null;
        for (BillApplyVO bill : bills) {
            for (int i = 0; i < idAttrs.length; i++) {
                uid = (String) bill.getAttributeValue(idAttrs[i]);
                if (!StringUtil.isEmpty(uid)) {
                    if (userMap.containsKey(uid)) {
                        userName = userMap.get(uid);
                    } else {
                        userName = userServiceImpl.queryUserJmVOByID(uid).getUser_name();
                    }
                    bill.setAttributeValue(nameAttrs[i], userName);
                }
            }
        }
    }

    private BillHistoryVO recodeHistory(BillApplyVO bill, DZFDateTime time, String userid, String content) {
        BillHistoryVO his = new BillHistoryVO();
        his.setPk_app_billapply(bill.getPk_app_billapply());
        his.setPk_corp(bill.getPk_corp());
        his.setPk_temp_corp(bill.getPk_temp_corp());
        his.setDr(0);
        his.setDopedate(time);
        his.setVopecontent(content);
        his.setVoperatetor(userid);
        return his;

    }

    private MsgAdminVO generateMessage(BillApplyVO bill, DZFDateTime time, String userid, String content) {
        MsgAdminVO msgvo = new MsgAdminVO();
        msgvo.setPk_corp(bill.getPk_corp());
        msgvo.setCuserid(bill.getVapplytor());
        msgvo.setMsgtype(MsgtypeEnum.MSG_TYPE_BILLING.getValue());
        msgvo.setMsgtypename(MsgtypeEnum.MSG_TYPE_BILLING.getName());
        msgvo.setVcontent(content);
        msgvo.setSendman(userid);
        msgvo.setVsenddate(time.toString());
        msgvo.setSys_send(ISysConstants.SYS_KJ);
        msgvo.setVtitle(null);
        msgvo.setIsread(DZFBoolean.FALSE);
        msgvo.setPk_corpk(bill.getPk_corp());//小企业主信息
        msgvo.setDr(0);
        msgvo.setPk_bill(bill.getPrimaryKey());
        return msgvo;
    }

    @Override
    public List<BillApplyDetailVo> queryB(String pk_apply, String pk_corp) throws DZFWarpException {

        if (StringUtil.isEmpty(pk_apply)) {
            throw new BusinessException("ID信息为空!");
        }
        CorpVO cpvo = corpService.queryByPk(pk_corp);

        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_apply);
        sp.addParam(pk_corp);
        StringBuffer qrysql = new StringBuffer();

        if (IcCostStyle.IC_ON.equals(cpvo.getBbuildic())) {//启用存货
            qrysql.append(" select b.*,   ");
            qrysql.append(" c.name as spmc,c.invspec as ggxh, ");
            qrysql.append(" d.name as jldw,c.taxratio as sl ");
            qrysql.append(" from  " + BillApplyDetailVo.TABLE_NAME + " b ");
            qrysql.append(" left join  ynt_inventory c on b.pk_inventory = c.pk_inventory ");
            qrysql.append(" left join ynt_measure d on c.pk_measure = d.pk_measure ");
            qrysql.append(" where nvl(b.dr,0)=0 and nvl(c.dr,0)=0  ");
            qrysql.append(" and  b.pk_app_billapply = ?  and b.pk_corp  = ? ");
        } else {
            qrysql.append(" select b.*, ");
            qrysql.append(" c.name as spmc,c.spec as ggxh,   ");
            qrysql.append(" c.unit as jldw,c.taxratio as sl  ");
            qrysql.append(" from  " + BillApplyDetailVo.TABLE_NAME + " b ");
            qrysql.append(" left join ynt_fzhs_b c on b.pk_app_commodity = c.pk_auacount_b  and c.pk_auacount_h = '000001000000000000000006' ");
            qrysql.append(" where nvl(b.dr,0)=0 and nvl(c.dr,0)=0  ");
            qrysql.append(" and  b.pk_app_billapply = ?  and b.pk_corp  = ? ");
        }
        List<BillApplyDetailVo> vos = (List<BillApplyDetailVo>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(BillApplyDetailVo.class));


        return vos;
    }

    @Override
    public String open(BillApplyVO[] bills, String userid)
            throws DZFWarpException {


        return null;
    }

}
