package com.example.buzzchat3;

import android.text.TextUtils;
import android.webkit.URLUtil;
import androidx.annotation.NonNull;

public class OpenTokConfig {
    /*
    Fill the following variables using your own Project info from the OpenTok dashboard
    https://dashboard.tokbox.com/projects
    Note that this application will ignore credentials in the `OpenTokConfig` file when `CHAT_SERVER_URL` contains a
    valid URL.
    */

    // Replace with a API key
    public static final String API_KEY = "47276134";

    // Replace with a generated Session ID
    public static final String SESSION_ID = "1_MX40NzI3NjEzNH5-MTYyNjAyNzE2NTUwN344R1VLYWpObjU0Q2tScCtwSjRxTEZpRUd-fg";

    // Replace with a generated token (from the dashboard or using an OpenTok server SDK)
    public static final String TOKEN = "T1==cGFydG5lcl9pZD00NzI3NjEzNCZzaWc9NzZhMzg3ODk2NDUwMTNiNDE3Y2QyNTM4ZTdjMDJhN2MwNDJjZTJkMzpzZXNzaW9uX2lkPTFfTVg0ME56STNOakV6Tkg1LU1UWXlOakF5TnpFMk5UVXdOMzQ0UjFWTFlXcE9ialUwUTJ0U2NDdHdTalJ4VEVacFJVZC1mZyZjcmVhdGVfdGltZT0xNjI2MDI4MTg2Jm5vbmNlPTAuNDgxMTYwODA2ODk0ODE0NTUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTYyODYyMDE4MyZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ==";

    // *** The code below is to validate this configuration file. You do not need to modify it  ***

    public static boolean isValid() {
        if (TextUtils.isEmpty(OpenTokConfig.API_KEY)
                || TextUtils.isEmpty(OpenTokConfig.SESSION_ID)
                || TextUtils.isEmpty(OpenTokConfig.TOKEN)) {
            return false;
        }

        return true;
    }

    @NonNull
    public static String getDescription() {
        return "OpenTokConfig:" + "\n"
                + "API_KEY: " + OpenTokConfig.API_KEY + "\n"
                + "SESSION_ID: " + OpenTokConfig.SESSION_ID + "\n"
                + "TOKEN: " + OpenTokConfig.TOKEN + "\n";
    }
}
