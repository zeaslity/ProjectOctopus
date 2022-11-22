FROM eclipse-temurin:11-jre-focal

MAINTAINER zeaslity@gmail.com

RUN /bin/sh -c apt-get update  && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends iputils-ping net-tools dnsutils lsof curl wget mtr-tiny vim   && echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen  && locale-gen en_US.UTF-8 && rm -rf /var/lib/apt/lists/*