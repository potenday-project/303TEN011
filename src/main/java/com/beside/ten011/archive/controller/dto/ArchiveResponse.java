package com.beside.ten011.archive.controller.dto;

import com.beside.ten011.archive.entity.Archive;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class ArchiveResponse {

    private Long id;
    private String title;
    private String author;
    private String content;
    private String imageSize;
    private String backgroundColor;
    private String fontStyle;
    private String fontColor;
    private String thumbnail;
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime createdDt;
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime modifiedDt;

    public static ArchiveResponse fromEntity(Archive archive) {
        return ArchiveResponse.builder()
                .id(archive.getId())
                .title(archive.getTitle())
                .author(archive.getAuthor())
                .content(archive.getContent())
                .imageSize(archive.getImageSize())
                .backgroundColor(archive.getBackgroundColor())
                .fontStyle(archive.getFontStyle())
                .fontColor(archive.getFontColor())
                .thumbnail(archive.getThumbnail())
                .createdDt(archive.getCreatedDt())
                .modifiedDt(archive.getModifiedDt())
                .build();
    }
}
