package com.hafez.password_manager;

import android.app.Application;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class App extends Application {

    private static App appInstance;
    private Executor databaseExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        databaseExecutor = Executors.newSingleThreadExecutor();
    }

    public static App getInstance() {
        return appInstance;
    }

    public Executor getDatabaseExecutor() {
        return databaseExecutor;
    }
}
