package io.wdd.common.beans.rabbitmq;

public enum OctopusMessageType {

    // agent initialization
    INIT,

    // important things agent should do with itself
    AGENT,

    // common shell or order execution
    EXECUTOR,

    // update or report agent status
    STATUS

}
