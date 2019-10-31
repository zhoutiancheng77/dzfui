package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.base.utils.FieldMapping;
import com.dzf.zxkj.base.utils.FieldMappingCache;
import com.dzf.zxkj.common.constant.FieldConstant;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.image.DcModelBVO;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IDcpzService;
import com.dzf.zxkj.platform.util.PinyinUtil;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/sys/sys_dcpzmb")
@Slf4j
public class DcPzmbController {

    @Autowired
    private IDcpzService dcpzjmbserv;
    @Autowired
    private ICorpService corpserv;

    //保存
    @PostMapping("/save")
    public ReturnData<Json> save(String head, String body){
        Json json = new Json();
        String tips = null;
        try{
            String corpid = SystemUtil.getLoginCorpId();
            body = body.replace("}{", "},{");
            body = "[" + body + "]";

            DcModelHVO headvo = JsonUtils.deserialize(head, DcModelHVO.class);
            DcModelBVO[] bodyvos = JsonUtils.deserialize(body, DcModelBVO[].class);

            setDefaultInfo(headvo,bodyvos);
            headvo.setChildren(bodyvos);
            String errorinfo = dcpzjmbserv.check(corpid,headvo);
            if(errorinfo == null || errorinfo.length() == 0){
                if(StringUtil.isEmpty(headvo.getPrimaryKey())){
                    tips= "新增数据中心凭证模板";
                }else{
                    tips= "修改数据中心凭证模板";
                }
                DcModelHVO savevo = dcpzjmbserv.save(headvo);
                DcModelBVO[] savebody = (DcModelBVO[])savevo.getChildren();
                savevo.setChildren(null);
                json.setSuccess(true);
                json.setMsg("保存成功!");
                json.setHead(savevo);
                json.setChilds(setGrid(savebody));
            }else{
                json.setSuccess(false);
                json.setMsg(errorinfo);
            }
        }catch(Exception e){
//            printErrorLog(json, log, e, "保存失败!");
            json.setSuccess(false);
            json.setMsg("保存失败!");
        }
//        writeLogRecord(LogRecordEnum.OPE_JITUAN_PZMB.getValue(), tips, ISysConstants.SYS_0);

        return ReturnData.ok().data(json);
    }
    //子表数据
    public Grid setGrid(DcModelBVO[] savebody){
        Grid gr = new Grid();
        gr.setTotal(Long.valueOf(savebody.length));
        gr.setSuccess(true);;
        gr.setRows(new ArrayList<DcModelBVO>(Arrays.asList(savebody)));
        return gr;
    }
    //赋默认值
    public void setDefaultInfo(DcModelHVO headvo,DcModelBVO[] bodyvos){
        if(headvo == null || bodyvos == null || bodyvos.length == 0)
            return;
        String  corpid = SystemUtil.getLoginCorpId();
        headvo.setPk_corp(corpid);
        headvo.setDr(0);
        //拼音简写
        String bustypename = headvo.getBusitypetempname();
        if(!StringUtil.isEmpty(bustypename)){
            String res = PinyinUtil.getFirstSpell(bustypename);
            if(StringUtil.isEmpty(res)){
                headvo.setShortpinyin(bustypename);
            }else{
                headvo.setShortpinyin(res);
            }
        }
        for(int i=0; i < bodyvos.length;i++){
			/*String[] names = bodyvos[i].getSubjname().split("_");
			bodyvos[i].setVcode(names[0]);
			bodyvos[i].setVname(names[1]);*/
            bodyvos[i].setPk_corp(corpid);
            bodyvos[i].setDr(0);
        }
    }

