package com.example.lab2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangKyActivity extends AppCompatActivity {

    EditText email, pass, repass, username, mobile;
    AppCompatButton button;
    SignInButton btnGoogle; // Đã sửa kiểu thành SignInButton

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 1000;

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
        initControl();
        initGoogleSignIn();
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        email = findViewById(R.id.txtemail);
        pass = findViewById(R.id.txtpassword);
        repass = findViewById(R.id.txtrepass);
        username = findViewById(R.id.txtusername);
        mobile = findViewById(R.id.txtphone);
        button = findViewById(R.id.btndangky);
        btnGoogle = findViewById(R.id.btnGoogleSignUp); // Đảm bảo XML đã có ID này
    }

    private void initControl() {
        button.setOnClickListener(v -> dangKi());
        btnGoogle.setOnClickListener(v -> signInWithGoogle());
    }

    private void dangKi() {
        String email_str = email.getText().toString();
        String pass_str = pass.getText().toString();
        String repass_str = repass.getText().toString();
        String username_str = username.getText().toString();
        String mobile_str = mobile.getText().toString();

        if (TextUtils.isEmpty(email_str) || TextUtils.isEmpty(pass_str) || TextUtils.isEmpty(repass_str)
                || TextUtils.isEmpty(username_str) || TextUtils.isEmpty(mobile_str)) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_LONG).show();
        } else if (!pass_str.equals(repass_str)) {
            Toast.makeText(getApplicationContext(), "Pass chưa khớp", Toast.LENGTH_LONG).show();
        } else {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email_str, pass_str)
                    .addOnCompleteListener(DangKyActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                postData(email_str, pass_str, username_str, mobile_str, user.getUid());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Email đã tồn tại", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void postData(String email_str, String pass_str, String username_str, String mobile_str, String uid) {
        compositeDisposable.add(apiBanHang.dangKi(email_str, pass_str, username_str, mobile_str, uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                Utils.user_current.setEmail(email_str);
                                Utils.user_current.setPass(pass_str);
                                Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show()
                ));
    }

    private void initGoogleSignIn() {
        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // lấy từ google-services.json
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String email = firebaseUser.getEmail();
                            String uid = firebaseUser.getUid();
                            String username = firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "GoogleUser";
                            String phone = "0000000000"; // cho phép cập nhật sau
                            postData(email, "GOOGLE_AUTH", username, phone, uid);
                        }
                    } else {
                        Toast.makeText(DangKyActivity.this, "Xác thực Firebase thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
