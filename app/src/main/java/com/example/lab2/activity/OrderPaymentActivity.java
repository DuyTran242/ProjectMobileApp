// filepath: d:\Doanmobile\app\src\main\java\com\example\lab2\activity\OrderPaymentActivity.java
package com.example.lab2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2.R;
import com.example.lab2.Zalopay.Api.CreateOrder;
import com.example.lab2.retrofit.ApiBanHang;
import com.example.lab2.retrofit.RetrofitClient;
import com.example.lab2.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONObject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class OrderPaymentActivity extends AppCompatActivity {

    TextView txtSoluong;
    TextView txtTongTien;
    TextView btnThanhToan;

    // THÊM: Các biến để lưu thông tin đơn hàng
    private String diaChi;
    private String email;
    private String sdt;
    private int totalItem;
    private double totalAmount;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiBanHang apiBanHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment);

        // Cho phép gọi mạng trên main thread (chỉ dùng trong development)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);

        // Khởi tạo ZaloPay SDK
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        // THÊM: Khởi tạo API
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        // Ánh xạ view
        txtSoluong = findViewById(R.id.tvItemCount);
        txtTongTien = findViewById(R.id.tvTotalAmount);
        btnThanhToan = findViewById(R.id.btnProceedPayment);

        // THÊM: Lấy dữ liệu từ Intent
        getDataFromIntent();

        // Hiển thị dữ liệu
        displayOrderInfo();

        // Xử lý click thanh toán
        btnThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processZaloPayPayment();
            }
        });
    }

    // THÊM: Lấy dữ liệu từ Intent
    private void getDataFromIntent() {
        Intent intent = getIntent();
        String soLuong = intent.getStringExtra("soluong");
        totalAmount = intent.getDoubleExtra("total", 0.0);
        diaChi = intent.getStringExtra("diachi");
        email = intent.getStringExtra("email");
        sdt = intent.getStringExtra("sdt");

        try {
            totalItem = Integer.parseInt(soLuong);
        } catch (NumberFormatException e) {
            totalItem = 0;
        }

        Log.d("OrderPayment", "Nhận dữ liệu: soluong=" + soLuong + ", total=" + totalAmount +
                ", diachi=" + diaChi + ", email=" + email + ", sdt=" + sdt);
    }

    // THÊM: Hiển thị thông tin đơn hàng
    private void displayOrderInfo() {
        txtSoluong.setText(totalItem + " sản phẩm");
        txtTongTien.setText(String.format("%.0f ₫", totalAmount));
    }

    // THÊM: Xử lý thanh toán ZaloPay
    // Trong method processZaloPayPayment()
    private void processZaloPayPayment() {
        // Hiển thị loading
        btnThanhToan.setEnabled(false);
        btnThanhToan.setText("Đang xử lý...");

        try {
            CreateOrder orderApi = new CreateOrder();
            String totalString = String.format("%.0f", totalAmount);
            JSONObject data = orderApi.createOrder(totalString);

            Log.d("ZaloPay", "Response received: " + data.toString());

            String returnCode = data.optString("return_code", "0");
            String returnMessage = data.optString("return_message", "Unknown error");

            if ("1".equals(returnCode)) {
                String token = data.optString("zp_trans_token", "");
                if (!token.isEmpty()) {
                    Log.d("ZaloPay", "Token received: " + token);

                    ZaloPaySDK.getInstance().payOrder(
                            OrderPaymentActivity.this,
                            token,
                            "demozpdk://app",
                            new PayOrderListener() {
                                @Override
                                public void onPaymentSucceeded(String transId, String orderId, String amount) {
                                    Log.d("ZaloPay", "Payment succeeded");
                                    createOrderAfterPayment(transId, orderId);
                                }

                                @Override
                                public void onPaymentCanceled(String zpTransToken, String appTransId) {
                                    Log.d("ZaloPay", "Payment canceled");
                                    resetButton();
                                    Toast.makeText(OrderPaymentActivity.this, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPaymentError(ZaloPayError error, String zpTransToken, String appTransId) {
                                    Log.e("ZaloPay", "Payment error: " + error.toString());
                                    resetButton();
                                    Toast.makeText(OrderPaymentActivity.this, "Lỗi thanh toán: " + error.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                } else {
                    resetButton();
                    Toast.makeText(this, "Không nhận được token từ ZaloPay", Toast.LENGTH_SHORT).show();
                }
            } else {
                resetButton();
                Toast.makeText(this, "Lỗi tạo đơn ZaloPay: " + returnMessage, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e("ZaloPay", "Exception: " + e.getMessage(), e);
            resetButton();
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void resetButton() {
        btnThanhToan.setEnabled(true);
        btnThanhToan.setText("Thanh toán");
    }

    // THÊM: Tạo đơn hàng sau khi thanh toán thành công
    private void createOrderAfterPayment(String transId, String orderId) {
        compositeDisposable.add(
                apiBanHang.createOrder(
                                email,
                                sdt,
                                String.valueOf((long)totalAmount),
                                Utils.user_current.getId(),
                                diaChi,
                                totalItem,
                                new Gson().toJson(Utils.mangmuahang)
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                userModel -> {
                                    if (userModel.isSuccess()) {
                                        Log.d("OrderPayment", "Đơn hàng đã được tạo thành công");

                                        // Clear giỏ hàng
                                        Utils.mangmuahang.clear();

                                        // Chuyển đến trang thông báo thành công
                                        Intent successIntent = new Intent(OrderPaymentActivity.this, PaymentNotificationActivity.class);
                                        successIntent.putExtra("transactionId", transId);
                                        successIntent.putExtra("orderId", orderId);
                                        successIntent.putExtra("totalAmount", String.format("%.0f ₫", totalAmount));
                                        successIntent.putExtra("transactionTime", java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()));

                                        startActivity(successIntent);
                                        finish();
                                    } else {
                                        Toast.makeText(OrderPaymentActivity.this, "Lỗi tạo đơn hàng: " + userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                },
                                throwable -> {
                                    Log.e("OrderPayment", "Lỗi tạo đơn hàng: " + throwable.getMessage());
                                    Toast.makeText(OrderPaymentActivity.this, "Lỗi tạo đơn hàng: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        )
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}