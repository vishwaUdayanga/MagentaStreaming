package com.example.magentastreaming.Activities;

import static com.example.magentastreaming.Activities.ApplicationClass.ACTION_NEXT;
import static com.example.magentastreaming.Activities.ApplicationClass.ACTION_PLAY;
import static com.example.magentastreaming.Activities.ApplicationClass.ACTION_PREV;
import static com.example.magentastreaming.Activities.ApplicationClass.CHANNEL_ID_2;
import static com.example.magentastreaming.Fragments.HomeFragment.musicFiles;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.magentastreaming.Models.MusicFiles;
import com.example.magentastreaming.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements ServiceConnection, ActionPlaying {
    MusicService musicService;
    TextView title, artist, durationPlayed, durationTotal;
    ImageView coverArt, nextButton, prevButton, backButton;

    FloatingActionButton playPauseButton;
    SeekBar seekBar;
    public static int position;
    static ArrayList<MusicFiles> listOfSongs = new ArrayList<>();
    static MediaPlayer mediaPlayer;
    public static Uri uri;

    private Handler handler = new Handler();

    StorageReference storageReference;
    private Thread playThread, prevThread, nextThread;

    public static Bitmap bitmap;


    public static MediaSessionCompat mediaSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        boolean isNew = getIntent().getBooleanExtra("isNew", false);
        if (isNew) {
            getActivityData();
        } else {
            resumeMusic();
        }


        //Button listeners
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextClicked();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevClicked();
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClicked();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    durationPlayed.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            position = (position+1)%musicFiles.size();
            try {
                mp.reset();
                setData();
                mp.setDataSource(listOfSongs.get(position).getClip_source());
                durationTotal.setText(String.valueOf(listOfSongs.get(position).getDuration()));
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);

        mediaSession = new MediaSessionCompat(this, "AudioPlayer");
        showNotification(R.drawable.pause_solid);

    }

    private void resumeMusic() {
        setData();
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
    }

