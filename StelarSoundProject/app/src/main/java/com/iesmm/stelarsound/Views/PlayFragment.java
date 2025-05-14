package com.iesmm.stelarsound.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.iesmm.stelarsound.MainActivity;
import com.iesmm.stelarsound.R;

public class PlayFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null && mainActivity.mediaPlayer.isPlaying()) {
            // Configurar la UI con la canción actual
        }

        return view;
    }
}