package com.example.lab2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.security.SecureRandom;

public class OTPManager {

    private static final String PREFS_NAME = "OTP_PREFS";
    private static final String KEY_OTP_CODE = "otp_code";
    private static final String KEY_OTP_TIME = "otp_time";
    private static final String KEY_USER_EMAIL = "user_email";

    private static final long OTP_VALIDITY_DURATION = 5 * 60 * 1000; // 5 phút

    private Context context;
    private SharedPreferences prefs;

    public OTPManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Tạo mã OTP 6 chữ số
     */
    public String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    /**
     * Lưu OTP vào SharedPreferences
     */
    public void saveOTP(String email, String otpCode) {
        prefs.edit()
                .putString(KEY_OTP_CODE, otpCode)
                .putLong(KEY_OTP_TIME, System.currentTimeMillis())
                .putString(KEY_USER_EMAIL, email)
                .apply();
    }

    /**
     * Xác thực OTP
     */
    public OTPResult verifyOTP(String inputOTP, String email) {
        String storedOTP = prefs.getString(KEY_OTP_CODE, null);
        long otpTime = prefs.getLong(KEY_OTP_TIME, 0);
        String storedEmail = prefs.getString(KEY_USER_EMAIL, null);

        // Kiểm tra email có khớp không
        if (!email.equals(storedEmail)) {
            return new OTPResult(false, "Email không khớp");
        }

        // Kiểm tra OTP có tồn tại không
        if (storedOTP == null) {
            return new OTPResult(false, "Không tìm thấy mã OTP");
        }

        // Kiểm tra thời gian hết hạn
        long currentTime = System.currentTimeMillis();
        if (currentTime - otpTime > OTP_VALIDITY_DURATION) {
            clearOTP();
            return new OTPResult(false, "Mã OTP đã hết hạn");
        }

        // Kiểm tra mã OTP có đúng không
        if (!inputOTP.equals(storedOTP)) {
            return new OTPResult(false, "Mã OTP không chính xác");
        }

        // OTP hợp lệ, xóa khỏi storage
        clearOTP();
        return new OTPResult(true, "Xác thực thành công");
    }

    /**
     * Xóa OTP khỏi SharedPreferences
     */
    public void clearOTP() {
        prefs.edit()
                .remove(KEY_OTP_CODE)
                .remove(KEY_OTP_TIME)
                .remove(KEY_USER_EMAIL)
                .apply();
    }

    /**
     * Kiểm tra thời gian còn lại của OTP
     */
    public long getRemainingTime() {
        long otpTime = prefs.getLong(KEY_OTP_TIME, 0);
        if (otpTime == 0) return 0;

        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - otpTime;
        long remaining = OTP_VALIDITY_DURATION - elapsed;

        return remaining > 0 ? remaining : 0;
    }

    /**
     * Class để trả về kết quả xác thực OTP
     */
    public static class OTPResult {
        private boolean success;
        private String message;

        public OTPResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}