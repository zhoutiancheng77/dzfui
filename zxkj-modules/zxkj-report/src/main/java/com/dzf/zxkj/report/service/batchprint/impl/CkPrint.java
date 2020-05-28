package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.constant.IcConst;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.DZFArrayUtil;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.icset.IntradeParamVO;
import com.dzf.zxkj.platform.model.icset.IntradeoutVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.utils.SystemUtil;
import com.dzf.zxkj.report.utils.VoUtils;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

/**
 * 出库
 */
@Slf4j
public class CkPrint extends AbstractPrint {

    private IZxkjPlatformService zxkjPlatformService;

    private PrintParamVO printParamVO;

    private KmReoprtQueryParamVO queryparamvo;

    public CkPrint(IZxkjPlatformService zxkjPlatformService, PrintParamVO printParamVO, KmReoprtQueryParamVO queryparamvo) {
        this.zxkjPlatformService = zxkjPlatformService;
        this.printParamVO = printParamVO;
        this.queryparamvo = queryparamvo;
    }

    public byte[] print (BatchPrintSetVo setVo,CorpVO corpVO, UserVO userVO) {
        // 老模式 启用库存
        PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, null);
        printReporUtil.setbSaveDfsSer(DZFBoolean.TRUE);
        printReporUtil.setIscross(new DZFBoolean(printParamVO.getPageOrt()));
        Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
        if (corpVO.getIbuildicstyle() == null || corpVO.getIbuildicstyle() != 1) {
            try {
                List<IntradeoutVO> list = null;
                list = zxkjPlatformService.queryTradeOut(queryparamvo);
                IntradeoutVO[] bodyvos = list.toArray(new IntradeoutVO[0]);
                String type = printParamVO.getType();
                Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存title
                tmap.put("公司", corpVO.getUnitname());
                tmap.put("期间", printParamVO.getTitleperiod());
                IntradeoutVO nvo = calTotal(bodyvos);
                bodyvos = DZFArrayUtil.combineArray(bodyvos,new IntradeoutVO[]{nvo});
                setDefaultValue(bodyvos, corpVO.getPk_corp(),queryparamvo.getBegindate1());//为后续设置精度赋值
                printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
                printReporUtil.setLineheight(22F);
                printReporUtil.printHz(new HashMap<>(),bodyvos, "出 库 单",
                        new String[] { "kmmc", "invname", "zy", "invspec","measure", "dbilldate", "nnum",
                                "ncost", "pzh", "memo" },
                        new String[] { "科目", "存货", "摘要", "规格(型号)", "计量单位", "单据日期", "数量", "成本", "凭证号", "备注" },
                        new int[] { 2, 3, 4, 4, 2, 2, 3, 2, 3, 2 }, 20, pmap, tmap);
                return printReporUtil.getContents();
            } catch (DocumentException e) {
                log.error("出库单打印失败", e);
            } catch (IOException e) {
                log.error("出库单打印失败", e);
            }catch (Exception e) {
                log.error("出库单打印失败", e);
            } finally {
            }
        } else{
            try {
                Map<String, String> tmap = new LinkedHashMap<String, String>();// 声明一个map用来存title
                printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体

                String title = "出 库 单";
                String[] columns = new String[] { "cbusitype", "invclassname", "invname", "invspec", "measure",
                        "nnum", "nprice", "ncost" };
                String[] columnnames = new String[] { "出库类型", "存货分类", "存货名称", "规格(型号)", "计量单位", "数量", "成本单价", "成本金额" };
                int[] widths = new int[] { 1, 1, 4, 2, 1, 2, 2, 2 };

                boolean isCombin = false;
                if (printParamVO != null && !StringUtil.isEmpty(printParamVO.getIsmerge())
                        && (printParamVO.getIsmerge().equals("Y") || printParamVO.getIsmerge().equals("true"))) {
                    isCombin = true;
                }
                printReporUtil.setLineheight(22f);
                Map<String,String> invmaps = new HashMap<>();
                if(printParamVO != null && !StringUtil.isEmpty(printParamVO.getIshidepzh())
                        && (printParamVO.getIshidepzh().equals("Y") || printParamVO.getIshidepzh().equals("true"))){
                    invmaps.put("isHiddenPzh","Y");
                }else{
                    invmaps.put("isHiddenPzh","N");
                }
                //会计
                if(!"true".equals(pmap.get("ishidekj"))){
                    pmap.put("会计","");
                }
                //库管员
                if(!"true".equals(pmap.get("ishidekgy"))){
                    pmap.put("库管员",pmap.get("ishidekgyname"));
                }
                Map<String, List<SuperVO>> vomap = getVoMap(printParamVO,queryparamvo.getPk_corp());
                if (vomap == null || vomap.size() == 0) {
                    return null;
                }
                if(pmap.get("type").equals("3")){//发票纸模板打印
                    printReporUtil.printICInvoice(vomap, null, title, columns, columnnames, widths, 20,invmaps, pmap, tmap);
                }else{
                    if (!isCombin) {
                        printReporUtil.printHz(vomap, null, title, columns, columnnames, widths, 20, pmap.get("type"), invmaps,pmap, tmap);
                    } else {
                        if (pmap.get("type").equals("1"))
                            printReporUtil.printGroupCombin(vomap, title, columns, columnnames, null, widths, 20, pmap,invmaps); // A4纸张打印
                        else if (pmap.get("type").equals("2"))
                            printReporUtil.printB5Combin(vomap, title, columns, columnnames, null, widths, 20, pmap,invmaps);
                    }
                }
                return printReporUtil.getContents();
            } catch (DocumentException e) {
                log.error("出库单打印失败", e);
            } catch (IOException e) {
                log.error("出库单打印失败", e);
            } catch (Exception e) {
                log.error("出库单打印失败", e);
            } finally {
            }
        }
        return null;
    }

    private Map<String, List<SuperVO>> getVoMap(PrintParamVO printParamVO,String pk_corp) {
        String priceStr =zxkjPlatformService.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
        int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
        Map<String, List<SuperVO>> vomap = new LinkedHashMap<>();

        AuxiliaryAccountBVO[] fzvos =zxkjPlatformService.queryBByFzlb(pk_corp, AuxiliaryConstant.ITEM_CUSTOMER);
        Map<String, AuxiliaryAccountBVO> aumap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(fzvos), new String[]{"pk_auacount_b"});
        IntradeParamVO paramVO = new IntradeParamVO();
        paramVO.setPk_corp(queryparamvo.getPk_corp());
        paramVO.setBegindate(queryparamvo.getBegindate1());
        paramVO.setEnddate(queryparamvo.getEnddate());
        List<IntradeHVO> list = zxkjPlatformService.queryIntradeHVOOut(paramVO);
        for (IntradeHVO head: list) {
            AuxiliaryAccountBVO custvo = 	aumap.get(head.getPk_cust());
            IntradeHVO newhead = zxkjPlatformService.queryIntradeHVOByID(head.getPrimaryKey(), pk_corp);
            SuperVO[] bodyvos = newhead.getChildren();
            List<SuperVO> alist = new ArrayList<>();
            if (bodyvos ==null || bodyvos.length ==0) {
                continue;
            }
            for (SuperVO body : bodyvos) {
                IntradeoutVO ivo = (IntradeoutVO) body;

                if(DZFValueCheck.isNotEmpty(custvo)){
                    ivo.setCustname(custvo.getName());
                }
                if (StringUtil.isEmpty(ivo.getPk_voucher())) {
                    ivo.setPk_voucher(head.getPzid());
                }
                if (StringUtil.isEmpty(ivo.getPzh())) {
                    ivo.setPzh(head.getPzh());
                }
                ivo.setCreator(head.getCreator());
                ivo.setDbillid(head.getDbillid());
                String cbusitype = ivo.getCbusitype();
                if (StringUtil.isEmpty(cbusitype)) {
                    ivo.setCbusitype("销售出库");
                } else {
                    if (cbusitype.equalsIgnoreCase(IcConst.LLTYPE)) {
                        ivo.setCbusitype("领料出库");
                    } else if (cbusitype.equalsIgnoreCase(IcConst.QTCTYPE)) {
                        ivo.setCbusitype("其他出库");
                    } else {
                        ivo.setCbusitype("销售出库");
                    }
                }
                if (ivo.getNcost() != null) {
                    ivo.setNprice(SafeCompute.div(ivo.getNcost(), ivo.getNnum()).setScale(price, 0));// 设置成本单价
                }
                alist.add(ivo);
            }
            IntradeoutVO nvo = calTotal(bodyvos);
            alist.add(nvo);
            vomap.put(head.getPrimaryKey(), Arrays.asList(alist.toArray(new SuperVO[alist.size()])));
        }
        return vomap;
    }

    private IntradeoutVO calTotal(SuperVO[] bodyvos) {
        // 计算合计行数据
        DZFDouble d1 = DZFDouble.ZERO_DBL;
        DZFDouble d2 = DZFDouble.ZERO_DBL;
        for (SuperVO body : bodyvos) {
            IntradeoutVO ivo = (IntradeoutVO) body;
            d1 = SafeCompute.add(d1, VoUtils.getDZFDouble(ivo.getNcost()).setScale(2, DZFDouble.ROUND_HALF_UP));
            d2 = SafeCompute.add(d2, VoUtils.getDZFDouble(ivo.getNnum()));
        }
        IntradeoutVO nvo = new IntradeoutVO();
        nvo.setKmmc("合计");
        nvo.setNcost(d1);
        nvo.setNnum(d2);
        return nvo;
    }
    private void setDefaultValue(IntradeoutVO[] bodyvos, String pk_corp, DZFDate date){
        if(bodyvos != null && bodyvos.length > 0){
            for(IntradeoutVO vo : bodyvos){
                vo.setPk_corp(pk_corp);
                vo.setDbilldate(date);
            }
        }
    }
}
