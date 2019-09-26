package com.dzf.zxkj.platform.model.sys;

//import javax.validation.ValidationException;

import com.dzf.zxkj.base.model.SuperVO;

/**
 * 用户委派角色VO,主要包括用户id、角色主键、登录公司.
 * <p>
 */
public class UserRoleVO extends SuperVO {

    public String pk_user_role;
    public String cuserid;
    public String pk_corp;
    public String pk_role;
//    public String source; ///以前是给[会计工厂业务确认]那里，赋值 HP140,,IBillTypeCode.HP140然后，校验权限用的。com.dzf.service.sys.factory.impl.FactoryServiceImpl

    public String role_code;// 角色编码，不存库，暂时加盟商使用

    public String getRole_code() {
        return role_code;
    }

    public void setRole_code(String role_code) {
        this.role_code = role_code;
    }

    /**
     * 属性cuserid的Getter方法.
     *
     * 创建日期:(2005-9-21)
     * 
     * @return String
     */
    public String getCuserid() {
        return cuserid;
    }

    /**
     * 属性pk_corp的Getter方法.
     *
     * 创建日期:(2005-9-21)
     * 
     * @return String
     */
    public String getPk_corp() {
        return pk_corp;
    }

    /**
     * 属性pk_role的Getter方法.
     *
     * 创建日期:(2005-9-21)
     * 
     * @return String
     */
    public String getPk_role() {
        return pk_role;
    }

    /**
     * 属性cuserid的setter方法.
     *
     * 创建日期:(2005-9-21)
     * 
     * @param newCuserid
     *            String
     */
    public void setCuserid(String newCuserid) {

        cuserid = newCuserid;
    }

    /**
     * 属性pk_corp的setter方法.
     *
     * 创建日期:(2005-9-21)
     * 
     * @param newPk_corp
     *            String
     */
    public void setPk_corp(String newPk_corp) {

        pk_corp = newPk_corp;
    }

    /**
     * 属性pk_role的setter方法.
     *
     * 创建日期:(2005-9-21)
     * 
     * @param newPk_role
     *            String
     */
    public void setPk_role(String newPk_role) {

        pk_role = newPk_role;
    }

    /**
     * 验证对象各属性之间的数据逻辑正确性.
     *
     * 创建日期:(2005-9-21)
     * 
     * @exception nc.vo.pub.ValidationException
     *                如果验证失败,抛出 ValidationException,对错误进行解释.
     */
    // public void validate() throws ValidationException {
    // }
    /**
     * <p>
     * 取得父VO主键字段.
     * <p>
     * 创建日期:(2005-9-21)
     * 
     * @return java.lang.String
     */
    public String getParentPKFieldName() {

        return null;
    }

    /**
     * <p>
     * 取得表主键.
     * <p>
     * 创建日期:(2005-9-21)
     *
     * @return java.lang.String
     */
    public String getPKFieldName() {

        return "pk_user_role";
    }

    /**
     * <p>
     * 返回表名称.
     * <p>
     * 创建日期:(2005-9-21)
     *
     * @return java.lang.String
     */
    public String getTableName() {

        return "sm_user_role";
    }

    /**
     * 使用主键字段进行初始化的构造子.
     *
     * 创建日期:(2005-9-21)
     */
    public UserRoleVO() {
        super();
    }

    /**
     * 使用主键进行初始化的构造子.
     *
     * 创建日期:(2005-9-21)
     * 
     * @param Pk_user_role
     *            主键值
     */
    public UserRoleVO(String newPk_user_role) {
        super();

        // 为主键字段赋值:
        pk_user_role = newPk_user_role;
    }

    /**
     * 返回对象标识,用来唯一定位对象.
     *
     * 创建日期:(2005-9-21)
     * 
     * @return String
     */
    public String getPrimaryKey() {

        return pk_user_role;
    }

//    public String getSource() {
//        return source;
//    }
//
//    public void setSource(String source) {
//        this.source = source;
//    }

    /**
     * 设置对象标识,用来唯一定位对象.
     *
     * 创建日期:(2005-9-21)
     * 
     * @param pk_user_role
     *            String
     */
    public void setPrimaryKey(String newPk_user_role) {

        pk_user_role = newPk_user_role;
    }

    /**
     * 返回数值对象的显示名称.
     *
     * 创建日期:(2005-9-21)
     * 
     * @return java.lang.String 返回数值对象的显示名称.
     */
    public String getEntityName() {

        return "UserRole";
    }
}