package com.iesmm.stelarsound.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.iesmm.stelarsound.MainActivity;
import com.iesmm.stelarsound.Models.Song;
import com.iesmm.stelarsound.R;

public class PlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                int id = item.getItemId();
                if(id == R.id.nav_home){
                    item.setChecked(false);
                    return true;
                } else if (id == R.id.nav_search) {
                    item.setChecked(false);
                    return true;
                } else if (id == R.id.nav_play) {
                    item.setChecked(true);
                    return true;
                } else if (id == R.id.nav_playlists) {
                    item.setChecked(false);
                    return true;
                }
                return false;
            };
}
