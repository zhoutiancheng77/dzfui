package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.common.utils.StringUtil;

import java.util.Arrays;
import java.util.Comparator;

public class LetterNumberSortUtil {

    public static <T extends Comparable<String>> Comparator<String> letterNumberOrder() {
        return LetterNumberOrderComparator.INSTANCE;
    }
        enum LetterNumberOrderComparator implements Comparator<String> {
            INSTANCE;

            class Int{
            public int i;
        }
        public int findDigitEnd(char[] arrChar, Int at) {
            int k = at.i;
            char c = arrChar[k];
            boolean bFirstZero = (c == '0');
            boolean bNoZero = false;
            while (k < arrChar.length) {
                c = arrChar[k];
                // first non-digit which is a high chance.

                if(c != '0'){
                    bNoZero =true;
                }
                if (c > '9' || c < '0') {
                    break;
                } else if (!bNoZero && bFirstZero && c == '0') {
                    at.i++;
                }
                k++;
            }
            return k;
        }

        @Override
        public int compare(String as, String bs) {
            if(!StringUtil.isEmpty(as) && !StringUtil.isEmpty(bs)){
                char[] a = as.toCharArray();
                char[] b = bs.toCharArray();
                Int aNonzeroIndex = new Int();
                Int bNonzeroIndex = new Int();
                int aIndex = 0, bIndex = 0,
                        aComparedUnitTailIndex, bComparedUnitTailIndex;
                while(aIndex < a.length && bIndex < b.length){
                    //aIndex <
                    aNonzeroIndex.i = aIndex;
                    bNonzeroIndex.i = bIndex;
                    aComparedUnitTailIndex = findDigitEnd(a, aNonzeroIndex);
                    bComparedUnitTailIndex = findDigitEnd(b, bNonzeroIndex);
                    //compare by number
                    if (aComparedUnitTailIndex > aIndex && bComparedUnitTailIndex > bIndex)
                    {
                        int aDigitIndex = aNonzeroIndex.i;
                        int bDigitIndex = bNonzeroIndex.i;
                        int aDigit = aComparedUnitTailIndex - aDigitIndex;
                        int bDigit = bComparedUnitTailIndex - bDigitIndex;
                        //compare by digit
                        if(aDigit != bDigit)
                            return aDigit - bDigit;
                        //the number of their digit is same.
                        while (aDigitIndex < aComparedUnitTailIndex){
                            if (a[aDigitIndex] != b[bDigitIndex])
                                return a[aDigitIndex] - b[bDigitIndex];
                            aDigitIndex++;
                            bDigitIndex++;
                        }
                        //if they are equal compared by number, compare the number of '0' when start with "0"
                        //ps note: paNonZero and pbNonZero can be added the above loop "while", but it is changed meanwhile.
                        //so, the following comparsion is ok.
                        aDigit = aNonzeroIndex.i - aIndex;
                        bDigit = bNonzeroIndex.i - bIndex;
                        if (aDigit != bDigit)
                            return -(aDigit - bDigit);
                        aIndex = aComparedUnitTailIndex;
                        bIndex = bComparedUnitTailIndex;
                    }else{
                        if (a[aIndex] != b[bIndex])
                            return a[aIndex] - b[bIndex];
                        aIndex++;
                        bIndex++;
                    }

                }
                return as.length() - bs.length();
            }
            return 0;
        }
    };

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String fileNames[] = { "01", "2", "1","003", "3", "001", "10", "20", "fss4", "aa1", "aa01", "aa12","fss01_3" };
        char chFileNames[][] = new char[fileNames.length][];
        String[] oldSortedNames = new String[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            chFileNames[i] = fileNames[i].toCharArray();
            oldSortedNames[i] = fileNames[i];
        }
        Arrays.sort(fileNames, letterNumberOrder());
        System.out.println("_Random_" + "\t" + "_Tradion_" + "\t" + "_Target_");
        String line;
        for (int i = 0; i < fileNames.length; i++) {
            line = fileNames[i] + (fileNames[i].length() >= 8 ? "\t" : "\t\t");
            line += oldSortedNames[i] + (oldSortedNames[i].length() >= 8 ? "\t" : "\t\t");
            line += new String(fileNames[i]);
            System.out.println(line);

        }
    }
}
