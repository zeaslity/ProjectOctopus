
# Base images that the image needs to depend on
FROM openjdk:11-jre-alpine

# The name of the maintainer
MAINTAINER super-wdd

# Set environment variables
ENV TZ=Asia/Shanghai JAVA_OPTS="-Xms512m -Xmx512m"

# Set time zone
RUN set -eux; \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime; \
    echo $TZ > /etc/timezone \

# Create Folder
RUN mkdir -p /wdd

# Define the work dir
WORKDIR /wdd

# Copy the jar and rename it
COPY ./target/server-*.jar /server.jar

# When the docker container starts, run the jar
ENTRYPOINT exec java ${JAVA_OPTS} -jar  /wdd/lfs-admin.jar
