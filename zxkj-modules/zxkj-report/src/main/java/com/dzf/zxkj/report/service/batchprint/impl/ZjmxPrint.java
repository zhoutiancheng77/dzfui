package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class ZjmxPrint extends  AbstractPrint {

    private IZxkjPlatformService zxkjPlatformService;

    private PrintParamVO printParamVO;

    private KmReoprtQueryParamVO queryparamvo;

    public ZjmxPrint(IZxkjPlatformService zxkjPlatformService, PrintParamVO printParamVO, KmReoprtQueryParamVO queryparamvo) {
        this.zxkjPlatformService = zxkjPlatformService;
        this.printParamVO = printParamVO;
        this.queryparamvo = queryparamvo;
    }

    public byte[] print (BatchPrintSetVo setVo,CorpVO corpVO, UserVO userVO) {
        try {
            // 校验
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, null);
            printReporUtil.setbSaveDfsSer(DZFBoolean.TRUE);
            Map<String, String> pmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            pmap.put("type", printParamVO.getType());
            pmap.put("pageOrt", printParamVO.getPageOrt());
            pmap.put("left", printParamVO.getLeft());
            pmap.put("top", printParamVO.getTop());
            pmap.put("printdate", printParamVO.getPrintdate());
            pmap.put("font", printParamVO.getFont());
            printReporUtil.setIscross(new DZFBoolean(printParamVO.getPageOrt()));
            printReporUtil.setIspaging(printParamVO.getIsPaging());
            AssetDepreciaTionVO[] bodyvos = zxkjPlatformService.getZczjMxVOs(queryparamvo);
            if (bodyvos == null || bodyvos.length ==0) {
                return null;
            }
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(printParamVO.getFont()), Font.NORMAL));//设置表头字体
            Map<String, String> tmap = new LinkedHashMap<String, String>();// 声明一个map用来存title
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getTitleperiod());
            String title = "折 旧 明 细 账";
            //转换map
            Map<String, List<SuperVO>> maps = new LinkedHashMap<>();
            String assetcode = "";
//            for (AssetDepreciaTionVO vo : bodyvos) {
//                assetcode = vo.getAssetcode();
//                if ("0".equals(vo.getAssetproperty()) || "2".equals(vo.getAssetproperty())) {//固定资产
//                    vo.setZy("计提折旧");
//                } else if (!StringUtil.isEmpty(vo.getAssetproperty())) {
//                    vo.setZy("计提摊销");
//                    title = "资 产 摊 销 明 细 账";
//                }
//                if (StringUtil.isEmpty(assetcode)) {
//                    continue;
//                }
//                if (maps.containsKey(assetcode)) {
//                    maps.get(assetcode).add(vo);
//                } else {
//                    List<SuperVO> list = new ArrayList<SuperVO>();
//                    list.add(vo);
//                    maps.put(assetcode, list);
//                }
//            }
            printReporUtil.printHz(maps, bodyvos, title,
                    new String[]{"catename", "assetcode", "assetname", "businessdate",
                            "uselimit", "assetmny", "depreciationmny", "assetnetmny",
                            "originalvalue"},
                    new String[]{"类别", "资产编码", "资产名称", "折旧日期",
                            "预计使用年限", "资产原值", "累计折旧", "资产净值",
                            "本期折旧"},
                    new int[]{3,2,
                            4,3, 3,
                            4, 4, 4, 4}, 20,  pmap, tmap);
            return printReporUtil.getContents();
        } catch (DocumentException e) {
            log.error("折旧明细账打印失败", e);
        } catch (IOException e) {
            log.error("折旧明细账打印失败", e);
        }
        return null;
    }
    private AssetDepreciaTionVO[] filter(AssetDepreciaTionVO[] bodyvos, String xjtotal, String hjtotal) {
        List<AssetDepreciaTionVO> list = new ArrayList<AssetDepreciaTionVO>();
        if (bodyvos != null && bodyvos.length > 0) {
            for (AssetDepreciaTionVO body : bodyvos) {
                if (StringUtil.isEmpty(body.getCatename())
                        && StringUtil.isEmpty(body.getAssetcode())) {
                    if ("小计".equals(body.getAssetname()) && "N".equals(xjtotal)) {
                        continue;
                    }
                    if ("合计".equals(body.getAssetname()) && "N".equals(hjtotal)) {
                        continue;
                    }
                }
                list.add(body);
            }
        }
        return list.toArray(new AssetDepreciaTionVO[0]);
    }
}
