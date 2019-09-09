package com.dzf.zxkj.common.utils;

import java.util.Map;
import java.util.TreeMap;

public interface IGlobalConstants extends IDefaultValue {

    // uuid , 每个客户端的唯一的uuid
    public final static String uuid = "uuid";
    // 应用类型
    public final static String appid = "appid";
    // 客户端地址
    public final static String remote_address = "remote_address";
    // 用户登录session
    public final static String login_user = "login_user";

    // 用户登录日期session
    public final static String login_date = "login_date";

    // 登录公司session
    public final static String login_corp = "login_corp";

    // 访问的客户端
    public final static String clientid = "clientid";

    // 用户登录失败次数，锁定
    public final static int lock_fail_login = 6;

    // 用户登录失败次数，验证码
    public final static int verify_fail_login = 3;

    // 用户登录失败锁定时间，分钟
    public final static int lock_login_min = 15;

    // 强制退出信息
    public final static String logout_msg = "logout_msg";

    // 获取币种cache的公司id
    public final static String currency_corp = DefaultGroup;

    public final static String RMB_currency_id = "00000100AA10000000000BKT";

    public final static String login_token = "login_token";
    // 现金类科目
    public final static Map<String, Boolean> xjll_km = new TreeMap<String, Boolean>() {
        {
            put("1001", true);
            put("1002", true);
            put("1009", true);
            put("1012", true);
        }
    };
    // 用户退出原因
    public final static Map<Integer, String> logoutType = new TreeMap<Integer, String>() {
        {
            put(0, "未退出");
            put(1, "用户正常退出");
            put(2, "强制退出");
            put(3, "session超出");
        }
    };
    // 用户退出原因
    public final static Map<Integer, String> loginStatus = new TreeMap<Integer, String>() {
        {
            put(0, "登录失败");
            put(1, "登录成功");
        }
    };

    // 合同付款方式
    public final static Map<Integer, String> contractPayMode = new TreeMap<Integer, String>() {
        {
            put(1, "月付");
            put(2, "季付");
            put(3, "年付");
            put(4, "一次付");
            put(5, "半年付");
        }
    };

    public static String DZF_KJ = "dzf_kj"; // 会计端
    public static String SYS_DZF = "sys_dzf"; // 集团管理端
    public static String SYS_DATA = "sys_data"; // 数据中心
    public static String ADMIN_KJ = "admin_kj"; // 会计管理端
    public static String DZF_FACTORY = "dzf_factory";// 会计工厂
    public static String DZF_OWNER = "dzf_owner";// 企业主
    public static String DZF_WEBSITE = "website";// 小微无忧购买网站
    public static String DZF_CHANNEL = "dzf_channel";// 加盟商在线业务系统
    public static String DZF_FAT = "dzf_fat";// 财税服务平台


    public static String POWER_MAP = "powerMap";

    public static String FONTPATH = "/data1/webApp/font/SIMKAI.TTF";// 打印字体
    // 本地C:/windows/fonts/simkai.ttf

    public static String zxkfroleid = "00e9iq00ANYADMINISZHZXKF";// 在线客服角色主键

    public static String openId = "openId";
    public static String formal_user = "formal_user";

    public static int saoma_three = 3;//发票扫码一日只允许扫3次。
}
