package com.dzf.zxkj.platform.controller.zcgl;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.zcgl.IZclbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sys/sys_zclbact")
@Slf4j
public class ZclbController extends BaseController {

    @Autowired
    private IZclbService sys_zclbserv;

    @GetMapping("/query")
    public ReturnData<Grid> query(@MultiRequestBody CorpVO corpVo) {
        Grid grid = new Grid();
        try {
            BdAssetCategoryVO[] vos = sys_zclbserv.queryAssetCategory(corpVo.getPk_corp());
            List<BdAssetCategoryVO> list = new ArrayList<BdAssetCategoryVO>(Arrays.asList(vos));
            grid.setRows(list);
            grid.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
        }
        return ReturnData.ok().data(grid);
    }

    //参照查询使用这个方法
    @GetMapping("/queryRef")
    public ReturnData<Grid> queryRef(@MultiRequestBody CorpVO corpVo) {
        List<BdAssetCategoryVO> list = null;
        try {
            BdAssetCategoryVO[] vos = sys_zclbserv.queryAssetCategoryRef(corpVo.getPk_corp());
            list = Arrays.asList(vos);
        } catch (Exception e) {
            log.error("查询失败", e);
        }
        Optional.ofNullable(list).orElse(new ArrayList<>());
        return ReturnData.ok().data(list);
    }

    @PostMapping("/save")
    public ReturnData<Json> save(@MultiRequestBody BdAssetCategoryVO vo, @MultiRequestBody CorpVO corpVO) {
        Json json = new Json();
        boolean isAdd = false;
        String msg = null;
        try {
            String pk_corp = corpVO.getPk_corp();
            sys_zclbserv.existCheck(vo, pk_corp);
            if (vo != null && msg == null) {
                if (StringUtil.isEmpty(vo.getPrimaryKey())) {
                    isAdd = true;
                }
//                FieldValidateUtils.Validate(vo);
                vo.setPk_corp(pk_corp);
                vo.setDr(0);
                vo.setDoperatedate(new DZFDate());
                sys_zclbserv.save(vo);
                json.setSuccess(true);
                json.setRows(vo);
                json.setMsg("成功");
            } else {
                if (msg != null)
                    json.setMsg(msg);
                else
                    json.setMsg("失败");
                json.setSuccess(false);
            }
        } catch (Exception e) {
            printErrorLog(json, e, "保存失败");
        }
        Integer logtype = LogRecordEnum.OPE_JITUAN_BDSET.getValue();
        Integer sysident = ISysConstants.SYS_0;
        if (!IDefaultValue.DefaultGroup.equals(corpVO.getPk_corp())) {
            logtype = LogRecordEnum.OPE_KJ_ZCGL.getValue();
            sysident = ISysConstants.SYS_2;
        }
//        if (json.isSuccess()) {
//            if (isAdd) {
//                writeLogRecord(logtype,"新增资产类别:" + data.getCatename(), sysident);
//            } else {
//                writeLogRecord(logtype, "修改资产类别:" + data.getCatename(), sysident);
//            }
//        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/delete")
    public ReturnData<Json> delete(@MultiRequestBody BdAssetCategoryVO vo, @MultiRequestBody CorpVO corpVO) {
        BdAssetCategoryVO checkVo = sys_zclbserv.queryAssetCategoryByPrimaryKey(vo);
        Json json = new Json();
        if (vo != null) {
            try {
                String pk_corp = corpVO.getPk_corp();
                sys_zclbserv.delete(vo, pk_corp);
                json.setSuccess(true);
                json.setRows(vo);
                json.setMsg("成功");
            } catch (Exception e) {
                printErrorLog(json, e, "删除失败");
            }
        } else {
            json.setSuccess(false);
            json.setMsg("失败");
        }
//        Integer logtype = LogRecordEnum.OPE_JITUAN_BDSET.getValue();
//        Integer sysident = ISysConstants.SYS_0;
//        if (!IDefaultValue.DefaultGroup.equals(corpVO.getPk_corp())) {
//            logtype = LogRecordEnum.OPE_KJ_ZCGL.getValue();
//            sysident = ISysConstants.SYS_2;
//        }
//        if (json.isSuccess()) {
//            writeLogRecord(logtype, "删除资产类别:" + checkVo.getCatename(), sysident);
//        }
        return ReturnData.ok().data(json);
    }

}
