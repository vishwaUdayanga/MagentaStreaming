package com.example.magentastreaming.Activities;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MusicService extends Service {
    private IBinder mBinder = new MyBinder();
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREV = "PREVIOUS";
    public static final String ACTION_PLAY = "PLAY";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionName = intent.getStringExtra("myAction");
        if (actionName != null) {
            switch (actionName) {
                case ACTION_PLAY:
                    break;

                case ACTION_NEXT:
                    break;

                case ACTION_PREV:
                    break;
            }
        }
        return START_STICKY;
    }
}
