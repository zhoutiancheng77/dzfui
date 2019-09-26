package com.dzf.zxkj.platform.services.common;


import com.dzf.zxkj.base.exception.DZFWarpException;

import java.util.ArrayList;
import java.util.Arrays;

public class InSqlBatchCaller
{
	ArrayList pks = new ArrayList();
	public final int GROUP_COUNT = 500;
	int group_size = GROUP_COUNT;
	
	/**
	 * @param pks
	 */
	public InSqlBatchCaller(ArrayList pks)
	{
		super();
		if(pks!=null);
			this.pks = pks;
	}	
	
	/**
	 * @param pks
	 * @param group_size
	 */
	public InSqlBatchCaller(ArrayList pks, int group_size)
	{
		super();
		if(pks!=null);
			this.pks = pks;
		if(group_size>10)
			this.group_size = group_size;
		else
			this.group_size = GROUP_COUNT;
	}
	
	/**
	 * @param pks
	 */
	public InSqlBatchCaller(String[] pks)
	{
		if(pks!=null)
			this.pks.addAll(Arrays.asList(pks));
	}

	public InSqlBatchCaller(String[] pks, int group_size)
	{
		if(pks!=null)
			this.pks.addAll(Arrays.asList(pks));
		if(group_size>10)
			this.group_size = group_size;
		else
			this.group_size = GROUP_COUNT;		
	}

	public Object execute(IInSqlBatchCallBack callBack) throws DZFWarpException {
		Object result = null;
		int n = 0;
		StringBuffer buf = null;
		for (int i = 0; i < pks.size(); i++)
		{
			n++;
			if(n==1)
			{
				buf = new StringBuffer();
				buf.append("(");
			}
			buf.append("'");
			buf.append(pks.get(i));
			if(n!=group_size&&i!=pks.size()-1)
			{
				buf.append("',");
			}
			else
			{
				buf.append("')");
				result = callBack.doWithInSql(buf.toString());
				n = 0;
			}
			
		}
		return result;
	}		
		
}
