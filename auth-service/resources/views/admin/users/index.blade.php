@extends('admin.layout')

@section('title', 'Users Management')

@section('content')

<div class="d-flex justify-content-between align-items-center mb-4">
    <h4>Users List</h4>
    @can('create users')
    <a href="{{ route('admin.users.create') }}" class="btn btn-primary">
        <i class="bi bi-person-plus"></i> Add User
    </a>
    @endcan
</div>

@if(session('success'))
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        {{ session('success') }}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
@endif

@if(session('error'))
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        {{ session('error') }}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
@endif

<div class="card shadow-sm">
    <div class="card-body">
        <table class="table table-striped table-hover align-middle">
            <thead class="table-light">
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Created At</th>
                    <th width="200">Actions</th>
                </tr>
            </thead>
            <tbody>
                @forelse($users as $user)
                <tr>
                    <td>{{ $user->id }}</td>
                    <td>
                        <i class="bi bi-person-circle me-2"></i>
                        {{ $user->name }}
                        @if($user->id === auth()->id())
                            <span class="badge bg-info ms-1">You</span>
                        @endif
                    </td>
                    <td>{{ $user->email }}</td>
                    <td>
                        @foreach($user->roles as $role)
                            @php
                                $roleColors = [
                                    'Super Admin' => 'danger',
                                    'Asset Manager' => 'primary',
                                    'Auditor' => 'warning',
                                    'Finance' => 'success',
                                    'Viewer' => 'secondary',
                                ];
                                $color = $roleColors[$role->name] ?? 'secondary';
                            @endphp
                            <span class="badge bg-{{ $color }}">{{ $role->name }}</span>
                        @endforeach
                        @if($user->roles->isEmpty())
                            <span class="badge bg-light text-dark">No Role</span>
                        @endif
                    </td>
                    <td>{{ $user->created_at->format('Y-m-d') }}</td>
                    <td>
                        <a href="{{ route('admin.users.show', $user) }}" class="btn btn-sm btn-info">
                            <i class="bi bi-eye"></i>
                        </a>
                        @can('edit users')
                        <a href="{{ route('admin.users.edit', $user) }}" class="btn btn-sm btn-warning">
                            <i class="bi bi-pencil"></i>
                        </a>
                        @endcan
                        @can('delete users')
                        @if($user->id !== auth()->id())
                        <form action="{{ route('admin.users.destroy', $user) }}" method="POST" class="d-inline">
                            @csrf
                            @method('DELETE')
                            <button type="submit" class="btn btn-sm btn-danger"
                                onclick="return confirm('Are you sure you want to delete this user?')">
                                <i class="bi bi-trash"></i>
                            </button>
                        </form>
                        @endif
                        @endcan
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="6" class="text-center text-muted">No users found.</td>
                </tr>
                @endforelse
            </tbody>
        </table>

        <div class="d-flex justify-content-center">
            {{ $users->links() }}
        </div>
    </div>
</div>

@endsection

