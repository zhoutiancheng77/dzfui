// Generated from D:/Works/dzf-zxkj/zxkj-modules/zxkj-platform/src/main/java/com/dzf/zxkj/platform/service/taxrpt/formula\TaxFormula.g4 by ANTLR 4.8
package com.dzf.zxkj.platform.service.taxrpt.formula;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TaxFormulaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TaxFormulaVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TaxFormulaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(TaxFormulaParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaxFormulaParser#expr1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr1(TaxFormulaParser.Expr1Context ctx);
	/**
	 * Visit a parse tree produced by {@link TaxFormulaParser#sobj}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSobj(TaxFormulaParser.SobjContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaxFormulaParser#cobj}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCobj(TaxFormulaParser.CobjContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaxFormulaParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField(TaxFormulaParser.FieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaxFormulaParser#method}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethod(TaxFormulaParser.MethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaxFormulaParser#funcName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncName(TaxFormulaParser.FuncNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaxFormulaParser#params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParams(TaxFormulaParser.ParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaxFormulaParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(TaxFormulaParser.ParamContext ctx);
}