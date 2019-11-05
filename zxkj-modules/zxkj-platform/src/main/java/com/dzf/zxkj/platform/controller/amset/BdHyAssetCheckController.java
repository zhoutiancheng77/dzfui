package com.dzf.zxkj.platform.controller.amset;

import com.dzf.zxkj.common.base.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 资产与总账对账[集团级 和公司级数据 同存在一个节点]
 *
 */
@RestController
@RequestMapping("/sys/sys_zczzdzbact")
@Slf4j
public class BdHyAssetCheckController extends BaseController {

//	@Autowired
//	private IHyAssetCheckService services = null;
//
//	private String kmfaid;
//
//	public IHyAssetCheckService getServices() {
//		return services;
//	}
//
//	@Autowired
//	public void setServices(IHyAssetCheckService services) {
//		this.services = services;
//	}
//
//	public void InitListInfo() {
//		Grid grid = new Grid();
//		try {
//			List<BdTradeAssetCheckVO> list = null;
//			CorpVO corpvo = getLoginCorpInfo();
//			list = getServices().queryAssCheckVOs(corpvo.getPk_corp(), kmfaid);
//			grid.setTotal(list == null ? 0 : (long) list.size());
//			grid.setRows(list == null ? new ArrayList<BdTradeAssetCheckVO>() : list);
//			grid.setSuccess(true);
//		} catch (Exception e) {
//			printErrorLog(grid, log, e, "初始化失败");
//		}
//
//		writeLogRecord(getLogType(), "公司资产与总账对照表操作", getSysIdent());
//
//		writeJson(grid);
//	}
//
//	// 新增保存
//	public void onSaveNew() {
//		Json json = new Json();
//		if (data != null) {
//			data = getActionVO(BdTradeAssetCheckVO.class);
//			data.setPk_corp(getLoginCorpInfo().getPk_corp());
//			if (!getLoginCorpInfo().getPk_corp().equals(IGlobalConstants.currency_corp)) {
//				data.setPk_trade_accountschema(getLoginCorpInfo().getCorptype());// 如果是公司的科目方案直接去公司的
//			}
//			data.setDoperatedate(new DZFDate());
//			data.setCoperatorid(getLoginUserInfo().getCuserid());
//			try {
//				data = getServices().saveNew(data);
//				json.setSuccess(true);
//				json.setMsg("保存成功");
//			} catch (Exception e) {
//				printErrorLog(json, log, e, "保存失败");
//			}
//		} else {
//			json.setSuccess(false);
//			json.setMsg("保存失败");
//		}
//
//		writeLogRecord(getLogType(),
//				"新增资产与总账对照表数据", getSysIdent());
//
//		writeJson(json);
//	}
//
//	// 修改保存
//	public void onEditSave() {
//		Json json = new Json();
//		if (data != null) {
//			try {
//				if (getLoginCorpInfo().getPk_corp().equals(data.getPk_corp())) {
//					data = getActionVO(BdTradeAssetCheckVO.class);
//					// data.setPk_corp(getLoginCorpInfo().getPk_corp());
//					if (!getLoginCorpInfo().getPk_corp().equals(IGlobalConstants.currency_corp)) {
//						data.setPk_trade_accountschema(getLoginCorpInfo().getCorptype());// 如果是公司的科目方案直接去公司的
//					}
//					getServices().update(data);
//					json.setSuccess(true);
//					json.setMsg("保存成功");
//				} else {
//					json.setSuccess(false);
//					json.setMsg("对不起，您无操作权限！");
//				}
//			} catch (Exception e) {
//				printErrorLog(json, log, e, "保存失败");
//			}
//		} else {
//			json.setSuccess(false);
//			json.setMsg("保存失败");
//		}
//
//		//记录日志
//		writeLogRecord(getLogType(), "修改资产与总账对照表数据", getSysIdent());
//
//		writeJson(json);
//	}
//
//	// 删除记录
//	public void onDelete() {
//		Json json = new Json();
//		if (data != null) {
//			try {
//				if (getLoginCorpInfo().getPk_corp().equals(data.getPk_corp())) {
//					data = getActionVO(BdTradeAssetCheckVO.class);
//					data.setPk_corp(getLoginCorpInfo().getPk_corp());
//					getServices().deleteInfovo(data);
//					json.setSuccess(true);
//					json.setMsg("删除成功！");
//				} else {
//					json.setSuccess(false);
//					json.setMsg("对不起，您无操作权限！");
//				}
//			} catch (Exception e) {
//				printErrorLog(json, log, e, "删除失败");
//			}
//		} else {
//			json.setSuccess(false);
//			json.setMsg("删除失败!");
//		}
//
//		//记录日志
//		writeLogRecord(getLogType(),  "删除资产与总账对照表数据",getSysIdent());
//
//		writeJson(json);
//	}
//
//	public String getKmfaid() {
//		return kmfaid;
//	}
//
//	public void setKmfaid(String kmfaid) {
//		this.kmfaid = kmfaid;
//	}
//
//
//	private Integer getLogType(){
//		Integer logtype = LogRecordEnum.OPE_JITUAN_PZMB.getValue();
//        if (!IDefaultValue.DefaultGroup.equals(getLogincorppk())) {
//        	logtype = LogRecordEnum.OPE_KJ_ZCGL.getValue();
//        }
//        return logtype;
//	}
//
//	private Integer getSysIdent(){
//        Integer sysident = ISysConstants.SYS_0;
//        if (!IDefaultValue.DefaultGroup.equals(getLogincorppk())) {
//        	sysident = ISysConstants.SYS_2;
//        }
//        return sysident;
//	}
}
