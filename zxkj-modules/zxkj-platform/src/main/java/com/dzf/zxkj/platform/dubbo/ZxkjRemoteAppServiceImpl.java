package com.dzf.zxkj.platform.dubbo;

import com.dzf.cloud.redis.lock.RedissonDistributedLock;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.image.DcModelBVO;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.common.IReferenceCheck;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.*;
import com.dzf.zxkj.platform.service.zncs.IBankStatementService;
import com.dzf.zxkj.platform.util.zncs.CommonXml;
import com.dzf.zxkj.platform.util.zncs.FpMsgClient;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@org.apache.dubbo.config.annotation.Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class ZxkjRemoteAppServiceImpl implements IZxkjRemoteAppService {
    @Autowired
    private ICorpService corpService;
    @Autowired
    private YntBoPubUtil yntBoPubUtil;
    @Autowired
    private ICpaccountService iCpaccountService;
    @Autowired
    private ICpaccountCodeRuleService iCpaccountCodeRuleService;
    @Autowired
    private IAuxiliaryAccountService gl_fzhsserv;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private IParameterSetService sys_parameteract;
    @Autowired
    private IImageGroupService  gl_pzimageserv;
    @Autowired
    private IQmgzService gzservice;
    @Autowired
    private IDcpzService dcpzjmbserv;
    @Autowired
    private IJtsjTemService sys_jtsjtemserv;
    @Autowired
    private IBankStatementService gl_yhdzdserv;
    @Autowired
    private IMsgService sys_msgtzserv;
    @Autowired
    private RedissonDistributedLock redissonDistributedLock;
	@Autowired
	private IUserService userServiceImpl;
    @Autowired
    private IReferenceCheck refchecksrv;



    @Override
    public CorpVO queryByPk(String pk_corp) {
        try {
            return corpService.queryByPk(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryByPk异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public Integer getAccountSchema(String pk_corp){
        try {
            return yntBoPubUtil.getAccountSchema(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getAccountSchema异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public String getCNYPk(){
        try {
            return yntBoPubUtil.getCNYPk();
        } catch (DZFWarpException e) {
            log.error(String.format("调用getCNYPk异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public String queryAccountRule(String pk_corp){
        try {
            return iCpaccountService.queryAccountRule(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryAccountRule异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public String getNewRuleCode(String oldCode, String oldrule, String newrule){
        try {
            return iCpaccountCodeRuleService.getNewRuleCode(oldCode,oldrule,newrule);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getNewRuleCode异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public AuxiliaryAccountBVO[] queryAllBByLb(String pk_corp, String fzlb){
        try {
            return gl_fzhsserv.queryAllBByLb(pk_corp,fzlb);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryAllBByLb异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public Map<String, YntCpaccountVO> queryMapByPk(String pk_corp){
        try {
            return accountService.queryMapByPk(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryMapByPk异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public YntCpaccountVO[] queryYCVoByPk(String pk_corp){
        try {
            return accountService.queryByPk(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryYCVoByPk异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public String queryParamterValueByCode(String pk_corp, String paramcode){
        try {
            return sys_parameteract.queryParamterValueByCode(pk_corp,paramcode);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryParamterValueByCode异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public void isQjSyJz(String pk_corp, String cvoucherdate){
        try {
            gl_pzimageserv.isQjSyJz(pk_corp,cvoucherdate);
        } catch (DZFWarpException e) {
            log.error(String.format("调用isQjSyJz异常,异常信息:%s", e.getMessage()), e);
        }
    }
    @Override
    public long getNowMaxImageGroupCode(String pk_corp){
        try {
            return gl_pzimageserv.getNowMaxImageGroupCode(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getNowMaxImageGroupCode异常,异常信息:%s", e.getMessage()), e);
            return 0;
        }
    }
    @Override
    public boolean isGz(String pk_corp, String startqj){
        try {
            return gzservice.isGz(pk_corp,startqj);
        } catch (DZFWarpException e) {
            log.error(String.format("调用isGz异常,异常信息:%s", e.getMessage()), e);
            return false;
        }
    }
    @Override
    public List<DcModelHVO> queryDcModelHVO(String pk_corp){
        try {
            return dcpzjmbserv.query(pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryDcModelHVO异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public List<DcModelBVO> queryByPId(String pid, String pk_corp){
        try {
            return dcpzjmbserv.queryByPId(pid,pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryByPId异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public String getNewVoucherNo(String pk_corp, DZFDate doperatedate){
        try {
            return yntBoPubUtil.getNewVoucherNo(pk_corp,doperatedate);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getNewVoucherNo异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public String getCpidFromTd(String id, String loginpk, String loginuser){
        try {
            return sys_jtsjtemserv.getCpidFromTd(id,loginpk,loginuser);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getCpidFromTd异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public void checkCreatePZ(String pk_corp, TzpzHVO hvo){
        try {
            gl_yhdzdserv.checkCreatePZ(pk_corp,hvo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用checkCreatePZ异常,异常信息:%s", e.getMessage()), e);

        }
    }
    @Override
    public void saveImageRecord(String pk_image_group,
                                String pk_source_id, String[] currs,String[] nextid ,String currope,
                                String pk_corp,String pk_temp_corp,String vmemo){
        try {
            sys_msgtzserv.saveImageRecord(pk_image_group,pk_source_id,currs,nextid,currope,pk_corp,pk_temp_corp,vmemo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用checkCreatePZ异常,异常信息:%s", e.getMessage()), e);

        }
    }
    @Override
    public boolean tryGetDistributedFairLock(String lock){
        try {
             return redissonDistributedLock.tryGetDistributedFairLock(lock);
        } catch (DZFWarpException e) {
            log.error(String.format("调用tryGetDistributedFairLock异常,异常信息:%s", e.getMessage()), e);
            return false;
        }
    }
    @Override
    public void releaseDistributedFairLock(String lock){
        try {
            redissonDistributedLock.releaseDistributedFairLock(lock);
        } catch (DZFWarpException e) {
            log.error(String.format("调用releaseDistributedFairLock异常,异常信息:%s", e.getMessage()), e);

        }
    }
    @Override
    public UserVO queryUserJmVOByID(String userid){
        try {
            return userServiceImpl.queryUserJmVOByID(userid);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryUserJmVOByID异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public boolean isReferenced(final String tableName, String key){
        try {
            return refchecksrv.isReferenced(tableName,key);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryUserJmVOByID异常,异常信息:%s", e.getMessage()), e);
            return false;
        }
    }
    @Override
    public void  deleteMsg(ImageGroupVO grouvo) {
        try {
            sys_msgtzserv.deleteMsg(grouvo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用deleteMsg异常,异常信息:%s", e.getMessage()), e);

        }
    }

    @Override
    public String sendPostXml(String drcode) {
        FpMsgClient fpclient = new FpMsgClient();
        String value = fpclient.sendPostXml(drcode);
        return value;
    }

    @Override
    public Element getContentElement(String zip, String encry, String content) {

        return CommonXml.getContentElement(zip,encry,content);
    }
}
