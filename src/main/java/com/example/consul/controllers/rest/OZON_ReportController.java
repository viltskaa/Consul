package com.example.consul.controllers.rest;

import com.example.consul.controllers.rest.requestBodies.OZON_RequestBody;
import com.example.consul.document.models.OZON_TableRow;
import com.example.consul.dto.OZON.OZON_TransactionReport;
import com.example.consul.services.OZON_Service;
import org.jetbrains.annotations.NotNull;
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

    @PostMapping(path = "/get")
    public List<OZON_TableRow> getReport(@RequestBody OZON_RequestBody body) {
        return ozonService.getData(
                body.getApiKey(),
                body.getClientId(),
                body.getPerformanceClientId(),
                body.getPerformanceClientSecret(),
                body.getYear(),
                body.getMonth()
        );
    }

    @PostMapping(path = "/getExcel")
    public @ResponseBody byte[] getReportExcel(@RequestBody OZON_RequestBody body) {
        return ozonService.createReport(
                body.getApiKey(),
                body.getClientId(),
                body.getPerformanceClientId(),
                body.getPerformanceClientSecret(),
                body.getYear(),
                body.getMonth()
        );
    }
}
