package com.example.lkm.ms_termproject_001;

import android.app.Activity;
import android.app.Application;

import com.kakao.auth.KakaoSDK;

/**
 * Created by sonjaehyeong on 2017. 11. 20..
 */

public class GlobalApplication extends Application {

    private static volatile GlobalApplication obj = null;
    private static volatile Activity currentActivity = null;
    String uuid;

    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static GlobalApplication getGlobalApplicationContext() {
        return obj;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    // Activity가 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
    public static void setCurrentActivity(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }
}