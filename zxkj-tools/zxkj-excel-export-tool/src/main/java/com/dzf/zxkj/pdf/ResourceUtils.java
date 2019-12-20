package com.dzf.zxkj.pdf;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class ResourceUtils {
public interface ICloseAction<T>{
	public Object doAction(T out)throws Exception;
}
public static Object doSOStreamAExec(HttpServletResponse hsr, ICloseAction<ServletOutputStream> action) throws Exception{
	ServletOutputStream out=null;
	Object obj=null;
	try{
		out=hsr.getOutputStream();
		obj=action.doAction(out);
	}finally{
		if(out!=null){
			out.close();
		}
	}
	return obj;
}
}