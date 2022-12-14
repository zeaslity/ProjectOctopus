package io.wdd.agent.config.beans.executor;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class StreamSenderEntity {

    private String streamKey;

    private ArrayList<String> cachedCommandLog;

    private boolean waitToSendLog;

    private int startIndex;

}
