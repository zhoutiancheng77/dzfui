// Generated from D:/Works/dzf-zxkj/zxkj-modules/zxkj-platform/src/main/java/com/dzf/zxkj/platform/service/taxrpt/formula\TaxFormula.g4 by ANTLR 4.8
package com.dzf.zxkj.platform.service.taxrpt.formula;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TaxFormulaLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, OP1=5, OP2=6, CELL=7, INT=8, NUM=9, STR=10, 
		ID=11, WS=12;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "OP1", "OP2", "CELL", "INT", "NUM", "STR", 
			"ID", "WS"
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


	public TaxFormulaLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "TaxFormula.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\16a\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3"+
		"\b\3\b\3\b\5\b+\n\b\3\b\3\b\3\b\5\b\60\n\b\3\t\6\t\63\n\t\r\t\16\t\64"+
		"\3\n\7\n8\n\n\f\n\16\n;\13\n\3\n\3\n\7\n?\n\n\f\n\16\nB\13\n\3\13\3\13"+
		"\7\13F\n\13\f\13\16\13I\13\13\3\13\3\13\3\13\7\13N\n\13\f\13\16\13Q\13"+
		"\13\3\13\5\13T\n\13\3\f\6\fW\n\f\r\f\16\fX\3\r\6\r\\\n\r\r\r\16\r]\3\r"+
		"\3\r\2\2\16\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\3"+
		"\2\f\4\2,,\61\61\5\2((--//\4\2TTtt\4\2C\\c|\4\2EEee\3\2\62;\3\2$$\3\2"+
		"))\b\2))\62;C\\aac|~~\4\2\13\13\"\"\2j\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2"+
		"\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\3\33\3\2\2\2\5\35\3"+
		"\2\2\2\7\37\3\2\2\2\t!\3\2\2\2\13#\3\2\2\2\r%\3\2\2\2\17\'\3\2\2\2\21"+
		"\62\3\2\2\2\239\3\2\2\2\25S\3\2\2\2\27V\3\2\2\2\31[\3\2\2\2\33\34\7*\2"+
		"\2\34\4\3\2\2\2\35\36\7+\2\2\36\6\3\2\2\2\37 \7\60\2\2 \b\3\2\2\2!\"\7"+
		".\2\2\"\n\3\2\2\2#$\t\2\2\2$\f\3\2\2\2%&\t\3\2\2&\16\3\2\2\2\'*\t\4\2"+
		"\2(+\5\21\t\2)+\t\5\2\2*(\3\2\2\2*)\3\2\2\2+,\3\2\2\2,/\t\6\2\2-\60\5"+
		"\21\t\2.\60\t\5\2\2/-\3\2\2\2/.\3\2\2\2\60\20\3\2\2\2\61\63\t\7\2\2\62"+
		"\61\3\2\2\2\63\64\3\2\2\2\64\62\3\2\2\2\64\65\3\2\2\2\65\22\3\2\2\2\66"+
		"8\t\7\2\2\67\66\3\2\2\28;\3\2\2\29\67\3\2\2\29:\3\2\2\2:<\3\2\2\2;9\3"+
		"\2\2\2<@\7\60\2\2=?\t\7\2\2>=\3\2\2\2?B\3\2\2\2@>\3\2\2\2@A\3\2\2\2A\24"+
		"\3\2\2\2B@\3\2\2\2CG\7$\2\2DF\n\b\2\2ED\3\2\2\2FI\3\2\2\2GE\3\2\2\2GH"+
		"\3\2\2\2HJ\3\2\2\2IG\3\2\2\2JT\7$\2\2KO\7)\2\2LN\n\t\2\2ML\3\2\2\2NQ\3"+
		"\2\2\2OM\3\2\2\2OP\3\2\2\2PR\3\2\2\2QO\3\2\2\2RT\7)\2\2SC\3\2\2\2SK\3"+
		"\2\2\2T\26\3\2\2\2UW\t\n\2\2VU\3\2\2\2WX\3\2\2\2XV\3\2\2\2XY\3\2\2\2Y"+
		"\30\3\2\2\2Z\\\t\13\2\2[Z\3\2\2\2\\]\3\2\2\2][\3\2\2\2]^\3\2\2\2^_\3\2"+
		"\2\2_`\b\r\2\2`\32\3\2\2\2\r\2*/\649@GOSX]\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}