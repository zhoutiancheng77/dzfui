package com.dzf.zxkj.platform.model.sys;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 角色VO
 */
public class RoleVO extends SuperVO {

	/**
	 * COMPANY_ADMIN_ROLE_PK：公司管理员角色的主键常量
	 */
	public final static String COMPANY_ADMIN_ROLE_PK = "COMPANYADMINISTRATOR";

	/**
	 * COMPANY_ADMIN_ROLE_CODE：公司管理员角色的编码常量
	 */
	public final static String COMPANY_ADMIN_ROLE_CODE = "corpad";
	
	/**
	 * 加盟商机构负责人
	 */
	public final static String JMS01_ROLE_CODE = "jms01";
	
	
	
	/**制单*/
	public final static String COMPANY_ROLE_PKZD = "COMPANYADMINISZHIDAN";

	public final static String COMPANY_ROLE_CODEZD = "corpzd";
	
	/**审核*/
	public final static String COMPANY_ROLE_PKSH = "COMPANYADMINISSHENHE";

	public final static String COMPANY_ROLE_CODESH = "corpsh";
	
	/**报税*/
	public final static String COMPANY_ROLE_PKBS = "COMPANYADMINISBAOSUI";

	public final static String COMPANY_ROLE_CODEBS = "corpbs";
	
	
	
	
	/**公司管理员角色的主键常量*/
	public final static String COMPANY_SJ_ROLE_PK = "COMPANYADMINISSHUJUG";

	/**公司管理员角色的编码常量*/
	public final static String COMPANY_SJ_ROLE_CODE = "corpsj";
	
	/**识图*/
	public final static String COMPANY_ROLE_PKST = "COMPANYADMINISSHITUA";

	public final static String COMPANY_ROLE_CODEST = "corpst";
	
	/**切图*/
	public final static String COMPANY_ROLE_PKQT = "COMPANYADMINISQIETUA";

	public final static String COMPANY_ROLE_CODEQT = "corpqt";
	
	/**预凭证*/
	public final static String COMPANY_ROLE_PKYZ = "COMPANYADMINISSHYUPZ";

	public final static String COMPANY_ROLE_CODEYZ = "corpyz";

	/**
	 * COMPANY_TYPE：公司资源类型（角色的资源类型常量） 
	 */
	public final static int COMPANY_TYPE = 1;

	/**
	 * ORGBOOK_TYPE：主体账簿类型（角色的资源类型常量）  
	 */
	public final static int ORGBOOK_TYPE = 2;

	/**
	 * COMPOSITE_TYPE：复合类型（角色的资源类型常量）  
	 */
	public final static int COMPOSITE_TYPE = 3;
	

	/**
	 * 会计角色
	 */
	public final static int ROLE_KJ = 1;

	/**
	 * 数据角色
	 */
	public final static int ROLE_SJ = 2;

	/*
	 * pk_role：角色主键
	 */
	@JsonProperty("role_id")
	private String pk_role;

	/**
	 * pk_corp：角色所在公司主键 
	 */
	@JsonProperty("corp_id")
	private String pk_corp;
	
	/**
	 * resource_type：角色的资源类型(引用常量COMPANY_TYPE、ORGBOOK_TYPE、COMPOSITE_TYPE) 
	 */
	@JsonProperty("src_type")
	private Integer resource_type;

	/**
	 * role_code：角色编码 
	 */
	@JsonProperty("role_bm")
	private String role_code;

	/**
	 * role_name：角色编码 
	 */
	@JsonProperty("role_mc")
	private String role_name;

	/**
	 * role_memo：角色备注 
	 */
	@JsonProperty("mark")
	private String role_memo;

	/**
	 * usercount：角色中的用户个数限制（目前没有使用）
	 * @deprecated 
	 */
	private Integer usercount;
	
	/**
	 * 角色类型
	 */
	@JsonProperty("rolelx")
	private Integer roletype;//1-大账房管理;2-数据中心管理员;3-会计公司;4-会计核算;5-老会计工厂;6-会计工厂;7-在线业务;8-加盟商;9-财税平台;10-分部管理
	
	private String keyrole;
	
	private String corpnm;
	
	//选择保存的功能节点id
	@JsonProperty("ckfnarr[]")
	private String[] ckfnarr;
	
	@JsonProperty("operatorid")
	private String coperatorid;
	
	@JsonProperty("seal")
	private DZFBoolean seal;//封存-WJX
	
