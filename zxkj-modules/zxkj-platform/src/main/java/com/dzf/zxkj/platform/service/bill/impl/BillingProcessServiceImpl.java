package com.dzf.zxkj.platform.service.bill.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.enums.MsgtypeEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bill.BillApplyDetailVo;
import com.dzf.zxkj.platform.model.bill.BillApplyVO;
import com.dzf.zxkj.platform.model.bill.BillHistoryVO;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.message.MsgAdminVO;
import com.dzf.zxkj.platform.model.piaotong.PiaoTongResVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bill.IBillingProcessService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.zncs.CommonXml;
import com.dzf.zxkj.platform.util.zncs.ICaiFangTongConstant;
import com.dzf.zxkj.platform.util.zncs.OcrUtil;
import com.dzf.zxkj.platform.util.zncs.PiaoTongUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service("gl_kpclserv")
public class BillingProcessServiceImpl implements IBillingProcessService {

    //	public final static Integer STATUS_APPLY = 0;
    public final static Integer STATUS_BILLING = 1;
    public final static Integer STATUS_SENTOUT = 2;
    public final static Integer STATUS_ACCOUNTING = 3;
    public final static Integer STATUS_TAX = 4;

    private static final String suff_xls = "xls";
    private static final String suff_xlsx = "xlsx";

    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private IUserService userServiceImpl;
    @Autowired
    private IAuxiliaryAccountService gl_fzhsserv;
    @Autowired
    private IInventoryAccSetService gl_ic_invtorysetserv = null;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @Override
    public List<BillApplyVO> query(String userid, String pk_corp,
                                   BillApplyVO param) throws DZFWarpException {
        Integer status = param.getIbillstatus();
        String customer = param.getVcompanyname();
        String fpdm = param.getFpdm();
        String fphm = param.getFphm();

        SQLParameter sp = new SQLParameter();
        StringBuffer sql = new StringBuffer();
        sql.append(" select bill.*, s.user_code as vapplycode, ")
//		.append(" customer.vcompanyname, customer.vcompanytype, ")
//		.append(" customer.vtaxcode, customer.vcompanyaddr, ")
//		.append(" customer.vphone, customer.vbank, customer.vbankcode  ")
                .append(" fzkh.name as vcompanyname,   ")
                .append(" fzkh.credit_code as vtaxcode,  fzkh.address as vcompanyaddr, ")
                .append(" fzkh.phone_num as vphone, fzkh.bank as vbank, fzkh.account_num  as vbankcode  ")
                .append(" from ynt_app_billapply bill ")
//		.append(" left join ynt_app_customer customer on bill.pk_app_customer = customer.pk_app_customer ")
                .append(" left join YNT_FZHS_B fzkh on bill.pk_app_customer = fzkh.pk_auacount_b and  fzkh.pk_auacount_h = '000001000000000000000001'  ")
                .append(" left join sm_user s on bill.vapplytor = s.cuserid  ")
                .append(" where nvl(bill.dr, 0)=0 ");
        if(StringUtil.isEmpty(param.getPk_app_billapply())) {
            sql.append(" and bill.pk_corp = ? ");
            sp.addParam(param.getPk_corp());
            if(status != null) {
                sql.append(" and ibillstatus = ? ");
                sp.addParam(status);
            }
            DZFDate begindate = param.getBegindate();
            DZFDate enddate   = param.getEnddate();
            if(begindate != null){
                sql.append(" and bill.dapplydate >= ? ");
                sp.addParam(begindate);
            }
            if(enddate != null){
//                enddate = DateUtils.getPeriodEndDate(DateUtils.getPeriod(enddate)).getDateAfter(1);
                sql.append(" and bill.dapplydate <= ? ");
                sp.addParam(enddate);
            }
            if(!StringUtil.isEmpty(fpdm)){
                sql.append(" and bill.fpdm like ? ");
                sp.addParam("%" + fpdm + "%");
            }
            if(!StringUtil.isEmpty(fphm)){
                sql.append(" and bill.fphm like ? ");
                sp.addParam("%" + fphm + "%");
            }
            if(!StringUtil.isEmpty(customer)) {
                sql.append(" and customer.vcompanyname like ? ");
                sp.addParam("%" + customer + "%");
            }
            sql.append(" order by  bill.dapplydate desc ");
        }else {
            sql.append(" and bill.pk_app_billapply = ? ");
            sp.addParam(param.getPk_app_billapply());
        }

        List<BillApplyVO> rs = (List<BillApplyVO>) singleObjectBO.executeQuery(
                sql.toString(), sp, new BeanListProcessor(BillApplyVO.class));
        if (rs.size() > 0) {
            dealUserName(rs);
        }
        return rs;
    }

    private CorpVO getCorpVO(String pk_corp) {
        CorpVO corp = zxkjPlatformService.queryCorpByPk(pk_corp);

        return corp;
    }

    @Override
    public List<BillApplyDetailVo> queryB(String pk_apply, String pk_corp) throws DZFWarpException {

        if (StringUtil.isEmpty(pk_apply)) {
            throw new BusinessException("ID信息为空!");
        }
        CorpVO cpvo =  getCorpVO(pk_corp);

        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_apply);
        sp.addParam(pk_corp);
        StringBuffer qrysql  = new StringBuffer();

        if(IcCostStyle.IC_ON.equals(cpvo.getBbuildic())){//启用存货
            qrysql.append(" select b.*,   ");
            qrysql.append(" c.name as spmc,c.invspec as ggxh, ");
            qrysql.append(" d.name as jldw,c.taxratio as sl ");
            qrysql.append(" from  "+ BillApplyDetailVo.TABLE_NAME +" b ");
            qrysql.append(" left join  ynt_inventory c on b.pk_inventory = c.pk_inventory ");
            qrysql.append(" left join ynt_measure d on c.pk_measure = d.pk_measure ");
            qrysql.append(" where nvl(b.dr,0)=0 and nvl(c.dr,0)=0  ");
            qrysql.append(" and  b.pk_app_billapply = ?  and b.pk_corp  = ? ");
        }else{
            qrysql.append(" select b.*, ");
            qrysql.append(" c.name as spmc,c.spec as ggxh,   ");
            qrysql.append(" c.unit as jldw,c.taxratio as sl  ");
            qrysql.append(" from  "+ BillApplyDetailVo.TABLE_NAME +" b ");
            qrysql.append(" left join ynt_fzhs_b c on b.pk_app_commodity = c.pk_auacount_b  and c.pk_auacount_h = '000001000000000000000006' ");
            qrysql.append(" where nvl(b.dr,0)=0 and nvl(c.dr,0)=0  ");
            qrysql.append(" and  b.pk_app_billapply = ?  and b.pk_corp  = ? ");
        }
        List<BillApplyDetailVo> vos = (List<BillApplyDetailVo>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(BillApplyDetailVo.class));


