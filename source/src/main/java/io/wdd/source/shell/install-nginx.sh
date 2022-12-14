#!/bin/bash


. /root/IdeaProjects/ProjectOctopus/source/src/main/java/io/wdd/source/shell/lib/wdd-lib-log.sh

. /root/IdeaProjects/ProjectOctopus/source/src/main/java/io/wdd/source/shell/lib/wdd-lib-env.sh



log "wdd is awesome !"
error "error message"

debug "debug message"
info "woshinibaba!"


debug "-------------------"

log "env TEST_ENV is $(env | grep TEST_ENV)"

TEST_ENV=cccc
log "env TEST_ENV is $(env | grep TEST_ENV)"