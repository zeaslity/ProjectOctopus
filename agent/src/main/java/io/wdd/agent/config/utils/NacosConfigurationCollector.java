package io.wdd.agent.config.utils;


import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import io.wdd.agent.executor.config.FunctionReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Executor;

import static io.wdd.agent.status.AppStatusCollector.ALL_APP_NEED_TO_MONITOR_STATUS;


@Component
@Lazy
@Slf4j
public class NacosConfigurationCollector {


    /**
     * store the Octopus Agent Functions and Function Command List
     * key: function name
     * value: function shell List<String> contend
     */
    public static HashMap<String, List<List<String>>> ALL_FUNCTION_MAP = new HashMap<>(128);

    /*
     *  listen to the nacos executor shell scripts
     * */
    public static ConfigService NacosConfigService;

    @Value("${spring.cloud.nacos.config.server-addr}")
    public String nacosAddr;

    @Value("${spring.cloud.nacos.config.group}")
    public String group;

    @Value("${spring.cloud.nacos.config.file-extension}")
    public String fileExtension;

    @Value("${octopus.executor.name}")
    public String executorFunctionDataId;

    @Value("${octopus.status.name}")
    public String appStatusDataId;

    @Resource
    FunctionReader functionReader;

    @PostConstruct
    private void CollectAllFunctionFromNacos() {

        try {

            // Initialize the configuration service, and the console automatically obtains the following parameters through the sample code.
            String executorFunctionDataId = this.executorFunctionDataId + "." + fileExtension;
            String appStatusDataId = this.appStatusDataId + "-" + group + "." + fileExtension;

            Properties properties = new Properties();
            properties.put("serverAddr", nacosAddr);
            NacosConfigService = NacosFactory.createConfigService(properties);


            String executorFunctionContent = NacosConfigService.getConfig(executorFunctionDataId, group, 5000);

            String allApplicationNeedToMonitorStatus = NacosConfigService.getConfig(appStatusDataId, group, 5000);


            parseNacosFunctionYamlToMap(executorFunctionContent);

            parseAllApplicationNeedToMonitorStatus(allApplicationNeedToMonitorStatus);


            /**
             *  https://nacos.io/zh-cn/docs/v2/guide/user/sdk.html
             *
             *  dynamically listen to the nacos
             *
             * Actively get the executor functions configuration.
             * */
            NacosConfigService.addListener(executorFunctionDataId, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String newExecutorFunction) {
                    log.debug("functions get from nacos are {}", executorFunctionContent);

                    parseNacosFunctionYamlToMap(newExecutorFunction);
                }
            });


            /**
             * Actively get ALl applications need to monitor
             * */
            NacosConfigService.addListener(appStatusDataId, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String allApplicationNeedToMonitorStatus) {

                    log.debug("all applications need to monitor status has changed to => {}", allApplicationNeedToMonitorStatus);

                    parseAllApplicationNeedToMonitorStatus(allApplicationNeedToMonitorStatus);
                }
            });

        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseAllApplicationNeedToMonitorStatus(String allApplicationNeedToMonitorStatus) {
        Yaml yaml = new Yaml();

        Map<String, Object> map = yaml.load(allApplicationNeedToMonitorStatus);
        Map<String, Object> octopus = (Map<String, Object>) map.get("octopus");
        Map<String, Object> agent = (Map<String, Object>) octopus.get("agent");
        Map<String, Object> status = (Map<String, Object>) agent.get("status");
        ArrayList<String> all_app_from_nacos = (ArrayList<String>) status.get("app");

        // need to keep update to nacos so need to clear the cache
        ALL_APP_NEED_TO_MONITOR_STATUS.clear();

        all_app_from_nacos.stream().forEach(
                app -> {
                    String[] split = app.split("/");
                    ALL_APP_NEED_TO_MONITOR_STATUS.put(split[0], split[1] + ".service");
                }
        );

        log.info("ALL_APP_NEED_TO_MONITOR_STATUS are => {}", ALL_APP_NEED_TO_MONITOR_STATUS);

        // help gc
        map = null;
        octopus = null;
        agent = null;
        status = null;
        all_app_from_nacos = null;

    }

    public void parseNacosFunctionYamlToMap(String allApplicationNeedToMonitorStatus) {

        Yaml yaml = new Yaml();

        yaml.loadAll(allApplicationNeedToMonitorStatus).iterator().forEachRemaining(realFunction -> {

            if (!(realFunction instanceof LinkedHashMap)) {
                System.out.println("realFunction = " + realFunction);
            }

            Map<String, String> stringMap = (Map<String, String>) realFunction;

            Optional<String> functionName = stringMap.keySet().stream().findFirst();

            List<List<String>> commandList = functionReader.ReadStringToCommandList(stringMap.get(functionName.get()));

            /*log.info("Function {} , content is {}", functionName.get(), commandList);*/

            ALL_FUNCTION_MAP.put(functionName.get(), commandList);

        });

        log.info("ALL_FUNCTION_MAP has been updated ! ---> {}", ALL_FUNCTION_MAP);

    }


    /**
     * due to can't get shell from the jar file
     * this is deprecated
     */
//    @PostConstruct
//    private void CollectAllFunctionShellScriptName() {
//
//        // scan current package files name and  store them to FUNCTION_REFLECTION
//
//
//        Path absolutePath = Paths.get("").toAbsolutePath();
//        log.info("current absolute path is {}", absolutePath);
//
//        Path currentDirectory = Path.of(absolutePath + "/src/main/java/io/wdd/agent/executor/function").toAbsolutePath();
//
//
//        IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(".sh");
//        IOFileFilter directoryFileFilter = DirectoryFileFilter.INSTANCE;
//
//        Collection<File> functionFileList = FileUtils.listFiles(currentDirectory.toFile(), fileFilter, directoryFileFilter);
//
//        log.debug("all function shell script files are : {}", functionFileList);
//
//        Map<String, String> collect = functionFileList.stream().collect(Collectors.toMap(
//                functionFile -> functionFile.getName().split("\\.")[0],
//                functionFile -> functionFile.getAbsolutePath()
//        ));
//
//        ALL_FUNCTION_MAP.putAll(collect);
//
//    }

}
