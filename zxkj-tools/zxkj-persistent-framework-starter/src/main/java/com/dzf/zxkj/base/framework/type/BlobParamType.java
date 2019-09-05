package com.dzf.zxkj.base.framework.type;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class BlobParamType implements SQLParamType {

    private static final long serialVersionUID = -8160659150199130371L;

    Object blob = null;

    byte bytes[] = null;

    int length = -1;

    private transient InputStream input = null;

    public BlobParamType(Object blob) {
        this.blob = blob;
    }

    public BlobParamType(byte[] bytes) {
        this.bytes = bytes;
        this.length = bytes.length;
    }

    public BlobParamType(InputStream input, int length) {
        this.input = input;
        this.length = length;
    }

    public Object getBlob() {
        return blob;
    }

    public byte[] getBytes() {
        if (bytes == null) {
            ByteArrayOutputStream baos = null;
            ObjectOutputStream oos = null;
            try {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(blob);
                oos.flush();
                baos.flush();
                bytes = baos.toByteArray();
            } catch (IOException e) {
                log.error("BlobParamType getBytes error", e);
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException e) {
                    }
                }
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return bytes;
    }

    public InputStream getInputStream() {
        if (input == null)
            input = new ByteArrayInputStream(getBytes());
        return input;
    }

    public int getLength() {
        if (length == -1)
            length = getBytes().length;
        return length;
    }
}
