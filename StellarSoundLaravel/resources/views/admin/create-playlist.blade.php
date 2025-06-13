@extends('layouts.app')

@section('content')
<div class="container py-4">
    <h1 class="mb-4">Crear nueva Playlist</h1>

    @if ($errors->any())
        <div class="alert alert-danger">
            <ul class="mb-0">
                @foreach ($errors->all() as $error)
                    <li>{{ $error }}</li>
                @endforeach
            </ul>
        </div>
    @endif

    <form action="{{ route('playlists.store') }}" method="POST" enctype="multipart/form-data">
        @csrf

        <div class="mb-3">
            <label for="name" class="form-label">Nombre de la Playlist</label>
            <input type="text" name="name" id="name" class="form-control" value="{{ old('name') }}" required>
        </div>

        <div class="mb-3">
            <label for="id_user" class="form-label">Asignar a Usuario</label>
            <select name="id_user" id="id_user" class="form-select" required>
                <option value="">Selecciona un usuario</option>
                @foreach($usuarios as $usuario)
                    <option value="{{ $usuario->id }}" {{ old('id_user') == $usuario->id ? 'selected' : '' }}>
                        {{ $usuario->name }} ({{ $usuario->email }})
                    </option>
                @endforeach
            </select>
        </div>

        <div class="mb-3">
            <label for="cover_url" class="form-label">Portada (imagen)</label>
            <input type="file" name="cover_url" id="cover_url" class="form-control" accept="image/*" required>
        </div>

        <button type="submit" class="btn btn-primary">Crear Playlist</button>
        <a href="{{ route('playlists.index') }}" class="btn btn-secondary">Cancelar</a>
    </form>
</div>
@endsection
