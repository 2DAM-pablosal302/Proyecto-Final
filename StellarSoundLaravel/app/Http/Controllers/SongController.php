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

        // Transformamos las URLs para que sean completas
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
                'liked_by_users' => $song->likedByUsers
            ];
        });
    }

    public function search(Request $request)
{
    $user = auth()->user();
    if (!$user) {
        return response()->json(['message' => 'Unauthorized'], 401);
    }

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

    return response()->json($songs);
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

        // Procesar la imagen de portada
        $coverPath = $this->storeCover($request->file('cover'));

        // Procesar el archivo de audio
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

        // Actualizar cover si se proporciona
        if ($request->hasFile('cover')) {
            // Eliminar el cover anterior si existe
            if ($song->cover_url && Storage::exists($song->cover_url)) {
                Storage::delete($song->cover_url);
            }
            $validated['cover_url'] = $this->storeCover($request->file('cover'));
        }

        // Actualizar audio si se proporciona
        if ($request->hasFile('audio')) {
            // Eliminar el audio anterior si existe
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
        // Eliminar archivos asociados
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

    /**
     * Almacena la imagen de portada y devuelve la ruta relativa
     */
    private function storeCover($file)
    {
        // Generar nombre único para el archivo
        $filename = 'cover_' . time() . '_' . uniqid() . '.' . $file->getClientOriginalExtension();

        // Redimensionar y guardar la imagen
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
        // Generar nombre único para el archivo
        $filename = 'audio_' . time() . '_' . uniqid() . '.' . $file->getClientOriginalExtension();

        // Guardar el archivo
        $path = $file->storeAs('audio', $filename, 'public');

        return $path;
    }
}
