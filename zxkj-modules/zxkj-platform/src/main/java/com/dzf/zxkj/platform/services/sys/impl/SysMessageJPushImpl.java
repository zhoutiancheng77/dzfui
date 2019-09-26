package com.dzf.zxkj.platform.services.sys.impl;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import cn.jpush.api.push.model.PushPayload.Builder;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.enums.SourceSysEnum;
import com.dzf.zxkj.platform.model.sys.JPMessageBean;
import com.dzf.zxkj.platform.services.sys.ISysMessageJPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service("sysmsgsrv")
@Slf4j
public class SysMessageJPushImpl implements ISysMessageJPush {

	private JPushClient jpushClient;
	
	private static JPushClient jpushClientAd;
	
	
	@Override
	public void sendSysMessage(JPMessageBean jpmessagebean) throws DZFWarpException {
		if((jpmessagebean.getCorptags()==null||jpmessagebean.getCorptags().length<=0)
				&&(jpmessagebean.getUserids()==null||jpmessagebean.getUserids().length<=0)){
			throw new BusinessException("没有需要送达的目标设备信息!","4002");
		}
		
		if((jpmessagebean.getMessage()==null||jpmessagebean.getMessage().length()<=0)&&
				(jpmessagebean.getExtras()==null||jpmessagebean.getExtras().size()<=0)){
			throw new BusinessException("没有需要推送的条目内容!","4003");
		}
        PushPayload pushpayload = buildPushObject(jpmessagebean);

//        log.info("AAAAAAAAAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBBBBBBBBB");
        
        try {
			sendPush(pushpayload,jpmessagebean.getSourcesys());
			log.info("推送成功!");
		} catch (Exception e) {
			log.error("错误",e);
		}
		
	}
	

	
	@Override
	public void sendSysMessageAll(String content) throws DZFWarpException {
		sendPush(PushPayload.messageAll(content),"");
	}
	
