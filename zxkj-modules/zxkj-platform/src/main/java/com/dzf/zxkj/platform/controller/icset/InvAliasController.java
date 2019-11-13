package com.dzf.zxkj.platform.controller.icset;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFStringUtil;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.service.glic.IInvAccAliasService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
public class InvAliasController {

	@Autowired
	private IInvAccAliasService ic_invtoryaliasserv = null;
	@GetMapping("/queryInv")
	public ReturnData query(@RequestParam Map<String, String> param){
		Grid grid = new Grid();
		String pk_inventory =param.get("pk_inventory");
		InventoryAliasVO[] vos = ic_invtoryaliasserv.query(SystemUtil.getLoginCorpId(),pk_inventory);
		if(vos != null && vos.length>0){
			grid.setRows(new ArrayList<InventoryAliasVO>(Arrays.asList(vos)));
			grid.setTotal(Long.valueOf(vos.length));
			grid.setMsg("查询成功");
		}else{
			grid.setTotal(0l);
			grid.setMsg("查询数据为空");
		}
		grid.setSuccess(true);
		return ReturnData.ok().data(grid);
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
		return ReturnData.ok().data(json);
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "存货档案_"+operateType+"存货'"+cateName+"'别名：别名："+data.getAliasname()+";", ISysConstants.SYS_2);
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
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "总账存货管理-存货档案，删除",ISysConstants.SYS_2);
		return ReturnData.ok().data(json);
	}
}