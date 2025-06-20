<?php

namespace App\Http\Controllers;

use App\Models\Song;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Intervention\Image\Facades\Image;
use App\Http\Resources\SongResource;

class SongController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $songs = Song::with(['genre', 'likedByUsers'])->get();


        return $songs->map(function ($song) {
            return [
                'id' => $song->id,
                'title' => $song->title,
                'artist' => $song->artist,
                'album' => $song->album,
                'id_genre' => $song->id_genre,
                'cover_url' => $song->cover_url ? asset("storage/{$song->cover_url}") : null,
                'audio_url' => $song->audio_url ? asset("storage/{$song->audio_url}") : null,
                'genre' => $song->genre,
                'is_liked' => $song->is_liked,
                'liked_by_users' => $song->likedByUsers
            ];
        });
    }

    public function search(Request $request)
    {


        $search = $request->query('q');
        if (empty($search)) {
            return response()->json(['message' => 'Debe proporcionar un término de búsqueda'], 400);
        }

        $songs = Song::with(['genre', 'likedByUsers'])
            ->where(function ($q) use ($search) {
                $q->where('title', 'like', "%{$search}%")
                    ->orWhere('artist', 'like', "%{$search}%")
                    ->orWhere('album', 'like', "%{$search}%");
            })
            ->get();

        return $songs->map(function ($song) {
            return [
                'id' => $song->id,
                'title' => $song->title,
                'artist' => $song->artist,
                'album' => $song->album,
                'id_genre' => $song->id_genre,
                'cover_url' => $song->cover_url ? asset("storage/{$song->cover_url}") : null,
                'audio_url' => $song->audio_url ? asset("storage/{$song->audio_url}") : null,
                'genre' => $song->genre,
                'is_liked' => $song->is_liked,
                'liked_by_users' => $song->likedByUsers
            ];
        });
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
            'cover' => 'required|image|mimes:jpeg,png,jpg|max:2048',
            'audio' => 'required|file|mimetypes:audio/mpeg,audio/wav,audio/mp3|max:51200'
        ]);


        $coverPath = $this->storeCover($request->file('cover'));


        $audioPath = $this->storeAudio($request->file('audio'));

        $songData = [
            'title' => $validated['title'],
            'artist' => $validated['artist'],
            'album' => $validated['album'],
            'id_genre' => $validated['id_genre'],
            'cover_url' => $coverPath,
            'audio_url' => $audioPath
        ];

        return Song::create($songData);
    }

    /**
     * Display the specified resource.
     */
    public function show(Song $song)
    {
        $song->load(['genre', 'playlists', 'likedByUsers']);

        return [
            'id' => $song->id,
            'title' => $song->title,
            'artist' => $song->artist,
            'album' => $song->album,
            'id_genre' => $song->id_genre,
            'cover_url' => $song->cover_url ? asset("storage/{$song->cover_url}") : null,
            'audio_url' => $song->audio_url ? asset("storage/{$song->audio_url}") : null,
            'genre' => $song->genre,
            'playlists' => $song->playlists,
            'is_liked' => $song->is_liked,
            'liked_by_users' => $song->likedByUsers
        ];
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
            'cover' => 'sometimes|image|mimes:jpeg,png,jpg|max:2048',
            'audio' => 'sometimes|file|mimetypes:audio/mpeg,audio/wav,audio/mp3|max:51200'
        ]);


        if ($request->hasFile('cover')) {

            if ($song->cover_url && Storage::exists($song->cover_url)) {
                Storage::delete($song->cover_url);
            }
            $validated['cover_url'] = $this->storeCover($request->file('cover'));
        }


        if ($request->hasFile('audio')) {

            if ($song->audio_url && Storage::exists($song->audio_url)) {
                Storage::delete($song->audio_url);
            }
            $validated['audio_url'] = $this->storeAudio($request->file('audio'));
        }

        $song->update($validated);

        return $song;
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Song $song)
    {

        if ($song->cover_url && Storage::exists($song->cover_url)) {
            Storage::delete($song->cover_url);
        }

        if ($song->audio_url && Storage::exists($song->audio_url)) {
            Storage::delete($song->audio_url);
        }

        $song->delete();
        return response()->noContent();
    }

    /**
     * Like a song
     */
    public function like($songId, Request $request)
    {
        $song = Song::findOrFail($songId);

        if (!$request->user()->likes()->where('song_id', $song->id)->exists()) {
            $request->user()->likes()->attach($song->id);
        }

        return response()->json(['message' => 'Song liked']);
    }


    /**
     * Unlike a song
     */
    public function unlike($songId, Request $request)
    {
        $user = $request->user();
        $song = Song::findOrFail($songId);
        \Log::info('Usuario:', ['id' => $user->id]);
        \Log::info('Canción:', ['id' => $song->id]);

        if ($user->likes()->where('song_id', $song->id)->exists()) {
            $user->likes()->detach($song->id);
            \Log::info('Like eliminado');
        } else {
            \Log::info('No existe like para eliminar');
        }

        return response()->json(['message' => 'Song unliked']);
    }


    /**
     * Almacena la imagen de portada y devuelve la ruta relativa
     */
    private function storeCover($file)
    {

        $filename = 'cover_' . time() . '_' . uniqid() . '.' . $file->getClientOriginalExtension();


        $image = Image::make($file)->resize(500, 500, function ($constraint) {
            $constraint->aspectRatio();
            $constraint->upsize();
        })->encode();

        Storage::disk('public')->put("covers/{$filename}", $image);

        return "covers/{$filename}";
    }

    /**
     * Almacena el archivo de audio y devuelve la ruta relativa
     */
    private function storeAudio($file)
    {

        $filename = 'audio_' . time() . '_' . uniqid() . '.' . $file->getClientOriginalExtension();


        $path = $file->storeAs('audio', $filename, 'public');

        return $path;
    }
}
