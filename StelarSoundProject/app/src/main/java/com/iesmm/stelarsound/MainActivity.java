package com.iesmm.stelarsound;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.iesmm.stelarsound.ViewModels.SongViewModel;
import com.iesmm.stelarsound.Views.HomeFragment;
import com.iesmm.stelarsound.Views.PlayFragment;
import com.iesmm.stelarsound.Views.PlaylistFragment;
import com.iesmm.stelarsound.Views.SearchFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    public MediaPlayer mediaPlayer;
    private Handler progressHandler = new Handler();
    private SongViewModel songViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        startProgressUpdates();

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
                }


                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }

                return true;
            };

    private void startProgressUpdates() {
        progressHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    songViewModel.setCurrentPosition(currentPosition);
                }
                progressHandler.postDelayed(this, 200); // Actualizar cada 200ms
            }
        }, 200);
    }

    public void pauseProgressUpdates() {
        progressHandler.removeCallbacksAndMessages(null);
    }

    public void resumeProgressUpdates() {
        startProgressUpdates();
    }

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
        progressHandler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    public BottomNavigationView getBottomNav() {
        return bottomNav;
    }


}
