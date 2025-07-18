package com.example.lab2.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lab2.R;
import com.example.lab2.adapter.LoaiSpAdapter;
import com.example.lab2.adapter.SanPhamMoiAdapter;
import com.example.lab2.model.LoaiSp;
import com.example.lab2.model.SanPhamMoi;
import com.example.lab2.model.User;
import com.example.lab2.retrofit.ApiBanHang;
import com.example.lab2.retrofit.RetrofitClient;
import com.example.lab2.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity  {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerview;
    NavigationView navigationview;
    ListView listviewmanhinhchinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // THÊM: Đảm bảo giỏ hàng được khởi tạo để tránh NullPointerException
        if (Utils.manggiohang == null) {
            Utils.manggiohang = new ArrayList<>();
        }
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        Paper.init(this);
        /**
         * XỬ LÝ THÔNG TIN USER ĐĂNG NHẬP
         *
         * Mục đích: Load thông tin user đã đăng nhập trước đó từ local storage
         * và khởi tạo Utils.user_current để tránh NullPointerException
         *
         * Luồng hoạt động:
         * 1. Kiểm tra xem có user nào đã lưu trong Paper (SharedPreferences) không
         * 2. Nếu có: Load user đó vào Utils.user_current
         * 3. Nếu không: Tạo user mặc định với thông tin "Guest"
         *
         * Lưu ý:
         * - Paper.book().read("user") trả về User object hoặc null
         * - Utils.user_current được sử dụng xuyên suốt app để hiển thị thông tin user
         * - Việc khởi tạo user mặc định giúp tránh crash khi truy cập AccountSecurityActivity
         */
        if (Paper.book().read("user") != null) {
            User user = Paper.book().read("user");
            Utils.user_current = user;
            Log.d("MainActivity", "User loaded: " + user.getUsername());
        } else {
            Log.d("MainActivity", "No user found in Paper");
            // Khởi tạo user mặc định để tránh null
            Utils.user_current = new User();
            Utils.user_current.setUsername("Guest");
            Utils.user_current.setName("Guest User");
        }

        if(Paper.book().read("user") != null) {
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }
        getToken();
        AnhXa();
        ActionBar();

        // THÊM: Xử lý BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true; // Đã ở trang chủ
            }
            if (id == R.id.nav_message) {
                startActivity(new Intent(this, MessageActivity.class));
                return true;
            }
            if (id == R.id.nav_account) {
                startActivity(new Intent(this, AccountSecurityActivity.class));
                return true;
            }
            if (id == R.id.nav_favorite) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            }
            return false;
        });

        if(isConnected(this)) {
           // Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
        }else{
            Toast.makeText(getApplicationContext(), "không co ket noi", Toast.LENGTH_LONG).show();
        }

    }
    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if(!TextUtils.isEmpty(s)) {
                            compositeDisposable.add(apiBanHang.updateToken(String.valueOf(Utils.user_current.getId()), s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            messageModel -> {

                                            },
                                            throwable -> {
                                                Log.d("log", throwable.getMessage());
                                            }
                                    )
                            );

                        }

                    }
                });

    }

    private void getEventClick() {
        listviewmanhinhchinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                switch (i) {
                    case 0: // Điện thoại (id=1)
                        Intent dienthoai = new Intent(getApplicationContext(), DienThoaiMainActivity.class);
                        dienthoai.putExtra("loai", 1);
                        startActivity(dienthoai);
                        break;
                    case 1: // Laptop (id=2)
                        Intent laptop = new Intent(getApplicationContext(), DienThoaiMainActivity.class);
                        laptop.putExtra("loai", 2);
                        startActivity(laptop);
                        break;
                    case 2: // Trang chủ (id=3)
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 3: // Thông tin (id=4)
                        Intent thongtin = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(thongtin);
                        break;
                    case 4: // Liên hệ (id=5)
                        Intent lienhe = new Intent(getApplicationContext(), MessageActivity.class);
                        startActivity(lienhe);
                        break;
                    case 5: // Lịch sử đơn hàng (id=6)
                        Intent lichsu = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(lichsu);
                        break;
                    case 6: // Hồ Sơ của tôi (id=7)
                        Intent hoso = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(hoso);
                        break;
                    case 7: // Quản lí (được thêm từ code)
                        Intent quanli = new Intent(getApplicationContext(), QuanLiActivity.class);
                        startActivity(quanli);
                        break;
                    case 8: // Đăng xuất (được thêm từ code)
                        Paper.book().delete("user");
                        FirebaseAuth.getInstance().signOut();
                        Intent dangxuat = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(dangxuat);
                        finish();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Chức năng chưa được triển khai", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                     sanPhamMoiModel -> {
                       if(sanPhamMoiModel.isSuccess()) {
                        mangSpMoi = sanPhamMoiModel.getResult();
                        spAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                        recyclerview.setAdapter(spAdapter);
                    }
                },
                throwable -> {
                    Toast.makeText(getApplicationContext(), "Ko kết nối dc voi server"+throwable.getMessage(),Toast.LENGTH_LONG).show();
                }
        ));
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if(loaiSpModel.isSuccess()) {
                              //  Toast.makeText(getApplicationContext(), loaiSpModel.getResult().get(0).getTensanpham(), Toast.LENGTH_LONG).show();
                                mangloaisp = loaiSpModel.getResult();
                                mangloaisp.add(new LoaiSp("Quản lí", ""));
                                mangloaisp.add(new LoaiSp("Đăng xuất", ""));
                                loaiSpAdapter = new LoaiSpAdapter(mangloaisp, getApplicationContext());
                                listviewmanhinhchinh.setAdapter(loaiSpAdapter);
                            }
                        },
                        throwable -> {
                            Log.e("ERROR", "Error fetching data", throwable);
                            Toast.makeText(getApplicationContext(), "Lỗi kết nối, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void ActionViewFlipper() {
        List<String> mangQuangCao = new ArrayList<>();
        mangQuangCao.add("https://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-Le-hoi-phu-kien-800-300.png");
        mangQuangCao.add("https://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-HC-Tra-Gop-800-300.png");
        mangQuangCao.add("https://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-big-ky-nguyen-800-300.jpg");
        for (int i = 0; i < mangQuangCao.size(); i++) {
            ImageView imageview = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangQuangCao.get(i)).into(imageview);
            imageview.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageview);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void AnhXa() {
        imgsearch = findViewById(R.id.imgsearch);
        toolbar = findViewById(R.id.toolbarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewflipper);
        recyclerview = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setHasFixedSize(true);
        navigationview = findViewById(R.id.navigationView);
        listviewmanhinhchinh = findViewById(R.id.listviewmanhinhchinh);
        drawerLayout = findViewById(R.id.drawerlayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);
        // khoi tao Adapter
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
        if(Utils.manggiohang == null) {
            Utils.manggiohang = new ArrayList<>();
        }else{
            int totalItem = 0;
            for(int i = 0; i < Utils.manggiohang.size(); i++) {
                totalItem += Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });
        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for(int i = 0; i < Utils.manggiohang.size(); i++) {
            totalItem += Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected())) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}