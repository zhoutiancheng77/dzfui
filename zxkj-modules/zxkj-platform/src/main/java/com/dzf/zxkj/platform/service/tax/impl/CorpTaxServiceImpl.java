package com.dzf.zxkj.platform.service.tax.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.PeriodType;
import com.dzf.zxkj.common.constant.TaxRptConstPub;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.AreaVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.*;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.tax.ICorpTaxService;
import com.dzf.zxkj.base.query.QueryParamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author 
 * 
 */
@SuppressWarnings("all")
@Service("corpTaxService")
@Slf4j
public class CorpTaxServiceImpl implements ICorpTaxService {

    @Autowired
    private SingleObjectBO singleObjectBO = null;

    @Autowired
    private ICorpService corpService;
    
    // WJX
    @Override
    public CorpTaxInfoVO[] queryCorpTaxInfo(CorpVO corpVO) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append("nvl(dr,0) = 0 ");
        String pk_corp = corpVO.getPk_corp();
        if (!StringUtil.isEmpty(pk_corp)) {
            sql.append(" and pk_corp = ?");
            sp.addParam(pk_corp);
        }
        return (CorpTaxInfoVO[]) singleObjectBO.queryByCondition(CorpTaxInfoVO.class, sql.toString(), sp);
    }

    // WJX
    @Override
    public String[] insertCorpTaxinfo(CorpTaxInfoVO[] corpTaxinfoVOs) throws DZFWarpException {
        if (corpTaxinfoVOs != null && corpTaxinfoVOs.length > 0) {
            checkBeforeTaxSave(corpTaxinfoVOs);
            String[] keys = singleObjectBO.insertVOWithPK(corpTaxinfoVOs[0].getPk_corp(), corpTaxinfoVOs);
            return keys;
        }
        return null;
    }

    /**
     * 税率信息保存前校验
     * 
     * @param CorpTaxInfoVO
     *            WJX
     */
    private void checkBeforeTaxSave(CorpTaxInfoVO[] corpTaxVOs) {
        for (CorpTaxInfoVO vo : corpTaxVOs) {
            if (vo.getTaxcode() == null) {
                throw new BusinessException("税种不能为空,保存失败!");
            }
            if (vo.getTaxrate() == null) {
                throw new BusinessException("税率不能为空,保存失败!");
            }
        }
    }


    
   
    @SuppressWarnings("rawtypes")
    private void pushUpdateTaxInfo(String pk_corp, HashMap sendData, CorpVO oldvo, CorpVO corpVO) {

        String cd = new String("  pk_corp=? and nvl(dr,0)=0");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        CorpTaxInfoVO[] taxinfovo = null;
        if (sendData.get("addTaxvos") != null || sendData.get("updTaxvos") != null
                || sendData.get("delTaxvos") != null) {
            taxinfovo = (CorpTaxInfoVO[]) singleObjectBO.queryByCondition(CorpTaxInfoVO.class, cd, sp);
        }

        Object taxupdate = SpringUtils.getBean("ystaxsrv");

        try {
            Method m = taxupdate.getClass().getMethod("pushTaxinfoUpdate", CorpVO.class, CorpVO.class,
                    CorpTaxInfoVO[].class);
            m.invoke(taxupdate, oldvo, corpVO, taxinfovo);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            
            log.error("推送更新的税务税率信息异常", e);
            String msg = "调用益世接口异常";
            if(e instanceof InvocationTargetException){
                InvocationTargetException ee = (InvocationTargetException)e;
                if(ee.getTargetException() instanceof BusinessException){
                    msg = ee.getTargetException().getMessage();
                }           
            }           
            throw new BusinessException(msg);
        }
    }

    /**
     * WJX 税率信息更新
     */
    private void execTaxinfoUpate(Map sendData, String pk_corp) {
        // 提醒信息-新增
        CorpTaxInfoVO[] addvos = (CorpTaxInfoVO[]) sendData.get("addTaxvos");
        if (addvos != null && addvos.length > 0) {
            for (CorpTaxInfoVO addvo : addvos) {
                addvo.setPk_corp(pk_corp);
            }
            singleObjectBO.insertVOArr(pk_corp, addvos);
        }

        // 提醒信息-修改
        CorpTaxInfoVO[] updvos = (CorpTaxInfoVO[]) sendData.get("updTaxvos");
        if (updvos != null && updvos.length > 0) {
            for (CorpTaxInfoVO updvo : updvos) {
                updvo.setPk_corp(pk_corp);
            }
            singleObjectBO.updateAry(updvos);
        }

        // 提醒信息-删除
        CorpTaxInfoVO[] delvos = (CorpTaxInfoVO[]) sendData.get("delTaxvos");
        if (delvos != null && delvos.length > 0) {
            for (CorpTaxInfoVO delvo : delvos) {
                delvo.setPk_corp(pk_corp);
            }
            singleObjectBO.deleteVOArray(delvos);
        }
    }


    @Override
    public void updateCorp(CorpVO corpvo, HashMap<String, SuperVO[]> sendData, String[] taxRptids, String[] taxUnRptids)
            throws DZFWarpException {
        
        String pk_corp = corpvo.getPk_corp();
        
        checkIsOnly(corpvo, false);

        CorpVO oldvo = null;
        if ("益世财税".equalsIgnoreCase(corpvo.getVcustsource())) {
            oldvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
        }
        
        singleObjectBO.update(corpvo, getTaxRptUpdateFields());

        execTaxinfoUpate(sendData,corpvo.getPk_corp());
        
        String sql = "delete from ynt_taxrpt where pk_corp=?";
        SQLParameter params = new SQLParameter();
        params.addParam(corpvo.getPk_corp());
        singleObjectBO.executeUpdate(sql, params);
        
        if(taxRptids==null||taxRptids.length<=0){
            return ;
        }
        String insql = SqlUtil.buildSqlForIn("pk_taxrpttemplet",taxRptids);
        String wherept = " nvl(dr,0)=0 and " + insql;
        List<TaxRptTempletVO> rptlist = (List<TaxRptTempletVO>)singleObjectBO.retrieveByClause(TaxRptTempletVO.class, wherept, null);
        if(rptlist==null||rptlist.size()<=0){
            return ;
        }
        List<CorpTaxRptVO> taxlist = new ArrayList<CorpTaxRptVO>();
        for(TaxRptTempletVO vo : rptlist){
            CorpTaxRptVO taxvo = new CorpTaxRptVO();
            taxvo.setPk_taxrpttemplet(vo.getPk_taxrpttemplet());
            taxvo.setTaxrptcode(vo.getReportcode());
            taxvo.setTaxrptname(vo.getReportname());
            taxvo.setPk_corp(corpvo.getPk_corp());
            taxlist.add(taxvo);
        }
        
        //zpm,没有选中不存库
//        if(taxUnRptids != null && taxUnRptids.length > 0){
//            String inNosql = SqlUtil.buildSqlForIn("pk_taxrpttemplet",taxUnRptids);
//            String whereNopt = " nvl(dr,0)=0 and " + inNosql;
//            List<TaxRptTempletVO> rptnolist = (List<TaxRptTempletVO>)singleObjectBO.retrieveByClause(TaxRptTempletVO.class, whereNopt, null);
//            if(rptnolist==null||rptnolist.size()<=0){
//                return ;
//            }
//            for(TaxRptTempletVO vo : rptnolist){
//                CorpTaxRptVO taxvo = new CorpTaxRptVO();
//                taxvo.setPk_taxrpttemplet(vo.getPk_taxrpttemplet());
//                taxvo.setTaxrptcode(vo.getReportcode());
//                taxvo.setTaxrptname(vo.getReportname());
//                taxvo.setPk_corp(corpvo.getPk_corp());
//                taxvo.setIsselect(DZFBoolean.FALSE);
//                taxlist.add(taxvo);
//            }
//            
//        }
        //保存前校验
        checkOFromTaxlist(taxlist);
        //
        singleObjectBO.insertVOArr(corpvo.getPk_corp(), taxlist.toArray(new CorpTaxRptVO[0]));
        // WJX 如果为益世客户，推送更新的税务税率信息
        if (oldvo != null) {
            final String fpk_corp = pk_corp;
            @SuppressWarnings("rawtypes")
            final HashMap fsendData = sendData;
            final CorpVO foldvo = oldvo;
            final CorpVO fcorpVO = corpvo;
            pushUpdateTaxInfo(fpk_corp, fsendData, foldvo, fcorpVO);
        }
    }
    
	/**
	 * 查询申报种类信息
	 * @param groupkey
	 * @return
	 */
	private Map<String, TaxTypeSBZLVO> queryTypeSBZLVO(){
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		TaxTypeSBZLVO[] vos = (TaxTypeSBZLVO[])singleObjectBO.queryByCondition(TaxTypeSBZLVO.class, " nvl(dr,0) =0 and pk_corp = ? ", sp);
		if(vos == null || vos.length == 0)
			return null;
		List<TaxTypeSBZLVO> ltax = new ArrayList<TaxTypeSBZLVO>(Arrays.asList(vos));
		Map<String,TaxTypeSBZLVO> map = DZfcommonTools.hashlizeObjectByPk(ltax, new String[]{"pk_taxsbzl"});
		return map;
	}
	//增值税(月、季)报表二者选一
	//所得税 A类(月、季)报表二者选一
	//所得税 B类(月、季)报表二者选一
	//附加税【代征地税】(一般纳税人)(月、季)二者选一
	//附加税【代征地税】(小规模纳税人)(月、季)二者选一
	//附加税 的选择必须跟增值税一样
	//所得科A类和B类只能选一种
    private void checkOFromTaxlist(List<CorpTaxRptVO> taxlist){
    	if(taxlist == null || taxlist.size() == 0)
    		return;
    	List<String> pklist = queryTaxTemplates(taxlist);
    	Map<String,TaxTypeSBZLVO> map = queryTypeSBZLVO();
    	if(pklist == null || pklist.size() == 0)
    		return;
    	Map<String,List<TaxTypeSBZLVO>> map1 = groupSbzlvo(pklist,map);
    	StringBuffer sf = new StringBuffer();
    	Set<String> set = new  HashSet<String>();
    	Set<String> set2 = new  HashSet<String>();
    	Map<String,String> zmap = new HashMap<String,String>();
    	for(String xmcode : map1.keySet()){
    		List<TaxTypeSBZLVO> list = map1.get(xmcode);
    		set.clear();
    		if(list!=null && list.size() >1){
    			for(TaxTypeSBZLVO s : list){
    				set.add(String.valueOf(s.getSbzq()));
        		}
    		}
    		//校验一
    		if(set.size()>1){
    			sf.append(list.get(0).getZsxmname()+"，月报和季报只能选择一种，请修改！<br>");
    		}
    		//校验二
    		if(TaxRptConstPub.ZSXMCODE_01.equals(xmcode)
    				|| TaxRptConstPub.ZSXMCODE_80.equals(xmcode)){
    			if(list!=null && list.size()>0){
    				zmap.put(xmcode, String.valueOf(list.get(0).getSbzq()));
    			}
    		}
    		//校验三
    		if(TaxRptConstPub.ZSXMCODE_04.equals(xmcode)
    				|| TaxRptConstPub.ZSXMCODE_05.equals(xmcode)){
    			set2.add(xmcode);
    		}
    	}
    	//校验一
    	if(sf.length()>0){
    		throw new BusinessException(sf.toString());
    	}
    	//校验二
    	String z1 = zmap.get(TaxRptConstPub.ZSXMCODE_01);
    	String z2 = zmap.get(TaxRptConstPub.ZSXMCODE_80);
    	if(!StringUtil.isEmpty(z1)
    			&& !StringUtil.isEmpty(z2)
    			&& !z1.equals(z2)){
    		throw new BusinessException("增值税和附加税(代征地税)的申报周期不一致，请修改！");
    	}
    	//校验三
    	if(set2.size()>1){
    		throw new BusinessException("所得税A类和所得税B类只能申报一种，请修改！");
    	}
    }
    
    private Map<String,List<TaxTypeSBZLVO>> groupSbzlvo(List<String> pklist,Map<String,TaxTypeSBZLVO> map){
    	Map<String,List<TaxTypeSBZLVO>> map1 = new HashMap<String,List<TaxTypeSBZLVO>>();
    	String key = "";
    	for(String pk : pklist){
    		TaxTypeSBZLVO vo = map.get(pk);
    		if(vo!=null){
    			if(vo.getSbzq() == PeriodType.yearreport)//年报跳过
    				continue;
    			key = vo.getZsxmcode();
    			if(map1.containsKey(key)){
    				map1.get(key).add(vo);
    			}else{
    				List<TaxTypeSBZLVO> list = new ArrayList<TaxTypeSBZLVO>();
    				list.add(vo);
    				map1.put(key, list);
    			}
    		}
    	}
    	return map1;
    }
    
	private List<String> queryTaxTemplates(List<CorpTaxRptVO> taxlist){
		if(taxlist == null || taxlist.size() == 0)
			return null;
		StringBuffer sf = new StringBuffer();
		sf.append(" select distinct pk_taxsbzl from ynt_taxrpttemplet ");
		sf.append(" where  nvl(dr,0) = 0 and pk_taxrpttemplet in( ");
		String key = "";
		SQLParameter sp = new SQLParameter();
		for(int i = 0 ;i<taxlist.size();i++){
			key = taxlist.get(i).getPk_taxrpttemplet();
			if(StringUtil.isEmpty(key))
				continue;
			sp.addParam(key);
			if(i==0){
				sf.append("?");
			}else{
				sf.append(",?");
			}
		}
		sf.append(") ");
		//
		List<String> list = (List<String>)singleObjectBO.executeQuery(sf.toString(), sp,new ResultSetProcessor(){
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				List<String> list = new ArrayList<String>();
				String key = "";
				while(rs.next()){
					key = rs.getString("pk_taxsbzl");
					if(!StringUtil.isEmpty(key)){
						list.add(key);
					}
				}
				return list;
			}
		});
		return list;
	}
    
    
    private String[] getTaxRptUpdateFields(){
        
    	return new String[]{"unitname","vsoccrecode",//客户名称，省，纳税人识别号
                //"vcompcode",//计算机代码
                "chargedeptname",// 公司性质，法人代表"legalbodycode",
                //"vcorporatephone","vbankname",  // 开户银行名称，法人电话
                //"citybuildtax",
                "def16", //城建税，登录方式，国税密码
               // "ismaintainedtax" ,  //是否已维护税率信息
                "industrycode","industry"     //行业代码
                //,"taxcontrmachtype"     //行业代码
                //,"isdsbsjg"
                //,"ikjzc",
                //,"drzsj"
                //,"vschlnd"
                //,"vyhzc",//所得税报送机关，会计政策，认证时间，首次获利年度，公司可享受的优惠政策
                //"vpersonalpwd",
                //"vlocaltaxpwd",个税密码 、地税密码、
               , "vcity","varea",//市、区、征收方式
                //地税登记号、档案号、有无税控盘、是否有税控机、地税是否有UKEY、UKEY到期日
                //国税所、国税位置、国税专管员、 国税专管员电话、
                  
                //地税所、地税专管员、地税专管员电话、地税位置
                 "isxrq","drdsj","linkman1",
                 //法定代表人、法人电话、期末从业人数
                 "legalbodycode","vcorporatephone"
                };
        
    }

    
