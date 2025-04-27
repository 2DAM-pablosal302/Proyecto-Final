@extends('layouts.app')

@section('content')
<div class="container-fluid px-4 py-4">
    <!-- Header con breadcrumb -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h3 fw-bold text-primary mb-0">Panel de Administración</h1>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-0">
                    <li class="breadcrumb-item"><a href="#">Inicio</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Dashboard</li>
                </ol>
            </nav>
        </div>
        <div class="d-flex gap-2">
            <button class="btn btn-sm btn-outline-secondary">
                <i class="bi bi-download me-1"></i> Exportar
            </button>
            <button class="btn btn-sm btn-primary">
                <i class="bi bi-plus-circle me-1"></i> Nuevo
            </button>
        </div>
    </div>

    <!-- Cards de métricas -->
    <div class="row g-4 mb-4">
        <!-- Usuarios -->
        <div class="col-xl-3 col-md-6">
            <div class="card border-0 shadow-sm h-100 hover-shadow">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <span class="badge bg-primary bg-opacity-10 text-primary mb-2">Usuarios</span>
                            <h2 class="fw-bold mb-0">{{ \App\Models\User::count() }}</h2>
                        </div>
                        <div class="bg-primary bg-opacity-10 p-3 rounded">
                            <i class="bi bi-people-fill text-primary fs-4"></i>
                        </div>
                    </div>
                    <div class="mt-3">
                        <a href="{{ route('users.index') }}" class="btn btn-sm btn-outline-primary stretched-link">
                            Gestionar <i class="bi bi-arrow-right ms-1"></i>
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Playlists -->
        <div class="col-xl-3 col-md-6">
            <div class="card border-0 shadow-sm h-100 hover-shadow">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <span class="badge bg-success bg-opacity-10 text-success mb-2">Playlists</span>
                            <h2 class="fw-bold mb-0">{{ \App\Models\Playlist::count() }}</h2>
                        </div>
                        <div class="bg-success bg-opacity-10 p-3 rounded">
                            <i class="bi bi-music-note-list text-success fs-4"></i>
                        </div>
                    </div>
                    <div class="mt-3">
                        <a href="{{ route('playlists.index') }}" class="btn btn-sm btn-outline-success stretched-link">
                            Gestionar <i class="bi bi-arrow-right ms-1"></i>
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Canciones -->
        <div class="col-xl-3 col-md-6">
            <div class="card border-0 shadow-sm h-100 hover-shadow">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <span class="badge bg-info bg-opacity-10 text-info mb-2">Canciones</span>
                            <h2 class="fw-bold mb-0">{{ \App\Models\Song::count() }}</h2>
                        </div>
                        <div class="bg-info bg-opacity-10 p-3 rounded">
                            <i class="bi bi-music-note-beamed text-info fs-4"></i>
                        </div>
                    </div>
                    <div class="mt-3">
                        <a href="{{ route('songs.index') }}" class="btn btn-sm btn-outline-info stretched-link">
                            Gestionar <i class="bi bi-arrow-right ms-1"></i>
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Géneros -->
        <div class="col-xl-3 col-md-6">
            <div class="card border-0 shadow-sm h-100 hover-shadow">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <span class="badge bg-warning bg-opacity-10 text-warning mb-2">Géneros</span>
                            <h2 class="fw-bold mb-0">{{ \App\Models\Genre::count() }}</h2>
                        </div>
                        <div class="bg-warning bg-opacity-10 p-3 rounded">
                            <i class="bi bi-tags-fill text-warning fs-4"></i>
                        </div>
                    </div>
                    <div class="mt-3">
                        <a href="{{ route('genres.index') }}" class="btn btn-sm btn-outline-warning stretched-link">
                            Gestionar <i class="bi bi-arrow-right ms-1"></i>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Sección adicional para gráficos/estadísticas -->
    <div class="row g-4">
        <div class="col-lg-8">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white border-0 py-3">
                    <h5 class="mb-0 fw-semibold">Actividad Reciente</h5>
                </div>
                <div class="card-body">
                    <div class="d-flex justify-content-center align-items-center" style="height: 250px;">
                        <p class="text-muted">Gráfico de actividad aparecerá aquí</p>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white border-0 py-3">
                    <h5 class="mb-0 fw-semibold">Distribución por Género</h5>
                </div>
                <div class="card-body">
                    <div class="d-flex justify-content-center align-items-center" style="height: 250px;">
                        <p class="text-muted">Gráfico circular aparecerá aquí</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection