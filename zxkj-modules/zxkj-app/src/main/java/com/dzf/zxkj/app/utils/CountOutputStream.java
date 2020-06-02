package com.dzf.zxkj.app.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class CountOutputStream extends FilterOutputStream {

    long count;

    public CountOutputStream(OutputStream output) {
        super(output);
       
    }

    public void write(int b) throws IOException {
        count++;
        super.write(b);
    }

    public long getCount() {
        return count;
    }

    public void write(byte[] b, int offset, int len) throws IOException {
        count += len;
        out.write(b, offset, len);
    }


    
}