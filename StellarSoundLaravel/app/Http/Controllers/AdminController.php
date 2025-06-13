<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Song;
use App\Models\Genre;

class AdminController extends Controller
{
    //Vista simple a modo de dashboard
    public function index()
    {
        $topSongs = Song::withCount('likes')
        ->orderByDesc('likes_count')
        ->take(5)
        ->get();

        // Conteo de canciones por género
        $genres = Genre::withCount('songs')->get();

        return view('admin.dashboard', compact('topSongs', 'genres'));
    }
}
