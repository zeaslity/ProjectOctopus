#!/bin/bash

docker container stop octopus-agent-ubuntu && docker container rm octopus-agent-ubuntu

docker run \
  -d \
  --privileged \
  --net=host \
  --pid=host \
  --ipc=host \
  --volume /:/host \
  --name=octopus-agent-ubuntu \
  octopus-agent-ubuntu:latest \

docker logs --tail -f octopus-agent-ubuntu

docker run \
  -d \
  -it \
  --env serverName="UbuntuStation" \
  --env serverIpPbV4="112.12312.1.1" \
  --env serverIpInV4="10.250.0.20" \
  --env serverIpPbV6="" \
  --env serverIpInV6="" \
  --env location="Chengdu" \
  --env provider="Lenovo" \
  --env managePort="22" \
  --env cpuBrand="i7 8700" \
  --env cpuCore="6" \
  --env memoryTotal="8 GB" \
  --env diskTotal="1 TB" \
  --env diskUsage="200 GB" \
  --env osInfo="Ubuntu 20.04.04" \
  --env osKernelInfo="5.10" \
  --env tcpControl="bbr" \
  --env virtualization="dedicated" \
  --env ioSpeed="150 MB/s" \
  --privileged \
  --net=host \
  --pid=host \
  --ipc=host \
  --volume /:/host \
  --name=octopus-agent-ubuntu \
  octopus-agent-ubuntu:latest \
#  chroot /host && /bin/bash
  /bin/bash


