package com.dzf.zxkj.platform.util.zncs;

import java.util.Vector;

import com.dzf.zxkj.platform.util.zncs.ikanalyze.CheckTheSame;
import com.dzf.zxkj.platform.util.zncs.ikanalyze.IKAnalyzerUtil;
import org.jboss.logging.Logger;


/**
 *
 */
public class HazyMatchKeyWord {

	Logger log = Logger.getLogger(this.getClass());
	public double compareTowWord(String key1, String key2) {
		double same1 = 0.0;
		if (key1 == null || key2 == null || "".equals(key1) || "".equals(key2))
			return same1;
		Vector<String> strs1 = CheckTheSame.participle(key1);
		Vector<String> strs2 = CheckTheSame.participle(key2);

		// 这里zpm重新处理。
		// strs1.remove("费");
		// strs2.remove("费");
		// strs1.remove("税");
		// strs2.remove("税");
		// strs1.remove("应");
		// strs2.remove("应");
		// strs1.remove("交");
		// strs2.remove("交");
		// strs1.remove("应交");
		// strs2.remove("应交");
		
		if(strs1 == null || strs1.size()==0 ||strs2 == null || strs2.size()==0)
			return same1;
		try {
			same1 = IKAnalyzerUtil.getSimilarity(strs1, strs2);
		} catch (Exception e) {
			log.info(e);
//			e.printStackTrace();
		}
		return same1;
	}

}
