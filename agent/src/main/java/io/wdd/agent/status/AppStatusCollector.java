package io.wdd.agent.status;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
public class AppStatusCollector {

    // storage all the applications agent status should report
    public static final HashMap<String, String> ALL_APP_NEED_TO_MONITOR_STATUS = new HashMap<>(16);

    /**
     * not very good
     * but also a kind of method to dynamically listen to nacos configuration change
     */
    /*@NacosValue(value = "${octopus.agent.status.enable}" , autoRefreshed = true)
    private String all_app_from_nacos;*



    /*@NacosConfigListener(
            groupId = "k3s",
            dataId = "octopus-agent-k3s.yaml",
            type = ConfigType.YAML,
            properties =
    )
    public void onMessage(String content){

        log.debug("update octopus-agent nacos config are ==> {} ", content);

        Yaml yaml = new Yaml();
        Object load = yaml.load(content);

        System.out.println("load = " + load);


    }*/

}