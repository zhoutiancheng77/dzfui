package com.dzf.zxkj.platform.util;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.base.utils.CodeUtils1;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 根据vo对象来进行解密
 * @author zhangj
 *
 */
@Slf4j
public class QueryDeCodeUtils {
	/**
	 * 
	 * @param columnkeys 需要加密解密的字段
	 * @param vos 需要传的vo数组
	 * @param mode 模式，0 加密，1解密
	 * @return
	 */
	public static List<? extends SuperVO> decKeyUtils(String[] columnkeys, List<? extends SuperVO> vos, int mode){
		Set<String> columndecs = new HashSet<String>();
		int len=vos==null?0:vos.size();
		if(len>0){
			try {
				if(columnkeys == null || columnkeys.length == 0){
					return null;
				}
				Object obj;
				for(int i=0;i<len;i++){
					for(String columkey:columnkeys){
						obj=vos.get(i).getAttributeValue(columkey);
						if(obj!=null){
							columndecs.add((String)obj);
						}
					}
				}
				String keyname =null;
				//数据解密
				HashMap<String, String> mapvalue = new HashMap<String,String>();
				for(String key:columndecs){
					if(mode == 0){
						 keyname = CodeUtils1.enCode(key);
						mapvalue.put(key, keyname);
					}else{
						 keyname = CodeUtils1.deCode(key);
						mapvalue.put(key, keyname);
					}
				}
				String value =null;
				String decvalue = null;
				for(int i=0;i<len;i++){
					for(String columnkey:columnkeys){
						obj=vos.get(i).getAttributeValue(columnkey);
						if(obj!=null){
							 value = (String) obj;
							 decvalue =  mapvalue.get(value);
							vos.get(i).setAttributeValue(columnkey, decvalue);
						}
					}
				}
			} catch (Exception e) {
				log.error("错误",e);
			}
		}
		return vos;
	}
	public static  SuperVO[] decKeyUtils(String[] columnkeys,SuperVO[] vos,int mode){
		Set<String> columndecs = new HashSet<String>();
		int len=vos==null?0:vos.length;
		if(len>0){
			try {
				if(columnkeys == null || columnkeys.length == 0){
					return null;
				}
				Object obj;
				for(int i=0;i<len;i++){
					for(String columkey:columnkeys){
						obj=vos[i].getAttributeValue(columkey);
						if(obj!=null){
							columndecs.add((String)obj);
						}
					}
				}
				String keyname =null;
				//数据解密
				HashMap<String, String> mapvalue = new HashMap<String,String>();
				for(String key:columndecs){
					if(mode == 0){
						 keyname = CodeUtils1.enCode(key);
						mapvalue.put(key, keyname);
					}else{
						 keyname = CodeUtils1.deCode(key);
						mapvalue.put(key, keyname);
					}
				}
				String value =null;
				String decvalue = null;
				for(int i=0;i<len;i++){
					for(String columnkey:columnkeys){
						if(vos[i].getAttributeValue(columnkey)!=null){
							 value = (String) vos[i].getAttributeValue(columnkey);
							 decvalue =  mapvalue.get(value);
							vos[i].setAttributeValue(columnkey, decvalue);
						}
					}
				}
			} catch (Exception e) {
				log.error("错误",e);
			}
		}
		return vos;
	}
	
	/**
	 * 
	 * 加密解密包含子vo的数据
	 * @param columnkeys 需要加密解密的字段
	 * @param vos 需要传的vo数组
	 * @param mode 模式，0 加密，1解密
	 * @return
	 */
	public static  SuperVO[] decKeyConSubVoUtils(String[] columnkeys,SuperVO[] vos,int mode){
		//处理子表数据
		SuperVO[] reselefvos   = decKeyUtils(columnkeys, vos , mode);
		for(SuperVO tempvo:reselefvos){
			chileHandle(columnkeys,tempvo,mode);
		}
		return reselefvos;
	}



	private static void chileHandle(String[] columnkeys, SuperVO tempvo, int mode) {
		if (tempvo.getChildren() == null || tempvo.getChildren().length == 0) {
			return;
		} else {
			for (SuperVO childvo : tempvo.getChildren()) {
				decKeyUtils(columnkeys, new SuperVO[]{childvo} , mode);
				chileHandle(columnkeys, childvo, mode);
			}
		}
	}
	
	/**
     * 单个VO对象加密解密
     * @param columnkeys 需要加密解密的字段
     * @param vo 需要传的vo
     * @param mode 模式，0 加密，1解密
     * @return
     */
	public static  SuperVO decKeyUtil(String[] columnkeys,SuperVO vo,int mode){
        Set<String> columndecs = new HashSet<String>();
        if(vo != null){
            try {
                if(columnkeys == null || columnkeys.length == 0){
                    return null;
                }
                Object obj;
                for(String columkey:columnkeys){
                    obj=vo.getAttributeValue(columkey);
                    if(obj!=null){
                        columndecs.add((String)obj);
                    }
                }
                String keyname =null;
                //数据解密
                HashMap<String, String> mapvalue = new HashMap<String,String>();
                for(String key:columndecs){
                    if(mode == 0){
                        keyname = CodeUtils1.enCode(key);
                        mapvalue.put(key, keyname);
                    }else{
                        keyname = CodeUtils1.deCode(key);
                        mapvalue.put(key, keyname);
                    }
                }
                String value =null;
                String decvalue = null;
                    for(String columnkey:columnkeys){
                        if(vo.getAttributeValue(columnkey)!=null){
                             value = (String) vo.getAttributeValue(columnkey);
                             decvalue =  mapvalue.get(value);
                            vo.setAttributeValue(columnkey, decvalue);
                        }
                    }
            } catch (Exception e) {
                log.error("错误",e);
            }
        }
        return vo;
    }
}
