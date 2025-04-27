<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}" data-bs-theme="auto">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="csrf-token" content="{{ csrf_token() }}">

    <title>{{ config('app.name', 'Laravel') }}</title>

    <!-- Fonts -->
    <link rel="preconnect" href="https://fonts.bunny.net">
    <link href="https://fonts.bunny.net/css?family=figtree:400,500,600&display=swap" rel="stylesheet" />

    <!-- Bootstrap CSS + Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <!-- Scripts -->
    @vite(['resources/css/app.css', 'resources/js/app.js'])
</head>

<body class="bg-body-tertiary">
    <!-- Navbar wrapper -->
    <div class="sticky-top shadow-sm">
        @include('layouts.navigation')
    </div>

    <div class="container-fluid px-0">
        <!-- Page Heading - Modern style -->
        @if (isset($header))
        <header class="bg-white border-bottom">
            <div class="container py-4">
                <div class="d-flex align-items-center justify-content-between">
                    <h1 class="h4 mb-0 fw-semibold text-primary">
                        <i class="bi bi-chevron-right me-2"></i>{{ $header }}
                    </h1>
                    <div class="d-flex gap-2">
                        <button class="btn btn-sm btn-outline-secondary">
                            <i class="bi bi-question-circle"></i>
                        </button>
                        <button class="btn btn-sm btn-primary">
                            <i class="bi bi-plus-lg"></i> Nuevo
                        </button>
                    </div>
                </div>
            </div>
        </header>
        @endif

        <!-- Main Content - Modern card layout -->
        <main class="container py-4">
            <div class="row g-4">
                <div class="col-12">
                    <div class="card border-0 shadow-sm">
                        <div class="card-body p-4">
                            @yield('content')
                        </div>
                    </div>
                </div>
            </div>
        </main>

      

    <!-- Bootstrap JS + Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Simple theme switcher -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const themeSwitcher = document.querySelector('[data-bs-theme-value]');
            if (themeSwitcher) {
                themeSwitcher.addEventListener('click', function() {
                    const theme = this.getAttribute('data-bs-theme-value');
                    document.documentElement.setAttribute('data-bs-theme', theme);
                });
            }
        });
    </script>
</body>

</html>