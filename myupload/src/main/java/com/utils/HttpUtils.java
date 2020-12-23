package com.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.listener.AsyncTaskCompleteListener;
import com.myupload.R;
import com.service.PingHostTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {
    private static final OkHttpClient mClient = HttpUtils.getUnsafeOkHttpClient();


    /**
     * @param uri is uri of url use for call api
     * @return url use for call api
     */
    public static String makeUrl(String domain, String uri) {
        return domain + uri;
    }

    public static void returnData(final GetDataCompleted callback, final int code, final String message, final Object data) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                callback.onCompleted(code, message, data);
            }
        });
    }

    public interface GetDataCompleted {
        void onCompleted(int code, String msg, Object data);
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void requestGETMethod(final Context context, String schema, String host, int port, String pathSegment, HashMap<String, String> queryParameter, String apiKey,
                                        final HttpUtils.GetDataCompleted getDataCompleted) {

        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme(schema);
        builder.host(host);
        if (port > 0) {
            builder.port(port);
        }

        builder.addPathSegments(pathSegment);

        if (queryParameter != null) {
            for (Map.Entry<String, String> entry : queryParameter.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                builder.addQueryParameter(key, value);
            }
        }

        HttpUrl url = builder.build();

        Log.i(Constant.TAG, "Url GET method: " + url);

        Request request = new Request.Builder()
                .addHeader(Constant.API_AUTHORIZATION, "Bearer " + apiKey)
                .url(url)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (Common.isNetworkAvailable(context)) {
                    final int errorCode = e.hashCode();
                    PingHostTask pingHostTask = new PingHostTask(context, new AsyncTaskCompleteListener<Boolean>() {
                        @Override
                        public void onTaskComplete(Boolean result) {
                            String message;
                            if (result) {
                                message = context.getString(R.string.msg_network_warning);
                            } else {
                                message = context.getString(R.string.message_problem_connect_to_server);
                            }

                            HttpUtils.returnData(getDataCompleted, errorCode, message, null);
                        }
                    });
                    pingHostTask.execute();
                } else {
                    HttpUtils.returnData(getDataCompleted, e.hashCode(), context.getString(R.string.message_no_internet_connection), null);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = 1;
                String msg = "Error";
                JSONObject result = new JSONObject();
                try {
                    String jsonResultString = response.body().string();
                    Log.i(Constant.TAG, "result json: " + jsonResultString);
                    JSONObject jsonObjectData = new JSONObject(jsonResultString);

                    result = jsonObjectData.getJSONObject(Constant.API_RESPONSE_RESULT);
                    code = result.getInt(Constant.API_ERROR_CODE);
                    msg = result.getString(Constant.API_MESSAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtils.returnData(getDataCompleted, code, msg, result);
            }
        });
    }

    public static void requestPOSTMethod(final Context context, String schema, String host, int port, String pathSegment, String bodyJson,
                                         final HttpUtils.GetDataCompleted getDataCompleted) {

        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme(schema);
        builder.host(host);
        if (port > 0) {
            builder.port(port);
        }
        builder.addPathSegments(pathSegment);

        HttpUrl url = builder.build();

        Log.i(Constant.TAG, "Url POST method: " + url);
        Log.i(Constant.TAG, "json post" + bodyJson);

        RequestBody body = RequestBody.create(Constant.JSON, bodyJson);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (Common.isNetworkAvailable(context)) {
                    final int errorCode = e.hashCode();
                    PingHostTask pingHostTask = new PingHostTask(context, new AsyncTaskCompleteListener<Boolean>() {
                        @Override
                        public void onTaskComplete(Boolean result) {
                            String message;
                            if (result) {
                                message = context.getString(R.string.msg_network_warning);
                            } else {
                                message = context.getString(R.string.message_problem_connect_to_server);
                            }

                            HttpUtils.returnData(getDataCompleted, errorCode, message, null);
                        }
                    });
                    pingHostTask.execute();
                } else {
                    HttpUtils.returnData(getDataCompleted, e.hashCode(), context.getString(R.string.message_no_internet_connection), null);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = 1;
                String msg = "Error";
                JSONObject result = new JSONObject();
                try {
                    String jsonResultString = response.body().string();

                    Log.i(Constant.TAG, "result json: " + jsonResultString);
                    JSONObject jsonObjectData = new JSONObject(jsonResultString);

                    result = jsonObjectData.getJSONObject(Constant.API_RESPONSE_RESULT);
                    code = result.getInt(Constant.API_ERROR_CODE);
                    msg = result.getString(Constant.API_MESSAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtils.returnData(getDataCompleted, code, msg, result);
            }
        });

    }
}
