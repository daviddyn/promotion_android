package edu.neu.promotion;

import android.app.Application;

public class MyApplication extends Application {

    public boolean firstRun;

    @Override
    public void onCreate() {
        super.onCreate();
        firstRun = true;
    }
}
