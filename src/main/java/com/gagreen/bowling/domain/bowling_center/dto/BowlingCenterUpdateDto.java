package com.gagreen.bowling.domain.bowling_center.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "볼링장 수정 요청")
public class BowlingCenterUpdateDto {

    @Schema(description = "볼링장 이름", example = "스타볼링장", maxLength = 64)
    @Size(max = 64, message = "볼링장 이름은 64자 이하여야 합니다.")
    private String name;

    @Schema(description = "시/도", example = "서울특별시", maxLength = 32)
    @Size(max = 32, message = "시/도는 32자 이하여야 합니다.")
    private String state;

    @Schema(description = "시/군/구", example = "강남구", maxLength = 32)
    @Size(max = 32, message = "시/군/구는 32자 이하여야 합니다.")
    private String city;

    @Schema(description = "읍/면/동", example = "역삼동", maxLength = 32)
    @Size(max = 32, message = "읍/면/동은 32자 이하여야 합니다.")
    private String district;

    @Schema(description = "상세 주소", example = "123-45", maxLength = 32)
    @Size(max = 32, message = "상세 주소는 32자 이하여야 합니다.")
    private String detailAddress;

    @Schema(description = "전화번호", example = "0212345678", maxLength = 11)
    @Size(max = 11, message = "전화번호는 11자 이하여야 합니다.")
    private String telNumber;
}
