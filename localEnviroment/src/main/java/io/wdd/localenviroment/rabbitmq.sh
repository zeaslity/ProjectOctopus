#!/usr/bin/env bash


docker run \
       -d \
       --hostname rabbitmq \
       --name rabbitmq \
       -e RABBITMQ_DEFAULT_USER=admin \
       -e RABBITMQ_DEFAULT_PASS=password \
       -e RABBITMQ_DEFAULT_VHOST=wdd \
       -p 5672:5672 \
       rabbitmq:3-management

