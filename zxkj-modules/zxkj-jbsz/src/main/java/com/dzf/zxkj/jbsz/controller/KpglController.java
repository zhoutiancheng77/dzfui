package com.dzf.zxkj.jbsz.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.jbsz.vo.AssetcardVO;
import com.dzf.zxkj.jbsz.vo.BankAccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
@RestController
@RequestMapping("kpgl")
public class KpglController {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @GetMapping("list")
    public ReturnData<List<AssetcardVO>> list(@RequestParam("pk_corp") String pk_corp) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        List<AssetcardVO> listVo = (List<AssetcardVO>) singleObjectBO
                .retrieveByClause(AssetcardVO.class,
                        " nvl(dr,0) = 0 and pk_corp=?", sp);
        return ReturnData.ok().data(listVo);
    }

}
