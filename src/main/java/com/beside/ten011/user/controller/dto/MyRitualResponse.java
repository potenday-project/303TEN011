package com.beside.ten011.user.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MyRitualResponse {
    private Long totalBookCount; // 읽은 책 권수
    private Long totalArchiveCount; // 기록한 문장
    private Long continuityDate;// 연속해서 만난날 = 연속해서 글쓴날(오늘 - 계산 필요)
}
