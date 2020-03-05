package com.dzf.zxkj.platform.controller.zncs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.cloud.redis.lock.RedissonDistributedLock;
import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.TimeUtils;
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
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.pjgl.InvoiceParamVO;
import com.dzf.zxkj.platform.model.pjgl.VatGoosInventoryRelationVO;
import com.dzf.zxkj.platform.model.pjgl.VatInvoiceSetVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.model.zncs.*;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.bdset.IPersonalSetService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.glic.impl.CheckInventorySet;
import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IDcpzService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.zncs.*;
import com.dzf.zxkj.platform.util.Kmschema;
import com.dzf.zxkj.platform.util.PinyinUtil;
import com.dzf.zxkj.platform.util.ReportUtil;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.dzf.zxkj.platform.util.zncs.ICaiFangTongConstant;
import com.dzf.zxkj.platform.util.zncs.OcrUtil;
import com.dzf.zxkj.platform.util.zncs.VatExportUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/zncs/gl_vatincinvact2")
public class VATInComInvoice2Controller extends BaseController {

    @Autowired
    private IVATInComInvoice2Service gl_vatincinvact2;
    @Autowired
    private IBankStatement2Service gl_yhdzdserv2;
    @Autowired
    private ISchedulCategoryService schedulCategoryService;
    //	@Autowired
//	private ITaxitemsetService taxitemserv;
    @Autowired
    private IVatInvoiceService vatinvoiceserv;
    @Autowired
    private IZncsVoucher zncsVoucher;
    @Autowired
    private IAuxiliaryAccountService gl_fzhsserv;
    //	@Autowired
//	private IInventoryService inventoryserv;
//	@Autowired
//	private IAuxiliaryAccountService gl_fzhsserv;
    @Autowired
    private ICbComconstant gl_cbconstant;
    @Autowired
    private IPersonalSetService gl_gxhszserv;
    @Autowired
    private IParameterSetService parameterserv;
    @Autowired
    private CheckInventorySet inventory_setcheck;
    @Autowired
    private IDcpzService dcpzjmbserv;
    @Autowired
    private IInventoryAccSetService gl_ic_invtorysetserv ;
    @Autowired
    private IVATSaleInvoice2Service gl_vatsalinvserv2;
    @Autowired
    ICpaccountService gl_cpacckmserv;
    @Autowired
    private IInterfaceBill ocrinterface;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private ICorpService corpService;
    @Autowired
    private RedissonDistributedLock redissonDistributedLock;

    @RequestMapping("/queryInfo")
    public ReturnData<Json> queryInfo(@RequestBody InvoiceParamVO paramvo){
        Json json = new Json();
        try {
            //查询并分页
            if(StringUtil.isEmpty(SystemUtil.getLoginCorpId())){//corpVo.getPrimaryKey()
                throw new BusinessException("出现数据无权问题！");
            }
            paramvo.setPk_corp(SystemUtil.getLoginCorpId());

            List<VATInComInvoiceVO2> list = gl_vatincinvact2.quyerByPkcorp(paramvo, paramvo.getSort(), paramvo.getOrder());
            //list变成数组
            json.setTotal((long) (list==null ? 0 : list.size()));
            //分页
            VATInComInvoiceVO2[] vos = null;
            if(list!=null && list.size()>0){
                vos = getPagedZZVOs(list.toArray(new VATInComInvoiceVO2[0]),paramvo.getPage(),paramvo.getRows());
                for (VATInComInvoiceVO2 vo : vos)
                {
                    //处理改版前的图片路径，将/gl/gl_imgview!search.action替换成/zncs/gl_imgview/search
                    if(!StringUtil.isEmpty(vo.getImgpath())&&vo.getImgpath().contains("/gl/gl_imgview!search.action")){
                        vo.setImgpath(vo.getImgpath().replace("/gl/gl_imgview!search.action","/zncs/gl_imgview/search"));
                    }
                    if (StringUtil.isEmpty(vo.getPk_tzpz_h()) && !StringUtil.isEmpty(vo.getImgpath()) && (!StringUtil.isEmpty(vo.getPk_model_h()) || !StringUtil.isEmpty(vo.getBusitypetempname())))
                    {
                        vo.setPk_model_h(null);
                        vo.setBusisztypecode(null);
                        vo.setBusitypetempname(null);
                    }
                }
            }
            log.info("查询成功！");
            json.setRows(vos==null?new ArrayList<VATInComInvoiceVO2>():Arrays.asList(vos));
            json.setHead(getHeadVO(list));
            json.setSuccess(true);
            json.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
        }

        return ReturnData.ok().data(json);
    }

    @RequestMapping("/queryInfoByID")
    public ReturnData<Json> queryInfoByID(String id){
        Json json = new Json();

        try {
            VATInComInvoiceVO2 hvo = gl_vatincinvact2.queryByID(
                   id);

            json.setData(hvo);
            json.setSuccess(true);
            json.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
        }

        return ReturnData.ok().data(json);
    }


    private VATInComInvoiceVO2[] getPagedZZVOs(VATInComInvoiceVO2[] vos, int page, int rows) {
        int beginIndex = rows * (page-1);
        int endIndex = rows * page;
        if(endIndex >= vos.length){//防止endIndex数组越界
            endIndex = vos.length;
        }
        vos = Arrays.copyOfRange(vos, beginIndex, endIndex);
        return vos;
    }

    //修改保存
    @RequestMapping("/saveOrUpdate")
    public ReturnData<Json> saveOrUpdate(@RequestBody Map<String,String> param){
        String msg = "";//记录日志
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{
            String head = param.get("header");
            String body = param.get("body");
            Map<String, VATInComInvoiceVO2[]> sendData = new HashMap<String, VATInComInvoiceVO2[]>();
            VATInComInvoiceVO2 headvo = JsonUtils.deserialize(head,VATInComInvoiceVO2.class);
            VATInComInvoiceBVO2[] bodyvos = JsonUtils.deserialize(body, VATInComInvoiceBVO2[].class);
            if (!StringUtil.isEmptyWithTrim(headvo.getGhfmc())){
                //处理特殊字符
                headvo.setGhfmc(OcrUtil.filterCorpName(headvo.getGhfmc()).trim());
            }
            if (!StringUtil.isEmptyWithTrim(headvo.getXhfmc())){
                //处理特殊字符
                headvo.setXhfmc(OcrUtil.filterCorpName(headvo.getXhfmc()).trim());
            }

            bodyvos = filterBlankBodyVos(bodyvos);
            setDefultValue(headvo, bodyvos);
            checkJEValid(headvo, bodyvos);
            headvo.setChildren(bodyvos);
            saveOrUpdateCorpReference(headvo);//新增本方公司参照
            msg += SystemUtil.getLoginUserVo().getUser_name();
            if(!StringUtils.isEmpty(headvo.getPk_vatincominvoice())){
                //gl_vatincinvact2.checkvoPzMsg(headvo.getPk_vatincominvoice());
                if(!StringUtils.isEmpty(headvo.getImgpath())){
                    throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
                }
            }
            //
            if(StringUtil.isEmpty(headvo.getPrimaryKey())){
                sendData.put("adddocvos", new VATInComInvoiceVO2[]{headvo});

                msg += "新增发票号码(" + headvo.getFp_hm() + ")的进项发票";
            }else{
                sendData.put("upddocvos", new VATInComInvoiceVO2[]{headvo});

                msg += "修改发票号码(" + headvo.getFp_hm() + ")的进项发票";
            }
            //增加结算方式
            gl_vatincinvact2.updateCategoryset(new DZFBoolean(false),headvo.getPk_model_h(),headvo.getBusisztypecode(),headvo.getPk_basecategory(),pk_corp,null,null,null,null);

            VATInComInvoiceVO2[] addvos = gl_vatincinvact2.updateVOArr(pk_corp, sendData);

            json.setStatus(200);
            json.setRows(addvos);
            json.setSuccess(true);
            json.setMsg("保存成功！");

            msg += "成功";
        }catch(Exception e){
            printErrorLog(json, e, "保存失败");

            if(!StringUtil.isEmpty(msg)){
                msg += "失败";
            }
        }

        if(StringUtil.isEmpty(msg)){
            msg = "进项发票编辑";
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, msg, ISysConstants.SYS_2);
        return  ReturnData.ok().data(json);
    }

    private VATInComInvoiceBVO2[] filterBlankBodyVos(VATInComInvoiceBVO2[] bodyvos){
        List<VATInComInvoiceBVO2> filterList = new ArrayList<VATInComInvoiceBVO2>();
        if(bodyvos != null && bodyvos.length > 0){
            VATInComInvoiceBVO2 bvo = null;
            for(int i = 0; i < bodyvos.length; i++){
                bvo = bodyvos[i];
                //处理前端传过来的空数据
                if(StringUtils.isEmpty(bvo.getBspmc())&&StringUtils.isEmpty(bvo.getPk_billcategory())){
                    continue;
                }
                if (!StringUtil.isEmptyWithTrim(bvo.getBspmc())){

                    bvo.setBspmc(OcrUtil.filterCorpName(bvo.getBspmc()).trim());
                }
                if(StringUtil.isEmpty(bvo.getBspmc())//商品名称
                        && StringUtil.isEmpty(bvo.getInvspec())
                        && StringUtil.isEmpty(bvo.getMeasurename())//规格
                        && bvo.getBnum() == null	//数量
                        && bvo.getBprice() == null	//单价
                        && bvo.getBhjje() == null	//金额
                        && bvo.getBspse() == null	//税额
                        && bvo.getBspsl() == null){	//税率
                    //什么都不做
                }else{
                    if(bvo.getBhjje() == null
                            || bvo.getBspse() == null){
                        throw new BusinessException("第" + (i+1) + "行，金额、税额不能为空,请检查");
                    }

                    filterList.add(bvo);
                }
            }
        }

        return filterList.toArray(new VATInComInvoiceBVO2[0]);
    }

