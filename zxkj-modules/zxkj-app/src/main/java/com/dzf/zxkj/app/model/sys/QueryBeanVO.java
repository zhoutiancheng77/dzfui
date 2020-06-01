package com.dzf.zxkj.app.model.sys;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({ "rawtypes" })
public class QueryBeanVO extends SuperVO {
	
	private static final long serialVersionUID = 2470131345409548660L;
	
	@JsonProperty("bdate")
	private String startdate; // 开始日期
	@JsonProperty("edate")
	private String enddate;// 结束日期
	@JsonProperty("corp_id")
	private String pk_corp;// 会计公司

	@JsonProperty("time")
	private String timestamp;
	@JsonProperty("cforder")
	private String codedefaultorder="desc";  //客户编码默认排序方式  降序

	// 用户令牌
	private String token;
	// 操作标识
	@JsonProperty("operate")
	private String operate;
	// 手机端传过来的json
	private String json;

	@JsonProperty("product_id")
	private String pk_product;// 业务大类
	@JsonProperty("btype_id")
	private String btypemin; // 业务小类
	@JsonProperty("corpk_id")
	private String pk_corpk; // 客户主键
	@JsonProperty("contractid")
	private String pk_contract;//合同主键

	// 新增潜在客户管理使用 开始
	@JsonProperty("flwstatus")
	private Integer flwstatus;// 跟进状态 0：待跟进 1：申请转为正式客户 2：已放弃  10： APP全部
	@JsonProperty("pkcustno")
	private String pk_customno; // 潜在客户主键
	private String offerins; // 报价说明
	private String followfeed; // 跟进反馈
	private String followdate; // 跟进时间
	private String cusname;// 客户姓名
	private String cusmobile;// 客户电话
	private String cusemail;// 客户邮件
	private String cusrequire;// 客户需求

	@JsonProperty("cdate")
	private String doperatedate;// 创建日期
	@JsonProperty("ldate")
	private String loginDate;//登录日期

	//收款管理
	@JsonProperty("charge_id")
	private String pk_charge;//收款主键
	@JsonProperty("vbcode")
	private String vbillcode;
	// 新增客户签约 开始
	private String corpcode;// 公司编码
	private String corpname;// 公司名称
	private String industry; // 行业
	private String corptype; // 科目方案
	private String pk_temp_svorg; // 唯一标识 主键
	// 新增客户签约 结束

	@JsonProperty("pk_proce")
	private String pk_proceset;// 业务流程设置

	@JsonProperty("pk_busi")
	private String pk_busimang; // 业务办理主键
	
	@JsonProperty("userid")
	private String cuserid; // 用户ID 相当于潜在客户跟进中的（创建人标识、跟进人）---已经拆分只剩下创建人的标识（fluser）
	@JsonProperty("user")
	private String user_code;// 用户编码
	@JsonProperty("name")
	private String user_name;// 用户名称
	@JsonProperty("pwd")
	private String password;// 用户密码

	@JsonProperty("newpwd")
	private String new_password;// 用户密码
	
	@JsonProperty("photo")
	private String photo;//头像
	@JsonProperty("phototype")
	private String phototype;//头像图片类型

	@JsonProperty("datas")
	private Object datas;

	@JsonProperty("data")
	private Object data;
	
	@JsonProperty("identify")
	private String identify;
	
	@JsonProperty("phone")
	private String phone;
	
	//客户查询 
	@JsonProperty("ucode")
	private String unitcode;   //客户编号
	@JsonProperty("uname")
	private String unitname;	//客户名称
	@JsonProperty("provide")
	private String isprovide;// 是否提供
	@JsonProperty("borrow")
	private String isborrow;// 是否借走
	private String xwwy_sessionid; //小微无忧 app sessionid
	
	// v 2.0 6/29上线
	@JsonProperty("product_code")
	private String vproductcode; //业务大类编号
	@JsonProperty("btype_code")
	private String vbusitypecode; //业务小类编号
	@JsonProperty("doc_id")
	private String pk_doc;  //图片主键
	@JsonProperty("charge_status")
	private String istatus;  //收款状态
	@JsonProperty("uuid")
	private String reqUrl;//二维码请求串
	@JsonProperty("vccode")
	private String vcontcode;	//合同编号
	
