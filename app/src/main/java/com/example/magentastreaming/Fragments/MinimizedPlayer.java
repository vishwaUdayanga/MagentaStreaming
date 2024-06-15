package com.example.magentastreaming.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.magentastreaming.Activities.AppHolder.MUSIC_FILE;
import static com.example.magentastreaming.Activities.AppHolder.SHOW_MINI_PLAYER;
import static com.example.magentastreaming.Activities.PlayerActivity.ALBUM_ART;
import static com.example.magentastreaming.Activities.PlayerActivity.MUSIC_LAST_PLAYED;
import static com.example.magentastreaming.Activities.PlayerActivity.SONG;
import static com.example.magentastreaming.Activities.PlayerActivity.SONG_NAME;
import static com.example.magentastreaming.Activities.PlayerActivity.bitmap;
import static com.example.magentastreaming.Activities.PlayerActivity.mediaPlayer;
import static com.example.magentastreaming.Activities.PlayerActivity.position;
import static com.example.magentastreaming.Activities.PlayerActivity.uri;
import static com.example.magentastreaming.Fragments.HomeFragment.musicFiles;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class MinimizedPlayer extends Fragment {

    ImageView nextButton, prevButton, playButton, albumArt;
    TextView artist, songName;

    View view;

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

        position = 0;

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minimizedPlay();
            }
        });

        return view;
    }

    private void minimizedPlay() {
        if (mediaPlayer == null) {

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
            } else {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);;
                storageReference = FirebaseStorage.getInstance().getReference("album_arts/"+sharedPreferences.getString(ALBUM_ART, null)+".jpg");
                songName.setText(sharedPreferences.getString(SONG, null));
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
            }

        }
    }
}