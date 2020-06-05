package com.dzf.zxkj.backup.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.zxkj.backup.service.ICorpService;
import com.dzf.zxkj.backup.service.IDataBackUp;
import com.dzf.zxkj.backup.service.ISecretKeyService;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.utils.JSONConvtoJAVA;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.BackupVO;
import com.dzf.zxkj.platform.model.secret.ISourceSys;
import com.dzf.zxkj.platform.model.secret.SecretKeyVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.IBackAndRestore;
import com.dzf.zxkj.platform.model.tax.TaxReportDetailVO;
import com.dzf.zxkj.platform.model.tax.TaxReportInitVO;
import com.dzf.zxkj.secret.CorpSecretUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 数据备份(还原)
 *
 * @author zhangj
 */
@Service
@Slf4j
@SuppressWarnings("all")
public class DataBackUpImpl implements IDataBackUp {

//	private static final StringBuffer publickey = new StringBuffer();
//	private static final StringBuffer privatekey = new StringBuffer();

    private static final int BUFFER_SIZE = 4 * 1024;
//	static {
//		publickey.append("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDJr/0/8LM3Z9Pdd9A4ywuLfLx0fLKkrbXixhSa\n");
//		publickey.append("vLRGNW+OhoacdcvVi4EPn+1cTFTtK40LgoxAhjKCwH18zpbOqzs9asPqX0gjZ6BmO+okuDq2nOS1\n");
//		publickey.append("6eUfkq7cV8n+PHVr0Afm795r/T1H4n8glXKRSyzA20hiqF0kQDIVMeo+0wIDAQAB");
//
//		privatekey.append("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMmv/T/wszdn09130DjLC4t8vHR8\n");
//		privatekey.append("sqStteLGFJq8tEY1b46Ghpx1y9WLgQ+f7VxMVO0rjQuCjECGMoLAfXzOls6rOz1qw+pfSCNnoGY7\n");
//		privatekey.append("6iS4Orac5LXp5R+SrtxXyf48dWvQB+bv3mv9PUfifyCVcpFLLMDbSGKoXSRAMhUx6j7TAgMBAAEC\n");
//		privatekey.append("gYEAhu2Z+2DIRNTNRGiXgKc1/gdg/H0/9jPQbvodZre/0wiErVEKVTnpN9+wZAeWAB2A43ozTfP0\n");
//		privatekey.append("aGZe/GJSkWCUOlb9omdJ7UQY9s29Q99ZneHMtAxHbBOUK4KZ1unMV72JXR4dQGtkAFAi6PF0bCrd\n");
//		privatekey.append("GEIQCHC1bH6mpu7ZFUw6owECQQDmBeSzrDT/Z7Nx1nhDEUYOUSSDMPfC0wFuDE4UFTOsHsOkDQIA\n");
//		privatekey.append("W13cs6cd5wXohUlTNk/SJpfhesF8VSDyOQSzAkEA4HbluwOwhzHEib7ld5DJk7uenPyWLHzhluHj\n");
//		privatekey.append("GAkkeL4zKeM4Z46SOaY7cxNLkBNPHs0cagvsnl4RRNUPH5QtYQJACRZAg6yQ52oUV7HuTE/5YYVp\n");
//		privatekey.append("GNmtX//v9YX866Qux2Tru6Zb5uG1IoviVTcUL6xcSjJCEv49T8YGsL+4Lnl8KwJAUOtMAlFgMDzG\n");
//		privatekey.append("x4mkG6h8ot9+XMXKNZuHj+c7AQ06srSOqUkaqmqBWHsO73tDQFtVqJr05V4LHUR4IUJVw2KdwQJB\n");
//		privatekey.append("AJ0Fi5OAzcoMklhRWiegHaGyUo4Asf+incwxDylpmpYRI6atOu5ED+YmHcqKcpi+I/iY83wQAT5O\n");
//		privatekey.append("W6FYtqVvHx0=\n");
//	}

    @Autowired
    private ISecretKeyService secretkeyser;
    @Autowired
    private ICorpService corpser;

    private SingleObjectBO singleObjectBO;

    @Autowired
    private FastDfsUtil fastDfsUtil;

