package com.dzf.zxkj.platform.services.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.ComboBoxVO;
import com.dzf.zxkj.platform.model.sys.YntArea;

import java.util.ArrayList;
import java.util.List;

public interface IAreaSearch {
    YntArea query() throws DZFWarpException;

    /**
     * 查询开通服务区域
     *
     * @return
     * @throws DZFWarpException
     */
    YntArea queryOpenArea() throws DZFWarpException;

    /**
     * 查询区域信息
     *
     * @param parenter_id
     * @return
     * @throws DZFWarpException
     */
    List queryArea(String parenter_id) throws DZFWarpException;

    /**
     * 查询报税区域
     *
     * @throws DZFWarpException
     */
    List queryBsArea() throws DZFWarpException;


    /**
     * 查询网站区域数据
     *
     * @param parenter_id
     * @return
     * @throws DZFWarpException
     */
    List queryWebArea(String parenter_id) throws DZFWarpException;

    /**
     * 查询地区：下拉框使用
     *
     * @param parenter_id
     * @return
     * @throws DZFWarpException
     */
    ArrayList<ComboBoxVO> queryComboxArea(String parenter_id) throws DZFWarpException;

    /**
     * 查询地区：下拉框使用
     * 纳税信息维护使用
     *
     * @param parenter_id
     * @return
     * @throws DZFWarpException
     */
    ArrayList<ComboBoxVO> queryComArea(String parenter_id) throws DZFWarpException;
}
