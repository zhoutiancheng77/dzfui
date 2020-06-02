package com.dzf.zxkj.app.utils;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

import java.util.HashMap;



public class NCObjectReplacer implements ObjectReplacer {

    private HashMap<Long, byte[]> dttimeSet = new HashMap<Long, byte[]>();

    private HashMap<UD, byte[]> dbSet = new HashMap<UD, byte[]>();

    private HashMap<Long, byte[]> dtSet = new HashMap<Long, byte[]>();

    public Object replaceObject(Object object) {
        if (object instanceof DZFDate) {
            DZFDate d = (DZFDate) object;
            return internDate(dtSet, UF_DATE, d.getMillis());
        } else if (object instanceof DZFDateTime) {
            DZFDateTime dt = (DZFDateTime) object;
            return internDate(dttimeSet, UF_DATETIME, dt.getMillis());
        } else if (object instanceof DZFBoolean) {
            DZFBoolean b = (DZFBoolean) object;
            if (b.booleanValue()) {
                return TRUE;
            } else {
                return FALSE;
            }
        } else if (object instanceof DZFDouble) {
            DZFDouble ud = (DZFDouble) object;
            return interDouble(ud);
        } else if (object instanceof String) {
            return ((String) object).intern();
        }
        return object;
    }

    private Object internDate(HashMap<Long, byte[]> map, byte t, long l) {
        byte[] ret = map.get(l);
        if (ret == null) {
            ret = new byte[10];
            ret[0] = t;
            ret[9] = UF_END;
            toByte(ret, 1, l);
            map.put(l, ret);
        }

        return ret;

    }

    private void toByte(byte[] bytes, int off, long v) {
        bytes[0 + off] = (byte) (v >>> 56);
        bytes[1 + off] = (byte) (v >>> 48);
        bytes[2 + off] = (byte) (v >>> 40);
        bytes[3 + off] = (byte) (v >>> 32);
        bytes[4 + off] = (byte) (v >>> 24);
        bytes[5 + off] = (byte) (v >>> 16);
        bytes[6 + off] = (byte) (v >>> 8);
        bytes[7 + off] = (byte) (v >>> 0);

    }

    private void toBytes(byte[] bytes, int from, int v) {
        bytes[from] = (byte) ((v >>> 24) & 0xFF);
        bytes[from + 1] = (byte) ((v >>> 16) & 0xFF);
        bytes[from + 2] = (byte) ((v >>> 8) & 0xFF);
        bytes[from + 3] = (byte) ((v >>> 0) & 0xFF);
    }

    private byte[] interDouble(DZFDouble dbl) {
        UD ud = new UD(dbl);
        byte[] ret = dbSet.get(ud);
        if (ret == null) {
            ret = new byte[24];
            ret[0] = UF_DOUBLE;
            ret[1] = (byte) dbl.getPower();
            ret[2] = dbl.getSIValue();
            for (int i = 0; i < dbl.getDV().length; i++) {
                toBytes(ret, 3 + i * 4, (int) dbl.getDV()[i]);
            }
            ret[23] = UF_END;
            dbSet.put(ud, ret);
        }
        return ret;
    }

    private static class UD {
        private DZFDouble u;

        int hashCode;

        private UD(DZFDouble u) {

            this.u = u;
            if (u != null) {
                hashCode = u.hashCode() + u.getPower() * 29;
            }
        }

        public int hashCode() {
            return hashCode;
        }

        public boolean equals(Object o) {
            if (o instanceof UD) {
                UD o1 = (UD) o;
                if (o1.u == null && u == null) {
                    return true;
                } else if (o1.u != null && u != null) {
                    return o1.u.equals(u) && o1.u.getPower() == u.getPower();
                }

            }
            return false;
        }

    }

}