//    private String getTypeByVprovince(String vprovince,String zcsx,String corptype){
//        String type = null;
//    	if("重庆市".equals(vprovince)){
//    		if("一般纳税人".equals(zcsx)){
//    			type = " sb_zlbh in('10101','10414','1041401','10415','1041501','A','10601'";
//            	if("00000100AA10000000000BMD".equals(corptype)){//2013 会计期间
//        			type = type+",'C3','C301','C302')";
//                }else if("00000100AA10000000000BMF".equals(corptype)){//2007 会计期间
//                	type = type+",'C4','C401','C402')";
//                }else{
//                    type = type+")";
//                }
//            }else{
//            	type =" sb_zlbh in('10102','1010201','10412','1041201','10413','1041301','A','10602'";
//            	if("00000100AA10000000000BMD".equals(corptype)){//2013 会计期间
//        			type = type+",'C1','C101','C102')";
//                }else if("00000100AA10000000000BMF".equals(corptype)){//2007 会计期间
//                	type = type+",'C2','C201','C202')";
//                }else{
//                    type = type+")";
//                }
//            }
//    		
//    	}else{//默认北京
//    	    type = " sb_zlbh in('10102','50102','10412','A','D1','1010201','10413','10601'";
//            if("一般纳税人".equals(zcsx)){
//                type = " sb_zlbh in('10101','50101','10412','A','D1','10413','10601'";
//            }
//            
//            if("00000100AA10000000000BMD".equals(corptype)){//2003 会计期间
//                type = type+",'C1','39806')";
//            }else if("00000100AA10000000000BMF".equals(corptype)){//2007 会计期间
//                type = type+",'C2','39801')";
//            }else{
//                type = type+")";
//            }
//    	}
//    	return type;
//    }
    
    public List<TaxRptTempletVO> queryCorpTaxRpt(QueryParamVO paramvo)  throws DZFWarpException {
    	String pk_corp = paramvo.getPk_corp();
    	String dq = paramvo.getVprovince();
    	//获取当前地区的id
    	String dqid = queryAreaVOByDqname(dq);
    	String qyxz = paramvo.getZcsx();
    	CorpVO pvo = corpService.queryByPk(pk_corp);
		if(paramvo==null||StringUtil.isEmpty(pk_corp))
		    throw new BusinessException("公司信息异常");
		//查询符合当前公司条件的申报报表模板
		List<TaxRptTempletVO> list1 = queryCorpRptTempletVO(pvo,dq,qyxz);
		List<CorpTaxRptVO> list2 = null;
		//比如说，也有修改了地区，修改了纳税人资格的时候，也是需要加载相应的默认值，但他并不是数据库已存在的。
		//只有传过来的地区和corpvo 中的地区一致才加载数据库已存在的。
		//并且只有传过来的纳税人资格和corpvo 中的纳税人资格一致才加载数据库已存在的。
		
		
		//根据公司查询公司对应的报税信息表add by zhangj 
		SQLParameter sp = new SQLParameter();
		sp.addParam(pvo.getPk_corp());
		CorpTaxVo[] corptaxvo  = (CorpTaxVo[]) singleObjectBO.queryByCondition(CorpTaxVo.class, "nvl(dr,0)=0 and pk_corp = ?", sp);
		if(!StringUtil.isEmpty(pvo.getChargedeptname()) && corptaxvo!=null
			&& corptaxvo.length>0
			&& corptaxvo[0].getTax_area()!=null
			&& pvo.getChargedeptname().equals(qyxz)
			&& String.valueOf(corptaxvo[0].getTax_area()).equals(dqid)){
//		if(!StringUtil.isEmpty(pvo.getChargedeptname()) 
//			&& pvo.getVprovince()!=null
//			&& pvo.getChargedeptname().equals(qyxz)
//			&& String.valueOf(pvo.getVprovince()).equals(dqid)){
			list2 = queryCorpTaxRptVO(pk_corp);
		}
		//设置选择项
		setSelectValue(list1,list2,qyxz,true);
		//构造树
		list1 = buildParentTree(list1);
		return list1;
    }
    
    /**
     * 开通报税的地区
     * @return
     */
	private List<String> queryFromKtBsdq(){
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		String sql = " select distinct location dq from ynt_taxrpttemplet where pk_corp = ? and nvl(dr,0) = 0 ";
		List<String> list = (List<String>)singleObjectBO.executeQuery(sql, sp, new ResultSetProcessor(){
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				List<String> list = new ArrayList<String>();
				while(rs.next()){
					list.add(rs.getString("dq"));
				}
				return list;
			}
		});
		return list;
	}
	
	private String queryAreaVOByDqname(String dqname){
    	String id = null;
    	if(StringUtil.isEmpty(dqname))
    		return null;
    	String sql = " select region_id from ynt_area where  region_name = ? and pk_corp = ? and nvl(dr,0) = 0 ";
    	SQLParameter sp = new SQLParameter();
    	sp.addParam(dqname);
    	sp.addParam(IDefaultValue.DefaultGroup);
    	Object obj = singleObjectBO.executeQuery(sql, sp, new ColumnProcessor());
    	if(obj != null){
    		id = ((BigDecimal) obj).toString();
    	}
    	return id;
	}
    
    private String queryTaxrpttmpLoc(String dq)throws DZFWarpException {
    	String bsdq = "通用";
    	if(StringUtil.isEmpty(dq))
    		return bsdq;
    	dq = dq.substring(0, 2);
    	List<String> list = queryFromKtBsdq();
    	for(String key : list){
    		if(dq.startsWith(key)){
    			bsdq = key;
    			break;
    		}
    	}
    	return bsdq;
    }
    

	@Override
	public String queryTaxrpttmpLoc(CorpTaxVo taxvo) throws DZFWarpException {
		String dq = "通用";
		if(taxvo == null)
			return dq;
		String regionid = String.valueOf(taxvo.getTax_area());
		if(StringUtil.isEmpty(regionid))
			return dq;
    	dq = queryProvinceName(regionid);
    	dq = queryTaxrpttmpLoc(dq);
    	return dq;
	}
    
    
    private List<TaxRptTempletVO> queryCorpRptTempletVO(CorpVO pvo,String dq,String qyxz){
		String corptype = pvo.getCorptype();
		if(StringUtil.isEmpty(corptype))
			 throw new BusinessException("公司科目方案为空");
    	if(StringUtil.isEmpty(dq))
    		return null;
    	if(dq.length()<2)
    		return null;
    	dq = queryTaxrpttmpLoc(dq);
		if(!"一般纳税人".equals(qyxz)){
			qyxz = "小规模纳税人";
		}
		//加载模板
    	StringBuffer sf = new StringBuffer();
		sf.append(" select t2.sbcode,t2.sbname,t2.sbzq,t1.* from ynt_taxrpttemplet t1 ");
		sf.append(" join ynt_tax_sbzl t2 on t1.pk_taxsbzl = t2.pk_taxsbzl ");
		sf.append(" where location like ?  ");
		sf.append(" and (t2.qyxz is null or t2.qyxz = ?)  ");
		sf.append(" and (t2.kmfa is null or t2.kmfa = ?) ");
		sf.append(" and nvl(t2.dr,0) = 0 and nvl(t1.dr,0) = 0 ");
		sf.append(" order by t2.showorder,t1.orderno ");
		SQLParameter params = new SQLParameter();
		params.addParam(dq+"%");
		params.addParam(qyxz);
		params.addParam(corptype);
		List<TaxRptTempletVO> list1 = (List<TaxRptTempletVO>)singleObjectBO.executeQuery(sf.toString(), params, new BeanListProcessor(TaxRptTempletVO.class));
		params.clearParams();
		list1 = addHDzSfilter(pvo,list1);
		return list1;
    }
    
    /**
     * 增加核定征收的过滤，查询当前月的上一个月的申报税种，目的是让客户方便修改。
     * 否则就得记住每次征收方式调整的版本了，就很麻烦了。
     */
    private List<TaxRptTempletVO> addHDzSfilter(CorpVO pvo,List<TaxRptTempletVO> list1){
    	String period = getPreviousPeriod(pvo);
    	String pk_corp = pvo.getPk_corp();
    	TaxEffeHistVO hvo = queryTaxEffHisVO(pk_corp,period);
    	Integer type = hvo.getIncomtaxtype();
    	if(type == null)
    		return list1;
    	if(type == TaxRptConstPub.INCOMTAXTYPE_QY){//企业所得税
    		if(hvo.getTaxlevytype() == null || hvo.getTaxlevytype() == TaxRptConstPub.TAXLEVYTYPE_CZZS){//为空默认查账征收
    			list1 = filterQybsReport(list1,TaxRptConstPub.SHOWSDSRPT_A);
    		}else if(hvo.getTaxlevytype() != null && hvo.getTaxlevytype() == TaxRptConstPub.TAXLEVYTYPE_HDZS){//为核定征收
    			list1 = filterQybsReport(list1,TaxRptConstPub.SHOWSDSRPT_B);
    		}
    	}else if(type == TaxRptConstPub.INCOMTAXTYPE_GR){//个人所得税
    		list1 = filterQybsReport(list1,TaxRptConstPub.SHOWSDSRPT_GR);
    	}
    	return list1;
    }
    
    private String getPreviousPeriod(CorpVO pvo){
    	DZFDate jzdate = pvo.getBegindate();
    	String jzperiod = DateUtils.getPeriod(jzdate);
    	String nowperiod = DateUtils.getPeriod(new DZFDate());
    	if(nowperiod.compareTo(jzperiod) > 0){
    		return DateUtils.getPreviousPeriod(nowperiod);
    	}else{
    		return jzperiod;
    	}
    }
    
    private List<TaxRptTempletVO> filterQybsReport(List<TaxRptTempletVO> list1,int showsdsrpt){
    	if(list1 == null || list1.size() == 0)
    		return list1;
    	List<TaxRptTempletVO> list = new ArrayList<TaxRptTempletVO>();
    	for(TaxRptTempletVO vo : list1){
    		if(showsdsrpt == TaxRptConstPub.SHOWSDSRPT_A){
    			if(TaxRptConstPub.SB_ZLBH10413.equals(vo.getSbcode()) 
    					|| TaxRptConstPub.SB_ZLBHA06442.equals(vo.getSbcode())){
    				continue;
    			}
    		}else if(showsdsrpt == TaxRptConstPub.SHOWSDSRPT_B){
				if(TaxRptConstPub.SB_ZLBH10412.equals(vo.getSbcode())
						|| TaxRptConstPub.SB_ZLBHA06442.equals(vo.getSbcode())){
					continue;
				}
    		}else if(showsdsrpt == TaxRptConstPub.SHOWSDSRPT_GR){
    			if(TaxRptConstPub.SB_ZLBH10412.equals(vo.getSbcode()) 
    					|| TaxRptConstPub.SB_ZLBH10413.equals(vo.getSbcode())){
    				continue;
    			}
    		}
    		list.add(vo);
    	}
    	return list;
    }
    
    
    private List<CorpTaxRptVO> queryCorpTaxRptVO(String pk_corp){
    	//查询保存的申报报表
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" nvl(dr,0) = 0 and pk_corp = ? ");
		List<CorpTaxRptVO> list2 = (List<CorpTaxRptVO>)singleObjectBO.retrieveByClause(CorpTaxRptVO.class, sf.toString(), params);
		params.clearParams();
		return list2;
    }
    
    private void setSelectValue(List<TaxRptTempletVO> list1,List<CorpTaxRptVO> list2,String qyxz,boolean isinit){
    	if(list1 == null || list1.size() == 0)
    		return;
    	if(list2 != null && list2.size() > 0){
    		Set<String> set = new HashSet<String>();
    		for(CorpTaxRptVO vo : list2){
    			set.add(vo.getPk_taxrpttemplet());
    		}
        	//说明：：必填选项就必须默认打勾没有意义，因为对一家企业来说，增值税月报、季度只能报一个。
        	//然而月报、季报里面都有必填的。
        	for(TaxRptTempletVO vo : list1){
        		if(set.contains(vo.getPk_taxrpttemplet())){
        			vo.setIsselect(true);
        		}
        	}
    	}else{
    		//初始化默认
    		if(isinit){
    			//在线会计使用的时候，默认初始化
    			initSelectValue(list1,qyxz);
    		}else{
    			//一键报税的初始化的时候，默认不选择
    			//donothing
    		}
    	}
    }

    private void initSelectValue(List<TaxRptTempletVO> list1,String qyxz){
    	if(list1==null || list1.size()==0)
    		return;
    	int sbzq = PeriodType.monthreport;
    	if("小规模纳税人".equals(qyxz)){
    		sbzq = PeriodType.jidureport;
		}
		//    	初始化原则:
		//    	附加税	季月，通过企业性质判断
		//    	增值税	季月，通过企业性质判断
		//    	A类	  季
		//    	财报	  季
    	//		印花税月报中的（印花税纳税申报表） 
    	//		地方各项基金费（工会经费、垃圾处理费）申报表 （全表）  季//月来各自判断
		//    	年报	全出
    	

    	for(TaxRptTempletVO vv : list1){
    		if(vv.getSbcode().startsWith(TaxRptConstPub.SB_ZLBH10101)//增值税
    				|| vv.getSbcode().startsWith(TaxRptConstPub.SB_ZLBH10102)//增值税
    				|| vv.getSbcode().startsWith(TaxRptConstPub.SB_ZLBH50101)//附加税
    				|| vv.getSbcode().startsWith(TaxRptConstPub.SB_ZLBH50102)){
    			if(vv.getSbzq() == sbzq && vv.getIfrequired()!=null && vv.getIfrequired().booleanValue()){
    				vv.setIsselect(true);
    			}
    		}else if(vv.getSbcode().startsWith(TaxRptConstPub.SB_ZLBH10412)//所得税季度(月度)纳税申报表(A类)
    				|| vv.getSbcode().startsWith(TaxRptConstPub.SB_ZLBHC1)//财报
    				|| vv.getSbcode().startsWith(TaxRptConstPub.SB_ZLBHC2)
    				|| vv.getSbcode().startsWith(TaxRptConstPub.SB_ZLBHQYKJZD)){//企业会计制度
    			if(vv.getSbzq() == PeriodType.jidureport 
    					&& vv.getIfrequired()!=null && vv.getIfrequired().booleanValue()){
    				vv.setIsselect(true);
    			}
    		}else if((vv.getSbcode().startsWith(TaxRptConstPub.ZSXMCODE_D1) 
    				&& "印花税纳税申报表".equals(vv.getReportname()))){//印花税月报中的（印花税纳税申报表） 
    				vv.setIsselect(true);
    		}else if(vv.getSbcode().startsWith(TaxRptConstPub.ZSXMCODE_31399)){//地方各项基金费（工会经费、垃圾处理费）申报表 （全表）
    			if(vv.getSbzq() == sbzq){
    				vv.setIsselect(true);
    			}
    		}else if(vv.getSbzq() == PeriodType.yearreport
    				&& vv.getIfrequired()!=null && vv.getIfrequired().booleanValue()){
    			vv.setIsselect(true);
    		}
    	}
    }
    
    
	private List<TaxRptTempletVO> buildParentTree(List<TaxRptTempletVO> list) {
		if (list == null || list.size() <= 0)
			return null;
		List<String> orlist = new ArrayList<String>();
		for(TaxRptTempletVO v : list){
			if(!orlist.contains(v.getPk_taxsbzl())){
				orlist.add(v.getPk_taxsbzl());
			}
		}
		List<TaxRptTempletVO> reslist = new ArrayList<TaxRptTempletVO>();
		Map<String, List<TaxRptTempletVO>> mp = DZfcommonTools.hashlizeObject(list, new String[] { "pk_taxsbzl" });
		for(String key : orlist){
			List<TaxRptTempletVO> list1 = mp.get(key);
			if(list1 != null && list1.size()>0){
				boolean falg = setchildParentid(list1,key);
				TaxRptTempletVO parvo = new TaxRptTempletVO();
				parvo.setPk_taxrpttemplet(key);
				parvo.setReportcode(key);
				parvo.setReportname(list1.get(0).getSbname());
				parvo.setSbzq(list1.get(0).getSbzq());
				parvo.setIsselect(falg);//父级节点是否选中
				parvo.setChildren(list1.toArray(new TaxRptTempletVO[0]));
				if(ischildAllnotselect(list1)){//没有一张报表选中
					parvo.setState("closed");//默认treegrid不展开
				}
				reslist.add(parvo);
			}
		}
		return reslist;
	}
	
	private boolean ischildAllnotselect(List<TaxRptTempletVO> list1){
		boolean falg = true;
		if(list1 == null || list1.size() ==0)
			return falg;
		for(TaxRptTempletVO t : list1){
			if(t.isIsselect()){
				falg = false;
				break;
			}
		}
		return falg;
	}
	
	private boolean setchildParentid(List<TaxRptTempletVO> list1,String key){
		if(list1 == null || list1.size() ==0)
			return false;
		boolean falg = true;
		for(TaxRptTempletVO t : list1){
			t.setParent_id(key);//赋值上级pk
			if(falg && !t.isIsselect()){
				falg =  false;
			}
		}
		return falg;
	}
    
    /**
     * 
     */
    public void saveInitCorpRptVOs(CorpVO pvo)throws DZFWarpException{
    	String pk_corp = pvo.getPk_corp();
    	CorpTaxVo taxvo = queryCorpTaxVO(pk_corp);
    	String dq = queryTaxrpttmpLoc(taxvo);
    	String qyxz = pvo.getChargedeptname();
    	if(StringUtil.isEmpty(pk_corp) 
    			|| StringUtil.isEmpty(dq) 
    			|| StringUtil.isEmpty(qyxz))
    		return;
    	List<CorpTaxRptVO> list2 = queryCorpTaxRptVO(pk_corp);
    	if(list2 == null || list2.size() == 0){
    		List<TaxRptTempletVO> list1 = queryCorpRptTempletVO(pvo,dq,qyxz);
    		if(list1 == null || list1.size() == 0)
    			return;
        	initSelectValue(list1,qyxz);
        	List<TaxRptTempletVO> zlist = new ArrayList<TaxRptTempletVO>();
        	for(TaxRptTempletVO vo : list1){
        		if(vo!=null && vo.isIsselect()){
        			zlist.add(vo);
        		}
        	}
        	list2 =  buildCorpRptVOs(pk_corp,zlist);
        	saveinitRptTempletVO(pk_corp,list2);
    	}
    }
    
    private String queryProvinceName(String id){
    	String name = null;
    	if(StringUtil.isEmpty(id))
    		return name;
    	AreaVO vo = (AreaVO)singleObjectBO.queryByPrimaryKey(AreaVO.class, id);
    	if(vo != null){
    		name = vo.getRegion_name();
    	}
    	return name;
    }
    
    private List<CorpTaxRptVO> buildCorpRptVOs (String pk_corp,List<TaxRptTempletVO> list1){
    	if(list1 == null || list1.size() == 0)
    		return null;
        List<CorpTaxRptVO> taxlist = new ArrayList<CorpTaxRptVO>();
        for(TaxRptTempletVO vo : list1){
            CorpTaxRptVO taxvo = new CorpTaxRptVO();
            taxvo.setPk_taxrpttemplet(vo.getPk_taxrpttemplet());
            taxvo.setTaxrptcode(vo.getReportcode());
            taxvo.setTaxrptname(vo.getReportname());
            taxvo.setPk_corp(pk_corp);
            taxlist.add(taxvo);
        }
        return taxlist;
    }
    
    private void saveinitRptTempletVO(String pk_corp,List<CorpTaxRptVO> list2)throws DZFWarpException{
    	if(list2 == null || list2.size() ==0)
    		return;
    	String sql = " delete from ynt_taxrpt where pk_corp=? ";
        SQLParameter params = new SQLParameter();
        params.addParam(pk_corp);
        singleObjectBO.executeUpdate(sql, params);
        singleObjectBO.insertVOArr(pk_corp, list2.toArray(new CorpTaxRptVO[0]));
    }
    
