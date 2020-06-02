package com.dzf.zxkj.app.service;


import com.dzf.zxkj.app.model.report.AppFzChVo;

public interface IStockQryService {

    public AppFzChVo getStockResvo(String startDate, String enddate, String pk_corp);
}
