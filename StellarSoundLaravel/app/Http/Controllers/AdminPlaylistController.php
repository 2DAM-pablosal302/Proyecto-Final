<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Playlist;
use App\Models\User;

class AdminPlaylistController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $playlists = Playlist::all();
        return view('admin.playlists', compact('playlists'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        $usuarios = User::orderBy('name')->get();
        return view('admin.create-playlist', compact('usuarios'));
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $request->validate([
            'name' => 'required|string|max:255',
            'id_user' => 'required|exists:users,id',
            'cover_url' => 'required|image|max:2048',
        ]);

        $path = $request->file('cover_url')->store('playlistcovers', 'public');

        Playlist::create([
            'id_user' => $request->id_user,
            'name' => $request->name,
            'cover_url' => 'playlistcovers/' . basename($path),
        ]);

        return redirect()->route('playlists.index')->with('success', 'Playlist creada correctamente.');
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
    public function edit(Playlist $playlist)
    {
        $users = User::all();
        $songs = \App\Models\Song::all();
        $selectedSongs = $playlist->songs->pluck('id')->toArray();

        return view('admin.edit-playlist', compact('playlist', 'users', 'songs', 'selectedSongs'));
    }



    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Playlist $playlist)
    {
        $request->validate([
            'name' => 'required|string|max:255',
            'id_user' => 'required|exists:users,id',
            'cover_url' => 'nullable|image|mimes:jpg,jpeg,png,webp|max:2048',
            'songs' => 'nullable|array',
            'songs.*' => 'exists:songs,id',
        ]);

        $data = $request->only('name', 'id_user');

        if ($request->hasFile('cover_url')) {
            $path = $request->file('cover_url')->store('playlistcovers', 'public');
            $data['cover_url'] = '/playlistcovers/' . basename($path);
        }

        $playlist->update($data);


        $playlist->songs()->sync($request->input('songs', []));

        return redirect()->route('playlists.index')->with('success', 'Playlist actualizada correctamente.');
    }



    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Playlist $playlist)
    {

        if ($playlist->cover_url && \Storage::disk('public')->exists('playlistcovers/' . basename($playlist->cover_url))) {
            \Storage::disk('public')->delete('playlistcovers/' . basename($playlist->cover_url));
        }


        $playlist->songs()->detach();


        $playlist->delete();

        return redirect()->route('playlists.index')->with('success', 'Playlist eliminada correctamente.');
    }
}
