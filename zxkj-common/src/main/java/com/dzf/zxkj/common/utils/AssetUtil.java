package com.dzf.zxkj.common.utils;

public class AssetUtil {
    public static String getAssetProperty(int assetproperty) {
        switch (assetproperty) {
            case 0:
                return "固定资产";
            case 1:
                return "无形资产";
            case 2:
                return "固定资产";
            case 3:
                return "待摊费用";
        }
        return "";
    }

    public static String getAssetAccount(int assetAccount) {
        switch (assetAccount) {
            case 0:
                return "资产原值";
            case 1:
                return "累计折旧";
            case 2:
                return "资产净值";
            case 3:
                return "待摊费用";
        }
        return "";
    }
}
