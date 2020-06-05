package com.dzf.zxkj.platform.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Getter
@Slf4j
public class SecretKeyConfig {

//    @Autowired
//    private CorpSecretKeyConfig corpSecretKeyConfig;

    private String pubKey;
    private String preKey;
    private String defaultKey;

    public SecretKeyConfig() throws RuntimeException, IOException, ClassNotFoundException {
//        log.info("秘钥初始化。。。。。。");
//        ClassPathResource resource = new ClassPathResource("param.txt");
//        ObjectInputStream ois = new ObjectInputStream(resource.getInputStream());
//
//        pubKey = corpSecretKeyConfig.getPubKey();
//        preKey = corpSecretKeyConfig.getPreKey();
//        defaultKey = corpSecretKeyConfig.getDefaultKey();
//        log.info("****************************************************");
//        log.info(pubKey);
//        log.info(preKey);
//        log.info(defaultKey);
//        log.info("****************************************************");
//        log.info("秘钥初始化成功！");
    }
}
