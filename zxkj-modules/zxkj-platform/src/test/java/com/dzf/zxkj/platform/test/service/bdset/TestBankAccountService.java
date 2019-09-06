package com.dzf.zxkj.platform.test.service.bdset;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.platform.ZxkjPlatformApplication;
import com.dzf.zxkj.platform.model.bdset.BankAccountVO;
import com.dzf.zxkj.platform.services.bdset.IBankAccountService;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @Auther: dandelion
 * @Date: 2019-09-06
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZxkjPlatformApplication.class)
@Slf4j
public class TestBankAccountService {

    private static final String pk_corp = "003Vgz";

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private IBankAccountService bankAccountService;

    @Before
    public void before(){
        log.info("******************************测试银行账户开始********************************");
    }

    @After
    public void after(){
        log.info("******************************测试银行账户结束********************************");
    }

    @Test
    public void TestQueryById(){
        BankAccountVO bankAccountVO = bankAccountService.queryById("003Vgz00000001gUs9h70002");
        Assert.assertNotNull(bankAccountVO);
    }

    @Test
    public void TestSave(){
        BankAccountVO bankAccountVO = new BankAccountVO();
        bankAccountVO.setPk_corp(pk_corp);
        bankAccountVO.setCoperatorid("002MPP00000001ZPu9oq000H");
        bankAccountVO.setBankcode("1234");
        bankAccountVO.setBankname("12345");
        bankAccountVO.setRelatedsubj("003Vgz00000001g8zEYs0002");
        bankAccountService.save(bankAccountVO);
    }

}
