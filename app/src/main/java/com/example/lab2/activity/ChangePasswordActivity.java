package com.example.lab2.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2.R;
import com.example.lab2.retrofit.ApiBanHang;
import com.example.lab2.retrofit.RetrofitClient;
import com.example.lab2.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.appbar.MaterialToolbar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChangePasswordActivity extends AppCompatActivity {
    TextInputEditText etNew, etConfirm;
    CompositeDisposable cd = new CompositeDisposable();
    ApiBanHang api;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_change_password);

        // Thiết lập toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_change_password);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Khởi tạo API
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        // Nhận thông tin từ Intent (nếu có)
        receiveUserInfo();

        // Ánh xạ views
        etNew = findViewById(R.id.et_new_password);
        etConfirm = findViewById(R.id.et_confirm_password);
        findViewById(R.id.btn_submit_change_password).setOnClickListener(v -> submit());

        // Log để debug
        android.util.Log.d("ChangePassword", "ChangePasswordActivity started for user: " +
                (Utils.user_current != null ? Utils.user_current.getUsername() : "null"));
    }

    /**
     * Nhận thông tin user từ Intent
     */
    private void receiveUserInfo() {
        if (getIntent() != null) {
            int userId = getIntent().getIntExtra("user_id", 0);
            String username = getIntent().getStringExtra("username");

            android.util.Log.d("ChangePassword", "Received user info - ID: " + userId + ", Username: " + username);

            // Có thể hiển thị thông tin user trong giao diện nếu cần
            if (getSupportActionBar() != null && username != null) {
                getSupportActionBar().setSubtitle("Đổi mật khẩu cho: " + username);
            }
        }
    }

    /**
     * Xử lý submit đổi mật khẩu
     */
    private void submit() {
        String newPassword = etNew.getText().toString().trim();
        String confirmPassword = etConfirm.getText().toString().trim();

        // Validate
        if (newPassword.length() < 6) {
            etNew.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirm.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        if (Utils.user_current == null || Utils.user_current.getId() <= 0) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin user", Toast.LENGTH_SHORT).show();
            return;
        }

        // Debug: Log thông tin request
        android.util.Log.d("ChangePassword", "=== CALLING API ===");
        android.util.Log.d("ChangePassword", "User ID: " + Utils.user_current.getId());
        android.util.Log.d("ChangePassword", "New Password: " + newPassword);
        android.util.Log.d("ChangePassword", "API URL: " + Utils.BASE_URL + "update_password.php");

        cd.add(api.updatePassword(Utils.user_current.getId(), newPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    android.util.Log.d("ChangePassword", "API Response: " + resp.getMessage());
                    Toast.makeText(this, resp.getMessage(), Toast.LENGTH_SHORT).show();
                    if (resp.isSuccess()) {
                        finish();
                    }
                }, error -> {
                    // Chi tiết lỗi JSON
                    android.util.Log.e("ChangePassword", "API Error: " + error.getMessage());
                    android.util.Log.e("ChangePassword", "Error Type: " + error.getClass().getSimpleName());

                    if (error instanceof com.google.gson.JsonSyntaxException) {
                        android.util.Log.e("ChangePassword", "JSON Syntax Error - Server trả về HTML thay vì JSON");
                        Toast.makeText(this, "Lỗi server: Không thể parse JSON", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    @Override
    protected void onDestroy() {
        cd.clear();
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}