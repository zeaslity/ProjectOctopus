#!/bin/bash



. /octopus-agent/shell/lib/wdd-lib-log.sh



# 判断命令是否存在
command_exists() {
  command -v "$@" >/dev/null 2>&1
}


