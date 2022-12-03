#!/usr/bin/env bash

docker run -d \
  -e ServerName: "${ServerName}"         \
  -e serverIpPbV4: "$serverIspPbV4"   \
  -e serverIpInV4: "$serverIpInV4"   \
  -e serverIpPbV6: "$serverIpPbV6"   \
  -e serverIpInV6:  "$serverIpInV6"  \
  -e location: "$location"   \
  -e provider: "$provider"   \
  -e managePort:  "$managePort"  \
  -e cpuBrand: "$cpuBrand"   \
  -e cpuCore: "$cpuCore"    \
  -e memoryTotal: "$memoryTotal"    \
  -e diskTotal: "$diskTotal"   \
  -e diskUsage: "$diskUsage"    \
  -e osInfo: "$osInfo"   \
  -e osKernelInfo: "$osKernelInfo"    \
  -e tcpControl: "$tcpControl"   \
  -e virtualization: "$virtualization"   \
  -e ioSpeed: "$ioSpeed"   \
  --privileged \
  --net=host \
  --pid=host \
  --ipc=host \
  --volume /:/host \
  icederce/wdd-octopus-agent:latest