package com.dzf.zxkj.app.service.ticket;


import com.dzf.zxkj.app.model.req.BwBusiReqBeanVo;
import com.dzf.zxkj.app.model.resp.bean.BwBusiRespBean;
import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.ticket.InvalidInvoiceResultBean;
import com.dzf.zxkj.app.model.ticket.InvoiceHVO;
import com.dzf.zxkj.app.model.ticket.InvoiceResultBean;
import com.dzf.zxkj.app.model.ticket.TaxManageInfoVO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;

/**
 * 百旺接口信息
 * 
 * @author zhangj
 *
 */
public interface IAppBwTicket {

//	/**
//	 * 生成凭证信息
//	 *
//	 * @param pk_corp
//	 * @param account_id
//	 * @param invoicevo
//	 * @throws DZFWarpException
//	 */
//	public InvoiceHVO genInvoiceFromBwTicket(String pk_corp, String account_id, InvoiceResultBean.ResultBean.InvoiceBean invoicevo)
//			throws DZFWarpException;
//
//	/**
//	 * 作废发票
//	 *
//	 * @param pk_corp
//	 * @param account_id
//	 */
//	public void saveDelVoucher(String pk_corp, String account_id, InvalidInvoiceResultBean.ResultBean.InvoiceBean.InvalidInfoBean.InvalidInfoList invalidbean,
//                               InvoiceResultBean.ResultBean.InvoiceBean.SalesUnitInfoBean salebean) throws DZFWarpException;
//
//	/**
//	 * 生成图片信息
//	 *
//	 * @param url
//	 * @param pk_corp
//	 * @param invoicevo
//	 * @throws DZFWarpException
//	 */
//	public ImageGroupVO imageGenFromBwTicket(String url, String pk_corp, String account_id, InvoiceHVO hvo)
//			throws DZFWarpException;
//
//	/**
//	 * 生成凭证通过百望的发票信息
//	 *
//	 * @param pk_corp
//	 * @param vo
//	 * @param userid
//	 * @param pk_image_group
//	 * @throws DZFWarpException
//	 */
//	public void genVoucherFromBwTicket(String pk_corp, InvoiceHVO vo, String userid, String pk_image_group)
//			throws DZFWarpException;
//
//
//	/**
//	 * 生成公司信息，同时自动登录成功
//	 * @param account_id
//	 * @param bw_login
//	 * @throws DZFWarpException
//	 */
//	public LoginResponseBeanVO genCorpFromBwCorp(String account_id, String iot, String bw_login, String isautologin,
//												 String pk_corp_father, String vdecimsg, String sourcesys) throws DZFWarpException;
//	/**
//	 * 保存抄报信息
//	 * @param account_id
//	 * @param pk_corp
//	 * @param manageInfo
//	 * @throws DZFWarpException
//	 */
//	public void saveManageInfo(String account_id, String pk_corp, TaxManageInfoVO[] manageInfo) throws DZFWarpException;
//
//	/**
//	 * iot 登录
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public BwBusiRespBean loginFromIot(String iot, String pwd, String vdevicemsg, String systype)throws DZFWarpException;
//
//
//	/**
//	 * 百望后台iot注册
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public BwBusiRespBean saveBwIotRegisterCorp(BwBusiReqBeanVo bwreqvo) throws DZFWarpException;
//
//
//	/**
//	 * 百望修改密码
//	 * @param bwreqvo
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public BwBusiRespBean updatePwd(BwBusiReqBeanVo bwreqvo) throws DZFWarpException;
//
//
//	/**
//	 * 修改百望信息 iot，税号，公司名称
//	 * @param bwreqvo
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public BwBusiRespBean updateBwCorp(BwBusiReqBeanVo bwreqvo)throws DZFWarpException;
//
}
