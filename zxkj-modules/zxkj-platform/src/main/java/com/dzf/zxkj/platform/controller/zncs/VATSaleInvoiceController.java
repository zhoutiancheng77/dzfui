package com.dzf.zxkj.platform.controller.zncs;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSON;
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
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.image.DcModelBVO;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.pjgl.*;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.model.zncs.VATSaleInvoiceBVO;
import com.dzf.zxkj.platform.service.bdset.IPersonalSetService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.glic.impl.CheckInventorySet;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IDcpzService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.zncs.IBankStatementService;
import com.dzf.zxkj.platform.service.zncs.IVATSaleInvoiceService;
import com.dzf.zxkj.platform.service.zncs.IVatInvoiceService;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.dzf.zxkj.platform.util.zncs.ICaiFangTongConstant;
import com.dzf.zxkj.platform.util.zncs.VatExportUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.*;

@RestController
@RequestMapping("/zncs/gl_vatsalinvact")
public class VATSaleInvoiceController extends BaseController {

    private Logger log = Logger.getLogger(this.getClass());

    @Autowired
    private IVATSaleInvoiceService gl_vatsalinvserv;
    @Autowired
    private IBankStatementService gl_yhdzdserv;
    //	@Autowired
//	private ITaxitemsetService taxitemserv;
    @Autowired
    private IVatInvoiceService vatinvoiceserv;
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
//	@Autowired
//	private ICbComconstant gl_cbconstant;

    @Autowired
    private CheckInventorySet inventory_setcheck;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private ICorpService corpService;
    @Autowired
    private RedissonDistributedLock redissonDistributedLock;

    @Autowired
    private IInventoryAccSetService gl_ic_invtorysetserv = null;

    @RequestMapping("/queryInfo")
    public ReturnData queryInfo(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
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
            if(StringUtil.isEmpty(SystemUtil.getLoginCorpId())){//corpVo.getPrimaryKey()
                throw new BusinessException("出现数据无权问题！");
            }

            InvoiceParamVO paramvo = getQueryParamVO(head);

            List<VATSaleInvoiceVO> list = gl_vatsalinvserv.quyerByPkcorp(paramvo, sort, order);
            //list变成数组
            grid.setTotal((long) (list==null ? 0 : list.size()));
            //分页
            VATSaleInvoiceVO[] vos = null;
            if(list!=null && list.size()>0){
                vos = getPagedZZVOs(list.toArray(new VATSaleInvoiceVO[0]),page,rows);
                for (VATSaleInvoiceVO vo:vos) {
                    //处理改版前的图片路径，将/gl/gl_imgview!search.action替换成/zncs/gl_imgview/search
                    if(!StringUtil.isEmpty(vo.getImgpath())&&vo.getImgpath().contains("/gl/gl_imgview!search.action")){
                        vo.setImgpath(vo.getImgpath().replace("/gl/gl_imgview!search.action","/zncs/gl_imgview/search"));
                    }
                }
            }
            log.info("查询成功！");
            grid.setRows(vos==null?new ArrayList<VATSaleInvoiceVO>(): Arrays.asList(vos));
            grid.setSuccess(true);
            grid.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
        }

