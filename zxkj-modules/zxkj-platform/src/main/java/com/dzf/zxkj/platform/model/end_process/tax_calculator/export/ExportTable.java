package com.dzf.zxkj.platform.model.end_process.tax_calculator.export;

public class ExportTable {
    private String title;
    private ExportRow[] head;
    private ExportRow[] body;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ExportRow[] getHead() {
        return head;
    }

    public void setHead(ExportRow[] head) {
        this.head = head;
    }

    public ExportRow[] getBody() {
        return body;
    }

    public void setBody(ExportRow[] body) {
        this.body = body;
    }
}
