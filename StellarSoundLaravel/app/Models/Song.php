<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\Auth;

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

    protected $appends = ['is_liked'];

    public function genre()
    {
        return $this->belongsTo(Genre::class, 'id_genre');
    }

    public function playlists()
    {
        return $this->belongsToMany(Playlist::class, 'playlist_song', 'song_id', 'playlist_id');
    }

    public function likes()
    {
        return $this->belongsToMany(User::class, 'likes', 'song_id', 'id_user');
    }


    public function likedByUsers()
    {
        return $this->belongsToMany(User::class, 'likes', 'song_id', 'id_user');
    }

    public function getIsLikedAttribute()
    {
        try {
            $user = Auth::user();
            if (!$user) {
                return false;
            }

            return $this->likedByUsers()->where('id_user', $user->id)->exists();
        } catch (\Throwable $e) {
            // Evita que explote si Auth no está disponible (opcional)
            return false;
        }
    }
}
