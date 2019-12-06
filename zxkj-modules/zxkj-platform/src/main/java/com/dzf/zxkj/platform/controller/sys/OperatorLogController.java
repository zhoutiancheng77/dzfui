package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.base.IOperatorLogService;
import com.dzf.zxkj.common.base.LogQueryParamVO;
import com.dzf.zxkj.common.base.LogRecordVo;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.sys.IOperatorType;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/sys_opelog")
@Slf4j
public class OperatorLogController {

    @Autowired
    private IOperatorLogService sys_ope_log;

    @GetMapping("/query")
    public ReturnData<Grid> query(int page, int rows, LogQueryParamVO paramvo) {
        Grid grid = new Grid();
        try {
//            int page =data == null ?1: data.getPage();
//            int rows =data ==null? 100000: data.getRows();
//            LogQueryParamVO paramvo = new LogQueryParamVO();
//            paramvo = (LogQueryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
            paramvo.setPk_corp(SystemUtil.getLoginCorpId());
            List<LogRecordVo> liststemp = sys_ope_log.query(paramvo);
            List<LogRecordVo> lists = getCurrLog(liststemp, page, rows);
            lists = (List<LogRecordVo>) QueryDeCodeUtils.decKeyUtils(new String[]{"vuser"}, lists, 1);
            grid.setMsg("查询成功!");
            grid.setRows(lists);
            grid.setSuccess(true);
            grid.setTotal((long) liststemp.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e );
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败!");
        }
        return ReturnData.ok().data(grid);
    }

    private List<LogRecordVo> getCurrLog(List<LogRecordVo> lists ,int page,int rows){
        List<LogRecordVo> listres = new ArrayList<LogRecordVo>();

        for(int i= (page-1)*rows;i<lists.size() && i < (page)*rows;i++){
            listres.add(lists.get(i));
        }
        return listres;
    }


    /**
     * 查询操作类型
     */
    @GetMapping("/queryType")
    public ReturnData<Json> queryType() {

        Json json = new Json();

        List<Map<String, String>> maps = new ArrayList<Map<String, String>>();

        try {

            Map<String, String> maptemp = null;

            IOperatorType sys_ope_type = (IOperatorType) SpringUtils.getBean("sys_ope_type");

            List<LogRecordEnum> records = sys_ope_type.getLogEnum(SystemUtil.getLoginCorpVo());

            for (LogRecordEnum enum1 : records) {

                maptemp = new HashMap<String, String>();

                maptemp.put("id", String.valueOf(enum1.getValue()));

                maptemp.put("name", enum1.getName());

                maps.add(maptemp);
            }

            json.setSuccess(true);
            json.setRows(maps);
            json.setTotal(Long.parseLong(maps.size()+""));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "获取类型失败!");
        }finally{

        }

        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryOpeUser")
    public ReturnData<Json> queryOpeUser(){

        Json json = new Json();

        List<Map<String, String>> maps = new ArrayList<Map<String, String>>();

        try {

            Map<String, String> maptemp = null;

            IOperatorType sys_ope_type = (IOperatorType) SpringUtils.getBean("sys_ope_type");

            List<UserVO> records = sys_ope_type.getListUservo(SystemUtil.getLoginCorpId());

            Map<String, String> qmap = new HashMap<String, String>();

            qmap.put("id", "");

            qmap.put("name", "所有用户");

            maps.add(qmap);

            for (UserVO enum1 : records) {

                maptemp = new HashMap<String, String>();

                maptemp.put("id", String.valueOf(enum1.getCuserid()));

                maptemp.put("name", enum1.getUser_name());

                maps.add(maptemp);
            }

            json.setSuccess(true);
            json.setTotal(Long.parseLong(maps.size()+""));
            json.setRows(maps);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg(e instanceof BusinessException ? e.getMessage() : "获取类型失败!");
        }finally{
        }
        return ReturnData.ok().data(json);
    }

    public Map<Integer,String> getTypeMap(){

        IOperatorType sys_ope_type = (IOperatorType) SpringUtils.getBean("sys_ope_type");

        List<LogRecordEnum> records = sys_ope_type.getLogEnum(SystemUtil.getLoginCorpVo());

        Map<Integer, String> typemap = new HashMap<Integer, String>();

        for (LogRecordEnum enum1 : records) {
            typemap.put( enum1.getValue(), enum1.getName());
        }

        return typemap;
    }


