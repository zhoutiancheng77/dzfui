package com.dzf.zxkj.app.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;


public class FastDeflaterOutputStream extends DeflaterOutputStream {

    private int count;

    public FastDeflaterOutputStream(OutputStream out) throws IOException {
        this(out, 512);
    }

    public FastDeflaterOutputStream(OutputStream out, int bufferSize) throws IOException {
        super(out, new Deflater(), bufferSize);

    }

    protected void deflate() throws IOException {
        int len = def.deflate(buf, count, buf.length - count);

        if (len > 0) {
            count = count + len;
            if (count == buf.length) {
                out.write(buf, 0, count);
                count = 0;
            }
        } else if (count > 0) {
            out.write(buf, 0, count);
            count = 0;
        }
    }

    public void flush() throws IOException {
        if (count > 0) {
            out.write(buf, 0, count);
            count = 0;
        }
        out.flush();
    }

    public void finsh() throws IOException {
        super.finish();
        flush();
    }

}