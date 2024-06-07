package com.example.magentastreaming;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AppHolder extends AppCompatActivity {

    static ViewPagerFragmentAdapter viewPagerFragmentAdapter;
    TabLayout tabLayout;
//    static ViewPager2 viewPager2;

    FrameLayout frameLayout;

    static FragmentManager fragmentManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_holder);

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