@extends('admin.layout')

@section('title', 'Asset Details')

@section('content')

<div class="d-flex justify-content-between align-items-center mb-4">
    <h4>Asset #{{ $asset->id }}</h4>
    <div>
        @can('edit assets')
        <a href="{{ route('admin.assets.edit', $asset) }}" class="btn btn-warning">
            <i class="bi bi-pencil"></i> Edit
        </a>
        @endcan
        @can('delete assets')
        <form action="{{ route('admin.assets.destroy', $asset) }}" method="POST" class="d-inline">
            @csrf
            @method('DELETE')
            <button type="submit" class="btn btn-danger" onclick="return confirm('Are you sure you want to delete this asset? This action cannot be undone.')">
                <i class="bi bi-trash"></i> Delete
            </button>
        </form>
        @endcan
        <a href="{{ route('admin.assets.index') }}" class="btn btn-secondary">← Back to List</a>
    </div>
</div>

{{-- Flash Messages --}}
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

<div class="row">
    {{-- Asset Details Card --}}
    <div class="col-md-8">
        <div class="card shadow-sm mb-4">
            <div class="card-header">
                <h5 class="mb-0">Asset Information</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <table class="table">
                            <tr>
                                <th>CAB Number</th>
                                <td>{{ $asset->cab_number }}</td>
                            </tr>
                            <tr>
                                <th>Asset Number</th>
                                <td>{{ $asset->asset_number }}</td>
                            </tr>
                            <tr>
                                <th>Designation</th>
                                <td>{{ $asset->designation }}</td>
                            </tr>
                            <tr>
                                <th>Serial Number</th>
                                <td>{{ $asset->serial_number ?? '-' }}</td>
                            </tr>
                            <tr>
                                <th>Quantity</th>
                                <td>{{ $asset->quantity }}</td>
                            </tr>
                            <tr>
                                <th>Unit Price</th>
                                <td>{{ number_format($asset->unit_price ?? 0, 2) }} €</td>
                            </tr>
                            <tr>
                                <th>Total Value</th>
                                <td><strong>{{ number_format(($asset->unit_price ?? 0) * ($asset->quantity ?? 1), 2) }} €</strong></td>
                            </tr>
                        </table>
                    </div>
                    <div class="col-md-6">
                        <table class="table">
                            <tr>
                                <th>Project</th>
                                <td>{{ $asset->project->name ?? '-' }}</td>
                            </tr>
                            <tr>
                                <th>Supplier</th>
                                <td>{{ $asset->supplier->name ?? '-' }}</td>
                            </tr>
                            <tr>
                                <th>Location</th>
                                <td>{{ $asset->location->name ?? '-' }}</td>
                            </tr>
                            <tr>
                                <th>Asset Type</th>
                                <td>{{ $asset->assetType->name ?? '-' }}</td>
                            </tr>
                            <tr>
                                <th>Delivery Date</th>
                                <td>{{ $asset->delivery_date ?? '-' }}</td>
                            </tr>
                            <tr>
                                <th>Created At</th>
                                <td>{{ $asset->created_at->format('Y-m-d H:i') }}</td>
                            </tr>
                            <tr>
                                <th>Updated At</th>
                                <td>{{ $asset->updated_at->format('Y-m-d H:i') }}</td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

    {{-- Status & Change Status Card --}}
    <div class="col-md-4">
        <div class="card shadow-sm mb-4">
            <div class="card-header">
                <h5 class="mb-0">Current Status</h5>
            </div>
            <div class="card-body text-center">
                @php
                    $statusColors = [
                        'pending' => 'secondary',
                        'in_service' => 'success',
                        'retired' => 'warning',
                        'transferred' => 'info',
                        'disposed' => 'danger',
                    ];
                    $currentStatus = $asset->status ?? 'pending';
                    $statusColor = $statusColors[$currentStatus] ?? 'secondary';
                @endphp

                <h3>
                    <span class="badge bg-{{ $statusColor }} fs-5 px-4 py-2">
                        {{ ucfirst(str_replace('_', ' ', $currentStatus)) }}
                    </span>
                </h3>

                @can('change asset status')
                @if(count($allowedTransitions) > 0)
                    <hr>
                    <h6>Change Status</h6>
                    <form action="{{ route('admin.assets.change-status', $asset) }}" method="POST">
                        @csrf
                        <div class="mb-3">
                            <select name="new_status" class="form-select" required>
                                <option value="">-- Select New Status --</option>
                                @foreach($allowedTransitions as $status)
                                    <option value="{{ $status }}">{{ ucfirst(str_replace('_', ' ', $status)) }}</option>
                                @endforeach
                            </select>
                        </div>
                        <div class="mb-3">
                            <textarea name="reason" class="form-control" placeholder="Reason for change (optional)" rows="2"></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">
                            <i class="bi bi-arrow-repeat"></i> Update Status
                        </button>
                    </form>
                @else
                    <p class="text-muted mt-3">
                        <em>This asset has reached its final status and cannot be changed.</em>
                    </p>
                @endif
                @endcan
            </div>
        </div>
    </div>
</div>

{{-- Status History Timeline Card --}}
<div class="card shadow-sm">
    <div class="card-header">
        <h5 class="mb-0">
            <i class="bi bi-clock-history"></i> Status History
        </h5>
    </div>
    <div class="card-body">
        @if($asset->statusHistories->count() > 0)
            <div class="timeline">
                @foreach($asset->statusHistories as $history)
                    <div class="timeline-item mb-4 pb-3 border-bottom">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <div class="mb-2">
                                    @if($history->previous_status)
                                        <span class="badge bg-{{ $statusColors[$history->previous_status] ?? 'secondary' }}">
                                            {{ ucfirst(str_replace('_', ' ', $history->previous_status)) }}
                                        </span>
                                        <i class="bi bi-arrow-right mx-2"></i>
                                    @else
                                        <span class="badge bg-light text-dark">Initial</span>
                                        <i class="bi bi-arrow-right mx-2"></i>
                                    @endif
                                    <span class="badge bg-{{ $statusColors[$history->new_status] ?? 'secondary' }}">
                                        {{ ucfirst(str_replace('_', ' ', $history->new_status)) }}
                                    </span>
                                </div>

                                @if($history->reason)
                                    <p class="text-muted mb-1">
                                        <strong>Reason:</strong> {{ $history->reason }}
                                    </p>
                                @endif

                                <small class="text-muted">
                                    <i class="bi bi-person"></i>
                                    {{ $history->changedByUser->name ?? 'System' }}
                                </small>
                            </div>
                            <div class="text-end">
                                <small class="text-muted">
                                    <i class="bi bi-calendar"></i>
                                    {{ $history->created_at->format('M d, Y') }}
                                    <br>
                                    <i class="bi bi-clock"></i>
                                    {{ $history->created_at->format('H:i:s') }}
                                </small>
                            </div>
                        </div>
                    </div>
                @endforeach
            </div>
        @else
            <div class="text-center py-4">
                <i class="bi bi-info-circle fs-1 text-muted"></i>
                <p class="text-muted mt-2">No status changes recorded.</p>
            </div>
        @endif
    </div>
</div>

<style>
    .timeline-item:last-child {
        border-bottom: none !important;
        margin-bottom: 0 !important;
        padding-bottom: 0 !important;
    }
</style>

@endsection
