// Generated from D:/Works/dzf-zxkj/zxkj-modules/zxkj-platform/src/main/java/com/dzf/zxkj/platform/service/taxrpt/formula\TaxFormula.g4 by ANTLR 4.8
package com.dzf.zxkj.platform.service.taxrpt.formula;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TaxFormulaParser}.
 */
public interface TaxFormulaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TaxFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(TaxFormulaParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaxFormulaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(TaxFormulaParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaxFormulaParser#expr1}.
	 * @param ctx the parse tree
	 */
	void enterExpr1(TaxFormulaParser.Expr1Context ctx);
	/**
	 * Exit a parse tree produced by {@link TaxFormulaParser#expr1}.
	 * @param ctx the parse tree
	 */
	void exitExpr1(TaxFormulaParser.Expr1Context ctx);
	/**
	 * Enter a parse tree produced by {@link TaxFormulaParser#sobj}.
	 * @param ctx the parse tree
	 */
	void enterSobj(TaxFormulaParser.SobjContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaxFormulaParser#sobj}.
	 * @param ctx the parse tree
	 */
	void exitSobj(TaxFormulaParser.SobjContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaxFormulaParser#cobj}.
	 * @param ctx the parse tree
	 */
	void enterCobj(TaxFormulaParser.CobjContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaxFormulaParser#cobj}.
	 * @param ctx the parse tree
	 */
	void exitCobj(TaxFormulaParser.CobjContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaxFormulaParser#field}.
	 * @param ctx the parse tree
	 */
	void enterField(TaxFormulaParser.FieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaxFormulaParser#field}.
	 * @param ctx the parse tree
	 */
	void exitField(TaxFormulaParser.FieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaxFormulaParser#method}.
	 * @param ctx the parse tree
	 */
	void enterMethod(TaxFormulaParser.MethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaxFormulaParser#method}.
	 * @param ctx the parse tree
	 */
	void exitMethod(TaxFormulaParser.MethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaxFormulaParser#funcName}.
	 * @param ctx the parse tree
	 */
	void enterFuncName(TaxFormulaParser.FuncNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaxFormulaParser#funcName}.
	 * @param ctx the parse tree
	 */
	void exitFuncName(TaxFormulaParser.FuncNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaxFormulaParser#params}.
	 * @param ctx the parse tree
	 */
	void enterParams(TaxFormulaParser.ParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaxFormulaParser#params}.
	 * @param ctx the parse tree
	 */
	void exitParams(TaxFormulaParser.ParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaxFormulaParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(TaxFormulaParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaxFormulaParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(TaxFormulaParser.ParamContext ctx);
}