//    @Deprecated
//    public List<TaxRptTempletVO> queryCorpTaxRpt2(QueryParamVO paramvo)  throws DZFWarpException {
//        
//        if(paramvo==null||StringUtil.isEmpty(paramvo.getPk_corp())){
//            throw new BusinessException("公司信息异常");
//        }
////      if(StringUtil.isEmpty(paramvo.getVprovince())||paramvo.getVprovince().indexOf("北京")<0){
////           return null;  //目前只支持北京
////      }
//        if(StringUtil.isEmpty(paramvo.getZcsx())){//公司性质
//            return null;
//        }
//        if(!"小规模纳税人".equals(paramvo.getZcsx())&&!"一般纳税人".equals(paramvo.getZcsx())){
//            throw new BusinessException("公司性质错误");
//        }
////        String type = " sb_zlbh in('10102','50102','10412','A','D1','1010201','10413','10601'";
////        if("一般纳税人".equals(paramvo.getZcsx())){
////            type = " sb_zlbh in('10101','50101','10412','A','D1','10413','10601'";
////        }
////        
//        String pk_corp = paramvo.getPk_corp();
//        CorpVO pvo = CorpCache.getInstance().get(null, pk_corp);
////        if("00000100AA10000000000BMD".equals(pvo.getCorptype())){//2003 会计期间
////            type = type+",'C1','39806')";
////        }else if("00000100AA10000000000BMF".equals(pvo.getCorptype())){//2007 会计期间
////            type = type+",'C2','39801')";
////        }else{
////            type = type+")";
////        }
//        
//        String type=getTypeByVprovince(paramvo.getVprovince(), paramvo.getZcsx(), pvo.getCorptype());
//        
//        SQLParameter params = new SQLParameter();
//        StringBuffer sbwhere = new StringBuffer();
//        sbwhere.append(" nvl(dr,0)=0 and ").append(type);
///*      if(StringUtil.isEmpty(paramvo.getVprovince())){
//            sbwhere.append(" and trim(location)='通用' ");
//        }else{
//            sbwhere.append(" and (trim(location)=? or trim(location)||'省'=? or trim(location)||'市'=?)");
//            params.addParam(paramvo.getVprovince());
//            params.addParam(paramvo.getVprovince());
//            params.addParam(paramvo.getVprovince());
//        }*/
//        sbwhere.append(" and (trim(location)=? or trim(location)||'省'=? or trim(location)||'市'=?)");
//        params.addParam(paramvo.getVprovince());
//        params.addParam(paramvo.getVprovince());
//        params.addParam(paramvo.getVprovince());
//        String orderBy = " sb_zlbh,orderno ";
//        List<TaxRptTempletVO> rptlist = (List<TaxRptTempletVO>)singleObjectBO.retrieveByClause(TaxRptTempletVO.class, sbwhere.toString(), orderBy, params);
//        
//        if(!StringUtil.isEmpty(paramvo.getVprovince())
//                &&(rptlist==null||rptlist.size()<=0)){
////          String sql = " nvl(dr,0)=0 and trim(location)='通用'";
//            String sql = " nvl(dr,0)=0 and trim(location)='通用' ";
//            if("一般纳税人".equals(paramvo.getZcsx())){
//                 sql = sql + " and sb_zlbh in ('A','10101','10412','10601' ,'10413','50101','10601','D1'";
//            }else{
//                 sql = sql + " and sb_zlbh in ('A','10102','10412','10601' ,'10413','1010201','50102','10601','D1'";
//                
//            }
//            //增加脚本
//            if("00000100AA10000000000BMD".equals(pvo.getCorptype())){//2003 会计期间
//                sql = sql+",'C1', '39806')";
//            }else if("00000100AA10000000000BMF".equals(pvo.getCorptype())){//2007 会计期间
//                sql = sql+",'C2', '39801')";
//            }else{
//                sql = sql+")";
//            }
//            rptlist = (List<TaxRptTempletVO>)singleObjectBO.retrieveByClause(TaxRptTempletVO.class, sql, orderBy, null);
//        }
//        setDefaultDatas(rptlist,paramvo.getVprovince());
//        initSelected(paramvo,rptlist);
//        
//        return buildParentTree(rptlist,paramvo.getVprovince());
//    }
    
    
//    private void setDefaultDatas(List<TaxRptTempletVO> rptlist,String vprovince){
//    	HashSet<String> hashSet=getChongQingDefaultReport();
//        for(TaxRptTempletVO vo : rptlist){
//            vo.setPdftemplet(null);
//            vo.setSpreadtemplet(null);
//            vo.setTs(null);
//            if(!"重庆市".equals(vprovince)){
//            	if(vo.getIfrequired()!=null&&vo.getIfrequired().booleanValue()){
//                    vo.setIsselect(true);
//                }else{
//                    if("A101010".equals(vo.getReportcode()) || "A102010".equals(vo.getReportcode()) || "A104000".equals(vo.getReportcode()) || "A105000".equals(vo.getReportcode()) 
//                            || "A105050".equals(vo.getReportcode()) || "A105080".equals(vo.getReportcode()) || "A106000".equals(vo.getReportcode()) 
//                            || "A107040".equals(vo.getReportcode()) || "10412004".equals(vo.getReportcode())){
//                        vo.setIsselect(true);
//                    }else{
//                        vo.setIsselect(false);
//                    }  
//                }
//            }else{
//            	if(hashSet.contains(vo.getReportcode())){
//                    vo.setIsselect(true);
//                }else{
//                    vo.setIsselect(false);
//                }  
//            }
//            //vo.setPrimaryKey(null);
//        }
//    }
    
