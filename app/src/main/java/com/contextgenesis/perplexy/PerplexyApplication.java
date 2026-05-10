package com.contextgenesis.perplexy;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.orm.SugarApp;

public class PerplexyApplication extends SugarApp {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }
}
