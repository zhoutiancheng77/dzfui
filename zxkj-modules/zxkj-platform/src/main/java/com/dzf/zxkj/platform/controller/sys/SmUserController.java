package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Page;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.PinyinUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("sm_user")
@Slf4j
public class SmUserController {

    @Autowired
    private IUserService userService;

    @PostMapping("gsSelect")
    public ReturnData<Grid> gsSelect(@MultiRequestBody UserVO userVO, @MultiRequestBody String pk_corp, @MultiRequestBody DZFDate loginDate) {
        Grid json = new Grid();
        if (userVO.getLocked_tag() != null && userVO.getLocked_tag().booleanValue()) {
            json.setMsg("当前用户被锁定，请联系管理员!");
        } else {

        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("gsQuery")
    public ReturnData<Grid> gsQuery(@MultiRequestBody UserVO userVO, Page page) {
        Grid json = new Grid();
        if (userVO.getLocked_tag() != null && userVO.getLocked_tag().booleanValue()) {
            json.setMsg("当前用户被锁定，请联系管理员!");
            json.setSuccess(false);
        } else {
            List<CorpVO> list = userService.queryPowerCorpKj(userVO.getPrimaryKey());
            if (list != null && list.size() > 0) {
                String pyfirstcomb = null;
                Map<String, String> syMap = userService.queryCorpSyByUser(userVO.getCuserid(), list);
                String currentYear = new DZFDate().toString().substring(0, 4);
                for (CorpVO corpVO : list) {
                    try {
                        corpVO.setUnitname(CodeUtils1.deCode(corpVO.getUnitname()));

                        pyfirstcomb = PinyinUtil.getFirstSpell(corpVO.getUnitname())
                                + PinyinUtil.getPinYin(corpVO.getUnitname());
                        corpVO.setPyfirstcomb(pyfirstcomb);//客户名称拼音首字母

                        String syPeriod = syMap
                                .get(corpVO.getPk_corp());
                        String accountProgressDate = corpVO.getBegindate().toString();
                        if (syPeriod != null
                                && syPeriod.compareTo(accountProgressDate.substring(0, 7)) >= 0) {
                            // 损益在建账日期后取损益日期下个月日期
                            accountProgressDate = new DZFDate(DateUtils.getNextMonth(
                                    new DZFDate(syPeriod + "-01").getMillis()))
                                    .toString();
                        }
                        corpVO.setAccountProgressDate(accountProgressDate);
                        corpVO.setAccountProgress((currentYear
                                .equals(accountProgressDate.substring(0, 4)) ? ""
                                : accountProgressDate.substring(0, 4) + "年")
                                + accountProgressDate.substring(5, 7) + "月");
                    } catch (Exception e) {
                        log.error("错误", e);
                    }
                }
            }
            json.setSuccess(true);
            json.setRows(list);
            json.setTotal(list == null ? 0L : list.size());
            json.setMsg("登录成功!");
        }
        return ReturnData.ok().data(json);
    }

}
