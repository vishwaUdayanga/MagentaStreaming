package com.example.magentastreaming.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magentastreaming.Adapters.SongAdapter;
import com.example.magentastreaming.Models.Liked;
import com.example.magentastreaming.Models.MusicFiles;
import com.example.magentastreaming.Models.User;
import com.example.magentastreaming.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LikedFragment extends Fragment {

    RecyclerView recyclerView;
    SongAdapter songAdapter;

    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;


    User appUser;

    static ArrayList<MusicFiles> songs;

    FirebaseAuth auth;

    FirebaseUser user;

    ArrayList<Liked> liked;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_liked, container, false);
        recyclerView = view.findViewById(R.id.liked_songs_recyclerView);

        //getting user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        liked = new ArrayList<Liked>();

        appUser = new User(user.getEmail(),user.getUid());

        songs = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("liked");
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("songs");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    if(dataSnapshot.child("userID").getValue().toString().equals(appUser.getUserID()))
                    {
                        liked.add(new Liked(dataSnapshot.child("userID").getValue().toString(),dataSnapshot.child("songID").getValue().toString()));

                    }

                }

                songAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", "Error");
            }
        });

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i=0; i< liked.size(); i++) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        if(dataSnapshot.getKey().toString().equals(liked.get(i).getSongID()))
                        {
                            songs.add(new MusicFiles(dataSnapshot.child("title").getValue().toString(), dataSnapshot.child("album_art").getValue().toString(), dataSnapshot.child("album_name").getValue().toString(), Double.parseDouble(dataSnapshot.child("duration").getValue().toString()), dataSnapshot.child("artist").getValue().toString(), dataSnapshot.child("clip_source").getValue().toString(), dataSnapshot.child("genre").getValue().toString(), dataSnapshot.getKey().toString()));
                        }
                        else {

                        }
                    }

                }
                songAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", "Error");
            }
        });

        songAdapter = new SongAdapter(getContext(), songs);

        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false));



        return view;
    }
}