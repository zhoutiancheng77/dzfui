package com.dzf.zxkj.platform.service.sys.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.ConCodeVO;
import com.dzf.zxkj.platform.model.sys.MaxCodeVO;
import com.dzf.zxkj.platform.service.sys.IBillCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.regex.Pattern;

@Service("billCodeServiceImpl")
public class BillCodeServiceImpl implements IBillCodeService {
    
    @Autowired
    private SingleObjectBO singleObjectBO;
    
    private final static String EXP_1 = "BEXP_1";
    
    private final static String EXP_2 = "BEXP_2";
    
    @Override
    public String getBillCode(final MaxCodeVO mcvo) throws DZFWarpException {
        String code = "";
        return code;
    }

    @Override
    public void unLockCode(MaxCodeVO mcvo) throws DZFWarpException {
        if(mcvo != null){
            String lockKey = mcvo.getPk_corp() + "-" + mcvo.getBillType() +"-"+ mcvo.getReturnCode();
//            LockUtil.getInstance().unLock_Key(mcvo.getTbName(), lockKey, mcvo.getUuid());
        }
    }
    
    
    private String getMaxCode(MaxCodeVO mcvo,ConCodeVO codeVO){
        if(StringUtil.isEmpty(mcvo.getCorpIdField())){
            mcvo.setCorpIdField("pk_corp");
        }
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append("select max(substr(");
        sql.append(mcvo.getFieldName());
        sql.append(",");
        sql.append(mcvo.getDiflen());
        sql.append(",30)) as vcode ");
        sql.append(" from ").append(mcvo.getTbName());
        sql.append(" WHERE ").append(mcvo.getFieldName());
        sql.append(" LIKE ");
        sql.append("'");
        sql.append(mcvo.getNewCode());
        sql.append("%'");
        sql.append(" and ").append(mcvo.getCorpIdField()).append(" = ?");
        sql.append(" and nvl(dr,0) = 0  ");
        sql.append(" and length(").append(mcvo.getFieldName()).append(")= ? " );
        sp.addParam(mcvo.getPk_corp());
        sp.addParam(mcvo.getVcode().length());
        
        if(!StringUtil.isEmpty(mcvo.getBillType()) && isBillType(mcvo.getBillType())){
            sql.append(" and vbilltype = ?");
            sp.addParam(mcvo.getBillType());
        }
        String maxcode =  (String) singleObjectBO.executeQuery(sql.toString(), sp,new ColumnProcessor("vcode"));
        
        int lastsn = 1;
        if(!StringUtil.isEmpty(maxcode) && isInteger(maxcode)){
            if(maxcode.length() == Integer.parseInt(codeVO.getInumber())){
               lastsn = Integer.parseInt(maxcode) + 1 ;
            }
        }
        String str = String.format("%0"+Integer.parseInt(codeVO.getInumber())+"d", lastsn);
        int len = str.length();
        if(len > Integer.parseInt(codeVO.getInumber())){
            String updateSql = " update ynt_contcodeset set inumber = ?,vcontcode = ? where pk_contcodeset = ? and pk_corp = ? ";
            SQLParameter param = new SQLParameter();
            param.addParam(len);
            param.addParam(mcvo.getNewCode() + String.format("%0"+len+"d", 1));
            param.addParam(codeVO.getPk_contcodeset());
            param.addParam(mcvo.getPk_corp());
            singleObjectBO.executeUpdate(updateSql, param);
        }
        return str;
    }
    
    public static boolean isInteger(String str) {  
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();  
    }

    /**
     * 单据号编码规则
     * @author gejw
     * @time 下午4:54:35
     * @param billType
     * @param pk_corp
     * @param typemax
     * @return
     */
    private ConCodeVO queryCodeSet(String billType, String pk_corp,String typemax){
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append("select * from ynt_contcodeset where vbilltype = ? and pk_corp= ? and nvl(dr,0)=0");
        sp.addParam(billType);
        sp.addParam(pk_corp);
        if(!StringUtil.isEmpty(typemax) && (billType.equals(IBillTypeCode.EP01) || billType.equals(IBillTypeCode.EP02))){
           sql.append(" and busitypemax = ? ");
           sp.addParam(typemax);
        }
        ArrayList<ConCodeVO> list = (ArrayList<ConCodeVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ConCodeVO.class));
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    
    private boolean isBillType(String billtype){
        if(billtype.equals(IBillTypeCode.EP11)){
            return false;
        }
        return true;
    }

    
    @Override
    public String getDefaultCode(final MaxCodeVO mcvo) throws DZFWarpException {
        String code = "";
        if(code.equals(EXP_1)){
            throw new BusinessException("自动生成编码失败，请手工录入编码。");
        }else if(code.equals(EXP_2)){
            throw new BusinessException("编码重复，请稍后再试.....");
        }
        return code;
    }

    /**
     * 判断手动输入的编码，是否符合自动生成的编码规则；且大于缓存里的最大流水号
     */
    private boolean checkIsCodeRule(MaxCodeVO mcvo){
        boolean flg=false;
        String code=mcvo.getEntryCode().replaceAll(" ", "");
        mcvo.setEntryCode(code);
        String rule=mcvo.getBillType();
        if(code.startsWith(rule)){
            if(code.length()==rule.length()+mcvo.getDiflen()){
                code=code.substring(rule.length());
                Pattern pattern = Pattern.compile("[0-9]*");
                if(pattern.matcher(code).matches()){
                    flg=true;
                }
            }
        }
        return flg;
    }
    
    /**
     * 从数据库里取上一个编码，并加1
     * @param mcvo
     * @return
     */
    private String makeCode(MaxCodeVO mcvo) {
        String code;
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        Integer len=mcvo.getBillType().length();
        sql.append("select max("+mcvo.getFieldName()+") as count from "+mcvo.getTbName());
//        sql.append(" where pk_corp=? and nvl(dr,0) = 0  and substr("+mcvo.getFieldName()+",0,"+len+")= ? ");
        sql.append(" where ").append(mcvo.getCorpIdField()).append(" = ?");
        sql.append(" and nvl(dr,0) = 0  and substr("+mcvo.getFieldName()+",0,"+len+")= ? ");
        sp.addParam(mcvo.getPk_corp());
        sp.addParam(mcvo.getBillType());
        String maxcode=null;
        try {
             maxcode = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor("count")).toString();
        } catch (NullPointerException e) {
            code=mcvo.getBillType()+String.format("%0"+mcvo.getDiflen()+"d", 1);
            return code;
        }
        return addCode(maxcode,mcvo.getDiflen());
    }
    
    /**
     * 在上一个编码上加1
     * @param maxcode：上一个编码
     * @param wei：几位流水号
     * @return
     */
    private String addCode(String maxcode,Integer wei){
        Integer num=Integer.parseInt(maxcode.substring(maxcode.length()-wei))+1;
        String str = String.format("%0"+wei+"d", num);
        maxcode=maxcode.substring(0,maxcode.length()-wei)+str;
        return maxcode;
    }
}
