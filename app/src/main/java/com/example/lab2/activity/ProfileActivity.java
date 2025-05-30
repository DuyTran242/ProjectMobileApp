package com.example.lab2.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.bumptech.glide.Glide;
import com.example.lab2.R;
import com.example.lab2.utils.Utils;
import com.example.lab2.retrofit.ApiBanHang;
import com.example.lab2.retrofit.RetrofitClient;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.callback.ErrorInfo;
import com.google.android.material.appbar.MaterialToolbar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1002;

    private ImageView imgAvatar;
    private Button btnUploadAvatar, btnSave;
    private EditText etUsername, etEmail, etName, etPhone;
    private RadioGroup rgGender;
    private Spinner spinnerDay, spinnerMonth, spinnerYear;

    private ApiBanHang apiBanHang;
    private List<String> days, months, years;
    private String pendingAvatarUrl; // giữ URL mới sau upload

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // cho phép layout fit hệ thống inset
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        setContentView(R.layout.activity_profile);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 1. Ánh xạ Views
        imgAvatar       = findViewById(R.id.imgAvatar);
        btnUploadAvatar = findViewById(R.id.btnUploadAvatar);
        btnSave         = findViewById(R.id.btnSave);
        etUsername      = findViewById(R.id.etUsername);
        etEmail         = findViewById(R.id.etEmail);
        etName          = findViewById(R.id.etName);
        etPhone         = findViewById(R.id.etPhone);
        rgGender        = findViewById(R.id.rgGender);
        spinnerDay      = findViewById(R.id.spinnerDay);
        spinnerMonth    = findViewById(R.id.spinnerMonth);
        spinnerYear     = findViewById(R.id.spinnerYear);

        // 2. Khởi tạo Cloudinary
        Map<String,String> cfg = new HashMap<>();
        cfg.put("cloud_name","your_cloud_name");
        cfg.put("api_key",   "your_api_key");
        cfg.put("api_secret","your_api_secret");
        MediaManager.init(this, cfg);

        // 3. Khởi tạo Retrofit
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL)
                .create(ApiBanHang.class);

        // 4. Chuẩn bị Spinner ngày/tháng/năm
        days   = new ArrayList<>();
        months = new ArrayList<>();
        years  = new ArrayList<>();
        for(int i=1;i<=31;i++) days.add(String.valueOf(i));
        for(int m=1;m<=12;m++) months.add(String.valueOf(m));
        int y0 = Calendar.getInstance().get(Calendar.YEAR);
        for(int y=y0;y>=1900;y--) years.add(String.valueOf(y));
        ArrayAdapter<String> adD=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,days);
        ArrayAdapter<String> adM=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,months);
        ArrayAdapter<String> adY=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,years);
        adD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adY.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adD);
        spinnerMonth.setAdapter(adM);
        spinnerYear.setAdapter(adY);

        // 5. Load dữ liệu lên UI
        loadUserInfo();

        // 6. Event
        btnUploadAvatar.setOnClickListener(v -> pickImage());
        btnSave        .setOnClickListener(v -> saveUserInfo());
    }

    // Lấy user từ server về và bind vào form
    private void loadUserInfo() {
        int uid = Utils.user_current.getId();
        apiBanHang.getUserById(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(u->{
                    etUsername.setText(u.getUsername());
                    etEmail   .setText(u.getEmail());
                    etName    .setText(u.getName());
                    etPhone   .setText(u.getMobile());
                    if(u.getAvatarUrl()!=null){
                        Glide.with(this).load(u.getAvatarUrl()).into(imgAvatar);
                    }
                    switch(u.getGender()){
                        case "M": rgGender.check(R.id.rbMale);   break;
                        case "F": rgGender.check(R.id.rbFemale); break;
                        default:  rgGender.check(R.id.rbOther);  break;
                    }
                    if(u.getBirthdate()!=null){
                        String d=new SimpleDateFormat("yyyy-MM-dd").format(u.getBirthdate());
                        String[] p=d.split("-");
                        int iY=years.indexOf(p[0]);
                        int iM=months.indexOf(p[1].replaceFirst("^0+",""));
                        int iD=days.indexOf(p[2].replaceFirst("^0+",""));
                        if(iY>=0) spinnerYear.setSelection(iY);
                        if(iM>=0) spinnerMonth.setSelection(iM);
                        if(iD>=0) spinnerDay.setSelection(iD);
                    }
                },e-> Toast.makeText(this,"Load lỗi: "+e.getMessage(),Toast.LENGTH_LONG).show());
    }

    // Mở gallery
    private void pickImage(){
        Intent it=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(it,PICK_IMAGE);
    }

    @Override protected void onActivityResult(int req,int res,@Nullable Intent d){
        super.onActivityResult(req,res,d);
        if(req==PICK_IMAGE && res==RESULT_OK && d!=null && d.getData()!=null){
            uploadToCloudinary(d.getData());
        }
    }

    // Upload lên Cloudinary, chỉ lưu URL vào pendingAvatarUrl
    private void uploadToCloudinary(Uri uri){
        Map<String,Object> opts=new HashMap<>();
        opts.put("unsigned","android_preset");
        opts.put("folder","users/avatars");
        MediaManager.get().upload(uri).options(opts)
                .callback(new UploadCallback(){
                    @Override public void onStart(String id){}
                    @Override public void onProgress(String id,long b,long t){}
                    @Override public void onSuccess(String id,Map res){
                        String url=(String)res.get("secure_url");
                        Glide.with(ProfileActivity.this).load(url).into(imgAvatar);
                        pendingAvatarUrl=url;
                        // cập nhật luôn cache nếu cần UI khác tự động reflect
                        Utils.user_current.setAvatarUrl(url);
                    }
                    @Override public void onError(String id,ErrorInfo err){
                        Toast.makeText(ProfileActivity.this,"Upload lỗi: "+err.getDescription(),
                                Toast.LENGTH_LONG).show();
                    }
                    @Override public void onReschedule(String id,ErrorInfo err){}
                }).dispatch();
    }

    // Gộp tất cả và gọi 1 lần duy nhất updateUser
    @SuppressLint("CheckResult")
    private void saveUserInfo(){
        String name=etName.getText().toString().trim();
        String mob =etPhone.getText().toString().trim();
        String bd  = spinnerYear.getSelectedItem()+"-"
                +spinnerMonth.getSelectedItem()+"-"
                +spinnerDay.getSelectedItem();
        String gen="O";
        int gid=rgGender.getCheckedRadioButtonId();
        if(gid==R.id.rbMale)   gen="M";
        if(gid==R.id.rbFemale) gen="F";
        String avu=(pendingAvatarUrl!=null)?pendingAvatarUrl:Utils.user_current.getAvatarUrl();

        RequestBody bId  =RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(Utils.user_current.getId()));
        RequestBody bNm  =RequestBody.create(MediaType.parse("text/plain"),name);
        RequestBody bMb  =RequestBody.create(MediaType.parse("text/plain"),mob);
        RequestBody bG   =RequestBody.create(MediaType.parse("text/plain"),gen);
        RequestBody bBd  =RequestBody.create(MediaType.parse("text/plain"),bd);
        RequestBody bAv  =RequestBody.create(MediaType.parse("text/plain"),avu!=null?avu:"");

        apiBanHang.updateUser(bId,bNm,bMb,bG,bBd,bAv)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r->{
                    if(r.isSuccess()){
                        Utils.user_current = r.getUser();
                        Toast.makeText(this,"Cập nhật thành công",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this,"Server lỗi: "+r.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                },e-> Toast.makeText(this,"Kết nối lỗi: "+e.getMessage(),
                        Toast.LENGTH_LONG).show());
    }
}
