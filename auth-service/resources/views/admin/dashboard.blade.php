@extends('admin.layout')

@section('title', 'Dashboard')

@section('content')

<div class="row mb-4">

    <div class="col-md-3">
        <div class="card shadow-sm border-primary">
            <div class="card-body">
                <h6 class="text-muted">Total Assets</h6>
                <h3 class="text-primary">{{ $totalAssets }}</h3>
            </div>
        </div>
    </div>

    <div class="col-md-3">
        <div class="card shadow-sm border-success">
            <div class="card-body">
                <h6 class="text-muted">Active Assets</h6>
                <h3 class="text-success">{{ $activeAssets }}</h3>
            </div>
        </div>
    </div>

    <div class="col-md-3">
        <div class="card shadow-sm border-danger">
            <div class="card-body">
                <h6 class="text-muted">Deleted Assets</h6>
                <h3 class="text-danger">{{ $deletedAssets }}</h3>
            </div>
        </div>
    </div>

    <div class="col-md-3">
        <div class="card shadow-sm border-info">
            <div class="card-body">
                <h6 class="text-muted">Total Value</h6>
                <h3 class="text-info">{{ number_format($totalValue, 2) }} €</h3>
            </div>
        </div>
    </div>

</div>

<div class="row mb-4">

    <div class="col-md-3">
        <div class="card shadow-sm">
            <div class="card-body">
                <h6 class="text-muted">Projects</h6>
                <h3>{{ $totalProjects }}</h3>
            </div>
        </div>
    </div>

    <div class="col-md-3">
        <div class="card shadow-sm">
            <div class="card-body">
                <h6 class="text-muted">Suppliers</h6>
                <h3>{{ $totalSuppliers }}</h3>
            </div>
        </div>
    </div>

</div>

<div class="row">
    <div class="col-md-12">
        <div class="card shadow-sm">
            <div class="card-header">
                <h5 class="mb-0">Recent Imports</h5>
            </div>
            <div class="card-body">
                @if($recentImports->count() > 0)
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>File</th>
                            <th>Status</th>
                            <th>Total</th>
                            <th>Success</th>
                            <th>Failed</th>
                            <th>Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach($recentImports as $import)
                        <tr>
                            <td>{{ $import->id }}</td>
                            <td>{{ $import->file_name }}</td>
                            <td>
                                @if($import->status === 'completed')
                                    <span class="badge bg-success">Completed</span>
                                @elseif($import->status === 'failed')
                                    <span class="badge bg-danger">Failed</span>
                                @elseif($import->status === 'processing')
                                    <span class="badge bg-warning">Processing</span>
                                @else
                                    <span class="badge bg-secondary">{{ $import->status }}</span>
                                @endif
                            </td>
                            <td>{{ $import->total_rows }}</td>
                            <td class="text-success">{{ $import->success_rows }}</td>
                            <td class="text-danger">{{ $import->failed_rows }}</td>
                            <td>{{ $import->created_at->format('Y-m-d H:i') }}</td>
                        </tr>
                        @endforeach
                    </tbody>
                </table>
                @else
                <p class="text-muted">No imports yet.</p>
                @endif
            </div>
        </div>
    </div>

</div>

@endsection
