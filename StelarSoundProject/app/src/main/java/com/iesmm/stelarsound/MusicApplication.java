package com.iesmm.stelarsound;

import android.app.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MusicApplication extends Application {
    private MusicRepository musicRepository;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Inicializar Room
        AppDatabase database = AppDatabase.getInstance(this);
        Executor diskExecutor = Executors.newSingleThreadExecutor();
        LocalDataSource localDataSource = new LocalDataSource(database.songDao(), diskExecutor);
        
        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://tu-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build())
            .build();
        
        MusicApiService apiService = retrofit.create(MusicApiService.class);
        RemoteDataSource remoteDataSource = new RemoteDataSource(apiService);
        
        // Crear el repositorio
        musicRepository = new MusicRepository(localDataSource, remoteDataSource);
    }
    
    public MusicRepository getMusicRepository() {
        return musicRepository;
    }
}