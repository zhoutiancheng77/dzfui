package com.dzf.zxkj.platform.controller.jzcl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.jzcl.AccountQryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.jzcl.IAccountQryService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("workbench/accountqry")
@Slf4j
public class AccountQryController {
    @Autowired
    private IAccountQryService accqryser;
    @Autowired
    private ICorpService corpService;

    /**
     * 查询
     */
    @PostMapping("query")
    public ReturnData<Grid> queryJzStatus(@MultiRequestBody QueryParamVO pamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO) {
        Grid<List<AccountQryVO>> grid = new Grid();
        try {
            pamvo.setFathercorp(corpVO.getFathercorp());
            CorpVO fcorpvo = corpService.queryByPk(pamvo.getFathercorp());
            boolean ischannel = false;
            if (fcorpvo != null && fcorpvo.getIschannel() != null && fcorpvo.getIschannel().booleanValue()) {
                ischannel = true;
            }
            if (StringUtil.isEmpty(pamvo.getCorpname())) {//不根据客户名称或编码过滤时，采用数据库分页
                int total = accqryser.queryTotalRow(pamvo, userVO, ischannel);
                if (total > 0) {
                    List<AccountQryVO> workvos = accqryser.query(pamvo, userVO, ischannel);
                    grid.setRows(workvos);
                } else {
                    grid.setRows(new ArrayList<AccountQryVO>());
                }
                grid.setSuccess(true);
                grid.setTotal((long) (total));
                grid.setMsg("查询成功");
            } else {//根据客户名称或编码过滤时，采用代码分页
                List<AccountQryVO> list = accqryser.queryAllData(pamvo, userVO, ischannel);
                int page = pamvo == null ? 1 : pamvo.getPage();
                int rows = pamvo == null ? 10000 : pamvo.getRows();
                int len = list == null ? 0 : list.size();
                if (len > 0) {
                    grid.setTotal((long) (len));
                    AccountQryVO[] jzVOs = list.toArray(new AccountQryVO[0]);
                    jzVOs = (AccountQryVO[]) getPagedVOs(jzVOs, page, rows);
                    grid.setRows(Arrays.asList(jzVOs));

                } else {
                    grid.setTotal(Long.valueOf(0));
                    grid.setRows(new ArrayList<AccountQryVO>());
                }
                grid.setSuccess(true);
                grid.setMsg("查询成功");
            }
            if (grid.isSuccess() && grid.getRows() != null && grid.getRows().size() > 0) {
                grid.setRows(accqryser.queryYjxxByMulti(grid.getRows(), pamvo.getQjq()));
            }
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                grid.setMsg(e.getMessage());
            } else {
                grid.setMsg("查询失败");
                log.error("查询失败", e);
            }
            grid.setSuccess(false);
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 将查询后的结果分页
     *
     * @param cvos
     * @param page
     * @param rows
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static SuperVO[] getPagedVOs(SuperVO[] cvos, int page, int rows) throws DZFWarpException {
        int beginIndex = rows * (page - 1);
        int endIndex = rows * page;
        if (endIndex >= cvos.length) {//防止endIndex数组越界
            endIndex = cvos.length;
        }
        cvos = Arrays.copyOfRange(cvos, beginIndex, endIndex);
        return cvos;
    }
}
