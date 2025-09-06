package com.ballon.domain.event.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventRequest {

    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(max = 100, message = "제목은 최대 100자까지 가능합니다.")
    private String title;

    @NotBlank(message = "설명은 필수 입력값입니다.")
    private String description;

    @NotNull(message = "시작일은 필수 입력값입니다.")
    @FutureOrPresent(message = "시작일은 현재 이후여야 합니다.")
    private LocalDateTime startDate;

    @NotNull(message = "종료일은 필수 입력값입니다.")
    @Future(message = "종료일은 미래여야 합니다.")
    private LocalDateTime endDate;
}