    //查询
    @GetMapping("/query")
    public ReturnData<Json> query(@RequestParam("isquickcreate") String quickcreate,
                      String isdefault, String busitypetempname){
        Grid grid = new Grid();
        String corpid = SystemUtil.getLoginCorpId();
        try {
            //是否快速制单
            if(!StringUtil.isEmpty(corpid)){
//                DcModelHVO hvo = (DcModelHVO)DzfTypeUtils.cast( getRequest(), new DcModelHVO());
                DcModelHVO hvo = new DcModelHVO();
                if(StringUtil.isEmpty(isdefault)){
                    hvo.setIsdefault(null);
                }
                hvo.setBusitypetempname(busitypetempname);
                List<DcModelHVO> list = dcpzjmbserv.queryself(corpid, quickcreate, hvo);
                if(list == null || list.size() == 0){
                    list = new ArrayList<DcModelHVO>();
                    grid.setTotal(Long.valueOf(0));
                }else{
                    grid.setTotal(Long.valueOf(list.size()));
                }
                grid.setRows(list);
                grid.setSuccess(true);
            }else{
                grid.setTotal(Long.valueOf(0));
                grid.setSuccess(false);
            }
        } catch (Exception e) {
//            printErrorLog(grid, log, e, "查询失败！");
            grid.setSuccess(false);
            grid.setMsg("查询失败！");
        }
        return ReturnData.ok().data(grid);
    }

    //查询
    @GetMapping("/queryChildByID")
    public ReturnData<Json> queryChildByID(@RequestBody DcModelHVO data){
        Grid grid = new Grid();
        grid.setTotal(Long.valueOf(0));
        grid.setSuccess(false);
        try {
            String pk_corp = SystemUtil.getLoginCorpId();
            List<DcModelBVO> list = dcpzjmbserv.queryByPId(data.getPrimaryKey(), pk_corp);
            if(list != null && list.size() > 0){
                grid.setTotal(Long.valueOf(list.size()));
                grid.setSuccess(true);
                grid.setRows(list);
            }
        } catch (Exception e) {
//            printErrorLog(grid, log, e, "查询子表失败！");
            grid.setSuccess(false);
            grid.setMsg("查询子表失败！");
        }
        return ReturnData.ok().data(grid);
    }

    // 删除
    @PostMapping("/delete")
    public ReturnData<Json> delete(@RequestBody DcModelHVO data){
        Json json = new Json();
        try{
            String pk_corp = SystemUtil.getLoginCorpId();
            dcpzjmbserv.delete(data.getPrimaryKey(), pk_corp);
            json.setSuccess(true);
            json.setMsg("删除成功!");
        }catch(Exception e){
//            printErrorLog(json, log, e, "删除失败!");
            json.setSuccess(false);
            json.setMsg("删除失败!");
        }

//        writeLogRecord(LogRecordEnum.OPE_JITUAN_PZMB.getValue(), "删除数据中心凭证模板", ISysConstants.SYS_0);
        return ReturnData.ok().data(json);
    }