//    private HashSet<String> getChongQingDefaultReport(){
//    	HashSet<String> hashSet=new HashSet<String>();
//    	/*一般纳税人增值税*/
//    	hashSet.add("10101001");//增值税纳税申报表（适用于增值税一般纳税人）
//    	hashSet.add("10101002");//增值税纳税申报表附列资料（一）
//    	hashSet.add("10101003");//增值税纳税申报表附列资料（二）
//    	hashSet.add("10101004");//增值税纳税申报表附列资料（三）
//    	hashSet.add("10101005");//增值税纳税申报表附列资料（四）
//    	hashSet.add("10101023");//增值税纳税申报表附列资料（五）
//    	hashSet.add("10101022");//本期抵扣进项税额结构明细表
//    	hashSet.add("10101006");//固定资产（不含不动产）进项税额抵扣情况表
//    	
//    	DZFDate curDate=new DZFDate();
//    	int month=curDate.getMonth();
//    	/*1 4 7 10月 默认勾选季报，别的月勾选月报，如果1-6还要勾选汇算清缴*/
//    	if(month==1||month==4||month==7||month==10){
//        	/*小规模纳税人增值税(季报)*/
//        	hashSet.add("10102001");//增值税纳税申报表
//        	/*企业所得税A类(季报)(一般和小规模一样)*/
//        	hashSet.add("10412001");//所得税月(季)度纳税申报表(A类）
//        	hashSet.add("10412004");//减免所得税优惠明细表(附表3)
//        	
//        	hashSet.add("10414001");//所得税月(季)度纳税申报表(A类）
//        	hashSet.add("10414004");//减免所得税优惠明细表(附表3)
//        	/*财报(季报)(一般和小规模一样)(一般企业和小企业一样)*/
//        	hashSet.add("C1001");//资产负债表
//        	hashSet.add("C1002");//利润表
//        	
//        	hashSet.add("C2001");//资产负债表
//        	hashSet.add("C2002");//利润表
//        	
//        	hashSet.add("C3001");//资产负债表
//        	hashSet.add("C3002");//利润表
//        	
//        	hashSet.add("C4001");//资产负债表
//        	hashSet.add("C4002");//利润表
//    	}else{
//        	/*小规模纳税人增值税(月报)*/
//        	hashSet.add("1010201001");//增值税纳税申报表
//        	/*企业所得税A类(月报)(一般和小规模一样)*/
//        	hashSet.add("1041201001");//所得税月(季)度纳税申报表(A类）
//        	hashSet.add("1041201004");//减免所得税优惠明细表(附表3)
//        	
//        	hashSet.add("1041401001");//所得税月(季)度纳税申报表(A类）
//        	hashSet.add("1041401004");//减免所得税优惠明细表(附表3)
//        	/*财报(月报)(一般和小规模一样)(一般企业和小企业一样)*/
//        	hashSet.add("C101001");//资产负债表
//        	hashSet.add("C101002");//利润表
//        	
//        	hashSet.add("C201001");//资产负债表
//        	hashSet.add("C201002");//利润表
//        	
//        	hashSet.add("C301001");//资产负债表
//        	hashSet.add("C301002");//利润表
//        	
//        	hashSet.add("C401001");//资产负债表
//        	hashSet.add("C401002");//利润表
//    	}
//    	if(month>=1&&month<=6){
//    		hashSet.add("AAAAAAA");//封面
//    		hashSet.add("A000000");//企业基础信息表
//    		hashSet.add("A100000");//中华人民共和国企业所得税年度纳税申报表（A类）
//    		hashSet.add("A101010");//一般企业收入明细表
//    		hashSet.add("A102010");//一般企业成本支出明细表
//    		hashSet.add("A104000");//期间费用明细表
//    		hashSet.add("A105000");//纳税调整项目明细表
//    		hashSet.add("A105050");//职工薪酬纳税调整明细表
//    		hashSet.add("A105080");//资产折旧、摊销情况及纳税调整明细表
//    		hashSet.add("A106000");//企业所得税弥补亏损明细表
//    		hashSet.add("A107040");//减免所得税优惠明细表
//    	}
//    	return hashSet;
//    }
    
