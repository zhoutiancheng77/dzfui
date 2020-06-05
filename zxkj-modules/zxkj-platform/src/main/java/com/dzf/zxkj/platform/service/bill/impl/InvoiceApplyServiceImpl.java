package com.dzf.zxkj.platform.service.bill.impl;

import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.IInvoiceApplyConstant;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.Base64CodeUtils;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bill.InvoiceApplyVO;
import com.dzf.zxkj.platform.model.piaotong.PiaoTongResVO;
import com.dzf.zxkj.platform.model.sys.CorpDocVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.YntArea;
import com.dzf.zxkj.platform.service.bill.IInvoiceApplyService;
import com.dzf.zxkj.platform.service.sys.IBDCorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.dzf.zxkj.platform.util.zncs.CommonXml;
import com.dzf.zxkj.platform.util.zncs.PiaoTongUtil;
import com.dzf.zxkj.secret.CorpSecretUtil;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service("gl_kpsqserv")
public class InvoiceApplyServiceImpl implements IInvoiceApplyService {

    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private IUserService userService;
    @Autowired
    private IBDCorpService corpService;
    @Autowired
    private FastDfsUtil  fastDfsUtil;

    @Override
    public List<InvoiceApplyVO> query(String userid) throws DZFWarpException {
        List<InvoiceApplyVO> list = new ArrayList<InvoiceApplyVO>();
        if(StringUtil.isEmpty(userid))
            return list;

        Set<String> corpSet = userService.querypowercorpSet(userid);
        if(corpSet == null || corpSet.size() == 0)
            return list;

        StringBuffer sf = new StringBuffer();
        sf.append(" select y.*, bd.innercode, bd.unitname, bd.vsoccrecode, bd.legalbodycode, ");
        sf.append("         bd.linkman2, bd.email1, bd.phone1, bd.vprovince, bd.postaddr  ");
        sf.append("   from ynt_invoice_apply y ");
        sf.append("   left join bd_corp bd ");
        sf.append("     on y.pk_corp = bd.pk_corp ");
        sf.append("  where nvl(y.dr, 0) = 0 and ");
        sf.append(SqlUtil.buildSqlForIn("y.pk_corp", corpSet.toArray(new String[0])));

        list = (List<InvoiceApplyVO>) singleObjectBO.executeQuery(sf.toString(), new SQLParameter(),
                new BeanListProcessor(InvoiceApplyVO.class));

        if(list == null || list.size() == 0){
            return new ArrayList<InvoiceApplyVO>();
        }

        QueryDeCodeUtils.decKeyUtils(new String[]{ "unitname", "phone1", "legalbodycode" },
                list, 1);

        Map<String, String[]> map = getMapByVprovince();
        String[] vproArr;
        for(InvoiceApplyVO vo : list){
            vproArr = map.get(vo.getVprovince() + "");
            if(vproArr != null){
                vo.setVprovname(vproArr[0]);
                vo.setVprovcode(vproArr[1]);
            }
        }
//        //查找营业执照相关
        buildCorpDoc(list);

        return list;
    }

    private void buildCorpDoc(List<InvoiceApplyVO> list) {
        if(list == null || list.size() == 0){
            return;
        }

        List<String> pks = new ArrayList<>();
        for(InvoiceApplyVO vo : list){
            pks.add(vo.getPk_corp());
        }

        StringBuffer sf = new StringBuffer();
        sf.append(" select y.pk_doc, y.pk_corp, y.doctemp, y.vfilepath, y.filetype from ynt_corpdoc y where nvl(dr,0)=0 and y.filetype = ? and ");
        sf.append(SqlUtil.buildSqlForIn("pk_corp", pks.toArray(new String[0])));

        SQLParameter sp = new SQLParameter();
        sp.addParam(IInvoiceApplyConstant.FILETYPE_2);//营业执照副本

        List<CorpDocVO> doclist = (List<CorpDocVO>) singleObjectBO.executeQuery(sf.toString(),
                sp, new BeanListProcessor(CorpDocVO.class));

        if(doclist == null || doclist.size() == 0){
            return;
        }

        Map<String, CorpDocVO> docMap = DZfcommonTools.hashlizeObjectByPk(doclist,
                new String[]{"pk_corp"});

        for(InvoiceApplyVO vo : list){
            if(docMap.containsKey(vo.getPk_corp())){
                vo.setFiletype(docMap.get(vo.getPk_corp()).getFiletype());
            }
        }

    }

