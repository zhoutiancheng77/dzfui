package com.dzf.zxkj.app.service.app.act.impl;


import com.dzf.zxkj.app.model.resp.bean.DailyBean;
import com.dzf.zxkj.app.model.resp.bean.MxbBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ReportBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.rptbean.LrbBeanVO;
import com.dzf.zxkj.app.model.resp.rptbean.XjllbBeanVO;
import com.dzf.zxkj.app.model.resp.rptbean.ZcfzbBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.IVersionConstant;
import com.dzf.zxkj.app.service.app.act.IQryReportService;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.DataSourceFactory;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.workbench.BsWorkbenchVO;
import com.dzf.zxkj.report.service.IRemoteReportService;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 查询报表接口
 * @author liangjy
 *
 */
@Slf4j
@Service("orgreportService")
public class QryReportServiceImpl extends QryReportAbstract implements IQryReportService {

    @Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
    private IRemoteReportService iRemoteReportService;
    @Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
    private IZxkjRemoteAppService iZxkjRemoteAppService;
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	/**
	 * 日报
	 * @param qryDailyBean
	 */
	public ResponseBaseBeanVO qryDaily(ReportBeanVO qryDailyBean) throws DZFWarpException {
		
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO() ;
		
		 String userid = qryDailyBean.getAccount_id();
		 String pk_corp = qryDailyBean.getPk_corp();
		 String period = qryDailyBean.getPeriod();
		 DZFDate startdate = DateUtils.getPeriodStartDate(period);
		 DZFDate enddate = DateUtils.getPeriodEndDate(period);
		 
		 if(StringUtil.isEmptyWithTrim(userid)||StringUtil.isEmptyWithTrim(pk_corp)){
			 bean.setRescode(IConstant.FIRDES) ;
			 bean.setResmsg("用户和公司不能为空") ;
		 }else{
			 if(startdate != null && enddate != null && startdate.getYear()+startdate.getMonth() != enddate.getYear()+enddate.getMonth()){
				 bean.setRescode(IConstant.FIRDES) ;
				 bean.setResmsg("查询日期超范围，只能查询一个月内的数据!") ;
			 }else if(startdate != null && enddate != null && startdate.after(enddate)){
				 bean.setRescode(IConstant.FIRDES) ;
				 bean.setResmsg("开始日期不能晚于结束日期!") ;
			 }else{
				 try {
					 bean.setRescode(IConstant.DEFAULT) ;
					 List<DailyBean> res = getDialyData(pk_corp, startdate, enddate);
					 bean.setResmsg(res) ;
				 }catch (Exception e) {
					 bean.setRescode(IConstant.FIRDES) ;
					 bean.setResmsg("获取数据失败:"+e.getMessage()) ;
					 log.error(e.getMessage(),e);
				}
			 }
		 }
		  return bean;
	}
	/**
	 * 月报
	 * @param qryDailyBean
	 */
	public ResponseBaseBeanVO qryMonthRep(ReportBeanVO qryDailyBean) {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
		String userid = qryDailyBean.getAccount_id();
		String pk_corp = qryDailyBean.getPk_corp();
		String period = qryDailyBean.getPeriod();
		String periodend = StringUtil.isEmpty(qryDailyBean.getEndperiod()) ? qryDailyBean.getPeriod():qryDailyBean.getEndperiod();

		if (StringUtil.isEmptyWithTrim(userid) || StringUtil.isEmptyWithTrim(pk_corp)) {

			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg("参数不能为空");
		} else {
			try {
				bean.setRescode(IConstant.DEFAULT);
				bean.setResmsg(getMonthData(pk_corp, period, periodend,qryDailyBean.getCorpname(), qryDailyBean.getVersionno())
						.toArray());
			} catch (Exception e) {
				bean.setRescode(IConstant.FIRDES);
				bean.setResmsg("获取数据失败:" + e.getMessage());
				log.error(e.getMessage(),e);
			}
		}
		return bean;
	}

	/**
	 * 资产负债表
	 * @param
	 * @param
	 * @param ReportBeanVO
	 * @throws
	 * @throws
	 */
	public ResponseBaseBeanVO qryAssetsLiab(ReportBeanVO ReportBeanVO) {
		// 获取查询公司、查询期间
		String corpname = ReportBeanVO.getCorpname();
		String period = ReportBeanVO.getPeriod();


		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();

		if (StringUtil.isEmptyWithTrim(corpname)) {
			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg("公司参数不能为空");
		}
		if (StringUtil.isEmptyWithTrim(period)) {
			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg("期间参数不能为空");
		}

		if (bean.getRescode() == null) {
			// 根据公司名获取公司PK
			String pk_corp = ReportBeanVO.getPk_corp();

			CorpVO cpvo = iZxkjRemoteAppService.queryByPk(pk_corp);

			if(cpvo.getBegindate().after(DateUtils.getPeriodEndDate(period))){
				throw new BusinessException("查询期间在建账日期前!");
			}

			try {
				if (StringUtil.isEmptyWithTrim(pk_corp) == false) {
					// 调用资产负债表接口
					ZcFzBVO[] vos = iRemoteReportService.getZCFZBVOs(period, pk_corp, "N","N");
					// 调用资产负债表接口
					if (vos != null && vos.length > 0) {
						Integer corpschema = iZxkjRemoteAppService.getAccountSchema(pk_corp);
						Map<Float, ZcfzbBeanVO> map1 = new HashMap<Float, ZcfzbBeanVO>();
						SuperVO[] zvos = null;
						List<ZcfzbBeanVO> vec = new ArrayList<ZcfzbBeanVO>();
						float[][][] nr = doZCFZ(vos, corpschema, map1);
						ZcfzbBeanVO zvo = null;
						float index = -1;
						List ls = null;
						HashSet<Float> hs = new HashSet<Float>();
						for (float[][] fs : nr) {
							index = fs[0][0];
							for (float f : fs[1]) {
								if (map1.containsKey(f)) {
									hs.add(f);
									zvo = map1.get(index);
									zvos = zvo.getChildren();
									if (zvos == null) {
										zvos = new SuperVO[0];
									}
									zvos = ArrayUtil.arrayAdd(zvos, map1.get(f));
									zvo.setChildren(zvos);
								}
							}
						}
						Float[] fs = map1.keySet().toArray(new Float[0]);
						Arrays.sort(fs);
						vec = new ArrayList<ZcfzbBeanVO>();
						for (Float f : fs) {
							if (hs.contains(f) == false) {
								vec.add(map1.get(f));
							}
						}
						bean.setRescode(IConstant.DEFAULT);
						if (!vec.isEmpty()) {
							if(ReportBeanVO.getVersionno().intValue()>= IVersionConstant.VERSIONNO320.intValue()){
								handlerGroupZcfz(bean, vec);
							}else{
								bean.setResmsg(vec.toArray(new ZcfzbBeanVO[0]));
							}
						}
					} else {
						bean.setRescode(IConstant.FIRDES);
						bean.setResmsg("查询数据为空");
					}
				}
			} catch (Exception e) {
				bean.setRescode(IConstant.FIRDES);
				bean.setResmsg("查询失败：" + e.getMessage());
				log.error(e.getMessage(),e);
			}
		}

		return bean;
	}

    /**
     * 利润表
     * @param
     * @param
     * @param ReportBeanVO
     * @throws
     */
    public ResponseBaseBeanVO  qryProfits(ReportBeanVO ReportBeanVO) {

        String corpname = ReportBeanVO.getCorpname();
        String period = ReportBeanVO.getPeriod();

        ResponseBaseBeanVO bean = new ResponseBaseBeanVO() ;
        if(StringUtil.isEmptyWithTrim(corpname)){
            bean.setRescode(IConstant.FIRDES) ;
            bean.setResmsg("公司参数不能为空") ;
        }
        if(StringUtil.isEmptyWithTrim(period)){
            bean.setRescode(IConstant.FIRDES) ;
            bean.setResmsg("期间参数不能为空") ;
        }

        if (bean.getRescode() == null) {
            // 根据公司名获取公司PK
            String pk_corp = ReportBeanVO.getPk_corp();
            try {
                if (pk_corp != null) {

                    // 校验查询期间起不能早于公司开账日期
                    QueryParamVO paramVO = new QueryParamVO();
                    paramVO.setPk_corp(pk_corp);
                    paramVO.setIshasjz(DZFBoolean.FALSE);// 审核与记账条件统一
                    paramVO.setIshassh(DZFBoolean.FALSE);
                    paramVO.setQjq(period);
                    paramVO.setQjz(period);
                    // 调用利润表接口
                    LrbVO[] vos = iRemoteReportService.getLRBVOs(paramVO);

                    if (vos != null && vos.length > 0) {
                        Integer corpschema = iZxkjRemoteAppService.getAccountSchema(pk_corp);
                        Map<Float, LrbBeanVO> map1 = new HashMap<Float, LrbBeanVO>();
                        SuperVO[] zvos = null;
                        List<LrbBeanVO> vec = new ArrayList<LrbBeanVO>();
                        float[][][] nr = doProfits(vos, corpschema, map1);
                        if(nr == null){
                            bean.setRescode(IConstant.FIRDES);
                            bean.setResmsg("该行业没有对应的利润表数据!");
                            return bean;
                        }
                        LrbBeanVO zvo = null;
                        float index = -1;
                        List ls = null;
                        HashSet<Float> hs = new HashSet<Float>();
                        for (float[][] fs : nr) {
                            index = fs[0][0];
                            for (float f : fs[1]) {
                                if (map1.containsKey(f)) {
                                    hs.add(f);
                                    zvo = map1.get(index);
                                    zvos = zvo.getChildren();
                                    if (zvos == null) {
                                        zvos = new SuperVO[0];
                                    }
                                    zvos = ArrayUtil.arrayAdd(zvos, map1.get(f));
                                    zvo.setChildren(zvos);
                                }
                            }

                        }
                        Float[] fs = map1.keySet().toArray(new Float[0]);
                        Arrays.sort(fs);
                        vec = new ArrayList<LrbBeanVO>();
                        for (Float f : fs) {
                            if (hs.contains(f) == false) {
                                vec.add(map1.get(f));
                            }
                        }
                        bean.setRescode(IConstant.DEFAULT);
                        if (!vec.isEmpty()) {
                            bean.setResmsg(vec.toArray(new LrbBeanVO[0]));
                        }

                    } else {
                        bean.setRescode(IConstant.FIRDES);
                        bean.setResmsg("查询数据为空");
                    }
                }

            } catch (Exception e) {
                bean.setRescode(IConstant.FIRDES);
                bean.setResmsg("查询失败：" + e.getMessage());
                log.error(e.getMessage(),e);
            }
        }

        return bean;
    }

