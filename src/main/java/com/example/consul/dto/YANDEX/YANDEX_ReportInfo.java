package com.example.consul.dto.YANDEX;

import com.example.consul.api.utils.YANDEX.YANDEX_ApiResponseStatusType;
import com.example.consul.api.utils.YANDEX.YANDEX_ReportStatusType;
import com.example.consul.api.utils.YANDEX.YANDEX_ReportSubStatusType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class YANDEX_ReportInfo {
    private YANDEX_ApiResponseStatusType status;
    private Result result;

    public YANDEX_ReportInfo(String status, Result result){
        this.status = YANDEX_ApiResponseStatusType.valueOf(status);
        this.result = result;
    }


    @Setter
    @Getter
    @Data
    public static class Result{
        private YANDEX_ReportStatusType status;
        private YANDEX_ReportSubStatusType subStatus;
        private String generationRequestedAt;
        private String generationFinishedAt;
        private String file;
        private Long estimatedGenerationTime;

        public Result(String status,
                      String subStatus,
                      String generationRequestedAt,
                      String generationFinishedAt,
                      Long estimatedGenerationTime)
        {
            this.status = YANDEX_ReportStatusType.valueOf(status);
            this.subStatus = YANDEX_ReportSubStatusType.valueOf(subStatus);
            this.generationRequestedAt = generationRequestedAt;
            this.generationFinishedAt = generationFinishedAt;
            this.estimatedGenerationTime = estimatedGenerationTime;
        }
    }
}
