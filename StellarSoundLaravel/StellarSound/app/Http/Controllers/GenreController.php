<?php

namespace App\Http\Controllers;

use App\Models\Genre;
use Illuminate\Http\Request;

class GenreController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return Genre::all();
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255|unique:genres'
        ]);

        return Genre::create($validated);
    }

    /**
     * Display the specified resource.
     */
    public function show(Genre $genre)
    {
        return $genre->load('songs');
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Genre $genre)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255|unique:genres,name,'.$genre->id
        ]);

        $genre->update($validated);

        return $genre;
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Genre $genre)
    {
        $genre->delete();
        return response()->noContent();
    }

    /**
     * Get songs by genre
     */
    public function songs(Genre $genre)
    {
        return $genre->songs;
    }
}
