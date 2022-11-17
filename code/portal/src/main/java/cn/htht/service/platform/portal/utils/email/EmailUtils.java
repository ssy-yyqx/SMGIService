package cn.htht.service.platform.portal.utils.email;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @ClassName EmailUtils
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class EmailUtils {

    // 初始化连接邮件服务器的会话信息
    private static Properties props = null;


    /**
     * 发邮件前必须先设置邮箱服务端信息
     *
     * @param protocol          邮件发送协议
     * @param host              SMTP邮件服务器
     * @param port              SMTP邮件服务器默认端口
     * @param isAuth            是否要求身份认证
     * @param isEnabledDebugMod 是否启用调试模式（启用调试模式可打印客户端与服务器交互过程时一问一答的响应消息）
     */
    public static void setProps(String protocol, String host, String port, String isAuth, String isEnabledDebugMod) {
        props = new Properties();
        props.setProperty("mail.transport.protocol", protocol);
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", port);
        props.setProperty("mail.smtp.auth", isAuth);
        props.setProperty("mail.debug", isEnabledDebugMod);
    }

    /**
     * 发送简单的文本邮件
     *
     * @param user      发送的邮箱名  ==》 取 @前的名字
     * @param password  SMTP 生成的第三方密码
     * @param addressor 发件人 "xxxxxx54@qq.com"
     * @param addressee 收件人 "xxxxxxan@hotmail.com"
     * @param subject   邮件主题名
     * @param content   内容
     * @return true|false
     * @throws Exception Exception
     */
    public static boolean sendEmail(String user, String password, String addressor, String addressee, String subject, String content) {
        try {
            // 创建Session实例对象
            Session session1 = Session.getDefaultInstance(props);

            // 创建MimeMessage实例对象
            MimeMessage message = new MimeMessage(session1);

            // 设置发件人
            message.setFrom(new InternetAddress(addressor));

            // 设置邮件主题
            message.setSubject(subject);

            // 设置收件人
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(addressee));

            // 设置发送时间
            message.setSentDate(new Date());

            // 设置纯文本内容为邮件正文
            message.setText(content);

            // 保存并生成最终的邮件内容
            message.saveChanges();

            // 获得Transport实例对象
            Transport transport = session1.getTransport();

            // 打开连接  xxxxxx4  rdpbuhymozfjbiag
            transport.connect(user, password);

            // 将message对象传递给transport对象，将邮件发送出去
            transport.sendMessage(message, message.getAllRecipients());

            // 关闭连接
            transport.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 发送简单的html邮件
     *
     * @param userName      发送的邮箱名  ==》 取 @前的名字
     * @param password      SMTP 生成的第三方密码
     * @param addressor     发送人邮箱
     * @param addressorName 发送人姓名
     * @param addressee     接收人邮箱
     * @param subject       邮件主题
     * @param content       邮件内容
     * @return true|false
     * @throws Exception UnsupportedEncodingException
     */
    public boolean sendHtmlEmail(String userName, String password, String addressor, String addressorName, String addressee, String subject, String content) throws Exception {
        // 创建Session实例对象  xxxxxxx54
        MyAuthenticator myAuthenticator = new MyAuthenticator();
        myAuthenticator.setUsername(userName);
        myAuthenticator.setPassword(password);
        Session session1 = Session.getInstance(props, myAuthenticator);

        // 创建MimeMessage实例对象
        MimeMessage message = new MimeMessage(session1);

        // 设置邮件主题
        message.setSubject(subject);

        // 设置发送人
        message.setFrom(new InternetAddress(addressor));

        // 设置发送时间
        message.setSentDate(new Date());

        // 设置收件人
        message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(addressee));

        // 设置html内容为邮件正文，指定MIME类型为text/html类型，并指定字符编码为gbk
        message.setContent("<div style='width: 600px;margin: 0 auto'><h3 style='color:#003E64; text-align:center; '> " + addressee + "</h3><p style='text-indent: 2em'> " + content + " </p><p style='text-align: right; color:#003E64; font-size: 20px;'></p></div>", "text/html;charset=utf-8");

        //设置自定义发件人昵称
        String nick = "";
        try {
            nick = MimeUtility.encodeText(addressorName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        message.setFrom(new InternetAddress(nick + " <" + addressor + ">"));

        // 保存并生成最终的邮件内容
        message.saveChanges();

        // 发送邮件
        try {
            Transport.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送带内嵌图片的HTML邮件
     *
     * @param userName      发送的邮箱名  ==》 取 @前的名字
     * @param password      SMTP 生成的第三方密码
     * @param addressor     发送人邮箱
     * @param addressee     接收人邮箱
     * @param subject       邮件主题
     * @param content       邮件内容
     * @param imagePathName 图片的路径及图片名字  路径用“\\”
     * @return true|false
     * @throws MessagingException MessagingException
     */
    public boolean sendHtmlWithInnerImageEmail(String userName, String password, String addressor, String addressee, String subject, String content, String imagePathName) throws MessagingException {
        // 创建Session实例对象
        MyAuthenticator myAuthenticator = new MyAuthenticator();
        myAuthenticator.setUsername(userName);
        myAuthenticator.setPassword(password);
        Session session = Session.getDefaultInstance(props, myAuthenticator);
        // 创建邮件内容
        MimeMessage message = new MimeMessage(session);

        // 邮件主题,并指定编码格式
        message.setSubject(subject, "utf-8");

        // 发件人
        message.setFrom(new InternetAddress(addressor));

        // 收件人
        message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(addressee));

        // 抄送
        message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress(addressee));

        // 密送 (不会在邮件收件人名单中显示出来)
        message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(addressee));

        // 发送时间
        message.setSentDate(new Date());

        // 创建一个MIME子类型为“related”的MimeMultipart对象
        // MIME 消息能包含文本、图像、音频、视频以及其他应用程序专用的数据。
        MimeMultipart mp = new MimeMultipart("related");

        // 创建一个表示正文的MimeBodyPart对象，并将它加入到前面创建的MimeMultipart对象中
        MimeBodyPart htmlPart = new MimeBodyPart();
        mp.addBodyPart(htmlPart);

        // 创建一个表示图片资源的MimeBodyPart对象，将将它加入到前面创建的MimeMultipart对象中
        MimeBodyPart imagePart = new MimeBodyPart();
        mp.addBodyPart(imagePart);
        // 将MimeMultipart对象设置为整个邮件的内容
        message.setContent(mp);

        // 设置内嵌图片邮件体
        DataSource ds = new FileDataSource(new File(imagePathName));
        DataHandler dh = new DataHandler(ds);
        imagePart.setDataHandler(dh);
        String[] aa = imagePathName.split("\\\\");
        System.out.println("aa   ==> " + aa);
        String imageName = aa[aa.length - 1];
        imagePart.setContentID(imageName);  // 设置内容编号,用于其它邮件体引用

        // 创建一个MIME子类型为"alternative"的MimeMultipart对象，并作为前面创建的htmlPart对象的邮件内容
        MimeMultipart htmlMultipart = new MimeMultipart("alternative");
        // 创建一个表示html正文的MimeBodyPart对象
        MimeBodyPart htmlBodypart = new MimeBodyPart();
        // 其中cid=androidlogo.gif是引用邮件内部的图片，即imagePart.setContentID("androidlogo.gif");方法所保存的图片
        htmlBodypart.setContent("<span style='color:red;'> " + content + " <img src=\"" + imageName + "\" /></span>", "text/html;charset=utf-8");
        htmlMultipart.addBodyPart(htmlBodypart);
        htmlPart.setContent(htmlMultipart);

        // 保存并生成最终的邮件内容
        message.saveChanges();

        // 发送邮件
        Transport.send(message);

        return true;
    }


    /**
     * 发送带内嵌图片、附件、多收件人(显示邮箱姓名)、邮件优先级、阅读回执的完整的HTML邮件
     *
     * @param userName       发送的邮箱名  ==》 取 @前的名字
     * @param password       SMTP 生成的第三方密码
     * @param addressor      发送人邮箱
     * @param addressorName  发送人的名字
     * @param subject        邮件主题
     * @param attachments    附件 ==》 多个 用List<String> 放置附件路径 ， 要求使用 "\\"
     * @param imagePathName  图片路径的名称  路径用“\\”
     * @param content        邮件内容
     * @param addressees     接收人邮箱 ==》 多个  用List<String> 放置
     * @param addresseeNames 接收人姓名  ==》 多个  用List<String> 放置， 要求姓名的个数与接收人的数量一致
     * @return true|false
     * @throws Exception Exception
     */
    public boolean sendMultipleEmail(String userName, String password, String addressor, String addressorName, String subject, List<String> attachments, String imagePathName, String content, List<String> addressees, List<String> addresseeNames) throws Exception {

        for (int i = 0; i < attachments.size(); i++) {

            String charset = "utf-8";   // 指定中文编码格式

            MyAuthenticator myAuthenticator = new MyAuthenticator();
            myAuthenticator.setUsername(userName);
            myAuthenticator.setPassword(password);
            // 创建Session实例对象
            Session session = Session.getDefaultInstance(props, myAuthenticator);

            // 创建MimeMessage实例对象
            MimeMessage message = new MimeMessage(session);

            // 设置主题
            message.setSubject(subject);

            // 设置发送人
            message.setFrom(new InternetAddress(addressor, addressorName, charset));

            // 设置收件人
            for (int z = 0; z < addressees.size(); z++) {
                String addressee = addressees.get(z);
                String addresseeName = addresseeNames.get(z);
                message.setRecipients(MimeMessage.RecipientType.TO,
                        new Address[]{
                                // 参数1：邮箱地址，参数2：姓名（在客户端收件只显示姓名，而不显示邮件地址），参数3：姓名中文字符串编码
                                new InternetAddress(addressee, addresseeName, charset),
                        }
                );
                // 设置抄送
                message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress(addressee, addresseeName, charset));

                // 设置密送
                message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(addressee, addresseeName, charset));

                // 设置发送时间
                message.setSentDate(new Date());

                // 设置回复人(收件人回复此邮件时,默认收件人)
                message.setReplyTo(InternetAddress.parse("\"" + MimeUtility.encodeText(addressorName) + "\" <" + addressor + ">"));

                // 设置优先级(1:紧急   3:普通    5:低)
                message.setHeader("X-Priority", "1");

                // 要求阅读回执(收件人阅读邮件时会提示回复发件人,表明邮件已收到,并已阅读)
                message.setHeader("Disposition-Notification-To", addressor);

                // 创建一个MIME子类型为"mixed"的MimeMultipart对象，表示这是一封混合组合类型的邮件
                MimeMultipart mailContent = new MimeMultipart("mixed");
                message.setContent(mailContent);

                // 附件
                MimeBodyPart attach = new MimeBodyPart();

                // 内容
                MimeBodyPart mailBody = new MimeBodyPart();

                // 将附件和内容添加到邮件当中
                mailContent.addBodyPart(attach);
                mailContent.addBodyPart(mailBody);

                String attachment = attachments.get(i);
                DataSource ds1 = new FileDataSource(attachment);
                DataHandler dh1 = new DataHandler(ds1);
                String[] aa = attachment.split("\\\\");
                String name = aa[aa.length - 1];
                attach.setFileName(MimeUtility.encodeText(name));
                attach.setDataHandler(dh1);

                // 邮件正文(内嵌图片+html文本)
                MimeMultipart body = new MimeMultipart("related");  //邮件正文也是一个组合体,需要指明组合关系
                mailBody.setContent(body);

                // 邮件正文由html和图片构成
                MimeBodyPart imgPart = new MimeBodyPart();
                MimeBodyPart htmlPart = new MimeBodyPart();
                body.addBodyPart(imgPart);
                body.addBodyPart(htmlPart);

                // 正文图片
                DataSource ds3 = new FileDataSource(imagePathName);
                DataHandler dh3 = new DataHandler(ds3);
                imgPart.setDataHandler(dh3);
                String[] aaa = imagePathName.split("\\\\");
                String ImageName = aa[aa.length - 1];
                imgPart.setContentID(ImageName);

                // html邮件内容
                MimeMultipart htmlMultipart = new MimeMultipart("alternative");
                htmlPart.setContent(htmlMultipart);
                MimeBodyPart htmlContent = new MimeBodyPart();
                htmlContent.setContent(
                        "<span style='color:red'>" + content +
                                "<img src='cid:" + ImageName + "' /></span>"
                        , "text/html;charset=gbk");
                htmlMultipart.addBodyPart(htmlContent);

                // 保存邮件内容修改
                message.saveChanges();


                // 发送邮件
                Transport.send(message);
            }
            return true;

        }
        return false;
    }

    public static void main(String[] args) {
//        try {
//            boolean b = new MailUtil().sendEmail("xxxxx4","rdpbuhymozfjbiag","xxxxx4@qq.com", "xxxxx4@hotmail.com","测试","测试内容");
//
//            System.out.println("===> " + b);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

//        try {
//            boolean b = new MailUtil().sendHtmlEmail("xxxxx4","rdpbuhymozfjbiag","xxxxx4@qq.com","咸鱼张","xxxxx4@hotmail.com","测试主题","邮件测试内容");
//            System.out.println("===> " + b);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

//        try {
//            boolean b = new MailUtil().sendHtmlWithInnerImageEmail("xxxxx254","rdpbuhymozfjbiag","xxxxxx254@qq.com","xxxxxxxan@hotmail.com","测试带图片的邮件","邮件内容测试11111","G:\\images\\2233.jpg");
//            System.out.println("===> " + b);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

//        try {
//            List list = new ArrayList();
//            list.add("G:\\images\\新建文本文档.md");
//            list.add("G:\\images\\12306-抢票.zip");
//            System.out.println("list  ==> " + list.toString());
//
//
//            List list2 = new ArrayList();
//            list2.add("xxxxxxan@hotmail.com");
//            list2.add("xxxxxx93@qq.com");
//            list2.add("xxxxxxx66@qq.com");
//            System.out.println("list2  ==> " + list2.toString());
//
//            List list3 = new ArrayList();
//            list3.add("测试一");
//            list3.add("测试二");
//            list3.add("测试三");
//            System.out.println("list3  ==> " + list3.toString());
//
//            boolean b = new MailUtil().sendMultipleEmail("xxxxxx54","rdpbuhymozfjbiag","xxxxxx54@qq.com","咸鱼一号","测试",list,"G:\\images\\2233.jpg","邮件测试内容",list2,list3);
//            System.out.println("===> " + b);
//        }catch(Exception e){
//            e.printStackTrace();
//        }

    }

    /**
     * 向邮件服务器提交认证信息
     */
    static class MyAuthenticator extends Authenticator {

        private String username;

        private String password;

        public MyAuthenticator() {
            super();
        }

        public MyAuthenticator(String username, String password) {
            super();
            this.username = username;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {

            return new PasswordAuthentication(username, password);
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
