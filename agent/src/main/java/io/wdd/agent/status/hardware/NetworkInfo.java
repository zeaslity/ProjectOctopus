package io.wdd.agent.status.hardware;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import oshi.hardware.NetworkIF;

import java.util.List;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class NetworkInfo {


    public static List<NetworkInfo> mapFromNetworkIFS(List<NetworkIF> networkIFList){

        return networkIFList.stream().map(
                networkIF -> NetworkInfo.builder()
                        .build()
        ).collect(Collectors.toList());
    }
}
