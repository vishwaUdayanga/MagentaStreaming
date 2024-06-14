package com.example.magentastreaming.Adapters;

import static com.example.magentastreaming.Activities.AppHolder.fragmentManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.magentastreaming.Fragments.GenreListFragment;
import com.example.magentastreaming.Models.Genre;
import com.example.magentastreaming.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;



public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {
    private Context genreContext;
    private ArrayList<Genre> genreFiles;

    StorageReference storageReference;

    public GenreAdapter(Context genreContext, ArrayList<Genre> genreFiles) {
        this.genreContext = genreContext;
        this.genreFiles = genreFiles;
    }

    @NonNull
    @Override
    public GenreAdapter.GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(genreContext).inflate(R.layout.item_genre, parent, false);
        return new GenreAdapter.GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreAdapter.GenreViewHolder holder, int position) {
        holder.genreTitle.setText(genreFiles.get(position).getGenreName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        storageReference = FirebaseStorage.getInstance().getReference("genre_arts/"+genreFiles.get(position).getGenreArt()+".jpg");

        try {
            File localFile = File.createTempFile("tempFile", ".jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
                    if (bitmap!= null) {
                        Glide.with(genreContext).asBitmap()
                                .load(bitmap)
                                .apply(requestOptions)
                                .into(holder.genreArt);
                    }
                    else {
                        Glide.with(genreContext)
                                .load(R.drawable.sample_bg)
                                .apply(requestOptions)
                                .into(holder.genreArt);
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle =new Bundle();
                bundle.putString("genre", genreFiles.get(position).getGenreName());



                Fragment fragment = new GenreListFragment();
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return genreFiles.size();
    }

    public class GenreViewHolder extends RecyclerView.ViewHolder {

        TextView genreTitle;
        ImageView genreArt;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            genreTitle = itemView.findViewById(R.id.regular_genre_text);
            genreArt = itemView.findViewById(R.id.regular_genre_img);
        }
    }

}
