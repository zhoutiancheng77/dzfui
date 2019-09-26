package com.dzf.zxkj.common.utils;

public class DataPasswroid {
    
    private static final char[] ALPHABET = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', '2', '3', '4', '5', '6', '7'
    };
    
    private static final byte[] DECODE_TABLE;
    
    static {
        DECODE_TABLE = new byte[128];
        
        for (int i = 0; i < DECODE_TABLE.length; i++) {
            DECODE_TABLE[i] = (byte)0xFF;
        }
        for (int i = 0; i < ALPHABET.length; i++) {
            DECODE_TABLE[(int)ALPHABET[i]] = (byte)i;
            if (i < 24) {
                DECODE_TABLE[(int)Character.toLowerCase(ALPHABET[i])] = (byte)i;
            }
        }
    }

    public static String encode(byte[] data) {
        
        char[] chars = new char[((data.length * 8) / 5) + ((data.length % 5) != 0 ? 1 : 0)];
        
        for (int i = 0, j = 0, index = 0; i < chars.length; i++) {
            if (index > 3) {
                int b = data[j] & (0xFF >> index);
                index = (index + 5) % 8;
                b <<= index;
                if (j < data.length - 1) {
                    b |= (data[j + 1] & 0xFF) >> (8 - index);
                }
                chars[i] = ALPHABET[b];
                j++;
            } else {
                chars[i] = ALPHABET[((data[j] >> (8 - (index + 5))) & 0x1F)];
                index = (index + 5) % 8;
                if (index == 0) {
                    j++;
                }
            }
        }
        
        return new String(chars);
    }

    public static byte[] decode(String s) throws Exception {
        
        char[] stringData = s.toCharArray();
        byte[] data = new byte[(stringData.length * 5) / 8];
        
        for (int i = 0, j = 0, index = 0; i < stringData.length; i++) {
            int val;
            
            try {
                val = DECODE_TABLE[stringData[i]];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new Exception("Illegal character");
            }
            
            if (val == 0xFF) {
                throw new Exception("Illegal character");
            }
            
            if (index <= 3) {
                index = (index + 5) % 8;
                if (index == 0) {
                    data[j++] |= val;
                }
                else {
                    data[j] |= val << (8 - index);
                }
            } else {
                index = (index + 5) % 8;
                data[j++] |= (val >> index);
                if (j < data.length) {
                    data[j] |= val << (8 - index);
                }
            }
        }
        
        return data;
    }
}