    /**
     * 现金流量
     * @param
     * @param
     * @param ReportBeanVO
     * @throws
     */
    public ResponseBaseBeanVO  qryCashFlow(ReportBeanVO ReportBeanVO){

        String corpname = ReportBeanVO.getCorpname();
        String period = ReportBeanVO.getPeriod();

        ResponseBaseBeanVO bean = new ResponseBaseBeanVO() ;
        if(StringUtil.isEmptyWithTrim(corpname)){
            bean.setRescode(IConstant.FIRDES) ;
            bean.setResmsg("公司参数不能为空") ;
        }else
        if(StringUtil.isEmptyWithTrim(period)){
            bean.setRescode(IConstant.FIRDES) ;
            bean.setResmsg("期间参数不能为空") ;
        }

        if(bean.getRescode()==null){
            //根据公司名获取公司PK
            String pk_corp = ReportBeanVO.getPk_corp() ;
            try {
                if(pk_corp!=null){
                    QueryParamVO qvo=new QueryParamVO();
                    qvo.setQjq(period);
                    qvo.setPk_corp(pk_corp);
                    //调用现金流量表接口
                    XjllbVO[] vos = iRemoteReportService.query(qvo) ;
                    if(vos!=null&&vos.length>0){
//						YntBoPubUtil ybu=(YntBoPubUtil) SpringUtils.getBean("yntBoPubUtil");
                        Integer corpschema = iZxkjRemoteAppService.getAccountSchema(pk_corp);

//						boolean is2007=ybu.is2007AccountSchema(pk_corp);
                        Map<Float, XjllbBeanVO> map1=new HashMap<Float, XjllbBeanVO>();
                        SuperVO[] zvos=null;
                        List<XjllbBeanVO> vec = new ArrayList<XjllbBeanVO>() ;
                        float[][][] nr = doCashFlow( vos, corpschema, map1);

                        if(nr == null){
                            bean.setRescode(IConstant.FIRDES);
                            bean.setResmsg("该行业没有对应的利润表数据!");
                            return bean;
                        }

                        XjllbBeanVO zvo=null;
                        float index=-1;
                        List ls=null;
                        HashSet<Float> hs=new HashSet<Float>();
                        for (float[][] fs : nr) {
                            index=fs[0][0];
                            for (float f : fs[1]) {
                                if(map1.containsKey(f)){
                                    hs.add(f);
                                    zvo=map1.get(index);
                                    zvos=zvo.getChildren();
                                    if(zvos==null){
                                        zvos=new SuperVO[0];
                                    }
                                    zvos=ArrayUtil.arrayAdd(zvos, map1.get(f));
                                    zvo.setChildren(zvos);
                                }
                            }

                        }
                        Float[] fs=map1.keySet().toArray(new Float[0]);
                        Arrays.sort(fs);
                        vec = new ArrayList<XjllbBeanVO>() ;
                        for (Float f : fs) {
                            if(hs.contains(f)==false){
                                vec.add(map1.get(f));
                            }
                        }

                        bean.setRescode(IConstant.DEFAULT) ;
                        if(!vec.isEmpty()){
                            bean.setResmsg(vec.toArray(new XjllbBeanVO[vec.size()]));
                        }

                    }else{
                        bean.setRescode(IConstant.FIRDES) ;
                        bean.setResmsg("查询数据为空") ;
                    }
                }

            } catch (Exception e) {
                bean.setRescode(IConstant.FIRDES) ;
                bean.setResmsg("查询现金流量表数据失败："+e.getMessage()) ;
                log.error(e.getMessage(),e);
            }
        }

        return bean;
    }

    /**
     * 查询明细账
     */
    public ResponseBaseBeanVO  qryDetailReport(ReportBeanVO ReportBeanVO) throws DZFWarpException{

        //获取查询公司、查询期间
        String corpname = ReportBeanVO.getCorpname();
        String beginperiod = ReportBeanVO.getBeginperiod();
        String endperiod = ReportBeanVO.getEndperiod();

        //科目属性
        String kmsx = ReportBeanVO.getKmsx();

        ResponseBaseBeanVO bean = new ResponseBaseBeanVO() ;
        if(StringUtil.isEmptyWithTrim(corpname)){
            bean.setRescode(IConstant.FIRDES) ;
            bean.setResmsg("公司参数不能为空") ;
        }
        if(StringUtil.isEmptyWithTrim(beginperiod)){
            bean.setRescode(IConstant.FIRDES) ;
            bean.setResmsg("期间起参数不能为空") ;
        }
        if(StringUtil.isEmptyWithTrim(endperiod)){
            bean.setRescode(IConstant.FIRDES) ;
            bean.setResmsg("期间至参数不能为空") ;
        }
        if(StringUtil.isEmptyWithTrim(kmsx)){
            bean.setRescode(IConstant.FIRDES) ;
            bean.setResmsg("科目属性不能为空") ;

        }

        if(bean.getRescode()==null){
            //根据公司名获取公司PK
            String pk_corp = ReportBeanVO.getPk_corp() ;
            try {
                if(pk_corp!=null){
                    //调用明细表接口
                    DZFDate begindate = DZFDate.getDate(beginperiod+"-01") ;
                    DZFDate enddate = DZFDate.getDate(endperiod+"-01") ;

                    QueryParamVO vo1=new QueryParamVO();
                    vo1.setPk_corp(pk_corp);
                    vo1.setKms("");
                    vo1.setKmsx(kmsx);
                    vo1.setBegindate1(begindate);
                    vo1.setEnddate(enddate);
                    vo1.setXswyewfs(DZFBoolean.FALSE);
                    vo1.setXsyljfs(DZFBoolean.FALSE);
                    vo1.setIshasjz(DZFBoolean.FALSE);
                    vo1.setIshassh(DZFBoolean.FALSE);
                    KmMxZVO[] vos =iRemoteReportService.getKMMXZVOs(vo1,null);//
                    Vector<MxbBeanVO> vec = new Vector<MxbBeanVO>() ;
                    if(vos!=null&&vos.length>0){
                        for(KmMxZVO vo:vos){
                            if((vo.getZy()!=null && vo.getZy().equals("期初余额"))||(vo.getJf()!=null&&vo.getJf().doubleValue()!=0)||(vo.getDf()!=null&&vo.getDf().doubleValue()!=0)||(vo.getYe()!=null&&vo.getYe().doubleValue()!=0)){
                                MxbBeanVO mxb = new MxbBeanVO() ;
                                mxb.setKm(vo.getKm()) ;
                                mxb.setRq(vo.getRq()) ;
                                mxb.setJfmny(vo.getJf()) ;
                                mxb.setDfmny(vo.getDf()) ;
                                mxb.setYe(vo.getYe()) ;
                                mxb.setZy(vo.getZy()) ;
                                mxb.setFx(vo.getFx()) ;
                                vec.add(mxb) ;
                            }
                        }
                        bean.setRescode(IConstant.DEFAULT) ;
                        if(!vec.isEmpty()){
                            bean.setResmsg(vec.toArray(new MxbBeanVO[vec.size()]));
                        }
                    }else{
                        bean.setRescode(IConstant.FIRDES) ;
                        bean.setResmsg("查询数据为空") ;
                    }
                }

            } catch (Exception e) {
                bean.setRescode(IConstant.FIRDES) ;
                bean.setResmsg("查询明细表数据失败："+e.getMessage()) ;
                log.error(e.getMessage(),e);
            }
        }
        return bean;
    }

    @Override
    public ResponseBaseBeanVO expendption(ReportBeanVO reportBean)
            throws DZFWarpException {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO() ;
        String userid = reportBean.getAccount_id();
        String pk_corp = reportBean.getPk_corp();
        String period = reportBean.getPeriod();

        if(StringUtil.isEmptyWithTrim(userid)||StringUtil.isEmptyWithTrim(pk_corp)){

            bean.setRescode(IConstant.FIRDES) ;
            bean.setResmsg("参数不能为空") ;
        }else{
            try{

                ExpendpTionVO[]  d =iRemoteReportService.getAppSjMny(period, pk_corp, "");
                DZFBoolean isallzero = DZFBoolean.TRUE;
                for(ExpendpTionVO vo:d){
                    if(vo.getValue() == null){
                        vo.setValue("0");
                    }
                    DZFDouble valuemny = new DZFDouble(vo.getValue());
                    if(vo.getValue()!=null &&  valuemny.doubleValue()!=0){
                        isallzero = DZFBoolean.FALSE;
                    }
                }

                if(isallzero.booleanValue()){
                    bean.setRescode(IConstant.FIRDES) ;
                    bean.setResmsg("该月没有数据!");
                }else{
                    bean.setRescode(IConstant.DEFAULT) ;
                    bean.setResmsg(d) ;
                }
            } catch (Exception e) {
                bean.setRescode(IConstant.FIRDES) ;
                bean.setResmsg(e.getMessage()) ;
                log.error(e.getMessage(),e);
            }
        }
        return bean;
    }

