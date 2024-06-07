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
import java.util.List;

public class SearchFragment extends Fragment {

    RecyclerView recyclerView;
    SearchAdapter searchAdapter;

    DatabaseReference databaseReference;
    SearchView searchView;

    ArrayList<MusicFiles> musicFiles;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initilializes searchView
        searchView = view.findViewById(R.id.search_bar);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredList(newText);
                return true;
            }
        });

        recyclerView = view.findViewById(R.id.search_recyclerView);
        recyclerView.setHasFixedSize(true);
        //Get audio files
        musicFiles = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("songs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    musicFiles.add(new MusicFiles(dataSnapshot.child("title").getValue().toString(), dataSnapshot.child("album_art").getValue().toString(), dataSnapshot.child("album_name").getValue().toString(), Double.parseDouble(dataSnapshot.child("duration").getValue().toString()), dataSnapshot.child("artist").getValue().toString(), dataSnapshot.child("clip_source").getValue().toString(), dataSnapshot.child("genre").getValue().toString()));
                }
                searchAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", "Error");
            }
        });

        searchAdapter = new SearchAdapter(getContext(), musicFiles);
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false));


        return view;
    }

    private void filteredList(String newText) {
        ArrayList<MusicFiles> filteredList = new ArrayList<>();
        for(MusicFiles musicFiles1 : musicFiles){
            if (musicFiles1.getTitle().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(musicFiles1);
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
        }else{
            searchAdapter.setFilteredList(filteredList);
        }
    }
}