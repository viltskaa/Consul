package com.example.consul.controllers.rest;

import com.example.consul.dto.OZON.OZON_TransactionReport;
import com.example.consul.services.OZON_Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "/ozonReport")
public class OZON_ReportController {
    private final OZON_Service ozonService;

    public OZON_ReportController(OZON_Service ozonService) {
        this.ozonService = ozonService;
    }

    @GetMapping(path = "/transaction")
    public OZON_TransactionReport getTransactionReport(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(value = "") ArrayList<String> operation_type,
            @RequestParam(value = "all") String transaction_type,
            @RequestParam String apiKey,
            @RequestParam String clientId
    ) {
        ozonService.setHeader(apiKey,clientId);
        return ozonService.getTransactionReport(from, to,operation_type,transaction_type);
    }
}
