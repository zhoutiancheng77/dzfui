package com.dzf.zxkj.base.framework.processor;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class JSONProcessor extends OutputStreamProcessor {

    private final static String DEFAULT_ROOT = "result-set";

    private JSONSerializer serializer;

    public void setOutputStream(OutputStream outputStreamPtr) {
        super.setOutputStream(outputStreamPtr);
        this.serializer = new JSONSerializer(outputStreamPtr);
    }

    public Object handleRS(ResultSet rs) throws SQLException {
        try {
            ResultSetMetaData rsm = rs.getMetaData();
            this.serializer.startDocument();
            this.serializer.startObject();
            this.serializer.startElement(DEFAULT_ROOT);
            this.serializer.startArray();
            boolean hasNext = rs.next();
            while (hasNext) {
                this.serializer.startObject();
                for (int i = 1; i <= rsm.getColumnCount(); i++) {
                    this.serializer.startElement(rsm.getColumnName(i));
                    this.serializer.element(rs.getObject(i));
                    if (i < rsm.getColumnCount()) {
                        this.serializer.separeElement();
                    }
                }
                this.serializer.endObject();
                hasNext = rs.next();
                if (hasNext) {
                    this.serializer.separeElement();
                }
            }
            this.serializer.endArray();
            this.serializer.endObject();
            this.serializer.endDocument();
        } catch (IOException e) {
            throw new SQLException(e.getMessage());
        }
        return null;
    }

}
