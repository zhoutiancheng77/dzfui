package com.dzf.zxkj.platform.service.message.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.enums.MsgtypeEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.message.MsgAdminVO;
import com.dzf.zxkj.platform.model.message.MsgSysVO;
import com.dzf.zxkj.platform.model.message.MsgTypeVO;
import com.dzf.zxkj.platform.service.message.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service("msgServiceImpl")
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public void saveAdminMsg(MsgAdminVO vo) throws DZFWarpException {
        singleObjectBO.saveObject(vo.getPk_corp(), vo);
    }

    @Override
    public void saveSysMsg(MsgSysVO sysvo) throws DZFWarpException {
        singleObjectBO.saveObject(sysvo.getPk_corp(), sysvo);
    }

    @Override
    public void deleteAdminMsg(MsgAdminVO vo) throws DZFWarpException {
        singleObjectBO.deleteObject(vo);
    }

    @Override
    public void deleteSysMsg(MsgSysVO sysvo) throws DZFWarpException {
        singleObjectBO.deleteObject(sysvo);
    }

    @Override
    public void updateAdminRead(MsgAdminVO[] vo) throws DZFWarpException {
        singleObjectBO.updateAry(vo, new String[]{"isread"});
    }

    @Override
    public List<MsgTypeVO> queryMsgType(String receiveUser, String receive_side)
            throws DZFWarpException {
        StringBuilder sb = new StringBuilder();
        SQLParameter sp = new SQLParameter();
        sb.append(" select msgtype as type_code, msgtypename as type_name, max(vsenddate) as latest_time from YNT_MSG_ADMIN")
                .append(" where cuserid = ? and nvl(dr, 0) = 0 ")
                .append(" group by msgtype, msgtypename union all ")
                .append(" select msgtype as type_code, msgtypename as type_name, max(vsenddate) as latest_time from YNT_MSG_SYS ")
                .append(" where sys_receive = ? and nvl(dr, 0) = 0 ")
                .append(" group by msgtype, msgtypename ");
        sp.addParam(receiveUser);
        sp.addParam(receive_side);
        List<MsgTypeVO> types = (List<MsgTypeVO>) singleObjectBO.executeQuery(sb.toString(),
                sp, new BeanListProcessor(MsgTypeVO.class));
        Collections.sort(types, new Comparator<MsgTypeVO>() {
            @Override
            public int compare(MsgTypeVO o1, MsgTypeVO o2) {
                return -o1.getLatest_time().compareTo(o2.getLatest_time());
            }
        });
        Map<Integer, Integer> countMap = countUnreadMsgBytype(receiveUser);
        for (MsgTypeVO msgTypeVO : types) {
            if (countMap.containsKey(msgTypeVO.getType_code())) {
                msgTypeVO.setNew_msg(countMap.get(msgTypeVO.getType_code()));
            }
        }
        return types;
    }

    @Override
    public List<MsgAdminVO> queryLatestAdminMsg(String receiveUser, String receive_side, int number)
            throws DZFWarpException {
        StringBuilder sb = new StringBuilder();
        SQLParameter sp = new SQLParameter();
        sb.append(" select * from (select pk_message, msgtype, msgtypename, vsenddate, vcontent, isread from YNT_MSG_ADMIN")
                .append(" where cuserid = ? ");
        sp.addParam(receiveUser);
        if (!StringUtil.isEmpty(receive_side)) {
            sb.append(" and sys_receive = ? ");
            sp.addParam(receive_side);
        }
        sb.append(" and nvl(dr, 0) = 0 and nvl(isread,'N') = 'N' order by vsenddate desc) where rownum <= ? ");
        sp.addParam(number);
        List<MsgAdminVO> latestMsgs = (List<MsgAdminVO>) singleObjectBO.executeQuery(sb.toString(),
                sp, new BeanListProcessor(MsgAdminVO.class));
        return latestMsgs;
    }

    @Override
    public List<MsgSysVO> queryLatestSysMsg(String receive_side, int number)
            throws DZFWarpException {
        StringBuilder sb = new StringBuilder();
        SQLParameter sp = new SQLParameter();
        sb.append(" select * from (select vtitle, msgtypename, vsenddate, vcontent, isread from YNT_MSG_SYS")
                .append(" where nvl(dr, 0) = 0 and sys_receive = ? order by vsenddate desc) where rownum <= ? ");
        sp.addParam(receive_side);
        sp.addParam(number);
        return (List<MsgSysVO>) singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(MsgSysVO.class));
    }

    @Override
    public int queryUnreadMsgNum(String receiveUser)
            throws DZFWarpException {
        StringBuilder sb = new StringBuilder();
        SQLParameter sp = new SQLParameter();
        sp.addParam(receiveUser);
        sb.append(" select count(1) from YNT_MSG_ADMIN")
                .append(" where cuserid = ? and nvl(isread, 'N') = 'N'")
                .append(" and nvl(dr, 0) = 0 ");
        BigDecimal dec = (BigDecimal) singleObjectBO.executeQuery(sb.toString(), sp, new ColumnProcessor());
        return dec.intValue();
    }

    private Map<Integer, Integer> countUnreadMsgBytype(String receiveUser) {
        Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
        StringBuilder sb = new StringBuilder();
        SQLParameter sp = new SQLParameter();
        sb.append(" select msgtype as type_code, count(1) as new_msg from YNT_MSG_ADMIN")
                .append(" where cuserid = ? and nvl(dr, 0) = 0 and nvl(isread, 'N') = 'N' ")
                .append(" group by msgtype");
        sp.addParam(receiveUser);
        List<MsgTypeVO> msgCount = (List<MsgTypeVO>) singleObjectBO.executeQuery(sb.toString(), sp,
                new BeanListProcessor(MsgTypeVO.class));
        for (MsgTypeVO msgTypeVO : msgCount) {
            countMap.put(msgTypeVO.getType_code(), msgTypeVO.getNew_msg());
        }
        return countMap;
    }

    @Override
    public MsgAdminVO[] query(String receiveUser, MsgAdminVO param)
            throws DZFWarpException {
        MsgAdminVO[] msgs = new MsgAdminVO[0];
        StringBuilder sb = new StringBuilder();
        SQLParameter sp = new SQLParameter();
        if (MsgtypeEnum.MSG_TYPE_DZFPTGG.getValue().equals(param.getMsgtype())) {
            sb.append(" select * from YNT_MSG_SYS where nvl(dr, 0) = 0 and sys_receive = ? ");
            sp.addParam(param.getSys_receive());
            if (!StringUtil.isEmpty(param.getVcontent())) {
                sb.append(" and vcontent like ? ");
                sp.addParam("%" + param.getVcontent() + "%");
            }
            if (!StringUtil.isEmpty(param.getBdate())) {
                sb.append(" and vsenddate >= ? ");
                sp.addParam(new DZFDateTime(param.getBdate()));
            }
            if (!StringUtil.isEmpty(param.getEdate())) {
                sb.append(" and vsenddate < ? ");
                Date edate = DateUtils.nextDay(DZFDate.getDate(param.getEdate()).toDate());
                sp.addParam(new DZFDateTime(edate));
            }
            sb.append(" order by vsenddate desc");
            List<MsgAdminVO> msgList = (List<MsgAdminVO>) singleObjectBO.executeQuery(sb.toString(),
                    sp, new BeanListProcessor(MsgAdminVO.class));
            msgs = msgList.toArray(msgs);
        } else {
            sb.append("nvl(dr, 0) = 0 and cuserid = ? ");
            sp.addParam(param.getCuserid());
            if (param.getMsgtype() != null) {
                sb.append(" and msgtype = ? ");
                sp.addParam(param.getMsgtype());
            }
            if (!StringUtil.isEmpty(param.getVcontent())) {
                sb.append(" and vcontent like ? ");
                sp.addParam("%" + param.getVcontent() + "%");
            }
            if (!StringUtil.isEmpty(param.getBdate())) {
                sb.append(" and vsenddate >= ? ");
                sp.addParam(new DZFDateTime(param.getBdate()));
            }
            if (!StringUtil.isEmpty(param.getEdate())) {
                sb.append(" and vsenddate < ? ");
                Date edate = DateUtils.nextDay(DZFDate.getDate(param.getEdate()).toDate());
                sp.addParam(new DZFDateTime(edate));
            }
            sb.append(" order by vsenddate desc");
            msgs = (MsgAdminVO[]) singleObjectBO.queryByCondition(MsgAdminVO.class, sb.toString(), sp);
        }
        return msgs;
    }


    @Override
    public void deleteAdminMsg(MsgAdminVO[] vos) throws DZFWarpException {
        for (MsgAdminVO vo : vos) {
            if (MsgtypeEnum.MSG_TYPE_DZFPTGG.getValue().equals(vo.getMsgtype()))
                throw new BusinessException("系统消息不允许删除！");
        }
        singleObjectBO.deleteVOArray(vos);
    }

    @Override
    public List<MsgSysVO> querySysMsgByDate(String receive_side, DZFDate currDate) throws DZFWarpException {
        StringBuilder sb = new StringBuilder();
        SQLParameter sp = new SQLParameter();
        sb.append(" select pk_message, vtitle, msgtypename, vsenddate, vcontent, isread from YNT_MSG_SYS")
                .append(" where nvl(dr, 0) = 0 and sys_receive = ? and substr(vsenddate,0,10) = ? and msgtype = 0 ");
        sp.addParam(receive_side);
        sp.addParam(currDate);
        return (List<MsgSysVO>) singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(MsgSysVO.class));
    }
}