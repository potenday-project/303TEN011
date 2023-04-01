package com.beside.ten011.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class MyRitualResponse {
    private Long totalBookCount; // 읽은 책 권수
    private Long totalArchiveCount; // 기록한 문장
    private Long continuityLoginDay;// 연속해서 만난날(연속출석일)
}
