package com.example.assetmanagement.service;

import com.example.assetmanagement.dto.DashboardResponse;
import com.example.assetmanagement.entity.Assignment;
import com.example.assetmanagement.entity.Asset;
import com.example.assetmanagement.entity.Purchase;
import com.example.assetmanagement.entity.Transfer;
import com.example.assetmanagement.repository.AssignmentRepository;
import com.example.assetmanagement.repository.AssetRepository;
import com.example.assetmanagement.repository.PurchaseRepository;
import com.example.assetmanagement.repository.TransferRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    private final AssetRepository assetRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;

    public DashboardService(
            AssetRepository assetRepository,
            PurchaseRepository purchaseRepository,
            TransferRepository transferRepository,
            AssignmentRepository assignmentRepository) {

        this.assetRepository = assetRepository;
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public DashboardResponse getDashboard(
            Long baseId,
            Long equipmentTypeId,
            LocalDate selectedDate) {

        LocalDate date = selectedDate != null ? selectedDate : LocalDate.now();

        List<Asset> assets = assetRepository.findAll();
        List<Purchase> purchases = purchaseRepository.findAll();
        List<Transfer> transfers = transferRepository.findAll();
        List<Assignment> assignments = assignmentRepository.findAll();

        // Filter current assets
        List<Asset> filteredAssets = assets.stream()
                .filter(asset ->
                        baseId == null ||
                        (asset.getBase() != null &&
                         baseId.equals(asset.getBase().getId())))
                .filter(asset ->
                        equipmentTypeId == null ||
                        (asset.getEquipmentType() != null &&
                         equipmentTypeId.equals(asset.getEquipmentType().getId())))
                .toList();

        /*
         * Current closing balance for the selected filters.
         */
        int currentClosingBalance = filteredAssets.stream()
                .mapToInt(asset ->
                        asset.getQuantity() == null ? 0 : asset.getQuantity())
                .sum();

        /*
         * Filter purchases.
         */
        List<Purchase> filteredPurchases = purchases.stream()
                .filter(purchase ->
                        baseId == null ||
                        (purchase.getBase() != null &&
                         baseId.equals(purchase.getBase().getId())))
                .filter(purchase ->
                        equipmentTypeId == null ||
                        (purchase.getEquipmentType() != null &&
                         equipmentTypeId.equals(
                                 purchase.getEquipmentType().getId())))
                .toList();

        int purchasesUpToDate = filteredPurchases.stream()
                .filter(purchase ->
                        purchase.getPurchaseDate() != null &&
                        !purchase.getPurchaseDate().isAfter(date))
                .mapToInt(purchase ->
                        purchase.getQuantity() == null ? 0 : purchase.getQuantity())
                .sum();

        int purchasesAfterDate = filteredPurchases.stream()
                .filter(purchase ->
                        purchase.getPurchaseDate() != null &&
                        purchase.getPurchaseDate().isAfter(date))
                .mapToInt(purchase ->
                        purchase.getQuantity() == null ? 0 : purchase.getQuantity())
                .sum();

        /*
         * Filter transfers.
         *
         * Transfer OUT is based on the source base.
         * Transfer IN is based on the destination base.
         */
        int transferInUpToDate = 0;
        int transferOutUpToDate = 0;
        int transferInAfterDate = 0;
        int transferOutAfterDate = 0;

        for (Transfer transfer : transfers) {

            if (transfer.getAsset() == null ||
                transfer.getAsset().getEquipmentType() == null) {
                continue;
            }

            boolean equipmentMatches =
                    equipmentTypeId == null ||
                    equipmentTypeId.equals(
                            transfer.getAsset()
                                    .getEquipmentType()
                                    .getId());

            if (!equipmentMatches) {
                continue;
            }

            if (transfer.getTransferDate() == null) {
                continue;
            }

            LocalDate transferDate =
                    transfer.getTransferDate().toLocalDate();

            int quantity =
                    transfer.getQuantity() == null
                            ? 0
                            : transfer.getQuantity();

            boolean beforeOrOnDate = !transferDate.isAfter(date);

            if (beforeOrOnDate) {

                if (baseId == null ||
                    (transfer.getToBase() != null &&
                     baseId.equals(transfer.getToBase().getId()))) {

                    transferInUpToDate += quantity;
                }

                if (baseId == null ||
                    (transfer.getFromBase() != null &&
                     baseId.equals(transfer.getFromBase().getId()))) {

                    transferOutUpToDate += quantity;
                }

            } else {

                if (baseId == null ||
                    (transfer.getToBase() != null &&
                     baseId.equals(transfer.getToBase().getId()))) {

                    transferInAfterDate += quantity;
                }

                if (baseId == null ||
                    (transfer.getFromBase() != null &&
                     baseId.equals(transfer.getFromBase().getId()))) {

                    transferOutAfterDate += quantity;
                }
            }
        }

        /*
         * Filter expenditure.
         */
        int expenditureUpToDate = 0;
        int expenditureAfterDate = 0;

        for (Assignment assignment : assignments) {

            if (!"EXPENDED".equalsIgnoreCase(assignment.getStatus())) {
                continue;
            }

            if (assignment.getAsset() == null) {
                continue;
            }

            Asset asset = assignment.getAsset();

            boolean baseMatches =
                    baseId == null ||
                    (asset.getBase() != null &&
                     baseId.equals(asset.getBase().getId()));

            boolean equipmentMatches =
                    equipmentTypeId == null ||
                    (asset.getEquipmentType() != null &&
                     equipmentTypeId.equals(
                             asset.getEquipmentType().getId()));

            if (!baseMatches || !equipmentMatches) {
                continue;
            }

            if (assignment.getAssignedDate() == null) {
                continue;
            }

            LocalDate assignmentDate =
                    assignment.getAssignedDate().toLocalDate();

            int quantity =
                    assignment.getQuantity() == null
                            ? 0
                            : assignment.getQuantity();

            if (!assignmentDate.isAfter(date)) {
                expenditureUpToDate += quantity;
            } else {
                expenditureAfterDate += quantity;
            }
        }

        /*
         * Reconstruct the closing balance as of the selected date.
         */
        int closingBalance = currentClosingBalance
                - purchasesAfterDate
                - transferInAfterDate
                + transferOutAfterDate
                + expenditureAfterDate;

        /*
         * Opening Balance =
         * Closing Balance
         * - Purchases
         * - Transfer In
         * + Transfer Out
         * + Expenditure
         */
        int netMovement =
                purchasesUpToDate
                + transferInUpToDate
                - transferOutUpToDate;

        int openingBalance =
                closingBalance
                - netMovement
                + expenditureUpToDate;

        DashboardResponse response = new DashboardResponse();

        response.setOpeningBalance(openingBalance);
        response.setPurchases(purchasesUpToDate);
        response.setTransferIn(transferInUpToDate);
        response.setTransferOut(transferOutUpToDate);
        response.setExpenditure(expenditureUpToDate);
        response.setNetMovement(netMovement);
        response.setClosingBalance(closingBalance);

        return response;
    }
}
