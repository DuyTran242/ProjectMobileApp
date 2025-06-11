package com.example.lab2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab2.R;
import com.example.lab2.retrofit.ApiBanHang;
import com.example.lab2.retrofit.RetrofitClient;
import com.example.lab2.utils.Utils;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangNhapActivity extends AppCompatActivity {

    TextView txtdangki, txtresetpass;
    EditText email_txt, pass_txt;
    AppCompatButton btndangnhap;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isLogin = false;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
        initControl();
        Paper.book().destroy(); // Xoá dữ liệu cũ (nếu có)

        // Khởi tạo Google Sign-In
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id))
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                .setAutoSelectEnabled(true)
                .build();
    }

    private void initView() {
        Paper.init(this);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        txtdangki = findViewById(R.id.txtdangki);
        txtresetpass = findViewById(R.id.txtresetpass);
        email_txt = findViewById(R.id.email_id);
        pass_txt = findViewById(R.id.pass_id);
        btndangnhap = findViewById(R.id.btndangnhap);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (Paper.book().read("email") != null && Paper.book().read("pass") != null) {
            email_txt.setText(Paper.book().read("email"));
            pass_txt.setText(Paper.book().read("pass"));
        }
    }

    private void initControl() {
        txtdangki.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), DangKyActivity.class));
        });

        txtresetpass.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ResetPassActivity.class));
        });

        btndangnhap.setOnClickListener(v -> {
            String str_email = email_txt.getText().toString().trim();
            String str_pass = pass_txt.getText().toString().trim();

            if (TextUtils.isEmpty(str_email)) {
                Toast.makeText(this, "Bạn chưa nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(str_pass)) {
                Toast.makeText(this, "Bạn chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            Paper.book().write("email", str_email);
            Paper.book().write("pass", str_pass);

            if (user != null) {
                dangNhap(str_email, str_pass, "normal");
            } else {
                firebaseAuth.signInWithEmailAndPassword(str_email, str_pass)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                dangNhap(str_email, str_pass, "normal");
                            } else {
                                Toast.makeText(this, "Xác thực Firebase thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        SignInButton btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnGoogleLogin.setOnClickListener(v -> {
            oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener(this, result -> {
                        try {
                            startIntentSenderForResult(result.getPendingIntent().getIntentSender(),
                                    REQ_ONE_TAP, null, 0, 0, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        Toast.makeText(this, "Không thể đăng nhập bằng Google", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void dangNhap(String email, String pass, String authType) {
        compositeDisposable.add(apiBanHang.dangNhap(email, pass, authType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userModel -> {
                            if (userModel.isSuccess()) {
                                isLogin = true;
                                Paper.book().write("isLogin", isLogin);
                                Utils.user_current = userModel.getResult().get(0);
                                Paper.book().write("user", Utils.user_current);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, "Lỗi kết nối máy chủ", Toast.LENGTH_LONG).show();
                        }
                ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    firebaseAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        String email = firebaseUser.getEmail();
                                        dangNhap(email, "", "google"); // Pass rỗng, dùng auth_type="google"
                                    }
                                } else {
                                    Toast.makeText(this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.user_current.getEmail() != null && Utils.user_current.getPass() != null) {
            email_txt.setText(Utils.user_current.getEmail());
            pass_txt.setText(Utils.user_current.getPass());
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
