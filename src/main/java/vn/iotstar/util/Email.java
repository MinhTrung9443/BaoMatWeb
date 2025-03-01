package vn.iotstar.util;

import java.util.Properties;


import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class Email {
    public static void main(String[] args) {
        final String from = "lienhuetien02@gmail.com"; // Địa chỉ email người gửi
        final String password = "tgunanwrkxlmeamt";    // Mật khẩu ứng dụng (Google App Password)
        final String to = "lienhuetien01@gmail.com";   // Địa chỉ email người nhận

        // Cấu hình các thuộc tính SMTP của Gmail
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Bật TLS cho bảo mật

        // Cấu hình xác thực
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        };

        // Thiết lập phiên làm việc với Gmail SMTP
        Session session = Session.getInstance(props, auth);

        try {
            // Tạo đối tượng MimeMessage để gửi email
            MimeMessage msg = new MimeMessage(session);

            // Cài đặt người gửi
            msg.setFrom(new InternetAddress(from));

            // Cài đặt người nhận
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Tiêu đề email
            msg.setSubject("Reset password");

            // Nội dung email (có thể thay đổi theo nhu cầu)
            msg.setText("Your code is: 123456", "UTF-8");

            // Gửi email
            Transport.send(msg);

            System.out.println("Email sent successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
