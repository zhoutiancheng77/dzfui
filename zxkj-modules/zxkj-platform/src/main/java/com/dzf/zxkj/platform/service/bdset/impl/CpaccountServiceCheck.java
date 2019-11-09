package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.RemittanceVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.jzcl.IVoucherTemplate;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.ICorpService;

import java.util.List;

/**
 * 科目校验类----新增
 */
public class CpaccountServiceCheck extends CpaccountServiceBaseCheck {


    public CpaccountServiceCheck(SingleObjectBO singleObjectBO) {
        super(singleObjectBO);
    }

    /**
     * 新增校验入口
     */
    public void checkAdd(YntCpaccountVO vo) throws BusinessException {
        //1、常规性校验
        if (StringUtil.isEmpty(vo.getAccountcode()) || StringUtil.isEmpty(vo.getAccountname()))
            throw new BusinessException("科目编码或者名称不能为空！");
        if (vo.getAccountcode().length() != vo.getAccountcode().getBytes().length)
            throw new BusinessException("科目编码不能输入汉字！");
        YntCpaccountVO parentvo = getParentVOByID(vo.getPk_corp(), vo.getAccountcode());
        if (parentvo == null)
            throw new BusinessException("当前新增科目，上级科目不存在！");
        String accountcode = vo.getAccountcode();

        ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
        String accoutrule = gl_cpacckmserv.queryAccountRule(vo.getPk_corp());
        String parentcode = DZfcommonTools.getParentCode(accountcode, accoutrule);
        String scode = accountcode.substring(parentcode.length());
        //取当前科目末尾对应的最大值
        String[] resvalues = getMaxValues(scode);
        if (scode.equals(resvalues[2]))
            throw new BusinessException("子科目编码应在" + resvalues[0] + "-" + resvalues[1] + "之间！");
        if (parentvo.getAccountkind().intValue() != vo.getAccountkind().intValue())
            throw new BusinessException("子科目的科目类型必须和父科目一致！");
        if (parentvo.getDirection().intValue() != vo.getDirection().intValue())
            throw new BusinessException("子科目的科目方向必须和父科目一致！");
        if (vo.getPrimaryKey() != null && parentvo.getBisseal().booleanValue())
            throw new BusinessException("父科目已封存，不允许操作");
        //2、校验编码规则
        checkCodeRule(vo, accoutrule);
        //3、校验是否有新增权限
        checkAddAuth(vo.getPk_corp(), parentvo, accoutrule);
        //4、 校验科目编码是否已经存在
        if (isAccountCodeExist(vo.getAccountcode(), vo.getPk_corp()))
            throw new BusinessException("当前科目编码" + vo.getAccountcode() + "已经存在！");
        //5、 校验同级科目名称是否已经存在
        if (isAccountNameExist(vo.getAccountcode(), vo.getAccountname(), vo.getPk_corp()))
            throw new BusinessException("当前科目名称" + vo.getAccountname() + "已经存在！");
        //6、校验父级科目是否被期初引用
        //checkQC(vo.getPk_corp(),parentvo);
        //7、校验是否被凭证模板引用
        //checkpzmb(vo.getPk_corp(),parentvo);
        //8、校验是否被凭证引用
        //checkpz(vo.getPk_corp(),parentvo);
        //9、判断当前公司是否启用库存管理
        //	if(vo.getIsnum() != null && vo.getIsnum().booleanValue()){
        //		CorpVO corpvo = (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class, vo.getPk_corp());
//			if(corpvo == null || corpvo.getBbuildic() == null || !corpvo.getBbuildic().booleanValue()){
//				throw new BusinessException("当前公司没有启用进销存！");
//			}
//			if("1403".equals(parentvo.getAccountcode()) || "1405".equals(parentvo.getAccountcode())){//原材料、库存商品
//				throw new BusinessException("启用进销存，原材料和库存商品不允许增加下级科目！");
//			}
        //	}
//		//10、判断当前公司是否启用外币(先不用判断)
//		if(vo.getIswhhs() != null && vo.getIswhhs().booleanValue()){
//			CorpVO corpvo = (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class, vo.getPk_corp());
//			if(corpvo == null || corpvo.getIscurr() == null || !corpvo.getIscurr().booleanValue()){
//				throw new BusinessException("当前公司没有启用外币！");
//			}
//		}
    }

