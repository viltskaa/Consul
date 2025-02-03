package com.example.consul.mapping;

import com.example.consul.dto.WB.WB_DetailReport;
import com.example.consul.dto.WB.WB_SaleReport;
import com.example.consul.dto.WB.enums.WB_AccrualType;
import com.example.consul.dto.WB.enums.WB_JustificationPayment;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class WB_dataProcessing {
    public String checkOnDouble(@NotNull String sku) {
        int center = sku.length() / 2;
        return sku.substring(0, center)
                .equals(sku.substring(center)) ? sku.substring(center) : sku;
    }

    public Integer checkSaleCount(WB_DetailReport line) {
        if (Objects.equals(line.getDocTypeName(), WB_AccrualType.SALE.toString())) {
            if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.SALE.toString())){
                return line.getQuantity();
            }
            else {
                return 0;
            }
        }
        else {
            return 0;
        }
    }

    public Double checkSaleSum(WB_DetailReport line) {
        if (Objects.equals(line.getDocTypeName(), WB_AccrualType.SALE.toString())) {
            if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.SALES_ADJUSTMENT.toString())){
                return line.getRetailAmount();
            }
            else if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.SALE.toString())){
                return line.getRetailAmount();
            }
            else {
                return 0.0;
            }
        }
        else {
            return 0.0;
        }
    }

    public Integer checkReturnCount(WB_DetailReport line) {
        if (Objects.equals(line.getDocTypeName(), WB_AccrualType.RETURN.toString())) {
            if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.RETURN.toString())){
                return line.getQuantity();
            }
            else {
                return 0;
            }
        }
        else {
            return 0;
        }
    }

    public Double checkReturnSum(WB_DetailReport line) {
        if (Objects.equals(line.getDocTypeName(), WB_AccrualType.RETURN.toString())) {
            if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.RETURN.toString())){
                return line.getRetailAmount();
            }
            else {
                return 0.0;
            }
        }
        else {
            return 0.0;
        }
    }

    public Double checkCompensationLost(WB_DetailReport line) {
        if (Objects.equals(line.getDocTypeName(), WB_AccrualType.SALE.toString())) {
            if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.COMPENSATION_DAMAGE.toString())){
                return line.getPpvzForPay();
            }
            else if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.VOLUNTARY_COMPENSATION_RETURN.toString())) {
                return line.getPpvzForPay();
            }
            else {
                return 0.0;
            }
        }
        else if (Objects.equals(line.getDocTypeName(), WB_AccrualType.RETURN.toString())) {
            if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.COMPENSATION_DAMAGE.toString())){
                return line.getPpvzForPay() * (-1);
            }
            else if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.VOLUNTARY_COMPENSATION_RETURN.toString())) {
                return line.getPpvzForPay() * (-1);
            }
            else {
                return 0.0;
            }
        }
        else {
            return 0.0;
        }
    }

    public Integer checkCountLost(WB_DetailReport line) {
        if (Objects.equals(line.getDocTypeName(), WB_AccrualType.SALE.toString())) {
            if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.COMPENSATION_DAMAGE.toString())){
                return 1;
            }
            else if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.VOLUNTARY_COMPENSATION_RETURN.toString())) {
                return 1;
            }
            else {
                return 0;
            }
        }
        else {
            return 0;
        }
    }

    public Double checkCommission(WB_DetailReport line) {
        if (Objects.equals(line.getDocTypeName(), WB_AccrualType.SALE.toString())) {
            return commissionConditions(line);
        }
        else {
            return 0.0;
        }
    }

    public Double checkReturnCommission(WB_DetailReport line) {
        if (Objects.equals(line.getDocTypeName(), WB_AccrualType.RETURN.toString())) {
            return commissionConditions(line);
        }
        else {
            return 0.0;
        }
    }

    public Double checkPenalty(WB_DetailReport line) {
        if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.PENALTY.toString())) {
            return line.getPenalty();
        }
        else {
            return 0.0;
        }
    }

    public Double checkDeduction(WB_DetailReport line) {
        if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.DEDUCTION.toString())) {
            return line.getDeduction();
        }
        else {
            return 0.0;
        }
    }

    public Double checkStorageFee(WB_DetailReport line) {
        if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.STORAGE.toString())) {
            return line.getStorageFee();
        }
        else {
            return 0.0;
        }
    }

    public Double checkLogistic(WB_DetailReport line) {
        if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.LOGISTIC.toString())) {
            return line.getDeliveryRub();
        }
        else if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.LOGISTICS_ADJUSTMENT.toString())) {
            return line.getDeliveryRub();
        }
        else {
            return 0.0;
        }
    }

    public Double checkLogisticStorno(WB_DetailReport line) {
        if (Objects.equals(line.getDocTypeName(), WB_AccrualType.RETURN.toString())) {
            if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.LOGISTIC_STORNO.toString())) {
                return line.getDeliveryRub();
            }
            else {
                return 0.0;
            }
        }
        else {
            return 0.0;
        }
    }

    @NotNull
    private Double commissionConditions(WB_DetailReport line) {
        if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.COMPENSATION_DAMAGE.toString())){
            return line.getPpvzForPay() - line.getRetailAmount();
        }
        else if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.ACQUIRING_ADJUSTMENT.toString())) {
            return line.getPpvzForPay() - line.getRetailAmount();
        }
        else if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.SALES_ADJUSTMENT.toString())) {
            return line.getPpvzForPay() - line.getRetailAmount();
        }
        else if (Objects.equals(line.getSupplierOperName(), WB_JustificationPayment.SALE.toString())) {
            return line.getPpvzForPay() - line.getRetailAmount();
        }
        else {
            return 0.0;
        }
    }

    public static Map<String, Long> getSalesCount(@NotNull List<WB_SaleReport> list) {
        Map<String, List<WB_SaleReport>> groupedMap = list.stream()
                .filter(x -> x.getSupplierArticle() != null && !x.getSupplierArticle().isEmpty())
                .collect(Collectors.groupingBy(
                        WB_SaleReport::getSupplierArticle,
                        Collectors.toList()
                ));

        return groupedMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(report -> report.getOrderType()
                                        .equals(WB_JustificationPayment.SALE.toString()))
                                .count()));
    }
}
