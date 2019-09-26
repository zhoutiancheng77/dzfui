package com.dzf.zxkj.platform.model.jzcl;

import java.io.Serializable;
import java.util.List;

public class TransFerVOInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8330364720117209707L;

	private QmclVO qmvo = null;
	
	private List<CostForwardVO> costforwardvolist1 = null;
	
	private List<CostForwardVO> costforwardvolist2 = null;
	
	private List<CostForwardVO> costforwardvolist3 = null;
	
	private CostForwardInfo[]   costforwardvolist4 = null;//注意VO类型不一致
	
	private List<CostForwardVO> costforwardvolist5 = null;

	public QmclVO getQmvo() {
		return qmvo;
	}

	public void setQmvo(QmclVO qmvo) {
		this.qmvo = qmvo;
	}

	public List<CostForwardVO> getCostforwardvolist1() {
		return costforwardvolist1;
	}

	public void setCostforwardvolist1(List<CostForwardVO> costforwardvolist1) {
		this.costforwardvolist1 = costforwardvolist1;
	}

	public List<CostForwardVO> getCostforwardvolist2() {
		return costforwardvolist2;
	}

	public void setCostforwardvolist2(List<CostForwardVO> costforwardvolist2) {
		this.costforwardvolist2 = costforwardvolist2;
	}

	public List<CostForwardVO> getCostforwardvolist3() {
		return costforwardvolist3;
	}

	public void setCostforwardvolist3(List<CostForwardVO> costforwardvolist3) {
		this.costforwardvolist3 = costforwardvolist3;
	}

	public CostForwardInfo[] getCostforwardvolist4() {
		return costforwardvolist4;
	}

	public void setCostforwardvolist4(CostForwardInfo[] costforwardvolist4) {
		this.costforwardvolist4 = costforwardvolist4;
	}

	public List<CostForwardVO> getCostforwardvolist5() {
		return costforwardvolist5;
	}

	public void setCostforwardvolist5(List<CostForwardVO> costforwardvolist5) {
		this.costforwardvolist5 = costforwardvolist5;
	}

}
