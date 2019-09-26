package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储用户（操作员）信息的VO类。
 */
public class UserVO extends SuperVO {

    @JsonProperty("rname")
    private String realName;
    @JsonProperty("phonenum")
    private String phone;// 手机号
    @JsonProperty("ck_code")
    private String checkcode;// 验证码
    private String langcode;
    @JsonProperty("pdl")
    private String pwdlevelcode;
    @JsonProperty("crtcorp_id")
    private String pk_creatcorp;// 创建公司
    @JsonProperty("att")
    private String authen_type;
    @JsonProperty("bl1")
    private DZFBoolean isca;
    @JsonProperty("lock_flag")
    private DZFBoolean locked_tag;// 锁定标识
    @JsonProperty("ucode")
    private String user_code;// 用户编码
    @JsonProperty("en_time")
    private DZFDate able_time;// 生效时间
    @JsonProperty("dis_time")
    private DZFDate disable_time;// 失效时间
    private DZFDateTime ts;
    @JsonProperty("u_pwd")
    private String user_password;// 密码
    @JsonProperty("yyyyy")
    private DZFBoolean bappuser;
    @JsonProperty("b_mng")
    private DZFBoolean ismanager;// 是否管理员
    @JsonProperty("u_note")
    private String user_note;//用户描述
    @JsonProperty("corp_id")
    private String pk_corp;//所属公司
    @JsonProperty("zzzz")
    private String pwdparam;
    @JsonProperty("uname")
    private String user_name;// 用户名称
    private Integer dr;
    private Integer istate;
    @JsonProperty("gjyh")
    private DZFBoolean keyuser;
    @JsonProperty("uid")
    private String cuserid;
    @JsonProperty("pppp")
    private int pwdtype;
    // 手机用户注册未签约公司 保存临时公司主键
    private String pk_tempcorp;
    // 签约时回写签约公司主键
    private String pk_signcorp;
    // 是否可图片处理
    private DZFBoolean bdata;
    // 是否财务处理
    private DZFBoolean baccount;

    private String corpnm;//不存库
    private String crtcorp;//不存库

    @JsonProperty("QQ")
    private String user_qq;
    @JsonProperty("mail")
    private String user_mail;// 邮箱
    private DZFBoolean isOwner;//不存库
    // 关联中服网账号
    private String zfuser_code;
    // 中服OAuth服务针对大账房应用分给指定中服账号的refreshtoken，可用它来直接重复申请access_token，而不必重新申请授权码
    private String zf_refreshtoken;
    private DZFBoolean isfactory;//不存库

    private String skin;
    private String kjskin;
    @JsonProperty("departid")
    private String pk_department;// 部门
    @JsonProperty("depart")
    private String deptname;//不存库
    private DZFBoolean islogin;// 是否登录过；

    private String xsstyle;// 1--代表销售管理员2----销售普通用户，6--加盟商负责用户，7--财税平台用户,8--重庆分部用户，其余的为其它用户，仅会对000001公司
    private String xsauthor;// 是否销售管理权限[1-----代表已分配权限],[2---------没有分配权限]  记录财税平台用户核心企业ID
    private String mandq;// 所管理的地区[地区档案的一级]

    // 一下三个字段企业主PC端微信登录用，不存数据库
    private String imagepath;// 用户头像
    private String pk_oauth2;
    private String nickname;// 微信QQ登录名

    @JsonProperty("emid")
    private String pk_employee;// 员工ID

    @JsonProperty("em")
    private String emname;// 员工姓名

    private String emcode;// 员工编码

    //以下4字段集团密码策略使用
    private Integer strategy;//密码策略,null或者0：不控制，1：到期提醒，不锁定账号，2：到期提前提醒，锁定账号。
    private DZFDate dpwddate;//密码策略时间 （修改密码，解锁，策略保存时更新）
    private Integer pwdeffectiveday;//密码有效日期，单位：天
    private Integer pwdremindday;//strategy=2时 有效，设置提前提醒，有效期日，小于pwdeffectiveday


    /*********员工信息---存放员工信息对应表ynt_employee*********/
    @JsonProperty("istatus")
    private Integer istatus;//员工状态  1:在职;2:离职;3:试用

    @JsonProperty("didate")
    private DZFDate dindate;//入职时间

    @JsonProperty("dodate")
    private DZFDate doutdate;//离职时间