    @Override
    public ResponseBaseBeanVO profitgrow(ReportBeanVO reportBean)
            throws DZFWarpException {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO() ;
        String userid = reportBean.getAccount_id();
        String pk_corp = reportBean.getPk_corp();
        String period = reportBean.getPeriod();

        if(StringUtil.isEmptyWithTrim(userid)||StringUtil.isEmptyWithTrim(pk_corp)){

            bean.setRescode(IConstant.FIRDES) ;
            bean.setResmsg("参数不能为空") ;
        }else{
            try{
                Integer corpschema = iZxkjRemoteAppService.getAccountSchema(pk_corp);

                DZFDouble[] d =iRemoteReportService.getAppNetProfit(period.substring(0, 4), pk_corp);

                DZFBoolean iszero = DZFBoolean.TRUE;

                for(DZFDouble value:d){
                    if(value!=null && value.doubleValue()!=0){
                        iszero=DZFBoolean.FALSE;
                    }
                }

                if(corpschema != DzfUtil.SEVENSCHEMA.intValue() && corpschema != DzfUtil.THIRTEENSCHEMA.intValue()){
                    bean.setRescode(IConstant.FIRDES) ;
                    bean.setResmsg("暂不支持该行业的公司!") ;
                    return bean;
                }

                if(iszero.booleanValue()){
                    bean.setRescode(IConstant.FIRDES) ;
                    bean.setResmsg("该公司没有数据!") ;
                    return bean;
                }

                String [] rs = new String[15];
                //求最大绝对值
                DZFDouble max = new DZFDouble(0);
                DZFDouble comp=new DZFDouble(0);
                for(DZFDouble n:d){
                    comp=new DZFDouble(Math.abs(n.doubleValue()));
                    if(max.compareTo(comp)<0){
                        max =comp;
                    }
                }

                Integer maxint = new Integer(max.intValue());
                int size =maxint.toString().length();//最大数值整数段长度
                String dw="";
                if(size>2){
                    maxint = new Integer(maxint.toString().substring(0, 2));
                    dw=dws[size-3]+dw;
                }

                Double dwkd = Math.ceil(new DZFDouble(maxint).div(new DZFDouble("0.8")).div(new DZFDouble("5")).doubleValue());//每5个单位刻度值
                Integer zdkdz = new DZFDouble(dwkd).multiply(new DZFDouble("5")).intValue();//最大刻度值

                //转化标准刻度值
                if(size>2){
                    for(int i=0;i<d.length;i++){
                        if(d[i].compareTo(DZFDouble.ZERO_DBL)==0){
                            rs[i]="0.00";
                        }else{
                            rs[i]=d[i].div(new DZFDouble(Math.pow(10,size-2)),2).toString();
                        }
                    }
                }else{
                    for(int i=0;i<d.length;i++){
                        if(d[i].compareTo(DZFDouble.ZERO_DBL)==0){
                            rs[i]="0.00";
                        }else{
                            String v = d[i].toString();
                            v=v.substring(v.indexOf("."), v.indexOf(".")+2);
                            rs[i]=v;
                        }
                    }
                }
                rs[12]=zdkdz.toString();
                rs[13]=dwkd.toString();
                rs[14]=dw;
                bean.setRescode(IConstant.DEFAULT) ;
                bean.setResmsg(rs) ;

            } catch (Exception e) {
                bean.setRescode(IConstant.FIRDES) ;
                bean.setResmsg(e.getMessage()) ;
                log.error(e.getMessage(),e);
            }
        }
        return bean;
    }
	 private String dws[] = new String[] {"拾","佰", "仟", "万", "拾万", "佰万", "仟万", "亿" };
	private float[][][] doCashFlow(XjllbVO[] vos,
			Integer corpschema, Map<Float, XjllbBeanVO> map) throws NumberFormatException,
		DZFWarpException {


		XjllbBeanVO xjllb = null;

		float[][][] nr=null;
		if(vos!=null&&vos.length>0){
			//资产类
			for(XjllbVO vo:vos){
				 xjllb = new XjllbBeanVO() ;
				xjllb.setProjectname(vo.getXm()) ;
				xjllb.setBqmny(vo.getBqje());
				xjllb.setBnmny(vo.getSqje()) ;

				map.put(vo.getRowno(),xjllb);


			}

		if(corpschema == DzfUtil.SEVENSCHEMA.intValue()){
			nr=new float[][][]{
				{{1},{2,3,4,5,6,7,8,9,10,11}},
				{{12},{13,14,15,16,17,18,19,20,21,22,23,24}},
				{{25},{26,27,28,29,30,31,32,33,34}},
				{{35},{}},{{36},{37}},{{38},{}}
			};
		}else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()){
			nr=new float[][][]{
					{{0.5f},{1,2,3,4,5,6,7}},
					{{7.5f},{8,9,10,11,12,13}},
					{{13.5f},{14,15,16,17,18,19}},
					{{20},{}},{{21},{}},{{22},{}}};
		}

		}
		return nr;
	}

	private float[][][] doProfits(LrbVO[] vos, Integer corpschema, Map<Float, LrbBeanVO> map) throws NumberFormatException, DZFWarpException {
		LrbBeanVO lrb = null;

		float[][][] nr=null;
		if(vos!=null&&vos.length>0){
			//资产类
			for(LrbVO vo:vos){
				lrb = new LrbBeanVO() ;
				lrb.setProjectname(vo.getXm()) ;
				lrb.setBqmny(vo.getByje());
				lrb.setBnmny(vo.getBnljje()) ;
				map.put(StringUtil.isEmptyWithTrim(vo.getHs())?-1:Float.parseFloat(vo.getHs()),lrb);
			}

		if(corpschema == DzfUtil.SEVENSCHEMA.intValue()){
			nr=new float[][][]{
					{{1},{2,3,4,5,6,7,8,9,10},{}},
					{{11},{12,13,14},{}},
					{{15},{16},{}},
					{{17},{},{}},{{18},{19,20},{}}
			};
		}else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()){
			nr=new float[][][]{
					{{1},{},{}},
					{{2},{},{}},
					{{3},{4,5,6,7,8,9,10},{}},
					{{11},{12,13},{}},
					{{14},{15,16,17},{}},
					{{18},{19},{}},
					{{20},{},{}},
					{{21},{},{}},
					{{22},{23},{}},
					{{24},{25,26,27,28,29},{}},
					{{30},{},{}},
					{{31},{},{}},
					{{32},{},{}},
					{{33},{},{}},
		};
		}
	}
		return nr;
	}


	private void handlerGroupZcfz(ResponseBaseBeanVO bean,List<ZcfzbBeanVO> vec){

		List<ZcfzbBeanVO> zcvec = new ArrayList<ZcfzbBeanVO>();
		List<ZcfzbBeanVO> fzvec = new ArrayList<ZcfzbBeanVO>();

		DZFBoolean isfzstart = DZFBoolean.FALSE;
		for(ZcfzbBeanVO beanvo:vec){
			if(!StringUtil.isEmpty(beanvo.getProjectname()) && beanvo.getProjectname().equals("流动负债：")){
				isfzstart = DZFBoolean.TRUE;
			}
			if(isfzstart.booleanValue()){
				fzvec.add(beanvo);
			}else{
				zcvec.add(beanvo);
			}
		}
		Map<String,List<ZcfzbBeanVO>> resmap = new HashMap<String,List<ZcfzbBeanVO>>();
		resmap.put("zcresmsg", zcvec);
		resmap.put("fzresmsg", fzvec);
		bean.setResmsg(resmap);
	}
	/**
	 * 获取日报数据
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws Exception
	 */
	private List<DailyBean> getDialyData(String pk_corp,DZFDate startdate,DZFDate enddate) throws DZFWarpException{

		if(StringUtil.isEmpty(pk_corp)){
			throw new BusinessException("公司信息为空");
		}
		if(startdate == null ||  enddate == null){
			throw new BusinessException("查询区间为空");
		}

		/**上传图片，凭证，报表
		 * key 日期
		 * value 内容
		 * */
		HashMap<String, Integer> picHs = qryPics(pk_corp, startdate, enddate);
		HashMap<String, Integer> vouHs = qryVouchers(pk_corp, startdate, enddate);
		HashMap<String, DailyBean> repHs = qryCashBankRep(pk_corp, startdate, enddate);

		List<DailyBean> retBeanLs = new ArrayList<DailyBean>();
		int i = 0;
		int days = enddate.getDaysAfter(startdate);

		 do {
			String date = startdate.getDateAfter(i).toString();
			DailyBean dialBean = repHs.get(date);
			Integer pics = picHs.get(date);
			Integer vouch =vouHs.get(date);
			if(dialBean!=null){
				dialBean.setBillsl(pics==null?0:pics);
				dialBean.setAccountbillsl(vouch==null?0:vouch);
				DZFDouble cashcreditmny = dialBean.getCashcreditmny() ==null?DZFDouble.ZERO_DBL:dialBean.getCashcreditmny();
				DZFDouble cashdebitmny = dialBean.getCashdebitmny() == null?DZFDouble.ZERO_DBL:dialBean.getCashdebitmny();
				dialBean.setHzcashmny(cashcreditmny.setScale(2, DZFDouble.ROUND_HALF_UP).toString()+","+cashdebitmny.setScale(2, DZFDouble.ROUND_HALF_UP).toString());

				DZFDouble bankcreditmny =  dialBean.getBankcreditmny() == null ? DZFDouble.ZERO_DBL:dialBean.getBankcreditmny();
				DZFDouble bankdebitmny =  dialBean.getBankdebitmny() == null ? DZFDouble.ZERO_DBL:dialBean.getBankdebitmny();
				dialBean.setHzbankmny(bankcreditmny.setScale(2, DZFDouble.ROUND_HALF_UP).toString()+","+bankdebitmny.setScale(2, DZFDouble.ROUND_HALF_UP).toString());

				if(dialBean.getBillsl().intValue() !=0
						|| dialBean.getAccountbillsl().intValue() !=0
						|| cashcreditmny.doubleValue()!=0
						|| cashdebitmny.doubleValue() !=0
						|| bankcreditmny.doubleValue() !=0
						|| bankdebitmny.doubleValue() !=0
						){
//					continue;
					retBeanLs.add(dialBean);
				}
			}
			i++;
		} while (i<=days);


		return retBeanLs;
	}

	private float[][][] doZCFZ(ZcFzBVO[] vos, Integer corpschema, Map<Float, ZcfzbBeanVO> map) throws NumberFormatException,
		DZFWarpException {
		float nhc1=0;
		float nhc2=0;
		String zcmx = null;
		String fzmx = null;
		ZcfzbBeanVO zcfzb1 = null;
		ZcfzbBeanVO zcfzb2 = null;
		float[][][] nr=null;
		String str=null;
		if (vos != null && vos.length > 0) {
			// 资产类
			for (ZcFzBVO vo : vos) {
				zcfzb1 = new ZcfzbBeanVO();
				str = vo.getZc();
				if (StringUtil.isEmpty(str) == false)
					str = str.trim();
				zcfzb1.setProjectname(str);
				zcfzb1.setQmmny(vo.getQmye1());
				zcfzb1.setNcmny(vo.getNcye1());
				nhc1 = StringUtil.isEmptyWithTrim(vo.getHc1()) ? -1 : Float.parseFloat(vo.getHc1());
				zcmx = vo.getZc();
				fzmx = vo.getFzhsyzqy();

				zcfzb2 = new ZcfzbBeanVO();
				str = vo.getFzhsyzqy();
				if (StringUtil.isEmpty(str) == false)
					str = str.trim();
				zcfzb2.setProjectname(str);
				zcfzb2.setQmmny(vo.getQmye2());
				zcfzb2.setNcmny(vo.getNcye2());
				nhc2 = StringUtil.isEmptyWithTrim(vo.getHc2()) ? -1 : Float.parseFloat(vo.getHc2());
				if(corpschema == DzfUtil.SEVENSCHEMA.intValue()){
					if (nhc1 < 1 && nhc2 < 1 ) {
						nhc1 = 0.5f;
						nhc2 = 33.5f;
					}else if(nhc1 == -1 && nhc2 == 48){
						nhc1 = 14.5f;
					} else if(nhc1 == 15 && nhc2 == -1){
						nhc2 = 48.5f;
					}  else if (nhc1 == 28 && nhc2 == -1) {
						nhc2 = 60.5f;
					}
				}else if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()){//13
					if (nhc1 < 1 && nhc2 < 1) {
						nhc1 = 0.5f;
						nhc2 = 30.5f;
					} else if (nhc1 == 12 && nhc2 == -1) {
						nhc2 = 41.5f;
					} else if (nhc1 == 24 && nhc2 == -1) {
						nhc2 = 47.5f;
					} else if (nhc1 == -1 && nhc2 == 45) {
						nhc1 = 15.5f;
					}
				}else if(corpschema == DzfUtil.POPULARSCHEMA.intValue()){//民间
					if (nhc1 < 1 && nhc2 < 1 && !StringUtil.isEmpty(zcmx) && !StringUtil.isEmpty(fzmx) && zcmx.equals("流动资产：") ) {
						nhc1 = 0.5f;
						nhc2 = 60.5f;
					} else if (nhc1 == -1 && nhc2 == -1 && zcmx.equals("长期投资：") && !StringUtil.isEmpty(zcmx) && StringUtil.isEmpty(fzmx)) {
						nhc1 = 20.5f;
					} else if (nhc1 == 21 && nhc2 == -1) {
						nhc2 = 80.5f;
					}else if (nhc1 == 32 && nhc2 == -1) {
						nhc2 = 90.5f;
					}else if (nhc1 == 35 && nhc2 == 100) {
						nhc2 = 99.5f;
					}else if (nhc1 == -1 && nhc2 == 90) {
						nhc1 = 30.5f;
					}else if (nhc1 == -1 && nhc2 == -1 && !StringUtil.isEmpty(zcmx) && zcmx.equals("无形资产：")) {
						nhc1 = 40.5f;
					}else if (nhc1 == 41 && nhc2 == -1 ) {
						nhc2 = 100.5f;
					}else if (nhc1 == 51 && nhc2 == 110) {
						nhc2 = 109.5f;
					}
				}else if(corpschema == DzfUtil.CAUSESCHEMA.intValue()){//事业
					if (nhc1 < 1 && nhc2 < 1) {
						nhc1 = 0.5f;
						nhc2 = 30.5f;
					} else if (nhc1 == 12 && nhc2 == -1) {
						nhc2 = 41.5f;
					}
				}
				zcfzb1.setRowno(nhc1);
				if(nhc1>0){
					map.put(nhc1, zcfzb1);
				}
				zcfzb2.setRowno(nhc2);
				if (nhc2 > 0)
					map.put(nhc2, zcfzb2);

			}

			if(corpschema == DzfUtil.SEVENSCHEMA.intValue()){
				nr=new float[][][]{
					{{0.5f},{1,2,3,4,5,6,7,8,9,10,11,12,13}},
//					{{14},{}},
					{{14.5f},{15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31}},
//					{{32},{}},
//					{{33},{}},
					{{33.5f},{34,35,36,37,38,39,40,41,42,43,44,45,46,47}},
//					{{48},{}},
					{{48.5f},{49,50,51,52,53,54,55,56,57,58}},
//					{{59},{}},
//					{{60},{}},
					{{60.5f},{61,62,63,64,65,66,67,68,69}},
//					{{70},{}},
//					{{71},{}},
				};
			}else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()){//13
				nr=new float[][][]{
						{{0.5f},{1,2,3,4,5,6,7,8,9,14}},
						{{9},{10,11,12,13}},
						{{15},{}},{{15.5f},{16,17,18,19,20,21,22,23,24,25,26,27,28}},
						{{29},{}},{{30},{}},
						{{30.5f},{31,32,33,34,35,36,37,38,39,40}},
						{{41},{}},{{41.5f},{42,43,44,45}},
						{{46},{}},{{47},{}},{{47.5f},{48,49,50,51}},{{52},{}},
						{{53},{}}};
			}else if(corpschema == DzfUtil.POPULARSCHEMA.intValue()){//民间
				nr=new float[][][]{
					{{0.5f},{1,2,3,4,8,9,15,18}},
					{{20},{}},
					{{20.5f},{21,24}},
					{{30},{}},
					{{30.5f},{31,32,33,34,35,38,}},
					{{40},{}},
					{{40.5f},{41}},
					{{51},{}},
					{{60},{}},
					{{60.5f},{61,62,63,65,66,71,72,74}},
					{{78},{}},
					{{80},{}},
					{{80.5f},{81,84,88}},
					{{90},{}},
					{{90.5f},{91}},
					{{99.5f},{100}},
					{{100.5f},{101,105}},
					{{109.5f},{110}},
					{{120},{}},
					};
			}else if(corpschema == DzfUtil.CAUSESCHEMA.intValue()){//事业单位
				nr=new float[][][]{
					{{0.5f},{1,2,3,4,5,6,7,8,9}},
					{{10},{}},
					{{11},{12,13,14,15,16,17,18,19,20}},
					{{21},{}},
					{{29},{}},
					{{30.5f},{31,32,33,34,35,36,37,38,39,40}},
					{{41},{}},
					{{41.5f},{42,43}},
					{{44},{}},
					{{46},{}},
					{{47},{48,49,50,51,52,53,54,55,56}},
					{{58},{}},
					{{59},{}},
					};
			}

		}
		return nr;
	}
    	/**
	 * 获取日报数据
	 * @param pk_corp
	 * @param period
	 * @param periodend
	 * @return
	 * @throws DZFWarpException
	 */
	private List getMonthData(String pk_corp,String period,String periodend,String corpname,Integer versionno) throws DZFWarpException{


		DZFDate startdate = DateUtils.getPeriodStartDate(period);
		DZFDate enddate = DateUtils.getPeriodEndDate(periodend);

		List vl = new ArrayList();
		Map<String,Map> valuemap = new HashMap<String,Map>();
		String[][] values =new String[4][];
		//单据凭证
		Map<String,Object> djzjmap = new HashMap<String,Object>();
		if(versionno.intValue()<IVersionConstant.VERSIONNO320){
			djzjmap.put("djpz", "单据凭证");
			djzjmap.put("scdj", "上传单据:  "+qryMonthPics(pk_corp, startdate, enddate)+"张");
			djzjmap.put("kjpz", "会计凭证:  "+qryMonthVouchers(pk_corp, startdate, enddate)+"张");
		} else {
			djzjmap.put("djpz", "单据凭证");
			djzjmap.put("scdj", "" + qryMonthPics(pk_corp, startdate, enddate) + "张");
			djzjmap.put("kjpz", "" + qryMonthVouchers(pk_corp, startdate, enddate) + "张");

			putNsZt(pk_corp, period, "isptx", "spzt", djzjmap);// 收票状态

			putNsZt(pk_corp, period, "taxstatecopy", "cszt", djzjmap);// 抄税状态

			putNsZt(pk_corp, period, "taxstateclean", "qkzt", djzjmap);// 清卡状态

			putNsZt(pk_corp, period, "ipzjjzt", "pzjj", djzjmap);// 凭证交接状态
		}
		vl.add(djzjmap);
		//资产负债状况
		values[1]=new String[8];
		values[1][0]="资产负债状况";
		Map<String,String> zcfzmap = new HashMap<String,String>();
		zcfzmap.put("zcfz", "资产负债状况");
		zcfzmap = getZcFz(zcfzmap,period, pk_corp,versionno);
		zcfzmap = getXjYh(zcfzmap,pk_corp, startdate, enddate,versionno);
		vl.add(zcfzmap);

		//经营成果
		values[2]=new String[5];
		values[2][0]="经营成果";

		DZFDouble qysds = DZFDouble.ZERO_DBL;
		Map<String,String> jycgmap = new HashMap<String,String>();
		jycgmap.put("jycg", "经营成果");
		if(!period.equals(periodend)){
			jycgmap=getJdFyzc(jycgmap,pk_corp, periodend,qysds,versionno);
		}else{
			jycgmap=getFyzc(jycgmap,pk_corp, period,qysds,versionno);
		}
		vl.add(jycgmap);

		//税金缴纳
		values[3]=new String[8];
		values[3][0]="缴纳税金";

		Map<String,String> sjjnmap = new HashMap<String,String>();
		sjjnmap.put("jjsj", "缴纳税金");
		sjjnmap = getSf(sjjnmap,pk_corp, startdate, enddate,versionno);
		valuemap.put("sjjn", sjjnmap);
		vl.add(sjjnmap);
		return vl;
	}
	/**
	 * 查询上传图片数量
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	private  HashMap<String, Integer> qryPics(String pk_corp,DZFDate startdate,DZFDate enddate) throws DZFWarpException{
		 HashMap<String, Integer> dtHs = new  HashMap<String, Integer>();
		 String picSql = "select count(pk_image_library) dr,gr.doperatedate doperatedate from ynt_image_group gr"
				 +" inner join ynt_image_library lb on gr.pk_image_group=lb.pk_image_group"
				 +" where gr.pk_corp = ? AND gr.doperatedate between ? AND ?"
				 +" and (nvl(gr.dr, 0) = 0) group by gr.doperatedate";
			SQLParameter sp=new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(startdate);
			sp.addParam(enddate);
		 ArrayList<ImageGroupVO> al = (ArrayList<ImageGroupVO>)singleObjectBO.executeQuery(picSql, sp,new BeanListProcessor(ImageGroupVO.class));
		 if(al!= null&& al.size()>0){
			 for(int i = 0 ; i < al.size() ; i++){
				 dtHs.put(al.get(i).getDoperatedate().toString(), al.get(i).getDr());
			 }
		 }
		 return dtHs;
	}
	/**
	 * 汇总月上传图片数量
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	private int qryMonthPics(String pk_corp,DZFDate startdate,DZFDate enddate) throws DZFWarpException{
		int monthPics = 0;
		 String picSql = "select count(pk_image_library) dr from ynt_image_group gr"
				 +" inner join ynt_image_library lb on gr.pk_image_group=lb.pk_image_group"
				 +" where gr.pk_corp = ? AND gr.doperatedate between ? AND ?"
				 +" and (nvl(gr.dr, 0) = 0) ";
		 SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null, pk_corp));
			SQLParameter sp=new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(startdate);
			sp.addParam(enddate);
		 ArrayList al = (ArrayList) sbo.executeQuery(picSql,sp, new ArrayListProcessor());
		 if(al!= null&& al.size()>0){
			 monthPics = Integer.parseInt(((Object[])al.get(0))[0].toString());
		 }
		 return monthPics;
	}
	/**
	 * 税费
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String,String>  getSf(Map<String,String> sjjnmap,String pk_corp,DZFDate startdate,DZFDate enddate,Integer versionno) throws DZFWarpException{
		String msg = "";
		QueryParamVO sfQryVO=new QueryParamVO();
		sfQryVO.setPk_corp(pk_corp);
		sfQryVO.setBegindate1(startdate);
		sfQryVO.setEnddate(enddate);
		sfQryVO.setXswyewfs(DZFBoolean.FALSE);
		sfQryVO.setXsyljfs(DZFBoolean.FALSE);
		sfQryVO.setIshasjz(DZFBoolean.FALSE);
		sfQryVO.setIshassh(DZFBoolean.TRUE);
//		sfQryVO.setKms("2221,");
		sfQryVO.setKms_first("2221");
		sfQryVO.setKms_last("2221");
//		ArrayList<String> codelist = new ArrayList<String>();
//		codelist.add("2221");
//		sfQryVO.setKmcodelist(codelist);
		KmMxZVO[] mkmxVOs = iRemoteReportService.getKMMXZVOs(sfQryVO,null);
		if(mkmxVOs != null){
			msg = msg+"\n税金交纳：";
			//增值税
			DZFDouble zzs = DZFDouble.ZERO_DBL;
			//营业税
			DZFDouble yys = DZFDouble.ZERO_DBL;
			//城建税
			DZFDouble cjs = DZFDouble.ZERO_DBL;
			//教育费附加
			DZFDouble jyfjs = DZFDouble.ZERO_DBL;
			//地方教育费附加
			DZFDouble dfjyfjs = DZFDouble.ZERO_DBL;
			//企业应交所得税
			DZFDouble qyyjsds = DZFDouble.ZERO_DBL;
			for(KmMxZVO kmVO : mkmxVOs ){
				if("应交税费".equals(kmVO.getKm())&&"本月合计".equals(kmVO.getZy())){
					if(versionno.intValue()<IVersionConstant.VERSIONNO320){
						sjjnmap.put("sjsj", "上交税金:  "+Common.format(kmVO.getJf()));
					}else{
						sjjnmap.put("sjsj", ""+ Common.format(kmVO.getJf()));
					}
				}else if(kmVO.getKmbm().contains("222107")&&"本月合计".equals(kmVO.getZy())){//营业税:
					yys = kmVO.getDf();
				}else if(kmVO.getKmbm().contains("222102")&&"本月合计".equals(kmVO.getZy())){//应交城建税
					cjs = kmVO.getDf();
				}else if(kmVO.getKmbm().contains("222103")&&"本月合计".equals(kmVO.getZy())){//应交教育费附加
					jyfjs = kmVO.getDf();
				}else if(kmVO.getKmbm().contains("222104")&&"本月合计".equals(kmVO.getZy())){//应交地方教育费附加
					dfjyfjs = kmVO.getDf();
				}else if(kmVO.getKmbm().contains("22210102")&&"本月合计".equals(kmVO.getZy())){//增值税
					zzs = zzs.add(getDzfDouble(kmVO.getDf()));
				}else if(kmVO.getKm().contains("应交企业所得税")&&"本月合计".equals(kmVO.getZy())){
					qyyjsds = kmVO.getDf();
				}else if(kmVO.getKmbm().equals("22210101")&&"本月合计".equals(kmVO.getZy())){
					zzs = zzs.sub(getDzfDouble(kmVO.getJf()));
				}else if(kmVO.getKmbm().equals("22210104")&&"本月合计".equals(kmVO.getZy())){
					zzs = zzs.add(getDzfDouble(kmVO.getDf()));
				}
			}
			if(versionno.intValue()<IVersionConstant.VERSIONNO320){
				sjjnmap.put("zzs", "增值税:  "+Common.format(zzs));
				sjjnmap.put("cjs", "营业税:  "+Common.format(cjs));
				sjjnmap.put("yys", "城建税:  "+Common.format(yys));
				sjjnmap.put("jyfj","教育费附加:  "+Common.format(jyfjs));
				sjjnmap.put("dfjyfj", "地方教育费附加:  "+Common.format(dfjyfjs));
				sjjnmap.put("qysds", "企业所得税:  "+Common.format(qyyjsds));
			}else{
				sjjnmap.put("zzs", ""+Common.format(zzs));
				sjjnmap.put("cjs", ""+Common.format(cjs));
				sjjnmap.put("yys", ""+Common.format(yys));
				sjjnmap.put("jyfj"," "+Common.format(jyfjs));
				sjjnmap.put("dfjyfj", ""+Common.format(dfjyfjs));
				sjjnmap.put("qysds", ""+Common.format(qyyjsds));
			}


//			msg = msg+"增值税"+Common.format(zzs)+"元，营业税"+Common.format(yys)+"元，城建税"+Common.format(cjs)+"元，教育费附加"+Common.format(jyfjs)+"元，地方教育费附加"+Common.format(dfjyfjs)+"元；";
		}
		return sjjnmap;
	}
	/**
	 * 查询凭证数量
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	private HashMap<String, Integer> qryVouchers(String pk_corp,DZFDate startdate,DZFDate enddate) throws DZFWarpException{

		 HashMap<String, Integer> dtHs = new  HashMap<String, Integer>();
		 String vouSql = "select count(h.pk_tzpz_h) dr,h.doperatedate doperatedate from ynt_tzpz_h h"
				 +" left join ynt_image_group g on h.pk_image_group=g.pk_image_group"
				 +" where h.pk_corp = ? AND h.doperatedate between ? AND ? and (nvl(h.dr,0)=0)  group by h.doperatedate";
			SQLParameter sp=new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(startdate);
			sp.addParam(enddate);
		 ArrayList<ImageGroupVO> al = (ArrayList<ImageGroupVO>)singleObjectBO.executeQuery(vouSql, sp,new BeanListProcessor(ImageGroupVO.class));
		 if(al!= null&& al.size()>0){
			 for(int i = 0 ; i < al.size() ; i++){
				 dtHs.put(al.get(i).getDoperatedate().toString(), al.get(i).getDr());
			 }

		 }
		 return dtHs;
	}
	/**
	 * 汇总月凭证数量
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	private int qryMonthVouchers(String pk_corp,DZFDate startdate,DZFDate enddate) throws DZFWarpException{

		int vouchs = 0;
//		String vouSql = "select count(h.pk_tzpz_h) dr from ynt_tzpz_h h"
//				 +" inner join ynt_image_group g on h.pk_image_group=g.pk_image_group"
//				 +" where h.pk_corp = ? AND h.doperatedate between ? AND ? and nvl(h.dr,0)=0";
		String vouSql = "select count(h.pk_tzpz_h) dr from ynt_tzpz_h h"
				 +" where h.pk_corp = ? AND h.doperatedate between ? AND ? and nvl(h.dr,0)=0";
		 SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null, pk_corp));
			SQLParameter sp=new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(startdate);
			sp.addParam(enddate);
		 ArrayList al = (ArrayList) sbo.executeQuery(vouSql,sp, new ArrayListProcessor());
		 if(al!= null&& al.size()>0){
			 vouchs = Integer.parseInt(((Object[])al.get(0))[0].toString());
		 }
		 return vouchs;
	}
    	/**
	 * 查询现金银行日记账表
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	private HashMap<String, DailyBean> qryCashBankRep(String pk_corp,DZFDate startdate,DZFDate enddate) throws DZFWarpException{
		HashMap<String, DailyBean> repHs = new HashMap<String, DailyBean>();
		//查询现金和银行科目主键
		//余额=期初+本期发生额(借方-贷方)
		HashMap<String, KmMxZVO> mxzVO = qryKmMxZVO(pk_corp,startdate, enddate, "期初余额");
		KmMxZVO xjVO = mxzVO.get("1001");
		KmMxZVO yhVO = mxzVO.get("1002");

		/**期初余额*/
		DZFDouble qcXjYe = DZFDouble.ZERO_DBL;
		DZFDouble qcYhYe = DZFDouble.ZERO_DBL;
		if(xjVO != null && xjVO.getYe() != null){
			qcXjYe = xjVO.getYe();
		}
		if(yhVO != null && yhVO.getYe() != null){
			qcYhYe = yhVO.getYe();
		}
		/*
		 * 汇总期初到当天的发生额(当月第一天到查询日期的发生额)
		 */
		StringBuffer qrySum2BeginDateSql = new StringBuffer();
		qrySum2BeginDateSql.append("select sum(b.jfmny) jfmny,sum(b.dfmny) dfmny,h.doperatedate doperatedate," );
		qrySum2BeginDateSql.append("  substr(sub.accountcode,0,4) memo  ");
		qrySum2BeginDateSql.append("  from YNT_TZPZ_H h  ");
		qrySum2BeginDateSql.append("  inner join YNT_TZPZ_B b on h.PK_TZPZ_H=b.PK_TZPZ_H " );
		qrySum2BeginDateSql.append("  inner join ynt_cpaccount sub on b.pk_accsubj = sub.pk_corp_account " );
		qrySum2BeginDateSql.append("  where h.pk_corp=? and h.doperatedate between ? and ? " );
		qrySum2BeginDateSql.append("  and nvl(h.dr,0)=0  and nvl(b.dr,0)=0");
		qrySum2BeginDateSql.append("  and (sub.accountcode like '1001%' or sub.accountcode like '1002%') " );;
		qrySum2BeginDateSql.append("  group by substr(sub.accountcode,0,4),h.doperatedate order by h.doperatedate  ");
				SQLParameter sp=new SQLParameter();
				sp.addParam(pk_corp);
				sp.addParam(startdate.toString());
				sp.addParam(enddate.toString());
			List<TzpzHVO> acvos = (List<TzpzHVO>) singleObjectBO.executeQuery(qrySum2BeginDateSql.toString(), sp,new BeanListProcessor(TzpzHVO.class));


		//按照发生额日期排序
		TreeMap<String, List<TzpzHVO>> date_kmac = new TreeMap<String, List<TzpzHVO>>();
		if(acvos != null && acvos.size()>0){
			for(TzpzHVO acVO : acvos){
				if(acVO.getDoperatedate().before(startdate)){
					//期初加上 查询开始日期之前发生额
					if(acVO.getMemo()!=null && acVO.getMemo().equals("1001"))
						qcXjYe = qcXjYe.add(DZFDouble.getUFDouble(acVO.getJfmny())).sub(DZFDouble.getUFDouble(acVO.getDfmny()));
					else if(acVO.getMemo()!=null && acVO.getMemo().equals("1002"))
						qcYhYe = qcYhYe.add(DZFDouble.getUFDouble(acVO.getJfmny())).sub(DZFDouble.getUFDouble(acVO.getDfmny()));
					continue;
				}

				List<TzpzHVO> voLs = new ArrayList<TzpzHVO>();
				if(date_kmac.containsKey(acVO.getDoperatedate().toString())){
					voLs = (date_kmac.get(acVO.getDoperatedate().toString()));
				}

				voLs.add(acVO);

				date_kmac.put(acVO.getDoperatedate().toString(), voLs);
			}
		}else{
			//本期发生额都为空 查询最晚日期现金，银行的余额，本日发生额都为空
			int days = enddate.getDaysAfter(startdate);
			int i = 0 ;
			 do {
				 DailyBean bean  = new DailyBean();
				 bean.setPrevue(startdate.getDateAfter(i).toString());
				 bean.setCashcreditmny(DZFDouble.ZERO_DBL);
				 bean.setCashdebitmny(DZFDouble.ZERO_DBL);
				 bean.setBankcreditmny(DZFDouble.ZERO_DBL);
				 bean.setBankdebitmny(DZFDouble.ZERO_DBL);
				 bean.setCashmny(qcXjYe);
				 bean.setBankmny(qcYhYe);
//				 String msg = "2.现金增加 0.00 元，减少 0.00 元，本日余额为"+Common.format(qcXjYe)+"元；\n3.银行存款增加 0.00 元，减少 0.00 元；本日余额为"+Common.format(qcYhYe)+"元；";
				 repHs.put(startdate.getDateAfter(i).toString(), bean);
				 i++;
				}while(i<=days);

			return repHs;
		}

		//记录上一天余额；key：日期+科目 value:上一天余额
		HashMap<String, DZFDouble> lastDateYeHs = new HashMap<String, DZFDouble>();
		//本期发生余额=借方-贷方
		int days = enddate.getDaysAfter(startdate);
		do {

			List<TzpzHVO> kmVOLs = date_kmac.get(startdate.toString());

			if(kmVOLs != null && kmVOLs.size()>0){

				String lastDateKey = startdate.getDateBefore(1).toString();

				for(TzpzHVO kmVO : kmVOLs){
					DZFDouble lastYe = DZFDouble.ZERO_DBL;
					if(lastDateYeHs.containsKey(lastDateKey+"#"+kmVO.getMemo())){//kmVO.getMemo 作为科目
						lastYe = lastDateYeHs.get(lastDateKey+"#"+kmVO.getMemo());
					}
					//记录上一天现金和银行两科目的发生差额
					lastDateYeHs.put(startdate+"#"+kmVO.getMemo(),  lastYe.add(DZFDouble.getUFDouble(kmVO.getJfmny()).sub(DZFDouble.getUFDouble(kmVO.getDfmny()))));
				}
				//如果后一天为查询的时间段，记录当天的
				if(enddate.getDaysAfter(startdate)>=0){

					DZFDouble xjzj = DZFDouble.ZERO_DBL;
					DZFDouble xjjs = DZFDouble.ZERO_DBL;
					DZFDouble yhzj = DZFDouble.ZERO_DBL;
					DZFDouble yhjs = DZFDouble.ZERO_DBL;

					for(TzpzHVO kmVO : kmVOLs){
						DZFDouble lastDateMny = DZFDouble.ZERO_DBL;
						if(lastDateYeHs.containsKey(lastDateKey+"#"+kmVO.getMemo())){
							lastDateMny = lastDateYeHs.get(lastDateKey+"#"+kmVO.getMemo());
						}
						if("1001".equals(kmVO.getMemo())){
							xjzj = kmVO.getJfmny();
							xjjs = kmVO.getDfmny();
							qcXjYe = qcXjYe.add(DZFDouble.getUFDouble(xjzj)).sub(DZFDouble.getUFDouble(xjjs));

						}else if("1002".equals(kmVO.getMemo())){
							yhzj = kmVO.getJfmny();
							yhjs = kmVO.getDfmny();
							qcYhYe = qcYhYe.add(DZFDouble.getUFDouble(yhzj)).sub(DZFDouble.getUFDouble(yhjs));
						}
					}

					 DailyBean bean  = new DailyBean();
					 bean.setPrevue(startdate.toString());
					 bean.setCashcreditmny(xjzj);
					 bean.setCashdebitmny(xjjs);
					 bean.setBankcreditmny(yhzj);
					 bean.setBankdebitmny(yhjs);
					 bean.setCashmny(qcXjYe);
					 bean.setBankmny(qcYhYe);

					repHs.put(startdate.toString(), bean);
				}
			}else{
				String nullDate = "";
				if(!date_kmac.containsKey((startdate+"#1001"))){
					nullDate = "2.现金增加 0.00 元，减少 0.00元，本日余额为"+Common.format(qcXjYe)+"元；";
				}
				if(!date_kmac.containsKey((startdate+"#1002"))){
					nullDate = nullDate+ "\n3.银行存款增加 0.00 元，减少 0.00元，本日余额为"+Common.format(qcYhYe)+"元；";
				}
				DailyBean bean  = new DailyBean();
				bean.setPrevue(startdate.toString());
				bean.setCashcreditmny(DZFDouble.ZERO_DBL);
				bean.setCashdebitmny(DZFDouble.ZERO_DBL);
				bean.setBankcreditmny(DZFDouble.ZERO_DBL);
				bean.setBankdebitmny(DZFDouble.ZERO_DBL);
				bean.setCashmny(qcXjYe);
				bean.setBankmny(qcYhYe);
				repHs.put(startdate.toString(), bean);
			}

			days--;
			startdate = startdate.getDateAfter(1);

		} while (days>=0);

		return repHs;
	}

	private void putNsZt(String pk_corp, String period, String column,String putcolumn,Map<String,Object> djzjmap ) {
		SQLParameter sp = new SQLParameter();
		String qrysql = "select * from nsworkbench where nvl(dr,0)=0 and pk_corp = ? and period = ?";
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<BsWorkbenchVO> bsworklist = (List<BsWorkbenchVO>) singleObjectBO.executeQuery(qrysql, sp,
				new BeanListProcessor(BsWorkbenchVO.class));

		if (bsworklist != null && bsworklist.size() > 0 && bsworklist.get(0).getAttributeValue(column) != null
				&& (Integer) bsworklist.get(0).getAttributeValue(column) == 1) {
			djzjmap.put(putcolumn+"num", 0);
			djzjmap.put(putcolumn,  "已完成" );
		} else {
			djzjmap.put(putcolumn+"num", 1);
			djzjmap.put(putcolumn,  "未完成" );
		}
	}

	/**
	 * 资产负债
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String,String> getZcFz(Map<String,String> zcfzmap,String period,String pk_corp,Integer versionno) throws DZFWarpException{
		String msg = "";
		ZcFzBVO[] result = iRemoteReportService.getZCFZBVOs(period, pk_corp,"N","N") ;
		if(result != null && result.length>0){
			msg = msg+("\n 资产负债状况：");
			DZFDouble zcze = DZFDouble.ZERO_DBL;
			DZFDouble fzhj = DZFDouble.ZERO_DBL;
			DZFDouble jzc = DZFDouble.ZERO_DBL;
			for(ZcFzBVO vo : result){
				if("负债和所有者权益(或股东权益)总计".equals(vo.getFzhsyzqy())){
					zcze = vo.getQmye1();
				}
				else if("负债合计".equals(vo.getFzhsyzqy())){
					fzhj = vo.getQmye2();
				}
				else if("所有者权益（或股东权益)合计".equals(vo.getFzhsyzqy())){
					jzc = vo.getQmye2();
				}
			}

			if(versionno.intValue()<IVersionConstant.VERSIONNO320){
				zcfzmap.put("zcze", "资产总额:  "+Common.format(zcze));
				zcfzmap.put("fzze", "负债总额:  "+Common.format(fzhj));
				zcfzmap.put("jzc", "净资产:  "+Common.format(jzc));
			}else{
				zcfzmap.put("zcze", ""+Common.format(zcze));
				zcfzmap.put("fzze", ""+Common.format(fzhj));
				zcfzmap.put("jzc", ""+Common.format(jzc));
			}
		}
		return zcfzmap;
	}
    	/**
	 * 查询现金银行
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String,String> getXjYh(Map<String,String> zcfzmap,String pk_corp,DZFDate startdate,DZFDate enddate,Integer versionno) throws DZFWarpException{
		DZFDouble xjzj = DZFDouble.ZERO_DBL;
		DZFDouble xjjs = DZFDouble.ZERO_DBL;
		DZFDouble xjye = DZFDouble.ZERO_DBL;
		DZFDouble yhzj = DZFDouble.ZERO_DBL;
		DZFDouble yhjs = DZFDouble.ZERO_DBL;
		DZFDouble yhye = DZFDouble.ZERO_DBL;
		//查询科目明细账
		KmMxZVO[] vos = iRemoteReportService.getXJRJZVOsConMo(pk_corp, "1001","1002",  startdate, enddate, DZFBoolean.FALSE, DZFBoolean.FALSE, DZFBoolean.FALSE , DZFBoolean.TRUE,"",Arrays.asList(new String[]{"1001","1002"}),null) ;
		if(vos != null && vos.length>0){
		//查询科目明细账

			for(KmMxZVO vo : vos){
				if("1001".equals(vo.getKmbm()) && "本月合计".equals(vo.getZy())){
					xjzj = SafeCompute.add(xjzj, vo.getJf());
					xjjs = SafeCompute.add(xjjs, vo.getDf());
					xjye = vo.getYe();

				}else if("1002".equals(vo.getKmbm()) && "本月合计".equals(vo.getZy())){
					yhzj = SafeCompute.add(yhzj, vo.getJf());
					yhjs = SafeCompute.add(yhjs, vo.getDf());
					yhye = vo.getYe();
				}
			}
		}

		if(versionno.intValue()<IVersionConstant.VERSIONNO320){
			zcfzmap.put("xj", "现金:  +"+Common.format(xjzj)+" -"+Common.format(xjjs));
			zcfzmap.put("xjye", "余额:  "+Common.format(xjye));
			zcfzmap.put("yh", "银行:  +"+Common.format(yhzj)+" -"+Common.format(yhjs));
			zcfzmap.put("yhye", "余额:  "+Common.format(yhye));
		}else{
			zcfzmap.put("xj", ""+Common.format(xjzj)+" "+Common.format(xjjs));
			zcfzmap.put("xjye", ""+Common.format(xjye));
			zcfzmap.put("yh", ""+Common.format(yhzj)+" "+Common.format(yhjs));
			zcfzmap.put("yhye", ""+Common.format(yhye));
		}

		return zcfzmap;
	}
    	/**
	 * 获取季度费用支出
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String,String> getJdFyzc(Map<String,String> jycgmap,String pk_corp,String period,DZFDouble qysds,Integer versionno) throws DZFWarpException{
		String msg = "";
		QueryParamVO qryvo=new QueryParamVO();
		qryvo.setPk_corp(pk_corp);
		qryvo.setIshasjz(DZFBoolean.FALSE);//审核与记账条件统一
		qryvo.setIshassh(DZFBoolean.FALSE);
		qryvo.setQjq(period);
		qryvo.setQjz(period);
		qryvo.setBegindate1(DateUtils.getPeriodStartDate(period));
		qryvo.setEnddate(DateUtils.getPeriodStartDate(period));
		//
		LrbquarterlyVO[] LrbVOs = iRemoteReportService.getLRBquarterlyVOs(qryvo) ;
		if(LrbVOs != null && LrbVOs.length>0){
			msg = msg+("\n经营成果：");
			DZFDouble zc = DZFDouble.ZERO_DBL;
			DZFDouble fy = DZFDouble.ZERO_DBL;
			DZFDouble lr = DZFDouble.ZERO_DBL;
			for(LrbquarterlyVO lrvo : LrbVOs){
				DZFDouble mny = lrvo.getByje()==null?DZFDouble.ZERO_DBL:lrvo.getByje();
				if("一、营业收入".equals(lrvo.getXm())){
					if(versionno.intValue()<IVersionConstant.VERSIONNO320){
						jycgmap.put("bysr", "本月收入:  "+Common.format(mny));
					}else{
						jycgmap.put("bysr", ""+Common.format(mny));
					}
				}else if(lrvo.getXm().contains("减：营业成本")){
					zc = zc.add(mny);
				}else if(lrvo.getXm().contains("税金及附加")){
					zc = zc.add(mny);
				}else if(lrvo.getXm().contains("销售费用")){
					zc = zc.add(mny);
					fy = fy.add(mny);
				}else if(lrvo.getXm().contains("管理费用")){
					zc = zc.add(mny);
					fy = fy.add(mny);
				}else if(lrvo.getXm().contains("财务费用")){
					zc = zc.add(mny);
					fy = fy.add(mny);
				}else if(lrvo.getXm().trim().contains("二、营业利润（亏损以“-”号填列）")){
					lr = mny;
				}
			}
			if(versionno.intValue()<IVersionConstant.VERSIONNO320){
				jycgmap.put("byzc", "本月支出:  "+Common.format(zc));
				jycgmap.put("gxfy", "各项费用:  "+Common.format(fy));
				jycgmap.put("lrze", "利润总额:  "+Common.format(lr));
			}else{
				jycgmap.put("byzc", ""+Common.format(zc));
				jycgmap.put("gxfy", ""+Common.format(fy));
				jycgmap.put("lrze", ""+Common.format(lr));
			}

		}
		return jycgmap;
	}
	/**
	 * 获取费用支出
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String,String> getFyzc(Map<String,String> jycgmap,String pk_corp,String period,DZFDouble qysds,Integer versionno) throws DZFWarpException{
		String msg = "";
		QueryParamVO qryvo=new QueryParamVO();
		qryvo.setPk_corp(pk_corp);

		qryvo.setIshasjz(DZFBoolean.FALSE);//审核与记账条件统一
		qryvo.setIshassh(DZFBoolean.FALSE);
		qryvo.setQjq(period);
		qryvo.setQjz(period);
		//
		LrbVO[] LrbVOs = iRemoteReportService.getLRBVOs(qryvo) ;
		if(LrbVOs != null && LrbVOs.length>0){
			msg = msg+("\n经营成果：");
			DZFDouble zc = DZFDouble.ZERO_DBL;
			DZFDouble fy = DZFDouble.ZERO_DBL;
			DZFDouble lr = DZFDouble.ZERO_DBL;
			for(LrbVO lrvo : LrbVOs){
				DZFDouble mny = lrvo.getByje()==null?DZFDouble.ZERO_DBL:lrvo.getByje();
				if("一、营业收入".equals(lrvo.getXm())){
					if(versionno.intValue()<IVersionConstant.VERSIONNO320){
						jycgmap.put("bysr", "本月收入:  "+Common.format(mny));
					}else{
						jycgmap.put("bysr", ""+Common.format(mny));
					}
				}else if(lrvo.getXm().contains("减：营业成本")){
					zc = zc.add(mny);
				}else if(lrvo.getXm().contains("税金及附加")){
					zc = zc.add(mny);
				}else if(lrvo.getXm().contains("销售费用")){
					zc = zc.add(mny);
					fy = fy.add(mny);
				}else if(lrvo.getXm().contains("管理费用")){
					zc = zc.add(mny);
					fy = fy.add(mny);
				}else if(lrvo.getXm().contains("财务费用")){
					zc = zc.add(mny);
					fy = fy.add(mny);
				}else if(lrvo.getXm().trim().contains("二、营业利润（亏损以“-”号填列）")){
					lr = mny;
				}
			}
			if(versionno.intValue()<IVersionConstant.VERSIONNO320){
				jycgmap.put("byzc", "本月支出:  "+Common.format(zc));
				jycgmap.put("gxfy", "各项费用:  "+Common.format(fy));
				jycgmap.put("lrze", "利润总额:  "+Common.format(lr));
			}else{
				jycgmap.put("byzc", ""+Common.format(zc));
				jycgmap.put("gxfy", ""+Common.format(fy));
				jycgmap.put("lrze", ""+Common.format(lr));
			}

		}
		return jycgmap;
	}

	private DZFDouble getDzfDouble(DZFDouble value){
		if(value == null ){
			return DZFDouble.ZERO_DBL;
		}
		return value;
	}
	/**
	 * 获取现金银行科目明细账
	 * @param pk_corp
	 * @param startdate
	 * @param enddate
	 * @param zy
	 * @return key=科目+摘要;value=明细账VO
	 * @throws DZFWarpException
	 */
	private HashMap<String, KmMxZVO> qryKmMxZVO(String pk_corp,DZFDate startdate,DZFDate enddate,String zy) throws DZFWarpException{

		HashMap<String, KmMxZVO> km_vo = new HashMap<String, KmMxZVO>();

		QueryParamVO vo=new QueryParamVO();
		vo.setPk_corp(pk_corp);
		vo.setKms("1001,1002");
		vo.setKmcodelist(Arrays.asList(vo.getKms().split(",")));
		vo.setKmsx(null);
		vo.setBegindate1(startdate);
		vo.setEnddate(enddate);
		vo.setXswyewfs(DZFBoolean.FALSE);
		vo.setXsyljfs(DZFBoolean.FALSE);
		vo.setIshasjz(DZFBoolean.FALSE);
		vo.setIshassh(DZFBoolean.FALSE);
		vo.setPk_currency("");
		KmMxZVO[] vos= iRemoteReportService.getKMMXZVOs(vo,null);

		if(vos != null && vos.length>0){

			for(KmMxZVO rvo : vos){
				if(rvo.getZy().equals(zy)){
					if("1001".equals(rvo.getKmbm())){
						km_vo.put("1001", rvo);
					}else if("1002".equals(rvo.getKmbm())){
						km_vo.put("1002", rvo);
					}
				}
			}
		}

		return km_vo;
	}




