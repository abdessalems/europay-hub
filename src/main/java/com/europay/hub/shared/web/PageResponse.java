package com.europay.hub.shared.web;

import com.europay.hub.shared.domain.PageResult;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;

/**
 * Serialization-friendly pagination wrapper. Avoids leaking Spring Data's {@code Page}
 * shape (which is unstable across versions) to API clients.
 *
 * @param <T> the DTO element type
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    /** Map an entity/domain page to a DTO page in one step. */
    public static <S, T> PageResponse<T> of(Page<S> page, Function<S, T> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    /** Build from a framework-neutral {@link PageResult}. */
    public static <T> PageResponse<T> of(PageResult<T> page) {
        return new PageResponse<>(
                page.content(), page.page(), page.size(), page.totalElements(), page.totalPages());
    }
}
