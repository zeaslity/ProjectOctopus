#!/bin/bash


. /octopus-agent/shell/lib/wdd-lib-log.sh
. /octopus-agent/shell/lib/wdd-lib-sys.sh


FunctionStart

log "prepare the env"
chmod +x /octopus-agent/shell/lib/wdd-lib-env.sh
/octopus-agent/shell/lib/wdd-lib-env.sh

systemctl stop octopus-agent.service
systemctl start octopus-agent.service

FunctionSuccess
FunctionEnd


