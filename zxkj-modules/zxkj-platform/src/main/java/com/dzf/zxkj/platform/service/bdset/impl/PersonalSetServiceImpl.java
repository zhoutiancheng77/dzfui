package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.service.bdset.IPersonalSetService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("gl_gxhszserv")
public class PersonalSetServiceImpl implements IPersonalSetService {
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IParameterSetService paramservice;
	
	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	@Override
	public GxhszVO query(String pk_corp) throws DZFWarpException {
		GxhszVO gvo = new GxhszVO();
		List<YntParameterSet> list = paramservice.queryParamter(pk_corp);
		Map<String, YntParameterSet> map = DZfcommonTools.hashlizeObjectByPk(
				list, new String[]{ "parameterbm" });
		//赋值
		String[][] fields = {
				{IParameterConstants.DZF015, "printType", "0"}, {IParameterConstants.DZF016, "subjectShow", "0"},
				{IParameterConstants.DZF017, "pzSubject", "2"}, {IParameterConstants.DZF018, "balanceShow", "0"},
				{IParameterConstants.DZF019, "subject_num_show", "1"}, {IParameterConstants.DZF020, "isshowlastmodifytime", "1"},
				{IParameterConstants.DZF021, "yjqp_gen_vch", "1"},
		};
		
		YntParameterSet setvo;
		Integer value = null;
		for(String[] arr : fields){
			setvo = map.get(arr[0]);
			if(setvo != null){
				value = setvo.getPardetailvalue();
			}
			
			if(value == null){
				value = Integer.parseInt(arr[2]);
			}
			
			gvo.setAttributeValue(arr[1], value);
		}
		
		return gvo;
		
//		GxhszVO gvo = new GxhszVO();
//		SQLParameter params = new SQLParameter();
//		params.addParam(pk_corp);
//		String condition = " pk_corp = ? and nvl(dr,0) = 0 ";
//		PersonalSetVO[] pvos = (PersonalSetVO[]) singleObjectBO
//				.queryByCondition(PersonalSetVO.class, condition, params);
//		if (pvos != null && pvos.length > 0) {
//			PersonalSetVO pvo = pvos[0];
//			gvo = readXML(pvo.getSettings());
//			gvo.setPk_personal(pvo.getPk_personal());
//		}
//		// 赋默认值
//		if (gvo.getPrintType() == null)
//			gvo.setPrintType(0);
//		if (gvo.getBalanceSheet() == null)
//			gvo.setBalanceSheet(1);
//		if (gvo.getSubjectShow() == null)
//			gvo.setSubjectShow(0);
//		if (gvo.getPzSubject() == null)
//			gvo.setPzSubject(2);
//		if (gvo.getUploadstyle() == null)
//			gvo.setUploadstyle(0);
//		if (gvo.getBalanceShow() == null)
//			gvo.setBalanceShow(0);
//		if (gvo.getPt_gen_vch() == null)
//			gvo.setPt_gen_vch(0);
//		if (gvo.getYjqp_gen_vch() == null)
//			gvo.setYjqp_gen_vch(1);// 默认为手动制单
//		if (gvo.getIsshowlastmodifytime() == null) {// 凭证显示最后修改时间默认是显示的。
//			gvo.setIsshowlastmodifytime(1);
//		}
//		if (gvo.getSubject_num_show() == null) {
//			gvo.setSubject_num_show(1);
//		}
//		return gvo;
	}

//	@Override
//	public void save(GxhszVO vo, String pk_corp) throws DZFWarpException {
//		PersonalSetVO pvo = new PersonalSetVO();
//		pvo.setPk_personal(vo.getPk_personal());
//		pvo.setSettings(createXML(vo));
//		pvo.setDr(0);
//		pvo.setPk_corp(pk_corp);
//		if (StringUtil.isEmpty(vo.getPk_personal()))
//			singleObjectBO.saveObject(pk_corp, pvo);
//		else
//			singleObjectBO.update(pvo);
//
//	}
//
//	// 根据vo生成XML字符串
//	private String createXML(GxhszVO vo) {
//		Document doc = DocumentHelper.createDocument();
//		Element root = doc.addElement("settings");
//		addElement(root, "printType", vo.getPrintType());
//		addElement(root, "balanceSheet", vo.getBalanceSheet());
//		addElement(root, "subjectShow", vo.getSubjectShow());
//		addElement(root, "pzSubject", vo.getPzSubject());
//		addElement(root, "uploadstyle", vo.getUploadstyle());
//		addElement(root, "balanceShow", vo.getBalanceShow());
//		addElement(root, "pt_gen_vch", vo.getPt_gen_vch());
//		addElement(root, "yjqpway", vo.getYjqp_gen_vch());
//		addElement(root, "lmodtime", vo.getIsshowlastmodifytime());
//		addElement(root, "subject_num_show", vo.getSubject_num_show());
//		String xmlStr = doc.asXML();
//		return xmlStr;
//	}
//
//	// 将XML字符串转成vo
//	private GxhszVO readXML(String xmlStr) throws DZFWarpException {
//		GxhszVO vo = new GxhszVO();
//		Document document;
//		try {
//			document = DocumentHelper.parseText(xmlStr);
//			Element root = document.getRootElement();
//			vo.setBalanceSheet(getElementValue(root.element("balanceSheet")));
//			vo.setPrintType(getElementValue(root.element("printType")));
//			vo.setSubjectShow(getElementValue(root.element("subjectShow")));
//			vo.setPzSubject(getElementValue(root.element("pzSubject")));
//			vo.setUploadstyle(getElementValue(root.element("uploadstyle")));
//			vo.setBalanceShow(getElementValue(root.element("balanceShow")));
//			vo.setPt_gen_vch(getElementValue(root.element("pt_gen_vch")));
//			vo.setYjqp_gen_vch(getElementValue(root.element("yjqpway")));
//			// 最后修改时间
//			vo.setIsshowlastmodifytime(getElementValue(root.element("lmodtime")));
//			vo.setSubject_num_show(getElementValue(root
//					.element("subject_num_show")));
//			return vo;
//		} catch (Exception e) {
//			throw new WiseRunException(e);
//		}
//	}
//
//	private Integer getElementValue(Element element) {
//		if (element != null && !"".equals(element.getText())) {
//			return Integer.valueOf(element.getText());
//		}
//		return null;
//	}
//
//	private void addElement(Element root, String name, Integer value) {
//		Element element = root.addElement(name);
//		element.setText(value == null ? "" : value.toString());
//	}
}
