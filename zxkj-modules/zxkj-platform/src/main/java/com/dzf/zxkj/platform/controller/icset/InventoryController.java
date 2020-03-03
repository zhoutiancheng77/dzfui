package com.dzf.zxkj.platform.controller.icset;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFStringUtil;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.icreport.IQueryLastNum;
import com.dzf.zxkj.platform.service.icset.IInventoryService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.ExcelReport;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

import static com.dzf.zxkj.platform.util.SystemUtil.getRequest;

/**
 * 
 * 存货
 *
 */
@RestController
@RequestMapping("/icset/inventoryact")
@Slf4j
public class InventoryController extends BaseController {

	@Autowired
	private IInventoryService iservice;
	@Autowired
	private IQueryLastNum ic_rep_cbbserv;
	@Autowired
	private IYntBoPubUtil yntBoPubUtil;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private ICorpService corpService;

	@GetMapping("/queryInfoByKmId")
	public ReturnData queryInfoByKmId(@RequestParam Map<String, String> param) {
		Grid grid = new Grid();
		InventoryVO queryParamvo = JsonUtils.convertValue(param, InventoryVO.class);
		if (StringUtil.isEmpty(queryParamvo.getPk_subject())) {// kmid == null ||
										// kmid.trim().length() == 0
			grid.setSuccess(false);
			grid.setTotal(0L);
			grid.setRows(new ArrayList<InventoryVO>());
			return ReturnData.error().data(grid);
		}
		List<InventoryVO> list = iservice.query(SystemUtil.getLoginCorpId(), queryParamvo.getPk_subject());
		if (queryParamvo != null && queryParamvo.getIsshow() != null && queryParamvo.getIsshow().booleanValue()) {
			String vchDate = param.get("vdate");
			DZFDate vDate = new DZFDate();
			if (!StringUtil.isEmpty(vchDate)) {
				vDate = new DZFDate(vchDate);
			}
			setNjzValue(list, vDate);
		}
		log.info("查询成功！");
		if (list == null)
			list = new ArrayList<InventoryVO>();
		grid.setTotal(Long.valueOf(list.size()));
		grid.setRows(list);
		grid.setSuccess(true);
		return ReturnData.ok().data(grid);
//		writeJson(grid);
	}

	private void setNjzValue(List<InventoryVO> list, DZFDate date) {

		Map<String, IcbalanceVO> balMap = ic_rep_cbbserv.queryLastBanlanceVOs_byMap1(date.toString(), SystemUtil.getLoginCorpId(),
				null, true);
		if (list != null && list.size() > 0) {
			for (InventoryVO vo : list) {
				if (balMap != null && balMap.size() > 0) {
					IcbalanceVO balvo = balMap.get(vo.getPk_inventory());
					if (balvo != null) {
						vo.setNjzmny(balvo.getNcost());
						vo.setNjznum(balvo.getNnum());
					}
				}
			}
		}
	}
	@GetMapping("/queryInfo")
	public ReturnData queryInfo(@RequestParam Map<String, String> param) {
		Grid grid = new Grid();
		InventoryVO data = JsonUtils.convertValue(param, InventoryVO.class);
		String pk_invclassify = param.get("pk_invclassify");
		List<InventoryVO> list = iservice.queryInfo(SystemUtil.getLoginCorpId(), pk_invclassify);

		String kmmc = param.get("kmmc");
		if (!DZFValueCheck.isEmpty(kmmc)) {
			list = filterKmList(list, kmmc);
		}

        String filtervalue = param.get("filtervalue");
		if (!DZFValueCheck.isEmpty(filtervalue)) {
			list = filterList(list, filtervalue);
		}
		if (data != null && data.getIsshow() != null && data.getIsshow().booleanValue()) {
			setJcInfo(list);
		}
		InventoryVO[]  vos = null;
		if (list != null && list.size() > 0) {
		    // 分页
			if (data != null && data.getIspage() != null && data.getIspage().booleanValue()) {
				int page = data.getPage();
				int rows = data.getRows();
				grid.setTotal((long) list.size());
				vos = getPagedZZVOs(list.toArray(new InventoryVO[list.size()]), page, rows);
			} else {
				vos = list.toArray(new InventoryVO[list.size()]);
			}
		}
		grid.setTotal(list == null ? 0L : list.size() );
		grid.setRows(vos == null ? new InventoryVO[0] : vos);
		grid.setMsg("查询成功！");
		grid.setSuccess(true);
		return ReturnData.ok().data(grid);
	}

