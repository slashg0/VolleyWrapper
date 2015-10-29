package com.slashg.myapplication;

import android.app.Application;

import com.slashg.volleywrapper.Spine;

/**
 * Created by SlashG on 28-10-2015.
 */
public class ApplicationExt extends Application{


    @Override
    public void onCreate() {
        super.onCreate();
        Spine.initialize(getApplicationContext());
    }
}
