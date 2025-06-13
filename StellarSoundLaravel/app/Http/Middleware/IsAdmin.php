<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class IsAdmin
{
    /**
     * Handle an incoming request.
     *
     * @param  \Closure(\Illuminate\Http\Request): (\Symfony\Component\HttpFoundation\Response)  $next
     */
    public function handle(Request $request, Closure $next): Response
    {
        if (!auth()->check()) {
        return redirect()->route('login');
        }

        if (auth()->user()->role !== 'admin') {
        auth()->logout(); // opcional: lo deslogueas
        return redirect()->route('login')->withErrors([
            'error' => 'No tienes permisos para acceder.'
        ]);
        }

        return $next($request);
    }
}
