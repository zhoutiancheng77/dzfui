package com.dzf.zxkj.platform.util;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class SendEmailService {
	private MimeMessage mimeMsg;  
    private Session session; 
    private Properties props; 
    private String username; 
    private String password;  
    private Multipart mp; 

    public SendEmailService(String smtp) { 
        setSmtpHost(smtp); 
        createMimeMessage();  
    } 

    public void setSmtpHost(String hostName) { 
       // System.out.println("设置系统属性：mail.smtp.host=" + hostName); 
        if (props == null) { 
            props = System.getProperties(); 
        }  
        props.put("mail.smtp.host", hostName); 
    } 

    public boolean createMimeMessage() { 
        try { 
            //System.out.println("准备获取邮件会话对象！"); 
            session = Session.getDefaultInstance(props, null); 
        } catch (Exception e) { 
            //System.out.println("获取邮件会话错误！" + e); 
            return false; 
        } 
        //System.out.println("准备创建MIME邮件对象！");  
        try { 
            mimeMsg = new MimeMessage(session); 
            mp = new MimeMultipart(); 
            return true;  
        } catch (Exception e) {  
           // System.out.println("创建MIME邮件对象失败！" + e);  
            return false;  
        }  
    } 

    /*定义SMTP是否需要验证*/  
    public void setNeedAuth(boolean need) { 
       // System.out.println("设置smtp身份认证：mail.smtp.auth = " + need); 
        if (props == null) 
            props = System.getProperties();  
        if (need) {  
            props.put("mail.smtp.auth", "true"); 
        } else {  
            props.put("mail.smtp.auth", "false");  
        }  
        props.put("mail.port", "587");//设置邮件传输协议
    }
  
    public void setNamePass(String name, String pass) { 
        username = name;  
        password = pass;  
    } 
 
    /*定义邮件主题*/  
    public boolean setSubject(String mailSubject) {  
     //   System.out.println("定义邮件主题！");  
        try {  
            mimeMsg.setSubject(mailSubject); 
            return true;  
        } catch (Exception e) {  
            System.err.println("定义邮件主题发生错误！");  
            return false;  
        }  
    } 
 
    /*定义邮件正文*/ 
    public boolean setBody(String mailBody) {  
        try { 
            BodyPart bp = new MimeBodyPart();  
            bp.setContent("" + mailBody, "text/html;charset=UTF-8");  
            mp.addBodyPart(bp);  
            return true;  
        } catch (Exception e) {  
            System.err.println("定义邮件正文时发生错误！" + e);  
            return false;  
        }  
    } 
 
    /*设置发信人*/ 
    public boolean setFrom(String from) {  
   //     System.out.println("设置发信人！");  
        try { 
            mimeMsg.setFrom(new InternetAddress(from)); //发信人 
            return true; 
        } catch (Exception e) { 
            return false;  
        } 
    } 
 
    /*定义收信人*/  
    public boolean setTo(String to) {  
        if (to == null)  
            return false;  
     //   System.out.println("定义收信人！"); 
        try { 
            mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));  
            return true;  
        } catch (Exception e) {  
            return false;  
        }  
    } 
 
    /*定义抄送人*/  
    public boolean setCopyTo(String copyto) { 
        if (copyto == null)  
            return false; 
        try { 
            mimeMsg.setRecipients(Message.RecipientType.CC, (Address[]) InternetAddress  
                    .parse(copyto));  
            return true; 
        } catch (Exception e) {  
            return false;  
        }  
    } 

    /*发送邮件模块*/ 
    public boolean sendOut() {  
        try { 
            mimeMsg.setContent(mp); 
            mimeMsg.saveChanges();  
       //     System.out.println("邮件发送中....");  
            Session mailSession = Session.getInstance(props, null);  
            Transport transport = mailSession.getTransport("smtp");  
            transport.connect((String) props.get("mail.smtp.host"), username, password); 
            transport.sendMessage(mimeMsg, mimeMsg 
            .getRecipients(Message.RecipientType.TO)); 
      //      System.out.println("发送成功！");  
            transport.close(); 
            return true;  
        } catch (Exception e) {  
            System.err.println("邮件失败！" + e); 
            return false;  
        }  
    } 
 
    /*调用sendOut方法完成发送*/ 
    public static boolean sendAndCc(String smtp, String from, String to, String copyto, 
        String subject, String content, String username, String password) { 
    	SendEmailService theMail = new SendEmailService(smtp);  
        theMail.setNeedAuth(true); // 验证  
        if (!theMail.setSubject(subject)) 
            return false;  
        if (!theMail.setBody(content))  
            return false;  
        if (!theMail.setTo(to))  
            return false;  
        if (!theMail.setCopyTo(copyto))  
            return false; 
        if (!theMail.setFrom(from))  
            return false; 
        theMail.setNamePass(username, password); 
        if (!theMail.sendOut()) 
            return false;  
        return true;  
    } 
    

public static void main(String[] args) { 

		/*String smtp = "smtp.163.com";// smtp服务器 
	    String from = "dg9259@163.com";// 邮件显示名称 
	    String to = "1250776020@qq.com";// 收件人的邮件地址，必须是真实地址 
	    String copyto = "";// 抄送人邮件地址 
	    String subject = "测试邮件";// 邮件标题 
	    String content = "你好！";// 邮件内容 
	    String username = "dg9259@163.com";// 发件人真实的账户名
	    String password = "18242981180";// 发件人密码 
*/		String smtp = "smtp.exmail.qq.com";// smtp服务器 
	    String from = "lili@dazhangfang.com";// 邮件显示名称 
	    String to = "1250776020@qq.com";// 收件人的邮件地址，必须是真实地址 
	    String copyto = "";// 抄送人邮件地址 
	    String subject = "测试邮件";// 邮件标题 
	    String content = "你好！";// 邮件内容 
	    String username = "lili@dazhangfang.com";// 发件人真实的账户名
	    String password = "19940709LIli11";// 发件人密码 
		SendEmailService.sendAndCc(smtp, from, to, copyto, subject, content, username, password); 
    } 

}
