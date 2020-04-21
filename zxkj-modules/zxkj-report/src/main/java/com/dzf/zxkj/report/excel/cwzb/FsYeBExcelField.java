package com.dzf.zxkj.report.excel.cwzb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.MuiltSheetAndTitleExceport;
import com.dzf.zxkj.excel.param.TitleColumnExcelport;
import com.dzf.zxkj.excel.param.UnitExceport;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 发生额余额表导出配置
 *
 * @author zhangj
 *
 */
public class FsYeBExcelField  extends MuiltSheetAndTitleExceport<FseJyeVO> implements UnitExceport {
    private FseJyeVO[] vos = null;

    private List<FseJyeVO[]> allsheetvos = null;

    private String nodename = null;

    private String pk_currency;//币种

    private String currencyname;//币种

    private String[] periods = null;

    private String[] allsheetname = null;

    private String qj = null;

    private String now = DZFDate.getDate(new Date()).toString();

    private String creator = null;

    private String corpname = null;

    public FsYeBExcelField(String nodename, String pk_currency, String currencyname, String qj, String corpname) {
        this.nodename = nodename;
        this.pk_currency = pk_currency;
        this.currencyname = currencyname;
        this.qj = qj;
        this.corpname = corpname;
    }

    public FsYeBExcelField(String nodename, String pk_currency, String currencyname, String[] periods, String[] allsheetname, String qj, String corpname) {
        this.nodename = nodename;
        this.pk_currency = pk_currency;
        this.currencyname = currencyname;
        this.periods = periods;
        this.allsheetname = allsheetname;
        this.qj = qj;
        this.corpname = corpname;
    }

    private Fieldelement[] getFileWbs() {
        List<Fieldelement> list = new ArrayList<Fieldelement>();
        list.add( new Fieldelement("kmlb", "科目类别", false, 0, true));
        list.add( new Fieldelement("kmbm", "科目编码", false, 0, true));
        list.add(new Fieldelement("kmmc", "科目名称", false, 0, true,80,false));
        Fieldelement qcelement = new Fieldelement("", "期初余额", true, 2, true, 1, 4);
        qcelement.setChilds(new Fieldelement[] {
                new Fieldelement("ybqcjf", "借方(原币)", true, 4, true),
                new Fieldelement("qcjf", "借方(本位币)", true, 2, true),
                new Fieldelement("ybqcdf", "贷方(原币)", true, 4, true),
                new Fieldelement("qcdf", "贷方(本位币)", true, 2, true) });
        list.add(qcelement);
        Fieldelement bqelement = new Fieldelement("", "本期发生额", true, 2, true, 1, 4);
        bqelement.setChilds(new Fieldelement[] {
                new Fieldelement("ybfsjf", "借方(原币)", true, 4, true),
                new Fieldelement("fsjf", "借方(本位币)", true, 2, true),
                new Fieldelement("ybfsdf", "贷方(原币)", true, 4, true),
                new Fieldelement("fsdf", "贷方(本位币)", true, 2, true) });
        list.add(bqelement);
        Fieldelement bnelement = new Fieldelement("", "本年发生额", true, 2, true, 1, 4);
        bnelement.setChilds(new Fieldelement[] {
                new Fieldelement("ybjftotal", "借方(原币)", true, 4, true),
                new Fieldelement("jftotal", "借方(本位币)", true, 2, true),
                new Fieldelement("ybdftotal", "贷方(原币)", true, 4, true),
                new Fieldelement("dftotal", "贷方(本位币)", true, 2, true) });
        list.add(bnelement);
        Fieldelement yeelement = new Fieldelement("", "期末余额", true, 2, true, 1, 4);
        yeelement.setChilds(new Fieldelement[] {
                new Fieldelement("ybqmjf", "借方(原币)", true, 4, true),
                new Fieldelement("qmjf", "借方(本位币)", true, 2, true),
                new Fieldelement("ybqmdf", "贷方(原币)", true, 4, true),
                new Fieldelement("qmdf", "贷方(本位币)", true, 2, true) });
        list.add(yeelement);
        return list.toArray(new Fieldelement[0]);
    }

