// Generated from D:/Works/dzf-zxkj/zxkj-modules/zxkj-platform/src/main/java/com/dzf/zxkj/platform/service/taxrpt/formula\TaxFormula.g4 by ANTLR 4.8
package com.dzf.zxkj.platform.service.taxrpt.formula;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TaxFormulaParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, OP1=5, OP2=6, CELL=7, INT=8, NUM=9, STR=10, 
		ID=11, WS=12;
	public static final int
		RULE_expr = 0, RULE_expr1 = 1, RULE_sobj = 2, RULE_cobj = 3, RULE_field = 4, 
		RULE_method = 5, RULE_funcName = 6, RULE_params = 7, RULE_param = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"expr", "expr1", "sobj", "cobj", "field", "method", "funcName", "params", 
			"param"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", "'.'", "','"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, "OP1", "OP2", "CELL", "INT", "NUM", "STR", 
			"ID", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "TaxFormula.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TaxFormulaParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ExprContext extends ParserRuleContext {
		public Expr1Context expr1() {
			return getRuleContext(Expr1Context.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode OP1() { return getToken(TaxFormulaParser.OP1, 0); }
		public TerminalNode OP2() { return getToken(TaxFormulaParser.OP2, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaxFormulaVisitor ) return ((TaxFormulaVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 0;
		enterRecursionRule(_localctx, 0, RULE_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CELL:
			case INT:
			case NUM:
			case STR:
			case ID:
				{
				setState(19);
				expr1();
				}
				break;
			case T__0:
				{
				setState(20);
				match(T__0);
				setState(21);
				expr(0);
				setState(22);
				match(T__1);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(34);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(32);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(26);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(27);
						match(OP1);
						setState(28);
						expr(5);
						}
						break;
					case 2:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(29);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(30);
						match(OP2);
						setState(31);
						expr(4);
						}
						break;
					}
					} 
				}
				setState(36);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Expr1Context extends ParserRuleContext {
		public TerminalNode INT() { return getToken(TaxFormulaParser.INT, 0); }
		public TerminalNode NUM() { return getToken(TaxFormulaParser.NUM, 0); }
		public TerminalNode STR() { return getToken(TaxFormulaParser.STR, 0); }
		public SobjContext sobj() {
			return getRuleContext(SobjContext.class,0);
		}
		public CobjContext cobj() {
			return getRuleContext(CobjContext.class,0);
		}
		public Expr1Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr1; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).enterExpr1(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).exitExpr1(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaxFormulaVisitor ) return ((TaxFormulaVisitor<? extends T>)visitor).visitExpr1(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Expr1Context expr1() throws RecognitionException {
		Expr1Context _localctx = new Expr1Context(_ctx, getState());
		enterRule(_localctx, 2, RULE_expr1);
		try {
			setState(42);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(37);
				match(INT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(38);
				match(NUM);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(39);
				match(STR);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(40);
				sobj();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(41);
				cobj(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SobjContext extends ParserRuleContext {
		public TerminalNode CELL() { return getToken(TaxFormulaParser.CELL, 0); }
		public TerminalNode ID() { return getToken(TaxFormulaParser.ID, 0); }
		public SobjContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sobj; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).enterSobj(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).exitSobj(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaxFormulaVisitor ) return ((TaxFormulaVisitor<? extends T>)visitor).visitSobj(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SobjContext sobj() throws RecognitionException {
		SobjContext _localctx = new SobjContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_sobj);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			_la = _input.LA(1);
			if ( !(_la==CELL || _la==ID) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CobjContext extends ParserRuleContext {
		public MethodContext method() {
			return getRuleContext(MethodContext.class,0);
		}
		public SobjContext sobj() {
			return getRuleContext(SobjContext.class,0);
		}
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public CobjContext cobj() {
			return getRuleContext(CobjContext.class,0);
		}
		public CobjContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cobj; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).enterCobj(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).exitCobj(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaxFormulaVisitor ) return ((TaxFormulaVisitor<? extends T>)visitor).visitCobj(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CobjContext cobj() throws RecognitionException {
		return cobj(0);
	}

	private CobjContext cobj(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		CobjContext _localctx = new CobjContext(_ctx, _parentState);
		CobjContext _prevctx = _localctx;
		int _startState = 6;
		enterRecursionRule(_localctx, 6, RULE_cobj, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(47);
				method();
				}
				break;
			case 2:
				{
				setState(48);
				sobj();
				setState(49);
				match(T__2);
				setState(50);
				method();
				}
				break;
			case 3:
				{
				setState(52);
				sobj();
				setState(53);
				match(T__2);
				setState(54);
				field();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(66);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(64);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
					case 1:
						{
						_localctx = new CobjContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_cobj);
						setState(58);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(59);
						match(T__2);
						setState(60);
						method();
						}
						break;
					case 2:
						{
						_localctx = new CobjContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_cobj);
						setState(61);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(62);
						match(T__2);
						setState(63);
						field();
						}
						break;
					}
					} 
				}
				setState(68);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class FieldContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(TaxFormulaParser.ID, 0); }
		public FieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).enterField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).exitField(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaxFormulaVisitor ) return ((TaxFormulaVisitor<? extends T>)visitor).visitField(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FieldContext field() throws RecognitionException {
		FieldContext _localctx = new FieldContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_field);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MethodContext extends ParserRuleContext {
		public FuncNameContext funcName() {
			return getRuleContext(FuncNameContext.class,0);
		}
		public ParamsContext params() {
			return getRuleContext(ParamsContext.class,0);
		}
		public MethodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).enterMethod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).exitMethod(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaxFormulaVisitor ) return ((TaxFormulaVisitor<? extends T>)visitor).visitMethod(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MethodContext method() throws RecognitionException {
		MethodContext _localctx = new MethodContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_method);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			funcName();
			setState(72);
			match(T__0);
			setState(73);
			params();
			setState(74);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuncNameContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(TaxFormulaParser.ID, 0); }
		public FuncNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).enterFuncName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).exitFuncName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaxFormulaVisitor ) return ((TaxFormulaVisitor<? extends T>)visitor).visitFuncName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncNameContext funcName() throws RecognitionException {
		FuncNameContext _localctx = new FuncNameContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_funcName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParamsContext extends ParserRuleContext {
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public ParamsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_params; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).enterParams(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).exitParams(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaxFormulaVisitor ) return ((TaxFormulaVisitor<? extends T>)visitor).visitParams(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamsContext params() throws RecognitionException {
		ParamsContext _localctx = new ParamsContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_params);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << CELL) | (1L << INT) | (1L << NUM) | (1L << STR) | (1L << ID))) != 0)) {
				{
				setState(78);
				param();
				setState(83);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3) {
					{
					{
					setState(79);
					match(T__3);
					setState(80);
					param();
					}
					}
					setState(85);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParamContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).enterParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaxFormulaListener ) ((TaxFormulaListener)listener).exitParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaxFormulaVisitor ) return ((TaxFormulaVisitor<? extends T>)visitor).visitParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 0:
			return expr_sempred((ExprContext)_localctx, predIndex);
		case 3:
			return cobj_sempred((CobjContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 4);
		case 1:
			return precpred(_ctx, 3);
		}
		return true;
	}
	private boolean cobj_sempred(CobjContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 2);
		case 3:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\16]\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\5\2\33\n\2\3\2\3\2\3\2\3\2\3\2\3\2\7\2#\n\2\f\2\16\2&\13"+
		"\2\3\3\3\3\3\3\3\3\3\3\5\3-\n\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\5\5;\n\5\3\5\3\5\3\5\3\5\3\5\3\5\7\5C\n\5\f\5\16\5F\13\5\3"+
		"\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\t\7\tT\n\t\f\t\16\tW\13\t"+
		"\5\tY\n\t\3\n\3\n\3\n\2\4\2\b\13\2\4\6\b\n\f\16\20\22\2\3\4\2\t\t\r\r"+
		"\2`\2\32\3\2\2\2\4,\3\2\2\2\6.\3\2\2\2\b:\3\2\2\2\nG\3\2\2\2\fI\3\2\2"+
		"\2\16N\3\2\2\2\20X\3\2\2\2\22Z\3\2\2\2\24\25\b\2\1\2\25\33\5\4\3\2\26"+
		"\27\7\3\2\2\27\30\5\2\2\2\30\31\7\4\2\2\31\33\3\2\2\2\32\24\3\2\2\2\32"+
		"\26\3\2\2\2\33$\3\2\2\2\34\35\f\6\2\2\35\36\7\7\2\2\36#\5\2\2\7\37 \f"+
		"\5\2\2 !\7\b\2\2!#\5\2\2\6\"\34\3\2\2\2\"\37\3\2\2\2#&\3\2\2\2$\"\3\2"+
		"\2\2$%\3\2\2\2%\3\3\2\2\2&$\3\2\2\2\'-\7\n\2\2(-\7\13\2\2)-\7\f\2\2*-"+
		"\5\6\4\2+-\5\b\5\2,\'\3\2\2\2,(\3\2\2\2,)\3\2\2\2,*\3\2\2\2,+\3\2\2\2"+
		"-\5\3\2\2\2./\t\2\2\2/\7\3\2\2\2\60\61\b\5\1\2\61;\5\f\7\2\62\63\5\6\4"+
		"\2\63\64\7\5\2\2\64\65\5\f\7\2\65;\3\2\2\2\66\67\5\6\4\2\678\7\5\2\28"+
		"9\5\n\6\29;\3\2\2\2:\60\3\2\2\2:\62\3\2\2\2:\66\3\2\2\2;D\3\2\2\2<=\f"+
		"\4\2\2=>\7\5\2\2>C\5\f\7\2?@\f\3\2\2@A\7\5\2\2AC\5\n\6\2B<\3\2\2\2B?\3"+
		"\2\2\2CF\3\2\2\2DB\3\2\2\2DE\3\2\2\2E\t\3\2\2\2FD\3\2\2\2GH\7\r\2\2H\13"+
		"\3\2\2\2IJ\5\16\b\2JK\7\3\2\2KL\5\20\t\2LM\7\4\2\2M\r\3\2\2\2NO\7\r\2"+
		"\2O\17\3\2\2\2PU\5\22\n\2QR\7\6\2\2RT\5\22\n\2SQ\3\2\2\2TW\3\2\2\2US\3"+
		"\2\2\2UV\3\2\2\2VY\3\2\2\2WU\3\2\2\2XP\3\2\2\2XY\3\2\2\2Y\21\3\2\2\2Z"+
		"[\5\2\2\2[\23\3\2\2\2\13\32\"$,:BDUX";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}