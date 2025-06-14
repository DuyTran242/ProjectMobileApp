package com.example.lab2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab2.R;
import com.example.lab2.Zalopay.Api.CreateOrder;
import com.example.lab2.retrofit.ApiBanHang;
import com.example.lab2.retrofit.RetrofitClient;
import com.example.lab2.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DecimalFormat;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class ThanhToanActivity extends AppCompatActivity implements OnMapReadyCallback {
    Toolbar toolbar;
    TextView txttongtien, txtsodt, txtemail;
    EditText edtdiachi;
    AppCompatButton btndathang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    long tongTien;
    int totalItem;
    ImageView imgMap;


    AppCompatButton btnZaloPay, btnMomo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thanh_toan);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo ZaloPay SDK
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        initView();
        countItem();
        initControll();

        // THÊM: Thiết lập event handlers cho các nút thanh toán
        setupPaymentButtons();
        // Load bản đồ động
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void countItem() {
        totalItem = 0;
        for(int i = 0; i < Utils.mangmuahang.size(); i++) {
            totalItem += Utils.mangmuahang.get(i).getSoluong();
        }
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toobartt);
        txttongtien = findViewById(R.id.txttongtien2);
        txtsodt = findViewById(R.id.txtsdt);
        txtemail = findViewById(R.id.txtmail);
        edtdiachi = findViewById(R.id.edtdiachi);
        btndathang = findViewById(R.id.btndathang);


        // THÊM: Ánh xạ các nút thanh toán
        btnZaloPay = findViewById(R.id.btnZaloPay);
        btnMomo = findViewById(R.id.btnMomo);
    }
    // Google map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng dhNongLam = new LatLng(10.871455506152442, 106.79170805140154);
        googleMap.addMarker(new MarkerOptions()
                .position(dhNongLam)
                .title("Đại học Nông Lâm TP.HCM")
                .snippet("Khu phố 6, Linh Trung, TP. Thủ Đức"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dhNongLam, 17f));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void initControll() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongTien = getIntent().getLongExtra("tongtien", 0);

        // Hiển thị thông tin
        txttongtien.setText(decimalFormat.format(tongTien));
        txtemail.setText(Utils.user_current.getEmail());
        txtsodt.setText(Utils.user_current.getMobile());

        // THÊM: Xử lý nút đặt hàng truyền thống
        btndathang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datHangThuong();
            }
        });
    }

    // THÊM: Thiết lập event handlers cho các nút thanh toán
    private void setupPaymentButtons() {
        // Xử lý nút thanh toán ZaloPay
        btnZaloPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    thanhToanZaloPay();
                }
            }
        });

        // Xử lý nút thanh toán Momo (nếu có)
        btnMomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    // TODO: Implement Momo payment
                    Toast.makeText(ThanhToanActivity.this, "Momo payment coming soon!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // THÊM: Validate input trước khi thanh toán
    private boolean validateInput() {
        String diachi = edtdiachi.getText().toString().trim();

        if (TextUtils.isEmpty(diachi)) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            edtdiachi.requestFocus();
            return false;
        }

        if (Utils.mangmuahang == null || Utils.mangmuahang.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // THÊM: Phương thức thanh toán ZaloPay
    private void thanhToanZaloPay() {
        try {
            // Chuyển đến OrderPaymentActivity trước
            Intent intent = new Intent(ThanhToanActivity.this, OrderPaymentActivity.class);
            intent.putExtra("soluong", String.valueOf(totalItem));
            intent.putExtra("total", (double) tongTien);
            intent.putExtra("diachi", edtdiachi.getText().toString().trim());
            intent.putExtra("email", Utils.user_current.getEmail());
            intent.putExtra("sdt", Utils.user_current.getMobile());

            Log.d("ThanhToan", "Chuyển đến OrderPaymentActivity với tổng tiền: " + tongTien);
            startActivity(intent);

        } catch (Exception e) {
            Log.e("ThanhToan", "Lỗi khi chuyển đến OrderPaymentActivity: " + e.getMessage());
            Toast.makeText(this, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // THÊM: Đặt hàng thường (không qua ZaloPay)
    private void datHangThuong() {
        String str_diachi = edtdiachi.getText().toString().trim();
        if (TextUtils.isEmpty(str_diachi)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
        } else {
            // Gọi API tạo đơn hàng thường
            String str_email = Utils.user_current.getEmail();
            String str_sdt = Utils.user_current.getMobile();
            int id = Utils.user_current.getId();
            Log.d("test", new Gson().toJson(Utils.mangmuahang));

            compositeDisposable.add(apiBanHang.createOrder(str_email, str_sdt, String.valueOf(tongTien),
                            id, str_diachi, totalItem, new Gson().toJson(Utils.mangmuahang))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            userModel -> {
                                Toast.makeText(getApplicationContext(), "Thành công", Toast.LENGTH_SHORT).show();
                                Utils.mangmuahang.clear();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    ));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}