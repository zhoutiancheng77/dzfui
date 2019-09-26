package com.dzf.zxkj.base.vo;

import com.dzf.zxkj.common.model.SuperVO;

public class QueryPageVO {

    private SuperVO[] pagevos = null;

    private int total = 0;//总条数

    private int page = 1;// 当前页

    private int pageofrows = 100;// 每页显示记录数

    public SuperVO[] getPagevos() {
        return pagevos;
    }

    public void setPagevos(SuperVO[] pagevos) {
        this.pagevos = pagevos;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageofrows() {
        return pageofrows;
    }

    public void setPageofrows(int pageofrows) {
        this.pageofrows = pageofrows;
    }
}