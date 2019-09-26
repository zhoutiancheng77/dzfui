package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 用户委派角色VO,主要包括用户id、角色主键、登录公司.
 * <p>
 */
public class JMUserRoleVO extends SuperVO {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String pk_userole;
    private String cuserid;
    private String pk_corp;
    private String pk_role;
    private String role_name;
    
    private Integer dr; // 删除标记

    private DZFDateTime ts; // 时间戳

    /**以下字段只页面展示使用*/
    private String user_code;
    
    private String user_name;
    
    private String zusername;//主办会计
    
    private String jusername;//记账会计
    
    private String deptname;
    
    private String roleids;
    
    private String rolenames;
    
    private String rolecode;
    
    private DZFBoolean haspower;//是否有客户权限
    
    @JsonProperty("incode")
    private String innercode;
    @JsonProperty("uname")
    private String unitname;

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public String getInnercode() {
        return innercode;
    }

    public void setInnercode(String innercode) {
        this.innercode = innercode;
    }

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public DZFBoolean getHaspower() {
        return haspower;
    }

    public void setHaspower(DZFBoolean haspower) {
        this.haspower = haspower;
    }

    public String getZusername() {
        return zusername;
    }

    public void setZusername(String zusername) {
        this.zusername = zusername;
    }

    public String getJusername() {
        return jusername;
    }

    public void setJusername(String jusername) {
        this.jusername = jusername;
    }

    public String getRolecode() {
        return rolecode;
    }

    public void setRolecode(String rolecode) {
        this.rolecode = rolecode;
    }

    public String getDeptname() {
        return deptname;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getRoleids() {
        return roleids;
    }

    public void setRoleids(String roleids) {
        this.roleids = roleids;
    }

    public String getRolenames() {
        return rolenames;
    }

    public void setRolenames(String rolenames) {
        this.rolenames = rolenames;
    }

    public String getPk_userole() {
        return pk_userole;
    }

    public void setPk_userole(String pk_userole) {
        this.pk_userole = pk_userole;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
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

        return "pk_userole";
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

        return "sm_userole";
    }

    /**
     * 使用主键字段进行初始化的构造子.
     *
     * 创建日期:(2005-9-21)
     */
    public JMUserRoleVO() {
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
    public JMUserRoleVO(String newPk_user_role) {
        super();

        // 为主键字段赋值:
        pk_userole = newPk_user_role;
    }

    /**
     * 返回对象标识,用来唯一定位对象.
     *
     * 创建日期:(2005-9-21)
     * 
     * @return String
     */
    public String getPrimaryKey() {

        return pk_userole;
    }

    /**
     * 设置对象标识,用来唯一定位对象.
     *
     * 创建日期:(2005-9-21)
     * 
     * @param pk_user_role
     *            String
     */
    public void setPrimaryKey(String newPk_user_role) {

        pk_userole = newPk_user_role;
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