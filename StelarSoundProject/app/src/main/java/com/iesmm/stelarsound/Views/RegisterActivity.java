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

import com.iesmm.stelarsound.Models.User;
import com.iesmm.stelarsound.R;
import com.iesmm.stelarsound.Services.ApiClient;
import com.iesmm.stelarsound.Services.ApiService;
import com.iesmm.stelarsound.Services.RegisterResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerBtn;
    private ApiService apiService;
    private CardView cardView;
    private float initialY;
    private float initialTouchY;
    private boolean isCardUp = false;
    private TextView loginTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.name_txt);
        emailEditText = findViewById(R.id.email_txt);
        passwordEditText = findViewById(R.id.passwd);
        confirmPasswordEditText = findViewById(R.id.confirm_passwd);
        registerBtn = findViewById(R.id.register_button);
        View dragHandle = findViewById(R.id.drag_handle);
        cardView = findViewById(R.id.card_view);
        loginTxt = findViewById(R.id.loginTxt);

        apiService = ApiClient.getClient().create(ApiService.class);

        TextView title = findViewById(R.id.stellarTitle);
        title.post(() -> {
            Shader shader = new LinearGradient(
                    0, 0, title.getWidth(), 0,
                    new int[]{Color.parseColor("#F3E5F5"), Color.parseColor("#4A148C")},
                    null,
                    Shader.TileMode.CLAMP // evita el efecto espejo
            );
            title.getPaint().setShader(shader);
            title.animate().alpha(1f).translationY(0).setDuration(800).start();
            title.invalidate();
        });

        loginTxt.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });


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


                        if (newY < 0) newY = 0;

                        cardView.setTranslationY(newY);
                        return true;
                    case MotionEvent.ACTION_UP:

                        float currentY = cardView.getTranslationY();
                        float cardHeight = cardView.getHeight();

                        if (currentY < cardHeight / 2) {

                            animateCardUp();
                            isCardUp = true;
                        } else {

                            animateCardDown();
                            isCardUp = false;
                        }
                        return true;
                }
                return false;
            }
        });


        new Handler().postDelayed(() -> {
            animateCardUp();
            isCardUp = true;
        }, 800);

        registerBtn.setOnClickListener(v -> validateRegister());
    }

    private void validateRegister() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String pass = passwordEditText.getText().toString().trim();
        String confirm = confirmPasswordEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> registerData = new HashMap<>();
        registerData.put("name", name);
        registerData.put("email", email);
        registerData.put("password", pass);
        registerData.put("password_confirmation", confirm);

        Call<RegisterResponse> call = apiService.register(registerData);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registro exitoso. Ahora puedes iniciar sesión.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error en el registro", Toast.LENGTH_LONG).show();
                    Log.e("REGISTER_FAIL", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("REGISTER_ERROR", t.getMessage());
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
