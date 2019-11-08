package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
public @interface TaxExcelPos {
	/**
	 * 行号
	 * 
	 * @return
	 */
	public int row() default -1;

	/**
	 * 列号
	 * 
	 * @return
	 */
	public int col() default -1;

	/**
	 * 动态行开始
	 * 
	 * @return
	 */
	public int rowBegin() default -1;

	/**
	 * 动态行结束
	 * 
	 * @return
	 */
	public int rowEnd() default -1;

	/**
	 * 动态列开始
	 * 
	 * @return
	 */
	public int colBegin() default -1;

	/**
	 * 动态列结束
	 * 
	 * @return
	 */
	public int colEnd() default -1;

	/**
	 * 名称
	 * 
	 * @return
	 */
	public String name() default "";

	/**
	 * 报表名称
	 * 
	 * @return
	 */
	public String reportname() default "";

	/**
	 * 是否本年累计，需要加上期初
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isTotal() default false;

	/**
	 * 是否为编码名称组合，需要截取编码<br>
	 * 用splitIndex代替
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isCode() default false;

	/**
	 * 是否为编码名称组合，需要截取名称<br>
	 * 用splitIndex代替
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isName() default false;

	/**
	 * 报表ID
	 * 
	 * @return
	 */
	public String reportID() default "";
	
	/**
	 * 代码名称等在同一单元格时，需分隔字符串
	 * 
	 * @return 调用String的split方法后字段在数组中的索引
	 */
	public int splitIndex() default -1;
}
