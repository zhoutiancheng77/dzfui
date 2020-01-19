package com.dzf.zxkj.platform.controller.zncs;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageParamVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pjgl.PictureBrowseVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.pzgl.IPzglService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/zncs/gl_pzglact")
public class PzglController extends BaseController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IPzglService gl_pzglserv;
    @Autowired
    private IImageGroupService img_groupserv;

    // 根据条件查询图片
    @RequestMapping("/search")
    public ReturnData<Grid> search(@RequestBody ImageParamVO imgparamvo) {
        Grid grid = new Grid();
        try {
            Set<String> corpSet = userService.querypowercorpSet(SystemUtil.getLoginUserId());
            String pk_corp = imgparamvo.getPk_corp();
            if (!StringUtil.isEmpty(pk_corp)) {// pk_corp != null &&
                // pk_corp.trim().length() > 0
                if (!corpSet.contains(pk_corp)) {
                    grid.setSuccess(false);
                    grid.setMsg("查询图片失败,公司错误！");
                    return ReturnData.error().data(grid);
                }
            } else {
                imgparamvo.setPk_corp(SystemUtil.getLoginCorpId());
            }
            List<PictureBrowseVO> imageList = gl_pzglserv.search(imgparamvo);
            if (imageList != null && imageList.size() > 0) {
                for (PictureBrowseVO vo : imageList) {
                    vo.setUnitname(CodeUtils1.deCode(vo.getUnitname()));
                    vo.setUser_name(CodeUtils1.deCode(vo.getUser_name()));
                    // 如果pzdt凭证状态 为-1
                    if (vo.getPzdt() != null && vo.getPzdt() == -1) {
                        vo.setIstate(PhotoState.state101);
                    }
                }
                grid.setTotal((long) imageList.size());
                grid.setRows(imageList);
            }
            grid.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(grid,  e, "查询图片失败！");
        }
        return ReturnData.ok().data(grid);
    }

    // 图片删除操作,与重传不一样
    @RequestMapping("/delImage")
    public ReturnData<Json> delImage(@RequestBody Map<String,String> param) {
        String desc = param.get("desc");
        String delTelGrpDataString = param.get("delTelData");
        String delOthDataString = param.get("delOthData");
        String clzBidDateString = param.get("clzBidDate");
        String pk_corp = param.get("pk_corp");
        if(StringUtil.isEmpty(pk_corp)){
            pk_corp = SystemUtil.getLoginCorpId();
        }
        checkSecurityData(null,new String[]{pk_corp}, null);
        Json json = new Json();
        json.setSuccess(false);
        try {
            if(StringUtil.isEmpty(delTelGrpDataString)&&StringUtil.isEmpty(delOthDataString)
                    &&StringUtil.isEmpty(clzBidDateString)){
                throw new BusinessException("请选择要作废的票据");
            }
            String[] delTelGrpData = null;
            String[] delOthData = null;
            String[] clzBidDate = null;
            if(!StringUtil.isEmpty(delTelGrpDataString)){
                delTelGrpData = delTelGrpDataString.split(",");
            }
            if(!StringUtil.isEmpty(delOthDataString)){
                delOthData = delOthDataString.split(",");
            }
            if(!StringUtil.isEmpty(clzBidDateString)){
                clzBidDate = clzBidDateString.split(",");
            }

            Set<String> corpSet = userService.querypowercorpSet(SystemUtil.getLoginUserId());
            if (pk_corp != null && pk_corp.trim().length() > 0) {
                if (!corpSet.contains(pk_corp)) {
                    json.setSuccess(false);
                    json.setMsg("作废图片失败,公司错误！");
                    return ReturnData.error().data(json);
                }
            }

            int tellength = delTelGrpData == null ? 0 : delTelGrpData.length;
            int othlength = delOthData == null ? 0 : delOthData.length;
            int alllength = tellength + othlength;
            if (alllength == 0) {
                throw new BusinessException("没有选择图片，请确认！");
            }
            log.info("开始进行图片作废操作：" + alllength);

            img_groupserv.deleteImgFromTpll(pk_corp, SystemUtil.getLoginUserId(), desc, delTelGrpData, delOthData,clzBidDate);
            json.setRows("");
            json.setMsg("图片作废成功");
            json.setSuccess(true);
            log.info(alllength + "图片作废成功!");
        } catch (Exception e) {
            printErrorLog(json, e, "图片作废失败！");
        }
        return ReturnData.ok().data(json);
    }

}
