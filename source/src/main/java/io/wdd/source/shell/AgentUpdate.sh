#!/bin/bash


containerId=$(docker ps | grep octopus-agent | awk '{print$1}')

docker container stop $(containerId) && docker container rm $(containerId)

echo "y
" | docker container prune

docker image rmi octopus-agent-ubuntu:latest -y

