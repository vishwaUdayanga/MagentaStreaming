package com.example.magentastreaming.Fragments;

//import static com.example.magentastreaming.Activities.AppHolder.viewPager2;

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

import com.example.magentastreaming.Adapters.MusicAdapter;
import com.example.magentastreaming.Models.MusicFiles;
import com.example.magentastreaming.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    MusicAdapter musicAdapter;

    DatabaseReference databaseReference;

    public static ArrayList<MusicFiles> musicFiles;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.home_recyclerView);
        recyclerView.setHasFixedSize(true);
        //Get audio files
        musicFiles = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("songs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    musicFiles.add(new MusicFiles(dataSnapshot.child("title").getValue().toString(), dataSnapshot.child("album_art").getValue().toString(), dataSnapshot.child("album_name").getValue().toString(), Double.parseDouble(dataSnapshot.child("duration").getValue().toString()), dataSnapshot.child("artist").getValue().toString(), dataSnapshot.child("clip_source").getValue().toString(), dataSnapshot.child("genre").getValue().toString(), dataSnapshot.getKey().toString()));
                }
                musicAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", "Error");
            }
        });

        musicAdapter = new MusicAdapter(getContext(), musicFiles);
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false));


        return view;
    }
}