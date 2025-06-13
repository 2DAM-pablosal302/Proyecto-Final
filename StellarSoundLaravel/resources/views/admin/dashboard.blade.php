@extends('layouts.app')

@section('content')
<div class="container-fluid px-4 py-4">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h3 fw-bold text-primary mb-0">Panel de Administración</h1>
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

    </div>

    <!-- Sección gráficos/estadísticas -->
    <div class="row g-4">
        <div class="col-lg-8">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white border-0 py-3">
                    <h5 class="mb-0 fw-semibold">Ranking de Canciones con Más Likes</h5>
                </div>
                <div class="card-body">
                    <canvas id="topSongsChart" style="max-height: 300px;"></canvas>
                </div>
            </div>
        </div>
        <div class="col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white border-0 py-3">
                    <h5 class="mb-0 fw-semibold">Distribución por Género</h5>
                </div>
                <div class="card-body">
                    <canvas id="genreChart" style="max-height: 300px;"></canvas>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>

    const topSongsLabels = @json($topSongs->pluck('title'));
    const topSongsLikes = @json($topSongs->pluck('likes_count'));

    const ctxTopSongs = document.getElementById('topSongsChart').getContext('2d');
    new Chart(ctxTopSongs, {
        type: 'bar',
        data: {
            labels: topSongsLabels,
            datasets: [{
                label: 'Likes',
                data: topSongsLikes,
                backgroundColor: 'rgba(54, 162, 235, 0.7)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1,
                borderRadius: 5
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    precision: 0
                }
            },
            responsive: true,
            plugins: {
                legend: { display: false },
                tooltip: { enabled: true }
            }
        }
    });

    const genreLabels = @json($genres->pluck('name'));
    const genreCounts = @json($genres->pluck('songs_count'));

    const ctxGenre = document.getElementById('genreChart').getContext('2d');
    new Chart(ctxGenre, {
        type: 'doughnut',
        data: {
            labels: genreLabels,
            datasets: [{
                label: 'Canciones por Género',
                data: genreCounts,
                backgroundColor: [
                    '#36A2EB',
                    '#FF6384',
                    '#FFCE56',
                    '#4BC0C0',
                    '#9966FF',
                    '#FF9F40',
                    '#8AFF8A',
                    '#FF8A8A'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { position: 'right' }
            }
        }
    });
</script>
@endsection