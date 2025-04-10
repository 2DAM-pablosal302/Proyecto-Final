<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Song extends Model
{
    use HasFactory;

    protected $fillable = [
        'title',
        'artist',
        'album',
        'id_genre',
        'cover_url',
        'audio_url'
    ];

    public function genre()
    {
        return $this->belongsTo(Genre::class, 'id_genre');
    }

    public function playlists()
    {
        return $this->belongsToMany(Playlist::class, 'playlist_song', 'song_id', 'playlist_id');
    }

    public function likedByUsers()
    {
        return $this->belongsToMany(User::class, 'likes', 'song_id', 'id_user');
    }
}
