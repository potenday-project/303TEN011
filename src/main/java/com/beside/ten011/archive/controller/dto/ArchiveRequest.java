package com.beside.ten011.archive.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ArchiveRequest {
    private String title;
    private String author;
    private String content;
    private String imageSize;
    private String backgroundColor;
    private String fontStyle;
    private String fontColor;
    private String thumbnail;
}
