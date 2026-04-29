@extends('admin.layout')

@section('title', 'Create Asset')

@section('content')

<div class="d-flex justify-content-between align-items-center mb-4">
    <h4>Create New Asset</h4>
    <a href="{{ route('admin.assets.index') }}" class="btn btn-secondary">← Back to List</a>
</div>

<div class="card shadow-sm">
    <div class="card-body">
        <form action="{{ route('admin.assets.store') }}" method="POST">
            @csrf

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">CAB Number *</label>
                    <input type="text" name="cab_number" class="form-control @error('cab_number') is-invalid @enderror" value="{{ old('cab_number') }}" required>
                    @error('cab_number')<div class="invalid-feedback">{{ $message }}</div>@enderror
                </div>

                <div class="col-md-6 mb-3">
                    <label class="form-label">Asset Number *</label>
                    <input type="text" name="asset_number" class="form-control @error('asset_number') is-invalid @enderror" value="{{ old('asset_number') }}" required>
                    @error('asset_number')<div class="invalid-feedback">{{ $message }}</div>@enderror
                </div>
            </div>

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">Designation *</label>
                    <input type="text" name="designation" class="form-control @error('designation') is-invalid @enderror" value="{{ old('designation') }}" required>
                    @error('designation')<div class="invalid-feedback">{{ $message }}</div>@enderror
                </div>

                <div class="col-md-6 mb-3">
                    <label class="form-label">Serial Number</label>
                    <input type="text" name="serial_number" class="form-control @error('serial_number') is-invalid @enderror" value="{{ old('serial_number') }}">
                    @error('serial_number')<div class="invalid-feedback">{{ $message }}</div>@enderror
                </div>
            </div>

            <div class="row">
                <div class="col-md-3 mb-3">
                    <label class="form-label">Quantity *</label>
                    <input type="number" name="quantity" class="form-control @error('quantity') is-invalid @enderror" value="{{ old('quantity', 1) }}" min="1" required>
                    @error('quantity')<div class="invalid-feedback">{{ $message }}</div>@enderror
                </div>

                <div class="col-md-3 mb-3">
                    <label class="form-label">Unit Price (€)</label>
                    <input type="number" name="unit_price" step="0.01" class="form-control @error('unit_price') is-invalid @enderror" value="{{ old('unit_price') }}">
                    @error('unit_price')<div class="invalid-feedback">{{ $message }}</div>@enderror
                </div>

                <div class="col-md-6 mb-3">
                    <label class="form-label">Project</label>
                    <select name="project_id" class="form-select">
                        <option value="">-- Select Project --</option>
                        @foreach($projects as $project)
                            <option value="{{ $project->id }}" {{ old('project_id') == $project->id ? 'selected' : '' }}>{{ $project->name }}</option>
                        @endforeach
                    </select>
                </div>
            </div>

            <div class="row">
                <div class="col-md-4 mb-3">
                    <label class="form-label">Supplier</label>
                    <select name="supplier_id" class="form-select">
                        <option value="">-- Select Supplier --</option>
                        @foreach($suppliers as $supplier)
                            <option value="{{ $supplier->id }}" {{ old('supplier_id') == $supplier->id ? 'selected' : '' }}>{{ $supplier->name }}</option>
                        @endforeach
                    </select>
                </div>

                <div class="col-md-4 mb-3">
                    <label class="form-label">Location</label>
                    <select name="location_id" class="form-select">
                        <option value="">-- Select Location --</option>
                        @foreach($locations as $location)
                            <option value="{{ $location->id }}" {{ old('location_id') == $location->id ? 'selected' : '' }}>{{ $location->name }}</option>
                        @endforeach
                    </select>
                </div>

                <div class="col-md-4 mb-3">
                    <label class="form-label">Asset Type</label>
                    <select name="asset_type_id" class="form-select">
                        <option value="">-- Select Type --</option>
                        @foreach($assetTypes as $type)
                            <option value="{{ $type->id }}" {{ old('asset_type_id') == $type->id ? 'selected' : '' }}>{{ $type->name }}</option>
                        @endforeach
                    </select>
                </div>
            </div>

            <div class="mt-4">
                <button type="submit" class="btn btn-primary">Create Asset</button>
                <a href="{{ route('admin.assets.index') }}" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

@endsection

