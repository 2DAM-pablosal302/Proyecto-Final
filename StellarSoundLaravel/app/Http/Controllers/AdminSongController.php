<?php

namespace App\Http\Controllers;

use App\Models\Song;
use App\Models\Genre;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;


class AdminSongController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $songs = Song::with('genre')->get();
        return view('admin.songs', compact('songs'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        $genres = Genre::all();
        $edit = false;
        return view('admin.form-song', compact('genres', 'edit'));
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
            'cover' => 'required|image|max:2048',
            'audio' => 'required|mimes:mp3,wav|max:10240',
        ]);

        try {
            $coverPath = $request->file('cover')->store('covers', 'public');
        } catch (\Exception $e) {
            return back()->withErrors(['cover' => 'Error al subir la portada: ' . $e->getMessage()]);
        }

        try {
            $audioPath = $request->file('audio')->store('audio', 'public');
        } catch (\Exception $e) {
            return back()->withErrors(['audio' => 'Error al subir el audio: ' . $e->getMessage()]);
        }

        Song::create([
            'title' => $validated['title'],
            'artist' => $validated['artist'],
            'album' => $validated['album'],
            'id_genre' => $validated['id_genre'],
            'cover_url' => $coverPath,
            'audio_url' => $audioPath,
        ]);

        return redirect()->route('songs.index')->with('success', 'Canción creada con éxito.');
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Song $song)
    {
        $genres = Genre::all();
        $edit = true;
        return view('admin.form-song', compact('song', 'genres', 'edit'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Song $song)
    {
        $validated = $request->validate([
            'title' => 'required|string|max:255',
            'artist' => 'required|string|max:255',
            'album' => 'required|string|max:255',
            'id_genre' => 'required|exists:genres,id',
            'cover' => 'nullable|image|max:2048',
            'audio' => 'nullable|mimes:mp3,wav|max:10240',
        ]);

        if ($request->hasFile('cover')) {
            Storage::disk('public')->delete($song->cover_url);
            $song->cover_url = $request->file('cover')->store('covers', 'public');
        }

        if ($request->hasFile('audio')) {
            Storage::disk('public')->delete($song->audio_url);
            $song->audio_url = $request->file('audio')->store('audio', 'public');
        }

        $song->update([
            'title' => $validated['title'],
            'artist' => $validated['artist'],
            'album' => $validated['album'],
            'id_genre' => $validated['id_genre'],
            'cover_url' => $song->cover_url,
            'audio_url' => $song->audio_url,
        ]);

        return redirect()->route('songs.index')->with('success', 'Canción actualizada.');
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Song $song)
    {
        Storage::disk('public')->delete([$song->cover_url, $song->audio_url]);
        $song->delete();
        return redirect()->route('songs.index')->with('success', 'Canción eliminada.');
    }
}
