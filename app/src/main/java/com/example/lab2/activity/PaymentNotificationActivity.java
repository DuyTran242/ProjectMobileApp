package com.example.lab2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2.R;

public class PaymentNotificationActivity extends AppCompatActivity {

    private ImageView btnClose;
    private TextView tvTransactionId;
    private TextView tvOrderId;
    private TextView tvTransactionTime;
    private TextView tvTotalAmount;
    private Button btnTrackOrder;
    private Button btnContinueShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_notification);

        // Ánh xạ views
        btnClose             = findViewById(R.id.btnClose);
        tvTransactionId      = findViewById(R.id.tvTransactionId);
        tvOrderId            = findViewById(R.id.tvOrderId);
        tvTransactionTime    = findViewById(R.id.tvTransactionTime);
        tvTotalAmount        = findViewById(R.id.tvTotalAmount);
        btnTrackOrder        = findViewById(R.id.btnTrackOrder);
        btnContinueShopping  = findViewById(R.id.btnContinueShopping);

        // Lấy dữ liệu từ Intent (nếu có)
        Intent intent = getIntent();
        String orderId       = intent.getStringExtra("orderId");
        String transactionId = intent.getStringExtra("transactionId");
        String time          = intent.getStringExtra("transactionTime");
        String amount        = intent.getStringExtra("totalAmount");

        // Hiển thị dữ liệu (nếu null thì dùng giá trị mặc định)
        tvOrderId.setText(orderId       != null ? orderId       : "#DH001234");
        tvTransactionId.setText(transactionId != null ? transactionId : "ZP240608153045");
        tvTransactionTime.setText(time  != null ? time          : "08/06/2025 - 15:30");
        tvTotalAmount.setText(amount    != null ? amount        : "1.250.000 ₫");

        // Đóng màn hình khi nhấn nút Close
        btnClose.setOnClickListener(v -> finish());

        // Nút "Theo dõi đơn hàng" — chỉ đóng Activity để quay về
        btnTrackOrder.setOnClickListener(v -> finish());

        // Nút "Tiếp tục mua sắm" — quay về MainActivity (xóa stack nếu cần)
        btnContinueShopping.setOnClickListener(v -> {
            Intent shopIntent = new Intent(PaymentNotificationActivity.this, MainActivity.class);
            shopIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(shopIntent);
            finish();
        });
    }
}
