<?php

namespace App\Http\Controllers;

use App\Models\Playlist;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

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
                    'cover_url' => $playlist->cover_url ? Storage::url($playlist->cover_url) : null,
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
            $playlistData['cover_url'] = $this->storeCover($request->file('cover'));
        }

        $playlist = Playlist::create($playlistData);

        return response()->json([
            'message' => 'Playlist created successfully',
            'playlist' => $this->formatPlaylistResponse($playlist)
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
                'audio_url' => $song->audio_url ? asset("storage/{$song->audio_url}") : null
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
    public function destroy(Playlist $playlist)
    {
        $this->authorize('delete', $playlist);
        
        // Delete cover if exists
        if ($playlist->cover_url) {
            Storage::delete($playlist->cover_url);
        }
        
        $playlist->delete();
        
        return response()->noContent();
    }

    /**
     * Add song to playlist
     */
    public function addSong(Playlist $playlist, Request $request)
    {
        $this->authorize('update', $playlist);
        
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
    }

    /**
     * Remove song from playlist
     */
    public function removeSong(Playlist $playlist, Request $request)
    {
        $this->authorize('update', $playlist);
        
        $request->validate([
            'song_id' => 'required|exists:songs,id'
        ]);

        $playlist->songs()->detach($request->song_id);

        return response()->json([
            'message' => 'Song removed from playlist',
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
