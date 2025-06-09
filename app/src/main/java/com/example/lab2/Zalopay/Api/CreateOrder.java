package com.example.lab2.Zalopay.Api;

import android.util.Log;

import com.example.lab2.Zalopay.Constant.AppInfo;
import com.example.lab2.Zalopay.Helper.Helpers;
import com.example.lab2.Zalopay.Helper.HMac.HMacUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateOrder {
    private static Map<String, Object> createOrderData(String amount) {
        Map<String, Object> order = new HashMap<>();

        // Tạo unique app_trans_id
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd_HHmmss", Locale.getDefault());
        String app_trans_id = formatter.format(new Date()) + "_" + System.currentTimeMillis();

        order.put("app_id", AppInfo.APP_ID);
        order.put("app_user", "user123");
        order.put("app_time", System.currentTimeMillis());
        order.put("amount", amount);
        order.put("app_trans_id", app_trans_id);
        order.put("embed_data", "{}");
        order.put("item", "[{\"itemid\":\"knb\",\"itemname\":\"kim nguyen bao\",\"itemprice\":198400,\"itemquantity\":1}]");
        order.put("bank_code", "zalopayapp");
        order.put("description", "Merchant pay for order " + app_trans_id);

        // Tạo MAC
        String data = order.get("app_id") + "|" + order.get("app_trans_id") + "|"
                + order.get("app_user") + "|" + order.get("amount") + "|"
                + order.get("app_time") + "|" + order.get("embed_data") + "|"
                + order.get("item");

        order.put("mac", HMacUtil.HMacHexStringEncode(HMacUtil.HMACSHA256, AppInfo.MAC_KEY, data));

        Log.d("CreateOrder", "Order data created: " + order.toString());
        return order;
    }

    public static JSONObject createOrder(String amount) throws Exception {
        Log.d("CreateOrder", "Creating order with amount: " + amount);

        try {
            Map<String, Object> orderData = createOrderData(amount);

            // THÊM: Tạo instance HttpProvider
            HttpProvider httpProvider = new HttpProvider();
            JSONObject result = httpProvider.sendPost(AppInfo.URL_CREATE_ORDER, orderData);

            Log.d("CreateOrder", "API Response: " + result.toString());

            // Kiểm tra response có hợp lệ không
            if (result.has("return_code")) {
                return result;
            } else {
                // Tạo response mặc định nếu không có return_code
                JSONObject fallbackResponse = new JSONObject();
                fallbackResponse.put("return_code", "0");
                fallbackResponse.put("return_message", "API response invalid");
                Log.e("CreateOrder", "No return_code in response, using fallback");
                return fallbackResponse;
            }

        } catch (Exception e) {
            Log.e("CreateOrder", "Exception creating order: " + e.getMessage(), e);

            // Trả về error response thay vì throw exception
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("return_code", "0");
            errorResponse.put("return_message", "Error: " + e.getMessage());
            return errorResponse;
        }
    }
}