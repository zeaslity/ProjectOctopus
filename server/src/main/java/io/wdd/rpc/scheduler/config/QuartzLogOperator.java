package io.wdd.rpc.scheduler.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *  save the octopus quartz log to database
 */
@Service
@Slf4j
public class QuartzLogOperator {

    public boolean save(){

        log.info("QuartzLogOperator pretend to have saved the log !");

        return true;

    }


}
