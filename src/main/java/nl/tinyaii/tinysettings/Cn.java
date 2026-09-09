package nl.tinyaii.tinysettings;

/**
 * 枚举值中文显示翻译（内部存英文，界面全中文）
 */
public final class Cn {
    private Cn() {}

    /** 难度: PEACEFUL->和平 EASY->简单 NORMAL->普通 HARD->困难 */
    public static String difficulty(String v) {
        switch (v == null ? "" : v.toUpperCase()) {
            case "PEACEFUL": return "和平";
            case "EASY":     return "简单";
            case "HARD":     return "困难";
            case "NORMAL":
            default:         return "普通";
        }
    }

    /** 时间: DAY->永昼 NIGHT->永夜 NORMAL->正常 */
    public static String time(String v) {
        switch (v == null ? "" : v.toUpperCase()) {
            case "DAY":   return "永昼";
            case "NIGHT": return "永夜";
            case "NORMAL":
            default:      return "正常";
        }
    }

    /** 天气: CLEAR->晴 RAIN->雨 THUNDER->雷雨 NORMAL->正常 */
    public static String weather(String v) {
        switch (v == null ? "" : v.toUpperCase()) {
            case "CLEAR":   return "晴";
            case "RAIN":    return "雨";
            case "THUNDER": return "雷雨";
            case "NORMAL":
            default:        return "正常";
        }
    }

    /** 开/关 */
    public static String onOff(boolean b) { return b ? "开" : "关"; }
}