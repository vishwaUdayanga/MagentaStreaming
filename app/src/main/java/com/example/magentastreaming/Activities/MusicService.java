package com.example.magentastreaming.Activities;

import static com.example.magentastreaming.Activities.ApplicationClass.ACTION_NEXT;
import static com.example.magentastreaming.Activities.ApplicationClass.ACTION_PLAY;
import static com.example.magentastreaming.Activities.ApplicationClass.ACTION_PREV;
import static com.example.magentastreaming.Activities.ApplicationClass.CHANNEL_ID_2;
import static com.example.magentastreaming.Activities.PlayerActivity.bitmap;
import static com.example.magentastreaming.Activities.PlayerActivity.listOfSongs;
import static com.example.magentastreaming.Activities.PlayerActivity.mediaPlayer;
import static com.example.magentastreaming.Activities.PlayerActivity.mediaSession;
import static com.example.magentastreaming.Activities.PlayerActivity.position;
import static com.example.magentastreaming.Activities.PlayerActivity.uri;
import static com.example.magentastreaming.Fragments.HomeFragment.musicFiles;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.magentastreaming.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class MusicService extends Service {
    private IBinder mBinder = new MyBinder();
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREV = "PREVIOUS";
    public static final String ACTION_PLAY = "PLAY";

    ActionPlaying actionPlaying;

    StorageReference storageReference;

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
        if (actionName != null && actionPlaying != null) {
            switch (actionName) {
                case ACTION_PLAY:
                    if (actionName != null) {
                        actionPlaying.playClicked();
                    }
                    break;

                case ACTION_NEXT:
                    if (actionName != null) {
                        actionPlaying.nextClicked();
                    }
                    break;

                case ACTION_PREV:
                    if (actionName != null) {
                        actionPlaying.prevClicked();
                    }
                    break;
            }
        } else {
            switch (actionName) {
                case ACTION_PLAY:
                    if (actionName != null) {
                        playClickedBg();
                    }
                    break;

                case ACTION_NEXT:
                    if (actionName != null) {
                        nextClickedBg();
                    }
                    break;

                case ACTION_PREV:
                    if (actionName != null) {
                        prevClickedBg();
                    }
                    break;
            }
        }
        return START_STICKY;
    }

    private void prevClickedBg() {
        if (position == 0) {
            position = musicFiles.size()-1;
        } else {
            position--;
        }

        getImage();

        try {
            if (listOfSongs != null) {
                uri = Uri.parse(listOfSongs.get(position).getClip_source());
            }

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
            } else {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
            }

            if (mediaPlayer.isPlaying()) {
                showNotification(R.drawable.pause_solid);
            } else {
                showNotification(R.drawable.play_solid);
            }

        } catch (Exception e) {
            Log.d("Error", "nextThreadButton: "+e.getMessage());
        }
    }

    private void nextClickedBg() {
        position = (position+1)%musicFiles.size();
        getImage();

        try {
            if (listOfSongs != null) {
                uri = Uri.parse(listOfSongs.get(position).getClip_source());
            }

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
            } else {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
            }

            if (mediaPlayer.isPlaying()) {
                showNotification(R.drawable.pause_solid);
            } else {
                showNotification(R.drawable.play_solid);
            }

        } catch (Exception e) {
            Log.d("Error", "nextThreadButton: "+e.getMessage());
        }
    }

    private void playClickedBg() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            showNotification(R.drawable.play_solid);
        } else {
            mediaPlayer.start();
            showNotification(R.drawable.pause_solid);

        }
    }

    public void setCallBack(ActionPlaying actionPlaying) {
        this.actionPlaying = actionPlaying;
    }

    public void showNotification(int playPauseBtn) {
        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_MUTABLE);

        Intent playIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_MUTABLE);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_MUTABLE);

        Bitmap picture = bitmap;

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(R.drawable.main_icon)
                .setLargeIcon(picture)
                .setContentTitle(musicFiles.get(position).getTitle())
                .setContentText(musicFiles.get(position).getArtist())
                .addAction(R.drawable.backward_step_solid, "Previous", prevPendingIntent)
                .addAction(playPauseBtn, "Play", playPendingIntent)
                .addAction(R.drawable.next_music, "Next", nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

    }

    public void getImage() {
        try {
            storageReference = FirebaseStorage.getInstance().getReference("album_arts/"+listOfSongs.get(position).getAlbumArt()+".jpg");
            File localFile = File.createTempFile("tempFile", ".jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
