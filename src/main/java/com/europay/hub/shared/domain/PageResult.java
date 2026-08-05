package com.europay.hub.shared.domain;

import java.util.List;
import java.util.function.Function;

/**
 * Framework-neutral pagination result used by domain repository ports, so the domain never
 * depends on Spring Data's {@code Page}. Adapters convert to/from it at the boundary.
 */
public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public <R> PageResult<R> map(Function<T, R> mapper) {
        return new PageResult<>(
                content.stream().map(mapper).toList(), page, size, totalElements, totalPages);
    }
}
