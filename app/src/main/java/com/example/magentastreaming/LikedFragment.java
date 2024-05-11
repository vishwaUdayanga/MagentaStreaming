package com.example.magentastreaming;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    static ArrayList<Song> songs;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_liked, container, false);
        recyclerView = view.findViewById(R.id.liked_songs_recyclerView);

        songs = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("genres");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    songs.add(new Song(dataSnapshot.child("genre_title").getValue().toString(), dataSnapshot.child("genre_art").getValue().toString(),"",""));
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