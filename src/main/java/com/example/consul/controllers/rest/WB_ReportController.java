package com.example.consul.controllers.rest;

import com.example.consul.controllers.rest.requestBodies.WB_RequestBody;
import com.example.consul.controllers.rest.requestBodies.WB_RequestBodyDay;
import com.example.consul.controllers.rest.requestBodies.WB_RequestBodyWeek;
import com.example.consul.document.models.ReportFile;
import com.example.consul.document.models.WB_SaleRow;
import com.example.consul.document.models.WB_TableRow;
import com.example.consul.services.WB_Service;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "/wbReport")
public class WB_ReportController {
    private final WB_Service wbService;

    public WB_ReportController(WB_Service wbService) {
        this.wbService = wbService;
    }

    @PostMapping(path = "/get")
    public List<WB_TableRow> getReport(@RequestBody WB_RequestBody body) {
        return wbService.getData(
                body.getApiKey(),
                body.getYear(),
                body.getMonth()
        );
    }

    @PostMapping(path = "/getExcel")
    public @ResponseBody ResponseEntity<Resource> getReportExcel(@RequestBody WB_RequestBody body) {
        ReportFile report = wbService.createReport(
                body.getApiKey(),
                body.getYear(),
                body.getMonth()
        );
        return report.toOkResource();
    }

    @PostMapping(path = "/getByWeek")
    public List<WB_TableRow> getWeekReport(@RequestBody WB_RequestBodyWeek body) {
        return wbService.getData(
                body.getApiKey(),
                body.getYear(),
                body.getMonth(),
                body.getWeekNumber()
        );
    }

    @PostMapping(path = "/getExcelByWeek")
    public @ResponseBody ResponseEntity<Resource> getWeekReportExcel(@RequestBody WB_RequestBodyWeek body) {
        ReportFile report = wbService.createReport(
                body.getApiKey(),
                body.getYear(),
                body.getMonth(),
                body.getWeekNumber()
        );
        return report.toOkResource();
    }

    @PostMapping(path = "/getSalesByDay")
    public List<WB_SaleRow> getSaleReport(@RequestBody WB_RequestBodyDay body) {
        return wbService.getData(
                body.getApiKey(),
                body.getDay()
        );
    }

    @PostMapping(path = "/getExcelSalesByDay")
    public @ResponseBody ResponseEntity<Resource> getSaleReportExcel(@RequestBody WB_RequestBodyDay body) {
        ReportFile report = wbService.createReport(
                body.getApiKey(),
                body.getDay()
        );
        return report.toOkResource();
    }
}
