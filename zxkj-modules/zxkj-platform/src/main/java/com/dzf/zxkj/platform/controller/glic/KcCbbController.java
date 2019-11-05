package com.dzf.zxkj.platform.controller.glic;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.utils.VOSortUtils;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.glic.IcDetailVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.jzcl.TempInvtoryVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.glic.IKcCbb;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 库存成本表
 * @author wangzhn
 *
 */
@RestController
@RequestMapping("/glic/gl_rep_kcccbact")
@Slf4j
public class KcCbbController extends GlicReportController{
    @Autowired
    private IKcCbb ic_rep_cbbserv;

    @Autowired
    private IInventoryAccSetService gl_ic_invtorysetserv;

    @GetMapping("/query")
    public ReturnData<Json> queryAction(@RequestParam Map<String, String> param){
        ReportDataGrid grid = new ReportDataGrid();
        QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
        checkPowerDate(queryParamvo);
        Map<String, IcDetailVO> result = ic_rep_cbbserv.queryDetail(queryParamvo,SystemUtil.getLoginCorpVo());
        //将查询后的数据分页展示
        List<IcDetailVO> list = getPagedMXZVos( result, queryParamvo.getPage(),queryParamvo.getRows() , grid);
        grid.setKcDetail(list);
        grid.setRows(list);
        grid.setSuccess(true);
        return ReturnData.ok().data(grid);
    }

    private List<IcDetailVO> getPagedMXZVos(Map<String, IcDetailVO> result, int page, int rows,
                                            ReportDataGrid grid){
        List<IcDetailVO> spList = new ArrayList<>();
        Set<Map.Entry<String, IcDetailVO>> entrySet = result.entrySet();
        Iterator<Map.Entry<String, IcDetailVO>> iter = entrySet.iterator();
        while (iter.hasNext()) {
            Map.Entry<String, IcDetailVO> entry = iter.next();
            spList.add(entry.getValue());
        }
        if(spList != null && spList.size() > 0){
            int start= (page-1)*rows;
            for(int i = start ; i < page * rows && i < spList.size(); i++){
                spList.add(spList.get(i));
            }
            grid.setTotal((long)spList.size());
        }else{
            grid.setTotal(0L);
        }
        return spList;
    }
    @GetMapping("/showzg")
    public ReturnData<Json> showzg(){
        Grid grid = new Grid();
            CorpVO corpvo = SystemUtil.getLoginCorpVo();
            String userid = SystemUtil.getLoginUserId();
            DZFDate doped = new DZFDate(SystemUtil.getLoginDate());

            List<TempInvtoryVO> cvos = ic_rep_cbbserv.queryZgVOs(corpvo, userid, doped);
            if(cvos != null && cvos.size() > 1){
                Collections.sort(cvos, new Comparator<TempInvtoryVO>() {
                    @Override
                    public int compare(TempInvtoryVO o1, TempInvtoryVO o2) {
                        return VOSortUtils.compareContainsNull(o1.getKmbm(), o2.getKmbm());
                    }
                });
            }
            grid.setRows(cvos== null ? new ArrayList<TempInvtoryVO>() : cvos);
            grid.setTotal(cvos == null ? 0L : (long)cvos.size());
            grid.setSuccess(true);
        return ReturnData.ok().data(grid);
    }

    @GetMapping("/adjustKc")
    public ReturnData adjustKc( @RequestParam("period") String period){
        Json json = new Json();
        if(StringUtil.isEmpty(period))
            throw new BusinessException("期间数据为空,请检查");
        String pk_corp =SystemUtil.getLoginCorpId() ;
        TzpzHVO headvo = ic_rep_cbbserv.queryJzPz(pk_corp, period);
        boolean flag = true;
        if(headvo == null){
            flag = false;
        }
        json.setSuccess(flag);
        json.setHead(headvo);
        json.setMsg(flag ? "查询成功" : period + "没有结转凭证，请检查");
        return ReturnData.ok().data(json);
    }

//	private List<TempInvtoryVO> tempInvtory(List<TempInvtoryVO> result){
//		if(result == null || result.size() == 0)
//			return result;
//		IcDetailVO dvo;
//		TempInvtoryVO tvo;
//		for(Map.Entry<String, IcDetailVO> entry : result.entrySet()){
//			dvo = entry.getValue();
//			tvo = new TempInvtoryVO();
//
//			tvo.setId(UUID.randomUUID().toString());
//			tvo.setPk_invtory(dvo.getPk_sp());
//			tvo.setSpfl(dvo.getSpfl());
//			tvo.setSpbm(dvo.getSpbm());
//			tvo.setInvname(dvo.getSpmc());
//			tvo.setSpgg(dvo.getSpgg());
//			tvo.setJldw(dvo.getJldw());
//			tvo.setNnumber_old(dvo.getJcsl());
//			tvo.setNnumber(SafeCompute.sub(DZFDouble.ZERO_DBL, tvo.getNnumber_old()));
//			tvo.setNprice(dvo.getZgdj());
//			tvo.setNmny(SafeCompute.multiply(tvo.getNnumber(), tvo.getNprice()));
//
//			list.add(tvo);
//		}
//
//		if(list.size() > 1){
//			Collections.sort(list, new Comparator<TempInvtoryVO>() {
//
//				@Override
//				public int compare(TempInvtoryVO o1, TempInvtoryVO o2) {
//					return o1.getSpbm().compareTo(o2.getSpbm());
//				}
//			});
//		}
//
//		return result;
//	}

