package com.example.lab2.Service;

import android.os.AsyncTask;
import android.util.Log;
import com.example.lab2.utils.EmailConfig;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {

    private static final String TAG = "EmailService";

    public interface EmailCallback {
        void onSuccess();
        void onError(String error);
    }

    public static void sendOTPEmail(String recipientEmail, String otpCode, EmailCallback callback) {
        // ✅ Kiểm tra configuration trước khi gửi
        if (!EmailConfig.isConfigurationValid()) {
            Log.e(TAG, "Email configuration is invalid");
            EmailConfig.logConfiguration();
            if (callback != null) {
                callback.onError("Email configuration not properly set up");
            }
            return;
        }

        new SendEmailTask(recipientEmail, otpCode, callback).execute();
    }

    private static class SendEmailTask extends AsyncTask<Void, Void, String> {
        private String recipientEmail;
        private String otpCode;
        private EmailCallback callback;

        public SendEmailTask(String recipientEmail, String otpCode, EmailCallback callback) {
            this.recipientEmail = recipientEmail;
            this.otpCode = otpCode;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // ✅ SỬ DỤNG EmailConfig thay vì hardcode
                Properties props = EmailConfig.getEmailProperties();

                Log.d(TAG, "Sending email with configuration:");
                Log.d(TAG, "Host: " + props.getProperty("mail.smtp.host"));
                Log.d(TAG, "Port: " + props.getProperty("mail.smtp.port"));
                Log.d(TAG, "From: " + EmailConfig.getSmtpUser());
                Log.d(TAG, "To: " + recipientEmail);

                // Tạo session với authentication
                javax.mail.Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                EmailConfig.getSmtpUser(),
                                EmailConfig.getSmtpPassword()
                        );
                    }
                });

                // Enable debug mode để troubleshoot
                session.setDebug(true);

                // Tạo message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EmailConfig.getSmtpUser()));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
                message.setSubject("Mã OTP - Đổi mật khẩu");

                // Nội dung email HTML
                String emailContent = createEmailContent(otpCode);
                message.setContent(emailContent, "text/html; charset=utf-8");

                // Gửi email
                Transport.send(message);

                Log.d(TAG, "OTP email sent successfully to: " + recipientEmail);
                return "SUCCESS";

            } catch (MessagingException e) {
                Log.e(TAG, "Failed to send OTP email", e);
                return "ERROR: " + e.getMessage();
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error while sending email", e);
                return "ERROR: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (callback != null) {
                if ("SUCCESS".equals(result)) {
                    callback.onSuccess();
                } else {
                    callback.onError(result);
                }
            }
        }
    }

    private static String createEmailContent(String otpCode) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }" +
                "        .container { max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                "        .header { text-align: center; color: #333; margin-bottom: 30px; }" +
                "        .otp-code { font-size: 36px; font-weight: bold; color: #007bff; text-align: center; padding: 20px; background-color: #f8f9fa; border-radius: 8px; margin: 20px 0; }" +
                "        .warning { color: #dc3545; font-weight: bold; text-align: center; }" +
                "        .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; color: #666; font-size: 14px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <h1>Xác thực đổi mật khẩu</h1>" +
                "        </div>" +
                "        <p>Xin chào,</p>" +
                "        <p>Bạn đã yêu cầu đổi mật khẩu cho tài khoản của mình. Vui lòng sử dụng mã OTP bên dưới để xác thực:</p>" +
                "        <div class='otp-code'>" + otpCode + "</div>" +
                "        <div class='warning'>" +
                "            <p>⚠️ Mã OTP này sẽ hết hạn sau 5 phút</p>" +
                "        </div>" +
                "        <p>Nếu bạn không yêu cầu đổi mật khẩu, vui lòng bỏ qua email này và liên hệ với chúng tôi ngay lập tức.</p>" +
                "        <div class='footer'>" +
                "            <p>Trân trọng,<br>Đội ngũ hỗ trợ</p>" +
                "            <p><small>Email này được gửi tự động, vui lòng không reply.</small></p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}