//    @SuppressWarnings("unchecked")
//    private void initSelected(QueryParamVO paramvo,List<TaxRptTempletVO> rptlist){
//        
//        if(rptlist==null||rptlist.size()<=0){
//            return;
//        }
//        
//        String where = " nvl(dr,0)=0 and pk_corp=?";
//        SQLParameter params = new SQLParameter();
//        params.addParam(paramvo.getPk_corp());
//        List<CorpTaxRptVO> list = (List<CorpTaxRptVO>)singleObjectBO.retrieveByClause(CorpTaxRptVO.class, where, params);
//        if(list==null||list.size()<=0){
//            return;
//        }
//        Set<String> set = new HashSet<String>();
//        Set<String> setSel = new HashSet<String>();
//        for(CorpTaxRptVO vo : list){
//            if(vo.getIsselect() != null && vo.getIsselect().booleanValue()){
//                set.add(vo.getPk_taxrpttemplet());
//            }else{
//                setSel.add(vo.getPk_taxrpttemplet());
//            }
//        }
//        for(TaxRptTempletVO vo : rptlist){
//            if(set.contains(vo.getPk_taxrpttemplet())){
//                vo.setIsselect(true);
//            }else if(setSel.contains(vo.getPk_taxrpttemplet())){
//                vo.setIsselect(false);
//            }          
//        }
//    }
    
