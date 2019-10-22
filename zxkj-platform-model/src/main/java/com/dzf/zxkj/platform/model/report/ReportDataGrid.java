package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

import java.util.List;

/**
 * 报表的grid
 * 
 * @author zhangj
 *
 */
@SuppressWarnings("serial")
public class ReportDataGrid extends DataGrid {

	private List<KmReportDatagridColumn> columnlist2;

	private List<KmmxConFzMxVO> kmconfz;
	
	private List<FzKmmxVO> fzkmmx;
	
	private List<NumMnyDetailVO> numMnyDetail;
	
	private List<NumMnyDetailVO> numcombox;
	
	private List<com.dzf.zxkj.platform.model.glic.IcDetailVO> kcDetail;
	
	private List<IcDetailVO> icDetail;
	
	private List<IcDetailFzVO> iccombox;
	
	private List<KmmxConFzMxVO> righttree;//科目右边显示数据
	
	private String selectkmid;//选中的科目id
	
	private boolean blancemsg;//资产负债表,现金流量表是否平衡
	
	private String blancetitle;//平衡消息
	
	private ZcfzMsgVo zcfz_jyx;//资产负债表校验项的值
	
	private XjllMsgVo xjll_jyx;//现金流量校验项
	
	public XjllMsgVo getXjll_jyx() {
		return xjll_jyx;
	}

	public void setXjll_jyx(XjllMsgVo xjll_jyx) {
		this.xjll_jyx = xjll_jyx;
	}

	public class XjllMsgVo extends SuperVO {
		
		
		private DZFDouble ce;//差额
		
		private DZFDouble xjlltotal;//现金发生值

		private DZFDouble kmqcvalue;//科目期初

		private DZFDouble kmqmvalue;//科目期末
		
		public DZFDouble getCe() {
			return ce;
		}

		public void setCe(DZFDouble ce) {
			this.ce = ce;
		}

		public DZFDouble getXjlltotal() {
			return xjlltotal;
		}

		public void setXjlltotal(DZFDouble xjlltotal) {
			this.xjlltotal = xjlltotal;
		}

		public DZFDouble getKmqcvalue() {
			return kmqcvalue;
		}

		public void setKmqcvalue(DZFDouble kmqcvalue) {
			this.kmqcvalue = kmqcvalue;
		}

		public DZFDouble getKmqmvalue() {
			return kmqmvalue;
		}

		public void setKmqmvalue(DZFDouble kmqmvalue) {
			this.kmqmvalue = kmqmvalue;
		}

		@Override
		public String getPKFieldName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getParentPKFieldName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getTableName() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public ZcfzMsgVo getZcfz_jyx() {
		return zcfz_jyx;
	}

	public void setZcfz_jyx(ZcfzMsgVo zcfz_jyx) {
		this.zcfz_jyx = zcfz_jyx;
	}

	public String getSelectkmid() {
		return selectkmid;
	}

	public void setSelectkmid(String selectkmid) {
		this.selectkmid = selectkmid;
	}

	public String getBlancetitle() {
		return blancetitle;
	}

	public void setBlancetitle(String blancetitle) {
		this.blancetitle = blancetitle;
	}

	public boolean isBlancemsg() {
		return blancemsg;
	}

	public void setBlancemsg(boolean blancemsg) {
		this.blancemsg = blancemsg;
	}

	public List<KmmxConFzMxVO> getRighttree() {
		return righttree;
	}

	public void setRighttree(List<KmmxConFzMxVO> righttree) {
		this.righttree = righttree;
	}

	public List<KmReportDatagridColumn> getColumnlist2() {
		return columnlist2;
	}

	public void setColumnlist2(List<KmReportDatagridColumn> columnlist2) {
		this.columnlist2 = columnlist2;
	}

	public List<FzKmmxVO> getFzkmmx() {
		return fzkmmx;
	}

	public void setFzkmmx(List<FzKmmxVO> fzkmmx) {
		this.fzkmmx = fzkmmx;
	}

	public List<KmmxConFzMxVO> getKmconfz() {
		return kmconfz;
	}

	public void setKmconfz(List<KmmxConFzMxVO> kmconfz) {
		this.kmconfz = kmconfz;
	}

	public List<NumMnyDetailVO> getNumMnyDetail() {
		return numMnyDetail;
	}

	public void setNumMnyDetail(List<NumMnyDetailVO> numMnyDetail) {
		this.numMnyDetail = numMnyDetail;
	}

	public List<NumMnyDetailVO> getNumcombox() {
		return numcombox;
	}

	public void setNumcombox(List<NumMnyDetailVO> numcombox) {
		this.numcombox = numcombox;
	}

	public List<IcDetailVO> getIcDetail() {
		return icDetail;
	}

	public void setIcDetail(List<IcDetailVO> icDetail) {
		this.icDetail = icDetail;
	}

	public List<IcDetailFzVO> getIccombox() {
		return iccombox;
	}

	public void setIccombox(List<IcDetailFzVO> iccombox) {
		this.iccombox = iccombox;
	}

	public List<com.dzf.zxkj.platform.model.glic.IcDetailVO> getKcDetail() {
		return kcDetail;
	}

	public void setKcDetail(List<com.dzf.zxkj.platform.model.glic.IcDetailVO> kcDetail) {
		this.kcDetail = kcDetail;
	}

}