    private String roleids;

    private String rolenames;

    private String[] rolepks;

    private String pk_depts;//部门ID，包括上级部门

    private String coperatorid;//录入人
    /*********员工信息*********/

    private String directlogin;//是否直接登录，不存库

    private String jcuid;//工作接手人，不存库

    private ArrayList<SysFunNodeVO> listFunnode;

    private List<CorpVO> list;//有登录权限的客户

    public List<CorpVO> getList() {
        return list;
    }

    public void setList(List<CorpVO> list) {
        this.list = list;
    }

    public String getJcuid() {
        return jcuid;
    }

    public void setJcuid(String jcuid) {
        this.jcuid = jcuid;
    }

    public ArrayList<SysFunNodeVO> getListFunnode() {
        return listFunnode;
    }

    public void setListFunnode(ArrayList<SysFunNodeVO> listFunnode) {
        this.listFunnode = listFunnode;
    }

    public String getDirectlogin() {
        return directlogin;
    }

    public void setDirectlogin(String directlogin) {
        this.directlogin = directlogin;
    }

    public Integer getIstatus() {
        return istatus;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public String getPk_depts() {
        return pk_depts;
    }

    public void setPk_depts(String pk_depts) {
        this.pk_depts = pk_depts;
    }

    public String[] getRolepks() {
        return rolepks;
    }

    public void setRolepks(String[] rolepks) {
        this.rolepks = rolepks;
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

    public void setIstatus(Integer istatus) {
        this.istatus = istatus;
    }

    public DZFDate getDindate() {
        return dindate;
    }

    public void setDindate(DZFDate dindate) {
        this.dindate = dindate;
    }

    public DZFDate getDoutdate() {
        return doutdate;
    }

    public void setDoutdate(DZFDate doutdate) {
        this.doutdate = doutdate;
    }

    public Integer getStrategy() {
        return strategy;
    }

    public void setStrategy(Integer strategy) {
        this.strategy = strategy;
    }

    public Integer getPwdeffectiveday() {
        return pwdeffectiveday;
    }

    public void setPwdeffectiveday(Integer pwdeffectiveday) {
        this.pwdeffectiveday = pwdeffectiveday;
    }

    public Integer getPwdremindday() {
        return pwdremindday;
    }

    public void setPwdremindday(Integer pwdremindday) {
        this.pwdremindday = pwdremindday;
    }

    public DZFDate getDpwddate() {
        return dpwddate;
    }

    public void setDpwddate(DZFDate dpwddate) {
        this.dpwddate = dpwddate;
    }

    public String getEmcode() {
        return emcode;
    }

    public void setEmcode(String emcode) {
        this.emcode = emcode;
    }

    public String getEmname() {
        return emname;
    }

    public void setEmname(String emname) {
        this.emname = emname;
    }

    public String getPk_employee() {
        return pk_employee;
    }

    public void setPk_employee(String pk_employee) {
        this.pk_employee = pk_employee;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getPk_oauth2() {
        return pk_oauth2;
    }

    public void setPk_oauth2(String pk_oauth2) {
        this.pk_oauth2 = pk_oauth2;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public DZFBoolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(DZFBoolean isOwner) {
        this.isOwner = isOwner;
    }

    public DZFBoolean getIsfactory() {
        return isfactory;
    }

    public void setIsfactory(DZFBoolean isfactory) {
        this.isfactory = isfactory;
    }

    public String getZfuser_code() {
        return zfuser_code;
    }

    public void setZfuser_code(String zfuser_code) {
        this.zfuser_code = zfuser_code;
    }

    public String getZf_refreshtoken() {
        return zf_refreshtoken;
    }

    public void setZf_refreshtoken(String zf_refreshtoken) {
        this.zf_refreshtoken = zf_refreshtoken;
    }

    public String getUser_qq() {
        return user_qq;
    }

    public void setUser_qq(String user_qq) {
        this.user_qq = user_qq;
    }

    public String getUser_mail() {
        return user_mail;
    }

    public void setUser_mail(String user_mail) {
        this.user_mail = user_mail;
    }

    public String getCorpnm() {
        return corpnm;
    }

    public void setCorpnm(String corpnm) {
        this.corpnm = corpnm;
    }

    public String getCrtcorp() {
        return crtcorp;
    }

    public void setCrtcorp(String crtcorp) {
        this.crtcorp = crtcorp;
    }

    public DZFBoolean getBdata() {
        return bdata;
    }

    public void setBdata(DZFBoolean bdata) {
        this.bdata = bdata;
    }

    public DZFBoolean getBaccount() {
        return baccount;
    }

    public void setBaccount(DZFBoolean baccount) {
        this.baccount = baccount;
    }

    public String getPk_tempcorp() {
        return pk_tempcorp;
    }

    public void setPk_tempcorp(String pk_tempcorp) {
        this.pk_tempcorp = pk_tempcorp;
    }

    public String getPk_signcorp() {
        return pk_signcorp;
    }

    public void setPk_signcorp(String pk_signcorp) {
        this.pk_signcorp = pk_signcorp;
    }

    public String getCheckcode() {
        return checkcode;
    }

    public void setCheckcode(String checkcode) {
        this.checkcode = checkcode;
    }

    public String getLangcode() {
        return langcode;
    }

    public void setLangcode(String langcode) {
        this.langcode = langcode;
    }

    public String getPwdlevelcode() {
        return pwdlevelcode;
    }

    public void setPwdlevelcode(String pwdlevelcode) {
        this.pwdlevelcode = pwdlevelcode;
    }

    public String getPk_creatcorp() {
        return pk_creatcorp;
    }

    public void setPk_creatcorp(String pk_creatcorp) {
        this.pk_creatcorp = pk_creatcorp;
    }

    public String getAuthen_type() {
        return authen_type;
    }

    public void setAuthen_type(String authen_type) {
        this.authen_type = authen_type;
    }

    public DZFBoolean getIsca() {
        return isca;
    }

    public void setIsca(DZFBoolean isca) {
        this.isca = isca;
    }

    public DZFBoolean getLocked_tag() {
        return locked_tag;
    }

    public void setLocked_tag(DZFBoolean locked_tag) {
        this.locked_tag = locked_tag;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public DZFDate getAble_time() {
        return able_time;
    }

    public void setAble_time(DZFDate able_time) {
        this.able_time = able_time;
    }

    public DZFDate getDisable_time() {
        return disable_time;
    }

    public void setDisable_time(DZFDate disable_time) {
        this.disable_time = disable_time;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public DZFBoolean getBappuser() {
        return bappuser;
    }

    public void setBappuser(DZFBoolean bappuser) {
        this.bappuser = bappuser;
    }

    public DZFBoolean getIsmanager() {
        return ismanager;
    }

    public void setIsmanager(DZFBoolean ismanager) {
        this.ismanager = ismanager;
    }

    public String getUser_note() {
        return user_note;
    }

    public void setUser_note(String user_note) {
        this.user_note = user_note;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getPwdparam() {
        return pwdparam;
    }

    public void setPwdparam(String pwdparam) {
        this.pwdparam = pwdparam;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public Integer getIstate() {
        return istate;
    }

    public void setIstate(Integer istate) {
        this.istate = istate;
    }

    public DZFBoolean getKeyuser() {
        return keyuser;
    }

    public void setKeyuser(DZFBoolean keyuser) {
        this.keyuser = keyuser;
    }

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public int getPwdtype() {
        return pwdtype;
    }

    public void setPwdtype(int pwdtype) {
        this.pwdtype = pwdtype;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getPKFieldName() {
        return "cuserid";
    }

    @Override
    public String getTableName() {
        return "sm_user";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getKjskin() {
        return kjskin;
    }

    public void setKjskin(String kjskin) {
        this.kjskin = kjskin;
    }

    public String getPk_department() {
        return pk_department;
    }

    public void setPk_department(String pk_department) {
        this.pk_department = pk_department;
    }

    public String getDeptname() {
        return deptname;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    public DZFBoolean getIslogin() {
        return islogin;
    }

    public void setIslogin(DZFBoolean islogin) {
        this.islogin = islogin;
    }

    public String getXsstyle() {
        return xsstyle;
    }

    public void setXsstyle(String xsstyle) {
        this.xsstyle = xsstyle;
    }

    public String getXsauthor() {
        return xsauthor;
    }

    public void setXsauthor(String xsauthor) {
        this.xsauthor = xsauthor;
    }

    public String getMandq() {
        return mandq;
    }

    public void setMandq(String mandq) {
        this.mandq = mandq;
    }

}