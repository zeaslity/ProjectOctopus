#!/usr/bin/env bash

docker run -d \
  --env serverName="${ServerName}"         \
  --env serverIpPbV4="$serverIspPbV4"   \
  --env serverIpInV4="$serverIpInV4"   \
  --env serverIpPbV6="$serverIpPbV6"   \
  --env serverIpInV6= "$serverIpInV6"  \
  --env location="$location"   \
  --env provider="$provider"   \
  --env managePort= "$managePort"  \
  --env cpuBrand="$cpuBrand"   \
  --env cpuCore="$cpuCore"    \
  --env memoryTotal="$memoryTotal"    \
  --env diskTotal="$diskTotal"   \
  --env diskUsage="$diskUsage"    \
  --env osInfo="$osInfo"   \
  --env osKernelInfo="$osKernelInfo"    \
  --env tcpControl="$tcpControl"   \
  --env virtualization="$virtualization"   \
  --env ioSpeed="$ioSpeed"   \
  --privileged \
  --net=host \
  --pid=host \
  --ipc=host \
  --volume /:/host \
  icederce/wdd-octopus-agent:latest