package com.dzf.zxkj.app.service.pub.impl;

import com.dzf.zxkj.app.model.app.corp.TempCorpVO;
import com.dzf.zxkj.app.model.app.corp.UserToCorp;
import com.dzf.zxkj.app.model.app.remote.AppCorpCtrlVO;
import com.dzf.zxkj.app.model.app.user.TempUserRegVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.rsa.RSAEncrypt;
import com.dzf.zxkj.app.pub.rsa.RSAUtils;
import com.dzf.zxkj.app.service.pub.IAppPubservice;
import com.dzf.zxkj.app.service.pub.IParameterSetService;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.app.utils.QueryDeCodeUtils;
import com.dzf.zxkj.app.utils.SourceSysEnum;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.Encode;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.ResponAreaVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * app公共接口的实现类
 * @author zhangj
 *
 */
@Service("apppubservice")
public class AppPubService implements IAppPubservice {
	
	
	private SingleObjectBO singleObjectBO;

	
	@Autowired
	private IParameterSetService paramService;

	/**
	 * 解密
	 * 
	 * @return
	 * @throws Exception
	 */
	public String decryptPwd(String sysType, String password1) throws DZFWarpException {
		String password = null;
		try{

			if (IConstant.FIRDES.equals(sysType)) {
				// IOS解密
				password = new Encode().encode(RSAEncrypt.iosdecrypt(password1));
			} else {
				password = new Encode().encode(RSAUtils.decryptString(password1));
			}
		}catch(Exception e){
			throw new WiseRunException(e);
		}
		
		return password;
	}
	
	
	@Override
	public Map<String,String> queryArea(String keyvalue) throws DZFWarpException {
		StringBuffer sql=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sql.append("select region_id,parenter_id, region_name\n");
		sql.append("  from ynt_area\n"); 
		sql.append(" where nvl(dr, 0) = 0\n"); 
		ArrayList<ResponAreaVO> list= (ArrayList<ResponAreaVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ResponAreaVO.class));
		
		//转换map
		Map<String,String> areamap = new HashMap<String,String>();
		
		if(keyvalue.equals("0")){
			for(ResponAreaVO areavo:list){
				areamap.put(String.valueOf(areavo.getRegion_id()), areavo.getRegion_name());
			} 
		}else if(keyvalue.equals("1")){
			for(ResponAreaVO areavo:list){
				areamap.put(areavo.getParenter_id()+"_"+areavo.getRegion_name(), String.valueOf(areavo.getRegion_id()));
			} 
		}
		
