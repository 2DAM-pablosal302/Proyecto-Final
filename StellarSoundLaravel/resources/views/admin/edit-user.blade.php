@extends('layouts.app')

@section('content')
<div class="container py-4">
    <h2 class="mb-4">Editar Usuario</h2>

    <form action="{{ route('users.update', $user->id) }}" method="POST" class="card p-4 shadow-sm">
        @csrf
        @method('PUT')

        <div class="mb-3">
            <label for="name" class="form-label">Nombre</label>
            <input type="text" name="name" value="{{ old('name', $user->name) }}" class="form-control" required>
        </div>

        <div class="mb-3">
            <label for="email" class="form-label">Correo electrónico</label>
            <input type="email" name="email" value="{{ old('email', $user->email) }}" class="form-control" required>
        </div>

        <div class="mb-3">
            <label for="password" class="form-label">Nueva contraseña (opcional)</label>
            <input type="password" name="password" class="form-control" placeholder="Deja en blanco para mantener la actual">
        </div>

        <div class="mb-3">
            <label for="role" class="form-label">Rol</label>
            <select name="role" class="form-select" required>
                <option value="user" {{ $user->role === 'user' ? 'selected' : '' }}>Usuario</option>
                <option value="admin" {{ $user->role === 'admin' ? 'selected' : '' }}>Administrador</option>
            </select>
        </div>

        <div class="d-flex justify-content-between">
            <a href="{{ route('users.index') }}" class="btn btn-secondary">Cancelar</a>
            <button type="submit" class="btn btn-primary">Guardar cambios</button>
        </div>
    </form>
</div>
@endsection
