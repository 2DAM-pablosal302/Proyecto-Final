@extends('layouts.app')

@section('content')
<div class="container py-4">
    <h2 class="mb-4">Crear Usuario</h2>

    <form action="{{ route('users.store') }}" method="POST" class="card p-4 shadow-sm">
        @csrf

        <div class="mb-3">
            <label for="name" class="form-label">Nombre</label>
            <input type="text" name="name" class="form-control" value="{{ old('name') }}" required>
        </div>

        <div class="mb-3">
            <label for="email" class="form-label">Correo electrónico</label>
            <input type="email" name="email" class="form-control" value="{{ old('email') }}" required>
        </div>

        <div class="mb-3">
            <label for="password" class="form-label">Contraseña</label>
            <input type="password" name="password" class="form-control" required>
        </div>

        <div class="mb-3">
            <label for="role" class="form-label">Rol</label>
            <select name="role" class="form-select" required>
                <option value="user" {{ old('role') === 'user' ? 'selected' : '' }}>Usuario</option>
                <option value="admin" {{ old('role') === 'admin' ? 'selected' : '' }}>Administrador</option>
            </select>
        </div>

        <div class="d-flex justify-content-between">
            <a href="{{ route('users.index') }}" class="btn btn-secondary">Cancelar</a>
            <button type="submit" class="btn btn-success">Crear usuario</button>
        </div>
    </form>
</div>
@endsection
