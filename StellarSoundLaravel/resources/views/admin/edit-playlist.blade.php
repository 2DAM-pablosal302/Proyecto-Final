@extends('layouts.app')

@section('content')
<div class="container mt-4">
    <h2 class="mb-4">Editar Playlist</h2>

    <form action="{{ route('playlists.update', $playlist->id) }}" method="POST" enctype="multipart/form-data">
        @csrf
        @method('PUT')

 
        <div class="mb-3">
            <label for="name" class="form-label">Nombre de la Playlist</label>
            <input type="text" class="form-control" id="name" name="name" value="{{ old('name', $playlist->name) }}" required>
        </div>

        <div class="mb-3">
            <label for="id_user" class="form-label">Usuario</label>
            <select class="form-select" id="id_user" name="id_user" required>
                @foreach ($users as $user)
                    <option value="{{ $user->id }}" {{ $playlist->id_user == $user->id ? 'selected' : '' }}>
                        {{ $user->name }} ({{ $user->email }})
                    </option>
                @endforeach
            </select>
        </div>

        <div class="mb-3">
            <label class="form-label">Portada actual:</label><br>
            <img src="{{ asset('storage' . $playlist->cover_url) }}" alt="Cover" width="120">
        </div>


        <div class="mb-3">
            <label for="cover_url" class="form-label">Cambiar imagen (opcional)</label>
            <input type="file" class="form-control" id="cover_url" name="cover_url">
        </div>

        <div class="mb-4">
    <label class="form-label">Canciones en la playlist</label>
    <div class="border rounded p-3" style="max-height: 300px; overflow-y: auto;">
        @forelse ($songs as $song)
            <div class="form-check">
                <input class="form-check-input" type="checkbox" name="songs[]" value="{{ $song->id }}"
                    id="song{{ $song->id }}" {{ in_array($song->id, $selectedSongs) ? 'checked' : '' }}>
                <label class="form-check-label" for="song{{ $song->id }}">
                    {{ $song->title }} — <small>{{ $song->artist }}</small>
                </label>
            </div>
        @empty
            <p class="text-muted">No hay canciones disponibles.</p>
        @endforelse
    </div>
</div>

        <!-- Botón -->
        <button type="submit" class="btn btn-primary">Guardar Cambios</button>
        <a href="{{ route('playlists.index') }}" class="btn btn-secondary">Cancelar</a>
    </form>
</div>
@endsection
