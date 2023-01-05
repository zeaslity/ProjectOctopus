package io.wdd.common.beans.status;


import io.wdd.common.utils.FormatUtils;
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

    private String name;
    private String displayName;

    private String macAddr;

    private String mtu;

    private String[] ipv4Addr;
    private String[] ipv6Addr;

    private String trafficRecv;

    private String trafficSend;

    public static List<NetworkInfo> mapFromNetworkIFS(List<NetworkIF> networkIFList) {

        return networkIFList.stream().map(networkIF -> NetworkInfo.builder().name(networkIF.getName()).displayName(networkIF.getDisplayName()).mtu(String.valueOf(networkIF.getMTU())).macAddr(networkIF.getMacaddr()).ipv4Addr(generateIPDICRFromNetworkIFList(networkIF, 4)).ipv6Addr(generateIPDICRFromNetworkIFList(networkIF, 6)).trafficSend(FormatUtils.formatData(networkIF.getBytesSent())).trafficRecv(FormatUtils.formatData(networkIF.getBytesRecv())).build()).collect(Collectors.toList());
    }

    private static String[] generateIPDICRFromNetworkIFList(NetworkIF networkIF, int Ipv4OrIpv6) {

        String[] iPAddr;
        Short[] subnetMasks;

        if (Ipv4OrIpv6 == 4) {
            iPAddr = networkIF.getIPv4addr();
            subnetMasks = networkIF.getSubnetMasks();
        } else {
            iPAddr = networkIF.getIPv6addr();
            subnetMasks = networkIF.getPrefixLengths();
        }

        String[] result = new String[iPAddr.length];
        for (int index = 0; index < iPAddr.length; index++) {
            result[index] = iPAddr[index] + "/" + subnetMasks[index];
        }

        return result;
    }
}
