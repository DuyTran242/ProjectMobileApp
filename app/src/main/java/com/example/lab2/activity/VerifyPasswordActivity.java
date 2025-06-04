package com.example.lab2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.lab2.R;
import com.example.lab2.utils.Utils;
import com.example.lab2.Service.EmailService;
import com.example.lab2.utils.OTPManager;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyPasswordActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etCurrentPassword;
    private MaterialButton btnConfirmPassword;
    private FirebaseAuth firebaseAuth;
    private OTPManager otpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_password);

        firebaseAuth = FirebaseAuth.getInstance();
        otpManager = new OTPManager(this);

        initViews();
        setupToolbar();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_verify_password);
        etCurrentPassword = findViewById(R.id.et_current_password);
        btnConfirmPassword = findViewById(R.id.btn_confirm_password);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupClickListeners() {
        btnConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPasswordAndSendOTP();
            }
        });
    }

    private void verifyPasswordAndSendOTP() {
        String currentPassword = etCurrentPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu hiện tại", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            // Tạo credential để re-authenticate
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            // Hiển thị loading
            btnConfirmPassword.setEnabled(false);
            btnConfirmPassword.setText("Đang xác minh...");

            Log.d("VerifyPassword", "Starting re-authentication for: " + user.getEmail());

            // Re-authenticate user trước
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("VerifyPassword", "Re-authentication successful");
                            // Sau khi xác thực thành công, gửi OTP qua email
                            sendOTPEmail(user.getEmail());
                        } else {
                            btnConfirmPassword.setEnabled(true);
                            btnConfirmPassword.setText("Đồng ý");

                            Log.e("VerifyPassword", "Re-authentication failed", task.getException());
                            String errorMessage = "Mật khẩu không chính xác";
                            Toast.makeText(VerifyPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Không thể xác minh người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendOTPEmail(String email) {
        // Tạo mã OTP
        String otpCode = otpManager.generateOTP();

        // Lưu OTP
        otpManager.saveOTP(email, otpCode);

        // Gửi email OTP
        EmailService.sendOTPEmail(email, otpCode, new EmailService.EmailCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    btnConfirmPassword.setEnabled(true);
                    btnConfirmPassword.setText("Đồng ý");

                    Log.d("VerifyPassword", "OTP email sent successfully to: " + email);
                    showOTPSentDialog(email);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnConfirmPassword.setEnabled(true);
                    btnConfirmPassword.setText("Đồng ý");

                    Log.e("VerifyPassword", "Failed to send OTP email: " + error);
                    Toast.makeText(VerifyPasswordActivity.this,
                            "Lỗi gửi email OTP: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showOTPSentDialog(String email) {
        Log.d("VerifyPassword", "Showing OTP dialog for email: " + email);

        new AlertDialog.Builder(this)
                .setTitle("Mã OTP đã được gửi")
                .setMessage("Chúng tôi đã gửi mã OTP đến email:\n" + email +
                        "\n\nVui lòng kiểm tra email (cả thư mục spam) và nhập mã OTP để tiếp tục." +
                        "\n\n⏰ Mã OTP sẽ hết hạn sau 5 phút.")
                .setPositiveButton("Nhập mã OTP", (dialog, which) -> {
                    try {
                        Log.d("VerifyPassword", "Starting OtpVerificationActivity...");
                        Intent intent = new Intent(VerifyPasswordActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("user_email", email);
                        intent.putExtra("is_reauthenticated", true);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Log.e("VerifyPassword", "Error starting OtpVerificationActivity", e);
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Gửi lại", (dialog, which) -> {
                    sendOTPEmail(email);
                })
                .setNeutralButton("Hủy", (dialog, which) -> {
                    otpManager.clearOTP();
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }
}