package com.dzf.zxkj.platform.auth.runner;

import com.dzf.zxkj.platform.auth.config.RsaKeyConfig;
import com.dzf.zxkj.platform.auth.utils.RsaKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RsaKeyRunner implements CommandLineRunner {

    @Autowired
    private RsaKeyConfig rsaKeyConfig;

    @Override
    public void run(String... args) throws Exception {
        Map<String, byte[]> keyMap = RsaKeyUtil.getInstance().generateKey(rsaKeyConfig.getUserSecret());
        rsaKeyConfig.setUserPriKey(keyMap.get("pri"));
        rsaKeyConfig.setUserPubKey(keyMap.get("pub"));
    }
}
