package com.example.magentastreaming.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.magentastreaming.Fragments.GenreFragment;
import com.example.magentastreaming.Fragments.HomeFragment;
import com.example.magentastreaming.Fragments.LikedFragment;
import com.example.magentastreaming.Models.User;
import com.example.magentastreaming.R;
import com.example.magentastreaming.Fragments.SearchFragment;
import com.example.magentastreaming.Adapters.ViewPagerFragmentAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class AppHolder extends AppCompatActivity {

    static ViewPagerFragmentAdapter viewPagerFragmentAdapter;
    TabLayout tabLayout;
//    static ViewPager2 viewPager2;

    FrameLayout frameLayout;

    public static FragmentManager fragmentManager;

    ImageView mainProfileImg;


    StorageReference storageReference;

    FirebaseAuth auth;
    FirebaseUser user;
    User appUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_holder);

        mainProfileImg = findViewById(R.id.main_profile_img);
        Glide.with(getApplicationContext()).asBitmap()
                .load(R.drawable.sample_user)
                .apply(RequestOptions.circleCropTransform())
                .into(mainProfileImg);
        //getting user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        appUser = new User(user.getEmail(),user.getUid());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        storageReference = FirebaseStorage.getInstance().getReference("profile_pic/"+appUser.getUserID());

        try {
            File localFile = File.createTempFile("tempFile", ".jpeg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
                    if (bitmap!= null) {
                        Glide.with(getApplicationContext()).asBitmap()
                                .load(bitmap)
                                .apply(RequestOptions.circleCropTransform())
                                .into(mainProfileImg);
                    }
                    else {
                        Glide.with(getApplicationContext())
                                .load(R.drawable.sample_user)
                                .apply(RequestOptions.circleCropTransform())
                                .into(mainProfileImg);
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

        mainProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(intent);
            }
        });

        frameLayout = findViewById(R.id.frame_layout);
//        viewPager2 = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        fragmentManager = getSupportFragmentManager();
//        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(this);
//        viewPagerFragmentAdapter.addFragments(new HomeFragment(), "Home");
//        viewPagerFragmentAdapter.addFragments(new GenreFragment(), "Genre");
//        viewPagerFragmentAdapter.addFragments(new LikedFragment(), "Liked");
//        viewPagerFragmentAdapter.addFragments(new SearchFragment(), "Search");

//        viewPager2.setAdapter(viewPagerFragmentAdapter);
//        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(viewPagerFragmentAdapter.getTitles().get(position))).attach();

        tabLayout.getTabAt(0).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setIcon(R.drawable.genre);
        tabLayout.getTabAt(2).setIcon(R.drawable.liked);
        tabLayout.getTabAt(3).setIcon(R.drawable.search);

        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.pink) , PorterDuff.Mode.SRC_IN);

        fragmentManager.beginTransaction().replace(R.id.frame_layout, new HomeFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

        tabLayout.setOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        tab.getIcon().setColorFilter(getResources().getColor(R.color.pink) , PorterDuff.Mode.SRC_IN);

                        Fragment fragment = null;

                        switch (tab.getPosition()) {
                            case 0:
                                fragment = new HomeFragment();
                                break;

                            case 1:
                                fragment = new GenreFragment();
                                break;

                            case 2:
                                fragment = new LikedFragment();
                                break;

                            case 3:
                                fragment = new SearchFragment();
                                break;
                        }

                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        tab.getIcon().setColorFilter(getResources().getColor(R.color.gray) , PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                }
        );


    }
}