    /**
     * 新增科目前校验
     */
    public void preCheckAdd(String subjectId, CorpVO corpvo) throws BusinessException {

        YntCpaccountVO parentvo = queryYntvoByid(subjectId);

        //1、常规性校验
        if (parentvo == null)
            throw new BusinessException("当前新增科目，上级科目不存在！");
        String accoutrule = corpvo.getAccountcoderule();
        if (accoutrule == null) {
            accoutrule = DZFConstant.ACCOUNTCODERULE;
        }
        //3、校验是否有新增权限
        checkAddAuth(corpvo.getPk_corp(), parentvo, accoutrule);

    }

    private String[] getMaxValues(String scode) {
        String[] temps = new String[3];
        StringBuffer maxvalue = new StringBuffer();
        StringBuffer minvalue = new StringBuffer();
        for (int i = 0; i < scode.length(); i++) {
            maxvalue.append("9");
            if (i == scode.length() - 1) {
                minvalue.append("1");
            } else {
                minvalue.append("0");
            }
        }
        temps[0] = maxvalue.toString();
        temps[1] = minvalue.toString();
        temps[2] = minvalue.toString().replace("1", "0");
        return temps;

    }

    /**
     * 校验是否被引用
     */
    public boolean checkIsQuote(String corpId, String subjectCode) throws BusinessException {

        YntCpaccountVO parentvo = getParentVOByID(corpId, subjectCode);
        if (parentvo == null)
            throw new BusinessException("当前新增科目，上级科目不存在！");
        boolean ischd = false;
        if (parentvo != null) {
            ischd = isHasChilden(corpId, parentvo.getAccountcode());
        }
        if (!ischd) {
            boolean ret = checkIsQC(corpId, parentvo);
            if (ret) {
                return ret;
            }
            ret = checkIsPzmb(corpId, parentvo.getPk_corp_account());
            if (ret) {
                return ret;
            }
            ret = checkIsPz(corpId, parentvo.getPk_corp_account());
            if (ret) {
                return ret;
            }
            ret = checkIsGzb(corpId, parentvo.getPk_corp_account());
            if (ret) {
                return ret;
            }
            //  校验是否是新库存模式
            ICorpService corpService = SpringUtils.getBean(ICorpService.class);
            CorpVO corpvo = corpService.queryByPk(corpId);
            if (corpvo.getIbuildicstyle() != null && corpvo.getIbuildicstyle() == 1) {
                ret = checkIsIc(corpId, parentvo.getPk_corp_account());
                if (ret) {
                    return ret;
                }
            }
        }
        return false;
    }

    /**
     * 校验----是否被库存模块引用
     */
    private boolean checkIsIc(String pk_corp, String subjectId) throws BusinessException {

        SQLParameter sqlp1 = new SQLParameter();
        sqlp1.addParam(subjectId);
        sqlp1.addParam(pk_corp);
        //库存期初
        boolean ret = isExists(pk_corp,
                "select 1 from ynt_icbalance where  pk_subject= ? and pk_corp = ?  and nvl(dr,0)=0 ", sqlp1);
        if (ret) {
            return ret;
        }
        //存货
        ret = isExists(pk_corp, "select 1 from ynt_inventory where  pk_subject= ? and pk_corp = ?  and nvl(dr,0)=0 ",
                sqlp1);
        if (ret) {
            return ret;
        }
        //入库单
        ret = isExists(pk_corp, "select 1 from ynt_ictradein where  pk_subject= ? and pk_corp = ?  and nvl(dr,0)=0 ",
                sqlp1);
        if (ret) {
            return ret;
        }
        //出库单
        ret = isExists(pk_corp, "select 1 from ynt_ictradeout where  pk_subject= ? and pk_corp = ?  and nvl(dr,0)=0 ",
                sqlp1);
        if (ret) {
            return ret;
        }
        return false;
    }

