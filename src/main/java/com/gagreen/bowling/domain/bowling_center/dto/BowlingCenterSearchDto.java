package com.gagreen.bowling.domain.bowling_center.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@AllArgsConstructor
public class BowlingCenterSearchDto {
    private Integer page=1;
    private Integer size=10;
    private String sort;

    private String keyword;
    private String type;

    private String name;
    private String state;
    private String city;
    private String district;

    public Pageable toPageable() {
        int page = (this.page != null && this.page > 0) ? this.page - 1 : 0;
        int size = (this.size != null && this.size > 0) ? this.size : 10;
        String sort = (this.sort != null) ? this.sort : "createdAt,desc";

        String[] sortInfo = sort.split(",");
        String sortField = sortInfo[0];
        Sort.Direction direction = (sortInfo.length > 1 && sortInfo[1].equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }

    public String getType() {
        if (type == null) {
            return null;
        }

        switch (type) {
            case "name", "state", "city", "district":
                break;
            default:
                type = null;
        }

        return type;
    }
}