//    @SuppressWarnings({"unchecked" })
//    private List<TaxRptTempletVO> buildParentTree(List<TaxRptTempletVO> list,String vprovince){
//        
//        if(list==null||list.size()<=0){
//            return null;
//        }
//        List<TaxRptTempletVO> reslist = new ArrayList<TaxRptTempletVO>();
//        
//        Map<String,List<TaxRptTempletVO>> mp = DZfcommonTools.hashlizeObject(list, new String[]{"sb_zlbh"});
//        Iterator<String> it = mp.keySet().iterator();
//        String key = "";
//        while(it.hasNext()){
//            key = it.next();
//            TaxRptTempletVO parvo = new TaxRptTempletVO();
//            parvo.setPk_taxrpttemplet(key);
//            parvo.setReportcode(key);
//            parvo.setReportname(getTaxRptNameByCode(key,vprovince));
//            parvo.setChildren(mp.get(key).toArray(new TaxRptTempletVO[0]));
//            reslist.add(parvo);
//        }
//        Collections.sort(reslist, new Comparator<TaxRptTempletVO>() {
//            @Override
//            public int compare(TaxRptTempletVO arg0, TaxRptTempletVO arg1) {
//                int i = arg0.getReportcode().compareTo(arg1.getReportcode());
//                return i;
//            }
//        });
//        return reslist;     
//    }
    
