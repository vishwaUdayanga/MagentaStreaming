package com.example.magentastreaming.Activities;

import static com.example.magentastreaming.Activities.ApplicationClass.ACTION_NEXT;
import static com.example.magentastreaming.Activities.ApplicationClass.ACTION_PLAY;
import static com.example.magentastreaming.Activities.ApplicationClass.ACTION_PREV;
import static com.example.magentastreaming.Activities.ApplicationClass.CHANNEL_ID_2;
import static com.example.magentastreaming.Fragments.HomeFragment.musicFiles;
import static com.example.magentastreaming.Fragments.MinimizedPlayer.playButton;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.magentastreaming.Models.Liked;
import com.example.magentastreaming.Models.MusicFiles;
import com.example.magentastreaming.Models.User;
import com.example.magentastreaming.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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

    boolean found = false;

    public static FloatingActionButton playPauseButton;
    SeekBar seekBar;
    public static int position;
    static ArrayList<MusicFiles> listOfSongs = new ArrayList<>();
    public static MediaPlayer mediaPlayer;
    public static Uri uri;

    private Handler handler = new Handler();

    StorageReference storageReference;
    private Thread playThread, prevThread, nextThread;

    public static Bitmap bitmap;

    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST = "STORED_ARTIST";
    public static final String SONG = "STORED_SONG";

    public static final String ALBUM_ART = "STORED_ART";
    public static String SONG_NAME = "Song name";

    public static String ARTIST_NAME = "Artist name";


    public static MediaSessionCompat mediaSession;

    ImageView heart;

    DatabaseReference databaseReference2;
    FirebaseAuth auth;

    FirebaseUser user;
    User appUser;

    String likedId;

    DatabaseReference fetchingSong;

    String songId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        boolean isNew = getIntent().getBooleanExtra("isNew", false);
        songId = getIntent().getStringExtra("songId");
        boolean isMain = getIntent().getBooleanExtra("mainSong", false);
        if (songId != null) {
            filteredPlay();
        } else if (isNew) {
            getActivityData();
        } else if (isMain) {
            position = 0;
            getActivityData();
        } else {
            resumeMusic();
        }

        heart = findViewById(R.id.like_logo);

        //like
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        appUser = new User(user.getEmail(),user.getUid());


        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference2 = FirebaseDatabase.getInstance().getReference().child("liked");

                if (databaseReference2 == null) {
                    found = false;
                }
                databaseReference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            try {
                                if(dataSnapshot.child("userID").getValue().toString().contains(appUser.getUserID()) && dataSnapshot.child("songID").getValue().toString().contains(musicFiles.get(position).getSongId()))
                                {
                                    found = true;
                                    likedId = dataSnapshot.getKey().toString();
                                    return;

                                }
                            } catch (Exception e) {
                                found = false;
                            }

                        }
                        found = false;
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Error", "Error");
                        found = false;
                    }
                });

                if (!found) {
                    Liked newLike = new Liked(appUser.getUserID(), musicFiles.get(position).getSongId());
                    databaseReference2.push().setValue(newLike);
                    heart.setImageResource(R.drawable.heart_solid);
                } else {
                    try {
                        databaseReference2.child(likedId).removeValue().addOnSuccessListener(aVoid -> {
                            heart.setImageResource(R.drawable.heart);
                        }).addOnFailureListener(e -> {
                            heart.setImageResource(R.drawable.heart_solid);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


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

//        mediaSession = new MediaSessionCompat(this, "AudioPlayer");
        showNotification(R.drawable.pause_solid);

    }

    public void checkLiked() {
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("liked");

        if (databaseReference2 == null) {
            found = false;
        }
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    try {
                        if(dataSnapshot.child("userID").getValue().toString().contains(appUser.getUserID()) && dataSnapshot.child("songID").getValue().toString().contains(musicFiles.get(position).getSongId()))
                        {
                            heart.setImageResource(R.drawable.heart_solid);

                        } else {
                            heart.setImageResource(R.drawable.heart);
                        }
                    } catch (Exception e) {
                        heart.setImageResource(R.drawable.heart);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", "Error");
                heart.setImageResource(R.drawable.heart);
            }
        });
    }



    private void filteredPlay() {
        for(int i=0; i<musicFiles.size(); i++) {
            if (musicFiles.get(i).getSongId().equals(songId)) {
                position = i;
            } else {
                position = 0;
            }
        }
        getActivityData();
        checkLiked();
        playButton.setImageResource(R.drawable.pause);
    }

    private void resumeMusic() {
        setData();
        showNotification(R.drawable.pause_solid);
        checkLiked();
        playButton.setImageResource(R.drawable.pause);
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
    }

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
        checkLiked();
        playButton.setImageResource(R.drawable.pause);

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
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE).edit();
        editor.putString(MUSIC_FILE, uri.toString());
        editor.putString(ARTIST, listOfSongs.get(position).getArtist());
        editor.putString(SONG, listOfSongs.get(position).getTitle());
        editor.putString(ALBUM_ART, listOfSongs.get(position).getAlbumArt());
        editor.apply();
        SONG_NAME = listOfSongs.get(position).getTitle();
        ARTIST_NAME = listOfSongs.get(position).getArtist();
        editor.apply();
        seekBar.setMax(mediaPlayer.getDuration() /  1000);
        durationTotal.setText(String.valueOf(listOfSongs.get(position).getDuration()));
        showNotification(R.drawable.pause_solid);
    }

    private void setData() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        Glide.with(getApplicationContext()).asBitmap()
                .load(R.drawable.sample_bg)
                .apply(requestOptions)
                .into(coverArt);
        title.setText(musicFiles.get(position).getTitle());
        artist.setText(musicFiles.get(position).getArtist());
        storageReference = FirebaseStorage.getInstance().getReference("album_arts/"+musicFiles.get(position).getAlbumArt()+".jpg");

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

        checkLiked();

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
            SONG_NAME = listOfSongs.get(position).getTitle();
            ARTIST_NAME = listOfSongs.get(position).getArtist();

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

        checkLiked();

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
            SONG_NAME = listOfSongs.get(position).getTitle();
            ARTIST_NAME = listOfSongs.get(position).getArtist();

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
            playButton.setImageResource(R.drawable.play_solid);
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
            playButton.setImageResource(R.drawable.pause);
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