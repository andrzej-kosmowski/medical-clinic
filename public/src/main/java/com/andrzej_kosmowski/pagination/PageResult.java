package com.andrzej_kosmowski.pagination;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public <R> PageResult<R> map(Function<T, R> mapperFunction) {
        List<R> mappedContent = this.content.stream()
                .map(mapperFunction)
                .toList();

        return new PageResult<>(
                mappedContent,
                this.page,
                this.size,
                this.totalElements,
                this.totalPages
        );
    }
}
