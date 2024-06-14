package com.example.magentastreaming.Fragments;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.magentastreaming.Adapters.FavouriteGenreAdapter;
import com.example.magentastreaming.Adapters.GenreAdapter;
import com.example.magentastreaming.Models.Genre;
import com.example.magentastreaming.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GenreFragment extends Fragment {

    String url = "https://si-system-api.onrender.com/api/genres";
    RequestQueue queue;

    RecyclerView recyclerView1,recyclerView2;
    GenreAdapter genreAdapter;
    FavouriteGenreAdapter favouriteGenreAdapter;
    DatabaseReference databaseReference;

    static ArrayList<Genre> genres;

    TextView textView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_genre, container, false);
        textView = view.findViewById(R.id.Top_genre);
        recyclerView1 = view.findViewById(R.id.regular_genre_recycler_view);
        recyclerView2 = view.findViewById(R.id.favourite_genre_recycler_view);
        genres = new ArrayList<Genre>();
        //initializing queue
        queue = Volley.newRequestQueue(getContext());
        loadData();
        JSONObject jsonObject;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                String genrename = response.getJSONObject(i).getString("genre");
                                genres.add(new Genre(genrename,null,i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        queue.add(jsonArrayRequest);

        //Getting genres


        databaseReference = FirebaseDatabase.getInstance().getReference().child("genres");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    genres.add(new Genre(dataSnapshot.child("genre_title").getValue().toString(), dataSnapshot.child("genre_art").getValue().toString(),1));
                }
                loadData();
                genreAdapter.notifyDataSetChanged();
                favouriteGenreAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", "Error");
            }
        });


        favouriteGenreAdapter = new FavouriteGenreAdapter(getContext(),genres);

        genreAdapter = new GenreAdapter(getContext(), genres);
        recyclerView2.setAdapter(favouriteGenreAdapter);
        recyclerView2.setLayoutManager(new GridLayoutManager(getContext(), 1, RecyclerView.HORIZONTAL, false));

        recyclerView1.setAdapter(genreAdapter);
        recyclerView1.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false));

        return view;
    }
    public void loadData()
    {

        JSONObject jsonObject;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                System.out.println();
                                String genrename = response.getJSONObject(i).getString("genre");
                                genres.add(new Genre(genrename,null,1));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        queue.add(jsonArrayRequest);
    }
}