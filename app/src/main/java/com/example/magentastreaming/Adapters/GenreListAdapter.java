package com.example.magentastreaming.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.magentastreaming.Models.MusicFiles;
import com.example.magentastreaming.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class GenreListAdapter extends RecyclerView.Adapter<GenreListAdapter.GenreListViewHolder> {

    private Context songContext;
    private ArrayList<MusicFiles> musicFiles;

    StorageReference storageReference;

    public void setFilteredList(ArrayList<MusicFiles> filteredList){
        this.musicFiles = filteredList;
        notifyDataSetChanged();
    }

    public GenreListAdapter(Context songContext, ArrayList<MusicFiles> musicFiles) {
        this.songContext = songContext;
        this.musicFiles = musicFiles;
    }

    @NonNull
    @Override
    public GenreListAdapter.GenreListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(songContext).inflate(R.layout.item_song, parent, false);
        return new GenreListAdapter.GenreListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreListAdapter.GenreListViewHolder holder, int position) {
        holder.songTitle.setText(musicFiles.get(position).getTitle());
        holder.songDuration.setText(String.valueOf(musicFiles.get(position).getDuration()));
        holder.songArtist.setText(musicFiles.get(position).getArtist());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        storageReference = FirebaseStorage.getInstance().getReference("album_arts/"+musicFiles.get(position).getAlbumArt()+".jpg");

        try {
            File localFile = File.createTempFile("tempFile", ".jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
                    if (bitmap!= null) {
                        Glide.with(songContext).asBitmap()
                                .load(bitmap)
                                .apply(requestOptions)
                                .into(holder.songArt);
                    }
                    else {
                        Glide.with(songContext)
                                .load(R.drawable.sample_bg)
                                .apply(requestOptions)
                                .into(holder.songArt);
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
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(genreContext, PlayerActivity.class);
//                intent.putExtra("position", position);
//                genreContext.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    public class GenreListViewHolder extends RecyclerView.ViewHolder {

        TextView songTitle;

        TextView songDuration;
        TextView songArtist;

        ImageView songArt;

        public GenreListViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.song_name);
            songArt = itemView.findViewById(R.id.song_img);
            songDuration = itemView.findViewById(R.id.song_duration);
            songArtist = itemView.findViewById(R.id.song_artist);
        }
    }
}
