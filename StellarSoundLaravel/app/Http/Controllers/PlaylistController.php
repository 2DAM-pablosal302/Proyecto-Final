<?php

namespace App\Http\Controllers;

use App\Models\Playlist;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Storage;

class PlaylistController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $playlists = $request->user()->playlists()
            ->withCount('songs')
            ->with(['user:id,name'])
            ->get();

        return response()->json([
            'playlists' => $playlists->map(function($playlist){
                return [
                    'id' => $playlist->id,
                    'name' => $playlist->name,
                    'creator' => $playlist->user->name,
                    'song_count' => $playlist->songs_count,
                    'cover_url' => $playlist->cover_url ? asset("storage/{$playlist->cover_url}") : null,
                    'created_at' => $playlist->created_at->diffForHumans()
                ];
            })
        ]);
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
{
    $validated = $request->validate([
        'name' => 'required|string|max:255',
        'cover' => 'sometimes|image|mimes:jpeg,png,jpg|max:2048'
    ]);

    $playlistData = [
        'id_user' => $request->user()->id,
        'name' => $validated['name']
    ];

    if ($request->hasFile('cover')) {
        $playlistData['cover_url'] = $request->file('cover')->store('playlistcovers', 'public');
    }

    $playlist = Playlist::create($playlistData);

    return response()->json([
        'message' => 'Playlist created successfully',
        'playlist' => $playlist
    ], 201);
}


    /**
     * Display the specified resource.
     */
    public function show($id)
    {
        $playlist = Playlist::with(['songs' => function($query) {
            $query->select('songs.id', 'title', 'artist', 'album', 'cover_url', 'audio_url');
        }])->findOrFail($id);

        // Verificar que el usuario es el dueño de la playlist
        if ($playlist->id_user != Auth::id()) {
            return response()->json([
                'status' => 'error',
                'message' => 'No tienes permiso para ver esta playlist'
            ], 403);
        }

        return response()->json([
            'status' => 'success',
            'playlist' => [
            'id' => $playlist->id,
            'name' => $playlist->name,
            'creator' => optional($playlist->user)->name ?? 'Usuario desconocido',
            'cover_url' => $playlist->cover_url ? asset("storage/{$playlist->cover_url}") : null,
            'song_count' => $playlist->songs->count(),
            'created_at' => $playlist->created_at->diffForHumans()
        ],
        'songs' => $playlist->songs->map(function($song) {
            return [
                'id' => $song->id,
                'title' => $song->title,
                'artist' => $song->artist,
                'album' => $song->album,
                'cover_url' => $song->cover_url ? asset("storage/{$song->cover_url}") : null,
                'audio_url' => $song->audio_url ? asset("storage/{$song->audio_url}") : null,
                'is_liked' => $song->is_liked,
                'liked_by_user' => $song->likedByUsers
            ];
        })
        ]);
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Playlist $playlist)
    {
        $this->authorize('update', $playlist);
        
        $validated = $request->validate([
            'name' => 'sometimes|string|max:255',
            'cover' => 'sometimes|image|mimes:jpeg,png,jpg|max:2048'
        ]);

        if ($request->has('name')) {
            $playlist->name = $validated['name'];
        }

        if ($request->hasFile('cover')) {
            // Eliminar cover anterior si existe
            if ($playlist->cover_url) {
                Storage::delete($playlist->cover_url);
            }
            $playlist->cover_url = $this->storeCover($request->file('cover'));
        }

        $playlist->save();

        return response()->json([
            'message' => 'Playlist updated successfully',
            'playlist' => $this->formatPlaylistResponse($playlist)
        ]);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy($id)
{
    $playlist = Playlist::findOrFail($id);
    
    // Verificar que el usuario es el dueño
    if (!$playlist) {
        return response()->json(['message' => 'Playlist no encontrada'], 404);
    }

    // Eliminar la imagen de portada si existe
    if ($playlist->cover_url) {
        $coverPath = str_replace('/storage', 'public', parse_url($playlist->cover_url, PHP_URL_PATH));
        Storage::delete($coverPath);
    }

    // Eliminar las relaciones con canciones
    $playlist->songs()->detach();
    
    // Eliminar la playlist
    $playlist->delete();

    return response()->json(['message' => 'Playlist eliminada correctamente']);
}

    /**
     * Add song to playlist
     */
    public function addSong($playlistId, Request $request)
{
    // Comprobar que el usuario logueado es dueño de la playlist
    //if ($playlist->user_id !== auth()->id()) {
    //    abort(403, 'No tienes permiso para modificar esta playlist.');
    //}
    \Log::debug('Playlist ID recibido: ' . $playlistId);
    
    $playlist = Playlist::find($playlistId);
    if (!$playlist) {
        return response()->json(['message' => 'Playlist no encontrada'], 404);
    }
    
    \Log::debug('Playlist encontrada: ' . $playlist->id);


    try {
    $request->validate([
        'song_id' => 'required|exists:songs,id'
    ]);

    if (!$playlist->songs()->where('song_id', $request->song_id)->exists()) {
        $playlist->songs()->attach($request->song_id);
    }

    return response()->json([
        'message' => 'Song added to playlist',
        'song_count' => $playlist->songs()->count()
    ]);
} catch (\Exception $e) {
    \Log::error('Error en addSong: '.$e->getMessage());
    return response()->json(['error' => $e->getMessage()], 500);
}

    
}


    /**
     * Remove song from playlist
     */
public function removeSong($playlistId, $songId)
{
    $playlist = Playlist::find($playlistId);
    
    if (!$playlist) {
        return response()->json(['message' => 'Playlist no encontrada'], 404);
    }

    // Validación manual (ya no usamos $request->validate)
    if (!is_numeric($songId)) {
        return response()->json(['message' => 'El ID de la canción debe ser numérico'], 422);
    }

    $playlist->songs()->detach($songId);

    return response()->json([
        'message' => 'Canción eliminada de la playlist',
        'song_count' => $playlist->songs()->count()
    ]);
}

    /**
     * Look for a playlist by name
     */

    public function search(Request $request)
{
    $request->validate([
        'query' => 'required|string|min:2'
    ]);

    $query = $request->input('query');

    $playlists = $request->user()->playlists()
        ->where('name', 'like', '%' . $query . '%')
        ->withCount('songs')
        ->with(['user:id,name'])
        ->get();

    return response()->json([
        'playlists' => $playlists->map(function($playlist) {
            return $this->formatPlaylistResponse($playlist);
        })
    ]);
}


    /**
     * Store cover
     */
    private function storeCover($file)
    {
        $filename = 'playlist_covers/'.Str::uuid().'.'.$file->getClientOriginalExtension();
        $file->storeAs('public', $filename);
        return $filename;
    }

    /**
     * Reboot playlist output
     */
    private function formatPlaylistResponse($playlist)
{
    return [
        'id' => $playlist->id,
        'name' => $playlist->name,
        'creator' => $playlist->user?->name,
        'song_count' => $playlist->songs_count ?? ($playlist->songs()?->count() ?? 0),
        'cover_url' => $playlist->cover_url ? Storage::url($playlist->cover_url) : null,
        'created_at' => optional($playlist->created_at)->diffForHumans(),
    ];
}

}
