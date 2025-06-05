package com.example.lab2.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lab2.R;

public class MessageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password); // tạm dùng layout này
        setTitle("Tin nhắn");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}