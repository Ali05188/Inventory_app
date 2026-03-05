@extends('admin.layout')

@section('title', 'Assets')

@section('content')

<div class="d-flex justify-content-between align-items-center mb-4">
    <h4>Assets List</h4>
    @can('create assets')
    <a href="{{ route('admin.assets.create') }}" class="btn btn-primary">+ Add Asset</a>
    @endcan
</div>

@if(session('success'))
    <div class="alert alert-success">{{ session('success') }}</div>
@endif

<!-- 🔎 FILTER CARD -->
<div class="card mb-4 shadow-sm">
    <div class="card-body">
        <form method="GET">

            <div class="row g-3">

                <div class="col-md-3">
                    <input type="text"
                           name="search"
                           class="form-control"
                           placeholder="Search CAB, Asset No, Serial..."
                           value="{{ request('search') }}">
                </div>

                <div class="col-md-2">
                    <select name="project_id" class="form-select">
                        <option value="">All Projects</option>
                        @foreach($projects as $project)
                            <option value="{{ $project->id }}"
                                {{ request('project_id') == $project->id ? 'selected' : '' }}>
                                {{ $project->name }}
                            </option>
                        @endforeach
                    </select>
                </div>

                <div class="col-md-2">
                    <select name="supplier_id" class="form-select">
                        <option value="">All Suppliers</option>
                        @foreach($suppliers as $supplier)
                            <option value="{{ $supplier->id }}"
                                {{ request('supplier_id') == $supplier->id ? 'selected' : '' }}>
                                {{ $supplier->name }}
                            </option>
                        @endforeach
                    </select>
                </div>

                <div class="col-md-2">
                    <select name="status" class="form-select">
                        <option value="">All Status</option>
                        <option value="in_service" {{ request('status') == 'in_service' ? 'selected' : '' }}>In Service</option>
                        <option value="retired" {{ request('status') == 'retired' ? 'selected' : '' }}>Retired</option>
                        <option value="disposed" {{ request('status') == 'disposed' ? 'selected' : '' }}>Disposed</option>
                    </select>
                </div>

                <div class="col-md-1">
                    <input type="date"
                           name="date_from"
                           class="form-control"
                           value="{{ request('date_from') }}">
                </div>

                <div class="col-md-1">
                    <input type="date"
                           name="date_to"
                           class="form-control"
                           value="{{ request('date_to') }}">
                </div>

                <div class="col-md-1 d-grid">
                    <button class="btn btn-dark">Filter</button>
                </div>

            </div>

        </form>
    </div>
</div>

<!-- 📊 TABLE -->
<div class="card shadow-sm">
    <div class="card-body">

        <table class="table table-striped table-hover align-middle">
            <thead class="table-light">
                <tr>
                    <th>ID</th>
                    <th>CAB</th>
                    <th>Asset No</th>
                    <th>Designation</th>
                    <th>Serial</th>
                    <th>Project</th>
                    <th>Supplier</th>
                    <th>Qty</th>
                    <th>Value (€)</th>
                    <th>Status</th>
                    <th width="150">Actions</th>
                </tr>
            </thead>

            <tbody>
                @forelse($assets as $asset)
                <tr>
                    <td>{{ $asset->id }}</td>
                    <td>{{ $asset->cab_number }}</td>
                    <td>{{ $asset->asset_number }}</td>
                    <td>{{ $asset->designation }}</td>
                    <td>{{ $asset->serial_number ?? '-' }}</td>
                    <td>{{ optional($asset->project)->name ?? '-' }}</td>
                    <td>{{ optional($asset->supplier)->name ?? '-' }}</td>
                    <td>{{ $asset->quantity }}</td>
                    <td>{{ number_format(($asset->unit_price ?? 0) * $asset->quantity, 2) }}</td>

                    <td>
                        @php
                            $badge = match($asset->status) {
                                'in_service' => 'success',
                                'retired' => 'warning',
                                'disposed' => 'danger',
                                default => 'secondary',
                            };
                        @endphp
                        <span class="badge bg-{{ $badge }}">
                            {{ ucfirst(str_replace('_',' ', $asset->status)) }}
                        </span>
                    </td>

                    <td>
                        <a href="{{ route('admin.assets.show', $asset) }}" class="btn btn-sm btn-info">View</a>
                        @can('edit assets')
                        <a href="{{ route('admin.assets.edit', $asset) }}" class="btn btn-sm btn-warning">Edit</a>
                        @endcan
                        @can('delete assets')
                        <form action="{{ route('admin.assets.destroy', $asset) }}" method="POST" class="d-inline delete-form">
                            @csrf
                            @method('DELETE')
                            <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Are you sure you want to delete this asset? This action cannot be undone.')">Delete</button>
                        </form>
                        @endcan
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="11" class="text-center text-muted">No assets found.</td>
                </tr>
                @endforelse
            </tbody>
        </table>

        <div class="d-flex justify-content-center">
            {{ $assets->links() }}
        </div>

    </div>
</div>

@endsection
