package com.dzf.zxkj.report.service.cwbb.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.entity.LrbRptSetVo;
import com.dzf.zxkj.report.entity.ZcfzRptSetVo;
import com.dzf.zxkj.report.mapper.LrbRptSetMapper;
import com.dzf.zxkj.report.mapper.ZcfzRptSetMapper;
import com.dzf.zxkj.report.query.cwbb.LrbQueryVO;
import com.dzf.zxkj.report.service.cwbb.ILrbService;
import com.dzf.zxkj.report.service.cwbb.IRptSetService;
import com.dzf.zxkj.report.service.cwbb.IZcFzBService;
import com.dzf.zxkj.report.vo.cwbb.LrbVO;
import com.dzf.zxkj.report.vo.cwbb.ZcFzBVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class RptSetServiceImpl implements IRptSetService {

    @Autowired
    private ZcfzRptSetMapper zcfzRptSetMapper;
    @Autowired
    private IZcFzBService zcFzBService;
    @Autowired
    private LrbRptSetMapper lrbRptSetMapper;
    @Autowired
    private ILrbService lrbService;

    @Override
    public ZcfzRptSetVo[] queryZcfzRptVOs(String pk_trade_accountschema) throws Exception {
        if (StringUtil.isEmpty(pk_trade_accountschema)) {
            throw new RuntimeException("请求参数为空");
        }

        QueryWrapper<ZcfzRptSetVo> queryWrapper = new QueryWrapper();

        queryWrapper.lambda().eq(ZcfzRptSetVo::getPk_trade_accountschema, pk_trade_accountschema).and(condition -> condition.eq(ZcfzRptSetVo::getDr, "0").or().isNull(ZcfzRptSetVo::getDr));

        List<ZcfzRptSetVo> zcfzRptSetVoList = zcfzRptSetMapper.selectList(queryWrapper);

        return zcfzRptSetVoList.stream().toArray(ZcfzRptSetVo[]::new);
    }

    @Override
    public List<String> queryZcFzKmFromSetVo(String pk_trade_accountschema) throws Exception {
        List<String> list = new ArrayList<String>();
        ZcfzRptSetVo[] setvos = queryZcfzRptVOs(pk_trade_accountschema);
        if (setvos != null && setvos.length > 0) {
            for (ZcfzRptSetVo setvo : setvos) {
                //重分类的没考虑
                String zckms = setvo.getZckm();
                String fzkms = setvo.getFzkm();
                putListKms(list, zckms);
                putListKms(list, fzkms);
            }
        }
        return list;
    }

    private void putListKms(List<String> list, String zckms) {
        if (!StringUtil.isEmpty(zckms)) {
            String strs[] = zckms.split(",");
            for (String str : strs) {
                if (!StringUtil.isEmpty(str) && !list.contains(str)) {
                    list.add(str.substring(0, 4));
                }
            }
        }
    }

    @Override
    public List<String> queryZcfzKmFromDaima(String pk_corp, List<String> xmhcid, String[] hasyes) throws Exception {
        List<String> kmlist = new ArrayList<String>();
        if (hasyes == null || hasyes.length != 5 || !"Y".equals(hasyes[0])) {
            hasyes = new String[]{"N", "N", "N", "N", "N"};
        }
        ZcFzBVO[] bvos = zcFzBService.getZcfzVOs(pk_corp, hasyes, new HashMap<String, YntCpaccountVO>(), null);
        if (bvos != null && bvos.length > 0) {
            for (ZcFzBVO bvo : bvos) {
                if (xmhcid != null && xmhcid.size() > 0) {
                    if (!xmhcid.contains(bvo.getHc1())
                            && !xmhcid.contains(bvo.getHc2())) {
                        continue;
                    }
                }
                if (!StringUtil.isEmpty(bvo.getZcconkms())) {
                    putListKms(kmlist, bvo.getZcconkms());
                }
                if (!StringUtil.isEmpty(bvo.getFzconkms())) {
                    putListKms(kmlist, bvo.getFzconkms());
                }
            }
        }
        return kmlist;
    }

    @Override
    public String queryZcFzKmsToString(String pk_trade_accountschema) throws Exception {
        List<String> lists = queryZcFzKmFromSetVo(pk_trade_accountschema);
        StringBuffer buffer = listToString(lists);
        return buffer.toString();
    }

    private StringBuffer listToString(List<String> lists) {
        StringBuffer buffer = new StringBuffer();
        if (lists != null && lists.size() > 0) {
            for (String str : lists) {
                if (!StringUtil.isEmpty(str)) {
                    buffer.append(str + ",");
                }
            }
            if (buffer.length() > 0) {
                buffer.substring(0, buffer.length() - 1);
            }
        }
        return buffer;
    }

    @Override
    public LrbRptSetVo[] queryLrbRptVos(String pk_trade_accountschema) throws Exception {
        if (StringUtil.isEmpty(pk_trade_accountschema)) {
            throw new RuntimeException("请求参数为空");
        }

        QueryWrapper<LrbRptSetVo> queryWrapper = new QueryWrapper();

        queryWrapper.lambda().eq(LrbRptSetVo::getPk_trade_accountschema, pk_trade_accountschema).and(condition -> condition.eq(LrbRptSetVo::getDr, "0").or().isNull(LrbRptSetVo::getDr));

        List<LrbRptSetVo> zcfzRptSetVoList = lrbRptSetMapper.selectList(queryWrapper);

        return zcfzRptSetVoList.stream().toArray(LrbRptSetVo[]::new);
    }

    @Override
    public List<String> queryLrbKmsFromSetVo(String pk_trade_accountschema) throws Exception {
        List<String> list = new ArrayList<String>();
        LrbRptSetVo[] setvos = queryLrbRptVos(pk_trade_accountschema);
        if (setvos != null && setvos.length > 0) {
            for (LrbRptSetVo setvo : setvos) {
                String kms = setvo.getKm();
                String kms2 = setvo.getKm2();
                putListKms(list, kms);
                putListKms(list, kms2);

            }
        }
        return list;
    }

    @Override
    public List<String> queryLrbKmsFromDaima(CorpVO corpVO, List<String> xmid) throws Exception {
        List<String> kmlist = new ArrayList<String>();
        LrbVO[] lrbvos = lrbService.getLrbVos(new LrbQueryVO(), corpVO, new HashMap<>(), null, "");
        if (lrbvos != null && lrbvos.length > 0) {
            for (LrbVO lrbvo : lrbvos) {
                if (xmid != null && xmid.size() > 0) {
                    if (!xmid.contains(lrbvo.getHs())) {
                        continue;
                    }
                }
                if (!StringUtil.isEmpty(lrbvo.getVconkms())) {
                    putListKms(kmlist, lrbvo.getVconkms());
                }
            }
        }

        if ("00000100AA10000000000BMD".equals(corpVO.getCorptype())) {//13
            kmlist.add("3103");
        } else if ("00000100AA10000000000BMF".equals(corpVO.getCorptype())) {//07
            kmlist.add("4103");
        } else if ("00000100000000Ig4yfE0005".equals(corpVO.getCorptype())) {//企业会计制度
            return null;//企业会计制度暂不优化
//			kmlist.add("3131");
        }
        return kmlist;
    }

    @Override
    public String queryLrbKmsToString(String pk_trade_accountschema) throws Exception {
        List<String> lists = queryLrbKmsFromSetVo(pk_trade_accountschema);
        StringBuffer buffer = listToString(lists);
        return buffer.toString();
    }
}
