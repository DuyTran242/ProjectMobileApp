package com.example.lab2.retrofit;

import com.example.lab2.model.DonHangModel;
import com.example.lab2.model.LoaiSpModel;
import com.example.lab2.model.MessageModel;
import com.example.lab2.model.SanPhamMoiModel;
import com.example.lab2.model.UpdateUserResponse;
import com.example.lab2.model.User;
import com.example.lab2.model.UserModel;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiBanHang {
    @GET("getloaisp.php")
    Observable<LoaiSpModel> getLoaiSp();

    @GET("getspmoi.php")
    Observable<SanPhamMoiModel> getSpMoi();
    @POST("chitiet.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getSanPham(
      @Field("page") int page,
      @Field("loai") int loai
    );

    @POST("dangki.php")
    @FormUrlEncoded
    Observable<UserModel> dangKi(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("username") String username,
            @Field("mobile") String mobile,
            @Field("uid") String uid
    );

    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangNhap(
            @Field("email") String email,
            @Field("pass") String pass
    );
    @POST("reset.php")
    @FormUrlEncoded
    Observable<UserModel> resetPass(
            @Field("email") String email
    );
    @POST("donhang.php")
    @FormUrlEncoded
    Observable<UserModel> createOrder(
            @Field("email") String email,
            @Field("sdt") String sdt,
            @Field("tongtien") String tongtien,
            @Field("iduser") int id,
            @Field("diachi") String diachi,
            @Field("soluong") int soluong,
            @Field("chitiet") String chitiet
    );
    @POST("xemdonhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> xemDonHang(
            @Field("iduser") int id
    );
    @POST("timkiem.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> search(
            @Field("search") String search
    );
    @POST("xoa.php")
    @FormUrlEncoded
    Observable<MessageModel> xoaSanPham(
            @Field("id") int id
    );
    @POST("insert.php")
    @FormUrlEncoded
    Observable<MessageModel> insertSp(
            @Field("tensp") String tensp,
            @Field("gia") String gia,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") int id
    );
    @POST("update.php")
    @FormUrlEncoded
    Observable<MessageModel> updateSp(
            @Field("tensp") String tensp,
            @Field("gia") String gia,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") int idloai,
            @Field("id") int id
    );
    @POST("updatetoken.php")
    @FormUrlEncoded
    Observable<MessageModel> updateToken(
            @Field("id") String id,
            @Field("token") String token

    );


    @GET("get_user.php")
    Single<User> getUserById(@Query("id") int id);
    @Multipart
    @POST("update_user.php")
    Single<UpdateUserResponse> updateUser(
            @Part("id") RequestBody id,
            @Part("name")      RequestBody name,
            @Part("mobile")    RequestBody mobile,
            @Part("gender")    RequestBody gender,
            @Part("birthdate") RequestBody birthdate,
            @Part("avatar_url")RequestBody avatarUrl
    );

    @POST("update_password.php")
    @FormUrlEncoded
    Observable<MessageModel> updatePassword(
            @Field("id") int id,
            @Field("new_password") String newPassword
    );

}