	private List<InventoryVO> filterList(List<InventoryVO> list,String value) {
		List<InventoryVO> tlist = new ArrayList<>();
		if (!DZFValueCheck.isEmpty(list)) {
			for (InventoryVO vo : list) {
				if (DZFValueCheck.isEmpty(vo))
					continue;
				if ((!DZFValueCheck.isEmpty(vo.getCode()) && vo.getCode().indexOf(value) >= 0)
						|| (!DZFValueCheck.isEmpty(vo.getName()) && vo.getName().indexOf(value) >= 0)
						|| (!DZFValueCheck.isEmpty(vo.getInvspec()) && vo.getInvspec().indexOf(value) >= 0)
						) {
					tlist.add(vo);
				}
			}
		}
		return tlist;
	}
	private List<InventoryVO> filterKmList(List<InventoryVO> list,String value) {
		List<InventoryVO> tlist = new ArrayList<>();
		if (!DZFValueCheck.isEmpty(list)) {
			for (InventoryVO vo : list) {
				if (DZFValueCheck.isEmpty(vo))
					continue;
				if ((!DZFValueCheck.isEmpty(vo.getKmname()) && vo.getKmname().indexOf(value) >= 0)){
					tlist.add(vo);
				}
			}
		}
		return tlist;
	}

	// 将查询后的结果分页
	private InventoryVO[] getPagedZZVOs(InventoryVO[] PzglPagevos, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= PzglPagevos.length) {// 防止endIndex数组越界
			endIndex = PzglPagevos.length;
		}
		PzglPagevos = Arrays.copyOfRange(PzglPagevos, beginIndex, endIndex);
		return PzglPagevos;
	}

	public void setJcInfo(List<InventoryVO> list){

		if (list == null  || list.size() == 0)
			return;
		String vchStr = getRequest().getParameter("vdate");
		DZFDate vDate = new DZFDate();

		if (!StringUtil.isEmpty(vchStr)) {
			vDate = new DZFDate(vchStr);
		}
		Map<String, IcbalanceVO> balMap = ic_rep_cbbserv.queryLastBanlanceVOs_byMap1(vDate.toString(),
				SystemUtil.getLoginCorpId(), null, true);

		// 新模式模式 启用库存
		CorpVO corpVo = (CorpVO) corpService.queryByPk(SystemUtil.getLoginCorpId());// 防止vo信息有变化
		Map<String, IcbalanceVO> balMap1 = ic_rep_cbbserv.queryLastBanlanceVOs_byMap4(vDate.toString(),
				SystemUtil.getLoginCorpId(), null, true);
		String numStr = parameterserv.queryParamterValueByCode(SystemUtil.getLoginCorpId(), IParameterConstants.DZF009);
		int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
		String priceStr = parameterserv.queryParamterValueByCode(SystemUtil.getLoginCorpId(), IParameterConstants.DZF010);
		int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		for (InventoryVO vo : list) {
			if (corpVo.getIbuildicstyle() != null && corpVo.getIbuildicstyle() == 1) {// 新模式库存
				if (balMap != null && balMap.size() > 0) {
					IcbalanceVO balvo = balMap.get(vo.getPk_inventory());
					if (balvo != null) {
						//
						vo.setNjznum(balvo.getNnum()==null?balvo.getNnum():new DZFDouble(balvo.getNnum().toString(),num));
					}

					IcbalanceVO balvo1 = balMap1.get(vo.getPk_inventory());
					if (balvo1 != null) {
						if ((vo.getNjznum() == null || vo.getNjznum().doubleValue() == 0)
								&& (vo.getNjzmny() == null || vo.getNjzmny().doubleValue() == 0)) {
						} else {
							vo.setNcbprice(SafeCompute.div(balvo1.getNcost(), balvo1.getNnum())
									.setScale(price, 2));
							vo.setNjzmny(balvo1.getNcost());
							vo.setJsprice(SafeCompute.div(balvo1.getNcost(), balvo1.getNnum())
									.setScale(price, 2));
						}
					}

				}
			} else {
				if ((vo.getNjznum() == null || vo.getNjznum().doubleValue() == 0)
						&& (vo.getNjzmny() == null || vo.getNjzmny().doubleValue() == 0)) {
				} else {
					vo.setNcbprice(SafeCompute.div(vo.getNjzmny(), vo.getNjznum()).setScale(price, 2));
					//vo.setJsprice(SafeCompute.div(vo.getNjzmny(), vo.getNjznum()).setScale(price, 2));
				}
			}
		}
	}

	@GetMapping("/queryInfoBypage")
	public ReturnData queryInfoBypage(@RequestParam Map<String, String> param) {
		Grid grid = new Grid();
		InventoryVO data = JsonUtils.convertValue(param, InventoryVO.class);
		int page = data.getPage();
		int rows = data.getRows();
		if (page < 1 || rows < 1) {
			throw new BusinessException("查询失败！");
		}
		List<InventoryVO> list = iservice.queryInfo(SystemUtil.getLoginCorpId(), null, data);
		log.info("查询成功！");
		grid.setTotal(Long.valueOf(list == null ? 0 : list.size()));

		if (list != null && list.size() > 0) {
			InventoryVO[] pvos = getPageVOs(list.toArray(new InventoryVO[list.size()]), page, rows);
			list = Arrays.asList(pvos);
		}

		grid.setRows(list == null ? new ArrayList<InventoryVO>() : list);
		grid.setSuccess(true);
		return ReturnData.ok().data(grid);
	}

	@GetMapping("/queryInfo_kcsp")
	public ReturnData queryInfo_kcsp() {
		Grid grid = new Grid();
		List<InventoryVO> list = iservice.querysp(SystemUtil.getLoginCorpId());
		grid.setTotal(Long.valueOf(list == null ? 0 : list.size()));
		grid.setRows(list == null ? new ArrayList<InventoryVO>() : list);
		grid.setSuccess(true);
		return ReturnData.ok().data(grid);
	}

	@PostMapping("/save")
	public ReturnData save(@RequestBody Map<String, String> param) {
		Json json = new Json();
		InventoryVO[] bodyvos;
		String pre = "新增";
		try {
			String spInfo =param.get("body"); // 获得前台传进来的
			if (!StringUtil.isEmpty(spInfo)) {
				spInfo = spInfo.replace("}{", "},{"); // 修改格式，对象之间用 "," 分隔
				spInfo = "[" + spInfo + "]"; // 最外层加上中括号，转化为json数组的格式
				bodyvos = JsonUtils.convertValue(spInfo, InventoryVO[].class);
			} else {
				bodyvos = new InventoryVO[] {  JsonUtils.convertValue(param, InventoryVO.class)  };// form提交保存
				json.setRows(bodyvos[0]);
			}
			String pk_corp = SystemUtil.getLoginCorpId(); // 获取公司主键
			setAddDefaultValue(bodyvos); // 设置公司名、创建时间、创建者
			if(!StringUtil.isEmpty(bodyvos[0].getPk_inventory())){
				pre = "修改";
			}
			String action = param.get("action"); // 获得前台传进来的
			String ids = param.get("ids");
			// 存货合并
			if (!StringUtil.isEmpty(ids)) {
				InventoryVO rtndata = iservice.saveMergeData(pk_corp, ids,bodyvos);
				json.setData(rtndata);
			} else {
				// 参照新增保存 节点单行修改保存
				if (!StringUtil.isEmpty(action) && "add".equals(action)) {
					InventoryVO[] rtndata = iservice.save1(pk_corp, bodyvos);
					json.setData(rtndata);
				} else {
					// 节点保存
					iservice.save(pk_corp, bodyvos); // 保存数据，返回一个标志位，如果是true添加成功，false为失败
				}
			}
			json.setSuccess(true);
			json.setMsg("保存成功");
			writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "存货保存，"+pre+"存货"+bodyvos[0].getCode()+"_"+bodyvos[0].getName(), ISysConstants.SYS_2);
		} catch (Exception e) {
			printErrorLog(json,e,"存货保存失败");
			writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "存货保存失败", ISysConstants.SYS_2);
		}

		return ReturnData.ok().data(json);
	}

	@PostMapping("/batchSave")
	public ReturnData batchSave(@RequestBody Map<String, String> param) {
		Json json = new Json();
        try {
            String ids = param.get("ids");
            String codes = param.get("codes");
            String names = param.get("names");
            String[] idsArr = DZFStringUtil.getString2Array(ids, ",");
            if (DZFValueCheck.isEmpty(idsArr)){
                throw new BusinessException("您未选择要更新的行数据!");
            }
            String spflid = param.get("splxidbatch");
            String unit = param.get("jldwidbatch");
            String spec = param.get("specbatch");
            String invtype = param.get("typebatch");
            if (StringUtil.isEmpty(invtype) && StringUtil.isEmpty(spflid) && StringUtil.isEmpty(unit)
                    && StringUtil.isEmpty(spec)) {
                throw new BusinessException("没有可以修改的数据");
            }

            String pk_corp = param.get("pk_corp");
            if (StringUtil.isEmpty(pk_corp)) {
                pk_corp = SystemUtil.getLoginCorpId();
            } else {
                checkSecurityData(null,new String[]{pk_corp},null);
            }

            InventoryVO update = new InventoryVO();
            update.setPk_invclassify(spflid);
            update.setPk_measure(unit);
            update.setInvspec(StringUtil.replaceBlank(spec));
    //			update.setInvtype(invtype);
            String susflag = iservice.updateBatch(pk_corp, ids, update);
            if (!StringUtil.isEmpty(susflag)) {
                // 日志记录
                writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET,"批量修改"+getLogInfo(codes,names,idsArr.length), ISysConstants.SYS_2);
                log.info("保存成功");
                json.setMsg("保存成功");
                json.setSuccess(true);
            } else {
                writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET,"批量修改存货失败",ISysConstants.SYS_2);
                json.setMsg("存货档案_批量修改失败");
                json.setSuccess(false);
            }
        } catch (Exception e) {
            printErrorLog(json,e,"存货档案_批量修改失败");
            writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "存货档案_批量修改失败", ISysConstants.SYS_2);
        }
		return ReturnData.ok().data(json);
	}

	@PostMapping("/mergeData")
	public ReturnData mergeData(@RequestBody Map<String, String> param) {
		Json json = new Json();

		String pk_corp = param.get("pk_corp");
        try {
            String spid = param.get("spid");
            String codename = param.get("codename");
            if (StringUtil.isEmpty(pk_corp)) {
                pk_corp = SystemUtil.getLoginCorpId();
            } else {
                checkSecurityData(null,new String[]{pk_corp},null);
            }
            String body = param.get("body"); // 子表
            body = body.replace("}{", "},{");
            InventoryVO[] bodyvos = JsonUtils.deserialize(body, InventoryVO[].class);
            if (DZFValueCheck.isEmpty(bodyvos)) {
                throw new BusinessException("被合并的存货不允许为空!");
            }
            if(DZFValueCheck.isEmpty(spid))
                throw new BusinessException("合并的存货不允许为空!");
            checkSecurityData(bodyvos,null,null,true);
            InventoryVO vo = iservice.saveMergeData(pk_corp, spid, bodyvos);
            json.setMsg("存货合并成功");
            json.setSuccess(true);

            writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, getMergeLogInfo(codename,bodyvos), ISysConstants.SYS_2);
        } catch (Exception e) {
            printErrorLog(json,e,"存货合并失败");
            writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "存货合并失败", ISysConstants.SYS_2);
        }
		return ReturnData.ok().data(json);
	}

    private String getMergeLogInfo(String codename ,InventoryVO[] bodyvos){
        StringBuffer strb = new StringBuffer();
        strb.append("存货合并为").append(codename).append("(");
       for(InventoryVO body:bodyvos){
           strb.append(body.getCode()).append("_").append(body.getName()).append(",");
       }
        String str = strb.substring(0,strb.length()-1);
        strb.setLength(0);
        strb.append(str);
        strb.append(")");
        return strb.toString();
    }

	private void setAddDefaultValue(InventoryVO[] vos) {
		if (vos == null || vos.length == 0)
			return;
		for (InventoryVO v : vos) {
			if (v != null && StringUtil.isEmpty(v.getPk_inventory())) {//// 每条数据不为空，主键不为空
				v.setPk_corp(SystemUtil.getLoginCorpId()); // 设置公司
				v.setCreatetime(new DZFDateTime(new Date())); // 设置创建时间
				v.setCreator(SystemUtil.getLoginUserId()); // 设置创建者
			}
		}
	}

	// 删除记录
	@PostMapping("/onDelete")
	public ReturnData onDelete(@RequestBody Map<String, String> param) {
		String ids = param.get("ids");
		String pk_corps= param.get("gs");
        Json json = new Json();
        try {
            String codes = param.get("codes");
            String names = param.get("names");
            String[] idsArr = DZFStringUtil.getString2Array(ids, ",");
            if (DZFValueCheck.isEmpty(idsArr)){
                throw new BusinessException("数据为空,删除失败!");
            }
            if (!SystemUtil.getLoginCorpId().equals(pk_corps)) {
                json.setSuccess(false);
                json.setMsg("您无操作权限！");
                return ReturnData.error().data(json);
            }
            String errmsg = iservice.deleteBatch(idsArr, SystemUtil.getLoginCorpId());
            json.setSuccess(true);
            if (StringUtil.isEmpty(errmsg)) {
                json.setMsg("删除成功!");
                writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "删除"+getLogInfo(codes,names,idsArr.length), ISysConstants.SYS_2);
            } else {
                json.setMsg(errmsg);
               int idex = errmsg.indexOf("成功删除");
                int idex1 = errmsg.indexOf("条存货!");
                if(idex>=0){
                    writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "成功删除"+errmsg.substring(idex+4,idex1)+"条存货!", ISysConstants.SYS_2);
                }else{
                    writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "删除存货失败!", ISysConstants.SYS_2);
                }
            }
        } catch (Exception e) {
            printErrorLog(json,e,"删除存货失败");
            writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "删除存货失败", ISysConstants.SYS_2);
        }
		return ReturnData.ok().data(json);
	}

	private String getLogInfo(String codes,String names,int total){
	    StringBuffer strb = new StringBuffer();
        String[] codeArr = DZFStringUtil.getString2Array(codes, ",");
        String[] nameArr = DZFStringUtil.getString2Array(names, ",");
        strb.append("存货");
        int len = total>10? 10:total;
        for(int i=0; i<len;i++){
            strb.append(codeArr[i]).append("_").append(nameArr[i]).append(",");
        }
        String str = strb.substring(0,strb.length()-1);
        strb.setLength(0);
        strb.append(str);
        if(total>10){
            strb.append("等").append(codeArr.length).append("条");
        }else{
            strb.append("共").append(codeArr.length).append("条");
        }
        return strb.toString();
    }

	// 查询特定科目信息
	@GetMapping("/queryBySpecialKM")
	public ReturnData queryBySpecialKM() {
		Grid grid = new Grid();
		List<InventoryVO> list = iservice.querySpecialKM(SystemUtil.getLoginCorpId());// corpVo.getPk_corp()
		log.info("查询成功！");
		grid.setRows(list == null ? new ArrayList<InventoryVO>() : list);
		grid.setSuccess(true);
		grid.setMsg("查询成功");
		return ReturnData.ok().data(grid);
	}

	@PostMapping("/impExcel")
	public ReturnData impExcel(HttpServletRequest request) {
		Json json = new Json();
		json.setSuccess(false);
		String userid =SystemUtil.getLoginUserId();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile infile = multipartRequest.getFile("impfile");
		if (infile == null) {
			throw new BusinessException("请选择导入文件!");
		}
		String filename = infile.getOriginalFilename();
		int index = filename.lastIndexOf(".");
		String fileType = filename.substring(index + 1);
		String pk_corp = SystemUtil.getLoginCorpId();
		String msg = iservice.saveImp(infile, pk_corp, fileType, userid);
		json.setMsg(msg);
		json.setSuccess(true);
        writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "导入存货", ISysConstants.SYS_2);
		return ReturnData.ok().data(json);
	}

	// 将查询后的结果分页
	private InventoryVO[] getPageVOs(InventoryVO[] pageVos, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= pageVos.length) {// 防止endIndex数组越界
			endIndex = pageVos.length;
		}
		pageVos = Arrays.copyOfRange(pageVos, beginIndex, endIndex);
		return pageVos;
	}

	// 查询单据号
	@GetMapping("/queryDjCode")
	public ReturnData queryDjCode() {
		Json grid = new Json();
		String invcode = yntBoPubUtil.getInventoryCode(SystemUtil.getLoginCorpId());
		log.info("获取单据号成功！");
		grid.setData(invcode);
		grid.setSuccess(true);
		grid.setMsg("获取单据号成功");
		return ReturnData.ok().data(grid);
//		writeJson(grid);
	}

	@PostMapping("/createPrice")
	public ReturnData createPrice(@RequestBody Map<String, String> param) {
		Json json = new Json();
        try {
            String pk_corp = param.get("pk_corp");
            if (StringUtil.isEmpty(pk_corp)) {
                pk_corp = SystemUtil.getLoginCorpId();
            } else {
                checkSecurityData(null,new String[]{pk_corp},null);
            }
            String bili = param.get("bili");
            String priceway = param.get("priceway");
            String vchStr = param.get("vdate");
            if (StringUtil.isEmpty(priceway)) {
                throw new BusinessException("生成结算价的规则不允许为空!");
            }
            if("2".equals(priceway)){
                if (StringUtil.isEmpty(bili)) {
                    throw new BusinessException("销售平均单价的比例不能为空!");
                }
            }

            String body = param.get("body"); // 子表
            body = body.replace("}{", "},{");
    //		body = "[" + body + "]";
            InventoryVO[] bodyvos = JsonUtils.deserialize(body, InventoryVO[].class);
            if (DZFValueCheck.isEmpty(bodyvos)) {
                throw new BusinessException("生成结算价的存货不允许为空!");
            }
            checkSecurityData(bodyvos,null,null,true);
            iservice.createPrice(pk_corp,priceway,bili, vchStr,bodyvos);
            json.setMsg("生成存货结算价成功");
            json.setSuccess(true);
            writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "生成存货结算价", ISysConstants.SYS_2);
        } catch (Exception e) {
            printErrorLog(json,e,"生成存货结算价失败");
            writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "生成存货结算价失败", ISysConstants.SYS_2);
        }
		return ReturnData.ok().data(json);
	}

	@PostMapping("/expExcel")
	public void expExcel(HttpServletResponse response, @RequestParam Map<String, String> pmap) {
		OutputStream toClient = null;
		try {
			response.reset();
			String  fileName = "shangpin.xls";
			// 设置response的Header
			String date = "存货";
			String formattedName = URLEncoder.encode(date, "UTF-8");
			response.addHeader("Content-Disposition",
					"attachment;filename=" + fileName + ";filename*=UTF-8''" + formattedName+ ".xls");
			toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			ExcelReport<AuxiliaryAccountBVO> ex = new ExcelReport<>();
			ex.expFile(toClient, fileName);
			toClient.flush();
			response.getOutputStream().flush();
		} catch (IOException e) {
			log.error("excel导出错误", e);
		} catch (Exception e) {
			log.error("excel导出错误", e);
		} finally {
			try {
				if (toClient != null) {
					toClient.close();
				}
			} catch (Exception e) {
				log.error("excel导出错误", e);
			}
			try {
				if (response != null && response.getOutputStream() != null) {
					response.getOutputStream().close();
				}
			} catch (Exception e) {
				log.error("excel导出错误", e);
			}
            writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "导出存货模板", ISysConstants.SYS_2);
		}
	}
}
