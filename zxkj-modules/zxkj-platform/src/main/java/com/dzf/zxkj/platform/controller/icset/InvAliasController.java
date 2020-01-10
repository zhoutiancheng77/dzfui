package com.dzf.zxkj.platform.controller.icset;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFStringUtil;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.service.glic.IInvAccAliasService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 别名设置
 * @author zhw
 *
 */

@RestController
@RequestMapping("/icset/invalias")
@Slf4j
public class InvAliasController extends BaseController {

	@Autowired
	private IInvAccAliasService ic_invtoryaliasserv = null;
	@GetMapping("/queryInv")
	public ReturnData query(@RequestParam Map<String, String> param){
		Grid grid = new Grid();
		String pk_inventory =param.get("pk_inventory");
		InventoryAliasVO[] vos = ic_invtoryaliasserv.query(SystemUtil.getLoginCorpId(),pk_inventory);
		String isfenye = param.get("isfenye");
		QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
		if (vos != null && vos.length > 0) {
			if("Y".equals(isfenye)) {
				int page = queryParamvo.getPage();
				int rows = queryParamvo.getRows();
				vos = getPagedZZVOs(vos, page, rows);
			}
		}
        grid.setTotal(vos == null ? 0L : vos.length );
        grid.setRows(vos == null ? new InventoryAliasVO[0] : vos);
		grid.setSuccess(true);
        grid.setMsg("查询成功");
		return ReturnData.ok().data(grid);
	}

	// 将查询后的结果分页
	private InventoryAliasVO[] getPagedZZVOs(InventoryAliasVO[] PzglPagevos, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= PzglPagevos.length) {// 防止endIndex数组越界
			endIndex = PzglPagevos.length;
		}
		PzglPagevos = Arrays.copyOfRange(PzglPagevos, beginIndex, endIndex);
		return PzglPagevos;
	}

	@PostMapping("/save")
	public ReturnData save(@RequestBody Map<String, String> param){
		Json json = new Json();
		InventoryAliasVO data = JsonUtils.convertValue(param, InventoryAliasVO.class);
		data.setPk_corp(SystemUtil.getLoginCorpId()); 
		String operateType = StringUtil.nequals(param.get("type"), "add")?"修改":"新增";
		String cateName = param.get("cate_name");
		InventoryAliasVO vo = ic_invtoryaliasserv.save(data);
		json.setRows(vo);
		json.setMsg("保存成功");
		json.setSuccess(true);
		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "存货档案_"+operateType+"存货'"+cateName+"'别名：别名："+data.getAliasname()+";", ISysConstants.SYS_2);
		return ReturnData.ok().data(json);
	}

	@PostMapping("/onDelete")
	public ReturnData onDelete(@RequestBody Map<String, String> param){
		Json json = new Json();
		String pk_alias = param.get("pk_aliass");
		String cateName = param.get("cate_name");
		String aliasname =param.get("aliasname");

		String[] pkss = DZFStringUtil.getString2Array(pk_alias, ",");
		if (DZFValueCheck.isEmpty(pkss)){
			throw new BusinessException("数据为空,删除失败!");
		}
		ic_invtoryaliasserv.deleteByPks(pkss, SystemUtil.getLoginCorpId());
		json.setMsg("删除成功");
		json.setSuccess(true);
		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "删除存货别名",ISysConstants.SYS_2);
		return ReturnData.ok().data(json);
	}
}