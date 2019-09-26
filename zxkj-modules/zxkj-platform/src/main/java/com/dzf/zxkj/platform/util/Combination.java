package com.dzf.zxkj.platform.util;
/** 
 * 计算并输出组合数 
 */  
public class Combination {  

    /** 
     * 计算数组 source[] 中大小为 m 的第 x 个组合数 
     * 按下述原理分组 
     * C(n,m) = C(n-1,m-1) + C(n-1,m) 
     * C(n,m) = C(n-1,m-1) + C(n-2,m-1) + C(n-2,m) 
     * ... 
     * C(n,m) = C(n-1,m-1) + C(n-2,m-1) + .. + C(m,m-1) + C(m,m) 
     * C(n,m) = C(n-1,m-1) + C(n-2,m-1) + .. + C(m,m-1) + C(m-1,m-1) 
     * 最后一步转换是因为 C(m,m) == C(m-1,m-1) 
     * @param source 
     * @param m 组合数的大小，且 1 <= m <= source.length 
     * @param x [0,C(n,m)) 
     * @return 
     */  
    public static String getValue(String[] source,int m,int x){  
        // 数组大小  
        int n = source.length;  
        // 存储组合数  
        StringBuilder sb = new StringBuilder();  
        int start = 0;  
        while(m > 0){  
            if (m == 1){  
                // m == 1 时为组合数的最后一个字符  
                sb.append(source[start + x]);  
                break;  
            }  
            for (int i=0; i<=n-m; i++){                        
                int cnm =  (int)getCnm(n-1-i,m-1);  
                if(x <= cnm-1){  
                    sb.append(source[start + i]);  
                    // 启始下标前移  
                    start = start + (i + 1);  
                    // 搜索区域减小  
                    n = n - (i+1);  
                    // 组合数的剩余字符个数减少  
                    m--;  
                    break;  
                } else {  
                    x = x - cnm;                      
                }         
            }  
        }  
        return sb.toString();  
    }  
    /** 
     * 计算组合数 
     * 计算组合数的推导过程如下， 
     * 因为直接计算 n! 容易发生数据溢出，故可改为计算 ln(n) 
     * C(n,m) = n!/(m!(n-m)!)  
     * C(n,m) = n(n-1)..(n-m+1)/m! 
     * ln(C(n,m)) = ln(n(n-1)..(n-m+1)/m!) 
     * ln(C(n,m)) = ln(n(n-1)..(n-m+1)) - ln(m!) 
     * ln(C(n,m)) = (ln(n) + ln(n-1) + .. + ln(n-m+1)) 
     *             -(ln(m) + ln(m-1) + .. + ln(1)) 
     * 由上可知 C(n,m) = e^ln(C(n,m)), 
     * 并且由公式右侧可知，m 越小计算量越小 
     * ∵ C(n,m) = C(n,n-m) 
     * ∴ 当 m>n/2.0时,可改为计算 C(n,n-m) 
     * @param n 
     * @param m 
     * @return 
     */  
    public static long getCnm(int n,int m){  
        if (n < 0 || m < 0){  
            throw new IllegalArgumentException("n,m must be > 0");  
        }  
        if (n == 0 || m == 0){  
            return 1;  
        }  
        if (m > n){  
            return 0;  
        }  
        if (m > n/2.0){  
            m = n-m;  
        }  
        double result = 0.0;  
        for (int i=n; i>=(n-m+1); i--){  
            result += Math.log(i);  
        }  
        for (int i=m; i>=1; i--){  
            result -= Math.log(i);  
        }  
        result = Math.exp(result);  
        return Math.round(result);  
    }  
    
    /** 
     * 测试 
     * @param args 
     */  
    public static void main(String[] args) {  
        String[] source = {"1","2","3","4","5"};  
        int m = 1;  
        int size = (int)getCnm(source.length,m);  
        for (int i=0; i<size; i++){  
            String data = getValue(source,m,i);  
            System.out.println(data);  
        }
    }  
}  