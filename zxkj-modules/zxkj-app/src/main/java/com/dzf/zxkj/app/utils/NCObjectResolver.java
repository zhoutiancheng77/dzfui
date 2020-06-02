package com.dzf.zxkj.app.utils;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

public class NCObjectResolver implements ObjectResolver {

    public Object resolveObject(Object obj) {

        if (obj instanceof byte[]) {
            byte[] bytes = (byte[]) obj;

            if (bytes.length >= UF_BOOLEAN_LEN && bytes[bytes.length - 1] == UF_END) {
                if (bytes.length == UF_BOOLEAN_LEN && bytes[0] == UF_BOOLEAN) {
                    if (bytes[1] == 1) {
                        return DZFBoolean.TRUE;
                    } else if (bytes[1] == 0) {
                        return DZFBoolean.FALSE;
                    }
                } else if (bytes.length == UF_DATE_LEN && bytes[0] == UF_DATE) {
                    return DZFDate.getDate(toLong(bytes, 1));
                } else if (bytes.length == UF_DATETIME_LEN && bytes[0] == UF_DATETIME) {
                    return new DZFDateTime(toLong(bytes, 1));
                } else if (bytes.length == UF_DOUBLE_LEN && bytes[0] == UF_DOUBLE) {
                    return toDZFDouble(bytes);
                }
            }
        }

        return obj;

    }

    private static int toInt(byte[] b, int off) {
        return ((b[off + 3] & 0xFF) << 0) + ((b[off + 2] & 0xFF) << 8) + ((b[off + 1] & 0xFF) << 16)
                + ((b[off + 0] & 0xFF) << 24);
    }

    private static long toLong(byte[] b, int off) {
        return ((b[7 + off] & 0xFFL) << 0) + ((b[6 + off] & 0xFFL) << 8) + ((b[5 + off] & 0xFFL) << 16)
                + ((b[4 + off] & 0xFFL) << 24) + ((b[3 + off] & 0xFFL) << 32) + ((b[2 + off] & 0xFFL) << 40)
                + ((b[1 + off] & 0xFFL) << 48) + ((b[0 + off] & 0xFFL) << 56);
    }

    private DZFDouble toDZFDouble(byte[] b) {

        int power;

        byte si;

        long[] dv;
        power = b[1];
        si = b[2];
        dv = new long[5];
        for (int i = 0; i < 5; i++) {
            dv[i] = toInt(b, 3 + i * 4);
        }

        int v = 0;
        for (int i = 0; i < dv.length; i++) {
            v += dv[i];
        }

        if (v == 0) {
            return DZFDouble.ZERO_DBL;
        } else if (v == 1 && dv[1] == 1 && si == 1 && power == -8) {
            return DZFDouble.ONE_DBL;
        } else {
            return new DZFDouble(dv, si, power);
        }

    }

}
