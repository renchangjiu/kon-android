package com.htt.kon.util.requests;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author su
 * @date 2020/1/10 15:36
 */
public class Requests {
    private Requests() {
    }

    public static Call get(String url) {
        return get(url, null, null);
    }

    public static Call post(String url) {
        return post(url, null, null);
    }

    public static Call post4json(String url, String json) {
        return post4json(url, json, null);
    }

    public static Call get(String url, Map<String, String> params, Map<String, String> headers) {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();

        StringBuilder sb = new StringBuilder(url).append("?");
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        url = StringUtils.strip(sb.toString(), "&");
        builder.url(url);
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        builder.get();
        Request request = builder.build();
        return client.newCall(request);
    }

    public static Call post(String url, Map<String, String> params, Map<String, String> headers) {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        // 构造请求体
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                bodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody body = bodyBuilder.build();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.url(url).post(body).build();
        return client.newCall(request);
    }

    /**
     * 以 json 数据提交请求
     *
     * @param json json, not null
     */
    public static Call post4json(String url, String json, Map<String, String> headers) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse(MediaTypes.APPLICATION_JSON);
        // 构造请求体
        RequestBody requestBody = RequestBody.create(json, mediaType);
        Request.Builder builder = new Request.Builder();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.url(url).post(requestBody).build();
        return client.newCall(request);
    }


}
