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
import com.dzf.zxkj.platform.model.sys.BDabstractsVO;
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
        ExrateVO svo = (ExrateVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
        return svo;
    }

    /**
     * 是否已经存在该币种的汇率
     *
     * @param vo
     * @throws DZFWarpException
     */
    private void existCheck(BDabstractsVO vo) throws DZFWarpException {
        SQLParameter sp1 = new SQLParameter();
        String code = vo.getAbstractscode();
        String name = vo.getAbstractsname();
        String id = "";
        if (!StringUtil.isEmpty(vo.getPk_abstracts())) {
            id = vo.getPk_abstracts();
        }
        sp1.addParam(code);
        sp1.addParam(name);
        if (!StringUtil.isEmpty(id)) {
            sp1.addParam(id);
            sp1.addParam(id);
        }
        sp1.addParam(vo.getPk_corp());
        sp1.addParam(vo.getPk_corp());
        String condition1 = " (abstractscode = ? or abstractsname = ?) ";
        if (StringUtil.isEmpty(id) == false)
            condition1 = condition1 + " and (pk_abstracts>? or pk_abstracts<?) ";
        condition1 = condition1 + " and pk_corp  in ((select fathercorp from bd_corp where pk_corp = ? ), ? ) and nvl(dr,0) = 0 ";
        List<BDabstractsVO> vo1 = (List<BDabstractsVO>) singleObjectBO.retrieveByClause(BDabstractsVO.class, condition1, sp1);
        int len = vo1 == null ? 0 : vo1.size();
        if (len > 0) {
            String str = vo1.get(0).getAbstractscode();
            if (code.equals(str))
                throw new BusinessException("摘要编号已存在");
            else throw new BusinessException("摘要名称已存在");
        }
    }

    @Override
    public List<ExrateVO> query(String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        StringBuffer sf = new StringBuffer();
        sf.append(" select rate.*,cy.currencyname, er.user_name creatorname from  ynt_exrate rate ");
        sf.append(" join ynt_bd_currency cy on rate.pk_currency = cy.pk_currency  ");
        sf.append(" left join sm_user er on rate.creator = er.cuserid ");
        sf.append(" where (rate.pk_corp = ? or rate.pk_corp ='");
        sf.append(IGlobalConstants.DefaultGroup);
        sf.append("'");
        sf.append(") and nvl(rate.dr,0) = 0");
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
