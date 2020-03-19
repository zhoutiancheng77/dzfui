package com.dzf.zxkj.platform.dubbo;

import com.dzf.zxkj.bankaccount.model.BankAccountVO;
import com.dzf.zxkj.bankaccount.model.ReturnData;
import com.dzf.zxkj.bankaccount.model.SignOnlineVO;
import com.dzf.zxkj.bankaccount.service.IBankAccountService;
import com.dzf.zxkj.bankaccount.system.BankAccountConstant;
import com.dzf.zxkj.platform.service.bdset.IYHZHService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@org.apache.dubbo.config.annotation.Service(version = BankAccountConstant.version, timeout = Integer.MAX_VALUE)
public class ZxkjBankAccountServiceImpl implements IBankAccountService {


    @Autowired
    private IYHZHService gl_yhzhserv;

    @Autowired
    private IYntBoPubUtil yntBoPubUtil;

    @Override
    public ReturnData saveBankAccount(BankAccountVO bankAccountVO) {
        com.dzf.zxkj.platform.model.bdset.BankAccountVO bankAccount = new com.dzf.zxkj.platform.model.bdset.BankAccountVO();
        bankAccount.setBanktype("中国工商银行");
        bankAccount.setBankaccount(bankAccountVO.getBankcode());
        bankAccount.setLy("0"); //签约生成
        bankAccount.setBankTypeCode("1");
        bankAccount.setZhlx("0");
        bankAccount.setCoperatorid(bankAccountVO.getVapplyuserid());
        bankAccount.setPk_corp(bankAccountVO.getInnercode());

        String invcode = yntBoPubUtil.getYhzhCode(bankAccountVO.getInnercode());

        bankAccount.setCode(invcode);

        ReturnData returnData = new ReturnData();

        try{
            gl_yhzhserv.save(bankAccount);
            returnData.setCode("200");
        }catch (Exception e){
            returnData.setCode("500");
            returnData.setMessage(e.getMessage());
        }
        return returnData;
    }

    @Override
    public ReturnData saveSignOnline(SignOnlineVO signOnlineVO) {
        return null;
    }
}
