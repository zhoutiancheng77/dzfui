package com.dzf.zxkj.platform.controller.zncs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.cloud.redis.lock.RedissonDistributedLock;
import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.DZFMapUtil;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.pjgl.InvoiceParamVO;
import com.dzf.zxkj.platform.model.pjgl.TicketNssbhVO;
import com.dzf.zxkj.platform.model.pjgl.VatGoosInventoryRelationVO;
import com.dzf.zxkj.platform.model.pjgl.VatInvoiceSetVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.model.zncs.*;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.bdset.IPersonalSetService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.glic.impl.CheckInventorySet;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IDcpzService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.zncs.*;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.dzf.zxkj.platform.util.zncs.ICaiFangTongConstant;
import com.dzf.zxkj.platform.util.zncs.OcrUtil;
import com.dzf.zxkj.platform.util.zncs.VatExportUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/zncs/gl_vatsalinvact2")
public class VATSaleInvoice2Controller extends BaseController {

    @Autowired
    private IVATSaleInvoice2Service gl_vatsalinvserv2;
    @Autowired
    private IBankStatement2Service gl_yhdzdserv2;
    @Autowired
    private IVATInComInvoice2Service gl_vatincinvact2;
    @Autowired
    private ISchedulCategoryService schedulCategoryService;
    //	@Autowired
//	private ITaxitemsetService taxitemserv;
    @Autowired
    private IVatInvoiceService vatinvoiceserv;
    @Autowired
    private IZncsVoucher zncsVoucher;
    //	@Autowired
//	private IInventoryService inventoryserv;
//	@Autowired
//	private IAuxiliaryAccountService gl_fzhsserv;
    @Autowired
    private IPersonalSetService gl_gxhszserv;
    @Autowired
    private IParameterSetService parameterserv;
    @Autowired
    private IDcpzService dcpzjmbserv;
    @Autowired
    private ICorpService corpService;
//	@Autowired
//	private ICbComconstant gl_cbconstant;

    @Autowired
    private CheckInventorySet inventory_setcheck;
    @Autowired
    ICpaccountService gl_cpacckmserv;
    @Autowired
    private IInventoryAccSetService gl_ic_invtorysetserv = null;
    @Autowired
    private IInterfaceBill ocrinterface;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private RedissonDistributedLock redissonDistributedLock;

    @RequestMapping("/queryInfo")
    public ReturnData<Json> queryInfo(@RequestBody Map<String,String> param) {
        Json json = new Json();
        try {
            String head = param.get("para");
            String sort = param.get("sort");
            String order = param.get("order");
            String spage = param.get("page");
            String srows = param.get("rows");
            Integer page = null;
            Integer rows = null;
            if(!StringUtil.isEmpty(spage)&&!StringUtil.isEmpty(srows)){
                page = Integer.parseInt(spage);
                rows = Integer.parseInt(srows);
            }else{
                throw new BusinessException("请输入查询页数和条数");
            }
            //查询并分页
            if (StringUtil.isEmpty(SystemUtil.getLoginCorpId())) {//corpVo.getPrimaryKey()
                throw new BusinessException("出现数据无权问题！");
            }

            InvoiceParamVO paramvo = getQueryParamVO(head);

            List<VATSaleInvoiceVO2> list = gl_vatsalinvserv2.quyerByPkcorp(paramvo, sort, order);
            //list变成数组
            json.setTotal((long) (list == null ? 0 : list.size()));
            //分页
            VATSaleInvoiceVO2[] vos = null;
            if (list != null && list.size() > 0) {
                vos = getPagedZZVOs(list.toArray(new VATSaleInvoiceVO2[0]), page, rows);
                for (VATSaleInvoiceVO2 vo : vos) {
                    //处理改版前的图片路径，将/gl/gl_imgview!search.action替换成/zncs/gl_imgview/search
                    if(!StringUtil.isEmpty(vo.getImgpath())&&vo.getImgpath().contains("/gl/gl_imgview!search.action")){
                        vo.setImgpath(vo.getImgpath().replace("/gl/gl_imgview!search.action","/zncs/gl_imgview/search"));
                    }
                    //未制证，有图片来源，有业务类型，则不显示业务类型，通过票据工作台处理
                    if (StringUtil.isEmpty(vo.getPk_tzpz_h()) && !StringUtil.isEmpty(vo.getImgpath()) && (!StringUtil.isEmpty(vo.getPk_model_h()) || !StringUtil.isEmpty(vo.getBusitypetempname()))) {
                        vo.setPk_model_h(null);
                        vo.setBusisztypecode(null);
                        vo.setBusitypetempname(null);
                    }
                }
            }
            log.info("查询成功！");
            json.setRows(vos == null ? new ArrayList<VATSaleInvoiceVO2>() : Arrays.asList(vos));
            json.setHead(getHeadVO(list));
            json.setSuccess(true);
            json.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
        }

        return ReturnData.ok().data(json);
    }

    @RequestMapping("/queryInfoByID")
    public ReturnData<Json> queryInfoByID(String id) {
        Json json = new Json();

        try {
            VATSaleInvoiceVO2 hvo = gl_vatsalinvserv2.queryByID(id);

            json.setData(hvo);
            json.setSuccess(true);
            json.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
        }

        return ReturnData.ok().data(json);
    }

    private InvoiceParamVO getQueryParamVO(String head) {


        InvoiceParamVO paramvo = JsonUtils.deserialize(head, InvoiceParamVO.class);
        if (paramvo == null) {
            paramvo = new InvoiceParamVO();
        }

        paramvo.setPk_corp(SystemUtil.getLoginCorpId());

        return paramvo;
    }

    private VATSaleInvoiceVO2[] getPagedZZVOs(VATSaleInvoiceVO2[] vos, int page, int rows) {
        int beginIndex = rows * (page - 1);
        int endIndex = rows * page;
        if (endIndex >= vos.length) {//防止endIndex数组越界
            endIndex = vos.length;
        }
        vos = Arrays.copyOfRange(vos, beginIndex, endIndex);
        return vos;
    }

    //修改保存
    @RequestMapping("/saveOrUpdate")
    public ReturnData<Json> saveOrUpdate(@RequestBody Map<String,String> param) {
        String msg = "";//记录日志
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            String head = param.get("header");
            String body = param.get("body");
            Map<String, VATSaleInvoiceVO2[]> sendData = new HashMap<String, VATSaleInvoiceVO2[]>();
            VATSaleInvoiceVO2 headvo = JsonUtils.deserialize(head, VATSaleInvoiceVO2.class);
            VATSaleInvoiceBVO2[] bodyvos = JsonUtils.deserialize(body, VATSaleInvoiceBVO2[].class);
            if (!StringUtil.isEmptyWithTrim(headvo.getKhmc())) {
                //处理特殊字符
                headvo.setKhmc(OcrUtil.filterCorpName(headvo.getKhmc()).trim());
            }
            if (!StringUtil.isEmptyWithTrim(headvo.getXhfmc())) {
                //处理特殊字符
                headvo.setXhfmc(OcrUtil.filterCorpName(headvo.getXhfmc()).trim());
            }

            bodyvos = filterBlankBodyVos(bodyvos);
            checkJEValid(headvo, bodyvos);
            setDefultValue(headvo, bodyvos);
            headvo.setChildren(bodyvos);
            saveOrUpdateCorpReference(headvo);//保存公司参照
            if (!StringUtils.isEmpty(headvo.getPk_vatsaleinvoice())) {

                gl_vatsalinvserv2.checkvoPzMsg(headvo.getPk_vatsaleinvoice());
            }
            msg += SystemUtil.getLoginUserVo().getUser_name();

            if (StringUtil.isEmpty(headvo.getPrimaryKey())) {
                sendData.put("adddocvos", new VATSaleInvoiceVO2[]{headvo});

                msg += "新增发票号码(" + headvo.getFp_hm() + ")的销项发票";
            } else {
                sendData.put("upddocvos", new VATSaleInvoiceVO2[]{headvo});

                msg += "修改发票号码(" + headvo.getFp_hm() + ")的销项发票";
            }
            //设置结算方式
            gl_vatincinvact2.updateCategoryset(new DZFBoolean(false), headvo.getPk_model_h(), headvo.getBusisztypecode(), headvo.getPk_basecategory(), SystemUtil.getLoginCorpId(), null, null, null, null);
            VATSaleInvoiceVO2[] addvos = gl_vatsalinvserv2.updateVOArr(pk_corp, sendData);

            json.setStatus(200);
            json.setRows(addvos);
            json.setSuccess(true);
            json.setMsg("保存成功！");

            msg += "成功";
        } catch (Exception e) {
            printErrorLog(json, e, "保存失败");

            if (!StringUtil.isEmpty(msg)) {
                msg += "失败";
            }
        }

        if (StringUtil.isEmpty(msg)) {
            msg = "销项发票编辑";
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, msg, ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private VATSaleInvoiceBVO2[] filterBlankBodyVos(VATSaleInvoiceBVO2[] bodyvos) {
        List<VATSaleInvoiceBVO2> filterList = new ArrayList<VATSaleInvoiceBVO2>();
        if (bodyvos != null && bodyvos.length > 0) {
            VATSaleInvoiceBVO2 bvo = null;
            for (int i = 0; i < bodyvos.length; i++) {
                bvo = bodyvos[i];
                //处理前端传过来的空数据，过滤
                if(StringUtils.isEmpty(bvo.getBspmc())&&StringUtils.isEmpty(bvo.getPk_billcategory())){
                    continue;
                }
                if (!StringUtil.isEmptyWithTrim(bvo.getBspmc())) {
                    //处理特殊字符
                    bvo.setBspmc(OcrUtil.filterCorpName(bvo.getBspmc()).trim());
                }

                if (StringUtil.isEmpty(bvo.getBspmc())//商品名称
                        && StringUtil.isEmpty(bvo.getInvspec())
                        && StringUtil.isEmpty(bvo.getMeasurename())//规格
                        && bvo.getBnum() == null    //数量
                        && bvo.getBprice() == null    //单价
                        && bvo.getBhjje() == null    //金额
                        && bvo.getBspse() == null    //税额
                        && bvo.getBspsl() == null) {    //税率
                    //什么都不做
                } else {
                    if (bvo.getBhjje() == null
                            || bvo.getBspse() == null) {
                        throw new BusinessException("第" + (i + 1) + "行，金额、税额不能为空,请检查");
                    }

                    filterList.add(bvo);
                }
            }
        }

        return filterList.toArray(new VATSaleInvoiceBVO2[0]);
    }

    private void checkJEValid(VATSaleInvoiceVO2 vo, VATSaleInvoiceBVO2[] body) {
        if (vo.getKprj() == null)
            throw new BusinessException("开票日期不允许为空或日期格式不正确，请检查");

        if (vo.getKprj().before(SystemUtil.getLoginCorpVo().getBegindate())) {
            throw new BusinessException("开票日期不允许在建账日期前，请检查");
        }

        if (body == null || body.length == 0) {
            throw new BusinessException("表体行至少填写一行，请检查");
        }

//		boolean flag = true;
//		if(SafeCompute.add(DZFDouble.ZERO_DBL, vo.getHjje()).doubleValue() == 0){
//			flag = false;
//		}else if(SafeCompute.add(DZFDouble.ZERO_DBL, vo.getSpse()).doubleValue() == 0){
//			flag = false;
//		}else if(SafeCompute.add(DZFDouble.ZERO_DBL, vo.getSpsl()).doubleValue() == 0){
//			flag = false;
//		}else if(SafeCompute.add(DZFDouble.ZERO_DBL, vo.getJshj()).doubleValue() == 0){
//			flag = false;
//		}
//
//		if(!flag)
//			throw new BusinessException("税率、金额、税额、价税合计等字段为必输项，请检查！");
//
//
    }

    private void setDefultValue(VATSaleInvoiceVO2 vo, VATSaleInvoiceBVO2[] body) {

        DZFDouble bse = DZFDouble.ZERO_DBL;
        DZFDouble bje = DZFDouble.ZERO_DBL;
        DZFDouble bsl = DZFDouble.ZERO_DBL;
        StringBuffer spmchz = new StringBuffer();
        VATSaleInvoiceBVO2 bvo = null;
        for (int i = 0; i < body.length; i++) {
            bvo = body[i];
            bvo.setPk_corp(SystemUtil.getLoginCorpId());
            bse = SafeCompute.add(bse, bvo.getBspse());
            bje = SafeCompute.add(bje, bvo.getBhjje());
            bsl = SafeCompute.multiply(SafeCompute.div(bvo.getBspse(), bvo.getBhjje()), new DZFDouble(100));
            bsl = bsl.setScale(0, DZFDouble.ROUND_HALF_UP);
            bvo.setBspsl(bsl);
            bvo.setRowno(i + 1);

            if (!StringUtil.isEmpty(bvo.getBspmc())
                    && spmchz.length() == 0) {
                spmchz.append(bvo.getBspmc());
            }
            if (StringUtil.isEmpty(bvo.getPk_billcategory())) {
                bvo.setPk_billcategory(vo.getPk_model_h());
                bvo.setPk_category_keyword(vo.getPk_category_keyword());
            }
        }

        if (spmchz.length() > 0) {
            vo.setSpmc(spmchz.toString());
        }

        vo.setHjje(bje);//金额
        vo.setSpse(bse);//税额
        vo.setJshj(SafeCompute.add(bje, bse));
        DZFDouble sl = SafeCompute.multiply(SafeCompute.div(vo.getSpse(), vo.getHjje()), new DZFDouble(100));
        vo.setSpsl(sl.setScale(0, DZFDouble.ROUND_HALF_UP));//税率
        vo.setPeriod(DateUtils.getPeriod(vo.getKprj()));
        vo.setInperiod(vo.getPeriod());//入账期间

        if (!StringUtil.isEmpty(vo.getPrimaryKey())) {
            vo.setModifydatetime(new DZFDateTime());
            vo.setModifyoperid(SystemUtil.getLoginUserId());
        } else {
            vo.setDoperatedate(new DZFDate());
            vo.setCoperatorid(SystemUtil.getLoginUserId());
            vo.setPk_corp(SystemUtil.getLoginCorpId());
            vo.setSourcetype(IBillManageConstants.MANUAL);
        }

		/*if(StringUtil.isEmpty(vo.getPk_model_h())){
			setBusiNameFromFon(vo, getLogincorppk());
		}*/
    }

	/*private void setBusiNameFromFon(VATSaleInvoiceVO2 vo, String pk_corp){
		String businame = vo.getBusitypetempname();
		String busicode = vo.getBusisztypecode();
		if(StringUtil.isEmpty(businame)
			|| StringUtil.isEmpty(busicode)){
			return;
		}

		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
		Map<String, DcModelHVO> dcmap = hashliseBusiTypeMap(dcList);

		String stylecode = vo.getIsZhuan() != null && vo.getIsZhuan().booleanValue()
				? FieldConstant.FPSTYLE_01 : FieldConstant.FPSTYLE_02;
		String key = vo.getBusitypetempname()
				+ "," + stylecode + "," + busicode;

		DcModelHVO hvo = dcmap.get(key);
		if(hvo != null){
			vo.setPk_model_h(hvo.getPrimaryKey());
		}else{
			String zhflag = stylecode.equals(FieldConstant.FPSTYLE_01) ? "专票" : "普票";
			String msg = "<p>销项发票[%s_%s]为%s与入账模板票据类型不一致，请检查</p>";
			throw new BusinessException(String.format(msg,
					vo.getFp_dm(), vo.getFp_hm(), zhflag));
		}
	}*/

	/*private Map<String, DcModelHVO> hashliseBusiTypeMap(List<DcModelHVO> dcList){
		Map<String, DcModelHVO> map = new LinkedHashMap<String, DcModelHVO>();
		if(dcList != null && dcList.size() > 0){
			String key;
			for(DcModelHVO hvo : dcList){
				key = hvo.getBusitypetempname()
						+ "," + hvo.getVspstylecode()
						+ "," + hvo.getSzstylecode();
				if(!map.containsKey(key)){
					map.put(key, hvo);
				}

			}
		}

		return map;
	}*/

    //删除记录
    @RequestMapping("/onDelete")
    public ReturnData<Json> onDelete(@RequestBody Map<String,String> param){
        Json json = new Json();
        json.setSuccess(false);
        StringBuffer strb = new StringBuffer();
        VATSaleInvoiceVO2[] bodyvos = null;
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        String msg = "";
        List<String> sucHM = new ArrayList<String>();
        List<String> errHM = new ArrayList<String>();
        boolean lock = false;
        try {
            String body = param.get("head");

            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("xiaoxiangdel"+pk_corp);
            if (!lock) {//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍候刷新界面");
                return ReturnData.error().data(json);
            }

            if (body == null) {
                throw new BusinessException("数据为空,删除失败!!");
            }

            bodyvos = JsonUtils.deserialize(body, VATSaleInvoiceVO2[].class);
            if (bodyvos == null || bodyvos.length == 0) {
                throw new BusinessException("数据为空,删除失败!!");
            }

            for (VATSaleInvoiceVO2 vo : bodyvos) {
//				gl_vatsalinvserv2.checkvoPzMsg(vo.getPk_vatsaleinvoice());
                try {
                    gl_vatsalinvserv2.delete(vo, pk_corp);
                    json.setSuccess(true);
                    strb.append("<font color='#2ab30f'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "],删除成功!</p></font>");

                    sucHM.add(vo.getFp_hm());
                } catch (Exception e) {
                    printErrorLog(json, e, "删除失败!");
                    strb.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]," + json.getMsg() + "</p></font>");

                    errHM.add(vo.getFp_hm());
                }
            }

            String inmsg = "";
            if (sucHM.size() > 0) {
                inmsg += SystemUtil.getLoginUserVo().getUser_name()
                        + "删除销项发票,发票号码(" + sucHM.get(0) + ")等" + sucHM.size() + "条记录成功";
            }
            if (errHM.size() > 0) {
                if (StringUtil.isEmpty(inmsg)) {
                    inmsg += SystemUtil.getLoginUserVo().getUser_name()
                            + "删除销项发票,发票号码(" + errHM.get(0) + ")等" + errHM.size() + "条记录失败";
                } else {
                    inmsg += ",发票号码(" + errHM.get(0) + ")等" + errHM.size() + "条记录失败";
                }
            }

            msg = inmsg;
        } catch (Exception e) {
            printErrorLog(json, e, "删除失败");
            strb.append("删除失败");

        } finally {
            if (lock)
            {
                redissonDistributedLock.releaseDistributedFairLock("xiaoxiangdel"+pk_corp);
            }
        }
        json.setMsg(strb.toString());

        if (StringUtil.isEmpty(msg)) {
            msg = "销项发票编辑";
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, msg, ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @RequestMapping("/impExcel")
    public ReturnData<Json> impExcel(@RequestBody MultipartFile file,String impForce) {
        String userid = SystemUtil.getLoginUserId();
        Json json = new Json();
        json.setSuccess(false);
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            DZFBoolean isFlag = "Y".equals(impForce) ? DZFBoolean.TRUE : DZFBoolean.FALSE;

            if (file == null || file.getSize() == 0) {
                throw new BusinessException("请选择导入文件!");
            }
            String fileName = file.getOriginalFilename();
            String fileType = null;
            if (fileName != null && fileName.length() > 0) {
                fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            }

            VATSaleInvoiceVO2 paramvo = new VATSaleInvoiceVO2();
            if (fileName == null || fileName.length() == 0) {
                throw new BusinessException("文件不存在");
            }

            paramvo.setIsFlag(isFlag);//设置是否强制导入
            StringBuffer msg = new StringBuffer();
            gl_vatsalinvserv2.saveImp(file, fileName, paramvo, pk_corp, fileType, userid, msg);

            json.setHead(paramvo);
            json.setMsg(msg.toString());
            json.setSuccess(paramvo.getCount() == 0 ? false : true);

            writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                    "导入销项发票"+(paramvo.getPeriod() != null ? "："+paramvo.getPeriod() : ""), ISysConstants.SYS_2);
        } catch (Exception e) {
            if (e instanceof BusinessException
                    && IBillManageConstants.ERROR_FLAG.equals(((BusinessException) e).getErrorCodeString())) {
                json.setMsg(e.getMessage());
                json.setStatus(Integer.parseInt(IBillManageConstants.ERROR_FLAG));
                json.setSuccess(false);
            } else {
                printErrorLog(json, e, "导入失败!");
            }

        }
        return ReturnData.ok().data(json);
    }


