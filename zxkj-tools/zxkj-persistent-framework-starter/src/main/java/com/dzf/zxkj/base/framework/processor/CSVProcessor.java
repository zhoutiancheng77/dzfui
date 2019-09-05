package com.dzf.zxkj.base.framework.processor;

import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

@Slf4j
public class CSVProcessor extends OutputStreamProcessor {
    /**
     *
     */
    private final static char COMMA = ';';

    /**
     *
     */
    private char separator;

    /**
     *
     */
    public CSVProcessor() {
        this.separator = COMMA;
    }

    /**
     *
     */
    public CSVProcessor(char separator) {
        this.separator = separator;
    }

    /**
     *
     */
    public Object handleRS(ResultSet rs) throws SQLException {
        ResultSetMetaData rsMeta = rs.getMetaData();
        int cols = rsMeta.getColumnCount();
        while (rs.next()) {
            try {
                for (int i = 1; i <= cols; i++) {
                    this.outputStreamPtr.write(rs.getBytes(i));
                    if (i < cols) {
                        this.outputStreamPtr.write((int) separator);
                    }
                }
                this.outputStreamPtr.write((int) '\n');
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

}