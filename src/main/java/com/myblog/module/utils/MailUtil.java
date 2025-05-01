package com.myblog.module.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * @author nsh
 * @data 2025/4/22 11:26
 * @description
 **/
@Service
@RequiredArgsConstructor
public class MailUtil {

    private final JavaMailSender mailSender;

    public void sendSimpleMail(String to , String content) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        String url = "http://8.137.77.31/api/reg?token=" + content;

        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <title>邮箱验证</title>\n" +
                "</head>\n" +
                "<body style=\"font-family:Arial, sans-serif; background:#f9f9f9; padding:20px;\">\n" +
                "  <div style=\"max-width:500px; margin:0 auto; background:#fff; padding:20px; border-radius:4px;\">\n" +
                "    <h2 style=\"margin-top:0; color:#333;\">请验证你的邮箱</h2>\n" +
                "    <p>谢谢你的注册。请点击下面的链接完成邮箱验证：</p>\n" +
                "    <p>\n" +
                "      <a href=" + url +  "target=\"_blank\"\n" +
                "         style=\"display:inline-block; padding:10px 16px; background:#2193b0; color:#fff; text-decoration:none; border-radius:4px;\">\n" +
                "        验证邮箱\n" +
                "      </a>\n" +
                "    </p>\n" +
                "    <p>如果无法点击，请复制以下地址到浏览器打开：</p>\n" +
                "    <p style=\"word-break:break-all; color:#0066cc;\">\n" +
                url +
                "    </p>\n" +
                "    <p>链接24小时内有效。</p>\n" +
                "    <p style=\"margin-bottom:0;\">—— TWind</p>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>\n";
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom("3204789832@qq.com");
        helper.setTo(to);
        helper.setSubject("Email验证邮件");
        helper.setText(html, true);
        mailSender.send(mimeMessage);

    }
}
