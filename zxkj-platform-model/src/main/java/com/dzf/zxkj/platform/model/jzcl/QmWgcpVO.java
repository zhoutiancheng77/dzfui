package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zpm
 * @Description:
 * @Date:Created by 2019/12/02
 * @Modified By:
 */
public class QmWgcpVO extends SuperVO {

    /**
     *
     */
    private static final long serialVersionUID = -7348342767518464795L;

    private String pk_inventory;
    private String vname;
    private String vcode = null;
    private DZFDouble ncailiao_qc;
    private DZFDouble nrengong_qc;
    private DZFDouble nzhizao_qc;
    private DZFDouble ncailiao_fs;
    private DZFDouble nrengong_fs;
    private DZFDouble nzhizao_fs;
    private DZFDouble ncailiao_wg;
    private DZFDouble nrengong_wg;
    private DZFDouble nzhizao_wg;
    private DZFDouble nnum_wg;// 数量
    private DZFDouble ncailiao_nwg;
    private DZFDouble nrengong_nwg;
    private DZFDouble nzhizao_nwg;
    private DZFDouble wgbl;// 完工比例
    private DZFDouble price;// 单价
    private DZFDouble mny;// 金额=单价*数量
    private DZFBoolean ispercent;

    /*********不启用工业****************/
    private String kmmc;
    private String kmid;
    private String fzid;
    private String kmbm;

    public String getPk_inventory() {
        return pk_inventory;
    }

    public void setPk_inventory(String pk_inventory) {
        this.pk_inventory = pk_inventory;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public DZFDouble getNcailiao_qc() {
        return ncailiao_qc;
    }

    public void setNcailiao_qc(DZFDouble ncailiao_qc) {
        this.ncailiao_qc = ncailiao_qc;
    }

    public DZFDouble getNrengong_qc() {
        return nrengong_qc;
    }

    public void setNrengong_qc(DZFDouble nrengong_qc) {
        this.nrengong_qc = nrengong_qc;
    }

    public DZFDouble getNzhizao_qc() {
        return nzhizao_qc;
    }

    public void setNzhizao_qc(DZFDouble nzhizao_qc) {
        this.nzhizao_qc = nzhizao_qc;
    }

    public DZFDouble getNcailiao_fs() {
        return ncailiao_fs;
    }

    public void setNcailiao_fs(DZFDouble ncailiao_fs) {
        this.ncailiao_fs = ncailiao_fs;
    }

    public DZFDouble getNrengong_fs() {
        return nrengong_fs;
    }

    public void setNrengong_fs(DZFDouble nrengong_fs) {
        this.nrengong_fs = nrengong_fs;
    }

    public DZFDouble getNzhizao_fs() {
        return nzhizao_fs;
    }

    public void setNzhizao_fs(DZFDouble nzhizao_fs) {
        this.nzhizao_fs = nzhizao_fs;
    }

    public DZFDouble getNcailiao_wg() {
        return ncailiao_wg;
    }

    public void setNcailiao_wg(DZFDouble ncailiao_wg) {
        this.ncailiao_wg = ncailiao_wg;
    }

    public DZFDouble getNrengong_wg() {
        return nrengong_wg;
    }

    public void setNrengong_wg(DZFDouble nrengong_wg) {
        this.nrengong_wg = nrengong_wg;
    }

    public DZFDouble getNzhizao_wg() {
        return nzhizao_wg;
    }

    public void setNzhizao_wg(DZFDouble nzhizao_wg) {
        this.nzhizao_wg = nzhizao_wg;
    }

    public DZFDouble getNnum_wg() {
        return nnum_wg;
    }

    public void setNnum_wg(DZFDouble nnum_wg) {
        this.nnum_wg = nnum_wg;
    }

    public DZFDouble getNcailiao_nwg() {
        return ncailiao_nwg;
    }

    public void setNcailiao_nwg(DZFDouble ncailiao_nwg) {
        this.ncailiao_nwg = ncailiao_nwg;
    }

    public DZFDouble getNrengong_nwg() {
        return nrengong_nwg;
    }

    public void setNrengong_nwg(DZFDouble nrengong_nwg) {
        this.nrengong_nwg = nrengong_nwg;
    }

    public DZFDouble getNzhizao_nwg() {
        return nzhizao_nwg;
    }

    public void setNzhizao_nwg(DZFDouble nzhizao_nwg) {
        this.nzhizao_nwg = nzhizao_nwg;
    }

    public DZFDouble getPrice() {
        return price;
    }

    public void setPrice(DZFDouble price) {
        this.price = price;
    }

    public DZFDouble getMny() {
        return mny;
    }

    public void setMny(DZFDouble mny) {
        this.mny = mny;
    }

    public DZFDouble getWgbl() {
        return wgbl;
    }

    public void setWgbl(DZFDouble wgbl) {
        this.wgbl = wgbl;
    }

    public String getKmmc() {
        return kmmc;
    }

    public String getKmid() {
        return kmid;
    }

    public String getFzid() {
        return fzid;
    }

    public String getKmbm() {
        return kmbm;
    }

    public void setKmmc(String kmmc) {
        this.kmmc = kmmc;
    }

    public void setKmid(String kmid) {
        this.kmid = kmid;
    }

    public void setFzid(String fzid) {
        this.fzid = fzid;
    }

    public void setKmbm(String kmbm) {
        this.kmbm = kmbm;
    }


    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public DZFBoolean getIspercent() {
        return ispercent;
    }

    public void setIspercent(DZFBoolean ispercent) {
        this.ispercent = ispercent;
    }

    public static Map<Integer, String> getExcelFieldColumn() {
        Map<Integer, String> fieldColumn = new HashMap<Integer, String>();
        fieldColumn.put(0, "vcode");
        fieldColumn.put(1, "vname");
        fieldColumn.put(2, "wgbl");
        fieldColumn.put(3, "ncailiao_qc");
        fieldColumn.put(4, "nrengong_qc");
        fieldColumn.put(5, "nzhizao_qc");
        fieldColumn.put(6, "ncailiao_fs");
        fieldColumn.put(7, "nrengong_fs");
        fieldColumn.put(8, "nzhizao_fs");
        fieldColumn.put(9, "ncailiao_wg");
        fieldColumn.put(10, "nrengong_wg");
        fieldColumn.put(11, "nzhizao_wg");
        fieldColumn.put(12, "nnum_wg");
        fieldColumn.put(13, "ncailiao_nwg");
        fieldColumn.put(14, "nrengong_nwg");
        fieldColumn.put(15, "nzhizao_nwg");
        return fieldColumn;
    }

    @Override
    public String getPKFieldName() {
        return null;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }

}