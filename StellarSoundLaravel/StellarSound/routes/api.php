<?php

use App\Http\Controllers\UserController;
use App\Http\Controllers\GenreController;
use App\Http\Controllers\SongController;
use App\Http\Controllers\PlaylistController;
use Illuminate\Support\Facades\Route;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

Route::apiResource('usuarios', UserController::class);
Route::get('users/{user}/playlists', [UserController::class, 'playlists']);
Route::get('users/{user}/likes', [UserController::class, 'likes']);

Route::apiResource('genres', GenreController::class);
Route::get('genres/{genre}/songs', [GenreController::class, 'songs']);

Route::apiResource('songs', SongController::class);
Route::post('songs/{song}/like', [SongController::class, 'like'])->middleware('auth:sanctum');
Route::post('songs/{song}/unlike', [SongController::class, 'unlike'])->middleware('auth:sanctum');

Route::apiResource('playlists', PlaylistController::class)->middleware('auth:sanctum');
Route::post('playlists/{playlist}/add-song', [PlaylistController::class, 'addSong'])->middleware('auth:sanctum');
Route::post('playlists/{playlist}/remove-song', [PlaylistController::class, 'removeSong'])->middleware('auth:sanctum');

Route::post('/loginAndroid', function (Request $request){
    $user = User::where('email', $request->email)->first();

    if (! $user || ! Hash::check($request->password, $user->password)) {
        return response()->json(['message' => 'Credenciales incorrectas'], 401);
    }

    $token = $user->createToken('android-token')->plainTextToken;

    return response()->json(['token' => $token]);
});