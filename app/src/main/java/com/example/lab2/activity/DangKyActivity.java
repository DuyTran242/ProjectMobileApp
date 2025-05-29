package com.example.lab2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab2.R;
import com.example.lab2.retrofit.ApiBanHang;
import com.example.lab2.retrofit.RetrofitClient;
import com.example.lab2.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangKyActivity extends AppCompatActivity {
    EditText email, pass, repass,username, mobile;
    AppCompatButton button;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ky);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        initControll();
    }

    private void initControll() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangKi();
            }
        });

    }
    private void dangKi() {
        String email_str = email.getText().toString();
        String pass_str = pass.getText().toString();
        String repass_str = repass.getText().toString();
        String username_str = username.getText().toString();
        String mobile_str = mobile.getText().toString();
        if(TextUtils.isEmpty(email_str)) {
            Toast.makeText(getApplicationContext(), "Chưa nhập vào email", Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(pass_str)) {
            Toast.makeText(getApplicationContext(), "Chưa nhập vào pass", Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(repass_str)) {
            Toast.makeText(getApplicationContext(), "Chuưa nhập vào repass", Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(username_str)) {
            Toast.makeText(getApplicationContext(), "Chưa nhập vào name", Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(mobile_str)) {
            Toast.makeText(getApplicationContext(), "Chưa nhập số điện thoai", Toast.LENGTH_LONG).show();
        }else{
            if(pass_str.equals(repass_str)) {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(email_str, pass_str)
                        .addOnCompleteListener(DangKyActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if(user != null) {
                                        postData(email_str, pass_str, username_str, mobile_str, user.getUid());
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Email đã tồn tại", Toast.LENGTH_LONG).show();
                                }

                            }
                        });

            }else{
                Toast.makeText(getApplicationContext(), "Pass chưa khớp", Toast.LENGTH_LONG).show();

            }
        }

    }
    private void postData(String email_str, String pass_str, String username_str, String mobile_str, String uid) {
        compositeDisposable.add(apiBanHang.dangKi(email_str,pass_str, username_str, mobile_str, uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()) {
                                Utils.user_current.setEmail(email_str);
                                Utils.user_current.setPass(pass_str);
                                // Toast.makeText(getApplicationContext(), "thanh cong", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }

                ));
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        email = findViewById(R.id.txtemail);
        pass = findViewById(R.id.txtpassword);
        repass = findViewById(R.id.txtrepass);
        button = findViewById(R.id.btndangky);
        username = findViewById(R.id.txtusername);
        mobile = findViewById(R.id.txtphone);


    }
}