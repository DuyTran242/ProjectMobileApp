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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;

import com.google.firebase.auth.FacebookAuthProvider;

import java.util.List;

public class DangNhapActivity extends AppCompatActivity {

    TextView txtdangki, txtresetpass;
    EditText email_txt, pass_txt;
    AppCompatButton btndangnhap;
    SignInButton btnGoogleLogin;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isLogin = false;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int REQ_ONE_TAP = 1000;

    private CallbackManager callbackManager;
    AppCompatButton btnFacebookLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.activity_dang_nhap);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        callbackManager = CallbackManager.Factory.create();

        initView();
        initControl();

        Paper.book().destroy();

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initView() {
        Paper.init(this);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        txtdangki = findViewById(R.id.txtdangki);
        txtresetpass = findViewById(R.id.txtresetpass);
        email_txt = findViewById(R.id.email_id);
        pass_txt = findViewById(R.id.pass_id);
        btndangnhap = findViewById(R.id.btndangnhap);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (Paper.book().read("email") != null && Paper.book().read("pass") != null) {
            email_txt.setText(Paper.book().read("email"));
            pass_txt.setText(Paper.book().read("pass"));
        }
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);
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

        btnGoogleLogin.setOnClickListener(v -> {
            // Đăng xuất để không tự động đăng nhập lại
            firebaseAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, REQ_ONE_TAP);
            });
        });

        btnFacebookLogin.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(
                    DangNhapActivity.this,
                    List.of("email", "public_profile")
            );
        });
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken token = loginResult.getAccessToken();
                        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

                        firebaseAuth.signInWithCredential(credential)
                                .addOnCompleteListener(DangNhapActivity.this, task -> {
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        if (firebaseUser != null) {
                                            String email = firebaseUser.getEmail();
                                            dangNhap(email, "", "facebook");
                                        }
                                    } else {
                                        Toast.makeText(DangNhapActivity.this, "Xác thực Facebook thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(DangNhapActivity.this, "Hủy đăng nhập Facebook", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(DangNhapActivity.this, "Lỗi đăng nhập Facebook", Toast.LENGTH_SHORT).show();
                    }
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

        // Google One Tap
        if (requestCode == REQ_ONE_TAP) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    firebaseAuth.signInWithCredential(credential)
                            .addOnCompleteListener(this, authTask -> {
                                if (authTask.isSuccessful()) {
                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        String email = firebaseUser.getEmail();
                                        dangNhap(email, "", "google");
                                        String username = firebaseUser.getDisplayName();
                                        String mobile = "0";
                                        String uid = firebaseUser.getUid();

                                        compositeDisposable.add(apiBanHang.dangNhap(email, "", "google")
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
                                                        // Nếu chưa có tài khoản thì đăng ký
                                                        compositeDisposable.add(apiBanHang.dangKi(email, "", username, mobile, uid)
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(registerModel -> {
                                                                    if (registerModel.isSuccess()) {
                                                                        dangNhap(email, "", "google");
                                                                    } else {
                                                                        Toast.makeText(this, "Đăng ký tài khoản Google thất bại", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }, err -> {
                                                                    Toast.makeText(this, "Lỗi đăng ký: " + err.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }));
                                                    }
                                                }, err -> {
                                                    Toast.makeText(this, "Lỗi đăng nhập: " + err.getMessage(), Toast.LENGTH_SHORT).show();
                                                }));
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

        // Facebook Login
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.user_current != null &&
                Utils.user_current.getEmail() != null &&
                Utils.user_current.getPass() != null) {
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