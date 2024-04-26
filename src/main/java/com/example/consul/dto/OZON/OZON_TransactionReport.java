package com.example.consul.dto.OZON;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Getter
@Data
public class OZON_TransactionReport {
    private Result result;

    public OZON_TransactionReport(Result result) {
        this.result = result;
    }

    @Setter
    @Getter
    @Data
    public static class Result{
        private List<Operation> operations;

        public Result(List<Operation> operations) {
            this.operations = operations;
        }
    }

    @Data
    public static class Posting{
        private String delivery_schema;
        private String order_date;
        private String posting_number;
        private Long warehouse_id;

        public Posting(String delivery_schema, String order_date, String posting_number, Long warehouse_id) {
            this.delivery_schema = delivery_schema;
            this.order_date = order_date;
            this.posting_number=posting_number;
            this.warehouse_id=warehouse_id;
        }
    }

    @Data
    public static class Item{
        private String name;
        private Long sku;

        public Item(String name, Long sku) {
            this.name = name;
            this.sku=sku;
        }
    }

    @Data
    public static class Service{
        private String name;
        private Double price;

        public Service(String name, Double price) {
            this.name = name;
            this.price=price;
        }
    }

    @Data
    public static class Operation{
        private Long operation_id;
        private String operation_type;
        private String operation_date;
        private String operation_type_name;
        private Double delivery_charge;
        private Double return_delivery_charge;
        private Double accruals_for_sale;
        private Double sale_commission;
        private Double amount;
        private String type;
        private Posting posting;
        private List<Item> items;
        private List<Service> services;

        public String getPostingNumber() {
            return getPosting().getPosting_number();
        }

        public Boolean hasSku(Long sku) {
            return getItems().stream().anyMatch(x -> x.getSku().equals(sku));
        }

        public static OZON_TransactionReport.Operation of(OZON_TransactionReport.Operation operation) {
            return new OZON_TransactionReport.Operation(operation.getOperation_id(),
                    operation.getOperation_type(),
                    operation.getOperation_date(),
                    operation.getOperation_type_name(),
                    operation.getDelivery_charge(),
                    operation.getReturn_delivery_charge(),
                    operation.getAccruals_for_sale(),
                    operation.getSale_commission(),
                    operation.getAmount(),
                    operation.getType(),
                    operation.getPosting(),
                    operation.getItems(),
                    operation.getServices());
        }

        public Operation(Long operation_id,
                         String operation_type,
                         String operation_date,
                         String operation_type_name,
                         Double delivery_charge,
                         Double return_delivery_charge,
                         Double accruals_for_sale,
                         Double sale_commission,
                         Double amount,
                         String type,
                         Posting posting,
                         List<Item> items,
                         List<Service> services) {
            this.operation_id = operation_id;
            this.operation_type = operation_type;
            this.operation_date = operation_date;
            this.operation_type_name = operation_type_name;
            this.delivery_charge = delivery_charge;
            this.return_delivery_charge = return_delivery_charge;
            this.accruals_for_sale = accruals_for_sale;
            this.sale_commission = sale_commission;
            this.amount = amount;
            this.type = type;
            this.posting = posting;
            this.items = items;
            this.services = services;
        }

        public Long getSku(){
            return items.get(0).getSku();
        }

        public double getPrice(){
            return services.get(0).getPrice();
        }

        public List<String> getAllServicesName(){
            return services.stream().flatMap(service -> Stream.of(service.getName())).collect(Collectors.toList());
        }

        public Double getPriceByServiceName(String serviceName){
            if (!checkServiceName(serviceName))
                return null;
            return services.stream().filter(service -> service.getName().equals(serviceName))
                    .findFirst().get().getPrice();
        }

        public boolean checkServiceName(String serviceName){
            return getAllServicesName().contains(serviceName);
        }
    }
}
