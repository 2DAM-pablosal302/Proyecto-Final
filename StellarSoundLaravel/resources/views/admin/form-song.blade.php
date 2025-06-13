@extends('layouts.app')

@section('content')
@if ($errors->any())
    <div class="alert alert-danger">
        <ul class="mb-0">
            @foreach ($errors->all() as $error)
                <li>{{ $error }}</li>
            @endforeach
        </ul>
    </div>
@endif

<form action="{{ $edit ? route('songs.update', $song) : route('songs.store') }}" method="POST" enctype="multipart/form-data">
    @csrf
    @if($edit) @method('PUT') @endif

    <div class="mb-3">
        <label for="title" class="form-label">Título</label>
        <input type="text" name="title" class="form-control" value="{{ old('title', $song->title ?? '') }}" required>
    </div>

    <div class="mb-3">
        <label for="artist" class="form-label">Artista</label>
        <input type="text" name="artist" class="form-control" value="{{ old('artist', $song->artist ?? '') }}" required>
    </div>

    <div class="mb-3">
        <label for="album" class="form-label">Álbum</label>
        <input type="text" name="album" class="form-control" value="{{ old('album', $song->album ?? '') }}" required>
    </div>

    <div class="mb-3">
        <label for="id_genre" class="form-label">Género</label>
        <select name="id_genre" class="form-select" required>
            @foreach($genres as $genre)
                <option value="{{ $genre->id }}" @selected(old('id_genre', $song->id_genre ?? '') == $genre->id)>
                    {{ $genre->name }}
                </option>
            @endforeach
        </select>
    </div>

    <div class="mb-3">
        <label for="cover" class="form-label">Portada</label>
        <input type="file" name="cover" class="form-control" {{ $edit ? '' : 'required' }}>
        @if($edit && $song->cover_url)
            <img src="{{ asset('storage/' . $song->cover_url) }}" class="mt-2 rounded" width="100">
        @endif
    </div>

    <div class="mb-3">
        <label for="audio" class="form-label">Archivo de audio</label>
        <input type="file" name="audio" class="form-control" {{ $edit ? '' : 'required' }}>
        @if($edit && $song->audio_url)
            <audio controls class="mt-2">
                <source src="{{ asset('storage/' . $song->audio_url) }}" type="audio/mpeg">
                Tu navegador no soporta la reproducción de audio.
            </audio>
        @endif
    </div>

    <div class="d-flex gap-2">
        <button type="submit" class="btn btn-success">{{ $edit ? 'Actualizar' : 'Crear' }}</button>
        <a href="{{ route('songs.index') }}" class="btn btn-secondary">Cancelar</a>
    </div>
</form>
@endsection