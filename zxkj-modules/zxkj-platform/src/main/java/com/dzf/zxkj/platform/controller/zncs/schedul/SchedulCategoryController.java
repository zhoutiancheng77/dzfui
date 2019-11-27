package com.dzf.zxkj.platform.controller.zncs.schedul;

import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.zncs.IPrebillService;
import com.dzf.zxkj.platform.service.zncs.ISchedulCategoryService;
import com.dzf.zxkj.platform.util.zncs.ZncsConst;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
//@Component
public class SchedulCategoryController {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Logger log = Logger.getLogger(this.getClass());

    @Autowired
    private IPrebillService prebillService;
    @Autowired
    private ISchedulCategoryService schedulCategoryService;
    @Autowired
    private ICorpService corpService;

    private ScheduledThreadPoolExecutor stexec;
    public SchedulCategoryController(){
        initSchedulThreadPool();
    }
    public void initSchedulThreadPool(){
        try {
            /*ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);*/
            stexec = new ScheduledThreadPoolExecutor(3);
            for (int i = 0; i < 3; i++) {
                /*QueryCategoryList queryCategoryList = new QueryCategoryList();*/
                AutoCategory autoCategory = new AutoCategory();
                /*executor.scheduleAtFixedRate(queryCategoryList, i, 60,  TimeUnit.SECONDS);*/
                stexec.scheduleAtFixedRate(autoCategory, i * 10, 60,  TimeUnit.SECONDS);
            }
        } catch (Exception e) {

            log.error("分类多线程异常！", e);
        }

    }


    private class AutoCategory implements Runnable {

        @Override
        public void run() {
            boolean lock = false;
            String requestid = null;
            List<OcrInvoiceVO> list = prebillService.queryNotCategory();// 查询出所有未分类的票据
            if (list != null&& list.size() > 0) {
                Map<String, List<OcrInvoiceVO>> map = DZfcommonTools.hashlizeObject(list,
                        new String[] { "pk_corp", "period" });// 公司+期间分组
                long lStart = System.currentTimeMillis();
                if (map != null && map.keySet().size() > 0)
                {
                    log.error("当前线程 " + Thread.currentThread().getId() + " 将处理" + map.size() + "个公司分类");

                    int iSuccessCount = 0;
                    for (String key : map.keySet()) {
                        try {
                            if (StringUtils.isEmpty(key)) {
                                continue;
                            }
                            requestid = UUID.randomUUID().toString();
//                            lock = LockUtil.getInstance().addLockKey("ZNCS_AUTOCATEGORY", key, requestid, 60);// 设置60秒
                            if (lock) {
                                // 加锁成功 公司+期间的list去分类
                                List<OcrInvoiceVO> InvoiceVOlist = map.get(key);
                                String pk_corp = InvoiceVOlist.get(0).getPk_corp();//公司id
                                String period = InvoiceVOlist.get(0).getPeriod();//期间
                                long begintime = System.currentTimeMillis();
                                log.error("线程" + Thread.currentThread().getId() + ", " + pk_corp + "," + period + " 开始运行，时间: " + begintime);
                                CorpVO corpVO = corpService.queryByPk(pk_corp);
                                schedulCategoryService.newSaveCorpCategory(InvoiceVOlist,pk_corp,period,corpVO);

                                for (OcrInvoiceVO ocrvo : InvoiceVOlist)
                                {
                                    ocrvo.setUpdateflag(new DZFBoolean(true));
                                    ocrvo.setDatasource(ZncsConst.SJLY_1);
                                }
                                corpVO.setZipcode("autocategory");	//自动分类标志，只是借用邮政编码字段传递临时值
                                schedulCategoryService.updateInvCategory(InvoiceVOlist,pk_corp,period,corpVO);//票据分类

                                long endtime = System.currentTimeMillis();
                                log.error("线程" + Thread.currentThread().getId() + ", " + pk_corp + "," + period + " 运行结束，时间: " + endtime + ", 用时" + ((endtime - begintime) / 1000.0) + "秒");
                                iSuccessCount++;
                            } else {
                                continue;
                            }
                        } catch (Exception e) {
                            log.error("分类任务异常", e);
                        } finally {
                            if(lock){
//                                LockUtil.getInstance().unLock_Key("ZNCS_AUTOCATEGORY", key, requestid);
                            }
                        }
                    }
                    log.error("当前线程 " + Thread.currentThread().getId() + " 结束，共成功处理 " + iSuccessCount + "个公司分类，耗时" + ((System.currentTimeMillis() - lStart) / 1000.0) + "秒");
                }
            }

        };
    }

	/*private class QueryCategoryList implements Runnable{
		private Map<String, List<OcrInvoiceVO>> map;

		public Map<String, List<OcrInvoiceVO>> getMap() {
			return map;
		}
		@Override
		public void run() {

			try {
				List<OcrInvoiceVO> list = prebillService.queryNotCategory();//查询出所有未分类的票据
				map = DZfcommonTools.hashlizeObject(list, new String[] { "pk_corp","period" });//公司+期间分组
			} catch (Exception e) {
				// TODO: handle exception
			}
		};
	}*/


}
