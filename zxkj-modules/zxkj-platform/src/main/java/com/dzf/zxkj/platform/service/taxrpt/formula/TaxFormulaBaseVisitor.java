// Generated from D:/Works/dzf-zxkj/zxkj-modules/zxkj-platform/src/main/java/com/dzf/zxkj/platform/service/taxrpt/formula\TaxFormula.g4 by ANTLR 4.8
package com.dzf.zxkj.platform.service.taxrpt.formula;
import com.dzf.zxkj.common.utils.StringUtil;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides an empty implementation of {@link TaxFormulaVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class TaxFormulaBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements TaxFormulaVisitor<T> {
	// 简单脚本工具
	private FormulaTool formulatool;
	// 上下文
	public HashMap<String, Object> context;

	public TaxFormulaBaseVisitor(FormulaTool formulatool) {
		this.formulatool = formulatool;
		this.context = formulatool.context;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitExpr(TaxFormulaParser.ExprContext ctx) {
		return visitChildren(ctx);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitExpr1(TaxFormulaParser.Expr1Context ctx) {
		if (ctx.sobj() != null || ctx.cobj() != null) { // 对象类型，继续解析
			return visitChildren(ctx);
		}

		// 数值和字符串等简单类型
		String text = ctx.getText();
		if (ctx.INT() != null) { // && !StringUtil.isEmpty(ctx.INT().getText())
			return (T)Integer.valueOf(text);
		} else if (ctx.NUM() != null) {
			return (T)Double.valueOf(text);
		} else if (ctx.STR() != null) {
			return (T)text.substring(1, text.length() - 1);
		}
		return visitChildren(ctx);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitSobj(TaxFormulaParser.SobjContext ctx) {
		String text = ctx.getText();
		T value = null;
		if (ctx.CELL() != null) { // 单元格取值
			Pattern p = Pattern.compile("[Rr](?:(\\d+)|\\w)[Cc](?:(\\d+)|\\w)");
			Matcher matcher = p.matcher(text);
			if (matcher.matches()) {
				int rowno = 0;
				if (matcher.group(1) == null) { // RXC2的形式
					if (context.containsKey("currRow"))
						rowno = (int)context.get("currRow");
				} else { // 带具体的行号，如：R1
					rowno = Integer.parseInt(matcher.group(1)) - 1;
				}
				int colno = 0;
				if (matcher.group(2) == null) { // R1CY的形式
					if (context.containsKey("currCol"))
						colno = (int)context.get("currCol");
				} else { // 带具体的列号，如：C1
					colno = Integer.parseInt(matcher.group(2)) - 1;
				}
				value = (T)formulatool.getCellValue(rowno, colno);
				// 把单元格中的空格去掉
				if (value instanceof String) {
					value = (T)((String)value).trim();
				}
				return value;
			}
		} else if (ctx.ID() != null) { // 上下文中的对象或预置对象，如：report、corp、qcLines、this等
			if (context.containsKey(text)) {
				value = (T)context.get(text);
				return value;
			}
			return null;
		}
		return visitChildren(ctx);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitCobj(TaxFormulaParser.CobjContext ctx) {
		if (ctx.getChildCount() == 1) { // 内置函数
			// 调用 FormulaTool 上的静态方法（splitString()等）

			// 属性或方法信息
			ParseTree rctx = ctx.getChild(0); //method
			String methodName = "";
			List<T> pvalueList = new ArrayList<>();
			List<Class<?>> paramTypes = new ArrayList<>();
			if (rctx instanceof TaxFormulaParser.MethodContext) {
				Map callinfo = (Map)visit(rctx); // 得到方法名及方法参数列表
				methodName = (String)callinfo.get("methodName");
				pvalueList = (List<T>)callinfo.get("paramList");
			}

			// 得到参数类型列表
			for (T value: pvalueList) {
				// 参数值value为null时，假设该参数的类型为String
				paramTypes.add(value != null ? value.getClass() : String.class);
			}
			T value = null;
			Method method = null;
			try {
				method = FormulaTool.class.getMethod(methodName, paramTypes.toArray(new Class<?>[0]));
				if (method != null) {
					value = (T)method.invoke(null, pvalueList.toArray(new Object[0]));
				}
			} catch (Exception e) {
			}
			return value;
		}
		if (ctx.getChildCount() == 3) { // 对象的属性或方法调用
			// 方法或属性
			if (!ctx.getChild(1).getText().equals("."))
				return null;

			// 调用 leftobj.method(paramList) 或 leftobj.getField()
			// 左边对象
			T leftobj = visit(ctx.getChild(0)); //funcName
			if (leftobj == null)
				return null;

			// 属性或方法信息
			ParseTree rctx = ctx.getChild(2); //method
			String methodName = "";
			List<T> pvalueList = new ArrayList<>();
			List<Class<?>> paramTypes = new ArrayList<>();
			if (rctx instanceof TaxFormulaParser.MethodContext) {
				Map callinfo = (Map)visit(rctx); // 得到方法名及方法参数列表
				methodName = (String)callinfo.get("methodName");
				pvalueList = (List<T>)callinfo.get("paramList");
			} else if (rctx instanceof TaxFormulaParser.FieldContext) {
				String fname = rctx.getText();
				methodName = "get" + fname.substring(0, 1).toUpperCase() + fname.substring(1);
			}

			// 得到参数类型列表
			for (T value: pvalueList) {
				// 参数值value为null时，假设该参数的类型为String
				paramTypes.add(value != null ? value.getClass() : String.class);
			}
			T value = null;
			Method method = null;
			try {
				if (leftobj instanceof Map && methodName.equals("get")) { // 泛型对象上的get方法，实际参数为Object类型
					method = leftobj.getClass().getMethod(methodName, Object.class);
				} else {
					method = leftobj.getClass().getMethod(methodName, paramTypes.toArray(new Class<?>[0]));
				}
				if (method != null) {
					value = (T)method.invoke(leftobj, pvalueList.toArray(new Object[0]));
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
			return value;
		}
		return visitChildren(ctx);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitField(TaxFormulaParser.FieldContext ctx) {
		// String fieldName = ctx.getText();
		return visitChildren(ctx);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitMethod(TaxFormulaParser.MethodContext ctx) {
		Map<String, Object> callinfo = new HashMap<>();
		callinfo.put("methodName", ctx.funcName().getText());

		//计算参数列表中各参数的值
		List<T> pvalueList = (List<T>)visit(ctx.params());
		callinfo.put("paramList", pvalueList);
		return (T)callinfo;
		//return visitChildren(ctx);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitFuncName(TaxFormulaParser.FuncNameContext ctx) {
		return visitChildren(ctx);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitParams(TaxFormulaParser.ParamsContext ctx) {
		List<T> valueList = new ArrayList<>();
		int n = ctx.getChildCount();
		// 拼接参数值列表
		for(int i = 0; i < n; ++i) {
			T value = visit(ctx.getChild(i));
			//value可能为null，null得不到类型，后面反射取method信息时需注意
			valueList.add(value);
		}
		return (T)valueList;
		//return visitChildren(ctx);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitParam(TaxFormulaParser.ParamContext ctx) {
		return visitChildren(ctx);
	}
}
