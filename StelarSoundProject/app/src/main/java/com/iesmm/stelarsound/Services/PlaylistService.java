package com.iesmm.stelarsound.Services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iesmm.stelarsound.Models.Playlist;
import com.iesmm.stelarsound.Models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistService {
    private static final String BASE_URL = "http://172.22.254.149:8000/api/";

    public interface PlaylistCallback {
        void onSuccess(List<Playlist> playlists);
        void onError(String message);
    }

    public interface PlaylistDetailCallback {
        void onSuccess(Playlist playlist);
        void onError(String message);
    }

    public interface SongListCallback {
        void onSuccess(List<Song> songs);
        void onError(String message);
    }

    public static void getUserPlaylists(Context context, String token, PlaylistCallback callback) {
        String url = BASE_URL + "playlists";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray playlistsArray = response.getJSONArray("playlists");
                        List<Playlist> playlists = new ArrayList<>();

                        for (int i = 0; i < playlistsArray.length(); i++) {
                            JSONObject obj = playlistsArray.getJSONObject(i);
                            Playlist playlist = new Playlist(
                                    obj.getInt("id"),
                                    obj.getString("name"),
                                    obj.getString("creator"),
                                    obj.optString("cover_url", null),
                                    obj.getInt("song_count")
                            );
                            playlists.add(playlist);
                        }

                        callback.onSuccess(playlists);
                    } catch (JSONException e) {
                        callback.onError("Error al procesar las playlists");
                        Log.e("PlaylistService", "JSON Error: " + e.getMessage());
                    }
                },
                error -> {
                    callback.onError("Error de conexión: " + error.getMessage());
                    Log.e("PlaylistService", "Request Error: " + error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public static void getPlaylistDetail(Context context, String token, int playlistId, PlaylistDetailCallback callback) {
        String url = BASE_URL + "playlists/" + playlistId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject playlistObj = response.getJSONObject("playlist");
                        Playlist playlist = new Playlist(
                                playlistObj.getInt("id"),
                                playlistObj.getString("name"),
                                playlistObj.getString("creator"),
                                playlistObj.optString("cover_url", null),
                                playlistObj.getInt("song_count")
                        );

                        JSONArray songsArray = response.getJSONArray("songs");
                        List<Song> songs = new ArrayList<>();

                        for (int i = 0; i < songsArray.length(); i++) {
                            JSONObject songObj = songsArray.getJSONObject(i);
                            Song song = new Song();
                            song.setTitle(songObj.getString("title"));
                            song.setArtist(songObj.getString("artist"));
                            song.setAlbum(songObj.getString("album"));
                            song.setCover(songObj.optString("cover_url", null));
                            song.setAudio(songObj.optString("audio_url", null));
                            songs.add(song);
                        }

                        playlist.setSongs(songs);
                        callback.onSuccess(playlist);
                    } catch (JSONException e) {
                        callback.onError("Error al procesar los datos de la playlist");
                        Log.e("PlaylistService", "JSON Error: " + e.getMessage());
                    }
                },
                error -> {
                    callback.onError("Error de conexión: " + error.getMessage());
                    Log.e("PlaylistService", "Request Error: " + error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public static void addSongToPlaylist(Context context, String token, int playlistId, int songId, SongListCallback callback) {
        String url = BASE_URL + "playlists/" + playlistId + "/songs";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("song_id", songId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, requestBody,
                response -> {
                    // Actualizar la lista de canciones después de añadir
                    getPlaylistSongs(context, token, playlistId, callback);
                },
                error -> {
                    callback.onError("Error al añadir canción: " + error.getMessage());
                    Log.e("PlaylistService", "Add Song Error: " + error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public static void removeSongFromPlaylist(Context context, String token, int playlistId, int songId, SongListCallback callback) {
        String url = BASE_URL + "playlists/" + playlistId + "/songs";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("song_id", songId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE, url, requestBody,
                response -> {
                    // Actualizar la lista de canciones después de eliminar
                    getPlaylistSongs(context, token, playlistId, callback);
                },
                error -> {
                    callback.onError("Error al eliminar canción: " + error.getMessage());
                    Log.e("PlaylistService", "Remove Song Error: " + error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public static void getPlaylistSongs(Context context, String token, int playlistId, SongListCallback callback) {
        String url = BASE_URL + "playlists/" + playlistId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray songsArray = response.getJSONArray("songs");
                        List<Song> songs = new ArrayList<>();

                        for (int i = 0; i < songsArray.length(); i++) {
                            JSONObject songObj = songsArray.getJSONObject(i);
                            Song song = new Song();
                            song.setTitle(songObj.getString("title"));
                            song.setArtist(songObj.getString("artist"));
                            song.setAlbum(songObj.getString("album"));
                            song.setCover(songObj.optString("cover_url", null));
                            song.setAudio(songObj.optString("audio_url", null));
                            songs.add(song);
                        }

                        callback.onSuccess(songs);
                    } catch (JSONException e) {
                        callback.onError("Error al procesar las canciones");
                        Log.e("PlaylistService", "JSON Error: " + e.getMessage());
                    }
                },
                error -> {
                    callback.onError("Error de conexión: " + error.getMessage());
                    Log.e("PlaylistService", "Request Error: " + error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public static void searchPlaylists(Context context, String token, String query, PlaylistCallback callback) {
        String url = BASE_URL + "playlists/search?query=" + query;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray playlistsArray = response.getJSONArray("playlists");
                        List<Playlist> playlists = new ArrayList<>();

                        for (int i = 0; i < playlistsArray.length(); i++) {
                            JSONObject obj = playlistsArray.getJSONObject(i);
                            Playlist playlist = new Playlist(
                                    obj.getInt("id"),
                                    obj.getString("name"),
                                    obj.getString("creator"),
                                    obj.optString("cover_url", null),
                                    obj.getInt("song_count")
                            );
                            playlists.add(playlist);
                        }

                        callback.onSuccess(playlists);
                    } catch (JSONException e) {
                        callback.onError("Error al procesar los resultados");
                        Log.e("PlaylistService", "JSON Error: " + e.getMessage());
                    }
                },
                error -> {
                    callback.onError("Error de búsqueda: " + error.getMessage());
                    Log.e("PlaylistService", "Search Error: " + error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
}