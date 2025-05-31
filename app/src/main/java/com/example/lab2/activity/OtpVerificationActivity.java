package com.example.lab2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.lab2.R;
import com.example.lab2.Service.EmailService;
import com.example.lab2.utils.OTPManager;

public class OtpVerificationActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etOtpCode;
    private MaterialButton btnVerifyOtp, btnResendOtp;
    private TextView tvCountdown, tvEmail;
    private CountDownTimer countDownTimer;
    private String userEmail;
    private boolean isReauthenticated;
    private OTPManager otpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // Lấy dữ liệu từ Intent
        userEmail = getIntent().getStringExtra("user_email");
        isReauthenticated = getIntent().getBooleanExtra("is_reauthenticated", false);

        otpManager = new OTPManager(this);

        initViews();
        setupToolbar();
        setupClickListeners();
        startCountdown();

        tvEmail.setText("Mã OTP đã được gửi đến:\n" + userEmail);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_otp);
        etOtpCode = findViewById(R.id.et_otp_code);
        btnVerifyOtp = findViewById(R.id.btn_verify_otp);
        btnResendOtp = findViewById(R.id.btn_resend_otp);
        tvCountdown = findViewById(R.id.tv_countdown);
        tvEmail = findViewById(R.id.tv_email);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupClickListeners() {
        btnVerifyOtp.setOnClickListener(v -> verifyOtp());
        btnResendOtp.setOnClickListener(v -> resendOtp());
    }

    private void startCountdown() {
        btnResendOtp.setEnabled(false);

        // Lấy thời gian còn lại từ OTPManager
        long remainingTime = otpManager.getRemainingTime();
        if (remainingTime <= 0) {
            remainingTime = 300000; // 5 phút mặc định
        }

        countDownTimer = new CountDownTimer(remainingTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                seconds = seconds % 60;
                tvCountdown.setText(String.format("Gửi lại OTP sau: %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("Mã OTP đã hết hạn. Bạn có thể gửi lại.");
                btnResendOtp.setEnabled(true);
                otpManager.clearOTP();
            }
        };
        countDownTimer.start();
    }

    private void verifyOtp() {
        String otpCode = etOtpCode.getText().toString().trim();

        if (TextUtils.isEmpty(otpCode)) {
            Toast.makeText(this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        if (otpCode.length() != 6) {
            Toast.makeText(this, "Mã OTP phải có 6 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verify OTP với OTPManager
        btnVerifyOtp.setEnabled(false);
        btnVerifyOtp.setText("Đang xác minh...");

        OTPManager.OTPResult result = otpManager.verifyOTP(otpCode, userEmail);

        if (result.isSuccess()) {
            Toast.makeText(this, "Xác thực OTP thành công!", Toast.LENGTH_SHORT).show();

            // Chuyển đến trang đổi mật khẩu
            Intent intent = new Intent(OtpVerificationActivity.this, ChangePasswordActivity.class);
            intent.putExtra("is_reauthenticated", isReauthenticated);
            intent.putExtra("is_otp_verified", true);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            btnVerifyOtp.setEnabled(true);
            btnVerifyOtp.setText("Xác nhận");
        }
    }

    private void resendOtp() {
        // Tạo OTP mới và gửi
        String newOtpCode = otpManager.generateOTP();
        otpManager.saveOTP(userEmail, newOtpCode);

        btnResendOtp.setEnabled(false);
        btnResendOtp.setText("Đang gửi...");

        EmailService.sendOTPEmail(userEmail, newOtpCode, new EmailService.EmailCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    btnResendOtp.setText("Gửi lại OTP");
                    Toast.makeText(OtpVerificationActivity.this,
                            "Đã gửi lại mã OTP đến " + userEmail, Toast.LENGTH_SHORT).show();
                    startCountdown();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnResendOtp.setEnabled(true);
                    btnResendOtp.setText("Gửi lại OTP");
                    Toast.makeText(OtpVerificationActivity.this,
                            "Lỗi gửi lại OTP: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        otpManager.clearOTP();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }
}