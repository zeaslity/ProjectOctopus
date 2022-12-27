#!/bin/bash


. ./lib/wdd-lib-log.sh
. ./lib/wdd-lib-sys.sh


FunctionStart

log "prepare the env"
chmod +x ./lib/wdd-lib-env.sh
./lib/wdd-lib-env.sh

systemctl stop octopus-agent.service
systemctl start octopus-agent.service

FunctionSuccess
FunctionEnd


