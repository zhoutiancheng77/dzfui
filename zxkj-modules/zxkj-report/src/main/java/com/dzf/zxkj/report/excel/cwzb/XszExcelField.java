package com.dzf.zxkj.report.excel.cwzb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.MuiltSheetAndTitleExceport;
import com.dzf.zxkj.excel.param.TitleColumnExcelport;
import com.dzf.zxkj.excel.param.UnitExceport;
import com.dzf.zxkj.platform.model.report.XsZVO;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 序时账导出配置
 *
 * @author zhangj
 *
 */
public class XszExcelField extends MuiltSheetAndTitleExceport<XsZVO> implements UnitExceport {
    private XsZVO[] vos = null;

    private List<XsZVO[]> allsheetvos = null;

    private String nodename = null;

    private String pk_currency;//币种

    private String currencyname;//币种

    private String[] periods = null;

    private String[] allsheetname = null;

    private String qj = null;

    private String now = DZFDate.getDate(new Date()).toString();

    private String creator = null;

    private String corpname = null;

    private String isxshl = null ;// 是否显示汇率

    public XszExcelField(String nodename, String pk_currency, String currencyname, String qj, String corpname) {
        this.nodename = nodename;
        this.pk_currency = pk_currency;
        this.currencyname = currencyname;
        this.qj = qj;
        this.corpname = corpname;
    }

    public XszExcelField(String nodename, String pk_currency, String currencyname, String[] periods, String[] allsheetname, String qj, String corpname,String isxshl) {
        this.nodename = nodename;
        this.pk_currency = pk_currency;
        this.currencyname = currencyname;
        this.periods = periods;
        this.allsheetname = allsheetname;
        this.qj = qj;
        this.corpname = corpname;
        this.isxshl = isxshl;
    }

    private Fieldelement[] getFileWbs() {
        List<Fieldelement> list = new ArrayList<Fieldelement>();
        list.add( new Fieldelement("rq", "日期", false, 0, true));
        list.add( new Fieldelement("year", "年度", false, 0, true));
        list.add( new Fieldelement("qj", "期间", false, 0, true));
        list.add(new Fieldelement("pzz", "凭证字", false, 0, true));
        list.add(new Fieldelement("pzh", "凭证号", false, 0, true));
        list.add(new Fieldelement("zy", "摘要", false, 0, true));
        list.add(new Fieldelement("kmbm", "科目编码", false, 0, true));
        list.add(new Fieldelement("kmmc", "科目名称", false, 0, true,50,false));
        list.add(new Fieldelement("bz", "币种", false, 0, true,50,false));
        list.add(new Fieldelement("hl", "汇率", true, 2, true));
        Fieldelement qcelement = new Fieldelement("", "借方", true, 2, true, 1, 2);
        qcelement.setChilds(new Fieldelement[] {
                new Fieldelement("ybjf", "原币", true, 2, true),
                new Fieldelement("jfmny", "本位币", true, 2, true)});
        list.add(qcelement);
        Fieldelement bqelement = new Fieldelement("", "贷方", true, 2, true, 1, 2);
        bqelement.setChilds(new Fieldelement[] {
                new Fieldelement("ybdf", "原币", true, 2, true),
                new Fieldelement("dfmny", "本位币", true, 2, true)});
        list.add(bqelement);
        return list.toArray(new Fieldelement[0]);
    }

    private Fieldelement[] getFileNoWbs() {
        List<Fieldelement> list = new ArrayList<Fieldelement>();
        list.add( new Fieldelement("rq", "日期", false, 0, true));
        list.add(new Fieldelement("pzh", "凭证号", false, 0, true));
        list.add(new Fieldelement("zy", "摘要", false, 0, true));
        list.add(new Fieldelement("kmbm", "科目编码", false, 0, true));
        list.add(new Fieldelement("kmmc", "科目名称", false, 0, true,50,false));
        list.add(new Fieldelement("jfmny", "借方", true, 2, true));
        list.add(new Fieldelement("dfmny", "贷方", true, 2, true));
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
    public XsZVO[] getData() {
        return vos;
    }

    @Override
    public Fieldelement[] getFieldInfo() {
        if (!StringUtil.isEmpty(isxshl) && isxshl.equals("true")) {
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
    public List<XsZVO[]> getAllSheetData() {
        return allsheetvos;
    }

    @Override
    public String[] getAllSheetName() {
        return allsheetname;
    }

    public List<XsZVO[]> getAllsheetzcvos() {
        return allsheetvos;
    }

    public void setAllsheetzcvos(List<XsZVO[]> allsheetzcvos) {
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
