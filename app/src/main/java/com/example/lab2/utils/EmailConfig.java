package com.example.lab2.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.InputStream;
import java.util.Properties;

public class EmailConfig {
    private static final String TAG = "EmailConfig";
    private static Properties emailProps;
    private static boolean isConfigLoaded = false;
    private static Context appContext;

    /**
     * Khởi tạo EmailConfig với Context
     */
    public static void init(Context context) {
        if (context != null) {
            appContext = context.getApplicationContext();
            loadEmailConfiguration();
        } else {
            Log.e(TAG, "❌ Context is null in init()");
        }
    }

    private static void loadEmailConfiguration() {
        emailProps = new Properties();

        if (appContext == null) {
            Log.e(TAG, "❌ App context is null, cannot load smtp.properties");
            loadFallbackConfiguration();
            return;
        }

        try {
            // ✅ ĐỌC TỪ ASSETS THƯ MỤC
            AssetManager assetManager = appContext.getAssets();
            InputStream is = assetManager.open("smtp.properties");

            emailProps.load(is);
            is.close();

            Log.d(TAG, "✅ SMTP configuration loaded from assets/smtp.properties");
            Log.d(TAG, "✅ Loaded user: " + emailProps.getProperty("mail.smtp.user"));
            Log.d(TAG, "✅ Loaded host: " + emailProps.getProperty("mail.smtp.host"));

            isConfigLoaded = true;

        } catch (Exception e) {
            Log.e(TAG, "❌ Error loading SMTP configuration: " + e.getMessage());
            Log.e(TAG, "❌ Make sure smtp.properties is in app/src/main/assets/ folder");
            e.printStackTrace();
            loadFallbackConfiguration();
        }
    }

    private static void loadFallbackConfiguration() {
        Log.w(TAG, "⚠️ Using fallback configuration");

        emailProps.setProperty("mail.smtp.host", "smtp.gmail.com");
        emailProps.setProperty("mail.smtp.port", "587");
        emailProps.setProperty("mail.smtp.auth", "true");
        emailProps.setProperty("mail.smtp.starttls.enable", "true");

        // ✅ FALLBACK VỚI THÔNG TIN THẬT
        emailProps.setProperty("mail.smtp.user", "hoangkhanggauss@gmail.com");
        emailProps.setProperty("mail.smtp.password", "aopu gbwq koqa watc");

        Log.d(TAG, "✅ Fallback user: " + emailProps.getProperty("mail.smtp.user"));
        isConfigLoaded = true;
    }

    /**
     * Lấy toàn bộ Properties cho SMTP
     */
    public static Properties getEmailProperties() {
        if (!isConfigLoaded || emailProps == null) {
            Log.w(TAG, "⚠️ Configuration not loaded, reloading...");
            loadEmailConfiguration();
        }
        return (Properties) emailProps.clone();
    }

    /**
     * Lấy SMTP User (Email)
     */
    public static String getSmtpUser() {
        if (!isConfigLoaded) {
            Log.w(TAG, "⚠️ Config not loaded, reloading...");
            loadEmailConfiguration();
        }
        String user = emailProps.getProperty("mail.smtp.user", "");
        Log.d(TAG, "Getting SMTP User: " + user);
        return user;
    }

    /**
     * Lấy SMTP Password
     */
    public static String getSmtpPassword() {
        if (!isConfigLoaded) {
            loadEmailConfiguration();
        }
        String pass = emailProps.getProperty("mail.smtp.password", "");
        Log.d(TAG, "Getting SMTP Password: " + (pass.isEmpty() ? "EMPTY" : "***SET***"));
        return pass;
    }

    /**
     * Lấy SMTP Host
     */
    public static String getSmtpHost() {
        return emailProps.getProperty("mail.smtp.host", "smtp.gmail.com");
    }

    /**
     * Lấy SMTP Port
     */
    public static String getSmtpPort() {
        return emailProps.getProperty("mail.smtp.port", "587");
    }

    /**
     * Kiểm tra xem configuration có hợp lệ không
     */
    public static boolean isConfigurationValid() {
        if (!isConfigLoaded) {
            loadEmailConfiguration();
        }

        String user = getSmtpUser();
        String pass = getSmtpPassword();

        boolean isValid = user != null && !user.isEmpty() &&
                pass != null && !pass.isEmpty() &&
                !user.equals("your_email@gmail.com") &&
                !pass.equals("your_app_password");

        Log.d(TAG, "=== Configuration Validation ===");
        Log.d(TAG, "User empty: " + (user == null || user.isEmpty()));
        Log.d(TAG, "Pass empty: " + (pass == null || pass.isEmpty()));
        Log.d(TAG, "User is placeholder: " + "your_email@gmail.com".equals(user));
        Log.d(TAG, "Pass is placeholder: " + "your_app_password".equals(pass));
        Log.d(TAG, "Configuration valid: " + isValid);
        Log.d(TAG, "===============================");

        return isValid;
    }

    /**
     * In thông tin configuration (ẩn password)
     */
    public static void logConfiguration() {
        Log.d(TAG, "=============== SMTP Configuration ===============");
        Log.d(TAG, "Context initialized: " + (appContext != null));
        Log.d(TAG, "Config loaded: " + isConfigLoaded);
        Log.d(TAG, "Host: " + getSmtpHost());
        Log.d(TAG, "Port: " + getSmtpPort());
        Log.d(TAG, "User: " + getSmtpUser());
        Log.d(TAG, "Password: " + (getSmtpPassword().isEmpty() ? "NOT SET" : "***HIDDEN***"));
        Log.d(TAG, "Valid: " + isConfigurationValid());
        Log.d(TAG, "===============================================");
    }
}