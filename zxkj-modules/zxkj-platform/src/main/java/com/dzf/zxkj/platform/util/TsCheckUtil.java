package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.utils.StringUtil;
import com.dzf.zxkj.base.exception.DZFWarpException;

public class TsCheckUtil {
    /**
     * 根据updatets 来判断是否变化
     * 如果vo的id为空则也是无变化
     * 当前传递vo的ts如果为空则默认是没有变化
     *
     * @param vo
     * @return
     * @throws DZFWarpException
     */
    public static boolean isDataChange(SuperVO vo) throws DZFWarpException {

        String id = vo.getPrimaryKey();

        if (!StringUtil.isEmpty(id)) {

            SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");

            SuperVO qryvo = singleObjectBO.queryByPrimaryKey(vo.getClass(), id);

            if (vo.getUpdatets() != null && !vo.getUpdatets().equals(qryvo.getUpdatets())) {//
                return true;//数据已变化
            }
        }

        return false;
    }
}
