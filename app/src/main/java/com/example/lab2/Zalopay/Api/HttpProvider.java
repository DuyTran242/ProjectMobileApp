package com.example.lab2.Zalopay.Api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpProvider {

    public JSONObject sendPost(String url, Map<String, Object> params) throws Exception {
        Log.d("HttpProvider", "Sending POST to: " + url);
        Log.d("HttpProvider", "Params: " + params.toString());

        // Tạo OkHttpClient với timeout cao hơn
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)  // Tăng từ 30s lên 60s
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        // Tạo FormBody
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue().toString());
        }

        Request request = new Request.Builder()
                .url(url)
                .post(formBuilder.build())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Log.d("HttpProvider", "Response code: " + response.code());
            Log.d("HttpProvider", "Response body: " + responseBody);

            if (response.isSuccessful()) {
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    try {
                        return new JSONObject(responseBody);
                    } catch (JSONException e) {
                        Log.e("HttpProvider", "Invalid JSON response: " + responseBody);
                        // Trả về response mặc định
                        JSONObject fallback = new JSONObject();
                        fallback.put("return_code", "0");
                        fallback.put("return_message", "Invalid JSON response");
                        return fallback;
                    }
                } else {
                    Log.e("HttpProvider", "Empty response body");
                    JSONObject fallback = new JSONObject();
                    fallback.put("return_code", "0");
                    fallback.put("return_message", "Empty response");
                    return fallback;
                }
            } else {
                Log.e("HttpProvider", "HTTP Error: " + response.code() + " - " + response.message());
                JSONObject error = new JSONObject();
                error.put("return_code", "0");
                error.put("return_message", "HTTP Error: " + response.code());
                return error;
            }
        } catch (IOException e) {
            Log.e("HttpProvider", "Network error: " + e.getMessage(), e);
            JSONObject error = new JSONObject();
            error.put("return_code", "0");
            error.put("return_message", "Network error: " + e.getMessage());
            return error;
        }
    }
}