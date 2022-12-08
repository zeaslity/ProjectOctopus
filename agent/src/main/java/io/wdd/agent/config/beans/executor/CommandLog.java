package io.wdd.agent.config.beans.executor;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.AccessType;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandLog {

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String lineTime;

    private String lineContend;

}
