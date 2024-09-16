package com.example.consul.mapping;

import com.example.consul.document.models.YANDEX_TableRow;
import com.example.consul.mapping.sheets.*;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class YANDEX_dataProcessing {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Разгруппировка строк и запись названия заголовка ниже (если строка ниже пустая)
    private static void ungroupCells(Sheet sheet) {
        for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);

            int mergedRow = mergedRegion.getFirstRow();
            int mergedColumn = mergedRegion.getFirstColumn();

            Row row = sheet.getRow(mergedRow);
            Cell mergedCell = row.getCell(mergedColumn);

            if (mergedCell != null) {
                String cellValue = mergedCell.getStringCellValue();
                Row nextRow = sheet.getRow(mergedRow + 1);
                if (nextRow != null) {
                    Cell nextCell = nextRow.getCell(mergedColumn);
                    if (nextCell != null && nextCell.getCellType() == CellType.BLANK) {
                        nextCell.setCellValue(cellValue);
                        mergedCell.setCellValue("");
                    }
                }

                sheet.removeMergedRegion(i);
            }
        }
    }

    // Если на листе есть автофильтр, вернуть строку, где содержится автофильтр (нахождение расположения заголовков)
    private static Integer findAutofilterRow(Sheet sheet) {
        if (sheet instanceof XSSFSheet xssfSheet) {
            if (xssfSheet.getCTWorksheet().getAutoFilter() != null) {
                AreaReference ref = new AreaReference(xssfSheet.getCTWorksheet().getAutoFilter().getRef(), SpreadsheetVersion.EXCEL2007);
                CellReference firstCell = ref.getFirstCell();
                return firstCell.getRow();
            }
        }
        return null;
    }

    private static List<YANDEX_DeliveredGoods> getDeliveredGoods(Sheet sheet) {
        List<YANDEX_DeliveredGoods> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum() - 1; j++) {
            Row row = sheet.getRow(j);
            boolean hasWarehouseSku = row.getCell(4).getCellType() == CellType.STRING;
            YANDEX_DeliveredGoods data;

            if (hasWarehouseSku) {
                data = YANDEX_DeliveredGoods.builder()
                        .orderNumber((long) row.getCell(0).getNumericCellValue())
                        .orderType(row.getCell(1).getStringCellValue())
                        .productName(row.getCell(2).getStringCellValue())
                        .productSku(row.getCell(3).getStringCellValue())
                        .warehouseSku(row.getCell(4).getStringCellValue())
                        .quantityShipped((int) row.getCell(5).getNumericCellValue())
                        .quantityDelivered((int) row.getCell(6).getNumericCellValue())
                        .orderDate(LocalDate.parse(row.getCell(7).getStringCellValue(), formatter))
                        .shipmentDate(LocalDate.parse(row.getCell(8).getStringCellValue(), formatter))
                        .deliveryDate(LocalDate.parse(row.getCell(9).getStringCellValue(), formatter))
                        .paymentMethod(row.getCell(10).getStringCellValue())
                        .vatRate(row.getCell(11).getStringCellValue())
                        .priceWithoutDiscount(row.getCell(12).getNumericCellValue())
                        .marketplaceDiscount(row.getCell(13).getNumericCellValue())
                        .sberThankYouBonusDiscount(row.getCell(14).getNumericCellValue())
                        .yandexPlusPointsDiscount(row.getCell(15).getNumericCellValue())
                        .priceWithDiscount(row.getCell(16).getNumericCellValue())
                        .totalPriceWithoutDiscount(row.getCell(17).getNumericCellValue())
                        .totalDiscount(row.getCell(18).getNumericCellValue())
                        .totalPriceWithDiscount(row.getCell(19).getNumericCellValue())
                        .build();
            } else {
                YANDEX_DeliveredGoods.YANDEX_DeliveredGoodsBuilder dataBuilder = YANDEX_DeliveredGoods.builder()
                        .orderNumber((long) row.getCell(0).getNumericCellValue())
                        .orderType(row.getCell(1).getStringCellValue())
                        .productName(row.getCell(2).getStringCellValue())
                        .productSku(row.getCell(3).getStringCellValue())
                        .quantityShipped((int) row.getCell(4).getNumericCellValue())
                        .quantityDelivered((int) row.getCell(5).getNumericCellValue())
                        .orderDate(LocalDate.parse(row.getCell(6).getStringCellValue(), formatter))
                        .shipmentDate(LocalDate.parse(row.getCell(7).getStringCellValue(), formatter))
                        .deliveryDate(LocalDate.parse(row.getCell(8).getStringCellValue(), formatter))
                        .paymentMethod(row.getCell(9).getStringCellValue())
                        .vatRate(row.getCell(10).getStringCellValue())
                        .priceWithoutDiscount(row.getCell(11).getNumericCellValue())
                        .marketplaceDiscount(row.getCell(12).getNumericCellValue())
                        .sberThankYouBonusDiscount(row.getCell(13).getNumericCellValue())
                        .yandexPlusPointsDiscount(row.getCell(14).getNumericCellValue())
                        .priceWithDiscount(row.getCell(15).getNumericCellValue())
                        .totalPriceWithoutDiscount(row.getCell(16).getNumericCellValue())
                        .totalDiscount(row.getCell(17).getNumericCellValue())
                        .totalPriceWithDiscount(row.getCell(18).getNumericCellValue());
                data = dataBuilder.build();
            }

            list.add(data);
        }

        return list;
    }

    private static List<YANDEX_GoodsInDelivery> getGoodsInDelivery(Sheet sheet) {
        List<YANDEX_GoodsInDelivery> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);


        for (int j = headerRow + 1; j <= sheet.getLastRowNum() - 1; j++) {
            Row row = sheet.getRow(j);
            boolean hasWarehouseSku = row.getCell(4).getCellType() == CellType.STRING;
            YANDEX_GoodsInDelivery data;

            if (hasWarehouseSku) {
                data = YANDEX_GoodsInDelivery.builder()
                        .orderNumber((long) row.getCell(0).getNumericCellValue())
                        .orderType(row.getCell(1).getStringCellValue())
                        .productName(row.getCell(2).getStringCellValue())
                        .productSku(row.getCell(3).getStringCellValue())
                        .warehouseSku(row.getCell(4).getStringCellValue())
                        .quantityShipped((int) row.getCell(5).getNumericCellValue())
                        .orderDate(LocalDate.parse(row.getCell(6).getStringCellValue(), formatter))
                        .shipmentDate(LocalDate.parse(row.getCell(7).getStringCellValue(), formatter))
                        .deliveryDate(Objects.equals(row.getCell(8).getStringCellValue(), "") ? null : LocalDate.parse(row.getCell(8).getStringCellValue(), formatter))
                        .paymentMethod(row.getCell(9).getStringCellValue())
                        .vatRate(row.getCell(10).getStringCellValue())
                        .priceWithoutDiscount(row.getCell(11).getNumericCellValue())
                        .marketplaceDiscount(row.getCell(12).getNumericCellValue())
                        .sberThankYouBonusDiscount(row.getCell(13).getNumericCellValue())
                        .yandexPlusPointsDiscount(row.getCell(14).getNumericCellValue())
                        .priceWithDiscount(row.getCell(15).getNumericCellValue())
                        .totalPriceWithoutDiscount(row.getCell(16).getNumericCellValue())
                        .totalDiscount(row.getCell(17).getNumericCellValue())
                        .totalPriceWithDiscount(row.getCell(18).getNumericCellValue())
                        .build();
            } else {
                YANDEX_GoodsInDelivery.YANDEX_GoodsInDeliveryBuilder dataBuilder = YANDEX_GoodsInDelivery.builder()
                        .orderNumber((long) row.getCell(0).getNumericCellValue())
                        .orderType(row.getCell(1).getStringCellValue())
                        .productName(row.getCell(2).getStringCellValue())
                        .productSku(row.getCell(3).getStringCellValue())
                        .quantityShipped((int) row.getCell(4).getNumericCellValue())
                        .orderDate(LocalDate.parse(row.getCell(5).getStringCellValue(), formatter))
                        .shipmentDate(LocalDate.parse(row.getCell(6).getStringCellValue(), formatter))
                        .deliveryDate(Objects.equals(row.getCell(7).getStringCellValue(), "") ? null : LocalDate.parse(row.getCell(7).getStringCellValue(), formatter))
                        .paymentMethod(row.getCell(8).getStringCellValue())
                        .vatRate(row.getCell(9).getStringCellValue())
                        .priceWithoutDiscount(row.getCell(10).getNumericCellValue())
                        .marketplaceDiscount(row.getCell(11).getNumericCellValue())
                        .sberThankYouBonusDiscount(row.getCell(12).getNumericCellValue())
                        .yandexPlusPointsDiscount(row.getCell(13).getNumericCellValue())
                        .priceWithDiscount(row.getCell(14).getNumericCellValue())
                        .totalPriceWithoutDiscount(row.getCell(15).getNumericCellValue())
                        .totalDiscount(row.getCell(16).getNumericCellValue())
                        .totalPriceWithDiscount(row.getCell(17).getNumericCellValue());
                data = dataBuilder.build();
            }

            list.add(data);
        }

        return list;
    }

    private static List<YANDEX_ReturnedGoods> getReturnedGoods(Sheet sheet) {
        List<YANDEX_ReturnedGoods> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum() - 1; j++) {
            Row row = sheet.getRow(j);
            boolean hasWarehouseSku = row.getCell(4).getCellType() == CellType.STRING;
            YANDEX_ReturnedGoods data;

            if (hasWarehouseSku) {
                data = YANDEX_ReturnedGoods.builder()
                        .orderNumber((long) row.getCell(0).getNumericCellValue())
                        .orderType(row.getCell(1).getStringCellValue())
                        .productName(row.getCell(2).getStringCellValue())
                        .productSku(row.getCell(3).getStringCellValue())
                        .warehouseSku(row.getCell(4).getStringCellValue())
                        .quantityDelivered((int) row.getCell(5).getNumericCellValue())
                        .quantityReturned((int) row.getCell(6).getNumericCellValue())
                        .orderDate(LocalDate.parse(row.getCell(7).getStringCellValue(), formatter))
                        .shipmentDate(LocalDate.parse(row.getCell(8).getStringCellValue(), formatter))
                        .deliveryDate(LocalDate.parse(row.getCell(9).getStringCellValue(), formatter))
                        .returnReceiptDate(LocalDate.parse(row.getCell(10).getStringCellValue(), formatter))
                        .paymentMethod(row.getCell(11).getStringCellValue())
                        .vatRate(row.getCell(12).getStringCellValue())
                        .priceWithoutDiscount(row.getCell(13).getNumericCellValue())
                        .marketplaceDiscount(row.getCell(14).getNumericCellValue())
                        .sberThankYouBonusDiscount(row.getCell(15).getNumericCellValue())
                        .yandexPlusPointsDiscount(row.getCell(16).getNumericCellValue())
                        .priceWithDiscount(row.getCell(17).getNumericCellValue())
                        .totalReturnedPriceWithoutDiscount(row.getCell(18).getNumericCellValue())
                        .totalDiscountForReturnedItems(row.getCell(19).getNumericCellValue())
                        .totalReturnedPriceWithDiscount(row.getCell(20).getNumericCellValue())
                        .build();
            } else {
                data = YANDEX_ReturnedGoods.builder()
                        .orderNumber((long) row.getCell(0).getNumericCellValue())
                        .orderType(row.getCell(1).getStringCellValue())
                        .productName(row.getCell(2).getStringCellValue())
                        .productSku(row.getCell(3).getStringCellValue())
                        .quantityDelivered((int) row.getCell(4).getNumericCellValue())
                        .quantityReturned((int) row.getCell(5).getNumericCellValue())
                        .orderDate(LocalDate.parse(row.getCell(6).getStringCellValue(), formatter))
                        .shipmentDate(LocalDate.parse(row.getCell(7).getStringCellValue(), formatter))
                        .deliveryDate(LocalDate.parse(row.getCell(8).getStringCellValue(), formatter))
                        .returnReceiptDate(LocalDate.parse(row.getCell(9).getStringCellValue(), formatter))
                        .paymentMethod(row.getCell(10).getStringCellValue())
                        .vatRate(row.getCell(11).getStringCellValue())
                        .priceWithoutDiscount(row.getCell(12).getNumericCellValue())
                        .marketplaceDiscount(row.getCell(13).getNumericCellValue())
                        .sberThankYouBonusDiscount(row.getCell(14).getNumericCellValue())
                        .yandexPlusPointsDiscount(row.getCell(15).getNumericCellValue())
                        .priceWithDiscount(row.getCell(16).getNumericCellValue())
                        .totalReturnedPriceWithoutDiscount(row.getCell(17).getNumericCellValue())
                        .totalDiscountForReturnedItems(row.getCell(18).getNumericCellValue())
                        .totalReturnedPriceWithDiscount(row.getCell(19).getNumericCellValue())
                        .build();
            }
            list.add(data);
        }
        return list;
    }

    private static List<YANDEX_Shelves> getShelves(Sheet sheet) {
        List<YANDEX_Shelves> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            YANDEX_Shelves data = YANDEX_Shelves.builder()
                    .businessAccountId((long) row.getCell(0).getNumericCellValue())
                    .workModel(row.getCell(1).getStringCellValue())
                    .storeId((long) row.getCell(2).getNumericCellValue())
                    .storeName(row.getCell(3).getStringCellValue())
                    .inn(row.getCell(4).getStringCellValue())
                    .placementContractNumber(row.getCell(5).getStringCellValue())
                    .promotionContractNumber(row.getCell(6).getStringCellValue())
                    .advertiserId((long) row.getCell(7).getNumericCellValue())
                    .companyNumber((long) row.getCell(8).getNumericCellValue())
                    .companyName(row.getCell(9).getStringCellValue())
                    .serviceType(row.getCell(10).getStringCellValue())
                    .quantityShows((int) row.getCell(11).getNumericCellValue())
                    .budgetType(row.getCell(12).getStringCellValue())
                    .budgetVolume(row.getCell(13).getNumericCellValue())
                    .serviceDateTime(LocalDateTime.parse(row.getCell(14).getStringCellValue(), formatterTime))
                    .actFormationDate(LocalDate.parse(row.getCell(15).getStringCellValue(), formatterDate))
                    .bonus(row.getCell(16).getNumericCellValue())
                    .serviceCost(row.getCell(17).getNumericCellValue())
                    .build();

            list.add(data);
        }

        return list;
    }

    private static List<YANDEX_BoostSales> getBoostSales(Sheet sheet) {
        List<YANDEX_BoostSales> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            YANDEX_BoostSales data = YANDEX_BoostSales.builder()
                    .businessAccountId((long) row.getCell(0).getNumericCellValue())
                    .workModel(row.getCell(1).getStringCellValue())
                    .storeId((long) row.getCell(2).getNumericCellValue())
                    .storeName(row.getCell(3).getStringCellValue())
                    .inn(row.getCell(4).getStringCellValue())
                    .placementContractNumber(row.getCell(5).getStringCellValue())
                    .promotionContractNumber(row.getCell(6).getStringCellValue())
                    .orderNumber((long) row.getCell(7).getNumericCellValue())
                    .sku(row.getCell(8).getStringCellValue())
                    .productName(row.getCell(9).getStringCellValue())
                    .category(row.getCell(10).getStringCellValue())
                    .pricePerUnit(row.getCell(11).getNumericCellValue())
                    .quantity((int) row.getCell(12).getNumericCellValue())
                    .service(row.getCell(13).getStringCellValue())
                    .ratePercentage(row.getCell(14).getNumericCellValue())
                    .prepayment(row.getCell(15) != null ? row.getCell(15).getNumericCellValue() : 0.0)
                    .postpayment(row.getCell(16) != null ? row.getCell(16).getNumericCellValue() : 0.0)
                    .paymentBonuses(row.getCell(17) != null ? row.getCell(17).getNumericCellValue() : 0.0)
                    .actFormationDate(LocalDate.parse(row.getCell(18).getStringCellValue(), formatterDate))
                    .serviceDateTime(LocalDateTime.parse(row.getCell(19).getStringCellValue(), formatterTime))
                    .build();

            list.add(data);
        }

        return list;
    }

    private static List<YANDEX_LoyaltyProgram> getLoyaltyProgram(Sheet sheet) {
        List<YANDEX_LoyaltyProgram> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            YANDEX_LoyaltyProgram data = YANDEX_LoyaltyProgram.builder()
                    .businessAccountId((long) row.getCell(0).getNumericCellValue())
                    .workModel(row.getCell(1).getStringCellValue())
                    .storeId((long) row.getCell(2).getNumericCellValue())
                    .storeName(row.getCell(3).getStringCellValue())
                    .inn(row.getCell(4).getStringCellValue())
                    .placementContractNumber(row.getCell(5).getStringCellValue())
                    .promotionContractNumber(row.getCell(6).getStringCellValue())
                    .orderNumber((long) row.getCell(7).getNumericCellValue())
                    .sku(row.getCell(8).getStringCellValue())
                    .productName(row.getCell(9).getStringCellValue())
                    .pricePerUnit(row.getCell(10).getNumericCellValue())
                    .userPaid(row.getCell(11).getNumericCellValue())
                    .quantity((int) row.getCell(12).getNumericCellValue())
                    .service(row.getCell(13).getStringCellValue())
                    .reviewId((long) row.getCell(14).getNumericCellValue())
                    .tariffPerUnit(row.getCell(15).getNumericCellValue())
                    .measurementUnit(row.getCell(16).getStringCellValue())
                    .serviceDateTime(LocalDateTime.parse(row.getCell(17).getStringCellValue(), formatterTime))
                    .actFormationDate(LocalDate.parse(row.getCell(18).getStringCellValue(), formatterDate))
                    .serviceCost(row.getCell(19).getNumericCellValue())
                    .build();

            list.add(data);
        }

        return list;
    }

    private static List<YANDEX_ShowPlacement> getShowPlacement(Sheet sheet) {
        List<YANDEX_ShowPlacement> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);

        for (int j = headerRow + 2; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            YANDEX_ShowPlacement data = YANDEX_ShowPlacement.builder()
                    .businessAccountId((long) row.getCell(0).getNumericCellValue())
                    .workModel(row.getCell(1).getStringCellValue())
                    .storeId((long) row.getCell(2).getNumericCellValue())
                    .storeName(row.getCell(3).getStringCellValue())
                    .inn(row.getCell(4).getStringCellValue())
                    .placementContractNumber(row.getCell(5).getStringCellValue())
                    .promotionContractNumber(row.getCell(6).getStringCellValue())
                    .orderNumber((long) row.getCell(7).getNumericCellValue())
                    .orderCreationDate(LocalDateTime.parse(row.getCell(8).getStringCellValue(), formatterTime))
                    .sku(row.getCell(9).getStringCellValue())
                    .productName(row.getCell(10).getStringCellValue())
                    .productType(row.getCell(11).getStringCellValue())
                    .pricePerUnit(row.getCell(12).getNumericCellValue())
                    .priceDifference(row.getCell(13).getNumericCellValue())
                    .quantity((int) row.getCell(14).getNumericCellValue())
                    .salesQuantum((int) row.getCell(15).getNumericCellValue())
                    .quantumsInOrder((int) row.getCell(16).getNumericCellValue())
                    .pricePerQuantum(row.getCell(17).getNumericCellValue())
                    .weightKg(row.getCell(18).getNumericCellValue())
                    .lengthCm(row.getCell(19).getNumericCellValue())
                    .widthCm(row.getCell(20).getNumericCellValue())
                    .heightCm(row.getCell(21).getNumericCellValue())
                    .sumOfDimensions(row.getCell(22).getNumericCellValue())
                    .paymentMethod(row.getCell(23).getStringCellValue())
                    .qualityIndex(row.getCell(24).getStringCellValue())
                    .service(row.getCell(25).getStringCellValue())
                    .tariffCondition(row.getCell(26).getStringCellValue())
                    .tariffPerUnit(row.getCell(27).getNumericCellValue())
                    .measurementUnit(row.getCell(28).getStringCellValue())
                    .minTariffPerUnit(row.getCell(29).getNumericCellValue())
                    .maxTariffPerUnit(row.getCell(30).getNumericCellValue())
                    .serviceCostBeforeMinTariff(row.getCell(31).getNumericCellValue())
                    .serviceDateTime(LocalDateTime.parse(row.getCell(32).getStringCellValue(), formatterTime))
                    .actFormationDate(LocalDate.parse(row.getCell(33).getStringCellValue(), formatterDate))
                    .serviceCostWithoutDiscounts(row.getCell(34).getNumericCellValue())
                    .tariffPercent(row.getCell(35).getNumericCellValue())
                    .discount(row.getCell(36).getNumericCellValue())
                    .lateDeliveryPenaltyPercent(row.getCell(37).getNumericCellValue())
                    .sellerFaultPenaltyPercent(row.getCell(38).getNumericCellValue())
                    .minCostPerUnit(row.getCell(39).getNumericCellValue())
                    .maxCostPerUnit(row.getCell(40).getNumericCellValue())
                    .serviceCostChange1(row.getCell(41).getNumericCellValue())
                    .tariff(row.getCell(42).getNumericCellValue())
                    .serviceCostChange2(row.getCell(43).getNumericCellValue())
                    .individualServiceDiscount(row.getCell(44).getNumericCellValue())
                    .loyaltyDiscount(row.getCell(45).getNumericCellValue())
                    .serviceCost(row.getCell(46).getNumericCellValue())
                    .build();

            list.add(data);
        }

        return list;
    }

    private static List<YANDEX_DeliveryCustomer> getDeliveryCustomer(Sheet sheet) {
        List<YANDEX_DeliveryCustomer> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);

        for (int j = headerRow + 1; j <= sheet.getPhysicalNumberOfRows()-1; j++) {
            Row row = sheet.getRow(j);
            YANDEX_DeliveryCustomer data = YANDEX_DeliveryCustomer.builder()
                    .businessAccountId((long) row.getCell(0).getNumericCellValue())
                    .workModel(row.getCell(1).getStringCellValue())
                    .storeId((long) row.getCell(2).getNumericCellValue())
                    .storeName(row.getCell(3).getStringCellValue())
                    .inn(row.getCell(4).getStringCellValue())
                    .placementContractNumber(row.getCell(5).getStringCellValue())
                    .promotionContractNumber(row.getCell(6).getStringCellValue())
                    .orderNumber((long) row.getCell(7).getNumericCellValue())
                    .sku(row.getCell(8).getStringCellValue())
                    .productName(row.getCell(9).getStringCellValue())
                    .pricePerUnit(row.getCell(10).getNumericCellValue())
                    .quantity((int) row.getCell(11).getNumericCellValue())
                    .saleQuantum(row.getCell(12) != null ? row.getCell(12).getNumericCellValue() : 0.0)
                    .quantumInOrder(row.getCell(13) != null ? row.getCell(13).getNumericCellValue() : 0.0)
                    .pricePerQuantum(row.getCell(14) != null ? row.getCell(14).getNumericCellValue() : 0.0)
                    .weightKg(row.getCell(15) != null ? row.getCell(15).getNumericCellValue() : 0.0)
                    .volumeWeightKg(row.getCell(16) != null ? row.getCell(16).getNumericCellValue() : 0.0)
                    .lengthCm(row.getCell(17) != null ? row.getCell(17).getNumericCellValue() : 0.0)
                    .widthCm(row.getCell(18) != null ? row.getCell(18).getNumericCellValue() : 0.0)
                    .heightCm(row.getCell(19) != null ? row.getCell(19).getNumericCellValue() : 0.0)
                    .sumOfDimensions(row.getCell(20) != null ? row.getCell(20).getNumericCellValue() : 0.0)
                    .localSalesShare(row.getCell(21) != null ? row.getCell(21).getNumericCellValue() : 0.0)
                    .service(row.getCell(22).getStringCellValue())
                    .fromLocation(row.getCell(23).getStringCellValue())
                    .toLocation(row.getCell(24).getStringCellValue())
                    .tariffPerUnit(row.getCell(25).getNumericCellValue())
                    .measurementUnit(row.getCell(26).getStringCellValue())
                    .minTariffPerUnit(row.getCell(27) != null ? row.getCell(27).getNumericCellValue() : 0.0)
                    .maxTariffPerUnit(row.getCell(28) != null ? row.getCell(28).getNumericCellValue() : 0.0)
                    .serviceCostWithoutLimits(row.getCell(29) != null ? row.getCell(29).getNumericCellValue() : 0.0)
                    .localityCoefficient(row.getCell(30) != null ? row.getCell(30).getNumericCellValue() : 0.0)
                    .serviceDateTime(LocalDateTime.parse(row.getCell(31).getStringCellValue(), formatterTime))
                    .actFormationDate(LocalDate.parse(row.getCell(32).getStringCellValue(), formatterDate))
                    .serviceCost(row.getCell(33).getNumericCellValue())
                    .build();

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_AcceptingPayment> getAcceptingPayment(Sheet sheet) {
        List<YANDEX_AcceptingPayment> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);

        for (int j = headerRow + 1; j <= sheet.getPhysicalNumberOfRows()-1; j++) {
            Row row = sheet.getRow(j);
            YANDEX_AcceptingPayment data = YANDEX_AcceptingPayment.builder()
                    .businessAccountId((long) row.getCell(0).getNumericCellValue())
                    .workModel(row.getCell(1).getStringCellValue())
                    .storeId((long) row.getCell(2).getNumericCellValue())
                    .storeName(row.getCell(3).getStringCellValue())
                    .inn(row.getCell(4).getStringCellValue())
                    .placementContractNumber(row.getCell(5).getStringCellValue())
                    .promotionContractNumber(row.getCell(6).getStringCellValue())
                    .orderNumber((long) row.getCell(7).getNumericCellValue())
                    .sku(row.getCell(8).getStringCellValue())
                    .productName(row.getCell(9).getStringCellValue())
                    .userPaid(row.getCell(10).getNumericCellValue())
                    .tariff(row.getCell(11).getNumericCellValue())
                    .tariffUnit(row.getCell(12).getStringCellValue())
                    .serviceDateTime(LocalDateTime.parse(row.getCell(13).getStringCellValue(), formatterTime))
                    .actFormationDate(LocalDate.parse(row.getCell(14).getStringCellValue(), formatterDate))
                    .serviceCost(row.getCell(15).getNumericCellValue())
                    .type(row.getCell(16).getStringCellValue())
                    .build();

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_TransferPayment> getTransferPayment(Sheet sheet) {
        List<YANDEX_TransferPayment> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);

        for (int j = headerRow + 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            YANDEX_TransferPayment data = YANDEX_TransferPayment.builder()
                    .businessAccountId((long) row.getCell(0).getNumericCellValue())
                    .workModel(row.getCell(1).getStringCellValue())
                    .storeId((long) row.getCell(2).getNumericCellValue())
                    .storeName(row.getCell(3).getStringCellValue())
                    .inn(row.getCell(4).getStringCellValue())
                    .placementContractNumber(row.getCell(5).getStringCellValue())
                    .promotionContractNumber(row.getCell(6).getStringCellValue())
                    .orderNumber((long) row.getCell(7).getNumericCellValue())
                    .sku(row.getCell(8).getStringCellValue())
                    .productName(row.getCell(9).getStringCellValue())
                    .userPaid(row.getCell(10).getNumericCellValue())
                    .tariff(row.getCell(11).getNumericCellValue())
                    .serviceDateTime(LocalDateTime.parse(row.getCell(12).getStringCellValue(), formatterTime))
                    .actFormationDate(LocalDate.parse(row.getCell(13).getStringCellValue(), formatterDate))
                    .serviceCost(row.getCell(14).getNumericCellValue())
                    .type(row.getCell(15).getStringCellValue())
                    .build();

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_ProcessingOrders> getProcessingOrders(Sheet sheet) {
        List<YANDEX_ProcessingOrders> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);

        for (int j = headerRow + 1; j <= sheet.getPhysicalNumberOfRows()-1; j++) {
            Row row = sheet.getRow(j);
            YANDEX_ProcessingOrders transferPayment = YANDEX_ProcessingOrders.builder()
                    .businessAccountId((long) row.getCell(0).getNumericCellValue())
                    .workModel(row.getCell(1).getStringCellValue())
                    .storeId((long) row.getCell(2).getNumericCellValue())
                    .storeName(row.getCell(3).getStringCellValue())
                    .inn(row.getCell(4).getStringCellValue())
                    .placementContractNumber(row.getCell(5).getStringCellValue())
                    .promotionContractNumber(row.getCell(6).getStringCellValue())
                    .orderNumber((long) row.getCell(7).getNumericCellValue())
                    .returnShippingNumber(row.getCell(8) != null ? row.getCell(8).getStringCellValue() : "")
                    .shippingPlace(row.getCell(9) != null ? row.getCell(9).getStringCellValue() : "")
                    .service(row.getCell(10) != null ? row.getCell(10).getStringCellValue() : "")
                    .tariff(row.getCell(11).getNumericCellValue())
                    .minAmount(row.getCell(12) != null ? row.getCell(12).getNumericCellValue() : 0)
                    .serviceDateTime(LocalDateTime.parse(row.getCell(13).getStringCellValue(), formatterTime))
                    .actFormationDate(LocalDate.parse(row.getCell(14).getStringCellValue(), formatterDate))
                    .serviceCost(row.getCell(15).getNumericCellValue())
                    .type(row.getCell(16).getStringCellValue())
                    .build();

            list.add(transferPayment);
        }

        return list;
    }

    public static List<YANDEX_TransactionsOrdersAndProducts> getTransactionsOnOrdersAndProducts(Sheet sheet) {
        List<YANDEX_TransactionsOrdersAndProducts> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);

        for (int j = headerRow + 1; j <= sheet.getPhysicalNumberOfRows()-1; j++) {
            Row row = sheet.getRow(j);
            YANDEX_TransactionsOrdersAndProducts data = YANDEX_TransactionsOrdersAndProducts.builder()
                    .businessAccountId((long) row.getCell(0).getNumericCellValue())
                    .workModel(row.getCell(1).getStringCellValue())
                    .storeId((long) row.getCell(2).getNumericCellValue())
                    .storeName(row.getCell(3).getStringCellValue())
                    .inn(row.getCell(4).getStringCellValue())
                    .placementContractNumber(row.getCell(5).getStringCellValue())
                    .promotionContractNumber(row.getCell(6).getStringCellValue())
                    .orderNumber((long) row.getCell(7).getNumericCellValue())
                    .storeOrderNumber(row.getCell(8) != null ? row.getCell(8).getStringCellValue() : "")
                    .registrationDate(LocalDate.parse(row.getCell(9).getStringCellValue(), formatter))
                    .orderType(row.getCell(10).getStringCellValue())
                    .sku(row.getCell(11).getStringCellValue())
                    .build();

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_StorageReturns> getStorageReturns(Sheet sheet) {
        List<YANDEX_StorageReturns> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);

        for (int j = headerRow + 1; j <= sheet.getPhysicalNumberOfRows()-1; j++) {
            Row row = sheet.getRow(j);
            YANDEX_StorageReturns data = YANDEX_StorageReturns.builder()
                    .businessAccountId((long) row.getCell(0).getNumericCellValue())
                    .workModel(row.getCell(1).getStringCellValue())
                    .storeId((long) row.getCell(2).getNumericCellValue())
                    .storeName(row.getCell(3).getStringCellValue())
                    .inn(row.getCell(4).getStringCellValue())
                    .placementContractNumber(row.getCell(5).getStringCellValue())
                    .promotionContractNumber(row.getCell(6).getStringCellValue())
                    .typeOfState(row.getCell(7).getStringCellValue())
                    .orderNumber((long) row.getCell(8).getNumericCellValue())
                    .returnNumber((long) row.getCell(9).getNumericCellValue())
                    .returnCount((int) row.getCell(10).getNumericCellValue())
                    .tariffNonPurchase(row.getCell(11).getNumericCellValue())
                    .tariffReturn(row.getCell(12).getNumericCellValue())
                    .serviceDateTime(LocalDateTime.parse(row.getCell(13).getStringCellValue(), formatterTime))
                    .actFormationDate(LocalDate.parse(row.getCell(14).getStringCellValue(), formatterDate))
                    .serviceCost(row.getCell(15).getNumericCellValue())
                    .type(row.getCell(16).getStringCellValue())
                    .build();

            list.add(data);
        }

        return list;
    }

    public static List<YANDEX_TableRow> getDataFromInputStream(InputStream inputStreamService, InputStream inputStreamRealization, InputStream inputStreamOrders) throws IOException {
        Workbook wbService = WorkbookFactory.create(inputStreamService);

        final Sheet[] sheetService = {
                wbService.getSheetAt(1),
                wbService.getSheetAt(3),
                wbService.getSheetAt(5),
                wbService.getSheetAt(9),
                wbService.getSheetAt(12),
                wbService.getSheetAt(19),
                wbService.getSheetAt(13),
                wbService.getSheetAt(22),
                wbService.getSheetAt(7)
        };

        Workbook wbRealization = WorkbookFactory.create(inputStreamRealization);

        final Sheet[] sheetRealization = {
                wbRealization.getSheetAt(2),
                wbRealization.getSheetAt(4),
                wbRealization.getSheetAt(1)
        };

        Workbook wbOrders = WorkbookFactory.create(inputStreamOrders);

        final Sheet[] sheetOrders = {
                wbOrders.getSheetAt(1)
        };

        CompletableFuture<Map<String, Double>> placingOnShowcaseCompletableFuture = CompletableFuture
                .supplyAsync(() -> {
                    ungroupCells(sheetService[0]);
                    return getMapPlacingOnShowcase(sheetService[0]);
                });

        CompletableFuture<Map<String, Double>> loyaltyProgramCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapLoyaltyProgram(sheetService[1]));

        CompletableFuture<Map<String, Double>> boostSalesCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapBoostSales(sheetService[2]));

        CompletableFuture<Map<String, Double>> deliveryToConsumerCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapDeliveryToConsumer(sheetService[3]));

        CompletableFuture<Map<String, Double>> acceptAndTransferPaymentCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapAcceptAndTransferPayment(
                        sheetService[4],
                        sheetService[6]
                ));

        CompletableFuture<Map<String, Double>> shelvesCompletableFuture = CompletableFuture
                .supplyAsync(() -> calculateServiceCostRatio(
                        sheetRealization[0],
                        sheetRealization[1],
                        sheetService[8]
                ));

        CompletableFuture<Map<String, Double>> favorSortingCenterPaymentCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapSortingCenter(
                        sheetService[3],
                        sheetRealization[2],
                        sheetService[5],
                        sheetService[4],
                        sheetOrders[0]
                ));

        CompletableFuture<Map<String, Double>> storageReturnCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapStorageReturn(
                        sheetService[3],
                        sheetRealization[2],
                        sheetService[7],
                        sheetService[4],
                        sheetOrders[0]
                ));

        CompletableFuture<Map<String, Double>> deliveredCostCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapDeliveryCost(sheetRealization[0]));

        CompletableFuture<Map<String, Integer>> deliveredCountCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapDeliveryCount(sheetRealization[0]));