    @PostMapping("/zgsave")
    public ReturnData zgsave(@RequestBody Map<String, String> map){
        Grid grid = new Grid();
        //处理暂估
        String period = "";
        String zg = map.get("zg");
        if(StringUtil.isEmpty(zg))
            throw new BusinessException("暂估数据不全，请检查");
        zg = zg.replace("}{", "},{");
        zg = "[" + zg + "]";
        TempInvtoryVO[] bodyvos = JsonUtils.deserialize(zg, TempInvtoryVO[].class);
        period = bodyvos[0].getPeriod();
        ic_rep_cbbserv.saveZg(bodyvos, SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId());
        grid.setSuccess(true);
        grid.setMsg("暂估处理成功");
//        //日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL.getValue(),
//                "库存成本表:存货暂估期间“" + period + "”存货数据", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    public void printAction() {
//        String period = "";//期间
//        try {
//            PrintParamVO printvo = new PrintParamVO();
//            printvo = (PrintParamVO) DzfTypeUtils.cast(getRequest(), printvo);
//
//            String strlist = getRequest().getParameter("list");
//            String type = getRequest().getParameter("type");
//            String pageOrt = getRequest().getParameter("pageOrt");
//            String left = getRequest().getParameter("left");
//            String top = getRequest().getParameter("top");
//            String printdate = getRequest().getParameter("printdate");
//            String font = getRequest().getParameter("font");
//            String pageNum = getRequest().getParameter("pageNum");
//            Map<String, String> pmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
//            pmap.put("type", type);
//            pmap.put("pageOrt", pageOrt);
//            pmap.put("left", left);
//            pmap.put("top", top);
//            pmap.put("printdate", printdate);
//            pmap.put("font", font);
//            pmap.put("pageNum", pageNum);
//            if (strlist == null) {
//                return;
//            }
//            if (pageOrt.equals("Y")) {
//                setIscross(DZFBoolean.TRUE);// 是否横向
//            } else {
//                setIscross(DZFBoolean.FALSE);// 是否横向
//            }
//            JSONArray array = (JSONArray) JSON.parseArray(strlist);
//            Map<String, String> bodymapping = FieldMapping.getFieldMapping(new IcDetailVO());
//            IcDetailVO[] bodyvos = DzfTypeUtils.cast(array, bodymapping, IcDetailVO[].class,
//                    JSONConvtoJAVA.getParserConfig());
//
////			Map<String, List<SuperVO>> mxmap = new HashMap<String, List<SuperVO>>();
////			mxmap = reloadVOs(bodyvos, getQueryParamVO());
//            period = bodyvos[0].getTitlePeriod();
//            String gs = bodyvos[0].getGs();
//            bodyvos = queryVos(getQueryParamVO());
//
//
//            Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
//            tmap.put("公司", gs);
//            tmap.put("期间", period);
//
//            String corp = (String) getRequest().getSession().getAttribute(IGlobalConstants.login_corp);
//            CorpVO corpvo = CorpCache.getInstance().get(null, corp);
//
//            array = JSON.parseArray(printvo.getColumnslist());
//            Map<String,String> columnres=FieldMapping.getFieldMapping(new ColumnCellAttr());
//            ColumnCellAttr[] columncellattrvos = DzfTypeUtils.cast(array,columnres,
//                    ColumnCellAttr[].class, JSONConvtoJAVA.getParserConfig());
//
//            setTableHeadFount(new Font(getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));
//
//            setDefaultValue(bodyvos, getLogincorppk());//为后续设置精度赋值
//
//            //设置精度
//            String strnum = getRequest().getParameter("numpre");
//            String strprice = getRequest().getParameter("pricepre");
//            precisionMap = new HashMap<>();
//            if(!StringUtil.isEmpty(strnum)){
//                precisionMap.put(IParameterConstants.DZF009, strnum);//数量
//            }
//            if(!StringUtil.isEmpty(strprice)){
//                precisionMap.put(IParameterConstants.DZF010, strprice);//单价
//            }
//            //初始化表体列编码和列名称
//            printReport(bodyvos,"库存成本表", Arrays.asList(columncellattrvos),15,pmap.get("type"),pmap,tmap);
//        } catch (DocumentException e) {
//            log.error("打印错误", e);
//        } catch (IOException e) {
//            log.error("打印错误", e);
//        }
//
//        //日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL.getValue(),
//                "库存成本表:打印期间“" + period + "”存货数据", ISysConstants.SYS_2);
    }

