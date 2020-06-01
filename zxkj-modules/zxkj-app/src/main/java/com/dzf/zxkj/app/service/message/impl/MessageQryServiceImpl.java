package com.dzf.zxkj.app.service.message.impl;

import com.dzf.zxkj.app.model.nssb.NssbBean;
import com.dzf.zxkj.app.model.resp.bean.MessageResponVo;
import com.dzf.zxkj.app.model.resp.bean.MessageTypeResponVo;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.model.version.VersionTipsVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.IVersionConstant;
import com.dzf.zxkj.app.pub.constant.MsgtypeEnum;
import com.dzf.zxkj.app.service.message.IMessageQryService;
import com.dzf.zxkj.app.service.pub.IAppPubservice;
import com.dzf.zxkj.app.utils.UserUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.DataSourceFactory;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.message.MsgAdminVO;
import com.dzf.zxkj.platform.model.message.MsgSysVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.workbench.BsWorkbenchVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("ms321service")
public class MessageQryServiceImpl implements IMessageQryService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private IAppPubservice apppubservice;

    @Override
    public ResponseBaseBeanVO getAllMessage(UserBeanVO userBean) throws DZFWarpException {
        ResponseBaseBeanVO resbean = new ResponseBaseBeanVO();
        try {
            List<MessageTypeResponVo> listtype = new ArrayList<MessageTypeResponVo>();

            List<MessageResponVo> resmsglist = getMsgRespon(userBean.getPk_corp(), userBean.getAccount_id(),
                    userBean.getPk_tempcorp());

            if (resmsglist != null && resmsglist.size() > 0) {
                // 类型分组
                Map<Integer, List<MessageResponVo>> typemap = new HashMap<Integer, List<MessageResponVo>>();

                List<Integer> sortlist = new ArrayList<Integer>();

                Map<Integer, String[]> typemsg = new HashMap<Integer, String[]>();

                // 根据类型分组显示
                getGroupMsgMap(resmsglist, typemap, sortlist, typemsg);

                // 设置每组的数量，最新消息
                if (typemap.size() > 0) {
                    for (Integer typecode : sortlist) {
                        MessageTypeResponVo typevo = new MessageTypeResponVo();
                        List<MessageResponVo> entry = typemap.get(typecode);
                        Integer msgtype = typecode;
                        String msgname = entry.get(0).getMsgname();
                        typevo.setMsgtype(msgtype);
                        typevo.setMsgtypename(msgname);
                        int uncount = 0;
                        for (MessageResponVo vo : entry) {
                            if (vo.getIsread() == null || !vo.getIsread().booleanValue()) {
                                uncount++;
                            }
                        }
                        typevo.setUnrcount(uncount);
                        String[] msg = typemsg.get(typecode);
                        if (msg != null && msg.length > 0) {
                            String newmsg = msg[0];
                            if (MsgtypeEnum.MSG_TYPE_GZRB.getValue().intValue() == typevo.getMsgtype()) {
                                UserVO uvo = (UserVO) singleObjectBO.queryByPrimaryKey(UserVO.class, entry.get(0).getSendname());// UserCache.getInstance().get(entry.get(0).getSendname(), "");
                                uvo = UserUtil.getUservo(uvo);
                                if (uvo != null) {
                                    newmsg = uvo.getUser_name() + newmsg;
                                }
                            }
                            typevo.setNewmsg(newmsg);
                            if (!StringUtil.isEmpty(msg[1])) {
                                typevo.setLastdate(String.valueOf(new DZFDateTime(msg[1]).getMillis()));
                            }
                        }

                        listtype.add(typevo);
                    }
                }
            }

            if (listtype.size() == 0) {
                throw new BusinessException("消息列表为空!");
            }
            resbean.setRescode(IConstant.DEFAULT);
            resbean.setResmsg(listtype);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            resbean.setRescode(IConstant.FIRDES);
            resbean.setResmsg("消息列表为空!");
        }
        return resbean;
    }

    @Override
    public ResponseBaseBeanVO getTypeMessage(UserBeanVO userBean) throws DZFWarpException {
        ResponseBaseBeanVO resbean = new ResponseBaseBeanVO();
        try {
            if (userBean == null || StringUtil.isEmpty(userBean.getMsgtype())) {
                throw new BusinessException("消息类型为空!");
            }
            int page = userBean == null ?1: userBean.getPage();
            int rows = userBean ==null? 100000: userBean.getRows();

            if(page == 0){
                page = 1;//
            }

            List<MessageResponVo> listtype = new ArrayList<MessageResponVo>();

            // 系统公告,代账机构信息
            putDzjgOrJituanType(listtype, userBean.getMsgtype());
            // 管理端消息
            putadminmsgType(listtype, userBean.getPk_corp(), userBean.getAccount_id(),userBean.getPk_tempcorp(), userBean.getMsgtype());
            if (listtype.size() == 0) {
                throw new BusinessException("消息列表为空!");
            }


            List<MessageResponVo> restype = new ArrayList<MessageResponVo>();

            int start= (page-1)*rows;
            for(int i=start;i<page*rows && i<listtype.size();i++){
                restype.add(listtype.get(i));
            }

            //处理管理端提醒消息
            handlerAdminMsg(restype,userBean.getMsgtype());

            resbean.setRescode(IConstant.DEFAULT);
            resbean.setResmsg(restype);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            resbean.setRescode(IConstant.FIRDES);
            resbean.setResmsg("消息列表为空!");
        }
        return resbean;
    }

    private Integer getHjLx() {
        Integer hb = Integer.parseInt(MsgtypeEnum.MSG_TYPE_YJTX.getValue().intValue() + ""
                + MsgtypeEnum.MSG_TYPE_SJTX.getValue().intValue());
        return hb;
    }

    private void handlerAdminMsg(List<MessageResponVo> restype, String msgtypestr) {
        // 提醒消息处理
        Integer msgtype = Integer.parseInt(msgtypestr);
        if (MsgtypeEnum.MSG_TYPE_SPTX.getValue().intValue() == msgtype.intValue()// 送票提醒
                || MsgtypeEnum.MSG_TYPE_CSTX.getValue().intValue() == msgtype.intValue()// 抄税提醒
                || MsgtypeEnum.MSG_TYPE_QKTX.getValue().intValue() == msgtype.intValue()// 清卡提醒
                || MsgtypeEnum.MSG_TYPE_JJPZTX.getValue().intValue() == msgtype.intValue()
                || MsgtypeEnum.MSG_TYPE_YJTX.getValue().intValue() == msgtype.intValue()
                || getHjLx().intValue() ==  msgtype.intValue()
        ) {// 凭证交接
            Map<String,MessageResponVo> b_map = new HashMap<String,MessageResponVo>();
            String pk_corp= "";
            String period = "";
            String type  = "";
            MessageResponVo value = null;
            SQLParameter sp = new SQLParameter();
            for(MessageResponVo vo : restype){
                pk_corp =  vo.getPk_corpk();
                period = vo.getPeriod();
                type = vo.getMsgtype()+"";
                vo.setMsg_complete(IConstant.FIRDES);//消息未完成
                if(b_map.containsKey(pk_corp+period+type)){
                    value = b_map.get(pk_corp+period+type);
                    vo.setMsg_complete(value.getMsg_complete());
                    vo.setMsg_yj_zt(value.getMsg_yj_zt());
                }else{
                    sp.clearParams();
                    String qrysql = "select * from nsworkbench where nvl(dr,0)=0 and pk_corp = ? and period = ? ";
                    sp.addParam(pk_corp);
                    sp.addParam(period);
                    List<BsWorkbenchVO> corpmsglist =  (List<BsWorkbenchVO>) singleObjectBO.executeQuery(qrysql, sp, new BeanListProcessor(BsWorkbenchVO.class));
                    DZFBoolean iscomplete = DZFBoolean.FALSE;//是否已经完成
                    if(corpmsglist!=null && corpmsglist.size()>0){
                        if(vo.getMsgtype() == MsgtypeEnum.MSG_TYPE_SPTX.getValue().intValue()
                                && corpmsglist.get(0).getIsptx() != null && corpmsglist.get(0).getIsptx().intValue() == 1 ){
                            vo.setMsg_complete(IConstant.DEFAULT);//消息已完成
                            iscomplete = DZFBoolean.TRUE;
                        }else if(vo.getMsgtype() == MsgtypeEnum.MSG_TYPE_CSTX.getValue().intValue()
                                && corpmsglist.get(0).getTaxStateCopy() != null && corpmsglist.get(0).getTaxStateCopy().intValue() == 1){//抄税
                            vo.setMsg_complete(IConstant.DEFAULT);//消息已完成
                            iscomplete = DZFBoolean.TRUE;
                        }else if(vo.getMsgtype() == MsgtypeEnum.MSG_TYPE_QKTX.getValue().intValue()
                                && corpmsglist.get(0).getTaxStateClean() != null && corpmsglist.get(0).getTaxStateClean().intValue() == 1){//清卡
                            vo.setMsg_complete(IConstant.DEFAULT);//消息已完成
                            iscomplete = DZFBoolean.TRUE;
                        }else if(vo.getMsgtype() == MsgtypeEnum.MSG_TYPE_JJPZTX.getValue().intValue()
                                && corpmsglist.get(0).getIpzjjzt() != null && corpmsglist.get(0).getIpzjjzt().intValue() == 1){//凭证交接
                            vo.setMsg_complete(IConstant.DEFAULT);//消息已完成
                            iscomplete = DZFBoolean.TRUE;
                        }else if(vo.getMsgtype() ==  MsgtypeEnum.MSG_TYPE_YJTX.getValue().intValue()  ){
                            if(corpmsglist.get(0).getItaxconfstate()== null || corpmsglist.get(0).getItaxconfstate().intValue() ==0 ){
                                vo.setMsg_yj_zt("2");//待确认
                            }else if( corpmsglist.get(0).getItaxconfstate().intValue() ==1 ){
                                vo.setMsg_yj_zt("0");//已同意
                            }else if( corpmsglist.get(0).getItaxconfstate().intValue() ==2){
                                vo.setMsg_yj_zt("1");//不同意
                            }
                        }
                    }
                    b_map.put(pk_corp+period+type, vo);
                }
            }
        }else if(MsgtypeEnum.MSG_TYPE_GZRB.getValue().intValue() == msgtype.intValue()){//工作日报
            for(MessageResponVo vo : restype){
                if(!StringUtil.isEmpty(vo.getSendname())){
                    UserVO uvo = (UserVO) singleObjectBO.queryByPrimaryKey(UserVO.class, vo.getSendname());
                    uvo = UserUtil.getUservo(uvo);
                    if(uvo!=null){
                        vo.setSendname(uvo.getUser_name());
                    }
                }
            }
        }else if(MsgtypeEnum.MSG_TYPE_FWPJ.getValue().intValue() == msgtype.intValue()){//服务评价
            for(MessageResponVo vo : restype){
                if (!StringUtil.isEmpty(vo.getSourid()) && !StringUtil.isEmpty(vo.getPk_corp())){
                    //看该来源的信息是否已经存在
                    SQLParameter sp = new SQLParameter();
                    sp.addParam(vo.getPk_corpk());
                    sp.addParam(vo.getSourid());
                    boolean hasfwpj = singleObjectBO.isExists(vo.getPk_corpk(),
                            " select 1 from app_collabtevalt where pk_corp=? and pk_bill=? and nvl(dr,0)=0 ", sp);
                    if(hasfwpj){
                        vo.setFwpj_zt("0");
                    }else{
                        vo.setFwpj_zt("1");
                    }
                }
            }
        }
    }

    private String getNewAdminSql(String pk_corp, String account_id, String pk_tempcorp, SQLParameter sp,
                                  Integer msgtype, String unread) {
        StringBuffer qrysql = new StringBuffer();
        qrysql.append(" select  * from ( ");
        qrysql.append(" select ya.pk_message as id ,ya.vtitle as title, ");//管理端消息
        qrysql.append(" vcontent as content,vsenddate as vdate,isread , msgtype,   ");
        qrysql.append(" msgtypename as msgname,  ");
        qrysql.append(" case msgtype when 30 then pk_bill when 31 then pk_bill when 36 then pk_bill else '' end as imgpk , ");
        qrysql.append(" pk_bill as sourid,ya.vperiod as period,ya.pk_corpk as pk_corpk ,to_char(ya.istatus) as istatus , ");
        qrysql.append(" ya.sendman as sendname, ");
        qrysql.append(" ya.vbusiname as vbusiname, ya.pk_corp as pk_corp ");
        qrysql.append(" from ynt_msg_admin ya ");
        qrysql.append(" where nvl(ya.dr,0)=0  ");
        qrysql.append(" and ( ya.pk_corpk = ? or  ya.pk_temp_corp = ? or msgtype ='46' ) ");
        sp.addParam( pk_corp );
        sp.addParam(pk_tempcorp);;
        qrysql.append(" and  ya.cuserid = ?  ");
        sp.addParam(account_id);
        if(msgtype!=null){
            Integer hb = getHjLx();
            if(msgtype.intValue() == hb.intValue()){
                qrysql.append(" and ya.msgtype in(?,?) ");
                sp.addParam(MsgtypeEnum.MSG_TYPE_YJTX.getValue().intValue());
                sp.addParam(MsgtypeEnum.MSG_TYPE_SJTX.getValue().intValue());
            }else{
                qrysql.append(" and ya.msgtype = ?  ");
                sp.addParam(msgtype);
            }
        }
        if(unread!=null){
            qrysql.append(" and  nvl(ya.isread,'N') =? ");
            sp.addParam(unread);
        }
        Integer fkqy = apppubservice.qryParamValue(pk_corp, "fkqy001");
        if(fkqy==null || fkqy.intValue() ==1){//不显示31（图片制证）的数据
            qrysql.append(" and ya.msgtype in ('1','21','22','18','19','16','17','20','24','30','33','34','36','37','38','39','40','41','46','48','51','52','54','55','59','60','61') ");
        }else{
            qrysql.append(" and ya.msgtype in ('1','21','22','18','19','16','17','20','24','30','31','33','34','36','37','38','39','40','41','46','48','51','52','54','55','59','60','61') ");
        }
        qrysql.append(" ) tbb ");
        return qrysql.toString();
    }

    private String getAdminMsgSql(String pk_corp, String account_id, String pk_tempcorp,
                                  SQLParameter sp,Integer msgtype,String unread) {
        return getNewAdminSql(pk_corp, account_id, pk_tempcorp, sp, msgtype, unread);
    }

    private List<MessageResponVo> getMsgAdminMsg(String pk_corp,String pk_tempcorp,String account_id, Integer msgtype,String unread) {
        StringBuffer qrysql = new StringBuffer();
        SQLParameter sp = new SQLParameter();

        qrysql.append( getAdminMsgSql(pk_corp, account_id, pk_tempcorp, sp,msgtype,unread));
        qrysql.append("   order by tbb.vdate desc,tbb.msgtype  ");

        List<MessageResponVo> adminlist = (List<MessageResponVo>) singleObjectBO.executeQuery(qrysql.toString(), sp,
                new BeanListProcessor(MessageResponVo.class));
        return adminlist;
    }

    private void putadminmsgType(List<MessageResponVo> listtype, String pk_corp, String account_id,String pk_tempcorp, String msgtype) {
        if ((StringUtil.isEmpty(pk_corp) || Common.tempidcreate.equals(pk_corp))
                && (StringUtil.isEmpty(pk_tempcorp))
        ) {
            return;
        }

        if (StringUtil.isEmpty(account_id)) {
            return;
        }

        List<MessageResponVo>  listtyperes = getMsgAdminMsg(pk_corp,pk_tempcorp ,account_id,Integer.parseInt(msgtype),null);

        if(listtyperes !=null && listtyperes.size()>0){
            List<String> uplist = new ArrayList<String>();

            for(MessageResponVo temp:listtyperes){
                if(temp.getIsread()==null || !temp.getIsread().booleanValue()){
                    uplist.add(temp.getId());
                }
                listtype.add(temp);
            }

            if(uplist.size()>0){
                String upsql = "update ynt_msg_admin set isread= 'Y' where nvl(dr,0)=0 and nvl(isread,'N')='N' and "+ SqlUtil.buildSqlForIn("pk_message", uplist.toArray(new String[0]));
                singleObjectBO.executeUpdate(upsql, new SQLParameter());
            }
        }


    }

    private void putDzjgOrJituanType(List<MessageResponVo> listtype, String type) {
        List<MsgSysVO> msglist = getMsgSysVo(type);// 系统和代账公司公告

        if (msglist != null && msglist.size() > 0) {
            MessageResponVo msgbean = null;
            for (MsgSysVO sysvo : msglist) {
                msgbean = new MessageResponVo();
                msgbean.setId(sysvo.getPrimaryKey());
                msgbean.setTitle(sysvo.getVtitle());
                msgbean.setContent(sysvo.getVcontent());
                msgbean.setVdate(sysvo.getVsenddate());
                listtype.add(msgbean);
            }
        }
    }


    /**
     * 系统消息的sql
     *
     * @return
     */
    private List<MsgSysVO> getMsgSysVo(String send) {
        SQLParameter sp = new SQLParameter();
        StringBuffer qrysql = new StringBuffer();
        qrysql.append("  select * from ynt_msg_sys    ");
        qrysql.append("  where nvl(dr,0)=0 and sys_receive= ? ");
        sp.addParam(ISysConstants.DZF_APP);
        qrysql.append(" and msgtype = ?  ");
        sp.addParam(Integer.parseInt(send));
        qrysql.append("  order by vsenddate desc , sys_send  ");

        List<MsgSysVO> adminmsglist = (List<MsgSysVO>) singleObjectBO.executeQuery(qrysql.toString(), sp,
                new BeanListProcessor(MsgSysVO.class));
        return adminmsglist;
    }

    @Override
    public Integer getAllMessageUnRead(String pk_corp, String pk_tempcorp, String account_id) throws DZFWarpException {
        List<MessageResponVo> respons = getMsgAdminMsg(pk_corp, pk_tempcorp, account_id, null,"N");
        return respons.size();
    }

    private ResponseBaseBeanVO getMessage1(UserBeanVO userBean) {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
        try {
            SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, userBean.getPk_corp()));
            String qry = "select * from sys_version_tips  where  nproject=3 and nvl(dr,0) = 0 order by ts desc ";
            List<VersionTipsVO> megVOs = (ArrayList) sbo.executeQuery(qry, new SQLParameter(),
                    new BeanListProcessor(VersionTipsVO.class));
            if (megVOs != null && megVOs.size() > 0) {
                bean.setRescode(IConstant.DEFAULT);
                List<MessageResponVo> msgLs = new ArrayList<MessageResponVo>();
                MessageResponVo msgbean = null;
                for (int i = 0; i < megVOs.size(); i++) {
                    msgbean = new MessageResponVo();
                    msgbean.setId(megVOs.get(i).getPrimaryKey());
                    msgbean.setTitle(megVOs.get(i).getVsystips());
                    msgbean.setContent(megVOs.get(i).getVmemo());
                    msgbean.setTs(megVOs.get(i).getTs().toString());
                    msgLs.add(msgbean);
                }
                bean.setResmsg(msgLs.toArray(new MessageResponVo[msgLs.size()]));
            }else{
                bean.setRescode(IConstant.FIRDES);
                bean.setResmsg("暂无系统公告!");
            }
        } catch (Exception e1) {
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("获取公告失败：" + e1.getMessage());
            log.error("错误",e1);
        }
        return bean;
    }

    @Override
    public ResponseBaseBeanVO getMessage(UserBeanVO userBean) throws DZFWarpException {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
        if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1 &&
                userBean.getVersionno().intValue()<IVersionConstant.VERSIONNO321) {// 第一版走的代码
            bean = getMessage1(userBean);
        }
        return bean;
    }

    @Override
    public ResponseBaseBeanVO getYJNssb(String id) throws DZFWarpException {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
        //根据id查询数据
        MsgAdminVO adminvo = (MsgAdminVO) singleObjectBO.queryByPrimaryKey(MsgAdminVO.class, id);

        if(adminvo == null){
            throw new BusinessException("信息为空");
        }

        String pk_corp = adminvo.getPk_corpk();

        String period = adminvo.getVperiod();

        if(StringUtil.isEmpty(pk_corp) || StringUtil.isEmpty(period)){
            throw new BusinessException("消息内容出错");
        }

        NssbBean nssbbean = null;
//        if(adminvo.getMsgtype().intValue() == MsgtypeEnum.MSG_TYPE_YJTX.getValue().intValue()){//应缴消息
//            nssbbean = orgreport1.getYjNsData(pk_corp, period);
//        }else{//实缴消息
//            nssbbean= orgreport1.qryNssbOnePeriod(pk_corp, period);
//        }

        bean.setRescode(IConstant.DEFAULT);
        bean.setResmsg(nssbbean);
        return bean;
    }

    @Override
    public List<MessageResponVo> getMsgRespon(String pk_corp, String account_id, String pk_tempcorp) throws DZFWarpException {
        StringBuffer qrysql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        qrysql.append(" select * from ( ");
        qrysql.append("   select  pk_message as id,vtitle as title,    ");
        qrysql.append("    vcontent as content,vsenddate as vdate,'Y' as isread,  ");
        qrysql.append("    case  sys_send  when 'sys_dzf' then 0 when  'admin_kj' then 20 end as msgtype , ");
        qrysql.append("    case  sys_send  when 'sys_dzf' then '系统公告' when  'admin_kj' then '代账机构消息' end as msgname,'' as imgpk ,'' as sourid,'' as period ,'' as pk_corpk, '' as istatus,'' as sendname,'' as vbusiname,pk_corp ");
        qrysql.append("   from ynt_msg_sys ");
        qrysql.append("  where nvl(dr,0)=0 and sys_receive= ? ");
        sp.addParam(ISysConstants.DZF_APP);
        qrysql.append("  union all ");
        qrysql.append(getAdminMsgSql(pk_corp, account_id, pk_tempcorp, sp,null,null));
        qrysql.append(" ");
        qrysql.append(" ) tt ");
        qrysql.append("  order by tt.vdate desc ");
        List<MessageResponVo> reslist = (List<MessageResponVo>) singleObjectBO.executeQuery(qrysql.toString(),
                sp, new BeanListProcessor(MessageResponVo.class));
        return reslist;
    }


    private void getGroupMsgMap(List<MessageResponVo> resmsglist, Map<Integer, List<MessageResponVo>> typemap,
                                List<Integer> sortlist, Map<Integer, String[]> typemsg) {
        for (MessageResponVo adminvo : resmsglist) {
            if (adminvo.getMsgtype().intValue() == MsgtypeEnum.MSG_TYPE_YJTX.getValue().intValue()
                    || adminvo.getMsgtype().intValue() == MsgtypeEnum.MSG_TYPE_SJTX.getValue().intValue()) {
                Integer hb = Integer.parseInt(MsgtypeEnum.MSG_TYPE_YJTX.getValue().intValue() + ""
                        + MsgtypeEnum.MSG_TYPE_SJTX.getValue().intValue());
                adminvo.setMsgtype(hb);// 合并的值
                adminvo.setMsgname("税款通知");
            }

            if (!sortlist.contains(adminvo.getMsgtype())) {
                sortlist.add(adminvo.getMsgtype());
                if (adminvo.getMsgtype().intValue() == MsgtypeEnum.MSG_TYPE_DZFPTGG.getValue()) {
                    typemsg.put(adminvo.getMsgtype(), new String[] { adminvo.getTitle(), adminvo.getVdate() });// 大账房，取公告
                } else {
                    typemsg.put(adminvo.getMsgtype(), new String[] { adminvo.getContent(), adminvo.getVdate() });
                }
            }
            if (typemap.containsKey(adminvo.getMsgtype())) {
                typemap.get(adminvo.getMsgtype()).add(adminvo);
            } else {
                List<MessageResponVo> listtemp = new ArrayList<MessageResponVo>();
                listtemp.add(adminvo);
                typemap.put(adminvo.getMsgtype(), listtemp);
            }
        }
    }
}
