package com.dzf.zxkj.platform.services.sys.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.enums.MsgtypeEnum;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.ImageLibraryVO;
import com.dzf.zxkj.platform.model.image.ImageRecordVO;
import com.dzf.zxkj.platform.model.image.ImageTurnMsgVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.JPMessageBean;
import com.dzf.zxkj.platform.model.sys.MsgAdminVO;
import com.dzf.zxkj.platform.services.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.services.sys.IAccountService;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import com.dzf.zxkj.platform.services.sys.IMsgService;
import com.dzf.zxkj.platform.services.sys.ISysMessageJPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("sys_msgtzserv")
@Slf4j
public class MsgServiceImpl implements IMsgService {


    @Autowired
    private ISysMessageJPush sysmsgsrv;//推送

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private IImageGroupService gl_pzimageserv;

    @Autowired
    private ICorpService corpService;
    @Autowired
    private IAccountService accountService;

    @Override
    public void saveMsgVoFromImage(String pk_corp, String currid, ImageTurnMsgVO vo, ImageGroupVO grouvo) throws DZFWarpException {
        try {
            if (grouvo == null || StringUtil.isEmpty(grouvo.getPrimaryKey())) {
                return;
            }

            grouvo = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, grouvo.getPrimaryKey());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

            MsgAdminVO msgvo = new MsgAdminVO();
            msgvo.setPk_corp("");
            msgvo.setCuserid(grouvo.getCoperatorid());
            String content = null;
            String opedate = sdf.format(new Date(grouvo.getTs().getMillis()));
            String opemsg = null;
            String currope = null;
            CorpVO cpvo = corpService.queryByPk(grouvo.getPk_corp());
            if (grouvo.getIstate().intValue() == PhotoState.state80) {//图片退回
                msgvo.setMsgtype(MsgtypeEnum.MSG_TYPE_TPTHTZ.getValue());
                msgvo.setMsgtypename(MsgtypeEnum.MSG_TYPE_TPTHTZ.getName());
                if (!StringUtil.isEmpty(grouvo.getCoperatorid()) && !grouvo.getCoperatorid().startsWith("appuse")
                        && !grouvo.getCoperatorid().startsWith("webuse")) {
                    String mny = grouvo.getMny() == null ? "0.00"
                            : grouvo.getMny().setScale(2, DZFDouble.ROUND_HALF_UP).toString();
                    long count = gl_pzimageserv.queryBackLibCount(grouvo.getPk_corp(), grouvo.getPk_image_group());
                    content = String.format("您%s上传的%s(公司)%s(图片编码)%s(金额)的%d(图片张数)被退回",
                            opedate, cpvo.unitname, grouvo.getGroupcode(), mny, count
                    );
                } else {
                    content = "您" + opedate + "上传的图片被退回，请点击查看。";
                }
                opemsg = StringUtil.isEmpty(vo.getMessage()) ? "您上传的图片被退回!" : "退回原因:" + vo.getMessage();
                currope = "return";
                currid = vo.getCoperatorid();
                //推送消息
                sendJgMessage(vo, grouvo);
            } else if (grouvo.getIstate().intValue() == PhotoState.state100) {//图片制证
                if (StringUtil.isEmpty(grouvo.getCoperatorid())
                        || !grouvo.getCoperatorid().startsWith("appuse")
                        || !grouvo.getCoperatorid().startsWith("webuse")) {//非app和webuse的账号，不让发送消息
                    return;
                }
                msgvo.setMsgtype(MsgtypeEnum.MSG_TYPE_TPZZTZ.getValue());
                msgvo.setMsgtypename(MsgtypeEnum.MSG_TYPE_TPZZTZ.getName());
                content = "您" + opedate + "上传的图片已制证，请点击查看。";
                opemsg = "您上传的图片已制证!";
                currope = "voucher";
            } else {
                return;
            }

            String[] currs = new String[]{currid, ""};
            String[] nextid = new String[]{"", ""};
            saveImageRecord(grouvo.getPrimaryKey(), vo != null ? vo.getPrimaryKey() : "",
                    currs, nextid, currope, pk_corp, "", opemsg);
            msgvo.setVcontent(content);
            msgvo.setSendman(grouvo.getCoperatorid());
            msgvo.setVsenddate(new DZFDateTime().toString());
            msgvo.setSys_send(ISysConstants.SYS_ADMIN);
            msgvo.setVtitle(null);
            msgvo.setIsread(DZFBoolean.FALSE);
            msgvo.setPk_corpk(grouvo.getPk_corp());//小企业主信息
            msgvo.setDr(0);
            msgvo.setPk_bill(grouvo.getPrimaryKey());

            singleObjectBO.saveObject(grouvo.getPk_corp(), msgvo);
        } catch (Exception e) {
            log.error("错误", e);
        }
    }

    private void sendJgMessage(ImageTurnMsgVO vo, ImageGroupVO groupVO) {
        try {
            if (vo == null) {
                return;
            }

            if (groupVO.getSourcemode() != null
                    && groupVO.getSourcemode() == PhotoState.SOURCEMODE_01) {// 手机端来源的
                vo = (ImageTurnMsgVO) singleObjectBO.queryByPrimaryKey(ImageTurnMsgVO.class, vo.getPrimaryKey());
                JPMessageBean bean = new JPMessageBean();
                Map<String, String> extras = new HashMap<String, String>();

                String pk_image_lib = vo.getPk_image_librarys();
                StringBuffer badimagepath = new StringBuffer();//图片退回信息

                //考虑到历史数据问题
                if (StringUtil.isEmpty(pk_image_lib) || pk_image_lib.equals(vo.getPk_image_group())) {
                    ImageLibraryVO[] libgrpvos = (ImageLibraryVO[]) singleObjectBO.queryByCondition(
                            ImageLibraryVO.class, "   pk_image_group ='" + vo.getPk_image_group() + "'",
                            new SQLParameter());

                    for (ImageLibraryVO lib : libgrpvos) {
                        badimagepath.append(lib.getImgpath().replace("\\\\", "/") + ",");
                    }
                } else {
                    String[] libs = null;
                    if (!StringUtil.isEmpty(pk_image_lib)) {
                        libs = pk_image_lib.split(",");
                    }
                    if (libs != null && libs.length > 0) {
                        StringBuffer wherepklib = new StringBuffer();
                        for (String str : libs) {
                            wherepklib.append("'" + str + "',");
                        }
                        ImageLibraryVO[] libvos = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class, "pk_image_library in(" + wherepklib.substring(0, wherepklib.length() - 1) + ")", new SQLParameter());
                        badimagepath.append(libvos[0].getImgpath().replace("\\\\", "/") + ",");
                    }
                }

                String settlemode = groupVO.getSettlemode() == null ? "" : groupVO.getSettlemode();
                DZFDate doprate = groupVO.getDoperatedate();
                String msg = vo.getMessage() == null ? ""
                        : new StringBuffer().append("您").append(doprate.getYear()).append("年")
                        .append(doprate.getMonth()).append("月").append(doprate.getDay()).append("日")
                        .append("上传的图片被退回:").append(vo.getMessage()).toString();
                String dealtime = vo.getTs().toString();
                String zy = groupVO.getMemo() == null ? "" : groupVO.getMemo();
                extras.put("bread", "N");
                extras.put("settle", settlemode);
                if (!StringUtil.isEmpty(groupVO.getPk_image_group())) {
                    extras.put("groupImagePaths", groupVO.getPk_image_group());
                } else {
                    extras.put("groupImagePaths", "");
                }
                extras.put("updatetime", groupVO.getTs().toString());
                extras.put("message", msg);
                extras.put("pk_corp", groupVO.getPk_corp());
                extras.put("key", vo.getPk_image_returnmsg());
                extras.put("dealtime", dealtime);
                if (badimagepath.length() > 0) {
                    extras.put("badImagePaths", "[" + badimagepath.substring(0, badimagepath.length() - 1) + "]");
                } else {
                    extras.put("badImagePaths", "");
                }
                extras.put("zy", zy);
                bean.setExtras(extras);
                bean.setUserids(new String[]{groupVO.getCoperatorid()});

                bean.setMessage(msg);
                sysmsgsrv.sendSysMessage(bean);
            }
        } catch (Exception e) {
            log.error("错误", e);
        }

    }

    @Override
    public void deleteMsg(ImageGroupVO grouvo) throws DZFWarpException {

        if (grouvo == null || StringUtil.isEmpty(grouvo.getPrimaryKey())) {
            return;
        }

        grouvo = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, grouvo.getPrimaryKey());

        if (grouvo.getIstate().intValue() == PhotoState.state0) {
            if (StringUtil.isEmpty(grouvo.getPrimaryKey()) || StringUtil.isEmpty(grouvo.getPk_corp())) {
                return;
            }
            SQLParameter sp = new SQLParameter();
            String delsql = "delete from ynt_msg_admin  where pk_bill = ? and pk_corpk = ? and msgtype in('30','31')";
            sp.addParam(grouvo.getPrimaryKey());
            sp.addParam(grouvo.getPk_corp());
            singleObjectBO.executeUpdate(delsql, sp);
        }


    }

    @Override
    public void saveImageRecord(String pk_image_group,
                                String pk_source_id, String[] currs, String[] nextid, String currope,
                                String pk_corp, String pk_temp_corp, String vmemo) throws DZFWarpException {
        ImageRecordVO recordvo = new ImageRecordVO();

        recordvo.setPk_corp(pk_corp);
        recordvo.setPk_temp_corp(pk_temp_corp);
        recordvo.setVcurapprovetor(currs[0]);
        recordvo.setVcurrole(currs[1]);
        recordvo.setVnexapprovetor(nextid[0]);
        recordvo.setVnextrole(nextid[1]);
        recordvo.setDoperatedate(new DZFDateTime());
        recordvo.setVapproveope(currope);
        recordvo.setVapprovemsg(vmemo);
        recordvo.setPk_image_group(pk_image_group);
        recordvo.setPk_source_id(pk_source_id);

        singleObjectBO.saveObject(pk_corp == null ? Common.tempidcreate : pk_corp, recordvo);

    }


    @Override
    public void newSaveMsgVoFromImage(String pk_corp, TzpzHVO headVO, ImageGroupVO grpvo) throws DZFWarpException {
        try {
            if (headVO == null || StringUtil.isEmpty(headVO.getPk_image_group())) {
                return;
            }

            ImageTurnMsgVO imageTurnMsgVO = new ImageTurnMsgVO();
            imageTurnMsgVO.setCoperatorid(headVO.getCoperatorid());
            imageTurnMsgVO.setDoperatedate(new DZFDate(new Date()));
            imageTurnMsgVO.setMessage("余额不足，请联系管理员！");
            imageTurnMsgVO.setPk_corp(headVO.getPk_corp());
            imageTurnMsgVO.setPk_image_group(headVO.getPk_image_group());
            imageTurnMsgVO.setPk_image_librarys(headVO.getPk_image_library());

            ImageGroupVO grouvo = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class,
                    headVO.getPk_image_group());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

            MsgAdminVO msgvo = new MsgAdminVO();
            msgvo.setPk_corp("");
            msgvo.setCuserid(grouvo.getCoperatorid());
            String opedate = sdf.format(new Date(grouvo.getTs().getMillis()));
            msgvo.setMsgtype(MsgtypeEnum.MSG_TYPE_CHARGE_NOTICE.getValue());
            msgvo.setMsgtypename(MsgtypeEnum.MSG_TYPE_CHARGE_NOTICE.getName());
            String content = "您于" + opedate + "余额不足，请联系管理员！";
            String opemsg = "余额不足，请联系管理员！";
            String currope = "voucher";

            String[] currs = new String[]{grouvo.getCoperatorid(), ""};
            String[] nextid = new String[]{"", ""};
            saveImageRecord(grouvo.getPrimaryKey(), "", currs, nextid, currope, pk_corp, "", opemsg);
            msgvo.setVcontent(content);
            msgvo.setSendman(grouvo.getCoperatorid());
            msgvo.setVsenddate(new DZFDateTime().toString());
            msgvo.setSys_send(ISysConstants.SYS_KJ);
            msgvo.setVtitle(null);
            msgvo.setIsread(DZFBoolean.FALSE);
            msgvo.setPk_corpk(grouvo.getPk_corp());// 小企业主信息
            msgvo.setDr(0);
            msgvo.setPk_bill(grouvo.getPrimaryKey());

            singleObjectBO.saveObject(grouvo.getPk_corp(), msgvo);
        } catch (Exception e) {
            log.error("错误", e);
        }
    }

}