    private Map<String, String[]> getMapByVprovince(){
        Map<String, String[]> map = new HashMap<>();
        map.put("2", new String[]{"北京市", "11"});
        map.put("3", new String[]{"天津市","12"});
        map.put("4", new String[]{"河北省","13"});
        map.put("5", new String[]{"山西省","14"});
        map.put("6", new String[]{"内蒙古自治区","15"});
        map.put("7", new String[]{"辽宁省","21"});
        map.put("8", new String[]{"吉林省","22"});
        map.put("9", new String[]{"黑龙江省","23"});
        map.put("10", new String[]{"上海市","31"});
        map.put("11", new String[]{"江苏省","32"});
        map.put("12", new String[]{"浙江省","33"});
        map.put("13", new String[]{"安徽省","34"});
        map.put("14", new String[]{"福建省","35"});
        map.put("15", new String[]{"江西省","36"});
        map.put("16", new String[]{"山东省","37"});
        map.put("17", new String[]{"河南省","41"});
        map.put("18", new String[]{"湖北省","42"});
        map.put("19", new String[]{"湖南省","43"});
        map.put("20", new String[]{"广东省","44"});
        map.put("21", new String[]{"广西壮族自治区","45"});
        map.put("22", new String[]{"海南省","46"});
        map.put("23", new String[]{"重庆市","50"});
        map.put("24", new String[]{"四川省","51"});
        map.put("25", new String[]{"贵州省","52"});
        map.put("26", new String[]{"云南省","53"});
        map.put("27", new String[]{"西藏自治区","54"});
        map.put("28", new String[]{"陕西省","61"});
        map.put("29", new String[]{"甘肃省","62"});
        map.put("30", new String[]{"青海省","63"});
        map.put("31", new String[]{"宁夏回族自治区","64"});
        map.put("32", new String[]{"新疆维吾尔自治区","65"});

        return map;
    }

    @Override
    public InvoiceApplyVO queryByGs(String pk_corp) throws DZFWarpException {

        String sql = " select * from ynt_invoice_apply y where y.pk_corp = ? and nvl(dr, 0) =0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);

        InvoiceApplyVO vo = (InvoiceApplyVO) singleObjectBO.executeQuery(sql, sp,
                new BeanProcessor(InvoiceApplyVO.class));

        return vo;
    }

    @Override
    public List<InvoiceApplyVO> save(String userid, String pk_corp, String[] gss) throws DZFWarpException{
        List<String> pks = new ArrayList(Arrays.asList(gss));
        List<InvoiceApplyVO> ovos = query(gss);
        if(ovos != null && ovos.size() > 0){
            for(InvoiceApplyVO vo : ovos){
                pks.remove(vo.getPk_corp());
            }
        }

        List<InvoiceApplyVO> cvos = new ArrayList<InvoiceApplyVO>();
        InvoiceApplyVO avo = null;
        for(String pk : pks){
            avo = new InvoiceApplyVO();
            avo.setPk_corp(pk);
            avo.setCuserid(userid);
            avo.setCreatedate(new DZFDate());
            avo.setIstatus(IInvoiceApplyConstant.APPLY_STATUS_0);
            avo.setImode(0);//自开模式

            cvos.add(avo);
        }

        String[] ids = singleObjectBO.insertVOArr(pk_corp, cvos.toArray(new InvoiceApplyVO[0]));
        for(int i = 0; i < ids.length; i++){
            cvos.get(i).setPrimaryKey(ids[i]);
        }

        return cvos;
    }

    public List<InvoiceApplyVO> query(String[] gss) throws DZFWarpException{
        StringBuffer sf = new StringBuffer();
        sf.append(" select * from ynt_invoice_apply y ");
        sf.append(" where nvl(dr,0) = 0 ");
        if(gss != null && gss.length > 0){
            sf.append(" and ").append(SqlUtil.buildSqlForIn("pk_corp", gss));
        }else{
            sf.append(" and 1!=1 ");
        }
        List<InvoiceApplyVO> list = (List<InvoiceApplyVO>) singleObjectBO.executeQuery(sf.toString(),
                new SQLParameter(), new BeanListProcessor(InvoiceApplyVO.class));

        return list;
    }