    /**
     * 同步数据，这个不要了
     */
//	public void syncData(){
//		Json json = new Json();
//		try{
//			String isForce = getRequest().getParameter("force");
//			dcpzjmbserv.processSync(getLoginCorpInfo(), isForce);
//			json.setSuccess(true);
//			json.setMsg("同步成功!");
//		}catch(Exception e){
//			printErrorLog(json, log, e, "公司模板未设置，请检查!");
//		}
//
//		writeJson1(json);
//	}
    /**
     * 复制到其它公司，只有会计端才调用
     */
    @PostMapping("/copyTocorp")
    public ReturnData<Json> copyTocorp(@RequestParam("corps") String gs, @RequestParam("parentid") String ids){
        Json json = new Json();
        json.setSuccess(false);
        json.setMsg("复制失败");
        try{
            if(!StringUtil.isEmpty(ids)
                    && !StringUtil.isEmpty(gs)){
                String[] gsss = gs.split(",");
                String[] idss = ids.split(",");
                String userid = SystemUtil.getLoginUserId();
                String corppk = SystemUtil.getLoginCorpId();
                String msg = dcpzjmbserv.copyCorpToCorp(gsss, idss, userid, corppk);
                json.setSuccess(true);
                if(StringUtil.isEmpty(msg)){
                    msg = "复制成功";
                }
                json.setMsg(msg);
            }
        }catch(Exception e){
//            printErrorLog(json, log, e, "复制失败!");
            json.setSuccess(false);
            json.setMsg("复制失败!");
        }
//        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET.getValue(), "复制业务模板到其它公司", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    //查询 供参照使用
    @GetMapping("/queryByRef")
    public ReturnData<Grid> queryByRef(String nohas, String noszcode, String xiaoxiang, String iszhuan){
        Grid grid = new Grid();
        String corpid = SystemUtil.getLoginCorpId();
        try {
            if(!StringUtil.isEmpty(corpid)){
                List<DcModelHVO> list = dcpzjmbserv.query(corpid);

                sortH(list, nohas, noszcode, xiaoxiang );
                list = filterData(list,corpid, nohas, noszcode, xiaoxiang, iszhuan);

                if(list == null || list.size() == 0){
                    list = new ArrayList<DcModelHVO>();
                    grid.setTotal(Long.valueOf(0));
                }else{
                    grid.setTotal(Long.valueOf(list.size()));
                }
                grid.setRows(list);
                grid.setSuccess(true);
            }else{
                grid.setTotal(Long.valueOf(0));
                grid.setSuccess(false);
            }
        } catch (Exception e) {
//            printErrorLog(grid, log, e, "查询失败！");
            grid.setSuccess(false);
            grid.setMsg("查询失败！");
        }

        return ReturnData.ok().data(grid);
    }

    public void sortH(List<DcModelHVO> list,String nohas, String noszcode, String xiaoxiang){
        final Map<String, Integer> stmap = buildBaseMap(nohas, noszcode, xiaoxiang);
        Collections.sort(list, new Comparator<DcModelHVO>() {
            @Override
            public int compare(DcModelHVO o1, DcModelHVO o2) {
                int c1 = IDefaultValue.DefaultGroup.equals(o1.getPk_corp()) ? 0 : 1;
                int c2 = IDefaultValue.DefaultGroup.equals(o2.getPk_corp()) ? 0 : 1;
                int i = c2 - c1;
                if (i == 0) {
                    Integer d1 = stmap.get(o1.getBusitypetempname());
                    Integer d2 = stmap.get(o2.getBusitypetempname());

                    c1 = d1 == null ? 1000 : d1;
                    c2 = d2 == null ? 1000 : d2;

                    i = c1 - c2;

                    if (i == 0) {
                        i = o1.getBusitypetempname().compareTo(o2.getBusitypetempname());
                    }
                }
                return i;
            }
        });
    }

    private Map<String, Integer> buildBaseMap(String nohas, String noszcode, String xiaoxiang) {
        Map<String, Integer> map = new HashMap<String, Integer>();

        if("Y".equals(nohas)){
            map.put("转入", 1);
            map.put("转出", 2);
            map.put("付款个人", 3);
            map.put("员工还款", 4);
            map.put("户间转账", 5);
            map.put("取款", 6);
            map.put("存款", 7);
            map.put("提现", 8);
            map.put("手续费", 9);
            map.put("利息支出", 10);
            map.put("利息收入", 11);
            map.put("公积金", 12);
            map.put("社保", 13);
            map.put("残保金", 14);
            map.put("工资", 15);
            map.put("罚款", 16);
            map.put("缴纳税款", 17);
            map.put("退款", 18);
            map.put("支付承兑汇票", 19);
            map.put("收到承兑汇票", 20);
            map.put("收到津贴", 21);
            map.put("购买理财", 22);
            map.put("理财收入", 23);
            map.put("理财赎回", 24);
            map.put("实收资本", 25);
            map.put("工会经费", 26);
            map.put("分红", 27);
            map.put("定金", 28);
            map.put("押金", 29);
            map.put("收回押金", 30);
            map.put("贷款收入", 31);
            map.put("货币兑换", 32);
            map.put("补贴", 33);
        }else if("Y".equals(noszcode) || "Y".equals(xiaoxiang)){
            map.put("采购原材料", 34);
            map.put("采购库存商品", 35);
            map.put("差旅费", 36);
            map.put("办公费", 37);
            map.put("租赁费", 38);
            map.put("通讯费", 39);
            map.put("车杂费", 40);
            map.put("交通费", 41);
            map.put("开办费", 42);
            map.put("职工福利费", 43);
            map.put("物业费", 44);
            map.put("业务招待费", 45);
            map.put("广告费和业务宣传费", 46);
            map.put("运费", 47);
            map.put("保险费", 48);
            map.put("技术服务费", 49);
            map.put("咨询顾问费", 50);
            map.put("修理费", 51);
            map.put("职工教育经费", 52);
            map.put("包装费", 53);
            map.put("劳务费", 54);
            map.put("研究费用", 55);
            map.put("购入生产设备", 56);
            map.put("销售成本", 57);
            map.put("购入电子设备", 58);
            map.put("采购库存商品待认证", 59);
            map.put("税控器减免", 60);
            map.put("劳务成本", 61);
            map.put("罚款", 62);
            map.put("商品维修费", 63);
            map.put("购入运输工具", 64);
            map.put("购入家具工器具", 65);
            map.put("购入房屋建筑物", 66);
            map.put("其他费用", 67);
        }

        return map;
    }

    private List<DcModelHVO> filterData(List<DcModelHVO> list, String corpid, String nohas, String noszcode,
                                        String xiaoxiang, String iszhuan) {

        /**
         * 行业字段为当前公司所属行业，显示在入账设置最前行 
         * 行业字段为空，显示在之后 
         * 行业字段为非当前公司所属行业，不显示
         */
        CorpVO corpvo = corpserv.queryByPk(corpid);
        List<DcModelHVO> listup = new ArrayList<>();
        List<DcModelHVO> listdown = new ArrayList<>();
        for (DcModelHVO vo : list) {
            if (StringUtil.isEmpty(vo.getPk_trade())) {
                listdown.add(vo);
            } else {
                if (StringUtil.isEmpty(corpvo.getDef20())) {
                    listdown.add(vo);
                } else {
                    if (vo.getPk_trade().equals(corpvo.getDef20())) {
                        listup.add(vo);
                    }
                }
            }
        }
        listup.addAll(listdown);

        DZFBoolean icinv = new DZFBoolean(IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic()));// 启用总账库存
        String keywordcon = null;
        List<DcModelHVO> copylist = new ArrayList<>();
        if ("Y".equals(nohas)) {// 银行对账单
            keywordcon = "中国农业银行";
            for (DcModelHVO vo : listup) {
                String bm = vo.getVspstylecode();
                String mc = vo.getKeywords();
                if (FieldConstant.FPSTYLE_20.equals(bm)) {// 银行收付款回单 按照 票据类型过滤
                    if (!StringUtil.isEmpty(vo.getPk_corp()) && IDefaultValue.DefaultGroup.equals(vo.getPk_corp())) {
                        mc = mc.split("_")[0];
                        if (keywordcon.indexOf(mc) >= 0 || mc.indexOf(keywordcon) >= 0) {
                            copylist.add(vo);
                        }
                    }else{//加入自定义的
                        copylist.add(vo);
                    }
                }
            }
        } else if ("Y".equals(noszcode)) {// 进项
            keywordcon = "其他采购";
            for (DcModelHVO vo : listup) {
                String bm = vo.getVspstylecode();

                if ("Y".equals(iszhuan)) {
                    if (FieldConstant.FPSTYLE_01.equals(bm)) {
                        addfilerJin(vo, icinv, copylist, keywordcon);
                    }
                } else if ("N".equals(iszhuan)) {
                    if (FieldConstant.FPSTYLE_02.equals(bm)) {
                        addfilerJin(vo, icinv, copylist, keywordcon);
                    }
                } else {
                    if (FieldConstant.FPSTYLE_01.equals(bm) || FieldConstant.FPSTYLE_02.equals(bm)) {
                        addfilerJin(vo, icinv, copylist, keywordcon);
                    }
                }
            }
        } else if ("Y".equals(xiaoxiang)) {// 销项
            keywordcon = "其他销售";
            for (DcModelHVO vo : listup) {
                String bm = vo.getVspstylecode();
                if ("Y".equals(iszhuan)) {
                    if (FieldConstant.FPSTYLE_01.equals(bm)) {
                        addfilerXiao(vo, icinv, copylist, keywordcon);
                    }
                } else if ("N".equals(iszhuan)) {
                    if (FieldConstant.FPSTYLE_02.equals(bm)) {
                        addfilerXiao(vo, icinv, copylist, keywordcon);
                    }
                } else {
                    if (FieldConstant.FPSTYLE_01.equals(bm) || FieldConstant.FPSTYLE_02.equals(bm)) {
                        addfilerXiao(vo, icinv, copylist, keywordcon);
                    }
                }
            }
        }else{
            for (DcModelHVO vo : listup) {
                copylist.add(vo);
            }
        }
        return copylist;
    }

