package com.myapp.quiz.constants;

import java.util.Arrays;
import java.util.List;

public class Constants {

    /* Constants Swagger Start */
    public static final String API_TITLE = "${open.api.title}";
    public static final String API_VERSION = "${open.api.version}";
    public static final String API_DESCRIPTION = "${open.api.description}";
    public static final String API_SERVER_URL = "${open.api.serverUrl}";
    public static final String API_SERVER_NAME = "${open.api.serverName}";
    public static final String API_AUTHORIZATION = "Authorization";
    /* Constants Swagger End */

    /* Constants Secret Token Start */
    public static final String API_JWT_SECRET = "${app.jwt.secret}";
    public static final String API_JWT_EXPIRATION_MILLISECONDS = "${app.jwt.expiration-milliseconds}";
    public static final String API_JWT_REFRESH_EXPIRATION_MILLISECONDS = "${app.jwt.refresh.expiration-milliseconds}";
    /* Constants Secret Token End */

    /* Constants Response Token Start */
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_TOKEN = "Bearer";
    /* Constants Response Token End */

    /* Constants Cors Start */
    public static final List<String> ALLOWED_HTTP_METHODS = Arrays.asList("GET", "POST", "PUT", "DELETE");
    public static final String ALLOWED_URI = "http://localhost:8080";
    public static final String ALLOWED_HEADER = "*";
    /* Constants Cors End */

    public static final String SPACE_HALF_SIZE = " ";

    public static final String REQUEST_ACCESS_TOKEN = "accessToken";
    public static final String REQUEST_REFRESH_TOKEN = "refreshToken";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String REFRESH_TOKEN_BLANK = "";
}
