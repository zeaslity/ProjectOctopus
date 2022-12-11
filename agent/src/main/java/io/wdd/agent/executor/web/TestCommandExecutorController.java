package io.wdd.agent.executor.web;


import io.wdd.agent.executor.shell.CommandExecutor;
import io.wdd.common.beans.response.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("testExecutor")
public class TestCommandExecutorController {

    @Resource
    CommandExecutor commandExecutor;


    @PostMapping("comand")
    public R<String> testFor(
            @RequestParam(value = "streamKey") String streamKey,
            @RequestParam(value = "command") List<String> command
    ){
        commandExecutor.execute(streamKey, command);

        return R.ok(streamKey);
    }

}
