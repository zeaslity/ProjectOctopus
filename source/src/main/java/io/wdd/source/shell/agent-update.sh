#!/bin/bash


. /octopus-agent/shell/lib/wdd-lib-log.sh
. /octopus-agent/shell/lib/wdd-lib-sys.sh
. /octopus-agent/shell/lib/wdd-lib-os.sh


RepoSourcePath=https://raw.githubusercontent.com/zeaslity/ProjectOctopus/main/source/src/main/java/io/wdd/source/shell


CheckAndDownloadLatestVersion

if [[ ! -f /octopus-agent/shell/agent-reboot.sh ]]; then\
  warn "agent-bootup.sh not exist! start to download !"
  cd /octopus-agent/shell
  wget $RepoSourcePath/agent-reboot.sh
fi


log "start to reboot the octopus agent !"
chmod +x /octopus-agent/shell/agent-reboot.sh
/octopus-agent/shell/agent-reboot.sh
