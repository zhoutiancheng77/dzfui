package com.dzf.zxkj.base.utils;

class WildcardMatcher {
    public static boolean match(String reg, String str) {
        return match(reg, str, false);
    }

    public static boolean matchIgnoreCase(String reg, String str) {
        return match(reg, str, true);
    }

    private static boolean match(String reg, String str, boolean ignoreCase) {
        if (str == null)
            return false;
        if (reg == null) {
            return false;
        }
        if (ignoreCase) {
            reg = reg.toLowerCase();
            str = str.toLowerCase();
        }
        boolean isMatch = match0(reg, str);
        return isMatch;
    }

    private static void switchPosition(int begin, char[] chars) {
        int scale = 1;
        for (int i = begin; i < chars.length; i++)
            if (chars[i] == '?') {
                if (chars[(i - 1)] == '*') {
                    chars[(i - scale)] = '?';
                    chars[i] = '*';
                }
            } else {
                if (chars[i] != '*') break;
                scale++;
            }
    }

    private static boolean match0(String reg, String str) {
        boolean isMatch = false;
        int regLen = reg.length();
        int strLen = str.length();
        int i = 0;
        int j = 0;
        char[] regChars = reg.toCharArray();
        while ((i < regLen) && (j < strLen)) {
            char rc = regChars[i];
            char sc = str.charAt(j);
            if (rc == '*') {
                if (i == regLen - 1) {
                    i++;
                    j = strLen;
                } else {
                    rc = reg.charAt(i + 1);
                    if (rc == '?') {
                        switchPosition(i + 1, regChars);
                    } else if (rc == '*') {
                        i++;
                    } else {
                        i++;
                        int next = str.substring(j).lastIndexOf(rc);
                        if (next == -1) {
                            break;
                        }
                        j += next;
                    }
                }
            } else {
                if ((rc != sc) && (rc != '?')) break;
                i++;
                j++;
            }

        }

        if ((i < regLen) && (j >= strLen) && (allIsStar(i, regChars)))
            isMatch = true;
        else if ((i == regLen) && (j == strLen))
            isMatch = true;
        return isMatch;
    }

    private static boolean allIsStar(int begin, char[] chars) {
        boolean b = true;
        int count = chars.length;
        for (int i = begin; i < count; i++) {
            if (chars[i] != '*') {
                b = false;
                break;
            }
        }
        return b;
    }
}