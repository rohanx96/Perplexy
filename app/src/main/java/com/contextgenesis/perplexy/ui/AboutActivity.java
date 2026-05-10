package com.contextgenesis.perplexy.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.contextgenesis.perplexy.databinding.ActivityAboutBinding;
import com.contextgenesis.perplexy.utils.SoundManager;

public class AboutActivity extends Activity {

    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.aboutBack.setOnClickListener(v -> goBack());
        binding.aboutButterknife.setOnClickListener(v -> setButterknife());
        binding.aboutDialog.setOnClickListener(v -> setDialog());
        binding.aboutFreepik.setOnClickListener(v -> setFreepik());
        binding.aboutIcons.setOnClickListener(v -> setIcons8());
        binding.aboutSugar.setOnClickListener(v -> setSugar());
        binding.aboutSwitch.setOnClickListener(v -> setSwitchBtn());
        binding.devandroid1.setOnClickListener(v -> setAndDev1());
        binding.devgplus1.setOnClickListener(v -> setgplus1());
        binding.devandroid2.setOnClickListener(v -> setAndDev2());
        binding.devgplus2.setOnClickListener(v -> setgplus2());
        binding.devandroid3.setOnClickListener(v -> setAndDev3());
        binding.devgplus3.setOnClickListener(v -> setgplus3());
        binding.devandroid4.setOnClickListener(v -> setAndroid4());
        binding.devgplus4.setOnClickListener(v -> setgplus4());
    }

    public void goBack() {
        SoundManager.playButtonClickSound(getApplicationContext());
        onBackPressed();
    }

    private void openWeb(String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    public void setButterknife() {
        openWeb("https://developer.android.com/topic/libraries/view-binding");
    }

    public void setDialog() {
        openWeb("https://github.com/orhanobut/dialogplus");
    }

    public void setFreepik() {
        openWeb("http://freepik.com/");
    }

    public void setIcons8() {
        openWeb("https://icons8.com/");
    }

    public void setSugar() {
        openWeb("https://developer.android.com/training/data-storage/room");
    }

    public void setSwitchBtn() {
        openWeb("https://github.com/kyleduo/SwitchButton");
    }

    public void setAndDev1() {
        openWeb("https://play.google.com/store/apps/details?id=com.Dhruv.MemoryMaze");
    }

    public void setgplus1() {
        openWeb("https://plus.google.com/u/0/115993146711231228746/about");
    }

    public void setAndDev2() {
        openWeb("https://play.google.com/store/apps/details?id=rish.crearo.lifehacks");
    }

    public void setgplus2() {
        openWeb("https://plus.google.com/u/0/114245670419614057385");
    }

    public void setAndDev3() {
        openWeb("https://play.google.com/store/apps/details?id=com.rose.quickwallet");
    }

    public void setgplus3() {
        openWeb("https://plus.google.com/u/0/117317494206385274738/about");
    }

    public void setAndroid4() {
        openWeb("https://play.google.com/store/apps/details?id=com.contextgenesis.perplexy");
    }

    public void setgplus4() {
        openWeb("https://plus.google.com/112784156774948582788/about");
    }
}
