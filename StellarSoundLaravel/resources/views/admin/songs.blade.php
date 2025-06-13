@extends('layouts.app')

@section('content')
<div class="container-fluid px-4 py-4">
    @if (session('success'))
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        {{ session('success') }}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
    </div>
@endif

@if (session('error'))
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        {{ session('error') }}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
    </div>
@endif


    <div class="d-flex justify-content-between mb-3">
        <h3 class="h3 fw-bold text-primary mb-0">Gestión de Canciones</h3>
        <a href="{{ route('songs.create') }}" class="btn btn-primary btn-sm">
            <i class="bi bi-plus-circle me-1"></i>Nueva Canción
        </a>
    </div>

    <div class="card shadow-sm">
        <div class="card-body p-0">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
    <tr>
        <th>#</th>
        <th>Título</th>
        <th>Artista</th>
        <th>Álbum</th>
        <th>Género</th>
        <th>Acciones</th>
    </tr>
</thead>
<tbody>
    @forelse($songs as $song)
        <tr>
            <td>{{ $song->id }}</td>
            <td>{{ $song->title }}</td>
            <td>{{ $song->artist }}</td>
            <td>{{ $song->album }}</td>
            <td>{{ $song->genre->name }}</td>
            <td>
                <a href="{{ route('songs.edit', $song) }}" class="btn btn-sm btn-outline-primary">
                    <i class="bi bi-pencil"></i>
                </a>
                <form action="{{ route('songs.destroy', $song) }}" method="POST" class="d-inline">
                    @csrf
                    @method('DELETE')
                    <button class="btn btn-sm btn-outline-danger" onclick="return confirm('¿Eliminar esta canción?')">
                        <i class="bi bi-trash"></i>
                    </button>
                </form>
            </td>
        </tr>
    @empty
        <tr>
            <td colspan="6" class="text-center py-4">No hay canciones registradas</td>
        </tr>
    @endforelse
</tbody>

            </table>
        </div>
    </div>
</div>
@endsection
