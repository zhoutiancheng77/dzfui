package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.ExrateVO;
import com.dzf.zxkj.platform.service.bdset.IHLService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("gl_bdhlserv")
@Slf4j
public class HlServiceImpl implements IHLService {

    private SingleObjectBO singleObjectBO = null;

    public SingleObjectBO getSingleObjectBO() {
        return singleObjectBO;
    }

    @Autowired
    public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
        this.singleObjectBO = singleObjectBO;
    }

    @Override
    public ExrateVO save(ExrateVO vo) throws DZFWarpException {
        if (existCheck(vo)) { //判断要新增或要改为的币种是否已存在（在"除了自己的其他汇率行"中是否存在）
            String sql = "select currencyname from ynt_bd_currency where pk_currency=?";
            SQLParameter sp = new SQLParameter();
            sp.addParam(vo.getPk_currency());
            Object obj = singleObjectBO.executeQuery(sql, sp, new ColumnProcessor());
            throw new BusinessException((obj != null ? (String) obj : "") + "的汇率已存在，不能重复添加");
        }
        //保存（新增或修改）
        ExrateVO svo = (ExrateVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
        return svo;
    }

    /**
     * 是否已经存在该币种的汇率
     * 用于后台校验等
     *
     * @param vo
     * @throws DZFWarpException
     */
    private boolean existCheck(ExrateVO vo) throws DZFWarpException {
        String id_rate = vo.getPk_exrate(); //修改时有id，新增时为空

        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_corp());
        sp.addParam(vo.getPk_currency());
        StringBuffer sf = new StringBuffer();
        sf.append("select 1 from ynt_exrate ");
        sf.append(" where (pk_corp=? or pk_corp='");
        sf.append(IGlobalConstants.DefaultGroup).append("')");
        // 要新增或要改为的币种在"除了自己的其他汇率行"中是否已存在
        sf.append(" and pk_currency=?");
        if (!StringUtil.isEmpty(id_rate)) {
            sf.append(" and pk_exrate!=?");
            sp.addParam(id_rate);
        }
        sf.append(" and nvl(dr,0)=0");
        boolean isExist = singleObjectBO.isExists(null, sf.toString(), sp);
        //List<ExrateVO> vo1 = (List<ExrateVO>) singleObjectBO.retrieveByClause(ExrateVO.class, sf.toString(), sp);
        return isExist;
    }

    @Override
    public List<ExrateVO> query(String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        StringBuffer sf = new StringBuffer();
        sf.append(" select rate.*, cy.currencyname, er.user_name creatorname from ynt_exrate rate ");
        sf.append(" join ynt_bd_currency cy on rate.pk_currency = cy.pk_currency ");
        sf.append(" left join sm_user er on rate.creator = er.cuserid ");
        sf.append(" where (rate.pk_corp = ? or rate.pk_corp ='");
        sf.append(IGlobalConstants.DefaultGroup);
        sf.append("'");
        sf.append(") and nvl(rate.dr,0) = 0 ");
        sf.append(" order by rate.pk_corp, rate.createtime ");
        List<ExrateVO> ancevos = (List<ExrateVO>) singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(ExrateVO.class));
        if (ancevos == null || ancevos.size() == 0)
            return null;
        return ancevos;
    }

    @Override
    public ExrateVO queryById(String id) throws DZFWarpException {
        ExrateVO vo = (ExrateVO) singleObjectBO.queryVOByID(id, ExrateVO.class);
        return vo;
    }

    @Override
    public void update(ExrateVO vo) throws DZFWarpException {
        singleObjectBO.update(vo);
    }

    @Override
    public void delete(ExrateVO vo) throws DZFWarpException {
        String sqlcorp = "select count(1) from  ynt_tzpz_b where pk_currency = ? and pk_corp = ? and nvl(dr,0) = 0 ";
        SQLParameter param = new SQLParameter();
        param.addParam(vo.getPk_currency());
        param.addParam(vo.getPk_corp());
        BigDecimal currencyCount = (BigDecimal) singleObjectBO.executeQuery(sqlcorp, param, new ColumnProcessor());
        if (currencyCount != null && currencyCount.intValue() > 0) {
            throw new BusinessException("凭证已引用,不能删除！");
        }
        singleObjectBO.deleteObject(vo);
    }
}
