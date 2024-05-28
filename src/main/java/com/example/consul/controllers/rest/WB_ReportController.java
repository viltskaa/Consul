package com.example.consul.controllers.rest;

import com.example.consul.controllers.rest.requestBodies.WB_RequestBody;
import com.example.consul.document.models.WB_TableRow;
import com.example.consul.services.WB_Service;
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
    public List<WB_TableRow> getReport(@RequestBody WB_RequestBody body){
        return wbService.getData(
                body.getApiKey(),
                body.getYear(),
                body.getMonth()
        );
    }

    @PostMapping(path = "/getExcel")
    public @ResponseBody byte[] getReportExcel(@RequestBody WB_RequestBody body){
        return wbService.createReport(
                body.getApiKey(),
                body.getYear(),
                body.getMonth()
        );
    }
}
