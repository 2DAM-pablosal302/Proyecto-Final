<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

class AdminController extends Controller
{
    //Vista simple a modo de dashboard
    public function index()
    {
        return view('admin.dashboard');
    }
}
