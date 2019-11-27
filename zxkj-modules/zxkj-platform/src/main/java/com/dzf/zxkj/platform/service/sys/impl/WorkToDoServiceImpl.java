package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.jzcl.QmGzBgVo;
import com.dzf.zxkj.platform.model.sys.CorpRoleVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.WorkToDoVo;
import com.dzf.zxkj.platform.service.jzcl.IQmGzBgService;
import com.dzf.zxkj.platform.service.sys.ICorpURoleService;
import com.dzf.zxkj.platform.service.sys.IWorkToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("gl_work_todoserv")
public class WorkToDoServiceImpl implements IWorkToDoService {

    @Autowired
    private IQmGzBgService gl_qmgzbgserv;//期末关账报告

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private ICorpURoleService corpUserRoleImpl;

    @Override
    public void saveWorkTodo(WorkToDoVo todovo) throws DZFWarpException {

        if (StringUtil.isEmpty(todovo.getPk_corp())) {
            throw new BusinessException("公司信息为空");
        }

        if (StringUtil.isEmpty(todovo.getVcontent())) {
            throw new BusinessException("内容不能为空");
        }

        todovo.setIstatus(0);//待处理

        todovo.setDoperatedate(new DZFDateTime());

        singleObjectBO.saveObject(todovo.getPk_corp(), todovo);

    }

    @Override
    public void updateHandToDo(String id) throws DZFWarpException {

        if (StringUtil.isEmpty(id)) {
            throw new BusinessException("id信息为空");
        }

        WorkToDoVo vo = (WorkToDoVo) singleObjectBO.queryByPrimaryKey(WorkToDoVo.class, id);

        if (vo == null) {
            throw new BusinessException("处理失败,信息不存在!");
        }

        vo.setIstatus(1);// 已处理

        singleObjectBO.update(vo);

    }

    @Override
    public void delteTodo(String id) throws DZFWarpException {
        if (StringUtil.isEmpty(id)) {
            throw new BusinessException("id信息为空");
        }

        WorkToDoVo vo = (WorkToDoVo) singleObjectBO.queryByPrimaryKey(WorkToDoVo.class, id);

        if (vo == null) {
            throw new BusinessException("处理失败,信息不存在!");
        }

        singleObjectBO.deleteObject(vo);

    }

    @Override
    public List<QmGzBgVo> queryTodo(String pk_corp, String qj, String opetorid) throws DZFWarpException {
        CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);

        List<QmGzBgVo> reslist = new ArrayList<QmGzBgVo>();

        //财务完整性的
        handBgCwcl(pk_corp, qj, cpvo, reslist);

        //最后迭代待办事项
        handWorkTodo(pk_corp, opetorid, reslist);

        return reslist;
    }

    private void handWorkTodo(String pk_corp, String opetorid, List<QmGzBgVo> reslist) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(opetorid);
        sp.addParam(opetorid);
        String wherepart = " nvl(dr,0)=0 and pk_corp = ? and (coperatorid = ?  or vattentor = ? ) order by ts   ";
        WorkToDoVo[] todovos = (WorkToDoVo[]) singleObjectBO.queryByCondition(WorkToDoVo.class, wherepart, sp);
        if (todovos != null && todovos.length > 0) {
            for (WorkToDoVo todovo : todovos) {
                QmGzBgVo bgvo = new QmGzBgVo();
                bgvo.setId(todovo.getPk_work_todo());
                bgvo.setVmemo(todovo.getVcontent());
                bgvo.setZt(todovo.getIstatus());//状态
                reslist.add(bgvo);
            }
        }
    }

    private void handBgCwcl(String pk_corp, String qj, CorpVO cpvo, List<QmGzBgVo> reslist) {
        Map<String, List<QmGzBgVo>> qmgzbgmap = new LinkedHashMap<String, List<QmGzBgVo>>();

        String month = qj.substring(5, 7);

        gl_qmgzbgserv.handCwcl(qmgzbgmap, pk_corp, qj, cpvo);

        List<QmGzBgVo> bgvos = qmgzbgmap.get("cwcl");

        for (QmGzBgVo vo : bgvos) {
            if (vo.getIssuccess() == null || !vo.getIssuccess().booleanValue()) {
                vo.setZt(-1);
                vo.setVmemo(month + "月" + vo.getVmemo());
                reslist.add(vo);
            }
        }
    }

    @Override
    public List<CorpRoleVO> getPowUvos(String pk_corp) throws DZFWarpException {

        CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);

        ArrayList<CorpRoleVO> vos = corpUserRoleImpl.queryUserPower(cpvo.getFathercorp(), new String[]{pk_corp});

        List<CorpRoleVO> uvos = new ArrayList<CorpRoleVO>();

        Set<String> uid = new HashSet<String>();
        if (vos != null && vos.size() > 0) {
            for (CorpRoleVO vo : vos) {
                if (!uid.contains(vo.getCuserid())) {
                    uid.add(vo.getCuserid());
                    vo.setUser_name(CodeUtils1.deCode(vo.getUser_name()));
                    uvos.add(vo);
                }
            }
        }

        return uvos;
    }

}
