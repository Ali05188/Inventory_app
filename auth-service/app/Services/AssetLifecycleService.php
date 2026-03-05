<?php

namespace App\Services;

use App\Models\Asset;
use App\Models\AssetStatusHistory;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use InvalidArgumentException;

class AssetLifecycleService
{
    /**
     * Allowed status transitions.
     *
     * From => [allowed to statuses]
     */
    protected array $allowedTransitions = [
        'pending' => ['in_service'],
        'in_service' => ['retired', 'transferred'],
        'retired' => ['disposed'],
        'transferred' => ['in_service', 'retired'],
        'disposed' => [], // Final state - no transitions allowed
    ];

    /**
     * All valid statuses.
     */
    protected array $validStatuses = [
        'pending',
        'in_service',
        'retired',
        'transferred',
        'disposed',
    ];

    /**
     * Change the status of an asset with full audit trail.
     *
     * @param Asset $asset The asset to update
     * @param string $newStatus The new status to set
     * @param string|null $reason Optional reason for the change
     * @param array|null $metadata Optional additional metadata
     * @return Asset The updated asset
     * @throws InvalidArgumentException If the transition is not allowed
     */
    public function changeStatus(
        Asset $asset,
        string $newStatus,
        ?string $reason = null,
        ?array $metadata = null
    ): Asset {
        // Validate the new status
        if (!in_array($newStatus, $this->validStatuses)) {
            throw new InvalidArgumentException("Invalid status: {$newStatus}");
        }

        $previousStatus = $asset->status;

        // Validate the transition (skip if first status assignment)
        if ($previousStatus !== null && !$this->isTransitionAllowed($previousStatus, $newStatus)) {
            throw new InvalidArgumentException(
                "Transition from '{$previousStatus}' to '{$newStatus}' is not allowed."
            );
        }

        // Wrap in transaction for atomic operation
        return DB::transaction(function () use ($asset, $newStatus, $previousStatus, $reason, $metadata) {
            // Update the asset status
            $asset->status = $newStatus;

            // Set service_start_date if transitioning to in_service
            if ($newStatus === 'in_service' && !$asset->service_start_date) {
                $asset->service_start_date = now();
            }

            // Set exit_date if transitioning to disposed or retired
            if (in_array($newStatus, ['disposed', 'retired']) && !$asset->exit_date) {
                $asset->exit_date = now();
                $asset->exit_reason = $reason;
            }

            $asset->save();

            // Create the audit history record
            AssetStatusHistory::create([
                'asset_id' => $asset->id,
                'previous_status' => $previousStatus,
                'new_status' => $newStatus,
                'changed_by' => Auth::id(),
                'reason' => $reason,
                'metadata' => $metadata,
            ]);

            return $asset->fresh();
        });
    }

    /**
     * Check if a status transition is allowed.
     */
    public function isTransitionAllowed(string $from, string $to): bool
    {
        if (!isset($this->allowedTransitions[$from])) {
            return false;
        }

        return in_array($to, $this->allowedTransitions[$from]);
    }

    /**
     * Get allowed transitions for a given status.
     */
    public function getAllowedTransitions(string $currentStatus): array
    {
        return $this->allowedTransitions[$currentStatus] ?? [];
    }

    /**
     * Get all valid statuses.
     */
    public function getValidStatuses(): array
    {
        return $this->validStatuses;
    }

    /**
     * Get the status history for an asset.
     */
    public function getStatusHistory(Asset $asset): \Illuminate\Database\Eloquent\Collection
    {
        return $asset->statusHistories()->with('changedByUser')->get();
    }

    /**
     * Log an initial status when asset is created (no transition validation).
     */
    public function logInitialStatus(
        Asset $asset,
        string $status = 'pending',
        ?string $reason = 'Asset created',
        ?array $metadata = null
    ): void {
        AssetStatusHistory::create([
            'asset_id' => $asset->id,
            'previous_status' => null,
            'new_status' => $status,
            'changed_by' => Auth::id(),
            'reason' => $reason,
            'metadata' => $metadata,
        ]);
    }
}