        return ReturnData.ok().data(grid);
    }

    @RequestMapping("/queryInfoByID")
    public ReturnData queryInfoByID(String id){
        Json json = new Json();

        try {
            VATSaleInvoiceVO hvo = gl_vatsalinvserv.queryByID(id);

            json.setData(hvo);
            json.setSuccess(true);
            json.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
        }

        return ReturnData.ok().data(json);
    }

    private InvoiceParamVO getQueryParamVO(String head){

        InvoiceParamVO paramvo = JsonUtils.deserialize(head, InvoiceParamVO.class);

        if(paramvo == null){
            paramvo = new InvoiceParamVO();
        }

        paramvo.setPk_corp(SystemUtil.getLoginCorpId());

        return paramvo;
    }

    private VATSaleInvoiceVO[] getPagedZZVOs(VATSaleInvoiceVO[] vos, int page, int rows) {
        int beginIndex = rows * (page-1);
        int endIndex = rows * page;
        if(endIndex >= vos.length){//防止endIndex数组越界
            endIndex = vos.length;
        }
        vos = Arrays.copyOfRange(vos, beginIndex, endIndex);
        return vos;
    }

    //修改保存
    @RequestMapping("/onUpdate")
    public ReturnData onUpdate(@RequestBody Map<String,String> param){
        String msg = "";//记录日志
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try{

            Map<String, VATSaleInvoiceVO[]> sendData = new HashMap<String, VATSaleInvoiceVO[]>();

            String head = param.get("header");
            String body = param.get("body");

            VATSaleInvoiceVO headvo = JsonUtils.deserialize(head, VATSaleInvoiceVO.class);
            VATSaleInvoiceBVO[] bodyvos = JsonUtils.deserialize(body,  VATSaleInvoiceBVO[].class);

            bodyvos = filterBlankBodyVos(bodyvos);
            checkJEValid(headvo, bodyvos);
            setDefultValue(headvo,bodyvos);
            headvo.setChildren(bodyvos);

            msg += SystemUtil.getLoginUserVo().getUser_name();

            if(StringUtil.isEmpty(headvo.getPrimaryKey())){
                sendData.put("adddocvos", new VATSaleInvoiceVO[]{headvo});

                msg += "新增发票号码(" + headvo.getFp_hm() + ")的销项发票";
            }else{
                sendData.put("upddocvos", new VATSaleInvoiceVO[]{headvo});

                msg += "修改发票号码(" + headvo.getFp_hm() + ")的销项发票";
            }

            VATSaleInvoiceVO[] addvos = gl_vatsalinvserv.updateVOArr(pk_corp, sendData);

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
            msg = "销项发票编辑";
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, msg, ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private VATSaleInvoiceBVO[] filterBlankBodyVos(VATSaleInvoiceBVO[] bodyvos){
        List<VATSaleInvoiceBVO> filterList = new ArrayList<VATSaleInvoiceBVO>();
        if(bodyvos != null && bodyvos.length > 0){
            VATSaleInvoiceBVO bvo = null;
            for(int i = 0; i < bodyvos.length; i++){
                bvo = bodyvos[i];
                //处理前端传过来的空数据，过滤
                if(StringUtils.isEmpty(bvo.getBspmc())&&bvo.getBhjje()==null){
                    continue;
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

        return filterList.toArray(new VATSaleInvoiceBVO[0]);
    }

    private void checkJEValid(VATSaleInvoiceVO vo, VATSaleInvoiceBVO[] body){
        if(vo.getKprj() == null)
            throw new BusinessException("开票日期不允许为空或日期格式不正确，请检查");

        if(vo.getKprj().before(SystemUtil.getLoginCorpVo().getBegindate())){
            throw new BusinessException("开票日期不允许在建账日期前，请检查");
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
//
//
    }

    private void setDefultValue(VATSaleInvoiceVO vo, VATSaleInvoiceBVO[] body){

        DZFDouble bse = DZFDouble.ZERO_DBL;
        DZFDouble bje = DZFDouble.ZERO_DBL;
        DZFDouble bsl = DZFDouble.ZERO_DBL;
        StringBuffer spmchz = new StringBuffer();
        VATSaleInvoiceBVO bvo = null;
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
        }

        if(spmchz.length() > 0){
            vo.setSpmc(spmchz.toString());
        }

        vo.setHjje(bje);//金额
        vo.setSpse(bse);//税额
        vo.setJshj(SafeCompute.add(bje, bse));
        DZFDouble sl = SafeCompute.multiply(SafeCompute.div(vo.getSpse(), vo.getHjje()), new DZFDouble(100));
        vo.setSpsl(sl.setScale(0, DZFDouble.ROUND_HALF_UP));//税率
        vo.setPeriod(DateUtils.getPeriod(vo.getKprj()));
        vo.setInperiod(vo.getPeriod());//入账期间

        if(!StringUtil.isEmpty(vo.getPrimaryKey())){
            vo.setModifydatetime(new DZFDateTime());
            vo.setModifyoperid(SystemUtil.getLoginUserId());
        }else{
            vo.setDoperatedate(new DZFDate());
            vo.setCoperatorid(SystemUtil.getLoginUserId());
            vo.setPk_corp(SystemUtil.getLoginCorpId());
            vo.setSourcetype(IBillManageConstants.MANUAL);
        }

        if(StringUtil.isEmpty(vo.getPk_model_h())){
            setBusiNameFromFon(vo, SystemUtil.getLoginCorpId());
        }
    }

    private void setBusiNameFromFon(VATSaleInvoiceVO vo, String pk_corp){
        String businame = vo.getBusitypetempname();
        String busicode = vo.getBusisztypecode();
        if(StringUtil.isEmpty(businame)
                || StringUtil.isEmpty(busicode)){
            return;
        }

        List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
        Map<String, DcModelHVO> dcmap = hashliseBusiTypeMap(dcList);

        String stylecode = vo.getIszhuan() != null && vo.getIszhuan().booleanValue()
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
    }

    private Map<String, DcModelHVO> hashliseBusiTypeMap(List<DcModelHVO> dcList){
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
    }

    //删除记录
    @RequestMapping("/onDelete")
    public ReturnData onDelete(@RequestBody Map<String,String> param){
        Json json = new Json();
        json.setSuccess(false);
        StringBuffer strb = new StringBuffer();
        VATSaleInvoiceVO[] bodyvos = null;
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        boolean lock = false;
        String msg = "";
        List<String> sucHM = new ArrayList<String>();
        List<String> errHM = new ArrayList<String>();
        try{
            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("xiaoxiangdel"+pk_corp);
            if(!lock){//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍候刷新界面");
                return ReturnData.error().data(json);
            }

            String body = param.get("head"); //
            if (body == null) {
                throw new BusinessException("数据为空,删除失败!!");
            }

            bodyvos = JsonUtils.deserialize(body, VATSaleInvoiceVO[].class);

            if (bodyvos == null || bodyvos.length == 0) {
                throw new BusinessException("数据为空,删除失败!!");
            }

            for(VATSaleInvoiceVO vo : bodyvos){
                try {
                    gl_vatsalinvserv.delete(vo, pk_corp);
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
            if(sucHM.size() > 0){
                inmsg += SystemUtil.getLoginUserVo().getUser_name()
                        + "删除销项发票,发票号码("+sucHM.get(0)+")等"+sucHM.size()+"条记录成功";
            }
            if(errHM.size() > 0){
                if(StringUtil.isEmpty(inmsg)){
                    inmsg += SystemUtil.getLoginUserVo().getUser_name()
                            + "删除销项发票,发票号码("+errHM.get(0)+")等"+errHM.size()+"条记录失败";
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
                redissonDistributedLock.releaseDistributedFairLock("xiaoxiangdel"+pk_corp);
            }
        }
        json.setMsg(strb.toString());

        if(StringUtil.isEmpty(msg)){
            msg = "销项发票编辑";
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, msg, ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @RequestMapping("/impExcel")
    public ReturnData impExcel(@RequestBody MultipartFile file,String impForce){
        String userid = SystemUtil.getLoginUserId();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, userid);
        Json json = new Json();
        json.setSuccess(false);
        try {
//			String source = null;//arr[data.getSourcetem() - 1];

            DZFBoolean isFlag = "Y".equals(impForce) ? DZFBoolean.TRUE : DZFBoolean.FALSE;

            if(file == null || file.getSize()==0){
                throw new BusinessException("请选择导入文件!");
            }
            String fileName = file.getOriginalFilename();
            String fileType = null;
            if (fileName != null && fileName.length() > 0) {
                fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            }

            VATSaleInvoiceVO paramvo = new VATSaleInvoiceVO();

            paramvo.setIsFlag(isFlag);//设置是否强制导入
            StringBuffer msg = new StringBuffer();
            gl_vatsalinvserv.saveImp(file, fileName, paramvo, pk_corp, fileType, userid, msg);

            json.setHead(paramvo);
            json.setMsg(msg.toString());
            json.setSuccess(paramvo.getCount()==0 ? false : true);

            writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                    "导入销项发票；", ISysConstants.SYS_2);
        } catch (Exception e) {
            if(e instanceof BusinessException
                    && IBillManageConstants.ERROR_FLAG.equals(((BusinessException) e).getErrorCodeString())){
                json.setMsg(e.getMessage());
                json.setStatus(Integer.parseInt(IBillManageConstants.ERROR_FLAG));
                json.setSuccess(false);
            }else{
                printErrorLog(json, e, "导入失败!");
            }

        }

        return ReturnData.ok().data(json);
    }

    /**
     * 生成凭证
     */
    public ReturnData createPZ(String lwstr,String body,String goodData){
        Json json = new Json();
        VATSaleInvoiceVO[] vos = null;
        boolean lock = false;
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
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

            vos = JsonUtils.deserialize(body, VATSaleInvoiceVO[].class);

            if(vos == null || vos.length == 0)
                throw new BusinessException("数据为空,生成凭证失败，请检查");

            VatGoosInventoryRelationVO[] goods = getGoodsData(goodData);

            Map<String, DcModelHVO> dcmap = gl_yhdzdserv.queryDcModelVO(pk_corp);

            List<VATSaleInvoiceVO> stoList = gl_vatsalinvserv.constructVatSale(vos, pk_corp);

            boolean accway = getAccWay(pk_corp);
            VatInvoiceSetVO setvo = queryRuleByType();
            if(stoList == null
                    || stoList.size() == 0)
                throw new BusinessException("未找销项发票数据，请检查");

            goodsToVatBVO(stoList, goods, pk_corp,SystemUtil.getLoginUserId());

            int errorCount = 0;
            StringBuffer msg = new StringBuffer();

            String key;
            String period = null;
            List<String> periodSet = new ArrayList<String>();
            String kplx = null;
            for(VATSaleInvoiceVO vo : stoList){
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
                        errorCount++;
                        periodSet.remove(period);
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成凭证。</p></font>");
//					}else if(vo.getIsic() != null && vo.getIsic().booleanValue()){
                    }else if(!StringUtil.isEmpty(vo.getPk_ictrade_h())){
                        errorCount++;
                        periodSet.remove(period);
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已关联出库，不能生成凭证。</p></font>");
                    }
                    else if(StringUtil.isEmpty(vo.getPk_tzpz_h())){
                        gl_vatsalinvserv.createPZ(vo, SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId(), dcmap, setvo, lwflag, accway, false);
                        msg.append("<font color='#2ab30f'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");
                    }else{
                        errorCount++;
                        periodSet.remove(period);
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]单据已生成凭证，无需再次生成凭证。</p></font>");
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if(e instanceof BusinessException
                            && !StringUtil.isEmpty(e.getMessage())
                            && !e.getMessage().startsWith("销项发票")
                            && !e.getMessage().startsWith("制单失败")){
                        try {
                            gl_vatsalinvserv.createPZ(vo, SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId(), dcmap, setvo, lwflag, accway, true);
                            msg.append("<font color='#2ab30f'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");
                        } catch (Exception ex) {
                            errorCount++;
                            if(ex instanceof BusinessException){
                                msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败，原因:")
                                        .append(e.getMessage())
                                        .append("。</p></font>");
                            }else{
                                msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败")
                                        .append("。</p></font>");
                            }
                            periodSet.remove(period);
                        }
                    }else if(!StringUtil.isEmpty(e.getMessage())
                            && (e.getMessage().startsWith("销项发票")
                            || e.getMessage().startsWith("制单失败"))){
                        errorCount++;
                        periodSet.remove(period);
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败，原因:")
                                .append(e.getMessage())
                                .append("。</p></font>");
                    }else{
                        errorCount++;
                        periodSet.remove(period);
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证失败 ")
                                .append("。</p></font>");
                    }
                }
            }

            StringBuffer headMsg = gl_yhdzdserv.buildQmjzMsg(periodSet, pk_corp);
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
                redissonDistributedLock.releaseDistributedFairLock("xiaoxiangcreatepz"+pk_corp);
            }
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "销项发票生成凭证", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    private boolean getAccWay(String pk_corp){
//		String acc = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF012);//入账设置
//		boolean accway = StringUtil.isEmpty(acc) || "0".equals(acc)
//				? true : false;//按入账日期为true, 按期间为false

        return true;
    }

    /**
     * 赋值存货主键
     * @param list
     * @param goods
     */
    private void goodsToVatBVO(List<VATSaleInvoiceVO> list, VatGoosInventoryRelationVO[] goods, String pk_corp, String userid){
        if(goods != null && goods.length > 0){
            Map<String, VatGoosInventoryRelationVO> map = DZfcommonTools.hashlizeObjectByPk(
                    Arrays.asList(goods), new String[]{ "spmc", "invspec" });

            VATSaleInvoiceBVO[] children;
            String pk_inventory;
            String pk_inventory_old;
//			List<VATSaleInvoiceBVO> bvoList;
//			Map<String, List<VATSaleInvoiceBVO>> relmap = new HashMap<String, List<VATSaleInvoiceBVO>>();
            List<VatGoosInventoryRelationVO> relList;
            Map<String, VatGoosInventoryRelationVO> temp = new HashMap<String, VatGoosInventoryRelationVO>();
            Map<String, List<VatGoosInventoryRelationVO>> newRelMap = new HashMap<String, List<VatGoosInventoryRelationVO>>();
            for(VATSaleInvoiceVO vo : list){
                children = (VATSaleInvoiceBVO[]) vo.getChildren();

                if(children != null && children.length > 0){
                    String key;
                    VatGoosInventoryRelationVO relvo;
                    for(VATSaleInvoiceBVO bvo : children){
                        key = bvo.getBspmc() + "," + bvo.getInvspec();

                        if(map.containsKey(key)){
                            relvo = map.get(key);
                            pk_inventory = relvo.getPk_inventory();

                            if(!StringUtil.isEmpty(pk_inventory)){
                                bvo.setPk_inventory(pk_inventory);

                                pk_inventory_old = relvo.getPk_inventory_old();
                                if(!pk_inventory.equals(pk_inventory_old) && !temp.containsKey(key)){//过滤不需要的数据
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

            gl_vatsalinvserv.saveGoodsRela(newRelMap, pk_corp, userid);
        }
    }

    private VatGoosInventoryRelationVO[] getGoodsData(String goods){

        VatGoosInventoryRelationVO[] vos = null;

        if(StringUtil.isEmpty(goods)){
            return null;
        }

        vos = JsonUtils.deserialize(goods, VatGoosInventoryRelationVO[].class);

        return vos;
    }

    @RequestMapping("/getGoodsInvenRela")
    public ReturnData getGoodsInvenRela(@RequestBody Map<String,String> param){
        Grid grid = new Grid();
        try{
            String body = param.get("head");
            if (body == null) {
                throw new BusinessException("数据为空,生成凭证失败!");
            }
            VATSaleInvoiceVO[] vos = JsonUtils.deserialize(body,VATSaleInvoiceVO[].class);

            if(vos == null || vos.length == 0)
                throw new BusinessException("数据为空,生成凭证失败，请检查");

            String pk_corp = SystemUtil.getLoginCorpId();

            Map<String, DcModelHVO> dcmap = gl_yhdzdserv.queryDcModelVO(pk_corp);

            List<VATSaleInvoiceVO> stoList = gl_vatsalinvserv.constructVatSale(vos, pk_corp);

            if(stoList == null
                    || stoList.size() == 0)
                throw new BusinessException("未找销项发票数据，请检查");

            List<VatGoosInventoryRelationVO> relvos = gl_vatsalinvserv.getGoodsInvenRela(
                    dcmap, stoList, pk_corp);

            List<VatGoosInventoryRelationVO> blankvos = new ArrayList<VatGoosInventoryRelationVO>();
            List<VatGoosInventoryRelationVO> newRelvos = getFilterGoods(relvos, blankvos, pk_corp);

            Long total = 0L;
            if(blankvos.size() == 0){
                total = newRelvos == null ? 0L : newRelvos.size();
            }

            grid.setTotal(total);
            grid.setRows(newRelvos);
            grid.setSuccess(true);
            grid.setMsg("获取存货对照关系成功");
        }catch(Exception e){
            printErrorLog(grid,e, "获取存货对照关系失败");
        }

        return ReturnData.ok().data(grid);
    }

    private List<VatGoosInventoryRelationVO> getFilterGoods(List<VatGoosInventoryRelationVO> relvos,
                                                            List<VatGoosInventoryRelationVO> blankvos,
                                                            String pk_corp){

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

    @RequestMapping("/combinePZ")
    public ReturnData combinePZ(@RequestBody Map<String,String> param){
        VatInvoiceSetVO setvo = queryRuleByType();
        ReturnData data = null;
        String lwstr = param.get("lwflag");
        String body = param.get("head");
        String goodData = param.get("goods");
        if(setvo == null
                || setvo.getValue() == null
                || setvo.getValue() == IBillManageConstants.HEBING_GZ_01){
            data = createPZ(lwstr,body,goodData);
        }else{
            data = combinePZ1(setvo,lwstr,body,goodData);
        }
        return data;
    }

    public ReturnData combinePZ1(VatInvoiceSetVO setvo,String lwstr,String body,String goodData){
        Json json = new Json();
        VATSaleInvoiceVO[] vos = null;
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        boolean lock = false;
        try {
            String userid  = SystemUtil.getLoginUserId();
            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("xiaoxiangcombinepz"+pk_corp);

            if(!lock){//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍候");
                return ReturnData.error().data(json);
            }


            DZFBoolean lwflag = "Y".equals(lwstr) ? DZFBoolean.TRUE : DZFBoolean.FALSE;
            if (body == null) {
                throw new BusinessException("数据为空,合并生成凭证失败!");
            }

            vos = JsonUtils.deserialize(body, VATSaleInvoiceVO[].class);

            if(vos == null || vos.length == 0)
                throw new BusinessException("数据为空，合并生成凭证失败，请检查");

            VatGoosInventoryRelationVO[] goods = getGoodsData(goodData);

//			VatInvoiceSetVO setvo = queryRuleByType();
            boolean accway = getAccWay(pk_corp);

            Map<String, DcModelHVO> dcmap = gl_yhdzdserv.queryDcModelVO(pk_corp);
            List<VATSaleInvoiceVO> storeList = gl_vatsalinvserv.constructVatSale(vos, pk_corp);

            if(storeList == null || storeList.size() == 0){
                throw new BusinessException("合并制证：查询销项发票失败，请检查");
            }

            goodsToVatBVO(storeList, goods, pk_corp, userid);

            Map<String, List<VATSaleInvoiceVO>> combineMap = new LinkedHashMap<String, List<VATSaleInvoiceVO>>();
            StringBuffer msg = new StringBuffer();
            String key;
            int errorCount = 0;
            List<String> periodSet = new ArrayList<String>();
            List<VATSaleInvoiceVO> combineList = null;
            String kplx = null;
            String period;
            for(VATSaleInvoiceVO vo : storeList){

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
                }else if(combineMap.containsKey(key)){
                    combineList = combineMap.get(key);

                    combineList.add(vo);
                }else{
                    combineList = new ArrayList<VATSaleInvoiceVO>();
                    combineList.add(vo);
                    combineMap.put(key, combineList);
                }
            }


            key = null;
            for(Map.Entry<String, List<VATSaleInvoiceVO>> entry : combineMap.entrySet()){
                try {
                    key = entry.getKey();
                    key = splitKey(key);
                    combineList = entry.getValue();
                    gl_vatsalinvserv.saveCombinePZ(combineList,
                            pk_corp, userid, dcmap, lwflag, setvo, accway, false);
                    msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if(e instanceof BusinessException
                            && !StringUtil.isEmpty(e.getMessage())
                            && !e.getMessage().startsWith("销项发票")
                            && !e.getMessage().startsWith("制单失败")){
                        try {
                            gl_vatsalinvserv.saveCombinePZ(combineList, pk_corp, userid, dcmap, lwflag, setvo, accway, true);
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
            StringBuffer headMsg = gl_yhdzdserv.buildQmjzMsg(periodSet, pk_corp);
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
        return ReturnData.ok().data(json);
    }

    private String splitKey(String key){

        return key.substring(0, 7);
    }

    private String buildkey(VATSaleInvoiceVO vo, VatInvoiceSetVO setvo){
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
//	private String buildkey(VATSaleInvoiceVO vo, Integer itype){
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
    public ReturnData chooseTicketWay(){
        Json json = new Json();
        json.setSuccess(false);

        try {

            CorpVO corpvo = gl_vatsalinvserv.chooseTicketWay(SystemUtil.getLoginCorpId());

            json.setHead(corpvo);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
        }

        return ReturnData.ok().data(json);
    }

    @RequestMapping("/setBusiType")
    public ReturnData setBusiType(@RequestBody Map<String,String> param){
        Json json = new Json();
        json.setSuccess(false);
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            String data = param.get("rows");
            String busiid = param.get("busiid");//历史主键
            String businame = param.get("businame");
            String selvalue = param.get("selvalue");

            if(StringUtil.isEmptyWithTrim(data)
                    || StringUtil.isEmptyWithTrim(selvalue)){
                throw new BusinessException("传入后台参数为空，请检查");
            }

            VATSaleInvoiceVO[] listvo = JsonUtils.deserialize(data, VATSaleInvoiceVO[].class);

            if(listvo == null || listvo.length == 0)
                throw new BusinessException("解析前台参数失败，请检查");

            String msg = gl_vatsalinvserv.saveBusiType(listvo, busiid, businame, selvalue, SystemUtil.getLoginUserId(), pk_corp);

            //重新查询
            String[] pks = buildPks(listvo);
            List<VATSaleInvoiceVO> newList = gl_vatsalinvserv.queryByPks(pks, pk_corp);

            json.setRows(newList);
            json.setMsg(msg);
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "更新业务类型失败");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "销项发票更新业务类型", ISysConstants.SYS_2);

        return ReturnData.ok().data(json);
    }

    @RequestMapping("/setBusiperiod")
    public ReturnData setBusiperiod(@RequestBody Map<String,String> param){
        Json json = new Json();
        json.setSuccess(false);
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
            String data = param.get("rows");
            String period = param.get("period");

            if(StringUtil.isEmptyWithTrim(data)
                    || StringUtil.isEmptyWithTrim(period)){
                throw new BusinessException("传入后台参数为空，请检查");
            }

            DZFDate date = DateUtils.getPeriodEndDate(period);
            if(date == null){
                throw new BusinessException("入账期间解析错误，请检查");
            }

            VATSaleInvoiceVO[] listvo = JsonUtils.deserialize(data, VATSaleInvoiceVO[].class);

            if(listvo == null || listvo.length == 0)
                throw new BusinessException("解析前台参数失败，请检查");
            String msg = gl_vatsalinvserv.saveBusiPeriod(listvo, pk_corp, period);

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

    private String[] buildPks(VATSaleInvoiceVO[] vos){
        String[] arr = new String[vos.length];

        for(int i = 0; i < vos.length; i++){
            arr[i] = vos[i].getPk_vatsaleinvoice();
        }

        return arr;
    }

    @RequestMapping("/checkBeforPZ")
    public ReturnData checkBeforPZ(@RequestBody Map<String,String> param){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        try {
            String str = param.get("row");

            VATSaleInvoiceVO[] listvo = JsonUtils.deserialize(str,VATSaleInvoiceVO[].class);

            if(listvo == null || listvo.length == 0)
                throw new BusinessException("解析前台参数失败，请检查");

            gl_vatsalinvserv.checkBeforeCombine(listvo);
            TzpzHVO headVO = constructCheckTzpzHVo(pk_corp, listvo[0]);
            gl_yhdzdserv.checkCreatePZ(pk_corp, headVO);

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
    private TzpzHVO constructCheckTzpzHVo(String pk_corp, VATSaleInvoiceVO vo){
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
    public ReturnData getTzpzHVOByID(@RequestBody Map<String,String> param){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        try {
//			if(StringUtil.isEmptyWithTrim(data.getPrimaryKey()))
//				throw new BusinessException("获取前台参数失败，请检查");

            String str = param.get("row");
            if (str == null) {
                throw new BusinessException("数据为空,请检查!");
            }

            VATSaleInvoiceVO[] listvo = JsonUtils.deserialize(str, VATSaleInvoiceVO[].class);

            if(listvo == null || listvo.length == 0){
                throw new BusinessException("转化后数据为空,请检查!");
            }
            boolean accway = getAccWay(pk_corp);
            VatInvoiceSetVO setvo = queryRuleByType();
            TzpzHVO hvo = gl_vatsalinvserv.getTzpzHVOByID(listvo,
                    pk_corp, SystemUtil.getLoginUserId(), setvo, accway);
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
    public void expExcelData(@RequestBody Map<String,String> param, HttpServletResponse response){
        String strrows = param.get("daterows");

        JSONArray array = JSON.parseArray(strrows);
        VATSaleInvoiceVO[] listvo = null;
        if(!StringUtils.isEmpty(strrows)){
            listvo = JsonUtils.deserialize(strrows, VATSaleInvoiceVO[].class);
        }
        OutputStream toClient = null;
        try {
            response.reset();
            String exName = new String("销项清单.xls");
            exName = new String(exName.getBytes("GB2312"), "ISO_8859_1");// 解决中文乱码问题
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(exName));
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            byte[] length = null;
            VatExportUtils exp = new VatExportUtils();
            Map<Integer, String> fieldColumn = getExpFieldMap();
            speTransValue(array);
            List<String> custNameList = gl_vatsalinvserv.getCustNames(SystemUtil.getLoginCorpId(), listvo);
            List<String> busiList = gl_vatsalinvserv.getBusiTypes(SystemUtil.getLoginCorpId());
            length = exp.exportExcel(fieldColumn, array, toClient,
                    "xiaoxiangqingdan.xls", 0, 1, 1, VatExportUtils.EXP_XX,
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
                !StringUtils.isEmpty(strrows)?"导出销项发票":"下载销项发票模板", ISysConstants.SYS_2);
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
        map.put(5, "bnum");
        map.put(6, "shjje");
        map.put(7, "se");
        map.put(8, "busitempname");
        map.put(9, "skhmc");
        //map.put(8, "kmbm");
        //map.put(9, "kmmc");

        return map;
    }

    @RequestMapping("/onTicket")
    public ReturnData onTicket(@RequestBody Map<String,String> param){
        Json json = new Json();

        VATSaleInvoiceVO paramvo = new VATSaleInvoiceVO();
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        boolean lock = false;
        try{
            paramvo.setKprj(new DZFDate(SystemUtil.getLoginDate()));//参数：当前登录时间
            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock(paramvo.getTableName()+pk_corp);
            if(!lock){//处理
                json.setSuccess(false);
                json.setMsg("正在处理中，请稍候");
                return ReturnData.error().data(json);
            }
            TicketNssbhVO nssbvo = gl_vatsalinvserv.getNssbvo(SystemUtil.getLoginCorpVo());//取当前公司

            Map<String, VATSaleInvoiceVO> repMap = null;

            //根据业务类型生成凭证
            StringBuffer msg = new StringBuffer();

            if(nssbvo == null){//规则  未认证走 账方通 ， 已认证走 票通取数
                String ccrecode = param.get("ccrecode");
                String f2 = param.get("f2");
                String period = param.get("period");
                DZFDate date = DateUtils.getPeriodEndDate(period);
                paramvo.setKprj(date);//参数：当前登录时间
                repMap = gl_vatsalinvserv.saveCft(pk_corp, SystemUtil.getLoginUserId(), ccrecode, f2, paramvo, msg);
            }else{
                repMap = gl_vatsalinvserv.saveKp(pk_corp,SystemUtil.getLoginUserId(), paramvo, nssbvo);
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
                    Map<String, VATSaleInvoiceVO> pzMap = new HashMap<String, VATSaleInvoiceVO>();
                    Map<String, VATSaleInvoiceVO> icMap = new HashMap<String, VATSaleInvoiceVO>();
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

    private void dealFirst(Map<String, VATSaleInvoiceVO> repMap,
                           Map<String, VATSaleInvoiceVO> pzMap,
                           Map<String, VATSaleInvoiceVO> icMap){
        String busiName;
        String key;
        VATSaleInvoiceVO vo;
        for(Map.Entry<String, VATSaleInvoiceVO> entry : repMap.entrySet()){
            key = entry.getKey();
            vo = entry.getValue();
            busiName = vo.getBusitypetempname();
            if(!StringUtil.isEmpty(busiName)
                    && busiName.contains("劳务收入")){
                pzMap.put(key, vo);
            }else{
                icMap.put(key, vo);
            }
        }
    }

    private int dealAfterTicketByPZ(Map<String, VATSaleInvoiceVO> map, StringBuffer msg){
        List<VATSaleInvoiceVO> list = buildMap2List(map);
        int errorCount = 0;
        String pk_corp = SystemUtil.getLoginCorpId();
        String userid = SystemUtil.getLoginUserId();

        Map<String, DcModelHVO> dcmap = gl_yhdzdserv.queryDcModelVO(pk_corp);
        YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);

        if(list == null || list.size() == 0){
            return errorCount;
        }

        String kplx = null;
        String key = null;

        Map<String, List<VATSaleInvoiceVO>> combineMap = new LinkedHashMap<String, List<VATSaleInvoiceVO>>();
        List<VATSaleInvoiceVO> combineList = null;
        DcModelHVO modelHVO = null;
        VatInvoiceSetVO setvo = queryRuleByType();
        boolean accway = getAccWay(pk_corp);
        for(VATSaleInvoiceVO vo : list){

            modelHVO = dcmap.get(vo.getPk_model_h());
            if(modelHVO == null){
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
                combineList = new ArrayList<VATSaleInvoiceVO>();
                combineList.add(vo);
                combineMap.put(key, combineList);
            }
        }

        key = null;
        for(Map.Entry<String, List<VATSaleInvoiceVO>> entry : combineMap.entrySet()){
            try {
                key = entry.getKey();
                key = splitKey(key);
                combineList = entry.getValue();

                gl_vatsalinvserv.saveCombinePZ(combineList,
                        pk_corp, userid, dcmap, null, setvo, accway, false);
                msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if(e instanceof BusinessException
                        && !StringUtil.isEmpty(e.getMessage())
                        && !e.getMessage().startsWith("销项发票")
                        && !e.getMessage().startsWith("制单失败")){
                    try {
                        gl_vatsalinvserv.saveCombinePZ(combineList, pk_corp, userid,
                                dcmap, null, setvo, accway, true);
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

    private int dealAfterTicketByIC(Map<String, VATSaleInvoiceVO> map, StringBuffer msg){
        List<VATSaleInvoiceVO> list = buildMap2List(map);
        int errorCount = 0;
        String pk_corp = SystemUtil.getLoginCorpId();
        String userid = SystemUtil.getLoginUserId();

        Map<String, DcModelHVO> dcmap = gl_yhdzdserv.queryDcModelVO(pk_corp);
        YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);

        if(list == null || list.size() == 0){
            return errorCount;
        }

        String kplx = null;

        Map<String, List<VATSaleInvoiceVO>> combineMap = new LinkedHashMap<String, List<VATSaleInvoiceVO>>();
        VatInvoiceSetVO setvo = queryRuleByType();
        List<VATSaleInvoiceVO> combineList = null;
        DcModelHVO modelHVO = null;
        DcModelBVO[] modelbvos = null;
        DcModelBVO modelbvo = null;
        String key = null;
        List<VATSaleInvoiceVO> errorList = new ArrayList<VATSaleInvoiceVO>();
        for(VATSaleInvoiceVO vo : list){

            modelHVO = dcmap.get(vo.getPk_model_h());
            if(modelHVO == null){
                errorCount++;
                msg.append("<font color='red'><p>销项发票[" + vo.getFp_hm() + "," + vo.getFp_hm() + "]业务类型未找到，请选择相应业务类型</p></font>");
                continue;
            }

            modelbvos = modelHVO.getChildren();
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
            }

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
                combineList = new ArrayList<VATSaleInvoiceVO>();
                combineList.add(vo);
                combineMap.put(key, combineList);
            }
        }

        key = null;

        IntradeHVO ihvo = null;
        List<IntradeHVO> ihvoList = null;

        for(Map.Entry<String, List<VATSaleInvoiceVO>> entry : combineMap.entrySet()){
            try {
                combineList = entry.getValue();

                ihvoList = new ArrayList<IntradeHVO>();
                for(VATSaleInvoiceVO vo : combineList){
                    try {
                        ihvo = gl_vatsalinvserv.createIC(vo, accounts, SystemUtil.getLoginCorpVo(), userid);
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
                if(ihvoList.size() > 0){
                    gl_vatsalinvserv.saveTotalGL(
                            ihvoList.toArray(new IntradeHVO[0]), pk_corp, userid);
                }

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
            VATSaleInvoiceVO ervo = null;
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

    private List<VATSaleInvoiceVO> buildMap2List(Map<String, VATSaleInvoiceVO> map){
        List<VATSaleInvoiceVO> list = new ArrayList<VATSaleInvoiceVO>();
        VATSaleInvoiceVO vo = null;

        for(Map.Entry<String, VATSaleInvoiceVO> entry : map.entrySet()){
            vo  = entry.getValue();

            list.add(vo);
        }

        return list;

    }

    @RequestMapping("/queryTaxItems")
    public ReturnData queryTaxItems(){
        Grid grid = new Grid();
        try{

            List<TaxitemVO> vos = gl_vatsalinvserv.queryTaxItems(SystemUtil.getLoginCorpId());

            grid.setRows(vos);
            grid.setSuccess(true);
            grid.setMsg("获取税目信息成功");
        }catch(Exception e){
            printErrorLog(grid, e, "获取税目信息失败");
        }

        return ReturnData.ok().data(grid);
    }

    @RequestMapping("/queryRule")
    public ReturnData queryRule(){
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
//				"销项发票更新业务类型", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @RequestMapping("/combineRule")
    public ReturnData combineRule(@RequestBody Map<String,String> param){
        Json json = new Json();
        try {
            String pzrule = param.get("pzrule");
            String flrule = param.get("flrule");
            String zy = param.get("zy");
            String setId = param.get("setid");
            String bk = param.get("bk");

            if(StringUtil.isEmpty(pzrule)
                    || StringUtil.isEmpty(flrule)){
                throw new BusinessException("设置失败，请重试");
            }

            String pk_corp = SystemUtil.getLoginCorpId();
            VatInvoiceSetVO vo = new VatInvoiceSetVO();
            String[] fields = new String[]{ "value", "entry_type", "isbank", "zy" };
            if(!StringUtil.isEmpty(setId)){
                vo.setPrimaryKey(setId);
            }else{
                vo.setPk_corp(pk_corp);
                vo.setStyle(IBillManageConstants.HEBING_XXFP);
            }
            vo.setValue(Integer.parseInt(pzrule));
            vo.setEntry_type(Integer.parseInt(flrule));
            vo.setZy(zy);

            if(StringUtil.isEmpty(bk)){
                vo.setIsbank(null);
            }else{
                vo.setIsbank(DZFBoolean.TRUE);
            }

            vatinvoiceserv.updateVO(pk_corp, vo, fields);
            json.setMsg("设置成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "设置失败");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL,
                "合并规则调整", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    /**
     * 生成出库
     */
    @RequestMapping("/createIC")
    public ReturnData createIC(@RequestBody Map<String,String> param){
        Json json = new Json();
        VATSaleInvoiceVO[] vos = null;
        boolean lock = false;
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
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
            String goodData = param.get("goods");
            String body = param.get("head");
            if (body == null) {
                throw new BusinessException("数据为空,生成出库单失败!");
            }

            vos = JsonUtils.deserialize(body, VATSaleInvoiceVO[].class);

            if(vos == null || vos.length == 0)
                throw new BusinessException("数据为空,生成出库单失败，请检查");

            List<VATSaleInvoiceVO> stoList = gl_vatsalinvserv.constructVatSale(vos, pk_corp);

            if(stoList == null
                    || stoList.size() == 0)
                throw new BusinessException("未找销项发票数据，请检查");

            VatGoosInventoryRelationVO[] goods = getGoodsData(goodData);
            goodsToVatBVO(stoList, goods, pk_corp, userid);

            YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);
            IntradeHVO ihvo = null;
            int errorCount = 0;
            StringBuffer msg = new StringBuffer();
            List<String> periodSet= new ArrayList<String>();
            String kplx = null;
            for(VATSaleInvoiceVO vo : stoList){
                try {
                    kplx = vo.getKplx();
                    if(!StringUtil.isEmpty(kplx)
                            && (ICaiFangTongConstant.FPLX_3.equals(kplx)//空白废票
                            || ICaiFangTongConstant.FPLX_4.equals(kplx)//正废
                            || ICaiFangTongConstant.FPLX_5.equals(kplx))){//负废
                        errorCount++;
                        msg.append("<font color='red'><p>销项发票清单[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据是废票，不能生成出库单。</p></font>");
                    }else if(StringUtil.isEmpty(vo.getPk_tzpz_h())){
                        ihvo = gl_vatsalinvserv.createIC(vo, accounts, corpvo, userid);
                        gl_vatsalinvserv.saveGL(ihvo, pk_corp, userid);
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
            StringBuffer headMsg = gl_yhdzdserv.buildQmjzMsg(periodSet, pk_corp);
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
    @RequestMapping("/matchInventoryData")
    public ReturnData matchInventoryData(@RequestBody Map<String,String> param) {
        Grid grid = new Grid();
        try {
            String goodData = param.get("goods");
            String body = param.get("head");
            if (body == null) {
                throw new BusinessException("数据为空,存货匹配失败!");
            }

            VATSaleInvoiceVO[] vos = JsonUtils.deserialize(body, VATSaleInvoiceVO[].class);

            if (vos == null || vos.length == 0)
                throw new BusinessException("数据为空,存货匹配失败，请检查");
            StringBuffer msg = new StringBuffer();
            String kplx = null;

            List<VATSaleInvoiceVO> list = new ArrayList<>();
            for (VATSaleInvoiceVO vo : vos) {
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
            String isshow = param.get("ishow");
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
                String error = inventory_setcheck.checkInventorySet(SystemUtil.getLoginUserId(), SystemUtil.getLoginCorpId(),invsetvo);
                if (!StringUtil.isEmpty(error)) {
                    error = error.replaceAll("<br>", " ");
                    throw new BusinessException("销项发票存货匹配失败:"+error);
                }

                String pk_corp = SystemUtil.getLoginCorpId();
                List<InventoryAliasVO> relvos = gl_vatsalinvserv.matchInventoryData(pk_corp, vos,invsetvo);

                if(relvos != null && relvos.size()>0){

                    InventoryAliasVO[] goods = getInvAliasData(goodData);
                    String[] keys = new String[]{"aliasname","spec","invtype","unit"};
                    Map<String,InventoryAliasVO> map = null;
                    if (!DZFValueCheck.isEmpty(goods)){
                        map =DZfcommonTools.hashlizeObjectByPk(Arrays.asList(goods), keys);
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
                    throw new BusinessException("销项发票明细数据不存在!");
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
    public ReturnData saveInventoryData(@RequestBody Map<String,String> param) {
        Grid grid = new Grid();
        boolean lock = false;
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        String goodData = param.get("goods");
        try {
            // 加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("xiaoxiangpp"+pk_corp);

            if (!lock) {// 处理
                grid.setSuccess(false);
                grid.setMsg("正在处理中，请稍候");
                return ReturnData.error().data(grid);
            }

            InventoryAliasVO[] goods = getInvAliasData(goodData);
            if (goods == null || goods.length == 0)
                throw new BusinessException("未找到存货别名数据，请检查");
            List<Grid> logList = new ArrayList<Grid>();//记录更新日志
            gl_vatsalinvserv.saveInventoryData(pk_corp, goods, logList);
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
                redissonDistributedLock.releaseDistributedFairLock("xiaoxiangpp"+pk_corp);
            }
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_PJGL, "销项发票匹配存货", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    @RequestMapping("/createPzData")
    public ReturnData createPzData(@RequestBody Map<String,String> param) {
        Grid grid = new Grid();
        boolean lock = false;
        String pk_corp = SystemUtil.getLoginCorpId();
        checkSecurityData(null,new String[]{pk_corp}, null);
        String sourceName = StringUtil.isEmpty(param.get("sourcename"))?"填制凭证_":param.get("sourcename")+"_";
        try {
            String body = param.get("head");
            //加锁
            lock = redissonDistributedLock.tryGetDistributedFairLock("xiaoxiangpz"+pk_corp);
            if(!lock){//处理
                grid.setSuccess(false);
                grid.setMsg("正在处理中，请稍候");
                return ReturnData.error().data(grid);
            }
            if (body == null) {
                throw new BusinessException("数据为空,生成凭证失败!");
            }

            VATSaleInvoiceVO[] vos = JsonUtils.deserialize(body, VATSaleInvoiceVO[].class);

            if (vos == null || vos.length == 0)
                throw new BusinessException("数据为空,生成凭证失败，请检查");

            List<VATSaleInvoiceVO> stoList = gl_vatsalinvserv.constructVatSale(vos, pk_corp);

            if (stoList == null || stoList.size() == 0)
                throw new BusinessException("未找销项发票数据，请检查");
            String goodData = param.get("goods");
            String jsfs = param.get("jsfs");
            String inperiod = param.get("inperiod");

            InventorySetVO invsetvo = query();
            Map<String, YntCpaccountVO> ccountMap = accountService.queryMapByPk(pk_corp);
            if (invsetvo == null)
                throw new BusinessException("存货设置未设置!");
            int chcbjzfs = invsetvo.getChcbjzfs();

            String error = inventory_setcheck.checkInventorySet(SystemUtil.getLoginUserId(), SystemUtil.getLoginCorpId(),invsetvo);
            if (!StringUtil.isEmpty(error)) {
                error = error.replaceAll("<br>", " ");
                throw new BusinessException("销项发票生成凭证失败:"+error);
            }

            if (chcbjzfs == 2) {// 不核算明细
                YntCpaccountVO cvo = getRkkmVO(invsetvo, ccountMap);
                if (cvo.getIsfzhs().charAt(5) == '1') {
                    throw new BusinessException("科目【" + cvo.getAccountname() + "】已启用存货辅助!");
                }
            }

            if (chcbjzfs != 2) {
                InventoryAliasVO[] goods = getInvAliasData(goodData);
                if(goods != null &&goods.length> 0){
                    List<InventoryAliasVO> list = new ArrayList<>();
                    for(InventoryAliasVO  good:goods){
                        if(good.getIsMatch() == null || !good.getIsMatch().booleanValue())
                            list.add(good);
                    }
                    List<Grid> logList = new ArrayList<Grid>();//记录更新日志
                    if(list != null && list.size()>0){
                        gl_vatsalinvserv.saveInventoryData(pk_corp, list.toArray(new InventoryAliasVO[list.size()]), logList);
                    }
                    //保存操作日志
                    for(Grid loggrid: logList){
                        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, sourceName+loggrid.getMsg(), ISysConstants.SYS_2);
                    }
                }
            }

            if(!StringUtil.isEmpty(inperiod)){
                for (VATSaleInvoiceVO vo : stoList) {
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
                redissonDistributedLock.releaseDistributedFairLock("xiaoxiangpz"+pk_corp);
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
        vos = JsonUtils.deserialize(goods, InventoryAliasVO[].class);

        return vos;
    }


    private  List<Object>  combinePZData(List<VATSaleInvoiceVO> stoList, InventorySetVO invsetvo, String jsfs) {
        VatInvoiceSetVO setvo = queryRuleByType();
        String numStr = parameterserv.queryParamterValueByCode(SystemUtil.getLoginCorpId(), IParameterConstants.DZF009);
        int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
        int chcbjzfs = invsetvo.getChcbjzfs();
        if (chcbjzfs != 2) {
            List<InventoryAliasVO> alist = gl_vatsalinvserv.matchInventoryData(SystemUtil.getLoginCorpId(),
                    stoList.toArray(new VATSaleInvoiceVO[stoList.size()]),invsetvo);

            if (alist == null || alist.size() == 0)
                throw new BusinessException("存货匹配信息出错");
            int pprule = invsetvo.getChppjscgz();//匹配规则
            for (VATSaleInvoiceVO incomvo : stoList) {

                SuperVO[] ibodyvos = (SuperVO[]) incomvo.getChildren();
                if (ibodyvos == null || ibodyvos.length == 0)
                    continue;
                for (SuperVO body : ibodyvos) {
                    VATSaleInvoiceBVO ibody = (VATSaleInvoiceBVO) body;
                    String key1 =buildByRule( ibody.getBspmc(), ibody.getInvspec(), ibody.getMeasurename(), pprule);
                    for (InventoryAliasVO aliavo : alist) {
                        String key =buildByRule( aliavo.getAliasname(), aliavo.getSpec(), aliavo.getUnit(), pprule);
                        if (key1.equals(key)) {
                            ibody.setPk_inventory(aliavo.getPk_inventory());
                            ibody.setPk_accsubj(aliavo.getChukukmid());
                            // 根据换算方式 计算数量
                            if(aliavo.getCalcmode()==0){
                                ibody.setBnum(SafeCompute.multiply(ibody.getBnum(), aliavo.getHsl()).setScale(num,  DZFDouble.ROUND_HALF_UP));
                            }else if(aliavo.getCalcmode()==1){
                                ibody.setBnum(SafeCompute.div(ibody.getBnum(), aliavo.getHsl()).setScale(num,  DZFDouble.ROUND_HALF_UP));
                            }
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
    private List<Object> createPZData(List<VATSaleInvoiceVO> stoList, VatInvoiceSetVO setvo, InventorySetVO invsetvo, String jsfs) {
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
        for (VATSaleInvoiceVO vo : stoList) {
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
                    gl_vatsalinvserv.createPZ(vo, pk_corp, userid, accway, false, setvo, invsetvo, jsfs);
                    msg.append("<font color='#2ab30f'><p>销项发票[" + vo.getFp_dm() + "_" + vo.getFp_hm() + "]的单据生成凭证成功。</p></font>");
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

        StringBuffer headMsg = gl_yhdzdserv.buildQmjzMsg(periodSet, pk_corp);
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
    private List<Object> combinePZData1(List<VATSaleInvoiceVO> stoList, VatInvoiceSetVO setvo, InventorySetVO invsetvo,
                                        String jsfs) {
        String pk_corp = SystemUtil.getLoginCorpId();

        String userid = SystemUtil.getLoginUserId();

        boolean accway = getAccWay(pk_corp);
        Map<String, List<VATSaleInvoiceVO>> combineMap = new LinkedHashMap<String, List<VATSaleInvoiceVO>>();
        StringBuffer msg = new StringBuffer();
        String key;
        List<VATSaleInvoiceVO> combineList = null;
        String kplx = null;
        String period;
        List<String> periodSet = new ArrayList<String>();
        List<Object> list = new ArrayList<>();
        int err=0;
        for (VATSaleInvoiceVO vo : stoList) {

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
                combineList = new ArrayList<VATSaleInvoiceVO>();
                combineList.add(vo);
                combineMap.put(key, combineList);
            }
        }

        key = null;
        for (Map.Entry<String, List<VATSaleInvoiceVO>> entry : combineMap.entrySet()) {
            try {
                key = entry.getKey();
                key = splitKey(key);
                combineList = entry.getValue();
                gl_vatsalinvserv.saveCombinePZ(combineList, pk_corp, userid, setvo, accway, false, invsetvo, jsfs);
                msg.append("<font color='#2ab30f'><p>入账期间为" + key + "的单据生成凭证成功。</p></font>");
            } catch (Exception e) {
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

        StringBuffer headMsg = gl_yhdzdserv.buildQmjzMsg(periodSet, pk_corp);
        if(headMsg != null && headMsg.length() > 0){
            headMsg.append(msg.toString());
            msg = headMsg;
        }

        list.add(err);
        list.add(msg.toString());
        return list;
    }

    @RequestMapping("/getBusiType")
    public ReturnData getBusiType(){
        Json json = new Json();
        json.setSuccess(false);

        try {
            List<VatBusinessTypeVO> list = gl_vatsalinvserv.getBusiType(SystemUtil.getLoginCorpId());

            json.setRows(list);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
        }

        return ReturnData.ok().data(json);
    }

}
