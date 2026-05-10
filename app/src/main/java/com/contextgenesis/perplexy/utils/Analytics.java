package com.contextgenesis.perplexy.utils;

import android.app.Activity;
import android.os.Bundle;

import com.contextgenesis.perplexy.PerplexyApplication;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {
    public static final String CATEGORY_UI = "UI";
    public static final String CATEGORY_ADS = "Ad";
    public static final String CATEGORY_COINS = "Coins";
    public static final String CATEGORY_QUESTION = "Questions";
    public static final String ACTION_NO_COINS = "Not enough Coins";
    public static final String ACTION_SHOW_HINT = "Show Hint";
    public static final String ACTION_SHOW_SOLUTION = "Show solution";
    public static final String ACTION_UNLOCK_QUESTION = "Unlock Question";
    public static final String ACTION_WATCH_AD = "Click on Watch Ad";
    public static final String ACTION_PLAY_CATEGORY = "Play category";

    private static FirebaseAnalytics getTracker(Activity activity) {
        return ((PerplexyApplication) activity.getApplication()).getFirebaseAnalytics();
    }

    public static void sendWatchAd(Activity activity, long coins) {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, CATEGORY_ADS);
        params.putLong("coins", coins);
        getTracker(activity).logEvent(ACTION_WATCH_AD, params);
    }

    public static void sendShowHint(Activity activity, int category, int questionNo) {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, CATEGORY_QUESTION);
        params.putInt("category", category);
        params.putInt("question_no", questionNo);
        getTracker(activity).logEvent("show_hint", params);
    }

    public static void sendShowSolution(Activity activity, int category, int questionNo) {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, CATEGORY_QUESTION);
        params.putInt("category", category);
        params.putInt("question_no", questionNo);
        getTracker(activity).logEvent("show_solution", params);
    }

    public static void sendUnlockQuestion(Activity activity, int category, int questionNo) {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, CATEGORY_QUESTION);
        params.putInt("category", category);
        params.putInt("question_no", questionNo);
        getTracker(activity).logEvent("unlock_question", params);
    }

    public static void sendNoCoins(Activity activity, long coins) {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, CATEGORY_COINS);
        params.putLong("coins", coins);
        getTracker(activity).logEvent("no_coins", params);
    }
}
