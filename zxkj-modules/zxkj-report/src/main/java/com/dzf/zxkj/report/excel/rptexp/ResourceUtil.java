package com.dzf.zxkj.report.excel.rptexp;

import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.report.config.ReportConfig;
import com.dzf.zxkj.report.excel.rptexp.enums.ExportTemplateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;

@Slf4j
public class ResourceUtil {

    private static String path;

    public enum ResourceEnum {
        KJ2007ALL("kj_2007_all"),
        KJ2007ZCFZ("kj_2007_zcfz"),
        KJ2007LR("kj_2007_lr"),
        KJ2007XJLL("kj_2007_xjll"),
        KJ2013ALL("kj_2013_all"),
        KJ2013ZCFZ("kj_2013_zcfz"),
        KJ2013LR("kj_2013_lr"),
        KJ2013XJLL("kj_2013_xjll");

        private String value;

        ResourceEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    static {
        ReportConfig reportConfig = SpringUtils.getBean(ReportConfig.class);
        path = reportConfig.getPath();
        if (StringUtil.isEmptyWithTrim(path)) {
            throw new RuntimeException("-----------没有配置税表导出模板路径--------------");
        }
    }


    public static Resource get(ExportTemplateEnum templateEnum, ResourceEnum resourceEnum) {
        File file = new File(path + File.separator + templateEnum.getAreaType() + File.separator + resourceEnum.getValue() + "." + ("0".equals(templateEnum.getFileType()) ? "xls" : "xml"));
        if (file.exists()) {
            return new FileSystemResource(file);
        } else {
            return null;
        }
    }
    public static Resource get(ExportTemplateEnum templateEnum, ResourceEnum resourceEnum,String versionno){
//        String path = ReportConfigUtil.getProperty("report.template.local.path");
        File file = new File(path+File.separator+templateEnum.getAreaType()+File.separator+ resourceEnum.getValue() + versionno + "." + ("0".equals(templateEnum.getFileType()) ? "xls" : "xml") );
        log.info("文件路径>>>>>>>>>>>>>>:"+path+File.separator+templateEnum.getAreaType()+File.separator+ resourceEnum.getValue() + versionno + "." + ("0".equals(templateEnum.getFileType()) ? "xls" : "xml") );
        if(file.exists()){
            return new FileSystemResource(file);
        }else{
            return null;
        }
    }

    public static Resource get(String areaType, String corpType, CwbbType cwbbType) {
        String fileName = getFileSuffix(corpType, cwbbType);
        File file = new File(path + File.separator + areaType + File.separator + fileName + ".xls");
        if (file.exists()) {
            return new FileSystemResource(file);
        } else {
            return null;
        }
    }

    private static String getFileSuffix(String corpType, CwbbType cwbbType) {
        StringBuilder sb = new StringBuilder();
        if (corpType.equals("00000100AA10000000000BMD"))
            sb.append("kj_2013");
        else if (corpType.equals("00000100AA10000000000BMF"))
            sb.append("kj_2007");
        sb.append("_");

        if (cwbbType == CwbbType.ZCFZB)
            sb.append("zcfz");
        else if (cwbbType == CwbbType.LRB)
            sb.append("lr");
        else if (cwbbType == CwbbType.XJLLB)
            sb.append("xjll");
        else
            sb.append("all");

        return sb.toString();
    }
}