    private Fieldelement[] getFileNoWbs() {
        List<Fieldelement> list = new ArrayList<Fieldelement>();
        list.add( new Fieldelement("kmlb", "科目类别", false, 0, true));
        list.add( new Fieldelement("kmbm", "科目编码", false, 0, true));
        list.add(new Fieldelement("kmmc", "科目名称", false, 0, true,80,false));
        Fieldelement qcelement = new Fieldelement("", "期初余额", true, 2, true, 1, 2);
        qcelement.setChilds(
                new Fieldelement[] { new Fieldelement("qcjf", "借方", true, 2, true),
                        new Fieldelement("qcdf", "贷方", true, 2, true) });
        list.add(qcelement);
        Fieldelement bqelement = new Fieldelement("", "本期发生额", true, 2, true, 1, 2);
        bqelement.setChilds(
                new Fieldelement[] { new Fieldelement("fsjf", "借方", true, 2, true),
                        new Fieldelement("fsdf", "贷方", true, 2, true) });
        list.add(bqelement);
        Fieldelement bnelement = new Fieldelement("", "本年发生额", true, 2, true, 1, 2);
        bnelement.setChilds(
                new Fieldelement[] { new Fieldelement("jftotal", "借方", true, 2, true),
                        new Fieldelement("dftotal", "贷方", true, 2, true) });
        list.add(bnelement);
        Fieldelement yeelement = new Fieldelement("", "期末余额", true, 2, true, 1, 2);
        yeelement.setChilds(
                new Fieldelement[] { new Fieldelement("qmjf", "借方", true, 2, true),
                        new Fieldelement("qmdf", "贷方", true, 2, true) });
        list.add(yeelement);
        return list.toArray(new Fieldelement[0]);
    }

    @Override
    public String getExcelport2007Name() {
        return nodename + "("+corpname+")_" + now + ".xlsx";
    }

    @Override
    public String getExcelport2003Name() {
        return nodename + "("+corpname+")_" + now + ".xls";
    }

    @Override
    public String getExceportHeadName() {
        return nodename;
    }

    @Override
    public String getSheetName() {
        return nodename;
    }

    @Override
    public FseJyeVO[] getData() {
        return vos;
    }

    @Override
    public Fieldelement[] getFieldInfo() {
        if (!StringUtil.isEmpty(pk_currency) && !pk_currency.equals(IGlobalConstants.RMB_currency_id)) {
            return getFileWbs();
        } else {
            return getFileNoWbs();
        }
    }


    @Override
    public String getQj() {
        return qj;
    }

    @Override
    public String getCreateSheetDate() {
        return now;
    }

    @Override
    public String getCreateor() {
        return creator;
    }

    @Override
    public String getCorpName() {
        return corpname;
    }


    @Override
    public boolean[] isShowTitDetail() {
        return new boolean[] { true, true, true };
    }

    @Override
    public List<FseJyeVO[]> getAllSheetData() {
        return allsheetvos;
    }

    @Override
    public String[] getAllSheetName() {
        return allsheetname;
    }

    public List<FseJyeVO[]> getAllsheetzcvos() {
        return allsheetvos;
    }

    public void setAllsheetzcvos(List<FseJyeVO[]> allsheetzcvos) {
        this.allsheetvos = allsheetzcvos;
    }

    @Override
    public String[] getAllPeriod() {
        return periods;
    }

    @Override
    public List<TitleColumnExcelport> getHeadColumns() {
        List<TitleColumnExcelport> lists = new ArrayList<TitleColumnExcelport>();
        return lists;
    }

    @Override
    public TitleColumnExcelport getTitleColumns() {
        TitleColumnExcelport column1 = new TitleColumnExcelport(1, getSheetName(), HorizontalAlignment.RIGHT);
        return column1;
    }

    @Override
    public String getDw() {
        if (StringUtil.isEmpty(currencyname)) {
            return "元";
        } else {
            return currencyname;
        }
    }


}
