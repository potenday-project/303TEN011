package com.beside.ten011.user.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

@NoArgsConstructor
@Getter
public class MyRitualResponse {
    private long totalBookCount; // 읽은 책 권수
    private long totalArchiveCount; // 기록한 문장
    private long continuityPostDay;// 연속해서 만난날(연속작성일)

    @Builder
    public MyRitualResponse(Long totalBookCount,
                            Long totalArchiveCount,
                            Long continuityPostDay){
        this.totalArchiveCount = ObjectUtils.defaultIfNull(totalArchiveCount, 0L);
        this.totalBookCount = ObjectUtils.defaultIfNull(totalBookCount, 0L);
        this.continuityPostDay = ObjectUtils.defaultIfNull(continuityPostDay, 0L);
    }
}
