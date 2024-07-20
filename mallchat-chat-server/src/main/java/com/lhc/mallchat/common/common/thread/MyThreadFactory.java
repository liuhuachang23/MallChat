package com.lhc.mallchat.common.common.thread;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ThreadFactory;

/**
 * @ClassName MyThreadFactory
 * @Description TODO
 * @Author Administrator
 * @Date 2024/7/10 19:14
 * @Version 1.0
 */
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {

    public static final MyUncaughtExceptionHandler MY_UNCAUGHT_EXCEPTION_HANDLER = new MyUncaughtExceptionHandler();

    @Autowired
    private ThreadFactory original;

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = original.newThread(r); //执行spring线程自己的创建逻辑
        thread.setUncaughtExceptionHandler(MY_UNCAUGHT_EXCEPTION_HANDLER); //额外装饰我们想要的创建逻辑
        return thread;
    }
}