	@JsonProperty("status")
	private String vstatus;	//通用状态：合同状态；
	@JsonProperty("year")
	private String qyear;//查询年
	@JsonProperty("feetypeid")
	private String pk_feetype; // 费用类型主键
	@JsonProperty("month")
	private String qmonth;//查询月
	@JsonProperty("istot")
	private String istotal;//是否合计 Y：合计；N：明细
	@JsonProperty("minsmny")
	private String minsermny;//最低月服务费
	@JsonProperty("maxsmny")
	private String maxsermny;//最高月服务费
	@JsonProperty("isfi")
	private String isfinal;//是否最终余额：Y:最终余额；N：明细
	//消息新增字段
	@JsonProperty("ssend")
	private String sys_send;//发送端
	@JsonProperty("dsdate")
	private String dsenddate;//反馈日期
	@JsonProperty("vcont")
	private String vcontent;//反馈内容
	@JsonProperty("mtype")
	private	Integer msgtype;//消息类型编码 1：送票；2：抄税；3：清卡；4：凭证交接
	//收款查询区分收款和退款
	@JsonProperty("btype")
	private Integer busitype;// 业务数据类型 0：收款 1：退款
	
	@JsonProperty("traceId")
	private String traceId;//app端记录操作日志参数，申请时传递，原样返回即可
	
	private String systype;//app类型：1：IOS，0：安卓；
	
	private Integer qrytype;//查询类型3:获取客户图片；4、获取用户图片；5、代办；6、已办;8、系统通知；9、短信通知；10、系统通知+短信通知（发送激活码）
							//11、一般纳税人；12、小规模纳税人
							//当前状态 38：送票提醒；42：已取票；39：抄税提醒；43：已抄税；40：清卡提醒；44：已清卡；41：凭证交接；45：凭证收到；
						    //13、一般客户；14、上传票据客户；15、资料交接客户列表
	
	private String imgpath;//获取图片路径
	
	//做账进度添加的字段，为了手机app
	@JsonProperty("qj")
	private String vperiod;//期间
	
	@JsonProperty("id")
	private String pk_zj;//（通用）主键
	
	@JsonProperty("fluser")
	private String followuser;// 跟进人
	
	private String paymethod;//支付方式
	
	private String memo;//摘要
	
	private String memo1;//备注
	
	private String mny;//金额
	
	private String groupkey;//图片组信息
	
	private String delimagekeys;//需要删除的id 000001,0000002
	
	private String curr_imagekeys;//当前界面的id 
	
	private String drcode;//二维码
	
	private String libs;//退回的详细图片 格式"001,002"
	
	private String message;//退回消息
	
	@JsonProperty("kjid")
	private String kjid;//会计主键
	
	private String mngtent;//办理情况
	
	private Long l_date;//日期
	
	private String costmny;//业务处理的过程费用

	@JsonProperty("vname")
	private String vname;//（通用）名称

	private String issendmsg;//是否可以发短信提醒客户，1：可以；0：不可以；
	
	private String img_state;//图片状态 0 未处理 80 已退回，100 已制单   其他，处理中
	
	@JsonProperty("contentid")
	private String pk_content;// 当前节点PK
	
	@JsonProperty("workflowid")
	private String pk_workflow;// 代办流程主键
	
	@JsonProperty("fileids")
	private String fileids;//多个资料主键 格式：fileid1,fileid2
	
	@JsonProperty("fileid")
	private String fileid;//单个资料主键
	
	@JsonProperty("nums")
	private Integer nums;//数量
	
	private String qrid;//二维码主键
	
	@JsonProperty("msgid")
	private String pk_msgid;//消息主键
	
	private String sourcesys;//大账房app区分来源

	public String getSourcesys() {
		return sourcesys;
	}

	public void setSourcesys(String sourcesys) {
		this.sourcesys = sourcesys;
	}

	public String getPk_msgid() {
		return pk_msgid;
	}

	public void setPk_msgid(String pk_msgid) {
		this.pk_msgid = pk_msgid;
	}

	public String getQrid() {
		return qrid;
	}

	public void setQrid(String qrid) {
		this.qrid = qrid;
	}

	public Integer getNums() {
		return nums;
	}

	public void setNums(Integer nums) {
		this.nums = nums;
	}

	public String getFileid() {
		return fileid;
	}

	public void setFileid(String fileid) {
		this.fileid = fileid;
	}

	public String getFileids() {
		return fileids;
	}

	public void setFileids(String fileids) {
		this.fileids = fileids;
	}

	public String getPk_workflow() {
		return pk_workflow;
	}

	public void setPk_workflow(String pk_workflow) {
		this.pk_workflow = pk_workflow;
	}

	public String getPk_content() {
		return pk_content;
	}

	public void setPk_content(String pk_content) {
		this.pk_content = pk_content;
	}

	public String getImg_state() {
		return img_state;
	}

	public void setImg_state(String img_state) {
		this.img_state = img_state;
	}

	public String getCurr_imagekeys() {
		return curr_imagekeys;
	}