    private void addfilerJin(DcModelHVO vo ,DZFBoolean icinv,List<DcModelHVO> copylist,String keywordcon) {
        String mc = vo.getKeywords();
        String code = vo.getSzstylecode();
        if (FieldConstant.FPSTYLE_02.equals(code) || FieldConstant.FPSTYLE_04.equals(code)
                || FieldConstant.FPSTYLE_06.equals(code)) {
            if (icinv.booleanValue()) {
                if(IDefaultValue.DefaultGroup.equals(vo.getPk_corp())){
                    if (mc.indexOf(keywordcon) >= 0)
                        copylist.add(vo);
                }else{
                    copylist.add(vo);
                }
            } else {
                copylist.add(vo);
            }
        }
    }

    private void addfilerXiao(DcModelHVO vo ,DZFBoolean icinv,List<DcModelHVO> copylist,String keywordcon) {
        String mc = vo.getKeywords();
        String code = vo.getSzstylecode();
        if (FieldConstant.FPSTYLE_01.equals(code) || FieldConstant.FPSTYLE_03.equals(code)
                || FieldConstant.FPSTYLE_05.equals(code)) {
            if (icinv.booleanValue()) {
                if(IDefaultValue.DefaultGroup.equals(vo.getPk_corp())){
                    if (mc.indexOf(keywordcon) >= 0)
                        copylist.add(vo);
                }else{
                    copylist.add(vo);
                }
            } else {
                copylist.add(vo);
            }
        }
    }

