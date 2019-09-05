package com.dzf.zxkj.base.framework.processor;

import java.io.IOException;
import java.io.OutputStream;

final class JSONSerializer {

    private OutputStream os;

    public JSONSerializer(OutputStream os) {
        this.os = os;
    }

    private final void writeChar(char c) throws IOException {
        this.os.write((int) c);
    }

    public void startDocument() throws IOException {
        if (this.os == null) {
            this.os = System.out;
        }
    }

    public void endDocument() throws IOException {
        this.os.flush();
    }

    public void startObject() throws IOException {
        this.writeChar('{');
    }

    public void endObject() throws IOException {
        this.writeChar('}');
    }

    public void startElement(String key) throws IOException {
        this.writeChar('"');
        this.os.write(key.getBytes());
        this.writeChar('"');
        this.writeChar(':');
    }

    public void element(Object value) throws IOException {
        byte[] valueString = String.valueOf(value).getBytes();
        if (value == null || value instanceof Number
                || value instanceof Boolean) {
            this.os.write(valueString);
        } else {
            this.writeChar('"');
            this.os.write(valueString);
            this.writeChar('"');
        }
    }

    public void separeElement() throws IOException {
        this.writeChar(',');
    }

    public void startArray() throws IOException {
        this.writeChar('[');
    }

    public void endArray() throws IOException {
        this.writeChar(']');
    }

}