	public void setCurr_imagekeys(String curr_imagekeys) {
		this.curr_imagekeys = curr_imagekeys;
	}

	public String getIssendmsg() {
		return issendmsg;
	}

	public void setIssendmsg(String issendmsg) {
		this.issendmsg = issendmsg;
	}

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public String getCostmny() {
		return costmny;
	}

	public void setCostmny(String costmny) {
		this.costmny = costmny;
	}

	public String getMemo1() {
		return memo1;
	}

	public void setMemo1(String memo1) {
		this.memo1 = memo1;
	}

	

	public Long getL_date() {
		return l_date;
	}

	public void setL_date(Long l_date) {
		this.l_date = l_date;
	}

	public String getMngtent() {
		return mngtent;
	}

	public void setMngtent(String mngtent) {
		this.mngtent = mngtent;
	}

	public String getKjid() {
		return kjid;
	}

	public void setKjid(String kjid) {
		this.kjid = kjid;
	}

	public String getLibs() {
		return libs;
	}

	public void setLibs(String libs) {
		this.libs = libs;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUrlid() {
		return urlid;
	}

	public void setUrlid(String urlid) {
		this.urlid = urlid;
	}

	private String urlid;//用户下载图片，拼接url用id(app接口的图片1,9,13)
	
	public String getDrcode() {
		return drcode;
	}

	public void setDrcode(String drcode) {
		this.drcode = drcode;
	}

	public String getDelimagekeys() {
		return delimagekeys;
	}

	public void setDelimagekeys(String delimagekeys) {
		this.delimagekeys = delimagekeys;
	}

	public String getGroupkey() {
		return groupkey;
	}

	public void setGroupkey(String groupkey) {
		this.groupkey = groupkey;
	}

	public String getPaymethod() {
		return paymethod;
	}

	public void setPaymethod(String paymethod) {
		this.paymethod = paymethod;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	

	public String getMny() {
		return mny;
	}

	public void setMny(String mny) {
		this.mny = mny;
	}

	public String getFollowuser() {
		return followuser;
	}

	public void setFollowuser(String followuser) {
		this.followuser = followuser;
	}

	public String getPk_zj() {
		return pk_zj;
	}

	public void setPk_zj(String pk_zj) {
		this.pk_zj = pk_zj;
	}

	public String getVperiod() {
		return vperiod;
	}
	
	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
	}

	public String getImgpath() {
		return imgpath;
	}

	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}

	public String getSystype() {
		return systype;
	}

	public void setSystype(String systype) {
		this.systype = systype;
	}

	public Integer getQrytype() {
		return qrytype;
	}

