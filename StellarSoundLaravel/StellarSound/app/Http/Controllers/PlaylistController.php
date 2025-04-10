<?php

namespace App\Http\Controllers;

use App\Models\Playlist;
use Illuminate\Http\Request;

class PlaylistController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        return $request->user()->playlists;
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255'
        ]);

        $validated['id_user'] = $request->user()->id;

        return Playlist::create($validated);
    }

    /**
     * Display the specified resource.
     */
    public function show(Playlist $playlist)
    {
        $this->authorize('view', $playlist);
        return $playlist->load(['songs', 'user']);
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Playlist $playlist)
    {
        $this->authorize('update', $playlist);
        
        $validated = $request->validate([
            'name' => 'sometimes|string|max:255'
        ]);

        $playlist->update($validated);

        return $playlist;
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Playlist $playlist)
    {
        $this->authorize('delete', $playlist);
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

        $playlist->songs()->attach($request->song_id);

        return response()->json(['message' => 'Song added to playlist']);
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

        return response()->json(['message' => 'Song removed from playlist']);
    }
}
