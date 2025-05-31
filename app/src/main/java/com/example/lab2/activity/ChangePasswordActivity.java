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
import com.example.lab2.retrofit.ApiBanHang;
import com.example.lab2.retrofit.RetrofitClient;
import com.example.lab2.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChangePasswordActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etNewPassword, etConfirmPassword;
    private MaterialButton btnSubmitChangePassword;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiBanHang apiBanHang;
    private FirebaseAuth firebaseAuth;
    private boolean isReauthenticated = false;
    private boolean bypassEmail = false;
    private boolean isPasswordResetFlow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        firebaseAuth = FirebaseAuth.getInstance();

        // Kiểm tra xem có được re-authenticate chưa
        isReauthenticated = getIntent().getBooleanExtra("is_reauthenticated", false);

        // Thêm kiểm tra email verification và OTP verification
        boolean isEmailVerified = getIntent().getBooleanExtra("is_email_verified", false);
        boolean isOtpVerified = getIntent().getBooleanExtra("is_otp_verified", false);

        // Kiểm tra tất cả các điều kiện xác thực
        if (!isReauthenticated || (!isEmailVerified && !isOtpVerified)) {
            Toast.makeText(this, "Phiên xác thực không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupClickListeners();
    }

    private boolean validateAccess(boolean isEmailVerified, boolean isOtpVerified) {
        // Nếu là luồng Password Reset, chỉ cần re-authenticate
        if (isPasswordResetFlow) {
            if (!isReauthenticated) {
                Toast.makeText(this, "Phiên xác thực không hợp lệ", Toast.LENGTH_SHORT).show();
                finish();
                return false;
            }
            return true;
        }

        // Luồng thông thường cần đầy đủ xác thực
        if (!isReauthenticated || (!isEmailVerified && !isOtpVerified && !bypassEmail)) {
            Toast.makeText(this, "Phiên xác thực không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        return true;
    }

    private void showSecurityNoticeIfNeeded() {
        if (bypassEmail && !isPasswordResetFlow) {
            new AlertDialog.Builder(this)
                    .setTitle("Lưu ý bảo mật")
                    .setMessage("Bạn đang đổi mật khẩu mà chưa xác thực qua email. " +
                            "Để đảm bảo bảo mật tối đa, hãy cập nhật email xác thực trong tương lai.")
                    .setPositiveButton("Tôi hiểu", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_change_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSubmitChangePassword = findViewById(R.id.btn_submit_change_password);
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
        btnSubmitChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (!validatePasswordInput(newPassword, confirmPassword)) {
            return;
        }

        // Hiển thị loading
        setLoadingState(true);

        // Kiểm tra luồng xử lý
        if (isPasswordResetFlow) {
            // Luồng Password Reset - chỉ cập nhật database (Firebase đã được reset)
            updatePasswordInDatabaseOnly(newPassword);
        } else {
            // Luồng thông thường - cập nhật Firebase trước, sau đó database
            updateFirebasePassword(newPassword);
        }
    }

    private boolean validatePasswordInput(String newPassword, String confirmPassword) {
        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng xác nhận mật khẩu mới", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra độ mạnh mật khẩu (tùy chọn)
        if (!isPasswordStrong(newPassword)) {
            showWeakPasswordDialog(newPassword);
            return false;
        }

        return true;
    }

    private boolean isPasswordStrong(String password) {
        // Kiểm tra cơ bản: có ít nhất 1 chữ hoa, 1 chữ thường, 1 số
        boolean hasUpper = !password.equals(password.toLowerCase());
        boolean hasLower = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");

        return password.length() >= 8 && hasUpper && hasLower && hasDigit;
    }

    private void showWeakPasswordDialog(String password) {
        new AlertDialog.Builder(this)
                .setTitle("Mật khẩu yếu")
                .setMessage("Mật khẩu nên có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và số.\n\nBạn có muốn tiếp tục với mật khẩu này không?")
                .setPositiveButton("Tiếp tục", (dialog, which) -> {
                    setLoadingState(true);
                    if (isPasswordResetFlow) {
                        updatePasswordInDatabaseOnly(password);
                    } else {
                        updateFirebasePassword(password);
                    }
                })
                .setNegativeButton("Chọn mật khẩu khác", null)
                .show();
    }

    private void setLoadingState(boolean loading) {
        btnSubmitChangePassword.setEnabled(!loading);
        btnSubmitChangePassword.setText(loading ? "Đang cập nhật..." : "Xác nhận");
    }

    private void updateFirebasePassword(String newPassword) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Log.d("ChangePassword", "Updating Firebase password...");

            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("ChangePassword", "Firebase password updated successfully");
                            // Firebase cập nhật thành công, tiếp tục cập nhật database
                            updatePasswordInDatabase(newPassword);
                        } else {
                            // Firebase cập nhật thất bại
                            setLoadingState(false);
                            handleFirebaseError(task.getException());
                        }
                    });
        } else {
            setLoadingState(false);
            Toast.makeText(this, "Người dùng không được xác thực", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePasswordInDatabaseOnly(String newPassword) {
        Log.d("ChangePassword", "Password Reset flow - updating database only for user ID: " + Utils.user_current.getId());
        updatePasswordInDatabase(newPassword);
    }

    private void updatePasswordInDatabase(String newPassword) {
        if (Utils.user_current == null || Utils.user_current.getId() <= 0) {
            setLoadingState(false);
            Toast.makeText(this, "Lỗi: Thông tin người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ChangePassword", "Updating database password for user ID: " + Utils.user_current.getId());

        compositeDisposable.add(apiBanHang.updatePassword(Utils.user_current.getId(), newPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            setLoadingState(false);
                            handleDatabaseResponse(messageModel, newPassword);
                        },
                        throwable -> {
                            setLoadingState(false);
                            Log.e("ChangePassword", "Database update error", throwable);
                            Toast.makeText(this, "Lỗi kết nối database: " + throwable.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void handleFirebaseError(Exception exception) {
        String errorMessage = "Lỗi cập nhật mật khẩu Firebase";
        if (exception != null) {
            Log.e("ChangePassword", "Firebase update error", exception);

            String exceptionMessage = exception.getMessage();
            if (exceptionMessage != null) {
                if (exceptionMessage.contains("weak")) {
                    errorMessage = "Mật khẩu quá yếu. Vui lòng chọn mật khẩu mạnh hơn.";
                } else if (exceptionMessage.contains("recent")) {
                    errorMessage = "Cần xác thực lại. Vui lòng thử lại từ đầu.";
                } else {
                    errorMessage += ": " + exceptionMessage;
                }
            }
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void handleDatabaseResponse(com.example.lab2.model.MessageModel messageModel, String newPassword) {
        Log.d("ChangePassword", "Database response - Success: " + messageModel.isSuccess() +
                ", Message: " + messageModel.getMessage());

        if (messageModel.isSuccess()) {
            // Cập nhật thành công
            Utils.user_current.setPass(newPassword);

            showSuccessDialog();
        } else {
            Toast.makeText(this, "Lỗi cập nhật database: " + messageModel.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thành công")
                .setMessage("Đổi mật khẩu thành công!\n\n" +
                        (isPasswordResetFlow ?
                                "Mật khẩu của bạn đã được cập nhật trong hệ thống." :
                                "Mật khẩu đã được đồng bộ trên Firebase và database."))
                .setPositiveButton("OK", (dialog, which) -> {
                    // Quay về trang Account Security hoặc MainActivity
                    navigateBack();
                })
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void navigateBack() {
        try {
            // Thử quay về AccountSecurityActivity trước
            Intent intent = new Intent(this, Class.forName("com.example.lab2.activity.AccountSecurityActivity"));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            // Nếu không tìm thấy, quay về MainActivity
            Log.w("ChangePassword", "AccountSecurityActivity not found, navigating to MainActivity");
            Intent intent = new Intent(this, com.example.lab2.activity.MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy đổi mật khẩu")
                .setMessage("Bạn có chắc chắn muốn hủy quá trình đổi mật khẩu?")
                .setPositiveButton("Hủy", (dialog, which) -> {
                    super.onBackPressed();
                })
                .setNegativeButton("Tiếp tục", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}