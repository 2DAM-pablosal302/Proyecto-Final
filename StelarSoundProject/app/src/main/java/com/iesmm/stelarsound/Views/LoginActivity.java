package com.iesmm.stelarsound.Views;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.iesmm.stelarsound.Models.User;
import com.iesmm.stelarsound.R;
import com.iesmm.stelarsound.Services.ApiClient;
import com.iesmm.stelarsound.Services.ApiService;
import com.iesmm.stelarsound.Services.LoginResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private CardView cardView;
    private float initialY;
    private float initialTouchY;
    private boolean isCardUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cardView = findViewById(R.id.card_view);
        View dragHandle = findViewById(R.id.drag_handle);

        // Configuración del arrastre
        dragHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialY = cardView.getTranslationY();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float deltaY = event.getRawY() - initialTouchY;
                        float newY = initialY + deltaY;

                        // Limitar el movimiento hacia arriba
                        if (newY < 0) newY = 0;

                        cardView.setTranslationY(newY);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Determinar si la tarjeta debe quedarse arriba o abajo
                        float currentY = cardView.getTranslationY();
                        float cardHeight = cardView.getHeight();

                        if (currentY < cardHeight / 2) {
                            // Animación para subir completamente
                            animateCardUp();
                            isCardUp = true;
                        } else {
                            // Animación para bajar completamente
                            animateCardDown();
                            isCardUp = false;
                        }
                        return true;
                }
                return false;
            }
        });

        // Mostrar la tarjeta después de un breve retraso
        new Handler().postDelayed(() -> {
            animateCardUp();
            isCardUp = true;
        }, 10000);


        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "maria@musicapp.com");
        loginData.put("password", "password");

        Call<LoginResponse> call = apiService.login(loginData);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    String token = response.body().token;
                    User user = response.body().data;
                    Log.d("TOKEN", token);
                } else {
                    Log.e("LOGIN_FAIL", "Credenciales incorrectas");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });

    }

    private void animateCardUp() {
        cardView.animate()
                .translationY(0)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void animateCardDown() {
        cardView.animate()
                .translationY(cardView.getHeight()-90)
                .setDuration(300)
                .setInterpolator(new AccelerateInterpolator())
                .start();
    }

}