    private void checkJEValid(VATInComInvoiceVO2 vo, VATInComInvoiceBVO2[] body){

        if(vo.getKprj() == null)
            throw new BusinessException("开票日期不允许为空或日期格式不正确,请检查");

        if(vo.getRzjg() != null && vo.getRzjg() == 1){
            if(vo.getRzrj() == null){
                throw new BusinessException("勾选已认证，认证日期为必输项，请检查");
            }

            if(vo.getRzrj().before(SystemUtil.getLoginCorpVo().getBegindate())){
                throw new BusinessException("认证日期不允许在建账日期前，请检查");
            }

            if(vo.getRzrj().before(vo.getKprj())){
                throw new BusinessException("认证日期不允许在开票日期前，请检查");
            }
        }

        if(body == null || body.length == 0){
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


    }

    private void setDefultValue(VATInComInvoiceVO2 vo, VATInComInvoiceBVO2[] body){

        //此period 与后续生成的凭证期间不一样
        String period = null;
        vo.setRzjg(0);
        if(vo.getRzrj() != null){
            vo.setRzjg(1);
            //period = DateUtils.getPeriod(vo.getRzrj());
        }

        if(StringUtils.isEmpty(vo.getPeriod())){
            vo.setPeriod(DateUtils.getPeriod(new DZFDate(SystemUtil.getLoginDate())));
        }
        if(StringUtils.isEmpty(vo.getInperiod())){
            vo.setInperiod(DateUtils.getPeriod(new DZFDate(SystemUtil.getLoginDate())));
        }
//		if(vo.getRzjg() != null && vo.getRzjg() == 1){
//			period = DateUtils.getPeriod(vo.getRzrj());
//		}else if(vo.getKprj() != null){
//			period = DateUtils.getPeriod(vo.getKprj());
//		}



        DZFDouble bse = DZFDouble.ZERO_DBL;
        DZFDouble bje = DZFDouble.ZERO_DBL;
        DZFDouble bsl = DZFDouble.ZERO_DBL;

        StringBuffer spmchz = new StringBuffer();
        VATInComInvoiceBVO2 bvo = null;
        for(int i = 0; i < body.length; i++){
            bvo = body[i];
            bvo.setPk_corp(SystemUtil.getLoginCorpId());
            bse = SafeCompute.add(bse, bvo.getBspse());
            bje = SafeCompute.add(bje, bvo.getBhjje());

            bsl = SafeCompute.multiply(SafeCompute.div(bvo.getBspse(), bvo.getBhjje()), new DZFDouble(100));
            bsl = bsl.setScale(0, DZFDouble.ROUND_HALF_UP);
            bvo.setBspsl(bsl);
            bvo.setRowno(i+1);

            if(!StringUtil.isEmpty(bvo.getBspmc())
                    && spmchz.length() == 0){
                spmchz.append(bvo.getBspmc());
            }
            if (StringUtil.isEmpty(bvo.getPk_billcategory()))
            {
                bvo.setPk_billcategory(vo.getPk_model_h());
                bvo.setPk_category_keyword(vo.getPk_category_keyword());
            }
        }

        if(spmchz.length() > 0){
            vo.setSpmc(spmchz.toString());
        }

        vo.setHjje(bje);//金额
        vo.setSpse(bse);//税额
        vo.setJshj(SafeCompute.add(bje, bse));
        DZFDouble sl = SafeCompute.multiply(SafeCompute.div(vo.getSpse(), vo.getHjje()), new DZFDouble(100));
        vo.setSpsl(sl.setScale(0, DZFDouble.ROUND_HALF_UP));//税率

        if(!StringUtil.isEmpty(vo.getPrimaryKey())){
            vo.setModifydatetime(new DZFDateTime());
            vo.setModifyoperid(SystemUtil.getLoginUserId());
        }else{
            vo.setDoperatedate(new DZFDate());
            vo.setCoperatorid(SystemUtil.getLoginUserId());
            vo.setPk_corp(SystemUtil.getLoginCorpId());
            vo.setSourcetype(IBillManageConstants.MANUAL);
        }

		/*if(StringUtil.isEmpty(vo.getPk_model_h())){
			setBusiNameFromFon(vo, getLogincorppk());
		}*/
    }

	/*private void setBusiNameFromFon(VATInComInvoiceVO2 vo, String pk_corp){
		String businame = vo.getBusitypetempname();
		String busicode = vo.getBusisztypecode();
		if(StringUtil.isEmpty(businame)
			|| StringUtil.isEmpty(busicode)){
			return;
		}

		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
		Map<String, DcModelHVO> dcmap = hashliseBusiTypeMap(dcList);

		String stylecode = vo.getIsZhuan() != null &&
				vo.getIsZhuan().booleanValue() ? FieldConstant.FPSTYLE_01 : FieldConstant.FPSTYLE_02;
		String key = vo.getBusitypetempname()
				+ "," + stylecode + "," + busicode;

		DcModelHVO hvo = dcmap.get(key);
		if(hvo != null){
			vo.setPk_model_h(hvo.getPrimaryKey());
		}else{
			String zhflag = stylecode.equals(FieldConstant.FPSTYLE_01) ? "专票" : "普票";
			String msg = "<p>进项发票[%s_%s]为%s与入账模板票据类型不一致，请检查</p>";
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
        VATInComInvoiceVO2[] bodyvos = null;
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        String msg = "";
        List<String> sucHM = new ArrayList<String>();
        List<String> errHM = new ArrayList<String>();
        boolean lock= false;
        try{
            String body = param.get("head");

            //加锁
             lock=redissonDistributedLock.tryGetDistributedFairLock("jinxiangDelete_"+pk_corp);
            if(!lock){//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍候刷新界面");
               return ReturnData.error().data(json);
            }
            if (body == null) {
                throw new BusinessException("数据为空,删除失败!!");
            }
            bodyvos = JsonUtils.deserialize(body, VATInComInvoiceVO2[].class);
            if (bodyvos == null || bodyvos.length == 0) {
                throw new BusinessException("数据为空,删除失败!!");
            }

            for(VATInComInvoiceVO2 vo : bodyvos){
                if(!StringUtils.isEmpty(vo.getPk_vatincominvoice())){
//					gl_vatincinvact2.checkvoPzMsg(vo.getPk_vatincominvoice());
                    if(!StringUtils.isEmpty(vo.getImgpath())){
                        throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
                    }
                }
                try {

                    gl_vatincinvact2.delete(vo, pk_corp);
                    json.setSuccess(true);
                    strb.append("<font color='#2ab30f'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "],删除成功!</p></font>");

                    sucHM.add(vo.getFp_hm());
                } catch (Exception e) {
                    printErrorLog(json, e, "删除失败!");
                    strb.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]," + json.getMsg() + "</p></font>");

                    errHM.add(vo.getFp_hm());
                }
            }

            String inmsg = "";
            if(sucHM.size() > 0){
                inmsg += SystemUtil.getLoginUserVo().getUser_name()
                        + "删除进项发票,发票号码("+sucHM.get(0)+")等"+sucHM.size()+"条记录成功";
            }
            if(errHM.size() > 0){
                if(StringUtil.isEmpty(inmsg)){
                    inmsg += SystemUtil.getLoginUserVo().getUser_name()
                            + "删除进项发票,发票号码("+errHM.get(0)+")等"+errHM.size()+"条记录失败";
                }else{
                    inmsg += ",发票号码("+errHM.get(0)+")等"+errHM.size()+"条记录失败";
                }
            }

            msg = inmsg;
        }catch(Exception e){
            printErrorLog(json, e, "删除失败");
            strb.append("删除失败");
        }finally {
            if(lock){
                redissonDistributedLock.releaseDistributedFairLock("jinxiangDelete_"+pk_corp);
            }
        }
        json.setMsg(strb.toString());

        if(StringUtil.isEmpty(msg)){
            msg = "进项发票编辑";
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, msg, ISysConstants.SYS_2);
        return  ReturnData.ok().data(json);
    }

    @RequestMapping("/impExcel")
    public ReturnData<Json> impExcel(@RequestBody MultipartFile file){
        String userid = SystemUtil.getLoginUserId();
        Json json = new Json();
        json.setSuccess(false);
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
//            File[] infiles = ((MultiPartRequestWrapper) getRequest()).getFiles("impfile");
//            if(infiles == null || infiles.length==0){
//                throw new BusinessException("请选择导入文件!");
//            }
//            String[] fileNames = ((MultiPartRequestWrapper) getRequest()).getFileNames("impfile");
            String fileName = file.getOriginalFilename();
            if(file == null || file.getSize()==0||StringUtils.isEmpty(fileName)){
                throw new BusinessException("请选择导入文件!");
            }
            String fileType = null;
            if (fileName != null && fileName.length() > 0) {
                fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            }

            VATInComInvoiceVO2 paramvo = new VATInComInvoiceVO2();
            paramvo.setInperiod(DateUtils.getPeriod(new DZFDate(SystemUtil.getLoginDate())));
            StringBuffer msg = new StringBuffer();
            gl_vatincinvact2.saveImp(file, paramvo, pk_corp, fileType, userid, msg);

            json.setHead(paramvo);
            json.setMsg(msg.toString());
            json.setSuccess(paramvo.getCount()==0 ? false : true);

            writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                    "导入进项发票" + (paramvo.getPeriod() != null ? "："+paramvo.getPeriod() : ""), ISysConstants.SYS_2);
        } catch (Exception e) {
            printErrorLog(json, e, "导入失败!");
        }

        return ReturnData.ok().data(json);
    }

    /**
     * 生成凭证
     */
    public ReturnData<Json> createPZ(String lwstr,String body,String goods){
        Json json = new Json();
        VATInComInvoiceVO2[] vos = null;
        CorpVO corpvo = SystemUtil.getLoginCorpVo();
        String pk_corp =  corpvo.getPk_corp();
        checkSecurityData(null,new String[]{pk_corp}, null);
        boolean lock = false;
        try {

            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("jinxiangcreatepz"+pk_corp);
            if(!lock){//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍候");
                return ReturnData.error().data(json);
            }

            DZFBoolean lwflag = "Y".equals(lwstr) ? DZFBoolean.TRUE : DZFBoolean.FALSE;
            if (body == null) {
                throw new BusinessException("数据为空,生成凭证失败!");
            }

            vos = JsonUtils.deserialize(body, VATInComInvoiceVO2[].class);
            if(vos == null || vos.length == 0)
                throw new BusinessException("数据为空,生成凭证失败，请检查");


            String userid  = SystemUtil.getLoginUserId();

            List<VATInComInvoiceVO2> storeList = gl_vatincinvact2.construcComInvoice(vos, pk_corp);
            if(storeList == null || storeList.size() == 0){
                throw new BusinessException("查询进项发票失败，请检查");
            }
            Map<String, DcModelHVO> dcmodelmap = gl_yhdzdserv2.queryDcModelVO(pk_corp);
            for (VATInComInvoiceVO2 vo : storeList) {
                //gl_vatincinvact2.checkvoPzMsg(vo.getPk_vatincominvoice());
                if(!StringUtils.isEmpty(vo.getImgpath())){
                    throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
                }

                if(dcmodelmap.containsKey(vo.getPk_model_h())){
                    throw new BusinessException("发票号码"+vo.getFp_hm()+"请重新选择业务类型");
                }
            }

            VatGoosInventoryRelationVO[] goodvos = getGoodsData(goods);
            goodsToVatBVO(storeList, goodvos, pk_corp, userid);

            //检查是否包含存货类型
//			String checkMsg = gl_vatincinvact2.checkNoStock(storeList,pk_corp);
//			if(!StringUtils.isEmpty(checkMsg)){
//				throw new BusinessException(checkMsg);
//			}
            boolean accway = getAccWay(pk_corp);
            VatInvoiceSetVO setvo = queryRuleByType();

            int errorCount = 0;
            StringBuffer msg = new StringBuffer();

            String key;
            String period;
            List<String> periodSet = new ArrayList<String>();
            String kplx = null;
            for(VATInComInvoiceVO2 vo : storeList){
                DZFDate rzrj = TimeUtils.getLastMonthDay(new DZFDate(vo.getInperiod()+"-01"));
                if(vo.getKprj().after(rzrj)){
                    throw new BusinessException("开票日期不能晚于入账日期！");
                }
                key = buildkey(vo, setvo);
                period = splitKey(key);
                if(!periodSet.contains(period)){
                    periodSet.add(period);//组装查询期间
                }
                try {
                    kplx = vo.getKplx();
                    if(!StringUtil.isEmpty(kplx)
                            && (ICaiFangTongConstant.FPLX_3.equals(kplx)//空白废票
                            || ICaiFangTongConstant.FPLX_4.equals(kplx)//正废
                            || ICaiFangTongConstant.FPLX_5.equals(kplx))){//负废
                        errorCount++;
                        periodSet.remove(period);
                        msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成凭证。</p></font>");
//					}else if(vo.getIsic() != null && vo.getIsic().booleanValue()){
                    }else if(!StringUtil.isEmpty(vo.getPk_ictrade_h())){
                        errorCount++;
                        periodSet.remove(period);
                        msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据已关联入库，不能生成凭证。</p></font>");
                    }
                    else if(StringUtil.isEmpty(vo.getPk_tzpz_h())){
                        gl_vatincinvact2.createPZ(vo, pk_corp, userid, period, setvo, lwflag, accway, false);
                        msg.append("<font color='#2ab30f'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");
                    }else{
                        errorCount++;
                        periodSet.remove(period);
                        msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成凭证。</p></font>");
                    }
                } catch (Exception e) {

                    log.error(e.getMessage(), e);
                    if(e instanceof BusinessException
                            && !StringUtil.isEmpty(e.getMessage())
                            && !e.getMessage().startsWith("进项发票")
                            && !e.getMessage().startsWith("制单失败")){
                        try {
                            gl_vatincinvact2.createPZ(vo, pk_corp, userid, period, setvo, lwflag, accway, true);
                            msg.append("<font color='#2ab30f'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");
                        } catch (Exception ex) {

                            errorCount++;
                            if(ex instanceof BusinessException){
                                msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败，原因:")
                                        .append(e.getMessage())
                                        .append("。</p></font>");
                            }else{
                                msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败")
                                        .append("。</p></font>");
                            }
                            periodSet.remove(period);
                        }
                    }else if(!StringUtil.isEmpty(e.getMessage())
                            && (e.getMessage().startsWith("进项发票")
                            || e.getMessage().startsWith("制单失败"))){
                        errorCount++;
                        msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败，原因:")
                                .append(e.getMessage())
                                .append("。</p></font>");
                        periodSet.remove(period);
                    }else{
                        errorCount++;
                        msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败 ")
                                .append("。</p></font>");
                        periodSet.remove(period);
                    }
                }
            }

            StringBuffer headMsg = gl_yhdzdserv2.buildQmjzMsg(periodSet, pk_corp);
            if(headMsg != null && headMsg.length() > 0){
                headMsg.append(msg.toString());
                msg = headMsg;
            }

            json.setSuccess(errorCount > 0 ? false : true);
            json.setMsg(msg.toString());
        } catch (Exception e) {
            printErrorLog(json, e, "生成凭证失败");
        } finally{
            if(lock){
                redissonDistributedLock.releaseDistributedFairLock("jinxiangcreatepz"+pk_corp);
            }

        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "进项发票生成凭证", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private boolean getAccWay(String pk_corp){
//		String acc = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF012);//入账设置
//		boolean accway = StringUtil.isEmpty(acc) || "0".equals(acc)
//				? true : false;//按入账日期为true, 按期间为false

        return true;
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
            Map<String, String> bodymapping = FieldMapping.getFieldMapping(new VATInComInvoiceVO2());
            VATInComInvoiceVO2[] listvo = DzfTypeUtils.cast(array, bodymapping, VATInComInvoiceVO2[].class, JSONConvtoJAVA.getParserConfig());

            if(listvo == null || listvo.length == 0)
                throw new BusinessException("解析前台参数失败，请检查");

            String msg = gl_vatincinvact2.saveBusiType(listvo, busiid, businame, selvalue, getLogin_userid(), getLogincorppk());

            //重新查询
            String[] pks = buildPks(listvo);
            List<VATInComInvoiceVO2> newList = gl_vatincinvact2.queryByPks(pks, getLogincorppk());

            json.setRows(newList);
            json.setMsg(msg);
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, log, e, "更新业务类型失败");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL.getValue(),
                "进项发票更新业务类型", ISysConstants.SYS_2);
        writeJson(json);
    }*/
    private String adjustPeriod(VATInComInvoiceVO2[] listvo, CorpVO corpVO, String ruzperiod)
    {
        List<BillCategoryVO> list2 = schedulCategoryService.queryBillCategoryByCorpAndPeriod(corpVO.getPk_corp(), ruzperiod);
        if (list2 == null || list2.size() == 0) {

            schedulCategoryService.newSaveCorpCategory(null, corpVO.getPk_corp(),ruzperiod, corpVO);

        }
        return gl_vatincinvact2.saveBusiPeriod(listvo,
                corpVO.getPk_corp(), new String[]{"入账期间", "inperiod", ruzperiod});
    }

    @RequestMapping("/setBusiperiod")
    public ReturnData<Json> setBusiperiod(@RequestBody Map<String,String> param){
        Json json = new Json();
        json.setSuccess(false);
        CorpVO corpVO = SystemUtil.getLoginCorpVo();
        checkSecurityData(null,new String[]{corpVO.getPk_corp()}, null);
        try {
            String data = param.get("rows");
            String ruzperiod = param.get("ruzper");
            String rezperiod = param.get("rezper");
            if(StringUtil.isEmptyWithTrim(data)){
                throw new BusinessException("传入后台参数为空，请检查");
            }

            VATInComInvoiceVO2[] listvo = JsonUtils.deserialize(data, VATInComInvoiceVO2[].class);
            if(listvo == null || listvo.length == 0)
                throw new BusinessException("解析前台参数失败，请检查");


            Map<String, DcModelHVO> dcmodelmap = gl_yhdzdserv2.queryDcModelVO(corpVO.getPk_corp());
            List<VATInComInvoiceVO2> listincomvo = new ArrayList<VATInComInvoiceVO2>();
            for (VATInComInvoiceVO2 vo : listvo) {
                VATInComInvoiceVO2 newvo = gl_vatincinvact2.checkvoPzMsg(vo.getPk_vatincominvoice());
                //List<DcModelHVO> list = gl_yhdzdserv2.queryIsDcModel(vo.getPk_model_h());
                if(dcmodelmap.containsKey(vo.getPk_model_h())){
                    throw new BusinessException("发票号码"+vo.getFp_hm()+"不允许期间调整，请重新设置业务类型");
                }
                listincomvo.add(newvo);
            }
            String msg = "";
            String msg1;
            if(!StringUtil.isEmpty(ruzperiod)){
                DZFDate ruzdate = DateUtils.getPeriodEndDate(ruzperiod);
                if(ruzdate == null){
                    throw new BusinessException("入账期间解析错误，请检查");
                }
                msg1 = adjustPeriod(listincomvo.toArray(new VATInComInvoiceVO2[0]), corpVO, ruzperiod);
                if(!StringUtil.isEmpty(msg1)){
                    msg += msg1;
                }
            }
            if(!StringUtil.isEmpty(rezperiod)){
                DZFDate rezdate = DateUtils.getPeriodEndDate(rezperiod);
                if(rezdate == null){
                    throw new BusinessException("认证期间解析错误，请检查");
                }

                msg1 = gl_vatincinvact2.saveBusiPeriod(listincomvo.toArray(new VATInComInvoiceVO2[0]),
                        corpVO.getPk_corp(), new String[]{"认证期间", "rzrj", rezperiod});
                if(!StringUtil.isEmpty(msg1)){
                    msg += msg1;
                }
            }

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

    private String[] buildPks(VATInComInvoiceVO2[] vos){
        String[] arr = new String[vos.length];

        for(int i = 0; i < vos.length; i++){
            arr[i] = vos[i].getPk_vatincominvoice();
        }

        return arr;
    }

    @RequestMapping("/checkBeforPZ")
    public ReturnData<Json> checkBeforPZ(@RequestParam("row") String str){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        try {
//            Map<String, String> bodymapping = FieldMapping.getFieldMapping(new VATInComInvoiceVO2());
//            VATInComInvoiceVO2[] listvo = DzfTypeUtils.cast(array, bodymapping, VATInComInvoiceVO2[].class, JSONConvtoJAVA.getParserConfig());
            VATInComInvoiceVO2[] listvo = JsonUtils.deserialize(str, VATInComInvoiceVO2[].class);
            if(listvo == null || listvo.length == 0)
                throw new BusinessException("解析前台参数失败，请检查");
            for (VATInComInvoiceVO2 vo : listvo) {
//				gl_vatincinvact2.checkvoPzMsg(vo.getPk_vatincominvoice());
                if(!StringUtils.isEmpty(vo.getImgpath())){
                    throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
                }
            }
            gl_vatincinvact2.checkBeforeCombine(listvo);
            TzpzHVO headVO = constructCheckTzpzHVo(pk_corp, listvo[0]);
            gl_yhdzdserv2.checkCreatePZ(pk_corp, headVO);

            json.setSuccess(true);
            json.setMsg("校验成功");
        } catch (Exception e) {
            printErrorLog(json, e, "校验失败");
        }

        return ReturnData.ok().data(json);
    }

    /**
     * 构造生成凭证前校验需要的HVO
     * @param pk_corp
     * @param vo
     * @return
     */
    private TzpzHVO constructCheckTzpzHVo(String pk_corp, VATInComInvoiceVO2 vo){
        TzpzHVO hvo = new TzpzHVO();
        hvo.setPk_corp(pk_corp);

        DZFDate voucherDate = (vo.getRzjg() != null && vo.getRzjg() == 1)
                ? vo.getRzrj() : new DZFDate();

        if(voucherDate == null)
            throw new BusinessException("认证结果与认证日期不匹配,请检查");

        hvo.setDoperatedate(voucherDate);
        hvo.setPeriod(DateUtils.getPeriod(voucherDate));

        return hvo;
    }

    @RequestMapping("/getTzpzHVOByID")
    public ReturnData<Json> getTzpzHVOByID(@RequestParam("row") String str){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
//			if(StringUtil.isEmptyWithTrim(data.getPrimaryKey()))
//				throw new BusinessException("获取前台参数失败，请检查");
//            String str = getRequest().getParameter("row");
            JSONArray array = (JSONArray) JSON.parseArray(str);
            if (array == null) {
                throw new BusinessException("数据为空,请检查!");
            }
//            Map<String, String> bodymapping = FieldMapping.getFieldMapping(new VATInComInvoiceVO2());
//            VATInComInvoiceVO2[] listvo = DzfTypeUtils.cast(array, bodymapping, VATInComInvoiceVO2[].class, JSONConvtoJAVA.getParserConfig());
            VATInComInvoiceVO2[] listvo = array.toArray(new VATInComInvoiceVO2[0]);
            if(listvo == null || listvo.length == 0)
                throw new BusinessException("转化后数据为空,请检查!");

            boolean accway = getAccWay(pk_corp);
            VatInvoiceSetVO setvo = queryRuleByType();

            TzpzHVO hvo = gl_vatincinvact2.getTzpzHVOByID(listvo,
                    pk_corp,SystemUtil.getLoginUserId(), setvo, accway);
            hvo.setPk_image_group(listvo[0].getPk_image_group());
            json.setData(hvo);
            json.setSuccess(true);
            json.setMsg("凭证分录构造成功");
        } catch (Exception e) {
            printErrorLog(json, e, "凭证分录构造失败");
        }

        return ReturnData.ok().data(json);
    }

    @RequestMapping("/combinePZ_long")
    public ReturnData<Json> combinePZ_long(@RequestBody Map<String,String> param){
        ReturnData<Json> returnData = null;
        VatInvoiceSetVO setvo = queryRuleByType();
        String lwstr = param.get("lwflag");
        String body = param.get("head");
        String goods = param.get("goods");
        if(setvo == null
                || setvo.getValue() == null
                || setvo.getValue() == IBillManageConstants.HEBING_GZ_01){//不合并
            returnData = createPZ(lwstr,body,goods);
        }else{
            returnData = combinePZ1(lwstr,body,goods);
        }
        return returnData;
    }

    public ReturnData<Json> combinePZ1(String lwstr,String body,String goods){
        Json json = new Json();
        VATInComInvoiceVO2[] vos = null;
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        boolean lock = false;
        try {

            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("jinxiangcombinepz"+pk_corp);
            if(!lock){//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍候");
                return ReturnData.error().data(json);
            }

            DZFBoolean lwflag = "Y".equals(lwstr) ? DZFBoolean.TRUE : DZFBoolean.FALSE;
            if (body == null) {
                throw new BusinessException("数据为空,合并生成凭证失败!");
            }

            vos = JsonUtils.deserialize(body, VATInComInvoiceVO2[].class);
            if(vos == null || vos.length == 0)
                throw new BusinessException("数据为空，合并生成凭证失败，请检查");

            String userid  = SystemUtil.getLoginUserId();

            VatInvoiceSetVO setvo = queryRuleByType();
            CorpVO corpvo = corpService.queryByPk(pk_corp);

            List<VATInComInvoiceVO2> storeList = gl_vatincinvact2.construcComInvoice(vos, pk_corp);

            if(storeList == null || storeList.size() == 0){
                throw new BusinessException("查询进项发票失败，请检查");
            }
            Map<String, DcModelHVO> dcmodelmap = gl_yhdzdserv2.queryDcModelVO(pk_corp);
            for (VATInComInvoiceVO2 vo : storeList) {
                //gl_vatincinvact2.checkvoPzMsg(vo.getPk_vatincominvoice());
                if(!StringUtils.isEmpty(vo.getImgpath())){
                    throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
                }
                //List<DcModelHVO> list = gl_yhdzdserv2.queryIsDcModel(vo.getPk_model_h());
                //if(list!=null&&list.size()>0){
                if(dcmodelmap.containsKey(vo.getPk_model_h())){
                    throw new BusinessException("发票号码"+vo.getFp_hm()+"请重新选择业务类型");
                }
                DZFDate rzrj =TimeUtils.getLastMonthDay(new DZFDate(vo.getInperiod()+"-01"));
                if(vo.getKprj().after(rzrj)){
                    throw new BusinessException("开票日期不能晚于入账日期！");
                }
            }
            VatGoosInventoryRelationVO[] goodvos = getGoodsData(goods);
            goodsToVatBVO(storeList, goodvos, pk_corp, userid);
            //检查是否包含存货类型
//			String checkMsg = gl_vatincinvact2.checkNoStock(storeList,pk_corp);
//			if(!StringUtils.isEmpty(checkMsg)){
//				throw new BusinessException(checkMsg);
//			}
            boolean accway = getAccWay(pk_corp);
            Map<String, List<VATInComInvoiceVO2>> combineMap = new LinkedHashMap<String, List<VATInComInvoiceVO2>>();
            StringBuffer msg = new StringBuffer();
            String key;
            int errorCount = 0;
            String period;
            List<String> periodSet = new ArrayList<String>();
            List<VATInComInvoiceVO2> combineList = null;
            String kplx = null;
            for(VATInComInvoiceVO2 vo : storeList){

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
                    errorCount++;
                    msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成凭证</p></font>");
                    periodSet.remove(period);
                }else if(!StringUtil.isEmpty(vo.getPk_tzpz_h())){
                    errorCount++;
                    msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成凭证。</p></font>");
                    periodSet.remove(period);
//				}else if(vo.getIsic() != null && vo.getIsic().booleanValue()){
                }else if(!StringUtil.isEmpty(vo.getPk_ictrade_h())){
                    errorCount++;
                    msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据已关联入库，不能生成凭证。</p></font>");
                    periodSet.remove(period);
                }else if(IcCostStyle.IC_ON.equals(corpvo.getBbuildic())&&gl_vatincinvact2.checkIsStock(vo)){
                    try {
                        gl_vatincinvact2.createPZ(vo, pk_corp, userid, period, setvo, lwflag, accway, false);
                        msg.append("<font color='#2ab30f'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");

                    } catch (Exception e) {
                        try {
                            gl_vatincinvact2.createPZ(vo, pk_corp, userid, period, setvo, lwflag, accway, true);
                            msg.append("<font color='#2ab30f'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");

                        } catch (Exception e2) {
                            if(e2 instanceof BusinessException){
                                msg.append("<font color='red'><p>进项发票入账期间为")
                                        .append(key)
                                        .append("的单据生成凭证失败，原因：")
                                        .append(e2.getMessage())
                                        .append("。</p></font>");
                            }else{
                                msg.append("<font color='red'><p>进项发票入账期间为")
                                        .append(key)
                                        .append("的单据生成凭证失败。</p></font>");
                            }
                        }
                    }

                }else if(combineMap.containsKey(key)){
                    combineList = combineMap.get(key);

                    combineList.add(vo);
                }else{
                    combineList = new ArrayList<VATInComInvoiceVO2>();
                    combineList.add(vo);
                    combineMap.put(key, combineList);
                }
            }


            key = null;
            for(Map.Entry<String, List<VATInComInvoiceVO2>> entry : combineMap.entrySet()){
                try {
                    key = entry.getKey();

                    key = splitKey(key);

                    combineList = entry.getValue();
                    gl_vatincinvact2.saveCombinePZ(combineList,
                            pk_corp, userid, key, setvo, lwflag, accway, false);
                    msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if(e instanceof BusinessException
                            && !StringUtil.isEmpty(e.getMessage())
                            && !e.getMessage().startsWith("进项发票")
                            && !e.getMessage().startsWith("制单失败")){
                        try {

                            gl_vatincinvact2.saveCombinePZ(combineList, pk_corp, userid, key, setvo, lwflag, accway, true);
                            msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
                        } catch (Exception ex) {
                            errorCount++;
                            if(ex instanceof BusinessException){
                                msg.append("<font color='red'><p>进项发票入账期间为")
                                        .append(key)
                                        .append("的单据生成凭证失败，原因：")
                                        .append(ex.getMessage())
                                        .append("。</p></font>");
                            }else{
                                msg.append("<font color='red'><p>进项发票入账期间为")
                                        .append(key)
                                        .append("的单据生成凭证失败。</p></font>");
                            }
                            periodSet.remove(key);
                        }
                    }else if(!StringUtil.isEmpty(e.getMessage())
                            && (e.getMessage().startsWith("进项发票")
                            || e.getMessage().startsWith("制单失败"))){
                        errorCount++;
                        msg.append("<font color='red'><p>进项发票入账期间为")
                                .append(key)
                                .append("的单据生成凭证失败，原因：")
                                .append(e.getMessage())
                                .append("。</p></font>");
                        periodSet.remove(key);
                    }else{
                        errorCount++;
                        msg.append("<font color='red'><p>进项发票入账期间为")
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
            redissonDistributedLock.releaseDistributedFairLock("jinxiangcombinepz"+pk_corp);
            }
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "进项发票合并凭证", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private String buildkey(VATInComInvoiceVO2 vo, VatInvoiceSetVO setvo){
        String key = null;

        if(!StringUtil.isEmpty(vo.getInperiod())){
            key = vo.getInperiod();
        }else{
            if(vo.getRzjg() != null
                    && vo.getRzjg() == 1
                    && vo.getRzrj() != null){
                key = DateUtils.getPeriod(vo.getRzrj());
            }else if(StringUtil.isEmpty(vo.getInperiod())){
                key = DateUtils.getPeriod(vo.getKprj());
            }else{
                key = vo.getInperiod();
            }
        }

        DZFBoolean iszh = vo.getIszhuan();//专普票标识
        iszh = iszh == null ? DZFBoolean.FALSE:iszh;
        key += "_" + iszh.toString();

        if(setvo != null
                && setvo.getValue() == IBillManageConstants.HEBING_GZ_02){//按往来单位合并
            String gys = vo.getXhfmc();
            if(!StringUtil.isEmpty(gys)){//销货方名称
                key += gys;
            }
        }

        return key;
    }

//	private String buildComKey(VATInComInvoiceVO2 vo){
//		String rj = (vo.getRzjg() != null && vo.getRzjg() == 1 && vo.getRzrj() != null) ?
//				vo.getRzrj().toString() : new DZFDate().toString();
//		String key = vo.getPk_model_h()
//				+ "_" + rj;
//
//		return key;
//	}
//
//	private String[] splitkeys(String allkey){
//		String[] keys = StringUtil.isEmpty(allkey) ? null : allkey.split("_");
//
//		return keys;
//	}
//
//	private String buildkey(VATInComInvoiceVO2 vo, Integer itype){
//		String key = null;
//
//		DZFDate rj = (vo.getRzjg() != null && vo.getRzjg() == 1 && vo.getRzrj() != null) ?
//				vo.getRzrj() : new DZFDate();
//
//		if(itype == IBillManageConstants.HEBING_GZ_01){//相同往来单位合并一张
//
//			String period = DateUtils.getPeriod(rj);
//
//			key = appendkey(new String[]{
//				vo.getPk_model_h(),
//				period,
//				vo.getXhfmc()
//			});
//		}else if(itype == IBillManageConstants.HEBING_GZ_02){
//
//			key = appendkey(new String[]{
//					vo.getPk_model_h(),
//					rj.toString()
//			});
//		}else if(itype == IBillManageConstants.HEBING_GZ_03){
//
//			key = appendkey(new String[]{
//					vo.getPk_model_h(),
//					rj.toString(),
//					vo.getXhfmc()
//			});
//		}else{
//
//			String period = DateUtils.getPeriod(rj);
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
                IBillManageConstants.HEBING_JXFP);

        if(setvos != null && setvos.length > 0){
            return setvos[0];
        }

        VatInvoiceSetVO vo = new VatInvoiceSetVO();
        vo.setValue(IBillManageConstants.HEBING_GZ_01);
        vo.setEntry_type(IBillManageConstants.HEBING_FL_02);
        return vo;
    }

    @RequestMapping("/expExcelData")
    public void expExcelData(@RequestBody Map<String,String> param, HttpServletResponse response){
        String strrows = param.get("daterows");
        JSONArray array = JSON.parseArray(strrows);
        OutputStream toClient = null;
        try {
            response.reset();
            String exName = new String("进项清单.xlsx");
            exName = new String(exName.getBytes("GB2312"), "ISO_8859_1");// 解决中文乱码问题
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(exName));
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            byte[] length = null;
            VatExportUtils exp = new VatExportUtils();
            Map<Integer, String> fieldColumn = getExpFieldMap();
            speTransValue(array);
            //List<String> busiList = gl_vatincinvact2.getBusiTypes(getLogincorppk());
            List<String> busiList = new ArrayList<String>();
            length = exp.exportExcelForXlsx(fieldColumn, array, toClient,
                    "jinxiangqingdan2.xlsx", 1, 1, 1, VatExportUtils.EXP_JX,
                    true, 2, busiList, 0, null);
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
                if (response!=null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (Exception e) {
                log.error("excel导出错误", e);
            }
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                !StringUtils.isEmpty(strrows)?"导出进项发票":"下载进项发票模板", ISysConstants.SYS_2);
    }

    private void speTransValue(JSONArray arr){
        int len = arr == null ? 0 : arr.size();
        Map<String, Object> map = null;
        Object obj = null;
        for(int i = 0; i < len; i++){
            map = (Map<String, Object>) arr.get(i);
            map.put("serialno", i+1);//设置序号
            obj = map.get("srzjg");
            if(obj != null
                    && ((obj instanceof String && IBillManageConstants.RSPASS == Integer.parseInt((String)obj))
                    || (obj instanceof Integer && IBillManageConstants.RSPASS == (Integer)obj))){//通过转换
                map.put("srzjg", "通过");
            }else{
                map.remove("srzjg");
            }
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
        map.put(4, "inqj");
        map.put(5, "sspmc");
        map.put(6, "invspec");
        map.put(7, "measurename");
        map.put(8, "bnum");
        map.put(9, "shjje");
        map.put(10, "se");
        map.put(11, "busitempname");
        map.put(12, "sxhfmc");
        map.put(13, "srzjg");
        map.put(14, "srzrj");

        return map;
    }

    /*
    begindate3 开票日期开始日期
    enddate3 开票日期结束日期
	period  开票日期
    serType 选择开票日期，认证日期
     rzPeriod   认证所属日期
     */
    @RequestMapping("/onTicket")
    public ReturnData<Json> onTicket(@RequestBody Map<String,String> param){
        Json json = new Json();

        VATInComInvoiceVO2 paramvo = new VATInComInvoiceVO2();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        boolean lock = false;
        try{
            String ccrecode = param.get("ccrecode");
            String f2 = param.get("f2");
            String begindate3 = param.get("begindate3");
            String enddate3 = param.get("enddate3");
            String serType = param.get("serType");
            String rzPeriod = param.get("rzPeriod");
            paramvo.setKprj(new DZFDate(SystemUtil.getLoginDate()));//参数：当前登录时间

            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock(paramvo.getTableName()+pk_corp);
            if(!lock){//处理
                json.setSuccess(true);
                json.setMsg("正在处理中，请稍候");
                redissonDistributedLock.releaseDistributedFairLock(paramvo.getTableName()+pk_corp);
                return ReturnData.ok().data(json);
            }
            if(StringUtils.isEmpty(begindate3)||StringUtils.isEmpty(enddate3)){
                throw new BusinessException("开票日期不能为空！");
            }
            if(enddate3.compareTo(begindate3)<0){
                throw new BusinessException("开始日期不能大于结束日期！");
            }
            if(begindate3.compareTo(DateUtils.getPeriod(SystemUtil.getLoginCorpVo().getBegindate()))<0){
                throw new BusinessException("开始日期不能早于建账日期！");
            }
            long begintime = DateUtils.parse(begindate3, "yyyy-MM-dd").getTime();
            long endtime = DateUtils.parse(enddate3, "yyyy-MM-dd").getTime();
            if((endtime-begintime)/(1000 * 60 * 60 * 24)>365){
                throw new BusinessException("开始日期至结束日期不可超过一年！");
            }
//			period="2019-04";
//			DZFDate date = DateUtils.getPeriodEndDate(period);
//			paramvo.setKprj(date);
            String crecode = null;
            if(!StringUtils.isEmpty(ccrecode)){
                crecode = ccrecode.trim();
            }
            paramvo.setInvoiceDateStart(begindate3);
            paramvo.setInvoiceDateEnd(enddate3);
            if(!StringUtils.isEmpty(rzPeriod)){
                rzPeriod=rzPeriod.replace("-", "");
            }
            Map<String, VATInComInvoiceVO2> repMap = gl_vatincinvact2.savePt(pk_corp, SystemUtil.getLoginUserId(), crecode, f2, paramvo,serType,rzPeriod);

            //根据业务类型生成凭证
            StringBuffer msg = new StringBuffer();
            msg.append("<p>一键取票成功<p>");

            int errorCount = 0;
            GxhszVO gxh = gl_gxhszserv.query(pk_corp);
            Integer yjqpway = gxh.getYjqp_gen_vch();//0自动 1 手工
            if(repMap != null && repMap.size() > 0 && yjqpway == 0){
                Map<String, VATInComInvoiceVO2> pzMap = new HashMap<String, VATInComInvoiceVO2>();
                Map<String, VATInComInvoiceVO2> icMap = new HashMap<String, VATInComInvoiceVO2>();
                dealFirst(repMap, pzMap, icMap);
                CorpVO corpvo = corpService.queryByPk(pk_corp);
                String bbuildic = corpvo.getBbuildic();
                Integer icstyle = corpvo.getIbuildicstyle();

                if(!StringUtil.isEmpty(bbuildic)
                        && IcCostStyle.IC_ON.equals(bbuildic)
                        && icstyle != null && icstyle == 1){//首先判断是不是启用库存，再判断是不是库存新模式
                    errorCount = dealAfterTicketByIC(icMap, msg);
                    errorCount+= dealAfterTicketByPZ(pzMap, msg);
                }else{
                    errorCount = dealAfterTicketByPZ(repMap, msg);
                }

            }

            json.setHead(paramvo);
            json.setSuccess(errorCount == 0 ? true : false);
            json.setMsg(msg.toString());
        }catch(Exception e){
            printErrorLog(json, e, "一键取票失败");
            json.setSuccess(false);
            json.setMsg(e.getMessage());
        }finally{
            //解锁
            if(lock){
            redissonDistributedLock.releaseDistributedFairLock(paramvo.getTableName()+pk_corp);
            }
        }

        return ReturnData.ok().data(json);
    }

    private void dealFirst(Map<String, VATInComInvoiceVO2> repMap,
                           Map<String, VATInComInvoiceVO2> pzMap,
                           Map<String, VATInComInvoiceVO2> icMap){
        String busiName;
        String key;
        VATInComInvoiceVO2 vo;
        for(Map.Entry<String, VATInComInvoiceVO2> entry : repMap.entrySet()){
            key = entry.getKey();
            vo = entry.getValue();
            busiName = vo.getBusitypetempname();
            if(!StringUtil.isEmpty(busiName)
                    && busiName.startsWith("库存采购-")){
                icMap.put(key, vo);
            }else{
                pzMap.put(key, vo);
            }
        }
    }

    private int dealAfterTicketByPZ(Map<String, VATInComInvoiceVO2> map, StringBuffer msg){
        List<VATInComInvoiceVO2> list = buildMap2List(map);
        int errorCount = 0;
        String pk_corp = SystemUtil.getLoginCorpId();
        String userid = SystemUtil.getLoginUserId();
        //Map<String, DcModelHVO> dcmap = gl_yhdzdserv2.queryDcModelVO(pk_corp);
        if(list == null || list.size() == 0){
            return errorCount;
        }

        Map<String, List<VATInComInvoiceVO2>> combineMap = new LinkedHashMap<String, List<VATInComInvoiceVO2>>();
        String kplx = null;
        String key = null;
        List<VATInComInvoiceVO2> combineList = null;
        //DcModelHVO modelHVO= null;
        VatInvoiceSetVO setvo = queryRuleByType();
        boolean accway = getAccWay(pk_corp);

        for(VATInComInvoiceVO2 vo : list){

            //modelHVO = dcmap.get(vo.getPk_model_h());
            if(StringUtils.isEmpty(vo.getPk_model_h())){
                errorCount++;
                msg.append("<font color='red'><p>进项发票：[" + vo.getFp_hm() + "," + vo.getFp_hm() + "]业务类型未找到，请选择相应业务类型</p></font>");
                continue;
            }

            key = buildkey(vo, setvo);

            kplx = vo.getKplx();
            if(!StringUtil.isEmpty(kplx)
                    && (ICaiFangTongConstant.FPLX_3.equals(kplx)//空白废票
                    || ICaiFangTongConstant.FPLX_4.equals(kplx)//正废
                    || ICaiFangTongConstant.FPLX_5.equals(kplx))){//负废
                errorCount++;
                msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成凭证</p></font>");
            }else if(!StringUtil.isEmpty(vo.getPk_tzpz_h())){
                errorCount++;
                msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成凭证。</p></font>");
            }else if(StringUtil.isEmpty(vo.getPk_model_h())){
                errorCount++;
                msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据没有业务类型，不能生成凭证。</p></font>");
            }else if(combineMap.containsKey(key)){
                combineList = combineMap.get(key);

                combineList.add(vo);
            }else{
                combineList = new ArrayList<VATInComInvoiceVO2>();
                combineList.add(vo);
                combineMap.put(key, combineList);
            }
        }

        key = null;
        for(Map.Entry<String, List<VATInComInvoiceVO2>> entry : combineMap.entrySet()){
            try {
                key = entry.getKey();

                key = splitKey(key);

                combineList = entry.getValue();

                gl_vatincinvact2.saveCombinePZ(combineList,
                        pk_corp, userid, key, setvo, null, accway, false);
                msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if(e instanceof BusinessException
                        && !StringUtil.isEmpty(e.getMessage())
                        && !e.getMessage().startsWith("进项发票")
                        && !e.getMessage().startsWith("制单失败")){
                    try {
                        gl_vatincinvact2.saveCombinePZ(combineList, pk_corp, userid, key, setvo, null, accway, true);
                        msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
                    } catch (Exception ex) {
                        errorCount++;
                        if(ex instanceof BusinessException){
                            msg.append("<font color='red'><p>进项发票入账期间为")
                                    .append(key)
                                    .append("的单据生成凭证失败，原因：")
                                    .append(ex.getMessage())
                                    .append("。</p></font>");
                        }else{
                            msg.append("<font color='red'><p>进项发票入账期间为")
                                    .append(key)
                                    .append("的单据生成凭证失败。</p></font>");
                        }
                    }
                }else if(!StringUtil.isEmpty(e.getMessage())
                        && (e.getMessage().startsWith("进项发票")
                        || e.getMessage().startsWith("制单失败"))){
                    errorCount++;
                    msg.append("<font color='red'><p>进项发票入账期间为")
                            .append(key)
                            .append("的单据生成凭证失败，原因：")
                            .append(e.getMessage())
                            .append("。</p></font>");
                }else{
                    errorCount++;
                    msg.append("<font color='red'><p>进项发票入账期间为")
                            .append(key)
                            .append("的单据生成凭证失败。</p></font>");
                }
            }
        }

        return errorCount;
    }

    private String splitKey(String key){

        return key.substring(0, 7);
    }

    private int dealAfterTicketByIC(Map<String, VATInComInvoiceVO2> map, StringBuffer msg){
        List<VATInComInvoiceVO2> list = buildMap2List(map);
        int errorCount = 0;
        String pk_corp = SystemUtil.getLoginCorpId();
        String userid = SystemUtil.getLoginUserId();
        //Map<String, DcModelHVO> dcmap = gl_yhdzdserv2.queryDcModelVO(pk_corp);
        if(list == null || list.size() == 0){
            return errorCount;
        }
        // 处理存货
        String mss = processGoods(list, pk_corp, userid);
        if(!StringUtil.isEmpty(mss)){
            msg.append("<font color='red'><p>存货匹配失败,原因:"+mss+"</p></font>");
        }

        YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);
        Map<String, List<VATInComInvoiceVO2>> combineMap = new LinkedHashMap<String, List<VATInComInvoiceVO2>>();
        VatInvoiceSetVO setvo = queryRuleByType();
        String kplx = null;
        String key = null;
        List<VATInComInvoiceVO2> combineList = null;
        //DcModelHVO modelHVO= null;
        //DcModelBVO[] modelbvos = null;
        //DcModelBVO modelbvo = null;
        List<VATInComInvoiceVO2> errorList = new ArrayList<VATInComInvoiceVO2>();

        for(VATInComInvoiceVO2 vo : list){
            key = vo.getPk_model_h();
            //modelHVO = dcmap.get(key);
            if(StringUtils.isEmpty(key)){
                errorCount++;
                msg.append("<font color='red'><p>进项发票：[" + vo.getFp_hm() + "," + vo.getFp_hm() + "]业务类型未找到，请选择相应业务类型</p></font>");
                continue;
            }

			/*modelbvos = modelHVO.getChildren();
			int len = modelbvos == null ? 0 : modelbvos.length;
			boolean flag = false;
			String kmbm = null;
			for(int i = 0; i < len; i++){
				modelbvo = modelbvos[i];
				kmbm = modelbvo.getKmbm();
				if(!StringUtil.isEmpty(kmbm) && ( kmbm.startsWith(gl_cbconstant.getKcsp_code())
						|| kmbm.startsWith(gl_cbconstant.getYcl_code()) )){
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
                msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成入库单</p></font>");
            }else if(!StringUtil.isEmpty(vo.getPk_tzpz_h())){
                errorCount++;
                msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成入库单。</p></font>");
            }else if(StringUtil.isEmpty(vo.getPk_model_h())){
                errorCount++;
                msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据没有业务类型，不能生成入库单。</p></font>");
            }else if(combineMap.containsKey(key)){
                combineList = combineMap.get(key);

                combineList.add(vo);

            }else{
                combineList = new ArrayList<VATInComInvoiceVO2>();
                combineList.add(vo);
                combineMap.put(key, combineList);
            }
        }


        key = null;
        IntradeHVO ihvo = null;
        List<IntradeHVO> ihvoList = null;

        for(Map.Entry<String, List<VATInComInvoiceVO2>> entry : combineMap.entrySet()){
            try {
                combineList = entry.getValue();

                ihvoList = new ArrayList<IntradeHVO>();
                for(VATInComInvoiceVO2 vo : combineList){
                    try {
                        ihvo = gl_vatincinvact2.createIC(vo, accounts, SystemUtil.getLoginCorpVo(), userid);
                        ihvoList.add(ihvo);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        if(e instanceof BusinessException
                                && !StringUtil.isEmpty(e.getMessage())){
                            errorCount++;
                            msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成入库单失败，原因:")
                                    .append(e.getMessage())
                                    .append("。</p></font>");
                        }else{
                            errorCount++;
                            msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成入库单失败。</p></font>");
                        }
                    }
                }

//				//汇总转总账
//				if(ihvoList.size() > 0){
//					gl_vatincinvact2.saveTotalGL(
//							ihvoList.toArray(new IntradeHVO[0]), pk_corp, userid);
//				}
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if(e instanceof BusinessException
                        && !StringUtil.isEmpty(e.getMessage())){
                    errorCount++;
                    msg.append("<font color='red'><p>进项发票的单据生成入库单失败，原因:")
                            .append(e.getMessage())
                            .append("。</p></font>");
                }else{
                    errorCount++;
                    msg.append("<font color='red'><p>进项发票的单据生成入库单失败。</p></font>");
                }
            }
        }
        //提示信息
        int len = errorList.size();
        if(len > 0){
            StringBuffer msg1 = new StringBuffer();
            msg1.append("<font color='red'><p>进项发票[");
            VATInComInvoiceVO2 ervo = null;
            for(int i=0; i < len; i++){
                ervo = errorList.get(i);
                msg1.append(ervo.getFp_hm());

                if(i != len -1){
                    msg1.append(", ");
                }
            }

            msg1.append("]入库检查失败:入账设置的科目未找到原材料或库存商品</p></font>");
            msg.append(msg1.toString());
        }

        return errorCount;
    }
    //处理存货
    public String processGoods(List<VATInComInvoiceVO2> list,String pk_corp,String userid ){

        CorpVO corpVo = corpService.queryByPk(pk_corp);
        //处理存货匹配yinyx1
        try {
            if(IcCostStyle.IC_INVTENTORY.equals(corpVo.getBbuildic())){
                InventorySetVO invsetvo = query();
                if (invsetvo == null)
                    return "存货设置未设置!";

                List<InventoryAliasVO> relvos = gl_vatincinvact2.matchInventoryData(pk_corp, list.toArray(new VATInComInvoiceVO2[0]),invsetvo);
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
                List<VatGoosInventoryRelationVO> relvos = gl_vatincinvact2.getGoodsInvenRela(list, pk_corp);
                if(relvos!=null &&relvos.size()>0){
                    goodsToVatBVO(list, relvos.toArray(new VatGoosInventoryRelationVO[0]), pk_corp, userid);
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }

        return null;
    }

    private List<VATInComInvoiceVO2> buildMap2List(Map<String, VATInComInvoiceVO2> map){
        List<VATInComInvoiceVO2> list = new ArrayList<VATInComInvoiceVO2>();

        VATInComInvoiceVO2 vo = null;
        for(Map.Entry<String, VATInComInvoiceVO2> entry : map.entrySet()){
            vo = entry.getValue();

            list.add(vo);
        }

        return list;
    }

    @RequestMapping("/queryTaxItems")
    public ReturnData<Grid> queryTaxItems(){
        Grid grid = new Grid();
        try{

            List<TaxitemVO> vos = gl_vatincinvact2.queryTaxItems(SystemUtil.getLoginCorpId());//taxitemserv.queryAllTaxitems();//查询税目档案

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
            VatInvoiceSetVO[] vos = vatinvoiceserv.queryByType(SystemUtil.getLoginCorpId(), IBillManageConstants.HEBING_JXFP);

            VatInvoiceSetVO vo = null;
            if(vos != null && vos.length > 0){
                vo = vos[0];
            }

            json.setRows(vo);
            json.setMsg("查询合并规则成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json,e, "查询合并规则失败");
        }

//		writeLogRecord(LogRecordEnum.OPE_KJ_PJGL.getValue(),
//				"银行对账单更新业务类型", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @RequestMapping("/combineRule")
    public ReturnData<Json> combineRule(@RequestBody Map<String,String> param){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp},null);
        try {
            String pzrq = param.get("pzrq");
            String pzrule = param.get("pzrule");
            String flrule = param.get("flrule");
            String zy = param.get("zy");
            String setId = param.get("setid");
            String bk = param.get("bk");
            if(StringUtil.isEmpty(pzrule)
                    || StringUtil.isEmpty(flrule)|| StringUtil.isEmpty(pzrq)){
                throw new BusinessException("合并规则设置失败，请重试");
            }
            VatInvoiceSetVO vo = new VatInvoiceSetVO();
            String[] fields = new String[]{ "pzrq","value", "entry_type", "isbank", "zy" };
            if(!StringUtil.isEmpty(setId)){
                vo.setPrimaryKey(setId);
            }else{
                vo.setPk_corp(pk_corp);
                vo.setStyle(IBillManageConstants.HEBING_JXFP);
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
     * 生成入库
     */
    @RequestMapping("/createIC")
    public ReturnData<Json> createIC(@RequestParam("head")String body,String goods ){
        Json json = new Json();
        VATInComInvoiceVO2[] vos = null;
        String pk_corp = SystemUtil.getLoginCorpId();
        String userid  = SystemUtil.getLoginUserId();
        checkSecurityData(null,new String[]{pk_corp},null);
        boolean lock = false;
        try {

            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("jinxiang2ic"+pk_corp);
            if(!lock){//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍候");
                return ReturnData.ok().data(json);
            }

            CorpVO corpvo = corpService.queryByPk(pk_corp);
            checkBeforeIC(corpvo);

            JSONArray array = (JSONArray) JSON.parseArray(body);

            if (array == null) {
                throw new BusinessException("数据为空,生成凭证失败!");
            }

//            Map<String, String> bodymapping = FieldMapping.getFieldMapping(new VATInComInvoiceVO2());
//            vos = DzfTypeUtils.cast(array, bodymapping, VATInComInvoiceVO2[].class,
//                    JSONConvtoJAVA.getParserConfig());
            vos = array.toArray(new VATInComInvoiceVO2[0]);
            if(vos == null || vos.length == 0)
                throw new BusinessException("数据为空,生成入库单失败，请检查");

            List<VATInComInvoiceVO2> storeList = gl_vatincinvact2.construcComInvoice(vos, pk_corp);

            if(storeList == null || storeList.size() == 0){
                throw new BusinessException("查询进项发票失败，请检查");
            }
            VatGoosInventoryRelationVO[] goodsVos = getGoodsData(goods);
            goodsToVatBVO(storeList, goodsVos, pk_corp, userid);
//			List<InventoryVO> invList = inventoryserv.query(pk_corp);//只针对库存新模式
//			AuxiliaryAccountBVO[] supplList = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_SUPPLIER, pk_corp, null);
//			Map<String, InventoryVO> invMap = new HashMap<String, InventoryVO>();//存货
//			Map<String, AuxiliaryAccountBVO> supplMap = new HashMap<String, AuxiliaryAccountBVO>();
//			buildInventoryMap(invList, invMap, supplList, supplMap);
            //if(1==1) throw new BusinessException("调试");
            YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);

            int errorCount = 0;
            StringBuffer msg = new StringBuffer();
            IntradeHVO ichvo = null;
            String kplx = null;
            for(VATInComInvoiceVO2 vo : storeList){
                try {

                    kplx = vo.getKplx();
                    if(!StringUtil.isEmpty(kplx)
                            && (ICaiFangTongConstant.FPLX_3.equals(kplx)//空白废票
                            || ICaiFangTongConstant.FPLX_4.equals(kplx)//正废
                            || ICaiFangTongConstant.FPLX_5.equals(kplx))){//负废
                        errorCount++;
                        msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成入库单。</p></font>");
                    }else if(StringUtil.isEmpty(vo.getPk_tzpz_h())){
                        ichvo = gl_vatincinvact2.createIC(vo, accounts, corpvo, userid);
//						gl_vatincinvact2.saveGL(ichvo, pk_corp, userid);
                        msg.append("<font color='#2ab30f'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成入库单成功。</p></font>");
                    }else{
                        errorCount++;
                        msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，不能生成入库单。</p></font>");
                    }
                    ichvo = null;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);

                    String err = ichvo == null ? "入库单" : "凭证";
                    if(!StringUtil.isEmpty(e.getMessage())
                            && e instanceof BusinessException){
                        errorCount++;
                        msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成"+ err +"失败，原因:")
                                .append(e.getMessage())
                                .append("。</p></font>");
                    }else{
                        errorCount++;
                        msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生"+ err +"失败。</p></font>");
                    }
                }
            }

            json.setSuccess(errorCount > 0 ? false : true);
            json.setMsg(msg.toString());
        } catch (Exception e) {
            printErrorLog(json, e, "生成入库单失败");
        } finally {
            if(lock){
                redissonDistributedLock.releaseDistributedFairLock("jinxiang2ic"+pk_corp);
            }
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "进项发票生成入库单", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private VatGoosInventoryRelationVO[] getGoodsData(String goods){

        VatGoosInventoryRelationVO[] vos = null;

        if(StringUtil.isEmpty(goods)){
            return null;
        }

        vos = JsonUtils.deserialize(goods, VatGoosInventoryRelationVO[].class);
        return vos;
    }


    private void checkBeforeIC(CorpVO corpvo){
        Integer icstyle = corpvo.getIbuildicstyle();
        if(icstyle == null || icstyle != 1){//只针对库存新模式
            throw new BusinessException("进项生成入库单只支持库存新模式。");
        }


        if(!IcCostStyle.IC_ON.equals(corpvo.getBbuildic())){
            throw new BusinessException("进项生成入库单前需启用库存模块。");
        }

    }


    @RequestMapping("/matchInventoryData_long")
    public ReturnData<Grid> matchInventoryData_long(@RequestBody Map<String,String> param) {
        Grid grid = new Grid();
        try {
            String body = param.get("head");
            String isshow = param.get("ishow");
            String goods = param.get("goods");

            if (body == null) {
                throw new BusinessException("数据为空,存货匹配失败!");
            }

            if(StringUtil.isEmpty(isshow)){
                isshow ="Y";
            }

            VATInComInvoiceVO2[] vos = JsonUtils.deserialize(body,VATInComInvoiceVO2[].class);
            if (vos == null || vos.length == 0)
                throw new BusinessException("数据为空,存货匹配失败，请检查");
            StringBuffer msg = new StringBuffer();
            String kplx = null;

            List<VATInComInvoiceVO2> list = new ArrayList<>();
            for (VATInComInvoiceVO2 vo : vos) {
                if(!StringUtils.isEmpty(vo.getImgpath())){
                    throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
                }
                kplx = vo.getKplx();
                if (!StringUtil.isEmpty(kplx) && (ICaiFangTongConstant.FPLX_3.equals(kplx)// 空白废票
                        || ICaiFangTongConstant.FPLX_4.equals(kplx)// 正废
                        || ICaiFangTongConstant.FPLX_5.equals(kplx))) {// 负废
                    msg.append("<p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成凭证。</p>");
                } else if (!StringUtil.isEmpty(vo.getPk_tzpz_h())) {

                    msg.append("<p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成凭证。</p>");
                }else{
                    list.add(vo);
                }
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
//					throw new BusinessException("进项发票存货匹配失败:"+error);
//				}

                String pk_corp = SystemUtil.getLoginCorpId();
                checkSecurityData(null,new String[]{pk_corp},null);
                List<InventoryAliasVO> relvos = gl_vatincinvact2.matchInventoryData(pk_corp, vos,invsetvo);

                if(relvos != null && relvos.size()>0){

                    String error = ocrinterface.checkInvtorySubj(relvos.toArray(new InventoryAliasVO[0]), invsetvo, pk_corp,SystemUtil.getLoginUserId(), false);
                    if (!StringUtil.isEmpty(error)) {
                        error = error.replaceAll("<br>", " ");
                        throw new BusinessException("进项发票存货匹配失败:"+error);
                    }

                    InventoryAliasVO[] goodvos = getInvAliasData(goods);
                    String[] keys = new String[]{"aliasname","spec","invtype","unit"};
                    Map<String,InventoryAliasVO> map = null;
                    if (!DZFValueCheck.isEmpty(goods)){
                        map = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(goodvos), keys);
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
                grid.setMsg("进项发票存货匹配成功");
            }
        } catch (Exception e) {
            printErrorLog(grid, e, "进项发票存货匹配失败");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "进项发票存货匹配", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    @RequestMapping("/saveInventoryData")
    public ReturnData<Grid> saveInventoryData(@RequestBody Map<String,String> param) {
        Grid grid = new Grid();
        String pk_corp = "";
        boolean lock = false;
        try {
            String goods = param.get("goods");
            pk_corp = SystemUtil.getLoginCorpId();
            checkSecurityData(null,new String[]{pk_corp},null);
            // 加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("jinxiang2pp"+pk_corp);
            if (!lock) {// 处理
                grid.setSuccess(false);
                grid.setMsg("正在处理中，请稍候");
                return ReturnData.error().data(grid);
            }

            InventoryAliasVO[] goodvos = getInvAliasData(goods);
            if (goodvos == null || goodvos.length == 0)
                throw new BusinessException("未找到存货别名数据，请检查");
            InventorySetVO invsetvo = query();
            String error = ocrinterface.checkInvtorySubj(goodvos, invsetvo, pk_corp, SystemUtil.getLoginUserId(), true);
            if (!StringUtil.isEmpty(error)) {
                error = error.replaceAll("<br>", " ");
                throw new BusinessException("进项发票存货匹配失败:"+error);
            }
            List<Grid> logList = new ArrayList<Grid>();//记录更新日志
            gl_vatincinvact2.saveInventoryData(pk_corp, goodvos, logList);
            //保存操作日志
            for(Grid loggrid: logList){
                writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "进项发票_"+loggrid.getMsg(), ISysConstants.SYS_2);
            }
            grid.setSuccess(true);
            grid.setMsg("进项发票匹配存货成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "进项发票匹配存货失败");
        } finally {
            if(lock){
                redissonDistributedLock.releaseDistributedFairLock("jinxiang2pp"+pk_corp);
            }
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "进项发票匹配存货", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    /**
     * 赋值存货主键
     * @param list
     * @param goods
     */
    private void goodsToVatBVO(List<VATInComInvoiceVO2> list, VatGoosInventoryRelationVO[] goods, String pk_corp, String userid){
        if(goods != null && goods.length > 0){
            Map<String, VatGoosInventoryRelationVO> map = DZfcommonTools.hashlizeObjectByPk(
                    Arrays.asList(goods), new String[]{ "spmc", "invspec","unit" });
            CorpVO corpvo = corpService.queryByPk(pk_corp);
            String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
            if(corpvo.getBbuildic().equals(IcCostStyle.IC_ON)){
                Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(pk_corp);
                YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);
                VATInComInvoiceBVO2[] children;
                String pk_inventory;
                String pk_inventory_old;
//			List<VATSaleInvoiceBVO2> bvoList;
//			Map<String, List<VATSaleInvoiceBVO2>> relmap = new HashMap<String, List<VATSaleInvoiceBVO2>>();
                List<VatGoosInventoryRelationVO> relList;
                Map<String, VatGoosInventoryRelationVO> temp = new HashMap<String, VatGoosInventoryRelationVO>();
                Map<String, List<VatGoosInventoryRelationVO>> newRelMap = new HashMap<String, List<VatGoosInventoryRelationVO>>();
                for(VATInComInvoiceVO2 vo : list){
                    children = (VATInComInvoiceBVO2[]) vo.getChildren();

                    if(children != null && children.length > 0){
                        String key;
                        VatGoosInventoryRelationVO relvo;
                        for(VATInComInvoiceBVO2 bvo : children){
                            key = bvo.getBspmc() + "," + bvo.getInvspec()+","+bvo.getMeasurename();

                            if(map.containsKey(key)){
                                relvo = map.get(key);
                                pk_inventory = relvo.getPk_inventory();

                                pk_inventory_old = relvo.getPk_inventory_old();
                                //主键为空的情况,,新增,,
                                //old不为空,,,,有可能新增有可能别名,,,,
                                if(StringUtil.isEmpty(pk_inventory)//肯定新增,如果有在主表匹配的和当前pk一致的则认为他是匹配自己主表的
                                        ||  (!StringUtil.isEmpty(pk_inventory_old)&&pk_inventory_old.equals(pk_inventory)  )
                                ){
                                    ocrinterface.matchInvtoryIC(relvo,pk_corp,userid,newrule,accmap,accounts);

                                }else{
                                    bvo.setPk_inventory(pk_inventory);

                                    if(!temp.containsKey(key)){//过滤不需要的数据//!pk_inventory.equals(pk_inventory_old) &&
                                        if(newRelMap.containsKey(pk_inventory)){
                                            relList = newRelMap.get(pk_inventory);
                                            relList.add(relvo);
                                        }else{
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
            }else{
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

    @RequestMapping("/createPzData_long")
    public ReturnData<Grid> createPzData_long(@RequestBody Map<String,String> param) {
        String sourcename = param.get("sourcename");
        String body = param.get("head");
        String jsfs = param.get("jsfs");
        String inperiod = param.get("inperiod");
        String goods = param.get("goods");
        Grid grid = new Grid();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp},null);
        boolean lock = false;
        String sourceName = StringUtil.isEmpty(sourcename)?"填制凭证_":sourcename+"_";
        try {

            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("jinxiang2pz"+pk_corp);
            if(!lock){//处理
                grid.setSuccess(false);
                grid.setMsg("正在处理中，请稍候");
                return ReturnData.ok().data(grid);
            }
            if (body == null) {
                throw new BusinessException("数据为空,生成凭证失败!");
            }

            VATInComInvoiceVO2[] vos = JsonUtils.deserialize(body,VATInComInvoiceVO2[].class);
            if (vos == null || vos.length == 0)
                throw new BusinessException("数据为空,生成凭证失败，请检查");
            List<VATInComInvoiceVO2> stoList = gl_vatincinvact2.construcComInvoice(vos, pk_corp);
            if (stoList == null || stoList.size() == 0){
                throw new BusinessException("未找进项发票数据，请检查");
            }
            Map<String, DcModelHVO> dcmodelmap = gl_yhdzdserv2.queryDcModelVO(pk_corp);
            for (VATInComInvoiceVO2 vo : stoList) {
                if(!StringUtils.isEmpty(vo.getImgpath())){
                    throw new BusinessException("发票号码：'" + vo.getFp_hm() + "' 是智能识别票据，请至票据工作台进行相关处理！");
                }
                //List<DcModelHVO> list = gl_yhdzdserv2.queryIsDcModel(vo.getPk_model_h());
//				if(list!=null&&list.size()>0){
                if(dcmodelmap.containsKey(vo.getPk_model_h())){
                    throw new BusinessException("发票号码"+vo.getFp_hm()+"请重新选择业务类型");
                }
            }
			/*String checkMsg = gl_vatincinvact2.checkIsStock(stoList);
			if(!StringUtils.isEmpty(checkMsg)){
				throw new BusinessException(checkMsg);
			}*/

            InventorySetVO invsetvo = query();
            Map<String, YntCpaccountVO> ccountMap = accountService.queryMapByPk(pk_corp);
            if (invsetvo == null)
                throw new BusinessException("存货设置未设置!");
            int chcbjzfs = invsetvo.getChcbjzfs();
//			String error = inventory_setcheck.checkInventorySet(getLoginUserid(), getLogincorppk(),invsetvo);
//			if (!StringUtil.isEmpty(error)) {
//				error = error.replaceAll("<br>", " ");
//				throw new BusinessException("进项发票生成凭证失败:"+error);
//			}

//			if (chcbjzfs == 2) {// 不核算明细
//				YntCpaccountVO cvo = getRkkmVO(invsetvo, ccountMap);
//				if (cvo.getIsfzhs().charAt(5) == '1') {
//					throw new BusinessException("科目【" + cvo.getAccountname() + "】已启用存货辅助,请调整存货设置或停用科目辅助!");
//				}
//			}

            if (chcbjzfs != 2) {
                InventoryAliasVO[] goodvos = getInvAliasData(goods);
                if(goodvos != null &&goodvos.length> 0){
                    String error = ocrinterface.checkInvtorySubj(goodvos, invsetvo, pk_corp, SystemUtil.getLoginUserId(), false);
                    if (!StringUtil.isEmpty(error)) {
                        error = error.replaceAll("<br>", " ");
                        throw new BusinessException("进项发票存货匹配失败:"+error);
                    }


                    List<InventoryAliasVO> list = new ArrayList<>();
                    for(InventoryAliasVO  good:goodvos){
                        if(good.getIsMatch() == null || !good.getIsMatch().booleanValue())
                            list.add(good);
                    }
                    List<Grid> logList = new ArrayList<Grid>();//记录更新日志
                    if(list != null && list.size()>0){
                        gl_vatincinvact2.saveInventoryData(pk_corp, list.toArray(new InventoryAliasVO[list.size()]), logList);
                    }
                    //保存操作日志
                    for(Grid loggrid: logList){
                        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, sourceName+loggrid.getMsg(), ISysConstants.SYS_2);
                    }
                }
            }

            if(!StringUtil.isEmpty(inperiod)){
                for (VATInComInvoiceVO2 vo : stoList) {
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
            printErrorLog(grid,e, "进项发票生成凭证失败");
        }finally {
            if(lock){
                redissonDistributedLock.releaseDistributedFairLock("jinxiang2pz"+pk_corp);
            }
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "进项发票生成凭证", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    private YntCpaccountVO getRkkmVO(InventorySetVO invsetvo, Map<String, YntCpaccountVO> ccountMap) {
        String pk_accsubj = invsetvo.getKcsprkkm();
        if (StringUtil.isEmpty(pk_accsubj)) {
            throw new BusinessException("入库科目未设置!");
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
        vos = JsonUtils.deserialize(goods, InventoryAliasVO[].class);
        return vos;
    }


    private List<Object> combinePZData(List<VATInComInvoiceVO2> stoList, InventorySetVO invsetvo, String jsfs) {
        VatInvoiceSetVO setvo = queryRuleByType();
        String numStr = parameterserv.queryParamterValueByCode(SystemUtil.getLoginCorpId(), IParameterConstants.DZF009);
        int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
        int chcbjzfs = invsetvo.getChcbjzfs();
        if (chcbjzfs != 2) {
            List<InventoryAliasVO> alist = gl_vatincinvact2.matchInventoryData(SystemUtil.getLoginCorpId(),
                    stoList.toArray(new VATInComInvoiceVO2[stoList.size()]),invsetvo);

            if (alist == null || alist.size() == 0)
                throw new BusinessException("存货匹配信息出错");
            int pprule = invsetvo.getChppjscgz();//匹配规则
            for (VATInComInvoiceVO2 incomvo : stoList) {

                VATInComInvoiceBVO2[] ibodyvos = (VATInComInvoiceBVO2[]) incomvo.getChildren();
                if (ibodyvos == null || ibodyvos.length == 0)
                    continue;
                for (VATInComInvoiceBVO2 body : ibodyvos) {
                    VATInComInvoiceBVO2 ibody = (VATInComInvoiceBVO2) body;
                    String key1 =buildByRule( ibody.getBspmc(), ibody.getInvspec(), ibody.getMeasurename(), pprule);
                    for (InventoryAliasVO aliavo : alist) {
                        String key =buildByRule( aliavo.getAliasname(), aliavo.getSpec(), aliavo.getUnit(), pprule);
                        if (key1.equals(key)) {
                            ibody.setPk_inventory(aliavo.getPk_inventory());
                            ibody.setPk_accsubj(aliavo.getKmclassify());
                            // 根据换算方式 计算数量
                            // 根据换算方式 计算数量
//							if(aliavo.getCalcmode()==0){
//								ibody.setBnum(SafeCompute.multiply(ibody.getBnum(), aliavo.getHsl()).setScale(num,  DZFDouble.ROUND_HALF_UP));
//							}else if(aliavo.getCalcmode()==1){
//								ibody.setBnum(SafeCompute.div(ibody.getBnum(), aliavo.getHsl()).setScale(num,  DZFDouble.ROUND_HALF_UP));
//							}
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
            list = combinePZData1(stoList, setvo, invsetvo,jsfs);
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
    private List<Object> createPZData(List<VATInComInvoiceVO2> stoList, VatInvoiceSetVO setvo, InventorySetVO invsetvo, String jsfs) {
        String pk_corp = SystemUtil.getLoginCorpId();

        boolean accway = getAccWay(pk_corp);

        String userid = SystemUtil.getLoginUserId();
        StringBuffer msg = new StringBuffer();

        List<Object> list = new ArrayList<>();
        String kplx = null;
        String key;
        String period;
        List<String> periodSet = new ArrayList<String>();
        int err=0;
        for (VATInComInvoiceVO2 vo : stoList) {
            kplx = vo.getKplx();
            key = buildkey(vo, setvo);
            period = splitKey(key);
            if(!periodSet.contains(period)){
                periodSet.add(period);//组装查询期间
            }
            try {
                if (!StringUtil.isEmpty(kplx) && (ICaiFangTongConstant.FPLX_3.equals(kplx)// 空白废票
                        || ICaiFangTongConstant.FPLX_4.equals(kplx)// 正废
                        || ICaiFangTongConstant.FPLX_5.equals(kplx))) {// 负废
                    err++;
                    periodSet.remove(period);
                    msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成凭证。</p></font>");
                } else if (StringUtil.isEmpty(vo.getPk_tzpz_h())) {
                    try {
                        gl_vatincinvact2.createPZ(vo, pk_corp, userid, accway, false, invsetvo, setvo, jsfs);
                        msg.append("<font color='#2ab30f'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");
                    } catch (Exception e) {
                        gl_vatincinvact2.createPZ(vo, pk_corp, userid, accway, true, invsetvo, setvo, jsfs);
                        msg.append("<font color='#2ab30f'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");
                    }

                } else {
                    err++;
                    periodSet.remove(period);
                    msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成凭证。</p></font>");

                }
            } catch (Exception e) {

                log.error(e.getMessage(), e);
                err++;
                if (e instanceof BusinessException) {
                    msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败，原因:")
                            .append(e.getMessage()).append("。</p></font>");
                } else {
                    msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败").append("。</p></font>");
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
    private List<Object>  combinePZData1(List<VATInComInvoiceVO2> stoList, VatInvoiceSetVO setvo, InventorySetVO invsetvo,
                                         String jsfs) {
        String pk_corp = SystemUtil.getLoginCorpId();

        String userid = SystemUtil.getLoginUserId();

        boolean accway = getAccWay(pk_corp);
        Map<String, List<VATInComInvoiceVO2>> combineMap = new LinkedHashMap<String, List<VATInComInvoiceVO2>>();
        StringBuffer msg = new StringBuffer();
        List<Object> list = new ArrayList<>();
        int err=0;
        String key;
        String period;
        List<String> periodSet = new ArrayList<String>();
        List<VATInComInvoiceVO2> combineList = null;
        String kplx = null;
        for (VATInComInvoiceVO2 vo : stoList) {

            key = buildkey(vo, setvo);
            period = splitKey(key);
            if(!periodSet.contains(period)){
                periodSet.add(period);//组装查询期间
            }
            kplx = vo.getKplx();

            if (!StringUtil.isEmpty(kplx) && (ICaiFangTongConstant.FPLX_3.equals(kplx)// 空白废票
                    || ICaiFangTongConstant.FPLX_4.equals(kplx)// 正废
                    || ICaiFangTongConstant.FPLX_5.equals(kplx))) {// 负废
                err++;
                periodSet.remove(period);
                msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成凭证</p></font>");
            } else if (!StringUtil.isEmpty(vo.getPk_tzpz_h())) {
                err++;
                periodSet.remove(period);
                msg.append("<font color='red'><p>进项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成凭证。</p></font>");
            } else if (combineMap.containsKey(key)) {
                combineList = combineMap.get(key);

                combineList.add(vo);
            } else {
                combineList = new ArrayList<VATInComInvoiceVO2>();
                combineList.add(vo);
                combineMap.put(key, combineList);
            }
        }

        key = null;
        for (Map.Entry<String, List<VATInComInvoiceVO2>> entry : combineMap.entrySet()) {
            try {
                key = entry.getKey();

                key = splitKey(key);

                combineList = entry.getValue();
                gl_vatincinvact2.saveCombinePZ(combineList, pk_corp, userid, setvo, accway, false, invsetvo, jsfs);
                msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
            } catch (Exception e) {
                try {
                    gl_vatincinvact2.saveCombinePZ(combineList, pk_corp, userid, setvo, accway, true, invsetvo, jsfs);
                    msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
                } catch (Exception e2) {
                    log.error(e.getMessage(), e);
                    err++;
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

    @RequestMapping("/chooseTicketWay")
    public ReturnData<Json> chooseTicketWay(){
        Json json = new Json();
        json.setSuccess(false);
        try {
            CorpVO corpvo = gl_vatincinvact2.chooseTicketWay(SystemUtil.getLoginCorpId());
            json.setHead(corpvo);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
        }

        return ReturnData.ok().data(json);
    }

    /*	public void getBusiType(){
            Json json = new Json();
            json.setSuccess(false);

            try {
                List<VatBusinessTypeVO> list = gl_vatincinvact2.getBusiType(getLogincorppk());

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
        ArrayList<String> pk_categoryList = new ArrayList<String>();
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
            //进项参照
            List<BillCategoryVO> list = gl_vatincinvact2.queryIncomeCategoryRef(corpVO.getPk_corp(),period);
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
    public ReturnData<Grid> queryCategoryset(@RequestBody Map<String,String> param ){
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
        checkSecurityData(null,new String[]{pk_corp},null);
        try {
            String pk_model_h = param.get("pk_model_h");
            String id = param.get("id");
            String busisztypecode = param.get("busisztypecode");
            String pk_basecategory = param.get("pk_basecategory");
            String pk_category_keyword = param.get("pk_category_keyword");
            String rzkm = param.get("rzkm");
            String jskm = param.get("jskm");
            String shkm = param.get("shkm");
            String zdyzy = param.get("zdyzy");

            String[] ids = id.split(",");
//			for (String pk_vatincominvoice : ids) {
//				gl_vatincinvact2.checkvoPzMsg(pk_vatincominvoice);
//			}
//
            gl_vatincinvact2.updateVO(ids , pk_model_h,pk_corp,pk_category_keyword,busisztypecode,rzkm,jskm,shkm);
            gl_vatincinvact2.updateCategoryset(new DZFBoolean(true),pk_model_h,busisztypecode,pk_basecategory,pk_corp,rzkm,jskm,shkm,zdyzy);
            List<VATInComInvoiceVO2> newVOList = gl_vatincinvact2.queryByPks(ids, pk_corp);
            log.info("设置成功！");
            grid.setRows(newVOList);
            grid.setSuccess(true);
            grid.setMsg("设置成功!");
        } catch (Exception e) {
            printErrorLog(grid, e, "设置失败！");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "进项发票更新业务类型", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }



    @RequestMapping("/getGoodsInvenRela_long")
    public ReturnData<Grid> getGoodsInvenRela_long(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        CorpVO corpvo = SystemUtil.getLoginCorpVo();
        String pk_corp = corpvo.getPk_corp();
        checkSecurityData(null,new String[]{pk_corp},null);
        try{

            String body = param.get("head");
            if(!corpvo.getBbuildic().equals(IcCostStyle.IC_OFF)){

                if (body == null) {
                    throw new BusinessException("数据为空,生成凭证失败!");
                }


                VATInComInvoiceVO2[] vos = JsonUtils.deserialize(body, VATInComInvoiceVO2[].class);
                if(vos == null || vos.length == 0)
                    throw new BusinessException("数据为空,生成凭证失败，请检查");

                //	String pk_corp = getLogincorppk();

                //Map<String, DcModelHVO> dcmap = gl_yhdzdserv2.queryDcModelVO(pk_corp);
                for (VATInComInvoiceVO2 vo : vos) {
//				gl_vatincinvact2.checkvoPzMsg(vo.getPk_vatincominvoice());
                    if(!StringUtils.isEmpty(vo.getImgpath())){
                        throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
                    }
                }
                List<VATInComInvoiceVO2> stoList = gl_vatincinvact2.constructVatSale(vos, pk_corp);

                if(stoList == null
                        || stoList.size() == 0)
                    throw new BusinessException("未找销项发票数据，请检查");

                List<VatGoosInventoryRelationVO> relvos = gl_vatincinvact2.getGoodsInvenRela(stoList, pk_corp);

                List<VatGoosInventoryRelationVO> blankvos = new ArrayList<VatGoosInventoryRelationVO>();
                List<VatGoosInventoryRelationVO> newRelvos = getFilterGoods(relvos, blankvos, pk_corp);

                Long total =relvos == null ? 0L : relvos.size();
//			if(relvos.size() == 0){
//				total = newRelvos == null ? 0L : newRelvos.size();
//			}
                CorpVO corp = corpService.queryByPk(pk_corp);
//			if(relvos.size() == 0){
//				total = newRelvos == null ? 0L : newRelvos.size();
//			}
//			if(!IcCostStyle.IC_OFF.equals(corp.getBbuildic()) && (relvos == null || relvos.size() == 0))
//				throw new BusinessException("所选非存货无需匹配存货!");


                grid.setTotal(total);
                grid.setRows(relvos);
                grid.setSuccess(true);
                grid.setMsg("获取存货对照关系成功");
            }else{
                grid.setTotal(null);
                grid.setSuccess(true);
            }
        }catch(Exception e){
            printErrorLog(grid, e, "获取存货对照关系失败");
        }

        return ReturnData.ok().data(grid);
    }


    private List<VatGoosInventoryRelationVO> getFilterGoods(List<VatGoosInventoryRelationVO> relvos,
                                                            List<VatGoosInventoryRelationVO> blankvos,
                                                            String pk_corp){

        if(relvos==null) return null;
        CorpVO corpvo = corpService.queryByPk(pk_corp);
        if(!corpvo.getBbuildic().equals(IcCostStyle.IC_OFF)){
            YntCpaccountVO[] cpavos = accountService.queryByPk(pk_corp);

            String code;
            String fzhs;
            boolean flag = false;
            for(YntCpaccountVO cpa : cpavos){
                code = cpa.getAccountcode();
                if(!StringUtil.isEmpty(code)
                        && (code.startsWith("500101")
                        || code.startsWith("600101")
                        || code.startsWith("5001001")
                        || code.startsWith("6001001"))){
                    fzhs = cpa.getIsfzhs();
                    if(!StringUtil.isEmpty(fzhs) && fzhs.charAt(5) == '1'){
                        flag = true;
                        break;
                    }

                }
            }

            if(!flag){
                return null;
            }

        }

        List<VatGoosInventoryRelationVO> sencondList = new ArrayList<VatGoosInventoryRelationVO>();

        if(relvos != null && relvos.size() > 0){
            String pk_inventory;
            for(VatGoosInventoryRelationVO vo : relvos){
                pk_inventory = vo.getPk_inventory();
                if(StringUtil.isEmpty(pk_inventory)){
                    blankvos.add(vo);
                }
                sencondList.add(vo);
            }
        }

        return sencondList;
    }

    private VATInComInvoiceVO2 getHeadVO(List<VATInComInvoiceVO2> list){
        DZFDouble zje = new DZFDouble();
        DZFDouble zse  = new DZFDouble();
        DZFDouble zjshj = new DZFDouble();

        if(list != null && list.size() > 0){
            for(VATInComInvoiceVO2 vo : list){
                if(StringUtils.isEmpty(vo.getKplx())||(!vo.getKplx().equals("4")&&!vo.getKplx().equals("5"))){
                    zje = zje.add(vo.getHjje());
                    zse = zse.add(vo.getSpse());
                    zjshj = zjshj.add(vo.getJshj());
                }
            }
        }
        VATInComInvoiceVO2 vo = new VATInComInvoiceVO2();
        vo.setZje(zje);
        vo.setZse(zse);
        vo.setZjshj(zjshj);
        return vo;
    }

    private void saveOrUpdateCorpReference(VATInComInvoiceVO2 inComvo){
        CorpReferenceVO vo = new CorpReferenceVO();
        vo.setPk_corp(SystemUtil.getLoginCorpId());
        vo.setCorpname(inComvo.getGhfmc());
        vo.setTaxnum(inComvo.getGhfsbh());
        vo.setAddressphone(inComvo.getGhfdzdh());
        vo.setBanknum(inComvo.getGhfyhzh());
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

            json.setData(referenceVO);
            json.setSuccess(true);
            json.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(json,e, "查询失败");
        }

        return ReturnData.ok().data(json);
    }

    @RequestMapping("/queryB")
    public ReturnData<Json> queryB(@RequestParam("id") String hid,String kmid,String pk_corp) {
        Json json = new Json();
        try {
            if (StringUtil.isEmpty(hid)) {
                json.setMsg("参数为空！");
                json.setSuccess(false);
                return ReturnData.error().data(json);
            }

            if (StringUtil.isEmpty(pk_corp)) {
                pk_corp = SystemUtil.getLoginCorpId();
            } else {
                if (!ReportUtil.checkHasRight(SystemUtil.getLoginUserId(), pk_corp))
                    throw new BusinessException("无权操作！");
            }
            AuxiliaryAccountBVO[] bvos = null;
            List<CustomerReferenceVO> cvos=new ArrayList<CustomerReferenceVO>();
            bvos = gl_fzhsserv.queryB(hid, pk_corp, kmid);
            if (bvos == null || bvos.length == 0) {
                bvos = new AuxiliaryAccountBVO[0];
            } else {
                bvos = Arrays.asList(bvos).stream().filter(v -> v.getSffc() == null || v.getSffc() == 0)
                        .toArray(AuxiliaryAccountBVO[]::new);

                for (AuxiliaryAccountBVO vo : bvos) {
                    CustomerReferenceVO cvo = new CustomerReferenceVO();
                    if(!StringUtils.isEmpty(vo.getName())){
                        String pinYin = PinyinUtil.getPinYin(vo.getName());
                        cvo.setPyname(pinYin);
                    }
                    cvo.setName(vo.getName());
                    cvo.setBank(vo.getBank());
                    cvo.setCredit_code(vo.getCredit_code());
                    cvo.setPhone_num(vo.getPhone_num());
                    cvo.setCode(vo.getCode());
                    cvo.setAccount_num(vo.getAccount_num());
                    cvo.setAddress(vo.getAddress());
                    cvo.setPk_auacount_b(vo.getPk_auacount_b());
                    cvo.setPk_auacount_h(vo.getPk_auacount_h());
                    cvos.add(cvo);
                }
            }
            json.setRows(cvos);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            json.setRows(new AuxiliaryAccountBVO[0]);
            printErrorLog(json, e, "查询失败!");
        }
        return ReturnData.ok().data(json);
    }

}
