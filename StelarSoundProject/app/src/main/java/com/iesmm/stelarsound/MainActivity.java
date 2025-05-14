package com.iesmm.stelarsound;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.iesmm.stelarsound.Views.HomeFragment;
import com.iesmm.stelarsound.Views.PlayFragment;
import com.iesmm.stelarsound.Views.PlaylistFragment;
import com.iesmm.stelarsound.Views.SearchFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    public MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Cargar fragment inicial si es la primera vez
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int id = item.getItemId();
                if(id == R.id.nav_home){
                    selectedFragment = new HomeFragment();
                } else if (id == R.id.nav_search) {
                    selectedFragment = new SearchFragment();
                } else if (id == R.id.nav_play) {
                    if (mediaPlayer.isPlaying()) {
                        selectedFragment = new PlayFragment();
                    } else {
                        Toast.makeText(this, "No hay ninguna canción reproduciéndose", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else if (id == R.id.nav_playlists) {
                    selectedFragment = new PlaylistFragment();
                    return true;
                }


                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }

                return true;
            };

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
