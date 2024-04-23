package com.example.consul.dto.OZON;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Getter
@Setter
@Data
public class OZON_PerformanceTokenExpires {
    private String access_token;
    private Long expires_in;

    private OZON_PerformanceTokenExpires(@NotNull String access_token,
                                         @NotNull Long expires_in) {
        this.access_token = access_token;
        this.expires_in = expires_in;
    }

    public static OZON_PerformanceTokenExpires of(
            OZON_PerformanceTokenResult ozonPerformanceTokenResult
    ) {
        return new OZON_PerformanceTokenExpires(
                ozonPerformanceTokenResult.getAccess_token(),
                Instant.now().getEpochSecond() + ozonPerformanceTokenResult.getExpires_in());
    }

    public boolean isExpired() {
        return Instant.now().getEpochSecond() > expires_in;
    }
}
