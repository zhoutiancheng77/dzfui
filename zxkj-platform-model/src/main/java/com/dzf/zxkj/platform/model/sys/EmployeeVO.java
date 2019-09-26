package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 员工信息VO
 * 
 * @author dzf
 *
 */
public class EmployeeVO extends SuperVO {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("eid")
	private String pk_employee;// 主键
	
	@JsonProperty("corpid")
	private String pk_corp;// 公司主键
	
	@JsonProperty("code")
	private String vemcode;// 员工编码
	
	@JsonProperty("name")
	private String vemname;// 员工名称
	
	@JsonProperty("sex")
	private Integer sex;// 性别 	1:男；2：女
	
	@JsonProperty("nation")
	private String vnation;// 民族

	@JsonProperty("bday")
	private DZFDate dbirthday;//出生日期
	
	@JsonProperty("email")
	private String email;// 电子邮箱
	
	@JsonProperty("vidno")
	private String vidno;//身份证号
	
	@JsonProperty("phone")
	private String phone;//手机号
	
	@JsonProperty("vaddress")
	private String vaddress;//户口地址
	
	@JsonProperty("vcontactinf")
	private String vcontactinf;//其他联系方式
	
	@JsonProperty("pk_dept")
	private String pk_department;//部门
	
	@JsonProperty("deptname")
	private String deptname;//部门名称

	@JsonProperty("vduties")
	private String vduties;//职务
	
	@JsonProperty("edu")
	private String education;//学历
	
	@JsonProperty("title")
	private String vtitle;// 职称
	
	@JsonProperty("didate")
	private DZFDate dindate;//入职时间
	
	@JsonProperty("dodate")
	private DZFDate doutdate;//离职时间
	
	@JsonProperty("status")
	private Integer istatus;//员工状态  1:在职;2:离职;3:试用
	
	@JsonProperty("vmemo")
	private String vmemo;//备注
	
	@JsonProperty("phurl")
	private String photourl;// 头像URL
	
	@JsonProperty("ctid")
	private String coperatorid;// 录入人
	
	@JsonProperty("dtdate")
	private DZFDate doperatedate;// 录入日期
	
	@JsonProperty("cuid")
	private String cuserid;
	
	@JsonProperty("dr")
	private Integer dr;//删除标志
	
	@JsonProperty("ts")
	private DZFDateTime ts;//时间戳
	
	@JsonProperty("bdate")
    private DZFDate dbegindate; // 开始日期（查询用的）用于入职日期开始
    
    @JsonProperty("edate")
    private DZFDate denddate; // 结束日期（查询用的）用于入职日期结束
    
	@JsonProperty("imgwh")
	private int imgwidth;//照片宽度
	
	@JsonProperty("imght")
	private int imgheight;//照片高度
	
	@JsonProperty("x1")
	private int x1;
	
	@JsonProperty("y1")
	private int y1;
	
	@JsonProperty("x2")
	private int x2;
	
	@JsonProperty("y2")
	private int y2;
	
	public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}

	public int getImgwidth() {
		return imgwidth;
	}

	public void setImgwidth(int imgwidth) {
		this.imgwidth = imgwidth;
	}

	public int getImgheight() {
		return imgheight;
	}

	public void setImgheight(int imgheight) {
		this.imgheight = imgheight;
	}

	
    public DZFDate getDbegindate() {
		return dbegindate;
	}

	public void setDbegindate(DZFDate dbegindate) {
		this.dbegindate = dbegindate;
	}

	public DZFDate getDenddate() {
		return denddate;
	}

	public void setDenddate(DZFDate denddate) {
		this.denddate = denddate;
	}

	public String getPk_employee() {
		return pk_employee;
	}

	public void setPk_employee(String pk_employee) {
		this.pk_employee = pk_employee;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVemcode() {
		return vemcode;
	}

	public void setVemcode(String vemcode) {
		this.vemcode = vemcode;
	}

	public String getVemname() {
		return vemname;
	}

	public void setVemname(String vemname) {
		this.vemname = vemname;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getVnation() {
		return vnation;
	}

	public void setVnation(String vnation) {
		this.vnation = vnation;
	}

	public DZFDate getDbirthday() {
		return dbirthday;
	}

	public void setDbirthday(DZFDate dbirthday) {
		this.dbirthday = dbirthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getVidno() {
		return vidno;
	}

	public void setVidno(String vidno) {
		this.vidno = vidno;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVaddress() {
		return vaddress;
	}

	public void setVaddress(String vaddress) {
		this.vaddress = vaddress;
	}

	public String getVcontactinf() {
		return vcontactinf;
	}

	public void setVcontactinf(String vcontactinf) {
		this.vcontactinf = vcontactinf;
	}

	public String getPk_department() {
		return pk_department;
	}

	public void setPk_department(String pk_department) {
		this.pk_department = pk_department;
	}

	public String getVduties() {
		return vduties;
	}

	public void setVduties(String vduties) {
		this.vduties = vduties;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getVtitle() {
		return vtitle;
	}

	public void setVtitle(String vtitle) {
		this.vtitle = vtitle;
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

	public Integer getIstatus() {
		return istatus;
	}

	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public String getPhotourl() {
		return photourl;
	}

	public void setPhotourl(String photourl) {
		this.photourl = photourl;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
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
	
	public String getDeptname() {
		return deptname;
	}

	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}

	@Override
	public String getPKFieldName() {
		return "pk_employee";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_employee";
	}

}
