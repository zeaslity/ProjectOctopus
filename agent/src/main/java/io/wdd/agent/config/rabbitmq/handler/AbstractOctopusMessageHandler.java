package io.wdd.agent.config.rabbitmq.handler;

import io.wdd.common.beans.rabbitmq.OctopusMessage;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;


/**
 *  response chain design pattern
 */
@Configuration
public abstract class AbstractOctopusMessageHandler  {

    protected AbstractOctopusMessageHandler next;

    public void addHandler(AbstractOctopusMessageHandler handler) {
        this.next = handler;

    }

    public AbstractOctopusMessageHandler getNextHandler() {
        return next;
    }

    public static class Builder {

        private AbstractOctopusMessageHandler head;
        private AbstractOctopusMessageHandler tail;

        public Builder addHandler(AbstractOctopusMessageHandler nextHandler) {
            if (this.head == null) {
                this.head = this.tail = nextHandler;
                return this;
            }
            this.tail.addHandler(nextHandler);
            this.tail = nextHandler;

            return this;
        }


        public AbstractOctopusMessageHandler build(){
            return this.head;
        }

    }


    public abstract boolean handle(OctopusMessage octopusMessage);
}
