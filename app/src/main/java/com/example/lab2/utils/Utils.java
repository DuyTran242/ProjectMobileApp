package com.example.lab2.utils;

import com.example.lab2.model.GioHang;
import com.example.lab2.model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static final String BASE_URL = "http://172.17.103.55/banhang/";

    public static List<GioHang> manggiohang;
    public static List<GioHang> mangmuahang = new ArrayList<>();
    public static User user_current = new User();

    // Hoặc thêm method để ensure initialization
    public static void initCart() {
        if (manggiohang == null) {
            manggiohang = new ArrayList<>();
        }
    }

}