    /**
     * 是否存在子科目
     *
     * @param pk_corp
     * @param accountCode
     * @return
     */
    public boolean isHasChilden(String pk_corp, String accountCode) {
        String where = " pk_corp=? and accountcode like '" + accountCode + "_%' and nvl(dr,0)=0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        List<YntCpaccountVO> rslist = (List<YntCpaccountVO>) singleObjectBO.retrieveByClause(YntCpaccountVO.class, where, sp);
        if (rslist != null && rslist.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 校验----父科目是否已经录入了期初余额
     */
    private boolean checkIsQC(String pk_corp, YntCpaccountVO parentVO) throws BusinessException {
        String where = " pk_corp=? and pk_accsubj=? and nvl(dr,0)=0 ";
        String where1 = " pk_corp=? and pk_accsubj=? and ((yearqc is not null and yearqc <> 0) or (yearjffse is not null and yearjffse <> 0) or (yeardffse is not null and yeardffse <> 0) or (thismonthqc is not null and thismonthqc <> 0)) and nvl(dr,0)=0";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(parentVO.getPrimaryKey());
        List<FzhsqcVO> rslist = (List<FzhsqcVO>) singleObjectBO.retrieveByClause(FzhsqcVO.class, where, sp);
        List<QcYeVO> relist1 = (List<QcYeVO>) singleObjectBO.retrieveByClause(QcYeVO.class, where1, sp);
        if (rslist != null && rslist.size() > 0) {
            //FzhsqcVO vo = rslist.get(0);
            //if(vo.getThismonthqc() != null && vo.getThismonthqc().compareTo(DZFDouble.ZERO_DBL) > 0){
            return true;
            //}
        }
        if (relist1 != null && relist1.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 校验----科目是否被凭证模板引用
     */
    private boolean checkIsPzmb(String pk_corp, String subjectId) throws BusinessException {
        //成本模板
        SQLParameter sqlp = new SQLParameter();
        sqlp.addParam(subjectId);
        sqlp.addParam(subjectId);
        sqlp.addParam(subjectId);
        boolean ret = isExists(pk_corp, "select 1 from ynt_cpcosttrans where"
                + "  (pk_fillaccount= ? or pk_creditaccount= ? or pk_debitaccount= ? ) and nvl(dr,0)=0 ", sqlp);
        if (ret) {
            return ret;
        }
        //常用模板
        SQLParameter sqlp1 = new SQLParameter();
        sqlp1.addParam(subjectId);
        ret = isExists(pk_corp, "select 1 from ynt_cppztemmb_b where  pk_accsubj= ? and nvl(dr,0)=0 ", sqlp1);
        if (ret) {
            return ret;
        }
        //折旧清理模板
        ret = isExists(pk_corp, "select 1 from ynt_cpmb_b where  pk_account= ? and nvl(dr,0)=0 ", sqlp1);
        if (ret) {
            return ret;
        }
        //汇兑损益模板
        SQLParameter sqlp2 = new SQLParameter();
        sqlp2.addParam(subjectId);
        sqlp2.addParam(subjectId);
        ret = isExists(pk_corp, "select 1 from ynt_remittance where  (pk_corp_account= ? or  pk_out_account= ?) and nvl(dr,0)=0 ", sqlp2);
        if (ret) {
            return ret;
        }
        //期间损益模板
        ret = isExists(pk_corp, "select 1 from ynt_cptransmb where  pk_transferinaccount= ? and nvl(dr,0)=0 ", sqlp1);
        if (ret) {
            return ret;
        }
        return ret;
    }

    /**
     * 校验----科目是否被凭证引用
     */
    public boolean checkIsPz(String pk_corp, String subjectId) throws BusinessException {
        SQLParameter sqlp = new SQLParameter();
        sqlp.addParam(pk_corp);
        sqlp.addParam(subjectId);
        boolean bpz = isExists(pk_corp, "select 1 from ynt_tzpz_b where pk_corp = ? and  pk_accsubj= ? and nvl(dr,0)=0 ", sqlp);
        if (bpz) {
            return bpz;
        }
        return false;
    }

    public boolean checkIsGzb(String pk_corp, String subjectId) throws BusinessException {
        SQLParameter sqlp = new SQLParameter();
        sqlp.addParam(pk_corp);
        sqlp.addParam(subjectId);
        boolean bpz = isExists(pk_corp, "select 1 from ynt_salarykmdept where pk_corp = ? and  ckjkmid= ? and nvl(dr,0)=0 ", sqlp);
        if (bpz) {
            return bpz;
        }
        return false;
    }


    /**
     * 校验----父科目是否已经录入了期初余额
     */
    private void checkQC(String pk_corp, YntCpaccountVO parentVO) throws BusinessException {
        String where = " pk_corp=? and pk_accsubj=? and nvl(dr,0)=0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(parentVO.getPrimaryKey());
        List<QcYeVO> rslist = (List<QcYeVO>) singleObjectBO.retrieveByClause(QcYeVO.class, where, sp);
        if (rslist != null && rslist.size() > 0) {
            //如果已经存在下级科目，则可以增加同级科目
            SQLParameter sqlp = new SQLParameter();
            sqlp.addParam((parentVO.getAccountlevel().intValue() + 1));
            sqlp.addParam(parentVO.getAccountcode() + "%");
            sqlp.addParam(pk_corp);
            String sql = " select 1 from ynt_cpaccount where accountlevel= ? " +
                    " and accountcode like ? and pk_corp= ? and nvl(dr,0)=0 ";
            boolean b = isExists(pk_corp, sql, sqlp);
            if (b == false) {
                if (rslist.get(0).getThismonthqc() != null && rslist.get(0).getThismonthqc().doubleValue() != 0) {
                    throw new BusinessException("上级科目【" + parentVO.getAccountcode() + "  " + parentVO.getAccountname() + "】已录入期初余额，不能再增加子科目，如需要，请删除上级科目期初余额！");
                } else if (rslist.get(0).getYeardffse() != null && rslist.get(0).getYeardffse().doubleValue() != 0) {
                    throw new BusinessException("上级科目【" + parentVO.getAccountcode() + "  " + parentVO.getAccountname() + "】已录入期初余额，不能再增加子科目，如需要，请删除上级科目期初余额！");
                } else if (rslist.get(0).getYearjffse() != null && rslist.get(0).getYearjffse().doubleValue() != 0) {
                    throw new BusinessException("上级科目【" + parentVO.getAccountcode() + "  " + parentVO.getAccountname() + "】已录入期初余额，不能再增加子科目，如需要，请删除上级科目期初余额！");
                } else if (rslist.get(0).getYearqc() != null && rslist.get(0).getYearqc().doubleValue() != 0) {
                    throw new BusinessException("上级科目【" + parentVO.getAccountcode() + "  " + parentVO.getAccountname() + "】已录入期初余额，不能再增加子科目，如需要，请删除上级科目期初余额！");
                }
            }
        }
    }

    /**
     * 校验----科目是否被凭证模板引用
     */
    private void checkpzmb(String pk_corp, YntCpaccountVO parentVO) throws BusinessException {
        //成本模板
        SQLParameter sqlp = new SQLParameter();
        sqlp.addParam(parentVO.getPrimaryKey());
        sqlp.addParam(parentVO.getPrimaryKey());
        sqlp.addParam(parentVO.getPrimaryKey());
        boolean bmb1 = isExists(pk_corp, "select 1 from ynt_cpcosttrans where"
                + "  (pk_fillaccount= ? or pk_creditaccount= ? or pk_debitaccount= ? ) and nvl(dr,0)=0 ", sqlp);
        if (bmb1) {
            throw new BusinessException("上级科目【" + parentVO.getAccountcode() + "  " + parentVO.getAccountname() + "】已被成本模板引用，不能新增下级科目!");
        }
        //常用模板
        SQLParameter sqlp1 = new SQLParameter();
        sqlp1.addParam(parentVO.getPrimaryKey());
        boolean bmb2 = isExists(pk_corp, "select 1 from ynt_cppztemmb_b where  pk_accsubj= ? and nvl(dr,0)=0 ", sqlp1);
        if (bmb2) {
            throw new BusinessException("上级科目【" + parentVO.getAccountcode() + "  " + parentVO.getAccountname() + "】已被凭证常用模板引用，不能新增下级科目!");
        }
        //折旧清理模板
        boolean bmb5 = isExists(pk_corp, "select 1 from ynt_cpmb_b where  pk_account= ?  and nvl(dr,0)=0 ", sqlp1);
        if (bmb5) {
            throw new BusinessException("上级科目【" + parentVO.getAccountcode() + "  " + parentVO.getAccountname() + "】已被折旧清理模板引用，不能新增下级科目!");
        }
        //汇兑损益模板
        SQLParameter sqlp2 = new SQLParameter();
        sqlp2.addParam(parentVO.getPrimaryKey());
        sqlp2.addParam(parentVO.getPrimaryKey());
        boolean bmb6 = isExists(pk_corp, "select 1 from ynt_remittance where  (pk_corp_account= ? or  pk_out_account= ?) and nvl(dr,0)=0 ", sqlp2);
        if (bmb6) {
            throw new BusinessException("上级科目【" + parentVO.getAccountcode() + "  " + parentVO.getAccountname() + "】已被汇兑损益模板引用，不能新增下级科目!");
        }
        //期间损益模板
        boolean bmb7 = isExists(pk_corp, "select 1 from ynt_cptransmb where  pk_transferinaccount= ? and nvl(dr,0)=0 ", sqlp1);
        if (bmb7) {
            throw new BusinessException("上级科目【" + parentVO.getAccountcode() + "  " + parentVO.getAccountname() + "】已被期间损益模板引用，不能新增下级科目!");
        }
    }

    /**
     * 校验----科目是否被凭证引用
     */
    private void checkpz(String pk_corp, YntCpaccountVO parentVO) throws BusinessException {
        SQLParameter sqlp = new SQLParameter();
        sqlp.addParam(pk_corp);
        sqlp.addParam(parentVO.getPrimaryKey());
        boolean bpz = isExists(pk_corp, "select 1 from ynt_tzpz_b where pk_corp = ? and  pk_accsubj= ? and nvl(dr,0)=0 ", sqlp);
        if (bpz) {
            throw new BusinessException("上级科目【" + parentVO.getAccountcode() + "  " + parentVO.getAccountname() + "】已被凭证引用，不能新增下级科目!");
        }
    }

    /**
     * 校验----是否有新增权限
     */
    private void checkAddAuth(String pk_corp, YntCpaccountVO parentVO, String accoutrule) throws BusinessException {
        if ("00000100AA10000000000BMD".equals(parentVO.getPk_corp_accountschema())
                || "00000100AA10000000000BMF".equals(parentVO.getPk_corp_accountschema())
                || "00000100000000Ig4yfE0005".equals(parentVO.getPk_corp_accountschema())) {
            //不允许增加二级[损益类科目（除税金及附加）、固定资产、累计折旧、无形资产、累计摊销、长期待摊费用、应付职工薪酬、生产成本、利润分配科目下不允许增加二级]
            checkTwoLevel(parentVO);
            //不允许增加三级[应交税费-应交增值税、管理费用、销售费用下的职工薪酬下不允许增加三级科目]
            checkThreeLevel(parentVO, accoutrule);
        }
        if ("00000100AA10000000000BMQ".equals(parentVO.getPk_corp_accountschema())) {
            /**
             * 	-民非制度收入类科目限制不能增加二级科目
             * 	add by gzx
             */
            checkTwoLevelForMF(parentVO);
        }

        //模板校验(有模板录入的则对应的科目不允许新增下级)
        IVoucherTemplate vouchertempser = (IVoucherTemplate) SpringUtils.getBean("vouchertempser");
        //汇兑模板
        RemittanceVO[] templatevos = (RemittanceVO[]) vouchertempser.queryTempateByName(RemittanceVO.class.getName(), pk_corp);
        if (templatevos != null && templatevos.length > 0) {
            for (RemittanceVO tvo : templatevos) {
                if (parentVO.getAccountcode().equals(tvo.getAccountcode())
                        || parentVO.getAccountcode().equals(tvo.getOutatcode())) {
                    throw new BusinessException("汇兑科目，不允许增加下级");
                }
            }
        }
    }

    private void checkTwoLevelForMF(YntCpaccountVO parentVO) throws BusinessException {
        if (parentVO.getAccountlevel() == 1 &&
                ("捐赠收入".equals(parentVO.getAccountname().trim())
                        || "会费收入".equals(parentVO.getAccountname().trim())
                        || "提供服务收入".equals(parentVO.getAccountname().trim())
                        || "政府补助收入".equals(parentVO.getAccountname().trim())
                        || "商品销售收入".equals(parentVO.getAccountname().trim())
                        || "投资收益".equals(parentVO.getAccountname().trim())
                        || "其他收入".equals(parentVO.getAccountname().trim())
                )) {
            throw new BusinessException("【捐赠收入、会费收入、提供服务收入、政府补助收入、商品销售收入、投资收益、其他收入】科目，不允许增加二级科目！");
        }
    }

    private void checkTwoLevel(YntCpaccountVO parentVO) throws BusinessException {
        if (parentVO == null)
            return;
        ICorpService corpService = SpringUtils.getBean(ICorpService.class);
        CorpVO corp = corpService.queryByPk(parentVO.getPk_corp());
        corp = corpService.queryByPk(corp.getFathercorp());//加盟商，取上级公司
        if (corp.getIschannel() != null && corp.getIschannel().booleanValue()) {
            // 加盟商增加二级科目控制
            if (parentVO.getAccountlevel() == 1
                    && parentVO.getAccountname()
                    .matches("应收账款|应付账款|预收账款|预付账款|其他应收款|其他应付款|固定资产|无形资产|累计折旧|累计折旧摊销|长期待摊费用|应付职工薪酬|生产成本|原材料|库存商品|商品销售收入")) {
                throw new BusinessException("加盟商客户，" + parentVO.getAccountname() + "科目不允许增加二级科目");
            }
        }

        IYntBoPubUtil bopub = (IYntBoPubUtil) SpringUtils.getBean("yntBoPubUtil");
        //民间的判断
        if (bopub.getAccountSchema(parentVO.getPk_corp()).intValue() == DzfUtil.POPULARSCHEMA.intValue() || bopub.getAccountSchema(parentVO.getPk_corp()).intValue() == DzfUtil.CAUSESCHEMA) {
            if (bopub.getAccountSchema(parentVO.getPk_corp()).intValue() != DzfUtil.CAUSESCHEMA) {
                if (parentVO.getAccountkind() == 5 && parentVO.getAccountlevel() == 1
                        && (!"业务活动成本".equals(parentVO.getAccountname().trim())
                        && !"管理费用".equals(parentVO.getAccountname().trim()) && !"筹资费用".equals(parentVO.getAccountname().trim())
                        && !"其他费用".equals(parentVO.getAccountname().trim())
                )) {//损益、一级、非[税金及附加]
                    throw new BusinessException("损益类科目（【业务活动成本】,【管理费用】,【筹资费用】,【其他费用】除外），不允许增加二级科目 ！");
                }
            }
            if (parentVO.getAccountlevel() == 1 &&
                    ("固定资产".equals(parentVO.getAccountname().trim())
                            || "累计折旧".equals(parentVO.getAccountname().trim())
                            || "无形资产".equals(parentVO.getAccountname().trim())
                            || "累计摊销".equals(parentVO.getAccountname().trim()))) {
                if (bopub.getAccountSchema(parentVO.getPk_corp()).intValue() == DzfUtil.CAUSESCHEMA) {//事业单位
                    throw new BusinessException("【固定资产、累计折旧、无形资产、累计摊销】科目，不允许增加二级科目！");
                } else if (bopub.getAccountSchema(parentVO.getPk_corp()).intValue() == DzfUtil.POPULARSCHEMA) {//民间
                    throw new BusinessException("【固定资产、累计折旧、无形资产】科目，不允许增加二级科目！");
                }

            }
        } else {
            String taxAddName = "00000100000000Ig4yfE0005".equals(parentVO.getPk_corp_accountschema())
                    ? "主营业务税金及附加" : "税金及附加";
            if (parentVO.getAccountkind() == 5 && parentVO.getAccountlevel() == 1
                    && !taxAddName.equals(parentVO.getAccountname().trim())) {//损益、一级、非[税金及附加]
                throw new BusinessException("损益类科目（【" + taxAddName + "】除外），不允许增加二级科目 ！");
            }
            if (parentVO.getAccountlevel() == 1 &&
                    ("固定资产".equals(parentVO.getAccountname().trim())
                            || "累计折旧".equals(parentVO.getAccountname().trim())
                            || "无形资产".equals(parentVO.getAccountname().trim())
                            || "累计摊销".equals(parentVO.getAccountname().trim())
                            || "长期待摊费用".equals(parentVO.getAccountname().trim())
                            || "应付职工薪酬".equals(parentVO.getAccountname().trim())
                            || "生产成本".equals(parentVO.getAccountname().trim())
                            //|| "利润分配".equals(parentVO.getAccountname().trim())
                    )) {
                throw new BusinessException("【固定资产、累计折旧、无形资产、累计摊销、长期待摊费用、应付职工薪酬、生产成本】科目，不允许增加二级科目！");
            }
        }
    }

    private void checkThreeLevel(YntCpaccountVO parentVO, String accoutrule) throws BusinessException {
        if (parentVO == null)
            return;

        String[] oldcodes = new String[]{"222101", "660201", "660101", "560201", "560101"};
        ICpaccountCodeRuleService gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils.getBean("gl_accountcoderule");
        String[] newcodes = gl_accountcoderule.getNewCodes(oldcodes, DZFConstant.ACCOUNTCODERULE, accoutrule);
        if (parentVO.getAccountlevel() == 2 &&
                (("应交增值税".equals(parentVO.getAccountname().trim()) && newcodes[0].equals(parentVO.getAccountcode().trim()))
                        || ("职工薪酬".equals(parentVO.getAccountname().trim()) && newcodes[1].equals(parentVO.getAccountcode().trim()))
                        || ("职工薪酬".equals(parentVO.getAccountname().trim()) && newcodes[2].equals(parentVO.getAccountcode().trim()))
                        || ("职工薪酬".equals(parentVO.getAccountname().trim()) && newcodes[3].equals(parentVO.getAccountcode().trim()))
                        || ("职工薪酬".equals(parentVO.getAccountname().trim()) && newcodes[4].equals(parentVO.getAccountcode().trim()))
                )) {
            throw new BusinessException("应交税费-应交增值税、管理费用及销售费用下的职工薪酬不允许增加三级科目!");
        }
    }

    public boolean checkBeginDataRef(String corpId, String subjectId) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(subjectId);
        sp.addParam(corpId);
        // 辅助核算期初
        String sql1 = " select 1 from ynt_fzhsqc where pk_accsubj = ? and pk_corp = ? and thismonthqc is not null and thismonthqc <> 0 and nvl(dr,0)=0 ";
        boolean exist1 = singleObjectBO.isExists(corpId, sql1, sp);
        // 科目期初
        String sql2 = " select 1 from ynt_qcye where pk_accsubj = ? and pk_corp = ? and thismonthqc is not null and thismonthqc <> 0 and nvl(dr,0)=0 ";
        boolean exist2 = singleObjectBO.isExists(corpId, sql2, sp);

        if (exist1 || exist2) {
            return true;
        }
        return false;
    }

    public boolean checkParentVerification(String corpId, String subjectCode) {
        boolean isVerify = false;
        if (subjectCode.length() == 4)
            return isVerify;
        SQLParameter sp = new SQLParameter();
        sp.addParam(corpId);
        sp.addParam(subjectCode.substring(0, 4) + "%");
        String sql = " select accountcode from ynt_cpaccount" +
                " where pk_corp = ? and accountcode like ? and nvl(dr,0)=0 and isverification = 'Y' ";
        List<String> rs = (List<String>) singleObjectBO.executeQuery(sql, sp, new ColumnListProcessor());
        for (String code : rs) {
            if (code.length() < subjectCode.length()) {
                isVerify = true;
                break;
            }
        }
        return isVerify;
    }
}
