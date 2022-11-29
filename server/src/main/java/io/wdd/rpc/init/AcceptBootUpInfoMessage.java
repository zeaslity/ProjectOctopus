package io.wdd.rpc.init;


import com.fasterxml.jackson.databind.json.JsonMapper;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.handler.MyRuntimeException;
import io.wdd.common.utils.MessageUtils;
import io.wdd.server.beans.po.ServerInfoPO;
import io.wdd.server.beans.vo.ServerInfoVO;
import io.wdd.server.utils.DaemonDatabaseOperator;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * The type Accept boot up info message.
 */
@Service
public class AcceptBootUpInfoMessage {


    @Resource
    DaemonDatabaseOperator databaseOperator;

    /**
     * Handle octopus agent boot up info.
     *
     * @param message the message
     */
    @RabbitHandler
    @RabbitListener(
            bindings =
            @QueueBinding(
                    value = @Queue(name = "${octopus.message.init_to_server}"),
                    exchange = @Exchange(name = "${octopus.message.init_exchange}", type = "direct"),
                    key = {"${octopus.message.init_to_server_key}"}
            )
            ,
            ackMode = "MANUAL"
    )
    public void handleOctopusAgentBootUpInfo(Message message) {

        JsonMapper jsonMapper = new JsonMapper();
        ServerInfoVO serverInfoVO;

        try {
            serverInfoVO = jsonMapper.readValue(message.getBody(), ServerInfoVO.class);
        } catch (IOException e) {
            throw new MyRuntimeException("parse rabbit server info error, please check !");
        }


        // 1. check if information is correct
        if(!validateServerInfo(serverInfoVO)){
            throw new MyRuntimeException("server info validated failed !");
        };
        // 2. generate the unique topic for agent
        String agentQueueTopic = generateAgentQueueTopic(serverInfoVO);
        // 3. save the agent info into database
        // backend fixed thread daemon to operate the database ensuring the operation is correct !
        if(!databaseOperator.saveInitOctopusAgentInfo(serverInfoVO)){
            throw new MyRuntimeException("database save agent info error !");
        }

        // 4. send InitMessage to agent

    }

    private String generateAgentQueueTopic(ServerInfoVO serverInfoVO) {
        return null;
    }

    private boolean validateServerInfo(ServerInfoVO serverInfoVO) {

        return false;
    }


}
