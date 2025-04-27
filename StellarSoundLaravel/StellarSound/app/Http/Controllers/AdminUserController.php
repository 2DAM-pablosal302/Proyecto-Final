<?php

namespace App\Http\Controllers;
use App\Models\User;
use Illuminate\Http\Request;

class AdminUserController extends Controller
{
    public function index(){
        $usuarios = User::all();
        return view('admin.usuarios', compact('usuarios'));
    }
}
