package com.example.magentastreaming;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GenreListFragment extends Fragment {

    RecyclerView recyclerView;
    GenreListAdapter genreListAdapter;

    DatabaseReference databaseReference;

    ArrayList<MusicFiles> musicFiles;

    ArrayList<MusicFiles> musicFilesFiltered;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_genre_list, container, false);
        String genre = getArguments().getString("genre");


        recyclerView = view.findViewById(R.id.genrelist_recyclerView);
        recyclerView.setHasFixedSize(true);

        musicFiles = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("songs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("genre").getValue().toString().toLowerCase().contains(genre.toLowerCase())) {
                        musicFiles.add(new MusicFiles(dataSnapshot.child("title").getValue().toString(), dataSnapshot.child("album_art").getValue().toString(), dataSnapshot.child("album_name").getValue().toString(), Double.parseDouble(dataSnapshot.child("duration").getValue().toString()), dataSnapshot.child("artist").getValue().toString(), dataSnapshot.child("clip_source").getValue().toString(), dataSnapshot.child("genre").getValue().toString()));
                    }
                }

                genreListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", "Error");
            }
        });

        genreListAdapter = new GenreListAdapter(getContext(), musicFiles);
        recyclerView.setAdapter(genreListAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false));


        return view;
    }

}