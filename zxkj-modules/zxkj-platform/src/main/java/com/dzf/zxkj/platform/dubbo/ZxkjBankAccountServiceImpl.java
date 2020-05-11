package com.dzf.zxkj.platform.dubbo;

import com.dzf.zxkj.bankaccount.model.BankAccountModel;
import com.dzf.zxkj.bankaccount.model.ReturnData;
import com.dzf.zxkj.bankaccount.model.SignOnlineModel;
import com.dzf.zxkj.bankaccount.service.IBankAccountService;
import com.dzf.zxkj.bankaccount.system.BankAccountConstant;
import com.dzf.zxkj.platform.model.bdset.BankAccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.bdset.IYHZHService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@org.apache.dubbo.config.annotation.Service(version = BankAccountConstant.version, timeout = Integer.MAX_VALUE)
public class ZxkjBankAccountServiceImpl implements IBankAccountService {


    @Autowired
    private IYHZHService gl_yhzhserv;

    @Autowired
    private IYntBoPubUtil yntBoPubUtil;

    @Autowired
    private ICpaccountService cpaccountService;


    private ReturnData saveBankAccount(BankAccountModel bankAccountVO) {

        BankAccountVO[] bankAccountVOList = gl_yhzhserv.queryByCode(bankAccountVO.getBankcode(), bankAccountVO.getPk_corp());
        //判断是否存在
        boolean isExists = bankAccountVOList != null && Arrays.stream(bankAccountVOList).anyMatch(v -> StringUtils.isNotBlank(v.getBanktype()));

        if (isExists) {
            ReturnData returnData = new ReturnData();
            returnData.setCode("500");
            returnData.setMessage("已存在相同银行账户");
            return returnData;
        }

        BankAccountVO bankAccount = new com.dzf.zxkj.platform.model.bdset.BankAccountVO();
        bankAccount.setBankname(bankAccountVO.getBankType());
        bankAccount.setBankaccount(bankAccountVO.getBankcode());
        bankAccount.setLy("1"); //签约生成
        bankAccount.setBankTypeCode("1");
        bankAccount.setCoperatorid(bankAccountVO.getUserid());
        bankAccount.setPk_corp(bankAccountVO.getPk_corp());

        String invcode = yntBoPubUtil.getYhzhCode(bankAccountVO.getPk_corp());


        YntCpaccountVO[] yntCpaccountVOS = cpaccountService.queryCpAccountVOs(bankAccount.getPk_corp(), "1002");

        bankAccount.setAccountcode(yntCpaccountVOS[0].getAccountcode());
        bankAccount.setRelatedsubj(yntCpaccountVOS[0].getPk_corp_account());
        bankAccount.setAccountname(yntCpaccountVOS[0].getAccountname());

        bankAccount.setCode(invcode);

        ReturnData returnData = new ReturnData();

        try {
            gl_yhzhserv.save(bankAccount);
            returnData.setCode("200");
        } catch (Exception e) {
            returnData.setCode("500");
            returnData.setMessage(e.getMessage());
            log.info("同步数据失败", e);
        }
        return returnData;
    }

    public ReturnData saveSignOnline(SignOnlineModel signOnlineVO) {


        BankAccountVO bankAccountVO = gl_yhzhserv.queryById(signOnlineVO.getPk_bankAccount());

        ReturnData returnData = new ReturnData();
        returnData.setCode("500");
        returnData.setMessage("银行账户不存在");

        if (bankAccountVO != null) {
            bankAccountVO.setVapplycode(signOnlineVO.getVapplycode());
            bankAccountVO.setIstatus(signOnlineVO.getIstatus().toString());
            try {
                gl_yhzhserv.update(bankAccountVO, new String[]{"vapplycode", "istatus"});
                returnData.setCode("200");
                returnData.setMessage("签约信息更新成功");
            } catch (Exception e) {
                returnData.setCode("500");
                returnData.setMessage(e.getMessage());
            }
        }

        return returnData;
    }

    @Override
    public List<ReturnData> batchSaveBankAccount(List<BankAccountModel> bankAccountVOList) {
        return bankAccountVOList.stream().map(v -> {
            return saveBankAccount(v);
        }).collect(Collectors.toList());
    }

    @Override
    public List<ReturnData> batchSaveSignOnline(List<SignOnlineModel> signOnlineVOList) {
        return signOnlineVOList.stream().map(v -> {
            return saveSignOnline(v);
        }).collect(Collectors.toList());
    }
}
