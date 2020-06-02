package com.dzf.zxkj.report.service.batchprint;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 批量定时任务
 */
@Component                //实例化
@Configurable             //注入bean
@EnableScheduling
@Slf4j
public class BatchPrintExecTask {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @Autowired
    private IBatchPrintSer batchPrintSer;

//    @Scheduled(cron = " 0 0 0 * * ?  ")
    @Scheduled(cron = " 0/30 * * * * ?")
    public void execTask (){
        SQLParameter sp = new SQLParameter();

        BatchPrintSetVo[] vos = (BatchPrintSetVo[]) singleObjectBO.queryByCondition(BatchPrintSetVo.class,
                "nvl(dr,0)=0 and (nvl(ifilestatue,0) = 0 or nvl(ifilestatue,0) = 3 )", sp);

        log.info("任务执行的条数:"+vos.length);

//        UserVO uservo = zxkjPlatformService.queryUserById(userid);

        batchPrintSer.batchexectask(vos, null);
    }
}
