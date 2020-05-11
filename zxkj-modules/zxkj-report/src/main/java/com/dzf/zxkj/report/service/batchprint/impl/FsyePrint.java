package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.cwzb.FsYeController;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class FsyePrint {

    private IFsYeReport gl_rep_fsyebserv;

    private IZxkjPlatformService zxkjPlatformService;

    private PrintParamVO printParamVO;

    private KmReoprtQueryParamVO queryparamvo;

    public FsyePrint(IFsYeReport gl_rep_fsyebserv, IZxkjPlatformService zxkjPlatformService, PrintParamVO printParamVO, KmReoprtQueryParamVO queryparamvo) {
        this.gl_rep_fsyebserv = gl_rep_fsyebserv;
        this.zxkjPlatformService = zxkjPlatformService;
        this.printParamVO = printParamVO;
        this.queryparamvo = queryparamvo;
    }


    public byte[] print(CorpVO corpVO, UserVO userVO) {
        try {
            FsYeController fsYeController = new FsYeController();
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, null);
            printReporUtil.setbSaveDfsSer(DZFBoolean.TRUE);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            /** 是否横向 */
            printReporUtil.setIscross(DZFBoolean.TRUE);
            DZFBoolean isshowfs = queryparamvo.getIshowfs();
            DZFBoolean isxswyewfs = queryparamvo.getXswyewfs();
            DZFBoolean isxswyewfs_bn = DZFBoolean.FALSE;
            FseJyeVO[] bodyvos = gl_rep_fsyebserv.getFsJyeVOs(queryparamvo,1);
            List<FseJyeVO> fsjyevoList = new ArrayList<FseJyeVO>();
            if (bodyvos != null && bodyvos.length > 0) {
                Set<String> conkmids = fsYeController.filterDatas(isshowfs, isxswyewfs, isxswyewfs_bn, bodyvos, queryparamvo);
                for (FseJyeVO fsjye : bodyvos) {// 级次
                    String kmid = fsjye.getPk_km().substring(0, 24);
                    if (conkmids.contains(kmid)) {
                        fsjyevoList.add(fsjye);
                    }
                }
                bodyvos = fsjyevoList.toArray(new FseJyeVO[0]);
            }
            if (bodyvos == null || bodyvos.length == 0) {
                return null;
            }
            fsYeController.putFsyeOnKmlb(bodyvos);
            /** 声明一个map用来存title */
            Map<String, String> tmap = new LinkedHashMap<String, String>();
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getTitleperiod());
            tmap.put("单位", new ReportUtil().getCurrencyDw(queryparamvo.getCurrency()));

            /** 设置表头字体 */
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));
            Object[] obj = null;
            printReporUtil.setLineheight(22f);
            if(!StringUtil.isEmpty(queryparamvo.getPk_currency()) && !queryparamvo.getPk_currency().equals(DzfUtil.PK_CNY)){
                obj = fsYeController.getPrintXm(1);
            }else{
                obj =  fsYeController.getPrintXm(0);
            }
            printReporUtil.printHz(new HashMap<String, List<SuperVO>>() ,bodyvos,"发 生 额 及 余 额 表",(String[])obj[0],
                    (String[])obj[1], (int[])obj[2],(int)obj[3],pmap,tmap);
            return printReporUtil.getContents();
        } catch (DocumentException e) {
            log.error("打印错误",e);
        } catch (IOException e) {
            log.error("打印错误",e);
        }
        return null;
    }
}