        return vos;
    }

    private boolean isExcel(String fileType) throws DZFWarpException {
        boolean result = false;
        if (suff_xls.equals(fileType) || suff_xlsx.equals(fileType)) {
            result = true;
        }

        return result;
    }

    @Override
    public void saveImp(InputStream is, BillApplyVO paramvo, String pk_corp, String fileType,
                        String userid, StringBuffer msg) throws DZFWarpException {

        List<BillApplyVO> list = null;
        if(isExcel(fileType)){
            list = importExcel(is, fileType, pk_corp, userid, msg);
        }else{
            throw new BusinessException("上传文件格式不符合规范，请检查");
        }

        if (list == null || list.size() == 0) {
            String frag = "<p>导入文件数据为空，请检查。</p>";

            if (msg.length() == 0) {
                msg.append(frag);
            }
            throw new BusinessException(msg.toString());
        }

        updateArr(pk_corp, list);
    }

    private void updateArr(String pk_corp, List<BillApplyVO> list){
        BillApplyVO[] addvos = list.toArray(new BillApplyVO[0]);

        String[] addpks = singleObjectBO.insertVOArr(pk_corp, addvos);

        SuperVO[] bvos;
        List<SuperVO> bvoList = new ArrayList<SuperVO>();
        for (int i = 0; i < addvos.length; i++) {
            bvos = addvos[i].getChildren();
            if (bvos != null && bvos.length > 0) {
                for (SuperVO bvo : bvos) {
                    bvo.setAttributeValue(bvo.getParentPKFieldName(), addpks[i]);
                    bvoList.add(bvo);
                }
            }
        }

        if (bvoList.size() > 0) {
            singleObjectBO.insertVOArr(pk_corp, bvoList.toArray(new BillApplyDetailVo[0]));
        }
    }

    private List<BillApplyVO> importExcel(InputStream is, String fileType, String pk_corp, String userid,
                                          StringBuffer msg) throws DZFWarpException {
        try {
            Workbook impBook = null;
            try {
                if (suff_xls.equals(fileType)) {
                    impBook = new HSSFWorkbook(is);
                } else if (suff_xlsx.equals(fileType)) {
                    impBook = new XSSFWorkbook(is);
                } else {
                    throw new BusinessException("不支持的文件格式");
                }
            } catch (Exception e) {
                log.error("错误",e);
                if (e instanceof BusinessException) {
                    throw new BusinessException(e.getMessage());
                } else {
//                    is = new FileInputStream(file);
                    impBook = new XSSFWorkbook(is);
                }
            }

            int sheetno = impBook.getNumberOfSheets();
            if (sheetno == 0) {
                throw new Exception("需要导入的数据为空。");
            }
            Sheet sheet1 = impBook.getSheetAt(0);
            List<BillApplyVO> list = getDataByExcel(sheet1, pk_corp, userid, fileType, msg);
            return list;
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("导入文件未找到");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("导入文件格式错误");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof BusinessException)
                throw new BusinessException(e.getMessage());
            throw new BusinessException("导入文件格式错误");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private List<BillApplyVO> getDataByExcel(Sheet sheet, String pk_corp, String userid, String fileType,
                                             StringBuffer msg) throws DZFWarpException {
        List<BillApplyVO> blist = new ArrayList<BillApplyVO>();
        CorpVO corpVO = getCorpVO(pk_corp);

        int iBegin = 1;
        Cell aCell = null;
        String sTmp = "";
        BillApplyVO excelvo = null;

        Object[][] STYLE_1 = getStyleByExcel();

        int count;// 计数器的作用判断该行是不是空行，如count == STYLE_1.length 则为空行
        boolean isNullFlag;
        StringBuffer innermsg = new StringBuffer();
        for (; iBegin < (sheet.getLastRowNum() + 1); iBegin++) {

//			if (iBegin == sheet.getLastRowNum())
//				throw new BusinessException("导入失败,导入文件抬头格式不正确 !");

            excelvo = new BillApplyVO();
            count = 0;
            isNullFlag = false;
            for (int j = 0; j < STYLE_1.length; j++) {
                if (sheet.getRow(iBegin) == null) {
                    isNullFlag = true;
                    break;
                }

                aCell = sheet.getRow(iBegin).getCell((new Integer(STYLE_1[j][0].toString())).intValue());
                sTmp = getExcelCellValue(aCell);

                if (sTmp != null && !StringUtil.isEmpty(sTmp.trim())) {
                    //个性化处理
                    if("bsl".equals(STYLE_1[j][2])){
                        specialSL(sTmp, excelvo, STYLE_1[j][2].toString());
                    }else{
                        excelvo.setAttributeValue(STYLE_1[j][2].toString(), sTmp.trim());// sTmp.replace("
                    }

                } else {
                    count++;
                }
            }

            if (count != STYLE_1.length && !isNullFlag) {
                innermsg.setLength(0);

                //做下特殊判断 如果一行中只有 含税金额有值，认为是空行
                if(count == STYLE_1.length -1
                        && (excelvo.getBnmny() != null && excelvo.getBnmny().doubleValue() == 0)){
                    break;
                }

                checkDataValid(excelvo, innermsg, iBegin, corpVO);

                if (innermsg.length() != 0) {
                    msg.append(innermsg);
                    continue;
                }

                setDefaultValue(pk_corp, userid, excelvo, iBegin);
                blist.add(excelvo);

            }else{
                //空行 直接返回
                break;
            }

        }

        //后续组装
        blist = specialBVO(blist, pk_corp, userid, corpVO);

        return blist;
    }

    private void specialSL(String sTmp, BillApplyVO excelvo, String key){
        if(IInvoiceApplyConstant.SL_MIANZHENG.equals(sTmp)){
            excelvo.setZerotaxflag(IInvoiceApplyConstant.ZERO_SL_1);
            excelvo.setLslyhzcbs(1);//优惠政策标识
            excelvo.setVatspeman(IInvoiceApplyConstant.SL_MIANZHENG);

            excelvo.setAttributeValue(key, DZFDouble.ZERO_DBL);
        }else if(IInvoiceApplyConstant.SL_BUZHENGSHUI.equals(sTmp)){
            excelvo.setZerotaxflag(IInvoiceApplyConstant.ZERO_SL_2);
            excelvo.setLslyhzcbs(1);//优惠政策标识
            excelvo.setVatspeman(IInvoiceApplyConstant.SL_BUZHENGSHUI);

            excelvo.setAttributeValue(key, DZFDouble.ZERO_DBL);
        }else if(sTmp.endsWith("%")){
            sTmp = sTmp.substring(0, sTmp.length() - 1);
            DZFDouble sl = SafeCompute.div(new DZFDouble(sTmp), new DZFDouble(100));

            if(sl.doubleValue() == 0){
                excelvo.setZerotaxflag(IInvoiceApplyConstant.ZERO_SL_3);
            }

            excelvo.setAttributeValue(key, sl.toString());
        }

    }

    private void fzhsAdapter(List<BillApplyVO> blist, String pk_corp, CorpVO corpvo){
        //客户
        AuxiliaryAccountBVO[] customervos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_CUSTOMER,
                pk_corp, null);
        if(customervos == null || customervos.length == 0)
            throw new BusinessException("客户档案为空, 请检查");
        List<AuxiliaryAccountBVO> customerList = Arrays.asList(customervos);
        Map<String, AuxiliaryAccountBVO> customerMap = DZfcommonTools.hashlizeObjectByPk(customerList, new String[]{ "name" });
        setCustomerFzhsValue(blist, customerMap, new String[]{ "vcompanyname" });

        //先查询
        Map<String, AuxiliaryAccountBVO> invenMap = new LinkedHashMap<>();
        Map<String, InventoryAliasVO> alisInvenMap = new LinkedHashMap<>();

        AuxiliaryAccountBVO[] invenvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_INVENTORY, pk_corp, null);
        if(invenvos == null || invenvos.length == 0)
            throw new BusinessException("存货档案为空, 请检查");

        String stype = corpvo.getBbuildic();//模式
        if(IcCostStyle.IC_INVTENTORY.equals(stype)){//启用总账存货
            InventorySetVO invsetvo = gl_ic_invtorysetserv.query(pk_corp);
            if(invsetvo == null){
                throw new BusinessException("启用总账核算存货，请先设置存货成本核算方式!");
            }

            List<AuxiliaryAccountBVO> invenList = Arrays.asList(invenvos);

            List<InventoryAliasVO> alisList = queryAlisInven(pk_corp);

            int pprule = invsetvo.getChppjscgz();//匹配规则

            if(pprule == InventoryConstant.IC_RULE_1 ){//存货名称+计量单位
                String[] keys =  new String[] { "name", "unit" };
                invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, keys);

                alisInvenMap = buildAlistMap(alisList, new String[]{ "aliasname", "unit" });

                setInventoryFzhsValue(blist, invenMap, alisInvenMap, false, new String[]{ "bspmc", "jldw" });
            }else{
                String[] keys =  new String[] { "name", "spec", "unit" };
                invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, keys);

                alisInvenMap = buildAlistMap(alisList,  new String[]{ "aliasname", "spec", "unit" });

                setInventoryFzhsValue(blist, invenMap, alisInvenMap, false, new String[]{ "bspmc", "ggxh", "jldw" });
            }

        }else if(IcCostStyle.IC_ON.equals(stype)){
            List<AuxiliaryAccountBVO> invenList = Arrays.asList(invenvos);
            List<InventoryAliasVO> alisList = queryAlisInven(pk_corp);
            String[] keys =  new String[] { "name", "spec", "unit" };

            invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, keys);
            alisInvenMap = buildAlistMap(alisList, keys);

            setInventoryFzhsValue(blist, invenMap, alisInvenMap, true, new String[]{ "bspmc", "ggxh", "jldw" });
        }else{

            List<AuxiliaryAccountBVO> invenList = Arrays.asList(invenvos);
            String[] keys =  new String[] { "name", "spec", "unit" };
            invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, keys);

            setInventoryFzhsValue(blist, invenMap, alisInvenMap, false, new String[]{ "bspmc", "ggxh", "jldw" });
        }

    }

    private void setCustomerFzhsValue(List<BillApplyVO> list,
                                      Map<String, AuxiliaryAccountBVO> customerMap,
                                      String[] arr){

        String key;
        String value;

        for(BillApplyVO vo : list){
            key = "";
            for(String str : arr){
                value = (String) vo.getAttributeValue(str);
                key +=  "," + value;
            }
            key = key.substring(1);
            String pk_customer = "";
            if(customerMap.containsKey(key)){
                AuxiliaryAccountBVO invenvo = customerMap.get(key);
                pk_customer = invenvo.getPk_auacount_b();

            }

            if(StringUtil.isEmpty(pk_customer)){
                throw new BusinessException("<p>第" + (vo.getSerino()) + "行 未匹配上客户</p> ");
            }

            vo.setPk_app_customer(pk_customer);
        }

    }

    private void setInventoryFzhsValue(List<BillApplyVO> list,
                                       Map<String, AuxiliaryAccountBVO> invenMap,
                                       Map<String, InventoryAliasVO> alisInvenMap,
                                       boolean kcflag,
                                       String[] arr){

        String key;
        String value;

        for(BillApplyVO vo : list){
            key = "";
            for(String str : arr){
                value = (String) vo.getAttributeValue(str);
                key += "," + value;
            }
            key = key.substring(1);
            String pk_inventory = "";
            if(alisInvenMap.containsKey(key)){
                InventoryAliasVO alisvo = alisInvenMap.get(key);
                pk_inventory = alisvo.getPk_inventory();

            }

            if(StringUtil.isEmpty(pk_inventory)
                    && invenMap.containsKey(key)){
                AuxiliaryAccountBVO invenvo = invenMap.get(key);
                pk_inventory = invenvo.getPk_auacount_b();
            }

            if(StringUtil.isEmpty(pk_inventory)){
                throw new BusinessException("<p>第" + (vo.getSerino()) + "行 未匹配上存货</p> ");
            }

            if(kcflag){
                vo.setPk_inventory(pk_inventory);
            }else{
                vo.setPk_app_commodity(pk_inventory);
            }

        }

    }

    private Object[][] getStyleByExcel(){
        Object[][] obj0 = new Object[][] {
                { 0, "订单号", "tradeno" },
                { 1, "分机号", "extensionnum" },
                { 2, "机器编号", "machinecode" },
                { 3, "购买方名称", "vcompanyname" },
                { 4, "购买方纳税人识别号", "vtaxcode" },
                { 5, "购买方开户行", "vbank" },
                { 6, "购买方开户行账号", "vbankcode" },
                { 7, "购买方地址", "vcompanyaddr" },
                { 8, "购买方电话", "vphone" },
                { 9, "商品名称", "bspmc" },
                { 10, "税收分类编码名称", "taxclassname" },
                { 11, "税收分类编码", "taxclassid" },
                { 12, "规格型号", "ggxh" },
                { 13, "计量单位", "jldw" },
                { 14, "数量", "bnnum" },
                { 15, "含税单价", "bnprice" },
                { 16, "含税金额", "bnmny" },
                { 17, "折扣行金额", "bnzkmny" },
                { 18, "税率", "bsl" },
                { 19, "扣除金额", "deducmny" },
                { 20, "收款人", "cashername" },
                { 21, "复核人", "reviewername" },
                { 22, "备注", "memo" },
                { 23, "收票人名称", "takername" },
                { 24, "收票人手机号", "takertel" },
                { 25, "收票人邮箱", "takeremail" },
        };

        return obj0;
    }

    private String[][] getBStyle(){
        String[][] obj0 = new String[][] { //
                { "pk_corp", "pk_corp" },
                { "pk_app_commodity", "pk_app_commodity" },
                { "pk_inventory", "pk_inventory" },
                { "pk_corp", "pk_corp" },
                { "bspmc", "spmc" },
                { "taxclassname", "taxclassname" },
                { "taxclassid", "taxclassid" },
                { "ggxh", "ggxh" },
                { "jldw", "jldw" },
                { "bnnum", "nnum" },
                { "bnprice", "nprice" },
                { "bnmny", "nmny" },
                { "bnzkmny", "nzkmny" },
                { "bsl", "ntax" },

                { "lslyhzcbs", "lslyhzcbs" },
                { "zerotaxflag", "zerotaxflag" },
                { "vatspeman", "vatspeman" },
        };

        return obj0;
    }

    private List<BillApplyVO> specialBVO(List<BillApplyVO> blist, String pk_corp,
                                         String userid, CorpVO corpvo){

        if (blist == null || blist.size() == 0) {
            return null;
        }

        //匹配
        fzhsAdapter(blist, pk_corp, corpvo);

        String[][] STYLE_2 = getBStyle();

        List<BillApplyVO> result = new ArrayList<BillApplyVO>();

        Map<String, List<BillApplyVO>> maps = DZfcommonTools.hashlizeObject(blist,
                new String[]{ "tradeno" });

        Object value;
        BillApplyVO appvo;
        BillApplyDetailVo detailvo;
        List<BillApplyVO> tempList;
        List<BillApplyDetailVo> tempBList;


        //再来一次检查  差额扣除只允许一行明细行
        DZFDouble kcvalue;
        for(Map.Entry<String, List<BillApplyVO>> entry : maps.entrySet()){
            tempList = entry.getValue();
            //累计有几个扣除行
            for(BillApplyVO vo : tempList){
                kcvalue = vo.getDeducmny();
                if(kcvalue != null
                        && kcvalue.doubleValue() != 0
                        && tempList.size() > 1){
                    throw new BusinessException("<p>第 " + (vo.getSerino()) + "行 数据有误,差额开票只允许有一个明细行</p> ");
                }
            }
        }

        DZFDouble totalje = DZFDouble.ZERO_DBL;
        DZFDouble totalse = DZFDouble.ZERO_DBL;

        for(Map.Entry<String, List<BillApplyVO>> entry : maps.entrySet()){
            tempList = entry.getValue();
            tempBList = new ArrayList<BillApplyDetailVo>();

            totalje = DZFDouble.ZERO_DBL;
            totalse = DZFDouble.ZERO_DBL;

            for(BillApplyVO vo : tempList){
                detailvo = new BillApplyDetailVo();
                for(String[] arr : STYLE_2){
                    value = vo.getAttributeValue(arr[0]);
                    if(value != null){
                        detailvo.setAttributeValue(arr[1], value);
                    }

                }

                //扣除金额
                DZFDouble kcje = vo.getDeducmny();

                //税率
                DZFDouble sl = detailvo.getNtax();
                if(sl != null || SafeCompute.add(sl, DZFDouble.ZERO_DBL).doubleValue() != 0){
                    //设置税额
                    DZFDouble ntaxmny = SafeCompute.multiply(
                            SafeCompute.div(SafeCompute.sub(detailvo.getNmny(), kcje),
                                    SafeCompute.add(sl, new DZFDouble(1))), sl);
                    ntaxmny = ntaxmny.setScale(2, DZFDouble.ROUND_UP);
                    detailvo.setNtaxmny(ntaxmny);
                }

                DZFDouble zkje = detailvo.getNzkmny();//折扣行的金额
                DZFDouble zkse = null;
                if(zkje != null && zkje.doubleValue() != 0){
                    zkse = SafeCompute.multiply(SafeCompute.div(zkje, SafeCompute.add(sl, new DZFDouble(1))), sl);//折扣行的税额
                    zkse = zkse.setScale(2, DZFDouble.ROUND_UP);

                    detailvo.setNzktax(SafeCompute.div(zkje, detailvo.getNmny()));//折扣率
                }

                //处理税收分类编码
                setTaxClassID(detailvo);

                totalje = SafeCompute.add(totalje, SafeCompute.sub(detailvo.getNmny(), zkje));
                totalse = SafeCompute.add(totalse, SafeCompute.sub(detailvo.getNtaxmny(), zkse));

                tempBList.add(detailvo);
            }

            appvo = tempList.get(0);
            appvo.setChildren(tempBList.toArray(new BillApplyDetailVo[0]));

            appvo.setNtaxtotal(totalje);
            appvo.setNmny(SafeCompute.sub(totalje, totalse));
            appvo.setNtaxmny(totalse);
//			appvo.setNtaxmny();

            if(SafeCompute.add(totalje, DZFDouble.ZERO_DBL).doubleValue() > 0){//判断是蓝票还是红票
                appvo.setFptype(Integer.parseInt(ICaiFangTongConstant.FPLX_1));
            }else{
                appvo.setFptype(Integer.parseInt(ICaiFangTongConstant.FPLX_2));
            }

            result.add(appvo);
        }

        return result;
    }

    private void setTaxClassID(BillApplyDetailVo detailvo){
        String classname = detailvo.getTaxclassid();
        if(!StringUtil.isEmpty(classname)){
            String regEx="[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(classname);
            detailvo.setTaxclassid(m.replaceAll("").trim());
        }
    }

    private Map<String, InventoryAliasVO> buildAlistMap(List<InventoryAliasVO> alisList, String[] keys){
        if(alisList == null || alisList.size() == 0){
            return new HashMap<String, InventoryAliasVO>();
        }

        Map<String, InventoryAliasVO> alisInvenMap =  DZfcommonTools.hashlizeObjectByPk(alisList, keys);
        return alisInvenMap;
    }

    private List<InventoryAliasVO> queryAlisInven(String pk_corp){
        String sql = "Select * From ynt_icalias y Where y.pk_corp = ? and nvl(dr,0) = 0 order by ts desc";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        List<InventoryAliasVO> list = (List<InventoryAliasVO>) singleObjectBO.executeQuery(sql, sp,
                new BeanListProcessor(InventoryAliasVO.class));

        return list;

    }

    private void setDefaultValue(String pk_corp, String userid, BillApplyVO vo, int index){
        vo.setPk_corp(pk_corp);
        vo.setIbillstatus(IInvoiceApplyConstant.INV_STATUS_0);//未开票
//		vo.setVbilltype(); 这个字段不知道设置什么值
        vo.setInvoserino(getSeriNO());//发票请求流水号
        vo.setSerino(index+1);

        vo.setSourcetype(IInvoiceApplyConstant.KP_SOURCE_1);//在线会计
        vo.setVapplytor(userid);//申请人
        vo.setDapplydate(new DZFDateTime());

    }

    private String getSeriNO(){
        return PiaoTongUtil.getSerialNo(PiaoTongUtil.xxptbm, 2);
    }

    private void checkDataValid(BillApplyVO vo, StringBuffer sf,
                                int index,
                                CorpVO corpvo) {

        StringBuffer msg = new StringBuffer();
        if(StringUtil.isEmpty(vo.getTradeno())){
            msg.append(" 订单号不允许为空,请检查！ ");
        }
        if(StringUtil.isEmpty(vo.getVcompanyname())){
            msg.append(" 购买方名称不允许为空,请检查！ ");
        }

        if(StringUtil.isEmpty(vo.getBspmc())){
            msg.append(" 商品名称不允许为空,请检查！ ");
        }

        if(StringUtil.isEmpty(vo.getTaxclassid())){
            msg.append(" 税收分类编码不允许为空,请检查！ ");
        }
        if(vo.getBnnum() == null){
            msg.append(" 数量不允许为空,请检查！ ");
        }
        if(vo.getBnprice() == null){
            msg.append(" 含税单价不允许为空,请检查！ ");
        }
        if(vo.getBnmny() == null){
            msg.append(" 含税金额不允许为空,请检查！ ");
        }
        if(vo.getBsl() == null){
            msg.append(" 税率不允许为空,请检查！ ");
        }

        if(vo.getDeducmny() != null
                && SafeCompute.add(DZFDouble.ZERO_DBL, vo.getDeducmny()).doubleValue() < 0){
            msg.append(" 扣除金额不允许录负数,请检查！ ");
        }

        if(vo.getBnzkmny() != null
                && SafeCompute.add(DZFDouble.ZERO_DBL, vo.getBnzkmny()).doubleValue() < 0){
            msg.append(" 折扣金额不允许录负数,请检查！ ");
        }

        if (!StringUtil.isEmpty(msg.toString())) {
            sf.append("<p>第").append(index + 1).append("行  ").append(msg.toString()).append(" </p> ");
            //暂时这么写
            throw new BusinessException(sf.toString());
        }
    }

    private String getExcelCellValue(Cell cell) {
        String ret = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 格式化日期字符串
            if (cell == null) {
                ret = null;
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
                ret = cell.getRichStringCellValue().getString();
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                ret = "" + Double.valueOf(cell.getNumericCellValue()).doubleValue();
//				 小数不可用这样格式，只为了凭证编码格式
                java.text.DecimalFormat formatter = new java.text.DecimalFormat("#############.##");
                if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                    ret = formatter.format(cell.getNumericCellValue());
                } else if(cell.getCellStyle().getDataFormatString().indexOf(".")>=0){
                    ret = formatter.format(cell.getNumericCellValue());
                }else{
                    ret = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                }
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
                String value1 = null;
                try {
                    java.text.DecimalFormat formatter = new java.text.DecimalFormat("#############.##");
                    value1 = formatter.format(cell.getNumericCellValue());
                    ret = value1;
                }
                catch (Exception e)
                {}
                if (StringUtil.isEmpty(value1) || "0.00".equals(ret))
                {
                    try {
                        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(cell);
                        ret = String.valueOf(cellValue.getNumberValue());
                    }
                    catch (Exception e)
                    {}
                }
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
                ret = "" + cell.getErrorCellValue();
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
                ret = "" + cell.getBooleanCellValue();
            } else if (cell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
                ret = null;
            }
        } catch (Exception ex) {
            log.error("错误",ex);
            ret = null;
        }
        return OcrUtil.filterString(ret);
    }

    @Override
    public boolean createKp(BillApplyVO vo, UserVO uservo) throws DZFWarpException {
        boolean flag = true;
        List<BillApplyVO> list = query(null, null, vo);
        if (list == null || list.size() == 0 || list.size() > 1)
            throw new BusinessException("参数不完整,请检查");
        BillApplyVO billvo = list.get(0);

        Integer status = vo.getIbillstatus();// 票据状态
        if ((status != null && (status == IInvoiceApplyConstant.INV_STATUS_1
                || status == IInvoiceApplyConstant.INV_STATUS_5))
                || !StringUtil.isEmpty(billvo.getFpdm())) {
            throw new BusinessException("开票中、开票成功的单据不允许再次开票");
        }

        List<BillApplyDetailVo> detailList = queryB(billvo.getPk_app_billapply(), billvo.getPk_corp());
        if (detailList == null || detailList.size() == 0)
            throw new BusinessException("子参数不完整,请检查");

        PiaoTongResVO resvo = requestLp(billvo, detailList);
        if (resvo == null) {
            throw new BusinessException("请求返回报错,请联系管理员");
        }

        if (CommonXml.rtnsucccode.equals(resvo.getCode())) {
            vo.setIbillstatus(IInvoiceApplyConstant.INV_STATUS_5);
            vo.setMemo(resvo.getMsg());
            vo.setVbilltor(uservo.getPrimaryKey());
        } else {
            flag = false;
            vo.setIbillstatus(IInvoiceApplyConstant.INV_STATUS_6);
            vo.setMemo(resvo.getMsg());
            vo.setVbilltor(uservo.getPrimaryKey());
        }

        singleObjectBO.update(vo, new String[] { "ibillstatus", "memo", "vbilltor" });

        return flag;
    }
    //	  Map<String, Object> map = new HashMap<String, Object>();
