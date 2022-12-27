#!/bin/bash


. ./lib/wdd-lib-log.sh
. ./lib/wdd-lib-sys.sh
. ./lib/wdd-lib-os.sh


RepoSourcePath=https://raw.githubusercontent.com/zeaslity/ProjectOctopus/main/source/src/main/java/io/wdd/source/shell



CheckAndDownloadLatestVersion


if [[ ! -f /octopus-agent/agent-reboot.sh ]]; then\
    warn "agent-bootup.sh not exist! start to download !"
   cd /octopus-agent
   wget $RepoSourcePath/agent-reboot.sh
fi


log "start to reboot the octopus agent !"
chmod +x /octopus-agent/agent-reboot.sh
/octopus-agent/agent-reboot.sh
