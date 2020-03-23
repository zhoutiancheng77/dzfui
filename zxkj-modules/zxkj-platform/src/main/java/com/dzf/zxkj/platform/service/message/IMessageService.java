package com.dzf.zxkj.platform.service.message;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.platform.model.message.MsgAdminVO;
import com.dzf.zxkj.platform.model.message.MsgSysVO;
import com.dzf.zxkj.platform.model.message.MsgTypeVO;

import java.util.List;

public interface IMessageService {

    /**
     * 保存管理平台产生的消息
     *
     * @throws DZFWarpException
     */
    void saveAdminMsg(MsgAdminVO vo) throws DZFWarpException;

    /**
     * 保存系统公告、内部通知类消息
     *
     * @param sysvo
     * @throws DZFWarpException
     */
    void saveSysMsg(MsgSysVO sysvo) throws DZFWarpException;

    /**
     * 删除管理平台产生的消息
     *
     * @throws DZFWarpException
     */
    void deleteAdminMsg(MsgAdminVO vo) throws DZFWarpException;


    /**
     * 删除系统公告、内部通知类消息
     *
     * @param sysvo
     * @throws DZFWarpException
     */
    void deleteSysMsg(MsgSysVO sysvo) throws DZFWarpException;

    /**
     * 管理平台--标记已读
     *
     * @throws DZFWarpException
     */
    void updateAdminRead(MsgAdminVO[] vo) throws DZFWarpException;

    /**
     * 批量删除管理平台产生的消息
     *
     * @param vos
     * @throws DZFWarpException
     */
    void deleteAdminMsg(MsgAdminVO[] vos) throws DZFWarpException;

    /**
     * 查询发给用户的所有消息类型，包含每种消息未读消息数
     *
     * @param receiveUser
     * @param receive_side
     * @return
     * @throws DZFWarpException
     */
    List<MsgTypeVO> queryMsgType(String receiveUser, String receive_side) throws DZFWarpException;

    /**
     * 查询最新管理平台产生的消息
     *
     * @param receiveUser
     * @param receive_side
     * @param number
     * @return
     * @throws DZFWarpException
     */
    List<MsgAdminVO> queryLatestAdminMsg(String receiveUser, String receive_side, int number) throws DZFWarpException;

    /**
     * 查询最新公告
     *
     * @param receive_side
     * @param number
     * @return
     * @throws DZFWarpException
     */
    List<MsgSysVO> queryLatestSysMsg(String receive_side, int number) throws DZFWarpException;

    /**
     * 查询用户未读消息数
     *
     * @return
     * @throws DZFWarpException
     */
    int queryUnreadMsgNum(String receiveUser) throws DZFWarpException;

    /**
     * 根据用户、类别、时间、内容等查询
     *
     * @param receiveUser
     * @param param
     * @return
     * @throws DZFWarpException
     */
    List<MsgAdminVO> query(String receiveUser, MsgAdminVO param) throws DZFWarpException;

    /**
     * 按条件查询并分页
     *
     * @param receiveUser
     * @param param
     * @return
     * @throws DZFWarpException
     */
    QueryPageVO queryByPage(String receiveUser, MsgAdminVO param) throws DZFWarpException;

    /**
     * 查询当天公告
     *
     * @param receive_side
     * @param currDate
     * @return
     * @throws DZFWarpException
     */
    List<MsgSysVO> querySysMsgByDate(String receive_side, DZFDate currDate) throws DZFWarpException;

}
