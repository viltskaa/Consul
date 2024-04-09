package com.example.consul.controllers.rest;

import com.example.consul.dto.WB_AdReport;
import com.example.consul.dto.WB_DetailReport;
import com.example.consul.services.WB_Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "/report")
public class WB_AdReportController {
    private final WB_Service wbService;

    public WB_AdReportController(WB_Service wbService) {
        this.wbService = wbService;
    }

    @GetMapping(path = "/ad")
    public List<WB_AdReport> getAdReport(
            @RequestParam String dateTo,
            @RequestParam String dateFrom,
            @RequestParam String apiKey
    ) {
        wbService.setApiKey(apiKey);
        return wbService.getAdReport(dateFrom, dateTo);
    }

    @GetMapping(path = "/detail")
    public List<WB_DetailReport> getDetailReport(
            @RequestParam String dateTo,
            @RequestParam String dateFrom,
            @RequestParam String apiKey
    ) {
        wbService.setApiKey(apiKey);
        return wbService.getDetailReport(dateFrom, dateTo);
    }
}
