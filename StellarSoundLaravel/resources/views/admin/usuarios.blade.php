@extends('layouts.app')

@section('content')
<div class="container-fluid px-4 py-4">
    <!-- Header con acciones -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h3 fw-bold text-primary mb-0">Gestión de Usuarios</h1>
        </div>
        <div class="d-flex gap-2">
            <a href="{{ route('users.create') }}" class="btn btn-sm btn-primary">
                <i class="bi bi-plus-circle me-1"></i> Nuevo Usuario
            </a>
        </div>
    </div>

    <!-- Tarjeta contenedora -->
    <div class="card border-0 shadow-sm">
        <div class="card-header bg-white border-0 py-3">
            <div class="d-flex justify-content-between align-items-center">
                <h5 class="mb-0 fw-semibold">Listado de Usuarios</h5>
                <div class="d-flex align-items-center">
                    <span class="badge bg-primary me-2">Total: {{ $usuarios->count() }}</span>
                    <div class="input-group input-group-sm" style="width: 200px;">
                        <input type="text" class="form-control" placeholder="Buscar...">
                        <button class="btn btn-outline-secondary" type="button">
                            <i class="bi bi-search"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="card-body">
            <!-- Tabla de usuarios -->
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th width="50px">ID</th>
                            <th>Nombre</th>
                            <th>Email</th>
                            <th>Rol</th>
                            <th>Playlists</th>
                            <th>Favoritos</th>
                            <th width="120px" class="text-end">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        @forelse($usuarios as $usuario)
                        <tr>
                            <td class="fw-semibold">#{{ $usuario->id }}</td>
                            <td>
                                <div class="d-flex align-items-center">
                                    <div class="avatar-sm me-2">
                                        <span class="avatar-title bg-primary bg-opacity-10 text-primary rounded-circle">
                                            {{ substr($usuario->name, 0, 1) }}
                                        </span>
                                    </div>
                                    <div>
                                        <h6 class="mb-0">{{ $usuario->name }}</h6>
                                        <small class="text-muted">
                                            Registrado: {{ $usuario->created_at ? $usuario->created_at->format('d/m/Y') 
                                            : 'Fecha no disponible' }}
                                        </small>
                                    </div>
                                </div>
                            </td>
                            <td>{{ $usuario->email }}</td>
                            <td>
                                @if($usuario->role == 'admin')
                                    <span class="badge bg-danger">Administrador</span>
                                @else
                                    <span class="badge bg-secondary">Usuario</span>
                                @endif
                            </td>
                            <td>{{ $usuario->playlists->count() }}</td>
                            <td>{{ $usuario->likes->count() }}</td>
                            <td class="text-end">
                                <div class="d-flex gap-2 justify-content-end">
                                    <a href="{{ route('users.edit', $usuario->id) }}" class="btn btn-sm btn-outline-primary" title="Editar">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <form action="{{ route('users.destroy', $usuario->id) }}" method="POST">
                                        @csrf
                                        @method('DELETE')
                                        <button type="submit" class="btn btn-sm btn-outline-danger" title="Eliminar" onclick="return confirm('¿Estás seguro?')">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                        @empty
                        <tr>
                            <td colspan="7" class="text-center py-4 text-muted">
                                No se encontraron usuarios
                            </td>
                        </tr>
                        @endforelse
                    </tbody>
                </table>
            </div>
            
            <!-- Paginación -->
            
        </div>
    </div>
</div>


@endsection