    //private Logger log = Logger.getLogger(this.getClass());

    public SingleObjectBO getSingleObjectBO() {
        return singleObjectBO;
    }

    @Autowired
    public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
        this.singleObjectBO = singleObjectBO;
    }

    @Override
    public String updatedataBackUp(CorpVO corpvo) throws DZFWarpException {
        if (corpvo == null) {
            throw new BusinessException("公司信息不能为空!");
        }
        corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, corpvo.getPk_corp());
        String resstr = null;
        try {
            BackupVO backup = new BackupVO();
            // 查询
            resstr = getCorpData(corpvo.getPk_corp());
            // 压缩
            byte[] bytes = gZip(resstr.getBytes("UTF-8"));
            // 加密
            byte[] resbytes = null;

            resbytes = rsaEnByte(bytes, new DZFDateTime().toString(), backup);
            // 文件存储
            saveFileAndRecord(resbytes, corpvo, backup);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw new BusinessException(e.getMessage());
            } else {
                throw new WiseRunException(e);
            }
        }
        return "";
    }

    @Override
    public void updatedateReturn(BackupVO backvo) throws DZFWarpException {
        if (backvo == null) {
            throw new BusinessException("导入数据为空，数据还原失败！");
        }

        // 读取文件
        String fileName = backvo.getFilePath();
        byte[] data = readFileData(fileName);

        if (data == null || data.length == 0) {
            throw new BusinessException("导入数据为空，数据还原失败！");
        }
        // 解密
        byte[] bytes = null;
        try {
            bytes = rsadeByte(data, backvo.getBackupTime().toString(), backvo);
        } catch (Exception e) {
            throw new WiseRunException(e);
        }

        byte[] unbytes = unGZip(bytes);
        // 该公司的数据需要删除，然后恢复
        dataRes(unbytes, backvo.getPk_corp());
    }

    /**
     * 数据恢复
     *
     * @param unbytes
     */
    @SuppressWarnings({"rawtypes", "unused"})
    private void dataRes(byte[] unbytes, String pk_corp) {
        try {
            String value = new String(unbytes, "UTF-8");
            SQLParameter sp = new SQLParameter();
            sp.addParam(pk_corp);
            // value转化成对象
            Map<String, String> bodymapping = new HashMap<String, String>();
//			System.out.println(value);
//			log.info(value);
            JSONArray res2 = JSONArray.parseArray(value);
            value = null; // 垃圾回收
            Map<String, List<SuperVO>> mapvalues = new HashMap<String, List<SuperVO>>();
            if (res2 != null && res2.size() > 0) {
                JSONObject obt = null;
                SuperVO tempvo = null;
                String[] names = null;
                Iterator ite = null;
                String obtkey = null;
                for (int i = 0; i < res2.size(); i++) {
                    obt = (JSONObject) res2.get(i);
                    if (obt == null || obt.size() == 0)
                        continue;
                    ite = obt.keySet().iterator();
                    if (obt != null) {
                        String key = obt.getString("tablename");
                        for (Class str : IBackAndRestore.classes) {
                            tempvo = (SuperVO) str.newInstance();
                            if (tempvo.getTableName().equals(key)) {
                                names = tempvo.getAttributeNames();
                                for (String name : names) {
                                    if (obt.containsKey(name)) {
                                        bodymapping.put(name, name);
                                    }
                                }
                                tempvo = TypeUtils.cast(obt, tempvo.getClass(),
                                        JSONConvtoJAVA.getParserConfig());
                                if (tempvo != null) {
                                    if (mapvalues.containsKey(key)) {
                                        mapvalues.get(key).add(tempvo);
                                    } else {
                                        List<SuperVO> templist = new ArrayList<SuperVO>();
                                        templist.add(tempvo);
                                        mapvalues.put(key, templist);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            res2 = null; // 垃圾回收
            if (mapvalues.size() > 0) {
                List<SuperVO> listvos = null;
                SuperVO vo = null;
                for (Class str : IBackAndRestore.classes) {
                    log.info(str.toString());
                    vo = (SuperVO) str.newInstance();
                    listvos = mapvalues.get(vo.getTableName());
                    // 先刪除 数据处理
                    if (!vo.getTableName().equals("bd_corp")) {
                        String sql = "delete from " + vo.getTableName() + " where pk_corp = ? ";
                        sp.clearParams();
                        sp.addParam(pk_corp);
                        singleObjectBO.executeUpdate(sql, sp);
                    }
                    if (listvos != null && listvos.size() > 0) {
                        log.info("存在的数据:" + str.toString());
                        vo = (SuperVO) str.newInstance();
                        if (vo.getTableName().equals("bd_corp")) {
                            // 只更新公司的编码规则
                            CorpVO cpvo = (CorpVO) (listvos.get(0));
                            if (cpvo != null) {
                                String prikey = cpvo.getPrimaryKey();
                                CorpVO oldcorpvo = corpser.queryByPk(prikey);
                                if (oldcorpvo == null) {
                                    throw new BusinessException("该公司不存在!");
                                }
                                StringBuffer updatesql = new StringBuffer();
                                updatesql.append(" update bd_corp set accountcoderule = ?,corptype= ?,  ");
                                //纳税信息
                                updatesql.append(" vprovince = ?, chargedeptname =?,  ");
                                updatesql.append(" holdflag = ? , busibegindate = ? ,");//固定资产涉及字段
                                updatesql.append(" bbuildic =?, icbegindate = ?  , ibuildicstyle = ? ,icostforwardstyle =? ");
//								updatesql.append(" ,vsoccrecode = ? ,def16 = ?, vstatetaxpwd =? ,vlocaluname =?,");
//								updatesql.append(" vlocaltaxpwd= ?,vpersonalpwd = ?,vstateuname= ?  ");
                                updatesql.append(" where pk_corp = ? ");
                                sp.clearParams();
                                sp.addParam(cpvo.getAccountcoderule());
                                sp.addParam(cpvo.getCorptype());
                                //纳税信息
                                sp.addParam(cpvo.getVprovince());//地区
                                sp.addParam(cpvo.getChargedeptname());//纳税人资格
                                sp.addParam(cpvo.getHoldflag());//资产涉及字段
                                sp.addParam(cpvo.getBusibegindate());//资产涉及字段
                                sp.addParam(getNewBbuildic(cpvo.getBbuildic()));//库存涉及字段
                                sp.addParam(cpvo.getIcbegindate());//库存涉及字段
                                sp.addParam(cpvo.getIbuildicstyle());//库存涉及字段
                                sp.addParam(cpvo.getIcostforwardstyle());
//								sp.addParam(cpvo.getVsoccrecode());//纳税人识别号
//								sp.addParam(cpvo.getDef16());//登录方式
//								sp.addParam(cpvo.getVstatetaxpwd());//国税密码
//								sp.addParam(cpvo.getVlocaluname());//地税用户名
//								sp.addParam(cpvo.getVlocaltaxpwd());//地税密码
//								sp.addParam(cpvo.getVpersonalpwd());//个税密码
//								sp.addParam(cpvo.getVstateuname());//国税用户名
                                sp.addParam(prikey);
                                singleObjectBO.executeUpdate(updatesql.toString(), sp);
                            }
                        } else {
                            if (str.getName().equals(TaxReportDetailVO.class.getName())
                                    || str.getName().equals(TaxReportInitVO.class.getName())) {
                                for (SuperVO resvo : listvos) {
                                    writeTaxFile(resvo, "pdffile", "pdffilevalue");
                                    writeTaxFile(resvo, "spreadfile", "spreadfilevalue");
                                }
                            }
                            singleObjectBO.insertVOWithPK(pk_corp, listvos.toArray(new SuperVO[0]));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new WiseRunException(e);
        }
    }

    private String getNewBbuildic(String bbuildic) {
        if ("Y".equals(bbuildic)) {//1
            return IcCostStyle.IC_ON;
        } else if ("N".equals(bbuildic)) {//0
            return IcCostStyle.IC_OFF;
        }
        return bbuildic;
    }

    @SuppressWarnings("rawtypes")
    private void writeTaxFile(SuperVO vo, String key, String key2) throws IOException {
        BASE64Decoder base64en = new BASE64Decoder();
        String spreadfile = (String) vo.getAttributeValue(key);// "spreadfile"
        String spreadfilevalue = (String) vo.getAttributeValue(key2);// "spreadfilevalue"

        if (!StringUtil.isEmpty(spreadfile) && !StringUtil.isEmpty(spreadfilevalue)) {
            vo.setAttributeValue(key, spreadfile);
            byte[] bytetemps = base64en.decodeBuffer(spreadfilevalue);
            String id = writeFileByBytes(spreadfile, unGZip(bytetemps));
            if (!StringUtil.isEmpty(id)) {
                vo.setAttributeValue(key, id);
            }
        }
    }

    /**
     * 文件写入
     *
     * @param fileName
     */
    public String writeFileByBytes(String fileName, byte[] bytes) throws DZFWarpException {
        if (!StringUtil.isEmpty(fileName) && fileName.startsWith("*")) {
            String id = "";
            try {
                id = fastDfsUtil.upload(bytes, fileName, new HashMap());
                if (StringUtil.isEmpty(id)) {
                    throw new BusinessException("写入文件失败!");
                }
            } catch (Exception e) {
                throw new BusinessException("写入文件失败!");
            }
            return "*" + id.substring(1);//fast文件则不处理
        }

        File file = new File(fileName);
        OutputStream out = null;
        try {
            // 打开文件输出流
            out = new FileOutputStream(file);
            // 写入文件
            out.write(bytes);
            log.info("写文件" + file.getAbsolutePath() + "成功！");
        } catch (IOException e) {
            log.info("写文件" + file.getAbsolutePath() + "失败！");
            throw new WiseRunException(e);
        } finally {
            if (out != null) {
                try {
                    // 关闭输出文件流
                    out.close();
                } catch (IOException e1) {
                    throw new WiseRunException(e1);
                }
            }
        }

        return "";
    }


    /**
     * 读取vo数据
     *
     * @param backvo
     */
    private byte[] readFileData(String fileName) {

        //读取文件
        if (!StringUtil.isEmpty(fileName) && fileName.startsWith("*")) {
            try {
                byte[] bytes = fastDfsUtil.downFile(fileName.substring(1));

                return bytes;

            } catch (Exception e) {
                throw new WiseRunException(e);
            }

        }

        InputStream in = null;
        ByteArrayOutputStream baos = null;
        byte[] byteData = null;
        try {
            // 一次读多个字节
            byte[] tempbytes = new byte[100];
            baos = new ByteArrayOutputStream();
            int byteread = 0;
            in = new FileInputStream(fileName);
            // 读入多个字节到字节数组中，byteread为一次读入的字节数
            while ((byteread = in.read(tempbytes)) != -1) {
                baos.write(tempbytes, 0, byteread);
            }
            byteData = baos.toByteArray();
        } catch (Exception e1) {
            log.error("错误", e1);
            throw new BusinessException("读取数据出错，该文件不存在！");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {

                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    log.error("错误", e);
                }
            }
        }

        return byteData;
    }

    /**
     * 文件存储
     *
     * @param resbytes
     * @return
     */
    private void saveFileAndRecord(byte[] resbytes, CorpVO cpvo, BackupVO backup) throws DZFWarpException {

        FileOutputStream fileOutput = null;
        File file = null;
        try {
            String basePath = Common.imageBasePath.replaceAll("\\\\", "/") + cpvo.getUnitcode() + "/backup/";
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String unitname = CorpSecretUtil.deCode(cpvo.getUnitshortname());
            if (StringUtil.isEmpty(unitname)) {
                unitname = CorpSecretUtil.deCode(cpvo.getUnitname());
            }
//			String fileName = cpvo.getUnitcode() + "_" + dateFormat.format(date);
            String fileName = unitname + "_" + dateFormat.format(date);
            File dir = new File(basePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(dir, fileName);
            fileOutput = new FileOutputStream(file);
            fileOutput.write(resbytes);
            // 数据存储
            backup.setBackupTime(new DZFDateTime());
            backup.setFileSize(resbytes.length / 1024 + "KB");
            backup.setFilePath(basePath + fileName);
            backup.setFileName(fileName);
            backup.setPk_corp(cpvo.getPk_corp());
            backup.setDr(0);

            singleObjectBO.saveObject(cpvo.getPk_corp(), backup);
        } catch (Exception e) {
            log.error("错误", e);
            throw new WiseRunException(e);
        } finally {
            if (fileOutput != null) {
                try {
                    fileOutput.close();
                } catch (IOException e) {
                    log.error("错误", e);
                }
            }
        }
    }

    /**
     * 文件加密
     *
     * @param bytes
     * @throws Exception
     */
    private byte[] rsaEnByte(byte[] bytes, String datetime, BackupVO backup) throws Exception {
        Map<String, String> resmap = queryRsaMap(datetime, backup);

        String pubkey = resmap.get("pubkey");

        if (StringUtil.isEmpty(pubkey)) {
            throw new BusinessException("秘钥不存在!");
        }

        byte[] resbytes = RSACodeUtils.encryptByPublicKey(bytes, pubkey);

        return resbytes;
    }

    private Map<String, String> queryRsaMap(String datetime, BackupVO backup) {
        List<SecretKeyVo> listvo = secretkeyser.querySecretKeyFromDate(IGlobalConstants.currency_corp, ISourceSys.DATABACK, datetime, backup.getSecretno());

        if (listvo == null || listvo.size() == 0) {
            throw new BusinessException("查找秘钥出错!");
        }

        SecretKeyVo keyvo = listvo.get(0);

        if (StringUtil.isEmpty(backup.getPrimaryKey())) {
            backup.setSecretno(keyvo.getVversionno());
        }

        if (StringUtil.isEmpty(keyvo.getRsafilename())) {
            throw new BusinessException("秘钥文件不存在");
        }

        Map<String, String> resmap = secretkeyser.getRsaCodeValue(keyvo.getRsafilename());

        return resmap;
    }

    /**
     * 文件解密加密
     *
     * @param bytes
     * @throws Exception
     */
    private byte[] rsadeByte(byte[] bytes, String backtime, BackupVO backup) throws Exception {

        Map<String, String> resmap = queryRsaMap(backtime, backup);

        String prikey = resmap.get("prikey");

        if (StringUtil.isEmpty(prikey)) {
            throw new BusinessException("秘钥不存在!");
        }

        byte[] resbytes = RSACodeUtils.decryptByPrivateKey(bytes, prikey);

        return resbytes;
    }

    /***
     * 压缩GZip
     *
     * @param data
     * @return
     */
    public byte[] gZip(byte[] data) throws DZFWarpException {
        byte[] b = null;
        ByteArrayOutputStream bos = null;
        GZIPOutputStream gzip = null;
        try {
            bos = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(bos);
            gzip.write(data);
            gzip.finish();
            b = bos.toByteArray();
        } catch (Exception ex) {
            log.error("压缩失败", ex);
            throw new WiseRunException(ex);
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    log.error("错误", e);
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    log.error("错误", e);
                }
            }

        }
        return b;
    }

    /***
     * 解压GZip
     *
     * @param data
     * @return
     */
    public byte[] unGZip(byte[] data) throws DZFWarpException {
        byte[] b = null;
        ByteArrayInputStream bis = null;
        GZIPInputStream gzip = null;
        ByteArrayOutputStream baos = null;
        try {
            bis = new ByteArrayInputStream(data);
            gzip = new GZIPInputStream(bis);
            byte[] buf = new byte[1024];
            int num = -1;
            baos = new ByteArrayOutputStream();
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }
            b = baos.toByteArray();
            baos.flush();
        } catch (Exception ex) {
            log.error("解压失败", ex);
            throw new WiseRunException(ex);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    log.error("解压失败", e);
                }
            }
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    log.error("解压失败", e);
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    log.error("解压失败", e);
                }
            }
        }
        return b;
    }

    /**
     * 获取json字符串
     *
     * @param pk_corp
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    private String getCorpData(String pk_corp) throws Exception {
        List<Map<String, Object>> keymap = new ArrayList<Map<String, Object>>();
        List listvalues = new ArrayList();
        SuperVO vo = null;
        StringBuffer json = new StringBuffer();
        for (Class str : IBackAndRestore.classes) {
            vo = (SuperVO) str.newInstance();
            getData(vo.getTableName(), pk_corp, str, keymap, listvalues, json);
        }

//		json  = getObjectMapper().writeValueAsString(keymap);
        StringBuffer json_res = new StringBuffer();
        if (json.toString().length() > 0) {
            json_res.append("[" + json.toString() + "]");
        }

        return json_res.toString();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void getData(String tablename, String pk_corp, Class classvalue,
                         List<Map<String, Object>> keymap, List listvalues, StringBuffer json) throws Exception {
        SQLParameter sp = new SQLParameter();
        String tablesql = "select * from " + tablename + " where nvl(dr,0)=0 and pk_corp =? ";
        sp.addParam(pk_corp);
        List<SuperVO> resvos = (List<SuperVO>) singleObjectBO.executeQuery(tablesql, sp,
                new BeanListProcessor(classvalue));
        String columnsql = "select lower(column_name) from user_tab_columns where table_name= ? ";
        sp.clearParams();
        sp.addParam(tablename.toUpperCase());
        List<String> columnnames = (List<String>) singleObjectBO.executeQuery(columnsql, sp, new ColumnListProcessor());
        // vo转换成json字符串
        if (resvos != null && resvos.size() > 0) {
            String[] names = null;
            Map<String, Object> map = null;
            Object value = null;
            for (SuperVO vo : resvos) {
                listvalues.add(vo);
                map = new HashMap<String, Object>();
                map.put("tablename", tablename);
                StringBuffer temp_json = new StringBuffer();
//				temp_json.append("\"tablename\":"+ "\"" + (tablename)+"\""+",");
                names = vo.getAttributeNames();
                for (String name : names) {
                    if (columnnames.contains(name.toLowerCase())) {
                        if (vo.getAttributeValue(name) != null) {
                            value = (Object) vo.getAttributeValue(name);
                            if (value instanceof DZFBoolean) {
                                value = ((DZFBoolean) value).booleanValue();
//								temp_json.append("\""+name+"\":"+ "\"" + (((boolean)value) ? "Y":"N")+"\""+",");
                            } else if (value instanceof DZFDouble) {
                                value = ((DZFDouble) value).doubleValue();
//								temp_json.append("\""+name+"\":"+ ((double)value)+",");
                            } else if (value instanceof DZFDateTime) {
                                value = ((DZFDateTime) value).toString();
//								temp_json.append("\""+name+"\":"+ "\"" + ((String)value)+"\""+",");
                            } else if (value instanceof DZFDate) {
                                value = ((DZFDate) value).toString();
//								temp_json.append("\""+name+"\":"+ "\"" + ((String)value)+"\""+",");
                            }
                            map.put(name, value);
                        }
                    }
                }


                String tvalue = getObjectMapper().writeValueAsString(map);

                // 税务申报子表
                if (vo.getClass().getName().equals(TaxReportDetailVO.class.getName())) {
                    String file = handerReportDetail(map);
                    if (!StringUtil.isEmpty(file)) {
                        temp_json.append(file);
                    }
                }

                // 税务申报期初表
                if (vo.getClass().getName().equals(TaxReportInitVO.class.getName())) {
                    String file = handerReportInit(map);
                    if (!StringUtil.isEmpty(file)) {
                        temp_json.append(file);
                    }
                }
                keymap.add(map);

                if (!StringUtil.isEmpty(tvalue) && tvalue.length() > 1) {
                    temp_json.append(tvalue.substring(1, tvalue.length() - 1));
                }

                if (temp_json.toString().length() > 0) {
//					System.out.println("{"+temp_json.toString().substring(0, temp_json.length()-1)+"},");
                    json.append("{");
                    json.append(temp_json.toString().substring(0, temp_json.length()));
                    json.append("},");
                }

            }
        }
    }

    /**
     * 税务申报期初表
     *
     * @param map
     */
    private String handerReportInit(Map<String, Object> map) {

        String spreadfile = (String) map.get("spreadfile");

        BASE64Encoder base64en = new BASE64Encoder();

        StringBuffer append_file = new StringBuffer();

        if (!StringUtil.isEmpty(spreadfile)) {
            byte[] spreadfilebytes = readFileData(spreadfile);

            String filename = base64en.encode(spreadfile.getBytes());

            byte[] zip_value = gZip(spreadfilebytes);

            String values = base64en.encode(zip_value);

//			map.put("spreadfile", filename);

            map.put("spreadfilevalue", values);

            append_file.append("\"spreadfilevalue\"" + ":\"" + values + "\",");
        }
        return append_file.toString();
    }

    /**
     * 税务申报子表
     *
     * @param map
     */
    private String handerReportDetail(Map<String, Object> map) {

        String pdffile = (String) map.get("pdffile");

        String spreadfile = (String) map.get("spreadfile");

        BASE64Encoder base64en = new BASE64Encoder();

        StringBuffer append_file = new StringBuffer();

        if (!StringUtil.isEmpty(pdffile)) {
            byte[] pdfbytes = readFileData(pdffile);

//			String filename = base64en.encode(pdffile.getBytes());

            byte[] zip_value = gZip(pdfbytes);

            String values = base64en.encode(zip_value);

//			map.put("pdffile", filename);

//			map.put("pdffilevalue", values);

            append_file.append("\"pdffilevalue\"" + ":\"" + values + "\",");
        }

        if (!StringUtil.isEmpty(spreadfile)) {
            byte[] spreadbytes = readFileData(spreadfile);

            byte[] zip_value = gZip(spreadbytes);

//			String filename = base64en.encode(spreadfile.getBytes());

            String values = base64en.encode(zip_value);

//			map.put("spreadfile", filename);

//			map.put("spreadfilevalue", values);

            append_file.append("\"spreadfilevalue\"" + ":\"" + values + "\",");
        }

        return append_file.toString();

    }

    @Override
    public List<BackupVO> query(String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        String sql = " select pk_backup,pk_corp,fileName,fileSize,backupTime,vmemo from sys_backup where pk_corp = ? and nvl(dr,0) = 0 order by backupTime desc ";
        sp.addParam(pk_corp);
        List<BackupVO> rs = (List<BackupVO>) singleObjectBO.executeQuery(sql, sp,
                new BeanListProcessor(BackupVO.class));
        return rs;
    }


    @Override
    public BackupVO queryByID(String pk_backup) {
        SQLParameter sp = new SQLParameter();
        String condition = " pk_backup = ? and nvl(dr,0) = 0 ";
        sp.addParam(pk_backup);
        BackupVO[] rs = (BackupVO[]) singleObjectBO.queryByCondition(BackupVO.class, condition, sp);
        if (rs != null && rs.length > 0) {
            return rs[0];
        }
        return null;
    }

    @Override
    public void delete(BackupVO vo) throws DZFWarpException {
        singleObjectBO.deleteObject(vo);
    }

    @Override
    public void saveUpFile(InputStream is, String fileName, CorpVO corp) throws DZFWarpException {
        BufferedInputStream bufferIn = null;
        BufferedOutputStream bufferOut = null;
//		FileInputStream is = null;
        FileOutputStream os = null;
        try {
            BackupVO backvo = new BackupVO();
            corp = queryCorp(corp.getPk_corp());
            backvo.setPk_corp(corp.getPk_corp());
            backvo.setDr(0);
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            int index = 0;
            try {
                index = fileName.indexOf("_");
                String dateStr = fileName.substring(index + 1, fileName.length());
                Date date = dateFormat.parse(dateStr);
                backvo.setBackupTime(new DZFDateTime(date));
            } catch (Exception e) {
                throw new BusinessException("文件名格式错误！");
            }
            String corpName = fileName.substring(0, index);
            if (!corp.getUnitshortname().equals(corpName)) {
                throw new BusinessException("请上传本公司备份数据！");
            }
            String basePath = Common.imageBasePath.replaceAll("\\\\", "/") + corp.getUnitcode() + "/backup/";
            File dir = new File(basePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File saveFile = new File(basePath + fileName);
//			is =new FileInputStream(file);
            os = new FileOutputStream(saveFile);
            bufferIn = new BufferedInputStream(is);
            bufferOut = new BufferedOutputStream(os);
            backvo.setFileSize(bufferIn.available() / 1024 + "KB");
            int count;
            byte[] data = new byte[BUFFER_SIZE];
            while ((count = bufferIn.read(data, 0, BUFFER_SIZE)) != -1) {
                bufferOut.write(data, 0, count);
            }
            bufferOut.flush();
            backvo.setFileName(fileName);
            backvo.setFilePath(basePath + fileName);
            singleObjectBO.saveObject(corp.getPk_corp(), backvo);
        } catch (IOException e) {
            throw new BusinessException("上传失败！");
        } finally {
            if (bufferIn != null) {
                try {
                    bufferIn.close();
                } catch (IOException e) {
                    log.error("错误", e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("错误", e);
                }
            }
            if (bufferOut != null) {
                try {
                    bufferOut.close();
                } catch (IOException e) {
                    log.error("错误", e);
                }
            }

            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("错误", e);
                }
            }
        }
    }

    private CorpVO queryCorp(String pk_corp) {
        SQLParameter sp = new SQLParameter();
        CorpVO corp = null;
        String condition = " pk_corp = ? and nvl(dr,0) = 0 ";
        sp.addParam(pk_corp);
        CorpVO[] rs = (CorpVO[]) singleObjectBO.queryByCondition(CorpVO.class, condition, sp);
        if (rs != null && rs.length > 0) {
            corp = rs[0];
            try {
                corp.setUnitname(CorpSecretUtil.deCode(corp.getUnitname()));
                corp.setUnitshortname(CorpSecretUtil.deCode(corp.getUnitshortname()));
            } catch (Exception e) {
                log.error("错误", e);
            }
        }
        return corp;
    }


    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {

            @Override
            public void serialize(Object value, JsonGenerator jg, SerializerProvider sp)
                    throws IOException, JsonProcessingException {
                jg.writeString("");
            }
        });
        objectMapper.setSerializationInclusion(Include.ALWAYS);
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        objectMapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, false);
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        return objectMapper;
    }

    @Override
    public BackupVO queryNewByCorp(String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        String condition = " pk_corp = ? and nvl(dr,0) = 0  order by ts desc ";
        sp.addParam(pk_corp);
        BackupVO[] rs = (BackupVO[]) singleObjectBO.queryByCondition(BackupVO.class, condition, sp);
        if (rs != null && rs.length > 0) {
            return rs[0];
        }
        return null;
    }

    @Override
    public List<CorpVO> queryCorpBackDate(List<CorpVO> cplist) throws DZFWarpException {

        if (cplist != null && cplist.size() > 0) {
            List<String> idlist = new ArrayList<String>();
            for (CorpVO cpvo : cplist) {
                cpvo.setDef1(null);//暂用自定义项1
                idlist.add(cpvo.getPk_corp());
            }

            SQLParameter sp = new SQLParameter();
            StringBuffer sql = new StringBuffer();
            sql.append("select pk_corp, max(backupTime) as  backupTime ");
            sql.append("  from sys_backup ");
            sql.append("  where  " + SqlUtil.buildSqlForIn("pk_corp", idlist.toArray(new String[0])));
            sql.append("  group by pk_corp ");
            List<BackupVO> rs = (List<BackupVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(BackupVO.class));

            if (rs != null && rs.size() > 0) {
                Map<String, DZFDateTime> tmap = new HashMap<String, DZFDateTime>();
                for (BackupVO bakcvo : rs) {
                    tmap.put(bakcvo.getPk_corp(), bakcvo.getBackupTime());
                }

                DZFDateTime datetemp = null;
                for (CorpVO cpvo : cplist) {
                    datetemp = tmap.get(cpvo.getPk_corp());
                    if (datetemp != null) {
                        cpvo.setDef1(datetemp.toString());
                    }
                }
            }
        }

        return cplist;
    }

    @Override
    public void updateBackVo(BackupVO upvo) throws DZFWarpException {
        if (upvo == null || StringUtil.isEmpty(upvo.getPrimaryKey())) {
            throw new BusinessException("数据不能为空!");
        }
        singleObjectBO.update(upvo, new String[]{"vmemo"});

    }
}
