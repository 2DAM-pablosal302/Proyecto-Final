package com.iesmm.stelarsound.Services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.iesmm.stelarsound.Models.Song;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SongService {

    private static final String URL_API = "http://172.22.254.149/api/songs";

    public interface VolleyCallback {
        void onSuccess(ArrayList<Song> lista);
        void onError(String mensaje);
    }

    public static void obtenerCanciones(Context context,  String token, VolleyCallback callback) {
        String url = "http://172.22.254.149:8000/api/songs";


        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        ArrayList<Song> canciones = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Song song = new Song();
                            song.setId(obj.getInt("id"));
                            song.setTitle(obj.getString("title"));
                            song.setArtist(obj.getString("artist"));
                            song.setAlbum(obj.getString("album"));
                            song.setCover(obj.getString("cover_url"));
                            song.setAudio(obj.getString("audio_url"));
                            canciones.add(song);
                        }
                        callback.onSuccess(canciones);
                    } catch (JSONException e) {
                        callback.onError("Error al procesar datos");
                        Log.e("Volley", "JSON Error: " + e.getMessage());
                    }
                },
                error -> {
                    callback.onError("Error en la solicitud: " + error.toString());
                    Log.e("Volley", "Request Error: " + error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }

    public static void searchSongs(Context context, String token, String query, VolleyCallback callback) throws UnsupportedEncodingException {
        String url = "http://172.22.254.149:8000/api/songs/search?q=" + URLEncoder.encode(query, "UTF-8");

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try{
                        ArrayList<Song> canciones = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Song song = new Song();
                            song.setTitle(obj.getString("title"));
                            song.setArtist(obj.getString("artist"));
                            song.setAlbum(obj.getString("album"));
                            song.setCover(obj.getString("cover_url"));
                            song.setAudio(obj.getString("audio_url"));
                            canciones.add(song);
                        }
                        callback.onSuccess(canciones);
                    }catch (JSONException e){
                        callback.onError("Error al procesar datos");
                        Log.e("Volley", "JSON Error: " + e.getMessage());
                    }
                },
                error -> {
                    callback.onError("Error en la solicitud: " + error.toString());
                    Log.e("Volley", "Request Error: " + error.toString());
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }
}
