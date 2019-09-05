package com.dzf.zxkj.base.framework.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class InOutUtil {
    public static byte[] serialize(Serializable s) {
        if (s == null)
            return null;
        ByteArrayOutputStream bo = null;
        ObjectOutputStream os = null;
        try {
            bo = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bo);
            os.writeObject(s);
            os.flush();
            return bo.toByteArray();
        } catch (IOException e) {
            log.error("serialize error", e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
            if (bo != null) {
                try {
                    bo.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    public static Serializable deserialize(byte[] ba) {
        Serializable value = null;
        ByteArrayInputStream bi = null;
        ObjectInputStream is = null;
        try {
            if (ba == null)
                return null;
            bi = new ByteArrayInputStream(ba);
            is = new ObjectInputStream(bi);

            value = (Serializable) is.readObject();
        } catch (IOException e) {
            log.error("deserialize error", e);
            return ba;
        } catch (ClassNotFoundException e) {
            log.error("deserialize error", e);

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    log.error("deserialize 关闭is流时error", e);
                }
            }
            if (bi != null) {
                try {
                    bi.close();
                } catch (Exception e) {
                    log.error("deserialize 关闭bi流时error", e);
                }
            }
        }
        return value;
    }
}
