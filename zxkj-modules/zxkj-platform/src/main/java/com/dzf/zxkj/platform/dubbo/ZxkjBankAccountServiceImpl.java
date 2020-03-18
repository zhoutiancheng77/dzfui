package com.dzf.zxkj.platform.dubbo;

import com.dzf.zxkj.bankaccount.model.BankAccountVO;
import com.dzf.zxkj.bankaccount.model.ReturnData;
import com.dzf.zxkj.bankaccount.model.SignOnlineVO;
import com.dzf.zxkj.bankaccount.service.IBankAccountService;
import com.dzf.zxkj.bankaccount.system.BankAccountConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@org.apache.dubbo.config.annotation.Service(version = BankAccountConstant.version, timeout = Integer.MAX_VALUE)
public class ZxkjBankAccountServiceImpl implements IBankAccountService {

    @Override
    public ReturnData saveBankAccount(BankAccountVO bankAccountVO) {

        return null;
    }

    @Override
    public ReturnData saveSignOnline(SignOnlineVO signOnlineVO) {
        return null;
    }
}
