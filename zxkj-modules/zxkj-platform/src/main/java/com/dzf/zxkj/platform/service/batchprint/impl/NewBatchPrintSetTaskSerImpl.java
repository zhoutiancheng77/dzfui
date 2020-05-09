package com.dzf.zxkj.platform.service.batchprint.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.batchprint.INewBatchPrintSetTaskSer;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.dzf.zxkj.report.service.IZxkjReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("newbatchprintser")
@Slf4j
public class NewBatchPrintSetTaskSerImpl implements INewBatchPrintSetTaskSer {

    @Autowired
    private SingleObjectBO singleObjectBO = null;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @Autowired
    private IZxkjReportService zxkjReportService;

    @Autowired
    private IUserService userServiceImpl;

    @Override
    public void execTask() throws DZFWarpException {

        // 1: 找到对应action

        // 2: 执行打印操作

        // 3: 生成目录页

        // 4: 合并打印文件

    }

    @Override
    public void deleteTask() throws DZFWarpException {

    }

    @Override
    public void downLoadFile() throws DZFWarpException {

    }

    @Override
    public List<BatchPrintSetVo> queryTask(String userid) throws DZFWarpException {

        if (StringUtil.isEmpty(userid)) {
            throw new BusinessException("查询参数为空");
        }

        // 查询当前所属的公司
        Set<String> cpids = userServiceImpl.querypowercorpSet(userid);

        if (cpids == null || cpids.size() == 0 ) {
            throw new BusinessException("该用户无关联公司");
        }

        StringBuffer qry = new StringBuffer();
        qry.append(" select a.*,bd_corp.unitname as cname from ");
        qry.append(" " + BatchPrintSetVo.TABLE_NAME + " a");
        qry.append(" left join bd_corp on  a.pk_corp = bd_corp.pk_corp ");
        qry.append(" where nvl(a.dr,0)=0 and  " + SqlUtil.buildSqlForIn("a.pk_corp",cpids.toArray(new String[0])));

        List<BatchPrintSetVo> qrylist = (List<BatchPrintSetVo>) singleObjectBO.executeQuery(qry.toString(), new SQLParameter(),new BeanListProcessor(BatchPrintSetVo.class));

        QueryDeCodeUtils.decKeyUtils(new String[]{"cname"}, qrylist, 1 );
        return qrylist;
    }

}
