#!/bin/bash


. /octopus-agent/shell/lib/wdd-lib-log.sh
. /octopus-agent/shell/lib/wdd-lib-sys.sh


log "Octopus Agent Restart !"

FunctionStart

log "prepare the env"
chmod +x /octopus-agent/shell/lib/wdd-lib-env.sh
/octopus-agent/shell/lib/wdd-lib-env.sh

log ""
#systemctl stop octopus-agent.service
#log "sleep for 5s waiting for agent shutdown !"
#sleep 5

log "restart octopus agent ! $(date)"
systemctl restart octopus-agent.service

FunctionSuccess
FunctionEnd


