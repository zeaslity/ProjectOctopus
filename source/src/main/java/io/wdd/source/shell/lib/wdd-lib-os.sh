#!/bin/bash


. ./wdd-lib-log.sh

OctopusAgentUrl=https://happybirthday.107421.xyz/octopus-agent/


CheckAndDownloadLatestVersion(){

  log "checking for the latest version"
  local latestVersion=$(curl $OctopusAgentUrl | grep -v h1 | grep "a href=" | awk '{print$2}' |cut -d">" -f2 | cut -d"<" -f1)

  log "octopus agent latest version is => [ $latestVersion ]"

  rm -rf /octopus-agent/*.jar
  cd /octopus-agent

  log "start to download the latest version !"

  wget "$OctopusAgentUrl$latestVersion"
  cp "$OctopusAgentUrl$latestVersion" agent.jar

  log ""
  ls /octopus-agent/ | grep jar

}