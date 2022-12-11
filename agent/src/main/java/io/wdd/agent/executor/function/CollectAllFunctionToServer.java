package io.wdd.agent.executor.function;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Lazy
@Slf4j
public class CollectAllFunctionToServer {


    public static Set<String> ALL_FUNCTIONS = new HashSet<>(128);

    /**
     * store the Octopus Agent Functions and Reflection Class Path
     *  key: function name
     *  value: function shell script relative path
     *
     */
    public static HashMap<String, String> FUNCTION_REFLECTION = new HashMap<>(128);


    @PostConstruct
    private void CollectAllFunctionShellScriptName(){

        // scan current package files name and  store them to FUNCTION_REFLECTION


        Path absolutePath = Paths.get("").toAbsolutePath();

        Path currentDirectory = Path.of(absolutePath + "/src/main/java/io/wdd/agent/executor/function").toAbsolutePath();


        IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(".sh");
        IOFileFilter directoryFileFilter = DirectoryFileFilter.INSTANCE;

        Collection<File> functionFileList = FileUtils.listFiles(currentDirectory.toFile(), fileFilter, directoryFileFilter);

        log.debug("all function shell script files are : {}", functionFileList);

        Map<String, String> collect = functionFileList.stream().collect(Collectors.toMap(
                functionFile -> functionFile.getName().split("\\.")[0],
                functionFile -> functionFile.getAbsolutePath()
        ));

        FUNCTION_REFLECTION.putAll(collect);

    }

}
