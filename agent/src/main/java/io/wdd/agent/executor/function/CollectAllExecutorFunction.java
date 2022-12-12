package io.wdd.agent.executor.function;


import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.agent.executor.config.FunctionReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Lazy
@Slf4j
public class CollectAllExecutorFunction {


    /**
     * store the Octopus Agent Functions and Reflection Class Path
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
    public String dataId;

    @Resource
    FunctionReader functionReader;

    @Resource
    ObjectMapper objectMapper;

    @PostConstruct
    private void CollectAllFunctionFromNacos() {

        try {

            // Initialize the configuration service, and the console automatically obtains the following parameters through the sample code.
            String completeDataId = dataId + "." + fileExtension;
            Properties properties = new Properties();
            properties.put("serverAddr", nacosAddr);

            NacosConfigService = NacosFactory.createConfigService(properties);

            // Actively get the configuration.
            String content = NacosConfigService.getConfig(completeDataId, group, 5000);

            log.info("functions get from nacos are {}", content);
            parseNacosFunctionYamlToMap(content);


        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseNacosFunctionYamlToMap(String content) {

        Yaml yaml = new Yaml();

        yaml.loadAll(content).iterator().forEachRemaining(
                realFunction -> {

                    if (!(realFunction instanceof LinkedHashMap)) {
                        System.out.println("realFunction = " + realFunction);
                    }

                    Map<String, String> stringMap = (Map<String, String>) realFunction;

                    Optional<String> functionName = stringMap.keySet().stream().findFirst();

                    List<List<String>> commandList = functionReader.ReadStringToCommandList(stringMap.get(functionName.get()));

                    /*log.info("Function {} , content is {}", functionName.get(), commandList);*/

                    ALL_FUNCTION_MAP.put(
                            functionName.get(),
                            commandList
                    );

                }
        );

        log.info("ALL_FUNCTION_MAP has been updated ! ---> {}", ALL_FUNCTION_MAP);

    }


    /**
     * due to can't get shell from the jar file
     * this is deprecated
     */
//    @PostConstruct
    private void CollectAllFunctionShellScriptName() {

        // scan current package files name and  store them to FUNCTION_REFLECTION


//        Path absolutePath = FileSystems.getDefault().getPath("" ).toAbsolutePath();
        Path absolutePath = Paths.get("").toAbsolutePath();
        log.info("current absolute path is {}", absolutePath);

        Path currentDirectory = Path.of(absolutePath + "/src/main/java/io/wdd/agent/executor/function").toAbsolutePath();


        IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(".sh");
        IOFileFilter directoryFileFilter = DirectoryFileFilter.INSTANCE;

        Collection<File> functionFileList = FileUtils.listFiles(currentDirectory.toFile(), fileFilter, directoryFileFilter);

        log.debug("all function shell script files are : {}", functionFileList);

        Map<String, String> collect = functionFileList.stream().collect(Collectors.toMap(
                functionFile -> functionFile.getName().split("\\.")[0],
                functionFile -> functionFile.getAbsolutePath()
        ));

//        ALL_FUNCTION_MAP.putAll(collect);

    }

}
