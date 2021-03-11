package com.naman14.timber.ytmusicapi;

import android.util.Log;

import okhttp3.*;


public class RequestJSON {


    public final String baseURL;
    public final String defaultKey;


    public RequestJSON() {

        this.baseURL = "https://music.youtube.com/youtubei/v1/";
        this.defaultKey = "?alt=json&key=AIzaSyC9XL3ZjWddXya6X74dJoCTL-WEYFDNX30";
    }

    public String getSearchSuggestions(String input) {
        Response response = null;
        String responseString = "";

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "{\"context\":{\"client\":{\"clientName\":\"WEB_REMIX\",\"clientVersion\":\"0.1\",\"hl\":\"de\",\"gl\":\"CH\",\"experimentIds\":[],\"experimentsToken\":\"\",\"browserName\":\"Chrome\",\"browserVersion\":\"88.0.4324.104\",\"osName\":\"Windows\",\"osVersion\":\"10.0\",\"platform\":\"DESKTOP\",\"utcOffsetMinutes\":60,\"locationInfo\":{\"locationPermissionAuthorizationStatus\":\"LOCATION_PERMISSION_AUTHORIZATION_STATUS_UNSUPPORTED\"},\"musicAppInfo\":{\"musicActivityMasterSwitch\":\"MUSIC_ACTIVITY_MASTER_SWITCH_INDETERMINATE\",\"musicLocationMasterSwitch\":\"MUSIC_LOCATION_MASTER_SWITCH_INDETERMINATE\",\"pwaInstallabilityStatus\":\"PWA_INSTALLABILITY_STATUS_CAN_BE_INSTALLED\"}},\"capabilities\":{},\"request\":{\"internalExperimentFlags\":[]},\"activePlayers\":{},\"user\":{\"enableSafetyMode\":false}},\"input\":\"" + input + "\"}");
        Request request = new Request.Builder()
                .url(baseURL + "music/get_search_suggestions" + defaultKey)
                .method("POST", body)
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.104 Safari/537.36")
                .addHeader("origin", "https://music.youtube.com")
                .addHeader("referer", "https://music.youtube.com/")
                .addHeader("accept-language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
                .addHeader("Content-Type", "text/plain")
                .build();

        try {
            response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (Exception e) {
            Log.e("Error : " , e.toString());
        } finally {
            return responseString;
        }
    }

    public String getSearchResult(String input) {
        Response response = null;
        String responseString = "";

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"context\":{\"client\":{\"clientName\":\"WEB_REMIX\",\"clientVersion\":\"0.1\",\"hl\":\"de\",\"gl\":\"CH\",\"experimentIds\":[],\"experimentsToken\":\"\",\"browserName\":\"Chrome\",\"browserVersion\":\"88.0.4324.146\",\"osName\":\"Windows\",\"osVersion\":\"10.0\",\"platform\":\"DESKTOP\",\"utcOffsetMinutes\":60,\"locationInfo\":{\"locationPermissionAuthorizationStatus\":\"LOCATION_PERMISSION_AUTHORIZATION_STATUS_UNSUPPORTED\"},\"musicAppInfo\":{\"musicActivityMasterSwitch\":\"MUSIC_ACTIVITY_MASTER_SWITCH_INDETERMINATE\",\"musicLocationMasterSwitch\":\"MUSIC_LOCATION_MASTER_SWITCH_INDETERMINATE\",\"pwaInstallabilityStatus\":\"PWA_INSTALLABILITY_STATUS_CAN_BE_INSTALLED\"}},\"capabilities\":{},\"request\":{\"internalExperimentFlags\":[]},\"activePlayers\":{},\"user\":{\"enableSafetyMode\":false}},\"query\":\"" + input + "\",\"suggestStats\":{\"validationStatus\":\"VALID\",\"parameterValidationStatus\":\"VALID_PARAMETERS\",\"clientName\":\"youtube-music\",\"searchMethod\":\"ENTER_KEY\",\"inputMethod\":\"KEYBOARD\",\"originalQuery\":\"" + input + "\",\"availableSuggestions\":[{\"index\":0,\"type\":0},{\"index\":1,\"type\":0},{\"index\":2,\"type\":0},{\"index\":3,\"type\":0},{\"index\":4,\"type\":0},{\"index\":5,\"type\":0},{\"index\":6,\"type\":0}],\"zeroPrefixEnabled\":true,\"firstEditTimeMsec\":0,\"lastEditTimeMsec\":0}}");
        Request request = new Request.Builder()
                .url(baseURL + "search" + defaultKey)
                .method("POST", body)
                .addHeader("authority", "music.youtube.com")
                .addHeader("pragma", "no-cache")
                .addHeader("cache-control", "no-cache")
                .addHeader("sec-ch-ua", "\"Chromium\";v=\"88\", \"Google Chrome\";v=\"88\", \";Not A Brand\";v=\"99\"")
                .addHeader("x-youtube-device", "cbr=Chrome&cbrver=88.0.4324.146&ceng=WebKit&cengver=537.36&cos=Windows&cosver=10.0&cplatform=DESKTOP")
                .addHeader("x-youtube-page-label", "youtube.music.web.client_20210201_00_RC01")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.146 Safari/537.36")
                .addHeader("content-type", "application/json")
                .addHeader("accept", "*/*")
                .addHeader("origin", "https://music.youtube.com")
                .addHeader("sec-fetch-site", "same-origin")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("referer", "https://music.youtube.com/")
                .addHeader("accept-language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
                //.addHeader("Cookie", "VISITOR_INFO1_LIVE=3FZRDdGzQY8; PREF=f6=400&volume=25&tz=Europe.Zurich&al=de&f4=4000000&f5=20030; CONSENT=PENDING+147")
                .build();
        try {
            response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (Exception e) {
            Log.e("Error : " , e.toString());
        }
        return responseString;
    }

    public String getMoreSearchResult(String query, String param) {
        Response response = null;
        String responseString = "";


        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"context\":{\"client\":{\"clientName\":\"WEB_REMIX\",\"clientVersion\":\"0.1\",\"hl\":\"de\",\"gl\":\"CH\",\"experimentIds\":[],\"experimentsToken\":\"\",\"browserName\":\"Chrome\",\"browserVersion\":\"88.0.4324.146\",\"osName\":\"Windows\",\"osVersion\":\"10.0\",\"platform\":\"DESKTOP\",\"utcOffsetMinutes\":60,\"locationInfo\":{\"locationPermissionAuthorizationStatus\":\"LOCATION_PERMISSION_AUTHORIZATION_STATUS_UNSUPPORTED\"},\"musicAppInfo\":{\"musicActivityMasterSwitch\":\"MUSIC_ACTIVITY_MASTER_SWITCH_INDETERMINATE\",\"musicLocationMasterSwitch\":\"MUSIC_LOCATION_MASTER_SWITCH_INDETERMINATE\",\"pwaInstallabilityStatus\":\"PWA_INSTALLABILITY_STATUS_CAN_BE_INSTALLED\"}},\"capabilities\":{},\"request\":{\"internalExperimentFlags\":[]},\"clickTracking\":{\"clickTrackingParams\":\"\"},\"activePlayers\":{},\"user\":{\"enableSafetyMode\":false}},\"query\":\"" + query + "\",\"params\":\"" + param + "\"}");
        Request request = new Request.Builder()
                .url("https://music.youtube.com/youtubei/v1/search?alt=json&key=AIzaSyC9XL3ZjWddXya6X74dJoCTL-WEYFDNX30")
                .method("POST", body)
                .addHeader("authority", "music.youtube.com")
                .addHeader("pragma", "no-cache")
                .addHeader("cache-control", "no-cache")
                .addHeader("sec-ch-ua", "\"Chromium\";v=\"88\", \"Google Chrome\";v=\"88\", \";Not A Brand\";v=\"99\"")
                .addHeader("x-youtube-device", "cbr=Chrome&cbrver=88.0.4324.146&ceng=WebKit&cengver=537.36&cos=Windows&cosver=10.0&cplatform=DESKTOP")
                .addHeader("x-youtube-page-label", "youtube.music.web.client_20210201_00_RC01")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.146 Safari/537.36")
                .addHeader("content-type", "application/json")
                .addHeader("accept", "*/*")
                .addHeader("referer", "https://music.youtube.com/search?q=oh+lale")
                .addHeader("accept-language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
                .build();
        try {
            response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (Exception e) {
            Log.e("Error : " , e.toString());
        }
        return responseString;
    }

    public String getPlaylist(String videoId, String playlistId){
        Response response = null;
        String responseString = "";

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"context\":{\"client\":{\"clientName\":\"WEB_REMIX\",\"clientVersion\":\"0.1\",\"hl\":\"de\",\"gl\":\"CH\",\"experimentIds\":[],\"experimentsToken\":\"\",\"browserName\":\"Chrome\",\"browserVersion\":\"88.0.4324.150\",\"osName\":\"Windows\",\"osVersion\":\"10.0\",\"platform\":\"DESKTOP\",\"utcOffsetMinutes\":60,\"locationInfo\":{\"locationPermissionAuthorizationStatus\":\"LOCATION_PERMISSION_AUTHORIZATION_STATUS_UNSUPPORTED\"},\"musicAppInfo\":{\"musicActivityMasterSwitch\":\"MUSIC_ACTIVITY_MASTER_SWITCH_INDETERMINATE\",\"musicLocationMasterSwitch\":\"MUSIC_LOCATION_MASTER_SWITCH_INDETERMINATE\",\"pwaInstallabilityStatus\":\"PWA_INSTALLABILITY_STATUS_CAN_BE_INSTALLED\"}},\"capabilities\":{},\"request\":{\"internalExperimentFlags\":[]},\"clickTracking\":{\"clickTrackingParams\":\"\"},\"activePlayers\":{},\"user\":{\"enableSafetyMode\":false}},\"enablePersistentPlaylistPanel\":true,\"tunerSettingValue\":\"AUTOMIX_SETTING_NORMAL\",\"videoId\":\"" + videoId + "\",\"playlistId\":\"" + playlistId + "\",\"params\":\"wAEB\",\"watchEndpointMusicSupportedConfigs\":{\"watchEndpointMusicConfig\":{\"musicVideoType\":\"MUSIC_VIDEO_TYPE_ATV\"}},\"isAudioOnly\":true}");
        Request request = new Request.Builder()
                .url("https://music.youtube.com/youtubei/v1/next?alt=json&key=AIzaSyC9XL3ZjWddXya6X74dJoCTL-WEYFDNX30")
                .method("POST", body)
                .addHeader("authority", "music.youtube.com")
                .addHeader("sec-ch-ua", "\"Chromium\";v=\"88\", \"Google Chrome\";v=\"88\", \";Not\\A\"Brand\";v=\"99\"")
                .addHeader("x-youtube-device", "cbr=Chrome&cbrver=88.0.4324.150&ceng=WebKit&cengver=537.36&cos=Windows&cosver=10.0&cplatform=DESKTOP")
                .addHeader("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Mobile Safari/537.36")
                .addHeader("content-type", "application/json")
                .addHeader("sec-ch-ua-mobile", "?1")
                .addHeader("x-youtube-time-zone", "Europe/Zurich")
                .addHeader("x-youtube-ad-signals", "dt=1612995403182&flash=0&frm&u_tz=60&u_his=10&u_java&u_h=882&u_w=1307&u_ah=882&u_aw=1307&u_cd=24&u_nplug&u_nmime&bc=31&bih=882&biw=1307&brdim=0%2C0%2C0%2C0%2C1307%2C0%2C1307%2C882%2C1307%2C882&vis=1&wgl=true&ca_type=image&bid=ANyPxKpKQxZpMUDLOoQ8IHKRLgvnQtrxituF-b2yo1SrillstZBLUBIE0is-Bc3TZRKXg9eBa1lL06A2VbGfk2EZsEHNHD1B8Q")
                .addHeader("accept", "*/*")
                .addHeader("origin", "https://music.youtube.com")
                .addHeader("referer", "https://music.youtube.com/watch?v=KapW2Q-T1jQ&list=RDAMVMKapW2Q-T1jQ")
                .addHeader("accept-language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
                .addHeader("Cookie", "VISITOR_INFO1_LIVE=3FZRDdGzQY8; PREF=f6=400&volume=25&tz=Europe.Zurich&al=de&f4=4000000&f5=20030; CONSENT=PENDING+147")
                .build();
        try {
            response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (Exception e) {
            Log.e("Error : " , e.toString());
        }
        return responseString;
    }

}
