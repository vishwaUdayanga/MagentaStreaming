package com.example.magentastreaming.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.magentastreaming.Activities.PlayerActivity;
import com.example.magentastreaming.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder>{

    private Context mContext;
    private ArrayList<MusicFiles> mFiles;

    StorageReference storageReference;

    public MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles) {
        this.mContext = mContext;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        holder.artist.setText(mFiles.get(position).getArtist());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        storageReference = FirebaseStorage.getInstance().getReference("album_arts/"+mFiles.get(position).getAlbumArt()+".jpg");

        try {
            File localFile = File.createTempFile("tempFile", ".jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
                    if (bitmap!= null) {
                        Glide.with(mContext).asBitmap()
                                .load(bitmap)
                                .apply(requestOptions)
                                .into(holder.album_art);
                    }
                    else {
                        Glide.with(mContext)
                                .load(R.drawable.sample_bg)
                                .apply(requestOptions)
                                .into(holder.album_art);
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
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("isNew", true);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView file_name;
        TextView artist;
        ImageView album_art;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_title);
            artist = itemView.findViewById(R.id.artist);
            album_art = itemView.findViewById(R.id.music_img);
        }
    }

//    private byte[] getAlbumArt(String uri) {
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        byte[] art;
//        try {
//            retriever.setDataSource(uri);
//            art = retriever.getEmbeddedPicture();
//            retriever.release();
//        } catch (Exception e) {
//            return null;
//        }
//        return art;
//    }
}
