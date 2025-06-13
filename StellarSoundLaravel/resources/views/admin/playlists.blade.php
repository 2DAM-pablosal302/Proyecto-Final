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


    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3 fw-bold text-primary mb-0">Gestión de Playlists</h1>
        <a href="{{ route('playlists.create') }}" class="btn btn-sm btn-primary">
            <i class="bi bi-plus-circle me-1"></i> Nueva Playlist
        </a>
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Usuario</th>
                            <th>Portada</th>
                            <th>Canciones</th>
                            <th class="text-end">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        @forelse($playlists as $playlist)
                        <tr>
                            <td>#{{ $playlist->id }}</td>
                            <td>{{ $playlist->name }}</td>
                            <td>{{ $playlist->user->name ?? 'N/A' }}</td>
                            <td>
                                @if($playlist->cover_url)
                                    <img src="{{ asset('storage/' . $playlist->cover_url) }}" alt="Portada" width="60" class="rounded shadow-sm">
                                @else
                                    <span class="text-muted">No disponible</span>
                                @endif
                            </td>
                            <td>{{ $playlist->songs->count() }}</td>
                            <td class="text-end">
                                <a href="{{ route('playlists.edit', $playlist->id) }}" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-pencil"></i>
                                </a>
                                <form action="{{ route('playlists.destroy', $playlist->id) }}" method="POST" class="d-inline">
                                    @csrf
                                    @method('DELETE')
                                    <button class="btn btn-sm btn-outline-danger" onclick="return confirm('¿Eliminar esta playlist?')">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </form>
                            </td>
                        </tr>
                        @empty
                        <tr>
                            <td colspan="6" class="text-center py-4 text-muted">No hay playlists registradas.</td>
                        </tr>
                        @endforelse
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
@endsection
