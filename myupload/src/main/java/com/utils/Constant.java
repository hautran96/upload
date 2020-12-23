package com.utils;

import okhttp3.MediaType;

public class Constant {
    public static final String TAG = "UPLOAD LIBRARY";
    public static final String DOMAIN_ISTORAGE = "http://istorage.fpt.net";
    public static final String DOMAIN_UPLOAD = DOMAIN_ISTORAGE + "/upload";
    public static final String HOST = "istorage.fpt.net";

    public static final String API_RESPONSE_RESULT = "ResponseResult";
    public static final String API_ERROR_CODE = "ErrorCode";
    public static final String API_MESSAGE = "Message";
    public static final String API_RESULTS = "Results";

    public static final String API_RESULTS_ERROR = "Error";
    public static final String FILE_KEY = "FileKey";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final String API_KEY_GET_TOKEN = "api_key";
    public static final String ACCOUNT = "account";
    public static final String IP_CLIENT = "ip_client";
    public static final String TOKEN = "Token";
    public static final String QUERY_FILE_KEY = "filekey";
    public static final String API_AUTHORIZATION = "Authorization";
    public static final String LINK_DOWNLOAD = "link_download";
}
