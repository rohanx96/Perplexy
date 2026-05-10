package com.contextgenesis.perplexy;

import android.app.Application;
import com.contextgenesis.perplexy.elements.AppDatabase;
import com.google.firebase.analytics.FirebaseAnalytics;

public class PerplexyApplication extends Application {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AppDatabase.getInstance(this); // initializes AppDatabase.db
    }

    public FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }
}
