package com.dzf.zxkj.app.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


public class CountInputStream extends FilterInputStream {

    long count;


    public CountInputStream(InputStream input) {
        super(input);
    }

    public int read() throws IOException {
        int value = super.read();
        count++;
        return value;
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
        int readLen = super.read(b, off, len);
        count += readLen;
        return readLen;
    }

    public long getCount() {
        return count;
    }

}