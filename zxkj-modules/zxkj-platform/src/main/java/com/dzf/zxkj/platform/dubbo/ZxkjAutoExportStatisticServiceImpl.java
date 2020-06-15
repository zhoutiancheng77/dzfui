package com.dzf.zxkj.platform.dubbo;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.config.QmjzByDzfConfig;
import com.dzf.zxkj.platform.model.monitor.MonitorExportStaticVO;
import com.dzf.zxkj.platform.util.SendEmailService;
import com.dzf.zxkj.xxljob.service.IAutoExportStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@org.apache.dubbo.config.annotation.Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class ZxkjAutoExportStatisticServiceImpl implements IAutoExportStatisticService {
    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private QmjzByDzfConfig qmjzByDzfConfig;

    @Override
    public void autoExportStatistic() {
        DZFDate lastday = new DZFDate();
        String msg = buildMsg("开始执行...");
        try {
            //查询符合条件的
            List<MonitorExportStaticVO> cps = queryLogs(lastday);
            msg = buildMsg("第一步执行完成(" + getSize(cps) + ")...");

            //排除白名单、次数的限制
            cps = filters(cps);
            msg = buildMsg("第二步执行完成(" + getSize(cps) + ")...");

            //整理发邮件
            sendMail(cps, lastday);
            msg = buildMsg("第三步执行完成(" + getSize(cps) + ")...");
        } catch (Exception e) {
            log.error("统计失败!", e);
        }
        //记录日志

        insertLog(lastday, msg);
    }

    private int getSize(List<MonitorExportStaticVO> cps){
        return cps == null || cps.size() == 0 ? 0 : cps.size();
    }

    private void insertLog(DZFDate date, String msg){
        MonitorExportStaticVO vo = new MonitorExportStaticVO();
        vo.setFtype(1);
        vo.setDoperatedate(date);
        vo.setIssuceess(DZFBoolean.TRUE);
        vo.setContent(msg);
        vo.setPk_corp(IDefaultValue.DefaultGroup);
        singleObjectBO.insertVO(IDefaultValue.DefaultGroup, vo);
    }

    private String buildMsg(String msg){
        log.info(msg);
        return msg;
    }

    private void sendMail(List<MonitorExportStaticVO> cps, DZFDate date){
        if(cps == null || cps.size() == 0)
            return;

        String smtp = qmjzByDzfConfig.smtp;
        String from = qmjzByDzfConfig.from;
        String to = qmjzByDzfConfig.to;
        String username = qmjzByDzfConfig.username;
        String password = qmjzByDzfConfig.password;
        String copyto = qmjzByDzfConfig.copyto;//抄送人
        copyto = StringUtil.isEmpty(copyto) ? "" : copyto;
        String subject = "账簿导出预警";
        String content = buildContent(cps, date);
        SendEmailService.sendAndCc(smtp, from, to, copyto, subject, content, username, password);
    }

    private String buildContent(List<MonitorExportStaticVO> cps, DZFDate date){

        StringBuffer sf = new StringBuffer();
        sf.append("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'><title>账簿导出预警</title><style>table tbody tr td{ word-wrap:break-word;word-break:break-all;}table {border-spacing: 0;border-collapse: collapse;text-align: center;} table thead th { background: #ccc; }</style></head>");
        sf.append("<body><div><h3>财务账簿导出超次数的会计公司:</h3></div>");
        sf.append("<table width='95%' border='1'>");
        sf.append("<thead><tr><th>日期</th><th>会计公司编码</th><th>会计公司名称</th><th>发生额及余额表导出次数</th></tr></thead><tbody>");

        for(MonitorExportStaticVO cp : cps){
            sf.append("<tr><td align='left'>").append(date.toString())
                    .append("</td><td align='left'>").append(cp.getInnercode())
                    .append("</td><td align='left'>").append(CodeUtils1.deCode(cp.getUnitname()))
                    .append("</td><td align='right'>").append(cp.getNnum())
                    .append("</td></tr>");

        }
        sf.append("</tbody></table></body></html>");

        return sf.toString();
    }

    private List<MonitorExportStaticVO> filters(List<MonitorExportStaticVO> cps){
        if(cps == null || cps.size() == 0)
            return null;
        //白名单，加盟商、非加盟商次数
        String white = qmjzByDzfConfig.whitelist;
        String chalnum = qmjzByDzfConfig.chalnum;
        String unchalnum = qmjzByDzfConfig.unchalnum;

        List<String> whitearr = StringUtil.isEmpty(white) ? null : Arrays.asList(white.split(","));
        int chalnumint = StringUtil.isEmpty(chalnum) ? -1 : Integer.parseInt(chalnum);
        int unchalnumint = StringUtil.isEmpty(unchalnum) ? -1 : Integer.parseInt(unchalnum);

        cps = cps.stream().filter(cp -> {
            if(whitearr != null && whitearr.contains(cp.getPk_corp())){
                return false;
            }else if(cp.getIschannel() != null && cp.getIschannel().booleanValue()
                    && (cp.getNnum() == null || cp.getNnum() < chalnumint)){
                return false;
            }else if((cp.getIschannel() == null || !cp.getIschannel().booleanValue())
                    &&(cp.getNnum() == null || cp.getNnum() < unchalnumint)){
                return false;
            }

            return true;
        }).collect(Collectors.toList());
        return cps;
    }

    private List<MonitorExportStaticVO> queryLogs(DZFDate lastday) throws DZFWarpException {
        String begintime = lastday.toString() + " 00:00:01";
        String endtime = lastday.toString() + " 23:59:59";

        //查询相关小客户主键
        SQLParameter sp = new SQLParameter();
        sp.addParam(ISysConstants.SYS_2);
        sp.addParam(begintime);
        sp.addParam(endtime);
        String sql = qmjzByDzfConfig.sql;
        List<String> ids = (List<String>) singleObjectBO.executeQuery(sql, sp, new ResultSetProcessor() {
            @Override
            public Object handleResultSet(ResultSet rs) throws SQLException {
                List<String> ll = new ArrayList<String>();
                while(rs.next()){
                    ll.add(rs.getString("pk_corp"));
                }
                return ll;
            }
        });

        if(ids == null || ids.size() == 0)
            return null;

        //查询小客户对应的会计公司（加盟商|非加盟商），考虑分支机构情况
        StringBuffer sf = new StringBuffer();
        sf.append(" select pk_corp, unitname, ischannel, innercode, sum(nnum) nnum ");
        sf.append("   from (select pk_corp, unitname, innercode, case ischannel when 'Y' then 'Y' else 'N' end ischannel, 1 nnum ");
        sf.append("   		from (select pk_corp, fathercorp, unitname, ischannel, innercode, 1 nnum ");
        sf.append("          	from bd_corp bd");
        sf.append("  		   start with " + SqlUtil.buildSqlForIn("pk_corp", ids.toArray(new String[0])));
        sf.append(" 		   connect by pk_corp = prior fathercorp and nvl(dr, 0) = 0) tt ");
        sf.append(" 		where tt.fathercorp = ?) gg ");
        sf.append("  group by pk_corp, unitname, ischannel, innercode ");
        sp.clearParams();
        sp.addParam(IDefaultValue.DefaultGroup);

        List<MonitorExportStaticVO> list = (List<MonitorExportStaticVO>) singleObjectBO.executeQuery(sf.toString(),
                sp, new BeanListProcessor(MonitorExportStaticVO.class));

        return list;
    }
}
