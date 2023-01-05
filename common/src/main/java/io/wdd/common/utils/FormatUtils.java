package io.wdd.common.utils;

import java.text.DecimalFormat;

public class FormatUtils {


    /**
     * 格式化输出百分比
     *
     * @param rate
     * @return
     */
    public static String formatRate(double rate) {
        return new DecimalFormat("#.##%").format(rate);
    }

    /**
     * 格式化输出大小 B/KB/MB...
     *
     * @param size
     * @return
     */
    public static String formatData(long size) {
        if (size <= 0L) {
            return "0B";
        } else {
            int digitGroups = Math.min(DataUnit.UNIT_NAMES.length - 1, (int) (Math.log10((double) size) / Math.log10(1024.0D)));
            return (new DecimalFormat("#,##0.##")).format((double) size / Math.pow(1024.0D, digitGroups)) + " " + DataUnit.UNIT_NAMES[digitGroups];
        }
    }

}