//    @Override
//    protected void onResume() {
////        playThreadButton();
////        nextThreadButton();
////        prevThreadButton();
//        Intent intent = new Intent(this, MusicService.class);
//        bindService(intent, this, BIND_AUTO_CREATE);
//        super.onResume();
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        unbindService(this);
//    }
//
//    private void prevThreadButton() {
//        prevThread = new Thread() {
//            @Override
//            public  void run() {
//                super.run();
//
//                prevButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public  void onClick(View v) {
//                        prevButtonButtonClicked();
//                        if (mediaPlayer.isPlaying()) {
//                            showNotification(R.drawable.pause_solid);
//                        } else {
//                            showNotification(R.drawable.play_solid);
//                        }
//                    }
//                });
//            }
//        };
//        prevThread.start();
//    }
//
//    private void prevButtonButtonClicked() {
//        if (position == 0) {
//            position = musicFiles.size()-1;
//        } else {
//            position--;
//        }
//
//        try {
//            setData();
//            if (listOfSongs != null) {
//                playPauseButton.setImageResource(R.drawable.pause_solid);
//                uri = Uri.parse(listOfSongs.get(position).getClip_source());
//            }
//
//            if (mediaPlayer != null) {
//                mediaPlayer.stop();
//                mediaPlayer.release();
//                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
//                mediaPlayer.start();
//            } else {
//                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
//                mediaPlayer.start();
//            }
//            seekBar.setMax(mediaPlayer.getDuration() /  1000);
//            durationTotal.setText(String.valueOf(listOfSongs.get(position).getDuration()));
//
//        } catch (Exception e) {
//            Log.d("Error", "nextThreadButton: "+e.getMessage());
//        }
//    }
//
//    private void nextThreadButton() {
//
//        nextThread = new Thread() {
//            @Override
//            public void run() {
//                super.run();
//
//                nextButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        nextButtonButtonClicked();
//                        if (mediaPlayer.isPlaying()) {
//                            showNotification(R.drawable.pause_solid);
//                        } else {
//                            showNotification(R.drawable.play_solid);
//                        }
//
//                    }
//                });
//            }
//        };
//        nextThread.start();
//    }
//
//    private void nextButtonButtonClicked() {
//
//        position = (position+1)%musicFiles.size();
//
//        try {
//            setData();
//            if (listOfSongs != null) {
//                playPauseButton.setImageResource(R.drawable.pause_solid);
//                uri = Uri.parse(listOfSongs.get(position).getClip_source());
//            }
//
//            if (mediaPlayer != null) {
//                mediaPlayer.stop();
//                mediaPlayer.release();
//                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
//                mediaPlayer.start();
//            } else {
//                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
//                mediaPlayer.start();
//            }
//            seekBar.setMax(mediaPlayer.getDuration() /  1000);
//            durationTotal.setText(String.valueOf(listOfSongs.get(position).getDuration()));
//
//        } catch (Exception e) {
//            Log.d("Error", "nextThreadButton: "+e.getMessage());
//        }
//    }
//
//    private void playThreadButton() {
//        playThread = new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                playPauseButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        playPauseButtonClicked();
//                    }
//                });
//            }
//        };
//        playThread.start();
//    }
//
//    private void playPauseButtonClicked() {
//        if (mediaPlayer.isPlaying()) {
//            playPauseButton.setImageResource(R.drawable.play_solid);
//            mediaPlayer.pause();
//            showNotification(R.drawable.play_solid);
//            seekBar.setMax(mediaPlayer.getDuration() / 1000);
//            PlayerActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (mediaPlayer != null) {
//                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
//                        seekBar.setProgress(mCurrentPosition);
//                    }
//                    handler.postDelayed(this, 1000);
//                }
//            });
//        } else {
//            playPauseButton.setImageResource(R.drawable.pause_solid);
//            mediaPlayer.start();
//            showNotification(R.drawable.pause_solid);
//            seekBar.setMax(mediaPlayer.getDuration() / 1000);
//            PlayerActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (mediaPlayer != null) {
//                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
//                        seekBar.setProgress(mCurrentPosition);
//                    }
//                    handler.postDelayed(this, 1000);
//                }
//            });
//        }
//    }

    private String formattedTime(int mCurrentPosition) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1) {
                return totalNew;
        } else {
            return totalOut;
        }
    }

    private void getActivityData() {
        position = getIntent().getIntExtra("position", position);
        listOfSongs = musicFiles;

        setData();

        if (listOfSongs != null) {
            playPauseButton.setImageResource(R.drawable.pause_solid);
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
        seekBar.setMax(mediaPlayer.getDuration() /  1000);
        durationTotal.setText(String.valueOf(listOfSongs.get(position).getDuration()));
    }

    private void setData() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        Glide.with(getApplicationContext()).asBitmap()
                .load(R.drawable.sample_bg)
                .apply(requestOptions)
                .into(coverArt);
        title.setText(listOfSongs.get(position).getTitle());
        artist.setText(listOfSongs.get(position).getArtist());
        storageReference = FirebaseStorage.getInstance().getReference("album_arts/"+listOfSongs.get(position).getAlbumArt()+".jpg");

        try {
            File localFile = File.createTempFile("tempFile", ".jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
                    if (bitmap!= null) {
                        Glide.with(getApplicationContext()).asBitmap()
                                .load(bitmap)
                                .apply(requestOptions)
                                .into(coverArt);
                    }
                    else {
                        Glide.with(getApplicationContext()).asBitmap()
                                .load(R.drawable.sample_bg)
                                .apply(requestOptions)
                                .into(coverArt);
                    }
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


    private void initViews() {
            title = findViewById(R.id.title);
            artist = findViewById(R.id.artist);
            durationPlayed = findViewById(R.id.duration_played);
            durationTotal = findViewById(R.id.duration_total);
            coverArt = findViewById(R.id.cover_art);
            nextButton = findViewById(R.id.go_next);
            prevButton = findViewById(R.id.go_to_previous);
            playPauseButton = findViewById(R.id.play_pause);
            backButton = findViewById(R.id.back_icon);
            seekBar = findViewById(R.id.seekbar);

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
        musicService.setCallBack(PlayerActivity.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
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

    @Override
    public void nextClicked() {
        position = (position+1)%musicFiles.size();

        try {
            setData();
            if (listOfSongs != null) {
                playPauseButton.setImageResource(R.drawable.pause_solid);
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
            seekBar.setMax(mediaPlayer.getDuration() /  1000);
            durationTotal.setText(String.valueOf(listOfSongs.get(position).getDuration()));

            if (mediaPlayer.isPlaying()) {
                showNotification(R.drawable.pause_solid);
            } else {
                showNotification(R.drawable.play_solid);
            }

        } catch (Exception e) {
            Log.d("Error", "nextThreadButton: "+e.getMessage());
        }
    }

    @Override
    public void prevClicked() {
        if (position == 0) {
            position = musicFiles.size()-1;
        } else {
            position--;
        }

        try {
            setData();
            if (listOfSongs != null) {
                playPauseButton.setImageResource(R.drawable.pause_solid);
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
            seekBar.setMax(mediaPlayer.getDuration() /  1000);
            durationTotal.setText(String.valueOf(listOfSongs.get(position).getDuration()));

            if (mediaPlayer.isPlaying()) {
                showNotification(R.drawable.pause_solid);
            } else {
                showNotification(R.drawable.play_solid);
            }

        } catch (Exception e) {
            Log.d("Error", "nextThreadButton: "+e.getMessage());
        }
    }

    @Override
    public void playClicked() {
        if (mediaPlayer.isPlaying()) {
            playPauseButton.setImageResource(R.drawable.play_solid);
            mediaPlayer.pause();
            showNotification(R.drawable.play_solid);
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {
            playPauseButton.setImageResource(R.drawable.pause_solid);
            mediaPlayer.start();
            showNotification(R.drawable.pause_solid);
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }
}