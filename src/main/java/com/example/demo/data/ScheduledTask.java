package com.example.demo.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTask {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 自动扫描，启动时间点之后5秒执行一次
     * http://cron.qqe2.com/ 进行在线表达式编写
     *
     * 自定义线程池
     *
     *     从控制台输出可以看见，多任务使用的是同一个线程。可结合上章节的异步调用来实现不同任务使用不同的线程进行任务执行。
     */
    @Async("asyncThreadPoolTaskExecutor")
//    @Scheduled(fixedRate=5000)
    public void getCurrentDate() {
        logger.info("Scheduled定时任务执行：" + new Date());
    }

    //动态添加定时任务
    //
    //    使用注解的方式，无法实现动态的修改或者添加新的定时任务的，这个使用就需要使用编程的方式进行任务的更新操作了。
    //   可直接使用ThreadPoolTaskScheduler或者SchedulingConfigurer接口进行自定义定时任务创建。
    //

    @Autowired
    private TaskScheduler taskScheduler;

    private String startTaskScheduler() {
        taskScheduler.schedule(new Runnable() {

            @Override
            public void run() {
                logger.info("startTaskScheduler定时任务：" + new Date());
            }
        }, new CronTrigger("0/3 * * * * ?"));//每3秒执行一次
        return "startTaskScheduler!";
    }

}