		return areamap;
	}

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}


	@Override
	public String getUserName(String account_id) throws DZFWarpException {
		
		String uname = null;
		if(!StringUtil.isEmpty(account_id)){
			
			UserVO uvo =  (UserVO) singleObjectBO.queryByPrimaryKey(UserVO.class, account_id);
			
			if(uvo != null){
				uname = CodeUtils1.deCode(uvo.getUser_name());
			}else{
				TempUserRegVO regvo = (TempUserRegVO) singleObjectBO.queryByPrimaryKey(TempUserRegVO.class, account_id);
				uname = regvo.getUser_name();
			}
		}
		
		return uname;
	}


	@Override
	public String getCorpName(String pk_corp, String pk_temp_corp) throws DZFWarpException {
		
		String corpname = null;
		
		if(!AppCheckValidUtils.isEmptyCorp(pk_corp)){
			
			CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
			
			if(cpvo!=null ){
				corpname = CodeUtils1.deCode(cpvo.getUnitname());
			}
			
		}else if(!StringUtil.isEmpty(pk_temp_corp)){
			
			TempCorpVO tempvo =  (TempCorpVO) singleObjectBO.queryByPrimaryKey(TempCorpVO.class, pk_temp_corp);
			
			
			if(tempvo != null){
				corpname = tempvo.getCorpname();
			}
		}
		
		return corpname;
	}


	@Override
	public CorpVO[] getNoLinkCorp(String account,String sourcesys) throws DZFWarpException {
		
		if(StringUtil.isEmpty(account)){
			return null;
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(account);
		UserVO[] uvos = (UserVO[]) singleObjectBO.queryByCondition(UserVO.class, "nvl(dr,0)=0 and user_code = ?", sp);
		
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select bp.pk_corp,bp.unitname,ba.phone1,ba.unitname as fathercorp from bd_corp bp ");
		sql.append(" inner join bd_account ba on bp.fathercorp = ba.pk_corp  ");
		sql.append(" where bp.phone2 = ?  and nvl(bp.isaccountcorp,'N')='N' and nvl(bp.isseal,'N')='N'  ");
		sql.append(" and nvl(bp.dr,0)=0 ");
		sql.append(" and  not exists( ");
		sql.append("       select 1 from ynt_corp_user  yu ");
		sql.append("       where   nvl(yu.dr,0)=0 and bp.pk_corp =yu.pk_corp and yu.pk_user = ? ) ");
		sp.clearParams();
		sp.addParam(CodeUtils1.enCode(account));
		if(uvos!=null && uvos.length>0){
			sp.addParam(uvos[0].getCuserid());
		}else{//如果不存在，肯定也不在公司内存在，所以默认用手机号
			sp.addParam(account);
		}
		if(SourceSysEnum.SOURCE_SYS_CST.getValue().equals(sourcesys)){
			sql.append(" and ba.pk_corp = ?  ");
			List<TempUserRegVO> tlist = getTempList(account);
			sp.addParam(tlist.get(0).getPk_svorg());
		}
		
		
		List<CorpVO> cpvos  =  (List<CorpVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(CorpVO.class));
		
		if (cpvos != null && cpvos.size() > 0){
			
			cpvos =  (List<CorpVO>) QueryDeCodeUtils.decKeyUtils(new String[]{"unitname","phone1","fathercorp"}, cpvos, 1);
			
			return cpvos.toArray(new CorpVO[0]);
		}
		
		return null;
	}
	
	/**
	 * 获取有权限的用户信息
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<String> getUserForPowerCorp(String pk_corp,String funpk) throws DZFWarpException{
		
		if(AppCheckValidUtils.isEmptyCorp(pk_corp)){
			throw new BusinessException("公司信息不能为空!");
		}
		
		SQLParameter sp = new SQLParameter();
		StringBuffer qrysql = new StringBuffer();
		qrysql.append("  select distinct sr.cuserid ");
		qrysql.append("   from sm_user_role  sr ");
		qrysql.append("   inner join  sm_power_func sf on sf.pk_role = sr.pk_role   ");
		qrysql.append("   where nvl(sr.dr,0)=0  and nvl(sf.dr,0)=0");
		qrysql.append("     and sr.pk_corp = ?  ");
		sp.addParam(pk_corp);
		if(!StringUtil.isEmpty(funpk)){
			qrysql.append("   and sf.resource_data_id = ? ");
			sp.addParam(funpk);
		}
		List<String> columns =  (List<String>) singleObjectBO.executeQuery(qrysql.toString(), sp, new ColumnListProcessor());
		
		return columns;
	}
	
	
	/**
	 * 获取参数设置(是否是系统后台生成)
	 */
	public boolean isParamSysCreatePZ(String pk_corp) throws DZFWarpException{
//		boolean result = false;
//		YntParameterSet parameter = paramService.queryParamterbyCode(pk_corp, PhotoParaCtlState.PhotoParaCtlCode);
//		Integer pardetailValue = parameter.getPardetailvalue();
//		if(parameter == null 
//				|| pardetailValue == PhotoParaCtlState.PhotoParaCtlValue_Sys){//参数如果不是会计公司生成，返回
//			result = true;
//		}
		CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		
		if(cpvo.getDef4()!=null && cpvo.getDef4().equals(PhotoState.TREAT_TYPE_0+"")){
			return false;
		}
		return true;
	}
	
	
	public Map<String, AppCorpCtrlVO> queryCorpCtrl(String pk_corp, String cuserid){
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT distinct rol.pk_corp as pk_corpk, funn.fun_name\n") ;
		sql.append("  FROM sm_user_role rol\n") ; 
		sql.append(" inner join sm_user usr on rol.cuserid = usr.cuserid\n") ; 
		sql.append(" inner join sm_role urol on rol.pk_role = urol.pk_role\n") ; 
		sql.append(" inner join sm_power_func fun on rol.pk_role = fun.pk_role\n") ; 
		sql.append(" inner join sm_funnode funn on fun.resource_data_id = funn.pk_funnode\n") ; 
		sql.append(" where nvl(rol.dr, 0) = 0\n") ; 
		sql.append("   and nvl(urol.dr, 0) = 0\n") ; 
		sql.append("   and nvl(fun.dr, 0) = 0\n") ; 
		sql.append("   and nvl(funn.dr, 0) = 0\n") ; 
		sql.append("   and usr.pk_corp = ? \n") ; 
		spm.addParam(pk_corp);
		sql.append("   and usr.cuserid = ? \n") ; 
		spm.addParam(cuserid);
		sql.append("   and funn.fun_name in ('上传图片', '填制凭证')");
		List<AppCorpCtrlVO> list = (List<AppCorpCtrlVO>)singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(AppCorpCtrlVO.class));
		Map<String,AppCorpCtrlVO> map = new HashMap<String,AppCorpCtrlVO>();
		if(list != null && list.size() > 0){
			AppCorpCtrlVO newvo = null;
			AppCorpCtrlVO oldvo = null;
			for(AppCorpCtrlVO vo : list){
				if(!map.containsKey(vo.getPk_corpk())){
					newvo = new AppCorpCtrlVO();
					if("上传图片".equals(vo.getFun_name())){
						newvo.setIshasupload(DZFBoolean.TRUE);
					}else if("填制凭证".equals(vo.getFun_name())){
						newvo.setIshasmake(DZFBoolean.TRUE);
					}
					map.put(vo.getPk_corpk(), newvo);
				}else{
					oldvo = map.get(vo.getPk_corpk());
					if("上传图片".equals(vo.getFun_name())){
						oldvo.setIshasupload(DZFBoolean.TRUE);
					}else if("填制凭证".equals(vo.getFun_name())){
						oldvo.setIshasmake(DZFBoolean.TRUE);
					}
				}
			}
		}
		return map;
	}


	@Override
	public Map<String, String> getCorpid(String[] corpname, String admincorpid,String account_id,Integer power) throws DZFWarpException {

		if (StringUtil.isEmpty(admincorpid)) {
			throw new BusinessException("代账公司信息为空!");
		}

		if (corpname == null || corpname.length == 0) {
			throw new BusinessException("公司名称不能为空!");
		}

		List<String> ulistname = new ArrayList<String>();

		for (String strname : corpname) {
			ulistname.add(CodeUtils1.enCode(strname));
		}

		StringBuffer qrysql = new StringBuffer();

		qrysql.append("select pk_corp ,unitname");
		qrysql.append("  from bd_corp ");
		qrysql.append("  where nvl(dr,0)=0");
		qrysql.append("  and fathercorp = ? ");
		qrysql.append(" and " + SqlUtil.buildSqlForIn("unitname", ulistname.toArray(new String[0])));
		
	    SQLParameter sp = new SQLParameter();
	    sp.addParam(admincorpid);
	    
	    List<CorpVO> cplist =  (List<CorpVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(CorpVO.class));
	    
	    Map<String, String> resmap = new HashMap<String, String>();
	    
	    if(cplist!=null && cplist.size()>0){
	    	
	    	Map<String, AppCorpCtrlVO> ctrlmap = queryCorpCtrl(admincorpid, account_id);//查询权限
	    	
	    	AppCorpCtrlVO ctrlvo = null; 
			for (CorpVO cpvo : cplist) {
				DZFBoolean isadd = DZFBoolean.FALSE;

				ctrlvo = ctrlmap.get(cpvo.getPk_corp());

				if (power == null) {
					isadd = DZFBoolean.TRUE;
				} else if (power == 1 && ctrlvo != null && ctrlvo.getIshasupload() != null
						&& ctrlvo.getIshasupload().booleanValue()) {//上传图片
					isadd = DZFBoolean.TRUE;
				}else if(power ==2 && ctrlvo!=null && ctrlvo.getIshasmake()!=null
						&& ctrlvo.getIshasmake().booleanValue()){//填制凭证
					isadd = DZFBoolean.TRUE;
				}
				if (isadd.booleanValue()) {
					resmap.put(cpvo.getPk_corp(), CodeUtils1.deCode(cpvo.getUnitname()));
				}
			}
	    }
	    	
		return resmap;
	}


	@Override
	public boolean isManageUserInCorp(String pk_corp, String pk_temp_corp, String account_id) throws DZFWarpException {
		AppCheckValidUtils.isEmptyWithCorp(pk_corp, pk_temp_corp, "");

		// 是否是管理员
		SQLParameter sp = new SQLParameter();
		String ismansql = null;
		if (!AppCheckValidUtils.isEmptyCorp(pk_corp)) {
			ismansql = "select 1 from ynt_corp_user where pk_corp = ?  and pk_user = ? and nvl(dr,0) =0  and ismanage  = 'Y' ";
			sp.addParam(pk_corp);
		} else {
			ismansql = "select 1 from ynt_corp_user where pk_tempcorp = ?  and pk_user = ? and nvl(dr,0) =0  and ismanage  = 'Y' ";
			sp.addParam(pk_temp_corp);
		}
		sp.addParam(account_id);

		boolean ismanage = singleObjectBO.isExists(pk_corp, ismansql, sp);

		return ismanage;
	}


	@Override
	public Integer qryParamValue(String pk_corp, String paramcode) throws DZFWarpException {
		
		if(AppCheckValidUtils.isEmptyCorp(pk_corp)){
			return  null;
		}

		YntParameterSet parameter = paramService.queryParamterbyCode(pk_corp, paramcode);
		if (parameter == null) {
			throw new BusinessException("参数" + paramcode + "不存在!");
		}

		Integer pardetailValue = parameter.getPardetailvalue();

		return pardetailValue;
	}


	@Override
	public boolean isExistUser(String account) throws DZFWarpException {
		StringBuffer checksql = new StringBuffer();
		checksql.append("select user_code from sm_user where user_code =? and nvl(dr,0)=0 ");
		checksql.append(" union all  ");
		checksql.append("select user_code from app_temp_user where user_code=? and nvl(dr,0)=0");
		SQLParameter sp = new SQLParameter();
		sp.addParam(account);
		sp.addParam(account);
		List<String> userlist = (List<String>) singleObjectBO.executeQuery(checksql.toString(), sp, new ColumnListProcessor());
		if (userlist != null && userlist.size() > 0) {
			return true;
		}else{
			return false;
		}
	}


	@Override
	public List<UserToCorp> getUserCorp(String account, String account_id) throws DZFWarpException {
		if (StringUtil.isEmpty(account) && StringUtil.isEmpty(account_id)) {
			throw new BusinessException("帐号不能为空!");
		}

		SQLParameter sp = new SQLParameter();
		StringBuffer qrysql = new StringBuffer();
		qrysql.append(" select b.* ");
		qrysql.append(" from ynt_corp_user b  ");
		if(!StringUtil.isEmpty(account)){
			qrysql.append(" inner join sm_user s on b.pk_user = s.cuserid ");
			qrysql.append(" where nvl(b.dr,0)=0 and nvl(s.dr,0) =0 ");
			qrysql.append(" and s.user_code = ?  ");
			sp.addParam(account);
		}else if(!StringUtil.isEmpty(account_id)){
			qrysql.append("  where b.pk_user = ? and nvl(b.dr,0)=0");
			sp.addParam(account_id);
		}

		List<UserToCorp> usercorplist = (List<UserToCorp>) singleObjectBO.executeQuery(qrysql.toString(), sp,
				new BeanListProcessor(UserToCorp.class));

		return usercorplist;
	}

	
	// 临时用户
	public List<TempUserRegVO> getTempList(String user_code)  throws DZFWarpException{
		SQLParameter sp = new SQLParameter();
		String appsql = "select * from app_temp_user where user_code=? and  nvl(isaccount,'N') ='N' ";
		sp.addParam(user_code);
		List<TempUserRegVO> templistapp = (List<TempUserRegVO>) singleObjectBO.executeQuery(appsql, sp,
				new BeanListProcessor(TempUserRegVO.class));

		return templistapp;
	}


	@Override
	public Map<String, String> getManageUserFromCorp(String[] pk_corps, String[] pk_temp_corps)
			throws DZFWarpException {
		Map<String, String> umap = new HashMap<String, String>();
		if ((pk_corps == null || pk_corps.length == 0) && (pk_temp_corps == null || pk_temp_corps.length == 0)) {
			return umap;
		}

		StringBuffer qrysql = new StringBuffer();
		qrysql.append(" select yu.*,su.user_name from ");
		qrysql.append(" ynt_corp_user yu ");
		qrysql.append(" left join sm_user  su on yu.pk_user = su.cuserid  ");
		qrysql.append(" where nvl(yu.dr,0)=0 and  nvl(yu.ismanage,'N') ='Y' ");
		if (pk_corps != null && pk_corps.length > 0) {
			qrysql.append(" and " + SqlUtil.buildSqlForIn("yu.pk_corp", pk_corps));
		}
		if (pk_temp_corps != null && pk_temp_corps.length > 0) {
			qrysql.append(" and " + SqlUtil.buildSqlForIn("yu.pk_tempcorp", pk_temp_corps));
		}
		List<UserToCorp> list = (List<UserToCorp>) singleObjectBO.executeQuery(qrysql.toString(), new SQLParameter(),
				new BeanListProcessor(UserToCorp.class));

		if (list!=null && list.size()>0) {
			for(UserToCorp vo:list){
				if(!AppCheckValidUtils.isEmptyCorp(vo.getPk_corp())){
					umap.put(vo.getPk_corp(), vo.getUsername());
				}
				if(!StringUtil.isEmpty(vo.getPk_tempcorp())){
					umap.put(vo.getPk_tempcorp(), vo.getUserName());
				}
			}
		}

		return umap;
	}
	
	
}
