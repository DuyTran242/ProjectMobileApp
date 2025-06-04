package com.example.lab2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.example.lab2.R;

public class AccountSecurityActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);

        initViews();
        setupToolbar();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_account_security);
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
        // Xử lý click cho các mục trong Account Security
        findViewById(R.id.row_user_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Điều hướng tới ProfileActivity
                startActivity(new android.content.Intent(AccountSecurityActivity.this, ProfileActivity.class));
            }
        });

        findViewById(R.id.row_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Điều hướng tới ChangePasswordActivity (cần tạo)
                // startActivity(new Intent(AccountSecurityActivity.this, ChangePasswordActivity.class));
            }
        });

        findViewById(R.id.row_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Điều hướng tới VerifyPasswordActivity trước khi đổi mật khẩu
                Intent intent = new Intent(AccountSecurityActivity.this, VerifyPasswordActivity.class);
                startActivity(intent);
            }
        });

    }
}