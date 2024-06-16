package com.example.magentastreaming.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;
import static com.example.magentastreaming.Activities.AppHolder.MUSIC_FILE;
import static com.example.magentastreaming.Activities.AppHolder.SHOW_MINI_PLAYER;
import static com.example.magentastreaming.Activities.ApplicationClass.ACTION_NEXT;
import static com.example.magentastreaming.Activities.ApplicationClass.ACTION_PLAY;
import static com.example.magentastreaming.Activities.ApplicationClass.ACTION_PREV;
import static com.example.magentastreaming.Activities.ApplicationClass.CHANNEL_ID_2;
import static com.example.magentastreaming.Activities.PlayerActivity.ALBUM_ART;
import static com.example.magentastreaming.Activities.PlayerActivity.ARTIST;
import static com.example.magentastreaming.Activities.PlayerActivity.ARTIST_NAME;
import static com.example.magentastreaming.Activities.PlayerActivity.MUSIC_LAST_PLAYED;
import static com.example.magentastreaming.Activities.PlayerActivity.SONG;
import static com.example.magentastreaming.Activities.PlayerActivity.SONG_ID;
import static com.example.magentastreaming.Activities.PlayerActivity.SONG_NAME;
import static com.example.magentastreaming.Activities.PlayerActivity.bitmap;
import static com.example.magentastreaming.Activities.PlayerActivity.mediaPlayer;
import static com.example.magentastreaming.Activities.PlayerActivity.mediaSession;
import static com.example.magentastreaming.Activities.PlayerActivity.position;
import static com.example.magentastreaming.Activities.PlayerActivity.uri;
import static com.example.magentastreaming.Fragments.HomeFragment.musicFiles;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.magentastreaming.Activities.ActionPlaying;
import com.example.magentastreaming.Activities.Login;
import com.example.magentastreaming.Activities.NotificationReceiver;
import com.example.magentastreaming.Activities.PlayerActivity;
import com.example.magentastreaming.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class MinimizedPlayer extends Fragment {

    ImageView nextButton, prevButton;

    public static ImageView albumArt;
    public static ImageView playButton;
    public static TextView artist, songName;

    View view;

    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREV = "PREVIOUS";
    public static final String ACTION_PLAY = "PLAY";

    ActionPlaying actionPlaying;

    StorageReference storageReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_minimized_player, container, false);

        artist = view.findViewById(R.id.artist_bottom);
        songName = view.findViewById(R.id.song_name_bottom);
        albumArt = view.findViewById(R.id.songImg);
        nextButton = view.findViewById(R.id.bottom_next);
        prevButton = view.findViewById(R.id.bottom_prev);
        playButton = view.findViewById(R.id.bottom_play);
        mediaSession = new MediaSessionCompat(getContext(), "AudioPlayer");

        songName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer!=null) {
                    Intent intent = new Intent(getContext(), PlayerActivity.class);
                    startActivity(intent);
                } else {
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
                    if (sharedPreferences.getString(SONG_ID, null) != null) {
                        Intent intent = new Intent(getContext(), PlayerActivity.class);
                        intent.putExtra("songId", sharedPreferences.getString(SONG_ID, null));
                        startActivity(intent);
                    }

                }
            }
        });

        position = 0;

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minimizedPlay();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minimizedPrev();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minimizedNext();
            }
        });

        return view;
    }

    private void minimizedNext() {
        position = (position+1)%musicFiles.size();

        try {
            if (musicFiles != null) {
                playButton.setImageResource(R.drawable.pause);
                uri = Uri.parse(musicFiles.get(position).getClip_source());
            }

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(getContext(), uri);
                mediaPlayer.start();
            } else {
                mediaPlayer = MediaPlayer.create(getContext(), uri);
                mediaPlayer.start();
            }
            songName.setText(musicFiles.get(position).getTitle());
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
                            Glide.with(getContext()).asBitmap()
                                    .load(bitmap)
                                    .apply(requestOptions)
                                    .into(albumArt);
                        }
                        else {
                            Glide.with(getContext()).asBitmap()
                                    .load(R.drawable.sample_bg)
                                    .apply(requestOptions)
                                    .into(albumArt);
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

            if (mediaPlayer.isPlaying()) {
                showNotification(R.drawable.pause_solid);
            } else {
                showNotification(R.drawable.play_solid);
            }

        } catch (Exception e) {
            Log.d("Error", "nextThreadButton: "+e.getMessage());
        }
    }

    private void minimizedPrev() {
        if (position == 0) {
            position = musicFiles.size()-1;
        } else {
            position--;
        }

        try {
            if (musicFiles != null) {
                playButton.setImageResource(R.drawable.pause);
                uri = Uri.parse(musicFiles.get(position).getClip_source());
            }

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(getContext(), uri);
                mediaPlayer.start();
            } else {
                mediaPlayer = MediaPlayer.create(getContext(), uri);
                mediaPlayer.start();
            }
            songName.setText(musicFiles.get(position).getTitle());
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
                            Glide.with(getContext()).asBitmap()
                                    .load(bitmap)
                                    .apply(requestOptions)
                                    .into(albumArt);
                        }
                        else {
                            Glide.with(getContext()).asBitmap()
                                    .load(R.drawable.sample_bg)
                                    .apply(requestOptions)
                                    .into(albumArt);
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

            if (mediaPlayer.isPlaying()) {
                showNotification(R.drawable.pause_solid);
            } else {
                showNotification(R.drawable.play_solid);
            }

        } catch (Exception e) {
            Log.d("Error", "nextThreadButton: "+e.getMessage());
        }
    }

    private void minimizedPlay() {
        if (mediaPlayer == null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
            position = 0;
            for(int i=0; i<musicFiles.size(); i++) {
                if (musicFiles.get(i).getSongId().toString().equalsIgnoreCase(sharedPreferences.getString(SONG_ID, null))) {
                    position = i;
                }
            }
            uri = Uri.parse(sharedPreferences.getString(MUSIC_FILE, null));
            mediaPlayer = MediaPlayer.create(getContext(), uri);
            mediaPlayer.start();
            playButton.setImageResource(R.drawable.pause);
        }
        if (mediaPlayer.isPlaying()) {
            playButton.setImageResource(R.drawable.play_solid);
            mediaPlayer.pause();
            showNotification(R.drawable.play_solid);
        } else {
            playButton.setImageResource(R.drawable.pause);
            mediaPlayer.start();
            showNotification(R.drawable.pause_solid);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        if (SHOW_MINI_PLAYER) {
            if (bitmap != null) {
                Glide.with(getContext()).asBitmap()
                        .load(bitmap)
                        .apply(requestOptions)
                        .into(albumArt);
                songName.setText(SONG_NAME);
                artist.setText(ARTIST_NAME);
            } else {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
                if (sharedPreferences.getString(SONG, null) == null) {
                    Glide.with(getContext()).asBitmap()
                            .load(R.drawable.sample_bg)
                            .apply(requestOptions)
                            .into(albumArt);
                    songName.setText("Song name");
                    artist.setText("Artist");
                } else {
                    songName.setText(sharedPreferences.getString(SONG, null));
                    artist.setText(sharedPreferences.getString(ARTIST, null));
                    try {
                        storageReference = FirebaseStorage.getInstance().getReference("album_arts/"+sharedPreferences.getString(ALBUM_ART, null)+".jpg");
                        File localFile = File.createTempFile("tempFile", ".jpg");
                        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
                                if (bitmap!= null) {
                                    Glide.with(getContext()).asBitmap()
                                            .load(bitmap)
                                            .apply(requestOptions)
                                            .into(albumArt);
                                }
                                else {
                                    Glide.with(getContext()).asBitmap()
                                            .load(R.drawable.sample_bg)
                                            .apply(requestOptions)
                                            .into(albumArt);
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
            }

        }
    }

    public void showNotification(int playPauseBtn) {
        Intent intent = new Intent(getContext(), PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_MUTABLE);

        Intent prevIntent = new Intent(getContext(), NotificationReceiver.class).setAction(ACTION_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(getContext(), 0, prevIntent, PendingIntent.FLAG_MUTABLE);

        Intent playIntent = new Intent(getContext(), NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(getContext(), 0, playIntent, PendingIntent.FLAG_MUTABLE);

        Intent nextIntent = new Intent(getContext(), NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(getContext(), 0, nextIntent, PendingIntent.FLAG_MUTABLE);

        Bitmap picture = bitmap;

        Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID_2)
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

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService( getActivity().NOTIFICATION_SERVICE );
        notificationManager.notify(0, notification);

    }
}