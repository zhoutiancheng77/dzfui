package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.surtax;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.TaxExcelPos;

@TaxExcelPos(reportID = "10516001", reportname = "城建税、教育费附加、地方教育附加税（费）申报表",
        rowBegin = 14, rowEnd = 16, col = 0)
public class SurtaxDetail {
    // 模型序号
    private String mxxh;

    // 征收品目
    private String zspmdm;

    // 征收品目名称
    @TaxExcelPos(col = 0, splitIndex = 1)
    private String zspmmc;

    // 征收项目代码
    private String zsxmdm;

    // 征收项目名称
    @TaxExcelPos(col = 0, splitIndex = 0)
    private String zsxmmc;

    // 一般增值税
    @TaxExcelPos(col = 1)
    private DZFDouble ybzzs;

    // 增值税免抵税额
    @TaxExcelPos(col = 2)
    private DZFDouble zzsmdse;

    // 消费税
    @TaxExcelPos(col = 3)
    private DZFDouble xfs;

    // 营业税
    @TaxExcelPos(col = 4)
    private DZFDouble yys;

    // 合计
    @TaxExcelPos(col = 5)
    private String hj;

    // 税率
    @TaxExcelPos(col = 6)
    private DZFDouble sl1;

    // 本期应纳税（费）额
    @TaxExcelPos(col = 7)
    private DZFDouble bqynsfe;

    // 减免性质代码
    @TaxExcelPos(col = 8, splitIndex = 0)
    private String ssjmxzdm;

    // 减免额
    @TaxExcelPos(col = 9)
    private DZFDouble jme;

    // 增值税小规模纳税人普惠减征额
    @TaxExcelPos(col = 10)
    private DZFDouble phjmse;

    // 试点建设培养产教融合型企业—减免性质代码
    @TaxExcelPos(col = 11, splitIndex = 0)
    private String jmxzdm;
    // 试点建设培养产教融合型企业—减免性质名称
    @TaxExcelPos(col = 11, splitIndex = 1)
    private String jmxzmc;

    // 本期抵免金额
    @TaxExcelPos(col = 12)
    private DZFDouble bqdmje;

    // 本期已缴税额
    @TaxExcelPos(col = 13)
    private DZFDouble bqyjse;

    // 本期应补退税额
    @TaxExcelPos(col = 14)
    private DZFDouble bqybtse;

    // 流水号
    private String lsh;
    // 本期减免税（费）额-减免性质名称
    private String ssjmxzmc;
    // 普惠减征比例
    private DZFDouble phjzbl;
    // 普惠减免性质代码
    private String phjmxzdm;

    // 税务事项代码
    private String swsxdm;
    // 普惠减免税务事项代码
    private String phjmswsxdm;

    public String getZspmdm() {
        if ("市区（增值税附征）".equals(this.zspmmc)) {
            return "101090101";
        } else if ("增值税教育费附加".equals(this.zspmmc)) {
            return "302030100";
        } else if ("增值税地方教育附加".equals(this.zspmmc)) {
            return "302160100";
        }
        return zspmdm;
    }

    public void setZspmdm(String zspmdm) {
        this.zspmdm = zspmdm;
    }

    public String getZspmmc() {
        return zspmmc;
    }

    public void setZspmmc(String zspmmc) {
        this.zspmmc = zspmmc;
    }

    public String getZsxmdm() {
        if ("市区（增值税附征）".equals(this.zspmmc)) {
            return "10109";
        } else if ("增值税教育费附加".equals(this.zspmmc)) {
            return "30203";
        } else if ("增值税地方教育附加".equals(this.zspmmc)) {
            return "30216";
        }
        return zsxmdm;
    }

    public void setZsxmdm(String zsxmdm) {
        this.zsxmdm = zsxmdm;
    }

    public String getZsxmmc() {
        return zsxmmc;
    }

    public void setZsxmmc(String zsxmmc) {
        this.zsxmmc = zsxmmc;
    }

    public DZFDouble getYbzzs() {
        return ybzzs;
    }

    public void setYbzzs(DZFDouble ybzzs) {
        this.ybzzs = ybzzs;
    }

    public DZFDouble getZzsmdse() {
        return zzsmdse;
    }

    public void setZzsmdse(DZFDouble zzsmdse) {
        this.zzsmdse = zzsmdse;
    }

    public DZFDouble getXfs() {
        return xfs;
    }

    public void setXfs(DZFDouble xfs) {
        this.xfs = xfs;
    }

    public DZFDouble getYys() {
        return yys;
    }

    public void setYys(DZFDouble yys) {
        this.yys = yys;
    }

    public String getHj() {
        return hj;
    }

    public void setHj(String hj) {
        this.hj = hj;
    }

    public DZFDouble getSl1() {
        return sl1;
    }

    public void setSl1(DZFDouble sl1) {
        this.sl1 = sl1;
    }

    public DZFDouble getBqynsfe() {
        return bqynsfe;
    }

    public void setBqynsfe(DZFDouble bqynsfe) {
        this.bqynsfe = bqynsfe;
    }

    public String getSsjmxzdm() {
        return ssjmxzdm;
    }

    public void setSsjmxzdm(String ssjmxzdm) {
        this.ssjmxzdm = ssjmxzdm;
    }

    public DZFDouble getJme() {
        return jme;
    }

    public void setJme(DZFDouble jme) {
        this.jme = jme;
    }

    public DZFDouble getPhjmse() {
        return phjmse;
    }

    public void setPhjmse(DZFDouble phjmse) {
        this.phjmse = phjmse;
    }

    public String getJmxzdm() {
        return jmxzdm;
    }

    public void setJmxzdm(String jmxzdm) {
        this.jmxzdm = jmxzdm;
    }

    public String getJmxzmc() {
        return jmxzmc;
    }

    public void setJmxzmc(String jmxzmc) {
        this.jmxzmc = jmxzmc;
    }

    public DZFDouble getBqdmje() {
        return bqdmje;
    }

    public void setBqdmje(DZFDouble bqdmje) {
        this.bqdmje = bqdmje;
    }

    public DZFDouble getBqyjse() {
        return bqyjse;
    }

    public void setBqyjse(DZFDouble bqyjse) {
        this.bqyjse = bqyjse;
    }

    public DZFDouble getBqybtse() {
        return bqybtse;
    }

    public void setBqybtse(DZFDouble bqybtse) {
        this.bqybtse = bqybtse;
    }

    public String getLsh() {
        return lsh;
    }

    public void setLsh(String lsh) {
        this.lsh = lsh;
    }

    public String getSsjmxzmc() {
        return ssjmxzmc;
    }

    public void setSsjmxzmc(String ssjmxzmc) {
        this.ssjmxzmc = ssjmxzmc;
    }

    public DZFDouble getPhjzbl() {
        return phjzbl;
    }

    public void setPhjzbl(DZFDouble phjzbl) {
        this.phjzbl = phjzbl;
    }

    public String getPhjmxzdm() {
        return phjmxzdm;
    }

    public void setPhjmxzdm(String phjmxzdm) {
        this.phjmxzdm = phjmxzdm;
    }

    public String getMxxh() {
        return mxxh;
    }

    public void setMxxh(String mxxh) {
        this.mxxh = mxxh;
    }

    public String getSwsxdm() {
        return swsxdm;
    }

    public void setSwsxdm(String swsxdm) {
        this.swsxdm = swsxdm;
    }

    public String getPhjmswsxdm() {
        return phjmswsxdm;
    }

    public void setPhjmswsxdm(String phjmswsxdm) {
        this.phjmswsxdm = phjmswsxdm;
    }
}