//	  map.put("taxpayerNum", "500102201007206608");  //销方税号
//	  map.put("invoiceReqSerialNo", PiaoTongUtil.getSerialNo(PiaoTongUtil.xxptbm, 2));//发票请求流水号
//	  map.put("buyerName", "购买方名称");//购买方名称
//	  map.put("buyerTaxpayerNum", "XX0000000000000000");//购买方税号(非必填,个人发票传null)
//	  List<Map<String, String>> list = new ArrayList<Map<String, String>>();
//	  Map<String, String> listMapOne = new HashMap<String, String>();
//	  listMapOne.put("taxClassificationCode", "1010101020000000000");//税收分类编码(可以按照Excel文档填写)
//	  listMapOne.put("quantity", "1.00");//数量
//	  listMapOne.put("goodsName", "货物名称");//货物名称
//	  listMapOne.put("unitPrice", "56.64");//单价
//	  listMapOne.put("invoiceAmount", "56.64");//金额
//	  listMapOne.put("taxRateValue", "0.16");//税率
//	  listMapOne.put("includeTaxFlag", "0");//含税标识
//	  //以下为零税率开票相关参数
//	  listMapOne.put("zeroTaxFlag", null);//零税率标识(空:非零税率,0:出口零税率,1:免税,2:不征税,3:普通零税率)
//	  listMapOne.put("preferentialPolicyFlag", null);//优惠政策标识(空:不使用,1:使用)   注:零税率标识传非空 此字段必须填写为"1"
//	  listMapOne.put("vatSpecialManage", null);//增值税特殊管理(preferentialPolicyFlag为1 此参数必填)
//	  list.add(listMapOne);
//	  map.put("itemList", list);
    private PiaoTongResVO requestLp(BillApplyVO applyVO, List<BillApplyDetailVo> childList){
        //前置数据
        CorpVO corpVO = getCorpVO(applyVO.getPk_corp());
        String vsoccrecode = corpVO.getVsoccrecode();
        if(StringUtil.isEmpty(vsoccrecode)){
            throw new BusinessException("纳税识别号为空,不允许开票");
        }
        String seriano = applyVO.getInvoserino();
        if(StringUtil.isEmpty(seriano)){
            throw new BusinessException("发票请求流水号空,不允许开票");
        }
        String gmfmc = applyVO.getVcompanyname();//购买方纳税人名称
        if(StringUtil.isEmpty(gmfmc)){
            throw new BusinessException("购买方名称空,不允许开票");
        }

        //销货方地址
        //销货方电话
        //销货方开户行
        //销货方银行账号
        //主开票项目名称
        //开票人名称
        //特殊票种
        //代开标示
        String[][] arr = {
                {"vtaxcode", "buyerTaxpayerNum"},
                {"vcompanyaddr", "buyerAddress"},
                {"vphone", "buyerTel"},
                {"vbank", "buyerBankName"},
                {"vbankcode", "buyerBankAccount"},
                {"cashername", "casherName"},
                {"reviewername", "reviewerName"},
                {"takername", "takerName"},
                {"takertel", "takerTel"},
                {"takeremail", "takerEmail"},
                {"memo", "remark"},
                {"tradeno", "tradeNo"},
//				{"extensionnum", "extensionNum"},
//				{"machinecode", "machineCode"},
        };

        //构造请求报文
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("taxpayerNum", vsoccrecode);  //销方税号  "500102192801051381"
        map.put("invoiceReqSerialNo", seriano);//发票请求流水号
        map.put("buyerName", gmfmc);//购买方名称

        String value;
        for(String[] ss : arr){
            value = (String) applyVO.getAttributeValue(ss[0]);
            if(!StringUtil.isEmpty(value)){
                map.put(ss[1], value);
            }
        }

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        String spmc = null;

        String ggxh = null;
        String unit = null;

        String taxclassid = null;
        DZFDouble nnum = null;
        DZFDouble nprice = null;
        DZFDouble nmny = null;
        DZFDouble ntax = null;
        DZFDouble bnzkmny = null;
        Integer preferentialPolicyFlag = null;
        Integer zeroTaxFlag = null;
        String vatSpecialManage = null;

        //
        DZFDouble deducmny = applyVO.getDeducmny();//差额开票

        for(BillApplyDetailVo detailvo : childList){
            Map<String, String> listMapOne = new HashMap<String, String>();
            spmc = detailvo.getSpmc();
            if(StringUtil.isEmpty(spmc)){
                throw new BusinessException("商品名称为空,不允许开票");
            }

            ggxh = detailvo.getGgxh();
            unit = detailvo.getJldw();

            taxclassid = detailvo.getTaxclassid();
            if(StringUtil.isEmpty(taxclassid)){
                throw new BusinessException("对应税收分类编码为空,不允许开票");
            }

            nnum = detailvo.getNnum();
            if(nnum == null){
                throw new BusinessException("商品数量为空,不允许开票");
            }

            nprice = detailvo.getNprice();
            if(nprice == null){
                throw new BusinessException("商品单价为空,不允许开票");
            }

            nmny = detailvo.getNmny();
            if(nmny == null){
                throw new BusinessException("商品金额为空,不允许开票");
            }else{
                nmny = nmny.setScale(2, DZFDouble.ROUND_UP);
            }

            ntax = detailvo.getNtax();
            if(ntax != null){
//        		 throw new BusinessException("商品税率为空,不允许开票");
                ntax = ntax.setScale(2, DZFDouble.ROUND_UP);
            }

            bnzkmny = detailvo.getNzkmny();

            listMapOne.put("taxClassificationCode", taxclassid);//税收分类编码(可以按照Excel文档填写)
            listMapOne.put("quantity", nnum.toString());//数量
            listMapOne.put("goodsName", spmc);//货物名称

            listMapOne.put("unitPrice", nprice.toString());//单价
            listMapOne.put("invoiceAmount", nmny.toString());//金额
            listMapOne.put("taxRateValue", ntax.toString());//税率
            listMapOne.put("includeTaxFlag", "1");//含税标识   含税

            //规格型号  计量单位
            if(!StringUtil.isEmpty(ggxh)){
                listMapOne.put("specificationModel", ggxh);
            }

            if(!StringUtil.isEmpty(unit)){
                listMapOne.put("meteringUnit", unit);
            }

            if(deducmny != null){
                deducmny = deducmny.setScale(2, DZFDouble.ROUND_UP);
                listMapOne.put("deductionAmount", deducmny.toString());
            }

            if(bnzkmny != null){
                //传负数
                bnzkmny = SafeCompute.sub(DZFDouble.ZERO_DBL, bnzkmny);
                bnzkmny = bnzkmny.setScale(2, DZFDouble.ROUND_UP);

                listMapOne.put("discountAmount", bnzkmny.toString());
            }

            preferentialPolicyFlag = detailvo.getLslyhzcbs();
            zeroTaxFlag = detailvo.getZerotaxflag();
            vatSpecialManage = detailvo.getVatspeman();

            //以下为零税率开票相关参数
            String zeroflag = zeroTaxFlag == null ? null : zeroTaxFlag+"";

            listMapOne.put("zeroTaxFlag", zeroflag);//零税率标识(空:非零税率,0:出口零税率,1:免税,2:不征税,3:普通零税率)

            String preflag = preferentialPolicyFlag == null ? null : preferentialPolicyFlag+"";
            listMapOne.put("preferentialPolicyFlag", preflag);//优惠政策标识(空:不使用,1:使用)   注:零税率标识传非空 此字段必须填写为"1"
            listMapOne.put("vatSpecialManage", vatSpecialManage);//增值税特殊管理(preferentialPolicyFlag为1 此参数必填)

            list.add(listMapOne);
        }

        map.put("itemList", list);

        String content = JsonUtils.serialize(map);

        PiaoTongResVO resVO = PiaoTongUtil.request(content, "/invoiceBlue.pt");
        return resVO;
    }

    @Override
    public BillApplyVO queryByFPDMHM(String fpdm, String fphm, String pk_corp)
            throws DZFWarpException {
        if(StringUtil.isEmpty(fpdm)
                || StringUtil.isEmpty(fphm)
                || StringUtil.isEmpty(pk_corp)){
            return null;
        }

        String sql = "select * from ynt_app_billapply y where y.pk_corp = ? and y.fpdm = ? and y.fphm = ? and nvl(y.dr,0)=0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(fpdm);
        sp.addParam(fphm);
        BillApplyVO applyVO = (BillApplyVO) singleObjectBO.executeQuery(sql, sp,
                new BeanProcessor(BillApplyVO.class));
        return applyVO;
    }

    @Override
    public BillApplyVO queryBySerialNo(String serialno) throws DZFWarpException {
        String sql = "select * from ynt_app_billapply y where nvl(dr,0)=0 and y.invoserino = ? ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(serialno);
        BillApplyVO applyVO = (BillApplyVO) singleObjectBO.executeQuery(sql, sp,
                new BeanProcessor(BillApplyVO.class));
        return applyVO;
    }

    @Override
    public BillApplyVO saveHcBill (BillApplyVO vo, UserVO uservo)
            throws DZFWarpException{
        BillApplyVO result = null;
        List<BillApplyVO> list = query(null, null, vo);
        if(list == null || list.size() == 0 || list.size() > 1)
            throw new BusinessException("参数不完整,请检查");
        BillApplyVO billvo = list.get(0);

        Integer source = vo.getSourcetype();
        if(source == null || source == IInvoiceApplyConstant.KP_SOURCE_0){
            throw new BusinessException("来源手机端的票据不允许进行冲红操作");
        }

        //校验
        DZFBoolean originred = billvo.getOriginred();
        if(originred != null && originred.booleanValue()){
            throw new BusinessException("已冲红的单据不允许再次进行冲红操作");
        }

        Integer fplx = billvo.getFptype();//发票类型

        if(fplx == null){
            throw new BusinessException("发票类型为空,不允许进行冲红操作");
        }

        Integer fpstatus = billvo.getIbillstatus();//发票状态
        if(fplx == Integer.parseInt(ICaiFangTongConstant.FPLX_1)
                && fpstatus != IInvoiceApplyConstant.INV_STATUS_1){//蓝票并且开票成功
            throw new BusinessException("已开票成功的单据才允许进行冲红操作");
        }

        if(fplx == Integer.parseInt(ICaiFangTongConstant.FPLX_2)
                && (fpstatus == IInvoiceApplyConstant.INV_STATUS_1
                || fpstatus == IInvoiceApplyConstant.INV_STATUS_5)){//红票未开票、开票失败
            throw new BusinessException("只未开票、开票失败的单据进行冲红操作");
        }

        if(fplx == Integer.parseInt(ICaiFangTongConstant.FPLX_1) ){//蓝票
            List<BillApplyDetailVo> detailList = queryB(billvo.getPk_app_billapply(), billvo.getPk_corp());
            if(detailList == null || detailList.size() == 0)
                throw new BusinessException("子参数不完整,请检查");

            billvo.setChildren(detailList.toArray(new BillApplyDetailVo[0]));

            //设置冲红原因
            billvo.setRedreason(vo.getRedreason());

            result = cloneBill(billvo, uservo.getPrimaryKey());

            //
            List<BillApplyVO> ll = new ArrayList<BillApplyVO>();
            ll.add(result);
            updateArr(result.getPk_corp(), ll);
            //

            billvo.setOriginred(DZFBoolean.TRUE);//设置下
            singleObjectBO.update(billvo, new String[]{ "originred" });
        }else{//红票只更新信息

            billvo.setRedreason(vo.getRedreason());
            singleObjectBO.update(billvo, new String[]{ "redreason" });
            result = billvo;
        }

        return result;
    }

    private BillApplyVO cloneBill(BillApplyVO vo, String userid){
        BillApplyVO newvo = deepClone(vo);
        newvo.setPk_app_billapply(null);
        newvo.setNmny(SafeCompute.sub(DZFDouble.ZERO_DBL, vo.getNmny()));
        newvo.setNtaxmny(SafeCompute.sub(DZFDouble.ZERO_DBL, vo.getNtaxmny()));
        newvo.setNtaxtotal(SafeCompute.sub(DZFDouble.ZERO_DBL, vo.getNtaxtotal()));
        newvo.setTs(null);
        newvo.setIbillstatus(IInvoiceApplyConstant.INV_STATUS_0);
        newvo.setVapplytor(userid);
        newvo.setDapplydate(new DZFDateTime());
        newvo.setInvoserino(getSeriNO());
        newvo.setFpdm(null);
        newvo.setFphm(null);
        newvo.setYfpdm(vo.getFpdm());
        newvo.setYfphm(vo.getFphm());
        newvo.setMemo(null);
        newvo.setTradeno(null);
        newvo.setDdate(null);//开票时间
        newvo.setFptype(Integer.parseInt(ICaiFangTongConstant.FPLX_2));//红票
        newvo.setRedflag(IInvoiceApplyConstant.RED_FLAG_3);//冲红中
        newvo.setSourcetype(IInvoiceApplyConstant.KP_SOURCE_2);//节点数据衍生出来的 如 冲红操作
        newvo.setSourceid(vo.getPrimaryKey());
        newvo.setRedreason(vo.getRedreason());

        BillApplyDetailVo[] children = (BillApplyDetailVo[]) newvo.getChildren();
        for(BillApplyDetailVo detailvo : children){
            detailvo.setPk_app_billapply(null);
            detailvo.setPk_app_billapply_detail(null);
            detailvo.setNnum(SafeCompute.sub(DZFDouble.ZERO_DBL, detailvo.getNnum()));
            detailvo.setNmny(SafeCompute.sub(DZFDouble.ZERO_DBL, detailvo.getNmny()));
            detailvo.setNtaxmny(SafeCompute.sub(DZFDouble.ZERO_DBL, detailvo.getNtaxmny()));
            detailvo.setNzkmny(SafeCompute.sub(DZFDouble.ZERO_DBL, detailvo.getNzkmny()));
            detailvo.setTs(null);
        }

        return newvo;
    }

    private BillApplyVO deepClone(BillApplyVO vo){

        ByteArrayOutputStream bo = null ;
        ObjectOutputStream oo =  null;
        ByteArrayInputStream bi = null;
        ObjectInputStream oi = null;

        try {
            bo = new ByteArrayOutputStream();

            oo = new ObjectOutputStream(bo);

            oo.flush();

            oo.writeObject(vo);

            bi = new ByteArrayInputStream(bo.toByteArray());

            oi = new ObjectInputStream(bi);

            return (BillApplyVO) oi.readObject();
        } catch (Exception e) {
            log.error("错误",e);
        }finally {

            if (oo != null){
                try {
                    oo.close();
                } catch (IOException e) {
                }
            }

            if (bo != null){
                try {
                    bo.close();
                } catch (IOException e) {
                }
            }
            if(bi!=null){
                try {
                    bi.close();
                } catch (IOException e) {
                }
            }

            if(oi!=null){
                try {
                    oi.close();
                } catch (IOException e) {
                }
            }
        }
        return null;

    }

    @Override
    public boolean createHc(BillApplyVO vo, UserVO uservo)
            throws DZFWarpException{
        boolean flag = true;
        List<BillApplyVO> list = query(null, null, vo);
        if(list == null || list.size() == 0 || list.size() > 1)
            throw new BusinessException("参数不完整,请检查");
        BillApplyVO billvo = list.get(0);
        checkDataValid(billvo);

        PiaoTongResVO resvo = requestHc(billvo);
        if(resvo == null){
            throw new BusinessException("请求返回报错,请联系管理员");
        }

        if(CommonXml.rtnsucccode.equals(resvo.getCode())){
            vo.setMemo(resvo.getMsg());
        }else{
            vo.setMemo(resvo.getMsg());
            flag = false;
        }
        singleObjectBO.update(vo, new String[] { "memo"});

        return flag;
    }

    private void checkDataValid(BillApplyVO vo){
        Integer status = vo.getIbillstatus();//状态
        if(status != null && (status == IInvoiceApplyConstant.INV_STATUS_1
                || status == IInvoiceApplyConstant.INV_STATUS_5)){
            throw new BusinessException("只允许未开票、开票失败的单据进行开票操作");
        }
    }

    private PiaoTongResVO requestHc(BillApplyVO applyVO){
        //前置数据
        CorpVO corpVO = getCorpVO(applyVO.getPk_corp());
        String vsoccrecode = corpVO.getVsoccrecode();
        if(StringUtil.isEmpty(vsoccrecode)){
            throw new BusinessException("纳税识别号为空,不允许开票");
        }

        Map<String, Object> map = new HashMap<String, Object>();

        DZFDouble amount = applyVO.getNtaxtotal();
        amount = amount.setScale(2, DZFDouble.ROUND_UP);

        map.put("taxpayerNum", vsoccrecode);//销方税号(请于要冲红的蓝票税号一致)500102192801051381
        // TODO 请更换请求流水号前缀
        map.put("invoiceReqSerialNo", applyVO.getInvoserino());//发票流水号 (唯一, 与蓝票发票流水号不一致)
        map.put("invoiceCode", applyVO.getYfpdm());//冲红发票的发票代码
        map.put("invoiceNo", applyVO.getYfphm());//冲红发票的发票号码
        map.put("redReason", applyVO.getRedreason());//冲红原因
        map.put("amount", amount.toString());//冲红金额 (要与原发票的总金额一致)

        String content = JsonUtils.serialize(map);

        PiaoTongResVO resVO = PiaoTongUtil.request(content, "/invoiceRed.pt");
        return resVO;
    }

    @Override
    public void delete(BillApplyVO vo, UserVO uservo)
            throws DZFWarpException{
        List<BillApplyVO> list = query(null, null, vo);
        if(list == null || list.size() == 0 || list.size() > 1)
            throw new BusinessException("参数不完整,请检查");
        BillApplyVO billvo = list.get(0);

        //判断是不是来源于手机端
        Integer source = vo.getSourcetype();
        if(source == null || source == IInvoiceApplyConstant.KP_SOURCE_0){
            throw new BusinessException("企业主提交的单据不允许删除");
        }

        Integer status = billvo.getIbillstatus();
        if(status != null
                && (IInvoiceApplyConstant.INV_STATUS_1 == status
                || IInvoiceApplyConstant.INV_STATUS_5 == status)){
            throw new BusinessException("状态为开票中、开票成功的单据不允许删除");
        }

        String sqlA = "update ynt_app_billapply y set y.dr=1 where y.pk_corp = ? and y.pk_app_billapply = ? ";
        String sqlB = "update ynt_app_billapply_detail y set y.dr=1 where y.pk_corp = ? and y.pk_app_billapply = ? ";

        SQLParameter sp = new SQLParameter();
        sp.addParam(billvo.getPk_corp());
        sp.addParam(billvo.getPk_app_billapply());

        singleObjectBO.executeUpdate(sqlA, sp);
        singleObjectBO.executeUpdate(sqlB, sp);

        Integer sourcetype = billvo.getSourcetype();
        if(sourcetype != null
                && sourcetype == IInvoiceApplyConstant.KP_SOURCE_2){
            String sqlC = "update ynt_app_billapply y set y.originred='N' where y.pk_corp = ? and y.pk_app_billapply = ? ";
            sp = new SQLParameter();
            sp.addParam(billvo.getPk_corp());
            sp.addParam(billvo.getSourceid());
            singleObjectBO.executeUpdate(sqlC, sp);
        }

    }

    //抄biling代码
    @Override
    public boolean createBilling(BillApplyVO bill, UserVO uservo) throws DZFWarpException {
        Integer status = bill.getIbillstatus();
        if(status != null && status != IInvoiceApplyConstant.INV_STATUS_0){
            throw new BusinessException("只允许状态为未开票的单据进行开票");
        }
        DZFDateTime time = new DZFDateTime();
        String userid = uservo.getCuserid();
        String pk_corp = bill.getPk_corp();
        List<MsgAdminVO> msgs = new ArrayList<MsgAdminVO>();
        List<BillHistoryVO> historys = new ArrayList<BillHistoryVO>();

        bill.setIbillstatus(STATUS_BILLING);
        bill.setVbilltor(userid);
        bill.setDdate(time);
        historys.add(recodeHistory(bill, time, userid, "开票"));
        String content = "您" + bill.getDapplydate() + "为" + bill.getVcompanyname() + "提交的开票申请已开票，请点击查看";
        msgs.add(generateMessage(bill, time, userid, content));

        singleObjectBO.insertVOArr(pk_corp, historys.toArray(new BillHistoryVO[0]));
        singleObjectBO.insertVOArr(pk_corp, msgs.toArray(new MsgAdminVO[0]));
        singleObjectBO.update(bill, new String[] { "ibillstatus",
                "vbilltor", "ddate" });

        return true;
    }

    @Override
    public String billing(BillApplyVO[] bills, String userid)
            throws DZFWarpException {
        String pk_corp = bills[0].getPk_corp();
        List<BillApplyVO> billList = new ArrayList<BillApplyVO>();
        List<MsgAdminVO> msgs = new ArrayList<MsgAdminVO>();
        List<BillHistoryVO> historys = new ArrayList<BillHistoryVO>();
        DZFDateTime time = new DZFDateTime();
        for (BillApplyVO bill : bills) {
            Integer status = bill.getIbillstatus();
            if (status == null || status < STATUS_BILLING) {
                bill.setIbillstatus(STATUS_BILLING);
                bill.setVbilltor(userid);
                bill.setDdate(time);
                billList.add(bill);
                historys.add(recodeHistory(bill, time, userid, "开票"));
                String content = "您" + bill.getDapplydate() + "为" + bill.getVcompanyname() + "提交的开票申请已开票，请点击查看";
                msgs.add(generateMessage(bill, time, userid, content));
            }
        }
        singleObjectBO.insertVOArr(pk_corp, historys.toArray(new BillHistoryVO[0]));
        singleObjectBO.insertVOArr(pk_corp, msgs.toArray(new MsgAdminVO[0]));
        singleObjectBO.updateAry(billList.toArray(new BillApplyVO[0]), new String[] { "ibillstatus",
                "vbilltor", "ddate" });

        int success = billList.size();
        int fail = bills.length - success;
        String result = "成功：" + success;
        if (fail > 0) {
            result += ", 失败：" + fail;
        }
        return result;
    }

    @Override
    public String sentOut(BillApplyVO[] bills, String userid)
            throws DZFWarpException {
        String pk_corp = bills[0].getPk_corp();
        List<BillApplyVO> billList = new ArrayList<BillApplyVO>();
        List<MsgAdminVO> msgs = new ArrayList<MsgAdminVO>();
        List<BillHistoryVO> historys = new ArrayList<BillHistoryVO>();
        DZFDateTime time = new DZFDateTime();
        for (BillApplyVO bill : bills) {
            Integer status = bill.getIbillstatus();
            if (status == STATUS_BILLING) {
                bill.setIbillstatus(STATUS_SENTOUT);
                bill.setVsendtor(userid);
                bill.setDsenddate(time);
                billList.add(bill);
                historys.add(recodeHistory(bill, time, userid, "寄出"));
                String content = "您" + bill.getDapplydate() + "为" + bill.getVcompanyname() + "提交的开票申请已寄出，请点击查看";
                msgs.add(generateMessage(bill, time, userid, content));
            }
        }
        singleObjectBO.insertVOArr(pk_corp, historys.toArray(new BillHistoryVO[0]));
        singleObjectBO.insertVOArr(pk_corp, msgs.toArray(new MsgAdminVO[0]));
        singleObjectBO.updateAry(billList.toArray(new BillApplyVO[0]), new String[] { "ibillstatus",
                "vsendtor", "dsenddate" });

        int success = billList.size();
        int fail = bills.length - success;
        String result = "成功：" + success;
        if (fail > 0) {
            result += ", 失败：" + fail;
        }
        return result;
    }

    @Override
    public String accounting(BillApplyVO[] bills, String userid)
            throws DZFWarpException {
        String pk_corp = bills[0].getPk_corp();
        List<BillApplyVO> billList = new ArrayList<BillApplyVO>();
        List<MsgAdminVO> msgs = new ArrayList<MsgAdminVO>();
        List<BillHistoryVO> historys = new ArrayList<BillHistoryVO>();
        DZFDateTime time = new DZFDateTime();
        for (BillApplyVO bill : bills) {
            Integer status = bill.getIbillstatus();
            if (status == STATUS_SENTOUT) {
                bill.setIbillstatus(STATUS_ACCOUNTING);
                bill.setVaccountor(userid);
                bill.setDaccountdate(time);
                billList.add(bill);
                historys.add(recodeHistory(bill, time, userid, "入账"));
                String content = "您" + bill.getDapplydate() + "为" + bill.getVcompanyname() + "提交的开票申请已入账，请点击查看";
                msgs.add(generateMessage(bill, time, userid, content));
            }
        }
        singleObjectBO.insertVOArr(pk_corp, historys.toArray(new BillHistoryVO[0]));
        singleObjectBO.insertVOArr(pk_corp, msgs.toArray(new MsgAdminVO[0]));
        singleObjectBO.updateAry(billList.toArray(new BillApplyVO[0]), new String[] { "ibillstatus",
                "vaccountor", "daccountdate" });

        int success = billList.size();
        int fail = bills.length - success;
        String result = "成功：" + success;
        if (fail > 0) {
            result += ", 失败：" + fail;
        }
        return result;
    }

    @Override
    public String tax(BillApplyVO[] bills, String userid)
            throws DZFWarpException {
        String pk_corp = bills[0].getPk_corp();
        List<BillApplyVO> billList = new ArrayList<BillApplyVO>();
        List<MsgAdminVO> msgs = new ArrayList<MsgAdminVO>();
        List<BillHistoryVO> historys = new ArrayList<BillHistoryVO>();
        DZFDateTime time = new DZFDateTime();
        for (BillApplyVO bill : bills) {
            Integer status = bill.getIbillstatus();
            if (status == STATUS_ACCOUNTING) {
                bill.setIbillstatus(STATUS_TAX);
                bill.setVtaxer(userid);
                bill.setDtaxdate(time);
                billList.add(bill);
                historys.add(recodeHistory(bill, time, userid, "报税"));
                String content = "您" + bill.getDapplydate() + "为" + bill.getVcompanyname() + "提交的开票申请已报税，请点击查看";
                msgs.add(generateMessage(bill, time, userid, content));
            }
        }
        singleObjectBO.insertVOArr(pk_corp, historys.toArray(new BillHistoryVO[0]));
        singleObjectBO.insertVOArr(pk_corp, msgs.toArray(new MsgAdminVO[0]));
        singleObjectBO.updateAry(billList.toArray(new BillApplyVO[0]), new String[] { "ibillstatus",
                "vtaxer", "dtaxdate" });

        int success = billList.size();
        int fail = bills.length - success;
        String result = "成功：" + success;
        if (fail > 0) {
            result += ", 失败：" + fail;
        }
        return result;
    }

    private void dealUserName (List<BillApplyVO> bills) {
        HashMap<String, String> userMap = new HashMap<String, String>();
        String[] idAttrs = {"vapplytor", "vbilltor", "vsendtor", "vaccountor", "vtaxer"};
        String[] nameAttrs = {"apply_name", "billing_name", "sendout_name",
                "accounting_name", "taxer_name"};
        String uid = null;
        String userName = null;
        for (BillApplyVO bill : bills) {
            for (int i = 0; i < idAttrs.length; i++) {
                uid = (String) bill.getAttributeValue(idAttrs[i]);
                if (!StringUtil.isEmpty(uid)) {
                    if (userMap.containsKey(uid)) {
                        userName = userMap.get(uid);
                    } else {
                        userName = userServiceImpl.queryUserJmVOByID(uid).getUser_name();
                    }
                    bill.setAttributeValue(nameAttrs[i], userName);
                }
            }
        }
    }

    private BillHistoryVO recodeHistory (BillApplyVO bill, DZFDateTime time, String userid, String content) {
        BillHistoryVO his = new BillHistoryVO();
        his.setPk_app_billapply(bill.getPk_app_billapply());
        his.setPk_corp(bill.getPk_corp());
        his.setPk_temp_corp(bill.getPk_temp_corp());
        his.setDr(0);
        his.setDopedate(time);
        his.setVopecontent(content);
        his.setVoperatetor(userid);
        return his;

    }

    private MsgAdminVO generateMessage (BillApplyVO bill, DZFDateTime time, String userid, String content) {
        MsgAdminVO msgvo = new MsgAdminVO();
        msgvo.setPk_corp(bill.getPk_corp());
        msgvo.setCuserid(bill.getVapplytor());
        msgvo.setMsgtype(MsgtypeEnum.MSG_TYPE_BILLING.getValue());
        msgvo.setMsgtypename(MsgtypeEnum.MSG_TYPE_BILLING.getName());
        msgvo.setVcontent(content);
        msgvo.setSendman(userid);
        msgvo.setVsenddate(time.toString());
        msgvo.setSys_send(ISysConstants.SYS_KJ);
        msgvo.setVtitle(null);
        msgvo.setIsread(DZFBoolean.FALSE);
        msgvo.setPk_corpk(bill.getPk_corp());//小企业主信息
        msgvo.setDr(0);
        msgvo.setPk_bill(bill.getPrimaryKey());
        return msgvo;
    }

}
