package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.Page;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.LoginLogVo;
import com.dzf.zxkj.platform.model.sys.SysFunNodeVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.ISysFunnodeService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.PinyinUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("sm_user")
@Slf4j
public class SmUserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private ISysFunnodeService sysFunnodeService = null;

    @Autowired
    private ICorpService corpService;

    @GetMapping("gsSelect")
    public ReturnData<Grid> gsSelect(@MultiRequestBody UserVO userVO, @RequestParam("pk_corp") String pk_corp, @RequestParam("login_date") DZFDate loginDate) {
        Grid json = new Grid();
        if (userVO.getLocked_tag() != null && userVO.getLocked_tag().booleanValue()) {
            json.setMsg("当前用户被锁定，请联系管理员!");
        } else {
            CorpVO corpVo = corpService.queryByPk(pk_corp);
            Set<String> corps = userService.querypowercorpSet(userVO.getPrimaryKey());
            if (!corps.contains(corpVo.getPk_corp())) {
                json.setMsg("公司不存在!");
            } else {
                if (corpVo == null) {
                    json.setMsg("公司不存在!");
                } else if (corpVo.getBegindate() == null) {
                    json.setMsg("请初始化公司开账日期!");
                }/*else if(corpVo.getBegindate().compareTo(optDate) > 0){
							json.setMsg("登录日期不能小于开账日期!");
						}*/ else if (corpVo.getBegindate().compareTo(loginDate) > 0) {
                    json.setMsg("登录日期不能小于开账日期!");
                }/*else if(userVo.getDisable_time() != null && userVo.getDisable_time().compareTo(optDate) < 0){
							json.setMsg("登录日期不能大于用户失效日期!");
						}*/ else {
                    //List<SysFunNodeVO> lfunnode = sysFunnodeService.querySysnodeByUserAndCorp(userVo, corpVo, IGlobalConstants.DZF_KJ);
                    List<SysFunNodeVO> lfunnode = sysFunnodeService.querySysnodeByUser1(userVO, IGlobalConstants.DZF_KJ, corpVo.getPk_corp());

                    json.setSuccess(true);
                    LoginLogVo loginLogVo = getLoginVo(IGlobalConstants.DZF_KJ);
                    loginLogVo.setLoginstatus(1);
                    loginLogVo.setPk_corp(corpVo.getPk_corp());
                    loginLogVo.setPk_user(userVO.getCuserid());
                    loginLogVo.setMemo("选公司");
                    userService.loginLog(loginLogVo);
                    json.setMsg("选择公司!");
                    json.setRows(corpVo);

                }
            }
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("/switchCorp")
    public ReturnData switchCorp(String corpId, String date) {
        Json json = new Json();
        CorpVO corpVo = corpService.queryByPk(corpId);
        DZFDate loginDate = new DZFDate(date);
        if (corpVo.getBegindate() == null) {
            throw new BusinessException("请初始化公司开账日期!");
        } else if (corpVo.getBegindate().after(loginDate)) {
            throw new BusinessException("登录日期不能小于开账日期!");
        }
        json.setData(corpVo);
        json.setSuccess(true);
        json.setMsg("切换成功");
        return ReturnData.ok().data(json);
    }

    private LoginLogVo getLoginVo(String project) {
        LoginLogVo loginLogVo = new LoginLogVo();
        try {
            loginLogVo.setLogindate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            loginLogVo.setLoginstatus(0);
            loginLogVo.setProject_name(project);
        } catch (Exception e) {
            log.error("错误", e);
        }
        return loginLogVo;
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

    @GetMapping("/getDefaultLoginDate")
    public ReturnData getDefaultLoginDate(String corpId) {
        Json json = new Json();
        String date = corpService.getDefaultLoginDate(corpId);
        json.setData(date);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }
}