	private void sendPush(PushPayload pushpayload,String sourcesys) throws DZFWarpException {
		JPushClient JjpushClient = getClient(sourcesys);
        try {
            PushResult result = JjpushClient.sendPush(pushpayload);
            log.info("Got result - " + result);

        } catch (APIConnectionException e) {
            // Connection error, should retry later
            log.error("Connection error, should retry later", e);
            throw new BusinessException("网络连接错误，请稍后重试!", "4001");

        } catch (APIRequestException e) {
            log.error("Should review the error, and fix the request", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
            
            throw new BusinessException(e.getErrorMessage(), Integer.toString(e.getStatus()));
        }
		
	
	}
	
	private JPushClient getClient(String sourcesys){
//		if(jpushClient==null){
//			jpushClient = new JPushClient("7b358c5272bd6b10efbd9c90","2e0353fb2e531954a5f2406c", 3);
			log.error("系统来源:"+sourcesys);
			String sercret = DzfUtil.MASTERSECRET;
			String key = DzfUtil.JPUSHAPPKEY;
			if(!StringUtil.isEmpty(sourcesys)){
				for(SourceSysEnum enumvalue: SourceSysEnum.values()){
					if(enumvalue.getValue().equals(sourcesys)){
						sercret = enumvalue.getJgsecret();
						key = enumvalue.getJgkey();
						break;
					}
				}
			}
			log.info("key:"+key);
			log.info("秘钥:"+sercret);
			jpushClient = new JPushClient(sercret, key, 3);
//		}
		return jpushClient;
	}
	
	/**
	 * 推送内容组装
	 * */
//	private PushPayload buildPushObject(JPMessageBean jpmessagebean) {
//
//		String extrastr="";
//		if(jpmessagebean.getExtras()!=null){
//			ObjectNode datanode = JsonNodeFactory.instance.objectNode();
//			Map extras = jpmessagebean.getExtras();
//			Set<String> keys = extras.keySet();
//			for(String key:keys){
//				datanode.put(key, extras.get(key).toString());
//			}
//			extrastr=datanode.toString();
//		} 
//		log.info(extrastr);
//		Builder b =null;
//		try {
//			b = PushPayload.newBuilder();
//			b.setPlatform(Platform.all());
//		} catch (Exception e) {
//			log.error("错误",e);
//			return null;
//		}
//		 
//		 if(jpmessagebean.getUserids()!=null&&jpmessagebean.getCorptags()!=null){//推送对象
//			 b.setAudience(Audience.newBuilder()
//			 .addAudienceTarget(AudienceTarget.alias(jpmessagebean.getUserids()))
//			 .addAudienceTarget(AudienceTarget.tag(jpmessagebean.getCorptags())).build());
//		 }else if(jpmessagebean.getUserids()!=null){
//			 b.setAudience(Audience.newBuilder()
//					 .addAudienceTarget(AudienceTarget.alias(jpmessagebean.getUserids())).build());
//		 }else if(jpmessagebean.getCorptags()!=null){
//			 b.setAudience(Audience.newBuilder()
//					 .addAudienceTarget(AudienceTarget.tag(jpmessagebean.getCorptags())).build());
//		 }
//		 if(jpmessagebean.getExtras()!=null){//推送内容
//			 b.setNotification(Notification.newBuilder()
//	         		.addPlatformNotification(AndroidNotification.newBuilder()
//	            			.setAlert(jpmessagebean.getMessage())
//	            			.addExtra("extrajson", extrastr).build())
//	            		.addPlatformNotification(IosNotification.newBuilder()
//	            			.setAlert(jpmessagebean.getMessage())
//	            			.setContentAvailable(true)
//	            			.setBadge(0)
//	            			.addExtra("extrajson", extrastr).build())
//	            		.build());
//			 b.setMessage(Message.newBuilder()
//					 .setMsgContent(jpmessagebean.getMessage())
//					 .addExtra("extrajson", extrastr).build());
//		 }else{
//
//			 b.setNotification(Notification.newBuilder()
//	         		.addPlatformNotification(AndroidNotification.newBuilder()
//	            			.setAlert(jpmessagebean.getMessage()).build())
//	            		.addPlatformNotification(IosNotification.newBuilder()
//	            			.setAlert(jpmessagebean.getMessage())
//	            			.setContentAvailable(true).build())
//	            		.build());
//			 b.setMessage(Message.newBuilder()
//					 .setMsgContent(jpmessagebean.getMessage()).build());
//		 }
//		 
//		 return b.build();
//	}
	
	
	   /**
     * 推送内容组装
     * */
    private PushPayload buildPushObject(JPMessageBean jpmessagebean) {
        Builder b =null;
        try {
            b = PushPayload.newBuilder();
            b.setPlatform(Platform.all());
        } catch (Exception e) {
            log.error("错误",e);
            return null;
        }
         
         if(jpmessagebean.getUserids()!=null&&jpmessagebean.getCorptags()!=null){//推送对象
             b.setAudience(Audience.newBuilder()
             .addAudienceTarget(AudienceTarget.alias(jpmessagebean.getUserids()))
             .addAudienceTarget(AudienceTarget.tag(jpmessagebean.getCorptags())).build());
         }else if(jpmessagebean.getUserids()!=null){
             b.setAudience(Audience.newBuilder()
                     .addAudienceTarget(AudienceTarget.alias(jpmessagebean.getUserids())).build());
         }else if(jpmessagebean.getCorptags()!=null){
             b.setAudience(Audience.newBuilder()
                     .addAudienceTarget(AudienceTarget.tag(jpmessagebean.getCorptags())).build());
         }
         if(jpmessagebean.getExtras()!=null){//推送内容
             b.setNotification(Notification.newBuilder()
                    .addPlatformNotification(AndroidNotification.newBuilder()
                            .setAlert(jpmessagebean.getMessage())
//                          .addExtra("extras", extrastr)
                            .addExtras(jpmessagebean.getExtras())
                            .build())
                        .addPlatformNotification(IosNotification.newBuilder()
                            .setAlert(jpmessagebean.getMessage())
                            .setContentAvailable(true)
                            .setBadge(0).addExtras(jpmessagebean.getExtras())
//                          .addExtra("extras", extrastr)
                            .build())
                        .build());
             Map extras = jpmessagebean.getExtras();
             Set<String> keys = extras.keySet();
             cn.jpush.api.push.model.Message.Builder bulider = Message.newBuilder().setMsgContent(jpmessagebean.getMessage());
             for(String key:keys){
                 bulider.addExtra(key, extras.get(key).toString());
             }
             b.setMessage(bulider.build());
//           b.setMessage(Message.newBuilder()
//                   .setMsgContent(jpmessagebean.getMessage())
//                   .addExtra("extrajson", "123123")
//                   .build());
         }else{

             b.setNotification(Notification.newBuilder()
                    .addPlatformNotification(AndroidNotification.newBuilder()
                            .setAlert(jpmessagebean.getMessage())
                            .build())
                        .addPlatformNotification(IosNotification.newBuilder()
                            .setAlert(jpmessagebean.getMessage())
                            .setContentAvailable(true).build())
                        .build());
             b.setMessage(Message.newBuilder()
                     .setMsgContent(jpmessagebean.getMessage()).build());
         }
         
         return b.build();
    }




	@Override
	public void sendAdminMessage(JPMessageBean jpmessagebean) throws DZFWarpException {
		if((jpmessagebean.getCorptags()==null||jpmessagebean.getCorptags().length<=0)
				&&(jpmessagebean.getUserids()==null||jpmessagebean.getUserids().length<=0)){
			throw new BusinessException("没有需要送达的目标设备信息!","4002");
		}
		if((jpmessagebean.getMessage()==null||jpmessagebean.getMessage().length()<=0)&&
				(jpmessagebean.getExtras()==null||jpmessagebean.getExtras().size()<=0)){
			throw new BusinessException("没有需要推送的条目内容!","4003");
		}
        PushPayload pushpayload = buildPushObject(jpmessagebean);
        try {
        	sendAdminPush(pushpayload);
			log.info("推送成功!");
		} catch (Exception e) {
			log.error("错误",e);
		}
	}

	@Override
	public void sendAdminMessageAll(String content) throws DZFWarpException {
		sendAdminPush(PushPayload.messageAll(content));		
	}
	
	/**
	 * 小薇无忧APP平台
	 * @param pushpayload
	 * @throws DZFWarpException
	 */
	private void sendAdminPush(PushPayload pushpayload) throws DZFWarpException {
		JPushClient JjpushClient = getAdminClient();
        try {
            PushResult result = JjpushClient.sendPush(pushpayload);
            log.info("Got result - " + result);

        } catch (APIConnectionException e) {
            log.error("Connection error, should retry later", e);
            throw new BusinessException("网络连接错误，请稍后重试!", "4001");

        } catch (APIRequestException e) {
            log.error("Should review the error, and fix the request", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
            throw new BusinessException(e.getErrorMessage(), Integer.toString(e.getStatus()));
        }
	}
	
	/**
	 * 小薇无忧APP平台
	 * @return
	 */
	private JPushClient getAdminClient(){
		if(jpushClientAd==null){
			jpushClientAd = new JPushClient(DzfUtil.MASTERSECRET_ADMIN, DzfUtil.JPUSHAPPKEY_ADMIN, 3);
		}
		return jpushClientAd;
	}
}
