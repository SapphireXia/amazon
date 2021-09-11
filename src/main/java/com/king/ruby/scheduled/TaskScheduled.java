package com.king.ruby.scheduled;

import com.king.ruby.controller.AmazonController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
public class TaskScheduled {
    Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Autowired
    private AmazonController amazonController;

    @Scheduled(cron = "0 0 */3 * * ?")
    private void startTask() {
        logger.info("执行静态定时任务时间: " + LocalDateTime.now());
        amazonController.start();
    }

}
