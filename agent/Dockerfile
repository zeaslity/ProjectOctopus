
# Base images that the image needs to depend on
FROM icederce/eclipse-temurin-11-jre-focal

# Set environment variables
ENV TZ=Asia/Shanghai ServerName="" serverIpPbV4="" serverIpInV4="" serverIpPbV6="" serverIpInV6="" location=""  provider="" managePort="" cpuBrand="" cpuCore="" memoryTotal="" diskTotal="" diskUsage="" osInfo="" osKernelInfo="" tcpControl="" virtualization="" ioSpeed="" JAVA_OPTS="-Xms2028m -Xmx2048m"

# Set time zone
RUN set -eux; \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime; \
    echo $TZ > /etc/timezone \

# Create Folder
RUN mkdir -p /wdd

# Define the work dir
WORKDIR /wdd

# Copy the jar and rename it
COPY ./agent/target/agent-*.jar /wdd/agent.jar

# When the docker container starts, run the jar
ENTRYPOINT exec java ${JAVA_OPTS} -jar  /wdd/agent.jar