    // 导出excel
    public void excelReport() {

//        HttpServletResponse response = getResponse();
//        OutputStream toClient = null;
//        String qj = "";
//        try {
//            String strlist = getRequest().getParameter("list");
//            String strnum = getRequest().getParameter("numpre");
//            String strprice = getRequest().getParameter("pricepre");
//            String strclassif = getRequest().getParameter("classif");
//
//            JSONArray array = (JSONArray) JSON.parseArray(strlist);
//            Map<String, String> bodymapping = FieldMapping.getFieldMapping(new IcDetailVO());
//            IcDetailVO[] vo = DzfTypeUtils.cast(array, bodymapping, IcDetailVO[].class, JSONConvtoJAVA.getParserConfig());
//            String gs = vo[0].getGs();
//            qj = vo[0].getTitlePeriod();
//            vo = queryVos(getQueryParamVO());
//
////			vo = reloadExcelData(getQueryParamVO());
//            Excelexport2003<IcDetailVO> lxs = new Excelexport2003<IcDetailVO>();
//
//            String pk_corp = getLogincorppk();
////			String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
////			String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
////			int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
////			int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
//
//            int num = StringUtil.isEmpty(strnum) ? 4 : Integer.parseInt(strnum);
//            int price = StringUtil.isEmpty(strprice) ? 4 : Integer.parseInt(strprice);
//
//            CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
//            KccbNewExcelField xsz = new KccbNewExcelField(num, price);
//
////			InventorySetVO setvo = gl_ic_invtorysetserv.query(pk_corp);
////			boolean flag = setvo != null && setvo.getChcbjzfs() == 1 ? true : false;
//            DZFBoolean flag = new DZFBoolean(strclassif);
//
//            xsz.setFields(flag.booleanValue() ? xsz.getFields2() : xsz.getFields1());
//            xsz.setIcDetailVos(vo);
//            xsz.setQj(qj);
//            xsz.setCreator(getLoginUserInfo().getUser_name());
//            xsz.setCorpName(gs);
//            response.reset();
//            String filename = xsz.getExcelport2003Name();
//            String formattedName = URLEncoder.encode(filename, "UTF-8");
//            response.addHeader("Content-Disposition",
//                    "attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
//            toClient = new BufferedOutputStream(response.getOutputStream());
//            response.setContentType("application/vnd.ms-excel;charset=gb2312");
//            lxs.exportExcel(xsz, toClient);
//            toClient.flush();
//            response.getOutputStream().flush();
//        } catch (IOException e) {
//            log.error("excel导出错误", e);
//        } finally {
//            try {
//                if (toClient != null) {
//                    toClient.close();
//                }
//            } catch (IOException e) {
//                log.error("excel导出错误", e);
//            }
//            try {
//                if (response!=null && response.getOutputStream() != null) {
//                    response.getOutputStream().close();
//                }
//            } catch (IOException e) {
//                log.error("excel导出错误", e);
//            }
//        }
//
//        //日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL.getValue(),
//                "库存成本表:导出期间“" + qj + "”存货数据", ISysConstants.SYS_2);
    }

    @GetMapping("/getClassifyFlag")
    public ReturnData getClassifyFlag(){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        InventorySetVO vo = gl_ic_invtorysetserv.query(pk_corp);

        boolean flag = true;
        if(vo != null && vo.getChcbjzfs() == 1){
            flag = false;
        }
        json.setSuccess(true);
        json.setHead(flag);
        json.setMsg("查询成功");
        return ReturnData.ok().data(json);
    }
}
