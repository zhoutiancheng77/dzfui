package com.dzf.zxkj.platform.controller.zcgl;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.sys.BdTradeAssetCheckVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.zcgl.IHyAssetCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 资产与总账对账[集团级 和公司级数据 同存在一个节点]
 *
 */
@RestController
@RequestMapping("/sys/sys_zczzdzbact")
@Slf4j
public class BdHyAssetCheckController extends BaseController {

	@Autowired
	private IHyAssetCheckService sys_zczzdzbserv ;


    @GetMapping("/query")
	public ReturnData<Grid> query(@MultiRequestBody CorpVO corpVo) {
		Grid grid = new Grid();
		try {
			List<BdTradeAssetCheckVO> list = null;
			list = sys_zczzdzbserv.queryAssCheckVOs(corpVo.getPk_corp(), "");
			grid.setTotal(list == null ? 0 : (long) list.size());
			grid.setRows(list == null ? new ArrayList<BdTradeAssetCheckVO>() : list);
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, e, "初始化失败");
		}

		writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL, "公司资产与总账对照表操作");

//		writeJson(grid);
        return ReturnData.ok().data(grid);
	}


	// 新增保存
    @PostMapping("/save")
	public ReturnData onSaveNew(@MultiRequestBody BdTradeAssetCheckVO data, @MultiRequestBody CorpVO corpVO , @MultiRequestBody UserVO uservo) {
		Json json = new Json();
		if (data != null) {
			data.setPk_corp(corpVO.getPk_corp());
			if (!corpVO.getPk_corp().equals(IGlobalConstants.currency_corp)) {
				data.setPk_trade_accountschema(corpVO.getCorptype());// 如果是公司的科目方案直接去公司的
			}

			try {
			    if(StringUtil.isEmpty(data.getPrimaryKey())){
                    data.setDoperatedate(new DZFDate());
                    data.setCoperatorid(uservo.getCuserid());
                    data = sys_zczzdzbserv.saveNew(data);
                }else{
                    sys_zczzdzbserv.update(data);
                }
				json.setSuccess(true);
				json.setMsg("保存成功");
			} catch (Exception e) {
				printErrorLog(json, e, "保存失败");
			}
		} else {
			json.setSuccess(false);
			json.setMsg("保存失败");
		}

		writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL,
				"新增资产与总账对照表数据");

        return ReturnData.ok().data(json);
	}


	// 删除记录
    @PostMapping("/delete")
	public ReturnData<Json> onDelete(@MultiRequestBody BdTradeAssetCheckVO data, @MultiRequestBody CorpVO corpVO ) {
		Json json = new Json();
		if (data != null) {
			try {
				if (corpVO.getPk_corp().equals(data.getPk_corp())) {
					data.setPk_corp(corpVO.getPk_corp());
                    sys_zczzdzbserv.deleteInfovo(data);
					json.setSuccess(true);
					json.setMsg("删除成功！");
				} else {
					json.setSuccess(false);
					json.setMsg("对不起，您无操作权限！");
				}
			} catch (Exception e) {
				printErrorLog(json, e, "删除失败");
			}
		} else {
			json.setSuccess(false);
			json.setMsg("删除失败!");
		}

		//记录日志
		writeLogRecord(LogRecordEnum.OPE_KJ_ZCGL,  "删除资产与总账对照表数据");
		return ReturnData.ok().data(json);
	}

}
