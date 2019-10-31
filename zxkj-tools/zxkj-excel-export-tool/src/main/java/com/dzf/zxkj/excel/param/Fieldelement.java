package com.dzf.zxkj.excel.param;

import java.io.Serializable;

public class Fieldelement implements Serializable{

	private String name;
	
	private String code;
	
	private boolean isdecimal = false;
	
	private int digit = 2;
	
	private boolean zeroshownull = true;
	
	private int colwidth = 15;
	
	private boolean ispercent = false;
	
	private Fieldelement[] childs = null;
	
	private String[] comboStrs;
	
	private Integer rowspan;//占用多少行
	
	private Integer colspan;//占用多少列
	
	private Integer cell_pos;//单元格起始位置

	public Fieldelement() {

	}

	/**
	 * 
	 * @param name  显示名称
	 * @param code  VO中code字段
	 * @param isdecimal  是否数字
	 * @param digit		有效数字几位
	 * @param zeroshownull 数据为0，是否显示为 null (为true，则显示为 null )
	 */
	public Fieldelement(String code,String name,boolean isdecimal,int digit,boolean zeroshownull){
		this.code = code;
		this.name = name;
		this.isdecimal = isdecimal;
		this.digit = digit;
		this.zeroshownull = zeroshownull;
	}
	/**
	 * 
	 * @param name  显示名称
	 * @param code  VO中code字段
	 * @param isdecimal  是否数字
	 * @param digit		有效数字几位
	 * @param zeroshownull 数据为0，是否显示为 null (为true，则显示为 null )
	 */
	public Fieldelement(String code,String name,boolean isdecimal,int digit,boolean zeroshownull,Integer rowspan,Integer colspan){
		this.code = code;
		this.name = name;
		this.isdecimal = isdecimal;
		this.digit = digit;
		this.zeroshownull = zeroshownull;
		this.rowspan = rowspan;
		this.colspan = colspan;
	}
	
	
	/**
     * 
     * @param name  显示名称
     * @param code  VO中code字段
     * @param isdecimal  是否数字
     * @param digit     有效数字几位
     * @param zeroshownull 数据为0，是否显示为 null (为true，则显示为 null )
     */
    public Fieldelement(String code,String name,boolean isdecimal,int digit,boolean zeroshownull,String[] comboStrs){
        this.code = code;
        this.name = name;
        this.isdecimal = isdecimal;
        this.digit = digit;
        this.zeroshownull = zeroshownull;
        this.comboStrs = comboStrs;
    }
	
	
	/**
	 * 
	 * @param name 显示名称
	 * @param childs 子字段
	 */
	public Fieldelement(String name,Fieldelement[] childs){
		this.name = name;
		this.childs = childs;
	}
	
	/**
	 * 
	 * @param name  显示名称
	 * @param code  VO中code字段
	 * @param isdecimal  是否数字
	 * @param digit		有效数字几位
	 * @param zeroshownull 数据为0，是否显示为 null (为true，则显示为 null )
	 * @param  colwidth 定义列宽度 (ColumnWidth 默认为 15)
	 * @param isPercent 是否为百分比
	 */
	public Fieldelement(String code,String name,boolean isdecimal,int digit,boolean zeroshownull,int colwidth,boolean ispercent,Integer rowspan,Integer colspan ){
		this.code = code;
		this.name = name;
		this.isdecimal = isdecimal;
		this.digit = digit;
		this.zeroshownull = zeroshownull;
		this.colwidth = colwidth;
		this.ispercent = ispercent;
		this.rowspan = rowspan;
		this.colspan = colspan;
	}

	
	
	/**
	 * 
	 * @param name  显示名称
	 * @param code  VO中code字段
	 * @param isdecimal  是否数字
	 * @param digit		有效数字几位
	 * @param zeroshownull 数据为0，是否显示为 null (为true，则显示为 null )
	 * @param  colwidth 定义列宽度 (ColumnWidth 默认为 15)
	 * @param isPercent 是否为百分比
	 */
	public Fieldelement(String code,String name,boolean isdecimal,int digit,boolean zeroshownull,int colwidth,boolean ispercent){
		this.code = code;
		this.name = name;
		this.isdecimal = isdecimal;
		this.digit = digit;
		this.zeroshownull = zeroshownull;
		this.colwidth = colwidth;
		this.ispercent = ispercent;
	}

	public String[] getComboStrs() {
        return comboStrs;
    }

    public void setComboStrs(String[] comboStrs) {
        this.comboStrs = comboStrs;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean getIsdecimal() {
		return isdecimal;
	}

	public void setIsdecimal(boolean isdecimal) {
		this.isdecimal = isdecimal;
	}

	public int getDigit() {
		return digit;
	}

	public void setDigit(int digit) {
		this.digit = digit;
	}

	public boolean isZeroshownull() {
		return zeroshownull;
	}

	public void setZeroshownull(boolean zeroshownull) {
		this.zeroshownull = zeroshownull;
	}

	public int getColwidth() {
		return colwidth;
	}

	public void setColwidth(int colwidth) {
		this.colwidth = colwidth;
	}

	public boolean isIspercent() {
		return ispercent;
	}

	public void setIspercent(boolean ispercent) {
		this.ispercent = ispercent;
	}

	public Fieldelement[] getChilds() {
		return childs;
	}

	public void setChilds(Fieldelement[] childs) {
		this.childs = childs;
	}

	public Integer getRowspan() {
		return rowspan;
	}

	public void setRowspan(Integer rowspan) {
		this.rowspan = rowspan;
	}

	public Integer getColspan() {
		return colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}
	public Integer getCell_pos() {
		return cell_pos;
	}
	public void setCell_pos(Integer cell_pos) {
		this.cell_pos = cell_pos;
	}
	
	
}
