package com.dzf.zxkj.report.dubbo;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.report.service.IRemoteReportService;
import com.dzf.zxkj.report.service.cwbb.IBatchLrbReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Slf4j
@Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class ZxkjRemoteReportImpl implements IRemoteReportService {

    @Autowired
    private IBatchLrbReport gl_rep_batchlrbserv;

    @Override
    public Map<String, double[]> queryLrbData(String period, String[] cids) {
        try {
            return gl_rep_batchlrbserv.queryLrbFromCorpids(period, cids);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getKMZZVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
}
