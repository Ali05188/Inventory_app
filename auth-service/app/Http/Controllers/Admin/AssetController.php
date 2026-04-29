<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Asset;
use App\Models\Project;
use App\Models\Supplier;
use App\Models\Location;
use App\Models\AssetType;
use App\Services\AssetLifecycleService;
use Illuminate\Http\Request;
use InvalidArgumentException;

class AssetController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $this->authorize('view assets');

        $query = Asset::with(['supplier', 'project', 'location', 'assetType']);

            // 🔎 Global Search
            if ($request->filled('search')) {
                $query->where(function ($q) use ($request) {
                    $q->where('cab_number', 'like', '%' . $request->search . '%')
                      ->orWhere('asset_number', 'like', '%' . $request->search . '%')
                      ->orWhere('designation', 'like', '%' . $request->search . '%')
                      ->orWhere('serial_number', 'like', '%' . $request->search . '%');
                });
            }

            // 🎛 Project Filter
            if ($request->filled('project_id')) {
                $query->where('project_id', $request->project_id);
            }

            // 🎛 Supplier Filter
            if ($request->filled('supplier_id')) {
                $query->where('supplier_id', $request->supplier_id);
            }

            // 🎛 Status Filter
            if ($request->filled('status')) {
                $query->where('status', $request->status);
            }

            // 📅 Date Filter (Delivery Date)
            if ($request->filled('date_from')) {
                $query->where('delivery_date', '>=', $request->date_from);
            }
            if ($request->filled('date_to')) {
                $query->where('delivery_date', '<=', $request->date_to);
            }

            $assets = $query->orderBy('created_at', 'desc')
                            ->paginate(15)
                            ->withQueryString();

            return view('admin.assets.index', [
                'assets' => $assets,
                'projects' => Project::all(),
                'suppliers' => Supplier::where('is_active', true)->get(),
            ]);
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        $this->authorize('create assets');

        return view('admin.assets.create', [
            'projects' => Project::all(),
            'suppliers' => Supplier::where('is_active', true)->get(),
            'locations' => Location::where('is_active', true)->get(),
            'assetTypes' => AssetType::all(),
        ]);
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $this->authorize('create assets');

        $validated = $request->validate([
            'cab_number' => 'required|string|max:255',
            'asset_number' => 'required|string|max:255',
            'designation' => 'required|string|max:255',
            'serial_number' => 'nullable|string|max:255|unique:assets,serial_number',
            'quantity' => 'required|integer|min:1',
            'unit_price' => 'nullable|numeric|min:0',
            'project_id' => 'nullable|exists:projects,id',
            'supplier_id' => 'nullable|exists:suppliers,id',
            'location_id' => 'nullable|exists:locations,id',
            'asset_type_id' => 'nullable|exists:asset_types,id',
        ]);

        Asset::create($validated);

        return redirect()->route('admin.assets.index')
            ->with('success', 'Asset created successfully.');
    }

    /**
     * Display the specified resource.
     */
    public function show(Asset $asset, AssetLifecycleService $lifecycleService)
    {
        $this->authorize('view assets');

        $asset->load(['supplier', 'project', 'location', 'assetType', 'statusHistories.changedByUser']);

        $allowedTransitions = $asset->status
            ? $lifecycleService->getAllowedTransitions($asset->status)
            : $lifecycleService->getValidStatuses();

        return view('admin.assets.show', [
            'asset' => $asset,
            'allowedTransitions' => $allowedTransitions,
        ]);
    }

    /**
     * Change the status of an asset.
     */
    public function changeStatus(Request $request, Asset $asset, AssetLifecycleService $lifecycleService)
    {
        $validated = $request->validate([
            'new_status' => 'required|string',
            'reason' => 'nullable|string|max:500',
        ]);

        try {
            $lifecycleService->changeStatus(
                $asset,
                $validated['new_status'],
                $validated['reason'] ?? null
            );

            return redirect()->route('admin.assets.show', $asset)
                ->with('success', 'Asset status changed successfully.');
        } catch (InvalidArgumentException $e) {
            return redirect()->route('admin.assets.show', $asset)
                ->with('error', $e->getMessage());
        }
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Asset $asset)
    {
        $this->authorize('edit assets');

        return view('admin.assets.edit', [
            'asset' => $asset,
            'projects' => Project::all(),
            'suppliers' => Supplier::where('is_active', true)->get(),
            'locations' => Location::where('is_active', true)->get(),
            'assetTypes' => AssetType::all(),
        ]);
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Asset $asset)
    {
        $this->authorize('edit assets');

        $validated = $request->validate([
            'cab_number' => 'required|string|max:255',
            'asset_number' => 'required|string|max:255',
            'designation' => 'required|string|max:255',
            'serial_number' => 'nullable|string|max:255|unique:assets,serial_number,' . $asset->id,
            'quantity' => 'required|integer|min:1',
            'unit_price' => 'nullable|numeric|min:0',
            'project_id' => 'nullable|exists:projects,id',
            'supplier_id' => 'nullable|exists:suppliers,id',
            'location_id' => 'nullable|exists:locations,id',
            'asset_type_id' => 'nullable|exists:asset_types,id',
        ]);

        $asset->update($validated);

        return redirect()->route('admin.assets.index')
            ->with('success', 'Asset updated successfully.');
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Asset $asset)
    {
        $this->authorize('delete assets');

        $asset->delete();

        return redirect()->route('admin.assets.index')
            ->with('success', 'Asset deleted successfully.');
    }
}
