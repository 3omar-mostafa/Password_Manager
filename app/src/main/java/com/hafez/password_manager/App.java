package com.hafez.password_manager;

import android.app.Application;

public class App extends Application {

    private static App appInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
    }

    public static App getInstance() {
        return appInstance;
    }

}
