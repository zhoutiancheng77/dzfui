package com.dzf.zxkj.platform.service.bill.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.common.constant.IInvoiceApplyConstant;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.enums.MsgtypeEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bill.BillApplyDetailVo;
import com.dzf.zxkj.platform.model.bill.BillApplyVO;
import com.dzf.zxkj.platform.model.bill.BillHistoryVO;
import com.dzf.zxkj.platform.model.message.MsgAdminVO;
import com.dzf.zxkj.platform.model.piaotong.PiaoTongResVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.bill.IBillingProcessService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.zncs.CommonXml;
import com.dzf.zxkj.platform.util.zncs.PiaoTongUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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
    public List<BillApplyVO> query(String pk_corp, BillApplyVO appvo) throws DZFWarpException {
        Integer status = appvo.getIbillstatus();
        String customer = appvo.getVcompanyname();

        SQLParameter sp = new SQLParameter();
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
        .append(" where nvl(bill.dr, 0)=0 ");
        if(StringUtil.isEmpty(appvo.getPk_app_billapply())) {
            sql.append(" and bill.pk_corp = ? ");
            sp.addParam(pk_corp);
            if (status != null) {
                sql.append(" and ibillstatus = ? ");
                sp.addParam(status);
            }
            if (!StringUtil.isEmpty(customer)) {
                sql.append(" and customer.vcompanyname like ? ");
                sp.addParam("%" + customer + "%");
            }
            sql.append(" order by  bill.dapplydate desc ");
        }else {
            sql.append(" and bill.pk_app_billapply = ? ");
            sp.addParam(appvo.getPk_app_billapply());
        }

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
    public boolean createKp(BillApplyVO vo, UserVO uservo, StringBuffer msg) throws DZFWarpException{
        boolean flag = true;
        try{
            List<BillApplyVO> list = query(null, vo);
            if(list == null || list.size() == 0 || list.size() > 1)
                throw new BusinessException("参数不完整,请检查");
            BillApplyVO billvo = list.get(0);
            List<BillApplyDetailVo> detailList = queryB(billvo.getPk_app_billapply(), billvo.getPk_corp());
            if(detailList == null || detailList.size() == 0)
                throw new BusinessException("子参数不完整,请检查");

            PiaoTongResVO resvo = requestLp(billvo, detailList);
            if(resvo == null){
                throw new BusinessException("请求返回报错,请联系管理员");
            }

            if(CommonXml.rtnsucccode.equals(resvo.getCode())){
                vo.setIbillstatus(IInvoiceApplyConstant.INV_STATUS_5);
                vo.setMemo(resvo.getMsg());
            }else{
                vo.setIbillstatus(IInvoiceApplyConstant.INV_STATUS_6);
                vo.setMemo(resvo.getMsg());
            }

            singleObjectBO.update(vo, new String[] { "ibillstatus", "memo"});
        }catch (Exception e){
            log.error("错误", e.getMessage());
            flag = false;
        }

        return flag;
    }

    private PiaoTongResVO requestLp(BillApplyVO applyVO, List<BillApplyDetailVo> childList){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("taxpayerNum", "500102201007206608");  //销方税号
        map.put("invoiceReqSerialNo", PiaoTongUtil.getSerialNo(PiaoTongUtil.xxptbm, 2));//发票请求流水号
        map.put("buyerName", "购买方名称");//购买方名称
        map.put("buyerTaxpayerNum", "XX0000000000000000");//购买方税号(非必填,个人发票传null)
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> listMapOne = new HashMap<String, String>();
        listMapOne.put("taxClassificationCode", "1010101020000000000");//税收分类编码(可以按照Excel文档填写)
        listMapOne.put("quantity", "1.00");//数量
        listMapOne.put("goodsName", "货物名称");//货物名称
        listMapOne.put("unitPrice", "56.64");//单价
        listMapOne.put("invoiceAmount", "56.64");//金额
        listMapOne.put("taxRateValue", "0.16");//税率
        listMapOne.put("includeTaxFlag", "0");//含税标识
        //以下为零税率开票相关参数
        listMapOne.put("zeroTaxFlag", null);//零税率标识(空:非零税率,0:出口零税率,1:免税,2:不征税,3:普通零税率)
        listMapOne.put("preferentialPolicyFlag", null);//优惠政策标识(空:不使用,1:使用)   注:零税率标识传非空 此字段必须填写为"1"
        listMapOne.put("vatSpecialManage", null);//增值税特殊管理(preferentialPolicyFlag为1 此参数必填)
        list.add(listMapOne);
        map.put("itemList", list);

        String content = JsonUtils.serialize(map);

        PiaoTongResVO resVO = PiaoTongUtil.request(content, "/invoiceBlue.pt");
        return resVO;
    }

    @Override
    public BillApplyVO queryBySerialNo(String serialno) throws DZFWarpException {
        String sql = "select * from ynt_app_billapply y where nvl(dr,0)=0 and y.invoserino = ? ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(serialno);
        BillApplyVO applyVO = (BillApplyVO) singleObjectBO.executeQuery(sql, sp,
                new BeanProcessor(BillApplyVO.class));
        return applyVO;
    }

    @Override
    public boolean createHc(BillApplyVO vo, UserVO uservo, StringBuffer msg) throws DZFWarpException{
        boolean flag = true;
        try{
            List<BillApplyVO> list = query(null, vo);
            if(list == null || list.size() == 0 || list.size() > 1)
                throw new BusinessException("参数不完整,请检查");
            BillApplyVO billvo = list.get(0);

            PiaoTongResVO resvo = requestHc(billvo);
            if(resvo == null){
                throw new BusinessException("请求返回报错,请联系管理员");
            }

            if(CommonXml.rtnsucccode.equals(resvo.getCode())){

            }else{

                flag = false;
            }

            singleObjectBO.update(vo, new String[] { "ibillstatus", "memo"});
        }catch (Exception e){
            log.error("错误", e.getMessage());
            flag = false;
        }

        return flag;
    }

    private PiaoTongResVO requestHc(BillApplyVO applyVO){
        Map<String, Object> map = new HashMap<String, Object>();

        DZFDouble amount = SafeCompute.sub(DZFDouble.ZERO_DBL, applyVO.getNtaxtotal());
        amount = amount.setScale(2, DZFDouble.ROUND_UP);

        map.put("taxpayerNum", "500102201007206608");//销方税号(请于要冲红的蓝票税号一致)
        // TODO 请更换请求流水号前缀
        map.put("invoiceReqSerialNo", PiaoTongUtil.getSerialNo(PiaoTongUtil.xxptbm, 2));//发票流水号 (唯一, 与蓝票发票流水号不一致)
        map.put("invoiceCode", applyVO.getFpdm());//冲红发票的发票代码
        map.put("invoiceNo", applyVO.getFphm());//冲红发票的发票号码
        map.put("redReason", "冲红");//冲红原因
        map.put("amount", "-64.00");//冲红金额 (要与原发票的总金额一致)

        String content = JsonUtils.serialize(map);

        PiaoTongResVO resVO = PiaoTongUtil.request(content, "/invoiceRed.pt");
        return resVO;
    }

    @Override
    public boolean delete(BillApplyVO vo, UserVO uservo, StringBuffer msg) throws DZFWarpException{
        boolean flag = true;
        try{
            String sqlA = "update ynt_app_billapply y set y.dr=1 where y.pk_corp = ? and y.pk_app_billapply = ? ";
            String sqlB = "update ynt_app_billapply_detail y set y.dr=1 where y.pk_corp = ? and y.pk_app_billapply = ? ";

            SQLParameter sp = new SQLParameter();
            sp.addParam(vo.getPk_corp());
            sp.addParam(vo.getPk_app_billapply());

            singleObjectBO.executeUpdate(sqlA, sp);
            singleObjectBO.executeUpdate(sqlB, sp);
        }catch (Exception e){
            flag = false;
            log.error("错误", e.getMessage());
        }

        return flag;
    }
}
