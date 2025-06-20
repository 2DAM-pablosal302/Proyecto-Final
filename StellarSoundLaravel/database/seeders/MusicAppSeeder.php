<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

use App\Models\Genre;
use App\Models\Song;
use App\Models\User;
use App\Models\Playlist;
use Illuminate\Support\Facades\Hash;

class MusicAppSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Crear géneros musicales
        $genres = [
            ['name' => 'Pop'],
            ['name' => 'Rock'],
            ['name' => 'Hip Hop'],
            ['name' => 'Electrónica'],
            ['name' => 'R&B'],
            ['name' => 'Jazz'],
            ['name' => 'Clásica'],
            ['name' => 'Reggaetón'],
            ['name' => 'Indie'],
            ['name' => 'Metal'],
        ];
        
        Genre::insert($genres);
        
        // Crear usuarios
        $users = [
            [
                'name' => 'Admin User',
                'email' => 'admin@musicapp.com',
                'password' => Hash::make('password'),
                'role' => 'admin'
            ],
            [
                'name' => 'Juan Pérez',
                'email' => 'juan@musicapp.com',
                'password' => Hash::make('password'),
                'role' => 'user'
            ],
            [
                'name' => 'María García',
                'email' => 'maria@musicapp.com',
                'password' => Hash::make('password'),
                'role' => 'user'
            ],
            [
                'name' => 'Carlos López',
                'email' => 'carlos@musicapp.com',
                'password' => Hash::make('password'),
                'role' => 'user'
            ],
        ];
        
        User::insert($users);
        
        // Crear canciones
        $songs = [
            // Pop
            [
                'title' => 'Pretty Devil',
                'artist' => 'Alessandra',
                'album' => 'Best Year Of My Life',
                'id_genre' => 1,
                'cover_url' => 'covers/pretty_devil.jpg',
                'audio_url' => 'audio/pretty_devil.mp3'
            ],
            [
                'title' => 'Exes',
                'artist' => 'Tate McRae',
                'album' => 'THINK LATER',
                'id_genre' => 1,
                'cover_url' => 'covers/exes.jpg',
                'audio_url' => 'audio/exes.mp3'
            ],
            
            // Rock
            [
                'title' => 'Sweet Child O\'Mine',
                'artist' => 'Guns N\' Roses',
                'album' => 'Appetite for Destruction',
                'id_genre' => 2,
                'cover_url' => 'https://example.com/covers/appetite.jpg',
                'audio_url' => 'https://example.com/audio/sweet-child.mp3'
            ],
            [
                'title' => 'Bohemian Rhapsody',
                'artist' => 'Queen',
                'album' => 'A Night at the Opera',
                'id_genre' => 2,
                'cover_url' => 'https://example.com/covers/queen.jpg',
                'audio_url' => 'https://example.com/audio/bohemian.mp3'
            ],
            
            // Hip Hop
            [
                'title' => 'SICKO MODE',
                'artist' => 'Travis Scott',
                'album' => 'ASTROWORLD',
                'id_genre' => 3,
                'cover_url' => 'https://example.com/covers/astroworld.jpg',
                'audio_url' => 'https://example.com/audio/sicko-mode.mp3'
            ],
            [
                'title' => 'God\'s Plan',
                'artist' => 'Drake',
                'album' => 'Scorpion',
                'id_genre' => 3,
                'cover_url' => 'https://example.com/covers/scorpion.jpg',
                'audio_url' => 'https://example.com/audio/gods-plan.mp3'
            ],
            
            // Electrónica
            [
                'title' => 'Strobe',
                'artist' => 'deadmau5',
                'album' => 'For Lack of a Better Name',
                'id_genre' => 4,
                'cover_url' => 'https://example.com/covers/deadmau5.jpg',
                'audio_url' => 'https://example.com/audio/strobe.mp3'
            ],
            [
                'title' => 'Animals',
                'artist' => 'Martin Garrix',
                'album' => 'Gold Skies',
                'id_genre' => 4,
                'cover_url' => 'https://example.com/covers/gold-skies.jpg',
                'audio_url' => 'https://example.com/audio/animals.mp3'
            ],
            
            // R&B
            [
                'title' => 'Thinkin Bout You',
                'artist' => 'Frank Ocean',
                'album' => 'Channel ORANGE',
                'id_genre' => 5,
                'cover_url' => 'https://example.com/covers/channel-orange.jpg',
                'audio_url' => 'https://example.com/audio/thinkin-bout-you.mp3'
            ],
        ];
        
        Song::insert($songs);
        
        // Crear playlists y asignar canciones
        $juan = User::where('email', 'juan@musicapp.com')->first();
        $maria = User::where('email', 'maria@musicapp.com')->first();
        $carlos = User::where('email', 'carlos@musicapp.com')->first();
        
        $playlists = [
            [
                'id_user' => $juan->id,
                'name' => 'Mis favoritas',
                'cover_url' => 'playlistcovers/favouritecover.png'
            ],
            [
                'id_user' => $juan->id,
                'name' => 'Para correr',
                'cover_url' => 'https://example.com/covers/channel-orange.jpg'
            ],
            [
                'id_user' => $maria->id,
                'name' => 'Relax',
                'cover_url' => 'https://example.com/covers/channel-orange.jpg'
            ],
            [
                'id_user' => $carlos->id,
                'name' => 'Fiesta',
                'cover_url' => 'https://example.com/covers/channel-orange.jpg'
            ],
        ];
        
        foreach ($playlists as $playlistData) {
            $playlist = Playlist::create($playlistData);
            
            // Asignar canciones aleatorias a cada playlist
            $randomSongs = Song::inRandomOrder()->limit(rand(3, 6))->pluck('id');
            $playlist->songs()->attach($randomSongs);
        }
        
        // Crear likes (canciones favoritas de usuarios)
        $allUsers = User::where('role', 'user')->get();
        $allSongs = Song::all();
        
        foreach ($allUsers as $user) {
            $randomSongs = $allSongs->random(rand(2, 5))->pluck('id');
            $user->likes()->attach($randomSongs);
        }
        
        $this->command->info('Datos de prueba generados exitosamente!');
    
    }
}