    @Override
    public PiaoTongResVO saveApply(String userid, InvoiceApplyVO vo) throws DZFWarpException{

        String pk_corp = vo.getPk_corp();
        CorpVO corpvo = corpService.queryByID(pk_corp);

        QueryDeCodeUtils.decKeyUtils(new String[]{ "unitname", "phone1", "legalbodycode" },
                new CorpVO[]{ corpvo }, 1);

        PiaoTongResVO resvo = request(vo, corpvo);

        if(resvo == null){
            throw new BusinessException("请求返回报错,请联系管理员");
        }
        String code = resvo.getCode();
        if(CommonXml.rtnfail_9004.equals(code)){
            //重新查询
            resvo = requestByBindingInfo(vo, corpvo);
            code = resvo.getCode();
            if(CommonXml.rtnsucccode.equals(code)){
                vo.setIstatus(IInvoiceApplyConstant.APPLY_STATUS_5);
                vo.setMemo(resvo.getMsg());
            }else{
                vo.setIstatus(IInvoiceApplyConstant.APPLY_STATUS_4);
                vo.setMemo(resvo.getMsg());
                vo.setMsg("请在票通平台绑定成功后，再查询开通状态");
            }

        }else if(CommonXml.rtnsucccode.equals(code)){
            vo.setIstatus(IInvoiceApplyConstant.APPLY_STATUS_3);//申请中
            vo.setMemo(resvo.getMsg());
        }else{
            vo.setIstatus(IInvoiceApplyConstant.APPLY_STATUS_4);//申请失败
            vo.setMemo(resvo.getMsg());
        }

        singleObjectBO.update(vo, new String[]{ "istatus", "memo" });

        return resvo;
    }

    private PiaoTongResVO requestByBindingInfo(InvoiceApplyVO vo, CorpVO corpvo){
        Map<String, String> conMap = new HashMap<String, String>();
        conMap.put("taxpayerNum", corpvo.getVsoccrecode());//销方纳税人识别号
        conMap.put("enterpriseName", corpvo.getUnitname());//销方企业名称

        String content = JsonUtils.serialize(conMap);

        PiaoTongResVO resVO = PiaoTongUtil.request(content, "/queryEnterpriseInfo.pt");

        return resVO;
    }

    private PiaoTongResVO request(InvoiceApplyVO vo, CorpVO corpvo){
        //查找营业执照
        String baseimg = getImg(corpvo.getPk_corp());
        //临时存放
        corpvo.setText(baseimg);
        corpvo.setCitycounty(vo.getVprovcode());
        //市区
        setAreaName(corpvo);
        //组装content
        String content = buildContent(corpvo);

        PiaoTongResVO resVO = PiaoTongUtil.request(content, "/register.pt");

        return resVO;
    }

    private void setAreaName(CorpVO corpvo){
        Integer city = corpvo.getVcity();
        if(city == null){
            throw new BusinessException("所属市为空,请检查");
        }
        String sql = "select * from ynt_area y where y.region_id = ? ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(city);

        YntArea area = (YntArea) singleObjectBO.executeQuery(sql, sp,
                new BeanProcessor(YntArea.class));

        if(area == null){
            throw new BusinessException("所属市不允许为空");
        }

        String name = area.getRegion_name();
        if("市辖区".equals(name)
                || "市".equals(name)
                || "县".equals(name)){

            Integer varea = corpvo.getVarea();
            if(varea == null){
                throw new BusinessException("所属区为空,请检查");
            }

            sp.clearParams();
            sp.addParam(varea);
            area = (YntArea) singleObjectBO.executeQuery(sql, sp,
                    new BeanProcessor(YntArea.class));

            if(area == null){
                throw new BusinessException("所属区不允许为空");
            }
        }

        corpvo.setDef1(area.getRegion_name());//临时存放
    }

    private String buildContent(CorpVO corpvo){
        Map<String, String> conMap = new HashMap<String, String>();
        conMap.put("taxpayerNum", corpvo.getVsoccrecode());//销方纳税人识别号
        conMap.put("enterpriseName", corpvo.getUnitname());//销方企业名称
        conMap.put("legalPersonName", corpvo.getLegalbodycode());//法人名称
        conMap.put("contactsName", corpvo.getLinkman2());//联系人名称
        conMap.put("contactsEmail", corpvo.getEmail1());//联系人邮箱
        conMap.put("contactsPhone", corpvo.getPhone1());//联系人手机号
        conMap.put("regionCode", corpvo.getCitycounty());//地区编码
        conMap.put("cityName", corpvo.getDef1());//市(区)名             def1临时存放字段
        conMap.put("enterpriseAddress", corpvo.getPostaddr());//详细地址
        // TODO 请修改为正确的图片Base64传
        conMap.put("taxRegistrationCertificate", corpvo.getText());//证件图片base64

        String content = JsonUtils.serialize(conMap);
        return content;
    }