//	public ResponseBaseBeanVO  qryDailyNew( ReportBeanVO qryDailyBean) throws DZFWarpException{
//
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO() ;
//		String pk_corp = qryDailyBean.getPk_corp();
//		String period = qryDailyBean.getPeriod();
//		DZFDate startdate = DateUtils.getPeriodStartDate(period);
//		//				 DZFDate.getDate(period+"-01");
//		DZFDate enddate = DateUtils.getPeriodEndDate(period);
//
//		IXjRjZReport ir =(IXjRjZReport)SpringUtils.getBean("gl_rep_xjyhrjzserv");
//
//		KmMxZVO[] kmmxvos = ir.getXJRJZVOsConMo(pk_corp,
//			    null,null,  startdate,enddate,
//			    new DZFBoolean(false)//无余额发生不显示
//				,new DZFBoolean(true)//有累计发生不显示
//				,new DZFBoolean(true),//包含未记账
//				new DZFBoolean(true), null, null,null);//默认人民币
//
//		  return bean;
//	}
//



//	/**
//	 * 查询现金银行科目pk
//	 * @param pk_corp
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private String qryKmPk(String pk_corp) throws DZFWarpException{
//
//		String qryKm = "select pk_corp_account from ynt_cpaccount where pk_corp=? and (accountcode like ? or accountcode like ?) and nvl(dr,0)=0";
//		 SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null, pk_corp));
//			SQLParameter sp=new SQLParameter();
//			sp.addParam(pk_corp);
//			sp.addParam("1001%");
//			sp.addParam("1002%");
//		ArrayList pk_sbjectLs = (ArrayList)sbo.executeQuery(qryKm, sp,new ArrayListProcessor());
//
//		if(pk_sbjectLs != null && pk_sbjectLs.size()>0){
//
//			StringBuffer pk_subj = new StringBuffer();
//
//			for(int i = 0 ; i < pk_sbjectLs.size() ; i++){
//
//				Object[] pk_subjObj = (Object[]) pk_sbjectLs.get(i);
//				if(pk_subjObj != null && pk_subjObj.length>0){
//					pk_subj.append(",'"+pk_subjObj[0]+"'");
//				}
//			}
//			return pk_subj+" ";
//		}
//		return null;
//	}











//
//
//
//
//
//
//	//纳税查询
//	public ResponseBaseBeanVO qryPayTax(ReportBeanVO ReportBeanVO) {
//		ResponseBaseBeanVO rbean = new ResponseBaseBeanVO();
//		IZzSyYsDbReport izz = (IZzSyYsDbReport) SpringUtils.getBean("gl_rep_yyssbbserv");
//		try {
//			String start = ReportBeanVO.getBeginperiod();
//			String end = ReportBeanVO.getEndperiod();
//
//			Integer istart = new Integer(start.substring(5));
//			Integer iend = new Integer(end.substring(5));
//
//			String year = start.substring(0, 4);
//			int m = iend - istart;
//
//			if (m < 0) {
//				rbean.setRescode(IConstant.FIRDES);
//				rbean.setResmsg("参数有误，纳税查询失败");
//				return rbean;
//			}
//
//			List<String> periodlist = new ArrayList<String>();
//
//			for (int i = 0; i < m + 1; i++) {
//				if ((istart + i) < 10) {
//					periodlist.add(year + "-0" + (istart + i));
//				} else {
//					periodlist.add(year + "-" + (istart + i));
//				}
//			}
//
//			ZzSyYsDBVO[] zzvo = izz.getZZSYYSDBVOsForPeriod(periodlist.toArray(new String[0]),
//					ReportBeanVO.getPk_corp());
//
//			rbean.setRescode(IConstant.DEFAULT);
//			//一条数据分成收入和税金处理
//			if(ReportBeanVO.getVersionno().intValue()>=IVersionConstant.VERSIONNO320){
//				List<ZzSyYsDBVO> listns = handlerNs(zzvo);
//				rbean.setResmsg(listns);
//			}else{
//				rbean.setResmsg(zzvo);
//			}
//		} catch (Exception e) {
//			rbean.setRescode(IConstant.FIRDES);
//			rbean.setResmsg("纳税查询失败 "  + e.getMessage());
//		}
//
//		return rbean;
//	}
//
//	/**
//	 * 按照收入和税金 分成两条数据处理
//	 * @param zzvo
//	 */
//	private List<ZzSyYsDBVO> handlerNs(ZzSyYsDBVO[] zzvo) {
//
//		List<ZzSyYsDBVO> resdbvos = new ArrayList<ZzSyYsDBVO>();
//
//		ZzSyYsDBVO srvo = null;
//		ZzSyYsDBVO sjvo = null;
//		for(ZzSyYsDBVO bvo:zzvo){
//			srvo = new ZzSyYsDBVO();
//			sjvo = new ZzSyYsDBVO();
//
//			srvo.setPeriod(bvo.getPeriod());
//			srvo.setZysr(getDzfDouble(bvo.getZysr()));
//			srvo.setQtywsr(getDzfDouble(bvo.getQtywsr()));
//			srvo.setInsum(getDzfDouble(bvo.getInsum()));
//
//			sjvo.setPeriod(bvo.getPeriod());
//			sjvo.setZztax(getDzfDouble(bvo.getZztax()));
//			sjvo.setBusitax(getDzfDouble(bvo.getBusitax()));
//			sjvo.setSpendtax(getDzfDouble(bvo.getSpendtax()));
//			sjvo.setCsmaintax(getDzfDouble(bvo.getCsmaintax()));
//			sjvo.setStudytax(getDzfDouble(bvo.getStudytax()));
//			sjvo.setPartstudytax(getDzfDouble(bvo.getPartstudytax()));
//			sjvo.setTaxsum(getDzfDouble(bvo.getTaxsum()));
//
//			resdbvos.add(sjvo);
//			resdbvos.add(srvo);
//		}
//
//		return resdbvos;
//
//	}


//
//	 @Override
//		public ResponseBaseBeanVO qryDeclarForm(ReportBeanVO reportBean)
//				throws DZFWarpException {
//			ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//			try {
//				String pk_corp = reportBean.getPk_corp();
//
//				Integer corpschema = yntBoPubUtil.getAccountSchema(pk_corp);
//
//				//查询年
//				String qYear = reportBean.getPeriod();
//				Calendar beginDate = Calendar.getInstance();
//				beginDate.set(Calendar.DAY_OF_MONTH, 1);
//				//当前年
//				int year = beginDate.get(Calendar.YEAR);
//
//				int endMonth = beginDate.get(Calendar.MONTH);
//				if (qYear != null && Integer.valueOf(qYear) != year) {
//					year = Integer.valueOf(qYear);
//					beginDate.set(Calendar.YEAR, year);
//					endMonth = 11;
//				}
//
//				CorpVO corp = CorpCache.getInstance().get("", pk_corp);
//				DZFDate corpBegin = corp.getBegindate();
//				if (corpBegin.getYear() > year) {
//					throw new BusinessException("查询年在建账年之前");
//				}
//
//				int beginMoth = 0;
//				if (corpBegin.getYear() == year) {
//					beginMoth = corpBegin.getMonth() - 1;
//				}
//
//				List<NssbbVO> sbbvos = new ArrayList<NssbbVO>();
//				QueryParamVO paramvo = new QueryParamVO();
//				paramvo.setPk_corp(pk_corp);
//
//				String zyCode = "";
//				if(corpschema == DzfUtil.SEVENSCHEMA.intValue()){
//					zyCode = "6001";
//				}else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()){
//					zyCode = "5001";
//				}else {
//					throw new BusinessException("该行业暂不支持利润表,敬请期待!");
//				}
//
//				List<String> accountlist = new ArrayList<String>();
//				accountlist.add(zyCode);
//
//				SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, pk_corp));
//				SQLParameter sp = new SQLParameter();
//				sp.addParam(pk_corp);
//
////				String coderule = (String) sbo.executeQuery("select accountcoderule from bd_corp where pk_corp = ?",
////						sp, new ColumnProcessor());
////
////				String xxCode = "22210102";//销项税额
////				if (coderule == null || coderule.startsWith("4/2/2")) {
////				} else if (coderule.startsWith("4/3/3")) {
////					xxCode = "2221001002";
////				} else if (coderule.startsWith("4/3/2")) {
////					xxCode = "222100102";
////				}
////				accountlist.add(xxCode);
////				paramvo.setKms(xxCode + "," + zyCode);
//
//				paramvo.setKmcodelist(accountlist);
//				paramvo.setKms(zyCode);
//				paramvo.setPk_currency(IGlobalConstants.RMB_currency_id);
//				paramvo.setIshasjz(DZFBoolean.FALSE);
//				paramvo.setXswyewfs(DZFBoolean.FALSE);
//				paramvo.setXsyljfs(DZFBoolean.TRUE);
//
//
//				SimpleDateFormat periodFormat = new SimpleDateFormat("yyyy-MM");
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//				IFsYeReport gl_rep_fsyebserv = (IFsYeReport) SpringUtils
//						.getBean("gl_rep_fsyebserv");
//				//额度
//				DZFDouble limt = new DZFDouble(90000);
//
//				String sql = "select count(1) from ynt_bw_invoice_h where pk_corp = ? and invoicedate"
//						+ " >= ? and invoicedate <= ? ";
//				int quarter = 1;
//				for (int mon = 0;  mon <= endMonth && mon < 11; mon++) {
//
//					beginDate.set(Calendar.MONTH, mon);
//					//季初日期
//					Date quarterBegin = beginDate.getTime();
//					paramvo.setQjq(periodFormat.format(quarterBegin));
//					mon += 2;
//					beginDate.set(Calendar.MONTH, mon);
//					beginDate.set(Calendar.DAY_OF_MONTH, beginDate.getMaximum(Calendar.DAY_OF_MONTH));
//					//季末日期
//					Date quarterEnd = beginDate.getTime();
//					paramvo.setQjz(periodFormat.format(quarterEnd));
//
//					String zqName = year + "年" + (quarter++) + "季度";
//
//					if (beginMoth > mon) {
//						continue;
//					}
//
//					NssbbVO nssb = new NssbbVO();
//
//					nssb.setYbtse(DZFDouble.ZERO_DBL);
//					nssb.setZqname(zqName);
//
//
//					FseJyeVO[] fsejyevos = gl_rep_fsyebserv.getFsJyeVOs(paramvo, 1);
//					//主营收入
//					DZFDouble zysr = DZFDouble.ZERO_DBL;
//					if (fsejyevos != null) {
//						for (FseJyeVO fseJyeVO : fsejyevos) {
//							if (zyCode.equals(fseJyeVO.getKmbm())) {
//								zysr = fseJyeVO.getLastmfsdf();
//							}
////							else if (xxCode.equals(fseJyeVO.getKmbm())) {
////								xxse = fseJyeVO.getLastmfsdf();
////							}
//						}
//					}
//					nssb.setKpsr(zysr);
//					// 剩余额度
//					DZFDouble syed = limt.sub(zysr);
//					nssb.setSyed(syed.doubleValue() >= 0 ? syed : DZFDouble.ZERO_DBL);
//					nssb.setYnse(zysr.multiply(0.03));
//					nssb.setJmse(nssb.getYnse());
//					//查询开票量
//					sp.clearParams();
//					sp.addParam(pk_corp);
//					sp.addParam(dateFormat.format(quarterBegin));
//					sp.addParam(dateFormat.format(quarterEnd));
//					BigDecimal kpnum = (BigDecimal) sbo.executeQuery(sql, sp, new ColumnProcessor());
//
//					nssb.setKpnum(kpnum.intValue());
//					sbbvos.add(nssb);
//				}
//				bean.setRescode(IConstant.DEFAULT);
//				bean.setResmsg(sbbvos);
//			} catch (Exception e) {
//				 bean.setRescode(IConstant.FIRDES) ;
//				 bean.setResmsg(e.getMessage()) ;
//				 log.error(e,e);
//			}
//
//			return bean;
//		}
}
