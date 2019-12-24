package com.dzf.zxkj.platform.service.taxrpt.shandong;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * 中税webservice
 */
@WebService
public interface IYjsbWebService {
	/**
	 * 纳税人信息登录验证接口
	 * @param nsrsbh
	 * @param supplier
	 * @param ywlx
	 * @param yzBwXml
	 * @param sing
	 * @return
	 */
	@WebMethod
	@WebResult(name = "resultMsg")
	public String yzNsrxx(@WebParam(name = "nsrsbh") String nsrsbh, @WebParam(name = "supplier") String supplier,
                          @WebParam(name = "ywlx") String ywlx, @WebParam(name = "yzBwXml") String yzBwXml,
                          @WebParam(name = "sign") String sing);

	/**
	 * 查询本期应申报清册
	 * @param nsrsbh
	 * @param supplier
	 * @param ywlx
	 * @param sbqcCxBwXml
	 * @param sing
	 * @param token
	 * @return
	 */
	@WebMethod
	@WebResult(name = "resultMsg")
	public String sbqcCx(@WebParam(name = "nsrsbh") String nsrsbh, @WebParam(name = "supplier") String supplier,
                         @WebParam(name = "ywlx") String ywlx, @WebParam(name = "sbqcCxBwXml") String sbqcCxBwXml,
                         @WebParam(name = "sign") String sing, @WebParam(name = "token") String token);

	/**
	 * 申报报文上传接口
	 * @param nsrsbh
	 * @param supplier
	 * @param ywlx
	 * @param yjsbBwXml
	 * @param sing
	 * @param token
	 * @return
	 */
	@WebMethod
	@WebResult(name = "resultMsg")
	public String yjsbBw(@WebParam(name = "nsrsbh") String nsrsbh, @WebParam(name = "supplier") String supplier,
                         @WebParam(name = "ywlx") String ywlx, @WebParam(name = "yjsbBwXml") String yjsbBwXml,
                         @WebParam(name = "sign") String sing, @WebParam(name = "token") String token);

	/**
	 * 生成签名信息接口
	 * @param privatekey
	 * @param sign
	 * @return
	 */
	@WebMethod
	@WebResult(name = "resultMsg")
	public String setSign(@WebParam(name = "privatekey") String privatekey, @WebParam(name = "sign") String sign);

}