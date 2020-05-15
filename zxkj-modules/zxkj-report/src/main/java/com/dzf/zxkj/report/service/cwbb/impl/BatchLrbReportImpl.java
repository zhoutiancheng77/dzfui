package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.report.service.cwbb.IBatchLrbReport;
import com.dzf.zxkj.report.service.cwbb.ILrbReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service("gl_rep_batchlrbserv")
public class BatchLrbReportImpl implements IBatchLrbReport {

    @Autowired
    private ILrbReport gl_rep_lrbserv;

    @Override
    public Map<String, double[]> queryLrbFromCorpids(String period, String[] corpids) throws DZFWarpException {
        Map<String, LrbVO[]> lrbmap = new HashMap<String, LrbVO[]>();
        if (corpids != null && corpids.length > 0 && !StringUtil.isEmpty(period)) {
            ExecutorService pool = null;
            try {
                int maxcount = 20;
                if (corpids.length <= maxcount) {
                    maxcount = corpids.length;
                }
                pool = Executors.newFixedThreadPool(maxcount);

                List<Future<String>> vc = new Vector<Future<String>>();
                for (String cid: corpids) {
                    Future<String> future = pool.submit(new ExecTask(cid,period,lrbmap));

                    vc.add(future);
                }
                for (Future<String> fu : vc) {
                    fu.get();
                }
                pool.shutdown();
            } catch (Exception e) {
                log.error("错误",e);
            } finally {
                try {
                    if (pool != null) {
                        pool.shutdown();
                    }
                } catch (Exception e) {
                    log.error("错误",e);
                }

            }
        }

        Map<String,double[]> res = new HashMap<>();
        if (lrbmap!=null && lrbmap.size() > 0) {
            for (Map.Entry<String, LrbVO[]> entry: lrbmap.entrySet()) {
                LrbVO[] lrvbos = entry.getValue();
                if (lrvbos != null && lrvbos.length > 0) {
                    // 营业收入
                    double d1 = 0;
                    double d2 = 0;
                    //利润总额
                    double d3 = 0;
                    double d4 = 0;
                    //净利润
                    double d5 = 0;
                    double d6 = 0;
                    for (LrbVO vo: lrvbos) {
                        if (!StringUtil.isEmpty(vo.getXm())) {
                            if (vo.getXm().indexOf("营业收入") > 0) {
                                d1 = vo.getByje() == null ? 0 : vo.getByje().doubleValue();
                                d2 = vo.getBnljje() == null ? 0 : vo.getBnljje().doubleValue();
                            } else if (vo.getXm().indexOf("利润总额") > 0) {
                                d3 = vo.getByje() == null ? 0 : vo.getByje().doubleValue();
                                d4 = vo.getBnljje() == null ? 0 : vo.getBnljje().doubleValue();
                            } else if (vo.getXm().indexOf("净利润") > 0) {
                                d5 = vo.getByje() == null ? 0 : vo.getByje().doubleValue();
                                d6 = vo.getBnljje() == null ? 0 : vo.getBnljje().doubleValue();
                            }
                        }
                    }
                    res.put(entry.getKey(),new double[]{d1, d2, d3, d4,d5,d6});
                }
            }
        }
        return res;
    }
    private class ExecTask implements Callable<String> {

        private String cid = null;

        private String period =null;

        private Map<String, LrbVO[]> map = null;

        public ExecTask(String cid, String period, Map<String, LrbVO[]> map) {
            this.cid = cid;
            this.period = period;
            this.map = map;
        }

        @Override
        public String call() throws Exception {
            try {
                QueryParamVO paramVO = new QueryParamVO();
                paramVO.setRptsource("lrb");
                paramVO.setQjz(period);
                paramVO.setBegindate1(DateUtils.getPeriodStartDate(period));
                paramVO.setEnddate(DateUtils.getPeriodEndDate(period));
                LrbVO[] lrbvos =  gl_rep_lrbserv.getLRBVOs(paramVO);
                map.put(cid, lrbvos);
            } catch (Exception e) {
            } finally {
            }
            return "end";
        }
    }
}