    /**
     * 打印
     */
//    @SuppressWarnings("unchecked")
//    public void print() {
//        try{
//            String columns =getRequest().getParameter("columns");
//
//            Map<Integer, String> typemap =  getTypeMap();
//
//            LogQueryParamVO paramvo = new LogQueryParamVO();
//            paramvo = (LogQueryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
//            paramvo.setPk_corp(getLogincorppk());
//            List<LogRecordVo> recordlists  = sys_ope_log.query(paramvo);
//
//            recordlists = (List<LogRecordVo>)QueryDeCodeUtils.decKeyUtils(new String[]{"vuser"}, recordlists, 1);
//
//            for(LogRecordVo lgvo:recordlists){
//                lgvo.setOpetypestr(typemap.get(lgvo.getIopetype()));
//            }
//
//            String value =  getObjectMapper().writeValueAsString(recordlists);
//
//            JSONArray array = (JSONArray) JSON.parseArray(value);
//            String qj = paramvo.getBegindate1().toString() +"~"+paramvo.getEnddate().toString();
//
//            JSONArray headlist = (JSONArray) JSON.parseArray(columns);
//            List<String> heads = new ArrayList<String>();
//            List<String> fieldslist = new ArrayList<String>();
//            setIscross(DZFBoolean.TRUE);
//            Map<String, String> name = null;
//            int[] widths =new  int[]{};
//            int len=headlist.size();
//            for (int i = 0 ; i< len; i ++) {
//                name=(Map<String, String>) headlist.get(i);
//                heads.add(name.get("title"));
//                fieldslist.add(name.get("field"));
//                widths =ArrayUtils.addAll(widths, new int[] {3});
//            }
//            String[] fields= (String[]) fieldslist.toArray(new String[fieldslist.size()]);
//
//
//            //字符类型字段(取界面元素id)
//            List<String> list = new ArrayList<String>();
//            list.add("opestr");
//            list.add("vuser");
//            list.add("vuserip");
//            list.add("opetypestr");
//            list.add("vopemsg");
//            printMultiColumn(array, "操作日志", heads, list.toArray(new String[0]), widths, 20, list);
//        }catch(Exception e){
//            log.error("打印失败",e);
//        }
//    }
//
//
//
//
//    //导出Excel
//    public void excelReport(){
//        HttpServletRequest request = getRequest();
//        request.getParameterNames();
//
//        String userid=(String) getRequest().getSession().getAttribute(IGlobalConstants.login_user);
//        LogQueryParamVO paramvo = new LogQueryParamVO();
//        paramvo = (LogQueryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
//        String qj = paramvo.getBegindate1().toString() +"~"+paramvo.getEnddate().toString();
//        paramvo.setPk_corp(getLogincorppk());
//        List<LogRecordVo> recordlists  = sys_ope_log.query(paramvo);
//        Map<Integer, String> typemap = getTypeMap();
//
//        for(LogRecordVo lgvo:recordlists){
//            lgvo.setOpetypestr(typemap.get(lgvo.getIopetype()));
//        }
//
//
//        recordlists = (List<LogRecordVo>)QueryDeCodeUtils.decKeyUtils(new String[]{"vuser"}, recordlists, 1);
//
//        Excelexport2003<LogRecordVo> lxs = new Excelexport2003<LogRecordVo>();
//        LogRecordExcelField xsz = new LogRecordExcelField();
//        xsz.setLogrecordvos(recordlists.toArray(new LogRecordVo[0]));;
//        xsz.setQj(qj);
//        xsz.setCreator(getLoginUserInfo().getUser_name());
//        xsz.setCorpName(getLoginCorpInfo().getUnitname());
//        HttpServletResponse response = getResponse();
//        OutputStream toClient = null;
//        try {
//            response.reset();
//            String fileName = xsz.getExcelport2003Name();
//            String formattedName = URLEncoder.encode(fileName, "UTF-8");
//            response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ";filename*=UTF-8''" + formattedName);
//            toClient = new BufferedOutputStream(response.getOutputStream());
//            response.setContentType("application/vnd.ms-excel;charset=gb2312");
//            lxs.exportExcel(xsz, toClient);
//            toClient.flush();
//            response.getOutputStream().flush();
//        } catch (IOException e) {
//            log.error("excel导出错误",e);
//        }finally{
//            try{
//                if(toClient != null){
//                    toClient.close();
//                }
//                if(response!=null && response.getOutputStream() != null){
//                    response.getOutputStream().close();
//                }
//            }catch(IOException e){
//                log.error("excel导出错误",e);
//            }
//        }
//    }

}
