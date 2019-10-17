package com.dzf.zxkj.platform.service.pjgl;


import com.dzf.zxkj.base.exception.DZFWarpException;

//  重新识别生成凭证
public interface ICreatePZByRecogService {

	public boolean creatPZ(String pk_corp, String[] imageKeys) throws DZFWarpException;
}
