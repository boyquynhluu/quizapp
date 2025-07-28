package com.myapp.quiz.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.myapp.quiz.entity.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "SEND MAIL")
public class SendMail {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;
    /**
     * 
     * @param user
     * @param token
     */
    public void sendVerificationEmail(User user, String token) {
        // Creating a mime message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            log.info("Start Send Mail Verify Token");
            // Set info send mail
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(user.getEmail());
            mimeMessageHelper.setSubject("Kích hoạt tài khoản");
            
            String verifyUrl = "http://localhost:3000/verify?token=" + token;
            String htmlContent = """
                    <div style="font-family: Arial, sans-serif; padding: 20px; background-color: #f9f9f9;">
                        <div style="max-width: 500px; margin: auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 3px 8px rgba(0,0,0,0.1);">
                            <h2 style="color: #4CAF50; text-align: center;">Kích hoạt tài khoản</h2>
                            <p>Xin chào <b>%s</b>,</p>
                            <p>Cảm ơn bạn đã đăng ký! Vui lòng nhấn vào nút bên dưới để kích hoạt tài khoản của bạn:</p>
                            <div style="text-align: center; margin: 20px 0;">
                                <a href="%s" style="background-color: #4CAF50; color: white; padding: 12px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;">Kích Hoạt Ngay</a>
                            </div>
                            <p>Liên kết sẽ hết hạn sau <b>30 phút</b>.</p>
                            <hr style="border:none; border-top:1px solid #ddd;">
                            <p style="font-size: 12px; color: #777;">Nếu bạn không yêu cầu tạo tài khoản, vui lòng bỏ qua email này.</p>
                        </div>
                    </div>
                    """.formatted(user.getFullName(), verifyUrl);

            mimeMessageHelper.setText(htmlContent, true);

            log.info("Send Mail...");
            // Sending the mail
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Send mail has error: {}", e.getMessage(), e);
        }
    }
}
