package com.dzf.zxkj.custom.type;


import com.dzf.zxkj.custom.utils.Common;

import java.math.BigDecimal;
import java.util.Arrays;


public class DZFDouble extends Number implements java.io.Serializable,
        Comparable<DZFDouble> {
    static final long serialVersionUID = -809396813980155342L;

    private int power = DEFAULT_POWER;


    public final static int ROUND_UP = 0;


    public final static int ROUND_DOWN = 1;

    public final static int ROUND_CEILING = 2;


    public final static int ROUND_FLOOR = 3;


    public final static int ROUND_HALF_UP = 4;


    public final static int ROUND_HALF_DOWN = 5;


    public final static int ROUND_HALF_EVEN = 6;


    public final static int ROUND_UNNECESSARY = 7;


    private final static int ARRAY_LENGTH = 5;

    private final static int EFFICIENCY_SEATE = 9;


    private final static long MAX_ONELONG_VALUE = (long) 1E9;

    private final static long POWER_ARRAY[];


    public final static int ROUND_TO_ZERO_AND_HALF = 8;

    private byte si = 1;

    private long v[] = new long[ARRAY_LENGTH];

    static {
        POWER_ARRAY = new long[EFFICIENCY_SEATE + 2];
        for (int i = 0; i < POWER_ARRAY.length - 1; i++) {
            POWER_ARRAY[i] = (long) Math.pow(10, EFFICIENCY_SEATE - i);
        }
        POWER_ARRAY[POWER_ARRAY.length - 1] = 0;
    }

    public final static DZFDouble ONE_DBL = new DZFDouble(1f);

    public final static DZFDouble ZERO_DBL = new DZFDouble(0f);


    public DZFDouble() {
        super();
    }

    public static DZFDouble getUFDouble(DZFDouble ufd) {
        return ufd == null ? DZFDouble.ZERO_DBL : ufd;
    }

    public DZFDouble(double d) throws NumberFormatException {
        this(d, DEFAULT_POWER);
    }


    public DZFDouble(double d, int newPower) throws NumberFormatException {
        setValue(d, newPower);
    }


    public DZFDouble(int d) {
        this((long) d);
    }

    public DZFDouble(int d, int pow) {
        this((long) d, pow);
    }


    public DZFDouble(long d) {
        this(d, DEFAULT_POWER);
    }


    public DZFDouble(long d, int pow) throws NumberFormatException {

        this(d + 0.0, pow);
    }


    public DZFDouble(long[] dv, byte si, int pow) throws NumberFormatException {
        if (dv == null || dv.length != ARRAY_LENGTH) {
            throw new NumberFormatException("array length must be 5");
        }
        this.v = dv;
        this.si = si;
        this.power = pow;
    }


    public DZFDouble(Double d) throws NumberFormatException {
        this(d.doubleValue(), DEFAULT_POWER);
    }


    public DZFDouble(String str) throws NumberFormatException {
        String s = "";
        if (str == null || str.trim().length() == 0)
            s = "0";
        else {
            java.util.StringTokenizer token = new java.util.StringTokenizer(
                    str, ",");
            while (token.hasMoreElements()) {
                s += token.nextElement().toString();
            }
            if (s.indexOf('e') >= 0 || s.indexOf('E') >= 0) {
                setValue(Double.parseDouble(s), -8);
                return;
            }
            if (s.charAt(0) == '-') {
                si = -1;
                s = s.substring(1);
            } else if (s.charAt(0) == '+')
                s = s.substring(1);
        }
        int loc = s.indexOf('.');
        if (loc >= 0) {
            String s1 = s.substring(loc + 1);
            if (s1.length() > -DEFAULT_POWER) {
                if (-DEFAULT_POWER >= EFFICIENCY_SEATE)
                    s1 = s1.substring(0, EFFICIENCY_SEATE);
                else
                    s1 = s1.substring(0, 1 - DEFAULT_POWER);
            }
            power = -s1.length();
            if (power < -EFFICIENCY_SEATE) {
                power = -EFFICIENCY_SEATE;
                s1 = s.substring(loc + 1, EFFICIENCY_SEATE + 1 + loc);
            } else {
                for (int i = s1.length(); i < EFFICIENCY_SEATE; i++)
                    s1 += "0";
            }
            v[0] = Long.parseLong(s1);
            s = s.substring(0, loc);
        } else {
            power = 0;
            v[0] = 0;
        }

        int len = s.length();
        int sitLoc = 1;
        while (len > 0) {
            String s1 = "";
            if (len > EFFICIENCY_SEATE) {
                s1 = s.substring(len - EFFICIENCY_SEATE);
                s = s.substring(0, len - EFFICIENCY_SEATE);
            } else {
                s1 = s;
                s = "";
            }
            len = s.length();
            v[sitLoc++] = Long.parseLong(s1);
        }
        for (int i = sitLoc; i < v.length; i++)
            v[i] = 0;
        round(ROUND_HALF_UP);
    }


    public DZFDouble(String str, int newPower) {

        newPower = getValidPower(newPower);
        String s = "";
        if (str == null || str.trim().length() == 0)
            s = "0";
        else {
            java.util.StringTokenizer token = new java.util.StringTokenizer(
                    str, ",");
            while (token.hasMoreElements()) {
                s += token.nextElement().toString();
            }
            if (s.indexOf('e') >= 0 || s.indexOf('E') >= 0) {
                setValue(Double.parseDouble(s), -newPower);
                return;
            }
            if (s.charAt(0) == '-') {
                si = -1;
                s = s.substring(1);
            } else if (s.charAt(0) == '+')
                s = s.substring(1);
        }
        int loc = s.indexOf('.');
        if (loc >= 0) {
            String s1 = s.substring(loc + 1);
            if (s1.length() > -newPower) {
                if (-newPower >= EFFICIENCY_SEATE)
                    s1 = s1.substring(0, EFFICIENCY_SEATE);
                else
                    s1 = s1.substring(0, 1 - newPower);
            }

            power = newPower;
            for (int i = s1.length(); i < EFFICIENCY_SEATE; i++)
                s1 += "0";
            v[0] = Long.parseLong(s1);
            s = s.substring(0, loc);
        } else {
            power = newPower;
            v[0] = 0;
        }

        int len = s.length();
        int sitLoc = 1;
        while (len > 0) {
            String s1 = "";
            if (len > EFFICIENCY_SEATE) {
                s1 = s.substring(len - EFFICIENCY_SEATE);
                s = s.substring(0, len - EFFICIENCY_SEATE);
            } else {
                s1 = s;
                s = "";
            }
            len = s.length();
            v[sitLoc++] = Long.parseLong(s1);
        }
        for (int i = sitLoc; i < v.length; i++)
            v[i] = 0;
        round(ROUND_HALF_UP);
    }


    public DZFDouble(BigDecimal value) {
        this(value.toString(), value.scale());
    }


    public DZFDouble(DZFDouble fd) {
        si = fd.si;
        for (int i = 0; i < v.length; i++) {
            v[i] = fd.v[i];
        }
        power = fd.power;
    }


    public DZFDouble add(double d1) {
        return add(new DZFDouble(d1));
    }


    public DZFDouble add(DZFDouble ufd) {
        return add(ufd, DEFAULT_POWER, ROUND_HALF_UP);
    }


    public DZFDouble add(DZFDouble ufd, int newPower) {
        return add(ufd, newPower, ROUND_HALF_UP);
    }


    public DZFDouble add(DZFDouble ufd, int newPower, int roundingMode) {

        newPower = getValidPower(newPower);

        DZFDouble fdnew = new DZFDouble(this);

        fdnew.power = newPower;

        fdnew.addUp0(ufd, newPower, roundingMode);
        return fdnew;
    }


    private void addUp0(double ufd) {
        addUp0(new DZFDouble(ufd), power, ROUND_HALF_UP);
    }


    private void addUp0(DZFDouble ufd, int newPower, int roundingMode) {

        toPlus();
        ufd.toPlus();
        for (int i = 0; i < v.length; i++) {
            v[i] += ufd.v[i];
        }
        judgNegative();
        adjustIncluedFs();

        ufd.judgNegative();
        round(roundingMode);
    }


    private void adjustIncluedFs() {
        for (int i = 1; i < v.length; i++) {
            if (v[i - 1] < 0) {
                v[i]--;
                v[i - 1] += MAX_ONELONG_VALUE;
            } else {
                v[i] = v[i] + v[i - 1] / MAX_ONELONG_VALUE;
                v[i - 1] = v[i - 1] % MAX_ONELONG_VALUE;
            }
        }
    }

    private void adjustNotIncluedFs() {
        for (int i = 1; i < v.length; i++) {
            v[i] = v[i] + v[i - 1] / MAX_ONELONG_VALUE;
            v[i - 1] = v[i - 1] % MAX_ONELONG_VALUE;
        }
    }


    public int compareTo(DZFDouble o) {
        return toDouble().compareTo(((DZFDouble) o).toDouble());
    }

    private void cutdown() {
        int p = -power;
        v[0] = v[0] / POWER_ARRAY[p] * POWER_ARRAY[p];
    }

    public DZFDouble div(double d1) {
        double d = getDouble() / d1;
        DZFDouble ufd = new DZFDouble(d, DEFAULT_POWER);
        ufd.round(ROUND_HALF_UP);
        return ufd;
    }

    public DZFDouble div(DZFDouble ufd) {
        double d = getDouble() / ufd.getDouble();
        DZFDouble ufdNew = new DZFDouble(d, DEFAULT_POWER);
        ufdNew.round(ROUND_HALF_UP);
        return ufdNew;
    }

    public DZFDouble div(DZFDouble ufd, int newPower) {

        newPower = getValidPower(newPower);

        double d = getDouble() / ufd.getDouble();
        DZFDouble ufdNew = new DZFDouble(d, newPower);
        ufdNew.round(ROUND_HALF_UP);
        return ufdNew;
    }


    public DZFDouble div(DZFDouble ufd, int newPower, int roundingMode) {

        newPower = getValidPower(newPower);

        double d = getDouble() / ufd.getDouble();
        DZFDouble ufdNew = new DZFDouble(d);
        ufdNew.power = newPower;
        ufdNew.round(roundingMode);
        return ufdNew;
    }


    public double doubleValue() {
        double d = 0;
        for (int i = v.length - 1; i >= 0; i--) {
            d *= MAX_ONELONG_VALUE;
            d += v[i];
        }
        d /= MAX_ONELONG_VALUE;
        return d * si;
    }


    public float floatValue() {
        return (float) getDouble();
    }


    public double getDouble() {
        return this.doubleValue();
    }


    public long[] getDV() {
        return this.v;
    }


    public byte getSIValue() {
        return this.si;
    }


    public int intValue() {
        return (int) getDouble();
    }


    private void judgNegative() {

        boolean isFs = false;
        for (int i = v.length - 1; i >= 0; i--) {
            if (v[i] < 0) {

                isFs = true;
                break;
            }
            if (v[i] > 0)
                break;
        }
        if (isFs) {
            for (int i = 0; i < v.length; i++)
                v[i] = -v[i];
            si = -1;
        }
    }


    public long longValue() {
        long d = 0;

        for (int i = v.length - 1; i > 0; i--) {
            d *= MAX_ONELONG_VALUE;
            d += v[i];
        }
        return d * si;
    }

    public static void main(String[] args) {

    }

    public DZFDouble multiply(double d1) {

        DZFDouble ufD1 = new DZFDouble(d1);
        return multiply(ufD1, DEFAULT_POWER, ROUND_HALF_UP);
    }

    public DZFDouble multiply(DZFDouble ufd) {
        return multiply(ufd, DEFAULT_POWER, ROUND_HALF_UP);
    }

    public DZFDouble multiply(DZFDouble ufd, int newPower) {
        return multiply(ufd, newPower, ROUND_HALF_UP);
    }

    public DZFDouble multiply(DZFDouble ufd, int newPower, int roundingMode) {

        newPower = getValidPower(newPower);

        long mv[] = new long[ARRAY_LENGTH * 2 + 1];
        for (int i = 0; i < mv.length; i++) {
            mv[i] = 0;
        }
        for (int i = 0; i < v.length; i++) {
            for (int j = 0; j < v.length; j++) {
                long l = v[i] * ufd.v[j];
                mv[i + j] += l % MAX_ONELONG_VALUE;
                mv[i + j + 1] += l / MAX_ONELONG_VALUE;
            }
        }
        DZFDouble fdnew = new DZFDouble();
        fdnew.power = newPower;

        fdnew.si = this.si;

        fdnew.si = (byte) (fdnew.si * ufd.si);
        for (int i = 0; i < v.length; i++) {
            fdnew.v[i] = mv[i + 1];
        }
        fdnew.round(roundingMode);
        return fdnew;
    }


    private DZFDouble round(double d, int newPower, int roundingMode) {

        newPower = getValidPower(newPower);

        boolean increment = true;
        switch (roundingMode) {
            case ROUND_UP:
                increment = true;
                break;
            case ROUND_CEILING:
                increment = false;
                break;
            case ROUND_FLOOR:
                increment = (d > 0);
                break;
            case ROUND_DOWN:
                increment = (d < 0);
                break;
            case ROUND_TO_ZERO_AND_HALF:

        }
        long l = (long) (d + ((increment) ? 0.5 : 0));
        return new DZFDouble(l, newPower);
    }


    private void round(int roundingMode) {
        boolean increment = true;
        switch (roundingMode) {
            case ROUND_UP:
                increment = true;
                break;
            case ROUND_CEILING:
                increment = si == 1;
                break;
            case ROUND_FLOOR:
                increment = si == -1;
                break;
            case ROUND_DOWN:
                increment = false;

                break;
            case ROUND_TO_ZERO_AND_HALF:

        }
        int p = -power;
        long vxs = POWER_ARRAY[p + 1];

        if (increment) {
            v[0] += vxs * 5;
            adjustNotIncluedFs();
        }
        cutdown();

        boolean isZero = true;
        for (int i = 0; i < v.length; i++) {
            if (v[i] != 0) {
                isZero = false;
                break;
            }
        }
        if (si == -1 && isZero)
            si = 1;
        //
    }


    public DZFDouble setScale(int power, int roundingMode) {
        return multiply(ONE_DBL, power, roundingMode);
    }

    private void setValue(double d, int newPower) throws NumberFormatException {
        double dd, ld;

        if (d < 0) {
            d = -d;
            si = -1;
        }
        dd = d;

        power = getValidPower(newPower);

        double dxs = d % 1;
        d -= dxs;
        ld = d;
        for (int i = 1; i < v.length; i++) {
            v[i] = (long) (d % MAX_ONELONG_VALUE);
            d = d / MAX_ONELONG_VALUE;
        }
        long v2 = 0;
        if (dxs == 0.0)
            v2 = (long) (dxs * MAX_ONELONG_VALUE);
        else {
            if (dd / ld == 1.0) {
                dxs = 0.0;
                v2 = (long) (dxs * MAX_ONELONG_VALUE);
            } else {
                if (power <= -8) {
                    int iv = (int) v[2];
                    if (iv != 0) {
                        if (iv >= 1000000)
                            power = -0;
                        else if (iv >= 100000)
                            power = -1;
                        else if (iv >= 10000)
                            power = -2;
                        else if (iv >= 1000)
                            power = -3;
                        else if (iv >= 100)
                            power = -4;
                        else if (iv >= 10)
                            power = -5;
                        else if (iv >= 1)
                            power = -6;
                    } else {
                        iv = (int) v[1];
                        if (iv >= 100000000)
                            power = -7;
                    }
                    if (power < 0) {
                        int ii = -power;
                        double d1;
                        int i2 = 1;
                        double dxs1;
                        for (int i = 1; i < ii; i++) {
                            i2 *= 10;
                            dxs1 = ((double) Math.round(dxs * i2)) / i2;
                            d1 = ld + dxs1;
                            if (dd / d1 == 1.0) {
                                dxs = dxs1;
                                break;
                            }
                        }
                    }
                }
                v2 = (long) ((dxs + 0.00000000001) * MAX_ONELONG_VALUE);
            }
        }
        v[0] = v2;
        round(ROUND_HALF_UP);
    }

    public DZFDouble sub(double d1) {
        DZFDouble ufd = new DZFDouble(d1);
        return sub(ufd, DEFAULT_POWER, ROUND_HALF_UP);
    }

    public DZFDouble sub(DZFDouble ufd) {
        return sub(ufd, DEFAULT_POWER, ROUND_HALF_UP);
    }

    public DZFDouble sub(DZFDouble ufd, int newPower) {
        return sub(ufd, newPower, ROUND_HALF_UP);
    }


    public DZFDouble sub(DZFDouble ufd, int newPower, int roundingMode) {

        newPower = getValidPower(newPower);

        DZFDouble ufdnew = new DZFDouble(ufd);
        ufdnew.si = (byte) -ufdnew.si;
        return add(ufdnew, newPower, roundingMode);
    }


    public static DZFDouble sum(double[] dArray) {
        return sum(dArray, DEFAULT_POWER);
    }


    public static DZFDouble sum(double[] dArray, int newPower) {

        newPower = getValidPower(newPower);

        DZFDouble ufd = new DZFDouble(0, newPower);
        for (int i = 0; i < dArray.length; i++) {
            ufd.addUp0(dArray[i]);
        }
        return ufd;
    }


    public static DZFDouble sum(double[] dArray, int newPower, int roundingMode) {

        newPower = getValidPower(newPower);

        DZFDouble ufd = new DZFDouble(0, newPower);
        for (int i = 0; i < dArray.length; i++) {
            DZFDouble ufdNew = new DZFDouble(dArray[i], newPower);
            ufd.addUp0(ufdNew, newPower, roundingMode);
        }
        return ufd;
    }

    public BigDecimal toBigDecimal() {

        return new BigDecimal(toString());
    }


    public Double toDouble() {
        return new Double(getDouble());
    }

    public static String getThousandsStr(DZFDouble d) {
        if (d == null || d.equals(DZFDouble.ZERO_DBL)) {
            return "-";
        }
        return Common.format(d);
    }

    private void toPlus() {
        if (si == 1)
            return;
        si = 1;
        for (int i = 0; i < v.length; i++) {
            v[i] = -v[i];
        }
    }

    public String toString() {

        boolean addZero = false;
        StringBuffer sb = new StringBuffer();
        if (si == -1)
            sb.append("-");
        for (int i = v.length - 1; i > 0; i--) {
            if (v[i] == 0 && !addZero)
                continue;
            String temp = String.valueOf(v[i]);
            if (addZero) {
                int len = temp.length();
                int addZeroNo = EFFICIENCY_SEATE - len;
                for (int j = 0; j < addZeroNo; j++) {
                    sb.append('0');
                }
            }
            sb.append(temp);
            addZero = true;
        }
        if (!addZero)
            sb.append('0');

        if (power < 0) {
            sb.append('.');
            for (int j = 0; j < EFFICIENCY_SEATE && j < -power; j++) {
                sb.append((v[0] / POWER_ARRAY[j + 1]) % 10);
            }
        }

        int index = -1;
        if (isTrimZero()) {
            if (power < 0) {
                String sTemp = sb.toString();
                for (int i = sb.length() - 1; i >= 0; i--) {
                    if (sTemp.substring(i, i + 1).equals("0"))
                        index = i;
                    else {
                        if (sTemp.substring(i, i + 1).equals(".")) {
                            index = i;
                        }
                        break;
                    }
                }
            }
        }
        if (index >= 0)
            sb = sb.delete(index, sb.length());
        return sb.toString();
    }

    public final static int DEFAULT_POWER = -8;

    private boolean trimZero = false;


    public DZFDouble abs() {
        DZFDouble fdnew = new DZFDouble();
        fdnew.power = this.power;
        fdnew.si = 1;
        for (int i = 0; i < v.length; i++) {
            fdnew.v[i] = v[i];
        }
        return fdnew;
    }


    public int getPower() {
        return power;
    }


    public boolean isTrimZero() {
        return trimZero;
    }


    public DZFDouble mod(DZFDouble ufd) {
        return mod(ufd, DEFAULT_POWER, ROUND_HALF_UP);
    }


    public DZFDouble mod(DZFDouble ufd, int newPower) {
        return mod(ufd, newPower, ROUND_HALF_UP);
    }


    public DZFDouble mod(DZFDouble ufd, int newPower, int roundingMode) {
        DZFDouble ufdDiv = div(ufd, 0, ROUND_DOWN);
        DZFDouble ufdnew = sub(ufdDiv.multiply(ufd));
        if (ufd.si != si)
            ufdnew = ufdnew.sub(ufd);
        ufdnew.power = newPower;
        ufdnew.round(roundingMode);
        return ufdnew;
    }


    public void setTrimZero(boolean newTrimZero) {
        trimZero = newTrimZero;
    }

    private static int getValidPower(int newPower) {

        int power = newPower > 0 ? -newPower : newPower;
        if (power < -9)
            power = -9;
        return power;

    }

    @Override
    public int hashCode() {
        int v = 0;
        for (int i = 0; i < this.v.length; i++) {
            v += this.v[i];
        }
        return v * this.si;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DZFDouble) {
            DZFDouble ud = (DZFDouble) o;
            return si == ud.si && Arrays.equals(v, ud.v);
        }
        return false;

    }
}