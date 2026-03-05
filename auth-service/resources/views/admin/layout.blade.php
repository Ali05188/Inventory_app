<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Asset Management System</title>

    <!-- Bootstrap5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body {
            background-color: #f4f6f9;
        }
        .sidebar {
            height: 100vh;
            background-color: #1f2937;
        }
        .sidebar a {
            color: #cbd5e1;
            text-decoration: none;
            display: block;
            padding: 12px 20px;
        }
        .sidebar a:hover {
            background-color: #374151;
            color: #fff;
        }
        .topbar {
            background-color: #ffffff;
            border-bottom: 1px solid #e5e7eb;
            padding: 15px;
        }
    </style>
</head>
<body>

<div class="container-fluid">
    <div class="row">

        <!-- Sidebar -->
        <div class="col-md-2 sidebar p-0">
            <h4 class="text-white text-center py-3">Admin Panel</h4>

            <a href="{{ route('admin.dashboard') }}">
                <i class="bi bi-speedometer2 me-2"></i> Dashboard
            </a>
            <a href="{{ route('admin.assets.index') }}">
                <i class="bi bi-box-seam me-2"></i> Assets
            </a>
            @can('view users')
            <a href="{{ route('admin.users.index') }}">
                <i class="bi bi-people me-2"></i> Users
            </a>
            @endcan
            <a href="#">
                <i class="bi bi-folder me-2"></i> Projects
            </a>
            <a href="#">
                <i class="bi bi-truck me-2"></i> Suppliers
            </a>

            <div class="mt-4 px-3">
                <small class="text-muted d-block mb-2">
                    Logged in as: {{ auth()->user()->name ?? 'Guest' }}
                    <br>
                    @if(auth()->user())
                        @foreach(auth()->user()->roles as $role)
                            <span class="badge bg-light text-dark">{{ $role->name }}</span>
                        @endforeach
                    @endif
                </small>
            </div>

            <form method="POST" action="{{ route('logout') }}" class="mt-2 px-3">
                @csrf
                <button class="btn btn-danger w-100">
                    <i class="bi bi-box-arrow-right me-1"></i> Logout
                </button>
            </form>
        </div>

        <!-- Main Content -->
        <div class="col-md-10 p-0">

            <div class="topbar">
                <h5 class="mb-0">@yield('title')</h5>
            </div>

            <div class="p-4">
                @yield('content')
            </div>

        </div>

    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
