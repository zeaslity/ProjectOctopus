FROM eclipse-temurin:11-jre-focal

MAINTAINER zeaslity@gmail.com

RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends iputils-ping net-tools dnsutils lsof curl wget mtr-tiny vim && rm -rf /var/lib/apt/lists/*