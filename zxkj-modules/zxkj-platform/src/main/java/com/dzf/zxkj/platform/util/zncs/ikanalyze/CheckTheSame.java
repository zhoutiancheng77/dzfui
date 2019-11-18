package com.dzf.zxkj.platform.util.zncs.ikanalyze;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

/**
 * <b>Description:</b>中文分词</br>
 * 
 * @author: 两只蜗牛
 * @Date: 2015-1-22下午1:20:34
 * @version 1.0
 */
public class CheckTheSame {

	/**
	 * 分词
	 * 
	 * @author: Administrator
	 */
	public static Vector<String> participle(String str) {
		Vector<String> str1 = new Vector<String>();
		try {
			StringReader reader = new StringReader(str);
			IKSegmenter ik = new IKSegmenter(reader, false);//当为true时，分词器进行最大词长切�?
			Lexeme lexeme = null;
			while ((lexeme = ik.next()) != null) {
				str1.add(lexeme.getLexemeText());
			}
			if (str1.size() == 0) {
				return null;
			}
//			System.out.println("str分词后：" + str1);
		} catch (IOException e1) {
//			System.out.println();
//			e1.printStackTrace();
		}
		return str1;
	}

	public static void main(String[] args) {
		//分词
		Vector<String> strs1 = participle("") ;
		Vector<String> strs2 = participle("") ;
		double same = 0 ;
		try {
			same = IKAnalyzerUtil.getSimilarity( strs1 , strs2 );
		} catch (Exception e) {
//			System.out.println( e.getMessage() );
//			e.printStackTrace();
		}
//		System.out.println( "相似度：" + same );
	}
}