    public List<DcModelHVO> filterDataCommon(List<DcModelHVO> list, String corpid, String nohas, String noszcode,
                                             String xiaoxiang, String iszhuan){
        list = filterData(list, corpid, nohas, noszcode, xiaoxiang, iszhuan);

        return list;
    }

//    public void writeJson1(Object o){
//        String headvoname = DcModelHVO.class.getName();
//        String bodyname = DcModelBVO.class.getName();
//        String key = headvoname+","+bodyname;
//        Map<String, String> m = FieldMappingCache.getInstance().get(key);
//        if(m == null || m.size() == 0){
//            DcModelHVO hevo = new DcModelHVO();
//            DcModelBVO bvo = new DcModelBVO();
//            m = FieldMapping.getFieldMapping(key,new SuperVO[]{hevo, bvo});
//        }
//        writeJsonByFilter(o,m);
//    }


//    public void impExcel(){
//        String userid = getLoginUserid();
//        Json json = new Json();
//        json.setSuccess(false);
//        try {
//            File[] infiles = ((MultiPartRequestWrapper) getRequest()).getFiles("impfile");
//            if(infiles == null || infiles.length==0){
//                throw new BusinessException("请选择导入文件!");
//            }
//            String[] fileNames = ((MultiPartRequestWrapper) getRequest()).getFileNames("impfile");
//            String fileType = null;
//            if (fileNames != null && fileNames.length > 0) {
//                String fileName = fileNames[0];
//                fileType = fileNames[0].substring(fileName.indexOf(".") + 1, fileName.length());
//            }
//            String pk_corp = getLogincorppk();
//            //
//            String msg = dcpzjmbserv.saveImp(infiles[0],fileType, userid, pk_corp);
//            json.setMsg(msg);
//            json.setSuccess(true);
//        } catch (Exception e) {
//            printErrorLog(json, log, e, "导入失败!");
//        }
//
//        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET.getValue(),"导入业务类型模板", ISysConstants.SYS_2);
//        writeJson(json);
//    }
}
