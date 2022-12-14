package io.wdd.rpc.init;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;
import io.wdd.common.handler.MyRuntimeException;
import io.wdd.rpc.message.sender.ToAgentMessageSender;
import io.wdd.server.beans.vo.ServerInfoVO;
import io.wdd.server.utils.DaemonDatabaseOperator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The type Accept boot up info message.
 */
@Service
@Slf4j(topic = "octopus agent init ")
public class AcceptAgentInitInfo {

    public static Set<String> ALL_SERVER_CITY_INFO = new HashSet<>(
            Arrays.asList(
                    "HongKong", "Tokyo", "Seoul", "Phoenix", "London", "Shanghai", "Chengdu"
            )
    );
    public static Set<String> ALL_SERVER_ARCH_INFO = new HashSet<>(
            Arrays.asList(
                    "amd64", "arm64", "arm32", "xia32", "miples"
            )
    );
    @Resource
    InitRabbitMQConfig initRabbitMQConfig;
    @Resource
    RedisTemplate redisTemplate;
    /**
     * The Database operator.
     */
    @Resource
    DaemonDatabaseOperator databaseOperator;


    @Resource
    ObjectMapper objectMapper;
    /**
     * The To agent order.
     */
    @Resource
    ToAgentMessageSender toAgentMessageSender;


    /**
     * Handle octopus agent boot up info.
     *
     * @param message the message
     */
    @SneakyThrows
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
    public void handleOctopusAgentBootUpInfo(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        // manual ack the rabbit message
        // https://stackoverflow.com/questions/38728668/spring-rabbitmq-using-manual-channel-acknowledgement-on-a-service-with-rabbit

        ServerInfoVO serverInfoVO;

        try {

            serverInfoVO = objectMapper.readValue(message.getBody(), ServerInfoVO.class);

            // 1. check if information is correct
            if (!validateServerInfo(serverInfoVO)) {
                throw new MyRuntimeException("server info validated failed !");
            }

            // 2. generate the unique topic for agent
            String agentQueueTopic = generateAgentQueueTopic(serverInfoVO);
            serverInfoVO.setTopicName(agentQueueTopic);

            // cache enabled for agent re-register
//            if (!checkAgentAlreadyRegister(agentQueueTopic)) {
//                log.info("[AGENT INIT] - agent not exist ! start to register !");
//            }
            // whether agent is registered already
            // save or update the octopus agent server info
            // 3. save the agent info into database
            // backend fixed thread daemon to operate the database ensuring the operation is correct !
            if (!databaseOperator.saveInitOctopusAgentInfo(serverInfoVO)) {
                throw new MyRuntimeException("database save agent info error !");
            }

            // 4. generate the Octopus Agent Status Redis Stream Key & Consumer-Group
            generateAgentStatusRedisStreamConsumerGroup(serverInfoVO.getServerName());

            // 5. send InitMessage to agent
            sendInitMessageToAgent(serverInfoVO);


        } catch (IOException e) {

            /**
             * ????????????????????????
             * basicNack(long deliveryTag, boolean multiple, boolean requeue)
             * requeue:true????????????????????????????????????,?????????????????????????????????;
             *         false:???????????????
             */

            // long deliveryTag, boolean multiple, boolean requeue

            channel.basicNack(deliveryTag, false, true);
            // long deliveryTag, boolean requeue
            // channel.basicReject(deliveryTag,true);

            // ??????????????????????????????????????????
            TimeUnit.SECONDS.sleep(5);

            /*
             * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
             * ????????????3??????????????????????????????????????????????????????????????????
             */


            throw new MyRuntimeException(" Octopus Server Initialization Error, please check !");
        }

        /**
         * ????????????????????????
         * ????????????????????????
         * basicAck(long deliveryTag, boolean multiple)
         * deliveryTag:?????????????????????????????????????????????;
         * multiple:???true????????????????????????,????????????deliveryTag???5,??????????????????
         * deliveryTag???5?????????????????????;???????????????false
         */
        // ack the rabbitmq info
        // If all logic is successful
        log.info("Agent [ {} ] has init successfully !", serverInfoVO.getTopicName());
        channel.basicAck(deliveryTag, false);
    }

    private void generateAgentStatusRedisStreamConsumerGroup(String serverName) {

        String statusStreamKey = serverName + "-status";

        // check for octopus-server consumer group
        if (redisTemplate.opsForStream().groups(statusStreamKey)
                .stream()
                .filter(
                        group -> group.groupName().startsWith("Octopus")
                ).collect(Collectors.toSet()).contains(Boolean.FALSE)) {

            log.debug(" not find the group, recreate");
            // not find the group, recreate
            redisTemplate.opsForStream().createGroup(statusStreamKey, "OctopusServer");
        }

        log.debug("octopus agent [ {} ] status report stream key [ {} ] has been created !", serverName, statusStreamKey);
    }

    private boolean checkAgentAlreadyRegister(String agentQueueTopic) {

        Optional<String> first = databaseOperator.getAllServerName().stream().
                filter(serverName -> agentQueueTopic.startsWith(serverName))
                .findFirst();

        return first.isPresent();
    }

    private boolean sendInitMessageToAgent(ServerInfoVO serverInfoVO) {

        OctopusMessage octopusMessage = OctopusMessage.builder()
                .type(OctopusMessageType.INIT)
                // should be the OctopusExchange Name
                .content(String.valueOf(initRabbitMQConfig.OCTOPUS_EXCHANGE))
                .init_time(LocalDateTime.now())
                .uuid(serverInfoVO.getTopicName())
                .build();

        toAgentMessageSender.sendINIT(octopusMessage);

        return true;
    }

    /**
     * Generate Octopus Agent Server Communicate Unique Topic
     * <p>
     * Strategy:
     * 1. total length 28 bytes( 28 english letters max)
     * 2. hostname -- machine_id
     * city-arch-num-machine_id(prefix 6 bytes)
     * 12  1  5 1 2 1 6 == 28
     * NewYork-amd64-01-53df13
     * Seoul-arm64-01-9sdd45
     *
     * @param serverInfoVO server info
     * @return
     */
    private String generateAgentQueueTopic(ServerInfoVO serverInfoVO) {

        // topic generate strategy
        String serverName = serverInfoVO.getServerName();
        serverName.replace(" ", "");
        serverInfoVO.setServerName(serverName);

        // validate serverName
        String[] split = serverName.split("-");
        if (split.length <= 2 || !ALL_SERVER_CITY_INFO.contains(split[0]) || !ALL_SERVER_ARCH_INFO.contains(split[1])) {
            throw new MyRuntimeException(" server name not validated !");
        }

        String machineIdPrefixSixBytes = String.valueOf(serverInfoVO.getMachineId().toCharArray(), 0, 6);

        return serverName + "-" + machineIdPrefixSixBytes;
    }

    private boolean validateServerInfo(ServerInfoVO serverInfoVO) {


        log.info("server info validated success !");
        return true;
    }


}
