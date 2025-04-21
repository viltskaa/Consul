package com.example.consul.document.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class ReportFile {
    private String filename;
    private ByteArrayResource content;

    public ReportFile(@NotNull String filename, @NotNull ByteArrayResource content) {
        this.filename = filename;
        this.content = content;
    }

    public ReportFile(@NotNull String filename, @NotNull byte[] content) {
        this.filename = filename;
        this.content = new ByteArrayResource(content);
    }

    public ResponseEntity<Resource> toOkResource() {
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"%s\"".formatted(this.getFilename())
                )
                .body(this.getContent());
    }
}
