#!/bin/bash


command_exists() {
  command -v "$@" >/dev/null 2>&1
}

command_exists "docker info"
if [[ $? -ne 0 ]] ; then
echo "no"
else
echo "yes"
fi


dockerVersion="20.10.10"

echo $dockerVersion | cut -d"." -f-2


export JAVA_OPTS="-Xms2048m -Xmx2048m -Dfile.encoding=utf-8  -Dspring.profiles.active=k3s -Dspring.cloud.nacos.config.group=k3s -Dspring.cloud.nacos.config.extension-configs[0].dataId=common-k3s.yaml -Dspring.cloud.nacos.config.extension-configs[0].group=k3s -Ddebug=false -Dlogging.level.io.wdd.server=info"
export OctopusServerContainerName="octopus-server"

docker container stop ${OctopusServerContainerName}
sleep 2
docker container rm ${OctopusServerContainerName}
docker image rmi  docker.io/icederce/wdd-octopus-server:latest


 systemctl status nginx.service | grep -c "active (running)"

docker logs --tail 500 -f ${ServerContainerName}
docker run -d \
    -p 9999:9999 \
    --name ${OctopusServerContainerName} \
    --env JAVA_OPTS="${JAVA_OPTS}" \
    docker.io/icederce/wdd-octopus-server:latest