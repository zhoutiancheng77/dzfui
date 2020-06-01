package com.dzf.zxkj.app.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.ObjectInputStream;

@Configuration
@Getter
@Slf4j
public class SecretKeyConfig {

    private String pubKey;
    private String preKey;
    private String defaultKey;

    public SecretKeyConfig() throws RuntimeException, IOException, ClassNotFoundException {
        log.info("秘钥初始化。。。。。。");
        ClassPathResource resource = new ClassPathResource("param.txt");
        ObjectInputStream ois = new ObjectInputStream(resource.getInputStream());
        pubKey = (String) ois.readObject();
        preKey = (String) ois.readObject();
        defaultKey = (String) ois.readObject();
        log.info("秘钥初始化成功！");
    }
}
