package com.dzf.zxkj.platform.service.message.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.common.enums.MsgtypeEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.message.MsgAdminVO;
import com.dzf.zxkj.platform.model.message.MsgSysVO;
import com.dzf.zxkj.platform.model.message.MsgTypeVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.service.message.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
        //奇怪的地方：判断大账房平台公告和系统升级的先后顺序的时候是按各自消息分别判断，查询其中消息的时候却是全部都查
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

    /**
     * 拼接查询条件，得到sql和SQLParameter
     * @param param
     * @param sp
     */
    private String getQrySql(MsgAdminVO param, SQLParameter sp) {
        StringBuilder sb = new StringBuilder();
        if (!MsgtypeEnum.MSG_TYPE_DZFPTGG.getValue().equals(param.getMsgtype())) {
            sb.append(" select * from YNT_MSG_ADMIN where nvl(dr,0) = 0 and cuserid = ? ");
            sp.addParam(param.getCuserid());
            if (param.getMsgtype() != null) {
                sb.append(" and msgtype = ? ");
                sp.addParam(param.getMsgtype());
            }
        } else {
            sb.append(" select * from YNT_MSG_SYS where nvl(dr,0) = 0 and sys_receive = ? ");
            sp.addParam(param.getSys_receive());
            //llh：消息类型=系统升级时只查系统升级的消息；消息类型=大账房平台公告时，同时查询公告和系统升级消息
            if (param.getMsgtypename() != null && !param.getMsgtypename().equals("大账房平台公告")) { //系统升级
                sb.append(" and msgtypename = ? ");
                sp.addParam(param.getMsgtypename());
            }
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
        return sb.toString();
    }

    @Override
    public List<MsgAdminVO> query(String receiveUser, MsgAdminVO param)
            throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        String sql = getQrySql(param, sp);

        List<MsgAdminVO> list = (List<MsgAdminVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(MsgAdminVO.class));
        return list;
    }

    @Override
    public QueryPageVO queryByPage(String receiveUser, MsgAdminVO param)
            throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        String sql = getQrySql(param, sp);
        //查询总记录数
        int total = singleObjectBO.getTotalRow(sql, sp);
        //查询分页数据
        List<MsgAdminVO> list = (List<MsgAdminVO>)executeQueryByPage(sql, sp, param.getPage(), param.getRows(), new BeanListProcessor(MsgAdminVO.class));
        QueryPageVO pageVO = new QueryPageVO();
        pageVO.setTotal(total);
        pageVO.setPagevos(list.toArray(new MsgAdminVO[0]));
        return pageVO;
    }

    /**
     * 对查询sql语句执行分页查询
     * @param sql 一条完整的sql查询语句，可带条件和排序，如："select.. where.. order by.."
     * @param sp
     * @param pageNo
     * @param pageSize
     * @param processor
     * @return
     */
    public Object executeQueryByPage(String sql, SQLParameter sp, int pageNo, int pageSize, ResultSetProcessor processor) {
        StringBuffer sb = new StringBuffer();
        sb.append("select * from (select rownum rn, tt.* from (");
        sb.append(sql);
        sb.append(") tt ");
        sb.append("where rownum <= " + pageNo * pageSize);
        sb.append(") where rn > " + (pageNo - 1) * pageSize);
        return singleObjectBO.executeQuery(sb.toString(), sp, processor);
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
