package com.example.consul.mapping;

import com.example.consul.document.models.YANDEX_TableRow;
import com.example.consul.mapping.sheets.*;
import joinery.DataFrame;
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
import java.util.stream.Collectors;

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

    // Получить заголовки для датафрейма
    private static List<String> getTitleForDataFrame(Sheet sheet) {
        List<String> listHeader = new ArrayList<>();
        int numSuffix = 1;

        for (int i = 0; i < sheet.getRow(findAutofilterRow(sheet)).getPhysicalNumberOfCells(); i++) {
            String cellValue = sheet.getRow(findAutofilterRow(sheet)).getCell(i).getStringCellValue();

            if (listHeader.contains(cellValue)) {
                String newCellValue = cellValue + numSuffix;
                numSuffix++;
                listHeader.add(newCellValue);
            } else {
                listHeader.add(cellValue);
            }
        }

        return listHeader;
    }

    private static DataFrame<Object> setDataToDataFrame(Sheet sheet, DataFrame<Object> df) {
        List<Object> listData = new ArrayList<>();

        for (int j = findAutofilterRow(sheet) + 1; j < sheet.getLastRowNum() + 1; j++) {
            for (int i = 0; i < sheet.getRow(j).getPhysicalNumberOfCells(); i++) {
                switch (sheet.getRow(j).getCell(i).getCellType()) {
                    case BLANK, NUMERIC -> listData.add(sheet.getRow(j).getCell(i).getNumericCellValue());
                    case BOOLEAN -> listData.add(sheet.getRow(j).getCell(i).getBooleanCellValue());
                    case STRING -> listData.add(sheet.getRow(j).getCell(i).getStringCellValue());
                }
            }
            df.append(listData);
            listData = new ArrayList<>();
        }

        return df;
    }

    private static List<YANDEX_DeliveredGoods> getDeliveredGoods(Sheet sheet) {
        List<YANDEX_DeliveredGoods> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);
        boolean hasWarehouseSku = sheet.getRow(headerRow).getLastCellNum() == 21; // Проверяем, есть ли столбец warehouseSku

        if (hasWarehouseSku) {
            for (int j = headerRow + 1; j <= sheet.getLastRowNum() - 1; j++) {
                Row row = sheet.getRow(j);
                YANDEX_DeliveredGoods data = YANDEX_DeliveredGoods.builder()
                        .orderNumber((long) row.getCell(0).getNumericCellValue())
                        .orderType(row.getCell(1).getStringCellValue())
                        .productName(row.getCell(2).getStringCellValue())
                        .productSku(row.getCell(3).getStringCellValue())
                        .warehouseSku(row.getCell(4).getStringCellValue())
                        .quantityShipped((int) row.getCell(5).getNumericCellValue())
                        .quantityDelivered((int) row.getCell(6).getNumericCellValue())
                        .orderStatus(row.getCell(7).getStringCellValue())
                        .orderDate(LocalDate.parse(row.getCell(8).getStringCellValue(), formatter))
                        .shipmentDate(LocalDate.parse(row.getCell(9).getStringCellValue(), formatter))
                        .deliveryDate(LocalDate.parse(row.getCell(10).getStringCellValue(), formatter))
                        .paymentMethod(row.getCell(11).getStringCellValue())
                        .vatRate(row.getCell(12).getStringCellValue())
                        .priceWithoutDiscount(row.getCell(13).getNumericCellValue())
                        .marketplaceDiscount(row.getCell(14).getNumericCellValue())
                        .sberThankYouBonusDiscount(row.getCell(15).getNumericCellValue())
                        .yandexPlusPointsDiscount(row.getCell(16).getNumericCellValue())
                        .priceWithDiscount(row.getCell(17).getNumericCellValue())
                        .totalPriceWithoutDiscount(row.getCell(18).getNumericCellValue())
                        .totalDiscount(row.getCell(19).getNumericCellValue())
                        .totalPriceWithDiscount(row.getCell(20).getNumericCellValue())
                        .build();

                list.add(data);
            }
        } else {
            for (int j = headerRow + 1; j <= sheet.getLastRowNum() - 1; j++) {
                Row row = sheet.getRow(j);
                YANDEX_DeliveredGoods.YANDEX_DeliveredGoodsBuilder dataBuilder = YANDEX_DeliveredGoods.builder()
                        .orderNumber((long) row.getCell(0).getNumericCellValue())
                        .orderType(row.getCell(1).getStringCellValue())
                        .productName(row.getCell(2).getStringCellValue())
                        .productSku(row.getCell(3).getStringCellValue())
                        .quantityShipped((int) row.getCell(4).getNumericCellValue())
                        .quantityDelivered((int) row.getCell(5).getNumericCellValue())
                        .orderStatus(row.getCell(6).getStringCellValue())
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
                        .totalPriceWithDiscount(row.getCell(19).getNumericCellValue());

                YANDEX_DeliveredGoods data = dataBuilder.build();
                list.add(data);
            }
        }

        return list;
    }

    private static List<YANDEX_GoodsInDelivery> getGoodsInDelivery(Sheet sheet) {
        List<YANDEX_GoodsInDelivery> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);
        boolean hasWarehouseSku = sheet.getRow(headerRow).getLastCellNum() == 20; // Проверяем, есть ли столбец warehouseSku

        if (hasWarehouseSku) {
            for (int j = headerRow + 1; j <= sheet.getLastRowNum() - 1; j++) {
                Row row = sheet.getRow(j);
                YANDEX_GoodsInDelivery data = YANDEX_GoodsInDelivery.builder()
                        .orderNumber((long) row.getCell(0).getNumericCellValue())
                        .orderType(row.getCell(1).getStringCellValue())
                        .productName(row.getCell(2).getStringCellValue())
                        .productSku(row.getCell(3).getStringCellValue())
                        .warehouseSku(row.getCell(4).getStringCellValue())
                        .quantityShipped((int) row.getCell(5).getNumericCellValue())
                        .orderStatus(row.getCell(6).getStringCellValue())
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

                list.add(data);
            }
        } else {
            for (int j = headerRow + 1; j <= sheet.getLastRowNum() - 1; j++) {
                Row row = sheet.getRow(j);
                YANDEX_GoodsInDelivery.YANDEX_GoodsInDeliveryBuilder dataBuilder = YANDEX_GoodsInDelivery.builder()
                        .orderNumber((long) row.getCell(0).getNumericCellValue())
                        .orderType(row.getCell(1).getStringCellValue())
                        .productName(row.getCell(2).getStringCellValue())
                        .productSku(row.getCell(3).getStringCellValue())
                        .quantityShipped((int) row.getCell(4).getNumericCellValue())
                        .orderStatus(row.getCell(5).getStringCellValue())
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
                        .totalPriceWithDiscount(row.getCell(18).getNumericCellValue());

                YANDEX_GoodsInDelivery data = dataBuilder.build();
                list.add(data);
            }
        }

        return list;
    }

    private static List<YANDEX_ReturnedGoods> getReturnedGoods(Sheet sheet) {
        List<YANDEX_ReturnedGoods> list = new ArrayList<>();
        int headerRow = findAutofilterRow(sheet);
        boolean hasWarehouseSku = sheet.getRow(headerRow).getLastCellNum() == 22;

        if (hasWarehouseSku) {
            for (int j = headerRow + 1; j <= sheet.getLastRowNum() - 1; j++) {
                Row row = sheet.getRow(j);
                YANDEX_ReturnedGoods data = YANDEX_ReturnedGoods.builder()
                        .orderNumber((long) row.getCell(0).getNumericCellValue())
                        .orderType(row.getCell(1).getStringCellValue())
                        .productName(row.getCell(2).getStringCellValue())
                        .productSku(row.getCell(3).getStringCellValue())
                        .warehouseSku(row.getCell(4).getStringCellValue())
                        .quantityDelivered((int) row.getCell(5).getNumericCellValue())
                        .quantityReturned((int) row.getCell(6).getNumericCellValue())
                        .orderStatus(row.getCell(7).getStringCellValue())
                        .orderDate(LocalDate.parse(row.getCell(8).getStringCellValue(), formatter))
                        .shipmentDate(LocalDate.parse(row.getCell(9).getStringCellValue(), formatter))
                        .deliveryDate(LocalDate.parse(row.getCell(10).getStringCellValue(), formatter))
                        .returnReceiptDate(LocalDate.parse(row.getCell(11).getStringCellValue(), formatter))
                        .paymentMethod(row.getCell(12).getStringCellValue())
                        .vatRate(row.getCell(13).getStringCellValue())
                        .priceWithoutDiscount(row.getCell(14).getNumericCellValue())
                        .marketplaceDiscount(row.getCell(15).getNumericCellValue())
                        .sberThankYouBonusDiscount(row.getCell(16).getNumericCellValue())
                        .yandexPlusPointsDiscount(row.getCell(17).getNumericCellValue())
                        .priceWithDiscount(row.getCell(18).getNumericCellValue())
                        .totalReturnedPriceWithoutDiscount(row.getCell(19).getNumericCellValue())
                        .totalDiscountForReturnedItems(row.getCell(20).getNumericCellValue())
                        .totalReturnedPriceWithDiscount(row.getCell(21).getNumericCellValue())
                        .build();

                list.add(data);
            }
        } else {
            for (int j = headerRow + 1; j <= sheet.getLastRowNum() - 1; j++) {
                Row row = sheet.getRow(j);
                YANDEX_ReturnedGoods data = YANDEX_ReturnedGoods.builder()
                        .orderNumber((long) row.getCell(0).getNumericCellValue())
                        .orderType(row.getCell(1).getStringCellValue())
                        .productName(row.getCell(2).getStringCellValue())
                        .productSku(row.getCell(3).getStringCellValue())
                        .quantityDelivered((int) row.getCell(4).getNumericCellValue())
                        .quantityReturned((int) row.getCell(5).getNumericCellValue())
                        .orderStatus(row.getCell(6).getStringCellValue())
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

                list.add(data);
            }
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
                    .pricePerUnit(row.getCell(11).getNumericCellValue())
                    .priceDifference(row.getCell(12).getNumericCellValue())
                    .quantity((int) row.getCell(13).getNumericCellValue())
                    .salesQuantum((int) row.getCell(14).getNumericCellValue())
                    .quantumsInOrder((int) row.getCell(15).getNumericCellValue())
                    .pricePerQuantum(row.getCell(16).getNumericCellValue())
                    .weightKg(row.getCell(17).getNumericCellValue())
                    .lengthCm(row.getCell(18).getNumericCellValue())
                    .widthCm(row.getCell(19).getNumericCellValue())
                    .heightCm(row.getCell(20).getNumericCellValue())
                    .sumOfDimensions(row.getCell(21).getNumericCellValue())
                    .paymentMethod(row.getCell(22).getStringCellValue())
                    .qualityIndex(row.getCell(23).getStringCellValue())
                    .service(row.getCell(24).getStringCellValue())
                    .tariffCondition(row.getCell(25).getStringCellValue())
                    .tariffPerUnit(row.getCell(26).getNumericCellValue())
                    .measurementUnit(row.getCell(27).getStringCellValue())
                    .minTariffPerUnit(row.getCell(28).getNumericCellValue())
                    .maxTariffPerUnit(row.getCell(29).getNumericCellValue())
                    .serviceCostBeforeMinTariff(row.getCell(30).getNumericCellValue())
                    .serviceDateTime(LocalDateTime.parse(row.getCell(31).getStringCellValue(), formatterTime))
                    .actFormationDate(LocalDate.parse(row.getCell(32).getStringCellValue(), formatterDate))
                    .serviceCostWithoutDiscounts(row.getCell(33).getNumericCellValue())
                    .tariffPercent(row.getCell(34).getNumericCellValue())
                    .discount(row.getCell(35).getNumericCellValue())
                    .lateDeliveryPenaltyPercent(row.getCell(36).getNumericCellValue())
                    .sellerFaultPenaltyPercent(row.getCell(37).getNumericCellValue())
                    .minCostPerUnit(row.getCell(38).getNumericCellValue())
                    .maxCostPerUnit(row.getCell(39).getNumericCellValue())
                    .serviceCostChange1(row.getCell(40).getNumericCellValue())
                    .tariff(row.getCell(41).getNumericCellValue())
                    .serviceCostChange2(row.getCell(42).getNumericCellValue())
                    .individualServiceDiscount(row.getCell(43).getNumericCellValue())
                    .loyaltyDiscount(row.getCell(44).getNumericCellValue())
                    .serviceCost(row.getCell(45).getNumericCellValue())
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

    public static List<YANDEX_TableRow> getDataFromInputStream(InputStream inputStreamService, InputStream inputStreamRealization) throws IOException {
        Workbook wbService = WorkbookFactory.create(inputStreamService);

        final Sheet[] sheetService = {
                wbService.getSheetAt(1),
                wbService.getSheetAt(3),
                wbService.getSheetAt(4),
                wbService.getSheetAt(8),
                wbService.getSheetAt(11),
                wbService.getSheetAt(18),
                wbService.getSheetAt(12)
        };

        Workbook wbRealization = WorkbookFactory.create(inputStreamRealization);

        final Sheet[] sheetRealization = {
                wbRealization.getSheetAt(2),
                wbRealization.getSheetAt(4),
                wbRealization.getSheetAt(1)
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

        CompletableFuture<Map<String, Double>> favorSortingCenterPaymentCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapSortingCenter(
                        sheetService[3],
                        sheetRealization[2],
                        sheetService[5],
                        sheetService[4]
                ));

        CompletableFuture<Map<String, Double>> deliveredCostCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapDeliveryCost(sheetRealization[0]));

        CompletableFuture<Map<String, Integer>> deliveredCountCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapDeliveryCount(sheetRealization[0]));

        CompletableFuture<Map<String, Double>> marketplaceDiscountCompletableFuture = CompletableFuture
                .supplyAsync(() -> getMapMarketplaceDiscount(sheetRealization[0]));

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
        Map<String, Double> marketplaceDiscount = marketplaceDiscountCompletableFuture.join();

        Map<String, List<Object>> mergedMap = new HashMap<>();

        for (String key : acceptAndTransferPayment.keySet()) {
            mergedMap.put(key, Arrays.asList(
                    deliveredCount.getOrDefault(key, 0).doubleValue(),
                    deliveredCost.getOrDefault(key, 0.0),
                    returnCount.getOrDefault(key, 0).doubleValue(),
                    returnCost.getOrDefault(key, 0.0),
                    placingOnShowcase.getOrDefault(key, 0.0),
                    deliveryToConsumer.getOrDefault(key, 0.0),
                    acceptAndTransferPayment.getOrDefault(key, 0.0),
                    favorSortingCenterPayment.getOrDefault(key, 0.0),
                    0.0, // Placeholder for "Хранение невыкуп. заказов/возвратов"
                    0.0, // Placeholder for "Расходы на рекламные кампании"
                    loyaltyProgram.getOrDefault(key, 0.0),
                    boostSales.getOrDefault(key, 0.0),
                    marketplaceDiscount.getOrDefault(key, 0.0)
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
                    (Double) values.get(12)  // promotionFavor
            );
        }).toList();
    }

    public static Map<String, Double> getMapSortingCenter(Sheet sheetDelivery, Sheet sheetShip, Sheet sheetSortingCenter, Sheet sheetAcceptPay) {
        List<YANDEX_AcceptingPayment> listAccept = getAcceptingPayment(sheetAcceptPay);
        List<YANDEX_DeliveryCustomer> listDel = getDeliveryCustomer(sheetDelivery);
        List<YANDEX_ProcessingOrders> listSorting = getProcessingOrders(sheetSortingCenter);
        List<YANDEX_GoodsInDelivery> listDelivery = getGoodsInDelivery(sheetShip);

        Map<Long, List<String>> orderNumberToSkuListMap = listDel.stream()
                .collect(Collectors.groupingBy(YANDEX_DeliveryCustomer::getOrderNumber,
                        Collectors.mapping(YANDEX_DeliveryCustomer::getSku, Collectors.toList())));

        Map<Long, List<String>> orderNumberToSkuListMapAccept = listAccept.stream()
                .collect(Collectors.groupingBy(YANDEX_AcceptingPayment::getOrderNumber,
                        Collectors.mapping(YANDEX_AcceptingPayment::getSku, Collectors.toList())));

        Map<Long, List<String>> orderNumberToSkuListMapDelivery = listDelivery.stream()
                .collect(Collectors.groupingBy(YANDEX_GoodsInDelivery::getOrderNumber,
                        Collectors.mapping(YANDEX_GoodsInDelivery::getProductSku, Collectors.toList())));

        orderNumberToSkuListMapAccept.forEach((orderNumber, skuList) ->
                orderNumberToSkuListMap.merge(orderNumber, skuList, (existingList, newList) -> {
                    existingList.addAll(newList);
                    return existingList;
                })
        );

        orderNumberToSkuListMapDelivery.forEach((orderNumber, skuList) ->
                orderNumberToSkuListMap.merge(orderNumber, skuList, (existingList, newList) -> {
                    existingList.addAll(newList);
                    return existingList;
                })
        );

        System.out.println(orderNumberToSkuListMap);

        Map<Long, Double> orderNumberToTotalTariffMap = listSorting.stream()
                .collect(Collectors.groupingBy(YANDEX_ProcessingOrders::getOrderNumber,
                        Collectors.summingDouble(YANDEX_ProcessingOrders::getTariff)));

        System.out.println(orderNumberToTotalTariffMap);

        Map<String, Double> skuToFinalTariffMap = new HashMap<>();

        orderNumberToSkuListMap.forEach((orderNumber, skuList) -> {
            Double totalTariff = orderNumberToTotalTariffMap.getOrDefault(orderNumber, 0.0);
            if (totalTariff > 0 && !skuList.isEmpty()) {
                double dividedTariff = totalTariff / skuList.size();
                skuList.forEach(sku -> skuToFinalTariffMap.merge(sku, dividedTariff, Double::sum));
            }
        });

        skuToFinalTariffMap.replaceAll((sku, value) -> (double) Math.round(value));

        return skuToFinalTariffMap;
    }

    public static Map<String, Double> getMapDeliveryCost(Sheet sheet) {
        List<YANDEX_DeliveredGoods> list = getDeliveredGoods(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_DeliveredGoods::getProductSku,
                        Collectors.summingDouble(YANDEX_DeliveredGoods::getTotalPriceWithDiscount)));
    }

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
                        Collectors.summingDouble(YANDEX_ShowPlacement::getServiceCostWithoutDiscounts)));
    }

    public static Map<String, Double> getMapDeliveryToConsumer(Sheet sheet) {
        List<YANDEX_DeliveryCustomer> list = getDeliveryCustomer(sheet);

        return list.stream()
                .collect(Collectors.groupingBy(YANDEX_DeliveryCustomer::getSku,
                        Collectors.summingDouble(YANDEX_DeliveryCustomer::getServiceCost)));
    }

    public static Map<String, Double> getMapAcceptAndTransferPayment(Sheet sheetAccept, Sheet sheetTrans) {
        List<YANDEX_AcceptingPayment> listAccept = getAcceptingPayment(sheetAccept);
        List<YANDEX_TransferPayment> listTrans = getTransferPayment(sheetTrans);

        Map<String, Double> result = listAccept.stream()
                .collect(Collectors.groupingBy(YANDEX_AcceptingPayment::getSku,
                        Collectors.summingDouble(YANDEX_AcceptingPayment::getServiceCost)));

        listTrans.stream()
                .collect(Collectors.groupingBy(
                        YANDEX_TransferPayment::getSku,
                        Collectors.summingDouble(YANDEX_TransferPayment::getServiceCost))
                ).forEach((key, value) -> result.merge(key, value, Double::sum));

        return result;
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
}
