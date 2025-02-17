package com.fixadate.global.factory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageFactory {

    public static Pageable getPageableSortBy(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = (isAsc ? Sort.Direction.ASC : Sort.Direction.DESC);
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }

}