	@JsonProperty("chat")
	private DZFBoolean ischat;//是否客服
	
	public DZFBoolean getIschat() {
		return ischat;
	}
	public void setIschat(DZFBoolean ischat) {
		this.ischat = ischat;
	}
	public DZFBoolean getSeal() {
		return seal;
	}
	public void setSeal(DZFBoolean seal) {
		this.seal = seal;
	}
	public String getCoperatorid() {
		return coperatorid;
	}
	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}
	public String[] getCkfnarr() {
		return ckfnarr;
	}
	public void setCkfnarr(String[] ckfnarr) {
		this.ckfnarr = ckfnarr;
	}
	
	public String getCorpnm() {
		return corpnm;
	}

	public void setCorpnm(String corpnm) {
		this.corpnm = corpnm;
	}

	public String getKeyrole() {
		return keyrole;
	}

	public void setKeyrole(String keyrole) {
		this.keyrole = keyrole;
	}

	public Integer getRoletype() {
		return roletype;
	}

	public void setRoletype(Integer roletype) {
		this.roletype = roletype;
	}

	/**
	 * 获得角色所在公司主键.
	 * @return String
	 */
	public String getPk_corp() {
		return pk_corp;
	}

	/**
	 * 获得角色资源类型.
	 * @return Integer
	 */
	public Integer getResource_type() {
		return resource_type;
	}

	/**
	 * 获得角色编码.
	 * @return String
	 */
	public String getRole_code() {
		return role_code;
	}

	/**
	 * 获得角色备注.
	 * @return String
	 */
	public String getRole_memo() {
		return role_memo;
	}

	/**
	 * 获得角色名称.
	 * @return String
	 */
	public String getRole_name() {
		return role_name;
	}

	/**
	 * @deprecated
	 */
	public Integer getUsercount() {
		return usercount;
	}

	/**
	 * 设置角色所在公司.
	 * @param newPk_corp String
	 */
	public void setPk_corp(String newPk_corp) {

		pk_corp = newPk_corp;
	}

	/**
	 * 设置角色资源类型.
	 * @param newResource_type Integer
	 */
	public void setResource_type(Integer newResource_type) {

		resource_type = newResource_type;
	}

	/**
	 * 设置角色编码.
	 * @param newRole_code String
	 */
	public void setRole_code(String newRole_code) {

		role_code = newRole_code;
	}

	/**
	 * 设置角色备注.
	 * @param newRole_memo String
	 */
	public void setRole_memo(String newRole_memo) {

		role_memo = newRole_memo;
	}

	/**
	 * 设置角色名称.
	 * @param newRole_name String
	 */
	public void setRole_name(String newRole_name) {

		role_name = newRole_name;
	}

	/**
	 * @deprecated
	 */
	public void setUsercount(Integer newUsercount) {

		usercount = newUsercount;
	}


	/**
	 * 取得父VO主键字段.
	 * @return java.lang.String
	 */
	public String getParentPKFieldName() {

		return null;
	}

	/**
	 * 取得表主键字段.
	 * @return java.lang.String
	 */
	public String getPKFieldName() {

		return "pk_role";
	}

	/**
	 * 返回表名称.
	 * @return java.lang.String
	 */
	public String getTableName() {

		return "sm_role";
	}

	/**
	 * 构造子.
	 */
	public RoleVO() {
		super();
	}

	/**
	 * 使用主键进行初始化的构造子.
	 * @param Pk_role 主键值
	 */
	public RoleVO(String newPk_role) {
		super();

		// 为主键字段赋值:
		pk_role = newPk_role;
	}

	/**
	 * 返回角色主键.
	 * @return String
	 */
	public String getPrimaryKey() {

		return pk_role;
	}

	/**
	 * 设置返回角色主键.
	 *
	 * 创建日期:(2005-9-21)
	 * @param pk_role String 
	 */
	public void setPrimaryKey(String newPk_role) {

		pk_role = newPk_role;
	}

	/**
	 * 返回数值对象的显示名称.
	 * @return java.lang.String 返回数值对象的显示名称.
	 */
	public String getEntityName() {

		return "Role";
	}

	public String toString() {
		return role_name;
	}

	/**
	 * 返回角色主键.
	 */
	public String getPk_role() {
		return pk_role;
	}

	/**
	 * 设置角色主键.
	 * @param pk_role 要设置的 pk_role。
	 */
	public void setPk_role(String pk_role) {
		this.pk_role = pk_role;
	}
}