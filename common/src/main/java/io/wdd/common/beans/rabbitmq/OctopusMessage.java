package io.wdd.common.beans.rabbitmq;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class OctopusMessage {

    String uuid;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime init_time;

    OctopusMessageType type;

    // server send message content
    Object content;

    // agent reply message content
    Object result;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime ac_time;

}
