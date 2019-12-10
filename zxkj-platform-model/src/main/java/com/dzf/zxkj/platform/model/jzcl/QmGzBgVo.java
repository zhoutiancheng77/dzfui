package com.dzf.zxkj.platform.model.jzcl;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 期末关账报告
 * 
 * @author zhangj
 *
 */
public class QmGzBgVo extends SuperVO {

	private String id;//临时性的
	private String xm;
	private String bz;//标注
	private DZFBoolean issuccess;// 是否通过
	private String vmemo;// 通过/失败消息内容
	private String url;//连接url
	private String paramstr; // 传递的参数
	private String name;//url名称
	private DZFDouble value;//金额值
	private DZFDouble min;//默认值(最小值)
	private DZFDouble max;//最大默认值
	private DZFDouble yjz;//预警值
	private Integer zt;//状态 -1 不用处理-查看，0 待处理 1 删除 (首页代办事项需要)
	
	private GjGx gjgx;//勾稽关系
	
	public class GjGx {
		private DZFDouble wfp;// 未分配

		private DZFDouble jlr;// 净利润

		private DZFDouble ce;// 差额

		public DZFDouble getWfp() {
			return wfp;
		}

		public void setWfp(DZFDouble wfp) {
			this.wfp = wfp;
		}

		public DZFDouble getJlr() {
			return jlr;
		}

		public void setJlr(DZFDouble jlr) {
			this.jlr = jlr;
		}

		public DZFDouble getCe() {
			return ce;
		}

		public void setCe(DZFDouble ce) {
			this.ce = ce;
		}

	}
	
	public GjGx getGjgx() {
		return gjgx;
	}

	public void setGjgx(GjGx gjgx) {
		this.gjgx = gjgx;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getZt() {
		return zt;
	}

	public void setZt(Integer zt) {
		this.zt = zt;
	}

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

	public DZFDouble getYjz() {
		return yjz;
	}

	public void setYjz(DZFDouble yjz) {
		this.yjz = yjz;
	}

	public DZFDouble getMin() {
		return min;
	}

	public void setMin(DZFDouble min) {
		this.min = min;
	}

	public DZFDouble getMax() {
		return max;
	}

	public void setMax(DZFDouble max) {
		this.max = max;
	}

	public DZFDouble getValue() {
		return value;
	}

	public void setValue(DZFDouble value) {
		this.value = value;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public DZFBoolean getIssuccess() {
		return issuccess;
	}

	public void setIssuccess(DZFBoolean issuccess) {
		this.issuccess = issuccess;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public String getParamstr() {
		return paramstr;
	}

	public void setParamstr(String paramstr) {
		this.paramstr = paramstr;
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
