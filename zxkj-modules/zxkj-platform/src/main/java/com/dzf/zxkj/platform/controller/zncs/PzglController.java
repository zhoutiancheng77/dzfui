package com.dzf.zxkj.platform.controller.zncs;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageParamVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pjgl.PictureBrowseVO;
import com.dzf.zxkj.platform.service.pzgl.IPzglService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/gl/gl_pzglact")
public class PzglController extends BaseController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IPzglService gl_pzglserv;

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
}
