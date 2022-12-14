package io.wdd.agent;

import io.wdd.agent.config.utils.NacosConfigurationCollector;
import io.wdd.agent.executor.FunctionExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

//@SpringBootTest
@Slf4j
class AgentApplicationTests {

    @Resource
    FunctionExecutor functionExecutor;

    @Resource
    NacosConfigurationCollector nacosConfigurationCollector;


//    @Test
    void testFileExecute(){

//        ExecutionMessage executionMessage = ExecutionMessage.builder().type("TestFunction").resultKey("simpleFor-test").contend("123456").build();
//
//
//        functionExecutor.execute(executionMessage);



    }

//    @Test
    void contextLoads() {

        // https://zhuanlan.zhihu.com/p/449416472
        // https://cloud.tencent.com/developer/article/1919814
        // https://blog.51cto.com/binghe001/5244823


        try{



            Path absolutePath = Paths.get("").toAbsolutePath();
            System.out.println("absolutePath = " + absolutePath);

            Path toAbsolutePath = Path.of(absolutePath + "/src/test/java/io/wdd/agent").toAbsolutePath();
            System.out.println("toAbsolutePath = " + toAbsolutePath);


            Path path = FileSystems.getDefault().getPath("");
            System.out.println("path.toAbsolutePath() = " + path.toAbsolutePath());


            IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(".sh");
            IOFileFilter directoryFileFilter = DirectoryFileFilter.INSTANCE;

            Collection<File> functionFileList = FileUtils.listFiles(toAbsolutePath.toFile(), fileFilter, directoryFileFilter);


            log.info("all function shell script files are : {}", functionFileList);

            long count = functionFileList.stream().map(
                    file -> {
                        System.out.println("file.getName() = " + file.getName());
                        return null;
                    }
            ).count();

            Map<String, String> collect = functionFileList.stream().collect(Collectors.toMap(
                    functionFile -> functionFile.getName().split(".")[1],
                    functionFile -> {
                        return functionFile.getAbsolutePath();
                    }
            ));

            log.info("map is {}", collect);

            System.out.println("count = " + count);


            System.out.println("this.getClass().getName() = " + this.getClass().getName());
            System.out.println("this.getClass().getCanonicalName() = " + this.getClass().getCanonicalName());
            System.out.println("this.getClass().getPackageName() = " + this.getClass().getPackageName());


//            Properties props =System.getProperties();
//
//            InetAddress ip = InetAddress.getLocalHost();
//            String localName = ip.getHostName();
//            String osName = System.getProperty("os.name");
//            String userName = System.getProperty("user.name");
//            String osVersion = System.getProperty("os.version");
//            String osArch = System.getProperty("os.arch");
//
//            System.out.println("???????????????" + userName);
//            System.out.println("?????????????????????"+props.getProperty("user.home"));
//            System.out.println("??????????????????????????????"+props.getProperty("user.dir"));
//            System.out.println("???????????????" + localName);
//            System.out.println("???????????????" + osName);
//            System.out.println("???????????????" + osVersion);
//            System.out.println("???????????????" + osArch);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
