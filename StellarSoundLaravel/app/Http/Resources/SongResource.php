<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class SongResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     *
     * @return array<string, mixed>
     */
    public function toArray(Request $request): array
    {
        return [
        'id' => $this->id,
        'title' => $this->title,
        'artist' => $this->artist,
        'album' => $this->album,
        'id_genre' => $this->id_genre,
        'cover_url' => $this->cover_url ? url("storage/{$this->cover_url}") : null,
        'audio_url' => $this->audio_url ? url("storage/{$this->audio_url}") : null,
        
    ];
    }
}
