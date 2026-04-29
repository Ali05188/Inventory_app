@extends('admin.layout')

@section('title', 'User Details')

@section('content')

<div class="d-flex justify-content-between align-items-center mb-4">
    <h4>User: {{ $user->name }}</h4>
    <div>
        @can('edit users')
        <a href="{{ route('admin.users.edit', $user) }}" class="btn btn-warning">
            <i class="bi bi-pencil"></i> Edit
        </a>
        @endcan
        @can('delete users')
        @if($user->id !== auth()->id())
        <form action="{{ route('admin.users.destroy', $user) }}" method="POST" class="d-inline">
            @csrf
            @method('DELETE')
            <button type="submit" class="btn btn-danger"
                onclick="return confirm('Are you sure you want to delete this user?')">
                <i class="bi bi-trash"></i> Delete
            </button>
        </form>
        @endif
        @endcan
        <a href="{{ route('admin.users.index') }}" class="btn btn-secondary">← Back to List</a>
    </div>
</div>

<div class="row">
    <div class="col-md-6">
        <div class="card shadow-sm mb-4">
            <div class="card-header">
                <h5 class="mb-0"><i class="bi bi-person"></i> User Information</h5>
            </div>
            <div class="card-body">
                <table class="table">
                    <tr>
                        <th width="150">ID</th>
                        <td>{{ $user->id }}</td>
                    </tr>
                    <tr>
                        <th>Name</th>
                        <td>{{ $user->name }}</td>
                    </tr>
                    <tr>
                        <th>Email</th>
                        <td>{{ $user->email }}</td>
                    </tr>
                    <tr>
                        <th>Created At</th>
                        <td>{{ $user->created_at->format('Y-m-d H:i') }}</td>
                    </tr>
                    <tr>
                        <th>Updated At</th>
                        <td>{{ $user->updated_at->format('Y-m-d H:i') }}</td>
                    </tr>
                </table>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="card shadow-sm mb-4">
            <div class="card-header">
                <h5 class="mb-0"><i class="bi bi-shield-lock"></i> Role & Permissions</h5>
            </div>
            <div class="card-body">
                <h6>Assigned Role:</h6>
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
                    <span class="badge bg-{{ $color }} fs-6 mb-3">{{ $role->name }}</span>
                @endforeach
                @if($user->roles->isEmpty())
                    <span class="badge bg-light text-dark">No Role Assigned</span>
                @endif

                <hr>

                <h6>Permissions:</h6>
                <div class="d-flex flex-wrap gap-1">
                    @foreach($user->getAllPermissions() as $permission)
                        <span class="badge bg-outline-secondary border text-dark">
                            {{ $permission->name }}
                        </span>
                    @endforeach
                    @if($user->getAllPermissions()->isEmpty())
                        <span class="text-muted">No permissions</span>
                    @endif
                </div>
            </div>
        </div>
    </div>
</div>

@endsection

