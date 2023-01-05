package io.wdd.common.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static ByteBuffer currentTimeByteBuffer() {

        byte[] timeBytes = LocalDateTime.now(ZoneId.of("UTC+8")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).getBytes(StandardCharsets.UTF_8);

        return ByteBuffer.wrap(timeBytes);
    }


    /**
     * @return UTC+8 [ yyyy-MM-dd HH:mm:ss ] Time String
     */
    public static String currentTimeString() {

        return LocalDateTime.now(ZoneId.of("UTC+8")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    public static String localDateTimeString(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    /**
     * https://memorynotfound.com/calculate-relative-time-time-ago-java/
     *
     *  calculate relative time from now on
     *   like 5 days, 3 hours, 16 minutes  level 3
     *
     * */

    private static final Map<String, Long> times = new HashMap<>(16);

    static {
        times.put("year", TimeUnit.DAYS.toMillis(365));
        times.put("month", TimeUnit.DAYS.toMillis(30));
        times.put("week", TimeUnit.DAYS.toMillis(7));
        times.put("day", TimeUnit.DAYS.toMillis(1));
        times.put("hour", TimeUnit.HOURS.toMillis(1));
        times.put("minute", TimeUnit.MINUTES.toMillis(1));
        times.put("second", TimeUnit.SECONDS.toMillis(1));
    }

    public static String toRelative(long duration, int maxLevel) {
        StringBuilder res = new StringBuilder();
        int level = 0;
        for (Map.Entry<String, Long> time : times.entrySet()){
            long timeDelta = duration / time.getValue();
            if (timeDelta > 0){
                res.append(timeDelta)
                        .append(" ")
                        .append(time.getKey())
                        .append(timeDelta > 1 ? "s" : "")
                        .append(", ");
                duration -= time.getValue() * timeDelta;
                level++;
            }
            if (level == maxLevel){
                break;
            }
        }
        if ("".equals(res.toString())) {
            return "0 seconds ago";
        } else {
            res.setLength(res.length() - 2);
            res.append(" ago");
            return res.toString();
        }
    }

    public static String toRelative(long duration) {
        return toRelative(duration, times.size());
    }

    public static String toRelative(Date start, Date end){
        assert start.after(end);
        return toRelative(end.getTime() - start.getTime());
    }

    public static String toRelative(Date start, Date end, int level){
        assert start.after(end);
        return toRelative(end.getTime() - start.getTime(), level);
    }
}
