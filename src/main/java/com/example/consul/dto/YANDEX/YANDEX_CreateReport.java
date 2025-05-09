package com.example.consul.dto.YANDEX;

import com.example.consul.api.utils.YANDEX.YANDEX_ApiResponseStatusType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class YANDEX_CreateReport {
    private YANDEX_ApiResponseStatusType status;
    private Result result;

    public YANDEX_CreateReport(String status, Result result){
        this.status = YANDEX_ApiResponseStatusType.valueOf(status);
        this.result = result;
    }

    public String getReportId(){
        return result.getReportId();
    }

    public Long getCreationTime(){
        return result.getEstimatedGenerationTime();
    }


    @Setter
    @Getter
    @Data
    public static class Result{
        private String reportId;
        private Long estimatedGenerationTime;

        public Result(String reportId, Long estimatedGenerationTime){
            this.reportId = reportId;
            this.estimatedGenerationTime = estimatedGenerationTime;
        }
    }
}
