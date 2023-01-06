package io.wdd.common.beans.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class AppStatusInfo {

    Set<String> Healthy;

    Set<String> Failure;

    Set<String> NotInstall;

}