//        CompletableFuture<Map<String, Double>> marketplaceDiscountCompletableFuture = CompletableFuture
//                .supplyAsync(() -> getMapMarketplaceDiscount(sheetRealization[0]));

        CompletableFuture<Map<String, Integer>> returnCountCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapReturnCount(sheetRealization[1]));

        CompletableFuture<Map<String, Double>> returnCostCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapReturnCost(sheetRealization[1]));

        Map<String, Double> placingOnShowcase = placingOnShowcaseCompletableFuture.join();
        Map<String, Double> loyaltyProgram = loyaltyProgramCompletableFuture.join();
        Map<String, Double> boostSales = boostSalesCompletableFuture.join();
        Map<String, Double> deliveryToConsumer = deliveryToConsumerCompletableFuture.join();
        Map<String, Double> acceptAndTransferPayment = acceptAndTransferPaymentCompletableFuture.join();
        Map<String, Double> favorSortingCenterPayment = favorSortingCenterPaymentCompletableFuture.join();
        Map<String, Double> deliveredCost = deliveredCostCompletableFuture.join();
        Map<String, Integer> deliveredCount = deliveredCountCompletableFuture.join();
        Map<String, Integer> returnCount = returnCountCompletableFuture.join();
        Map<String, Double> returnCost = returnCostCompletableFuture.join();
