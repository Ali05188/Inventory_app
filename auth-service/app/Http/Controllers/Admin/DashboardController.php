<?php
namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Asset;
use App\Models\AssetImportBatch;
use App\Models\Project;
use App\Models\Supplier;
use Illuminate\Support\Facades\DB;

class DashboardController extends Controller
{
    public function index()
    {
        return view('admin.dashboard', [
            'totalAssets' => Asset::count(),
            'activeAssets' => Asset::whereNull('deleted_at')->count(),
            'deletedAssets' => Asset::onlyTrashed()->count(),
            'totalValue' => Asset::sum(DB::raw('COALESCE(unit_price, 0) * COALESCE(quantity, 1)')),
            'totalProjects' => Project::count(),
            'totalSuppliers' => Supplier::count(),
            'recentImports' => AssetImportBatch::latest()->take(5)->get(),
        ]);
    }
}
