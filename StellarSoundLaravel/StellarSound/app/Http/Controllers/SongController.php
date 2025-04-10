<?php

namespace App\Http\Controllers;

use App\Models\Song;
use Illuminate\Http\Request;

class SongController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return Song::with(['genre', 'likedByUsers'])->get();
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'title' => 'required|string|max:255',
            'artist' => 'required|string|max:255',
            'album' => 'required|string|max:255',
            'id_genre' => 'required|exists:genres,id',
            'cover_url' => 'required|url',
            'audio_url' => 'required|url'
        ]);

        return Song::create($validated);
    }

    /**
     * Display the specified resource.
     */
    public function show(Song $song)
    {
        return $song->load(['genre', 'playlists', 'likedByUsers']);
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Song $song)
    {
        $validated = $request->validate([
            'title' => 'sometimes|string|max:255',
            'artist' => 'sometimes|string|max:255',
            'album' => 'sometimes|string|max:255',
            'id_genre' => 'sometimes|exists:genres,id',
            'cover_url' => 'sometimes|url',
            'audio_url' => 'sometimes|url'
        ]);

        $song->update($validated);

        return $song;
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Song $song)
    {
        $song->delete();
        return response()->noContent();
    }

    /**
     * Like a song
     */
    public function like(Song $song, Request $request)
    {
        $request->user()->likes()->attach($song);
        return response()->json(['message' => 'Song liked']);
    }

    /**
     * Unlike a song
     */
    public function unlike(Song $song, Request $request)
    {
        $request->user()->likes()->detach($song);
        return response()->json(['message' => 'Song unliked']);
    }
}
