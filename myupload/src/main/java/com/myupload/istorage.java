package com.myupload;

import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.listener.AsyncTaskCompleteListener;
import com.service.PingHostTask;
import com.utils.Common;
import com.utils.Constant;
import com.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class istorage {
    Context context;
    private String mPath;
    private String mApikey = "1207d55d8a880040c5ee587b8fef09487043150e";
    private String mFileKey;
    private static final String SCHEMA = "http";
    private static final String HOST_ISTORAGE = Constant.HOST;
    private static final String PATH_GET_TOKEN = "api/partner/getAccessToken";
    private static final String PATH_GET_LINK = "api/partner/getFileByKey";
    private static final int PORT = 0;
    private String mLinkRespone;
    public final int TIME_OUT_UPLOAD = 720 * 1000; // 12 minutes

    public istorage(Context context){
        this.context = context;
    }

    public istorage setLinkFile(String mPath){
        this.mPath = mPath;
        return this;
    }

    public istorage setToken(String token){
        this.mApikey = token;
        return this;
    }

    public istorage setFileKey(String fileKey){
        this.mFileKey = fileKey;
        return this;
    }

    public void upload(){
        if(mPath != null){
            AsyncUpload mAsyncUpload = new AsyncUpload(mPath, mApikey, null);
            mAsyncUpload.execute();
            Log.i(Constant.TAG, "upload mPath "+ mPath + "upload mApikey " + mApikey);
        } else  {
            Toast.makeText(context, "upload " + mPath, Toast.LENGTH_LONG).show();
        }
    }


    public String getLink(){
        if(mFileKey != null){
            Log.i(Constant.TAG, "onPostExecute mPath "+ mPath + "onPostExecute mApikey " + mApikey + " file_key " + mFileKey);
            HashMap<String, String> mapQueryParameters = new HashMap<>();
            mapQueryParameters.put(Constant.QUERY_FILE_KEY, mFileKey);
            HttpUtils.requestGETMethod(context, SCHEMA, HOST_ISTORAGE, PORT, PATH_GET_LINK, mapQueryParameters, mApikey , new HttpUtils.GetDataCompleted() {
                @Override
                public void onCompleted(int code, String msg, Object data) {
                    handleGetLinkByKey(code, data);
                    if(data != null){
                        try {
                            JSONObject dataResult = ((JSONObject) data).getJSONObject(Constant.API_RESULTS);
                            if(code == 0){
                                mLinkRespone = dataResult.getString(Constant.LINK_DOWNLOAD);
                                Log.i(Constant.TAG, "handleGetLinkByKey " + mLinkRespone);
                            }else {
                                Toast.makeText(context, "lỗi get link image", Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }else {
            Toast.makeText(context, "Chưa có file key " + mFileKey , Toast.LENGTH_LONG).show();
        }

        return mLinkRespone;
    }

    private String getKeyAfterUpload(String jsonData) {
        JSONObject result;
        JSONObject results;
        String file_key = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            Log.i(Constant.TAG, "jsonObject" + jsonObject);
            result = jsonObject.getJSONObject(Constant.API_RESPONSE_RESULT);
            results = result.getJSONObject(Constant.API_RESULTS);
            file_key = results.getString(Constant.FILE_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return file_key; 
    }


    class AsyncUpload extends AsyncTask<Void, Integer, Void>{
        private final String mToken;
        private final String mImgpath;
        private Context context;
        private String mKey;

        AsyncUpload(String imPath, String token, String mFile){
            this.mImgpath = imPath;
            this.mToken = token;
            this.mKey = mFile;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(Constant.TAG, "onPreExecute mPath "+ mPath + "onPreExecute mApikey " + mApikey);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i(Constant.TAG, "doInBackground mPath "+ mPath + "doInBackground mApikey " + mApikey);
            boolean isConnectSuccess = false;
            do {
                if(isCancelled()) {
                    break;
                }
                if(Common.isNetworkAvailable(context)){
                    HttpURLConnection urlConnection;
                    try {
                        urlConnection = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                        urlConnection.setRequestProperty("User-Agent", "Test");
                        urlConnection.setRequestProperty("Connection", "close");
                        urlConnection.setConnectTimeout(3000);
                        urlConnection.connect();
                        if(urlConnection.getResponseCode() == 200) {
                            String jsonResultUpload;
                            String fileKey = "";
                            jsonResultUpload = sendFileToServer(mImgpath, mToken);
                            if (Common.isJSONValid(jsonResultUpload)) {
                                fileKey = getKeyAfterUpload(jsonResultUpload);
                            }else  {
                                fileKey = "";
                            }
                            mKey = fileKey;
                            isConnectSuccess = true;
                        } else {
                            isConnectSuccess = false;
                        }
                    }catch (IOException e){
                        Log.e(Constant.TAG, "" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } while (!isConnectSuccess);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private void getTokenUpload(Context context){
        JSONObject json = new JSONObject();
        try {
            json.put(Constant.API_KEY_GET_TOKEN, "9e92c421b3584d89823bdf1a2e70b1c439709f11");
            json.put(Constant.ACCOUNT, "tester");
            json.put(Constant.IP_CLIENT, "192.168.1.1");
        }catch (JSONException e){
            e.printStackTrace();
        }
        HttpUtils.requestPOSTMethod(context, SCHEMA, HOST_ISTORAGE, PORT, PATH_GET_TOKEN, json.toString(), new HttpUtils.GetDataCompleted() {
            @Override
            public void onCompleted(int code, String msg, Object data) {
                handleGetTokenResult(code, data);
            }
        });
    }

    private void handleGetTokenResult(int errorCode, Object json){
        if(json != null){
            try {
                JSONObject dataResult = ((JSONObject) json).getJSONObject(Constant.API_RESULTS);
                if(errorCode == 0){
                    mApikey = dataResult.getString(Constant.TOKEN);
                    AsyncUpload mAsyncUpload = new AsyncUpload(mPath, mApikey, null);
                    mAsyncUpload.execute();
                }else {
                    Toast.makeText(context, "Lỗi get token", Toast.LENGTH_LONG).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }else {
            Toast.makeText(context, "Lỗi get token", Toast.LENGTH_LONG).show();
        }
    }

    private void handleGetLinkByKey(int errorCode, Object json){
        if(json != null){
            try {
                JSONObject dataResult = ((JSONObject) json).getJSONObject(Constant.API_RESULTS);
                if(errorCode == 0){
                    mLinkRespone = dataResult.getString(Constant.LINK_DOWNLOAD);
                    Log.i(Constant.TAG, "handleGetLinkByKey " + mLinkRespone);
                }else {
                    Toast.makeText(context, "lỗi get link image", Toast.LENGTH_LONG).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private String sendFileToServer(String filename, String token) {
        String response = "error";
        Log.e("Image filename ", filename);
        Log.e("url ", Constant.DOMAIN_UPLOAD);
        HttpURLConnection connection = null;

        BufferedOutputStream out = null;
        InputStream fileStream = null;
        long total = 0;
        String checksum = "";

        File fileUpload = new File(filename);
        try {
            byte[] buf = new byte[1024 * 16];
            fileStream = new FileInputStream(filename);
            long lengthOfFile = fileUpload.length();
            long sizeOfBlock = 1024 * 1024; //
            int totalChunk = (int) ((lengthOfFile + (sizeOfBlock - 1)) / sizeOfBlock);
            String headerValue;
            byte[] data = filename.getBytes("UTF-8");
            String name = fileUpload.getName();
            String base64 = Base64.encodeToString(data, Base64.NO_WRAP).replaceAll("[\"\';\\-\\+\\.\\^:,?=!@#$%^&*()\\[\\]]", "");
            long contentLength;
            int i = 0;
            while (i < totalChunk) {
                long totalTmp = total;
                if(fileStream == null) {
                    fileStream = new FileInputStream(filename);
                }
                try {
                    long from = i * sizeOfBlock;
                    long to;
                    if ((from + sizeOfBlock) > lengthOfFile) {
                        to = lengthOfFile;
                    } else {
                        to = (sizeOfBlock * (i + 1));
                    }
                    to = to - 1;
                    contentLength = to - from + 1;
                    headerValue = "bytes " + from + "-" + to + "/" + lengthOfFile;

                    URL url = new URL(Constant.DOMAIN_UPLOAD);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(15 * 1000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Authorization","Bearer " + token);
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("file-type","image");
                    connection.setRequestProperty("file-name",name);
                    connection.setRequestProperty("Content-Disposition","attachment; filename=\"" + name + "\"");
                    connection.setRequestProperty("Session-ID", base64);
                    connection.setRequestProperty("Content-Range", headerValue);
                    connection.setRequestProperty("X-Chunk-Index", (i + 1) + "");
                    connection.setRequestProperty("X-Chunks-Number", totalChunk + "");
                    connection.setRequestProperty("user-upload", "userUpload");
                    connection.setRequestProperty("Content-Length", contentLength + "");
                    connection.setRequestProperty("Content-Type", "application/json");
                    if (i > 0) {
                        connection.setRequestProperty("X-Last-Checksum", checksum);
                    }

                    out = new BufferedOutputStream(connection.getOutputStream());

                    int read = 1;
                    while (read > 0 && total < sizeOfBlock * (i + 1)) {
                        read = fileStream.read(buf);
                        if (read > 0) {
                            total += read;
                            out.write(buf, 0, read);
                            out.flush();
                        }
                    }
                    int serverResponseCode = connection.getResponseCode();
                    if (serverResponseCode == HttpURLConnection.HTTP_OK
                            || serverResponseCode == HttpURLConnection.HTTP_CREATED) {
                        checksum = "";
                        i++;
                        Map<String, List<String>> map = connection.getHeaderFields();
                        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                            if (!(entry.getKey() + "").isEmpty() && (entry.getKey() + "")
                                    .equals("X-Checksum")) {
                                for (String item : entry.getValue()) {
                                    checksum = checksum + item;
                                }
                                break;
                            }
                        }
                    }

                    if (i < totalChunk - 1) {
                        out.close();
                        out = null;
                        connection.disconnect();
                    } else {
                        response = Common.getStringFromInputStream(connection.getInputStream());
                        out.close();
                        out = null;
                        connection.disconnect();
                    }
                } catch (Exception e) {
                    total = totalTmp;
                    if(connection != null) {
                        connection.disconnect();
                    }

                    if(out != null) {
                        out.close();
                        out = null;
                    }
                    fileStream.close();
                    fileStream = null;
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            response = "error";
            ex.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
            try {
                if(fileStream != null) {
                    fileStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }
}
