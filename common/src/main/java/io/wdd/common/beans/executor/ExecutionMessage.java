package io.wdd.common.beans.executor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ExecutionMessage {

    private String type;

    private List<String> commandList;

    private String resultKey;

}
