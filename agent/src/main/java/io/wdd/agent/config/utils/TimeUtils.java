package io.wdd.agent.config.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
}