	public void setQrytype(Integer qrytype) {
		this.qrytype = qrytype;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public Integer getBusitype() {
		return busitype;
	}

	public void setBusitype(Integer busitype) {
		this.busitype = busitype;
	}

	public Integer getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(Integer msgtype) {
		this.msgtype = msgtype;
	}

	public String getVcontent() {
		return vcontent;
	}

	public void setVcontent(String vcontent) {
		this.vcontent = vcontent;
	}

	public String getDsenddate() {
		return dsenddate;
	}

	public void setDsenddate(String dsenddate) {
		this.dsenddate = dsenddate;
	}

	public String getSys_send() {
		return sys_send;
	}

	public void setSys_send(String sys_send) {
		this.sys_send = sys_send;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getPhototype() {
		return phototype;
	}

	public void setPhototype(String phototype) {
		this.phototype = phototype;
	}

	public String getVcontcode() {
		return vcontcode;
	}

	public void setVcontcode(String vcontcode) {
		this.vcontcode = vcontcode;
	}

	public String getReqUrl() {
		return reqUrl;
	}

	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}

	public String getIstatus() {
		return istatus;
	}

	public void setIstatus(String istatus) {
		this.istatus = istatus;
	}

	public String getPk_doc() {
		return pk_doc;
	}

	public void setPk_doc(String pk_doc) {
		this.pk_doc = pk_doc;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public String getVproductcode() {
		return vproductcode;
	}

	public void setVproductcode(String vproductcode) {
		this.vproductcode = vproductcode;
	}

	public String getVbusitypecode() {
		return vbusitypecode;
	}

	public void setVbusitypecode(String vbusitypecode) {
		this.vbusitypecode = vbusitypecode;
	}

	public String getCodedefaultorder() {
		return codedefaultorder;
	}

	public void setCodedefaultorder(String codedefaultorder) {
		this.codedefaultorder = codedefaultorder;
	}

	public String getXwwy_sessionid() {
		return xwwy_sessionid;
	}

	public void setXwwy_sessionid(String xwwy_sessionid) {
		this.xwwy_sessionid = xwwy_sessionid;
	}

	public String getIsprovide() {
		return isprovide;
	}

	public void setIsprovide(String isprovide) {
		this.isprovide = isprovide;
	}

	public String getIsborrow() {
		return isborrow;
	}

	public void setIsborrow(String isborrow) {
		this.isborrow = isborrow;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIdentify() {
		return identify;
	}

	public void setIdentify(String identify) {
		this.identify = identify;
	}

	public String getPk_contract() {
		return pk_contract;
	}

	public void setPk_contract(String pk_contract) {
		this.pk_contract = pk_contract;
	}

	public String getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(String loginDate) {
		this.loginDate = loginDate;
	}

	public String getNew_password() {
		return new_password;
	}

	public void setNew_password(String new_password) {
		this.new_password = new_password;
	}

	public Object getDatas() {
		return datas;
	}

	public void setDatas(Object datas) {
		this.datas = datas;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public Integer getFlwstatus() {
		return flwstatus;
	}

	public void setFlwstatus(Integer flwstatus) {
		this.flwstatus = flwstatus;
	}

	public String getPk_busimang() {
		return pk_busimang;
	}

	public void setPk_busimang(String pk_busimang) {
		this.pk_busimang = pk_busimang;
	}

	public String getPk_proceset() {
		return pk_proceset;
	}

	public void setPk_proceset(String pk_proceset) {
		this.pk_proceset = pk_proceset;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getOfferins() {
		return offerins;
	}

	public void setOfferins(String offerins) {
		this.offerins = offerins;
	}

	public String getFollowfeed() {
		return followfeed;
	}

	public void setFollowfeed(String followfeed) {
		this.followfeed = followfeed;
	}

	public String getCorpcode() {
		return corpcode;
	}

	public void setCorpcode(String corpcode) {
		this.corpcode = corpcode;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getCorptype() {
		return corptype;
	}

	public void setCorptype(String corptype) {
		this.corptype = corptype;
	}

	public String getPk_temp_svorg() {
		return pk_temp_svorg;
	}

	public void setPk_temp_svorg(String pk_temp_svorg) {
		this.pk_temp_svorg = pk_temp_svorg;
	}

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public String getBtypemin() {
		return btypemin;
	}

	public void setBtypemin(String btypemin) {
		this.btypemin = btypemin;
	}

	public String getPk_customno() {
		return pk_customno;
	}

	public void setPk_customno(String pk_customno) {
		this.pk_customno = pk_customno;
	}

	public String getFollowdate() {
		return followdate;
	}

	public void setFollowdate(String followdate) {
		this.followdate = followdate;
	}

	public String getPk_product() {
		return pk_product;
	}

	public void setPk_product(String pk_product) {
		this.pk_product = pk_product;
	}

	public String getCusname() {
		return cusname;
	}

	public void setCusname(String cusname) {
		this.cusname = cusname;
	}

	public String getCusmobile() {
		return cusmobile;
	}

	public void setCusmobile(String cusmobile) {
		this.cusmobile = cusmobile;
	}

	public String getCusemail() {
		return cusemail;
	}

	public void setCusemail(String cusemail) {
		this.cusemail = cusemail;
	}

	public String getCusrequire() {
		return cusrequire;
	}

	public void setCusrequire(String cusrequire) {
		this.cusrequire = cusrequire;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(String doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getPk_charge() {
		return pk_charge;
	}

	public void setPk_charge(String pk_charge) {
		this.pk_charge = pk_charge;
	}

	public String getVstatus() {
		return vstatus;
	}

	public void setVstatus(String vstatus) {
		this.vstatus = vstatus;
	}

	public String getQyear() {
		return qyear;
	}

	public void setQyear(String qyear) {
		this.qyear = qyear;
	}

	public String getPk_feetype() {
		return pk_feetype;
	}

	public void setPk_feetype(String pk_feetype) {
		this.pk_feetype = pk_feetype;
	}

	public String getQmonth() {
		return qmonth;
	}

	public void setQmonth(String qmonth) {
		this.qmonth = qmonth;
	}

	public String getIstotal() {
		return istotal;
	}

	public void setIstotal(String istotal) {
		this.istotal = istotal;
	}

	public String getMinsermny() {
		return minsermny;
	}

	public void setMinsermny(String minsermny) {
		this.minsermny = minsermny;
	}

	public String getMaxsermny() {
		return maxsermny;
	}

	public void setMaxsermny(String maxsermny) {
		this.maxsermny = maxsermny;
	}

	public String getIsfinal() {
		return isfinal;
	}

	public void setIsfinal(String isfinal) {
		this.isfinal = isfinal;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