//    private String getTaxRptNameByChongQing(String code){
//        String name = "申报表";
//        switch(code){
//            case  "10101":
//                name = CqtcZLBHConst.SB_ZLBH10101_NAME;
//                break;
//            case  "10414":
//                name = CqtcZLBHConst.SB_ZLBH10414_NAME;
//                break;
//            case  "1041401":
//                name = CqtcZLBHConst.SB_ZLBH1041401_NAME;
//                break;
//            case  "10415":
//                name = CqtcZLBHConst.SB_ZLBH10415_NAME;
//                break;
//            case  "1041501":
//                name = CqtcZLBHConst.SB_ZLBH1041501_NAME;
//                break;
//            case  "C401":
//                name = CqtcZLBHConst.SB_ZLBHC401_NAME;
//                break;
//            case  "C4":
//                name = CqtcZLBHConst.SB_ZLBHC4_NAME;
//                break;
//            case  "C402":
//                name = CqtcZLBHConst.SB_ZLBHC402_NAME;
//                break;
//            case  "C301":
//                name = CqtcZLBHConst.SB_ZLBHC301_NAME;
//                break;
//            case  "C3":
//                name = CqtcZLBHConst.SB_ZLBHC3_NAME;
//                break;
//            case  "C302":
//                name = CqtcZLBHConst.SB_ZLBHC302_NAME;
//                break;
//            case  "10601":
//                name = CqtcZLBHConst.SB_ZLBH10601_NAME;
//                break;
//            case  "A":
//                name = CqtcZLBHConst.SB_ZLBH_SETTLEMENT_A_NAME;
//                break;
//            case  "10102":
//                name = CqtcZLBHConst.SB_ZLBH10102_NAME;
//                break;
//            case  "1010201":
//                name = CqtcZLBHConst.SB_ZLBH1010201_NAME;
//                break;
//            case  "10412":
//                name = CqtcZLBHConst.SB_ZLBH10412_NAME;
//                break;
//            case  "1041201":
//                name = CqtcZLBHConst.SB_ZLBH1041201_NAME;
//                break;
//            case  "10413":
//                name = CqtcZLBHConst.SB_ZLBH10413_NAME;
//                break;
//            case  "1041301":
//                name = CqtcZLBHConst.SB_ZLBH1041301_NAME;
//                break;
//            case  "C201":
//                name = CqtcZLBHConst.SB_ZLBHC201_NAME;
//                break;
//            case  "C2":
//                name = CqtcZLBHConst.SB_ZLBHC2_NAME;
//                break;
//            case  "C202":
//                name = CqtcZLBHConst.SB_ZLBHC202_NAME;
//                break;
//            case  "C101":
//                name = CqtcZLBHConst.SB_ZLBHC101_NAME;
//                break;
//            case  "C1":
//                name = CqtcZLBHConst.SB_ZLBHC1_NAME;
//                break;
//            case  "C102":
//                name = CqtcZLBHConst.SB_ZLBHC102_NAME;
//                break;
//            case  "10602":
//                name = CqtcZLBHConst.SB_ZLBH10602_NAME;
//                break;
//            case  "B":
//                name = CqtcZLBHConst.SB_ZLBH_SETTLEMENT_B_NAME;
//                break;
//        }
//        
//        return name;        
//    }
//    private String getTaxRptNameByCode(String code,String vprovince){
//        if("重庆市".equals(vprovince)){
//        	return getTaxRptNameByChongQing(code);
//        }
//        
//        String name = "申报表";
//        switch(code){
//            case  "10101":
//                name = ITaxReportConst.SB_ZLBH10101_NAME;
//                break;
//            case  "10102":
//                name = ITaxReportConst.SB_ZLBH10102_NAME;
//                break;
//            case  "50101":
//                name = ITaxReportConst.SB_ZLBH50101_NAME;
//                break;
//            case  "50102":
//                name = ITaxReportConst.SB_ZLBH50102_NAME;
//                break;
//            case  "10412":
//                name = "所得税月(季)度纳税申报表(A类)";
//                break;
//            case  "A":
//                name = ITaxReportConst.SB_ZLBHA_NAME;
//                break;
//            case  "C1"://财报
//                name = ITaxReportConst.SB_ZS_CB;
//                break;
//            case  "C2"://财报
//                name = ITaxReportConst.SB_ZS_CB;
//                break;
//            case  "D1"://印花税
//                name = ITaxReportConst.SB_ZS_YHS;
//                break;
//            case  "1010201":
//                name = "增值税小规模纳税人申报表月报";
//                break;
//            case  "10413":
//                name = "所得税月(季)度纳税申报表(B类)";
//                break;
//            case  "39801":
//                name = "财报年报";
//                break;
//            case  "39806":
//                name = "财报年报";
//                break;
//            case  "10601":
//                name = "文化事业建设费";
//                break;
//        }
//        
//        return name;        
//    }

    //更新缓存
    @Override
    public void updateTaxradio(String pk_corp, String radio) throws DZFWarpException {
        String sql = "update bd_corp set def19 = ? where pk_corp = ?";
        SQLParameter params = new SQLParameter();
        params.addParam(radio);
        params.addParam(pk_corp);
        singleObjectBO.executeUpdate(sql, params);
    }
    
    private boolean checkIsOnly(CorpVO condCorpVO, boolean isInsert)
            throws DZFWarpException {
        StringBuilder sbCode = new StringBuilder(
                "select count(1) from bd_corp where innercode='").append(
                condCorpVO.getInnercode()).append("' and fathercorp = '"+condCorpVO.getFathercorp()+"'");
        StringBuilder sbName = new StringBuilder(
                "select count(1) from bd_corp where unitname='").append(
                condCorpVO.getUnitname()).append("' and fathercorp = '"+condCorpVO.getFathercorp()+"'");
        if (!isInsert) {
            sbCode.append(" and pk_corp!='").append(condCorpVO.getPrimaryKey())
                    .append("'");
            sbName.append(" and pk_corp!='").append(condCorpVO.getPrimaryKey())
                    .append("'");
        }
        BigDecimal ojbCodeNum = (BigDecimal) singleObjectBO.executeQuery(
                sbCode.toString(), null, new ColumnProcessor());
        Integer repeatCodeNum = ojbCodeNum.intValue();
        BigDecimal objNameNum = (BigDecimal) singleObjectBO.executeQuery(
                sbName.toString(), null, new ColumnProcessor());
        Integer repeatNameNum = objNameNum.intValue();

        if (repeatCodeNum > 0) {
            throw new BusinessException("客户编码不能重复。");
        } else if (repeatNameNum > 0) {
            throw new BusinessException("客户名称不能重复。");
        }
        return false;
    }
    
	@Override
	public CorpTaxVo queryCorpTaxVO(String pk_corp) throws DZFWarpException {
		CorpTaxVo vo = new CorpTaxVo();
		vo.setPk_corp(pk_corp);
		
		if(StringUtil.isEmpty(pk_corp))
			return vo;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		CorpTaxVo[] bodyvos = (CorpTaxVo[])singleObjectBO.queryByCondition(CorpTaxVo.class, " pk_corp = ? and nvl(dr,0) = 0  ", sp);
		if(bodyvos != null && bodyvos.length > 0){
			vo = bodyvos[0];
		}
		setCorpTaxDefaultValue(vo, pk_corp);
		return vo;
	}
	
	//默认值
	private void setCorpTaxDefaultValue(CorpTaxVo vo, String pk_corp){
		CorpVO corpVO = (CorpVO) singleObjectBO.queryVOByID(pk_corp,
				CorpVO.class);
		
		if(corpVO == null)
			return;
		
		String yhzc = vo.getVyhzc();//优惠政策
		if(StringUtil.isEmpty(yhzc)){
			vo.setVyhzc("0");//默认为小型微利
		}
		
		DZFDouble citytax = vo.getCitybuildtax();//城建税
		if(citytax == null){
			vo.setCitybuildtax(new DZFDouble(0.07));
		}
		DZFDouble localtax = vo.getLocaleducaddtax();//地方教育附加费
		if(localtax == null){
			vo.setLocaleducaddtax(new DZFDouble(0.02));
		}
//		DZFDouble educaddtax = vo.getEducaddtax();//教育费附加
//		if(educaddtax == null){
//			vo.setEducaddtax(new DZFDouble(0.03));
//		}
		
		String corptype = corpVO.getCorptype();
		if(!"00000100AA10000000000BMD".equals(corptype)){//不是科目方案13不设默认值
			return;
		}
		
		Integer intype = vo.getIncomtaxtype();
		if(intype == null){
			Integer comtype = corpVO.getIcompanytype();
			if(comtype == null ||
					( comtype != 2 && comtype != 20 && comtype != 21)){
				vo.setIncomtaxtype(TaxRptConstPub.INCOMTAXTYPE_QY);
			}else{
				vo.setIncomtaxtype(TaxRptConstPub.INCOMTAXTYPE_GR);
			}
		}
		
		Integer taxletype = vo.getTaxlevytype();
		if(taxletype == null){
			vo.setTaxlevytype(TaxRptConstPub.TAXLEVYTYPE_CZZS);//查账征收
		}
	}
	
	@Override
	public TaxEffeHistVO queryTaxEffHisVO(String pk_corp, String period) throws DZFWarpException {
		
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		String corptype = corpvo.getCorptype();
		if(!"00000100AA10000000000BMD".equals(corptype)){//不是科目方案13不设默认值
			TaxEffeHistVO effvo = new TaxEffeHistVO();
			effvo.setTaxlevytype(TaxRptConstPub.TAXLEVYTYPE_CZZS);//默认查账征收
			//企业所得税类型为空
			return effvo;
		}
		
		TaxEffeHistVO effvo = null;
//		Integer type = 1;//默认查账征收
		List<TaxEffeHistVO> hiss = queryChargeHis(pk_corp);
		if(hiss != null && hiss.size() > 0){
			Integer hisType;
			String beginPer;
			String endPer;
			for(TaxEffeHistVO vo : hiss){
				hisType = vo.getTaxlevytype();
				beginPer = vo.getSxbegperiod();
				endPer = vo.getSxendperiod();
				if(hisType == TaxRptConstPub.TAXLEVYTYPE_CZZS 
						&& period.compareTo(beginPer) >= 0
						&& (StringUtil.isEmpty(endPer) 
								|| period.compareTo(endPer) <= 0)){//查账征收
					effvo = vo;
					break;
				}
				if(hisType == TaxRptConstPub.TAXLEVYTYPE_HDZS 
						&& period.compareTo(beginPer) >= 0
						&& period.compareTo(endPer) <=0){
					effvo = vo;
					break;
				}
				
			}
		}
		
		if(effvo == null){
			CorpTaxVo taxvo = queryCorpTaxVO(pk_corp);
			effvo = new TaxEffeHistVO();
			effvo.setTaxlevytype(taxvo.getTaxlevytype());//征收
			effvo.setIncomtaxtype(taxvo.getIncomtaxtype());//所得税
			effvo.setIncometaxrate(taxvo.getIncometaxrate());//所得税率
			effvo.setSxbegperiod(taxvo.getSxbegperiod());//生效开始期间
			effvo.setSxendperiod(taxvo.getSxendperiod());//生效结束期间
			
		}
		
		return effvo;
	}
	
	@Override
	public List<TaxEffeHistVO> queryChargeHis(String pk_corp) throws DZFWarpException {
		String sql = " select * from ynt_taxeff_his y where nvl(dr,0)=0 and pk_corp = ? order by sxbegperiod desc ";//按生效开始期间排
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		List<TaxEffeHistVO> list = (List<TaxEffeHistVO>) singleObjectBO.executeQuery(sql, 
				sp, new BeanListProcessor(TaxEffeHistVO.class));
		
		return list;
	}
}