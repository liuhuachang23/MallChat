package com.lhc.mallchat.common.common.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName MyUncaughtExceptionHandler
 * @Description TODO
 * @Author Administrator
 * @Date 2024/7/10 19:07
 * @Version 1.0
 */
@Slf4j
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in thread " + e);
    }

}