    private String getImg(String pk_corp){
        String sql = "select * from ynt_corpdoc y where nvl(dr,0)=0 and y.pk_corp = ? and y.filetype = ?";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(IInvoiceApplyConstant.FILETYPE_2);
        List<CorpDocVO> list = (List<CorpDocVO>) singleObjectBO.executeQuery(sql, sp,
                new BeanListProcessor(CorpDocVO.class));
        if(list == null
                || list.size() == 0
                || StringUtil.isEmpty(list.get(0).getVfilepath()))
            throw new BusinessException("请上传营业执照");

        String sReturn = "";
        String path = list.get(0).getVfilepath();
        if(StringUtil.isEmpty(path)){
            return "";
        }
        //注意 此块和在线会计通用的方法不太一样
        if(path.contains("ImageUpload")){
            File f = new File(path);
            if (f.exists() && f.isFile()) {
                int byteread = 0;
                int bytesum = 0;

                FileInputStream inStream = null;
                ByteOutputStream bos = null;
                try {
                    inStream = new FileInputStream(f);

                    bos = new ByteOutputStream();
                    byte[] buffer = new byte[1444];
                    int length;
                    while ((byteread = inStream.read(buffer)) != -1) {
                        bytesum += byteread; // 字节数 文件大小
                        bos.write(buffer, 0, byteread);
                    }
                    bos.flush();
                    byte[] bs = bos.toByteArray();
//					sReturn = new String(bs, "utf-8");
                    sReturn = Base64CodeUtils.encode(bs);
                } catch (Exception e) {
                    throw new WiseRunException(e);
                } finally {
                    if (inStream != null) {
                        try {
                            inStream.close();
                        } catch (IOException ioe) {
                        }
                    }
                    if (bos != null)
                        bos.close();
                }
            }
        }else{
            try {
                byte[] bytes = fastDfsUtil.downFile(path);
                if (bytes != null && bytes.length > 0) {
                    sReturn = new String(bytes, "utf-8");
                }

            } catch (Exception e) {
            }
        }

        return sReturn;
    }

    @Override
    public List<InvoiceApplyVO> queryInviceByCode(String unitname, String vsoccrecode) throws DZFWarpException {
        //解密
        unitname = CorpSecretUtil.enCode(unitname);
        StringBuffer sf = new StringBuffer();
        sf.append(" select * ");
        sf.append("   from ynt_invoice_apply y ");
        sf.append("  where y.pk_corp in (select pk_corp ");
        sf.append("                        from bd_corp bd ");
        sf.append("                       where bd.unitname = ? ");
        sf.append("                         and bd.vsoccrecode = ? ");
        sf.append("                         and nvl(bd.dr, 0) = 0) ");

        SQLParameter sp = new SQLParameter();
        sp.addParam(unitname);
        sp.addParam(vsoccrecode);

        List<InvoiceApplyVO> list = (List<InvoiceApplyVO>) singleObjectBO.executeQuery(sf.toString(), sp,
                new BeanListProcessor(InvoiceApplyVO.class));

        return list;
    }

//    @Override
//    public void saveApply(String userid, String pk_corp, InvoiceApplyVO[] vos) throws DZFWarpException{
//        //往app发消息
//        List<MsgAdminVO> msgs = new ArrayList<MsgAdminVO>();
//        DZFDateTime time = new DZFDateTime();
//
//        String content;
//        MsgAdminVO msgvo;
//        for(InvoiceApplyVO vo : vos){
//            vo.setIstatus(IInvoiceApplyConstant.APPLY_STATUS_1);//企业主确认中
//            vo.setModifydatetime(time);
//
//            content = String.format("%s 公司提交了开票申请，请尽快处理",
//                    new String[] { vo.getUnitname()});
//            msgvo = convertVO(vo, time, userid, content);
//            msgs.add(msgvo);
//        }
//
//        singleObjectBO.insertVOArr(pk_corp, msgs.toArray(new MsgAdminVO[0]));
//
//        //更新单据状态
//        singleObjectBO.updateAry(vos, new String[]{ "istatus", "modifydatetime" });
//    }
//
//    private MsgAdminVO convertVO(InvoiceApplyVO vo, DZFDateTime time,
//                                 String userid, String content){
//        MsgAdminVO msgvo = new MsgAdminVO();
//        msgvo.setPk_corp(vo.getFathercorp());
//        msgvo.setCuserid(userid);
//        msgvo.setMsgtype(MsgtypeEnum.MSG_TYPE_KPFW.getValue());
//        msgvo.setMsgtypename(MsgtypeEnum.MSG_TYPE_KPFW.getName());
//        msgvo.setVcontent(content);
//        msgvo.setSendman(userid);
//        msgvo.setVsenddate(time.toString());
//        msgvo.setSys_send(ISysConstants.SYS_KJ);
//        msgvo.setVtitle(null);
//        msgvo.setIsread(DZFBoolean.FALSE);
//        msgvo.setPk_corpk(vo.getPk_corp());//小企业主信息
//        msgvo.setDr(0);
//        msgvo.setPk_bill(vo.getPk_invoice_apply());
//        return msgvo;
//    }
}
