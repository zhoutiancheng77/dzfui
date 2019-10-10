package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.vo.cwzb.FkTjSetVO;

public interface IFkTjBgService {
    /**
     * 查询
     * @param pk_corp
     * @param begdate
     * @param enddate
     * @return
     * @throws Exception
     */
    FkTjSetVO[] query(String pk_corp, DZFDate begdate, DZFDate enddate) throws Exception;

    /**
     * 保存
     * @param vo
     * @throws Exception
     */
    void save(FkTjSetVO vo) throws Exception;

    /**
     *  计算增值税
     * @param year
     * @param cpvo
     * @return
     * @throws Exception
     */
    Object[] queryZzsBg(String year, CorpVO cpvo) throws Exception;

    /**
     * 计算所得税
     * 季度税负：本季度的税负率=(所得税*25%）/（本季度营业收入）*100%
     * 	累计税负：当前季度累计的税负率=(所得税*25%）/（本年营业收入）*100%
     * @param year
     * @param cpvo
     * @return
     * @throws Exception
     */
    Object[] querySdsBg(String year, CorpVO cpvo) throws Exception;

    /**
     * 风控体检报告
     * @param enddate
     * @param cpvo
     * @return
     * @throws Exception
     */
    FkTjSetVO[] queryFktj(DZFDate enddate, CorpVO cpvo) throws Exception;
}
