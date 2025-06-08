package com.example.lab2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lab2.R;
import com.example.lab2.utils.Utils;
import com.google.android.material.appbar.MaterialToolbar;

public class AccountSecurityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);

        try {
            // Thiết lập toolbar
            MaterialToolbar toolbar = findViewById(R.id.toolbar_account_security);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            // Hiển thị thông tin user từ database
            initUserInfo();

            // Thiết lập các sự kiện click
            initClickEvents();

        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    /**
     * Hiển thị thông tin user từ database
     * Database có 3 user: ID 4,5,6 như đã thấy trong SQL
     */
    private void initUserInfo() {
        TextView tvUsername = findViewById(R.id.tv_username_value);
        if (tvUsername != null) {
            if (Utils.user_current != null && Utils.user_current.getUsername() != null) {
                // Hiển thị username thực từ database
                tvUsername.setText(Utils.user_current.getUsername());

                // Log để debug - xem user nào đang đăng nhập
                android.util.Log.d("AccountSecurity", "Current user: " +
                        Utils.user_current.getUsername() + " (ID: " + Utils.user_current.getId() + ")");
            } else {
                tvUsername.setText("Chưa đăng nhập");
            }
        }
    }

    /**
     * Thiết lập các sự kiện click cho các mục menu
     */
    private void initClickEvents() {
        // 1. Click "Hồ sơ của tôi"
        LinearLayout rowUserProfile = findViewById(R.id.row_user_profile);
        if (rowUserProfile != null) {
            rowUserProfile.setOnClickListener(v -> {
                // Kiểm tra user đã đăng nhập chưa
                if (isUserLoggedIn()) {
                    startActivity(new Intent(this, ProfileActivity.class));
                } else {
                    showLoginRequired();
                }
            });
        }

        // 2. Click "Đổi mật khẩu" - ĐIỀU HƯỚNG CHÍNH
        LinearLayout rowChangePassword = findViewById(R.id.row_change_password);
        if (rowChangePassword != null) {
            rowChangePassword.setOnClickListener(v -> {
                // Kiểm tra user đã đăng nhập chưa
                if (isUserLoggedIn()) {
                    // Điều hướng đến ChangePasswordActivity
                    Intent intent = new Intent(AccountSecurityActivity.this, ChangePasswordActivity.class);

                    // (Tùy chọn) Truyền thêm thông tin user nếu cần
                    intent.putExtra("user_id", Utils.user_current.getId());
                    intent.putExtra("username", Utils.user_current.getUsername());

                    startActivity(intent);

                    android.util.Log.d("AccountSecurity", "Navigating to ChangePasswordActivity for user: " +
                            Utils.user_current.getUsername());
                } else {
                    showLoginRequired();
                }
            });
        }
    }

    /**
     * Kiểm tra user đã đăng nhập thực sự chưa
     * @return true nếu đã đăng nhập (không phải Guest)
     */
    private boolean isUserLoggedIn() {
        return Utils.user_current != null &&
                Utils.user_current.getId() > 0 &&
                !"Guest".equals(Utils.user_current.getUsername()) &&
                !"Chưa đăng nhập".equals(Utils.user_current.getUsername());
    }

    /**
     * Hiển thị thông báo yêu cầu đăng nhập
     */
    private void showLoginRequired() {
        android.widget.Toast.makeText(this,
                "Vui lòng đăng nhập để sử dụng tính năng này",
                android.widget.Toast.LENGTH_SHORT).show();

        // (Tùy chọn) Chuyển đến màn hình đăng nhập
        // startActivity(new Intent(this, DangNhapActivity.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}