//        Map<String, Double> marketplaceDiscount = marketplaceDiscountCompletableFuture.join();
        Map<String, Double> storageReturn = storageReturnCompletableFuture.join();
        Map<String, Double> shelves = shelvesCompletableFuture.join();

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(acceptAndTransferPayment.keySet());
        allKeys.addAll(deliveredCount.keySet());
        allKeys.addAll(deliveredCost.keySet());
        allKeys.addAll(returnCount.keySet());
        allKeys.addAll(returnCost.keySet());
        allKeys.addAll(placingOnShowcase.keySet());
        allKeys.addAll(deliveryToConsumer.keySet());
        allKeys.addAll(favorSortingCenterPayment.keySet());
        allKeys.addAll(storageReturn.keySet());
        allKeys.addAll(loyaltyProgram.keySet());
        allKeys.addAll(boostSales.keySet());
        allKeys.addAll(shelves.keySet());
//        allKeys.addAll(marketplaceDiscount.keySet());

        Map<String, List<Object>> mergedMap = new HashMap<>();

        for (String key : allKeys) {
            mergedMap.put(key, Arrays.asList(
                    deliveredCount.getOrDefault(key, 0).doubleValue(),
                    deliveredCost.getOrDefault(key, 0.0),
                    returnCount.getOrDefault(key, 0).doubleValue(),
                    returnCost.getOrDefault(key, 0.0),
                    placingOnShowcase.getOrDefault(key, 0.0),
                    deliveryToConsumer.getOrDefault(key, 0.0),
                    acceptAndTransferPayment.getOrDefault(key, 0.0),
                    favorSortingCenterPayment.getOrDefault(key, 0.0),
                    storageReturn.getOrDefault(key, 0.0),
                    0.0, // Placeholder for "Расходы на рекламные кампании"
                    loyaltyProgram.getOrDefault(key, 0.0),
                    boostSales.getOrDefault(key, 0.0),
                    shelves.getOrDefault(key, 0.0)
//                    marketplaceDiscount.getOrDefault(key, 0.0)
            ));
        }

        return mergedMap.entrySet().stream().map(entry -> {
            List<Object> values = entry.getValue();
            return new YANDEX_TableRow(
                    entry.getKey(),
                    (Double) values.get(0), // deliveryCount
                    (Double) values.get(1), // accrued
                    (Double) values.get(2), // returnCount
                    (Double) values.get(3), // returnCost
                    (Double) values.get(4), // showcasePlacing
                    (Double) values.get(5), // deliveryToConsumer
                    (Double) values.get(6), // acceptAndTransferPayment
                    (Double) values.get(7), // favorSorting
                    (Double) values.get(8), // unredeemedStorage
                    (Double) values.get(9), // adCampaignCost
                    (Double) values.get(10), // loyaltyProgram
                    (Double) values.get(11), // boostSales
                    (Double) values.get(12) //shelves
//                    (Double) values.get(12)  // promotionFavor
            );
        }).toList();
    }

    public static Map<String, Double> getMapSortingCenter(Sheet sheetDelivery, Sheet sheetShip, Sheet sheetSortingCenter, Sheet sheetAcceptPay, Sheet sheetTransact) {
        CompletableFuture<List<YANDEX_AcceptingPayment>> acceptFuture = CompletableFuture.supplyAsync(() -> getAcceptingPayment(sheetAcceptPay));
        CompletableFuture<List<YANDEX_DeliveryCustomer>> deliveryFuture = CompletableFuture.supplyAsync(() -> getDeliveryCustomer(sheetDelivery));
        CompletableFuture<List<YANDEX_GoodsInDelivery>> goodsFuture = CompletableFuture.supplyAsync(() -> getGoodsInDelivery(sheetShip));
        CompletableFuture<List<YANDEX_TransactionsOrdersAndProducts>> transactFuture = CompletableFuture.supplyAsync(() -> getTransactionsOnOrdersAndProducts(sheetTransact));
        CompletableFuture<List<YANDEX_ProcessingOrders>> sortingFuture = CompletableFuture.supplyAsync(() -> getProcessingOrders(sheetSortingCenter));

        try {
            List<YANDEX_AcceptingPayment> listAccept = acceptFuture.get();
            List<YANDEX_DeliveryCustomer> listDel = deliveryFuture.get();
            List<YANDEX_GoodsInDelivery> listDelivery = goodsFuture.get();
            List<YANDEX_TransactionsOrdersAndProducts> listTransact = transactFuture.get();
            List<YANDEX_ProcessingOrders> listSorting = sortingFuture.get();

            Map<Long, Set<String>> orderNumberToSkuSetMap = Stream.of(listDel, listAccept, listDelivery, listTransact)
                    .flatMap(List::stream)
                    .parallel()
                    .collect(Collectors.toConcurrentMap(
                            YANDEX_dataProcessing::getOrderNumberFromRecord,
                            record -> new HashSet<>(Collections.singletonList(getSkuFromRecord(record))),
                            (existing, replacement) -> {
                                existing.addAll(replacement);
                                return existing;
                            },
                            ConcurrentHashMap::new
                    ));

            Map<Long, Double> orderNumberToTotalTariffMap = listSorting.parallelStream()
                    .collect(Collectors.groupingBy(
                            YANDEX_ProcessingOrders::getOrderNumber,
                            Collectors.summingDouble(YANDEX_ProcessingOrders::getTariff)
                    ));

            Map<String, Double> skuToFinalTariffMap = new ConcurrentHashMap<>();

            orderNumberToSkuSetMap.forEach((orderNumber, skuSet) -> {
                Double totalTariff = orderNumberToTotalTariffMap.getOrDefault(orderNumber, 0.0);
                if (totalTariff > 0 && !skuSet.isEmpty()) {
                    double dividedTariff = totalTariff / skuSet.size();
                    skuSet.forEach(sku -> skuToFinalTariffMap.merge(sku, dividedTariff, Double::sum));
                }
            });

            skuToFinalTariffMap.replaceAll((sku, value) -> Math.round(value * 100.0) / 100.0);

            return skuToFinalTariffMap;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while processing data", e);
        }
    }

    private static Long getOrderNumberFromRecord(Object record) {
        if (record instanceof YANDEX_DeliveryCustomer) {
            return ((YANDEX_DeliveryCustomer) record).getOrderNumber();
        } else if (record instanceof YANDEX_AcceptingPayment) {
            return ((YANDEX_AcceptingPayment) record).getOrderNumber();
        } else if (record instanceof YANDEX_GoodsInDelivery) {
            return ((YANDEX_GoodsInDelivery) record).getOrderNumber();
        } else if (record instanceof YANDEX_TransactionsOrdersAndProducts) {
            return ((YANDEX_TransactionsOrdersAndProducts) record).getOrderNumber();
        }
        throw new IllegalArgumentException("Unknown record type");
    }

    private static String getSkuFromRecord(Object record) {
        if (record instanceof YANDEX_DeliveryCustomer) {
            return ((YANDEX_DeliveryCustomer) record).getSku();
        } else if (record instanceof YANDEX_AcceptingPayment) {
            return ((YANDEX_AcceptingPayment) record).getSku();
        } else if (record instanceof YANDEX_GoodsInDelivery) {
            return ((YANDEX_GoodsInDelivery) record).getProductSku();
        } else if (record instanceof YANDEX_TransactionsOrdersAndProducts) {
            return ((YANDEX_TransactionsOrdersAndProducts) record).getSku();
        }
        throw new IllegalArgumentException("Unknown record type");
    }

    public static Map<String, Double> getMapStorageReturn(Sheet sheetDelivery, Sheet sheetShip, Sheet sheetStorageReturn, Sheet sheetAcceptPay, Sheet sheetTransact) {
        CompletableFuture<List<YANDEX_AcceptingPayment>> acceptFuture = CompletableFuture.supplyAsync(() -> getAcceptingPayment(sheetAcceptPay));
        CompletableFuture<List<YANDEX_DeliveryCustomer>> deliveryFuture = CompletableFuture.supplyAsync(() -> getDeliveryCustomer(sheetDelivery));
        CompletableFuture<List<YANDEX_StorageReturns>> storageReturnFuture = CompletableFuture.supplyAsync(() -> getStorageReturns(sheetStorageReturn));
        CompletableFuture<List<YANDEX_GoodsInDelivery>> goodsFuture = CompletableFuture.supplyAsync(() -> getGoodsInDelivery(sheetShip));
        CompletableFuture<List<YANDEX_TransactionsOrdersAndProducts>> transactFuture = CompletableFuture.supplyAsync(() -> getTransactionsOnOrdersAndProducts(sheetTransact));

        try {
            List<YANDEX_AcceptingPayment> listAccept = acceptFuture.get();
            List<YANDEX_DeliveryCustomer> listDel = deliveryFuture.get();
            List<YANDEX_StorageReturns> listSorting = storageReturnFuture.get();
            List<YANDEX_GoodsInDelivery> listDelivery = goodsFuture.get();
            List<YANDEX_TransactionsOrdersAndProducts> listTransact = transactFuture.get();

            Map<Long, List<String>> orderNumberToSkuListMap = Stream.of(listDel, listAccept, listDelivery, listTransact)
                    .flatMap(List::stream)
                    .parallel()
                    .collect(Collectors.groupingBy(
                            YANDEX_dataProcessing::getOrderNumberFromRecord,
                            ConcurrentHashMap::new,
                            Collectors.mapping(YANDEX_dataProcessing::getSkuFromRecord, Collectors.toList())
                    ));

            Map<Long, Double> orderNumberToTotalTariffMap = listSorting.parallelStream()
                    .collect(Collectors.groupingBy(
                            YANDEX_StorageReturns::getOrderNumber,
                            Collectors.summingDouble(YANDEX_StorageReturns::getServiceCost)
                    ));

            Map<String, Double> skuToFinalTariffMap = new ConcurrentHashMap<>();

            orderNumberToSkuListMap.forEach((orderNumber, skuList) -> {
                Double totalTariff = orderNumberToTotalTariffMap.getOrDefault(orderNumber, 0.0);
                if (totalTariff > 0 && !skuList.isEmpty()) {
                    double dividedTariff = totalTariff / skuList.size();
                    skuList.forEach(sku -> skuToFinalTariffMap.merge(sku, dividedTariff, Double::sum));
                }
            });

            skuToFinalTariffMap.replaceAll((sku, value) -> Math.round(value * 100.0) / 100.0);

            return skuToFinalTariffMap;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while processing data", e);
        }
    }

    public static Map<String, Double> getMapDeliveryCost(Sheet sheet) {
        List<YANDEX_DeliveredGoods> list = getDeliveredGoods(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_DeliveredGoods::getProductSku,
                        Collectors.summingDouble(YANDEX_DeliveredGoods::getTotalPriceWithDiscount)));
    }

    @Deprecated
    public static Map<String, Double> getMapMarketplaceDiscount(Sheet sheet) {
        List<YANDEX_DeliveredGoods> list = getDeliveredGoods(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_DeliveredGoods::getProductSku,
                        Collectors.summingDouble(YANDEX_DeliveredGoods::getTotalDiscount)));
    }

    public static Map<String, Integer> getMapDeliveryCount(Sheet sheet) {
        List<YANDEX_DeliveredGoods> list = getDeliveredGoods(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_DeliveredGoods::getProductSku,
                        Collectors.summingInt(YANDEX_DeliveredGoods::getQuantityDelivered)));
    }

    public static Map<String, Double> getMapReturnCost(Sheet sheet) {
        List<YANDEX_ReturnedGoods> list = getReturnedGoods(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_ReturnedGoods::getProductSku,
                        Collectors.summingDouble(YANDEX_ReturnedGoods::getPriceWithDiscount)));
    }

    public static Map<String, Integer> getMapReturnCount(Sheet sheet) {
        List<YANDEX_ReturnedGoods> list = getReturnedGoods(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_ReturnedGoods::getProductSku,
                        Collectors.summingInt(YANDEX_ReturnedGoods::getQuantityReturned)));
    }

    public static Map<String, Double> getMapPlacingOnShowcase(Sheet sheet) {
        List<YANDEX_ShowPlacement> list = getShowPlacement(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_ShowPlacement::getSku,
                        Collectors.summingDouble(YANDEX_ShowPlacement::getServiceCost)));
    }

    public static Map<String, Double> getMapDeliveryToConsumer(Sheet sheet) {
        List<YANDEX_DeliveryCustomer> list = getDeliveryCustomer(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_DeliveryCustomer::getSku,
                        Collectors.summingDouble(YANDEX_DeliveryCustomer::getServiceCost)));
    }

    public static Map<String, Double> getMapAcceptAndTransferPayment(Sheet sheetAccept, Sheet sheetTrans) {
        CompletableFuture<List<YANDEX_AcceptingPayment>> acceptPaymentsFuture = CompletableFuture.supplyAsync(() -> getAcceptingPayment(sheetAccept));
        CompletableFuture<List<YANDEX_TransferPayment>> transferPaymentsFuture = CompletableFuture.supplyAsync(() -> getTransferPayment(sheetTrans));

        try {
            List<YANDEX_AcceptingPayment> listAccept = acceptPaymentsFuture.get();
            List<YANDEX_TransferPayment> listTrans = transferPaymentsFuture.get();

            Map<String, Double> result = new ConcurrentHashMap<>();

            listAccept.parallelStream()
                    .collect(Collectors.groupingBy(YANDEX_AcceptingPayment::getSku,
                            Collectors.summingDouble(YANDEX_AcceptingPayment::getServiceCost)))
                    .forEach((sku, cost) -> result.merge(sku, cost, Double::sum));

            listTrans.parallelStream()
                    .collect(Collectors.groupingBy(YANDEX_TransferPayment::getSku,
                            Collectors.summingDouble(YANDEX_TransferPayment::getServiceCost)))
                    .forEach((sku, cost) -> result.merge(sku, cost, Double::sum));

            return result;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while processing payment data", e);
        }
    }

    public static Map<String, Double> getMapLoyaltyProgram(Sheet sheet) {
        List<YANDEX_LoyaltyProgram> list = getLoyaltyProgram(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_LoyaltyProgram::getSku,
                        Collectors.summingDouble(YANDEX_LoyaltyProgram::getServiceCost)));
    }

    public static Map<String, Double> getMapBoostSales(Sheet sheet) {
        List<YANDEX_BoostSales> list = getBoostSales(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_BoostSales::getSku,
                        Collectors.summingDouble(YANDEX_BoostSales::getPostpayment)));
    }

    public static Double getSumShelves(Sheet sheet) {
        List<YANDEX_Shelves> list = getShelves(sheet);

        return list.stream()
                .mapToDouble(YANDEX_Shelves::getServiceCost)
                .sum();
    }

    public static Map<String, Double> calculateServiceCostRatio(Sheet deliverySheet, Sheet returnSheet, Sheet serviceSheet) {
        CompletableFuture<Double> totalServiceCostFuture = CompletableFuture.supplyAsync(() -> getSumShelves(serviceSheet));
        CompletableFuture<Map<String, Integer>> deliveryReturnDifferenceFuture = CompletableFuture.supplyAsync(() -> getDeliveryReturnDifference(deliverySheet, returnSheet));

        try {
            Double totalServiceCost = totalServiceCostFuture.get();
            Map<String, Integer> deliveryReturnDifference = deliveryReturnDifferenceFuture.get();

            Integer totalDeliveryReturnDifference = deliveryReturnDifference.values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();

            Map<String, Double> resultMap = new ConcurrentHashMap<>();

            if (totalDeliveryReturnDifference == 0) {
                deliveryReturnDifference.forEach((sku, difference) -> resultMap.put(sku, 0.0));
            } else {
                deliveryReturnDifference.forEach((sku, difference) -> {
                    Double ratio = (totalServiceCost / totalDeliveryReturnDifference) * difference;
                    resultMap.put(sku, ratio);
                });
            }

            return resultMap;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while processing data", e);
        }
    }

    public static Map<String, Integer> getDeliveryReturnDifference(Sheet deliverySheet, Sheet returnSheet) {
        CompletableFuture<Map<String, Integer>> deliveryCountFuture = CompletableFuture.supplyAsync(() -> getMapDeliveryCount(deliverySheet));
        CompletableFuture<Map<String, Integer>> returnCountFuture = CompletableFuture.supplyAsync(() -> getMapReturnCount(returnSheet));

        try {
            Map<String, Integer> deliveryCount = deliveryCountFuture.get();
            Map<String, Integer> returnCount = returnCountFuture.get();

            Map<String, Integer> differenceMap = new ConcurrentHashMap<>();

            deliveryCount.forEach((sku, delivery) -> {
                Integer returns = returnCount.getOrDefault(sku, 0);
                differenceMap.put(sku, delivery - returns);
            });

            returnCount.forEach((sku, returns) -> {
                if (!differenceMap.containsKey(sku)) {
                    differenceMap.put(sku, -returns);
                }
            });

            return differenceMap;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while processing data", e);
        }
    }
}
