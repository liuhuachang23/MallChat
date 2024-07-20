package com.lhc.mallchat.common;

import com.lhc.mallchat.common.common.utils.JwtUtils;
import com.lhc.mallchat.common.common.utils.RedisUtils;
import com.lhc.mallchat.common.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName DaoTest
 * @Description TODO
 * @Author Administrator
 * @Date 2024/6/17 19:03
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class DaoTest {

    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private JwtUtils jwtUtils;


    @Test
    public void jwt(){
        System.out.println(jwtUtils.createToken(1L));
    }

    @Autowired
    private LoginService loginService;
    @Test
    public void getValidUid(){
        String token = "";
        Long uid = loginService.getValidUid(token);
        System.out.println(uid);
    }

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Test
    public void thread(){
        threadPoolTaskExecutor.execute(()->{
            if (1==1){
                log.error("123");
                throw new RuntimeException("123");
            }
        });
    }


    @Autowired
    private RedissonClient redissonClient;
    @Test
    public void redisson(){
        RLock lock = redissonClient.getLock("123");
        lock.lock();
        System.out.println();
        lock.unlock();
    }

    @Test
    public void redis(){
        RedisUtils.set("name","卷心菜");
        String name = RedisUtils.getStr("name");
        System.out.println(name);
    }

    @Test
    public void test() throws WxErrorException {
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(1, 2000);
        String url = wxMpQrCodeTicket.getUrl();
        System.out.println(url);
    }
}
