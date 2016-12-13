package com.lizhangqu.sample;

import android.app.Application;
import android.content.Context;
import android.util.Log;



public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.e("TAG", "real attachBaseContext");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", "real onCreate");

    }
}