    /**
     * 检查是否包含旧业务类型
     *
     * @param setPk_model_h
     */
    private boolean checkPK_Model_H(Set<String> setPk_model_h, boolean isThrowException) {
        if (setPk_model_h.size() > 0) {
            String pks = "";
            for (String s : setPk_model_h) {
                pks += (pks.length() == 0 ? "" : ",") + s;
            }
            List<DcModelHVO> list = gl_yhdzdserv2.queryIsDcModels(pks);
            if (list != null && list.size() > 0) {
                if (isThrowException) {
                    StringBuffer errname = new StringBuffer();
                    ;
                    for (DcModelHVO dcmvo : list) {
                        if (!StringUtil.isEmpty(dcmvo.getBusitypetempname())) {
                            errname.append(dcmvo.getBusitypetempname());
                            errname.append(",");
                        }
                    }

                    throw new BusinessException("业务类型 " + errname.toString() + " 是旧版本类型，请重新选择业务类型");
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 生成凭证
     */
    public ReturnData<Json> createPZ(String lwstr, String body, String good) {
        Json json = new Json();
        VATSaleInvoiceVO2[] vos = null;
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        boolean lock = false;
        try {
            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("xiaoxiangcreatepz"+pk_corp);
            if(!lock){//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍候");
                return ReturnData.error().data(json);
            }


            DZFBoolean lwflag = "Y".equals(lwstr) ? DZFBoolean.TRUE : DZFBoolean.FALSE;
            if (body == null) {
                throw new BusinessException("数据为空,生成凭证失败!");
            }

            vos = JsonUtils.deserialize(body, VATSaleInvoiceVO2[].class);
            if (vos == null || vos.length == 0)
                throw new BusinessException("数据为空,生成凭证失败，请检查");

            VatGoosInventoryRelationVO[] goods = getGoodsData(good);

            List<VATSaleInvoiceVO2> stoList = gl_vatsalinvserv2.constructVatSale(vos, pk_corp);
            boolean accway = getAccWay(pk_corp);
            VatInvoiceSetVO setvo = queryRuleByType();
            if (stoList == null
                    || stoList.size() == 0) {
                throw new BusinessException("查询销项发票失败，请检查!");
            }
            Set<String> setPk_model_h = new HashSet<String>();
            //校验是否存在老数据
            for (VATSaleInvoiceVO2 vo : stoList) {
//				gl_vatsalinvserv2.checkvoPzMsg(vo.getPk_vatsaleinvoice());
                if (vo == null || !StringUtils.isEmpty(vo.getImgpath())) {
                    throw new BusinessException("发票号码：'" + vo.getFp_hm() + "' 是智能识别票据，请至票据工作台进行相关处理！");
                }
                if (vo.getPk_model_h() != null && setPk_model_h.contains(vo.getPk_model_h()) == false) {
                    setPk_model_h.add(vo.getPk_model_h());
                }
            }
            //检查是否包含旧业务类型
            if (setPk_model_h.size() > 0) {
                checkPK_Model_H(setPk_model_h, true);
            } else {
                throw new BusinessException("请设置业务类型！");
            }
            //检查是否包含存货类型
//			String checkMsg=gl_vatsalinvserv2.checkNoStock(stoList,pk_corp);
//			if(!StringUtils.isEmpty(checkMsg)){
//				throw new BusinessException(checkMsg);
//			}
            goodsToVatBVO(stoList, goods, pk_corp, SystemUtil.getLoginUserId());

            int errorCount = 0;
            StringBuffer msg = new StringBuffer();

            String key;
            String period = null;

            CorpVO corpvo = corpService.queryByPk(pk_corp);
            Map<String, YntCpaccountVO> accountMap = accountService.queryMapByPk(corpvo.getPk_corp());
            YntCpaccountVO[] accVOs = accountService.queryByPk(corpvo.getPk_corp());
            Map<String, Object> paramMap = null;
            List<List<Object[]>> levelList = null;
            Map<String, Object[]> categoryMap = null;
            Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap = null;
            Set<String> zyFzhsList = null;
            Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap = null;
            InventorySetVO inventorySetVO = null;
            Map<String, InventoryAliasVO> fzhsBMMap = null;
            List<Object> paramList = null;
            Map<String, BdCurrencyVO> currMap = null;
            Map<String, Object[]> rateMap = null;
            Map<String, String> bankAccountMap = null;
            Map<String, AuxiliaryAccountBVO> assistMap = null;
            Map<String, List<AccsetVO>> accsetMap = null;
            Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map = null;
            Map<String, String> jituanSubMap = null;
            String tradeCode = null;
            String newrule = null;
            List<AuxiliaryAccountBVO> chFzhsBodyVOs = null;
            Map<String, Map<String, Object>> periodMap = new HashMap<String, Map<String, Object>>();

            List<String> periodSet = new ArrayList<String>();
            String kplx = null;
            for (VATSaleInvoiceVO2 vo : stoList) {
                try {

                    kplx = vo.getKplx();

                    key = buildkey(vo, setvo);
                    period = splitKey(key);

                    if (periodMap.containsKey(period)) {
                        paramMap = periodMap.get(period);
                    } else {
                        paramMap = zncsVoucher.initVoucherParam(corpvo, period, false);
                        periodMap.put(period, paramMap);
                    }

                    levelList = (List<List<Object[]>>) paramMap.get("levelList");
                    categoryMap = (Map<String, Object[]>) paramMap.get("categoryMap");
                    fzhsHeadMap = (Map<Integer, AuxiliaryAccountHVO>) paramMap.get("fzhsHeadMap");
                    zyFzhsList = (Set<String>) paramMap.get("zyFzhsList");
                    fzhsBodyMap = (Map<String, List<AuxiliaryAccountBVO>>) paramMap.get("fzhsBodyMap");
                    inventorySetVO = (InventorySetVO) paramMap.get("inventorySetVO");
                    fzhsBMMap = (Map<String, InventoryAliasVO>) paramMap.get("fzhsBMMap");
                    paramList = (List<Object>) paramMap.get("paramList");
                    currMap = (Map<String, BdCurrencyVO>) paramMap.get("currMap");
                    rateMap = (Map<String, Object[]>) paramMap.get("rateMap");
                    bankAccountMap = (Map<String, String>) paramMap.get("bankAccountMap");
                    assistMap = (Map<String, AuxiliaryAccountBVO>) paramMap.get("assistMap");
                    accsetMap = (Map<String, List<AccsetVO>>) paramMap.get("accsetMap");
                    accsetKeywordBVO2Map = (Map<String, List<AccsetKeywordBVO2>>) paramMap.get("accsetKeywordBVO2Map");
                    jituanSubMap = (Map<String, String>) paramMap.get("jituanSubMap");
                    tradeCode = (String) paramMap.get("tradeCode");
                    newrule = (String) paramMap.get("newrule");
                    chFzhsBodyVOs = (List<AuxiliaryAccountBVO>) paramMap.get("chFzhsBodyVOs");

                    if (!periodSet.contains(period)) {
                        periodSet.add(period);//组装查询期间
                    }

                    if (!StringUtil.isEmpty(kplx)
                            && (ICaiFangTongConstant.FPLX_3.equals(kplx)//空白废票
                            || ICaiFangTongConstant.FPLX_4.equals(kplx)//正废
                            || ICaiFangTongConstant.FPLX_5.equals(kplx))) {//负废
                        errorCount++;
                        periodSet.remove(period);
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成凭证。</p></font>");
//					}else if(vo.getIsic() != null && vo.getIsic().booleanValue()){
                    } else if (!StringUtil.isEmpty(vo.getPk_ictrade_h())) {
                        errorCount++;
                        periodSet.remove(period);
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已关联出库，不能生成凭证。</p></font>");
                    } else if (StringUtil.isEmpty(vo.getPk_tzpz_h())) {
                        gl_vatsalinvserv2.createPZ(vo, SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId(), period, setvo, lwflag, accway, false, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
                        msg.append("<font color='#2ab30f'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");
                    } else {
                        errorCount++;
                        periodSet.remove(period);
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成凭证。</p></font>");
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);

                    if (e instanceof BusinessException
                            && !StringUtil.isEmpty(e.getMessage())
                            && !e.getMessage().startsWith("销项发票")
                            && !e.getMessage().startsWith("制单失败")) {
                        try {
                            gl_vatsalinvserv2.createPZ(vo, SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId(),
                                    period, setvo, lwflag, accway, true, levelList, categoryMap,
                                    fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap,
                                    paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap,
                                    accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode,
                                    newrule, chFzhsBodyVOs);
                            msg.append("<font color='#2ab30f'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");
                        } catch (Exception ex) {

                            errorCount++;
                            if (ex instanceof BusinessException) {
                                msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败，原因:")
                                        .append(e.getMessage())
                                        .append("。</p></font>");
                            } else {
                                msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败")
                                        .append("。</p></font>");
                            }
                            periodSet.remove(period);
                        }
                    } else if (!StringUtil.isEmpty(e.getMessage())
                            && (e.getMessage().startsWith("销项发票")
                            || e.getMessage().startsWith("制单失败"))) {
                        errorCount++;
                        periodSet.remove(period);
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败，原因:")
                                .append(e.getMessage())
                                .append("。</p></font>");
                    } else {
                        errorCount++;
                        periodSet.remove(period);
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败 ")
                                .append("。</p></font>");
                    }
                }
            }

            StringBuffer headMsg = gl_yhdzdserv2.buildQmjzMsg(periodSet, pk_corp);
            if (headMsg != null && headMsg.length() > 0) {
                headMsg.append(msg.toString());
                msg = headMsg;
            }

            json.setSuccess(errorCount > 0 ? false : true);
            json.setMsg(msg.toString());
        } catch (Exception e) {
            printErrorLog(json, e, "生成凭证失败");
        } finally {
            if(lock){
                redissonDistributedLock.releaseDistributedFairLock("xiaoxiangcreatepz"+pk_corp);
            }
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "销项发票生成凭证", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private boolean getAccWay(String pk_corp) {
//		String acc = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF012);//入账设置
//		boolean accway = StringUtil.isEmpty(acc) || "0".equals(acc)
//				? true : false;//按入账日期为true, 按期间为false

        return true;
    }

    /**
     * 赋值存货主键
     *
     * @param list
     * @param goods
     */
    private void goodsToVatBVO(List<VATSaleInvoiceVO2> list, VatGoosInventoryRelationVO[] goods, String pk_corp, String userid) {
        if (goods != null && goods.length > 0) {

            CorpVO corpvo = corpService.queryByPk(pk_corp);

            if (corpvo.getBbuildic().equals(IcCostStyle.IC_ON)) {

                Map<String, VatGoosInventoryRelationVO> map = DZfcommonTools.hashlizeObjectByPk(
                        Arrays.asList(goods), new String[]{"spmc", "invspec", "unit"});
                Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(pk_corp);
                YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);
                String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
                VATSaleInvoiceBVO2[] children;
                String pk_inventory;
                String pk_inventory_old;
                //			List<VATSaleInvoiceBVO2> bvoList;
                //			Map<String, List<VATSaleInvoiceBVO2>> relmap = new HashMap<String, List<VATSaleInvoiceBVO2>>();
                List<VatGoosInventoryRelationVO> relList;
                Map<String, VatGoosInventoryRelationVO> temp = new HashMap<String, VatGoosInventoryRelationVO>();
                Map<String, List<VatGoosInventoryRelationVO>> newRelMap = new HashMap<String, List<VatGoosInventoryRelationVO>>();
                for (VATSaleInvoiceVO2 vo : list) {
                    children = (VATSaleInvoiceBVO2[]) vo.getChildren();

                    if (children != null && children.length > 0) {
                        String key;
                        VatGoosInventoryRelationVO relvo;
                        for (VATSaleInvoiceBVO2 bvo : children) {
                            key = bvo.getBspmc() + "," + bvo.getInvspec() + "," + bvo.getMeasurename();

                            if (map.containsKey(key)) {
                                relvo = map.get(key);
                                pk_inventory = relvo.getPk_inventory();

                                pk_inventory_old = relvo.getPk_inventory_old();
                                //主键为空的情况,,新增,,
                                //old不为空,,,,有可能新增有可能别名,,,,
                                if (StringUtil.isEmpty(pk_inventory)//肯定新增,如果有在主表匹配的和当前pk一致的则认为他是匹配自己主表的
                                        || (!StringUtil.isEmpty(pk_inventory_old) && pk_inventory_old.equals(pk_inventory))
                                ) {
                                    ocrinterface.matchInvtoryIC(relvo, pk_corp, userid, newrule, accmap, accounts);

                                } else {
                                    bvo.setPk_inventory(pk_inventory);

                                    if (!temp.containsKey(key)) {//过滤不需要的数据//!pk_inventory.equals(pk_inventory_old) &&
                                        if (newRelMap.containsKey(pk_inventory)) {
                                            relList = newRelMap.get(pk_inventory);
                                            relList.add(relvo);
                                        } else {
                                            relList = new ArrayList<VatGoosInventoryRelationVO>();
                                            relList.add(relvo);
                                            newRelMap.put(pk_inventory, relList);
                                        }

                                        temp.put(key, relvo);
                                    }
                                }

                            }
                        }
                    }
                }

                gl_vatsalinvserv2.saveGoodsRela(newRelMap, pk_corp, userid);
            } else {
                List<InventoryAliasVO> inlist = new ArrayList<InventoryAliasVO>();
                for (VatGoosInventoryRelationVO inventoryAliasVO : goods) {
                    InventoryAliasVO ivo = new InventoryAliasVO();
                    ivo.setAliasname(inventoryAliasVO.getSpmc());
                    ivo.setSpec(inventoryAliasVO.getInvspec());
                    ivo.setPk_inventory(inventoryAliasVO.getPk_inventory());
                    //ivo.set
                    inlist.add(ivo);
                }
                List<Grid> logList = new ArrayList<Grid>();//记录更新日志
                gl_vatsalinvserv2.saveInventoryData(pk_corp, inlist.toArray(new InventoryAliasVO[0]), logList);
            }
        }
    }

    private VatGoosInventoryRelationVO[] getGoodsData(String goods) {

        VatGoosInventoryRelationVO[] vos = null;

        if (StringUtil.isEmpty(goods)) {
            return null;
        }

        vos = JsonUtils.deserialize(goods, VatGoosInventoryRelationVO[].class);
        return vos;
    }

    @RequestMapping("/getGoodsInvenRela_long")
    public ReturnData<Grid> getGoodsInvenRela_long(@RequestBody Map<String,String> param) {
        Grid grid = new Grid();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            String body = param.get("head");
            CorpVO corpvo = SystemUtil.getLoginCorpVo();
            if (!corpvo.getBbuildic().equals(IcCostStyle.IC_OFF)) {
                if (body == null) {
                    throw new BusinessException("数据为空,生成凭证失败!");
                }

                VATSaleInvoiceVO2[] vos = JsonUtils.deserialize(body, VATSaleInvoiceVO2[].class);
                if (vos == null || vos.length == 0)
                    throw new BusinessException("数据为空,生成凭证失败，请检查");

                //Map<String, DcModelHVO> dcmap = gl_yhdzdserv2.queryDcModelVO(pk_corp);

                List<VATSaleInvoiceVO2> stoList = gl_vatsalinvserv2.constructVatSale(vos, pk_corp);

                if (stoList == null
                        || stoList.size() == 0)
                    throw new BusinessException("未找销项发票数据，请检查");

                for (VATSaleInvoiceVO2 vo : stoList) {
//				gl_vatsalinvserv2.checkvoPzMsg(vo.getPk_vatsaleinvoice());
                    if (vo == null || !StringUtils.isEmpty(vo.getImgpath())) {
                        throw new BusinessException("发票号码：'" + vo.getFp_hm() + "' 是智能识别票据，请至票据工作台进行相关处理！");
                    }
                }

                List<VatGoosInventoryRelationVO> relvos = gl_vatsalinvserv2.getGoodsInvenRela(stoList, pk_corp);

                List<VatGoosInventoryRelationVO> blankvos = new ArrayList<VatGoosInventoryRelationVO>();
                List<VatGoosInventoryRelationVO> newRelvos = getFilterGoods(relvos, blankvos, pk_corp);

                Long total = relvos == null ? 0L : relvos.size();
//			if(blankvos.size() == 0){
//				total = newRelvos == null ? 0L : newRelvos.size();
//			}
//			if(relvos == null || relvos.size() == 0)
//				throw new BusinessException("所选非存货无需匹配存货!");

                grid.setTotal(total);
                grid.setRows(relvos);
                grid.setSuccess(true);
                grid.setMsg("获取存货对照关系成功");
            } else {
                grid.setTotal(null);
                grid.setSuccess(true);
            }
        } catch (Exception e) {
            printErrorLog(grid, e, "获取存货对照关系失败");
        }

        return ReturnData.ok().data(grid);
    }

    private List<VatGoosInventoryRelationVO> getFilterGoods(List<VatGoosInventoryRelationVO> relvos,
                                                            List<VatGoosInventoryRelationVO> blankvos,
                                                            String pk_corp) {


        if (relvos == null) return null;
        CorpVO corpvo = corpService.queryByPk(pk_corp);
        if (!corpvo.getBbuildic().equals(IcCostStyle.IC_OFF)) {
            YntCpaccountVO[] cpavos = accountService.queryByPk(pk_corp);

            String code;
            String fzhs;
            boolean flag = false;
            for (YntCpaccountVO cpa : cpavos) {
                code = cpa.getAccountcode();
                if (!StringUtil.isEmpty(code)
                        && (code.startsWith("500101")
                        || code.startsWith("600101")
                        || code.startsWith("5001001")
                        || code.startsWith("6001001"))) {
                    fzhs = cpa.getIsfzhs();
                    if (!StringUtil.isEmpty(fzhs) && fzhs.charAt(5) == '1') {
                        flag = true;
                        break;
                    }

                }
            }

            if (!flag) {
                return null;
            }

        }

        List<VatGoosInventoryRelationVO> sencondList = new ArrayList<VatGoosInventoryRelationVO>();

        if (relvos != null && relvos.size() > 0) {
            String pk_inventory;
            for (VatGoosInventoryRelationVO vo : relvos) {
                pk_inventory = vo.getPk_inventory();
                if (StringUtil.isEmpty(pk_inventory)) {
                    blankvos.add(vo);
                }
                sencondList.add(vo);
            }
        }

        return sencondList;
    }

    @RequestMapping("/combinePZ_long")
    public ReturnData<Json> combinePZ_long(@RequestBody Map<String,String> param) {
        String lwstr = param.get("lwflag");
        String body = param.get("head");
        String good = param.get("goods");
        VatInvoiceSetVO setvo = queryRuleByType();
        ReturnData<Json> returnData = null;
        if (setvo == null
                || setvo.getValue() == null
                || setvo.getValue() == IBillManageConstants.HEBING_GZ_01) {
            returnData = createPZ(lwstr, body, good);
        } else {
            returnData = combinePZ1(setvo, lwstr, body, good);
        }
        return returnData;
    }


    public ReturnData<Json> combinePZ1(VatInvoiceSetVO setvo,String lwstr,String body,String good){
        Json json = new Json();
        VATSaleInvoiceVO2[] vos = null;
        String pk_corp = SystemUtil.getLoginCorpId();
        String userid  = SystemUtil.getLoginUserId();
        checkSecurityData(null,new String[]{pk_corp}, userid);
        boolean lock = false;
        try {

            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("xiaoxiangcombinepz"+pk_corp);
            if(!lock){//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍候");
                return ReturnData.ok().data(json);
            }
            DZFBoolean lwflag = "Y".equals(lwstr) ? DZFBoolean.TRUE : DZFBoolean.FALSE;
            if (body == null) {
                throw new BusinessException("数据为空,合并生成凭证失败!");
            }

            vos = JsonUtils.deserialize(body, VATSaleInvoiceVO2[].class);
            if(vos == null || vos.length == 0)
                throw new BusinessException("数据为空，合并生成凭证失败，请检查");

            VatGoosInventoryRelationVO[] goods = getGoodsData(good);

//			VatInvoiceSetVO setvo = queryRuleByType();
            boolean accway = getAccWay(pk_corp);

            List<VATSaleInvoiceVO2> storeList = gl_vatsalinvserv2.constructVatSale(vos, pk_corp);

            if(storeList == null || storeList.size() == 0){
                throw new BusinessException("查询销项发票失败，请检查！");
            }
            Set<String> setPk_model_h = new HashSet<String>();
            for (VATSaleInvoiceVO2 vo : storeList) {
//				gl_vatsalinvserv2.checkvoPzMsg(vo.getPk_vatsaleinvoice());
                if(vo==null||!StringUtils.isEmpty(vo.getImgpath())){
                    throw new BusinessException("发票号码：'" + vo.getFp_hm() + "' 是智能识别票据，请至票据工作台进行相关处理！");
                }
                if (vo.getPk_model_h() != null && !setPk_model_h.contains(vo.getPk_model_h())){
                    setPk_model_h.add(vo.getPk_model_h());
                }

            }
            //检查是否包含旧业务类型
            if (setPk_model_h.size() > 0)
            {
                checkPK_Model_H(setPk_model_h, true);
            }
            else
            {
                throw new BusinessException("请设置业务类型！");
            }

            //检查是否包含存货类型
//			String  checkMsg= gl_vatsalinvserv2.checkNoStock(storeList,pk_corp);
//			if(!StringUtils.isEmpty(checkMsg)){
//				throw new BusinessException(checkMsg);
//			}
            goodsToVatBVO(storeList, goods, pk_corp, userid);

            Map<String, List<VATSaleInvoiceVO2>> combineMap = new LinkedHashMap<String, List<VATSaleInvoiceVO2>>();
            StringBuffer msg = new StringBuffer();
            String key;
            int errorCount = 0;
            List<String> periodSet = new ArrayList<String>();
            List<VATSaleInvoiceVO2> combineList = null;
            String kplx = null;
            String period;

            CorpVO corpvo=corpService.queryByPk(pk_corp);
            Map<String,YntCpaccountVO> accountMap = accountService.queryMapByPk(corpvo.getPk_corp());
            YntCpaccountVO[] accVOs=accountService.queryByPk(corpvo.getPk_corp());
            Map<String, Object> paramMap=null;
            List<List<Object[]>> levelList=null;
            Map<String, Object[]> categoryMap =null;
            Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap=null;
            Set<String> zyFzhsList=null;
            Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap=null;
            InventorySetVO inventorySetVO=null;
            Map<String, InventoryAliasVO> fzhsBMMap=null;
            List<Object> paramList = null;
            Map<String, BdCurrencyVO> currMap=null;
            Map<String, Object[]> rateMap=null;
            Map<String, String> bankAccountMap=null;
            Map<String, AuxiliaryAccountBVO> assistMap=null;
            Map<String, List<AccsetVO>> accsetMap=null;
            Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map=null;
            Map<String, String> jituanSubMap=null;
            String tradeCode=null;
            String newrule = null;
            List<AuxiliaryAccountBVO> chFzhsBodyVOs=null;
            Map<String, Map<String, Object>> periodMap=new HashMap<String, Map<String, Object>>();

            for(VATSaleInvoiceVO2 vo : storeList){

                key = buildkey(vo, setvo);
                period = splitKey(key);

                if(periodMap.containsKey(period)){
                    paramMap=periodMap.get(period);
                }else{
                    paramMap=zncsVoucher.initVoucherParam(corpvo, period,false);
                    periodMap.put(period, paramMap);
                }
                levelList=(List<List<Object[]>>) paramMap.get("levelList");
                categoryMap =(Map<String, Object[]>) paramMap.get("categoryMap");
                fzhsHeadMap=(Map<Integer, AuxiliaryAccountHVO>) paramMap.get("fzhsHeadMap");
                zyFzhsList=(Set<String>) paramMap.get("zyFzhsList");
                fzhsBodyMap=(Map<String, List<AuxiliaryAccountBVO>>) paramMap.get("fzhsBodyMap");
                inventorySetVO=(InventorySetVO) paramMap.get("inventorySetVO");
                fzhsBMMap=(Map<String, InventoryAliasVO>) paramMap.get("fzhsBMMap");
                paramList = (List<Object>) paramMap.get("paramList");
                currMap=(Map<String, BdCurrencyVO>) paramMap.get("currMap");
                rateMap=(Map<String, Object[]>) paramMap.get("rateMap");
                bankAccountMap=(Map<String, String>) paramMap.get("bankAccountMap");
                assistMap=(Map<String, AuxiliaryAccountBVO>) paramMap.get("assistMap");
                accsetMap=(Map<String, List<AccsetVO>>) paramMap.get("accsetMap");
                accsetKeywordBVO2Map=(Map<String, List<AccsetKeywordBVO2>>) paramMap.get("accsetKeywordBVO2Map");
                jituanSubMap=(Map<String, String>) paramMap.get("jituanSubMap");
                tradeCode=(String) paramMap.get("tradeCode");
                newrule = (String) paramMap.get("newrule");
                chFzhsBodyVOs=(List<AuxiliaryAccountBVO>) paramMap.get("chFzhsBodyVOs");

                if(!periodSet.contains(period)){
                    periodSet.add(period);//组装查询期间
                }
                kplx = vo.getKplx();
                if(!StringUtil.isEmpty(kplx)
                        && (ICaiFangTongConstant.FPLX_3.equals(kplx)//空白废票
                        || ICaiFangTongConstant.FPLX_4.equals(kplx)//正废
                        || ICaiFangTongConstant.FPLX_5.equals(kplx))){//负废
                    errorCount++;
                    periodSet.remove(key);
                    msg.append("<font color='red'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成凭证</p></font>");
//				}else if(vo.getIsic() != null && vo.getIsic().booleanValue()){
                }else if(!StringUtil.isEmpty(vo.getPk_ictrade_h())){
                    errorCount++;
                    periodSet.remove(key);
                    msg.append("<font color='red'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据已关联出库，不能生成凭证。</p></font>");
                }else if(!StringUtil.isEmpty(vo.getPk_tzpz_h())){
                    errorCount++;
                    periodSet.remove(key);
                    msg.append("<font color='red'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成凭证。</p></font>");
                }else if(IcCostStyle.IC_ON.equals(corpvo.getBbuildic())&&gl_vatsalinvserv2.checkIsStock(vo)){
                    try {
                        gl_vatsalinvserv2.createPZ(vo, SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId(), period, setvo, lwflag, accway, false, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
                        msg.append("<font color='#2ab30f'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");

                    } catch (Exception e) {
                        try {
                            if(e.getMessage().contains("未录入辅助核算")){
                                gl_vatsalinvserv2.createPZ(vo, SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId(), period, setvo, lwflag, accway, true, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
                                msg.append("<font color='#2ab30f'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");

                            }
                        } catch (Exception e1) {
                            if(e1 instanceof BusinessException){
                                msg.append("<font color='red'><p>销项发票入账期间为")
                                        .append(key)
                                        .append("的单据生成凭证失败，原因：")
                                        .append(e1.getMessage())
                                        .append("。</p></font>");
                            }else{
                                msg.append("<font color='red'><p>销项发票入账期间为")
                                        .append(key)
                                        .append("的单据生成凭证失败。</p></font>");
                            }
                        }
                    }
                }else if(combineMap.containsKey(key)){
                    combineList = combineMap.get(key);

                    combineList.add(vo);
                }else{
                    combineList = new ArrayList<VATSaleInvoiceVO2>();
                    combineList.add(vo);
                    combineMap.put(key, combineList);
                }
            }


            key = null;
            for(Map.Entry<String, List<VATSaleInvoiceVO2>> entry : combineMap.entrySet()){
                try {
                    key = entry.getKey();
                    key = splitKey(key);

                    if(periodMap.containsKey(key)){
                        paramMap=periodMap.get(key);
                    }else{
                        paramMap=zncsVoucher.initVoucherParam(corpvo, key,false);
                        periodMap.put(key, paramMap);
                    }
                    levelList=(List<List<Object[]>>) paramMap.get("levelList");
                    categoryMap =(Map<String, Object[]>) paramMap.get("categoryMap");
                    fzhsHeadMap=(Map<Integer, AuxiliaryAccountHVO>) paramMap.get("fzhsHeadMap");
                    zyFzhsList=(Set<String>) paramMap.get("zyFzhsList");
                    fzhsBodyMap=(Map<String, List<AuxiliaryAccountBVO>>) paramMap.get("fzhsBodyMap");
                    inventorySetVO=(InventorySetVO) paramMap.get("inventorySetVO");
                    fzhsBMMap=(Map<String, InventoryAliasVO>) paramMap.get("fzhsBMMap");
                    paramList = (List<Object>) paramMap.get("paramList");
                    currMap=(Map<String, BdCurrencyVO>) paramMap.get("currMap");
                    rateMap=(Map<String, Object[]>) paramMap.get("rateMap");
                    bankAccountMap=(Map<String, String>) paramMap.get("bankAccountMap");
                    assistMap=(Map<String, AuxiliaryAccountBVO>) paramMap.get("assistMap");
                    accsetMap=(Map<String, List<AccsetVO>>) paramMap.get("accsetMap");
                    accsetKeywordBVO2Map=(Map<String, List<AccsetKeywordBVO2>>) paramMap.get("accsetKeywordBVO2Map");
                    jituanSubMap=(Map<String, String>) paramMap.get("jituanSubMap");
                    tradeCode=(String) paramMap.get("tradeCode");
                    newrule = (String) paramMap.get("newrule");
                    chFzhsBodyVOs=(List<AuxiliaryAccountBVO>) paramMap.get("chFzhsBodyVOs");
                    combineList = entry.getValue();
                    gl_vatsalinvserv2.saveCombinePZ(combineList,
                            pk_corp, userid, key, lwflag, setvo, accway, false, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
                    msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if(e instanceof BusinessException
                            && !StringUtil.isEmpty(e.getMessage())
                            && !e.getMessage().startsWith("销项发票")
                            && !e.getMessage().startsWith("制单失败")){
                        try {

                            gl_vatsalinvserv2.saveCombinePZ(combineList, pk_corp, userid, key, lwflag, setvo, accway, true, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
                            msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
                        } catch (Exception ex) {
                            errorCount++;
                            if(ex instanceof BusinessException){
                                msg.append("<font color='red'><p>销项发票入账期间为")
                                        .append(key)
                                        .append("的单据生成凭证失败，原因：")
                                        .append(ex.getMessage())
                                        .append("。</p></font>");
                            }else{
                                msg.append("<font color='red'><p>销项发票入账期间为")
                                        .append(key)
                                        .append("的单据生成凭证失败。</p></font>");
                            }
                            periodSet.remove(key);
                        }
                    }else if(!StringUtil.isEmpty(e.getMessage())
                            && (e.getMessage().startsWith("销项发票")
                            || e.getMessage().startsWith("制单失败"))){
                        errorCount++;
                        msg.append("<font color='red'><p>销项发票入账期间为")
                                .append(key)
                                .append("的单据生成凭证失败，原因:")
                                .append(e.getMessage())
                                .append("。</p></font>");
                        periodSet.remove(key);
                    }else{
                        errorCount++;
                        msg.append("<font color='red'><p>销项发票入账期间为")
                                .append(key)
                                .append("的单据生成凭证失败。</p></font>");
                        periodSet.remove(key);
                    }
                }
            }
            //应产品要求：判断所在期间期末结转情况
            StringBuffer headMsg = gl_yhdzdserv2.buildQmjzMsg(periodSet, pk_corp);
            if(headMsg != null && headMsg.length() > 0){
                headMsg.append(msg.toString());
                msg = headMsg;
            }

            json.setSuccess(errorCount > 0 ? false : true);
            json.setMsg(msg.toString());
        } catch (Exception e) {
            printErrorLog(json, e, "生成凭证失败");
        } finally {
            if(lock){
                redissonDistributedLock.releaseDistributedFairLock("xiaoxiangcombinepz"+pk_corp);
            }
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "销项发票合并凭证", ISysConstants.SYS_2);
        return  ReturnData.ok().data(json);
    }

    private String splitKey(String key){

        return key.substring(0, 7);
    }

    private String buildkey(VATSaleInvoiceVO2 vo, VatInvoiceSetVO setvo){
        String key = null;
        if(!StringUtil.isEmpty(vo.getInperiod())){//入账期间
            key = vo.getInperiod();
        }else if(StringUtil.isEmpty(vo.getPeriod())){
            key = DateUtils.getPeriod(vo.getKprj());
        }else{
            key = vo.getPeriod();
        }

        DZFBoolean iszh = vo.getIszhuan();//专普票标识
        iszh = iszh == null ? DZFBoolean.FALSE:iszh;
        key += "_" + iszh.toString();

        if(setvo != null
                && setvo.getValue() == IBillManageConstants.HEBING_GZ_02){//按往来单位合并
            String gys = vo.getKhmc();
            if(!StringUtil.isEmpty(gys)){//购买方名称
                key += gys;
            }
        }

        return key;
    }

//	private String[] splitkeys(String allkey){
//		String[] keys = StringUtil.isEmpty(allkey) ? null : allkey.split("_");
//
//		return keys;
//	}
//
//	private String buildkey(VATSaleInvoiceVO2 vo, Integer itype){
//		String key = null;
//		if(itype == IBillManageConstants.HEBING_GZ_01){//相同往来单位合并一张
//
//			String period = DateUtils.getPeriod(vo.getKprj());
//
//			key = appendkey(new String[]{
//				vo.getPk_model_h(),
//				period,
//				vo.getKhmc()
//			});
//		}else if(itype == IBillManageConstants.HEBING_GZ_02){
//
//			key = appendkey(new String[]{
//					vo.getPk_model_h(),
//					vo.getKprj().toString()
//			});
//		}else if(itype == IBillManageConstants.HEBING_GZ_03){
//
//			key = appendkey(new String[]{
//					vo.getPk_model_h(),
//					vo.getKprj().toString(),
//					vo.getKhmc()
//			});
//		}else{
//
//			String period = DateUtils.getPeriod(vo.getKprj());
//
//			key = appendkey(new String[]{
//					vo.getPk_model_h(),
//					period
//			});
//		}
//
//		return key;
//	}
//
//	private String appendkey(String[] params){
//		StringBuffer sf = new StringBuffer();
//
//		for(int i = 0; i < params.length; i++){
//			sf.append(params[i]);
//
//			if(i != params.length - 1){
//				sf.append("_");
//			}
//		}
//
//		return sf.toString();
//	}

    private VatInvoiceSetVO queryRuleByType(){

        VatInvoiceSetVO[] setvos = vatinvoiceserv.queryByType(SystemUtil.getLoginCorpId(),
                IBillManageConstants.HEBING_XXFP);

        if(setvos != null && setvos.length > 0){
            return setvos[0];
        }

        VatInvoiceSetVO vo = new VatInvoiceSetVO();
        vo.setValue(IBillManageConstants.HEBING_GZ_01);
        vo.setEntry_type(IBillManageConstants.HEBING_FL_02);
        return vo;
    }

    @RequestMapping("/chooseTicketWay")
    public ReturnData<Json> chooseTicketWay(){
        Json json = new Json();
        json.setSuccess(false);

        try {

            CorpVO corpvo = gl_vatsalinvserv2.chooseTicketWay(SystemUtil.getLoginCorpId());

            json.setHead(corpvo);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
        }

        return ReturnData.ok().data(json);
    }

	/*public void setBusiType(){
		Json json = new Json();
		json.setSuccess(false);

		try {
			String data = getRequest().getParameter("rows");
			String busiid = getRequest().getParameter("busiid");//历史主键
			String businame = getRequest().getParameter("businame");
			String selvalue = getRequest().getParameter("selvalue");

			if(StringUtil.isEmptyWithTrim(data)
					|| StringUtil.isEmptyWithTrim(selvalue)){
				throw new BusinessException("传入后台参数为空，请检查");
			}

			JSONArray array = (JSONArray) JSON.parseArray(data);
			Map<String, String> bodymapping = FieldMapping.getFieldMapping(new VATSaleInvoiceVO2());
			VATSaleInvoiceVO2[] listvo = DzfTypeUtils.cast(array, bodymapping, VATSaleInvoiceVO2[].class, JSONConvtoJAVA.getParserConfig());

			if(listvo == null || listvo.length == 0)
				throw new BusinessException("解析前台参数失败，请检查");

			String msg = gl_vatsalinvserv2.saveBusiType(listvo, busiid, businame, selvalue, getLogin_userid(), getLogincorppk());

			//重新查询
			String[] pks = buildPks(listvo);
			List<VATSaleInvoiceVO2> newList = gl_vatsalinvserv2.queryByPks(pks, getLogincorppk());

			json.setRows(newList);
			json.setMsg(msg);
			json.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(json, log, e, "更新业务类型失败");
		}

		writeLogRecord(LogRecordEnum.OPE_KJ_PJGL.getValue(),
				"销项发票更新业务类型", ISysConstants.SYS_2);

		writeJson(json);
	}*/

	@RequestMapping("/setBusiperiod")
    public ReturnData<Json> setBusiperiod(@RequestParam("rows")String data,String period){
        Json json = new Json();
        json.setSuccess(false);
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            if(StringUtil.isEmptyWithTrim(data)
                    || StringUtil.isEmptyWithTrim(period)){
                throw new BusinessException("传入后台参数为空，请检查");
            }

            DZFDate date = DateUtils.getPeriodEndDate(period);
            if(date == null){
                throw new BusinessException("入账期间解析错误，请检查");
            }

            JSONArray array = (JSONArray) JSON.parseArray(data);
//            Map<String, String> bodymapping = FieldMapping.getFieldMapping(new VATSaleInvoiceVO2());
//            VATSaleInvoiceVO2[] listvo = DzfTypeUtils.cast(array, bodymapping, VATSaleInvoiceVO2[].class, JSONConvtoJAVA.getParserConfig());
            VATSaleInvoiceVO2[] listvo = array.toArray(new VATSaleInvoiceVO2[0]);
            if(listvo == null || listvo.length == 0)
                throw new BusinessException("解析前台参数失败，请检查");

            List<VATSaleInvoiceVO2> stoList = gl_vatsalinvserv2.constructVatSale(listvo, pk_corp);

            Set<String> setPk_model_h = new HashSet<String>();
            for (VATSaleInvoiceVO2 vo : stoList) {
//				List<DcModelHVO> list = gl_yhdzdserv2.queryIsDcModel(vo.getPk_model_h());

//				if(list!=null&&list.size()>0){
//					throw new BusinessException("发票号码"+vo.getFp_hm()+"不允许期间调整，请重新设置业务类型!");
//				}

                if (vo.getPk_model_h() != null && !setPk_model_h.contains(vo.getPk_model_h())){
                    setPk_model_h.add(vo.getPk_model_h());
                }
            }

            //检查是否包含旧业务类型
            if (setPk_model_h.size() > 0)
            {
                checkPK_Model_H(setPk_model_h, true);
            }
            else
            {
                throw new BusinessException("请先设置业务类型，再做期间调整！");
            }

            String msg = gl_vatsalinvserv2.saveBusiPeriod(listvo, pk_corp, period);

            json.setRows(null);
            json.setMsg(msg);
            json.setSuccess(true);//(StringUtil.isEmpty(msg) || msg.contains("详细原因") ) ? false : true
        } catch (Exception e) {
            printErrorLog(json, e, "期间调整失败");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "进项发票期间调整", ISysConstants.SYS_2);

        return ReturnData.ok().data(json);
    }

    private String[] buildPks(VATSaleInvoiceVO2[] vos){
        String[] arr = new String[vos.length];

        for(int i = 0; i < vos.length; i++){
            arr[i] = vos[i].getPk_vatsaleinvoice();
        }

        return arr;
    }

    @RequestMapping("/checkBeforPZ")
    public ReturnData<Json> checkBeforPZ(@RequestParam("row")String str){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        try {

            VATSaleInvoiceVO2[] listvo = JsonUtils.deserialize(str, VATSaleInvoiceVO2[].class);
            if(listvo == null || listvo.length == 0)
                throw new BusinessException("解析前台参数失败，请检查");

            gl_vatsalinvserv2.checkBeforeCombine(listvo);
            TzpzHVO headVO = constructCheckTzpzHVo(pk_corp, listvo[0]);
            gl_yhdzdserv2.checkCreatePZ(pk_corp, headVO);

            json.setSuccess(true);
            json.setMsg("校验成功");
        } catch (Exception e) {
            printErrorLog(json, e, "校验失败");
        }

        return  ReturnData.ok().data(json);
    }

    /**
     * 构造生成凭证前校验需要的HVO
     * @param pk_corp
     * @param vo
     * @return
     */
    private TzpzHVO constructCheckTzpzHVo(String pk_corp, VATSaleInvoiceVO2 vo){
        DZFDate lastDate = DateUtils.getPeriodEndDate(
                DateUtils.getPeriod(vo.getKprj())
        );//取开票日期所在期间的最后一天
        TzpzHVO hvo = new TzpzHVO();
        hvo.setPk_corp(pk_corp);
        hvo.setDoperatedate(lastDate);//
        hvo.setPeriod(vo.getPeriod());

        return hvo;
    }

    @RequestMapping("/getTzpzHVOByID")
    public ReturnData<Json> getTzpzHVOByID(@RequestParam("row")String str){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
//			if(StringUtil.isEmptyWithTrim(data.getPrimaryKey()))
//				throw new BusinessException("获取前台参数失败，请检查");

            JSONArray array = (JSONArray) JSON.parseArray(str);
            if (array == null) {
                throw new BusinessException("数据为空,请检查!");
            }
            VATSaleInvoiceVO2[] listvo = array.toArray(new VATSaleInvoiceVO2[0]);
            if(listvo == null || listvo.length == 0){
                throw new BusinessException("转化后数据为空,请检查!");
            }

            boolean accway = getAccWay(pk_corp);
            VatInvoiceSetVO setvo = queryRuleByType();
            TzpzHVO hvo = gl_vatsalinvserv2.getTzpzHVOByID(listvo,
                    pk_corp, SystemUtil.getLoginCorpId(), setvo, accway);
            hvo.setPk_image_group(listvo[0].getPk_image_group());
            json.setData(hvo);
            json.setSuccess(true);
            json.setMsg("凭证分录构造成功");
        } catch (Exception e) {
            printErrorLog(json, e, "凭证分录构造失败");
        }

        return ReturnData.ok().data(json);
    }

    @RequestMapping("/expExcelData")
    public void expExcelData(@RequestBody Map<String,String> param,HttpServletResponse response){
        String strrows = param.get("daterows");
        VATSaleInvoiceVO2[] listvo = null;
        JSONArray array = JSON.parseArray(strrows);
        if(!StringUtils.isEmpty(strrows)) {
            listvo = JsonUtils.deserialize(strrows,VATSaleInvoiceVO2[].class);
        }

        OutputStream toClient = null;
        try {
            response.reset();
            String exName = new String("销项清单.xlsx");
            exName = new String(exName.getBytes("GB2312"), "ISO_8859_1");// 解决中文乱码问题
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(exName));
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            byte[] length = null;
            VatExportUtils exp = new VatExportUtils();
            Map<Integer, String> fieldColumn = getExpFieldMap();
            speTransValue(array);
            List<String> custNameList = gl_vatsalinvserv2.getCustNames(SystemUtil.getLoginCorpId(), listvo);
            //List<String> busiList = gl_vatsalinvserv2.getBusiTypes(getLogincorppk());
            List<String> busiList = new ArrayList<String>();
            length = exp.exportExcelForXlsx(fieldColumn, array, toClient,
                    "xiaoxiangqingdan2.xlsx", 0, 1, 1, VatExportUtils.EXP_XX,
                    true, 1, custNameList, 2, busiList);
            String srt2 = new String(length, "UTF-8");
            response.addHeader("Content-Length", srt2);
            toClient.flush();
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("excel导出错误", e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (Exception e) {
                log.error("excel导出错误", e);
            }
            try {
                if (response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (Exception e) {
                log.error("excel导出错误", e);
            }
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                !StringUtils.isEmpty(strrows)?"导出销项发票":"", ISysConstants.SYS_2);
    }

    private void speTransValue(JSONArray arr){
        String pk_corp = SystemUtil.getLoginCorpId();
        int len = arr == null ? 0 : arr.size();
        Map<String, Object> map = null;
        Object obj = null;
//		Map<String, YntCpaccountVO> yntMap = AccountCache.getInstance().getMap(null, pk_corp);
//		YntCpaccountVO cpavo = null;
        for(int i = 0; i < len; i++){
            map = (Map<String, Object>) arr.get(i);
            map.put("serialno", i+1);//设置序号

//			obj = map.get("accid");
//			if(obj != null){
//				cpavo = yntMap.get(obj);
//				if(cpavo != null){
//					map.put("kmbm", cpavo.getAccountcode());
//					map.put("kmmc", cpavo.getAccountname());
//				}
//			}

        }
    }
    /**
     * 导出映射字段
     * @return
     */
    private Map<Integer, String> getExpFieldMap(){
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(0, "serialno");
        map.put(1, "fpdm");
        map.put(2, "fphm");
        map.put(3, "skprj");
        map.put(4, "sspmc");
        map.put(5, "invspec");
        map.put(6, "measurename");
        map.put(7, "bnum");
        map.put(8, "shjje");
        map.put(9, "se");
        map.put(10, "busitempname");
        map.put(11, "skhmc");
        //map.put(8, "kmbm");
        //map.put(9, "kmmc");

        return map;
    }

    @RequestMapping("/onTicket")
    public ReturnData<Json> onTicket(@RequestBody Map<String,String> param){
        Json json = new Json();
        VATSaleInvoiceVO2 paramvo = new VATSaleInvoiceVO2();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        boolean lock = false;
        try{
            String ccrecode = param.get("ccrecode");
            String f2 = param.get("f2");
            String period = param.get("period");
            paramvo.setKprj(new DZFDate(SystemUtil.getLoginDate()));//参数：当前登录时间
            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock(paramvo.getTableName()+pk_corp);
            if(!lock){
                json.setSuccess(true);
                json.setMsg("正在处理中，请稍候");
                return ReturnData.ok().data(json);
            }
            TicketNssbhVO nssbvo = gl_vatsalinvserv2.getNssbvo(SystemUtil.getLoginCorpVo());//取当前公司
            Map<String, VATSaleInvoiceVO2> repMap = null;
            //根据业务类型生成凭证
            StringBuffer msg = new StringBuffer();

            if(nssbvo == null){//规则  未认证走 账方通 ， 已认证走 票通取数 票通宝

                DZFDate date = DateUtils.getPeriodEndDate(period);
                paramvo.setKprj(date);//参数：当前登录时间
                String crecode = null;
                if(!StringUtils.isEmpty(ccrecode)){
                    crecode = ccrecode.trim();
                }

                repMap = gl_vatsalinvserv2.saveCft(pk_corp, SystemUtil.getLoginUserId(), crecode, f2, paramvo, msg);
            }else{
                repMap = gl_vatsalinvserv2.saveKp(pk_corp,
                        SystemUtil.getLoginUserId(), paramvo, nssbvo);
            }

            msg.append("<p>一键取票成功<p>");

            int errorCount = 0;
            GxhszVO gxh = gl_gxhszserv.query(pk_corp);
            Integer yjqpway = gxh.getYjqp_gen_vch();//0自动 1 手工
            if(repMap != null && repMap.size() > 0 && yjqpway == 0){
                CorpVO corpvo = corpService.queryByPk(pk_corp);
                String bbuildic = corpvo.getBbuildic();
                Integer icstyle = SystemUtil.getLoginCorpVo().getIbuildicstyle();

                if(!StringUtil.isEmpty(bbuildic)
                        && IcCostStyle.IC_ON.equals(bbuildic)
                        && icstyle != null && icstyle == 1){
                    Map<String, VATSaleInvoiceVO2> pzMap = new HashMap<String, VATSaleInvoiceVO2>();
                    Map<String, VATSaleInvoiceVO2> icMap = new HashMap<String, VATSaleInvoiceVO2>();
                    dealFirst(repMap, pzMap, icMap);
                    int pzCount = 0;
                    int icCount = 0;
                    if(pzMap.size() > 0){
                        pzCount = dealAfterTicketByPZ(pzMap, msg);
                    }
                    if(icMap.size() > 0){
                        icCount = dealAfterTicketByIC(icMap, msg);
                    }
                    errorCount = pzCount + icCount;
                }else{
                    errorCount = dealAfterTicketByPZ(repMap, msg);
                }

            }

            json.setHead(paramvo);
            json.setSuccess(errorCount == 0 ? true : false);
            json.setMsg(msg.toString());
        }catch(Exception e){
            printErrorLog(json, e, "一键取票失败");
        }finally{
            //解锁
            if(lock){
                redissonDistributedLock.releaseDistributedFairLock(paramvo.getTableName()+pk_corp);
            }
        }
        return ReturnData.ok().data(json);
    }

    private void dealFirst(Map<String, VATSaleInvoiceVO2> repMap,
                           Map<String, VATSaleInvoiceVO2> pzMap,
                           Map<String, VATSaleInvoiceVO2> icMap){
        String busiName;
        String key;
        VATSaleInvoiceVO2 vo;
        for(Map.Entry<String, VATSaleInvoiceVO2> entry : repMap.entrySet()){
            key = entry.getKey();
            vo = entry.getValue();
            busiName = vo.getBusitypetempname();
            if(!StringUtil.isEmpty(busiName)
                    && (busiName.startsWith("收入-主营业务收入-商品销售收入")||busiName.startsWith("收入-其他业务收入-销售材料收入"))){
                icMap.put(key, vo);
            }else{
                pzMap.put(key, vo);
            }
        }
    }

    private int dealAfterTicketByPZ(Map<String, VATSaleInvoiceVO2> map, StringBuffer msg){
        List<VATSaleInvoiceVO2> list = buildMap2List(map);
        int errorCount = 0;
        String pk_corp = SystemUtil.getLoginCorpId();
        String userid = SystemUtil.getLoginUserId();

        //Map<String, DcModelHVO> dcmap = gl_yhdzdserv2.queryDcModelVO(pk_corp);
        YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);

        if(list == null || list.size() == 0){
            return errorCount;
        }

        String kplx = null;
        String key = null;

        Map<String, List<VATSaleInvoiceVO2>> combineMap = new LinkedHashMap<String, List<VATSaleInvoiceVO2>>();
        List<VATSaleInvoiceVO2> combineList = null;
        //DcModelHVO modelHVO = null;
        VatInvoiceSetVO setvo = queryRuleByType();
        boolean accway = getAccWay(pk_corp);
        for(VATSaleInvoiceVO2 vo : list){

            //modelHVO = dcmap.get(vo.getPk_model_h());
            if(StringUtils.isEmpty(vo.getPk_model_h())){
                errorCount++;
                msg.append("<font color='red'><p>销项发票[" + vo.getFp_hm() + "," + vo.getFp_hm() + "]业务类型未找到，请选择相应业务类型</p></font>");
                continue;
            }

            key = buildkey(vo, setvo);
            kplx = vo.getKplx();

            if(!StringUtil.isEmpty(kplx)
                    && (ICaiFangTongConstant.FPLX_3.equals(kplx)//空白废票
                    || ICaiFangTongConstant.FPLX_4.equals(kplx)//正废
                    || ICaiFangTongConstant.FPLX_5.equals(kplx))){//负废
                errorCount++;
                msg.append("<font color='red'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成凭证</p></font>");
            }else if(!StringUtil.isEmpty(vo.getPk_tzpz_h())){
                errorCount++;
                msg.append("<font color='red'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成凭证。</p></font>");
            }else if(StringUtil.isEmpty(vo.getPk_model_h())){
                errorCount++;
                msg.append("<font color='red'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据没有业务类型，不能生成凭证。</p></font>");
            }else if(combineMap.containsKey(key)){
                combineList = combineMap.get(key);

                combineList.add(vo);
            }else{
                combineList = new ArrayList<VATSaleInvoiceVO2>();
                combineList.add(vo);
                combineMap.put(key, combineList);
            }
        }

        key = null;
        CorpVO corpvo=corpService.queryByPk(pk_corp);
        Map<String,YntCpaccountVO> accountMap = accountService.queryMapByPk(corpvo.getPk_corp());
        YntCpaccountVO[] accVOs=accountService.queryByPk(corpvo.getPk_corp());
        Map<String, Object> paramMap=null;
        List<List<Object[]>> levelList=null;
        Map<String, Object[]> categoryMap =null;
        Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap=null;
        Set<String> zyFzhsList=null;
        Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap=null;
        InventorySetVO inventorySetVO=null;
        Map<String, InventoryAliasVO> fzhsBMMap=null;
        List<Object> paramList = null;
        Map<String, BdCurrencyVO> currMap=null;
        Map<String, Object[]> rateMap=null;
        Map<String, String> bankAccountMap=null;
        Map<String, AuxiliaryAccountBVO> assistMap=null;
        Map<String, List<AccsetVO>> accsetMap=null;
        Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map=null;
        Map<String, String> jituanSubMap=null;
        String tradeCode=null;
        String newrule = null;
        List<AuxiliaryAccountBVO> chFzhsBodyVOs=null;
        Map<String, Map<String, Object>> periodMap=new HashMap<String, Map<String, Object>>();
        for(Map.Entry<String, List<VATSaleInvoiceVO2>> entry : combineMap.entrySet()){
            try {
                key = entry.getKey();
                key = splitKey(key);

                if(periodMap.containsKey(key)){
                    paramMap=periodMap.get(key);
                }else{
                    paramMap=zncsVoucher.initVoucherParam(corpvo, key,false);
                    periodMap.put(key, paramMap);
                }
                levelList=(List<List<Object[]>>) paramMap.get("levelList");
                categoryMap =(Map<String, Object[]>) paramMap.get("categoryMap");
                fzhsHeadMap=(Map<Integer, AuxiliaryAccountHVO>) paramMap.get("fzhsHeadMap");
                zyFzhsList=(Set<String>) paramMap.get("zyFzhsList");
                fzhsBodyMap=(Map<String, List<AuxiliaryAccountBVO>>) paramMap.get("fzhsBodyMap");
                inventorySetVO=(InventorySetVO) paramMap.get("inventorySetVO");
                fzhsBMMap=(Map<String, InventoryAliasVO>) paramMap.get("fzhsBMMap");
                paramList = (List<Object>) paramMap.get("paramList");
                currMap=(Map<String, BdCurrencyVO>) paramMap.get("currMap");
                rateMap=(Map<String, Object[]>) paramMap.get("rateMap");
                bankAccountMap=(Map<String, String>) paramMap.get("bankAccountMap");
                assistMap=(Map<String, AuxiliaryAccountBVO>) paramMap.get("assistMap");
                accsetMap=(Map<String, List<AccsetVO>>) paramMap.get("accsetMap");
                accsetKeywordBVO2Map=(Map<String, List<AccsetKeywordBVO2>>) paramMap.get("accsetKeywordBVO2Map");
                jituanSubMap=(Map<String, String>) paramMap.get("jituanSubMap");
                tradeCode=(String) paramMap.get("tradeCode");
                newrule = (String) paramMap.get("newrule");
                chFzhsBodyVOs=(List<AuxiliaryAccountBVO>) paramMap.get("chFzhsBodyVOs");

                combineList = entry.getValue();

                gl_vatsalinvserv2.saveCombinePZ(combineList,
                        pk_corp, userid, key, null, setvo, accway, false, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
                msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if(e instanceof BusinessException
                        && !StringUtil.isEmpty(e.getMessage())
                        && !e.getMessage().startsWith("销项发票")
                        && !e.getMessage().startsWith("制单失败")){
                    try {
                        gl_vatsalinvserv2.saveCombinePZ(combineList, pk_corp, userid,
                                key, null, setvo, accway, true, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
                        msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
                    } catch (Exception ex) {
                        errorCount++;
                        if(ex instanceof BusinessException){
                            msg.append("<font color='red'><p>销项发票入账期间为")
                                    .append(key)
                                    .append("的单据生成凭证失败，原因：")
                                    .append(ex.getMessage())
                                    .append("。</p></font>");
                        }else{
                            msg.append("<font color='red'><p>销项发票入账期间为")
                                    .append(key)
                                    .append("的单据生成凭证失败。</p></font>");
                        }
                    }
                }else if(!StringUtil.isEmpty(e.getMessage())
                        && (e.getMessage().startsWith("销项发票")
                        || e.getMessage().startsWith("制单失败"))){
                    errorCount++;
                    msg.append("<font color='red'><p>销项发票入账期间为")
                            .append(key)
                            .append("的单据生成凭证失败，原因:")
                            .append(e.getMessage())
                            .append("。</p></font>");
                }else{
                    errorCount++;
                    msg.append("<font color='red'><p>销项发票入账期间为")
                            .append(key)
                            .append("的单据生成凭证失败。</p></font>");
                }
            }
        }

        return errorCount;
    }

    private int dealAfterTicketByIC(Map<String, VATSaleInvoiceVO2> map, StringBuffer msg){
        List<VATSaleInvoiceVO2> list = buildMap2List(map);
        int errorCount = 0;
        String pk_corp = SystemUtil.getLoginCorpId();
        String userid = SystemUtil.getLoginUserId();

        //Map<String, DcModelHVO> dcmap = gl_yhdzdserv2.queryDcModelVO(pk_corp);
        YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);

        if(list == null || list.size() == 0){
            return errorCount;
        }
        String mss = processGoods(list, pk_corp, userid);
        if(!StringUtil.isEmpty(mss)){
            msg.append("<font color='red'><p>存货匹配失败,原因:"+mss+"</p></font>");
        }
        String kplx = null;

        Map<String, List<VATSaleInvoiceVO2>> combineMap = new LinkedHashMap<String, List<VATSaleInvoiceVO2>>();
        VatInvoiceSetVO setvo = queryRuleByType();
        List<VATSaleInvoiceVO2> combineList = null;
        //DcModelHVO modelHVO = null;
        //DcModelBVO[] modelbvos = null;
        //DcModelBVO modelbvo = null;
        String key = null;
        List<VATSaleInvoiceVO2> errorList = new ArrayList<VATSaleInvoiceVO2>();
        for(VATSaleInvoiceVO2 vo : list){

            //modelHVO = dcmap.get(vo.getPk_model_h());
            if(StringUtils.isEmpty(vo.getPk_model_h())){
                errorCount++;
                msg.append("<font color='red'><p>销项发票[" + vo.getFp_hm() + "," + vo.getFp_hm() + "]业务类型未找到，请选择相应业务类型</p></font>");
                continue;
            }

			/*modelbvos = modelHVO.getChildren();
			int len = modelbvos == null ? 0 : modelbvos.length;
			boolean flag = false;
			String kmbm = null;
			for(int i = 0; i < len; i++){
				modelbvo = modelbvos[i];
				kmbm = modelbvo.getKmbm();
				if(!StringUtil.isEmpty(kmbm) && (kmbm.startsWith("5001")
						|| kmbm.startsWith("6001"))){
					flag = true;
					break;
				}
			}

			if(!flag){
				errorCount++;
				errorList.add(vo);
				continue;
			}*/

            key = buildkey(vo, setvo);

            kplx = vo.getKplx();
            if(!StringUtil.isEmpty(kplx)
                    && (ICaiFangTongConstant.FPLX_3.equals(kplx)//空白废票
                    || ICaiFangTongConstant.FPLX_4.equals(kplx)//正废
                    || ICaiFangTongConstant.FPLX_5.equals(kplx))){//负废
                errorCount++;
                msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成出库单</p></font>");
            }else if(!StringUtil.isEmpty(vo.getPk_tzpz_h())){
                errorCount++;
                msg.append("<font color='red'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成出库单。</p></font>");
            }else if(StringUtil.isEmpty(vo.getPk_model_h())){
                errorCount++;
                msg.append("<font color='red'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据没有业务类型，不能生成出库单。</p></font>");
            }else if(combineMap.containsKey(key)){
                combineList = combineMap.get(key);

                combineList.add(vo);

            }else{
                combineList = new ArrayList<VATSaleInvoiceVO2>();
                combineList.add(vo);
                combineMap.put(key, combineList);
            }
        }

        key = null;

        IntradeHVO ihvo = null;
        List<IntradeHVO> ihvoList = null;
        CorpVO corpvo=corpService.queryByPk(pk_corp);
        Map<String,YntCpaccountVO> accountMap = accountService.queryMapByPk(corpvo.getPk_corp());
        YntCpaccountVO[] accVOs=accountService.queryByPk(corpvo.getPk_corp());
        Map<String, Object> paramMap=null;
        List<List<Object[]>> levelList=null;
        Map<String, Object[]> categoryMap =null;
        Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap=null;
        Set<String> zyFzhsList=null;
        Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap=null;
        InventorySetVO inventorySetVO=null;
        Map<String, InventoryAliasVO> fzhsBMMap=null;
        List<Object> paramList = null;
        Map<String, BdCurrencyVO> currMap=null;
        Map<String, Object[]> rateMap=null;
        Map<String, String> bankAccountMap=null;
        Map<String, AuxiliaryAccountBVO> assistMap=null;
        Map<String, List<AccsetVO>> accsetMap=null;
        Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map=null;
        Map<String, String> jituanSubMap=null;
        String tradeCode=null;
        String newrule = null;
        List<AuxiliaryAccountBVO> chFzhsBodyVOs=null;
        Map<String, Map<String, Object>> periodMap=new HashMap<String, Map<String, Object>>();
        for(Map.Entry<String, List<VATSaleInvoiceVO2>> entry : combineMap.entrySet()){
            try {
                combineList = entry.getValue();

                ihvoList = new ArrayList<IntradeHVO>();
                for(VATSaleInvoiceVO2 vo : combineList){

                    if(periodMap.containsKey(vo.getPeriod())){
                        paramMap=periodMap.get(vo.getPeriod());
                    }else{
                        paramMap=zncsVoucher.initVoucherParam(corpvo, vo.getPeriod(),false);
                        periodMap.put(vo.getPeriod(), paramMap);
                    }
                    levelList=(List<List<Object[]>>) paramMap.get("levelList");
                    categoryMap =(Map<String, Object[]>) paramMap.get("categoryMap");
                    fzhsHeadMap=(Map<Integer, AuxiliaryAccountHVO>) paramMap.get("fzhsHeadMap");
                    zyFzhsList=(Set<String>) paramMap.get("zyFzhsList");
                    fzhsBodyMap=(Map<String, List<AuxiliaryAccountBVO>>) paramMap.get("fzhsBodyMap");
                    inventorySetVO=(InventorySetVO) paramMap.get("inventorySetVO");
                    fzhsBMMap=(Map<String, InventoryAliasVO>) paramMap.get("fzhsBMMap");
                    paramList = (List<Object>) paramMap.get("paramList");
                    currMap=(Map<String, BdCurrencyVO>) paramMap.get("currMap");
                    rateMap=(Map<String, Object[]>) paramMap.get("rateMap");
                    bankAccountMap=(Map<String, String>) paramMap.get("bankAccountMap");
                    assistMap=(Map<String, AuxiliaryAccountBVO>) paramMap.get("assistMap");
                    accsetMap=(Map<String, List<AccsetVO>>) paramMap.get("accsetMap");
                    accsetKeywordBVO2Map=(Map<String, List<AccsetKeywordBVO2>>) paramMap.get("accsetKeywordBVO2Map");
                    jituanSubMap=(Map<String, String>) paramMap.get("jituanSubMap");
                    tradeCode=(String) paramMap.get("tradeCode");
                    newrule = (String) paramMap.get("newrule");
                    chFzhsBodyVOs=(List<AuxiliaryAccountBVO>) paramMap.get("chFzhsBodyVOs");

                    try {
                        ihvo = gl_vatsalinvserv2.createIC(vo, accounts, SystemUtil.getLoginCorpVo(), userid, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
                        ihvoList.add(ihvo);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        if(e instanceof BusinessException
                                && !StringUtil.isEmpty(e.getMessage())){
                            errorCount++;
                            msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成出库单失败，原因:")
                                    .append(e.getMessage())
                                    .append("。</p></font>");
                        }else{
                            errorCount++;
                            msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成出库单失败。</p></font>");
                        }
                    }
                }
                //汇总转总账
//				if(ihvoList.size() > 0){
//					gl_vatsalinvserv2.saveTotalGL(
//							ihvoList.toArray(new IntradeHVO[0]), pk_corp, userid);
//				}

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if(e instanceof BusinessException
                        && !StringUtil.isEmpty(e.getMessage())){
                    errorCount++;
                    msg.append("<font color='red'><p>销项发票清单的单据生成出库单失败，原因:")
                            .append(e.getMessage())
                            .append("。</p></font>");
                }else{
                    errorCount++;
                    msg.append("<font color='red'><p>销项发票清单的单据生成出库单失败。</p></font>");
                }
            }
        }

        //提示信息
        int len = errorList.size();
        if(len > 0){
            StringBuffer msg1 = new StringBuffer();
            msg1.append("<font color='red'><p>销项发票[");
            VATSaleInvoiceVO2 ervo = null;
            for(int i=0; i < len; i++){
                ervo = errorList.get(i);
                msg1.append(ervo.getFp_hm());

                if(i != len -1){
                    msg1.append(", ");
                }
            }

            msg1.append("]出库检查失败:入账设置的科目未找到主营业务收入</p></font>");
            msg.append(msg1.toString());
        }

        return errorCount;
    }
    public String processGoods(List<VATSaleInvoiceVO2> list,String pk_corp,String userid ){

        CorpVO corpVo = corpService.queryByPk(pk_corp);
        //处理存货匹配yinyx1
        try {
            if(IcCostStyle.IC_INVTENTORY.equals(corpVo.getBbuildic())){
                InventorySetVO invsetvo = query();
                if (invsetvo == null)
                    return "存货设置未设置!";

                List<InventoryAliasVO> relvos = gl_vatsalinvserv2.matchInventoryData(pk_corp, list.toArray(new VATSaleInvoiceVO2[0]),invsetvo);
                String error = ocrinterface.checkInvtorySubj(relvos.toArray(new InventoryAliasVO[0]), invsetvo, pk_corp, SystemUtil.getLoginUserId(), false);
                if (!StringUtil.isEmpty(error)) {
                    error = error.replaceAll("<br>", " ");
                    throw new BusinessException("进项发票存货匹配失败:"+error);
                }
                List<Grid> logList = new ArrayList<Grid>();//记录更新日志
                if(relvos!=null&&relvos.size()>0){
                    gl_vatincinvact2.saveInventoryData(pk_corp, relvos.toArray(new InventoryAliasVO[0]), logList);
                }
            }else if(IcCostStyle.IC_ON.equals(corpVo.getBbuildic())){
                List<VatGoosInventoryRelationVO> relvos = gl_vatsalinvserv2.getGoodsInvenRela(list, pk_corp);
                if(relvos!=null &&relvos.size()>0){
                    goodsToVatBVO(list, relvos.toArray(new VatGoosInventoryRelationVO[0]), pk_corp, userid);
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }

        return null;
    }
    private List<VATSaleInvoiceVO2> buildMap2List(Map<String, VATSaleInvoiceVO2> map){
        List<VATSaleInvoiceVO2> list = new ArrayList<VATSaleInvoiceVO2>();
        VATSaleInvoiceVO2 vo = null;

        for(Map.Entry<String, VATSaleInvoiceVO2> entry : map.entrySet()){
            vo  = entry.getValue();

            list.add(vo);
        }

        return list;

    }

    @RequestMapping("/queryTaxItems")
    public ReturnData<Grid> queryTaxItems(){
        Grid grid = new Grid();
        try{

            List<TaxitemVO> vos = gl_vatsalinvserv2.queryTaxItems(SystemUtil.getLoginCorpId());

            grid.setRows(vos);
            grid.setSuccess(true);
            grid.setMsg("获取税目信息成功");
        }catch(Exception e){
            printErrorLog(grid, e, "获取税目信息失败");
        }

        return ReturnData.ok().data(grid);
    }

    @RequestMapping("/queryRule")
    public ReturnData<Json> queryRule(){
        Json json = new Json();
        try {
            VatInvoiceSetVO[] vos = vatinvoiceserv.queryByType(SystemUtil.getLoginCorpId(), IBillManageConstants.HEBING_XXFP);

            VatInvoiceSetVO vo = null;
            if(vos != null && vos.length > 0){
                vo = vos[0];
            }

            json.setRows(vo);
            json.setMsg("查询合并规则成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "查询合并规则失败");
        }

//		writeLogRecord(LogRecordEnum.OPE_KJ_PJGL.getValue(),
//				"银行对账单更新业务类型", ISysConstants.SYS_2);
       return ReturnData.ok().data(json);
    }

    @RequestMapping("/combineRule")
    public ReturnData<Json> combineRule(@RequestBody Map<String,String> param){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            String pzrq = param.get("pzrq");
            String pzrule = param.get("pzrule");
            String flrule = param.get("flrule");
            String zy = param.get("zy");
            String setId = param.get("setid");
            String bk = param.get("bk");
            if(StringUtil.isEmpty(pzrule)
                    || StringUtil.isEmpty(flrule)||StringUtil.isEmpty(pzrq)){
                throw new BusinessException("合并规则设置失败，请重试");
            }

            VatInvoiceSetVO vo = new VatInvoiceSetVO();
            String[] fields = new String[]{ "pzrq","value", "entry_type", "isbank", "zy" };
            if(!StringUtil.isEmpty(setId)){
                vo.setPrimaryKey(setId);
            }else{
                vo.setPk_corp(pk_corp);
                vo.setStyle(IBillManageConstants.HEBING_XXFP);
            }
            vo.setPzrq(Integer.parseInt(pzrq));
            vo.setValue(Integer.parseInt(pzrule));
            vo.setEntry_type(Integer.parseInt(flrule));
            vo.setZy(zy);

            if(StringUtil.isEmpty(bk)){
                vo.setIsbank(null);
            }else{
                vo.setIsbank(DZFBoolean.TRUE);
            }

            vatinvoiceserv.updateVO(pk_corp, vo, fields);
            json.setMsg("合并规则设置成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "合并规则设置失败");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "合并规则调整", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    /**
     * 生成出库
     */
    @RequestMapping("/createIC")
    public ReturnData<Json> createIC(@RequestParam("head") String body,String good){
        Json json = new Json();
        VATSaleInvoiceVO2[] vos = null;
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        boolean lock = false;
        try {

            String userid  = SystemUtil.getLoginUserId();
            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("xiaoxiang2ic"+pk_corp);
            if(!lock){//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍候");
                return ReturnData.error().data(json);
            }


            CorpVO corpvo = corpService.queryByPk(pk_corp);
            checkBeforeIC(corpvo);

            if (body == null) {
                throw new BusinessException("数据为空,生成出库单失败!");
            }
//            Map<String, String> bodymapping = FieldMapping.getFieldMapping(new VATSaleInvoiceVO2());
//            vos = DzfTypeUtils.cast(array, bodymapping, VATSaleInvoiceVO2[].class,
//                    JSONConvtoJAVA.getParserConfig());
            vos = JsonUtils.deserialize(body, VATSaleInvoiceVO2[].class);
            if(vos == null || vos.length == 0)
                throw new BusinessException("数据为空,生成出库单失败，请检查");

            List<VATSaleInvoiceVO2> stoList = gl_vatsalinvserv2.constructVatSale(vos, pk_corp);

            if(stoList == null
                    || stoList.size() == 0)
                throw new BusinessException("未找销项发票数据，请检查");

            VatGoosInventoryRelationVO[] goods = getGoodsData(good);
            goodsToVatBVO(stoList, goods, pk_corp, userid);

            YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);
            IntradeHVO ihvo = null;
            int errorCount = 0;
            StringBuffer msg = new StringBuffer();

            List<String> periodSet= new ArrayList<String>();
            String kplx = null;
            Map<String,YntCpaccountVO> accountMap = accountService.queryMapByPk(corpvo.getPk_corp());
            YntCpaccountVO[] accVOs=accountService.queryByPk(corpvo.getPk_corp());
            Map<String, Object> paramMap=null;
            List<List<Object[]>> levelList=null;
            Map<String, Object[]> categoryMap =null;
            Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap=null;
            Set<String> zyFzhsList=null;
            Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap=null;
            InventorySetVO inventorySetVO=null;
            Map<String, InventoryAliasVO> fzhsBMMap=null;
            List<Object> paramList = null;
            Map<String, BdCurrencyVO> currMap=null;
            Map<String, Object[]> rateMap=null;
            Map<String, String> bankAccountMap=null;
            Map<String, AuxiliaryAccountBVO> assistMap=null;
            Map<String, List<AccsetVO>> accsetMap=null;
            Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map=null;
            Map<String, String> jituanSubMap=null;
            String tradeCode=null;
            String newrule = null;
            List<AuxiliaryAccountBVO> chFzhsBodyVOs=null;
            Map<String, Map<String, Object>> periodMap=new HashMap<String, Map<String, Object>>();
            for(VATSaleInvoiceVO2 vo : stoList){
                try {

                    kplx = vo.getKplx();

                    if(periodMap.containsKey(vo.getPeriod())){
                        paramMap=periodMap.get(vo.getPeriod());
                    }else{
                        paramMap=zncsVoucher.initVoucherParam(corpvo, vo.getPeriod(),false);
                        periodMap.put(vo.getPeriod(), paramMap);
                    }
                    levelList=(List<List<Object[]>>) paramMap.get("levelList");
                    categoryMap =(Map<String, Object[]>) paramMap.get("categoryMap");
                    fzhsHeadMap=(Map<Integer, AuxiliaryAccountHVO>) paramMap.get("fzhsHeadMap");
                    zyFzhsList=(Set<String>) paramMap.get("zyFzhsList");
                    fzhsBodyMap=(Map<String, List<AuxiliaryAccountBVO>>) paramMap.get("fzhsBodyMap");
                    inventorySetVO=(InventorySetVO) paramMap.get("inventorySetVO");
                    fzhsBMMap=(Map<String, InventoryAliasVO>) paramMap.get("fzhsBMMap");
                    paramList = (List<Object>) paramMap.get("paramList");
                    currMap=(Map<String, BdCurrencyVO>) paramMap.get("currMap");
                    rateMap=(Map<String, Object[]>) paramMap.get("rateMap");
                    bankAccountMap=(Map<String, String>) paramMap.get("bankAccountMap");
                    assistMap=(Map<String, AuxiliaryAccountBVO>) paramMap.get("assistMap");
                    accsetMap=(Map<String, List<AccsetVO>>) paramMap.get("accsetMap");
                    accsetKeywordBVO2Map=(Map<String, List<AccsetKeywordBVO2>>) paramMap.get("accsetKeywordBVO2Map");
                    jituanSubMap=(Map<String, String>) paramMap.get("jituanSubMap");
                    tradeCode=(String) paramMap.get("tradeCode");
                    newrule = (String) paramMap.get("newrule");
                    chFzhsBodyVOs=(List<AuxiliaryAccountBVO>) paramMap.get("chFzhsBodyVOs");

                    if(!StringUtil.isEmpty(kplx)
                            && (ICaiFangTongConstant.FPLX_3.equals(kplx)//空白废票
                            || ICaiFangTongConstant.FPLX_4.equals(kplx)//正废
                            || ICaiFangTongConstant.FPLX_5.equals(kplx))){//负废
                        errorCount++;
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成出库单。</p></font>");
                    }else if(StringUtil.isEmpty(vo.getPk_tzpz_h())){
                        ihvo = gl_vatsalinvserv2.createIC(vo, accounts, corpvo, userid, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
//						gl_vatsalinvserv2.saveGL(ihvo, pk_corp, userid);
                        //成功的时候提示(是否损益结转,成本结转等)
                        if(ihvo.getDbilldate()!=null){
                            periodSet.add(ihvo.getDbilldate().toString().substring(0, 7));
                        }
                        msg.append("<font color='#2ab30f'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成出库单成功。</p></font>");
                    }else{
                        errorCount++;
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成出库单。</p></font>");
                    }
                    ihvo = null;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);

                    String err = ihvo == null ? "出库单" : "凭证";
                    if(e instanceof BusinessException
                            && !StringUtil.isEmpty(e.getMessage())){
                        errorCount++;
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成" +err+ "失败，原因:")
                                .append(e.getMessage())
                                .append("。</p></font>");
                    }else{
                        errorCount++;
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成" +err+ "失败。</p></font>");
                    }
                }
            }

            //zhangj
            StringBuffer headMsg = gl_yhdzdserv2.buildQmjzMsg(periodSet, pk_corp);
            if(headMsg != null && headMsg.length() > 0){
                headMsg.append(msg.toString());
                msg = headMsg;
            }

            json.setSuccess(errorCount > 0 ? false : true);
            json.setMsg(msg.toString());
        } catch (Exception e) {
            printErrorLog(json, e, "生成出库单失败");
        } finally {
            if(lock){
                redissonDistributedLock.releaseDistributedFairLock("xiaoxiang2ic"+pk_corp);
            }
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "销项发票生成出库单", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private void checkBeforeIC(CorpVO corpvo){
        Integer icstyle = corpvo.getIbuildicstyle();
        if(icstyle == null || icstyle != 1){//只针对库存新模式
            throw new BusinessException("销项生成出库单只支持库存新模式。");
        }

        if(!IcCostStyle.IC_ON.equals(corpvo.getBbuildic())){
            throw new BusinessException("销项生成出库单前需启用库存模块。");
        }

    }
    @RequestMapping("/matchInventoryData_long")
    public ReturnData<Grid> matchInventoryData_long(@RequestBody Map<String,String> param) {
        Grid grid = new Grid();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            String body = param.get("head");
            String isshow = param.get("ishow");
            String goods = param.get("goods");

            if (body == null) {
                throw new BusinessException("数据为空,存货匹配失败!");
            }

            VATSaleInvoiceVO2[] vos = JsonUtils.deserialize(body, VATSaleInvoiceVO2[].class);
            if (vos == null || vos.length == 0)
                throw new BusinessException("数据为空,存货匹配失败，请检查");
            List<VATSaleInvoiceVO2> stoList = gl_vatsalinvserv2.constructVatSale(vos, pk_corp);

            StringBuffer msg = new StringBuffer();
            String kplx = null;

            List<VATSaleInvoiceVO2> list = new ArrayList<>();
            for (VATSaleInvoiceVO2 vo : stoList) {
//				gl_vatsalinvserv2.checkvoPzMsg(vo.getPk_vatsaleinvoice());
                if(vo==null||!StringUtils.isEmpty(vo.getImgpath())){
                    throw new BusinessException("发票号码：'" + vo.getFp_hm() + "' 是智能识别票据，请至票据工作台进行相关处理！");
                }
                kplx = vo.getKplx();
                if(!StringUtil.isEmpty(kplx)
                        && (ICaiFangTongConstant.FPLX_3.equals(kplx)//空白废票
                        || ICaiFangTongConstant.FPLX_4.equals(kplx)//正废
                        || ICaiFangTongConstant.FPLX_5.equals(kplx))){//负废
                    msg.append("<p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成凭证。</p>");
                } else if (!StringUtil.isEmpty(vo.getPk_tzpz_h())) {

                    msg.append("<p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成凭证。</p>");
                }else{
                    list.add(vo);
                }
            }

            if(StringUtil.isEmpty(isshow)){
                isshow ="Y";
            }
            if(list ==null || list.size()==0){
                grid.setSuccess(false);
                grid.setMsg(msg.toString());
            }else{
                InventorySetVO invsetvo = query();
                if (invsetvo == null)
                    throw new BusinessException("存货设置未设置!");
//				String error = inventory_setcheck.checkInventorySet(getLoginUserid(), getLogincorppk(),invsetvo);
//				if (!StringUtil.isEmpty(error)) {
//					error = error.replaceAll("<br>", " ");
//					throw new BusinessException("销项发票存货匹配失败:"+error);
//				}


                List<InventoryAliasVO> relvos = gl_vatsalinvserv2.matchInventoryData(pk_corp, vos,invsetvo);

                if(relvos != null && relvos.size()>0){
                    String error = ocrinterface.checkInvtorySubj(relvos.toArray(new InventoryAliasVO[0]), invsetvo, pk_corp, SystemUtil.getLoginUserId(), false);
                    if (!StringUtil.isEmpty(error)) {
                        error = error.replaceAll("<br>", " ");
                        throw new BusinessException("销项发票存货匹配失败:"+error);
                    }
                    InventoryAliasVO[] goodvos = getInvAliasData(goods);
                    String[] keys = new String[]{"aliasname","spec","invtype","unit"};
                    Map<String,InventoryAliasVO> map = null;
                    if (!DZFValueCheck.isEmpty(goodvos)){
                        map =DZfcommonTools.hashlizeObjectByPk(Arrays.asList(goodvos), keys);
                    }
                    //没有匹配上存货的默认新增
                    List<InventoryAliasVO>  addlist =new ArrayList<>();
                    DZFBoolean  show = new DZFBoolean(isshow);
                    for(InventoryAliasVO vo:relvos){
                        if(StringUtil.isEmpty(vo.getPk_inventory())){
                            vo.setIsAdd(DZFBoolean.TRUE);
                            String key = DZfcommonTools.getCombinesKey(vo, keys);
                            if(DZFMapUtil.isEmpty(map) || !map.containsKey(key)){
                                addlist.add(vo);
                            }else{
                                addlist.add(map.get(key));
                            }
                        }else{
                            if(show.booleanValue()){
                                addlist.add(vo);
                            }
                            vo.setIsAdd(DZFBoolean.FALSE);
                        }
                    }
                    relvos =addlist;

                    Collections.sort(relvos, new Comparator<InventoryAliasVO>() {
                        @Override
                        public int compare(InventoryAliasVO o1, InventoryAliasVO o2) {
                            return o2.getIsAdd().compareTo(o1.getIsAdd());
                        }
                    });
                }else{
                    //throw new BusinessException("所选非存货无需匹配存货!");
                }

                grid.setRows(relvos);
                grid.setSuccess(true);
                grid.setMsg("销项发票存货匹配成功");
            }
        } catch (Exception e) {
            printErrorLog(grid, e, "销项发票存货匹配失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "销项发票存货匹配", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    @RequestMapping("/saveInventoryData")
    public ReturnData<Grid> saveInventoryData(@RequestBody Map<String,String> param) {
        Grid grid = new Grid();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        boolean lock = false;
        String goods = param.get("goods");
        try {
            // 加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("xiaoxiang2pp"+pk_corp);
            if (!lock) {// 处理
                grid.setSuccess(false);
                grid.setMsg("正在处理中，请稍候");
                return ReturnData.error().data(grid);
            }

            InventoryAliasVO[] goodvos = getInvAliasData(goods);
            if (goodvos == null || goodvos.length == 0)
                throw new BusinessException("未找到存货别名数据，请检查");
            InventorySetVO invsetvo = query();
            String error = ocrinterface.checkInvtorySubj(goodvos, invsetvo, pk_corp,SystemUtil.getLoginUserId(), true);
            if (!StringUtil.isEmpty(error)) {
                error = error.replaceAll("<br>", " ");
                throw new BusinessException("销项发票存货匹配失败:"+error);
            }

            List<Grid> logList = new ArrayList<Grid>();//记录更新日志
            gl_vatsalinvserv2.saveInventoryData(pk_corp, goodvos, logList);
            //保存操作日志
            for(Grid loggrid: logList){
                writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "销项发票_"+loggrid.getMsg(), ISysConstants.SYS_2);
            }
            grid.setSuccess(true);
            grid.setMsg("销项发票匹配存货成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "销项发票匹配存货失败");
        } finally {
            if(lock){
                redissonDistributedLock.releaseDistributedFairLock("xiaoxiang2pp"+pk_corp);
            }
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "销项发票匹配存货", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    @RequestMapping("/createPzData_long")
    public ReturnData<Grid> createPzData_long(@RequestBody Map<String,String> param) {
        String sourcename = param.get("sourcename");
        String body  = param.get("head");
        String jsfs = param.get("jsfs");
        String inperiod = param.get("inperiod");
        String goods = param.get("goods");
        Grid grid = new Grid();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        boolean lock = false;
        String sourceName = StringUtil.isEmpty(sourcename)?"填制凭证_":sourcename+"_";
        try {
            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("xiaoxiang2pz"+pk_corp);
            if(!lock){//处理
                grid.setSuccess(false);
                grid.setMsg("正在处理中，请稍候");
                return ReturnData.error().data(grid);
            }
            if (body == null) {
                throw new BusinessException("数据为空,生成凭证失败!");
            }


            VATSaleInvoiceVO2[] vos = JsonUtils.deserialize(body,VATSaleInvoiceVO2[].class);
            if (vos == null || vos.length == 0)
                throw new BusinessException("数据为空,生成凭证失败，请检查");

            List<VATSaleInvoiceVO2> stoList = gl_vatsalinvserv2.constructVatSale(vos, pk_corp);

            if (stoList == null || stoList.size() == 0){
                throw new BusinessException("未找销项发票数据，请检查");
            }

            Set<String> setPk_model_h = new HashSet<String>();
            for (VATSaleInvoiceVO2 vo : stoList) {
//				gl_vatsalinvserv2.checkvoPzMsg(vo.getPk_vatsaleinvoice());
                if(vo==null||!StringUtils.isEmpty(vo.getImgpath())){
                    throw new BusinessException("发票号码：'" + vo.getFp_hm() + "' 是智能识别票据，请至票据工作台进行相关处理！");
                }
                if (vo.getPk_model_h() != null && !setPk_model_h.contains(vo.getPk_model_h())){
                    setPk_model_h.add(vo.getPk_model_h());
                }

            }
            //检查是否包含旧业务类型
            if (setPk_model_h.size() > 0)
            {
                checkPK_Model_H(setPk_model_h, true);
            }
            else
            {
                throw new BusinessException("请设置业务类型！");
            }

            //检查是否包含存货类型
			/*String checkMsg = gl_vatsalinvserv2.checkIsStock(stoList);
			if(!StringUtils.isEmpty(checkMsg)){
				throw new BusinessException(checkMsg);
			}*/

            InventorySetVO invsetvo = query();
            Map<String, YntCpaccountVO> ccountMap = accountService.queryMapByPk( pk_corp);
            if (invsetvo == null)
                throw new BusinessException("存货设置未设置!");
            int chcbjzfs = invsetvo.getChcbjzfs();

//			String error = inventory_setcheck.checkInventorySet(getLoginUserid(), getLogincorppk(),invsetvo);
//			if (!StringUtil.isEmpty(error)) {
//				error = error.replaceAll("<br>", " ");
//				throw new BusinessException("销项发票生成凭证失败:"+error);
//			}

//			if (chcbjzfs == 2) {// 不核算明细
//				YntCpaccountVO cvo = getRkkmVO(invsetvo, ccountMap);
//				if (cvo.getIsfzhs().charAt(5) == '1') {
//					throw new BusinessException("科目【" + cvo.getAccountname() + "】已启用存货辅助!");
//				}
//			}

            if (chcbjzfs != 2) {
                InventoryAliasVO[] goodvos = getInvAliasData(goods);
                if(goodvos != null &&goodvos.length> 0){

                    String error = ocrinterface.checkInvtorySubj(goodvos, invsetvo, pk_corp, SystemUtil.getLoginUserId(), false);
                    if (!StringUtil.isEmpty(error)) {
                        error = error.replaceAll("<br>", " ");
                        throw new BusinessException("销项发票生成凭证失败:"+error);
                    }

                    List<InventoryAliasVO> list = new ArrayList<>();
                    for(InventoryAliasVO  good:goodvos){
                        if(good.getIsMatch() == null || !good.getIsMatch().booleanValue())
                            list.add(good);
                    }
                    List<Grid> logList = new ArrayList<Grid>();//记录更新日志
                    if(list != null && list.size()>0){
                        gl_vatsalinvserv2.saveInventoryData(pk_corp, list.toArray(new InventoryAliasVO[list.size()]), logList);
                    }
                    //保存操作日志
                    for(Grid loggrid: logList){
                        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, sourceName+loggrid.getMsg(), ISysConstants.SYS_2);
                    }
                }
            }

            if(!StringUtil.isEmpty(inperiod)){
                for (VATSaleInvoiceVO2 vo : stoList) {
                    vo.setInperiod(inperiod);
                }
            }

            List<Object>  list= combinePZData(stoList, invsetvo, jsfs);
            int errcount = (int)list.get(0);
            String msg = (String)list.get(1);

            if (!StringUtil.isEmpty(msg)) {
                grid.setMsg(msg);
                grid.setSuccess(errcount > 0 ? false : true);
            }else{
                grid.setSuccess(true);
                grid.setMsg("");
            }

        } catch (Exception e) {
            printErrorLog(grid, e, "销项发票生成凭证失败");
        }finally {
            if(lock){
                redissonDistributedLock.releaseDistributedFairLock("xiaoxiang2pz"+pk_corp);
            }
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "销项发票生成凭证", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    private YntCpaccountVO getRkkmVO(InventorySetVO invsetvo, Map<String, YntCpaccountVO> ccountMap) {
        String pk_accsubj = invsetvo.getKcspckkm();
        if (StringUtil.isEmpty(pk_accsubj)) {
            throw new BusinessException("出库科目未设置!");
        }

        YntCpaccountVO cvo = ccountMap.get(pk_accsubj);
        if (cvo == null)
            throw new BusinessException("科目不存在，或已经被删除");

        boolean isleaf = cvo.getIsleaf() == null ? false : cvo.getIsleaf().booleanValue();

        if (!isleaf) {//
            // 第一个下级的最末级
            cvo = getFisrtNextLeafAccount(cvo.getAccountcode(), ccountMap);
        }
        return cvo;
    }

    // 查询第一分支的最末级科目
    private YntCpaccountVO getFisrtNextLeafAccount(String accountcode, Map<String, YntCpaccountVO> ccountMap) {

        List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();// 存储下级科目
        for (YntCpaccountVO accvo : ccountMap.values()) {
            if (accvo.getIsleaf().booleanValue() && accvo.getAccountcode() != null
                    && accvo.getAccountcode().startsWith(accountcode)) {
                list.add(accvo);
            }
        }

        if (list == null || list.size() == 0) {
            return null;
        }
        YntCpaccountVO[] accountvo = list.toArray(new YntCpaccountVO[list.size()]);
        VOUtil.ascSort(accountvo, new String[] { "accountcode" });
        return accountvo[0];
    }


    private InventoryAliasVO[] getInvAliasData(String goods) {

        InventoryAliasVO[] vos = null;

        if (StringUtil.isEmpty(goods)) {
            return null;
        }
        vos = JsonUtils.deserialize(goods,InventoryAliasVO[].class);
        return vos;
    }


    private  List<Object>  combinePZData(List<VATSaleInvoiceVO2> stoList, InventorySetVO invsetvo, String jsfs) {
        VatInvoiceSetVO setvo = queryRuleByType();
        String numStr = parameterserv.queryParamterValueByCode(SystemUtil.getLoginCorpId(), IParameterConstants.DZF009);
        int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
        int chcbjzfs = invsetvo.getChcbjzfs();
        if (chcbjzfs != 2) {
            List<InventoryAliasVO> alist = gl_vatsalinvserv2.matchInventoryData(SystemUtil.getLoginCorpId(),
                    stoList.toArray(new VATSaleInvoiceVO2[stoList.size()]),invsetvo);

            if (alist == null || alist.size() == 0)
                throw new BusinessException("存货匹配信息出错");
            int pprule = invsetvo.getChppjscgz();//匹配规则
            for (VATSaleInvoiceVO2 incomvo : stoList) {

                SuperVO[] ibodyvos = (SuperVO[]) incomvo.getChildren();
                if (ibodyvos == null || ibodyvos.length == 0)
                    continue;
                for (SuperVO body : ibodyvos) {
                    VATSaleInvoiceBVO2 ibody = (VATSaleInvoiceBVO2) body;
                    String key1 =buildByRule( ibody.getBspmc(), ibody.getInvspec(), ibody.getMeasurename(), pprule);
                    for (InventoryAliasVO aliavo : alist) {
                        String key =buildByRule( aliavo.getAliasname(), aliavo.getSpec(), aliavo.getUnit(), pprule);
                        if (key1.equals(key)) {
                            ibody.setPk_inventory(aliavo.getPk_inventory());
                            ibody.setPk_accsubj(aliavo.getChukukmid());
                            // 根据换算方式 计算数量
							/*if(aliavo.getCalcmode()==0){
								ibody.setBnum(SafeCompute.multiply(ibody.getBnum(), aliavo.getHsl()).setScale(num,  DZFDouble.ROUND_HALF_UP));
							}else if(aliavo.getCalcmode()==1){
								ibody.setBnum(SafeCompute.div(ibody.getBnum(), aliavo.getHsl()).setScale(num,  DZFDouble.ROUND_HALF_UP));
							}*/
                            continue;
                        }
                    }
                }
            }
        }
        List<Object> list = null;
        if (setvo == null || setvo.getValue() == null || setvo.getValue() == IBillManageConstants.HEBING_GZ_01) {
            list = createPZData(stoList, setvo, invsetvo, jsfs);
        } else {
            list = combinePZData1(stoList, setvo, invsetvo, jsfs);
//			list = createPZData(stoList, invsetvo, jsfs);
        }
        return list;
    }

    private String buildByRule(String name, String gg, String unit, int rule){
        String key = null;
        if(rule == InventoryConstant.IC_RULE_1){//存货名称+计量单位
            key = name + ",null," + unit;
        }else{
            key = name + "," + gg + "," + unit;
        }

        return key;
    }

    /**
     * 单独生成凭证
     */
    private List<Object> createPZData(List<VATSaleInvoiceVO2> stoList, VatInvoiceSetVO setvo, InventorySetVO invsetvo, String jsfs) {
        String pk_corp = SystemUtil.getLoginCorpId();

        boolean accway = getAccWay(pk_corp);

        String userid = SystemUtil.getLoginUserId();
        StringBuffer msg = new StringBuffer();
        List<Object> list = new ArrayList<>();
        int err=0;
        String kplx = null;
        String key;
        String period = null;
        List<String> periodSet = new ArrayList<String>();
        for (VATSaleInvoiceVO2 vo : stoList) {
            try {

                kplx = vo.getKplx();

                key = buildkey(vo, setvo);
                period = splitKey(key);
                if(!periodSet.contains(period)){
                    periodSet.add(period);//组装查询期间
                }

                if(!StringUtil.isEmpty(kplx)
                        && (ICaiFangTongConstant.FPLX_3.equals(kplx)//空白废票
                        || ICaiFangTongConstant.FPLX_4.equals(kplx)//正废
                        || ICaiFangTongConstant.FPLX_5.equals(kplx))){//负废
                    err++;
                    periodSet.remove(period);
                    msg.append("<font color='red'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成凭证。</p></font>");
                } else if (StringUtil.isEmpty(vo.getPk_tzpz_h())) {
                    try {
                        gl_vatsalinvserv2.createPZ(vo, pk_corp, userid, accway, false, setvo, invsetvo, jsfs);
                        msg.append("<font color='#2ab30f'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");
                    } catch (Exception e) {
                        gl_vatsalinvserv2.createPZ(vo, pk_corp, userid, accway, true, setvo, invsetvo, jsfs);
                        msg.append("<font color='#2ab30f'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");
                    }
                } else {
                    err++;
                    periodSet.remove(period);
                    msg.append("<font color='red'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成凭证。</p></font>");
                }
            } catch (Exception e) {

                log.error(e.getMessage(), e);
                err++;
                if (e instanceof BusinessException) {
                    msg.append("<font color='red'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败，原因:")
                            .append(e.getMessage()).append("。</p></font>");
                } else {
                    msg.append("<font color='red'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败").append("。</p></font>");
                }
                periodSet.remove(period);
            }
        }

        StringBuffer headMsg = gl_yhdzdserv2.buildQmjzMsg(periodSet, pk_corp);
        if(headMsg != null && headMsg.length() > 0){
            headMsg.append(msg.toString());
            msg = headMsg;
        }

        list.add(err);
        list.add(msg.toString());
        return list;
    }


    public InventorySetVO query() {
        InventorySetVO vo = gl_ic_invtorysetserv.query(SystemUtil.getLoginCorpId());
        return vo;
    }
    /**
     * 合并制证
     * @param stoList
     */
    private List<Object> combinePZData1(List<VATSaleInvoiceVO2> stoList, VatInvoiceSetVO setvo, InventorySetVO invsetvo,
                                        String jsfs) {
        String pk_corp = SystemUtil.getLoginCorpId();

        String userid = SystemUtil.getLoginUserId();

        boolean accway = getAccWay(pk_corp);
        Map<String, List<VATSaleInvoiceVO2>> combineMap = new LinkedHashMap<String, List<VATSaleInvoiceVO2>>();
        StringBuffer msg = new StringBuffer();
        String key;
        List<VATSaleInvoiceVO2> combineList = null;
        String kplx = null;
        String period;
        List<String> periodSet = new ArrayList<String>();
        List<Object> list = new ArrayList<>();
        int err=0;
        for (VATSaleInvoiceVO2 vo : stoList) {

            key = buildkey(vo, setvo);
            period = splitKey(key);
            if(!periodSet.contains(period)){
                periodSet.add(period);//组装查询期间
            }
            kplx = vo.getKplx();
            if(!StringUtil.isEmpty(kplx)
                    && (ICaiFangTongConstant.FPLX_3.equals(kplx)//空白废票
                    || ICaiFangTongConstant.FPLX_4.equals(kplx)//正废
                    || ICaiFangTongConstant.FPLX_5.equals(kplx))){//负废
                err++;
                periodSet.remove(period);
                msg.append("<font color='red'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成凭证</p></font>");
            } else if (!StringUtil.isEmpty(vo.getPk_tzpz_h())) {
                err++;
                periodSet.remove(period);
                msg.append("<font color='red'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成凭证。</p></font>");
            } else if (combineMap.containsKey(key)) {
                combineList = combineMap.get(key);

                combineList.add(vo);
            } else {
                combineList = new ArrayList<VATSaleInvoiceVO2>();
                combineList.add(vo);
                combineMap.put(key, combineList);
            }
        }

        key = null;
        for (Map.Entry<String, List<VATSaleInvoiceVO2>> entry : combineMap.entrySet()) {
            try {
                key = entry.getKey();
                key = splitKey(key);
                combineList = entry.getValue();
                gl_vatsalinvserv2.saveCombinePZ(combineList, pk_corp, userid, setvo, accway, false, invsetvo, jsfs);
                msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
            } catch (Exception e) {
                try {
                    gl_vatsalinvserv2.saveCombinePZ(combineList, pk_corp, userid, setvo, accway, true, invsetvo, jsfs);
                    msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
                } catch (Exception e2) {
                    err++;
                    log.error(e.getMessage(), e);
                    if (e instanceof BusinessException) {
                        msg.append("<font color='red'><p>入账期间为" + key + "的单据生成凭证失败，原因：")
                                .append(e.getMessage()).append("。</p></font>");
                    } else {
                        msg.append("<font color='red'><p>入账期间为" + key + "的单据生成凭证失败。</p></font>");
                    }
                    periodSet.remove(key);
                }


            }
        }

        StringBuffer headMsg = gl_yhdzdserv2.buildQmjzMsg(periodSet, pk_corp);
        if(headMsg != null && headMsg.length() > 0){
            headMsg.append(msg.toString());
            msg = headMsg;
        }

        list.add(err);
        list.add(msg.toString());
        return list;
    }

    /*public void getBusiType(){
        Json json = new Json();
        json.setSuccess(false);

        try {
            List<VatBusinessTypeVO> list = gl_vatsalinvserv2.getBusiType(getLogincorppk());

            json.setRows(list);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, log, e, "查询失败");
        }

        writeJson(json);
    }*/
    @RequestMapping("/queryCategoryRef")
    public ReturnData<Grid> queryCategoryRef(String period){
        Grid grid = new Grid();
        List<String> pk_categoryList = new ArrayList<String>();
        try {

            try {
                DZFDate date = DateUtils.getPeriodEndDate(period);
                if(date == null){
                    throw new BusinessException("入账期间解析错误，请检查");
                }
            }
            catch (Exception ex)
            {
                throw new BusinessException("入账期间解析错误，请检查");
            }

            CorpVO corpVO = SystemUtil.getLoginCorpVo();
            List<BillCategoryVO> list2 = schedulCategoryService.queryBillCategoryByCorpAndPeriod(corpVO.getPk_corp(), period);
            if (list2 == null || list2.size() == 0) {

                schedulCategoryService.newSaveCorpCategory(null, corpVO.getPk_corp(), period, corpVO);

            }
            //销项参照
            List<BillCategoryVO> list = gl_vatsalinvserv2.querySaleCategoryRef(corpVO.getPk_corp(),period);
            for (BillCategoryVO billCategoryVO : list) {
                pk_categoryList.add(billCategoryVO.getPk_category());
            }
            //查询全名称
            Map<String, String> map = zncsVoucher.queryCategoryFullName(pk_categoryList, period, corpVO.getPk_corp());
            for (BillCategoryVO billCategoryVO : list) {
                billCategoryVO.setCategoryname(map.get(billCategoryVO.getPk_category()));
            }
            log.info("查询成功！");
            grid.setRows(list==null?new ArrayList<BillCategoryVO>():list);
            grid.setSuccess(true);
            grid.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
        }

        return ReturnData.ok().data(grid);
    }
    @RequestMapping("/queryCategoryset")
    public ReturnData<Grid> queryCategoryset(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        try {
            String id =  param.get("id");
            String period = param.get("period");
            ArrayList<String> pk_categoryList = new ArrayList<String>();
            List<CategorysetVO> list = gl_vatincinvact2.queryIncomeCategorySet(id,SystemUtil.getLoginCorpId());
            for (CategorysetVO vo : list) {
                pk_categoryList.add(vo.getPk_category());
            }
            //查询全名称
            Map<String, String> map = zncsVoucher.queryCategoryFullName(pk_categoryList, period, SystemUtil.getLoginCorpId());
            for (CategorysetVO vo : list) {
                vo.setCategoryname(map.get(vo.getPk_category()));
            }
            log.info("查询成功！");
            grid.setRows(list==null?new ArrayList<BillCategoryVO>():list);
            grid.setSuccess(true);
            grid.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
        }

        return ReturnData.ok().data(grid);
    }

    @RequestMapping("/updateCategoryset")
    public ReturnData<Grid> updateCategoryset(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            String pk_model_h = param.get("pk_model_h");
            String busisztypecode = param.get("busisztypecode");
            String pk_basecategory = param.get("pk_basecategory");
            String pk_category_keyword = param.get("pk_category_keyword");
            String rzkm = param.get("rzkm");
            String jskm = param.get("jskm");
            String shkm = param.get("shkm");
            String zdyzy = param.get("zdyzy");
            String id = param.get("id");
            String[] ids = id.split(",");
//			for (String pk_vatsaleinvoice : ids) {
//				gl_vatsalinvserv2.checkvoPzMsg(pk_vatsaleinvoice);
//			}
            gl_vatsalinvserv2.updateVO(ids , pk_model_h,pk_corp,pk_category_keyword,busisztypecode,rzkm,jskm,shkm);
            gl_vatincinvact2.updateCategoryset(new DZFBoolean(true),pk_model_h,busisztypecode,pk_basecategory,pk_corp,rzkm,jskm,shkm,zdyzy);
            List<VATSaleInvoiceVO2> volist = gl_vatsalinvserv2.queryByPks(ids, pk_corp);
            log.info("设置成功！");
            grid.setRows(volist);
            grid.setSuccess(true);
            grid.setMsg("设置成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "设置失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "销项发票更新业务类型", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }
    private VATSaleInvoiceVO2 getHeadVO(List<VATSaleInvoiceVO2> list){
        DZFDouble zje = new DZFDouble();
        DZFDouble zse  = new DZFDouble();
        DZFDouble zjshj = new DZFDouble();

        if(list != null && list.size() > 0){
            for(VATSaleInvoiceVO2 vo : list){
                if(StringUtils.isEmpty(vo.getKplx())||(!vo.getKplx().equals("4")&&!vo.getKplx().equals("5"))){
                    zje = zje.add(vo.getHjje());
                    zse = zse.add(vo.getSpse());
                    zjshj = zjshj.add(vo.getJshj());
                }
            }
        }
        VATSaleInvoiceVO2 vo = new VATSaleInvoiceVO2();
        vo.setZje(zje);
        vo.setZse(zse);
        vo.setZjshj(zjshj);
        return vo;
    }
    private void saveOrUpdateCorpReference(VATSaleInvoiceVO2 salevo){
        CorpReferenceVO vo = new CorpReferenceVO();
        vo.setPk_corp(SystemUtil.getLoginCorpId());
        vo.setCorpname(salevo.getXhfmc());
        vo.setTaxnum(salevo.getXhfsbh());
        vo.setAddressphone(salevo.getXhfdzdh());
        vo.setBanknum(salevo.getXhfyhzh());
        vo.setIsjinxiang(0);
        vo.setDr(0);
        gl_vatincinvact2.saveOrUpdateCorpReference(vo);
    }

    @RequestMapping("/queryCorpReference")
    public ReturnData<Json> queryCorpReference(){
        Json json = new Json();
        try {
            String pk_corp = SystemUtil.getLoginCorpId();
            CorpReferenceVO referenceVO = gl_vatincinvact2.queryCorpReference(pk_corp,0);
            log.info("查询成功！");
            json.setData(referenceVO);
            json.setSuccess(true);
            json.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
        }

        return ReturnData.ok().data(json);
    }

}
