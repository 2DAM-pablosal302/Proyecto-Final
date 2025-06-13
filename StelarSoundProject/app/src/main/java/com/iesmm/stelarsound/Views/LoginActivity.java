package com.iesmm.stelarsound.Views;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.iesmm.stelarsound.MainActivity;
import com.iesmm.stelarsound.Models.Token;
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
    private EditText email ;
    private EditText passwd ;
    private Button loginbtn;
    private ApiService apiService;
    private TextView registerTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cardView = findViewById(R.id.card_view);
        View dragHandle = findViewById(R.id.drag_handle);
        email= findViewById(R.id.email_txt);
        passwd = findViewById(R.id.passwd);
        registerTxt = findViewById(R.id.registerTxt);

        TextView title = findViewById(R.id.stellarTitle);
        title.post(() -> {
            Shader shader = new LinearGradient(
                    0, 0, title.getWidth(), 0, // horizontal: desde el borde izquierdo al derecho
                    new int[]{Color.parseColor("#F3E5F5"), Color.parseColor("#4A148C")},
                    null,
                    Shader.TileMode.CLAMP // evita el efecto espejo
            );
            title.getPaint().setShader(shader);
            title.animate().alpha(1f).translationY(0).setDuration(800).start();
            title.invalidate();
        });


        registerTxt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

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
        }, 800);


        apiService = ApiClient.getClient().create(ApiService.class);

        loginbtn = findViewById(R.id.login_button);
        loginbtn.setOnClickListener(v -> validateLogin());

    }

    private void validateLogin(){
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", email.getText().toString());
        loginData.put("password", passwd.getText().toString());

        if (email.toString().isEmpty() || passwd.toString().isEmpty()) {
            Toast.makeText(this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<LoginResponse> call = apiService.login(loginData);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    String token_txt = response.body().token;
                    Token token = new Token();
                    token.setBody(token_txt);
                    User user = response.body().data;
                    Log.d("TOKEN", token_txt);
                    goToMain(user, token);
                } else {
                    Log.e("LOGIN_FAIL", "Credenciales incorrectas");
                    Toast.makeText(getApplicationContext(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    private void goToMain(User user, Token token){
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("usuario", user);
        bundle.putParcelable("token", token);
        intent.putExtras(bundle);
        startActivity(intent);
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



