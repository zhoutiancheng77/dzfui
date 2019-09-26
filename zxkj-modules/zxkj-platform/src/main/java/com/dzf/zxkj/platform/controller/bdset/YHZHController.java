package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.platform.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.BankAccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.bdset.IYHZHService;
import com.dzf.zxkj.platform.services.report.IYntBoPubUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bdset/gl_yhzhact")
public class YHZHController {
    @Autowired
    private IYHZHService gl_yhzhserv;
    @Autowired
    private IYntBoPubUtil yntBoPubUtil;

    @GetMapping("/query")
    public ReturnData<List<BankAccountVO>> query(String pk_corp, String isnhsty, @MultiRequestBody CorpVO corpVO, @MultiRequestBody String isimp){
        List<BankAccountVO> list = gl_yhzhserv.query(pk_corp, isnhsty);
        return ReturnData.ok().data(list);
    }
}
