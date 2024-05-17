package com.example.consul.controllers.rest.requestBodies;

import com.example.consul.api.utils.YANDEX.YANDEX_PlacementType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class YANDEX_RequestBody {
    private String auth;
    private Long campaignId;
    private int year;
    private int month;
    private Long businessId;
    private List<YANDEX_PlacementType> placementPrograms;
}
