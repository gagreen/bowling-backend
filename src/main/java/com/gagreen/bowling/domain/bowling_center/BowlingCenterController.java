package com.gagreen.bowling.domain.bowling_center;

import com.gagreen.bowling.domain.bowling_center.dto.BowlingCenterSearchDto;
import com.gagreen.bowling.exception.BadRequestException;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-api/centers")
public class BowlingCenterController {

    private final BowlingCenterService service;

    @GetMapping
    public Page<BowlingCenterVo> searchList(BowlingCenterSearchDto dto) {

        return service.searchList(dto);
    }

    @GetMapping("/{centerId}")
    public BowlingCenterVo detail(@PathVariable String centerId) {
        if (!StringUtils.isNumeric(centerId)) {
            throw new BadRequestException("옳지 않은 접근 방식입니다.");
        }

        return service.getItem(Long.valueOf(centerId));
    }
}
