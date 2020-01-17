package com.dzf.zxkj.platform.service.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IUserService {
    CorpVO[] getValidateCorpByUserId(String dsName, String userID)
            throws DZFWarpException;

    // 保存
    UserVO save(UserVO vo) throws DZFWarpException;

    // 保存
    void update(UserVO vo) throws DZFWarpException;

    /**
     * 初次登录时，修改密码
     *
     * @param vo
     * @throws DZFWarpException
     * @author gejw
     * @time 下午7:59:58
     */
    void updateLoginPwd(UserVO vo) throws DZFWarpException;

    //验证时查询
    UserVO queryUserById(String id) throws DZFWarpException;

    /**
     * 锁定、解锁用户
     *
     * @param vo
     * @throws DZFWarpException
     */
    void updateLock(UserVO vo) throws DZFWarpException;

    // 登录
    UserVO login(UserVO user) throws DZFWarpException;

    // 登录
    UserVO loginByCode(String usercode) throws DZFWarpException;

    // 登录日志
    void loginLog(LoginLogVo loginLogVo) throws DZFWarpException;

    // 登录日志
//	public void logoutLog(LoginLogVo loginLogVo) throws DZFWarpException;

    // 查询有权限的公司
    List<CorpVO> querypowercorp(String userid) throws DZFWarpException;

    // 查询有权限的公司:在线会计登录使用
    List<CorpVO> queryPowerCorpKj(String userid) throws DZFWarpException;

    // 查询有权限的公司:管理端使用
    List<CorpVO> queryPowerCorpAdmin(String userid) throws DZFWarpException;

    // 查询有权限的公司:管理端使用
    Set<String> queryPowerCorpAdSet(String userid) throws DZFWarpException;

    // 查询有权限的公司
    Set<String> querypowercorpSet(String userid) throws DZFWarpException;

    // 查询公司
    CorpVO querypowercorpById(String id) throws DZFWarpException;

    UserVO queryByCode(String pkCorp, String usercode) throws DZFWarpException;

    // 删除
    void delete(UserVO vo) throws DZFWarpException;

    List<UserVO> query(String pk_corp, SysPowerConditVO qryVO) throws DZFWarpException;

    // 校验用户名或编码是否存在
    boolean exist(UserVO vo) throws DZFWarpException;

    //用户口令修改密码
    int updatePsw(UserVO uvo, String psw) throws DZFWarpException;

    // 查询授权可访问的页面
    SysFunNodeVO[] getAuthAccessPage(UserVO user, CorpVO corp, String path) throws DZFWarpException;

    List<UserVO> queryOwner(String pk_corp, SysPowerConditVO qryVO) throws DZFWarpException;

    /**
     * 批量指定部门
     *
     * @param vos
     * @throws DZFWarpException
     */
    void updateDept(UserVO[] vos) throws DZFWarpException;

    //企业主用户登录
    UserVO loginByOwner(String usercode) throws DZFWarpException;

    //企业主有权限的公司
    List<CorpVO> queryPowerCorpByOwner(String userid) throws DZFWarpException;

    Set<String> queryPowerCorpOwnerSet(String userid) throws DZFWarpException;

    //发送手机验证码
    int sendVerify(String phone, String url) throws DZFWarpException;

    /**
     * 更新登录标记
     *
     * @param
     * @throws DZFWarpException
     */
    void updateLoginFlag(UserVO vo) throws DZFWarpException;

    //网站登录后直接登录企业主调用，用临时用户主键查正式用户
    UserVO queryOwnerByTempUser(String pk_temp_user) throws DZFWarpException;

    //企业主修改密码同步修改网站用户表密码
    void updateOwnerPass(UserVO vo) throws DZFWarpException;

    UserToCorp[] queryCustMngUsers(String pk_corp) throws DZFWarpException;

    /**
     * 会计端查询用户有权限公司最后期间损益期间 加盟商查最后结账
     *
     * @param userid
     * @return
     * @throws DZFWarpException
     */
    Map<String, String> queryCorpSyByUser(String userid, List<CorpVO> list) throws DZFWarpException;

    /**
     * 导入保存
     *
     * @param
     * @param
     * @throws DZFWarpException
     */
    List<UserVO> onImportSave(UserVO[] uvos, CorpVO cvo, String loginUserid) throws DZFWarpException;

    /**
     * 保存导入日志
     *
     * @param uvos
     * @throws DZFWarpException
     */
    void saveErrorInfo(ImpErrorVO[] uvos, String pk_corp) throws DZFWarpException;

    /**
     * 查询导入日志
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    ImpErrorVO[] queryErrorInfo(String pk_corp) throws DZFWarpException;

    /**
     * 密码策略天数
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    MsgremindsetVO queryPwdStrategy(String pk_corp) throws DZFWarpException;

    /**
     * 查询关联VO
     *
     * @param kryuserid
     * @param kryshopid
     * @return
     * @throws DZFWarpException
     */
    KryDZFRelationVO queryKryDZFRelationVO(String kryuserid, String kryshopid) throws DZFWarpException;

    /**
     * 检查是否有未完成的业务
     *
     * @return
     * @throws DZFWarpException
     * @author gejw
     * @time 上午11:01:27
     */
    boolean checkBusiness(String cuid, String pk_corp) throws DZFWarpException;


    /**
     * 查询得到用户名UserVO，名称已解密，设置密码为空
     *
     * @param userid
     */
    UserVO queryUserJmVOByID(String userid) throws DZFWarpException;

    /**
     * 根据代账公司ID查询所有用户
     *
     * @param pk_corp 代账公司ID
     * @param isDel   是否查询已删除用户，ture-查询；false-不查询
     * @return
     * @throws DZFWarpException
     * @author gejw
     * @time 上午9:23:51
     */
    HashMap<String, UserVO> queryUserMap(String pk_corp, boolean isDel) throws DZFWarpException;

    /**
     * 根据用户校验小客户是否有权限
     * @param corpList 小客户list
     * @param userid 用户id
     * @return
     * @throws DZFWarpException
     */
    boolean isExistCorpPower (List<String> corpList,String userid) throws DZFWarpException;
}
