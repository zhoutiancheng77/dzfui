package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFNumberUtil;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.base.utils.ValueUtils;
import com.dzf.zxkj.common.enums.SalaryReportEnum;
import com.dzf.zxkj.common.enums.SalaryTypeEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportColumn;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.utils.SystemUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.LinkedMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GzbPrint extends AbstractPrint {

    private IZxkjPlatformService zxkjPlatformService;

    private PrintParamVO printParamVO;

    private KmReoprtQueryParamVO queryparamvo;

    public GzbPrint(IZxkjPlatformService zxkjPlatformService, PrintParamVO printParamVO, KmReoprtQueryParamVO queryparamvo) {
        this.zxkjPlatformService = zxkjPlatformService;
        this.printParamVO = printParamVO;
        this.queryparamvo = queryparamvo;
    }

    public byte[] print (BatchPrintSetVo setVo,CorpVO corpVO, UserVO userVO) {
        String opdate = null;
        String beginPeriod = null;
        String endPeriod = null;
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, null);

            printReporUtil.setbSaveDfsSer(DZFBoolean.TRUE);

            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);

            String billtype = "01";
            String pk_corp = queryparamvo.getPk_corp();
            String[] periods = new String []{DateUtils.getPeriod(queryparamvo.getBegindate1()),
                    DateUtils.getPeriod(queryparamvo.getEnddate())};

            if(periods == null || periods.length !=2){
                throw new BusinessException("传入期间出错");
            }
            beginPeriod = periods[0];
            if (StringUtil.isEmpty(beginPeriod))
                throw new BusinessException("期间为空");
            endPeriod = periods[1];
            if (StringUtil.isEmpty(endPeriod))
                throw new BusinessException("期间为空");
            SalaryReportVO[] bodyvos = zxkjPlatformService.queryGzb(pk_corp, beginPeriod,endPeriod, billtype);
            if (bodyvos == null || bodyvos.length == 0)
                return null;
            for (SalaryReportVO vo : bodyvos) {
                vo.setZjlx(SalaryReportEnum.getTypeEnumByValue(vo.getZjlx()).getName());
            }
            CorpVO cvo = zxkjPlatformService.queryCorpByPk(pk_corp);
            bodyvos[0].setPk_corp(cvo.getUnitname());// 用pk_corp属性传递
            // 公司名称
            Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存title参数
            tmap.put("公司", cvo.getUnitname());
            tmap.put("期间", bodyvos[0].getQj());
            String hiddenphone = pmap.get("hiddenphone");
            String zbr = pmap.get("zbr");
            if (!StringUtil.isEmpty(zbr) && new DZFBoolean(zbr).booleanValue()) {
                tmap.put("制表人", SystemUtil.getLoginUserVo().getUser_name());
            }
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
            List<Integer> hiddenColList = getHiddenColumn(billtype);
            if (!StringUtil.isEmpty(hiddenphone) && new DZFBoolean(hiddenphone).booleanValue()) {
                // 隐藏手机号
                hiddenColList.add(1);
            }

            printReporUtil.setLineheight(22F);
            String[] columns = SalaryReportColumn.getCodes(hiddenColList);
            String[] columnNames = SalaryReportColumn.getNames(hiddenColList, billtype);
            int[] widths = SalaryReportColumn.getWidths(hiddenColList);
            for (int i = 0; i < columnNames.length; i++) {
                if ("费用科目".equals(columnNames[i])) {
                    columnNames = deleteArr(i, columnNames);
                    columns = deleteArr(i, columns);
                    widths = deleteArr(i, widths);
                    break;
                }
            }
            if (pmap.get("type").equals("4")) {
//                printReporUtil.setRotate(DZFBoolean.TRUE);
            }
            Map<String, List<SuperVO>> smap = getMapSaraly(bodyvos);
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(printParamVO.getFont()), Font.NORMAL));//设置表头字体
            printReporUtil.setIscross(DZFBoolean.TRUE);
            printReporUtil.printHz(smap,null,
                    "工 资 表(" + SalaryTypeEnum.getTypeEnumByValue(billtype).getName() + ")", columns, columnNames,
                    widths, 60, pmap, tmap);
            return printReporUtil.getContents();
        } catch (DocumentException e) {
            log.error("工资表打印失败!", e);
        } catch (IOException e) {
            log.error("工资表打印失败!", e);
        } catch (Exception e) {
            log.error("工资表打印失败!", e);
        }finally {
        }
        return null;
    }

    private Map<String,List<SuperVO>> getMapSaraly( SalaryReportVO[] bodyvos){

        Map<String,List<SuperVO>> map = new LinkedMap();
        VOUtil.ascSort(bodyvos, new String[] { "qj" });
        String key = "";
        List<SuperVO> list = null;
        for (SalaryReportVO vo : bodyvos) {
            key = vo.getQj();
            if(map.containsKey(key)){
                list = map.get(key);
            }else{
                list = new ArrayList<>();
            }
            list.add(vo);
            map.put(key,list);
        }
        map.forEach((ke1, value) -> {
            SalaryReportVO nvo = calTotal(value.toArray(new SalaryReportVO[value.size()]));
            value.add(nvo);
        });
        return map;
    }

    private SalaryReportVO calTotal(SalaryReportVO[] vos) {
        // 计算合计行数据
        String[] columns = {"yfgz", "yanglaobx", "yiliaobx", "shiyebx", "zfgjj", "ynssde", "grsds", "sfgz", "znjyzc",
                "jxjyzc", "zfdkzc", "zfzjzc", "sylrzc", "ljsre", "ljznjyzc", "ljjxjyzc", "ljzfdkzc", "ljzfzjzc",
                "ljsylrzc", "ljynse", "yyjse", "ljzxkc", "qyyanglaobx", "qyyiliaobx", "qyshiyebx", "qyzfgjj", "qygsbx",
                "qyshybx"};

        SalaryReportVO nvo = new SalaryReportVO();
        nvo.setYgbm("合计");

        for (String column : columns) {
            DZFDouble d1 = DZFDouble.ZERO_DBL;
            for (SalaryReportVO svo : vos) {
                if (svo.getYfgz() == null)
                    svo.setYfgz(DZFDouble.ZERO_DBL);
                d1 = SafeCompute.add(d1,
                        DZFNumberUtil.toNotNullValue(ValueUtils.getDZFDouble(svo.getAttributeValue(column))).setScale(2,
                                DZFDouble.ROUND_HALF_UP));
                nvo.setAttributeValue(column, d1);
            }
        }
        return nvo;
    }

    private int[] deleteArr(int index, int array[]) {
        // 数组的删除其实就是覆盖前一位
        int[] arrNew = new int[array.length - 1];
        for (int i = index; i < array.length - 1; i++) {
            array[i] = array[i + 1];
        }
        System.arraycopy(array, 0, arrNew, 0, arrNew.length);
        return arrNew;
    }

    private String[] deleteArr(int index, String array[]) {
        // 数组的删除其实就是覆盖前一位
        String[] arrNew = new String[array.length - 1];
        for (int i = index; i < array.length - 1; i++) {
            array[i] = array[i + 1];
        }
        System.arraycopy(array, 0, arrNew, 0, arrNew.length);
        return arrNew;
    }

    private List<Integer> getHiddenColumn(String billtype) {
        List<Integer> list = new ArrayList<>();
        int[] hidenCol = null;
        if (billtype.equals(SalaryTypeEnum.REMUNERATION.getValue())) {
            hidenCol = SalaryReportColumn.LWHIDEN;
        } else if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
            hidenCol = SalaryReportColumn.WJGZHIDEN;
        } else if (billtype.equals(SalaryTypeEnum.ANNUALBONUS.getValue())) {
            hidenCol = SalaryReportColumn.NZJHIDEN;
        } else if (billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
            hidenCol = SalaryReportColumn.ZCHIDEN;
        }

        if (hidenCol == null || hidenCol.length == 0)
            return list;
        for (int col : hidenCol) {
            list.add(col);
        }
        return list;
    }

}
