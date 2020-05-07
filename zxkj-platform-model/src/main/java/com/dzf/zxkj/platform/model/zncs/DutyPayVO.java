package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

import java.util.List;

/**
 * 完税统计vo
 */
public class DutyPayVO extends SuperVO {
    private String invname;

    private String corpname ;

    private DZFDouble itemmny;

    private String period;
    private String pk_corp;
    private String sourceid;
    private String imgname;

    private List<DutyPayVO> imageInfo;

    private String corps[];

    public String[] getCorps() {
        return corps;
    }

    public void setCorps(String[] corps) {
        this.corps = corps;
    }

    public String getInvname() {
        return invname;
    }

    public void setInvname(String invname) {
        this.invname = invname;
    }

    public String getCorpname() {
        return corpname;
    }

    public void setCorpname(String corpname) {
        this.corpname = corpname;
    }

    public DZFDouble getItemmny() {
        return itemmny;
    }

    public void setItemmny(DZFDouble itemmny) {
        this.itemmny = itemmny;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getSourceid() {
        return sourceid;
    }

    public void setSourceid(String sourceid) {
        this.sourceid = sourceid;
    }

    public String getImgname() {
        return imgname;
    }

    public void setImgname(String imgname) {
        this.imgname = imgname;
    }

    public List<DutyPayVO> getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(List<DutyPayVO>  imageInfo) {
        this.imageInfo = imageInfo;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "DZF_TMP_DUTY";
    }
}
